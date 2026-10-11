# Spec: Rankings, your city against the world (design study, 2026-10-08)

*Samuel: the player compares their city to real nations and cities on two leaderboards, today (2025) and projected to the city's year; three currencies on a toggle (domestic, the game's USD, real USD); inflation, exchange rates and PPP handled; "a ton" of measures; made-up figures allowed behind a tiny (i); the player's other saved cities on the board too, compared "per year or static year".*

No code was read or changed. The mockups are the canvas **"Rankings Study"** (five boards; board 1 is interactive, so every toggle can be pressed). Every figure in the mockups is illustrative. Nine real 2025 values were checked against the IMF World Economic Outlook (Oct 2025): GDP per person for the US, Germany, the UK, Canada, France, Italy, Russia, Türkiye and China. The rest are estimates from memory, and every city figure is invented to look right. The arithmetic is `claude/leaderboards-model.py` (§11), and every figure on the boards came out of it.

---

## 1. Summary for Samuel

1. **One pivot, then display.** Every money figure is turned into one unit first, real US dollars at 2025 prices (`US$₂₅`). The board ranks in that unit and only then shows the result in the money the player picked. So **the Money toggle can never reorder the board.** Only three things can: Peers as of, Convert at (PPP), and Per year for saved cities.
2. **The anchor: one founding game dollar is one 2025 dollar.** The game's prices were struck from 2025-26 real figures (the rebalance), even though `CityCalendar` starts in January 2000. This one decision makes "real USD" well defined. It is decision **D1**.
3. **The three moneys inflate differently, and that is the whole point of having three.**
   - **A$** (the city's own money) inflates at the city's rate, about 2% a year under the autopilot since 0.7.42.
   - **The game's US$** wanders and does not trend. `WorldEconomy`'s level is pulled back to a flat trend, so at the default 1% setting it settles near 1.14 times founding for good (Jerus's old city: 1.137).
   - **Real US$** is held at 2025 prices.

   So `US$₂₅ = game US$ ÷ W(t)`, where W is that world's price level, 1.0 at founding.
4. **Two boards, as asked.**
   - **Today** is the real world in 2025.
   - **Projected** is the real world at the city's own year. Before 2025 that means actual history ("2012 · actual"). From 2025 to 2100 it is the published projections. Past 2100 no source reaches, so the game carries every place on by one stated rule.
5. **Per year vs static, read as nominal vs one year's prices** (D2). The real world has no game money of its own, so this toggle only moves your **saved cities**:
   - **per year** leaves each saved city in its own year's game dollars;
   - **static** brings it to the active city's prices.

   Because the game's dollar doesn't trend, the two differ by a few percent (Danzik: 125,840 against 121,151). That is honest, not a bug. The bigger difference lives in each city's own money (D$ in 2272 against A$ in 2050), which is exactly why the board never ranks own-moneys against each other.
6. **A third option worth having: same year.** It reads each saved city's history file at the active city's month. Every city starts in January 2000, so the same year is also the same age: "at your age, Danzik had 312,400 people and US$34,532 a head". It is a ghost race, cheap because every slot already keeps `slot-NN-history.json`. Decision **D3**.
7. **PPP for the city comes for free.** The game's parity rate (`fxParity`, the city's basket against the world's) *is* a PPP rate. A PPP figure is the domestic figure ÷ parity instead of ÷ the market rate.
8. **Provenance is three kinds, not one disclaimer.**
   - **● published** (a 2025 figure from a source)
   - **◐ published projection** (a source's own forecast, on its own assumptions)
   - **○ the game's estimate** (a fill, or a figure held or carried past a source's horizon)

   The tiny (i) by the title explains all three. Each row carries a small shape-coded mark, told apart by shape, not colour. Opening a row shows the source of every figure. Decision **D6** asks whether the per-row marks stay.
9. **70 measures in ten groups** (board 5, §6). 66 have published figures for nations. 13 also have them for city regions. Four have no comparable real figure (orphans, recorded crime, sick at any time, care coverage): only your own cities rank on those (D10).
10. **Totals rank a small city last for two centuries.** By GDP, Arden is last of the real rows until it is a metropolis. So the default measure is **GDP per person**, the default view is **around you** (the top three, then you and six either side), and a **Your size** filter keeps places within ten times your population: Monaco, Liechtenstein, Iceland, Reykjavik, Halifax. Microstates and city regions are the natural rivals of a city sim, and they are on the peer list on purpose.

## 2. The three moneys

| | what it is | how it inflates | where it is in the game |
|---|---|---|---|
| **A$** (the city's own, named at founding) | what every screen already shows | the city's own price index; about 2% a year under the autopilot since 0.7.42 (`Expectations`, Pe) | `game.getCurrency()`, `PriceIndex` |
| **US$ · game** | the dollar of this city's world: the vault, Trade, land prices | `WorldEconomy.priceLevel`: stationary around 1.15 at a 1% setting (the closed form in `the-world-a-city-is-founded-into.md`) | `WorldEconomy`, `ForeignAccounts` rate and parity |
| **US$ · real 2025** (`US$₂₅`) | today's dollar in the real world, held at 2025 prices | does not: it is a constant-price unit | the leaderboard's pivot, new |

**Why the anchor is 2025 and not 2000** (D1). If a founding game dollar were a 2000 dollar, every city would read about 1.9 times richer in real terms (US prices roughly doubled from 2000 to 2025), even though its wages and prices were copied from 2025. The calendar label and the price level disagree, and the price level is the one that was calibrated. The alternative is to move the calendar's epoch to 2025. That is a bigger change and Jerus's call; it would also remove the "historical" case of D11.

## 3. The model

**The pivot.** For a game city c at month t with a domestic money figure X:

```
game US$ (market) = X ÷ fx_c(t)            fx      = local per game US$ (ForeignAccounts rate)
game US$ (PPP)    = X ÷ parity_c(t)        parity  = fxParity, the city's basket against the world's
US$₂₅             = game US$ ÷ W_c(t)      W       = that world's US$ price level, 1.0 at founding
```

A real-world figure arrives already in `US$₂₅`: market-rate GDP in constant 2025 dollars, or PPP GDP in constant 2025 international dollars.

**Display.** The active city is 0, at year Y0:

```
Real US$                  : US$₂₅
Game US$, static          : US$₂₅ × W0(Y0)
A$, static                : US$₂₅ × W0(Y0) × rate0(Y0)            rate = fx, or parity under PPP
Game US$, per year        : a saved city i → X_i ÷ rate_i(t_i)    (its own year's game dollars)
A$, per year              : a saved city i → X_i ÷ rate_i(t_i) × rate0(Y0)
```

Everything else is the same in per year as in static.

**Identities the harness should assert** (the model asserts the first two):
- The active city's A$ figure never changes, under market or PPP. The toggles move everyone else into A$, never the city out of it.
- Static boards in A$, game US$ and real US$ have one order.
- Per year changes only rows of kind "saved", and only when Money is not real US$.
- Projected real-world rows grow only by real growth. They never take an inflation assumption, because in game money they inflate with the active city's world.

**Prices is locked** when Money is real US$ (2025 prices by definition) and on measures that are not money. The board greys the control and says why in its tooltip.

**A sentence under the controls** names exactly what is on screen: *"Real US dollars at 2025 prices, converted at market exchange rates. The real world as it was in 2025; Arden in July 2050."* This one line carries the combinations the toggles make, and it is the most important piece of the UI.

**What each toggle moves** (board 2):

| | the active city | the real world | saved cities |
|---|---|---|---|
| Peers as of | nothing | 2025, or the city's year | nothing |
| Money | the unit | the unit | the unit |
| Prices | nothing (already Y0) | nothing (no game money of its own) | per year: own year's dollars; static: Y0 prices |
| Convert at | market or parity | market or PPP figures | each through its own rate |

## 4. The two boards and their years

| the city's year | Today board | Projected board | marks |
|---|---|---|---|
| 2000–2024 | 2025 actual | that year's actual history ("2012 · actual") | ● |
| 2025 | 2025 actual | same | ● |
| 2026–2100 | 2025 actual | the projections at that year | ◐ where a source projects; ○ for fills |
| after 2100 | 2025 actual | the game carries each place on (rule below) | ○ for every real row |

**Sources and their horizons** (checked 2026-10-08):
- **People:** UN World Population Prospects 2024, medium variant. It covers every country to 2100. The world reaches about 9.66B in 2050 and peaks near 10.3B in the mid-2080s.
- **GDP per person:** the OECD Long-Term Baseline runs to 2060 for OECD and G20 economies. The SSP2 ("middle of the road") GDP paths run to 2100 for nearly every country. IMF WEO runs to 2030 only.
- **Other measures:** from these sources, or held at their last published value (○, "held at 2025") with a rule per measure in the data file.

**The rule after 2100** (D8):
- People stay at their 2100 count.
- Income per person grows at its 2090s pace, eased toward 1% a year over 50 years.
- Every other measure is held.

The banner on board 4 says this in two sentences.

**Cities.** Metro-level projections are paid products (Oxford Economics' Global Cities service covers the 1,000 largest cities) or stop around 2035 (UN urbanization prospects). So a city region is projected as **its nation's path times its 2025 share**: GDP and people grow with the nation's, and rates are held. Every projected city figure is therefore ○. Honest, and it keeps a city from drifting away from its nation.

## 5. Your saved cities

**Reading them.**
- `SaveHeader` already parses the save itself, so the rankings need a few more fields from each slot's save: the measures in §6 that the game has, fx, parity, W, the price index, the currency's code, and the month.
- The **same year** option reads the slot's history file at month Y0. If the city never reached Y0, the row shows "stopped in Feb 2031" and no figure.
- Slot 0 (the autosave) is left out. It is a copy of a numbered city.

**Each save is its own world.** Each has its own world inflation setting, its own W path and its own money name. The pivot is the only bridge between two saves. That is why per year is bridged on each world's game US$ and never on own-money figures.

**A currency reform** (÷10, ÷100) changes the units of a city's money and its history. A saved city's money is always read in the units of its last save, and the pivot divides it out, so a reform never moves a rank.

## 6. The 70 measures

Arrows: ↑ higher is better · ↓ lower is better · – neither, ranked largest first with no verdict colour. `$` follows the Money toggle. N and C mean published for nations and for city regions. "yours" means only your cities rank.

| group | measures |
|---|---|
| **Economy (11)** | GDP $↑ NC · GDP per person $↑ NC · real growth ↑ NC · 10-year growth ↑ N · household spending per person $↑ N · investment % GDP – · government spending % GDP – · exports % – · imports % – · trade balance % – · current account % – |
| **Work & pay (6)** | unemployment ↓ NC · taking part in work ↑ · average wage $↑ NC · minimum wage $– · minimum against average wage – · adults in college or university ↑ |
| **Prices & money (8)** | inflation – · policy rate – · 10-year yield ↓ · real interest rate – · currency against parity – · reserves in months of imports ↑ · M2 % GDP – · price level against the US – |
| **Public money (8)** | debt % GDP ↓ · debt in foreign money ↓ · budget balance ↑ · tax revenue % – · interest % of revenue ↓ · city fund % GDP ↑ · credit rating ↑ · pensions % GDP – |
| **Banks & markets (5)** | bank capital ratio ↑ · bad loans ↓ · credit to firms and households % – · stock market % – · household debt % income ↓ |
| **People (11)** | people – NC · growth – NC · births – · deaths ↓ · child deaths ↓ · life expectancy ↑ · aged 65+ – · old-age dependency – · net migration – · people per household – · orphans per 10,000 ↓ yours |
| **Housing (5)** | rent % income ↓ NC · homes per 1,000 ↑ · without a home per 10,000 ↓ · households sharing ↓ · home price to income ↓ NC |
| **Safety (4)** | homicides ↓ NC · prisoners – · police – · recorded crime ↓ yours |
| **Health & care (4)** | health spending % – · sick at any time ↓ yours · going hungry ↓ · care coverage ↑ yours |
| **Land, power & travel (8)** | area – NC · people per km² – NC · electricity per person – · renewable share ↑ · water per person – · cars per 1,000 – · commutes by transit ↑ NC · oil produced – |

**Where the game's side comes from.** Almost all of it is already a History series (217 lines since 0.7.37): GDP and its parts, M0 and M2, the bank's capital ratio, deaths by band, homicides, prisoners, police, the unemployed, the unhoused, the fund. A few are new arithmetic:
- **Life expectancy:** a period life table built from the five bands' monthly mortality. That is crude with five bands, so it is labelled as computed.
- **Kaitz index:** the minimum wage over the average wage.
- **Old-age dependency:** seniors over adults.
- **Price level against the US:** parity over fx.

**Real-world sources, by measure:**
- IMF WEO: GDP, growth, inflation, unemployment, current account, debt.
- World Bank WDI: trade, investment, electricity, health spending.
- UN WPP: people, births, deaths, life expectancy, ages, migration.
- UNODC: homicides, police.
- World Prison Brief: prisoners.
- FAO: undernourishment.
- OECD: housing and household debt where covered.
- BIS: credit.

## 7. Provenance: the (i) and the marks

- **The (i)** by the title opens a popover (board 4). It explains the three kinds in a sentence each, notes the anchor ("one founding game dollar is one 2025 dollar: the game's prices were set from 2025 figures, though its calendar starts in 2000"), and gives the data build's date and counts.
- **The marks:** a filled dot, a half dot and a dashed ring, all in `TEXT_MUTED` grey. They are told apart by shape, because colour is news.
- **The legend** sits under the table, with a count: "11 of 37 figures here are the game's estimates".
- **Opening a row** (a drawer, board 4) lists every measure for that place, 2025 beside the projection, each with its mark and its source in a few words.
- **Rows with no figure** for a measure are left off that ranking. They are never shown as 0.

## 8. The data files (for the coding pass)

- **`world-2025.json`** (resources): about 195 nations and about 60 city regions. Each entry has a kind, the 70 measures (value, mark, source key), a PPP figure where one exists, and a size band. It is about 250 × 70 cells, under 1 MB.
- **`world-path.json`**: five-year steps from 2025 to 2100 per place, for people, GDP per person (market and PPP) and the few projected rates. The years between steps are interpolated geometrically. There is also a history block from 2000 to 2024 for the "actual" case (D11).
- **`world-sources.json`**: source keys to their titles, editions and links. The (i) and the drawer read it.
- **An offline builder** (Python, kept in the repo's tools, not shipped) makes all three from the public downloads, applies the fill rules and writes each rule into the file as text, so the drawer can say how a ○ was made. The game never fetches anything.

**The peer list** (D7): every UN member, plus Taiwan, Hong Kong and Macau. About 60 city regions:
- the largest metros on each continent;
- every capital over 2M;
- ten small cities near the game's usual sizes, Halifax among them;
- the microstates as nations.

## 9. The screen

- **Where it lives** (D5): a rail tab "Rankings" with Lucide's trophy, after History, in money blue. It is also reachable from the main menu's YOUR CITIES as "Compare".
- **Board 1, top to bottom:**
  - the title, the (i), and the city's name, month and people;
  - four segmented controls: Peers as of · Money · Prices · Convert at;
  - the sentence;
  - three columns:
    - **measures:** pinned measures, the ten groups, "Browse all 70";
    - **the table:** filter chips with counts, Around you / Full list, then rank, kind icon, name, what, as of, value, bar and mark;
    - **Arden's card:** rank of N, above you, below you, the gap to pass the next one up, your ranks on the pinned measures, and **rank over the years** against the fixed 2025 board. That board holds still, so a rank there is a fair line over time.
- **The rank history** (D12) is computed when the tab opens from the slot's history: one rank per December, against the Today board only.
- **Ties share a rank.** Lower-is-better measures rank ascending and say so under the rank.

## 10. Decisions for Samuel (and Jerus)

| | decision | my recommendation |
|---|---|---|
| D1 | What a founding game dollar is in real money | **A 2025 dollar.** Or move the calendar to start in 2025 (Jerus's call) |
| D2 | "Per year or static year": I read it as nominal (each in its own year's money) vs one year's prices. Is that what you meant? | yes, default **static** |
| D3 | Add **same year** for saved cities (a ghost race from the history files) | **yes**, as "Your cities at: now / Arden's year" |
| D4 | Real US$ always at 2025 prices, or also offer nominal real-world dollars of the year (needs a US inflation path) | **always 2025** |
| D5 | Where it lives | **a Rankings rail tab**, plus Compare on the menu |
| D6 | Provenance: only the (i), or the (i) plus the per-row marks | **both**: the marks are small and the count line makes them useful |
| D7 | Peer list size and which cities | **195 nations and about 60 city regions**, small cities and microstates on purpose |
| D8 | After 2100 | **carry on by one rule** (people held, growth eased to 1%), every figure ○ |
| D9 | Default measure and view | **GDP per person, around you** |
| D10 | Measures with no comparable real figure | **rank only your cities**; real rows show "—" |
| D11 | A city before 2025 on the Projected board | **that year's actual history**, labelled "actual" |
| D12 | The rank history line | **against the Today board only**, computed on open |
| D13 | PPP for places with no published PPP figure | **use the market figure**, marked ○ |

## 11. The worked example (model.py)

`claude/leaderboards-model.py` (in this project) takes Arden in July 2050 (61,240 people, A$6.48B GDP, fx 1.74, parity 1.49, W 1.137), two saves and 34 real places. It asserts the two identities in §3 and prints every figure the boards show.

| | market | PPP |
|---|---|---|
| A$ | 105,813 | 105,813 |
| game US$ | 60,812 | 71,016 |
| real US$ at 2025 prices | 53,485 | 62,459 |

Arden is 26th of 37 on the Today board (real US$, market) and 28th at PPP. It is 29th on the Projected board: the world grows to 2050 and Arden, measured in constant dollars, stays where it is.

## Sources

- [IMF WEO Oct 2025 via StatisticsTimes: United States GDP per capita](https://statisticstimes.com/economy/country/usa-gdp-per-capita.php) (with the same site's pages for Germany, the UK, Canada, France, Italy, Russia, Türkiye and China)
- [UN World Population Prospects 2024](https://population.un.org/wpp/)
- [Pew Research Center: how the world's population is expected to change by 2100](https://www.pewresearch.org/short-reads/2025/07/09/5-facts-about-how-the-worlds-population-is-expected-to-change-by-2100/)
- [OECD Long-term baseline projections, No. 114 (horizon 2060)](https://katalog.bibliothek.kit.edu/bib/1406453)
- [Dellink et al., GDP projections for the Shared Socioeconomic Pathways (to 2100)](https://ageconsearch.umn.edu/record/330258/files/6444_Dellink.pdf)
- [Oxford Economics Global Cities Index 2025](https://www.oxfordeconomics.com/resource/oxford-economics-launches-global-cities-index-2025/)
- Project notes read: `the-world-a-city-is-founded-into.md`, `founding-a-city.md`, `spec-inflation.md`, `status-strips-and-calendar.md`, `save-system.md`, `the-palette.md`, `a-front-door-and-a-dashboard.md`, `the-world-the-water-and-the-map.md`
