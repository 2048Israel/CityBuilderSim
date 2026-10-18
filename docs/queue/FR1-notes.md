# FR1 - trains on the map, as the ships are: notes

*0.8.2, 2026-10-18. The second freight batch (`docs/studies/spec-freight-and-infrastructure.md` §4.4 and §7's FR1 row; decision D16, Jerus 2026-10-12: "do rec"). The map's alone: the model gains one tally that nothing in it reads, and the playtest's trace is byte-identical. `DEEPSEEK_API_KEY` was not set on the PC (process, user or machine), so no slice went to DeepSeek. Claude wrote the model, the routes, the drawing and their harnesses. Jerus's rule came mid-batch ("when deepseek isnt avaiable use sonnet 5.5 on extra"), and the slice after it went to Sonnet (§6).*

## 1. What shipped

- **The railway's tonnes by kind** (`sectors.Rail`; RailCheck 7 and 5). `haul()` tallies the month billed's railed tonnes of each kind of cargo (`Good.cargo()`: liquid, dry bulk, boxes, general), landed and shipped, at the share in force: `railedIn()`, `railedOut()`. The ports already tally the boats' tonnes the same way. The kinds come to the tonnes hauled. They are saved in the railway's extras, so a reload's trains are the month's; there is no format change.
- **The trains** (`TrainSchedule`; MapCheck 9). BoatSchedule's shape, almost line for line:
  - a train is a schedule entry: a kind, a direction, a route and an arrival hashed into the month;
  - its place is a pure function of time on the boats' clock (`MONTH_SECONDS` 60);
  - trains a month = railed tonnes of a kind ÷ a train's payload:
    - unit trains of hoppers or tank wagons, 8,000 t;
    - mixed trains, 1,500 t;
    - intermodal trains of 100 boxes, one train both ways, the larger direction counting (the boats' rule).
  - A train runs its whole route in 6 s, stands 3 s on a siding and runs back out.
  - An import arrives full and leaves empty; an export the other way; boxes are full both ways.
  - A frame looks only at the trains whose visit spans the moment, by a binary search of the month's arrivals.
- **The routes** (`CityRuns.wayTo()`, `CityMap.trainRoutes()`). Each route runs:
  - from 64 plots out along the railway's world line, where the train fades in;
  - to the gate arm's end, along the main line and the corridors (the main line's own chain, walked to any yard);
  - through the yard's ladder onto one of its eight sidings, as `TileRaster` lays the fan.

  There is one route a yard and siding. Boxes and general cargo go to the freight station; bulk goes to the other yards while the station handles boxes, else to every yard.
- **Drawn** (`ui/MapView`, `TrainShapes`, `LandMap`). From L1 in, as the boats:
  - at the middle view, a dash of the train's colour along the line;
  - at the near view, each vehicle: a dark locomotive at each end and the wagons between, to scale in length and the bed's width across;
  - from 8 px a plot, each wagon's mark: a hopper's heap of ore, a tank's barrel, the gap between a flat's two boxes, a van's roof.

  The colours are the ships' four: blue in full, orange out full, grey empty, white boxes. The legend gains "Train: wagons in the ships' colours".
- **The freight station's look** (`Game.mapRail()`). It is a container yard when the railway landed or shipped boxes the month billed (`TrainSchedule.boxesByRail()`). That is the rule the trains are sent by, so an intermodal train never pulls into a yard of ore. This is FR0's ★4, refined.

## 2. Calls taken where the spec left room (★)

All confirmed as built (Jerus, 2026-10-18, "rec all").

1. **★1 Past 30 trains a month, each train drawn stands for several** (`MOST_A_MONTH`, `stands()`), every kind alike. The 4,000-month playtest's city ships 1,166,821 t a month across its boundary (Rail's header). Railed whole, the spec's rule alone would draw that as 146 unit trains in a 60-s month, one every 0.4 s on one line, and trains 21 plots long would overlap into a ribbon. The spec's own worked example, 25 a month, "reads as a busy line, not a blur", so the cap sits just above it. It is the trucks' rule from D21, one mark per N loads.
2. **★2 A locomotive at each end** (distributed power, as a unit train has). The train leaves the way it came with no run-round, so nothing jumps when it departs.
3. **★3 A city with no gateway draws no train.** Its railway has no world line for a train to come in on, and no main line or freight station (FR0's rule). FR0 found half of twelve worlds' default sites coastal with no gateway, so their railways run empty on the map.
4. **★4 Bulk stays off the freight station while it handles boxes**, so a hopper never pulls into a yard of boxes. With no other yard, bulk goes there anyway. A station handling no boxes takes its share of bulk.
5. **★5 Liquids ride unit trains of tank wagons at the hoppers' 8,000 t.** The spec gives payloads for bulk, mixed and intermodal trains, and wagon shapes for liquids, but no liquids payload.
6. **★6 An intermodal train is 40 flat wagons drawn, two boxes each.** "The payload ÷ 100 t" would give nine wagons for 900 t of boxes, and [R46]'s 750 m train is 50 wagons for about 100 boxes. Unit trains are 40 wagons drawn and mixed trains 15, by the spec's rule.
7. **★7 A vehicle is drawn 0.42 plot wide**, about the track bed's. A true wagon, 3 m, is a tenth of a plot, under a pixel at the near view. Its length is to scale: a wagon 0.5 plot, a locomotive 0.7.
8. **★8 The world line's part is 64 plots**, about 1.9 km. Over it a train fades in as a boat fades in the abyss. It is part of the 6-s run, so a city's main line, a few hundred plots, takes most of the run.
9. **★9 A yard entered on a 45-degree stretch has one route, to its anchor**, with no siding. The yards are laid where the track runs straight through a cell, so none of the fixtures has one.

## 3. Found on the way

- **In a steel city the mixed train is the common one.** Steel, fabricated steel and machinery are general cargo (`Good.cargo()`), so they ride 1,500-t mixed trains, not unit trains. RailCheck's trading city rails 63,775 t a month: 34,483 t of dry bulk in, 28,178 t of general cargo out, 753 t of boxes and 296 t of liquids. That comes to about 4 hopper trains and 19 mixed trains a month, an intermodal train in three months of five and a tank train in one month of 27: about 24 a month, under ★1's cap.
- **MapCheck's played city rails nothing**, so it could not show the station's container look. RailCheck 7 holds that on its own city, whose railway moves boxes. MapCheck holds the converse.
- **MapCheck counts the plane's legend entries**, so the train's entry moved its sum by one. The legend grew, and the premise is unchanged.
- **The routes are cheap.** The yard city's 2 yards make 16 routes, found in 0.7 ms; the longest is 250 plots. At 10 billion, 1,600 synthetic yards on one main line make 12,800 routes. A frame costs 0.003 ms on an L0 or an L1 screen against the 0.5 ms bound, because the cap keeps a month to 29 trains (each standing for 468,837) and a frame looks only at those on the line.

## 4. Not built

Nothing of the FR1 row is left. Two things the trains could use are FR0's, still pending as **FR0-L**:

- **Sidings into the bulk works (D14).** With them, a hopper train could end at the iron works rather than at a yard.
- **Access roads (D17)**, which FR5's trucks would drive.

## 5. To check by eye (FR1-C)

- **The trains on his own city**, at the near view (vehicles, marks) and at the middle view (dashes), and their pace on the clock at 1x. The view's drawing is JavaFX (`MapView.trains()`). It was compiled and its model run headless here, but no one has watched it in the game.
- **The legend's new entry**, on the expanded map.
- **His calls on ★1 to ★4.**
- **A train standing on a siding is drawn over the yard's own standing wagons**, FR0's (in the fleet's share of its wagon sets), when the two share a siding.
- **Headless renders** of MapCheck's yard city are in `FR1-notes/`, drawn by `RenderTrains.java` beside them from the shapes MapView fills. Each has a month of every kind: ore in, fuel in, steel out, boxes both ways.
  - `fr1-station.png`: the freight station at 8 px a plot.
  - `fr1-station-close.png`: the station at 16 px, with the wagons' marks.
  - `fr1-gateway.png`: the gateway, where trains fade in along the world line.
  - `fr1-middle-view.png`: the middle view at 2 px, where trains are dashes.

  Each caption names its moment's trains.

## 6. The Sonnet trial (Jerus, 2026-10-18)

Jerus's words: "when deepseek isnt avaiable use sonnet 5.5 on extra, see how that goes, same way youd use deepseek". It reached the batch after the model, the schedule, the routes, the drawing and their harnesses were written and committed, so those stayed Claude's. The slices after it went to an Agent subagent on Sonnet at extra effort (`xhigh`). Each was briefed as `docs/deepseek.md` briefs DeepSeek: the files, the shape to copy, what it must and must not touch. It compiled but ran no harness, because the machine's one game JVM was the docs pass's. Its diff was reviewed as a stranger's, then committed.

| slice | what it did | what the review changed |
|---|---|---|
| The trains on ReadPathCheck's read path; the legend's train in RailDrawCheck 20 | every new reader called on a loaded game, beside the boats' (`railedIn`/`railedOut`, a schedule on one route, a frame, each car's shapes), null positions guarded; the legend's check and the section's title | only the comment, wrapped to the 130 columns of the lines round it. Two files as briefed, LF kept, compiled first time; its report named the one API edge it was unsure of (`Kind.of()` null), correctly harmless. 84k tokens, 18 tool calls, 72 s |
