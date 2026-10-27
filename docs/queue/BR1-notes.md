# BR1 - the name and the look ({next}, 2026-10-27)

A side batch started from the orchestrator's session on release day, with
Jerus deciding each step in the chat. No model, save or month change: the
title, the window's icon, the exe's icon and the start screen's title art.
Written by Opus in one session (the code is about 200 lines; a Sonnet brief
would have been as long as the change).

## 1. The name

The game wore two names: CityBuilderSim in the game and the repository, Civic
Ledger on itch.io and the 0.8.60 release. Eight candidates were checked
(web, Steam and itch.io search, the US trademark register at
tmsearch.uspto.gov):

| name | verdict |
|---|---|
| Civic Ledger | no game, no US mark; a small Australian blockchain firm, Civic Ledger Pty Ltd (water trading), shares the name - another field |
| Bread & Bonds, Gross City Product | clear |
| Ledger City | out: a live US mark for downloadable game software (Dev Null Productions) |
| Invisible Hand | out: The Invisible Hand, a 2021 stock-market game on Steam |
| Commonweal, Circular Flow, City of Coin | small clashes (an itch game and a magazine; a 2017 game and a textbook term; sounds like crypto) |

Jerus chose Civic Ledger. `GameVersion.NAME` says it; `GameFiles.APP_NAME`
(the save folder) and the exe keep CityBuilderSim (branding/README.md says
why).

## 2. The icon and the title art

Four concepts each, drawn as SVG from the title screen's palette and shown
large and at 48, 32, 24 and 16 px. Picked: icon B, "towers as a chart"; title
art C, "the ledger's total". The icon is drawn twice, a bolder one for 40 px
and under, because the full one turns to mush at 16. The title art is drawn by
the start screen in JavaFX (`Branding.title`, following the label's own width)
and for the store by `tools/branding/make_title.py` from the same proportions.

## 3. Checks

`BrandingCheck` (new, in `AllChecks`): every icon PNG on the classpath at its
size with alpha, the .ico listing exactly `Branding.ICO_SIZES` as PNG entries,
`Build EXE.bat`'s `--icon` pointing at it, and `make_title.py`'s proportions
equal to `Branding`'s. By hand: the start screen and the window icon on the
PC (the log says `Icon: 9 of 9 sizes`); jpackage run on a throwaway jar with
the new `--icon` built an exe that carries the icon, FileDescription "Civic
Ledger" (its ProductName stays CityBuilderSim: jpackage ties it to `--name`).

## 4. itch.io (with Jerus's OK at each step)

Graphics ticked in the AI disclosure; the cover redone from his San Francisco
run (coast close-up, icon and title art); the five screenshots replaced by
eight from that run, picked by a subagent from his 30 and checked here (two
red notices cropped off; one shot showing an Edge window with his tabs left
out). Title and address unchanged.

## 5. Left

- The itch.io download is still 0.8.60, without the icon, until the next build
  is pushed there.
- GitHub release titles: `Civic Ledger (<version>)` from now on (no script
  makes them; branding/README.md records it).
- Steam store art (capsules, logo) waits on Jerus: AI-drawn from branding/, or
  a human artist; and on his Steamworks verification.
