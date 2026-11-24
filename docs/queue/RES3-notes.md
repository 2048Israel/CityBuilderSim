# RES3 — home choice, typed rents and the 14-form catalogue

Built on batch/RES3 from shipped RES2 (7e484c4da), refreshed through HC1 on
main efca2c5fd (0.9.190), merged as ef18a4b6c, and the queue-only e66c310a3.
AP2 then shipped as 1b89c5c4c (0.9.191), merged as 9af9ee926. Final
verification and publication status are below.
Current integration is shipped PW5 f937d2c22 (0.9.192): RES3's new form IDs are
156-166 after Substation 155, nextId 167, and save format/floor 64. Earlier
63/155-165 records below describe the pre-PW5 build and its checks.
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
would read wrongly. The final format and floor are 63, advancing from HC1's
shipped 62. No conversion code is added.

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

RES3 initially used format/floor 62 above RES2's 61. After HC1 shipped its 62,
RES3 merged it and advanced both numbers to 63; no conversion is introduced.
Its new form IDs must likewise follow any shipped Substation row. PW5 has not
shipped as of the latest main refresh recorded above.

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

HousingDoorCheck then caused a compatibility bottleneck: 77.558 unplaced at
m706, mostly large child families, with all paid doors occupied but spare compact
guest slots. The bounded HousingChoiceCheck reproduction failed four assertions:
one adult softly chose the family door, the large family fit only that door in
emergency, and the compact door stayed unused. Locking the normal assignment
before the emergency pass made the soft choice an accidental hard constraint.

★ Emergency completion now uses the normal matching's residual paths. Integer
edge costs count normally suitable placements: a zero-cost augmentation may
reroute movers while preserving the maximum normal count. Retained incumbents
have no reverse edge. Equivalent cells/classes still coalesce and split pro rata;
physical caps, budgets, children restrictions and strict final stock checks stay.
The reproduction is green, including input reversal and an incumbent that must
stay even when that leaves the family blocked.

A separate integrated input case failed with 0.85 of a child family unplaced:
five actual single adults formed a group that took the family host space before
ordinary guests were offered compact space. Three funded hosts had enough real
slots for everyone after that transfer. ★ The group phase reserves compatible
family host slots for waiting child families first; adult groups can use compact
host space explicitly as crowding. Only actual occupied host slots are offered,
and each funded host retains its original bill. No income, credit or capacity
is invented. The caused case also checks actual family-member conservation,
sharing, the exact three occupied doors, receipts and the two-household cap.

The final allocation replay passed HousingDoorCheck through m900: 36,625 people,
8,603 homes, no material unplaced household (worst prints 0.000, m802), and all
899 household cash identities closed. The bank-capital premise is real again:
49 capital-short orders were offered to the owners whole, none offered less;
the original down-payment, halving, advisor and no-stranded-cash checks pass.
HousingChoiceCheck and HousingMarketCheck are green with both caused mends.

The committed [catalogue report](RES3-catalogue.md) is generated from those model
inputs. HC1-integrated repeat: small city 210 people, 55.842 ms,
H/T/A/M/L 65/32/112/0/0. Present-day: 45.487 ms, 84/32/330/0/0;
larger 148,967-person case 54.640 ms, 82/32/314/0/0. Historical:
39.424 ms, 62/32/193/0/0; larger 149,126-person case 41.473 ms,
62/32/193/0/0. These are entire integrated months, not solver
microbenchmarks. All retain 141 permanent household cells. Modern physical
classes are 32; historical known stock is 16 classes, with all 32 quoted and
availability reported. M is new typed loan cohorts (HO1), L live listing cohorts;
both are zero here. Paid starts and BuildLog completions are observed separately.

The next full named pass was green for --fast, inventory, households/flows,
building data, investment, housing, utilities, SaveFileCheck, ReadPathCheck and
ReloadCheck. Reload compared 423,170 history figures and 99,495 saved figures
through m1229 exactly. HousingCheck's existing optional retirement-in-debt path
was reported OBSERVED/not reached, not counted as a pass.

DenominationCheck then returned the recorded exit 3 but with 18 large comparison
failures, rather than the earlier eight small DK1 differences. That verdict was
not accepted as evidence of a main fault. The trace shows equivalent housing
asks/allocations through m124 (about 1e-15/1e-13); at m125 Agriculture alone
offered 22.3997697976 in the plain twin, nothing in the reformed twin. The plain
twin built two Mixed Farms while the other held for the city's housing priority.
Their m124 Agriculture cash, debt, shares, income and quote agreed to arithmetic
precision; the share quote parted only after that unequal offering. Both books
were empty, so this was not the queued DK1 dust-fill event.

★ A caused final-match case has 100 funded hosts, 100 zero-resource guests and
one ULP of a 100-household source in a separate cell. It failed three exact-zero
assertions: that tiny entire cell survived the per-cell rounding and became a
positive final shortage, activating Agriculture's unchanged `> 0` guard. A real
0.0001-household deficit remained explicitly unplaced in the same fixture.
The final aggregate shortage/reason counters now use the existing whole-source
FamilyModel.packingRounding once. Family status rows and residual price pressure
respect that final classification. No assignment, title, bill, money tolerance
or stock check is changed; physical occupancy and two-household limits remain
strict. The genuine fractional shortage must still activate housing priority.
This is RES3's integration fault and is mended here, not waived as DK1.

The large unit fork disappeared, but the remaining eight small comparisons were
also investigated rather than waived. Their first assignment split was m181,
0.002825 household units, while budgets/asks still agreed within 1e-14/1e-15.
The affected cells had identical affordability decisions and preference order in
both currencies. A second caused kernel case failed: an incumbent's one-ULP
retention remainder was counted as an extra suitable mover. ★ Residual need and
free supply now use their original source's existing packing resolution before
becoming graph nodes, and again after a phase. No capacity is rounded upward.
That caused case passes; DenominationCheck is now fully green, all comparisons.
At m181 the assignment difference is only 9e-13 households. This was another RES3
residual-node defect, not the queued DK1 event. No money/save bound was changed.

Modern ScaleCheck tracking is now green too. Its historical tracking still has
five empirical savings/deposit findings after the changed played founding. The
optional trace compares the native class quotes, free-material regime, bank,
household stocks, audits and financial flows; that diagnosis follows below.

The historical source and K-copy agree at load on native stock and money, and
the existing whole-horse-post rounding bounds pass. Every home quote draws the
same free-material regime. Through the tracking window rent weights are exactly
33464 at both sizes, rents agree to about 1e-6, GDP differs only about 1e-5 at
the savings jump, and neither bank resolves or writes down deposits. Month four
household cash is 18522.6861343 versus 19529.1947414 per city. The actual pool
ledger isolates the transfer: desk shares bought from households are 0.8451357253
versus 1073.4886562494. Purchases in the opposite direction are 320.8681100 versus
349.2379038. These are cash-for-share transactions, not rent or invented income.
The household cash identity closes on both actual sets of flows. The optional
trace keeps their prior/closing savings, foreign holdings, bonds, debt and bank
cash; the historical tracking loop now additionally asserts pool and household
conservation at the existing MoneyAudit tolerances in both cities every month.

★ The five changed historical empirical TRACK pins are recorded with FS2, the
queued sweep that owns ScaleCheck's played fixtures. All existing bands and
native/save/count/register checks remain. They are not labelled the earlier
DK1 event: no precise desk-threshold fix is claimed here. The historical town's
whole-post differences change a discrete financial trading outcome after the
played founding moved. This batch changes no Exchange or OrderBook production
code. RES3's own two unit-invariance defects above are mended and all currency
comparisons pass. The final ScaleCheck repeat must also pass the added money
identities and name only these five FS2 pins.

## HC1 integration

HC1 shipped on main efca2c5fd (0.9.190) while the prior-source full playtest was
running. It was merged immediately; its published VERSION, card stocks, debt
statistics, cash channels and census stock routes are kept. Generated map
conflicts are regenerated from the combined source. Format/floor advance from
shipped 62 to 63 because RES3's class flows cannot be reconstructed. PW5's IDs
remain to be refreshed if it ships before this batch.

The kept prior-source full playtest is target/RES3-LongPlaytest-pre-HC1.log:
its only model red is 20 unplaced-household months, 689-922. Its money, own-line
bounds and native continuation checks are green. That supply finding is not
waived; the integrated model's traced replay follows the card-budget seam.

A caused five-to-one sharing/census move preserves all HC1 positions: card
balance 328.0831740677, holder position 3.2808317407, late balance 65.6166348135,
late-holder position 0.6561663481, and excluded position 1.6404158703. The budget
case first failed with zero principal reserve where 0.3 was due. ★ Live housing
now quotes the entire opening balance's contractual card minimum at the bank's
current funding price and scheduled income. The bare API reserves interest and
minimum principal. The prediction is separate from HC1's saved settled flow:
it matches actual card settlement, and neither the saved minimum/interest nor
any card position changes when a home is quoted. HousingMarketCheck is green
with these caused cases and both native-era continuations.

The integrated 1,000-month diagnostic has no unplaced-household finding. Its
three end-of-run own-line mean bounds are short-window findings and are not
waived; the full 2,400-month acceptance run uses the original bounds.

The integrated HousingDoorCheck's only red is its played capital-shortage
opportunity: there are zero such orders in the m431-900 window. It observes
157 lender-door months, with 70 short-segment orders written whole, no cut or
held short-segment orders, no down-payment refusal, no stranded cash and zero
unplaced households. All 899 household cash identities close. Population is
15,168 and homes 2,656. ★ This missing played opportunity is recorded with
FS11, the queued sweep that owns HousingDoorCheck, retaining the original
assertion and all money/placement checks. The deliberately caused capital
cases in CreditCrunchCheck are included in the final named verification.

## Integrated native asset-mark finding

The unchanged exact HC2 debt-statistics save assertion caught a real missing
flow. Every financial holding, cell count, car stock and exchange rate matched,
but debt/assets was 2.5883189261098205e-9 live versus 2.5883189268332515e-9
loaded. The live used-car mark was 0.22470679371627045; load lost it and used
the new-car-price floor, 0.17791977463721569, instead. This was not a housing
or debt-stock rounding tolerance.

★ The closed used-car trade is now one bounded citywide DataSave tuple: traded,
offered, spend, credit and actual price. The late load restorer keeps the mark
after the rebuild. Currency reform divides spend, credit and price; the K-city
copier multiplies counts, spend and credit and leaves the unit price alone.
SaveFileCheck causes a positive actual used-car trade, checks the tuple exactly,
and retains the original exact debt-statistics assertion. Both reload assertions
passed on the first mended run. DenominationCheck and ScaleCheck additionally
check the tuple through their existing reform and native copy paths.

The AW2 construction twin's staffing premise also failed after the played town
moved: closing its ten shops and four medical schools left only 60.22% of a new
shop's posts available, below the unchanged 80% gate. ★ Its fixture now also
retires the actual university and releases its ground before the existing
settling month. This causes spare workers through the model's workplace inputs;
it changes neither the gate nor its staffing answer. The original staffing,
automatic land buying, private-order and native continuation assertions all
passed. SaveFileCheck is green, including the exact used-car and debt-survey
checks (target/RES3-save-mends.txt and target/checks/SaveFileCheck.log).

The first new reform guard found that DenominationCheck's played town offered
no used cars. Its money comparisons passed but the positive-mark premise was
empty. The fixture now causes a financed second-hand sale with the existing
HouseholdBalance market (an insolvent car owner and a buyer with a deposit),
then restores that same actual closed trade to both currency twins before the
reform. ScaleCheck also sends that nonzero trade through its native JSON copier.
This is an input for the flow-transform checks; SaveFileCheck independently
causes the positive trade through the integrated played city's monthly market.
The caused flow has 800 offered, 20 traded, price 15.679012345679013,
spend 313.58024691358025 and credit 223.58024691358042. The buyer's deposit
is above SHARE_CUSHION_MONTHS times income and derived from CAR_DEPOSIT,
CAR_ADOPTION and the used-car floor; every saved tuple component is nonzero.
The copier proof uses a separate copy of the fixture's complete native save,
leaving the economic tracking source intact. Its initial tuple-only JSON input
was incomplete for the census copier and correctly failed on missing keys.

## Final integrated profile

The final HousingMarketCheck repeat passed both eras, their actual paid starts
and completion receipts, monthly pool/household money identities and exact
native continuation. Its complete report is RES3-catalogue.md. Measured full
months on this laptop (not solver-only timings):

| Scratch city | People | Month ms | H/T/A/M/L |
| --- | ---: | ---: | --- |
| Small | 210 | 49.178 | 65/32/112/0/0 |
| Present-day | supporting catalogue inputs above | 51.096 | 84/32/330/0/0 |
| Larger present-day | 148,967 | 45.542 | 82/32/314/0/0 |
| Historical | supporting catalogue inputs above | 31.336 | 62/32/193/0/0 |
| Larger historical | 149,126 | 35.990 | 62/32/193/0/0 |

Permanent household cells remain 141 in both larger cities; physical classes
are 32 today and 16 in 1900. All 32 class quotes and 14 form availability rows
are reported in each era. M is the new typed ownership-loan cohort seam for HO1,
and L is live listing cohorts; both remain zero in these rental fixtures.

The final ReloadCheck repeat is exit 0: its m1229 autosave continuation compares
818,555 history numbers in 341 series and 105,494 saved numbers exactly. After
the passed assertions it prints an asynchronous autosave-history temporary-file
rename warning in the shared Earth scratch fixture (the same warning appeared
on the earlier pre-card-integration repeat). This is recorded separately from
the successful native comparisons; no save check or production I/O path was
weakened to pass it. SaveSlotCheck also passes, including its held-writer and
one-writer-at-a-time cases; its wall-clock timing observation is soft.

## Retirement-room and capital-fixture finding

The deliberately caused CreditCrunchCheck exposed a derived-room defect: its
physical retirement removed 70 homes, yet the room read stayed at 108.6. Leases
from the previous clearing still occupied a demolished class, and their
displaced households were not reserved against other classes before arrivals.
The crunch then had 7.59 unplaced households in its first month.

A focused HousingMarketCheck case first failed with room 4 where 2 fit: two
actually placed child families lose their occupied form while another form
stands. ★ The read-only room projection now reserves other rooms for displaced
lease households, keeping child families in family-capable doors and using
compact rooms for displaced adults first. This adds no quote, clearing or
saved state; the assertion also checks that the read leaves the monthly state
unchanged. The physical two-household-per-door limit remains exact.

The crunch fixture's cut budget consequently counts two potential slots per
removed door. Its unchanged margin assertion and no-homelessness watch still
apply. Its separate hand-written six-block order previously reached a demand
limit before the bank-capital trim: actual standing homes and funded sites
already reserved those tenants. ★ That hand-order fixture now physically removes
standing rental homes before its order, causing actual replacement demand;
no vacant candidate receives fictitious rent to force the lender through.

The mended CreditCrunchCheck is fully green: 48 homes retired leave 12.6
households of room, every crunch month has zero unplaced households, and the
original six-block hand-order assertion reaches the bank-capital trim. Its
door quote is 6851.6 versus the planner's 6754.4; the lender reads the door
price, the owners are offered 5138.7 and put in 244.6. The original pricing,
partial-equity, leverage-band and no-sticky-capital-refusal assertions all pass.

AP2's shipped exact-state performance changes are retained. Its VERSION is
0.9.191; the shipped save format remains 62, so RES3's format/floor remain 63.
The source merge resolved only four generated-document conflicts, regenerated
by the next named checks. The already-compiled full playtest finishes on its
pre-AP2 classes; AP2's own 2,405-month trace comparison is byte-identical.
Following native checks compile the merged source.

The final HousingDoorCheck repeat has 149 lender-door months, 52 whole orders
while the segment is short, no cut/held/down-payment refusal, and zero unplaced
households throughout the window. All 899 household money identities close.
It ends at 10,801 people and 1,893 homes. Its only red remains the missing
played bank-capital opportunity, recorded with FS11; the deliberately caused
CreditCrunchCheck is fully green. These final numbers supersede the earlier
157/70 observation on the pre-retirement-room source.

The final full LongPlaytest is exit 0. Its 2,404 audited monthly transitions
have no money finding, every reload matches, and the maximum unplaced-household
count is zero. The original mean-month carrier bounds hold: UnsettledIncome
3,360 ppm against 5,000; UnsettledRent 345 against 500; UnsettledFees 21.1
against 30. The additional HA1 and payslip-carrier fixtures pass too. Kept logs:
target/RES3-LongPlaytest-final.log and target/RES3-LongPlaytest-final.txt,
compiled from a5f4f6c0c before the AP2 merge described above. No short-window
bound or homeless finding is waived.

SaveFileCheck on the AP2-integrated source is exit 0. It retains exact card
positions/flows, used-car trade/mark and debt-statistics comparisons. The AW2
workplace inputs now give share 1.000000 before the original 80% staffing gate;
the ordinary saved/live construction twins still pass every original assertion.

## Local acceptance and review handoff

The final model includes HC1 and AP2 from shipped main 1b89c5c4c. PW5 has not
shipped at this handoff; its eventual shipped ID and save format must be
refreshed before RES3's final gate. All calls below use the supplied earth/world
paths and isolated fixture directories. Named checks run one call at a time;
the final card selection additionally uses cbs.parallel=1 because it selects
BuildCardCheck too.

| Check | Result |
| --- | --- |
| --fast | 0; no firm Stale finding, 199 soft observations |
| HousingInventoryCheck, HousingChoiceCheck, HousingMarketCheck | 0 each; final merged-tree repeats |
| HouseholdCheck, HouseholdFlowCheck, BuildingDataCheck | 0 each; final merged-tree repeats |
| CardCheck (including BuildCardCheck), DebtStatisticsCheck | 0; final merged-tree repeats |
| HousingCheck, InvestCheck, CreditCrunchCheck | 0 each; all original laws retained |
| HousingDoorCheck | 3; only its one FS11 played-opportunity premise; money and placement green |
| DenominationCheck | 0; original 1e-9 comparisons and the nonzero used-trade transforms |
| ScaleCheck | 3; two HT1-H historical financial pins after REV-RES3; strict native, count, flow-transform and money checks green |
| LongPlaytest | 0; full 2,400-month run, additional fixtures, no findings or unplaced households |
| SaveFileCheck, ReloadCheck, ReadPathCheck | 0 each on the AP2-integrated source |
| SaveSlotCheck | 0 in the post-HC1 native-writer repeat; one soft wall-clock observation |
| WaterCheck | Red at 42c991099 for the residential utility partition; 0 after REV-RES3 mends |

The earlier pre-HC1 five-pin ScaleCheck record is retained above as evidence,
not as five current waivers. The final ship file keeps only month-four savings
and month-five deposits under HT1-H, plus the single HousingDoorCheck FS11 row.
No money, conservation, save, numeric or carrier bound is widened.

The final read-only ScaleCheck trace confirms the current FS2 class. At month
four, household savings per city are 25,568.8001625 versus 26,637.8022059.
The desk buys household shares for 0.0196997180 versus 1,173.78489006;
its reverse sales are 323.779344144 versus 323.780738880. Both banks have
zero resolution loss. Housing rent is 0.00344637227121 versus 0.00344637010252,
with equal occupied weight 33,286. The GDP miss is 1.76e-5 and CPI miss 9.56e-6;
the existing half-post bound and both pool/household money identities pass.
This is the same discrete cash-for-shares class traced before HC1, with the
current card-integrated numbers. Its only remaining original TRACK reds are
month-four savings and month-five deposits. Kept trace:
target/RES3-ScaleCheck-final-trace.log (metadata target/RES3-scale-final-trace.txt).

Review is owed for money and saves: typed lease budgets/rents and actual builder
funding; DataSave, the load rebuild, SAVE_FORMAT and OLDEST_LOADABLE_FORMAT.
There is no threading change. The pick is ALL 246. The provisional marker
requests the stranger's review; the changed trace's 80-run ensemble and the ALL
dry run on the gate box follow that review's go, before the marker is dropped.

## REV-RES3: mends from 42c991099

The review found two P1s and three further findings. All five are handled in
this delta; the earlier FS2 ownership references above describe the superseded
handoff, not an approval to pass a red under a shipped row.

★ `acceptTypedHousing` now takes `lastHomesBySize` from the inventory on both
the live and load paths. This is derived state, so it needs no saved field.
The typed load previously skipped `house()` and left this input null, or kept
the previous city's census during an in-place load. The caused scarce-door
fixture first failed its fresh-load and in-place-load floor comparisons and
its first-next-month migration crowding comparison. Its live floor is
161.284185872; the same full family matrix without a census reads 100.769453876.
The corrected basic case preserves the actual next-month crowding factor
0.547229504882 in all three cities. The final fixture also funds replacement
housing before the native save, to exercise the following construction and
migration phase. Save/read/reload results are recorded below.

★ The two ScaleCheck TRACK rows now name queued HT1-H. FS2 shipped on
2026-11-20 and is not an eligible queued owner. The m4 trace establishes the
cash-for-shares divergence, but does not establish an `Exchange.deskLimits`
room threshold parting the controls; no DK1 cause is claimed. HT1-H is the
review's specified fallback for that played premise. Its queue wording is the
orchestrator's record. Both original TRACK assertions and all money identities
remain. HousingDoorCheck retains its valid FS11 owner.

The numeric IDs 0 (House), 1 (Studio Apartments) and 6 (Low-Rise Apartments)
are retained while their specifications are revised to RES1 section 12:

| Existing row | Specification changes |
| --- | --- |
| 0 / C02 | dwelling `legacy` to `H120`; one private bay; benchmark cost weight 846,000 |
| 1 / C06 | cash 8,628 to 8,692; materials 320 to 322; points 680 to 700; dwelling `S`; 20 shared bays; benchmark cost weight 196,725 |
| 6 / C08 | cash 10,196 to 10,284; materials 377 to 381; points 2,400 to 2,432; area 80 to 75 m²; dwelling `F`; 32 shared bays; benchmark cost weight 319,714.285714 |

All three explicitly use the existing 1900 availability gate. Their door counts,
capacities, bedrooms and qualities retain their previous values. Eleven new
rows append at IDs 155-165; no numeric ID is renumbered. The catalogue's nextId
and fallback comment now say 166. These recipe/dwelling-key changes are also
part of why the format/floor bump is owed. PW5 remains unshipped at this mend;
if it ships first, its 155 Substation must precede our unshipped rows 156-166.
Move `HousingForms.ALL`'s hard-coded IDs (HousingForms.java:40-96) with the JSON
rows: otherwise `installFallback` rewrites PW5's 155 Substation as a Compact
house. Set nextId 167, raise format/floor from its shipped value, and regenerate
the report before gating.

★ The services partition now includes only *unbilled* residential buildings in
the homes category. Rental-building power/water already belong to Real Estate's
billed draw. The existing screen's categories therefore count each unit once
and retain the city's own draw. BuildingsTemplate, ServicesManager,
EconomyManager and UtilitiesHandler documentation now agrees with that policy.
The 40,000-house WaterCheck case first failed four assertions: it double-counted
8,000 water units and 40,000 power units, reported the city's 20 water units as
-7,980, and summed 64,170 units against the actual 56,170. The corrected test
keeps actual draw, supply, rationing, payroll, revenue and city-use checks;
billed homes add zero to the unbilled category. WaterCheck is green.
WaterCheck was also red at the reviewed 42c991099 handoff; its omission from
that handoff's acceptance list did not make it green. The Services screen's
homes segment now reads 0, with the rented homes under "business, billed".
The existing category labels need a later relabel to explain that partition;
this batch changes no UI.

★ The exact one-below-floor boundary uses the genuine shipped 0.9.191
format-62 save/history pair, not current JSON re-stamped as old. Its producer
ran alone against unedited shipped 1b89c5c4c classes, after that detached
worktree's --fast compile. It wrote scratch slot 10 at month 31. The pair has
141 native 144-field household cells and two HC2 surveys, with no RES3 market,
home-pay history or used-trade tuple. Original schema, version and SHA-256
hashes are pinned in the check and in
src/main/resources/fixtures/res3-format62/README.md. No file is converted.
The common refusal helper retains every previous format-60/61 byte, history,
manager, month and cash guard, and additionally checks family identity and the
migration floor. The new pair is refused in fresh and already-running cities,
with both files byte-identical and the entire running city/history unchanged;
ReadPathCheck repeats its reads after refusal. SaveFileCheck is green.

The final caused reload fixture pays for twenty construction depots, holds
new private/city orders, and saves one funded aggregate order of 100 Houses
while family doors are scarce. Existing work and staffing complete 70 Houses
on the next native press. The source floor is 189.183099038 versus
122.097069387 in the identical family matrix without its door census. Fresh
and in-place loads preserve that floor. All three cities then admit
20.8935037267 people with crowding factor 0.470736722549 and identical class
markets; their money audits close. A separate guard requires positive arrivals,
an active crowding ramp strictly between zero and one, and arrivals below the
physical-room cap, so the migration comparison cannot pass on a zero-flow or
already-capped case. The earlier simpler fixture's zero-arrival outcome was
retained as a finding of insufficient test inputs, not used as acceptance.

Post-review checks: HousingMarketCheck (both eras, profiles, positive scarce-door
reload twins), WaterCheck, SaveFileCheck, ReadPathCheck, ReloadCheck and
BuildingDataCheck all exit 0. ScaleCheck exits 3 with only the same two original
TRACK pins, now assigned to queued HT1-H. The fast check reports no firm Stale
finding and 199 soft observations. The full playtest and other household/card
acceptance results above remain the pre-review live-model record; no bound or
assertion was widened in these mends. The changed main since the review is
queue/measurement documentation only (through ea16576cd), merged locally.

REV-RES3's delta review of 47764c061 is SHIP (2026-11-22). Its three small
follow-ups are folded in without another review: the fallback-ID warning
above, one-space nextId indentation, and the WaterCheck/Services-label record.

## First ensemble and the funded-demand mend (2026-11-23)

The approved model, merged with main's queue records through cbf8a362e, was
pushed as 964e65cd7. `RES3-final-20261123` ran on the gate box, eight cities at
a time: 80/80 finished, none short or missing, 16.7 minutes of runs. Its files
are kept under target/ensemble/RES3-final-20261123 and its controller log is
target/RES3-ensemble-final.txt. The box used its earth-2/world-1 copies; the
0.9.190 baseline has no extra JAVA_OPTS beyond its data properties.

This first run is **not acceptance of the final model**. Against 0.9.190's
25/26 information-plus-hard score and 10/10 hard score, it scored 11/26 and
5/10. Every M1 money-audit criterion passed, with zero failed or unread months.
Every F1 criterion failed solely on "households with no home": AUTO2 seeds
2/10/12, ZERO_SLACK 2/10/12, ZERO_HOT 11, SHOCK 4/10, HIGH 7/10. AUTO2's final
median population was 2,723,424 against the baseline's 5,217,451; its takeoff
criterion still passed 16/16, median month 1,016 against 739. The complete
score and changed information verdicts remain in that run's score.md. No
F1 line is waived on the basis of the money audit passing.

Read-only native HousingDoorCheck overlays replayed six representative seeds,
with the ensemble's founding, advice rhythm and native policy setters. Each
matched the ensemble's population, price index, paid rent and monthly GDP at
the written nine-significant-figure precision in every observed row: AUTO2-10
(171 rows), AUTO2-12 (301), AUTO2-2 (111), ZERO_HOT-11 (61), SHOCK-4 (71), HIGH-7
(81). These are four-column comparisons, not a claim of complete CSV byte
identity. The overlays and logs remain under target/RES3-shortage-*; production
HousingDoorCheck and LongPlaytest are restored after diagnostics.

The native shortages include distinct causes. Seed 10's first shortage has
605 homes, no pipeline, and 1,211.88980565 seeking households, with the plan
held for land. Seed 12's first has 2,001 homes, no pipeline, and 4,005.29230203
seekers; its 192 flagged months include 178 land holds and 14 months building.
SHOCK-4's nine flagged months are land holds. FB11's growth-land funding class
is relevant to those observations; that does not explain away the other
funding holds or establish the final model's ensemble verdict.

At ZERO_HOT-11 month 247 the plan selects the Large-home luxury tower, whose
native lender cover is 0.938915846302 against Mortgage's 1.20 requirement.
A read-only check of all fourteen alternatives finds no ignored feasible
project: the other thirteen miss the operating build hurdle; the selected
tower alone covers it and then fails the lender. For example, Compact house
income 0.0412068448168 is below its 0.0434345266555 hurdle. This finding does
not justify bypassing either the operating margin or the lender's coverage.

★ The class-price pressure had an omission in RES3 itself. After emergency
doubling it counted the occupied physical shares, then looked for additional
demand only among households still without any shelter. Two households able
to fund whole suitable homes, sheltered in one door, therefore registered only
one unit of demand. The legacy rent pressure still counted seekers who were
doubled up. With demand erased until shelter actually ran out, rents could
stay below the native build hurdle during a real shortage of separate doors.

The mend keeps the physical occupied shares, then counts each funded remaining
share once in the actual class when a whole home there is suitable and
affordable. Other residual demand goes once to its best compatible affordable
alternative, retaining the existing tie split. It does not create a second
lease, receipt or permanent preference cell. The new native scratch regression
first failed with class demand 1 against two sheltered households. Its control
retains the lower demand when only a half bill is affordable. An intermediate
attempt sent all extra demand to preferred alternatives; the occupied-class
scarcity guard correctly stayed red. Keeping the funded demand for the actual
suitable class makes both guards green, with the one actual rent unchanged.

HousingMarketCheck passes the mend, including both era reports, real funded
builds, the positive-arrival reload twins, cards, budgets and money audits.
The existing saved class-demand field carries the corrected quantity; no new
saved field is introduced. The format/floor remains 63, with the real shipped
format-62 boundary. The final model's ensemble is still owed after the new
money-review delta; the failed first run is retained as the finding.

The corrected 4a33d0a83 model replayed ZERO_HOT-11 through its full endpoint,
month 402: all 401 observed months have no flagged housing shortage, against
the first model's 16 flagged months. This uses the same native founding,
advice rhythm and policy, without changing the operating margin or mortgage
test. Kept: target/RES3-pressure-ZERO_HOT-11.log and its batch metadata.

The new report in RES3-catalogue.md is from this corrected model:

| Scratch city | Integrated month | H/T/A/M/L |
| --- | --- | --- |
| Small, 203 people | 39.840 ms | 70/32/70/0/0 |
| Present-day catalogue | 51.649 ms | 84/32/351/0/0 |
| Present-day larger, 148,949 people | 47.180 ms | 82/32/341/0/0 |
| Historical catalogue | 38.997 ms | 62/32/187/0/0 |
| Historical larger, 149,029 people | 38.411 ms | 62/32/187/0/0 |

Both catalogue eras still quote all 32 classes, and retain 141 permanent
household cells in the larger cities. All fourteen forms follow the actual
era gates and paid builder path.
The table and catalogue were refreshed on the final PW5-integrated model
ec8fb7510, including its native format-64 reloads and next-month twins.

The post-mend ScaleCheck's historical month-four savings and month-five
deposits TRACK assertions are green, so their old ship-file pins are removed.
Its only red is the existing free-growth fixture search: eight candidate
windows within the unchanged 60-month search provide no six consecutive
months with affordability pull 1. The evaluated native count, tracking and
money laws pass; the free-growth copy lacks its fixture premise. This is the
played-condition class owned by queued HT1-H, not shipped FS2. Its original
assertion and bounds remain, and the replacement ship row names that exact
premise rather than carrying the now-green financial pins. Rejudging the kept
native log against the updated rows reports only the known fixture red.

The original HousingDoorCheck is now fully green after the pressure mend:
6,809 family homes finish in its window, no household is left without shelter,
and all 899 monthly household cash identities close. Its FS11 ship row is
removed. No part of HousingDoorCheck was changed to obtain that result.

The affected SaveFileCheck then exposed an exact load-seam difference:
familyRentWeight 7636.075444713484 became 7636.075444713485. The loader derived
the family half by subtracting studio weight from total weight, overwriting
the exact family value already saved in Real Estate's extras. Card loans and
bank capital agreed; the complete opening JSON comparison correctly failed.
★ Carry the existing saved family segment through the rebuild, and restore
total/studio/family aggregates independently at both typed-load restore sites.
This adds one load-workspace value, with no new JSON key or conversion. The
original complete CardCheck.continues comparison now passes. Its temporary
diagnostic writes are removed; no equality or tolerance was relaxed.

## PW5 shipped: applied integration and native format-63 boundary

PW5 shipped as f937d2c22 (0.9.192, format/floor 63) during these checks. The
own check controller was stopped by its verified PID; no verdict from a partial
run is acceptance. Merge 59a40be89 keeps PW5's JSON/fallback Substation 155,
shifts only RES3's eleven unshipped rows to 156-166, and moves HousingForms.ALL's
hard-coded IDs with them. The three existing numeric IDs remain 0/1/6, with
the specification changes recorded above. nextId and the fallback comment
are 167. Format and floor rise together from shipped 63 to 64: PW5's native
layout has its closing grid costs but lacks RES3's class market and new rental
flows. No old-save conversion is introduced.

The merge preserves both refusal histories. Its poor-retiree fixture keeps
RES3's source/destination cohort cash/credit inputs and PW5's securities
conservation: the cohort's securities, basis, tax claims and bonds move to a
populated recipient outside that former-tier cohort. They do not disappear or
refill the target through retirement/shape redistribution. The original care,
issuer, money, save and continuation assertions remain.

The genuine native 0.9.192 format-63 pair was produced at month 31 by
HousingFormat63Fixture, running alone against unedited f937d2c22 classes after
that detached tree's --fast build. Source diffs before/after are empty. Native
slot 10 wrote both files; gzip only packages the raw bytes. The pair contains
141 144-field cells, two HC1 debt surveys and PW5's pending/struck grid costs,
with no RES3 market/home-pay/used-trade fields. Provenance and both pinned
uncompressed SHA-256 values are in
src/main/resources/fixtures/res3-format63/README.md. The detached source tree
was removed only after its resolved target path and unedited source were
verified; raw files and build/producer evidence remain under target/RES3-native63-*.

ec8fb7510 preserves all native 60/61 refusal fixtures, PW5's native 62 pair,
and RES3's independent native 62 pair. The new 63 pair supplies the exact
one-below-floor case. All pairs retain the byte, manager, family-floor, history
identity and full running-city guards. The PW5-integrated SaveFileCheck exits
0, including the populated-city exact continuation. The final ensemble must
use this merged model and the newer 0.9.192 baseline; the failed 0.9.190-based
first run is not reused as that verdict.

On the merged model, ReloadCheck's only red is main's already recorded HT1-H
hand-order premise: fixture 3b has zero crews drawing materials. Every native
save, field and continuation comparison passes. ScaleCheck retains only the
free-growth premise above, now ten candidate windows on the PW5-integrated
path; all its evaluated tracking, count and money laws pass. No bound changes.

DenominationCheck exposed another played-fixture premise: its bus towns had
positive riders, fares and transit bills, but no positive private-driver fuel
payment. ★ Give this unit-reform fixture explicit opening fleet assets in an
existing working household cell, and transfer a buffer from its funded
treasury using the model's savings-cushion, subsistence and journey-cost inputs.
No closed fuel value is set. A cash-only attempt still produced no fleet; the
opening fleet makes native drivers and a fuel bill. The first press strikes
that bill, and the following press carries it into household settlement.
Both fixed native periods now assert pool and household money conservation in
both towns. This causes the existing positive-fuel condition through model
inputs and the actual settlement sequence, without changing the condition or
the exact currency-reform comparisons.

387d54043's DenominationCheck exits 0. Its native driver readings are ownership
0.00218071132 per household, 4.01000040 drivers, fuel struck 0.0000463920781,
billed 0.00783993061 and paid 0.00782785503. The paid figure is the preceding
press's actual bill. All reform assertions remain, with added money audits;
the ordinary floating-currency neutrality twins keep their original setup.
The failed attempts remain in target/RES3-driver-original.*, -cash-only.log
and -first-press.log; the final record is target/RES3-PW5-DenominationCheck*.

## Final integrated checks before the second review

The final code is 387d54043, with shipped main f937d2c22 merged. Each named
harness ran alone with the two shared data paths and cbs.parallel=1. These
checks exit 0: HousingMarketCheck, HousingCheck, HousingDoorCheck,
HouseholdCheck, HouseholdFlowCheck, BuildingDataCheck, InvestCheck, WaterCheck,
SubstationCheck, SaveFileCheck, ReadPathCheck, DenominationCheck and LongPlaytest.
HousingDoorCheck closes all 899 monthly household cash identities. The complete
2,400-month playtest and its additional controls report 2,404 audited months,
no findings, and every reload matched. The final --fast exits 0, with zero
firm stale findings and 199 soft findings. Evidence is kept in
target/RES3-PW5-<harness>.txt and .log, and target/RES3-PW5-fast.txt.

ReloadCheck and ScaleCheck exit 3, known reds only. ReloadCheck's one hand-order
premise is already recorded on main under HT1-H; all its save/continuation
comparisons pass. ScaleCheck's sole free-growth premise is the ten-window case
above, recorded under queued HT1-H in this ship file. The old historical TRACK
and HousingDoorCheck pins are gone because those assertions pass. No money,
save, conservation or comparison check was weakened.

The final pick against origin/main is ALL, 247 harnesses. Its review line is
"needed - saves: DataSave.java, +SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT".
Money and the load path also changed after REV-RES3's SHIP on 47764c061:
funded doubled demand, exact saved rental segments, the shipped PW5 integration
and the native driver fixture need the reviewer's delta check. The provisional
ship marker stays in place. A fresh 80-run ensemble against the newest
0.9.192 baseline and the ALL dry run remain owed after that review; the failed
first ensemble is evidence, not the final ensemble verdict. No dry run has
started, and the orchestrator's dry-run go is still required.

REV-RES3 reviewed 47764c061..8c7a470ff and returned SHIP with no P1. Its two P2s
are folded without another review: main ecbc3b975 now gives HT1-H the remaining
free-growth premise, and SaveFileCheck's boundary comment names the actual
res3-format63 pair from shipped 0.9.192. The native bytes and assertions are
unchanged. The final ensemble is scored against docs/ensemble/0.9.192.md,
with rents, affordability and growth read alongside the hard gates.
The comment-only follow-up's --fast exits 0 (0 firm/199 soft); the six
generated map changes were dates only and were discarded. The named model
checks above remain the validation of the unchanged reviewed model.

## Second ensemble: emergency host space must respect feasible alternatives

The reviewed fd7071f54 snapshot ran on gatebox as
RES3-reviewed-PW5-20261123 against docs/ensemble/0.9.192.md, all 80 runs complete,
none missing or short. It exits 4: 14/26 overall, 6/10 hard, beside the baseline's
20/26 and 6/10. All five M1 policies pass, with zero failed or unread audit
months. F1 fails for AUTO2, ZERO_SLACK and HIGH (3/16 each versus baseline 1),
and SHOCK (2/16 versus 1); ZERO_HOT passes with 0/16. The unchanged raw score
and CSVs remain in target/ensemble/RES3-reviewed-PW5-20261123. This failed run
is not the final baseline and grants no exception to the findings.

All sixteen AUTO2 towns reach 40,000, median month 1,027 versus 747. Final
median populations are AUTO2 2,490,129 versus 4,649,724; ZERO_SLACK 1,462,930
versus 2,883,284; ZERO_HOT 5,968 versus 7,632; SHOCK 23,140 versus 67,498; HIGH
9,789 versus 32,356. Only ZERO_HOT passes T2's informational 75% bound.

For the reviewer's rent/growth watch, these are medians across the sixteen
seed readings, each reading averaged over its last 120 native months:

| Policy | Rent series / index, baseline | RES3 | Rent series / worker wage, baseline | RES3 |
| --- | --- | --- | --- | --- |
| AUTO2 | 0.007019 | 0.006542 | 0.0844 | 0.0803 |
| ZERO_SLACK | 0.007420 | 0.007230 | 0.1008 | 0.0961 |
| ZERO_HOT | 0.008373 | 0.009185 | 0.0997 | 0.1109 |
| SHOCK | 0.007967 | 0.008674 | 0.0886 | 0.0895 |
| HIGH | 0.008852 | 0.010366 | 0.0967 | 0.1156 |

These use Ensemble's existing weighted getAverageRentPaid series and average
worker wage, a price-series proxy. Actual affordability uses each household's
full recurring budget. Native baseline and RES3 CSV readings, sources, ranges
and per-seed values are kept in target/RES3-reviewed-rent-readings.json.

Native AUTO2 replays of seeds 9, 3 and 14 compare all 491 observed months'
population, index, rent and GDP with the ensemble at its nine-significant-digit
CSV precision: zero differences. This establishes the recorded city path,
without claiming full CSV byte identity. Findings in a CSV label their first
occurrence; duration comes from the native trace, not that label alone.

Seed 3 is short from m609 through the trace's m660 endpoint (52 months),
peaking there at 103.203 households, 1,147 homes, no pipeline and 2,271 sq ft
free. Every flagged month is held for land. Ground looks m461, 469 and 591
already refuse the whole offer under the cash-share budget: at m591 price
1,470.890 versus budget 46.142. Seed 14 flags m634 and two later months through
m660, peak 2.442 at m651, 941 homes, no pipeline and 4,048 sq ft free. Its
m592/593 ground looks also refuse the offer under the cash-share budget. These
observations show the whole-offer/cash-share mechanism, but do not establish
that its cause is outside RES3. REV-RES3 requires native main 0.9.192 twins
before an FB11 attribution; the forms' land use, demand and smaller towns may
instead be the cause. Neither seed passes by name on this evidence alone.
The kept traces are target/RES3-reviewed-native-{3,9,14}.log, with the native
market/inventory snapshots in target/RES3-shortage-<seed>-*.json.

Seed 9 is different: at m47 it leaves 1.344704 retired households unplaced
while homes are vacant and land is available. The controlled first-red case
is one funded host, one zero-budget guest, one guest able to fund half a door,
and a second actual vacant door. The sequential free-host pass splits its one
free place pro rata across the guests; the paid-vacancy pass then shelters the
fundable residue but strands half the poor household. HousingMarketCheck
exits 4 with four original assertions red (target/RES3-free-host-first-red.*).

★ Match free host places and self-funded vacant half doors in one flow, after
normal placements and same-kind five-adult sharing groups. Feasible shelter
takes priority over soft preference or which emergency pass happened first.
The existing solver retains its suitability, maximum-two-household shares,
budget checks and rotated equivalent ties. Temporary flow-node keys are
translated to actual comparable classes before a lease is retained; no new
class, saved field or quote is introduced. Original hosts keep their full
bills; guests pay only their actual funded vacant shares, and zero-budget
hosted guests owe zero. Physical shares and hosted/crowding reasons reconcile.

The positive case now shelters all three, records one hosted guest, 1.5 actual
occupied door equivalents and exactly 1.5 asks of receipts. The full existing
HousingMarketCheck passes (target/RES3-free-host-mend.*). A counter-case lowers
the second guest below half the ask and requires one explicitly unplaced
household, just one occupied home and no additional rent. The affected native
replay, checks, money/save delta review and another final ensemble remain owed.

The corrected native AUTO2-9 replay completes m2-70 with zero unplaced
households in all 69 observations (target/RES3-host-mend-native9.*), including
the old m47 failure. Its diagnostics are removed, both observed source files
match the reviewed source, and their timestamps force a clean recompilation.

The regular HousingMarketCheck passes with the unfunded counter-case too.
Compiled HousingDoorCheck and LongPlaytest contain no temporary diagnostic
branch. Paid half-door shares retain each cell's separate lease and can be
partitioned by seeker kind within aggregate vacant stock; the solver does not
merge their household counts. Existing same-kind five-adult groups still form
only in emergencySharing. No borrowing, host subsidy or new building ID is
created by a temporary flow node.

On this mend's played HousingDoorCheck window, the only red is again its FS11
capital/owners fixture premise: six orders would be capital-short, all six
behind quotes supporting no share issue, so none is offered to the owners.
The lending/quote rules and every assertion are unchanged. There are zero
unplaced households, 2,155 homes finish by size in the watched window, and all
899 monthly household cash identities close. The same FS11 row is restored
with this native cause; it had correctly been removed on the previous model
where the premise occurred. The pre-pin report is retained separately as
target/RES3-host-mend-HDC-beforepin.*.

The refreshed catalogue and profiles are from the regular check of ff8ea23be,
including the new unfunded control. All fourteen forms and thirty-two classes
remain reported in both eras, with the shipped Substation at 155 and residential
IDs through 166. Both populated native reloads and their next months pass.

| Scratch city | Integrated month | H/T/A/M/L |
| --- | --- | --- |
| Small, 203 people | 40.277 ms | 70/32/70/0/0 |
| Present-day catalogue | 48.869 ms | 84/32/352/0/0 |
| Present-day larger, 148,950 people | 47.710 ms | 82/32/341/0/0 |
| Historical catalogue | 38.512 ms | 62/32/190/0/0 |
| Historical larger, 149,168 people | 35.396 ms | 62/32/189/0/0 |

The larger cities still have 141 permanent household cells. This algorithmic
mend adds no saved state; the batch's already owed format/floor 64 stays 64.

## Emergency-match mend: completed checks and review handoff

Final model ff8ea23be passes HousingMarketCheck, HousingChoiceCheck,
HousingCheck, HouseholdCheck, HouseholdFlowCheck, BuildingDataCheck,
InvestCheck, WaterCheck, SubstationCheck, SaveFileCheck, ReadPathCheck,
DenominationCheck and LongPlaytest (exit 0), each run alone. HousingDoorCheck
exits 3 with only its restored FS11 quote-supported-capital premise.
ReloadCheck exits 3 with only main's HT1-H hand-order/material premise.
ScaleCheck exits 3 with only HT1-H's free-growth premise, still ten candidate
windows; every historical savings/deposit TRACK assertion passes. All native
cash, save, continuation, lending, placement and count comparisons are kept.

The full default playtest and its additional money controls pass: 2,404
audited months, no findings, every reload matched, ending "All checks passed."
The final --fast exits 0 (0 firm/199 soft). All final logs and batch metadata
are retained as target/RES3-host-mend-<harness>.log and .txt, with
target/RES3-host-mend-fast.txt. The pick against merged main ecbc3b975 is ALL,
247 harnesses; its review line remains "needed - saves: DataSave.java,
+SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT". The new money delta is the emergency
allocation itself; neither DataSave nor its format changes again in this mend.

No diagnostic source or compiled diagnostic branch remains. The refreshed
catalogue is the check's actual model report, not recalculated estimates.
The provisional marker stays while REV-RES3 reviews the delta after fd7071f54.
Another final-model ensemble against the newest baseline remains owed after
that go. The second ensemble's raw FAIL and land-funding evidence above remain
unaltered; no F1 assertion is waived by this handoff. No dry run has started.

## REV-RES3 attribution P1: native shipped-main counterfactual

REV-RES3 returned SHIP for fd7071f54..ab8656288's code, and rejected the
outside-cause attribution above. The presence of a cash-share hold alone
does not make a new RES3 shortage main's fault. That attribution is withdrawn:
neither seed 3 nor seed 14 is passed to FB11 or LP-G1 by name.

Native shipped 0.9.192, f937d2c22, replayed AUTO2 seeds 3 and 14 through m660,
observing every month m450-660. Its isolated worktree first passed pristine
--fast with an empty source diff. The only temporary edits were read-only
observations in HousingDoorCheck and LongPlaytest's existing ground-look trace;
founding, controller rhythm, cash-share budget and all model code stayed native.
Each case ran alone through the named harness with the shared Earth/world data.
Both main traces have zero unplaced households in all 422 observations. Their
population, index, rent and GDP match all 422 baseline CSV rows at the CSV's
nine-significant-digit precision, zero differences. The baseline snapshot
9c769adf3 differs from shipped f937d2c22 in source only by the version string.

The already reviewed final model ff8ea23be was then replayed with the same
observations: seeds 3, 14 and 9, each m450-660. All 633 observations have exactly
zero unplaced households. Thus the failing fd7071f54 snapshot's new shortages
belong to RES3; its later emergency-match mend already avoids those windows.
No further model change is made on the strength of a missing main failure.
These bounded replays do not establish later months or the final 80-run verdict.

Land per home below is actual standing residential template plot area divided
by its dwelling count, in square feet. Seeking is the model's current household
door demand, including explicit sharing; sites counts actual funded home doors.
Cash is the city's native cash in model thousands. Every displayed unplaced
count is zero, rather than a tolerated finding or a changed threshold.

| Seed | Model | Month | People | City cash | Homes / sites | Seeking | Plot ft2/home | Unplaced |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 3 | Main 0.9.192 | 450 | 24,511 | 9,870.534 | 6,504 / 269 | 6,719.764 | 1,414.668 | 0 |
| 3 | Reviewed RES3 | 450 | 6,914 | 13,909.152 | 1,476 / 30 | 2,239.281 | 4,382.114 | 0 |
| 3 | Main 0.9.192 | 609 | 33,642 | 48,050.688 | 9,635 / 0 | 9,614.121 | 1,211.313 | 0 |
| 3 | Reviewed RES3 | 609 | 8,718 | 38,512.648 | 2,050 / 108 | 2,873.854 | 4,414.634 | 0 |
| 3 | Main 0.9.192 | 660 | 36,499 | 3,018.449 | 10,458 / 0 | 10,458.000 | 1,193.632 | 0 |
| 3 | Reviewed RES3 | 660 | 10,379 | 107,061.061 | 2,182 / 90 | 3,383.050 | 4,329.056 | 0 |
| 14 | Main 0.9.192 | 450 | 5,897 | 10,170.548 | 1,014 / 160 | 1,708.487 | 5,642.012 | 0 |
| 14 | Reviewed RES3 | 450 | 4,979 | 687.426 | 1,081 / 66 | 1,559.864 | 4,725.254 | 0 |
| 14 | Main 0.9.192 | 634 | 25,055 | 86,170.114 | 7,025 / 63 | 7,025.000 | 1,625.907 | 0 |
| 14 | Reviewed RES3 | 634 | 7,021 | 8,513.868 | 1,413 / 0 | 2,286.432 | 4,326.964 | 0 |
| 14 | Main 0.9.192 | 660 | 25,686 | 150,685.552 | 7,340 / 0 | 7,271.894 | 1,597.003 | 0 |
| 14 | Reviewed RES3 | 660 | 7,295 | 32,263.430 | 1,413 / 0 | 2,366.155 | 4,326.964 | 0 |

The ground looks differ with the actual played paths. Main seed 3 looks at
m467/468 with budgets 2,182.526/2,510.346; reviewed RES3 looks at m463/464
with 1,900.179/2,095.244. Main seed 14 looks at m463/471 with
1,256.883/1,443.203; reviewed RES3 looks at m456/464 with zero cash-share
budget, and again at m586 with zero. Both may encounter exhausted land;
only the actual observed shelter outcome establishes the finding. RES3's
lower populations and different plot-per-home mix remain visible in the table.

Seed 9's reviewed replay also covers the baseline's m605 window: m600, 605,
609 and 660 all have zero unplaced households. At m605, 8,106 people seek
2,626.395 doors beside 1,502 homes and 6 doors on site, with 424.505 city cash.
The landlord is held for land, with 3,605 sq ft free, but nobody is unplaced.
The final ensemble must still read this month in its own seed-9 CSVs.

Evidence: target/RES3-main192-{3,14}.{log,txt},
target/RES3-final-compare-{3,14,9}.{log,txt},
target/RES3-main192-pristine-fast.txt and
target/RES3-counterfactual-records.json. The JSON retains every observed field,
including family/studio door shortfalls, latent head demand, city cash, land
and exact decisions; each raw log retains ground looks/offers/holds/purchases
and standing form counts. Both observed sources were restored byte-for-byte
afterward. Only the notes change in this attribution follow-up, so the next
step under the reviewer's go is the final-model ensemble against 0.9.192.

## Third ensemble: own land exhaustion and blueprint/order integration

RES3-host-final-20261123 ran b948e245e (model ff8ea23be) on gatebox against
0.9.192: 80/80 complete, none missing/short, raw FAIL 16/26, hard 8/10.
Every M1 passes with zero failed/unread audits. AUTO2 and ZERO_SLACK each have
two F1 runs versus baseline one: seed 8 first flags m618, seed 12 m927.
SHOCK and HIGH each have one and pass against baseline one; ZERO_HOT has none.
All sixteen AUTO2 cities take off, median m1,040 versus 747. Final medians
are 3,031,056 AUTO2, 1,634,084 ZERO_SLACK, 5,819 ZERO_HOT, 22,681 SHOCK and
8,995 HIGH, beside baseline 4,649,724 / 2,883,284 / 7,632 / 67,498 / 32,356.
Only ZERO_HOT passes the informational T2 bound. The raw FAIL stays unchanged
in target/ensemble/RES3-host-final-20261123; no outside-class pass is used.

The 633 native rows for seeds 3/14/9 match all four observed columns of this
ensemble at CSV precision, zero differences. Seed 9's m605 rows have audit=1,
findings_new=0, findings_distinct=0 and an empty finding in AUTO2, ZERO_SLACK,
SHOCK and HIGH; ZERO_HOT ends before m605. Exact values and rent/wage/real-rent
readings are retained in target/RES3-host-final-{readings.txt,rent-readings.json,
seed9-m605.json}. The third-run F1s are new RES3 failures, not an extension of
the withdrawn FB11 attribution.

Read-only native observations reproduced seeds 8 and 12 and saved full native
slot-10 checkpoints immediately before their first flagged presses. The
observed source is restored afterward. Loading each checkpoint and taking one
native player press reproduces the original population, index, rent, GDP and
unplaced count at CSV precision, even after the read-only candidate quotes.
The evidence is target/RES3-host-final-finding-{8,12}.*,
target/RES3-host-final-{8,12}-before-{save,history}.json,
target/RES3-host-final-{8,12}-bad-market.json and target/RES3-candidate-reads.*.
Two temporary observer mistakes (uncaught file I/O, then a missing scratch
save directory) were corrected; their failures are diagnostic setup, not
accepted model checks. No observer or checkpoint changes a model input.

At seed 8 m618, all 1,063 homes are fully doubled, 2.033377 households lack
shelter, there is no home pipeline and only 1,393 ft2 is free. House/terrace
stock consumes 5,354,000 ft2, 5,036.689 ft2 per home. At seed 12 m927, all
3,458 homes are fully doubled, 13.241186 households lack shelter and there is
no home pipeline. The remaining 7,279 ft2 fits a compact home, while the planner
holds for an 18,000-ft2 terrace. These are actual physical shortages, not
unfunded rents, and belong to RES3.

The checkpoint's candidate readings identify the blueprint trap. At seed 8,
unbuilt Low-Rise quotes 19.449 per project against a 34.778 operating build
hurdle; mixed mid-rise quotes 15.960 against 33.710. Established houses and
terraces quote roughly twice the neutral gross capital anchor. A new class
was initialized at that gross anchor and had no operating-cost quote because
it was absent from physical stock. It could not enter, while the established
large-plot forms kept winning. Seed 12 also shows a profitable compact offer
that fits the remaining land but loses to the preferred, non-fitting terrace.
Finally, the typed order replaced the full door backlog with income from one
project, capping its door-based quantity at one project when head-capacity
shortfall did not dominate.

★ The mend in progress quotes an unbuilt class's actual operation plus its
capital-return anchor, with the existing lease update speed. Actual standing
stock keeps its existing scarcity/glut and carry-floor rule. A blueprint cannot
inherit debt from another class. All form quotes are prepared, including
classes absent from physical stock; optional parking creates no lease receipts.
★ Profitable candidates that fit the actual land compete before a preferred
form that cannot fit. If none fits, the native land hold remains. Child-family
eligibility and actual budgets remain hard constraints.
★ Size the typed order from the compatible, affordable remaining demand and
the actual recipe's per-class unit counts, reserving standing homes and funded
sites once. The existing full-cost, build-margin, mortgage, quantity-halving,
owner-funding and actual construction gates remain in the path.

The controlled first-red HousingMarketCheck reports fourteen missing
operating quotes, a stranded compact alternative and a one-project order for
an eighty-household backlog (target/RES3-planner-first-red.*). Its child-family
and zero-budget controls already pass. The first mend fixed selection/sizing
but still missed unbuilt operating carry because it iterated physical stock;
that intermediate failure is retained as target/RES3-planner-stock-only-carry.log.
The corrected catalogue-wide cost preparation and native replays are being
checked. This model moves money/quotes/orders: fresh named checks, REV-RES3's
delta review and another final-model ensemble remain owed before any dry run.

The corrected HousingMarketCheck now passes (target/RES3-planner-mend.*).
The fractional-demand comparator is updated to the explicitly selected unbuilt
supply quote (capital anchor plus actual operating carry); it retains the same
exact near comparison and all zero-demand/phantom-scarcity assertions. The
operating-return test uses allocated home capital, preserving optional parking's
separate capital with no invented parking receipts. Neither a money/save
comparison nor a quantity/conservation bound changed. Both loaded checkpoint
controls reproduce the old failing press exactly in the four observed columns
and the unplaced count. Fresh from-founding seed-8 and seed-12 mend replays are
now running; their success is not assumed from the isolated fixtures.

The fresh model ae8aad170 removes both native shortages: seed 8 has exactly
zero unplaced households in all 151 observations m550-700, and seed 12 in
all 151 observations m850-1000. At the former m618 failure seed 8 now has
26,203 people, 7,688 homes and 184 funded doors on site; at m927 seed 12 has
12,254 people, 4,418 homes, no doors on site and zero unplaced. The differing
growth paths are retained rather than passed off as identical cities. Seed 12
is smaller than its old path, while shelter is ample; growth remains an
explicit informational comparison for the next ensemble.

The raw evidence is target/RES3-planner-native-{8,12}.{log,txt}. Both observed
source files are restored to their committed bytes and touched for compilation.
The model changes are committed at ae8aad170; its full named-check pass,
catalogue refresh and money-review handoff are in progress. No dry run has
started, no F1 exception is requested, and another 80-run ensemble is owed
after REV-RES3 reviews this actual model change.

The normal HousingDoorCheck is green on this model: 61 capital-short orders,
59 offered to owners whole and two behind unsupported quotes; 2,918 homes
finish in its watched window, no household is unplaced and all 899 monthly
household cash identities close. Its now-green FS11 pin is removed again.

InvestCheck exposed two stale premises, retained in
target/RES3-planner-InvestCheck-beforefix.*. Its land-negative case supplied
one foot less than the preferred plot, although the new compact alternative
could fit. It now supplies one foot less than the smallest known housing plot
and asserts no known form fits; the original refusal and land-reason assertions
remain. Its ten-house labour quote assumed a scarcity multiplier of one on a
played construction queue. The exact work identity now includes the model's
actual order multiplier, retaining the 1e-9 bound, wage-input checks and every
tax, materials and settlement identity. No money or save check is removed or
loosened; the resumed run must make both premises explicit.

InvestCheck now exits 0 with those same exact bounds and identities.
The model's latest catalogue/profile report is from HousingMarketCheck on
ae8aad170, with both populated native reloads and next-month twins green:

| Scratch city | Integrated month | H/T/A/M/L |
| --- | --- | --- |
| Small, 214 people | 48.813 ms | 63/32/124/0/0 |
| Present-day catalogue | 44.496 ms | 84/32/274/0/0 |
| Present-day larger, 148,943 people | 45.706 ms | 82/32/259/0/0 |
| Historical catalogue | 38.753 ms | 62/32/126/0/0 |
| Historical larger, 148,988 people | 37.317 ms | 62/32/135/0/0 |

Both larger cities retain 141 permanent household cells. The report still
lists all fourteen forms and thirty-two classes, their actual price/budget/
availability/work outcomes, and the shipped IDs after Substation 155.

CardCheck also selects BuildCardCheck by the runner's substring rule. The
credit-card harness itself has zero failures, including the exact saved
statistics and native continuation. The build-card harness finds three red
assertions in its existing loss-first Rail Spur case. Read-only observations
show a negative estimate, -28.13918112808416, and both LOSS and INTEREST
correctly failing. Its first failure is STAFFING: 29% now, 70.7393422% with a
year's arrivals, below the unchanged 80% bar. The standing railway already
covers all 1,590 tonnes traded with 49,792 tonnes of capacity; lack of trade
is not the missing premise. The fixture must cause enough labour to reach
its loss-first assertion. These three moved played-town premises pass by
name to queued FS12, the existing build/card sweep. Every original staffing,
loss, interest and first-failure assertion stays intact; no UI or production
code is changed for them. Evidence: target/RES3-planner-BuildCardCheck-observe.*
and the restored-source target/RES3-planner-CardCheck.*. The two diagnostic
prints also contain the word FAIL and are not accepted check lines. The
observed source is restored byte-for-byte and compiled class files are
checked to contain no RES3_CARD_DIAG remnants.

LongPlaytest completes its normal 2,400 months, all subsequent material and
household money fixtures passing. Its one raw finding is the map ring at
m2400: industry 2.43 km, homes' band 1.78 km, outer kinds 3.35 km. The
repository's existing LongPlaytest/CL7 record classifies that named finding;
the raw result remains visible. There is no shelter finding in this run.
This is not an exception to the pending monetary ensemble's M1/F1 verdict.

Main cdbe83dc0 is merged at 30dd6a7e0. The only main delta from ecbc3b975
is QUEUE.md's ownership record for FS5; no source, schema, catalogue IDs or
baseline changes. Format and load floor remain 64; the planner algorithm
adds no saved state. No new bump is owed by this delta. The batch's original
bump remains owed under the old-saves rule, with all real previous-format
pairs and intact-file/live-city refusal checks retained.

MortgageCheck's original branch-reload premise also moved: the short closure
window had zero paying customers while its homes and supporting works were
still being built. All origination, lender, insurance, capital, closure timing,
money and payout laws passed, but the nonzero customer assertion properly
failed. FS9 has shipped, so that is not a usable queued owner. This fixture
is mended through inputs: its existing landlordCity factory stands operating
works and three depots, standLandlords stands fifty actual homes, and the
town is given exactly seven branches before the six past the charter close.
Two additional assertions require that starting count. The original six-at-once,
first-cost-known, demolition, capital, bank-equity, money and exact reload
comparisons remain unchanged. A trial with the full standing factory and six
additional branches correctly failed: it had eight and closed seven. Standing
homes alone still had no paying customers. Both intermediate failures are
retained, not accepted checks. The final fixture passes with 84.023846119
actual fee-paying customers and later-branch cost 6.411122386 restored exactly.
No bank/customer field is stubbed, no fee is invented, no closure limit or
save tolerance is loosened, and no production code changes for this fixture.
Evidence: target/RES3-planner-MortgageCheck-beforefix.*,
target/RES3-planner-MortgageCheck-standing-works.*,
target/RES3-planner-MortgageCheck-homes-only.* and the final
target/RES3-planner-MortgageCheck.*.

## Shipped PW1-W integration before the new money review

Main b34277adc shipped 0.9.193 while the final fixture checks were running.
It is merged at 17abb80ab. All source merges were clean; the generated map,
dial, harness and month-order conflicts were resolved by regeneration from
the combined compiled source. --fast exits 0, zero firm/199 soft Stale findings
(target/RES3-PW1W-fast.txt). The merge retains typed rent collection, the
unbilled-homes draw of zero, IDs 156-166 after Substation 155, nextId 167 and
format/floor 64. VERSION 0.9.193 is inherited from the publisher, not edited.
PW1-W's new pending-water record and internal water/power transfer coexist
with the saved class market; its billed utility revenue stays the real payers'
bills. No new RES3 saved field is added in this follow-up.

PW1-W-notes section 5 retains all eight default trace CSVs byte-identical to
native main 0.9.192, so it publishes no newer ensemble baseline. The newest
baseline remains docs/ensemble/0.9.192.md. The new RES3 model still owes its
own final 80-run ensemble after REV-RES3's delta review; the water integration
does not excuse it or the new seeds' F1 findings.

The merged HousingMarketCheck passes, including the controlled operating-quote,
fit and backlog first-red cases, all original conservation/budget controls,
both populated native reloads and next-month twins. The catalogue report is
regenerated from this merged model. Its populations, class counts, assignments
and cohorts are the previous report's; the measured integrated months are:

| Scratch city | Integrated month | H/T/A/M/L |
| --- | --- | --- |
| Small, 214 people | 44.137 ms | 63/32/124/0/0 |
| Present-day catalogue | 38.053 ms | 84/32/274/0/0 |
| Present-day larger, 148,943 people | 48.567 ms | 82/32/259/0/0 |
| Historical catalogue | 39.702 ms | 62/32/126/0/0 |
| Historical larger, 148,988 people | 32.851 ms | 62/32/135/0/0 |

Both larger cities retain 141 permanent household cells. The combined model's
utility, native save/rebuild and banking checks finish one named call at a time:

| Scope | Checks | Result |
| --- | --- | --- |
| Planner ae8aad170, with the fixture mends above | HousingChoiceCheck, HousingCheck, HousingDoorCheck, HouseholdCheck, HouseholdFlowCheck, BuildingDataCheck, InvestCheck, WaterCheck, SubstationCheck, SaveFileCheck, ReadPathCheck, ReloadCheck, DenominationCheck, CardCheck, MortgageCheck | All pass; exact comparisons and all money/conservation checks retained |
| Same planner | ScaleCheck | 3, one HT1-H free-growth premise; six windows, all evaluated TRACK/count/money laws pass |
| Same planner | LongPlaytest | 3, the one inherited CL7 map-ring line at m2400; all money fixtures pass, no shelter finding |
| Combined 17abb80ab | HousingMarketCheck, WaterPowerCheck, WaterCheck, SubstationCheck, SaveFileCheck, ReadPathCheck, ReloadCheck, MortgageCheck | 0 each; WaterPowerCheck 41/41 |
| Combined 17abb80ab | CardCheck selection | 3 only for the three BuildCardCheck/FS12 staffing premises; CardCheck itself has zero failures and exact native continuation passes |
| Combined 17abb80ab | --fast | 0, zero firm/199 soft Stale findings |

The combined reload compares 821,235 history numbers in 336 series after the
autosave, then 185,156 save numbers exactly. Its log also retains an actual
post-success warning: the Earth scratch city's asynchronous autosave history
rename fails after "All checks passed." The tested month-1229 autosave/history
and all continuation comparisons completed successfully before this message.
ReloadCheck.main calls System.exit without draining all Earth/twin saves;
TempDirs installs its deletion hook at shutdown. This ordering is consistent
with a scratch teardown race, not proof of an exclusive cause. A harness
lifecycle follow-up should drain its pending saves before exit. The warning
is disclosed for the saves review; no save comparison is removed, no failure
line is pinned for it, and no production save workaround is introduced.
Raw evidence remains target/RES3-PW1W-ReloadCheck.{log,txt}.

The pre-integration check evidence is target/RES3-planner-<name>.{log,txt}
(HousingMarketCheck is target/RES3-planner-mend.*); the final integration
evidence is target/RES3-PW1W-<name>.{log,txt}. All temporary observations are
absent from source and compiled classes. Each named invocation uses the shared
Earth/world files, cbs.parallel=1 and scratch/TempDirs city files. No full
local pick or save slot in the player's data directory is used.

tools/pick.sh origin/main HEAD selects ALL 248. Its review line is
"needed - saves: DataSave.java, +SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT".
The changed quotes/orders also owe a stranger's money review. The provisional
ship record retains only the diagnosed ScaleCheck/HT1-H and three
BuildCardCheck/FS12 rows; HousingDoorCheck's FS11 row is removed because its
opportunity, owner funding and 899 cash identities are green. The ship file
passes tools/known-reds.sh. No F1 exception or outside attribution is requested
for the new seeds: seeds 8 and 12 remain RES3's own mended findings.

This checkpoint goes back to REV-RES3 before another ensemble, as ordered
when the model changes. The new final 80 runs against docs/ensemble/0.9.192.md
remain owed on the reviewed combined model, including seed 9 at m605 and all
new F1 seeds, with rents, affordability and growth reported. All three earlier
raw FAIL verdicts remain recorded above. No dry run has started, no kept
dry-run directory is claimed, and the provisional readiness marker remains.

## REV-RES3 SHIP and the three P2 folds

REV-RES3 returns SHIP on b948e245e..ae903ee4b, no P1: the backlog preserves
the funding gates and creates no money, the fixture conditions are caused,
the FS12/CL7 premises and main's teardown lifecycle are accepted, and the
PW1-W merge holds. Its three P2s are folded without moving the model:

- InvestCheck's wage/work identity no longer reads orderPriceMultiplier as
  its expected multiplier. A separate fresh city has no queued site or
  priority work, the same explicit wage ladder and tax rates, and either no
  builder month or enough sites' output that all ten Houses stay below
  BuildersShort.KNEE_MONTHS without foreign crews. The expected premium is
  exactly one. Every existing tax, material and settlement identity and its
  strict bound remains; the played city's later billing/save cases remain.
- HousingMarketCheck retains the eighty-door lower bound, requires
  quantityNeeded to be the smallest whole six-door recipe covering eighty,
  and requires the actual plan quantity to equal it: fourteen projects,
  eighty-four doors. The smaller head-based gap cannot inflate the order.
- Main 86359b9d4 is merged at f33363857. Only QUEUE.md changes, explicitly
  naming the Rail Spur loss-first staffing case under queued FS12. The three
  ship-file rows keep that owner.

InvestCheck and HousingMarketCheck both exit 0; --fast exits 0 with zero
firm/199 soft findings. Evidence: target/RES3-P2-{InvestCheck,
HousingMarketCheck}.{log,txt}, target/RES3-P2-fast.txt. The refreshed catalogue
has the same populations, mix, cohorts and 141 permanent cells. Its measured
months are small 49.316 ms, present-day 45.067 ms, present-day larger 46.302 ms,
historical 36.216 ms and historical larger 37.092 ms. No additional money/save
review is owed under the explicit P2 go: only the harnesses, reports and main's
ownership record move. The final-model ensemble runs next against 0.9.192.

The final run is RES3-planner-final-20261123, pushed snapshot 2d9a79aea,
against docs/ensemble/0.9.192.md with no extra JAVA_OPTS. Gatebox accepts it
at /home/jerus/cbs/tools/2d9a79aeafeb004bdad67b532fdf4a929b1ec1a2/target/ensemble/RES3-planner-final-20261123,
queued behind FC2's dry run under the runner's gate-first rule. Its controller
log is target/RES3-planner-final-ensemble.txt. No laptop fallback is used.

## Final ensemble: all M1/F1 criteria green

RES3-planner-final-20261123 finishes 80/80, no missing or short run, on
the pushed 2d9a79aea snapshot (0.9.193), gatebox, OpenJDK 21.0.13, eight
jobs. It exits 0: PASS, hard 10/10, total 19/25 with one vacuous and one
retired criterion excluded. Beside it 0.9.192's recorded raw score is 20/26,
hard 6/10. Every policy has zero failed/unread audits and zero findings in
all sixteen runs. Pairing is 64/64. No known-class exception is used for a
single run. The earlier three raw FAIL ensembles remain unchanged above.

The complete baseline is committed as [RES3](../ensemble/RES3.md), with
publisher version placeholders only in its heading and model introductory version, and the raw score,
summary JSON and provenance preserved. The residential comparison is appended
there beside the scored results: final populations, last-120-month real paid
rents, the paid-rent/average-worker-wage comparison, and the exact seed-9
month-605 rows. The ratio is descriptive, not a household disposable-budget
share; actual eligibility remains the matching checks' hard constraint.

Late real rents are higher than baseline in every policy. Paid rent divided
by worker wage moves AUTO2 8.44% -> 9.07%, ZERO_SLACK 10.08% -> 10.29%,
ZERO_HOT 9.97% -> 12.39%, SHOCK 8.86% -> 11.25%, HIGH 9.67% -> 12.54%.
Final population medians are 4,431,240.5 / 2,188,964 / 5,805.5 / 30,798.5 /
15,385.5 against 4,649,724.5 / 2,883,284.5 / 7,632.5 / 67,498 / 32,356.
T2 passes for AUTO2, ZERO_SLACK and ZERO_HOT, and fails for SHOCK and HIGH.
T1 still passes its stated bound, but only 15/16 AUTO2 cities reach 40,000,
median m977 against baseline 16/16 and m747. Seed 4 ends at 32,259, far
below baseline's smallest AUTO2 city, 2,459,618. That growth outlier belongs
in the comparison and is not attributed to an outside class.

The other changed informational verdicts remain explicit: AUTO2 A1 fails
(2.89% median inflation, eleven of sixteen in band); HIGH R2 fails (0.47
unemployment points versus its 0.50 bound); HIGH R3 fails (three seeds above
one inflation point); ZERO_HOT H2 is vacuous because there is no burst to
stop, with the scorer retaining that the numbers alone would fail.
★ Keep the reviewed costs, budget eligibility and real funding rules, and
publish these informational rent/growth outcomes under contract section 4.
The comparison covers the full RES3 catalogue and match; it does not isolate
one price-rule change. No calibration or model change follows this run.

At seed 9 m605, AUTO2 is 17,841 people, mean paid rent 0.164557612 and
average worker wage 1.358492740, with audit=1 and new/distinct findings=0/0.
The baseline witness has 21,605, 0.155366776, 1.089396160 and a homelessness
finding. ZERO_SLACK, SHOCK and HIGH also have passing audits and empty
findings at m605; ZERO_HOT ends at m402. Seeds 3, 14, 8 and 12 finish with
no findings in any policy. All 302 rows of the two prior native AUTO2
replays (8 m550-700, 12 m850-1000) match the final ensemble's population,
index, rent and GDP at CSV precision, zero differences.

Evidence: target/ensemble/RES3-planner-final-20261123/{score.md,baseline.md,
summary.json,snapshot.txt,progress.txt}; target/RES3-planner-final-ensemble.txt;
target/RES3-planner-final-{readings.txt,rent-readings.json,seed9-m605.json}.
The gatebox run path above is an ensemble, not a dry-run reuse directory.
Main is still 86359b9d4 after the final fetch. The model remains 2d9a79aea;
only the baseline, notes and ship record follow it. Pick ALL 248 still owes
the dry run, which is requested from the orchestrator after these records
are pushed. No dry run has started; the provisional marker stays until the
explicit go and a green kept dry run.

## First ALL dry run: stopped, slot released

The orchestrator grants the dry run at fde7db7b9. Main remains
86359b9d448e95fe1a5632a16d075c3f1d0b14d2, so no further merge precedes it.
The gatebox run is /home/jerus/cbs/runs/cbs-gate-RES3-xIpD5dJW, copied to
C:/Users/Jerus/AppData/Local/Temp/cbs-gate-RES3-xIpD5dJW. These are retained
red-run evidence, not a reuse claim. Branch commit:
fde7db7b9411feb6dfa1832f240dc8f3db595d4e. Local controller log:
target/RES3-dry.txt. No process or command is run inside the kept clone.

The gate exits 1, 2026-11-23 12:06:09 UTC: 247 harnesses ran in 1,289 s,
204 passed/43 failed, 38 harnesses with 182 unnamed lines (including exits
and summaries; some harnesses have both known and new lines). Stale does not
decide this run; the suite stops first. The slot is released by bus at
08:10:42 Halifax. The provisional marker remains; no ready message is sent.
The nine-minute foreground watcher intervals expired normally while the gate
continued; the controller and gate both finish, no process is abandoned.

The initial trace distinguishes owned data changes from unclassified laws:

- EquipmentCheck's old House is now billed to RE, so it cannot be its
  unbilled-building example. A city's Gravel Road has positive draw and no
  utility invoice; the unchanged zero-cost/zero-steam assertions use it, with
  the unbilled premise asserted explicitly.
- ResearchCheck's four newly dated tower rows are the catalogue's 1931/1969
  dates. All fourteen residential rows must retain their declared form year;
  every other non-code/non-special row retains the old undated/1900 rule.
  DiscoveryCheck and ResearchViewCheck use the actually undated Gravel Road
  for the original undated case; the year-dated Quarry and all known/from-
  research/unknown/origin assertions remain. These are being checked alone,
  not passed to a sweep.
- A diagnostic-only AutoBuildCheck run isolates section 2, changing no model
  input. Its native callback at m37 reads 3,112 people, annual GDP
  2,925.739475548268, cash 57,227.47701054205 and debt ratio 0. Full builder
  output is 302 and site output is 0. Paved Road, Walk-in Clinic and Childcare
  Centre are all HELD with cut BUILDERS. The helper's WG2 comment records
  five depots calibrated at 3,902 people/output 418, just past repairs. Its
  original full 600-month operational/save laws pass and place 28 orders;
  this trace does not establish all 38 short-fixture failures' causes.
  Evidence: target/RES3-dry-AutoBuild-trace.{log,txt}. The observation is
  restored byte-for-byte before accepted checks; its scoped failure is a
  diagnostic, not a green full-harness result.
- LandmassCheck's total builder points do not establish the demolition's
  own share: it receives 15,754 total points against 7,778 initially owed
  without finishing in 129 months. Allocation and competition still need
  tracing. The same warning applies to other work/pending-order premises.
- Currency and other money comparisons remain unclassified. Their original
  laws and tolerances are kept; neither a sweep name nor a different played
  city is an attribution by itself.

The orchestrator supplies FC2's red run, e90eb0b93,
C:/Users/Jerus/AppData/Local/Temp/cbs-gate-FC2-0uEcU2DO, and asks whether
the currency cases share a premise. Fourteen red harnesses overlap:
BankCheck, CallCheck, CarCheck, CentralBankCheck, CurrencyCheck,
DeathRecordCheck, DeskCheck, EssentialWorksCheck, FundCheck, FundLedgerCheck,
GoldStandardCheck, OrderDoorCheck, RailCheck and ShortSellingCheck.
GoldStandardCheck explicitly fails its no-top-of-month-M0-move premise in
both: RES3 has edge owed 34.18; FC2 has gold due 31.66 with zero reserves,
and its mid-cover premise already fails. RES3's before-top cover cannot be
used for its struck-rate equality; FC2 additionally loses its Gold fixture.
These are related fixture/phase assumptions, not an exclusive common cause
or a waiver. CurrencyCheck's cumulative defence comparison fails in both
with different figures and still needs its actual-flow trace. DeskCheck's
premises differ (unchanged spare for paper here, fixed-fee coverage there).
The overlap and evidence are mailed to the orchestrator at 08:25:29, with
the recommendation that FC2 owns common money-fixture mends to avoid two
builders editing them together. RES3 leaves those fourteen files untouched
pending coordination while its own catalogue checks proceed.

No production model, money/save check, threshold or ensemble score is
changed in this trace. The final monetary ensemble remains the passing
2d9a79aea model; another dry run requires a new slot go after every unnamed
line is mended or honestly named with its queued owner and cause.

The four catalogue/utility assumption repairs all pass independently:
EquipmentCheck, ResearchCheck, DiscoveryCheck and ResearchViewCheck, each
exit 0. The first compile caught a remaining local `house` reference in
ResearchViewCheck's separate lab example; that example still tests House,
and the undated-origin example now tests Gravel Road. No threshold changed.

The ChildcareCheck.town trace is now a native-main comparison, not a guess
about PW5. Native 0.9.193 at 86359b9d4, with the identical observer and
36-month fixture, ends at m37 with 4,016 people, 2,415 workers, construction
fill 0.7279804650063673, full output 431 and site output 87. RES3 ends with
3,112 people, 1,865 workers, fill 0.5029633156194911, full output 302 and
site output 0. Both have exactly 344.05000000000007 repair points, including
7.5 for the same 900 Houses; the entire standing-stock lists match and
neither has a Substation. Road ratio is 0.35 in both; health work ratios
are 0.7833219508408702 and 0.7934365755180257 respectively. RES3's housing
choice changes the people and staffed crews; PW5 has not added a repair
load to this fixture. Raw diagnostics are
`target/RES3-native-builder-trace.{txt,log}` and
`target/RES3-dry-Childcare-trace.{txt,log}`. These are one-fixture observer
runs, not full ChildcareCheck passes. The native scratch worktree was
restored, checked clean and removed after its process ended.

★ Cause this fixture's intended positive site output with physical inputs:
start with its five depots, add paid standing depots while the actual
staffed output leaves nothing after repairs, play a labour month after
each addition, and fail the fixture if twelve additions cannot cause it.
Do not repick a fixed count or change the service, funding or payback laws.
This harness-only mend is awaiting the full named checks below; it does
not change the final ensemble's production model.

The LandmassCheck observer reproduces both demolition reds. The standing
terminal owes 7,777.8 points; at its old 129-month ceiling the fixture has
counted 15,754 whole-city site points, but the demolition has received only
7,666.487358457717. Its queue now also includes C04, Compact House,
Duplex, other homes and business sites. For the shell, the initial owed is
47.75, and the second month counts 99 site points while a new C04 site gets
92.99986942494814 and the demolition gets only 6.000130575051852. This is
the endogenous orders breaking the wait's assumed bound on later work;
the demolition does advance, and the ground-release rule has not yet
fired. Raw: `target/RES3-dry-Landmass-trace.{txt,log}`. This observer run is
diagnostic, not a green check. The queued mend is to cause a fixed queue
for these three demolition-only cities by holding private sectors and
bank branches before their first month; the deadline and the exact ground
and save assertions stay intact. Other LandmassCheck growth runs remain
live.

The diagnostic Landmass run was stopped after the section-6 causes were
captured, during section 13's separate 1,200-month full-file growth run:
only its verified own child PID 14176 (parent 35324, LandmassCheck) was
ended. Its exit -1 is a cancelled diagnostic, not a third model failure.
The observers were restored before the repaired named checks.

PriceLevelCheck's three new lines have a concrete same-level trace. Its
eight built-city children, six months each, all complete. At history index
3, the factor's unnudged child records sick rate .1833 and unhoused 26.62;
the other seven record .1825 and 26.09, including all three children at
the same factor nudged by +1e-12, -1e-12 and +1e-13. Every child has the
same live population vector `[0,92,92,92,92,92,91]`. The unnudged child
also rejoins the other seven's history readings at the next index. This
is not a split confined to different price levels. `parting()` explicitly
skips every `h.*` series, so it reports the later live split at month 5
and compares a history already parted by rounding. Raw eight-child
readings: `target/RES3-dry-price-trace.{txt,log}`. The temporary observer
is removed. Keep the invariant and its tolerances; name these three exact
history lines under the existing HT1-H rounding/onset sweep, with this
same-level evidence. The orchestrator gets the new case in the mends list.

Two more data assumptions are mended through their inputs, awaiting named
checks. PolicyOpenCheck moved the first textual `month` in a save; RES3's
earlier housing-market state has its own `month`, before DataSave's root
month, so the city stayed at its original date with no load failure. Set
DataSave.month through its setter before serializing, retaining the exact
loaded-month and policy assertions. TruckRouteCheck's supposedly empty
three-month founding imports 30 tonnes for new private construction. Hold
private sectors and bank branches before those months to cause the zero
bulk case; retain the exact zero bulk and three zero tally laws.

ZoningCheck likewise removed only the building named House, leaving the
other typed homes available to the displaced households, so its outside
count was zero and its unzoned-farm shortage premise was absent. Buy out
and demolish at least nine in ten buildings of every available residential
form (asserting each actual purchase), hold replacement home orders for
that month, and retain the positive-outside, staffing, farm-zone and tiny
zone refusal assertions. No production rule or funding gate changes.

The first full AutoBuildCheck after causing positive builder output places
its first advice card and passes the decades/reload laws, but leaves 19
failed assertions. The subsequent trace causes the remaining premises:

- The school town has 172 elementary pupils, below
  `FIRST_WORK_SHARE * 600 = 180`, so no first school is warranted. In the
  school-only cases, set the child cohort so demand is halfway between the
  first-work threshold and half the smallest school's places, retaining
  the old below-half-school condition. Restore the named cohort array,
  population and workforce through their existing model inputs. Assert
  the warranted demand explicitly; do not change its threshold, payback,
  funding or first-school assertions.
- A bond for two years of GDP yields only 202-203% debt, below the 240%
  default limit. Size the fixture's funding-page bond from twice
  `DEFAULT_DEBT_LIMIT` years of GDP and assert that it actually crosses the
  limit. The zero-limit/central-bank case separately causes a real advance
  by playing a month overdrawn and asserts that advance exists.
- The three-month default young town has no output. Give it the standard
  forty Houses and three Convenience Stores before its months; its actual
  recorded GDP is now 105,123 against screens' annualised 420,492, and the
  unchanged strict inequality and national-accounts identity both pass.

With those inputs, the full AutoBuildCheck suite passes every assertion.
`target/RES3-dry-mend-AutoBuildCheck.txt` reports suite OK; the final serial
driver records its exit after maps/Stale. The earlier 19-red run is a
failed fixture iteration, not a waived result. HydroCheck, IslandLinkCheck,
LandmassCheck, ChildcareCheck and AuctionCheck follow sequentially.

AutoBuildCheck, HydroCheck and IslandLinkCheck finish exit 0 on the repaired
town. IslandLinkCheck's generated-world case now places 16 orders, meets
ground shortage in 142 of its 240 months, and still orders no link, bridge
or off-home port; the Earth link and both kinds of crossings pass too.

The first frozen-queue LandmassCheck iteration clears the shell but fails
the standing terminal's fixture: no repair headroom is caused in this tiny
town, and it stalls 24 months after its first 437 site points. Do not lift
the stall or work bounds. Prepare its crews before the three demolition
scenarios: leave homes and construction available while adding paid
standing depots and playing actual labour months, with linked home ground
to house the workers; hold other sectors and bank branches. Stop only when
staffed site output exceeds the repairs of all three future terminals,
then freeze every private sector. The initial wait includes any work
already queued during this preparation, and no later private orders can
invalidate its bound. Other landmass growth cases still invest normally.

The focused section-6 diagnostic passes all three cases: the standing
terminal finishes in 19 months (9,096 site points against 16,102 initially
owed), the lone terminal in 17 (16,123 against 16,102), and the shell in six
(4,023 against 8,197). Every exact release, island placement and on-site
save assertion in the section passes. Raw
`target/RES3-demolition-prep.{txt,log}`; its temporary main shortcut is
removed. These are diagnostic results, not a substitute for the full
LandmassCheck now running in the serial final harness driver.

The other fixture mends awaiting that driver are explicit about the actual
catalogue and costs. CreditCrunchCheck trims all residential forms, larger
mixes first, retaining its exact MARGIN bound. BuildersFirstCheck funds the
private top-up's complete `doorPriceOf`, including ground, starting from
nonnegative cash, and asserts the private order was actually placed.
ForestryCheck holds unrelated private orders before the hand clearing so
new home construction cannot add salvage during its one-month accounting
comparison. TreasuryCheck retains its failed note fixture, prints the two
surpluses that size its notes, and stops that dependent section if a note
is absent instead of dereferencing null; it does not report a passed
rollover or netting law on missing paper. CurrencyRegimeCheck and
MonetaryCheck add read-only phase/rent reports beside their unchanged
assertions to trace the remaining played-city premises.

The remaining map/page/rail fixture lines are named individually, not as
suite waivers. PlanCheck's contrasting reuse geometry now retains 74 of
74 boxes with both algorithms, so the strict advantage condition is
absent (queued FS6). KeptCheck's additional three NEW lines are in the
queued FS5 sections: its held-rule holding set grows from 62 to 162 with
100 other building lots; its literal research reach sentence is false
while the preceding full card-to-model equality passes; and its effect
fixture has zero moved rows after the held month against three while
dragged, while the held thumb, node and text laws pass. The fund page's
other retention line and the 1,796.5 MiB heap line were already named on
main, not NEW here: `unnamed.txt` contains just those three new assertions.
The kept dry logs remain the authority.

HomeRailCheck's boundary uses 175,253 of 199,167 tonnes, leaving a city
share .898104529914984 for 26,626 tonnes, so it has not caused saturation.
Its interior fixture leaves 91,376 tonnes after boundary traffic, enough
for all 58,561 city tonnes, so share 1 is correct and the strict interior
premise (and its dependent check) is absent. HT1-H already names that
guarded played-city search; the mends list includes the saturation case
too. RailDepotCheck's sizing snapshot has 97,209 bulk tonnes and does not
meet its more-than-two-spurs premise; the computed count is zero, so the
subsequent zero dealing at zero siding capacity is correct. FS7 shipped;
the mends list asks that these two lines join HT1-H's played-city cases.
The ship rows scope each literal condition; no capacity, money, save or
retention comparison is weakened.

The full LandmassCheck now passes, including the prepared fixed-queue
demolitions, their saved on-site units and exact ground releases, every
live investment/map case, and the separate 1,200-month Earth run and its
audit. `target/RES3-dry-final-LandmassCheck.{txt,log}` reports suite OK.
No diagnostic shortcut remains in the source. The full check retains
`STAND_WORK = 2` and `STALL_MONTHS = 24` unchanged.

The first-dry counts distinguish the suite from its uncovered list: 38
harnesses are NEW against main, two already scoped by RES3's existing
ScaleCheck/BuildCardCheck rows. `unnamed.txt` therefore names 36 uncovered
harnesses, of which 14 are the agreed FC2 files. The disposition helper
`target/RES3-dry-disposition.py` reports those 36; the mends list also
includes the two previously named ones.

The serial pass finds PolicyOpenCheck and TruckRouteCheck green. The
follow-up fixes remain inputs/measurements, with their hard laws kept:

- AuctionCheck's repeated-ask bound called `AutoSale.asked` cash, but
  `noteSale()` supplies the offered face there. Compare actual net proceeds
  with the repriced cash gap and count of lots, and principal growth with
  actual face sold (the old bound used face offered), as the adjoining
  conservation assertions do. The first retry's receipt shows offered and
  sold face 168.276586140 against cash gap 74.9227378377 plus one lot
  84.1382930699; the cash comparison must account for the issue by reading
  net proceeds, not by lifting its allowance. Print each component. The separate
  already-over-limit case now sets the zero limit, so its existing funded
  paper actually exceeds it instead of assuming it exceeds 10% of GDP.
- BuildersFirstCheck observes `getPointsBuilt()`, reset at each actual
  advance and including the foreign crews. The previous post-month
  `getBuildingOutput()` is next month's local capacity, not the work that
  just built 28 homes. This keeps the every-month positive-work law.
- ZoningCheck restores the prior home-investment hold before its later
  save comparison. ForestryCheck reapplies the same unsaved harness holds
  to its loaded twin. Their new controls had otherwise differed between
  the live and loaded cities; keep the exact cash/stock comparisons.
- CreditCrunchCheck reaches room 13.4 after retiring 309 actual homes.
  The original one-house upper bound added one household of room, though
  its trimming loop removes two household slots per door. Measure the
  smallest actual positive room reduction from a single-door retirement
  and require the remaining room above MARGIN to be smaller than that
  quantum. Keep MARGIN, the all-form trimming inputs, every leverage,
  mortgage, money and unplaced-household assertion unchanged.
- TreasuryCheck's forty startup homes make the second annual surplus
  positive (180.25) while the first is still zero. Step the three producing
  twins together until a positive trailing-year surplus is actually
  caused, bounded by another year, assert it, and only then size the first
  note. Do not manufacture a surplus or silently skip a failed note case.

CurrencyRegimeCheck's read-only reports reproduce all six new lines.
After the first month the gap is .0641459260050 against BREAK_GAP .10;
hazard is still .001 and the held peg has hot stock 2171.03138642, so its
larger-gap predicate is absent. The purchase-save fixture at m40 has gap
-.0763558615137, edge owed 450.353060385 and held=true, but doubt=0. The
hot-money save at m40 has gap -.0505131416061, edge owed 296.720373641,
stock 2323.19882352 and 2.32552434787 leaving on the previous .001 doubt,
but no doubt for the next month. Its loaded twin reports the same negative
gap, zero doubt, edge and stock. The subsequent stay/leave contrast and
positive-departure assertion require a positive source doubt that the
save never held. These six exact fixture/phase lines join the proposed
HT1-H scope in the mends list (FS10 shipped). No FX rule or save equality
changes; the source reports and assertion lines are in
`target/RES3-dry-final-CurrencyRegimeCheck.{txt,log}`.

The later full checks finish green for BuildersFirstCheck, ZoningCheck,
ForestryCheck, TruckRouteCheck, PolicyOpenCheck and TreasuryCheck.
Forestry's exact continuation now has city cash 30544.948813 on both
twins, identical cut/forest/sector cash, and its money residual is
1.63e-14 of what moved. Treasury's producing twins wait for the actual
positive annual surplus before issuing their two notes; all note,
netting, rollover, currency and conservation assertions pass. Raw final
results are the `RES3-dry-retry-*` files, except the already-green
PolicyOpenCheck and TruckRouteCheck in `RES3-dry-final-*`.

CreditCrunchCheck's measured one-door quantum is initially absent
(`Infinity`): the played town has only multi-home forms to retire. Stand
one paid House at the trimming input, then measure its actual room
reduction. This causes the stated one-house condition rather than assuming
the catalogue still contains an old House. The unchanged bank and
unplaced-household laws already pass; the corrected fixture is rerunning.

The last AuctionCheck receipt confirms the unit correction: net proceeds
139.295743191 fit the repriced cash gap 74.9227378377 plus one lot
84.1382930699; offered and sold face are 168.276586140, exactly the
principal increase. The cash bound has not been raised. Its full audited
run is still completing, not yet reported green here.

Native main's MonetaryCheck control at 86359b9d4 is green (exit 0), using
the same JDK/data and only the read-only rent/component report additions
to its harness. No production file differed. Its 3/10/20/40 percent rows
have inflation 11.44/10.10/9.23/9.21 percent, a 2.231-point reduction, and
calendar-year timing noise .044/.025/.110 points, within .650. Family
rent per capacity at 3 percent goes .0112442786132 -> .0174763533338;
at 40 percent .0113796763528 -> .0158280937670. Rent component growth is
14.4974 versus 11.9776 percent a year, and groceries/services also slow
with the dial. Its fixed D$ is .6991; each model pins its own common
pre-treatment founding rate, so this is a native-model control, not a
claim of identical cross-model opening states. Raw
`target/RES3-native-policy.{txt,log}`. The scratch worktree was restored,
checked clean and removed after its process ended.

RES3's three monetary-curve lines are therefore an owned finding pending
diagnosis, not attributed to main and not entered as known reds. Its
3/10/20/40 percent rows are 6.46/8.64/9.16/8.15, with calendar-year timing
noise .308/1.173/2.269 points. Business borrowing still falls (1,473 at
3 percent versus 768 at 10), but higher-rate rent growth and other
feedbacks can oppose that channel. The expanded component report is
running on RES3 too. No rent, price-index, credit or macro calibration is
changed to make the curve pass.

AuctionCheck now finishes exit 0, and CreditCrunchCheck finishes exit 0.
The latter has a real paid single-door input, retires 310 homes across
the catalogue, leaves room 13.4 above MARGIN 12, and measures the actual
one-door quantum as 2.00000000 households. Its leverage, mortgage, money
and no-more-than-one-unplaced-household checks pass. Final raw logs:
`target/RES3-dry-last-AuctionCheck.{txt,log}` and
`target/RES3-dry-last-CreditCrunchCheck.{txt,log}`.

The monetary component report localises the counter-channel: at 3 versus
10 percent, groceries slow from 10.6249 to 3.00265 percent a year, while
the rent component accelerates from 5.00121 to 10.6751 percent. Rent has
71.83-72.11 percent of the basket. Thus the borrowing/goods response is
present, but the rent component outweighs it in this fixed-FX toy. This
is an owned observed outcome, not a waived conservation finding.

Two predeclared input controls are being tried in a temporary curve-only
diagnostic, without changing any rate, direction, minimum-effect or noise
assertion: the model's wealthy founding cash (to remove fiscal borrowing),
and the same cash plus one paid standing specimen of each currently known
residential form, with subsequent private housing orders held (to remove
the transitional supply mix). Both retain actual matching, class rent
updates, utility/repair bills and every quoted/funded purchase. The first
control has zero public principal and advances at every reported close
but reproduces the original curve exactly: 6.46/8.64/9.16/8.15. Fiscal
borrowing is not the explanation. The stocked control is still running.
Raw outputs are `target/RES3-policy-wealthy.{txt,log}` and
`target/RES3-policy-stocked.{txt,log}`. This diagnostic has not been
adopted as a final fixture, and its temporary shortcut will be removed.

Neither diagnostic is adopted. The wealthy control reproduces all three
original curve failures with no public debt. The stocked control restores
direction (-2.50/-2.44/-2.21/-5.35 percent inflation) but fails the unchanged
timing-noise tests, with calendar-year differences 1.763/1.650/1.406 points.
Its failure remains a finding; no noise allowance is increased. Both
temporary controls and the curve-only shortcut are removed from source.

The subsequent model audit finds a concrete price-demand budget defect.
After emergency sharing pools poor singles into an existing shared cell,
the price-pressure loop applies the old target cell's budget to its larger
new household count. That can promise unfunded whole doors even though the
matcher itself uses the conserved current budget correctly. A validated
snapshot causes it: five old shared households at budget 20 plus twenty-five
singles at budget 1 form ten shared households at budget 12.5, preserving
125 resources. They occupy five half-price doors at ask 15, paying exactly
75. The old pressure loop reports ten whole-door demands; the current
budget can fund no extra whole door, so occupied pressure is five. A second
case with current budget 20 must still report all ten funded demands.

`HousingMarketCheck.mergedBudgetPressure` is first-red on the old algorithm:
10 observed, 5 expected; the resource, receipts and funded-20 controls pass.
Raw `target/RES3-pressure-first-red.{txt,log}`. The price-demand calculation
is extracted without behavioral change so the saved, resource-consistent
inputs exercise the same path as monthly clearing. ★ Use the actual
post-transfer budget for price pressure; retain pre-transfer metadata for
the independent planning projection. Do not apply a historical richer
budget to the new pooled count. The existing lease-length sticky-price
function and all price constants remain unchanged.

This is a new production model mend, so it requires a stranger's delta
review and a fresh final ensemble after the named checks. It adds no saved
field and changes no stored meaning: format/floor 64 remain the owed bump
from shipped 63. The existing old-format native refusal pairs and reload
comparisons remain required. HousingMarketCheck and MonetaryCheck are
running again before further attribution of the curve findings.

The pooled-budget regression and full HousingMarketCheck now pass. Both
historical and present-day catalogue runs, paid builder completions,
MoneyAudit/household identities, actual rent receipts and next-month native
reload comparisons remain green; the regenerated report is committed as
`RES3-catalogue.md`. Raw `target/RES3-pressure-HousingMarketCheck.{txt,log}`.
HousingCheck, HousingDoorCheck, HouseholdCheck, BuildingDataCheck, InvestCheck
and ReadPathCheck also finish exit 0 on this model. HousingDoorCheck includes
899 played months of the household cash identity. SaveFileCheck is still
running at this point.

MonetaryCheck reproduces its three original failures after the budget fix,
with exactly the same curve and timing-noise readings. The mixed rich/poor
sharing defect is real, but it is not the cause of that toy's counter-channel.
Raw `target/RES3-pressure-MonetaryCheck.{txt,log}`. No assertion or threshold
is relaxed, and no monetary line is entered in the ship file. A further
read-only report records each standing class's homes, occupied leases,
funded demand, ask, target, capital and carry at the measurement opening
and each calendar-year close; it changes no fixture input or model state.

SaveFileCheck finishes exit 0 too, including the genuine 0.9.192 format-63
save/history boundary and preservation of both refused files and the
running city. All seven checks in `target/RES3-pressure-core.sh` pass;
their individual raw outputs are `target/RES3-pressure-<Harness>.{txt,log}`.

ReloadCheck also passes in full (215 seconds): checkpoints through m1205,
the caused events at m1211, autosave m1229, and next-month history/save
comparisons all agree. The autosave continuation compares 821,235 history
numbers in 336 series and 185,156 save numbers. Its shutdown again prints
the already-traced native harness lifecycle warning while deleting its
scratch autosave-history folder, after all assertions pass; it is not a
changed comparison or a player save. Raw
`target/RES3-pressure-detail-ReloadCheck.{txt,log}`.

The corrected read-only class report completes and reproduces the three
monetary reds (`target/RES3-pressure-detail-final-MonetaryCheck.{txt,log}`).
At m49 the 3/10 percent towns have similar populations (285/293) and H55
capital quotes (4.8007804/4.7619382), but 58/12 actual Compact houses. Their
asks are .025161104/.039079114. By m84 their H55 stocks are 87/66; the
3-percent town also has 30 T100 homes where the 10-percent town has none.
Thus the dial changes actual financed supply and the occupied mix, not
only households' demand. The higher-rate class carry at m49 is actually
lower (.018494662 versus .022005625 for H55), so a rising interest floor
alone is not the explanation.

A third, single-variable control is declared from that observation:
hold new private Real Estate orders immediately after the common m25
founding, without changing its stock, pending orders, households, money,
prices, any rate or the measurement window. Keep actual leases, rent
updates, maintenance and every existing policy/noise assertion. This
isolates the newly observed supply response in the fixture's stated
steady-town premise, unlike the rejected all-form stocked town with its
changed founding population. If the control does not cause identical
standing stock through the measurement window or still fails the hard
assertions, it is not a completed fixture repair. Its temporary input hook
is restored automatically after the diagnostic, and is not final source.

That hold-after-founding control restores every noise bound, but inflation
is 11.32/10.61/10.90/11.02 percent: only .296 points across the range, so the
one-point minimum still fails. Nor does the control cause fixed standing
stock: the original paid housing queue continues to complete, at different
times across the dials. At m49 its House counts are 52/53 and H240 counts
13/14 at 3/10 percent. It is rejected as a completed repair. Raw
`target/RES3-policy-fixed-stock.{txt,log}`. The source hook is removed.

Complete that causal test by causing the actual proposed condition,
rather than repicking a seed, rate, window or threshold: the same two
40/20 House inputs are paid standing fixture stock at their original
founding months, and new private housing orders are held before the first
month. The fixture supplies their actual plot area and cost, as it already
supplies its Bakery/Diner/Boutique and hospital capital. No intervention
occurs after the common founding. A diagnostic assertion requires exactly
those sixty Houses and zero other RE homes at every reported measurement
checkpoint; all existing monetary assertions remain. This control too is
temporary unless it actually causes its condition and passes the laws.

The completed fixed-standing-stock control causes all 32 checkpoint
inventory predicates: exactly sixty Houses and no other RE homes, across
the four on-time and four month-late runs. Populations are nearly steady
(3 percent: 259 at m49, 273 at m84; 40 percent: 260/274). Every original
fixture, noise and direction assertion passes, but the one-point minimum
still fails: inflation is 7.42/7.47/7.16/7.30 percent, a .121-point effect.
Calendar-year timing noise is .034/.033/.159 points, against the unchanged
.650 allowance. Raw `target/RES3-policy-standing-stock.{txt,log}`.
This control is rejected too; the automatic restoration removes its input
hook, extra fixture assertions and all changes to founding. Final source
keeps only the read-only component/class reports. There is no adopted
fixture change, model calibration, rate/window/tolerance change or
MonetaryCheck known-red row.

### Monetary calibration block after the first dry-run mends

The owned block is the original MonetaryCheck policy curve: native main
86359b9d4 passes; RES3 reverses the 3-to-10-percent CPI ordering, misses the
one-point 3-to-40-percent minimum, and exceeds the calendar-year timing
bound. Credit/goods transmission remains present, while financed housing
supply and its paid rent mix oppose it. Removing fiscal borrowing did not
change the result; alternative stocked/fixed-supply controls did not pass
all the retained laws. Even the genuinely fixed, nearly steady sixty-House
control misses the minimum. These are evidence, not replacement premises.

The brief authorizes calibrated starting inputs and actual rent/choice,
but does not decide a new CPI definition, a different minimum policy
effect or a new smoothing/choice rule. Changing those solely to turn the
curve green would not mend the finding. The next needed call is a focused
money/macro review of the measured rate-and-housing response and a concrete
calibration direction that retains the laws. Recommend keeping the current
assertions and this block until that diagnosis is settled, rather than
assigning these owned failures to a fixture-sweep row. The pooled-budget
price-demand defect has its separate first-red/fix and still requires a
stranger's delta review. No new final ensemble or second dry run is claimed.

First-dry disposition: of the 36 uncovered NEW harnesses, fifteen have
green input/measurement mends, fourteen are the agreed untouched FC2 files,
six have individually traced fixture-premise rows proposed for their
queued owners, and MonetaryCheck is the remaining owned block. The two
previously named ScaleCheck/BuildCardCheck cases bring the total NEW count
to 38. HT1-H's additional CurrencyRegime/PriceLevel/HomeRail/RailDepot scope
still needs the orchestrator's queue amendment; no branch edits QUEUE.md.
FC2 must ship, be merged and have its fourteen shared checks verified before
another slot request. The provisional ship marker stays in place.

The restored final source passes `./batch-check.sh --fast` (exit 0): compile,
generated maps, and zero firm Stale findings (199 soft). Its MonetaryCheck
bytes equal the pre-control backup; only the read-only reports differ from
the reviewed source. `git diff --check`, LF checks and
`sh tools/known-reds.sh RES3 docs/queue/RES3-ship.md` pass. Raw fast output:
`target/RES3-block-fast.txt`. This is a blocked checkpoint, not a mends/SHIP
or ready claim. The earlier 80-run report remains explicitly a snapshot of
2d9a79aea, not validation of the new pooled-budget mend.

### DX-RES3 monetary direction and the caused mends (2026-11-23)

The orchestrator accepts DX-RES3's like-for-like CPI diagnosis and orders
that defect mended, then the missing funded-demand channel, preserving
MonetaryCheck's fixture, rates, calendar window, minimum effect and noise
laws. DX mail `20261123-135707-894-DX-RES3-5da0`, call
`20261123-135800-281-orchestrator-be0f`. Main's records-only LP-G1 ordering
commit 5eae9afd4 is merged before the mends; FC2 has not shipped.

The CPI regression fails on the previous calculation: eight studios at
20 and two family homes at 10 open at 180/24 = 7.5 per capacity; switching
the occupied counts to two/eight with unchanged class bills incorrectly
reports 120/36 = 3.3333333333. A subsequent ten-percent rise in both class
bills should read 8.25, but reads 3.6666666667. Raw
`target/RES3-like-first-red.{txt,log}`. These are model snapshot inputs,
not an adjusted CPI assertion or a different played-city premise.

★ Chain the typed rent level on the preceding month's occupied doors at
each class's previous and current actual paid bill per door. Open at the
first paid unit value. Carry forward a class without a paid observation;
a newly occupied class joins the next basket. `sold()` strikes the level
before replacing the existing saved lastTrade/tradeQuantity fields, so
there is one update at the actual monthly sale, none on a getter/load.
The new scalar `HousingMarket.State.rentPriceIndex` travels through the
existing DataSave housing snapshot and native restore; validation is
atomic, reset clears it, and redenomination scales it once. The branch's
unshipped format/floor 64 already covers this stored-meaning change, as
the orchestrator specifies; no conversion or additional bump is added.
Actual leases, weights, receipts, bills and money settlement are untouched.

HousingMarketCheck passes the unchanged-mix-price regression, actual
receipt controls, ten-percent comparable price rise, pure reads, saved
level restore, redenomination, invalid-level atomic refusal and missing
observation carry-forward. Full historical/modern paid build and native
continuation cases pass too. `target/RES3-like-HousingMarketCheck.{txt,log}`.
With this defect alone MonetaryCheck exactly reproduces DX's probe:
6.77/2.99/4.52/2.47 percent; 4.299 points across the range, with the 20/10
ordering and two noise assertions still red. Whole-window timing offsets
are .000/1.254/.213/.094; calendar-year maxima .054/1.096/3.093.
Raw `target/RES3-like-MonetaryCheck.{txt,log}`.

The budget audit finds the omitted link: housing reads available income
plus one year's liquid-savings support but never the existing ex-ante
saving response used by household spending. The new regression starts
with resources 10 and a current rent 4; SPEND_FLOOR .5 should leave a bid
7. The old path still returns 10, admits the same twenty bidders at an ask
between the two intended budgets, and leaves the price target unchanged.
All four assertions fail first (`target/RES3-bid-first-red.{txt,log}`).

★ Apply the existing spending response to resources above the committed
funded rent, with available resources as the cash ceiling. Preserve a
fundable current lease, every fixed fee, subsistence food and HC1's current
card minimum; a stimulus cannot invent credit above that ceiling. Incoming
workers have no current commitment. Live clearing and incoming forecasts
receive `Game.spendFactor()` for this month's ex-ante real deposit return,
not the previous plan's stored workspace. Bare model calls retain the
existing factor-1 default. No new dial, saved budget axis or cash movement
is introduced. The budget-to-affordable-demand-to-target regression now
passes, as do the cash ceiling, current lease and untouched savings controls.

The full HousingMarketCheck remains green. MonetaryCheck with both mends
reads 6.77/2.99/4.11/2.26 percent, 4.515 points across the range. The higher
20/40-percent dials now lower inflation through the restored bid channel,
but the 20/10 ordering and both timing assertions still fail. Whole-window
offsets .000/1.254/.095/1.426; calendar-year maxima .165/1.308/3.174.
Raw `target/RES3-bid-<Harness>.{txt,log}`. SCARCITY_ELASTICITY stays at the
existing value 1.0; no coefficient is fitted to the harness. A read-only
monthly class/CPI/bid trace is running to determine whether the remaining
laws fail on the lender-held supply timing alone, as the call requires.

The monthly diagnostic reproduces those same three reds with no changed
fixture input (`-Dres3.housingTrace=true`, a read-only reporting switch).
Raw `target/RES3-bid-flow.{txt,log}`, parsed rows
`target/RES3-bid-flow.json`, event table
`target/RES3-bid-flow-summary.txt`. The remaining price comparison is now
in the expected direction at a common supply: at m72, 10/20 percent have
77.0897/64.6021 funded Compact demands and capital 5.0411358/4.9877745,
with carry .020302509/.019131820. Both demand and holding costs are lower
at 20 percent. Its stock, however, is only 40 Compacts against 52, so its
target is .038935015 against .036121694. Holding its stock at the same 52
doors would give .029950012 from its own observed capital and demand,
below the 10-percent target. This is a direct scarcity calculation, not
a changed simulation or a new target law. The actual 20/10 rent-component
growth is 8.0790/3.4575 percent in year two, and 7.8860/6.0662 in year three.

The worst timing pair is 40 percent. Its first new Compact completions
after the common 14-door stock occur at m64 on time (five doors), but m66
on the month-late run (four doors). At m66 it is 27 versus 18 doors; at m72
50 versus 43. Capital at m72 is nearly identical, 4.9994720/5.0008834,
but target rents are .027682695/.033510600 and asks .040079686/.042439961.
The different completion path continues into year three: m84 stock is
66/70 Compacts and 28/26 Duplexes, with CPI 1.030045117/1.071200417 and
chained rent .007047656550/.007444763787. Year-three CPI inflation is
2.405991/5.580067 percent, the 3.174-point timing failure. Year-two maxima
are 1.308 points. DX's lender-held supply mechanism remains visible after
both defect mends; the resulting population and bid feedback is recorded,
not assumed identical across different construction paths.

Following call 4, stop monetary model changes here and report these
remaining supply-timing figures for the orchestrator's premise decision.
The corrected index and funded saving channel have caused regression tests
which fail before their respective fixes and pass after them;
the full macro assertions remain red, unwaived and unassigned to another
queue row. No scarcity exponent, lease speed, lender gate, fixture, rate,
window, minimum effect or tolerance is changed. The pending conservation,
native save and reload checks are completing before that checkpoint is
pushed; no new ensemble or dry-run slot is requested.

All eight serial invariant checks now pass on these model mends:
HousingCheck, HousingDoorCheck, HouseholdCheck, BuildingDataCheck,
InvestCheck, ReadPathCheck, SaveFileCheck and ReloadCheck. The housing cash
identity closes for 899 months. The genuine shipped format-63 refusal and
both-file/running-city preservation checks remain green. The long reload
finishes in 53 seconds with 820,770 history values in 338 series and
106,202 saved values equal in its autosave continuation. Raw
`target/RES3-dx-<Harness>.{txt,log}`. HousingMarketCheck's complete catalogue,
money, native round-trip and new caused regressions already pass under
`target/RES3-bid-HousingMarketCheck.{txt,log}`.

`./batch-check.sh --fast` passes (exit 0), with generated maps and zero firm
Stale findings, 199 soft; raw `target/RES3-dx-fast.txt`. Authored source and
queue notes pass `git diff --check`; generated month-order references keep
Maps' existing Markdown hard-break spaces. No extra JVM or fixture probe
remains running. The three MonetaryCheck lines stay owned and red; the
provisional marker and the deferred combined review remain in place.

### Lane resumed: reality, several towns and the format-65 boundary

Jerus's 2026-11-23 23:33 answer is the authority for correcting the old
universal adjacent-rate law: "even reality higher interest rates doesnt
always equal liwer inflation". His 23:55 direction is "sometimes its as
simple as check reality". The orchestrator's revised 23:42 instruction
replaces the earlier B wording: retain the overall effect, assert the
housing supply channel separately, and assess ordering/timing over several
towns or a longer window without hiding an actual supply response as noise.
The lane go is `20261124-015023-063-orchestrator-5dec`.

The evidence supports that correction. [Jorda and Yalcin, FRBSF Economic
Letter 2024-23, September 3, 2024](https://www.frbsf.org/research-and-insights/publications/economic-letter/2024/09/when-is-shelter-services-inflation-coming-down/)
explain the opposing effects of higher rates on housing demand and supply:
financing costs reduce new construction's returns, while leases reset
slowly. Their US discussion through July 2024 shows why shelter inflation
can remain high after other components cool. [Kmetz, Louie and Mondragon,
FRBSF Economic Letter 2023-19](https://www.frbsf.org/research-and-insights/publications/economic-letter/where-is-shelter-inflation-headed/)
document that divergence after the 2022 tightening as market asking rents
slowed before the CPI shelter component. These observations establish
persistence and timing, not a causal estimate from the dates alone.

[Dias and Duarte, Federal Reserve IFDP 1248, May 2019](https://www.federalreserve.gov/econres/ifdp/monetary-policy-housing-rents-and-inflation.htm)
find that rents can rise after a contractionary monetary shock, while
other major CPI components decline or are unresponsive; shelter accounts
for much of the measured price puzzle. Their tenure mechanism is evidence
against a universal net-price sign, not a claim that RES3 already implements
HO1's own-versus-rent choices. The game's lender-held construction is the
financing/supply mechanism described by the 2024 FRBSF analysis. No model
coefficient is fitted to these papers or to a passing harness result.

★ Before the new measurement, fix world paths 0,1,2,3,4, all four original
rates, and both original hold dates. Keep months 49-84, the original seed-0
one-point effect bound, and the .650-point timing bound. The multi-town
ordering statistic is the mean policy/CPI regression slope over the whole
four-rate curve, plus the mean far-end effect; local adjacent inversions
remain reported as actual outcomes. Timing is the absolute paired mean
late-minus-on-time difference for each rate, over the window and in each
calendar year. Individual magnitudes remain printed. This measures the
timing of the aggregate estimate rather than cancelling an unreported
failure or selecting towns after observing them.

★ Observe accepted starts in a harness-only `HousingTown` subclass:
`buildFor` calls the real implementation once and records housing doors
only after it succeeds. The helper verified that BuildLog and the previous
stock differences are completions, not starts. No model hook, new saved
flow or inferred start from closing stock is introduced. The supply case
is predeclared as the original town's 3/10-percent pair: count accepted
housing doors from m25 through m48, then compare the same C01 class's
average posted rent during m49-60. Both halves are hard assertions.

Tracing was delegated first to a short read-only helper, then, under the
new contract 4 rule, to MemWatch `RES3-logs` and `RES3-log2`, each reporting
at most fifteen lines, with no source edits or JVMs. Committed memory was
63/67 percent before launches. The builder retains implementation, source
review, checks and commits. The helper sessions use an isolated checkout.

Main through R6f 0.9.196 (`94ba28823`) is merged. PW2 already reserved
156-166 for these housing forms and shipped power IDs 167-171, so no IDs
are moved: the merged catalogue has unique IDs through 171, nextId 172,
and the fallback preserves both catalogues. AllChecks keeps both batches'
harnesses; date checks keep both the housing and power rows; both independent
used-car reload checks remain.

R6f owns the shipped format/floor 64. RES3 therefore raises both to 65,
without conversion. A new real format-64 pair is captured from unchanged
shipped `94ba28823`, 0.9.196, by HousingFormat64Fixture after that tree's
`--fast` passed with an empty source diff. It plays twelve native months
after probeCity and writes the native scratch slot-10 pair at m31. The
compressed artifacts, raw hashes and provenance are beside the older
pairs in `src/main/resources/fixtures/res3-format64/`. SaveFileCheck's exact
one-below-floor boundary now uses that pair, with the same byte/history,
fresh-city and running-city guards; the real format-62/63 checks remain.

The first five-path probe exposed a fixture-control error: calling the
old town "seed 0" did not make its default path seed zero. Founding's
actual default is 48213. The helper caught that the first probe replaced
the original town rather than retaining it. Correct it by explicitly
including `Founding.defaults().getPathSeed()` first. Earlier MonetaryCheck
references to its original "seed 0" mean this default town; ensemble path
numbers are unaffected. All five pilot paths and their adverse outcomes
remain in the record, not silently dropped. The pilot's four reds and
per-path values are in the immutable helper mail
`20261124-021155-604-RES3-log2-9efb` and
`target/RES3-reality5-helper.txt`, with its wrapper output
`target/RES3-reality-MonetaryCheck.txt`.

★ Use the existing ensemble's sixteen consecutive world paths 0-15 for
the broader paired estimate, retaining all pilot paths, and the original
default town for its unchanged effect/supply tests. This is a sample-size
increase, not a choice of passing towns; it is declared before the new run.
The pilot's pooled yearly timing sensitivity was 1.543760 points, so five
towns did not establish the retained .650-point bound. The expanded sample
keeps that bound pending measurement. Do not impose a new universal
one-point effect on every world-path population: the original bound is
asserted on the original town, while the whole-curve slope is assessed
over the fixed broader sample and all per-town effects are reported.

The corrected 17-town calibration run preserves the actual default town:
its effect is still 4.515 points, and the supply assertions now observe
137 versus 24 accepted doors, followed by C01 average asks
.02486437563/.04124895851. All founding, currency, crew, calendar and
fixed-basket premises pass for every path. Pooled whole-window timing is
.356534 points; the three annual maxima are
.2011492203/.5114997838/.9612071193. Only the pooled third-year timing
bound remains red. The pooled policy/CPI slope is -.000676, but individual
paths have both positive and negative total effects, as the revised law
allows. `RES3-log3` reports the complete results in immutable mail
`20261124-022255-747-RES3-log3-bf93`; raw
`target/RES3-reality17-MonetaryCheck.{txt,log}`.

★ Keep the old harness's approximately twofold calibration margin, and
choose upward quarter-percentage-point rounding for these two new
aggregate timing limits. The 17-town calibration therefore gives
.750 points for the whole-window estimate (twice .356534) and 2.000 for
an annual estimate (twice .961207). Preserve the old .650-point ordering
resolution and the original one-point effect bound. For ordering, compare
the pooled whole-curve slope across the 3%-40% range with that retained
resolution; do not assert an exact negative sign at arbitrarily small
scale or require every local pair to fall. Print the full mean curve.
These are calibrated harness measurement limits, not altered price,
credit or household-response coefficients.

Before seeing any new outcomes, choose the next sixteen consecutive paths
16-31 as independent validation. The final harness retains the original
default-town effect and supply cases, and applies the frozen aggregate
ordering/timing limits to those validation paths. Every calibration path
and every adverse outcome remains in the study record above; none is
relabelled a pass or selected away. The source default resets to the
actual default seed after measurement. No fixture intervention, new
month window, model adjustment or per-town noise waiver is involved.

The original calibration's .650-point ordering allowance is retained for
the pooled curve: scale its regression slope by the full 3%-40% rate span
and compare with that resolution, rather than impose an exact negative
sign below the measurement's resolution. This does not change the original
town's one-point effect assertion. The two newly calibrated timing limits
are frozen at .750/2.000 points before validation on paths 16-31; the
validation sample excludes the calibration paths. The original default
town is tested separately, not reused in the validation estimate. The
whole validation curve, paired timing estimates and every individual
town's outcome remain printed for review.

Calibration observations (percentage points; timing columns are each
town's actual responses, not limits on the paired mean):

| Path | Low-minus-high inflation | Largest full-window timing response | Largest annual timing response |
|---:|---:|---:|---:|
| 48213, original | 4.515 | 1.426 | 3.174 |
| 0 | -1.916 | .256 | 1.183 |
| 1 | 1.963 | .260 | .950 |
| 2 | 1.276 | 1.633 | 3.943 |
| 3 | -2.942 | .380 | 1.338 |
| 4 | 1.409 | 1.310 | 4.047 |
| 5 | 7.179 | .181 | 1.221 |
| 6 | .791 | .251 | .706 |
| 7 | -2.124 | 1.335 | 2.589 |
| 8 | 1.791 | .697 | 2.182 |
| 9 | -2.715 | .596 | 1.352 |
| 10 | .462 | 2.254 | 5.713 |
| 11 | -3.596 | .516 | 1.503 |
| 12 | .571 | .265 | 1.080 |
| 13 | -3.180 | .882 | 3.120 |
| 14 | 2.761 | .565 | 1.449 |
| 15 | -2.164 | .562 | 1.573 |

The independent validation on paths 16-31 passes MonetaryCheck (exit 0),
with no recalibration after observing them. Its mean 3/10/20/40-percent CPI
curve is 4.056172/3.093337/4.375457/4.046189 percent. The whole-curve slope
is +.010666, or +.3946 percentage points across the rate range, below the
retained .650-point resolution. The mean far-end reduction is only
.009983 points. That near-zero broader effect is a limitation of the
result: the retained one-point transmission assertion is on the original
town, whose curve remains 6.77/2.99/4.11/2.26 percent and effect 4.515
points. The multi-town result does not establish a universal negative
slope or a one-point effect in every town.

Paired timing sensitivity on the validation sample is .193845 points for
the full window and .130032/.329205/.774209 for its three calendar years,
all within the predeclared .750/2.000 limits. Individual towns can have
much larger real construction responses: path 29 reaches 4.313 points
over the window and 15.071 in one year. Those printed observations remain
actual supply/timing outcomes, not a per-town waiver or a claim that they
are small. Every path passes the same dial-only, pinned-currency, full-crew,
whole-calendar and based-basket premises. The separate original-town
supply assertions pass with 137/24 accepted homes and later C01 rents
.02486437563/.04124895851. Source cleanup after this run changes names,
formatting and output wording only, including replacing the old
MEASUREMENT_NOISE name with ORDERING_RESOLUTION; no statistic or bound
changes. Raw `target/RES3-reality-validation-MonetaryCheck.{txt,log}`;
the read-only helper's fifteen-line report is immutable mail
`20261124-024247-795-RES3-validation-770c`. Its session has ended.

After merging main, the named HousingMarketCheck, HousingInventoryCheck,
HousingChoiceCheck, HousingCheck, HousingDoorCheck, HouseholdCheck,
BuildingDataCheck, ReadPathCheck, ReloadCheck and ResearchCheck all pass
with exit 0; InvestCheck returns 3 for only main's known red. The typed
catalogue, paid builds, money/household audits and real reload comparisons
therefore run under the format-65 model, not yesterday's classes. Their
raw files are `target/RES3-resume-<Harness>.{txt,log}`. SaveFileCheck separately
passes the real format-64 boundary and its older native pairs (exit 0),
`target/RES3-format65-SaveFileCheck.{txt,log}`. The catalogue report is
regenerated after the merge, including its current small and larger city
integrated-month observations. The validation runner's subsequent
`./batch-check.sh --fast` also passes, exit 0.

Final CurveCheck, ExpectationsCheck, TimingCheck and WaterCheck each pass
(exit 0), one named harness per call. The final fast check passes after
all source cleanup, regenerating the maps with 0 firm and 199 soft Stale
findings. `RES3-close-logs`, launched at 69-percent committed memory, reads
all named outputs and confirms these results in immutable mail
`20261124-025240-784-RES3-close-logs-305d`; it has ended. Raw final outputs:
`target/RES3-reality-<Harness>.{txt,log}` and
`target/RES3-reality-final-fast.txt`. The InvestCheck red is exactly main's
HT1-H/FB3/RT2-L wage/rebate premise: index 1.158005 and both rebates zero,
so the required strict contrast is not caused. No new red is passed as
known. HousingCheck also prints an uncounted OBSERVED line because its
played town did not close homes leaving landlord debt; that observation
is not a new passing assertion. HousingDoorCheck still closes all 899
months' cash identities. Native refusal, current-format reload and the
atomic malformed-market guards all pass.

The final source pick against merged main `94ba28823` is ALL, 251
harnesses. The review line is "needed - saves: DataSave.java,
+SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT". The combined stranger review also
covers money: the current-pool demand budget, the chained comparable-rent
CPI level, saving-sensitive funded bids and their native restoration.
There is no threading change. The revised monetary law, real boundary
pair and main merge belong to that same delta. The provisional ship
marker remains. Known-reds syntax passes with exit 0, including the four
HT1-H fixture classes awaiting the review's premise acceptance; no
MonetaryCheck waiver is added. The earlier ensemble is not validation of
this model. The fresh final ensemble against `docs/ensemble/0.9.195.md`,
the FC2 merge and its four named shared guards when it ships, and the
slot-authorized ALL dry run remain after the review's SHIP. No other job
or helper is left running at the review handoff.

### REV-RES3x P1: guard the surviving transmission across the declared towns

The review correctly rejects the earlier pooled ordering assertion as a
transmission guard: it admits zero and a positive response within .650
points, while the mean far-end effect was only printed. The original
town's 4.515-point effect does not establish a broader response. Keep that
original effect and both actual-starts/later-rent assertions; diagnose
paths 16-31 before changing the model or the law again.

MemWatch helper `RES3-P1-trace` runs native main `94ba28823` and RES3
`8fdb111b6` in separate detached checkouts, strictly serial named probes.
Its fifteen-line report is mail `20261124-033229-435-RES3-P1-trace-94a5`.
Both invocations exit 0: 272 path/rate/timing observations, with 1,904
founding, basket, crew, currency and calendar premises passing. The
portable probe and founding-body hashes match between models. Equal
fixture inputs do not imply equal month-25 states across different
models; each model's policy/timing twins have equal fingerprints. The
typed total-CPI mean exactly reproduces the earlier validation curve.
No model source or household/policy input is changed for the comparison.

Native main is adverse over the same window, not almost flat: its mean
total CPI is 5.793602/5.435019/8.231521/8.325781 percent at 3/10/20/40%,
a -2.532180-point far-end effect. Its common-basket non-shelter effect is
also adverse, -.074993 points, with twelve adverse paths. RES3's common
non-shelter curve is 4.527429/2.171282/2.117868/2.904979 percent, a
1.622450-point reduction. It has eleven positive and five adverse paths,
all retained (16, 19, 23, 25, 26 are adverse). Every path in both models
has a lower mean live spending-plan factor at 40% than at 3%: a falling
factor alone does not establish price transmission. Shelter offsets
RES3's surviving non-shelter response, while the total mean remains only
.009983 points lower. This is not a claim that every housing feedback
has been causally isolated. There is no lost non-shelter channel to mend
in RES3 on this evidence.

[The committed comparison](RES3-transmission.md) files native main's
coverage gap and every path's total/shelter/non-shelter effect. Main's
original town passes a 2.231010-point total effect while its broader
non-shelter mean is adverse; do not call that general transmission or
attribute it to RES3. The new price guard would reject that native curve.
Full portable source, entry-point patches, commands, unrounded data and
snapshots remain under `target/RES3-P1-trace/`. This is measured evidence
over the original window, not a new longer-run claim or a known-red
transfer. The FRBSF and IFDP sources and Jerus authority above still apply.

★ Guard a meaningful surviving price channel directly: the mean
common-basket non-shelter reduction across all sixteen declared paths
must be at least the existing TRANSMISSION_FLOOR, one percentage point.
This retains the original minimum effect size rather than calibrating a
new near-zero allowance. Also require the higher real saving return to
reach each town's live mean spending-plan factor. The existing
HouseholdCheck laws separately verify that this factor is actually
applied to grocery surplus, luxury and meals, including the live month.
Those quantity/application laws and the new price law protect different
parts of the channel; neither replaces the other. The original-town
effect, supply assertions, rates, fixture, window and timing limits stay.

★ Hold a genuinely common physical basket across policies, including
each services fee quantity. Policy twins can have different component
weights and nested fee shares at their links, so excluding the rent
component from each twin's own basket alone is not a clean price
comparison. Fix component quantities and every fee quantity from the
3% on-time twin; price that same bundle at both endpoints of each rate.
The common normalization cancels in the ratio. Assert a fixed link and
quantities throughout each run, and reconstruct each run's native CPI
change from its own observed quantities as a hard measurement premise.
No annual percentage changes are added as if they were CPI contributions.
Repricing both models with the identical native physical basket still
gives RES3 1.669167 points, so the positive result is not the choice of
RES3's quantity bundle. The implementation uses three pure PriceIndex
price/base readers instead of decoding the saved array in the lasting
harness. There is no new model state, coefficient, flow, format bump or
default-trace movement from those readers; format/floor 65 remain owed.

The strengthened MonetaryCheck now passes, exit 0: 491 assertions and no
reds. Mean non-shelter prices are
4.5274290012492875/2.1712822045330857/2.117867665076757/2.9049788572586515
percent, a 1.622450143990636-point effect, matching the independent
diagnostic to floating-point precision. All 136 fixed-basket/fee checks
and all 136 native CPI reconstructions pass, as do the 17 live plan
contrasts. Every older fixture premise, original-town 4.515-point effect,
137/24 starts, later C01 rents .02486437563/.04124895851 and retained
aggregate timing assertion remains green. The new guard rejects both
zero transmission and native main's observed -.074993333-point
non-shelter mean. This is a benchmark/predicate comparison, not a
response-suppression ablation. No ablation or universal-town price sign
is claimed.

`./batch-check.sh --fast` passes, exit 0, with 0 firm and 199 soft Stale
findings. Raw `target/RES3-P1-fast.txt` and
`target/RES3-P1-MonetaryCheck.{txt,log}`. Read-only `RES3-P1-verify`, launched
at 70-percent committed memory, confirms these results and the authored
measurement diff in its fifteen-line mail
`20261124-034825-281-RES3-P1-verify-9f1e`; its session has ended. Its check
of the committed per-path table finds every value within the printed
rounding precision. The earlier HouseholdCheck application laws remain
unchanged and green from the format-65 run; no existing money, savings,
conservation or reload assertion is weakened. The new production methods
are pure readers, so this P1 mend adds no default-trace movement.

Main's records-only NK1 queue update `149d88cd7` is merged after the run;
the model still equals the diagnosed `94ba28823` baseline plus RES3. The
provisional marker and review-required ALL pick remain. The independent
diagnostic's temporary worktrees are removed after its source patches,
commands, data and logs are kept under `target/RES3-P1-trace/`. No helper
or JVM is left running at the short P1 re-review handoff.
