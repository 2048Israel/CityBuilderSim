# GroceryCheck.java - 460 lines · 10 methods · 0 constants · harnesses

`ham/citybuildersim/GroceryCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> Groceries at a price, the shelf cleared stickily, and food assistance
> (0.7.43): the households' demand curve, the clearing price, who is handed
> the baskets a shortage leaves, the shelf's step toward its target, and the
> treasury's voucher, paid to the cent.
> 
> WHY. Until 0.7.43 the grocer was handed `want` in money - subsistence and
> most of what was left, the wealth term with it - so the city asked for
> fourteen to seventeen times the baskets it could eat, and the shelf's price
> answered a head count capped by the shops' coverage: no price in the index
> read anybody's money. The project's spec-inflation.md section 2.6-2.9 is
> the design; HouseholdBalance (GROCERIES AT A PRICE, AND FOOD ASSISTANCE),
> Retail.sellOwnPriced() and Retail.repriceShelf() are the mechanics.
> 
> WHAT THIS HAS TO PROVE
> 
>   1. A household asks for a basket a head at most, however rich.
>   2. All of it up to the satiation price, and above it GROCERY_ELASTICITY
>      less per unit of the price, in logs.
>   3. Never more than its money buys - and the city's demand falls in the
>      price, so there is a price that clears.
>   4. The clearing price is the one at which no more is wanted than the shops
>      can hand over, found inside its band.
>   5. A shortage is shared out at the clearing price, so the poorest are
>      priced out first, every basket paid for at the price charged, and the
>      cells add up to what was sold.
>   6. The shelf aims at the clearing price between its floor and CLEARING_CAP
>      over it, and moves CLEAR_SPEED of the way there a month in logs,
>      drifting with expected inflation.
>   7. A shelf under its floor closes FLOOR_CATCH_UP of the gap to the floor
>      grown a month.
>   8. Food assistance: a voucher of the dial's share of the baskets at the
>      price, for a household whose baskets would take more than
>      FOOD_ASSISTANCE_MEANS_SHARE of its means, paid only on the baskets got,
>      into its savings - and the poor are handed more of a shortage for it.
>      The means count the year's investment income, smoothed (0.7.45), so
>      a single month's coupon does not take the voucher away.
>   9. In a city: what the vouchers paid is what the treasury paid, on the
>      budget's line and on the audit's, to the cent, every month, with the
>      audit closing - and the Food tab's reads (0.7.45): the dial's preview
>      at rest is what the month paid, the rows add up to the sale, the
>      priced out and the short of stock to the hunger, and THE BUDGET
>      previews the dial by the vouchers it would have paid.

**Uses:** [Retail](Retail.md) (20), [HouseholdBalance](HouseholdBalance.md) (16), [PolicyPreview](PolicyPreview.md) (15), [Household](Household.md) (14), [Game](Game.md) (8), [FamilyStructure](FamilyStructure.md) (7), [PayTier](PayTier.md) (7), [BondCheck](BondCheck.md) (2), [TreasuryLine](TreasuryLine.md) (2), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [LongPlaytest](LongPlaytest.md) (1), [PriceIndex](PriceIndex.md) (1), [MoneyAudit](MoneyAudit.md) (1), [TaxPolicy](TaxPolicy.md) (1)

## Sections

| line | section |
|---:|---|
| 87 | · 1-3. demand at a price |
| 116 | · 4. the clearing price |
| 131 | · 5. the shortage, shared out |
| 149 | · 6-7. the shelf |
| 179 | · 8. food assistance, on the bench |
| 255 | 9. IN A CITY: THE TREASURY PAYS, TO THE CENT |

## Fields (state)

| line | field | says |
|---:|---|---|
| 54 | `static int fails` |  |
| 55 | `static PrintStream out` |  |
| 56 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 409 | **type** `public class GroceryCheck` | Groceries at a price, the shelf cleared stickily, and food assistance (0.7.43): the households' demand curve, the clearing price, who is handed the baskets a shortage leaves, the shelf's step toward its target, and th... |
| 58 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 63 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 70 | 8 | `static Household cell(HouseholdBalance hb, FamilyStructure shape, PayTier tier, double households, double foodMoney)` | A bench of the household ledger: one cell of each kind the sections need, by hand. |
| 79 | 146 | `public static void main(String[] args) throws Exception` |  |
| 232 | 10 | `static double aidedWithIncome(double pSat, double floor)` | A household whose investment income lifts its means past twice its baskets holds no voucher. |
| 244 | 10 | `static double aidedWithACoupon(double pSat, double floor)` | ...and one that had the same income in one month only - a coupon - which the year's average holds a twelfth of: still a voucher. |

### 9. IN A CITY: THE TREASURY PAYS, TO THE CENT (lines 255-460)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 19 | `static Game founding(Path root, String label)` |  |
| 289 | 9 | `static boolean underTheCap(Game g, double share)` | Whether somebody's voucher at this dial is above nothing and under what their baskets cost at the last sale - the voucher's cap (0.7.62). |
| 299 | 156 | `static void inACity(Path root)` |  |
| 456 | 4 | `static Household byKey(HouseholdBalance hb, String key)` |  |

