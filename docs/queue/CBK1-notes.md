# CBK1 - the Bank tab's two banks, and the central bank's own statements and page

Jerus, 2026-11-26 (desk chat): "when you click bank you have two options, the commercial bank, and
the central bank ... give both the banks proper statements, aka ability to not only view summary but
also the statements same way the other sectors, but make sure to really include alot of stuff into
the central bank area". The brief is `docs/queue/CBK1.md`; CBK2 (its QE/QT at the player's pace) and
CBK3 (the window against collateral) build on it and are not here.

## What was built

**The model side, written beside the month and read by nothing in it.**
- `CentralBank`, ITS OWN STATEMENTS: the month as `Line`s (`thisMonth()`, `lastMonth()`), the sheet
  as `Sheet`s (`sheet()`, `yearAgo()`, a twelve-month ring), and what the profit strike at
  `closeMonth()` did with the month (`getStruckProfit()`, `getLossRecovered()`, `getToRemit()`,
  `getLossAdded()`), M0 as the month opened (`getM0AtStart()`), `getBoughtFromBank()`. The strike's
  split is four `+=` beside the existing arithmetic; `fileMonth()` at the top of `startMonth()` copies
  the month and the sheet before they are zeroed. Saved at the end of the bank's own array.
- `DebtManager`: the holdings' maturity ladder at face or market (`centralBankLadder()`,
  `CENTRAL_BANK_LADDER` = 12, 24, 60, 120 months), their market value and its mark against face
  (`centralBankMarketValue()`, `centralBankMarkGain()`), the months left on average. Pure readers of
  `marketValue()` and each paper's held share.
- `CentralBankStatements` (new, pure): the income statement (interest at the window, on the
  treasury's advances, coupons and gains on the paper, less interest on reserves; the profit; what the
  strike did with it: a loss made good, owed the treasury, a loss carried; memoranda: remitted this
  month, the loss carried, the vault's interest), the balance sheet against a year ago (assets; M0 as
  the liabilities; equity; memoranda: the remittance due, the loss carried, the paper at market and its
  mark), money made and destroyed (ten flows that make M0, nine that destroy it, M0 opened and closed),
  the ladder's rungs, and three ratios the pages show.

**The screens.**
- The Bank tab opens on THE CHOICE (`BankScreen.choicePage()`): the commercial bank and the central
  bank side by side, each card a door, each figure the getter its own pages read. The rail's bank icon
  lands there; the inbox's bank notices go to the commercial bank.
- The commercial bank's pages are as they were, behind "Bank › Commercial bank" (its statements were
  already SectorStatements' since 0.7.74). "Bank" is the way back to the choice.
- `ui/CentralBankScreen.java` (new), behind "Bank › Central bank", in BankScreen's frame and pool:
  Overview; Income statement; Balance sheet; Money made and destroyed (the three in StatementView's
  form, as the sectors' and the commercial bank's, each with its ratios beside it); Holdings (the
  ladder at face and market with its gain or loss, its shares of the term paper and of all paper, the
  month's and the lifetime's buying and selling, and what it takes off the curve at 1, 5, 10 and 30
  years); Inflation & credibility (inflation against target and the gap, what people expect, the
  trust, the real rate, what the rule would set); Money (M0 against M2, its parts, the vault and the
  exchange rate, the currency's defence, gold, since founding); History (seven charts of series the
  history already keeps); Policy (Policy's own rule, holdings and ceiling cards, which set at once, and
  the policy rate read-only with a door to Policy, since that dial stages there; a comment marks where
  CBK2's pace dials go).
- Finances › Money's book card and Government's central bank card each have a door to the pages.

## The model does not move

The default playtest with `-Dplaytest.trace`, one JVM each:

main's classes are the branch's with main's `CentralBank.java` and `DebtManager.java` compiled in, the only
two model files the branch changes (`CentralBankStatements` is new and read by nothing in the month).
Twice: on the branch's base, main a21ccb679 (0.9.211), against the branch at df7c1adf8 (its model final,
every later commit `ui/` and harnesses); and after MR1 shipped, main e02e09bc0 (0.9.212) against the
merged branch 2fa69e41b. All eight the same, both times (MR1 does not move the default playtest):

| file | md5, main and CBK1 (both pairs) |
|---|---|
| t-bank.csv | aa2af5f1316c33dde8ba9366884ce152 |
| t-bonds.csv | fa135be296fe76e98685d040a2d05a33 |
| t-borrow.csv | 36b47896b1f126a8b75ef1102132319d |
| t-build.csv | d2e6afe778756cef8b4c8186b0c316df |
| t-house.csv | 96cf152bb7b38f96e2198749252b88b2 |
| t-labour.csv | e9cf9e0947a832addd388e041eaeccaf |
| t-month.csv | 9989337eb618a85564fa3d65307aeabe |
| t-pop.csv | c6f5e3bacc4ae22a60a9be9775855794 |

AP6 (0.9.213, BondMarket, BuildingManager, WorksValue) merged after the second pair; it touches nothing
the branch touches. So no ensemble.

## The checks

- `CentralBankStatementCheck` (new, registered in AllChecks), 70 assertions, exit 0. A 300-month
  player's city with the holdings dial at 30% from month 100 and 5% from 220, two serials sold, its
  treasury emptied at 60 and refilled at 84; every month: the income statement sums to the struck
  profit and its use leaves nothing, the remittance struck is the next month's paid, the loss carried
  moves by what was made good and added; the sheet balances now and a year ago, its total liabilities
  are M0; money made and destroyed is ΔM0 to the cent in played months; the last-month and year-ago
  columns are what was printed then. Every line it names was caused (counted) except flows this city
  never makes: paper bought abroad, dollars bought, gold minted and redeemed, paper the treasury bought
  back. Reading every month leaves two cities equal to the bit to cities never read (section 5). A save
  and load keeps every column; an older save (the array cut at its CBK1 length) reads no last month
  and no year ago and everything else as it was.
- `KeptCheck` 104-106 (new): the choice and the nine central pages kept across months, equal to pages
  built new in signature and pixels, and what reshapes the tab's two sides (163 assertions, 0 FAIL;
  the block takes about 100 s). 55-57, the commercial bank's, unchanged and green.
  The whole of KeptCheck, run straight (without AllChecks' 1,740 s limit, which on this PC it passes at
  about section 74): exit 0, 4,485 OK, no FAIL, Policy's 75-80 (whose rule, holdings and ceiling cards
  now take their redraw) included.
- On the tree merged with MR1 and AP6 (2fa69e41b, then origin/main b2f50a9ef): `--fast` green;
  `CentralBankStatementCheck`, `SectorStatementCheck` and `BankCheck` exit 0; `SaveFileCheck` exit 0
  once restated (below); `CentralBankCheck` exit 0 run straight (through `batch-check.sh` it ran past
  the PC's 1,740 s limit in section 21, its many foundings, with eight other JVMs on the PC: DQ1's
  notes record the same, and alone it passes).
- **SaveFileCheck, restated (check reality):** its FB14 section read the gold's two figures as the
  last two slots of the central bank's array. CBK1 appends its statements' block after them, so the
  section now reads them where FB14 put them, the two slots before
  `CentralBankStatementCheck.BEFORE_CBK1` (13, the revenue ring and main's fourteen - main's array
  length), and an array from before FB14 is still that one cut two slots shorter. The three
  assertions are the same; only where they look moved.

## The calls taken (★, each the recommended way; Jerus may overturn any)

1. ★ **The vault's interest is a memorandum, outside the profit.** The model books none of it in
   `monthProfit()` (ForeignAccounts keeps it in the vault), so the income statement shows it below
   the line rather than invent a remittance the treasury never gets.
2. ★ **Equity is one line**, with the remittance due and the loss carried as memoranda under it: the
   model keeps no capital and surplus apart, and the page does not invent the split.
3. ★ **The paper is carried at face; its market value and the mark are memoranda**, not booked (the
   way a central bank reports its holdings at amortized cost with fair value in the notes), so the
   equity is the model's.
4. ★ **A year ago is the sheet as the eleventh month back opened** - the twelfth back's close, with
   whatever the player did between months. Read against the close instead, 20 of 4,592 year-ago vaults
   differed in the first run, moved by the test player's hand between months.
5. ★ **No SAVE_FORMAT bump.** The new figures are appended to CentralBank's own array, each block
   with its count; an old save loads them as zeros, shows no last month until two months are filed and
   no year ago until twelve - incomplete for a month, never wrong (the old-saves rule).
6. ★ **The month's flows are saved and restored into the bank's own fields**, so a loaded month
   prints what was saved; safe because MoneyAudit is struck only inside `nextMonth()`.
7. ★ **Navigation.** The rail's bank icon resets to the choice; the inbox's "bank" and "preferred"
   notices open the commercial bank; Government's "Bank" doors land on the side last open.
8. ★ **The Policy page draws Policy's own cards** (rule, holdings, ceiling), each given the redraw
   of the page it is on (`Runnable again`; Policy passes `showPolicyMenu`, so Policy draws exactly as
   before). The policy rate is staged on Policy (Apply/Discard), so here it is read-only with a door.
9. ★ Inflation reads "—" until the price index has a year of prices (`PriceIndex.hasRate()`), as
   Policy reads it.

## The slices and the review

Three Sonnet slices at xhigh, each reviewed as a stranger's diff: (1) the choice, the frame and the
four statement pages - kept; CLAUDE.md's count of interface files mended (Stale). (2) the other five
pages and the links - checked that the three embedded Policy cards call nothing of Policy's own
(`showPolicyMenu`, `stage`, `policyArea`, `openLines`: none). (3) KeptCheck 104-106 - the "longer
than its window" premise asserted only on the two pages that are (History, Policy), the scroll kept
checked on all nine; no assertion weakened.

## Left for Jerus

An eye on the pages: Bank, then each card, then each of the nine chips (nothing pending).
