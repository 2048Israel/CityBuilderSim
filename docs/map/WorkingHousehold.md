# WorkingHousehold.java - 83 lines · 13 methods · 0 constants · model

`ham/citybuildersim/WorkingHousehold.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> A household with an earner in it, at one pay tier.
> 
> Its income is the tier's wage bill, split across the tier's cells by how
> many earners each shape fields - a couple takes home twice what a single
> adult does, five adults sharing five times. Its money follows its earners
> when the household changes shape, for the same reason.
> 
> There is no SharedHousehold. Five adults sharing differ from a couple in
> nothing this ledger does - they pay one rent, they draw on one credit line
> per household, they discharge together - and a subclass that overrode
> nothing would be a costume. The flatshare's difference is upstream, in how
> FamilyModel forms it and what the rent per door does to five wages at once.

**Uses:** [PayTier](PayTier.md) (6), [AdultStage](AdultStage.md) (6), [FamilyStructure](FamilyStructure.md) (2), [Household](Household.md) (1)

**Used by (12):** [BankCheck](BankCheck.md), [CapitalGainsCheck](CapitalGainsCheck.md), [CardCheck](CardCheck.md), [DebtStatistics](DebtStatistics.md), [DenominationCheck](DenominationCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdStageCheck](HouseholdStageCheck.md), [MoneyWordsCheck](MoneyWordsCheck.md), [PeopleScreen](PeopleScreen.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 19 | `private final PayTier tier` |  |
| 21 | `private final AdultStage stage` | Which adult stage its grown-ups are in (HS2): the young, 18-34, or the established, 35-69. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 67 | **type** `public class WorkingHousehold extends Household` | A household with an earner in it, at one pay tier. |
| 24 | 3 | `public WorkingHousehold(FamilyStructure shape, PayTier tier)` | An established household: every working household before the stages was one. |
| 28 | 11 | `public WorkingHousehold(FamilyStructure shape, PayTier tier, AdultStage stage)` |  |
| 40 | 1 | `public PayTier tier()` |  |
| 42 | 1 | `public AdultStage stage()` | The adult stage of its grown-ups (HS2). |
| 50 | 3 | `public int stockGroup()` | The young's money is a stock group of its own, one a tier after the retired's (HS2): what a young household leaves behind goes first to the young of its tier, so the established's savings are not spread over a twenty-... |
| 55 | 1 | `public static int youngGroup(PayTier tier)` | The stock group of the young at a tier: after the retired's, one a tier (Household.STOCK_GROUPS). |
| 58 | 1 | `public String key()` | "COUPLE:SKILLED", and "COUPLE:SKILLED:YOUNG" for the young: the established keep the keys from before the stages. |
| 60 | 3 | `public String label()` |  |
| 63 | 1 | `public int row()` |  |
| 64 | 1 | `public boolean isRetired()` |  |
| 65 | 1 | `public int grownUps()` |  |
| 72 | 1 | `protected double studentRepayment()` | A graduate's student loan, repaid out of the family's wages over nine and a half years: a 114th of the balance a month. |
| 80 | 3 | `public double studentInterestAt(double annualRate)` | ...and the month's interest on it, at the city's annual rate over twelve, paid on top of the instalment. |

