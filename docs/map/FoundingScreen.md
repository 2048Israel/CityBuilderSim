# FoundingScreen.java - 400 lines · 16 methods · 1 constants · interface

`ham/citybuildersim/ui/FoundingScreen.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

> Found a city: its name, its money, what the founders leave in the treasury and the vault, and the world it is founded into.
> 
> WHY THIS EXISTS (0.7.10). "Start New Game" founded the same city every time
> - Danzik, the Danzik dollar, D$2.5B and US$1B - and the one founding choice
> there was, the world's inflation, sat on Settings, where it did nothing
> until the next city. Jerus: "a small thing at the start of the game where
> you choose the name of the city and the currency ... and perhaps the
> settings to choose the starting cash and usd cash and offworld inflation
> rate ... (with option to just start at default)". So Start New Game comes
> here, and game.newGame() runs only when the player founds the city.
> 
> EVERY FIGURE IS THE MODEL'S. The currency's name, code and symbols are
> Currency.fromCityName() and Currency.typed(); the presets and the bounds on
> a custom founding are Founding's; what each buys is Game.whatItBuys(); the
> world's settled level is WorldEconomy.settledLevelAt(); and whether the
> city can be founded, and if not why, is Founding.problem(). The screen
> holds the player's half-made choices between redraws and nothing else.
> 
> TYPING DOES NOT REDRAW THE PAGE. The page is rebuilt when a chip or the tick
> changes it; a keystroke only refreshes the lines that depend on it - the
> currency, what a custom founding buys, and whether Found is lit and why not -
> so the field being typed in keeps its focus and its caret.

**Uses:** [Founding](Founding.md) (43), [Palette](Palette.md) (42), [Currency](Currency.md) (9), [Game](Game.md) (7), [WorldEconomy](WorldEconomy.md) (6), [UserInterface](UserInterface.md) (2), [DebtQuote](DebtQuote.md) (2)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 50 | · the choices, between redraws |
| 60 | · what a keystroke refreshes |
| 84 | THE PAGE |
| 98 | · · the name |
| 109 | · · the money |
| 142 | · · what the founders leave |
| 179 | · · the world |
| 193 | · · found it |
| 235 | WHAT A KEYSTROKE CHANGES |
| 336 | THE CHOICES, AS THE MODEL READS THEM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `FoundingScreen.SCREEN` | `"showFoundingScreen"` | This screen's name for clearMenu(), the key filter and isGameMenu(). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 52 | `private String cityName` |  |
| 53 | `private boolean ownCurrency` |  |
| 54 | `private String typedName` |  |
| 55 | `private Founding.Preset preset` |  |
| 57 | `private String customCash` | A custom founding, as typed: millions of the city's money, and millions of US dollars. |
| 58 | `private double mean` |  |
| 62 | `private Label currencyLine, whyNot` |  |
| 63 | `private VBox buysBox` |  |
| 64 | `private Button found` |  |
| 67 | `private DebtQuote[] dayZero` | An Insane city's day-0 quotes (Game.dayZeroQuotes()), kept while the world chosen is the one they were struck in: a scratch city is founded to strike them. |
| 68 | `private double dayZeroMean` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 361 | **type** `final class FoundingScreen` | Found a city: its name, its money, what the founders leave in the treasury and the vault, and the world it is founded into. |
| 48 | 1 | `FoundingScreen(UserInterface ui)` |  |

### the choices, between redraws (lines 50-59)

### what a keystroke refreshes (lines 60-83)

| line | len | member | says |
|---:|---:|---|---|
| 71 | 12 | `void show()` | From the main menu: a fresh page on the defaults, whatever was typed last time. |

### THE PAGE (lines 84-234)

| line | len | member | says |
|---:|---:|---|---|
| 87 | 133 | `void draw()` |  |
| 222 | 12 | `private Button worldChip(double m)` | One choice of world, shown as what it is. |

### WHAT A KEYSTROKE CHANGES (lines 235-335)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 44 | `private void refresh()` |  |
| 290 | 35 | `private void insaneBuys(String here)` | WHAT INSANE BUYS (0.7.14): nothing in cash. |
| 327 | 8 | `private static String village()` | "60 houses, 5 convenience stores, ..." - the village, from the model's own list. |

### THE CHOICES, AS THE MODEL READS THEM (lines 336-400)

| line | len | member | says |
|---:|---:|---|---|
| 341 | 4 | `private Currency currency()` | The money as chosen, or null when a typed pair does not pass. |
| 347 | 3 | `private double cash()` | The treasury in thousands, or NaN when a custom figure will not parse. |
| 352 | 3 | `private double reserveUsd()` | The vault in thousands of US dollars, or NaN. |
| 357 | 13 | `private String problem()` | Why Found is not lit, or null: the model's reason, or the screen's when a field will not parse. |
| 372 | 4 | `private Founding choices()` | The founding as chosen. |
| 378 | 5 | `boolean foundIfReady()` | Found it if it can be - Found's button and the Enter key. |
| 384 | 8 | `private static double millions(String typed)` |  |
| 393 | 3 | `private static String trim(double v)` |  |
| 397 | 3 | `private static String percent(double m)` |  |

