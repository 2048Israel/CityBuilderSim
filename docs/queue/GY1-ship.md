<!-- readiness: provisional -->
## changelog
### {next} — GY1, the graph's GDP by the year, and debt to GDP — 2026-11-20, ready, see docs/queue/GY1-notes.md
The graph's "GDP" line is now a rolling year of output ("GDP (yr)", nominal, the twelve months summed the way real GDP's
year is, drawn from the end of the first year), so it reads on the same axis as the public debt; the month it used to
draw is still there as "GDP (month)" under the real and per-capita lines. A new line, "Debt to GDP", is the public debt
over that year of output as a per cent (205% is a debt of two years of output), the measure the debt limit reads,
blank where there is no year of output behind the month. A pin, a preset or the header GDP tile's door that named GDP now
draws the year. Both figures come from `YearBook` (`gdpYear`, `debtToGdp`) and are derived from the history already kept:
no saved field, no save format change. `YearBookCheck`'s new section builds the history by hand and asserts the
twelve-month sum, the first-year rule, and the ratio at chosen months with a year of nothing giving no figure; each
fails with the getter swapped for the month. The default playtest's trace is main's, byte for byte (eight files, 2,405 months).
