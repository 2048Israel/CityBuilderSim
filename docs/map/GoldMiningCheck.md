# GoldMiningCheck.java - 268 lines · 12 methods · 2 constants · harnesses

`ham/citybuildersim/GoldMiningCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

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

**Uses:** [Resource](Resource.md) (26), [Good](Good.md) (17), [Game](Game.md) (14), [GoldMining](GoldMining.md) (9), [ForeignAccounts](ForeignAccounts.md) (8), [Sectors](Sectors.md) (6), [Geology](Geology.md) (6), [Deposit](Deposit.md) (6), [BuildingManager](BuildingManager.md) (4), [GoldPrice](GoldPrice.md) (4), [GameVersion](GameVersion.md) (4), [Markets](Markets.md) (3), [Sector](Sector.md) (3), [MoneyAudit](MoneyAudit.md) (3), [GameFiles](GameFiles.md) (2), [LandManager](LandManager.md) (2), [NationalAccounts](NationalAccounts.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [GoodsMarket](GoodsMarket.md) (2), [DataSave](DataSave.md) (2), [CentralBank](CentralBank.md) (2), [Founding](Founding.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [WorldMoneys](WorldMoneys.md) (1), [SectorState](SectorState.md) (1), [CityLand](CityLand.md) (1)

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
| 22 | 247 | **type** `public final class GoldMiningCheck` | Gold Mining from geological fields through the mill to exports and the mint. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 5 | `static void close(String label, double actual, double expected)` |  |
| 38 | 5 | `static void quietly(Runnable work)` |  |
| 44 | 16 | `static Game town(String label)` |  |
| 61 | 19 | `static void registry()` |  |
| 81 | 14 | `static void row()` |  |
| 96 | 35 | `static void grades()` |  |
| 132 | 20 | `static Game exported()` |  |
| 153 | 33 | `static void minted(Game g)` |  |
| 187 | 48 | `static void saved(Game g) throws Exception` |  |
| 236 | 20 | `static void played(Game g)` |  |
| 257 | 11 | `public static void main(String[] args) throws Exception` |  |

