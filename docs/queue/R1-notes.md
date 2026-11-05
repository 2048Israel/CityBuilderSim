# R1 - recipes and equipment as data, the first part: equipment on each building (0.9.15, 2026-11-05)

## WHERE IT STANDS

**Ready** (04:45). Branch `batch/R1` from `batch/R0c`'s ship file; main
(0.9.14, R0c in, e668e9cf) merged at 7e98567a. Slice A (the data, the count
on each building, the forms set from it, the save) is bfd75c17, slice B (the
payback rule's verdicts) 5ccd06ef. `--pick` on the merge: exit 3, known reds
only (PriceLevelCheck, MapCheck). The default playtest is main's to the byte
but for its run times, so no ensemble. R1b, R1c and R1d go in the ship
file's `## queued` (§1).

## 1. What R1 is, and where it splits

The brief is research-design §9 item 2 with research-1900 §3 and §12 item 4,
and what R0c left it (Pending R0c-R) and R0 (call 2: R0d's 1901 ladder goes
on with R1's 1900 recipes):

- `buildings.json`'s `recipes` and `equipment` (research-design §2.1, §2.2);
- a building runs its best known recipe at the city's prices;
- equipment fitted only when it pays over its life, and refits ranked with
  the sector's new buildings in `SpreadPlanner.best()`;
- the city's buildings get a Fit action;
- the known-in-1900 equipment (research-1900 §3) unfitted at founding, and
  R0c's forms (the farms' and the carts' horses, the factories' steam) set
  from it; the tractor's petrol, the team's price, the carters' sizing and
  the steam share in the brownout (R0c-R).

By `docs/batch.md` 3 that is four batches, and the last one moves every
city's trace:

| part | what | why there |
|---|---|---|
| **R1, this batch** | **equipment as data and on each building, off**: the `equipment` lists in `buildings.json`, a count of the unfitted buildings on each stack (saved), R0c's forms set from the counts each month, and the payback rule (§2.2 steps 1-4) as each piece's verdict, read on demand | the state and the rule everything later reads; nothing moves in a played city (every building fitted, as today) |
| R1b | **the fit acted on**: the fitting bought (its goods at the city's market, its work) one order at a time, ranked with the sector's new buildings in `SpreadPlanner.best()` (§2.2 step 5); new buildings with every piece that pays at the order (step 6); the city's buildings' Fit action; a city founded the 1900 way (the known pieces unfitted), off by default | the money path, and the planner, every processing sector's |
| R1c | **recipes as data**: the `recipes` lists, the best known recipe each month (`SpreadPlanner.cityValue()`), the tractor's `PETROL` in today's farm (R0c ★5), the 1900 recipes of the §3 rows that have no form yet (open hearth, by-product coke ovens, coal cutter, steam shovel, rock drill, rotary kiln, pulp, binder, Hulett unloader, X-ray, antitoxin) | each 1900 recipe is its own sourcing |
| R1d | **switched on, with R0d's ladder**: a 1900 city founded unfitted, the 1901 wages, judged on a 1900 city (the carters' sizing, R0c ★7; the team's price, ★6; steam in the brownout, ★8), the ensemble | the one that moves the trace; R0's call 2 ties the ladder to it |

## 2. The calls (★)

1. **★ The split above.** Recommended and taken. Why: R0c built its forms off
   (its ★1) and R0 tied the 1901 ladder to R1 (its call 2), so the part that
   moves the trace is one batch with the ladder and the ensemble, not four;
   the money path and the planner (R1b) touch every processing sector's
   orders, and each 1900 recipe (R1c) needs its own sources. This part is the
   state they all read, with no figure of the month moved.
2. **★ Equipment only where a 1900 form stands.** Recommended and taken: the
   electric motor drive (ends the factory's steam), the motor truck (ends the
   carts' horses) and the tractor (ends the farm's horses). The other §3 rows
   have no 1900 form in the game: fitted, they would save nothing, so they
   come with their 1900 recipes in R1c.
3. **★ The electric drive on the manufacturing templates.** [P1.8]'s 4.8% and
   82.3% are of factory power equipment, so the piece goes on the factories:
   Food Processing's three, Industry's two bakeries, Manufacturing's four,
   Automotive's five, Wood Products' two sawmills and the Paper Mill, and the
   Construction Materials Plant. Not on the works that are electric by
   nature (the arc furnaces and mini-mills, the electrolytic refinery), the
   Pulp Mill (it draws nothing) or the mines, smelters, kilns and refineries,
   whose 1900 power is part of their own 1900 recipe (R1c). Recommended and
   taken.
4. **★ A count on the stack, not a flag on a building.** A stack is a number
   of buildings of one template with no building of its own, so the state is
   the stack's count of unfitted buildings by piece, never more than stand; a
   building that finishes comes fitted (today's tech, until R1b's order
   asks); a building scrapped takes an unfitted one first (the older plant).
   The form's share is the unfitted part of the sector, weighted by what the
   form scales: the steam share by each building's grid draw, the farm's
   horses by its land, the carts by the depot's truck room. R0c's ★3 asked
   for exactly this ("R1 sets the sector's share from its count").
5. **★ The counts are saved, though empty in every played city.** Unlike
   R0c's shares (its ★2) the count is the city's capital, which R1b fits
   against: saved by template id, written only where a stack has any. No
   format bump: an old save has no key, which Gson leaves alone, and reads as
   every building fitted, today's. Recommended and taken.
6. **★ The forms are set only where a count is or was.** A sector's share is
   set from its counts only while one of its stacks has an unfitted building,
   and once more to 0 when the last is fitted, so the shares a harness sets
   by hand (Forms1900Check) stand, and a played city's month calls nothing.
   Set where the Policy hears the month (`policyHearsTheMonth()`'s three
   places: founding, the month's start, the load), before anything is made.
7. **★ `since` is a year or a catalogue code.** A year is known from its
   January; a code ("PE14", the tractor, 1917) stays unknown until R2's
   catalogue reads it, so in this part the tractor is data and never pays.
   Recommended and taken: research-design §2.1's shape.
8. **★ What fitting buys is goods, as data.** `"buys": {"MACHINERY": 10.9}`
   (or `{"TRUCKS": 100}` for a depot's trucks) and `"work"` in
   `BUILDING_WORK` points, priced at the city's own price; research-design's
   single `machinery` figure generalised, because the truck's capital is a
   fleet of `TRUCKS`, not machinery. The design's 10% for engineering is left
   out until R1b's order, which buys it or not. Recommended and taken.
9. **★ The estimates.** Electric drive: 8 kg of motors, shafting and
   switchgear a kW of peak draw, the peak the month's mean over a 0.6 load
   factor (est.), so 0.0133 t of `MACHINERY` a unit of the building's
   electricity; fitting work a tenth of its construction points; a life of
   300 months (est.). The motor truck: the depot's truck room in `TRUCKS`;
   fitting work 0; a truck's life (Haulage's). The tractor: 3 t of
   `MACHINERY` a hundred acres of the farm's land (a 1917 Fordson was about
   1.2 t; est.), 180 months.
   All marked est. in `buildings.json`'s readme; R1d judges them on a 1900
   city.
10. **★ The verdict is read on demand, never in the month.** Research-design
    §2.2's verdicts (fitted; pays, ranked by earnings on cost; not worth it
    yet, with what it earns back of its cost; not known) are a getter for
    R1b's order and R5's screen, so this part moves no figure and costs no
    time a month.
11. **★ T is the piece's life.** Research-design §2.2's T is the lesser of
    the piece's life and the building's remaining life, but a stack keeps no
    building's age. Recommended: the piece's life, until R1b's vintages; the
    "won't fit" verdict waits for them.

## 3. Slices

| slice | what | state |
|---|---|---|
| A | `Equipment` (the piece, read from `buildings.json`), the stack's unfitted counts, `EquipmentForms` setting R0c's shares, the save; `EquipmentCheck` 1-7 | bfd75c17 |
| B | `EquipmentFit` (§2.2 steps 1-4, pure) and each piece's verdict at the city's prices; `EquipmentCheck` 8-11 | 5ccd06ef |

Both drafted by Sonnet 5.5 at xhigh, reviewed here. What the reviews found
and what the slices decided:

- **The load says the forms twice.** `policyHearsTheMonth()` runs in the
  load about 350 lines before the stacks stand, so the load path restores
  the counts after `orderStacks()` and calls `EquipmentForms.apply()` again
  there, before `rebuildSimulationState()` re-derives the factories' grid
  draw (EquipmentCheck 7 fails without it).
- **A scrapped building is an unfitted one first** (★4): with 2 of 5
  unfitted, scrapping one leaves 1 of 4.
- **The steam gain** reads the building's billed draw (`powerBought()`, as
  R0c's `steamCoal()` does), the coal at `SpreadPlanner.cityValue()` and the
  power at the grid's price a unit; **the cart gain** a depot's truck room x
  `TONNES_A_TRUCK_MONTH` x (`horseEstimate()` less the new
  `Haulage.truckRunningEstimate()`: `estimate()`'s diesel and drivers, the
  trucks' wear and return left out, since the trucks are the cost); **the
  cost** each good at the city's price and the work at the builders' price
  a point; **the rate and hurdle** a building order of the whole cost would
  get (SpreadPlanner's own hurdle, to the bit; the annuity at BuildAdvice's
  convention).

### Found: the electric drive's fitting work is most of its cost (for R1d)

At the fixture town's prices in 1900 a Bakery's electric drive does not pay
yet: over its life it earns back about 5.4 of its 8.9 (thousands), and 8.8
of the 8.9 is the fitting work (a tenth of the building's construction
points, ★9), not the motors. The depot's motor truck pays easily. Both are
estimates on a city that is not a 1900 city yet (today's wages beside 1900
prices); R1d, with the 1901 ladder, judges them, and the work's tenth is the
first figure to look at.

## 4. Checks

`--pick` on the merge with main (7e98567a; 104 of 174 harnesses and
LongPlaytest, for 14 files changed since e668e9cf), the gate's world files
named: **exit 3**, "known red only - PriceLevelCheck, MapCheck"
(`docs/notes/gate.txt`: RT1's twin parting, DM2's 2h); two timing lines
SOFT, not counted (SaveSlotCheck 1, MapCheck 1). `pick.sh` asks for the
gate's stranger's review of the saves (`DataSave`: the unfitted counts by
template id, no format bump, ★5).

The default playtest (2,405 months) on R1's tip against main's e668e9cf,
both run here back to back with the world files: byte-identical but for
"in 62s"/"in 60s" and the layout windows' "1.8 s"/"1.6 s". No ensemble
(batch.md 6): nothing in a played city is unfitted, so no form is set and no
verdict is read.

Found after the run: EquipmentCheck 7 compared the saved counts with the
stacks' quantities after a month, which a month without the world files
moves (the investors put up a building, which comes fitted). The fixture now
keeps the counts as set; EquipmentCheck is green with and without the files.
