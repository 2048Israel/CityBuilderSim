# Brief — batch 2 of the bank as a business: losses, capital, and what the bank does with its profit (0.7.8)

You are the implementer. A second agent with a fresh context does the docs pass after you; the orchestrating session keeps the gate (suite, eight-seed ensemble, stress runs) and deploys. You change the code, verify it compiles and passes, and report.

## Where you are

- Working tree `/home/claude/cbs` — it already contains **batch 1 (0.7.7)**, built and gated: read `/home/claude/runs/batch1-notes.md` (what batch 1 changed and why, file by file) and `/home/claude/runs/brief-batch1.md` (its brief) before anything else. The untouched pre-batch-1 tree is `/home/claude/cbs-base`; the tree as batch 1 left it (after its docs pass) is `/home/claude/cbs-b1` — diff against that for your own changes. Never edit either.
- Then `/home/claude/cbs/CLAUDE.md` and `README.md` ("The checks"). Do NOT read `Game.java` whole; use `docs/map/Game.md`, `docs/month-order.md`, grep and `java -cp build/classes ham.citybuildersim.tools.Where <name> [-print]`.
- Build: `/home/claude/bin/build.sh` (model), `/home/claude/bin/build-ui.sh` (all, must be silent). Suite: `/home/claude/bin/checks.sh /home/claude/cbs -q`. **Baseline after batch 1: 60/60 (`BuildMenuCheck` skipped without JavaFX). `InfrastructureCheck`'s "...and its shops can actually be supplied" is a known-fragile line — red on 0.7.6, green on batch 1's final build by chance of the young city's rates; if it goes red again and nothing else does, report it, do not chase it.**
- Line endings mixed; keep each file's own (`/home/claude/bin/jedit.py`; new files LF); `/home/claude/bin/endings-check.sh` must pass at the end.
- Do not deploy, do not touch the PC, do not write to the claude.ai project. Your record goes to `/home/claude/runs/batch2-notes.md`.
- Eight-seed baseline for this batch: `/home/claude/runs/b1b-0..7.txt` (batch 1's final build); its stress setups are `b1bauto-N.txt` (autopilot on) and `b1bheld10-N.txt` (dial held at 10%). `python3 /home/claude/bin/tab.py def b1b` prints a table of any labels. The pre-batch-1 baseline is `/home/claude/runs/def-0..7.txt`.

## Why (the plan is `claude/the-bank-as-a-business-plan.md`; its substance is here)

Jerus: *"lets make banks realistic, and remember, its a business, it wants to make money."* Batch 1 made the bank price like one. What it still does not do is behave like a business with its capital:

- **It sets nothing aside for losses.** A loss hits equity the month a sector is written down or a household discharged. Real banks (IFRS 9) set aside the loan's 12-month expected loss when it is made, top it up to the loan's lifetime expected loss when the borrower weakens, and take write-offs out of that allowance first. The old wait-for-the-loss rule was abandoned after 2008 because it recognised losses too late, which is how banks walked into crises with no cushion. Real credit losses run ~0.4% of loans a year in a normal year (RBC 2025: 0.43%).
- **It has no capital target and no payout policy.** It pays 40% of every positive month as a dividend, capped at its cash — so a bank funding its loans from its own cash pays nothing (`Game.java` ~1650, found by batch 1) — and otherwise retains everything. On batch 1's seed 0 its equity ended at $26.6bn–$50.5bn against a $1.7–10bn book (hundreds of percent of the 8% it needs), so its return on equity reads 1–5% where real banks earn 10–16%. Real Canadian banks: regulators expect 11% of risk-weighted assets (OSFI, June 2026: 4.5% + 2.5% buffer + 1% big-bank + 3.0% stability buffer); banks hold ~13.5%; they pay ~43% of earnings as dividends (RBC 2025) and buy back shares with the rest when they are over their target.
- **Its lending falls off a cliff instead of tightening.** Capacity is zero in resolution, and (since batch 1 removed the premium) nothing at all restrains lending past capacity short of failure — it just funds at the window. A real bank that is short of capital slows its new lending and keeps its profit; it does not stop dead, and it does not lend freely either.

## Jerus's decisions (2026-09-23) — the requirement

- *"the bank chooses"* its capital band, and *"the bank also chooses"* its required return (batch 1 read the return from the market's own pricing of its shares: `Equity.requiredYield()`, 12.5% at the defaults). So: **the regulatory minimum is the city's rule (`Bank.CAPITAL_RATIO`, 8% of risk-weighted assets), and everything above it is the bank's own choice, made by a rule a banker would recognise — not a number the designer typed.**
- Rescue for shares and the government bidding on the exchange are a LATER batch: *"thats even another batch"*. Leave `recapitaliseBank` / resolution as they are, except where this brief says otherwise.

## What to build

### A. A loss allowance (simplified IFRS 9)
- Keep an allowance per book the bank lends from: each sector's business debt, the households' book, (the city's paper and the carry book carry none: the city is the sovereign, and the carry borrowers never default by Jerus's call).
- **Stage 1**: when lending is outstanding and the borrower is sound, the allowance holds the 12-month expected loss: the book × `BASE_LOSS_RATE` (batch 1's through-the-cycle 0.4%).
- **Stage 2**: when a borrower weakens, the allowance for it rises to its lifetime expected loss — for a sector, what the model would actually write off if it defaulted now (the restructure rule: debt above `assets × INSOLVENCY_TRIGGER` is written down to `assets × RESTRUCTURE_TARGET`), scaled by how close it is to that trigger; for households, the debt of the cells that are behind (months of income owed) scaled toward the discharge line. Define "weakens" from what the model already tracks (leverage against `INSOLVENCY_TRIGGER`, `isInsolvent(sector)`, loss streaks, months owed) — pick the simplest definition that a reader of the code would accept as "this borrower is in trouble", and name it with a sentence.
- **Provision** (the income-statement line) = the month's change in the allowance + write-offs the allowance did not cover. A write-off is taken out of the allowance first. Over a full cycle the provisions equal the write-offs; what changes is WHEN the loss is recognised. Net loans on the balance sheet = gross − allowance, and equity = assets − liabilities must still move by exactly the month's net income (the harness already asserts the articulation — keep it true).
- Calibrate against the real ~0.3–0.5% of loans a year in a normal year: measure and report the eight seeds' provisions over lending in growth years; tune only with a sourced sentence.

### B. The bank's own capital target and band
- **Minimum**: `CAPITAL_RATIO` (8%), the city's rule, unchanged.
- **Target** (the bank's choice): the minimum plus a management buffer big enough to take the worst year of losses it has seen and still be at the minimum — the buffer is the larger of the Basel capital-conservation buffer (2.5 points; name it, cite it) and its worst trailing-twelve-month provisions as a share of its risk-weighted book over its recorded history. A bank that has lived through a crisis holds more; a young bank holds the standard buffer. Save whatever history this needs.
- **Top of the band**: the target plus a cushion (name it; real banks run ~2–2.5 points over their requirement — OSFI's 13.5% held against 11% expected).
- Batch 1's capital charge in loan pricing used `TARGET_CAPITAL_RATIO` = 8% + a fixed 3-point `CAPITAL_BUFFER`; point it at the bank's chosen target instead, and retire the fixed buffer.

### C. What the bank does with its profit
- **Under the minimum**: no dividends, no buybacks, and no new lending except what keeps existing borrowers running (restructures, the interest reserve) — see D.
- **Between the minimum and the target**: it keeps all its profit and slows its new lending (D).
- **Inside the band (target to top)**: it pays out a share of after-tax profit — about 45% (RBC 2025: 43%; name and cite it).
- **Over the top**: it pays out the excess above the top, spread over a year (special dividends and/or buying back its own shares — the bank already trades its own shares through the exchange desk; use what exists), so that a bank with far more capital than it needs gives it back to its owners rather than sitting on it.
- **Drop the cap at the bank's cash** (`Game.java` ~1650): a bank's dividend is limited by its capital, not by whether it happens to hold reserves this month; paying from borrowed funds is ordinary for a bank inside its band. Keep the "no dividend while failed / under the minimum" guards.
- This replaces `Equity.PAYOUT` (40% of every positive month) for the bank only; the other companies keep their payout rule. `Equity.requiredYield()` is the market's convention for valuing shares and stays as batch 1 left it.

### D. Lending tightens smoothly with capital
- While the bank stands (equity > 0, not in resolution), capacity is never zero. Instead new lending is governed by the capital ratio: at or above the target, it lends as it does now; between the minimum and the target, the book may grow at most a rate that rises smoothly from ~0 at the minimum to unrestricted at the target (the brief Jerus brought suggested 0–1% a month at the bottom); under the minimum, only what keeps existing borrowers alive. Existing loans always run on at their contracted rates.
- Find where the model actually decides to lend (`BusinessDebtManager` — `isLendingOpen`, `setLendingOpen`, `borrowingRoom`; the household credit line's `lendingOpen`; the carry trade's headroom; car finance) and make that the one place the capital rule is applied. Say in the notes what the investor sees when it is refused (its `getLastInvestment()` string).
- Resolution (equity < 0) stays as it is: `resolveIfFailed`, the rescue button, `recapitalisationNeeded()` — but check `recapitalisationNeeded()`'s standing-bank branch (it asks for capital to 12% of the weighted book whenever the bank is under that, which the Bank tab reads as "under its required ratio"): make it ask for what the bank's own rule needs (to the minimum, or to the target — say which and why).
- `strain()` and the branch decision stay.

### E. Found by batch 1, to fix here because they are the bank's
- The bank's monthly income-statement lines are not saved, so a reloaded city's Income page reads zero until a month is played: save the month's statement lines the Bank tab reads (and the allowance and provision, and whatever the capital target needs).
- `Bank.redenominate` does not rescale the bank's saved last-month figures, so absolute comparisons are off for a month after a currency reform: rescale everything in money units (and the new allowance, target history, anything you add). `DenominationCheck` must stay green.
- `Game.java` ~4282: a failed bank is resolved before the items booked after month-end, so a failure they cause shows a month late — fix if it is contained; otherwise document.

### E2. Four player-facing strings the batch-1 docs pass flagged (runtime text, so an implementer's)
They still describe the premium batch 1 removed: `Inbox.java` ~L254; `BusinessInvestment.java` ~L742 (and `BankCheck` ~L1072 asserts that string contains "credit" — change the two together, keeping the check's intent); `YearBook.java` ~L194, the `bankStrain` column description ("funds abroad" — it funds at the central bank's window since 0.7.0, and past 1 it no longer costs anyone a premium); `ui/UserInterface.java` ~L1719, the no-bank tooltip. Make each say what is true now. And `CapitalFlows.arrivalsAt()` has no caller since batch 1 removed the hot-money bid: delete it if nothing needs it, and say so.

### F. The year book and history
New series with a rule each (RATE averages, FLOW sums, LEVEL takes December): `bankCapitalRatio`, `bankCapitalTarget` (RATE), `bankAllowance` (LEVEL), `bankProvisions`, `bankDividends` (FLOW), `bankReturnOnEquity` (RATE, annualised). `HistoryCheck` / `YearBookCheck` cover them. Getters for everything the Bank tab (batch 3) will show: the ratio, the minimum, the target, the top, the stage of each book, the allowance by book, the month's provision, the payout decision in words (e.g. "rebuilding capital", "paying out 45%", "returning excess capital"), dividends and buybacks this month and over the year, the lending limit the capital rule sets this month.

### G. The Bank tab keeps compiling and stays true
Batch 3 redesigns it. Here: the minimum edits so it compiles and nothing on it is false — the Income page shows Provisions in place of "Loans that died" (with write-offs against the allowance in the detail), the Balance sheet shows the allowance against the loans, and the "under its required ratio" alert reads the real minimum. No redesign.

## Rules that do not move
- Never move a harness's premise. A section asserting behaviour this batch deliberately replaces (e.g. the 40% payout for the bank) is rewritten to assert the replacement, and you say so; a section merely disturbed gets a fixture that causes its condition again.
- Every new constant: `static final`, one sentence above it, a source where it is a real-world figure. House conventions per CLAUDE.md.
- New saved fields: `DataSave` out and in, the `Game` load path, `SaveFileCheck`/`ReadPathCheck`. No `SAVE_FORMAT` bump unless an old save would load WRONGLY; a 0.7.6 and a 0.7.7 save must both load and run (an old bank with no allowance starts with the stage-1 allowance set up on load — say how, and make sure it does not appear as a one-month provision spike that moves the audit).
- `MoneyAudit` must close every month on every seed.
- `GameVersion.VERSION` → "0.7.8" with a changelog entry in the house shape.
- `BankCheck` gains sections, each fixture causing its condition: (7) a new loan is met by its stage-1 allowance; a weakening borrower moves to stage 2 and the allowance rises; a write-off draws the allowance before equity and the provisions over the episode equal the write-offs; (8) a bank that has lived through a bad year chooses a higher target than a young one, never below minimum + 2.5; (9) under the target it retains all profit and its book growth is capped, and the cap rises smoothly to none at the target; under the minimum no new lending but restructures still happen; (10) inside the band it pays ~45% of after-tax profit, over the top it pays out the excess over a year, and a bank with no reserves inside its band still pays; (11) while the bank stands, capacity is never zero; (12) a reload mid-month reads the same income lines, allowance and target.

## What to report (in `/home/claude/runs/batch2-notes.md` and as your final message)
1. What you changed, file by file, one line each, with the WHY in your own words.
2. Suite count against the baseline (60/60), `build-ui.sh` silent, endings kept.
3. The eight seeds (`/home/claude/runs/b2-<seed>.txt`, two at a time is fine) against `b1b-0..7` and `def-0..7`, and the two stress setups (`-Dplaytest.autopilot=true` into `b2auto-N.txt`, `-Dplaytest.policyRate=0.10` into `b2held10-N.txt`) against `b1bauto`/`b1bheld10`: mean population at the end and at month ~1,300, bank failures, written off, unemployment, and the bank's capital ratio, target, return on equity and provisions over lending — medians over growth years and the extremes.
4. **Found on the way**, with file:line.
5. Anything left undone and why.
