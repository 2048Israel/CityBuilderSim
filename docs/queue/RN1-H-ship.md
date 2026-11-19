<!-- readiness: provisional -->
## changelog
### {next} — RN1-H, the homes' headroom a margin over demand — 2026-11-19, ready, see docs/queue/RN1-H-notes.md
Homes now plan capacity at demand plus `BusinessInvestment.TARGET_HEADROOM`, counting standing homes and those on site once. Previously the planner waited until demand exceeded capacity by that margin, then ordered only to bare demand. The planner's quantity and the credit check use the same target. HousingCheck causes equal demand and capacity, orders exactly the margin, and proves that planned and completed reserve homes stop further orders. DiscoveryCheck includes headroom in the capacity required by research jobs. The other planners' readings were audited and left unchanged.

## pending
| RN1-H-A | The shared headroom dial also names economic queue/output thresholds: maker expansion, Restaurants, Retail food and petrol, and LuxuryRetail wait for demand over capacity times 1.05 and size to bare demand; at 100 supply and 106 demand they order to 106, not a margin target of 111.3 ({next}) | Call for Jerus. Recommended: give these thresholds their own named dial and retain their tested hysteresis, including LuxuryRetail's explicit queue premise, until he chooses whether those markets should also keep spare supply. The orchestrator accepted homes only for RN1-H. | docs/queue/RN1-H-notes.md |
