# RES1 - Final Real Estate study handoff

Jerus's 2026-11-21 instruction to finish the study so HO1 is green lighted closes
the general study-review hold. The final decision record is section 8; section
14 names the implementation contracts and remaining model acceptance gates.
This is documentation only. No game behavior, saves, UI or playtest trace changed.

## Findings and decisions

- Reuse the existing household cells (141 measured), roughly180 as the initial
  budget; 14 initial building forms/32 dwelling-form classes. Keep all physical,
  financial, occupancy and parking state in bounded aggregate cohorts.
- One monthly quote/clearing cadence, cached factors, one preference vector per
  existing shape/tier/stage cell initially. Temporary joint-eligibility regions
  prevent the same wealthy/qualified buyer share being counted for alternatives.
- Keep sitting tenants; outside owner-occupiers need vacancy. Owners pay full
  common/owner obligations with zero rent; bank qualification counts half the
  actual common fee. Finance and pay insurance once.
- Common pools include RE's unsold shares and survive zero RE ownership. Start
  management at actual cost recovery, profit markup0, reserve contribution0.
  Record actual cash/receivables, allocate shared/private repairs once, retain
  shared-parking surplus and show manager revenue separately from owner calls.
- HO1 immediately prevents financial residential demolition and safely carries
  owner/loan/obligation stocks through life events. RES5 then supplies deliberate
  listings and initial1% monthly continuing-ask markdown, tested at0.5/1/2%.
- Costs, rents, work, parking and fees are explicit initial recipe/reference
  inputs. Actual funded construction, occupancy, era/rate response and integrated
  speed remain caused/played acceptance work for RES3/RES4/HO1, without a new
  blanket study approval wait. No unimplemented game outcomes are called measured.

## Checks and provenance

Earlier exact-rational common-fee/split-merge checks, 14-form geometry/capital
checks and source-model probes remain recorded in sections11-13. The original
33 kernel timings and4 model windows are unchanged. The measured model source
base is a9bfcd070; closure/dependencies were refreshed against main a9ffdac83.

New closure:34 caused design fixtures and32 class-level credit/fee input checks
passed. Companion validation embeds exact Python and supplemental Java source.
Supplemental timing and the standard docs-only --fast pass are pending while
this ship file is provisional. Final results will replace this sentence before
mends publication. No suite or ensemble is owed by this all-Markdown diff.

## Publisher handoff

Publish RES1 untagged. Queue RES2 -> RES3 -> RES4 immediately before the existing
HO1 row, after its existing HT1/HS3/HC1 prerequisites. Amend that row to link
HO1-real-estate.md and retain the accepted credit policy. Queue RES5 after HO1.
Move REU1's now-decided study-design row from Pending to Queued after RES5 and
its other model dependencies; use its rewritten brief. Preserve HO4/HO2/LP1
ordering and resolve actual screen/source overlaps by the contract.

The design/review hold is cleared. HO1 execution is authorized once its actual
code dependencies ship; no foundation has been described as already implemented.
The orchestrator alone edits QUEUE/main/version/changelog/gate records.

Review: money=no, saves=no, threading=no in this diff; only Markdown study,
validation, future implementation briefs and publication inputs. Model briefs
require their later normal money/save review, gates and ensembles.
