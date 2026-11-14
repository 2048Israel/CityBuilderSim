# GoldMiningCheck.java - 212 lines · 12 methods · 2 constants · harnesses

`ham/citybuildersim/GoldMiningCheck.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> Gold Mining from geological fields through the mill to exports and the mint.
> 
> WHY (GD4, spec-gold-money 4.4). The mint creates domestic reserves, so a
> test must cause a mint purchase and close the money audit in a played city.
> 
> WHAT THIS HAS TO PROVE
> 1. Appended resource, good and sector, geology weights and units.
> 2. The permanent building row, inputs, site and historical availability.
> 3. Reproducible grades by geology and depletion at fixed ore throughput.
> 4. A mill sells abroad, buys chemicals and depletes exactly its output.
> 5. Under Gold the mint receives ounces and issues M into the miner's books.
> 6. Depletion, vault and mint issuance survive their saved state round trips.
> 7. A real city's months exercise the mechanic with a closing money audit.

**Uses:** [Resource](Resource.md) (24), [Good](Good.md) (12), [Game](Game.md) (10), [GoldMining](GoldMining.md) (7), [ForeignAccounts](ForeignAccounts.md) (7), [Sectors](Sectors.md) (6), [Geology](Geology.md) (6), [Deposit](Deposit.md) (6), [BuildingManager](BuildingManager.md) (4), [GameVersion](GameVersion.md) (4), [Sector](Sector.md) (3), [LandManager](LandManager.md) (2), [GoldPrice](GoldPrice.md) (2), [MoneyAudit](MoneyAudit.md) (2), [CentralBank](CentralBank.md) (2), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [Markets](Markets.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [GoodsMarket](GoodsMarket.md) (1), [WorldMoneys](WorldMoneys.md) (1), [SectorState](SectorState.md) (1), [CityLand](CityLand.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 24 | `GoldMiningCheck.out` | `System.out` |  |
| 25 | `GoldMiningCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 23 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 191 | **type** `public final class GoldMiningCheck` | Gold Mining from geological fields through the mill to exports and the mint. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 5 | `static void close(String label, double actual, double expected)` |  |
| 38 | 5 | `static void quietly(Runnable work)` |  |
| 44 | 16 | `static Game town(String label)` |  |
| 61 | 17 | `static void registry()` |  |
| 79 | 14 | `static void row()` |  |
| 94 | 28 | `static void grades()` |  |
| 123 | 20 | `static Game exported()` |  |
| 144 | 25 | `static void minted(Game g)` |  |
| 170 | 17 | `static void saved(Game g)` |  |
| 188 | 12 | `static void played(Game g)` |  |
| 201 | 11 | `public static void main(String[] args)` |  |

