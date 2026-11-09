# Palette.java - 734 lines · 27 methods · 75 constants · interface

`ham/citybuildersim/ui/Palette.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> Every colour, size and spacing this game is allowed to use, in one place.
> 
> WHY THIS EXISTS
> 
> There are about five hundred hex literals in UserInterface.java. They are not
> wrong - each was chosen against the thing next to it - but there is no way to
> answer "what colour is a warning" except by finding another warning and
> copying it, and no way to change one's mind about the answer except by
> finding all of them. Every screen redone from here reads its colours from
> this class, so the next forty screens agree with each other by construction
> rather than by my memory.
> 
> THE RULE THE WHOLE PALETTE IS BUILT ON
> 
> Colour is news. The grounds and the greys carry structure and never say
> anything; GOOD, WARN and BAD are the only colours that say how the city is
> doing, and a screen that uses them for decoration has spent the one signal
> it had. That is why there are greys and three meanings: the greys do the
> layout, and the three are saved for when the city is actually telling you
> something. Since 0.7.21 four more say WHERE you are - the area colours -
> and never how it is going; the two kinds never swap jobs.
> 
> A LABEL IS NEVER THE NEWS. In every row in this game the label on the left
> stays grey and the figure on the right takes the colour, because the figure
> is what changed. Colouring the label makes a row shout about its own
> existence.
> 
> NOT RETROFITTED IN ONE GO, on purpose. Rewriting five hundred literals in one
> pass is a change nobody can review and a diff that hides every real edit
> inside it. The screens adopt this as they are redone, one rail tab at a time.
> 
> THE 0.7.21 PALETTE. Jerus asked for the interface to be "less of a
> textbook, more colourful", saw the mockups and said "that is damn pretty,
> go for it". The values below are the mockups' (the project's
> playing-0-7-19-ui-notes.md, section 7; gen_common.py): a darker, bluer
> set of grounds, three greys for text, FOUR AREA COLOURS that say which
> part of the city a screen is about - on its rail button, its title's
> swatch, its header tile and a one-line chart - and the three verdicts,
> which still mean good, watch and bad and nothing else. The roles kept
> their names, so every screen that read them took the new values without
> an edit; the window's own literals moved here the same day.

**Used by (39):** [BankScreen](BankScreen.md), [Branding](Branding.md), [BuildScreen](BuildScreen.md), [CapChips](CapChips.md), [ChartFitBench](ChartFitBench.md), [ConstructionScreen](ConstructionScreen.md), [EarthData](EarthData.md), [FinancesScreen](FinancesScreen.md), [FoundingScreen](FoundingScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [IslandChip](IslandChip.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [Ladder](Ladder.md), [LandScreen](LandScreen.md), [Levers](Levers.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [PartnersPage](PartnersPage.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [RankingsScreen](RankingsScreen.md), [ReplayGraphLayer](ReplayGraphLayer.md), [ReplayMode](ReplayMode.md), [ResearchScreen](ResearchScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [Statement](Statement.md), [StatementView](StatementView.md), [SummaryScreen](SummaryScreen.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [VaultTable](VaultTable.md), [ZoningScreen](ZoningScreen.md)

## Sections

| line | section |
|---:|---|
| 52 | THE GROUNDS |
| 82 | THE EDGES |
| 95 | THE TEXT |
| 141 | THE THREE THAT MEAN SOMETHING |
| 179 | WHAT CAN BE PRESSED |
| 203 | THE FOUR AREAS (0.7.21) |
| 255 | THE CHART'S LINES (0.7.23) |
| 330 | THE CHART RAMPS |
| 406 | TYPE |
| 454 | SPACE |
| 471 | THE FIXED WIDTHS |
| 496 | THE THREE THINGS A SCREEN IS BUILT FROM |
| 546 | THE FACES (0.7.21) |
| 705 | · on a canvas (0.7.23): a Font, not CSS, for the charts |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 62 | `Palette.STAGE` | `"#0b1118"` | The middle of the window - the darkest thing, and the biggest. |
| 65 | `Palette.PANEL` | `"#101924"` | The strips around it: the two side panels (a drawer and an overlay since 0.7.24), the header, the rail and the construction tab. |
| 68 | `Palette.RAISED` | `"#152130"` | A block lifted off a panel: a card, a header tile, an open section. |
| 71 | `Palette.PINNED` | `"#1a2839"` | The vitals block, and anything that should read as pinned rather than raised - and a popover. |
| 74 | `Palette.FIELD` | `STAGE` | Inside a control, and inside the inbox: darker than the panel it sits on - the window's own ground. |
| 77 | `Palette.CONTROL` | `"#1a2839"` | A button at rest: the mockups' raised. |
| 80 | `Palette.HOVER` | `"#22334a"` | A button under the pointer: a step above CONTROL in its hue (the mockups draw no hover). |
| 87 | `Palette.EDGE` | `"#24354a"` | Panel against stage. |
| 90 | `Palette.HAIRLINE` | `EDGE` | A quieter rule: inside a panel, under a heading, around a control. |
| 93 | `Palette.CONTROL_EDGE` | `"#33475f"` | The border of a control that can be pressed: a step up from the line, in its hue (the mockups draw none). |
| 112 | `Palette.TEXT` | `"#e6edf3"` | Text: titles, figures, the date. |
| 115 | `Palette.TEXT_2` | `"#a9b8c9"` | Secondary: the label on the left of a row, a card's second line. |
| 118 | `Palette.TEXT_3` | `"#8496ab"` | Captions: a note, a unit, a lead under a title (at least 4.5:1 on the panel). |
| 121 | `Palette.TEXT_MAX` | `TEXT` | The date. |
| 124 | `Palette.TEXT_HEAD` | `TEXT` | A screen title, a section heading, a figure that is the answer. |
| 127 | `Palette.TEXT_BODY` | `TEXT` | Ordinary text and ordinary figures. |
| 130 | `Palette.TEXT_MUTED` | `TEXT_3` | A caption, a unit, a subtitle, a note under a figure. |
| 133 | `Palette.TEXT_LABEL` | `TEXT_2` | The label on the left of a row. |
| 136 | `Palette.TEXT_FAINT` | `TEXT_3` | Disabled, or a heading that is not the one you are on. |
| 139 | `Palette.TEXT_SPENT` | `"#5f7189"` | Struck through, settled, over with - a resolved notice, an old entry. |
| 150 | `Palette.GOOD` | `"#3fb950"` | It is going the right way: on target, covered, in surplus. |
| 153 | `Palette.GOOD_MONEY` | `GOOD` | Money going the right way - the same green since 0.7.21, which has one. |
| 156 | `Palette.WARN` | `"#e3b341"` | It is not wrong yet, and it will be - near a limit, or good news with a cost. |
| 159 | `Palette.BAD` | `"#f85149"` | It is costing the city something now: past a limit, overdrawn, unhoused, failing. |
| 162 | `Palette.ON_FILL` | `STAGE` | What sits on a verdict's fill - a play button, a rating, a confirm in the mockups' style. |
| 165 | `Palette.BAD_SOFT` | `"#ff9e9e"` | The same, in small type where full strength reads as shouting. |
| 168 | `Palette.BAD_TEXT` | `"#ffd9d4"` | Text sitting on the alert ground below. |
| 171 | `Palette.ALERT_GROUND` | `"#331d1d"` | Behind something wrong. |
| 174 | `Palette.ALERT_GROUND_LOUD` | `"#3b1f1f"` | Behind something wrong and unread. |
| 177 | `Palette.ALERT_EDGE` | `"#c0392b"` | The edge of either. |
| 189 | `Palette.ACCENT` | `"#5aa9ff"` | The active tab, a live link, the heading of an open section - the money blue, which the mockups' links are. |
| 192 | `Palette.ACCENT_FILL` | `"#2f6fa8"` | Filled: the primary button that carries white text. |
| 195 | `Palette.ACCENT_LIGHT` | `"#8cc4ff"` | The accent under the pointer: a link hovered, a slider's thumb (the mockups' link hover). |
| 198 | `Palette.CONFIRM` | `"#2f7d52"` | Filled: the button that commits - pays, builds, sets the policy. |
| 201 | `Palette.CONFIRM_GROUND` | `"#13291d"` | Behind a confirmation that has already happened. |
| 216 | `Palette.PEOPLE` | `"#2ec4b6"` | People and services: People, Services, Infrastructure. |
| 219 | `Palette.MONEY` | `"#5aa9ff"` | Money and policy: Government, Finances, the bank, Policy, History. |
| 222 | `Palette.BUSINESS` | `"#a78bfa"` | Business and trade: Sectors, Trade & the world. |
| 230 | `Palette.DISCOVERY` | `BUSINESS` | What research brought (R5): a discovery's flag on City History, a NEW building's mark and edge, the Research area's accents. |
| 233 | `Palette.BUILDING` | `"#f17cb0"` | Building and land: Build, the land office. |
| 242 | `Palette.CATEGORIES` | `{ MONEY, PEOPLE, BUILDING, BUSINESS, "#c9b68f" }` | Categories told apart (0.7.21): the Government's two rings, whose three biggest revenue slices were three steps of one blue and read as one. |
| 253 | `Palette.ORE` | `"#c9b68f"` | Ore (0.7.26): the land office's deposits - a plot's ORE tag, the sand stripe on the ground bar, the Ore card. |
| 278 | `Palette.MONEY_LIGHT` | `"#a9d1ff", MONEY_DARK = "#2f80d9"` | The money blue, a step lighter and a step darker: the money area's second and third lines. |
| 281 | `Palette.PEOPLE_LIGHT` | `"#93e2da", PEOPLE_DARK = "#1c968b"` | The people teal, lighter and darker. |
| 284 | `Palette.BUSINESS_LIGHT` | `"#d3c5fd", BUSINESS_DARK = "#8669e8"` | The business violet, lighter and darker. |
| 287 | `Palette.BUILDING_LIGHT` | `"#f9bcd7", BUILDING_DARK = "#cf5590"` | The building pink, lighter and darker. |
| 290 | `Palette.LINE_COLOURS` | `13` | How many lines one chart can draw before a colour repeats: the five CATEGORIES and two steps of each of the four areas. |
| 361 | `Palette.REVENUE_RAMP` | `{ "#afd5fe", "#8dbef1", "#6aa6e4", "#468fd6", "#1577c8" }` | Money coming in. |
| 366 | `Palette.SPENDING_RAMP` | `{ "#efca9f", "#deaf78", "#cd954f", "#bc7a19", "#aa6000" }` | Money going out. |
| 385 | `Palette.LADDER` | `{ REVENUE_RAMP [ 0 ], REVENUE_RAMP [ 2 ], REVENUE_RAMP [ 4 ] }` | The three instruments on the maturity ladder, short to long. |
| 396 | `Palette.GDP_LAYERS` | `{ REVENUE_RAMP [ 0 ], REVENUE_RAMP [ 2 ], REVENUE_RAMP [ 4 ] }` | GDP's three stacked layers on the Reports page (0.7.6) - consumption, investment, government, bottom to top - on the same three validated steps as the maturity ladder, and for the same reason: they are parts of one wh... |
| 401 | `Palette.RAMP_REST` | `"#5c6b75"` | Everything too small to have its own step. |
| 404 | `Palette.RING` | `26` | How thick a donut's ring is drawn, and how wide the hole is. |
| 434 | `Palette.SIZE_TITLE` | `20` | A screen's title. |
| 437 | `Palette.SIZE_LEAD` | `17` | A figure that is the point of its panel. |
| 440 | `Palette.SIZE_SECTION` | `14` | A section heading inside a screen. |
| 443 | `Palette.SIZE_HEADING` | `12` | A panel's own heading. |
| 446 | `Palette.SIZE_BODY` | `11` | Body text, and the figure in a row. |
| 449 | `Palette.SIZE_LABEL` | `10` | The label in a row, and a button in a dense list. |
| 452 | `Palette.SIZE_CAPTION` | `9` | A caption under something, and a unit after something. |
| 460 | `Palette.GAP_TIGHT` | `4` |  |
| 461 | `Palette.GAP` | `8` |  |
| 462 | `Palette.GAP_LOOSE` | `12` |  |
| 463 | `Palette.GAP_SECTION` | `20` |  |
| 466 | `Palette.RADIUS` | `4` | Corner of a block, a chip, a control. |
| 469 | `Palette.RADIUS_TIGHT` | `3` | Corner of something small - a row, a badge. |
| 479 | `Palette.RAIL` | `76` | The navigation rail, at the window's left edge: an icon over its name (0.7.21). |
| 482 | `Palette.HEADER` | `84` | The header across the top: the clock, the money block, five headline tiles, the "Needs you" chip, the rating and the inbox (0.7.21; the money block and the chip 0.7.24). |
| 485 | `Palette.CITY_PANEL` | `290` | The city panel, not counting the rail: the drawer over the stage's left since 0.7.24. |
| 488 | `Palette.BUILD_PANEL` | `280` | The construction panel down the right: over the stage while open, a tab while folded, since 0.7.24. |
| 491 | `Palette.BUILD_ROW` | `400` | A building row, so the price column lines up down the list. |
| 494 | `Palette.INBOX` | `470` | The inbox, sized to the 62-character lines the notices are written at. |
| 600 | `Palette.Fonts.FOLDER` | `"/fonts/"` | Where the files sit on the classpath. |
| 603 | `Palette.Fonts.SYSTEM_FACE` | `"System"` | The platform's own face, for words when Plex Sans did not load. |
| 606 | `Palette.Fonts.FIGURE_FACE` | `"Courier New"` | The face figures used before 0.7.21, and use again when Plex Mono did not load. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 608 | `private static String sans` |  |
| 609 | `private static String mono` |  |
| 610 | `private static boolean tried` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 687 | **type** `public final class Palette` | Every colour, size and spacing this game is allowed to use, in one place. |
| 50 | 1 | `private Palette()` |  |

### THE GROUNDS (lines 52-81)

### THE EDGES (lines 82-94)

### THE TEXT (lines 95-140)

### THE THREE THAT MEAN SOMETHING (lines 141-178)

### WHAT CAN BE PRESSED (lines 179-202)

### THE FOUR AREAS (0.7.21) (lines 203-254)

### THE CHART'S LINES (0.7.23) (lines 255-329)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 7 | `static String[] stepsOf(String area)` | An area's two further steps, lighter first; none for a colour that is not an area's. |
| 307 | 22 | `public static String[] lineColours(java.util.List<String> areas)` | Each line's colour, given each line's area in the order they are drawn: the area's own for the first of an area, then the CATEGORIES nobody on the chart has, then the area's lighter and darker steps, then any of the t... |

### THE CHART RAMPS (lines 330-405)

### TYPE (lines 406-453)

| line | len | member | says |
|---:|---:|---|---|
| 424 | 1 | `public static String mono()` | Figures. |

### SPACE (lines 454-470)

### THE FIXED WIDTHS (lines 471-495)

### THE THREE THINGS A SCREEN IS BUILT FROM (lines 496-545)

| line | len | member | says |
|---:|---:|---|---|
| 505 | 3 | `public static String fill(String colour)` | "-fx-text-fill: X;" |
| 514 | 4 | `public static String figure(int size, String colour)` | A figure: monospaced, at a size, in a colour - semibold since 0.7.21, as the mockups set their figures (Courier's was bold, and Plex Mono's bold is heavier than the figure needs). |
| 520 | 4 | `public static String figureRegular(int size, String colour)` | A figure at the regular weight: a table's cells, where a column of semibold is a wall. |
| 526 | 3 | `public static String strong(int size, String colour)` | Words at the semibold weight: a title, a tile's label (0.7.21). |
| 531 | 3 | `public static String words(int size, String colour)` | A word: the words' face, at a size, in a colour - Plex Sans from the stylesheet's root since 0.7.21, the platform's if it did not load. |
| 536 | 3 | `public static String block(String ground)` | A block of content raised off its ground. |
| 541 | 4 | `public static String block(String ground, String edge)` | A block with an edge, for when it has to be told from its neighbour. |

### THE FACES (0.7.21) (lines 546-734)

| line | len | member | says |
|---:|---:|---|---|
| 595 | 139 | **type** `public static final class Fonts` | The game's two typefaces, IBM Plex Sans for words and IBM Plex Mono for every figure, loaded from the jar once at start-up. |
| 597 | 1 | `private Fonts()` _(in Palette.Fonts)_ |  |
| 618 | 30 | `static synchronized void load()` _(in Palette.Fonts)_ | Loads the eight files and records the family JavaFX reports for each. |
| 650 | 17 | `private static String family(String file, StringBuilder said)` _(in Palette.Fonts)_ | One file: the family JavaFX reports for it, or null - and why, in the log line. |
| 669 | 1 | `static boolean loaded()` _(in Palette.Fonts)_ | Whether Plex Sans loaded; the words are the platform's face if not. |
| 672 | 1 | `static String sans()` _(in Palette.Fonts)_ | The family for words, quoted for CSS: "'IBM Plex Sans'", or the platform's. |
| 675 | 1 | `static String mono()` _(in Palette.Fonts)_ | The family for figures, quoted for CSS: "'IBM Plex Mono'", or Courier New. |
| 678 | 5 | `static String sansSemiBold()` _(in Palette.Fonts)_ | CSS for words at the semibold weight: the SmBld family at the normal weight, or bold of the words' face. |
| 685 | 4 | `static String sansMedium()` _(in Palette.Fonts)_ | CSS for words at the medium weight, or the regular face. |
| 691 | 5 | `static String monoSemiBold()` _(in Palette.Fonts)_ | CSS for figures at the semibold weight: the SmBld family at the normal weight, or bold of the figures' face. |
| 698 | 4 | `static String monoMedium()` _(in Palette.Fonts)_ | CSS for figures at the medium weight, or the regular face. |
| 703 | 1 | `private static String quoted(String family)` _(in Palette.Fonts)_ |  |
| 708 | 3 | `static javafx.scene.text.Font sansFont(double size)` _(in Palette.Fonts)_ | Words at this size, for a canvas: Plex Sans or the platform's. |
| 713 | 4 | `static javafx.scene.text.Font sansStrongFont(double size)` _(in Palette.Fonts)_ | Words at the semibold weight, for a canvas: the SmBld family, or bold of the words' face. |
| 719 | 3 | `static javafx.scene.text.Font monoFont(double size)` _(in Palette.Fonts)_ | Figures at this size, for a canvas: Plex Mono or Courier New. |
| 724 | 3 | `static javafx.scene.text.Font sansMediumFont(double size)` _(in Palette.Fonts)_ | Words at the medium weight, for a drawing (0.7.95, the refinery's pictogram): the Medium family, or the words' face. |
| 729 | 4 | `static javafx.scene.text.Font monoStrongFont(double size)` _(in Palette.Fonts)_ | Figures at the semibold weight, for a drawing (0.7.95): the SmBld family, or bold of the figures' face. |

