# The bank's share under its target

BF1 put a shared capital bound on growth lending: the bank's room to its
target. That room is zero anywhere under the target. It therefore stopped
every new building's loan and, while the leverage ratio bound, every new
mortgage in the band between the bank's minimum and its target. The
borrower's growth rule still allowed a share there, so BF1's bound made
rationing a complete stop. Its own note said insured mortgages would still
be rationed through the borrower's capital room.

MC2 restores that share. Between the binding minimum and target, each
borrower keeps the existing growth curve: no growth at the minimum, one
percent of its debt halfway through the band, rising toward the target.
The bank also has one shared allowance, the same growth rate applied to
the book the binding measure counts: exposure when leverage binds, the
weighted book when risk weights bind. Pending commitments consume it, and
losses already known before settlement reduce the equity used to compute
it. Each loan must fit both the borrower's share and the bank's remaining
allowance. At or over the target, BF1's room to the target remains the bound.

The adopted recommendations keep that restoration bounded. The shared
allowance stops at the room to the minimum, so the curve near the target
cannot lend past the bank's minimum in one month. A bank whose monthly
rule allowed unlimited borrower growth gets no newly opened band allowance
when a known loss takes it under the target before settlement: a default
must not increase lending room. That guard also prevents a new bond bid
in the same loss window. The allowance remains shared rather than letting
each desk spend the same capital independently. Under the target, growth
uses this band allowance rather than the carry trade's strain allowance;
the carry trade itself still has no room there. These calls preserve the
existing borrower curve while restoring the lending BF1's note described.

BankFailureCheck section 9 causes the conditions with banks placed on both
sides of the target and around the minimum. All 23 lines pass, including
known losses, shared commitments, insured mortgages, business loans and
a bank bound by risk weights. Fourteen of its seventeen assertions fail
with the old rule. The minimum fixture stands a margin either side of the
edge, avoiding a rounding-dependent test exactly on it. The money review,
REV-MC2-2, accepted the mends; later main merges left these source changes
disjoint. The final focused bank check passed. MortgageCheck and
CreditCrunchCheck returned only the fixture reds already recorded on main;
those runs do not establish their failed fixture premises.

The earlier crunch figures belong to the branch's historical .174 model:
it recorded six mortgages, owners putting in 6,950 and homes rising from
497 to 1,195. They are not figures for the final model after FB15. That
historical model's completed MC2m ensemble is retained separately, with
its comparison against .174, rather than replayed or presented as the
final result.

The final ensemble used the merged .177 model at fe42a4567, label
MC2c2-final, against the published .177 baseline. All 80 runs completed
on the gate box in 14.1 minutes, with no missing or short runs. Money-audit
and findings criteria passed ten out of ten, with no failed or unread
audits and no LongPlaytest findings. The overall score was 25/26, with no
changed verdicts: the informational inflation-spell criterion A2 remained
FAIL and H1 remained retired. TN1's later .178 display changes were merged
without changing the model trace; the merged tree's fast check and
EnsembleCheck passed. These results support the bounded lending correction;
they do not promise that every city builds more homes.

The adopted calls, fixtures, historical comparisons and retained evidence
are recorded in [MC2's notes](../docs/queue/MC2-notes.md), with the final
score table and raw summary for `MC2c2-final` in
[the ensemble records](../docs/ensemble/).
No further call is left for Jerus by this batch.
