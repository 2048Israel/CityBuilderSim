# MoneySignCheck.java - 397 lines · 9 methods · 5 constants · harnesses

`ham/citybuildersim/MoneySignCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The money's sign is the city's to choose and place, and with the default it
> writes what it always wrote (FM1). Not part of the game.
> 
> WHY THIS EXISTS. Jerus, 2026-11-02: his Rotterdam typed "fRotterdam Guilder"
> for the sign, because the only sign a city had was the "$" with the money's
> first letter in front of it. The sign is its own field now, before the figure
> or after it ("2.6M kr"), and Currency.write() is the one writer of it. Two
> promises, and a harness for each:
> 
>   - THE DEFAULT IS BYTE FOR BYTE WHAT IT WAS. The playtest's trace is compared
>     against main's, and every string it holds that has a "$" in it was written
>     by a "$" the code now routes through the writer. Section 1 holds the
>     formatters (ui.Money's and Formats') to the strings the code gave before
>     FM1, got by running that code and pasted in as they came.
>   - A SIGN AFTER IS WRITTEN EVERYWHERE THE "$" WAS, and nowhere else: section
>     4 puts a krona after the figure and reads every form back.
> 
> What it proves, a section each, every one a fixture that causes the condition:
> 
>   1. "$" before writes what it wrote before: the golden table.
>   2. write() places the sign: before and after, with a space where a letter
>      touches the figure, and a minus never moved.
>   3. The qualified sign: the initial and the sign where the sign is one the
>      vault's moneys also use ("$", "¥", "€", "C$"), the sign alone where it is
>      the city's own ("kr", "zł", "R$"), and qualify() puts it in place.
>   4. Every form with "kr" after: no "$", each ends in " kr", the foreign money
>      is written the same whatever the city's sign is.
>   5. signProblem() and typed().
>   6. The save keeps the sign and its place, and a load sets what the static
>      formatters write.
>   7. The founding row offers only the signs the interface's font draws
>      (ui/SignFont; the run says which candidates it dropped), a city founded
>      with a typed "kr"-after money writes it, and Currency.using() puts back
>      the money it found, also when its drawing throws.

**Uses:** [Currency](Currency.md) (111), [Money](Money.md) (73), [Game](Game.md) (15), [SignFont](SignFont.md) (9), [Founding](Founding.md) (5), [Formats](Formats.md) (4), [GameFiles](GameFiles.md) (4), [ForeignAccounts](ForeignAccounts.md) (2), [DataSave](DataSave.md) (2)

## Sections

| line | section |
|---:|---|
| 166 | · · 1 |
| 191 | · · 2 |
| 214 | · · 3 |
| 235 | · · 4 |
| 300 | · · 5 |
| 330 | · · 6 |
| 355 | · · 7 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `MoneySignCheck.V` | `{ 0, - 0.0004, 0.4, - 5, 12.3456, 999.6, 2.6e3, - 2.6e6, 1e12, 1.3e15, 1e-7, ...` | Model money in thousands, the golden table's inputs: nothing, a hair under nothing, a few hundred, a loss, then each unit from k to Q and the prices under a dollar. |
| 63 | `MoneySignCheck.D` | `{ 0, - 0.4, 0.4, 9999, 10000, 99999.5, 999499, 999500, - 999600, 1e9 - 1, 999...` | Dollars, for tightMoney(): either side of each place it changes unit. |
| 98 | `MoneySignCheck.GOLDEN` | `{ { "money", "$0", "$0", "$400", "-$5,000", "$12k", "$1.0M", "$2.6M", "-$2.6B...` | The strings the code gave before FM1, "$" before, at V (at D for tight and tightFull): the form's name, then one string per input. |
| 126 | `MoneySignCheck.REAL_OUT` | `System.out` |  |
| 127 | `MoneySignCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `private static int fails, checks` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 46 | 352 | **type** `public class MoneySignCheck` | The money's sign is the city's to choose and place, and with the default it writes what it always wrote (FM1). |
| 50 | 1 | `static void check(String label, boolean ok)` |  |
| 52 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 67 | 5 | `static String oldMarked(String prefix, String amount)` | The old ui.Money.marked(), as it was written before FM1: the reference the "$" before must still equal. |
| 74 | 22 | `static String apply(String form, double x)` | One form of the golden table at one input. |
| 130 | 4 | `static<T> T quietly(java.util.function.Supplier<T> work)` | Runs a piece of the city with the game's own narration off. |
| 136 | 1 | `static Currency krona()` | A krona after the figure, the fixture of sections 4 and 6. |
| 139 | 13 | `static List<String> forms(double x)` | The forms of one input that write the city's money, for the signs to be read off. |
| 153 | 11 | `public static void main(String[] args) throws Exception` |  |
| 165 | 232 | `static void sections()` |  |

