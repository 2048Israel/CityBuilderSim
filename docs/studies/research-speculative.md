# Research: the speculative tier, 2030-2060

*2026-11-16. A design study, R6-T: the research catalogue's rows past the
evidence, each on a public target for its date and its effect (Jerus,
2026-11-09: "Keep to 2060", overturning the near-future study's ★3, which
ended the catalogue at the evidence, about 2028). It changes no code and adds
no row to `research.json`: a row goes in with the batch that builds what it
gives (R6's ★6). Read after `research-near-future.md` (§2 the rules, §4 the
slips, §9 the calls) and R6b's row in `QUEUE.md`. The gatherers' fact sheets,
every source with its mark, are in `research-speculative/`.*

## 0. In short

1. **Eight rows, all dated 2030-2035**: seven engineering, one medicine
   (table 3.1). Four are power plants or a plant's part (a sodium reactor
   with heat storage, fusion, superhot geothermal, power from orbit), one a
   better solar module, two improve a farm the game has or a works R6b builds
   (the driverless season; electrolysis at 46 kWh a kilogram), and one is the
   tuberculosis vaccine.
2. **The public record runs out about 2035, not 2060.** Later dates are a
   fusion prototype (STEP, 2040), late railways (Tokyo-Nagoya at least 2036,
   HS2 2036-2043) and goals for outcomes (cancer deaths halved by 2047,
   tuberculosis deaths down 95% by 2035, 100 GW of Indian nuclear by 2047).
   None names a new thing with a figure. The tier fills 2030-2035 and leaves
   2036-2060 to be read again as its events come (★4).
3. **The less is shown, the more a date slips.** A plant being built slipped
   about a year (R6); one decided but not building, two to three (Natrium,
   Stegra, Salzgitter, Electra, Boston Metal); one whose deciding test is
   unread, four to twelve (ITER, Europe's DEMO, China's thorium reactor). The
   allowance is staged on that (★1).
4. **Business and law give no row.** Every dated target found is a law the
   game has nothing for (VAT reporting against a fraud the game does not
   model; a pension age of 70, which the game already has), a share of firms
   adopting something, or dated before 2030. Medicine past 2030 is dated by
   trial registries whose endpoints carry no target; one row stands (FM3).
5. **Many targets are costs** (the US energy department's $45 a MWh for
   geothermal and for floating wind by 2035, 2 cents a kWh for solar by 2030,
   long storage 90% cheaper by 2030). A cost is not a row's effect: the
   game's prices decide what pays (★2).
6. **R6's watch list, read into the tier:** fusion and the TB vaccine are
   rows; electrolytic iron, the pig kidney, Alzheimer's prevention, the
   cholesterol edit, the universal flu vaccine, the new antibiotic, humanoid
   robots, deep-sea nodules and AI agents stay out, each for the reason in
   section 4.
7. **Two rows have something behind them, in the game or in R6b** (FE8 at
   the farms, FE10 at FE4's works): R6d, queued here after R6b. The other six
   wait on the power batches and the care batch (section 7).

Four calls for Jerus, each with a recommendation, are in section 6.

## 1. What was asked, and the rules kept

Q2 (research-design §7) asked for near-future projections to 2060, marked;
after 2060, nothing. R6 found nothing it could measure after 2028 and
recommended ending there (its ★3 (a)); Jerus took its (b), a second tier at
the builders' dates with the builders' targets for effects. This is that
tier. From R6, unchanged:

- **★1** a projected row has one central date, fixed, and is marked
  "projected" wherever its date shows, its window kept in the data: R6b's
  `projected` and `window` keys, held to `ResearchCatalog.HISTORY_ENDS`
  (2026-11). Every row here is after it.
- **★2** the date is the first commercial-scale operation (a drug's first
  approval, a law's day in force), at the builder's date plus a slip
  allowance; section 2 extends the allowance to the stages R6 gave no row.
- **★4** the carbon rows wait (section 4.4).
- **★6** a row enters `research.json` only in the batch that builds what it
  gives.
- **★7** codes FE, FM, FB and FL, continuing R6's: FE7 on, FM3 on.
- **★8** the work rule, to 2060 (section 5).

What changes is R6's rule 2. A row's effect may now be a **target**, stated
by its builder, a government, an agency or a trial's design, where R6 asked
for a figure measured at pilot scale. That is what makes the tier
speculative. Rule 4 stays: the effect is physical, what goes in and what
comes out. The marks are R6's (page, snippet, memory): every figure in table
3.1 was seen on its page, and the pages opened again from this session are
listed in `docs/queue/R6-T-notes.md`. A forecaster's estimate (a
consultancy, a scenario, a modelling paper's assumption) is nobody's target
and no row's figure. The licence rule is R6's §1: dates and figures are
facts, used with the source named and no wording copied.

A builder's words read as a year: "early 2030s" or "early next decade" (said
in the 2020s) is 2032, "mid-2030s" and "the 2030s" 2035, "by the end of the
decade" 2029, a range its later end.

## 2. The allowance past the evidence (★1)

R6's allowance (its §4) dated a plant in commissioning or being built and a
drug whose trial had read, and gave no row to anything earlier. The slips on
record in both studies, by the stage the thing was at when its date was
given:

| stage when the date was given | slips on record | central date |
|---|---|---|
| in commissioning | Aurora 4 months; Linglong One about 1 year | the stated date, or the next year if it is within six months or past (R6) |
| being built | Stegra's later steps, end 2025 to 2026 to early 2027, about a year each | stated + 1 (R6) |
| shown at pilot scale, or sold in an earlier form; the first commercial unit not yet building | Natrium 2028 to 2030-31 (about 3, before its construction); Stegra 2024 to early 2027 in all (3); Form Energy 2023 to 2026-27 (3-4, R6); Salzgitter's decision 2026 to 2028-29 (2-3); Electra's first plant 2027 to 2029 (2); Boston Metal's demonstration 2026 to 2028 (2); Rolls-Royce's small reactor early to mid-2030s (about 3, snippet); Darlington 2 (R6) | **stated + 3** |
| the deciding test unread (nothing has run at pilot scale) | ITER's fuel 4, its first plasma 9; China's thorium demonstration 2030 to 2035 (5); Quaise's pilot 2028 to 2030 (2); Helion's net electricity 2024 to not yet (2, open); Europe's DEMO early 2040s to after 2054 (12 or more) | **stated + 5** |
| a drug whose deciding trial is unread | FM2 read in Aug 2026, its filing within 12 months, its approval named for 2027 | **the registry's primary completion + 2**: a year to read and file, a year's review |
| a statute adopted, its day in force set | ETS2 2027 to 2028, by law | the day in force; read again on any amendment |
| no date from a builder, a government, a registry or a statute | | no row |

**The window** opens at the earliest date any builder or plan gives for the
same thing and closes at the later of the latest date any of them gives and
the central date plus one more slip of the stage's size; never after 2060
(Q2). On R6's rows this gives R6's windows back (FE4 2027-2030, FE5 to 2028,
FE6 to 2030). A row whose central date would fall after 2060 is not in the
catalogue.

Five years for an unread test sits low among its slips (4 to 12): the upper
end would put fusion after 2045, later than every builder's date but
Europe's. The larger risk of an unread test is that it fails, which no
allowance prices; the grade says so (R6's rule 3), and the row's event
(section 3.2) is what a later batch reads first.

## 3. The catalogue

### 3.1 The rows

Codes in order of date; `sources` are section 8's keys.

| code | subject | central (stage) | window | effect and its figure | sources | waits on |
|---|---|---|---|---|---|---|
| FM3 | Tuberculosis vaccine (M72/AS01E) | 2030 (trial unread: primary completion Apr 2028, + 2) | 2029-2032 | about 50% fewer infected adults go on to active lung TB over three years (13 of 1,626 against 26 of 1,663, phase 2b); the phase 3 of 20,080 reads after 110 cases | X1, X2 | R2f: the long-term sick's TB deaths, which PM6 (streptomycin) already moves |
| FE7 | Sodium fast reactor with heat storage (Natrium) | 2032 (being built since Apr 2026: 2031 + 1) | 2030-2033 | 345 MWe, lifted to 500 MWe for more than 5.5 hours from molten-salt storage (155 MW for 5.5 h, about 850 MWh, calc); about US$4 bn all in (about US$11,600 a kW, calc) | X3 | PW4 (the Nuclear Plant, its fuel bought abroad) and PW1's load blocks, which the storage serves |
| FE8 | Driverless field work | 2033 (autonomous tillage sold; the whole season not shown: 2030 + 3) | 2030-2036 | corn and soybeans with "spring tillage, planting, spraying, harvest and fall tillage all done autonomously": no operator in the tractor or the combine; the remote watch it needs is not stated by the builder | X4, X5, X6 | R6d, after R6b's mark and R2-R's tractor gain |
| FE9 | Tandem solar module at 30% | 2033 (25% modules ship; 30% not made: 2030 + 3) | 2030-2036 | module efficiency 30% against 25% now, a fifth more power from a panel (calc); a life of 30 years against the 15 targeted for 2026 | X7 | PW2's solar farm, and the gap's tandem-module row (Sep 2024, R6-G) |
| FE10 | Electrolysis at 46 kWh a kilogram | 2034 (sold at 55; 46 not shown: 2031 + 3) | 2031-2037 | 46 kWh of electricity a kg of hydrogen for the whole system (72%) against 55 (61%) in 2022; stack life 80,000 hours against 40,000; on FE4's 54 kg a tonne of iron, 2.48 MWh against 2.97 (calc) | X8 | R6d, after R6b's FE4 works |
| FE11 | Fusion power plant | 2034 (test unread: Orion on line 2028, at full power a year later, 2029 + 5) | 2028-2040 | 50 MW or more a plant (Helion) to about 400 MW (CFS); burns nothing bought on the market and needs no deposit; no builder states a cost | X9, X10, X11 | PW1 and PW2: a firm plant in the merit order; its cost a stated assumption or the row held, as R6 said of FE1 |
| FE12 | Superhot rock geothermal | 2035 (drilling at 365 C not shown: 2030 + 5) | 2030-2040 | well pairs and triplets targeting 20 to 40 MW electric (FE3 measured 3.5 MW a well); 250 MW from six wells; up to ten times a usual well's energy above 300 C at the same flow | X12, X13 | FE3's plant (its owner named in R6b's ship file, after PW2) |
| FE13 | Power from orbit | 2035 (nothing beamed at scale: 2030 + 5) | 2030-2040 | 30 MW delivered to the ground in Iceland, bought by Reykjavik Energy, "scaleup into the gigawatt range" planned; no cost stated | X14, X15 | PW1 to PW3: a firm plant in the merit order |

In `research.json`'s other columns (R6's table 3.1). The month is 6, not
known, for every row; the world's text is the central year and the place
(FE7 "projected 2032, Kemmerer, Wyoming"); every row carries the fee. "Opens"
is the year the field's lag (7 years in engineering, 9 in medicine) first
lets a city's own researchers find it.

| code | field | size | grade | work (researcher-years) | opens |
|---|---|---|---|---|---|
| FM3 | MEDICINE | drug | speculative | 7,838 | 2021 |
| FE7 | ENGINEERING | building | likely | 27,618 | 2025 |
| FE8 | ENGINEERING | equipment | speculative | 29,081 | 2026 |
| FE9 | ENGINEERING | equipment | speculative | 29,081 | 2026 |
| FE10 | ENGINEERING | equipment | speculative | 30,623 | 2027 |
| FE11 | ENGINEERING | building | speculative | 30,623 | 2027 |
| FE12 | ENGINEERING | equipment | speculative | 32,246 | 2028 |
| FE13 | ENGINEERING | building | speculative | 32,246 | 2028 |

### 3.2 Row by row, each with the event that moves it

**FM3, the tuberculosis vaccine.** The phase 3 (Gates Medical Research
Institute, started March 2024) enrolled 20,080 people, eleven months ahead of
plan; its registry gives primary completion in April 2028, and the institute
expects results "by 2028 (if not sooner)" (a civil-society report quoting
it). Nobody states a licence date; a modelling paper's assumption of 2029 is
not one. The effect is the phase 2b's measured figure, which the phase 3 was
designed to detect, against no vaccine in adults already infected. WHO's End
TB goals (deaths 90% lower by 2030 and 95% by 2035, against 2015; in 2024
they were 29% lower, against 2025's milestone of 75%) are a path for the care batch, not the row's effect. *Event:*
the phase 3's read.

**FE7, the sodium reactor with heat storage.** TerraPower's Kemmerer 1 has
been built since 23 April 2026, on a construction permit of 4 March; the
builder told the regulator construction would be complete by February 2031
and targets commercial operation in 2030-2031. It was first said for 2028 and
put back two years in December 2022 for want of its fuel, so it has slipped
about three already. What it gives the game is the storage: a base-load
nuclear plant (PW4's) that can add 155 MW to the evening block for 5.5
hours. X-energy's four 80 MWe gas-cooled units for Dow (permit 9 November
2026, start "early next decade") and Kairos's 50 MWe Hermes 2 (2030) are the
window's other builders; the gas-cooled one sells heat to a works, which no
works in the game buys (section 4.1). *Event:* the operating licence
(applied for in 2028) and the end of construction.

**FE8, driverless field work.** Deere's goal, said by the chief executive
of Bear Flag Robotics, its autonomy company, in May 2022, by its vice-president in March 2024
("demonstrate a fully autonomous production system" by 2030) and again at
CES 2025. What is sold is autonomous tillage: 35 tractors for about 15
customers in its first season. On the game's books it is posts: the operator
of each tractor and combine at the Grain Farm and the Mixed Farm. Nobody
publishes the watch it needs; a university's model assumes a person steps in
six minutes an hour (about ten machines a person, calc), an assumption and not a
target, so R6d states one or holds that share. The tractor paid first: R2-R's gain in hands is in
(R1d's call 7, 0.9.126), and R6d built the row on it. *Event:* the
2030 demonstration.

**FE9, the 30% tandem module.** Oxford PV's chief executive, January 2026:
25% now; 26% and a 15-year life in 2026; 27% and 20 years in 2027; 30% and 30
years by 2030; then a point a year to 2032. The game has no solar farm until
PW2 and no tandem module until the gap's row of 2024 (R6-G's). *Event:* the
yearly steps; a 27% missed in 2027 moves the row.

**FE10, electrolysis at 46 kWh a kilogram.** The US energy department's
targets for PEM electrolysis (page of 29 May 2026): the system at 55 kWh a kg
in 2022, 51 targeted for 2026, 46 ultimately; the same page puts hydrogen at
$1 a kg by 2031, the cost the ultimate figures are set for. No builder gives
a date. The department has since merged its hydrogen office into a fuels
office whose page does not mention the shot; the shot's own page gives no
year, and the year 2031 is its programme plan's (page of June 2026). In the
game it is equipment at
FE4's works, whose 3.48 MWh a tonne of liquid steel goes mostly to hydrogen.
*Event:* the department's next status.

**FE11, the fusion plant.** No machine has yet made net electricity.
Helion's Orion (Malaga, Washington; Microsoft the buyer since 2023; two state
licences in June 2026) "aims to be on line by 2028", 50 MW or more after a
year's ramp. CFS's ARC (Chesterfield County, Virginia; Google takes half)
gives about 400 MW "in the early 2030s", after its SPARC shows more fusion
power out than in, set for 2027. Then Britain's STEP, over 100 MW net, first
operation 2040; the US roadmap's commercial fusion "in the mid-2030s";
Japan's demonstration, moved from about 2050 to the 2030s; Europe's DEMO,
after 2054 as said in 2017 (no current date found, so out of the window). The
leader's date is taken, as R6 took Linglong One's. *Event:* SPARC's gain
above one and Orion's first power.

**FE12, superhot rock geothermal.** Quaise at Newberry, Oregon: a first phase
of 50 MW "as early as 2030", its power sold, a second of 250 MW; the first
wells drilled the usual way, its millimetre-wave drill not before the 365 C
wells. The drill has cut 100 m of granite and is nearing 1 km; the pilot was
first said for 2028. On the game's books it is FE3's plant with fewer wells a
MW. *Event:* the first 365 C well.

**FE13, power from orbit.** Space Solar (Britain) with Transition Labs: 30 MW
beamed to Iceland by 2030 for Reykjavik Energy (November 2024; the builder's
own site says it is under construction). Japan's OHISAMA is to beam 1 kW from
orbit in 2026. A 1 GW array "within 25 years" appears in a press article that
names nobody, so it is no target. No cost from anyone: the weakest row here,
kept because it meets the rule. *Event:* OHISAMA's beam.

## 4. Out of the table

Each with what was found and what it waits on; the sheets have the sources.

### 4.1 Dated, with a figure, and nothing in the game for it

| subject | target found | would wait on |
|---|---|---|
| Maglev, Tokyo-Nagoya (Chuo Shinkansen) | at least 2036, first said 2027; 500 km/h, 40 minutes for 286 km | travel between cities: no batch |
| Gas-cooled reactor heat for a works (Xe-100, Dow's Long Mott) | start "early next decade"; 80 MWe and 200 MW of heat a unit | PW4, and a works that buys heat: none queued |
| Thorium molten-salt reactor | a 100 MW demonstration about 2035 (first said 2030) | PW4 and a thorium good; a demonstration is not a commercial plant |
| Deep-sea nodules (TMC) | first production late 2027 if permitted (2030 by the rule); 10.8 Mt of wet nodules a year, a tonne holding about 9.0 kg nickel, 221 kg manganese, 6.5 kg copper and 0.69 kg cobalt (calc) | a sea-floor deposit in the world file: none |
| EU digital VAT reporting (ViDA) | in force 1 Jul 2030 for trade across EU borders, national systems aligned by 1 Jan 2035; VAT fraud "up to EUR 11 billion a year" less; compliance costs over EUR 4.1 bn a year less | the sales tax has no evasion to cut: none |
| Pension age 70 (Denmark, 2040, passed May 2025); 68 in Britain, 2044-46 by statute | the game's people retire at 70 already, so the law gives nothing | a retirement age that moves (HS2 keeps 70): none |
| The digital euro | issuance "during 2029" if its law is adopted in 2026, which it was not by 16 Nov 2026; no holding limit decided (EUR 500 to 3,000 tried as cases) | a retail form of the city's money: none |
| Smoke-free generation (Britain 1 Jan 2027; the Maldives 1 Nov 2025) | before 2030; Britain's assessment models smoking at 14 and over falling from 11.3% (2023) to 1.5% (2056) | smoking as a cause of death: none |
| Driverless vehicles by number (Japan) | 10,000 at level 4 by fiscal 2030, in a draft plan: a count | FE1's (R6b) |

### 4.2 Dated, and no effect target found

| subject | date found | what would make it a row |
|---|---|---|
| Electrolytic iron (Electra; Boston Metal) | a commercial plant "could be operational in 2029" (Electra); Boston Metal's demonstration by 2028 | an energy a tonne on a builder's page (Boston Metal's about 4 MWh a tonne of steel was seen only in a summary); then FE4's works |
| Humanoid robots | Hyundai's Atlas on component assembly by 2030; Figure's line 12,000 a year, Agility's 10,000 | a user's output a robot, or a price; it would take the car plants' welding-robot slot |
| Crewless ships | the IMO's mandatory code in force 1 Jan 2032 (expected); half of Japan's coastal shipping autonomous by 2040 | a crew figure, and a shipping batch |
| Pig-kidney transplant | the EXPAND trial's primary completion Oct 2028; no filing date | its 24-week results; it would end dialysis (M4) for those transplanted |
| Alzheimer's prevention | the two trials' primary completions, Nov 2027 and Dec 2028 | a result read; and dementia, which the game lacks |
| One-time cholesterol edit (VERVE-102) | phase 2 enrolling by the end of 2026; no approval date | a date (its 62% lower LDL at the top dose, in 35 people, is measured) |
| Universal flu vaccine (BPL-1357) | approval targeted for 2029 (US health department, May 2025); no efficacy target | a phase 2 or 3 read; another candidate missed its 50% in Oct 2026 (24%) |
| A new antibiotic class (zosurabalpin) | approval "by the end of the decade" (Roche, 2025); phase 3's start not confirmed | an effect; resistance is a path (R6 §7) |
| The cancer vaccine in lung cancer | phase 3 primary completions Jun 2030, May 2033, Aug 2034 | a target for disease-free survival; FM2's next step |
| Once-yearly HIV prevention (lenacapavir) | primary completion Sep 2028 | an efficacy figure |
| AI at work | China: agents in over 90% of use by 2030; the EU: three firms in four on cloud, big data or AI by 2030 | a productivity target: none found |
| Seawater desalination's energy | Saudi Arabia: 30% less energy across its water sector by 2030, no base year; Singapore 2.0 kWh a m3, no year | a dated figure for a plant |
| Factory-built housing | none with a date and a figure (Britain, Canada, Singapore searched) | a government's dated figure |
| Solid-state car batteries | the makers' 2027-2030 (R6's list, not gathered again) | an electric car |

### 4.3 Goals for an outcome: paths, not rows

The US Cancer Moonshot's age-adjusted cancer death rate halved by 2047 (the
National Cancer Institute's 2026 plan; 44% on the trend); tuberculosis deaths
90% lower by 2030 and 95% by 2035; malaria 90% lower by 2030 (its incidence
higher in 2024 than in 2015); HIV infections and deaths 90% below
2010 by 2030; deaths with resistant bacteria 10% lower by 2030 (the UN);
India's 100 GW of nuclear by 2047 and five small reactors by 2033; the energy
department's cost shots. Each is its owner's path (the care batch's death
rates, the power batches' capacity), as R6 put antibiotic resistance.

### 4.4 Carbon (★4, R6-C)

The EU's 2035 rule for new cars (a 100% cut in law, 90% proposed in December
2025, 87.5% in committee in November 2026); ReFuelEU's synthetic fuel, 1.2%
in 2030 to 35% in 2050; FuelEU Maritime, 6% in 2030 to 80% in 2050; ETS2 in
2028; electric cement kilns (a 1 MW furnace in 2026); carbon capture at
Padeswood, 2029. They join R6's four in R6-C.

## 5. The work rule at these dates (★8)

R6 §8's table holds. At the rows' years a building or a piece of equipment is
27,618 researcher-years (2032) to 32,246 (2035), D$6.0 bn to D$7.0 bn at the
design's check figure, and being first takes a team of 3,945 to 4,607 for
seven years; FM3 is 7,838 (D$1.7 bn). So the design's consequence (§7) is
stronger here: nearly every city receives these rows from the world on their
dates and pays the fee.

## 6. Calls for Jerus

Each with a recommendation; R6d and the owners' batches take the recommended
way unless he says otherwise.

1. **★ The allowance (section 2).** (a) Staged by what had been shown when
   the date was given, as section 2. (b) The builders' dates as stated, as
   R6's ★3 (b) worded it. (c) One allowance for every row, the stated date
   plus three. **Recommended: (a).** Every date traced slipped, and by more
   the less had been shown; (b) puts fusion in 2028 against a record of four
   to twelve years, and (c) treats a reactor being built like a machine
   nobody has run.
2. **★ What a target may be.** (a) A physical figure only (MW, kWh a
   kilogram, efficiency, posts, a share protected); a cost a MWh or a cut in
   cost is noted and is never a row's figure, though a builder's capital cost
   a kW may set the building's price, as Darlington's does for FE6. (b) Cost
   targets too, scaled to the game's prices. **Recommended: (a)**, R6's rule
   4: the game's own prices decide whether a thing pays, and a cost target is
   the price its builder hopes for; (b) would make the energy department's
   shots rows that pay by decree.
3. **★ The mark.** (a) R6b's one mark, `projected` with its `window`; the
   grade stays in the study. (b) A third key, `speculative`, which the card
   prints. **Recommended: (a).** The window's width already says how sure a
   date is (FE7's three years, FE11's twelve), the card names its source, and
   a third key needs its own rule in `ResearchCheck` and changes nothing in
   play.
4. **★ 2036-2060.** The rows end in 2035. (a) Leave the years empty, and read
   the tier again when a named event happens (each row's in 3.2, each of
   4.2's) and whenever `HISTORY_ENDS` moves, adding a row then. (b) Make 4.3's
   goals rows (cancer deaths halved, 2047). (c) Carry the pace on with rows
   nobody has dated. **Recommended: (a).** A goal for deaths is not a
   discovery, and the care batch owns the path; (c) is the design's third
   option (§7), which needs discoveries nobody has made. The cost: a city in
   2036 has nothing new to find, where R6's end left it nothing in 2029.

## 7. For the batches that build them

- **R6d** (queued here, after R6b): FE8 at the Grain Farm and the Mixed Farm
  (equipment: the operator posts of the tractor and the combine; after R2-R
  gives the tractor its gain in hands) and FE10 at FE4's works (the
  electrolyser's kWh a kilogram). Each figure's page is opened again at the
  build, as R6 asked of R6b; FE8's watch share is a stated assumption or held.
- **The power rows**: FE7 with PW4 (and PW1's blocks); FE11 and FE13 with
  PW2's plants and PW3's planner; FE9 with PW2's solar farm, after R6-G's
  tandem row; FE12 with FE3's batch. **FM3** with R2f, beside PM6. They are
  the ship file's pending row R6-T-P, as R6b names FE3, FE5, FE6, FM1 and FM2
  for their owners.
- **Each batch reads its row's event first.** A row whose event has come is
  moved to history at its real date, or dropped if its test failed; a code
  is never reused.

Found on the way, for other batches:

- **R6b's FE4:** Stegra's own 10 TWh a year for 2.5 Mt of steel is 4.0 MWh a
  tonne (calc), against Vogl's 3.48; its start, now early 2027, is still in
  FE4's window.
- **FE6 and PW4:** Darlington's first unit costs C$6.1 bn alone (about
  C$20,300 a kW, calc), the later units C$5 bn falling to C$4 bn.
- **FE3:** Fervo's second phase, 400 MW for 2028, at about US$5,500 a kW (its
  chief operating officer, in the press).
- **R6-G:** R6's ★7 left E19 onward to the gap, but R2e has since used E19
  and E20 (oriented strand board, 1981; wood pellets, 1970), so the gap's
  engineering rows begin at E21. Saudi Arabia's Yanbu plant at 1.55 kWh a
  m3 (reported April 2026) is a measurement, for R6b or R6-G.

## 8. Sources

Keys for the rows' `sources`. Section 4's sources are in the sheets, by
subject. All were seen on the page; "here" marks those opened again from
this session.

| key | source | type | mark |
|---|---|---|---|
| X1 | ClinicalTrials.gov, NCT06062238 (M72/AS01E, phase 3). https://clinicaltrials.gov/api/v2/studies/NCT06062238 | trial registry | page, here |
| X2 | Gates Medical Research Institute, the phase 3's launch. https://www.gatesmri.org/mri-initiates-phase-3-clinical-trial-tuberculosis-vaccine-candidate | announcement | page, here |
| X3 | POWER, Kemmerer 1 enters construction. https://www.powermag.com/terrapowers-kemmerer-1-enters-construction-timeline-of-the-natrium-projects-road-to-first-power/ | press | page, here |
| X4 | Precision Farming Dealer, 26 May 2022, Deere's 2030 system. https://www.precisionfarmingdealer.com/articles/5099-deere-fully-autonomous-corn-soybean-production-system-planned-by-2030 | press | page, here |
| X5 | Western Producer, 14 Mar 2024. https://www.producer.com/crops/deere-reaches-for-fully-autonomous-fleet-by-2030/ | press | page |
| X6 | SAE Media, CES 2025. https://www.mobilityengineeringtech.com/component/content/article/52385 | press | page |
| X7 | pv magazine, 16 Jan 2026, Oxford PV's chief executive. http://www.pv-magazine.com/2026/01/16/oxford-pv-targets-20-year-lifetime-for-perovskite-silicon-tandem-modules-by-2028/ | press | page, here |
| X8 | US Department of Energy, technical targets for PEM electrolysis. https://www.energy.gov/eere/fuelcells/technical-targets-proton-exchange-membrane-electrolysis | government | page, here |
| X9 | American Nuclear Society, 23 Jun 2026, Helion's licences. https://www.ans.org/news/2026-06-23/article-8140/helion-secures-new-licenses-from-washington/ | press | page, here |
| X10 | Commonwealth Fusion Systems, ARC. https://cfs.energy/technology/arc/ | announcement | page, here |
| X11 | UK government, 12 Jun 2025, STEP. https://www.gov.uk/government/news/25-billion-for-world-first-prototype-fusion-energy-plant | government plan | page |
| X12 | ThinkGeoEnergy, 24 Apr 2026, Quaise at Newberry. https://www.thinkgeoenergy.com/quaise-presents-development-plans-for-50-mw-superhot-geothermal-plant-in-oregon/ | press | page, here |
| X13 | Renewable Energy World, 3 Aug 2026, by Quaise's co-founder. https://renewableenergyworld.com/energy-business/new-project-development/what-is-superhot-geothermal-deep-rock-to-baseload-power-explained/ | builder's statement | page, here |
| X14 | CleanTechnica, 9 Nov 2024, Space Solar. https://cleantechnica.com/2024/11/09/a-30-megawatt-space-solar-power-plant-is-scheduled-for-2030/ | press | page, here |
| X15 | AlCircle, 21 Feb 2026, OHISAMA. https://www.alcircle.com/news/ohisama-japans-2026-satellite-mission-to-transmit-wireless-solar-energy-from-space-117374 | press | page, here |
