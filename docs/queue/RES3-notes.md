# RES3 — home choice, typed rents and the 14-form catalogue

Built on batch/RES3 from shipped RES2 (7e484c4da), refreshed through main's
db9b1fd99 queue correction. Final verification and publication status are below.
No user city or user save slot 1–9 is opened or written. Existing checks use
their own isolated scratch slots; new RES3 native fixtures use slot 10.

The implementation adds the section 12 recipe catalogue, aggregate cell/class
assignment and class rental ledgers. The existing builder quotes, work queue,
funding and mortgage path remain authoritative; no finish timer is introduced.
The model and its caused checks precede the final calibration report.

★ The initial area/quality/layout utility follows the numerical prototype.
Actual garden geometry and explicitly ground-level access add family/retired
terms. Unknown equipment supplies no extra amenity. Equivalent demand and offer
nodes coalesce and their allocation splits pro rata. Residual paths maximize
compatible affordable placements, retaining valid affordable incumbents first.

★ R1g's selected residential wage scaler is ported, including its saved twelve
monthly pay indices and its load rebuild, from origin/batch/R1g. Its demographic,
founding, research, advice and blocked GDP changes are excluded. Land is not scaled.
BO1-H remains a seam; this batch adds no business-owner seeker group. OG1's real
construction queue and work proofs are retained.

★ Saved class quotes, actual trade quantities/values, staleness and assignments
are flows that cannot be reconstructed from closing stock. They pass through
DataSave and the load path. A format bump and matching oldest-loadable floor
are owed under the old-saves rule: old class IDs/mixes and missing assignments
would read wrongly. The final format is set from shipped main after HC1/PW5
integration. No conversion code is added.

Initial baseline --fast passed before source edits (52 s compile; no firm Stale
finding). The first integrated compile exposed and corrected a pooled-weight
getter typo. Focused acceptance, integrated profiles and the final report follow.

## Findings while integrating

HousingChoiceCheck passed its caused cardinality, adults-only, affordability,
premium vacancy, family garden, retired accessibility, incumbency, fractional
demand, equivalent-tie/input-reversal and billion-fold count cases.
HousingInventoryCheck passed its existing native inventory/title/listing and
RN2 first-month continuation proofs with the new catalogue.

The first BuildingDataCheck run retained its exact dated-ID assertion and
correctly found the new housing dates. The expected date map now additionally
contains all fourteen forms; every existing pinned date and equality remains.

The integrated fixture initially ordered the entire catalogue onto an empty
city's yard at once: no project completed in 24 months. This is the real shared
work queue, not a finish timer. Its premise was corrected to stage the existing
founding playtest's houses/jobs first, then measure the catalogue-wide orders.
Both historical and present-day cases now cause paid starts, actual occupied
rent, conserved monthly pool/household money, exact class-state reload and exact
next-month parity. A final repeat will incorporate the cash-strike budget split.

HousingCheck caught two model integration defects. Quote-only classes with no
physical row were erroneously sent to the inventory's explicit occupancy setter;
only physical rows now receive assignments. Its strict conservation check then
caught 4.000000000000001 assigned to four Houses. The allocation now rounds only
within FamilyModel.packingRounding, caps the sequential sum to actual supply,
and leaves a genuine excess an error. Neither the strict title/occupancy guard
nor a conservation/save assertion was relaxed. Typed monthly matching clamps
listing subsets as the existing legacy match did, retaining non-RE occupants.

★ Quote-only unavailable classes are reported but cannot match, start or trade.
Their asks freeze until the existing research/calendar gate opens them. The
initial year cutoffs (1900 through 12 storeys, 1931 for ordinary/luxury towers,
1969 for the 80-storey residential form) are conservative catalogue calibration
choices, not claims about the first building of each height. Their reference
points are the owner's [Empire State Building history](https://www.esbnyc.com/about/history)
(1931) and the contemporary AISC [Architect-Engineer, 1971](https://www.aisc.org/media/l3mkj3lx/aae_1971.pdf)
description of the Hancock mixed office/apartment tower. No modern equipment
attribute is inferred from either; only explicit ground-floor access is scored.
These are pointers to evidence, not redistributed source data or illustrations.

★ Asks use Game.doorPriceOf and its actual taxed builder/work/material/land path,
including the order queue. Separately let parking's capital is excluded from
housing's initial gross-return anchor, while all actual project costs still
have a payer. Candidates receive one-count eligible demand, after standing
assignments and the funded pipeline; quantities use the same bounded allocation
and mortgage forecasts use its attainable income per requested structure.

★ Existing debt is still a sector obligation. Fixed capital weights, including
sites, allocate its carried interest once; class rent targets then cover only
their own allocated carry when tight. A glut class cannot pass its shortfall
to another class. The 0.058 rate is an initialization comparator, not a forced
actual return. BO1-H's absent owner-family seeker group remains its queued seam.

## Integration mends

The prospective-worker forecast now uses actual unfilled/funded jobs and the
current wage/tax schedule, rather than the budget of a current unemployed or
retired household. Standing homes and funded sites reserve eligible demand
once. Each candidate alternative reads the same remaining demand snapshot;
requested quantities and the mortgage desk use its attainable occupied income.
No candidate receives full-capacity proceeds merely because it has capacity.

★ The live pooled-price sharing passes are retired. A single monthly quote
feeds normal and emergency allocation phases. Only unplaced singles whose
pooled recurring resources fund a real remaining door enter sharing groups.
Working singles move into their existing shape/tier/stage shared cells;
seekers retain explicit sharing flows. The existing financial census moves
stocks, without resetting debts or balances. Saved bounded transfer records
retain pre-transfer counts/resources for prospective normal-home substitution.
There is no new permanent household preference axis.

HousingMarketCheck now causes both sides: affordable singles remain normal,
while five otherwise unaffordable budgets fund an actual home. People, bills,
sharing counts/resources and the native snapshot reconcile. Quote-only classes
retain their cost comparator until built; an unbuilt class is not vacant stock.
Equal residual preferences split one-count rent pressure without catalogue-order
priority. Zero posted asks remain valid instead of silently reinitializing.

InvestCheck passes with the new forecast. Its grouped-thousands hold-reason
fixture now supplies 10,000 House doors and explicitly posts their class ask at
zero: with typed affordability, spare unaffordable houses do not cause the
original no-beneficial-order premise. All original assertions remain. The
controlled HD1/RN1-H fixtures retain their original three-form menu; independent
catalogue checks exercise all fourteen forms. No conservation or save assertion
has been removed or weakened.

The catalogue fixture observes BuildLog completion events every month, including
other funded work completing in the window. Its initial workforce, supporting
infrastructure, land and equity are disclosed fixture inputs; all residential
work and settlement use the paid builder path. All fourteen present-day orders
and all historically available orders complete through actual work, and both
cities close the monthly money identities and native continuation exactly.
Final focused checks and the committed report follow.

HousingDoorCheck's first typed run found a real omission: 1,081.827 households
were still unplaced at month 703. Smaller-home crowding was present, but the
original two-households-to-a-door emergency valve was absent. Worse, the legacy
noteUnplaced projection still inferred guests without actual class assignments.
The finding and its threshold remain intact; it now retains actual budgets and
class occupancy at the worst month when it fails.

★ A lease now carries both household quantity and occupied door-equivalents.
Normal placements and valid incumbents precede emergency sharing and doubling.
Doubling can fund two households in a vacant door, with half of the one rent
charged to each. Seekers keep their own-kind rule for those paid pairs, and
emergency groups rotate access to shared vacancies. Equal class/cell nodes
still coalesce.
No additional permanent preference or identity axis is introduced. The actual
allocation, rather than the legacy inferred valve, sets the crowding, unplaced
and reason readers. Separate household/door quantities and bills are saved and
validated together. HousingMarketCheck causes an otherwise unaffordable pair,
proves one physical door and one rent, and round-trips those quantities.

★ Retirement quantities are bounded by vacant RE titles in every class of the
actual mix, including the distress path. The existing spare-retirement hook is
also consulted for distress with a zero keep floor; other sectors retain their
existing result (Freight's zero floor permits the requested quantity). This
protects incumbents when a multi-structure order follows the one-structure
eligibility test. The caused double-door fixture asserts that cap.

The repeated lender run's retained inputs found two further real integration
errors. FamilyStructure.dependants includes retired adults; treating it as a
child count barred retired singles from compact homes. Retired shapes now have
zero children, while working and outside dependants retain the adults-only rule.
The student budget also omitted its existing open treasury facility, although
StudentHousehold actually funds the rent shortfall through that loan. Housing
now reads that actual planning room only while the facility is open; quoting
does not borrow anything. Ordinary households still receive no assumed new
credit. Caused checks assert both facility states, unchanged debt while quoting,
and retired input classification in the actual city. The lender finding is
being repeated without changing its people, rhythm or unplaced threshold.

The next played failure retained retired households with zero resources after
food/fees and unemployed parents with no own-kind host. The former implicit
valve called these guests, without requiring a second rent payment.

★ Emergency hosting now makes that actual: after normal affordable placements
and funded sharing, one additional household may join an already funded home.
The host continues its existing full bill; the guest pays zero, creates no extra
rental proceeds or credit, and cannot bypass the adults-only rule. Extended
family hosting may cross income/status cells; the bounded saved hosted counter
distinguishes it from a normal affordable lease and from a paid vacant pair.
The corresponding shares still sum to one real door, with at most two household
units per occupied door. The primary funded tenant is retained next month from
its actual class payment, rather than treating the emergency guest as a new
normal incumbent. A caused funded-host check asserts zero guest resources, the
host's unchanged bill, one physical home, one actual rent, and explicit hosting.

The hosting run narrowed the lender finding to 230.452 household units at month
810, almost entirely students, while every door and guest slot was used. The
cause was a second omitted part of the original valve: sharing groups were
formed only if a vacant door could be paid for. An unplaced sharing group can
now occupy a real remaining guest slot in a funded home too. Its actual placed
group count, not a proposal, changes the source census. Both emergency phases
use the remaining capacity 2 x door-equivalents minus household units, so the
later phase cannot add a third household unit to an already doubled home. The
caused host fixture also covers this combination, one rent and the two-unit cap.

## Actual migration room and the played proof

The next lender run had 137.346 unplaced household units at month 720. Every
physical door and its one guest slot was occupied. Migration still asked the
old size-only projection for room, using an already squeezed household mix as
its model of new arrivals. That could admit more raw new households than the
typed ledger could place.

★ In a live city FamilyModel.roomLeft now derives the remaining normal/guest
capacity from the actual class assignments and current physical inventory,
reserves all existing unplaced demand first, and uses the raw formed working
household mix for incoming size/children. It is a read-only projection: no new
quote, clearing, trade or saved counter. Current completions enter its room
without rewriting last month's quote. The existing migration lastRoom save
continues to carry the resulting bound. Bare FamilyModel fixtures retain their
original API. The callback is wired both at initial construction and the new-city
rebuild, and native continuation proves the load context.

The crowding approach ramp remains a physical measure, with the existing
separate affordability pull. Using only paid occupancy for that ramp froze an
empty founding city before workers could earn their first wages; the fixture
correctly failed both its growth and mortgage-door premises. The hard arrival
bound still uses actual remaining physical slots. Quote-only classes are never
queried as physical inventory; its Unknown-class guard remains strict.

HousingDoorCheck now passes the unchanged AUTO2 seed 12 city to month 900:
15,093 people, 3,192 homes; the worst unplaced value prints 0.000, and all 899
household cash identities close. The original mortgage-order, capital-issue,
trimming and no-land checks are intact. HousingMarketCheck passes the actual
room/host/paired-door invariants, both historical and present-day paid catalogue
construction, monthly pool/household money, native state and next-month parity.

Current SAVE_FORMAT and OLDEST_LOADABLE_FORMAT are 62, above shipped main's 61.
If HC1/PW5 publishes its 62 before RES3, RES3 merges it and advances from that
shipped value; no conversion is introduced. Its new form IDs likewise follow
any shipped Substation row. Neither concurrent batch has shipped as of the
latest main refresh recorded above.

## Save and unit findings during final checks

SaveFileCheck found two real zero-weight reload errors: the old size matcher
could replace a saved zero rent weight with a positive one. The load rebuild
now projects saved typed assignments without clearing again, and restores zero
weights as well as positive weights. The topology save byte comparisons pass
unchanged. Compatibility occupied-home readers and their saved field now read
actual typed lets, including a valid zero, rather than a capped demand estimate.

PowerViewCheck's future-input alignment changed the fixture's unsaved House
power override back to the native template but left the live sector's cached
residential draw behind. RE now genuinely pays that building-side utility bill.
The fixture recomputes its future sector draws from the aligned templates and
also asserts every sector's draw agrees; all dispatch/observation/history,
actual next bill, fuel, currency and audit comparisons remain. PowerViewCheck
passes, including both native fuel and exact native bills.

SaveFileCheck's withdrawal case drained only free fund cash, leaving cash
reserved by its earlier hand-order fixture. The withdrawal could therefore
pay instead of owing the sale. It now drains the whole actual till through
fundDrawOut (the treasury is the counterparty) and asserts it opens empty.
The actual fund sale, world fills, late payment and save comparisons pass.
The remaining hunger premise is being strengthened through actual retail input
prices and a nonzero rationed shelf, rather than depending on a poor cell's
pre-census balances remaining in that shape after housing moves. Assertions
and every continuation comparison remain, with delivered share also required
positive to avoid a default-zero round trip.

ReadPathCheck passes. The latest ReloadCheck exits 3 with only the two existing
RN3 fixture-3k premises (live and caused all-homes-closed). They are main's
documented stranded-debt-share rounding class: the hand closure can yield
0.99999999999999990 where the fixture requires exactly 1. Actual continuation
comparisons remain green; neither premise nor save comparison was relaxed.
DenominationCheck found a new divergence to mend, not a tolerance to relax.
The student's large finite planning allowance now follows the actual currency
unit through derived city context, including after load/new-city reset. No new
saved stock is added. Genuinely equivalent demand on zero-cost offers now
coalesces independent of irrelevant cash budgets; caused tests cover pro-rata
free offers and the scaled treasury allowance. Final currency proof follows.

The affected ScaleCheck fixture copier now classifies native housing titles and
typed flows explicitly: counts and aggregate bills grow with the copied city;
per-home asks/carry/capital, per-household budgets, attributes, dates and wage
indices remain intensive. It also includes RES2's native inventory holdings,
which the older copier omitted. No assertion or scaling tolerance changes.

The full 2,400-month LongPlaytest then reported one new model finding: three
early unplaced-household months, 2–38, with no money or native-continuation
finding. A short traced replay identifies zero housing budgets across every
new working cell in months 2–3: last month's empty aggregate income rows were
being divided across this month's new people. ★ Current scheduled net wages
and pensions now supply recurring housing income, with all existing fee,
food, interest and savings reserves retained. The ordinary payroll/cash strike
still pays it; the quote creates no income, loan or receipt. The same founding
inputs now have a caused HousingMarketCheck case with monthly money audits.

DenominationCheck's extended trace localized a second discrete choice defect:
class asks parted at m124 while actual assigned counts still agreed to about
2e-13 households and all budgets to about 9e-16 relative. A recomputed positive
ULP remainder was pressure on an unbuilt class; its no-stock positive-demand
branch changed the target multiplier. Residual demand now observes the same
existing packing-rounding bound as the allocation/summarizer. No capacity,
conservation or money/save tolerance moves. The caused fractional-demand case
requires exactly zero demand in every unbuilt class after full placement,
then checks each opening comparator is unchanged.

The first hunger input shock was too broad and stopped ordinary shopping;
the second revealed that a one-basket recipe pantry can round just under one
and Retail's whole-basket sale becomes zero. The fixture now caps its price
input at the model's existing retail cap, supplies ten real baskets and strikes
three months for health's lag. Its original hunger, shopping and continuation
assertions remain, and delivered share is additionally required positive.

The next trace proved why the per-source remainder bound alone was insufficient:
at m125 an unbuilt Duplex had demand 6.17145223814e-14 in one currency and zero
in the other, despite actual allocations agreeing to about 3e-13. ★ A blueprint
without standing stock retains its opening cost comparator; it has no observed
scarcity market. Demand is still reported and used once by prospective choice.
Scarcity/glut repricing applies when stock exists. This removes the discontinuous
"any positive quantity means double the target" shortcut without discarding
small fractional households or changing any financial comparison.

A separate caused price case failed before its mend: one ULP below ten fully
occupied homes waived carry and targeted 4.833333333333332 instead of 5. The
economic glut classification now uses the existing packing bound, while an
actual 0.1-home vacancy still waives the lift. Physical occupancy stays strictly
bounded. Native loading additionally refuses class demand below its occupied
quantity, atomically.

Final SaveFileCheck passes. Its full-shelves poverty premise is now caused by
the actual zero-pension policy and empty cash/foreign/paper stocks across one
large former tier's working and retired cells, with consumer credit exhausted.
That prevents the census's shape/retirement moves from refilling a single cell.
Other working tiers keep shopping. Ten real pantry baskets cause positive
rationed delivery; three presses carry the sale through health's lag. The broad
price shock was removed. No pension or hunger output is assigned directly, and
all original money/save comparisons remain; positive delivery is strengthened.

★ Historical ScaleCheck controls now begin with an ample one-billion-unit city
material yard each. The copier intentionally does not K-scale that yard or its
fixed 36-unit monthly replenishment. With actual builder quotes now pricing
homes, the large copy exhausted its yard on repairs at month 1 and the small
one retained 3,838 units: the House's allowance was 1.78522166439 versus zero,
with identical land and wage scaler. That is a different intensive cost input,
not a tracking error to hide in a larger band. Both controls now assert each
month that their House quote draws all its material from the same free-yard
regime. All original TRACK prices, savings, counts, registers and other bounds
pass unchanged; ordinary construction and the catalogue report retain the
actual unnormalized yard/funding path. ScaleCheck exits 0.

Refreshed main through 7b60b9a8d (queue coordination only). The current traced
DenominationCheck exits 3 with eight recorded DK1 comparison lines; first-month
and year checks pass, and national-card/trade-lane comparisons pass. Its
first later financial drift is visible in the Real Estate share mark at m133,
while housing asks and assigned quantities still agree to about 2e-12 and
5e-13. A native Bank book price split is visible by m142. The residual decade
gaps are about 1e-6 in output/rent/index/currency, with the shops' small till
about 2.5e-5 relative. The share/desk code is untouched. The recorded financial
rounding class remains with DK1; its automatic known-red verdict is not a
relaxed comparison. The stranger's money/save review sees the trace and the
unchanged 1e-9 checks. Final ordinary (untraced) repetition follows below.

The final HousingCheck staffing premise selected 150 Houses, a valid zero-staff
home, so it did not exercise the assertion's "cannot staff the ordered home"
condition. For that one decision the fixture now offers its two apartment
blocks, asserts that every offered home has posts, then immediately restores
its original three-form menu in finally. Its original order/staffing assertion
and every card, payroll and carry assertion remain. No model gate is bypassed.

The catalogue report now gives full-staff payroll cash as well as posts for
each form, plus the actual current staffed RE total. Both burden columns use
the same two-earner unskilled reference household, with its actual payslip
levies; the observed mixed unskilled row mean is reported separately so sharing
does not silently change the take-home comparison's reference household.

The final 2,400-month run passed its money and native continuation checks but
caused a later supply finding: 56 unplaced-household months, m1009–1102, worst
108.751 at m1067 with 4,547 physical homes. The traced replay shows all paid
doors already doubled: m1009 has 3,924 occupied doors, 3,924 emergency household
units, and 7.016 still needing supply despite positive budgets. Arrivals were
admitted against last month's household formation at working-family density;
current domestic formation and arrivals who start work next month then exceeded
the real two-household capacity. No occupancy limit is raised.

★ Before migration, a bounded copy of the existing FamilyModel previews current
domestic formation from the current pyramid and existing employment/outside
context. Positive family/adult demand changes reserve their compatible rooms
first. Immediate arrivals conservatively require at most one room per person;
when children are present their outside-household profile requires family space.
They do not assume the established workers' sharing/density. This frame moves
no people, money, quote or lease. It is derived once before migration, discarded
by clearing, rebuilt after load, and adds no saved field. The caused room cases
prove the formation reserve, actual extra-door admission and unchanged live
families/monthly state; the founding case also requires positive population and
occupied homes.

Main was refreshed through 653cb95ce: only QUEUE.md and the future FC2 brief
changed. No foreign-construction-pricing code is taken ahead of that batch.

The changed played path also exposed a withdrawal-fixture observation error.
Its first withdrawal owed 67.884292601; the normal step had already sold
12,168.8674462 Retail shares by rule to the world for 67.8842926011. No ask was
left resting for the fixture's later manual world buyer. The fund's actual cash
was 59.079630949 and that amount was paid late next press. The fixture now
counts actual current-month rule share-sale ledger units plus resting asks.
It separately requires no hand sale mixed in, and reads actual fund-to-world
settlement cash. Dividing by the highest actual sale price gives a conservative
lower bound on world-filled share quantity, preserving the original share-unit
thresholds. An unsold/unfunded position still fails; no sale output is assigned.
