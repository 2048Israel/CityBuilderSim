# EssentialWorksCheck.java - 113 lines · 7 methods · 3 constants · harnesses

`ham/citybuildersim/EssentialWorksCheck.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> Essential works ordered for their unmet service, including their ground (HW1).
> WHY. Highways waited behind street sites at the twelve-month cap; schools
> and police waited for a payback that the service itself must never face.
> Fixtures cause short highways with and without ground, a long queue, an
> unaffordable preferred street, and schools that cannot pay their upkeep.

**Uses:** [BuildAdvice](BuildAdvice.md) (15), [AutoBuilder](AutoBuilder.md) (14), [Game](Game.md) (5), [ChildcareCheck](ChildcareCheck.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [CityNeeds](CityNeeds.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [LandManager](LandManager.md) (1), [EducationType](EducationType.md) (1), [SafetyType](SafetyType.md) (1), [Crime](Crime.md) (1), [TempDirs](TempDirs.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 18 | `EssentialWorksCheck.out` | `System.out` |  |
| 19 | `EssentialWorksCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 20 | `EssentialWorksCheck.HIGHWAYS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 98 | **type** `public final class EssentialWorksCheck` | Essential works ordered for their unmet service, including their ground (HW1). |
| 22 | 4 | `static void check(String label, boolean ok)` |  |
| 27 | 5 | `static void quietly(Runnable run)` |  |
| 33 | 7 | `static Game town(Path root, String name)` |  |
| 41 | 3 | `static AutoBuilder.Step step(Game g, BuildAdvice.Measure m)` |  |
| 45 | 25 | `static void highways(Path root, boolean ground)` |  |
| 71 | 33 | `static void schoolsAndPolice(Path root)` |  |
| 105 | 8 | `public static void main(String[] args) throws Exception` |  |

