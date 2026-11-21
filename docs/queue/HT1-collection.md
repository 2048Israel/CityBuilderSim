# HT1: collecting a tax when the assets are not cash

Decision, 2026-11-21. Jerus reviewed HT1's block and delegated the rule by mail
`20261121-182152-415-jerus-422e`: "Ht1 idk acquire a collection rule then".
The orchestrator chooses automatic secured deferral. This completes the missing
collection premise in household study 3.6 while preserving 3.3's in-kind estate.
It does not change the accepted gains inclusion, wage rates or dividend integration.

## The game rule

Assess the deemed gain once, before the death/departure transfer, using the
accepted average basis, losses and tax rate. Collect the tax from cash actually
available. A shortage becomes an interest-free unpaid-tax liability secured on
the affected assets and their proceeds; it is neither forgiven nor cash revenue.
There is no tax-induced stock sale, borrowing, negative savings or invented cash.

An estate first performs its existing revolving-debt settlement and student-loan
write-off. Tax uses the cash remaining after that settlement. Its assets then pass
in kind under the existing heir allocation, with their assessed basis stepped up
and their unpaid claim carried in the same allocation. A surviving partner keeps
the original basis untaxed. Stage/pool/owner-line movements transfer the liability
with its affected holdings; they do not assess the same gain again.

A leaver is assessed before their savings leave. Cash pays first; the unpaid claim
stays attached to the departing assets, including shares that have become foreign
holdings. Collect from their actual exchange settlements or other capital receipts,
before the net proceeds leave. An unfilled order is not cash and does not erase a
claim. The existing EMIGRANTS field resets each month: it cannot by itself preserve
the attribution of a claim on holdings left unsold in an earlier month.

Subsequent actual asset-sale, redemption and after-current-tax dividend receipts
pay an outstanding claim before optional reinvestment/distribution. Collection is
capped by both the remaining claim and attributable available proceeds. Do not
charge an unrelated heir's wages, another cell's cash, new leavers or unrelated
world investors. Existing current-sale/dividend tax is calculated once under its
own rule; collecting old tax does not tax that gain a second time.

Use bounded aggregate stocks in the game's fixed cells/company pools, not a growing
list of people or estates. Keep sufficient affected-holding/proceeds attribution
to distinguish tax-bearing holdings from unrelated ones after mixing. Record the
precise averaging/allocation in the implementation notes and prove conservation.
If that attribution cannot be represented honestly by the existing interfaces,
report the exact engineering gap before editing a broader system; the policy
decision itself does not require another preference question to Jerus.

The claim is limited to the affected inherited/departing assets and their proceeds,
not unlimited personal liability of an heir. Lack of cash alone never writes it
off. Genuine extinguishment or final insolvent disposal may write off the part
that those assets/proceeds cannot cover, explicitly as a noncash claim loss.
An escheat to the treasury consolidates any self-claim without inventing a tax
receipt; keep the existing unclaimed-property classification and cash/noncash
distinction. Do not create a home market or change its mortgage priority in HT1.

Example: value 1000, basis 400, cash 10, inclusion 50%, rate 15% gives tax 45.
Collect 10 and carry 35 with the assets. If a later attributable net cash receipt
is 20, collect 20 and retain 15. Only the two actual receipts, totaling 30, enter
cash tax revenue. A save/load, split or month boundary cannot erase the remaining
15 or cause the original 45 assessment to be repeated.

## Accounting and evidence

Save both the unpaid liability and matching government claim, including foreign
attribution; copy, stock transfers, resets and currency scaling must preserve them.
Distinguish assessed, collected, outstanding and written-off amounts. Cash revenue,
TreasuryJournal's cash bridge and MoneyAudit record only the actual cash transfer,
once. Noncash assessment/step-up/write-off must not change the GDP definition.

Before source work, map the exact death/departure/month/settlement phases and the
bounded stock representation. Required causing proofs include cash-sufficient and
cash-short estates/leavers, untaxed survivor rollover, step-up/no repeated gain,
debt priority, actual partial/full and unfilled settlements across months, unrelated
holder isolation, inherited liability through cell movements, genuine insolvency,
escheat, splits/currency scaling, and both save/load continuations. Retain the
existing actual-money audits and final-model ensemble/review/gate obligations.

This decision is versioned on batch/HT1 while the batch is incomplete. No source
completion, runtime result, review, readiness or publication follows from this note.
The separate L7 TFSA account design remains an owner item; it is not a reason to
invent an inert research entry or prevent the decided tax core from being built.

## Primary references and the deliberate simplification

CRA permits elected departure-tax payment to be deferred without interest until
the property is disposed of, and requires security above its stated threshold.
See [CRA: dispositions of property for emigrants](https://www.canada.ca/en/revenue-agency/services/tax/international-non-residents/individuals-leaving-entering-canada-non-residents/dispositions-property.html).
CRA also permits estate clearance after amounts are paid or accepted security is
provided. See [CRA: clearance certificates](https://www.canada.ca/en/revenue-agency/services/tax/individuals/life-events/doing-taxes-someone-died/clearance-certificate.html).

Those references support deferral/security as a collection mechanism. Automatic
cash-first deferral for both game events, no interest, no application/threshold,
collection from subsequent capital receipts and fixed aggregate attribution are
the chosen game rule, not a claim to reproduce Canadian estate/departure law.
