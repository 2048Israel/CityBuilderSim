# FS10H — zero cash must not net a floating-point residue of central-bank par

Companion to the shipped FS10 sweep, queued under the orchestrator's HT1
end-to-end direction of 2026-11-22. Do not reuse the merged FS10 batch.

CentralBankCheck section22's original two-year held-line fixture, on HT1's
path in versioned dry run `cbs-gate-HT1-AnHRdJzp`, has a first issue at
20000.152511469427 face and central-bank holdings 20000.15251146943. Its
native plan has cash0, netted0, no market issues and rolled par
20000.152511469427, yet `centralBankNetted` is3.637978807091713e-12.
Both the par-only and hand-reopened-line branches fail their exact
`centralBankNetted()==0` premise. The original-class observation reproduces
those exact values; no custody face is reserved or consolidated there.

Trace the untouched native rollover/holder arithmetic at this rounding
boundary. Cause the all-central-bank-held issue with a nonzero discount,
retain cash0 and exact ownership, no market issue, real payment and M0
guards, and prove there is no invented surplus netting. A tolerance change
or an injected holder/cash correction is not the fix. Keep the mixed-holder
rollover, real inherited own-paper consolidation and all audit assertions.

This is distinct from section19's correct actual own-paper retirement after
the holdings step. HT1 updates that fixture's at-dial cause and separately
reconciles the native retired face. It does not change the rollover model.

Evidence: HT1 notes and `target/ht1-dry-mends-20261122-0220/local-observers-r3/`.
