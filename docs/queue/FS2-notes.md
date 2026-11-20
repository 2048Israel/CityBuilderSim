# FS2 notes: the twins and the scale cause their conditions

Batch FS2, built 2026-11-20 by Claude (Opus, headless) on `batch/FS2` from
origin/main e2f399d7c (0.9.162). Brief: `docs/studies/flaky-suite.md` 6.1 and
its tables (`flaky-suite/by-section.md`, the FS2 rows). Takes HP1 item 3, HP2
items 1 and 3, PK1's ScaleCheck half and HL1-R item 2. Harness-only: the diff
is `ScaleCheck.java`, `PriceLevelCheck.java`, `DenominationCheck.java` and the
generated maps. The model and the trace do not move (no model class is
touched; `moves_playtest=no`).

## The rows

All fifteen FS2 rows of `docs/notes/known-reds.txt` go green and leave the
file. None is a model fault, so nothing is queued.

| row | harness, line | cause | change |
|---|---|---|---|
| 35 | Scale: savings within TRACK_SAVINGS | the K = 1 city's whole units (2.83e-2 on LX1's town, 1.12e-2 on today's) | the city side is TRACK_CITY_TIMES = 10 copies: 9.05e-4 |
| 55, 56 | PriceLevel: `FAIL h.`, money series to their rounding | twins compared past the month rounding parts them; `h.*` cut by index, not by what each list held | the parting's onset (below); history cut by its recorded lengths |
| 64, 65 | PriceLevel: `FAIL reload.`, `in reload.` | the town's reload is month 120's, read where the twins had parted at 96 | the town run again to the month compared, its reload read there |
| 66 | PriceLevel: the town's lines shown alike | cut-offs counted over months past the parting | counted over the months compared |
| 67 | PriceLevel: some partner given a lane | found: 0 city-months on main's path | given: a partner at twice and one at half the floor, every month |
| 104 | PriceLevel: real series within 1e-9 (HS1's path) | as 55 | as 55 |
| 107-110 | Scale: 1900 months 3-6, prices | the 1900 copies trade at the world's band, which the K-times copy moves by its own volume | the 1900 town and its copies on the literals, as section 6's are: 2e-5 |
| 118, 119 | PriceLevel: month 79, the homes (OP1's path) | rounding: a nudge parts at 80, the twin at 79 | the onset; green on OP1's own classes (below) |
| 134 | Denomination: a partner with a lane and an arrow | found on BO1-S's path | the card's partner given twice the floor (`givePartner()`) |

## PriceLevelCheck (HP1 item 3, HP2 item 1)

**The finding (OP1's path).** A nudge of the money's scale is neutral in exact
arithmetic, so a nudged city and the twin at the other level are the same kind
of rounding perturbation. Ten children of both levels on OP1's dry-run classes
(17f8a4c05) part the town's homes between months 79 and 83, same-level pairs as
often as across the levels. One nudge a level gave a third to a half chance
that a chaotic fork parts the twin before the nudge's horizon: rows 118 and 119
were that, not the factor.

**The onset.** Each twin now runs three nudges a level (`NUDGES`: 1 + 1e-12,
1 - 1e-12, 1 + 1e-13), eight cities. The onset is the first month any two part
(`onset()`). A parting with no same-level pair apart is the factor's and fails
("the twins part only where rounding parts two cities of one level"); else the
twins are compared through the month before it. The rule is shown on hand-made
cities (`comparisonMachinery()`): a rounding parting shows same-level pairs,
one by the factor alone shows none.

**The history** (`h.*`) is cut by what each list held at the month (the child
prints `L` lines, `Digest.lengths()`), not by index; a company's list begins
at its listing. **The reload** (`reload.*`), the run's last month's, is dropped
from a short prefix; for the town the children run again to the month compared
(`cbs.priceLevel.months`) and their reload is read there, with the law that the
rerun is the full run there to the bit. **The cut-offs** (section 10) count
over the months compared.

**The lanes** (row 67): each child gives two partners a quarter of the globe
off a month's exports of twice and half the board's floor in real terms, held
to the feed's own floor: one lane at either level in every month after the
founding (the market, and its feed, is built the first time a month asks for
it). The fault line: a floor left at the board's D$300 thousand gives the
factor's partner none. Section 11 (the 1900 twin) is in the header's list now.

Checks: today's classes exit 0, 303 s (the base: exit 1 on row 67, 198 s; the
six nudge children a twin are the 105 s). OP1's dry-run classes exit 0, 316 s:
the town parts at month 79 by 8 same-level pairs, is compared through 78, and
the rerun to 78 is the full run to the bit. Logs (local, no kept dry run):
`target/plc-2.txt`, `target/plc-op1.txt`.

## DenominationCheck (row 134)

`cardAndLanes()` first gives the month's largest partner (none within a degree
of Halifax) twice the plain city's floor in crop exports in both cities
(`givePartner()`, `GIVEN_FLOORS`), the reformed city's in its money, with no
export units so the world's band does not move. "Right after the reform" is
the same month and keeps what the reform divided. The reform's state is taken
after the gift. Checks: exit 0, 34 s (the base: exit 1, 52 s); the line reads 1
lane and 1 arrow in all four cards.

## ScaleCheck (HP2 item 3, PK1's half, HL1-R item 2)

**The strike (section 6).** The copy was struck at a picked month (172, 165,
225, each re-picked when a path moved the town). The town now plays on from
FREE_FIXTURE_MONTHS by its own plan (`playTown()`, split from `ownTown()`)
until a month is growing, has ground free for a home and its own rents price no
migrant out in the six months after. A month that misses the last has played
them, so the search goes on from there; none within FREE_FIXTURE_SEARCH = 60
fails as a fixture. The three Observations are that one fixture now. The
copy's "ground for some building" and section 9's empty sites are hard
fixtures (caused by the strike's ground and by the held months). Today: struck
at 225, the first month tried.

**The city side (section 9).** The misses are the K = 1 city's own whole units
and fall with its copies: the worst savings 1.12e-2, 9.05e-4 and 1.11e-4 at 1,
10 and 100 copies; GDP 7.04e-2, 1.03e-2 and 1.21e-3; deposits, people and
prices the same way. The city side is TRACK_CITY_TIMES = 10 copies. The fault
line: a copy whose treasury the scaler left at its city's size misses its cash
by 0.989 (TRACK_CASH .0125). A copy with the bank's household deposits left so
missed by 9.6e-5 only, since the load rebuilds them, so that one was not used.

**The 1900 town (section 10).** On the world's band its K = 1 and K = 1,000
copies' price levels parted by 1.09e-3 by month 3 on today's classes; on the
literals by 2e-5, the food basket the same at both sizes. The town and its
copies play on the literals now, as section 6's do. Its fixture moves with it
(153 copies, not 123), and every other line of the section holds.

Checks: exit 0, 27 s (the base: exit 0, 44 s). `target/sc-k1.txt`,
`sc-k10.txt` and `sc-k100.txt` (the city side probe), `sc-lit-*.txt` (the
literals probe).

## The calls (★)

- ★ PriceLevelCheck: three nudges a level and the onset by a same-level
  parting, not one nudge's horizon. It costs 105 s a run; the 10-minute child
  guards are kept.
- ★ PriceLevelCheck: the town's reload by a rerun to the month compared,
  asserted to be the full run there to the bit (month 78 on OP1's path).
- ★ DenominationCheck: the eight "the same ..." lines (sections 243, 463, 471
  and 475) are untouched. They assert DK1's real fault (the share desk's room,
  on the queue), so their tolerance is DK1's to set once it is mended. Section
  879's played world-fixture city is kept, as the only place the card reads the
  game's feed, with its lane given now rather than found.
- ★ ScaleCheck: ten copies a side, not a hundred. A figure the scaler left at
  one city's size is a K-th of the city side too, so each copy more blunts the
  law for it; sections 3 and 4 count those exactly, and the fault line shows
  the law still reads FW9's class.
- ★ ScaleCheck: the 1900 town on the literals. The world's depth is the
  world's, and `playOnLiterals()`'s own javadoc names a scale the world's fill
  would break as its case.
- ScaleCheck's "it builds" and "the free copy ordered" stay Observations: they
  are the copies' outcomes, not premises a fixture sets. BO1-S's `lineOff` is
  kept.

## Not replayed

Rows 55, 56 and 64-66 (OX1's path), 104 (HS1's), 107-110 (R1e's dry run) and
35 (LX1's) were red on paths since merged into main, which passes all of them.
The mechanisms are shown on today's classes and, for the parting, the reload
and the cut-offs, on OP1's dry-run classes.

## The pick and the review

`sh tools/pick.sh origin/main HEAD`: 26 of 228. Review: not needed (no money,
saves or threading in the diff). No dry run (the pick is not ALL).
