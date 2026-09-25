# Founding.java - 365 lines · 29 methods · 12 constants · model

`ham/citybuildersim/Founding.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [Currency](Currency.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (7), [WorldEconomy](WorldEconomy.md) (3), [Game](Game.md) (2), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (33):** [BankCheck](BankCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [ConservationCheck](ConservationCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DataSave](DataSave.md), [DenominationCheck](DenominationCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FoundingScreen](FoundingScreen.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OutsideCheck](OutsideCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveHeader](SaveHeader.md), [SicknessCheck](SicknessCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md)

## Sections

| line | section |
|---:|---|
| 43 | THE PRESETS (0.7.10) |
| 95 | THE BOUNDS ON A CUSTOM FOUNDING |
| 132 | · the name |
| 140 | · the record |
| 205 | · is it a city |
| 254 | WHAT IT BUYS (0.7.10) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 66 | `Founding.Preset.LEAN` |  |
| 67 | `Founding.Preset.STANDARD` |  |
| 68 | `Founding.Preset.WEALTHY` |  |
| 69 | `Founding.Preset.CUSTOM` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 53 | `Founding.LEAN_CASH` | `25_000` | Lean's treasury, in thousands: D$25M - three-quarters of the founding village at a new city's invoices, so the city borrows from its first months, for the rest of it and for every big work. |
| 56 | `Founding.LEAN_RESERVE_USD` | `10_000` | Lean's vault, in thousands of US dollars: US$10M - a few months to two years of a young city's imports. |
| 59 | `Founding.WEALTHY_CASH` | `2_500_000` | Wealthy's treasury, in thousands: D$2.5B - the start every city had from 0.6.10 to 0.7.9, which the playtest never drew below D$2.46B in thirty years. |
| 62 | `Founding.WEALTHY_RESERVE_USD` | `1_000_000` | Wealthy's vault, in thousands of US dollars: US$1B - the old start's, which the playtest's defence took sixty-odd years to spend half of (month 739, the median of eight seeds). |
| 121 | `Founding.MIN_CASH` | `5_000` | The least a city may be founded with in its treasury, in thousands: D$5M, ten houses and a shop at a new city's invoices. |
| 124 | `Founding.MAX_CASH` | `10_000_000` | The most, in thousands: D$10B, four times the Wealthy start. |
| 127 | `Founding.MIN_RESERVE_USD` | `0` | The least in the vault, in thousands of US dollars: none, as every city had before 0.6.10. |
| 130 | `Founding.MAX_RESERVE_USD` | `4_000_000` | The most, in thousands of US dollars: US$4B, four times the Wealthy start. |
| 135 | `Founding.DEFAULT_CITY_NAME` | `"Danzik"` | The city a founding is named when nobody names it - Jerus's first city, and every save's before 0.7.10. |
| 138 | `Founding.MAX_CITY_NAME_LENGTH` | `24` | The longest city name: room for the window's title and the slot list's line, not a policy. |
| 279 | `Founding.VILLAGE` | `{ { "House", "60" }, { "Convenience Store", "5" }, { "Mixed Farm", "2" }, { "...` | The founding village: the playtest's hand-built settlement, name and count. |
| 283 | `Founding.FIRST_WORKS` | `{ "Wind Farm", "Water Treatment Plant", "Elementary School", "Police Station" }` | The first big works, in the order a young city tends to need them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `private final String label` |  |
| 72 | `private final double cash, reserveUsd` |  |
| 142 | `private final String cityName` |  |
| 143 | `private final Currency currency` |  |
| 144 | `private final double cash` |  |
| 145 | `private final double reserveUsd` |  |
| 146 | `private final double meanInflation` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 325 | **type** `public final class Founding` | How a city was founded: its name, its money's name, and the treasury and the vault the founders left it. |

### THE PRESETS (0.7.10) (lines 43-94)

| line | len | member | says |
|---:|---:|---|---|
| 65 | 29 | **type** `public enum Preset` | The four choices of money; CUSTOM carries no figures of its own. |
| 74 | 5 | `Preset(String label, double cash, double reserveUsd)` _(in Founding.Preset)_ |  |
| 80 | 1 | `public String label()` _(in Founding.Preset)_ |  |
| 82 | 1 | `public double cash()` _(in Founding.Preset)_ | The treasury, in thousands; NaN for CUSTOM. |
| 84 | 1 | `public double reserveUsd()` _(in Founding.Preset)_ | The vault, in thousands of US dollars; NaN for CUSTOM. |
| 87 | 6 | `public static Preset of(double cash, double reserveUsd)` _(in Founding.Preset)_ | The preset these two figures are, or CUSTOM. |

### THE BOUNDS ON A CUSTOM FOUNDING (lines 95-131)

### the name (lines 132-139)

### the record (lines 140-204)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 7 | `public Founding(String cityName, Currency currency, double cash, double reserveUsd, double meanInflation)` | A founding exactly as given. |
| 166 | 3 | `public static Founding defaults()` | "Found with defaults": Danzik, its money named after it, the Standard preset, the default world. |
| 171 | 5 | `public static Founding named(String cityName, Preset preset, double meanInflation)` | A city with its money named after it, on one of the three presets with figures. |
| 178 | 3 | `public static Founding custom(String cityName, double cash, double reserveUsd, double meanInflation)` | A city with its money named after it, on figures of the player's own. |
| 183 | 3 | `public Founding withCurrency(Currency typed)` | The same founding with money the player named by hand - Currency.typed(), which is null when the typed pair does not pass, and problem() then says so. |
| 188 | 3 | `public static Founding legacy(double meanInflation)` | What a save from before 0.7.10 was founded with. |
| 192 | 1 | `private static String clean(String name)` |  |
| 194 | 1 | `public String getCityName()` |  |
| 195 | 1 | `public Currency getCurrency()` |  |
| 197 | 1 | `public double getCash()` | The treasury it was founded with, in thousands. |
| 199 | 1 | `public double getReserveUsd()` | The vault it was founded with, in thousands of US dollars - bought on day one at the opening rate. |
| 201 | 1 | `public double getMeanInflation()` | The world's average inflation it was founded into. |
| 203 | 1 | `public Preset getPreset()` | Which of the four this is. |

### is it a city (lines 205-253)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 8 | `public static String cityNameProblem(String name)` | Why a city name will not do, in the player's words, or null when it will. |
| 218 | 6 | `public static String cashProblem(double cash)` | Why a treasury will not do, or null. |
| 226 | 7 | `public static String reserveProblem(double reserveUsd)` | Why a vault will not do, or null. |
| 239 | 14 | `public String problem()` | Why this founding cannot found a city, or null when it can: the first of the city's name, its money (a typed pair that did not pass leaves no currency - the screen says which half), the two amounts and the world. |

### WHAT IT BUYS (0.7.10) (lines 254-365)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 1 | **type** `public record Work(String name, double cost, boolean fitsInCash, double bondNeeded)` | One of the first works: what it is invoiced at on a new city, and whether the treasury pays it in cash after the village or needs a bond for the rest. |
| 293 | 17 | **type** `public record Buys(double village, double leftAfterVillage, List<Work> works, double reserveUsd, String vau...` | What a founding buys: the village, what is left, each first work taken on its own after the village, and the vault in words. |
| 297 | 5 | `public List<Work> inCash()` _(in Founding.Buys)_ | The works the treasury pays for in cash after the village, each on its own. |
| 304 | 5 | `public List<Work> onABond()` _(in Founding.Buys)_ | The ones it needs a bond for. |
| 312 | 3 | `public static double foundingMaterialPrice()` | What one unit of building material costs a new city abroad: the world's price at the opening rate, the world's level at one. |
| 322 | 4 | `public static double orderCost(BuildingsTemplate t, int quantity, double yard)` | An order's invoice on a new city with `yard` units of material in the yard: its cash cost, and whatever material the yard does not hold at foundingMaterialPrice(). |
| 328 | 20 | `public static Buys whatItBuys(List<BuildingsTemplate> catalogue, double cash, double reserveUsd)` | What these two figures buy, over this catalogue. |
| 350 | 10 | `public static String vaultSays(double reserveUsd)` | The vault, in words: what it is for. |
| 361 | 4 | `private static BuildingsTemplate find(List<BuildingsTemplate> catalogue, String name)` |  |

