# GD4 — Gold mines by geology

## Built and calls

Gold is the eighth resource, after FOREST, and Gold Mining is the twenty-fifth
sector, after Wood Products. Geology supplies the brief's W_GOLD weights.
EarthTerrainCheck 13 measures Z_GOLD at 0.60963879, stored to four places as
0.6096. Existing Earth and world files supply all required inputs.

★ The permanent mine id is 152. Main's R4 already owns ids 148–151 for research
labs. The orchestrator confirmed 152 through cbs-bus on 2026-11-13.

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
rows widen. Older saves are refused cleanly. 

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

The gold plan values the entire recovered offer at the mint price under Gold,
including the saved dealer-wedge difference on the reference output. A rich
1900 field's projected return and the same mill's loss at a worked-down grade
are checked directly against the planner.

## Review REV-GD4-1

The orchestrator's review of 3ef0676d found that the mint's domestic purchase
was absent from expenditure GDP, a load without WorldMarket cleared the
independent gold band, and gold stock was absent from NationalAccounts.HELD.
All three receive specific GoldMiningCheck assertions.

★ New gold bought by the mint is an acquisition of valuables in investment
before monetization. It has its own NationalAccounts component and saved
slot after the held-goods baseline. HistorySave already records total
investment, so that series includes it. Government's Output card shows the
new component when nonzero. Imported reagents still subtract through net
exports. The money audit and the expenditure identity both close.

BEA's handbook describes the international SNA acquisition-of-valuables
treatment alongside the United States' different net-export adjustment for
gold. The game uses the former. This is accounting-method evidence from a
US government publication, with no numerical data copied. [BEA NIPA Handbook,
chapter 8](https://www.bea.gov/system/files/2019-12/Chapter-8.pdf).

Gold's physical inventory is appended to HELD. Its saved bullion spot quote
and independent GoldPrice band survive the no-world-market load path,
including playOnLiterals. The harness invokes that load hook with a 1934
quote so a fallback to the 1900 literal fails visibly.

PriceLevelCheck also caught cancellation of its counterfactual nominal-unit
override in the new gold good. The actual CPI ratio now has its own constant.
Good.GOLD's literals pass through the ordinary price funnel, and Game's gold
quotes honor the nominal-unit override. At the normal historical factor the
multiplier is exactly one. The price-level twin's premise stays intact.

Catalogue checks now cover the added industry's group and reagent demand,
the tenth extraction/processing page, and the mandated wider save records.
These extensions preserve the checks' original behaviors for existing rows.

## KeptCheck's Government Overview finding

★ File the narrow 40-of-384 allocation assertion to queued KS1-UI, as the
orchestrator directed after identifying the nodes. Unchanged main 97eaa608
passes this Government Overview section. Its targeted Kept run reaches the
later research sections before its harness timeout, with the already known
KF63 line. The control covers the relevant prefix.

The GD4 prefix's page signatures remain equal. Temporary diagnostic copies
under target/gd4-trace-source record every new node when the Overview creates
40, preserving the actual KeptCheck prefix and stopping after that section.
Their log is target/checks/gd4-kept-nodes.log. The forty nodes are

- 11 for Principal repaid in the cash bridge.
- 11 for Raised by issuing paper in the cash bridge.
- 11 for Today's dials, not the month's in the earned-to-budget bridge.
- One past-bar Region for Paid out.
- One Sales tax ring Arc and five nodes in its ring key.

All are rows and graphics main already draws. The Government Overview, bridge and budget methods, Pieces and Kept
are unchanged. The later Output-card addition only displays mint investment
when nonzero. The new path exposes old scopes being recreated when those
ordinary fiscal rows return. Preserve the 10% ceiling for KS1-UI's repair.

The first full pick also identified existing catalogue assertions that assumed
Wood Products was the final key, only copper mines bought ACID, and bullion
shared ordinary goods' freight fraction. Those premises now include the
appended sector and gold's explicitly specified band. The old goods' delivered
prices and freight expectations remain asserted.

Focused current-model checks now pass GoldMiningCheck, GdpCheck, BuildCardCheck,
PriceLevelCheck and SaveFileCheck. The price-level twin reports no nominal
mismatches after the bullion funnel repair.

## Changed assertions for the delta review

| Check and assertion | Main's form | GD4's form | Reason |
|---|---|---|---|
| BuildingDataCheck, buildings on sites | 16 | 17, with the Gold Mine & Mill's one ANY gold site and dig of one | The appended building stands on gold. |
| BuildingDataCheck, the generic site shape | Oil's one-site shape after the typed minerals | Gold gets an explicit id/resource/ANY/one-site/one-dig branch, then the existing oil test | Distinguish the new resource's catalogue contract. |
| BuildingDataCheck, consumable buyers | The set of 41 existing ids | The same set plus GoldMining.MINE_AND_MILL, 42 | The mill buys its fixed-throughput consumables. |
| BuildingDataCheck, historical years | Existing id/year map | The same map plus id 152 at 1900 | Gold mining and milling are available at founding. |
| BuildCardCheck, Industry groups | Existing ordered group list | That list with Gold Mining appended | The new sector's building has its own group. |
| BuildCardCheck, smelter-acid outlets | One buyer, Copper Mining, at its input nameplate | Two buyers, Copper Mining then Gold Mining, each at its input nameplate | The approved reagent stand-in creates the second buyer. |
| BuildCardCheck, chain-page coverage | Nine extraction/processing pages with all goods' buyer lines | Ten pages with the same per-good buyer-line assertions | GoldMining is the added extraction page. |
| CopperRefiningCheck, acid users | Copper Mining | Copper Mining and Gold Mining | The new mill uses ACID. Other maker/user sets retain their entries. |
| WoodProductsCheck, registry position | Index 23 after Forestry and the final key | Index 23 after Forestry, followed by Gold Mining, with keys.length == at + 2 | Append the twenty-fifth sector. GoldMiningCheck separately requires 25 keys and Gold Mining last. |
| GdpCheck, HELD's existing tail | Twelve trailing goods from CRUDE through TRUCKS, with their stockability checks | Those twelve retain their order, followed by GOLD and its stockability check | Append the new stockable commodity. The every-held-good coverage assertion stays as written. |
| TradeCostCheck, delivered-price table | Every pre-GD4 traded good's founding literal, bit-for-bit | The same rows plus gold's founding quote from the statute and declared freight/margin shares | Extend the exhaustive commodity table. Existing rows keep their exact-price comparison. |
| TradeCostCheck, freight fraction | Every pre-GD4 two-sided band has freight at 75% of its half-wedge | The existing bands retain 75%; GOLD uses the ratio of its declared freight to freight plus dealer margin, 50% | The brief specifies gold freight and margin at 0.25% each side. The original 1.5%-of-half-wedge tolerance is unchanged. |
| SaveFileCheck, seller rows | OFFER_FIELDS equals 29, round trip and seller-sidecar assertions | OFFER_FIELDS equals 31, with the same round trip and seller-sidecar assertions | The eighth resource adds a site count and amount. |
| SaveFileCheck, relief sidecars | Centre 24, holding 31, offer 29, with the same lengths in their arrays | Centre 26, holding 33, offer 31, with the same array checks | The resource row widens in the mandated format break. Relief keeps its parallel arrays. |
| EarthTerrainCheck 13 | The six existing Z values and historical replays | Those assertions plus measured gold Z within Z_ROUNDING | The new geology weight needs normalization on the PC's full file. |
| GoldMiningCheck | Absent | New seven-section harness with production, contracts, GDP, money, saved state and regime-transition assertions | The new mechanic's required harness. |

PriceLevelCheck, the same-band reload assertion, the money-audit tolerance,
the Government Overview's allocation ceiling and the remaining harness
fixtures keep their premises and tolerances.

The mine plan's displayed quantity now uses Resource.unit() in its existing
format string, so gold reads in troy ounces. Existing tonne resources retain
their wording. This is a display-text correction over the ensemble's
cd3c3abd arithmetic model.

REV-GD4-2 retains WoodProductsCheck's exact terminal-count assertion by
extending it from at + 1 to at + 2 alongside the appended GOLD key. The
Changed assertions table was committed in 47d5be25 and includes that condition.
The ensemble's arithmetic model remains cd3c3abd.

## Visual and compute checks

Rendered Government's Output page from a city with an active domestic mint
purchase, through the normal KeptBench page and its first-draw preparation.
The inspected image is target/checks/gd4-gold-output.png. Investment shows
construction, stock and the new gold acquisition together. The added line
fits its card and the four expenditure cards stay aligned. The fixture books
41.7884607368 thousand D$ of new gold, 396.95628 thousand D$ of total
investment and 146.37432 thousand D$ of GDP, with both identities checked.

EarthTerrainCheck's final-pick full-file gold normalization is green at
0.60963879. Its loaded-PC cells pass measured 1784 ms first and 1620 ms again,
scored SOFT against the unchanged 1000 ms budget. The earlier focused run
with eight resources measured 362 ms first and 311 ms again, alongside the
study's 370–450 ms. Three ensemble JVMs and the checks share this PC during
the final run. Timing remains informational for the nightly judge.

## Final ensemble

GD4-verified completed all 80 runs on the cd3c3abd arithmetic model in
59.6 minutes, using three JVMs. It scored 24/26, alongside 0.9.98's 24/26.
All ten hard M1/F1 criteria pass. Money-audit failures are zero and every
policy has zero finding-bearing runs out of sixteen. There are no missing
or short runs and no changed verdicts. The retained score and raw runs are
under target/ensemble/GD4-verified. Its baseline is committed as
docs/ensemble/GD4.md, ready for the publisher's version name.

The earlier partial snapshots at 8a3d1887 and 3ef0676d were stopped during
review and superseded by this complete run.
