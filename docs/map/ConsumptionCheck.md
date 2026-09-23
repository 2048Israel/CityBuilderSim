# ConsumptionCheck.java - 342 lines · 4 methods · 1 constants · harnesses

`ham/citybuildersim/ConsumptionCheck.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> Verifies the consumption model against the two laws it is shaped to obey,
> and guards the data file against the Java.
> 
> WHY A HARNESS FOR SOMETHING NOTHING EATS YET. Every number in here will be
> load-bearing the month households start buying these goods, and hunger runs
> into sickness and sickness runs into mortality - so a wrong curve is not a
> wrong figure on a screen, it is a body count. The model is proved first and
> connected second, which is the only order that lets the ensemble say
> anything when it IS connected.
> 
> THREE OF THESE ASSERTIONS EXIST BECAUSE THE PROBE FOUND THE BUG FIRST, and
> each is written so the bug cannot come back: calories that fell as a
> household got richer, a basket that came back empty at five hundred times
> subsistence, and a quality score that said a famine diet of eighty per cent
> starch was better than a mixed one.

**Uses:** [Consumption](Consumption.md) (19), [FamilyStructure](FamilyStructure.md) (4), [Good](Good.md) (2)

## Sections

| line | section |
|---:|---|
| 49 | · 1. the file itself |
| 88 | · 2. Engel, both halves |
| 123 | · 3. Bennett |
| 143 | · 4. satiation |
| 157 | · 5. the time axis |
| 188 | · 6. diet quality is balance |
| 208 | · 7. a currency reform changes nothing |
| 241 | · 8. the OTHER currency |
| 281 | · 9. the file and the enum |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 38 | `ConsumptionCheck.LADDER` | `{ 1, 2, 5, 10, 20, 27, 50, 90, 200, 500, 2_000 }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 320 | **type** `public class ConsumptionCheck` | Verifies the consumption model against the two laws it is shaped to obey, and guards the data file against the Java. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 5 | `static void check(String label, double actual, double expected, double tol)` |  |
| 40 | 277 | `public static void main(String[] args)` |  |
| 319 | 23 | `static String scaledFile(Consumption c, double by)` | The file as it stands, with every price multiplied - a currency reform, on paper. |

