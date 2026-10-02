# PolicyPreview.java - 307 lines · 32 methods · 0 constants · model

`ham/citybuildersim/PolicyPreview.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> What a staged set of the Policy tab's dials would do, by the model's own
> arithmetic (0.7.36): the tax take under another policy, line by line, and
> THE BUDGET before and after it - advice, not a model change.
> 
> WHY. Every dial on the Policy tab is a proposal until it is applied, and
> the tab shows what it would do first ("a before/after preview once you've
> moved the dial", Jerus's call in 0.6.x). Nine of those "afters" were the
> screen's own arithmetic: the tax total summed in the screen with a clamp
> copied from TaxPolicy (and missing the bank's tax), the pension and the
> premiums scaled by the ratio of two rates (so a dial at zero previewed
> nothing), the EI bill scaled from the one the treasury paid rather than
> restruck on the pool, the savers' rate a share of the dial beside the rate
> the bank chose. CLAUDE.md's rule is that a screen recomputes nothing, and a
> rule changed in the model would not have changed on the tab. The Policy
> spec (runs/spec-policy-0735.md, its section 6.2 and D2) moved every one of
> them here and onto the classes that own the arithmetic.
> 
> HOW. The staged set is put through a detached copy of the city's policy
> (TaxPolicy.copy()), so its own setters clamp it; each line is then the
> owner's read at the copy - EconomyManager's tax take and payroll lines,
> Bank.taxAt() through Game.bankTaxUnder(), Unemployment.benefitsAt(),
> Healthcare's fees at one, Education.billedAt(), the grant's bill and the
> loan's interest - and the same read at the live policy is the line's
> "before". THE BUDGET is last month's balance (NationalAccounts.getBalance())
> plus the sum of every line's after less its before: struck the same way
> twice, so the difference is the answer even where a line's "before" is not
> last month's booked figure to the dollar (the bank's tax is NEXT month's,
> charged in arrears; the sales tax is scaled, D11). At the live policy each
> sector's and band's tax line is the month's booked figure (property's on
> the roll as it stands): PolicyPreviewCheck holds it.
> 
> WHAT NONE OF IT KNOWS is who changes what they do. A business taxed
> harder earns less, a dearer clinic turns people away, a cheaper school
> fills; that arrives in the month's own books, and every card on the tab
> says so behind its (i). Pure: it reads the city and changes nothing.

**Uses:** [Game](Game.md) (19), [TaxPolicy](TaxPolicy.md) (12), [WageBand](WageBand.md) (7), [EconomyManager](EconomyManager.md) (7), [Sectors](Sectors.md) (5), [Sector](Sector.md) (3), [Healthcare](Healthcare.md) (3), [JobType](JobType.md) (3), [HouseholdBalance](HouseholdBalance.md) (2), [LabourMarket](LabourMarket.md) (2), [Unemployment](Unemployment.md) (1), [EducationType](EducationType.md) (1), [HouseholdAccounts](HouseholdAccounts.md) (1)

**Used by (3):** [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 48 | THE TAX TAKE UNDER A POLICY (M2) |
| 102 | THE BUDGET (M8) |
| 169 | THE POLICY RATE, THE FLOOR AND THE PENSIONER |
| 201 | THE PROMISES, THE CLINIC AND THE SCHOOLS |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 264 | **type** `public final class PolicyPreview` | What a staged set of the Policy tab's dials would do, by the model's own arithmetic (0.7.36): the tax take under another policy, line by line, and THE BUDGET before and after it - advice, not a model change. |
| 46 | 1 | `private PolicyPreview()` |  |

### THE TAX TAKE UNDER A POLICY (M2) (lines 48-101)

| line | len | member | says |
|---:|---:|---|---|
| 58 | 28 | **type** `public record TaxTake(Map<String, Double> profit, Map<String, Double> sales, Map<String, Double> property, ...` | The tax a month every line would raise under one policy, struck on this month's books: profit, sales and property by sector key, wage by band, and the bank's profit tax at the policy's retail rate (next month's bill -... |
| 62 | 1 | `public double profit(String key)` _(in PolicyPreview.TaxTake)_ | One sector's line of a kind, 0 for a sector the map does not hold. |
| 63 | 1 | `public double sales(String key)` _(in PolicyPreview.TaxTake)_ |  |
| 64 | 1 | `public double property(String key)` _(in PolicyPreview.TaxTake)_ |  |
| 65 | 1 | `public double wage(WageBand band)` _(in PolicyPreview.TaxTake)_ |  |
| 68 | 1 | `public double profitTotal()` _(in PolicyPreview.TaxTake)_ | Every sector's profit tax and the bank's: the Profit page's head, which NationalAccounts' business line includes the bank in (the spec's B5). |
| 69 | 1 | `public double salesTotal()` _(in PolicyPreview.TaxTake)_ |  |
| 70 | 1 | `public double propertyTotal()` _(in PolicyPreview.TaxTake)_ |  |
| 71 | 5 | `public double wageTotal()` _(in PolicyPreview.TaxTake)_ |  |
| 78 | 1 | `public double total()` _(in PolicyPreview.TaxTake)_ | The four taxes together. |
| 80 | 5 | `private static double sum(Map<String, Double> m)` _(in PolicyPreview.TaxTake)_ |  |
| 88 | 13 | `public static TaxTake taxTake(Game g, TaxPolicy p)` | The tax take under `p`: every sector's three lines, every band's wage tax and the bank's, each its owner's read. |

### THE BUDGET (M8) (lines 102-168)

| line | len | member | says |
|---:|---:|---|---|
| 107 | 3 | **type** `public record Budget(double before, double after)` | THE BUDGET a month, before and after: last month's balance, and the same plus what the staged set would change. |
| 108 | 1 | `public double change()` _(in PolicyPreview.Budget)_ |  |
| 124 | 24 | `public static double change(Game g, TaxPolicy live, TaxPolicy after, double shareNow, double shareThen)` | What a staged set would change in the budget a month: every line it reaches, its read under `after` less the same read under `live`, revenue up and spending down. |
| 150 | 6 | `public static Budget budget(Game g, TaxPolicy after, double shareThen)` | THE BUDGET under `after` with the city's share of tuition at `shareThen`: last month's balance, and that plus change(). |
| 158 | 4 | `public static double tuitionPaid(Game g, TaxPolicy p, double share)` | What households would pay for tuition a month at a policy's scales and a share the city waives: Education.billedAt() less the waiver. |
| 164 | 4 | `public static double tuitionWaived(Game g, TaxPolicy p, double share)` | ...and what the city would waive of it. |

### THE POLICY RATE, THE FLOOR AND THE PENSIONER (lines 169-200)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 3 | `public static double realDepositRateAt(Game g, double policy)` | What savers would earn after inflation next month at this dial (M7): the bank's own choice at it, less the year's inflation - Game.realDepositRate()'s rule. |
| 179 | 3 | `public static double spendFactorAt(Game g, double policy)` | ...and the share of their spending above a basket a head the households would plan at it: HouseholdBalance.spendFactor() on that. |
| 190 | 5 | `public static double wageMoveAt(Game g, double floor)` | The factor every wage heads for with the real floor at `floor` (M6): a job's target is its base at the floor times multiples that do not move with it, and never under the floor in today's money, both in proportion to ... |
| 197 | 3 | `public static double cityPayrollAt(Game g, double floor)` | What the city's own staff - teachers, doctors and nurses - would cost a month with every wage moved by wageMoveAt(): today's posts, paid at the targets of the new floor. |

### THE PROMISES, THE CLINIC AND THE SCHOOLS (lines 201-307)

| line | len | member | says |
|---:|---:|---|---|
| 214 | 3 | **type** `public record Promises(double pensionGap, double eiPastPremiums, double subsidies, double grants)` | What the promises cost the treasury in a month, net of what they collect: the pension's gap, the sectors kept alive, EI past its premiums and the students' grants - the Policy tab's PROMISES, which the screen summed i... |
| 215 | 1 | `public double total()` _(in PolicyPreview.Promises)_ |  |
| 219 | 5 | `public static Promises promises(Game g)` | ...this month's. |
| 226 | 5 | `public static double careTreasuryShare(Game g)` | What the treasury paid towards care this month: the service's cost less the fees it collected, the funerals' and the health premium (the Health page's "the treasury's share"). |
| 237 | 5 | `public static double careTreasuryShareAt(Game g, double feeScale, double premiumRate)` | ...at a fee scale and a premium of the caller's, with every patient paying (the spec's B13: the fees at full on both sides of a move, so the move is the dial's and not who was priced out this month). |
| 250 | 7 | `public static double schoolBurden(Game g, EducationType kind, double scale, double share)` | What a place on a course costs a family as a share of a month's wage at a tuition scale and a city share of the caller's: the fee at the scale, less the share, over the best wage somebody who could take the course ear... |
| 259 | 5 | `public static double farmlandOnRoll(Game g, TaxPolicy p)` | The fields' ground on the property roll under a policy (the farmland relief, T15): today's land price on the share the policy assesses. |
| 266 | 6 | `public static double farmlandForgone(Game g, TaxPolicy p)` | ...and the tax a month the relief forgoes on the rest of it, at the fields' own monthly rate. |
| 274 | 4 | `public static double eiTreasuryAt(Game g, double premiumRate, double benefitRate)` | What EI and the grants take from the treasury a month at a premium and a benefit rate of the caller's: the pool's bill at the rate and the grants, less the premiums off the wage bill. |
| 280 | 4 | `public static double pensionGapUnder(Game g, TaxPolicy p)` | The pension's gap under a policy: the pensions at its rate less the contributions at its rate off the wage bill - getPensionShortfall()'s rule (M3, M4). |
| 286 | 5 | `public static double pensionCoverageUnder(Game g, TaxPolicy p)` | ...and what the contributions cover of the pensions under it - getPensionCoverage()'s rule; 1 with nothing owed. |
| 302 | 5 | `public static double pensionerHasUnder(Game g, TaxPolicy after)` | What one pensioner household would have to spend under `after` (M11): the retired row, whose income is the pension bill, with the bill moved by what the dial's move would change it by at today's seniors - HouseholdBal... |

