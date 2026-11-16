# R1d ? the 1900 city switched on

Started from main 32869da28 (0.9.123). In progress; the final checks and retained gate provenance follow below.

## Design calls

- ? New cities use the historical founding by default, including the 1901 wage ladder and dated arrivals/basket. The founding screen also offers All fitted explicitly; existing saves keep their saved flag. Reason: the normal played path must exercise research, while comparisons and existing all-fitted cities retain their meaning.
- ? Scrapping cancels the oldest on-site fitting of each piece on the scrapped template, immediately at the stack removal. No refund: goods were delivered and the builders accepted the contract at the order; cancelling a paid installation is a sunk investment. Partial work cannot migrate to another building. The fixture removes an actual partly fitted bakery while another remains unfitted.
- ? Builders receive the actual treasury payment, including when SHORTFALL_DUST admits a quote slightly above the available cash. The fixture binds the advance ceiling and puts cash strictly inside that gap.
- ? Steam's share survives a grid brownout; the electric share alone is multiplied by the grid ratio. Other staffing, water, road, health and freight constraints still apply. Reason: burning coal in a factory does not depend on grid delivery.
- ? Electric-drive installation is estimated at 10 BUILDING_WORK points per tonne of motor, shafting and switchgear, rounded up (Equipment.DRIVE_WORK_PER_TONNE). This replaces one tenth of a factory's unrelated construction work. The former Bakery estimate was 70 points for 0.3 tonnes; the new one is 3. Motor mass, life and energy conversion are retained. This remains a balance estimate for Jerus.
- ? The tractor's gain includes the composed process's saved horse feed and extra hands, less its fuel, and patent rent on the same physical comparison. The previous recipe-only comparison priced the added fuel alone.
- ? Carters retain nameplate sizing, like the fleet; teams remain hired with capital outside the city's goods ledger. Reason: changing hired capacity to spot employment or introducing horses/wagons as goods changes another market and employment rule; measurements on the historical city will show the scale before a new contract is chosen.
- ? Fittings quote the builders' sales tax, net their suppliers' local input credits, and record the owner's capital purchase. Reason: the same capital VAT rules as building work must apply when the fitting action is the normal path. Lead-time output now deducts fitting work after repairs, as actual site output does.

## Pending rows read

- R0e-R: the historical basket is active and its shares are drawn on Household money. Cordwood and household utility billing remain open: there is no firewood good or billed replacement heat/power. Retain the sourced lamp/coal quantities and present-year endpoint; adding those markets is a separate gameplay batch.
- R0d-R: the 1901 ladder and dated arrivals are active on the normal path. Retain the elite estimate, 1901/1913 knot, interpolated 1953?2021 line and present licence shares: the switch supplies no new historical observation. Growth and lab affordability measurements belong below.
- R2-R: the tractor now values the horse form's feed and hands beside the recipe's fuel cost.
- R1c-R: binder/tractor composition is already on main from R2d. Medical rows remain R2f's; retain the steel/coke/pulp estimates pending the measurements below. PULP remains one good, so groundwood's quality distinction remains open.
- R1b-R: default historical founding and a founding-screen alternative, fitting VAT, and lead-time deductions are included. Shell repricing stays open: equipment is chosen at completion while contracts and planners price the order. Removing equipment costs without freezing and saving the order's equipment would charge one plant and deliver another. Recommend a dedicated saved order-equipment contract change; no pretend discount is applied here.

## Checks

First fast compile passed. Named mechanics checks are running; no local ALL run is used.
