# RestructureCheck.java - 523 lines · 5 methods · 1 constants · harnesses

`ham/citybuildersim/RestructureCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (14), [ShortTermTBill](ShortTermTBill.md) (12), [LongTermBond](LongTermBond.md) (10), [Debt](Debt.md) (6), [GameFiles](GameFiles.md) (6), [MediumTermBond](MediumTermBond.md) (3), [ForeignDebtCheck](ForeignDebtCheck.md) (1), [BuildingManager](BuildingManager.md) (1)

## Sections

| line | section |
|---:|---|
| 46 | · 1. the present value, against hand arithmetic |
| 90 | · 2. the edges |
| 108 | · 3. a real city buying real paper back |
| 152 | · 4. the credit story |
| 176 | · 5. THE ONE THAT MATTERS: is it a money pump? |
| 273 | · 5b. the serial bond, on its own terms |
| 345 | · 5c. the note knows its own term |
| 367 | · 5d. yield to maturity |
| 395 | · 6. and it survives a save |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 514 | `RestructureCheck.DOLLAR_ASK` | `5_000` | What the dollar round trip borrows: small against what its city earns, so the world's premium stays low. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 23 | `static int fails` |  |
| 24 | `static PrintStream out` |  |
| 25 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 503 | **type** `public class RestructureCheck` | Buying the city's own debt back, at what the paper is actually worth. |
| 30 | 6 | `static void check(String label, double actual, double expected, double tolerance)` |  |
| 37 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 42 | 454 | `public static void main(String[] args) throws Exception` |  |
| 498 | 14 | `static Game founded(Path root, String name) throws Exception` | A city with an economy, so the market has something to price against. |
| 516 | 7 | `static void cleanUp(Path root)` |  |

