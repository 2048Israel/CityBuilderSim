# LX1 - Luxury Retail's sales per post (2026-11-17)

The brief is LX1's row in `QUEUE.md` and [LS1's study](../studies/spec-labour-share.md), section 5; CU1's ★6 (LX1
waits for CU2, whose prices set the old city's demand). Cut from main ec481b4a (0.9.132, after CU2 and TQ1). A model
batch, built by an Opus session (Codex out of plan to 2026-11-17 19:35).

## 1. Measured first, on main's path (ec481b4a), before the balance moved

**How.** LS1's probe (`docs/studies/labour-share/LabourProbe.java`, unchanged) on the default playtest, played as
LS1's `run.sh` plays it (a copy of `LongPlaytest.java` with the probe's line after `g.simulateMonths(1)`, the city in
a `TempDirs` folder), with a second read-only probe beside it for the shops themselves (`LuxuryProbe`, kept in
`target/lx1/probe-src/`, never committed: the templates standing, coverage, the queue and the pieces served over the
year, the margin, the posts). Seed 0 to m3996, seed 1 to m2400, Rotterdam (`--earth Rotterdam`) to m2400, as LS1 ran
them; LS1's `report.py` over the CSVs. Main's own default playtest (the trace's reference) exits 1 on TQ1's finding,
"households with no home", 54 times in months 692-745; the Rotterdam run exits 1 on "industry stands farther out than
the homes" at m2400. Neither is this batch's.

**Payroll over the margin** (LS1's share: payroll over value added, the margin less power, water and upkeep, a year
summed), and a post's month of margin in mean monthly wages ("mrg/wkr"; BEA's other retail, 4A0: 41.7% and 2.4):

| run | m240 | m600 | m1200 | m2400 | m3996 |
|---|---|---|---|---|---|
| seed 0: share | 6.8% | 0.6% (m720) | 1.4% | 0.8% | 0.8% |
| seed 0: mrg/wkr | 15.6 | 136.5 (m720) | 68.1 | 112.7 | 117.9 |
| seed 1: share | 13.1% | 1.7% | 1.1% | 0.8% | |
| seed 1: mrg/wkr | 7.9 | 54.2 | 84.2 | 112.4 | |
| Rotterdam: share | 3.4% | 0.7% | 1.3% | 0.6% | |
| Rotterdam: mrg/wkr | 29.4 | 123.1 | 63.9 | 154.6 | |

After the founding the share is 0.5-1.7% (seed 0's years m480-m3996: 0.5-1.4%, median 0.7%), lower than LS1's 2.2%
before CU1 and CU2, and a post's month of margin is 54-185 mean wages, 20-75 times BEA's.

**Sales per post.** Pieces served a post a month: 17-43 (seed 0: 40.7 at m600, 17.0 at m1200, 37.3 at m2400, 30.9 at
m3996; seed 1 18.0-29.9; Rotterdam 19.8-42.7), against the Boutique's 320 over 7 posts, 45.7, at nameplate. The
shops serve 37-93% of their coverage (u) at a struck margin of 2.10-2.81. A piece lands at 1.6-3.4 mean monthly wages
(seed 0, every 240 months; 2.4 at m3996), so a post's month of margin at the margin struck is 4 mean wages a piece.
No Department Store stands on any of the three runs at any month read: the Boutique is the luxury shop.

**The size, and what BEA's share would make it at today's demand.** Luxury Retail's margin is 10-30% of the
business sectors' value added on seed 0 (11.1% at m2400, 25.0% at m3996; seed 1 8-11%; Rotterdam 9-19%), with
0.2-0.8% of their workers. At BEA's 41.7% of that margin, paid at the shops' own pay a worker, the sector would employ
12% of the business sectors' workers at seed 0's m2400 and 30-35% from m3120 on (seed 1 9-10%, Rotterdam 7-19%):
LS1's warning ("twenty times the posts at today's demand would take most of the city") holds after CU2, and the
factor is about fifty, not twenty.

**Why the demand does not fall with the price.** The households' luxury demand is a budget
(`HouseholdBalance.luxuriesWanted()`: the budget over the price, `luxuryBudgetOf()`: the wealth term and a share of
the surplus), so the spending is the same at any margin the shops can serve, and the margin's value added is the
budget times (1 - 1/margin). The share is therefore (posts / coverage) x (pay a post / landed) / (u x (margin - 1)),
and the number of workers it makes is fixed by the budget and the share, whatever the Boutique's coverage and posts:
only the margin moves it, and only between the planner's settling point and `MARGIN_CEILING`.
