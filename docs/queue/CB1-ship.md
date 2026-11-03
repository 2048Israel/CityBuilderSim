## changelog
### {next} — CB1, the central bank's strictness measured over twelve cities — 2026-11-03, ready, see docs/queue/CB1-notes.md
CentralBankCheck 21's "very strict holds prices lower than Standard" was one probe city's path, re-picked six times as
batches moved it; it is now read over twelve founding shapes (`STRICT_SHAPES`: 40 to 43 houses, 3 to 5 months, the
playtest's seeds 0 to 11, the probe city's among them), each played to month 600 and twinned 240 months at both
settings: very strict under Standard at the median and in `STRICT_SHARE` (three in four) of them, with a fixture that
reverses cities by hand (green with one, red from four). On 0.9.8 ten of twelve hold, the median 0.42 points a year
under; two shapes run the other way, moved overnight by LD2 to DM1 while the probe city held. The very loose twin, the
fourth game, the stop and trust keep the probe city. CentralBankCheck takes about 153 s, 70 to 80 s more.
## pending
| CB1-L | Start CentralBankCheck first in the suite: name it in `AllChecks.LONGEST_FIRST` after MapCheck (153 s with CB1, the second-longest harness), so the suite's tail does not wait on it ({next}) | `AllChecks` is lane 2's file; CB1's ★2, recommended | [CB1 notes](docs/queue/CB1-notes.md) |
