# R3b - research: Policy › Research's Overview and Funding (0.9.36, 2026-11-06)

## WHERE IT STANDS

**Ready.** Branch `batch/R3b` from `batch/R5`'s ship file (RL1, 7f76eb97); main with R5 in (a3d8e66c, 0.9.33; tip
90c56f75) merged at 5fb9970f, the generated docs alone in conflict (main's side, regenerated). Slice A1 is b6937637,
A2 4ba8ef6d, B 74ae8eb8, the review's mend of B 56698350. The default trace is R5's to the byte, so no ensemble. The
full suite on the merge: **exit 3**, main's recorded reds only (§4). Pending's R4-R (the prize cards, the purse in Government's list, the labs'
build cards) is done: the cards here, the purse in Spending's Subsidies line (§1), the build cards R5's (★4 there).

## 1. What R3b is

R3's ★1 split research-design §9 item 4 into the mechanism (R3) and its screen (R3b): Policy › Research's Overview and
Funding pages (§5.5), the dial cards with each card's preview of the finds here over ten years from the sizes'
distribution and never their draw (§6.2), §9's harness that the young and big cities of §5.4 reproduce the previews,
and each school's posts line. R4 put a prize dial beside each grant (R4-R: the four fields' cards with both dials,
mockup 1); R5 opened the area with Found and left Overview and Funding in front of it. The row also carried the gate
reviews' items of R3, R4 and R5 (§3).

| slice | what | state |
|---|---|---|
| A1 | `ResearchPreview` (Research.month() on arrays over 400 draws of the hidden sizes, each conditioned on its pile, the labs by the game's own chase), `ResearchView`'s Overview and Funding reads, the cached preview and its words, R5's gate-review reads; `ResearchPreviewCheck` 1-9, `ResearchViewCheck` 11-15; `docs/studies/research-model/previews.py`, the reference run | b6937637 |
| A2 | the gate reviews' items in the model and the save (§3); DiscoveryCheck 19-20, PrizeCheck 18-20 | 4ba8ef6d |
| B | `ui/ResearchScreen`'s Overview and Funding, the hub's sixth card, each school's posts line, Government's research line and payroll, Policy's payroll sums; `ResearchView.Card`, the dials' ranges, the budget's effect, a school's posts; KeptCheck 99-101, ResearchViewCheck 16-19 | 74ae8eb8 |

All drafted by Sonnet 5.5 at xhigh, reviewed here. What the reviews found:

- **A1.** The preview is `Research.month()` written again on arrays (the model's own step, on maps and strings, is a few
  hundred times too slow for a card's two runs a draw): the world's dates, the fields in order with a find counting at
  once for the later ones, the water fill, the chase and the labs from last month's city shares, as `labsWanted()`
  reads them. A size is drawn from the lognormal above its pile (a tail sampler where the pile is far past the work);
  the seed is fixed and never the city's, and an as-is and a staged run share the draws, so their difference is not
  noise. The cache's key is everything the answer reads (the month, piles, finds, city shares, dials, wages). Two
  javadoc "he"s for the player mended here.
- **A2** ran in a worktree of its own, cut from main (a6d07279) and not from R5's tip; every hunk applied here cleanly
  and its harnesses were run again on this tree, green. RealEstate now reads `Game.getJobs()`, which is the live
  buildings' array with the posts added: the default city's figure to the bit (the trace, §4). Item 5 needed no
  change (★4). A stale "struck at the top" in Game's research banner mended here.
- **B.** Every figure on the two pages comes from `ResearchView` (a `Card` holds a field's now and after); the card's
  sentence picks among the model's cases with no arithmetic. The dials stage through Policy's set and tray (keys
  `researchGrant:FIELD`, `researchPrize:FIELD`), the tray's budget effect the staged posts' bill, and Apply logs a
  PROMISE, as R3 ★3 files the research bill. A second draw in the same month writes nothing on any new page; to get
  Government's Spending there B mended two writes of its own (the list total's style set twice, a `new Insets` in its
  `frameOver`), KP2c's class. Found keeps Policy's four figures (KeptCheck 95 holds its default page).
- **Mended here (56698350):** B put the prizes paid to the labs in a row under Spending's total, its (i) saying the total
  does not count them; the books strike them in Subsidies (`strikeGovernmentBooks()`: `getTotalSubsidyPaid() +
  prizePaid`, R4 ★5), so the row showed them twice. The row is gone, the Subsidies line opens on the sectors kept going
  and the research prizes in a month one was paid, and KeptCheck 101 holds the line to the two.

## 2. The calls (★)

1. **★ The prizes' previews follow the game's chase, not the study's one problem.** The study's figures put the labs
   on one problem by hand (figures.py: 200 lab researchers on hydrocracking, 130 on keyhole surgery). The game's rule
   (R4: `labsFor()` whole labs, poured cheapest first on every open problem the purse covers that a team at its limit
   could finish before the world) does otherwise in both of §5.4's cities: the young city's D$ 140M engineering prize
   covers semi-dwarf wheat (E10, its science just in reach) as well as hydrocracking, draws 4 labs (room 395) and
   expects 2.74 engineering finds over ten years with a chance of about 100%, not "about 75%"; the big city's D$ 350M
   medical prize draws 4 labs (room 363 on keyhole surgery), 1.51 medical finds, not 1.25. A card must say what the
   game will do, so the preview plays the game's rule; ResearchPreviewCheck holds both against the study's model run
   by that rule (`previews.py`). Recommended as built. R4's notes (★8: "the young city's D$ 140M engineering prize
   still draws two labs, the big city's medical prize one") do not hold at the study's state.
2. **★ A lab the investors want is there the month they want it**: the preview has no building time and no shedding
   lag (six idle months), as the study's model had none. It flatters a prize by a few months of work. Recommended as
   built; the alternative is the investors' pipeline played inside every draw.
3. **★ A chance or a count**: a card shows the chance of at least one find here while the larger of its now and after
   expected counts is under 1, the expected number from 1 up (`ResearchView.asChance()`), so one card never mixes the
   two; the mockup's big city showed business's 0.05 → 0.18 as counts, which this shows as 5% → 16%. Recommended.
4. **★ The Exchange's buyback and the labs' stake** (R4's review asked whether the buyback hands the stake back before
   the find): it does not in any played 1900 city - Business Services is never a buyer there (its regime BAD every
   month of a 360-month run, its equity 9-47% against a 70% target, its till under the cushion), PrizeCheck 20. A
   Business Services that qualified and held a stake past its six months' cushion would be bought back; nothing
   reaches that. Recommended: no change and nothing queued; a `reserved()` on `Exchange.Companies` is the mend if a
   later balance makes Business Services a buyer.

## 3. The gate reviews' items

| item | what was found | done |
|---|---|---|
| R3: posts before `closeDemolished()` | a school ordered down gave a month's posts from its room | struck after it (DiscoveryCheck 19) |
| R3: Government's list, PolicyPreview's wage cost, GovernmentScreen's payroll, `latentHeadShortfall()` | the research line and the posts left out | `PolicyPreview.cityPayroll()`; RealEstate reads `Game.getJobs()`, as `plan()` does (DiscoveryCheck 20); the screens: B |
| R3: the month's counts unsaved | researchers, idle and open read 0 after a load | saved with the research record, the labs' two as well (PrizeCheck 18) |
| R4: `payPrizes()` inside the isFinite block | a find's labs' share neither paid nor owed in a month whose books did not close | owed as arrears on the prizes' line (`owePrizes()`; PrizeCheck 19) |
| R4: the buyback and the labs' stake | not reached in a played city | ★4, PrizeCheck 20 |
| R5: figures worked out in ResearchScreen | the series on History's months, still to come, the bars' shares | `ResearchView.cumulativeOn()`, `stillToCome()`, `share()`; the screen reads them (B) |
| R3b's gate review: the default card's words | a default city with a University and no grant: "N researchers hold the posts a grant bought ... D$ 0 a month" of its faculty | posts claimed only where some are held (`Overview.posts()`), else the schools' own staff, costing nothing (cec4a07b; KeptCheck 99) |
| R3b's gate review: the counts' save | saved only while the research was not empty, so that city's faculty read 0 after a load | the record written whenever the month has counts too (`Research.hasMonthCounts()`; ResearchViewCheck 20) |

## 4. Checks

- **§9's harness** (ResearchPreviewCheck, 20,000 draws a figure, 94 ok): every one of §5.4's figures within three
  standard errors of the study's 1,000-draw run. The study's medicine figures are all two or three errors under the
  value its model converges to - each of its scenarios drew from the same seed - so the young city's medical grant is
  30% rather than "25%" (the study's own model at 5,000 draws on two other seeds: 29.2% and 30.3%), and the big city's
  medicine 0.85 rather than 0.81. A port of `forward()` line for line, run at 400,000 draws, agrees with the preview
  at 1,000,000 to 0.001 of a find, held at four errors over twelve comparisons. With the sizes the city's own draw
  gives, the preview's path finds what `Research.month()` finds in the same months (6 cases × 30 seeds, 688 finds),
  with and without a prize. 400 draws take 3-8 ms.
- **The catalogue** is the study's for every entry §5.4 reads but M1's needs (PM6, known by 1955).
- **The trace.** The default playtest (2,405 months) on R5's tip (7f76eb97, built apart) and on A2 (4ba8ef6d), back to
  back: all eight CSVs byte-identical, the log but for "in 54s"/"in 57s". No ensemble (`docs/batch.md` 6).
- **The save** (`DataSave.ResearchRecord`): five maps by field name. Since the gate's review the record is written
  while the research has anything of its own or the month has counts, so a default city with a school now writes one
  (its counts alone) where it wrote none. No format bump: a save without them reads them empty.
- **`--pick` on the review's mends** (cec4a07b, main 0.9.34 in, 28 of 183 harnesses with KeptCheck and ResearchViewCheck):
  exit 4, its one NEW red ResearchViewCheck 20's own fixture (a village whose new University had no staff two months
  in, so its faculty were 0); the fixture now starts from KeptCheck 99's default town with its grant taken away, and
  ResearchViewCheck alone is 217 ok. Everything else picked, KeptCheck's 99-101 among it, was main's recorded reds only.
- **The trace again after the review's mends**: the default playtest on main 0.9.34 (437f3648) and on cec4a07b, built
  apart, back to back: all eight CSVs byte-identical, the log but for "in 59s"/"in 75s" and the layout windows' time.
- **The screens** (KeptCheck 99-101, 98 until UP7 took it: 44, 75 and 21 ok; ResearchViewCheck 209 ok): each new page drawn kept over last
  month's nodes is the page built new, month after month, in the 1900 town and a default city, with a dial staged and
  a month landing under a held thumb; a second draw in the month writes 0 (section 92's rule) on Overview, Funding,
  the hub, Education, Spending and the wage floor.
- **`--pick` on R5's tip + R3b** (74ae8eb8): 28 of 183 harnesses for 61 files, Game left to the gate with LongPlaytest
  in its place: **exit 3**, main's recorded reds only (MapCheck, KeptCheck's section 49 line); timing SOFT. The review
  lines: the save (above) and the threading, a `synchronized` preview cache and an `AtomicInteger` run counter, both
  for a cache the FX thread and a harness's threads may share. Under load B once saw ReplayCheck red; it was green
  alone and in this run.
- **The full suite on the merge** (`./batch-check.sh`, timing soft, on 5fb9970f, 183 harnesses): **exit 3**, the six reds main's recorded ones and none NEW (SlopeCheck, MonetaryCheck,
  DenominationCheck, ScaleCheck, MapCheck, KeptCheck); timing SOFT. (A first run on the tree before the merge was
  stopped when R5 landed, its processes ended with it, so that this one ran on the merge.)
- Not a process batch: no scripts, checks' machinery or rules changed.

## 5. What to look at (by eye, if an issue turns up)

On `ResearchViewCheck.researchTown()` with a grant and a prize set: Funding's cards are 540 px, two across from about
1,130 px of page and one column below it; the grant dial's room mark and the prize dial's "labs come" mark if they
land near the thumb; Overview's rows under 1,000 px, where the reach words squeeze; the hub's six cards in a row; the
University's card on Services › Education, whose research line runs to three or four lines at 228 px. On a default
city: Research's Overview, one quiet card, and Funding's four cards saying there is nothing to find.
