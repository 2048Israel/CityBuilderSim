# GDP1 - Imports unsold at the month's end counted in that month's inventory

Jerus, in chat on 2026-11-12 at about 15:50:

> for gdp, most probably its timing, so just make it so its recorded in inventory no? that same month.

RT3 found seed 11's month 902 reporting GDP -431.689, with C 8,921.046,
construction 0, inventory -236.426, G 3,919.509 and NX -13,035.817.
Copper Refining's struck statement has 26,713.959 of imports and zero exports;
its later pending ledger holds 26,756.764 of exports and 4,621.895 tonnes of rod
shipped. See origin/batch/RT3:docs/queue/RT3-notes.md, "Ensemble and GDP finding",
and that worktree's target/checks/rt3-gdp*.txt diagnostics.

What a month imports, or makes from its imports, and has not yet sold or
exported when the month's accounts are struck must enter that month's
inventory change. The later sale or export draws that inventory down; the
same goods must never be counted twice. Establish whether the fault affects
Copper Refining alone or the common processing and extraction export path.

Done means a harness fixture that causes the cross-phase import/export
condition and asserts the expenditure identity in that month; the unchanged
LongPlaytest finding "negative monthly GDP"; a passing money audit; and a note
identifying GDP's model consumers (policy, central bank, test player included)
and whether the default trace moves. Build from main. This is a versioned
batch, with a provisional ship file pushed before the required gate dry run.
