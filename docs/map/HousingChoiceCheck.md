# HousingChoiceCheck.java - 208 lines · 12 methods · 0 constants · harnesses

`ham/citybuildersim/HousingChoiceCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Caused cardinality, budget, preference, incumbent and fair-tie findings for typed housing.
> WHY (RES3): neither a pooled price nor a catalogue table proves who obtains an actual home.

**Uses:** [HousingChoice](HousingChoice.md) (90), [FamilyModel](FamilyModel.md) (3), [HousingInventory](HousingInventory.md) (1)

## Fields (state)

| line | field | says |
|---:|---|---|
| 10 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 9 | 200 | **type** `public final class HousingChoiceCheck` | Caused cardinality, budget, preference, incumbent and fair-tie findings for typed housing. |
| 11 | 1 | `static void check(String label, boolean yes)` |  |
| 12 | 1 | `static void near(String label, double a, double b)` |  |
| 13 | 1 | `static HousingChoice.Demand d(String key, double n, int size, int children, int tier, double budget)` |  |
| 14 | 2 | `static HousingChoice.Offer o(String key, double n, int size, double area, double quality, double garden, boolean accessible, do...` |  |
| 16 | 1 | `static double count(HousingChoice.Result r, String cell, String home)` |  |
| 17 | 1 | `static HousingChoice.Result clear(List<HousingChoice.Demand> d, List<HousingChoice.Offer> o)` |  |
| 18 | 16 | `static void retainedHalfLeases()` |  |
| 35 | 14 | `static void mixedContracts()` |  |
| 49 | 1 | `static List<HousingChoice.Lease> canonical(List<HousingChoice.Lease> leases)` |  |
| 50 | 41 | `static void paidExcessContracts()` |  |
| 91 | 27 | `static void fractionalExcessBook()` |  |
| 118 | 90 | `public static void main(String[] args)` |  |

