# LandScreen.java - 403 lines · 3 methods · 0 constants · interface

`ham/citybuildersim/ui/LandScreen.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The land office: the city's position across the top, the plots on the
> market as tiles you can compare - price per square foot against what the
> office charges outside, the best value and the richest deposit flagged - and
> the economics of the margin underneath.
> 
> Split out of UserInterface on 2026-09-18: the two banners THE LAND OFFICE
> and THE STATEMENT (what was left of it once the statement primitives had
> gone to Statement.java - the plot tile) exactly as they were, the shell's
> members reached through ui. The shell only ever calls showLandMenu().

**Uses:** [Palette](Palette.md) (59), [LandParcel](LandParcel.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (3), [UserInterface](UserInterface.md) (2), [LandManager](LandManager.md) (1), [LandMarket](LandMarket.md) (1), [Sectors](Sectors.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 36 | THE LAND OFFICE. |
| 71 | · WHERE THE CITY STANDS |
| 101 | · WHAT IS ON OFFER |
| 189 | · AND HOW THE MARGIN WORKS |
| 239 | · · WHAT IS UNDER THE GROUND |
| 257 | · · WHO IS WAITING ON LAND |
| 283 | THE STATEMENT. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 32 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 375 | **type** `final class LandScreen` | The land office: the city's position across the top, the plots on the market as tiles you can compare - price per square foot against what the office charges outside, the best value and the richest deposit flagged - a... |
| 34 | 1 | `LandScreen(UserInterface ui)` |  |

### THE LAND OFFICE. (lines 36-282)

| line | len | member | says |
|---:|---:|---|---|
| 55 | 227 | `void showLandMenu()` |  |

### THE STATEMENT. (lines 283-403)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 81 | `StackPane parcelTile(LandParcel parcel, double going, boolean best, boolean richest)` | One plot on the market. |

