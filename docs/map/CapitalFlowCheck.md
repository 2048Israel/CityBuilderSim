# CapitalFlowCheck.java - 843 lines · 10 methods · 1 constants · harnesses

`ham/citybuildersim/CapitalFlowCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> Hot money: does it come for the right reason, and does it leave for one?
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Does the SPREAD pull it - the excess return net of what the risk costs -
>      rather than the headline rate? A city paying 9% while the world charges
>      it 7% for its own risk is not offering anything, and money that came for
>      that is money that came for a number rather than a reason.
> 
>   2. Is fragility distinct from crisis? Thin reserves must make a shock into
>      a run without themselves being one - the first version got this backwards
>      and produced 110 sudden stops in 333 years, each of them a city fleeing
>      itself before any money had arrived.
> 
>   3. Do reserves actually defend? That is the entire answer to "what can the
>      player do", so if a war chest does not change the outcome, phases 2 and
>      3 bought nothing.
> 
>   4. Does it reach the bank, and does the bank's capacity go with it? Money
>      that arrives and changes nothing is a number on a screen.
> 
>   5. Is the coupon the businesses earn on their dollars abroad profit, kept
>      abroad (HD1, 2026-10-29; HD2, 2026-10-30)? A line of the statement
>      below the operating line, taxed, in the dividend, with the cash kept
>      where it was earned: the dollars held grow by it, the till pays the tax
>      and the dividend and receives none of it - while the rate moving the
>      dollars is a revaluation, no income.
> 
> FIXTURES (FS9). Sections 1-6 run on bare CapitalFlows (and a bare Bank in 6) with the spread, the premium and the reserves handed to the rule. Section 7
> is a built city played AUDITED_MONTHS with an audit each (the identity: the money balances every month): its carry is caused by
> the dial (setPolicyRate(.06) over the floor DebtManager.floorRate(), asserted over the world's rate and the country premium
> before a month runs) and what arrives is read against the rule - ARRIVAL_SPEED of the gap to the target each month, a position
> over MIN_STOCK and never past the target - not against a spread or a stock the played city happened to reach. Section 8 sets its
> tills, the borrower's loan and the bank's rate by hand. Section 9 is a twin of two cities, the rate pinned (ForeignAccounts.pinRate())
> at one rate and pinned again a set rateMove higher between the two struck months, and the builders' profit set by booking a sale
> to them in each struck month (Sector.sellOldMetal() with Sector.billForService() on the copper refiners, as CopperRefining books
> one), so the twin is in profit and the rate moves by cause. WHY: the played city moved under them - the twin was a few hundred
> dollars short of a profit and the dollars' rate moved or did not as its market drew - and the laws beneath were skipped, not run.

**Uses:** [CapitalFlows](CapitalFlows.md) (33), [Game](Game.md) (15), [DebtManager](DebtManager.md) (13), [SectorStatements](SectorStatements.md) (10), [Sectors](Sectors.md) (7), [SectorBooks](SectorBooks.md) (6), [Bank](Bank.md) (4), [GameFiles](GameFiles.md) (4), [ForeignAccounts](ForeignAccounts.md) (4), [Sector](Sector.md) (4), [Equity](Equity.md) (4), [OutwardInvestment](OutwardInvestment.md) (3), [MoneyAudit](MoneyAudit.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [TempDirs](TempDirs.md) (2), [Statement](Statement.md) (2), [Founding](Founding.md) (1), [EconomyManager](EconomyManager.md) (1), [OldMetal](OldMetal.md) (1), [Landmasses](Landmasses.md) (1)

## Sections

| line | section |
|---:|---|
| 80 | · 1. it comes for the spread |
| 113 | · 2. and it leaves when it closes |
| 122 | · 3. fragility is not a crisis |
| 153 | · 4. reserves are the defence |
| 199 | · 5. painful, never fatal |
| 226 | · 6. it reaches the bank |
| 287 | · 7. in a real city, and across a reload |
| 428 | · 8. and the city's own money goes the other way |
| 526 | · 9. the coupon abroad is profit, kept abroad (HD1, HD2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 803 | `CapitalFlowCheck.AUDITED_MONTHS` | `10 * CapitalFlows.PANIC_MONTHS` | The months the hot city is played with an audit each: ten panic windows (PANIC_MONTHS), so the money arrives, runs home in a stop and comes back over the audit. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `static int fails` |  |
| 50 | `static PrintStream out` |  |
| 51 | `static PrintStream quiet` |  |
| 827 | `static boolean fixtureListBuilt` | BA1, 2026-11-04: whether section 7's whole build list was taken (see the comment where it is ordered). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 47 | 797 | **type** `public class CapitalFlowCheck` | Hot money: does it come for the right reason, and does it leave for one? |
| 53 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 58 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 69 | 5 | `static void calm(CapitalFlows f, double cityRate, double reserves, int month)` | A quiet month with no shock in it. |
| 75 | 705 | `public static void main(String[] args) throws Exception` |  |
| 782 | 19 | `static Game couponCity(String label)` | A plain founding city two years on: its builders have earned, so a sector is in profit and the profit tax has something to take (HD1). |
| 806 | 4 | `static void bookSale(Game g, String seller, double amount)` | A sale of old metal booked to a seller as the model books one (CopperRefining.settle()): other revenue for the seller, an input for the copper refiners, a transfer within the sectors. |
| 812 | 4 | `static void quietMonth(Game g)` | One month of a city, its log kept quiet. |
| 818 | 7 | `static MoneyAudit.Result bop(double exports, double imports, double investedAbroad)` | A month at the city's edge: goods in and out, and money the sectors sent abroad. |
| 830 | 6 | `static boolean order(Game game, String name, int quantity)` | An order paid out of cash: the treasury is raised to the order's quote if the queue's price outran it; true if the order was taken. |
| 837 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |

