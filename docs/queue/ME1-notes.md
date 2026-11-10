# ME1 notes ({next}, 2026-11-09)

The manual's edition (`QUEUE.md`'s ME1; Jerus: "Yes, one edition now"). The six manual texts that waited
under Pending are in the published page, the page is republished in place, and `docs/manual.html` and
`docs/manual.md` are pulled from it. No model change, no ensemble. Every changed path ends in `.md` but
`docs/manual.html`, which `tools.ManualToMarkdown` writes.

## 1. What went where

The page has 26 sections now, where it had 25: Rankings is a section of its own, and the three after it
moved down one (the contents, the section numbers and the separators with them).

| row | where in the page | from |
|---|---|---|
| FC1-M | 9, The builders & the queue: "When the builders are short", after "Two sites go first" | `FC1-notes.md` §6 |
| WN6-M | 16, Prices, the currency & the world: "The nations on the Trade tab", its last subsection | `WN6-notes.md` §7 |
| EP1-M | 17, Founding a city: "Founded in 1900, at 1900's prices", its last subsection; and the calendar's line in 24 ("Month 1 is January 1900") | `EP1-notes.md` §1-§3 |
| WN5-M | 18, Land: "Land is sold by its nation", after "Land is bought in dollars" | `WN5-notes.md` §1 |
| LB3-M | 23, Rankings, new, after "The screen": seven subsections | `LB3-manual.md` |
| TR1-M | 24, Time, saves & logs: "Recording and replay", under the left column | `TR1-notes.md` §6 |

The footer gained one line that says what this edition is (9 November 2026, at build 0.9.75, the six texts).

## 2. What changed against the texts

Each text's builds, counts and figures were checked against main at 0.9.75 (b69bc612). The newer figure won.

- **FC1.** `BuildersShort`'s dials are as written (knee nine months, half at 24, ceiling +80%, the foreign
  floor 5%, price ×2, a quarter of the capacity), and `ConstructionTrapCheck` still gives 441.6 of 458 on
  repairs, the 30,600-point book over 26 months and ten more depots. Added: FC1b's change (0.9.7, a
  cancelled order comes off the contractor's book; a rushed site's overtime on the city's own crews alone).
- **WN6.** The row says "after The world"; section 16 has no such subsection (that title is Land's), so the
  text is section 16's last. Added: the line WN5b put under the currency's chart (0.9.10, "Weighs Germany
  50% ...").
- **WN5.** The factors were printed again from `data/world-1.bin` (US 1.35, NL 10.9, DE 4.4, JP 3.8, VE
  0.42, CN 0.31, elasticity 0.79, Malta 30 times Canada; Canada US$9,564 a hectare): as written. The
  receipt's example is the one `LandNationsCheck` prints today. Added: WN5b (0.9.10, the hover priced on its
  own thread, the island's sentence). Worded again: "83 km² against 135" compares one Rotterdam city with
  the old single price, not Rotterdam with Halifax; the harness's fixtures are Windsor, Halifax and the
  Strait of Gibraltar.
- **EP1.** Save format and floor were 50 at 0.8.95 and are 51 since 0.9.50; the text says both. EP1b is
  still queued, so the stand-in century stands. "With today's technology" is out: R0-R5 have shipped since.
  The line the row quoted ("Month 1 is January 2000") is corrected where it stood, in section 24.
- **TR1.** Keys, speeds and labels are as written (`ReplayMode`). Added: CM1 (0.9.11), a glide and a fly
  load first ("Loading the move...", "Ready - click to run").
- **LB3.** Three things had moved. (a) The text said the other sixty measures "rank on Real world 2025
  only"; `world-2025.json` names 70 measures, 47 with the real world's figures and 23 with none (LC1,
  0.8.85, took 17 out; LC2, 0.9.58, brought 9 back), and Your world carries ten. (b) "A founding game dollar
  counts as one 2015 dollar" was true until EP1; it is a 1900 US$ worth 28.4 of 2015's (`RealDollar.BRIDGE`),
  and the three formulas carry the bridge. (c) The controls' labels are the screen's. Added: LB4 (0.9.64,
  the city in play as saved, the leaner read).

## 3. The page

- The live page was version `1792539524-6c49` (1,166,511 bytes wrapped). It was never read into this
  session: it was pulled to a file, outlined with grep, and edited by one script that inserts six fragments
  at anchors each found exactly once, then checks the result (no dead link, sections, contents and
  separators 1 to 26, the added tags balanced, no character the page did not use before). The Artifact tool
  takes a republish only from a session that has read every line of the live version, so a subagent with its
  own context read it, rebuilt the page from the script, compared it byte for byte and published.
- Published in place to the same URL as its Version 21, `1794278327-4899`, 1,189,170 bytes as published
  (+23,205); no second artifact. The pull is byte for byte what was built. The subagent found no script on
  the page, no outside resource but its three Google Fonts links, and no text addressed to an assistant.
- `ManualToMarkdown`: 26 sections, 36 tables, 34 formulas, 27 notes, 15 steps of the month, 2,462 lines.
  Besides the six texts, `docs/manual.md` changes about sixty lines by one thing only: their links to the
  three renumbered sections (`#23-time-saves--logs` is `#24-...` now). With those anchors set aside, seven
  lines of the old file differ, all of them this edition's.

## 4. Calls

- ★ **The masthead is left as it was** ("Build 0.8.11 · Save format 40 · 20 October 2026", the vitals
  under it, and the footer's first lines at 0.7.135). Recommended: leave it. This edition adds six texts and
  checks only them; setting the masthead to 0.9.75 would claim the other twenty sections were checked too.
  The footer's new line says exactly what was. A full edition, the page read against 0.9.x, is a batch of
  its own and is not queued here.
- ★ **The pinned version.** The Artifact tool reports the page as "shared with anyone with the link
  (viewers see a pinned earlier version, not this live version)". Jerus sees the new edition; whoever opens
  the shared link sees the pinned one until he shares the new version from the page. Recommended: share it
  when he has looked.
- `docs/queue/LB3-manual.md` stays: it is the text's record, as the other five are their notes' sections.
- The ship file writes `{next}`, as today's studies did; the manual is what a player reads.

## 5. Checks

`./batch-check.sh --fast`: exit 0 (compile, the maps unchanged, Stale with no firm finding and none that
names the manual). `ConstructionTrapCheck` and `LandNationsCheck` were run alone for the figures above and
passed. No save was read and no slot written; the scratch probe that printed the land
factors stayed in the scratchpad.
