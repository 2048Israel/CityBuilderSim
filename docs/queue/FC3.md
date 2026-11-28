# FC3 - the foreign crews take their share of a contract, not the whole of it

From ASH4 (its ensemble ASH4c, `docs/queue/ASH4-notes.md`, section 5). One run, ZERO_HOT seed 3, raised LongPlaytest's
"negative monthly GDP" at month 275: -85.08 (C 157, Iconstr 199, Istock 119, G 198, NX -759; imports: food 13,
materials 400, raw 958; exports 612). The cause is main's code, FC2b's handover and R1g's early premium, reached on
ASH4's path. ASH4's diff (Household, HouseholdBalance, Exchange) touches no site, contract or account.

## What happens

1. **The handover moves the whole contract.** `BuildingManager.handSiteAbroad` hands a site to the foreign crews
   through `BuildingsStacks.handAbroad(payer)` (BuildingsStacks.java ~247), which moves the payer's whole home
   contract, value, units, allowance and points, into `abroadContracts`. Only the points the foreign crews take on
   (`abroadTermsOf`, a share at their pace) should move.
2. **The abroad part earns all of it.** In `advanceConstruction` (~775-787) the home part earns
   `(contractValue - abroadValue) * fh` and the abroad part `abroadValue * fa`. With all the value abroad, the home
   crews' share earns nothing while they still draw `(materialsOwed - abroadMat) * fh` of material.
3. **On the run.** The Pulp Mill (Wood Products) owed 90,400 points at m256, 85,064 of them on the foreign book's
   claim. The handover was for 5,336 points at 400 a month, yet it moved all 90,400 points and v12,564. The contract was
   earned at about 959 a month and was empty by m269, with 32,054 points and 1,062 units of material still owed.
   From m270 to m276 the home crews drew 221 to 270 units a month with no revenue against them, about 238 of it
   imported at m274. A Distribution Substation was drained the same way by R1g's `earnAhead` (~395-412), which took
   the foreign premium early out of the home contracts on other sites, allowance included. GDP fell from about 1,100
   to 31, then to -85. Over a site's life the recognised total is right; the fault is timing.

## The mend

- `handAbroad(payer, points)`: move only `f = min(1, points / c.points)` of the contract's value, units, allowance and
  points, and leave the rest at home; `handSiteAbroad` passes the points handed over. Contracts are saved with their
  points, so no format bump. On the run this removes 175 of the 233 units and m275's GDP is positive.
- Cap `earnAhead` at the contract's value less its allowance, so a site it drains still earns its allowance as it
  draws (the Substation's 58 units).

## The check

ForeignChoiceCheck already calls the handover. One payer's contract on a site of about 100,000 points, about 94% of
the owed points on the foreign book's claim. Hand over the rest of the payer's share through `abroadTermsOf` and
`handSiteAbroad` (about 5,300 points at 400 a month), then advance the site a month at a time to the end, about 4,500
home points and 400 abroad a month. Assert:
- after the handover the home contract keeps `1 - handed/owed` of its value;
- no month draws material (`getMaterialsDue() > 0`) with `getRevenueDue() == 0` while points are owed;
- `getContractValue() > 0` while `owedPoints() > 0`.

On main the second assertion should go red within about fourteen months. The run itself is a probe, not a check:
`ham.citybuildersim.Ensemble ZERO_HOT 3 out.csv` on ASH4's classes (c3e284b1f), wrapped by a main that prints each
`LongPlaytest.findings` entry's `worst` after `Ensemble.main` returns. It reads -85.08 at m275 before the mend.
