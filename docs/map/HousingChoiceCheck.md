# HousingChoiceCheck.java - 104 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/HousingChoiceCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> Caused cardinality, budget, preference, incumbent and fair-tie findings for typed housing.
> WHY (RES3): neither a pooled price nor a catalogue table proves who obtains an actual home.

**Uses:** [HousingChoice](HousingChoice.md) (26), [HousingInventory](HousingInventory.md) (1), [FamilyModel](FamilyModel.md) (1)

## Fields (state)

| line | field | says |
|---:|---|---|
| 10 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 9 | 96 | **type** `public final class HousingChoiceCheck` | Caused cardinality, budget, preference, incumbent and fair-tie findings for typed housing. |
| 11 | 1 | `static void check(String label, boolean yes)` |  |
| 12 | 1 | `static void near(String label, double a, double b)` |  |
| 13 | 1 | `static HousingChoice.Demand d(String key, double n, int size, int children, int tier, double budget)` |  |
| 14 | 2 | `static HousingChoice.Offer o(String key, double n, int size, double area, double quality, double garden, boolean accessible, do...` |  |
| 16 | 1 | `static double count(HousingChoice.Result r, String cell, String home)` |  |
| 17 | 1 | `static HousingChoice.Result clear(List<HousingChoice.Demand> d, List<HousingChoice.Offer> o)` |  |
| 18 | 86 | `public static void main(String[] args)` |  |

