<!-- readiness: provisional -->
## changelog
### {next} — BK2b, BK2's review follow-ups (each holder's own discount) — 2026-11-15, ready, see docs/queue/BK2b-notes.md
A partial buyback taking all but rounding-level face now takes the seller's whole unearned discount, as a transfer already does, leaving no discount dust on no face. HoldersCheck causes the ulp-short case for every seller at two price levels and covers the existing retained loss when the bank pays a premium beyond its own discount. The reopening comment now describes each holder keeping its own discount until settlement. HoldersCheck and BuybackCheck pass; full merge checks pending.
