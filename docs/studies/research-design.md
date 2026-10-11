# Research: design study and mockups

*2026-10-10. A design study for research in Civic Ledger, from the brief `brief-research-design.md`: Jerus's words, his standing rules, and eight parts for the report and five mockups. Nothing here gets built until every planned batch is done (the fixes, zoning, the five materials chains, Earth, buying land from the map, childcare). No game code was written or changed. The code was read at 0.7.106 (`2048Israel/CityBuilderSim`, main, 75bac03): `buildings.json`, `JobType`, `PayTier`, `EducationType`, `CityCalendar`, `World` and `Health`'s seeding, and the maps of `SpreadPlanner`, `Refining`, `Health`, `Sickness`, `CareType`, `Notice`. The project docs read are the brief's list. Four research passes ran the same day (R&D economics, the 1950 baseline, an engineering catalogue, and medicine, business and law); every figure below is theirs or the game's, cited by key to §11. "(est.)" marks a figure derived here, with its arithmetic. Money is D$ at the founding parity (one founding D$ is one 2025 US$, `spec-leaderboards.md`); research money is in the currency the source gives.*

*The model behind the figures is `claude/research-model/` (four Python files, a design aid like `refinery-margins.py`, not game code). It turns money into posts, posts into researchers and researchers into discoveries; `figures.py` reproduces §5.4 and the mockups' previews.*

> **Decided after this study (Jerus, 2026-10-10, 21:24): the game starts in 1900, not 1950.** "I think its a great idea, cause it also slowly introduces mechanics."
> - **Why:** on a 1900 calendar the playtest's city has 4,000–12,000 people in late 1950 and 17,000–30,000 by early 1989, so this catalogue's 40 discoveries (1951–1995) arrive when cities can afford to research them (Q2). And history brings the game's systems in one at a time: few kinds of building at the start, then cars, power, refining and modern medicine as their dates come.
> - **What it costs:** about 30 of the 101 buildings did not exist in 1900 (a rough count, est., against 13 in 1950), and most of the rest ran on hand labour, steam and horses. The oil chain is a kerosene business, with petrol mostly a by-product until thermal cracking (1913). Income per head in Canada in 1913 was about a sixth of today's (Maddison Project 2020: 7,088 against 44,869 in 2018, 2011 int.$), against about a fifth in 1950. The world carries two world wars, the 1918 flu and the Depression, and Canada had no central bank until 1935.
> - **What changes here:** nothing in §2 or §5–§7, which do not depend on the year. §3's 1950 set becomes a waypoint: 1950 is the 1900 city with the 1900–1950 catalogue applied. The catalogue grows backward to 1900, and Q1's economy study is for 1900; both are now done in `claude/research-1900.md` (2026-10-11): what existed in 1900, how it worked, the 1900 economy, the world to 1950, the Policy levers on Canada's dates, and a 1900–1950 catalogue of 48 entries. Q2's near-future list is still wanted, from 2025. Q3's `since` goes on anything after 1900. The mockups keep their dates (June 1955, June 1979); their cities are illustrative.
> - **A start year the player picks** (1900 or 1950) comes from the same rule later: a start is today's game with every later discovery undone.

---

## 0. In one page

### The design
- **A grant buys research posts, not odds.** Each field's grant pays for posts at the schools that host it, at what a post really costs: a researcher from the field's bands, support staff, and the lab's other costs. Labour is 60–67% of real R&D spending [TOE], so an engineering post costs **D$ 18.2k a month** (est., §1.8). The posts are jobs in the labour market. They pull graduates, and a post nobody fills costs nothing.
- **Researchers make discoveries by working on open problems.** A discovery is open when the science under it exists, which is a fixed number of years before history's date for it (Mansfield's academic-to-product lags, §1.3), and when what it needs first is known. Researchers spread over a field's open problems. No problem takes more than a team can use, and each has a minimum time. Each problem has a hidden size drawn once from the city's seed. When the work piled on a problem reaches its size, the inbox says so.
- **A prize pays only for a result,** and draws private Research Labs. Investors build a lab while the expected purse pays its researchers: a lab joins the cheapest open problem it could finish before the world. The city pays only when a discovery is made here (§5.3).
- **Three unlocks.** A **building** is a new template on the build list. A **recipe** is a new way an existing building turns inputs into outputs: free, and used the month it pays. **Equipment** is bought into a building and fitted only when it pays over its life, ranked against new buildings by the same spread planner (§2).
- **The world discovers too, on history's dates.** Everything reaches the city at its real date, from abroad and under licence. A city that researches can get there first, by as much as the science allows, and keeps the licence fee at home (§7).
- **There is no tree to click.** The order is hidden; the player sees researchers at work, money spent, how many problems each field has in reach and which sectors they touch, and every discovery on a timeline (§6).
- **1950 is today's game minus the catalogue.** `buildings.json` stays the destination. A 1950 building is today's with every catalogued discovery that touches it undone (§3.1).
- **What the numbers say:** a young city with one university finds almost nothing before the world, and gets its technology from abroad as real small economies do (75–90% from abroad [KELLER]). A big city funding three fields at about Canada's rate finds about **two discoveries a decade** before the world, **1.5 to 4.6 years** early (§5.4).

### The three calls that matter most (§8)
1. **The 1950 economy.** A 1950 start with today's wages and prices makes every 1950 building lose money, and makes every labour-saving machine pay at once. Real 1950 wages were about 45% of today's in Canada (est., [SC-E]). This needs its own study before research can be balanced.
2. **The world follows history, and then what?** History's catalogue ends about 2000. Most cities are small for their first century (the playtest's city has 17,000–30,000 people at month 1,070, early 2039 on a 1950 calendar), so they live through history as importers, and have nothing left to find by the time they can afford to look.
3. **Recipes as data, now.** The materials batches come first. If each building keeps its inputs, outputs and posts in a `recipes` list (§2.1), research later is mostly adding recipes.

### What comes with this study
- **Five mockups** in `claude/mockups-research/`: funding, a discovery arriving, what has been discovered, equipment on a sector's page, and a new building's card. Each shows a young city in June 1955 and a big city in June 1979 where that helps.
- **A catalogue of 40 discoveries** (§4), 1951–1995.
- **Twenty questions** (§8), each with a recommendation.

---

## 1. How real research turns money into technology

### 1.1 People
| Researchers (FTE) per 1,000 employed | Value | Source |
|---|---|---|
| Canada 1950 | **~0.8** (est.: US 2.7 × Canada's 1965 ratio to the US, 0.2 ÷ 0.65) | [JONES], [SCC6] |
| US 1950 / 1965 | 2.7 / 7.3 | [JONES], [FRED] |
| Canada 1965 | **2.0**: 14,910 S&E in R&D; industry 43%, government 32%, universities 23% | [SCC6] |
| Canada 1981 / 2000 / 2023 | 3.5 / 7.2 / **11.3** | [MSTI] |
| Today's frontier | Sweden 17.2, Korea 17.6 | [MSTI] |
| Canada 2023, by sector | business 66%, government 4%, higher education 29% | [MSTI] |

The game models the schools' research and private labs that chase prizes. It does not model businesses' own R&D, which is two-thirds of today's researchers (Q15).

### 1.2 Money
- **A researcher-year costs about three average wages.** Canada spends C$244k a year per researcher, 3.0× the average wage of C$80k (2023); its peers spend 3–7× [MSTI], [WAGE]. Canada's higher education spends C$274k.
- **Labour is 60–67% of the cost** (Canada business 67%, Germany 61%, US business 64%); equipment and buildings are 4–9% outside government labs [TOE].
- **R&D staff earn about 1.3× the average wage** in Canada; researchers are 0.70 of R&D personnel, so there are about 0.43 support staff per researcher [TOE], [MSTI].
- **Grants are small next to a researcher's cost.** An NSERC Discovery grant is C$42–49k a year, about 0.17 of a researcher-year at the full cost; a CIHR Project grant is about C$200k a year, about 0.8 [RCC], [CIHR]. Most of a university grant pays trainees: about 2.7 per funded researcher [NSERC-DRR].
- **Faculty research is already paid for.** A Canadian professor's workload is 40% teaching, 40% research and 20% service [UVIC].

### 1.3 Time
| Kind | Central | Range | Source |
|---|---|---|---|
| Firm R&D → patent | same year | 0–1 yr | [HGH] |
| Academic finding → first commercial product | **7.0 yrs** | instruments 4.2, electrical 5.3, chemicals 6.8, information 7.0, drugs 8.8, metals 9.8 | [MANS91] |
| Drug, synthesis → approval | 10.7 yrs | clinical start → approval 8.1 yrs; 11.8% of drugs entering trials are approved | [DIMASI] |
| Public basic biomedical research → new drug | 17–24 yrs | | [TOOLE] |
| Academic science → industry productivity | ~20 yrs | 10–30 | [ADAMS] |
| Business process | 2–5 yrs (est.) | | R1 §3 |

### 1.4 Scale
- **Output rises less than in proportion.** Patents rise with R&D at an elasticity of about 0.3 within firms (0.1–0.6 in the literature) [HGH], [KORTUM]. Doubling researchers in one period gives 1.2–1.7 times the output ("stepping on toes", λ = 0.5–0.75) [BJVW], [JW].
- **Ideas get harder to find.** Researcher-years per unit of progress rise every year: drugs 3.5%, Moore's law 6.8%, crop yields about 4–6%, the US economy 5.3% [BJVW]. The cost per approved drug rose 8.5% a year in real terms [DIMASI].
- **Researcher-years per output:** a US patent took about 4 in the 1950s and about 8 in the 1980s; a patent filed in the US, Europe and Japan takes 100–350 today (Canada 343); an approved drug about 3,300 [KORTUM], [MSTI], [DIMASI] (all est., R1 §4c).

### 1.5 Grants against prizes
| | Grant: pays for the work | Prize: pays only for the result |
|---|---|---|
| Who bears the risk | the funder | the entrants (Harrison waited 36 years for the Longitude money [LONG]) |
| Who enters | established researchers; a marginal NIH grant adds about one paper in five years, because the losers find other money [JL] | outsiders and many teams: a RASE gold medal brought 65–78% more entrants; doubling a cash prize brought 4–7% more patents [BLN]; DARPA's 2005 race drew 195 teams [DARPA] |
| What it suits | open, uncertain work; long failure-tolerant funding raised top-1% papers by 96% and flops by 35% [AGM] | a defined, near-feasible target [WIL]; the pneumococcal advance commitment brought existing vaccines in about 5 years sooner [KLS] |
| Private money it draws | none: it is the money | the Ansari X Prize drew about 10 private dollars per prize dollar (est.) [XP] |
| Moving researchers | 10% more money moves a researcher's direction by about 1% [MYERS] | entry rises with the purse |

### 1.6 Diffusion
- **Between countries:** technologies invented before 1950 reached the average country after 44 years; post-1950 ones arrived in 7–18 (the oxygen converter 14) [CM].
- **Within a country, 10% to 90% of users:** fast cases take 4–10 years (hybrid corn in Iowa 4.3; the oxygen converter in Japan 9.6), the typical case 15–20 (the converter in Germany 15, the US 21), and refits that need a whole plant redesigned 30–40 (electric motors) [SUTCH], [HCCTA] (fits est.), [HALL04].
- **What sets the speed:** profitability raises it and the size of the investment lowers it [MANS61] (coefficients unverified). The reaper waited until farm wages were high enough to pay for it [HALL04]. Early barcode scanners probably did not pay [M-B4].

### 1.7 Home-grown against imported
- Smaller OECD economies, Canada among them, get **75–90%** of their R&D-driven productivity from abroad [KELLER], [EK99].
- **Canadians file 12% of Canada's patents** (2023) [WB-PAT]. Canada pays US$20.3bn a year in IP charges and receives US$8.0bn [WB-IP].
- **Canada imported 63% of its equipment** in 1985 [EK01].
- **A licence costs 4–7% of sales** by industry median, or about 25% of the licensee's profit from the licensed product (the "25% rule") [ROY].
- **University licence income is about 0.5–0.6% of research spending** in Canada, about what the licensing offices cost to run [SC171].

### 1.8 What the game uses
| Quantity | Value | Basis |
|---|---|---|
| A research post, engineering | **D$ 18.2k a month** | researcher pay (half `UNIV_HIGHTECH_ENG` D$ 9.6k, half `UNIV_SCIENCE` 8.2k) + 0.43 college support at 5.5k, ÷ a 62% labour share (est.) |
| medical | **D$ 23.8k** | half `UNIV_DOCTOR` 15.6k, half science; ÷ 60% (est.) |
| business | **D$ 12.2k** | half `UNIV_FINANCE`, half `UNIV_POLICY`; 0.20 support at 4.5k; ÷ 80% (no labs) (est.) |
| law | **D$ 16.0k** | half `UNIV_LAW`, half policy; ÷ 80% (est.) |
| Check | engineering D$ 218k a year; Canada's higher education C$274k, business C$197k | [MSTI] |
| Faculty research | 40% of each school's university-band staff | [UVIC] |
| Room for posts at a school | 2.7 posts per university-band staff member | NSERC's 2.7 trainees per funded researcher [NSERC-DRR] (est.) |
| Effort of a discovery | buildings and equipment **400** researcher-years at 1950, recipes **200**, drugs and vaccines **500** | R1's 300–1,000 for engineering in 1950; a drug ~3,300 by 2005 (est.) |
| …growing | engineering, business and law 5.3% a year; medicine 3.5% | [BJVW] |
| Minimum time | 3 years (building, equipment), 2 (recipe), 8 (drug: clinical phases) | [DIMASI], [DARPA] 18 months to a finished vehicle (est.) |
| Most researchers on one problem | effort ÷ minimum time (est.) | |
| How far ahead of the world | engineering 7 years, medicine 9, business and law 4 | [MANS91]; business est. |
| Hidden size | effort × a lognormal draw (σ 0.6, mean 1) from the city's seed and the discovery's id | (est.) |

---

## 2. The three unlocks

### 2.1 Recipes, and the shape the materials batches should leave room for
Jerus: "Recipes are free to upgrade immediately." A recipe is a way a building runs: what it uses, what it makes, who it needs, what it draws. A building can know several; each month it runs the best one it knows at the city's own prices (`SpreadPlanner.cityValue()`), as the refinery picks its slate. Switching costs nothing and takes effect that month.

**The shape** (proposed for `buildings.json`, so the materials batches can leave room for it):
```json
{ "id": 11, "name": "Electric Arc Furnace", "sector": "Iron & Steel",
  "cashCost": 22471, "constructionMaterials": 416, "land": 90000,
  "recipes": [
    { "id": "eaf-ingot", "since": "1950",
      "uses": { "SCRAP": 1320 }, "makes": { "STEEL": 1080 },
      "jobs": { "NO_DIPLOMA": 25, "DIPLOMA": 14, "COLLEGE_ENGINEERING": 4 },
      "electricity": 750, "fuel": 32500 },
    { "id": "eaf-caster", "since": "E9", "needsEquipment": "caster",
      "uses": { "SCRAP": 1320 }, "makes": { "STEEL": 1200 },
      "jobs": { "NO_DIPLOMA": 22, "DIPLOMA": 12, "COLLEGE_ENGINEERING": 4 },
      "electricity": 750 } ],
  "equipment": [ { "id": "caster", "since": "E9", "machinery": 120, "work": 900,
                   "importShare": 0.4, "lifeMonths": 300 } ] }
```
- **Keep the top-level `makes`, `uses` and `jobs` readable as the default recipe** until every building is moved, so nothing changes at the defaults.
- **`since`** is the start year ("1900", the box at the top; the example above was written for 1950) or a discovery's code. A recipe or equipment whose discovery is unknown does not exist for the city.
- **A recipe can need equipment** (`needsEquipment`); equipment can enable several recipes.
- **Buildings the materials batches add that are post-1950 should be written with `since`**: the Pelletising Plant (E4, 1955), the Leach & SX-EW Plant (E12, 1968), the Harvesting Operation (E14, 1973), the Large Mini-Mill (E13), and the Oxygen Steel Shop's converters (E1, 1952), which in 1950 are open hearths. With the 1900 start, everything that post-dates 1900 carries `since`, not only these.
- **The processing sectors already rank by spread**; with recipes, a candidate building is valued at its best known recipe.

### 2.2 Equipment
Jerus: "equipment requires investment, thus sectors would not, and could not, even invest until it's actually worth it."

**The rule, in the spread planner's terms (★E1).** For each building and each piece of equipment that fits it:
1. **What it adds a month** (`g`): the building's spread with it, less without it, at the city's own prices: inputs saved, extra output (at the import price up to the shortfall, the export price beyond), posts saved at the band's wage, power, water and upkeep; less the equipment's own running cost and any licence fee (§2.4).
2. **What it costs** (`C`): its machinery at the city's price for `MACHINERY` (made by Machine Works, else imported), its fitting as `BUILDING_WORK`, its design as `ENGINEERING_WORK` (est. 10% of the cost), and an import share for what the city cannot make (electronics, until it has a maker). A refit stops the building for its fitting months, and the output lost is part of `C`.
3. **It pays over its life:** `g × annuity(T, r) ≥ C`, where `T` is the lesser of the equipment's life and the building's remaining life, and `r` the sector's real borrowing rate (`BusinessInvestment.realTestRate`). This is Jerus's long-run rule (2026-10-10), the same as automatic building's.
4. **It passes the money gate** as a building does: `g ≥ 1.25 × C × r ÷ 12` (`PROFIT_OVER_INTEREST`), tested on its whole cost (materials decision A).
5. **It competes with new buildings.** Its earnings on cost (`g ÷ C`) are ranked with the sector's other candidates in `SpreadPlanner.best()`, one order at a time (`MAX_CONCURRENT_ORDERS`).
6. **New buildings come with every piece that pays** at the order (vintages).

**Why diffusion then looks like history without a curve being coded.** Buildings differ in age, so in remaining life; the sector fits one at a time; wages rise as the city grows, so labour-saving equipment starts to pay later in cheap-labour cities; and imported equipment costs more when the currency is weak. Those are the reasons in [MANS61] and [HALL04]: speed rises with profitability and falls with the size of the investment.

**Made at home or imported.** Either, by the city's market for `MACHINERY`, as today. An adoption wave is a wave of machinery demand. If Machine Works cannot meet it, imports rise, the current account worsens and the D$ weakens, which makes the next refit dearer and Machine Works more profitable. Canada imported 63% of its equipment in 1985 [EK01], so a wave mostly leaks abroad unless the city makes machines.

**The city's own buildings.** Hospitals, police and schools are the city's. Their equipment (a CT scanner, a breathing machine, a DNA lab) is fitted by automatic building under its long-run cost per unit served (P2, P2b), or by the player from the building's page, with the same payback in its own terms: "D$ 1.9M; 50 bed-days a month saved; pays back in 6.1 years at today's wages" (est.).

**The verdicts** a sector page prints (mockup 4):
- **fitted**;
- **next: ranks N** (its earnings on cost against the sector's other orders);
- **not worth it yet**: "over its 10-year life it earns back D$ 52k of its D$ 68k; it would pay at wages 30% higher";
- **won't fit**: "4 years left in this furnace; the caster would pay back in 4.0";
- **needs**: "needs a steady run of heats: fit the oxygen converter first".

### 2.3 Buildings
A discovered building joins the build list and the investors' candidate lists. Jerus's rule holds: **a new template only for a new mechanic.** Of the 40 discoveries, the buildings are:
- **ten gated buildings the game already has**: Container Terminal, Desalination Plant, the two Hydrocrackers, Steel Mini-Mill, Wind Farm, Contact Centre, Walk-in Clinic, Assisted Living Residence and Shared Services Centre, which did not exist in 1950 [R2];
- **three the materials batches will add**: Pelletising Plant, Leach & SX-EW Plant, Harvesting Operation;
- **one new template**: a Fulfilment Centre (B8), because online selling moves groceries off shelves into vans, which is new;
- **one more for prizes**: the Research Lab (§5.3), whose income is prizes, which is new.

### 2.4 Licences
**Proposed (★E2): technology from abroad carries a fee while it is under patent.** A patented recipe, equipment or building the city got from the world pays **25% of what it adds** (the 25% rule [ROY]) for **20 years from the world's date** (the patent term, est.), and the fee leaves the city on the current account as IP charges. What the city found itself carries no fee for its own firms.
- **Not patented, so free from the start:** medical procedures (methods of medical treatment are not patentable in Canada), business methods, and law. The catalogue marks each entry (§4).
- **Licence income from abroad** for what the city found first is left out (Q14): real universities earn back about half a percent of what they spend [SC171].

### 2.5 Business and law unlocks
- **Business** unlocks are equipment (the business computer, the cash machine, the barcode scanner), buildings (Contact Centre, Walk-in Clinic, Shared Services Centre, Fulfilment Centre) and one recipe (lean production).
- **Law** unlocks are either **rules the market uses at once** (reorganisation: a business that cannot pay keeps its plant under a stay), or **options the player turns on in Policy** (deposit insurance under Money; electronic monitoring as a sentencing option), or **equipment** (the DNA lab at Police Headquarters).

---

## 3. The 1950 starting set

### 3.1 The principle (★E3)
**`buildings.json` is the destination; 1950 is today's game with the catalogue undone.** Every figure in the game is a 2020s figure, and it is balanced. So a 1950 building is today's building with every catalogued discovery that touches it taken back out:
- the Electric Arc Furnace without the continuous caster makes 10% less steel from the same scrap, with 12.5% more posts and about 1.2 GJ a tonne more fuel [T4];
- the cracking units run on the amorphous catalyst: petrol 55.5% of the feed, not 62.0% [T26];
- general care in 1950 keeps a patient 10.0 days, not 7.3 [SC-B], [CIHI].

A city with everything therefore ends up exactly on today's balanced figures. **What the catalogue leaves out stays at today's level**, so a 1950 city is somewhat more productive than the real one; each sector's page could say how much of the real gap the catalogue covers (est.: US farm labour fell 75% from 1948 to 2015 [ERS], and the catalogue's farm entries cover part of it).

### 3.2 What existed in 1950
Of the game's 101 buildings, **33 existed in roughly today's form, 55 existed but worked differently, and 13 did not exist** [R2]. The ones that worked differently, in short:
- **Industry:** steel came from open hearths (electric furnaces made 2–6% of US steel, mostly alloy) [BLS-55]; cement from wet kilns of 680 t a day burning 0.25–0.30 t of coal a tonne [KILN]; iron came from natural hematite (pellets from 1955) [T12]; machine tools were manual (NC was demonstrated in 1952) [NC]; car plants had no robots [T57].
- **Farms:** US corn yielded 38 bushels an acre against 179 now, and wheat 16.5 against 51.2 [ERS-FG], [ERS-W]; farming was 20.5% of Canadian employment [SC-D].
- **Power:** fossil plants turned 24% of their fuel into power, against 32% for coal today [EIA-A6].
- **Hospitals:** a 10.0-day stay and 1.15–1.28 staff per bed; staff per bed then doubled by 1975, while the stay barely moved until after 1980 [SC-B], [M-S1].
- **Ports and rail:** all general cargo was break-bulk, with 20–22 dockers a hold and loading at about US$5.83 a tonne [CONT]; railways were half-way from steam to diesel [DIESEL].
- **Banks and shops:** ledgers were posted by hand (the first bank computer came in 1959); many grocers still served over the counter [ERMA], [SUPER].

**Some equipment existed in 1950 and was not yet fitted**, which suits the adoption rule without any discovery: the self-propelled combine (1937–40) [T67], diesel locomotives, flash smelting (1949) [T14], catalytic reformers (1949) and fracturing (1949) [R2]. These start known.

### 3.3 The 13 buildings that did not exist
| Building | First real instance | In this study |
|---|---|---|
| Desalination Plant | Kuwait, 1957 (flash distillation); reverse osmosis 1965 test, 1975–77 commercial | E6 |
| Community College | Lethbridge, Alberta, 1957; Ontario's system 1965 | **not research**: a policy choice; available in 1950 as a technical college |
| Small Hydrocracker, Hydrocracker | Standard Oil of California, 1959; large units 1965 | E7 |
| Container Terminal | Ideal-X, April 1956; Elizabeth NJ, 1962; Halifax 1969 | E5 |
| Contact Centre | Birmingham, 1965 (call distribution) | B2 |
| Steel Mini-Mill | Nucor, Darlington SC, 1969 | E13 |
| Walk-in Clinic | United States 1973; Edmonton 1979 | B4 |
| Strategic Reserve | IEA 1974; US reserve 1975 | **not research**: a policy; available in 1950 |
| Light Rail Line | Edmonton, April 1978 | **not research**: available in 1950 as the streetcar line it replaced, with 1950 figures |
| Wind Farm | Crotched Mountain NH, 1980; Canada 1993 | E16 |
| Assisted Living Residence | Portland OR, 1981 | M8 |
| Shared Services Centre | early 1980s (loosely sourced) | B6 |

Sources: [R2] §1, with each row's own source there.

**Close calls the study treats as available in 1950** (Q12): the catalytic reformer (first unit 1949), offshore platforms in water up to 30 m (Kerr-McGee, 1947), self-service filling stations (Winnipeg 1949), diesel locomotive building (1949–50). **Metro Line and Elevated Highway** existed abroad but not in Canada until 1954 and 1962; they stay available.

### 3.4 What else a 1950 start touches (questions, not a redesign)
| Item | 1950 | Today | Source |
|---|---|---|---|
| Canada's GNP per head | C$1,288, about C$16,900 in 2025 dollars | C$77,871 (×4.6 real) | [BOC50], [WB-GDP] |
| Average weekly wage | C$45.08 (NS 39.40), about C$592 in 2025 dollars | about C$1,300 (est.) | [SC-E] |
| CPI (2002=100) | 12.5 | 164.2 (×13.1) | [SC-CPI] |
| Bank Rate | 1.5%, 2.0% from 17 Oct 1950 | | [BOC50] |
| The dollar | pegged at US$0.909, floated 1 Oct 1950; gold at US$35 an ounce under Bretton Woods | | [BOC50] |
| Unemployment | 3.6% | | [SC-D] |
| Oil, US first purchase | US$2.51 a barrel (about US$33 in 2025 money, est.) | US$63–75 | [EIA-OIL] |
| Steel / copper / wheat | US$76.5/t, US$476/t, US$2.00/bu | | [USGS-DS140], [ERS-W] |
| World population / GDP per head | 2.5 billion; US$3,360 (2011 int.$) | 8.1 billion; US$16,677 | [MADDISON] |
| Life expectancy / fertility | men 66.4, women 70.9 / 3.44 | 82.6 / 1.26 | [SC-MT] |
| Women in the labour force | 23.2% | about 82% of women 25–54 | [SC-D] |
| University | 7.0% of 18–21-year-olds enrolled; 17,185 bachelor's degrees | | [SC-W] |
| Oil fields known | Kirkuk, East Texas, Burgan, Leduc (1947), Redwater, Ghawar (1948) | Prudhoe Bay 1967–68, Ekofisk 1969, Hibernia 1979 found later | [R2] §3 |

What this means for the game is §8's Q1 and Q13: wages, prices, the world's figures and whether Earth's later fields are hidden until found.

---

## 4. The catalogue (40)

Each row: its code, the discovery, its kind, history's date, what it touches, the effect from its source, what it needs first, whether it would carry a licence fee (§2.4), and the work it takes at that date (researcher-years, §1.8). Sources for the medical, business and law rows are [M-…], [B-…], [L-…]; for engineering [T…].

### Engineering (the Institute of technology), 18
| | Discovery | Kind | World | Touches | Effect | Needs | Fee | Work |
|---|---|---|---|---|---|---|---|---|
| E1 | Oxygen converter | equipment | Nov 1952, Linz | Steel Shop (open hearths in 1950) | a heat in under 40 min, from 10–12 h; 50–55 Nm³ of oxygen a tonne [T1], [T2] | hot iron from a blast furnace | yes | 444 |
| E2 | Preheater kiln | equipment | 1953, Beckum | Cement Plant, Lime Kiln | fuel 7.0 GJ (wet) to 3.0–3.6 GJ a tonne of clinker [T32], [T34] | dry raw meal | yes | 467 |
| E3 | Jack-up drilling rig | recipe | 1954 | Offshore Platform | deepest water 30 m to 120–150 m [T21] | — | yes | 246 |
| E4 | Taconite pellets | building | 1955, Silver Bay | Pelletising Plant | 30–35% Fe rock to 65% Fe pellets [T12] | a magnetite field | yes | 518 |
| E5 | Shipping container | building | Apr 1956, Ideal-X | Container Terminal | loading US$5.83 to 0.16 a tonne; 1.7 to 30 t an hour a docker; turnaround 3 weeks to 18 h with the gantry crane (1959) [T53], [T54] | a coast | no (est.) | 545 |
| E6 | Flash desalination | building | 1957, Kuwait | Desalination Plant | fresh water from the sea at 13.5–25.5 kWh a m³ [T52] | a coast | yes | 574 |
| E7 | Hydrocracking | building | 1959, SoCal | Small Hydrocracker, Hydrocracker | gas oil into about 107% of its volume as jet and diesel [T30], [T31] | hydrogen (a reformer) | yes | 637 |
| E8 | Welding robot | equipment | 1961, GM | Vehicle Works, Assembly Plant, Commercial Vehicle Plant | torch working 90% of the time, from 30% [T57] | B1 | yes | 706 |
| E9 | Continuous caster | equipment | early 1960s | Steel Shop, Electric Arc Furnace, Mini-Mill | +10–12% steel from the same melt; −10–15% labour; −1 MBtu a ton direct; US$40–80 per tonne a year (1978) [T4] | a steady run of heats | yes | 743 |
| E10 | Semi-dwarf wheat | recipe | 1962, CIMMYT | Grain Farm, Mixed Farm | yields about doubled in five years, with fertiliser [T38] | fertiliser (a new good, Q16) | no | 372 |
| E11 | Zeolite catalyst | recipe | 1964, Mobil | Cracking Unit | petrol 55.5 to 62.0% of the feed; coke 5.6 to 4.1% [T26] | — | yes | 412 |
| E12 | Leaching and electrowinning | building | Mar 1968, Bluebird AZ | Leach & SX-EW Plant | cathode from ore under 1% Cu, 60–70% recovered [T15], [T16] | an oxide field; acid | yes | 1,013 |
| E13 | Scrap mini-mill | building | 1969, Nucor | Steel Mini-Mill | ~400 kWh a tonne from scrap; US$140–200 per tonne a year against ~1,000 integrated [T3], [T6] | E9 | yes | 1,067 |
| E14 | Mechanised harvesting | building | 1973, PIKA 75 | Harvesting Operation | 3.8× the wood a worker of a Logging Camp (the materials study's figures) [T37] | — | yes | 1,312 |
| E15 | Longwall shields | equipment | 1975, US | Underground Coal Mine | 30 to 149 t a face worker a day (1975–89); 60% to 80% of the seam [T17], [T20] | — | yes | 1,455 |
| E16 | Wind farm | building | Dec 1980, Crotched Mountain | Wind Farm | Altamont's 1986 farms ran at about 9% of capacity (est.); projects built 1998–2003 run at 23%, 2013–21 at 40% [T49], [T50] | — | yes | 1,883 |
| E17 | Double-stack rail | equipment | 1984, APL/UP | Freight Line, Rail Terminal | twice the containers a train [T55] | E5 | yes | 2,315 |
| E18 | Thin-slab caster | equipment | Jul 1989, Crawfordsville | Steel Mini-Mill | flat steel from scrap; reheating 2.0 to 0.5–0.8 MBtu a tonne [T10], [T3] | E13, E9 | yes | 2,998 |

### Medicine (the Medical school), 10
Every row changes a mechanism the game has (`Health`, `Sickness`, `CareType`); none is a health multiplier.

| | Discovery | Kind | World | Mechanism in the game | Effect | Needs | Fee | Work |
|---|---|---|---|---|---|---|---|---|
| M1 | TB drugs | recipe (drug) | 1952 | the year-or-more slot of `Sickness`' ring recovers | Canadian TB beds 18,977 to 9,722 in ten years; stays 362 to 224 days [M-M3a], [M-S1] | — | yes | 536 |
| M2 | Breathing machine (ICU) | equipment | Sep 1952, Copenhagen | fewer of the served long-sick die; more `COLLEGE_HEALTH` a bed | respiratory polio deaths 87% to about 22% [M-M5] | — | yes | 428 |
| M3 | Polio vaccine | recipe (drug) | Apr 1955 | summer outbreaks lose their polio share; fewer enter the long-sick pool disabled | US paralytic cases −90% in 5 years, −99.7% in 10; Canada 1953: about 9,000 cases, 494 deaths [M-M1a], [M-M1b] | — | no (not patented; unverified) | 594 |
| M4 | Kidney dialysis | equipment | 1960, Seattle | a death becomes a patient: 3 four-hour sessions a week | about 6 patients a station (est.); US patients 78,000 (1984) to 187,000 (1994) [M-M7c] | M2 | yes | 564 |
| M5 | Hip replacement | recipe | 1962, Wrightington | general-care stays shorter; more recover in senior care | stay about 30 days, 9.1 by 1991, 3.7 by 2008; discharged to care 17.8% to 34.3% [M-M9c] | — | no | 302 |
| M6 | CT scanner | equipment | Oct 1971, London | fewer needless operations: general care's capacity up | per 100 suspected appendicitis, 13 operations and 50 bed-days avoided [M-M13b] | B1 | yes | 824 |
| M7 | Angioplasty | equipment | Sep 1977, Zurich | heart patients back to work sooner; fewer deaths | back at work at 2 months 40% against 10%; deaths 9% to 7% [M-M11b], [M-M12a] | M2 | yes | 1,013 |
| M8 | Assisted living | building | 1981, Portland | a senior who needs help but not a nursing bed | a new senior-care place [R2] | — | no | 1,162 |
| M9 | Keyhole and day surgery | equipment + recipe | 1987, Lyon | 3 fewer days in a bed and 22.5 fewer off work; 35% more operations | laparoscopic share <1% to 92% in ten years (Ontario) [M-M14a], [M-M14b] | M2 | equipment yes | 1,428 |
| M10 | Statins | recipe (drug) | 1987 | coronary deaths and hospital days fall | deaths 11.5% to 8.2% over 5.4 years; hospital days −34% [M-M10b] | — | yes | 1,786 |

Left out of medicine, and why: the measles vaccine (folded into M3's mechanism, 1963), the coronary care unit (folded into M2 and M7), chlorpromazine (the game has no mental hospital; Q17), obstetric ultrasound (no sourced death effect: RR 0.89, not significant [M-M6]).

### Business (the Business school), 8
| | Discovery | Kind | World | Touches | Effect | Needs | Fee | Work |
|---|---|---|---|---|---|---|---|---|
| B1 | Business computer | equipment | Nov 1951, LEO | Commercial Bank, Shared Services, Contact Centre | posting 245 to 33,000 accounts an hour; −25% of the clerks it touches, +29 computer posts an office [M-B1c], [M-B1d] | — | yes | 421 |
| B2 | Call distributor | building | 1965 | Contact Centre | one queue for every line: 23–37% fewer agents for the same calls (est., Erlang C) [M-B6] | B1 | yes | 868 |
| B3 | Cash machine | equipment | Jun 1967, Enfield | Commercial Bank | tellers a branch 20 to 13; branches +43% [M-B3a] | B1 | yes | 962 |
| B4 | Walk-in clinic | building | 1973 | Walk-in Clinic | a doctor without an appointment [R2] | — | no | 656 |
| B5 | Barcode scanner | equipment | Jun 1974, Troy OH | grocery and convenience stores, filling stations | store payroll −5.35%; about 8 years' payback at 1974 prices [M-B4] | B1 | no (open standard, est.) | 1,381 |
| B6 | Shared services | building | early 1980s | Shared Services Centre | one back office for many firms [R2] | B1, B2 | no | 1,044 |
| B7 | Lean production | recipe | 1984, NUMMI | car plants, Machine Works | 26.5 to 19.1 hours a vehicle; inventory 96 to 81 days [T58], [M-B5a] | — | no | 1,158 |
| B8 | Online shop | building | 1995 | Fulfilment Centre (new) | e-commerce 0.6% to 17.1% of US retail in 27 years [M-B9] | B1, B5 | no | 4,086 |

### Law (the Law school), 4: it earns a place
| | Discovery | Kind | World | Touches | Effect | Needs | Fee | Work |
|---|---|---|---|---|---|---|---|---|
| L1 | Deposit insurance | recipe (a Policy dial) | Mar 1967 (CDIC) | the bank: insured deposits do not run | US bank failures 1,350–4,000 a year (1930–33) to 9 (1934) [L-L1a] | — | no | 481 |
| L2 | Reorganisation | recipe (a market rule) | 1978 (Chapter 11; CCAA from the 1980s) | a business that cannot pay keeps its plant under a stay | kept as a going concern 80% of the time against 63–71%; 77¢ on the dollar in rich countries [L-L3a] | — | no | 849 |
| L3 | Electronic monitoring | recipe (a sentencing option) | 1983 | Jail and Penitentiary beds; the offender keeps working | re-arrest 22% to 13%; custody costs 3.9× supervision [L-L6a], [L-L6b] | — | no | 1,099 |
| L4 | DNA evidence | equipment | 1986, Leicestershire | Police Headquarters: detection | reoffending −42% within a year; 1% more detection, over 2% less crime [L-L5a] | B1 | yes | 2,567 |

**What law could add later:** 911 (response time, if police get distances), mortgage-backed securities (landlords' mortgages 0.13–0.24 points cheaper [L-L2b]), university patent ownership (Bayh-Dole, research spawning firms [L-L4b]).

### Reserves, sourced but left out
Pulverised coal injection (early 1960s; 180–200 kg of coal replaces 0.9 kg of coke each) [T11]; supercritical coal (1957; about 40% against 29.9%) [T46], which waits for the electricity run; reverse osmosis (1977; 16 to 2–3 kWh a m³) [T52]; horizontal wells with slickwater fracturing (1997) [T23], which needs Earth's tight fields; the precalciner (late 1960s) [T35]; GM crops (1996; yields +22%) [T44]; bigger wind turbines (0.7 to 3.2 MW, capacity factor 23% to 40%) [T49]; nuclear power (1957; CANDU 1962) [T48].

---

## 5. The player's lever

Jerus: "You just increase the grants and prizes paid, with options to specific stuff, aka engineering or business or medical schools."

### 5.1 Money becomes posts
- **One grant dial and one prize dial per field**: engineering, medicine, business, law.
- **A grant is D$ a month.** It buys posts at the field's post cost (§1.8), up to the room its schools have.
- **Where posts sit:** engineering at the Institute of technology and the University; medicine at the Medical school and the University; business at the Business school and the University; law at the Law school and the University. **A school holds 2.7 posts for each of its university-band staff** (est.): a University 59 for science fields and 16 each for business and law, an Institute of technology 124, a Medical school 135, a Business school 81, a Law school 92 (est., from `buildings.json`'s staff).
- **Who fills them:** the field's bands: `UNIV_HIGHTECH_ENG` and `UNIV_SCIENCE`; `UNIV_DOCTOR` and science; `UNIV_FINANCE` and `UNIV_POLICY`; `UNIV_LAW` and policy. A post is a job in `staffablePostsByBand()`, so a grant raises those bands' premium and pulls graduates to the city (`WageBand.arrivalCeiling()`). That is the second thing a grant buys: skilled jobs.
- **The schools' own staff already research 40% of the time** [UVIC]. A University alone gives 22 researcher-FTE across its fields; nothing is paid for that.
- **A post nobody fills costs nothing.** Unspent grant money stays in the treasury and the dial card says how much.

### 5.2 Researchers become discoveries
1. **Open problems.** A discovery is open to the city's researchers once (a) everything in its `needs` is known, here or abroad, and (b) its science exists: from history's date less the field's lag (engineering 7 years, medicine 9, business and law 4 [MANS91]).
2. **Researchers spread evenly** over their field's open problems. No problem takes more than its effort ÷ its minimum time (a drug can't be found in less than 8 years, however many work on it). Researchers left over when every open problem is full are idle, and the screen says so.
3. **Work piles up.** Each month, a problem gains the researchers on it ÷ 12 researcher-years.
4. **A hidden size.** Each problem's size is its effort (§1.8) times a draw from the city's seed (§6.3). When the pile reaches it, the city has the discovery. The player never sees the size.
5. **The world.** On history's date the world has it, and so does the city, from abroad. The city's pile on it stops (§7).

**Why it stays a mechanism:** money buys posts; posts are filled by people from the labour market; the people work a month at a time on problems with a fixed amount of work in them. Nothing multiplies a chance.

### 5.3 Prizes
- **The dial** is D$ per discovery made here in that field. The prize goes to whoever makes it: a school (it joins that field's grant pot, so it buys posts) or a lab.
- **A Research Lab** (new, Business Services; ★R1): a building with 100 research posts of one field (est.), D$ 12M to build (est., from the Engineering Services Office's D$ 10.9M for 120 staff), running at the field's post cost. Its income is prizes. **Investors build one while the purse pays its posts:** for the cheapest open problem it could finish before the world at its team's limit, the prize must be at least the problem's remaining typical work × a researcher-year's cost. A lab that has nothing left to chase idles, then is shed, as any building is.
- **So a prize is what the evidence says it is:** it pays only on success; it brings in outsiders and their money; it suits near targets, because labs go for the cheapest problem; and it pays for discoveries the city's own grants would have made too.

### 5.4 What money buys: the two cities
**A young city, June 1955:** about 38,000 people (illustrative), one University (1953), no grants yet. Its staff give 9 researcher-FTE each to engineering and medicine and 2 each to business and law. Engineering has 6 open problems, medicine 2, business and law none. Eight discoveries have reached it from abroad.

| Staged | Researchers | Chance of a find here before the world, next 10 years |
|---|---|---|
| Engineering grant 0 → D$ 0.55M | 9 → 39 | none → under 1% (six open problems share 39 people) |
| Medical grant 0 → D$ 0.70M | 9 → 38 | 0.2% → **25%** (two small open problems) |
| Engineering prize 0 → D$ 140M | labs 0 → 200 | about 1% → **75%**: labs go for the cheapest problem |

**A big city decades later, June 1979:** about 420,000 people; two Universities, two Institutes of technology, a Medical school, a Business school and a Law school (1977). Grants: engineering D$ 6.5M, medicine D$ 4.5M, business D$ 0.9M a month (D$ 11.9M, about 0.6% of GDP at Canada's income per head, est.; Canada's universities spend about 0.6%). It has found four discoveries here: angioplasty (February 1973, 4.6 years before the world), longwall shields (July 1973, 1.9), assisted living (April 1977, 4.2) and the wind farm (June 1979, 1.5). Twenty-seven more came from abroad.

| Field | Researchers | Room | Open problems | Expected finds here, next 10 years |
|---|---|---|---|---|
| Engineering | 412 | 367 posts | 1 (rail) | 1.10 |
| Medicine | 227 → **290** (D$ 4.5M → 6.0M, the room's limit) | 254 | 2 (hospitals, clinics) | 0.81 → **1.06** |
| Business | 90 → **130** (D$ 0.90M → 1.38M, the room's limit) | 113 | 1 (offices) | 0.05 → **0.18** |
| Law | 18 (faculty only) | 124 | 1 (jails) | 0 |
| Medical prize 0 → D$ 350M | labs 0 → 130 | | | medicine 0.81 → **1.25**; expected purse paid D$ 440M over ten years |
| All, as is → staged | | | | **1.96 → 2.33** |

Cutting engineering's grant to nothing drops the city's expected finds from 1.96 to 0.90. Researchers per 1,000 workers: about 3.6, Canada's 1981 level for all sectors [MSTI].

### 5.5 Where it lives
**A sixth Policy area, Research (★R2),** after Taxes, Wages, Money, Promises and Zoning. It is not a Promise: Promises are transfers to households, and research has its own picture and its own results. Its pages:
- **Overview:** researchers at work by field, money a month, what is in reach, discoveries so far here and from abroad.
- **Funding:** the eight dial cards (mockup 1).
- **Found:** what has been discovered (mockup 3).

Elsewhere: each school's page under Services › Education shows its research posts (filled, room, field); City History draws a flag at every discovery; the sector pages show equipment (mockup 4); the build list marks new buildings (mockup 5).

### 5.6 No funding at all
The city still has every discovery on history's date, from abroad, under licence. Its schools' staff still research 40% of the time, so a city with a Medical school may find a small medical recipe first now and then. Nothing breaks and nothing waits: that is how most real small economies get their technology [KELLER].

---

## 6. Randomness without a tree

Jerus: "There is no tech tree you can click." And: "just randomly: oh, the tech institute researchers in university came up with this new tech."

### 6.1 A hidden order: yes (★R3)
Two things order discoveries, both real:
- **What it needs first**, which may cross fields: the CT scanner needs the business computer; the mini-mill needs the continuous caster; dialysis, angioplasty and keyhole surgery need the breathing machine.
- **The science under it**, which exists only some years before history's date (§5.2). This is why a big city is early by years, not decades: in June 1979 the big city's engineers had one problem in reach, because the next one's science did not exist yet.

### 6.2 What the player sees, so it does not feel arbitrary
- **Researchers at work**, by field and by school, and the money a month (mockup 1).
- **How many problems each field has in reach**, and **which sectors they touch**, without naming them: "1 problem in reach: rail" (★R4). A field with none says so: "nothing in reach: what they'd work on needs ideas nobody has yet."
- **Idle researchers** when every open problem is full.
- **What to expect**: each dial card's preview, as a chance or a number of finds over ten years, using the size's distribution and never its draw (mockup 1).
- **Every discovery on a timeline**: found here (with how far ahead of the world) or from abroad (mockup 3).
- **The inbox** says when one arrives, here or from abroad (mockup 2).

### 6.3 Reproducible
- **A problem's size** is drawn once from the city's seed and the discovery's id, with `World.mix()`, the SplitMix64 finaliser the world already uses. It is a fact about the city, so a reload draws the same size and nothing about it needs saving.
- **The save carries** each open problem's pile (researcher-years), each discovery's month and source (here or abroad), each building's fitted equipment, and each lab's field. Recipes in use are re-derived each month from prices, which the save already carries.
- **Nothing is rolled in the month.** Given the same funding, a seed finds the same things in the same months, so test runs can compare seeds, and a discovery cannot be save-scummed.

---

## 7. The world

**Recommendation (★W1): the world follows history's dates.** On each discovery's real date the city has it from abroad: buildings to build, recipes to run, equipment to buy, under licence while patented (§2.4). A city that funds research can get there first, by up to the field's lag, and its firms then pay no fee. A city that doesn't still gets everything on history's date. This matches the evidence (§1.7), keeps the real Earth's timeline, and means no city is ever stuck in 1950.

**The alternatives, rejected:** the world discovers nothing (a city without research is stuck in 1950, a wall); the world discovers at random (history's dates are the point of a real-Earth game); arrivals bring technology with them (the game already pulls licensed professionals by wages; that is how skills arrive, not technology).

**Through what the city gets the world's discoveries:** buildings and recipes by licence; equipment by importing it (or `MACHINERY` from Machine Works, with the licence fee); medicine through the city's clinics and hospitals the month it is known.

**What history's dates cannot do (Q2).** The catalogue ends in the 1990s and real history around now. The game runs for centuries, and its cities grow slowly: the playtest's city has 4,000–12,000 people at month 611 (November 2000 on a 1950 calendar) and 17,000–30,000 at month 1,070 (2039), by 0.7.102's and 0.7.103's road rules; automatic building's cities pass 10,000 people between months 345 and 824 (P2b's table). So most cities live through history as importers and are big enough to research only after history has run out. Options: stop at the present (research then has nothing left); add a near-future list from today's pilots and roadmaps with projected dates (hydrogen steel, small reactors, carbon capture in cement), marked as projections; or let the world keep its measured pace, which needs discoveries nobody has made yet. The recommendation is the second, for 2025–2060, with Jerus deciding what follows.

---

## 8. Questions for Jerus

**The three that matter most:**

1. **The 1950 economy.** With today's wages, 1950's buildings lose money and every labour-saving machine pays at once, and with 1950's, the game's whole calibration moves. Real 1950 wages were about 45% of today's in Canada (est., [SC-E]) and income per head about 22% [WB-GDP]. **Recommendation:** a separate study of the world in 1950 before research is built: the city's founding wages at 1950's level, the world's wages and prices following history in constant founding dollars, and wages that rise because discoveries raise what a worker makes. Until then research can't be balanced.
2. **The world, and after history.** **Recommendation:** the world follows history's dates (§7), and the catalogue is extended with near-future projections for 2025–2060, marked as such. What comes after 2060 is yours to decide. **Decided (2026-10-10): the game starts in 1900** (the box at the top), so cities grow up while history is still being made; the near-future list still follows 2025.
3. **Recipes as data, in the materials batches.** **Recommendation:** yes. The materials batches write each building's inputs, outputs and posts as a `recipes` list (§2.1), with `since` on anything after 1900, the start (the box at the top), so research later is mostly data.

**The rest:**

4. **Grants buy posts at the field's real cost** (§5.1), filled from the labour market. **Recommendation:** yes; this is the brief's point 2 and the evidence's.
5. **The schools' staff research 40% of the time for free** (§5.1). **Recommendation:** yes [UVIC]; it means a city with a Medical school finds a small medical recipe now and then with no grant.
6. **Room for posts: 2.7 per university-band staff member** (est.). **Recommendation:** yes, so a big research city needs several schools, as real ones have.
7. **The effort figures** (§1.8: 400 / 200 / 500 researcher-years at 1950, growing 5.3% or 3.5% a year). **Recommendation:** use them; they give a big city about two finds a decade. If that feels slow, lower the 1950 efforts, not the growth.
8. **How far ahead of the world a city can be** (engineering 7 years, medicine 9, business and law 4). **Recommendation:** Mansfield's lags as given.
9. **Researchers spread evenly over open problems.** The alternative is weighting by what the city's businesses would gain. **Recommendation:** evenly; Myers finds funding barely redirects researchers [MYERS], and prizes are the tool for aiming.
10. **Prizes draw Research Labs** (a new building in Business Services; §5.3). **Recommendation:** yes; it is the one new template, and its mechanic (income from prizes) is new.
11. **Licence fees: 25% of what a patented foreign technology adds, for 20 years from history's date** (§2.4). **Recommendation:** yes; it is what makes researching at home worth something in cash, and it puts real IP charges on the balance of payments.
12. **Brand-new-in-1950 technology** (the catalytic reformer, offshore platforms to 30 m, self-service stations, diesel locomotives): available at the start. **Recommendation:** available.
13. **Earth's oil fields found after 1950** (Prudhoe Bay, the North Sea, Hibernia). **Recommendation:** a later question for the Earth batches: whether a field is unknown until its real discovery date. It is not research.
14. **Licence income from abroad** for what the city found first. **Recommendation:** not now; real universities earn back about half a percent of research spending [SC171].
15. **Businesses' own R&D** (two-thirds of real researchers). **Recommendation:** not in the first build; prizes and labs stand in for it.
16. **Semi-dwarf wheat needs fertiliser,** a good the game doesn't have. **Recommendation:** add `FERTILISER` with the recipe, imported until the materials or a chemicals chain makes it.
17. **Medicine's mechanisms.** The game has one general-care coverage and no causes of death. **Recommendation:** map each medical discovery to the constants it moves (§4's column), and leave causes of death and a mental hospital (for chlorpromazine) for a health study.
18. **Show which sectors the open problems touch** (§6.2). **Recommendation:** yes; it shows the work is aimed at something without showing the tree.
19. **A sixth Policy area, Research** (§5.5). **Recommendation:** yes, rather than a page under Promises.
20. **The Community College, Light Rail and the Strategic Reserve:** available in 1950 as a technical college, a streetcar line and a stock of oil the city chooses to keep. **Recommendation:** yes; none is a research discovery.

---

## 9. For the implementer

**Order, after every planned batch:**
1. **R0, the 1900 economy** (Q1's study, now for 1900: the box at the top) and the calendar's epoch to 1900 (`CityCalendar.EPOCH_YEAR`). `claude/research-1900.md` §12 replaces this order with its own (R0, R0b, R0c, then R1–R5).
2. **R1, recipes and equipment as data:** `buildings.json`'s `recipes` and `equipment`; a building runs its best known recipe; the spread planner ranks refits with buildings; the city's buildings get a Fit action. No discovery yet: everything known at the start (1900) is in, later ones absent.
3. **R2, the catalogue and the world:** the 40 entries as data (`research.json`), history's dates, licence fees on the current account, the start's figures derived by undoing each entry, 1900–1950's included (§3.1).
4. **R3, posts and discoveries:** research posts at schools, the grant dials, open problems, piles, hidden sizes, the inbox notices, Policy › Research.
5. **R4, prizes and the Research Lab.**
6. **R5, the screens:** Found, equipment on sector pages, new-building cards, History's flags.

**Save:** each open problem's pile; each discovery's month and source; each building's fitted equipment; each lab's field; the eight dials. Sizes are not saved (§6.3). Old saves need not load until the game is published (Jerus, 2026-10-10).

**Harnesses:**
- a discovery's month is the same across a reload and across two runs of a seed;
- a field's researchers equal its faculty FTE plus its filled posts, and posts never exceed room or money;
- with no grants and no prizes, every discovery arrives on history's date;
- a building with everything known runs exactly today's `buildings.json` figures;
- equipment is never fitted when it fails §2.2's rule, and a refit order is ranked with the sector's buildings;
- licence fees appear on the current account and stop 20 years after history's date;
- the young and big cities of §5.4 reproduce the study's previews within their Monte Carlo error.

---

## 10. The mockups
Static HTML, one file each, in `claude/mockups-research/`, in the oil and materials mockups' frame at 1,389 × 868. The young city is June 1955; the big city June 1979. Figures come from the model (§5.4) and the sources; ones marked (est.) or "illustrative" in a page's notes are drawn to show the screen, not measured.

| File | Shows |
|---|---|
| `1-funding.html` | Policy › Research › Funding: the four fields as grant and prize dial cards with before → after rows; both cities. |
| `2-discovery-arriving.html` | The inbox open on a discovery: the polio vaccine from abroad (young city), and the wind farm found by the Institute of technology (big city). |
| `3-what-was-found.html` | Policy › Research › Found: the timeline by field, recipes in use, equipment fitted with each sector's verdict, and four graphs; both cities. |
| `4-equipment-on-a-sector.html` | Sectors › Retail (barcode scanners: fitted, next, won't fit) and Sectors › Iron & Steel (the continuous caster against a new mini-mill), big city. |
| `5-new-building-card.html` | Build cards marked NEW: a Pelletising Plant from abroad (young city) and a Wind Farm found here (big city). |

---

## 11. Sources

**R&D economics** (pass R1)
- [MSTI] OECD, *Main Science and Technology Indicators*, extracted 2026-10-10. https://sdmx.oecd.org/public/rest/data/OECD.STI.STP,DSD_MSTI@DF_MSTI,/
- [TOE] OECD, *GERD by sector of performance and type of expenditure*, 2026. https://sdmx.oecd.org/public/rest/data/OECD.STI.STP,DSD_RDS_GERD@DF_GERD_TOE,/
- [WAGE] OECD, *Average annual wages*, 2026. https://sdmx.oecd.org/public/rest/data/OECD.ELS.SAE,DSD_EARNINGS@AV_AN_WAGE,/
- [WB-PAT] World Bank WDI (WIPO), patent applications by residents. https://data.worldbank.org/indicator/IP.PAT.RESD
- [WB-IP] World Bank WDI (IMF BoP), charges for the use of intellectual property. https://data.worldbank.org/indicator/BM.GSR.ROYL.CD
- [SCC6] Science Council of Canada, Special Study No. 6, *Background Studies in Science Policy*, 1969. https://www.uottawa.ca/research-innovation/sites/g/files/bhrskd326/files/2022-08/special_study_no._6_-_background_studiesd_in_science_policy.pdf
- [JONES] C.I. Jones, data appendix to "Sources of U.S. Economic Growth in a World of Ideas", 2000. https://web.stanford.edu/~chadj/Sources50.asc
- [FRED] Federal Reserve Bank of St Louis: CE16OV, GDPDEF. https://fred.stlouisfed.org/
- [RCC] Canada Research Coordinating Committee, Annex II, 2024. https://www.canada.ca/en/research-coordinating-committee/corporate/annual-progress-summary/annex-2.html
- [NSERC-DRR] NSERC, *Departmental Results Report 2019–20*. https://nserc-crsng.canada.ca/en/plans-priorites-et-rendement/departmental-results-report-2019-20/details-transfer-payment-programs
- [CIHR] CIHR, *Project Grant: Fall 2024 results*. https://cihr-irsc.gc.ca/e/54191.html
- [UVIC] University of Victoria Faculty Association, "Teaching and research" (40/40/20 under the collective agreement). https://www.uvic.ca/bargaining/faculty-association/statistics/teaching-research/index.php
- [HGH] Hall, Griliches & Hausman, "Patents and R and D: Is There a Lag?", *IER* 27(2), 1986. https://eml.berkeley.edu/~bhhall/papers/HGH86_patents_rnd_lag_IER.pdf
- [MANS91] Mansfield, "Academic Research and Industrial Innovation", *Research Policy* 20(1), 1991. https://www.edegan.com/pdfs/Mansfield%20(1991)%20-%20Academic%20Research%20and%20Industrial%20Innovation.pdf
- [MANS61] Mansfield, "Technical Change and the Rate of Imitation", *Econometrica* 29(4), 1961 (coefficients unverified). https://www.jstor.org/stable/1911817
- [DIMASI] DiMasi, Grabowski & Hansen, *J. Health Economics* 47, 2016; Tufts CSDD briefing 2014. https://www.keionline.org/wp-content/uploads/2017/10/Tufts_CSDD_briefing_on_RD_cost_study_-_Nov_18,_2014..pdf
- [TOOLE] Toole, *Research Policy* 41(1), 2012. https://www.zew.de/PU69038-1
- [ADAMS] Adams, "Fundamental Stocks of Knowledge and Productivity Growth", *JPE* 98(4), 1990. https://ideas.repec.org/a/ucp/jpolec/v98y1990i4p673-702.html
- [BJVW] Bloom, Jones, Van Reenen & Webb, "Are Ideas Getting Harder to Find?", *AER* 110(4), 2020. https://www.nber.org/papers/w23782
- [KORTUM] Kortum, "Equilibrium R&D and the Patent–R&D Ratio", *AER P&P* 83(2), 1993. https://bpb-us-w2.wpmucdn.com/campuspress.yale.edu/dist/c/4257/files/2023/09/Equilibrium-RD-and-the-Patent-RD-Ratio_US-Evidence.pdf
- [JW] Jones & Williams, NBER WP 7283; *J. Economic Growth* 5(1), 2000. https://www.nber.org/papers/w7283
- [JL] Jacob & Lefgren, *J. Public Economics* 95, 2011. https://www.nber.org/papers/w13519
- [AGM] Azoulay, Graff Zivin & Manso, *RAND J. Econ.* 42(3), 2011. https://www.nber.org/papers/w15466
- [MYERS] Myers, "The Elasticity of Science", *AEJ: Applied* 12(4), 2020. https://www.kylemyers.org/_files/ugd/73b908_0a793223c0a644bc8ce519636abdc456.pdf
- [WIL] Williams, "Innovation inducement prizes", *JPAM* 31(3), 2012. https://dspace.mit.edu/handle/1721.1/85845
- [BLN] Brunt, Lerner & Nicholas, "Inducement Prizes and Innovation", *J. Industrial Economics* 60(4), 2012. https://img.nber.org/conferences/2008/si2008/DAE/lerner.pdf
- [KLS] Kremer, Levin & Snyder, NBER WP 26775, 2020. https://www.nber.org/papers/w26775
- [LONG] Wikipedia, "Longitude rewards". https://en.wikipedia.org/wiki/Longitude_rewards
- [DARPA] Wikipedia, "DARPA Grand Challenge". https://en.wikipedia.org/wiki/DARPA_Grand_Challenge
- [XP] Wikipedia, "Ansari X Prize". https://en.wikipedia.org/wiki/Ansari_X_Prize
- [CM] Comin & Mestieri, NBER WP 19010; *AEJ: Macro* 10(3), 2018. https://www.nber.org/papers/w19010
- [HCCTA] Comin & Hobijn, *Historical Cross-Country Technology Adoption dataset*, NBER. https://www.nber.org/hccta/hcctad.txt
- [HALL04] Hall, "Innovation and Diffusion", NBER WP 10212, 2004. https://www.nber.org/papers/w10212
- [SUTCH] Sutch, NBER WP 14141, 2008. https://www.nber.org/papers/w14141
- [KELLER] Keller, "International Technology Diffusion", *JEL* 42(3), 2004. https://www.nber.org/papers/w8573
- [EK99] Eaton & Kortum, "International Technology Diffusion: Theory and Measurement", *IER* 40(3), 1999 (via [KELLER]).
- [EK01] Eaton & Kortum, "Trade in Capital Goods", *EER* 45(7), 2001. https://www.nber.org/papers/w8070
- [SC171] Statistics Canada, Table 27-10-0171-01. https://www150.statcan.gc.ca/t1/tbl1/en/tv.action?pid=2710017101
- [ROY] Wikipedia, "Royalty payment" (citing Goldscheider, Jarosz & Mulhern, *les Nouvelles*, 2002). https://en.wikipedia.org/wiki/Royalty_payment

**1950** (pass R2; every row of §3.3 has its own source there)
- [SC-B] Statistics Canada, *Historical Statistics of Canada*, Section B (hospitals). https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionb/4147437-eng.htm
- [SC-D] Section D (labour force). https://www150.statcan.gc.ca/n1/pub/11-516-x/sectiond/4057750-eng.htm
- [SC-E] Section E (wages), series E49-59. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectione/4147438-eng.htm
- [SC-W] Section W (education). https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionw/4147445-eng.htm
- [SC-CPI] Statistics Canada, Table 18-10-0005-01. https://www150.statcan.gc.ca/t1/tbl1/en/tv.action?pid=1810000501
- [SC-MT] Statistics Canada, *Megatrends*. https://www150.statcan.gc.ca/n1/pub/11-630-x/11-630-x2016002-eng.htm
- [CIHI] CIHI, *Hospital stays in Canada 2023–2024*. https://www.cihi.ca/en/hospital-stays-in-canada-2023-2024
- [BOC50] Bank of Canada, *Annual Report 1950*. https://oar-rao.bank-banque-canada.ca/record/2631/files/BOC_Annual_Report_1950.pdf
- [WB-GDP] World Bank, GDP per capita, Canada. https://data.worldbank.org/indicator/NY.GDP.PCAP.CN?locations=CA
- [MADDISON] Maddison Project Database 2023. https://www.rug.nl/ggdc/historicaldevelopment/maddison/releases/maddison-project-database-2023
- [EIA-A6] EIA, Total Energy, Table A6 (heat rates). https://www.eia.gov/totalenergy/data/browser/?tbl=TA6
- [EIA-OIL] EIA, US crude oil first purchase price. https://www.eia.gov/dnav/pet/hist/LeafHandler.ashx?n=PET&s=F000000__3&f=A
- [USGS-DS140] USGS, *Historical Statistics for Mineral and Material Commodities*. https://www.usgs.gov/centers/national-minerals-information-center/historical-statistics-mineral-and-material-commodities
- [ERS], [ERS-FG], [ERS-W] USDA ERS: agricultural productivity 1948–2015; feed grains and wheat yearbooks. https://www.ers.usda.gov/amber-waves/2018/march/agricultural-productivity-growth-in-the-united-states-1948-2015 ; https://www.ers.usda.gov/data-products/feed-grains-database/feed-grains-yearbook-tables ; https://www.ers.usda.gov/data-products/wheat-data
- [BLS-55] BLS, *Man-hours per unit of output, basic steel industry, 1939–55*. https://fraser.stlouisfed.org/title/man-hours-per-unit-output-basic-steel-industry-1939-55-4495/fulltext
- [KILN] Wikipedia, "Cement kiln". https://en.wikipedia.org/wiki/Cement_kiln
- [NC] Wikipedia, "History of numerical control". https://en.wikipedia.org/wiki/History_of_numerical_control
- [CONT] Wikipedia, "Containerization". https://en.wikipedia.org/wiki/Containerization
- [DIESEL] Wikipedia, "Dieselisation". https://en.wikipedia.org/wiki/Dieselisation
- [ERMA] Wikipedia, "Electronic Recording Machine, Accounting". https://en.wikipedia.org/wiki/Electronic_Recording_Machine,_Accounting
- [SUPER] Wikipedia, "Supermarket". https://en.wikipedia.org/wiki/Supermarket
- [R2] The 1950 baseline research file, with all 101 buildings and their sources: `R2-1950-baseline.md` in `research-sources.zip`.

**Engineering** (pass R3; numbering kept)
- [T1] Wikipedia, "Basic oxygen steelmaking". https://en.wikipedia.org/wiki/Basic_oxygen_steelmaking
- [T2] Worrell et al., LBNL-4779E, 2010. https://eta-publications.lbl.gov/sites/default/files/lbnl-4779e.pdf
- [T3] Stubbles, *Energy Use in the U.S. Steel Industry*, US DOE, 2000. https://www1.eere.energy.gov/manufacturing/resources/steel/pdfs/steel_energy_use.pdf
- [T4] US Congress OTA, *Benefits of Increased Use of Continuous Casting*, 1979. https://www.princeton.edu/~ota/disk3/1979/7902/790203.PDF
- [T6] Wikipedia, "Electric arc furnace". https://en.wikipedia.org/wiki/Electric_arc_furnace
- [T10] IspatGuru, "Thin Slab Casting and Rolling". https://www.ispatguru.com/?p=3003
- [T11] IspatGuru, "Pulverized Coal Injection in a Blast Furnace". https://www.ispatguru.com/pulverized-coal-injection-in-a-blast-furnace/
- [T12] Wikipedia, "Taconite"; "Mesabi Range". https://en.wikipedia.org/wiki/Taconite
- [T14] Wikipedia, "Flash smelting". https://en.wikipedia.org/wiki/Flash_smelting
- [T15] Mining Foundation of the Southwest, "Kenneth Power". https://miningfoundationsw.org/Kenneth_Power
- [T16] Wikipedia, "Heap leaching"; "Solvent extraction and electrowinning". https://en.wikipedia.org/wiki/Heap_leaching
- [T17] Barczak, *The History and Future of Longwall Mining in the United States*, US Bureau of Mines IC 9316, 1992. https://stacks.cdc.gov/view/cdc/236126
- [T20] Wikipedia, "Longwall mining". https://en.wikipedia.org/wiki/Longwall_mining
- [T21] Wikipedia, "Jackup rig"; "Oil platform". https://en.wikipedia.org/wiki/Jackup_rig
- [T23] Wikipedia, "Fracking in the United States". https://en.wikipedia.org/wiki/Fracking_in_the_United_States
- [T26] Sadeghbeigi, *Fluid Catalytic Cracking Handbook*, 2000, as in Wikipedia, "Fluid catalytic cracking". https://en.wikipedia.org/wiki/Fluid_catalytic_cracking
- [T30] The Petro Solutions, "History of Hydroprocessing". https://thepetrosolutions.com/history-hydroprocessing-hydrotreating-hydrocracking/
- [T31] UOP Isocracking example, as cited in R3 §R2.
- [T32] Worrell, Kermeli & Galitsky, *ENERGY STAR Guide for Cement Making*, US EPA, 2013. https://www.energystar.gov/
- [T34] NRCan, *Energy Consumption Benchmark Guide: Cement Clinker Production*, 2001. https://prod-natural-resources.azure.cloud.nrcan-rncan.gc.ca/
- [T35] Moore, cementkilns.co.uk, "Suspension preheater kilns"; "Precalciner kilns". https://cementkilns.co.uk/suspension_preheater_kilns.html
- [T37] Wikipedia, "Harvester (forestry)"; "Feller buncher". https://en.wikipedia.org/wiki/Harvester_(forestry)
- [T38] Wikipedia, "Green Revolution"; "IR8". https://en.wikipedia.org/wiki/Green_Revolution
- [T44] GM crops meta-analysis (2014), as cited in R3 #23.
- [T46] ASME Landmark #228, "Philo 6". https://www.asme.org/about-asme/who-we-are/engineering-history/landmarks/228-philo-6-steam-electric-generating-unit
- [T48] Shippingport and CANDU sources, as cited in R3 #27.
- [T49] Wiser, Bolinger et al., *Land-Based Wind Market Report: 2023 Edition*, LBNL. https://www.energy.gov/sites/default/files/2023-08/land-based-wind-market-report-2023-edition.pdf
- [T50] Wikipedia, "Wind power in the United States". https://en.wikipedia.org/wiki/Wind_power_in_the_United_States
- [T52] Wikipedia, "Desalination"; "Reverse osmosis". https://en.wikipedia.org/wiki/Desalination
- [T53] Levinson, *The Box*, 2006; Bernhofen, El-Sahli & Kneller, Lund WP 2013:4.
- [T54] ASME Landmark #85, "PACECO Container Crane". https://www.asme.org/about-asme/engineering-history/landmarks/85-paceco-container-crane
- [T55] Wikipedia, "Double-stack rail transport". https://en.wikipedia.org/wiki/Double-stack_rail_transport
- [T57] Wikipedia, "Unimate"; "Robot welding". https://en.wikipedia.org/wiki/Unimate
- [T58] Krafcik, "Triumph of the Lean Production System", *Sloan Management Review* 30(1), 1988. https://www.lean.org/downloads/MITSloan.pdf
- [T67] Wikipedia, "Combine harvester". https://en.wikipedia.org/wiki/Combine_harvester

**Medicine, business and law** (pass R4; numbering kept)
- [M-S1] Statistics Canada, *Historical Statistics of Canada*, Section B. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionb/4147437-eng.htm
- [M-M1a] CDC, *Pink Book*, ch. 18, Poliomyelitis. https://www.cdc.gov/pinkbook/hcp/table-of-contents/chapter-18-poliomyelitis.html
- [M-M1b] *The Canadian Encyclopedia*, "Canada and the Development of the Polio Vaccine". https://thecanadianencyclopedia.ca/en/article/canada-and-the-development-of-the-polio-vaccine
- [M-M3a] Library and Archives Canada, "The end of the sanatorium era". https://epe.lac-bac.gc.ca/100/205/301/ic/cdc/tuberculosis/tbhistory/sanatoriums/end.html
- [M-M5] Wendel Garcia et al., *Critical Care* 24:211, 2020. https://pmc.ncbi.nlm.nih.gov/articles/PMC7209973
- [M-M6] Whitworth, Bricker & Mullan, Cochrane CD007058, 2015. https://www.cochrane.org/evidence/CD007058_routine-compared-selective-ultrasound-early-pregnancy
- [M-M7c] US GAO, HEHS-96-33, 1996. https://www.govinfo.gov/content/pkg/GAOREPORTS-HEHS-96-33/html/GAOREPORTS-HEHS-96-33.htm
- [M-M9c] Cram et al., *JAMA* 305(15), 2011. https://www.sciencedaily.com/releases/2011/04/110419164205.htm
- [M-M10b] ACC, "4S". https://www.acc.org/latest-in-cardiology/clinical-trials/2010/02/22/18/14/4s
- [M-M11b] ACC, "RITA". https://www.acc.org/latest-in-cardiology/clinical-trials/2010/02/23/19/16/rita
- [M-M12a] Keeley, Boura & Grines, *Lancet*, 2003. https://www.qxmd.com/r/12517460
- [M-M13b] Rao et al., *NEJM* 1998. https://www.reliasmedia.com/articles/34336-effect-of-ct-of-the-appendix-on-treatment-of-patients-and-use-of-hospital-resour
- [M-M14a] Keus et al., Cochrane CD006231, 2006. https://www.cochrane.org/evidence/CD006231
- [M-M14b] Urbach & Stukel, *CMAJ* 172(8), 2005. https://www.cmaj.ca/content/172/8/1015
- [M-B1c] SRI International, "Banking automation: ERMA". https://www.sri.com/hoi/banking-automation-erma/
- [M-B1d] BLS Bulletin 1276, *Adjustments to the Introduction of Office Automation*, 1960. https://fraser.stlouisfed.org/files/docs/publications/bls/bls_1276_1960.pdf
- [M-B3a] Bessen, "Toil and Technology", IMF *F&D* 52(1), 2015. https://www.imf.org/external/pubs/ft/fandd/2015/03/bessen.htm
- [M-B4] Basker, "Raising the Barcode Scanner", *AEJ: Applied* 4(3), 2012. https://www.nber.org/papers/w17825
- [M-B5a] Chen, Frank & Wu, *Management Science* 51(7), 2005. https://ideas.repec.org/a/inm/ormnsc/v51y2005i7p1015-1031.html
- [M-B6] Wikipedia, "Automatic call distributor". https://en.wikipedia.org/wiki/Automatic_call_distributor
- [M-B9] FRED, ECOMPCTSA (US Census). https://fred.stlouisfed.org/series/ECOMPCTSA
- [L-L1a] FDIC, *A Brief History of Deposit Insurance in the United States*, 1998. https://www.fdic.gov/bank/historical/brief/brhist.pdf
- [L-L2b] Sherlund, Federal Reserve FEDS 2008-01. https://www.federalreserve.gov/pubs/feds/2008/200801/index.html
- [L-L3a] Djankov, Hart, McLiesh & Shleifer, "Debt Enforcement Around the World", *JPE* 2008. https://www.nber.org/papers/w12807
- [L-L4b] US GAO, RCED-98-126, 1998. https://www.gao.gov/assets/rced-98-126.pdf
- [L-L5a] Anker, Doleac & Landersø, *AEJ: Applied* 13(4), 2021. https://www.aeaweb.org/articles?id=10.1257/app.20190207
- [L-L6a] Di Tella & Schargrodsky, *JPE* 121(1), 2013. https://www.nber.org/papers/w15602
- [L-L6b] Public Safety Canada, *Corrections and Conditional Release Statistical Overview 2019*. https://www.publicsafety.gc.ca/cnt/rsrcs/pblctns/ccrso-2019/index-en.aspx

**The four research files**, with every source and the arithmetic behind each (est.), went with this study as `research-sources.zip` (R1 R&D economics, R2 the 1950 baseline, R3 engineering, R4 medicine, business and law). They are not in the project, which is near its knowledge limit.

**Licensing of shipped data:** World Bank WDI is CC BY 4.0, Statistics Canada is under its Open Licence, OECD data has been CC BY 4.0 since 2024 (R1 §10, to confirm); history's dates and figures from papers are facts used with citation. Nothing here needs a licence fee.
