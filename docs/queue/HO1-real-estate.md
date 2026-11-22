# HO1 - Final Real Estate dependency and ownership contract

## Green light and execution dependencies

Jerus's 2026-11-21 instruction releases RES1's general design/review hold.
HO1 is authorized for implementation using this amended contract. It starts once
the existing HT1/HS3/HC1 prerequisites and RES2, RES3 and RES4 are on main and
their acceptance checks are satisfied. None of these new foundations was on
main a9ffdac83 when closure began. The green light does not skip dependencies.

This brief supersedes the affected inventory/clearing/billing/timing/18-month
pipeline assumptions in [the credit study sections 2.3 and 7](../studies/spec-credit-housing.md),
as enumerated in [RES1 section 10](../studies/spec-real-estate.md).
Its previously chosen credit policy remains: one 5% minimum, insurance under
20% down with LTV premiums, +2 points without a nominal floor, GDS/TDS39/44,
agreed amortization/renewal rules, wanted share by available stage, and ordinary
landlord net-value pass-through0.4. Existing estimates remain explicit calibration
inputs. They are game policy, not a claim of current national law.

## Expected source scope

New `HomeMarket`/`HomeLoan` and aggregate ownership/common-cost models; `Household`,
`HouseholdBalance`, `sectors/RealEstate`, `Bank`, `TaxPolicy`, relevant housing
registry/builders APIs, `Game.startOfMonthUpdate`/load, derived synchronization
in `SimulationEngine`, `MoneyAudit`, `DataSave`/history and focused home/credit/
housing/accounting/save checks. No UI or new sector; coordinate live overlaps.

## Required behavior

1. Purchase quantities in actual dwelling classes. Preserve physical stock,
   occupancy and listing subset identities. Include allocated land and rights.
   Owners pay zero rent, full actual fees/owner costs, tax, mortgage and their
   optional parking. Retain cost basis and secured debt allocation.
2. Credit includes the financed insurance premium, property tax, applicable heat
   and 50% of the actual common fee. The cash waterfall pays100% of the fee.
   Apply actual other debt/TDS, essential spending, resources and bank funding.
   Heat already inside the common fee cannot be charged twice.
3. One quoted/clearing month with bounded aggregate work. Independent class CDF
   caps must not reuse the same wealthy/qualified tail. Partition joint income/
   savings eligibility into temporary disjoint regions, merge identical eligible
   masks, and move cohort quantities. With independence use closed-form marginal
   CDF differences; record that approximation. Final allocation/cash checks also
   enforce all additional qualification constraints. Persist no region identity.
4. Keep tenants. Eligible incumbent tenants may buy their current class; a refusal
   continues renting. Outside owner-occupiers consume vacant supply. Sellers and
   buyers cannot produce a second home or a second purchase for one demand share.
5. Ordinary RE investment sells above/buys below comparable net rental value,
   after vacancy, tax and actual operating/common obligations. Financing interest
   is not deducted a second time in capitalized NOI. Sale prices are accepted
   asks of funded matched quantities. Unsold offers have no proceeds/new trade
   price. Keep class last trade, trade volume/value and staleness separately.
6. A partial closing releases allocated collateral/debt first, transfers counts,
   cash and rights once, and realizes seller gain/loss. Capitalized insurance is
   paid once. A voluntary short sale needs seller cash/lender agreement; no
   automatic city insurance. Recognized default loss follows insured/uninsured
   policy. Seller/RE debt cannot become buyers' common liabilities.
7. Common-cost pools exist from the first apartment sale, for equivalent physical/
   service cohorts. Fixed area/right weights include RE's unsold vacant or rented
   quantity. Partition repair, tax, staff, utilities and private/common parking
   exactly once. Initial management is actual cost recovery, markup0, reserve
   contribution0; existing repairs remain paid. RE management staffing cannot be
   billed again in another cost pool. Parking surplus remains pool cash and reduces
   future calls. An unpaid fee is a receivable/cash shortage, not cash received.
8. After RE ownership reaches zero, all physical/common obligations and management
   survive; RE owner contributions/rent stop. Show actual management service revenue
   and its expense, not the whole common call as profit. A replaced manager needs
   a funded successor before its costs disappear.
9. Bill opening tenure and parking positions. Settle once after Motoring and
   before luxury inside the current `Game.startOfMonthUpdate` call graph. The
   engine synchronizes derived data; loading only rebuilds. Next-month bills use
   closing positions. Re-verify the current seam before implementation.
10. Stop residential financial demolition immediately when HO1 ships. Until RES5
    adds deliberate exit policy, hold unsold structures and their carrying bills.
    Implement arrears/default seller cohorts; never swap a foreclosure for physical
    salvage. Builders supply only completed funded stock from the actual pipeline.
11. Carry aggregate ownership/loans with existing household flows. On death or
    departure, retain unsold stocks, debt and costs in bounded estate/outbound
    seller cohorts and settle real net proceeds before any beneficiary payment.
    HO2 extends move/trade-up behavior; it cannot be required to prevent HO1
    corruption during the first ageing/death event. Do not invent new tax exemptions.
12. Save authoritative stocks, weighted loan/payment sums, common cash/receivables,
    rights, asks/ages/intents and monthly flows. Do not average incompatible rate/
    term/risk obligations to meet a performance cap. Bump SAVE_FORMAT and oldest
    readable format if an old save would read wrongly, with no conversion code.

## Required evidence before HO1 ships

Cause every qualifying threshold from both sides; full-fee cash failure despite
half-fee credit pass; financed premium; shared-wealthy-tail duplicate-demand
counterexample; tenant purchase/refusal; no vacancy; partial and last RE sale;
common cash/arrears and zero-owner boundary; insufficient bank/seller funding;
insured versus uninsured recognized loss; no own-exit reacquisition; physical
conservation; equivalent split/merge; 1000x count scaling; ageing/death/departure;
live versus reload including the first post-load allocation and next bill.
Use model constants and actual model APIs, not the study's formula as a test oracle.

New HomeMarket checks plus affected Housing/HousingDoor/Household/Mortgage/Invest/
Car/BuildingData, MoneyAudit and save/reload checks; actual pick/review follows
the diff. Refresh the played ensemble, including price/rate response with typed
offers and no-trade periods. The old -6.6% homogeneous result is reference evidence,
not a new pass criterion. Measure actual H/T/A/M/L, temporary regions, memory,
save/reload size and integrated monthly overhead. Record assumptions and observed
outcomes before the gate. No additional Jerus study approval is required to begin
after these dependencies; normal model review, gates and findings still apply.

## Authority and bounds

Jerus, 2026-11-21: "ok so now do what you must to finish up the study, so that ho1 is green lighted".
This authorizes closure of the study and its recommended implementation direction.
The controlling design is [RES1 sections 8, 10 and 14](../studies/spec-real-estate.md).
The numerical catalogue is an initial implementation baseline; completed game outcomes
and historical/rate calibration are proved in the named model batches.
Everything is aggregate: bounded physical stocks, household-cell assignments,
ownership, loan/listing cohorts, parking capacities and common-cost pools. No
per-person, per-home, per-building, per-mortgage or per-bay identities.
Reuse existing household cells (141 in the measured snapshot); roughly 180 cells
and 32 form/dwelling classes across 14 forms are the initial dimension budget.
Temporary disjoint funding regions are solver workspace, not saved household cells.
Equivalent cohorts coalesce; count scaling cannot create more records.

Read the current maps and contract before source. Cut from then-current main after
predecessors ship. Refresh overlapping batches before selecting files. The model,
save, caused checks, money/save review and ensemble precede UI. Use scratch cities;
old saves get no conversion code. Measure actual row counts and integrated cost;
the 0.864 ms synthetic kernel is a reference fixture, not a final speed guarantee.
