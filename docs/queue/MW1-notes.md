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
