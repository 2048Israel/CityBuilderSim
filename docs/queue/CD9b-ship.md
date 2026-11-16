<!-- readiness: provisional -->
<!-- gate-run: /tmp/cbs-gate-CD9b-X1wtPzgv -->
## changelog
### CD9b — CD9's review follow-ups (the foreign debt's) — 2026-11-16, ready, no tag (harness), see docs/queue/CD9b-notes.md
Debt.effectiveRateOf's javadoc now says what getYieldToMaturity() does since CD9
(its bracket split at a price equal to the payments' total). CurrencyCheck's two
extreme-rate sections no longer build a bare ledger and assert revalueForeign's own
formula back: they drive a real city's ledger through the extreme rate and assert the
booked exchange line CD7 keeps (NationalAccounts' debtExchangeGain), red when the
revaluation is skipped. No game or save format change.
