# AP3 - a stuck highway link reruns its search only when what it read changes

Jerus, 2026-11-22, on AP2's calls 3-5 (`docs/queue/AP2-notes.md`, section 7):
"Rec on all except last right", then "Yes, send both". This is call 3, as
AP2 recommended it.

## What is true today

A highway piece whose link failed waits on `stuckAt()` (`CityRuns.java:650`,
checked at :1280 and :1373) for the ground's version to move. Any land
purchase anywhere moves it, so the whole `LINK_SEARCH` ring runs again, up to
2,097,152 plots.

On Jerus's slot 9 (17.3 million people) that took about 1.2-1.7 s in 6 of the
14 months AP2 measured. It is the largest thing left in a land month: 36% of
month 1135.

## The batch

The search's answer depends only on what it read: the plots it visited, the
nets and the rail, and the landmass picture.

- Keep, with the failure, the box of the plots it visited plus one, and the
  other inputs' stamps.
- Rerun the search only when a purchase touches that box or a stamp moves.

This is exact only if that list of inputs is complete, and the batch proves
it is. If it cannot be made exact, the fallback (back off N months) is a
behaviour change and goes back to Jerus, not into this batch.

Files: `CityRuns` (`linkWay`, `LinkSearch`, `stuckAt`), and
`CityMap.RunGround` if the box needs the ground. Harness: `GatewayCheck`,
beside AP2's section.

## Bounds

A performance batch like AP1 and AP2: every figure the same to the bit.

- **The trace:** the playtest's trace md5s, on main and on the branch, are
  recorded in the notes. The playtest does not move.
- **The harness:** causes the kept failure's rerun on both sides. A purchase
  inside the box and a moved stamp each rerun it; a purchase outside the box
  does not, and the kept answer equals the fresh search's.
- **The measurement:** on slot 9's copy, in AP2's evidence,
  `C:/Users/Jerus/AppData/Local/Claude-Data/claude_pzs8sxrjxfjjc/side-session-notes/local_b5752af0-03f3-40b3-abde-e20d6249aad4/AP2-evidence/slot-09-copy-2026-11-22`. The copy is format 61, so measure on tag
  0.9.189 with the changed files over it, as AP2 did (`AP2-notes.md`, and
  that folder's `drivers/`).
- **The review:** a threading and exactness review, if the kept state is
  shared across threads.
