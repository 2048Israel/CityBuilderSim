# OrderDoorCheck.java - 376 lines · 16 methods · 8 constants · harnesses

`ham/citybuildersim/OrderDoorCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

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
> ensemble plays it, to the first month from FROM at which its landlord stands
> at HN1-A's edge. Each order is then put to Game.consider() itself, by
> reflection (as BankFailureCheck puts one to retire()), between months, on
> the till the fixture sets.
> 
> What this has to prove:
>   1. the fixture: a month whose landlord is past its first year and asks its
>      register for nothing on two blocks, holds nothing abroad, whose quote
>      supports an issue, whose yard is short of a block's materials so the
>      door's price of one is over the planner's, the lender's test passing
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
>      the other side, a till short of one is held whole, in the bank's words.

**Uses:** [LongPlaytest](LongPlaytest.md) (33), [Game](Game.md) (21), [BuildingsTemplate](BuildingsTemplate.md) (10), [BusinessInvestment](BusinessInvestment.md) (8), [Mortgage](Mortgage.md) (8), [Equity](Equity.md) (6), [BusinessDebtManager](BusinessDebtManager.md) (3), [EconomyManager](EconomyManager.md) (2), [GameFiles](GameFiles.md) (2), [DebtManager](DebtManager.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Sectors](Sectors.md) (1), [SectorBooks](SectorBooks.md) (1), [Investor](Investor.md) (1), [Founding](Founding.md) (1), [LandParcel](LandParcel.md) (1)

## Sections

| line | section |
|---:|---|
| 284 | THE ENSEMBLE'S FOUNDING (as HousingDoorCheck plays it), to the edge |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `OrderDoorCheck.SEED` | `12` | The ensemble's seed HN1's harness plays (HousingDoorCheck.SEED). |
| 58 | `OrderDoorCheck.FROM` | `200, TO = 420` | The months searched for the landlord's edge: from FROM, to TO at most. |
| 60 | `OrderDoorCheck.TARGET` | `.02` | The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). |
| 61 | `OrderDoorCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 62 | `OrderDoorCheck.BLOCK` | `"Low-Rise Apartments", STORE = "Convenience Store"` |  |
| 64 | `OrderDoorCheck.SIX` | `6` | The project's order, and its till in stores at the door's price: one, and not two. |
| 65 | `OrderDoorCheck.TILL` | `1.5, SHORT_TILL =.5` |  |
| 67 | `OrderDoorCheck.MAX_PLOTS` | `60` | The plots the fixture may buy for its ground. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static PrintStream out` |  |
| 53 | `static int fails` |  |
| 148 | `static long asked, passed` | What the landlord's order told the door: asked, its price of one and of the order, the till, what the lender passed. |
| 149 | `static double priceOne, priceAsked, tillAtOrder` |  |
| 151 | `static double tillAtProject` | What the project's order told the door: the till, and the slice that carries its interest. |
| 152 | `static long carried` |  |
| 288 | `static boolean stuck, edged` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 327 | **type** `public class OrderDoorCheck` | An order at the door (FB15, 2026-11-20): a landlord's order sized, and its owners asked, at the price buildFor() charges; a project's order the lender says no to asked again of half, down to one. |
| 69 | 4 | `static void check(String label, boolean ok)` |  |
| 74 | 3 | `static boolean near(double a, double b)` |  |
| 79 | 3 | `static double door(Game g, BuildingsTemplate t, long n)` | What buildFor() charges for n of these, read as HousingDoorCheck reads the door: the quote and the plot. |
| 83 | 3 | `static boolean ground(Game g, BuildingsTemplate t, long n)` |  |
| 88 | 6 | `static double raise(Game g, String sector, double cost)` | What its register would ask its owners for on this cost (Equity.raiseFor()); nothing for a sector not listed. |
| 96 | 5 | `static boolean raisesNothing(Game g, String sector, double cost)` | Past its first year and asking its register for nothing on this cost. |
| 103 | 6 | `static boolean lenderPasses(Game g, BuildingsTemplate t, long n)` | The lender's test of n blocks with the down payment at the door's price in hand, as lenderReads() asks it. |
| 111 | 13 | `static boolean landlordAtTheEdge(Game g)` | HN1-A's landlord at its edge, its ground apart (bought: buyGround()), read and never acted on. |
| 126 | 4 | `static double shopBrings(Game g, BuildingsTemplate store)` | What the shopkeeper's register raises on SIX and it holds abroad: what reaches its till before the door. |
| 132 | 7 | `static boolean shopkeeperReady(Game g)` | A shopkeeper HN1-B's order can be put to: lending open, not banned, and bringing under half a store to its till. |
| 141 | 5 | `static int buyGround(Game g, BuildingsTemplate t, long n)` | Free ground for n of these, bought the cheapest plot at a time while the city can pay; how many plots it took. |
| 154 | 7 | `static void consider(Game g, BusinessInvestment.Decision d) throws Exception` |  |
| 162 | 116 | `public static void main(String[] args) throws Exception` |  |
| 279 | 4 | `static void finish()` |  |

### THE ENSEMBLE'S FOUNDING (as HousingDoorCheck plays it), to the edge (lines 284-376)

| line | len | member | says |
|---:|---:|---|---|
| 292 | 15 | `static void run(Game g, int months)` | ...and stopped at the first month-end from FROM with the landlord at its edge and the shopkeeper ready, the ground for three blocks bought (buyGround()), every later step a no-op. |
| 308 | 68 | `static void foundAndPlay(Game g)` |  |

