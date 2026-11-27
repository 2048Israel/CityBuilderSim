# AskProceedsCheck.java - 562 lines · 29 methods · 13 constants · harnesses

`ham/citybuildersim/AskProceedsCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The bond proceeds an open ask has raised pay its works, as a fenced fund pays them: the treasury holds them outside the operating
> reserve, the order is placed on them, and where the month's bills drew the cash under them the order is paid from them all the
> same, the treasury overdrawn by what the bills drew as far as the central bank advances (AutoBuilder.spendableFor(), funded(),
> covered(), stillToRaise(); Game.allowProceeds()).
> WHY (OA1, 2026-11-25). An open ask held an essential works for years: PW3's ensemble had a Water Treatment Plant's ask raise its
> whole cost, the cash 53.4M against the order's 49.8M and the reserve 6.9M, and the old rule took the reserve out of the proceeds
> first, so the order waited 133 months with water served at 26% - and, its sales past its cost, nothing was offered again, so
> neither the order, a sale nor the debt limit's test ever ran. OA1's first mend sold again what the bills drew, bounded only by
> the debt limit (REV-OA1: a deficit wider than the market sold paper every month for an order never covered); 2b and 2c play
> that case for months. The real law: bond proceeds are restricted to the works they were raised for, outside the operating
> reserve (GASB Statement 54; GFOA's fund-balance practice); and a tax-exempt issue keeps its temporary period only if its issuer
> expects to bind 5% of the proceeds within six months and spend 85% within three years (26 CFR 1.148-2(e)(2)) - the six months
> this harness and AutoBuilder.PROCEEDS_WAIT_MONTHS read.
> What this has to prove:
> 1. The finding, caused: a town short of water, an ask whose sales raised the order's whole cost, the cash its proceeds and half
>    the reserve; one pass orders the works from them, closes the ask, sells no bond.
> 2. The bills drew the proceeds within what the central bank advances, a sale able to sell: one pass orders the works from them,
>    sells no paper for it, and the treasury goes under by what the bills drew; the money conserved.
> 2b. Drawn, and the market short, played for months: the ask closes within six months (in the first), raising nothing past its
>    cost, every month's money audit closed.
> 2c. Drawn past what the central bank advances, played for months with the bills drawing the cash back each month: the order is
>    not placed, nothing is sold twice (a repricing at most), the builder says why, and the ask is withdrawn at the first pass it has been open
>    PROCEEDS_WAIT_MONTHS.
> 3. The debt limit closes an ask with money still to raise: the limit just under what a sale of the rest would take the debt to;
>    the ask is withdrawn, nothing sold, what it raised stays in the treasury.
> 4. Within months, in the played month: case 1's city played with the month, the works on order in the first month and audited.
> 5. Without the lack nothing changes: no ask, spendable() is the cash over the reserve (an overdraft in full); with proceeds and
>    reserve both whole, held() is the cash over the reserve beside the other asks' money; water served in full opens no ask.

**Uses:** [AutoBuilder](AutoBuilder.md) (80), [Game](Game.md) (25), [BuildAdvice](BuildAdvice.md) (22), [EssentialWorksCheck](EssentialWorksCheck.md) (9), [MoneyAudit](MoneyAudit.md) (7), [AuctionCheck](AuctionCheck.md) (7), [Auction](Auction.md) (6), [DebtQuote](DebtQuote.md) (4), [CityNeeds](CityNeeds.md) (3), [WorksFinanceCheck](WorksFinanceCheck.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [TimeSkipReport](TimeSkipReport.md) (1), [TempDirs](TempDirs.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `AskProceedsCheck.out` | `System.out` |  |
| 51 | `AskProceedsCheck.WATER` | `BuildAdvice.Measure.of(BuildAdvice.Kind.WATER)` |  |
| 52 | `AskProceedsCheck.POWER` | `BuildAdvice.Measure.of(BuildAdvice.Kind.POWER)` |  |
| 53 | `AskProceedsCheck.ROADS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS)` |  |
| 56 | `AskProceedsCheck.BINDING_COMMITMENT_MONTHS` | `6` | The months the works may take to be ordered at the latest: the six months in which a tax-exempt issuer must expect to bind 5% of the proceeds (26 CFR 1.148-2(e)(2)). |
| 59 | `AskProceedsCheck.WATER_DRAW_OVER_SUPPLY` | `3` | How many times what the plants supply the buildings are made to draw, so the town is short of water. |
| 62 | `AskProceedsCheck.LIMIT_ROOM` | `1` | The room the debt limit is given over the town's ratio, in years of GDP: a sale of an order's gap fits within it, a second plant's whole bond does not. |
| 65 | `AskProceedsCheck.PAVED_ROADS` | `24` | How many Paved Roads the town stands, so its streets are ahead of their load and the only works the builder wants first is water. |
| 68 | `AskProceedsCheck.AMPLE_ROOM` | `1e9, SHORT_ROOM = 100` | The face of paper the bank has room for where the market is made ample, and where it is made short. |
| 70 | `AskProceedsCheck.BY` | `"Automatic building"` |  |
| 73 | `AskProceedsCheck.RESERVE_BESIDE` | `.5` | The share of the reserve the cash holds beside the proceeds in the finding: the proceeds whole, the reserve half there, so the cash is over the cost and the cash over the reserve is under it. |
| 76 | `AskProceedsCheck.PROCEEDS_LEFT` | `.5` | The share of the proceeds the treasury still holds after the month's bills drew the rest, where the bills have drawn them. |
| 79 | `AskProceedsCheck.WATER_RAISED` | `.6, POWER_RAISED =.3, FREE_RESERVES = 1` | The shares of their costs that section 5's two asks have raised, each short of its cost; and how many reserves of cash stand free over the reserve and the money the asks hold. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 38 | 525 | **type** `public final class AskProceedsCheck` | The bond proceeds an open ask has raised pay its works, as a fenced fund pays them: the treasury holds them outside the operating reserve, the order is placed on them, and where the month's bills drew the cash under t... |
| 42 | 4 | `static void check(String words, boolean condition)` |  |
| 47 | 3 | `static boolean near(double a, double b)` |  |
| 82 | 4 | `static MoneyAudit.Result play(Game g)` | A month, played with the narration off, and its money audit (AuctionCheck.play() puts back AuctionCheck.out, which only its own main sets: null here). |
| 88 | 1 | **type** `record Finding(Game g, AutoBuilder auto, BuildAdvice.Suggestion card, double cost)` | A town short of water with its works' card: `cost` is the order's, one building and its ground, as the pass prices it. |
| 90 | 5 | `static BuildAdvice.Suggestion card(Game g)` |  |
| 96 | 3 | `static double orderCost(Game g, BuildAdvice.Suggestion card)` |  |
| 100 | 3 | `static double[] water(Game g)` |  |
| 105 | 11 | `static Game town(Path root, String name)` | WorksFinanceCheck.town(): its founding and terms, with PAVED_ROADS roads in its works. |
| 118 | 11 | `static Finding finding(Path root, String name)` | The town, its buildings made to draw more water than the plants supply, the builder on and its limit open. |
| 130 | 13 | `static AutoBuilder.OpenAsk ask(Game g, BuildAdvice.Measure m, BuildAdvice.Suggestion card, double cost, double raised)` |  |
| 145 | 7 | `static void open(Finding f, double raised, double cash)` | The ask for the water works open with `raised` of its cost raised, and the treasury at `cash`. |
| 153 | 4 | `static AutoBuilder.Entry order(AutoBuilder auto, int from)` |  |
| 158 | 3 | `static double paid(AutoBuilder.Entry e)` |  |
| 162 | 5 | `static double soldFace(Game g)` |  |
| 169 | 6 | `static void steps(AutoBuilder auto)` | What the pass ordered, a line each, and what it said of water: the figures a reader sees beside the checks. |
| 176 | 4 | `static void figures(Game g, AutoBuilder.OpenAsk a, AutoBuilder auto)` |  |
| 182 | 9 | `static void stands(Finding f, AutoBuilder.OpenAsk a)` | The conditions every water section stands on, said first. |
| 193 | 5 | `static void limitOpen(Finding f)` | The debt limit open, said first where a section needs the sale: the town is within it with room to spare. |
| 199 | 39 | `static void theFinding(Path root)` |  |
| 240 | 3 | `static double advance(Game g)` | What the central bank advances the treasury past its cash today: Game.discretionaryRoom() over the cash in hand. |
| 249 | 9 | `static Finding drawn(Path root, String name, Auction.Cap cap, double room, boolean pastAdvance)` | Case 1's city with the bills drawn: the ask's whole cost raised, and the treasury holding less. |
| 260 | 1 | **type** `record Pass(AutoBuilder.Entry e, double paidAll, double newPaper, double otherPaper)` | The money over one pass from its start: the orders it placed (ground with them), the paper it sold, what the ask's own order took. |
| 262 | 10 | `static Pass pass(Game g, AutoBuilder auto, int logged, double borrowed)` |  |
| 273 | 37 | `static void billsDrew(Path root)` |  |
| 311 | 44 | `static void marketShort(Path root)` |  |
| 356 | 52 | `static void pastAdvance(Path root)` |  |
| 410 | 4 | `static double limitUnder(Game g, DebtQuote quote)` | The highest debt limit, on its slider's steps, that a sale of `quote` takes the debt past: it is at or over the ratio now, or the fixture says no step lies between. |
| 415 | 36 | `static void limitReleases(Path root)` |  |
| 452 | 34 | `static void withinMonths(Path root)` |  |
| 487 | 61 | `static void withoutTheLack(Path root)` |  |
| 549 | 13 | `public static void main(String[] args) throws Exception` |  |

