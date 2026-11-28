# UX1 audit 5: Policy, City History and Rankings

Pages 095 to 120 at 1920x1080, read from the pictures only, not the source. The cities are `playtest2400` (the big
city, month 2,401, 174,226 people), `slot1` (the young town, month 115, 1,888 people), and `playtest600` (checked
for the hub and the ladder). Places on a page are window pixels (x, y). None of these pages has a `-whole.png`.

Caveat: in the big city many Promises pages show their empty state. It pays no pension, no EI and no food
assistance, it has no school, and nobody researches. The young town is the only city where those dials are in use.

Faults common to the area, said once here and not repeated under each page:
- **Three ways to the same six areas.** The area buttons at top right (x 1138-1658, y 110), the hub's six cards,
  and the links in the strip. Each area then adds a second row of tabs, and the ladder adds a third.
- **The strip changes meaning.** The 4-cell strip (x 254-1028; the rest of its row is empty) is the hub's own
  (Tax a month, The floor, The policy rate, Promises), and it repeats on every Taxes, Wages, Money and Promises
  page and on Research › Found. Zoning, Research › Overview and Research › Funding have strips of their own.
- **Every dial is the same block.** A slider (300 px, or 160 px on the floor and Promises), then "WHAT IT WOULD
  DO" ending at x 1230. x 1230-1700 is empty on every dial panel.
- **Fine print.** Nearly every figure has a line of 9-10 px grey prose under it.

## Policy

### Policy hub (095)
- **Shows:** six area cards (a headline figure and a bar each), "What is biting" (alerts, each with one link),
  and recent decisions as chips.
- **Wrong:** the strip (y 150-215) repeats four of the six cards directly below it (y 235-440).
- The six card bars mean six different things: segments of the take, a tick on a range, three ticks, an empty
  track, a grey bar with a pink dot. None has a label or a scale.
- Big city: seven alert cards at about 120 px each. Four are the same "X cannot borrow · Subsidies". Every card
  says NEW, so NEW tells nothing. Their top edges are red, blue or (young town) yellow, with no key.
  "Trust is falling" is three lines of 10 px text.
- "Prices are 341158.3 times" has no thousands separator. The strip's "rule says 0.00%" and the alert's "rule
  would set it at -4.56%" are the same rule, given two ways.
- Hard to find: in the big city "Recent decisions" is at y 1056, and only its heading shows. In the young town
  the "recent" chips are from m23, 92 months ago. The young town leaves the lower 250 px empty.
- Missing from the alerts: the big city's 87.9% who ate less than a basket, no school, no pension for 23,669
  seniors, and nobody researching.
- **For the map:** the six cards become the Policy panel's list (one row each: name, figure, chevron). "What is
  biting" belongs in the alert marks on the top strip. Recent decisions go to History.

### Taxes: Everything (096)
- **Shows:** last month's take as a stacked bar, a card per tax with a sparkline, and one dial that moves profit,
  sales and wage together.
- **Wrong:** Fuel and Port dues have tabs but are left out of the take bar, the cards and "Tax a month". $755.7B
  is the four taxes only; port dues are another $10.7B a month.
- The take bar's legend is 9 px. "$755.7B last month · 19.3% of annual GDP" sits at x 1500, far from its heading.
- Property says "1.50% a year" among monthly figures. Its card's $110.3B is not its own page's $110.6B.
- The sparklines have no time axis and no scale. Below y 760 the page is empty in both cities.
- **For the map:** a panel: the take bar, the four taxes as rows, the dial.

### Taxes: Profit (097)
- **Shows:** the profit-rate dial, "Who pays" by sector (bar, tax on income, rate pill), and the sectors that
  paid nothing.
- **Wrong:** the table is wider than the eye: name at x 305, bar to x 1236, figure at x 1245-1430, rate at x 1510.
- Big city: the bars are linear, so Manufacturing fills the scale and 7 of 10 rows are 1 px stubs.
- "16 paid nothing: Retail, Industry, Iron & Steel, ..." runs 1,060 px of 10 px names.
- The rate pills (all 15.00%) don't look like controls. "· its own move" in 9 px blue under every name looks
  like a link. The hint "click a row for its own move" is out at x 1570.
- The bank's row has a bar in the young town and none in the big city. Its figure is the text "taxed at
  Retail's rate, in arrears".
- The old table sits behind a 9 px link, "details ▸ the table, as it was" (y 980).
- **For the map:** the dial goes in the panel. The table needs the wide view, or the top five in the panel with
  "all sectors". A sector's own rate can go on its building card.

### Taxes: Sales (098)
- **Shows:** the same shape as Profit, on sales.
- **Wrong:** refunds get no bar. Manufacturing's credit of $274.0B against $235.3B owed, the largest flow on the
  page, is drawn as nothing beside a 9 px grey "in refund" pill.
- Precision is mixed in one cell: "credit $289.7M against $5,533 owed".
- The 17-name "paid nothing" line again.
- **For the map:** as Profit.

### Taxes: Wage (099)
- **Shows:** the wage-rate dial, who pays by band, what a payslip loses, and the capital gains dial.
- **Wrong:** the heading "WHO PAYS: THE ELEVEN JOBS, IN FOUR BANDS" sits over four rows.
- Capital gains is hidden on the Wage tab. Its "What it would do" column has no figure, only "Applied to future
  realisations."
- "What a payslip loses" is a 1,420 px bar for one figure (15%) and three 0.00% legend items. Its button leads
  to Promises, another area.
- The page scrolls, and its panels sit 4 px left of the other tax pages (x 250 against 254).
- **For the map:** a panel. The payslip bar fits in 480 px.

### Taxes: Property (100)
- **Shows:** the property-rate dial, who pays on assessed value, and the farmland relief dial.
- **Wrong:** one rate in two units: the dial says 1.50% (a year), the line under it "Billed 0.1250% ... a month".
- The same wide table and the 17-name line. The farmland panel is cut by the window (y 1050).
- Big city: "Nothing under cultivation", yet the farmland dial is drawn full size and does nothing.
- Young town: "Farmland on the roll $0" over "the ground under the fields $691k". It reads as a contradiction
  until you see the 100% relief.
- **For the map:** assessed value is a land-value tint on the map. The dials go in the panel.

### Taxes: Fuel (101)
- **Shows:** one dial, the fuel duty, and what it would do.
- **Wrong:** one panel on an otherwise empty page (y 460-1080). The title is said twice, as the section heading
  and as the card title.
- The slider's numbers make no sense. Big city: "none to $794.57 a litre · one step is $306.98", under three
  steps. Young town: "$0.0293 ... $0.0113" and "A litre at the pump $0.0238", four decimals of a dollar.
- **For the map:** one row in a Taxes panel, with Port dues, as "other duties".

### Taxes: Port dues (102)
- **Shows:** one dial on the dues, with handling per tonne and what it would do.
- **Wrong:** one panel on an empty page. The value "x1.00 the default due" wraps to two lines (x 668-780).
- Young town: "no cargo crossed a berth last month". The town has no port, yet gets a page with a live dial.
- **For the map:** the dues go on the port's own card on the map, the dial in the Taxes panel.

### Wages: The floor (103)
- **Shows:** the minimum-wage dial; two gauges (against unskilled work, against the average wage with nations
  marked); what a minimum job pays for; who is near it; what it is tied to; the wage ladder below the fold.
- **Wrong:** six panels and a ladder, the busiest Policy page. The ladder is cut at the window (y 1060).
- Labels collide: the city's mark covers a nation's label (big city 68% hides Costa Rica 69%; young town 44%
  hides Canada 43%). Young town: the floor line cuts the "has room · 393 spare" pill (x 518, y 1018).
- "Tie it to" draws three options two ways: the chosen one as an outlined button with "in force", the others as
  flat grey chips. The currency page does the same.
- The dial is 160 px, against 300 px on Taxes. Big city: "one step is $201,666".
- The "over 100%" band of the first gauge is olive, a colour used nowhere else.
- Two ladders: "The wage ladder" here and "The qualification ladder" on the next tab.
- **For the map:** the dial, "who is near it" and the two gauges stack in a 480 px panel.

### Wages: The ladder (104)
- **Shows:** each qualification's posts, holders, months, fees, wages forgone and pay now/after cost, as bars,
  for young or established adults.
- **Wrong:** BROKEN in the big city. No row draws a bar, only the same faint tick at x 1398, while the figures run
  from $7.8M to $91.9M. The mid city and the young town draw their bars.
- The zero line (x 478) runs through every detail line ("N|o course", "fa|mily fees").
- Each detail line is six facts in 10 px: "38,972 posts · 88,243 holders · 60 months · family fees $3.4M ·
  wages forgone $0 · Above the step below after cost".
- The "Young adults / Established adults" toggle floats at x 875-1080, a third row of tabs, away from its table.
- "1 posts" (young town). The bottom 260 px are empty.
- **For the map:** the wide view, since it is a table. A panel can hold each row's pay and after-cost pair.

### Money: The policy rate (105)
- **Shows:** the rate line (every rate on one scale), the spreads against the world, the anchor (expectations
  and trust), the currency's level, the dial, the rule, the central bank's holdings, and three "What it would do"
  blocks below the fold.
- **Wrong:** the rate line's labels overprint. In the young town "target2%neutral 3.00%" and "dial 13.00% ...
  13.76%" are one unreadable smear (x 1140-1240). The big city stacks them in three cramped rows.
- One rate has three controls: 16 preset chips (0% to 100%), a slider, and "hand the dial to the rule". The
  bank's holdings have 13 chips in two rows, the second centred under the first.
- The rule's panel opens with six lines of 10 px prose before its figures.
- Young town: "In the rule's hand: it sets 14.69% every month", beside a dial that reads 13.00%.
- Two multipliers in one area: "Money constants are struck at ×30698.129" here, "341158.3 times" on the hub.
- The currency's-level panel is a sentence and a link in 480x250 px.
- **For the map:** the dial and the rule's verdict go in the panel. The rate line needs a wide strip.

### Money: The currency (106)
- **Shows:** five ways to set the currency, who buys at the strong edge, what the dial aims at, and a pointer to
  buying or selling it.
- **Wrong:** each choice is a text chip with a paragraph under it. Only the chosen one is outlined (with
  "in force"); the rest look like labels, not buttons.
- Young town: "the R$ jumped 0.0% weaker that month; the float's rule had it at R$1.0000", a sentence about a
  break that moved nothing.
- Big city: "Dollars bought at an edge US$785k" and "...paid in new money D$785k" give the same figure in two
  currencies, at D$4,558 per US$. Check it.
- "Buy or sell the D$" is a whole panel for one sentence and a link out to Trade › The reserves.
- **For the map:** a panel: the five choices as a radio list, then what the dial aims at.

### Money: Currency reform (107)
- **Shows:** prices since founding against the 10× that unlocks a reform, and a choice of 10:1, 100:1 or
  1,000:1.
- **Wrong:** "341158.30× of 10×" has no separators, and its bar is simply full.
- At ×341,158 the largest choice (1,000 to 1) still leaves ×341. Nothing previews the result (what $187.9T
  becomes).
- The choices are 9 px grey chips (y 497). Young town: one panel saying "Not yet"; the whole tab is locked.
- **For the map:** one row on The currency, and a confirm dialog for the act itself.

### Promises: Pensions (108)
- **Shows:** the pension cover, a pensioner household's month as a waterfall, what workers pay in, and what
  seniors receive.
- **Wrong:** big city: the waterfall draws "-$2.3M rent and bills" and "-$2.3M left for the shop" as tall
  upright bars, blue as if healthy. "Has to spend $0" is a flat line.
- "Covered 100%" with $0 collected and $0 paid (big city): 0 of 0 shown as full.
- The cover panel's lower half is empty (x 270-960, y 440-515).
- The 23,669 seniors eating out of savings appear only in one yellow line (y 500), never as an alert.
- **For the map:** a panel: the cover, the two dials and the waterfall (fits 480 px).

### Promises: Out of work (109)
- **Shows:** the EI cover, what workers pay, what EI replaces, and links to Schools and Food.
- **Wrong:** the cover panel is 1,440 px wide, with labels at x 267 and their values at x 1680 ("Insured to ...
  $13.0M a month").
- Young town: "EI past its premiums, a month $3,887" sits above "Paid a month, on the pool $7,221" for the same
  dial.
- The Food link floats outside any panel (y 854). Big city: "nobody drew EI", with 5,783 on EI.
- **For the map:** a panel.

### Promises: Food (110)
- **Shows:** who went without (a stacked bar), the food-assistance dial, and who would get it by group.
- **Wrong:** the big city's 87.9% who ate less than a basket is the worst figure in the area. It is not on the
  hub, in the alerts or in the strip.
- The "The shops · Sectors › Retail" link shows in the young town (under 0.01% short) and is missing in the big
  city, where 26.6% met empty shelves.
- "Who would get it": five identical bars ("got .10 of a basket" in every row), a bare decimal with a leading
  dot, and the figures in 10 px monospace at x 1430-1680. Every row has the same fork-and-knife icon.
- **For the map:** where people went without, as a tint by district. The dial and the groups go in the panel.

### Promises: Health (111)
- **Shows:** who pays for care (fees, premium, treasury), who was turned away (three tiles), the patient-fee
  dial and the health-premium dial.
- **Wrong:** young town: general care and childcare show a red "0" with "priced out · coverage cut to 100% of
  what the beds could do". An alarm on zero, and a note that contradicts it.
- Young town: at ×0.10 the fees show "$0" and "$1" a head. The treasury's share ($35.3B) is at x 1660,
  1,400 px from its label.
- Big city: "would meet its whole cost at x26.12, beyond the dial's reach"; the dial stops at ×15.
- **For the map:** who was turned away goes on each clinic's card. The dials go in the panel.

### Promises: Schools (112)
- **Shows:** who can afford a place at each level (against a month's wage), who pays for school, the schools
  this month, and the price multiplier.
- **Wrong:** big city, month 2,401: "No school is standing yet" is a 9 px pill at the top, every row says
  "no school", and the hub says nothing.
- Every row has a red tick at the same x (957), with no label or legend.
- The table runs 1,400 px wide (name at x 300, % at x 1270, status at x 1480), and the panels go on past the
  fold (y 1050).
- "The schools this month" is three $0 rows in a 470x280 px panel (big city).
- **For the map:** places and fees go on each school's card. The dials go in a panel, the affordability table
  in the wide view.

### Promises: Subsidies (113)
- **Shows:** 25 tiles, one per sector, each "protected / not protected" with a Protect it or Stop protecting
  button.
- **Wrong:** 25 identical outlined buttons. The one protected sector (Construction, big city) looks like the
  rest, because "Stop protecting" has the same style.
- Tiles with a red "cannot borrow" pill grow taller, so the buttons in a row don't line up (Industry's at y 449,
  Retail's at y 423).
- No tile shows a figure: not the loss, not the cost of protection, not the sector's size. Sectors the town
  doesn't have (Oil, Gold Mining) look the same as the rest.
- Eight sectors share one generic folder icon (Coal Mining, Quarrying, Cement, both Coppers, Forestry, Wood
  Products, Gold Mining).
- **For the map:** a sorted list in a 480 px panel, protected first, then "cannot borrow". A sector's
  buildings can carry "protected" on their cards.

### Zoning: The ground (114)
- **Shows:** the city's ground as one bar (built, free for anyone, open forest), the zones (none yet) with
  "Zone ground for an industry", and a summary of the forest law.
- **Wrong:** this is ground drawn as a bar. The zones, the free ground and the forest are all on the map, and
  this page covers it.
- "Zone ground for an industry" is the only filled primary button in Policy.
- Units are mixed: km² and m² side by side (young town "0.643 km²", "1,130 m²", "0.0108 km² of forest").
- The forest panel repeats the bar from The forest law tab exactly, and its "The forest law ›" link points at
  the tab beside it. The page is empty below y 690.
- **For the map:** all of it. Zones and free ground as a map tint, the forest as a layer, "zone ground" as a map
  tool, and the strip's four figures in a small panel.

### Zoning: The forest law (115)
- **Shows:** the forest under the law (open and cleared), two dials (protected, kept from building) with what
  they would do, and the crew rule.
- **Wrong:** "THE CREW RULE (E) · at 120 months" puts a code letter in player text. That panel has no icon, and
  "THE FOREST" heading has no (i); the other panels and headings have them.
- "Ground free for anyone 4.61 km²" here against "3.03 km²" on The ground. One is the long run, and only a
  footnote says so.
- "The most that may be cut 22k m²" sits in m² among km² rows. The page is empty below y 770.
- **For the map:** protected and working forest as a hatched map layer. The dials go in the panel.

### Research: Overview (116)
- **Shows:** each field's researchers, problems in reach, and finds here and from abroad; then research by
  school.
- **Wrong:** big city: every row reads 0 researchers, "nothing in reach", "no school". A page of zeros, with no
  link to build a school.
- Young town: "a fertiliser works (no FERTILISER good yet; Q11)" puts a code and an all-caps name in player
  text, and the in-reach line runs 760 px. The bottom 500 px are empty.
- **For the map:** a panel with four rows.

### Research: Funding (117)
- **Shows:** each field's grant dial and prize dial, its researchers, its cost, and the chance of finding first.
- **Wrong:** without a school, eight sliders "buy nothing", and each card says so twice (under the grant and in a
  boxed note).
- The 2x2 grid ends at x 1340, leaving 350 px empty on the right; every other page uses the full width.
- Young town: Engineering's in-reach text wraps to four lines in the card's head, so the two top cards differ in
  height.
- Missing: the way out, a "build a University" link.
- **For the map:** a panel, one field at a time: the two dials and the three figures.

### Research: Found (118)
- **Shows:** what the city knows, a timeline of finds by field (hollow means from abroad), finds made here,
  recipes in use, and equipment fitted (below the fold).
- **Wrong:** the strip is the hub's (Tax a month...), where Overview and Funding show research's own.
- Big city timeline: 200 years at about 60 px a decade, dots stacked five high, nothing labelled without hover,
  the last 60 years empty, and "2090 2100" crowded at the end of the axis.
- The recipes and equipment counts sit at x 530 on purple bars, yet another colour. Equipment (19 pieces) is
  below the fold.
- **For the map:** the timeline in the wide view; "fitted" on each building's card.

## City History

### City History (119)
- **Shows:** the city in four figures, two 10-year charts (real GDP, population), 15 topic chips, a large chart
  of up to three series with range buttons, the episodes bar, your decisions as flags, and "Write the year book".
- **Wrong:** the right axis reads "500000.00 ... 300000.00": a price index with two decimals and no separators.
- Episode and decision labels are cut to nothing ("Epid...", "Bor...", "From ab...", "Bought..."), and the
  flags overlap (y 1040-1057). In the big city the window cuts the flags off.
- "GDP, real (yr) $135.8M" sits under a top strip that says "GDP $46.9T / yr". The two differ about 345,000-fold
  on one screen, and only the word "real" tells them apart.
- The 15 topic chips wrap to two rows, with "The households" alone on the second. "clear all" and "log" look
  just like topics.
- **For the map:** the wide view. This is the one page that earns its full width. A panel can hold the four
  figures and the two small charts.

## Rankings

### Rankings (120)
- **Shows:** your city against nations and cities on one of 70 measures: options for board, money, prices and
  conversion; a ranked table; and your card with neighbours and your rank over the years.
- **Wrong:** 11 option buttons in one row, under 8 px group labels (BOARD, MONEY, PRICES, CONVERT AT).
  "Per year" and "Static" look disabled.
- The page opens on Full list from rank 1, so the player's row (214th, or 258th in the young town) is pages
  away. "Around you" should be the default.
- Young town: "Below you: Rotterdam US$75.6M" puts the player's own city (an older save) under itself. Its extra
  "Your cities at Now / Your age" pushes the list tabs onto a second row; the big city has no such row.
- A column of white dots at x 1250 has no heading (the last one is hollow). GDP is listed twice in Measures
  (Pinned and Economy). The rank card's column is empty below y 690.
- **For the map:** the rank card fits the panel (rank, neighbours, sparkline). The table goes in the wide view.

## The area's five worst problems

1. **The alarms are not where the player looks.** In the big city the hub's alerts are four look-alike
   "cannot borrow" cards. The big city's real crises each sit on one sub-page, sometimes in a 9 px pill: 87.9%
   ate less than a basket (Food), no school stands at month 2,401 (Schools), no pension for 23,669 seniors
   (Pensions), nobody researches. Recent decisions are below the fold.
2. **Drawings that are broken or contradict themselves.** In the big city the ladder draws no bars at all. The
   pension waterfall draws negatives as upright blue bars. 0 of 0 is "Covered 100%". A red 0 is an alarm. The
   rate line's labels overprint, the city's mark hides a nation's label, and a zero line runs through text.
   "The R$ jumped 0.0% weaker". Codes leak into player text: "(E)", "Q11", "FERTILISER".
3. **Navigation tripled and inconsistent.** Area buttons, cards and strip links all lead to the same six areas.
   There are up to three rows of tabs. The strip is the hub's on Taxes to Promises and on Found, but each
   area's own on Zoning, Research Overview and Funding. Options are drawn as buttons or as flat chips
   depending on whether they are chosen. Buttons jump across areas: Wage tax to Promises, Currency to Trade.
4. **Tables and rows wider than the eye, under fine print.** The "Who pays" tables run 1,200 px from name to
   rate, on linear bars that leave stubs. "16 paid nothing: ..." runs 1,060 px. In EI and Health, labels sit
   1,400 px from their values. Every figure trails 9-10 px grey prose, and the rule, trust and currency
   panels are paragraphs.
5. **Half-empty pages, and numbers unfit to read.** Fuel, Port dues, Currency reform, Zoning and Research
   Overview are one panel each on an empty page. Subsidies is 25 identical buttons with no figure. Every dial
   panel leaves 470 px empty. The numbers: "341158.30×", "500000.00", "×30698.129", "$0.0238 a litre",
   "one step is $306.98". Units are mixed: km² with m², a year with a month.

## Pictures worth keeping

- `playtest2400/095-policy.png`: three ways to the same areas, seven alert cards (four alike), "341158.3", and
  recent decisions below the fold.
- `playtest2400/097-policy-taxes-profit.png`: the 1,200 px table, the 1 px stubs, the run of names and the
  empty half of the dial panel.
- `playtest2400/104-policy-wages-the-ladder.png`: no bar drawn, and the zero line through the text.
- `slot1/105-policy-money-the-policy-rate.png`: the overprinted rate line, and 16 chips plus a slider plus a
  button for one rate.
- `playtest2400/108-policy-promises-pensions.png`: negatives drawn as upright bars, and "Covered 100%" of
  nothing.
- `playtest2400/113-policy-promises-subsidies.png`: 25 identical buttons, misaligned, with no figure.
