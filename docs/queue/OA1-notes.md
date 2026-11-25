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
  no ask, its hold line "no building the city could staff would move it now" (crews, `Outcome.NOTHING`).

## 2. Reality first

No real city holds the money it borrowed for a works it lacks while the works goes unbuilt for nine years:
- **The proceeds are the works's, outside the reserve.** Under GASB Statement 54, unspent bond proceeds are restricted
  fund balance, constrained by the bond's terms; the operating reserve is measured on the unrestricted balance beside
  them. GFOA's practice puts that reserve at no less than two months of the general fund's operating revenue or
  expenditure, unrestricted ([GFOA, Fund Balance Guidelines for the General Fund](https://www.gfoa.org/materials/fund-balance-guidelines-for-the-general-fund);
  [Minnesota OSA, fund balances after GASB 54](https://www.auditor.state.mn.us/media/jo4jyd0n/fundbalances_postgasb54_1012_statement.pdf)).
  So a reserve under its floor is not taken out of a works's proceeds.
- **They are spent on it within months.** A tax-exempt issue for capital projects keeps its three-year temporary period
  only if the issuer expects to incur, within six months, a substantial binding obligation to spend at least 5% of the
  net proceeds, to spend at least 85% within three years, and to proceed with due diligence
  ([26 CFR 1.148-2(e)(2)](https://www.ecfr.gov/current/title-26/chapter-I/subchapter-A/part-1/subject-group-ECFR3c5c9fb15635ebb/section-1.148-2)).

The rule, restated: an ask's bond proceeds pay its works first, the reserve reckoned on the cash beside them; where the
city's bills drew them (the model's one treasury cannot fence them), what they drew is borrowed again under the debt
limit, and the limit, not a wait, ends the ask.

## 3. What changed (★ the calls)

`AutoBuilder.java`, THE CAP AND THE OPEN ASKS:
- ★ **spendableFor(own): the proceeds first.** With asks open and the cash at or over nothing, each ask's proceeds are
  its raised money at most its order's cost (a repricing's leftover is the city's), held by the cash oldest ask first;
  an ask holds its own proceeds plus the cash over the reserve beside every ask's. No ask, or an overdraft: as before
  (overReserve()). Oldest first because the proceeds were raised in that order and, where the bills drew the cash under
  them, the latest raised are the ones spent; it also leaves no loop-order bias between equals.
- ★ **stillToRaise(): the order's gap**, the cost less spendableFor(own) (an overdraft in full), as place() and canPay()
  reckon it. Where the bills drew the proceeds, what they drew is offered again, each sale under the limit's test, which
  withdraws the ask when it closes. Weighed against: (i) mending cause 1 alone and keeping "the paper is never sold
  twice": rejected, because the dead state stays reachable whenever a deficit draws the proceeds between short sales,
  and AuctionCheck's own fixture builds exactly that state; (ii) releasing an ask whose sales passed its cost but whose
  treasury no longer holds it: rejected, because the measure then reopens an ask and sells the same new paper through a
  withdraw-and-reopen churn, resetting its months open, while the protected-work hold freezes the other orders
  meanwhile. The net position is reality's: the new paper replaces the proceeds the deficit spent, where a fenced fund
  would have left the deficit on the overdraft and the proceeds whole.
- The debt limit stays as it was. An ask whose sale the market refuses at the player's cap still waits (D30, Jerus:
  "stays open the ask until money raised"); not this batch's.
- askWords(): the line for an ask whose proceeds the bills drew says that what they drew is offered again every month
  until the order is paid or the limit closes on it (was: "nothing more is borrowed for it").
- **Old saves:** no saved field changes (OpenAsk keeps its fields); a saved ask reads as it was and follows the new rule.
  Nothing loads wrongly: no format bump.

## 4. The checks

- **AskProceedsCheck (new, 42 checks): green.** Sections from its header: 1, the finding caused (a town short of water,
  supply 49,383 against 149,637; cost 3,972.4, raised 3,972.4, cash 4,037.6, reserve 130.4): ordered from the proceeds,
  its order selling no paper of its own (its entry's borrowing the 3,972.4 raised), the pass's money conserved; 2, the bills drew half: one sale of the gap (raised 1,991.8 for a
  1,986.2 gap, within a lot), the order placed that pass, the cash conserved; 2b, the market short: offered again,
  short, the order waits, and the words; 3, the limit between the ratio (2.005) and a sale's (2.469): withdrawn, nothing
  sold, the raised money stays; 4, played: ordered in month 1, within the six-month test, every month's money audit
  closed; 5, without the lack: no ask, spendable() is the cash over the reserve (an overdraft in full); two asks with
  proceeds and reserve whole hold what they held before; water served in full opens no ask.
- **Against main's AutoBuilder** (origin/main's file swapped in, run, put back; on the merged 0.9.206 code): 18 red, all in
  sections 1 to 4 (never ordered, never offered again, never withdrawn, nothing in six months); section 5 green. The
  harness is the finding.
- **After merging R1g (0.9.206, the 1900 city):** section 1's pass now goes on, after the ask's plant, to order a second
  Water Treatment Plant on its own bond (4,649.1, borrowing 4,692.6): the town's ratio, 2.018 against the fixture's limit
  of 3.0, leaves that bond within it, where before it did not. Its two assertions read the cash and bonds over the whole
  pass ("no bond was sold", "the cash fell by the order's cost"); restated to the claim itself: the ask's order sold no
  paper of its own (its entry's borrowing is what the ask raised), and over the pass the cash fell by the orders' cost
  less the new paper's proceeds (8,621.5 less 4,692.6 = 3,929.0), the principal by its face alone. Green again.
- **AuctionCheck section 10, "the month's bills draw the money": restated (check reality).** Its four assertions stated
  the old law (the paper not sold twice, the orders waiting on the cash) and went red; the wait they asserted is the
  finding. Replaced by four: each ask is offered again for its gap and its order placed that month, every ask closed;
  the new paper is that gap and no more (at most the orders' cost and ground less what the treasury held of the
  proceeds, a granule an ask; the principal by the face asked alone); the inbox says the order was placed on what its
  ask raised; the month's money audit closed. The run: 1 ask, 253 raised before, the cash at the pass 96; placed for
  461, its paper raising 391 more (the gap 365 and a granule); the builder's other measures, no longer held for it,
  ordered 4 times. The section's other assertions, and the cash coming back in "THE CASH COVERS IT", are unchanged.
- **WorksFinanceCheck, the fully raised ask: its fixture premise restated.** It was "cash held is short", the reserve
  and the lower ask's earmark taken out of this ask's proceeds; under the real law the treasury holds them. The fixture
  now says that, and that the cash over the reserve beside the earmark is short of the cost and a loan of that shortfall
  would sell; its three assertions (ordered, the lower earmark released, no additional paper) stand unchanged and green.
  Its title: "a fully raised ask whose proceeds the treasury holds borrows nothing again".
- **The named harnesses, one at a time, on the merged code (62a4e1551 and the section-1 restatement):** `--fast`
  green; AskProceedsCheck, AuctionCheck, WorksFinanceCheck and EssentialWorksCheck green; AutoBuildCheck exit 3, main's
  known red only. Before the merge also IslandLinkCheck and BuildCardCheck green. The pick: ALL (AllChecks lists the new
  harness), so a dry run is owed; its review line says "not needed - no money, saves or threading in the diff", but the
  batch moves the city's bond money and its works orders, so the brief has the orchestrator order a stranger's money
  review at "mends at".
