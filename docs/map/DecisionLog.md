# DecisionLog.java - 206 lines · 24 methods · 8 constants · model

`ham/citybuildersim/DecisionLog.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

> What the player decided, and when: every change of a policy and every
> spend at scale, one short line each, at the month it was made (0.7.23).
> 
> WHY THIS EXISTS
> 
> Jerus, on the charts: "event markers on the timeline, including the
> player's own decisions ('taxes to 17%', 'central bank cut to 0%', 'bank
> rescued')". Nothing in the game kept them. A tax rate is saved as what it
> IS, not as when it moved, and the history keeps the rate a month at a
> time - so the month a player cut it could be guessed from a step in a
> line, and the reason a line stepped could not be told from the line. A
> decision is a flow, and a flow cannot be reconstructed from the state a
> month ended in, so it is recorded as it happens and saved.
> 
> RECORDED WHERE IT IS APPLIED, NEVER IN THE INTERFACE. Each policy has one
> place that applies it - TaxPolicy's setters, LabourMarket's floor, the
> central bank's dials, Education's share of tuition, and Game's methods
> for the standing subsidies, the bank, the fund, the paper, the currency
> and the queue - and that is where the line is written, so a decision made
> from any screen, from the playtest's advisor or from a harness is
> recorded the same way, and one that changes nothing (a value set to what
> it already was) is not recorded at all. Ordinary build orders, and the
> plots the land office buys, are not decisions: a city places them every
> month, and the big ones the treasury cannot pay for arrive here as the
> borrowing that paid for them.
> 
> HELD WHILE A CITY IS BUILT. A new city, a load and the constructor set
> their dials through the same setters a player does; none of that is a
> decision, so the log is held from buildWorld() until the door into the
> city is through, and a load holds it for itself (Game.loadGame()). It is
> held too where the city acts by itself through a player's own method: the
> rollover's issues (Game.issueForRollover()), a foreign default the
> treasury could not avoid (checkForeignSolvency()), and the reserves a
> dollar issue buys as part of itself (handleForeignLogic(), whose own line
> says "held as reserves").
> 
> NOTHING HERE PRINTS. Every println is the game's log and the playtest's
> report reads the log; a decision is in this list and in the save, on
> the charts (ChartModel.flags()) and in Policy's RECENT DECISIONS and City
> History's lists, and nowhere else.

**Uses:** [Formats](Formats.md) (1)

**Used by (27):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [CentralBank](CentralBank.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [DataSave](DataSave.md), [DebtManager](DebtManager.md), [Education](Education.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundCheck](FundCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [HistoryScreen](HistoryScreen.md), [Icons](Icons.md), [LabourMarket](LabourMarket.md), [LongPlaytest](LongPlaytest.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [TaxPolicy](TaxPolicy.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 53 | · the kinds a decision comes in: what its flag is coloured by |
| 148 | · the save (DataSave.decisionLog) |
| 167 | · the words a line is written in |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `DecisionLog.TAX` | `"tax"` | A tax rate, an offset or the farmland relief. |
| 59 | `DecisionLog.PROMISE` | `"promise"` | A promise: the wage floor, pensions, EI, schools, health, the fare, a standing subsidy. |
| 62 | `DecisionLog.CENTRAL_BANK` | `"central bank"` | The central bank's dials: the rate, the rule, the target, its holdings, its advances. |
| 65 | `DecisionLog.CURRENCY` | `"currency"` | The money itself: a reform, the vault bought or sold, how land is paid for. |
| 68 | `DecisionLog.BORROWING` | `"borrowing"` | The city's paper: an issue, a buyback, the rollover's setting, a default abroad. |
| 71 | `DecisionLog.BANK` | `"bank"` | The commercial bank: a rescue, the preferred offer, the rescue setting. |
| 74 | `DecisionLog.FUND` | `"fund"` | The city's fund: its dial, and the hand on it. |
| 77 | `DecisionLog.CONSTRUCTION` | `"construction"` | The construction queue (0.7.22): the order, rushes, cancels, restarts, demolitions, buy-outs. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `public int month` |  |
| 82 | `public String kind` |  |
| 83 | `public String label` |  |
| 99 | `private final List<Entry> entries` |  |
| 102 | `private final IntSupplier clock` | The month a decision is made in: the city's own counter, read when it is recorded. |
| 105 | `private int held` | How many doors are open that set dials without deciding anything; nothing is recorded while any is. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 156 | **type** `public final class DecisionLog` | What the player decided, and when: every change of a policy and every spend at scale, one short line each, at the month it was made (0.7.23). |

### the kinds a decision comes in: what its flag is coloured by (lines 53-147)

| line | len | member | says |
|---:|---:|---|---|
| 80 | 18 | **type** `public static final class Entry` | One decision: when, what kind, and what it was, in a few words. |
| 86 | 1 | `public Entry()` _(in DecisionLog.Entry)_ | Gson needs it. |
| 88 | 5 | `public Entry(int month, String kind, String label)` _(in DecisionLog.Entry)_ |  |
| 94 | 1 | `public int month()` _(in DecisionLog.Entry)_ |  |
| 95 | 1 | `public String kind()` _(in DecisionLog.Entry)_ |  |
| 96 | 1 | `public String label()` _(in DecisionLog.Entry)_ |  |
| 108 | 1 | `public DecisionLog()` | A log on no clock - every decision at month 0. |
| 110 | 3 | `public DecisionLog(IntSupplier clock)` |  |
| 119 | 4 | `public void record(String kind, String label)` | Records one decision at this month. |
| 125 | 1 | `public void hold()` | Stops recording until the matching release(): a city being built or loaded. |
| 128 | 1 | `public void release()` | Ends one hold(). |
| 131 | 1 | `public boolean isHeld()` | Whether anything is being recorded now. |
| 134 | 1 | `public List<Entry> entries()` | Every decision, oldest first. |
| 136 | 1 | `public int size()` |  |
| 139 | 5 | `public List<Entry> inMonth(int month)` | The decisions of one month, in the order they were made. |
| 146 | 1 | `public Entry last()` | The newest decision, or null. |

### the save (DataSave.decisionLog) (lines 148-166)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 5 | `public List<Entry> toState()` | For the save: a copy, so the save never holds the live list. |
| 158 | 8 | `public void restore(List<Entry> saved)` | ...and back; null - a save from before 0.7.23 (format 28 and older) - is an empty log. |

### the words a line is written in (lines 167-206)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 3 | `public static String pct(double share)` | A share as a percentage, as few places as it needs and never more than two: "17%", "17.5%", "0.25%". |
| 175 | 3 | `public static String pct2(double share)` | A rate to two places, as the central bank's dial reads: "0.00%", "3.25%". |
| 180 | 5 | `public static String points(double share)` | Points over or under a base: "+2 pts", "-1.5 pts", "0 pts". |
| 187 | 3 | `public static String times(double scale)` | A multiplier: "x1.2", "x0". |
| 192 | 3 | `public static String money(double thousands)` | Money, in the model's thousands, as the screens print it: "$58.7M". |
| 197 | 3 | `public static boolean moved(double was, double now)` | Whether two dial readings differ by more than a rounding hair. |
| 201 | 5 | `private static String trim(String s)` |  |

