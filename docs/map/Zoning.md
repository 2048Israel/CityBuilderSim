# Zoning.java - 411 lines · 40 methods · 3 constants · model

`ham/citybuildersim/Zoning.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The city's zoning (0.7.107, batch Z1): ground held for one sector's
> buildings - a share of the city's dry ground or a size in square
> kilometres, at a share of the going business price - and the forest law,
> two shares of the city's forest ground kept from building and from cutting.
> 
> WHY. Until it, free ground was free for anyone at the one going price, and
> that price is a clock: a farm's twenty-four blocks, a mine's square
> kilometre or a refinery's campus is worth more as somewhere to live long
> before it is worth less as what it is, so a city that grows prices its
> ground-hungry industries out (sectors.Agriculture's FARMLAND_RELIEF was the
> one lever, and only for the fields). Jerus, 2026-10-08 (the project's
> spec-zoning.md): "in policy zoning will now exist, and basically the
> default is free, anything everywhere, but with zoning, you can allocate a %
> of total land or a specific km² for a specific industry, and even
> provide the land at a lower rate, or free" - "so that some industries can
> build even tho land is expensive and property tax would kill them", and
> "reserve specific space for some industries". Forest is "a resource on the
> map same as ores and oil, just a lot more common and together", with a law
> "of not building on the forest, or always keep a certain % of forest
> available".
> 
> WHAT IS HERE. The settings and what each zone has used - nothing priced,
> nothing measured: LandManager holds the ground and works out what each
> zone holds of it (ZONES AND THE FOREST LAW there), EconomyManager assesses
> zoned ground at its zone's price, BusinessInvestment prices and plots an
> order on it, PolicyPreview shows a dial's before and after, and since
> 0.7.109 (batch Z2) ZoningView and ui/ZoningScreen draw it all on Policy ›
> Zoning. Zones are AMOUNTS, NOT PLACES (Z1): the model has no positions; a
> tint of the map's districts is still to come (batch Z2's screens left it).
> 
> THE ZONE'S TAXES ARE THE SECTOR'S OWN OFFSETS (Z5: "one dial, two doors"):
> TaxPolicy's profit, sales and property offsets of the sector, moved through
> the zone's door (Game.setZoneTax()) as through Policy > Taxes - no second
> set of rates, so nothing of them is kept here.
> 
> EMPTY, NOTHING CHANGES: no zones and the law at 0 and 0 give the ground
> as it always was (ZoningCheck 1).
> 
> THE FARMLAND RESERVE (RG1, 2026-11-25; Jerus, 2026-11-24: "Policy, that
> also lets farms crops as well be cheaper"). The fields' zone may be priced
> at the sellers' farmland price instead of a share of the going business
> price (setFarmland()): the reserve. Its ground is then sold and assessed at
> the farmland price of the nation that sells the city its ground, outside
> the crowding premium and the city's scarcity (LandMarket's FARMLAND), never
> above the going price; and it holds ground for every farm - Agriculture's
> fields and the city's own Wind and Solar Farms (LandManager.reserveKeyOf()),
> which build in it as a sector builds in its zone. Off by default: no zone,
> or a zone at a share, is the ground as before.
> 
> WHY A ZONE AND NOT A PRICE ALONE. A wind farm's ground was 87% of its
> capital and a solar farm's 96% at the going price (REV-PW3b, item 4), so a
> grown city built none; the fields have run on the same clock since they
> came (sectors.Agriculture). Real farmland sells near what it grows because
> it is zoned for farm use, and the zoning is the price. British Columbia's
> Agricultural Land Reserve (the Land Commission Act, 18 April 1973; about
> 4.7 million hectares, a twentieth of the province) holds its ground to farm
> use, and the Agricultural Land Commission alone may take a parcel out,
> divide it or put it to another use - before it, 4,000 to 6,000 hectares of
> prime farmland a year went to other uses - so a parcel's highest and best
> use, and with it what it sells and is assessed at, is the farm's. Oregon
> ... (10 more lines in the source)

**Uses:** [TaxPolicy](TaxPolicy.md) (1), [LandManager](LandManager.md) (1), [Sectors](Sectors.md) (1)

**Used by (24):** [AdviceValueOracle](AdviceValueOracle.md), [BuildScreen](BuildScreen.md), [CityNeeds](CityNeeds.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [FarmlandReserveCheck](FarmlandReserveCheck.md), [ForestryCheck](ForestryCheck.md), [Game](Game.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandmassCheck](LandmassCheck.md), [PolicyOpenCheck](PolicyOpenCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 190 | · reading |
| 236 | · setting |
| 339 | · the save |

## Enum constants

| line | constant | says |
|---:|---|---|
| 83 | `Zoning.Size.SHARE` |  |
| 83 | `Zoning.Size.KM2` |  |
| 86 | `Zoning.Tax.PROFIT` |  |
| 86 | `Zoning.Tax.SALES` |  |
| 86 | `Zoning.Tax.PROPERTY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `Zoning.MAX_PRICE_SHARE` | `1` | The most of the going business price a zone's ground is sold at: the going price itself (Z3: "0-100%"). |
| 92 | `Zoning.MAX_SIZE_SHARE` | `1` | The most of the city's dry ground one zone may be: all of it. |
| 95 | `Zoning.RESERVE_SECTOR` | `TaxPolicy.FARM_SECTOR` | The one sector whose zone may be the farmland reserve (RG1): the fields', Agriculture's - the city's Wind and Solar Farms build in it too. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 105 | `private final String sector` |  |
| 106 | `private Size kind` |  |
| 107 | `private double size` |  |
| 108 | `private double priceShare` |  |
| 109 | `private double usedSqFt` |  |
| 111 | `private final boolean farmland` | At the farmland price (RG1): the reserve, the fields' zone only. |
| 113 | `private double farmShare` | The farmland price over the going business price as last struck (Zoning.strikeFarmland()); NaN before any. |
| 120 | `private double cityFarmSqFt` | The city's own Wind and Solar Farms' ground in the zone (RG1, REV-RG1): built on, so the zone no longer holds it, but not its sector's - the roll and the buyback read usedSqFt alone, the fields' own ground. |
| 170 | `private final Map<String, Zone> zones` | The zones, at most one a sector, by sector key. |
| 173 | `private double protectedShare` | The forest law (Z6): the share of the city's forest ground protected (never cut, never built on)... |
| 176 | `private double keptShare` | ...and kept from building (protected and working), never under the protected share. |
| 179 | `private double farmlandShare` | The farmland price over the going business price as last struck (RG1, strikeFarmland()): what the reserve sells at; NaN before any. |
| 343 | `public String sector` |  |
| 344 | `public String kind` |  |
| 345 | `public double size` |  |
| 346 | `public double priceShare` |  |
| 347 | `public double usedSqFt` |  |
| 348 | `public boolean farmland` |  |
| 350 | `public double cityFarmSqFt` | The city's own farms' ground in it (RG1, Zone.cityFarmSqFt()); absent on an older save, which has none: 0. |
| 365 | `public List<ZoneState> zones` |  |
| 366 | `public double protectedShare` |  |
| 367 | `public double keptShare` |  |
| 368 | `public double clearedSqFt` |  |
| 369 | `public double clearedM3` |  |
| 370 | `public double salvageM3` |  |
| 372 | `public double farmlandShare` | The farmland price over the going price as last struck (RG1), so a loaded reserve sells at what it sold at before the save and not at its dial until the month strikes it again; 0 reads as none struck, and an older sav... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 80 | 332 | **type** `public final class Zoning` | The city's zoning (0.7.107, batch Z1): ground held for one sector's buildings - a share of the city's dry ground or a size in square kilometres, at a share of the going business price - and the forest law, two shares ... |
| 83 | 1 | **type** `public enum Size` | How a zone's size is given: a share of the city's dry ground, re-read as the city buys land so 2% stays 2% (Z2, the default), or square kilometres. |
| 86 | 1 | **type** `public enum Tax` | A zone's three tax rows (Z5): the sector's own profit, sales and property offsets, through the zone's door (Game.setZoneTax()). |
| 104 | 64 | **type** `public static final class Zone` | One sector's zone: its size, the share of the going business price its ground is sold and assessed at, and the ground its sector has built on in it (used) - the zone's own, in square feet, saved; what it HOLDS of the ... |
| 122 | 3 | `Zone(String sector, Size kind, double size, double priceShare, double usedSqFt)` _(in Zoning.Zone)_ |  |
| 126 | 10 | `Zone(String sector, Size kind, double size, double priceShare, double usedSqFt, boolean farmland, double farmShare, double city...` _(in Zoning.Zone)_ |  |
| 137 | 1 | `public String sector()` _(in Zoning.Zone)_ |  |
| 138 | 1 | `public Size kind()` _(in Zoning.Zone)_ |  |
| 140 | 1 | `public double size()` _(in Zoning.Zone)_ | A share of the dry ground (SHARE) or square kilometres (KM2). |
| 147 | 3 | `public double priceShare()` _(in Zoning.Zone)_ | The share of the going business price the zone's ground is sold and assessed at: the dial's (dialShare()), or for the farmland reserve (RG1) the farmland price over the going price as last struck, never above the goin... |
| 151 | 1 | `public double dialShare()` _(in Zoning.Zone)_ | The share the player set, whatever the reserve sells at: what the price card's dial reads. |
| 153 | 1 | `public boolean farmland()` _(in Zoning.Zone)_ | Whether this zone is the farmland reserve (RG1): sold at the sellers' farmland price, holding ground for every farm. |
| 155 | 1 | `public double usedSqFt()` _(in Zoning.Zone)_ | The ground its sector has built on in it: what the roll assesses and the buyback pays at the zone's price. |
| 157 | 1 | `public double cityFarmSqFt()` _(in Zoning.Zone)_ | The city's own Wind and Solar Farms' ground in it (RG1): neither the sector's nor held. |
| 159 | 1 | `public double builtSqFt()` _(in Zoning.Zone)_ | Every square foot built on in it, its sector's and the city's farms': what it no longer holds (LandManager.heldBy()). |
| 162 | 3 | `public double targetSqFt(double drySqFt)` _(in Zoning.Zone)_ | What the zone is to be, in square feet, on this much dry ground: its share of it, or its square kilometres. |
| 166 | 1 | `Zone copy()` _(in Zoning.Zone)_ |  |
| 181 | 3 | `private static double clampShare(double share, double most)` |  |
| 185 | 4 | `private static double clampSize(Size kind, double size)` |  |

### reading (lines 190-235)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 1 | `public Zone zone(String sector)` | A sector's zone, or null for none. |
| 196 | 1 | `public boolean hasZones()` | Whether any zone stands. |
| 199 | 1 | `public boolean isEmpty()` | Nothing set: no zones, and the law at 0 and 0 - the ground as it always was. |
| 202 | 9 | `public List<Zone> zones()` | The zones in Sectors.KEYS order, then any other key in the order zoned: the order every pass over them takes. |
| 212 | 1 | `public double protectedShare()` |  |
| 213 | 1 | `public double keptShare()` |  |
| 216 | 4 | `public double used(String sector)` | The ground a sector has built on in its zone, square feet; 0 with no zone. |
| 222 | 4 | `public double priceShare(String sector)` | The share of the going price a sector's zone sells and assesses at; 1, the going price, with no zone. |
| 228 | 4 | `public Zone reserve()` | The farmland reserve (RG1): the fields' zone while it is at the farmland price; null for none. |
| 234 | 1 | `public double farmlandShare()` | The farmland price over the going business price as last struck (strikeFarmland()); NaN before any. |

### setting (lines 236-338)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 8 | `public Zone set(String sector, Size kind, double size, double priceShare)` | Zones a sector's ground, or re-sizes and re-prices its zone, keeping what it has used: `size` a share of the dry ground (SHARE, 0 to 1) or square kilometres (KM2); `priceShare` 0 to 1 of the going business price. |
| 261 | 7 | `public Zone setFarmland(String sector, boolean on)` | Puts a zone at the farmland price, or back at its dial's share (RG1): the reserve, which only the fields' zone may be (RESERVE_SECTOR), its size, dial and used ground kept. |
| 270 | 4 | `void strikeFarmland(double share)` | The farmland price over the going business price, struck where the going price is (LandManager.strikeFarmland()): what the reserve sells at from now on. |
| 281 | 3 | `public Zone remove(String sector)` | Un-zones a sector: what the zone held goes back to everyone, and the ground already sold in it stays with its owner at what it paid - assessed at the going price from now on, there being no zone to assess it at. |
| 286 | 4 | `public void setForestLaw(double protectedShare, double keptShare)` | The forest law (Z6): `protectedShare` of the forest ground never cut nor built on, `keptShare` (at least that) never built on. |
| 292 | 4 | `void addUsed(String sector, double sqFt)` | The sector built this much in its zone (LandManager.allocate()). |
| 298 | 7 | `double releaseUsed(String sector, double sqFt)` | ...and gave this much back (LandManager.release()): its zone's ground first, the zone holding it again; what came out of the zone. |
| 307 | 4 | `void addCityFarm(double sqFt)` | The city's own Wind or Solar Farm took this much of the reserve's held ground (RG1, LandManager.allocateCityFarm()): counted apart from the fields' used ground. |
| 313 | 7 | `double releaseCityFarm(double sqFt)` | ...and a city farm gave this much back (LandManager.releaseCityFarm()), whatever the reserve is by then: the city's farms' ground in the zone first, the zone holding it again; what came out of it. |
| 322 | 6 | `public void clear()` | Every zone gone and the law at 0 and 0: a new city's. |
| 330 | 8 | `public Zoning copy()` | A detached copy, for a preview to stage a dial on (PolicyPreview's zoning reads): nothing it does reaches the city. |

### the save (lines 339-411)

| line | len | member | says |
|---:|---:|---|---|
| 342 | 11 | **type** `public static final class ZoneState` | One zone as saved: its sector, its size's kind by name, its size, its price share and the ground used; since RG1 whether it is the farmland reserve (absent on an older save: false, a zone at its share, as it was). |
| 351 | 1 | `public ZoneState()` _(in Zoning.ZoneState)_ |  |
| 364 | 11 | **type** `public static final class State` | DataSave's zoning (0.7.107): the zones, the law and the forest cleared for building (LandManager's C: its ground in square feet, the ledger's unit, and the wood that stood on it). |
| 373 | 1 | `public State()` _(in Zoning.State)_ |  |
| 377 | 18 | `State toState()` | The zones and the law as saved; the forest cleared is the land's, filled in by LandManager.zoningState(). |
| 397 | 14 | `void restore(State s)` | Puts the zones and the law back as saved; null, none and 0 and 0. |

