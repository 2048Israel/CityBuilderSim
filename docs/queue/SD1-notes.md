# SD1 — say Scrapped when a building is scrapped

Self-reviewed provisional mends, 2026-11-22; untagged, `moves_playtest=no`.
The readiness marker remains; review/heavy admission and publication belong
to the orchestrator.

`Game.retire` physically removes the plant, releases its plot, recovers steel,
sells the recovered material and records the demolition. Its message now starts
`Scrapped`; every format argument and the plot/material suffixes are unchanged.
Compulsory purchase in `buyOutAndDemolish` still says `Sold`. No economic,
ownership, financing, save-schema or month-order statement has changed.
`BuildCard.wordKind` keeps both prefixes in SELLING, after BUILDING and before
LAND, with its existing enum and colours. BuildCardCheck follows that wording
and checks precedence for both paths.

The existing BuildingStackCheck shedding fixture now reads the line after
calling the native Game retirement path for three Convenience Stores. The
existing ConstructionControlCheck compulsory-purchase fixture reads its Sold
line immediately after the native buyout. Both retain all their original
fixtures and economic assertions. Both native proofs passed.

Reader audit found one additional production owner: `ui/SectorScreen.shortWord`
(around line 765). It truncated only Built/Sold messages at ` - ` for compact
cards. The orchestrator admitted this exact addition on 2026-11-21 before its
edit: Scrapped joins that prefix test to preserve the existing display
behaviour, and its documentation follows. BuildCardCheck calls the text-only
reader without constructing a screen, proving the compact Scrapped line and
retaining the Sold and Built controls. Other displays
pass the wording through or use BuildCard's classification. Other Sold
messages describe genuine purchases, financial sales, or recovered material.

Trace basis: `tools/ensemble/Ensemble.columns` contains numeric model reads,
policy phase and finding names, with no investment-message field. LongPlaytest
has no `playtest.trace` prefix by default. Its `houseReason` classifier has no
Sold/Scrapped branch, so replacing that prefix preserves its housing reason
counts; the leverage refusal match also does not depend on either prefix.
Optional `playtest.trace` house/bank CSV sidecars explicitly include advisor
text, so those text fields carry corrected wording when that line is read;
optional bank/sector diagnostic reports print it too. Such text is not
claimed byte-identical. The inspected default and ensemble outputs exclude
these investor strings. The production diff changes no model read or action;
`moves_playtest=no` is appropriate for this words-only, untagged batch.
No baseline main suite or ensemble was run.

All three changed-source `batch-check.sh --fast` runs: exit 0; compile OK, maps
regenerated, Stale 0 firm findings. Maps were also regenerated with UTC to
avoid changing hundreds of date-only headers from the fork's UTC date.
`pick.sh origin/main`: ALL, 238 harnesses, Game has 200 readers; review floor
`not needed - no money, saves or threading in the diff`.
The orchestrator admitted the named slot after HT1 and the study explicitly
released their JVMs. Four selectors ran five harnesses sequentially with
`JDK_JAVA_OPTIONS` naming main's C:/ earth-2/world-1 files and
`-Dcbs.parallel=1`: BuildCardCheck, ConstructionControlCheck, TimingCheck and
the substring-selected KeptTimingCheck all exited 0. BuildingStackCheck's
first run found one overstrong new assertion: it required a material-payment
suffix even though this existing fixture's builders pay 0. The assertion was
mended to match the existing Salvage.paid() condition, with no fixture,
production, tolerance or original assertion change. Its focused wrapper rerun
exited 0 (one harness, all assertions passed). The initial failure was fixed,
not waived. First and final logs remain under target/SD1-* and target/checks/.

The actual retirement line was `Scrapped 3 Convenience Store - SB1 fixture
(plot back to the city for $0.18)`; no material-payment suffix is due, and
the recorded salvage quantity and every original economic check pass. The
actual native buyout line was `Sold 20 House to the city by compulsory purchase, for $228.29`. Both
read as SELLING ahead of land, and the retirement reads BUILDING for an order
on site. The compact reader's Scrapped, Sold and Built controls pass.
ConstructionControlCheck's unchanged household-retention observation was
not reached (households did not outnumber homes); its native buyout,
compensation, debt, demolition and other economic assertions passed.
The named slot was released to the orchestrator after actual terminal;
no SD1 job remains. Root admission is required before the owed ALL dry run.

Self-review: the three production owners are Game.retire (one format-string
prefix), BuildCard.wordKind and its SELLING documentation, and
ui/SectorScreen.shortWord and its documentation. Proof owners are
BuildCardCheck, BuildingStackCheck and ConstructionControlCheck. The diff
changes no retirement, buyout, debt, tax, land, material, funding or save
operation, no fixture setup, and no existing economic assertion. Classification
and compact-card handling follow the corrected prefix; other consumers pass
the text through or match unaffected phrases. A byte-level comparison against
the branch base confirmed exactly those wording, reader and documentation
replacements in all three production files, with every other byte retained.
No other source owner is needed.
