# TR2 - The ghost cursor and Follow, in replay ({next}, 2026-11-09)

The brief is `docs/queue/TR2.md` (TR1's section 6, and TR1's review's four
follow-ups). Built on main at 0.9.71 (3edee6bc), lane B after BR2. Three
slices on Sonnet (A: the review fixes and the model; B1: the re-enactor; B2:
the ghost and Follow in the window; B3: the check in the window), each
reviewed here before its commit.

## What it does

In replay, **Follow** (the bar's button, or L) plays the player's own input
for the month on view back in real time of play: his clicks, drags, the wheel
and his keys land in the copy as the scene would have delivered them, so the
tabs switch, the pages scroll, the map pans and zooms, the hover cards show.
Months turn when the log says they turned, each loaded whole. Joining in the
middle of a month loads it and plays its input from its marker without
drawing up to the moment; stopping keeps the moment, and Follow on again in
the same month starts there. Speeds 0.5x to 4x (the bar's chips, Up and Down
while following); idle stretches skipped by default (the bar's toggle).
Follow sets the window to the logged scene size first (as TR1's 1920 x 1080
button does), and says so when the screen is too small for it.

The **ghost cursor** (Palette.GHOST, magenta, see-through, a ripple at each
press) is the player's pointer for the moment on view, shown whenever the
copy is on the screen he had open then; H or the bar's button turns it off.

What is not re-enacted is a rule (`ui/FollowRules`): everything logged while
the player was on a game menu (Save, Load, New city, Quit are there) or while
the copy is on one, anything while a dialog is up, a press on a node tagged
`FollowRules.DROP` (the rail's Menu, the clock), the clock's keys (Space,
Left, Right), F11, and Esc or P where they would open the menu. Every 300 ms
after a screen marker the copy is resynced to the logged screen, tab, map and
map view, so a drop or a missed hover cannot leave it on another page.

The viewer's own press, wheel or key, or a move of more than 8 px, ends
Follow; the first 1.5 s after it starts take moves as the hand settling.

## Files

- `InputTimeline`, `FollowPlayer` (new): which sitting holds each month after
  a rewind (the latest whose markers reach it), where Follow starts for a
  month, the state at a moment, the idle rule; the player stepping it.
- `ui/Reenactor`, `ui/FollowRules`, `ui/GhostLayer`, `ui/FollowBench` (new);
  `ui/ReplayMode` (Follow, the bar's group, the resync, the viewer's filters,
  the window fit), `ui/UserInterface` (the hooks Follow drives, the DROP tags,
  cinematic's kept form), `ui/InputRecorder` (logs the map's closing),
  `ui/MapView`, `ui/ReplayGraphLayer`, `ui/Palette` (small hooks).
- TR1's review: `InputLog` (a failed write takes nothing more and lets its
  file go), `Recording` (an id only of `newId`'s shape; the months past a
  first write cleared before it, and the recording ended when they cannot
  be), `Game` (a month that autosaves is filled once, the recording's month
  the autosave's save but for its name).
- `ReplayCheck` sections 7-10; `FollowBench` runs alone too.

Measured: a sitting's input log reads in 3 ms when a recording is entered;
ReplayCheck's new sections take 4.8 s, 0.1 s, 4.7 s and 7.4 s (10's window
part 29 findings in 6.7 s). Removing the rail Menu's DROP tag turned section
10's Menu, order, ghost and click lines red and took the copy to the main
menu, so the check sees what it is there for.

## Decided while building

- **The model under Follow is pure**: the window part only applies what
  `FollowPlayer` delivers, so section 9 proves the timing headless.
- **Clicks are re-enacted as the scene would deliver them**, through a pick of
  our own (the window's layers top down, clip and `contains`), with enter and
  exit, pressed and hover states and the click on the common ancestor; hover
  and pressed are set by reflection where JavaFX keeps them private (the game
  runs on the classpath; FollowBench prints whether both worked).
- **Item 5, the district plans across quick steps, is not built.** TR1's call
  3 made it conditional on the screen recordings showing the lag, and none has
  been seen; `CityMap` is also lane A's file tonight. Pending, TR2-P.
- **Accepted limits**: a rejoin made while the window is still being resized
  starts at the month's start; and the window fit is asynchronous, so the
  in-window checks need the toolkit (skipped with a line on a headless JVM).
- **No ensemble**: recording is off unless a city turns it on and Follow runs
  only inside replay, so the default playtest's trace cannot move.

## Calls (★)

- ★1 **Cinematic mode while following keeps the header and the rail** - the
  re-enacted clicks land where the player's layout put them, which the chrome
  is part of; the time bar and the real cursor still go. Recommended: keep.
- ★2 **The viewer's input that ends Follow**: a press, the wheel, a key, or
  the mouse moved more than 8 px (a nudge of the desk while filming does not
  end the shot), with 1.5 s's grace at the start. Recommended: keep.
- ★3 **Idle stretches are skipped by default** (a gap over five seconds with
  no input plays as one second); the bar's toggle plays them whole.
  Recommended: keep.

## What to look at, if an issue turns up

Follow a month in which a decision was taken on the Policy screen: the ghost
moves to the control, the click lands, the figure changes as it did.
