# CapitalFlowCheck.java - 727 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/CapitalFlowCheck.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

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

**Uses:** [CapitalFlows](CapitalFlows.md) (29), [DebtManager](DebtManager.md) (14), [Game](Game.md) (12), [SectorStatements](SectorStatements.md) (10), [SectorBooks](SectorBooks.md) (6), [Bank](Bank.md) (4), [GameFiles](GameFiles.md) (4), [Sectors](Sectors.md) (4), [ForeignAccounts](ForeignAccounts.md) (4), [Sector](Sector.md) (4), [Equity](Equity.md) (4), [OutwardInvestment](OutwardInvestment.md) (3), [MoneyAudit](MoneyAudit.md) (3), [TempDirs](TempDirs.md) (2), [Statement](Statement.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [Founding](Founding.md) (1), [EconomyManager](EconomyManager.md) (1)

## Sections

| line | section |
|---:|---|
| 69 | · 1. it comes for the spread |
| 102 | · 2. and it leaves when it closes |
| 111 | · 3. fragility is not a crisis |
| 142 | · 4. reserves are the defence |
| 188 | · 5. painful, never fatal |
| 215 | · 6. it reaches the bank |
| 276 | · 7. in a real city, and across a reload |
| 384 | · 8. and the city's own money goes the other way |
| 482 | · 9. the coupon abroad is profit, kept abroad (HD1, HD2) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |
| 39 | `static PrintStream out` |  |
| 40 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 36 | 692 | **type** `public class CapitalFlowCheck` | Hot money: does it come for the right reason, and does it leave for one? |
| 42 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 47 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 58 | 5 | `static void calm(CapitalFlows f, double cityRate, double reserves, int month)` | A quiet month with no shock in it. |
| 64 | 633 | `public static void main(String[] args) throws Exception` |  |
| 699 | 6 | `static Game couponCity(String label)` | A plain founding city two years on: its builders have earned, so a sector is in profit and the profit tax has something to take (HD1). |
| 707 | 4 | `static void quietMonth(Game g)` | One month of a city, its log kept quiet. |
| 713 | 7 | `static MoneyAudit.Result bop(double exports, double imports, double investedAbroad)` | A month at the city's edge: goods in and out, and money the sectors sent abroad. |
| 721 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |

