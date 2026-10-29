# Founding.java - 589 lines · 52 methods · 16 constants · model

`ham/citybuildersim/Founding.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

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
> 
> ...OR THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 2 and
> 6): a founding's world is its key (World.Key, since 0.7.115) - generated
> from its seed, or the real Earth round a spot, its latitude and longitude,
> drawn from the data the key names (EarthData.EARTH_2 for the game's), the
> seed deciding the detail below the data and the fields. The founding
> screen offers Earth when the data is here (EarthSite.gameData()); saved as
> DataSave's worldKind, earthLat, earthLon and earthData (SAVE_FORMAT 38). A
> spot the founding card refuses - no ground a building may stand on within
> 5 km (the sea, an island too small, or slopes too steep: ET2), or ice - is a
> problem() like a bad name.
> 
> AND THE PATH OF THE WORLD BEYOND THE CITY (WN1; spec-nations 7): Historical
> or Random, and the seed that draws it, picked here once and saved as
> worldPath and pathSeed (DataSave). The path's seed is not the ground's
> worldSeed: the ground is the city's land, the path the nations' years.
> Every constructor and factory founds on Nations.DEFAULT_PATH and
> Nations.DEFAULT_SEED, so the harnesses and the playtest meet one world, and
> a save without them reads the same. Nothing in the economy reads them yet
> ... (1 more lines in the source)

**Uses:** [Currency](Currency.md) (11), [World](World.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (7), [Nations](Nations.md) (6), [CityLand](CityLand.md) (3), [LandManager](LandManager.md) (3), [WorldEconomy](WorldEconomy.md) (3), [LandMarket](LandMarket.md) (2), [Game](Game.md) (2), [EarthData](EarthData.md) (1), [EarthSite](EarthSite.md) (1), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [TaxPolicy](TaxPolicy.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (100):** [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BridgeCheck](BridgeCheck.md), [BridgeMapCheck](BridgeMapCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConversionCheck](ConversionCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [DataSave](DataSave.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DenominationCheck](DenominationCheck.md), [DeskCheck](DeskCheck.md), [EarthDepositsCheck](EarthDepositsCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [ForestryCheck](ForestryCheck.md), [FoundingScreen](FoundingScreen.md), [FoundingYardCheck](FoundingYardCheck.md), [FreightViewCheck](FreightViewCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GatewayCheck](GatewayCheck.md), [GdpCheck](GdpCheck.md), [GridCheck](GridCheck.md), [GroceryCheck](GroceryCheck.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HydroCheck](HydroCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [IslandNetCheck](IslandNetCheck.md), [JunctionCheck](JunctionCheck.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandlordPortCheck](LandlordPortCheck.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OilViewCheck](OilViewCheck.md), [OutsideCheck](OutsideCheck.md), [PassThroughCheck](PassThroughCheck.md), [PlanCheck](PlanCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PortCheck](PortCheck.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryViewCheck](RefineryViewCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveHeader](SaveHeader.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SicknessCheck](SicknessCheck.md), [SkipReportCheck](SkipReportCheck.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeBandCheck](TradeBandCheck.md), [TreasuryCheck](TreasuryCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WellCheck](WellCheck.md), [WorldCheck](WorldCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 74 | THE PRESETS (0.7.10) |
| 174 | THE BOUNDS ON A CUSTOM FOUNDING |
| 224 | · the name |
| 232 | · the world |
| 269 | · the record |
| 410 | · is it a city |
| 466 | WHAT IT BUYS (0.7.10) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 144 | `Founding.Preset.INSANE` | Nothing in the treasury or the vault, and the founding ground owed abroad (0.7.14): the one preset under MIN_CASH, on purpose. |
| 145 | `Founding.Preset.LEAN` |  |
| 146 | `Founding.Preset.STANDARD` |  |
| 147 | `Founding.Preset.WEALTHY` |  |
| 148 | `Founding.Preset.CUSTOM` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 107 | `Founding.INSANE_LAND_COUPON` | `.03` | Insane's coupon on the land it owes for, a year: 3%, Jerus's number. |
| 110 | `Founding.INSANE_LAND_YEARS` | `20` | Insane's land bond's term, in years: twenty, Jerus's "20y" - one of the six bond maturities (LongTermBond.MATURITIES). |
| 130 | `Founding.LEAN_CASH` | `25_000` | Lean's treasury, in thousands: D$25M - two-thirds of the founding village at a new city's invoices (three-quarters until the builders' sales tax went into them, 0.7.19), so the city borrows from its first months, for ... |
| 133 | `Founding.LEAN_RESERVE_USD` | `10_000` | Lean's vault, in thousands of US dollars: US$10M - a few months to two years of a young city's imports. |
| 136 | `Founding.WEALTHY_CASH` | `2_500_000` | Wealthy's treasury, in thousands: D$2.5B - the start every city had from 0.6.10 to 0.7.9, which the playtest never drew below D$2.46B in thirty years. |
| 139 | `Founding.WEALTHY_RESERVE_USD` | `1_000_000` | Wealthy's vault, in thousands of US dollars: US$1B - the old start's, which the playtest's defence took sixty-odd years to spend half of (month 739, the median of eight seeds). |
| 213 | `Founding.MIN_CASH` | `6_000` | The least a city may be founded with in its treasury, in thousands: D$6M, ten houses and a shop at a new city's invoices - D$5.37M since the builders' sales tax went into them (0.7.19; it was D$5M, over D$4.56M). |
| 216 | `Founding.MAX_CASH` | `10_000_000` | The most, in thousands: D$10B, four times the Wealthy start. |
| 219 | `Founding.MIN_RESERVE_USD` | `0` | The least in the vault, in thousands of US dollars: none, as every city had before 0.6.10. |
| 222 | `Founding.MAX_RESERVE_USD` | `4_000_000` | The most, in thousands of US dollars: US$4B, four times the Wealthy start. |
| 227 | `Founding.DEFAULT_CITY_NAME` | `"Danzik"` | The city a founding is named when nobody names it - Jerus's first city, and every save's before 0.7.10. |
| 230 | `Founding.MAX_CITY_NAME_LENGTH` | `24` | The longest city name: room for the window's title and the slot list's line, not a policy. |
| 235 | `Founding.DEFAULT_WORLD_SEED` | `4127` | The world a founding stands on when nobody rolls another: 4127, the map mockup's default seed (city-map.html), so every harness and the playtest found on the same ground. |
| 238 | `Founding.ROLLED_SEED_MAX` | `999_999_999` | The largest seed the founding screen's dice rolls: 999,999,999, nine digits a player can read back and type. |
| 496 | `Founding.VILLAGE` | `{ { "House", "60" }, { "Convenience Store", "5" }, { "Mixed Farm", "2" }, { "...` | The founding village: the playtest's hand-built settlement, name and count. |
| 500 | `Founding.FIRST_WORKS` | `{ "Wind Farm", "Water Treatment Plant", "Elementary School", "Police Station" }` | The first big works, in the order a young city tends to need them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 150 | `private final String label` |  |
| 151 | `private final double cash, reserveUsd` |  |
| 271 | `private final String cityName` |  |
| 272 | `private final Currency currency` |  |
| 273 | `private final double cash` |  |
| 274 | `private final double reserveUsd` |  |
| 275 | `private final double meanInflation` |  |
| 276 | `private final long worldSeed` |  |
| 278 | `private final World.Key worldKey` | The world it stands on (0.7.115, batch E2): Key.generated(worldSeed), or the real Earth round a spot (onEarth(), since 0.7.121 the founding screen's, and kept by a save). |
| 280 | `private final Nations.Path path` | The path of the world beyond the city (WN1; spec-nations 7): Historical or Random, picked once at founding. |
| 282 | `private final long pathSeed` | The seed that draws the path (WN1; spec-nations 7.3) - not worldSeed, which is the ground's. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 72 | 518 | **type** `public final class Founding` | How a city was founded: its name, its money's name, and the treasury and the vault the founders left it. |

### THE PRESETS (0.7.10) (lines 74-173)

| line | len | member | says |
|---:|---:|---|---|
| 113 | 5 | `public static double landBondUsd(CityLand land)` | What an Insane city owes for its founding ground, in thousands of US dollars: the plots its centre holds, as the land office charges ground (LandMarket.groundKm2()), at the land market's opening dollar price a square ... |
| 120 | 3 | `public static double landBondUsd(long worldSeed)` | ...for a city founded on the world of this seed (LandManager.foundingLand()): what the founding screen quotes, and what Game.foundTheLandBond() books. |
| 125 | 3 | `public static double landBondUsd(World.Key worldKey)` | ...on the world a key names (0.7.115): Game.foundTheLandBond()'s, its founding's key. |
| 142 | 31 | **type** `public enum Preset` | The five choices of money, hardest first; CUSTOM carries no figures of its own. |
| 153 | 5 | `Preset(String label, double cash, double reserveUsd)` _(in Founding.Preset)_ |  |
| 159 | 1 | `public String label()` _(in Founding.Preset)_ |  |
| 161 | 1 | `public double cash()` _(in Founding.Preset)_ | The treasury, in thousands; NaN for CUSTOM. |
| 163 | 1 | `public double reserveUsd()` _(in Founding.Preset)_ | The vault, in thousands of US dollars; NaN for CUSTOM. |
| 166 | 6 | `public static Preset of(double cash, double reserveUsd)` _(in Founding.Preset)_ | The preset these two figures are, or CUSTOM. |

### THE BOUNDS ON A CUSTOM FOUNDING (lines 174-223)

### the name (lines 224-231)

### the world (lines 232-268)

| line | len | member | says |
|---:|---:|---|---|
| 241 | 3 | `public static long rollWorldSeed()` | A seed for the founding screen's dice, 1 to ROLLED_SEED_MAX. |
| 246 | 9 | `public static Long parseWorldSeed(String typed)` | A typed seed as a whole number - digits, a leading minus, commas ignored - or null when it is not one. |
| 263 | 5 | `public static long derivedWorldSeed(String cityName, double foundingCash, double landOwnedSqFt, int month)` | The seed a save from before 0.7.56 is read with (spec-land 2.9): SplitMix64 of its city's name's hash, the bits of the treasury it was founded with, the bits of the ground it owns and the month, XORed - what the save ... |

### the record (lines 269-409)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 12 | `public Founding(String cityName, Currency currency, double cash, double reserveUsd, double meanInflation, long worldSeed)` | A founding exactly as given. |
| 308 | 12 | `private Founding(String cityName, Currency currency, double cash, double reserveUsd, double meanInflation, World.Key worldKey, ...` | A founding as given on the world a key names (0.7.115): its seed the key's. |
| 322 | 3 | `public Founding(String cityName, Currency currency, double cash, double reserveUsd, double meanInflation)` | ...on the default world, DEFAULT_WORLD_SEED. |
| 327 | 3 | `public static Founding defaults()` | The defaults: Danzik, its money named after it, the Standard preset, the default world - what "Found with defaults" founded until 0.7.21. |
| 332 | 5 | `public static Founding named(String cityName, Preset preset, double meanInflation)` | A city with its money named after it, on one of the four presets with figures. |
| 339 | 3 | `public static Founding custom(String cityName, double cash, double reserveUsd, double meanInflation)` | A city with its money named after it, on figures of the player's own. |
| 344 | 3 | `public Founding withCurrency(Currency typed)` | The same founding with money the player named by hand - Currency.typed(), which is null when the typed pair does not pass, and problem() then says so. |
| 349 | 3 | `public Founding withWorldSeed(long seed)` | The same founding on another world: the founding screen's World field and its dice (0.7.56). |
| 354 | 3 | `public Founding withWorldKey(World.Key key)` | The same founding on the world a key names (0.7.115): an Earth world (onEarth()) or a harness's; a generated key is withWorldSeed(). |
| 359 | 3 | `public Founding onEarth(double lat, double lon)` | The same founding on the real Earth round a spot (0.7.121, batch E4): its seed this founding's, its data the game's, EarthData.EARTH_2 - the founding screen's Earth. |
| 364 | 3 | `public Founding withMeanInflation(double meanInflation)` | The same founding with the world's mean inflation it is handed (Game.getFounding()), on the same world. |
| 369 | 4 | `public Founding withPath(Nations.Path path, long seed)` | The same founding on another path of the world beyond the city, drawn from this seed (WN1; spec-nations 7): the founding screen's Historical or Random and its dice. |
| 375 | 3 | `public static Founding legacy(double meanInflation)` | What a save from before 0.7.10 was founded with. |
| 379 | 1 | `private static String clean(String name)` |  |
| 381 | 1 | `public String getCityName()` |  |
| 382 | 1 | `public Currency getCurrency()` |  |
| 384 | 1 | `public double getCash()` | The treasury it was founded with, in thousands. |
| 386 | 1 | `public double getReserveUsd()` | The vault it was founded with, in thousands of US dollars - bought on day one at the opening rate. |
| 388 | 1 | `public double getMeanInflation()` | The world's average inflation it was founded into. |
| 390 | 1 | `public Preset getPreset()` | Which of the five this is. |
| 392 | 1 | `public long getWorldSeed()` | The seed of the world it stands on (0.7.56): World.of(getWorldSeed()) is its ground. |
| 394 | 1 | `public World.Key getWorldKey()` | The world it stands on (0.7.115): World.of(getWorldKey()) is its ground - the generated world of getWorldSeed() but on Earth. |
| 396 | 1 | `public Nations.Path getPath()` | The path of the world beyond the city (WN1; spec-nations 7): Historical or Random. |
| 398 | 1 | `public long getPathSeed()` | The seed that draws the path (WN1; spec-nations 7.3) - not getWorldSeed(), the ground's. |
| 400 | 1 | `public World.Kind getWorldKind()` | Which world it stands on (0.7.121): GENERATED, or EARTH. |
| 402 | 1 | `public boolean isOnEarth()` | Whether it stands on the real Earth (0.7.121). |
| 404 | 1 | `public double getEarthLat()` | On Earth, the spot's latitude in degrees - the projection's centre, which the site is snapped from; NaN on a generated world (0.7.121). |
| 406 | 1 | `public double getEarthLon()` | ...and its longitude. |
| 408 | 1 | `public String getEarthData()` | On Earth, the data it is drawn from, by identity ("earth-2:<hash>"); null on a generated world (0.7.121). |

### is it a city (lines 410-465)

| line | len | member | says |
|---:|---:|---|---|
| 413 | 8 | `public static String cityNameProblem(String name)` | Why a city name will not do, in the player's words, or null when it will. |
| 423 | 6 | `public static String cashProblem(double cash)` | Why a treasury will not do, or null. |
| 431 | 7 | `public static String reserveProblem(double reserveUsd)` | Why a vault will not do, or null. |
| 447 | 18 | `public String problem()` | Why this founding cannot found a city, or null when it can: the first of the city's name, its money (a typed pair that did not pass leaves no currency - the screen says which half), the two amounts and the world - and... |

### WHAT IT BUYS (0.7.10) (lines 466-589)

| line | len | member | says |
|---:|---:|---|---|
| 504 | 1 | **type** `public record Work(String name, double cost, boolean fitsInCash, double bondNeeded)` | One of the first works: what it is invoiced at on a new city, and whether the treasury pays it in cash after the village or needs a bond for the rest. |
| 510 | 17 | **type** `public record Buys(double village, double leftAfterVillage, List<Work> works, double reserveUsd, String vau...` | What a founding buys: the village, what is left, each first work taken on its own after the village, and the vault in words. |
| 514 | 5 | `public List<Work> inCash()` _(in Founding.Buys)_ | The works the treasury pays for in cash after the village, each on its own. |
| 521 | 5 | `public List<Work> onABond()` _(in Founding.Buys)_ | The ones it needs a bond for. |
| 529 | 3 | `public static double foundingMaterialPrice()` | What one unit of building material costs a new city abroad: the world's price at the opening rate, the world's level at one. |
| 534 | 3 | `public static double foundingBuildersRate()` | The builders' sales tax on a new city: what a fresh policy charges them (0.7.19; Game, THE BUILDERS' PRICE). |
| 545 | 5 | `public static double orderCost(BuildingsTemplate t, int quantity, double yard)` | An order's invoice on a new city with `yard` units of material in the yard: its cash cost - no wage has been paid yet, so its labour is at the founding ladder - and whatever material the yard does not hold at founding... |
| 552 | 20 | `public static Buys whatItBuys(List<BuildingsTemplate> catalogue, double cash, double reserveUsd)` | What these two figures buy, over this catalogue. |
| 574 | 10 | `public static String vaultSays(double reserveUsd)` | The vault, in words: what it is for. |
| 585 | 4 | `private static BuildingsTemplate find(List<BuildingsTemplate> catalogue, String name)` |  |

