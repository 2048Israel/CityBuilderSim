# RE1 - Real Estate's stall, found at its cause (study, no tag, 2026-10-30)

Jerus, 2026-10-28: "gotta add workers to real estate"; 2026-10-29: "sure queue
the small thing to find cause". HD1's posts (2 DIPLOMA posts a Studio and
Low-Rise, homes before workers, the payroll in the rent floor; fec704a7,
reverted in f3704d8d) stalled the ensemble's seed 4 near 25,000 people from
m1,100. This batch finds why. It is a study: the model and the trace are
main's (0.8.77), so there is no suite run and no ensemble. The posts go back in
as RE2, which the ship file queues. The world lane, after FL1.

## 1. The cause

**The posts' payroll took a thin landlord margin into a sustained loss, and a
landlord at a loss cannot fund a home by either door, so the city stopped
building homes. With no homes coming, the staffing gate stopped every builder
whose posts needed workers.** The town then sat at its migration target for
900 months.

The chain, on HD1's tree and seed 4 (AUTO2, both data files named):

1. **The margin.** Without posts the landlords cleared +8,472 a month before
   tax on 39,004 of rent at m900 (22%). With the posts they cleared +4,224 on
   43,584, after a payroll of 3,971 (9% of the rent). At m975 the driver's tax
   step lifted the property tax from 1.5% to 2.5% in both runs. The run
   without posts fell to +400 and recovered (+4,568 at m1,000, +17,993 at
   m1,200). The run with posts fell to +37 and then lost money in 275 of the
   301 months from m1,000 to m1,300 (-2,004 at m1,000, -11,387 at m1,200).
2. **The till.** The landlords borrowed in 228 of those 301 months to carry
   the loss. Their interest went from 14,286 a month at m1,100 to 29,115 at
   m1,200, which deepened the loss, and the till stayed near nothing (748 at
   m1,200). Nor did the owners put money in: nothing was raised after m1,058,
   where the run without posts raised 156,821 at m1,100 alone.
3. **Both doors to a home shut.** From m1,062 the landlords held in 211 of
   the 301 months with "needs $N of its own for the 15% down payment on a
   Low-Rise Apartments" ($21.2M at m1,070, $35.2M at m1,300). The run without
   posts held so in none of them. With the down payment waived, the lender's
   test declines instead (§2: a loan with no own funds fails the 1.20x
   cover). The last home was ordered at m1,057: their 8,355 homes, and the
   city's capacity of 27,282, stay as they are to m1,500.
4. **The staffing gate holds the rest.** Population settles at the migration
   target (0.75 of posts times residents per post, plus 0.25 of homes):
   24,588 to 25,348 from m1,100 to m1,500, with posts frozen at 13,379 to
   13,875. With no homes coming there are no new workers. From m1,100 to
   m1,500, Manufacturing, Food Processing, Automotive and Agriculture held at
   the gate in all 401 months, Freight in 394, Cement & Concrete in 375 and
   Luxury Retail in 265 ("the city could staff 20% of a Meat Works; it wants
   80%"). From m1,000 to m1,300 there were 1,342 staffing holds against 35
   without posts.

**The brief's reading was not it.** Migrants were never priced out: from
m1,100 to m1,500 the affordability pull stays at 1.00, the rent burden at 0.20
to 0.21, and the homes have room (crowding 0.62 to 0.66, room for about 10,000
more). The rent rises with the floor (2.59 to 5.50) because the carry does
(the interest doubles in the first hundred months, the tax and the repairs
follow prices), while nobody new comes. The landlords' own posts in the
latent demand and the floor make little difference (§2). Wages never reach
the lender's net operating income: a new block's estimate is its rent alone,
and the lender takes off maintenance and property tax (`housingCarry()`)
only.

## 2. One run per candidate, each switched off alone

HD1's tree reproduces exactly with both data files named: 44,157 at the end
(25,028 at m1,200), and 241,339 for the coupon alone (3dea900f). A switch
armed from the founding parts from the first month, which is weather (below).
So each switch was armed at **m960**, before the onset: every run follows the
identical path to m960 (the control is byte-identical to the run without
switches), then differs by the one switch alone. The columns after the bar
count months from m1,000 to m1,800.

| switched off from m960 | m1,100 | m1,300 | m1,500 | m1,800 | down-payment holds | loss months | staffing holds |
|---|---|---|---|---|---|---|---|
| nothing (control) | 25,348 | 24,749 | 25,213 | 26,217 | 711 | 528 | 5,225 |
| the posts altogether | 27,624 | 60,190 | 77,134 | 139,090 | 20 | 0 | 705 |
| the landlords' wages (`wages`) | 25,724 | 31,035 | 60,253 | 98,855 | 38 | 0 | 1,817 |
| their loss covered by the city (`subsidy`) | 25,630 | 26,229 | 39,441 | 75,321 | 130 | 109 | 2,306 |
| the payroll in the rent floor (`floor`) | 25,964 | 24,842 | 25,374 | 46,890 | 423 | 156 | 4,310 |
| their own posts in the latent demand (`latent`) | 26,493 | 25,617 | 26,324 | 42,305 | 393 | 99 | 4,439 |
| homes before workers (`gate`: they ask staffing) | 25,348 | 24,749 | 25,213 | 26,217 | 711 | 528 | 5,225 |
| the 15% down payment (`down`) | 25,906 | 25,081 | 25,761 | 26,659 | 0 | 774 | 5,327 |

- **The wage bill holds the town there.** Off alone, the landlords never lose
  a month and the town is past 60,000 by m1,500. The city covering their loss
  (the player's own auto-subsidy) frees it more slowly, because it restores
  break-even and not a till.
- **The posts' labour draw adds to it.** With the posts gone (no jobs, no
  workers drawn, no payroll), the town is past 60,000 by m1,300 and holds at
  the gate 705 times against 1,817 with the wages alone: about 250 DIPLOMA
  workers matter to the builders at the gate.
- **The down payment is not the lock by itself.** Waived, the landlords'
  orders are declined by the lender instead (440 "Declined Low-Rise
  Apartments - its rent would cover the mortgage" from m1,000 to m1,500): a
  landlord with no own funds fails the cover on the whole loan. Both doors
  need a till.
- **The floor and the latent demand only nudge it.** Both stall to m1,500 as
  the control does. HD1's run with the payroll out of the floor also stalled
  to m1,500.
- **Homes before workers changes nothing here**: a block's 2 posts always
  pass the staffing test on seed 4 (staffable share 1.0), so the switch never
  fires.
- `wages` is a study fiction: the households are still paid off the city's
  posts (`Game.syncHouseholdAccounts()`), and the audit, which debits the
  sectors' payroll, does not see the unpaid bill. The `posts` and `subsidy`
  rows conserve money.

From the founding (switch armed at m0), m1,800 population: floor 49,345,
latent 152,513, gate 26,217, wages 110,031, posts 120,177 (byte-identical to
the coupon alone, so nothing else in HD1's diff moves the model), down 68,812,
subsidy 87,811. The latent switch frees the town from the founding but not
from the fork, which shows these runs are weather.

## 3. Today's main does not stall

On 0.8.77 (WN3, EM1, HD2's coupon kept abroad, FL1), HD1's posts applied
cleanly (`git apply --check`), and seed 4 ends at 208,664 against main's
286,350. Main's own run reproduces the baseline's seed 4 byte for byte when
both data files are named. The landlords' margin carries the payroll:
their pre-tax income is 5-25% of the rent with posts (main 11-35%), with a
payroll of 7-9% of it, 21 loss months in 2,400 (main 10) and 38 months of the
down-payment hold (main 38). The other 15 AUTO2 seeds with posts, and
ZERO_SLACK seed 4, compared with the 0.8.77 baseline (`docs/ensemble/0.8.77.md`):

| | 40,000 people at | final population, over the baseline's |
|---|---|---|
| baseline, 16 AUTO2 seeds | m956-m1,145 | - |
| with posts, 16 AUTO2 seeds | m916-m1,208 | 0.63 to 1.31, median 1.02 |
| ZERO_SLACK seed 4 | m1,124 (baseline m995) | 160,184 against 196,744 (HD1: 30,090) |

No seed stalls. Seed 8 falls furthest (272,479 against 429,530) and seed 10 is
latest to 40,000 (m1,208). The margin is what protects the town, and it thins
late in a run: on seed 4 with posts the landlords' pre-tax income falls to 5%
of the rent by m2,400 (main 11%).

The harnesses HD1's posts broke, with the posts on 0.8.77 and both data files
named:

- HealthCheck, OrderSearchCheck, SaveFileCheck, LabourCheck, MonetaryCheck and
  BuildingDataCheck pass. SaveFileCheck needs none of HD1's fund-fixture
  change.
- HousingCheck (with its section 6) and SectorStatementCheck pass with HD1's
  edits to them from fec704a7. Without them SectorStatementCheck's "...a gate
  it has not is no gate: no ore without a site, no licence, no posts, no
  ground (297)" is red.
- EducationCheck is red: "fixture: last month ran a surplus, on the bridge".
  The 50% income tax's surplus no longer shows within its three months.
  HD1's EducationCheck edit (a revenue sum) no longer applies and is another
  line.
- ScaleCheck shows main's recorded reds, but section 9 is red on its cash line
  alone, at 1.95e-02 against main's recorded 1.20e+00 (main has five section
  9 lines red). It is tagged NEW only because the figure differs. Section 6's
  town is HD2-S, which EM2 rescans.

## 4. What RE2 must do

Put the posts back as HD1 built them, on main: fec704a7's `buildings.json`,
`BuildingManager`, `Sector`, `sectors/RealEstate`, `BuildCard`, and the edits
to `HousingCheck` (section 6) and `SectorStatementCheck`. Leave out its
`HistorySave`, `YearBook`, `ui/HistoryScreen`, SaveFileCheck (13b and the fund
fixture, which shipped or are not needed) and the EducationCheck sum. Then:
re-cause EducationCheck's surplus fixture; read ScaleCheck section 9 again
once EM2 has given section 6 a growing month; and run the ensemble on its
final model. In that ensemble, look in every seed for this study's
signature: the landlords at a loss with the down-payment hold, the town flat,
and the gate holding the builders. RE1's probe (`RE1/probe-main.patch`) counts
it. If no seed shows it, the posts ship as built. If a seed does, that seed is
RE2's fixture for the trap (RE1-T, pending: Jerus's call how to close it).

**The trap (RE1-T)** is main's, and the posts only make it likelier. A
landlord at a sustained loss has an empty till. With it he can neither put
15% down nor, without the down payment, pass the lender's cover, so the city
stops building homes and then stops at the staffing gate. Main's own seed 4
has 38 months of the down-payment hold and 10 loss months. Ways out:

- (a) the rent carries the staff at once: the payroll billed with the rent
  each month, not reached through the floor's lift at lease speed (the
  trap's rent stayed under its floor: 3.27 against 3.34 at m1,200). The
  `wages` fork is the nearest measurement of this.
- (b) the owners are asked for the down payment whatever the quote (Equity,
  the money lane's file, after EM2).
- (c) the lender judges a block on the whole rent roll, not on the block
  alone.
- (d) leave it, with the ensemble watching for the signature.

Recommended: (d) until a seed on the model RE2 ships shows it, then (a), with
that seed as the fixture, because the trap's first link is the loss.

## 5. ★ Calls taken, on the recommendation

- **★1 The fixture: HD1's own tree**, because seed 4 no longer stalls on
  today's main. That tree reproduces HD1's figures exactly. Recommended:
  only a town that stalls can show what holds it.
- **★2 The method: each switch forked at m960** beside the brief's runs from
  the founding. Recommended: from the founding every run parts in the first
  month, and the latent switch read 152,513 there against a stall to m1,500
  from the fork.
- **★3 RE2 is the posts as built, with the ensemble watching for the trap**,
  not the posts plus a fix for a trap that no seed on 0.8.77 shows.
  Recommended: a fix needs a fixture that causes the trap, and today only
  HD1's tree does. The trap itself is the pending row RE1-T.

## 6. How it was done

All of it ran in a scratch worktree of fec704a7 and on scratch copies of main.
Nothing in `src/` changed on this branch. One slice went to Sonnet 5.5 at
xhigh, the switches, and its diff was reviewed as a stranger's. Opus did the
probe, the runs and this note. The files are in `docs/queue/RE1/`:

- `switches.patch`: the seven switches and `RE1.java`, for fec704a7.
  `-Dre1.off=<keys>` and `-Dre1.from=<month>`; inert by default.
- `probe.patch` (for fec704a7's `tools/ensemble/Ensemble.java`) and
  `probe-main.patch` (for main's): a TSV a month of every investor's last
  decision and the landlords' books (`-Dprobe.file`, `-Dprobe.from`,
  `-Dprobe.to`).
- `run.sh`, `matrix.sh`, `summary.sh`: the runs and the table above. The
  classes and drivers sit beside them (paths in the scripts), and both data
  files are named.

A seed 4 run takes 20-60 s. All told: 2 trees rebuilt, 15 fork and founding
runs, 17 seed runs on main with posts, and 10 harnesses on main with posts.
