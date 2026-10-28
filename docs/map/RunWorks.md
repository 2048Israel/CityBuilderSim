# RunWorks.java - 131 lines · 4 methods · 12 constants · model

`ham/citybuildersim/RunWorks.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> What a highway or a railway line needs built on each class of ground beyond what it costs on the flat - cuts and
> fills on foothill, a viaduct on steep ground, a tunnel on very steep - in US dollars a m2 of the run's land, priced
> from published unit costs, and the works an order's land needs at the city's own mix of ground, turned into
> construction points at the run's own founding price a point. Pure arithmetic: Game.quoteBuild() prices it into the
> order, and the points are owed by the order's site as a foothill pad's are (BuildingsStacks.addPreparation()).
> 
> WHY. spec-earth2-terrain 4.3 (D10; Jerus, 2026-10-12, "all rec"). Until ET4 a highway or a railway cost the same on
> a valley floor as across a mountain. Roads and rail in the model are capacity units with land, not lengths, and the
> runs that place them are the map's (CityRuns), so the model cannot price the works where a line actually runs. It
> prices them at the city's own mix of ground instead: the order's land times the sum, over the classes, of the
> class's share of the city's dry ground and the works a m2 of that class needs - the same mix the run's land is
> spread over (LandManager, Use.RUN). Moving the runs into the model is the honest alternative and far larger (D10).
> As SitePreparation does for a pad, the dollars are turned into points once, so the money keeps up with the
> builders' wages and drifts with nothing else; unlike a pad, a run's points are struck at its own founding price a
> point, since the works are more of the run's own kind of work, and so take the builders as long as they cost.
> 
> For scale (the rates below, at the founding): a highway costs about $1,640 a m2 of its land on the flat (the
> Elevated Highway, $9.6M for 63,000 sq ft), so on steep ground it costs about 2.3 times as much and in tunnel 2.4,
> inside the 1.5 to 3 times the spec's sources give mountain highways. A railway (the Freight Line, about $2,330 a m2)
> in tunnel comes to about $8,700 a m2, $261M a km of a 30 m run; the Gotthard Base Tunnel cost CHF 12.2 billion for
> its 57 km, about CHF 214M a km (Swiss federal fact sheet, 2016).

**Uses:** [World](World.md) (6), [LandManager](LandManager.md) (2), [SitePreparation](SitePreparation.md) (2)

**Used by (4):** [BridgeCheck](BridgeCheck.md), [BridgeWorks](BridgeWorks.md), [Game](Game.md), [RunWorksCheck](RunWorksCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 31 | `RunWorks.SQ_FT_PER_M2` | `1 / LandManager.SQ_M_PER_SQ_FT` | Square feet in a square metre. |
| 34 | `RunWorks.RUN_WIDTH_M` | `SitePreparation.PAD_WIDTH_M` | The width of a run across the ground, metres: one plot (30 m), as a foothill pad's (SitePreparation.PAD_WIDTH_M). |
| 41 | `RunWorks.FOOTHILL_USD_PER_M2` | `SitePreparation.USD_PER_M2` | Foothill: a bench cut and filled across the slope, with its wall where it meets the ground above - a pad's arithmetic on the class's mean grade over the run's width, which is a pad's (SitePreparation.USD_PER_M2, about... |
| 48 | `RunWorks.DECK_USD_PER_M2` | `250 * SQ_FT_PER_M2` | A bridge's deck, US dollars a m2: Wisconsin DOT's 2025 planning cost to replace a structure is $220 a sq ft of deck (wisconsindot.gov, "bos-cost", June 2025) and Colorado DOT's 2025 cost data book's weighted bid avera... |
| 54 | `RunWorks.DECK_WIDTH_M` | `2 *(2 * 3.66 + 3.05 + 1.22)` | The deck a highway's viaduct carries, metres: four lanes of 3.66 m (12 ft), an outside shoulder of 3.05 m (10 ft) and an inside one of 1.22 m (4 ft) each way, the Interstate standard's - 23.2 m of the run's 30. |
| 57 | `RunWorks.STEEP_USD_PER_M2` | `DECK_USD_PER_M2 * DECK_WIDTH_M / RUN_WIDTH_M` | Steep: the run carried on a viaduct, its deck over the run's width, about $2,080 a m2 of the run's land. |
| 66 | `RunWorks.TUNNEL_BORE_USD_PER_KM` | `35e6` | One bore of a tunnel, US dollars a km: the European Commission's rail unit-cost study finds tunnels cost 25-30 million euros a km on average and over 80 million in very complex cases (Assessment of unit costs (standar... |
| 69 | `RunWorks.TUNNEL_BORES` | `2` | The bores a highway's tunnel needs: two, one a direction, its four lanes two to a bore. |
| 72 | `RunWorks.VERY_STEEP_USD_PER_M2` | `TUNNEL_BORES * TUNNEL_BORE_USD_PER_KM /(1000 * RUN_WIDTH_M)` | Very steep: the run in tunnel, its bores over the run's width, about $2,330 a m2 of the run's land. |
| 78 | `RunWorks.HIGHWAY_GRADE` | `0.06` | The steepest grade a highway climbs, 6%: the Interstate standard's in mountainous terrain (AASHTO's geometric design policy; WSDOT Design Manual M 22-01, chapter 1220). |
| 84 | `RunWorks.RAIL_GRADE` | `0.022` | ...and a main-line railway, 2.2%: the practicable limit the Baltimore and Ohio set in the 1830s, and the ruling grades of the western US mountain main lines, about 2 to 2.4% (Trains, "Mountain railroading terminology"). |
| 92 | `RunWorks.RAIL_FACTOR` | `HIGHWAY_GRADE / RAIL_GRADE` | How many times a highway's works a m2 of a railway's land needs on any class above flat, about 2.7: a line gains a height in no less than the height over its grade of length, so to cross the same ground the railway ne... |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 106 | **type** `public final class RunWorks` | What a highway or a railway line needs built on each class of ground beyond what it costs on the flat - cuts and fills on foothill, a viaduct on steep ground, a tunnel on very steep - in US dollars a m2 of the run's l... |
| 28 | 1 | `private RunWorks()` |  |
| 98 | 10 | `public static double usdPerM2(int relief, boolean rail)` | The works a m2 of a run's land needs on ground of one relief (World.FLAT to World.VERY_STEEP), US dollars at the founding: none on the flat, the run's own price being the flat one; a railway's RAIL_FACTOR times a high... |
| 113 | 7 | `public static double usdPerM2OfMix(double flat, double foothill, double steep, double verySteep, boolean rail)` | ...at a mix of ground, the city's own: each class's works at its share of the four (any area unit); 0 where the mix holds no ground or no ground above flat, so a flat city's runs carry no works at all. |
| 127 | 4 | `public static double points(double sqFt, double usdPerM2, double foundingCashPerPoint)` | Construction points for `sqFt` square feet of a run's land at `usdPerM2` of works a m2: its dollars, in the thousands buildings.json counts in, over what the builders charge a point of the run's own work at founding -... |

