# UX1 audit 1: the chrome, the map and Build

Pages 000 to 021, read from the picture tool's PNGs at 1920x1080: playtest2400 first (the playtest city
at month 2,406, June 2100, 174,226 people), then playtest600 (month 606, 3,900 people) and slot1 (month
115, 1,888 people) where they differ. Five `-whole.png` read: playtest2400 004, 006, 010, 011 and 012.

**Dropped from the first pass, as artifacts of slot9's and slot2's broken load:** population 36,732 and
0, no sparklines, "generates 0.0 kW" / "serves 0" / 0% filled on every card, "$InfinityQ" on three Homes
cards, payback figures of 8,772 to 257,989 months, 25,089 clinics serving 0, "Builders 0 pts" beside
9,875,859 spare pts sold abroad, 104,582 Elevated Highways, the map's "1,525,000 buildings packed",
"a Assembly Plant", and verdict lines in the billions.

One caveat: the brief's stability line arrived without its list (it reads "...: STAB."), so the only
stability red named here is the one on 012, and it was not rechecked on these pictures.

## City (the chrome: header, rail, alerts)

### 000-city (the city as it lands)
- **Shows:** the game opens on Build's Overview (the same page as 004). Above it is the header: play
  button, date, speed, six figure tiles with sparklines, a "Needs you" chip (7), an AAA chip and an
  envelope (12). The rail is down the left, and a folded "UNDER CONSTRUCTION" side rail sits on the
  right (4 in the big city, 0 in slot1).
- **Wrong:** the landing page is a build form, not the city.
- **The header is an alert row of its own:** four of the six tiles carry red or orange warnings (POPULATION
  "▼ 134 this month", GDP "▼ 7.0% real", INFLATION -3.0% with "trust 26%" in red, OUT OF WORK 12.0%
  "high"). None of these is counted by the chip.
- Three red "[!]" toasts sit bottom right and cover the right half of "Turn automatic building on".
  "Government wealth: hoarding" (and in playtest600, "rich") gets the same red alarm mark as "Businesses
  are going bust".
- "AAA" is a green chip with no label, so it isn't clear it is the credit rating. The speed control
  ("< 1x >") is about 10 px text.
- One sum of money wears three signs: the treasury reads "$187.9T", the map's toolbar "cash D$187.9T",
  and Build's land tile "US$144.4M".
- The right side rail takes about 45 px on every page to show one count.
- **For the map:** the six tiles and the date, play and speed controls are the top strip as they stand.
  The chip, the envelope, the toasts and the tiles' warnings become one alert row. The construction side
  rail becomes a count in the strip, with the sites drawn on the map.

### 001-city-drawer
- **Shows:** the "City overview" drawer (Pin, close; Summary and Dashboard tabs). It has a cash, earned
  and population box, red rows pinned at the top (playtest2400: "Roads 104% served · 95% flow"; slot1:
  Roads 48% served; playtest600: none), then 13 folded rows from ECONOMY to BUILDINGS, each with one
  figure. The "Needs you" chip is outlined, so the chip opens this drawer.
- **Wrong:** the chip's count isn't listed anywhere on the drawer. playtest2400 says 7 and shows 1 pinned
  row and 4 red values (BANK, SCHOOLS, HEALTH, RESOURCES); playtest600 says 4 with no pinned row and 2
  red values; slot1 says 2 with 1 pinned row.
- What gets pinned is odd: Roads at 104% served is pinned, while "SCHOOLS 0% served" is an ordinary row.
- "TRADE 4,558.26 · surplus" is the exchange rate with no unit.
- The drawer lays over the Build page's left column instead of pushing it.
- The rows are monospace figures in four colours with "open all / close all" links in tiny text.
- **For the map:** this drawer is already the narrow panel. Its 13 rows are the needs sidebar's circles,
  one each. The pinned red rows are alert marks. It fits in 480 px as it is.

### 002-city-inbox
- **Shows:** the envelope's popover, a list of messages, newest first. Open ones are red with "[!]";
  settled ones are grey with a tick and "settled <month>".
- **Wrong:** the badge doesn't count what is open. playtest600 shows 6 with 3 open; slot1 shows 6 with 1
  open.
- **The big city buries the open alerts.** The badge says 12, and all nine items visible are settled.
  Seven of them are the same quarterly "The bank asks the city to buy its preferred shares", and two are
  "The bank failed, and the city took it over" (in July and December 2098, both grey). None of the three
  open alerts on the toasts is among them.
- Open items stay open for decades: playtest600's "The city is not looking after its people" has been
  open since June 1918 (32 years). slot1's one open item ("Government wealth: broke", January 1901) is
  white with no [!].
- The same open items appear five times on one screen: in the popover, as toasts, behind the chip, as red
  drawer rows and as red header figures.
- There is no dismiss control; settling is the only way out.
- **For the map:** this is the alert row. Each open item is one mark: left click opens it in the panel,
  right click dismisses it. Settled items go to a history behind a sub-menu, with repeats folded into one
  line.

## Map (Land office > Map, the rail's thumbnail)

### 003-map
- **Shows:** a "Land office > Map" toolbar (cash, "Convert D$", "From the vault", the layer chips Trade
  routes and Resources, a drag hint, "Select every offer", Fit, "Back to the land office · Esc"). Below it
  is the city on its terrain, with nations named, hatched numbered plots for sale (1-24) and a legend. The
  map takes the whole window under the header: the rail is gone.
- **Wrong:** the legend is open by default. It has about 55 swatches in 4 columns and covers about 580 x
  330 px of the bottom right. In playtest2400 its fourth column runs over a place name.
- Finance buttons ("Convert D$", "From the vault") sit in a map toolbar.
- The big city draws as a few hundred coloured squares; playtest600 still draws streets and single
  buildings. The legend's footnote counts what doesn't fit ("268 buildings packed at the city's edge" in
  playtest600).
- Shapes with no clear legend entry: pale-blue squares in the water (all three cities), mid-blue
  rectangles over the water west and north of the big city, and three dark "0" markers in the open sea.
- Nothing on the picture shows a building's information, and nothing from Build or the needs shows on
  the map.
- **For the map:** this is the ground. Fold the legend to the layer in use. Move the land-office controls
  into a Land panel. The big city already draws as tints by kind; a district view (kind and density)
  makes that deliberate.

## Build

Common to the category pages, 005-018:
- **The page:** 15 tabs in one line, at about 11 px: Overview, 5 city kinds with blue dots, a divider,
  then 9 market kinds with purple dots. The only key to the dots is a pair of dots at the top right ("only
  the city builds", "investors build too").
- Under the tabs is the same 4-tile strip on every page (Materials, Per unit, Land free with a red "Buy
  the best" link, Builders). It is about 820 px wide, with 600 px empty beside it. In the big city "Land
  free 4.61 km²" and "Buy the best: North 2 · 4.61 km²" are the same figure, which reads as a mistake.
- **The card:** 12-16 lines of 9-11 px text in up to five colours (white, grey, green or red price, yellow
  "this one: would lose..." or port warnings, purple "Investors" prose). It also has two bars, a "-1 0 +1"
  stepper and a big "Build · choose how many" button, which is two ways to choose one count.
- Two lines are the same on every card of every page: "builders at their price · ≈ 7.4 months queued ·
  foreign crews +16% a point, past 21.0 months".
- Rows are vertically centred, so the card tops are ragged. Cards are 4 to a row and end at about x=1480
  of 1690.
- Price colour is green when the treasury covers it and red when it doesn't (slot1's $1.6M Police
  Station against $435k); nothing says so.
- The "X are the scarcest: N% of their posts filled · why >" line is grey, at the far right. In the big
  city it names a different worker on each page (technicians, nurses, business-college graduates) at the
  same 84%, while the drawer says LABOUR 99% filled; worth checking.

### 004-build-overview
- **Shows:** a banner ("Your job is the city's own works..."), then four sections:
  - THE CITY'S JOB: 5 cards, each with a ring, a status and a coloured top border.
  - WHAT WOULD HELP MOST: 3 suggestion cards with "Show >", a build button and "Build all three"
    (playtest2400: 2 Police Stations, 21 Elementary Schools, 3 Community Colleges, $3.4T; playtest600
    and slot1: "Build 1 on credit").
  - AUTOMATIC BUILDING: 3 panels (spare margin, debt limit with bond yield, on/off).
  - THE MARKET BUILDS THESE: 9 tiles, each a count "standing" (Homes 38,545, Shops 845, Industry 340 · 4
    on site, Offices 0, Farms 0, Freight 1, Vehicles 0, Luxury shops 444, Restaurants 366 · 4 on site).
- **Wrong: the page argues with itself.**
  - Safety says "crime is in hand · nothing needed", yet the first suggestion is 2 Police Stations.
  - Utilities says "nothing needed" while 005's Power grid tile is 5%, 194 MW short.
  - Childcare is 1% with 7,370 people without, and no suggestion offers it.
  - Healthcare and Education read "served, short" beside 1% and 0% rings, and Infrastructure's 104% is a
    red ring.
- Each suggestion is about 5 lines of dense prose ("Opens ~6 mo; sized 12 mo out: people +6.1% on the
  trend, reserve target 15%. Land 0.0111 km² ≈ $875.4M..."). slot1's "Short 0.0968 km² of land" button
  looks disabled but is a link.
- Automatic building is 3 panels of about 475 px each, mostly empty. The debt-limit panel alone holds a
  slider, a two-way switch, a three-way yield picker, a text field and three paragraphs. Its "Turn
  automatic building on" sits under the toasts.
- The market tiles carry only a count: no ring, no status, unlike the city cards.
- Automatic building and the market row fall below the fold.
- **For the map:** the five rings become the needs sidebar. The suggestions fit a 480 px panel, one per
  row, with "Show" lighting the site on the map. Automatic building is a settings sub-panel. The market
  counts become map tints or a strip line.

### 005-build-utilities
- **Shows:** 3 coverage tiles (Power 222%, Power grid 5%, Water 312%), then POWER, 8 plant cards in 2
  rows (playtest2400: Coal 1, Oil-Fired 2 and Diesel 4 built; the rest none).
- **Wrong:** the tiles are sub-tabs: Power is outlined pink and only Power's cards show. Nothing says
  Grid or Water open their own lists.
- Power grid's ring says 5% beside "served · 194 MW short": "served" and short at once.
- Tops are ragged (the Wind Farm card is about 25 px higher than its row).
- "From abroad, December 1980 / Licence: none now. The patent ended..." takes two lines on cards that
  already have 14.
- The Hydro Station card reads "generates 0.0 kW" with a red "needs a river... none owned", and offers
  Build like the rest.
- **For the map:** a 480 px panel holds the 3 circles and one column of short cards. Plants could be
  marked on the map.

### 006-build-infrastructure
- **Shows:** tiles Streets (104%), Through traffic (100%) and Transit (65%). Below are STREETS (Gravel
  Road with a "Pave to a Paved Road" block, and Paved Road), PORTS (5 terminals), RESERVE (1) and BRIDGES
  (3). The page is 2,639 px, about 2.5 screens.
- **Wrong:** the tiles act as sub-tabs for the first section only. Ports, Reserve and Bridges have no tile
  and always show.
- Elevated Highway (35 standing, 6 on site) isn't on the page unless Through traffic is chosen.
- Streets and Through traffic both say "6 on site · ~6 mo"; the only road site on 019 is the Elevated
  Highway (5 of 11 built), so the same six are counted twice.
- Streets is a red ring at 104% beside "6.8k trips spare".
- The Paved Road card floats about 90 px below Gravel Road's top, leaving a hole.
- All three bridge cards carry the same red paragraph ("Both ends are on the city's own landmass...").
- slot1: Through traffic shows a 0% grey ring beside "served, enough".
- PORTS starts at the fold.
- **For the map:** roads, ports and bridges belong on the map: a traffic tint on streets, a card on a
  berth. The panel keeps the 3 circles and a short list.

### 007-build-healthcare
- **Shows:** 4 tiles (General care 128%, Childcare 1%, Senior care 1%, Death care 100%), then the chosen
  tile's cards. The big city opened on CHILDCARE (3 cards, none built).
- **Wrong:** Childcare and Senior care read "served, short" at 1% ("short by 7,370 children", "short by
  8,055 seniors").
- The lower 30% of the window is empty.
- The scarcity line ("nurses are the scarcest: 84% of their posts filled · why >") is grey, at the far
  right, 1,200 px from the cards it explains.
- **For the map:** a panel holds it whole. Coverage could be a district tint on the map (people without
  care).

### 008-build-education
- **Shows:** 9 tiles (Elementary to Institute of technology), then ELEMENTARY, 1 card.
- **Wrong:** the tiles are drawn a second way here: narrow, with the ring on top. Every other kind uses
  wide tiles with the ring on the left.
- In a city of 174,226, all nine tiles are 0% or "-", and every one says "served" ("served, short ·
  short 9,274 places").
- Nine sub-tabs lead to one card each.
- Institute of technology's ring reads "-" in blue and Business school's reads 0% grey, over the same
  "0 would come and be hired"; slot1's higher-ed rings are 0% grey. There is no way to tell "none needed"
  from "none built".
- The page is two-thirds empty.
- **For the map:** the 9 tiles become one stacked list in the panel (a circle and a line each). Picking a
  row shows its one card.

### 009-build-safety
- **Shows:** 2 wide tiles (Police, Cells), then POLICE, 2 cards.
- **Wrong:** the ring contradicts its own text:
  - playtest2400: "0.2x · 71 officers short of full cover" in a full green ring.
  - slot1: "1.1x · 2 officers short" in a green ring.
  - slot1: Cells is a 0% grey ring over "a cell for everyone caught" (playtest2400: 100%).
- "posts the city can't fill, per 10,000 officers: 1,810" (slot1: 24,075) is a figure no player can read.
- The page is 60% empty.
- **For the map:** panel. Police cover could be a district tint.

### 010-build-homes
- **Shows:** "Investors build these on their own. Building one yourself is optional.", then HOMES, 14
  cards in 4 rows (about 2 screens).
- **Wrong:** the same "Investors holding: housing ahead of jobs (96,287 now, 1,161 coming); rent does
  not cover a new Large-home luxury tower" is on all 14 cards, including the House's.
- The page's verdict ("studios: 47,793 households without a door · family homes: 37,720 to spare") is
  the biggest figure on the page, and it is grey, far right.
- The 14 "you have" counts add up to 38,545, the Overview's "Homes 38,545 standing": the investors'
  homes counted as yours (see 012).
- There are no coverage tiles; the city kinds have them.
- **For the map:** the verdict and a "what investors are doing" line go at the top of the panel, said
  once. The 14 types need a wide comparison table (residents, cost and land per resident). Homes are a
  density tint on the map.

### 011-build-shops
- **Shows:** GROCERIES (2 cards), FILLING STATIONS (1), THE BANK'S BRANCHES (Commercial Bank, 1).
- **Wrong:** the bank's branches hide under Shops.
- Rows are 1 or 2 cards wide, with half the width or more empty.
- "the drivers bought 1,406,017 L; the stations sell 15,707" is raw and has no unit on its second figure,
  while the one Filling Station's card says "sells 350k L of petrol a month".
- 840 + 3 + 1 + 1 is the Overview's "Shops 845 standing".
- **For the map:** panel. Shops could be a coverage tint (households far from a grocer).

### 012-build-industry
- **Shows:** about 75 cards in about 26 rows and some 20 sections, from FOOD MILLS and FOOD PROCESSING
  down. The page is 13,606 px, about 13 screens.
- **Wrong:** **broken (stability, from the brief, not rechecked here):** a month moves the scroller 0.6
  px. The place is held as a fraction of the run, not a pixel, so a page that grows above the view
  shifts it.
- Why it is "a mess, too many buildings to make a selection":
  1. Every card is open at full size with no filter, sort, search or fold. "none built" and "you have N"
     look alike. The city has 340 industrial buildings (Overview), but the first screens are all "none
     built", and nothing says which chains it has.
  2. The one verdict per section ("the city used 6,828 kg of bread; plants here make 0") is grey, at the
     far right, in raw numbers. Nothing ranks which chain is short.
  3. **The cards' figures disagree with each other.** The comparison figure, "its price, in months of what
     it adds", looks good (Bakery 5.5 mo, Bottling Plant 4.6 mo) because it counts output "at today's
     prices". The same card's investors line says "this one: would lose $576.9M a month". The Bakery
     makes 232k kg of bread where the city used 6,828 kg. The same holds on Offices, Farms and Vehicles.
  4. The best-buy chips ("adds the most per $") are the only comparison, they hold only within a section,
     and they sit on buildings the card says would lose money (Bakery, Mixed Farm, Assembly Plant).
  5. Each card's "Investors holding..." line is the same on every card of a section ("output ahead of
     demand", "the parts cost more than the vehicle fetches"), with only the loss changing.
- **City's own vs the market's: the cards do not tell them apart.**
  - "you have N" counts every one standing, investors' included. The Homes cards add up to 38,545, the
    Shops cards to 845, and the Diner card's "you have 366 · 4 on site" is a site 019 lists as "their
    order".
  - Only the purple tab dot, the line at the top, a purple icon tile and the per-card "Investors..." line
    say the market is involved. That last line says whether investors would build more, not whose the
    standing ones are.
  - Construction does tell them apart (019 Sites' "City" tag and "their order", 020's colours, 021's two
    lists); Build doesn't.
- **For the map:** this needs a wide view: a table with one row per type and sortable columns (makes,
  price, months to repay at real demand, land, staff gap, standing split between the city and investors),
  grouped by chain with the supply-and-demand line as each group's header. The panel shows the chosen
  row's card. Works go on the map by chain.

### 013-build-offices
- **Shows:** OFFICES, 7 cards: 3 offices and 4 research labs, none built.
- **Wrong:** the 6-line paragraph "A lab is the investors' building for a prize..." is repeated word for
  word on each lab card.
- Every office card carries the section's investors line ("39 more engineering licences before a practice
  could open"), even the Contact Centre's, which says "8.6 mo" and "would lose $2.8B a month".
- Labs read "exports 0 a month · the world pays nothing for it" with "-" for the months figure.
- **For the map:** the lab paragraph goes once in the panel header. Otherwise as 012.

### 014-build-farms
- **Shows:** FARMS, 4 cards, none built ("Farms 0 standing" on the Overview).
- **Wrong:** "the city used 0 t of crops; farms here grow 0" is the verdict, while the food mills on 012
  each want hundreds of tonnes of crops.
- All 4 cards say "0 tonnes/mo is not enough for a first Mixed Farm", naming the Mixed Farm on the other
  three. The Mixed Farm wears "adds the most per $" at 2.7 mo and "would lose $141.3M a month".
- About a third of the window is empty.
- **For the map:** panel. Farmland is a natural map tint.

### 015-build-freight
- **Shows:** RAIL (4 cards, none built) and HAULAGE (Truck Depot, 1).
- **Wrong:** the top line is worded unlike every other market page ("Freight's investors build these. You
  can build them too.").
- The Truck Depot card says "Freight would not build one" where the others say "Investors".
- All four rail cards repeat "borrowing ban, 77 more months · Rail Depot would need credit".
- "the city trades 686,374 t a month; the network carries 0" is a raw number.
- **For the map:** rail lines and depots belong on the map (the trade-routes layer). The panel lists
  them.

### 016-build-vehicles
- **Shows:** VEHICLES, 5 cards, none built.
- **Wrong:** "Investors holding: the parts cost more than the vehicle fetches · this one: would lose..."
  is on all 5 cards. The Assembly Plant wears "adds the most per $" at 12.7 mo and "would lose $11.8B a
  month".
- "the city used 443 cars; plants here make 0" is grey, far right.
- **For the map:** panel, or rows in 012's table.

### 017-build-luxury-shops
- **Shows:** LUXURY SHOPS, 2 cards (Boutique, you have 444; Department Store, none).
- **Wrong:** a whole tab for 2 cards; the page is 60% empty.
- "6,249 customers came; the counters serve 3,108" is grey and far right, and it says the city has half
  the counters it needs.
- **For the map:** fold into Shops as a section in the panel.

### 018-build-restaurants
- **Shows:** RESTAURANTS, 2 cards (Diner, you have 366; Restaurant, none).
- **Wrong:** like 017, it is a tab for 2 cards, mostly empty, and "5,230,801 meals wanted; the kitchens
  serve 4,941,000" is raw.
- The Diner's "you have 366 · 4 on site" is the Overview's 366 and 4, and the site is investors' (019).
- "Investors built 5 Diner (trimmed from 23...)": the plural is missing.
- **For the map:** fold into Shops (food) in the panel.

## Construction

### 019-construction-sites
- **Shows:** the Sites, Timeline and Demolish tabs and "< Build". THE BUILDERS strip has 6 figures and a
  queue bar from 0 to 33 months. The SITES table (playtest2400, 4 rows) has these columns: building with a
  tag ("City" or its sector), progress ("5 of 11 built", 53%), share of crews and time left, paid and
  owed, and status. The city's rows have up/down order arrows and Rush/Cancel; investors' read "their
  order".
- **Wrong:** the heading is "CONSTRUCTION" in capitals where Build uses "Build", and the page is full
  width (x=107) where Build is inset (x=250).
- The rail still lights "Build".
- The builders figure disagrees between pages: 24,797 pts "after the repairs" here, 30,253 pts on Build's
  strip.
- "across 4 sites, 15 buildings" matches neither the rows' totals (1 + 11 + 14 + 49) nor what is left to
  build (12).
- About 40% of the window is empty below the table.
- **For the map:** sites go on the map as markers with a progress ring. The builders strip goes in a
  panel. The table fits 480 px as building, progress and time left.

### 020-construction-timeline
- **Shows:** the same builders strip, then WHEN EACH FINISHES: one bar per site from now to +1 yr, blue
  for the city's and purple for investors' and landlords'.
- **Wrong:** the builders strip is repeated from Sites, and the bars repeat Sites' time-left column. Half
  the page is empty.
- **For the map:** a wide view (a Gantt chart) when it has many rows; otherwise one line in the sites
  panel.

### 021-construction-demolish
- **Shows:** WHAT TO PULL DOWN (a search box and a list split into "The city's buildings" and
  "Businesses' and landlords' - buy out first", each with a count; the big city's city list alone fills
  the box), and WHAT IT WOULD DO ("Pick a building on the left").
- **Wrong:** Construction splits the city's own from the businesses' on all three tabs; Build's cards
  should too.
- The list is a 530 px scroll box over 400 px of empty page, while the right pane stays empty until you
  pick.
- **For the map:** pick the building on the map, and its card says what pulling it down would do. The list
  becomes a filter in the panel.

## The area's five worst problems

1. **Build's category pages are walls of cards whose figures argue.** Each building is a 12-16 line card
   of small, five-colour prose, all open, 4 to a row, with ragged tops. Industry is about 75 cards over
   13 screens with no filter, sort, search or fold. Its "months of what it adds" and "adds the most per $"
   chips praise buildings the same card says would lose $576.9M a month. Each section's real verdict
   (supply against demand) is a grey, raw-number line at the far right. Shared "Investors" and lab
   paragraphs repeat on every card.
2. **The alerts are in six places and none is whole.** They are the header tiles (4 of 6 in red or
   orange), the "Needs you" chip (7, matching nothing in its drawer), the envelope (12, whose popover
   shows nine settled items, seven the same bank message, and none of the 3 open), the toasts (over
   "Turn automatic building on", and "hoarding" or "rich" get the red [!]), and the drawer (Roads 104%
   pinned, Schools 0% not). Nothing can be dismissed, and open items stay for decades.
3. **Nothing on Build tells the city's own works from the market's.** "you have N" counts every one
   standing: the Homes cards add up to the Overview's 38,545 and the Shops cards to 845, and the Diner's
   "you have 366 · 4 on site" is investors' "their order". Only Construction (Sites' tags, Timeline's
   colours, Demolish's two lists) splits them.
4. **Status is drawn and worded inconsistently.** The word "served" appears at 0%: all nine Education
   tiles read it in a city of 174,226 with no school. Streets at 104% is a red ring, and Police at 0.2x
   is full green with 71 officers short. The Overview says Safety "nothing needed" beside a 2-Police-Station
   suggestion. Rings mix %, x and "-", and Education draws its tiles another way. The tiles are also
   unlabelled sub-tabs that hide part of the page (Grid, Water, Elevated Highway). Market kinds have no
   status at all.
5. **The map is a land-office sub-page, not the ground.** It takes the whole window without the rail.
   A legend of about 55 swatches is open by default over 580 x 330 px, there are finance buttons in its
   toolbar, and shapes in the water have no key. No figure from Build or the needs reaches it.

Also broken, outside the five: the 0.6 px drift on 012 (from the brief); builders 30,253 pts on Build
against 24,797 on Sites; "4 sites, 15 buildings" against the table; Streets and Through traffic both
counting the same 6 highway sites; one sum shown as $, D$ and US$; "Investors built 5 Diner".

## The pictures worth keeping

- `playtest2400/012-build-industry-whole.png`: 13 screens of cards
- `playtest2400/012-build-industry.png`: one screen of it: card density, the Bakery's "adds the most per
  $" and 5.5 mo beside "would lose $576.9M a month", the grey verdict
- `playtest2400/002-city-inbox.png`: the alerts at once: chip 7, envelope 12, a popover of nine settled
  items with none of the open three, red header figures, toasts over the automatic-building button
- `playtest600/002-city-inbox.png`: the inbox at mid size: 3 open of a badge 6, one open since 1918,
  "Government wealth: rich" as a red alarm
- `playtest2400/004-build-overview-whole.png`: the landing page entire: Safety "nothing needed" beside the
  Police Station suggestion, "served, short" at 0%, mostly empty automatic-building panels, the count-only
  market row
- `playtest2400/010-build-homes-whole.png`: 14 cards whose "you have" add up to the Overview's 38,545,
  one investors' line repeated 14 times
- `playtest2400/008-build-education.png`: nine tiles all "served" at 0% in a city of 174,226
- `playtest2400/019-construction-sites.png`: the one place sites are tagged City or "their order"
- `playtest2400/003-map.png`: the big city as tinted blocks, the legend over the corner, the full-window
  land-office toolbar
- `slot1/009-build-safety.png`: rings that contradict their text, and a mostly empty category page
