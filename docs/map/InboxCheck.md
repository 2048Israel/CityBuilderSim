# InboxCheck.java - 652 lines · 13 methods · 2 constants · harnesses

`ham/citybuildersim/InboxCheck.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> Verifies the inbox: raising, refreshing, resolving, culling and the round
> trip. Not part of the game.
> 
> WHY THIS EXISTS
> 
> The four things in here were banners, and a banner is checkable by looking at
> it: it is on the screen or it is not. A notice is not. It is raised by the
> city in a month nobody was watching, kept after it is read, and kept after it
> is fixed - so every way it can go wrong is quiet.
> 
> 1. RAISED TWICE. A condition that is true for forty months must produce ONE
>    notice that keeps its wording current, not forty notices saying the same
>    thing. Getting this wrong turns the inbox into a log and the count on the
>    envelope into noise.
> 2. NEVER RESOLVED. A notice whose condition has gone away must go grey. One
>    that stays red is a city permanently reporting a problem it has already
>    fixed, and a player who learns to ignore a red line has lost the whole
>    feature.
> 3. LOST ON RELOAD. The notices are a new key in the save. A line forgotten in
>    the load path reads perfectly all session and comes back empty, which
>    looks exactly like a city that has had nothing go wrong.
> 4. NEVER CULLED. Resolved notices are kept for two years and then dropped. A
>    cull that never fires makes a four-hundred-year city carry four hundred
>    years of settled complaints.
> 5. TOLD ONCE AND NEVER AGAIN (PT1-M2). The wait on ground is the one
>    standing condition told again: a new notice each year it lasts, naming
>    the sectors and the ground each needs, on a rhythm a save and a load
>    keep, and silent once the ground is bought (section 7).
> 6. FORGOTTEN WEALTH BAND (GV1). The first measured band is a quiet
>    baseline; a crossing raises one notice, and the saved standing band
>    survives reading, loading and the resolved notices' two-year cull.
> 
> The awkward part of testing this is that the four conditions are hard to
> cause on purpose - a bank does not fail when you ask it to. So the lifecycle
> is driven through the one condition a harness CAN stage (a city with no
> healthcare has nowhere to bury anybody and no doctors, which is the notice a
> new city raises on its own), and the rest is checked structurally.

**Uses:** [Notice](Notice.md) (41), [Inbox](Inbox.md) (29), [GovernmentView](GovernmentView.md) (26), [Game](Game.md) (22), [GameFiles](GameFiles.md) (4), [TempDirs](TempDirs.md) (1), [Sector](Sector.md) (1), [HistorySave](HistorySave.md) (1)

## Sections

| line | section |
|---:|---|
| 72 | · 1. a new city has an empty inbox |
| 82 | · 2. living raises what is true |
| 148 | · 3. unread, urgent, and then read |
| 192 | · 4. a live notice is refreshed, never re-raised |
| 267 | · 5. it survives a save and a reload |
| 299 | · 6. resolving and culling |
| 350 | · 7. the wait on ground, told again each year (PT1-M2) |
| 353 | · 8. wealth changes, rather than every month's reading (GV1) |
| 356 | · 9. a new game forgets it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 52 | `InboxCheck.OUT` | `System.out` |  |
| 53 | `InboxCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 48 | 605 | **type** `public class InboxCheck` | Verifies the inbox: raising, refreshing, resolving, culling and the round trip. |
| 57 | 4 | `static void quietly(Runnable work)` |  |
| 62 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 67 | 300 | `public static void main(String[] args) throws Exception` |  |
| 373 | 10 | `static boolean yearlyRetell(Inbox inbox, Notice notice, int previous)` | Whether `notice` is the wait on ground told again a year on (PT1-M2): the "landlock" notice raised Inbox.LANDLOCK_REPEAT_MONTHS after the one raised in `previous`, which was settled the month this one was raised. |
| 393 | 118 | `static void landWaitRetold(GameFiles files)` | Section 7 (PT1-M2, Jerus 2026-10-12, "a notice repeated"). |
| 513 | 3 | `static boolean waitStanding(Game city)` | Whether the city has the wait on ground standing now. |
| 518 | 99 | `static void wealthBandChanges(GameFiles files)` | Section 8 (GV1): cause each band with treasury cash, leaving the measured revenue and the rest of the wealth alone. |
| 619 | 4 | `static void setWealthYears(Game city, double years)` | Set only treasury cash, so the government's measured wealth reaches the stated interior of a band. |
| 624 | 1 | `static List<Notice> wealthNotices(Inbox inbox)` |  |
| 625 | 3 | `static List<Notice> wealthNotices(List<Notice> notices)` |  |
| 630 | 5 | `static boolean sameNotice(Notice was, Notice now)` | The saved identity and state, rather than Java object identity across a load. |
| 637 | 7 | `static Notice stale(int month)` | A notice settled longer ago than the inbox keeps them. |
| 645 | 7 | `static void cleanUp(Path root)` |  |

