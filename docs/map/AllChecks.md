# AllChecks.java - 203 lines · 4 methods · 6 constants · harnesses

`ham/citybuildersim/AllChecks.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

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
>     java -cp <classes> ham.citybuildersim.AllChecks -f         failures only
>     java -cp <classes> ham.citybuildersim.AllChecks Labour Money   a subset, by name
>     java -cp <classes> ham.citybuildersim.AllChecks --timing   the timing harnesses alone (Timing.HARNESSES)
> 
> Exit status is the number of harnesses that failed, so a build script can
> stop on it. BuildMenuCheck and KeptCheck need JavaFX on the classpath and are skipped,
> with a note, when it is not there.
> 
> -f is for an AI session, where every line printed is read and paid for
> (2026-10-13). -q printed a line for each of a hundred-odd harnesses and,
> for a red one, only "FAILED", so the session ran it again in full to find
> the assertion and carried its thousands of lines from then on. -f keeps
> each harness's whole output in target/checks/NAME.log, says nothing of a
> harness that passed, and for one that failed prints its failing lines
> under the section they are in - at most FAIL_LINES of them - and where
> the rest is.
> 
> Timing (TM1, 2026-10-18): -Dcbs.timing=soft given to this JVM is given to
> every harness it starts, where a timing figure over its budget is SOFT and
> not counted (Timing). -f counts the SOFT lines in every log, a passing
> harness's too, and says how many under the summary. --timing runs only the
> harnesses that time something: what the ship gate runs, hard.

**Uses:** [Timing](Timing.md) (5)

**Used by (1):** [TimingCheck](TimingCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `AllChecks.HARNESSES` | `{ "BuildingDataCheck", "NewGameCheck", "WorldCheck", "GridCheck", "Conversion...` | In the order they are cheapest to fail. |
| 66 | `AllChecks.FAIL_LINES` | `40` | The most lines -f prints for one failed harness; the rest stays in its log. |
| 69 | `AllChecks.LOGS` | `Paths.get("target", "checks")` | Where -f keeps each harness's whole output. |
| 72 | `AllChecks.SECTION` | `Pattern.compile("^\\s*---\\s.*\\s---\\s*$")` | A harness's section title, "--- what it proves ---". |
| 75 | `AllChecks.SUMMARY` | `Pattern.compile("^\\s*\\d+ FAILED\\s*$")` | A harness's closing count, "N FAILED": printed, but not under the last section's title. |
| 78 | `AllChecks.FAILING` | `Pattern.compile("\\bFAIL(ED)?\\b\|Exception\|^\\S*Error\\b\|^Caused by")` | A line that says something failed: an assertion's FAIL, the "N FAILED" summary, an exception. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 47 | 157 | **type** `public class AllChecks` | Runs every harness, one JVM each, and says which failed. |
| 80 | 66 | `public static void main(String[] args) throws Exception` |  |
| 155 | 7 | `static List<String> command(String java, String classpath, String name, boolean failuresOnly)` | How a harness is started: the JVM and classpath this one was, the timing property if it is set here (a child JVM inherits no system property, only JDK_JAVA_OPTIONS), and under -f its output in UTF-8 whatever the machi... |
| 164 | 10 | `static int softLines(Path log)` | How many of a log's assertion lines went SOFT (Timing.SOFT_LINE); a log that cannot be read has none. |
| 182 | 21 | `static List<String> failingLines(Path log) throws IOException` | What -f prints of a failed harness's log: each failing line, after the title of the section it is in (once per section), at most FAIL_LINES in all; a log with no failing line in it - a harness that died some other way... |

