# FC2b — the choices that use the foreign crews — notes

Builder: Claude (Opus 5.5), slices on Sonnet (contract 4). Branch batch/FC2b from origin/main b92af507f (0.9.197, SAVE_FORMAT 64).
Brief: docs/queue/FC2.md, "## FC2b" (Jerus, 2026-11-22: "allowing businesses to even decide to pay more to finish sooner (for
example, something finished in 20 months is better than something finished in 100 months, cause thats 80 more months of paying
interest...)"; "also let the city itself do it, like per building, aka for example if roads are causing the issues then you could pay
more to get just roads fixed").

## 1. What was built

- **A site's work abroad, dedicated.** A payer hands its share of one site's (one stack's) remaining work to the foreign crews. They
  build it on that site at a pace and price struck at the handover (BuildingManager.abroadTermsOf(), handSiteAbroad()), apart from the
  city's crews' rule and from FC2's pooled book. The record is ConstructionControl's G. ABROAD (Abroad: the part's points owed, its
  materials, pace, price a point, what was paid, the months home and abroad, the interest saved, the earnings brought forward), saved
  whole with the control's state.
- **The stack stays one** (BuildingsStacks): its buildings finish front first as ever, each its template's sector's whoever paid; its work
  splits into the home part (the crews' rule) and the abroad part (the crews' pace). The payer's contract moves to `abroadContracts` and
  is earned by the abroad part's fraction done; the other payers' by the home part's; the material likewise.
- **The month** (BuildingManager.advanceConstruction): the shares and the plan read each site's home work (homeOwedOf(), homeCapOf():
  PW2's build-time cap is the site's, both crews together); each handed site gets min(owed, pace) of the crews' own; the record ends
  when its part is built or the site empties. pointsBuilt stays the city's crews' and FC2's pooled work; getAbroadPointsBuilt() and
  getAbroadBill() are the crews on sites, billed by Construction.payCrewsOnSites() (an import of services, FOREIGN_CREWS, as FC1's),
  their points off the builders' backlog. queueAtHome() is less the work handed abroad; the crews' supply curve (excessAt) counts
  their month on the sites handed them (ConstructionControl.abroadMonth()).
- **The choices** (Game, THE CHOICES THAT USE THE FOREIGN CREWS): investorsHandAbroad() once a month before the crews' offer; the
  city's abroadPreview(key) card and handSiteAbroad(key); settleCrewsOnSites() after FC2's settle.
- **The arithmetic** (BuildersShort, THE CHOICE TO FINISH SOONER): sitePace(), monthsAbroad(), broughtForward(), interestSaved().

## 2. The calls (★ design)

- ★ **Dedicated, not pooled.** FC2's book pools his points into the sites' output and shares them by the crews' rule, so a payer could
  not buy its own site's speed through it. A handover is its own crews on its own site, at pace max(W/T, the largest depot) (FC2's
  own rule for his book: the work abroad waits no longer than T, the threshold's months; FC1's depot floor), never over W and never
  over PW2's build-time cap. So the part finishes within about T months: Jerus's "20 months vs 100".
- ★ **A shared stack.** The mover takes its own contract's share of the site's home work, by points: a contract now carries the
  points its orders put on site (Contract.points, noted at each of the four bookings), earned pro rata as its value is, the stack's
  own rule. Buildings are not named to a payer (they finish front first, each its template's sector's), so the order leaves as points
  of work; the other payers' contracts are earned only by the home part, so their progress is untouched. A site with work abroad is
  not isCitysOwn() (no reorder, rush, stop or cancel of it by the city). One handover a site while its record stands.
- ★ **The saving.** S = broughtForward(P, i, Wa, Wh) = P((1+i)^-Wa - (1+i)^-Wh)/i: the earnings P a month from the opening, brought
  forward from Wh to Wa, at the payer's monthly real rate (BA1's waitCarry rate, realTestRate/12). It is exactly the brief's two
  parts: the interest on the cost C for the months saved, interestSaved(C) = broughtForward(C·i), plus the earnings over that interest
  brought forward, broughtForward(P - C·i). Adding interest on the sunk cost to the whole earnings would count the loan's interest
  twice (it runs in both cases until the opening pays it). Money already spent is sunk: only the remaining work moves, and the extra
  is struck on it alone.
- ★ **The business** moves when the extra it pays, net of the builders' tax it gets back (buildFor's rule), is under S, with
  P = estimatedMonthlyProfit(sector, t) x its buildings on site, C = getCostOf(t, 1) x them, the rate judged(sector, shortfall).tested();
  it pays as fit() pays (borrows a shortfall if it can, else stays). The builders' own depots are not weighed (they go first already).
- ★ **The city** earns no money from a site, so its saving is the interest on its cost alone, at its long real rate
  (BuildAdvice.lifeRate()); the player decides from the card. The treasury pays (TreasuryLine.BUILDINGS, cityCapitalSpending) and
  refuses when short, as fit() does for the government. A rushed site's rush is stopped first (the overtime is the home crews').
- ★ **The extra** is the crews' price over the home price as it stands: points x base x max(0, excess - scarcityPremium()), grossed
  for the builders' tax. The builders keep the home price paid for that work and pass the crews' bill; the difference between the
  premium paid at the order and today's is theirs, as FC2's rule keeps quoted work at the price quoted. Where today's home premium is
  over the crews' excess, a move costs nothing extra and the crews' supply curve and the shorter queue at home price the next one.
- ★ **The waits** other than the shares (siteMonths and the order quotes) still count a handed site's abroad part at home: an
  over-estimate, conservative for the next payer's test.
- ★ getRemainingConstructionPoints() is unchanged: the dedicated work stays in the builders' backlog until built (they are its general
  contractor, as for FC2's book).

## 3. The checks

- Slice A (state): --fast green; ConstructionControlCheck, SaveFileCheck green (nothing handed yet).
- Slice B1 (month): --fast green; ForeignCrewsCheck, ConstructionControlCheck, ConstructionTrapCheck green, bit-identical with no record.
  The review caught one wrong read (the terms subtracted the control's record where FC2's pooled claim was meant), mended.
- Slice B2 (choices): --fast green; ForeignCrewsCheck, ConstructionControlCheck green. SaveFileCheck: one red, "REV-HC1 P5: the
  populated format-62 city continues its card stocks, services, loan book and capital", with 26 handovers in the harness, 17 in P5's
  city. The one leaf apart at the opening comparison is /sectors[1]/extras/familyRentWeight, live 14304.000000000002 against loaded
  14304.000000000004 (1 ulp). **Main's own load path:** the save keeps the total rent weight T and the studio weight S
  (Game.java ~18208-18209, DataSave.rentWeight, rentWeightStudio) but not the family weight F; the load rebuilds F as T - S (Game.java
  ~21221 and ~22813-22814), which is F only when the sums carry no dust. On FC2b's path P5's city has T = 18464.000000000004, S = 4160,
  F = 14304.000000000002, and T - S is the loaded figure. On main the same fixture sums exactly (T 15524, S 2240, F 13284), and main's
  own data/slot-02.json at m62 shows the same mismatch (F 3834.0000000000005, T - S 3834.0) with no FC2b code in it. None of FC2b's
  code touches the rent weights, FamilyModel, RealEstate or their save fields. So the known-reds row passes it to FRW1 (queued in the
  ship file): save F, or keep the value RealEstate.restoreExtras() already restores exactly. The review moved the card's "already
  with the foreign crews" before "not the city's own site" (a handed site is not isCitysOwn()).
- Slice C (ForeignChoiceCheck, new, 7 sections from the brief's bounds): 212 assertions green in 16 s, the money audit closing in
  every month played (148, worst residual 1e-9). Section 2's twins differ in the exchange rate alone, the queue at home lengthened
  by standing roads until the strong D$'s extra is under the saving and the city's own rate's over it; section 3 a half-built site;
  section 4 the city's Houses on the landlords' site; section 5 two Middle Schools twins, one handed by the card; section 6 the money
  through the audit, the national and the foreign accounts; section 7 a save and load with a record half done, four months on. The
  slice found the terms counted the part's buildings as the payer's share of all on site while its points leave FC2's pooled claim
  out, overstating the saving on a claimed site: mended, the buildings counted by the part's points (BuildingManager.abroadTermsOf()).
- Slice D (the Construction screen): --fast green. The hand row is a flow (Kept.flow) so Rush, Foreign crews and Cancel fit the
  168 px column; the card's captions and the crews' line in their own Kept scopes; ConstructionControl.Abroad.monthsLeft() so the
  screen computes nothing. No harness reads this screen's scopes; the window was not opened.

## 4. The saves

- ★ **Format 65, the floor with it** (the old-saves rule). The control's G. ABROAD list and the builders' crews-on-sites figures are
  absent from an older save and read as none, which is right. But a contract's points are not: a format-64 save's contracts hold
  none, and once an order adds points to one payer's contract on a shared site, the share by points (homePointsShare()) reads the
  older payers' work there as none. That is a wrong load, not an incomplete one, so the floor rises with the format; no conversion.
  RES3 is to ship format 65 too: whichever ships second bumps from the other's (FC2b re-bumps at its merge if RES3 is first).
- The record is saved whole with ConstructionControl's state (Gson, State.copy(), restore()'s null guard); the contracts' points and
  the abroad ones in BuildingManager.ContractRecord (points, abroad); the crews' month in Construction's extras. Nothing is derived on
  load. ForeignChoiceCheck 7 holds the round trip; SaveFileCheck as section 3.

## 5. The checks on the final code (91167f730 and the records after it)

- The pick is ALL (AllChecks gains ForeignChoiceCheck). A full suite started here was stopped by the orchestrator (contract 4: the
  whole suite is the dry run's, on the box); its log is not a result. Run here, one at a time: ./batch-check.sh --fast green; green:
  ForeignChoiceCheck, ForeignCrewsCheck, ConstructionControlCheck, ConstructionTrapCheck, SaveFileCheck, CardCheck (its two), ForeignCheck,
  FitoutCheck, GapOrderCheck, MortgageCheck, NewUnitCheck, RunWorksCheck, WellCheck; InvestCheck known red only (main's,
  docs/notes/gate.txt). The named set is every harness that reads BuildersShort, a stack's contracts, the contract records, the
  crews' bill or terms, the queue at home or the home premium.
- ★ **SaveFileCheck green on the final path**: the buildings mend (slice C) moved which sites go abroad and P5's rent weights now sum
  exactly, so its red of section 3 no longer fires and no known-reds row is filed for it. The fault is main's and still latent (a
  moved city can bring it back), so FRW1 stays queued in the ship file; if the dry run shows it, the row naming FRW1 goes back.
- The review owed: money and saves (the brief; tools/pick.sh reads no money or saves in the diff, but the handover's payment, the
  crews' import and format 65 are both). The ensemble is owed after the review (the trace moves: businesses hand sites abroad).

## 6. The review (REV-FC2b-1) and what it folded

- SHIP, no P1 (the orchestrator, 2026-11-24: money booked once; the interest call right, only the earnings brought forward are
  incremental; the split exact; the bump owed; the refusal real). Three notes folded, no further review:
  - (2) A handover record kept +Infinity for a wait at home that never ends, and DataSave's Gson refuses an infinity. Every figure
    the record keeps is now finite (BuildingManager.handSiteAbroad(), finiteOr0()), the endless wait as Double.MAX_VALUE, siteMonths()'
    own never. ForeignChoiceCheck 8 saves and loads such a record (its save line throws without the guard). No input of a played city
    reaches it: Game.orderWaitOutput() adds the crews' month, never nothing while trade is on, so the section reaches the endless wait
    through abroadTermsOf()'s wait output of nothing and says so (★: kept as a guard, not a path).
  - (3) ForeignChoiceCheck 9: a Paved Road site paving two Gravel Roads handed by the card; its record is the site's whole owed work, the
    gravel roads retire as the paved ones open, all by month 20 against month 28 in the twin kept at home.
  - (5) ForeignChoiceCheck 10: a landlord that must borrow its extra moves and its debt rises by the shortfall, and its twin barred from
    borrowing (a write-down's ban, restoreCreditRecord) stays with nothing moved; the treasury holding the card's extra hands the site,
    and holding half of it refuses ("the treasury holds ... and the crews' extra costs ...") with nothing moved. Found on the way: the
    government never borrows (Game's government canBorrow() is false), so the card's shortfall always refuses, as fit()'s does.
- ForeignChoiceCheck: 281 assertions green, 1m48s with its compile. Main had not moved: nothing to merge.

## 7. The ensemble

- docs/ensemble/FC2b.md: 80 of 80 runs on the gate box, classes of 5b0ec70b8 (the final model; the commits after it are records),
  no JVM options beyond the box's data files (the baseline's), scored against docs/ensemble/0.9.195.md as the orchestrator set
  (LP-G1 on main, the newest baseline of a main: 0.9.197.md is FC2's branch at 348fdae18, before PW2, LP-G1 and R6f were merged into
  it, so not FC2b's parent). Exit 0: **M1 and F1 pass in every policy** (no month the money audit failed, no LongPlaytest finding),
  and no verdict moved. The trace moves, faster: AUTO2 reaches 40,000 people at m322 [275 to 397] against m400 [345 to 465], its final
  population 9.08 M against 9.03 M; ZERO_SLACK 7.63 M against 7.14 M, SHOCK 1.50 M against 1.15 M, HIGH 712 k against 355 k. The move
  against 0.9.195 carries FC2's and R6f's too; scored against 0.9.197 (not like for like) the change is larger (AUTO2 at m678 there).
- **The handovers** (the count the orchestrator asked for): the driver silences the model's lines, so AUTO2's 16 seeds were run again
  on the box (label FC2b-count, not committed; each run's population equal to its scored twin's every month's end, so the read-only
  columns moved nothing) with three getter columns: the records standing, and the crews-on-sites points and bill each month. A
  handover shows as a rise in the records standing, so the rises are a floor on the count: **at least 124 to 283 handovers a run of
  2,400 months, median about 175, 3,006 over the 16 seeds**; at most 8 to 14 sites with the crews at once; the first at month 11 or 12
  in every seed (the founding's queue, the city's builders few), and some site with the crews in 269 to 712 of the 2,400 months.
  All are businesses' (the city's are the player's).

## 8. The dry run

- `sh tools/gate.sh FC2b --dry-run` on the orchestrator's go, on the gate box: run /home/jerus/cbs/runs/cbs-gate-FC2b-Yd0ZcIDj
  (the branch 02d4392de, main 2186dbbf5, 0.9.197 and QUEUE commits), exit 1 with 9 unnamed red lines in five harnesses. Each was
  traced by a helper in scratch (the worktree untouched): every harness here mutes System.out while it plays, so its log cannot show
  a handover; the helper replayed each city with a counting stream, a twin with investorsHandAbroad() overridden to nothing, and a
  build of main b92af507f. The twin and main give main's figures exactly in each, so **none is an FC2b code path giving a wrong
  figure; all are cities the handovers moved**, entered as `## known-reds` rows in the ship file:
  - **SectorFlowCheck** (two lines): main's SectorFlow.of() has no row for Statement.powerSold. The fixture's Pulp Mill, handed to
    the crews about m38, opens at m58 on FC2b's path, and Wood Products' outputs fall short of its revenue at m61 by $293,460, that
    month's powerSold; on main the mill is still on site at m61. Queued as **SFP1** in the ship file (the row, null good, "Power sold
    to the grid").
  - **AutoBuildCheck** (the priced-out fixture, ~805-815 on the decades city ~374): 12 handovers; general care priced out 0.0 of
    7,209.5 (main 183.3 of 6,931.8). FS12, the harness's queued sweep, takes it; its by-section row names only the "placed orders"
    premise, so the line is added here for it.
  - **ForeignDebtCheck** (theLadder ~812, the 60-month trading city ~130): 4 handovers, 4,032 people against 2,980; the quote sells
    nothing ("the bids reached only $21,316 of the $103,533 asked"), main's covers 1.42x with 36 payments. FS13 shipped; HT1-H.
  - **CarCheck** (three lines, OE1 ~1250-1305 and CI1 ~1383, the buying city ~340): 7 handovers open Automotive sooner, 2 cars on
    its shelves at m24 and 12 at m25 (main 0). OE1's edge needs empty shelves (no edge in 4,800 trials); CI1's month needs every car
    off the ship (6.0 on the shelves). Its "imports declared ... paid" pair is not a money mismatch: paid less declared is 0.7920,
    exactly the six shelf cars at 0.132, the households' domestic purchases; FC2b touches no household car code. FS7 shipped; HT1-H.
  - **GroundSkipCheck** (section 3's premise and its next line, ~268-269 and ~285): the one handover in probeCity's first 19 months
    (Industry's Bakery, 2 months against 20) grows probePlay()'s town to 2,314 people and 0.9053 built on at m245 (main 1,285 and
    0.9274); the landlords are held 0 of the 60 skip months (main 2). LP-G2, queued for this town's holds and owner of the
    section's other two lines, causes the premise.
- The ship file's first line names the run, so the gate reuses its suite; `sh tools/known-reds.sh FC2b docs/queue/FC2b-ship.md`
  reads the nine rows. Only the notes and the ship file changed after the run.
- The dry run again on fafc01299 (the rows entered, main 77c77ab8c, QUEUE and contract commits only): run
  /home/jerus/cbs/runs/cbs-gate-FC2b-Z4xWHH5X, the first run's suite reused, **exit 0, known red only, unnamed.txt empty**. The ship
  file's first line names it.
