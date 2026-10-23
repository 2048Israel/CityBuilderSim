# PT1-M3 - the advice card says what automatic building holds back

Jerus, 2026-10-12, on PT1's M3 ("yes do rec"): at month 304 WHAT WOULD HELP
MOST offered a Middle School and a Police Station while automatic building held
both back, and only the inbox said why. The advice card now carries that
verdict, in the appraisal's own words, under each building automatic building
held back as not paying back. Neither rule changes: the advice offers what it
offered, and the pass decides as it did.

## What changed

- **AutoBuilder** keeps every building its pass passes over as not paying back
  (`PaybackHold`: the measure by label, the template id, and the payback weighed
  for one - runs, covered, paid, costs). Every one the walk passed over, not only
  the first the inbox names, because the card may offer any of them. Cleared at
  each pass, when switched off, and on a load; redenominated with the open asks.
- **The getter**: `heldBackWords(Suggestion)` is the card's line,
  `HELD_BACK` ("Automatic building holds it back; ") and `paybackWords()` of the
  payback the pass weighed - the very function that writes the inbox's line.
  `heldForPayback(Suggestion)` gives the payback itself; both null for a paving,
  a building the pass did not hold so for that measure, or with it off.
- **The Build overview**: `cardWords()` carries the line (`CardWords.heldBack`)
  and `suggestionCard()` draws it under the land line, in amber with the coin
  icon, wrapped; its tooltip says what the payback is and that the order is still
  the player's. The overview is not drawn kept (`ui/Kept` serves Bank, Finances,
  Government, Infrastructure, Land, People and the maps, not Build), so KeptCheck
  is untouched.

## The save

`AutoBuilder.State.paybackHolds`, saved with the held lines it sits beside: the
pass runs first in the month, so a city saved and loaded before the next month
shows on its cards what its pass decided, as its inbox does. A save from before
has none and its cards say nothing until the next pass; no format bump (Gson
leaves the missing key null, read as none).

## The harness

AutoBuildCheck section 6, on section 5's town with its elementary schools
retired, automatic building on, a month played: the pass holds the first school
back as not paying back while the advice offers it - the disagreement, caused.
Asserted: the card's line equals `HELD_BACK + paybackWords()` of the payback the
pass weighed; it contains the pass's figures (6,720 residents over 20 years,
$914.63 paid and $3,821 cost each, $382,036 a month to run); the inbox's notice
gives the same words; saved and loaded before the next month, the same line;
switched off, none; and switched on again with room (`withRoom()`), the next pass
weighs that school as paying back and orders it, and the card it was carries no
line.

## Renders

`PT1-M3/advice-before.png` (main 0.8.33's classes) and `advice-after.png`, by
`PT1-M3/RenderPT1M3.java` (scratch, not in the build; usage at its top), on the
same town: its advice is an Elementary School, a Police Station and five Paved
Roads, and the pass held back the school and the station (and the cells) - Jerus's
month 304 in small. After, both carry the line; the road, which the payback does
not weigh, does not. The rest of the page is unchanged, moved down by the
taller cards.

## The checks, the trace

- Baseline on main (bc7ed17c, 0.8.33): 135 harnesses, all green.
- The trace: main built apart from its own sources, and the batch, the default
  playtest traced on each (`-Dplaytest.trace`): all eight CSVs byte-identical;
  the report differs only in its two run-time lines. So no ensemble (EN1). The
  default playtest plays with automatic building off; the pass's one new step,
  weighing one more payback for each building past the first it passes over, is
  a read.
- The batch, main unmoved: `./batch-check.sh` 135 harnesses run, all green;
  Stale 0 firm findings; the maps regenerated. `--timing` left to the gate.

## Calls for Jerus

- **Fewer, not none.** Where some of a card's count pay back, the pass orders
  that many (a PAYBACK cut) and the card, which asks for more, says nothing:
  automatic building does not hold the building back. A line such as
  "Automatic building orders 2 of these; the third would not pay back" is
  possible on the same record, if he wants it.
- **The other holds.** Only the payback's verdict is on the card, as asked. The
  builders, the ground, the debt limit and the cash also hold an order; the
  AUTOMATIC BUILDING card's "Held back" and the inbox name them.
