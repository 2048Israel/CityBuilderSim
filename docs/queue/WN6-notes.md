# WN6 - Nations: the screens (0.9.0, 2026-11-01)

The nations line's screens (`docs/studies/spec-nations.md` 8 boards 2 and 3, 9 WN6's row; D29 and
`spec-exchange-rates.md` 3.9): Trade › Partners, a nation's own page, the Overview's partners strip, the map card's
door to it, The reserves in eight moneys, and the real 2015 US$. Nothing in the month reads any of it.

## 1. What was built

- **The model the screens read** (no screen recomputes a figure; CLAUDE.md, "A screen section"):
  - `WorldMarket.Curve` (+280 lines): each good's two sides at the city's site, an immutable snapshot rebuilt with
    the fills (a solved year, a new site or berth, a load) and published through a volatile. `band()` and
    `allocate()` now call the same `Fill.model/buyers/sellers` code, so the curve's floor, ceiling and buyers are
    theirs to the bit. Prices in the band's unit, volumes game units a month; the joins, the level and who sets it,
    the next buyers, a 48-point grid on the share of the world's use.
  - `PartnersView` (315): the partners table (month or year so far; rank, money and its mark, D$ a unit, the policy
    rate where it is one of the eight, tonnes, distance, land factor and land bought), a nation's page (the map
    card's own rows and ranks, now `NationsMap.figureRows` and `YearFigures`, shared), a good's board.
  - `VaultView` (64): the eight moneys' rows; the month's move read from the history's `fx<code>` series, so a
    loaded city shows it.
  - `PartnerTrade.Snapshot` carries the month's units by good (already saved); `Game.realGdp2015UsdYear()`.
- **The screens** (`ui/PartnersPage` 1,054, `ui/VaultTable` 214, TradeScreen +94):
  - **Trade › Partners**, between What we trade and The currency: WHO WE TRADE WITH (Month / This year, 12 rows then
    the rest); WHERE THE NEXT UNIT GOES for a chosen good (the curve on a log share of the world's use, the month's
    line and the world's dashed, where the city sells, where the next buyers join, named); AT WHAT WE SELL NOW (the
    buyers taking, each netting the level; "Next to buy: ..."); THE BAND THIS MONTH (floor and ceiling named by the
    nation that sets each); WHAT MOVES IT (our own selling, the world's answer by January, the freight to the first
    buyer). A good the city only buys shows who sells it.
  - **A nation's page** (a row's click, the strip, the map card's new "Its trade with us"): its figures and ranks,
    our trade with it (the month, the year by good, its finished years as mirrored bars), its money (rate, D$ a
    unit, policy rate, the vault's holding of it, what the city owes in it), its land (factor, published price,
    bought), THE CITY BESIDE IT (the city's GDP in real 2015 US$ against its output), doors to Rankings.
  - **The Overview**: OUR PARTNERS THIS MONTH, the top five as doors; SOLD/BOUGHT ABROAD say "to 23 nations".
  - **The reserves**: IN THE VAULT, IN EIGHT MONEYS first (held, worth, share, this month against the US$, policy
    rate, owed where any), the month's cross revaluation line, and a fold with the seven against the US$ and the
    eight's policy rates over the years. Its table replaces the mix card's "held: ..." line.
  - **D29**: The currency's rate card says what a real 2015 US$ costs and the real effective rate (100 at founding),
    each with an (i) that says it is for comparison only; the header's GDP tooltip adds the city's GDP in real 2015
    US$ a year, where the city has the world market.
- **WN5-R settled here** (Pending, ★ recommended): the real effective rate carries a partner across a change of its
  money (`RealDollar.moneyChain`): c(Y) = rate(Y-1)/rate(Y) x P_US(Y-1)/P_US(Y) x P_n(Y)/P_n(Y-1), the partner's
  real rate held through the year it changed; a nation that never changed reads as before, to the bit
  (RealDollarCheck 7, on El Salvador's SVC→USD and Cyprus's CYP→EUR). **Its Pending row can go at the merge.**

## 2. The calls (★; each takes the recommended way)

1. ★ **WN5-R taken in WN6** rather than a batch of its own: WN6 is what shows the rate. Recommended: keep.
2. ★ **A nation's full page is Partners › the nation**, not a tab: the table, the strip and the map card open it;
   "All its measures" stays the Rankings tab. Recommended: keep.
3. ★ **The city's real GDP beside a nation is the last month's at a year's pace** (x12), the model's getter being
   the month's; a twelve-month sum would need a series in 2015 US$. Recommended: keep.
4. ★ **The curves are a snapshot of the fills, nothing saved**: rebuilt on a load from the restored market
   (PartnersCheck 2: the loaded city's curves are the original's to the bit). Recommended: keep.
5. ★ **No World Bank region tag on the nation's page**: the map card's comes from the NationsMap the map builds
   from the borders, which the Trade tab does not hold. Recommended: leave it to the map card.
6. ★ **The band card's volumes are the month last tallied** (what next month's band is struck from); "we sell / buy
   it at" are the market's own prices, so in a month the volumes moved they can differ from the floor and
   ceiling. Recommended: keep.

## 3. The trace

The default playtest (4,000 months, both data files named) traced on this branch (dc99d125) and on main at
96208585: all eight trace files byte-identical, the logs identical; both exit 0, every month audited and every
reload matched. **No ensemble** (batch.md 6).

## 4. Checks

- `PartnersCheck` (new, 8 sections): the curve is the band and the allocation to the bit (780 volumes); a loaded
  city's curves; the table foots, both spans; a nation's page foots and its ranks are the card's; the vault's
  worths add to `getReservesUsd()`; a reader thread sees only whole curves while the market solves 3 years
  (1.58M reads); a Halifax city for distances and land; a good's board is the curve's at the tally's volumes.
- `RealDollarCheck` 7 (A CHANGE OF MONEY). WorldMarketCheck, KeptCheck, MapCheck green.
- The screens were laid out headless on `ui/KeptBench` and looked at (Overview, Partners, a good, Canada's page,
  The reserves with four moneys, The currency); the throwaway is not in the tree.
- README's harness count 168 and CLAUDE.md's "forty-two" interface files are written here (GT1 is not built): a
  batch merged first that adds a harness or a `ui/` file collides on them (StaleCheck).

## 5. For the next batches

- The map's near view already draws the borders that cross the city's ground (WN2 call 3), WN5's ask for the land
  office; nothing more was added.
- A chart of the real effective rate over time needs a saved series (`HistorySave`); none is kept.
- The "owes" column and the nation page's debt line were not seen with debt in a foreign money (CD8) in the
  renders; PartnersCheck reads the getter.

## 6. One in, one out

Not a process batch: no script, check machinery or rule a session reads changed (CLAUDE.md's interface count is a
stated count StaleCheck holds, not a rule).

## 7. The manual (for the orchestrator to add to section 16, after "The world")

### The nations on the Trade tab

*Since 0.9.0: who the city trades with, where the next unit goes, and the vault in eight moneys.*

**Trade › Partners** lists the nations the city traded with, this month or so far this year: what it sold each
and bought of each, the money each pays in and what one is worth in D$, its central bank's rate where its money is
one of the eight the vault keeps, the tonnes each way, how far away it is, and - on the real Earth - its land
factor and the land the city bought from it. Click a nation for its own page: its output, people and output a
head with their ranks among the 196, our trade with it by good and by year, its money, its land, and the city's
own output beside its, in real 2015 US$. The map's nation card opens the same page ("Its trade with us"), and the
Overview shows the month's five largest partners.

Below the table, pick a good to see **where the next unit goes**. The world does not buy at one price: each nation
that takes the city's goods nets something different once the freight is paid, and the city sells first where it
nets most. The chart draws what the next unit would net against how much the city sells, as a share of the
world's use a year: the solid line is this month, the dashed one after the world's prices answer in January. The
marks show where each new buyer starts to take any. Every nation buying at the city's volume nets the same: the
city sells to each until the next unit would net more somewhere else. The good's band this month names the
nation that sets its floor (the best buyer, less the freight to it) and its ceiling (the cheapest seller, plus the
freight from it), and **what moves it** separates the city's own selling, the world's answer and the freight.

**Trade › The reserves** opens with the vault in eight moneys: what it holds of each, its worth in US$ and D$,
its share, how each moved against the dollar this month, and each central bank's rate; a fold draws the seven
against the dollar and the eight's rates over the years.

**Two US dollars.** The US$ the city trades in is the in-game one, whose prices follow the United States' path.
The other is the real 2015 US$, the unit the nations' output is counted in. The currency card says what a real
2015 US$ costs in D$ and gives the D$'s real effective rate, 100 at founding, its partners' moneys weighted by the
trade and the prices taken out of both ends; the GDP tile's tooltip gives the city's output in real 2015 US$. Both
are there to compare the city with the nations: nothing in the city's economy reads them.
