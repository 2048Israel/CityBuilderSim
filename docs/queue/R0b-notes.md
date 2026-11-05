# R0b - causes of death in Health ({next}, 2026-11-04)

The second batch of research from 1900: its row is "causes of death in
Health - typhoid, summer diarrhoea, outbreaks, TB, home births, pneumonia,
the 1918 influenza" (`docs/studies/research-1900.md` §2.7, §4's causes-of-death
row, §11 Q3 and Q14, §12 item 2). Jerus took every call of the study as
recommended (2026-10-18, "rec all"). Built on R0 (lane 1's predecessor,
its ship file up at 778fbe90, not yet on main at the start).

## WHERE IT STANDS

(Kept at the top; a session resuming from GitHub starts here.)

- **2026-11-04 21:55:** started on `origin/batch/R0` (R0 not yet on main).
  Slice 1 committed (8ba5f54f: `CausesOfDeath` and `CausesOfDeathCheck` 1-8,
  133 OK); `Health` carries the influenza's sick (3afff4c2). Slices 2 (the
  month, the save, the People page, the inbox) and 3 (the screen) with Sonnet.

## 1. What the game has now, measured

A scratch probe (`LongPlaytest.probeCity` and `probePlay`, the default city,
R0's tree) read the dead by decade, 1900-2000:

| decade | people | deaths per 1,000 a year | babies' deaths per 1,000 babies | adults' per 1,000 adults | general care | childcare |
|---|---:|---:|---:|---:|---:|---:|
| 1900s | 3,936 | 13.2 | 5.0 | 2.36 | 99% | 51% |
| 1910s | 14,890 | 15.5 | 16.7 | 2.38 | 96% | 14% |
| 1920s | 33,080 | 15.7 | 30.1 | 2.43 | 95% | 4% |
| 1950s | 159,335 | 17.0 | 39.0 | 2.46 | 91% | 1% |
| 1990s | 369,409 | 17.7 | 41.5 | 2.72 | 84% | 0% |

Two things follow, and they decide the shape of the batch:

- **Babies and children already die at 1900's rate where there is no
  childcare.** `Healthcare.CHILDCARE_SWING` (40) takes the babies'
  `AgeBand` rate of 0.10% a year to 4.0% unserved, and the children's 0.0075%
  to 0.30%; Canada's 1901 under-sixes died at roughly 3.4% a year (est., from
  §2.7's 144 and 124 per 1,000 infants and the US's 30.4% of deaths under five).
  Causes added on top would count those deaths twice.
- **Adults, teenagers, seniors and elders die at today's rate.** 2.4 per 1,000
  adults a year, against roughly 8 in 1901 (est.); general care at 90-99% from
  the first decade keeps the long sick's deaths (Sickness) near nothing. The
  causes of 1900 are missing from these bands.

## 2. What R0b builds

`CausesOfDeath` (new, beside `Sickness`): seven causes, each a death rate by
band, a competing risk handed to `PopulationCohorts.advanceMonth()` beside
illness and violence.

| cause | 1900 rate, per 100,000 a year | who | season | what the city does to it | history's fall (each step named by its catalogue row) |
|---|---:|---|---|---|---|
| typhoid | 40.8 (Toronto 1910) | adults most | - | water: the share without piped water drinks at 1900's rate whatever the year | flat to 1910; chlorination (PE9, Toronto) to 1/40.8 by 1915; 0.5 by 1950 |
| summer diarrhoea | 155 (US 1900) | babies 85% | Jul-Sep x2.5 | water, as typhoid; hunger | 72 by 1921-25, 14 by 1946-50 |
| childhood outbreaks (diphtheria, whooping cough, measles, scarlet fever) | 82 (US 1900's 243 per 100,000 under 15, over all ages) | babies and children | carried by `Health`'s outbreaks: none between them | care blunts them as it blunts the outbreak | 0.44 of 1900 by 1923 (toxoid PM3 from 1925); 0.055 by 1948 |
| TB | 180 (Canada 1901) | adults 73% | - | hunger; no general care | 37 by 1946-50; streptomycin (PM6, Canada 1948) -74% by 1952 |
| pneumonia | 202 (US 1900) | babies, adults, the old | Dec-Mar x1.6 | no general care | 141 by 1921-25; sulfa (PM4, 1937) -25% by 1940; 55 by 1946-50 |
| childbirth (mothers at home births) | 4.7 per 1,000 births (1921, held back to 1900) | adults | - | no general care | 5.8 by 1930; sulfa (PM4) -30% by 1940; penicillin (PM5) 2.8 in 1944, 1.1 in 1950 |
| the 1918 influenza | 0.6% of the people (about 50,000 Canadians) | young adults most | Oct 1918 - Mar 1920 in three waves | - | an episode; a quarter of the people sick in it (est.), on `Health`'s rate |

Every cause runs on to zero by 1980 (est.: today's rates are in `AgeBand`'s
own), so a city past 1980 dies as it does today. Who dies of each (the shares
of its deaths by band) and the 1901 pyramid that turns an all-ages rate into
a band's are est., and say so where they are set.

## 3. Slices

1. **The model** (Sonnet): `CausesOfDeath` - the causes, their rates, shares,
   seasons, conditions and history; the babies' and children's split; the
   influenza's months; the month's dead booked by cause, the last twelve
   months and the totals; its state for the save. `CausesOfDeathCheck`,
   sections 1-6 on the model alone. Its line in `AllChecks`.
2. **The month and the save** (Sonnet): `Game.advanceDemographics` reads the
   causes and hands them to the cohorts as a fourth risk (`PopulationCohorts`
   books their dead, saved with the other parts of the month's dead); the
   babies' and children's factor keeps only the swing's part the causes do not
   name; `Health` carries the influenza's sick; `DataSave` and the load path;
   `SaveFileCheck`'s parts of the dead; the People page's waterfall gains the
   causes' part; `CausesOfDeathCheck` 7-9 on a real city.
3. **The screen** (Sonnet, beside slice 2): Services, Health - "What people
   die of": each cause's dead in the last twelve months, per 100,000 a year,
   beside 1900's rate, and what lowers it. Checked by eye.

## 4. The calls (★), each taken as recommended

- **★1 Where the causes live.** The study says "terms in `Health`". `Health`'s
  own rule is "nobody dies here": it says how many are off sick, and `Sickness`
  kills the long sick. **Recommended and taken:** a class of their own beside
  `Sickness`; `Health` keeps one term, the influenza's sick, of the shape of
  the unburied's and the hungry's.
- **★2 Add, or split what is already there.** **Recommended and taken:** for
  the bands whose rate is today's (teens, adults, seniors, elders) the causes
  add. For babies and children, whose unserved rate childcare's swing already
  sets at 1900's level (section 1), the causes take their 1900 share of the
  swing's deaths above today's rate (babies about 46%, children capped at 90%)
  and name them; history then lowers their part. A 1900 city's babies die as
  before; from 1910 fewer die where there is no childcare. **For Jerus:** after
  1980 an unserved city's babies die at about 22 times today's rate where they
  died at 40, and its children at about 5 times; `CHILDCARE_SWING` is
  untouched, and is the dial if childcare should bite as hard as before.
- **★3 What lowers a cause before the catalogue.** The catalogue's rows (R2)
  and the 1900 plant (R1) are not built. **Recommended and taken:** each cause
  follows Canada's observed rates on their dates (§2.7's 1900, 1921-25 and
  1946-50 columns, the rows' own figures), each step named by its row, so a
  city with no research has every entry on its date (§12's first harness). R2
  turns the named steps into the city's finds. The city's own conditions raise
  a cause above history's path, never below: water short, hunger, no general
  care.
- **★4 TB and the long sick.** The study: "TB fills the game's queue of people
  sick for a year or more". `Sickness`'s ring is matched to the city's sick
  rate every month and scales every slot down when the rate falls, so a TB
  patient put in it would be cured by the end of a flu. **Recommended and
  taken:** the TB sick are counted beside the ring (the deaths over the long
  sick's monthly chance, `Sickness.deathChance(ADULT)`), shown as the long sick
  of TB, and die of TB.
- **★5 No absence from the causes.** The sick rate kills through the ring, so a
  cause that also raised it would kill twice. **Recommended and taken:** the
  causes are deaths only; the one exception is the 1918 influenza's sick, which
  were the epidemic's cost to work.
- **★6 Mothers and the hospital.** Births in hospital rose from 5% (US, 1900) to
  76% (Canada, 1950), but mothers' deaths did not fall until sulfa (1937).
  **Recommended and taken:** mothers' deaths follow the date; a city with no
  general care raises them (no doctor at the birth).

## 5. What it does to a city

The same probe as section 1, on the built model (db4c7fa8), before and after:

| decade | people, before | after | deaths per 1,000, before | after | Canada | babies' per 1,000 babies, before | after | adults' per 1,000 adults, before | after |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 1900s | 3,936 | 4,186 | 13.2 | 17.2 | about 16 | 5.0 | 4.4 | 2.4 | 6.4 |
| 1920s | 33,080 | 30,363 | 15.7 | 16.6 | about 11 | 30.1 | 21.4 | 2.4 | 5.1 |
| 1950s | 159,335 | 134,912 | 17.0 | 15.3 | about 9 | 39.0 | 22.0 | 2.5 | 2.8 |
| 2000s | 373,593 | 320,896 | 17.5 | 15.7 | about 7 | 40.9 | 22.9 | 2.6 | 2.7 |

A 1900 city now dies about as Canada did (17 per 1,000), most of it adults'
TB, pneumonia and typhoid. The adults' extra dead cost the probe city 15% of its
people by the 1950s, which it does not win back (the arrivals follow the jobs).
From the 1920s fewer babies die where there is no childcare (history took the
diarrhoea, the outbreaks and pneumonia), so the city's deaths per 1,000 end
below what they were. The game still dies at about twice Canada's rate after
1950. That is the swing's babies (23 per 1,000 a year where Canada's were under
1) and the old, not the causes, which are nought by 1980.

What the eye should look at (Jerus, only if it looks wrong): Services, Health,
"What people die of", in a 1900 city and again past 1920 (each cause's year,
its rate beside 1900's); the sick rate's bar in October 1918 (the influenza's
part); the inbox's "Influenza is in the city" from October 1918; People, the
month's dead with its new part, "of typhoid, TB, childbirth and the like".
</content>
</invoke>
