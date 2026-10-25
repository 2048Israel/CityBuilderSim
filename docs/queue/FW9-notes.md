# FW9 - ScaleCheck's copy tracks its city ({next}, 2026-10-25)

The brief: `QUEUE.md`'s row, from `docs/notes/todo-archive.md` (FOUND ON THE
WAY IN 0.7.55 TO 0.7.64 and 0.7.53 TO 0.7.54; fixH2 ★9): ScaleCheck's copies
lose about 0.85% of their people a month, because the founding care and
police endowments do not scale, and the scaler leaves the bond market, the
exchange, the equity register and the fund unscaled. List what `scale()`
misses today, scale it (the money and the quantities; the logs, rates and
counts left), and add a section holding a x1000 copy to its city month by
month, red on main and green on the fix.

## 1. What was there

Measured with a scratch probe (not in the tree) that reads section 6's
fixture - the playtest's town at month 812, 5,319 people - through `scale()`
at K = 1 and K = 1,000 and compares the two saves leaf by leaf each month.
On main the copy lost 1.3% of its people in its first month and 6.1% in six,
and its treasury went from 4.5e8 to -9.7e8 in the first. Two causes:

- **The founding endowments are model constants, not save state**: the 100
  household places (`BuildingManager.getTotalHouseCapacity()`), the founding
  doctor, nursery, almshouse, churchyard and constabulary
  (`Healthcare.foundingCapacity()`, `SafetyType.foundingCapacity()`), and the
  grid's 10,000 kW and the wells' 8,000 units (`UtilitiesHandler`). A town of
  five thousand lives on them; its copy had 0.2% of its power and 0.3% of its
  water. No rewrite of the save can reach a constant.
- **About sixty save keys were left at one city's size**: the bond market,
  the exchange's books, the register of shares, the fund, the bank's
  records, the city's debts and paper in flight, the national accounts, the
  CD2-CD5 debt books, the reserve, the ports, the old metal, the care, the
  police, the schools and more (the list is in `registers()`'s banner).

## 2. What was done

- **The model: one saved count, `foundingEndowments`** (`BuildingManager`,
  boxed `Long` in `DataSave`, written by the save, always set by the load):
  1 for every city a player founds, K for a harness's copy K times over. The
  care and safety readers, the house places and `UtilitiesHandler`'s grid and
  wells multiply by it. At 1 every figure is what it was, to the bit: the
  slice compared the default playtest's trace over 700 months against main's,
  byte-identical (8 CSVs). No format bump: a save without it reads as 1.
- **The scaler** (`ScaleCheck.scale()` and the new `registers()`): every key
  named, with its slots or fields and why. What is per household stays per
  household - the cells' shares, paper and bonds are no longer divided by K -
  and the registers are K times over, so they still add up to their holders.
  Prices, fair values, yields, rates, months, ids and the logs stay. Layouts
  read by position stop the scaler with a message if the model adds a slot
  (`expect()`). Two names the pattern misread are fixed each way
  (`borrowingChargesLeft`, `buildingMaterials` scale; `crudeMix`,
  `wholesale`, `duty` stay).
- **ScaleCheck 9**, a copy 1,000 times over tracks its city: people, cash,
  savings, GDP, deposits, the price level, unemployment and the policy rate,
  each against a `TRACK_` dial set from the measured worst, and the shares',
  corporate bonds' and city paper's registers adding up to the households'
  holdings (1e-14 measured, 1e-9 asserted). Red on main's scaler (5 FAIL),
  green on the fix. The javadoc's WHAT IT STILL LOSES is rewritten.

| miss, month 1 / worst of six | main's scaler | the fix | dial |
|---|---|---|---|
| people | 1.3e-2 / 6.1e-2 | 2.7e-4 / 1.0e-2 | .015 |
| cash | 3.15 / 3.15 | 2.7e-3 / 8.4e-3 | .0125 |
| savings | 8.4e-2 / .30 | 7.4e-3 / 1.05e-2 | .016 |
| deposits | 2.0e-2 / .28 | 9e-4 / 1.7e-2 | .026 |
| prices | 1.6e-3 / 4.2e-2 | 7e-5 / 2.5e-3 | .004 |
| GDP | 2.5e-2 / 9.3e-2 | 3.4e-3 / 9.3e-2 | .14 |
| unemployment (points) | 1.3e-3 / 9.5e-3 | 2.3e-3 / 2.2e-2 | .033 |

Section 6's 5 and 10 billion copies now grow 1.21% and 0.98% a month
against the town's 1.38% (printed, as before).

## 3. Existing assertions touched, none loosened

- ScaleCheck 3, `addsUp()` and `sumsAgree()`: the household places counted
  100 per endowment, so "K times the founded city's, past its own 100" became
  "K times, its own hundred included" (the stronger form). Two new asserts:
  the copy holds K endowments, and a save gives them back.
- OrderSearchCheck's twin: its fixture's deep trims came from the broken
  copy (it ordered 101,640 buildings at once, far past any till). A faithful
  copy's orders are built whole or refused, as the city's are. The fixture
  is re-caused: the twin's months hold a 40% policy rate (`TWIN_RATE`), as the
  long run already holds `DEAR_RATE`; 2 trims, both past the counted slices,
  the deepest by 172. Its assertions are unchanged.

## 4. ★ calls, each built as recommended

1. **The model changed, where the brief said only the harness would.** A
   constant cannot be scaled from the save, and the copy cannot track
   without it. Recommended as built: one saved count, neutral at 1.
2. **The grid and the wells joined the endowments** beyond the queue row's
   care and police. They were the copy's largest loss. Recommended.
3. **OrderSearchCheck's twin at a 40% policy rate** (section 3).
   Recommended: the fixture causes its condition; 35% gives one trim, 40%
   two.
4. **`TRACK_GDP` and `TRACK_UNEMPLOYMENT` do not tell the fix from main**:
   the town's GDP is 88% construction and lumpy, and the copy's unemployment
   is worse than main's (below). The other five do. Recommended: keep them as
   bounds.
5. **The road network's `BASE_CAPACITY` (400) is left.** Scaled in a scratch
   test it moved the sixth month's people by 44 of 5.7 million.

## 5. Found, not fixed

- **What a copy cannot share is the city's granularity**. The Industry's
  retirement rule takes a quarter of a holding a month, at least one, and the
  town's three bakeries are one step of it; the copy's 3,000 are not, and it
  sheds 500, 375, 282, 211 Bakeries and 250 and 188 Industrial Bakeries in
  six months. The bank's first branch is the charter and never closes; the
  copy's 1,000 branches are not, its fees cover about 90, and 909 close in
  the first month. Together they cost 1.5% of the posts: the copy's
  unemployment 2.2 points over, its people 1% short.
- `BuildAdvice` (line 586) projects the water with one founding endowment of
  wells (the four-argument `UtilitiesHandler.waterOutput()`); in a copy the
  advice understates the wells. `ui/ServicesScreen` and
  `ui/InfrastructureScreen` print the endowments as constants. Neither moves
  a founded city.
- Section 6's free copies now run a median month of 8 ms (bound 1,500).

## 6. The run

The slice was coded by a Sonnet 5.5 subagent at xhigh (657k tokens, 61
minutes); Opus wrote the probe and the brief and reviewed the diff. Checks:
`./batch-check.sh --pick` exit 3, 30 of 143 harnesses, 29 passed and MapCheck
8 red, main's recorded red. BuildingManager and Game were left to the gate's
suite, with LongPlaytest in their place (passed: every month audited, every
reload matched). The review line names `DataSave.java` (saves). The trace did
not move, so no ensemble.
