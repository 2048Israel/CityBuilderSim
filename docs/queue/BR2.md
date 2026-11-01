# BR2 - Steam's store art, drawn from our art

Queued 2026-10-31 on Jerus's call, "From our art (AI)": the capsules at Steam's
sizes and a transparent logo, made from `branding/`'s title art and icon
rather than by a human artist (BR1 notes §5). His Steamworks verification,
and putting the art on the store, stay his.

## What it does

- **The sizes Steamworks asks for**, read from its graphical-assets page at
  the time (as of 2024: header capsule 920 x 430, small 462 x 174, main
  1232 x 706, vertical 748 x 896; library capsule 600 x 900, library header
  920 x 430, library hero 3840 x 1240, library logo 1280 x 720 transparent
  PNG; page background 1438 x 810), with its rules on what a capsule holds
  (the game's name legible, no review or award text).
- **Made by a script** beside `tools/branding/make_title.py` (Python and Edge
  or Chrome, no image library), from `branding/title.svg`, the icon and the
  palette, so the art is remade when the title art is; the city behind the
  title, if any, from a played run's shot as the itch.io cover was.
- **Into `branding/steam/`**, recorded in `branding/README.md`'s table.

Checked by eye at each size (the name legible at the small capsule). No game
code; the trace does not move.
