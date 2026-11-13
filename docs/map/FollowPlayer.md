# FollowPlayer.java - 174 lines · 15 methods · 3 constants · model

`ham/citybuildersim/FollowPlayer.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> Plays an InputTimeline in real time (TR2, 2026-11-09): the cursor of Follow. It moves along the log by the time it is given,
> at a chosen speed, with idle stretches shortened when asked, and hands every event that falls due to a {@link Target} in the
> order it was logged; the month markers it turns into {@link Target#loadMonth} itself, before the events after them. No
> JavaFX, no clock of its own: the interface calls {@link #advance} with the real milliseconds of each frame, so a harness plays
> a sitting in no time and the same way.
> 
> <p>WHY IT EXISTS (TR2): Follow feeds the player's logged input into the replay's copy - tabs switch, pages scroll, the map pans,
> clicks land - and months turn when the log says they turned, each by loading that month whole. Which events, which month and
> when is arithmetic that must be right to the frame whatever the window does, so it lives here, away from the scene. Joining
> mid-month loads the month and plays its input from its start in one go, flagged as catching up so that the interface changes
> state without drawing the way there; the state the month's marker found (the screen open, the window, the map) is
> {@link InputTimeline#stateAt} of the segment's start.

**Uses:** [InputTimeline](InputTimeline.md) (10), [InputLog](InputLog.md) (4)

**Used by (2):** [ReplayCheck](ReplayCheck.md), [ReplayMode](ReplayMode.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 30 | `FollowPlayer.SPEEDS` | `{ 0.5, 1, 2, 4 }` | The speeds Follow offers, as multiples of the player's own time. |
| 33 | `FollowPlayer.DEFAULT_SPEED` | `1` | The speed a player starts at. |
| 36 | `FollowPlayer.SLACK_MS` | `1e-6` | How close to the cursor an event's millisecond may be to count as due, in milliseconds: floating-point slack. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `private final InputTimeline timeline` |  |
| 39 | `private final Target target` |  |
| 40 | `private double speed` |  |
| 41 | `private boolean skipIdle` |  |
| 42 | `private boolean started, stopped, done` |  |
| 43 | `private int segment, next, month` |  |
| 44 | `private double cursor` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 158 | **type** `public final class FollowPlayer` | Plays an InputTimeline in real time (TR2, 2026-11-09): the cursor of Follow. |
| 20 | 8 | **type** `public interface Target` | What plays the log: the interface (or a harness), on the thread that calls start() and advance(). |
| 23 | 1 | `void loadMonth(int month)` _(in FollowPlayer.Target)_ | The month the log shows now: load it whole. |
| 26 | 1 | `void event(InputLog.Event e, boolean catchingUp)` _(in FollowPlayer.Target)_ | A logged event to re-enact (a marker too: the target decides what a SCREEN, a WINDOW, a MAP_VIEW or a CLOCK means); {@code catchingUp} while start() plays a month's input up to the moment joined, which is not drawn. |
| 46 | 4 | `public FollowPlayer(InputTimeline timeline, Target target)` |  |
| 56 | 20 | `public void start(InputTimeline.Moment at)` | Joins the timeline at {@code at}: loads the month in effect there and plays that month's input from just after its marker up to the moment, all at once, flagged as catching up; then it is live. |
| 83 | 46 | `public void advance(double realMs)` | Moves the log's cursor by {@code realMs} of the viewer's time times the speed - a stretch of idle compressed to {@link InputTimeline#IDLE_KEEP_MS} while skipping - and delivers the events that fall due, in order, not ... |
| 131 | 14 | `private void deliverDue(InputTimeline.Seg s)` | Hands over the events at or before the cursor, and every one left when the cursor has reached the segment's end. |
| 147 | 3 | `public void setSpeed(double speed)` | Sets the speed, a multiple of the player's own time (see {@link #SPEEDS}); a value that is not above 0 is ignored. |
| 151 | 1 | `public double speed()` |  |
| 154 | 1 | `public void setSkipIdle(boolean skip)` | Whether idle stretches play shortened (on at the start). |
| 156 | 1 | `public boolean skipIdle()` |  |
| 159 | 3 | `public InputTimeline.Moment now()` | The place the cursor is at, to stop and rejoin later with start(); null before the player has started. |
| 164 | 1 | `public int month()` | The month the log shows now, or -1 before the player has started. |
| 167 | 1 | `public void stop()` | Ends the playing: nothing more is delivered until start() is called again. |
| 170 | 1 | `public boolean isRunning()` | Whether the player is started and neither stopped nor at the end. |
| 173 | 1 | `public boolean isDone()` | Whether the cursor has reached the end of the timeline. |

