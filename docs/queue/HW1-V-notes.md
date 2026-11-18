# HW1-V — refine projected ground and borrowing cash

Status: provisional mends; the scheduled normal pick and merge dry run remain.

Base: origin/main `45de48dc2` (0.9.140), branch `batch/HW1-V`.
Final model: `0077f3cf5`; final hard fixtures: `3cf66f550`.

The projected whole-parcel pool now retains its linked usable class and
recomputes zoning as ownership grows. A later addition buys another parcel
when the first parcel's dry remainder is steep, reserved by a zone, or kept
under the forest law. The projection preserves unused usable ground and the
foreign-vault bill across the sequence. WorksValue and AdviceValueOracle
each carry their own pool arithmetic.

Quoted bond cash, including excess from the issuance granule, remains
available to the next addition. This applies to both the initial borrowing
and subsequent borrowing. Quoted coupons, issue spread, maturity and the
existing scaled short-quote/fallback assumptions are retained.

★ Keep the land change inside the two projections; do not change the live
automatic builder's parcel selection. Repeat its existing selection over
remaining offers until usable room covers the projected addition; retain
the office-price fallback when the shelf cannot cover it. This addresses
REV-HW1-4 findings 3–4 without changing the live purchase policy.

★ Carry linked forest alongside relief so the forest law does not reserve
forest across unlinked water. LandParcel's additional transient derived
figure is counted in Landmasses.offerSplit, rebuilt with the existing split,
and is not a saved field; no format change is needed.

★ Office-priced unavailable ground is an estimate outside the offered-parcel
pool, with no reusable remainder. Counting its footprint against parcels
that were never bought would overcharge a later, smaller addition that the
shelf can cover. The new fixture preserves that distinction.

★ Preserve the existing scaled short-quote and no-quote assumptions; only
carry the resulting quoted proceeds through the cash ledger. Changing
future credit availability is outside findings 3–4.

Hard fixtures in WorksFinanceCheck use hand-priced parcels with four orders
of dry ground but only 1.5 orders of usable ground, separately with and
without a share zone. They check four successive purchases/reuses in each
projection. A separate cash ledger on growing clinic plans checks actual
quoted proceeds across at least two later purchase dates, both when the
first order is paid in cash and when it borrows. `--projection` runs these
focused fixtures for removal checks without repeating the older sections.
An additional hard case includes forest entirely across unlinked water,
under a law keeping all forest. An unavailable large addition is estimated
without consuming a parcel or charging its virtual footprint against the
pool a later, smaller addition can actually buy.

Checks so far:

- Initial `./batch-check.sh --fast`: exit 0, clean generated maps, zero firm
  Stale findings, base `45de48dc2`.
- Development `./batch-check.sh WorksFinanceCheck`: exit 0; every old and new
  section green, zero firm Stale findings (`HW1-V-finance.log`, full harness
  output `target/checks/WorksFinanceCheck.log`).
- Ten initial removal checks each exit 1 with their premises retained:
  the old whole-linked-dry carry, omission of growing zoning, omission of
  linked forest, and discarded initial/later excess proceeds, separately
  in WorksValue and AdviceValueOracle. Logs: `target/hw1v-removal/` and
  `target/HW1-V-removal.log`. Corrections restored afterward.
- Twelve final removal checks include both estimated-footprint cases and use
  separate classes under `target/hw1v-removal/classes`, leaving the live
  source and the named suite's classes alone. All twelve mutants exit 1,
  every `fixture:` assertion remains green, and the mended focused baseline
  exits 0. Summary: `target/HW1-V-removal-final.log`.
- Named advice/ground/finance/save run: exit 0, 13 harnesses green:
  BuildAdviceCheck, EssentialWorksCheck, WorksFinanceCheck, RunWorksCheck,
  BuildCardCheck, AutoBuildCheck, FundCheck, LandCheck, LandClassCheck,
  ZoningCheck, IslandNetCheck, ReadPathCheck and SaveFileCheck. One timing
  assertion in IslandNetCheck was soft; zero firm Stale findings. Summary:
  `target/HW1-V-named.log`. The final fallback distinction was added during
  this run; its final focused fixtures pass as above. A final rebuild and
  repeat of the three advice/value harnesses also pass: final
  `./batch-check.sh --fast` exit 0, then BuildAdviceCheck, WorksFinanceCheck
  and BuildCardCheck all pass (3/3, exit 0, 77 s) on that rebuilt final code.
  Summaries: `target/HW1-V-final-fast.log` and
  `target/HW1-V-final-advice.log`.
- Normal pick currently selects 37 of 218 harnesses, not ALL. Review line:
  `not needed - no money, saves or threading in the diff`. The full picked
  run and gate are pending the orchestrator's machine slot.

The orchestrator supplied main's retained eight-file default reference:
`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-FB4-10cc0ca2/trace-*.csv`.
It is model `10cc0ca20`, equivalent to main `45de48dc2` by the orchestrator's
source comparison (bus message `20261117-195927-548-orchestrator-d85a`).
No new main comparison run was made. The final model's default LongPlaytest
exits 0, "All checks passed", with no findings. Its eight completed CSVs at
`target/hw1v-trace/default-*.csv` are byte-for-byte identical to the reference:
bank, bonds, build, house, labour, month and pop each have 2,405 rows, and
borrow has 41. Report: `target/HW1-V-trace.log`; comparison:
`target/HW1-V-trace-compare.txt`. No ensemble is required by batch checklist 6.
The orchestrator holds the machine for FB6's dry run until about 20:45;
no normal picked suite, ensemble or dry run has been started during that hold.

Remaining at provisional handoff: run `./batch-check.sh --pick` when the
orchestrator releases the slot, request the merge dry run with "HW1-V dry run?",
then run the authorised dry run and retain its directory and its branch/main
commits here. No kept dry run exists yet. The ship file retains the provisional
marker until that gate passes; remove it last in its own commit and verify it
is absent on origin. Review was agreed unnecessary by the orchestrator's
message above. No calls are left for Jerus.

No queue Pending row or known-reds entry names HW1-V at this base.
