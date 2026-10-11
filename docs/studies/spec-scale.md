# Can the game run a city of 5 to 10 billion people? A scale study, 2026-10-06

Read-only study of 0.7.52 (`/home/claude/cbs`), built and measured in `/home/claude/scratch-scale/`. The probe is `probe/ham/citybuildersim/ScaleProbe.java`, with `scale_save.py`, `run.sh`, the probe-only patches in `patch/`, and CSVs and profiles in `out/`. Every figure is measured in those runs or derived from the saves, with its source beside it.

## 1. The verdict for Jerus

**Yes, after named fixes.** Today the game cannot pass 2.1 billion people. Once the fixes are in, nothing I measured stops 5B or 10B.

- **The month: yes, after one small fix.** The model is aggregate: people, households and buildings are counts in cells and stacks, and the `BuildingInstance` list is never filled (`addInstance()` has no caller).
  - The one cost that grows with size is three loops that count an order one building at a time. In the real game they made months at 1.1B people take up to 14.8 s (mean 4.1 s).
  - With the loops bisected, the median month is 23 to 43 ms from 113 thousand to 2.04 billion people (the largest under the int limit), with identical results in every month over 600 months.
- **Overflow: no, until counts are `long`.** Population past 2^31 wraps silently (a 5.09B city read back as 938,901,759) and the workforce saturates at 2,147,483,647.
  - At Jerus's ratios the workforce overflows at 4.25B people, jobs at 5.23B, households at 5.57B and homes at 8.75B.
  - The fix covers about 230 declarations, about 340 call sites and 11 integer series, all found by the compiler: 1.5 to 2 days, with no `SAVE_FORMAT` bump.
- **Money: yes, with a tolerance change.** Money is a `double` in thousands of dollars. The audit is already relative, and its worst residual at every size was 1.1e-12 of what moved.
  - What breaks is the harnesses' absolute "to the cent" checks and two display formats. A 10B city's lifetime counters reach 2 to 5 × 10^14 units, where a double's step is 0.03 to 0.06 of a unit.
  - The fix is a tolerance relative to the books; long cents are not big enough.
- **Save and memory: yes, as they are.** Heap (about 19 MB), save (0.4 to 3.2 MB) and load time (under 0.9 s) are flat from ×1 to ×10,000. History grows with months, not people: 3.2 KB a month.
- **The map: yes, storing districts rather than tiles.** At Jerus's density, 10B people need 1.76M km², or 1.95B plots of 30 m.
  - Store 29,830 districts in an 11 MB binary sidecar.
  - Derive tiles by a stable seeded deal (22.5 µs a district) and paint each in 12 µs. A screenful of tiles takes about 3 ms.
- **★ One mechanic is not scale-free, and Jerus has to decide it.** The land price premium is linear in absolute size (`LandMarket.java:328-329`); at 10B land costs about 19,500 times today's price per sq ft.
  - A ×10 copy of his city (5.1M people) saw rents ×3.9, prices ×2.3 and 18% of its people leave in 30 months.
  - The same copy with the premium read at the original size tracked his real city.

## 2. The overflow inventory

**Method.** `ovf.py` (output in `ovf.txt` and `ovf-pop.txt`) matched `int`/`short`/`float`/`Integer` declarations, methods and `(int)` casts by name: people, workforce, jobs, posts, homes, households, capacity, units, buildings. Per-building template constants are excluded. There is no `float` or `short` anywhere in the model.

| group | sites | representative `file:line` |
|---|---|---|
| model | 202 in 33 files | `Game.java:31` `int population`; `:32` `int[] jobs`; `:9320` `(int) Math.round(cohorts.total())` **wraps**; `PopulationManager.java:14,21,24`; `:120` `(int) Math.max(0, adults)` **saturates**; `EconomyManager.java:67-69`; `HouseholdAccounts.java:1168-1170`; int × int products at `BuildingManager.java:4278` (`quantity * per`) and `BuildingsStacks.java:379`; `BuildingManager.java:4214` job sums; `RealEstate.java:912`; `FamilyModel.java:921`; `CityNeeds.java:374`; `BusinessInvestment.java:173` `recordMonth(int)`; `TimeSkipReport.java:48-50` |
| save | 8 fields, 11 `List<Integer>` | `DataSave.java:168,261,1717`; `SaveHeader.java:25`; `HistorySave.java:66-83` (jobs, workforce, outOfWork, population, births, deaths, arrivals, departures), `:276` homes, `:385`, `:566-569` casts |
| UI | 18 in 6 files | `ui/PeopleScreen.java:543,824-825`; `ui/SummaryScreen.java:779,1020-1025`; `ui/UserInterface.java:1913`; `ui/SectorScreen.java:308`. These show the wrapped figures. |
| harnesses | 105 in 30 files | `LongPlaytest` (21), `PopulationCheck` (14), `BuildAdviceCheck` (10). They need only to compile again; no fixture passes 2^31. |

**What still fits an `int` at 10B:** building counts (the largest type 119M, all buildings 279M), land purchases, deposits (3.0M in the city, 882M in the world) and order sizes. A plot index does not fit at the sparser density (3.28B plots, section 6).

**Measured past the limit.** Jerus ×10,000 (5.09B) with the ints clamped in the save (`out/fix-jerus-x10000-months.csv`): population read back as 938,901,759 (the long-to-int cast wrapped), the workforce stuck at 2,147,483,647 and jobs at 2,096,946,703. Unclamped, the save cannot be parsed, because `SaveHeader.population` is an int. The menu marks the slot unreadable (`GameFiles.java:281-298`), and a direct `loadGameSave` left a blank month-1 city.

**Work.** Move these to `long`:
- the getters and their fields: `getPopulation` (137 call sites), `getWorkforce` (39), `getTotalJobs` (61), `getTotalHomes` (16), `getHouseholdCapacity` (10), `getJobs` (18), `getJobsFilled` (18), `getUnemployed` (19), `getTotalHouseCapacity` (13), `homesBySize` (9);
- `int[] jobs` → `long[]`; `(long) quantity * per`; `(int) Math.round(x)` → `Math.round(x)`;
- the history lists → `List<Long>`.

About half the call sites are harnesses; 1.5 to 2 days with the suite. **Save format:** JSON numbers have no width, so an old save loads into `long` fields unchanged, and the standing rule needs no bump ★.

## 3. Money at scale

**What money is held in.** A `double` in **thousands of dollars** (`Formats.cash(double thousands)`, `Formats.java:28-32`). A harness "cent" ("kept to the cent of a thousand", `HistoryCheck.java:189`) is 0.01 of a unit, or $10; a real cent is 1e-5 of a unit. A double resolves 0.01 of a unit below 2^46 = 7.04e13 units, and a real cent below 2^36 = 6.87e10.

**The audit's tolerance.** `MoneyAudit.Result.relative()` (`MoneyAudit.java:198`) is |residual| / max(1, |in| + |out|). `MoneyCheck` requires it below 1e-4 (`:95`); `LongPlaytest` fails a month only when |residual| > 0.01 **and** the relative figure is above 1e-7 (`:1078`). Measured, land-neutral, skipping the first month after a scaled load:

| run | people | worst relative | worst absolute residual (units) |
|---|---|---|---|
| city2400 ×1 | 112K | 2.1e-14 | 0.0000 |
| Jerus ×100 | 51M | 3.9e-13 | 0.0116 |
| Jerus ×1000, 600 months | 510M | 1.1e-12 | 10.35 |
| city2400 ×10,000 | 1.12B | 9.6e-15 | 0.080 |
| Jerus ×4000 | 2.04B | 3.5e-13 | 705 |

The audit is safe at any size, five orders of magnitude inside its bar. Every absolute check is not.

**The largest stocks and flows**, per head in Jerus's city (month 1,851, 509,455 people, price index 18.2):

| figure | per head | 5B | 10B |
|---|---|---|---|
| treasury | 2,595 | 1.3e13 | 2.6e13 |
| household savings | 6,807 | 3.4e13 | 6.8e13 |
| money moved in a month | 200 | 1.0e12 | 2.0e12 |
| lifetime exports (`foreignAccounts[5]`, the largest) | 22,910 | 1.15e14 | 2.29e14 |

- After his city's own 600 more months (price index 23.3), lifetime exports are 45,360 a head: 2.3e14 at 5B and 4.5e14 at 10B.
- A ×1000 copy run 600 months ended with a treasury of 1.65e14 and a sector credit line of 1.35e15.
- So at 5B the lifetime counters pass 7.04e13, with the treasury and savings following as prices rise. Real cents are lost much earlier, at $68.7B.

**What breaks:**
- **The audit:** nothing.
- **The treasury journal and harness checks:** `TreasuryCheck.near` has an absolute `TOLERANCE = 1e-6` (`:99`), and there are about 85 absolute `Math.abs(..) < k` comparisons in `*Check.java` (a regex count, not all money; for example `HistoryCheck.java:241` `.025`, `ForeignCheck.java:400` `1e-6`). All of them fail once the books pass about 1e13.
- **The year book and history:** `round2` (`HistorySave.java:790`) holds until 9.2e16 units.
- **The display:** `Formats.amount` stops at "T" (`:55`), so a 10B treasury reads "$26000.0T". `Formats.cash` rounds dollars to a long (`:31`), which saturates at 9.2e15 units; the scaled ×4000 run reached 6.66e15.

**★ The smallest sound fix.** Keep `double`, and route every absolute money comparison through one helper, `MoneyAudit.tolerance(scale) = max(0.01, 1e-12 × scale)`, with scale being what the books moved or hold. `TreasuryCheck.nearOf` (`:115`, 1e-9 × size) already works this way. Give `Formats` a quadrillion step, and have `cash()` fall back to `amount()` above $10^12.

Long cents are rejected for two reasons: they hold 9.2e13 units, less than a 10B city's lifetime exports, and every money operation in the model is fractional.

## 4. Month time against size (measured)

**Scaling.** A copy of the save is multiplied by k: population, workforce, the pyramid, families, the households in each cell, buildings and sites, every sector's and market's stocks and flows, loans, treasury, bank cash, foreign accounts and the central bank. Per-household figures are kept; per-household shares and bonds are divided by k so the registers still add up.

**Timing.** 6 warm-up months, then 24 timed, with `-Xmx3g` on 2 cores. The yearly autosave (250 to 350 ms) falls in the mean and the max. "Land-neutral" reads the land premium at the unscaled size (probe patch `patch/LandMarket.java`); this isolates timing from the land mechanic and makes copies track the original (Jerus ×1000 went 509M → 515M while ×1 went 509K → 521K).

Median / mean / max ms per month:

| city | people | original loops | bisected loops |
|---|---|---|---|
| city600, as is | 5,989 | 33 / 41 / 197 | – |
| city2400, as is | 112,795 | 15 / 35 / 299 | – |
| Jerus, as is | 509,455 | 40 / 61 / 384 | – |
| city2400 ×1, land-neutral | 113K | 19 / 38 / 250 | 23 / 40 / 312 |
| city2400 ×10 | 1.13M | 22 / 42 / 274 | 30 / 46 / 307 |
| city2400 ×100 | 11.3M | 20 / 39 / 283 | 25 / 42 / 327 |
| city2400 ×1000 | 113M | 50 / 65 / 330 | 26 / 56 / 372 |
| city2400 ×10,000 | 1.13B | **576 / 596 / 1,296** | 29 / 47 / 294 |
| Jerus ×100, land-neutral | 51M | 46 / 65 / 325 | 43 / 61 / 309 |
| Jerus ×1000, 600 months | 509M | 21 / 48 / 671 | 17 / 36 / 354 |
| Jerus ×4000 | 2.04B | 39 / **209 / 893** | 32 / 53 / 307 |
| city2400 ×10,000, **real game** (land as is) | 1.13B | mean **4,120**, max **14,840** | mean 53, max 331 |

**The hot spots**, from a 2 ms stack sampler on the month thread (`out/*-profile.txt`):
- **Real game, ×10,000.** `Game.consider` takes 98.5% inclusive. **`Game.java:3846`** `for (int n = decision.quantity; n >= 1; n--)` calls `financeProject` for every n, which reaches `BusinessDebtManager.getLoanPrincipal` (66.9% self, a scan of every loan) and `BondMarket.plan` (`Bidders.<init>` 62.9%).
- **Land-neutral, ×10,000.** `BusinessInvestment.orderSize` takes 93.8%. **`BusinessInvestment.java:245`** `for (int n = 1; n <= needed; n++)` calls `leadTime` and then `BuildingManager.waitWith` (74.3% self, a scan of the stacks for each n). It is called from `LuxuryRetail.plan` (43.7%), `Retail.plan` (30.7%) and `Restaurants.plan` (18.8%).
- **At ×1000** the third loop also shows: **`Mortgage.java:272`** `for (int n = asked; n >= 1; n--)`, through `considerOnMortgage`, about 20%.
- **After bisection** the yearly autosave leads (`HistorySave.saveHistory` 29%, once in 12 months). Nothing else passes 9%, and nothing grows with size.

**What does not grow with people** (checked in the saves at ×1 and ×1000): bonds (62 to 105), business debts (40 to 107), the build and demolition logs (capped at 40) and the order books.

**What to change: bisect the three loops** (patched for the probe in `patch/`).
- The bisected build matched the original in population, cash and buildings in every month: over 600 months at ×1 and ×1000, and in every city2400 run up to ×1000.
- At ×10,000 the population differed by 0.12% after 30 months, so the test is not strictly monotone at the edge ★.
- Keep the refusal text: compute the rate at the full order, and the facts for one building, separately.

## 5. Memory and the save

| measure | value | grows with |
|---|---|---|
| heap after GC, loaded | 15.5 MB (city2400), 16.5 MB (Jerus), at every k | months |
| heap after 30 months | 17.9 to 19.4 MB from ×1 to ×10,000 | months |
| heap after 600 more months | 22.0 MB (×1), 22.4 MB (×1000): about 9 KB a month | months |
| save | city2400 0.38 → 0.69 MB (×1 → ×10,000); Jerus 3.2 MB (bond market 1.26, fund 0.30, `householdBondsByCell` 0.20) | bonds, not people |
| history | 5.796 → 5.816 MB from ×1 to ×10,000 (digits only); Jerus 5.58 → 7.50 MB over months 1,851 → 2,451 (+3.2 KB a month), 7.81 MB at ×1000 | months |
| load; save + history | 0.55 to 0.86 s; 0.14 to 0.22 s, at every scale | months |

The `.bak` copies double the disk. Derived for 1,000 years: about 37 MB of history, about 110 MB of heap, and about 1 s per autosave (7.5 MB is written in 0.2 s). None of it depends on population.

## 6. The painted map at scale

**Densities, from land owned** (`landOwned` sq ft × 0.0929):

| city | people per km² | notes |
|---|---|---|
| Jerus | 5,684 | 89.6 km²; 158.6 buildings per km² (14,214 buildings of 55 kinds) |
| city2400 | 3,389 | |
| city600 | 1,975 | |
| mockup, step 40 | 2,050 | |

A plot is 30 m, a tile is 32 × 32 plots (0.9216 km²) and a district is 8 × 8 tiles (58.98 km²):

| | 5B, Jerus's density | 10B, Jerus's density | 10B, city2400's |
|---|---|---|---|
| area | 879,600 km² (938 km a side) | 1.759M km² (1,326 km) | 2.951M km² (1,718 km) |
| 30 m plots | 977M | 1.955B | **3.28B (past 2^31)** |
| tiles | 954,500 | 1.909M | 3.20M |
| districts | 14,910 (123 a side) | 29,830 (173 a side) | 50,030 (224 a side) |
| pyramid levels above districts | 7 | 8 | 8 |
| stored map, at 360 B a district | 5.4 MB | 10.7 MB | 18.0 MB |

A district record holds 73 counts × 4 B, 3 road lengths × 4 B, 6 reserves × 8 B and 8 B of flags.

**Prototypes** (`painter/*.java`, timed on this machine):
- **`TilePainter`** turns seed + counts into roads and buildings. Roads cross an edge at ports hashed from the shared edge, so neighbouring tiles agree (checked). It takes **12.2 µs a tile** for 146 buildings (12 or 25 kinds, 200 road plots), 7.4 µs at 40 buildings and 15.4 µs at 400.
- **`DistrictBench`** splits a district into its 64 tiles in 22.5 µs for 9,354 buildings. Each building goes to the tile its own hash picks, so one more building changes one tile by one (checked). A cumulative-floor split took 11.7 µs but moved 21 tile counts for each building added, so I rejected it. The pyramid over 29,929 districts builds in 31 ms (10,160 nodes).

**Worst-case objects per 1,920 × 1,080 frame:**
- **Buildings one by one (≥ 3.2 px a plot):** 202,500 plots in view. At Jerus's 0.143 buildings a plot that is about 28,900 buildings, and 202,500 only if every plot held a 1 × 1 building. At most 252 tiles are on screen, about 3.1 ms to paint them all from nothing.
- **4 × 4 blocks at 1.4 px:** 66,200.
- **Pyramid nodes of at least 4 px:** at most 129,600.
- **Each node cached as a 256 px image:** at most about 54 draws at any zoom, whatever the city's size.

**★ The scheme.** Save one record per district in a binary, deflated sidecar beside the save, not in the JSON. Derive tile counts on demand by the stable per-building deal, and sum the pyramid at load. Paint tiles from seed + counts; terrain comes from the seed and is never stored. Cache an image per visible node, and repaint only the nodes whose counts change. Use 64-bit or tile-relative plot coordinates.

## 7. The world's resolution

**Measured** (`WorldSum`): enumerating the Earth's 510M km² in 64 km cells (124,512 cells), drawing each cell's deposits of six kinds from the seed, takes **52 ms**. 10 km cells (5.1M) take 3.2 s.

**Deposit density.** The research cities hold 1.73 to 3.30 iron deposits per km² owned, at 3.28M t each (Jerus: 155 deposits, 5.08e8 t). At 1.73 per km² over all 510M km² that is 882M deposits and 2.9e15 t, an upper bound since the sea holds none.

**Sketch.**
- **World totals:** one 64 km pass at founding, or the closed form λ × area × mean size.
- **Visible deposits:** generated per km² in view. A screen at 1 px a plot covers 57.6 × 32.4 km, about 1,870 cells.
- **Stored:** only the depletion per district and kind, 30K × 6 × 8 B ≈ 1.4 MB at 10B.

## 8. The scale-fix batch, in build order

1. **Bisect the three order loops** (`Game.java:3846`, `Mortgage.java:272`, `BusinessInvestment.java:245`). Keep `firstRate` at the full order and the facts for one building, for the refusal text. Harness: the same decisions as the countdown over a long run, and a bound on the `financeProject` calls per order. About 0.5 day.
2. **Decide the land premium** (`LandMarket.java:152-155, 328-329`) ★: saturate it, or make it depend on density. About 0.5 day once Jerus decides.
3. **Counts to `long`**: the model, then the save, then the screens (section 2). This covers `SaveHeader`, `DataSave`, `HistorySave` as `List<Long>`, the int × int products, and every `(int) Math.round` on a count. 1.5 to 2 days.
4. **Money tolerances.** Add `MoneyAudit.tolerance(scale)` for the absolute money assertions and the journal reconciliation, plus the `Formats` quadrillion step and a `cash()` guard. About 0.5 day.
5. **A `ScaleCheck` harness.** Scale a research city to 5B and to 10B through the model's setters. Assert that population is unwrapped, the jobs, homes and households sums are exact, the audit's relative residual is under 1e-10 and the month median is bounded. Then run the suite and the docs pass.
6. **Land purchases proportional to the city,** like the mockup's 5.5% strips. Jerus's parcels are 0.2 to 2.4 km² (`landListing`), so 10B (1.76M km²) would take on the order of a million purchases.
7. **The map (section 6) and the world (section 7)**, in the land and map batch.

## 9. ★ decisions and risks

- ★1 **Scaling method.** Extensive state was scaled, per-household figures kept, and per-household shares and bonds divided by k. The bank's statements, the equity and exchange registers, the bond market and the fund were not scaled.
  - At ×1000 to ×10,000 the currency weakened faster than at ×1 (FX 1.43 against 0.83 after 30 months); I did not trace why.
  - Jerus's scaled copies show a one-off jump in cash and residual in their first month (relative up to 2e-6).
  - Timing is unaffected, since both builds reach the same state.
- ★2 **Bisection assumes the test is monotone in n.** It was exact to ×1000 and off by 0.12% at ×10,000. If exactness matters, step down from the top in doubling steps, then bisect.
- ★3 **The land premium is a balance call for Jerus.** Bigger cities can have dearer land, but not 2 × 10^6 times dearer.
- ★4 **`long` for counts, not `double`.** Sums stay exact and JSON loads it unchanged.
- ★5 **No `SAVE_FORMAT` bump.** An old build shows a save over 2^31 as unreadable rather than loading it wrong.
- ★6 **A relative money tolerance, not long cents** (section 3).
- ★7 **Store districts, not tiles.** The tile size and the 360 B record are my choices; per-tile storage would be 1.9M records, roughly 90 MB, at 10B.
- ★8 **World in 64 km cells.** Deposits come from the seed, and only depletion is stored.
- **Risk: history is never trimmed.** 1,000 years is about 37 MB on disk and about 110 MB of heap, which is acceptable.
- **Risk: gameplay.** 10B people on a 1,326 km square is a gameplay question too: travel, the reach of services, one city's two highways.
