# HouseholdDebtPanel.java - 132 lines · 6 methods · 0 constants · interface

`ham/citybuildersim/ui/HouseholdDebtPanel.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The debt survey on Household money, after HS3's stage page (HC1/HC2).

**Uses:** [DebtStatistics](DebtStatistics.md) (19), [Palette](Palette.md) (19), [Kept](Kept.md) (13), [PayTier](PayTier.md) (5), [PeopleScreen](PeopleScreen.md) (5), [Game](Game.md) (2)

**Used by (1):** [PeopleScreen](PeopleScreen.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `String tier` |  |
| 19 | `boolean adult, annual` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 116 | **type** `final class HouseholdDebtPanel` | The debt survey on Household money, after HS3's stage page (HC1/HC2). |
| 22 | 14 | `DebtStatistics.Snapshot snapshot(Game game)` | Read the selected population and year from the model's own statistics. |
| 37 | 9 | `static String stageName(DebtStatistics.Stage s)` |  |
| 48 | 35 | `VBox draw(Game game, Runnable redraw, boolean details)` | Cards lead with the share affected; the full comparison remains behind details. |
| 85 | 38 | `VBox table(DebtStatistics.Snapshot s)` | Each kind's amounts are among holders; the late rates name their denominator. |
| 124 | 6 | `private static Label heading(String text)` |  |
| 131 | 1 | `private static String pct(double value)` |  |

