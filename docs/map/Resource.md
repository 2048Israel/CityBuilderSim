# Resource.java - 121 lines · 10 methods · 0 constants · model

`ham/citybuildersim/Resource.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
> and no good).

**Uses:** [Good](Good.md) (5), [LandManager](LandManager.md) (4)

**Used by (23):** [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildingVisual](BuildingVisual.md), [CityLand](CityLand.md), [CityMap](CityMap.md), [Deposit](Deposit.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [OilCheck](OilCheck.md), [ReadPathCheck](ReadPathCheck.md), [TileRaster](TileRaster.md), [World](World.md), [WorldCheck](WorldCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 40 | `Resource.IRON` | Iron ore: 0.2 fields a square kilometre of land, a site the Iron Mine's own ground (400,000 sq ft, 0.03716 km2), 15 Mt a site - 7.59 Mt a km2 of land, today's listing's ore on a quarter of its sites, 1.88% of the map. |
| 52 | `Resource.OIL` | Oil: 0.02 fields a km2 of land, a site a well's 10-acre spacing (435,600 sq ft, 0.04047 km2), 150,000 t a site - thirty years of a 100-barrel-a-day well at 415 t a month. |
| 56 | `Resource.STONE` | Stone: 0.01 fields a km2, a site 0.1 km2, 13.5 Mt a site - a quarry 50 m deep at 2.7 t a cubic metre. |
| 59 | `Resource.COAL` | Coal: 0.002 fields a km2, a site 1 km2, 6.5 Mt a site - a seam 5 m thick at 1.3 t a cubic metre. |
| 62 | `Resource.COPPER` | Copper: 0.0005 fields a km2, a site 0.5 km2, 300,000 t of metal a site - 120 Mt of ore a km2 at 0.5%. |
| 65 | `Resource.URANIUM` | Uranium: 0.0002 fields a km2, a site 0.1 km2, 200 t a site - 1 Mt of ore a km2 at 0.2%. |
| 72 | `Resource.FOREST` | Standing timber: the terrain's forest class, 31% of land (FAO 2020), World.FOREST_M3_PER_KM2 a square kilometre. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 74 | `private final String label` |  |
| 75 | `private final String unit` |  |
| 76 | `private final double fieldsPerKm2` |  |
| 77 | `private final double siteKm2` |  |
| 78 | `private final double amountPerSite` |  |
| 79 | `private final int colour` |  |
| 80 | `private final Good good` |  |
| 81 | `private final double inGroundShare` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 30 | 92 | **type** `public enum Resource` | The seven things that lie in the world's ground: iron ore, oil, stone, coal, copper, uranium and standing timber, each with how thickly its fields lie, how big a site is, what a site holds, its colour on the map, the ... |
| 83 | 11 | `Resource(String label, String unit, double fieldsPerKm2, double siteKm2, double amountPerSite, int colour, Good good, double in...` |  |
| 96 | 1 | `public String label()` | Its name, as the map's legend and the land office will write it. |
| 99 | 1 | `public String unit()` | What its amount is counted in: "tonne", or forest's "cubic metre". |
| 102 | 1 | `public double fieldsPerKm2()` | Fields a square kilometre of land: the mean of each world cell's Poisson count, over its land. |
| 105 | 1 | `public double siteKm2()` | The ground one site takes, in square kilometres: what one mine or well stands on. |
| 108 | 1 | `public double amountPerSite()` | What one site holds, in its unit, before the cell's richness. |
| 111 | 1 | `public int colour()` | Its colour on the map, as 0xRRGGBB - the mockup's (city-map.html). |
| 114 | 1 | `public Good good()` | The good it is sold as, or null while no industry makes it. |
| 117 | 1 | `public double inGroundShare()` | What the ground over a tonne sells for, as a share of the good's world export price; zero while it has no good. |
| 120 | 1 | `public boolean inFields()` | Whether it lies in fields (everything but forest, which is terrain). |

