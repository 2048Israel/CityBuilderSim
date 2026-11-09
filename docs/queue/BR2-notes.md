# BR2 - Steam's store art, drawn from our art ({next}, 2026-11-09)

The brief: `docs/queue/BR2.md` (Jerus, 2026-10-31: "From our art (AI)"). Branch `batch/BR2` from main f092bc17 (TS1 merged).
A small batch (art and a script, no game code, no money, saves or threading), so one Sonnet 5.5 session did all of it,
no slices; Opus reviews at the gate. Steamworks verification, the upload and the store page stay Jerus's: nothing here
touches Steam.

## What it does

`tools/branding/make_steam.py` (new, 215 lines) writes `branding/steam/<name>-<w>x<h>.png`, nine files, in 17 s with
Edge headless and the standard library (the way `make_icons.py` and `make_title.py` do). Seeded, so a rerun gives the
same bytes (checked: a second run left `git status` clean).

| file | size | holds |
|---|---|---|
| header-capsule, library-header | 920 x 430 | the name, the skyline |
| small-capsule | 462 x 174 | the name nearly filling it, a thin skyline |
| main-capsule | 1232 x 706 | the name, the skyline |
| vertical-capsule | 748 x 896, library-capsule 600 x 900 | the name in the sky, tall towers under the line |
| library-hero | 3840 x 1240 | **no text** (Steam: "cannot include any text"), the skyline across it, the bottom left kept quiet where the logo sits by default |
| library-logo | 1280 x 720 | the name alone (`title.svg`), a transparent PNG |
| page-background | 1438 x 810 | the skyline at 55% dimmed, no text: ambient, as Steam asks |

The art: the icon's "towers as a chart" drawn wide. A skyline of towers, each lit in one of the four palette colours
(`make_icons`' own constants and `smooth()`, imported, so the palette is the icon's), a dim row behind, ledger rules in
the sky, the money-blue chart line with ringed dots running along the roofs (the towers' tops follow it). The name is
`title.svg` on a scrim; nothing else is written anywhere: no tagline, no review or award text, which Steam forbids on
a capsule. All nine are 8-bit RGB (no alpha) but the logo, which is RGBA with 90% of its pixels transparent and a
transparent corner (read back, not assumed).

`pngio.py` gained `png_rgb_bytes` (the RGB writer) and an `opaque` option on `screenshot`/`png_read` (Edge hands back an
RGB PNG when the page paints its own background; the old reader refused it as "no transparency").

`BrandingCheck` section 5: the folder holds exactly the nine files, each a PNG of Steamworks' size (listed in the
harness, apart from the tool's own `ASSETS`), RGB but the logo's RGBA, and `make_steam.py` lists the same nine names and
sizes, so a size changed in the tool alone, or a picture dropped from it, reds.

## Sizes, and where they came from

Read from Steamworks' two pages today (`partner.steamgames.com/doc/store/assets/standard` and `.../libraryassets`):
header 920 x 430, small 462 x 174, main 1232 x 706, vertical 748 x 896, page background 1438 x 810; library capsule
600 x 900, library header 920 x 430, hero 3840 x 1240 (safe area 860 x 380 in the middle), logo "1280 px wide and/or
720 px tall", PNG, transparent. The brief's list is the same list. Rules kept: the title only on a capsule and in
the logo, legible against its background; none on the hero.

## Calls made (each with a recommendation, taken)

- **No city behind the title.** The itch.io cover is a played run's coast shot; a script cannot play a run, and the
  brief says "if any". The brand's own towers and chart line carry the capsules and are remade with the title art.
  A shot-based capsule is a later hand job on top of the same files (the cover's way).
- **The logo is 1280 x 720 as the brief says**, the name at 97% of the width, centred. Steam's rule is "1280 wide
  *and/or* 720 tall", so a canvas trimmed to 1280 wide by about 366 tall is also within it and would pin tighter in
  the preview tool's corners; changing that is one number (`library-logo`'s `logo` share and its size in `ASSETS`).
  Jerus looks at it in the Steamworks preview and can ask for the trim.
- **Library header = header capsule**, the same picture twice (both are 920 x 430 and "title only").

## Checked by eye

Every file looked at at full size. The small capsule was also drawn at Steam's two generated sizes (184 x 69 and
120 x 45): the name reads at both, the skyline is a band under it. The main and vertical capsules and the hero have a
quiet sky above the skyline where Steam's own overlays land.

## Not done (in the ship file's pending)

Steam also asks for **five or more gameplay screenshots** (1920 x 1080 or more, 16:9, no concept art), and has a
**community icon and a client icon**. None is in BR2's brief: the screenshots are a played run's (a script cannot take them; the itch.io eight are from his own
run, whether they suit Steam is his call), the icons are a resize of `icon-256.png`.

The ensemble is skipped: no game code, so the default trace cannot move. One in, one out: not a process batch.
