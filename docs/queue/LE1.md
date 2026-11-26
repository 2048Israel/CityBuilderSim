# LE1 — identify borrowers before limiting large exposures

Queued by the orchestrator's CBK3 instruction, 2026-11-26
(`20261126-165359-042-orchestrator-423c`). CBK3 ships nine working dials;
it neither saves nor shows a single-borrower control without borrowers.

The present private books pool industries. BusinessDebt identifies a sector,
not a firm or connected group; a construction order can contain many buildings.
Neither a ticket nor a building count establishes independent ownership.
Bank's `NO LARGE-EXPOSURE LIMIT` records why the old industry-as-borrower
implementation was withdrawn: it sent 99% of business credit abroad.

Build a persistent borrower and connected-group ledger spanning loans,
mortgages, interim financing and the bank's corporate bonds. Decide and
document the allocation premise for existing aggregate sector debt before
implementing it; apply the old-saves rule if its meaning changes. Preserve
identity through renewal, refinancing, partial default and pending commitments.
Do not manufacture an independent borrower on every ticket or infer firms
from loan/building counts.

Then expose a saved large-exposure limit against the relevant bank capital,
with Basel's 25% reference, and sovereign exposures exempt. Keep the current
industry concentration pricing separate. Checks must cause repeated tickets
to one borrower and linked firms, total exposure across instruments, the
sovereign exemption, the actual issuance gate, saves and the money identity.
The gate must refuse or resize funding before a builder spends it.

Expected files: Bank, BusinessDebtManager, BusinessDebt/Mortgage/InterimLoan,
CorporateBond/BondMarket, Game's financing doors, DataSave/load, BankRules and
its shared Policy/Bank pane. Check the current file map and batches in flight
before choosing a design. This row authorizes no industry or loan-ticket cap.
