<!-- readiness: provisional -->
## changelog
### {next} — AQ1, A3's one seed on GW1: the bust — 2026-11-17, ready, see docs/queue/AQ1-notes.md
GW1's seed-2 bust is off main's path, but its cause is on it: a price taker with no demand reading (Business
Services, Automotive, Manufacturing) held all its plant through years of losses the bank's shortfall desk lent it,
then, refused, sold a quarter of its biggest holding a month (on 0.9.132's AUTO2 seed 2, 6,000 posts in a year and
8% to 38% out of work). Such a sector (`Sector.bleedsWhileLent()`) now sells while the desk still lends it (its own
cash is not a lender), once its plant has run at an operating loss `DISTRESS_LOSS_MONTHS` running (a new saved
streak, old metal left out): a `BusinessInvestment.BLEED_MONTHS`-th of its biggest holding a month. A loss of interest
alone holds; overdrawn with no lender the quarter stands. Ensemble 23/26, hard 10/10, SHOCK K2 now passes; on AUTO2
the largest ten-month rise in out of work is at most 15 points (nine seeds at 20-26 on 0.9.134), 41 months over 20%
against 264. New: `DistressBleedCheck`. RN1-A closed: A3's one-month red, accepted at 0.9.63, passes on 0.9.132.
## queued
| QR1 | The distress rule's quarter sells plant that pays its running costs when the only loss is interest: its gate (`BusinessInvestment.planDistressRetirement`, overdrawn with no lender) reads the NET-loss streak, so a sector whose operating income is positive sells `MAX_RETIREMENT_FRACTION` of its biggest holding a month once interest has kept it in loss `DISTRESS_LOSS_MONTHS` running. Default playtest, merged with 0.9.134: Heavy Industry m2151-2155, 7 Steel Mini-Mills (910 posts) on operating income +$571M to +$201M a month, $283B owed, operating-loss streak 0; on AQ1's e17ee67f9: Manufacturing m1818-1820, 19 Machine Works (about 3,300 posts, out of work to 29.7%) on +$20M to +$125M a month, $9.1B owed. ★ The gate also reads AQ1's operating-loss streak: an overdrawn sector whose plant has paid its running costs (streak under the dial) sells no plant to the distress rule, its overdraft left to the bank's restructuring, which QR1 reads first; other ways: the quarter at `BLEED_MONTHS`'s pace instead; sell only plant that loses on its running costs; keep main's | [AQ1 notes §9](docs/queue/AQ1-notes.md) |
