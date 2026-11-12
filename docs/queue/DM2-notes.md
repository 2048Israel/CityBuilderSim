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
