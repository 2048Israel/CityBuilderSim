# SectorBooksCheck.java - 256 lines · 4 methods · 1 constants · harnesses

`ham/citybuildersim/SectorBooksCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> Plays a city and audits every sector's statements, every month. Not part of
> the game.
> 
> WHAT THIS IS FOR. BooksCheck already proves the food industry's statement is
> right against hand arithmetic on one fixture. This proves something different
> and, for a screen, more important: that the SAME three statements hold for
> all six sectors, on a city that is actually running, for a hundred months
> together - because a reporting layer that is correct in isolation and wrong
> on a live city is a reporting layer that lies to the player.
> 
> The three things it will not let past:
> 
>   1. ASSETS = LIABILITIES + EQUITY. It is a plug in this model, so this
>      catches arithmetic rather than accounting - but a plug that does not
>      plug means a figure moved between being read and being used.
> 
>   2. THE INCOME STATEMENT ADDS UP, top to bottom: revenue less the operating
>      lines is operating income, less property tax, interest and sales tax is
>      pre-tax, less profit tax is what the sector kept. Every line on the
>      screen is one of these, so a break here is a screen that does not foot.
> 
>      The sales tax joined that chain on 2026-09-09. It used to be remitted
>      out of cash and appear on no statement, so this harness had to carry it
>      as a cash-flow movement to make the month close - and the screen printed
>      a red block saying the profit above it was overstated by that much,
>      because it was.
> 
>   3. THE CASH FLOW CLOSES. Opening cash plus what it kept plus what it
>      borrowed less what it repaid plus what the city paid in equals closing
>      cash. This is the one that would actually catch a bug: if any other part
>      of the game moves a sector's cash, the residual appears here first.

**Uses:** [Sector](Sector.md) (9), [Sectors](Sectors.md) (7), [Game](Game.md) (5), [SectorBooks](SectorBooks.md) (5), [GameFiles](GameFiles.md) (1), [Statement](Statement.md) (1)

## Sections

| line | section |
|---:|---|
| 45 | 4. AND EVERY FIGURE ON THE PAGE IS A FIGURE (2026-09-16) |
| 120 | · · the sheet |
| 124 | · · the income statement |
| 135 | · · the cash flow |
| 211 | · AND IT HAS TO SURVIVE A SAVE. |
| 243 | · · and the month after |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `SectorBooksCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `static int fails` |  |
| 41 | `static int months` |  |
| 42 | `static int statements` |  |
| 43 | `static int screenLines` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 219 | **type** `public class SectorBooksCheck` | Plays a city and audits every sector's statements, every month. |

### 4. AND EVERY FIGURE ON THE PAGE IS A FIGURE (2026-09-16) (lines 45-256)

| line | len | member | says |
|---:|---:|---|---|
| 61 | 10 | `static boolean isAFigure(String value)` |  |
| 72 | 14 | `static void pageIsReadable(Game game)` |  |
| 90 | 9 | `static void near(String what, String sector, int month, double actual, double expected)` |  |
| 100 | 156 | `public static void main(String[] args)` |  |

