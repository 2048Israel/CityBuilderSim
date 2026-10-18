# FX3 - the freight map's faults, Jerus's second eye check (2026-10-18)

The brief for batch FX3. `QUEUE.md` links here. The orchestrator wrote it from Jerus's eye check of 0.8.3: five screenshots of his Halifax city, in `FX3/` beside this file. His words: "line to the world seems to be working now, and lonely highways seems to be fixed, but uh ya still quite a bit of stuff to get done". FX2's two faults are settled, and what follows is new. Each item is a fault, not a design choice ("ship fixes without asking").

The faults are all on the map. Nothing in the model reads the map, so the playtest's trace must stay byte-identical. Positions below are pixels in the named screenshot, as (x, y) from its top left.

## 1. The second yard's ladders miss the line (certain)

**`4-two-yards.png`** and **`5-two-yards-close.png`**: two Rail Terminal yards stand one above the other on a north-south main line.
- **The top yard is right.** Its fans close to the line at x = 265 in `4-two-yards.png`, at (265, 68) and (265, 440).
- **The bottom yard is drawn shifted east.** Its freight shed is at x = 210-248, and its sidings run from x = 250 to 420. Its fans close at (337, 465) and (337, 838), about 72 px east of the line.
- **So the line misses both of the bottom yard's ends.** It stops in a stub of track between the yards, at (265, 445-460), and comes out below the yard at x = 265, from y = 845 down, joined to neither fan.

Find why the second yard's picture is laid off its anchor. Candidates are its stockpiles (narrower than the top yard's) or the way two adjacent yards share a cell.

**What is right instead.** Every yard's fans close on the line it is entered from.

## 2. A train drawn across the sidings, and a track entering a yard from its side (certain)

- **The track enters from the side.** In `4-two-yards.png`, an east-west track meets the bottom yard's east side at (525, 530), among the stockpiles, with no ladder into the sidings. The same line carries on west of the yard, at (0-135, 530).
- **The train is drawn across the sidings.** A train runs perpendicular to them in an L, from (265, 470) down to (265, 532), then east to (335, 532), with locomotives drawn at its ends. `5-two-yards-close.png` shows it as a bar across the sidings at (213-337, 587).

**What is right instead.** A yard's line enters at a ladder. A train in a yard stands on a siding, along it.

If an east-west line must pass a north-south yard, it goes round the yard or joins it at a ladder; it never runs into the yard's side. The batch decides which and records it.

## 3. A 45-degree highway drawn as a staircase (certain)

**`3-diagonal-highway.png`**: the horizontal highway from the west ends at (300, 322). From there, a south-east stretch runs to the junction at (500, 515). It is drawn as a staircase of separate dark blocks with black hatching, not as one diagonal road with its centre line and verges.

**What is right instead.** A 45-degree highway stretch draws as one continuous carriageway, as the rail's 45-degree stretches do.

## 4. Track laid along a street, drawn as a crossing on every plot (likely)

- **A vertical strip of white rungs** runs along a boulevard at x = 1385, from y = 175 to 700, in `1-north2-boulevards.png`.
- **Two more strips in `2-north4-2300.png`:** a vertical one at x = 385, from y = 45 to 740, along a district's edge, and a horizontal one at y = 740, from x = 385 to 1065.
- **The thinner version** in `3-diagonal-highway.png`: the vertical rail at x = 875 runs down the middle of a street from the top to y = 870, with level-crossing marks along it.

The likely reading is that a track and a street share these plots, so every plot is drawn as a level crossing. Find what they are. If track runs along a street, either lay it beside the street, or draw it as street-running track without a crossing on every plot. The batch decides which.

## 5. To look at while the batch is there (not certain)

- **White spiky marks at the boulevards' crossings** in `1-north2-boulevards.png`, at (345, 350), (690, 350), (1040, 350), (345, 690), (690, 690) and (1040, 695). Say whether they are FR0's roundabouts and whether they draw as meant.
- **Whole districts whose ground between the lots is tan**, the gravel road's colour, beside districts that are grey, the paved road's. The middle of `2-north4-2300.png` shows this, in the year 2300. Say whether those streets really are unpaved there, or whether the colour is wrong.
- **A brown dotted line along the road**, at x = 137 down to y = 450, then west at y = 452, in `4-two-yards.png`. At the same zoom the main line is drawn as rails. Say whether it is the legend's "Track" and is meant to draw differently from "Railway".
- **A train standing on the top yard's leftmost siding, over the yard's own standing wagons**, at x = 131 in `5-two-yards-close.png`. FR1-C already names this. Decide whether the yard's standing wagons should clear the siding a train stands on.

## 6. What done looks like

- **A harness section for each certain fault, with a fixture that causes it** on 0.8.3 before checking the fix:
  - §1: two yards stacked on one line, and every yard's fan apexes on the line.
  - §2: an east-west line passing a north-south yard; assert no track enters a yard except at a ladder, and no train position lies across a siding.
  - §3: a 45-degree highway stretch rasters as one carriageway.
  - §4, once it is understood: a fixture of its own.
- **Headless renders** of the same views, after the fix, in `FX3/`.
- **The playtest's trace byte-identical.**
- **The suite green.** Name anything that was already red. If Jerus's game runs beside the suite, rerun a timing red alone once he isn't playing, and say so; never change a budget to pass it.
- **The record:**
  - `GameVersion.VERSION` 0.8.4 and its entry;
  - the row out of `QUEUE.md`;
  - FX2-C, FR0-C and FR1-C updated with what FX3 settled;
  - notes in `FX3-notes.md`, in FX2-notes' shape, with the Sonnet trial's per-slice table.
- **Jerus's city.** It is in one of his slots; find which with `tools.SaveDump`. Read it, or copy it to a scratch path. Never write slots 1-9.
