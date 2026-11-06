# R1c - research: recipes as data ({next}, 2026-11-05)

## WHERE IT STANDS

**Building.** Branch `batch/R1c` from `batch/R1b`'s ship file (RL1); R1b not
on main yet.

## 1. What R1c is

R1-notes §1's third part (research-design §2.1, research-1900 §3):

- **recipes as data**: `buildings.json`'s `recipes` on a template, each a
  way the building runs (what it uses, makes, who it needs, what it draws),
  the top level the default;
- **the best known recipe each month at the city's prices**
  (`SpreadPlanner.cityValue()`), in a city founded the 1900 way;
- **the tractor's fuel in today's farm** (R0c ★5);
- **the 1900 recipes of research-1900 §3's rows with no form yet**.

## 2. The calls (★)

1. **★ Only a city founded the 1900 way chooses.** In a city founded the
   default way every building runs its top level, today's figures, and the
   month reads no price for it: research-design §9's "a building with
   everything known runs exactly today's `buildings.json` figures", and the
   default trace moves only by the farms' fuel (★7). A city founded the 1900
   way (`Founding.withUnfitted()`, R1b, off until R1d) knows a recipe by its
   `since` (a year from its January; a catalogue code never, until R2) and
   runs the best it can at the city's prices. Recommended and taken: the
   alternative, a default city choosing too, would let today's buildings fall
   back on 1900's ways at odd prices.
2. **★ The piece replaces the old way.** On a template with a recipe piece,
   its fitted buildings run only the recipes that need the piece and its
   unfitted ones only the recipes that need nothing (by-product ovens replace
   beehive ovens; a farm with its tractor has sold its horses, so it cannot
   save the fuel without paying the feed and the hands R0c's form charges).
   A template with no piece (the Pulp Mill) runs the best of all it knows.
   Recommended and taken.
3. **★ The template's figures are what its buildings run.** Every reader of
   a building's figures (well over a hundred) reads the template, and a
   city's templates are its own (`Game.buildWorld()` makes a new
   `BuildingManager`), so the running recipe is set on the template: the mix
   of its fitted and unfitted parts' recipes, weighted by their counts, posts
   and kW rounded (exact while one part is empty). So a planner weighs one
   more building of a 1900 city's template at the mix, not at what a new one
   would run; R1d, which switches the 1900 city on, judges it. Recommended.
4. **★ One recipe piece a template.** Each §3 row is one piece on its
   templates; composing two (R2's PE14 beside the binder on a Grain Farm)
   needs recipes written as changes, not whole figure sets. Left to R2.
5. **★ The choice is saved.** It is struck at the month's top from that
   month's prices, which the state the month ends in does not hold (the
   flow rule), so a save carries it by template id (fitted part, unfitted
   part), written only where a part runs other than the default: never in a
   city founded the default way. No format bump.
6. **★ The X-ray and the antitoxin wait.** They change deaths and hospital
   stays, which `CausesOfDeath` takes from history's path (R0b), and a
   hospital is the city's, with no prices to choose a recipe at. Recommended:
   R2's medical rows (PM3's toxoid acts on the same cause) or a care batch;
   R1c-R in the ship file's `## pending`.
7. **★ Today's farm buys diesel and petrol, not petrol alone.** The row says
   `PETROL` (research-1900's PE14 is the 1917 gasoline tractor), but the
   top level is today's farm, whose tractors and combines burn diesel:
   Canada's farms spend 3.4-4.5% of their cash receipts on machinery fuel
   (2022-2025 [U]), about 87% of it diesel (Quebec's 6.2 PJ against 0.9
   [AF], est. for Canada). So each farm buys 4% of its output at export
   prices in fuel, 87% `DIESEL`, 13% `PETROL`, at their import prices (est.):
   the Grain Farm 7,080 L and 1,190 L a month (14 L of diesel a tonne of
   crop; a prairie field's own work is 7-10 [V]), the Mixed Farm 1,770 and
   300, the Livestock Farm 26,850 and 4,510. The tractor's recipe is the top
   level (since PE14, needs the tractor); the horses' recipe buys none
   (R0c's form still charges the feed and the hands). This moves every
   city's trace: the ensemble (§5). Recommended; the row's petrol alone is a
   one-key change.
8. **★ The binder waits for two pieces on one template.** The Grain Farm's
   tractor is its recipe piece (★7), so the binder would be a second (★4).
   Its figures are sourced (§3 row 7) for R2, where PE14 and PE17 sit on the
   same farm. R1c-R.
9. **★ The Oxygen Steel Shop has three recipes, the Pulp Mill three and no
   piece.** The shop's top level is the basic-oxygen converter, since `E1`
   (research-design's 1952 entry, a code: never known in a 1900 city until
   R2); its 1900 ways are the Bessemer converter (needs nothing) and the open
   hearth (needs the furnace, the §3 piece). The Pulp Mill's top level is
   kraft, since 1907 (East Angus, Quebec [N]); groundwood and sulphite are
   1900 recipes needing nothing, since "fitting groundwood" to a kraft mill
   is not a thing: a 1900 mill runs whichever pays. Recommended and taken.
10. **★ What a piece costs, by one rule.** A piece built into the plant (the
    by-product ovens, the rotary kiln, the open hearth) buys a quarter of the
    plant's cash cost in `MACHINERY` at its import price, and its work is a
    tenth of the plant's points (R1's drive); a piece carried in (the cutter,
    the shovel, the drill) buys its machines and takes no work (R1's truck
    and tractor); the Hulett, built into the dock, a tenth of the points and
    four 800 t machines (est.). Every figure est.; R1d judges them on a 1900
    city.
11. **★ Ship at 23/26, A3 short in two seeds' tails.** The farms' fuel is the
    one thing that moves a played city, and the ensemble on it (§5) keeps the
    baseline's median deflation spell (4 months) and improves A2, S1 and the
    final city, while two seeds of sixteen hold yoy under -10% for 12 and 15
    months in a young city's slump (m700-900), against 0.9.19's longest of 8.
    Recommended: ship, as 0.9.13 shipped "A3 misses by a month in one seed";
    the other way, the fuel left out until R1d (two keys a farm and
    Agriculture's two lines), keeps today's farm buying nothing to run its
    machines.

## 3. The recipes, row by row

Each 1900 recipe is today's building with that one piece's effect undone, so
most figures are ratios applied to the top level. Sources are research-1900's
keys where it has them, else this batch's (listed under the table).

| row | template(s) | the 1900 recipe | from |
|---|---|---|---|
| 1 by-product ovens | Coke Ovens | beehive: coal x1.119 (1.60 t a t of coke against 1.43, US 1935) = 60,420 t; no gas, so `gen` 0; 220 kW (est.); posts x1.94 (3.7 t a man-day on a 37-oven bench against today's 200 posts for 40,000 t, unverified): 308 unskilled, 70, 9 engineers, no scientists | [AA], [AE], est. |
| 2 machine coal cutter | Underground Coal Mine | pick and powder: the face's unskilled x1.5 (machine labour 62-70% of the pick rate, undercutting half a hand miner's work) = 360; 28,100 kW (cutters under 3% of the draw, est.) | [P2.8], [O] p.70 |
| 3 steam shovel | Large Open-Pit Mine, Iron Mine | hand-loaded: about 20 t a man-shift (a shovel's crew did 6-12 times a hand shift), +960 and +38 unskilled; the shovels' quarter of the diesel left out (est.) | [P2.7] p.139, [R] |
| 4 power rock drill | Quarry, Large Quarry, Copper Mine & Mill | hand-drilled: drilling a fifth of a quarry's work by machine (a quarter in a copper mine), 9 times as long by hand (8-10 ft a man-day against 65-96), so drilling is then two thirds to three quarters of the work, as Barger and Schurr say: +35, +200 and +240 unskilled; the drills' compressors left out of the draw (est.) | [P], [P2.7] p.123, 142 |
| 5 rotary kiln | Cement Plant | shaft kilns: coal x1.4 (1.2-1.6 times today's 13.4% of the cement's weight) = 4,690 t; burning about 11% of a rotary plant's labour, 3.4-5.7 times as much on shaft kilns (x4): +20 unskilled; 2,800 kW (no kiln drive, est.) | [G], [H] |
| 6 groundwood, sulphite | Pulp Mill | groundwood: 85-87% yield against kraft's 51%, so 29,400 m3 of logs and no chips; 1,430 kWh a tonne (79.7 hp-days), 21,850 kW, no `gen`; fewer engineers; consumables 150 (est.). Sulphite: kraft's wood (2 cords a tonne, both, 1916); 1.75 t of steam coal a tonne (unverified), 19,250 t; 6,100 kW (est.); no `gen`; sulphur and limestone as consumables, 450 (unverified) | [K], [L], [J] |
| 7 binder (waits, ★8) | Grain Farm | without binder and thresher, harvest and threshing x6-9, 60-80% of a binder farm's wheat labour: posts about x5.5 | [T], [AH] |
| 8 Hulett unloader | Bulk Terminal | rope and tub: 19 cents a tonne against the Hulett's 6 (x3.2), unloading about 40% of the terminal's labour (est.): +375 unskilled | [P1.87], [D] |
| 9 open hearth | Oxygen Steel Shop | Bessemer: 1.10 t of pig a tonne of ingot, no scrap, no lime, no fuel, no gas = 319,000 t of pig. Open hearth: pig and scrap equal (159,500 t each), limestone 6.5% (est.), 0.33 t of coal a tonne for the producer gas (US 0.25-0.40, est.) = 95,700 t, no gas. Both: the unskilled x1.5 (unverified: no source broke a 1900 shop's labour out) | [Y], [W], [X] |

[AA] US coke yields 1935, https://search.library.wisc.edu/digital/AIFQJQCKS4JB3H85/text/APFLBLDZUD4DQI9B ·
[AE] https://epe.lac-bac.gc.ca/100/205/301/ic/cdc/coal/impact/cokemin.html ·
[O] Sullivan Machinery 1902, https://distantreader.org/stacks/trust/uc2/uc2_ark+=13960=t08w3922z.txt ·
[R] Hermann 1894, http://www.gutenberg.org/cache/epub/47187/pg47187.txt ·
[P] Mine and Quarry, Aug 1911, https://quarriesandbeyond.org/pdf/quarrying_with_hammer_drills-mine_and_quarry-aug_1911.pdf ·
[G] Sabin, https://bookdome.com/science/Cement-Concrete/Preparation-And-Mixing-Of-The-Raw-Materials-Part-3.html ·
[H] Eckel 1905, https://distantreader.org/stacks/trust/uc2/uc2_ark+=13960=t6zw1b26d.txt ·
[J] Canada Year Book 1921 p.319, https://www66.statcan.gc.ca/eng/1921/192103590319_p.%20319.pdf ·
[K] https://distantreader.org/stacks/trust/uc2/uc2_ark+=13960=t42r3q04v.txt ·
[L] https://distantreader.org/stacks/trust/uc2/uc2_ark+=13960=t8ff3nh3p.txt ·
[N] https://images.ourontario.ca/Terracebay/3765523/page/11 ·
[T] https://www.lhf.org/2015/07/grain-harvest-and-threshing-time/ ·
[AH] https://econedlink.org/resources/the-mystery-of-the-amazing-farmers/ ·
[D] ASME landmark 199, https://www.asme.org/wwwasmeorg/media/ResourceFiles/AboutASME/Who%20We%20Are/Engineering%20History/Landmarks/199-Hulett-Ore-Unloaders.pdf ·
[Y] 1911 Britannica, Iron and Steel, https://en.wikisource.org/wiki/1911_Encyclop%C3%A6dia_Britannica/Iron_and_Steel ·
[W] https://runeberg.org/sweden14/2/0316.html ·
[X] https://tekniskkulturarv.dk/book/a48005b8-d768-40d2-ad2d-cf3321a8ab02?page=459 ·
[U] Statistics Canada farm expenses, https://www150.statcan.gc.ca/n1/daily-quotidien/231128/dq231128b-eng.htm ·
[AF] MAPAQ BioClips 33-24, https://www.mapaq.gouv.qc.ca/SiteCollectionDocuments/Bioclips/BioClips2025/BioClips_Vol33_no24.pdf ·
[V] https://www.albertafarmexpress.ca/?p=85827

The weakest: steel's labour, the by-product ovens' and the open hearth's
capital (no source priced them by the tonne), the coal cutter's price and
power, sulphite's chemicals and steam. R1d, which runs a 1900 city, is the
first to weigh any of them.

## 4. Slices

| slice | what | state |
|---|---|---|
| A | `Recipe`, `Recipes`, the catalogue's `recipes`, the template's live figures (`runRecipes()`), the choice in a 1900 city only, the save, EquipmentFit's gain for a recipe piece; `RecipeCheck` 1-8 | 564bc2c6 |
| data | the rows of §3 and the farms' fuel, by this session (`buildings.json`, the built-in farms, `Agriculture` declaring DIESEL and PETROL) | 6c34f632 |
| B | `RecipeCheck` 9 (the shipped rows), EquipmentCheck and FittingCheck on them | c10c6134 |

A and B drafted by Sonnet 5.5 at xhigh, reviewed here. What the reviews
changed and what the slices found:

- **Every recipe's goods are declared at once in a 1900 city.** The slice
  declared a recipe's new goods (sulphite's coal for Wood Products) when it
  first ran, as R0c declares the horses' crops; a declaration is never
  undone, so a city played straight would carry every good any month had run
  and its reloaded twin only those of the choices it restored. Declared from
  the template's list, both hold the same set. A default city declares none.
- **A WHY paragraph quoted Jerus with words he never wrote**; replaced with
  his line research-design §2.1 records ("Recipes are free to upgrade
  immediately.").
- **The live maps are swapped, not refilled**, and `volatile`: a screen on
  the FX thread iterating `goodsMade()` keeps the map it was given while the
  month's thread sets the next (`pick.sh` asks the gate's threading review).
- **FittingCheck 4 met the 1900 village ordering a Pulp Mill.** Its two
  Bakeries cost 968 against the default village's 609: not a cost leak (the
  cash cost and the materials are the same figure) but the 1900 village's
  Wood Products, unheld, putting up a Pulp Mill on groundwood, which pays
  there, so 101,384 points sit in the builders' queue against 774 and the
  order is priced at 1.75 times. The fixture now holds each city's charge to
  the model's quote and the template's cost to the same figure in both.

### Found: groundwood always beats kraft (for R1d)

At the fixture town's prices the Pulp Mill's groundwood earns 106.8 a month
against kraft's 90.6 even in 1907, when kraft is known: it needs half the
wood a tonne and `PULP` is one good, so nothing prices groundwood's weaker
fibre (newsprint, not kraft paper or board). A 1900 city's mills stay on
groundwood and its sulphite never runs, and its Wood Products orders Pulp
Mills where the default city does not. R1d, which plays a 1900 city, decides:
a second pulp good, or groundwood's market share capped (Canada's 1916 mix:
64% groundwood, 28% sulphite, 8% sulphate [J]).

Also seen there: at month 1 the wage rates are 0, so the cutter's and the
drills' gain is 0 and the shovel's below it (the labour market has not
struck a wage yet); by month 7 every piece pays but the rotary kiln (5.14
against a 5.16 hurdle). Every steel-shop recipe loses money at the village's
prices (the open hearth -355 a month): no village builds one.

## 5. Checks

- **The trace.** The default playtest (2,405 months) traced on R1b's tip
  (998097e5) and on slice A merged with it (644345aa), each built apart and
  run back to back: all eight CSVs byte-identical, the log but for "in
  88s"/"in 85s" and the layout windows' "5.2 s"/"4.4 s". Slice A changes
  nothing in a city founded the default way. On the data commit (6c34f632)
  every CSV moves: the farms buy their fuel.
- **The ensemble** (`tools/ensemble/ensemble.sh R1c`, 80 runs on 6c34f632,
  14.7 min, against 0.9.19's): **23/26 against 24/26**, as
  `docs/ensemble/R1c.md`. A2 and S1 fail as 0.9.19's do and both improve
  (A2's spell 16 months [10 to 36] against 18 [10 to 37]; S1 2.14% against
  2.54%); the final city is 602,188 against 578,707. **A3 is new**: its
  median deflation spell is 4 months as before, but two seeds of sixteen run
  under -10% a year for 12 and 15 months (seed 5 to m738; seed 8 to m876,
  where unemployment goes from 27% to 60% in a young city's slump), against
  0.9.19's longest of 8. ★11 below.
- **`--pick`** (102 of 177 harnesses and LongPlaytest, for 56 files changed
  since R1b's tip; the gate's world files named) and **the full suite**
  (`AllChecks -f`, 177 harnesses, 170 green) on 2578ab8f, before R1b
  landed. Mended here: OilCheck 6, whose identity had the refiners' home
  petrol all going to the forecourts; the farms now buy it too (§2 ★7), so
  it is the forecourts' purchase and every other sector's, to the bit. The
  reds left are none of R1c's code; the farms' fuel moves the default path,
  and on it:
  - **main's known classes, by name**: SlopeCheck's "a building on the map
    moved" and MapCheck's moved boxes (DM2's), DenominationCheck's reformed
    twin 1e-9 apart (a price tie in the bond book, OB1), ScaleCheck (main's
    own red, ST1's class), each with other numbers than main's record;
  - **main's margins, met on the moved path** (pass by name, follow-ups
    queued): MonetaryCheck's "a month's delay in the hand moves them less
    than the allowance" reads 0.100 points of its 0.100, where R1b's tip,
    built apart and run here, reads 0.099 (`MEASUREMENT_NOISE` keeps "about
    twice the largest reading", which main had lost already: MN1); KeptCheck
    49's "a month's redraw after each choice hands back nine in ten of the
    page's nodes" finds Construction's income statement handing back 89.8%
    on R1c's city, where R1b's city's worst page is 100% (KP4's new floor; R1c
    touches no screen, and in a default city changes nothing but the farms'
    fuel: KS1, for the screens lane);
  - **timing**, hard only in the direct `AllChecks -f` run (SaveSlotCheck
    1, MapCheck's two, KeptCheck's chrome), SOFT under `batch-check.sh`.
  `pick.sh` asks the gate's stranger's review of the saves (`DataSave`'s
  `recipeChoices`, a key only where a part runs off its default) and the
  threading (`volatile` on the template's live maps, §4).
- Not a process batch: no scripts, checks' machinery or rules changed.
