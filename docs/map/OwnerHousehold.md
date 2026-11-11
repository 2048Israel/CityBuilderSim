# OwnerHousehold.java - 88 lines · 11 methods · 5 constants · model

`ham/citybuildersim/OwnerHousehold.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The owner families, as one ledger: one two-adult household per company that
> has listed and not gone broke.
> 
> Jerus, 2026-11-02, on the households study: business owners are "few in
> nature and very rich", "one per listed company", "No job, lives of
> dividends or sells or whatever"; "the others still have shares, they just
> dont get the initial ownership"; "if sector goes broke he just joins the
> regular pop pool".
> 
> WHY A CELL OF THEIR OWN. The founders' grant used to go to every household,
> by grown-ups, and ordinary households ended up owning 98% of what
> households owned at month 21 (study 2.3): the people who started the
> company held none of it more than anybody else. Now a listing company's
> founders' shares go to this cell alone, each family an equal part of every
> founding stake; the others buy their shares at the offerings and on the
> exchange, by the households' rules (tradesShares()), and the stakes here
> are sold down by the line's own rule (OWNER_SELL_A_YEAR).
> 
> WHAT AN OWNER HAS. No wage - they live on dividends and on what they sell -
> so the earning weight is nothing and the labour force does not count them
> (PopulationManager.setOwners). Their savings, bonds and money abroad are
> kept by the ordinary rules: only the shares are theirs alone.
> 
> WHO IS IN IT is the line's: HouseholdBalance.ownerFamilies() companies, two
> adults each, taken out of the adult pool with the outside adults
> (FamilyModel.rebuild).

**Uses:** [Household](Household.md) (1), [PayTier](PayTier.md) (1)

**Used by (4):** [Exchange](Exchange.md), [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [OwnerCheck](OwnerCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 37 | `OwnerHousehold.OWNER_SELL_A_YEAR` | `.05` | The share of each founding stake the line offers on the exchange in a year: a stake falls to about a fifth in 30 years (.95^30 = .21). |
| 44 | `OwnerHousehold.OWNER_SELL_A_MONTH` | `1 - Math.pow(1 - OWNER_SELL_A_YEAR, 1.0 / 12)` | A twelfth of the year's sale, compounded: a month's share of each holding the line offers on the exchange, so twelve months leave 1 - OWNER_SELL_A_YEAR of it. |
| 47 | `OwnerHousehold.KEY` | `"OWNER"` | The line's key: the cell's name on the books, the save and the exchange's orders. |
| 54 | `OwnerHousehold.OWNER_BROKE_MONTHS` | `12` | Months a company's fair value sits at the exchange's floor before its family gives up and joins the regular pool: the window Migration.DECLINE_MONTHS gives a tier. |
| 62 | `OwnerHousehold.IN_CITIES` | `false` | Whether a city runs the owner line: off until Jerus's call on what pays the retired's care once the founders' dividends are the owners' (BO1's notes, section 1: with it on, the playtest city had about a third of its p... |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 31 | 58 | **type** `public class OwnerHousehold extends Household` | The owner families, as one ledger: one two-adult household per company that has listed and not gone broke. |
| 64 | 3 | `public OwnerHousehold()` |  |
| 68 | 1 | `public PayTier tier()` |  |
| 69 | 1 | `public int row()` |  |
| 70 | 1 | `public boolean isRetired()` |  |
| 71 | 1 | `public int grownUps()` |  |
| 72 | 1 | `public int size()` |  |
| 74 | 1 | `public String label()` |  |
| 75 | 1 | `public String key()` |  |
| 78 | 1 | `public double earningWeight()` | No wage: "No job." Nobody's income is split to an owner family. |
| 84 | 1 | `public boolean tradesShares()` | Not by the households' rules: the founding stakes are sold down by the line's own (OWNER_SELL_A_YEAR), and the line takes no offering. |
| 87 | 1 | `public boolean canInvest()` | They hold bonds, money abroad and the city's paper by the ordinary rules. |

