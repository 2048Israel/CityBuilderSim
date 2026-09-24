# Batch 2 (0.7.8): the bank as a business with its capital - implementer's record

Working tree `/home/claude/cbs`. Nothing outside it was edited (probes ran on copies in the scratchpad), nothing was deployed, and nothing was written to the project. Diff against `/home/claude/cbs-b1` for the whole batch, and against `/home/claude/cbs-b2v0` (the gate's snapshot of round 1) for round 2.

# Round 2: the gate's two changes

**What ships now is round 1 plus change (1). Change (2) was measured, was clearly worse on the whole, and is reverted to cbs-b2v0's behaviour, as the gate allowed.**
- The round-2 variant's sources are kept in `/home/claude/runs/b2c-variant-src/` (11 files).
- Suite: **59/60**. Only the known-fragile InfrastructureCheck line is red.
- `build-ui.sh` is silent, `endings-check.sh` passes, and the findstr in `Build EXE.bat` still matches one line.
- The final build's files differ from cbs-b2v0 in seven files: `Bank.java`, `Exchange.java`, `Game.java` (a comment), `ExchangeCheck.java` (a comment), `ReadPathCheck.java`, `EducationCheck.java` and `GameVersion.java`.

| setup | build | mean pop | pop @~1300 | GDP/mo | failures | written off | unemp (end) | worst px | clean |
|---|---|---|---|---|---|---|---|---|---|
| default | 0.7.6 (def) | 148,668 | 33,350 | $743.8M | 19 | $102.8B | 13.3% | 1.39 | 8/8 |
| default | 0.7.7 (b1b) | 135,993 | 26,733 | $769.0M | 4 | $119.3B | 17.4% | 1.63 | 7/8 |
| default | round 1 (b2 = cbs-b2v0) | 146,130 | 31,890 | $812.6M | 85 | $166.5B | 11.8% | 3.63 | 7/8 |
| default | (1)+(2) (b2c) | 140,529 | 30,912 | $724.7M | 68 | **$233.2B** | **21.5%** | 2.90 | 6/8 |
| default | **(1) only, shipped (b2d)** | **151,979** | **32,761** | **$820.8M** | **92** | $120.8B | 17.9% | 2.08 | 8/8 |
| autopilot | 0.7.7 (b1bauto) | 162,751 | 25,239 | $805.8M | 1 | $128.6B | 15.7% | 1.50 | 8/8 |
| autopilot | round 1 (b2auto) | 158,485 | 39,053 | $978.2M | 74 | $145.9B | 12.2% | 1.51 | 8/8 |
| autopilot | (1)+(2) (b2cauto) | 143,606 | 26,280 | $795.3M | **114** | $138.6B | 16.9% | 1.72 | 8/8 |
| autopilot | **(1) only, shipped (b2dauto)** | **160,676** | 36,275 | $939.5M | **69** | $143.1B | 14.3% | 1.69 | 8/8 |
| held 10% | 0.7.7 (b1bheld10) | 7,211 | 1,127 | $48.8M | 40 | $10.2B | 20.2% | 5.64 | 0/8 |
| held 10% | round 1 (b2held10) | 3,403 | 1,066 | $10.9M | 176 | $7.9B | 16.1% | 1.45 | 0/8 |
| held 10% | (1)+(2) (b2cheld10) | 3,285 | 1,075 | $11.8M | 195 | $7.7B | 21.6% | 1.66 | 0/8 |
| held 10% | **(1) only, shipped (b2dheld10)** | **2,443** | 1,090 | $8.3M | **266** | $9.2B | 18.8% | 1.33 | 0/8 |

- **Unemployment "at the end" is one month.** Per seed it runs 0–61% on every build: b2d's seeds read 8–34%, and b2's 0–24%. Read the 11.8 → 17.9 move with that in mind.
- **Held-10% "clean 0/8" is the pre-existing finding** of homeless households in a stalled town, as on every build.

**The bank's capital in growth years (median of the seeds' medians):**

| build | ratio | target | ROE | paid out / profit | new shares issued / seed | provisions / average loans, whole run |
|---|---|---|---|---|---|---|
| b2 (round 1) | 26.7% | 16.5% | 21.1% | 173% | $28–86bn | 1.54%/yr |
| b2c ((1)+(2)) | 58.7% | 55.8% | 13.3% | 80% | $3.5–15bn | 3.08%/yr |
| **b2d ((1), shipped)** | **21.3%** | **16.5%** | **23.8%** | **83%** | **$0.4–2.1bn** | **0.98%/yr** |

## (1) The bank issues shares only to rebuild capital: kept

**The change.**
- `Bank.makesMarketInOwnShares()` is split into `buysBackOwnShares()` (standing and at or over its target, as before) and `issuesOwnShares()` (standing, lending, and **under** its target).
- `Exchange.deskCanBuy(BANK)` reads the first; `deskCanSell(BANK)` reads the second.
- Both keep the register's NEW/BAD-record guard (`ownSharesTrade()`) and the buyback pace.

**What else reads the bank's offering** (`Exchange.java` / `Equity.java`):
- **The world (takeMonth step 2):** foreigners buy when the bank's yield beats the world hurdle, up to `deskCanSell`. What they can't have goes to `noteUnfilled()`, and that lifts the bank's quote: mid = fair × (1 − pressure), and with the desk holding none of its own shares, unfilled demand is negative pressure, up to CEILING × fair.
- **The households (step 3, `buyForHouseholds`):** capacity = `deskCanSell` × ask. When it is zero, their money goes to the next company on their yield list, and the unfilled part also lifts the quote.
- **Buybacks:** emigrants' and the world's sales (steps 1–2) and a household's distress sale (`sellForHousehold`) read `deskCanBuy`, unchanged.
- **The register:** `Equity.deskSellsToHouseholds/deskSellsAbroad` add to the bank's shares on issue. `Equity.getRegime()` feeds the guard.
- **What households no longer buy at the target** stays in deposits or goes to the other listed companies.

**Measured.**
- MonetaryCheck's month-late noise is unchanged at 0.000 / 0.021 / 0.022 / 0.005.
- **Payout over 100% of profit goes away.** Paid out is 83% of profit in growth years (seeds 71–87%), against 131–183%.
- New shares issued fell from $28–86bn a seed to $0.4–2.1bn, and dividends over the eight default seeds from $746bn to $284bn.
- On the default and autopilot runs it is neutral to good:
  - population +4% and +1.4%;
  - failures 92 against 85, and 69 against 74;
  - written off $121bn against $167bn.
- **Held at 10% it is worse: 266 failures against 176, and 2,443 people against 3,403.**
  - There the shares round 1 sold at the target had been a buffer: $0.3–0.8bn a seed, returned over a year.
  - A bank under its target pays no dividend, so households, who buy by yield, don't buy what it may still issue: $4–22M a seed.
  - A probe that let it issue under its target on a bad record too (b2eheld10, scratch tree only) issued $7–21M and failed 253 times. The record guard is not what blocks it.
- I kept (1) because it is the rule the gate asked for and it is neutral or better on the main setups. **The held-10 cost is the gate's to weigh.**

## (2) A concentration stress test and a price for it: measured, clearly worse, reverted

**What was built** (sources in `b2c-variant-src/`):
- **Target.** The target = the minimum + max(the conservation buffer, the largest single sector's stress loss / risk-weighted assets), with no cap.
  - The stress loss is max(`STRESS_LGD` × principal, the restructure rule on current assets), capped at principal, less that sector's allowance.
  - `STRESS_LGD` = 1 − RESTRUCTURE_TARGET / INSOLVENCY_TRIGGER = 60%: a borrower defaults owing 1.5× its assets and is written down to 0.6× of them.
  - It was saved in the allowance map and restored on load.
- **Ordinary target.** Every loan's capital charge, the rationing and the call on the city used an ordinary target of the minimum + the conservation buffer (10.5%). The bank's own target governed the payout, the desk's own shares and the carry trade's room.
- **Premium.** A sector owing more than 25% of the bank's equity (`LARGE_EXPOSURE_SHARE`, BCBS large-exposures framework, April 2014) paid `concentrationPremium()` on new borrowing: (STRESS_LGD − 1.0 × 10.5%) × (required return − funds-transfer price).
- **Checks.** BankCheck (8) was rewritten for it and HistoryCheck 2e adjusted. Both passed.

**The concentration charge big sectors actually paid (default seeds, checkpoint lines):**

| seed | sector | exposure / bank capital | quoted rate | premium in it |
|---|---|---|---|---|
| b2c-0 | Real Estate | 1.28× | 10.32% | +5.83 |
| b2c-0 | Manufacturing | 1.14× | 14.10% | +5.83 |
| b2c-3 | Automotive | 3.64× | 17.49% | +5.87 |
| b2c-3 | Manufacturing | 2.39× | 18.56% | +5.88 |
| b2c-4 | Real Estate | 1.26× | 9.03% | +6.02 |
| b2c-4 | Manufacturing | 1.44× | 11.62% | +6.10 |
| b2c-1 | Real Estate | 4.50× | 18.28% | +3.28 |
| b2c-5 | Heavy Industry | 1.62× | 13.72% | +5.43 |

- **How often it was paid.** 8,200–10,400 sector-months per seed out of ~60,000; the premium averaged 5.3–5.8 points. Real Estate paid it in ~3,950 of ~4,000 months, and Manufacturing and Automotive in ~2,100–2,500 each.
  - A sector's exposure peaked at 9–19× the bank's capital.
  - Held at 10% the premium is 1.2 points, because the cost of equity over funding is small at that dial.
- **The stress target.** The largest default averaged 48–62% of the weighted book, so the target ran at 51–65% (median 56%) and the ratio at 59%.
- **Did early growth suffer?** On average little: 30,912 at month ~1,300 against 31,890. But two seeds stalled badly: seed 5 had 5,236 against 39,386, and seed 7 13,121 against 50,871. Seed 0 grew faster (56,069 against 25,676).
- **Why it was worse.**
  - The premium didn't slow the concentrated borrowers. Real Estate paid it almost every month. It raised their costs, and write-offs went up 40% ($233bn against $167bn, 2.9% of loans a year against 1.4%), with unemployment at 21.5%.
  - Failures fell only 85 → 68. The bank lent freely at the ordinary target while far short of its concentration target. On seed 0, 9 of 11 failures came with the bank in "rebuilding", under its own target. The month before, its ratio was 8–71% against a target of 32–77%, with the largest stress loss often above its whole equity.
  - A default could also exceed the stress estimate. On seed 0 at m3898, $1.06bn was written off against a $0.5bn largest stress loss.
  - On the autopilot it failed 114 times against 74, and held at 10% 195 against 176.
- **The 8.5-point cap.** With the ordinary charge struck at 10.5%, the cap wasn't needed for pricing, so the variant had none. After the revert the cap is back, because round 1's target prices every loan again (see MAX_BUFFER's javadoc, which now records this experiment).

**Harness notes for round 2.**
- `EducationCheck`'s "...counted in the revenue total" went red under the variant. Its sum left out `NationalAccounts.getCentralBankRemittance()`, part of the revenue total since 0.7.0, and the variant's city happened to remit in the fixture month.
  - I added the term. It passed with it, confirming the cause.
  - The premise, that student interest is counted in the total, is unchanged. The term is kept: it is right on every build.
- BankCheck (8) and HistoryCheck 2e are back at round 1's text with the revert.

**Where that leaves the failures.** Neither a price nor a bigger buffer fixes them.
- A whole sector is one borrower, and the stress test shows the book: the largest default is about half the weighted book.
- What has not been tried is lending to the concentrated borrower being governed by the bank's own concentration target, while every other borrower lends at the ordinary one. That is a soft limit, close to what was withdrawn on 2026-09-11 (todo "Not doing"). It is the gate's call.

---

# Round 1 (cbs-b2v0), as reported before the gate

## Headline for the gate

**The build is clean.**
- Suite: **59/60**. The one red line is `InfrastructureCheck`'s known-fragile "...and its shops can actually be supplied" (shelves 65% against 100%). The brief says it was red on 0.7.6 and green on batch 1 by chance; I did not chase it.
- `build-ui.sh` is silent, and `endings-check.sh` passes.
- `GameVersion.VERSION` is "0.7.8", with a changelog entry. `Build EXE.bat`'s findstr still matches one line. `SAVE_FORMAT` is 27, unchanged.

**The batch does what the brief asked, and the bank now fails far more often. That is the result the gate has to decide on.**

| setup | build | mean pop | pop @~1300 | GDP/mo | bank failures | written off | unemp | worst px | clean |
|---|---|---|---|---|---|---|---|---|---|
| default | 0.7.6 (def) | 148,668 | 33,350 | $743.8M | 19 | $102.8B | 13.3% | 1.39 | 8/8 |
| default | 0.7.7 (b1b) | 135,993 | 26,733 | $769.0M | 4 | $119.3B | 17.4% | 1.63 | 7/8 |
| default | **0.7.8 (b2)** | **146,130** | **31,890** | **$812.6M** | **85** | $166.5B | **11.8%** | 3.63 | 7/8 |
| autopilot | 0.7.7 (b1bauto) | 162,751 | 25,239 | $805.8M | 1 | $128.6B | 15.7% | 1.50 | 8/8 |
| autopilot | **0.7.8 (b2auto)** | 158,485 | **39,053** | **$978.2M** | **74** | $145.9B | 12.2% | 1.51 | 8/8 |
| held 10% | 0.7.7 (b1bheld10) | 7,211 | 1,127 | $48.8M | 40 | $10.2B | 20.2% | 5.64 | 0/8 |
| held 10% | **0.7.8 (b2held10)** | **3,403** | 1,066 | $10.9M | **176** | $7.9B | 16.1% | 1.45 | 0/8 |

- **The city is bigger and busier on the default and autopilot runs.**
  - Default: mean population is +7% on 0.7.7, 1.7% under 0.7.6, and +19% at month 1,300. Unemployment is 11.8% against 17.4%.
  - The bank gave its owners $746bn over the eight default seeds. 0.7.7's bank sat on $24–151bn of equity against $2–30bn books, 366–2,376% where 8% is required. 0.7.8's ended at $0.4–3.2bn, 18–122%.
- **The bank fails ten times a run where it failed about once in ten runs.** The city put $65.4bn of capital back in over the eight default seeds, against $0.5bn.
  - The cause is measured in section 4. A whole sector is one borrower, so each seed's worst year cost 37–185% of the weighted book. Through the run that is 1.5% of loans a year, against a real bank's 0.4%.
  - A bank holding a real bank's 16.5–19% cannot take that. 0.7.7's survived on capital it never paid out.
  - A probe with the excess not returned (C's last clause off): seeds 0 and 3 failed 3 times each, not 14 and 12, but ended with 78,048 and 127,198 people against 150,518 and 141,302.
- **Held at 10% it is worse on every count.** It fails 176 times against 40, with 3,403 people against 7,211. That is not the payout rule and not stage 2 (probes in 4).
- **One deviation from the brief: `Bank.MAX_BUFFER`.**
  - The target's buffer is capped at 8.5 points, the whole Basel III stack, so the target is 16.5% at most.
  - Uncapped, the brief's rule gave targets of 48–108% after one restructure, prime of 10–19% and a stalled city (8,348 people).
  - The javadoc has the sourced sentence.
  - The cap binds in every seed's growth years: the target reads 16.5% throughout.

---

## 1. What changed, file by file, and why

### The model

**`Bank.java`**: the whole mechanism. There is a new banner section, THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8), at ~L2545.
- **A, the allowance.**
  - `provide()` strikes each book's allowance at month end.
    - Stage 1 is `BASE_LOSS_RATE` of the book.
    - Stage 2 is `lossIfDefaulted()` scaled by closeness to the line. For a sector the line runs from `SECTOR_WATCH_LEVERAGE` (= `BusinessDebtManager.MAX_LOAN_TO_ASSETS`, 0.9) to `INSOLVENCY_TRIGGER`. For the families it runs from `HOUSEHOLD_WATCH_MONTHS` (half the credit ceiling) to `BANKRUPT_AT_MONTHS`.
    - "Weakens" is defined as past the point where the shortfall desk would not lend it another dollar. It was measured before it went in: the 24-month default rate is 1.3–1.5% under 0.9 and ~50–100% over it.
  - Write-offs are recorded per book through `writeOffSector()` and `writeOffHouseholds()`. They are drawn from the allowance first (`getAllowanceUsed()`).
  - `provisions()` = the allowance's move + write-offs. `afterLosses()` uses provisions.
  - `netLoans()` = the book − the allowance, and `totalAssets()` uses net loans, so equity articulates.
  - `openAllowance()` is the old-save setup. It sets the opening allowance equal to the allowance, so there is no provision.
- **B, the target.**
  - `recordLosses()` in `closeMonth()` fills a 12-month ring. It records the worst trailing-12-month provisions over the average weighted book.
  - That rate has a founding guard: the denominator is never under what the branches' founding capital carries. Without it, BankCheck's city read a 141% year on a $9M book.
  - `capitalBuffer()` = min(`MAX_BUFFER`, max(`CONSERVATION_BUFFER`, worst)).
  - `capitalTarget()` and `capitalTop()` (+`MANAGEMENT_CUSHION`).
  - `capitalCharge()` and `strikePrices()` read `capitalTarget()`. `CAPITAL_BUFFER` and `TARGET_CAPITAL_RATIO` are retired.
- **C, the payout.**
  - `dividendDue()` pays nothing under the target and `PAYOUT_IN_BAND` × the profit after tax inside the band. Over the top it adds `excessCapital() / EXCESS_PAYOUT_MONTHS`, and never pays more than keeps it at the target.
  - `payOwners()` records what the rule read (`getPayoutProfit/Excess/OverTarget()`). `payoutStance()` and `payoutDecision()` give it in words.
  - `topEquity()` never falls under the branches' founding capital, so a young bank does not pay out what it opened with.
- **Its own shares.**
  - `buyBackOwnShares()` and `issueOwnShares()` book capital, not income. Until 0.7.8 they were trading income, and on 0.7.7's seed 0 that was $8.2bn of $8.65bn of "trading profit", taxed and paid out.
  - `makesMarketInOwnShares()`: the desk makes the market in the bank's shares only while the bank stands at or over its target.
  - Two other rules were measured first (the comment block at ~L2968 has both).
    - Buying back only over the top up to the excess, and issuing only while rebuilding, moved MonetaryCheck §6's month-late noise to 0.073.
    - Ratio-gated buybacks with no issuance gave 252 held-10 failures.
  - `dividendsOverYear()` and `buybacksOverYear()` are 12-month rings. `returnOnEquity()` is annualised on the opening equity.
- **D, lending.**
  - `lendingGrowthLimit()`:
    - ∞ at or over the target, or with no branch;
    - 0 at or under the minimum, or when failed;
    - `RATIONED_GROWTH` × x/(1−x) between them. x is the position from the minimum to the target, so the limit is 0 at the minimum, 1% halfway and ∞ at the target.
  - `lendsOnlyToKeepBorrowersGoing()`, `lendingLimit()` and `lendingStance()`.
  - `headroom()`, which the carry trade reads, is also capped at equity/target − the weighted book, so the carry never takes the bank under its target.
- **`recapitalisationNeeded()`:**
  - A standing bank is asked for nothing at or over the minimum. Under the minimum it is asked for what reaches its **target**.
  - First I made it ask for the minimum. Measured, the playtest advisor then topped the bank up to exactly 8.00%, where the bank's own rule lends nothing new. Held-10 seed 0 spent 538 months lending only to keep borrowers going.
  - The trigger is the city's rule and the amount is the bank's. A failed bank is asked for the same as before.
- **E.**
  - `monthLinesToSave()`/`restoreMonthLines()`: 46 values, every flow `startMonth()` clears, plus the opening allowance and the payout fields.
  - `allowanceToSave()`/`restoreAllowance()`.
  - `capitalRecordToSave()`/`restoreCapitalRecord()`: the loss ring, the worst rate, and the dividend and buyback rings.
  - `redenominate()` now scales the last-month figures (the 0.7.7 miss), `buybackGains`, every allowance map and field, the payout fields and the rings.
  - `reset()` clears it all.
- **F: getters for batch 3.** These are the stage by book (`getStage()`, `getHouseholdStage()`), the allowance by book, `getSectorsWatched()`, `getProvisionCharge()`, `getWriteOffsBeyondAllowance()`, the ratio, minimum, target, top, stance and limit.

**`Game.java`**
- `provideForLosses()` (L1836) runs after the first month-end `refreshBank()` (L4229). `sectorPositions()` gives each sector its principal and assets, crediting borrowing since the insolvency check as assets (construction under way).
- Per-sector write-offs go to `writeOffSector()`, and household ones to `writeOffHouseholds()`.
- `payDividends()` calls `bank.payOwners()`. **The cap at the bank's cash is gone.**
- `tradeShares()`: the stale "flush" override is removed.
- **The capital rule** is read once at the top of the month (L4778–4783) and handed to `BusinessDebtManager.setCapitalRule()` and `HouseholdBalance.setCapitalRule()`.
- **E, the late failure:** a second `bank.resolveIfFailed()` at L4444, after the desk's late items (dividends, `tradeShares()`, `sellPaperForSpread()`). A failure they cause is resolved in its own month.
- `consider()` (L2329): a refusal by the capital rule says so, instead of blaming the land. **What the investor sees:**
  - Under the minimum: "Holding: the bank is under its capital minimum and lends only to keep its borrowers going - X would need credit".
  - Rebuilding: "Holding: the bank is rebuilding its capital and lets a borrower's debt grow N.NN% this month - X would need more credit than that".
- Save and load:
  - `setBankAllowance`, `setBankCapitalRecord` and `setBankMonthLines`.
  - On load, `restoreCapitalRecord()`, then `restoreAllowance()`. If there is none (an old save), at the very end of load: `pushBalanceSheetInputs()` + `refreshCreditAssets()`, then `openAllowance()`.
  - Reading assets before the balance sheets had their buildings gave $370M against a live $1.6M. The setup now equals the live bank's allowance exactly.

**`BusinessDebtManager.java`**
- `setCapitalRule()` sets `capitalRoom()` = the base × (1+g) − the principal.
- `roomUnder()` takes the tighter of the ceiling and the capital room. `canFundProject()` refuses on the capital room or on keep-going-only, and records `wasRefusedForCapital()`.
- The interest reserve reads the ceiling only, so a borrower's interest is always carried (keep-going).
- `principalJudged` is recorded at the insolvency check for the allowance. `RESTRUCTURE_TARGET` is public, and `redenominate()` scales the new fields.

**`Household.java` / `HouseholdBalance.java`**
- Each cell gets a `capitalCeiling` = its debt × (1+g) (∞ when free). `fundShortfall()` lends past it only the month's interest. `wantOf()` and `offerOf()` read `lendableRoom()`.
- `lossAllowance()` and `debtInTrouble()` feed the families' stage 2.
- The ceiling is re-set every month, so it is not saved. It is scaled in `redenominate()`.
- **Before this, household credit was never shut by a failed bank at all** (found on the way). Only the businesses' `setLendingOpen()` existed (cbs-b1 `Game.java:4693`).

**`Exchange.java`**
- The bank's own shares route to `buyBackOwnShares()`/`issueOwnShares()`, not `deskPays()`/`deskReceives()`.
- A `dealer` field is read live at every deal. It replaces `Companies.bankFlush()`, which was struck once a month and read false after a reload (found on the way).
- `deskCanBuy()`/`deskCanSell()` for the bank require `ownSharesTrade()` (not the NEW/BAD regime) and `makesMarketInOwnShares()`, at the buyback pace.

**`DataSave.java`**: `bankAllowance`, `bankCapitalRecord` and `bankMonthLines`, each with a javadoc.

**`HistorySave.java`, `YearBook.java`**
- Six series:
  - `bankCapitalRatio`, `bankCapitalTarget` and `bankReturnOnEquity` are RATE. The ratio and the ROE are clamped to ±10.
  - `bankAllowance` is LEVEL.
  - `bankProvisions` and `bankDividends` are FLOW.
- The money series are scaled on reform.
- **E2:** the `bankStrain` column description now says it is capacity (the tighter of capital and deposits), clamped at 10, and costs nobody a premium since 0.7.7.

**E2, the strings:**
- `Inbox.java` ~L254 (the failed bank: the families draw only for interest, no business borrows to build or cover a loss).
- `BusinessInvestment.java` ~L742 ("nowhere in the city to bank - its credit comes from outside, at the window's price"), together with BankCheck's assertion (window/credit, not "punitive").
- `ui/UserInterface.java` ~L1719, the no-bank tooltip.
- **`CapitalFlows.arrivalsAt()` is deleted**: it had no caller. Its comments are fixed.

### Harnesses

**`BankCheck.java`**
- (2) reads `capitalTarget()`.
- (6) is **rewritten**, because the batch replaces the 40% payout for the bank. It now asserts the payout rule through `getPayoutProfit/Excess/OverTarget()`.
- (8)'s articulation includes shares issued and bought back (`capitalMoved()`), and the desk's parts exclude the bank's own shares.
- (10)'s cash arithmetic adds the allowance's move.
- New sections (7)–(11) in `theBankAsABusinessWithItsCapital()`:
  - (7): stage 1 on a new loan; stage 2 on weakening; the write-off draws the allowance; provisions over the episode equal the write-offs.
  - (8): a bank with a bad year chooses a higher target, never below 10.5%.
  - (9): under the target it retains and caps growth, and the cap rises smoothly to none; under the minimum it lends nothing new but restructures still run.
  - (10): 45% in the band, the excess over a year, and a bank with no reserves still pays.
  - (11): capacity is never zero while it stands.
- (12), after 8b: a mid-month reload reads the same lines, allowance and target, and a fabricated 0.7.7 save gets its stage-1 allowance with no provision.

**The other harnesses**
- `HistoryCheck` 2e: the six series and their rules. 3b is extended.
- `YearBookCheck` 4c: `theBanksCapitalFoldsByItsKinds()`.
- `SaveFileCheck`: the allowance, provision, target, income lines, month lines, capital record and allowance by book round trip.
- `ReadPathCheck`: the new fields in the bank print, and every new getter in the read sweep.
- `ExchangeCheck`: the stale flush override is removed.
- **`LongPlaytest`**:
  - A yearly bank record, with a summary line: the medians and extremes over growth years of the ratio, target, ROE, provisions over loans and payout.
  - An over-the-run line: dividends, buybacks and issues, provisions **as a share of the average loans a year** and written off likewise, months rationed and keep-going, stance months, the worst year and the target.
  - `bankEra` gains capital, target, top, stance and allowance. Its ROE is clamped at ±999%: a failed bank's read 18 digits.
  - The round trip compares the allowance, target, provision, net income and `dividendsOverYear()`.
  - The trace `-Dplaytest.capital=true` prints CAP lines (every provision or write-off month) and FAIL lines (every month the bank went under, with the month's flows).

### Interface (G, the minimum)

**`ui/BankScreen.java`**
- The Income page shows "Provisions for losses", with write-offs against the allowance in the detail, where "Loans that died" was.
- The desk lines exclude the bank's own shares.
- The balance sheet has an allowance line. The equity movement adds own shares bought back and issued, and the gap formula includes them.
- The ratio page has a target and top line. The "under its required ratio" alert reads the minimum, for a standing bank only (a failed bank has its own alert). It gives what takes the bank back to its target.

**`ui/SectorScreen.java`**: the BANK's owners note describes the capital policy.

---

## 2. Suite and interface build

- `checks.sh -q`: **59/60**. `InfrastructureCheck` is red on the known-fragile line (shelves 65% against 100%). Every other harness is green, including BankCheck (all sections), MonetaryCheck, DenominationCheck, SaveFileCheck, ReadPathCheck, HistoryCheck, YearBookCheck, ExchangeCheck, StaleCheck and LongPlaytest. The output is in `allchecks-b2.txt`.
- MonetaryCheck §6: the same rows held a month late differ by 0.000 / 0.021 / 0.022 / 0.005 points, against 0.05. The premise is unchanged.
- `build-ui.sh` is silent. `endings-check.sh`: "all files keep their own".
- **Old saves.** A 0.7.6 or a 0.7.7 save has none of the three new keys. BankCheck (12) loads a fabricated one:
  - the allowance is set up on load from the book, by the same rules;
  - the opening allowance is set equal to it, so the month's provision is 0 and the audit does not move;
  - the loss record starts empty, which is a young bank's 10.5% target;
  - the month lines read as a month not yet played.

---

## 3. The eight seeds, and the stress setups

Files: `b2-N`, `b2auto-N`, `b2held10-N`. They were built from the final tree before the VERSION string and changelog edit; that edit changes no behaviour. Tables: `python3 /home/claude/bin/tab.py def b1b b2 b1bauto b2auto b1bheld10 b2held10`. The totals are in the headline table.

**Default, per seed** (final population, failures):

| seed | b2 | b1b | def | b2 failures | b1b failures |
|---|---|---|---|---|---|
| 0 | 150,518 | 126,656 | 130,978 | 14 | 1 |
| 1 | 169,813 | 141,408 | 155,840 | 12 | 1 |
| 2 | 152,287 | 146,772 | 164,579 | 10 | 1 |
| 3 | 141,302 | 158,677 | 141,531 | 12 | 1 |
| 4 | 158,090 | 137,058 | 138,071 | 9 | 0 |
| 5 | 112,607 | 126,629 | 160,267 | 11 | 0 |
| 6 | 119,650 | 111,831 | 142,216 | 4 | 0 |
| 7 | 164,775 | 138,911 | 155,859 | 13 | 0 |

- Seed 1 has one month of negative GDP (m774, net exports −$31.5M on a raw-materials import). Every month passed the audit and every reload matched on all 24 runs.
- Seed 7's price level peaked at 3.63 early: 2.48 at m619, 2.68 at m770, back to 1.14 by m1323. It was a boom: GDP was $85.0M a month at m619 against b1b's $15.9M, and 11,765 people against 6,375. The young bank returned its surplus at a dial near zero. I did not investigate further.

**The bank's capital, default (growth years; medians per seed, then the extremes over all seeds):**

| measure | median of seeds' medians | seeds' medians | extremes (any growth year) |
|---|---|---|---|
| capital ratio | 26.7% | 24.5–31.3% | 10.2% – 515% |
| target | 16.5% | 16.5% (all) | 10.5% (young bank) – 16.5% |
| return on equity | 21.1% | 16.8–26.4% | −132.6% – 308.2% |
| provisions / loans (growth years) | 0.02% | 0.02–0.04% | −31.4% – 67.3% |
| provisions / average loans, **whole run** | 1.54% a year | 0.48–2.40% | — |
| written off / average loans, whole run | 1.43% a year | 0.48–2.40% | — |
| paid out / profit | 173% | 131–183% | — |

- Rationed months are 217–862 per seed. Keep-going-only months: 0 on every default seed.
- Worst year on record: 37–185% of the weighted book.

**Autopilot:** ratio 28.1%, ROE 18.7%, provisions 0.03% in growth years, 1.08% a year through the run, rationed 107–301 months, 74 failures.

**Held 10%:**
- Ratio 72%. ROE 3.1%. Provisions 13.9% a year through the run (2.6–16.8% by seed): the tiny city's book is wiped out repeatedly.
- Rationed 76–1,066 months, and 176 failures.

**How to read the provision figures.**
- In growth years they are ~0.02–0.04% of loans, not the brief's ~0.3–0.5%. The model has no steady trickle of small defaults: a sector is one borrower, and its loss comes in one piece. In a year without a restructure, the provision is only stage 1's 0.4% on the book's *growth*.
- Through the cycle they are 1.5% of loans a year on the default run (0.5–2.4%), three to four times a real bank's. **Nothing was tuned.** A sourced change would be to the loss structure (exposure limits, partial recovery), not to the allowance.
- The allowance does anticipate. On the two default seeds traced (`-Dplaytest.capital=true`), 70.5% and 63.7% of the money written off was already in the allowance before the month of the write-off. On held-10 it was 34–47%.

**"Paid out" over 100% of profit.**
- The desk issued $28–86bn of the bank's shares per default seed to households that wanted them while the bank was at or over its target, and the excess this created was returned as dividends.
- It is the same churn 0.7.7 had: it issued $12.97bn and bought back $4.74bn on seed 0. 0.7.7 booked it as trading income; now it is capital and comes back as a dividend.
- It does not cause failures: it adds capital while the bank is at its target.

---

## 4. Why the bank fails more: the probes

All probes ran on copies of the tree in the scratchpad, not on the working tree.

| run (seeds 0 / 3) | failures | final pop |
|---|---|---|
| b2 default | 14 / 12 | 150,518 / 141,302 |
| **probe C** (no excess returned; 45% in band only) | **3 / 3** | 78,048 / 127,198 |
| probe S (no stage 2: stage 1 only) | 12 / 10 | 148,027 / 167,859 |
| b2 held-10 | 27 / 30 | 2,278 / 2,043 |
| probe C, held-10 | 25 / 22 | 2,681 / 2,262 |
| probe S, held-10 | 21 / 25 | 2,695 / 2,873 |

- **Default runs: the failures are C's last clause** working as designed.
  - A bank that returns everything over its top holds 16.5–19% (plus founding capital). A sector restructure takes 37–185% of the weighted book in its worst year, so the bank goes under.
  - Kept, the capital survives it, as 0.7.7's did. But the city is smaller (−48% and −10% on the two seeds): the returned billions are demand.
  - Stage 2 barely matters here. It moves the loss earlier; it does not create it.
- **Held-10: neither C nor stage 2.**
  - The FAIL trace shows a one-branch bank with a $37–68M book, often in one sector, taking $25–31M write-offs against $14–28M of equity.
  - 0.7.7's bank took the same write-offs: m146, m472 and m739 match on 0.7.7 seed 0 traced with `-Dplaytest.fx`. It came into them with about twice the equity: ~$38M before m991's $31M write-off, against 0.7.8's ~$19M at m949.
  - 0.7.7's advisor topped the bank up to 12% whenever it dipped under: 334 top-ups on seed 0. Only 2 of them were of a bank at or under zero, so the late-failure fix is not what changed the count.
  - My reading: at a 10% dial, 0.7.8's small bank earns less. It prices on a 16.5% target, not 11%. It rations. Its carry trade is capped at its target, not at 10%. So it never rebuilds between hits. **I did not tune any of it**, because every part of it is the brief's rule.
- **The real fix is not a buffer.** A real bank is not one sector's lender; Basel's large-exposure standard caps one counterparty at 25% of Tier 1.
  - Here a sector is a borrower, and one sector's restructure is routinely over the bank's whole equity.
  - A single-borrower exposure limit, or partial recovery in the restructure, is the next question. It is not in this brief.

---

## 5. Found on the way

1. **The bank's own shares were trading income.** Until now, cbs-b1 `Exchange.java:500/518/531/544/777` routed them through `deskPays()`/`deskReceives()`. On 0.7.7 seed 0 that was $8.2bn of $8.65bn of trading profit, taxed and paid out. Fixed: they are capital now.
2. **`Companies.bankFlush()`** (cbs-b1 `Exchange.java:488, 569, 583`) was struck once a month and read false after a reload. Fixed: it is read live.
3. **Household credit was never gated by the bank's state** (cbs-b1 `Game.java:4693` gates businesses only). Fixed: a failed or under-minimum bank now lends the families only their interest.
4. **`Game.consider()` still files any build that failed after planning as a land shortage** (`Game.java:2344–2353`, "which now has exactly one cause: the land went"). `canFundProject()` can also refuse on the leverage line (`BusinessDebtManager.java:727`, `INSOLVENCY_TRIGGER`), so a leverage refusal flags `landBlockedSectors`. The capital refusal is now split out; the leverage one is not changed.
5. **The late-failure premise.** The brief said a failure caused after the close "shows a month late". In the playtest it often did not show at all, because the advisor topped the bank up between months. Only 2 of 0.7.7 held-10 seed 0's 334 top-ups were of a bank at or under zero, so the count barely moves. Fixed anyway (`Game.java:4444`).
6. **An uncapped target locks the city's credit** (`Bank.MAX_BUFFER` javadoc, ~L2848). It is the deviation reported above.
7. **`recapitalisationNeeded()` asked for the minimum would trap a topped-up bank on the line** (`Bank.java:1243`). It asks for the target now; see 1.
8. **A failed bank's ROE printed as 18 digits** in the playtest's era line (`LongPlaytest.bankEra`). It is clamped.
9. **A whole sector is one borrower**, so losses are lumpy, and a real-world capital level cannot absorb them. See 4. This is the structural finding of the batch.
10. **Stage-2 calibration** (seeds 0 and 3, 24-month default rate by leverage on adjusted assets):

    | leverage | seed 0, by count | seed 0, debt-weighted | seed 3, by count | seed 3, debt-weighted |
    |---|---|---|---|---|
    | ≤0.9 | 1.5% | 0.4% | 1.3% | 1.3% |
    | 0.9–1.1 | ~50% | 36% | ~50% | 24% |
    | 1.1–1.3 | 86% | 57% | 90% | 52% |
    | 1.3–1.5 | 93% | 90% | 100% | 100% |

    A loss streak of 6+ months alone gives 15–18%. Leverage is the better predictor, and that is the definition used.

---

## 6. Left undone, and why

- **The failure count is reported, not fixed.** The fix would be an exposure limit or a change to the restructure's loss. Both are outside the brief, and both are design calls for Jerus. Nothing was tuned without a source; `MAX_BUFFER` is the one sourced cap, and it is a deviation.
- The growth-year provision figure (0.02–0.04% against a real 0.3–0.5%) is not calibrated. The model has no small defaults to provide against; see 3.
- The desk's own-share churn is kept (it is 0.7.7's, and it is now honest as capital). Giving the bank's shares a market other than its own desk belongs to the later exchange/rescue batch.
- Seed 7's early price spike (3.63) is not investigated beyond its cause (the young bank returning its surplus into a boom at a near-zero dial).
- `Game.consider()`'s leverage-as-land mislabel is not changed (found on the way, 4).
- `docs/map` is not regenerated, and the project write-up is not written. Both are the docs pass's and the orchestrator's jobs.
- `InfrastructureCheck`'s fragile line, per the brief.
