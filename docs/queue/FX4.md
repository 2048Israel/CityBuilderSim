# FX4 - the currency running away in a new city (brief)

*2026-10-19. Jerus, playing a new city on 0.8.8: "idk why but 8.8 caused
something that causes massive appreciation of currency, like for some reason
parity was 0, the usd would go to near zero". The D$ appreciates massively
and the exchange rate falls toward zero.*

- **Reproduce it.** Find where parity is struck (`ForeignAccounts.setParity()`)
  and what can set it to 0.
- **Name the cause.** He last played 0.8.4; FR2 (0.8.5), FR7 (0.8.6), FR3
  (0.8.7) and FR5 (0.8.8) shipped since. Bisect over the tags with a short
  headless run, on the default world and on the Earth at Halifax, unless the
  cause turns out not to be a batch's.
- **A harness section** whose fixture causes the runaway on the bad build,
  then the fix in the model. Never move a harness's premise.
- The usual close: notes in `FX4-notes.md`, the full `Batch check.bat --maps`
  (main's only red is ScaleCheck's "...and it builds"), the trace
  re-baselined if the model moves, `--timing` last and alone, the version
  after main's, `batch/FX4` pushed as ready.
