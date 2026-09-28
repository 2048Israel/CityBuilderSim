# Founding.java - 398 lines · 30 methods · 14 constants · model

`ham/citybuildersim/Founding.java` - generated 2026-09-27 by CodeMap; line numbers are as of that run.

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

**Uses:** [Currency](Currency.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (7), [WorldEconomy](WorldEconomy.md) (3), [Game](Game.md) (2), [LandManager](LandManager.md) (1), [LandMarket](LandMarket.md) (1), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (35):** [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [ConservationCheck](ConservationCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DataSave](DataSave.md), [DenominationCheck](DenominationCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FoundingScreen](FoundingScreen.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OutsideCheck](OutsideCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveHeader](SaveHeader.md), [SicknessCheck](SicknessCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md)

## Sections

| line | section |
|---:|---|
| 43 | THE PRESETS (0.7.10) |
| 119 | THE BOUNDS ON A CUSTOM FOUNDING |
| 162 | · the name |
| 170 | · the record |
| 235 | · is it a city |
| 287 | WHAT IT BUYS (0.7.10) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 89 | `Founding.Preset.INSANE` | Nothing in the treasury or the vault, and the founding ground owed abroad (0.7.14): the one preset under MIN_CASH, on purpose. |
| 90 | `Founding.Preset.LEAN` |  |
| 91 | `Founding.Preset.STANDARD` |  |
| 92 | `Founding.Preset.WEALTHY` |  |
| 93 | `Founding.Preset.CUSTOM` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `Founding.INSANE_LAND_COUPON` | `.03` | Insane's coupon on the land it owes for, a year: 3%, Jerus's number. |
| 67 | `Founding.INSANE_LAND_YEARS` | `20` | Insane's land bond's term, in years: twenty, Jerus's "20y" - one of the five term loans (LongTermBond.MATURITIES). |
| 75 | `Founding.LEAN_CASH` | `25_000` | Lean's treasury, in thousands: D$25M - three-quarters of the founding village at a new city's invoices, so the city borrows from its first months, for the rest of it and for every big work. |
| 78 | `Founding.LEAN_RESERVE_USD` | `10_000` | Lean's vault, in thousands of US dollars: US$10M - a few months to two years of a young city's imports. |
| 81 | `Founding.WEALTHY_CASH` | `2_500_000` | Wealthy's treasury, in thousands: D$2.5B - the start every city had from 0.6.10 to 0.7.9, which the playtest never drew below D$2.46B in thirty years. |
| 84 | `Founding.WEALTHY_RESERVE_USD` | `1_000_000` | Wealthy's vault, in thousands of US dollars: US$1B - the old start's, which the playtest's defence took sixty-odd years to spend half of (month 739, the median of eight seeds). |
| 151 | `Founding.MIN_CASH` | `5_000` | The least a city may be founded with in its treasury, in thousands: D$5M, ten houses and a shop at a new city's invoices. |
| 154 | `Founding.MAX_CASH` | `10_000_000` | The most, in thousands: D$10B, four times the Wealthy start. |
| 157 | `Founding.MIN_RESERVE_USD` | `0` | The least in the vault, in thousands of US dollars: none, as every city had before 0.6.10. |
| 160 | `Founding.MAX_RESERVE_USD` | `4_000_000` | The most, in thousands of US dollars: US$4B, four times the Wealthy start. |
| 165 | `Founding.DEFAULT_CITY_NAME` | `"Danzik"` | The city a founding is named when nobody names it - Jerus's first city, and every save's before 0.7.10. |
| 168 | `Founding.MAX_CITY_NAME_LENGTH` | `24` | The longest city name: room for the window's title and the slot list's line, not a policy. |
| 312 | `Founding.VILLAGE` | `{ { "House", "60" }, { "Convenience Store", "5" }, { "Mixed Farm", "2" }, { "...` | The founding village: the playtest's hand-built settlement, name and count. |
| 316 | `Founding.FIRST_WORKS` | `{ "Wind Farm", "Water Treatment Plant", "Elementary School", "Police Station" }` | The first big works, in the order a young city tends to need them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 95 | `private final String label` |  |
| 96 | `private final double cash, reserveUsd` |  |
| 172 | `private final String cityName` |  |
| 173 | `private final Currency currency` |  |
| 174 | `private final double cash` |  |
| 175 | `private final double reserveUsd` |  |
| 176 | `private final double meanInflation` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 358 | **type** `public final class Founding` | How a city was founded: its name, its money's name, and the treasury and the vault the founders left it. |

### THE PRESETS (0.7.10) (lines 43-118)

| line | len | member | says |
|---:|---:|---|---|
| 70 | 3 | `public static double landBondUsd()` | What an Insane city owes for its founding ground, in thousands of US dollars: every starting square foot at the land market's opening dollar price. |
| 87 | 31 | **type** `public enum Preset` | The five choices of money, hardest first; CUSTOM carries no figures of its own. |
| 98 | 5 | `Preset(String label, double cash, double reserveUsd)` _(in Founding.Preset)_ |  |
| 104 | 1 | `public String label()` _(in Founding.Preset)_ |  |
| 106 | 1 | `public double cash()` _(in Founding.Preset)_ | The treasury, in thousands; NaN for CUSTOM. |
| 108 | 1 | `public double reserveUsd()` _(in Founding.Preset)_ | The vault, in thousands of US dollars; NaN for CUSTOM. |
| 111 | 6 | `public static Preset of(double cash, double reserveUsd)` _(in Founding.Preset)_ | The preset these two figures are, or CUSTOM. |

### THE BOUNDS ON A CUSTOM FOUNDING (lines 119-161)

### the name (lines 162-169)

### the record (lines 170-234)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 7 | `public Founding(String cityName, Currency currency, double cash, double reserveUsd, double meanInflation)` | A founding exactly as given. |
| 196 | 3 | `public static Founding defaults()` | "Found with defaults": Danzik, its money named after it, the Standard preset, the default world. |
| 201 | 5 | `public static Founding named(String cityName, Preset preset, double meanInflation)` | A city with its money named after it, on one of the four presets with figures. |
| 208 | 3 | `public static Founding custom(String cityName, double cash, double reserveUsd, double meanInflation)` | A city with its money named after it, on figures of the player's own. |
| 213 | 3 | `public Founding withCurrency(Currency typed)` | The same founding with money the player named by hand - Currency.typed(), which is null when the typed pair does not pass, and problem() then says so. |
| 218 | 3 | `public static Founding legacy(double meanInflation)` | What a save from before 0.7.10 was founded with. |
| 222 | 1 | `private static String clean(String name)` |  |
| 224 | 1 | `public String getCityName()` |  |
| 225 | 1 | `public Currency getCurrency()` |  |
| 227 | 1 | `public double getCash()` | The treasury it was founded with, in thousands. |
| 229 | 1 | `public double getReserveUsd()` | The vault it was founded with, in thousands of US dollars - bought on day one at the opening rate. |
| 231 | 1 | `public double getMeanInflation()` | The world's average inflation it was founded into. |
| 233 | 1 | `public Preset getPreset()` | Which of the five this is. |

### is it a city (lines 235-286)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 8 | `public static String cityNameProblem(String name)` | Why a city name will not do, in the player's words, or null when it will. |
| 248 | 6 | `public static String cashProblem(double cash)` | Why a treasury will not do, or null. |
| 256 | 7 | `public static String reserveProblem(double reserveUsd)` | Why a vault will not do, or null. |
| 269 | 17 | `public String problem()` | Why this founding cannot found a city, or null when it can: the first of the city's name, its money (a typed pair that did not pass leaves no currency - the screen says which half), the two amounts and the world. |

### WHAT IT BUYS (0.7.10) (lines 287-398)

| line | len | member | says |
|---:|---:|---|---|
| 320 | 1 | **type** `public record Work(String name, double cost, boolean fitsInCash, double bondNeeded)` | One of the first works: what it is invoiced at on a new city, and whether the treasury pays it in cash after the village or needs a bond for the rest. |
| 326 | 17 | **type** `public record Buys(double village, double leftAfterVillage, List<Work> works, double reserveUsd, String vau...` | What a founding buys: the village, what is left, each first work taken on its own after the village, and the vault in words. |
| 330 | 5 | `public List<Work> inCash()` _(in Founding.Buys)_ | The works the treasury pays for in cash after the village, each on its own. |
| 337 | 5 | `public List<Work> onABond()` _(in Founding.Buys)_ | The ones it needs a bond for. |
| 345 | 3 | `public static double foundingMaterialPrice()` | What one unit of building material costs a new city abroad: the world's price at the opening rate, the world's level at one. |
| 355 | 4 | `public static double orderCost(BuildingsTemplate t, int quantity, double yard)` | An order's invoice on a new city with `yard` units of material in the yard: its cash cost, and whatever material the yard does not hold at foundingMaterialPrice(). |
| 361 | 20 | `public static Buys whatItBuys(List<BuildingsTemplate> catalogue, double cash, double reserveUsd)` | What these two figures buy, over this catalogue. |
| 383 | 10 | `public static String vaultSays(double reserveUsd)` | The vault, in words: what it is for. |
| 394 | 4 | `private static BuildingsTemplate find(List<BuildingsTemplate> catalogue, String name)` |  |

