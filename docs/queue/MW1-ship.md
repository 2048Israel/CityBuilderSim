## changelog
### {next} — MW1, a city saved with the world file and loaded without it — 2026-11-09, ready, see docs/queue/MW1-notes.md
A city saved on the US's price path and loaded without `world-1.bin` no longer walks its dollar back to the flat
trend of 1 (WN4-W): the walk's trend now starts at the level the path hands it, at the month it takes over, so the
dollar carries on from where it stood, inside the walk's own spread, instead of losing half the gap in about ten
years. `WorldEconomy` keeps the trend's base, its start month and whether the last month was on the path, three
new slots at the end of the world's saved array (no format bump: an older save reads 1, 0 and off, and walks as it
did). A city that never had the file walks as before, to the bit. `RealDollarCheck` section 8 proves the hand-over
on a bare world (a decade on, the level handed times a walk from 1) and on a scratch town saved on the file and
loaded on the literals; the pre-MW1 walk falls short of it by more than half what `TREND_PULL` closes in ten
years. The default trace is byte-identical, so no ensemble.
