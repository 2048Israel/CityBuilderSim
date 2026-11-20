# FB13 - the island's words, the Government screen's coupons and the build card's licence line (2026-11-20)

The brief is `docs/queue/FB13.md` (AL1-W, CD9-G, R5-L; each row's own brief stands: AL1-notes, CD9-notes §3, R5-notes §2). Built on
main at 919c1fa41 (0.9.162), one Sonnet session, no slices. Screens only: no money, save or month line is touched, so **the default
trace does not move and no ensemble runs** (section 5).

## 1. Findings

- **R5-L stays in the bundle.** Licence fees exist since R2c and R2c-P: `LicenceFees.payable()`, `TERM_MONTHS`, `SHARE`, `forNew()` (a sector's
  new building's monthly rent on its own estimate), `ResearchCatalog.Entry.fee()` and `worldMonth()`. The line is drawn from them.
- **AL1-W: the words were `LandmassView`'s**, pure functions of `Landmasses`, so `IslandChip` needed no change (it takes any number of
  lines); `LandScreen`, `MapView`, `Inbox` and `UserInterface` only pass what `BuildAdvice` says. The rule the builders read,
  `BuildAdvice.linkFor()`, cannot be what a screen reads: it places nothing but it writes (the bridge card's chosen landmass, restored; the
  survey that found no crossing, remembered 12 months and then waited on; `Game`'s one-slot survey cache), so a screen that read it would
  make the model's timing depend on which screen was open. A fifth place named the port too: the not-enough-land page ("Only a sea terminal
  can be built there... Build a port there ›").
- **CD9-G:** the figure (`Game.getForeignInterestPaidThisMonth()`, "kept in LOCAL money") is the month's coupons in D$; the note "in
  dollars" dates from before CD8 and read as US$. The model keeps no per-money split of the month's coupons (a flow; saving one is a model
  change, not a screen's), so the note names the moneys the paper on the books is written in.

## 2. What was built

- **`BuildAdvice` (THE ISLAND LINK, AS THE SCREENS SAY IT).** `linkFor()` is unchanged in what it answers (its island list and `linkOf()` are
  split out so the screens' read runs the very same code); `linkOf()` takes the memory of surveys that found no crossing as a parameter: the
  rule's `NO_CROSSING`, the screens' `SCREEN_NO_CROSSING`. `islandLink(game, id)` and `linkShown(game)` are the screens' reads: the link for
  each island (a BRIDGE with no orders for an island a bridge is on site to: `Link.underWay()`), kept for the month, the landmasses' version,
  the land's stamp and the terminals and bridges on each island, so a map that draws every frame reads once. `Link.bridge()` and
  `underWay()` are small getters.
- **`LandmassView` (the words).** `needsWords/chipLines/chipWords/noticeBody/noticeButton/rows/noLandLines` each gain a form taking the link
  (the old forms pass null and are the old words, to the letter: LandmassCheck unchanged and green). A bridge is named first, the port after:
  "no: needs a Road Bridge (450 m) or a port here and one at home"; the chip has a third line, "or a port here ...", each line as short as
  the old second; the notice says "A bridge links Island 2 without a port: A Road Bridge (450 m) is allowed there (To Island 2: 450 m over
  water up to 10 m deep (mean 10 m))" before the port's sentence; its button is "Build a bridge there ›" (the bridge's card with the island
  chosen), "See the bridge ›" while one is being built. `Door` says where a button goes; `linkCard()` words the Build tab's card.
- **The Build tab** (`BuildScreen`): AN ISLAND TO LINK, between WHAT WOULD HELP MOST and AUTOMATIC BUILDING on the Overview, drawn only while
  the rule has an island to link: the island and its ground, what links it, the price and the wait of the rule's quote, a door to the card
  of the building (a bridge or the first terminal), with the island chosen for its next order. Where nothing can link the island the card
  says the rule's reason and has no door. `openBridgeCardFor()` is the notice's door; the not-enough-land page gains the bridge line and door.
- **`ForeignCouponView`** (new): the Government row's note, "in D$, from coupons written in US dollars, euros and yen, at the month's rates"
  (the moneys by `DebtManager.hasForeignDebtIn()`, named by `ForeignAccounts.MONEY_PLURAL`), "in D$, at the month's rates" with no foreign
  paper.
- **`ResearchView`** (R5-L): `licence(game, template)` (HERE, OWN, FREE, UNDER, ENDED, from `LicenceFees.payable()`, the entry's `fee()`
  and `worldMonth() + TERM_MONTHS`; the amount `LicenceFees.forNew()`) and `licenceWords()`, the line under the origin on the card:
  "Licence: a quarter of what it adds goes abroad until December 1920, about $18k a month." (amber), "Licence: none. Found by the Institute of
  technology; your builders pay no fee, ever." (violet), "none now" with the patent ended or found since, "none. It is not patented".
- `AllChecks` gains `FB13Check`; `docs/tree.md` the two new files.

## 3. The calls (★; each taken as recommended)

1. ★ **The screens read the island rule with a memory of their own**, not `linkFor()`: a screen open must not decide when the rule surveys
   (FB13Check 5 causes it: the rule, asked after a screen's read of a no-crossing island, still surveys once and then remembers). The cost:
   a no-crossing island is surveyed once more by the screens (up to 1.7 s, ET6) on the first read after any land change.
2. ★ **A bridge named first, the port after it**, both: a port links the island too. Where the rule would link by port or has no link, every
   word is the old one.
3. ★ **The chip grows a third line** rather than one long line (MT1/BK1-L: some 70 characters over a small island was the fault).
4. ★ **An island a bridge is on site to says so** ("a bridge is being built to it") instead of the port words: a `Link` of kind BRIDGE with
   no orders, which `linkFor()` never returns.
5. ★ **The Build tab's card is its own section**, not one of WHAT WOULD HELP MOST's cards: those are the city's needs ranked; the link is
   the rule's (AL1), worded as a card of its own with the price and wait of the quote.
6. ★ **The coupons' note names the moneys on the books**, not the month's coupons by money (no such flow is kept): a piece whose last
   coupon fell due in the month is not named (the note then names what remains, or D$ alone).
7. ★ **The licence line has five kinds** (mockup 5 shows two): found here (none, ever), found here since the world had it (none now), not
   patented, under patent, ended. The amount is `LicenceFees.forNew()`, a sector's new building's monthly rent: a city's own works (power,
   water: R2c-P's `forNewWork()`, a licence over a life) get the date without an amount; pending as FB13-U.

## 4. The checks

- **FB13Check** (new, 60 assertions, seven sections; the Rotterdam fixture of IslandLinkCheck and BridgeCheck, DebtMoneysCheck's town,
  ResearchViewCheck's `researchTown()`): green. The old words are put back by reverting: with `"in dollars"` back on the Government row
  the check fails on the screen's line (tried); each text is asserted against literal strings built from the model's constants and getters.
- **IslandLinkCheck**: exactly its one known red (FS7, `docs/notes/known-reds.txt`), section 9's played run unmoved (6 months short of ground,
  one port link of 2 terminal orders, never linked), sections 1-8 green. **LandmassCheck**, **ResearchViewCheck**, **BuildCardCheck**: green.
- **KeptCheck** (the screens: the Overview now draws `linkSection()`, the card `licenceLine()`, the Government detail the new note): sections
  1-74 green on this laptop (802 and more OKs, no FAIL), then the harness's own 1740 s limit (`AllChecks.HARNESS_SECONDS`) was reached
  at section 74 with HS2's ensemble running beside it, so sections 75-100 (97, the NEW build card of the 1900 town, is the one that draws
  the licence line) are left to the dry run on the gate box. Section 97's assertions are "the kept page equals a page built new" and the
  origin line's words, both unmoved by a deterministic line of the model's.
- **`./batch-check.sh --fast`**: green (compile, maps regenerated, Stale 0 firm findings). Maps and docs regenerated and committed.

## 5. The trace

Screens only, and the model's reads are nothing where nothing is unlinked: `BuildAdvice.islandLink()` returns at once for a city with no
unlinked landmass (every generated world, which the default playtest founds, and every city on one landmass), so the default trace does
not move and **no ensemble runs**. Where an island is unlinked the new reads (the notice's, a screen's) place nothing, buy nothing, and
restore the one thing the rule touches (the bridge card's chosen landmass); IslandLinkCheck section 9's played Earth run, which has the
notice taken every month, gives the figures the known red's row records (6 months short of ground, one port link of 2 terminal orders, never
linked in 240) - the same as main's. The Inbox's body for an island is the one text the saved state carries that differs (a bridge's
sentence), and only where a bridge is allowed.

## 6. For the reviewer

- `BuildAdvice` gains a `static volatile` cache (`shownKept`): the word is the review line's "threading", and nothing runs concurrently - every
  read is the FX thread's or the month's, one thread - the volatile only publishes an immutable record safely if a harness thread ever reads.
- `linkOf()`'s signature gained the memory parameter; `linkFor()` passes `NO_CROSSING`, so the rule's own behaviour is untouched
  (IslandLinkCheck 1-8 green, `linkSurveys` counted only for the rule's memory).
- A screen's read of an island whose crossing survey finds none costs up to a survey (1.7 s, ET6) once per land change (calls, 1).

## 7. Left

- FB13-U (the ship file's `## pending`): the licence line has no monthly amount for a city's own works.
- Nothing is queued by this batch beyond it; the eye check (not pending work): the Land tab's table and the map's chip on Jerus's Bergen
  city once it loads (a bridge-allowed island reads "needs a Road Bridge (… m) or a port here (…)", three lines on the chip); the Build tab's
  Overview with an island to link; a 1900 city's card of a patented building (amber line) and of what the city found itself (violet).
