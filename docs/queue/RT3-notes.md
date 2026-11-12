# RT3 — BusinessInvestment's retirement in a falling city

## Model and calls

★ Recommend pacing spare-capacity retirement by the retireable excess, with
MAX_RETIREMENT_FRACTION applied after keeping demand and TARGET_HEADROOM.
Round up to one whole building so a small, genuinely spare holding can leave.
The old rule paced the largest holding and could remove a modest surplus in
one month. Lost payroll then reduced demand for the next retirement.
This choice uses each sector's existing demand measure and needs no assumed
employment rate or additional saved history. Distress still follows its own
solvency test and pace.

LongPlaytest counts actual retired building posts over trailing twelve-month
windows against each sector's posts before the window began. Both retirement
rules contribute. It reports overlapping windows above one fifth, separate
episodes per sector, and the largest annual share. The counter lives beside
the plant counts, outside the layout watcher and map files shared with DM2.

InvestCheck exercises modest and whole surpluses, headroom, the building
granule, orders in flight, and the rolling counter. KeptCheck section 61
checks that its plain months retain four fifths of their opening posts.

## Checks

- Initial `./batch-check.sh --fast` compiled, regenerated the indexes and
  reported zero firm Stale findings.
- Named harnesses and the falling-city result are in progress.
