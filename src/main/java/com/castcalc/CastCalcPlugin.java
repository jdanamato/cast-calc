package com.castcalc;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@PluginDescriptor(
    name = "Cast Calc",
    description = "Calculate the GP cost of every spell cast with live GE prices, rune-saving gear detection, and profit/loss for utility spells.",
    tags = {"magic", "runes", "cost", "calculator", "alchemy", "high alch", "tan leather", "profit", "ge"}
)
public class CastCalcPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private CastCalcConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private SpriteManager spriteManager;

    private CostCalculator calculator;
    private CastCalcPanel panel;
    private NavigationButton navButton;
    private ScheduledFuture<?> priceRefreshTask;

    @Override
    protected void startUp()
    {
        calculator = new CostCalculator(client, itemManager);
        panel = new CastCalcPanel(client, config, calculator, itemManager, clientThread, spriteManager);

        BufferedImage icon;
        try
        {
            icon = ImageUtil.loadImageResource(getClass(), "cast_calc_icon.png");
        }
        catch (Exception e)
        {
            log.debug("Icon not found, using blank placeholder");
            icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        }

        navButton = NavigationButton.builder()
            .tooltip("Cast Calc")
            .icon(icon)
            .priority(8)
            .panel(panel)
            .build();

        clientToolbar.addNavigation(navButton);

        // Warm up the price cache on the client thread, THEN initialize the panel.
        // clientThread.invokeLater will queue until the client thread is alive.
        clientThread.invokeLater(() ->
        {
            try
            {
                calculator.refreshPriceCache();
                RuneSavingGear.updateCache(client);
                // Push current Magic level if already logged in when plugin starts
                if (client.getGameState() == GameState.LOGGED_IN)
                {
                    int magicLevel = client.getRealSkillLevel(Skill.MAGIC);
                    SwingUtilities.invokeLater(() -> panel.setCurrentMagicLevel(magicLevel));
                }
            }
            catch (Exception e)
            {
                log.warn("Failed initial cache warmup", e);
            }
            SwingUtilities.invokeLater(() ->
            {
                panel.initialize();
                panel.updatePrices();
            });
        });

        // Periodic price refresh — runs on client thread, then updates UI
        priceRefreshTask = executor.scheduleAtFixedRate(
            () -> clientThread.invokeLater(() ->
            {
                try
                {
                    calculator.refreshPriceCache();
                    RuneSavingGear.updateCache(client);
                }
                catch (Exception e)
                {
                    log.debug("Periodic price refresh failed", e);
                }
                SwingUtilities.invokeLater(() -> panel.updatePrices());
            }),
            5, 5, TimeUnit.MINUTES
        );

        log.info("Cast Calc started");
    }

    @Override
    protected void shutDown()
    {
        clientToolbar.removeNavigation(navButton);

        if (priceRefreshTask != null)
        {
            priceRefreshTask.cancel(true);
            priceRefreshTask = null;
        }

        log.info("Cast Calc stopped");
    }

    @Provides
    CastCalcConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CastCalcConfig.class);
    }

    @Subscribe
    public void onStatChanged(StatChanged event)
    {
        if (event.getSkill() == Skill.MAGIC)
        {
            int level = client.getRealSkillLevel(Skill.MAGIC);
            SwingUtilities.invokeLater(() -> panel.setCurrentMagicLevel(level));
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (event.getContainerId() == net.runelite.api.InventoryID.EQUIPMENT.getId())
        {
            RuneSavingGear.updateCache(client);
            SwingUtilities.invokeLater(() -> panel.updatePrices());
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGGED_IN)
        {
            calculator.refreshPriceCache();
            RuneSavingGear.updateCache(client);
            int level = client.getRealSkillLevel(Skill.MAGIC);
            SwingUtilities.invokeLater(() ->
            {
                panel.setCurrentMagicLevel(level);
                panel.updatePrices();
            });
        }
    }
}
