# UP4 - study: the screens updated in place, not rebuilt every month

Jerus, 2026-10-22: the game gets sluggish after long play. The plan behind
UP1-UP3 measured why (UP1's notes have the method, `docs/queue/UP1/` the
probe and its output). He was asked whether the screens should update in
place instead of being torn down and rebuilt each month, which means
rewriting the twelve screen classes, and answered "Study it now".

A study, not a code batch: it plans the rewrite and queues it. It writes no
screen code except the pilot below, which may ship only if it is green and
measured.

## What is known

- Only the screen on show is built, but every redraw tears it down and
  rebuilds it, header and rail included. At month 4,000 that is 6-21 ms to
  build and 17-34 ms of CSS and layout a page, once a month: every 120 ms at
  50x, which is 25-45% of the FX thread.
- CSS and layout are about 60% of a redraw. New nodes are styled and laid out
  from nothing each time; kept nodes whose text changes are not.
- The twelve rail screens come to about 46,000 lines.
- Each screen restores its own scroll position because it is rebuilt on the
  clock (`CLAUDE.md`, "A screen section"). In-place updates would make that
  unnecessary.
- UP2 keeps the chrome (header, rail, panel, inbox) and updates it in place.
  It is the first piece of this; this study may plan it and UP2 then follows.
- The per-screen figures (build, CSS, layout, frames) at months 1,000-4,000
  are in `docs/queue/UP1/default-m1000-4000.txt`.

## What the study answers

1. **Per screen, what is rebuilt and what changes.** Of a page's nodes,
   which change month to month: figures, bars, charts' data, rows that come
   and go. Which are fixed: labels, layout, captions, (i) popups.
2. **The pattern.** One way to write a screen that builds its nodes once and
   updates them on the clock: what holds the nodes, how rows that come and go
   are kept (a pool, keyed by building or sector), how a chart takes new data
   without new nodes, and what happens when the player changes a choice that
   reshapes the page. It must keep the convention that every model figure
   comes through a public getter, never recomputed in the screen.
3. **A pilot on one screen.** Pick the screen whose redraw costs most, or the
   one most visited if the costs are close. Rewrite it to the pattern, and
   measure it before and after with UP1's `ui/UiProbe`, or the probe in
   `docs/queue/UP1/probe/` if UP1 has not shipped. Give the pilot's own
   numbers, not an estimate.
4. **The order and the size.** Rank the other screens by cost saved (their
   redraw cost by how often a player sits on them) against lines to rewrite.
   Group them into batches a brief can pin down.
5. **What checks it.** A timing harness through `Timing` with a dial: a
   page's month update under its budget at a long-run city. A harness that
   the in-place page shows the same figures as a fresh build of it, so the
   rewrite cannot drift from the model.

## Deliverable

- A note in `docs/studies/` and its line in `docs/studies/README.md`: the
  findings, the pattern, the pilot's numbers.
- The rewrite's batches queued: each gets an id never used, a brief in
  `docs/queue/`, and a row in `QUEUE.md` under its rules.
- Questions only where the call is Jerus's, with their facts in the question.

## Order

Queued after UP1 and ahead of UP2. It writes docs and one screen, so it can
run beside UP1, which touches the save path. UP2 waits for it so the chrome
is planned once.
