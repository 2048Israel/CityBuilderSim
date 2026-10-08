# Founding.java - 481 lines · 37 methods · 16 constants · model

`ham/citybuildersim/Founding.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> How a city was founded: its name, its money's name, and the treasury and the vault the founders left it.
> 
> WHY THIS EXISTS (0.7.10). Every city used to be founded the same way - the
> Danzik dollar, D$2.5B in the treasury and US$1B in the vault - so none of it
> was a fact about a city: it was five static finals in Currency and two
> constants in Game, and a screen that wanted "the founders' dollars" read the
> constant. Jerus asked for a screen at the start of the game "where you choose
> the name of the city and the currency ... and perhaps the settings to choose
> the starting cash and usd cash and offworld inflation rate ... (with option
> to just start at default)". Once the choice exists the constant is only the
> DEFAULT, and a screen reading it would describe a city it is not looking at.
> 
> SO THE CHOICE IS A RECORD ON THE CITY. Set once, by the one founding path
> (Game's constructor, newGame(), and newGame() after a load - all through
> buildWorld()), saved with the city (DataSave: cityName, the five currency
> fields, foundingCash, foundingReserveUsd), and never changed afterwards. The
> world's inflation is chosen here too but not kept here: WorldEconomy already
> saves the mean a city grew up in, and a second copy would be a second place
> to disagree - so the record a loaded city carries reads it back from there.
> 
> AN OLD SAVE HAS NONE OF IT and loads as legacy(): the city Danzik, the
> Danzik dollar (Currency.DANZIK), D$2.5B and US$1B - how every city has been
> founded since 0.6.10 (2026-09-21), when the endowment was split between the
> treasury and the vault. Saves from before 0.6.10 were founded otherwise (the
> whole endowment in cash, no vault); nothing reads that far back - the
> founders' note shows only for Game.FOUNDERS_NOTE_MONTHS, and a city that
> old is past them.
> 
> WHAT IS NOT HERE: anything that is a figure about the city's life. The
> record says what the city started with; the treasury and the vault say what
> it has.
> 
> AND THE WORLD IT STANDS ON (0.7.56, batch J1a): the seed of the World the
> city is founded on - its coast, its lakes and river, the fields of ore and
> oil under it - saved as worldSeed. Founding.defaults() and every preset take
> DEFAULT_WORLD_SEED, so the harnesses and the playtest found on one world;
> the founding screen rolls a new one when it opens (rollWorldSeed()). A save
> from before 0.7.56 has none, and reads one made from what it does carry
> (derivedWorldSeed()), the same every time it is loaded until a save keeps
> it. The city's land stands on it since 0.7.57 (CityLand; the project's
> spec-land.md).

**Uses:** [Currency](Currency.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (7), [WorldEconomy](WorldEconomy.md) (3), [Game](Game.md) (2), [LandManager](LandManager.md) (1), [LandMarket](LandMarket.md) (1), [World](World.md) (1), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [TaxPolicy](TaxPolicy.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (55):** [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ConservationCheck](ConservationCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConversionCheck](ConversionCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DataSave](DataSave.md), [DenominationCheck](DenominationCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FoundingScreen](FoundingScreen.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GridCheck](GridCheck.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OutsideCheck](OutsideCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveHeader](SaveHeader.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SicknessCheck](SicknessCheck.md), [SkipReportCheck](SkipReportCheck.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WorldCheck](WorldCheck.md)

## Sections

| line | section |
|---:|---|
| 53 | THE PRESETS (0.7.10) |
| 130 | THE BOUNDS ON A CUSTOM FOUNDING |
| 180 | · the name |
| 188 | · the world |
| 225 | · the record |
| 306 | · is it a city |
| 358 | WHAT IT BUYS (0.7.10) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 100 | `Founding.Preset.INSANE` | Nothing in the treasury or the vault, and the founding ground owed abroad (0.7.14): the one preset under MIN_CASH, on purpose. |
| 101 | `Founding.Preset.LEAN` |  |
| 102 | `Founding.Preset.STANDARD` |  |
| 103 | `Founding.Preset.WEALTHY` |  |
| 104 | `Founding.Preset.CUSTOM` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `Founding.INSANE_LAND_COUPON` | `.03` | Insane's coupon on the land it owes for, a year: 3%, Jerus's number. |
| 78 | `Founding.INSANE_LAND_YEARS` | `20` | Insane's land bond's term, in years: twenty, Jerus's "20y" - one of the five term loans (LongTermBond.MATURITIES). |
| 86 | `Founding.LEAN_CASH` | `25_000` | Lean's treasury, in thousands: D$25M - two-thirds of the founding village at a new city's invoices (three-quarters until the builders' sales tax went into them, 0.7.19), so the city borrows from its first months, for ... |
| 89 | `Founding.LEAN_RESERVE_USD` | `10_000` | Lean's vault, in thousands of US dollars: US$10M - a few months to two years of a young city's imports. |
| 92 | `Founding.WEALTHY_CASH` | `2_500_000` | Wealthy's treasury, in thousands: D$2.5B - the start every city had from 0.6.10 to 0.7.9, which the playtest never drew below D$2.46B in thirty years. |
| 95 | `Founding.WEALTHY_RESERVE_USD` | `1_000_000` | Wealthy's vault, in thousands of US dollars: US$1B - the old start's, which the playtest's defence took sixty-odd years to spend half of (month 739, the median of eight seeds). |
| 169 | `Founding.MIN_CASH` | `6_000` | The least a city may be founded with in its treasury, in thousands: D$6M, ten houses and a shop at a new city's invoices - D$5.37M since the builders' sales tax went into them (0.7.19; it was D$5M, over D$4.56M). |
| 172 | `Founding.MAX_CASH` | `10_000_000` | The most, in thousands: D$10B, four times the Wealthy start. |
| 175 | `Founding.MIN_RESERVE_USD` | `0` | The least in the vault, in thousands of US dollars: none, as every city had before 0.6.10. |
| 178 | `Founding.MAX_RESERVE_USD` | `4_000_000` | The most, in thousands of US dollars: US$4B, four times the Wealthy start. |
| 183 | `Founding.DEFAULT_CITY_NAME` | `"Danzik"` | The city a founding is named when nobody names it - Jerus's first city, and every save's before 0.7.10. |
| 186 | `Founding.MAX_CITY_NAME_LENGTH` | `24` | The longest city name: room for the window's title and the slot list's line, not a policy. |
| 191 | `Founding.DEFAULT_WORLD_SEED` | `4127` | The world a founding stands on when nobody rolls another: 4127, the map mockup's default seed (city-map.html), so every harness and the playtest found on the same ground. |
| 194 | `Founding.ROLLED_SEED_MAX` | `999_999_999` | The largest seed the founding screen's dice rolls: 999,999,999, nine digits a player can read back and type. |
| 388 | `Founding.VILLAGE` | `{ { "House", "60" }, { "Convenience Store", "5" }, { "Mixed Farm", "2" }, { "...` | The founding village: the playtest's hand-built settlement, name and count. |
| 392 | `Founding.FIRST_WORKS` | `{ "Wind Farm", "Water Treatment Plant", "Elementary School", "Police Station" }` | The first big works, in the order a young city tends to need them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 106 | `private final String label` |  |
| 107 | `private final double cash, reserveUsd` |  |
| 227 | `private final String cityName` |  |
| 228 | `private final Currency currency` |  |
| 229 | `private final double cash` |  |
| 230 | `private final double reserveUsd` |  |
| 231 | `private final double meanInflation` |  |
| 232 | `private final long worldSeed` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 431 | **type** `public final class Founding` | How a city was founded: its name, its money's name, and the treasury and the vault the founders left it. |

### THE PRESETS (0.7.10) (lines 53-129)

| line | len | member | says |
|---:|---:|---|---|
| 81 | 3 | `public static double landBondUsd()` | What an Insane city owes for its founding ground, in thousands of US dollars: every starting square foot at the land market's opening dollar price - STARTING_SQ_FT, the figure its centre is drawn to hold, though since... |
| 98 | 31 | **type** `public enum Preset` | The five choices of money, hardest first; CUSTOM carries no figures of its own. |
| 109 | 5 | `Preset(String label, double cash, double reserveUsd)` _(in Founding.Preset)_ |  |
| 115 | 1 | `public String label()` _(in Founding.Preset)_ |  |
| 117 | 1 | `public double cash()` _(in Founding.Preset)_ | The treasury, in thousands; NaN for CUSTOM. |
| 119 | 1 | `public double reserveUsd()` _(in Founding.Preset)_ | The vault, in thousands of US dollars; NaN for CUSTOM. |
| 122 | 6 | `public static Preset of(double cash, double reserveUsd)` _(in Founding.Preset)_ | The preset these two figures are, or CUSTOM. |

### THE BOUNDS ON A CUSTOM FOUNDING (lines 130-179)

### the name (lines 180-187)

### the world (lines 188-224)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 3 | `public static long rollWorldSeed()` | A seed for the founding screen's dice, 1 to ROLLED_SEED_MAX. |
| 202 | 9 | `public static Long parseWorldSeed(String typed)` | A typed seed as a whole number - digits, a leading minus, commas ignored - or null when it is not one. |
| 219 | 5 | `public static long derivedWorldSeed(String cityName, double foundingCash, double landOwnedSqFt, int month)` | The seed a save from before 0.7.56 is read with (spec-land 2.9): SplitMix64 of its city's name's hash, the bits of the treasury it was founded with, the bits of the ground it owns and the month, XORed - what the save ... |

### the record (lines 225-305)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 9 | `public Founding(String cityName, Currency currency, double cash, double reserveUsd, double meanInflation, long worldSeed)` | A founding exactly as given. |
| 255 | 3 | `public Founding(String cityName, Currency currency, double cash, double reserveUsd, double meanInflation)` | ...on the default world, DEFAULT_WORLD_SEED. |
| 260 | 3 | `public static Founding defaults()` | The defaults: Danzik, its money named after it, the Standard preset, the default world - what "Found with defaults" founded until 0.7.21. |
| 265 | 5 | `public static Founding named(String cityName, Preset preset, double meanInflation)` | A city with its money named after it, on one of the four presets with figures. |
| 272 | 3 | `public static Founding custom(String cityName, double cash, double reserveUsd, double meanInflation)` | A city with its money named after it, on figures of the player's own. |
| 277 | 3 | `public Founding withCurrency(Currency typed)` | The same founding with money the player named by hand - Currency.typed(), which is null when the typed pair does not pass, and problem() then says so. |
| 282 | 3 | `public Founding withWorldSeed(long seed)` | The same founding on another world: the founding screen's World field and its dice (0.7.56). |
| 287 | 3 | `public static Founding legacy(double meanInflation)` | What a save from before 0.7.10 was founded with. |
| 291 | 1 | `private static String clean(String name)` |  |
| 293 | 1 | `public String getCityName()` |  |
| 294 | 1 | `public Currency getCurrency()` |  |
| 296 | 1 | `public double getCash()` | The treasury it was founded with, in thousands. |
| 298 | 1 | `public double getReserveUsd()` | The vault it was founded with, in thousands of US dollars - bought on day one at the opening rate. |
| 300 | 1 | `public double getMeanInflation()` | The world's average inflation it was founded into. |
| 302 | 1 | `public Preset getPreset()` | Which of the five this is. |
| 304 | 1 | `public long getWorldSeed()` | The seed of the world it stands on (0.7.56): World.of(getWorldSeed()) is its ground. |

### is it a city (lines 306-357)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 8 | `public static String cityNameProblem(String name)` | Why a city name will not do, in the player's words, or null when it will. |
| 319 | 6 | `public static String cashProblem(double cash)` | Why a treasury will not do, or null. |
| 327 | 7 | `public static String reserveProblem(double reserveUsd)` | Why a vault will not do, or null. |
| 340 | 17 | `public String problem()` | Why this founding cannot found a city, or null when it can: the first of the city's name, its money (a typed pair that did not pass leaves no currency - the screen says which half), the two amounts and the world. |

### WHAT IT BUYS (0.7.10) (lines 358-481)

| line | len | member | says |
|---:|---:|---|---|
| 396 | 1 | **type** `public record Work(String name, double cost, boolean fitsInCash, double bondNeeded)` | One of the first works: what it is invoiced at on a new city, and whether the treasury pays it in cash after the village or needs a bond for the rest. |
| 402 | 17 | **type** `public record Buys(double village, double leftAfterVillage, List<Work> works, double reserveUsd, String vau...` | What a founding buys: the village, what is left, each first work taken on its own after the village, and the vault in words. |
| 406 | 5 | `public List<Work> inCash()` _(in Founding.Buys)_ | The works the treasury pays for in cash after the village, each on its own. |
| 413 | 5 | `public List<Work> onABond()` _(in Founding.Buys)_ | The ones it needs a bond for. |
| 421 | 3 | `public static double foundingMaterialPrice()` | What one unit of building material costs a new city abroad: the world's price at the opening rate, the world's level at one. |
| 426 | 3 | `public static double foundingBuildersRate()` | The builders' sales tax on a new city: what a fresh policy charges them (0.7.19; Game, THE BUILDERS' PRICE). |
| 437 | 5 | `public static double orderCost(BuildingsTemplate t, int quantity, double yard)` | An order's invoice on a new city with `yard` units of material in the yard: its cash cost - no wage has been paid yet, so its labour is at the founding ladder - and whatever material the yard does not hold at founding... |
| 444 | 20 | `public static Buys whatItBuys(List<BuildingsTemplate> catalogue, double cash, double reserveUsd)` | What these two figures buy, over this catalogue. |
| 466 | 10 | `public static String vaultSays(double reserveUsd)` | The vault, in words: what it is for. |
| 477 | 4 | `private static BuildingsTemplate find(List<BuildingsTemplate> catalogue, String name)` |  |

