# FoundingScreen.java - 444 lines · 17 methods · 3 constants · interface

`ham/citybuildersim/ui/FoundingScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> Found a city: its name, its money, what the founders leave in the treasury and the vault, and the world it is founded into.
> 
> WHY THIS EXISTS (0.7.10). "Start New Game" founded the same city every time
> - Danzik, the Danzik dollar, D$2.5B and US$1B - and the one founding choice
> there was, the world's inflation, sat on Settings, where it did nothing
> until the next city. Jerus: "a small thing at the start of the game where
> you choose the name of the city and the currency ... and perhaps the
> settings to choose the starting cash and usd cash and offworld inflation
> rate ... (with option to just start at default)". So Start New Game - New
> city since 0.7.21 - comes here, and game.newGame() runs only when the
> player founds the city.
> 
> EVERY FIGURE IS THE MODEL'S. The currency's name, code and symbols are
> Currency.fromCityName() and Currency.typed(); the presets and the bounds on
> a custom founding are Founding's; the world's settled level is
> WorldEconomy.settledLevelAt(); and whether the city can be founded, and if
> not why, is Founding.problem(). The screen holds the player's half-made
> choices between redraws and nothing else.
> 
> AND IT NO LONGER SAYS WHAT THE MONEY BUYS (0.7.20). It listed "...the
> founding village" and its houses, "...and what is left", and the first
> works "in cash" or "a bond for" (Game.whatItBuys()) - and the village is
> the playtest's, placed by hand before its advisor takes over, which the
> game never builds for a player: Jerus's play-through found a treasury of
> $100.0M and one house. Jerus: "even the screen shouldnt say what the money
> could buy, the start screen should be real simple". Each choice shows its
> treasury and its vault, and nothing about spending them.
> Founding.whatItBuys() stays in the model: the harnesses use it
> (NewGameCheck, FundCheck, ReadPathCheck, LongPlaytest). So does
> Game.dayZeroQuotes(), which only this screen called and nothing calls now.
> 
> REAL SIMPLE (0.7.21). Jerus: "the start screen should be real simple". The
> page is the mockups' (Found.dc.html): one panel in the middle of the window
> over the main menu's backdrop, dimmed - the name, one line for its money,
> four cards for what it starts with, a row of five for the world, and Found
> and Back. Each card says how hard the start is, never what the money
> buys; the currency's fields and Custom's open in place from a link; the
> long explanations sit behind an (i) or a tooltip. "Found with defaults"
> went, because the defaults are what the page opens on. It fits a window
> 768 pixels high and scrolls in a smaller one.
> 
> TYPING DOES NOT REDRAW THE PAGE. The page is rebuilt when a card, a link or
> a choice of world changes it; a keystroke only refreshes the lines that
> depend on it - the currency, and whether Found is lit and why not - so the
> field being typed in keeps its focus and its caret.

**Uses:** [Palette](Palette.md) (68), [Founding](Founding.md) (32), [Currency](Currency.md) (12), [WorldEconomy](WorldEconomy.md) (5), [UserInterface](UserInterface.md) (2)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 76 | · the choices, between redraws |
| 86 | · what a keystroke refreshes |
| 117 | THE PAGE |
| 126 | · · the name |
| 174 | · · what it starts with |
| 210 | · · the world |
| 227 | · · found it |
| 361 | WHAT A KEYSTROKE CHANGES |
| 380 | THE CHOICES, AS THE MODEL READS THEM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 69 | `FoundingScreen.SCREEN` | `"showFoundingScreen"` | This screen's name for clearMenu(), the key filter and isGameMenu(). |
| 112 | `FoundingScreen.PANEL` | `760` | The panel's width, as the mockups draw it. |
| 115 | `FoundingScreen.PANEL_PAD` | `40` | The panel's padding, left and right: what the cards and fields share is the rest. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 78 | `private String cityName` |  |
| 79 | `private boolean ownCurrency` |  |
| 80 | `private String typedName` |  |
| 81 | `private Founding.Preset preset` |  |
| 83 | `private String customCash` | A custom founding, as typed: millions of the city's money, and millions of US dollars. |
| 84 | `private double mean` |  |
| 88 | `private Label currencyLine, whyNot` |  |
| 89 | `private Button found` |  |
| 90 | `private TextField nameField` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 66 | 379 | **type** `final class FoundingScreen` | Found a city: its name, its money, what the founders leave in the treasury and the vault, and the world it is founded into. |
| 74 | 1 | `FoundingScreen(UserInterface ui)` |  |

### the choices, between redraws (lines 76-85)

### what a keystroke refreshes (lines 86-116)

| line | len | member | says |
|---:|---:|---|---|
| 93 | 17 | `void show()` | From the main menu: a fresh page on the defaults, whatever was typed last time. |

### THE PAGE (lines 117-360)

| line | len | member | says |
|---:|---:|---|---|
| 120 | 159 | `void draw()` |  |
| 281 | 5 | `private static Label caption(String text)` | A section's caption over its controls, as the mockups set it. |
| 293 | 48 | `private VBox presetCard(Founding.Preset p)` | One of the four starts, as a card: its name, its money, and one line on how hard it is - never on what the money buys (Jerus: "even the screen shouldnt say what the money could buy"). |
| 343 | 3 | `private static String compact(String amount)` | A whole amount without its ".0": "D$100.0M" is "D$100M" on a card, where the room is a card's. |
| 348 | 12 | `private Button worldChip(double m, boolean first, boolean last)` | One choice of world, as one of a row of five joined segments. |

### WHAT A KEYSTROKE CHANGES (lines 361-379)

| line | len | member | says |
|---:|---:|---|---|
| 364 | 15 | `private void refresh()` |  |

### THE CHOICES, AS THE MODEL READS THEM (lines 380-444)

| line | len | member | says |
|---:|---:|---|---|
| 385 | 4 | `private Currency currency()` | The money as chosen, or null when a typed pair does not pass. |
| 391 | 3 | `private double cash()` | The treasury in thousands, or NaN when a custom figure will not parse. |
| 396 | 3 | `private double reserveUsd()` | The vault in thousands of US dollars, or NaN. |
| 401 | 13 | `private String problem()` | Why Found is not lit, or null: the model's reason, or the screen's when a field will not parse. |
| 416 | 4 | `private Founding choices()` | The founding as chosen. |
| 422 | 5 | `boolean foundIfReady()` | Found it if it can be - Found's button and the Enter key. |
| 428 | 8 | `private static double millions(String typed)` |  |
| 437 | 3 | `private static String trim(double v)` |  |
| 441 | 3 | `private static String percent(double m)` |  |

