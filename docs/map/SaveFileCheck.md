# SaveFileCheck.java - 2,662 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/SaveFileCheck.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (83), [GameFiles](GameFiles.md) (47), [DataSave](DataSave.md) (19), [Founding](Founding.md) (18), [Bank](Bank.md) (13), [CareType](CareType.md) (12), [Sectors](Sectors.md) (11), [GameVersion](GameVersion.md) (11), [Oil](Oil.md) (10), [OrderBook](OrderBook.md) (10), [Equity](Equity.md) (9), [TreasuryFund](TreasuryFund.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (8), [Sector](Sector.md) (8), [Household](Household.md) (8), [MoneyAudit](MoneyAudit.md) (8), [Ports](Ports.md) (7), [Migration](Migration.md) (6), [LandManager](LandManager.md) (5), [Resource](Resource.md) (5), [EconomyManager](EconomyManager.md) (5), [FundLedger](FundLedger.md) (5), [HistorySave](HistorySave.md) (4), [WageBand](WageBand.md) (4), [Exchange](Exchange.md) (4), [PopulationCohorts](PopulationCohorts.md) (4), [PriceIndex](PriceIndex.md) (4), [CityLand](CityLand.md) (3), [SaveHeader](SaveHeader.md) (3), [BuildingManager](BuildingManager.md) (3)... and 27 more

## Sections

| line | section |
|---:|---|
| 61 | · 1. where it goes |
| 109 | · 2. writing safely |
| 142 | · 3. a write that cannot possibly work |
| 169 | · 4. the old folder |
| 202 | · 5. it never overwrites a newer save |
| 237 | · 6. a real round trip |
| 405 | · 7. construction survives a save |
| 502 | · 7b. the warning survives a save |
| 580 | · 8. a save with nothing else built |
| 604 | · 8b. a stack emptied keeps its place (0.7.103) |
| 634 | · 9. the headline income does not move |
| 674 | · 10. the whole figure, to the cent |
| 853 | · 11. a city that is still MOVING |
| 957 | · 12. a city that OWES money |
| 1045 | · 12b. ...AND ONE THAT OWES ABROAD (2026-09-21) |
| 1095 | · 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY |
| 1971 | · 14. a reloaded city PLAYS ON as the one it was saved from |
| 2272 | · the city's fund, its rescue setting and the bank's preferred (0.7.14) |
| 2527 | · the oldest format this build loads (0.7.108) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 2640 | **type** `public class SaveFileCheck` | Verifies where saves go and how they are written. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 44 | 9 | `static void same(String label, double actual, double expected)` | Two readings of one month, which must not differ at all. |
| 55 | 3 | `static String flat(Path p)` | Forward slashes, so the assertions read the same on any host. |
| 59 | 2576 | `public static void main(String[] args) throws Exception` |  |
| 2636 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 2644 | 9 | `static java.util.Map<String, byte[]> folderBytes(Path dir) throws IOException` | Every file under a folder and its bytes, by its path within it - to say nothing in it moved. |
| 2655 | 7 | `static void cleanUp(Path root)` | Temp directory only - nothing here ever points at a real save folder. |

