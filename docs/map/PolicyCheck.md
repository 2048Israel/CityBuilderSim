# PolicyCheck.java - 475 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/PolicyCheck.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sectors](Sectors.md) (57), [TaxPolicy](TaxPolicy.md) (34), [WageBand](WageBand.md) (21), [Game](Game.md) (8), [SalesTaxLedger](SalesTaxLedger.md) (8), [JobType](JobType.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [GameFiles](GameFiles.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [Sector](Sector.md) (2)

## Sections

| line | section |
|---:|---|
| 46 | · 1. zero offsets reproduce the single rate |
| 64 | · 2. offsets, and their clamps |
| 114 | · 3. the wage tax is banded, not averaged |
| 152 | · 4. the VAT taxes value added, once |
| 177 | · 5. exports are zero-rated, and can refund |
| 195 | · 6. imports carry the tax in, and out again |
| 226 | · 7. the subsidy floors a sector and stops the count |
| 288 | · 8. the money goes somewhere |
| 306 | · 9. it all survives a save |
| 398 | · 10. three bases, one per tax (0.7.4) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 23 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 455 | **type** `public class PolicyCheck` | The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies. |
| 25 | 6 | `static void check(String label, double actual, double expected)` |  |
| 32 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 37 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 44 | 419 | `public static void main(String[] args) throws Exception` |  |
| 470 | 5 | `static double payOneSubsidy(Game city, Sector handler, double loss)` | Runs one subsidy payment against a stated loss. |

