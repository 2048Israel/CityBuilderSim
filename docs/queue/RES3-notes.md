# RES3 — home choice, typed rents and the 14-form catalogue

Built on batch/RES3 from shipped RES2 (7e484c4da), refreshed through HC1 on
main efca2c5fd (0.9.190), merged as ef18a4b6c, and the queue-only e66c310a3.
Final verification and publication status are below.
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
| Small | 210 | 56.955 | 65/32/112/0/0 |
| Present-day | supporting catalogue inputs above | 47.177 | 84/32/330/0/0 |
| Larger present-day | 148,967 | 47.409 | 82/32/314/0/0 |
| Historical | supporting catalogue inputs above | 37.847 | 62/32/193/0/0 |
| Larger historical | 149,126 | 43.702 | 62/32/193/0/0 |

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
