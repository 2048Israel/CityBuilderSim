# CD1-H - a current-code handoff for CD1, for ChatGPT

ChatGPT's second task (Claude, 2026-10-13, at Jerus's request). Reading and
writing only: nothing is built or run, and no code changes. The same shape as
your R0 handoff (`docs/studies/research-1900.md` §12.1), which was accurate
and useful; this one shorter.

## Why

CD1, "the curve", rebuilds how every price of money in the city reads the
policy dial (`docs/studies/spec-city-debt.md` §3 and §8). It runs after the
freight batches FR0-FR6, which do not touch the debt code, so a map of
today's code stays true until then. A good map lets CD1's implementer read 50
lines instead of exploring the tree.

## What to write

A subsection **8.1 Current-code handoff for CD1 (2026-10-13)** at the end of
§8 of `docs/studies/spec-city-debt.md`, **at most 50 lines**:

1. **The seams.** Where today's code turns the dial and expectations into
   the city's rates and prices: `Expectations`, `CentralBank`, `DebtManager`,
   `BondMarket`, `Rollover`, `Debt`/`DebtQuote` and the bond classes
   (`LongTermBond`, `MediumTermBond`), with each method's name and what it
   does now, checked against the source (`docs/map/NAME.md` gives every
   method's line). Then everything else that reads those rates - §3.4 of the
   spec lists what should; find what does.
2. **The traps.** What CD1 could break without noticing:
   - money created or lost when a price changes;
   - a flow a screen or check needs next month but nobody saves;
   - a line in one phase of the month and not the other
     (`docs/month-order.md`);
   - an old save that would load *wrongly*, which needs the save-format bump
     (`CLAUDE.md`, the standing rules).
3. **The harnesses CD1 needs.** For each one, the fixture that *causes* the
   condition and what it asserts against the model's own constants; name the
   existing harnesses that already cover part of it (`BondCheck`,
   `ExpectationsCheck`, `CentralBankCheck`, `MonetaryCheck`, `MoneyCheck`).

State what you checked in the source and what you inferred. Mark a design call
as one for Jerus rather than a rule.

## How

- Branch `chatgpt/cd1-handoff` from `main`; one commit there.
- The commit message says what was read, what was edited, and that nothing
  was built or run.
- If you reach the free tier's message limit, commit what you have, say where
  you stopped, and stop.
- Leave `QUEUE.md` alone; the session that ships this takes CD1-H's row out.
