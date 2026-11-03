# BA1 - Every business may build ahead ({next}, 2026-11-02)

**WHERE IT STANDS (WIP, paused on the orchestrator's word, 2026-11-02 ~21:20):**
1. Branch `batch/BA1` is built on `origin/batch/FC1` (8c772b41), not yet on FC1b; merge `origin/batch/FC1b` when its ship file lands (step 0's wait), then `origin/main` once FC1 and FC1b are on it.
2. The WIP commit holds two Sonnet slices stopped part-way and unreviewed: B (core: BusinessInvestment, BuildersShort, BuildingManager, Game, Mortgage, Founding, Sector, BuildCard, AutoBuilder, BuildAdvice, RealEstate, Construction) and A (the other sector planners). It does not compile yet.
3. Next: finish both slices against the design below (B stopped at AutoBuilder/BuildAdvice; A at Oil's sea planning, then Freight, the shops, Restaurants, LuxuryRetail, Retail, Refining to check), compile, review the diff as a stranger's.
4. Then: mend the harnesses that read the removed APIs (InvestCheck, AutoBuildCheck, BuildAdviceCheck, BuildCardCheck, BuildersFirstCheck, OrderSearchCheck, ReadPathCheck, RefineryCheck, ScaleCheck, MortgageCheck, NewGameCheck and the rest the compile names), the new BuildAheadCheck, the screens (SectorScreen's planner lines, ConstructionScreen's QUEUE warning at BuildersShort.KNEE_MONTHS), the full suite, the ensemble.
5. The "before" for the ensemble: FC1's run `FC1-lc3` (kp1-c03b86's target/ensemble): 16 busts (people 15%+ under a peak) in 13 of 16 seeds; people at m600 median 14,681 (scratchpad busts.py).

## The design (decided; ★ = a call for Jerus, recommended way taken)

- The caps go: `MAX_CONCURRENT_ORDERS`, `MAX_ORDER_MONTHS`, `withinMonthsOfWork()`; `orderSize()` is the gap and the plots; planMaker no longer refuses a plant past twelve months. AutoBuilder keeps its own `ORDER_MONTHS` 12 for the city's automatic orders (no profit test rations them).
- ★ An order's own buildings are priced along FC1's curve: each building on the queue the ones before it in the order leave (`BuildersShort.meanPremium`, exact sum to 64, the integral past it); one building exactly FC1's price. Without it one order of six hundred homes takes years of the queue at the price before it.
- The interest on the wait: every money test grows the cost by `(1 + real rate / 12)^wait`, the wait being the order's whole wait (`leadTime()`, conservative, and rising with the order so the landlord's order stays a run from one); the landlords' lender reads the loan grown the same way. The wait is read at the sites' month plus the foreign contractor's while it takes orders (`Game.orderWaitOutput()`), so the builders' depot still gets out of the trap.
- The horizon: `horizonFor(lead)` = the lead, at least `PLANNING_HORIZON` (6), at most `HORIZON_MOST` = `TREND_WINDOW` (12); BuildAdvice's opening reads it.
- Each planner counts its own buildings on site as supply (the survey: Oil's jacket, Refining's conversion units, the processing works, Freight's lines, the shops, Restaurants, LuxuryRetail, Retail's stations, Automotive's steel); Oil keeps one pipe on site (its own physical rule); the refiners' crude units keep their count.
- `Sector.staffing()` counts a sector's own posts on site as spoken for.
- The builders count depots on site at nameplate as output to come; one depot a month.
