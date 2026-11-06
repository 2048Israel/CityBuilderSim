# R3 - research: posts and discoveries (0.9.28, 2026-11-06)

## WHERE IT STANDS

**Ready.** Branch `batch/R3` from `batch/XI1`'s ship file (RL1); main with
XI1 in (892ef16a) merged at b28039e5, and with KP6 (6441cac5) at 6c21782e.
Slice A is d7178a03, slice B c439d261. The default trace is main's to the
byte, so no ensemble. `--pick` on the merge: **exit 3**, main's recorded
reds only; the full suite 174 of 180 green, its six reds main's, none NEW
(§4). R3b, the screen, in the ship file's `## queued` (§1).

## 1. What R3 is, and where it splits

The brief is research-design §9 item 4: "research posts at schools, the
grant dials, open problems, piles, hidden sizes, the inbox notices, Policy ›
Research", with §9's harnesses and §5.1-5.2's rules, as research-1900 §12
item 6 keeps them for a city founded in 1900.

By `docs/batch.md` 3 that is two batches, the model and its screen:

| part | what | why there |
|---|---|---|
| **R3, this batch** | **the mechanism**: the four grant dials; posts struck at the top of the month from the grants at the post's cost (§1.8) within the schools' room, and hired as jobs in the labour market; their pay and the labs' other costs paid by the treasury as a line of their own; the faculty's 40% and the filled posts spread evenly over each field's open problems, piles, hidden sizes, a find here, history's arrivals; a find known in the city (recipes, equipment, buildings); a law find opening its Policy lever early; the inbox's notices; the save | the state and the money R3b and R4 read |
| R3b | **Policy › Research** (§5.5): the Overview and Funding pages, the eight dial cards (four until R4's prizes), each card's preview as a chance or an expected count of finds over ten years from the sizes' distribution and never their draws (§6.2), and §9's harness "the young and big cities of §5.4 reproduce the previews within their Monte Carlo error"; each school's page's posts line | a screen, in the kept way (`Kept.scope`), on Policy as KP6 (0.9.27) keeps it; the screens lane held Policy while R3 was written |

## 2. The calls (★)

1. **★ The split above.** Recommended and taken. Nothing R3 adds is seen by
   a player until a city can be founded the 1900 way (R1d), so the screen
   loses nothing by waiting; the previews are the screen's (they are what a
   dial card prints) and go with it. R3b is best run beside R5 (the other
   research screens) and before R1d, which lets a player found the city it
   is for; until R3b only a harness sets a grant.
2. **★ Research runs only in a city founded the 1900 way** (R2 ★3, R1c ★1).
   In a city founded the default way every discovery is known from the
   start, so nothing is open, nothing is found and no notice is raised; a
   grant there still buys posts, which are hired and paid, and every
   researcher idles (the screen will say so). With every grant at 0, the
   default, nothing in any city moves.
3. **★ The research bill is a promise** (`TreasuryLine.RESEARCH`), as the
   schools' bill is: its posts' wages are already in the households' pay
   (`PopulationManager.getTotalWage()` counts every job filled), so a refused
   bill would leave them paid by nobody.
4. **★ A post is its jobs**: a researcher, half from each of the field's two
   university types, and the support §1.8 prices (0.43 college staff a
   post in engineering and medicine, 0.20 diploma staff in business and law:
   the study's "support at 4.5k" is the diploma tier's wage). The labs'
   other costs (38-40% in engineering and medicine, 20% in business and law)
   are the bill less the payroll, paid out of the city like the schools'
   upkeep.
5. **★ The faculty research from the staff a school has, the room from the
   staff it is built for.** A school short of professors researches less;
   its room for posts is its size (2.7 a post per university-band staff
   member, §5.1).
6. **★ Posts are struck at the top of the month at last month's wages** and
   paid at this month's, times the month's fill: "posts never exceed the
   room or the money" holds at the strike, and a post nobody fills costs
   nothing.
7. **★ A drug dated before 1962 takes 2 years at least**, not 8
   (research-1900 Q6, decided 2026-10-18).

## 3. Slices

| slice | what | state |
|---|---|---|
| A | `Research` (the dials, the post's cost, the schools' staff by field, faculty and room, posts and their jobs, payroll and bill, the even spread, open problems, hidden sizes, the month's step, the save's getters and `restore()`), the seam (`knownAt(month, Research)` on Recipe, Equipment and BuildingsTemplate, through `Recipes`, `EquipmentFit` and `Game.fit()`), `DiscoveryCheck` 1-11 | d7178a03 |
| B | the month (posts struck, jobs, the bill and the work beside the schools, a law find's lever), the money (EconomyManager, `TreasuryLine.RESEARCH`, MoneyAudit, the national accounts, redenomination), the inbox, the save; `DiscoveryCheck` 12-18; `CityNeeds.hires()` counts the posts | c439d261 |

Both drafted by Sonnet 5.5 at xhigh, reviewed here. What the reviews found:

- **Slice A.** The step mirrors `research_model.py` (the world's dates
  first, then each field's open problems, the water fill, piles over twelve);
  the hidden size matches the model's draw to 1e-12. Every old signature of
  the seam stays as an overload that reads history's, so no harness moved.
- **Slice B.** The bill follows the schools' everywhere they go
  (`getExpenses()`, `finalUpdateEconomy()`, the audit's two lines, government
  consumption, the national accounts' spending, saved at its block's next
  slot, so a 38-slot save reads 0). A zero bill pays nothing
  (`treasuryPays()` returns at once), so a city with no grant writes no
  line. The slice left `CityNeeds.hires()` reading the buildings' jobs alone;
  mended here, so the school planner counts a post a grant bought.
- **A law find opens its lever the month after the find**, not in it
  (`bringForward(lever, month + 1)`): the policy has held the lever shut at
  the top of the find's month, and an opening inside it would leave it open
  with its rate at zero, the founding rate then overwriting one the player
  set in between (DiscoveryCheck 16).
- **The month's bill is saved with the research**, as transit's is: a load
  would otherwise strike it at the fill the month ended on.
- **A 1900 city with a school and no grant writes a research key**: its
  faculty's 40% piles work. Only a city with no school hosting research, or
  one founded the default way with no grant, writes none.
- **After a load the month's researchers, idle and open counts read 0**
  until the next month (they are the month's, not saved): for R3b's screen.

## 4. Checks

- **The trace.** The default playtest (2,405 months) traced on main
  (892ef16a, built apart) and on this branch (c439d261), back to back: all
  eight CSVs byte-identical, the log but for "in 56s"/"in 52s" and the
  layout windows' "1.9 s"/"1.8 s". So no ensemble (`docs/batch.md` 6): with
  every grant at 0 and the default founding, R3 moves nothing.
- **`--pick` on the merge with main** (XI1 in, 892ef16a; merged at
  b28039e5): 89 of 180 harnesses for 19 files, BuildingsTemplate, Game and
  MoneyAudit left to the gate with LongPlaytest in their place: **exit 3**,
  main's recorded reds only, word for word (SlopeCheck, DenominationCheck,
  ScaleCheck, MapCheck); timing SOFT. `DiscoveryCheck`: 267 ok. Again on
  the merge with KP6 (6c21782e), 90 harnesses: exit 3, the same four and
  KeptCheck, all on main's record.
- **The full suite** (`AllChecks -f`, timing soft, on 52c3d514): 180
  harnesses, 174 green; the six reds are main's recorded ones, every line
  `[known]` and none `[NEW]` (SlopeCheck, MonetaryCheck, DenominationCheck,
  ScaleCheck, MapCheck, KeptCheck).
- Not a process batch: no scripts, checks' machinery or rules changed.

## 5. For the next batches

- **R2b** gates the planners by `Game.knows(BuildingsTemplate)`, which reads
  the city's finds; nothing asks it yet.
- **R4** (prizes and the Research Lab) adds four prize dials beside the
  grants and the labs' researchers to `Research.month()`'s per-field count.
- **R2f** maps a medical find (a drug) to what it moves; until then a drug
  found here is recorded and moves nothing.
- The Sector page asks `knownIn(year)`, history's reading (R2 ★8): a piece
  found here shows as unknown there until R5's screens take the month and
  the city's research.
