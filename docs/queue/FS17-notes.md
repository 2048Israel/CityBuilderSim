# FS17 notes: the saves, the history and the previews cause their conditions

Batch FS17, built 2026-11-20 by Claude (Opus, headless) on `batch/FS17` from
origin/main f4fb7dc65 (0.9.170). Origin/main 3294dde04 (0.9.171: FS16, FS13)
is merged in. The brief is `docs/studies/flaky-suite.md` 6.16 and its table
(`flaky-suite/by-section.md`, the FS17 rows), with the four FS17 rows of
`docs/notes/known-reds.txt`.

The batch is harness-only. The diff is ten of the eleven harnesses
(BasketYear, Calendar, Gdp, History, MoneyDates, NewGame, PolicyPreview,
ReadPath, Reload, SaveFile, each `...Check`), one line of `Observation.java`
(PolicyPreviewCheck and SaveFileCheck leave `Observation.HARNESSES`, ★3) and
the generated maps. SkipReportCheck's one row (5a) is KEEP, so it is
unchanged. No model class is touched, so the model and the trace do not move
(`moves_playtest=no`), and no ensemble is owed.

## 1. The known-reds rows: all four go green

None of them is a model fault, so nothing is queued and the ship file's
`## known-reds` is empty: the four rows leave the file at the merge.

| row | harness, line | cause | change |
|---|---|---|---|
| 78 | SaveFile: over the default it owes what it sells for, and paid some late | HS1's college town: the fund's 44,441 Retail shares had no bid, so a year's press sold nothing and paid nothing late | month 1 runs short by the dial's own arithmetic; between the steps the world bids at the fund's ask for all that rests at or under it (`Exchange.tradeForCheck`); month 2's top pays late |
| 105 | SaveFile: somebody could not afford a basket even at full shelves | on BO1-S's path every cell could afford a basket at full shelves in the month read | the cell with the least after its fixed bills is left with no savings, paper or dollars abroad and its credit room borrowed, before each of the two struck months (`groceryDemandOf` bounds its baskets by its food money) |
| 106 | BasketYear: the 1900 town with the cap lifted | on BO1-S's path (the founders' dividends the owner families') the lifted town bought no car in 24 months (0.0000; main 0.0133) | before every month each household cell is handed a car's price over its cushion (`SHARE_CUSHION_MONTHS` of take-home plus two cars at the local price) and its lockout lifted, as FS7 did in CarCheck |
| 107 | BasketYear: ...and is smaller than the same town's with the cap lifted, and than the default town's | follows 106 | the same; now 0.1208 a household in the default town, 0.0000 in the 1900 town, 0.1562 with the cap lifted (the cap peaked at 0.0026) |

## 2. What each harness causes now

The table's 25 CAUSE rows, by harness. The 15 KEEP rows (the save's
identities, ★4) are untouched: SaveFile 13's and 14's loaded-against-live
comparisons, ReloadCheck 1, 2, 4 and 5, ReadPath 1715-1891, NewGame 292-528,
History 1-2c, PolicyPreview 2 (at the city's own), MoneyDates' 746-month
audit, SkipReport 5a.

- **BasketYearCheck** (Opus). 7: the car money, above. 6: one quantity by one
  path: the homes' ask is the model's own (`HomeFuel.lampOilLitres`,
  `coalTonnes`), held to the law homes x share x constant; the struck litres
  are the ask as the draw delivered it; Retail's ledger is the struck quantity.
- **PolicyPreviewCheck** (slice A; `city()` untouched, GovernmentViewCheck
  borrows it). 6 and 7: the bank's profit by `Bank.setProfitLastMonth`
  (`BANK_PROFIT`), the EI pool by `layOff()`, which retires employers until
  the posts gone pass twice the city's own vacancies. 3: a budget twin, the
  dials put back to the defaults and the fuel duty set (`BUDGET_FUEL_DUTY`),
  a month struck at them. 4: the deposits put in the bank's book and put back,
  a month's revenue filed with the central bank (`noteRevenue`), and the
  floor's laws on `laddered()`'s twin: `LADDER_POSTS` posts in every job but
  `UNIV_POLICY`, the skilled as many as their posts, every band at par with
  its supply (`LabourMarket.advanceMonth`), the default floor. The WS1 page
  reads the same ladder, its applied floor paid by the labour market's own
  month. 8: a share order by hand reserves cash beside the idle cash
  (cancelled after). 10: the berths handed their share
  (`LandlordPortCheck.handShares`), worked until a quote is struck, at most
  `PORT_MONTHS`, past which the fixture fails. All 19 Observation sites go.
- **SaveFileCheck 13** (slice B). The 150 played months stay (the identity's
  breadth); both waits (up to 240 months each) go. A failed bank is resolved
  (`resolveBank`), two months are struck at the squeeze with the shelf at half
  the want, the hot money's stock at half its target and the poorest cell
  without means (row 105); the arrival is placed (`placeArrivalForTest`); the
  savers' rate is the bank's own rule's at the policy dial
  (`Bank.setDepositRate(depositRateAt(policy))`). Both Observations go.
- **SaveFileCheck, the rest** (slice C). 7: the depots ordered are three of
  the builders' months (`getConstructionCapacity`) and one over, asserted
  past the nameplate and the foreign contractor's month
  (`BuildersShort.foreignCapacity`). 11: 300 houses and 30 shops dropped in at
  the save's month. 12b: the 120-month wait goes; the rate is pinned a tenth
  off (`pinRate`) for the one revaluing month. 14: saved where
  `PriceIndex.monthsUntilRate()` says the index is four shopping months short
  of its basing (bounded by `SETTLING_MONTHS`, failing past it). WN3: a house
  order past the yard (four yards' worth of `BASE_MATERIALS`) draws materials
  from the world in month 1, and the months are the calendar's to the year's
  turn plus two. The fund's withdrawal: row 78. The four Observations go.
- **GdpCheck** (slice D). The rail fleet, the copper yard and the sheds are
  sized to `MONTHS_OF_OUTPUT` (3) of the last month's GDP from the model's own
  prices (`asManyAsWorth`, `EconomyManager.heldPrice`, `Freight.TONNES_PER_SET`),
  not by the 36th month's size.
- **HistoryCheck** (slice D). 2d and 2e on a "lent" city, a founding played a
  month: the bank lent by `Bank.lendCarry`, its capital record put to no bad
  year (`restoreCapitalRecord`) and then to the worst (`MAX_BUFFER`: both
  sides of the conservation buffer), the fee taken by `takeLoanFees`, the
  month recorded by the game's `recordMonth`. 2f: a month at a time until the
  high schools hand out diplomas, read at that month; twelve months is the
  fixture's bound (a school year), failing past it.
- **CalendarCheck** (slice D). 6: played until the log holds the six houses,
  bounded by the points owed on site over the builders' capacity.
- **MoneyDatesCheck** (slice D). 4: `Retail.setWantedSpend(0)` and
  `setSpendingCapacity(0)` before the save, not a month found to want
  nothing; `saveChecks`/`readChecks` keep their signatures.
- **NewGameCheck** (slice E). 1: the insured mortgage issued by hand
  (`setInsuredMortgageRate`, `issueMortgage`) before the last two of the 120
  months; the vault's land was already bought by hand.
- **ReadPathCheck** (slice E). "a city worth reading": the 60-month wait for
  the fund's bonds goes; a bond priced by `BondMarket.plan` and issued, half
  of its largest holding sold to the fund's hand and an ask left resting, a
  share sold to the fund by the world (`Exchange.tradeForCheck`).
- **ReloadCheck 3** (slices E and F). On the caused point (Danzik, after the
  last checkpoint): a market's tally (`restoreTally`), the hand-order good
  re-priced, the carry always placed, an outbreak in force on both twins
  before the skips (`Health.restore`, severity `CAUSED_OUTBREAK`, the mean of
  `OUTBREAK_MIN_PEAK` and `OUTBREAK_MAX_PEAK`), one sector's shares this month
  and last (`SectorBooks.restoreShares`), a trade on every seller-priced list
  after the first month on (`GoodsMarket.record`, both twins); 3e asserted by
  the constant (every point past `SETTLING_MONTHS` and based). Each cause has
  a "by hand" line on the caused point's own city.

## 3. The law failing on a fixture that breaks it

- BasketYear 7: the lifted town is the 1900 town without the cap; with the
  money given it buys 0.1562 cars a household against the cap's 0.0026, so a
  model that ignored the cap would make the capped town read the same and fail
  "stays under the highest cap" and "smaller than".
- PolicyPreview (slice A, six mutations in one run, reverted): `BANK_PROFIT = 0`
  fails "the bank made a profit to be taxed on"; posts on the ladder's no-post
  job fail "a job with no posts"; a staged floor on the wrong side (x1.1) fails
  the under-every-target fixture; `layOff` retiring nothing fails "the EI pool
  has claimants"; a share order of 0 fails the reserve; a sea share of 0 fails
  "the port carried tonnes".
- SaveFile 13 (slice B): no poor cell and the loaded bank's rate at 0 fail row
  105's premise and "what savers are paid" (0.000000 against 0.026833); no
  half shelf and the dial off fail "the shops handed over less" and "the dial
  really did pay out".
- SaveFile, the rest (slice C): no months ordered fails "the order is more than
  the sites' month can build"; no month played fails "the order's month moved
  the town's people"; the rate unmoved fails "the rate was moved"; nine months
  to basing fails the index's fixture; a one-house order fails the world's
  materials; the fund's ask withdrawn instead of bid fails "the world bought
  them (0.0 of 593.57)".
- NewGame (slice E): a mortgage issued after `newGame()` leaks
  (`credit.mortgages 1383.2157 should be 0`, 4 fields leaked). ReadPath: a read
  that rests an ask from the fund's hand fails "reading the city fifty times
  changed nothing". ReloadCheck: the reloaded tally, carry and stop words
  zeroed fail 3c (96 differ), 3g and 3h; no outbreak, no shares, no hand trade
  fail 3f, 3d and 3j by hand (slice F).
- Not mutated: Gdp (its own printed line shows the month reading -1,620 as a
  fall in stock without the held term), History 2d-2f, Calendar 6, MoneyDates
  4, ReloadCheck 3c's notes and 3e (neither can be broken from the harness).

## 4. Still played, and why

- SaveFile 13: the stocks the 150 months built (the mortgages and bonds
  outstanding; the bank's allowance, running cost, loss rates, last month on
  file, year of sheets and equity split; the month's deaths, moves, shipped
  goods and trade) are hard fixture premises: no setter reaches them without
  writing the history whose save the section tests. "The dial really did pay
  out" is caused by the squeeze (every sector subsidised, the income tax at
  its ceiling), not by a setter: a sector's month's loss has none.
- SaveFile 14's later fixtures (the hauls, the college, the buses), which play
  one to three months; ReadPath's "every sector is actually trading" (the 26
  months); PolicyPreview 3's seniors, the clinics' patients and the litres at
  the pump (the month's flows, already hard fixtures). Gdp's 36-month towns
  are the towns GDP is read from; what was picked (the size against a month's
  output) is caused.

## 5. Calls

- ★ PolicyPreview's floor sections read a hand-made ladder twin rather than
  the played city's labour market: the ladder is the only state where the
  rungs, the floors either side of the targets and the workers in them are
  the fixture's; the played city stays for every other section, and
  `city()` is untouched for its borrower.
- ★ SaveFile 13 keeps its 150 played months and strikes two months at the
  squeeze instead of waiting: two because the first month's sale is what the
  second month's top reads (the hunger, the delivered share, row 105).
- ★ The savers' rate is set to the bank's own rule's choice at the policy
  dial, not a number: what is saved is a rate the model would choose.
- ★ Row 78's sale is caused by a world bid at the fund's own ask between the
  steps (a buyer arriving between clearings), rather than a bond holding given
  to the fund: it is the market book the dial's step sells into.
- ★ BasketYear 7 tops the savings up each month (never lowers them) and lifts
  the lockout, but leaves the debt: owing is no bar to buying a car
  (`CAR_DEPOSIT`), and clearing it would move the bank's books.
- Labels whose words had become untrue and that no record names were made true
  (PolicyPreview: "the played city/port" to "the ladder"/"the port", "(none at
  the default)" to "(the duty set by hand)"; SaveFile 7: "the twenty depots").
  Every label that `known-reds.txt` or `gate.txt` names is unchanged.

## 6. What the review changed

Slices A to F were reviewed as strangers' diffs for model files (none),
borrowed builders (`PolicyPreviewCheck.city`, `ReloadCheck.reload`/`Point`,
`NewGameCheck.snapshot`, `ReadPathCheck.handRelief`/`afterALoad`,
`EducationCheck.city`, `LandlordPortCheck`: untouched), measured numbers in
laws, and premises moved to get green. The review changed the untrue labels
above. Slice E left ReloadCheck 3's notes, shares, weights, skips and
seller-priced sales found in the checkpoints; slice F caused them on the
caused point.

## 7. The checks

- Each slice ran its harnesses alone (and the borrowers: GovernmentViewCheck,
  BridgeCheck, ObservationCheck), each green before review.
- On the merged tree (de972965b): `./batch-check.sh --fast` exit 0 (compile,
  maps, Stale 0 firm, tree: only generated docs and these notes). Then, with
  `-Dcbs.parallel=1`, the eleven harnesses, ObservationCheck and the
  borrowers GovernmentViewCheck, BridgeCheck and LandmassCheck: "suite OK - 15
  harnesses", exit 0, no OBSERVED line in PolicyPreviewCheck or SaveFileCheck,
  all four FS17 known-reds lines OK. Seconds on this laptop: Landmass 251,
  Reload 151, SaveFile 69, NewGame 33, ReadPath 31, Bridge 24, MoneyDates 22,
  BasketYear 12, Gdp 11, PolicyPreview 9, History 9, GovernmentView 6,
  SkipReport 3, Calendar 2, Observation 1.
- `sh tools/pick.sh origin/main HEAD`: 58 of 232 (Observation.java brings its
  users), not ALL, so no dry run is owed; the review line: "not needed - no
  money, saves or threading in the diff". The gate runs the picked 58.
- The model and the trace do not move: no class outside the eleven harnesses
  and `Observation.HARNESSES` is changed (`git diff --stat origin/main` over
  `src/`), so no ensemble is owed.
