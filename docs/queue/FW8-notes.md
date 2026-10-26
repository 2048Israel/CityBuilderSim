# FW8 - WHAT IS BITING flags a zone short of what it wants (0.8.48, 2026-10-25)

The brief: `QUEUE.md`'s row, from `docs/notes/todo-archive.md` (FOUND ON THE
WAY IN 0.7.107 TO 0.7.109, Z2; and 0.7.117 TO 0.7.120, the docs pass's
"Later"): the Policy hub's WHAT IS BITING has no zoning flag, and a zone
short of what it wants would be one; the map's tint of a zone's districts is
I14's.

## 1. What was there

WHAT IS BITING (`ui/PolicyScreen.policyFlags()`) lists the levers forcing
somebody's hand: the wage floor, a sector shut out of borrowing, tuition,
pensions, subsidies, the policy rate, trust, the peg, the money, the taxes at
their stops. Zoning, the fifth area since 0.7.109, had none. The model
already knew when a zone is short: `ZoningView.Row.isShort()` (Z10), true
when the zones together want more than the free ground, so that each holds
the same share of its want (`LandManager.heldByZone()`). Then the zone's
sector cannot build out its zone, and nothing is free for anyone else
(`freeForAnyone()` is the free ground less what the zones hold). Only
Policy › Zoning showed it, as a "holds less than it wants" chip on a zone's
row. No harness read any WHAT IS BITING flag.

The map's half of the row was done before this batch: I14 (0.8.0) tints a
zone's districts in its sector's colour. ZT1, queued, gives Policy › Zoning
the same palette. FW8 leaves nothing undone, so it has no FW8-C.

## 2. What changed

- **`ZoningView.shortfall(g)`** (new section, WHAT IS BITING'S ZONING FLAG):
  the short zones' rows in the zones' order, what they want together, what
  they hold (all the free ground there is), the share of its want each holds,
  and the flag's heading and words. It is null when no zone is short. The
  words live in the model, as NEEDS YOU's GROUND row's do in `CityNeeds`, so
  the harness reads the exact text. Pure: it changes nothing.
- **The flag** is the last one in `policyFlags()`, a warning: "Agriculture's
  zone holds less than it wants", "It holds 78.5 km² of the 90 km² it wants:
  all the free ground there is. Nothing is left for anyone else to build on.
  Buying land fills it; a smaller zone wants less." With two or more zones
  short: "2 zones hold less than they want", "...each the same share of what
  it wants". With no free ground at all: "It holds none of the 40 km² it
  wants: the city has no free ground." Its door opens the zone's page
  ("Agriculture's zone"), or Zoning's The ground page when several are short.
  The screen computes nothing; it paints the view's words.
- **Not kept.** The hub is rebuilt each redraw: only People draws through
  `UserInterface.keepMenu()` (UP4). So there was no kept drawing code to
  change, and `KeptCheck` reads nothing FW8 touched.

## 3. The check

`ZoningViewCheck` 9, "what is biting", has 22 lines. Its fixtures cause each
case: no zone; a zone of 2% that holds all it wants; Agriculture zoned 100 km²
on 80 km² of dry ground (one zone short); Mining's 50% and Agriculture's 60%
(each within the free ground alone, together past it); Mining's 50% alone (a
large zone the free ground covers); built to the edge (no free ground). It
asserts against `LandManager`'s reads (`heldBy()`, `getFreeSqFt()`,
`freeForAnyone()`), that each short zone holds the same share (Z10), and the
heading and words exactly. It also reads the hub's own list:
`policyFlags()` on a `UserInterface` bound to the city, with no window, by
reflection, since the screen is package-private (BuildMenuCheck binds one the
same way). That proves the panel carries exactly one zoning flag, with the
view's words and its door, and none with enough. All 81 lines of the harness
pass.

## 4. The renders

`docs/queue/FW8/`, drawn headless by `RenderFW8` and `RenderFW8Page` beside
them (scratch, not in the build; CD6's way: the hub built on a
`UserInterface` bound to the city, themed, snapshot), 1,200 px wide, on
ZoningViewCheck's city at month 13:

- `before-short-one.png` and `before-short-two.png`: main's code. Agriculture
  zoned 90 km² (holds 78.5), and then Mining 50% plus Agriculture 60%: WHAT IS
  BITING reads "Nothing is binding".
- `after-short-one.png`: the flag, with its door to "Agriculture's zone".
- `after-short-two.png`: one flag for two zones, with its door to "The ground".
- `after-enough.png`: Mining zoned 2%, holding all it wants: "Nothing is
  binding".

## 5. The trace

The model's month is untouched. `ZoningView` is read only by the screens
(LandScreen, ZoningScreen, now PolicyScreen) and by its harness, and nothing
in `Game.nextMonth()` or `SimulationEngine` calls it. The default playtest's
trace is therefore unchanged by construction, and there is no ensemble
(EN1).

## 6. How it was done

Opus wrote it whole, with no Sonnet slice. The change is about 75 lines of
model and screen plus the harness section, and the context needed to write it
was already read: a slice's brief would have cost more than the code.
