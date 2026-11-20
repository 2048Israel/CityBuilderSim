# FB13 - the island's words, the Government screen's coupons and the build card's licence line (2026-11-20)

The brief is `docs/queue/FB13.md` (AL1-W, CD9-G, R5-L; each row's own brief stands: AL1-notes, CD9-notes §3, R5-notes §2). Built on
main at 919c1fa41 (0.9.162), one Sonnet session, no slices; main 44e0aa664 merged in. Screens only in what the player sees; the model is
touched by reads that place nothing, so **the default trace does not move and no ensemble runs** (section 5, proved byte for byte).

## 1. Findings

- **R5-L stays in the bundle.** Licence fees exist since R2c and R2c-P (`LicenceFees`). The line is drawn from the reckoning the planners' running
  cost sums, `LicenceFees.rentOnNew()`, not from the building's own discovery alone (section 7 below, the review's P2-2).
- **AL1-W: the words were `LandmassView`'s**, pure functions of `Landmasses`, so `IslandChip` needed no change (it takes any number of
  lines); `LandScreen`, `MapView`, `Inbox` and `UserInterface` only pass what `BuildAdvice` says. The rule the builders read,
  `BuildAdvice.linkFor()`, cannot be what a screen reads: it places nothing but it writes (the bridge card's chosen landmass, restored; the
  survey that found no crossing, remembered 12 months; `Game`'s one-slot survey cache), so a screen that read it would make the model's timing
  depend on which screen was open. A fifth place named the port too: the not-enough-land page ("Only a sea terminal can be built there...
  Build a port there ›").
- **CD9-G:** the figure (`Game.getForeignInterestPaidThisMonth()`, "kept in LOCAL money") is the month's coupons in D$; the note "in
  dollars" dates from before CD8 and read as US$. The books keep no split of the month's coupons by money (`getInterestBookedOf()` is by
  kind), so the note says D$ and names the paper *held now*, in those words.

## 2. What was built

- **`BuildAdvice` (THE ISLAND LINK, AS THE SCREENS SAY IT).** `linkFor()` answers as before (`islandsToLink()` and `linkOf()` are split out so
  the screens run the very same code). `linkOf(.., screens)`: the rule writes its memory of a survey that found no crossing (`NO_CROSSING`,
  12 months, `linkSurveys`); a screen's read **reads the rule's fresh entry first** (so it says what the rule will order) and keeps a memory
  of its own (`SCREEN_NO_CROSSING`, `screenSurveys`) only where the rule has none, for the landmasses' version and the land's stamp it was
  made on (REV-FB13 P2-1: it was 12 months whatever the land did). `islandLink(game, id)` and `linkShown(game)` are the reads: the link for
  each island (a BRIDGE with no orders for an island a bridge is on site to: `Link.underWay()`), kept for the month, the version, the stamp
  and the terminals and bridges on each island, so a map that draws every frame reads once.
- **`LandmassView` (the words).** `needsWords/chipLines/chipWords/noticeBody/noticeButton/rows/noLandLines` each gain a form taking the link
  (the old forms pass null and are the old words, to the letter: LandmassCheck unchanged and green). A bridge is named first, the port after:
  "no: needs a Road Bridge (450 m) or a port here and one at home"; the chip has a third line, "or a port here ...", each line as short as the
  old second; the notice says "A bridge links Island 2 without a port: A Road Bridge (450 m) is allowed there (To Island 2: 450 m over water
  up to 10 m deep (mean 10 m))" before the port's sentence; its button is "Build a bridge there ›" (the bridge's card with the island chosen),
  "See the bridge ›" while one is being built. `Door` says where a button goes; `linkCard()` words the Build tab's card.
- **The Build tab** (`BuildScreen`): AN ISLAND TO LINK, between WHAT WOULD HELP MOST and AUTOMATIC BUILDING on the Overview, drawn only while
  the rule has an island to link: the island and its ground, what links it, the price and the wait of the rule's quote (the wait left out
  where the quote has none), a door to the card of the building (a bridge or the first terminal) with the island chosen for its next
  order. Where nothing can link the island the card says the rule's reason and has no door. `openBridgeCardFor()` is the notice's door; the
  not-enough-land page gains the bridge line and door.
- **`ForeignCouponView`** (new): the Government row's note, "in D$, at the month's rates · paper held now in US dollars, euros and yen" (the
  moneys by `DebtManager.hasForeignDebtIn()`, named by `ForeignAccounts.MONEY_PLURAL`), "in D$, at the month's rates" with no foreign paper.
- **`LicenceFees.rentOnNew()`** (new read; `one()` and `billOf()` gain an overload that hands out the codes they charge, their values untouched):
  what one more of a building pays a month (`forNew()` for a sector's building, `forNewWork(.., 1, 0)` for a city utility) and the patents
  among those the bill is made of that are due now, the soonest to end first. **`ResearchView.licence()`** is UNDER wherever one is due and
  otherwise the building's own discovery says it (HERE, OWN, FREE, ENDED); `licenceWords()` words it, on the card under the origin line:
  "Licence: a quarter of what it adds goes abroad until December 1920, about $18k a month." (amber; "on 3 patents, the first ending X and the
  last Y" where several are due), "Licence: none. Found by the Institute of technology; your builders pay no fee, ever." (violet), "none now"
  with the patent ended or found since, "none. It is not patented".
- `AllChecks` gains `FB13Check`; `docs/tree.md` the two new files.

## 3. The calls (★; each taken as recommended)

1. ★ **The screens read the island rule through the rule's own fresh memory, then a memory of their own keyed by ground**: a screen open must
   not decide when the rule surveys, and must say what the rule will order (FB13Check 5 causes both: the rule asked after a screen's read
   still surveys once and remembers; with the rule holding a refusal a month old the screens say terminals with it, and the bridge the month
   it surveys again). The cost: a no-crossing island is surveyed by the screens once per ground where the rule has no entry (up to 1.7 s,
   ET6, on the first read after a land change).
2. ★ **A bridge named first, the port after it**, both: a port links the island too. Where the rule would link by port or has no link, every
   word is the old one.
3. ★ **The chip grows a third line** rather than one long line (MT1/BK1-L: some 70 characters over a small island was the fault).
4. ★ **An island a bridge is on site to says so** ("a bridge is being built to it") instead of the port words: a `Link` of kind BRIDGE with
   no orders, which `linkFor()` never returns.
5. ★ **The Build tab's card is its own section**, not one of WHAT WOULD HELP MOST's cards: those are the city's needs ranked; the link is
   the rule's (AL1), worded as a card of its own with the price and wait of the quote.
6. ★ **The coupons' note says "held now"** (the books keep no split of the month's coupons by money): the moneys the paper on the books is
   written in, or D$ alone with none; a piece whose last coupon fell due in the month is not named.
7. ★ **The licence line is the planners' licence**, not the building's entry: UNDER from any patent due among the building's, its recipe's and
   its fitted pieces' (so a building known by a year with a patented piece has its line, and no card says none while `forNew()` charges),
   one date for one patent, "on N patents, the first ending X and the last Y" for several; five kinds (mockup 5 shows two). A city's own
   works (power, water) get the amount too, by `forNewWork(.., 1, 0)`, which is already a month's bill (FB13-U, filed first as a pending
   row, is not needed).

## 4. The review (REV-FB13: SHIP, no P1) and what the mends were

- **P2-1** the screens' memory and the rule's were separate and not keyed by land: for up to 11 months the card and the notice could name a
  bridge while `linkFor()` ordered terminals, or the reverse. Mended as the review said (the rule's fresh entry first, the screens' own only
  where it has none) and keyed the screens' own by the landmasses' version and the land's stamp (call 1; FB13Check 5's two new scenarios).
- **P2-2** the kind and date came from the template's own entry, the amount from `forNew()`, which also charges recipe and fitted-piece
  patents. Mended: `LicenceFees.rentOnNew()` hands out the patents the bill is made of (the codes `one()` and `billOf()` look at),
  `ResearchView.licence()` is built on it (call 7). FB13Check 7 holds, for every template of the 1900 town at its own month and at 1960's
  January (14 under patent, 8 ended, 4 not patented, 284 with no line; fees equal `forNew()`/`forNewWork()` to the bit, no template says none or
  has no line while the planners charge), a year-known building (Fabrication Shop) with a line from its pieces, and FREE from `licence()` itself
  on a real card (Walk-in Clinic).
- **P3**: (3) FB13-U withdrawn (above); (4) the coupons' note says "held now" (call 6); (5) FB13Check 8 holds as text the line of each screen
  that hands the model's link, note or licence to the words (LandScreen, MapView, UserInterface's label and its door, Inbox, BuildScreen's
  Overview card, no-land page and licence line, GovernmentScreen), so reverting one fails (a screen cannot be drawn without the toolkit); FREE
  is `licence()`'s own answer, not a record built by hand (the one hand-built record left holds the words for several patents, which no card of
  the 1900 town has at once).

## 5. The trace: byte for byte

The orchestrator asked the claim proved (BuildAdvice, LicenceFees and Inbox are model code): the default playtest, `LongPlaytest` with
`-Dplaytest.trace`, one JVM at a time, 2,400 months audited (2,405 played), the same data files named, on main 44e0aa664 (checked out beside
and compiled alone) and on this branch's final classes (32606d8a6, after the review's mends; GB2's merge since touches `tools/` only).
**All eight trace files are byte-identical** (`-month` 965,996 bytes, `-borrow` 2,205, `-house` 897,482, `-pop` 444,487, `-bank` 899,230,
`-bonds` 5,275,413, `-build` 698,136, `-labour` 781,494; `cmp`). The two reports differ in 4 lines only: the run's seconds (1528 against
1516), two log timestamps, and an object's identity hash in a printed `LandParcel@...`. No ensemble: the trace did not move. (A first branch
run on the classes before the mends was stopped for the mends; the figures above are of the final classes. Main's run took 1,582 s on this
laptop beside another session's trace and HS2's ensemble, the branch's 1,554 s.)

## 6. The checks

- **FB13Check** (new, 8 sections; the Rotterdam fixture of IslandLinkCheck and BridgeCheck, DebtMoneysCheck's town, ResearchViewCheck's
  `researchTown()`): green. The old words are put back by reverting: with `"in dollars"` back on the Government row the check fails on
  the screen's line (tried); each text is asserted against literal strings built from the model's constants and getters.
- **IslandLinkCheck** (before the mends): exactly its one known red (FS7, `docs/notes/known-reds.txt`), section 9's played run unmoved (6 months
  short of ground, one port link of 2 terminal orders, never linked), sections 1-8 green. **LandmassCheck**, **ResearchViewCheck**,
  **BuildCardCheck**: green (before the mends; the dry run runs them all again).
- **KeptCheck** (before the mends): sections 1-74 green on this laptop, then the harness's own 1740 s limit (`AllChecks.HARNESS_SECONDS`)
  with HS2's ensemble running beside it; sections 75-100 (97 draws the licence line) are the dry run's.
- **`./batch-check.sh --fast`**: green. Maps and docs regenerated and committed.

## 7. Left

- Nothing is queued by this batch. The eye check (not pending work): the Land tab's table and the map's chip on Jerus's Bergen city once it
  loads (a bridge-allowed island reads "needs a Road Bridge (… m) or a port here (…)", three lines on the chip); the Build tab's Overview
  with an island to link; a 1900 city's card of a patented building (amber line) and of what the city found itself (violet).

## 8. The dry run

`sh tools/gate.sh FB13 --dry-run`, on the gate box: **exit 0**, run `/home/jerus/cbs/runs/cbs-gate-FB13-UVGg54DG` (copied home as
`C:\Users\Jerus\AppData\Local\Temp\cbs-gate-FB13-UVGg54DG`; `ship.log`, `unnamed.txt` empty), of this branch at 40057bcc4 merged with main
e2f399d7c. The suite ran 229 harnesses in 801 s and judged "known red only": BasketYearCheck, CarCheck, CentralBankCheck, ChildcareCheck,
CreditCheck, CurveCheck, DeathRecordCheck, DenominationCheck, EssentialWorksCheck, ExchangeRateCheck, Forms1900Check, FundCheck, HoldersCheck,
IslandLinkCheck (its FS7 row), LabourCheck, PriceLevelCheck, RoadCheck, SaveFileCheck, SlopeCheck, YoungBankCheck - each a line of
`docs/notes/known-reds.txt`, none in a harness this batch touches but IslandLinkCheck's one named line. **KeptCheck passed whole** on the box
(sections 75-100, the NEW card with the licence line among them, which the laptop could not reach), and FB13Check, LandmassCheck,
ResearchViewCheck, BuildCardCheck, LicenceFeesCheck, PublicLicenceCheck and HistoricalLicenceCheck are green. After the dry run only these notes
and the ship file changed.
