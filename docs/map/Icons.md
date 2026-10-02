# Icons.java - 483 lines · 6 methods · 58 constants · interface

`ham/citybuildersim/ui/Icons.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> The rail's icons, as vector outlines - and since 0.7.24 the Build tab's, one
> per category, and the few the frame draws (the money block, the "Needs you"
> chip, the construction tab).
> 
> WHY PATHS AND NOT PICTURES
> 
> There is still no image file anywhere in this project, and these did not add
> one. A PNG would have to be packaged into the jar, found at runtime, shipped
> at two or three sizes for different screens, and re-exported in a second
> colour for the active tab. An SVG path is a string: it draws crisp at any
> size, it takes its colour from whoever draws it, and it cannot fail to load.
> 
> WHAT REPLACED WHAT
> 
> The rail used geometric glyphs from the Unicode block - and the trouble with
> picking eleven marks out of one block is that they all look alike. Three of
> them were diamonds and four were filled squares, so the rail was learned by
> POSITION rather than read, which is fine for the person who built the game
> and useless for anybody in their first ten minutes of it.
> 
> These are eleven different objects: a building, a plot of land, a group of
> people, a heartbeat, a factory, a parliament, a coin, a globe, a set of
> scales, a graph, a gear. Nobody has to learn them - and since 0.7.21 each
> has its name under it on the rail, the gear is three lines (Menu), and the
> header's envelope and the (i) beside a short line are drawn the same way.
> 
> SOURCE AND LICENCE
> 
> Lucide (lucide.dev), ISC licence - free to use in a commercial game, no
> attribution required in the product. Each icon is drawn on a 24x24 grid with
> a 2px stroke, round caps and round joins, and is STROKED rather than filled:
> see UserInterface.railButton(), which sets fill to null.
> 
> MULTI-PART ICONS ARE ONE STRING. Lucide draws several of these with a mix of
> <path>, <circle> and <rect> elements. JavaFX SVGPath takes a single path, so
> the parts are concatenated - every "M" starts a fresh subpath and all of them
> get stroked - and the circles and rectangles are written out as arcs, because
> SVGPath has no notion of either.

**Uses:** [Sectors](Sectors.md) (15), [DecisionLog](DecisionLog.md) (8), [Good](Good.md) (1), [Sector](Sector.md) (1)

**Used by (15):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 162 | THE BUILD TAB'S AND THE FRAME'S (0.7.24) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `Icons.BUILD` | `"M12 10h.01 M12 14h.01 M12 6h.01 M16 10h.01 M16 14h.01 M16 6h.01" + "M8 10h.0...` | A building. |
| 67 | `Icons.LAND` | `"M2 4 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0" + "M14 5 l3-3 3 3 M14 10 l3-3 3 3 M17 1...` | A tent and trees - ground, rather than a map of it. |
| 72 | `Icons.POPULATION` | `"M17 21a5 5 0 0 0-10 0" + "M22 10.5a3.5 3.5 0 0 0-5.507-2.868" + "M7.507 7.63...` | Three people, one in front. |
| 81 | `Icons.SERVICES` | `"M2 9.5a5.5 5.5 0 0 1 9.591-3.676.56.56 0 0 0.818 0A5.49 5.49 0 0 1 22 9.5" +...` | A heart with a pulse through it. |
| 87 | `Icons.SECTOR` | `"M12 16h.01 M16 16h.01 M8 16h.01" + "M3 19a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V8.5...` | A factory. |
| 94 | `Icons.GOVERNMENT` | `"M10 18v-7 M14 18v-7 M18 18v-7 M6 18v-7 M3 22h18" + "M11.119 2.205a2 2 0 0 1 ...` | A parliament, columns and all. |
| 99 | `Icons.FINANCES` | `"M2 12 a10 10 0 1 0 20 0 a10 10 0 1 0 -20 0" + "M16 8h-6a2 2 0 1 0 0 4h4a2 2 ...` | A coin with a dollar in it. |
| 115 | `Icons.BANK` | `"M3 22h18 M4 18v-7 M9 18v-7 M15 18v-7 M20 18v-7" + "M2 18h20" + "M11.5 2.4a1 ...` | A bank: a pediment on columns, with a doorway. |
| 128 | `Icons.INFRASTRUCTURE` | `"M3 19 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0" + "M15 5 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 ...` | A route: two waypoints and the road that winds between them. |
| 134 | `Icons.TRADE` | `"M2 12 a10 10 0 1 0 20 0 a10 10 0 1 0 -20 0" + "M12 2a14.5 14.5 0 0 0 0 20 14...` | A globe. |
| 140 | `Icons.POLICY` | `"M12 3v18 M7 21h10" + "M3 7h1a17 17 0 0 0 8-2 17 17 0 0 0 8 2h1" + "M19 8 l3 ...` | Scales. |
| 147 | `Icons.REPORTS` | `"M3 3v16a2 2 0 0 0 2 2h16" + "M19 9 l-5 5 -4-4 -3 3"` | A line on axes. |
| 152 | `Icons.MENU` | `"M4 7h16 M4 12h16 M4 17h16"` | Three lines: the game menu, at the foot of the rail since 0.7.21 (it was the gear). |
| 155 | `Icons.MAIL` | `"M4 6h16v12H4z M4 7l8 6 8-6"` | An envelope: the inbox, in the header since 0.7.21. |
| 158 | `Icons.INFO` | `"M3 12 a9 9 0 1 0 18 0 a9 9 0 1 0 -18 0" + "M12 11v5 M12 8h.01"` | A circled i: the (i) that opens a line's full text (0.7.21; Pieces.infoButton()). |
| 172 | `Icons.OVERVIEW` | `"M4 4h7v7H4z M13 4h7v7h-7z M4 13h7v7H4z M13 13h7v7h-7z"` | Four squares: the Build overview. |
| 175 | `Icons.UTILITIES` | `"M13 3L5 13h6l-1 8 8-10h-6l1-8z"` | A bolt: Utilities. |
| 178 | `Icons.ROADS` | `"M8 3L5 21 M16 3l3 18 M12 4v3 M12 10.5v3 M12 17v3"` | Two kerbs and a dashed line: Roads & transit. |
| 181 | `Icons.HEALTH` | `"M9 3h6v6h6v6h-6v6H9v-6H3V9h6z"` | A cross: Healthcare. |
| 184 | `Icons.EDUCATION` | `"M2 9l10-5 10 5-10 5z M6 11v5c0 1.5 2.7 3 6 3s6-1.5 6-3v-5 M22 9v6"` | A mortarboard: Education. |
| 187 | `Icons.SAFETY` | `"M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z M9 12l2 2 4-4"` | A shield with a tick: Safety. |
| 190 | `Icons.HOMES` | `"M4 20V10l8-6 8 6v10z M10 20v-5h4v5"` | A house: Homes. |
| 193 | `Icons.SHOPS` | `"M4 9l1.5-5h13L20 9" + "M4 9h16v1.5a2.7 2.7 0 0 1-5.3 0 2.7 2.7 0 0 1-5.4 0A2...` | An awning over a shopfront: Shops. |
| 198 | `Icons.INDUSTRY` | `"M3 20h18 M4 20V10l5 3V10l5 3V7h3l.6-3h1.2l.6 3v13"` | Saw-tooth roofs and a chimney: Industry. |
| 201 | `Icons.OFFICES` | `"M5 20V4h10v16 M15 9h4v11 M3 20h18" + "M8 7h1 M11 7h1 M8 10h1 M11 10h1 M8 13h...` | An office block: Offices. |
| 205 | `Icons.FARMS` | `"M12 21V9" + "M12 9c-2.4-.8-3.6-2.8-3.6-5.4 2.4.3 3.6 2.4 3.6 5.4z" + "M12 9c...` | An ear of wheat: Farms. |
| 214 | `Icons.RAIL` | `"M7 3h10a2 2 0 0 1 2 2v9a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V5a2 2 0 0 1 2-2z" + "M...` | A train's face: Rail. |
| 218 | `Icons.VEHICLES` | `"M5 16v-4l2-5h10l2 5v4z M3 12h18" + "M6 17 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0" + ...` | A car: Vehicles. |
| 223 | `Icons.LUXURY` | `"M6 4h12l3 5-9 11L3 9z M3 9h18 M9 4l3 16 3-16"` | A cut stone: Luxury shops. |
| 226 | `Icons.FOOD` | `"M7 3v7a2 2 0 0 0 2 2v9 M5 3v5 M9 3v5 M17 21V3c-2 1-3.2 4-3.2 8H17"` | A fork and a knife: Restaurants. |
| 229 | `Icons.CRANE` | `"M6 21V5 M3 5h17 M6 5l4-3 M18 5v5 M17 10h2v2h-2z M3 21h7"` | A tower crane: what is on site - the overview's tiles and the construction tab. |
| 232 | `Icons.COIN` | `"M4 12 a8 8 0 1 0 16 0 a8 8 0 1 0 -16 0" + "M14.5 9.2c-.6-.8-1.5-1.2-2.5-1.2-...` | A coin: the header's money block. |
| 237 | `Icons.ALERT` | `"M12 4l9 16H3z M12 10v4 M12 17h.01"` | A warning triangle: the header's "Needs you" chip. |
| 240 | `Icons.STAFF` | `"M8.5 7 a3.5 3.5 0 1 0 7 0 a3.5 3.5 0 1 0 -7 0" + "M5 21c0-4 3-6.5 7-6.5s7 2....` | A person: a Build card's staff bar, the posts the city can't fill. |
| 244 | `Icons.PIN` | `"M12 17v5" + "M9 10.76a2 2 0 0 1-1.11 1.79l-1.78.9A2 2 0 0 0 5 15.24V16a1 1 0...` | A pin: the drawer that stays open (Lucide's pin). |
| 250 | `Icons.CLOSE` | `"M6 6l12 12 M18 6L6 18"` | A cross: close. |
| 257 | `Icons.ORE` | `"M4 20L16 8 M9.5 4.5Q19.5 4.5 19.5 14.5Q17 7 9.5 4.5z"` | A pick: ore - the land office's Ore card (0.7.26). |
| 267 | `Icons.BIRTH` | `"M9 7a3 3 0 1 0 6 0a3 3 0 1 0-6 0 M6 20c0-4 3-6 6-6s6 2 6 6 M19 3v4 M17 5h4"` | A head and shoulders with a plus: born. |
| 270 | `Icons.DEATH` | `"M7 21V9a5 5 0 0 1 10 0v12 M4 21h16 M12 10v5 M10 12h4"` | A headstone on the ground: died. |
| 273 | `Icons.ARRIVE` | `"M3 12h11 M10 8l4 4-4 4 M15 4h5v16h-5"` | An arrow into a door: moved in. |
| 276 | `Icons.LEAVE` | `"M21 12H10 M17 8l4 4-4 4 M9 4H4v16h5"` | An arrow out of a door: moved out. |
| 279 | `Icons.CHILD` | `"M10 6a2 2 0 1 0 4 0a2 2 0 1 0-4 0 M8 20v-6l4-3 4 3v6"` | A small figure: a child, the orphans' tile. |
| 282 | `Icons.TICK` | `"M5 12.5l4.5 4.5L19 7.5"` | A tick: nothing needed (the "Needs you" chip, a ring's middle). |
| 291 | `Icons.DROP` | `"M12 3c-3.5 5-6 8-6 11a6 6 0 0 0 12 0c0-3-2.5-6-6-11z"` | A drop: water. |
| 294 | `Icons.CANE` | `"M9 8a3 3 0 0 1 6 0v13"` | A walking cane, its crook at the top: senior care. |
| 297 | `Icons.CELL` | `"M5 4h14v16H5z M9 4v16 M12 4v16 M15 4v16"` | A door of bars: the prisons. |
| 300 | `Icons.BUS` | `"M8 6v6 M15 6v6 M2 12h19.6" + " M18 18h3s0.5 -1.7 0.8 -2.8c0.1 -0.4 0.2 -0.8 ...` | A bus, from the side (0.7.29, Lucide's bus): the Infrastructure tab's Transit page. |
| 306 | `Icons.LORRY` | `"M14 18V6a2 2 0 0 0 -2 -2H4a2 2 0 0 0 -2 2v11a1 1 0 0 0 1 1h2 M15 18H9" + " M...` | A lorry (0.7.29, Lucide's truck): the Infrastructure tab's Freight page, and the fleets on it. |
| 319 | `Icons.MILL` | `"M9.5 21l1-8h3l1 8z M11 21v-2.5h2V21" + " M12 10L6.5 4.5 M12 10l5.5-5.5 M12 1...` | A windmill, its sails crossed over the tower: Industry, the food mills. |
| 323 | `Icons.CAN` | `"M6 6.5 a6 2.5 0 1 0 12 0 a6 2.5 0 1 0 -12 0" + " M6 6.5v11 a6 2.5 0 0 0 12 0...` | A tin, its lid an ellipse: Food Processing. |
| 327 | `Icons.INGOT` | `"M2 18h20l-3-6H5z M5 12l2.5-4h9l2.5 4"` | An ingot, its top face over its side: Heavy Industry, the steel. |
| 330 | `Icons.GEAR` | `"M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915" + "a2.34 2...` | A gear: Manufacturing (the settings gear's outline, drawn since 0.7.30). |
| 340 | `Icons.PICK` | `ORE` | A pick: Mining, and an investors' word about ore (the land office's ORE). |
| 351 | `Icons.PAPER` | `"M15 2H6a2 2 0 0 0 -2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2 -2V7z" + " M14 2v4a2 ...` | A sheet of paper with its lines: the city's paper, The book. |
| 355 | `Icons.BANKNOTE` | `"M4 6h16a2 2 0 0 1 2 2v8a2 2 0 0 1 -2 2H4a2 2 0 0 1 -2 -2V8a2 2 0 0 1 2 -2z" ...` | A banknote: the money itself, M0 and M2. |
| 359 | `Icons.SAFE` | `"M5 3h14a2 2 0 0 1 2 2v12a2 2 0 0 1 -2 2H5a2 2 0 0 1 -2 -2V5a2 2 0 0 1 2 -2z"...` | A safe on two feet, its dial and its handle: the city's fund. |
| 363 | `Icons.EXCHANGE` | `"M8 3L4 7l4 4 M4 7h16 M16 21l4-4-4-4 M20 17H4"` | Two arrows passing, one each way (Lucide's arrow-left-right): money changed from one currency to the other - the Trade tab's exchange (0.7.35). |
| 475 | `Icons.SETTINGS` | `"M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915" + "a2.34 2...` | A gear. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 45 | 439 | **type** `public final class Icons` | The rail's icons, as vector outlines - and since 0.7.24 the Build tab's, one per category, and the few the frame draws (the money block, the "Needs you" chip, the construction tab). |
| 47 | 1 | `private Icons()` |  |

### THE BUILD TAB'S AND THE FRAME'S (0.7.24) (lines 162-483)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 15 | `public static String ofGood(ham.citybuildersim.Good good)` | The icon of a good (0.7.35, the Trade tab's rows): its family's - FOOD for the fourteen foods and the restaurants' meals, ORE for iron ore, INDUSTRY for steel, fabricated steel and machinery, VEHICLES for cars and van... |
| 391 | 21 | `public static String ofSector(ham.citybuildersim.Sector sector)` | The icon of a sector (0.7.30): its Build category's, or one of the five above where the category is shared. |
| 414 | 19 | `public static String ofCategory(String name)` | The icon of a Build category, by its name (BuildAdvice's). |
| 441 | 11 | `public static String ofEpisode(String kind)` | The icon of a named episode's kind (0.7.37, City History's hard times): a recession or a depression the History chart's own line, a slump the out of work, an epidemic the cross, a financial crisis the bank, the money'... |
| 460 | 13 | `public static String ofDecision(String kind)` | The icon of a decision's kind (0.7.37, City History's decisions), as its flag is coloured by the area it is about: a tax the government's, a promise the policy scales, the central bank and the bank the bank's, the mon... |

