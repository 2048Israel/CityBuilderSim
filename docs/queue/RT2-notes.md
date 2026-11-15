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
