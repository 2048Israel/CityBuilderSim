# FundLedger.java - 766 lines · 92 methods · 7 constants · model

`ham/citybuildersim/FundLedger.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> The city's fund's cost basis: what each holding cost, by the average-cost method, what its sales and maturities realized, what it has paid in income, and every trade, income, transfer and event the fund saw (0.7.39).
> 
> WHY. Until 0.7.39 the fund knew what it was worth and, to the cent, every
> flow that ever went in or out of it (TreasuryFund's lifetime counters), but
> not what any one holding had cost - only the rescue book's cost was kept
> (TreasuryFund.getRescueCost()). Nothing recorded what one of the player's
> orders filled, at what price, or what lapsed: the History flag said "Fund
> to buy D$4.0M of Construction shares" of an order that bought nothing.
> Jerus, 2026-10-02: "the city fund should show pnl and acb and all that".
> The project's spec-fund-0739.md, section 3.
> 
> THE RULES (Canada's adjusted cost base, by the average-cost method):
> 
>   A BUY adds its full cost - the units times the price paid; the game
>   charges no fee - to the lot's ACB. A SALE takes out the ACB times the
>   units sold over the units held before it, and realizes the proceeds less
>   that. The units are always read from the model - the register's city
>   shares, a bond's city face - never kept here, so a split needs nothing:
>   the ACB stands and the average a unit falls by the factor.
> 
>   LOTS. One for each company's market book (the rule's buys and the hand's
>   pooled: they are one register holding, and Canada's identical-property
>   rule averages them), one for the rescue book (the bank's), one a bond.
>   The hand's sale takes the market lot first and then the rescue lot, as
>   the register does (Equity.sellCityByHand()).
> 
>   INCOME apart: dividends and coupons to the lot's income, never its ACB.
> 
>   A MATURITY realizes the face less the ACB; A WRITE-DOWN (a default or a
>   restructure, BondMarket.writeDown()) takes the ACB out in the share the
>   face lost, as a realized loss with no proceeds; a bond whose face has gone
>   to dust realizes the rest of its ACB.
> 
>   THE RESCUE. The fund's market-book bank shares pass to the city for
>   nothing (Equity.takeAllForCity()): their ACB is realized as a loss. The
>   rescue lot's ACB is TreasuryFund.getRescueCost() - what the city paid for
>   the rescue book's shares it still holds - one source, read, not kept here.
> 
>   The preferred is at par and its warrants cost nothing, so their cost is
>   the bank's own record (Bank.preferredOutstanding()) and their gains the
>   fund's counters; they are rows here, not lots (FundView reads them).
> 
> BOOKKEEPING ONLY. Every hook writes this object and nothing else; nothing
> the month reads reads it; prices, cash and units are read, never set. So
> the default playtest's traces cannot move (the spec's 3.6).
> 
> AN OLDER SAVE has no ledger: Game's load path seeds one once everything is
> back (Game.seedFundLedger()) - each market lot and each bond at its market
> value that month, flagged as such, the rescue lot from the counters, which
> were always exact - and one TRACKING row says so.
> 
> Saved inside TreasuryFund.State as `ledger`: plain lists Gson writes as they
> stand, no new save key, SAVE_FORMAT unchanged.

**Uses:** [CorporateBond](CorporateBond.md) (7), [Equity](Equity.md) (4), [TreasuryFund](TreasuryFund.md) (3), [OrderBook](OrderBook.md) (1), [Game](Game.md) (1), [Exchange](Exchange.md) (1)

**Used by (10):** [BondMarket](BondMarket.md), [Exchange](Exchange.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [TreasuryFund](TreasuryFund.md)

## Sections

| line | section |
|---:|---|
| 65 | the dials |
| 79 | the kinds |
| 97 | a lot |
| 166 | a row |
| 225 | the state |
| 327 | the rows |
| 396 | the hooks |
| 684 | an older save |
| 737 | a reform |
| 757 | totals, for the checks |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 68 | `FundLedger.ACTIVITY_ROWS` | `1000` | The newest activity rows kept: the played research cities made 0-5 a month, and 37 in the busiest month after a pay-in, so this is some fifteen years of a quiet fund and two of a busy one. |
| 71 | `FundLedger.DUST` | `OrderBook.DUST` | Anything this small, in units or money, is the arithmetic's dust: a lot holding less is closed, a fill this small is no trade (OrderBook.DUST). |
| 74 | `FundLedger.WRITE_DOWN_ROW` | `1e-3` | A write-down that takes less than this share of a bond's face is booked on its lot but given no row: a restructure keeping all but a sliver of the face (the 600-month research city's year made 24 such, each under D$1;... |
| 77 | `FundLedger.BARGAIN` | `.05` | A buy that paid at least this share less than what it bought was worth at the step - a share's fair value, a bond's value - was a bargain, and its lot and its row say so (the spec's 5: the 600-month research city's ru... |
| 82 | `FundLedger.BUY` | `"BUY", SELL = "SELL", LAPSED = "LAPSED", DIVIDEND = "DIVIDEND", COUPON = "COU...` | What an activity row records. |
| 88 | `FundLedger.SHARES` | `"SHARES", RESCUE_BOOK = "RESCUE", BOND = "BOND"` | What a lot is: a company's market book, a rescue book, a bond. |
| 95 | `FundLedger.PREFERRED_KEY` | `"P:Bank", WARRANTS_KEY = "W:Bank"` | The preferred and the warrants, which are rows and not lots. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 112 | `String key, kind, name, issuer` |  |
| 113 | `int bondId` |  |
| 114 | `double coupon` |  |
| 115 | `int maturity` |  |
| 116 | `double acb, realized, proceeds, acbOut, bought, income, incomeBefore` |  |
| 117 | `int since` |  |
| 118 | `boolean seeded` |  |
| 119 | `int closedMonth` |  |
| 120 | `double boughtValue, lastIncome` |  |
| 121 | `int lastIncomeMonth` |  |
| 178 | `int month` |  |
| 179 | `String kind, key` |  |
| 180 | `boolean hand` |  |
| 181 | `double units, money, realized` |  |
| 182 | `String note` |  |
| 183 | `boolean buy, open` |  |
| 184 | `double asked, limit, lapsed` |  |
| 185 | `double value` |  |
| 227 | `private List<Lot> lots` |  |
| 228 | `private List<Activity> activity` |  |
| 230 | `private int trackingSince` | The month the ledger began: -1 for a city founded with it, the load month for an older save it was seeded on. |
| 232 | `private int dropped` | Rows dropped past ACTIVITY_ROWS, over the ledger's life. |
| 235 | `private transient Map<String, Lot> index` | Lots by key, rebuilt on demand (never saved). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 704 | **type** `public final class FundLedger` | The city's fund's cost basis: what each holding cost, by the average-cost method, what its sales and maturities realized, what it has paid in income, and every trade, income, transfer and event the fund saw (0.7.39). |

### the dials (lines 65-78)

### the kinds (lines 79-96)

| line | len | member | says |
|---:|---:|---|---|
| 91 | 1 | `public static String shareKey(String company)` | A lot's key: by the register's name, never its index. |
| 92 | 1 | `public static String rescueKey(String company)` |  |
| 93 | 1 | `public static String bondKey(int id)` |  |

### a lot (lines 97-165)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 54 | **type** `public static final class Lot` | One holding's cost: its key, what it is (and for a bond its issuer, coupon and maturity, kept so a closed lot still says what it was); its ACB now; over its life the realized gain, the proceeds and the ACB taken out, ... |
| 123 | 1 | `Lot()` _(in FundLedger.Lot)_ |  |
| 125 | 9 | `Lot copy()` _(in FundLedger.Lot)_ |  |
| 135 | 1 | `public String key()` _(in FundLedger.Lot)_ |  |
| 136 | 1 | `public String kind()` _(in FundLedger.Lot)_ |  |
| 138 | 1 | `public String name()` _(in FundLedger.Lot)_ | The company's name, or "#142 Automotive" for a bond. |
| 139 | 1 | `public String issuer()` _(in FundLedger.Lot)_ |  |
| 140 | 1 | `public int bondId()` _(in FundLedger.Lot)_ |  |
| 141 | 1 | `public double coupon()` _(in FundLedger.Lot)_ |  |
| 142 | 1 | `public int maturity()` _(in FundLedger.Lot)_ |  |
| 144 | 1 | `public double acb()` _(in FundLedger.Lot)_ | What the units held now cost; the rescue lot's is TreasuryFund.getRescueCost(), not this. |
| 145 | 1 | `public double realized()` _(in FundLedger.Lot)_ |  |
| 146 | 1 | `public double proceeds()` _(in FundLedger.Lot)_ |  |
| 147 | 1 | `public double acbOut()` _(in FundLedger.Lot)_ |  |
| 148 | 1 | `public double bought()` _(in FundLedger.Lot)_ |  |
| 149 | 1 | `public double income()` _(in FundLedger.Lot)_ |  |
| 150 | 1 | `public double incomeBefore()` _(in FundLedger.Lot)_ |  |
| 151 | 1 | `public int since()` _(in FundLedger.Lot)_ |  |
| 152 | 1 | `public boolean seeded()` _(in FundLedger.Lot)_ |  |
| 153 | 1 | `public int closedMonth()` _(in FundLedger.Lot)_ |  |
| 154 | 1 | `public boolean isClosed()` _(in FundLedger.Lot)_ |  |
| 155 | 1 | `public boolean isBond()` _(in FundLedger.Lot)_ |  |
| 156 | 1 | `public boolean isRescue()` _(in FundLedger.Lot)_ |  |
| 158 | 1 | `public double boughtValue()` _(in FundLedger.Lot)_ | What its buys were worth at the step's value when it made them (a share's fair value, a bond's value), against `bought`, what they cost. |
| 160 | 1 | `public double underValue()` _(in FundLedger.Lot)_ | How far under that value it bought, as a share of it: 0.26 for 74 paid for 100 worth; NaN with nothing bought since tracking began. |
| 162 | 1 | `public double lastIncome()` _(in FundLedger.Lot)_ | The income it was paid in the last month it was paid any, and that month (-1: none yet). |
| 163 | 1 | `public int lastIncomeMonth()` _(in FundLedger.Lot)_ |  |

### a row (lines 166-224)

| line | len | member | says |
|---:|---:|---|---|
| 177 | 47 | **type** `public static final class Activity` | One line of what the fund did or had done to it: the month, the kind, the lot's key (null for the fund as a whole), whether it was the player's hand, the units (shares, face, or a count for an aggregate row), the mone... |
| 187 | 1 | `Activity()` _(in FundLedger.Activity)_ |  |
| 189 | 7 | `Activity copy()` _(in FundLedger.Activity)_ |  |
| 197 | 1 | `public int month()` _(in FundLedger.Activity)_ |  |
| 198 | 1 | `public String kind()` _(in FundLedger.Activity)_ |  |
| 200 | 1 | `public String key()` _(in FundLedger.Activity)_ | The lot's key, or null for the fund as a whole. |
| 202 | 1 | `public boolean hand()` _(in FundLedger.Activity)_ | True for the player's hand; false for the rule and for the model. |
| 204 | 1 | `public double units()` _(in FundLedger.Activity)_ | Shares or face traded; for an aggregate row (dividends, coupons) how many lots it covers. |
| 205 | 1 | `public double money()` _(in FundLedger.Activity)_ |  |
| 206 | 1 | `public double realized()` _(in FundLedger.Activity)_ |  |
| 207 | 1 | `public String note()` _(in FundLedger.Activity)_ |  |
| 209 | 1 | `public boolean buy()` _(in FundLedger.Activity)_ | A trade's side. |
| 211 | 1 | `public boolean open()` _(in FundLedger.Activity)_ | A hand order still resting on its book. |
| 213 | 1 | `public double asked()` _(in FundLedger.Activity)_ | A hand order's units asked for and its price a unit. |
| 214 | 1 | `public double limit()` _(in FundLedger.Activity)_ |  |
| 216 | 1 | `public double lapsed()` _(in FundLedger.Activity)_ | ...and what of it lapsed, unfilled, at the step that withdrew it. |
| 218 | 1 | `public double average()` _(in FundLedger.Activity)_ | The average price a unit it filled at, or NaN for nothing filled. |
| 220 | 1 | `public double value()` _(in FundLedger.Activity)_ | A buy's units at the step's value when it filled (money); 0 where it was not known. |
| 222 | 1 | `public double underValue()` _(in FundLedger.Activity)_ | How far under that value it paid, as a share of it, or NaN. |

### the state (lines 225-326)

| line | len | member | says |
|---:|---:|---|---|
| 237 | 1 | `public FundLedger()` |  |
| 240 | 8 | `public FundLedger copy()` | A deep copy: what a save writes and a load takes, so the two never share a row. |
| 250 | 7 | `public void reset()` | Empty, for a new city or a fund reset: tracking from the founding. |
| 258 | 9 | `private Map<String, Lot> index()` |  |
| 269 | 1 | `public Lot lot(String key)` | A lot by its key, or null. |
| 272 | 1 | `public List<Lot> getLots()` | Every lot, held and closed, in the order each was opened. |
| 275 | 1 | `public List<Activity> getActivity()` | Every row kept, oldest first. |
| 278 | 1 | `public int getTrackingSince()` | The month the ledger began: -1 for a city founded with it. |
| 281 | 1 | `public int getDropped()` | Rows dropped past ACTIVITY_ROWS. |
| 284 | 14 | `private Lot shareLot(String company, boolean rescue, int month)` | A share lot, opened if new. |
| 300 | 18 | `private Lot bondLot(CorporateBond b, int month)` | A bond's lot, opened if new, carrying what the bond is. |
| 320 | 6 | `private static void reopen(Lot l, int month)` | A lot bought into again after it closed is held again; its record stands. |

### the rows (lines 327-395)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 11 | `private Activity add(int month, String kind, String key, boolean hand)` |  |
| 342 | 8 | `private void trim()` | Drops the oldest rows past ACTIVITY_ROWS, never one still open. |
| 352 | 3 | `private Activity monthRow(int month, String kind, String key, boolean buy)` | This month's row of a kind for a lot (the rule's or the model's), made if there is none: the rows aggregate a month. |
| 357 | 11 | `private Activity monthRow(int month, String kind, String key, boolean buy, boolean hand)` | ...or the hand's, for a fill with no order of its own on record. |
| 375 | 9 | `private Activity openHand(String key, boolean buy)` | The hand's open order for this lot and side that a fill belongs to: the oldest that has not filled what it asked for - the book matches the hand's orders on one name in the order they were posted - or, past them all, ... |
| 386 | 9 | `private Activity fillRow(int month, String key, boolean hand, boolean buy)` | Where a fill is written: the hand's open order for the lot and side if it is the hand's, otherwise this month's row. |

### the hooks (lines 396-683)

| line | len | member | says |
|---:|---:|---|---|
| 403 | 12 | `void boughtShare(String company, boolean hand, double q, double price, double value, int month)` | The fund bought shares of a company's market book, by its rule or the player's hand: `q` at `price` (Exchange's settle), when the company's fair value a share was `value`. |
| 417 | 5 | `private static void worth(Lot l, Activity a, double q, double value)` | A buy's units at the step's value, on its lot and its row; nothing where the value is not known. |
| 424 | 5 | `private static void paid(Lot l, double amount, int month)` | Income to a lot: its total, and the last month's that paid any. |
| 437 | 32 | `void soldShare(String company, boolean hand, double q, double price, double fromRescue, double marketBefore, double rescueBefor...` | ...sold `q` at `price`: `fromRescue` of them out of the rescue book (the hand's sale takes the market book first), the market and rescue books' units before the sale, and the rescue book's cost before it (TreasuryFund... |
| 471 | 12 | `void boughtBond(CorporateBond b, boolean hand, double q, double price, double value, int month)` | The fund bought `q` of a bond's face at `price` a unit of face, when the bond's value a unit was `value`. |
| 485 | 16 | `void soldBond(CorporateBond b, boolean hand, double q, double price, double faceBefore, int month)` | ...sold `q` of its face at `price`, out of `faceBefore`. |
| 503 | 8 | `void coupon(int bondId, double amount, int month)` | A bond's coupon to the fund, by the bond's id - it may have been repaid since the coupon was struck: income, never cost. |
| 513 | 18 | `void matured(CorporateBond b, double face, int month)` | A bond repaid: the face against what it cost. |
| 533 | 16 | `void writtenDown(CorporateBond b, double before, double after, int month)` | A default or a restructure took the face from `before` to `after`: that share of the ACB out, a realized loss, no proceeds. |
| 551 | 13 | `void gone(CorporateBond b, int month)` | A bond whose face a backstop wrote to dust, taken off the market: what is left of its cost, a realized loss. |
| 566 | 9 | `void dividend(String company, double amount, double rescueShare, int month)` | A dividend: income on each book's lot, by the share each holds; one row a month for them all. |
| 577 | 17 | `void split(String company, double k, int month)` | A company's shares split (k > 1) or consolidated: the ACB stands, the average a share moves by 1/k. |
| 601 | 10 | `double rescueTakes(String company, int month)` | THE RESCUE: before the register takes every share, the fund's market-book shares of the company pass for nothing - their ACB realized as a loss. |
| 612 | 9 | `void rescued(String company, TreasuryFund.Resolution r, double marketAcbLost, int month)` |  |
| 623 | 7 | `void preferred(String what, double money, double realized, int month)` | The preferred: bought (par), a dividend, redeemed at par, or cancelled at a failure (`par` negative: a realized loss). |
| 632 | 7 | `void warrants(String what, double money, double shares, int month)` | The warrants: bought back (`money`, all of it realized, they cost nothing) or exercised into `shares` of the rescue book. |
| 641 | 5 | `void flow(String kind, double money, boolean hand, int month)` | Money in or out of the fund as a whole: a pay-in (by the dial or by hand), a draw-out, the month's transfer. |
| 651 | 8 | `void handPosted(String key, boolean buy, double units, double limit, int month)` | One of the player's orders posted at the step: a row from now until the step that withdraws it, at `limit` a unit for `units`. |
| 661 | 8 | `void handDropped(String key, boolean buy, double asked, double limit, String why, int month)` | ...one that could not be posted at all: the company was not listed or worth nothing, the bond had gone or was worth nothing, a bond buy too small to post, nothing was held to sell, or no room under the cap. |
| 671 | 7 | `void handClosed(String key, boolean buy)` | The step withdrew what was left of the hand's orders on a lot and side: each closes, what it did not fill lapsed. |
| 680 | 3 | `void closeAllHand()` | Every open hand row, closed: an older save, or a fund reset. |

### an older save (lines 684-736)

| line | len | member | says |
|---:|---:|---|---|
| 694 | 42 | `void seed(Game g, int month)` | Seeds the ledger of a save from before it: each market lot and bond at its market value at the load month (`seeded`, `since` that month), with the dividends paid to the city before it; the rescue lot exact from the co... |

### a reform (lines 737-756)

| line | len | member | says |
|---:|---:|---|---|
| 740 | 16 | `public void redenominate(double scale)` | Every money figure in the new unit: a lot's cost and record, a row's money, a bond's face and a share's price limit; share counts and a bond's price a unit of face do not move. |

### totals, for the checks (lines 757-766)

| line | len | member | says |
|---:|---:|---|---|
| 760 | 1 | `public double purchases()` | What every trade it booked paid, and what every sale and maturity brought in - since tracking began. |
| 761 | 1 | `public double proceeds()` |  |
| 763 | 1 | `public double realized()` | The realized gain on every lot. |
| 765 | 1 | `public double acbHeld()` | The ACB of every market and bond lot (the rescue lot's is TreasuryFund.getRescueCost()). |

