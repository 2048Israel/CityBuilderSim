# BPM1 — carry the bank's purchased bond premiums

Queued from CBK3's solvency replay, 2026-11-27. This is inherited bond
accounting, distinct from CBK3's collateral window and BFW1's wholesale risk.

ZERO_HOT seed 4, month 176, starts with equity 1684.624934615. Its movement
includes `discountRestruck = -1730.021818379` and net income -85.317168655,
leaving -130.714052420 before rescue. The actual funding bill is 8.576881022;
all scheduled rollover needs were funded. See
[the event operands](CBK3-failure-trace.csv) and
[the source/proof record](CBK3-notes.md#solvency-replay-requested-while-res3-mends-2026-11-27).

`Debt.reopen` holds signed face minus proceeds until settlement, but
`Debt.settled` floors the bank's residual unearned discount at zero.
`Game.restrikeBankDiscount` therefore recognizes premium beyond the bank's
own discount as a retained loss. These methods are unchanged in CBK3,
main c52ad5136 and the retained 0.9.212 control. BK2's notes, call ★1,
explicitly left premium carrying for its own batch; no queued row remained.

The [OCC's August 2026 Bank Accounting Advisory Series, 1A question 16](https://www.occ.treas.gov/publications-and-resources/publications/banker-education/files/pub-bank-accounting-advisory-series.pdf)
treats premiums as yield adjustments amortized over the applicable life,
with call/prepayment exceptions. Paying above par is not itself proof of
an immediate economic loss of the premium. CBK3's trace did not capture the
individual line/coupon/quote/face/cash operands; do not claim it proves the
entire recorded loss was economically unwarranted or that the bank was sound.

Carry purchased premium in the holder's asset cost and amortize it alongside
discount accretion. Keep actual market/credit/disposal losses separately
identifiable. First cause a reopening whose bank premium exceeds its own
remaining discount, and capture actual cash, face, coupon, yield and carrying
value before and after settlement. Assert no purchase-premium capital loss,
correct later effective yield and maturity/call/disposal, and an equity bridge
and money audit exact to the cent. Include the played seed-4 finding.

Map every holder's premium/discount treatment, including the central bank;
keep the treasury's existing effective-interest liability book consistent.
Likely files are Debt, DebtManager, Bank, CentralBank, Game's primary settle
and re-strikes, DataSave/load, HoldersCheck, BankCheck, CentralBankCheck and
SaveFileCheck. Restate existing immediate-loss assertions to the real law
with the evidence; preserve their conservation coverage. Saved fields cross
both readers and writers; apply the old-saves rule, with no conversion.

Coordinate the shared bond sections with CBK2 and any held AP6 figures: a
new writer raises them. Money, saves and default behaviour need a stranger's
review and a final-model ensemble. This row does not authorize changes to
the window's collateral/desk rules or a capital subsidy to hide losses.
