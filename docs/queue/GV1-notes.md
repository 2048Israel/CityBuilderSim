# GV1 - the government's wealth, and the test player's January ({next}, 2026-11-10)

The brief is GV1's row in `QUEUE.md`, with GV2 folded in, and
`docs/studies/spec-wealth.md` sections 2.7 and 3.2. Branch `batch/GV1`
starts from ee6130b1. Written and checked by Codex; the orchestrator gates
and merges it. Nothing here changes a live player's policy automatically.

## Decisions taken, with their recommendations

- The five estimated bands are the brief's: below 0.1 broke, 0.1 to below
  0.5 thin, 0.5 through 2 sound, above 2 through 5 rich, above 5 hoarding.
  Recommendation taken: keep the exact 0.5 and 2 endpoints inside the
  test player's quiet band; its rule says *under* and *past* those bounds.
- Recommendation taken: require twelve recorded revenue months and positive
  revenue. An unfinished year is not a forecast; zero receipts do not turn
  the government infinitely rich. Screens explain an unavailable ratio and
  notices and the test player retain their state without acting on it.
- The stock is treasury cash plus the whole fund at its existing marks plus
  the vault at the current local-money rate, less the city's outstanding
  principal. Fund cash is counted once. Existing getters own every figure.
- Recommendation taken: show an illustrative one-percentage-point cut to
  the largest of the four tax families, and a five-point rise to pensions
  or EI, whichever moves the larger monthly payment to current recipients.
  Detached `TaxPolicy` copies and `PolicyPreview` provide the amounts; the
  existing controls still require the player to choose and apply a policy.
- Recommendation taken: buyback advice opens an existing CD4 ticket, never
  deals by itself. Cash must exceed the debt, and the actual offered price
  must yield more than idle cash (zero). Discount bills qualify despite no
  coupon. The line says coupons stopped, not an invented monthly payment
  on a bill. Affordable partial purchases retain CD4's unsettled-piece rule.
- Recommendation taken: retain the current wealth band in a live notice's
  existing saved key, not another save field. The first measured year is a
  read baseline. Crossing settles it and raises one unread notice; the
  live baseline survives the two-year cull. No save-format change.
- GV2 is the player's rule only, shared through `GovernmentPlayer` and
  `LongPlaytest.run` by the playtest, ensemble and WealthRun. The scripted
  income/property-tax moves and WealthRun's separate prototype are removed.
- Recommendation taken: act after each successfully completed January.
  The prototype's December modulo preceded January's newly opening policy
  gates; this order lets the current January's existing setters apply. The
  first eligible January is month 13. Each January is considered once per
  city, even across long skips; loading resumes with the next played month.
- The rich-side steps are the brief's: profit tax down five points, sales
  down two, pension and EI replacement up five, withdrawal to its maximum.
  Recommendation taken: the prototype's poor-side ceilings (profit 30%,
  sales 15%) and pension floor (30%); EI's existing default is its floor,
  and withdrawal falls one literal dial step toward zero. A lever already
  beyond that player's bound is left there rather than moved the wrong way.
  Historical gates and model caps still hold. No dividend or new lever.

## Verification and the review

Checks name the PC's earth-2 and world-1 by absolute C:/ paths and use
scratch cities only. The first named run passed GovernmentViewCheck,
GovernmentPlayerCheck, InboxCheck, BuybackCheck and the 2,400-month
LongPlaytest. The reviewed discount-bill fixture also passed separately.

The Government overview was rendered at 1456 by 900 for an actual played
city, a hoarding city with all three doors active, an unfinished revenue
year and a full year with no receipts. The card and its amounts fit without
clipping. Clicking the three links opened the buyback ticket, tax page and
pensions page. A repeated draw made no new nodes and reused 278; the kept
and freshly built page signatures matched. Evidence is in this worktree's
`target/checks/GV1-*.png` and `GV1-visual.log`.

The January player changes the default trace, as its two-year harness
demonstrates with two actual tax moves across a scratch save/load. The
required ensemble is running on the final production model at bf20a92b;
the snapshot's dirty files are harnesses, registrations, notes and maps.
The final `./batch-check.sh --pick` exited 0: eleven of 200 registered
harnesses passed in 132 seconds, maps regenerated, zero firm Stale
findings. Selected: DiscoveryCheck, EnsembleCheck, GovernmentPlayerCheck,
GovernmentViewCheck, InboxCheck, LandmassCheck, LongPlaytest,
PlaytestFixCheck, PolicyDatesCheck, PrizeCheck and TimingCheck. The latter
checks the timing machinery; no full suite or `--timing` run was made.
WealthRun's standalone source also compiled against the completed model.
The review's synchronization flag is the weak per-city review map's lock;
this batch starts no threads. Source whitespace and LF checks passed.

The premium fixture initially assumed 1.01 times face was short of the
whole quote; it was not. It now restores the deliberately stale market
after the purity test and places cash strictly between the quoted whole
price and the larger of face and the quoted quarter. In the passing run:
whole 1006.631, quarter 251.658, face 1000, cash 1003.316. Both original
assertions stand: the premise occurs and advice offers an affordable part.
Only the fixture changed after the ensemble snapshot, not production code.

Review caught the zero-coupon/yield distinction and the premium-price
partial-buyback case; neither weakens the existing dealing rules. The
original `Game.quoteBuyback` still reprices as before; its factored
`quoteBuybackAtRest` lets advice read the same auction without changing the
curve or interest. No production money flow was changed for GV1's screens.

For the screen check: Government > Overview, the wealth line and its
component tooltip; partial-year and no-revenue text; the three doors,
including a discount bill and paper with the central bank holding a share.
The figures must update in place without moving the scroll.
