# CapitalFlowCheck.java - 483 lines · 6 methods · 0 constants · harnesses

`ham/citybuildersim/CapitalFlowCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [CapitalFlows](CapitalFlows.md) (29), [DebtManager](DebtManager.md) (12), [Game](Game.md) (7), [Bank](Bank.md) (4), [ForeignAccounts](ForeignAccounts.md) (4), [GameFiles](GameFiles.md) (3), [OutwardInvestment](OutwardInvestment.md) (2), [Sectors](Sectors.md) (2), [MoneyAudit](MoneyAudit.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [EconomyManager](EconomyManager.md) (1)

## Sections

| line | section |
|---:|---|
| 63 | · 1. it comes for the spread |
| 96 | · 2. and it leaves when it closes |
| 105 | · 3. fragility is not a crisis |
| 136 | · 4. reserves are the defence |
| 182 | · 5. painful, never fatal |
| 209 | · 6. it reaches the bank |
| 270 | · 7. in a real city, and across a reload |
| 374 | · 8. and the city's own money goes the other way |

## Fields (state)

| line | field | says |
|---:|---|---|
| 32 | `static int fails` |  |
| 33 | `static PrintStream out` |  |
| 34 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 30 | 454 | **type** `public class CapitalFlowCheck` | Hot money: does it come for the right reason, and does it leave for one? |
| 36 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 41 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 52 | 5 | `static void calm(CapitalFlows f, double cityRate, double reserves, int month)` | A quiet month with no shock in it. |
| 58 | 409 | `public static void main(String[] args) throws Exception` |  |
| 469 | 7 | `static MoneyAudit.Result bop(double exports, double imports, double investedAbroad)` | A month at the city's edge: goods in and out, and money the sectors sent abroad. |
| 477 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |

