## changelog

### {next} — BS1, Bond settles to a fixed point, the bid tick and the buyback — 2026-11-12, ready, see docs/queue/BS1-notes.md

Corporate bond issues converge within 1e-14, and bond orders quote on a 1e-12 price tick, keeping the price-level and denomination twins equal within their bounds. Whole-piece buybacks take every offer in full, including typed faces within the quantity rounding, and cancel unpaid sales at their exact original proceeds. The bank offers its paid face, and cancelled proceeds leave its next settlement. The cancellation has its own legend and colour. Regressions cover typed faces, premium reopenings, curve consistency, the money audit and the bank's equity split through the following months. The ensemble scores 23/26. Jerus accepts A1's 10/16 against 13/16 seeds in band and FoodProcessingCheck's meat-bet line as path noise, with the latter's fixture repair queued as FPK1. KeptCheck's funding and household tooltip fixture transitions are traced and queued as KBF1 and KHT1.

## queued

| FPK1 | FoodProcessingCheck 5's meat bet sets its own farm-floor and ship prices so the Meat Works' profit sum is tested independently of the city's path. | [BS1 notes](docs/queue/BS1-notes.md#fpk1) |
| KBF1 | Cause stable auction coverage in KeptCheck 44's funding-page monthly reuse fixture and explicitly cause the first shortfall controls, preserving the 90% floor. | [BS1 notes](docs/queue/BS1-notes.md#kbf1) |
| KHT1 | Cause populated cash-grid cells to persist across KeptCheck 89's all-households tooltip month, preserving the tooltip identity and zero-loss assertions. | [BS1 notes](docs/queue/BS1-notes.md#kht1) |

## known-reds

FoodProcessingCheck | ...and the difference is most of what a Meat Works is worth | FPK1 | 2026-11-12 | The fixture reads the city's own meat prices, which any path change moves. 1.8x on BS1, 2.6x on main. Jerus accepts this line as noise.
DeathRecordCheck | the orphan section does not fill behind it, in any month of the two years | OX1 | 2026-11-12 | The family builder leaves children unplaced when a wave of adults is rehired. This line is already red on main and queued under OX1.
KeptCheck | a month's redraw after each choice hands back nine in ten of the page's nodes, or more | KBF1 | 2026-11-12 | Section 44 causes a cash gap but leaves auction coverage free to change. The previews become short and correctly add 19 response nodes while retaining 74. Main 6e369844 adds none.
KeptCheck | Household money, all of them: every tooltip a node had and still has is the same Tooltip after a month's kept redraw | KHT1 | 2026-11-12 | The fixture lets the Couple, no children, Senior professional cell fall below 0.5 households. cashCell correctly draws an empty dash and clears its tooltip. Main 6e369844 retains 94 of 94.
