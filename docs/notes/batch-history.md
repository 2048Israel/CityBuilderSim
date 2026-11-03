# The batch process: the lessons and the longer histories

Moved out of `docs/batch.md` (a batch session's checklist) and
`docs/orchestrator.md` (the orchestrator's manual) in BM1 (2026-10-28). The
manuals keep each rule with a one-line WHY naming its incident; this is the
rest of the story, for the day someone wants to change a rule: without it a
tidy-up can bring an old mistake back. Nothing here is a rule, and nobody
reads it for a batch.

## The lessons of 2026-10-23/24

Cited by letter in `docs/deepseek.md` and in the two manuals; verbatim.

- (a) 2026-10-23/24: PT1-M4's reds held the Earth lane about six hours. A
  blocked batch now goes under Pending at once, so its lane continues.
- (b) 2026-10-24: batches finished at 11:25 and 13:53 sat unshipped until
  22:45, because the orchestrator is not woken after many automated turns
  in a row with no message from Jerus. Hence the hourly check-in.
- (c) The real Earth is earth-2.bin since 0.8.41 (ET1, 2026-10-24); a gate
  that day named earth-1.bin by mistake, and the Earth harnesses'
  full-file sections then tested the wrong file.
- (d) 2026-10-24: `batch-check.sh` decides soft or hard once, at its start,
  and the PC's speed can change after a restart. So a timing red at the
  gate is compared with main's own run the same day before it counts as
  the batch's (TM3, 2026-10-27: not minutes later, one run to one: the two are
  timed in turn, in the same minutes, and the median decides).
- (e) 2026-10-24, Jerus: "anything on high effort or lower is banned". A
  medium-effort orchestrator had stalled a lane, clashed versions and named
  the wrong Earth file.
- (f) 2026-10-23/24: batches built side by side each took main's version
  plus one, so their versions clashed and they had to re-merge. The
  version is now set at the gate.

## What the one-line WHYs leave out

- **The ready-branch wait** (GP1): a background Bash command costs no tokens
  while it waits; the notification is the only sign it ended. The hard timing
  run's old quiet minute (TM2) cost up to 30 minutes of a 22-25 minute gate and
  could not make a green run any greener.
- **Timing at the gate** (TM3, SP1): main goes first in odd rounds and the
  batch in even ones because a run straight after a switch of build was the
  slower; HY1's gate stopped on three map lines main itself misses, and SC1's
  on a shifting load, both sides within the budget, then both over.
- **`--red-ok`** (TM3 review): the lines are known before his word, since he is
  asked after a stop with that stop's lines in hand.
- **Costs** (BP1): an Opus batch session 400k-770k tokens, a stranger's review
  190k-260k, the suite about three times a batch; the suite alone about nine
  minutes, 15-22 beside other runs, 800 and 917 s on 2026-10-22.
- **`--pick`'s fan-out** (BP1 call ★1, `CBS_PICK_FANOUT`; `=999` flips it,
  which reopens if more than one of the first four batches bounces at the gate
  on a harness so left).
- **No ship file** (RL1): the old way, with a batch's own version and row, is
  gone; one left on a branch sent back made `day.sh --due` call it ready every
  hour.
- **The first batch after BP1** reported its token cost against BP1's
  figures; BP1-C's Pending row (1) still waits on it, and is now moot.

## The scripts' headers

The WHY paragraphs of `batch-check.sh` and `ship.sh`, moved here verbatim in
GT1 (2026-11-02, one in, one out); each header keeps a one-line WHY.

### batch-check.sh

WHY (2026-10-13): a session paid for every line it read, and a batch's
checks were a dozen calls - each re-reading the whole conversation - and
their output: a line for every harness, a red one run again in full to find
its assertion, Stale's report whole. This prints about ten lines when all is
well and the failing assertions when it is not (AllChecks -f).
BP1 (2026-10-25): a batch ran the whole suite three times (15-22 minutes
each) and a person read every red to tell main's own from the batch's; now a
batch runs --pick (tools/pick.sh: the harnesses that read what its diff
changed), the one full suite is the gate's, and the red lines and the exit
statuses 3 and 4 let the gate pass main's recorded reds, word for word, with
nobody reading the log.
FW7 (2026-10-25): AllChecks -q hid a second red in a harness that was already
a known red, and only this script compared lines. AllChecks now reads the
record too (-Dcbs.knownReds, the file written to target/checks/known-reds.txt
before the suite runs) and says "known red" or "NEW red: N of M" of each
failed harness, tagging the lines; this script strips the tags (the red lines
are the same as before) and refuses a log whose N is not its own count, so
the two judges cannot disagree unseen. --judge LOG gives the verdict on a
saved log, from the folder the log is in, against the record named by
$CBS_KNOWN_REDS (read only there, never in a real run): what KnownRedsCheck
runs, from inside the suite, on the logs it builds.

### ship.sh

WHY (2026-10-13): the gate was a conversation - a dozen calls, each
re-reading the session, to do the same seven things in the same order.
The judgement stays the orchestrator's (whether the batch did what its
brief asked, the playtest's trace); the mechanics are this.
WHY (BP1, 2026-10-25): side by side, batches each took main + 1 for their
version and had to re-merge, each wrote the same top of the changelog and
QUEUE.md (a conflict for the second), and every ship since FW11 was
finished by hand past main's known reds, a timing red compared by hand and
a map conflict resolved by hand. The version, the records, those reds and
that conflict are now this script's, and a dry run shows the real merge.
WHY (BP1 review, 2026-10-25): a red passed with --red-ok in one of the
eleven timing harnesses came back in the hard timing run and was judged
against main's run, where it was never red; a timing red accepted as main's
was written to gate.txt as a plain line, so a later regression on the same
assertion printed the same line and passed as "recorded"; a ship file with
{next} but no changelog block tagged a version with no paragraph, and one
with a copied table header corrupted QUEUE.md; and an old-style branch could
ship a version main had tagged already, or a --no-tag merge that moved
VERSION and stopped every later ship at step 3. tools/ship-selftest.sh runs
this script against a throwaway origin with a stub for the checks (no JVM):
run it after changing this file.
WHY (TM3, 2026-10-27): the timing was judged once on the merge and then against
one run of main's, minutes later. On 2026-10-26 HY1, MS2 and CC1-S stopped on map
lines main itself misses under Jerus's running game (a 59.9 ms median against 58;
main's own run of it read 75.5 earlier that day), and two shipped only through a
copy of this script changed by hand at that one stop. Now a timing red is timed
again with main and the merge in turn, in the same minutes, and counts only if the
merge is the slower by more than a margin; and --red-ok reaches that stop. And
(the review of TM3, the orchestrator) his words pass only the lines he named, in the
suite step and the timing step alike: he is asked after a stop, with that stop's
lines in hand, and a broad --red-ok let a different flaky line go red in the rerun
and pass under words given for another (CC1-S's gate). The script runs from a copy of itself: a batch that changes ship.sh
(TM3 did) rewrote it under the shell that was reading it at the merge.
WHY (RL1, 2026-10-27): a lane's next batch is cut from its predecessor's tip while
that one is at the gate, so merging it before the predecessor would ship the
predecessor ungated, with no records: the stop on a branch built on another batch
main lacks. The old way (no ship file, the branch's own version and row) went with
it: every batch has written a ship file since BP1, and one without stops, or ships
--no-tag as an assistant's and a docs pass's do.

### day.sh

WHY (BP1, 2026-10-24): a fresh orchestrator began each day with about ten
calls - QUEUE.md, the changelog, the branch lists, the tags - and the hourly
check-in needed a probe that costs one short call when nothing is due. The
first draft counted the merged branches inside a pipe's subshell, where the
count was lost; the loop here runs in the shell itself.
RL1 (2026-10-27): the old-style ready test (a branch finished the old way, its row
out of its own QUEUE.md, no ship file; ET2 was the last) is gone: no branch from
before BP1 is left on origin. A branch built on an unmerged predecessor is "waiting",
not "ready": ship.sh would stop it, and gating it would ship the predecessor ungated.
The gate's date was the first date in any '#' line, which is often a date in
the queue row's text; it is now the '# date:' line ship.sh writes for it.

### wait.sh

WHY (BP1, 2026-10-24): a session that must wait for another batch's row to go
ended its turn and was woken by a poll, or wrote an until-loop by hand with no
deadline, which loops for ever, without a word, while a fetch keeps failing.
This has a deadline when asked for one, tells a failing fetch from a quiet
queue (6 failures in a row end it), and says how it ended in one line.
2026-10-25, the review: the last fetch of a --for 9 call started at the
deadline, and a stalled one (GIT_HTTP_LOW_SPEED_* does not cover the connect
phase: curl waits 300 s) outlived the Bash tool's 10 minutes with no line;
now no fetch starts in the last 75 s and each is cut off at 45. Nothing said
to take origin/main after the wait either, so a successor built on its
predecessor's old base: the header says it now, and the done line checks.
GP1 (2026-10-27): a branch that was ready waited for the orchestrator's next
hourly check-in, a median of about 60 minutes from the ship file to the merge;
--ship makes the ship file itself the thing waited for. tools/wait-selftest.sh.
RL1 (2026-10-27): a lane's next batch starts at its predecessor's ship file, built
on its tip, so --row also ends when that tip moves on to a ready state HEAD lacks
(the predecessor sent back and mended); --ship leaves a branch built on an unmerged
one out. Both fetch main and the batch branches, which --row needs now.

### pick.sh

WHY (BP1, 2026-10-25): a batch ran the whole suite three times (the
baseline, its final run, the gate's rerun, 15-22 minutes each) and the
orchestrator read every diff for a reason to review it. docs/harnesses.md
already says which harnesses mention which class, so the diff names the
harnesses and the review the same way. A cap that fell back to the whole
suite when a pick grew was left out: the pick is a floor, the gate's full
suite is the net, and a class most harnesses read is said to be left to it.
