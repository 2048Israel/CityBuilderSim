# LandClassView.java - 186 lines · 16 methods · 1 constants · model

`ham/citybuildersim/LandClassView.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> What the land office and the sector statements say about the CLASSES of ground (ET3, spec-earth2-terrain 5.3), in
> words, worked out without a node: the office's prices for flat, foothill and steep ground, an offer's ground by
> class, and note 11's rows (a sector's land by the ground it stands on) written as the sentences the screens show.
> 
> WHY. Slices A and B gave every class of ground a price and the books a note by class, and left the screens saying
> nothing of them: a player on hills saw one price for ground that was sold at three, an offer that was mostly
> mountain looked like any other, and the balance sheet's land was a single line. The words were going to be
> written twice, in LandScreen and in SectorScreen, and read a third time by the harness and by the renders that
> go in front of Jerus (tools.LandClassRenders), so they are written once, here, in the model's package, from the
> model's own getters (LandManager, LandMarket, SectorStatements.landNote()) and none of the toolkit. A screen lays
> these lines out; it recomputes nothing. In a city with no foothill and no steep ground, and an offer that is all
> flat, every method here answers nothing - the screens draw exactly what they drew before ET3.
> 
> THE MONEY WORDS are the screens' (ui.Money): a ground price is a square metre's, to the cent from a dollar up and
> to the ten-thousandth under one (unitPrice()), a sum compact (money()), the city's mark ahead of the figure and
> the world's "US$". They are copied here, since the model never imports the interface, and LandClassCheck 7 holds
> the copy against ui.Money on a spread of figures so the two cannot drift.

**Uses:** [LandManager](LandManager.md) (9), [CityLand](CityLand.md) (7), [SectorStatements](SectorStatements.md) (5), [LandMarket](LandMarket.md) (3), [Game](Game.md) (2), [LandParcel](LandParcel.md) (2), [SectorBooks](SectorBooks.md) (2), [Currency](Currency.md) (2)

**Used by (4):** [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandScreen](LandScreen.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 31 | THE LAND OFFICE'S PRICES BY CLASS |
| 68 | AN OFFER'S GROUND BY CLASS |
| 103 | NOTE 11, A SECTOR'S LAND BY CLASS |
| 127 | THE MONEY WORDS (ui.Money's, copied; LandClassCheck 7 holds them to it) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 160 | `LandClassView.GROUPED` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 160 | **type** `public final class LandClassView` | What the land office and the sector statements say about the CLASSES of ground (ET3, spec-earth2-terrain 5.3), in words, worked out without a node: the office's prices for flat, foothill and steep ground, an offer's g... |
| 29 | 1 | `private LandClassView()` |  |

### THE LAND OFFICE'S PRICES BY CLASS (lines 31-67)

| line | len | member | says |
|---:|---:|---|---|
| 40 | 27 | `public static List<String> officeLines(Game g)` | The land office's prices by class, a line for each class of dry ground the city owns or the shelf offers, flat first: what a business pays the city a square metre (LandManager's getPricePerSqFt(), getFoothillPricePerS... |

### AN OFFER'S GROUND BY CLASS (lines 68-102)

| line | len | member | says |
|---:|---:|---|---|
| 77 | 25 | `public static String offerLine(LandParcel p, LandMarket m)` | An offer's dry ground by class, in the unit of its dry ground whole, and what a buyer who builds on all of it pays: "Flat 0.62 km² · foothill 0.31 · steep 0.08 · buildable 0.93 km², US$1.2M with its foothill prepared"... |

### NOTE 11, A SECTOR'S LAND BY CLASS (lines 103-126)

| line | len | member | says |
|---:|---:|---|---|
| 110 | 5 | `public static List<String> landNoteLines(SectorBooks.SectorMonth now, String mark)` | Note 11's rows as words, one for each class that holds any of the sector's land (SectorStatements.landNote()): "Flat 1.24 km² at D$41.20 a m² · D$51.1M". |
| 117 | 3 | `public static List<String> landNoteLines(Game g, SectorBooks.SectorMonth now)` | ...for a game's city (its money's mark). |
| 122 | 4 | `public static String landNoteLabel(SectorStatements.Row r, String mark)` | One of note 11's rows without its value, which the statement's own column writes: "Flat 1.24 km² at D$41.20 a m²". |

### THE MONEY WORDS (ui.Money's, copied; LandClassCheck 7 holds them to it) (lines 127-186)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 1 | `public static String local(String mark, double thousandsPerSqFt)` | A ground price a square metre in the city's money from the thousands a square foot the model keeps: "D$41.20" (Money.groundPrice() marked). |
| 133 | 1 | `public static String usd(double thousandsPerSqFt)` | ...in the world's: "US$3.10". |
| 136 | 1 | `public static String usd0(double thousands)` | A sum in thousands of US dollars: "US$1.2M" (Money.usd()). |
| 139 | 1 | `static String groundPrice(double thousandsPerSqFt)` | Money.groundPrice(): a square metre of what the model keeps a square foot, as unitPrice() writes it, no mark. |
| 142 | 4 | `public static String marked(String prefix, String amount)` | Money.marked(): the mark replaces the "$" of the amount. |
| 148 | 1 | `public static String money(double thousands)` | Money.money(): thousands as the screens abbreviate city money. |
| 151 | 8 | `static String unitPrice(double thousands)` | Money.unitPrice(): a price small enough that the cents matter - "$20,882", "$41.20", "$0.7500". |
| 161 | 4 | `static { ... }` |  |
| 167 | 11 | `private static String tight(double value, boolean compact)` | Money.tightMoney(): `compact` abbreviates from ten thousand dollars (k), else only from a million (M). |
| 179 | 1 | `private static double tenths(double v)` |  |
| 181 | 5 | `private static double unsigned0(double value, int decimals)` |  |

