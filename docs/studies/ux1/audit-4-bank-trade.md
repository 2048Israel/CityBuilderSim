# UX1 audit 4: the Bank and Trade

Pages 072 to 094 at 1920x1080, read from the pictures alone, not the source. Cities: `playtest2400` (the big
city, month 2,401: the commercial bank under its minimum, deflation of 3%, a central bank carrying a $16.8T
loss), `slot1` (the young town, month 115: a healthy bank, 9.8% inflation, an empty vault), and `playtest600`
for the Bank's choice page, Holdings and the Trade overview, where the first two disagreed. These pages have no
`-whole.png`, so anything below the fold is judged from the window picture alone. Places are given as window
pixels (x, y). The stability harness found nothing red on these pages.

## Bank

### The choice of the two banks (072)
- **Shows:** two cards side by side, the commercial bank (a badge, a sentence, four figures) and the central bank
  (a sentence, six figures), each with an Open button; two text links at the top right.
- **Wrong:** this page is little more than a click. In all three cities the cards fill the top 40% and the rest is empty.
- Big city: the commercial bank's trouble is said three times in the top 350 px: the red alert card, the red
  UNDER ITS MINIMUM badge, and the card's first sentence.
- One figure is measured against two benchmarks: "1.0% against 16.5%" (its own target), while the sentence says the city requires 8.0%.
- Inflation "-3.04% against 2.00%" (big city) and "9.79% against 2.00%" (young town) has no colour, while
  the capital line beside it is red or green and the top strip's inflation is coloured.
- "The policy rate ›" and "When it fails ›" are 9 px links at the far top right (x 1550-1700, y 112).
- **For the map:** both cards fit one 480 px panel, stacked, so this page need not be a stop of its own. On the
  map: a card on the bank building (capital against the minimum, profit, branches).

### Commercial bank: Overview (073)
- **Shows:** a health strip, the capital gauge, eight KPI tiles (profit, ROE, credit losses, how full, margin,
  costs, lent, deposits), and the ladder of rates from the policy rate to what each borrower pays.
- **Wrong:** big city: the alert card and the red strip under it say the same thing (under the minimum, 1.0% against 8.0%) in 140 px.
- The tiles stop at x≈1500 inside a card that runs to x≈1690, leaving a 190 px dead band.
- The ladder's bars are 820 px wide for rates that differ by tenths. In the young town ten of eleven borrower
  rows sit between 13.78% and 14.81%, a wall of near-identical bars. The ladder runs past the fold.
- Each ladder row ends in a grey chip and an (i) at x≈1670, 1,100 px from its label.
- Big city: profit $21.0B is green next to ROE -405% and a -$503B year. A green profit reads as good.
- This Overview has no KPI strip, while all six of its sub-pages have one.
- **For the map:** the gauge and the tiles fit a panel (two columns of four). The ladder needs a wide view or a
  shorter, sorted form. The red "shut 40 mo" and "defaulting" tags belong on the borrowers' buildings.

### Commercial bank: Profit (074)
- **Shows:** the KPI strip, a waterfall from interest earned to what stays in the bank (this month / last 12),
  four cards (margin, costs, ROE, the year), and the trading desk (flows, cap bars, holdings chips).
- **Wrong:** big city: the UNDER ITS MINIMUM badge in the strip and the red card under it say the same thing twice in the top 340 px, and do so on every sub-page.
- The waterfall's labels are 8 px. Four steps of -$52M to -$981M draw as hairlines.
- The ROE card has a track with only a tick at 12.5% and no fill, which means nothing at -405%. "This month alone ×29.4" is in a different unit from the % above it.
- Young town: the waterfall mixes "$23k", "-$3,778", "$309" and "$88k". The desk's "142% of a 50% cap" is drawn yellow, not red.
- The trading desk is a separate subject parked under Profit. Its holdings chips are 7 px and are cut at the fold.
- **For the map:** the four cards fit a panel. The waterfall fits 480 px only if it is turned vertical. The desk
  needs a sub-page of its own.

### Commercial bank: Balance sheet (075)
- **Shows:** bars for what it owns and what it owes, a two-column statement (what it owns; what it owes and its
  owners' share), the deposits beside the sheet, and its equity in parts.
- **Wrong:** two 700 px statement columns of about 22 rows at 11 px. Each row has a label, an (i), a figure,
  an arrow and a change.
- Young town: one column mixes "$948,454", "$3,325" and "$38,433" with "$1.0M" and "$3.5M".
- Deposits are $363.0T on the Overview and $358.5T here ("the city's own"). The D$4.5T from abroad that makes the difference is not said on this card.
- Two of the eleven rows are link-blue and expand, the other nine are white. The colour is the only hint that a row opens.
- In "equity in parts" the labels wrap to two lines and the zero line is unmarked. The "details ▸" link is 9 px grey at the foot of the page.
- **For the map:** the statement needs a wide view. "Beside the sheet" and "equity in parts" fit a panel.

### Commercial bank: Lending (076)
- **Shows:** the book by kind of loan, then "who owes it, one by one": a card per borrowing sector (rate, what it
  owes, a leverage gauge, stage chips, what is set aside), with the landlords last.
- **Wrong:** big city: the minimum is said three times above the book: the strip badge, the red card, and a
  badge with "lends only to keep its borrowers going".
- Shut sectors have their names in red (Industry, Business Services, Food Processing, Freight). Red reads as danger, but here it means closed.
- Each leverage gauge is a bare track with ticks at 0.90× and 1.50× and one marker, with no fill. Business Services owes $0 but still gets a full card.
- The cards in a row do not line up. In the young town an extra "bonds" line in Freight's card puts its gauge 23 px lower than the gauges beside it.
- The landlords card uses a different layout (one big figure) in the same grid. Young town: the "LENDS FREELY" chip sits alone on a row.
- **For the map:** of all the pages here, this one belongs on the map most. Tint business buildings by credit stage
  (stage 1, stage 2, defaulting, shut) and homes by insured mortgage; a sector's card can become its buildings'
  card. The book bar fits a panel.

### Commercial bank: Funding (077)
- **Shows:** what the city has banked against what funds the book (two bars), savers, the central bank's window,
  what the bank can carry, and its branches with a Build button.
- **Wrong:** a false alarm: "Some of its funding is foreign" is a full-width orange warning card at 1% (big
  city) and at 0% ($11k, young town). In the big city it sits under the red minimum card, so alerts fill y 260-430.
- "Build a Commercial Bank" is a pink outline button at the bottom right of the last card (x≈1355, y≈1015). It is
  the only build action in the area and is unlike every other button here.
- The savers chain "2.36% › asks 35% of it › it chose 0.19% › paid 0.056%" is four numbers with 7 px captions.
- "its deposits carry $2.2Q": a unit (Q) seen almost nowhere else. The branches donut ("5.8×") has no scale.
- Big city: the central bank card is six rows of $0.
- **For the map:** branches belong on the map, as a card on each branch (customers, fees, "another one?") with
  the Build button on it. Savers, the window and what it can carry fit a panel.

### Commercial bank: Capital & owners (078)
- **Shows:** the risk-weighted and leverage gauges, payout, how its equity moved, and its owners (a households /
  abroad bar, quote, bid/ask, fair value, return, yield, market cap) with a share-price chart.
- **Wrong:** the risk-weighted gauge repeats the Overview's. The leverage gauge is drawn white at 0.5%, under its
  3.0% minimum, while the risk-weighted gauge beside it is red.
- Big city: Payout is a mostly empty card ("pays nothing", rows of $0). "How its equity moved" spends 400 px on one bar.
- Young town, to check: the quote reads $3 and the book $0, but the chart's legend says "The quote $258" and
  equity is $1.1M.
- "Held abroad 100%" (in the young town the city does not own its bank) is a 7 px legend.
- The owners' block is twelve mono figures with 7 px captions, and it runs past the fold.
- **For the map:** the gauges and payout fit a panel. The owners' quote and chart need a wide view, or belong
  with the markets in Finances.

### Commercial bank: History (079)
- **Shows:** its rates over ten years with event flags, then charts of risk-weighted capital, return on equity,
  money set aside and written off, and how full it is (these continue below the fold).
- **Wrong:** event flags crowd the rates chart's axis and their labels clip ("Bought...", "Bougl" at x≈1430).
- Three lines in near-identical blues and greys. The shaded bands are unexplained, and a stray "you" sits under the y axis.
- ROE on a ±2,000% axis is noise. Risk-weighted capital on a 1,500% axis (young town) flattens the target line.
- Each chart stops 150-250 px short of its card's right edge.
- The most telling figures, "under its target 447 of 2405 months" and "under the minimum 82", are 8 px tags.
- **For the map:** a wide view, or the History tab. One chart (capital against the minimum) fits a panel.

### Central bank: Overview (080)
- **Shows:** a status strip, four cards of label/value rows (the money it has made, what it holds, its month, the
  rate and prices), and "the pages behind it" as a row of buttons.
- **Wrong:** big city: the commercial bank's "It is under the minimum" card heads all nine central-bank pages.
  It is another institution's alert.
- "The pages behind it" (y≈753) repeats the tab row at y≈157: the same eight names.
- $16.8T appears three times (M0, reserves, the loss carried). "A loss still carried $16.8T", the one figure that matters, is an amber row in the middle of a card.
- "Over the target, in points -5.04 pts" has no colour, and neither does inflation. "Over" sits oddly with a minus.
- About 30% of the window is empty below the cards, and there is no KPI strip, unlike the commercial bank and Trade.
- **For the map:** the four cards fit a panel as they are. Nothing here belongs on the map.

### Central bank: Income statement (081)
- **Shows:** a profit-or-loss statement (this month, last month, change, % of income) with ratios beside it.
- **Wrong:** big city: every line is "–" and the double rules draw as empty boxes. The statement has nothing in it.
- Its one figure is "16,765,274.8" ($ millions). The tab next door shows the same amount as $16.8T.
- The unit changes with the city ("in $ millions" in the big city, "in $ thousands" in the young town), and only an 8 px grey subtitle says so.
- In the big city the ratios are seven grey "–" pills. The right 28% and the bottom 40% of the window are empty.
- **For the map:** a wide view, or one "accounts" page that folds this together with the balance sheet and money made.

### Central bank: Balance sheet (082)
- **Shows:** the statement of financial position (assets, liabilities = M0, equity, for the record), ratios beside it.
- **Wrong:** big city: equity of -$11.4T (-214% of assets) appears only as "(11,427,150.4)" and a small red pill. Nothing on the page says the central bank owes more than it owns.
- The figures have nine digits and a decimal ("16,765,275.6") where the rest of the game writes $16.8T. There are four columns of them in 400 px.
- A third of the window is empty below, and there is a 500 px empty band on the right.
- **For the map:** a wide view. The equity line belongs in the panel's summary.

### Central bank: Money made and destroyed (083)
- **Shows:** money made (the remittance), money destroyed (the window repaid, its interest), M0 moved, M0 opened and closed.
- **Wrong:** big city: nothing moved. Every line is "–", and opening and closing are both 16,765,275.6. The page says nothing happened.
- Young town: "% of money made" reads (235.5%) and (341.0%), percentages of a small base that cannot be read.
- "M0 moved by -$61,787" in the ratios and "(62)" in the table are the same figure in two units.
- The footnote is 8 px grey. 55% of the window is empty.
- **For the map:** fold it into the balance sheet as one line (opened, made, destroyed, closed). If it stays a page, it needs a wide view.

### Central bank: Holdings (084)
- **Shows:** its paper by maturity, its book at market, what it bought and sold this month and since founding,
  and what it takes off the curve.
- **Wrong:** in all three cities every figure is $0 or "–": 28 rows of zeros and a ladder card that says there is nothing to put on a ladder.
- Some of the $0 are bold white and others grey, with no reason shown.
- "What it takes off the curve" is a mono mini-table without row rules, unlike every other table in the Bank.
- The dial that would fill it (the share of term paper) is on another tab (Policy).
- **For the map:** one line in the panel ("holds none of the city's paper") until it holds some, and the page only
  appears then.

### Central bank: Inflation & credibility (085)
- **Shows:** a sentence, then inflation (year, struck, target, gap, expected), credibility (trust gauge,
  expectations, drift), the real rate, and the rule.
- **Wrong:** the central fact is grey prose at the bottom right. In the big city "the rule would set -4.6%, the dial stops at 0%" while the dial is 2.36% and is held by "you".
- Trust is 26%, one point above the 25% floor, and is drawn plain blue. Inflation at -3.04% or +9.79% has no colour either.
- The inflation card repeats the Overview and the top strip (-3.04%, 2%, -5.04, -1.00%). A real rate of +3.36 points in deflation is not flagged.
- 30% of the window is empty.
- **For the map:** a panel. Inflation and trust belong to the top strip's inflation tile, which already shows "trust 26%".

### Central bank: Money (086)
- **Shows:** M0 in its parts, M2 against M0, the vault, since founding.
- **Wrong:** the M0 bar's two legend colours (reserves, currency) are near-identical blues.
- M0 $16.8T, M2 $363.0T, "21.7 times" and "M0 moved $0" repeat the Overview.
- The vault is "$5.3T" here and "D$5.3T" on Trade › The reserves. The city's money is written "$" in the Bank and "D$" or "R$" in Trade.
- Young town: the vault card disappears when empty and leaves a hole in the grid (the right column under M2),
  while Holdings shows its zeros. Empty things follow two different rules.
- **For the map:** a panel.

### Central bank: History (087)
- **Shows:** charts over ten years: M2 and M0, reserves, advances to the treasury, the remittance, the policy
  rate, expected inflation (credibility below the fold).
- **Wrong:** big city: two of the six charts are flat at $0 on a $0-$1 axis.
- The axes print "$4000000B", "$170000B" and "$14000B": billions left unformatted.
- There is no range control here. The commercial bank's history links "Over the years ›", and the currency chart has 1Y-All and a navigator.
- The policy rate chart repeats the commercial bank's rates chart. Each chart stops about 150 px short of its card's right edge.
- **For the map:** a wide view, or the History tab.

### Central bank: Policy (088)
- **Shows:** the policy rate (read only: "Set it on Policy › Money"), the rule (inflation target, how strictly
  it is held), its holdings of term paper, and how far it advances the treasury, each with "what it would do".
- **Wrong:** the central bank's main dial, the policy rate, cannot be set here, but three lesser dials can. The
  dials are split across two rail tabs.
- Each dial is drawn three ways at once: preset chips (0-5%, 0-100%, 3-36 months), a slider, and a -/+ stepper with the value.
- The headline figures are mono ("2.36% a year", "6 months of revenue = $6.1T") where every other page uses sans for its headlines.
- Card titles such as "THE CENTRAL BANK: ITS HOLDINGS" repeat the page's name.
- The rule's -4.6% / 0% / 2.36% story is the same grey paragraph as on 085. The rule card runs past the fold.
- **For the map:** every dial fits a 480 px panel (one slider and its "what it would do" line). The policy rate
  dial should live here, or this page should move to Policy.

## Trade

### Overview (089)
- **Shows:** a KPI strip (sold, bought, the month, the currency, the vault), alert cards, what we trade
  (diverging bars), the currency card with a chart, five partner cards, and "what decides whether a shock is
  survivable" (three cards cut at the fold).
- **Wrong:** young town: four full-width alert cards (money leaving, import cover, hot money, dollar debt) take
  510 px and two of them open the same page. The content starts at y≈800. The mid city has three alerts, the big city one.
- The vault is "US$1.2B" in the strip and "D$5.3T" in the alert directly under it.
- One fork-and-knife icon serves wire and cable, copper rod, thermal coal, fruit, fish and meat.
- Bought and sold are two purples a shade apart.
- The currency chart's axis reads "15000.000" and "80.000", and its two lines have no legend. "Policy › The currency" sends the player to another tab.
- **For the map:** the strip (turned vertical) and the currency card fit a panel. What we trade fits a panel as a
  sorted list with one-sided bars. On the map: exporters as building cards (the steel works, "sold abroad
  D$4.9T"). Partners belong on the globe, not the city map.

### The month (090)
- **Shows:** the month across the city's border as a waterfall (sold, bought, trade balance, income, current account,
  capital in and out, the month), then what the city holds abroad against what the world holds here.
- **Wrong:** young town: the four alert cards push the waterfall below the fold, so only the tops of its bars show.
- Small steps (income from abroad, +D$4.5B) draw as hairlines, with 8 px labels.
- In the holdings, labels sit at x≈280 and figures at x≈930, a 650 px jump for the eye. Every figure is given twice ("D$49.1B · US$10.3M").
- The "steps / river" toggle and the two account chips ("current account +D$2.8T") float at the top centre, away from the chart.
- **For the map:** the waterfall needs a wide view, or a vertical waterfall in a panel. The holdings bars fit a panel.

### What we trade (091)
- **Shows:** sold, bought and the balance this month, a diverging bar per good (by good or by business), "+16
  more", the last ten years, and a waterfall since founding.
- **Wrong:** every row gives its figure twice, at the bar's end ("D$4.9T") and in the right column ("+D$4.9T").
- The eye crosses the whole width: names at x≈290, bars centred at x≈1025, net figures at x≈1680.
- Big city: on a linear scale against fabricated steel's D$4.9T, the last five of the twelve rows draw no bar that can be seen.
- Wrong icons (fork and knife for copper, coal and cable). "Tap one" is phone wording in a desktop game.
- The ten-year chart's axis reads "$8000B" (no D$, and not $8T), and since founding prints "D$1.4Q".
- **For the map:** "by business" is a map view: tint or badge the buildings that export and import. The list fits
  a panel if each row is a name, one bar and one figure.

### Partners (092)
- **Shows:** a table of trading nations (sold, bought, their money, its rate, their policy rate, haul out and in,
  distance; the young town adds land factor and land bought), then "where the next tonne goes" (a log-scale curve
  per good, with nations marked).
- **Wrong:** the table has 9 to 11 mono columns. In the big city it ends at x≈1145, leaving a 550 px empty band. In the
  young town "10.86× 0.46 km²" run together.
- The columns differ between cities: land factor and land bought only appear in the young town.
- To check: the strip says bought R$94k (young town) and D$2.2T (big city), but the table's foot says R$80k and D$2.1T. The rest is not explained.
- The next-tonne chart is for experts: a log axis and five dotted verticals, and the dotted lines cross out the "Jamaica" label (x≈1375).
- Nation names are link-blue in this table and plain on the Overview's cards.
- **For the map:** a wide table, or a globe view (nations, distance, haul) rather than the city map. The
  next-tonne chart needs a wide view.

### The currency (093)
- **Shows:** the rate card (the rate, where goods are struck, the real effective rate, gauges against parity and
  the fair level), the rate over time with 1Y-All and a range navigator, and what moved it this month (below the fold).
- **Wrong:** the rate card is a 400 px column of 14 lines, two gauges and chips, the densest card in the area.
- This chart is the only one in the Bank and Trade with zoom, pan, ranges and a navigator. The others are fixed ten-year windows.
- The axis reads "15000.000", with three decimals. The rate is "D$4,558.26" in the big city and "D$50.4486" (four decimals) in the mid city.
- Two empty rows under the axis are labelled "episodes" and "you". There is a 200 px empty band right of the chart, and the two cards are of unequal height.
- The control is again on another tab ("Policy › The currency").
- **For the map:** the rate card fits a panel (it nearly is one already). The chart needs a wide view.

### The reserves (094)
- **Shows:** the vault in eight moneys and gold (held, worth, share, this month, policy rate; the young town adds
  what the city owes in each), whose it is, how long it would last with Buy reserves, and the money that can leave
  (below the fold).
- **Wrong:** a ten-row table to say "all in US dollars" (big city) or "nothing" (young town): nine rows of "–".
- The table ends at x≈1035, while its total ("worth US$1.2B · D$5.3T") floats at the far right (x≈1680).
- The real risk is in small text: D$4.5T of the D$5.3T vault is other people's money, "parked in the bank, free to leave". The city's own is D$879.7B.
- "Buy reserves" appears twice in one window (in the alert and in the card).
- To check: "the treasury's position +D$5.3T" equals the whole vault, but the page also says D$4.5T of it is parked money.
- **For the map:** "how long it would last" with Buy reserves and "whose it is" fit a panel. The vault table can
  shrink to the moneys actually held.

## The area's five worst problems

1. **Alerts stacked on every page push the content down and repeat themselves.** The commercial bank's "It is under
   the minimum" card heads all 16 Bank pages, the nine central-bank pages included. On Lending the same fact is
   said three times above the fold. In the young town every one of Trade's six pages opens with four full-width
   alert cards (510 px, two of them opening the same page), so The month's waterfall starts at y≈800. The
   design's row of alert marks at the top answers this: one mark per alert, never repeated inside the page.
2. **Pages and cards with nothing on them.** Central bank Holdings is 28 rows of $0 in all three cities. In the
   big city the Income statement and Money made and destroyed are all "–", History has two flat $0 charts, and
   Payout and the window card are rows of $0. The choice page and most central-bank pages leave 30-60% of the
   window empty. The central bank's nine tabs hold about two pages of content.
3. **One figure, written many ways.** The city's money is "$" in the Bank and "D$" or "R$" in Trade (the vault is
   $5.3T on one page and D$5.3T on another). Statements print "16,765,275.6" (in $ millions; thousands in the young town)
   for what the Overview calls $16.8T. One column mixes "$948,454" with "$1.0M". Axes read "$4000000B",
   "15000.000" and "$8000B". "Q" turns up for quadrillions, and Trade rows give D$ and US$ side by side.
4. **The dials are elsewhere, and each is drawn three ways.** The policy rate is "set on Policy › Money" and the
   currency on "Policy › The currency", while the target, how strictly it is held, the term-paper share and the treasury advance are set on
   Central bank › Policy, each as chips, a slider and a -/+ stepper at once. In the big city the key fact (the rule
   wants -4.6%, the dial stops at 0%, and you hold 2.36% in deflation) is a grey paragraph. "Build a Commercial
   Bank" is a pink button at the foot of Funding.
5. **Danger is not drawn as danger, and calm is.** The central bank's -$11.4T equity (-214% of assets) is a small
   red pill on a statement. Inflation 5 to 8 points off target has no colour on the Bank pages. Leverage under its
   minimum is white beside a red risk-weighted gauge. Profit is green next to ROE -405% and a -$503B year. Trust
   at 26% (floor 25%) is plain blue. Meanwhile "Some of its funding is foreign" raises a warning card at 0-1%,
   and the names of shut sectors are red on Lending.

## The pictures worth keeping

1. `slot1/090-trade-the-month.png`: four alert cards, with the page's waterfall pushed below the fold.
2. `playtest2400/076-bank-commercial-lending.png`: the minimum said three times above the fold, red names for
   shut sectors, gauges without fill.
3. `playtest2400/084-bank-central-holdings.png`: a whole page of $0.
4. `playtest2400/082-bank-central-balance-sheet.png`: nine-digit figures in $ millions, -$11.4T equity as a small
   pill, half the window empty.
5. `playtest2400/088-bank-central-policy.png`: chips, a slider and a stepper for each dial, while the main dial is
   "set on Policy › Money".
6. `playtest2400/091-trade-what-we-trade.png`: every figure twice, bars too small to see, food icons for coal
   and copper, a "$8000B" axis.
