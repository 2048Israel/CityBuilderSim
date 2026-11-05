## changelog
### {next} — KP3, Screens kept: Build and the construction page — 2026-11-05, ready, see docs/queue/KP3-notes.md
Build and the construction page are drawn over last month's nodes instead of rebuilt (study UP4's KP3): Build's
Overview, every category and market page, the stopped pages (no land, deposit, coast, river, crossing or licence),
the funding and fell-short pages, each building's card and each part of it that comes and goes, a scope keyed by
what it shows; the construction page's Sites, Timeline and Demolish tabs the same, the timeline a picture told its
figures. The order bar is filled once a draw, not once a card. A kept control keeps the player's hand: automatic
building's dial held across a month, the cap chips' yield box and the construction page's find box keep their
focus, text and caret; a tile, door or card's (i) under a still pointer keeps its hover look. A move between Build's
pages is an arrival, as before. KeptCheck 36-46 compare each kept page with one built new through every page, ring,
order, pin and tab; Build's months fell from 15-30 ms to 4-6 ms, the construction page's from 8-17 ms to under 2
(KEPT_BUILD_MS 12, KEPT_CONSTRUCTION_MS 4). No ensemble: the model is untouched.
## pending
| KP3-N | Parts of Build's pages kept but reached by no fixture ({next}): the Overview's open asks (an order whose bond sold short at the cap, waiting for its money), the sea terminal's payback, the port and crossing choosers, the coast refusal and the fell-short pages as the model reaches them; and two pieces still made new on a click, not on the clock (the receipt's popup, `Pieces.openPopover`'s contents). Recommended: a fixture for each when a harness city reaches it | Drawn by the same code as the kept pages around them, so correct now, only not proven kept | [KP3-notes §7](docs/queue/KP3-notes.md) |
