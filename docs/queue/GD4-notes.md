# GD4 — Gold mines by geology

## Built and calls

Gold is the eighth resource, after FOREST, and Gold Mining is the twenty-fifth
sector, after Wood Products. Geology supplies the brief's W_GOLD weights.
EarthTerrainCheck 13 measures Z_GOLD at 0.60963879, stored to four places as
0.6096. Existing Earth and world files supply all required inputs.

★ The permanent mine id is 152. Main's R4 already owns ids 148–151 for research
labs. The orchestrator confirmed 152 through cbs-bus on 2026-11-13. No ids move.

★ Keep the brief's field density of one per 500 km² and calibrate the site to
the gold/copper flow ratio. The proposed 25 t/site gives 126.2 kg/km² at 2.524
sites/field, while the brief's target is 54 kg/km². The orchestrator chose the
flow target. Each gold site therefore contains 10.695652 t of fine metal,
stored as troy ounces. Copper's density and site mass times 3,280/23,000,000
give the same result.

★ Main has no CHEMICALS good. Following the orchestrator's decision, the mill
uses existing ACID as its reagent stand-in, 30 t per 30,000 t ore milled,
alongside 90,000 litres of diesel. A separate cyanide good would need its own
balance decision. The building's other figures are estimates, with 120 posts,
5,500 kW, 476 water units and consumables of US$1.72M/month at today's level.

★ Unworked recovered grades average 12 g/t on shields, 10 in fold belts, 6 in
rifts, 4 in basins and 8 elsewhere. A deterministic field draw varies each
by ±25%. Generated ground averages 10 g/t. Grade falls linearly with its
depleted fine-metal fraction to 20% of the original grade. The model integrates
recovery over fixed ore throughput, including transitions between fields.
Saved acquisition-order depletion already supplies its age.

Gold is a stockable fine troy ounce with its own month's nominal GoldPrice
and spot exchange rate. The import/export band adds/removes 0.5% each side,
with 0.25% freight and 0.25% dealer margin. Nominal gold avoids a second
world-price-level multiplication. The mill offers its new output directly.
Abroad it follows the export floor. Under Gold the mint pays M for the offer,
keeps the fine ounces and issues matching base money. The sector books a
domestic receipt and MoneyAudit names the new money. The accepted offer is carried in SectorState.extras until the next strike,
when the sale reaches cash. The vault receives the ounces and CentralBank
issues its payment inside that audit window. The original offer remains
payable if the player leaves Gold between production and settlement. Monthly
and lifetime payments persist in CentralBank's existing saved array.

SAVE_FORMAT and OLDEST_LOADABLE_FORMAT both rise from 51 to 52 because CityLand
rows widen. Older saves are refused cleanly. There is no conversion code.

HW1-owned files, including LongPlaytest, remain outside this diff.

## Research

USGS MCS 2026 confirms 2024 gold mine production of 3,280 t and a reserve
total of 66,000 t. MCS 2025 confirms copper's 2024 estimate of 23 million t
and reserves of 980 million t. The flow ratio is 0.0001426087. The reserve
ratio is 0.00006734694, 47.2% of the flow ratio. This confirms the brief's
comparison and its selection of production to scale the simulated ground.
The precise field frequency, grade draws and depletion law remain estimates.
The publications and the USGS dataset's CC0 status allow commercial reuse.
[Gold and copper, MCS 2026](https://pubs.usgs.gov/periodicals/mcs2026/mcs2026.pdf),
[copper, MCS 2025](https://pubs.usgs.gov/periodicals/mcs2025/mcs2025.pdf),
[USGS release rights](https://www.usgs.gov/data/mineral-commodity-summaries-2026-data-release).

The geology classification follows the USGS deposit models. Model 36a covers
gold veins associated with greenstone belts and accreted margins. The class
weights are estimates drawn from these settings. [USGS Bulletin 1693](https://pubs.usgs.gov/bul/1693/report.pdf).

## Verification

- ./batch-check.sh --fast compiled the model and regenerated indexes.
- ./batch-check.sh GoldMining BuildingData passes both harnesses. GoldMiningCheck
  exercises exports, reagent purchases, exact depletion, deterministic geology
  grades, saved mint offers, vault ounces and issuance. The mint is reached in
  24 played months, with all 24 money audits closing.
- The initial EarthTerrainCheck measured gold Z at 0.60963879. Its provisional
  constant was then replaced by 0.6096. Its full final verdict is pending.
- The final pick, ensemble and retained gate provenance are pending.

The first full pick found Gold's missing far-map symbol in MapCheck's existing
resource-mark assertion. DepositMarks now supplies Au. The same run found a
Government Overview node-allocation ceiling in KeptCheck section 68. Its
classification is pending a targeted run on unchanged main 97eaa608.

Review found that bullion's spot exchange quote must survive a reload,
separately from ordinary goods' lagged, inflation-adjusted quote. Markets.State
now carries that gold quote and GoldMiningCheck tests the restore, including
leaving ordinary goods at their handed exchange factor. The accepted mint
offer is also checked at M and through a change from Gold to Float.

An early ensemble attempt at 8a3d1887 was stopped using its own run label
after these save-quote and map findings. Its partial output is superseded.
The final model will receive a fresh 80-run ensemble.

★ The mint's accepted payment also follows currency redenomination, while its
fine ounces keep their unit. The sector's standard reset hook clears an old
offer. Both paths have dedicated assertions beside the saved-offer check.
