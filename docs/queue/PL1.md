# PL1 - a building's power draw follows what it does: an idle works draws less than a working one

From ASH4 (Jerus's Ashbourne finding 11; `docs/queue/ASH4-notes.md` section 1, "Finding 11"). The orchestrator
settled it as a design call (QUEUE's rules, BP1-C): real load follows output.

## What ASH4 found

- The city's load is the sum, over the buildings standing, of each template's fixed `powerBought()` kW
  (`ServicesManager.gridDraw`, summed at `ServicesManager.java:126` into `UtilitiesHandler.setWattsConsumption`).
  The water works (149-150) and the billed utilities (173) read the same `gridDraw`.
- So a building draws the same whether it is staffed, half-staffed or idle, and whether its works run full or
  stand short of inputs. In Ashbourne, the builders went from 123 to 479 at month 194 with no change in the load.
  Finding 11's own cause was real and different: two Lime Kilns (690 kW each) closed (AshbourneCheck 3).
- In the real world, a plant's electricity is mostly variable with output. Fixed loads (lighting, heating, controls)
  are a small part of an industrial building's use. Offices and homes are closer to fixed.

## What to build

- A building's draw is a fixed share, standing, plus the rest in proportion to its activity: output against
  capacity for a works, staffing against posts where it has no output. Dials: the standing share per category,
  each with its sentence and its source. Homes and services keep their draw as it is, unless the brief's
  builder finds a reason in the source.
- Every reader of `gridDraw` reads the same draw: the grid's load, the water works, the utilities' bill, and
  BuildAdvice's and the auto-builder's forecast of the load. A held figure (AP5-AP8) that reads the load raises
  when the activity moves.
- The check: one works, staffed and then idle (no workers, or no inputs). The idle draw falls to the standing
  share to 1e-9, the city's load falls by the difference, and the bill falls with it. A full works draws what it
  draws today, so a city at capacity moves little.

## What it moves

- The default trace's load and price whenever works run below capacity: an ensemble against the newest baseline.
- The power harnesses that fix a draw per building (PowerPlanCheck, EssentialWorksCheck, SubstationCheck,
  PowerDispatchCheck) assert against the model's draw, not a number.
