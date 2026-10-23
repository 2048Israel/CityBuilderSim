# CD4 - closing early: notes

*0.8.30, 2026-10-23. The batch is `docs/studies/spec-city-debt.md` §8's CD4
row and the sections it points to: the buyback as a reverse auction (§6.2,
§6.3), callable lines and the call (§2.6, §6.4), "worth calling", and its
screen, Close it now and the call card (§7 board 3, D31). Jerus put CD3's ★5
into it on 2026-10-23 ("★5 to CD4"). Built on main at 5b67eafc (0.8.29),
merged with main at b60471f4 (the review desk's records).*

## 1. What it does

- **The reverse auction (`Buyback`, pure, beside `Auction`).** The city names
  the face of one line it wants back and, if it likes, the most it will pay
  per 100. Every holder but the central bank offers what it holds in
  `SLICES` (10) slices, each at the price where its own buying rule would
  stop wanting it, taken at the holding that remains once the slice is sold,
  and never under fair (D17):
  - households at the deposit rate plus their remaining share of the line
    over `HOUSEHOLD_PAPER_APPETITE` (`Auction.householdsAt` inverted);
  - businesses at the better of the deposit and the world plus their share
    of idle wealth over `OutwardInvestment.APPETITE`, at fair when the rule
    wants nothing;
  - the world at its rate plus the country premium plus what its remaining
    stock still asks (`CapitalFlows`' rule, to `MAX_SPREAD`), at fair in a run;
  - the bank at fair (D34); dollar paper's world at fair (D26);
  - the central bank not at all (D18): its face stays on the line.
  The city takes the cheapest first, each paid its own price (D19, multiple
  price); the stop is the dearest taken. `Game.quoteBuyback()` strikes it once
  and `buyBack()` pays exactly that. A part of a line is retired by
  `Debt.retirePart()`: each holder's face down by what it sold, the face,
  principal, schedule, discount and carrying value scaled, the rates kept. A
  line with a sale not yet settled may be bought back only whole, as before.
  `quoteRepurchase`, `repurchaseGain` and `repurchaseDebt` stay as the whole
  line's quote, gain and purchase; `fairValueOf()` is the old "worth today".
- **Callable lines (`CallTree`, `LongTermBond`).** A bond of 10 years or more
  may be sold callable (D22): first call at 5 years for a 10-year, 10 for the
  longer, at 100, whole lines. The option is priced on a Ho-Lee tree, monthly,
  fitted to the curve's own discount factors (the tree prices every zero and
  every non-call bullet at the curve to 1e-9), at the volatility of the city's
  quoted rate over `VOL_MONTHS` (60) months, never under `RATE_VOL_FLOOR`
  (0.75 points, D23). On the study's calm curve the spreads are 34.7, 58.4,
  49.4 and 82.0 bp against the study's 34, 58, 49 and 82. A callable sale's
  fair yield is the non-call fair plus the spread; it reopens only a callable
  line of the same term and call. `DebtManager.marketValue()` prices a
  callable line on the tree, so the buyback's fair and the balance's fair
  value see the option.
- **The call.** From its first call month a callable line is called whole at
  100: every holder is paid its face, the central bank included (the call is
  the paper's own term, `paperBoughtBack()`, settled the next month as
  before). The gain or loss is carrying value less face. **Worth calling**
  (D24) when the remaining payments at today's curve, as a non-call bond, less
  the face, less a new sale's fee pass `WORTH_CALLING` (3%) of face.
- **CD3's ★5.** History keeps `retiredGain` a month. `Game.monthOfSpending()`
  and `GovernmentScreen.spendingYear()` add it back to revenue less the
  surplus, so the year's "Paid out", the Spending page's share of GDP and the
  fund's floor agree with the month's figures after a buyback.
- **The screens (D31).** Finances: a piece's card says "Close it now" (with a
  callable line's call date and "Worth calling" when it is), and opens a page
  of its own: the ticket (the face, the most per 100, offered and cover, taken
  from whom, stop and average, fair, the price), the offers as a ladder
  cheapest first by holder with the stop and the most-to-pay marked, the three
  comparisons (against carrying value, against face, over fair), the journal,
  the honest line, and the call card with Call it. Borrow: a Callable switch
  for a bond of 10 years or more, with "the call adds N bp to the coupon". No
  colour on a gain or a loss.

## 2. How the money closes

A buyback is cash: the treasury pays the price on `TreasuryLine.BUYBACKS` and
each holder receives exactly the sum of its own slices - the bank to its cash
(`sellPaperBack`, with the discount it had not yet earned on what it sold),
households to their savings and the businesses to their tills at once, the
world's share abroad through the next month's window - so nothing is counted
twice and nothing waits for the central bank any more. A call pays the same
way at the face, and the central bank's share is destroyed at the next
month's top as it always was. The gain against carrying value is no cash: it
is booked to the month (`retiredGainSoFar`) and the bridge's not-cash row
takes it back out. BuybackCheck and CallCheck close the audit in the month of
each buyback and call and the month after, partial and whole, with and
without the central bank. The treasury bridge shows a residual in the month
of a buyback or a call - the bank's share of the retired piece's coupon,
which clears the next month - and main shows the same figure to the cent
(-628.30 on the same fixture on both), so it is not CD4's: it is a follow-up.

**What a gain reaches** (CD3-C): the gain is in the balance (CD3's ★3), so it
reaches the surplus the rollover nets (`surplusOverLastYear()`) and the
fund's pay-in (`surplusThisYearSoFar()`) for the year; the fund's floor
(`monthOfSpending()`) now leaves it out, as spending does. That is ★4.

## 3. ★ calls for Jerus

1. **★1 The households offer dear when deposits pay far under the paper.** Their
   rule holds the line for its spread over deposits, so their last slices ask
   nearly the line's value at the deposit rate: in a played city at 14.7%
   rates the households' slices ran 140 to 163 per 100 against fair 126, and a
   whole 10-year round trip cost 123 per 100 against fair 97. That is §6.2's
   rule as written; the most-per-100 field is the player's tool. Keep, or
   price them otherwise (at most the auction's yield they bought at, say).
2. **★2 The call pays the central bank; the buyback does not** (D18). A call is
   the paper's own term, like a maturity, so the central bank is paid at 100.
3. **★3 The volatility is the quoted short rate's** (History's `interestRate`),
   the nearest the history keeps to the spec's 1-year rate. A city with
   volatile rates prices the option dear: 118 bp on a 10-year in the played
   city above.
4. **★4 A buyback's gain stays in the surplus** that the rollover nets and the
   fund pays in from; only spending (the year's "Paid out", the fund's floor)
   adds it back.
5. **★5 The honest line is a month's**, where §6.3 writes a year's.

## 4. The checks, the trace and the ensemble

- **BuybackCheck** (new): the pure ladder (every offer at or over fair, the
  bank at fair, each holder dearer as it sells, the households' first slice
  against the constants, the central bank never offered, the most per 100,
  stop >= average >= fair); the hiking case caused (households under fair, so
  at fair); a played city's half buyback (the piece halved, each holder down
  by its sale, cash, the gain, the audit two months); the central bank's face
  left; no money pump; a dollar piece at fair; a refusal changes nothing; and
  ★5 (a buyback at a gain; revenue less surplus plus the gain is the
  months' expenses; without it the year is off by the gain).
- **CallCheck** (new): the tree is the curve; the option's bounds and order;
  the volatility and its floor; the sale (coupon over a twin's non-call by the
  spread, the call date, no reopening across kinds, a 5-year refused, nothing
  callable in a city that never asked); the call refused before its date and
  paid at it with the central bank holding part; worth calling from both
  sides against `WORTH_CALLING`; the save.
- **Restated with their premises kept** (marked `// CD4:`): CentralBankCheck
  5 and HoldersCheck 5, 10, 11 read each holder's own price from the quote
  instead of a pro-rata share, and the central bank's face staying put (D18).
- **The trace:** the default playtest never buys back or calls, and its log
  on the final model is byte-identical to main's but for the run-time line,
  so the ensemble was not run (EN1).
- **The suite:** baseline on main 131 of 131. On the final model merged with
  main (`./batch-check.sh`, beside other batches' JVMs, timing soft): 133 run,
  132 passed; the one red StaleCheck's README count (130 harnesses), fixed and
  Stale then 0 firm findings. The maps are regenerated. **`--timing`, alone and hard: 10 of 10 passed** in 226 s.

## 5. Who wrote what

Sonnet 5.5 at xhigh wrote the three slices: the reverse auction and its
harness (and, on review, the whole buyback of an unsettled line kept as
before, and the four restated sections), the callable lines and the call
(and, on review, the residual traced to main), and the screens. Claude (Opus
5.5) wrote the briefs, CD3's ★5 fix, reviewed each diff and the screens'
snapshots, and wrote the record.
