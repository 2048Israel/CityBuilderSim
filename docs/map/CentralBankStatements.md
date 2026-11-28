# CentralBankStatements.java - 287 lines · 14 methods · 6 constants · model

`ham/citybuildersim/CentralBankStatements.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The central bank's month as formal statements (CBK1): its income statement down to the profit and what the strike did
> with it, its balance sheet against a year ago, and the money it made and destroyed from M0 as the month opened to M0 as
> it closed - each a SectorStatements.Table the Bank tab's central bank pages draw through StatementView, as the sectors'
> and the commercial bank's are drawn - and its holdings by how long they have left, at face and at market.
> 
> <p>WHY. Jerus, 2026-11-26: "when you click bank you have two options, the commercial bank, and the central bank ...
> give both the banks proper statements, aka ability to not only view summary but also the statements same way the other
> sectors, but make sure to really include alot of stuff into the central bank area". The central bank had no page: a
> card on Finances > Money listed what it holds and the month's money made and destroyed, a smaller one on Government
> its advances, each figure a getter the card read once, with no last month, no year ago and no statement that added up.
> 
> <p>PRESENTATION ONLY, SectorStatements' rule. Every bottom line is the bank's own figure - the profit closeMonth()
> struck, the sheet's totals, getIssued() and getRetired(), M0 - and the subtotals are this class's running sums of its
> own lines, each with a NOT ACCOUNTED FOR line against the figure it must reach, shown from a dollar: a line missing or
> counted twice shows there, and CentralBankStatementCheck holds every one of them under a cent. Pure: it reads
> CentralBank (its ITS OWN STATEMENTS) and DebtManager's book of it, and changes nothing; nothing in the model reads it.

**Uses:** [CentralBank](CentralBank.md) (95), [SectorStatements](SectorStatements.md) (11), [DebtManager](DebtManager.md) (3)

**Used by (2):** [CentralBankScreen](CentralBankScreen.md), [CentralBankStatementCheck](CentralBankStatementCheck.md)

## Sections

| line | section |
|---:|---|
| 31 | THE INCOME STATEMENT |
| 91 | THE BALANCE SHEET |
| 159 | THE MONEY MADE AND DESTROYED |
| 238 | THE HOLDINGS' LADDER |
| 271 | FIGURES THE PAGES SHOW BESIDE THE STATEMENTS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 29 | `CentralBankStatements.CENT` | `1e-5` | A cent, in the model's thousands: what a statement's NOT ACCOUNTED FOR is held under (CentralBankStatementCheck). |
| 34 | `CentralBankStatements.I_INCOME_HEAD` | `"cb.incomeHead", I_WINDOW = "cb.window", I_ADVANCES = "cb.advances", I_COUPON...` | The income statement's row ids, which the screen and CentralBankStatementCheck look a row up by. |
| 94 | `CentralBankStatements.S_ASSETS_HEAD` | `"cbs.assetsHead", S_WINDOW = "cbs.window", S_TREASURY = "cbs.treasury", S_PAP...` | The balance sheet's row ids. |
| 162 | `CentralBankStatements.M_MADE_HEAD` | `"cbm.madeHead", M_MADE_REST = "cbm.madeRest", M_MADE = "cbm.made", M_DESTROYE...` | The money statement's row ids. |
| 167 | `CentralBankStatements.MADE` | `{ { "Lent to the commercial bank at the window", CentralBank.Line.ADVANCED_TO...` | The money made, by flow: {label, line}, in the order the statement prints them. |
| 180 | `CentralBankStatements.DESTROYED` | `{ { "Repaid by the commercial bank at the window", CentralBank.Line.REPAID_BY...` | ...and destroyed. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 264 | **type** `public final class CentralBankStatements` | The central bank's month as formal statements (CBK1): its income statement down to the profit and what the strike did with it, its balance sheet against a year ago, and the money it made and destroyed from M0 as the m... |
| 26 | 1 | `private CentralBankStatements()` |  |

### THE INCOME STATEMENT (lines 31-90)

| line | len | member | says |
|---:|---:|---|---|
| 51 | 34 | `public static SectorStatements.Table income(CentralBank cb, double vaultInterest)` | Its statement of profit or loss, this month against last: interest earned at the window, on the treasury's advances and on the paper it holds, and the gains against face on that paper; less the interest it pays on res... |
| 87 | 3 | `private static void line(SectorStatements.Builder b, CentralBank cb, String id, String label, CentralBank.Line line, int sign)` | A line of its month, signed as it adds: this month and last. |

### THE BALANCE SHEET (lines 91-158)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 37 | `public static SectorStatements.Table sheet(CentralBank cb, double atMarket, double mark)` | Its statement of financial position, this month against a year ago: what it has lent and holds, what it owes - M0, every dollar it has made and not taken back - and its equity, what it owns past that. |
| 149 | 3 | `private static void sheetLine(SectorStatements.Builder b, CentralBank cb, String id, String label, CentralBank.Sheet line)` |  |
| 153 | 5 | `private static double sum(CentralBank cb, CentralBank.Sheet[] lines, boolean yearAgo)` |  |

### THE MONEY MADE AND DESTROYED (lines 159-237)

| line | len | member | says |
|---:|---:|---|---|
| 192 | 1 | `public static String moneyId(CentralBank.Line line)` | A flow's row id on the money statement. |
| 200 | 37 | `public static SectorStatements.Table money(CentralBank cb)` | The money it made and destroyed this month, against last: every flow that made money (MoneyAudit's ISSUED), every flow that destroyed it (RETIRED), each group against getIssued() and getRetired(); the difference, M0 M... |

### THE HOLDINGS' LADDER (lines 238-270)

| line | len | member | says |
|---:|---:|---|---|
| 241 | 1 | **type** `public record Rung(String label, double face, double market, double gain)` | One rung of the ladder: its words, what it holds there at face and at market, and the gain or loss between. |
| 244 | 12 | `public static List<Rung> ladder(DebtManager market)` | Its holdings by months left (DebtManager.CENTRAL_BANK_LADDER), each at face and at market; then the whole book as a last rung, "Every piece". |
| 258 | 6 | `static String rungWords(int k)` | A rung's words: "a year or less", "1 to 2 years", "over 10 years". |
| 265 | 5 | `private static String years(int months)` |  |

### FIGURES THE PAGES SHOW BESIDE THE STATEMENTS (lines 271-287)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 3 | `public static double m2OverM0(double m2, CentralBank cb)` | What the public holds for each dollar the central bank has made: M2 over M0; NaN with nothing made. |
| 279 | 3 | `public static double inflationGap(double inflation, DebtManager market)` | Inflation against the target, in points: what the rule leans against (positive over it). |
| 284 | 3 | `public static double ceilingUsed(CentralBank cb)` | The share of the treasury's ceiling it has drawn: its advances over CentralBank.ceiling(); NaN with no ceiling. |

