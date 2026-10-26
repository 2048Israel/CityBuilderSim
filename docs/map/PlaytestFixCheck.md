# PlaytestFixCheck.java - 224 lines · 9 methods · 2 constants · harnesses

`ham/citybuildersim/PlaytestFixCheck.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> Verifies the small fixes of a played city (PT1): words and the log, nothing
> the month does. Not part of the game.
> 
> WHY THIS EXISTS
> 
> Jerus played a city and wrote down what read wrongly: a bank "that is none"
> while one stood, a power plant "13333% served", a notice toasted again on a
> load, two lines of the log. Each is a sentence, so none of them changes a
> figure in the model, and a sentence that is wrong is only found by someone
> reading it. Each is held here by a fixture that causes it and asserts
> against the model's own constant.
> 
> What this has to prove, by section (the population chart's axis, PT1 item
> 6, is ChartCheck's section 9):
> 
>   1. A BANK THAT STANDS BEFORE IT HAS OPENED IS NOT "NONE". A founded
>      city has its Commercial Bank standing, and Bank.getBranches() is 0
>      until the first month runs: NEEDS YOU reads "opens with the first
>      month" and does not count the row; the same city with no bank
>      standing still counts it, red, "there is none"; and once a month has
>      run the bank is open.
>   2. SERVED PAST A FEW TIMES WHAT IS ASKED PRINTS AS A MULTIPLE. Just
>      under CityNeeds.SERVED_AS_MULTIPLE it is a per cent; from it up a
>      multiple, "30×"; a share no one asks of is still a dash.
>   3. A NOTICE RAISED BEFORE A SAVE DOES NOT TOAST ON LOAD. A loaded inbox
>      holding an urgent unread notice, with the toast set emptied as
>      anotherCity() empties it, would toast it at once; after
>      Inbox.markAllToasted() it does not, it stays unread and urgent, and
>      a notice raised afterwards toasts once and not twice.
>   4. TWO LINES OF THE LOG. "Debt-to-GDP Ratio" in a month with no GDP
>      prints a dash, not NaN; "Interest Rate" has its per cent sign after
>      the figure.
> 
> Every fixture causes its condition.

**Uses:** [CityNeeds](CityNeeds.md) (20), [Notice](Notice.md) (12), [Game](Game.md) (9), [Inbox](Inbox.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (3), [EconomyManager](EconomyManager.md) (3), [GameFiles](GameFiles.md) (2), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 101 | · 1. a bank that stands before it has opened |
| 131 | · 2. served as a multiple |
| 150 | · 3. a notice does not toast again on load |
| 190 | · 4. two lines of the log |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 52 | `PlaytestFixCheck.OUT` | `System.out` |  |
| 53 | `PlaytestFixCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 48 | 177 | **type** `public class PlaytestFixCheck` | Verifies the small fixes of a played city (PT1): words and the log, nothing the month does. |
| 57 | 4 | `static void quietly(Runnable work)` |  |
| 63 | 6 | `static String printed(Runnable work)` | What the work printed, as text. |
| 71 | 3 | `static String plain(String text)` | A figure for a label: the multiple sign and the dash as plain characters, so a console that is not UTF-8 reads it. |
| 75 | 4 | `static void check(String label, boolean ok)` |  |
| 80 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 87 | 4 | `static CityNeeds.Need bankRow(Game g)` |  |
| 92 | 4 | `static boolean bankIsListed(Game g)` |  |
| 97 | 121 | `public static void main(String[] args) throws Exception` |  |
| 220 | 4 | `static String lineStarting(String log, String prefix)` | The first line of `log` that starts with `prefix`, trimmed, or null. |

