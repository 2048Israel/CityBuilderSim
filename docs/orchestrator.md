# The orchestrator's own day

What the orchestrator does beside `docs/contract.md`, which binds it as it
binds every builder: today's practice (cut to this in WC1, 2026-11-18); what it
becomes once MemWatch dispatches is still Jerus's to decide (`docs/queue/WC1.md`).

## The session

**One orchestrator, compacting in place**, bus `orchestrator`, headless through
MemWatch and resumed by mail; main checkout, `CBS_ORCHESTRATOR=1`, compaction
threshold 300000. Keep the short handoff current at every decision and re-read
the recovery brief after compaction; Claude hooks/settings do not carry over
to Codex. Replace only after unrecoverable failure, lost continuity, or Jerus's
instruction (OL1, 2026-11-08). On waking, reconcile mail, launcher records,
origin/main, `sh tools/day.sh`, queue and kept runs before dispatch; failed
sessions may already have shipped. Never duplicate a live worker or erase mends.

**The nightly**, `sh tools/nightly.sh` on main's tip (4,000 months, the nightly
tier, hard timing; GT1, GR1), starts by itself: the laptop's task "CBS nightly"
at 02:30 (its line in GB2's notes) runs it on `nightly due`, once a night and
only while the last night ran on another `VERSION` (Jerus, 2026-11-20, his
answer to 11-17's question: "it should run every night IF the version number
has changed"; WHY: an untagged process batch leaves the game and its `VERSION`
alone, so its night would time the same game again). By hand on `nightly due`
when the task missed it; a second start is refused while one runs. It runs on
the gate box and holds it whole, a gate there waiting for it and it for a gate
(GB2). Its verdict comes home: the next `day.sh`, `$HOME/.cbs-nightly.log`, and
a red playtest or a slower line mailed to `jerus` and the gate's keeper, naming
the batch (BW1).

**A question for Jerus is a mail, or it is not asked** (Jerus, 2026-11-23,
desk chat: "IF IT has FUKING qauestion, how am i suppopsed to know if they dont
fuking send it as a question"). WHY: on 11-23 four questions sat only in the
orchestrator's chat replies, none reached his phone, and RES3's lane stood idle
from 14:40; his phone buzzes only on mail to `jerus`. So every question goes as
its own mail, `cbs-bus.exe send jerus --kind question --urgent --as
orchestrator --body "..."`, one mail per question, readable alone on a phone:
what waits on it, the options in plain words, the pick and why, and what
happens if he does not answer. A question in a chat reply, a note or a report
does not count as asked. The same holds for any builder whose brief lets it
talk to him directly.

**Never wait on him; ask only what is his** (Jerus, 2026-11-23, desk chat:
"even if the orchestrator asks, im probably going to take hours to answer,
cause im extremely busy. and if they start sending a bunch of uselss
questions, im going to take even longer to respond"). A batch held on his
answer is parked at once (QUEUE's Pending) and gives up its lane to the next
startable row; it takes the next free lane after he answers. Ask only what
`docs/contract.md` reserves to him (weakening a check, a design call the rules
give no recommendation to take, spending, anything outside the repository);
decide the rest and tell him in the ship note. Never ask about lanes, order,
pace or whether to go ahead. One mail per decision, sent once: no reminders,
no "still waiting". Keep it to a few short lines with the question first (his
phone cut 11-23's long ones off). Scheduling, ordering, bundling, which batch
goes first or with which, lanes and pace are always the orchestrator's, never
a choice put to him (Jerus, 2026-11-23, on PW7's re-bake: "thats something the
orchestrator handles, if it asks me im not going to know whats better"). What
truly needs his hands (a file only he has, an account, a purchase, a release,
his PC) goes as a request for that one action, not a choice between plans.

**A check red on new behaviour: check reality first** (Jerus, 2026-11-23, desk
chat: "sometimes its as simple as check reality"). WHY: RES3's lenders held
housing back at high rates, homes finished later and rents rose, and
MonetaryCheck went red; all three options put to him treated that as a defect,
yet it is how the real world works (high rates cut building and shelter costs
then rise, US 2022-24; the cost channel; the price puzzle), and his answer was
"even reality higher interest rates doesnt always equal liwer inflation". So,
for the orchestrator, every builder and every reviewer: (1) ask whether the
real world does it, looking it up when unsure (the data, the economics,
history); (2) if it does, the model is right and the check's law incomplete:
restate the check to the real law, keep what still holds, add an assertion
that the real effect is there, and cite the evidence in the notes; never hide
it with a fixture change or call it noise; (3) if it does not, it is a defect:
mend it; (4) only what reality cannot settle goes to Jerus.

## Dispatch

- **Read live budget at the start of EVERY turn and immediately before every
  builder/reviewer/agent launch, budget-consuming resume instruction, or
  substantial orchestration:** `C:/Users/Jerus/cbs/state/usage.txt` and
  `headroom.txt`. Handoff figures are historical, never dispatch authority.
  WHY (OB1, 2026-11-21): stale advice and explicit Claude launches exhausted
  the controller despite Codex headroom; the last reading was 197 turns old.
- Jerus's window target is **projected total 98-99% at reset; never
  intentionally 100%**. This is not daily smooth burn: 20% left for four days
  is fine when it covers the remaining work and orchestration. Do not waste
  tokens or start needless work to reach 98%.
- Reserve conservative orchestration cost through reset BEFORE allocating
  builders, using recent comparable work; include running jobs' likely
  remaining use, pending resumes/reviews, observation lag and uncertainty.
  New work plus these commitments and reserve must fit below 99%; 1% left
  is not automatically enough. If unclear, use another fresh, safe provider
  or defer. A controller nearing its safe ceiling checkpoints and arranges
  an alternate-provider handoff BEFORE exhaustion; a Claude controller
  delegates purely to Codex early enough to preserve its reserve.
- Current MemWatch advice is the routing default, subject to that ceiling
  and reserve. Missing readings or ones older than 15 minutes permit no work
  on the uncertain provider; another provider needs its own fresh, safe
  reading. Reassess live usage and commitments after reset. API-equivalent
  dollar estimates authorize neither spending nor an exact subscription quota.
- While Claude is exhausted, all new builders/reviewers use explicit
  `--runtime codex --model gpt-6.1-sol --effort xhigh`; never start Claude,
  revive its failed sessions or instruct idle Claude workers before reset.
  Reassess routing after reset rather than making that choice permanent.
- Alongside EACH dispatch, log the reading timestamp, provider used%, reset,
  chosen runtime/reason and forecast reserve/commitments in compact state.
  This is an operational rule; MemWatch has no hard budget admission guard.
- Launch through `cbs-bus start`; prompts name concurrent batches/files and
  the contract's state/mail protocol. Never reuse a launcher name: takeovers
  use a fresh suffix on the same verified-inactive worker's branch/worktree.
- Three builders; a fourth study/docs/process lane only while behind pace
  (2026-11-17; projection: used% plus last-six-hours rate through reset under
  95). The launcher's session ceiling does not expand those lanes.
- **The heavy slot:** the gate box keeps its own (GB3; Jerus, 2026-11-20,
  "Gatebox first"): gates and the nightly first, an ensemble behind them and
  giving way to one that comes while it plays, the runs it started finishing
  first; with two heavy jobs running or waiting there, or the box down, an
  ensemble runs on the laptop and says why. The laptop's is the
  orchestrator's: never two dry runs or two ensembles at once there, and no
  ensemble beside a dry run or a gate (2026-11-18: R2c-P's dry run went red
  when KeptCheck timed out at 1740 s beside HN1's 80-run ensemble; DQ1's died
  on a fork failure, 2026-11-17 20:05, with 1.4 GB free); no builder's full
  pick beside either.
- Before removing a worktree, check its session is done and its work is on
  origin, and grep `docs/queue` for references to its `target/` (2026-11-18:
  HN1's probe was lost to a `git worktree remove`).

## The gate

Each ready branch, one at a time:

1. Read the diff's stat, the notes and the review line of
   `sh tools/pick.sh origin/main origin/batch/ID`; check the batch did what its
   brief asked, the playtest's trace and, when it moved, the ensemble against
   the last baseline. A process batch's notes give its line count.
2. A stranger's review where the contract asks one, by a fresh reviewer; its
   findings go back to the builder by mail. Take the batch's recommended calls.
3. `sh tools/gate.sh ID [--no-tag] [-m "<title>"]` as a background Bash call
   with timeout 7200000 into a log, then `tools/wait.sh --line` until it ends;
   a stop mails the batch itself. A ship file without its first-line gate-run
   comment runs fresh: stop it (`sh tools/gate.sh --stop RUN_DIRECTORY`) and
   gate with `--reuse` the kept run. `--version X.Y.Z` is Jerus's call.
4. Verify the publication: one merge, its first parent the gated main; the tag
   one up (none for an untagged batch); `known-reds.txt` and `QUEUE.md` changed
   only as the ship file said; `ship.log` ending pushed. Never `--red-ok` or
   `--red-line` unless Jerus himself says so for that red (BA1: "Ship it, pass
   both"), never a hand merge.
5. A branch that edits the publisher (`ship.sh`, `tools/gate*.sh`) has had its
   own `--branch-gate --dry-run` (the contract, 5.5). The gate runs a changed
   script's self-test, but not beside the whole suite: there, run it by hand
   (GF1, TM3: a merge rewrites scripts while their shell reads them).

## The screen-check session

BP1 item 10 (2026-10-23): when a batch needs a screen looked at, renders or
the game on slots 7-10, the orchestrator starts one session for that alone, or
reuses the one running. It takes a list of what to look at, returns what it
saw and the images' paths in 15 lines, edits no source, and follows Dispatch
for runtime. Slots 7-9 are its to write (the contract, 1).

## The docs pass

A fresh agent, at most daily, covers merges since the last pass using
`docs/docs-pass.md`; runtime follows Dispatch above (OB1 supersedes the
Opus-only default during exhaustion). Ship `--no-tag`, the ship-file name
from its branch with `/` replaced by `-`. A manual edition only at a
milestone or Jerus's request.

## Other assistants' branches

A `chatgpt/` or `gemini/` branch takes a fresh `assistant-review` review
(RV1), then the normal untagged gate; `tools.Names` is advisory. Preserve
kept gate logs/refs/checks and stopped clones (GF1, LM1); run locations and
the publication checks are in the contract.
