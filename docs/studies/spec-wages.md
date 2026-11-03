# Wages: a real minimum, and the Wages screen - the study, 2026-11-02

Read-only study of 0.9.2 (main at 5602c6d0), for Jerus's asks of 2026-11-02: "yes a real minimum wage"; a bar chart of each wage step and its cost ("technically they should be all roughly equal after cost right?"); a floor that says whether it is too much or too little; a Wages screen that is intuitive; and "in big cities, unemployment avgs 22% roughly". Prototypes and outputs in `wages/`: `WageProbe.java` (a playtest city, one CSV row a month of the whole ladder), `real-minimum.patch` and `migration-target.patch` (the two rules as prototypes, each neutral until a property asks), `run.sh` (the runs), `summarize.py` (the tables), `ladder.py` (pay after cost), `SaveLadder.java` and `saves.py` (his saves, read only), `mockup.html` (the screen), `out/` (every table quoted here).

Each figure is one of four kinds: **sourced** [S1] (section 8, with its licence; the game is sold, so a figure that reaches the game comes only from a source that allows commercial use); **derived**, with the sum shown; **measured**, with the run named (`out/`); or **★est.**, a named estimate with its reason. The runs are the ensemble's AUTO2 city (the founding and rhythm of `tools/ensemble/Ensemble.java`), 4 seeds × 2,400 months a variant, medians over the seeds, measured from month 240 unless said; each city in a `TempDirs` folder, never a save slot.

## 1. Summary for Jerus

1. **Today the floor is the city's wage.** Every wage is the floor × a fixed ratio (PayTier's) × a shortage multiple, so the floor anchors the whole ladder. In the playtest city, from month 240, **94% of all workers are paid exactly the floor**, the median wage *is* the floor, and the diploma band sits on it 99.7% of months (measured, `out/runs-floor.txt`). That is why the number tells you nothing: it is not under the wages, it is the wages.
2. **A real minimum, as the model:** the ladder is anchored on the **going rate** (the founding unskilled rung, lifted by the cost of living as every wage already is), and the floor only lifts the wages under it. At today's default it changes nothing, byte for byte (measured). It matters in two places: where the default sits, and what moving it does.
3. **Where the default sits is your first call.** At the going rate (today) it holds 94% of workers. At **0.85 of it** (Nova Scotia's legal minimum, $17.00 an hour since 1 October 2026 [S2], over the labourer's median $20.00 [S3] that the unskilled rung already is) it holds **2.5%**: unskilled pay settles at 0.88-0.96 of the going rate, the median worker is paid 6-11% less, **unemployment is 3 points lower** (9.3% against 12.2%), inflation the same within 0.1 point, the city's size the same within the seeds' noise. At 0.90 it holds 59%. I recommend 0.85.
4. **Moving it, measured:** a floor raised 20% at month 240 kept unemployment 7-8 points higher for twenty years and the city 10-24% smaller at month 480 (every seed), with the median worker 20% better paid; the surplus cannot be priced away, so it leaves. A real minimum does the same to the bottom and leaves the skilled wages alone, so their lead narrows (college's after-cost lead over no diploma falls from 1.58× to 1.32×).
5. **The dial, read three ways:** against the going rate (the one that says whether it bites: under 70% never, 70-100% in a glut, over 100% always); against the average wage, beside the 33 OECD nations the game's world file already ships (28 of them at 37-55% [S1]); and what a full-time minimum job's take-home covers of living alone (131% at 0.85 over the runs). Plus who is near it, a preview of the floor's own move only, and a tie: to prices (today), to the average wage, or to nothing.
6. **The ladder chart:** one bar per qualification with its pay after cost (school years, fees, interest, wages given up), drawn as they are, inverted or not (`mockup.html`). **Roughly equal after cost?** Only if anyone could take any step: then people keep training until the steps pay the same, and a step left above the others shows what is holding them back. In life they are not equal either: a bachelor's pays 1.41× upper secondary across the OECD and 1.48× in Canada, for three or four years of study [S5]. Here, after every cost, college pays 1.6× no diploma, university 1.9× and a diploma 1.00× (measured), because seats and a sixtieth a month bind.
7. **Schooling answers to pay, partly.** College and university intake follow the next band's best wage over this band's (`Education.returnOn()`), but nothing of the cost: not the years, the fees, the interest or the wages given up. High school follows nothing but places and fees. And a degree's return is read off the **doctor's** wage, which a degree alone cannot earn. I recommend intake read pay after cost (your third call).
8. **Your screenshot's oddities, found in your own Stockholm (the autosave, month 61):**
   - **a diploma pays the same as none:** the unskilled band is short (93 posts, 3.5 people without a diploma), so diploma holders fill those posts and the two bands are one market at one wage, $136.5. In grown cities it is the floor instead, holding both; either way a city has more than two diploma holders for every diploma post (2.1 in Stockholm, 2.5 in the grown playtest city), because every migrant and every schooled teen holds one;
   - **scientists below college:** the city has no scientist or policy posts at all; their wages are still walking up to targets that are in the right order ($299 against college's $289);
   - **"$218 → $326"** is the walk to today's target and the floor's own move, added;
   - **"not binding on anybody"** is true there: the floor is $89.6, the unskilled wage $136.5.
9. **The 21% is migration's, not wages'.** Your big cities (slots 1, 2 and 4-8) sit at 17.7-21.8% out of work, at 1.25-1.32 workers a post, and every one has senior care at its full pull, **×1.30 on migration's whole target, jobs included** (read from your saves, `out/saves.txt`). The target aims at 12.5% more workers than posts (about 11% out of work) before that. Slots 3 and 9 sit at 0-3% only because their dear rents halve the draw. Reproduced in the playtest: senior care's pull held at 1.30 takes the same city from 12% to **30%** (measured).
10. **What healthy means, and what the game can reach.** Big cities in rich countries run 2.4% (Tokyo) to 9% (Madrid); 20% is a city in crisis (Madrid in 2013) [S4]. The game's own city cannot go much under 10% by migration alone: a business builds only when 80% of a building's posts can be filled from spare workers, so the jobless are what the city grows on, and every rule that brought fewer people (a lower slack, a brake on arrivals, the jobless leaving, vacancies drawing people) cut 2-3 points and left every seed 30-80% smaller at month 480. What migration can do is take senior care's excess away: with the pulls lifting only the homes' draw, arrivals slowing past 6% and the jobless past it leaving, a senior-care city goes from 30% to 12-13% in every seed, smaller in its young decades and no different by month 2,400 within the seeds' noise. I recommend that, the verdict green at 3-7%, amber to 12%, red past it, and a study of the staffing gate for anything lower (your second call).
11. **Five batches and a study** (section 6): WG1 the real minimum and the floor's tie; WG2 senior care's excess taken off migration and the honest verdict; WG3 schooling on pay after cost; WS1 the minimum-wage page; WS2 the ladder page; GW0, a study of the staffing gate and growth. WG1, WG2 and WG3 move the trace (ensemble each); WG2 can run beside WG1.

## 2. What the code does today

### 2.1 The ladder (`LabourMarket`, `PayTier`, `PopulationManager.fillByBand()`)

    base(job)   = minimumWage × ratio(job) × costOfLiving     ratio = PayTier wage ÷ the unskilled rung
    multiple    = clamp(tightness ^ .5, MIN_MULTIPLE .70, MAX_MULTIPLE 4.0)      per band (×licence premium for a gated job)
    target(job) = base(job) × multiple
    wage       += (target − wage) × ADJUST_RATE .12                               a month
    wage        = max(wage, minimumWage × costOfLiving × (1 + nudge))             the floor, at once

- **The ratios** are PayTier's Nova Scotia medians: no diploma 1.00, diploma 1.30, college 1.59, science and policy 2.37, finance and engineering 2.77, medicine and law 4.51 [S3].
- **The floor anchors it.** `baseWage()` is the floor times the ratio, so the dial moves every base; `PolicyPreview.wageMoveAt()` divides the unskilled job's target at two settings, and every target is the floor times a fixed number, so "every wage, once walked" moves by exactly the dial's own percentage: a +10.5% there is the dial having moved 10.5% (it steps in two-hundredths of its range, 2.4% of the founding rung, so a round 10% is rarely on it).
- **A band's floor of its own:** a glut takes its multiple to .70, which for the diploma band is .70 × 1.30 = .91 of the unskilled base, under the floor; so a diploma glut lands on the floor beside the unskilled.
- **One market where the bottom is short:** workers take the best-paid post they qualify for. When a band below is short and pays more than the band above, the two are filled as one market at one wage, lambda (0.7.18's rule).
- **The words:** the screen says "Every wage in the city is a multiple of this number" (true: the base) and, when no band is at its own floor with people spare, "the minimum wage is not currently binding on anybody" (also true, of the clamp). Both true, and together they read as a contradiction.

### 2.2 Who is paid the floor (measured, `out/runs-floor.txt`, variant `today`)

| from month 240 | today's model |
|---|---|
| workers paid exactly the floor | 94.3% |
| the floor over the median wage; over the average | 1.00; 0.95 |
| months the unskilled band is held by the floor; the diploma band | 100%; 99.7% |
| months a diploma pays the same as none | 98.1% |
| out of work (from month 1,200) | 12.2% (10.1%) |
| workers per post | 1.12 |
| a full-time floor job's take-home over living alone (rent per door + groceries + fees per head, `HouseholdAccounts.livingAlonePressure()`'s cost) | 158% |
| pay after cost over no diploma (`ladder.py`): diploma, college, university | 1.00×, 1.57×, 1.92× |

**Why 94%:** the unskilled and diploma posts are 94% of all posts (at month 1,200 of seed 0: 22,280 and 11,060 of 35,570), and both bands have more workers than posts all the time (27,790 diploma holders for those 11,060 diploma posts). Migration aims at 12.5% more workers than posts and the surplus cascades to the bottom; the diploma band has 2.5 holders a post, because every arrival holds one (`WageBand`: "A DIPLOMA IS THE BASE, at 1.00 and unconditional") and every teen with a school place leaves with one. Their market wages (multiples .88-.96 and .70) fall under a floor set at the going rate, and the floor holds them.

### 2.3 The screenshot's oddities, found (`SaveLadder`, his autosave Stockholm, 0.9.1, month 61, read from a copy; `out/stockholm-m61.txt`)

| band | posts | supply | own | multiple | job: wage → target |
|---|---|---|---|---|---|
| No diploma | 93 | 87.7 | 3.5 | 1.47 | $136.5 → $132.0 |
| Diploma | 77 | 122.9 | 161.3 | 1.13 | $136.5 → $132.0 |
| College | 54 | 20.3 | 20.2 | 2.03 | business $226.8 → $288.6 |
| University | 1.1 | 1.0 | 1.1 | 1.41 | science, policy (0 posts) $219.3 → $299.5; finance (8 posts, 1 held) $512.9 → $698.0 |

The floor is $89.6 ($89.56, the founding rung of 1900, cost of living 1.00).

- **A diploma pays the same as none.** One market: the unskilled posts are short and the diploma holders, 84 spare, fill them, so both bands pay lambda (both targets $132.0). In grown cities the same tie is the floor holding both (2.2). Measured over the four seeds: in months 24-240 a diploma pays the same as none 95% of the time, the joined market above the floor in 15-26% of those months and the floor in the rest; from month 240, the floor always (`young.py`, `out/young.txt`).
- **Scientists and policy below college.** The city has no scientist or policy posts (nor has the playtest city in 54-58% of months 24-240). Their band's multiple is set by its one finance post, and their wages, like college's, are walking up, 12% of the gap a month (`ADJUST_RATE`), toward targets that stand in the right order ($299.5 over $288.6). The walk is the inversion: in months 24-240 a college wage is more than 10% off its target in 15-23% of months (0-1% later). A second, rarer case, measured in seed 3: when college is twice short its wage passes the university base, and a university band whose posts are mostly licensed cannot follow (19 of 864 young months, under 1% apart).
- **"$218 → $326".** The ghost bar is `targetWageAt(job, staged floor)`: the target at the staged floor, set against the wage as it is. Here college's wage is $226.8 against a target of $288.6 at today's floor, so most of the arrow is the walk; the rest is the floor's own move.
- **"Not binding on anybody"** is right in this city: unskilled pay is 1.52× the floor. In the playtest's grown cities the other sentence is the right one 100% of months.
- **11.7% "a healthy slack"**: `PeopleScreen.OUT_OF_WORK_SHORT` .03, `_HIGH` .15 and `_FAR` .25 make green anything from 3% to 15% (2.4 and 4, C2).

### 2.4 The 21%: migration's target (`Migration.monthlyNet()`; his saves, `out/saves.txt`)

    target = (JOB_WEIGHT .75 × jobs × residentsPerJob + HOME_WEIGHT .25 × home capacity) × seniorPull × affordabilityPull × crimePull
    residentsPerJob = (1 + TARGET_LABOUR_SLACK .125) ÷ adult share         1.125 workers a post: "about 11% unemployment"

The senior-care pull (up to `SENIOR_CARE_PULL` +30% at full coverage) multiplies the whole target, the jobs' draw with it, so a city with full senior care aims at 1.125 × 1.30 = 1.46 workers a post from the jobs alone (derived) and gets the homes' quarter on top.

| slot | people | out of work, last 10 years | workers a post | senior pull | homes' share of the draw |
|---|---|---|---|---|---|
| 1 | 1,729,091 | 21.3% | 1.30 | 1.30 | 23.8% |
| 2 | 160,626 | 17.7% | 1.25 | 1.30 | 23.1% |
| 4 | 590,291 | 20.5% | 1.27 | 1.30 | 23.7% |
| 5 | 6,759,121 | 21.3% | 1.32 | 1.30 | 23.6% |
| 6 | 1,104,518 | 21.1% | 1.30 | 1.30 | 23.6% |
| 7 | 1,604,631 | 21.8% | 1.31 | 1.30 | 24.6% |
| 8 | 3,751,459 | 21.5% | 1.30 | 1.30 | 23.5% |
| 3 | 2,913,384 | 0.0% | 1.01 | 1.25 | 25.5% (affordability pull .54) |
| 9 | 6,419,054 | 3.1% | 1.06 | 1.30 | 24.1% (affordability pull .46) |

Pooled over every slot's months from 120: a median of 21.1% in cities over a million, 20.7% from 100,000 to a million, 15.0% from 10,000 to 100,000. The playtest builds little senior care (pull 1.02) and sits at 12% with 1.12 workers a post (2.2). So the 22% is the target itself; wages have no part in it.

**Reproduced in the playtest:** the same city with senior care held at its full pull (`-Dwages.seniorPull=1.3`, variant `sen13`) runs at 30.4% out of work and 1.42 workers a post, against today's 12.2% and 1.12, with the median worker's pay unchanged (table 2). The playtest's city is younger and less crowded than his, so it lands higher than his 21%; the cause is the same.

### 2.5 Who studies, and whether it answers to pay (`Education`)

- **High school** (`diplomas()`): teens leaving × the ladder's coverage (the least of elementary, middle and high school places) × whether the family can afford the fee. No pay in it.
- **College, university, the four professional schools** (`study()`): intake = eligible × `MAX_PARTICIPATION` .90 × return × affordability × `ENROLMENT_RATE` 1/60, up to the free seats; the course keeps them `EducationType.months()` (24, 48; medicine 84, law 36, business 24, engineering 48).
- **The return** (`returnOn()`): `min(1, (after ÷ now − 1) ^ 1.3)`, after and now the best-paid job in each band. Three things wrong with it as a reading of pay:
  - no cost in it (the fee is only in affordability, the years and the wages given up nowhere);
  - it saturates at 2× (a degree worth 2.5× reads as one worth 2×);
  - a degree's "after" is `bandWage(UNIVERSITY)`, the best university job, which is the doctor's ($4,766 against science's $2,372 at month 1,200 of seed 0), though a degree alone cannot hold it. Professional schools read the ungated wage as "now" correctly.
- **Measured consequence:** after cost the steps stay far apart for the whole run (2.2): seats and the sixtieth a month bind, and nothing pulls the steps together but slowly.

## 3. The design, on the code

### 3.1 A real minimum (`LabourMarket`; prototype `real-minimum.patch`)

- **The anchor:** `GOING_RATE = PayTier.UNSKILLED.getMonthlyWage()`, a constant in founding money: what an unskilled post pays where there are exactly enough workers. `baseWage(job) = GOING_RATE × ratioOf(job) × costOfLiving`; `targetWageAt(job, floor)` the same with the floor only in the `max()`.
- **The floor:** unchanged in form, `wage = max(wage, cashMinimumWage())`, now lifting only the wages under it. `isPinned()` unchanged (`max(base × MIN_MULTIPLE, cash floor)`), so Migration's surplus departures read the same test.
- **The default:** `DEFAULT_MINIMUM_WAGE = GOING_RATE × DEFAULT_FLOOR_SHARE`, the share by C1 (0.85 recommended: $17.00 ÷ $20.00 [S2][S3]). The dial's bounds stay multiples of the going rate (.25 to 5.0).
- **What reads the floor and changes meaning:** `PolicyPreview.wageMoveAt()` and `cityPayrollAt()` become the floor's lift (workers lifted, their pay, the payroll); `CityMeasures`' `min_wage` and `kaitz` (the Rankings) keep reading `cashMinimumWage()`, now a real minimum; `PolicyScreen`'s words (FLOOR_INFO, PINNED_WORDS, FREE_WORDS, the city-payroll card: "every one of their wages is a multiple of the floor too") are rewritten in WG1 because they become false; `Migration`'s premium (wage ÷ base) now reads above 1 for the unskilled when the floor is over the going rate, which draws unskilled arrivals at the band's ceiling; measured as part of `real12` (section 5).
- **Measured neutral at the old default:** `today` and the patch at floor 1.0 give byte-identical CSVs (600 and 2,400 months, seed 0).

### 3.2 The floor's tie (`LabourMarket`, saved)

- **Prices** (today's rule, the default): the floor is set in founding money and lifted by the cost of living.
- **The average wage**, at a share the player sets (default 45%, ★est.: the middle of the OECD's 37-55% [S1]): each month the real floor is struck as share × last month's average wage over the cost of living. The average is the Rankings' (`YearBook.averageWage()`: the wage bill over posts filled).
- **Nothing:** a cash figure, fixed until moved; inflation wears it down, deflation makes it bite. The gold study notes that today wages follow prices down as well as up (`COST_OF_LIVING_PASS_THROUGH` 1.0 both ways, no ratchet); this tie is the one way the game would have a wage that does not fall (section 7).
- **Saved:** the tie and its share (`DataSave`, the load path; `SaveFileCheck`, `ReadPathCheck`).

### 3.3 The ripple (measured, and left out)

The discussed rule: the band just above the highest band the floor holds keeps half its usual step over it. Prototyped (`-Dwages.ripple=.5`) and measured: **it never fired** (`real12r` is byte-identical to `real12`). A floor high enough to hold the unskilled holds the diploma band too (its market wage is .91 of the going rate at most in a glut), and the next band up, college, is short and well above it. So it is left out; the batch notes say so.

### 3.4 Migration aims at a healthy slack (`Migration`; prototype `migration-target.patch`)

**Why less slack shrinks a city here.** A business may order a building only when 80% of its posts can be filled from the city's spare workers (`Sector.MIN_STAFFABLE_TO_ORDER` .80, the staffing gate). So the jobless are what the city grows on: every rule measured that brings fewer people than today's also brings fewer buildings, and the city is smaller (table 2: a lower slack, 34-53% fewer people at month 480 in every seed; a brake on arrivals, 38-80%). A real city grows the other way round too: a firm posts a vacancy and people move for it. The game already counts those vacancies: the posts the staffing gate refused (`WantedPosts`, LD1), which the labour market prices as if they were offered. So the rule has four parts, each prototyped behind its own property:

1. **The pulls lift the homes' draw only** (`-Dwages.pullsOnHomes`): senior care, cheap rents and safety change how many people the city's homes draw; the jobs' draw is cut by a dear or unsafe city and never raised. `target = .75 × jobTarget × min(1, affordability × crime) + .25 × homeTarget × senior × affordability × crime`.
2. **Arrivals slow past a healthy rate** (`-Dwages.brake=.06/.12`): full under 6% out of work, none over 12%, a straight line between (★est.: 6% is the middle of big rich-country cities [S4]; twice it is where the verdict turns red).
3. **The jobless past the healthy rate leave** (`-Dwages.leave`): those over 6% go at `SURPLUS_DEPARTURE_RATE` (2% a month), whatever their wage, less those the pinned bands already sent. Today only a pinned band's surplus leaves, so under a real minimum that pins nobody, nobody leaves for want of work.
4. **Vacancies draw people as posts do** (`-Dwages.wanted`): the refused posts of last month join `totalJobs` in the jobs' draw, so a city short of hands draws the people its businesses are waiting for. **Measured, and left out:** with parts 2-3 it kept unemployment at 9.9% and every seed was still 30-75% smaller at month 480 (table 2, `aimw`). The refused posts are few while there are spare workers to staff them, which is most months; the gate, not the draw, sets the pool.

**So WG2 is parts 1-3**, with the healthy rate and the stop at 6% and 12% (C2). The young city is the risk to measure first in the batch: it runs 12-18% out of work while its posts catch up with its founders, so the brake holds arrivals there (769 people at month 120 against 2,243); a brake that starts later (from month 240, or from 8% to 16%) is the first thing WG2 tries. **Anything under about 10%** needs the gate itself to change, so that a business may order a building its wage would draw the workers for; that is growth, not wages, and goes to a study (GW0).

`TARGET_LABOUR_SLACK` stays .125: the measured slack (table 2, `slack05`) moved unemployment by under 2 points and the city's size by a quarter, so the aim is set by parts 2 and 3, not by it. The verdict: `PeopleScreen.OUT_OF_WORK_SHORT` .03, `_HIGH` .07, `_FAR` .12, and the tip's words with the cities beside it [S4]. Measured: section 5, table 2.

### 3.5 Schooling on pay after cost; the ladder model (new `WageLadder`, pure; `Education.returnOn()`)

- **`WageLadder.of(market, schools, tax, rate)`**, pure, one row per qualification: what a holder earns (the best job they may hold: a degree's best **ungated** job, a licence its own job), the course's months, its fees after the city's share, the wages given up while studying (the band studied from), and pay after cost, a level month over the working life (AgeBand.ADULT, 18 to 70: 624 months), at the student loan's rate, the policy rate if dearer, never under 3% a year (★est.: a family's own price of waiting; the loan's default is 0% and the policy rate sits at 0% for long spells). `ladder.py` is its prototype, line for line.
- **The return:** `returnOn()` reads `after-cost(next) ÷ after-cost(this) − 1` in place of the wage ratio, with the same curve, so a step's pull falls as its lead after cost closes, and the fee, the years and the wages given up are in it. The degree's "after" becomes the best ungated job.
- **High school** stays as it is (C3 asks): a teen gives up no wage, and a diploma's step is near zero after cost in every city measured, so a return on it would empty the high schools.
- **The households study** may split adults into stages (young, established, retired): if it does, the working life left is the stage's, and who may enrol is the stage's (section 7).

### 3.6 The screen (`PolicyScreen`, WAGES; `PeopleScreen`; `mockup.html`)

**The minimum-wage page** (WS1), top to bottom:
- **the dial card**, in today's money, with the founding figure under it, and one line: who it holds, or "nobody here is paid it: unskilled work goes for $893";
- **against the going rate**, a gauge 0-150% in three zones: under 70% never binds (the market alone stops at .70), 70-100% binds in a glut, over 100% binds even when hands are short; ticks for the floor, the unskilled wage and the staged floor;
- **against the average wage**, a gauge with the OECD's 33 nations from the world file (hatched 37-55%, ticked US, Canada, Costa Rica [S1]), and why this city reads high;
- **what a full-time minimum job pays for**: take-home after the payslip (`TaxPolicy.payslipShare()`) as a bar of rent, groceries and fees per head and what is left (`HouseholdAccounts`), red under 100% with "such workers share a home";
- **who is near it**: paid it, within 10% above it (by band; ★est., the reach the spillover evidence gives the next few dollars [S6]), everyone else; from the fill (`PopulationManager.getJobVacancy()`'s staffed posts and the wages);
- **the tie**: prices, the average wage at a share, nothing;
- **the preview**, the floor's own move only (each job's target at the staged floor against its target now): workers lifted, their pay, the payroll, everyone above unchanged; and the measured cost of a floor over the going rate, as evidence, not a forecast;
- **the words** for a joined market ("one market: diploma holders fill the unskilled posts, so both pay $136.5") and for a job with no posts (greyed, "no posts here").

**The ladder page** (WS2): one bar per qualification (no diploma, diploma, college, university, the four licences), paid now and pay after cost, posts and holders under each, the course's years, fees and wages given up, and a verdict: "far above the step below after cost: too few seats, or too dear"; "at or under the step below: few will take it, and its holders will get scarce"; "no posts here". The wages' own walk (wage against target) is a faint tick on each bar, never folded into the preview.

**The People screen:** the out-of-work tile and the header's line on the new thresholds (C2).

### 3.7 The save

- WG1: the tie and its share (two fields). No format bump: an old save keeps its saved floor (the going rate), which loads as a real minimum at the going rate; that is a city whose floor holds its bottom bands, as it did, not a wrong one.
- WG2, WG3, WS1, WS2: nothing saved. `WageLadder` is pure.

### 3.8 Premises that change (Jerus's decisions, so the harnesses are restated, not bent)

- **LabourCheck 2, "THE DIAL MOVES EVERYTHING"** ("the doctor's base doubled too", "the ladder kept its shape") becomes "the dial moves only the wages under it"; section 1, "at the default minimum wage, nothing has changed", holds only if C1 keeps the going rate.
- **PolicyPreviewCheck L581** ("every job's target moves by the floor's own ratio") becomes the lift of the jobs under it.
- **Fixtures that raise the floor to make labour dear** (`BusinessServicesCheck` L274 ×3, `ManufacturingCheck` L491 ×4, `ChartCheck` L229 ×1.1, `LabourCheck` L89 and L395 ×2): they still lift 95% of posts; each is read and restated in WG1 where it meant every wage.
- **PopulationCheck L501-518** (the slack's arithmetic) holds, since the slack stays; its migration sections gain one per part of 3.4 in WG2.
- **EducationCheck's return sections** follow WG3.

## 4. The calls

**Answered by Jerus, 2026-11-02: all four as recommended** - C1 (b) 0.85, "0.85, Nova Scotia's"; C2 (a), "Cut the excess, honest label"; C3 (a), "After cost, not high school"; C4, "Queue all six". The batches are in `QUEUE.md`.

**C1. Where the floor starts.** Options: (a) at the going rate, as today: byte-identical, and the floor holds 94% of workers; (b) at 0.85 of it, Nova Scotia's legal minimum over the labourer's median [S2][S3]: it holds 2.5% of workers (in a deep glut), unemployment 3 points lower, the median worker paid 6-11% less; (c) at 0.90: it holds 59%, unemployment 1-3 points lower, pay 6-10% lower. **Recommended: (b)**, the one sourced figure, and the only one where the dial is a protection with room under it. (Nova Scotia's minimum moves each April by last year's prices plus a point [S2]; the game's is held real, so 0.85 is the figure on the day, not a rule.)

**C2. Senior care's excess, and the verdict.** Big cities in rich countries run 2.4-9% [S4]; the game's city sits near 10% because of the staffing gate (3.4), and a senior-care city near 30% because of the pull. Options: (a) 3.4's parts 1-3 (the pulls on the homes' draw, arrivals slowing past 6%, the jobless past 6% leaving): a senior-care city to 12-13%, smaller in its young decades; the verdict green 3-7%, amber to 12%, red past it; GW0 studies the gate; (b) part 1 only and the verdict: a senior-care city to 17-19%; (c) the verdict only: his cities read red at 21%; (d) neither. **Recommended: (a).** Part 4 (vacancies drawing people) is left out: measured, it did not keep the cities' size (table 2).

**C3. Schooling on pay after cost.** Options: (a) college, university and the professional schools read pay after cost, the degree's return off its best ungated job, high school unchanged; (b) the same, and high school too; (c) the wage ratio as today, the degree fixed only. **Recommended: (a).**

**C4. The batches.** WG1, WG2, WG3, WS1 and WS2 and the study GW0 as section 6, at the end of the queue, WG2 beside WG1. **Recommended: take them.**

**Settled here without a call** (each can be overturned): the ripple is left out (prototyped, it never fired, 3.3); the tie starts on prices, as today, with the average wage at 45% and nothing offered beside it (3.2); "near the floor" is within 10% of it (3.6); high school's intake keeps its rule unless C3 says otherwise.

## 5. Measured

### Table 1: the floor (`out/runs-floor.txt`; 4 seeds × 2,400 months; from month 240)

| measure | today | real 0.85 | real 0.90 | real 0.825 and 0.60 | today, +20% at m240 | real, +20% at m240 |
|---|---|---|---|---|---|---|
| out of work | 12.2% | 9.3% | 11.1% | 9.3% | 14.6% | 14.5% |
| out of work from m1,200 | 10.1% | 7.3% | 9.5% | 7.3% | 12.3% | 12.9% |
| workers paid the floor | 94.3% | 2.5% | 58.9% | 0% | 94.2% | 94.3% |
| months the unskilled band is held by the floor | 100% | 3.8% | 60.2% | 0% | 100% | 100% |
| floor ÷ median wage | 1.00 | 0.92 | 0.99 | 0.89 (0.65) | 1.00 | 1.00 |
| floor ÷ average wage | 0.95 | 0.88 | 0.93 | 0.85 (0.62) | 0.95 | 0.97 |
| months a diploma pays the same as none | 98.1% | 36.9% | 47.3% | 36.9% | 100% | 100% |
| a floor job's take-home over living alone | 158% | 131% | 143% | 127% (93%) | 198% | 196% |
| the median worker's pay over the going rate | 1.00 | 0.925 | 0.909 | 0.925 | 1.20 | 1.20 |
| leaving a year (share of people) | 5.8% | 4.9% | 4.8% | 4.9% | 6.2% | 6.0% |
| inflation a year | 1.97% | 2.09% | 2.12% | 2.09% | 2.07% | 2.02% |
| after cost over no diploma: college; university | 1.57; 1.92 | 1.68; 2.06 | 1.70; 2.08 | 1.68; 2.06 | 1.58; 1.95 | 1.32; 1.59 |

Against `today`, same seed (median over seeds; out of work in points, the median worker's pay, people at the window's end):

| variant | m240-480 | m480-1,200 | m1,200-2,400 |
|---|---|---|---|
| real 0.85 | −2.7 pt; −6.2%; −0.3% | −3.6 pt; −10.9%; −2.1% | −3.1 pt; −6.3%; −10.1% |
| real 0.90 | −2.8 pt; −5.8%; −0.8% | −0.8 pt; −9.6%; −8.3% | −1.3 pt; −7.8%; +9.5% |
| today +20% at m240 | +8.0 pt; +19.9%; −14.6% | +1.0 pt; +20.0%; −15.2% | +1.8 pt; +20.0%; −7.4% |
| real +20% at m240 | +7.3 pt; +19.9%; −16.0% | −0.2 pt; +20.0%; −10.0% | +2.2 pt; +20.0%; −6.9% |

A real minimum under about 0.85 of the going rate binds nobody in these cities (0.60 and 0.825 are the same run), and 0.85 binds only in the deepest gluts: the unskilled band's glut is mild (tightness .78-.93), so its market wage stays at .88-.96 of the going rate. **People are noisy.** A change on the same seed sends the city down another path: the 0.85 floor's people at month 2,400 run from −13% to +27% of their twin's, seed by seed. So a difference in people counts only where every seed agrees on its sign: a floor raised 20% (every seed 10-24% smaller at month 480) does; the 0.85 floor (−18% to +13% at month 480) has no measurable effect on the city's size.

### Table 2: migration (`out/runs-migration.txt`; 4 seeds × 2,400 months; from month 240; people against `today` on the same seed)

| variant | out of work | from m1,200 | workers a post | people at m480 against `today` | at m2,400 | leaving a year | inflation |
|---|---|---|---|---|---|---|---|
| today | 12.2% | 10.1% | 1.12 | - | - | 5.8% | 1.97% |
| full senior care (`sen13`: the pull held at 1.30, his cities) | 30.4% | 30.0% | 1.42 | +9% | −3% | 10.6% | 2.13% |
| ...the pulls on the homes' draw only (`sen13h`, part 1) | 18.4% | 18.0% | 1.21 | −14% | −30% | 7.6% | 2.29% |
| ...arrivals braked past 6% (`sen13brk`, part 2) | 12.2% | 12.0% | 1.12 | −57% | −33% | 5.6% | 2.54% |
| ...braked, and the jobless past 6% leaving (`sen13aim`, parts 2-3) | 12.9% | 12.3% | 1.13 | −40% | −28% | 5.7% | 2.47% |
| ...and the floor a real minimum at 0.85 (`sen13r085aim`) | 12.0% | 11.8% | 1.12 | −20% | −17% | 5.7% | 2.29% |
| the slack at .05 (`slack05`) | 10.4% | 10.0% | 1.10 | −40% | −23% | 5.2% | 2.34% |
| arrivals braked past 6% (`brk`) | 9.7% | 9.2% | 1.08 | −47% | −24% | 4.7% | 2.36% |
| braked, and the jobless leaving (`aim`) | 10.4% | 9.5% | 1.10 | −62% | −40% | 4.8% | 2.43% |
| the floor a real minimum at 0.85 alone (`r085`) | 9.2% | 7.3% | 1.09 | 0% | −10% | 4.8% | 2.09% |
| ...braked, and the jobless leaving (`r085aim`) | 9.6% | 9.7% | 1.08 | −43% | −28% | 4.5% | 2.41% |
| ...parts 2-3 with vacancies drawing people (`sen13aimw`, part 4) | 12.4% | 11.8% | 1.12 | −38% | −22% | 5.8% | 2.45% |
| ...and the floor at 0.85 (`sen13r085aimw`) | 11.9% | 11.9% | 1.12 | −46% | −13% | 5.6% | 2.48% |
| vacancies drawing people alone (`wnt`) | 14.4% | 13.0% | 1.15 | 0% | −24% | 3.7% | 2.09% |
| braked, leaving, vacancies (`aimw`) | 9.9% | 9.8% | 1.08 | −57% | −30% | 4.8% | 2.35% |
| ...and the floor at 0.85 (`r085aimw`) | 9.5% | 9.0% | 1.08 | −56% | −3% | 4.4% | 2.44% |

Each seed's own ratios are in `out/runs-migration.txt` (`people.py`). Against `sen13`, its own twin, rather than `today`: parts 2-3 (`sen13aim`) leave every seed 32-54% smaller at month 480 and the four seeds at +51%, +11%, −31% and −46% by month 2,400; part 1 alone (`sen13h`) takes out of work from 30% to 17-19% in every seed.

People here are medians of four seeds' ratios to their twin, and a single seed's ratio wanders ±30% for no cause (table 1's note); the levers that cut arrivals (`slack05`, `brk`, `aim`) are the ones every seed agrees on: 34-80% fewer people at month 480.

**What it says.** Senior care's pull is the whole of the difference between the playtest's 12% and a city like his: held at 1.30 it puts 18 points on (`sen13`). Every lever on migration takes that excess away, and the ones that cut arrivals take people with it, most in the city's young years (a young city runs 12-18% out of work while its posts catch up with its founders, so a brake at 12% stops arrivals when it needs them most: 769 people at month 120 against 2,243). None takes the playtest's own city much under 10%: that is the pool the staffing gate needs to build on (3.4). The cheapest cut in the table is the real minimum at 0.85, which takes 3 points off by price with no measurable change in the city's size.

## 6. The batches

Each batch: a fresh session on its branch, slices to Sonnet 5.5 at xhigh, the gate by `ship.sh`. ★ moves the playtest trace and needs the ensemble.

| batch | size | what | files | saved · checks | trace |
|---|---|---|---|---|---|
| **WG1 ★ A real minimum** | medium | the going rate as the anchor; the floor lifts only the wages under it; the default by C1; the tie (prices, the average wage, nothing); the getters the page needs (the going rate, the average wage, workers at and near the floor, a floor job's cover); the floor's words that become false | `LabourMarket`, `PolicyPreview`, `PopulationManager`, `DataSave`, `Game` (load), `PolicyScreen` (words only) | the tie, its share · LabourCheck 1-2 restated and a section for the tie; PolicyPreviewCheck; the floor fixtures (3.8); SaveFileCheck | moves from month 1 under C1 (b); byte-identical under (a) |
| **WG2 ★ Senior care's excess, and the verdict** | medium | 3.4's parts 1-3 (the pulls on the homes' draw; arrivals slowing past the healthy rate; the jobless past it leaving), the rates by C2, the young city measured first; the out-of-work verdict and its tip | `Migration`, `PeopleScreen`, `UserInterface` | none · a PopulationCheck section per part (its migration sections), each fixture causing it (a city with full senior care and no jobs; a city past 12%; a city past 6% whose bands are not pinned); PopulationCheck's slack premise read again | moves (most in a city with senior care) |
| **WG3 ★ Schooling on pay after cost** | medium | `WageLadder` (pure, from `ladder.py`); `returnOn()` on it; the degree's ungated "after" | new `WageLadder`, `Education` | none · WageLadderCheck (each row against a hand sum); EducationCheck's return sections | moves from the first college |
| **WS1 The minimum-wage page** | medium | section 3.6's page; the ghost as the floor's own move; jobs with no posts greyed; the joined-market words | `PolicyScreen` (WAGES), a probe | none · PolicyPreviewCheck's page probe; the words probe | byte-identical; Jerus's PC check |
| **WS2 The ladder page** | medium | the bar chart and its verdicts | `PolicyScreen` (WAGES), a probe | none · a probe of the bars against `WageLadder` | byte-identical; Jerus's PC check |
| **GW0 The staffing gate and growth** (a study) | a session | why a city grows only on its jobless (`Sector.MIN_STAFFABLE_TO_ORDER` .80) and what a gate that counts the workers a building's wage would draw does to growth and to the out-of-work rate; the probe and the migration patch here are its starting kit | none | - | none (a study) |

**Order:** WG1, then WS1 (it needs WG1's getters); WG2 beside WG1 (no file in common); WG3 after WG1, then WS2. If the households study splits adults into stages, WG3 waits for that batch (section 7). GW0 any time; it needs no batch first.

## 7. Where this touches the other studies

- **Households** (`spec-households.md`, in progress): the city grows only on its jobless (the staffing gate, 3.4), so any change to who arrives or how many moves the city's size as much as its out-of-work rate; measure both. If adults are split into stages, (i) the working life a step is valued over, and who may go back to study, are the stage's (WG3 reads them; the households study owns the stages); (ii) `residentsPerJob()` reads the adult share, so the slack's arithmetic in WG2 follows whatever the stages make of it; (iii) migrants arriving with their own money is that study's; the slack they arrive into is this one's.
- **Credit and housing:** the student loan's rate is one input of pay after cost (`TaxPolicy.getStudentLoanRate()`, 0% by default); how the loan is lent and repaid is that study's. The rent in "what a minimum job covers" is the housing market's, read as it is.
- **Gold and money:** wages today follow prices down as well as up; the floor's "nothing" tie is the one wage that would not. Under a gold standard a deficit city adjusts by falling wages, so that tie makes gold cost output, which is the gold study's to weigh.
- **Wealth:** nothing shared.

## 8. Sources

Read 2026-11-02 (the OECD, Statistics Canada, BLS, Eurostat and OECD education series through their own APIs, the rest from the publisher's page). "Ships" means the figure may reach the game; "evidence" that it is cited only. Licences as in `licences.md`, where they have a row; Job Bank's row is new and WG1 adds it there.

- **[S1] OECD, minimum relative to average wages of full-time workers** (`DSD_EARNINGS@MIN2AVE`). The **mean** series ships already: the game's world file carries it for 33 nations, 2025 (`world-2025.json`, measure `kaitz`): United States 17%, Canada 43%, Japan 40%, Germany 44%, France 50%, United Kingdom 51%, New Zealand 55%, Mexico 61%, Costa Rica 69%; 28 of the 33 at 37-55% (derived from the file). The **median** series, 2024: Canada 49.9%, United States 25.0, United Kingdom 61.1, France 62.5, Germany 50.1, Japan 46.8, Korea 60.5, Spain 55.2, New Zealand 65.7; the 25 members other than the US and the Latin American four run 41.8-65.7%, the middle about 50. OECD terms: any purpose, commercial too. Ships.
- **[S2] Nova Scotia's minimum wage:** $17.00 an hour from 1 October 2026 ($16.75 from 1 April 2026, $16.50 from 1 October 2025, $15.70 before); the province's Labour Standards page and news releases (novascotia.ca/lae/employmentrights/minimumwage.asp; news.novascotia.ca, 2026-04-01 and 2025-10-01). A rate fixed in regulation: a fact. Ships.
- **[S3] The rungs and the province's wages.** PayTier's rungs are Job Bank's Nova Scotia occupation medians at 173 hours a month (material-handling labourer $20.00 an hour, $3,460 a month; PayTier's header has the rest): Employment and Social Development Canada, "Wages", Open Government Licence – Canada (open.canada.ca, dataset adad580f-76b0-4502-bd05-20c125de9116; licence at open.canada.ca/en/open-government-licence-canada: any lawful purpose, commercial too, credit asked). Ships (already does). Statistics Canada table 14-10-0064-01, Nova Scotia 2025: median hourly wage $27.37, average $32.39 (Canada $30.77, $36.40); Open Licence. Ships. So the province's own minimum over its median is about 62% ($17.00 ÷ $27.37, derived; a 2026 minimum over a 2025 median, so a little high).
- **[S4] Unemployment.** Cities, annual: Tokyo 2.4% (2025; Tokyo Metropolitan Government's Labour Force Survey release, 2026-03-26; Public Data License 1.0, compatible with CC BY 4.0); New York City 5.0% (2024; BLS LAUS, public domain); London 5.5% (2024; ONS series YCNI, Open Government Licence); Halifax 5.5%, Montreal 6.2%, Toronto 8.0% (2024; Statistics Canada 14-10-0461-01, Open Licence); Berlin 5.6%, Ile-de-France 8.0%, the Comunidad de Madrid 9.0% (2024) and 19.8% at its 2013 peak, Spain 26.1% then (Eurostat `lfst_r_lfu3rt`, CC BY 4.0). Spain's under-25s 55.5% in 2013 and 26.5% in 2024; the OECD's 11.1% (2024), its all-ages rate 4.9% (Eurostat `une_rt_a`; OECD `DF_IALFS_INDIC`, `DF_SUMTAB`). The US CBO's noncyclical rate, about 4.4% (FRED series NROU; public domain). Johannesburg not read (Statistics South Africa refuses automated reads and states no licence): evidence of the 20%-and-over cities rests on Madrid and Spain in 2013. Ships (all but the CBO's, which is evidence).
- **[S5] Earnings by education.** OECD Education at a Glance (`DF_LSO_EARN_REL_UPPER`), full-time workers 25-64, upper secondary = 100: Canada 2023, below upper secondary 89.8, bachelor's 147.9, master's or doctoral 172.2; the OECD average 2024, 82.9, 140.8, 182.0. Statistics Canada 98-10-0410-01 (2021 Census), Nova Scotia's full-year full-time median employment income: no certificate $40,800 (0.90 of high school), high school $45,200, college $52,800 (1.17), bachelor's $71,000 (1.57), master's $85,000 (1.88), medicine and the like $99,000 (2.19). Against the game's ladder (PayTier, occupation medians): a diploma 1.30× no diploma, college 1.22× a diploma, a degree's ungated job 1.82×, medicine 3.47× (derived), every step steeper than the province's by education. Ships (OECD, Open Licence); evidence here.
- **[S6] Spillovers and jobs** (evidence only, journal articles): Cengiz, Dube, Lindner and Zipperer (2019), "The Effect of Minimum Wages on Low-Wage Jobs", *Quarterly Journal of Economics* 134(3) (NBER w25434): over 138 US state rises, low-wage jobs unchanged five years on; spillovers reach $3 above the new minimum (about the 23rd percentile) and are about 40% of the wage gain. Autor, Manning and Smith (2016), *AEJ: Applied Economics* 8(1): spillovers reach about the 15th percentile, pooled, and may be partly reporting error. In this game's bands the next rung up sits well above the floor's reach, which is why the ripple never fired (3.3).
