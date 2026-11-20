## changelog
### EDU1 — the poverty trap's gap in EducationCheck — 2026-11-20, ready, no tag (harness), see docs/queue/EDU1-notes.md
EducationCheck's "a poor city at x3 enrols fewer than at x1: the trap is back" was red on main from CU2 (0.9.130), seen only at R6b's pick, the harness being nightly.
Bisected on the kept gate runs and probes: green at SQ1's and DQ1's merges (x1 1,673, x3 1,438), red at CU2's (1,652 / 1,632). The model did not move: at x3 the
willing share still falls from .72 to .35 and the month's intake to about half of x1's. The chained world prices make the fixture city a sixth larger at five years
(23,011 people at x3 against 19,779), so x3's half-rate intake filled the one university that x1 and x0 already filled. Section 15's three cities now build three
universities, as the rest of the harness does, stepped by the month, with a fixture line that none filled at x1 or x3 (at most .72 and .32 of the staffed seats).
The trap line is unchanged: 3,307 at x1, 1,543 at x3, 3,867 at x0. It goes red with one university, where the fixture line names why, and with the fee kept out of
affordability. EducationCheck's known red is retired.
