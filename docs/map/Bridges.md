# Bridges.java - 315 lines · 30 methods · 4 constants · model

`ham/citybuildersim/Bridges.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The city's long bridges, built and building: for each, the template it was ordered as, its kind, the two landings and the
> landmass at each end, its length, the depth of the water under it, the chain of plots it runs along as its corner plots,
> and whether it stands, is on site or is a stopped shell - and, as Landmasses.Joins, the landmasses a standing one joins.
> 
> WHY THIS EXISTS. spec-earth2-terrain 6.5 and 6.6 (ET6). ET5 left a slot in the linked set for the second way to link two
> landmasses, "a long bridge from a linked landmass links the one it reaches", and the map needs to lay a built bridge
> along its path (CityRuns, DistrictPlan, TileRaster.DECK) and read it from the model, since the model never reads the
> map. A building is a template and a count with no place in it, so where a bridge runs, and between which landmasses, is
> kept here - as ET5 keeps where the sea terminals stand: fitted to the buildings' own counts at every month's settle
> (reconcile()), a completion taking the oldest bridge on site to standing, a stop the newest to the shell, a restart the
> shell back to the oldest end of on site. A bridge joins its two landmasses once it STANDS: one on site or a shell never
> does. An order of n is n parallel spans on the same survey, n records the builders finish one after another.
> 
> SAVED, whole: DataSave.bridges holds a row for each (toState()), absent while the city has none. Nothing is derived, since
> the survey that found a bridge's path read the ground as it was when it was ordered and a later purchase could not find
> the same path again.

**Uses:** [BridgeWorks](BridgeWorks.md) (3), [Landmasses](Landmasses.md) (1), [BridgeSurvey](BridgeSurvey.md) (1)

**Used by (7):** [BridgeCheck](BridgeCheck.md), [DataSave](DataSave.md), [Game](Game.md), [LandManager](LandManager.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md)

## Sections

| line | section |
|---:|---|
| 125 | THE ORDERS - a bridge is ordered, stopped, restarted and built |
| 218 | THE LINKS - what a standing bridge joins |
| 238 | THE CHOICE - the landmass the card bridges toward |
| 254 | READS - plain getters; none fills anything |
| 292 | THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 29 | `Bridges.STANDING` | `0` | A bridge that stands. |
| 32 | `Bridges.ON_SITE` | `1` | ...one the builders are at work on. |
| 35 | `Bridges.SHELL` | `2` | ...one a stopped order left standing as a shell. |
| 249 | `Bridges.NONE_CHOSEN` | `Integer.MIN_VALUE` | What chosen() says when the card has picked nothing: the rule decides. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `public int template` |  |
| 59 | `public String kind` |  |
| 60 | `public int state` |  |
| 61 | `public long nearX, nearY, farX, farY` |  |
| 62 | `public int nearLandmass, farLandmass` |  |
| 63 | `public double lengthM, deepestM, meanM` |  |
| 64 | `public long[] path` |  |
| 100 | `public List<Row> bridges` |  |
| 106 | `private final List<Row> rows` | Every bridge, oldest first; a template's records stay in the order they were ordered. |
| 109 | `private final TreeMap<Integer, Integer> chosen` | The landmass the card chose to bridge toward, by template (ONE order's: spent by it and forgotten at the month's settle); transient, never saved. |
| 111 | `private Stock stock` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 26 | 290 | **type** `public final class Bridges implements Landmasses.Joins` | The city's long bridges, built and building: for each, the template it was ordered as, its kind, the two landings and the landmass at each end, its length, the depth of the water under it, the chain of plots it runs a... |
| 38 | 13 | **type** `public interface Stock` | What Bridges reads of the buildings: Game wires it; a bare one has none and no bridge is fitted to a count. |
| 40 | 1 | `boolean known(int template)` _(in Bridges.Stock)_ | Whether the template is one of the bridges'. |
| 43 | 1 | `int standing(int template)` _(in Bridges.Stock)_ | The finished bridges of a template. |
| 46 | 1 | `int onSite(int template)` _(in Bridges.Stock)_ | ...those under construction. |
| 49 | 1 | `int shell(int template)` _(in Bridges.Stock)_ | ...those a stopped order left standing as a shell. |
| 57 | 40 | **type** `public static final class Row` | One bridge, as saved and as read: its template, kind, state, landings (the near on the landmass the city has linked, the far across the water) with the landmass at each, length and depths (metres) and the chain's corn... |
| 66 | 1 | `public Row()` _(in Bridges.Row)_ |  |
| 69 | 7 | `public BridgeWorks.Kind kind()` _(in Bridges.Row)_ | Its kind. |
| 77 | 1 | `public boolean standing()` _(in Bridges.Row)_ |  |
| 79 | 17 | `Row copy()` _(in Bridges.Row)_ |  |
| 99 | 5 | **type** `public static final class State` | DataSave's bridges (ET6): every bridge the city has built or is building. |
| 102 | 1 | `public State()` _(in Bridges.State)_ |  |
| 114 | 1 | `public Bridges()` | A bare one: no bridge. |
| 117 | 4 | `void reset()` | Back to a bare one (LandManager.reset(): a new city). |
| 123 | 1 | `void setStock(Stock stock)` | Wires the buildings' counts (Game, once). |

### THE ORDERS - a bridge is ordered, stopped, restarted and built (lines 125-217)

| line | len | member | says |
|---:|---:|---|---|
| 133 | 20 | `void ordered(int template, BridgeWorks.Kind kind, BridgeSurvey.Result survey, int n, boolean instant)` | An order of n bridges of a template was placed on a surveyed crossing, after the stack took it: n records on the same survey, on site - standing at once for an order built at once (`instant`). |
| 161 | 9 | `void reconcile()` | Fits every template's records to the buildings' own counts (at the month's settle and at a load): a completion has taken the oldest bridge on site to standing, a stop the newest to the shell, a restart the shell back ... |
| 171 | 5 | `private List<Row> of(int template, int state)` |  |
| 177 | 30 | `private void fit(int template)` |  |
| 208 | 3 | `private void drop(List<Row> list, int count)` |  |
| 213 | 4 | `void settle()` | The month's settle (Game.settleConstructionControl()): the choices are forgotten (one order's), the records fitted. |

### THE LINKS - what a standing bridge joins (lines 218-237)

| line | len | member | says |
|---:|---:|---|---|
| 224 | 13 | `public int[] joinedTo(int id)` | The landmasses the standing bridges join to this one, both ways: the far end of one whose near end is on it, the near end of one whose far end is. |

### THE CHOICE - the landmass the card bridges toward (lines 238-253)

| line | len | member | says |
|---:|---:|---|---|
| 243 | 1 | `void choose(int template, int landmass)` | The card's pick of the far landmass for a template's next order (ONE order's). |
| 246 | 1 | `public int chosen(int template)` | The landmass the card picked for a template, or NONE_CHOSEN. |
| 252 | 1 | `void clearChoice(int template)` | An order was placed: its pick is spent. |

### READS - plain getters; none fills anything (lines 254-291)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 5 | `public List<Row> all()` | Every bridge, oldest first, as read-only copies. |
| 266 | 1 | `public int size()` | How many bridges the city has built or is building, in any state. |
| 269 | 5 | `public int count(int state)` | How many are in a state (STANDING, ON_SITE or SHELL). |
| 276 | 5 | `public int count(int template, int state)` | How many of a template are in a state. |
| 283 | 5 | `public double standingLengthM()` | The standing bridges' length, metres, all together. |
| 290 | 1 | `public boolean joins(int landmass)` | Whether a landmass has a standing bridge at either end. |

### THE SAVE (lines 292-315)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 6 | `public State toState()` | DataSave's bridges: fresh on every call (the autosave's snapshot shares nothing), null when the city has none. |
| 305 | 10 | `void restore(State s)` | Puts the bridges back as saved; null, none. |

