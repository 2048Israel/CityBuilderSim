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
