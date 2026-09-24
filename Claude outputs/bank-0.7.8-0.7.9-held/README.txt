BATCHES 2 AND 3 OF THE BANK AS A BUSINESS - HELD, NOT DEPLOYED (2026-09-23)

This folder is a safety copy, not part of the build. Nothing under
"Claude outputs" is compiled. The game on this PC is 0.7.7 (tag 0923e).

What is here: every source file that batches 2 (0.7.8) and 3 (0.7.9) change,
at their paths under src/, taken on top of 0.7.7; and in notes/ the two
briefs and the implementer's two records. The five interface files are in
ui/ as well: the copies under src/.../ui/ sit one folder too deep for the
cloud to read back, so the ui/ copies are the ones it checked.

  0.7.8 - the bank's capital: a loss allowance, its own capital target and
          band, a payout policy, lending that tightens with capital, its own
          shares issued under the target and bought back over it.
  0.7.9 - the Bank tab rebuilt: "The bank at a glance" (status sentence,
          scorecard, rate ladder), then Profit, Lending, Funding,
          Capital & owners and History.

Why it is held: with batch 2 the bank fails 85-92 times over eight seeds
against 0.7.7's 4, because a whole sector is one borrower. Jerus's call:
  A - ship as it is
  B - keep 0.7.7's "keep all its profit", ship the rest
  C - (recommended) a sector's default becomes partial
Batch 3 works with any of the three. The docs passes for both still have to
run. The claude.ai project's todo.md, section 4, has the full entry.

Do not copy these over src/ by hand: they are built against 0.7.7 exactly,
and the deploy from the cloud re-checks each file before writing it.
