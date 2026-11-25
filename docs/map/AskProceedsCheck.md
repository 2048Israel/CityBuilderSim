# AskProceedsCheck.java - 460 lines · 26 methods · 13 constants · harnesses

`ham/citybuildersim/AskProceedsCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> The bond proceeds an open ask has raised pay its works: the treasury holds them outside the operating reserve, the order is
> placed on them, and what the month's bills drew is offered again under the debt limit (AutoBuilder.spendableFor(), held(),
> stillToRaise()).
> WHY (OA1, 2026-11-25). An open ask held an essential works for years: PW3's ensemble had a Water Treatment Plant's ask raise its
> whole cost, the cash 53.4M against the order's 49.8M and the reserve 6.9M, and the old rule took the reserve out of the proceeds
> first, so the order waited 133 months with water served at 26% - and, its sales past its cost, nothing was offered again, so
> neither the order, a sale nor the debt limit's test ever ran. The real law: bond proceeds are restricted to the works they were
> raised for, outside the operating reserve (GASB Statement 54; GFOA's fund-balance practice), and spent on it within months
> (26 CFR 1.148-2(e)(2): 5% bound within six months, 85% spent within three years).
> What this has to prove:
> 1. The finding, caused: a town short of water, an ask whose sales raised the order's whole cost, the cash its proceeds and half
>    the reserve; one pass orders the works from them, closes the ask, sells no bond.
> 2. The bills drew the proceeds: cash half the cost, room to sell: the ask is offered again for the gap, sold in full, and the
>    order placed that pass, the new paper the gap and no more.
> 2b. Drawn, and the market short: the bond is offered again and sells short, the order is not placed, the ask stays open with what is
>    still to raise, and the builder says why.
> 3. The debt limit releases it: the limit just under what a sale of the gap would take the debt to; the ask is withdrawn, nothing
>    sold, what it raised stays in the treasury.
> 4. Within months, in the played month: case 1's city played with the month, the works on order in the first month and audited.
> 5. Without the lack nothing changes: no ask, spendable() is the cash over the reserve (an overdraft in full); with proceeds and
>    reserve both whole, held() is the cash over the reserve beside the other asks' money; water served in full opens no ask.

**Uses:** [AutoBuilder](AutoBuilder.md) (67), [BuildAdvice](BuildAdvice.md) (22), [Game](Game.md) (22), [EssentialWorksCheck](EssentialWorksCheck.md) (10), [AuctionCheck](AuctionCheck.md) (9), [Auction](Auction.md) (5), [DebtQuote](DebtQuote.md) (4), [MoneyAudit](MoneyAudit.md) (3), [CityNeeds](CityNeeds.md) (3), [WorksFinanceCheck](WorksFinanceCheck.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [TimeSkipReport](TimeSkipReport.md) (1), [TempDirs](TempDirs.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `AskProceedsCheck.out` | `System.out` |  |
| 44 | `AskProceedsCheck.WATER` | `BuildAdvice.Measure.of(BuildAdvice.Kind.WATER)` |  |
| 45 | `AskProceedsCheck.POWER` | `BuildAdvice.Measure.of(BuildAdvice.Kind.POWER)` |  |
| 46 | `AskProceedsCheck.ROADS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS)` |  |
| 49 | `AskProceedsCheck.BINDING_COMMITMENT_MONTHS` | `6` | The months the works may take to be ordered at the latest: the six-month binding-commitment test for bond proceeds (26 CFR 1.148-2(e)(2)). |
| 52 | `AskProceedsCheck.WATER_DRAW_OVER_SUPPLY` | `3` | How many times what the plants supply the buildings are made to draw, so the town is short of water. |
| 55 | `AskProceedsCheck.LIMIT_ROOM` | `1` | The room the debt limit is given over the town's ratio, in years of GDP: a sale of an order's gap fits within it, a second plant's whole bond does not. |
| 58 | `AskProceedsCheck.PAVED_ROADS` | `24` | How many Paved Roads the town stands, so its streets are ahead of their load and the only works the builder wants first is water. |
| 61 | `AskProceedsCheck.AMPLE_ROOM` | `1e9, SHORT_ROOM = 100` | The face of paper the bank has room for where the market is made ample, and where it is made short. |
| 63 | `AskProceedsCheck.BY` | `"Automatic building"` |  |
| 66 | `AskProceedsCheck.RESERVE_BESIDE` | `.5` | The share of the reserve the cash holds beside the proceeds in the finding: the proceeds whole, the reserve half there, so the cash is over the cost and the cash over the reserve is under it. |
| 69 | `AskProceedsCheck.PROCEEDS_LEFT` | `.5` | The share of the proceeds the treasury still holds after the month's bills drew the rest, where the bills have drawn them. |
| 72 | `AskProceedsCheck.WATER_RAISED` | `.6, POWER_RAISED =.3, FREE_RESERVES = 1` | The shares of their costs that section 5's two asks have raised, each short of its cost; and how many reserves of cash stand free over the reserve and the money the asks hold. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 33 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 31 | 430 | **type** `public final class AskProceedsCheck` | The bond proceeds an open ask has raised pay its works: the treasury holds them outside the operating reserve, the order is placed on them, and what the month's bills drew is offered again under the debt limit (AutoBu... |
| 35 | 4 | `static void check(String words, boolean condition)` |  |
| 40 | 3 | `static boolean near(double a, double b)` |  |
| 75 | 4 | `static MoneyAudit.Result play(Game g)` | A month, played with the narration off, and its money audit (AuctionCheck.play() puts back AuctionCheck.out, which only its own main sets: null here). |
| 81 | 1 | **type** `record Finding(Game g, AutoBuilder auto, BuildAdvice.Suggestion card, double cost)` | A town short of water with its works' card: `cost` is the order's, one building and its ground, as the pass prices it. |
| 83 | 5 | `static BuildAdvice.Suggestion card(Game g)` |  |
| 89 | 3 | `static double orderCost(Game g, BuildAdvice.Suggestion card)` |  |
| 93 | 3 | `static double[] water(Game g)` |  |
| 98 | 11 | `static Game town(Path root, String name)` | WorksFinanceCheck.town(): its founding and terms, with PAVED_ROADS roads in its works. |
| 111 | 11 | `static Finding finding(Path root, String name)` | The town, its buildings made to draw more water than the plants supply, the builder on and its limit open. |
| 123 | 13 | `static AutoBuilder.OpenAsk ask(Game g, BuildAdvice.Measure m, BuildAdvice.Suggestion card, double cost, double raised)` |  |
| 138 | 7 | `static void open(Finding f, double raised, double cash)` | The ask for the water works open with `raised` of its cost raised, and the treasury at `cash`. |
| 146 | 4 | `static AutoBuilder.Entry order(AutoBuilder auto, int from)` |  |
| 151 | 3 | `static double paid(AutoBuilder.Entry e)` |  |
| 155 | 5 | `static double soldFace(Game g)` |  |
| 162 | 6 | `static void steps(AutoBuilder auto)` | What the pass ordered, a line each, and what it said of water: the figures a reader sees beside the checks. |
| 169 | 4 | `static void figures(Game g, AutoBuilder.OpenAsk a, AutoBuilder auto)` |  |
| 175 | 9 | `static void stands(Finding f, AutoBuilder.OpenAsk a)` | The conditions every water section stands on, said first. |
| 186 | 5 | `static void limitOpen(Finding f)` | The debt limit open, said first where a section needs the sale: the town is within it with room to spare. |
| 192 | 39 | `static void theFinding(Path root)` |  |
| 233 | 7 | `static Finding drawn(Path root, String name, Auction.Cap cap, double room)` | Case 1's city with the bills drawn: the treasury at half the cost. |
| 241 | 34 | `static void billsDrew(Path root)` |  |
| 276 | 36 | `static void marketShort(Path root)` |  |
| 314 | 4 | `static double limitUnder(Game g, DebtQuote quote)` | The highest debt limit, on its slider's steps, that a sale of `quote` takes the debt past: it is at or over the ratio now, or the fixture says no step lies between. |
| 319 | 31 | `static void limitReleases(Path root)` |  |
| 351 | 34 | `static void withinMonths(Path root)` |  |
| 386 | 61 | `static void withoutTheLack(Path root)` |  |
| 448 | 12 | `public static void main(String[] args) throws Exception` |  |

