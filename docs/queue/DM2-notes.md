# DM2 notes

## Work and decisions

- Built on origin/main at fc3b6be5, on batch/DM2.
- The field-belt regression uses seed 1 and monthly arrivals of 15 houses and 3 shops. A probe compiled against the original DistrictPlan found 130 standing boxes moved at month 25, with 9 wall-way plots before and after. The real Earth and world assets were supplied.
- ★ Store wall-way transitions separately from the runs journal because their plots span the plan frame, including its east and south seams. FORMAT 16 appends month deltas and plot/state records after the existing journals. Older sidecars begin with an empty wall journal.
- ★ A plan returns its journal as immutable snapshot arrays. CityMap keeps appended discoveries on its own thread. Worker results can extend only their input journal, preserving records from newer work.
- ★ Apply wall events beside dated runs during arrival replay. An opening uses the existing moveOff operation, preserving the instances and all boxes outside its plots. A released way keeps its historical moves and frees room for later arrivals.
- No model month statement was added. Wall dating is map-owned, beside the existing run dates.

## Checks so far

- Initial PlanCheck on the original planner with the first simple-house fixture exited 3, its two recorded PX1 assertions. That fixture caused a wall but no drift, so it was replaced by the mixed-house/shop field belt that the original-planner probe demonstrated.
- First --fast compiled and regenerated the indexes with zero firm Stale findings. The temporary runner was rewritten while Bash was still reading it, so its final exit line was lost. Subsequent runs use an unchanged LF runner.

- ★ Reused DM1's arrival-ranked wall walk for a new route. The saved journal keeps routes already chosen through subsequent land and street-piece changes, which replay ranks alone could not do.

- ★ FC1's enabled played-city path left the rail hub beside water and laid no yard. Merely handing it more ground still laid none. The 2d and idle-yard fixtures now find a wholly buildable square sized from YARD_LONG on the existing generated world before filling it, so their depot can cause the condition those checks assert.

- The first named run finished with SlopeCheck green. It exposed a full-ground pass that still used FW11's old sparse clear list, while the new journal used its kept-state array. Applying the kept-state flag in the shared field/occupancy pass fixed the disconnected field-belt plans. Two adjacent documentation blocks were also moved to their methods after Stale caught them.
- The focused probe now passes the field belt, dated arrivals, 2d yard arrival/removal/return, the idle-yard lifecycle and FORMAT 16/15 journal checks with zero failures. Its yard displaced exactly 37 boxes on arrival and 66 on return, with zero later moves and zero moves when removed.
- A subsequent --fast exited 0, with zero firm Stale findings.

- The incremental ground pass compared only run kinds and skipped a wall event with the same run kind before and after. Wall events now use the full ground pass, preserving its before/after occupancy arrays for moveOff. The explicit 18c fixture verifies exactly one displacement, no movement on release and a later arrival using the freed plot. All three assertions pass.
- The repaired played city's founding district has one street piece and replays its seven recorded way plots identically without further events. The focused probe's only remaining red is MapCheck 1's recorded DM3 missing-drawn-buildings line.
- Initial default traces on main and the branch matched across all eight CSVs. After the incremental-ground correction the final model's default and 4,000-month playtests are being run again against that stored main trace.
- The first --pick compile caught a harness variable named `in` where the input was `pj.in`. It was corrected before the replacement --pick run.

- The final 4,000-month run still found the late drift. A captured planner-input pair at m3003/3004 differed only in the General Cargo Terminal count, 2 to 3. The own log, carry, ground and wall journal were unchanged. Returning a unit that the shore had excluded replayed its old arrival, shifting 660 later boxes. This is the queued MF2-S case, whose row permits repair in DM2.
- ★ Keep a dated adjustment journal for site/shore units alongside the wall journal in FORMAT 16. Coalesce its changes with the model log by month and type. A returning unit becomes an arrival in its return month, and a unit allocated away becomes a departure then. The model's district log continues to sum to all its counts. This records the flow the count alone loses.
- The original planner compiled from origin/main also fails MapCheck 1's drawn-count assertion with FC1 enabled, drawing 1,355 of 1,356 buildings at month 121. The corrected planner has the same total shortfall there. Its existing DM3 record covers that independently reproduced finding.

- The unit journal removed the 660-box cascade. Its 4,000-month replay left two moves at m3002. Captured actual worker inputs showed a Rail Depot's count return from 7 to 8 and an older log entry change from 0 to 1, beside a genuinely expanding yard. The same return-flow defect affected MF2's static yard-unit removal. The dated adjustments now cover yards too, replacing that static removal, with both cargo-terminal and rail-depot return/departure fixtures.

- The cargo-terminal and rail-depot unit-flow fixtures pass all six assertions apiece, including the sidecar byte round trip. The first 89-harness pick completed with only the already-repaired incremental-ground network failures as NEW reds. Its other checks, including ReadPathCheck and SaveFileCheck, completed. A final pick is now being run on the dated-unit repair.
