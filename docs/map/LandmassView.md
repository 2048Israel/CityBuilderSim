# LandmassView.java - 515 lines · 37 methods · 18 constants · model

`ham/citybuildersim/LandmassView.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> What the screens and the investors' words say of the city's landmasses: pure functions of the model's figures, so that
> the words are written in one place and a harness can rebuild them from getters (ET5, 2026-10-25).
> 
> WHY THIS EXISTS. A screen must not recompute a model figure, and a refusal the investors write ("no land - needs ...")
> and the Build card's gate must say the same thing about ground across the water in the same words. LandClassView is
> the shape: a pure class beside the model that the interface calls. Slice A of ET5 put here the two things the model's
> own words and the pickers need - the investors' clause and the ISLAND tag's share; slice D adds the rest, so that
> every sentence the interface writes about a landmass is here and ham.citybuildersim.LandmassCheck (section 11) can
> hold each of them without the toolkit: the LANDMASSES table's heads, widths and rows, an offer's line, the strip's
> lines, the map's chip, hover and legend, the notice a landmass raises and its button, the port card's three link
> sentences, the receipt's clause, the refusals' words and the playtest's report line. Every figure is a model getter's
> (Landmasses, LandManager, LandParcel); nothing here is worked out from a screen's own state. While the city owns
> ground on one landmass nothing here has anything to say, and each function answers "" or null or false.

**Uses:** [Landmasses](Landmasses.md) (36), [LandManager](LandManager.md) (15), [Inbox](Inbox.md) (8), [LandParcel](LandParcel.md) (6), [LandMap](LandMap.md) (5), [Ports](Ports.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [World](World.md) (1)

**Used by (12):** [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [Game](Game.md), [Inbox](Inbox.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [MapView](MapView.md), [ReadPathCheck](ReadPathCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 45 | NAMES AND SMALL WORDS (ET5 slice D) |
| 109 | THE LANDMASSES TABLE (the land office, between THE GROUND and WHAT THE GROUND IS WORTH) |
| 206 | THE OFFERS, THE STRIP AND THE RECEIPT |
| 291 | THE MAP |
| 306 | THE PORT CARD: where, and what it links |
| 377 | THE REFUSALS |
| 433 | THE NOTICE |
| 492 | THE PLAYTEST'S REPORT |

## Enum constants

| line | constant | says |
|---:|---|---|
| 132 | `LandmassView.Tone.PLAIN` |  |
| 132 | `LandmassView.Tone.GOOD` |  |
| 132 | `LandmassView.Tone.WARN` |  |
| 132 | `LandmassView.Tone.MUTED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 29 | `LandmassView.ISLAND_TAG_SHARE` | `0.5` | The share of an offer's dry ground that must lie on landmasses the city has not linked for the land office to tag the offer ISLAND (before BEST VALUE and the rest): half. |
| 50 | `LandmassView.ISLAND_TAG` | `"ISLAND"` | The tag the land office puts on an offer that is mostly ground no port links. |
| 53 | `LandmassView.NOT_LINKED_CLAUSE` | `"which no port links"` | The words for ground on a landmass no port links, as a clause: "which no port links". |
| 56 | `LandmassView.LEGEND_WORDS` | `"Not linked: only a port"` | The map's legend entry for the hatch over ground no port links. |
| 59 | `LandmassView.ONLY_A_PORT` | `"only a port can be built there until it has one"` | What only a port can do on ground no port links, as a sentence's end: "only a port can be built there until it has one". |
| 114 | `LandmassView.TABLE_TITLE` | `"LANDMASSES"` | The table's section head. |
| 117 | `LandmassView.TABLE_HINT` | `"where the city's ground is · only a port can be built where none links"` | The table's hint, at the right of its head. |
| 120 | `LandmassView.TABLE_MIN_ROWS` | `2` | The table is drawn for a city that owns ground on this many landmasses or more (home counts): 2. |
| 123 | `LandmassView.HEADS` | `{ "landmass", "dry owned", "free", "ports", "linked" }` | The table's heads, in its columns' order. |
| 126 | `LandmassView.WIDTHS` | `{ 0, 92, 230, 190, 290 }` | The columns' widths in pixels (the first, 0, takes the rest). |
| 129 | `LandmassView.RIGHT` | `{ false, true, false, false, false }` | Which columns' cells lie to the right. |
| 276 | `LandmassView.SEGMENT_KEY` | `"free, no port links it"` | The key's words for that segment. |
| 311 | `LandmassView.CHOOSER_LABEL` | `"Where"` | The chooser's label, before its chips. |
| 396 | `LandmassView.NO_SHORE_WAY` | `"own some sea in front of dry shore, on the landmass it serves: the Land offi...` | ...and the way out under it. |
| 431 | `LandmassView.PORT_DOOR` | `"Build a port there ›"` | The label of the door from the refusal to the port card. |
| 438 | `LandmassView.NOTICE_KEY` | `"island:"` | A standing notice's key prefix: "island:" and the landmass's id. |
| 456 | `LandmassView.NOTICE_BUTTON` | `PORT_DOOR` | The button on that notice. |
| 459 | `LandmassView.NOTICE_BUTTON_NO_SHORE` | `"Go to the Land office ›"` | The button on that notice where the city owns no shore on the landmass: a terminal cannot stand there yet, and sea is bought at the Land office. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 492 | **type** `public final class LandmassView` | What the screens and the investors' words say of the city's landmasses: pure functions of the model's figures, so that the words are written in one place and a harness can rebuild them from getters (ET5, 2026-10-25). |
| 26 | 1 | `private LandmassView()` |  |
| 32 | 3 | `public static boolean islandTagged(LandParcel p)` | Whether the land office tags an offer ISLAND: it has dry ground on a landmass the city has not linked, and at least ISLAND_TAG_SHARE of its dry ground is such. |
| 41 | 3 | `public static String unlinkedWords(double freeSqFt, String names)` | The clause the investors' land refusals end with when the city owns ground it cannot build on: "12.0 km² is free on Island 2, which no port links". |

### NAMES AND SMALL WORDS (ET5 slice D) (lines 45-108)

| line | len | member | says |
|---:|---:|---|---|
| 65 | 6 | `public static String landmassName(Landmasses lms, int id)` | A landmass's name for a sentence: "Home", the data's name as the founding card writes it, "Island 2" - from the rows the city owns - and "another landmass" for one it owns nothing on (an offer's ground the city has no... |
| 73 | 5 | `public static boolean hasPort(Landmasses lms, int id)` | Whether a landmass has a port standing on it: some terminal of any kind (Landmasses.portsOn()). |
| 80 | 5 | `public static int terminalsOn(Landmasses lms, int id)` | The terminals standing on a landmass, all kinds together. |
| 90 | 6 | `public static String linkedPortName(Landmasses lms)` | The linked landmass that has a port standing - what makes a port on another landmass link it (Landmasses.closure()) - named for a sentence: "home" for home's, else the island's name; null when no linked landmass has one. |
| 98 | 10 | `public static String ordinal(int n)` | "2nd", "3rd", "4th", "11th", "21st": a count as an ordinal. |

### THE LANDMASSES TABLE (the land office, between THE GROUND and WHAT THE GROUND IS WORTH) (lines 109-205)

| line | len | member | says |
|---:|---:|---|---|
| 132 | 1 | **type** `public enum Tone` | A cell's tone: the screen maps it to its colour. |
| 147 | 2 | **type** `public record Line(int id, String name, String dry, String free, Tone freeTone, String ports, String linked...` | One landmass as a table row, worded. |
| 151 | 3 | `public static boolean shown(Landmasses lms)` | Whether the table is drawn: the city owns ground on TABLE_MIN_ROWS landmasses or more. |
| 159 | 20 | `public static List<Line> rows(LandManager land)` | The table's rows, home first: from the landmasses' own rows (Landmasses.rows()) and the free ground the strip shows (LandManager.getAvailableSqFt()), the linked landmasses sharing that one figure. |
| 181 | 11 | `public static String portsWords(Landmasses lms, int id)` | The terminals standing on a landmass by kind, "1 general cargo, 2 containers", or "none". |
| 199 | 6 | `public static String needsWords(Landmasses lms, int id)` | Why a landmass is not linked, as the table's cell and the map's chip say it: "no: needs a port here (home has one)" where a linked landmass has a port already, "no: needs a port here and one at home" where none has, "... |

### THE OFFERS, THE STRIP AND THE RECEIPT (lines 206-290)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 7 | `public static String offerLine(LandParcel p, Landmasses lms)` | The line the land office writes under an offer that has dry ground on a landmass no port links, beside LandClassView.offerLine(): "On Island 2, which no port links: only a port can be built there until it has one" for... |
| 229 | 15 | `public static String picksLine(List<LandParcel> picks, Landmasses lms)` | ...for the map card of several picks (offerLine() is one offer's): "0.842 km² of their dry ground is on Island 2, which no port links: only a port can be built there until it has one", or "Their dry ground is on ..." ... |
| 249 | 4 | `public static double roomSqFt(LandParcel p)` | What an offer adds to the ground the city can build on, square feet: its size less its dry ground on landmasses no port links (LandParcel.getUnlinkedDryKm2()); its size itself where it has none, to the bit. |
| 258 | 10 | `public static List<String> unlinkedLines(LandManager land)` | The strip's lines under THE GROUND's figures, one for each landmass no port links: "Island 2: 12.0 km² owned, 11.8 km² free, no port links it". |
| 270 | 4 | `public static String segmentWords(LandManager land)` | The tooltip of the bar's striped segment for the ground free on landmasses no port links. |
| 283 | 7 | `public static String receiptWords(LandParcel p, Landmasses lms)` | The clause a purchase's receipt ends with when it bought ground on a landmass no port links: " - on Island 2, which no port links" (" - 0.842 km² of it on Island 2, ..." when only part of it lay there). |

### THE MAP (lines 291-305)

| line | len | member | says |
|---:|---:|---|---|
| 296 | 4 | `public static String chipWords(Landmasses lms, LandMap.Chip chip)` | The map's chip on a landmass no port links: "Island 2 · 12.0 km² · not linked: needs a port here (home has one)". |
| 302 | 3 | `public static String hoverWords(Landmasses lms, int id)` | The hover card's line for a plot of owned or offered ground on a landmass no port links: "On Island 2, which no port links: only a port can be built here". |

### THE PORT CARD: where, and what it links (lines 306-376)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 3 | `public static boolean chooserShown(List<Landmasses.Row> choices)` | Whether the card shows the chooser for these choices (Game.portLandmassChoices()): there is something to choose between, or the one landmass with shore is not home (an island's terminal is chosen, never the rule's). |
| 323 | 5 | `public static String[] choiceNames(List<Landmasses.Row> choices)` | The names on the chooser's chips, in Game.portLandmassChoices()' order: an island no port links says so. |
| 330 | 5 | `public static String currentChoice(List<Landmasses.Row> choices, int landmass)` | The chip that stands for the landmass the order would stand on now (Landmasses.portLandmass()), or null for none among the choices. |
| 342 | 15 | `public static String linkWords(Landmasses lms, int landmass)` | What a sea terminal on the landmass it is ordered for would link, as the card says it (D10). |
| 358 | 4 | `private static String joined(List<String> list)` |  |
| 367 | 9 | `public static BuildingsTemplate portFor(List<BuildingsTemplate> catalogue)` | The sea terminal a notice's "Build a port there" opens the card of: the first General Cargo terminal in the catalogue's order, else the first of any kind; null for a catalogue with none. |

### THE REFUSALS (lines 377-432)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 6 | `public static String noShoreTitle(Landmasses lms, boolean port, int chosen)` | What a sea terminal's NO_COAST says on the Build card's button once the city owns ground on more than one landmass: the shore has to be on the landmass it serves. |
| 399 | 6 | `public static String noShoreQuote(Landmasses lms, boolean port, int chosen)` | The quote line's words for the same refusal: "no shore on Island 2". |
| 407 | 4 | `public static String noCoastParagraph(Landmasses lms)` | The NO COAST page's extra paragraph for a city on more than one landmass: a terminal's shore is the landmass's own. |
| 413 | 3 | `public static String seaClause(Landmasses lms)` | The clause the sea terminal's description adds for a city on more than one landmass: ", in front of dry shore on the landmass it serves". |
| 421 | 8 | `public static List<String> noLandLines(LandManager land)` | The not-enough-land page's lines for ground the city owns and no port links: the clause the investors read, and where to go. |

### THE NOTICE (lines 433-491)

| line | len | member | says |
|---:|---:|---|---|
| 441 | 3 | `public static String noticeKey(int id)` | The key of the notice for a landmass. |
| 446 | 8 | `public static int noticeId(String key)` | The landmass a notice's key is for, or -1 for a key that is no island's. |
| 462 | 3 | `public static String noticeButton(Landmasses lms, int id)` | The button on the notice for a landmass: the port card's door where the city owns shore on it, else the Land office's. |
| 467 | 3 | `public static String noticeTitle(Landmasses lms, int id)` | A notice's title: "Island 2: owned, but no port links it". |
| 472 | 19 | `public static List<String> noticeBody(Landmasses lms, int id)` | A notice's body, in lines of at most Inbox.BODY_COLUMNS characters: what the city owns there, what that means, what links it. |

### THE PLAYTEST'S REPORT (lines 492-515)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 14 | `public static String reportLine(Landmasses lms)` | The long playtest's line after ITS GROUND AT THE END: the city's dry ground by landmass and the terminals standing off home - "ITS LANDMASSES AT THE END: Home 177.7 km², Island 2 3.1 km2 (not linked); terminals off ho... |

