# XI2 - XI1's texts on Trade, true of today's model (0.9.44, 2026-11-07)

## WHERE IT STANDS

**Ready.** A small fix batch, run on Sonnet whole (BP1 item 7).
- **The branch.** `batch/XI2` from main 0.9.41, merged with main 0.9.42 (R0g) at 24070b3a; the generated docs took main's side
  and `--pick` regenerated them.
- **The trace.** Nothing moves. Five pure getters and the words; steps 5 and 7 of `repriceCurrency()` are untouched. No ensemble (EN1).
- **The checks.** `--pick` exit 3, main's recorded reds only (section 4).
- No money changes, no ★ calls.

## 1. What it builds

XI1's gate review (2026-11-06) found three texts on Trade that the bounded rule made wrong, and XR5 (0.9.35) changed what a peg and a
band do since. Each is now true of the model as it stands.

- **THE RULE** (Trade › The currency, folded at the foot) said dealing moves the rate "2% for each 1% of a year's output", the
  unbounded rule. It now says up to 10% of a year's output dealt between two month ends, and by less for each dollar past that, and
  that under a peg or a band the rate is held, so dealing moves the rate behind the hold and the vault deals to meet it.
  `INTERVENTION_INFO` (the Interventions row's (i)) says the same in a clause: counted over everything dealt since the month turned,
  and the hold takes it off the rate again.
- **The warning past the bound** (Trade › Reserves, the exchange) read this deal alone. The model reads the sum: step 5 feeds
  `interventionMove(dealtSinceReprice, annual)`. So two pieces of 9% each crossed the bound together unwarned, and the second
  piece's figure was its move alone (`×1.2`) when the model gives it on top of the first (`×1.1`). It now reads the sum and says what
  is dealt already: "This purchase takes the month's dealing to 18% of a year of the city's output", "The treasury has already
  bought D$60.0M since the month turned, 9.0% of a year's output, so this piece lands on top of it", and the figure is the piece on
  top. A sale on top of a big purchase warns when the net is still past the bound (the model's rule is on the net).
- **Under a peg** the old text said "it moves nothing: the rate is held". The rate is held, but the deal moves the float's own rate
  behind the peg, by the rule of the sum, and the vault then deals to hold it (XR5: the hold pays the pressure). It now says that,
  with "a peg gives way if holding it would take the vault under its floor".
- **Under a band** the old text said "would move the rate ×N" whatever the band did. Now two sentences: once the move would carry
  the rate past an edge, "the rate stops at its edge, D$X" and the float's own rate is the ×N, with the vault dealing the rest;
  inside the edges, the rate moves by ×N to D$X, as under Float and Lean.

The words are `TradeScreen.pastBound()`, a pure method (the title, the line and the (i)); the card draws it through the same
`Kept.scope("past the bound", ...)` as before, so a month with nothing new writes nothing.

New pure getters on `ForeignAccounts`, all read by the screen and none computed in it:
`dealtYears()` (the net dealing since the reprice in years of output), `yearsOfOutputWith(local)` (that plus one more deal),
`dealEffectOnTop(local)` (the rule of the sum less the rule of what is dealt), `dealFactor(local)` (its factor, 1 or more),
`rateAfterDeal(local)` (the rate the regime leaves: the peg, a band's edge, or the rate times the move) and
`regimeHoldsDeal(local)` (whether the regime keeps the move off the rate). `dealEffect()` keeps reading the deal alone: Policy's
preview is a fresh 1% and calls it.

## 2. Decisions

- **The bound is on the net.** A sale after a purchase subtracts, as step 5 does. A treasury that bought 20% of a year and now asks
  a sale of 5% sees "takes the month's dealing to 15%".
- **The rate after a deal is the rate now times the move.** As XI1's text had it: the month's other forces and the fading of what is
  felt are not in it. A band's edge clamps it, a peg's rate is the peg.
- **The Lean defence is not in the warning.** Under Lean the central bank may sell into the fall the purchase causes; the text, as
  XI1's, gives the deal's own move.

## 3. Left as it is

- The manual (the published page) still states the old rule for the interventions, a peg and a band (XR5's notes say so).
- Policy's deal preview is `DEAL_SHARE` (1% of a year's output), under the bound.
- **Not looked at by eye:** the warning on Trade, Reserves, with a piece dealt and an ask on top, under Lean, Peg and Band.

## 4. The checks

- **InterventionCheck 6 (new, model).** Two pieces of 9% cross the bound together; the second's effect is `0.1376` against `0.1800`
  alone; the effects add to the rule of the sum, which is what the reprice felt. Under a peg: the rate is the peg, the float's own rate
  moved by the rule of the sum against a twin that dealt nothing, and the vault sold the dollars to hold it (US$12,960k against the
  twin's 0). Under a band: the rate stopped at the weak edge with the float's rate beyond it, and a small piece stays inside the edges.
- **KeptCheck 74b (new, the texts).** On the Reserves page of the played city, each scenario a fixture that causes the condition and
  each drawn kept and equal to a page built new: two pieces under the bound (the title, the "already bought", the figure on top and
  not the figure alone), an ask past the bound with nothing dealt (the old title), a sale on top of a purchase, a Peg, a Band held at
  its edge and a Band inside its edges. A scenario deals on `ForeignAccounts` directly and puts it back from its save array
  (`toSaveArray()` / `restore()`), and no month turns, so the sections after run on the city they ran on.
- **A first draft failed two lines of KeptCheck that main's passes** (`Policy's Taxes › Profit page: a policyRow was found...`, and
  `the kept header's signature equals a header built new ... (18 differ)`): it dealt through `Game` and stepped a month after each
  scenario through `pageReshaped`. A run of main alone on the same machine passed both (exit 3), so the draft caused them; its
  cause was not traced further (the city was 8 months older at KP7: 4455 against main's 4447), and the final fixture takes no month.
- **Final runs.** `--pick` on the merge with main 0.9.42 (31 of 186 harnesses) exits 3: main's recorded reds only (MonetaryCheck,
  KeptCheck's one line, LongPlaytest), none NEW. InterventionCheck: all green, 5.7 s.
