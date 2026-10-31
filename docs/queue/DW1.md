# DW1 - platforms in deep water, at deep water's cost

Queued 2026-10-31 on Jerus's word: "offshore oil platforms cant build on
deep oceans... so uh", then, asked which way, "Allow deep, costlier
(Recommended)".

## What is there

- A platform stands only where the sea is no deeper than
  `Oil.PLATFORM_MAX_DEPTH_M` (150 m): the fixed platform's limit, a jacket
  on legs (the research's 3.2, spec-oil 2.7).
- `Oil` checks the field's centre depth (about line 805). `BuildScreen`
  (about 4345) and `OilView` (about 983) say why a deeper field cannot have
  one, and EarthFoundingCheck holds the line on the real Earth (Rotterdam's
  North Sea passes; the sea off Valparaiso, 656 and 2,369 m, does not).
- The platform's wells: `PLATFORM_PLATEAU_MONTHS` 36, then
  `PLATFORM_KEEPS_A_YEAR` .915 (the IEA's shallow-offshore decline).

## What to build

- **Past 150 m, a floating platform** (semi-submersible, tension-leg, spar
  or FPSO; the research picks the one the game models), to the deepest
  depth production reaches today (about 3,000 m; find the figure and its
  source).
- **Its cost rises with depth.** Build cost, running cost and each well's
  cost, from real deepwater figures: field development cost per barrel of
  reserves by water depth, and the day rates of deepwater drilling. Each
  figure gets its source key, and the sources must allow commercial use if
  their figures ship (`docs/studies/licences.md` once LC1
  lands). Deepwater wells' own decline and plateau too, if the sources
  differ from shallow water's.
- **The same building or a new one**: if a floating platform is a new
  `BuildingType`, its id is the next free one at the end of
  `buildings.json` (ids are permanent; saves are keyed by them). Prefer one
  platform whose numbers follow the depth, if the sources allow it.
- **The screens say it**: the build card and the oil page show the depth and
  what it costs there, and no longer refuse deep water. The refusal stays
  only past the deepest depth.

## Checks

EarthFoundingCheck's Valparaiso line is re-premised from "no platform
stands" to "a platform stands, at deep water's cost", and the North Sea
line holds. Add an Oil harness section that causes a deep field and asserts
the cost against the constants. Run `--pick`, then the gate's suite. The
ensemble runs only if the trace moves: the playtest's founding has no sea
oil, so likely not. A save stays readable: new keys only, and the format
moves only by CLAUDE.md's rule.
