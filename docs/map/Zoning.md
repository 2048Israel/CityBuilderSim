# Zoning.java - 280 lines · 29 methods · 2 constants · model

`ham/citybuildersim/Zoning.java` - generated 2026-10-20 by CodeMap; line numbers are as of that run.

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

**Uses:** [LandManager](LandManager.md) (1), [Sectors](Sectors.md) (1)

**Used by (15):** [BuildScreen](BuildScreen.md), [CityNeeds](CityNeeds.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [ForestryCheck](ForestryCheck.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 116 | · reading |
| 153 | · setting |
| 217 | · the save |

## Enum constants

| line | constant | says |
|---:|---|---|
| 50 | `Zoning.Size.SHARE` |  |
| 50 | `Zoning.Size.KM2` |  |
| 53 | `Zoning.Tax.PROFIT` |  |
| 53 | `Zoning.Tax.SALES` |  |
| 53 | `Zoning.Tax.PROPERTY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `Zoning.MAX_PRICE_SHARE` | `1` | The most of the going business price a zone's ground is sold at: the going price itself (Z3: "0-100%"). |
| 59 | `Zoning.MAX_SIZE_SHARE` | `1` | The most of the city's dry ground one zone may be: all of it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `private final String sector` |  |
| 70 | `private Size kind` |  |
| 71 | `private double size` |  |
| 72 | `private double priceShare` |  |
| 73 | `private double usedSqFt` |  |
| 99 | `private final Map<String, Zone> zones` | The zones, at most one a sector, by sector key. |
| 102 | `private double protectedShare` | The forest law (Z6): the share of the city's forest ground protected (never cut, never built on)... |
| 105 | `private double keptShare` | ...and kept from building (protected and working), never under the protected share. |
| 221 | `public String sector` |  |
| 222 | `public String kind` |  |
| 223 | `public double size` |  |
| 224 | `public double priceShare` |  |
| 225 | `public double usedSqFt` |  |
| 240 | `public List<ZoneState> zones` |  |
| 241 | `public double protectedShare` |  |
| 242 | `public double keptShare` |  |
| 243 | `public double clearedSqFt` |  |
| 244 | `public double clearedM3` |  |
| 245 | `public double salvageM3` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 234 | **type** `public final class Zoning` | The city's zoning (0.7.107, batch Z1): ground held for one sector's buildings - a share of the city's dry ground or a size in square kilometres, at a share of the going business price - and the forest law, two shares ... |
| 50 | 1 | **type** `public enum Size` | How a zone's size is given: a share of the city's dry ground, re-read as the city buys land so 2% stays 2% (Z2, the default), or square kilometres. |
| 53 | 1 | **type** `public enum Tax` | A zone's three tax rows (Z5): the sector's own profit, sales and property offsets, through the zone's door (Game.setZoneTax()). |
| 68 | 29 | **type** `public static final class Zone` | One sector's zone: its size, the share of the going business price its ground is sold and assessed at, and the ground its sector has built on in it (used) - the zone's own, in square feet, saved; what it HOLDS of the ... |
| 75 | 7 | `Zone(String sector, Size kind, double size, double priceShare, double usedSqFt)` _(in Zoning.Zone)_ |  |
| 83 | 1 | `public String sector()` _(in Zoning.Zone)_ |  |
| 84 | 1 | `public Size kind()` _(in Zoning.Zone)_ |  |
| 86 | 1 | `public double size()` _(in Zoning.Zone)_ | A share of the dry ground (SHARE) or square kilometres (KM2). |
| 87 | 1 | `public double priceShare()` _(in Zoning.Zone)_ |  |
| 88 | 1 | `public double usedSqFt()` _(in Zoning.Zone)_ |  |
| 91 | 3 | `public double targetSqFt(double drySqFt)` _(in Zoning.Zone)_ | What the zone is to be, in square feet, on this much dry ground: its share of it, or its square kilometres. |
| 95 | 1 | `Zone copy()` _(in Zoning.Zone)_ |  |
| 107 | 3 | `private static double clampShare(double share, double most)` |  |
| 111 | 4 | `private static double clampSize(Size kind, double size)` |  |

### reading (lines 116-152)

| line | len | member | says |
|---:|---:|---|---|
| 119 | 1 | `public Zone zone(String sector)` | A sector's zone, or null for none. |
| 122 | 1 | `public boolean hasZones()` | Whether any zone stands. |
| 125 | 1 | `public boolean isEmpty()` | Nothing set: no zones, and the law at 0 and 0 - the ground as it always was. |
| 128 | 9 | `public List<Zone> zones()` | The zones in Sectors.KEYS order, then any other key in the order zoned: the order every pass over them takes. |
| 138 | 1 | `public double protectedShare()` |  |
| 139 | 1 | `public double keptShare()` |  |
| 142 | 4 | `public double used(String sector)` | The ground a sector has built on in its zone, square feet; 0 with no zone. |
| 148 | 4 | `public double priceShare(String sector)` | The share of the going price a sector's zone sells and assesses at; 1, the going price, with no zone. |

### setting (lines 153-216)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 7 | `public Zone set(String sector, Size kind, double size, double priceShare)` | Zones a sector's ground, or re-sizes and re-prices its zone, keeping what it has used: `size` a share of the dry ground (SHARE, 0 to 1) or square kilometres (KM2); `priceShare` 0 to 1 of the going business price. |
| 176 | 3 | `public Zone remove(String sector)` | Un-zones a sector: what the zone held goes back to everyone, and the ground already sold in it stays with its owner at what it paid - assessed at the going price from now on, there being no zone to assess it at. |
| 181 | 4 | `public void setForestLaw(double protectedShare, double keptShare)` | The forest law (Z6): `protectedShare` of the forest ground never cut nor built on, `keptShare` (at least that) never built on. |
| 187 | 4 | `void addUsed(String sector, double sqFt)` | The sector built this much in its zone (LandManager.allocate()). |
| 193 | 7 | `double releaseUsed(String sector, double sqFt)` | ...and gave this much back (LandManager.release()): its zone's ground first, the zone holding it again; what came out of the zone. |
| 202 | 5 | `public void clear()` | Every zone gone and the law at 0 and 0: a new city's. |
| 209 | 7 | `public Zoning copy()` | A detached copy, for a preview to stage a dial on (PolicyPreview's zoning reads): nothing it does reaches the city. |

### the save (lines 217-280)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 8 | **type** `public static final class ZoneState` | One zone as saved: its sector, its size's kind by name, its size, its price share and the ground used. |
| 226 | 1 | `public ZoneState()` _(in Zoning.ZoneState)_ |  |
| 239 | 9 | **type** `public static final class State` | DataSave's zoning (0.7.107): the zones, the law and the forest cleared for building (LandManager's C: its ground in square feet, the ledger's unit, and the wood that stood on it). |
| 246 | 1 | `public State()` _(in Zoning.State)_ |  |
| 250 | 15 | `State toState()` | The zones and the law as saved; the forest cleared is the land's, filled in by LandManager.zoningState(). |
| 267 | 13 | `void restore(State s)` | Puts the zones and the law back as saved; null, none and 0 and 0. |

