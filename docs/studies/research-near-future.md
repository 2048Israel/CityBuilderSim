# Research: the near future, 2025-2060

*2026-11-09. A design study, the first half of R6: what today's pilots and
public roadmaps give the research catalogue after 2025, every projected date
marked as one. It changes no code and adds no row to `research.json`; the
rows are R6b's (section 10). Read after `research-design.md` (§2 the rules,
§4 the catalogue, §7 the world) and `docs/queue/R2-notes.md` §1. The
gatherers' fact sheets, with every source and whether its figure was seen on
the page, are in `research-near-future/`.*

## 0. In short

1. **Eight rows stand** on a builder's date and a measured effect. Four have
   already happened since January 2025 (the driverless truck, sulphide heap
   leaching, enhanced geothermal, the mRNA flu vaccine) and four are projected
   for 2027-2028 (the small modular reactor, the hundred-hour battery,
   hydrogen-reduced iron, the personalised cancer vaccine). All eight are
   engineering or medicine. Business and law have none (section 3.4).
2. **The evidence ends about 2030, not 2060.** After 2028 the public record
   holds targets (fusion "in the early 2030s", a pension age of 70 in 2040)
   and nothing measured. Q2 asked for rows to 2060; this study found none it
   could put a figure on after 2028 (★3).
3. **Most of "the near future" has already happened, in years the catalogue
   skips.** Its last row is B8, the online shop, 1995. The design wrote "the
   catalogue ends in the 1990s and real history around now" and Q2 began the
   future at 2025; nobody decided to leave 1996-2024 out. About forty-five
   things belong there, among them shale gas, GM crops, HIV therapy, the solar
   farm, the grid battery, mRNA vaccines and the obesity drugs (section 6, ★5).
4. **Half of the industrial future exists because carbon is priced**, and the
   game has no carbon price and counts no emissions. Seven of the fourteen
   industrial subjects are of that kind, and four give nothing else: among
   them carbon capture at a cement works, one of the design's own three
   examples for this list. With the carbon border tax they are dated and
   real, and in the game they would cost and give nothing (section 3.3, ★4).
5. **Every projected plant date found had slipped, by one to four years; none
   came early** (section 4). A projected row's date is the builder's date
   plus an allowance, never the builder's date alone (★2).
6. **The work rule holds to 2060** (section 8). A 2027 building is 21,333
   researcher-years, about D$4.7 bn, which is the order of what such things
   cost. Being first takes about 3,000 engineers for seven years, so for
   nearly every city these rows will arrive from the world, at the fee.
7. **A near-future row has nothing behind it yet.** A historical row gates a
   building that already stands in `buildings.json`; a hydrogen iron works
   stands nowhere. So R6b is not a data batch: each row ships with what it
   gives, or waits for the batch that owns its mechanism (★6).

Eight calls for Jerus, each with a recommendation, are in section 9.

## 1. What was asked, and how it was done

Q2 (research-design §7, decided 2026-10-18, "rec all"): the world follows
history's dates, "and the catalogue is extended with near-future projections
for 2025-2060, marked as such"; after 2060, nothing. R2's notes made that a
study first, R6, then its rows.

The gathering went to four agents on 2026-11-09, one each for industry and
food, energy and transport, medicine, and business and law: 60 subjects, each
with its state today, the dates its builders give, the slips already on
record, and any measured effect. The dates the table rests on were then
looked up again from the study's own session (`docs/queue/R6-notes.md`,
"Checked here").

Three marks say how far a figure can be trusted, and they are kept in every
table here:

- **page**: the page was opened and the figure was on it.
- **snippet**: the figure was seen only in a search result's summary, which a
  model writes. It names its page; nobody opened it.
- **memory**: from background knowledge, not looked up. Never a row's figure.

**Licence** (`licences.md`'s rule; the game is sold): a date, a capacity, a
trial's result and a statute are facts and nobody's property; they are used
with the source named and no wording copied. A forecaster's estimate (a
consultancy's cost curve, an agency's scenario) is its compiler's, so none is
a row's figure: every figure in table 3.1 is a builder's own statement, a
regulator's record, a trial's result or a paper's finding. No dataset is
shipped.

## 2. The rules a row is held to

1. **The date is the first commercial-scale operation**, as the catalogue's
   dates are (Linz 1952, Crawfordsville 1989). For a drug it is the first
   approval (statins, 1987); for a law, the day it is in force.
2. **A row needs two things: a date from its builder or regulator, and an
   effect measured at pilot scale or above.** A target is not a measurement.
3. **Grades.** *Firm*: it has happened. *Likely*: it works at pilot scale or
   its trial met its endpoint, and the first commercial unit is being built or
   filed; the date may slip. *Speculative*: the deciding test has not been
   read. Speculative subjects are the watch list (section 5), not rows.
4. **The effect is a physical recipe**, what goes in and comes out, as the old
   rows' are ("reheating 2.0 to 0.5-0.8 MBtu a tonne"). The game's own prices
   then decide whether it pays.
5. **A projection is marked**, and carries its window and the day it was read
   (2026-11-09). When its real date arrives a later batch corrects the row;
   the code never changes.
6. **2025 and later only.** What first ran before 2025 is the gap's (section
   6), however new it feels.

By the lag rule (§2: the science exists 7 years before the world's date in
engineering, 9 in medicine) these rows open to a city's own researchers
between 2017 and 2021, which is about when their pilots began.

## 3. The catalogue

### 3.1 The rows

In `research.json`'s columns. `study` is "research-near-future 3" for all;
`sources` are the keys of section 11. Codes begin F (★7).

| code | name | field | kind | year, month (known) | world | projected (window) | grade | work |
|---|---|---|---|---|---|---|---|---|
| FE1 | Driverless truck | ENGINEERING | equipment | 2025, 5 (yes) | May 2025, Dallas to Houston | no | firm | 19,239 |
| FE2 | Sulphide heap leaching | ENGINEERING | recipe | 2025, 11 (yes) | Nov 2025, Johnson Camp, Arizona | no | likely | 9,620 |
| FE3 | Enhanced geothermal | ENGINEERING | building | 2026, 9 (yes) | Sep 2026, Cape Station, Utah | no | firm | 20,259 |
| FM1 | mRNA flu vaccine | MEDICINE | drug | 2026, 8 (yes) | Aug 2026, United States | no | firm | 6,830 |
| FE5 | Hundred-hour battery | ENGINEERING | building | 2027, 6 (no) | projected 2027, Cambridge, Minnesota | yes (Dec 2026 to 2028) | likely | 21,333 |
| FE6 | Small modular reactor | ENGINEERING | building | 2027, 6 (no) | projected 2027, Hainan; Ontario by end 2030 | yes (2027 to 2030) | likely | 21,333 |
| FM2 | Personalised cancer vaccine | MEDICINE | drug | 2027, 6 (no) | projected 2027, after surgery for melanoma | yes (2027 to 2028) | likely | 7,069 |
| FE4 | Hydrogen-reduced iron | ENGINEERING | building | 2028, 6 (no) | projected 2028, Boden, Sweden | yes (2027 to 2030) | likely | 22,463 |

| code | touches | effect | needs | requires | fee | sources |
|---|---|---|---|---|---|---|
| FE1 | Truck Depot | no driver in the cab on a fixed motorway lane; a driven truck is held to 11 hours' driving in a 14-hour day; cost a mile not published | none | | yes | N1, N2, N3 |
| FE2 | Leach & SX-EW Plant | sulphide ore to cathode on the heap, with no concentrator, smelter or refinery; recovery up to 85% (the builder's ceiling); 71 L of water a kg of copper against about 130 | E12 | | yes | N4, N5 |
| FE3 | a power plant (new): firm, burns nothing, stands on hot rock, not on a field | 3.5 MW a well (pilot, 2023); a well US$9.4m falling to 4.8m, the fastest drilled in 21 days; first block 33 MW | none | the merit order (PW1) | yes | N6, N7 |
| FM1 | flu among those 50 and over | 26.6% fewer flu cases than the standard shot (95% CI 16.7 to 35.4; 40,805 adults); 27.4% at 65 and over | the mRNA vaccine of 2020 (the gap) | | yes | N17, N18, N19 |
| FE5 | grid storage (new) | 100 hours at rated power (1.5 MW, 150 MWh); gives back about 40 to 50% of what it takes, lithium-ion 85 to 90% (press); cost not published | the four-hour battery of 2017 (the gap) | storage in the merit order | yes | N11, N12, N13 |
| FE6 | Nuclear Plant (PW4): a small one | 125 MWe (Linglong One) to 300 MWe (BWRX-300) a unit; Darlington's four: C$20.9 bn for 1,200 MWe, about C$17,400 a kW, 5.6 years from decision to grid as planned | the reactor of 1957 (no code yet) | PW4 | yes | N14, N15, N16 |
| FM2 | cancer deaths: melanoma after surgery, so far | recurrence or death 49% lower at five years on top of the standard drug (phase 2b); phase 3, 1,137 patients, met its endpoint in Aug 2026, hazard ratio 0.58 as reported | mRNA vaccine 2020, checkpoint immunotherapy 2011 (the gap) | | yes | N20, N21 |
| FE4 | an iron works (new) feeding the Electric Arc Furnace, in place of Blast Furnace and Oxygen Steel Shop | 3.48 MWh of electricity a tonne of liquid steel, most of it making hydrogen (54 kg a tonne of iron by the chemistry); no coke | PE1, E4 | | yes | N8, N9, N10 |

### 3.2 Row by row

**FE1, the driverless truck.** Aurora began carrying freight with nobody in
the cab between Dallas and Houston in the last days of April 2025 and said so
on 1 May (snippet); Fort Worth to El Paso, about 600 miles, followed about
October 2025; by its third-quarter 2026 results it counted over 100,000
driverless miles, ten customers and some twenty trucks (snippet). An observer
rode in some cabs at the truck maker's request, and the hardware meant to end
that was due in 2026. Its own targets have slipped by months (200 trucks by
the end of 2026 is now the end of February 2027); a thousand trucks by the end
of 2027 is a target. **Nothing is published on cost.** What is on record is
the rule the truck escapes: a driver may drive 11 hours in a 14-hour day and
must then rest 10 (49 CFR 395.3; memory, R6b reads it). On the game's books:
the depot's driver posts for each truck, and the hours a truck works in a day.
The staff who watch the trucks from afar and the price of the kit are not
published, so R6b either takes a stated assumption or holds the row.

**FE2, sulphide heap leaching.** E12's leaching and electrowinning (Bluebird,
1968) was for oxide ore; most of the world's copper is in primary sulphides, which have
needed a concentrator, a smelter and a refinery. Rio Tinto's Nuton made its
first cathode at Gunnison Copper's Johnson Camp in November 2025 (page:
release of 4 December): about 30,000 tonnes over four years, some 14,000 of
them by Nuton's own process; recovery "up to 85%" is a ceiling, not an
average. *Likely, not firm:* a catalysed leach (Jetti) has run on old dumps at
Pinto Valley since mid-2019 and its output later fell as the ore sealed
itself (snippet), so the date is arguable (the gap's study may move the row to
2019) and nobody has yet shown it at a whole mine's scale. On the game's
books it is a real change: a copper city makes cathode at the mine and needs
no Copper Smelter for sulphide ore.

**FE3, enhanced geothermal.** Fervo's Cape Station, Beaver County, Utah: first
power on 24 September 2026 from the first 33 MW block (page); the first phase
is 100 MW in three blocks, the second 400 MW for 2028. What is measured is
drilling: 3.5 MW a well at the 2023 pilot, and the first four horizontal wells
at Cape falling from US$9.4m to US$4.8m each, the fastest in 21 days
(snippet, the builder's release). The plant's own cost a MWh is not
published; a US government target of $45 a MWh by 2035 is a target. A
closed-loop rival (Eavor, Geretsried) gave about 0.5 MW against a design of
8.2 and is not the row. On the game's books it is the one plant that is firm,
burns nothing and needs no deposit, at a cost in wells. It means nothing until
power is sold by the merit order (PW1), and Earth's data holds no map of hot
rock, so where it may stand is a question for the batch that builds it.

**FM1, the mRNA flu vaccine.** The FDA approved Moderna's on 5 August 2026 for
adults of 50 and over: in full for 50 to 64, and conditionally for 65 and over
until a confirming trial reads (snippets, three reports, checked here). The
figure is against the ordinary flu shot, not against none: 26.6% fewer cases
among 40,805 adults. So the row improves a shot the game's people may already
get, and which constant that is belongs to the care batch (R2f). It needs the
mRNA vaccine itself (December 2020), which is in the gap.

**FE5, the hundred-hour battery.** Form Energy's iron-air battery for Great
River Energy at Cambridge, Minnesota: 1.5 MW for 100 hours. It was due in
2023, then late 2025; the utility's May 2026 plan says the end of 2026 and a
report of October 2026 says 2027 (snippets, checked here). The builder has
published neither its cost nor what it gives back; the press says about 40%
in one report and 40 to 50% in another, against 85 to 90% for lithium-ion.
Its use is a calm week on a grid of wind and sun. The game has no storage at
all: spec-electricity's batches leave even the four-hour battery out. The row
waits for a storage mechanism and for the four-hour battery's own row, which
is the gap's (2017).

**FE6, the small modular reactor.** China's Linglong One (125 MWe) on Hainan
poured its first concrete in July 2021 on a 58-month plan; it missed its
target of the first half of 2026, was in hot tests through 2026 (page) and
had loaded no fuel by September (snippet, checked here); the trade press now
says early 2027. Ontario's Darlington (four of 300 MWe) was licensed in April
2025 and decided on 8 May 2025, the first unit due on the grid by the end of
2030 (page). The only cost on record is Darlington's budget, C$20.9 bn for
1,200 MWe: dearer a kilowatt than a large reactor. What the row gives is the
smaller lump and, as planned, the shorter build. *A definition:* two small
reactors already sell power, Russia's floating Akademik Lomonosov (2020) and
China's gas-cooled HTR-PM (December 2023; both memory). This row is the
factory-built water-cooled module ordered in series; if the gap's study takes
the wider meaning, the row is its, dated 2020 or 2023. It needs the reactor
of 1957, which is research-design's reserve [T48] and has no code; PW4 brings
the Nuclear Plant from 1957.

**FM2, the personalised cancer vaccine.** A vaccine made for one patient from
their own tumour's mutations (Moderna and Merck's), given with the standard
immunotherapy after surgery for melanoma. The phase 2b trial at five years:
recurrence or death 49% lower (hazard ratio 0.51, 0.29 to 0.89; snippet). The
phase 3 trial, 1,137 patients, met its endpoint, announced 19 August 2026
(snippet, checked here); the detail as two press reports give it is a hazard
ratio of 0.58 (0.45 to 0.75) at a median 24.6 months, with no difference in
the first six. The companies gave no filing date, so 2027 is this study's
projection: a result, then about a year's review. It is one cancer, after
surgery: a small share of cancer deaths. Trials in lung, bladder and kidney
cancer are running, and the row's effect grows only as they read.

**FE4, hydrogen-reduced iron.** Iron ore reduced by hydrogen made with
electricity, then melted in an electric arc furnace: no coke, no blast
furnace. The pilot (HYBRIT, Sweden) ran until 2024 (page). The first
commercial plant, Stegra's at Boden, is over 60% built with 740 MW of
electrolysers installed; its first steel was due in 2025, then 2026, then
2027, and since June 2026, when a EUR 1.4 bn rescue closed, the timetable is
"under review" (snippet, checked here). Salzgitter's first stage is also due
in 2027; ArcelorMittal cancelled two such plants in June 2025 and thyssenkrupp
postponed its hydrogen tender. The figure is a paper's model from pilot data:
3.48 MWh a tonne of liquid steel (snippet of the abstract, checked here). **It
does not pay everywhere:** the same paper puts it level with the blast furnace
only at a carbon price of EUR 34 to 68 a tonne with power at EUR 40 a MWh. In
the game, with no carbon price, it pays where power is cheap or coking coal
cannot be had, and nowhere else. That is an honest row, and its harness should
show a city of each kind.

### 3.3 Dated and real, and paying only where carbon is priced

No source file in the game mentions carbon or emissions, and spec-electricity
left carbon out of its run. These rows meet rules 1 to 3 and fail to matter:
each costs fuel, power or money and gives back only carbon not emitted. They
wait on ★4 and take no code until then.

| name | field, kind | world | effect | grade | sources |
|---|---|---|---|---|---|
| Carbon capture at a cement works | ENGINEERING, equipment (Cement Plant, Large Cement Plant) | Jun 2025, Brevik, Norway | 400,000 t of CO2 a year, half the works' emissions, about 0.4 t a tonne of cement; the energy and the cost a tonne not found | firm (snippet; the date also memory) | N24 |
| Carbon border tax | LAW, recipe | Jan 2026, European Union | importers of cement, iron and steel, aluminium, fertiliser, electricity and hydrogen pay the Union's carbon price on what the goods emitted: 2.5% of it in 2026 rising to all of it in 2034; the first price EUR 75.36 a tonne of CO2 | firm (page; the price snippet) | N25, N26 |
| A carbon price on heating and road fuel | LAW, recipe | projected 2028, European Union | by statute, put back from 2027 in December 2025; no price yet | firm as a statute (snippet) | N27 |
| Synthetic fuel quotas | LAW, recipe | 2025 to 2050, European Union | by statute: 2% of aviation fuel sustainable from 2025, 6% from 2030, 70% by 2050 (memory); the fuel itself costs several times kerosene | firm as a statute | - |

Hydrogen-reduced iron (FE4) is in table 3.1 and not here because its recipe
replaces coke with power: a city short of coking coal has a use for it at any
carbon price. Calcined-clay cement is in the gap for the same reason: it
burns less fuel.

### 3.4 Why business and law have no row

Every business and law subject found is one of three things: already running
before 2025 and so the gap's (AI assistants, drone delivery, warehouse robots,
instant payments, the pension age tied to life expectancy, the congestion
charge, upzoning, the law that lets driverless vehicles on the road); a
carbon law (3.3); or without any measured effect (AI agents doing whole jobs,
where the figures are the sellers' and one firm that replaced its support
staff took people back; shops without cashiers; factories without workers;
the four-day week; a basic income; the AI laws, which are dates only).

## 4. How dates slip

Every projected date for a plant that this study could trace back had been
later than first said. The one thing that came early was a trial's enrolment.

| what | first said | now | late by | mark |
|---|---|---|---|---|
| Stegra, Boden: first steel | 2025 | 2027, "under review" since Jun 2026 | 2 years, open | snippet |
| Form Energy, Cambridge: in service | 2023 | end of 2026 (the utility) or 2027 (press) | 3 to 4 years | snippet |
| Linglong One: commercial operation | first half of 2026 | no fuel by Sep 2026; early 2027 (press) | about 1 year | snippet |
| Darlington, first unit | 2028 (memory) | end of 2030 | 2 years | page |
| ITER: fusion fuel | 2035 | 2039 | 4 years (first plasma: 9) | snippet |
| The Metals Company: first nodules | 2024 (memory) | end of 2027, if permitted | 3 years | snippet |
| Salzgitter: decision on stages 2 and 3 | 2026 | 2028-29 | 2 to 3 years | snippet |
| EU carbon price on heating and road fuel | 2027 | 2028 | 1 year, by law | snippet |
| Aurora: driverless launch | end of 2024 (memory) | May 2025 | 4 months | snippet |
| Aurora: 200 trucks | end of 2026 | end of Feb 2027 | 2 months | snippet |
| Eavor, Geretsried: first power | first half of 2025 | late 2025, at 0.5 MW of 8.2 | months, and short | snippet |
| NuScale's Idaho plant | 2029-30 | cancelled Nov 2023 | never | snippet |
| ArcelorMittal, Bremen and Eisenhüttenstadt | - | cancelled Jun 2025 | never | snippet |
| M72 vaccine trial: enrolment full | 2026 | Apr 2025 | 11 months early | page |

**The allowance this study used** (★2): a plant in commissioning, the stated
date, or the next year if that date is within six months or already past; a
plant under construction, the stated date plus one year; a plant not yet under
construction, no row. A drug with its trial read, one year for the review; a
drug with its trial unread, no row. The window runs from the builder's own
date to the latest date any builder of the same thing gives.

## 5. The watch list

Dated by their builders, and each short of rule 2: the deciding test has not
been read, or nothing is measured. Each names the event that would make it a
row. None is for R6b.

| subject | the event | builders' dates | measured today |
|---|---|---|---|
| TB vaccine (M72/AS01E) | its phase 3 result: 20,080 people, registry's primary completion Apr 2028 (page) | a licence about 2029 to 2031 (this study's guess; nobody states one) | 49.7% fewer cases over three years in infected adults, phase 2b, 3,575 people; the 95% interval runs from 2.1 to 74.2 (snippet, checked here) |
| Fusion power | a plant selling power | net energy in a test machine 2027 (page); a 400 MW plant "early 2030s"; 50 MW to one buyer by 2028; Britain's plant 2040; ITER's fuel 2039; Europe's plant in the 2050s | no plant, no cost, no output figure anywhere |
| Electrolytic iron | a commercial plant | 2029 (one builder, page); demonstrations of 500 t a year in 2026 | no kWh a tonne published |
| Pig-kidney transplant | the first trial's result: registry's primary completion Oct 2028 (page) | "5 to 10 years", one expert | the longest any has worked: 271 days |
| Alzheimer's prevention | two trials in people without symptoms, reading Nov 2027 and Dec 2028 (registry) | none | nothing yet; the drugs slow decline by 27% once it has begun (the gap) |
| One-time cholesterol edit | a phase 3 trial; none has begun | none | early trials only |
| Universal flu vaccine | an efficacy trial | a US target of approval in 2029 | a 45-person safety trial |
| A new antibiotic against resistant bacteria | a phase 3 result | "by the end of the decade", its maker's hope | none |
| Deep-sea nodules | a permit: the US decision due early 2027; no international code before 2027 | end of 2027 if permitted | the ore's grade (about 1.4% nickel, 1.2% copper, 31% manganese); the game has no seabed |
| Humanoid robots | an output-per-worker figure from a user | none | one warehouse: 100,000 totes moved |
| AI agents doing whole jobs | an independent measure | none | sellers' figures only |
| Solid-state batteries | a car in series production | 2027 to 2030, the makers' | nothing; and the game has no electric car |

M72 is on this list and the cancer vaccine in the table because one trial has
read and the other has not. Two trials gathered here failed in 2026 as their
dates came (a blood test for many cancers in May, a heart drug in September,
both as reported), which is what an unread trial can do.

## 6. The gap, 1996-2024

The catalogue's last rows are E18 (1989), M10 (1987), B8 (1995) and L4 (1986).
Nothing in it is dated between 1996 and 2024. The design did not decide that:
§7 says "the catalogue ends in the 1990s and real history around now", and Q2
then began the future at 2025. Its own reserves (§4, "sourced but left out")
already hold three of the gap's largest: slickwater fracturing in horizontal
wells (1997), GM crops (1996; yields +22%) and bigger wind turbines.

Why it matters to this study: three of the eight rows need a gap row (FE5 the
four-hour battery, FM1 and FM2 the mRNA vaccine), and a city would otherwise
go from the online shop straight to the driverless truck. And the gap is the
easy part: its dates are history, so every row is firm.

### 6.1 Found on the way, with a figure

Gathered while looking for the future. The figures are the gatherers'
(`research-near-future/`); the cell names where each comes from. A start for
the gap's own study, which sources them afresh.

| subject | first commercial | measured | mark |
|---|---|---|---|
| Robotaxi | Oct 2020, Phoenix | 271 million miles with no driver to Jun 2026; 95% fewer serious-injury crashes than drivers on the same roads (the operator's figures); cost not published | page |
| Battery long-haul truck | Dec 2024, series production | 1.06 kWh a km; power about $0.17 a mile against diesel $0.50 to 0.70 (a press estimate) | snippet |
| Floating wind farm | Oct 2017, Scotland | 382 MW worldwide by Oct 2026; $7,349 a kW against $5,441 fixed to the seabed (NREL's 2023 models) | page |
| Tandem solar module | Sep 2024 | 24.5% module efficiency as shipped; outdoor lifetime not yet shown | snippet |
| Vanadium flow battery | 2022; the first of 1,000 MWh in Jan 2026 | 200 MW for five hours | snippet |
| Sodium-ion battery | 2021 to 2026 | 160 to 175 Wh a kg; cost not found | snippet |
| Rotor sails on ships | 2018 | fuel 6.3 to 9.4% lower across suitable ships; 9.1% over a year on one | snippet |
| Low-energy reverse osmosis | 2005 on (the design's reserve has RO at 1977) | 2.3 kWh a m3 for a whole plant at best, against 2.5 to 3.5 | snippet |
| Calcined-clay cement | about 2020; a 400,000 t works in Ghana, 2026 | clinker 35% of the cement; 310 kg of CO2 a tonne against 780 | page |
| Mass timber towers | 2021 building code, 18 storeys; a 25-storey tower in 2022 | storeys allowed; build time not sourced | snippet |
| Gene-edited crops | 2019, United States | no field-trial yield figure found | snippet |
| mRNA vaccines | Dec 2020 | not gathered (FM1 and FM2 need the row) | memory |
| Obesity drugs (GLP-1) | Jun 2021; as a pill Dec 2025 | heart attack, stroke or cardiovascular death 20% lower in 17,604 adults (SELECT) | snippet |
| Alzheimer's antibodies | Jul 2023 | 27% less decline over 18 months, 1,795 patients (CLARITY AD) | snippet |
| RSV vaccines at 60 and over | May 2023 | RSV hospital stays 75 to 77% lower (pooled studies, 1.5 million adults) | snippet |
| Gene editing as a treatment | Dec 2023 | pain crises ended in the trial's sickle-cell patients; small groups | snippet |
| Hospital care at home | Nov 2020, Medicare's waiver, now to Sep 2030 | cost 38% lower, readmission 7% against 23% (randomised, 91 patients; Levine 2020) | snippet |
| Six-month cure for resistant TB | 2022, WHO | 6 months against 9 to 24; success 89% against 52% | snippet |
| AI-read breast screening | 2023 to 2026, Sweden | radiologists' reading 44% lower; sensitivity 80.5% against 73.8% (MASAI) | snippet |
| AI assistants at work | Nov 2022 | cases resolved an hour +15% among 5,172 support agents (Brynjolfsson, Li and Raymond); 19.8% of US firms use AI (May 2026) | snippet |
| Drone delivery | 2019 to 2022 | over 2 million deliveries by one operator; cost not published | snippet |
| Warehouse robots | 2012 | one firm's millionth robot in Jul 2025; no staff-per-parcel figure published | snippet |
| Instant payments | 2008, Britain; 2020, Brazil | 318 million payments in a day (Brazil) | snippet |
| Invoices reported to the tax office as issued | 2019, Italy | no study isolates its effect | snippet |
| Pension age tied to life expectancy | 2006, Denmark | by its 2025 law: 68 in 2030, 69 in 2035, 70 in 2040 | snippet |
| Congestion charge | 2003, London; New York 5 Jan 2025 | New York's first year: entries 11% lower, subway +9%, about $0.55 bn raised | snippet |
| Upzoning | 2016, Auckland | 21,800 more homes consented in five years; rents 26 to 33% under where they would have been after six | snippet |
| Driverless vehicles allowed on the road | 2021, Germany; a UN regulation adopted 24 Jun 2026 | no measured effect of the law itself | snippet |
| Carbon price | 1990-91, Finland and Sweden; 2005, the EU's market | see ★4 | memory |

### 6.2 Named only

Not gathered here; from the design's reserves and from memory, so that the
gap's study starts with them: slickwater fracturing in horizontal wells
(1997, [T23]); GM crops (1996, [T44]); bigger wind turbines ([T49]); the solar
farm at utility scale; the four-hour grid battery (2017); driverless haul
trucks in mines (2008); the smartphone (2007) and rented computing (2006);
combination therapy for HIV (1996); targeted cancer drugs (2001) and
checkpoint immunotherapy (2011); the hepatitis C cure (2013); the HPV vaccine
(2006); the coated stent (2003); robot surgery (2000); the smoking ban (2004).

## 7. Reserves, with reasons

- **Carbon only, beyond 3.3:** kerosene and methanol made from hydrogen and
  captured CO2; ammonia-fuelled ships (the first ocean-going one was delivered
  on 10 June 2026); electric cement kilns.
- **Nothing in the game for it to touch:** electric air taxis; maglev;
  crewless ships; a central bank's digital money (a euro pilot from late
  2027).
- **No measured effect found:** cultivated meat; printed and factory-built
  homes; driverless tractors; and 3.4's list.
- **Date unsettled:** the battery locomotive; powered motorway lanes for
  trucks.
- **Not a discovery:** antibiotic resistance. It is a cost that rises (a
  forecast of 1.9 million deaths a year by 2050), a path and not a row; where
  it belongs is the care batch's question.

## 8. The work rule, to 2060

`ResearchCatalog.workByRule`: 400 researcher-years for a building or a piece
of equipment at 1950, 200 for a recipe, 500 for a drug, growing 5.3% a year
(medicine 3.5%). The cost is at the design's check figure of D$218,000 an
engineer-year; the last column is the team that finishes a building in the
seven years between its science and the world's date.

| year | building, equipment | recipe | drug | a building's cost | team to be first |
|---|---|---|---|---|---|
| 1995 (B8, for scale) | 4,086 | 2,043 | 2,351 | D$0.9 bn | 584 |
| 2025 | 19,239 | 9,620 | 6,599 | D$4.2 bn | 2,748 |
| 2027 | 21,333 | 10,666 | 7,069 | D$4.7 bn | 3,048 |
| 2030 | 24,907 | 12,454 | 7,838 | D$5.4 bn | 3,558 |
| 2035 | 32,246 | 16,123 | 9,309 | D$7.0 bn | 4,607 |
| 2040 | 41,746 | 20,873 | 11,056 | D$9.1 bn | 5,964 |
| 2050 | 69,968 | 34,984 | 15,596 | D$15.3 bn | 9,995 |
| 2060 | 117,268 | 58,634 | 21,999 | D$25.6 bn | 16,753 |

The 2027 figures are of the right order: a reactor design has cost its maker
one to two billion dollars to bring to a licence, and a new drug is usually
put at one to three (both memory; 7,069 researcher-years is D$1.5 bn at the
same rate). So the rule is kept
(★8). Its consequence is the design's own (§7): most cities are importers. A
city needs three thousand engineers on one problem for seven years to be
first to a 2027 building; the rest receive it from the world on its date and
pay the fee, 25% of what it adds for 20 years (§2.4).

## 9. Calls for Jerus

Each with a recommendation; R6b takes the recommended way unless he says
otherwise.

1. **★ How a projected date behaves in the game.** (a) A fixed date, the
   central one, with the row marked "projected" wherever its date is shown and
   the window kept in the data. (b) A date drawn from the window by the
   city's seed, so each city's future differs. (c) No world date: only the
   city's own research finds it. **Recommended: (a).** The world follows
   history's dates (★W1) so that every city lives the same history and every
   harness can name a month; (b) breaks that for a little colour and (c) makes
   the rows unreachable for nearly every city (section 8).
2. **★ The date rule for a projection.** The first commercial-scale
   operation, dated at the builder's date plus section 4's allowance, with the
   window beside it. **Recommended: yes.** Every plant date traced had slipped;
   the builder's date alone would be wrong on the record.
3. **★ Where the catalogue ends.** Q2 said 2060. The rows this study can put
   a figure on end in 2028. (a) End at the evidence: eight rows now, and the
   watch list read again whenever one of its named events happens, a row
   added then. (b) Add the watch list now as a second tier, "speculative", at
   the builders' dates (fusion about 2035-2040, the pig kidney about 2032),
   with effects that are the builders' targets. **Recommended: (a).** A row's
   effect is a measured figure everywhere else in the catalogue, and the
   model cannot build on a target; two of the trials gathered here failed
   this year. The cost is that a city in 2040 has nothing left to find, as a
   city in 1996 has nothing today.
4. **★ Carbon.** (a) The carbon rows (3.3) wait, and whether the game prices
   carbon at all is a study of its own. (b) Ship them as rows that cost and
   give nothing. **Recommended: (a).** Carbon capture at a cement works was
   one of the design's three examples for this list, so it is said plainly:
   without a carbon price it is a pure cost, and no city would build it.
5. **★ The gap, 1996-2024.** (a) A study of its own, as research-1900 was for
   1900-1950, starting from section 6, and before R6b's rows that need it.
   (b) Leave the gap. **Recommended: (a), before R6b's FE5, FM1 and FM2.** It
   is the larger half of what Q2 wanted, its dates are history, and it holds
   the rows the near future stands on.
6. **★ A row with nothing behind it.** A historical row gates a building that
   already stands in `buildings.json`. A near-future row has no building, so
   as data alone it is a name a city pays D$4.7 bn to find. (a) A row enters
   `research.json` in the batch that builds what it gives. (b) All eight as
   data now, their mechanics later, as R2 did. **Recommended: (a).** It makes
   R6b three rows whose mechanisms exist (FE1, FE2, FE4) and sends FE3, FE5
   and FE6 to the power batches and FM1 and FM2 to the care batch.
7. **★ Codes.** FE, FM, FB, FL for rows from 2025, leaving E19, M11, B9 and
   L6 onward to the gap. **Recommended: yes.** Two batches drawing codes from
   one sequence would collide, and a code is permanent.
8. **★ The work rule to 2060.** **Recommended: keep it** (section 8).

## 10. The brief for R6b

On the recommended ways. By `docs/batch.md` 3 it may be two batches: the mark
with FE1 and FE2, then FE4's works.

**What.**
1. *The mark.* `research.json` learns two optional keys, `"projected": true`
   and `"window": "2027-2030"`; `ResearchCatalog.Entry` reads them, and a row
   without them is history. One dial, `ResearchCatalog.HISTORY_ENDS`, the
   month the catalogue was last read against the world (2026-11).
   `ResearchCheck` holds every row to it: dated after it, the row is
   projected; on or before it, it is not. Gson leaves a missing key alone and
   a save holds codes, so there is no `SAVE_FORMAT` change.
2. *Where it shows.* Wherever an entry's date or `world` text is printed:
   the research screen's card and timeline (`ui/ResearchScreen`,
   `ResearchView`, `ResearchPreview`), the inbox's line for a discovery from
   abroad (`Inbox`, about line 263) and the year book (`YearBook`). A
   projected row says "projected" there, with its window.
3. *The rows, each with what it gives.* FE1 at the Truck Depot (the driver
   posts for a truck, the hours a truck works; the oversight staff and the
   kit's price are unpublished, so a stated assumption or the row is held).
   FE2 at the Leach & SX-EW Plant (sulphide ore to cathode with no smelter;
   first read whether the game's plant is held to oxide ore at all). FE4 as a
   new template in the steel chain (pellets and power in, iron for the
   Electric Arc Furnace out, at 3.48 MWh a tonne of liquid steel), with a
   harness city where it pays and one where it does not. Each row's figures
   are sourced at the build, as R1c's and R2d's are; table 3.1 is the physical
   recipe and section 11 its sources.
4. *The rest, named in its ship file* as R2 named R2b to R2f: FE3 and FE6
   after PW2 and PW4; FE5 after a storage batch; FM1 and FM2 after R2f and the
   gap's mRNA row.

**Done looks like.** `ResearchCheck`'s count moves from 89 by the rows added
(its section 1 says 89 in four assertions); a section proves the mark against
`HISTORY_ENDS`; the screens' harnesses (`ResearchViewCheck`,
`ResearchPreviewCheck`, `KeptCheck`) show the word. The default playtest runs
to 2100 on a 1900 calendar, and rows that open from 2018 give idle
researchers work, so the trace may move after about month 1,400: compare it,
and run the ensemble if it does (EN1).

**Not R6b's.** The carbon rows (★4), the watch list (★3), the gap (★5).

## 11. Sources

Keys for `research.json`'s `sources`. The type is the source's own kind; the
mark is how this study saw it. The gatherers' sheets in
`research-near-future/` hold the rest, numbered per subject.

| key | source | type | mark |
|---|---|---|---|
| N1 | TechCrunch, 1 May 2025, on Aurora's launch. https://techcrunch.com/2025/05/01/aurora-launches-commercial-self-driving-truck-service-in-texas | press | snippet |
| N2 | Aurora, third-quarter 2026 results (republished). https://www.webull.com/news/15827710638425088 | announcement | snippet |
| N3 | 49 CFR 395.3, hours of service. https://www.ecfr.gov/current/title-49/section-395.3 | statute | memory |
| N4 | Rio Tinto, 4 Dec 2025. https://www.riotinto.com/en/news/releases/2025/rio-tintos-nuton-technology-produces-first-copper | announcement | page |
| N5 | Mining.com on Jetti at Pinto Valley. https://www.mining.com/jetti-resources-quest-for-copper-minings-holy-grail/ | press | snippet |
| N6 | Fervo Energy, first power at Cape Station. https://fervoenergy.com/fervo-energy-achieves-first-power-at-cape-station-a-landmark-moment-for-the-future-of-enhanced-geothermal-systems/ | announcement | page |
| N7 | Fervo Energy via Business Wire, Feb 2024, drilling. https://www.businesswire.com/news/home/20240212763499/en | announcement | snippet |
| N8 | Argus, Stegra's funding and timetable. https://www.argusmedia.com/en/news-and-insights/latest-market-news/2843412-stegra-closes-eu1.4bn-funding-for-low-carbon-steel-plant | press | snippet |
| N9 | Vogl, Åhman and Nilsson, "Assessment of hydrogen direct reduction for fossil-free steelmaking", Journal of Cleaner Production 203 (2018) 736-745. https://lup.lub.lu.se/record/47f58242-a20b-4d85-a43d-92c5904590c1 | paper | snippet of the abstract |
| N10 | LKAB, the HYBRIT demonstration project. https://lkab.com/en/what-we-do/our-transformation/carbon-free-processes/hybrit-demonstration-project/ | announcement | page |
| N11 | Great River Energy, Cambridge Energy Storage Project. https://greatriverenergy.com/what-we-do/cambridge-energy-storage-project | announcement | snippet |
| N12 | MIT Technology Review, 6 Oct 2026, on Form Energy. https://www.technologyreview.com/2026/10/06/1145020/2026-climate-tech-companies-to-watch-form-energy-iron-batteries/ | press | snippet |
| N13 | GCN, Oct 2026, on the iron-air battery's efficiency. https://gcn.com/?p=22820 | press | snippet |
| N14 | American Nuclear Society, 5 Jun 2026, Darlington. https://ans.org/news/2026-06-05/article-8047/north-american-construction-is-back-smaller-and-faster-at-opgs-darlington/ | press | page |
| N15 | World Nuclear News, hot testing of Linglong One. https://www.world-nuclear-news.org/articles/hot-testing-of-chinese-smr-under-way | press | page |
| N16 | NucNet, hot testing at Linglong-1. https://www.nucnet.org/news/hot-testing-begins-at-china-s-linglong-1-small-modular-reactor-10-3-2026 | press | snippet |
| N17 | FDA, clinical review of 5 Aug 2026. https://www.fda.gov/media/194140/download | regulator | snippet |
| N18 | HCPLive on the approval. https://www.hcplive.com/view/mflusiva-fda-approval-influenza-adults-50-older | press | snippet |
| N19 | BioPharm International on the trial's figures. https://www.biopharminternational.com/view/moderna-mflusiva-approval-validates-mrna-platform-beyond-covid-19-what-it-means-for-biopharmaceutical-manufacturing | press | snippet |
| N20 | Merck and Moderna, 19 Aug 2026. https://www.merck.com/news/merck-and-moderna-announce-phase-3-interpath-001-trial-of-intismeran-autogene-plus-keytruda-met-endpoints-of-recurrence-free-survival-rfs-and-distant-metastasis-free-survival-dmfs-in-patient/ | announcement | snippet |
| N21 | OncLive on the phase 3 figures. https://www.onclive.com/view/intismeran-autogene-plus-pembrolizumab-meets-rfs-dmfs-end-points-in-resected-melanoma | press | snippet |
| N22 | ClinicalTrials.gov NCT06062238 (M72/AS01E, phase 3). https://clinicaltrials.gov/api/v2/studies/NCT06062238 | trial registry | page |
| N23 | Tait et al., "Final Analysis of a Trial of M72/AS01E Vaccine to Prevent Tuberculosis", NEJM 381(25), 2019. | paper | snippet |
| N24 | The Engineer and CemNet on Brevik. https://www.cemnet.com/News/story/179365/heidelberg-materials-launches-milestone-carbon-capture-plant-at-brevik.html | press | snippet |
| N25 | European Commission, the carbon border adjustment mechanism. https://taxation-customs.ec.europa.eu/carbon-border-adjustment-mechanism_en | EU page | page |
| N26 | CMS, the first certificate price. https://cms.law/en/gbr/legal-updates/eu-cbam-first-price-announcement-and-cbam-planning-strategies | law firm's note | snippet |
| N27 | Euronews, 10 Dec 2025, the delay to 2028. https://www.euronews.com/2025/12/10/carbon-tax-on-buildings-and-transport-delayed-to-2028-under-eu-climate-deal | press | snippet |

Most marks are "snippet". Before R6b puts a figure in `research.json` it
opens that figure's page; where the page will not open, the figure is held.
