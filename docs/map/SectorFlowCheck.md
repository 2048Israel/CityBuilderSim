# SectorFlowCheck.java - 309 lines · 11 methods · 3 constants · harnesses

`ham/citybuildersim/SectorFlowCheck.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The flow (0.7.30): SectorFlow's figures - what went into each business,
> what its plant made of it and what held it back, and what came out - held
> to the model's own reads in a played city.
> 
> WHY. The Sectors screen's Operations page draws a business as inputs →
> the plant → outputs since 0.7.30, with the money on every row and the
> plant's six throttles as a cascade down to the rate it runs at. A row whose
> money did not add up to the income statement's line, a cascade that ended
> somewhere other than the operating rate, or a rate drawn over a sector
> with nothing standing (the old page's "Running at 43%" in red over three
> empty sectors) would be a confident wrong picture. The screen is checked
> by eye; this holds what it is drawn from.
> 
> What this has to prove:
>   1. THE MONEY ADDS UP: each sector's inputs - goods and services - come to
>      the statement's inputs line, and its outputs - goods and work billed -
>      to its revenue, to the cent; and both are the SectorBooks month's.
>   2. THE CASCADE IS THE RATE: the six throttles are the sector's own, each
>      step is the product of those before it, and the last is
>      getOperatingRate() to the bit.
>   3. NOTHING STANDING HAS NO RATE: a sector with no building has a plant
>      of none, no rate and no lowest throttle, and its operations page says
>      "no plant standing" where it read a rate; the page's note names all
>      six throttles and says they multiply.
>   4. THE UNITS ARE THE PRODUCTION ROWS, read without creating any; a
>      seller-priced good's capacity is the Build group's note.
>   5. AFTER A LOAD the units are not counted until a month runs (the rows
>      are not saved), and the money, which is, is the same money.
>   6. THE PAGE IS THE BLOCK AND THE SECTOR'S OWN LINES: operations() is
>      plantLines() then ownLines() for the sectors that keep the block, and
>      ownLines() alone for the seven that replace it.
> 
> Every fixture causes its condition.

**Uses:** [SectorFlow](SectorFlow.md) (28), [Sector](Sector.md) (18), [Game](Game.md) (14), [Good](Good.md) (5), [GameFiles](GameFiles.md) (3), [Formats](Formats.md) (3), [BuildCard](BuildCard.md) (3), [LongPlaytest](LongPlaytest.md) (2), [Founding](Founding.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [Sectors](Sectors.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [SectorBooks](SectorBooks.md) (1), [BuildAdvice](BuildAdvice.md) (1)

## Sections

| line | section |
|---:|---|
| 141 | 1. THE MONEY |
| 164 | 2. THE CASCADE |
| 197 | 3. NOTHING STANDING |
| 220 | 4. THE UNITS |
| 265 | 5. AFTER A LOAD |
| 293 | 6. THE PAGE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `SectorFlowCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 101 | `SectorFlowCheck.ORDERS` | `{ { "House", "400" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | The fixture's orders, and whether each went on site. |
| 107 | `SectorFlowCheck.built` | `new java.util.LinkedHashMap<>()` | ...whether each went on site, as city() found it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `static int fails` |  |
| 50 | `static PrintStream out` |  |
| 51 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 263 | **type** `public class SectorFlowCheck` | The flow (0.7.30): SectorFlow's figures - what went into each business, what its plant made of it and what held it back, and what came out - held to the model's own reads in a played city. |
| 53 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 61 | 5 | `static void quietly(Runnable r)` |  |
| 75 | 24 | `static Game city(Path root)` | A played city with a bit of every chain in it - homes, shops and a diner; bakeries; a mine, a foundry and a fabrication shop; builders; power, water, roads and a rail spur, so the shippers pay a railway for haulage, a... |
| 109 | 18 | `public static void main(String[] args) throws Exception` |  |
| 129 | 11 | `static void printCity(Game g)` | The city as the flow reads it, for the record. |

### 1. THE MONEY (lines 141-163)

| line | len | member | says |
|---:|---:|---|---|
| 143 | 20 | `static void theMoney(Game g)` |  |

### 2. THE CASCADE (lines 164-196)

| line | len | member | says |
|---:|---:|---|---|
| 166 | 30 | `static void theCascade(Game g)` |  |

### 3. NOTHING STANDING (lines 197-219)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 20 | `static void nothingStanding(Game g)` |  |

### 4. THE UNITS (lines 220-264)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 42 | `static void theUnits(Game g)` |  |

### 5. AFTER A LOAD (lines 265-292)

| line | len | member | says |
|---:|---:|---|---|
| 267 | 25 | `static void afterALoad(Path root, Game g)` |  |

### 6. THE PAGE (lines 293-309)

| line | len | member | says |
|---:|---:|---|---|
| 295 | 14 | `static void thePage(Game g)` |  |

