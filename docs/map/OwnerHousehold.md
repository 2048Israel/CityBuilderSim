# OwnerHousehold.java - 117 lines · 12 methods · 6 constants · model

`ham/citybuildersim/OwnerHousehold.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

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
> (FamilyModel.rebuild) - at most OWNER_ADULT_SHARE of the city's adults
> (Game.ownerAdultsWanted()).
> 
> WHY THE SHARE (BO1-S, 2026-11-20). Two adults a company, whatever the town's
> size, took a founding town's whole labour force: the supply takes the owners
> off every band (PopulationManager.workforceByBand), so a 1900 farm town had
> no builders at all, a held town's Home Care was unbuilt in 120 months (53
> with the line off), and a Medellin city owned a twelfth of its ground at
> month 2,000. BO1's "a family a company" holds now only in a city of
> 2 / OWNER_ADULT_SHARE adults a company (200 for five): under it the cell holds
> fewer families than companies, and under 1 / OWNER_ADULT_SHARE adults (20) it
> is empty, so the founders' grant waits, as it always did for an empty cell,
> and the company lists once the town has grown to its family.

**Uses:** [Household](Household.md) (1), [PayTier](PayTier.md) (1)

**Used by (6):** [Exchange](Exchange.md), [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [OwnerCheck](OwnerCheck.md), [PeopleScreen](PeopleScreen.md), [ShortSellingCheck](ShortSellingCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `OwnerHousehold.OWNER_SELL_A_YEAR` | `.05` | The share of each founding stake the line offers on the exchange in a year: a stake falls to about a fifth in 30 years (.95^30 = .21). |
| 56 | `OwnerHousehold.OWNER_SELL_A_MONTH` | `1 - Math.pow(1 - OWNER_SELL_A_YEAR, 1.0 / 12)` | A twelfth of the year's sale, compounded: a month's share of each holding the line offers on the exchange, so twelve months leave 1 - OWNER_SELL_A_YEAR of it. |
| 59 | `OwnerHousehold.KEY` | `"OWNER"` | The line's key: the cell's name on the books, the save and the exchange's orders. |
| 66 | `OwnerHousehold.OWNER_BROKE_MONTHS` | `12` | Months a company's fair value sits at the exchange's floor before its family gives up and joins the regular pool: the window Migration.DECLINE_MONTHS gives a tier. |
| 69 | `OwnerHousehold.OWNER_ADULT_SHARE` | `.05` | The most of a city's adults the owner families may be: two adults a company took a founding town's whole labour force (BO1-S). |
| 91 | `OwnerHousehold.IN_CITIES` | `true` | Whether a city runs the owner line: on since BO1-S. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 43 | 75 | **type** `public class OwnerHousehold extends Household` | The owner families, as one ledger: one two-adult household per company that has listed and not gone broke. |
| 77 | 3 | `public static double adultsOf(int families, double adults)` | The owner families' adults in a city of these adults with this many companies' families (reserved ones counted): two a family, at most OWNER_ADULT_SHARE of the adults. |
| 93 | 3 | `public OwnerHousehold()` |  |
| 97 | 1 | `public PayTier tier()` |  |
| 98 | 1 | `public int row()` |  |
| 99 | 1 | `public boolean isRetired()` |  |
| 100 | 1 | `public int grownUps()` |  |
| 101 | 1 | `public int size()` |  |
| 103 | 1 | `public String label()` |  |
| 104 | 1 | `public String key()` |  |
| 107 | 1 | `public double earningWeight()` | No wage: "No job." Nobody's income is split to an owner family. |
| 113 | 1 | `public boolean tradesShares()` | Not by the households' rules: the founding stakes are sold down by the line's own (OWNER_SELL_A_YEAR), and the line takes no offering. |
| 116 | 1 | `public boolean canInvest()` | They hold bonds, money abroad and the city's paper by the ordinary rules. |

