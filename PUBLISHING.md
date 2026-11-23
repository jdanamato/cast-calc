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
- [x] Confirm all Plugin Hub checks pass.
- [x] Address reviewer feedback, updating the marker commit when necessary.
- [x] After acceptance, verify Cast Calc appears in the RuneLite Plugin Hub.

## Maintenance

RuneLite builds every hub plugin against the latest RuneLite release. If an
API change breaks the build and it stays broken, the plugin is disabled with
`disabled=unmaintained` in its marker file (this happened on 2026-10-15 when
`ItemManager.getItemPrice()` started returning `long`).

A weekly cloud routine, "Cast Calc maintenance" (Mondays 14:00 UTC), does this
automatically: it builds against the latest RuneLite, makes the smallest fix if
the build breaks, pushes to `master`, and opens or updates the Plugin Hub PR.
It does not publish if `master` has unreleased feature commits since the
published commit. Manage it at
https://claude.ai/code/routines/trig_01G9sZ16fpzhtWcFSqmr2oYh

A weekly GitHub Action, "Check spell data" (Thursdays 14:00 UTC, the day after
the usual OSRS game update), compares `SpellDatabase.java` with the OSRS Wiki
using `scripts/check_spells.py`. It's plain Python calling the wiki's API (no
AI, no secrets). If a spell is missing, removed, or has a different level, XP
or rune cost, it opens an issue titled "Spell data differs from the OSRS Wiki"
(or updates the open one), and closes it once everything matches. Run it by
hand from the repo's Actions tab, or locally with
`python3 scripts/check_spells.py`. To leave a spell out on purpose, add it to
`IGNORED` (wiki-only) or `PLUGIN_ONLY` (plugin-only) in the script, with the
reason.

To ship a fix or update by hand:

1. Fix and build locally (`./gradlew clean build`), then push to `master`.
2. In the `plugin-hub` fork, sync `master` with upstream and branch from it.
3. Set `commit=` in `plugins/cast-calc` to the new commit hash, and remove
   any `disabled=` line.
4. Open a pull request to `runelite/plugin-hub` and wait for review.

- [x] Re-enable after the 2026-10-15 disable:
  https://github.com/runelite/plugin-hub/pull/21034
  (marker commit `8c24065fc7c9b4ce79e24fd747412774ac168e81`)
- [ ] Confirm checks pass and the PR is merged.
- [ ] Verify Cast Calc is back in the RuneLite Plugin Hub.

Official instructions: https://github.com/runelite/plugin-hub
