# FoundingScreen.java - 486 lines · 18 methods · 4 constants · interface

`ham/citybuildersim/ui/FoundingScreen.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> Found a city: its name, its money, what the founders leave in the treasury and the vault, the ground it stands on and the world it is founded into.
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
> 
> AND THE GROUND IT STANDS ON (0.7.56, batch J1a). A row for the World: the
> seed of the city's coast, lakes, river and ore (World, the project's
> spec-land.md), rolled when the page opens and again by the dice, or typed.
> Any whole number is a world and the same number is the same world; it
> cannot be changed later. The city's land stands on it since 0.7.57
> (CityLand).

**Uses:** [Palette](Palette.md) (76), [Founding](Founding.md) (38), [Currency](Currency.md) (12), [WorldEconomy](WorldEconomy.md) (5), [UserInterface](UserInterface.md) (2), [Icons](Icons.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 83 | · the choices, between redraws |
| 95 | · what a keystroke refreshes |
| 131 | THE PAGE |
| 140 | · · the name |
| 188 | · · what it starts with |
| 224 | · · the ground |
| 243 | · · the world |
| 260 | · · found it |
| 395 | WHAT A KEYSTROKE CHANGES |
| 414 | THE CHOICES, AS THE MODEL READS THEM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `FoundingScreen.SCREEN` | `"showFoundingScreen"` | This screen's name for clearMenu(), the key filter and isGameMenu(). |
| 123 | `FoundingScreen.PANEL` | `760` | The panel's width, as the mockups draw it. |
| 126 | `FoundingScreen.PANEL_PAD` | `40` | The panel's padding, left and right: what the cards and fields share is the rest. |
| 129 | `FoundingScreen.SEED_FIELD` | `200` | The World's field: room for a seed of nineteen digits and a sign in the figures' face at 14 px (0.7.56). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 85 | `private String cityName` |  |
| 86 | `private boolean ownCurrency` |  |
| 87 | `private String typedName` |  |
| 88 | `private Founding.Preset preset` |  |
| 90 | `private String customCash` | A custom founding, as typed: millions of the city's money, and millions of US dollars. |
| 91 | `private double mean` |  |
| 93 | `private String worldSeed` | The world's seed, as typed or rolled. |
| 97 | `private Label currencyLine, whyNot` |  |
| 98 | `private Button found` |  |
| 99 | `private TextField nameField` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 414 | **type** `final class FoundingScreen` | Found a city: its name, its money, what the founders leave in the treasury and the vault, the ground it stands on and the world it is founded into. |
| 81 | 1 | `FoundingScreen(UserInterface ui)` |  |

### the choices, between redraws (lines 83-94)

### what a keystroke refreshes (lines 95-130)

| line | len | member | says |
|---:|---:|---|---|
| 102 | 19 | `void show()` | From the main menu: a fresh page on the defaults, whatever was typed last time. |

### THE PAGE (lines 131-394)

| line | len | member | says |
|---:|---:|---|---|
| 134 | 179 | `void draw()` |  |
| 315 | 5 | `private static Label caption(String text)` | A section's caption over its controls, as the mockups set it. |
| 327 | 48 | `private VBox presetCard(Founding.Preset p)` | One of the four starts, as a card: its name, its money, and one line on how hard it is - never on what the money buys (Jerus: "even the screen shouldnt say what the money could buy"). |
| 377 | 3 | `private static String compact(String amount)` | A whole amount without its ".0": "D$100.0M" is "D$100M" on a card, where the room is a card's. |
| 382 | 12 | `private Button worldChip(double m, boolean first, boolean last)` | One choice of world, as one of a row of five joined segments. |

### WHAT A KEYSTROKE CHANGES (lines 395-413)

| line | len | member | says |
|---:|---:|---|---|
| 398 | 15 | `private void refresh()` |  |

### THE CHOICES, AS THE MODEL READS THEM (lines 414-486)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 4 | `private Currency currency()` | The money as chosen, or null when a typed pair does not pass. |
| 425 | 3 | `private double cash()` | The treasury in thousands, or NaN when a custom figure will not parse. |
| 430 | 3 | `private double reserveUsd()` | The vault in thousands of US dollars, or NaN. |
| 435 | 3 | `private Long seed()` | The world's seed as typed, or null when it is not a whole number. |
| 440 | 14 | `private String problem()` | Why Found is not lit, or null: the model's reason, or the screen's when a field will not parse. |
| 456 | 6 | `private Founding choices()` | The founding as chosen. |
| 464 | 5 | `boolean foundIfReady()` | Found it if it can be - Found's button and the Enter key. |
| 470 | 8 | `private static double millions(String typed)` |  |
| 479 | 3 | `private static String trim(double v)` |  |
| 483 | 3 | `private static String percent(double m)` |  |

