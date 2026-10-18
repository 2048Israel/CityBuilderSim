# FX2 - the freight map's faults, from Jerus's eye check (2026-10-18)

The brief for batch FX2. `QUEUE.md` links here. The orchestrator wrote it from Jerus's eye check of FR0 (0.8.1) and FR1 (0.8.2): seven screenshots of his Halifax city on the Earth, in `FX2/` beside this file. His words: "i think the images are self explanatory, but even with the bugs, you are doing great". Each item below is a fault, not a design choice ("ship fixes without asking").

The faults are all on the map. Nothing in the model reads the map, so the playtest's trace must stay byte-identical. Positions below are pixels in the named screenshot, as (x, y) from its top left.

## 1. The world line and a train over open sea (certain)

**`6-train-in-the-sea.png`** (3 March 2085, 200 m scale):
- A train is drawn on open sea, bent into a V. One arm runs from (600, 730) down to (740, 870), and the other runs from there up to (920, 740).
- The world line, solid dark grey, carries on from (920, 740) north-east across the water to the view's edge. Its label "to the w..." is clipped at the top right.
- The V's apex sits just past the city's edge, the light line at y = 857. Beyond it the ground is hatched as not the city's.
- No track is drawn under either arm.
- The tooltip shows that sea is the city's own ground: "East 6, bought in month 893".

**`7-far-line-in-the-bay.webp`** (10 January 2085, 1 km scale):
- A track runs south-east at 45 degrees from the city along the bay's west shore, from (630, 462) to the coast at (765, 598).
- A lone grey segment, from (868, 698) to (925, 755), lies in the middle of the bay on the same heading, joined to nothing.

**The likely reading, for the batch to confirm or correct.** Per FR0-notes §1, the world line runs on "from where that arm's heading leaves the city's ground". Here, the city's ground includes the sea districts it bought, so the line starts over water. The bend also suggests the gateway's arm heads south-east while the gateway's bearing is north-east. FR1's route (★8: "the world line's part is 64 plots") then sends the train along both legs, over the sea.

**What is right instead.** No world line, and no train position, over a sea plot or a fresh-water plot. And no V: a train's route meets the world line where the track ends on land, without leaving the land.

How the batch gets there is for it to decide and record. For example, the world line could start at the arm's last land plot, or the arm could be laid so that its heading agrees with the bearing. Either must hold for a generated coast as well as the Earth.

**Where to look:** `Gateways`, the gate arm in `CityRuns`, `ui/MapView.worldLines()` and `trains()`, and `TrainSchedule`'s routes.

## 2. A piece of highway alone inside a block (certain)

**`5-west6-orphan-highway.png`** (near view, West 6, about 3 km north-west of Halifax):
- A short east-west piece of highway lies in the middle of a block, joined to no road. It runs from (488, 252) to (570, 262), with yellow centre dashes and its dark green verge around it, from (485, 240) to (580, 276).
- The far view in `7-far-line-in-the-bay.webp` may show it as a dark dash in the city's west, near (233, 363). That is to check, not certain.

Find what lays or draws it. Candidates:
- a highway plot left from before FR0 grew the gate arm first (★3);
- a highway's end (FR0 ★7, drawn from 4 px) put where no highway ends;
- a plot the runs mark as highway that no net reaches.

**What is right instead.** Every highway plot the map draws belongs to a highway that reaches the city's net or the world line.

## 3. To look at while the batch is there (not certain)

- **A small grey glyph on a highway, short of its end.** It is at (555-595, 625-640) in `1-east-near.webp`, where the highway runs on about 90 px to the coast. Another is at (1755-1800, 622-640) in `3-yard-200m.webp`, mid-highway. It may be a highway's end, or a crossing's bridge, drawn in the wrong place. Say what it is. Fix it if it is wrong.
- **The main line's course in `2-yard-500m.png`.** It runs south from the yard at x = 1172 and turns east at y = 820, under the legend. A diagonal line runs south-east at the lower right, from (1700, 790) to (1880, 1000). Check that this is the world line, and that it joins the main line over land.
- **The world line's label**, clipped at the view's edge in `6-train-in-the-sea.png`. The label should stay inside the view.

## 4. What done looks like

- **A harness section for each certain fault, with a fixture that causes it.** For §1: a city whose gate arm reaches its land's edge while it owns sea beyond. Halifax on the Earth is one, if earth-1.bin is named. A generated coast must be another, so the check runs without the Earth file. The section asserts:
  - no world-line point and no train position on water, over a real run's months;
  - the route meets the world line on land.

  For §2: assert that every drawn highway plot is reached from the net or the world line.
- **Headless renders** of the same views, after the fix, in `FX2/`.
- **The playtest's trace byte-identical.**
- **The suite green.** Name anything that was already red.
- **The record:**
  - `GameVersion.VERSION` 0.8.3 and its entry;
  - the row out of `QUEUE.md`;
  - FR0-C's and FR1-C's rows updated with what FX2 settled;
  - notes in `FX2-notes.md`, in FR0-notes' shape.
- **Jerus's city.** It is in one of his slots. Find which with `tools.SaveDump`. Read it, or copy it to a scratch path, to reproduce the faults. Never write slots 1-9.
