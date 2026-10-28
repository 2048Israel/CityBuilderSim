# FR8 - the trucks' share dealt per industry: notes ({next}, 2026-10-28)

The brief is `docs/queue/FR8.md` (Jerus, 2026-10-20, on FR2's ★5: "pool per
industry"). Built by the batch agent (Opus) with one Sonnet slice.

## 1. What shipped

- **The deal** (`Haulage.deal()`, at the haul, after the city's share): when the
  trucks do not carry the whole need, each sector is first served its standing
  demand - the room it was dealt at the last haul, no more than it asks now -
  the smallest first; the growth over that, the newest demand, is served last,
  pro rata, from the room left (`getNewestShare()`). A sector's share is what it
  was served over its need, a literal 1 when it was served whole; one that asks
  nothing is dealt a literal 1. A giant works landing shortens its own sector,
  not the town's.
- **The readers**: `Sector.getHaulageRatio()` reads `Haulage.getShare(this)`, and
  `billMonth()` bills each sector its legs at its own share, the sum scaled to
  what the trucks could carry. `getShare()` stays the city's (the fleet over the
  whole need), for the screens and the order. The order, the price, the depots and
  the fleet account do not change; the need's total is bit for bit main's.
- **Saved** in Freight's extras: `haul.share.<sector>`, `haul.standing.<sector>`
  (tonnes, scaled by ScaleCheck's copy), `haul.newestShare`, `haul.standingShare`.
  A save from before FR8 has none and deals its first month as the one share it
  ran at, so no format bump.
- **The Freight page's note** says the newest demand runs slower, not every works.
- **LongPlaytest** counts the months a sector ran short of what it was carried the
  month before (`getStandingShare() < 1`) and the worst any sector's own share was.
- **HaulageCheck 11** (the deal): a giant works in a full fleet (its sector alone
  short, every other at a literal 1 where the one share would have run it at the
  city's), the converse, the growth pro rata and a first works waiting a haul,
  the standing demand cut smallest first, and the deal saved and reloaded to the
  bit. Its fixtures were mended where the rule moved them (§3).

## 2. ★ calls (each taken the recommended way, BP1)

1. **★1 The standing demand served by size, the smallest first** (ties in
   `Sectors.KEYS` order), the brief's one choice. A fleet short of what the
   sectors were carried then cuts the largest standing demands, where the cut is
   the smallest part of a sector's own legs, and leaves the most sectors whole.
   `KEYS` is an append-only registry with no economic meaning: in its order the
   cut would always land on the industries added last (Wood Products, Forestry),
   whatever their size.
2. **★2 The standing demand is the room a sector was dealt at the last haul, at
   nameplate**, not the tonnes it actually moved. The need is the plant (FR2's
   ★4), and "pool per industry" is each industry keeping the trucks its plant was
   dealt; it is also the only reading under which the brief's own premise holds,
   "the others at the share they had". Its cost: room kept for a sector's whole
   plant sits idle while it runs below nameplate, and the newest demand gets only
   the trucks past it. The alternative (standing = last month's carried tonnes)
   pools that slack with the newcomer, but every sector below nameplate then reads
   a share under 1 whenever any works grows - the one share's symptom, milder.
3. **★3 A sector that asks nothing at the haul is dealt a literal 1**, and its
   first works, standing after the haul, waits for the next one, as every growth
   waits a haul. The slice's first draft dealt it the newest share: HaulageCheck's
   mine selling all its ore at home (no legs of its own) ran at the floor in months
   the fleet was short.
4. **★4 HomeRailCheck's real run counts a home share "with no room" only if no
   station or spur stood at the haul that struck it either.** On FR8's path
   Freight's distress rule (`Game.runRetirement()`) scrapped its one Rail Depot
   at m1160, after the haul, and the month-end read found a share struck over a
   depot that had gone. Main's run never scraps a station. The model keeps the
   share for the month by design (shares struck at the haul, FR2's ★9); whether a
   scrapped station should end the month's rail shares at once is left (§7).

## 3. The slice, and what the review changed

One Sonnet slice (xhigh): the deal, the readers, the save, LongPlaytest's count
and HaulageCheck 11. The review changed: ★3 (a sector asking nothing read the
newest share, 0 in a short month with no growth), which restored section 3's
bitwise "the mine ... was billed nothing" (the slice had loosened it to a
residue of 1e-12); 11b, 11c and 11e to the literal 1; HomeRailCheck (★4).
Mended where the rule moved them, premises kept: `predict()` bills each sector
at its own share with one cap; `setState()` deals every sector the share given;
section 6 reads the plant's and the shop's own shares, and strikes a fleet of
none before the shop's half fleet so the shop is caused short (under the deal a
half fleet after a .9 one cut only the largest sector).

## 4. What moved in the trace

Against main at b49f4f08 (0.8.62), the model at e1f535df. Both runs: "Nothing.
Every month passed the audit and every reload matched."

| default playtest | main | FR8 |
|---|---|---|
| end | m4000: 765,819 people, GDP $4.4T a month at a price index of 712 | m4005: 544,112, $21.2T at 5,175 (real GDP -34%) |
| m1071 / m2022 | 44,440 / 224,441 | 17,165 / 100,461 |
| the city's share short | 780 months, 44 under 90%, worst 18% (m48) | 699 months, 35 under 90%, worst 25% (m27) |
| **a sector other than the newest demand short** | **780** (the one share: every bulk sector in every short month) | **67** (the fleet short of what the sectors were carried the month before) |
| worst any sector's own share | the city's, 18% | 0% (Materials, m1516: a new works' sector in a full fleet runs at the floor) |
| trucks, depot places at most | 1,121, 1,100 | 1,404, 1,400 |

One run swings a long way with the city's path (FR2's drafts ended this run at
30,614 to 795,408); the ensemble is the measure.

## 5. The ensemble

`tools/ensemble/ensemble.sh fr8`, 2 runners, 80 of 80 runs in 13.9 min, on
e1f535df, scored against `docs/ensemble/0.8.57.md`; committed as
`docs/ensemble/FR8.md`. **23 of 25 criteria pass and no red is new**: A2 and S1
were red in the baseline too (A2 10 months [3 to 31] against 9 [0 to 24]; S1's
median 1.88% against 1.09%, longest spell 63 months against 52).
Growth is within the noise: AUTO2's final population 246,893 [199,706 to 317,402]
against the baseline's 258,838 [91,767 to 334,102] (95%; the band narrowed, the
lowest seed doubled); ZERO_SLACK 159,060 against 171,863; 40,000 people in 16 of
16 seeds at m1,243 [1,110 to 1,480] against m1,189 [890 to 1,438]. No seed stalls.

## 6. The suite

`./batch-check.sh --pick ReadPathCheck ScaleCheck` (21 picked, LongPlaytest in
Sector.java's place): every harness green but MapCheck, after ★4. **MapCheck
is a NEW red on FR8's path, 7 lines**, and main's recorded line passes. Its
fixture city is smaller by month 122 (1,129 owned plots against main's 1,513),
the highway hub it lays toward moves from (48,48) to (80,-16), and sixty months
of Elevated Highways never cross the founding district ("0 plots, month -1");
the picked-block fixture fails beside it. MapCheck is CC1-M's file this day, so
FR8 leaves it untouched.

## 7. Not built, and what is left

- **Not this batch's**: the screens' per-sector shares (FR4 reads the city's;
  `getShare(Sector)`, `getStanding()` and `sectorShares()` are there for it).
  The manual's sentence "One share serves the whole city, so a giant works landing
  slows every bulk sector" is stale until its next edition.
- **Left (FR8-C)**: a station scrapped after the haul leaves the month's home,
  quay and boundary rail shares in force (★4); MapCheck's highway and picked-block
  fixtures read the city's path (§6).
