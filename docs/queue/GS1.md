# GS1 - ten of Stale's soft findings, for Gemini

Gemini's first batch (Claude, 2026-10-13, at Jerus's request). Prose only:
no behaviour changes, so the suite can only fail if a comment edit breaks
the build. It shows whether your edits to this tree hold up before the
batches grow.

## What

`tools.Stale` lists what the prose says that the tree does not. Its firm
findings are 0 and stay 0; its soft ones are questions, 124 on 2026-10-13:
107 "unresolved member" (a comment names a method nothing declares - for
instance `Bank.java` names `ratePremium()` three times) and 17 "fuzzy count"
(a count of sections or sectors that may be stale, or may mean something
else).

Take **ten** of them that are plainly stale prose and make each true again.

**A passage about the past is not stale** (added after the first try,
2026-10-13). "Until 0.7.7 ratePremium() turned it into ...", "X USED TO BE
HERE", "it was ... plus ratePremium()": the method existed then, and its
exact name is the record - leave it. About 70 of the 124 findings are of this
kind. Take only a comment that says what the code does *now* and names
something that no longer exists, or a count that is wrong *now*. Never
reword a name only so that Stale stops matching it (`getSetCost()` to "a
getSetCost method"): the finding is the sentence's truth, not the tool's.

- For an unresolved member, find what the comment meant - the method was
  renamed, moved or folded into another - with `Where` or grep, and name the
  real one. If it was removed and nothing replaced it, say what the code does
  now in plain words instead.
- For a fuzzy count, count what the sentence is about, and fix the number or
  the word only if the sentence is really about what Stale counted.
- Skip any finding you are not sure of; list it in your report as skipped and
  why. Ten sure fixes beat twenty guesses.

## How

1. Pull `main`. Make the branch `gemini/stale-soft-1` from it.
2. `java -version` first (your brief). Then
   `java -cp target/classes ham.citybuildersim.tools.Stale` after a build
   (`./batch-check.sh --fast` builds), or read `target/checks/stale.txt`.
3. Change comments and Markdown only. Not: code, `CLAUDE.md`'s standing rules,
   the generated files (`docs/map/`, `docs/dials.md`, `docs/month-order.md`,
   `docs/harnesses.md`, `docs/manual.*`) - `batch-check.sh` regenerates the
   maps; commit them with your change - and not `QUEUE.md`: the session that
   ships this takes GS1's row out.
4. `./batch-check.sh --fast`: compile OK, Stale 0 firm and ten fewer soft.
   That is all: **do not run the whole suite** (2026-10-13). It took 25
   minutes in your container and most of a day's credits, its wall-clock
   bounds fail there anyway, and the gate runs it on Claude's machine.
5. Commit with a message that lists each fix as `file:line - was / now`, the
   skipped ones and why, and what you ran with its last lines. Push the
   branch and tell Jerus it is ready. Claude reviews the diff and ships it.

Line endings LF. Done when the branch is pushed with the suite green and the
report in its commit message.
