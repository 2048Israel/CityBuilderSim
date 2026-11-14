# RefineryCheck.java - 1,543 lines · 35 methods · 18 constants · harnesses

`ham/citybuildersim/RefineryCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The refinery's units and the flow through them (0.7.80, batch O4;
> docs/studies/spec-oil.md 2.3, and 4's RefineryCheck), and since 0.7.82 the
> spread planner that orders them (batch O5; spec-oil 2.4, SpreadPlanner).
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
>      market imported any this month, its net export price otherwise - its
>      local price until 0.7.105 when it exported none (R1) - and a good the
>      world will not buy its local price (SpreadPlanner.cityValue()).
> 
>  10. A UNIT'S EARNINGS ARE ITS SPREAD, to the bit: at the values, on the
>      feed it would find spare, at most its own, at the rate, less its
>      running and standing costs - each kind finding its own stream (the
>      coker the residue and the heavy residue, a hydrocracker the gas oil
>      the spare hydrogen treats).
> 
>  11. EACH GATE REFUSES ON A FIXTURE THAT CAUSES IT: the feed (no crude unit,
>      no stream; a crude unit a hair under rule 6's FEED_GATE), the ground
> ... (80 more lines in the source)

**Uses:** [Refining](Refining.md) (188), [Good](Good.md) (132), [RefineryFlow](RefineryFlow.md) (86), [SpreadPlanner](SpreadPlanner.md) (79), [BuildingsTemplate](BuildingsTemplate.md) (38), [Game](Game.md) (25), [BusinessInvestment](BusinessInvestment.md) (20), [Sectors](Sectors.md) (16), [BuildingManager](BuildingManager.md) (15), [GoodsMarket](GoodsMarket.md) (14), [Sector](Sector.md) (10), [Deposit](Deposit.md) (9), [GameFiles](GameFiles.md) (9), [Freight](Freight.md) (5), [RefineryView](RefineryView.md) (5), [Founding](Founding.md) (3), [Trade](Trade.md) (3), [Ports](Ports.md) (3), [LandManager](LandManager.md) (2), [MoneyAudit](MoneyAudit.md) (2), [PriceLevel](PriceLevel.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (1), [Resource](Resource.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

## Sections

| line | section |
|---:|---|
| 213 | · 1. NO UNIT: THE SLATE |
| 236 | · 2. THE SPREADS |
| 278 | · 3. THE BALANCE |
| 324 | · 4. HYDROGEN |
| 352 | · 5. THE CUT |
| 387 | · 6. THE ORDER |
| 432 | · 7. THE REFINERY'S NAMEPLATE |
| 435 | · 9-14. THE SPREAD PLANNER |
| 445 | · 16-19. THE RUN, THE PLANNER AT THE MARGIN, ONE ORDER (0.7.105) |
| 570 | THE SPREAD PLANNER (0.7.82, batch O5) |
| 1119 | 15. THE WORLD'S DEMAND FOR LUBRICANTS (0.7.103) |
| 1191 | 16. THE RUN (0.7.105) |
| 1350 | 17. THE RUN IN A TOWN (0.7.105) |
| 1445 | 18. THE PLANNER'S PRICES AT THE MARGIN (0.7.105) |
| 1497 | 19. NO ORDER IN FLIGHT TO WAIT FOR, NO SECOND CRUDE UNIT ON SITE (0.7.105; BA1) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1216 | `RefineryCheck.Way.LORRY` |  |
| 1216 | `RefineryCheck.Way.RAIL` |  |
| 1216 | `RefineryCheck.Way.SEA` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 158 | `RefineryCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 160 | `RefineryCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 456 | `RefineryCheck.UNITS` | `{ "Small Reformer", "Small Cracking Unit", "Small Hydrocracker", "Small Alkyl...` | The units the town stands beside its Oil Refinery: one small one of every kind but the asphalt unit, which medium crude cannot feed. |
| 573 | `RefineryCheck.PROTOTYPE_WAGE` | `4.84 * PriceLevel.FACTOR` | The prototype's frame (spread.py): wages a post-month, world $k (m4000's Refining, 736k over 200 posts at 760). |
| 576 | `RefineryCheck.PROTOTYPE_RATE` | `.005` | ...and its money test, a month on the whole cost (its RATE). |
| 579 | `RefineryCheck.PROTOTYPE_REFINERY_CAPITAL` | `78_000 * PriceLevel.FACTOR` | ...and its Oil Refinery's capital: D$60M and its 1,000 materials at 18 (spread.py's TOPPING); every other building its cash. |
| 582 | `RefineryCheck.PROTOTYPE_BUILD_MONTHS` | `6` | ...and the months an order takes to open. |
| 585 | `RefineryCheck.PROTOTYPE_MONTHS` | `240` | ...and the months it runs. |
| 588 | `RefineryCheck.M4000_PETROL` | `7.63e6, M4000_DIESEL = 16.13e6` | The prototype's demands, litres a month of petrol and diesel: the playtest's city at m4000 (spread.py's cities). |
| 591 | `RefineryCheck.FORTY_WELLS` | `40 * 415` | ...and its wells in the 40-well rows: 40 land wells' 415 t a month. |
| 934 | `RefineryCheck.PLANNER_FILES` | `new java.util.IdentityHashMap<>()` | Each planner town's save folder (plannerTown()), for the sections that save one and load its twin. |
| 1194 | `RefineryCheck.TOWN_WELLS` | `30` | Wells the town of section 14 stands under its Oil Refinery (OilCheck 4's thirty): more crude than it runs, its barrel paying at their price. |
| 1197 | `RefineryCheck.STUDY_LORRY` | `-.100, STUDY_RAIL = -.047, STUDY_FLOOR = -.007, STUDY_TANKER =.023, STUDY_CON...` | The study's margins (the project's why-the-refinery-loses.md, on 0.7.99), a share of crude's world middle: a topping barrel of medium crude on imported crude, all exported, by lorry, by rail at .60 and at the floor, a... |
| 1200 | `RefineryCheck.STUDY_HOME_FLOOR` | `.014, STUDY_HOME_RAIL = -.017` | ...and the barrels the city's own petrol and diesel take, at the net import price: at the floor, and at .60. |
| 1203 | `RefineryCheck.STUDY_QUOTE` | `.60` | The study's railway above its floor: a quote of .60 of a lorry's rate. |
| 1206 | `RefineryCheck.STUDY_ROUNDING` | `.0006` | The study's figures are to a tenth of a point, and its tanker's +2.3% is +2.25% here: within six hundredths. |
| 1209 | `RefineryCheck.HIS_CRUDE` | `5 * 415_000 + 8_300` | Jerus's campus: five Crude Units and an Oil Refinery - the study's 500,000 b/d topping plant. |
| 1212 | `RefineryCheck.NINE` | `{ Good.PETROL, Good.DIESEL, Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL, ...` | The nine products, as Refining lists them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 156 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 154 | 1390 | **type** `public class RefineryCheck` | The refinery's units and the flow through them (0.7.80, batch O4; docs/studies/spec-oil.md 2.3, and 4's RefineryCheck), and since 0.7.82 the spread planner that orders them (batch O5; spec-oil 2.4, SpreadPlanner). |
| 162 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 167 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 172 | 5 | `static void quietly(Runnable r)` |  |
| 179 | 3 | `static double[] worldMid()` | Each product's world middle, by Good's ordinal: (import + export) / 2, the prototype's prices. |
| 183 | 1 | `static double v(double[] values, Good g)` |  |
| 186 | 7 | `static double[] campus(BuildingManager b, int n)` | Each kind's feed: its small and its large unit, `n` of each, from the catalogue. |
| 194 | 5 | `static double[] only(Kind k, double litres)` |  |
| 200 | 3 | `static double[] mix(double light, double medium, double heavy)` |  |
| 204 | 250 | `public static void main(String[] args) throws Exception` |  |
| 459 | 110 | `static void town() throws Exception` |  |

### THE SPREAD PLANNER (0.7.82, batch O5) (lines 570-1118)

| line | len | member | says |
|---:|---:|---|---|
| 594 | 15 | `static SpreadPlanner.City frame(String noLand, String noStaff)` | The prototype's frame as the planner's City: its capital, its wages, no standing costs, nameplate, and its money test; `land` and `staff` refuse the buildings named. |
| 611 | 7 | `static double[] prototypeValues(RefineryFlow.Flow f, double petrol, double diesel)` | The prototype's city_values: a product at its import price while the city wants more than it makes, its export price while it makes more, the middle otherwise. |
| 620 | 6 | `static double[] blend(double[] a, double wa, double[] b, double wb)` | Two runs' mixes as one, each grade's share of the two. |
| 628 | 10 | `static Refining.Outlook prototypeOutlook(double[] feed, double crude, double petrol, double diesel, double wells, double[] well...` | The outlook of a city in the prototype's frame: its units and crude units standing, the wells' crude at their grade, its values settled in three passes. |
| 646 | 27 | `static java.util.Map<String, Integer> prototypeRun(java.util.List<BuildingsTemplate> ts, double petrol, double diesel, double w...` | The planner run PROTOTYPE_MONTHS months in the prototype's frame: an order opens PROTOTYPE_BUILD_MONTHS on, one at a time; `beforeOpening` decides each month on its state before the month's opening, as the prototype's... |
| 674 | 5 | `static void open(BuildingsTemplate t, double[] feed, double[] crude, java.util.Map<String, Integer> standing)` |  |
| 680 | 5 | `static java.util.Map<String, Integer> row(Object...nameAndCount)` |  |
| 686 | 30 | `static void cityValue()` |  |
| 717 | 39 | `static void earnings(BuildingManager catalogue)` |  |
| 757 | 61 | `static void gates(BuildingManager catalogue)` |  |
| 819 | 48 | `static void theGameMoneyGates() throws Exception` |  |
| 868 | 34 | `static void fourRows(BuildingManager catalogue)` |  |
| 904 | 28 | `static Game plannerTown(String label, boolean held, String...units)` | The town the planner sections stand: the section 7 town's houses and works, an Oil Refinery and `units`; Refining held or not. |
| 936 | 43 | `static void idleThenShed() throws Exception` |  |
| 980 | 138 | `static void theInvestorsOrderAUnit() throws Exception` |  |

### 15. THE WORLD'S DEMAND FOR LUBRICANTS (0.7.103) (lines 1119-1190)

| line | len | member | says |
|---:|---:|---|---|
| 1121 | 69 | `static void lubricantsCapped(BuildingManager catalogue) throws Exception` |  |

### 16. THE RUN (0.7.105) (lines 1191-1349)

| line | len | member | says |
|---:|---:|---|---|
| 1216 | 1 | **type** `enum Way` | How the oil moves in a fixture: by lorry, all by rail at a quote, or by sea. |
| 1219 | 8 | `static GoodsMarket moved(Good g, Way way, double quote)` | A good's market as it moves one way (exchange 1): the band's share of a lorry's freight and the railway's charge, as sectors.Freight.haul() sets them (Ports.factor()). |
| 1229 | 14 | `static Refining.RunPrices movedPrices(Way way, double quote, double petrol, double diesel, double lubricants, double wells)` | The run's prices, every good moved one way: petrol and diesel's `petrol` and `diesel` litres at the net import price, the rest of every product at its net export price, the wells' `wells` tonnes at crude's export pric... |
| 1245 | 1 | `static double crudeMiddle()` | Crude's world middle: what the study's margins are a share of. |
| 1248 | 1 | `static double ofMiddle(double share, double crudeCost)` | A verdict's barrel - `share`, a share of what its crude cost - as a share of crude's world middle. |
| 1250 | 99 | `static void theRun(BuildingManager catalogue)` |  |

### 17. THE RUN IN A TOWN (0.7.105) (lines 1350-1444)

| line | len | member | says |
|---:|---:|---|---|
| 1352 | 92 | `static void theRunInATown() throws Exception` |  |

### 18. THE PLANNER'S PRICES AT THE MARGIN (0.7.105) (lines 1445-1496)

| line | len | member | says |
|---:|---:|---|---|
| 1447 | 49 | `static void thePlannerAtTheMargin(BuildingManager catalogue)` |  |

### 19. NO ORDER IN FLIGHT TO WAIT FOR, NO SECOND CRUDE UNIT ON SITE (0.7.105; BA1) (lines 1497-1543)

| line | len | member | says |
|---:|---:|---|---|
| 1499 | 37 | `static void oneOrderInFlight() throws Exception` |  |
| 1538 | 5 | `static String names(java.util.List<BuildingsTemplate> with)` | The buildings a package was weighed with, by name. |

