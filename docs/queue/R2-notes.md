# R2 - research: the catalogue and the world's dates ({next}, 2026-11-05)

## WHERE IT STANDS

(filled at the end)

## 1. What R2 is, and where it splits

The brief is research-design §9 item 3 ("the 40 entries as data
(`research.json`), history's dates, licence fees on the current account, the
start's figures derived by undoing each entry, 1900-1950's included") with
research-1900 §12 item 5 (89 entries: §8's 48, the first study's 40, and L5),
and what R1, R1b and R1c left it: a `since` that is a catalogue code (the
tractor's PE14, the oxygen converter's E1) is "never known until R2's catalogue
reads it" (R1-notes ★7), and recipes composed as changes (R1c ★4, ★8).

By `docs/batch.md` 3 that is several batches, as R1 was four. Only the first
part is the state the rest read; the others each touch a money path, every
planner or a row's own sourcing:

| part | what | why there |
|---|---|---|
| **R2, this batch** | **the catalogue as data and history's dates**: `research.json`'s 89 entries (field, kind, history's month, what each needs, whether it carries a fee, its work), `ResearchCatalog`, and every `since` code in `buildings.json` resolved to its entry's month in a city founded the 1900 way: the tractor known from 1917, the oxygen converter's recipe from November 1952, each building research-1900 §1.2 says did not exist dated by the entry that opens it | the data R2b-R3 read; nothing moves in a city founded the default way |
| R2b | **buildings on history's dates**: in a city founded the 1900 way, a template not yet known is no candidate for any planner (the processing sectors' spread, the extraction and retail planners, `BusinessInvestment`, the city's own works, the berths) and its order is refused | every sector's candidate list (`getTemplatesBySector()`, about 40 places), and the refinery's conversion units, which a 1900 refinery lacks |
| R2c | **licence fees on the current account** (research-design §2.4, ★E2): 25% of what a patented entry the city got from the world adds, for 20 years from history's date, paid by the firm that runs it and leaving the city as IP charges; the planners' and the fitting's gain less the fee | a money path through the sectors, the audit and the balance of payments |
| R2d | **the 1900-1950 entries undone**: each P row's 1900 recipe or piece (research-1900 §8.5) on its templates, so a city with every entry runs today's figures (§3.1, ★E3); recipes written as changes, so two pieces compose on one template (R1c ★4, ★8: the binder beside the tractor) | each row is its own sourcing, as R1c's nine were |
| R2e | **the 1951-2000 entries undone** (research-design §3.1-3.2), the engineering and business rows; the medical rows (M, PM) to a care batch, as R1c-R left the X-ray and the antitoxin | the same, and medicine changes `Health`'s mechanisms, not a building's figures |

## 2. The calls (★)

1. **★ The split above.** Recommended and taken. R2 is the state the rest
   read and moves nothing in a played city; R2b and R2c each reach every
   sector's planner (and R2c the money), and each undoing is its own
   sourcing, so they go in that order, R2b and R2c before R3 (whose own
   discoveries carry no fee) and R2d and R2e before R1d judges a 1900 city.
   The medical rows (M, PM) go to a care batch, R2f, with R1c-R's X-ray and
   antitoxin; the near-future list (Q2, decided: projections to 2060) needs a
   study first, R6. Each is a row in the ship file's `## queued`.
2. **★ History's month: the study's where it names one, else its model's.**
   Thirty entries' tables name the month (E1 November 1952, PE3 January
   1901). For the rest research.json carries the month the study's own model
   ran them in (`docs/studies/research-model/`, June for 43 of them:
   mid-year, so the date is not early on average), `monthKnown` false; the
   law rows carry
   PolicyDates' dates exactly (a year alone in January, R0's rule), so the
   Policy gate and the catalogue cannot disagree (ResearchCheck 3 holds them
   lever by lever). The other way, January for every year alone (R0's and
   R1's year rule), brings 50 entries up to eleven months early and makes
   PE2, high-speed tool steel (1900), known at the founding. Recommended
   and taken; each month is explicit in the file, so changing one is data.
3. **★ Only a city founded the 1900 way reads it** (R1c ★1): a city founded
   the default way chooses no recipe, has every building fitted and orders
   any template, so a code resolved changes nothing there (§4: the default
   trace). In a 1900 city the tractor is known from June 1917 and the
   oxygen converter's recipe from November 1952, where R1-R1c had them never.
4. **★ L4, the DNA lab, carries a fee.** Law is free (research-design §2.4:
   not patentable), but L4 is equipment and its table says yes. Taken as the
   table says; the slice's brief had it free, mended in the review.
5. **★ The Oxygen Steel Shop stands from 1900.** I4 dated it 1952 (E1, the
   oxygen converter), but R1c gave it its 1900 ways (Bessemer, open hearth),
   so the shop is the 1900 steelworks and E1 dates its converter recipe, as
   the data already says. Recommended and taken.
6. **★ The Magnetite Mine & Concentrator opens with E4**, with the
   Pelletising Plant and the Large Magnetite Complex (research-1900 §1.2
   names the two; I5 dated all three 1955 for the same reason, taconite at
   Silver Bay). Recommended and taken.
7. **★ The Panel Mill and the Wood Pellet Mill stay years** (1980, 1970):
   research-1900 Q13 asks for two entries "with sourced dates" when the wood
   batch is written, and I10 shipped without them. For R2e, which sources
   the 1951-2000 rows.
8. **★ A year's reading kept for the screen.** `knownIn(year)` stays, as
   "known by that year's January" (`knownAt()` of it, one reading), because
   the Sector page asks by the year (`SectorScreen`, the screens lane's
   file): it shows a code known from the January after its month. R5's
   screens take the month.

## 3. Slices

| slice | what | state |
|---|---|---|
| A | `research.json` (89 entries: code, name, field, kind, the work's size, year, month and whether history names it, the World words, what it touches, its effect and sources, the codes it needs and the rest in words, its fee, its work, its study), `ResearchCatalog` (the reader, the work rule's dials, `knownFrom()`), `ResearchCheck` 1-6 | c77d716b |
| B | `Recipe`, `Equipment` and `BuildingsTemplate` known from a month (`knownAt()`; `knownIn(year)` its January's), a template's since a year or a code (`getSinceText()`), the catalogue's reader reporting a code it lacks; `Recipes`, `EquipmentFit` and `Game.fit()` by the month; the 29 templates of research-1900 §1.2 dated by code in `buildings.json` and the built-ins, the Oxygen Steel Shop 1900; `ResearchCheck` 7-9, and the premises that said a code is never known (RecipeCheck 4, EquipmentCheck 1 and 9, FittingCheck's labels, BuildingDataCheck's dated set) | 29262966 |

Both drafted by Sonnet 5.5 at xhigh, reviewed here. What the reviews found:

- **L4's fee.** The slice's brief said no law row carries a fee; the
  table gives L4, the DNA lab (equipment), a yes. Set as the table says,
  and ResearchCheck 6 holds it (★4).
- **A year's January, unclamped.** `PolicyDates.monthOf()` floors every
  month at 1, so `knownIn(1899)` read through it would call a 1900 piece
  known in 1899; the knownIn() reading uses `Equipment.januaryOf()`, the
  same formula without the floor, and agrees with PolicyDates from 1900.
- **The slice reports nothing it did not mend**: thirteen harnesses green
  (ResearchCheck 183 ok), no premise moved but the ones that said a code is
  never known, each kept beside a fixture code that is in no catalogue.

### Found: the tractor is known from 1917 and still never pays (for R1d)

In a 1900 city the tractor now becomes known in June 1917, and its verdict
moves from NOT_KNOWN to NOT_YET, never PAYS: `EquipmentFit.gainOf()` gives a
piece that ends FARM_HORSES no gain (NaN, R1's "R1c writes its petrol"), and
R1c's ★7 made the tractor the farms' recipe piece without writing it one. Its
gain is the form's (the horses' feed and hands, which R0c's form charges,
not the horses' recipe) less the recipe's (the tractor's fuel): so
`Recipes.gainOf()` alone would make it negative. R1d, which plays a 1900
city, writes it; until then a 1900 city's farms stay on horses past 1917.

## 4. Checks

(filled)
