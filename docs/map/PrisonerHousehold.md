# PrisonerHousehold.java - 79 lines · 17 methods · 0 constants · model

`ham/citybuildersim/PrisonerHousehold.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Adults serving a sentence, as one ledger: the prisoners' ledger.
> 
> Jerus, 2026-09-11: "yes that means another population category, only adults
> can go to jail", and of a prisoner's money, held in a prisoners' ledger.
> 
> WHAT A PRISONER HAS. No income, no rent, no groceries - the city houses and
> feeds them, which is what the prisons' upkeep is for - and no credit. Their
> debts are FROZEN: no interest runs, nothing is discharged, nothing is
> borrowed. Their savings sit in the bank and do nothing else: no shares
> bought, nothing sent abroad, no offering subscribed.
> 
> THE MONEY FOLLOWS THEM, like everyone's. An adult sent down carries their
> savings and debt out of the cell they were in and into this one, and a
> prisoner released carries them back out into the out-of-work pool -
> HouseholdBalance.followThePeople() does it on the net change, as it does
> for every other move. Who goes to prison is mostly who is out of work, so
> the money moves with the unskilled tier's first, as the out of work's does.
> 
> WHO IS IN IT is Crime's: six monthly cohorts, each serving six months.

**Uses:** [PayTier](PayTier.md) (2), [Household](Household.md) (1)

**Used by (5):** [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [ServicesScreen](ServicesScreen.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 24 | 56 | **type** `public class PrisonerHousehold extends Household` | Adults serving a sentence, as one ledger: the prisoners' ledger. |
| 26 | 3 | `public PrisonerHousehold()` |  |
| 30 | 1 | `public PayTier tier()` |  |
| 31 | 1 | `public int row()` |  |
| 32 | 1 | `public boolean isRetired()` |  |
| 33 | 1 | `public int grownUps()` |  |
| 34 | 1 | `public int size()` |  |
| 36 | 1 | `public String label()` |  |
| 37 | 1 | `public String key()` |  |
| 40 | 1 | `public double earningWeight()` | Nobody's income is split to a prisoner. |
| 43 | 1 | `public int stockGroup()` | Most of them were out of work: see UnemployedHousehold.stockGroup(). |
| 46 | 1 | `protected double baskets()` | The city feeds them. |
| 49 | 1 | `protected boolean debtFrozen()` | Frozen while they are inside. |
| 66 | 1 | `protected double studentRepayment()` | Nothing comes off the loan while they are inside. |
| 69 | 1 | `public double studentInterestAt(double annualRate)` | ...and nothing is charged on it. |
| 72 | 1 | `public boolean canInvest()` | Held in the ledger. |
| 75 | 1 | `public double creditRoom(double disposablePer)` | No lender lends to somebody inside. |
| 78 | 1 | `protected double planningRoom()` | ...and they plan nothing: nothing to spend, no room to borrow. |

