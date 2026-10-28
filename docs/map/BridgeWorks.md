# BridgeWorks.java - 126 lines · 7 methods · 12 constants · model

`ham/citybuildersim/BridgeWorks.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> What a long bridge's span costs, in US dollars a metre by kind and by the depth of the water it crosses, and the
> construction points those dollars are at the bridge's own founding price a point. Pure arithmetic: Game.quoteBuild()
> prices the span into the order (Game.bridgeWorksFor()), and the points are owed by the order's site before its first
> bridge finishes, as a highway's works and a foothill pad's are (BuildingsStacks.addPreparation()).
> 
> WHY. spec-earth2-terrain 6.4 (ET6; D13, D14, D16). A long bridge is an order between two landmasses, its length
> and the depth of its water found by the model from the Earth's map (BridgeSurvey), and the template cannot hold a
> price for a length it does not have: the template prices the two landings, the span is billed per order, by length
> and by depth. As RunWorks does for a viaduct, the dollars are turned into points once, so the money keeps up with
> the builders' wages and drifts with nothing else, and a bridge of 36 km takes the builders' capacity for years, as
> the real ones did.
> 
> THE RATES, from two built structures. In shallow water the span is a viaduct: RunWorks's deck a m2 (US DOT's 2025
> planning costs) times the deck's width. In deep water the piers get taller, and the Confederation Bridge (12.9 km,
> an 11 m deck, built 1993-97, its piers standing in water up to about 35 m) is the dearest deck a m2 the game has a
> figure for, about US$11,800: both rates are the model's own, and the rate between them is linear in depth. A rail
> bridge costs RAIL_OVER_HIGHWAY times a highway's a metre, and a road bridge, a deck about half as wide, about half
> of a highway's, so rail is dearer than highway, and highway than road, at every depth.

**Uses:** [RunWorks](RunWorks.md) (2), [BridgeSurvey](BridgeSurvey.md) (1)

**Used by (10):** [BridgeCheck](BridgeCheck.md), [Bridges](Bridges.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [Game](Game.md), [SaveSlotCheck](SaveSlotCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 28 | `BridgeWorks.Kind.HIGHWAY` |  |
| 28 | `BridgeWorks.Kind.RAIL` |  |
| 28 | `BridgeWorks.Kind.ROAD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `BridgeWorks.BASE_DEPTH_M` | `5` | The depth, metres, at and under which the deck costs the base rate: 5, a viaduct over shallows and over fresh water, whose depth the World does not hold (World.depthAt() is 0 on a lake and a river). |
| 37 | `BridgeWorks.BASE_USD_PER_M2` | `RunWorks.DECK_USD_PER_M2` | The deck a m2 at BASE_DEPTH_M or less, and on fresh water: RunWorks.DECK_USD_PER_M2 (US DOT's 2025 planning costs, $250 a sq ft, about $2,690 a m2). |
| 44 | `BridgeWorks.CONFEDERATION_COST_CAD` | `1.3e9` | The Confederation Bridge's cost as built, Canadian dollars: C$1.3 billion (Wikipedia's figure for the cost as built, about 30% over the C$840 million projected; the C$1.0 billion some sources give, Library and Archive... |
| 47 | `BridgeWorks.CONFEDERATION_LENGTH_M` | `12_900` | The Confederation Bridge's length, metres: 12,900 (Prince Edward Island to New Brunswick, over the Northumberland Strait). |
| 50 | `BridgeWorks.CONFEDERATION_DECK_M` | `11` | ...and its deck's width, metres: 11 (two lanes and shoulders). |
| 53 | `BridgeWorks.CONFEDERATION_DEPTH_M` | `35` | ...and the depth its piers stand in at the deepest, metres: 35 (about; the deepest water of the strait's crossing). |
| 56 | `BridgeWorks.CAD_CPI_1997_TO_2025` | `1.79` | Canada's consumer prices from 1997, the year the bridge opened, to 2025: about 1.79 (Statistics Canada, table 18-10-0005). |
| 59 | `BridgeWorks.USD_PER_CAD_2025` | `0.72` | US dollars a Canadian dollar in 2025: 0.72. |
| 62 | `BridgeWorks.CONFEDERATION_USD_PER_M2` | `CONFEDERATION_COST_CAD * CAD_CPI_1997_TO_2025 * USD_PER_CAD_2025 /(CONFEDERAT...` | The Confederation's deck a m2 in 2025 US dollars, its cost over its deck's area: about $11,800. |
| 66 | `BridgeWorks.HIGHWAY_DECK_WIDTH_M` | `RunWorks.DECK_WIDTH_M` | A highway bridge's deck, metres: RunWorks.DECK_WIDTH_M (23.2, the Interstate standard's four lanes and shoulders). |
| 69 | `BridgeWorks.ROAD_DECK_WIDTH_M` | `2 * 3.66 + 2 * 2.44` | A road bridge's deck, metres: two lanes of 3.66 m (12 ft) and two shoulders of 2.44 m (8 ft), the usual rural two-lane section, 12.2 m, the Confederation's 11 m beside it. |
| 76 | `BridgeWorks.RAIL_OVER_HIGHWAY` | `1.42` | How many times a highway's metre a railway's costs, 1.42: the game's own scale in RunWorks's javadoc - the Freight Line's $2,330 a m2 of its land over the Elevated Highway's $1,640. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 104 | **type** `public final class BridgeWorks` | What a long bridge's span costs, in US dollars a metre by kind and by the depth of the water it crosses, and the construction points those dollars are at the bridge's own founding price a point. |
| 25 | 1 | `private BridgeWorks()` |  |
| 28 | 1 | **type** `public enum Kind` | The three kinds of long bridge, as buildings.json's "bridge" names them: a highway's, a railway line's and a street's. |
| 79 | 3 | `public static double deckWidthM(Kind kind)` | The deck's width a kind of bridge carries, metres: a rail bridge's is a highway's. |
| 87 | 4 | `public static double deckUsdPerM2(double depthM)` | The deck a m2 in water of this depth, US dollars: the base rate at BASE_DEPTH_M or less, the Confederation's at CONFEDERATION_DEPTH_M, linear between and beyond (taller piers in deeper water), never below the base. |
| 93 | 4 | `public static double usdPerM(Kind kind, double depthM)` | What a metre of the span costs in water of this depth, US dollars: the deck a m2 times the kind's width, a railway's times RAIL_OVER_HIGHWAY. |
| 104 | 7 | `public static double spanUsd(Kind kind, double[] stepM, double[] depthM)` | The span of a surveyed crossing, US dollars: for each step between two plots of the chain, its length times the rate at the depths of its two ends, half each - a step runs from one plot's centre to the next's, half of... |
| 113 | 3 | `public static double spanUsd(Kind kind, BridgeSurvey.Result survey)` | ...of a surveyed crossing (BridgeSurvey.Result). |
| 122 | 4 | `public static double points(double usd, double foundingCashPerPoint)` | Construction points for `usd` dollars of span: the dollars, in the thousands buildings.json counts in, over what the builders charge a point of the bridge's own work at founding - its template's founding cash cost ove... |

