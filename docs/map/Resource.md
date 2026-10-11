# Resource.java - 166 lines · 13 methods · 1 constants · model

`ham/citybuildersim/Resource.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The seven things that lie in the world's ground: iron ore, oil, stone, coal, copper, uranium and standing timber, each with how thickly its fields lie, how big a site is, what a site holds, its colour on the map, the good it becomes and what the ground under it sells for.
> 
> WHY THIS EXISTS (0.7.56, batch J1a; the project's spec-land.md 2.1). Until
> now the only thing under the city was iron, and it was a number on a parcel
> drawn by the land office's generator: a site count of one to four and a pool
> of tonnes, with no place. The world (World) lays every resource in real
> fields - a place, a number of sites, a tonnage - so a deposit is somewhere,
> the map can draw it and the land office can sell the ground it is in. The
> densities and sizes are below, each with its source; the iron is sized so a
> square kilometre of land carries what today's listing carries (7.59 Mt
> measured against 7.39 Mt simulated), on a quarter of the sites, so a deposit
> reads as a place (spec-land star 5).
> 
> ORDER IS LOAD-BEARING from J1b on: the land's records keep sites and tonnes
> as arrays in this order (DataSave's landCentre, landPurchases, landOffers,
> depletion). A new resource goes on the end.
> 
> FOREST IS NOT IN FIELDS. It is the forest class of the terrain, and its
> amount is its area times FOREST's timber a square kilometre
> (World.FOREST_M3_PER_KM2); its density, site and amount a site are zero.
> 
> WHAT USES IT. Iron, since J1b put the city's land on the world; oil, lifted
> as CRUDE by the wells since 0.7.62 (batch K). The other five wait for their
> industries, and until then their ground is free (an in-ground share of zero
> and no good). Since 0.7.110 (batch I1) iron's, coal's, copper's and stone's
> fields are each of two types (typesOf(), Deposit.type()), which nothing
> reads yet.

**Uses:** [Deposit](Deposit.md) (12), [Good](Good.md) (6), [LandManager](LandManager.md) (4)

**Used by (48):** [AutoBuildCheck](AutoBuildCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingVisual](BuildingVisual.md), [BuildingsTemplate](BuildingsTemplate.md), [CityLand](CityLand.md), [CityMap](CityMap.md), [ConversionCheck](ConversionCheck.md), [Deposit](Deposit.md), [ExtractionSector](ExtractionSector.md), [Game](Game.md), [GridConversion](GridConversion.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [LegacyLand](LegacyLand.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MaterialsCheck](MaterialsCheck.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PlanCheck](PlanCheck.md), [PolicyPreview](PolicyPreview.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryViewCheck](RefineryViewCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [TileRaster](TileRaster.md), [WellCheck](WellCheck.md), [World](World.md), [WorldCheck](WorldCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 124 | ITS FIELDS' TYPES (0.7.110, batch I1; runs/spec-materials.md 2.4) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 42 | `Resource.IRON` | Iron ore: 0.2 fields a square kilometre of land, a site the Iron Mine's own ground (400,000 sq ft, 0.03716 km2), 15 Mt a site - 7.59 Mt a km2 of land, today's listing's ore on a quarter of its sites, 1.88% of the map. |
| 54 | `Resource.OIL` | Oil: 0.02 fields a km2 of land, a site a well's 10-acre spacing (435,600 sq ft, 0.04047 km2), 150,000 t a site - thirty years of a 100-barrel-a-day well at 415 t a month. |
| 58 | `Resource.STONE` | Stone: 0.01 fields a km2, a site 0.1 km2, 13.5 Mt a site - a quarry 50 m deep at 2.7 t a cubic metre. |
| 61 | `Resource.COAL` | Coal: 0.002 fields a km2, a site 1 km2, 6.5 Mt a site - a seam 5 m thick at 1.3 t a cubic metre. |
| 64 | `Resource.COPPER` | Copper: 0.0005 fields a km2, a site 0.5 km2, 300,000 t of metal a site - 120 Mt of ore a km2 at 0.5%. |
| 67 | `Resource.URANIUM` | Uranium: 0.0002 fields a km2, a site 0.1 km2, 200 t a site - 1 Mt of ore a km2 at 0.2%. |
| 74 | `Resource.FOREST` | Standing timber: the terrain's forest class, 31% of land (FAO 2020), World.FOREST_M3_PER_KM2 a square kilometre. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 135 | `Resource.NO_TYPES` | `java.util.List.of(Deposit.FieldType.NONE), IRON_TYPES = java.util.List.of(Dep...` | Each resource's field types as typesOf() hands them out: NONE alone, or the two of iron, coal, copper and stone, the commoner first. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 76 | `private final String label` |  |
| 77 | `private final String unit` |  |
| 78 | `private final double fieldsPerKm2` |  |
| 79 | `private final double siteKm2` |  |
| 80 | `private final double amountPerSite` |  |
| 81 | `private final int colour` |  |
| 82 | `private final Good good` |  |
| 83 | `private final double inGroundShare` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 32 | 135 | **type** `public enum Resource` | The seven things that lie in the world's ground: iron ore, oil, stone, coal, copper, uranium and standing timber, each with how thickly its fields lie, how big a site is, what a site holds, its colour on the map, the ... |
| 85 | 11 | `Resource(String label, String unit, double fieldsPerKm2, double siteKm2, double amountPerSite, int colour, Good good, double in...` |  |
| 98 | 1 | `public String label()` | Its name, as the map's legend and the land office will write it. |
| 101 | 1 | `public String unit()` | What its amount is counted in: "tonne", or forest's "cubic metre". |
| 104 | 1 | `public double fieldsPerKm2()` | Fields a square kilometre of land: the mean of each world cell's Poisson count, over its land. |
| 107 | 1 | `public double siteKm2()` | The ground one site takes, in square kilometres: what one mine or well stands on. |
| 110 | 1 | `public double amountPerSite()` | What one site holds, in its unit, before the cell's richness. |
| 113 | 1 | `public int colour()` | Its colour on the map, as 0xRRGGBB - the mockup's (city-map.html). |
| 116 | 1 | `public Good good()` | The good it is sold as, or null while no industry makes it. |
| 119 | 1 | `public double inGroundShare()` | What the ground over a tonne sells for, as a share of the good's world export price; zero while it has no good. |
| 122 | 1 | `public boolean inFields()` | Whether it lies in fields (everything but forest, which is terrain). |

### ITS FIELDS' TYPES (0.7.110, batch I1; runs/spec-materials.md 2.4) (lines 124-166)

| line | len | member | says |
|---:|---:|---|---|
| 142 | 9 | `public java.util.List<Deposit.FieldType> typesOf()` | The types of its fields, the commoner first: two for iron, coal, copper and stone; NONE alone for the rest. |
| 153 | 1 | `public boolean hasTypes()` | Whether its fields come in more than one type: iron, coal, copper and stone. |
| 163 | 3 | `public Good goodOf(Deposit.FieldType type)` | The good a field of a type becomes when it is dug (0.7.110): null for a type that is not one of its own; for ANY (null) or one of its own, its good() for now - IRON for both iron types, nothing for coal, copper and st... |

