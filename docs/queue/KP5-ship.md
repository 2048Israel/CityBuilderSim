## changelog
### {next} — KP5, Screens kept: Bank, Finances and the fund — 2026-11-05, ready, see docs/queue/KP5-notes.md
The Bank tab, the Finances tab and the city's fund are drawn over last month's nodes instead of rebuilt (study UP4's
KP5): the Bank's Overview and six pages in both views; Finances' hub and every area and page - the position, the book
with a line's page and Close it now, Borrow at home and abroad with the ask and the auction, Money, the bond market and
a bond's book, the foreign default and debt result pages; the fund's Portfolio, Search, Activity, Rules & cash and a
security with its ticket - each page keyed by what is open on it, each row that comes and goes by what it shows. The
curves, the desk's pictures, the buyback ladder, the auction and the column charts are told their figures and drawn
again only where one moved; the boxes keep the player's typing, caret and focus across a month; Bank's pops and gauge
and the fund's count run only at 1x-10x, as on People, History and Sectors. KeptCheck 55-68 compare each kept page with
one built new through every page, view, choice, fold and box; a month on the Bank's Overview fell from 16-19 ms to 5-8,
Borrow's from 17-24 to 8-10, the fund's Search from 106-173 to 58-71 (its long lists: FP1). No ensemble: model untouched.
## pending
| KP5-N | Parts of the money screens kept but reached by no fixture ({next}): the bank's rescue, the preferred offer with a shortfall, a city with no bank or under its minimum, the equity split and the residual; foreign money on Funding (`Bank.setForeignDeposits`), apart from a page built new by one hidden empty label (13 px against 0) in a deep city, not traced; the linked and callable bonds' line pages (the scratch city would not sell them); and `TimeChart` painting on every `setData`/`setSize`, twice a kept draw whether or not its data moved. Recommended: a fixture for each when a harness city reaches it, the Funding label traced then; the chart's paint into KP7 | Drawn by the same code as the kept pages around them, so correct now, only not proven kept | [KP5-notes §7](docs/queue/KP5-notes.md) |
## queued
| FP1 | The fund's long lists a page at a time: Search's first rows with Show more, Activity's last year open and each earlier year a fold (KP5's ★C: 13,000 nodes on Search at month 2,100, 58-192 ms a month kept) | [FP1](docs/queue/FP1.md) |
