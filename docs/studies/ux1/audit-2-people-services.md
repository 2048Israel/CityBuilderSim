# UX1 audit 2: the land office, People, Services and Infrastructure

Pages 022 to 046 at 1920x1080. These are read from the pictures only, not the source. Cities: `playtest2400` (the big
city, month 2,401, 174,226 people), `slot1` (the young town, month 115, 1,888 people), and `playtest600`, checked
for Education and Utilities. Places on a page are given as window pixels (x, y).

Caveat: the playtest city has built no school of any kind at months 600 and 2,400, so the crowded case of
Education is its empty case. The young town is the only one whose schools are filled.

## Land office

### Land office (022)
- **Shows:** buying ground. A parcel map with numbered edge plots, the offers on each side (North/East/South/West
  tabs), "Buy the best 5" with a free-ground bar, and four cards on what the ground is worth.
- **Wrong:** the page draws its own 600x400 map, with an Expand button, inside a page that covers the real map.
- Big city: five of the six North offers are MOSTLY SEA with "–" per dry km². East and South say "no dry ground"
  but keep their tabs. "Buy the best 5" (D$789.3B) buys four sea plots, and its bar squeezes those four into a
  sliver of marks at the right end (x 1672-1680). Only the "1" segment can be read.
- Empty space: right of the offer table (x 1520-1700, y 270-560). "Ore and oil" (0 deposits) and "Waiting on
  ground" (a tick) are cards with almost nothing in them.
- Controls unlike their kind: the "-1 5 +1" stepper is bare text. In the young town, filled "Buy" and outlined
  "Fund" buttons share one column, and the prices that need Fund are red without saying why.
- Out of place: the "Convert D$ / From the vault" currency buttons and a tiny "‹ Build" back link at top right.
- **For the map:** the parcels go on the city map itself: hatched and numbered, with a card on click (size,
  dry/fresh/sea, price, Buy). The offer list, the best-5 stepper and the free-ground bar fit a 480 px panel, and
  so do the four worth cards stacked. The margin arithmetic needs a wide statement behind "details".

## People

### People (023)
- **Shows:** the population. Five tiles, the age pyramid, this month's waterfall (born, died, moved), why people
  come (a formula), the care rings, homes and the households treemap. Below the fold: outside the families, the
  pool, an orphans warning, then Work and the skill ladder (the whole page is about 2,000 px).
- **Wrong:** hard to find. The red box "NOBODY FEEDS THE ORPHANS" is about 1,470 px down, under four sections.
  Work ("12,376 adults with nowhere to work") comes last.
- In the strip, "Going short 87.9%" (106,894 can't afford a basket) is the city's worst figure, yet it gets the
  same small tile as Spare homes. Living here, a head count, is drawn red.
- The care rings repeat Services › Health's four. Here a click builds ("Build's rings: click one to build for
  it"); there it doesn't. The same drawing has two behaviours.
- The households treemap clips its labels: "+ baby, ...", "large ...", "+ child, ...", "single ...", "five ..."
  (x 1290-1690, y 1057).
- Walls of small text: the formula chips under "Why people come" are about 9 px, and "The pool this month" is
  three 9 px lines.
- Repeats: three stacked "details ▸" links at the foot of Work, and two "Household money ›" buttons.
- The young town fills the page well. Nothing is empty.
- **For the map:** the strip, the pyramid, the waterfall, the care rings and the outside-the-families cards fit
  a 480 px panel. The treemap, the skill ladder and the pool need a wide view. Homes wanted against homes built,
  and doubled-up households, are a district tint. Orphans and no-home are alert marks.

### Household money (024)
- **Shows:** what each kind of household earns and keeps. Four tiles, a "hungry" paragraph, a grid of household
  kind by pay tier, then the retired and outside rows. Below the fold: who goes short, the city's month
  (waterfall), what they have put by, and household debt.
- **Wrong:** a wall of text. The hungry paragraph is 8 lines of 10 px type naming 25 household kinds with
  counts (y 250-360).
- The grid cells are about 8 px monospace (x 420-965). The Senior professional column is all "–" in the big
  city, and three of the six columns say "no earners" in the young town.
- The right half (x 975-1700, y 430-1080) is empty apart from "Click a cell to open its books".
- The page reads as contradicting itself. The top says "All 84 kinds of working household cover their month"
  and the grid is all green. 1,700 px down, every tier from Skilled to Elite "got .10 of a basket". Money and
  food disagree, and the page never joins them.
- The retired rows start at the fold, all red (-$2.8M to -$3.4M each; "a pension of $0 a head").
- Who goes short: ten identical rows ("got .10 of a basket · no food voucher").
- The city's month: 7 of its 17 bars are $0.
- Household debt: three rows of filter tabs (tier, stage, month/survey) for three figures.
- The back button "‹ People" sits left of the active tab. The land office puts its "‹ Build" far right.
- **For the map:** the strip and "who goes short" fit a panel. The grid and the city's month are wide tables.
  The hungry list is an alert, not a paragraph.

## Services

Every Services page shares one frame: a breadcrumb, four system tabs each with a status dot, a 4-tile strip, a
row of page tabs, and "Build them on Build ›" at top right. That is three layers of navigation before any
content, which starts at y 320. The strip is 775 px wide in a 1,440 px column, so its right half is empty. Nine
of the 18 pages end above y 690, and five more above y 800. The stability harness, run on another town: all 18
pages breathe (the height flips 0.8 px on every redraw with nothing changed, and the empty toast corner moves
with it).

### Health overview (025)
- **Shows:** the sick rate and where it comes from (a stacked bar), four care cards (each with a ring and a
  Build button), and what people die of (a table of 1900 diseases).
- **Wrong:** the disease table (y 770-1035) lists seven causes from 1900, all 0 in 2100. It is monospace, its
  right 40% is empty, and the window cuts its last line.
- Childcare's ring at 1% ("×38.1 infant deaths") has the same weight as General care's 100%. So does Senior
  care at 1%.
- The cause of short care is a 9 px grey caption at top right (x 1475-1695, y 470): "nurses are the scarcest:
  84%", and in the young town "doctors are the scarcest: 0% of their posts filled".
- A full cemetery reads as fine: the "Ground left: full" tile is neutral and the Death care card is teal.
- The sick-rate legend greys out five "none" entries beside the two that count.
- **For the map:** the strip and the four cards fit a 480 px panel, stacked. The disease table is wide, or a
  details sheet. Care buildings get a card on the map (places, staffed).

### General care (026)
- **Shows:** the sick by months ill (a 12-bar histogram), what care cures (one dot on a line), who gets a
  doctor (four bars), and the fee.
- **Wrong:** the histogram spans 1,170 px for two visible bars and ten empty slots. "What care cures" is a
  700x175 card for one dot. The bottom 330 px are empty.
- Young town: the overview card says "served, short" (99%), but this page says "enough places for everyone".
- **For the map:** all of it fits a panel.

### Childcare (027)
- **Shows:** what childcare buys (three slider rows: infant deaths, how often babies fall ill, births), who it
  reaches (four bars), and the fee.
- **Wrong:** each slider is a 900 px line carrying one dot (x 500-1415). Lower is better on two rows and higher
  on the third, and no colour says which.
- Big city: this is the least-served system, yet "short by 7,370 places" is plain 9 px text and the page has no
  Build button of its own.
- Everything below y 750 is empty, and so is the right half below y 540.
- **For the map:** a panel, with the sliders as three short rows.

### Senior care (028)
- **Shows:** the same layout as Childcare, with four slider rows.
- **Wrong:** the same faults, and the card's icon reads as a "1".
- Big city: 84 places for 8,140 seniors at a fee of $238,372 a month. The retired get a pension of $0, but that
  is on Household money. The two figures that explain each other sit on different tabs.
- **For the map:** a panel.

### Death care (029)
- **Shows:** the ground (plots left), this month's dead (buried or cremated), crematoria use, and the fees.
- **Wrong:** three bars on a page, with 45% of the window empty. The big city's full cemetery gets the same
  teal bar as a healthy one.
- Young town: the crematoria bar is an empty full-width track, and "cremated none / lying unburied none" are
  greyed out.
- The two cards share one icon.
- **For the map:** a card on the cemetery (plots left, months left at this rate).

### Health books (030)
- **Shows:** what care costs and what comes back (two stacked bars), the net cost, and who was priced out.
- **Wrong:** one 85 px chart and two lines leave 55% of the window empty. The books themselves are behind
  "details ▸ every line and table".
- The same figure is written two ways: "nobody priced out of care" in the big city, "0 priced out of care" in
  the young town.
- The net cost repeats The bill tile above it.
- **For the map:** the foot of the panel. The statement needs a wide view.

### Education overview (031)
- **Shows:** the basic ladder (three stages and diplomas), adult study (college, university) and the licensed
  professions (four schools), each with a ring.
- **Wrong:** the big city (and the mid one) has no school of any kind: 0 of 9,274 children taught, a bill of $0.
  The only alarms are a red dot on the tab and a red "0%" in the strip. Nothing says the city never built one.
- "Share served" is drawn three ways: a dark ring with "0%", a blue ring with "–" (Institute of technology, and
  University in the young town), and half-gauges on the sub-pages.
- In the young town, "100%" touches the inside of its ring.
- The "not built: N would come, M posts" chips wrap on some cards and not others, and "Institute of technology"
  wraps to two lines. Card heights differ along a row.
- The right third (x 1190-1700) is empty, and its captions are 9 px grey.
- **For the map:** the three rows of cards fit a 480 px panel as a list. Schools are buildings with a card.

### Basic ladder (032)
- **Shows:** each stage's seats, staff, pupils in class and pupils to teach (bars), the diplomas, and the seat
  cost and subsidy.
- **Wrong:** "held up by elementary" appears in the strip and at top right even when all three stages are 100%
  served (young town).
- Big city: three empty 1,180 px tracks. The seat price ("a seat $143,023 a month · the household $57,209"),
  likely why no school exists, is a 10 px line under the card.
- The lower 45% is empty.
- **For the map:** a panel.

### College (033)
- **Shows:** the not-built state: how many would come, wage-return and can-afford half-gauges, "Build for it",
  and the seat cost.
- **Wrong:** a 1,440 px card whose content fills the left 600 px, and the rest of the window is empty. The
  "not built" chip at top right repeats the card's number.
- **For the map:** a panel. College and University share one template and could share one page.

### University (034)
- **Shows:** the same template as College.
- **Wrong:** young town: wage return 0% and "0 would come", yet the Build button is as prominent as anywhere.
  It sits beside the sentence saying such a school "teaches nobody".
- **For the map:** merge with College in one panel.

### Professions (035)
- **Shows:** four not-built schools in a 2x2 grid, each with gauges, a Build button and the seat cost.
- **Wrong:** the same two-line explanation and the same seat-cost line are printed four times, and each title
  repeats its "not built" chip.
- Young town: "the city has 0 of them" (university holders) and 0 would come, yet there are four Build buttons.
- The page fills the window with repetition, not information.
- **For the map:** a list in a panel, with one shared footnote.

### Education books (036)
- **Shows:** the same layout as Health books: one two-bar chart and the net cost.
- **Wrong:** 55% of the window is empty. Big city: two empty tracks and $0 everywhere.
- **For the map:** the foot of the panel.

### Utilities overview (037)
- **Shows:** power served (a bar with the city's ask, business load against the city's own), and how the power
  ran (the dispatch: asked, generated, delivered, curtailed, unserved, then six load blocks). Water is below the
  fold; the scroller's thumb covers a third of its track.
- **Wrong:** the big city's page contradicts itself. The card says "362% served, enough · spare 389 MW"; the
  dispatch below says "103,233 MWh asked, 7,200 generated, 96,033 MWh unserved" in red. The mid city agrees
  with itself (224%, 0 unserved).
- The same figure is written two ways: 362% and 312% in the big city, 5× and 8× in the young town.
- Roads is in the Utilities strip, drawn red. It turns the Utilities dot red in both towns while power and water
  are fine. Roads belongs to Infrastructure.
- The dispatch bars span 1,410 px, and the window cuts the six rows at y 1020.
- **For the map:** power and water status fit a panel. The dispatch needs a wide view. Plants get a card.

### Utilities books (038)
- **Shows:** power and water, sales against costs, the net for each, both together, and the roads' upkeep.
- **Wrong:** "$17.4B both together" and "-$44.3B the roads' upkeep" float under the cards with no card of their
  own. The sum, -$26.9B, is never shown, while the strip says "They earn $17.4B/mo".
- A row label changes between towns ("water: costs" in one, "water: wages" in the other).
- Young town: power has "$0" of sales against "1.48 MW billed".
- The lower half is empty.
- **For the map:** the foot of the panel. The roads' upkeep moves to Infrastructure.

### Safety overview (039)
- **Shows:** crime a month and its causes (a stacked bar), then three cards: police, cells, and what crime did.
- **Wrong:** crime is drawn green in both towns, including the young town's 6,415 a year per 100,000 (1.15×
  Canada's). "1 caught, not held" is green, and so is the Safety dot.
- Young town: the Cells card is a full-width empty track.
- The legend greys out "none" entries beside the live ones.
- **For the map:** a panel. Police stations get a card. Crime would make a district tint if the model ever
  places it; today it is citywide.

### Police (040)
- **Shows:** crime with no police against crime with these police (two bars), the officers (on shift against
  full cover), and a one-more-station forecast.
- **Wrong:** the page's best line is 10 px plain text under the card, apart from the Build button above it:
  "One more police station: coverage 89% → 100%, crimes a month 196.8 → 61.3".
- Each card holds two bars across 1,150 px, and 40% of the window is empty.
- **For the map:** a panel. The forecast belongs on the Build button and on the station's card.

### Prisons (041)
- **Shows:** cells against the number needed, prisoners by months served (six columns), this month's flows, and
  the prisoners' ledger.
- **Wrong:** fractions of people ("15.7 caught", "0.6 caught and not held").
- Big city: 600 cells for 94 needed is drawn as a nearly full bar, so overbuilding reads as tight.
- Young town: every figure is zero. There is an empty bar track and an empty 1,440x115 chart.
- **For the map:** a panel, or the prison's card.

### Safety books (042)
- **Shows:** what police and prisons cost (two bars), the net, and what crime took.
- **Wrong:** one chart, with 55% of the window empty.
- Young town: police cost "$0" and "$0 an officer on shift a year" while 4 officers are on shift. Either the
  page hides who pays them or the figure is wrong.
- **For the map:** the foot of the panel.

## Infrastructure

Infrastructure uses a different frame from Services: no breadcrumb, a 5-tile strip 965 px wide, tabs with icons,
and a pink "Build · Infrastructure ›" button beside a plain blue link "The road over the years ›". Freight and
Ports add a third row of tabs (Division: All / Haulage / Rail / Ports). The stability harness: these pages
breathe too.

### Roads (043)
- **Shows:** whether the road is served and how traffic flows (a curve), trips turned into traffic (five
  stacked rows), what is on the road (commuters, goods, bulk), and a second 4-tile strip.
- **Wrong:** the consequence that matters is a 10 px grey line under the headline: "every shop, plant and site
  works at 43% of its output" in the young town (95% in the big city).
- Two tile strips on one page, the second half-width and pushed left. "Cars 56%" in the top strip repeats as
  "Cars per household 56%" in the bottom one.
- Young town: three of the five trip rows are words ("no transit yet", "nobody owns a car", "no rail") across
  770 px tracks.
- Road works in progress ("6 on site · ~6 mo") are a 9 px pink line.
- **For the map:** this is the map's own job: the road network tinted by flow. The headline, the curve and the
  three cards fit a panel. The trip rows need a wide view.

### Transit (044)
- **Shows:** who rides, how commuters become riders (the ceilings, riders by reason), what it costs the city (a
  statement), and the fare (a slider with − and +) with what a change would do.
- **Wrong:** a raw multiplier: "×30698.129 the prices people expect" (fare line, y 758).
- The fare slider with − and + is the only control of its kind in this area.
- The statement's figures sit 560 px from their labels.
- Big city: the cards run past the window, ending at y 1035.
- Young town: there is no transit, yet the fare slider and "what it would do" (all 0) are drawn in full.
- One label flips with the sign: "Net cost to the city" in one town, "Net profit to the city" in the other.
- **For the map:** who rides and the fare fit a panel. The ceilings and riders by reason need a wide view.
  Lines and stops belong on the map.

### Freight (045)
- **Shows:** what moving each good adds to its price: 30-plus goods, a bar each, and delivered % and by-lorry %.
- **Wrong:** three rows of tabs. Division's "Ports" repeats the Ports tab.
- The heading "WHAT MOVING IT ADDS TO EACH GOOD'S PRICE" runs into "BULK · rail-eligible" with no gap (x 520).
- The list runs past the window (the scroller's thumb is a quarter of its track). The left column is empty under
  its four-line legend (x 260-500, y 460-1030).
- Most rows repeat one number twice ("65% delivered · 65% by lorry"). The railway's purple never appears,
  because there is no rail.
- The 1909 town gets the same 30-plus rows, jet fuel and lubricants included.
- **For the map:** a wide table. A panel would hold only the summary for each division.

### Ports (046)
- **Shows:** the ports' month (billed, dues, tonnes, ships), berths by kind with a use bar, a table of handling
  and dues, and the ships that called.
- **Wrong:** the Ports tab opens Freight's Division › Ports, so there are two routes to one page. The top
  button reverts to "Build · Infrastructure" while the page's own "Berths on Build ›" sits at the bottom.
- The headline reads "$12.0B handling billed $10.7B", with "dues to the city" on the next line, so the two
  labels run together.
- The handling table is mostly "–" (all of it in the young town). The ships card is 700 px wide for one row.
- Big city: the berths are full (334,714 t of 333,333, with 166,667 t more on site), drawn in calm blue.
- **For the map:** a panel. Berths and ships go on the map; "The ships on the map ›" already points there.

## The area's five worst problems

1. **The alarms are buried, not raised.** The orphans box is about 1,470 px down People. "87.9% ate less than a
   basket" is near the bottom of Household money. A 174,226-person city has no school at all, and only a tab
   dot says so. Childcare and senior care at 1% look the same as care at 100%. "Every shop works at 43% of its
   output" is a grey caption. Each of these is an alert mark, not body text.
2. **Half the pages are nearly empty, and the real tables hide behind "details ▸".** Nine of Services' 18 pages
   end above y 690. Each books page is one bar chart and a link. In the young town, prisons, ports, transit and
   the not-built schools draw full-size charts of zeros.
3. **The same thing is drawn many ways.** Header strips come in four widths and two alignments. The build action
   has eight labels ("Build them on Build", "Build · Infrastructure", "Build · Freight", "Build for it", "Build
   transit", "Build homes", "Berths on Build", "‹ Build"). "Share served" has three drawings. Power is written
   as 362% in one town and 5× in another. The Roads figure drives the Utilities dot, and the roads' upkeep sits
   in Utilities' books.
4. **Pages contradict themselves.** Utilities at month 2,400: "362% served, enough" over "96,033 MWh unserved".
   Household money: "every working household covers its month" against "every tier got .10 of a basket".
   "Held up by elementary" appears at 100% served. Safety's bill is $0 with 4 officers on shift. Full cemeteries
   and full berths are drawn in calm colours.
5. **Walls of tiny text and tables wider than the eye.** The hungry paragraph is 8 lines of 10 px type.
   Household money's grid has 8 px cells. Freight has 30-plus rows of 780 px bars, cut off by the window, with
   its heading running into its subheading. Health lists 1900's diseases at 0 in 2100. The households treemap
   clips its labels. Transit prints "×30698.129".

## Pictures worth keeping

- `playtest2400/023-people-whole.png`: the orphans alarm and Work buried down a 2,000 px page.
- `playtest2400/024-people-household.png`: the wall of text, the 8 px grid and the empty right half.
- `playtest2400/031-services-education-overview.png`: a big city with no school, flagged only by a dot, and
  three drawings of "served".
- `playtest2400/037-services-utilities-overview.png`: "362% served, enough" over "96,033 MWh unserved".
- `playtest2400/030-services-health-books.png`: a page that is one chart and a link.
- `playtest2400/045-infrastructure-freight.png`: three rows of tabs, the heading collision and a list cut off
  by the window.
