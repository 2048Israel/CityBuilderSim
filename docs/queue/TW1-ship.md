## changelog
### TW1 — TreasuryCheck's dollar-note fixture causes its own window — 2026-11-18, ready, see docs/queue/TW1-notes.md
TreasuryCheck's rollover section no longer lets the path decide whether the borrowing window is open as its dollar note
falls due. It asked for a flat D$500k six-month note and asserted the window open; the window shuts when a year's
service on what is owed abroad is more than DebtManager.WINDOW_SHUT_SERVICE_SHARE of a year's exports, and the exports
are the path's, so TQ1's path shut it (five assertions red on dc8f4da6) and DQ1 and CU2 opened it again. The note is now
sized from the city's own exports to half that share, and a save of the same city at the same month gets one sized to
twice it, with the window shut and the dollar note rolled at home as the model rolls it; the D$ is pinned in both so
the share at the maturity is the one set. Every law asserted before is kept; 17 assertions are added and each fails with
its cause removed. On dc8f4da6 the harness passes. No model change: the default trace is unmoved, no ensemble.
