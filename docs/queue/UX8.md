# UX8 - the figures read one way: money, its sign, its unit, the axes

From UX1's audit (`docs/queue/UX1-notes.md`, section 4; `docs/studies/ux1/audit-3-sectors-government-finances.md`
and `audit-4-bank-trade.md`, their worst problem 1 and 3). A row of its own because it crosses every money
page, and the page rows (UX9 to UX13) build on it.

## What is wrong today

- Statements fixed in "$ millions" are cut off in the big city ("66,768,4...") and read 0.0 in the young
  town; the Overview calls the same figure $16.8T.
- Card text says "$14,155,161.0k"; chart axes say "$4000B", "$400000B", "$1000000B", "$4000000B";
  "15000.000"; a "Q" for quadrillions; share figures round to "$0"; one column mixes "$948,454" and "$1.0M".
- The city's sign is "$" in the header, on Government and in the Bank, and "D$" or "R$" on Finances and
  Trade (the vault is $5.3T on one page and D$5.3T on another); the minus sometimes comes after the sign.
- Numbers unfit to read on Policy and Services: "341158.30×", "500000.00", "×30698.129", "one step is
  $306.98"; km² beside m², a year beside a month.

## What it builds

1. **One money formatter** that scales its unit (k, M, B, T) to the figure, keeps three significant figures
   in text and a column's one unit in a table (the unit in the column's header, the figures right-aligned
   with separators), and never prints a unit past T or a "Q". `ui/Money.java` is the interface's one place
   today; `Formats.java` the model's; the private `money()` copies in AutoBuilder, DecisionLog, Inbox,
   IndustryView, OilView, LandMap, NationsMap and RefineryView route through them.
2. **Axes that scale**: a chart's axis reads in the unit its largest tick needs ("$0 · $1T · $2T", never
   "$1000000B"), through `ui/ChartFitBench.java`'s or TimeChart's tick rule.
3. **The sign** (★ the study's call): the city's sign as FM1 lets the city choose it, written the same way on
   every page; a foreign sign (US$) only where two currencies meet (Trade, the currency, the reserves, debt
   abroad); the minus before the sign ("−$2.0T").
4. **Factors and units**: a factor prints at most three significant figures ("×30.7k" or "30,698×" as its
   sentence reads), a dial's step in the dial's unit, one land unit a page.

## Files

`ui/Money.java`, `Formats.java`, `ui/StatementView.java`, `ui/TimeChart.java` or `ui/ChartFitBench.java`, the
private copies above; the screens only where a page prints money by hand (`Where money -print` lists the
callers). No page's layout changes.

## Size

Medium: one session, about 800 lines, many of them small edits across files; a Sonnet slice suits the
routing, behind an Opus review.

## The guard

- **FormatsCheck**, **MoneySignCheck**, **MoneyWordsCheck** and **CurrencyCheck** green, with new sections:
  every magnitude from $1 to $10^18 scales to one unit with three figures; a column of mixed magnitudes takes
  one unit; an axis to $4T reads "$4T"; the sign is the city's on every page and the foreign one only where
  two meet; the minus before the sign.
- **UiSteadyCheck**: no page grows or rebuilds from it.
- Pictures before and after by `ui.PagePictures`: Sectors' balance sheet, Finances' fund portfolio, the
  central bank's balance sheet and Trade's month, big city and young town.

## Order and fit

After UX2, beside UX3 to UX7, before the page rows UX9 to UX13. It changes the words a page prints, not its
layout: CBK3, ASH3 and HO4 merge on top of it without conflict beyond a line's format call.

## Not this batch's

Any page's layout, the manual.
