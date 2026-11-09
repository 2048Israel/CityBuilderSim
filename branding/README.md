# Civic Ledger - the name and the look

The game is **Civic Ledger** since BR1 (2026-10-27, Jerus's pick). The
repository, the exe (`CityBuilderSim.exe`) and the save folder
(`%APPDATA%\CityBuilderSim`) keep the old name: renaming the folder would
leave every player's cities behind, and renaming the exe would break a
player's shortcuts, the itch.io app's launch and a Steam launch option at the
first update. `GameVersion.NAME` is the one place the game states its name.

## What is where

| piece | file | made by |
|---|---|---|
| the icon, "towers as a chart" | `src/main/resources/icons/icon.svg` (48 px and up), `icon-small.svg` (40 px and under), `icon-16.png` ... `icon-256.png`, `icon.ico` | `tools/branding/make_icons.py` |
| the title art, "the ledger's total" | `branding/title.svg` (the font inside it), `title-600.png`, `title-1200.png`, `title-2400.png` | `tools/branding/make_title.py` |
| Steam's store art (BR2): the header, small, main and vertical capsules, the library's capsule, header, hero and transparent logo, the page background | `branding/steam/<name>-<width>x<height>.png`, nine files at Steamworks' sizes | `tools/branding/make_steam.py`, from `title.svg` and the icon's towers |
| the itch.io cover, 630 x 500 | `branding/cover-630x500.png` | by hand from the San Francisco run's coast shot, the icon and `title.svg` |

On screen, `ui/Branding` gives the window every icon size and draws the title
art on the start screen itself (from the same proportions as `make_title.py`;
`BrandingCheck` holds the two equal). `Build EXE.bat` passes `icon.ico` to
jpackage, and `--description "Civic Ledger"` names the exe in Task Manager.

The colours are the title screen's (`ui/Palette`: STAGE, MONEY, PEOPLE,
BUSINESS, BUILDING), the font IBM Plex Sans Bold (`src/main/resources/fonts/`,
under the OFL, which allows embedding it).

## Remaking it

    python tools/branding/make_icons.py
    python tools/branding/make_title.py
    python tools/branding/make_steam.py          # after make_title.py: it draws with title.svg

All three need only Python and Edge or Chrome (`--browser` names another); no image
library. Change a size list in `ui/Branding` and `make_icons.py` together, and
the title art's proportions in `ui/Branding` and `make_title.py` together.

## Releases and stores

- **GitHub releases** are titled `Civic Ledger (<version>)`, as 0.8.60 was;
  the tags stay the version alone.
- **itch.io** (https://2048israel.itch.io/civic-ledger): the title and the
  address `civic-ledger` were already right. BR1 put up the new cover and eight
  screenshots and ticked Graphics in the AI generation disclosure (with Code
  and Text & Dialog). The download there is 0.8.60, without the icon: the
  first build pushed after BR1 carries it.
- **Steam** (app 5571850, verification and upload Jerus's): its art is in
  `branding/steam/` since BR2, drawn by `make_steam.py` from our art (Jerus,
  2026-10-31: "From our art (AI)"): skyline towers under a rising chart line,
  the name in the sky, the hero without text, the logo transparent. The
  sizes are Steamworks' (`partner.steamgames.com/doc/store/assets/standard`
  and `.../libraryassets`, read 2026-11-09); `BrandingCheck` holds the folder
  to them. Which file goes in which Steamworks slot is its name.

The icon, the title art, the cover and Steam's art were drawn by an AI (Claude),
which the itch.io page's AI disclosure says (Steam's own disclosure, when the
page is filled in, is Jerus's to tick).
