# RestructureCheck.java - 418 lines · 5 methods · 2 constants · harnesses

`ham/citybuildersim/RestructureCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> Buying the city's own debt back, at what the paper is actually worth.
> 
> The arithmetic is textbook and the risk is not in the arithmetic. It is that
> a buyback priced off the same market the city moves by borrowing might be a
> MONEY PUMP: issue, buy back, pocket the difference, repeat. Section 5 is the
> reason this file exists, and it is written to try to break the feature rather
> than to demonstrate it.
> 
> The rest is the property that makes the button worth having at all: market
> value is not face value, and which side of face it lands on says something
> true about the city's credit.
> 
> THE DOLLAR CITY (FS13, 2026-11-20): section 5's dollar case is ForeignDebtCheck's trading city, played 60 months, handed the exports the
> world prices it on (ForeignDebtCheck.earnsAbroad(), sized to DOLLAR_ASK) and its dial held over the world's quote for the ask, so the
> world lends it for less than it lends itself by cause and not by what its depots earned.

**Uses:** [PriceLevel](PriceLevel.md) (22), [Game](Game.md) (13), [LongTermBond](LongTermBond.md) (8), [GameFiles](GameFiles.md) (6), [Debt](Debt.md) (5), [ShortTermTBill](ShortTermTBill.md) (4), [TempDirs](TempDirs.md) (3), [ForeignDebtCheck](ForeignDebtCheck.md) (2), [DebtManager](DebtManager.md) (1), [FloatLinkCheck](FloatLinkCheck.md) (1), [MediumTermBond](MediumTermBond.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (2):** [BuybackCheck](BuybackCheck.md), [CurveCheck](CurveCheck.md)

## Sections

| line | section |
|---:|---|
| 50 | · 1. the present value, against hand arithmetic |
| 94 | · 2. the edges |
| 112 | · 3. a real city buying real paper back |
| 156 | · 4. the credit story |
| 180 | · 5. THE ONE THAT MATTERS: is it a money pump? |
| 287 | · 6. and it survives a save |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 406 | `RestructureCheck.DOLLAR_ASK` | `5_000 * PriceLevel.FACTOR` | What the dollar round trip borrows: small against what its city earns, so the world's premium stays low. |
| 409 | `RestructureCheck.DOLLAR_STRESS_SHARE` | `1.0 / 16` | The share of a full solvency stress (DebtManager.FULL_STRESS_EXPORT_YEARS of exports owed) the dollar ask is sized to: a sixteenth, a premium of a point or two. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 27 | `static int fails` |  |
| 28 | `static PrintStream out` |  |
| 29 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 394 | **type** `public class RestructureCheck` | Buying the city's own debt back, at what the paper is actually worth. |
| 34 | 6 | `static void check(String label, double actual, double expected, double tolerance)` |  |
| 41 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 46 | 342 | `public static void main(String[] args) throws Exception` |  |
| 390 | 14 | `static Game founded(Path root, String name) throws Exception` | A city with an economy, so the market has something to price against. |
| 411 | 7 | `static void cleanUp(Path root)` |  |

