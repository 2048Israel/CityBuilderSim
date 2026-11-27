# AP7 notes — slot 9 at 50x: the maintenance valuation's eligibility read once

Built from `482dbfab2` (0.9.214, format 66); main's `b543f1f9c` (a one-line CBK3 brief) merged at `4f70185a9`.
Brief: `docs/queue/AP7.md`. Jerus, 2026-11-25: "ok once again, slow down, slot 9". No saved field, no format change,
no conservation or save check touched. Model files: `BuildAdvice.java`, `WorksValue.java`; harness `BuildAdviceCheck`.

## 1. What was found

`BuildAdvice.maintained()` read `warranted()` afresh every time it was asked: at every count `count()` tried (its
doubling and halving), at every date of the maintenance horizon `WorksValue.relief()` steps through (yearly, monthly for
power), at every dated requirement of the power planner. Each read walks the known templates, asking each its research
(`Game.knows` → `Research.knownFrom` → `ResearchCatalog.knownFrom`), fits each serving template's new unit, and reads the
measure's demand today. Inside one of those searches nothing in the city changes (`suggestFor()` reads and quotes, R1g's
premise for `NewUnit.heldWhile`), so the answer cannot move. AP6's JFR: `warranted` 488 of 3,696 FX samples, 357 under relief.

## 2. What changed, and the calls

- ★ **One held answer per search, never between searches.** `BuildAdvice.Warrant` reads `warranted(game, m)` at its first
  ask and holds it for one search or valuation. Created once at the top of `suggestionFor()` (handed to its `count()`s,
  the power plan and its requirements, `timely()`, and its closing `maintained()`), once by every `count()` not handed
  one, and once per `WorksValue.relief()` valuation (handed to its `maintained()` and `count()`). Recommended over a
  pass-wide or month-wide hold: warranted's inputs (demand today, the known templates, each template's unit) have many
  writers, and a pass places orders between its searches; a hold that dies with its call has nothing to invalidate.
- ★ **`WorksValue.of` keeps its signature**: each valuation reads its own once rather than taking the reading's warrant
  through five overloads. The cost is one read per valuation (AP7's remaining `warranted`, 2.43% of FX below).
- A warrant asked about another city or measure reads afresh (never happens; a guard, not a cache key).
- Paving (`weighPaving`) and the auto-builder's own single calls (`AutoBuilder` 506/580, `lacksNow`) are unchanged:
  one read each already. `count()`'s own `supplyDemand` readings, the quotes and the search order are untouched.
- Harness hooks (since REV-AP7, section 8): a `BuildAdvice.WarrantProbe`, opened by a harness with `probe(game)` for one
  city on its own thread (a `ThreadLocal`), closed by try-with-resources. A Warrant takes it when it is made, only once a
  harness has opened one in the JVM (`probed`, a plain static); another city or thread never sees it. Plain fields: `bare`
  (every ask read afresh, the original behaviour), `checked` (each held answer also read afresh and compared), counts
  `asked`, `read`, `compared`, `differed`. In play an ask reads one plain final field more, never an atomic or a volatile.
  The warrant itself is a local of one call on one thread; nothing held is shared between threads or calls.
- No broad catalogue cache: `getKnownTemplates` falls from 6.98% to 0.94% of FX without one (section 6).

## 3. The proof: BuildAdviceCheck 16 (a slice wrote it; reviewed, restructured once)

The fixture is EssentialWorksCheck's medical-school town: 400 Community Health Centres, the doctors' vacancies the
demand, `FIRST_WORK_SHARE` of the Medical School's 420 seats the threshold. The town is saved once to its scratch slot 10
(asserted under the harness root); **each writer runs on a city of its own** loaded from it, so its warm reading is that
city's first held reading. Each block: a `fixture:` line asserting the writer moves the bare `warranted()` across the
threshold; warm readings (the card `suggestFor`, a `count()`, a `WorksValue.of` valuation) held; the writer; the same
readings held and bare, equal to the bit (doubles as raw bits, every record component), and the held answers read afresh
with none different. Writers: the people (skilled heads), a build (7 clinics finished), a demolition (7 retired), a
template (the school's `setUntil` off the menu), a research step, a load (slot 11, back to 12 clinics from 19).
The research step is on POWER: in a city founded the default way research gates only power plants (Game's templateKnown),
so the Small Steam Station is gated with code E1 and the city's own finding (`Research.restore`) brings it back.
Counts on a warranted city: one `count()` asks its warrant 10 times and reads `warranted()` once; one valuation asks it
100 times and reads it once. A probe is its city's and its thread's (REV-AP7): another city read on its thread counts 0 on
it, and its city read on another thread, with it set bare, holds its answer there (10 asks, 1 reading) and counts 0 on it.
Three auto-builder passes, every held answer compared: 7,395 compared, 0 differed.
★ The first version shared one city across the blocks; a kept-for-the-city's-life mutation then left the demolition,
research and load blocks green (they flip back to the answer first kept). Restructured so every block is red under it.

Mutation trials (`mutations/run.sh` in the evidence; source restored after each, the final source rerun green):

| trial: the held answer kept for the city's life (per game and measure) at | BuildAdviceCheck reds | writer blocks red |
|---|---:|---:|
| M1: every warrant (`Warrant.of`'s answer) | 11 | 6 of 6 |
| M2: `count()`'s own warrant | 8 | 6 of 6 |
| M3: relief's warrant (`WorksValue`) | 11 | 6 of 6 |
| M4: `suggestionFor()`'s warrant | 10 | 6 of 6 |

## 4. Checks

`./batch-check.sh --fast` green (Stale 0 firm). On the final model, one per call: `BuildAdviceCheck` exit 0;
`AutoBuildCheck` exit 3, only main's recorded FS12 care-fee red (`docs/notes/known-reds.txt`, as in AP5/AP6);
`LabourCheck`, `MonthPostsCheck`, `BondCheck` exit 0. `sh tools/pick.sh origin/main HEAD`: 40 of 259 harnesses, not
ALL, so no dry run is owed; review line "needed - threading: +AtomicLong, +volatile in BuildAdvice.java" (since REV-AP7,
"+new ThreadLocal": section 8).

The default `LongPlaytest`, `-Dplaytest.trace`, `-Xmx4g`, explicit Earth/world, one JVM each, both exit 0:

| trace file | main 482dbfab2 | AP7 af7c79979 (the model of HEAD) |
|---|---|---|
| t-bank.csv | aa2af5f1316c33dde8ba9366884ce152 | aa2af5f1316c33dde8ba9366884ce152 |
| t-bonds.csv | fa135be296fe76e98685d040a2d05a33 | fa135be296fe76e98685d040a2d05a33 |
| t-borrow.csv | 36b47896b1f126a8b75ef1102132319d | 36b47896b1f126a8b75ef1102132319d |
| t-build.csv | d2e6afe778756cef8b4c8186b0c316df | d2e6afe778756cef8b4c8186b0c316df |
| t-house.csv | 96cf152bb7b38f96e2198749252b88b2 | 96cf152bb7b38f96e2198749252b88b2 |
| t-labour.csv | e9cf9e0947a832addd388e041eaeccaf | e9cf9e0947a832addd388e041eaeccaf |
| t-month.csv | 9989337eb618a85564fa3d65307aeabe | 9989337eb618a85564fa3d65307aeabe |
| t-pop.csv | c6f5e3bacc4ae22a60a9be9775855794 | c6f5e3bacc4ae22a60a9be9775855794 |

All eight equal (and equal to AP6's and CBK1's recorded ones). No ensemble: the trace is unchanged.
Later commits change only `BuildAdviceCheck`, a comment in `BuildAdvice`, and docs.

## 5. Slot 9, two adjacent pairs

AP6's drivers (`Perf9`, `PausedProbe` at rung 8, 5 s a screen), the slot-9 copy (dojha, m4866, 21,918,046 people; md5s
equal to AP5's kept `orig`) in the evidence's own scratch, `GameFiles(dir, legacy)`, `-Xmx4g`. Pair 1: main then AP7;
pair 2: AP7 then main. The laptop ran other sessions: pair 2's main run is slow on every screen, so read each pair alone.

Headless, 12 months after 2 warm (mean / median / max ms): main 302.8 / 280.1 / 457.2 → AP7 288.0 / 263.2 / 417.0;
AP7 299.0 / 263.4 / 422.5 → main 295.1 / 298.1 / 410.9.

| screen (months/s; frame p50/p95 ms) | main (1) | AP7 (1) | AP7 (2) | main (2) | FRAME_P95 |
|---|---|---|---|---|---:|
| Build | 7.4; 116/179 | 8.7; 103/168 | 9.0; 89/153 | 6.1; 135/221 | 99 |
| Land | 7.3; 126/231 | 7.7; 128/193 | 7.9; 112/173 | 7.4; 128/225 | 57 |
| People | 9.5; 104/127 | 9.8; 86/125 | 9.8; 80/134 | 8.1; 120/152 | 34 |
| Services | 9.6; 94/139 | 9.9; 63/119 | 10.0; 14/101 | 8.4; 116/158 | 37 |
| Infra | 9.9; 12/97 | 9.8; 16/78 | 9.1; 75/149 | 6.8; 131/210 | 43 |
| Sectors | 8.6; 105/150 | 9.4; 39/103 | 7.2; 148/211 | 6.2; 130/233 | 65 |
| Govt | 9.9; 18/123 | 10.0; 22/81 | 9.2; 94/151 | 5.8; 168/212 | 36 |
| Finances | 9.8; 70/114 | 9.9; 20/81 | 8.4; 82/160 | 5.9; 172/192 | 32 |
| Bank | 9.8; 11/83 | 9.9; 16/76 | 7.7; 132/161 | 6.2; 153/207 | 42 |
| Trade | 9.9; 16/94 | 9.8; 79/117 | 8.6; 113/162 | 5.7; 177/215 | 20 |
| Policy | 9.6; 76/125 | 9.8; 16/101 | 9.8; 82/113 | 6.5; 159/207 | 32 |
| History | 9.7; 76/133 | 7.6; 100/200 | 9.6; 63/134 | 7.2; 132/192 | 60 |
| Rankings | 9.8; 25/113 | 9.8; 32/115 | 9.9; 16/82 | 7.2; 131/177 | 60 |

Every p95 still fails its budget; Build and Land stay below 10 months a second in both pairs.

## 6. One final profile; AP8 queued, not built

Read once offline: pair 2's AP7 recording `ap7-ui/ui-4866.jfr`, 66.68 s, 3,712 FX samples, 0 truncated; AP6's
`JfrAfterProfile.java` with eight more targets, `-Xmx256m`, one processor, Serial GC, exit 0.

| inclusive (% of FX) | AP6 final | AP7 final |
|---|---:|---:|
| AutoBuilder.pass | 35.50 | 28.02 |
| WorksValue.relief | 27.68 | 19.91 |
| BuildAdvice.maintained | 15.75 | 4.47 |
| BuildAdvice.warranted | 13.20 | 2.43 |
| BuildAdvice.count | 8.31 | 2.10 |
| BuildingManager.getKnownTemplates | 6.98 | 0.94 |
| ResearchCatalog.knownFrom (first app frame) | 5.14 | 0.38 |
| BondMarket.takeMonth | 15.31 | 16.33 |
| Game.runPrivateInvestment | 10.04 | 11.67 |
| Game.quoteBuild | (not read) | 7.57 |

★ Queued **AP8** (id checked free by `tools/batch-id.sh`), brief `docs/queue/AP8.md`: the households' bond orders
(`OrderBook.submit` 11.34% from `BondMarket.submit`), the build quote's abroad terms read twice for the same arguments
(`abroadOfOrder` 6.03%, once inside `orderPriceMultiplier` and once directly), private investment 11.67%,
`HouseholdBalance.followThePeople` 9.16%. No AP8 code was built.

## 7. Evidence and handoff

`C:/Users/Jerus/AppData/Local/Temp/cbs-AP7-evidence/`: `scratchpad/original` (the slot copy), `measure.sh`, `trace.sh`,
`main-classes` (482dbfab2), `ap7a-classes` (af7c79979), `ap7-classes` (6ea01c424), the four headless logs, the four
window logs and their JFRs, `main-trace/` and `ap7a-trace/`, `mutations/` (script, diffs, logs), `after-profile/`
(reader and `profile.txt`); since REV-AP7 `ap7r-classes` (8eab54613), `ap7r-trace/`, `main3-ui*` and `ap7r-ui*`.
No player's slot was opened or written. No dry run (pick not ALL).

## 8. REV-AP7: the mend of P1 (8eab54613)

P1, `BuildAdvice.java:1357`: every held ask incremented the JVM-wide `WARRANT_ASKED` and read the volatile switches, a
synchronised write inside the loops AP7 speeds, and one harness's switches reached every Game in the JVM. Mended:

- **Play pays one plain field read per ask**: `Warrant.of` reads its own final `probe` field (null in play) and nothing
  else new. No atomic, no volatile remains in the warrant; the `AtomicLong`s and `volatile` switches are gone.
- **The counters and switches live in a `WarrantProbe`** a harness opens for one city on its own thread
  (`BuildAdvice.probe(game)`, a `ThreadLocal`, closed by try-with-resources). A Warrant takes it when it is made, and only
  after a harness has opened one in the JVM (`probed`, a plain static, never set in play), so a Warrant made in play reads
  that one plain field once. A Warrant of another city, or made on another thread, never sees the probe.
- The held answer's logic is the same code in play and under a probe (the probe only counts, compares, or reads bare), so
  the harness proves the path play runs. The pick's review line is now "threading: +new ThreadLocal in BuildAdvice.java".

Re-shown on the mend:

- `BuildAdviceCheck` exit 0, the same counts as before (10 asks with 1 read, 100 with 1, 7,395 compared and 0 differed),
  and a new line: a probe is its city's and its thread's (another city read on its thread counts 0 on it; its city read on
  another thread, with it set bare, holds its answer there, 10 asks and 1 reading, and counts 0 on it).
- The mutations, `mutations/run.sh` unchanged on the mended source (the first results in `mutations/before-rev/`):

  | trial | reds | writer blocks red |
  |---|---:|---:|
  | M1: every warrant kept for the city's life | 11 | 6 of 6 |
  | M2: `count()`'s own warrant | 9 | 6 of 6 |
  | M3: relief's warrant (`WorksValue`) | 11 | 6 of 6 |
  | M4: `suggestionFor()`'s warrant | 10 | 6 of 6 |

  M2's ninth red is the new isolation line: a kept warrant carries this thread's probe to the other thread.
- The default trace on the mend's classes (`ap7r-classes`, 8eab54613): all eight md5s equal main's (section 4's table).
- `./batch-check.sh --fast` green.
- One adjacent 50x pair, main 482dbfab2 then the mend (`main3-ui`, `ap7r-ui`), months a second; frame p50/p95 ms:

  | screen | main (3) | AP7 mended (3) |
  |---|---|---|
  | Build | 8.5; 107/154 | 9.2; 74/142 |
  | Land | 7.1; 133/294 | 9.3; 87/137 |
  | People | 9.8; 88/124 | 9.9; 22/111 |
  | Services | 9.8; 78/123 | 9.9; 13/93 |
  | Sectors | 9.8; 32/93 | 9.7; 17/91 |
  | the other eight | 9.8 to 10.0 | 9.8 to 10.0 |

  Every p95 still fails its budget on both sides (13 of 13). Build and Land are still below 10, so AP8 stands.
