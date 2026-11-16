# R1d ? the 1900 city switched on

Started from main 32869da28 (0.9.123). In progress; the final checks and retained gate provenance follow below.

## Design calls

- ? New cities use the historical founding by default, including the 1901 wage ladder and dated arrivals/basket. The founding screen also offers All fitted explicitly; existing saves keep their saved flag. Reason: the normal played path must exercise research, while comparisons and existing all-fitted cities retain their meaning.
- ? Scrapping cancels the oldest on-site fitting of each piece on the scrapped template, immediately at the stack removal. No refund: goods were delivered and the builders accepted the contract at the order; cancelling a paid installation is a sunk investment. Partial work cannot migrate to another building. The fixture removes an actual partly fitted bakery while another remains unfitted.
- ? Builders receive the actual treasury payment, including when SHORTFALL_DUST admits a quote slightly above the available cash. The fixture binds the advance ceiling and puts cash strictly inside that gap.
- ? Steam's share survives a grid brownout; the electric share alone is multiplied by the grid ratio. Other staffing, water, road, health and freight constraints still apply. Reason: burning coal in a factory does not depend on grid delivery.
- ? Electric-drive installation is estimated at one setup point plus 10 BUILDING_WORK points per tonne of motor, shafting and switchgear, rounded up (Equipment.DRIVE_WORK_PER_TONNE). This replaces one tenth of a factory's unrelated construction work. The former Bakery estimate was 70 points for 0.3 tonnes; the new one is 4. Motor mass, life and energy conversion are retained. This remains a balance estimate for Jerus.
- ? The tractor's gain includes the composed process's saved horse feed and extra hands, less its fuel, and patent rent on the same physical comparison. The previous recipe-only comparison priced the added fuel alone.
- ? Carters retain nameplate sizing, like the fleet. Hired teams now pay a capital service: an estimated $300 in 1900 money per pair and wagon, 120-month blended life, and Freight.TARGET_RETURN. The world supplies that capital hire, booked once as the named imported service Horse teams hired; feed and local carter wages remain separate. This prices the team without inventing a horse/wagon traded good. Both capital and life remain balance calls for Jerus.
- ? Fittings quote the builders' sales tax, net their suppliers' local input credits, and record the owner's capital purchase. The planner retains its city-value estimate, grossed up for builders' tax; the order uses the exact market split. Reason: the same capital VAT rules as building work must apply when the fitting action is the normal path. Lead times count fitting points as a finite priority queue. Steady site capacity is unchanged; if foreign crews are present they continue site work while local crews fit. The extra delay is the smaller of P/(L+F) and B*L/F, with P/L for local crews alone and no lost throughput when L is already zero. Queue premiums include those finite points once.

## Pending rows read

- R0e-R: the historical basket is active and its shares are drawn on Household money. Cordwood and household utility billing remain open: there is no firewood good or billed replacement heat/power. Retain the sourced lamp/coal quantities and present-year endpoint; adding those markets is a separate gameplay batch.
- R0d-R: the 1901 ladder and dated arrivals are active on the normal path. Retain the elite estimate, 1901/1913 knot, interpolated 1953?2021 line and present licence shares: the switch supplies no new historical observation. Growth and lab affordability measurements belong below.
- R2-R: the tractor now values the horse form's feed and hands beside the recipe's fuel cost.
- R1c-R: binder/tractor composition is already on main from R2d. Medical rows remain R2f's; retain the steel/coke/pulp estimates pending the measurements below. PULP remains one good, so groundwood's quality distinction remains open.
- R1b-R: default historical founding and a founding-screen alternative, fitting VAT, and lead-time deductions are included. Shell repricing stays open: equipment is chosen at completion while contracts and planners price the order. Removing equipment costs without freezing and saving the order's equipment would charge one plant and deliver another. Recommend a dedicated saved order-equipment contract change; no pretend discount is applied here.

## Checks

First fast compile passed. Named mechanics checks are running; no local ALL run is used.

The first named run exposed the default-switch loader bug: an omitted `foundedUnfitted` flag inherited the new constructor default. `DataSave.getFounding()` now restores the Boolean explicitly, including false for a missing key. No save format bump is needed: the meaning of the saved field is unchanged.

The original named fixtures also relied on expensive fitting never occurring during setup. The setup now places the required unfitted count after its settle, applies the forms, and preserves the same one/two/three-unfitted premises. The pure-read twin test holds investment in both twins, preserving its six-month unfitted comparison; Haulage's fixture explicitly retains its modern fleet and driver ladder. The multi-month work fixture configures a long drive from the builders' actual output, rather than assuming an arbitrary factory's estimate must exceed that output. The new scrap/payment regressions passed in the first run.

## Independent money/save review

The separate Codex reviewer inspected the source/resource diff against 32869da28 at HEAD d5fbfe8e plus the working changes (final review fingerprint 173e918d82a9c624fb14c5d893644dfd745cab41). Its two valid findings are mended: the tractor counterfactual now uses Agriculture's land-weighted horse share and rounded whole-sector jobs, including other farm templates; fitting waits now add a finite delay while preserving foreign construction in parallel. All public wait boundaries include the delay once. The reviewer withdrew a proposed PE14 royalty finding after checking research.json: PE14 has fee:false. That catalogue rule is preserved and tested. The final static review found no blocking money/save defect; execution checks remain the builder's responsibility.

The load scaffold is explicitly all-fitted until the saved founding and recipe decisions are restored. Otherwise the new constructor default chose historical recipes before loading an all-fitted save. Game.run lays the historical ladder, basket and founding counts only on its first initialization; it retains the constructor's original hand-on-dial/manual-rollover semantics and is idempotent. Game.newGame retains its existing autopilot/rollover behavior.

The save-flow fixtures whose original conditions need a floating rate, exchange gains, car ownership, fuel sales or a modern power shortage now state their all-fitted founding explicitly. Their assertions and quantities are retained. CurrencyRegimeCheck's supported-peg town likewise needs an actual chosen peg rather than the historical gold rule. ChildcareCheck's shared service/automatic-construction town keeps its calibrated equipment and wages explicitly; NewGameCheck's used-city setup needs insured mortgages before testing the reset into the historical default. ReadPathCheck's own historical city is retained.

The broad named check included FittingCheck, EquipmentCheck, Forms1900Check, HaulageCheck, RecipeCheck, ResearchCheck, DiscoveryCheck, HistoricalRecipeCheck, PostwarRecipeCheck, LicenceFeesCheck, PrizeCheck, SaveFileCheck, ReadPathCheck and NewGameCheck. The recipe/research/discovery/licence cases passed; PrizeCheck retained its known RT2-P lines. The save-flow premises above and the invalid positive-PE14-fee premise drove the next focused run. No assertion was relaxed.
