# TR2 - The ghost cursor and Follow, in replay ({next}, 2026-11-09)

The brief is `docs/queue/TR2.md` (TR1's section 6, and TR1's review's four
follow-ups). Built on main at 0.9.71 (3edee6bc), lane B after BR2.

## Decided while building (draft, kept current)

- **The model under Follow is pure** (`InputTimeline`, `FollowPlayer`): which
  sitting holds each month after a rewind, where Follow starts for a month,
  the state at a moment, the idle rule, real-time play at a speed. The window
  part (`ui/Reenactor`, `ui/FollowRules`, the ghost, ReplayMode's Follow) only
  applies what the player delivers.
- **What is dropped is a rule, not a list of buttons alone** (`FollowRules`):
  every input while the player was on a game menu (his clicks there were Save,
  Load, New city, Quit) or while the copy is on one; the clock's keys and F11;
  Esc and P when they would open the menu; a press on a node tagged
  `FollowRules.DROP` (the rail's Menu, the clock).
- **Item 5, the district plans across quick steps, is not built.** TR1's call
  3 made it conditional on the screen recordings showing the lag, and none has
  been seen; `CityMap` is also lane A's file tonight (RI1). Pending, TR2-P.

## Calls (★)

- ★1 **Cinematic mode while following keeps the header and the rail** - the
  re-enacted clicks land where the player's layout put them, which the chrome
  is part of; the time bar and the real cursor still go. Recommended: keep.
- ★2 **The viewer's input that ends Follow**: a press, the wheel, a key, or
  the mouse moved more than a few pixels (a nudge of the desk while filming
  does not end the shot). Recommended: keep.
- ★3 **Idle stretches are skipped by default** (a gap over five seconds with
  no input plays as one second); the bar's toggle plays them whole.
  Recommended: keep.
