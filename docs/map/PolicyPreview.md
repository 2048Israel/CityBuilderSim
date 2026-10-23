# PolicyPreview.java - 583 lines · 49 methods · 0 constants · model

`ham/citybuildersim/PolicyPreview.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

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
> AND ONE DIAL OFF THE POLICY TAB (0.7.48, C2): the fund's withdrawal, on
> Finances > The city's fund > Rules & cash - what it would pay next month,
> from its cash and from its market book, and the fund a year on
> (fundWithdrawalAt()), struck as TreasuryFund.payTransfer() strikes it.
> 
> AND ZONING (0.7.107, batch Z1): a zone's held ground, what it keeps from
> everyone, the price it gives up and its tax relief (zoneAt()), and the
> forest law's three kinds and its wood (forestAt()), each before and after
> on a staged Zoning.copy().
> 
> AND THE RULE'S STRICTNESS (0.7.52): at any step of the dial beside the
> inflation target, what the bank would aim at, what the rule sets on
> target and what it would advise at this year's inflation (ruleAt()),
> struck by DebtManager.ruleRate() at that step; CentralBankCheck 21
> holds it, the preview at the step in force being the rule's own.
> 
> WHAT NONE OF IT KNOWS is who changes what they do. A business taxed
> harder earns less, a dearer clinic turns people away, a cheaper school
> fills; that arrives in the month's own books, and every card on the tab
> says so behind its (i). Pure: it reads the city and changes nothing.

**Uses:** [Game](Game.md) (36), [TaxPolicy](TaxPolicy.md) (17), [EconomyManager](EconomyManager.md) (8), [WageBand](WageBand.md) (7), [Ports](Ports.md) (6), [Sectors](Sectors.md) (6), [TreasuryFund](TreasuryFund.md) (6), [Zoning](Zoning.md) (5), [DebtManager](DebtManager.md) (5), [Freight](Freight.md) (4), [Sector](Sector.md) (3), [Healthcare](Healthcare.md) (3), [JobType](JobType.md) (3), [LandManager](LandManager.md) (3), [HouseholdBalance](HouseholdBalance.md) (2), [Retail](Retail.md) (2), [PortDivision](PortDivision.md) (2), [LabourMarket](LabourMarket.md) (2), [Unemployment](Unemployment.md) (1), [EducationType](EducationType.md) (1), [HouseholdAccounts](HouseholdAccounts.md) (1), [Resource](Resource.md) (1), [PriceIndex](PriceIndex.md) (1)

**Used by (10):** [CentralBankCheck](CentralBankCheck.md), [FundScreen](FundScreen.md), [GroceryCheck](GroceryCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | THE TAX TAKE UNDER A POLICY (M2) |
| 118 | THE BUDGET (M8) |
| 173 | THE FUEL DUTY (0.7.103, batch P4; Jerus's A7) |
| 204 | THE PORT DUES (FR4; the dial is FR3's) |
| 279 | THE POLICY RATE, THE FLOOR AND THE PENSIONER |
| 311 | THE PROMISES, THE CLINIC AND THE SCHOOLS |
| 457 | THE FUND'S WITHDRAWAL (0.7.48, C2) |
| 491 | ZONING (0.7.107, batch Z1; the project's spec-zoning.md 4) |
| 561 | THE RULE'S STRICTNESS (0.7.52) |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 524 | **type** `public final class PolicyPreview` | What a staged set of the Policy tab's dials would do, by the model's own arithmetic (0.7.36): the tax take under another policy, line by line, and THE BUDGET before and after it - advice, not a model change. |
| 62 | 1 | `private PolicyPreview()` |  |

### THE TAX TAKE UNDER A POLICY (M2) (lines 64-117)

| line | len | member | says |
|---:|---:|---|---|
| 74 | 28 | **type** `public record TaxTake(Map<String, Double> profit, Map<String, Double> sales, Map<String, Double> property, ...` | The tax a month every line would raise under one policy, struck on this month's books: profit, sales and property by sector key, wage by band, and the bank's profit tax at the policy's retail rate (next month's bill -... |
| 78 | 1 | `public double profit(String key)` _(in PolicyPreview.TaxTake)_ | One sector's line of a kind, 0 for a sector the map does not hold. |
| 79 | 1 | `public double sales(String key)` _(in PolicyPreview.TaxTake)_ |  |
| 80 | 1 | `public double property(String key)` _(in PolicyPreview.TaxTake)_ |  |
| 81 | 1 | `public double wage(WageBand band)` _(in PolicyPreview.TaxTake)_ |  |
| 84 | 1 | `public double profitTotal()` _(in PolicyPreview.TaxTake)_ | Every sector's profit tax and the bank's: the Profit page's head, which NationalAccounts' business line includes the bank in (the spec's B5). |
| 85 | 1 | `public double salesTotal()` _(in PolicyPreview.TaxTake)_ |  |
| 86 | 1 | `public double propertyTotal()` _(in PolicyPreview.TaxTake)_ |  |
| 87 | 5 | `public double wageTotal()` _(in PolicyPreview.TaxTake)_ |  |
| 94 | 1 | `public double total()` _(in PolicyPreview.TaxTake)_ | The four taxes together. |
| 96 | 5 | `private static double sum(Map<String, Double> m)` _(in PolicyPreview.TaxTake)_ |  |
| 104 | 13 | `public static TaxTake taxTake(Game g, TaxPolicy p)` | The tax take under `p`: every sector's three lines, every band's wage tax and the bank's, each its owner's read. |

### THE BUDGET (M8) (lines 118-172)

| line | len | member | says |
|---:|---:|---|---|
| 123 | 3 | **type** `public record Budget(double before, double after)` | THE BUDGET a month, before and after: last month's balance, and the same plus what the staged set would change. |
| 124 | 1 | `public double change()` _(in PolicyPreview.Budget)_ |  |
| 144 | 28 | `public static double change(Game g, TaxPolicy live, TaxPolicy after, double shareNow, double shareThen)` | What a staged set would change in the budget a month: every line it reaches, its read under `after` less the same read under `live`, revenue up and spending down. |

### THE FUEL DUTY (0.7.103, batch P4; Jerus's A7) (lines 173-203)

| line | len | member | says |
|---:|---:|---|---|
| 178 | 4 | `public static double fuelLitres(Game g)` | The litres the drivers bought at the pump last month, at the stations and past them: what a duty is struck on (sectors.Retail). |
| 191 | 4 | `public static double fuelDutyAt(Game g, TaxPolicy p)` | What the fuel duty would raise a month under a policy: the month's litres at the pump at its duty a litre as charged (TaxPolicy .chargedFuelDuty()) - at the city's own dial what the month took, to the bit (EconomyMana... |
| 197 | 6 | `public static double pumpPriceAt(Game g, TaxPolicy p)` | ...and a litre at the pump under it: the sign's price today without the city's duty, and the policy's on top. |

### THE PORT DUES (FR4; the dial is FR3's) (lines 204-278)

| line | len | member | says |
|---:|---:|---|---|
| 209 | 7 | `public static double portTonnes(Game g)` | The tonnes that crossed the city's berths last month and were billed, all kinds: what the dues are struck on (sectors.PortDivision). |
| 228 | 11 | `public static double portDuesAt(Game g, TaxPolicy p)` | What the port dues would raise a month under a policy: the month's tonnes across the berths, kind by kind, at the kind's default due a tonne (the dial not in it, as the month was billed) times the policy's dial - the ... |
| 246 | 6 | `public static double handlingQuoteAt(Game g, Ports.Cargo kind, TaxPolicy p)` | ...and what a tonne of a kind pays to cross a berth under it: the crew's quote in force and the kind's due at the policy's dial (PortDivision .getQuote()'s own sum, so at the city's dial it is the quote to the bit); n... |
| 254 | 4 | `public static boolean hasBerth(Game g, Ports.Cargo kind)` | Whether a kind has a berth to charge on: the division billed it a default due last month (PortDivision.getDuesRate()). |
| 260 | 6 | `public static Budget budget(Game g, TaxPolicy after, double shareThen)` | THE BUDGET under `after` with the city's share of tuition at `shareThen`: last month's balance, and that plus change(). |
| 268 | 4 | `public static double tuitionPaid(Game g, TaxPolicy p, double share)` | What households would pay for tuition a month at a policy's scales and a share the city waives: Education.billedAt() less the waiver. |
| 274 | 4 | `public static double tuitionWaived(Game g, TaxPolicy p, double share)` | ...and what the city would waive of it. |

### THE POLICY RATE, THE FLOOR AND THE PENSIONER (lines 279-310)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 3 | `public static double realDepositRateAt(Game g, double policy)` | What savers would earn after inflation next month at this dial (M7): the bank's own choice at it, less the inflation savers expect - Game.realDepositRate()'s rule since 0.7.42, so at the dial in force it parts from th... |
| 289 | 3 | `public static double spendFactorAt(Game g, double policy)` | ...and the share of their spending above a basket a head the households would plan at it: HouseholdBalance.spendFactor() on that. |
| 300 | 5 | `public static double wageMoveAt(Game g, double floor)` | The factor every wage heads for with the real floor at `floor` (M6): a job's target is its base at the floor times multiples that do not move with it, and never under the floor in today's money, both in proportion to ... |
| 307 | 3 | `public static double cityPayrollAt(Game g, double floor)` | What the city's own staff - teachers, doctors and nurses - would cost a month with every wage moved by wageMoveAt(): today's posts, paid at the targets of the new floor. |

### THE PROMISES, THE CLINIC AND THE SCHOOLS (lines 311-456)

| line | len | member | says |
|---:|---:|---|---|
| 325 | 4 | **type** `public record Promises(double pensionGap, double eiPastPremiums, double subsidies, double grants, double fo...` | What the promises cost the treasury in a month, net of what they collect: the pension's gap, the sectors kept alive, EI past its premiums, the students' grants and - since 0.7.43 - the food vouchers paid at the month'... |
| 327 | 1 | `public double total()` _(in PolicyPreview.Promises)_ |  |
| 331 | 5 | `public static Promises promises(Game g)` | ...this month's. |
| 350 | 3 | `public static double foodAssistanceAt(Game g, double share)` | What the food vouchers would have cost at the last sale at a dial of the caller's (0.7.43): each eligible household's voucher at that share of its baskets at the price that sale charged, up to the baskets it got - Hou... |
| 355 | 3 | `public static double foodAssistanceHouseholds(Game g)` | ...and the households a voucher would go to at that price, at any dial over 0. |
| 360 | 3 | `public static double foodAssistanceHouseholdsAt(Game g, double share)` | ...the same at a dial of the caller's (0.7.45): none at 0, every eligible household at any dial over it. |
| 365 | 3 | `public static double foodAssistanceBasketsAt(Game g, double share)` | ...and the baskets the vouchers would have paid for at it (0.7.45): foodAssistanceAt() over the price. |
| 370 | 3 | `public static double lastSalePrice(Game g)` | The price a basket was charged at the last sale, which the vouchers were struck at (Retail.getChargedPrice()). |
| 375 | 5 | `public static double careTreasuryShare(Game g)` | What the treasury paid towards care this month: the service's cost less the fees it collected, the funerals' and the health premium (the Health page's "the treasury's share"). |
| 386 | 5 | `public static double careTreasuryShareAt(Game g, double feeScale, double premiumRate)` | ...at a fee scale and a premium of the caller's, with every patient paying (the spec's B13: the fees at full on both sides of a move, so the move is the dial's and not who was priced out this month). |
| 399 | 7 | `public static double schoolBurden(Game g, EducationType kind, double scale, double share)` | What a place on a course costs a family as a share of a month's wage at a tuition scale and a city share of the caller's: the fee at the scale, less the share, over the best wage somebody who could take the course ear... |
| 408 | 5 | `public static double farmlandOnRoll(Game g, TaxPolicy p)` | The fields' ground on the property roll under a policy (the farmland relief, T15): today's land price on the share the policy assesses. |
| 415 | 6 | `public static double farmlandForgone(Game g, TaxPolicy p)` | ...and the tax a month the relief forgoes on the rest of it, at the fields' own monthly rate. |
| 423 | 4 | `public static double eiTreasuryAt(Game g, double premiumRate, double benefitRate)` | What EI and the grants take from the treasury a month at a premium and a benefit rate of the caller's: the pool's bill at the rate and the grants, less the premiums off the wage bill. |
| 429 | 4 | `public static double pensionGapUnder(Game g, TaxPolicy p)` | The pension's gap under a policy: the pensions at its rate less the contributions at its rate off the wage bill - getPensionShortfall()'s rule (M3, M4). |
| 435 | 5 | `public static double pensionCoverageUnder(Game g, TaxPolicy p)` | ...and what the contributions cover of the pensions under it - getPensionCoverage()'s rule; 1 with nothing owed. |
| 451 | 5 | `public static double pensionerHasUnder(Game g, TaxPolicy after)` | What one pensioner household would have to spend under `after` (M11): the retired row, whose income is the pension bill, with the bill moved by what the dial's move would change it by at today's seniors - HouseholdBal... |

### THE FUND'S WITHDRAWAL (0.7.48, C2) (lines 457-490)

| line | len | member | says |
|---:|---:|---|---|
| 468 | 1 | **type** `public record Withdrawal(double due, double fromCash, double toSell, double unpaid, double yearOn)` | What the fund's withdrawal would pay the treasury next month at a dial of the caller's, on the fund as it stands: the due; what its cash would pay of it; the rest - over the default sold from its market book (toSell),... |
| 479 | 11 | `public static Withdrawal fundWithdrawalAt(Game g, double rate)` | ...struck as TreasuryFund.payTransfer() strikes it: the due at the rate's whole steps on Game.fundValue() (TreasuryFund.withdrawalAt() - Game.fundTransferDue() at the dial in force, to the bit); its cash after what la... |

### ZONING (0.7.107, batch Z1; the project's spec-zoning.md 4) (lines 491-560)

| line | len | member | says |
|---:|---:|---|---|
| 515 | 2 | **type** `public record ZoneAt(String sector, double sizeSqFt, double usedSqFt, double heldSqFt, double priceShare, d...` | One zone under a zoning, on the land as it stands: its size and what its sector has built in it, square feet; what it holds of the free ground; its price share; the held ground at the going price; the price given up s... |
| 519 | 14 | `public static ZoneAt zoneAt(Game g, Zoning z, String sector)` | ...struck from LandManager.heldUnder() and the roll's rule: the relief is the zone's built ground (never more than the sector stands on) at the roll's price less the zone's, on the share the roll carries, at the secto... |
| 535 | 6 | `public static double zoningTaxChange(Game g, Zoning after)` | What a staged zoning would change in the property tax a month: every zone's relief at the city's own zoning less at the staged one's. |
| 549 | 2 | **type** `public record ForestAt(double protectedSqFt, double workingSqFt, double openSqFt, double freeForAnyone, dou...` | The forest law under a zoning, on the land as it stands: the protected, working and open forest and the ground free for anyone, square feet; the wood it keeps standing for ever and the wood that may be cut, cubic metr... |
| 553 | 7 | `public static ForestAt forestAt(Game g, Zoning z)` | ...struck by LandManager.groundUnder(). |

### THE RULE'S STRICTNESS (0.7.52) (lines 561-583)

| line | len | member | says |
|---:|---:|---|---|
| 573 | 1 | **type** `public record RuleAt(DebtManager.Strictness step, String aims, double onTarget, double advised)` | What the rule would do at one step of the strictness dial, at the city's target: the step; what it aims at, in words (DebtManager.aimWords(): "aims under 2.0%, at 1.0%", "acts only past 4.0% (or under 2.0%)"); the rat... |
| 576 | 7 | `public static RuleAt ruleAt(Game g, DebtManager.Strictness step)` | ...struck by DebtManager.ruleRate() and advisedPolicyRate() at that step; at the step in force, the rule's own to the bit. |

