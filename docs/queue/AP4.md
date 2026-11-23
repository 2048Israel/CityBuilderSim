# AP4 - an island link re-measures only the districts that changed

Jerus, 2026-11-22, on AP2's calls 3-5 (`docs/queue/AP2-notes.md`, section 7):
"Rec on all except last right", then "Yes, send both". This is call 4, as
AP2 recommended it. Call 5 (holding the paused Build Overview's auction
quotes across draws) is not to be done.

## What is true today

When a landmass is linked, `CityMap.landmassPicture()` re-measures all 151
districts. On slot 9 that took 624 ms and 1,630 ms in month 1135, and
2,801 ms in month 1139: about 1.7 s, 34% of a land month.

## The batch

Re-measure only the districts on the islands whose linked state changed.

`measure()` feeds `syncLand`'s counts and the site index. So the batch proves
that the narrower pass gives the same counts, the same site index and the
same runs, to the bit.

Files: `CityMap` (`landmassPicture`, `measure`, the site index). Harnesses:
`IslandLinkCheck`, `IslandNetCheck` and `LandmassCheck`.

## Bounds

A performance batch like AP1 and AP2: every figure the same to the bit.

- **The trace:** the playtest's trace md5s, on main and on the branch, are
  recorded in the notes. The playtest does not move.
- **The harness:** links an island and asserts that the narrowed pass equals
  a full re-measure in counts, site index and runs. It also shows that a
  district off the changed islands is not re-measured.
- **The measurement:** on slot 9's copy, in AP2's evidence,
  `C:/Users/Jerus/AppData/Local/Claude-Data/claude_pzs8sxrjxfjjc/side-session-notes/local_b5752af0-03f3-40b3-abde-e20d6249aad4/AP2-evidence/slot-09-copy-2026-11-22`. The copy is format 61, so measure on tag
  0.9.189 with the changed files over it, as AP2 did.
