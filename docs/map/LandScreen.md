# LandScreen.java - 721 lines · 7 methods · 0 constants · interface

`ham/citybuildersim/ui/LandScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The land office: how the city pays and the city's position across the top,
> the plots on the market as tiles you can compare - price per square foot
> against what the office charges outside, the best value and the richest
> deposit flagged - and the economics of the margin underneath.
> 
> In US dollars since 0.7.6: every plot shows its dollar price and what that
> costs in local money at today's rate, and a chip pair at the top chooses
> whether the treasury converts cash for it (the default) or pays out of the
> vault.
> 
> Since 0.7.13 a plot's price is large in the money the toggle pays in, with
> the other beside it; its size reads in square kilometres rather than
> blocks; its button stays live and, short, opens the build screen's
> funding page sized to the gap (showLandFunding()); and a control above the
> cards buys the next N of them at once (nextPlots()).
> 
> Split out of UserInterface on 2026-09-18: the banners THE LAND OFFICE
> and THE STATEMENT (what was left of it once the statement primitives had
> gone to Statement.java - the plot tile) exactly as they were, the shell's
> members reached through ui. The shell only ever calls showLandMenu().

**Uses:** [Palette](Palette.md) (85), [Game](Game.md) (23), [LandParcel](LandParcel.md) (5), [LandManager](LandManager.md) (4), [DebtQuote](DebtQuote.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [UserInterface](UserInterface.md) (2), [Money](Money.md) (2), [LandMarket](LandMarket.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Sectors](Sectors.md) (1), [Rollover](Rollover.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 47 | THE LAND OFFICE. |
| 83 | · HOW THE LAND IS PAID FOR (0.7.6) |
| 128 | · WHERE THE CITY STANDS |
| 159 | · WHAT IS ON OFFER |
| 250 | · AND HOW THE MARGIN WORKS |
| 305 | · · WHAT IS UNDER THE GROUND |
| 323 | · · WHO IS WAITING ON LAND |
| 349 | BUY THE NEXT N PLOTS (0.7.13) |
| 413 | WHEN THE CITY IS SHORT (0.7.13) |
| 572 | THE STATEMENT. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 362 | `int nextCount` | How many plots the next-N control buys; kept across redraws, held inside 1..listed. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 682 | **type** `final class LandScreen` | The land office: how the city pays and the city's position across the top, the plots on the market as tiles you can compare - price per square foot against what the office charges outside, the best value and the riche... |
| 45 | 1 | `LandScreen(UserInterface ui)` |  |

### THE LAND OFFICE. (lines 47-348)

| line | len | member | says |
|---:|---:|---|---|
| 66 | 282 | `void showLandMenu()` |  |

### BUY THE NEXT N PLOTS (0.7.13) (lines 349-412)

| line | len | member | says |
|---:|---:|---|---|
| 364 | 38 | `HBox nextPlots()` |  |
| 404 | 8 | `void buyOrFund(java.util.List<Integer> ids)` | Buys these plots the way the toggle pays, or - the city short - opens the funding page for them. |

### WHEN THE CITY IS SHORT (0.7.13) (lines 413-571)

| line | len | member | says |
|---:|---:|---|---|
| 435 | 108 | `void showLandFunding(java.util.List<Integer> ids)` |  |
| 551 | 20 | `private void buyOnTheLoan(java.util.List<Integer> ids, String paper)` | The money is in: the plots are bought, each as its own button would buy it, and whatever the purchase answers is shown - the build screen's rule that a refusal nobody reads is a silent one (BuildScreen.buildOnTheLoan()). |

### THE STATEMENT. (lines 572-721)

| line | len | member | says |
|---:|---:|---|---|
| 612 | 109 | `StackPane parcelTile(LandParcel parcel, double going, boolean best, boolean richest)` | One plot on the market. |

