## changelog
### {next} — XI1, the intervention bounded — 2026-11-06, ready, see docs/queue/XI1-notes.md
A purchase or sale of dollars now moves the D$ a bounded amount: 2% for each 1% of a year's output up to a tenth of a
year's output (`ForeignAccounts.INTERVENTION_LINEAR`), and logarithmically past it, so each further dollar moves the
rate less (`interventionMove()`; GD0 §4.5, Jerus 2026-11-02). Jerus's Rotterdam bought 4.5 years of its output in a
month and its D$ went ×11,600. The same purchase now moves it ×2.6, where the old rule gave ×8,100 (`InterventionCheck`,
on a bare currency and on a played town). Under the bound every figure is the old one to the bit. Past it, the central
bank's defence still takes back its share of a fall, and sells more dollars to do it. Trade's buy-or-sell card warns past
the bound: "This purchase is 4.5 years of the city's output". The default playtest is the same to the byte until month
2,119, where its war chest bought 11% of a year's output. The ensemble scores 23 of 26, as 0.9.23 did.
