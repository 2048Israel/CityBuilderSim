# DecisionLog.java - 213 lines · 24 methods · 10 constants · model

`ham/citybuildersim/DecisionLog.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

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
> for the standing subsidies, the bank, the fund, the paper, the currency,
> the queue and, since 0.7.107, the zones and the forest law - and that is
> where the line is written, so a decision made from any screen, from the
> playtest's advisor or from a harness is recorded the same way, and one
> that changes nothing (a value set to what it already was) is not
> recorded at all. Ordinary build orders, and the
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

**Used by (32):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [CentralBank](CentralBank.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [DataSave](DataSave.md), [DebtManager](DebtManager.md), [Education](Education.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundCheck](FundCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [HistoryScreen](HistoryScreen.md), [Icons](Icons.md), [LabourMarket](LabourMarket.md), [LongPlaytest](LongPlaytest.md), [OilCheck](OilCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [TaxPolicy](TaxPolicy.md), [TimeChart](TimeChart.md), [TimeSkipReport](TimeSkipReport.md), [TradeScreen](TradeScreen.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 54 | · the kinds a decision comes in: what its flag is coloured by |
| 155 | · the save (DataSave.decisionLog) |
| 174 | · the words a line is written in |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `DecisionLog.TAX` | `"tax"` | A tax rate, an offset or the farmland relief. |
| 60 | `DecisionLog.PROMISE` | `"promise"` | A promise: the wage floor, pensions, EI, schools, health, the fare, a standing subsidy. |
| 63 | `DecisionLog.CENTRAL_BANK` | `"central bank"` | The central bank's dials: the rate, the rule, the target, its holdings, its advances. |
| 66 | `DecisionLog.CURRENCY` | `"currency"` | The money itself: a reform, the vault bought or sold, how land is paid for. |
| 69 | `DecisionLog.BORROWING` | `"borrowing"` | The city's paper: an issue, a buyback, the rollover's setting, a default abroad. |
| 72 | `DecisionLog.BANK` | `"bank"` | The commercial bank: a rescue, the preferred offer, the rescue setting. |
| 75 | `DecisionLog.FUND` | `"fund"` | The city's fund: its dial, and the hand on it. |
| 78 | `DecisionLog.CONSTRUCTION` | `"construction"` | The construction queue (0.7.22): the order, rushes, cancels, restarts, demolitions, buy-outs. |
| 81 | `DecisionLog.RESERVE` | `"reserve"` | The city's strategic reserve (0.7.85): a fill ordered, a release set or stopped. |
| 84 | `DecisionLog.ZONING` | `"zoning"` | Zoning (0.7.109, batch Z2): a zone set, re-sized, re-priced or taken off, and the forest law - its own kind, where Z1 filed it under TAX; a zone's tax rows stay TAX, being the sector's own offsets. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 88 | `public int month` |  |
| 89 | `public String kind` |  |
| 90 | `public String label` |  |
| 106 | `private final List<Entry> entries` |  |
| 109 | `private final IntSupplier clock` | The month a decision is made in: the city's own counter, read when it is recorded. |
| 112 | `private int held` | How many doors are open that set dials without deciding anything; nothing is recorded while any is. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 162 | **type** `public final class DecisionLog` | What the player decided, and when: every change of a policy and every spend at scale, one short line each, at the month it was made (0.7.23). |

### the kinds a decision comes in: what its flag is coloured by (lines 54-154)

| line | len | member | says |
|---:|---:|---|---|
| 87 | 18 | **type** `public static final class Entry` | One decision: when, what kind, and what it was, in a few words. |
| 93 | 1 | `public Entry()` _(in DecisionLog.Entry)_ | Gson needs it. |
| 95 | 5 | `public Entry(int month, String kind, String label)` _(in DecisionLog.Entry)_ |  |
| 101 | 1 | `public int month()` _(in DecisionLog.Entry)_ |  |
| 102 | 1 | `public String kind()` _(in DecisionLog.Entry)_ |  |
| 103 | 1 | `public String label()` _(in DecisionLog.Entry)_ |  |
| 115 | 1 | `public DecisionLog()` | A log on no clock - every decision at month 0. |
| 117 | 3 | `public DecisionLog(IntSupplier clock)` |  |
| 126 | 4 | `public void record(String kind, String label)` | Records one decision at this month. |
| 132 | 1 | `public void hold()` | Stops recording until the matching release(): a city being built or loaded. |
| 135 | 1 | `public void release()` | Ends one hold(). |
| 138 | 1 | `public boolean isHeld()` | Whether anything is being recorded now. |
| 141 | 1 | `public List<Entry> entries()` | Every decision, oldest first. |
| 143 | 1 | `public int size()` |  |
| 146 | 5 | `public List<Entry> inMonth(int month)` | The decisions of one month, in the order they were made. |
| 153 | 1 | `public Entry last()` | The newest decision, or null. |

### the save (DataSave.decisionLog) (lines 155-173)

| line | len | member | says |
|---:|---:|---|---|
| 158 | 5 | `public List<Entry> toState()` | For the save: a copy, so the save never holds the live list. |
| 165 | 8 | `public void restore(List<Entry> saved)` | ...and back; null - a save from before 0.7.23 (format 28 and older) - is an empty log. |

### the words a line is written in (lines 174-213)

| line | len | member | says |
|---:|---:|---|---|
| 177 | 3 | `public static String pct(double share)` | A share as a percentage, as few places as it needs and never more than two: "17%", "17.5%", "0.25%". |
| 182 | 3 | `public static String pct2(double share)` | A rate to two places, as the central bank's dial reads: "0.00%", "3.25%". |
| 187 | 5 | `public static String points(double share)` | Points over or under a base: "+2 pts", "-1.5 pts", "0 pts". |
| 194 | 3 | `public static String times(double scale)` | A multiplier: "x1.2", "x0". |
| 199 | 3 | `public static String money(double thousands)` | Money, in the model's thousands, as the screens print it: "$58.7M". |
| 204 | 3 | `public static boolean moved(double was, double now)` | Whether two dial readings differ by more than a rounding hair. |
| 208 | 5 | `private static String trim(String s)` |  |

