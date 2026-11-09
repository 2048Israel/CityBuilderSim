## changelog
### {next} — WN4c, a peg whose anchor lost its price keeps the loaded rate — 2026-11-09, ready, see docs/queue/WN4c-notes.md
WN4b's review follow-up. A save whose peg is anchored to a money the save no longer has a US$ price for (the euro's
rate gone) falls back to the US$, as before, but its peg had kept the saved D$ per euro, which the US$ peg then held as
D$ per US$ (1.1000 held where the city stood at 1.0523); the fallback now holds the loaded rate, as the peg's own
month would have struck it (PegAnchorCheck 8, red before the fix). The review's two low notes were looked at and
neither is wrong: in a held month the hold's support is exactly the log of the held rate over the shadow, so it needs no
clamp, and the dial reads the gap against this month's anchor price, the held rate the reprice then measures against
(both pinned in PegAnchorCheck 10). Nothing in a played city reaches the fallback, so the default trace cannot move:
no ensemble.
