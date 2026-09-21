# InboxCheck.java - 364 lines · 5 methods · 2 constants · harnesses

`ham/citybuildersim/InboxCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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
> 
> The awkward part of testing this is that the four conditions are hard to
> cause on purpose - a bank does not fail when you ask it to. So the lifecycle
> is driven through the one condition a harness CAN stage (a city with no
> healthcare has nowhere to bury anybody and no doctors, which is the notice a
> new city raises on its own), and the rest is checked structurally.

**Uses:** [Notice](Notice.md) (18), [Inbox](Inbox.md) (9), [Game](Game.md) (8), [GameFiles](GameFiles.md) (2)

## Sections

| line | section |
|---:|---|
| 65 | · 1. a new city has an empty inbox |
| 75 | · 2. living raises what is true |
| 141 | · 3. unread, urgent, and then read |
| 185 | · 4. a live notice is refreshed, never re-raised |
| 253 | · 5. it survives a save and a reload |
| 285 | · 6. resolving and culling |
| 336 | · 7. a new game forgets it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `InboxCheck.OUT` | `System.out` |  |
| 46 | `InboxCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 41 | 324 | **type** `public class InboxCheck` | Verifies the inbox: raising, refreshing, resolving, culling and the round trip. |
| 50 | 4 | `static void quietly(Runnable work)` |  |
| 55 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 60 | 287 | `public static void main(String[] args) throws Exception` |  |
| 349 | 7 | `static Notice stale(int month)` | A notice settled longer ago than the inbox keeps them. |
| 357 | 7 | `static void cleanUp(Path root)` |  |

