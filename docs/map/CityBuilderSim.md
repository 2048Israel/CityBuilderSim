# CityBuilderSim.java - 55 lines · 2 methods · 0 constants · interface

`ham/citybuildersim/CityBuilderSim.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The way in.
> 
> Deliberately does NOT extend Application, and that is load-bearing rather than
> a style choice: the JVM launcher checks whether the main class is an
> Application subclass and, if it is, refuses to start unless javafx.graphics is
> on the MODULE path - the "JavaFX runtime components are missing" error. A
> launcher class that merely calls launch() sidesteps that check, which is the
> only reason the packaged jar runs at all. See claude/packaging-and-distribution.md.

**Uses:** [GameLog](GameLog.md) (2), [GameFiles](GameFiles.md) (1), [UserInterface](UserInterface.md) (1)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 33 | **type** `public class CityBuilderSim` | The way in. |
| 25 | 17 | `public static void main(String[] args)` |  |
| 51 | 4 | `private static void installCrashHandler()` | Catches what nothing else did. |

