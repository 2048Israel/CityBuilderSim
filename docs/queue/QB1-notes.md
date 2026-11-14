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
