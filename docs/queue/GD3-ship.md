## changelog
### {next} — GD3, the gold standard — 2026-11-10, ready, see docs/queue/GD3-notes.md
Gold is the fifth regime on Policy › The currency (spec-gold-money 4.3; Jerus, 2026-11-02, every call as recommended).
Entering it strikes the mint at the day's rate times gold's price, so the D$ keeps its value (D$20.6718 an ounce at
1.00 in 1900), or at a par typed. The rate is held at the gold points, 0.5% either side of the mint price over gold's:
gold is sold first at the weak point and bought at the strong. The law's cover is 40% of M0: a sale that would leave
less takes the D$ off gold (the break's new cause), and the peg's doubt is scaled by how far the cover is short of
full. Defend the gold, the autopilot's new aim under Gold, ties the dial to the US's rate and lifts it as the cover
falls, ten points at the floor. In April 1933, February 1934 and August 1971 a city on gold is asked to stay at its
par, follow the dollar or leave gold for Lean (a notice; the buttons on Policy). Slots 152-155 of foreignAccounts, no
format bump; GoldStandardCheck. GD2's two findings mended (gold's price never throws; eightOf never finds gold).
Lean is the default and the trace is main's: no ensemble.
## pending
| GD3-F | The founding screen's "start on gold" box beside the money (spec 4.3), the founders' vault into gold ({next}) | after FM1, which rewrites the founding screen's money line; ★ how much of the vault: enough for the 40% cover (Recommended) or all of it | [GD3 notes](docs/queue/GD3-notes.md) |
| GD3-R | GD3's review (0.9.x): (1) main's KeptCheck red "the kept header's signature equals a header built new" (18 differ: the RATE tile's hidden label "D$1,000,000,000.00 per US$" at MAX_RATE keeps its old size when kept, none when built new) is no longer reached once Gold adds a currency pass, and has no row: fix it, so it does not drop out of the record; (2) ExchangeRateCheck 14 loops over every regime on a fixture with no gold price, so setRegime(GOLD) is refused and its "under GOLD" lines test Lean: give the fixture a gold price or leave Gold out; (3) ForeignAccounts.monthsToFloor reads moneyBase, not saved and 0 after a load, so Policy's AT THIS PACE counts all the gold free until the next month while FREE GOLD uses m0(); (4) a gold break is flagged "The peg broke" and Gold has no thin-cover flag like the peg's (PolicyScreen ~1543-1547); (5) no harness loads a GD2-length (152-slot) array and checks GD3's defaults; (6) main's own: selling gold does not shrink M0 (CentralBank.dollarsSold only records it), so the cover drains faster than under a real gold standard | a small fix batch | docs/queue/GD3-notes.md |
