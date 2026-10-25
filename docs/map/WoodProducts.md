# WoodProducts.java - 107 lines · 2 methods · 6 constants · sectors

`ham/citybuildersim/sectors/WoodProducts.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> Sawmills, and the panel, pulp, paper and pellet mills - Wood Products
> (0.7.140, batch I10; docs/studies/spec-materials.md 2.1, 2.7, 2.8, 2.12 and
> 5's I10 row; the study's - materials-chains-research.md - 1.6 and 5.3 to
> 5.6): wood's processing industry, on the end of Sectors.KEYS after Forestry
> (Jerus's Q21: iron, coal, stone, copper, wood).
> 
> WHY. Forestry's crews cut sawlogs and pulpwood (0.7.138, batch I9), and
> until now the world took them at its export price, freight off. One sector
> owns every mill that turns wood into something else and builds whichever
> earns most on its whole cost (the study's 1.1 and 1.2, Jerus's A) -
> ProcessingSector's rule, so this class is a declaration: what it makes,
> what it uses, and its rows.
> 
> THE SIX BUILDINGS (the study's 5.4 and 5.6; ids 133 to 138):
>   - the SAWMILL, 2,080 m3 of lumber and 1,640 m3 of residues - chips and
>     sawdust, four tenths of a log [W26] - from 4,000 m3 of sawlogs;
>   - the LARGE SAWMILL, ten times that less a little: 20,020 and 15,785 from
>     38,500;
>   - the PANEL MILL, 15,000 m3 of oriented strand board from 27,750 m3 of
>     pulpwood;
>   - the PULP MILL, 11,000 t of pulp from 28,000 m3 of pulpwood and 21,500
>     m3 of residues, burning its black liquor and bark for its own power:
>     13,500 kW past its own draw, sold to the grid at the tariff (the
>     study's 1.6, Q20; the template's gen, Sector, THE WORKS' OWN POWER);
>   - the PAPER MILL, 5,500 t of paper from 5,775 t of pulp, its drying
>     steam 850,000 litres of fuel oil (Jerus's Q16: "Add a paper mill now")
>     - the refinery's first buyer of fuel oil;
>   - the WOOD PELLET MILL, 6,000 t of pellets from 13,200 m3 of residues.
> 
> WHAT IT PAYS (the study's 5.5, at parity; A on imported feed for export, B
> on the city's feed at its export price, C that feed replacing imports, D
> both at import prices): a sawmill -0.5, 0.7, 2.3 and 1.1% of its cost a
> month and a large one 0.1, 1.3, 2.8 and 1.6; the panel mill 0.0, 1.1, 2.8
> and 1.7; the pulp mill 0.4, 1.3, 1.9 and 1.1, its power sales in them; the
> paper mill -0.6, 0.0, 0.6 and 0.0 - paper follows the pulp it is made
> from, so it pays only where the city buys paper, which nothing here does
> yet (the study's 5.6); the pellet mill 0.4, 1.7, 3.1 and 1.8. A cubic
> metre of residues is worth more to a pulp mill than to a pellet mill (the
> study's 5.5), so the rule builds pellet mills where there is no pulp mill
> to feed.
> 
> CAPTIVE (ProcessingSector, the spec's 2.7): residues and pulp go to its own
> mills' need first - the sawmills' chips to the pulp and pellet mills, the
> pulp mill's pulp to the paper mill - and only the rest is bought or sold.
> 
> ITS FEED is the market's: Forestry's sawlogs and pulpwood sold to it at
> home first, the world's past them (the markets clear the city's own supply
> before an import); its fuel oil the refinery's, then the world's.
> 
> Who buys: nobody here yet. Lumber and panels wait for the construction
> upgrade (spec 2.12, 2.14), paper for a basket that holds it (the study's
> 10.2) and pellets for the electricity run (Q10); until then the world
> takes them, and the pulp the paper mills do not.

**Uses:** [Good](Good.md) (11), [ProcessingSector](ProcessingSector.md) (1), [Game](Game.md) (1), [BuildCard](BuildCard.md) (1), [Sectors](Sectors.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (6):** [BuildCardCheck](BuildCardCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [GdpCheck](GdpCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [Sectors](Sectors.md), [WoodProductsCheck](WoodProductsCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 65 | `WoodProducts.SAWMILL` | `133` | The Sawmill's permanent id in buildings.json (0.7.140): 2,080 m3 of lumber and 1,640 m3 of residues a month from 4,000 m3 of sawlogs. |
| 68 | `WoodProducts.LARGE_SAWMILL` | `134` | The Large Sawmill's (0.7.140): 20,020 m3 of lumber and 15,785 m3 of residues a month from 38,500 m3 of sawlogs. |
| 71 | `WoodProducts.PANEL_MILL` | `135` | The Panel Mill's (0.7.140): 15,000 m3 of oriented strand board a month from 27,750 m3 of pulpwood. |
| 74 | `WoodProducts.PULP_MILL` | `136` | The Pulp Mill's (0.7.140): 11,000 t of pulp a month from 28,000 m3 of pulpwood and 21,500 m3 of residues, and 13,500 kW sold. |
| 77 | `WoodProducts.PAPER_MILL` | `137` | The Paper Mill's (0.7.140): 5,500 t of paper a month from 5,775 t of pulp and 850,000 litres of fuel oil. |
| 80 | `WoodProducts.PELLET_MILL` | `138` | The Wood Pellet Mill's (0.7.140): 6,000 t of pellets a month from 13,200 m3 of residues. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 62 | 46 | **type** `public final class WoodProducts extends ProcessingSector` | Sawmills, and the panel, pulp, paper and pellet mills - Wood Products (0.7.140, batch I10; docs/studies/spec-materials.md 2.1, 2.7, 2.8, 2.12 and 5's I10 row; the study's - materials-chains-research.md - 1.6 and 5.3 t... |
| 84 | 3 | `public java.util.List<Line> ownLines(ham.citybuildersim.Game game)` | The page (0.7.142, batch I13): the pulp mills' own power sold, and who buys what it makes - its own mills the residues and the pulp first, nobody here the lumber, panels, paper or pellets yet (BuildCard.chainLines()). |
| 88 | 19 | `public WoodProducts()` |  |

