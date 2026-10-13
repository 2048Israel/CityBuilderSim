# I13 notes - cards and pages for the materials buildings (0.7.142)

Worktree /home/claude/git/wt-I13, branch batch/I13, base c2cee14 (0.7.140). Scratch /home/claude/scratch-i13.
Steps: a5b458e (the model, the screens, the harness), e07efa2 (words, the payback by type), 91032da (version, rows,
queue) - the gated final state (section 5).

## 1. What I built
- **BuildCard, THE MATERIALS' CARDS** (pure, every figure a model read): `materials()` (owner a ProcessingSector or an
  ExtractionSector: the nine industries, 40 buildings); `Stands stands()` (resource, type, sites one takes, the city's
  sites by type, Game.sitesFor/committedFor/remainingFor, Game.hasDepositFor, ExtractionSector.fieldWanting(), and
  what one more makes - Quarrying/CoalMining.madeBy()); `Weighed weighed()` (a works = ProcessingSector.appraise() to
  the bit; a mine/quarry/washing plant/crew = its plan's estimate on getCostOf + fleetCapital through
  SpreadPlanner.judge() with its feed = field, E or raw coal; vans, fleet, the money gate's hurdle, first gate + words);
  `Power power()` (gen, draw, bought, sold, sold x tariff); `OldMetalBought oldMetal()` (Scrap Yard / Scrap Copper
  Refinery: intake, price, arisen, the yards' take, spare); `buyers()` / `Outlet outlets()` (sectors whose buildings
  use the good, captive first, or it ships); `CrewRule crewRule()` (E: cuttable, committed, cut, months, room, more,
  carries); `chainLines()` (a page's power + WHO BUYS IT HERE). Investors and Appraisal carry `planned` (the Weighed);
  `Investors.showPlanned()`. ORE_WORDS + "field", "fields", "worked out".
- **ui/BuildScreen, THE MATERIALS' LINES**: under the investors' line, muted lines - where it stands ("stands on 20
  sites of one hematite field · the city's: 4 hematite, 23 magnetite, 3 of its kind spoken for · 161.7M t of hematite
  ore left · fits", red "does not fit"; a quarry: "one more on limestone makes 40,000 t of limestone a month, not
  aggregate"), how it is weighed ("its whole cost $529.8M, its ground and 2,538 lorries ($173.3M) with it · it would
  lose $2.2M a month on it · the money gate asks $3.0M"), its power ("makes 60,000 kW of its own power: its 51,400 kW
  draw, and 8,600 kW sold to the grid, $86k a month at the tariff"), old metal ("buys the city's old steel first:
  10,500 t a month at $210.50 a tonne, 70% of scrap's export price · ..."), where it goes ("sulphuric acid to Copper
  Mining, ...", "nobody here buys panels: it ships", "residues to its own works first"), a crew's E. The investors'
  "this one:" is the planner's refusal (`showPlanned`), consider()'s gate only where the planner passes. The hero's
  "needs a deposit" line is left to the stands line for typed sites. A run's NO_DEPOSIT names the resource.
- **ExtractionSector (words)**: `noField()` by resource and type ("no coking coal left in the city's fields for an
  Underground Coal Mine", "no thermal coal field with 4 free sites for a Large Surface Mine"); public `oreWords()`,
  `fieldWords()`, `fieldWanting()`; the plan's "the coal is worked out"; the page's note names its resource.
- **ownLines**: the nine end on `chainLines()` (Mining gets one; Heavy Industry gets its old steel, as Copper Refining
  has its copper; Cement & Concrete and Wood Products get one; Coal, Quarrying, Copper Mining add the chain also when
  nothing stands); Forestry adds "Room under E for more Logging Camps: N more; it carries M".
- **CityLand**: `fieldsOnByType()` (fieldsOn()'s twin, fieldsOn untouched), `sitesOnByType(LandParcel, r)`,
  `fieldSitesOn(LandParcel, r)` (field by field). **LandScreen**: the row's and the map card's hover add
  " - 206 hematite, 18 magnetite". **ZoningScreen** (outside): THE CREW RULE (E) card on the forest law page.
- **BuildAdvice**: `fieldMines()` and `fieldEarnings(Game, LandParcel)` - each field filled by the iron mines of its
  type in score order, counted as fieldEarnings() counts; `ironPayback()` reads it. LongPlaytest's iron rule too.
- **BuildCardCheck 12** (a-h, 33 assertions) and 8 (three field words). Headers already said 139 at the base.

## 2. Departures
- The money-gate fixture's Pelletising Plant fails at a loss in the town ("would lose $2,344.6k a month on it"), the
  money gate's first refusal; the prime-raised branch for "earns under the bar" is written but does not fire there.
- The Forestry zone's E line went on Policy › Zoning › The forest law (board 3's line), ZoningScreen being outside
  my files; FOREST_INFO's "the crews come later" made true.

## 3. Premises changed
- MaterialsCheck 8: "the ore is worked out" -> "the iron ore is worked out" (the word renamed; the premise the same).
- None moved otherwise. New: BuildCardCheck 12, 8's three examples.

## 4. Trace movement
None expected: nothing in the model reads the new code but the words (no trace carries a mine's word) and the test
player's iron rule, which neither run reaches ("found none listed" 208 and 209 times at the references).

## 5. The gate (21:44-22:26, final commit 91032da, the tree clean)
Both builds silent; suite 107/107 in 969 s (HealthCheck and MapCheck green); BuildMenuCheck PASS, 139 of 139;
tools.Stale 0 firm, 467 soft; playtests byte-identical, default 8/8 against pt07140 and Earth 8/8 against pte07140
(the iron rule still "found none listed" 208 and 209 times); no w/crlf but the .bat files.

## 6. ★ calls
- ★1 (in place of the brief's "fix only if it does not move the playtest"): built, as asked - the playtests cannot
  move (the rule is never reached); the int fieldEarnings() stays for LandCheck's premise; kinds staffed per kind,
  not jointly (an approximation; one kind, as on every hematite offer under 20 sites, is the old figure to the bit).
- ★2 One gate on the card: the planner's first refusal, in its words, in place of consider()'s (which asks after it);
  consider()'s still shows where the planner passes (the licence above all).
- ★3 A mine's, a crew's and the washing plant's gates through SpreadPlanner.judge(): FEED (field / E / raw coal),
  LAND, STAFF, MONEY - the extraction plans ask field, staff, money, then land, so the card's first can be LAND where
  the plan's word is the staff; both refuse. NOTHING_TO_WASH is new words (planWash() has no refusal of its own).
- ★4 E's refusal words copied into BuildCard.eWords() (Forestry.eWords() is package-private, Forestry outside my
  files but ownLines); 12f holds the copy to the plan's word, to the letter.
- ★5 "Who buys it here" is the sectors whose catalogue buildings use the good (no city building uses goods;
  households' basket holds no materials good) - "none standing" when none does.
- ★6 The stands line says "does not fit" in red and leaves the why to the investors' "this one:" (the plan's words),
  so the card says it once; the line wrapped to four lines at 300 px otherwise.

## 7. For the next batch
- I14: the map's pits by sites can read BuildCard.stands()/Game.claimsOn(); fieldSitesOn() gives an offer's fields.
- The land office's Ore card ("ore that nothing is digging", the mines count) is still iron's alone (I6's note), and
  the landlords' card does not ask the mortgage lender (C1's note): QUEUE.md Pending I13-L.
- SectorScreen's investor report (R4) can show Appraisal.planned() (the planner's view) - not drawn here.
- Docs pass: maps (BuildCard, BuildScreen, CityLand, BuildAdvice, ExtractionSector, the nine), dials (NOTHING_TO_WASH),
  harnesses.md (BuildCardCheck 12); ExtractionSector's header "THE PAGE" line.
- scratch-i13 is 520 MB (the suite's harness folders in tmp; no rm here): clear it when convenient.

## 8. For Jerus to check on the PC (1,389 x 868)
1. Build › Industry: an Iron & Steel card (Blast Furnace) and a Mining card (Large Open-Pit Mine) - the lines under
   the investors' line, their wrap at the card's 300 px, the red "does not fit".
2. The land office: hover an offer holding iron or stone - "- 206 hematite, 18 magnetite" on its deposit line.
3. Policy › Zoning › The forest law: THE CREW RULE (E) card under the law's dials.
4. Sectors › Wood Products and Iron & Steel: the closing "Its own power" and "Who buys it here" blocks.

## 9. Outside my files
ui/ZoningScreen (the E card, FOREST_INFO), LongPlaytest (one call: the iron rule on BuildAdvice.fieldEarnings(g,
field)), MaterialsCheck (one word), QUEUE.md (row out, I13-L under Pending). Sector classes: only ownLines (new
methods placed mid-file: before the constructor in CementConcrete/WoodProducts, after oldSteelPrice in HeavyIndustry,
before dig() in Mining) - FQNs, no import added.
