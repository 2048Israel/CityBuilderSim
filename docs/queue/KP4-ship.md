## changelog
### {next} — KP4, Screens kept: Sectors, the list and a business's five pages — 2026-11-05, ready, see docs/queue/KP4-notes.md
The Sectors tab is drawn over last month's nodes instead of rebuilt (study UP4's KP4): the list and every card, and a
business's frame and five pages in both views - Operations of every kind (the plant, the tonnes, the refinery, the
materials industries, the oil industry and its reserve, Freight by division, the shelf, what it charges), Income and
its waterfall, the balance sheet, cash and credit, the statements, the investors - each page keyed by the business,
the page and the view, each row that comes and goes by what it shows. The refinery's and the materials' pictures and
Freight's legs are told their figures, not rebuilt; the reserve's dials keep the player's hand across a month. The
count-up counts only at 1x-10x, as on People and History: faster, the month's figures are set at once, and a count
still running is stopped. KeptCheck 47-54 compare each kept page with one built new through every sector, page,
view, division, fold and column; the list's month fell from 22-28 ms to 7-9, a business's from 11-31 ms to 3-7
(KEPT_SECTORS_MS 18, KEPT_SECTOR_MS 15). No ensemble: the model is untouched.
## pending
| KP4-N | Parts of the Sectors tab kept but reached by no fixture ({next}): the list's "Nothing recorded yet" and "no word yet" pages, a business with no month before, the Ports division's berth-less row (`caption("")`, the twin of the empty wrapped label `billRow` was mended for), the land office's landmass table through `drawOffice` (a second landmass); and `Levers.dialCard`'s dial-or-none shift (a card that loses its dial takes the dial's nodes, keyed round on the reserve's cards here). Recommended: the dial card's into KP2b beside `Ladder`'s; a fixture for each of the rest when a harness city reaches it | Drawn by the same code as the kept pages around them, so correct now, only not proven kept | [KP4-notes §7](docs/queue/KP4-notes.md) |
