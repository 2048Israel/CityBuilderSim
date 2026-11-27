# UX1 audit 1: the chrome, the map and Build

Pages 000 to 021, read from the picture tool's PNGs at 1920x1080: slot9 (month 6,681) first, then slot1
(month 115, 1,888 people), slot2 only where it differs. Six `-whole.png` read: slot1 004, slot9 005, 006, 010,
011 and 012.

Two caveats before the pages:
- **slot9 and slot2 look as if they were caught on the month they loaded, not in a steady state.** slot9's
  header says POPULATION 36,732 and "36,732 this month" (the brief says 50 million), GDP "annualised · first
  year", no sparklines, and every staffed building reads "generates 0.0 kW" or "serves 0", 0% filled. slot2
  (Ashbourne, 8,000 people) says POPULATION **0**, "no change this month". slot1 has sparklines and figures
  that hold together. So slot9's and slot2's *layouts* are evidence; their *figures* may not be the game's.
  This is either the tool or a load bug, and someone should check which before reading slot9 for numbers.
- The brief's stability line arrived without its list (it reads "...: STAB."), so the only stability red
  named here is the one on 012.

## City (the chrome: header, rail, alerts)

### 000-city (the city as it lands)
- **Shows:** the game opens on Build's Overview. Above it is the header (play button, date, speed, six
  figure tiles, "Needs you" chip, AAA chip, envelope). The rail is down the left, and a folded "UNDER
  CONSTRUCTION" side rail sits on the right.
- **Wrong:** the landing page is a build form, not the city. Its first sentence in slot9 contradicts the
  page: power and water are 0% served, yet "What would help most" says "nothing to suggest".
- Three red "[!]" toasts sit bottom right and cover the Overview's Restaurants tile. "Government wealth:
  rich" gets the same red alarm mark as "The bank failed".
- "AAA" is a green chip with no label, so it isn't clear it is the credit rating. The speed control
  ("< 1x >") is about 10 px text.
- The right side rail takes about 45 px on every page to say "0".
- The header tiles have sparklines in slot1 and none in slot9 or slot2.
- **For the map:** the six tiles and the date, play and speed controls are the top strip as they stand.
  The chip, the envelope and the toasts become the alert row. The construction side rail becomes a count
  in the strip, with the sites drawn on the map.

### 001-city-drawer
- **Shows:** the "City overview" drawer (Pin, close; Summary and Dashboard tabs). It has a cash, earned and
  population box, red rows pinned at the top (slot9: Power 0%, Water 0%; slot1: Roads 48% served), then 15
  folded rows from ECONOMY to BUILDINGS, each with one figure. The "Needs you" chip is outlined, so the chip
  opens this drawer.
- **Wrong:** the chip's count isn't listed anywhere on the drawer. slot9 says 6 but shows 2 pinned rows
  and 3 red values; slot1 says 2 and shows 1 pinned row.
- The drawer lays over the Build page's left column instead of pushing it.
- The rows are monospace figures in four colours with "open all / close all" links in tiny text.
- **For the map:** this drawer is already the narrow panel. Its 15 rows are the needs sidebar's circles,
  one each. The pinned red rows are alert marks. It fits in 480 px as it is.

### 002-city-inbox
- **Shows:** the envelope's popover, a list of messages. Open ones are red with "[!]"; settled ones are
  grey with a tick and "settled <month>".
- **Wrong:** the badge doesn't count what is open. slot9 shows 8 on the badge with 3 open; slot1 shows 6
  with 1 open.
- slot1's one open item ("Government wealth: broke", January 1901) is white with no [!] and has been open
  for 8 years.
- The same three items appear four times on one screen: in the popover, as toasts, behind the chip, and
  as red header figures.
- There is no dismiss control; settling is the only way out.
- **For the map:** this is the alert row. Each open item is one mark: left click opens it in the panel,
  right click dismisses it. Settled items go to a history behind a sub-menu.

## Map (Land office > Map, the rail's thumbnail)

### 003-map
- **Shows:** a "Land office > Map" toolbar (cash, "Convert D$", "From the vault", the layer chips Nations,
  Trade routes, Borders and Resources, a drag hint, "Select every offer", Fit, Back). Below it is the city
  on its terrain, with numbered plots for sale (1-24, hatched) and a legend.
- **Wrong:** the legend is open by default. It has about 70 swatches in 4 columns and covers about 520 x
  360 px of the bottom right.
- Finance buttons ("Convert D$", "From the vault") sit in a map toolbar.
- slot9: the legend's footnote says "1,525,000 buildings packed at the city's edge; 4,077,544 plots of road
  more than its streets carry". The big city draws as a few dozen blocks, so the map cannot show it.
- slot9: large dark-purple and grey rectangles west and east have no legend entry.
- slot1: a light-blue square sits in the sea with no obvious meaning.
- There is no hover card and no building info. Nothing from Build or the needs shows on the map.
- **For the map:** this is the ground. Fold the legend to the layer in use. Move the land-office controls
  into a Land panel. The big city needs a district view (tints by kind and density) instead of one square
  per building.

## Build

Common to the category pages, 005-018:
- **The page:** 15 tabs in one line, at about 11 px: Overview, 5 city kinds with blue dots, a divider, then
  9 market kinds with purple dots. The only key to the dots is a pair of dots at the top right.
- Under the tabs is the same 4-tile strip on every page (Materials, Per unit, Land free with a red "Buy the
  best" link, Builders). It is about 820 px wide, with 600 px empty beside it.
- **The card:** 12-16 lines of 9-11 px text in up to five colours (white, grey, green or red price, yellow
  builders' terms, red staff gap, purple "Investors" prose). It also has two bars, a "-1 0 +1" stepper and a
  big "Build · choose how many" button, which is two ways to choose one count.
- Rows are vertically centred, so the card tops are ragged. Cards are 4 to a row and end at about x=1475 of
  1690.
- Price colour is green when the treasury covers it and red when it doesn't; nothing says so.

### 004-build-overview
- **Shows:** a banner ("Your job is the city's own works..."), then five sections:
  - THE CITY'S JOB: 5 cards, each with a ring and status.
  - WHAT WOULD HELP MOST: slot1 has 3 suggestion cards with "Show >", "Build 1 on credit · $18k" and "Build
    all three".
  - AUTOMATIC BUILDING: 3 panels (spare margin, debt limit with bond yield, on/off).
  - THE MARKET BUILDS THESE: 9 tiles, each a count "standing".
- **Wrong:** each suggestion is about 8 lines of prose in four colours. The "Short 0.0968 km² of land"
  button looks disabled but is a link.
- Automatic building is 3 panels of about 325 px each, mostly empty. The debt-limit panel alone holds a
  slider, a two-way switch, a three-way yield picker, a text field and three paragraphs.
- The market tiles carry only a count: no ring, no status, unlike the city cards.
- slot1: automatic building and the market row fall below the fold.
- slot9: the card borders are red, but the cards say "nothing on site" and the page offers nothing.
- **For the map:** the five rings become the needs sidebar. The suggestions fit a 480 px panel, one per
  row, with "Show" lighting the site on the map. Automatic building is a settings sub-panel. The market
  counts become map tints or a strip line.

### 005-build-utilities
- **Shows:** 3 coverage tiles (Power, Power grid, Water), then POWER, 8 plant cards in 2 rows (slot1: 4).
- **Wrong:** the tiles are sub-tabs: Power is outlined pink and only Power's cards show. Nothing says
  Grid or Water open their own lists.
- The rings mix units: 247%, 334%, "8x".
- slot9: every card reads "generates 0.0 kW" (the load caveat). Tops are ragged (the Wind Farm card is 20
  px higher than its row).
- "From abroad, December 1980 / Licence: none now. The patent ended..." takes two lines on cards that
  already have 14.
- **For the map:** a 480 px panel holds the 3 circles and one column of short cards. Plants could be
  marked on the map ("you have 338").

### 006-build-infrastructure
- **Shows:** tiles Streets, Through traffic and Transit. Below are STREETS (Gravel Road with a "Pave to a
  Paved Road" block, and Paved Road), PORTS (5 terminals), RESERVE (1) and BRIDGES (3). The page is about
  2.5 screens.
- **Wrong:** the tiles act as sub-tabs for the first section only. Ports, Reserve and Bridges have no tile
  and always show.
- Elevated Highway (104,582 in slot9's Demolish list) isn't on the page unless Through traffic is chosen.
- The Paved Road card floats 85 px below Gravel Road's top, leaving a hole.
- slot1: Through traffic shows a 0% grey ring beside "served, enough".
- PORTS starts at the fold.
- **For the map:** roads, ports and bridges belong on the map: a traffic tint on streets, a card on a
  berth. The panel keeps the 3 circles and a short list.

### 007-build-healthcare
- **Shows:** 4 tiles (General care, Childcare, Senior care, Death care), then GENERAL CARE, 4 cards.
- **Wrong:** the lower 30% of the window is empty.
- slot9: 25,089 Walk-in Clinics "serve 0" against "short by 35,532" (the load caveat).
- The scarcity line ("diploma holders are the scarcest: 0% of their posts filled · why >") is grey, at the
  far right, 1,200 px from the cards it explains.
- **For the map:** a panel holds it whole. Coverage could be a district tint on the map (people without
  care).

### 008-build-education
- **Shows:** 9 tiles (Elementary to Institute of technology), then ELEMENTARY, 1 card.
- **Wrong:** the tiles are drawn a second way here: narrow, with the ring on top. Every other kind uses
  wide tiles with the ring on the left.
- Nine sub-tabs lead to one card each.
- The higher-ed rings read "-" in blue (slot9) or 0% grey (slot1), with no way to tell "none needed" from
  "none built".
- The page is two-thirds empty.
- **For the map:** the 9 tiles become one stacked list in the panel (a circle and a line each). Picking a
  row shows its one card.

### 009-build-safety
- **Shows:** 2 wide tiles (Police, Cells), then POLICE, 2 cards.
- **Wrong:** the ring contradicts its own text:
  - slot9: "0.0x · 128 officers short of full cover" in a full green ring.
  - slot1: "1.1x · 2 officers short" in a green ring.
  - slot1: Cells is a 0% grey ring over "a cell for everyone caught".
- slot1: "posts the city can't fill, per 10,000 officers: 24,075" is a figure no player can read.
- The page is 60% empty.
- **For the map:** panel. Police cover could be a district tint.

### 010-build-homes
- **Shows:** "Investors build these on their own...", then HOMES, 14 cards in 4 rows (about 2 screens).
- **Wrong:** **broken:** "a payment of $InfinityQ a month" on the Large high-rise, Very tall tower and
  Large-home luxury tower cards (slot9 whole).
- The same "Investors holding: housing ahead of jobs (4,662,906 now, 0 coming)" line is on all 14 cards.
  In slot1, a 7-line "Investors declined Terrace..." paragraph is repeated on most cards.
- The page's verdict ("studios: 436 households without a door · family homes: 30 to spare") is grey, far
  right.
- There are no coverage tiles; the city kinds have them.
- **For the map:** the verdict and a "what investors are doing" line go at the top of the panel, said
  once. The 14 types need a wide comparison table (residents, cost and land per resident). Homes are a
  density tint on the map.

### 011-build-shops
- **Shows:** GROCERIES (2 cards), FILLING STATIONS (1), THE BANK'S BRANCHES (Commercial Bank, 1).
- **Wrong:** the bank's branches hide under Shops.
- Most rows are 1 card wide with two-thirds of the width empty.
- "the stations sell 1,182,818,679" is a raw number where the cards print "350k".
- **For the map:** panel. Shops could be a coverage tint (households far from a grocer).

### 012-build-industry
- **Shows:** about 74 cards in about 26 rows and some 20 sections, from FOOD MILLS and FOOD PROCESSING
  down. The page is 12,186 px, about 12 screens.
- **Wrong:** **broken (stability):** a month moves the scroller 0.6 px. The place is held as a fraction of
  the run, not a pixel, so a page that grows above the view shifts it.
- Why it is "a mess, too many buildings to make a selection":
  1. Every card is open at full size with no filter, sort, search or fold. A type with 11,713 standing and
     a type with none look alike.
  2. The one verdict per section ("the city used 56,147,531 kg of bread; plants here make 1,837,000") is
     grey, at the far right, in raw numbers. Nothing ranks which chain is short.
  3. The comparison figure, "its price, in months of what it adds", reads "18792.1 mo" or "257989.3 mo"
     (1,500 to 21,000 years), with no separator and no good or bad mark.
  4. The best-buy chips ("adds the most per $") are the only comparison, and they hold only within a
     section.
  5. Each card's "Investors fitted the electric motor drive to a Snack & Oils Plant..." prose is often the
     same on every card of a section (also "a Assembly Plant" in 016).
- **City's own vs the market's: the cards do not tell them apart.**
  - "you have N" counts every one standing, investors' included: slot1's Bakery says "you have 3", and
    Demolish lists those 3 under "Businesses' and landlords'".
  - Only the purple tab dot, the line at the top and the per-card "Investors..." line (whether investors
    would build more) say the market is involved.
- **For the map:** this needs a wide view: a table with one row per type and sortable columns (makes,
  price, months to repay, land, staff gap, standing split city and investors), grouped by chain with the
  supply-and-demand line as each group's header. The panel shows the chosen row's card. Works go on the map
  by chain.

### 013-build-offices
- **Shows:** OFFICES, 7 cards: 3 offices and 4 research labs.
- **Wrong:** the 6-line paragraph "A lab is the investors' building for a prize..." is repeated word for
  word on each lab card.
- Payback reads "8772.2 mo".
- **For the map:** the lab paragraph goes once in the panel header. Otherwise as 012.

### 014-build-farms
- **Shows:** FARMS, 4 cards.
- **Wrong:** "you have 83" plus "you have 1" equals the Overview's "Farms 84 standing", which confirms
  "you have" means all standing.
- slot9 says "the city used 0 t of crops" (the load caveat).
- Half the window is empty.
- **For the map:** panel. Farmland is a natural map tint.

### 015-build-freight
- **Shows:** RAIL (4 cards) and HAULAGE (Truck Depot).
- **Wrong:** the top line is worded unlike every other market page ("Freight's investors build these. You
  can build them too.").
- The Truck Depot card says "Freight would not build one" where the others say "Investors".
- "the city trades 31,638,106 t a month" is a raw number.
- **For the map:** rail lines and depots belong on the map (the trade-routes layer). The panel lists
  them.

### 016-build-vehicles
- **Shows:** VEHICLES, 5 cards.
- **Wrong:** "Investors fitted the electric motor drive to a Assembly Plant..." (bad grammar) is repeated
  on all 5 cards.
- Payback reads "257989.3 mo".
- slot9 says "the city used 0 cars; plants here make 1,444,000".
- **For the map:** panel, or rows in 012's table.

### 017-build-luxury-shops
- **Shows:** LUXURY SHOPS, 2 cards (Boutique, Department Store).
- **Wrong:** a whole tab for 2 cards; the page is 70% empty.
- "0 customers came; the counters serve 5,190,234" is a raw number.
- **For the map:** fold into Shops as a section in the panel.

### 018-build-restaurants
- **Shows:** RESTAURANTS, 2 cards (Diner, Restaurant).
- **Wrong:** like 017: a tab for 2 cards, 70% empty, and "the kitchens serve 1,458,081,000" is raw.
- 106,161 plus 615 equals the Overview's 106,776, the same "you have" meaning again.
- **For the map:** fold into Shops (food) in the panel.

## Construction

### 019-construction-sites
- **Shows:** the Sites, Timeline and Demolish tabs and "< Build". THE BUILDERS strip has 6 figures and a
  queue bar from 0 to 33 months. Below is a SITES table header.
- **Wrong:** all three slots have nothing on site, so the table was never seen with a row; the tool needs a
  fixture with sites.
- The heading is "CONSTRUCTION" in capitals where Build uses "Build", and the page is full width (x=107)
  where Build is inset (x=243).
- The rail still lights "Build".
- slot9/slot2: "Builders 0 pts" next to "SOLD ABROAD: 9,875,859 spare pts last month".
- 60% of the window is empty.
- **For the map:** sites go on the map as markers with a progress ring. The builders strip goes in a
  panel. The table fits 480 px as building, progress and time left.

### 020-construction-timeline
- **Shows:** the same builders strip, then WHEN EACH FINISHES with a key (the city, investors and
  landlords): "Nothing on site." in all three slots.
- **Wrong:** the builders strip is repeated from Sites, and the timeline itself was never seen. Almost the
  whole page is empty.
- **For the map:** a wide view (a Gantt chart) when it has rows; otherwise one line in the sites panel.

### 021-construction-demolish
- **Shows:** WHAT TO PULL DOWN (a search box and a list split into "The city's buildings" and
  "Businesses' and landlords' - buy out first", each with a count), and WHAT IT WOULD DO ("Pick a building
  on the left").
- **Wrong:** this is the only page that splits the city's own from the businesses'; Build's cards should
  too.
- The list is a 450 px scroll box over 350 px of empty page, while the right pane stays empty until you
  pick.
- **For the map:** pick the building on the map, and its card says what pulling it down would do. The list
  becomes a filter in the panel.

## The area's five worst problems

1. **Build's category pages are walls of cards.** Each building is a 12-16 line card of small,
   five-colour prose, all open, 4 to a row, with ragged tops. Industry is about 74 cards over 12 screens
   with no filter, sort, search or fold. Each section's real verdict (supply against demand) is a grey,
   raw-number line at the far right, and the comparison figure prints as "257989.3 mo". Shared "Investors"
   and lab paragraphs repeat on every card.
2. **The alerts are in five places and none is whole.** They are the "Needs you" chip (count not listed),
   the envelope (badge counts settled items: 8 against 3 open), the popover, the toasts (they cover the
   Restaurants tile, and good news gets the red [!]) and the drawer's pinned red rows. Nothing can be
   dismissed.
3. **Nothing on Build tells the city's own works from the market's.** "you have N" counts every one
   standing (farms 83+1=84, restaurants 106,776, slot1's 3 investor bakeries). Only Demolish splits them.
4. **Status is drawn inconsistently.** Rings in %, x and "-" show full green on "128 officers short" and
   grey 0% on "served, enough". Education draws its tiles another way. The tiles are also unlabelled
   sub-tabs that hide part of the page (Grid, Water, Elevated Highway). Market kinds have no status at all.
5. **The map is a land-office sub-page, not the ground.** A legend of about 70 swatches is open by default,
   there are finance buttons in its toolbar, the big city is "1,525,000 buildings packed at the city's
   edge" drawn as a few dozen blocks, and no figure from Build or the needs reaches it.

Also broken, outside the five: "$InfinityQ" in three Homes cards; slot9/slot2 caught on the month they
loaded (population 36,732 and 0); the 0.6 px drift on 012; "a Assembly Plant".

## The pictures worth keeping

- `slot9/012-build-industry-whole.png`: 12 screens of cards
- `slot9/012-build-industry.png`: one screen of it: card density, "18792.1 mo", repeated prose, the grey
  verdict
- `slot9/002-city-inbox.png`: the alerts four ways at once (chip, badge 8 with 3 open, popover, toasts over
  a tile)
- `slot9/003-map.png`: the big city as blocks, the legend over the corner, the land-office toolbar
- `slot1/004-build-overview-whole.png`: the landing page entire: suggestions, empty automatic-building
  panels, count-only market row
- `slot1/009-build-safety.png`: rings that contradict their text, and a mostly empty category page
