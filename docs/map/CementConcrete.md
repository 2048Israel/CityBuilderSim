# CementConcrete.java - 73 lines · 2 methods · 3 constants · sectors

`ham/citybuildersim/sectors/CementConcrete.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

> Kilns and cement plants - Cement & Concrete (0.7.131, batch I7;
> docs/studies/spec-materials.md 2.1, 2.12 and 5's I7 row; the study's 6):
> stone's processing industry, on Sectors.KEYS after Quarrying - its end
> when it came (Jerus's Q21).
> 
> WHY. Limestone is what the kilns burn: into quicklime, the Oxygen Steel
> Shop's flux, which the city imported since the integrated route (0.7.123,
> batch I4), and with aggregate and coal into cement. One sector owns every
> building that turns stone into something else and builds whichever earns
> most on its whole cost (the study's 1.1 and 1.2, Jerus's A) -
> ProcessingSector's rule, so this class is a declaration: what it makes,
> what it uses, and its rows.
> 
> THE THREE BUILDINGS (the study's 6.4; ids 118 to 120):
>   - the CEMENT PLANT, 25,000 t of cement a month from 30,000 t of
>     limestone, 3,750 t of aggregate and 3,350 t of thermal coal;
>   - the LARGE CEMENT PLANT, five times that, 125,000 t;
>   - the LIME KILN, 18,000 t of quicklime from 32,400 t of limestone and
>     3,600 t of thermal coal.
> The Ready-Mix Plant and the Asphalt Plant, CONCRETE and ASPHALT, wait for
> the construction upgrade (Jerus's Q18).
> 
> WHAT IT PAYS (the study's 6.5): a cement plant 0.1-0.3% a month exporting
> and 1.5-1.9% replacing imports - cement is "really regional", its band
> wide - so the rule builds one only where the city buys cement, which
> nothing here does until the construction upgrade; the lime kiln 1.6% on
> limestone at its export price and 5.7% replacing imported lime. Its
> limestone is weighed at the export price for as much as the quarries
> carried into aggregate (ProcessingSector.values(); sectors.Quarrying, THE
> CARRY), the import price past it; its coal is Coal Mining's where a city
> digs it (batch I6), else imported.
> 
> Who buys: Iron & Steel's oxygen shop the lime; the world the cement and the
> rest of the lime, until the construction upgrade draws cement for the
> builders (spec 2.14).

**Uses:** [Good](Good.md) (5), [ProcessingSector](ProcessingSector.md) (1), [Game](Game.md) (1), [BuildCard](BuildCard.md) (1), [Sectors](Sectors.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (5):** [BuildingDataCheck](BuildingDataCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [QuarryingCheck](QuarryingCheck.md), [Sectors](Sectors.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 47 | `CementConcrete.CEMENT_PLANT` | `118` | The Cement Plant's permanent id in buildings.json (0.7.131): 25,000 t of cement a month. |
| 50 | `CementConcrete.LARGE_CEMENT_PLANT` | `119` | The Large Cement Plant's (0.7.131): 125,000 t of cement a month - Edison's long kilns, 1902. |
| 53 | `CementConcrete.LIME_KILN` | `120` | The Lime Kiln's (0.7.131): 18,000 t of quicklime a month. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 44 | 30 | **type** `public final class CementConcrete extends ProcessingSector` | Kilns and cement plants - Cement & Concrete (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.12 and 5's I7 row; the study's 6): stone's processing industry, on Sectors.KEYS after Quarrying - its end when it c... |
| 57 | 3 | `public java.util.List<Line> ownLines(ham.citybuildersim.Game game)` | The page (0.7.142, batch I13): who buys its cement and lime here - Iron & Steel's oxygen shops the lime - or that they ship (BuildCard.chainLines()). |
| 61 | 12 | `public CementConcrete()` |  |

