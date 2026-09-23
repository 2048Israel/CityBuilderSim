# BuildMenuCheck.java - 211 lines · 2 methods · 0 constants · harnesses

`ham/citybuildersim/BuildMenuCheck.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> Verifies that every building in the game can describe itself. Not part of the
> game.
> 
> WHY THIS EXISTS
> 
> The build menu's info card carries no written text at all - every sentence on
> it is derived from the building's own fields, so that no description can ever
> describe a building as it was two rebalances ago. That buys accuracy at the
> cost of a specific new failure: production1 is tonnes of ore in a mine,
> construction points in a depot, kilowatts in a power plant and units of food
> in a mill, and capacity is people in a house, shelf stock in a shop,
> warehouse space in a mill and treatments in a clinic. A card that reads the
> wrong one produces a confident sentence about the wrong number, which is
> worse than no sentence at all - and it produces it silently, because the
> string still formats.
> 
> So this prints the description of all 29 buildings, which is how a person
> checks the mapping, and asserts the things a person would not notice: that
> every building says SOMETHING, that no sentence carries a null or a NaN, and
> that every JobType has a label. The last one is the trap with a fuse on it -
> add a JobType and the card silently falls back to printing the enum constant
> at a player.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (4), [Game](Game.md) (2), [UserInterface](UserInterface.md) (2), [CareType](CareType.md) (2), [JobType](JobType.md) (2), [GameFiles](GameFiles.md) (1), [BuildingManager](BuildingManager.md) (1), [BuildingType](BuildingType.md) (1)

## Sections

| line | section |
|---:|---|
| 99 | · · EVERY JOB TYPE HAS A NAME |
| 128 | · · THE TWO PRICES AGREE WITH THE TILL |
| 163 | · · THE RECEIPT SERIAL COUNTS |

## Fields (state)

| line | field | says |
|---:|---|---|
| 33 | `private static int failures` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 31 | 181 | **type** `public class BuildMenuCheck` | Verifies that every building in the game can describe itself. |
| 35 | 6 | `private static void check(boolean condition, String what)` |  |
| 42 | 169 | `public static void main(String[] args)` |  |

