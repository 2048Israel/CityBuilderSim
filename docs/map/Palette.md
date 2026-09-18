# Palette.java - 355 lines · 6 methods · 57 constants · interface

`ham/citybuildersim/ui/Palette.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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
> anything; GOOD, WARN and BAD are the only colours that mean something, and a
> screen that uses them for decoration has spent the one signal it had. That is
> why there are five greys and three meanings: the greys do the layout, and the
> three are saved for when the city is actually telling you something.
> 
> A LABEL IS NEVER THE NEWS. In every row in this game the label on the left
> stays grey and the figure on the right takes the colour, because the figure
> is what changed. Colouring the label makes a row shout about its own
> existence.
> 
> NOT RETROFITTED IN ONE GO, on purpose. Rewriting five hundred literals in one
> pass is a change nobody can review and a diff that hides every real edit
> inside it. The screens adopt this as they are redone, one rail tab at a time.

**Used by (16):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [LandScreen](LandScreen.md), [Levers](Levers.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [Statement](Statement.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 39 | THE GROUNDS |
| 66 | THE EDGES |
| 79 | THE TEXT |
| 109 | THE THREE THAT MEAN SOMETHING |
| 144 | WHAT CAN BE PRESSED |
| 165 | THE CHART |
| 177 | THE CHART RAMPS |
| 237 | TYPE |
| 279 | SPACE |
| 296 | THE FIXED WIDTHS |
| 321 | THE THREE THINGS A SCREEN IS BUILT FROM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `Palette.STAGE` | `"#111a24"` | The middle of the window - the darkest thing, and the biggest. |
| 52 | `Palette.PANEL` | `"#1c262b"` | The four strips around it: both side panels, the date bar, the debt bar. |
| 55 | `Palette.RAISED` | `"#26343b"` | A block lifted off a panel: the date and cash boxes, an open section. |
| 58 | `Palette.PINNED` | `"#223038"` | The vitals block, and anything that should read as pinned rather than raised. |
| 61 | `Palette.FIELD` | `"#17212c"` | Inside a control, and inside the inbox: darker than the panel it sits on. |
| 64 | `Palette.CONTROL` | `"#22303c"` | A button at rest. |
| 71 | `Palette.EDGE` | `"#37474f"` | Panel against stage. |
| 74 | `Palette.HAIRLINE` | `"#33404b"` | A quieter rule: inside a panel, under a heading, around a control. |
| 77 | `Palette.CONTROL_EDGE` | `"#4a5c68"` | The border of a control that can be pressed. |
| 89 | `Palette.TEXT_MAX` | `"#ffffff"` | The date, and nothing else. |
| 92 | `Palette.TEXT_HEAD` | `"#eceff1"` | A screen title, a section heading, a figure that is the answer. |
| 95 | `Palette.TEXT_BODY` | `"#c3ccd3"` | Ordinary text and ordinary figures. |
| 98 | `Palette.TEXT_MUTED` | `"#8fa3b0"` | A caption, a unit, a subtitle, a note under a figure. |
| 101 | `Palette.TEXT_LABEL` | `"#78909c"` | The label on the left of a row. |
| 104 | `Palette.TEXT_FAINT` | `"#7d8f9c"` | Disabled, or a heading that is not the one you are on. |
| 107 | `Palette.TEXT_SPENT` | `"#6b7a84"` | Struck through, settled, over with - a resolved notice, an old entry. |
| 118 | `Palette.GOOD` | `"#5fd68a"` | It is going the right way. |
| 121 | `Palette.GOOD_MONEY` | `"#8fe0aa"` | Money going the right way - a little paler, because it is bigger type. |
| 124 | `Palette.WARN` | `"#ffb454"` | It is not wrong yet, and it will be. |
| 127 | `Palette.BAD` | `"#ff6b6b"` | It is costing the city something now. |
| 130 | `Palette.BAD_SOFT` | `"#ff9e9e"` | The same, in small type where full strength reads as shouting. |
| 133 | `Palette.BAD_TEXT` | `"#ffd9d4"` | Text sitting on the alert ground below. |
| 136 | `Palette.ALERT_GROUND` | `"#331d1d"` | Behind something wrong. |
| 139 | `Palette.ALERT_GROUND_LOUD` | `"#3b1f1f"` | Behind something wrong and unread. |
| 142 | `Palette.ALERT_EDGE` | `"#c0392b"` | The edge of either. |
| 154 | `Palette.ACCENT` | `"#5cb8ff"` | The active tab, a live link, the heading of an open section. |
| 157 | `Palette.ACCENT_FILL` | `"#2f6fa8"` | Filled: the primary button, the advance-a-month control. |
| 160 | `Palette.CONFIRM` | `"#2f7d52"` | Filled: the button that commits - pays, builds, sets the policy. |
| 163 | `Palette.CONFIRM_GROUND` | `"#13291d"` | Behind a confirmation that has already happened. |
| 172 | `Palette.SERIES` | `{ "#5cb8ff", "#ff6b6b", "#5fd68a", "#ffb454", "#ce93d8", "#4dd0e1", "#d4e157"...` |  |
| 203 | `Palette.REVENUE_RAMP` | `{ "#afd5fe", "#8dbef1", "#6aa6e4", "#468fd6", "#1577c8" }` | Money coming in. |
| 208 | `Palette.SPENDING_RAMP` | `{ "#efca9f", "#deaf78", "#cd954f", "#bc7a19", "#aa6000" }` | Money going out. |
| 227 | `Palette.LADDER` | `{ REVENUE_RAMP [ 0 ], REVENUE_RAMP [ 2 ], REVENUE_RAMP [ 4 ] }` | The three instruments on the maturity ladder, short to long. |
| 232 | `Palette.RAMP_REST` | `"#5c6b75"` | Everything too small to have its own step. |
| 235 | `Palette.RING` | `26` | How thick a donut's ring is drawn, and how wide the hole is. |
| 249 | `Palette.MONO` | `"'Courier New'"` | Figures. |
| 253 | `Palette.GLYPH` | `"'Segoe UI Symbol', 'Segoe UI', sans-serif"` | Glyphs - the rail, the envelope, the round buttons. |
| 256 | `Palette.SIZE_HEADLINE` | `28` | The date and the cash: the two figures readable from across the room. |
| 259 | `Palette.SIZE_TITLE` | `20` | A screen's title. |
| 262 | `Palette.SIZE_LEAD` | `17` | A figure that is the point of its panel. |
| 265 | `Palette.SIZE_SECTION` | `14` | A section heading inside a screen. |
| 268 | `Palette.SIZE_HEADING` | `12` | A panel's own heading. |
| 271 | `Palette.SIZE_BODY` | `11` | Body text, and the figure in a row. |
| 274 | `Palette.SIZE_LABEL` | `10` | The label in a row, and a button in a dense list. |
| 277 | `Palette.SIZE_CAPTION` | `9` | A caption under something, and a unit after something. |
| 285 | `Palette.GAP_TIGHT` | `4` |  |
| 286 | `Palette.GAP` | `8` |  |
| 287 | `Palette.GAP_LOOSE` | `12` |  |
| 288 | `Palette.GAP_SECTION` | `20` |  |
| 291 | `Palette.RADIUS` | `4` | Corner of a block, a chip, a control. |
| 294 | `Palette.RADIUS_TIGHT` | `3` | Corner of something small - a row, a badge. |
| 304 | `Palette.RAIL` | `46` | The navigation rail, on the city panel's ground. |
| 307 | `Palette.CITY_PANEL` | `290` | The city panel, not counting the rail. |
| 310 | `Palette.BUILD_PANEL` | `280` | The construction panel down the right. |
| 313 | `Palette.STRIP` | `72` | The strip under the stage holding the dome and the time controls. |
| 316 | `Palette.BUILD_ROW` | `400` | A building row, so the price column lines up down the list. |
| 319 | `Palette.INBOX` | `470` | The inbox, sized to the 62-character lines the notices are written at. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 321 | **type** `public final class Palette` | Every colour, size and spacing this game is allowed to use, in one place. |
| 37 | 1 | `private Palette()` |  |

### THE GROUNDS (lines 39-65)

### THE EDGES (lines 66-78)

### THE TEXT (lines 79-108)

### THE THREE THAT MEAN SOMETHING (lines 109-143)

### WHAT CAN BE PRESSED (lines 144-164)

### THE CHART (lines 165-176)

### THE CHART RAMPS (lines 177-236)

### TYPE (lines 237-278)

### SPACE (lines 279-295)

### THE FIXED WIDTHS (lines 296-320)

### THE THREE THINGS A SCREEN IS BUILT FROM (lines 321-355)

| line | len | member | says |
|---:|---:|---|---|
| 330 | 3 | `public static String fill(String colour)` | "-fx-text-fill: X;" |
| 335 | 4 | `public static String figure(int size, String colour)` | A figure: monospaced, at a size, in a colour. |
| 341 | 3 | `public static String words(int size, String colour)` | A word: the platform face, at a size, in a colour. |
| 346 | 3 | `public static String block(String ground)` | A block of content raised off its ground. |
| 351 | 4 | `public static String block(String ground, String edge)` | A block with an edge, for when it has to be told from its neighbour. |

