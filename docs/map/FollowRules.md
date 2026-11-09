# FollowRules.java - 100 lines · 2 methods · 7 constants · interface

`ham/citybuildersim/ui/FollowRules.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> What Follow re-enacts of a logged sitting and what it drops (TR2, 2026-11-09): the verdict on one logged input event, from
> the screen the player had open, the screen the copy shows now and what the copy's window is doing. No JavaFX in this class,
> so a model harness can ask it; the window's nodes are told to it as a flag ({@link #DROP}), the keys as the log's codes.
> 
> <p>WHY (TR2): Follow plays a sitting's mouse and keys back into the copy's window (Jerus chose "Re-enact it"), and a click
> of the player's that saved, loaded, founded a city, quit or ran the clock must not do that again in the copy: the copy is
> a throwaway shown to a viewer, whose own clock, files and window are not the ghost's to touch. Everything on a game menu is
> the player leaving the city, so every input there is dropped, on the logged side and on the copy's; the clock's keys and
> the keys that open the menu or change the window are dropped; a click on a node that opens a menu, leaves the city or moves
> the clock is dropped. The Reenactor (the one that fires the events) asks here for each one, and keeps a press's verdict
> for its drags and its release, so a dropped press is never half done.

**Uses:** [InputLog](InputLog.md) (7), [UserInterface](UserInterface.md) (2)

**Used by (4):** [FollowBench](FollowBench.md), [Reenactor](Reenactor.md), [ReplayCheck](ReplayCheck.md), [UserInterface](UserInterface.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 45 | `FollowRules.Verdict.KEEP` |  |
| 47 | `FollowRules.Verdict.DROP_LOGGED_MENU` | The player's screen was a game menu (Save, Load, New city, Settings, Quit were clicked there). |
| 49 | `FollowRules.Verdict.DROP_COPY_MENU` | The copy shows a game menu. |
| 51 | `FollowRules.Verdict.DROP_DIALOG` | A dialog (Quit's) is up on the copy. |
| 53 | `FollowRules.Verdict.DROP_NODE` | A press or release on a node marked {@link #DROP}. |
| 55 | `FollowRules.Verdict.DROP_CLOCK_KEY` | Space, Left or Right: the clock and its speed. |
| 57 | `FollowRules.Verdict.DROP_WINDOW_KEY` | F11: the window's full screen. |
| 59 | `FollowRules.Verdict.DROP_MENU_KEY` | Esc or P where it would open the game menu. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 23 | `FollowRules.DROP` | `"follow.drop"` | The node property that marks a node (or, set on an ancestor, everything in it) the ghost never clicks: it opens a game menu, leaves the city, quits or moves the clock. |
| 26 | `FollowRules.SPACE` | `InputLog.keyCode("Space")` | The log's code of the Space key, which runs the clock: the toolkit's key name, as {@link InputLog#keyCode} makes it. |
| 29 | `FollowRules.LEFT` | `InputLog.keyCode("Left")` | The log's code of the Left key, which slows the clock. |
| 32 | `FollowRules.RIGHT` | `InputLog.keyCode("Right")` | The log's code of the Right key, which speeds the clock. |
| 35 | `FollowRules.F11` | `InputLog.keyCode("F11")` | The log's code of the F11 key, which is the window's full screen. |
| 38 | `FollowRules.ESC` | `InputLog.keyCode("Esc")` | The log's code of the Esc key, which opens the game menu. |
| 41 | `FollowRules.P` | `InputLog.keyCode("P")` | The log's code of the P key, which opens the game menu too when nobody is typing. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 18 | 83 | **type** `public final class FollowRules` | What Follow re-enacts of a logged sitting and what it drops (TR2, 2026-11-09): the verdict on one logged input event, from the screen the player had open, the screen the copy shows now and what the copy's window is do... |
| 20 | 1 | `private FollowRules()` |  |
| 44 | 17 | **type** `public enum Verdict` | KEEP: re-enact it; every other verdict is a reason for dropping the event. |
| 72 | 28 | `public static Verdict judge(InputLog.Event e, String loggedScreen, String copyScreen, boolean dialogUp, boolean typing, boolean...` | The verdict on one logged event. |

