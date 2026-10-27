# I2b, the whole-cost test on every other sector's orders: already shipped as 0.7.113 (2026-10-27)

**What was asked.** The row queued 2026-10-18 (Jerus: "every order", "rec all"):
every order through `Game.Slices` to pass the money gate on its whole cost,
as SpreadPlanner's users do, whatever the sector's cash.

**What was found.** It shipped on 2026-10-11 as 0.7.113, under this same id
(todo-archive: "I2b (0.7.113): decision A13, the whole-cost test reaching the
orders that go through `Game.Slices` and the sea's orders", done, 9ce5f5a,
merged as 01bf29c; ensemble 24 of 26). `Slices.passes()` strikes the interest
on `orderGate.testedOn(cost, cash)`, and `orderGate` is SpreadPlanner's own
`ON_ITS_WHOLE_COST` (Game, banner ON ITS WHOLE COST). InvestCheck's section
"an order is tested on its whole cost, whoever pays (0.7.113)" holds the
brief's two cases already: a cash-rich Diner earning half the bar refused,
and one at the bar built, against `PROFIT_OVER_INTEREST` and the model's
rate. The row came from spec-materials §1's "Still open" line, written before
0.7.113 and never brought up to date; this branch corrects that line.

**What still differs from SpreadPlanner: the rate alone.** `Slices` strikes
the whole cost's interest at the rate the order's financing carries:
`projectRate(sector, borrowed)`, the leverage the order leaves, or a cheaper
bond's blend (0.7.113: "the till's part priced as the money it stands in
for"). SpreadPlanner strikes it at `projectRate(sector, wholeCost)`, as if all
of it were borrowed. For SpreadPlanner's users the two agree (they hold no
cash when they weigh, O5's ptW); for a cash-rich shop they do not, and the
whole cost at `projectRate(cost)` would refuse more.

**Nothing in the model changed.** No trace moved; no ensemble was run.

★ The rate: leave 0.7.113's (recommended: it is the rate the order is
financed at, and a till-paid order leaves the sector's leverage where it
was), or move `Slices` to `projectRate` of the whole cost as a new batch
under a new id, with its trace and ensemble (CC1-S's stalls are the premise
to watch).
