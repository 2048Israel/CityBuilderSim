# UX1 audit 3: Sectors, Government and Finances

Pages 047 to 071, read from the picture tool's PNGs at 1920x1080. playtest2400 (month 2,406, 174,226 people,
money in D$, GDP $46.9T a year) was read first, then slot1 (month 115, 1,888 people, R$, GDP $4.3M).
playtest600 (month 606, 3,900 people) was read only for 047 and 053. None of these pages has a `-whole.png`, so
nothing below the window's fold was read: the sector list's last row, Government's Central bank card, Your
rate's "The short end", the fund's holdings. The sector books are Manufacturing in playtest2400 and "Industry"
(food mills) in slot1.

## Sectors

### 047-sectors (the sector list)
- **Shows:** a four-tile strip (kept, losing 8 of 25, workers, running at) above 25 sector cards, three across.
  Each card has profit a month, a sparkline, workers, a bar and one coloured reason line (money, ore, staff,
  credit, enough, building).
- **Wrong:** every sector gets the same size of card. In the big city about 13 of the 18 cards on show say
  $0/mo, 0 workers and "no plant standing", most with "no deposit to dig", and each is as large as
  Manufacturing's $2.2T. The order is fixed rather than by size or trouble, and the last row is cut at the fold.
- The worst news is in the smallest text: "power 7%" on Retail and Iron & Steel, and "handed over 28% of what
  was asked" on Retail, in about 9 px grey.
- Numbers lose their scale: "$14,155,161.0k a month", "($-14,106,717.7k this month)" and "$827,546,891k" sit
  beside "-$32.6B/mo" in the corner. The minus comes after the $ in one and before it in the other.
- The names are confusing: "Industry · food mills" sits next to "Food Processing" and "Manufacturing". Most
  extraction sectors share one generic factory icon, while Iron Mining has an arrow.
- Empty cards draw a cross of axis lines where the sparkline would be. The summary strip uses 770 of the 1,440
  px. slot1's Forestry says "the forest it may cut, 0k m³, would last 0 months".
- **For the map:** the list becomes a panel with one row per sector (icon, name, $/mo, workers, one status
  mark), and the dead sectors fold into "11 with nothing standing". On the map: tint the selected sector's
  buildings, mark a losing plant, and put the binding gate ("power 7%") on the plant's card. Deposits are a
  map layer.

### 048-sectors-manufacturing-operations (slot1: foodindustry)
- **Shows:** the sector's book: five tiles (kept, margin, cash, owes, running at), a status banner and five
  tabs. Below are what goes in, the plant (a 43% ring and six gate bars) and what comes out, then "How its
  tonnes move": each good's legs and modes.
- **Wrong:** "How its tonnes move" draws every leg at the full width of its row. "Sea 0" and "World lorry 0"
  are as long as "Sea 171k", and slot1's "Truck 35" is as long as "Truck 1.5k". Zero tonnes look like the
  biggest flow.
- In the big city the IN column's grey bars are all the same length, and each subtitle ("316,095 tonnes · 0%
  from the city") is 9 px.
- OUT's pills ("$16.2M · costs $7.5M") have no label. OUT has two rows with 250 px of empty card under them.
- The banner's "Investors ›" link duplicates the Investors tab. slot1 lists Crops twice with no grouping.
- **For the map:** the tiles, the banner and the plant ring with its six gates fit the panel, since they are the
  sector at a glance. The in and out lists fit narrow. The tonnes' legs are a flow: draw them on the map
  (port, roads, world edge) or keep them in a wide view.

### 049-sectors-manufacturing-income
- **Shows:** a waterfall of where a dollar of revenue went, "In short" (this month, last month, change), "Read
  as ratios" pills, and "Of every dollar it took" as one bar.
- **Wrong:** "In short" is fixed in "$ millions". The big city prints 4,968,785.7 (seven digits, where it
  should say 5.0T), and the young town prints 0.1 and 0.0 on every line, so the table says nothing.
- Margin shows twice: the tile's 44.3% and the Net margin pill, both green. slot1's "Earnings a share $0 a
  month" sits on a sector that kept $37k.
- "Summary | Statement" is a segmented control at the far right, so this page has two styles of tab.
- In short and the ratios take the left 760 px, and the right 680 px of the page is empty. The waterfall's
  labels are about 9 px.
- **For the map:** the waterfall and the one bar are wide-view statements. The ratios fit a panel, and so does
  In short once its unit scales.

### 050-sectors-manufacturing-balance-sheet
- **Shows:** what it owns against owes plus equity as two bars, In short, ratios, an "Its equity this month"
  waterfall, and a link to Investors.
- **Wrong:** In short's figures are cut off in the big city ("66,768,4...", "66,862,7...", "13,899,4...") and
  read 1.4, 0.2 and 0.0 in the young town.
- In both cities the equity waterfall shows two equal tall bars (start and end) and four steps too thin to
  see: 200 px of chart that shows nothing.
- Current ratio and quick ratio are empty "–" pills when the sector owes nothing. The owes-plus-equity bar has
  one segment.
- The footer link is a third way to Investors, after the banner link and the tab.
- **For the map:** the two bars and the ratios fit a panel. The waterfall moves to a wide view or is dropped.

### 051-sectors-manufacturing-cash-debt
- **Shows:** a cash waterfall (from trading, investing, financing), free cash flow and cash on hand, "Can it
  carry what it owes", what its borrowing costs (a rate bar and a leverage gauge), and a two-column "What it
  borrows on" table.
- **Wrong:** in both cities the leverage gauge's labels print over each other, so its caption can't be read.
- New borrowing costs (4.61%) and leverage (0.00) appear in the card and again in the table below.
- This waterfall gives start and end as text, while Income's and Equity's draw them as bars: three waterfalls
  in two styles.
- "Can it carry what it owes" is three pills in a mostly empty card. The table's rows run 710 px from label to
  figure.
- **For the map:** the tiles and the three carry ratios fit a panel. The table and the waterfall need a wide
  view.

### 052-sectors-manufacturing-investors
- **Shows:** a shareholder strip (quote, fair value, EPS, dividend, yield, P/E, P/B, held). Then its owners
  (holders bar, bid/ask, market cap, a price chart), its buildings (standing, value added, investors' estimate,
  the first gate each fails) and its equipment.
- **Wrong:** in the big city the pink "Fit Electric motor drive · $645.8M ›" and "· $3.6B ›" buttons print
  over the value-added figures ($34.2B/mo and $31.5B/mo).
- The status banner is drawn twice, once for the page and again inside Investors. Quote, fair value and yield
  appear in both the strip and the owners card.
- Per-share figures round to whole dollars. slot1 shows the quote as "$9", fair value as "$8", and earnings
  and dividend a share as "$0", although the sector paid a $14,014 dividend.
- In the big city the equipment section is below the fold.
- **For the map:** each row of "Its buildings" belongs on that building's card on the map: value added, the
  gate it fails, the equipment to fit. The strip fits a panel. The price chart needs a wide view.

## Government

### 053-government-overview
- **Shows:** five tiles (earned, surplus, banked, owed, tax take), government wealth with "Three ways back",
  two donuts with the balance between them, the "From earned to banked" bridge, then Central bank and Against
  the economy below the fold.
- **Wrong:** one month has three results side by side: EARNED +$608.1B, SURPLUS +$788.3B, BANKED +$788.3B
  (playtest600: -$637k, -$104k, +$460k). Only the bridge, third block down, explains them.
- slot1 contradicts itself. The same card says "SURPLUS +$21k" and "a year's deficit, 54.4% of annual GDP",
  and the town is "broke" at -3.7 years of revenue yet "rated AAA". playtest600 draws its deficit (-$104k,
  "short $104k") in green.
- "Three ways back" (buy back paper, cut business tax, raise EI) is also offered to the broke town, where all
  three spend money it doesn't have.
- "Hoarding" (big city) is the same red as "broke" (slot1), while "rich" (playtest600) is amber.
- The two donuts use the same colours for different things: blue is business tax on the left and transit on
  the right.
- In playtest600 the BANKED card's pill ("mostly: Exchange gain (loss) on foreign debt +$567k") runs past the
  card's edge. The same item shows as -$567k and +$567k on this page.
- **For the map:** the tiles go to the top strip. The wealth line, the balance and one donut at a time fit a
  panel. The bridge needs a wide view, and Central bank belongs with Bank.

### 054-government-revenue
- **Shows:** a row per revenue source, each with a bar, the month's figure, its share of the budget and of
  GDP, and "set it ›". Below are a "nothing this month" list and the total.
- **Wrong:** the page shows the player a developer's check line: "Capital gains: assessed $86.2B; cash
  collected ...; noncash claim losses $0; ... USD held 0.0; idle cars 0.0. These stocks are not cash revenue."
  It is also the stability harness's red: a new node at every redraw, so it flickers.
- The bars are 815 px wide, and the figures sit 1,100 px from the names, so the eye loses the row.
- The "of GDP" column needs a check. slot1's sales tax ($9,690 a month) reads 12.21% next to business tax's
  $34k at 14.08%. In the big city, sales tax ($79.4B) is 1.46% while property tax ($110.3B) is 2.86%.
- "• who" under each name is a 9 px link. Half the page is empty.
- **For the map:** the list fits a 480 px panel with short bars, and "set it ›" opens the tax control in the
  same panel. Property tax, land sold and transit fares by district are map tints.

### 055-government-spending
- **Shows:** the same kind of list for spending, then three cards: the debt, the services that charge, and
  pensions.
- **Wrong:** the headline's share of GDP disagrees with its rows. "$248.3B paid out · 13.8% of annual GDP"
  sits over rows that add up to 6.4%, and slot1's "$78k · 92.5%" over rows that add up to about 21%. If the
  headline covers twelve months, its line ("· June 2100") doesn't say so.
- Buildings is a negative spending row (-$4.5B, -1.8% of the budget) with an empty bar.
- The debt card's bar has one segment ("Term loans"). slot1's "Exchange gain (loss) on foreign debt +$3,431"
  floats between the total and the cards.
- The rows are as wide as Revenue's, and the bottom third of the page is empty.
- **For the map:** the list goes in the panel. Spending on healthcare, police, transit and repairs is a
  district layer, and fee cover ("fees cover 4%") goes on the service buildings' cards.

### 056-government-output
- **Shows:** GDP as a stacked chart (consumption, investment, government) with real GDP as a line ("a rolling
  year, in founding money"), GDP this month, four component cards, the month as one bar, and a strip (annual,
  trend, per head, year on year, month on month).
- **Wrong:** GDP is the city's economy, yet it lives in Government › Output, the fourth tab. The header's GDP
  tile doesn't lead here.
- The chart is in founding money ($0 to $200M) while the figures beside it are in today's money ($4.0T this
  month). The gap between them is net exports, and the chart doesn't label it.
- "This month, as one bar" draws slot1's net exports (-$24k) as a positive grey segment, so the bar adds up to
  more than GDP.
- The bottom strip starts with 250 px of empty cell, and the "GDP this month" panel has empty space under it.
  "Month on month -1% annualised" reads as two different things.
- **For the map:** the GDP figure and its four parts fit a panel, opened from the top strip's GDP tile. The
  chart needs a wide view.

## Finances

### 057-finances (the hub)
- **Shows:** five tiles (treasury, owed, the rate, coupon, next due), "When it falls due" by year, the
  rollover settings, "When the bank fails" (automatic, or wait for my button), and "Finances, opened": six
  doors to the area's pages.
- **Wrong:** the six doors into the area's 14 pages are the last thing on the page (y 880 to 1000), below the
  rollover and bank settings.
- The treasury is "D$187.9T" here and "$187.9T" in the header and on Government: one figure, two currencies.
- Settings look like tabs: "By hand / Same structure / 12-month bills" and "fair + 1 pt / at ... / Fill it"
  are drawn like the page's own tabs.
- 2.37% appears three times: the tile, The position and Borrow. "When the bank fails" is a 700x290 px card
  with two lines in it.
- **For the map:** the tiles go to the top strip, and the doors become the panel's menu. The rollover and
  bank-failure settings go to a settings panel or to Policy.

### 058-finances-the-position-overview
- **Shows:** the balance (treasury, owed, net), the credit band (your rate between spotless and hopeless),
  Against the economy, and a chart of what is owed and the rate.
- **Wrong:** the chart's axis reads "$4000B" and "$3000B" (B where it means T, and "$" where it means "D$").
  A stray "you" label sits under the axis.
- In slot1 the borrowing flags along the axis are cut off and overlap ("Borrowed $32,000: 6-...", "Borrow...").
- The credit band is a 700 px card holding one bar. Against the economy spends 1,150 px on three figures.
- The same five tiles head all 14 Finances pages.
- **For the map:** everything except the chart fits a panel. The chart needs a wide view.

### 059-finances-the-position-debt-service
- **Shows:** debt service against the take on a green, amber and red band, with a "Last month" card
  (interest, coupon, discount, principal) and, in slot1, a foreign card.
- **Wrong:** the headline measures something different in each city: "A month's coupon, in cash 0.7%" in
  the big city and "The next 12 months 27.4%" in slot1, in the same place at the same size.
- The explanation is one 1,150 px line of 9 px text, and the band is 1,170 px wide for one number.
- The big city has no foreign card, so 60% of its page is empty.
- **For the map:** the whole page fits a panel.

### 060-finances-the-position-home-abroad
- **Shows:** who holds the debt (one bar), then abroad: the dollar debt and what is behind it in slot1, or
  "Nothing is owed abroad" in the big city.
- **Wrong:** nearly empty in both cities: one bar and one line in the big city, one bar and two small cards in
  slot1.
- slot1's "the currency moved it -R$3,431 last month" is a green pill, while Government shows the same thing
  as "+$3,431": the signs look opposite.
- **For the map:** a panel. It can fold into The position's overview.

### 061-finances-the-position-your-rate
- **Shows:** the city's yield curve, what the market expects the dial to do, the 10-year rate broken into its
  parts, and real rates against the inflation the market prices. "The short end" is below the fold.
- **Wrong:** two rates have nearly the same name. The header's "RATE · D$ 2.36%" is the central bank's dial,
  while Finances' "THE RATE 2.37%" is the city's borrowing rate (slot1: 13.00% and 13.41%).
- "The dial" means the policy rate here, and the share of the surplus kept on the fund's Rules page.
- This is the central bank's material (the rule, credibility, expectations), filed under the city's Finances.
- It is dense: four charts and two grey paragraphs in small type.
- **For the map:** a wide view. The market-expects table alone fits a panel.

### 062-finances-the-book-every-piece
- **Shows:** a second strip of six tiles (at face, carried, worth today, if closed now, coupon, a point on the
  curve), each line on the curve, bars for at face, carried and worth, and "Every line": a 15-column table
  with "Close it now" on each row.
- **Wrong:** "If closed now" (-D$469.0B, -47.4%) is shown three times: tile, card and table.
- There are twelve tiles in two stacked strips before any content.
- The table is 15 columns of 9 px figures across 1,410 px. Its coupon column reads "8NN" (big city) and
  "3NN" or "6NN" (slot1) where the line's name says 8¼%, 3½% or 6¼%. The figure font may lack the fraction
  glyphs; worth a check.
- The chart's labels ("8¼% Dec 2106", "3½% Mar 1923") sit on top of the curve and its points.
- **For the map:** the table needs a wide view. The tiles and a short list of lines (name, face, falls due,
  close it) fit a panel.

### 063-finances-borrow-at-home
- **Shows:** the borrowing form: five kinds as cards, the term as six bars with today's fair yield, callable,
  how much face (a field and chips), the highest yield, the quote (right), and the auction (below the fold).
- **Wrong:** until something is asked, the quote card is 700x680 px with one sentence in it.
- "nothing asked for yet" is in large grey monospace, styled like a figure.
- Choices are drawn as tabs again (callable; fair + 1 pt). The "highest yield" control repeats the hub's
  rollover control word for word.
- slot1's field suggests "40B, 2.5T, 750M" to a town that owes R$6.6M.
- The auction, where the result appears, is below the fold.
- **For the map:** the form fits a 480 px panel, with the quote under it. The auction's bid chart needs a wide
  view.

### 064-finances-borrow-abroad
- **Shows:** the form for borrowing abroad: which money (8 currencies with their rates), bills or bonds, the
  term, how many USD, and what to do with the dollars.
- **Wrong:** the term is six plain number buttons here and six yield bars at home: one choice drawn two ways.
- The quote card's text still says "at home the auction itself".
- In slot1, "Default on the foreign debt..." is a red-outlined button at the foot of the borrowing form.
- Half the page is empty.
- **For the map:** a panel. Default should stand apart from the form, behind a confirmation.

### 065-finances-money-money
- **Shows:** the central bank's book (holds, owes, equity), what the public holds (M2), the money made and
  destroyed this month, and charts of M2 and M0 and of advances to the treasury.
- **Wrong:** in the big city the axis reads "$400000B" and "$200000B".
- "Advanced to the treasury" is a full-size chart of a flat zero on a $0-$1 axis. This month's two bars are
  empty (D$0 each) yet keep their full legends.
- The central bank also has its own pages under Bank, and this page links to them.
- **For the map:** the book and M2 fit a panel, and the charts need a wide view. Merge it with Bank's pages.

### 066-finances-the-bond-market-every-issue
- **Shows:** the businesses' bonds: outstanding, share of business debt, coupons, issuers, who holds them,
  every issuer as a card, this month, and the order books.
- **Wrong:** this is the firms' market, filed under the city's Finances, and no issuer card links to its
  sector's book. The lower 280 px is empty.
- **For the map:** a panel. An issuer's bonds belong on its sector's Investors page and its buildings' cards.

### 067-finances-the-bond-market-a-bond-s-book
- **Shows:** one bond (owed, coupon, sold, matures, worth, last traded) and the depth of its bids and asks.
- **Wrong:** you pick the bond from a row of 12 chips, out of 173 bonds, and there is no list or search for the
  rest.
- The chips name bonds like "Restaurants 2318", where 2318 is an issue number, while Search names them like
  "#2260 Restaurants 5.75% 2106", where 2106 is the year it falls due. The issue number reads as a year.
- This is a third level of tabs (page, sub-tab, chips), and 60% of the page is empty.
- **For the map:** a panel. The depth chart fits narrow.

### 068-finances-the-city-s-fund-portfolio
- **Shows:** the fund (worth, cash, income, paid to the treasury), a chart of what it is worth with a
  navigator and episode flags, what it holds, the rule's share, and holdings (144 lots, below the fold).
- **Wrong:** the chart disagrees with the figures beside it. Its axis runs to "$1000000B" for a D$58.0T fund,
  and its line falls over ten years while the text says "+D$3.6T (+5.9%) over 10Y".
- The episode flags overlap, and the last is cut off ("Bo...").
- In slot1 the fund is empty, but the page draws everything anyway: a $0-$1 chart, empty bars, R$0 five times.
- **For the map:** the figures and the holdings bar fit a panel. The chart and the holdings table need a wide
  view.

### 069-finances-the-city-s-fund-search
- **Shows:** a search over shares and bonds with All, Shares, Bonds and Held filters: your holdings, then
  the market, each row with a quote, a year's change, a sparkline and what is held.
- **Wrong:** rows run 1,410 px: the name at the left, the price at 800, the sparkline at 1,130 and the amount
  held at 1,600. The tab-styled filters sit 180 px to the right of the search box.
- In slot1 one row says "1Y: not recorded precisely enough" where the change should be.
- **For the map:** the list fits a 480 px panel (name, price, change, held). A share's row could open its
  sector's buildings on the map.

### 070-finances-the-city-s-fund-activity
- **Shows:** your orders, and what the fund did, filtered by all, trades, income, money in and out, or events.
- **Wrong:** in the big city, a D$58T fund lists dozens of "Written down: #22xx Luxury Retail ..." rows, from
  D$184 to D$176k, one row each, with "the model" in grey on every row. The amounts sit in two columns 1,200
  px from the names.
- In slot1 the whole page is one line: "Nothing in the last 12 months."
- **For the map:** a panel, with small write-downs grouped (for example "41 Luxury Retail bonds written down,
  D$1.2M").

### 071-finances-the-city-s-fund-rules-cash
- **Shows:** the withdrawal (a slider) and what it would do, the dial (the share of the year's surplus), what
  goes to the treasury, the rescue book (bank shares, preferred, warrants), pay in and draw out, and the rule.
- **Wrong:** one page sets numbers with three different controls: a slider with − and + (withdrawal), point
  chips -50 to +50 (the dial), and a field with chips (pay in).
- "The dial" here means something different from the central bank's dial.
- The rescue book is ten lines of 9 px grey text, and the rule is a seven-line paragraph.
- In the big city the page's two actions, Pay in and Draw out, are below the fold.
- The line "What falls due rolls as the same structure; a failed bank is resolved automatically. Change on
  Finances ›" appears here and on Borrow, but the setting is on the hub.
- **For the map:** the controls fit a panel. The rescue book belongs on the bank's building card and with Bank.

## The area's five worst problems

1. **Money has no single format.** Tables fixed in "$ millions" get cut off ("66,768,4...") in the big city
   and read 0.0 in the young town (049, 050). Card text says "$14,155,161.0k" (047), and chart axes say
   "$4000B", "$400000B" and "$1000000B" (058, 065, 068). Share figures round to "$0" (052). The currency is
   "$" in the header and on Government but "D$"/"R$" on Finances, and the minus sometimes comes after the $.
   One formatter that scales its unit would fix all of it.
2. **Text prints over other text, and a debug line shows.** Investors' "Fit Electric motor drive" buttons
   cover the value-added column (052), and the leverage gauge's labels overprint (051). Chart flags and labels
   are cut off or overlap (058, 062, 068), and a pill runs past its card (053, playtest600). Revenue shows the
   player a developer's check line, and that line flickers (054).
3. **Figures on the same page disagree.** Spending's headline (13.8%, or slot1's 92.5% of GDP) doesn't match
   its rows (6.4%, about 21%) (055). slot1 shows "SURPLUS" next to "a year's deficit" (053), and playtest600
   colours a deficit green. The fund chart's axis and line contradict "+5.9% over 10Y" (068). Debt service
   switches its headline measure between cities (059). Two rates (2.36% and 2.37%) share one name, and "the
   dial" means two different things (061, 071).
4. **The way in is buried and the area is four levels deep.** Finances' six doors are at the foot of its hub
   (057). A bond is picked from 12 chips out of 173 (067). GDP sits under Government › Output (056), and
   central-bank material is split between Finances › Money, Finances › Your rate and Bank. One sector page has
   three links to Investors and draws its banner twice (048 to 052). Settings are drawn as tabs (057, 063,
   069).
5. **Space goes to empty things while what matters is in small type.** The sector list gives about 13 dead
   sectors full cards (047), and zero flows get full-width bars (048). The young town gets full-size pages of
   zeros (065, 068, 070, 071). Activity lists D$184 write-downs one per row (070). Meanwhile "power 7%" and
   "handed over 28% of what was asked" are 9 px grey (047), and the fund's Pay in and Draw out are below the
   fold (071).

## The pictures worth keeping

- playtest2400/047-sectors.png: same-size dead cards, "$14,155,161.0k", the power shortage in 9 px grey
- playtest2400/050-sectors-manufacturing-balance-sheet.png: In short cut to "66,768,4...", the waterfall
  that shows nothing
- playtest2400/052-sectors-manufacturing-investors.png: buttons over figures, the banner twice
- playtest2400/054-government-revenue.png: the debug line (the flicker), rows 1,100 px from name to figure
- slot1/055-government-spending.png: "92.5% of annual GDP" over rows that add up to about 21%
- playtest2400/057-finances.png: the doors at the foot, D$ against the header's $, settings drawn as tabs
