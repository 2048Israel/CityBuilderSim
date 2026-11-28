# BFW1 — the bank pays for its own wholesale credit risk

Queued by REV-CBK3 and the orchestrator, 2026-11-26
(`20261126-215827-989-orchestrator-1654`). This is a follow-up to CBK3;
CBK3's secured window, stable desk cash and foreign funding accounting remain.

Foreign wholesale funding currently reprices each month at the sovereign's
12-month local-money quote (`Bank.setForeignFundingRate`, the funding settle
in `Game`), with no bank credit spread or quantity limit. It remains open
while the bank is in resolution. Calling it term funding does not give it
an actual locked rate or a credible counterparty funding decision.

Give the bank its own wholesale credit spread that widens as its capital
thins, and close access to new market funding while it is insolvent/in
resolution. Existing debt must remain owed and serviced under an explicit
rollover/maturity rule; never erase it when access closes. Settle how the
bank reduces new lending and purchases when the market will not supply the
gap, before any financing door spends it. Research actual unsecured bank
funding and resolution practice; record the chosen simplified law.

Decide whether liabilities have actual term maturities or monthly repricing,
and name the choice in the statements. Preserve the existing distinction
between actual central-bank advances, foreign principal and interest paid
abroad. Any new saved state crosses DataSave, both load paths and
SaveFileCheck; follow the old-saves rule for a changed meaning. Include
conservation checks for opening, refinancing, repayment and closure, and
cause both declining-capital spread widening and a rejected insolvent bank.

Likely files: Bank, Game's bank-funding settle, DebtManager's local wholesale
quote, statements, DataSave/load, BankCheck, BankFailureCheck, MonetaryCheck
and SaveFileCheck. Re-read their current maps and the CBK3 notes before work.
A final-model ensemble measures credit, failures and remittances; M1 and F1
remain hard stops. This row authorizes no unlimited emergency replacement
for closed market funding and no weakening of conservation or save checks.
