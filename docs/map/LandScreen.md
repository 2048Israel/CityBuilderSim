# LandScreen.java - 474 lines · 3 methods · 0 constants · interface

`ham/citybuildersim/ui/LandScreen.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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
> Split out of UserInterface on 2026-09-18: the two banners THE LAND OFFICE
> and THE STATEMENT (what was left of it once the statement primitives had
> gone to Statement.java - the plot tile) exactly as they were, the shell's
> members reached through ui. The shell only ever calls showLandMenu().

**Uses:** [Palette](Palette.md) (69), [LandParcel](LandParcel.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (3), [UserInterface](UserInterface.md) (2), [Currency](Currency.md) (2), [LandManager](LandManager.md) (1), [LandMarket](LandMarket.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Sectors](Sectors.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 41 | THE LAND OFFICE. |
| 79 | · HOW THE LAND IS PAID FOR (0.7.6) |
| 123 | · WHERE THE CITY STANDS |
| 154 | · WHAT IS ON OFFER |
| 242 | · AND HOW THE MARGIN WORKS |
| 297 | · · WHAT IS UNDER THE GROUND |
| 315 | · · WHO IS WAITING ON LAND |
| 341 | THE STATEMENT. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 441 | **type** `final class LandScreen` | The land office: how the city pays and the city's position across the top, the plots on the market as tiles you can compare - price per square foot against what the office charges outside, the best value and the riche... |
| 39 | 1 | `LandScreen(UserInterface ui)` |  |

### THE LAND OFFICE. (lines 41-340)

| line | len | member | says |
|---:|---:|---|---|
| 60 | 280 | `void showLandMenu()` |  |

### THE STATEMENT. (lines 341-474)

| line | len | member | says |
|---:|---:|---|---|
| 380 | 94 | `StackPane parcelTile(LandParcel parcel, double going, boolean best, boolean richest)` | One plot on the market. |

