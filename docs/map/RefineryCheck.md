# RefineryCheck.java - 950 lines · 25 methods · 11 constants · harnesses

`ham/citybuildersim/RefineryCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The refinery's units and the flow through them (0.7.80, batch O4;
> runs/spec-oil.md 2.3, and 4's RefineryCheck), and since 0.7.82 the spread
> planner that orders them (batch O5; spec-oil 2.4, SpreadPlanner).
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. WITH NO CONVERSION UNIT THE FLOW IS THE SLATE, to the bit: crude units
>      alone make what Refining.slate() of the same crude and mix makes, on
>      every grade and mix - and so does a refinery whose units all stand
>      at a spread of nothing, which idle.
> 
>   2. THE SPREADS ARE THE SPEC'S, to the bit: each kind's products of a
>      litre at the values, less what the litre is worth with no unit - its
>      leftover's value, or residue's (4 x fuel oil - diesel) / 3.
> 
>   3. THE UNITS BALANCE TO THE LITRE: the crude's litres, plus what each
>      unit gains or loses of its feed's volume, are the products' litres
>      and the residue burned; the coke and the bitumen are each unit's run
>      at its weight a litre; no unit takes more than its feed or than its
>      stream holds; and the flow at nameplate times a rate is the flow at
>      that rate (the reads' rate on top).
> 
>   4. HYDROGEN CAPS THE HYDROCRACKERS at the reformers' make on the
>      straight-run heavy naphtha (Q12): with no reformer, or idle ones
>      (star O4-3), none runs.
> 
>   5. THE RESIDUE IS CUT THREE TO ONE WITH THE DIESEL and the rest burned
>      (Q5): on heavy crude with no unit, residue burns; a coker or an
>      asphalt unit takes residue the furnaces would have burned.
> 
>   6. WITHIN A STREAM THE WIDEST SPREAD TAKES FIRST, and a unit at a spread
>      of nothing or less takes none: the gas oil to the unit that makes
>      the most of it, the heavy residue to the asphalt unit ahead of the
>      coker when its spread is the wider (star O4-2), the coker first when
>      it is not.
> 
>   7. THE REFINERY'S NAMEPLATE IS THE FLOW: in a town with an Oil Refinery
>      and its units standing, each product's capacity is the flow of the
>      refinery's own crude, mix, units and prices, to the bit; the units
>      on site are its pipeline; the audit closes every month, nothing is
>      written off; and a saved city loads to the same flow.
> 
>   8. THE PLAYER MAY ORDER A UNIT: the city's own order for one goes on
>      site (the town's refiners held, so no investor's order is among it).
> 
>   THE SPREAD PLANNER (0.7.82):
> 
>   9. THE CITY'S OWN PRICE: a good is worth its net import price while its
>      market imported any this month, its net export price while it
>      exported any, its local price otherwise (SpreadPlanner.cityValue()).
> 
>  10. A UNIT'S EARNINGS ARE ITS SPREAD, to the bit: at the values, on the
>      feed it would find spare, at most its own, at the rate, less its
>      running and standing costs - each kind finding its own stream (the
>      coker the residue and the heavy residue, a hydrocracker the gas oil
>      the spare hydrogen treats).
> 
>  11. EACH GATE REFUSES ON A FIXTURE THAT CAUSES IT: the feed (no crude unit,
>      no stream; a crude unit a hair under rule 6's FEED_GATE), the ground
>      and the staff (a frame that refuses them; ground the only refusal is
> ... (25 more lines in the source)

**Uses:** [Good](Good.md) (81), [Refining](Refining.md) (70), [RefineryFlow](RefineryFlow.md) (66), [SpreadPlanner](SpreadPlanner.md) (58), [BuildingsTemplate](BuildingsTemplate.md) (27), [Game](Game.md) (15), [BusinessInvestment](BusinessInvestment.md) (15), [BuildingManager](BuildingManager.md) (10), [Deposit](Deposit.md) (9), [Sectors](Sectors.md) (9), [GameFiles](GameFiles.md) (8), [GoodsMarket](GoodsMarket.md) (6), [Sector](Sector.md) (3), [Trade](Trade.md) (3), [Founding](Founding.md) (2), [LandManager](LandManager.md) (2), [MoneyAudit](MoneyAudit.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (1)

## Sections

| line | section |
|---:|---|
| 156 | · 1. NO UNIT: THE SLATE |
| 179 | · 2. THE SPREADS |
| 221 | · 3. THE BALANCE |
| 267 | · 4. HYDROGEN |
| 295 | · 5. THE CUT |
| 330 | · 6. THE ORDER |
| 375 | · 7. THE REFINERY'S NAMEPLATE |
| 378 | · 9-14. THE SPREAD PLANNER |
| 506 | THE SPREAD PLANNER (0.7.82, batch O5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 102 | `RefineryCheck.out` | `System.out` |  |
| 103 | `RefineryCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 392 | `RefineryCheck.UNITS` | `{ "Small Reformer", "Small Cracking Unit", "Small Hydrocracker", "Small Alkyl...` | The units the town stands beside its Oil Refinery: one small one of every kind but the asphalt unit, which medium crude cannot feed. |
| 509 | `RefineryCheck.PROTOTYPE_WAGE` | `4.84` | The prototype's frame (spread.py): wages a post-month, world $k (m4000's Refining, 736k over 200 posts at 760). |
| 512 | `RefineryCheck.PROTOTYPE_RATE` | `.005` | ...and its money test, a month on the whole cost (its RATE). |
| 515 | `RefineryCheck.PROTOTYPE_REFINERY_CAPITAL` | `78_000` | ...and its Oil Refinery's capital: D$60M and its 1,000 materials at 18 (spread.py's TOPPING); every other building its cash. |
| 518 | `RefineryCheck.PROTOTYPE_BUILD_MONTHS` | `6` | ...and the months an order takes to open. |
| 521 | `RefineryCheck.PROTOTYPE_MONTHS` | `240` | ...and the months it runs. |
| 524 | `RefineryCheck.M4000_PETROL` | `7.63e6, M4000_DIESEL = 16.13e6` | The prototype's demands, litres a month of petrol and diesel: the playtest's city at m4000 (spread.py's cities). |
| 527 | `RefineryCheck.FORTY_WELLS` | `40 * 415` | ...and its wells in the 40-well rows: 40 land wells' 415 t a month. |
| 841 | `RefineryCheck.PLANNER_FILES` | `new java.util.IdentityHashMap<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 101 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 99 | 852 | **type** `public class RefineryCheck` | The refinery's units and the flow through them (0.7.80, batch O4; runs/spec-oil.md 2.3, and 4's RefineryCheck), and since 0.7.82 the spread planner that orders them (batch O5; spec-oil 2.4, SpreadPlanner). |
| 105 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 110 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 115 | 5 | `static void quietly(Runnable r)` |  |
| 122 | 3 | `static double[] worldMid()` | Each product's world middle, by Good's ordinal: (import + export) / 2, the prototype's prices. |
| 126 | 1 | `static double v(double[] values, Good g)` |  |
| 129 | 7 | `static double[] campus(BuildingManager b, int n)` | Each kind's feed: its small and its large unit, `n` of each, from the catalogue. |
| 137 | 5 | `static double[] only(Kind k, double litres)` |  |
| 143 | 3 | `static double[] mix(double light, double medium, double heavy)` |  |
| 147 | 243 | `public static void main(String[] args) throws Exception` |  |
| 395 | 110 | `static void town() throws Exception` |  |

### THE SPREAD PLANNER (0.7.82, batch O5) (lines 506-950)

| line | len | member | says |
|---:|---:|---|---|
| 530 | 15 | `static SpreadPlanner.City frame(String noLand, String noStaff)` | The prototype's frame as the planner's City: its capital, its wages, no standing costs, nameplate, and its money test; `land` and `staff` refuse the buildings named. |
| 547 | 7 | `static double[] prototypeValues(RefineryFlow.Flow f, double petrol, double diesel)` | The prototype's city_values: a product at its import price while the city wants more than it makes, its export price while it makes more, the middle otherwise. |
| 556 | 6 | `static double[] blend(double[] a, double wa, double[] b, double wb)` | Two runs' mixes as one, each grade's share of the two. |
| 564 | 10 | `static Refining.Outlook prototypeOutlook(double[] feed, double crude, double petrol, double diesel, double wells, double[] well...` | The outlook of a city in the prototype's frame: its units and crude units standing, the wells' crude at their grade, its values settled in three passes. |
| 582 | 27 | `static java.util.Map<String, Integer> prototypeRun(java.util.List<BuildingsTemplate> ts, double petrol, double diesel, double w...` | The planner run PROTOTYPE_MONTHS months in the prototype's frame: an order opens PROTOTYPE_BUILD_MONTHS on, one at a time; `beforeOpening` decides each month on its state before the month's opening, as the prototype's... |
| 610 | 5 | `static void open(BuildingsTemplate t, double[] feed, double[] crude, java.util.Map<String, Integer> standing)` |  |
| 616 | 5 | `static java.util.Map<String, Integer> row(Object...nameAndCount)` |  |
| 622 | 21 | `static void cityValue()` |  |
| 644 | 39 | `static void earnings(BuildingManager catalogue)` |  |
| 684 | 61 | `static void gates(BuildingManager catalogue)` |  |
| 746 | 34 | `static void theGameMoneyGates() throws Exception` |  |
| 781 | 34 | `static void fourRows(BuildingManager catalogue)` |  |
| 817 | 23 | `static Game plannerTown(String label, boolean held, String...units)` | The town the planner sections stand: the section 7 town's houses and works, an Oil Refinery and `units`; Refining held or not. |
| 843 | 43 | `static void idleThenShed() throws Exception` |  |
| 887 | 63 | `static void theInvestorsOrderAUnit() throws Exception` |  |

