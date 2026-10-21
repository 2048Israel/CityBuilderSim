# Spec: Rankings, your city against the world (design study, 2026-10-08; refreshed 2026-10-21)

*Samuel: the player compares their city to real nations and cities on two leaderboards, today (2025) and projected to the city's year; three currencies on a toggle (domestic, the game's USD, real USD); inflation, exchange rates and PPP handled; "a ton" of measures; made-up figures allowed behind a tiny (i); the player's other saved cities on the board too, compared "per year or static year".*

*Refreshed 2026-10-21 (Jerus: "start a session to refresh the leaderboard study against the nations study and D29 (2015), split it into batches after WN, and ask me its open calls"). §0 says what changed, §10 holds every call with his answers of that day, §11 the batches LB1-LB3, queued after WN7.*

No code was read or changed. The mockups are the artifact **"Rankings Study"** (five boards; board 1 is interactive, so every toggle can be pressed); since the refresh an orange note beside them names what §0 overtakes, and where they differ the study wins. Every figure in the mockups is illustrative. Nine real 2025 values were checked against the IMF World Economic Outlook (Oct 2025): GDP per person for the US, Germany, the UK, Canada, France, Italy, Russia, Türkiye and China. The rest are estimates from memory, and every city figure is invented to look right. The arithmetic is `leaderboards-model.py` beside this note (§12); its unit and its world's dollar are overtaken (§12 says how).

---

## 0. The refresh: what changed since 2026-10-08

1. **The nations study (2026-10-20, `spec-nations.md`) took the peer list and the world files.**
   - Its D1 ("All 196", Jerus) is this study's peer list: every UN member, plus Taiwan, Hong Kong and Macau.
   - Its D24 makes one world file, `data/world-1.bin`, built by `tools/world/` (WN0). This study's planned `world-path.json` is that file's paths. Its `world-sources.json` folds into the files' own meta. Only `world-2025.json`, the 70 measures, stays this study's (LB1), made by the same pipeline.
   - Its three marks (● published, ◐ a published projection, ○ the game's estimate) are this study's, with the same meaning (nations §3.2).
   - Its licence rule (nations §3.3): every source is named with its licence, and the IMF's WEO is out, because its terms ask permission for commercial reuse. This study's measures leaned on the WEO; they move to the World Bank's WDI and the other open sources (§6).
2. **D27: the second board reads the game's world.** It shows the city's own nations on its own path (Historical or Random), not the real world's projections. So:
   - the OECD's and the SSP's GDP paths leave the study;
   - before 2025 a Historical world *is* the real history, so D11's "actual" is the world's own (●);
   - past 2100 the world runs on the rule this study wrote (D8), which the nations study adopted (its D14).
   - The board is renamed **Your world**, and the first **Real world 2025** (D16).
3. **D29: two US dollars, the real one at constant 2015 dollars** (Jerus, 2026-10-21).
   - The game's US$ is now the US nation's money. It inflates on the US's path: Historical's real prices to 2025, about 2.5% a year; Random's drawn. The foreign inflation slider goes.
   - Before D29 it wandered round a flat trend near 1.14 times founding. So "per year or static" now moves a saved city a lot, not a few percent.
   - The real US$ is the game's US$ deflated by the US's prices: the board's pivot, at 2015 prices, not 2025's (D4).
   - **And a founding game dollar counts as a 2015 dollar** (D1, changed): the game's US$ and the real US$ start equal and part only as the game's US prices rise.
4. **The calendar moves back to 1900** (R0, after these batches). Jerus: the start date "will move back anyways".
   - D1's bridge does not depend on the start year, so R0 changes no real figure.
   - **Real world 2025** does not move.
   - **Your world** reaches back as far as R0 carries the world file (Maddison's output before 2000, nations §9).
   - "Same year" for saved cities is read as **same age**, months since founding. That is the same thing while every city starts in one month.
5. **The exchange-rate study (2026-10-21).** The city's parity (`fxParity`) becomes XR1's basket with rent held to a third. It is still the city's PPP rate. The D$ floats (XR1) and the world's moneys swing (XR3, in WN4). None of that can reorder the board through the Money toggle: one pivot, then display (§3).

**What still stands:** one pivot before display; three moneys on a toggle; the provenance marks and the (i); the 70 measures; saved cities read from their slots; ties and lower-is-better; rows with no figure left off; the rail tab.

## 1. Summary for Samuel

1. **One pivot, then display.** Every money figure is turned into one unit first, real US dollars at 2015 prices (`US$₁₅`). The board ranks in that unit and only then shows the result in the money the player picked. So **the Money toggle can never reorder the board.** Only these can: the board (Real world 2025 or Your world), Convert at, and Prices for saved cities.
2. **The bridge: one founding game dollar counts as one 2015 dollar** (D1, Jerus 2026-10-21). The game's prices stay as struck from 2025-26 real figures (the rebalance). So in real terms a city reads about 36% richer than if they were called 2025 dollars, since US prices rose about 36% from 2015 to 2025. It holds at any start year: 2000 today, 1900 after R0.
3. **The three moneys inflate differently, and that is the point of having three.**
   - **The city's money** (A$ in the examples) inflates at the city's rate, about 2% a year under the autopilot since 0.7.42. Its rate floats round its parity (XR1).
   - **The game's US$** inflates on the US's path (D29, WN4).
   - **Real US$** is held at 2015 prices.

   So `US$₁₅ = game US$ ÷ P(t)`, where P is that world's US price level, 1.0 at founding.
4. **Two boards** (D16, D27):
   - **Real world 2025** is the real world's latest year, fixed.
   - **Your world** is the city's own world at the city's month: 196 nations on their path, and the city regions moving with their nations. It ranks the ten measures the world carries (D15).
5. **Three ways to convert** (D14, Jerus: "both types of rankings"):
   - **at this month's rate**, the default: places move with their currencies, as on the IMF's lists;
   - **at the constant 2015 rate**, the World Bank's way: only real growth moves a place;
   - **at PPP**: prices evened out between places.
6. **Per year or static** (D2): nominal against one year's prices. It moves only **saved cities**. The default is **per year** (Jerus): each saved city in its own year's dollars. Since D29 a city 200 years on reads about 50 times bigger that way at 2% a year; static brings it to the active city's prices.
7. **Your cities at your age** (D3): each saved city read from its history file at the active city's age. "At your age, Danzik had 312,400 people and US$34,532 a head." A ghost race, cheap because every slot keeps `slot-NN-history.json`.
8. **PPP for the city comes for free.** The game's parity rate (`fxParity`, XR1's) *is* a PPP rate. A PPP figure is the domestic figure ÷ parity instead of ÷ the market rate.
9. **Provenance is three kinds, not one disclaimer** (D6): ● published, ◐ a published projection, ○ the game's estimate. The (i) by the title explains them. Each row carries a small grey mark, told apart by shape, with a count under the table.
10. **70 measures in ten groups** (§6). 66 have published figures for nations and 13 for city regions. Four have no comparable real figure; only your cities rank on those (D10).
11. **The board opens on GDP, the full list** (D9, Jerus). By total GDP a small city sits near the foot for a long time. **GDP per person**, **Around you** (the top three, then you and six either side) and **Your size** (places within ten times your people: Monaco, Liechtenstein, Iceland, Reykjavik, Halifax) are each one click away.

## 2. The three moneys

| | what it is | how it inflates | where it is in the game |
|---|---|---|---|
| **The city's money** (A$ here, named at founding) | what every screen already shows | the city's own price index, about 2% a year under the autopilot (`Expectations`, Pe); its rate a float round its parity (XR1) | `game.getCurrency()`, `PriceIndex`, `ForeignAccounts` |
| **US$ · game** | the US nation's money in this city's world: the vault, Trade, land | the US's prices on its path (D29): Historical's real ones to 2025, then the game's rule; Random's drawn | `Nations` (WN1), the moneys (WN4) |
| **US$ · real 2015** (`US$₁₅`) | the game's US$ deflated by its US prices since founding; a founding dollar counts as a 2015 dollar (D1) | does not: a constant-price unit | built in WN4, shown in WN6 (D29), the board's pivot |

**Why the bridge is fixed at founding, not at the calendar** (D1). The game's prices were struck from 2025 figures whatever its calendar says. A bridge through the calendar, with a founding dollar worth a dollar of the start year, would make every city read 1.9 times richer at a 2000 start, and about 38 times at 1900 (est.), with nothing in the city changed. Jerus fixed the bridge at founding and named it 2015, "so that its calibrated to the other": the two US dollars start equal.

## 3. The model

**The pivot.** For a game city c at month t with a domestic money figure X:

```
this month's rate : game US$ = X ÷ fx_c(t)          fx     = the city's money per game US$ (XR1's float)
PPP               : game US$ = X ÷ parity_c(t)      parity = fxParity, XR1's basket with rent held to a third
then              : US$₁₅    = game US$ ÷ P_c(t)    P      = that world's US price level, 1.0 at founding (D1, D29)
constant 2015     : US$₁₅    = X_real ÷ fx_c(0)     X_real = the figure at founding's prices (realGdp); fx_c(0) the founding rate
```

**The real rows arrive in `US$₁₅`:**
- this month's rate: 2025's current-dollar figure × the US's prices 2015 over 2025, about 0.74 (CPI-U, est.);
- constant: the WDI's constant 2015 US$ series, as published (●);
- PPP: 2025's current international dollars × the same 0.74.
- **Your world's nations** come from the game's world: real output, prices and the money's rate (WN1, WN4), by the same three rules.

**Constant** applies where a measure has a constant-price series: GDP, GDP per person, household spending per person. Other money measures (wages, the minimum wage) show this month's rate under constant, and the sentence says so.

**Display.** The active city is 0, at year Y0:

```
Real US$                    : US$₁₅
Game US$, static            : US$₁₅ × P0(Y0)
City's money, static        : US$₁₅ × P0(Y0) × rate0(Y0)            rate = fx, or parity under PPP
Game US$, per year          : a saved city i → X_i ÷ rate_i(t_i)    (its own year's game dollars)
City's money, per year      : a saved city i → X_i ÷ rate_i(t_i) × rate0(Y0)
Constant, any money         : founding's prices: real US$ as is, game US$ at P = 1, the city's money at fx_c(0)
```

**Identities the harness asserts** (the model asserts the first two):
- The active city's figure in its own money never changes under this month's rate or PPP: the toggles move everyone else into the city's money, never the city out of it. Under constant it is the city's real GDP at founding's prices.
- Static boards in the city's money, game US$ and real US$ have one order.
- Prices (per year or static) changes only rows of kind "saved", and only when Money is not real US$.
- At founding, real US$ equals game US$ (D1).
- Under constant, a city whose real GDP holds keeps its figure whatever its rate does.
- Your world's rows equal `Nations`' figures for the month, and Historical's equal the world file to 2025.

**Prices is locked** when Money is real US$ (2015 prices by definition), under constant, and on measures that are not money. The board greys the control and says why in its tooltip.

**A sentence under the controls** names exactly what is on screen: *"Real US dollars at 2015 prices, at this month's exchange rates. The real world in 2025; Arden in July 2050."* This one line carries the combinations the toggles make, and it is the most important piece of the UI.

**What each toggle moves** (board 2):

| | the active city | the real world | Your world's nations | saved cities |
|---|---|---|---|---|
| Board | nothing | Real world 2025, or off | on, at the city's month | nothing |
| Money | the unit | the unit | the unit | the unit |
| Prices | nothing (already Y0) | nothing | nothing (at Y0) | per year: own year's dollars; static: Y0 prices |
| Convert at | this month's rate, constant or parity | the matching published figure | by the matching rule | each through its own rate |

## 4. The two boards and their years

**Real world 2025** is fixed: the real world's latest year, every figure ● or ○, the same at every month of every city. Its rank is a fair line over time (D12).

**Your world** is the city's own world at the city's month (D27):

| the city's year | a Historical world | a Random world |
|---|---|---|
| before 2000 | after R0 only, as far back as R0 carries the world file (Maddison's output) | the same start, then its draws |
| 2000-2025 | the world file's real figures ● | real at January 2000, then its own draws ○ |
| 2025-2100 | people: the UN's projection ◐; output: the game's rule with seeded shocks ○ (nations D14) | people: the UN's ◐; output: its draws ○ |
| after 2100 | people held at 2100, output a head growing at its 2090s pace eased toward 1% a year over 50 years (this study's rule, D8, nations D14) ○ | its draws continue; people held at 2100 ○ |

**What Your world ranks** (D15): the ten measures the world carries for each nation:
- people and their growth;
- GDP, GDP per person, real growth and 10-year growth;
- inflation;
- price level against the US;
- area and people per km².

The other 60 rank on Real world 2025 only. A measure held at 2025 in a world at 1950 or 2300 would say nothing true.

**Cities** (D7). About 60 city regions sit on both boards. On Your world each is its nation's path times its 2025 share of the nation's people and output, with rates held, so every city figure there is ○. Metro-level projections are paid products or stop around 2035. The share keeps a city from drifting away from its nation.

## 5. Your saved cities

**Reading them.**
- `SaveHeader` already parses each slot's save. The rankings need a few more of its fields: the measures in §6 that the game has, fx, parity, P, the price index, the money's code and the month. Read only: the board never writes a slot.
- **At your age** (D3) reads the slot's history file at the active city's age in months. If the city never reached it, the row shows "stopped at Feb 2031" and no figure.
- Slot 0 (the autosave) is left out. It is a copy of a numbered city.

**Each save is its own world.** Each has its own path or seed, its own US prices and its own money's name. The pivot is the only bridge between two saves. That is why per year is bridged on each world's game US$ and never on own-money figures.

**A currency reform** (÷10, ÷100) changes the units of a city's money and its history. A saved city's money is always read in the units of its last save, and the pivot divides it out, so a reform never moves a rank.

## 6. The 70 measures

Arrows: ↑ higher is better · ↓ lower is better · – neither, ranked largest first with no verdict colour. `$` follows the Money toggle. N and C mean published for nations and for city regions. "yours" means only your cities rank (D10). **W** marks the ten on Your world (D15).

| group | measures |
|---|---|
| **Economy (11)** | GDP $↑ NC W · GDP per person $↑ NC W · real growth ↑ NC W · 10-year growth ↑ N W · household spending per person $↑ N · investment % GDP – · government spending % GDP – · exports % – · imports % – · trade balance % – · current account % – |
| **Work & pay (6)** | unemployment ↓ NC · taking part in work ↑ · average wage $↑ NC · minimum wage $– · minimum against average wage – · adults in college or university ↑ |
| **Prices & money (8)** | inflation – W · policy rate – · 10-year yield ↓ · real interest rate – · currency against parity – · reserves in months of imports ↑ · M2 % GDP – · price level against the US – W |
| **Public money (8)** | debt % GDP ↓ · debt in foreign money ↓ · budget balance ↑ · tax revenue % – · interest % of revenue ↓ · city fund % GDP ↑ · credit rating ↑ · pensions % GDP – |
| **Banks & markets (5)** | bank capital ratio ↑ · bad loans ↓ · credit to firms and households % – · stock market % – · household debt % income ↓ |
| **People (11)** | people – NC W · growth – NC W · births – · deaths ↓ · child deaths ↓ · life expectancy ↑ · aged 65+ – · old-age dependency – · net migration – · people per household – · orphans per 10,000 ↓ yours |
| **Housing (5)** | rent % income ↓ NC · homes per 1,000 ↑ · without a home per 10,000 ↓ · households sharing ↓ · home price to income ↓ NC |
| **Safety (4)** | homicides ↓ NC · prisoners – · police – · recorded crime ↓ yours |
| **Health & care (4)** | health spending % – · sick at any time ↓ yours · going hungry ↓ · care coverage ↑ yours |
| **Land, power & travel (8)** | area – NC W · people per km² – NC W · electricity per person – · renewable share ↑ · water per person – · cars per 1,000 – · commutes by transit ↑ NC · oil produced – |

**Where the game's side comes from.** Almost all of it is already a History series (217 lines since 0.7.37): GDP and its parts, real GDP (`realGdp`), M0 and M2, the bank's capital ratio, deaths by band, homicides, prisoners, police, the unemployed, the unhoused, the fund. A few are new arithmetic:
- **Life expectancy:** a period life table built from the five bands' monthly mortality. That is crude with five bands, so it is labelled as computed.
- **Kaitz index:** the minimum wage over the average wage.
- **Old-age dependency:** seniors over adults.
- **Price level against the US:** parity over fx.

**Real-world sources, by measure.** Each is named in `world-2025.json`'s meta with its licence, by the nations study's rule (§3.3). A source whose terms do not let a game ship numbers made from it is left out. Its measure's real rows then show "—", as D10's do.
- **The World Bank's WDI** (CC BY 4.0), in place of the IMF's WEO: GDP (current, constant 2015 US$ and PPP), growth, inflation, unemployment, current account, trade, investment, electricity, health spending, debt where covered.
- **The UN's WPP:** people, births, deaths, life expectancy, ages, migration.
- **UNODC:** homicides, police.
- **World Prison Brief:** prisoners.
- **FAO:** undernourishment.
- **OECD:** housing and household debt where covered.
- **BIS:** credit.

LB1 checks each licence before a figure goes in.

## 7. Provenance: the (i) and the marks

- **The (i)** by the title opens a popover (board 4). It explains the three kinds in a sentence each. It notes the bridge ("a founding game dollar counts as a 2015 dollar; the game's prices were set from 2025 figures"), and gives the data build's date and counts.
- **The marks** (D6): a filled dot, a half dot and a dashed ring, all in `TEXT_MUTED` grey. They are told apart by shape, because colour is news.
- **The legend** sits under the table, with a count: "11 of 37 figures here are the game's estimates".
- **Opening a row** (a drawer, board 4) lists every measure for that place, 2025 beside Your world's figure, each with its mark and its source in a few words.
- **Rows with no figure** for a measure are left off that ranking. They are never shown as 0.

## 8. The data files

- **`data/world-1.bin`** (WN0, the nations study): the 196 nations, their borders, and their paths 2000-2100 (people, real output, prices, the money's rate). Your world reads it through `Nations`.
- **`world-2025.json`** (resources, LB1): 196 nations and about 60 city regions. Each entry has:
  - a kind and a size band;
  - the 70 measures (value, year, mark, source key);
  - GDP and GDP per person at current US$, at constant 2015 US$ and at PPP;
  - for a city region, its nation and its share of the nation's people and output.

  Its meta carries the sources with their licences, and the US's prices 2015 over 2025. It is about 250 × 70 cells, under 1 MB.
- **One pipeline**: `tools/world/` writes both. It is Python, kept in the repository and not shipped. The game never fetches anything. The fill rules are written into the file as text, so the drawer can say how a ○ was made.
- **Asked of WN0** (D29 and D14): the world file's real output per nation in **constant 2015 US$**, the WDI's own series. Then Your world's constant figures are native and need no second conversion.

**The peer list** (D7): every UN member, plus Taiwan, Hong Kong and Macau (nations D1). Territories count with their sovereign, as in the nations study (§3.1). About 60 city regions:
- the largest metros on each continent;
- every capital over 2M;
- ten small cities near the game's usual sizes, Halifax among them.

The microstates are on it as nations.

## 9. The screen

- **Where it lives** (D5): a rail tab "Rankings" with Lucide's trophy, after History, in money blue. It is also reachable from the main menu's YOUR CITIES as "Compare", and from the nation's card on the map ("All its measures", nations §8).
- **Board 1, top to bottom:**
  - the title, the (i), and the city's name, month and people;
  - four segmented controls:
    - **Board:** Real world 2025 · Your world;
    - **Money:** the city's · game US$ · real US$;
    - **Prices:** per year · static;
    - **Convert at:** this month's rate · constant 2015 · PPP;
  - the sentence;
  - three columns:
    - **measures:** pinned measures, the ten groups, "Browse all 70";
    - **the table:** filter chips with counts (Your size among them); Full list or Around you; then rank, kind icon, name, what, as of, value, bar and mark;
    - **Arden's card:** rank of N, above you, below you, the gap to pass the next one up, your ranks on the pinned measures, and **rank over the years** against Real world 2025, which holds still.
- **Your cities at:** now · your age (D3), beside the saved cities' chip.
- **Opens on** (D9): GDP, the full list, real US$, this month's rate, per year.
- **The rank history** (D12) is computed when the tab opens from the slot's history: one rank per December, against Real world 2025 only.
- **Ties share a rank.** Lower-is-better measures rank ascending and say so under the rank.

## 10. Decisions

**Answered, Jerus, 2026-10-21**, in the refresh's four rounds of questions. Four calls were already settled by later decisions; the other nine he answered, and three new ones the refresh raised.

| | call | answer |
|---|---|---|
| D1 | What a founding game dollar is in real money | **Changed. Jerus: "2015 so that its calibrated to the other"**, confirmed as "Yes, units only": a founding game dollar counts as one real 2015 dollar, so the game's US$ and the real US$ start equal. The game's prices stay as struck from 2025's (the calibration the nations study's §7.1 and the research studies rely on). Each city reads about 36% richer in real terms than under the study's first answer (a 2025 dollar), which the nations study had kept under its D13. |
| D2 | "Per year or static year": nominal (each in its own year's money) against one year's prices | **Jerus: "Per year, each its own year"** as the default, against the recommendation of static; static is on the toggle |
| D3 | A third choice for saved cities from their history files | **Jerus: "Yes"** (recommended): "Your cities at: now / your age", read as months since founding |
| D4 | Real US$ always at one year's prices, or also nominal real-world dollars | **Settled by D29** (nations study, Jerus 2026-10-21): "the real usd dollar, aka two us dollars, one is the in game us dollar which inflates over time, and the other is the real us dollar, and that one is what real gdp is shown in"; base year "2015, cause later the start date will move back anyways". The nominal dollar D4 set aside is now the game's US$ |
| D5 | Where it lives | **Jerus: "Rail tab after History"** (recommended), plus Compare on the menu and the nation's card's door |
| D6 | Provenance: only the (i), or the (i) plus the per-row marks | **Jerus: "The (i) and a mark per row"** (recommended). The three kinds are also the world file's (nations §3.2) |
| D7 | Peer list size and which cities | **Nations settled by the nations study's D1** (Jerus: "All 196"). **Cities, Jerus: "Yes, about 60"** (recommended) |
| D8 | After 2100 | **Settled by the nations study's D14** (taken on recommendation, 2026-10-20): Historical after 2025 is "output by the game's own rule..., then the leaderboard's rule". The rule now lives in the world, and the board reads it (D27). A Random world runs on its draws; people as D8 says |
| D9 | Default measure and view | **Jerus: "GDP, full list"**, against the recommendation of GDP per person, around you; both are one click away |
| D10 | Measures with no comparable real figure | **Jerus: "Rank only your cities"** (recommended); real rows show "—" |
| D11 | A city before 2025 on the second board | **Settled by D27** (nations study, taken on recommendation 2026-10-20): "The leaderboard's Projected board reads the game's world: the city's own nations on its own path, not the real projections". Historical before 2025 is the file's history (●); Random's is its own path; before 2000, after R0, as far as R0 carries the world |
| D12 | The rank history line | **Jerus: "Real world 2025 only"** (recommended), computed on open |
| D13 | PPP for places with no published PPP figure | **Jerus: "Market figure, marked"** (recommended): their market-rate figure in its place, ○ |
| D14 | *New.* Should a city's money rising or falling move its rank? | **Jerus: "both types of rankings"**, confirmed as three choices on Convert at: this month's rate (the default, Jerus: "This month's rate"), constant 2015 rate (the World Bank's way, real growth only), and PPP |
| D15 | *New.* What Your world ranks | **Jerus: "Only those ten"** (recommended): people, their growth, GDP, GDP per person, real growth, 10-year growth, inflation, price level against the US, area, people per km² |
| D16 | *New.* The boards' names | **Jerus: "Real world 2025 / Your world"** (recommended), in place of Today / Projected |

## 11. Batches

Fresh ids: LB appears nowhere in `QUEUE.md`, `docs/` or the git log. Queued after WN7 and before the research rows R0-R5, in this order.

| Batch | What | Harness | Traces |
|---|---|---|---|
| **LB1** | **The measures file:** `tools/world/` (WN0's pipeline) writes `world-2025.json` (§8): 196 nations and about 60 city regions (D7), the 70 measures with value, year, mark and source key, GDP at current, constant 2015 and PPP dollars, the city regions' shares, the sources and their licences (no WEO), the US's prices 2015 over 2025. A resources fixture; `WorldMeasures`, the reader. | `WorldMeasuresCheck`: every nation of `world-1.bin` present once; a missing figure is absent, never 0; every mark and source key resolves and every source names its licence; a city region's shares of its nation under 1; the 2015-over-2025 ratio equals the CPI-U's to the digit | identical: nothing reads it |
| **LB2** | **The board:** `Rankings`, a pure model in the `*View` manner. It holds the city's measures from History, plus the life table, the Kaitz index, old-age dependency and price level against the US. It holds the pivot (D1, D29) with the three conversions (D14), and the display. Its two boards: Real world 2025 from `WorldMeasures`; Your world from `Nations` at the city's month (WN1, WN4) on the ten measures (D15), with the city regions by share. Ties and lower-is-better; rows with no figure left off. **The Rankings rail tab** (D5, board 1): the four controls, the sentence, the table opening on GDP's full list (D9), Around you and Your size, the city's card, the (i), the marks and their count (D6), the row's drawer; the nation's card's "All its measures" door. | `RankingsCheck`: §3's identities; at founding real US$ equals game US$; constant holds a steady real GDP's figure while its rate moves; Your world's rows equal `Nations`' figures, Historical's the file to 2025; a lower-is-better measure ranks ascending; ties share; a place with no figure is off, never 0 | identical: a pure model and a screen |
| **LB3** | **Your saved cities:** the other slots read through `SaveHeader` (read only, slot 0 left out); per year by default and static (D2); at your age from the slot's history (D3), "stopped at" where it never got there; a reform never moves a rank. The rank over the years against Real world 2025, computed on open (D12). Compare on the main menu's YOUR CITIES. The manual's Rankings page. | `RankingsCheck`'s new sections, on scratch saves (`GameFiles.scratch`): Prices moves only saved rows and never under real US$; a ÷100 reform leaves every rank; at your age equals the history file's month; the rank line has one point a December | identical |

**Order and fit:**
- **LB1** needs only WN0's pipeline. It is queued after WN7 as the brief asked; it could move up to just after WN0 if Jerus wants the file sooner.
- **LB2** needs WN1 (the nations' years), WN4 (the moneys and the real US$), WN6 (the nation's page, real GDP shown) and XR1 (the parity). **LB3** follows LB2.
- **R0** (the calendar to 1900) comes after. Your world follows the world file back with it; nothing in LB changes for it.
- **The ensemble:** none of the three moves the default playtest's trace, so each skips it (EN1).

## 12. The worked example (model.py)

`leaderboards-model.py` (beside this note) takes Arden in July 2050 (61,240 people, A$6.48B GDP, fx 1.74, parity 1.49, W 1.137), two saves and 34 real places. It asserts the two identities in §3 and prints every figure the boards show. Its figures, at the study's first unit:

| | market | PPP |
|---|---|---|
| A$ | 105,813 | 105,813 |
| game US$ | 60,812 | 71,016 |
| real US$ at 2025 prices | 53,485 | 62,459 |

Arden was 26th of 37 on the Today board (real US$, market) and 28th at PPP. It was 29th on the Projected board: the world grows to 2050 and Arden, measured in constant dollars, stays where it is.

**What the refresh overtakes in it.** The pivot, the display and the identities stand. Its unit and its dollar do not:
- Under D1 and D29, real US$ is game US$ ÷ P, the game's US prices since founding. At 2% a year from 2000 that is about 2.7 by 2050 (est.), where the model has a stationary W of 1.137 and a 2025 dollar.
- Its Projected board is the real projections, where Your world is now the game's own.

LB2's harness redoes the arithmetic on the game's own figures. The model is kept as written, as the record of the first pass.

## Sources

- [IMF WEO Oct 2025 via StatisticsTimes: United States GDP per capita](https://statisticstimes.com/economy/country/usa-gdp-per-capita.php) (with the same site's pages for Germany, the UK, Canada, France, Italy, Russia, Türkiye and China): the first pass's nine checks. The WEO is not a source for the file (nations §3.3, its R12).
- [World Bank, World Development Indicators](https://datatopics.worldbank.org/world-development-indicators/) (CC BY 4.0): GDP in current, constant 2015 and PPP dollars, and the rest of §6's economy.
- [UN World Population Prospects 2024](https://population.un.org/wpp/)
- [Pew Research Center: how the world's population is expected to change by 2100](https://www.pewresearch.org/short-reads/2025/07/09/5-facts-about-how-the-worlds-population-is-expected-to-change-by-2100/)
- [Oxford Economics Global Cities Index 2025](https://www.oxfordeconomics.com/resource/oxford-economics-launches-global-cities-index-2025/) (why city projections are a nation's path times a share)
- The first pass also read the OECD's Long-term baseline (to 2060) and Dellink et al.'s SSP GDP paths (to 2100). D27 retires both from the board.
- The repository's studies: `spec-nations.md` (§1.6, §3, §7, §10: D1, D13, D14, D24, D27, D29), `spec-exchange-rates.md` (§3.7, §3.9, §5), `research-design.md` and `research-1900.md` (R0, the calendar).
- Project notes the first pass read: `the-world-a-city-is-founded-into.md`, `founding-a-city.md`, `spec-inflation.md`, `status-strips-and-calendar.md`, `save-system.md`, `the-palette.md`, `a-front-door-and-a-dashboard.md`, `the-world-the-water-and-the-map.md`
