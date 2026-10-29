## changelog
### {next} — BK1, the bank's equity split and its interest margin — 2026-10-29, ready, see docs/queue/BK1-notes.md
Issue #5, Jerus's San Francisco city. The bank's equity no longer drifts from its two parts: a line of the city's
paper keeps one pool of discount shared by face, and reopened at another price while households, firms, the world or
the central bank hold part of it, the pool re-blended and the bank's unearned discount moved with no cash or income
(found at m260 of 0.8.52's San Francisco playtest, +$0.856k; $212.5M in his city). The re-blend is now its gain or
loss on the city's paper, booked at the settle and the central bank's add-on (HoldersCheck 12). His save keeps its
$212.5M and its red card (no conversion) but no longer grows. The interest margin is on earning assets - the book
and the reserves, which earn the policy rate - month and year, the year from the saved sheets: San Francisco's
9,972% was reserves 700 times the loans (BankCheck 13). The trace is main's: no ensemble.
## pending
| BK1-L | The island label on the land office map ({next}): "Island 2 · 5.37 km² · not linked: needs a port here (none has one)", some 70 characters of amber mono whose only ground is a 3-pixel dark stroke round the letters, drawn over the landmass's own amber hatch and tint (`MapView.islandChips()`), so over busy ground it is hard to read (issue #5) | Recommended: a dark rounded plate the width of the words under the chip, and two lines at the near view (name and area; why not linked). The other way: the name and area only, the reason on hover | [BK1-notes](docs/queue/BK1-notes.md) |
| BK1-C | A fixture for the central bank's add-on into a reopened line ({next}): `Game.rollCentralBankAtIssue()` books the bank's re-struck discount through the same helper as the settle, which HoldersCheck 12 causes and proves; no harness drives an add-on into a reopened line (CentralBankCheck 19 reaches only fresh pieces) | Recommended: a short CentralBankCheck section, the central bank's par alone rolled into a benchmark line the households hold part of, asserting the movement and the split whole, in the next check batch; nothing waits on it | [BK1-notes](docs/queue/BK1-notes.md) |
