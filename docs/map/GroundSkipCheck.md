# GroundSkipCheck.java - 338 lines · 12 methods · 5 constants · harnesses

`ham/citybuildersim/GroundSkipCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The test player's ground across its long skips (LP-G1): a growing town whose ground holds the look's
> BuildAdvice.HORIZON months but not the skip after it, skipped as the player skipped until LP-G1 and as it skips now
> (LongPlaytest.skipWatchingGround()), and an offer the look's cash share cannot pay, held inside a skip.
> 
> WHY: LongPlaytest.keepGroundAhead() leaves room for HORIZON months' growth and ran only at the advisor's looks, six
> to a hundred and twenty months apart. The default playtest (seed 0) looked at m743 and next at m805, and from m760
> its landlords were held for land with no home on site, full power, unlimited mortgage capital and builders to spare,
> until 12.3 households had nowhere at m807; the ensemble's seed 9 went from m500 to m603 without a look and flagged
> m605 (docs/queue/PW5-notes.md, docs/queue/LP-G1.md). This town causes the class from the model's own inputs, not
> a played month: the playtest's own village, its ground built on up to the player's line, then the rhythm's
> sixty-month skip.
> 
> What this has to prove:
>   1. THE OLD SKIP RAN THE RESERVE OUT. The town at its line - a look buys nothing, the ground holding HORIZON months'
>      growth with the slack - skipped sixty months with no look at its ground (runPlayer() alone, the skip until
>      LP-G1): its ground passes the line more than HORIZON months after the look with no look to serve it, and the
>      landlords' order is held for land ("no land") with no home on site.
>   2. THE WATCHED SKIP LOOKS IN TIME. The same town, built again and equal at the look, skipped the same sixty months
>      through skipWatchingGround(): no month comes more than HORIZON months after a look at the ground; the looks
>      inside the skip buy ground, each look within its GROUND_AHEAD_CASH_SHARE of the cash, and every offer bought is
>      paid (the land office's dollars and the city's ground grow by the offers); the landlords are never held for
>      land; and the playtest's own audit of every month (LongPlaytest.audit()) flags nothing in it the old skip did not.
>   3. AN OFFER THE SHARE CANNOT PAY STILL WAITS, IN A CITY ITS OWN TEST WILL NOT LEND TO. The same town skipped the
>      old way until its landlords are held for land, borrowed on the funding page's bond until the player's own test
>      for borrowing (LongPlaytest.canService()) refuses any more (FB11: else a sector waiting on ground is funded on
>      that bond, ER1Check), its cash then short of the best offer over GROUND_AHEAD_CASH_SHARE but within the cash:
>      the look the watched skip makes buys nothing, pays nothing and places nothing, and is counted short; the next
>      comes HORIZON months on, and with the share able to pay it buys the offer and the landlords are released.
>   4. EACH CITY KEEPS ITS OWN LOOK. Two towns built alike and skipped watched in step, a month of one and a month of
>      the other, look at their ground in the same months and end alike: the record of the last look is the city's.

**Uses:** [LongPlaytest](LongPlaytest.md) (46), [Game](Game.md) (20), [BuildAdvice](BuildAdvice.md) (13), [LandManager](LandManager.md) (5), [Sectors](Sectors.md) (2), [GameFiles](GameFiles.md) (2), [CityLand](CityLand.md) (2), [LandParcel](LandParcel.md) (2), [DebtManager](DebtManager.md) (1), [LandMarket](LandMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 169 | · · 1. the old skip |
| 210 | · · 2. the watched skip |
| 266 | · · 3. the unaffordable offer |
| 316 | · · 4. each city its own look |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `GroundSkipCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` | The game's and the playtest's narration while a fixture plays. |
| 45 | `GroundSkipCheck.SKIP` | `60` | The rhythm's sixty-month skip (LongPlaytest.main(), stop % 6 == 3): the gap the default playtest's m743-m805 was. |
| 48 | `GroundSkipCheck.UNDER_THE_LINE` | `.002` | How far under the player's line the fixture leaves the ground built on: a look there buys nothing. |
| 94 | `GroundSkipCheck.TOWN_MONTHS` | `240` | The months the town is played before its look: past the founding's surge, when the businesses' forecast reads a town growing by the tenth of a percent a month and not by percents. |
| 98 | `GroundSkipCheck.TOWN_SLOT` | `1` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `static int fails` |  |
| 40 | `static PrintStream out` |  |
| 67 | `static Game audited` | The city whose M0 the playtest's audit read last. |
| 97 | `static GameFiles townFiles` | The town's save, in its own scratch folder, which each section loads a copy of. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 38 | 301 | **type** `public class GroundSkipCheck` | The test player's ground across its long skips (LP-G1): a growing town whose ground holds the look's BuildAdvice.HORIZON months but not the skip after it, skipped as the player skipped until LP-G1 and as it skips now ... |
| 50 | 4 | `static void check(String label, boolean ok)` |  |
| 55 | 5 | `static void quietly(Runnable action)` |  |
| 62 | 3 | `static void month(Game g)` | One month of the player's own play, as the playtest's skip runs it (LongPlaytest.runPlayer()). |
| 75 | 7 | `static void play(Game g)` | A month of runPlayer() on `g`, the audit's M0 its own: LongPlaytest.lastM0 is one static for the JVM, and this harness plays several towns, so a month of another town than the last is first given its own M0 - else its... |
| 84 | 3 | `static boolean landlordsHeld(Game g)` | Whether the landlords' order held for ground this month (Game.getLandBlockedSectors(): BusinessInvestment's noLand()). |
| 89 | 3 | `static double homesOnSite(Game g)` | The homes the landlords have on site. |
| 101 | 13 | `static void playTheTown()` | The playtest's own town, today's (LongPlaytest.probeTodayCity(), R1g: its moment is today's played path's; then probePlay(): its rhythm of skips, looks and moves) to TOWN_MONTHS, saved. |
| 120 | 14 | `static Game town()` | A copy of the town, loaded from its save, at its look: the look made (keepGroundAhead()), then its dry ground built on up to UNDER_THE_LINE below the player's line (LandManager.allocate(), as LandCheck 22 builds a cit... |
| 136 | 1 | **type** `record Seen(int month, double cash, double used, double line, int sinceLook, boolean held, double onSite, S...` | What a month of a skip saw. |
| 138 | 6 | `static Seen seen(Game g)` |  |
| 146 | 5 | `static java.util.Map<String, Integer> findingCounts()` | The months the playtest's audit has flagged each finding in so far, by its name (LongPlaytest.Finding.count). |
| 157 | 5 | `static java.util.Set<String> flaggedSince(java.util.Map<String, Integer> before)` | The findings the audit flagged since `before`: each whose count of months grew, so a name the founding, the town's play or an earlier section raised already is counted again when a month of this skip raises it again (... |
| 163 | 175 | `public static void main(String[] args) throws Exception` |  |

