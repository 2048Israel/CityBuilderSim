# InboxCheck.java - 527 lines · 8 methods · 2 constants · harnesses

`ham/citybuildersim/InboxCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

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
> 
> The awkward part of testing this is that the four conditions are hard to
> cause on purpose - a bank does not fail when you ask it to. So the lifecycle
> is driven through the one condition a harness CAN stage (a city with no
> healthcare has nowhere to bury anybody and no doctors, which is the notice a
> new city raises on its own), and the rest is checked structurally.

**Uses:** [Notice](Notice.md) (26), [Inbox](Inbox.md) (20), [Game](Game.md) (15), [GameFiles](GameFiles.md) (3), [TempDirs](TempDirs.md) (1), [Sector](Sector.md) (1)

## Sections

| line | section |
|---:|---|
| 69 | · 1. a new city has an empty inbox |
| 79 | · 2. living raises what is true |
| 145 | · 3. unread, urgent, and then read |
| 189 | · 4. a live notice is refreshed, never re-raised |
| 264 | · 5. it survives a save and a reload |
| 296 | · 6. resolving and culling |
| 347 | · 7. the wait on ground, told again each year (PT1-M2) |
| 350 | · 8. a new game forgets it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `InboxCheck.OUT` | `System.out` |  |
| 50 | `InboxCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 45 | 483 | **type** `public class InboxCheck` | Verifies the inbox: raising, refreshing, resolving, culling and the round trip. |
| 54 | 4 | `static void quietly(Runnable work)` |  |
| 59 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 64 | 297 | `public static void main(String[] args) throws Exception` |  |
| 367 | 10 | `static boolean yearlyRetell(Inbox inbox, Notice notice, int previous)` | Whether `notice` is the wait on ground told again a year on (PT1-M2): the "landlock" notice raised Inbox.LANDLOCK_REPEAT_MONTHS after the one raised in `previous`, which was settled the month this one was raised. |
| 387 | 118 | `static void landWaitRetold(GameFiles files)` | Section 7 (PT1-M2, Jerus 2026-10-12, "a notice repeated"). |
| 507 | 3 | `static boolean waitStanding(Game city)` | Whether the city has the wait on ground standing now. |
| 512 | 7 | `static Notice stale(int month)` | A notice settled longer ago than the inbox keeps them. |
| 520 | 7 | `static void cleanUp(Path root)` |  |

