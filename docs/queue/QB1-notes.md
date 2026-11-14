# QB1 — The test player borrows at the quoted rate

Started from main `916b7c16` (HW1, 0.9.104), 2026-11-14.
Brief: CC1-S-notes section 7; Jerus took all recommended decisions.

★ Compare the sale's actual home and foreign quotes by default, retaining
`-Dplaytest.quotedBorrowing=false` as the previous-path counterfactual.
The existing quote selection and the `ABROAD_SHARE` threshold stay intact.
No save format or player UI changes are needed.

The starting known-red registry contains no QB1-owned rows. Existing PX1,
BF1-M and DM3-R map/planner failures remain separate from QB1's findings.
WN5d's CityLand, LandMap, LandMarket, LandSellers, ui/MapView and
LandNationsCheck are outside this batch's edits.

Validation and fixture attribution are in progress.

## The moved fixtures

★ ScaleCheck's own-town copy moves from month 172 to month 225. The old
borrowing rule passes at 172 (`target/checks/qb1-scale-main-rule.log`).
With quotes on, the savings and deposit misses are both 0.0355, over the
unchanged 0.016 and 0.026 floors (`target/checks/ScaleCheck.log`, initial run).
The temporary scan used the same town, foreign-rate pin, death hold, scale
copies, free sectors and held-sector tracking as the harness. Months
168–176, 180, 190, 200, 210, 160, 150, 165 and 155 did not qualify.
Month 220 had no numeric red but missed a million-building interest-order
premise, so it was rejected. Month 225 qualifies: 10,524 people, growing
over the previous year, ground free and affordable rents for all six months;
both free cities exercise the large-order and interest premises. The held
copy's worst savings miss is 0.00893 and deposits 0.00745. No assertion,
threshold, plan or mechanic changed. Scan logs: `target/checks/qb1-scale-probe{,2,3}.log`.

AutoBuildCheck 7 already causes its shortage on current main (FR8/IC1/GW1):
turn the pass off while clinics on site open, then retire the standing
general-care clinics. With quoted borrowing it reaches m64, 2,512 people,
zero care buildings, supply 1,200 versus demand 2,512. Its first refused
order is its own general care, held for DEBT with the shortfall SHORT;
the pass reaches all 14 later measures, and restoring the limit recovers
that service's own order one month later and grows the city. All existing
assertions pass. Raising the road slack as the old brief suggested is no
longer needed; preserve this explicit shortage and its stronger recovery law.

OrderSearchCheck's existing month-500 fixture and complete harness pass with
the switch on; retain it, since re-picking a qualifying fixture adds no
coverage. The initial focused logs are retained under `target/checks/`.
