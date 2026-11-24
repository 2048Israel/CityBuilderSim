# PW3 - wind and solar by place and season, and the power planner

Builder: Claude (Opus), headless, started by the orchestrator 2026-11-23, on `batch/PW3` cut from origin/main
0c7f55602 (0.9.195, LP-G1, SAVE_FORMAT 63). The brief: QUEUE.md's PW3 row and
[spec-electricity §5](../studies/spec-electricity.md#5-the-batches), with PW1-P's five gaps (PW1 notes §0, §2, §6)
and PW2's ground finding (REV-PW2).

## 0. Scope (as decided, before the code)

- **Wind, solar and hydro by calendar month**, from EIA's 2025 fleet (EPM table 6.07.B), each month's index its
  capacity factor over the mean of the twelve, so the twelve average exactly 1 [D].
- **Solar by place**: the farm's annual capacity factor at a city's latitude is EIA's US fleet 24.0% times PVGIS's
  yield in the city's 10-degree band over PVGIS's yield at the US fleet's latitudes; its monthly shape is the band's
  own (PVGIS, both hemispheres, the south's months turned six). A generated world (latitude NaN) is the US fleet.
- **The south's season**: a city south of the equator reads SEASON, wind, solar and hydro six months on.
- **Credits fall with their share**: wind's and solar's firm credit falls in a straight line from its own figure to
  a tenth of it as the variable rows' share of the energy rises from 0 to a half (the study's credit(p, share)).
- **The planner** (BuildAdvice's POWER branch, so the advice, the auto-builder and the playtest player all follow
  it): the reserve test first, the plant ranked by its price a firm kW-year in the slice it serves (the screening
  curve, with its fuel); then, when the test passes, the spread, re-dispatched over a year.
- **PW1-P's gaps**: (1) the works' own power counted as supply, (2) a plant on site counted only from when it opens,
  (3) the screening curve, (4) the south's season, (5) a plant's firm supply at its own posts' fill.
- **groundFor()**: the planner prices a plant's ground at what it can buy; a young city assembles a large plant's
  ground over several offers (Jerus, 2026-11-23: "you just buy more land").
- **Research**: E25 (the single-axis tracker) enters `research.json`: it fits the farm PW3 builds (★G). E31 does not
  (★G): Pending with PW7/FE9. E29 is the storage batch's, queued by the ship file.

## 1. Sources

- **EIA, Electric Power Monthly, table 6.07.B** (capacity factors for utility-scale generators not primarily using
  fossil fuels), 2025, fetched 2026-11-23; US public domain. Wind 37.4 37.2 43.1 40.7 32.1 32.2 27.5 23.8 23.1 33.8
  35.4 39.2 (annual 33.8%); solar PV 16.2 18.7 23.6 27.5 29.3 31.9 31.9 29.7 26.7 22.4 16.9 13.1 (24.0%); hydro
  34.7 35.1 38.2 40.8 42.8 39.8 34.2 32.3 26.1 24.9 28.5 36.1 (34.4%). The spec's figures (§2.3-2.4: wind Jan 1.11,
  Mar 1.28, Jul 0.81, Sep 0.68; solar Dec 0.55, Jun-Jul 1.33; hydro May 1.24, Oct 0.72) are these.
- **PVGIS 5.3** (European Commission, JRC), PVcalc with the ERA5 database, a 1 kWp c-Si system at 14% losses, a
  fixed rack at optimal angles and a single horizontal N-S axis tracker; `docs/studies/electricity/pvgis.py` and its
  output `out/pvgis.txt`. The licence: the PVGIS pages link the Commission's legal notice, "content owned by the EU
  on this website is licensed under the Creative Commons Attribution 4.0 International (CC BY 4.0) licence ...
  reuse is allowed, provided appropriate credit is given and changes are indicated". Credit "PVGIS (c) European
  Communities"; the change is the bands' averaging and normalising. Recorded in `docs/studies/licences.md`.

## 2. The calls (★)

- ★A **Hydro by month too.** Spec §2.4 is PW3's section; the river's month is EIA's hydro fleet, normalised.
- ★B **The south turns six months**, SEASON with it (the study's); a city at the equator is the north's.
- ★C **Credits as dials, from the study**: wind's peak credit 0.730 of its average, solar's 2.621 of its average
  [M, the study's dispatch of the S4 load]; the fall to a tenth (CREDIT_FLOOR) at a half of the energy (the study's
  credit(p, share)).
- ★D **The planner's peak is the year's peak hour.** PLAN_PEAK becomes PEAK_HOUR / (1 - losses), the study's 1.549
  of the mean draw [D, S4: 760,649 / 491,034 MW], so the NEEDS YOU lines (yellow 0.75, red 1) and the automatic
  builder's 15% slack are the study's reserve test (NERC's 15% margin) without a new line.
- ★E **One life for every row**: the game's 20-year bond life and its real rate (BuildAdvice.lifeFactor), as every
  other order is valued; buildings.json has no life field and the study's per-row lives would be a new one.
- ★F **groundFor() assembles**: when the standing listing cannot cover, the plan prices the rest at the listing's
  going rate and place() buys offer by offer (each purchase lists the place's next), until it covers, the money
  runs out or a cap on offers is reached. Ground bought and not built on stays the city's.
- ★G **E25 fits, E31 does not.** The tracker raises the farm's yield a kWp (PVGIS tracked over fixed, by band):
  before E25 is known the farm runs at the fixed rack's yield, after it at the tracked (the undone pattern, today's
  figure the tracked). The tandem module raises a module's watts a square metre, not its yield a kWp: its gain is
  the farm's ground, which is FE9's question, so E31 stays Pending with PW7/FE9.

## 3. Old saves

(to be stated when the code is final)

## 4. The checks

(the harnesses run, each with what it showed)
