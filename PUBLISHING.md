# Plugin Hub Publishing Checklist

This checklist follows the official RuneLite Plugin Hub submission process.

## Repository preparation

- [x] Make the GitHub repository public.
- [x] Provide `runelite-plugin.properties` with display name, author,
  description, tags, plugin class, and standard build type.
- [x] Keep `runeLiteVersion` set to `latest.release`.
- [x] Target Java 11.
- [x] Use the BSD 2-Clause license.
- [x] Avoid dependencies outside the Plugin Hub standard build template.
- [x] Build and test the plugin locally.
- [x] Add or approve a root `icon.png` no larger than 48 by 72 pixels.
- [x] Commit the final repository state.
- [x] Push the final commit to GitHub.

## Plugin Hub submission

- [x] Fork `runelite/plugin-hub` on GitHub.
- [x] Add a marker file named `plugins/cast-calc` containing:

  ```properties
  repository=https://github.com/jdanamato/cast-calc.git
  commit=cc5e8bce25437d2d973a1494600b5a53e56971e0
  ```

- [x] Open a pull request from the fork to `runelite/plugin-hub`:
  https://github.com/runelite/plugin-hub/pull/13834
- [ ] Confirm all Plugin Hub checks pass.
- [ ] Address reviewer feedback, updating the marker commit when necessary.
- [ ] After acceptance, verify Cast Calc appears in the RuneLite Plugin Hub.

Official instructions: https://github.com/runelite/plugin-hub
