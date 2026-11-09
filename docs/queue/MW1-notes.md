# MW1 - a city saved with the world file and loaded without it ({next}, 2026-11-09)

Session: CLAUDE_CODE_AUTO_COMPACT_WINDOW W=150000 (the orchestrator's env).
Branch batch/MW1 from origin/main 3b4f040c (WN4b merged).

## Decisions

1. The walk's trend gets a base level and a start month (fresh city: 1 and 0,
   so a fresh walk is unchanged). When the walk takes over from the US path
   (a load without the world file, or followUsPath(null) mid-game), the base is
   the level it is handed and the start the months run. Both saved, with a
   "last month was on the path" flag, as new slots at the end of
   toSaveArray(): an older save reads 1/0/false, as before; no format bump.

**Decision 2 (the save and the harness).** The three new values are slots at
the end of the world's array: `5+LEVEL_RING` the trend's base, `6+LEVEL_RING`
its start month, `7+LEVEL_RING` "last month was on the path" as 1/0. Three
fixture lines indexed that array from its end (RealDollarCheck's shock section
twice, CentralBankCheck.worldPricesFall once); they now name their slots, so
`LEVEL_RING` is package-visible. The harness is RealDollarCheck's section 8:
a bare world walked 600 months on a growing path, saved, restored with no path
and walked a decade must equal L times a walk from 1 (the step is linear in the
level and the base); the same array with the flag cleared (the pre-MW1 walk)
must fall short by at least half the gap `TREND_PULL` closes in 120 months; a
walk never on the path round-trips bit for bit; an array without the slots
reads base 1, start 0, flag off; a scratch town saved on the world file and
loaded on the literals holds the loaded level as its trend.
An older save carries no flag and walks to the flat trend as before: no
conversion code (CLAUDE.md, old saves), no format bump.

## The slice

One Sonnet slice (`slice`, xhigh): `WorldEconomy` (+33/-4: `trendBase`,
`trendStart`, `lastOnPath`; the hand-over at the top of the walk in
`advanceMonth`, after the path's early return; `trendLevel()` and the pull on
the base and start; the array at `8+LEVEL_RING`; `restore` and `reset`),
`RealDollarCheck` (+102/-2, section 8 and the shock's slots) and
`CentralBankCheck` (+3/-3, its slot named). Reviewed as a stranger's and
committed as 412548a9. The path is set to null only when the moneys are gone,
so a game on the world file never hands over; a fresh walk is bit-identical
(`1 * pow(..., (m - 0) / 12.0)`).

Section 8's figures: the level handed L = 3.3161; the first walked month's
`trendLevel()` equals L to the bit; a decade on, 3.5991 = L x 1.0853 (the walk
from 1), ratio 1.000000000000. The pre-MW1 walk (flag cleared) ends at 2.3545,
short by 1.2446 against the 0.5956 required. A walk never on the path
round-trips 60 of 60 months bit for bit, its trend 1; an array cut to
`5+LEVEL_RING` reads trend 1. (e): a scratch town on the world file, saved at
1.004733 and loaded on the literals, walks on with `trendLevel()` 1.004733 to
the bit, off the path, its level 1.008494 two months on.
