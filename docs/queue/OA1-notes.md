# OA1 — the open ask holds an essential works for years

Built by OA1 (Claude, Opus), 2026-11-25, on `batch/OA1` cut from main d169af247 (0.9.205, format 66). The brief is
QUEUE.md's row; its evidence is `docs/queue/PW3-notes.md` section 6, "The water hold" (REV-PW3d).

## 1. What was found

A helper traced PW3's water-held seeds with a copy of `tools/ensemble/Ensemble.java` printing every open ask each month
(`target/oa1-trace/` on the laptop, not committed: `v4043_zero_hot{6,9,15}.trace`, `zh{6,9,15}.trace` for 0.9.205,
`v199_zero_hot{6,9,15}.trace` for 0.9.199).

- **It reproduces on PW3's fourth-run commit 4043d6f3c, not on 0.9.205 or 0.9.199 main.** The ask section of
  `AutoBuilder` is the same in all three; the seeds' paths differ, and on 0.9.205 seeds 6, 9 and 15 open no water ask
  (0 ask lines, m200 to m402). The defect is main's and latent: any city that reaches the state below holds.
- **On 4043d6f3c:** ZERO_HOT seed 6 holds water under half for 136 months (m267 to m402, 0.261 at the end), seed 9 for
  114 (m289 to m402), seed 15 for 111 (m292 to m402), each with a Water Treatment Plant's ask open all through.
- **Seed 6, the ask:** opened at m267 for 46.0M; the bond sold short at its 17.64% cap, raising 19.0M. Its sales passed
  the cost at m271 (51.2M against 49.8M) and reached 77.0M in ten sales. Raised was at least the cost in 132 of 135
  months; held() was at least the cost in **none**.
- **Cause 1, the reserve came out of the proceeds.** held() was the cash over the reserve (a month's tax, 6.3M to 9.8M)
  less other asks' money, so the proceeds counted only above the reserve. At m271 the cash was 53.4M against the
  order's 49.8M and the reserve 6.9M: short 3.3M. At m288, 61.7M against 55.1M and 7.6M: short 0.9M. The over-raise
  (1M to 4M) never exceeded the reserve, so a flat treasury was never covered.
- **Cause 2, a state no test leaves.** stillToRaise() was the cost less the greater of held() and everything raised,
  so once raised passed the cost it was 0: nothing was offered (sales only when a repricing lifted the cost above
  raised, at m305, m307 and m350), so reoffer()'s debt-limit test never ran (ratio 0.93 to 1.52 against 2.40); the advice
  still ranked the plant, so it was never withdrawn; and covered() waited for cash the city's deficit never brought (the
  budget's surplus summed -74.4M over m269-402, negative in 86 of 134 months; a bank resolution drew 80.5M; cash under
  0 in 17 months). The last month's line: "raised $77.0M of the $63.2M the order costs, but the treasury holds only
  $0.00 for it now".
- **It froze the builder.** The water measure lacking service, the pass kept "cash and borrowing room ... for Water"
  (other measures held for it in 29, 17 and 183 measure-months in seeds 6, 9 and 15), and its earmark took all the
  spendable cash: works orders stood at 70 from m269 to m402.
- Seeds 9 and 15, the same: raised at least the cost in 110 of 113 and 108 of 110 months, held() never; shortfall at
  least 5.3M and 5.6M against reserves of the same size.
- In passing, not this batch's and not traced: on 0.9.205 seed 6 has water at 0.39 to 0.44 for 24 months (m379-402) with
  no ask, its hold line "no building the city could staff would move it now" (crews, `Outcome.NOTHING`); seeds 9
  (m305-314) and 15 (m302-314) the same line, and on 0.9.199 seeds 6, 9 and 15 for 6, 42 and 52 months. Queued as
  **SW1** in the ship file (the orchestrator, 2026-11-25: "not yours to mend, but it must not be lost"): no real city
  leaves its water at 40% for two years because it cannot staff the works, so the row asks why the staffing test
  refuses every water card.

## 2. Reality first

No real city holds the money it borrowed for a works it lacks while the works goes unbuilt for nine years:
- **The proceeds are the works's, outside the reserve.** Under GASB Statement 54, unspent bond proceeds are restricted
  fund balance, constrained by the bond's terms; the operating reserve is measured on the unrestricted balance beside
  them. GFOA's practice puts that reserve at no less than two months of the general fund's operating revenue or
  expenditure, unrestricted ([GFOA, Fund Balance Guidelines for the General Fund](https://www.gfoa.org/materials/fund-balance-guidelines-for-the-general-fund);
  [Minnesota OSA, fund balances after GASB 54](https://www.auditor.state.mn.us/media/jo4jyd0n/fundbalances_postgasb54_1012_statement.pdf)).
  So a reserve under its floor is not taken out of a works's proceeds.
- **They are bound to it within months.** This is the arbitrage rules' expectation test, not a duty to spend: a tax-exempt
  issue for capital projects keeps its three-year temporary period only if the issuer reasonably expects, at issue, to
  incur within six months a substantial binding obligation to spend at least 5% of the net proceeds, to spend at least
  85% within three years, and to proceed with due diligence
  ([26 CFR 1.148-2(e)(2)](https://www.ecfr.gov/current/title-26/chapter-I/subchapter-A/part-1/subject-group-ECFR3c5c9fb15635ebb/section-1.148-2)).
  A city that borrows for a works expects to build it within months; the six months is the harness's and the dial's.

The rule, restated: an ask's bond proceeds pay its works, as a fenced fund pays them, the reserve reckoned on the cash
beside them. Where the city's bills drew the cash under them (the model's one treasury cannot fence them), the works is
paid from them all the same and the operating deficit is left on the overdraft, as far as the central bank advances.
The paper is never sold twice. Where the treasury cannot pay even with the advance, the ask waits a bounded time, then
ends.

## 3. What changed (★ the calls)

`AutoBuilder.java`, THE CAP AND THE OPEN ASKS:
- ★ **spendableFor(own): the proceeds first** (unchanged by REV-OA1). With asks open and the cash at or over nothing,
  each ask's proceeds are its raised money at most its order's cost (a repricing's leftover is the city's), held by the
  cash oldest ask first. An ask holds its own proceeds plus the cash over the reserve beside every ask's. With no ask, or
  an overdraft, it is as before (overReserve()). Oldest first because the proceeds were raised in that order: where the
  bills drew the cash under them, the latest raised are the ones spent. It also leaves no loop-order bias between equals.
- ★ **funded() and covered(): the fenced proceeds pay the order** (REV-OA1). An ask is covered when its raised money, or
  what the treasury holds for it where that is more (held()), reaches the order's cost, and the treasury can pay that
  today (payable(): the cost within Game.discretionaryRoom(), the cash plus the central bank's headroom). Where the bills
  drew the cash, place() pays the order past the cash (Game.allowProceeds(), below): the treasury is overdrawn by what
  the bills drew. canPay(…, own) says the same for the release of lower asks.
- ★ **stillToRaise(): the cost less the proceeds**, or less what the treasury holds where that is more (main's formula
  on the new spendableFor()). The paper is never sold twice; an overdraft is the treasury's, not the order's. A repricing
  above what was raised is offered, as on main.
- ★ **The set end: PROCEEDS_WAIT_MONTHS = 6** (a new dial). An ask raised in full that the treasury cannot pay (the bills
  drew past the central bank's advance) sells nothing more and waits. At the first pass at which it has been open six
  months it is withdrawn: "the month's bills have spent the proceeds and the central bank advances no more, so the
  treasury cannot pay the order". Six is the expectation test's term. Months open is counted from the ask's opening, so
  an ask that took long to raise its cost and then finds the treasury unable to pay is withdrawn at that pass; one
  raised in time has the rest of its six months to wait.
- place(): an ask's order borrows the cost past funded(). Where it would take the cash under, it is held first if the
  treasury cannot pay it (nothing bought, nothing sold). It sets the allowance around its ground and building, clears it
  in a finally, and its log line says the treasury is overdrawn for "the deficit a fenced fund would have left".
- askWords(): an ask raised in full that cannot be paid says "the month's bills have drawn the rest, and the central
  bank advances no more: nothing more is borrowed for it, and the order waits ... withdrawn once it has been open 6
  months". These are main's two phrases with the new reason.

`Game.java`, AN ORDER PAID FROM ITS OWN BOND'S PROCEEDS (a sub-banner by landPayable()):
- `proceedsUnder`, a transient allowance (not saved), with `allowProceeds(under)` (package-private). It is added to the
  four cash tests an order meets: buildStack() and processBuildOrder() (`totalCost <= cash + proceedsUnder`),
  paveRoads() (`q.total > cash + proceedsUnder`) and landPayable(). It is 0 at every other moment, so the player's and
  every other order need their cash as before. The payments go through treasuryPays() as ever, BUILDINGS and LAND
  being discretionary lines within discretionaryRoom(), which payable() checked first.

Weighed and rejected:
- (i) OA1's first mend, which sold again what the bills drew (stillToRaise() = the cost less spendableFor()). REV-OA1:
  only the debt limit bounded it, and a deficit wider than the market sells at the cap sold the gap, the overdraft with
  it, every month for an order never covered, while the DEBT hold kept Water the protected work: the freeze again.
- (ii) Bounded re-sales (a count) without the fenced order: they end in a withdrawal and a reopened ask selling again.
- (iii) Paying past the central bank's ceiling as arrears: BUILDINGS accrues arrears past the ceiling, but no order has
  ever been placed that way. The set end is the plainer edge.

The debt limit stays as it was. An ask whose sale the market refuses at the player's cap still waits while it has money
to raise (D30, Jerus: "stays open the ask until money raised"); its paper is bounded by its cost.

**Old saves:** no saved field changes (OpenAsk keeps its fields; proceedsUnder is transient). A saved ask reads as it was
and follows the new rule. Nothing loads wrongly, so there is no format bump.

## 4. The checks

- **AskProceedsCheck (new, 49 checks): green.** Its sections come from its header:
  - 1, the finding caused: a town short of water (supply 49,383 against 149,637); cost 3,972.4, raised 3,972.4, cash
    4,037.6, reserve 130.4. The works is ordered from the proceeds, its order sells no paper of its own, and the pass's
    money is conserved.
  - 2, the bills drew 316.4, within the central bank's 632.7 advance, Fill with room: ordered that pass, no paper for
    it, the cash taken under. The pass's other order, a second plant, sold its own paper (5,027.9, clearing the
    overdraft). Conserved.
  - 2b, the same drawn treasury with the market short (a bond for the 316.4 sells 98.7), played for months: ordered in
    month 1, its entry's borrowing the 3,972.4 raised, nothing past its cost. Every month's audit closed. (The pass
    then opened a fresh ask for the next plant, the town needing four; it is a new ask with its own cost.)
  - 2c, drawn past the advance (cash 1,669.8, cash plus advance 2,302.6 against 3,972.4), Fill with room, the cash and
    the water draw set back before each month: not ordered; nothing sold twice (one lot, 67.6 for a 22.4 repricing at
    month 4); its words; withdrawn at the pass of month 9, opened at month 3, exactly six months; every audit closed.
  - 3, the limit between the ratio (2.0175) and a sale of the rest (2.4040) of a half-raised ask: withdrawn, nothing
    sold, the raised money stays.
  - 4, played: ordered in month 1, within six months, every audit closed.
  - 5, without the lack: as before.
- **Against main's AutoBuilder** (ca030ab08's file swapped in with the dial added so the harness compiles, run, put back,
  recompiled): 15 red, in sections 1, 2, 2b, 2c's set end, 3's fixture (main's held() takes the reserve out of the
  proceeds) and 4. Section 5 is green. The harness is the finding.
- **AuctionCheck section 10, "the month's bills draw the money": main's part restored** (its four assertions and the cash
  coming back), and one label restated with its reason. On main the order waited because "the treasury does not hold
  the cost ... no order is placed on money that is not there". Under the fenced rule it waits because the bills drew
  past the central bank's advance (costs 464, cash plus advance at the pass 331). The label says so, and the assertion
  reads the treasury's room at the pass's end (a probe). The other three hold unchanged: offered what it still has to
  raise, sold in full; not sold twice next month, the orders waiting; the inbox's "have drawn the rest" and "nothing
  more is borrowed for it". OA1's first restatement of this part is gone.
- **WorksFinanceCheck, the fully raised ask** (REV-OA1 #2). Its premise stays restated to the proceeds the treasury
  holds. Its "the lower funded reservation is released" passed for a reason it did not name: the release fires for
  "Power grid", an essential the cash cannot pay, not for streets, which their own proceeds pay. The label and its
  condition now say that: the withdrawal line names a measure other than streets.
- **The named harnesses, one at a time, on 278400533's base with the mend:** `--fast` green; AskProceedsCheck,
  AuctionCheck, WorksFinanceCheck, EssentialWorksCheck, BuildCardCheck and IslandLinkCheck green; AutoBuildCheck exit
  3, main's known red only. The pick: ALL (AllChecks lists the new harness), so a dry run is owed.

## 5. REV-OA1 (the stranger's money review, 2026-11-25, on 42101f39f)

NOT SHIP, one P1, mended here:
- P1: the re-sale was bounded only by the debt limit (above, (i)). The mend is the fenced order and the set end; the
  months-long 2b and 2c fixtures are the evidence it asked for. "No further review."
- 2: WorksFinanceCheck's release label (section 4).
- 3, holds: the re-sale booked like the first sale (now gone); the money closes; proceeds outside the reserve; with no
  ask open, spending is main's.
- 4: the 26 CFR wording (section 2, the harness header, the banner): the expectation test, not a duty to spend.

## 6. The ensemble

80 of 80 on the gate box (29.3 min), the classes of dc7f9cc7d (the fenced mend on 0.9.206), scored against
`docs/ensemble/0.9.206.md` (R1g), JAVA_OPTS empty (the baseline's data paths alone). Committed as
`docs/ensemble/OA1.md`.
- **M1 and F1 pass in all five policies**: no month's money audit failed, and no run has a LongPlaytest finding. The
  batch verdict is PASS, hard criteria 10 of 10, 23 of 26 criteria. A1 and A2 (AUTO2, info) fail as the baseline's do.
- Changed verdicts: T2 (info) FAIL to PASS in all five. The baseline was scored against 0.9.202, this run against
  0.9.206 itself. ZERO_HOT's final population is 15,883 [7,343 to 22,744] against 13,344 [6,266 to 26,551].
  ZERO_HOT's H2 (info) goes PASS to FAIL: 11 of 16 seeds below against the 12 asked, median +0.67% against -0.45%.
- **The water-held seeds, before and after** (the runs' `served_water`: the longest spell under 0.5 from m200, read by
  `target/oa1-water.py`; the baseline's CSVs copied from the box's R1g run):
  - On PW3's fourth run (4043d6f3c), ZERO_HOT seeds 6, 9 and 15 were held under half for 136, 114 and 111 months to the
    run's end, a Water Treatment Plant's ask open all through (section 1).
  - On 0.9.206 (R1g), ZERO_HOT seeds 6, 9 and 15 are never under half from m200. ZERO_HOT's one spell is seed 5's, 13
    months (m362-m374). In all, 37 runs hold water under half for 12 months or more, 12 to 29 months: seeds 0, 4, 9,
    10, 11, 12, 13, 14 and 15 of AUTO2, ZERO_SLACK, SHOCK and HIGH, which share their early years, and ZERO_HOT 5.
    Every one is served in full by the end except HIGH 13 (0.952).
  - With OA1, ZERO_HOT seeds 6, 9 and 15 are never under half from m200; seed 5's spell is 16 months (m358-m373). In
    all, 28 runs, 12 to 26 months: seeds 0, 1, 8, 10, 11, 12 and 14 of the same four policies, and ZERO_HOT 5. Every
    one is served in full by the end except SHOCK 0 (0.956).
  - So no seed holds water for years before or after on 0.9.206. The growth-phase spells of one to two years are the
    baseline's pattern, on other seeds as the paths part. The longest is 26 months against 29, and 12 runs are over
    24 months against 8.
