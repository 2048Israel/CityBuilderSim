# RT2 — the other buildings counted in their sector retirement measure

## Scope and calls

Built from main 2853630b on batch/RT2. The queue row links RT1 notes, call 5.
Read known-reds before changing fixtures: no entry names RT2 or RT1 at this
base. Existing FoodProcessingCheck's meat-price difference belongs to FPK1;
it is separate from any moved fixture here.

★ Keep retirement additive. FoodIndustry keeps its headcount measure and
counts both baked goods of each building in heads, by BAKED_KG_A_HEAD.
CoalMining's whole-deposit exhaustion measure will count raw mine nameplate,
as unitsOf and the existing type-exhaustion measure do. Its saleable thermal
flow depends on washing and cannot be added mine by mine. Freight will measure
standing track against offered tonnes, as its unitsOf counts track; the
operating capacity stays the minimum of track, sets and handling. A shortage
of sets or stations must not conceal otherwise spare track.
No save fields, money movement or screens change.

## Verification

In progress. Each sector gets a fixture demonstrating the former mismatch
and the quantity the retirement constants permit. Retain each assertion and
premise if a played fixture needs a new seed, month or city.

## Records

Pending final pick, ensemble, stranger's review and retained gate run.

First verification: fast exit 0, 0 firm Stale findings; the three sector
harnesses exit 0. Same harness bytecode with 2853630b's three sector classes
compiled into target/rt2-before/classes: FoodIndustryCheck fails its sum
(677,000 kg versus 193,600 heads) and sale (0 versus 1 Bakery); CoalMiningCheck
fails only fully washed, whole-deposit exhaustion (400,000 thermal tonnes
versus 2,000,000 raw, 1 versus 2 longwalls); RailDepotCheck fails both sums and
sales (0 versus 2 lines under the 2,500-tonne set bottleneck; 1 versus 2
under 200,000 tonnes of station handling, with 1,800,000 track tonnes).
The unwashed and type-exhaustion coal controls pass main's classes.
Logs: target/rt2-before/{FoodIndustryCheck,CoalMiningCheck,RailDepotCheck}.log,
target/checks/rt2-sectors.txt. Also return no FoodIndustry measure without a
city: its old fallback was kilograms again, with no shop headcount to serve.

## Moved fixture: LongPlaytest's power and consumables

The first final-model named run (target/checks/rt2-playtest.txt) exits 4:
FoodIndustryCheck, CoalMiningCheck and RailDepotCheck pass, but three existing
LongPlaytest assertions fail in its works-power money fixture. These are new
moved fixtures, not known reds. Its 36-month city has retired every Industrial
Bakery, so it sells no power and buys none of their consumables. The audit
itself still closes. Main's retained LD3 run reaches both flows at m36
(power 0.02k, consumables 0.37k).

★ Re-pick only that fixture's month, 36 to 8. Keep the same city, buildings,
generators, consumables, staffing, retirement rules and all four assertions.
The monthly probe target/rt2-probes/power.txt shows both flows and audit
annotations from m3 through m9; m8 is the last month both kinds stand
(one of each), and m9 retires the last generator. At m8, power sold and the
utility's own payment are 0.020706771k, consumables 0.460594276k inside
18.177914781k imports. The original m36 has no generator. The chosen m8
comes after retirement has actually fired, rather than disabling it.

The full LongPlaytest.MoneyFixtures.run, called unchanged by a temporary
package-local launcher after the month re-pick, exits 0 (all assertions;
target/rt2-probes/money-final.txt). The rebuilt fast check exits 0 with
0 firm Stale findings.

## Default trace beside main

Main is LD3 c0120d77, retained at
C:/Users/Jerus/AppData/Local/Temp/cbs-gate-LD3-mauTys0a/checks/LongPlaytest.log
(LD3 notes establish it matches CM2). RT2's final model, before the independent
money-fixture month re-pick, is target/checks/LongPlaytest.log; the changed
default trace is unaffected by that re-pick. No main baseline was rerun.
Comparison: target/rt2-trace.diff and target/rt2-trace-comparison.json.

| At m2405 | Main | RT2 |
|---|---:|---:|
| Population | 1,074,657 | 651,292 |
| GDP/month | $986.8B | $15.6T |
| Price index | 4,294.25 | 115,232.00 |
| Spare-capacity buildings retired | 154 | 167 |
| Industry buildings retired by spare capacity | 7 | 7 |
| Distress retirements | 19 | 28 |
| Findings | map movement at m2389, CL6 ring verdict | none |

The same aggregate of seven Industry sales hides different timing and a
different path. No CoalMining or Freight spare-capacity sale occurs in this
default city; the sector fixtures cause those rules independently.

Known-reds kept apart: no registry entry names RT2 or RT1 at the base or
after LD3. The LongPlaytest "(no FAIL line)" and "industry stands farther
out than the homes" entries belong to CL6; "a building on the map moved"
belongs to DM3-R. All three are inactive on RT2's path, which does not repair
their code, so their queued fault registrations remain. CPB1's negative-GDP
entry is also not observed in this default run; the ensemble is separate.

The related-check selection exits 0: eight harnesses, including
PriceLevelCheck, AgricultureCheck, FoodProcessingCheck, RailCheck and
FreightViewCheck (RailCheck also selects its named borrowers by substring).
Logs: target/checks/rt2-related.txt. No fixture or assertion moved there;
FPK1's existing meat-price line is green on this path. Model and fixtures
were reported for stranger review as a54e054e, now pushed with provisional
ship records. REV-RT2-1 is reviewing c0120d77..a54e054e.

## Stranger review: the railway's handling

REV-RT2-1 found that track-only accounting could sell handling still needed:
eight depots plus four lines, enough sets, 350,000 tonnes offered. The
track-based quotient proposed two depot sales, dropping handling from
400,000 to 300,000 tonnes. Main's bottleneck-based quotient had refused it.
This is RT2's own fault and is mended here, not registered as a known red.

★ Add a small Sector.limitSpareRetirement quantity hook, called only by
BusinessInvestment.planRetirement after its existing most-held, spare-track
and gradual-sale limits. Freight limits the proposed quantity so the
prospective operating minimum of track, sets and handling still holds the
planner's existing keepAtLeast headroom. A yes/no mayRetire predicate cannot
cap a sale of two at one. The default hook changes no other sector; distress
retirement keeps its existing rule. Sector and BusinessInvestment were
reported to the orchestrator before these small shared-file hunks.

RailDepotCheck section 10 now also causes the reviewer's exact template-based
350,000-tonne case, a case allowing one of two proposed depots, and siding
handling cases. It checks the whole sale against the remaining operating
minimum, with demand and expected quantities from the model's constants.

Review-mend verification: RailDepotCheck exits 0. The same new harness with
a54e054e's Sector, BusinessInvestment and Freight compiled into
target/rt2-review-before/classes exits 1, exactly four handling failures:
it sells two depots where 0 and 1 are safe, and two spurs where 0 and 1 are
safe. Log: target/rt2-review-before/RailDepotCheck.log.
The no-op controls AgricultureCheck, FoodProcessingCheck, FoodIndustryCheck
and CoalMiningCheck all exit 0 (target/checks/rt2-no-op.txt), retaining their
asserted retirement quantities. The mended shared hook changes no additive
sector's sale.

REV-RT2-2 on a54e054e..9632e40b returns SHIP: every rail kind is capped by
prospective operating capacity, other sectors' quantities and sale accounting
are unchanged, and the fixtures cause the conditions. RT2's own model is
settled at 9632e40b. The later 5d3a4afc only refreshes generated line references.
The orchestrator reserves local ALL pick, ensemble and dry run until after
CM1b's and R0f's dry runs, so no heavy RT2 job runs during this wait.

CM1b/main e8ac5a53 is merged at a6cd4c1f; only generated dials and map-index
conflicts, regenerated from the combined source. No RT2 model changes.
On 2026-11-15 at 01:23 the orchestrator changes the shared-PC slot order to
RT1b's dry run, R0f's rerun, then RT2. Local ALL pick, ensemble and dry run
remain on hold by explicit instruction. RT2 stays idle with no JVMs.

RT1b/main cd7f9dca is merged after its ship at 01:54. Only generated
harness/map-index conflicts, regenerated from the combined source. Its
holder-ledger repair is inherited from main; RT2's own model stays unchanged.
R0f's dry run starts next, with RT2 still idle until the orchestrator's go.

## Final combined model and execution route

R0f/main b49a6023 (0.9.111) is merged after the orchestrator's 02:44 go.
AllChecks auto-merges with both MoneyDatesCheck and FoodIndustryCheck;
conflicts are confined to generated indexes and regenerated from combined
source. No RT2 model/fixture change since the accepted 9632e40b review.

The final combined sector selection exits 0 (all three sector harnesses),
with compile, maps and 0 firm Stale findings. The unchanged full money-fixture
run also exits 0; m8 still causes both power and consumable flows. Logs:
target/checks/rt2-final-sectors.txt and
target/rt2-probes/money-merged-final.txt.

The starter's explicit bus instruction 20261115-024441-229-orchestrator-880d
is: "no separate local ALL pick: the dry run runs it". Thus the final ALL
selection runs through the launcher's fresh clone, after the ensemble,
rather than duplicating that run in this worktree. The heavy slot is RT2's;
ensemble and dry run run one after the other. Before the dry run RT2 still
sends the required "RT2 dry run?" coordination message.

Known-reds re-read on b49a6023: no entry names RT2 or RT1. R0f adds only its
existing MoneyDatesCheck whole-foreign-account twin entry, queued as R0f-S;
leave it distinct from RT2. R0f's final eight default CSVs match its prior
main byte for byte (R0f notes, final verification), and RT1b's do too. The
newest stored ensemble baseline is therefore still 0.9.106.

## First ensemble, before the goods-station floor

One run on the final merged model, classes snapshot 0783d66f, label RT2:
80/80 complete, none missing or short, 85.2 minutes with the normal five
workers. Newest stored baseline 0.9.106; no main baseline rerun.
Runner exit 0, batch verdict PASS, all ten M1/F1 hard checks pass.
Every policy has zero money-audit failures, zero unread audits and zero
LongPlaytest findings in all sixteen seeds. No CPB1 finding occurs here.

Raw score: 22/25 criteria, versus baseline 22/26; H1 remains retired and H2
is now vacuous. The three changed verdicts are information, as instructed:

| Criterion | Baseline 0.9.106 | RT2 | Verdict change (information) |
|---|---|---|---|
| ZERO_HOT H2, late inflation minus AUTO2 twin | -0.36% [-5.42%, 4.53%] | 0.34% [-2.08%, 2.77%] | FAIL to VACUOUS: no burst to stop; the numbers alone would fail |
| ZERO_SLACK T2, final population | 513,902 [100,209, 1,932,923] | 1,136,972 [49,717, 2,018,574] | FAIL to PASS |
| ZERO_HOT T2, final population | 6,644 [4,733, 13,403] | 4,786 [3,227, 8,781] | PASS to FAIL, below 75% of baseline |

For context, AUTO2's median final population is 1,860,775
[35,109, 3,095,735], versus 1,269,080 [505,663, 2,728,057].
All sixteen seeds reach 40,000 people: median m1198 [727, 1779], versus
m1159 [622, 1727]; both T1 and AUTO2 T2 stay PASS.
All other verdicts stay as baseline. The full table with baseline readings
and raw summary is committed as docs/ensemble/RT2.md for publisher naming.
Run output: target/ensemble/RT2/{score.md,summary.json,baseline.md},
target/checks/rt2-ensemble.txt.

The orchestrator's R2b request (20261115-031200-063-orchestrator-48ae) asks for
the eight final default trace CSVs in a retained named path, in these notes
and the ready message. The dry run will set the trace prefix within its
documented CBS_GATE_DIRECTORY so the trace outlives this worktree.

## Final-review finding: goods cannot use spare sidings

The first dry run at C:/Users/Jerus/AppData/Local/Temp/cbs-gate-RT2-20261115-041842
is deliberately stopped, exit 130, not a reusable kept run. Branch
216b7e2bf7fb44ce0e0e2dee46dcdc7cd2f63ed3, main
b49a6023c892e61e575f39c57b4d16774cf6fdd7. It was stopped by its own normal
tools/gate.sh --stop route; origin refs stayed unmoved. Its logs remain
diagnostic evidence, not readiness.

Further review found an RT2-owned regression missed by the aggregate guard:
nine depots (450,000 station tonnes), eight spurs (400,000 siding tonnes),
four lines and sets for 450,000 tonnes, offered 400,000 tonnes of GOODS only.
The guard sold three depots because aggregate handling still covered the
offer; Freight.deal then carried only 300,000 goods tonnes. Main's unchanged
Freight sells none and carries all 400,000. Its Freight blob is unchanged
between original main 2853630b and b49a6023. Probe logs:
target/rt2-probes/goods-retirement-{rt2,main}.txt.
This is ours to mend, not a known-red transfer.

★ Retain the goods share of planner headroom specifically in stations, as
well as retaining total headroom in track, sets and aggregate handling.
Goods offered is rTonnes - rBulkOffered, the railway's offer AFTER the
ships' first cut; the city's own bulk is excluded from these planner figures
already. No new saved state or change to the shared default hook.

The added RailDepotCheck cases fail before this extra guard: two goods-only
offers (400,000 and 350,000 tonnes) sell three depots where zero and one are
safe, and lose goods headroom. A mixed 200,000 bulk / 200,000 goods control
allows three safely because bulk can ride sidings. The same assertions check
both the decision quantity and Freight.deal on the whole sale at headroom.
Before log: target/rt2-goods-before/RailDepotCheck.log, four failures.

The mended RailDepotCheck exits 0; compile and maps pass, Stale has zero firm
findings (target/checks/rt2-goods-mend.txt). Its goods cases sell 0, 1 and 3
depots respectively and Freight.deal retains both traffic kinds' headroom.
The independent nine-depot probe now sells 0 and carries all 400,000 goods
tonnes (target/rt2-probes/goods-retirement-mended.txt).
The first 80-run score above is therefore intermediate, not the final model's
baseline. Its committed artifact is removed until the mended model's new run
replaces it; its raw data remains under target/ensemble/RT2.

The orchestrator accepts the mend and requests a delta review
(20261115-043619-864-orchestrator-fdc6).
His explicit resource-policy revision, message
20261115-043926-091-orchestrator-c5eb, is:
"That constraint was mine and I've revised it on tonight's evidence: one dry
run beside one ensemble is allowed ... never two of either at once."
This revises the original brief's no-ensemble-beside-a-dry-run restriction.
KD1's baseline ensemble goes first; RT2's new final-model ensemble follows it,
then RT2's dry run. RT2's new ensemble remains on hold meanwhile.

REV-RT2-3 on 216b7e2b..2cb4a7b1 returns SHIP: goods station room and total
operating room retain both streams at planner headroom, and the three new
cases require exactly 0, 1 and 3 safe sales without weakening checks.
The orchestrator declares the model final at 2cb4a7b1; RT2's new ensemble
follows KD1's baseline (estimated 85 minutes from 04:40), with a go to follow.
No heavy RT2 job runs during this hold.
