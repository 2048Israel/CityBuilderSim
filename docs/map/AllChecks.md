# AllChecks.java - 81 lines · 1 methods · 1 constants · harnesses

`ham/citybuildersim/AllChecks.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Runs every harness, one JVM each, and says which failed.
> 
> Twenty-nine harnesses and no runner meant "all checks pass" was a claim
> made by whoever had the patience to run twenty-nine main() methods by hand
> that day - and a harness nobody ran is a harness that does not exist. Each
> one calls System.exit() with its verdict, so they cannot share a JVM; this
> spawns them with the JVM and classpath it was itself started with.
> 
>     java -cp <classes> ham.citybuildersim.AllChecks            everything
>     java -cp <classes> ham.citybuildersim.AllChecks -q         verdicts only
>     java -cp <classes> ham.citybuildersim.AllChecks Labour Money   a subset, by name
> 
> Exit status is the number of harnesses that failed, so a build script can
> stop on it. BuildMenuCheck needs JavaFX on the classpath and is skipped,
> with a note, when it is not there.

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 27 | `AllChecks.HARNESSES` | `{ "BuildingDataCheck", "NewGameCheck", "CalendarCheck", "BooksCheck", "WaterC...` | In the order they are cheapest to fail. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 24 | 58 | **type** `public class AllChecks` | Runs every harness, one JVM each, and says which failed. |
| 39 | 42 | `public static void main(String[] args) throws Exception` |  |

