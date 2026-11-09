# EquipmentForms.java - 124 lines · 5 methods · 0 constants · model

`ham/citybuildersim/EquipmentForms.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> Sets R0c's 1900 forms from the buildings still without the equipment that ends them: the steam share of each sector, the farms' horse share
> and the carts' horse share, each the unfitted part of what it scales.
> 
> WHY (R1, 2026-11-05; docs/queue/R1-notes.md calls 4 and 6). R0c built the forms as shares nobody set, off in every played city, and its
> call 3 said "R1 fits equipment building by building and sets the sector's share from its count; the forms read one number". This is that
> number's one writer. A stack keeps how many of its buildings lack each piece of equipment (BuildingsStacks.getUnfitted()), and a piece names the
> form it ends (Equipment.Form), so a form's share is the unfitted part of the sector, weighted by what the form scales: the steam share by each
> building's grid draw (its electricity), the farms' horses by their land, the carts by the depots' truck room. Fitting a piece (R1b) is then a
> count going down and nothing else.
> 
> SET ONLY WHERE A COUNT IS OR WAS (call 6). A share is written only while some stack has an unfitted building that ends its form, and once more
> when the last is fitted (to 0): a sector with no count, and none last time, is not touched, so the shares a harness sets by hand
> (Forms1900Check) stand and a played city's month calls nothing. Which shares this has driven is kept in memory and not saved: after a load the
> counts, which are saved, drive them again (Game's load path).
> 
> Called where the Policy hears the month (Game.policyHearsTheMonth(): the founding, the top of every month, a load), before anything is made,
> and once more in every month right after its sites advance (R1b; SimulationEngine.simulateMonth()), the last statement of the month that changes
> a stack's quantity or its counts and before everything the month derives from them, so that the shares a city rests in between two presses are
> its counts' at rest and not the month's top's: a save written there and the load that reads it hold the same shares and the same derived
> state. clear() forgets which shares were driven, where Game rebuilds its world (R1b), so a city founded or loaded into a Game that played
> another starts as a fresh process does.

**Uses:** [Equipment](Equipment.md) (9), [BuildingsStacks](BuildingsStacks.md) (4), [Game](Game.md) (1), [Sectors](Sectors.md) (1), [Agriculture](Agriculture.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [Sector](Sector.md) (1)

**Used by (1):** [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `private final Set<String> driven` | The shares driven by the last apply(), each as the form's name and the sector's key ("STEAM:Industry"; "FARM_HORSES:Agriculture"; "CART_HORSES:Freight"): the ones a count that is gone still has to set back to 0. |
| 41 | `double total, off` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 34 | 91 | **type** `public final class EquipmentForms` | Sets R0c's 1900 forms from the buildings still without the equipment that ends them: the steam share of each sector, the farms' horse share and the carts' horse share, each the unfitted part of what it scales. |
| 40 | 3 | **type** `private static final class Weight` | What a form scales over a set of stacks: the whole, and the part of it that lacks the equipment. |
| 45 | 45 | `public void apply(Game game)` | Sets every form's share from the counts. |
| 95 | 9 | `private double drive(Equipment.Form form, String sectorKey, Weight w)` | The share to set for one form of one sector, or -1 for none to set: set when some of it is unfitted or was last time (then to what the counts say, 0 when they are gone), and remembered as driven only while some is. |
| 106 | 8 | `private static int unfitted(BuildingsStacks stack, Equipment.Form form)` | How many of a stack's buildings lack a piece that ends this form: the pieces' counts added, never more than stand (a building lacking two such pieces is counted once). |
| 116 | 3 | `public void clear()` | Forgets which shares were driven: Game.buildWorld() calls it, so that a city founded or loaded into a Game that played another city with counts sets nothing to 0 it never drove. |
| 121 | 3 | `public Set<String> driven()` | The shares this has set and not yet set back to 0, as "FORM:sector key", sorted: for the harnesses. |

