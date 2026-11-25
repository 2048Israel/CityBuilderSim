# R6e-rest - the engineering rows R6e held, researched again

The brief (QUEUE.md's row; R6e's notes, "Final row dispositions"): for E22
with E33, E23, E26, E27, E30 and E32, find the evidence R6e said was
missing (the denominator, the boundary, the commercial date, a licence that
lets the game use the figure), adopt a row only when it is found and
consistent, and hold the rest at the old figure with what was searched.
E24 waits on PW3, which had not shipped when this batch reached it
(`git log origin/main --grep "Merge batch/PW3"` empty, 2026-11-24): left.

## In short

One row is adopted: **E22, the pressure exchanger**, on the Desalination
Plant. Six are held: E33 (reverse osmosis), E23 (autosteer), E26 (driverless
haul trucks), E27 (endless strip), E30 (calcined-clay cement) and E32 (the
electric long-haul truck). Three of the six (E27, E30, E32) fail on a
condition R6e did not name: today's figure does not contain them, so an undo
before their date removes nothing (★2).

Each row was researched by its own helper (a foreground general-purpose
agent at xhigh, web search and fetch, one at a time), reporting the figure,
its boundary, its source and its licence. Their findings are below, with
what each would still need.

## The licence question (★1)

R6e held several rows on "numerical commercial-use permission unverified".
The project's rule is that a single figure quoted is a fact: the study's
section 2 ("Facts are nobody's to own and no wording is copied ... Journals,
agencies and companies' own releases are cited for their facts only"),
`docs/studies/licences.md`'s rule ("Facts are nobody's") and its line on the
wealth papers ("a figure quoted is a fact"), and R6f's shipped M12 (Choi et
al., Springer) and B10 (an NBER working paper, whose site terms are
non-commercial). So a figure from an all-rights-reserved paper or a maker's
release may set a game number, cited, with nothing copied; a dataset, a
table or wording may not. No row below is held on its licence alone.

## Row by row

### E22 with E33: seawater reverse osmosis and the pressure exchanger

Found:

- **E33's date.** The first commercial seawater RO plant is Jeddah, Saudi
  Arabia, about 12,000 m3 a day, 1979 (SWCC, "The Evolution of Water Reverse
  Osmosis", Aug 2023, p8, the Saudi agency's own; the start month is January
  in one abstract seen only as a snippet and February in another). Cape
  Coral's 1977 plant is brackish, as R6e found.
- **The flash plant's energy** (what E33 displaced): IEA-ETSAP/IRENA Tech
  Brief I12 (2012): 290 kJ/kg of heat (80.6 kWh_th/m3) plus 2.5-3.5 kWh_e/m3.
  US DOE, Seawater Desalination Bandwidth Study (Oct 2017) p15: 4.0 kWh_e/m3
  plus 11.05 kWh_e-equivalent of heat, "assuming an efficiency of 33% for
  converting thermal energy to electrical work" (p70: a grid's generation,
  transmission and distribution). That is 33.5 kWh_th/m3, which does not
  agree with the same report's GOR 4-8 (81-162 kWh_th/m3). USBR's Desalting
  Handbook (2003) p61: about 54 kWh_th/m3 at best.
- **The exchanger's step**, measured: Stover (2010, Desalination and Water
  Treatment 13:33-41, pp38-39; read by R6e): Las Palmas III's late-2008
  Pelton-to-PX retrofit, 15,000 m3 a day, SWRO process 3.49 to 2.68 kWh/m3
  (-23.2%). The plant's own authors (Lemes et al. 2013, abstract, CC BY-NC-ND,
  evidence only): "more than 20%" on those trains; the whole plant 6.16
  (Francis), 5.18 (Pelton), 4.15 kWh/m3 (isobaric).
- **The split of a modern plant's draw**: US DOE 2017 pp16/24: the RO step
  "2.7 kWhe/m3 (with energy recovery)" at Carlsbad, confirmed by the plant's
  staff; its whole-plant current typical is 3.68 and state of the art 2.95.
  WateReuse (2011): the RO process with energy recovery is 65-85% of a
  plant's total.
- **Today's figure contains both.** The game's plant draws 10,880 kW for
  60,000 units of 10,000 US gal: 3.449 kWh/m3, between DOE's typical and
  state of the art, so it is a modern SWRO plant with energy recovery. Over
  80% of new SWRO plants use isobaric devices (Stover 2010, the maker's).

**E22 is adopted** (below). Its step is consistent three ways: Stover's
post-PX 2.68 is DOE's 2.70 within 1%; the remainder, 0.749 of 3.449
kWh/m3 (21.7%), is inside WateReuse's 15-35% outside the RO process; and
the game's before/after ratio, 1.237, is Las Palmas III's whole-plant
5.18/4.15 = 1.248 within 1%.

**E33 is held.** Its date is found, but the flash plant's heat has no
boundary in the plant's one input, electricity: the plant buys no fuel and
no steam, and the sources' heat, put in electricity, runs from 11 (DOE's
33% conversion, inconsistent with its own GOR) to 27 kWh_e/m3 (ETSAP's 80.6
kWh_th at the same 33%). Holding E33 also leaves the study's design ("E22
needs E33") for later: E22 needs E6, the plant's own row (★3). What would
close E33: one source giving a flash plant's heat and its electricity
equivalent on a stated basis (a dual-purpose plant's forgone generation), or
a reader that buys the plant's heat as fuel, a new owner beyond
research.json.

### E23, GPS autosteer

Found: no measured fuel saving over a season's field work. Per operation:
overlap 7% in spring cultivating and 3.3% in planting without guidance
(Iowa State, Darr, 2012); with and without RTK autosteer, overlap 1.9-2.2%
to 0.25-1.4% of working width (Kviz, Kroulik and Chyba 2014; fuel not
measured); spraying -11.5% fuel a hectare (D'Antonio et al. 2023, CC BY-NC);
seeding -6.3%, not significant (Scarfone et al. 2021, snippet only). USDA
ERS (ERR-217, 2016) bundles fuel with other inputs. No agency states the
field-work share of a farm's diesel; Iowa State PM 709's gallons an
operation give 40-64% by the helper's own arithmetic. Adoption is sourced:
auto-steer and guidance on 58.4% of US corn acres (2016), 54.5% of soybeans
(2018), 55.9% of winter wheat (2017) (ERS, McFadden and Njuki 2023, public
domain).

**Held.** Saving times field share times adoption gives 0.5-2.5% of the
farms' diesel, assembled from inconsistent per-operation figures, none of
them a measured whole-season effect. Missing: a season-long measured trial
of autosteer against manual steering on grain, and a sourced field-work
share of diesel.

### E26, driverless haul trucks

Found: no measured fuel effect. Komatsu's own analytics find fuel "about
neutral" between autonomous and manned trucks (International Mining, Sep
2022); a simulation gives -6.1% a tonne (Parreira, UBC thesis 2013, excerpt
only). Haul trucks' share of mine energy is modelled only: 74.5% of an iron
mine's extraction energy (US DOE/BCS profile, 2002, from a 1982 model) or
36.7% of a coal surface mine's materials-handling diesel (DOE bandwidth
study, 2007). Posts: Suncor's forecast of about 400 net roles for over 150
trucks (Global News, Jan 2018), not a measurement. Adoption: 3,832
autonomous haul trucks in July 2025 (GlobalData), about 4% of mining trucks
by the helper's arithmetic; Rio Tinto's 300th, about 80% of its Pilbara
capacity (Komatsu, Aug 2024).

**Held.** No measured fuel effect, no measured haul share of an iron-ore
mine's diesel, no measured net change in posts. R6e's +12% utilisation and
-13% cost remain context.

### E27, endless strip production

Found: no measured carrier-by-carrier step against thin-slab casting. The
pieces: a thin-slab tunnel furnace burns 0.50 GJ/t (EU JRC Ferrous Metals
Processing BREF, 2022, p165); ESP burns no fuel and uses "in the range of
120 kWh/t" in all (Primetals, MHI Technical Review 61(2), 2024), with no
matched thin-slab electricity; Arvedi's 2009 "25-30% less than ISP" is a
pre-startup projection. Endless lines: 11 (Primetals, May 2026), about 25-30
Mt a year, about 1.5% of world steel; one US line since late 2024.

**Held**, on both conditions: no measured step beyond E18, and today's
typical mini-mill is thin-slab with a tunnel furnace, not endless (★2).

### E30, calcined-clay cement

Found: calcined clay is under 1% of world cement (LeadIT/SEI, Sep 2026:
capacity for about 26 Mt a year of about 4,000); the US does not report it
(USGS, Dec 2025: 96% of blended cement is Type IL). World clinker ratio 0.71
(IEA, 2023, CC BY 4.0). No matched, measured LC3-against-Portland comparison
from an operating line; the published ones are modelled (Sánchez Berriel et
al. 2016, about -17% cradle to gate). Argos's own report gives company-wide
figures, not Rio Claro's before and after. And 2020 is not calcined clay's
first commercial year: the LeadIT table lists three Brazilian plants of
2009.

**Held** (★2): the game's Cement Plant, at 0.134 t of coal a tonne, is a
near-pure-clinker Portland plant; today's figure contains no LC3, so an
undo before 2020 changes nothing. Seen in passing, not a defect this batch
owns: that coal is above what the world's 0.71 clinker ratio would give.

### E32, the battery-electric long-haul truck

Found: 0.3% of the EU's 6.16 million trucks over 3.5 t were battery-electric
in 2024, mostly regional rigids (ACEA, Jan 2026); new heavy-truck sales 3%
electric in the world in 2024 and 9% in 2025, over 90% of them in China
(IEA Global EV Outlook 2026, CC BY 4.0); under 0.5% in the US. Measured in
service: 0.96 kWh/km at 11-18 t for 19 eActros rigids (Öko-Institut,
ELV-LIVE, 2025, at the battery); charging 89.7% efficient (Cenex, BETT,
2023). Payload about 22 t for the eActros 600 (Daimler Truck, Nov 2025).

**Held** (★2): today's fleet is about 99% combustion, so an undo before
December 2024 removes nothing, and an electric fleet is a new energy path
(charging, dispatch, the depot's power), not a research row's undo.

## What was built: E22

- **research.json** row E22, "Pressure exchanger": ENGINEERING, equipment,
  1997 (Energy Recovery's first PX at sea, the Canary Islands; June is a
  placeholder, monthKnown false), needs E6, fee yes, work 4,531 by
  `ResearchCatalog.workByRule` (equipment, 1997). Its four sources are in
  `docs/studies/licences.md` section 6.
- **buildings.json**, the Desalination Plant (id 73): one undo, on its
  electricity alone, `+2,574.22 kW` = 2.70 kWh/m3 x (3.49/2.68 - 1) x
  2,271,247 m3 a month / 720 h. Before E22, or before a plant is fitted, it
  draws Math.round(10,880 + 2,574.22) = 13,454 kW; every other figure is
  today's. The undo is carried by a piece, `pressure-exchanger` (R1c call 10's
  estimate: MACHINERY 2,454.6 = 25% of cashCost / 13.4, work 8,800 = 10% of
  constructionPoints, 300 months), because every patentable change on a city
  work is one (PublicLicenceCheck: the capture reads a template's and its
  pieces' entries). A plant that finishes before June 1997 is recorded as
  lacking it; one finished after is fitted; the player's Fit
  (`Game.fitForCity()`) fits an old one. The water's power
  (`ServicesManager.setWaterWorksDraw`) reads the plant's draw, so the
  water's books pay the undone power.
- **The licence's reader**, `LicenceFees.entryGain()`: a city utility's
  public gain was the service its entry delivers at the tariff, so a piece
  that saves power and serves no more water billed nothing, and E22's fee
  never reached the city. A piece's gain now adds the power its buildings
  would draw more without it, at the commerce price
  (`EconomyManager.getPowerPriceFor()`), through a new package-private
  `RecipeChanges.electricityAt()` beside `productionAt()` (compose()'s own
  sum). Only E22 is moved: the other city-work pieces (PE5 rapid sand, PE6
  steam turbine, PE16 pulverised coal) undo production alone, so their
  saving is 0 (checked in buildings.json). The planners' price of a new
  plant (`forNewWork()`) carries the fee from June 1997 to June 2017.
- **The save:** SAVE_FORMAT and OLDEST_LOADABLE_FORMAT 65 to 66. A format-65
  save holds no unfitted count for a piece that did not exist, so its
  plants would load as fitted and draw 10,880 from June 1997 with no fitting
  built: loaded wrongly, so the floor rises, no conversion.

## The calls

- ★1 (above): no row is held on its licence alone; a figure quoted is a fact.
- ★2: a row whose technology today's figure does not contain (a small share
  of practice: E27, E30, E32) has nothing to undo. Recommended: hold, as
  here; such a row is a new building or recipe, not an undo.
- ★3: E22 needs E6, the plant's own row, not E33 as the study drew it, since
  E33 is held. The study's 3.1 table says so in E22's owner cell.
- ★4: E22 is a piece, not a free-standing recipe change, so the licence is
  captured and an old plant keeps the old draw until it is refitted.
  The alternative, a recipe flag every plant drops in June 1997, failed
  PublicLicenceCheck ("no city work's ... free-standing recipe change
  carries a payable entry").
- ★5: the licence bill on the power saved (the `entryGain` mend above).
  The alternative was to leave E22's fee uncaptured and queue the reader;
  it is one term on one branch, moves no other row, and without it the
  row's `fee: true` is read by nothing. Recommended: as built.

## Checks run

On the final code (after the mend; ResearchCheck and PostwarRecipeCheck
again after the merge of main at 0.9.200). The review (REV-R6e-rest, SHIP)
had two wording nits, folded: the saving is priced at the commerce price
(`getPowerPriceFor()`, not the works' surplus price), and E22's date is
Energy Recovery's release, not Stover's 2008 retrofit:

- ResearchCheck: 101 entries, E prefix 22, ENGINEERING 54, the needs pin
  (36 links over 30 entries), E22's date spot and study tail.
- ModernRowsCheck, a new section 4 (240 ok): the row and the data; 13,454 kW
  before June 1997 and for an unfitted plant after it, a new plant fitted at
  10,880; the sea works' draw after a real month; a default city at 10,880
  across the month; a save and load keeps the unfitted count; no sector
  fits a city work's piece (a NOTE: twelve months, nothing ordered) and the
  player's Fit does, after which the draw is today's; the imported
  exchanger's public gain inside its term is the power saved at the works'
  price, and the bill is SHARE of it; a plant with no record of the count
  is read as fitted from June 1997.
- PostwarRecipeCheck (233 ok; template changes 31), PublicLicenceCheck (68),
  HistoricalLicenceCheck (80), SaveFileCheck, ReadPathCheck,
  BuildingDataCheck: green.

## The ensemble

80 of 80 runs on the gate box (aef18c2e3, 35 minutes), scored against
`docs/ensemble/0.9.199.md`, committed as `docs/ensemble/R6e-rest.md`:
batch verdict PASS, the hard criteria 10/10, M1 0 and F1 0/16 in every
policy, criteria 25/26, no verdict changed. A2 (a spell of inflation over
10% from m240, 13 months against under 12) fails as the baseline's does.
The trace is unchanged: every median equals the baseline's (AUTO2's final
population 9,223,026 in both), as it should, since the ensemble's cities are
founded today's way and E22, its piece and its licence act only in a city
founded the 1900 way.

## The dry run

`sh tools/gate.sh R6e-rest --dry-run` on the gate box at c5f72fe9a (the
orchestrator's go with the review's SHIP, once the ensemble passed): gate
exit 0, run `/home/jerus/cbs/runs/cbs-gate-R6e-rest-HOMKXWfI` (the ship
file's first line). The suite took 1,279 s and exited 3, known reds only,
every one main's (AutoBuildCheck, CarCheck, CopperRefiningCheck,
CurrencyCheck, DeathRecordCheck, DenominationCheck, ForeignDebtCheck,
GroundSkipCheck, HoldersCheck, InvestCheck, OrderDoorCheck, PlanCheck,
PopulationCheck, RailDrawCheck, RoadCheck), with no unnamed line.

## Seen in passing, not this batch's

- A city work's piece is fitted only by the player (`EquipmentFit` ranks
  and chooses a sector's stacks; a city work has none), so in an unplayed
  1900 city a plant built before 1997 runs 13,454 kW for its life. This
  is how PE5, PE6 and PE16 already behave; ModernRowsCheck prints it as a
  NOTE. The orchestrator may queue it.
- The Cement Plant's 0.134 t of coal a tonne is above what the world's 0.71
  clinker ratio would give (E30 above); not checked further.

## E24, and the others in flight

E24 waits on PW3 (its place and season dispatch): the ship file's pending
row. RES3 also takes format 66; whichever ships second bumps again.
