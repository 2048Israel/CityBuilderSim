# SaveFileCheck.java - 1,530 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/SaveFileCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [GameFiles](GameFiles.md) (41), [Game](Game.md) (37), [Founding](Founding.md) (11), [OrderBook](OrderBook.md) (8), [Sectors](Sectors.md) (7), [Bank](Bank.md) (7), [DataSave](DataSave.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (5), [Sector](Sector.md) (5), [EconomyManager](EconomyManager.md) (5), [Household](Household.md) (5), [Currency](Currency.md) (2), [HistorySave](HistorySave.md) (2), [BuildingManager](BuildingManager.md) (2), [Construction](Construction.md) (2), [TaxPolicy](TaxPolicy.md) (2), [WageBand](WageBand.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [CorporateBond](CorporateBond.md) (2), [TreasuryJournal](TreasuryJournal.md) (2), [WorldEconomy](WorldEconomy.md) (1), [SaveHeader](SaveHeader.md) (1), [Good](Good.md) (1), [PopulationManager](PopulationManager.md) (1), [Retail](Retail.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [BondMarket](BondMarket.md) (1), [Exchange](Exchange.md) (1), [Equity](Equity.md) (1), [PriceIndex](PriceIndex.md) (1)

## Sections

| line | section |
|---:|---|
| 61 | · 1. where it goes |
| 109 | · 2. writing safely |
| 142 | · 3. a write that cannot possibly work |
| 169 | · 4. the old folder |
| 202 | · 5. it never overwrites a newer save |
| 237 | · 6. a real round trip |
| 311 | · 7. construction survives a save |
| 401 | · 7b. the warning survives a save |
| 479 | · 8. a save with nothing else built |
| 503 | · 9. the headline income does not move |
| 543 | · 10. the whole figure, to the cent |
| 689 | · 11. a city that is still MOVING |
| 793 | · 12. a city that OWES money |
| 881 | · 12b. ...AND ONE THAT OWES ABROAD (2026-09-21) |
| 931 | · 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY |
| 1443 | · 14. a reloaded city PLAYS ON as the one it was saved from |

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 1508 | **type** `public class SaveFileCheck` | Verifies where saves go and how they are written. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 44 | 9 | `static void same(String label, double actual, double expected)` | Two readings of one month, which must not differ at all. |
| 55 | 3 | `static String flat(Path p)` | Forward slashes, so the assertions read the same on any host. |
| 59 | 1455 | `public static void main(String[] args) throws Exception` |  |
| 1515 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 1523 | 7 | `static void cleanUp(Path root)` | Temp directory only - nothing here ever points at a real save folder. |

