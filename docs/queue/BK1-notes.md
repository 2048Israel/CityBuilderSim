# BK1 - notes: the bank's equity split, and its interest margin ({next}, 2026-10-29)

*[The brief](BK1.md), from [issue #5](https://github.com/2048Israel/CityBuilderSim/issues/5):
Jerus's San Francisco city (slot 8, month 2,852) showed $212.5M of the bank's equity neither paid
in nor retained, and an interest margin of 9,972.71%. Started 2026-10-29 on his word ("yes but just
bk1 hd1 and em1 and stop for the day"); HD1 ran beside it.*

## 1. Where the split broke

**Not the load, and not today's month as it runs.** Slot 8 was copied into a `GameFiles.scratch`
folder (slot 8 itself never written) and played on main: its m2,845 save (0.8.60) for 600 months and
its `.bak` (0.8.52, m687, an earlier San Francisco city: the two histories part at month 3) for
1,200, with a save and reload every 50 months. The residual never moved ($212,453k and $27,634k
throughout), every month's equity movement left nothing unexplained (under 1e-8), and every reload
read the split it was saved with. The two saves' residuals were opened before they were written, and
no record dates them: the history keeps the bank's equity but not its parts, and the Year Book is
written from the history.

**Found on 0.8.52's own playtest.** The San Francisco playtest (`LongPlaytest --earth
37.7692,-122.4172`, 1,200 months) on 0.8.52 ends with the split off by $0.94k; instrumenting that
build's `Bank` (every public method's entry, months 255-262) put the step inside the month's bank
refresh at m260 (+$0.856309k) and m261 (+$0.084900k), the city's paper both times.

**The cause.** A line of the city's paper keeps one pool of unaccreted discount for all its face
(`Debt.discountLeft`), and each holder's share of it is pro rata by face (`Debt.unaccretedOnPaid()`,
`unaccretedOn()`). A reopening (`Debt.reopen()`, and the central bank's add-on,
`reopenForCentralBank()`) adds its tranche's discount at that tranche's price to the pool. While
households, firms, the world or the central bank hold part of the line, the pool's blended discount
per face moves for every holder, so the bank's share moved by more or less than the discount on what
it bought: a change in its liability (the unearned discount) that no cash and no income explained.
At m260 a $38,800k line was reopened by $39,000k at a lower price while households held $5,002k of
it: the bank's face +$34,552.62k, its cash -$34,308.42k, its unearned discount +$243.34k, leaving
+$0.856k; at m259, a fresh line, it left nothing. `Debt`, `DebtManager` and the bond market are
unchanged since 0.8.52: the cause is today's code, and main's own 4,000-month playtest leaves the
split off by $0.000032k.

**The fix** (`Game.restrikeBankDiscount()`, `Bank.restrikeDiscount()`). At the two events that grow
a pool, the settle (`householdsTakeTheirShare()`) and the central bank's add-on
(`rollCentralBankAtIssue()`), the bank's re-blend is measured from the debt manager's own totals -
what its paper gained in face, less what it paid, less what its unearned discount rose by - and
routed straight to retained earnings as a named cause of the equity's movement
(`EquityMovement.discountRestruck()`, "A reopened line's discount, re-struck" on the Bank tab),
saved at the end of the month lines (an older save's month re-struck nothing). It moves no profit,
tax or dividend: equity moves exactly as on main, and now every cause is named. Under a rounding
guard (1e-9 of the figures it is made from) nothing is booked, so a fresh line moves nothing. The
pooled discount stays for now; BK2, queued, gives each holder its own and takes the artifact away.

**Sent back from the gate once** (08:20). The first fix booked the re-blend as a gain on the city's
paper (`PAPER_GAINS`), through income. The full suite's AutoBuildCheck went red ("it borrowed, on the
funding page's bond, at least once": 3 passes on main, 0 here). Its cities re-strike up to -$1,641k
in a month (m61) on about $30,000k of equity, where the bank earns $100-600k a month; main leaves the
very same amounts unexplained in the very same months (m50 +$5.404108k, m61 -$1,640.95k on both), so
the measure was right - but through income they cut the bank's tax and dividends, and over 600 months
the decades city ended with 18,988 people against main's 21,729. An artifact of the pooled discount
should not move the city: the orchestrator took the retained route (2026-10-29, on BK1's
recommendation), replacing ★1's income routing.

**A save already out of step** (CLAUDE.md, old saves): it loads as it did. The two counters are
restored as saved and equity is what the balance sheet says, so the residual stays and the red card
("Its two parts do not add up to its equity") keeps saying it; nothing converts it. It does not grow
after the load. No format bump: no save loads differently.

**The harness** (`HoldersCheck` 12). A twenty-year bond sold and settled with the households and
the bank each holding part, then the policy rate up four points and the same line reopened (its
discount per face 0.0124 to 0.0229), settled the next month - in the city and in a twin built the
same way that books no re-strike, as main does (`Game.restrikeOffForTest`). The fresh settle
re-strikes nothing; the reopened one re-strikes -$1.311467k (rates rose) as the movement's own cause,
outside PAPER_GAINS. Over that month and three more, the city's movement residual and split are
0.000000000 every month, and its net income, tax, dividends, gains on paper, the treasury's cash and
the bank's equity are the twin's bit for bit; the twin's split opens by exactly $1.311467k and stays
open, as San Francisco's did. The audit closes every month (114).
The central bank's add-on goes through the same helper; no harness drives an add-on into a reopened
line (`CentralBankCheck` 19 reaches it with fresh pieces only), so it is pending as BK1-C.

## 2. The interest margin, on what earns it interest

`Bank.netInterestMargin()` and `netInterestMarginOverYear()` divided net interest by the book
(`getBook()`: the loans, the city's paper, the carry, the bonds), while net interest also holds what
the reserves earn at the central bank's policy rate (`Line.FROM_RESERVES`); San Francisco's reserves
were 700 times its book. Both now divide by `Bank.earningAssets()`, the gross book plus the reserves
(the sheet's RESERVES, BUSINESS_LOANS, INTERIM, MORTGAGES, FAMILIES, CARRY, CITY_PAPER and BONDS),
the year's average taken from the year of balance sheets the bank already saves (since 0.7.13), so a
loaded save's average is right from its first month and no field was added. The five places
`BankScreen` reads it say "on what earns it interest" and the card's note names the three.
`BankCheck` (13) causes the issue's case - the played bank given capital 200 times its book, its
reserves then 195 times its book: the old reading 585.56%, the new 2.99%, under the best rate any of
its assets earned (5.43%) - and asserts the margin against the model's own lines, month and year.

## 3. Looked at, not fixed

- **The money that piles up** (treasury $38.5T, deposits $118.8T, GDP $8.4T a year, 22% out of
  work): [HD1](HD1.md) has the figures and the causes.
- **The island label on the land office map** (`MapView.islandChips()`): a caption of some 70
  characters in amber mono, its only ground a 3-pixel dark stroke round the letters, centred over the
  landmass's own amber hatch and tint - so over busy ground the letters run into the hatch. Pending
  as BK1-L.

## 4. The calls (each took its recommended way, BP1)

- **★1 The re-blend routed to retained, a named cause, not through income** - the orchestrator's call
  (2026-10-29), on BK1's recommendation, replacing the income routing the gate sent back: it is an
  artifact of the pooled discount, so it moves no profit, tax or dividend. Each holder's own discount
  on every line, which removes it, is BK2, queued.
- **★2 Reserves count as earning assets whatever the policy rate.** Taken: the margin does not jump
  when the rate touches zero; they earn the policy rate, zero included. The other way: only while the
  rate is above zero.
- **★3 The island label: a plate behind it.** Recommended for BK1-L: a dark rounded plate the width
  of the words under the chip, and two lines at the near view (name and area; why not linked). The
  other way: shorten it to the name and area, the reason on hover.

## 5. The checks, the trace

- **The full suite** (`./batch-check.sh`, both data files named), after the gate's send-back:
  exit 0, 152 harnesses green in 1,042 s (8 timing figures SOFT, run beside other batches), Stale
  no firm findings. AutoBuildCheck passes untouched.
- **The trace**: the default playtest on main (916d4ac7, built apart) and on this branch, traced
  (`-Dplaytest.trace`), before the send-back and again on the final model: all eight CSVs
  byte-identical, the log identical but for its run-time lines (74 s; 92 s, 109 s). The default run's
  split is off by $0.000032k at most on both, rounding on $480B of equity, under the guard. **No
  ensemble**: the trace did not move.
- The slices went to Sonnet 5.5 at xhigh (A: the margin; B: the re-strike; C: the re-strike to
  retained, after the gate), each reviewed here. `--pick` asks for a money review of `Bank.java`
  and `ui/BankScreen.java`.
- Not a process batch: no scripts, checks' machinery or rules changed.
- HD1's files untouched (`OutwardInvestment`, `SectorBooks`, `buildings.json`, `HistorySave`,
  `DataSave`).
