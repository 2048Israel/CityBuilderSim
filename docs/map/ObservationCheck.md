# ObservationCheck.java - 126 lines · 4 methods · 0 constants · harnesses

`ham/citybuildersim/ObservationCheck.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> Holds the path-observation contract with a premise deliberately held off.
> 
> WHY (PO1): a soft premise must never make its unrun law pass, and nightly
> must retain the same miss without turning it into a correctness failure.

**Uses:** [Observation](Observation.md) (15), [AllChecks](AllChecks.md) (5), [PriceLevelCheck](PriceLevelCheck.md) (4), [TempDirs](TempDirs.md) (1)

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 110 | **type** `public final class ObservationCheck` | Holds the path-observation contract with a premise deliberately held off. |
| 20 | 4 | `static void check(String label, boolean ok)` |  |
| 26 | 20 | **type** `public static final class Fixture` | A miniature harness whose missed premise protects an unsafe dependent law. |
| 27 | 5 | **type** `static final class Delegate` _(in ObservationCheck.Fixture)_ |  |
| 28 | 3 | `static boolean paper()` _(in ObservationCheck.Fixture.Delegate)_ |  |
| 33 | 12 | `public static void main(String[] args)` _(in ObservationCheck.Fixture)_ |  |
| 47 | 79 | `public static void main(String[] args) throws Exception` |  |

