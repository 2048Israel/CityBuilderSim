# Health: the causes of death the game lacks, and a mental hospital

*MED1, 2026-11-17. This is the health study that research-design's Q17 and R2f's
★10 left the causes of death to. For heart disease, stroke, cancer, diabetes,
kidney failure and polio, it gives Canada's rates from 1900 to today, what moved
them and what the game would need to carry them. It does the same for the mental
hospital that chlorpromazine was waiting for. It ends with the batches that build
them.*

**WHY.** R2f (0.9.127) made each medical row move the constant it moves. Four
rows were left with nothing behind them, because the game had no cause for them
to act on: PM2 insulin, M3 the polio vaccine, M4 dialysis and M7 angioplasty.
Parts of four more rows were left the same way: PM4's recovery, PM5's long-sick
deaths, M5's senior care and M10's coronary deaths (R6 ★6: a row's effect enters
with the batch that builds what it gives). R6-G then left eleven more for this
study: M11, M13-M18, M21 and M22, and L6. Behind all of them is one gap. After
1980 every death in the game comes from `AgeBand`'s base rate or from `Sickness`,
with no cause named, and the base is today's rate in every year from 1900 on. So
the biggest killers of the century (heart disease, peaking in the 1950s, and
cancer, peaking in 1988) are in the game unnamed and flat. Nothing they did can
move, and so no row can move them.

The figures are in five sheets in `health/`, gathered on 2026-11-17. Each figure
is marked page, snippet or memory and keyed to a source with its licence:

| sheet | keys | what it holds |
|---|---|---|
| `heart.md` | HD-1..46 | heart disease and stroke |
| `cancer.md` | CA-1..32 | cancer |
| `diabetes-kidney.md` | DK-1..39 | diabetes, kidney failure, insulin, dialysis |
| `polio-and-after-1980.md` | PO-1..31 | polio; pneumonia, children's diarrhoea and TB past 1980 |
| `mental-hospital.md` | MH-1..29 | the mental hospital and chlorpromazine |

This note cites those keys. It writes "est." where a number is this study's
estimate, not a source's.

## 0. In short

- **Five causes of today, named.** Heart disease, stroke, cancer, diabetes and
  kidney failure become named shares of today's base rate in each band. Each
  share is Canada's 2024 rate for that cause in that band (Statistics Canada,
  Open Licence). For adults the five come to about 70% of the base, for seniors
  55% and for elders 46%. Each cause also gets a history factor H: its
  age-standardized rate in a year over today's. Applied to its share, H makes
  the 1900-2024 path real, and H is 1 from today on.
  - Heart disease's H rises to 5.5 in 1950 and falls to 1.
  - Stroke's is 9.0 in 1900.
  - Cancer's is 0.79 in 1900, 1.55 at the 1988 peak, then falls.
  - Diabetes peaks at 2.1 in 2002.
  - Kidney failure is 1.57 in 2000.

  This moves the default city's deaths from 1900 to today, as R0b's causes did
  (★1).
- **Pneumonia, children's diarrhoea and TB past 1980** (R6-G ★2) are named the
  same way, at Canada's rates today. In 2024 pneumonia killed 437 elders per
  100,000. Children's diarrhoea killed one to seven children under five in a
  year, and TB about a hundred people. So the rows on these three move little,
  and that is the honest result.
- **The rows act as steps on these causes,** through R2f's `Step` and
  `ahead()`: history's path already holds each row's step, and a city that finds
  the row ahead of the world takes the step early.
  - Insulin, on the young bands' diabetes deaths: x.54.
  - Angioplasty, on heart deaths: x.978 (est.).
  - Statins, on heart deaths: x.938 (est.).
  - The workplace smoking ban, on heart deaths: x.944 (est.). These three
    heart steps come from Ontario's 1994-2005 split of the fall and the
    trials.
  - Imatinib, on cancer deaths: x.998.
  - Checkpoint immunotherapy, on cancer deaths: x.995.
  - PCV7, on babies' pneumonia: x.61.

  Six rows had no population effect that shows by 2024: M15, M16, M18, M21,
  M22, and M14 on deaths. Each enters as a dated entry that moves nothing
  (★5). M14's repeat procedures go to general care's stays on R6f's pattern.
- **Polio** is a seasonal (July-November) cause on Canada's own years, 1927-1964
  (PHAC's counts, Open Government Licence - Canada). Its deaths peaked at 3.3
  per 100,000 in 1953. M3, the Salk vaccine, is its step. Survivors are not
  modelled (★6).
- **Dialysis** keeps a saved pool of people with kidney failure. Those who are
  served live; the rest die within the month. Recommended: general care's
  hospitals serve the pool once M4 is known, as Canada's hospital-run renal
  programmes do. The other way is a Dialysis Unit building, 19 stations as
  Ontario's average location (★2). The register with Canada's numbers (CIHI's)
  is not licensed for a sold game. USRDS's (US, public domain) is.
- **The mental hospital: leave it out (★3).** The evidence measures the beds
  (4.1 patients per 1,000 Canadians in 1954, 0.36 today), the stays (turnover
  4.1 years in 1955, 2.0 in 1963) and the staff. It measures no outcome that a
  served person has and an unserved one lacks. The homelessness link is weak.
  So the building would be pure cost, and chlorpromazine stays out of the
  catalogue as research-design left it. A brief for building it anyway is in
  §10.5.
- **Four batches,** in order:
  1. **MED2:** today's causes, the mechanism and all eight named causes.
  2. **MED3:** the rows' steps, after R6f.
  3. **MED4:** polio and M3.
  4. **MED5:** dialysis and M4.

  The six calls are one Pending row, MED1-K, each with its recommended way.

## 1. What was asked, and how it was done

**What was asked.** The orchestrator's brief, from the queue's MED1 row:

- For each cause the game lacks, and for the mental hospital, give:
  - the real rates from 1900 to today, sourced;
  - what moved them;
  - what the game would need, and where it meets the code;
  - the batches to build it, in order, each one builder's work.
- Every source recommended for shipping must allow commercial use (Jerus,
  2026-10-31; `licences.md`).
- Keep off FM2 (held under EP1b) and FM3 (research-speculative §7).
- Settle the Pending rows that name MED1: R6-G-K ★2 and ★3, and R6-G-P's
  M11-M22 and L6 (§8).

**How it was done.**

- **Gathering.** Five agents read the sources on 2026-11-17, one per sheet, and
  each wrote what it read with its mark and key. Opus read the game's side
  (`CausesOfDeath`, `PopulationCohorts`, `Sickness`, `research.json`,
  `buildings.json`, R2f's notes) and wrote this note. No code was changed, and
  no prototype was run.
- **The arithmetic** below (the shares, the H knots, the step factors) is done
  by hand from the sheets' figures. It is shown where it matters.

**The rules the sources are held to.**

- **Licences.** Series that would ship as knots come only from sources whose
  licence allows a sold game:
  - Statistics Canada, including the Historical Statistics of Canada and the
    old DBS Year Books (Open Licence);
  - PHAC's notifiable-disease file (Open Government Licence - Canada);
  - the US NCHS and NIH (public domain);
  - the OECD.
- **Facts.** A trial's measured effect is a fact, cited as R2f cited sulfa's,
  and it sets a dial.
- **CIHI, WHO and IHME** forbid commercial use without permission. **The
  charities** do too: Heart and Stroke, Diabetes Canada, the Kidney Foundation
  and the Canadian Cancer Society. So no series is taken from them, and their
  figures appear here only as findings.

## 2. The game today

**Where a death comes from.** Three places.

1. **`AgeBand`'s base rate.** It is today's annual rate by band, the same in
   every year:

   | Band | Ages | Annual rate |
   |---|---|---|
   | Babies | 0-5 | .0010 |
   | Children | 6-12 | .000075 |
   | Teens | 13-17 | .0002 |
   | Adults | 18-69 | .00225 |
   | Seniors | 70-84 | .0290 |
   | Elders | 85+ | .1277 |

   Children, teens and adults were halved on 2026-09-11, because `Sickness`
   kills the rest.
2. **`Sickness`.** It kills the sick who are not cured. Its death chance a
   month runs from .01 to .08 by band.
3. **`CausesOfDeath`**, since R0b. It holds seven causes of 1900: typhoid,
   children's diarrhoea, the childhood outbreaks, TB, pneumonia, childbirth and
   the 1918-20 influenza.
   - **Knots.** Each cause has a rate in 1900 and knots `{year, factor}`,
     linear between, reaching 0 by 1980.
   - **Adults and older.** The causes are added to the base.
   - **Babies and children.** Childcare's swing sets these bands, and the
     causes split the swing's excess (`SPLIT`, `MAX_SPLIT` .9).
   - **R2f's rows.** Six steps (`STEPS`) bring a cause's history forward by the
     months the city found the row ahead (`ahead()`). A city that found nothing
     ahead, and every city founded the default way (`MedicalRows.TODAY`), dies
     on history's path to the bit.

**Where they meet the month** (`docs/month-order.md`, steps 33-37):

- `Healthcare.mortalityFactors()` makes the base's factor by band (Game,
  L15273).
- `causesOfDeath.rates()` makes the causes' rates (L15336).
- `PopulationCohorts` adds the four parts in one line (L309):
  `all = rate + ill + kill + cause`. It caps them at
  `AgeBand.MAX_MONTHLY_MORTALITY` and keeps each part's share.
- `causesOfDeath.book()` books the dead by cause (L15348).

Two more calls pass `Healthcare.mortalityFactors(0, 0, 0)`, at L15294 and
L15360. A change to the base factor must reach every path, and not only the one
in `nextMonth`.

**What is saved.** `CausesOfDeath.getState()` saves, for n causes:

- the last month's deaths, n values;
- the totals, n values;
- a twelve-month ring, n x 12 values;
- the TB sick, one value.

`restore()` refuses a save of the wrong length outright. So a save made before
new causes are added reads zeros for the causes. That is incomplete, not wrong,
so no format bump is needed (CLAUDE.md's rule).

**What it means.** Every city starts in January 1900 (`CityCalendar.EPOCH_YEAR`)
and lives through history's path. Its 1900 causes fall away to nothing by 1980.
After that, its people die at today's rate in every band. Heart disease's 1950s
and cancer's 1980s are not in it, so nothing that ended them can be found.

## 3. Today's causes: heart disease, stroke, cancer, diabetes, kidney failure

### 3.1 The shape: a named share of today's base, with history's factor

The rate of cause c in band b today is Canada's 2024 rate,
`r(c,b)` per 100,000. Its share of the game's base in that band is

`s(c,b) = r(c,b) / base(b)`

Each cause has a history factor `H(c,t)`: its age-standardized rate in year t
over its rate today, linear between knots, 1 from the last knot on. The base
in band b then runs at

`base(b) x (1 + sum over c of s(c,b) x (H(c,t) x A(c,t) - 1))`

where `A(c,t)` is the city's steps ahead (`ahead()`, 1 for a default city).

- **As a factor on the base.** The rule enters as a factor on the base, and not
  as a fifth addend. Cancer's H is below 1 before 1960, so the part can be
  negative, and `PopulationCohorts` drops a negative cause (`Math.max(0, ...)`).
- **Booking.** The base's deaths are split among the named causes by
  `s(c,b) x H x A` over the bracket. The rest stays unnamed, as "other causes".
- **Which bands take H.**
  - Babies and children: H stays 1. Their named shares are booked, but their
    history is the swing's excess, which R0b's causes already split.
  - Teens and older: H is applied.
- **The cap.** `sum s` must stay under `MAX_SPLIT` (.9). It does: adults are at
  .70.

**Canada 2024, rates per 100,000.** From 13-10-0394-01 and 13-10-0392-01.
Adults are read as 20-69, the tables' nearest band.

| Band | heart | stroke | cancer | diabetes | kidney | sum of shares | base |
|---|---|---|---|---|---|---|---|
| Adults 18-69 | 42.8 | 8.1 (est.) | 98.4 | 7.2 (est.) | 2.0 (est.) | .70 | 225 |
| Seniors 70-84 | 471.8 | 118.7 (est.) | 892 | 62.4 (est.) | 36.7 (est.) | .55 | 2,900 |
| Elders 85+ | 2,729.6 | 665.7 (est.) | 2,026 | 228.8 | 213.8 | .46 | 12,770 |

- **Where the figures come from.**
  - Heart: HD-3 and HD-4. Cancer: CA-2.
  - Elders' diabetes and kidney failure: DK-2.
  - The figures marked est. are 2024's deaths in the band (HD-3, DK-2) over the
    band's population. Each population is derived from the heart deaths and
    the heart rate:

    | Band | Population |
    |---|---|
    | Adults 20-69 | 27.31 M |
    | Seniors 70-84 | 4.47 M |
    | Elders 85+ | 0.92 M |
- **Young bands.** Their counts are tiny, and the tables cut 1-14 and 15-19
  across the game's bands. Cancer is the only cause of size there:

  | Band | Cancer rate per 100,000 | Share of the base |
  |---|---|---|
  | Babies | 2.2 | 2.2% |
  | Children | 2.1 | 28% |
  | Teens | 2.4 | 12% |

  Heart disease and stroke kill 30 a year under 15. Diabetes kills three at
  1-14, and kidney failure none (HD-3, DK-2).
- **Stroke is a cause of its own.** Statistics Canada counts heart disease and
  stroke as two causes and ranks them apart: heart disease second and stroke
  fourth in 2023 and 2024 (HD-16). The game does the same.

### 3.2 Heart disease and stroke

**The rates.** All are age-standardized, per 100,000.

Canada, heart disease alone, 2011 standard (HD-1, page). 2024 is 114.5, derived
from the 2021-standard table's 2023-24 ratio (HD-2):

| Year | 2000 | 2005 | 2010 | 2015 | 2019 | 2023 |
|---|---|---|---|---|---|---|
| Rate | 222.7 | 179.9 | 143.0 | 130.5 | 121.9 | 118.4 |

Canada, circulatory disease (heart, stroke and vessels), 2011 standard (HD-5,
page):

| Year | 1950 | 1960 | 1970 | 1980 | 1990 | 2000 | 2010 |
|---|---|---|---|---|---|---|---|
| Men | 998.4 | 1,020.5 | 911.1 | 740.6 | 512.9 | 400.7 | 256.4 |
| Women | 814.1 | 748.4 | 594.9 | 459.8 | 324.1 | 255.4 | 164.2 |

Men peaked in 1957 (1,032.7) and women in 1951 (822.7).

There is no Canadian standardized series before 1950. The Historical Statistics
of Canada's cardiovascular-renal group is crude: 221.9 in 1921-25, rising to
414.0 in 1951-55 (HD-6). So the shape before 1950 is the United States'.

US heart disease, 2000 standard (HD-19, NCHS, page):

| Year | 1900 | 1910 | 1920 | 1930 | 1940 | 1950 | 1960 | 1970 | 1980 | 1990 | 2000 | 2010 | 2018 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Rate | 265.4 | 345.1 | 375.0 | 467.9 | 558.5 | 588.8 | 559.0 | 492.7 | 412.1 | 321.8 | 257.6 | 179.1 | 163.6 |

US stroke, 2000 standard (HD-19, page):

| Year | 1900 | 1910 | 1920 | 1930 | 1940 | 1950 | 1960 | 1970 | 1980 | 1990 | 2000 | 2010 | 2018 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Rate | 244.2 | 238.5 | 238.5 | 203.9 | 177.2 | 180.7 | 177.9 | 147.7 | 96.4 | 65.5 | 60.9 | 39.1 | 37.1 |

Canada's stroke rate, 2011 standard, was 63.7 in 2000 and 28.3 in 2023 (HD-1).

**H, the knots** (est., by this arithmetic):

- **2000-2024** is Canada's own series.
- **1950-2000** is Canada's circulatory ratio, both sexes weighted 0.4 men and
  0.6 women, chained onto 2000.
- **Before 1950** is the US shape, chained onto 1950.

| Year | 1900 | 1910 | 1920 | 1930 | 1940 | 1950 | 1960 | 1970 | 1980 | 1990 | 2000 | 2010 | 2024 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| heart | 2.48 | 3.23 | 3.51 | 4.38 | 5.23 | 5.51 | 5.32 | 4.48 | 3.55 | 2.48 | 1.945 | 1.249 | 1 |
| stroke | 9.02 | 8.81 | 8.81 | 7.53 | 6.55 | 6.68 | 6.57 | 5.46 | 3.56 | 2.42 | 2.25 | 1.38 | 1 |

Stroke's knots are Canada's 2000/2023 ratio (2.25) times the US shape before
2000. They are high before 1950, and that is real: in 1900 the US crude stroke
rate was 106.9 against heart disease's 137.4 (HD-18). The elders' H is
shallower than the all-ages one. Their heart rate fell 1.70 times from 2000 to
2024, against 1.945 for all ages (HD-3). §3.6 shows what that does.

**What moved it** (`heart.md` §4):

- **Treatments.**
  - The coronary care unit, May 1962: early deaths after a heart attack fell
    from about 30% to about 15% by the late 1960s.
  - The VA blood-pressure trials, 1967 and 1970.
  - Bypass surgery, 9 May 1967. No population effect was found.
  - Angioplasty, September 1977: deaths 7% against thrombolysis's 9%.
  - Statins, 31 August 1987. In 4S, deaths fell with a relative risk of 0.70,
    and coronary deaths 0.58. CTT found 0.78 for each mmol/L.
  - The drug-coated stent, 24 April 2003. In SIRIUS, repeat procedures were
    4.1% against 16.6%, and no death figure was gathered.
  - GLP-1 drugs, 4 June 2021. In SELECT, the hazard ratio was 0.80 (0.72-0.90),
    but Canada's heart rate is flat from 2019 to 2023.
- **Smoking.** Canadian smoking fell from 49.5% in 1965 to 10% in 2020.
  Smoke-free workplace laws came province by province from 2003 to 2006,
  Manitoba's on 1 October 2004. Heart-attack admissions fell 12% in Ireland's
  first year, and pooled studies give a rate ratio of 0.83 (0.80-0.87).
- **Two decompositions** split the fall between treatments and risk factors:

  | Share of the fall | Ford, US 1980-2000 (HD-23) | Wijeysundera, Ontario 1994-2005 (HD-24) |
  |---|---|---|
  | Treatments, all | about 47% | 43% |
  | ...secondary prevention / stable coronary disease | 11% | 17% |
  | ...first treatment of a heart attack | 10% | 8% |
  | ...heart failure | 9% | 10% (in the community) |
  | ...revascularisation for angina | 5% | - |
  | ...other treatments | 12% | - |
  | Risk factors, all | about 44% | 48% |
  | ...cholesterol | 24% | 23% |
  | ...blood pressure | 20% | 20% |
  | ...smoking | 12% | - |
  | ...inactivity | 5% | - |
  | ...diabetes | -10% | -6% |
  | ...body-mass index | -8% | -2% |
  | Unexplained | - | about 9% |

  In Ontario, coronary deaths fell from 191 to 125 per 100,000 (-35%).

The game's rows are angioplasty (M7), statins (M10), the stent (M14), the GLP-1
drug (M21) and the smoking ban (L6). §4 turns each into a step or a verdict. The
coronary care unit, the blood-pressure trials and bypass are no rows of the
catalogue: research-design folded the coronary care unit into M2 and M7. Their
fall stays in history's knots, with no step a city could take early.

### 3.3 Cancer

**The rates.**

Canada, both sexes, 2011 standard (CA-1, page):

| Year | 2000 | 2005 | 2010 | 2015 | 2020 | 2023 | 2024 |
|---|---|---|---|---|---|---|---|
| Rate | 243.2 | 230.3 | 213.2 | 199.2 | 184.4 | 176.2 | about 172.5 (calc) |

Before 2000, Canada by sex, 1950-2012 (CA-5). Both sexes is derived as 0.4 men
and 0.6 women:

| Year | 1950 | 1960 | 1970 | 1980 | 1988 (peak) | 1990 | 2000 | 2010 |
|---|---|---|---|---|---|---|---|---|
| Both sexes | ~230 | ~239 | ~247 | ~253 | ~267 | ~261 | ~251 | ~219 |

The US, age-adjusted (CA-12, NCHS):

| Year | 1900 | 1910 | 1920 | 1930 | 1940 | 1950 | 1990 |
|---|---|---|---|---|---|---|---|
| Rate | 114.8 | 143.1 | 159.0 | 174.5 | 187.8 | 193.9 | 216.0 (the series' maximum) |

**H** (est.). Canada's ratio is used from 1950, and the US shape before it.
Relative to 172.5:

| Year | 1900 | 1910 | 1920 | 1930 | 1940 | 1950 | 1960 | 1970 | 1980 | 1988 | 1990 | 2000 | 2010 | 2024 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| H | .789 | .984 | 1.093 | 1.200 | 1.291 | 1.333 | 1.386 | 1.432 | 1.467 | 1.548 | 1.513 | 1.410 | 1.236 | 1 |

Below 1 before 1912 means fewer cancer deaths than today. Part of that is real:
fewer people reached the cancer ages, and fewer smoked. Part is diagnosis that
did not yet exist. The game cannot tell the two apart.

**What moved it** (`cancer.md` §4):

- **Tobacco**, rising then falling. Lung cancer drove the rise to 1988 and the
  fall after.
- **Stomach cancer's long fall**, with refrigeration.
- **The Pap test.** British Columbia from 1948, a provincial programme from
  1960. Cervical deaths fell 83%.
- **Mammography.** British Columbia from 1988.
- **Childhood leukaemia.** Five-year survival rose from 57% in 1975 to 92%.

**The rows** (shares of 2024's cancer deaths, `cancer.md` §3):

- **Imatinib** (M13, May 2001), for chronic myeloid leukaemia, 0.14% of cancer
  deaths. 89% are alive at five years. Deaths were 203 in 2000 and 123 in 2024.
- **The HPV vaccine** (M16, June 2006), for cervical cancer, 0.50%. Incidence
  fell 87% in girls offered it at 12-13.
- **Checkpoint immunotherapy** (M17, March 2011). The measured trial is in
  melanoma (1.53% of cancer deaths), with a hazard ratio of 0.68.
  - The wider checkpoint sites (lung, kidney and bladder) are about 29% of
    cancer deaths, but their trials were not gathered.
  - FM2 needs M17 (R6b). M17 entering the catalogue is all that FM2 needs from
    this study; FM2 itself stays held.

### 3.4 Diabetes and insulin

**The rates.**

Canada, age-standardized, 2021 standard (DK-1, page). 2002 is the peak:

| Year | 2000 | 2002 | 2010 | 2020 | 2024 |
|---|---|---|---|---|---|
| Rate | 32.0 | 35.4 | 24.8 | 20.6 | 16.5 |

In 2024 that was 6,874 deaths, 2.1% of all. There is no Canadian standardized
series before 2000. The crude rate was 10 in 1921 and 12.2 in 1963 (DK-6,
DK-7).

The US death registration states' rates by age (DK-9, HIST290, page) give the
shape:

| Year | 15-24 | 25-34 | 45-54 | 55-64 | 65-74 | 75-84 | 85+ |
|---|---|---|---|---|---|---|---|
| 1900 | 4.2 | 5.0 | 16.3 | 40.3 | 67.0 | 72.0 | 48.1 |
| 1921 | 4.8 | 5.9 | 23.6 | 69.0 | 114.2 | 132.9 | 135.1 |
| 1925 | 2.9 | 3.4 | 23.1 | 69.8 | 130.6 | 161.6 | 124.4 |
| 1939 | 2.3 | 2.6 | 25.7 | 85.0 | 185.1 | 264.0 | 204.9 |

**H by band** (est.). The US rate by age over Canada's 2024 rate in the same
ages: 6.0 at 45-54, 15.4 at 55-64, 33.2 at 65-74, 62.4 at 70-84 and 228.8 at
85+ (DK-2).

| Band | 1900 | 1921 | 1939 | 2000 | 2002 | 2010 | 2020 | 2024 |
|---|---|---|---|---|---|---|---|---|
| Adults | 2.7 | 4.2 | 4.9 | 1.94 | 2.15 | 1.50 | 1.25 | 1 |
| Seniors | 1.1 | 2.0 | 3.5 | 1.94 | 2.15 | 1.50 | 1.25 | 1 |
| Elders | .21 | .59 | .90 | 1.94 | 2.15 | 1.50 | 1.25 | 1 |

- The adults' H uses the 45-64 rates, where most adult diabetes deaths were.
- 1939 to 2000 is linear (est.). The US series cannot bridge it: ICD-6 recoded
  diabetes in 1949.
- Teens take the 15-24 ratio. Canada's 15-19 rate was about 0.2 in 2024 (4
  deaths), against 4.8 in 1921, so H is about 26. That is still only a fifth of
  the teens' small base.

**Insulin** (PM2) was given to Leonard Thompson on 11 and 23 January 1922, and
Connaught had it from 15 May 1922 (DK-15 to DK-17).

- **The population rates.** Between 1922 and 1925, the US registration states'
  rates fell -46% at 5-14, -42% at 15-24 and -46% at 25-34. The rates of the old
  did not fall (§1e and §2c of the sheet).
- **Joslin's diagnosed patients.** Ten-year mortality fell from 386 per 1,000
  person-years (1914-22) to 61 (1922-26) (DK-13).

The game's cause is a population rate, so PM2 takes the population figure. §4
gives its step.

### 3.5 Kidney failure

**The rates.**

Canada, today's ICD-10 group, 2021 standard (DK-1, page):

| Year | 2000 | 2010 | 2011 | 2024 |
|---|---|---|---|---|
| Rate | 15.5 | 14.0 | 11.6 | 9.9 |

In 2024 that was 4,147 deaths. The 2010-11 step may be coding.

- **The old "nephritis (all forms)" bin.**
  - In the US it ran 84-103 per 100,000 in 1900-1926, sixth among the causes
    (DK-11).
  - In Canada it ran 32-55 in 1921-26 (DK-6) and 6.6-7.2 in the early 1960s
    (DK-7).
  - It is a different bin. It held much of what is now coded as hypertensive
    heart disease. A 1971 US study found that the 1950-60 fall was mostly
    doctors changing how they recorded the cause (snippet, the sheet's §4d).
  - So it cannot give kidney failure's history: those deaths are in heart
    disease's and stroke's H already.

**H** (est.):

- Canada's own series from 2000: 1.566 in 2000, 1.414 in 2010 and 1.17 in 2011.
- Before 2000, held at 1.566.
- The deaths that dialysis removed come in through the pool (§6), not through
  H.

### 3.6 A check on the sums

Each band's annual death rate per 100,000 on the knots above, before
`Sickness` and the 1900 causes (est., by hand):

| Band | 1900 | 1950 | 2024 |
|---|---|---|---|
| Adults | about 345 | about 522 | 225 + 0 |
| Seniors | about 4,390 | about 6,160 | 2,900 |
| Elders | about 21,660 | about 29,700 | 12,770 |

- **1900.** The 1900 causes (TB, pneumonia, influenza) come on top. That gap is
  what they are for.
- **1950.** The elders' about 30% is too high against the period's life tables
  (to be read, not gathered here). The cause is the all-ages heart and stroke
  ratios applied to a band whose rate fell less.

So MED2 fits the elders' H for heart disease and stroke as follows:

- From 2000 on, Canada's by-band rates, which the tables give.
- Before 2000, the all-ages H scaled by the same ratio, 1.70/1.945 (est.).
- Then a check of every band's total, with `Sickness` and the 1900 causes,
  against Statistics Canada's life tables for 1921, 1951 and 1981 (Open
  Licence). Each must be within a tolerance the batch states.

The harness asserts that check. That is the "count it in a real run" rule.

### 3.7 Pneumonia, children's diarrhoea and TB past 1980 (R6-G ★2)

R0b's excess for each of these causes runs to 0 by 1980, leaving today's level
unnamed in the base. On ★2's recommended way, MED2 names today's level as a
share of the base, as in §3.1, with H = 1: the history is R0b's excess, which
stays as it is.

- **Pneumonia, J09-J18** (Statistics Canada 13-10-0394-01 and 13-10-0933-01, the sheet's §4a; PO-22). 7,658 deaths in 2024. Rates
  per 100,000 by age:

  | Age | 65-74 | 75-84 | 85+ |
  |---|---|---|---|
  | Rate | 21.7 | 72.5 | 436.8 |

  That is about 3.4% of the elders' base.
- **Children's diarrhoea, A04 and A07-A09.** One to seven deaths a year under
  five. The adult deaths in this group (400 at 70-84 and 505 at 85+ in 2024)
  are mostly C. difficile, an elders' hospital infection, and are not
  children's diarrhoea. They stay unnamed.
- **TB.** Mortality fell from 0.8 per 100,000 in 1980 to 0.2 in 2010, about a
  hundred deaths a year. About 17 drug-resistant cases a year (PO-23 to PO-26).

R0b's 1900 rate of pneumonia already sits on today's level, an overcount of
about 9% in 1900. It is left as it is: it is inside R0b's fitted knots.

## 4. The rows on these causes: steps and verdicts

The rule is R2f's.

- **History holds each step.** History's path already holds every row's step on
  its date, because the knots are history. A city that finds a row N months
  ahead takes its step N months early (`ahead()`).
- **A step's factor** is what the row took off the cause in history.
- **Its ramp** is when history took it.
- **The ramp must end by the cause's last knot (2024)**, because H is 1 after
  that (★5).
- **Shares of heart deaths**, read from HD-1 (2011 standard):

  | Share of heart deaths | 2000 | 2023 |
  |---|---|---|
  | Ischaemic heart disease, c | .77 | .60 |
  | Heart attack, a | .36 | .21 |

| row | date | cause | step | history's ramp | basis |
|---|---|---|---|---|---|
| PM2 insulin | Jan 1922 | diabetes | teens x.54; adults x.93 (est.); seniors and elders 1 | 1922 to 1925 | US registration states, -42 to -46% under 35, the old unchanged (DK-9); adults weighted by the under-35s' share of the band's 1921 deaths, about .15 (est.) |
| M7 angioplasty | Sep 1977 | heart | x.978 (est.) | 1978 to 2000 | Ontario's heart-attack treatment, 8% of a 35% fall in coronary deaths, times c: 1 - .08 x .35 x .77 |
| M10 statins | Sep 1987 | heart | x.938 (est.) | 1988 to 2005 | Ontario's cholesterol, 23% of the 35% fall, times c: 1 - .23 x .35 x .77. It gives statins the whole of cholesterol's share, part of which was diet (est.) |
| L6 workplace smoking ban | Oct 2004 | heart | x.944 (est.) | 2004 to 2006 | heart-attack admissions RR 0.83 (HD-36), taken for deaths, times a in 2004 (about .33): 1 - .17 x .33 |
| M13 imatinib | May 2001 | cancer | x.998 | 2001 to 2006 | chronic myeloid leukaemia, .33% of cancer deaths in 2000 (203 of 62,672), deaths about halved (est. from 203 to 123 against an ageing population) |
| M17 checkpoint immunotherapy | Mar 2011 | cancer | x.995 (est.) | 2011 to 2016 | melanoma, 1.53% of cancer deaths, hazard 0.68: 1 - .0153 x .32. The other sites' trials were not gathered |
| M11 PCV7 | Feb 2000 | pneumonia, babies only | x.61 | 2002 to 2006 (est., Canada's programmes) | pneumonia admissions under 2, -39% (22-52) (G-M8 in R6-G), taken for deaths |
| M14 drug-coated stent | Apr 2003 | no death step | - | - | SIRIUS measured repeat procedures, not deaths. It goes to general care's stays on R6f's pattern, its share of stays sourced first, or else nothing |
| M15 rotavirus | Feb 2006 | nothing | - | - | children's diarrhoea deaths were one to seven a year before it |
| M16 HPV vaccine | Jun 2006 | nothing (★5) | - | - | the girls vaccinated in 2007 reach cervical cancer's ages after 2035: no deaths averted by 2024 |
| M18 the all-oral TB cure | Aug 2019 | nothing | - | - | about 17 drug-resistant cases a year in Canada |
| M21 GLP-1 drugs | Jun 2021 | nothing (★5) | - | - | SELECT's 0.80 is a trial result. Canada's heart rate was flat from 2019 to 2023, so no population effect shows yet (HD-28) |
| M22 RSV vaccine | May 2023 | nothing (★5) | - | - | lower-respiratory disease -82.6% in trials. Little uptake by 2024 |

**What the table means.**

- **Dated entries.** A row that moves nothing still enters `research.json` as a
  dated entry that can be found, as R2f did for PM2, M3, M4 and M7. That keeps
  the catalogue's years whole.
- **Codes.** They follow R6-G-K ★1: M11-M22 and L6-L9, in date order.
  - R6f brings M12 and M19.
  - FM1's batch brings M20.
  - MED3 brings the rest of MED1's.
- **Steps by band.** PM2's step differs by band, which the `Step` record cannot
  hold today: it has one factor for every band. MED3 gives it one factor per
  band, or splits PM2 into band steps.

## 5. Polio and the polio vaccine (M3)

**Cases and deaths, Canada** (PO-1, PHAC, Open Government Licence - Canada;
deaths from the DBS Year Books PO-3 to PO-7, Open Licence):

| Year | 1927 | 1931 | 1937 | 1941 | 1946 | 1949 | 1951 | 1952 | 1953 | 1955 | 1957 | 1959 | 1960 | 1963 | 1964 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Cases | 609 | 1,342 | 3,905 | 1,881 | 2,527 | 2,458 | 2,568 | 2,334 | 5,384 | 584 | 185 | 1,887 | 909 | 122 | 19 |
| Deaths | - | - | - | - | - | - | 162 | 311 | 481 | - | 26 | 182 | 83 | 16 | 5 |

- **Deaths per case.** About one paralytic case in ten died: 9.7% in 1953's
  Year Book pair. Ordinary years ran 0.5-2.0 deaths per 100,000, and 1953 was
  3.3.
- **Season and ages.** Polio came from July to November (PO-8). In 1926, 70% of
  the deaths were children under fifteen; in 1953 it was 30%, and 60.9% of the
  deaths were over 20 (PO-3).
- **Bulbar polio.** In Copenhagen in 1952, manual ventilation cut bulbar deaths
  from 87% to about 25% (PO-12).
- **The vaccines.**
  - Connaught's Salk vaccine went into Canadian arms from April 1955 (grades
    1-3). Sabin's was licensed in March 1962.
  - In the US, paralytic cases fell 90% in five years and 99.7% in ten (M3's own
    row).
  - Canada's last wild outbreak was in 1978, and it was certified polio-free in
    1994.

**What the game needs.**

- **A cause of 1900's kind, POLIO.** It goes at the end of `Cause`. Its knots
  are Canada's own yearly deaths per 100,000.
  - Where deaths are missing (1927-1949, 1954-56, 1961-62), they are PHAC's
    cases times .097 (est.).
  - Before 1927 it runs at the ordinary level, 0.5 (est.).
  - It reaches 0 after 1965.
  - A knot a year makes 1937's and 1953's epidemics come in every city's 1937
    and 1953, as the influenza comes in 1918-20. The game's history is Canada's.
- **A season**, July to November, built as `PNEUMONIA_WINTER` and
  `DIARRHOEA_SUMMER` are.
- **Two age patterns** of `DEATH_SHARE`: 1926's (70% of deaths under fifteen)
  and 1953's (60.9% over twenty), linear between and held after 1953 (est.).
- **The swing.** Babies' and children's polio deaths join the split of the
  swing's excess. Teens' and adults' are added.
- **M3's step.** x.003, the 99.7% fall in ten years, ramping from April 1955
  over ten years. A city that finds M3 in 1940 has no 1953.
- **No step from M2.** The breathing machine already moves its own constant,
  `SERVED_DEATHS_BEFORE_M2`. The tank respirator was in Canada from the 1930s,
  so 1953's deaths are with it. Polio takes no M2 step.
- **Measles stays in the knots.** Research-design folded the measles vaccine
  (1963) into M3's mechanism. That would make finding the polio vaccine end
  measles too, so measles stays in the outbreaks' knots with no step.

The survivors are ★6.

## 6. Dialysis (M4)

**The facts** (DK-18 to DK-38):

- **Firsts.**
  - Montreal's Royal Victoria gave acute dialysis in 1948.
  - Seattle's Clyde Shields became the first chronic patient on 9 March 1960.
  - Canada's first chronic programme was in Edmonton in 1962.
- **Who it serves.**
  - About 6,243 people a year start renal replacement in Canada outside Quebec.
    The incident rate was 208 per million in 2019, and 54% of new patients are
    65 or older.
  - 29,906 people were on dialysis at the end of 2023 and 19,356 had a working
    transplant (CIHI).
  - Deaths on dialysis run 168.4 per 1,000 person-years (USRDS 2023, snippet).
    About 40% are alive at five years.
  - Untreated kidney failure kills in days to weeks.
- **Ontario's in-centre units** (DK-28, DK-29):
  - 94 locations, almost 1,800 stations, about 19 a location;
  - 5.7 patients a station at capacity, about 4.5 in practice;
  - one nurse to three patients, and 168 patients a nephrologist;
  - almost $142,000 a patient a year;
  - 477 litres of water and 12.6 kWh a session.

**What the game needs, either way.**

- **A pool of people with kidney failure,** by band, saved in `DataSave` and the
  load path.
  - **Entering:** an incidence by band.
  - **Served:** the served die at the dialysis rate, about 17% a year. Those
    deaths are already in today's base, so they are not added again.
  - **Unserved:** the unserved die in the month. That excess is booked as
    kidney failure.
  - **Before M4,** no one is served. So from 1900 to 1960, a default city
    loses its whole incidence each year. That is the deaths dialysis later
    removed.
- **The incidence licence.** CIHI's register gives Canada's incidence, but CIHI
  allows no commercial use without written permission (DK-21). USRDS's Annual
  Data Report is public domain (DK-36) and gives the US incidence by age, about
  twice Canada's. Both ways under ★2 take USRDS's by age, unless Jerus asks CIHI
  (copyright@cihi.ca) and is given leave.

**★2 is who serves the pool.**

- **Recommended: general care.** The pool is served at general care's coverage
  once M4 is known.
  - Canada's dialysis is the hospitals': 27 regional renal programmes run the
    94 locations.
  - The catalogue already calls M4 "equipment".
  - The General Hospital's figures in `buildings.json` are today's, and
    dialysis is already in them. The dialysis nurses of a 40,000-person
    hospital are about 10 of its 363 posts (est.). So nothing new is built,
    staffed or billed.
- **The other way: a Dialysis Unit**, opened by M4 (`since: "M4"`, as the
  Assisted Living Residence is opened by M8). Ontario's average location, est.:

  | Item | Value |
  |---|---|
  | Stations | 19 |
  | Patients | 86 (at 4.5 a station) |
  | Sessions a year | about 13,400 |
  | Water | 6.4 million litres a year |
  | Electricity | 170 MWh a year |
  | Nurses | about 15 COLLEGE_HEALTH |
  | Technicians | 2 DIPLOMA |
  | Other staff | 5 NO_DIPLOMA |
  | Nephrologist | 1 UNIV_DOCTOR |
  | Cost | about $12 million a year at Ontario's cost |

  It is a new care type, DIALYSIS. It touches `Healthcare.noteCoverage()`
  (three types today), `BuildAdvice`'s Measure list and label, `BuildCard`,
  `CityNeeds`, `AdviceValueOracle` and the auto-builder. M4's kind becomes
  "building", and `ResearchCheck`'s count of building rows moves.

## 7. The mental hospital and chlorpromazine

**The facts** (MH-1 to MH-29; the series are Statistics Canada's, Open Licence):

- **Patients in mental institutions,** per 1,000 Canadians:

  | Year | 1931 | 1941 | 1951 | 1954 | 1956 |
  |---|---|---|---|---|---|
  | Patients | 3.05 | 3.93 | 3.96 | 4.11 (the peak) | 4.05 |

  - The public mental hospitals held 59,308 patients on their books in 1960, at
    their peak.
  - Rated beds fell from 67,446 in 1962 to 45,489 in 1975, about 2.0 per 1,000.
  - Today it is 0.36 per 1,000 (OECD, 2013).
- **Chlorpromazine.**
  - Paris, 1952.
  - Heinz Lehmann gave it to 71 patients at the Verdun Protestant Hospital in
    spring 1953 and published in February 1954.
  - Cochrane's trials: "not improved" RR 0.80, relapse RR 0.57.
- **What changed in the public hospitals, 1955 to 1963.**
  - Admissions rose 82% and discharges 130%.
  - Turnover fell from 4.1 years to 2.0.
  - Beds fell mainly after 1965, by policy.
- **Staff** rose from 19.7 per 100 patients in 1932 to 32 in 1956 and about 117
  in 1975.
- **Cost** rose from $1.94 a patient-day in 1949 to $4.94 in 1960.
- **The illness.**
  - Schizophrenia affects about 1 in 100 people aged 10 and over.
  - Ontario's people with schizophrenia die at 3.12 times the rate of others.
  - The 1955 admission rates by age ran from 56 per 100,000 at 0-9 to 1,467
    at 90+.

**The finding.** Everything measured is what the hospital held and spent: the
beds, the stays, the staff, the cost. Nothing measures what a served person had
that an unserved one lacked.

- **Death.** The 3.12 ratio is of people in today's care, served. No unserved
  figure exists.
- **Work.** No Canadian employment rate was found. The candidates (10-30%) are
  weak.
- **Homelessness.** Ecological studies link fewer beds to more homeless and
  imprisoned mentally ill people. A systematic review of 23 studies of people
  discharged found no such link (Winkler 2016, MH-28), and the housing market
  is the better-supported cause.
- **The game.** A served patient and an unserved one are both out of work and
  housed somewhere.

So a mental hospital in the game would cost money, posts and land, and change
nothing a harness could count (R6 ★6). Chlorpromazine would halve its stays,
and halving a building that does nothing does nothing. ★3 recommends leaving
both out: chlorpromazine stays where research-design put it, "left out of
medicine". §10.5 is the brief for building it anyway, if Jerus wants the
building for what it was.

## 8. The Pending rows and R2f's leftovers, settled

| row or part | settled |
|---|---|
| R2f ★10: PM2 | MED3: insulin's step on diabetes, by band (§4) |
| R2f ★10: M3 | MED4: polio and its step (§5) |
| R2f ★10: M4 | MED5: the dialysis pool (§6, ★2) |
| R2f ★10: M7 | MED3: a step on heart deaths. Its "back at work at 2 months 40% against 10%" has no heart sick to act on and is closed |
| R2f ★10: M10's coronary deaths | MED3: a step on heart deaths (§4) |
| R2f ★10: PM4's recovery | **closed.** No figure for a recovery rate was found. Its measured effect on all deaths (-2% to -3%) is its pneumonia and mothers' steps, which are already carried. Scarlet fever (-52% to -65%) is in the outbreaks' knots. A step of its own needs scarlet fever's share of the outbreaks in 1937, which was not gathered |
| R2f ★10: PM5's long-sick deaths | **closed.** Its figure is gas gangrene in the First War's wounded. The long-sick pool has no infection cause, and no figure for it was found |
| R2f ★10: M5's senior care | **closed.** "Discharged to care 17.8% to 34.3%" is where hip patients went after hospital. Senior care in the game is not fed by discharges. The stays part is already carried (`STAYS_BEFORE_LATER_ROW`) |
| R6-G-K ★2 | taken as recommended: MED2 names the three causes' today's level (§3.7). M11's step is in MED3. M15, M18 and M22 are dated entries that move nothing (§4) |
| R6-G-K ★3 | taken as recommended: MED2's cancer cause (§3.3). M13's and M17's steps are in MED3, and M16 is a dated entry. With M17 in the catalogue, FM2 needs nothing more from this study (FM2 itself stays held) |
| R6-G-P: M14, M21, L6 (heart disease) | MED3. M14 goes to stays on R6f's pattern. M21 is a dated entry (★5). L6's step and ★4 |
| R6-G-P: M11, M15, M18, M22 | MED3 (as ★2 above) |
| R6-G-P: M13, M16, M17 | MED3 (as ★3 above) |

R6-G-P's other rows belong to other owners (PW2, PW3, HO4, HT1, FM1), so that
row stays in Pending for them.

## 9. Calls for Jerus

1. **★1 Today's causes move the city's deaths from 1900 to today.**
   - **Recommended (a):** heart disease, stroke, cancer, diabetes and kidney
     failure as named shares of today's base, each with its history factor
     (§3.1). The default city then dies of heart disease in the 1950s as Canada
     did. Its deaths move every year from 1900 to 2024, so its trace moves and
     an ensemble is run, as R0b's was.
   - **(b)** Names only: the shares are booked, but H stays 1. The trace does
     not move. No row has a history to bring forward, so the rows in §4 move
     nothing.
2. **★2 Who serves the dialysis pool.**
   - **Recommended (a):** general care's hospitals, at their coverage, once M4
     is known. That is Canada's hospital-run programmes, and the catalogue
     calls M4 equipment. Nothing new is built.
   - **(b)** A Dialysis Unit building opened by M4: 19 stations, its own water
     and power bills, and a new care type the auto-builder must learn.
   - Either way the incidence is USRDS's (public domain), unless Jerus asks
     CIHI for leave to use Canada's register.
3. **★3 The mental hospital.**
   - **Recommended (a):** leave it out, and chlorpromazine with it. Nothing
     measured says what serving a patient changes (§7).
   - **(b)** Build it anyway (§10.5), as a need the planners meet with beds, out
     of the labour force either way, with chlorpromazine (M23) halving the
     stays.
4. **★4 L6, the workplace smoking ban.**
   - **Recommended (a):** a dated row the city finds like the others. Its step
     is taken from the month it is known.
   - **(b)** A Policy lever the player switches on, as R6-G's table wrote it.
     No cost of the ban was gathered, so a lever with no cost is a switch every
     player flips.
5. **★5 Rows whose effect had not shown by 2024** (M16, M21, M22).
   - **Recommended (a):** each step is what history took by the cause's last
     knot. These three take nothing, and today's rates stay the game's rates
     from today on.
   - **(b)** Project each row's full trial effect past 2024 on a ramp, so a
     default city's deaths fall after 2024 by the projected rest. This needs an
     uptake for each row, and none was gathered.
6. **★6 Polio's survivors.**
   - **Recommended (a):** deaths only.
   - **(b)** The paralysed survivors enter the long-sick pool, out of work, as
     M3's "fewer enter the long-sick pool disabled" says. The share left
     disabled was not gathered.

## 10. The batches

In this order. Each touches `CausesOfDeath`, `CausesOfDeathCheck` and
`MedicineCheck`, so none runs beside another. All are on the recommended calls
unless Jerus says otherwise, and all keep off FM2 and FM3. Every batch that
moves the default trace (MED2, MED4, MED5) runs the ensemble on the PC.

### 10.1 MED2

**Today's causes** (§3; ★1 (a)).

- **What it builds.**
  - Five new causes at the end of `CausesOfDeath.Cause`: HEART, STROKE,
    CANCER, DIABETES and KIDNEY.
  - Their shares by band (§3.1's table, from the tables named there). The
    shares of PNEUMONIA, DIARRHOEA (babies and children only) and TB today
    (§3.7).
  - Each new cause's H knots (§3.2-3.5). The elders' H fitted as §3.6 says.
- **Where it changes the code.**
  - The base's factor: wherever `Healthcare.mortalityFactors()` is used, all
    three calls.
  - The causes' booking: `book()`.
  - The save: `getState()` and `restore()`. Lengths grow and there is no
    format bump.
  - The Services screen's causes table, with today's causes and their 1900
    rate.
- **The harness** is new sections in `CausesOfDeathCheck`:
  - each share against Canada's 2024 rate;
  - H at 1 from 2024;
  - a default city bit-identical from 2025 on;
  - every band's 1921, 1951 and 1981 totals against Statistics Canada's life
    tables, within a stated tolerance.
- **Checks.** `SaveFileCheck` and `ReadPathCheck` for the state, and the
  ensemble on the PC.
- **Size:** one builder; the mechanism is the work.

### 10.2 MED3

**The rows' steps** (§4; ★4 (a), ★5 (a)). After R6f, which adds M12 and M19 to
`research.json` and `MedicalRows.CODES`.

- **The steps.** PM2 (by band), M7, M10, L6, M13, M17 and M11 as
  `CausesOfDeath.STEPS`. Each factor is a dial with its sentence, as R2f's
  were.
- **The new rows.** M11, M13-M18, M21, M22 and L6 enter `research.json` in date
  order (R6-G-K ★1), with their work from R6-G's table, and enter
  `MedicalRows.CODES`.
- **M14** joins general care's stays by R6f's rule if its share of stays is
  sourced, else it is a dated entry.
- **The harness.** `MedicineCheck`: each step's factor reached at the ramp's
  end, and a city finding a row 10 years early takes it 10 years early. Also
  `ResearchCheck`.
- **Size:** one builder.

### 10.3 MED4

**Polio and M3** (§5; ★6 (a)).

- **What it builds.**
  - POLIO at the end of `Cause`, with its yearly knots and its season.
  - The two age patterns.
  - M3's step.
- **The harness** is a new section in `CausesOfDeathCheck`:
  - 1953's deaths per 100,000 in a default city;
  - the season's months;
  - a city with M3 in 1940 has no 1953.
- **Checks.** The ensemble.
- **Size:** small.

### 10.4 MED5

**Dialysis's pool and M4** (§6; ★2 (a)).

- **What it builds.**
  - The kidney-failure pool by band, its incidence from USRDS's Annual Data
    Report by age, read first.
  - Served at general care's coverage once M4 is known. Unserved deaths are
    booked as KIDNEY.
- **The save.** `DataSave` and `HistorySave` for the pool and its flows, and
  the load path.
- **The harness** is a new section, in `SicknessCheck` or a new
  `DialysisCheck`:
  - a city with no M4 loses its incidence each year;
  - a fully served one keeps its pool at the dialysis death rate.
- **Checks.** `SaveFileCheck` and the ensemble.
- **On ★2 (b)** it adds the Dialysis Unit (§6's figures; the next id in
  `buildings.json`; `BuildingManager`'s template; `BuildingDataCheck`) and the
  DIALYSIS care type. That is two builders.

### 10.5 The mental hospital, if ★3 is (b)

This has no id until it is queued.

- **The building.** A PSYCHIATRIC care type, and a Mental Hospital with these
  figures (est., from MH-3 to MH-5):

  | Item | Value |
  |---|---|
  | Patients | about 1,100 (1955's average) |
  | Staff | 32 per 100 patients in 1956, nursing 57% |
  | Upkeep | scaled from $2.97 a patient-day in 1955 |
  | Opened by | 1900 |
- **The need.** The 1955 admission rates by age, and stays from 4.1 years
  before M23 to 2.0 after (§7).
- **The patients** are housed in it, outside the labour force.
- **M23, chlorpromazine** (R6-G-K ★1's rule for a 1951-1995 row), May 1954,
  halves the stay.
- **The rest.** The new care type's readers, as in ★2 (b). A harness for the
  need and the stays. No death effect.

## 11. Sources and licences

Every figure is in the sheets, with its key, its mark and its source's licence.
The sources that would ship as knots or series:

| source | what | licence | ships |
|---|---|---|---|
| Statistics Canada, tables 13-10-0394-01, 13-10-0392-01, 13-10-0800-01, 13-10-0933-01 | 2000-2024 rates by cause and age | Statistics Canada Open Licence | yes |
| Statistics Canada, "Changes in causes of death, 1950 to 2012" (11-630-X) | circulatory and cancer by sex, 1950-2012 | Open Licence | yes |
| Historical Statistics of Canada (11-516-X), section B | cardiovascular-renal 1921-55; mental hospitals | Open Licence | yes |
| DBS Year Books and *Mental Health Statistics 1955-63* (83-505) | polio deaths; hospital patients and stays | Open Licence | yes |
| PHAC, notifiable diseases 1924-2016 | polio cases | Open Government Licence - Canada | yes |
| NCHS, mortality trends 1900-2018 and HIST290/293 | the US shapes before 1950; diabetes by age | US government work | yes |
| NIDDK and USRDS | insulin's effect; the dialysis pool's incidence and death rate | US government work (the ADR is public domain) | yes |
| OECD health statistics | psychiatric beds today | OECD terms, commercial use allowed | yes |
| Trials and decompositions (4S, CTT, SIRIUS, SELECT, Ford, Wijeysundera, the smoke-free meta-analysis, Cochrane) | the steps' factors | journal facts, cited | facts only |
| CIHI (the organ register, psychiatric beds) | Canada's dialysis counts | commercial use needs written leave | no |
| Heart and Stroke, Diabetes Canada, the Kidney Foundation, the Canadian Cancer Society, WHO, IHME | findings only | no or unclear | no |
