# The docs pass

This is the brief for a **fresh** agent — one that did not make the change —
run at the end of every batch, after the code compiles and the suite is green
and before the batch is called done. Its job is to make everything that
*describes* the code agree with the code again: the comments beside the
change, the class headers, the generated indexes, the changelog, the list,
the README's counts. It does not change what the code does.

Why a second agent: the one that made the change is at the end of a long
context and has stopped seeing the prose. The interface split of 2026-09-18
found a dozen small lies that way — "the twelve banners" over a file with
ten, a sentence pasted twice, a javadoc left with no method under it, a
sub-banner explaining a helper that had moved out. A reader with nothing in
its head but the diff sees those at once, because to it they simply read
wrong. What that reader cannot recover is *why* the change was made, so the
implementer writes the design note and this pass does everything derived
from the code.

## What you are given

- **The changed files.** From git: `git diff --name-only HEAD~1` (or the
  commit range named). From a Cowork session: the deploy set's file list.
- **The design note** for the batch — `claude/<title>.md` in the project, or
  its text pasted in — which is the *why*. Read it first. If there is none,
  say so in the report and do the mechanical parts only.
- **`CLAUDE.md`** at the repository root, for where things are and the
  conventions the indexes rely on (a class header's first sentence, `WHY`
  paragraphs, `/* ===== TITLE` banners, one sentence above every dial).

## What to do, in order

1. **Regenerate the indexes, then run `Stale`.** `Regenerate maps.bat` on
   the PC, or `java -cp target/classes ham.citybuildersim.tools.Maps`
   anywhere; then `Stale report.bat`, or `java -cp target/classes
   ham.citybuildersim.tools.Stale`. Its three firm categories (an orphaned
   javadoc, a named file that is not there, a stated count that is out) are
   the first of your list - the suite's `StaleCheck` fails on them - and its
   soft ones (a `name()` nothing declares, a fuzzy count) are leads. Never
   edit anything under `docs/map/`, `docs/dials.md`, `docs/month-order.md` or
   `docs/harnesses.md` by hand — they are generated, and a hand edit is
   overwritten next time. If an index shows a *blank* — a dial with no
   sentence, a class with "(no class header)" — the fix is in the source.

2. **Read every changed file's diff, and the comments within reach of it.**
   For each hunk, look at the comment block above the member, the banner the
   member sits under, and the class header; ask of each sentence whether the
   code below it still makes it true. The things that go stale most:
   - counts ("the twelve banners", "fifty-six harnesses", "eleven sectors");
   - names ("see `foo()`", "in `UserInterface`") of members that moved or
     were renamed — `Where name` finds where a member is now;
   - "since <date>" sentences that describe an intermediate state;
   - a javadoc with no member under it, or two javadocs on one member;
   - a banner whose section is now empty;
   - a `WHY` paragraph that explains a replacement for a thing this batch
     replaced again.
   Fix what is plainly wrong, in the file's own voice and its own line
   endings. Do not rewrite prose that is merely long; do not "improve" a
   comment that is true.

3. **The class headers.** Every class the batch created or reshaped has a
   javadoc whose first sentence says what it is (the code map prints it) and,
   where the class replaced something, a `WHY` paragraph. A new class without
   one gets one, written from the design note — not from a guess.

4. **The record.** In the project (`Leverage & Growth: Java Game`):
   - `claude/changelog.md`: a block at the top in the house shape —
     `### TITLE — date, state, see doc.md` then one paragraph: what shipped,
     what it found, the verification (compile, `BuildMenuCheck`, the suite's
     count against its baseline, files verified on the PC). Newest first.
   - `claude/todo.md`: strike the line the batch closed (`~~…~~ — done
     <date>`), add a line for anything the batch opened and did not do.
     Section 0 if it is a thing to do on the PC this week.
   - `claude/index.md`: a line for a new design note, under its subsystem.
   If you cannot reach the project — a headless run on the PC cannot — write
   the changelog block and the todo lines to `docs/notes/pending.md` in the
   repository instead, newest at the top, and the next Cowork session moves
   them over and empties the file.

5. **The two front doors.** `README.md` and `CLAUDE.md` state counts and
   shapes — files, harnesses, sectors, the tree — and each batch that changes
   one leaves the other wrong. `grep -c` the real number; fix the sentence.
   `AGENTS.md` only points at `CLAUDE.md` and should stay that way.

6. **The manual.** If the batch changed a headline number the manual carries
   (build, save format, building count, harness count, sector count) or
   closed one of its open questions, add a line under the manual's entry in
   `claude/todo.md` section 0 saying so. Do not edit the manual itself; that
   is its own batch, assembled from the published page.

## What not to do

- **Do not change behaviour.** Not a constant, not an order of operations,
  not a "while I'm here". If a comment and the code disagree and you cannot
  tell which is right, the code is right *for now*: leave the comment and
  flag it.
- **Do not invent a why.** A sentence you would have to guess at gets a
  `TODO(docs): <what you could not tell>` in the comment and a line in the
  report, not a plausible paragraph.
- **Do not renormalise line endings.** The tree is still mixed (see
  `CLAUDE.md`); a text-mode edit of a CRLF file turns into a whole-file
  diff. `grep -c $'\r' file` before and after.
- **Do not touch saves, `buildings.json`, or `AllChecks`' list.**

## The report

End with a short report, in prose, in this order: what you changed (file and
what in it, one line each); what you flagged and why; what the indexes now
say (the line from `docs/map/README.md` for each changed file). If nothing
needed changing, say that — it is a result.

## Where this runs

- **From a Cowork session:** the implementer, at the end of the batch, spawns
  a subagent with a fresh context and gives it this file, the changed-file
  list and the design note, then acts on the report before the deploy is
  verified. It is a step in `CLAUDE.md`'s "How a batch is done".
- **From Claude Code on the PC:** `Docs pass.bat` runs it headless against
  the last commit, which is the fully automatic version — it cannot ask
  questions, so it flags rather than guesses. `docs/docs-pass-agent.md` is
  the same agent as a subagent definition; copy it to
  `.claude/agents/docs-pass.md` to invoke it from an interactive session.
