# OrderDoorCheck.java - 463 lines · 19 methods · 10 constants · harnesses

`ham/citybuildersim/OrderDoorCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> An order at the door (FB15, 2026-11-20): a landlord's order sized, and its
> owners asked, at the price buildFor() charges; a project's order the lender
> says no to asked again of half, down to one.
> 
> WHY: HN1 left two rows (docs/queue/HN1-notes.md, section 6). HN1-A: while
> the city buys materials in, the planner prices a landlord's home 2-5% under
> the door (BusinessInvestment.getCostOf() at the builders' materials price,
> buildFor() at the market's draw), and consider() raised the owners' money to
> the plan's down payment exactly, so the door said "needs more of its own for
> the 15% down payment" even on one block, and an order of more lost half of
> it. HN1-B: consider()'s order with no mortgage was still all or nothing at
> the door on the bank's capital, where HN1 had made the landlords' a halving.
> 
> The city is the ensemble's AUTO2 seed 12, founded and played as the
> ensemble plays it, to month FROM. There the two conditions of HN1-A's edge
> that are prices are set by hand (FS9): the yard left YARD_SHARE of a block's
> materials, and the market's materials, made here and imported, at DEAR over
> the planner's world price (GoodsMarket.setLocalPrice(), setWorldPrices()).
> WHY: the harness searched m200-420 for the first month the played landlord
> stood at the edge, and on HT1's path (0.9.184) none did: the door was at or
> under the planner in 170 of 220 months, the yard holding a block or the
> market's materials cheaper than the world's. Each order is then put to
> Game.consider() itself, by reflection (as BankFailureCheck puts one to
> retire()), between months, on the till the fixture sets.
> 
> What this has to prove:
>   1. the fixture: a month whose landlord is past its first year and asks its
>      register for nothing on two blocks, holds nothing abroad, whose quote
>      supports an issue, whose yard is short of a block's materials so the
>      door's price of one is over the planner's (both set by hand, the
>      market's price read back at the door's draw), the lender's test passing
>      two on the down payment at the door's price and the bank not short of
>      capital for them; and a shopkeeper whose register and money abroad
>      bring its till at the door to under half a store. Ground for three
>      blocks, and later for SIX stores, is bought a plot at a time, as the
>      ensemble's player buys it (Game.buyLandBlock());
>   2. HN1-A: the order is priced as the door charges (the order's price of
>      one and of two, the build card's lender test); an order of one, the
>      till empty and the owners asked for its down payment, is built on a
>      mortgage, not held at the door; an order of two the same way is built
>      whole, not halved at the door;
>   3. HN1-B: an order of SIX stores whose slice carries its interest, the
>      bank's capital rule letting no borrower's debt grow and the till
>      holding one at the door's price but not six, is built in part: the
>      halving's largest the till buys outright, paid at the door's price, in
>      the advisor's words, and not left marked as refused for capital; from
>      the other side, a till short of one is held whole, in the bank's words;
>      and with the bank shut, as past the default point, the order is held
>      whole with the till holding one, as on main (REV-FB15: there the till is
>      the sector's last money).

**Uses:** [LongPlaytest](LongPlaytest.md) (33), [Game](Game.md) (25), [BuildingsTemplate](BuildingsTemplate.md) (12), [BusinessInvestment](BusinessInvestment.md) (9), [Mortgage](Mortgage.md) (9), [Equity](Equity.md) (7), [BusinessDebtManager](BusinessDebtManager.md) (4), [EconomyManager](EconomyManager.md) (3), [GoodsMarket](GoodsMarket.md) (2), [Good](Good.md) (2), [GameFiles](GameFiles.md) (2), [SubstationCheck](SubstationCheck.md) (2), [DebtManager](DebtManager.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Sectors](Sectors.md) (1), [SectorBooks](SectorBooks.md) (1), [Investor](Investor.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [Founding](Founding.md) (1), [LandParcel](LandParcel.md) (1)

## Sections

| line | section |
|---:|---|
| 375 | THE ENSEMBLE'S FOUNDING (as HousingDoorCheck plays it), to the edge |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 66 | `OrderDoorCheck.SEED` | `12` | The ensemble's seed HN1's harness plays (HousingDoorCheck.SEED). |
| 68 | `OrderDoorCheck.FROM` | `200` | The month the city is played to, the edge's prices then set by hand (FS9: no search). |
| 70 | `OrderDoorCheck.YARD_SHARE` | `.5` | ★ The yard the fixture leaves, a share of one block's materials: short of a block, as main's own edge was (204 of 377 at m206). |
| 72 | `OrderDoorCheck.DEAR` | `.035` | ★ The market's materials at the door over the planner's world price, set by hand: within HN1-A's measured 2-5%. |
| 74 | `OrderDoorCheck.TARGET` | `.02` | The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). |
| 75 | `OrderDoorCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 76 | `OrderDoorCheck.BLOCK` | `"Low-Rise Apartments", STORE = "Convenience Store"` |  |
| 78 | `OrderDoorCheck.SIX` | `6` | The project's order, and its till in stores at the door's price: one, and not two. |
| 79 | `OrderDoorCheck.TILL` | `1.5, SHORT_TILL =.5` |  |
| 81 | `OrderDoorCheck.MAX_PLOTS` | `60` | The plots the fixture may buy for its ground. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static PrintStream out` |  |
| 63 | `static int fails` |  |
| 197 | `static long asked, passed` | What the landlord's order told the door: asked, its price of one and of the order, the till, what the lender passed. |
| 198 | `static double priceOne, priceAsked, tillAtOrder` |  |
| 200 | `static double tillAtProject` | What the project's order told the door: the till, and the slice that carries its interest. |
| 201 | `static long carried` |  |
| 379 | `static boolean stuck` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 404 | **type** `public class OrderDoorCheck` | An order at the door (FB15, 2026-11-20): a landlord's order sized, and its owners asked, at the price buildFor() charges; a project's order the lender says no to asked again of half, down to one. |
| 83 | 4 | `static void check(String label, boolean ok)` |  |
| 88 | 3 | `static boolean near(double a, double b)` |  |
| 93 | 3 | `static double door(Game g, BuildingsTemplate t, long n)` | What buildFor() charges for n of these, read as HousingDoorCheck reads the door: the quote and the plot. |
| 97 | 3 | `static boolean ground(Game g, BuildingsTemplate t, long n)` |  |
| 102 | 6 | `static double raise(Game g, String sector, double cost)` | What its register would ask its owners for on this cost (Equity.raiseFor()); nothing for a sector not listed. |
| 110 | 5 | `static boolean raisesNothing(Game g, String sector, double cost)` | Past its first year and asking its register for nothing on this cost. |
| 117 | 6 | `static boolean lenderPasses(Game g, BuildingsTemplate t, long n)` | The lender's test of n blocks with the down payment at the door's price in hand, as lenderReads() asks it. |
| 125 | 13 | `static boolean landlordAtTheEdge(Game g)` | HN1-A's landlord at its edge, its ground apart (bought: buyGround()), read and never acted on. |
| 140 | 4 | `static double shopBrings(Game g, BuildingsTemplate store)` | What the shopkeeper's register raises on SIX and it holds abroad: what reaches its till before the door. |
| 146 | 7 | `static boolean shopkeeperReady(Game g)` | A shopkeeper HN1-B's order can be put to: lending open, not banned, and bringing under half a store to its till. |
| 155 | 5 | `static int buyGround(Game g, BuildingsTemplate t, long n)` | Free ground for n of these, bought the cheapest plot at a time while the city can pay; how many plots it took. |
| 166 | 7 | `static void shortAndDear(Game g, BuildingsTemplate block)` | HN1-A's two prices, by hand: the yard left YARD_SHARE of a block, and the market's materials, the makers' and the world's landed, at DEAR over what the planner prices them at (BuildingManager.getConstructionMaterialPr... |
| 175 | 3 | `static double dearPrice(Game g)` | What the fixture prices the market's materials at: DEAR over the planner's price. |
| 180 | 15 | `static void edgeParts(Game g, BuildingsTemplate block)` | Each condition of the edge, printed, so a fixture that misses it says which. |
| 203 | 7 | `static void consider(Game g, BusinessInvestment.Decision d) throws Exception` |  |
| 211 | 158 | `public static void main(String[] args) throws Exception` |  |
| 370 | 4 | `static void finish()` |  |

### THE ENSEMBLE'S FOUNDING (as HousingDoorCheck plays it), to the edge (lines 375-463)

| line | len | member | says |
|---:|---:|---|---|
| 382 | 10 | `static void run(Game g, int months)` | ...and stopped at the month-end FROM, every later step a no-op (FS9: the edge is then caused, not searched for). |
| 393 | 70 | `static void foundAndPlay(Game g)` |  |

