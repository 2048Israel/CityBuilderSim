## changelog
### {next} — LS1, the labour share by sector, a study — 2026-11-09, ready, see docs/queue/LS1-notes.md
Each sector's payroll over its value added, a year at a time, through the default playtest (seed 0 to m3996, seed 1
to m2400) and Earth's Rotterdam (to m2400), against BEA's compensation over value added by industry
(docs/studies/spec-labour-share.md; the probe, BEA's tables and the runs in docs/studies/labour-share/). Luxury
Retail's 2% (BEA's other retail 41.7%) is there from m240, and the mean wage keeps up with the price index in every
run, so it is not wages trailing prices; nor the markup (pieces bought at 41% of their price). It is a Boutique's
sales per post: 320 pieces a month of a $9,000 good sold near $22,000, for 7 posts. Seed 0's old city pays 10% of
its value added because Copper Refining's margin (its input at 52% of its sales) is 72% of it. Restaurants,
Construction and Freight sit at 0.3-0.5 of BEA's, each half under twice off. No model change; the trace is main's.
## queued
| CU1 | Copper Refining's margin, a study: why a refinery buys its input at 52% of its sales and keeps 35% as net income, the cathode's price over its concentrate's (treatment and refining charges) on the seeds that build refineries, then the batch that sets it; at seed 0's m3996 the margin is 72% of the city's value added (LS1, section 4) | [LS1's study](docs/studies/spec-labour-share.md) |
| LX1 | Luxury Retail's sales per post: payroll over the margin from about 2% toward BEA's 41.7% through the Boutique's coverage and posts together, the luxury piece's price held, checked by LS1's probe rerun; after CU1, whose margin sets the old city's demand (LS1, section 5) | [LS1's study](docs/studies/spec-labour-share.md) |
