# SaveFileCheck.java - 2,460 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/SaveFileCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> Verifies where saves go and how they are written.
> 
> This is the only part of the game where a bug destroys something the player
> cannot get back. Everything else can be re-simulated; a save written over
> badly is a city that no longer exists. So the tests here are deliberately
> mean: they fill the target with junk, point the writer at a path it cannot
> write to, and check that in every case the file that was already on disk is
> still exactly what it was.
> 
> Path resolution is tested through the pure resolveDirectory() rather than the
> real environment, because a chooser that reads System.getenv can only be
> tested on the machine it is running on - which is precisely the machine where
> a mistake in the other two branches would never show up.

**Uses:** [Game](Game.md) (73), [GameFiles](GameFiles.md) (44), [DataSave](DataSave.md) (18), [Founding](Founding.md) (18), [Bank](Bank.md) (13), [CareType](CareType.md) (12), [Sectors](Sectors.md) (11), [Oil](Oil.md) (10), [OrderBook](OrderBook.md) (10), [Equity](Equity.md) (9), [TreasuryFund](TreasuryFund.md) (9), [Sector](Sector.md) (8), [Household](Household.md) (8), [MoneyAudit](MoneyAudit.md) (8), [Ports](Ports.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [Migration](Migration.md) (6), [EconomyManager](EconomyManager.md) (5), [FundLedger](FundLedger.md) (5), [HistorySave](HistorySave.md) (4), [WageBand](WageBand.md) (4), [Exchange](Exchange.md) (4), [PopulationCohorts](PopulationCohorts.md) (4), [PriceIndex](PriceIndex.md) (4), [LandManager](LandManager.md) (3), [CityLand](CityLand.md) (3), [Good](Good.md) (3), [StrategicReserve](StrategicReserve.md) (3), [RefineryFlow](RefineryFlow.md) (3), [HouseholdBalance](HouseholdBalance.md) (3)... and 23 more

## Sections

| line | section |
|---:|---|
| 61 | · 1. where it goes |
| 109 | · 2. writing safely |
| 142 | · 3. a write that cannot possibly work |
| 169 | · 4. the old folder |
| 202 | · 5. it never overwrites a newer save |
| 237 | · 6. a real round trip |
| 389 | · 7. construction survives a save |
| 486 | · 7b. the warning survives a save |
| 564 | · 8. a save with nothing else built |
| 588 | · 9. the headline income does not move |
| 628 | · 10. the whole figure, to the cent |
| 807 | · 11. a city that is still MOVING |
| 911 | · 12. a city that OWES money |
| 999 | · 12b. ...AND ONE THAT OWES ABROAD (2026-09-21) |
| 1049 | · 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY |
| 1883 | · 14. a reloaded city PLAYS ON as the one it was saved from |
| 2184 | · the city's fund, its rescue setting and the bank's preferred (0.7.14) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 2438 | **type** `public class SaveFileCheck` | Verifies where saves go and how they are written. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 44 | 9 | `static void same(String label, double actual, double expected)` | Two readings of one month, which must not differ at all. |
| 55 | 3 | `static String flat(Path p)` | Forward slashes, so the assertions read the same on any host. |
| 59 | 2385 | `public static void main(String[] args) throws Exception` |  |
| 2445 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 2453 | 7 | `static void cleanUp(Path root)` | Temp directory only - nothing here ever points at a real save folder. |

