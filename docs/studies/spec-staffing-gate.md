# The staffing gate and growth - the study, 2026-11-09

`QUEUE.md`'s GW0. Its brief is [the wages study](spec-wages.md), 3.4 and call C2. The game's own city cannot go
much under 10% out of work by migration alone, because a business builds only when 80% of a building's posts can
be filled from the workers spare today. In the wages study's words, "anything under about 10% needs the gate
itself to change, so that a business may order a building its wage would draw the workers for". This is a study:
nothing in `src/` changes. The prototypes are in [staffing-gate/kit.md](staffing-gate/kit.md), kept as Markdown so
the merge changes only Markdown. Every table of the runs is in [staffing-gate/out.md](staffing-gate/out.md).

## 1. Summary for Jerus

1. **Why the city sits near 10%.** A business may order a building only when the workers spare today could fill
   80% of its posts (`Sector.MIN_STAFFABLE_TO_ORDER`). So the jobless are the only thing a city grows on.
   Migration keeps an eighth more people than the jobs need, and the game's own city runs 11.5% out of work from
   month 240 (the median of 16 seeds).
2. **The gate refuses the young city's works, month after month.** Over seeds 0-3's first 40 years, about 15
   orders a month for a single building failed on the spare workers alone: 28,585 in all, over 65 buildings
   (out.md, table 3). The most refused were the Machine Works, the Shared Services Centre, the Pulp and Paper
   Mills, the Oxygen Steel Shop, the Blast Furnace, the Copper Smelter and the Contact Centre. Each was refused
   from month 2 to month 480. Seed 0's village stood at 505 posts from month 96 to month 120, with 21-25% of its
   workers jobless, waiting until its jobless alone made up 80% of a works.
3. **A gate that counts the draw.** In life, a works is built where people will come for it. The prototype keeps
   the 80% but counts two groups toward it: the workers spare today, and the workers migration would bring for
   the building's own posts within a year. Migration's own sums give that number from last month's figures:
   the jobs' share of the target, how fast a gap closes, the brake, and the mix of bands. Nothing new is saved.
   A 12-month draw passes 80% of the orders refused today. It never passes the Shared Services Centre (0 of
   1,916 asks). The spare could never fill more than a third of it, and the draw never brings its college and
   university staff in time.
4. **What it does** (16 seeds, 2,400 months each, medians against today on the same seed; section 4):
   - The young city's out of work halves, 12.0% to 6.3% (months 24-240). The city is larger: by 58% at
     month 240, 20% at 480, 112% at 1,200 (larger in 14 of 16 seeds) and 12% at 2,400.
   - On its own it does not lower the grown city's rate (11.7% against 11.5%). Migration still aims an eighth
     over the jobs.
   - With WG2's aim (arrivals slow past 6% out of work and stop at 12%; the jobless past 6% leave), the grown
     city's rate comes down too: 8.3% from month 240, 6.1% from month 1,200, 5.2% in the young city. The city is
     54%, 148% and 22% larger at months 240, 1,200 and 2,400. No seed's people fell by a third from their high
     (today: 3 seeds of 16). A seed spends a median of 14 months over 25% out of work, against 111 today.
   - WG2's aim on today's gate costs the young city instead. It is 42% smaller at month 240 (smaller in 14 of 16
     seeds) and 12.5% out of work in its first 20 years. That is the risk the wages study named.
   - The costs. In the young city, 5.5-7.3% of posts are unfilled against 3.9%; in the grown city, 3.1-3.2%
     against 2.3%. Works stand in villages sooner: a Fabrication Works of 276 posts at month 31 and 800 people,
     where today's first stands at month 402 and 9,200. Output a head, sectors losing money and the homeless
     are within the seeds' noise. Inflation is 2.0% a year against 2.2%.
5. **What did not do as well.**
   - **A lower share without the draw** (60% or 40%) builds what the city cannot keep. At 40%, 13.1% are out of
     work and three seeds of 16 shrink by a third.
   - **A 6-month draw** is too short to help past the first decades (12.9%).
   - **A 24-month draw** grows fastest early, but it counts on workers who do not come. Seed 5 stood 600 months
     at 14,000 people with 25% out of work.
   - **No share test at all**, keeping only the band test, builds the largest city and has the fewest bad months
     (with WG2's aim: 8.4% out of work, the city 106%, 247% and 29% larger). But it builds what nobody here is
     schooled for. The Shared Services Centre stood at month 54 in a town of 1,800 in all eight seeds checked.
     In seed 0 at month 60, 30% of the posts were empty while 21.5% of the workers were jobless, and a tenth of
     the young city's posts stood empty (10.3% against 5.5% with the draw).
6. **Four calls** (section 5) and one batch, GW1 (section 6). I recommend the 12-month draw with WG2's aim as
   you took it, and GW1 before WG2.

## 2. What the gate does today

### 2.1 The rule (`Sector.staffing()`, `Sector.Staffing`)

`staffing(type, count)` asks each wage band what share of the posts it wants its spare workers could fill. A
band's spare is `supplyByBand(posts)` less the posts, after the cascade (a worker may take any post at or below
their band). A working builder's withheld crews and the sector's own building sites count as taken. An idle
builder's laid-off crews have counted as spare since IC1. The share is Σ min(wanted, spare) over the posts.
`passes()` asks for two things:

- the share is at least `MIN_STAFFABLE_TO_ORDER` (.80);
- no band is one nobody can fill: no spare worker, nobody above who could step down, and no migrant who comes for
  it (`nobodyCanFill`, after Jerus's rule that the 20% "can't be jobs nobody can fill").

Who reads the gate:

- every planner asks it before ordering (each sector's `plan()` and `SpreadPlanner`);
- `staffableCount` sizes an order by it, and cuts the count silently;
- BuildAdvice (the mines), BuildCard and BusinessInvestment read it;
- RealEstate is exempt (`ordersOnlyWhatItCanStaff`).

Since LD1, a planner's hold hands its short bands to the wage (`WantedPosts`). Next month those posts are priced
as if they were offered, so the band's migrants have a reason to come. Today's loop is: refuse, bid, wait, ask
again.

The dial's own comment gives its reason: 80% is "roughly what a real firm needs to see before it signs a lease",
and "it puts the first one in a city of three or four thousand rather than a village". The two tests answer
different questions. The band test asks whether anybody here could do the work. The share asks whether enough of
them are here already. The share is what holds the young city back.

### 2.2 Who it refuses (out.md, table 3)

These are the young city's works:

- the steel chain: the Blast Furnace (1,200 posts), the Oxygen Steel Shop (1,800), the Large Mini-Mill, the
  Coke Ovens;
- the Copper Smelter (700);
- the Pulp and Paper Mills: a fifth of their posts are college and university, so with those bands short the
  share cannot reach 80%;
- the Machine Works (173 posts, 22% college and university);
- the Contact Centre (300).

Each is asked for again every month a sector still wants it, and refused each time, from the city's second month
to the end of the instrument's 480 months. The spare would fill anywhere from nothing to just under 80% of them.
Most refusals are never handed to the wage. In the same months the planners hand `WantedPosts` 0.3-0.6 holds a
month, and only 0.07-0.21 of those bid. The instrument counts 13-16 refused single-building asks a month.

### 2.3 The young city (out.md, table 5)

Seed 0 as it is: 158 people at month 12, with 37% out of work. It holds 505 posts from month 96 to month 120
while 21-25% of its workers are jobless, and reaches 2,627 people at month 240. With the draw, the same seed has
3,345 people at month 96 with under 1% out of work, and 7,675 at month 240.

## 3. The design: count the draw (prototype `gate.patch`)

### 3.1 The rule

Beside each band's spare workers, the gate counts the workers migration would bring for the order's own posts
within the window (12 months) after it opens. `Migration.drawFor()` works them out step by step:

- **The draw.** The posts raise migration's target by the jobs' share of it: `JOB_WEIGHT` .75 × the posts ×
  last month's residents a job × the pulls on the jobs' draw.
- **How much comes within the window.** A gap closes at `ARRIVAL_RATE` .15 × last month's crowding a month. In
  12 months that closes 1 − (1 − that)^12 of it, about 86% at full room.
- **The brake.** Under WG2's brake, the draw shrinks as arrivals do, to nothing at 12% out of work.
- **People to workers.** The city's workers per resident turns the people into workers.
- **By band.** The workers are split as `composeArrivals()` splits a month's arrivals, with the order's posts
  added to each band's opportunity and the licences to the university band.

Each band's drawn workers fill its own posts first. What is left steps down to the bands below. The band test is
untouched: a band nobody here can fill, and no migrant comes for, still refuses. Every figure the draw reads is
migration's last month (crowding, the pulls, residents a job), and those are saved (`LAST_SCALARS`). So a loaded
city's gate is the one it would have had, and there is no new saved field.

### 3.2 The share stays at 80%

Lowering the share without asking where the workers come from builds what the city cannot keep. At 60% and 40%
the young city does grow (+95% and +68% at month 240). But the rate from month 240 rises (12.0% and 13.1%), a
seed spends 61-94 months over 25% out of work, and at 40% three seeds of 16 shrink by a third. With no share
test at all (the band test kept), the posts stand empty instead (2.3 above; out.md, tables 1 and 4).

### 3.3 The window

| window | young city | from month 240 | people, month 1,200 | months over 25% | shrank by a third |
|---|---|---|---|---|---|
| 6 months | 6.6% | 12.9% | +26% | 104 | none |
| 12 months | 6.3% | 11.7% | +112% | 42 | none |
| 24 months | 6.8% | 6.8% | +169% | 16 | 2 of 16 |

The 24-month draw keeps the grown city tightest, but it promises more than comes. Seed 5 grew to 27,500 people
with 14,500 posts by month 456, lost more than half its posts by month 600, and then stood 600 months at 14,000 people,
25% out of work. Seed 7 fell at month 231. A year is the longest window in which no seed shrank.

### 3.4 With migration's aim

The draw makes WG2's aim affordable. On today's gate, slowing arrivals past 6% out of work holds back the very
months the young city grows in: 42% smaller at month 240, 12.5% out of work young. With the draw, the works go
up first and the people follow: 5.2% young, 8.3% from month 240, and larger than today at every month measured.
A lower slack (5% over the jobs against 12.5%) with the draw does about as well: 4.4% young, 4.8% from month
1,200, but only 9% larger at month 2,400.

## 4. Measured (out.md, tables 1 and 2)

16 seeds of the AUTO2 founding, 2,400 months each, medians. People are each seed's ratio to today on the same
seed, with the number of seeds above their twin. "Months over 25%" is the median seed's months over 25% out of
work, with the worst seed in brackets. "Posts unfilled" is from month 240.

| variant | out of work from m240 | from m1,200 | young, m24-240 | people m240 | m1,200 | m2,400 | months over 25% | shrank by a third | posts unfilled |
|---|---|---|---|---|---|---|---|---|---|
| today | 11.5% | 8.5% | 12.0% | 3.5k | 65.0k | 557.7k | 111 (232) | 3 of 16 | 2.3% |
| g60 | 12.0% | 8.5% | 8.4% | +95% (14 up) | +40% (10 up) | +8% (9 up) | 61 (1032) | none | 3.6% |
| g40 | 13.1% | 9.7% | 7.9% | +68% (14 up) | +38% (10 up) | -4% (6 up) | 94 (284) | 3 of 16 | 3.7% |
| g0 | 10.3% | 7.0% | 11.4% | +167% (16 up) | +167% (16 up) | +27% (12 up) | 13 (133) | none | 3.3% |
| draw6 | 12.9% | 10.1% | 6.6% | +37% (12 up) | +26% (10 up) | +1% (8 up) | 104 (214) | none | 3.3% |
| draw12 | 11.7% | 8.6% | 6.3% | +58% (14 up) | +112% (14 up) | +12% (11 up) | 42 (146) | none | 3.1% |
| draw24 | 6.8% | 3.9% | 6.8% | +173% (16 up) | +169% (14 up) | -4% (8 up) | 16 (469) | 2 of 16 | 3.7% |
| aim | 8.2% | 6.5% | 12.5% | -42% (2 up) | +9% (9 up) | +9% (11 up) | 45 (71) | 1 of 16 | 2.3% |
| slack05 | 8.5% | 6.2% | 14.3% | -8% (4 up) | -3% (7 up) | -14% (4 up) | 70 (144) | 1 of 16 | 2.5% |
| d12aim | 8.3% | 6.1% | 5.2% | +54% (12 up) | +148% (14 up) | +22% (12 up) | 14 (48) | none | 3.2% |
| d12slack05 | 8.2% | 4.8% | 4.4% | +62% (16 up) | +131% (15 up) | +9% (9 up) | 14 (77) | none | 3.5% |
| g0aim | 8.4% | 6.9% | 8.8% | +106% (15 up) | +247% (16 up) | +29% (12 up) | 6 (31) | none | 3.5% |

The variants: `g60`, `g40` and `g0` lower the share to 60%, 40% and nothing, without the draw; `drawN` counts the
draw within N months; `aim` is WG2's aim (arrivals slow past 6% and stop at 12%, the jobless past 6% leave);
`slack05` has migration aim 5% over the jobs; `d12` is the 12-month draw combined with either; `g0aim` is no
share test with the aim.

- **The young city's posts unfilled** (months 24-240, median, worst seed in brackets): today 3.9% (6.3),
  draw12 5.5% (9.7), d12aim 7.3% (9.2), g0 10.3% (11.8), g0aim 10.6% (20.6). By months 240-720 all are 3.7-4.3%.
- **Output a head at month 2,400**, against today on the same seed: draw12 −6% (7 of 16 up), d12aim 0% (7 up),
  g0aim +4% (9 up), g0 +15% (12 up). The seeds range from about −29% to +40% in every row, which is noise at
  this count.
- **Sectors losing money** stay at 2.4-2.8 in every row, and **the homeless** at 0.08-0.10% of the city.
- **Inflation** from month 240: today 2.23%, draw12 2.00%, d12aim 1.98%, g0aim 2.03%, aim alone 2.47%.

Where the first of each large building stands (out.md, table 4; seeds 0-7 to month 1,200, medians):

| building (posts) | today | the 12-month draw | no share test |
|---|---|---|---|
| Machine Works (173) | m39, 600 people (7 of 8) | m74, 2,000 | m88, 3,500 |
| Fabrication Works (276) | m402, 9,200 | m31, 800 | m32, 800 |
| Vehicle Works (170) | m423, 10,200 | m56, 1,400 | m56, 1,900 |
| Contact Centre (300) | m135, 1,500 | m56, 1,500 | m312, 8,700 |
| Shared Services Centre (300) | not by m1,200 | not by m1,200 | m54, 1,800 (8 of 8) |
| Pulp Mill (200) | not by m1,200 | m624, 18,700 (6 of 8) | m527, 30,800 (5 of 8) |
| Blast Furnace (1,200) | not by m1,200 | m1,171, 99,100 (1 of 8) | m645, 28,100 (1 of 8) |

Today's gate already puts a Machine Works in a village of 600 when the village's jobless are there. The draw does
the same for a Fabrication Works or a Vehicle Works, whose people come within the year. Company towns grew this
way: Gary, Indiana, was laid out by U.S. Steel round its works in 1906, and Kitimat, British Columbia, was built
for Alcan's smelter in the 1950s. Without the share test, a 300-post office of college and university staff opens
in a town of 1,800 that has neither. In seed 0 at month 54, the town had 35 university posts and 5 university
workers, and 217 college posts and 28 college workers.

## 5. The calls

**C1. The gate.** Options:
- (a) Count the draw: 80% of the posts filled by the spare workers plus those migration would bring within the
  window, the band test as it is. The young city runs 6.3% out of work and is larger at every month measured.
- (b) No share test, only the band test. The largest city and the fewest bad months, but a tenth of the young
  city's posts stand empty and the Shared Services Centre opens in a town of 1,800.
- (c) A lower share (60% or 40%). Worse than (a) on the rate and on the falls.
- (d) As it is.

**Recommended: (a).** It keeps Jerus's rule ("whether they can staff a building before building it") true with
a fuller count. It refuses what the people will not come for: a full city, a city at the brake, posts in a band
that no migrant here is pulled into. (b) assumes they always come. They do in AUTO2, which builds its homes ahead,
but a player's city need not (not measured; section 8).

**C2. The window.** Options: (a) 12 months; (b) 6 months; (c) 24 months. **Recommended: (a).** It is the longest
window in which no seed shrank. Six months is too short to help past the young city. With 24 months, two seeds of
16 shrank by a third, one of them for 600 months.

**C3. Migration's aim with it.** Options:
- (a) WG2 as you took it (arrivals slow past 6% and stop at 12%; the jobless past 6% leave): 5.2% young, 8.3%
  from month 240, 6.1% from month 1,200.
- (b) A 5% slack instead: 4.4%, 8.2% and 4.8%, but only 9% larger at month 2,400 (9 of 16 seeds) against 22%.
- (c) Neither: the draw alone, 11.7%.

**Recommended: (a).** You decided it on 2026-11-02, and with the draw it costs the young city nothing. (b) is a
dial for later if 6% from month 1,200 reads too high.

**C4. The order.** Options:
- (a) GW1 before WG2. WG2's brief gains one line: its brake and its pull rule apply to `drawFor()` too.
- (b) WG2 first, trying the later brake the wages study planned (from month 240, or from 8% to 16%), with GW1
  after.
- (c) Both in one batch.

**Recommended: (a).** WG2's aim alone costs the young city 42% at month 240 (smaller in 14 of 16 seeds); with
the draw first, the same aim costs nothing. If WG2 is already in hand, take (b) and measure its young city
against this study's `aim` row.

## 6. The batch

| batch | size | what | files | saves · checks | trace |
|---|---|---|---|---|---|
| **GW1 The gate counts the draw** | medium | `Sector.staffing()` counts the workers `Migration.drawFor()` says the order's posts would bring within `GATE_DRAW_MONTHS` (12) beside the spare workers, each band's drawn workers stepping down as the cascade does; the band test unchanged. The hold's words name both counts (now, and with a year's arrivals), on the build card and the sector screen. | `Sector`, `Migration`, `BuildCard`, `ui/SectorScreen` | no new field (the draw reads migration's saved last-month scalars) · a `StaffingDrawCheck`, each fixture causing its case: a village with a works whose draw passes and whose spare does not; a city with no room (crowding nothing), where the draw refuses what the spare refuses; a city at the brake; the Shared Services Centre in a town without its bands, still refused; a loaded city's gate equal to its twin's. The 19 harnesses that name the gate read again for a premise of "only the spare" (InvestCheck and WantedPostsCheck most), restated where the draw is now the condition, never bent | moves (most in the young city; ensemble) |

The prototype's `drawFor()` reads the migration patch's brake and `PULLS_ON_HOMES`. Main has neither yet. On
main, GW1's draw reads the pulls as they are (senior × affordability × crime) and no brake, which is exactly the
`draw12` row. WG2 then adds its brake to the draw in the same lines it adds to arrivals.

## 7. Where this touches the other batches

- **WG2** (queued): the draw and WG2's aim are measured together (`d12aim`), and the order between them is C4.
  The files they share are `Migration` alone: GW1 adds a method, and WG2 changes the target, the arrivals and the leaving.
- **WG1** (queued): the real minimum moves the bands' premiums, and the draw's band mix reads them through
  `reach()`. The files differ (`LabourMarket` against `Sector` and `Migration`), and so do the harnesses.
- **LD1 and LD2** (`WantedPosts`): every run here has the bid on, as main does. The two compound: the bid raises a
  short band's pay, and the draw reads the premium. With the draw, fewer orders are refused, so fewer bid. Whether
  the bid still earns its place with the draw on is not measured.
- **IC1:** unchanged. An idle builder's crews are spare before the draw is counted.
- **The households study:** `residentsPerJob()` reads the adult share, so the draw follows whatever the stages
  make of it, as the target does.
- **The ensemble:** GW1 moves the trace, most in the young city. R2 reads the out-of-work rate.

## 8. Not measured

- **Jerus's own cities, a 1900 founding and Random seeds.** No save was read and no slot was written. Only the
  AUTO2 founding was played, seeds 0-15. A 1900 city splits its arrivals by `yearMix`, which the draw reads, but
  it was not played.
- **A player's city that does not build homes ahead of its people.** This is the case C1's recommendation leans
  on. GW1's no-room fixture is the first check of it.
- **The ensemble's 80 runs** (GW1's).
- **The draw with LD1's bid off.**
- **Counting the homes' quarter of the draw too** (`gate.drawAll`): prototyped, not run.
- **The run time.** The probe's cities took 37-212 s each on a shared PC, with no difference seen between
  variants. GW1's timing harnesses will say.
- **The hold's new words on the screens:** not drafted.
