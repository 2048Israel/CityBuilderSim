# LB4 - Rankings: the city in play beside its own slot, and a lighter read of the saved cities

Queued 2026-10-31, settling LB3-D and LB3-R the recommended way, on Jerus's
"Take all (Recommended)" (BP1 item 9). Both are the board's saved cities
(`SavedCities`, `RankingsScreen`); LB3's notes §8 and §9 have the findings.

1. **The city in play beside its own slot (LB3-D).** A city loaded from slot
   3 also shows slot 3 as a saved row, the same name, "slot 3", the city as
   last saved, and at Your age it nearly ties with itself. The game does not
   know which slot a city came from. Mark a slot whose founding (name and
   world seed, read from `Game`'s founding) is the city in play's as "this
   city, as saved", and leave it out at Your age.
2. **The memory a read takes (LB3-R, the gate's review, PLAUSIBLE).** Each
   history is parsed whole into boxed `List<Double>` (a 26 MB file perhaps
   100-150 MB at its peak) and 58 of its series are kept, boxed, for up to
   ten slots (about 8 MB a 500-year slot), with no `-Xmx` set. Parse with
   Gson's `JsonReader`, skipping every series but the kept ones, and keep
   those as `double[]` (under 3 MB a slot, no full parse). Measured by
   UiProbe's heap with ten large saved cities in a scratch save folder
   (`GameFiles.scratch`), never slots 1-9.

RankingsCheck asserts both: a scratch slot with the city in play's founding
marked and out at Your age, one with another's not; the kept series equal to
the whole parse's, value for value. No model change; the trace does not move.
