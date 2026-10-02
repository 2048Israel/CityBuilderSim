# SectorFlowCheck.java - 300 lines · 11 methods · 3 constants · harnesses

`ham/citybuildersim/SectorFlowCheck.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [SectorFlow](SectorFlow.md) (28), [Sector](Sector.md) (18), [Game](Game.md) (14), [Good](Good.md) (5), [GameFiles](GameFiles.md) (3), [Formats](Formats.md) (3), [BuildCard](BuildCard.md) (3), [Founding](Founding.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [LongPlaytest](LongPlaytest.md) (1), [SectorBooks](SectorBooks.md) (1), [BuildAdvice](BuildAdvice.md) (1)

## Sections

| line | section |
|---:|---|
| 132 | 1. THE MONEY |
| 155 | 2. THE CASCADE |
| 188 | 3. NOTHING STANDING |
| 211 | 4. THE UNITS |
| 256 | 5. AFTER A LOAD |
| 284 | 6. THE PAGE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `SectorFlowCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 92 | `SectorFlowCheck.ORDERS` | `{ { "House", "400" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Con...` | The fixture's orders, and whether each went on site. |
| 98 | `SectorFlowCheck.built` | `new java.util.LinkedHashMap<>()` | ...whether each went on site, as city() found it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `static int fails` |  |
| 48 | `static PrintStream out` |  |
| 49 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 45 | 256 | **type** `public class SectorFlowCheck` | The flow (0.7.30): SectorFlow's figures - what went into each business, what its plant made of it and what held it back, and what came out - held to the model's own reads in a played city. |
| 51 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 59 | 5 | `static void quietly(Runnable r)` |  |
| 73 | 17 | `static Game city(Path root)` | A played city with a bit of every chain in it - homes, shops and a diner; bakeries; a mine, a foundry and a fabrication shop; builders; power, water, roads and a rail spur, so the shippers pay a railway for haulage, a... |
| 100 | 18 | `public static void main(String[] args) throws Exception` |  |
| 120 | 11 | `static void printCity(Game g)` | The city as the flow reads it, for the record. |

### 1. THE MONEY (lines 132-154)

| line | len | member | says |
|---:|---:|---|---|
| 134 | 20 | `static void theMoney(Game g)` |  |

### 2. THE CASCADE (lines 155-187)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 30 | `static void theCascade(Game g)` |  |

### 3. NOTHING STANDING (lines 188-210)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 20 | `static void nothingStanding(Game g)` |  |

### 4. THE UNITS (lines 211-255)

| line | len | member | says |
|---:|---:|---|---|
| 213 | 42 | `static void theUnits(Game g)` |  |

### 5. AFTER A LOAD (lines 256-283)

| line | len | member | says |
|---:|---:|---|---|
| 258 | 25 | `static void afterALoad(Path root, Game g)` |  |

### 6. THE PAGE (lines 284-300)

| line | len | member | says |
|---:|---:|---|---|
| 286 | 14 | `static void thePage(Game g)` |  |

