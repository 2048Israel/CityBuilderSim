<!-- gate-run: /tmp/cbs-gate-FB4-AvAhuTIn -->
## changelog
### {next} — FB4, three gate reviews' follow-ups — 2026-11-17, ready, see docs/queue/FB4-notes.md
GV1-R: the wealth band's notice leaves a band only 10% past its bound (`GovernmentView.BAND_MARGIN`), so wealth on a
bound no longer posts a notice a month (241 over 243 readings before, 3 now, InboxCheck); the government's debt counts
the central bank's advances and the linked bonds' indexation; the buyback advice keeps its quote while nothing it
reads has moved. WG2-R: the brake migration applied is kept and saved (`Migration.getLastBrake()`), and the People
screen's slowing word reads it; SaveFileCheck reloads a city at 239 and 240 months recorded. RB1-R: the fund's idle
cash is read after last month's toRaise is paid, the idle figures count what was paid, and FundCheck 9e counts the cap
sale (230 of 240 months) and the idle transfer (90) firing at the default dial, the holding never past the fixed
point. The playtest moves from m1322, through GV1-R's debt in its player's January read; the ensemble passes
(docs/ensemble/FB4.md: 24/26, hard 10/10, F1 as the baseline). Queued: PK1 (two fixtures that pick a month) and RB2
(the fund's due struck on a value that counts the toRaise).
## queued
| PK1 | ScaleCheck's copy month and FundCheck's 73 months cause their conditions at any month, not at a picked one (WG2-R item 5, filed back by FB4) | [PK1](docs/queue/PK1.md) |
| RB2 | The fund's withdrawal is struck on its value less last month's toRaise, in the model and the preview; moves the playtest (found in FB4) | [RB2](docs/queue/RB2.md) |
| SX2 | Why the cells never buy shares from the world on LX1's path (ShortSellingCheck 7: 0 months in 1,000 on 0.9.138, 63 on 0.9.137); a model fix or a fixture that causes the purchase, the count's premise kept (found at FB4's dry run) | [SX2](docs/queue/SX2.md) |
## known-reds
ShortSellingCheck | the cells traded shares with the world in the real run | SX2 | 2026-11-17 | LX1 (0.9.138): section 7's 1,000-month city never buys shares from the world (0 months; 63 on f18b10537, before LX1); red on main 886bd92dc and c471e961f with FB4's figures to the cent, not FB4's code
