# PolicyCheck.java - 353 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/PolicyCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies.
> 
> THE ONE THING THIS HARNESS IS REALLY FOR
> 
> Every rate in the game moved from "one number" to "a city rate plus an
> offset", and every one of those changes is invisible when the offsets are
> zero - which is how they ship. A plumbing change that reproduces the old
> behaviour exactly is indistinguishable from a plumbing change that quietly
> broke and reproduced the old behaviour by accident, unless something moves the
> dials and checks the result. Section 1 pins the zero case; everything after it
> moves a dial.

**Uses:** [Sectors](Sectors.md) (40), [WageBand](WageBand.md) (17), [TaxPolicy](TaxPolicy.md) (12), [SalesTaxLedger](SalesTaxLedger.md) (8), [Game](Game.md) (6), [JobType](JobType.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [GameFiles](GameFiles.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [Sector](Sector.md) (2)

## Sections

| line | section |
|---:|---|
| 46 | · 1. zero offsets reproduce the single rate |
| 64 | · 2. offsets, and their clamps |
| 91 | · 3. the wage tax is banded, not averaged |
| 129 | · 4. the VAT taxes value added, once |
| 154 | · 5. exports are zero-rated, and can refund |
| 172 | · 6. imports carry the tax in, and out again |
| 203 | · 7. the subsidy floors a sector and stops the count |
| 265 | · 8. the money goes somewhere |
| 283 | · 9. it all survives a save |

## Fields (state)

| line | field | says |
|---:|---|---|
| 23 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 333 | **type** `public class PolicyCheck` | The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies. |
| 25 | 6 | `static void check(String label, double actual, double expected)` |  |
| 32 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 37 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 44 | 297 | `public static void main(String[] args) throws Exception` |  |
| 348 | 5 | `static double payOneSubsidy(Game city, Sector handler, double loss)` | Runs one subsidy payment against a stated loss. |

