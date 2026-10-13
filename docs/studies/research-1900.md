# Research from 1900: the 1900 study

*2026-10-11. The follow-up to `claude/research-design.md` after Jerus decided, on 2026-10-10, that the game starts in 1900 ("I think its a great idea, cause it also slowly introduces mechanics"). That study built research for a 1950 start. This one finds what the city looked like in 1900, how it worked, the 1900 economy, the world from 1900 to 1950, when each Policy lever first existed, and the discoveries that turned 1900 into 1950. Four research passes ran on 2026-10-10: (1) energy, oil and transport; (2) materials, farms and food; (3) people and services, with medicine, business and law; (4) the economy, the world and policy. Every figure below is theirs or the game's, cited by key to §13. "(est.)" marks a figure derived here, with its arithmetic. No game code was written or changed; the code was read at 0.7.108 (`2048Israel/CityBuilderSim`, main, 9ec1d5f). Nothing here is built until every planned batch is done, as with the first study.*

*The model is `claude/research-model/`, extended: `catalogue_1900.py` holds the 48 new entries and `scenarios_1900.py` runs the comparisons in §10.*

---

## 0. In one page

### What a 1900 city is
- **The build list is smaller.** Of the game's 101 buildings, **7** existed in January 1900 in roughly today's form, **68** existed but worked differently, and **26** did not exist (in 1950: 33, 55 and 13). Of the 39 buildings the materials batches will add, 5, 22 and 12. Most of the 1900 differences are big (§1, §2):
  - **Power:** a 1900 power station turned about **3.6%** of its coal into electricity (about 4% by another source), against 24.3% in 1950: **about 6.75 times the coal per kWh** (est.; about 6 times at 4%) [P1.1], [P1.6]. Canada's power was mostly **hydro**, which the game has no building for [P1.12].
  - **Oil:** the refinery was a **kerosene works**: 63% of US refinery output by weight was lamp oil, 14% gasoline and other light products (1899) [P1.48].
  - **Transport:** horses. Hauling by wagon cost **about 34 times** the rail rate per tonne-km (est.) [P1.21], [P1.22]. About **22%** of US cropland fed farm horses and mules, and another 5% fed town and mine animals [P2.47].
  - **Work:** **40.2%** of working Canadians were in farming (15.7% in 1951) [P2.43]. About **80%** of US factory power came from steam engines on the spot; electric motors were 4.8% of factory power equipment in 1899 [P2.26], [P1.8].
  - **Health:** life expectancy at birth was **47.1 for men and 50.1 for women** (1901) [BL97]. Canada had 1.02 doctors per 1,000 people, as in 1951, but only **0.05 nurses** (about 3 in 1951) [SC-B82].
  - **Money:** income per head was **about a tenth of today's** ($4,640 against $45,530 in 2011 international dollars, which adjust for price differences between countries) [MAD]. There was no income tax and no central bank. The dollar was gold, at par with the US dollar [POWELL].
- **Much of the change to 1950 needs no discovery.** The open hearth, electric motors, machine coal cutters, the motor truck and about 15 more things were already in use in 1900 and only had to spread (§3). The equipment rule the first study set (§2.2 there) spreads them when they pay.

### What the catalogue gains
- **48 entries, 1900–1950** (§8): **27** in engineering, **6** in medicine, **3** in business and **12** in law. The law entries are the Policy levers themselves, on Canada's dates (§7). With the first study's 40, the catalogue runs from 1900 to 1995 with **88** entries, **89** with a proposed new L5 (§8.6).
- **History becomes the tutorial** (§9). A 1900 player starts with about 80 of the game's 101 kinds of building, property tax and the city's own services. Cars, cracking, the assembly line, income tax, insulin, the central bank, sulfa and penicillin then arrive decade by decade.

### What the numbers say (§10)
- **A city that builds schools early gets a real research era:** a University from 1925, a Law school, a Medical school and an Institute of technology by 1950, each field funded to its room (the most research posts its schools can hold: 2.7 for each university-band staff member, the first study's §1.8), makes **about 13 discoveries here before 1950** and about 25 by 2026, **3.2 years** ahead of the world on average. Founded in 1950 with the same schools at the same ages, it makes 0.1.
- **A brisk player** (a University at month 726) makes about **0.9**, all after 1960. **The scripted playtest's pace** makes none in either century. The near-future list the first study recommended (its Q2) is still needed.

### The three calls that matter most (§11)
1. **Policy on history's dates.** Each lever opens on the date Canada first had it: profit tax 1916, income tax 1917, minimum wage 1918, sales tax 1920, pensions 1927, the central bank 1935, EI 1940, and so on. Money follows Canada's own history: the dollar is on gold, held at par by the city's reserves, to August 1914 and again from July 1926 to 1929–31, and floats in between and after; the government's Advance Rate (1914) opens the policy rate, and the Bank of Canada (1935) the rest of Money. A Law school can bring a lever forward by up to four years. This is the strongest single piece of the tutorial, and the main design call.
2. **The 1900 economy (batch R0).** Real wages were about **a quarter** of today's (est.) and income per head about a tenth. The founding wages, the world's prices, the household's basket and the world's mix of skills all need 1900's figures before research can be balanced.
3. **Which new mechanics to build.** A small set of six: horses as recipes that eat feed, steam power in factories, household light and heat, causes of death in `Health`, hydro power, and Money's gold years. Coal gas, natural ice, mail order, wartime mobilisation, rationing and tariffs are left out; home butter and bread become part of the 1900 household's basket.

---

## 1. What existed in January 1900

### 1.1 The counts
| | Existed as today (a) | Worked differently (b) | Did not exist (c) | Source |
|---|---|---|---|---|
| Energy, water, oil, ports, rail, roads, transit, vehicles (44) | 3 | 24 | 17 | [P1] |
| Industry, farms, food (17) | 0 | 15 | 2 | [P2] |
| Housing, shops, health, schools, police, banks, offices (40) | 4 | 29 | 7 | [P3] |
| **The game's 101, January 1900** | **7** | **68** | **26** | |
| The game's 101, 1950 (the first study) | 33 | 55 | 13 | [R2] |
| The materials batches' 39 new buildings (not counting the Iron Mine, arc furnace and mini-mill, above) | 5 | 22 | 12 | [P2] |

The seven "a" buildings: Gravel Road, Metro Line, Tank Farm (est.), Boutique, Department Store, Restaurant, Municipal Cemetery. In the materials chains: Dimension Stone Quarry, and (est.) Scrap Yard, Mini Blast Furnace, Electrolytic Refinery and Lime Kiln.

The 1900 count takes the streetcar as the Light Rail Line's 1900 form (b), as the first study's Q20 decided; the 1950 count, from [R2], took the Light Rail Line as not yet existing (c). On the 1950 count's basis, 1900 has 67 b and 27 c.

### 1.2 The buildings that did not exist, and what opens each
| Building | First real instance | Opened by |
|---|---|---|
| Electric Arc Furnace (today's Steel Foundry) | Dec 1900, France (Héroult); North America 5 Apr 1906, Syracuse (1907 by another source) | PE1 |
| Large Cement Plant | 1902, New Village NJ (Edison's long kilns) | PE4 |
| Filling Station | 1905, St. Louis; Canada 1907, Vancouver (Imperial Oil) | PB1 |
| Large Copper Mine | 1906, Bingham Canyon | PE8 |
| Middle School | 1909, Columbus (junior high); Canada 1919, Winnipeg | **available in 1900**, as the senior grades of an elementary school (§11, Q12) |
| Small Cracking Unit, Cracking Unit | Jan 1913, Whiting, Indiana (Burton) | PE11 |
| Assembly Plant | 7 Oct 1913, Highland Park (Ford's moving line) | PE13 |
| Ready-Mix Plant | about 1913 (unverified) | waits for the construction upgrade, as the materials study decided |
| Community Health Centre | 1926, Mount Carmel Clinic, Winnipeg | **available in 1900**, as the charity dispensary and doctors' office (Q5) |
| Small Coker, Coker | 1929, Standard Oil of Indiana | PE23 |
| Elevated Highway | 1929–30, West Side Elevated Highway, New York | **available**: elevated railways existed; a city wants one only once it has cars |
| Small Alkylation Unit, Alkylation Unit | 1938–40 | PE25 |
| Small Reformer, Reformer | 1949, Muskegon (Platforming) | PE27 |
| Pelletising Plant, Large Magnetite Complex | 1955–56, Silver Bay | E4 (first study) |
| Container Terminal | 1956 | E5 |
| Desalination Plant | 1957, Kuwait | E6 |
| Community College | 1957, Lethbridge | **available**, as a technical college (first study, Q20) |
| Small Hydrocracker, Hydrocracker | 1959 | E7 |
| Contact Centre | 1965 | B2 |
| Leach & SX-EW Plant | 1968 | E12 |
| Steel Mini-Mill, Large Mini-Mill | 1969, Nucor | E13 |
| Harvesting Operation | 1973 | E14 |
| Walk-in Clinic | 1973; Edmonton 1979 | B4 |
| Strategic Reserve | 1974–75 | **available**: a stock of oil the city chooses to keep (first study, Q20) |
| Wood Pellet Mill | 1970s (unverified) | **no entry yet** (Q13) |
| Wind Farm | 1980 | E16 |
| Panel Mill (OSB) | about 1980 (waferboard 1960s; both unverified) | **no entry yet** (Q13) |
| Assisted Living Residence | 1981, Portland | M8 |
| Shared Services Centre | early 1980s | B6 |
| Large Surface Mine, Tree Plantation | Large Surface Mine about the 1910s–20s (est., unverified); Tree Plantation not found | **available**: stripping shovels and planting were known |

Sources: [P1] §1, [P2] §1–2, [P3] §1, [R2].

So **a 1900 player can build 80 of the game's 101 kinds** (est.: 101 − 26 + 5 made available), **91 by 1950** and all 101 by 1995. Business School and Crematorium existed abroad in 1900 (Wharton 1881, unverified; Washington PA 1876), though Canada's first were HEC Montréal (1907) and Mount Royal (1901–02) [P3]: both are available.

---

## 2. How the 1900 city worked

### 2.1 Power and light
| | ~1900 | 1950 | Today | Source |
|---|---|---|---|---|
| Power station efficiency (US) | ~3.6%, about 4% by another source (about 100 MJ of coal a kWh, est.) | 24.3% (14.8 MJ) | coal 31.7% (11.4 MJ) | [P1.1], [P1.6] |
| Largest units | 1.5 MW turbines (1900–01); Chicago's largest station 16.2 MW of engines (1894) | median 100 MW (1945) | the game's plant 325 MW | [P1.3], [P1.4] |
| Factory power from electric motors (US, share of power equipment) | 4.8% (1899) | 89.8% (1939) | — | [P1.8] |
| Factory steam engines: coal into shaft work | 3% (1900); 2–6 lb of coal a hp-hour; shafting lost a third to three-quarters | — | — | [P1.1], [P2.26], [P2.27] |
| Canada | 133,000 kW of **hydro** installed by 1900; AC lines already working (DeCew Falls–Hamilton 1898, 22.4 kV); hydro over 90% of capacity 1920–50 | | | [P1.12], [P1.13] |
| Homes with electricity (US) | under 5% | 98.8% (1956) | ~100% | [P1.10] |
| Canada, electric customers | not found for 1900 | about 44% of households (1921, est.); about 82% (1950, est.) | | [Q97], [A248] |
| How homes were lit | the kerosene lamp most of all; gas in Paris and London | | | [P1.14] |

At the game's electricity unit (720 kWh a month), a 1900 station burns about **72 GJ of coal per unit-month**, against 10.7 GJ in 1950 and 8.2 GJ today (est.) [P1].

### 2.2 Water
Some cities had slow sand filters; most pumped raw lake or river water. Mechanical filters were at the trial stage (Louisville 1895–97), and **no city chlorinated its water continuously** (Lincoln, UK, 1905; Jersey City, 1908) [P1.44], [P1.43]; Maidstone dosed its whole supply once, in 1897 [TYPH]. Toronto's first filtration plant came in 1912 [P1.47]. Its typhoid deaths were **40.8 per 100,000 in 1910** and about 1 by 1915 [P1.46].

### 2.3 How people and goods moved
- **Streetcars carried the city.** 94% of US street-railway mileage was electric by 1902. Canada's electric railways carried **120.9 million passengers on 553 miles** in 1901 with 10,557 employees, about 60 rides a city dweller a year (est.) [P1.28], [P1.27]. In the game this is the Light Rail Line's 1900 form, as the first study decided.
- **Horses did the rest.** A city horse ate **1.4 short tons of oats and 2.4 of hay a year** (about 1.3 t and 2.2 t), about 0.29 t a month (est.), and dropped 15–30 lb of manure a day. Montreal had about 3,000 stables (1891) and 1,429 carters heading households (1901). Horses and horse vehicles killed 200 people in New York in 1900 [P1.19], [P1.20].
- **Cartage was dear:** about $0.171 a tonne-km by wagon (1895, est.) against $0.0050 by rail (1900, est.), **about 34 times** [P1.21], [P1.22].
- **Cars were toys of the rich.** The US had 8,000 in 1900. Canada registered 178 cars in 1903; motor vehicles numbered 8,967 in 1910, 408,790 in 1920 (407,064 in the Canada Year Book) and 1,187,331 in 1929; in 1950, 2.60 million, of them 1.91 million cars, about **0.56 cars a household** (est.) [T147], [P1.26].
- **Rail was all steam:** 4–8% efficient, the best about 11% (1900). Diesel-electric reached 28% (c.1950) [P1.1].
- **Roads:** only 7.1% of US rural road had any surface (1904, est.). City streets were stone block, brick, macadam or Trinidad sheet asphalt [P1.34], [P1.35].

### 2.4 Oil
| | 1899 | 1909 | 1919 | 1929 | 1937 |
|---|---|---|---|---|---|
| Gasoline and other light products, % of US refinery output by weight | 14 | 12 | 27 | 48 | 50 |
| Illuminating oils (kerosene) | 63 | 38 | 15 | 6 | 5 |
| Fuel oils | 15 | 38 | 52 | 42 | 42 |
| Lubricants | 8 | 12 | 6 | 4 | 3 |

Source: [P1.48]. Straight-run gasoline was about 12–13% of crude (est.); thermal cracking doubled it to about 25%, and catalytic cracking gave 48–50% gasoline from the oil it cracked [P1.49]. **In the game, kerosene is the `KEROSENE` cut sold as `JET`**, so 1900's lamp oil is demand for `JET` [P1].

US crude was 63.6 million barrels in 1900 at $1.19 a barrel; 1,974 million in 1950 at $2.51 [P1.61], [P1.62]. Canada (Petrolia, Lambton County) had been a net importer since the 1880s; Imperial Oil refined at Sarnia from 1897–99 [P1.73], [P1.78].

**Oil fields known in January 1900:** Pennsylvania, Ohio and Lima-Indiana; Lambton (Ontario); Baku; California (Coalinga, Midway-Sunset, Kern River). **Found from 1900 to 1950:** Spindletop (10 Jan 1901), Masjed Soleyman (26 May 1908), Caddo Lake, Elk Hills and South Belridge (1911), Norman Wells (1911/1919), Turner Valley (14 May 1914), Mene Grande (1914), Bolivar Coastal (1917; gusher 1922), the Permian Basin (1921), Yates and Chicontepec (1926), Gachsaran (1927), Kirkuk (15 Oct 1927), East Texas (3 Oct 1930), Bahrain and Wilmington (1932), Burgan (Feb 1938), Aghajari (1938), Dukhan (1939), Abqaiq (1940), Spraberry (1943), Leduc (13 Feb 1947), Redwater, Ghawar and Romashkino (1948), Az Zubayr (1949) [P1.63]–[P1.75], [P1.66].

### 2.5 Industry
- **Steel:** Bessemer converters made **71%** of US steel in 1899 and open hearths 28%; by 1909 it was 39% and 60%. Merchant blast furnaces took about **7 man-hours a long ton** of pig iron (1912–14) against 3.4 in 1926, and 2,546 lb of coke against 1,979 [P2.1], [P2.3]. Canada's modern steel began at Sydney (1901: 400 by-product coke ovens, ten 50-t open hearths) [P2.2].
- **Coal:** pick, powder and hand shovel. A quarter of US underground coal was machine-cut in 1900 and none was machine-loaded; 87.9% was cut and 31% loaded by machine in 1939 [P2.8].
- **Copper:** vein ore of about **2.7%** copper, 60–75% of it recovered by gravity; 1.25% ore and about 90% recovery by 1939 [P2.10].
- **Cement:** rotary kilns about 60 ft long making about **30 t of clinker a day**, burning coal at about 30% of the clinker's weight; 680 t a day in 1950's large kilns [P2.29], [R2].
- **Wood:** axe, bucksaw and crosscut saw, horse sleds and the spring river drive: about **2 cords a worker a day** [P2.33]. Lumber was Canada's biggest industry: 75,704 wage earners in 1900, **24.5%** of all manufacturing (est.) [P2.38].
- **Factories** ran on a steam engine and line shafts. Tools cut at about 30 ft a minute before high-speed steel [P2.24], [P2.26].

### 2.6 Farms and food
| | 1900–1911 | ~1950 | Source |
|---|---|---|---|
| Canadians working in agriculture | 40.2% (1901) | 15.7% (1951) | [P2.43] |
| Farms; average size | 511,073; 124 acres (1901) | 623,087; 279 acres | [P2.42] |
| Horses in Canada | 1.58 million (1901), 2.60 million (1911) | about 2 a farm (mid-1950s) | [P2.40], [P2.41], [P2.44] |
| Tractors in Canada | none counted (47,455 in 1921) | 399,683 (1951) | [P2.45] |
| US cropland feeding draught animals | about 22% (1880–1920), plus 5% for town and mine animals | 5 million acres (1960) | [P2.47] |
| Wheat labour (US) | 40–50 h per 100 bushels (1890) | 15–20 h (1930); 6.5 h (1955) | [P2.48] |
| Canada wheat yield | 14.9 bu an acre (1911, est.) | — | [P2.41] |
| Butter made at home (Canada) | 74% (1901, est.) | — | [P2.40] |
| Bottles | a hand shop of 6 men and boys: 2,880 in 12 h | Owens machine: 50,400 a day | [P2.56] |

The game's Grain Farm makes 500 t a month with 4 posts. A 1900 farm needed **about 11–14 times the posts per tonne** of wheat (est., [P2] §6), part of it covered by the tractor, the combine and better yields.

### 2.7 Health
| | c.1900 | 1950 | Source |
|---|---|---|---|
| Life expectancy at birth, men / women | 47.1 / 50.1 (1901) | 66.3 / 70.8 (1951) | [BL97], [SC-B65] |
| Babies dying before age one | 144 boys, 124 girls per 1,000 (1901); Montreal about 270 | 42 per 1,000 (1950) | [BL97], [WP-PH], [SC-B51] |
| Mothers dying in childbirth, per 1,000 live births | 4.7 (1921), 5.8 (1930) | 1.1 | [SC-B51] |
| Births in hospital | about 5% (US, 1900) | 76.0% (Canada) | [THOM], [SC-B1] |
| Doctors / nurses per 1,000 | 1.02 / 0.05 (1901) | 1.02 / 3.08 (1951; the 41,088 registered nurses would be about 2.9, so the rate counts more than registered nurses) | [SC-B82] |
| General hospital stay | 15–24 days (1925, three provinces, est.; Ontario's 17.6 includes sanatoria) | 10.0 days | [CYB26], [SC-B285] |
| Staff per bed, public general hospitals | 0.74 (1932, est.) | 1.28 (est.) | [SC-B141], [SC-B304] |
| Paid for by | fees, charity and per-day grants (Ontario 50¢ a day for 120 days) | fees and plans; universal insurance 1958–61 | [CYB26] |

**What people died of**, per 100,000 a year:

| Cause | US 1900 | Canada 1901, or 1921–25 | Canada 1946–50 | Source |
|---|---|---|---|---|
| Tuberculosis | 194 | 180 (1901) | 37 | [VCU], [LAC-TB], [SC-B35] |
| Influenza and pneumonia | 202 | 141 (1921–25) | 55 | [VCU], [SC-B35] |
| Diarrhoea and enteritis | 155 | 72 (1921–25) | 14 | [GOR53], [SC-B35] |
| Typhoid | 31 | Toronto 40.8 (1910) | about 0 | [GOR53], [TOR-W] |
| Diphtheria, whooping cough, measles, scarlet fever | 243 (per 100,000 children under 15) | 47 with typhoid, all ages (1921–25) | 5 | [GOR53], [SC-B35] |
| Heart and kidney | 345 | 222 (1921–25) | 413 | [VCU], [SC-B35] |

In 1900, **30.4% of all US deaths were of children under 5** [MMWR99].

The game's General Hospital has 363 staff. At 1932's 0.74 staff a bed it would run about 490 beds; at 1950's 1.28, about 284 (est.) [P3].

### 2.8 Schools, police, prisons, banks, shops
- **Schools:** 1,055,000 enrolled in public schools in 1900 (about 1,083,000 by another series [CYB27S]), but attendance averaged **61.2%** of enrolment (86.5% in 1950). Each teacher had 38.5 enrolled pupils (26.7 in 1950, est.); 77% of teachers were women (71% by the 1901 census's earnings table [CYB07b]) [SC-W67], [SC-W150].
- **Universities:** **6,641** full-time undergraduates and 1,214 degrees in 1901, against 64,036 and 17,185 bachelor's degrees in 1950. Medicine had 27% of the students; engineering 698; law 452; commerce none [SC-W439], [SC-W504].
- **Police:** Toronto had 302 officers for about 208,000–250,000 people (1902), 0.34–0.40 of the game's full coverage (est.). They walked beats and reported from call boxes [TPS03], [P3].
- **Prisons:** the federal penitentiaries held 1,430 people (1899–1900), 27 per 100,000 (est.), at $244 a prisoner-year [SC-Z223].
- **Banks:** 1,145 branches at the end of 1900 (from scanned text, not yet checked), each keeping its own ledgers by hand. Banks could fail with depositor losses: Farmers Bank (1910) about $1.31 million [NMC-BB].
- **Shops:** counter service. A clerk fetched every item, delivered the order and kept the family's account; delivery cost about 3% of the bill and credit about 2% [CPL-PW]. Eaton's catalogue (from 1884) reached the farms [EATON].
- **Housing:** most homes lacked running water, a bathroom, electricity and central heat; half of Americans lived in households of six or more (1900). Canada has no housing census before 1941 [FMC-5.4], [FMC-5.1].

---

## 3. Known in 1900: equipment to fit, not to discover

These were in commercial use before 1900, so they are not catalogue rows. A 1900 building starts without them and fits them when they pay: when what a piece saves over its life repays its cost and beats the sector's other investments (the first study's §2.2). New buildings come with every piece that pays at the order. **Most of steel's and the factories' change from 1900 to 1950 is this, not discovery** [P2] §5.

| Equipment | First use | In 1900 | By 1950 | Touches | Source |
|---|---|---|---|---|---|
| Open-hearth furnace | 1860s (unverified) | 28% of US steel (1899) | ~90% | Oxygen Steel Shop | [P2.1] |
| By-product coke ovens | 1893, Syracuse | Sydney built 400 in 1901 | passed beehive ovens in 1919 | Coke Ovens | [P2.2], [P2.5] |
| Mechanical blast-furnace charging and pig casting | 1890s (unverified) | 8 of 37 plants fully mechanised (1911–14) | 34 of 49 (1926) | Blast Furnace | [P2.3] |
| Machine coal cutter | before 1899 | 24.9% of US underground coal | 87.9% (1939) | Underground Coal Mine | [P2.8] |
| Steam-shovel open pit (iron) | Mesabi, 1890s | half of Mesabi's mines (1902) | 65.8% of US iron ore (1937) | Large Open-Pit Mine | [P2.9], [P2.17] |
| Power rock drill | before 1902 | over 85% of underground copper ore (1902) | — | mines, Quarry | [P2.7] |
| Short rotary cement kiln | 1890s | 10 US plants (1901) | standard | Cement Plant | [P2.29] |
| Groundwood and sulphite pulp | Canada 1866–69, 1887 | 25 Canadian pulp mills (1901) | kraft passed sulphite in the 1940s | Pulp Mill | [P2.35]–[P2.37] |
| Electric motor drive | group drive, mid-1890s | 4.8% of US factory power equipment (1899) | 82.3% (1929) | factories, bakeries, bottling, meat works | [P1.8], [P2.26] |
| Refrigerator car and ice | 1880 | 68,500 cars | — | Meat Works | [P2.57] |
| Binder and threshing machine | 1880s | in use; common by 1915 | replaced by the combine | Grain Farm | [P2.48] |
| Motor truck | 1 Oct 1896, Daimler | about 1,400 in the US by 1905 | 8.60 million (US) | Commercial Vehicle Plant; cartage | [P1.33], [P1.25] |
| Motor bus | 18 Mar 1895, Siegen | London 1902; Toronto 1921 | — | Bus Network | [P1.31], [P1.30] |
| Electric streetcar | Toronto 1892 | 94% of US street-railway mileage (1902) | — | Light Rail Line | [P1.28], [P1.29] |
| AC transmission | DeCew Falls–Hamilton 1898 | working | — | the grid | [P1.12] |
| Hulett ore unloader | 1899, Conneaut | 275 t an hour; 6¢ a tonne against 19¢ | — | Bulk Terminal | [P1.87] |
| X-ray machine | 1896 | in hospital use | — | hospitals | [P3] |
| Diphtheria antitoxin | 1894 | treated cases died at 1–5% | — | hospitals | [DIPH] |

**The motor truck and bus are the clearest case.** They existed in 1900 but did not pay: petrol came by the can, roads were mud, and a truck was hand-built. They spread as the assembly line (PE13), the filling station (PB1) and cheap cracked petrol (PE11) made them pay. US off-farm horses fell from 3.45 million (1910) to 2.13 million (1920) [P1.18]. Nothing in the game needs to schedule that.

---

## 4. New mechanics a 1900 city needs

Each row is something the game has no mechanism for. The recommendation keeps to Jerus's rule: a new template only for a new mechanic, and a recipe where a recipe will do.

| Mechanic | How big | When it faded | Recommendation |
|---|---|---|---|
| **Horses on farms** | 22% of US cropland fed farm horses and mules; 3 acres a horse; Canada 1.58 million horses (1901) [P2.47], [P2.40] | tractors gave 11% of US farm power in 1920, 64% in 1940 [P2.47] | **A recipe, no template.** The 1900 farm recipe keeps part of its own `CROPS` as feed and needs more posts; the tractor (PE14) switches it to `PETROL`. The land the horses ate is freed by the switch. |
| **Horses for cartage and transit** | city horse 0.29 t of feed a month (est.); cartage ~34× rail [P1.19], [P1.21] | off-farm horses 3.45 M (1910) → 2.13 M (1920) [P1.18] | **A recipe.** Road freight's 1900 form costs the horse's rate per tonne-km and eats `CROPS`; the motor truck (§3) replaces it when it pays. The Bus Network's 1900 form is the horse omnibus. |
| **Household light and heat** | US working families: fuel 4.19% and light 1.06% of spending (1901) [P1.15]; Canadian family of five: fuel and light about 16% of the staple basket (1900, est.) [CYB18] | lamp oil fell to 6% of refinery output by 1929 as homes were electrified [P1.48] | **Two goods the household buys:** lamp oil (`JET`, the kerosene cut) and heat (`THERMAL_COAL` or wood from the materials batches). Electrified homes buy power instead. Part of R0's household basket. |
| **Steam power in factories** | ~80% of US factory drive (1900); 1.8 kg of coal a kWh at the shaft (est.) [P2.26], [P2.27] | electric drive 82.3% of factory power equipment by 1929 [P1.8] | **A recipe.** A factory's 1900 recipe burns `THERMAL_COAL` on site instead of drawing grid power; electric drive (§3) is fitted when grid power is cheaper. |
| **Hydro power** | Canada: 133,000 kW by 1900; over 90% of capacity 1920–50 [P1.12] | still 60% of Canada's generation (2021) | **A new template** (Q4): a Hydro Station needs a river and a drop on Earth's map, which nothing in the game has. For the electricity run; today's game lacks it too. |
| **Causes of death** | typhoid, babies' summer diarrhoea, childhood epidemics, TB, childbirth, pneumonia (§2.7) | most fell 60–100% by 1950 (est., §2.7) | **Terms in `Health`**, of the same shape as the existing terms for the unburied and the hungry (Q3 of §11's top three): water coverage drives typhoid; milk and water drive babies' diarrhoea; outbreaks carry the childhood diseases; TB fills the game's queue of people sick for a year or more; mothers die at home births. Without them the medical catalogue has nothing to act on. |
| **Doctors without beds; almost no nurses** | 5,442 doctors, mostly on house calls (est.); 280 "nurses" (1901) [SC-B82] | nurses 41,088 by 1951 | **No new mechanism:** the Community Health Centre's 1900 form is the dispensary and doctors' office (Q5), and nurses are scarce because the world's mix of skills follows history (R0). |
| **Poor relief instead of pensions and EI** | houses of refuge; 58% of Waterloo's 1900 admissions were over 60 [WHR] | pensions 1927, UI 1940 | **No new mechanism:** the Long-Term Care Complex's 1900 form is the house of refuge, and the Promises dials open on their dates (§7). |
| **Money's gold years** | the dollar fixed in gold at par with the US dollar; no central bank [POWELL] | off gold Aug 1914–Jun 1926 and from 1929–31; Bank of Canada 1935 | **Money's dated states** (§7.2): on gold, the D$ is held at par by the city's reserves and its interest follows the world's (Dominion bonds yielded 3.0% in 1900); off gold, it floats. |
| Coal gas | US gas works: 28,400 staff (1899); gas lighting negligible after 1919 [P1.9] | 1919 | **Leave out.** Canada lit by kerosene and then electricity. |
| Natural ice | US 1914: 24 Mt natural, 26 Mt plant ice [P1.17] | after WWI | **Leave out**; fold the cost into the Meat Works' 1900 recipe. |
| Home butter and bread | 74% of Canadian butter made at home (1901) [P2.40] | 1910s–30s | **Data, not a mechanism:** R0's household basket for 1900 buys more raw staples. |
| Shop delivery and credit; mail order | 3% and 2% of the bill [CPL-PW]; Eaton's catalogue [EATON] | 1912–1930s | **In the grocery's 1900 recipe** (its costs); mail order left out. |
| Wartime mobilisation, price controls, rationing | 619,636 in the CEF; 779,000 in the forces (1944) [CEF-W], [D124] | 1945–47 | **Leave out for now**; the world's prices carry the wars (§6). |
| The tariff | customs 53% of federal revenue (1900) [H1] | — | **Leave out**: a city does not set it. |

---

## 5. The 1900 economy

### 5.1 Canada in 1900, beside 1950 and today
| | 1900 | 1950 | Today | Source |
|---|---|---|---|---|
| GNP | C$902 million | C$17.7–18.7 billion | — | [CM] |
| Population | 5.30 million (5.37 million at the 1901 census) | 13.7 million | 39.3 million | [A1], [A67] |
| GNP per head | C$170, about **C$6,300 in 2025 dollars** (est.: ×36.9) | C$1,288, about C$16,900 | C$77,871 | [CM], [CPI], [R2] |
| GDP per head, Maddison (2011 international dollars) | $4,640 (58% of the US's $8,038) | $11,622 | $45,530 (2022): **9.8× 1900** | [MAD] |
| Real GNP growth | ×2.19 from 1900 to 1913; ×6.8 from 1900 to 1950 (est.) | | | [CM] |
| Price level (CPI, 2002 = 100) | 4.45 (spliced, est.) | 12.5 | 164.2 | [CPI], [CM] |
| Federal revenue | C$53 million: customs 53%, excise C$10 million; **no income, sales or profit tax** | C$3,113 million | | [H1] |
| Tariff | 27.7% of dutiable imports, 16.7% of all imports | 17.1% / 8.8% | | [G473] |
| Share urban | 37.5% (1901, est.) | 56.7% (est.) | | [A67] |
| Household size | 5.0 people | 4.0 | | [A248] |
| Immigrant arrivals | 41,681 (1900), 400,870 (1913) | 73,912 | | [A350] |
| Women in paid work | 14.4% of women aged 10 and over | 24.4% of women 14+ | about 82% of women 25–54 | [D107] |
| Fertility | 4.03 children a woman (1900–05) | 3.44 | 1.26 | [TFR-W] |
| Children at school | 52.1% of 5–19-year-olds for some of the year; 42.6% for 7–9 months (1901) | | | [CYB27S] |

### 5.2 Wages
| Skill (the game's nearest band) | 1901, a year | Compared with a factory man (est.) | Source |
|---|---|---|---|
| Farm hand, man (`NO_DIPLOMA`) | $207.55, often with board | 0.51 | [CYB07a] |
| Labourer and domestic service, men / women | $272 / $137 | 0.68 / 0.34 | [CYB07a] |
| Factory, men / women | **$403** / $193 | 1 / 0.48 | [CYB07a] |
| Mining, men | $514 | 1.27 | [CYB07a] |
| Trade and transport, men / women (`DIPLOMA`, `COLLEGE_BUSINESS`) | $503 / $238 | 1.25 / 0.59 | [CYB07a] |
| Teachers, men / women | $486 / $246 | 1.21 / 0.61 | [CYB07b] |
| Electricians | $518 | 1.29 | [CYB07b] |
| Engineers (`UNIV_HIGHTECH_ENG`) | **$1,306** | 3.24 | [CYB07b] |
| Doctors and lawyers | not found (self-employed) | — | gap |

- **Real wages were about a quarter of today's** (est.): a factory man's real wage roughly doubled from 1901 to 1950 (×1.95, [E41]), and 1950's was about 45% of today's (the first study, [SC-E]); 0.45 ÷ 1.95 = 0.23. On the game's bands, today's `NO_DIPLOMA` D$ 3.46k a month would be about **D$ 0.8k** in 1900 (est.).
- **Skill paid more in 1900:** an engineer earned 3.2 times a factory man, against 2.8 times between the game's `UNIV_HIGHTECH_ENG` (D$ 9.6k a month) and `NO_DIPLOMA` (D$ 3.46k) today (est.).
- **Hours** were about 60 a week in factories (unverified) against 42.3 in 1950 [E128].

### 5.3 What a family spent, and money
A family of five's weekly staple budget in 1900, at average prices in 60 cities: **$9.37**. Food $5.48 (58%), fuel and light about $1.50 (16%, est.), rent $2.37 (25%). Clothing and other goods are not in it. Coal oil (kerosene) cost 24¢ a gallon [CYB18].

**Money in 1900** [POWELL]:
- The dollar was defined in gold at par with the US dollar; the sovereign was legal tender at C$4.8666.
- The government issued the small notes (Dominion notes), partly backed by gold. The chartered banks issued their own notes of $5 and up, not backed by gold, up to their paid-in capital.
- **There was no central bank, and no lender of last resort** (nobody to lend to banks in a run). Dominion bonds yielded **3.0%**. Bank rates for 1900 were not found; the traditional prime loan rate was 6% and the deposit rate 3% until 1933, when deposits fell to 2.5% [CM], [POWELL].
- Money held by the public was C$362 million, 40% of GNP (est.) [CM].

### 5.4 What batch R0 needs (the first study's Q1, now for 1900)
1. **The calendar's epoch to 1900** (`CityCalendar.EPOCH_YEAR`).
2. **Founding wages at 1901's level** in constant founding D$, about a quarter of today's (est.), with the 1901 census's ratios between skills (§5.2).
3. **The world's prices, wages, interest and migration follow history** year by year (§6.1), in constant founding D$.
4. **A city's wages rise as what a worker makes rises**, from the discoveries it gets and the equipment it fits, as the first study recommended.
5. **The household's basket follows the year:** 1900's has more raw staples, lamp oil and coal, and no car (§4).
6. **The world's mix of skills follows the year:** 1.2 university students per 1,000 people in 1901 against 4.7 in 1950 (est.), and 0.05 nurses per 1,000 against about 3. Arrivals' diplomas come from that mix.
7. **The gap the catalogue leaves.** A 1900 building is today's with every discovery since 1900 undone and the known-in-1900 equipment not yet fitted. The passes estimate the rows cover about 80% of the change in power and oil, about 60% in farms and mining, 35–40% in steel, rail, machine shops and cement, 20–30% in food and forestry, about 10% in ports, and a quarter to a half of the fall in deaths ([P1] §5, [P2] §6, [P3] §3.5). The rest stays at today's level, so a 1900 city is more productive than the real one. R0 measures that sector by sector, and each sector's page can say how much of the real gap the catalogue covers.
8. **Money's gold years** (§7.2). While the D$ is on gold, it cannot fall into the slide seen in the slot-3 run (`reading-slot-3.md` §3), where a founding city's imports pushed the rate up and the rate pushed prices up, each feeding the other: on gold the reserves drain instead.

---

## 6. The world, 1900–1950

### 6.1 Year by year
Real GNP is Urquhart's series to 1926, then Statistics Canada's, joined by McInnis [CM]. Prices before 1914 are Urquhart's GNP deflator scaled to the CPI (est.). Unemployment is the 1 June estimate; none exists before 1921.

| Year | Real GNP (1900 = 100) | GDP per head (2011 int.$) | CPI (2002 = 100) | Wholesale (1935–39 = 100) | Unemployed, % | Wheat, ¢ a bushel | Immigrants |
|---|---|---|---|---|---|---|---|
| 1900 | 100 | 4,640 | 4.45* | 62.4 | — | 74.6 | 41,681 |
| 1901 | 108 | 4,937 | 4.49* | 63.7 | — | 75.2 | 55,747 |
| 1902 | 119 | 5,310 | 4.63* | 66.6 | — | 72.9 | 89,102 |
| 1903 | 123 | 5,314 | 4.72* | 67.5 | — | 78.8 | 138,660 |
| 1904 | 124 | 5,228 | 4.76* | 68.3 | — | 91.6 | 131,252 |
| 1905 | 138 | 5,678 | 4.85* | 70.4 | — | 77.2 | 141,465 |
| 1906 | 152 | 6,130 | 4.94* | 70.7 | — | 78.8 | 211,653 |
| 1907 | 161 | 6,177 | 5.30* | 76.4 | — | 105.2 | 272,409 |
| 1908 | 153 | 5,694 | 5.34* | 76.3 | — | 109.0 | 143,326 |
| 1909 | 168 | 6,137 | 5.38* | 77.6 | — | 101.1 | 173,694 |
| 1910 | 184 | 6,481 | 5.43* | 78.5 | — | 94.2 | 286,839 |
| 1911 | 196 | 6,715 | 5.61* | 81.1 | — | 100.8 | 331,288 |
| 1912 | 211 | 6,977 | 5.83* | 85.2 | — | 90.1 | 375,756 |
| 1913 | 219 | 7,088 | 5.96* | 83.4 | — | 89.1 | 400,870 |
| 1914 | 201 | 6,416 | 6.00 | 85.4 | — | 132.4 | 150,484 |
| 1915 | 217 | 6,747 | 6.10 | 91.8 | — | 113.6 | 36,665 |
| 1916 | 242 | 7,406 | 6.70 | 109.8 | — | 205.5 | 55,914 |
| 1917 | 252 | 7,653 | 7.90 | 148.9 | — | 221.1 | 72,910 |
| 1918 | 237 | 7,079 | 8.90 | 166.0 | — | 224.2 | 41,845 |
| 1919 | 238 | 6,406 | 9.80 | 174.7 | — | 217.4 | 107,698 |
| 1920 | 221 | 6,154 | 11.40 | 203.2 | — | 199.4 | 138,824 |
| 1921 | 201 | 5,351 | 10.00 | 143.4 | 5.8 | 129.7 | 91,728 |
| 1922 | 229 | 6,046 | 9.20 | 126.8 | 4.4 | 110.5 | 64,224 |
| 1923 | 246 | 6,338 | 9.20 | 127.7 | 3.2 | 107.0 | 133,729 |
| 1924 | 247 | 6,339 | 9.00 | 129.5 | 4.5 | 168.5 | 124,164 |
| 1925 | 277 | 6,918 | 9.10 | 133.8 | 4.4 | 151.2 | 84,907 |
| 1926 | 293 | 7,168 | 9.20 | 130.3 | 3.0 | 146.2 | 135,982 |
| 1927 | 311 | 7,726 | 9.10 | 127.3 | 1.8 | 146.3 | 158,886 |
| 1928 | 333 | 8,244 | 9.10 | 125.6 | 1.7 | 124.0 | 166,783 |
| 1929 | 335 | 8,074 | 9.20 | 124.6 | 2.9 | 124.2 | 164,993 |
| 1930 | 322 | 7,669 | 9.10 | 112.9 | 9.1 | 64.2 | 104,806 |
| 1931 | 284 | 6,382 | 8.20 | 94.0 | 11.6 | 59.8 | 27,530 |
| 1932 | 254 | 5,852 | 7.50 | 86.9 | 17.6 | 54.3 | 20,591 |
| 1933 | 237 | 5,372 | 7.10 | 87.4 | 19.3 | 68.1 | 14,382 |
| 1934 | 264 | 5,883 | 7.20 | 93.4 | 14.5 | 81.9 | 12,476 |
| 1935 | 284 | 6,298 | 7.30 | 94.1 | 14.2 | 85.1 | 11,277 |
| 1936 | 297 | 6,574 | 7.40 | 96.7 | 12.8 | 122.6 | 11,643 |
| 1937 | 326 | 7,130 | 7.70 | 108.4 | 9.1 | 131.5 | 15,101 |
| 1938 | 331 | 7,246 | 7.70 | 101.9 | 11.4 | 62.0 | 17,244 |
| 1939 | 353 | 7,600 | 7.70 | 99.0 | 11.4 | 76.5 | 16,994 |
| 1940 | 403 | 8,557 | 8.00 | 107.8 | 9.2 | 74.0 | 11,324 |
| 1941 | 463 | 9,645 | 8.50 | 116.2 | 4.4 | 76.6 | 9,329 |
| 1942 | 546 | 11,210 | 8.80 | 122.8 | 3.0 | 94.5 | 7,576 |
| 1943 | 570 | 11,577 | 9.00 | 127.8 | 1.7 | 123.2 | 8,504 |
| 1944 | 592 | 11,864 | 9.10 | 130.5 | 1.4 | 125.0 | 12,801 |
| 1945 | 580 | 11,370 | 9.20 | 132.0 | 1.6 | 125.0 | 22,722 |
| 1946 | 560 | 11,048 | 9.40 | 139.2 | 2.6 | 138.5 | 71,719 |
| 1947 | 591 | 11,298 | 10.30 | 165.8 | 1.9 | 158.5 | 64,127 |
| 1948 | 601 | 11,261 | 11.80 | 197.2 | 1.6 | 205.0 | 125,414 |
| 1949 | 630 | 11,260 | 12.20 | 201.7 | 2.0 | 205.9 | 95,217 |
| 1950 | 678 | 11,622 | 12.50 | 215.4 | 2.7 | 191.2 | 73,912 |

Sources: [CM], [MAD], [CPI], [K33], [D124], [M228], [A350]. * spliced (est.).

### 6.2 What a city would have felt
| Episode | What happened, in figures | Source |
|---|---|---|
| The Laurier boom, 1896–1913 | real GNP ×2.19 (1900–13); immigrants 41,681 → 400,870 a year; rent in the family budget +71% (1900–10, est.) | [CM], [A350], [CYB18] |
| The panic of 1907 | real GNP −5.1%, GDP per head −7.8% (1907–08); immigration halved | [CM], [MAD], [A350] |
| The First World War, 1914–18 | 619,636 enlisted, 60,661 died; CPI +90% (1914–20); wholesale +138%; wheat 89¢ (1913) → 224¢ (1918); gold suspended Aug 1914; profit tax (1916) and income tax (1917) | [CEF-W], [CPI], [K33], [M228], [POWELL] |
| The influenza, 1918–20 | about 50,000 Canadian deaths, about 0.6% of the people (est.) | [FLU-W] |
| The slump of 1920–21 | wholesale prices −29% in a year; CPI −19% (1920–22); real GNP −9%; wheat 199¢ → 107¢ (1923) | [K33], [CPI], [CM], [M228] |
| The 1920s | real GNP +67% (1921–29); unemployment 1.7–4.5%; motor vehicles 408,790 → 1,187,331 | [CM], [D124], [T147] |
| The Depression, 1929–39 | real GNP −29% (1929–33); CPI −23%; wheat 124¢ → 54¢ (1932); unemployment 2.9% → **19.3%** (June 1933), 11.4% in 1939; immigration 11,000–14,000 a year | [CM], [CPI], [M228], [D124], [A350] |
| The Second World War, 1939–45 | real GNP +68% (1939–44); unemployment 1.4% (1944); a price ceiling held prices to about +3% from when it began (Dec 1941, date unverified) to 1945, while the yearly CPI rose 8% from 1941 to 1945; exchange control at US$1 = C$1.10 | [CM], [D124], [WPTB-W], [POWELL] |
| After the war | par with the US dollar (July 1946), C$1.10 again (Sep 1949), floated 1 Oct 1950; CPI +33% (1946–50) | [POWELL], [CPI] |

**Recommendation:** the world carries these as its prices (the CPI and wholesale columns), its wheat price, its interest rate (bond yields), and the arrivals it sends. The 1918 influenza is an epidemic in `Health` (one of the causes of death, §4). Wartime mobilisation, the price ceiling and rationing are left out for now.

### 6.3 Money, 1900–1950
| Date | Event | Source |
|---|---|---|
| to Aug 1914 | Gold standard; the dollar at par with the US dollar | [POWELL] |
| Aug 1914 | Dominion notes no longer redeemable in gold; the Finance Act lets the government lend to banks in a run, at an "Advance Rate" (3.75% in the 1920s) | [POWELL] |
| 1 Jul 1926 | Back on gold | [POWELL] |
| 1929; 31 Oct 1931 | Off gold in effect; gold exports banned after Britain left gold (21 Sep 1931) | [POWELL] |
| 11 Mar 1935 | The Bank of Canada opens; Bank Rate 2.5% | [BOC-W], [BOC-BR] |
| 15 Sep 1939 | Exchange control: US$1 = C$1.10 | [POWELL] |
| Jul 1944 | Bretton Woods: the US dollar at $35 an ounce of gold | [BW-W] |

### 6.4 Can the data ship?
**Yes, with credit:** Statistics Canada's *Historical Statistics of Canada* and CPI table (Statistics Canada Open Licence), the Maddison Project (CC BY 4.0), EIA and FHWA (US public domain). **Check first:** the NBER Macrohistory price series (no licence stated; USGS DS140, public domain, can replace most of it), the McInnis series for real GNP before 1926 (built on Urquhart's book, © McGill-Queen's; key the figures from Urquhart's published tables if needed), and the Bank of Canada's pages [P4] §2.4.

---

## 7. Policy on history's dates

### 7.1 When Canada first had each lever
| Game lever | Canada's first | First terms | In 1900 | Entry |
|---|---|---|---|---|
| Property tax (Taxes) | municipal, before 1900 | a council mill rate on land and buildings; 79% of municipal revenue in 1933 (est.) | **open** | — |
| Schools (Promises) | compulsory and free: Ontario 1871, BC 1873, PEI 1877, NS 1883; Quebec 1943 | run by school boards on a school rate | **open** | — |
| Wage tax (Taxes) | no payroll tax; the nearest is the workers' compensation levy, Ontario 1 Jan 1915 | no-fault, paid by employers | closed | PL1, 1915 |
| Profit tax (Taxes) | Business Profits War Tax, 1916; corporate income tax, 1917 | 25% of profits above 7% of capital (companies) | closed | PL2, 1916 |
| Income tax (Taxes) | Income War Tax Act, 1917 | 4% above $1,500 (single) or $3,000; supertax 2% to 25% | closed | PL3, 1917 |
| Minimum wage (Wages) | Manitoba and BC, 1918 (1917 by Russell), for women; men mostly in the 1930s (unverified) | weekly rates by trade and city | closed | PL4, 1918 |
| Sales tax (Taxes) | 1920 | 1% on manufacturers' and wholesalers' sales; 6% by 1923 | closed | PL5, 1920 |
| Zoning | Kitchener, 1924 (unverified); New York 1916 | use districts | closed (everything free, the decided default) | PL6, 1924 |
| Pensions (Promises) | Old Age Pensions Act, 1927 | up to $20 a month at 70; income under $365; Ottawa paid half | closed | PL7, 1927 |
| Money: the policy rate and the currency | the Finance Act's Advance Rate, Aug 1914; the Bank of Canada, 11 Mar 1935 | Bank Rate 2.5% (1935) | **on gold** | dated: the policy rate in Aug 1914; PL8, 1935, the rest |
| Student loans (Taxes, Promises) | Dominion-Provincial Student Aid, 1939 | terms not found | closed | PL9, 1939 |
| EI premium and benefit (Taxes, Promises) | Unemployment Insurance Act, 7 Aug 1940; contributions from 1 Jul 1941 | 9¢–36¢ a week by wage class, plus 20% from the government; benefit 34–40 times the average contribution; about 42% of workers covered | closed | PL10, 1940 |
| Care subsidies (Promises) | wartime day nurseries, 1942 (unverified) | half from Ottawa (unverified) | closed | PL11, 1942 |
| Health premium; public hospital insurance | Saskatchewan, 1 Jan 1947; national cost-sharing 1958 | Saskatchewan's premium $5 a person (unverified) | closed | PL12, 1947 |
| Pension contributions (Taxes) | Canada Pension Plan, 1 Jan 1966 | 1.8% from the worker, 1.8% from the employer | closed | **L5, 1966** (new; proposed for the first study's list) |
| Deposit insurance (Money) | CDIC, 1967 | $20,000 an account | closed | **L1, 1967** (the first study's, unchanged) |
| Subsidies (Promises) | municipal "bonuses" to attract factories (extent unverified) | — | **open** | — |

Sources: [P4] §3.1 ([BURNS], [CYB25], [H1], [H113], [LOP], [SC-UI], [ILO63], [BOC-W], [BOC-BR], [AWCBC], [TCE-MW], [OREO], [CPP-W], [CDIC-W], [HIDS-W], [ZON-W]).

In 1900 a Canadian city set only its property tax, its schools (through school boards), its services and its building bylaws. Everything else was the province's or Ottawa's, or did not exist yet [P4] §3.2.

### 7.2 The recommendation
- **Each lever opens on the date Canada first had it.** The game's institutions are Canada's: its wages are Nova Scotia's and its taxes are Canada's kinds. Technology follows the world's first use because it crosses borders under licence; a law does not.
- **This is what makes the tutorial.** By the world's first use, several levers existed abroad by 1900: workers' compensation (Germany, 1884), old-age pensions (Germany, 1889, unverified), income tax (Britain, unverified) and central banks [P3] §2. A 1900 city would then start with much of the Policy screen open.
- **A lever opens at zero.** The inbox says when Canada brought it in and on what terms ("an income tax: 4% above $1,500"), and the player decides.
- **The Law school can bring a lever forward**, by up to the law field's four years, as a law discovery (PL1–PL12).
- **Money follows Canada's own dates**, as data, not discoveries:
  - **January 1900 to August 1914: on gold.** The D$ is held at par by the city's reserves; there is no policy rate, and interest follows the world's.
  - **August 1914: off gold.** The D$ floats, and the policy-rate dial opens as the government's Advance Rate (the Finance Act let it lend to banks).
  - **July 1926: back on gold** at par, and **off again in 1929–31**.
  - **March 1935: the Bank of Canada** (PL8) opens the rest of Money.
  - A city whose reserves run out while on gold leaves gold early. That is a game rule, not a claim about why Canada left: in 1914 it was the war, and in 1931 Canada followed Britain off gold.
  - A simpler alternative, if Jerus prefers: gold until 1935 throughout, unless the reserves run out.
- **The first study's L1 stays at 1967.** The people-and-services pass proposed the US's 1934, but by this rule Canada's date holds.
- **Family allowances (1945)** have no game lever; they are left out.

---

## 8. The 1900–1950 catalogue (48)

Each row: its code ("P" for before 1950), the discovery, its kind, history's date (first commercial use anywhere; for law, Canada's date), what it touches, the effect from its source, what it needs first, whether it would carry a licence fee (the first study's §2.4: 25% of what it adds, for 20 years), and the work it takes at its date in researcher-years. The work runs the first study's rule backward: 400 researcher-years at 1950 for a building or equipment, 200 for a recipe, 500 for a drug, growing 5.3% a year (3.5% in medicine) [BJVW]. So a building is about 30 researcher-years in 1900, 110 in 1925 and 400 in 1950. The Filling Station (PB1) is costed as a recipe: a pump at an existing store.

### 8.1 Engineering, 27
| | Discovery | Kind | World | Touches | Effect | Needs | Fee | Work |
|---|---|---|---|---|---|---|---|---|
| PE1 | Electric arc steel furnace | building | Dec 1900, France (Héroult); North America 5 Apr 1906, Syracuse (1907 by another source) | Electric Arc Furnace | steel from scrap by electricity, not fuel: a 3-t heat on a 500 kW supply (1906); early kWh a tonne not found [P2.20], [P2.21] | — | yes | 30 |
| PE2 | High-speed tool steel | equipment | 1900, Bethlehem; shown at Paris | Machine Works, Fabrication Shop, Fabrication Works | cutting speed 30 → 90 ft a minute [P2.24] | — | yes | 30 |
| PE3 | Rotary drilling | recipe | 10 Jan 1901, Spindletop (rotary: unverified) | Oil Well | soft-rock and salt-dome fields: one well about 100,000 bbl a day; Spindletop 20% of US output in 1902 (est.) [P1.63], [P1.61] | — | yes (est.) | 16 |
| PE4 | Long rotary kiln | building + equipment | 1902, New Village NJ (Edison) | Cement Plant, Large Cement Plant, Lime Kiln | kilns 60–80 ft → up to 150 ft; about 30 t a day a kiln (1900) → 680 t (1950); coal a tonne about the same [P2.29], [P2.30] | — | yes | 34 |
| PE5 | Rapid sand filter | equipment | 1902, Little Falls NJ (date unverified) | Water Treatment Plant | 60 times the slow-sand rate, so about 1/60 the filter land (est.); typhoid deaths −36% to −46% (two studies, three readings) [P1.44], [P1.40], [P1.41], [P1.42] | — | yes (est.) | 34 |
| PE6 | Steam-turbine station | equipment | 2 Oct 1903, Fisk Street, Chicago (5 MW) | Coal Power Plant | station efficiency about 3.6% (1900) toward 24.3% (1950) with PE16: 100 → 14.8 MJ of coal a kWh (est.) [P1.1], [P1.3], [P1.4], [P1.6] | — | yes | 35 |
| PE7 | Froth flotation | recipe | 1905, Broken Hill; for copper 1912, Britannia, BC | Copper Mine & Mill, Large Copper Mine | copper recovered 60–75% → about 90% [P2.10], [P2.14], [P2.15] | — | yes | 20 |
| PE8 | Open-pit porphyry copper | building | 1906, Bingham Canyon | Large Copper Mine | open-pit share of US copper 2% (1907) → 44% (1936); 88 → 259 lb of copper a man-day (1914–36) [P2.7], [P2.10], [P2.16] | PE7 | no (est.) | 41 |
| PE9 | Chlorination | recipe | Sep 1908, Jersey City; Lincoln, UK, 1905 | Water Treatment Plant | Toronto typhoid deaths 40.8 (1910) → 1.0 (1915) per 100,000; no separate effect found in 25 US cities [P1.43], [P1.46], [P1.42] | — | no (est.) | 23 |
| PE10 | Fat hydrogenation | recipe | 1909, Crosfield, Warrington | Snack & Oils Plant | solid fat from crops (`CROPS` → `FATS`); before, only lard and butter [P2.55] | — | yes | 24 |
| PE11 | Thermal cracking | building | Jan 1913, Whiting, Indiana (Burton) | Small Cracking Unit, Cracking Unit | petrol from crude about 12–13% → about 25%; about 0.24 L of petrol a litre of gas oil (est.) [P1.49], [P1.52], [P1.48] | — | yes | 59 |
| PE12 | Synthetic ammonia | building | Sep 1913, Oppau (BASF) | a fertiliser works (no `FERTILISER` good yet; Q11) | nitrogen from the air; no yield figure gathered [P2.54] | — | yes | 59 |
| PE13 | Moving assembly line | building + recipe | 7 Oct 1913, Highland Park (Ford) | Assembly Plant; Vehicle Works, Commercial Vehicle Plant | 12.5 → 1.55 man-hours a chassis; Model T $850 (1909) → $290 (1925) [P1.82], [P1.81] | — | no (est.) | 59 |
| PE14 | Gasoline tractor | equipment | 1917, Fordson | Grain Farm, Mixed Farm, Livestock Farm | replaces horses: about 3 acres of feed freed a horse; 27.3% of the US fall in farm jobs (1910–60) [P2.47] | — | no | 73 |
| PE15 | Mechanical coal loader | equipment | 1 Dec 1917, the first Joy loader | Underground Coal Mine | 5.5 → 8.5 short tons a man a day (one colliery, 1926); machine-loaded 0 → 31% (1939) [P2.8], [P2.12], [P2.13] | — | yes | 73 |
| PE16 | Pulverised coal, high-pressure steam | equipment | 1918, Oneida Street, Milwaukee | Coal Power Plant | the best plant 10,800 Btu a kWh against an average above 18,000: −40% (est.) [P1.5], [P1.7], [P1.3] | PE6 | yes | 77 |
| PE17 | Combine harvester | equipment | 1918, prairie combine with its own engine; self-propelled 1937 (Massey-Harris) | Grain Farm | wheat 40–50 → 15–20 hours per 100 bushels with PE14; 6.5 h (1955) [P2.48], [P2.53] | PE14 | yes (est.) | 77 |
| PE18 | Milking machine | equipment | about 1902–18 (unverified) | Mixed Farm, Livestock Farm | cows milked an hour a milker 10 → 30 [P2.51], [P2.52] | — | yes (est.) | 77 |
| PE19 | Continuous strip mill | equipment | 1923, Ashland KY (Armco) | Oxygen Steel Shop (its rolling side) | sheet about 0.5 → 8 t a man-shift (est.) [P2.22], [P2.23] | — | yes | 99 |
| PE20 | Hybrid corn | recipe | 1925, Iowa | Grain Farm, Mixed Farm | yield +9% to +20%; seed bought every year [P2.50] | — | no | 55 |
| PE21 | Diesel-electric locomotive | equipment | 1925, the Bronx (ALCO); CN 9000, 1928 | Freight Line, Rail Spur, Rail Terminal; Locomotive Works | fuel into work 7.5% → 28%: −73% fuel a tonne-km (est.) [P1.1], [P1.85], [P1.86] | — | yes | 110 |
| PE22 | Carbide cutting tools | equipment | 1927, Krupp (Widia) | Machine Works, Fabrication Shop, Fabrication Works, Vehicle Works | cutting speed about 4 times high-speed steel [P2.25] | PE2 | yes | 122 |
| PE23 | Delayed coking | building | 1929, Standard Oil of Indiana | Small Coker, Coker | residue into light products and coke (30% of its weight, the game's recipe) [R2], [P1.48] | PE11 | yes | 135 |
| PE24 | Catalytic cracking | recipe | Jun 1936, Paulsboro (Houdry; Apr 1936 or Mar 1937 by other sources); fluid 1942 | Cracking Unit | about 0.24 → 0.555 L of petrol a litre of gas oil fed to it; 48% gasoline at 81 octane [P1.49], [P1.50], [P1.51] | PE11 | yes | 97 |
| PE25 | Alkylation | building | 1938–40 | Small Alkylation Unit, Alkylation Unit | cracked gas into petrol at 0.80 L a litre (the game's recipe) [P1.57], [P1.58] | PE11 | yes | 215 |
| PE26 | One-man chainsaw | equipment | 1946, Vancouver (IEL; 1950 by another source) | Logging Camp | 2 → up to 4 cords a worker a day [P2.33], [P2.34] | — | yes (est.) | 325 |
| PE27 | Catalytic reforming | building | 1949, Muskegon (Platforming) | Small Reformer, Reformer | heavy naphtha into petrol at 0.82 L a litre; hydrogen for the later hydrocracker [P1.59] | — | yes | 380 |

### 8.2 Medicine, 6
Every row changes a mechanism the game has or the causes of death (§4) would add; none is a health multiplier. The medical side of clean water is on PE5 and PE9.

| | Discovery | Kind | World | Mechanism | Effect | Needs | Fee | Work |
|---|---|---|---|---|---|---|---|---|
| PM1 | Pasteurised milk (a by-law) | recipe | 1914, Toronto (1918 by another source); Ontario 1938 | milk-borne TB; babies' deaths | about 15% of TB cases came from dairy (c.1900); infected cattle 5% (1920s) → 2% (1950). No effect on babies' deaths found in 25 US cities [MOH-MILK], [TOR-MILK], [ACR] | — | no | 58 |
| PM2 | Insulin | drug | Jan 1922, Toronto General Hospital | a death becomes a lasting patient (as the first study's M4) | children with diabetes died "in a matter of months"; after, they lived on daily injections; the caseload was not found [INS-HK], [INS-HD] | — | yes (University of Toronto, for $1) | 191 |
| PM3 | Diphtheria toxoid | drug | 1923, Ramon; Connaught trials from Sep 1925 | childhood outbreaks; children's deaths | Hamilton 742 cases and 32 deaths (1922) → 5 and 0 (1931); Toronto cases down at least 90% with 3 doses [CON-D], [CON-R], [DIPH] | — | no (est.) | 198 |
| PM4 | Sulfa drugs | drug | 1935, Prontosil; in Canada from 1937 | recovery; mothers' and pneumonia deaths | US 1937–43: maternal deaths −24% to −36%, pneumonia deaths −17% to −32%, scarlet fever −52% to −65%, all deaths −2% to −3% [JLS], [SULF], [COLE] | — | partly | 298 |
| PM5 | Penicillin | drug | 1943–44, US mass production; Connaught, Apr 1944 | recovery; the long-sick's deaths; stays | gas gangrene, 150 deaths per 1,000 WWI casualties, almost gone in 1944–45. Canada, observed and not proven causal: stay 11.0 → 10.0 days (1945–50); maternal deaths 2.8 → 1.1 per 1,000 births (1944–50) [PEN], [CON-H], [SC-B285], [SC-B51] | — | yes (est.) | 393 |
| PM6 | Streptomycin | drug | Mar 1946, first cure; Canada 1948 | TB deaths among the game's long-term sick (a year or more) | deaths within six months 27% → 7% (MRC trial, 1946–48; unverified); Canadian TB beds kept rising until 1953 [STREP], [R4] | — | yes (est.) | 436 |

### 8.3 Business, 3
| | Discovery | Kind | World | Touches | Effect | Needs | Fee | Work |
|---|---|---|---|---|---|---|---|---|
| PB1 | Filling station | building | 1905, St. Louis; Canada 1907, Vancouver | Filling Station | petrol from a pump instead of by the can at a store [P1.80], [P1.79] | — | no | 20 |
| PB2 | Self-service grocery | recipe | 11 Sep 1916, Memphis (Piggly Wiggly); Toronto 1919 (Loblaw) | Small Grocery Store, Convenience Store | no clerk fetching each item; no delivery (about 3% of the bill) or charge account (about 2%). Posts a sale: not found [CPL-PW], [PW], [LOB] | — | no: the store was patented in the US, but the first study treats business methods as free | 35 |
| PB3 | Supermarket | recipe | Aug 1930, Queens (King Kullen); Quebec 1934 | Small Grocery Store | US supermarkets about 300 (1935) → over 15,000 (1950). Cost a sale: not found [WQ], [R2] | PB2 | no | 71 |

### 8.4 Law, 12
The Policy levers, on Canada's dates (§7): PL1 wage tax (1915), PL2 profit tax (1916), PL3 income tax (1917), PL4 minimum wage (1918), PL5 sales tax (1920), PL6 zoning (1924), PL7 pensions (1927), PL8 the central bank (1935), PL9 student loans (1939), PL10 EI (1940), PL11 care subsidies (1942), PL12 public hospital insurance (1947). Each is a recipe of the law field (work 33 researcher-years for PL1, rising to 171 for PL12), with no fee.

### 8.5 Each row's 1900 recipe (what undoing it leaves)
- **PE1:** no arc furnace; steel from Bessemer converters and open hearths fired by fuel; tool steel from crucibles (est.).
- **PE2, PE22:** carbon or Mushet tool steel at about 30 ft a minute, on machines belted to a steam engine's line shaft.
- **PE3:** cable-tool wells in hard-rock fields only.
- **PE4:** short kilns of about 60 ft making about 30 t a day each, burning coal at about 30% of the clinker's weight.
- **PE5, PE9:** raw or settled water at best through slow sand filters, with no disinfection; Toronto typhoid deaths about 40 per 100,000.
- **PE6, PE16:** reciprocating engines at about 150 psi, about 3.6% efficient: about 100 MJ of coal a kWh, 72 GJ per game electricity unit-month (est.).
- **PE7, PE8:** underground vein ore at about 2.7% copper, 60–75% recovered by gravity: about 18 kg of copper a tonne of ore (est.).
- **PE10:** `FATS` from animals only (lard and butter).
- **PE11, PE23–PE25, PE27:** shell stills cutting crude into kerosene (63% of output), fuel oil (15%), lube stock (8%) and light products (14%); no conversion units; residue sold as fuel oil.
- **PE12:** nitrogen from manure, clover and Chilean nitrate (est.).
- **PE13:** craft building: 12.5 man-hours just to assemble a chassis before the moving line.
- **PE14, PE17:** horses, 3.1 a Canadian farm (1901, est.), each eating the crop of about 3 acres; a binder and a separate threshing gang: 40–50 hours per 100 bushels of wheat.
- **PE15:** every tonne loaded by hand shovel, a quarter undercut by machine; about 3 short tons a man a day (unverified).
- **PE18:** hand milking, about 10 cows an hour; most butter churned in the farm kitchen.
- **PE19:** sheet and tinplate on hand mills: about 0.5 t a man-shift (est.).
- **PE20:** saved open-pollinated seed: about 25–28 bushels of corn an acre.
- **PE21:** hand-fired steam locomotives, 4–8% efficient.
- **PE26:** axes, crosscut saws and bucksaws: about 2 cords a worker a day.
- **PM1:** raw milk from about 900 farms within 40 miles of Toronto, sometimes 36 hours on the road (1909).
- **PM2:** a starvation diet; children with diabetes died within months.
- **PM3:** antitoxin treated diphtheria (1–5% of treated cases died), but nothing prevented it.
- **PM4, PM5:** no antibacterial drugs: pneumonia and childbed fever were nursed; stays of 15–24 days (1925).
- **PM6:** TB patients rested in a sanatorium for about a year (311 days in 1939).
- **PB1:** petrol in cans from general stores and hardware stores.
- **PB2, PB3:** a counter store of a few clerks with a delivery boy, carrying its customers' accounts.

### 8.6 Changes to the first study's catalogue
1. **M1 (TB drugs, 1952) needs PM6 (streptomycin).** M1 keeps its date and its effect on beds and stays, which came with isoniazid; PM6 carries streptomycin's effect on deaths. The people-and-services pass preferred folding them together and moving M1 to 1947–48; keeping them apart keeps the first study's figures.
2. **L1 (deposit insurance) stays at 1967** under the Policy rule (§7.2).
3. **A new L5, pension contributions (CPP), January 1966**, for the lever the 1951–1995 list lacked. With it the catalogue has 89 entries; the model in §10 was run on 88, without it.
4. **Two materials buildings have no entry:** the Panel Mill (oriented strand board, about 1980; waferboard 1960s) and the Wood Pellet Mill (1970s). Both dates are unverified (Q13).
5. **The Large Magnetite Complex** opens with E4 (taconite pellets, 1955), as the Pelletising Plant does.
6. **The catalytic reformer**, which the first study left available in 1950 as brand-new (its Q12), is now PE27 (1949). The self-propelled combine stays the 1937 form of PE17, adopted when it pays.

### 8.7 Held, for want of a sourced effect
Fingerprint identification (London 1901; Canada 1911, unverified), police radio and patrol cars (Detroit 1928), punched-card accounting machines, instalment credit (GMAC 1919), blood banks (1937), the iron lung (1928; fold into M2), the Owens bottle machine (1903; no glass good), coated-electrode welding (1907, unverified), machine bread plants (1910s; slicing 1928), tetraethyl lead (1923; the game has no octane), the reflection seismograph (1921), the over-water platform (1911), welded large pipelines (1920s–40s), tarmac (1902) and concrete roads (1909) [P1] §5, [P2] §6, [P3] §3.4.

---

## 9. History as the tutorial

What a city founded in January 1900 meets, decade by decade, if it does no research. Dates are history's.

| | Buildings | Recipes and equipment | Medicine | Policy | The world |
|---|---|---|---|---|---|
| **At founding** | 80 of the 101 kinds; streetcars, steam rail, a kerosene refinery, coal stations, horses on farms and streets | known equipment ready to fit when it pays (§3) | hospitals, X-rays, antitoxin | property tax, schools, services; the dollar on gold | the Laurier boom (from 1896) is under way |
| **1900s** | Electric Arc Furnace (1900), Large Cement Plant (1902), Filling Station (1905), Large Copper Mine (1906) | high-speed steel, rotary drilling, rapid filters, the steam turbine (1903), flotation, chlorination (1908), hydrogenation (1909) | | | immigrants 41,681 → 272,409 a year; Spindletop; the panic of 1907 |
| **1910s** | Cracking Units and the Assembly Plant (1913) | the tractor (1917), the coal loader (1917), pulverised coal, the combine and the milking machine (1918); synthetic ammonia (1913, waiting on a fertiliser good) | pasteurised milk (1914) | off gold and the policy rate (1914), wage tax as workers' compensation (1915), profit tax (1916), income tax (1917), minimum wage (1918); self-service groceries (1916) | the First World War: prices +90% (1914–20); the 1918 influenza |
| **1920s** | Cokers (1929) | the strip mill (1923), hybrid corn and the diesel locomotive (1925), carbide tools (1927) | insulin (1922), diphtheria toxoid (1923) | sales tax (1920), zoning (1924), back on gold (1926), pensions (1927) | the 1920–21 slump; motor vehicles in Canada 408,790 → 1,187,331 |
| **1930s** | Alkylation Units (1938) | catalytic cracking (1936) | sulfa drugs (1935) | off gold again (1929–31); **the Bank of Canada** (1935), student loans (1939); supermarkets (1930) | the Depression: unemployment 19.3% (1933) |
| **1940s** | Reformers (1949) | the chainsaw (1946) | penicillin (1943), streptomycin (1946) | EI (1940), care subsidies (1942), hospital insurance (1947) | the Second World War; Leduc (1947) |
| **1950 on** | the first study's 40 entries, 1951–1995 | | | pension contributions (1966), deposit insurance (1967) | |

**The point of it.** A new player meets a city of houses, streetcars, farms and coal, with one tax to set. Cars arrive with their filling stations and cracking units; the income tax arrives in the middle of a war; the central bank arrives in the middle of the Depression. A player who wants a dial early can found a Law school and research it.

---

## 10. What research does from 1900

The model (`scenarios_1900.py`, 400 cities each) runs the same city founded in 1900 and in 1950, with schools at the same months after founding and every field funded to its schools' room (the most posts they can hold). It runs the 88 entries, without the proposed L5. History runs out in January 2026.

| Pace | Schools (months after founding) | Founded 1950: finds here before 2026 | Founded 1900: finds here before 2026 | …of them before 1950 |
|---|---|---|---|---|
| **Early**: a player who builds schools for research | University 300, Law school 360, Medical school 480, Institute of technology 600 | 0.10 | **24.6** | **13.1** |
| **Brisk**: the hand-played run of 2026-09-04 (37,730 people by about month 726) | University 726; Institute of technology and Medical school 1,000 | 0.00 | **0.89** | 0 |
| **Slow**: the scripted playtest (17,000–30,000 people at month 1,070) | University 1,100 | 0.00 | 0.00 | 0 |

- **The early city**, before 1950, finds 5.1 of the 12 law entries, 3.9 of the 27 engineering, 1.9 of the 6 medical and 0.65 of the 3 business, plus 1.4 of the first study's 1950s entries early (mostly M1–M3); and about 11 more from 1950 to 2026 (a second run of 150 cities). It is 3.2 years ahead of the world on average. Its schools' full room costs D$ 9.9 million a month at today's post costs; at 1900's wages, about a quarter of that (est., §5.2).
- **It hardly depends on how cheap early ideas were.** With every pre-1950 entry held at 1950's work, the early city still finds 21.3 (10.0 before 1950); with half the growth rate, 22.7 (11.4). **It depends on how many researchers it pays for:** with half the posts it finds 14.7 (9.4 before 1950), while cutting the professors' research time from 40% to 10% changes little (22.5; 12.4), because paid posts are most of its researchers. Beyond that, what limits it is the science window (at most 7 years ahead of the world in engineering) and how many people can work on one problem.
- **The brisk city** opens its University in 1960 on a 1900 calendar, so the first study's 1951–1995 list is still partly ahead of it: hip replacements, CT scanners, statins and thin-slab casting are its usual finds. On a 1950 calendar its University opens in 2010, after history's list has ended.
- **The slow city** opens its University in 1991, too late for either list. **The near-future list (2025–2060) the first study recommended is still needed.**
- **The growth paces come from older builds** (0.6-era hand play, 0.7.103's scripted playtest) and are illustrative.

---

## 11. Questions for Jerus

**The three that matter most:**

1. **Policy on history's dates** (§7). Each lever opens on the date Canada first had it, at zero, with an inbox notice of its first terms; a Law school can bring one forward by up to four years. Money follows Canada's own history: the dollar is on gold, held at par by the city's reserves, to August 1914 and again from July 1926 to 1929–31, and floats in between and after; the government's Advance Rate (1914) opens the policy rate, and the Bank of Canada (1935) the rest of Money. **Recommendation:** yes. It is the strongest part of the tutorial, and it is what a city really controlled in 1900. If Money's dated states are too much, the simpler form is gold until 1935 unless the reserves run out.
2. **The 1900 economy (batch R0)** (§5.4). Founding wages at 1901's level, about a quarter of today's in real terms (est.); the world's prices, wheat, interest and migration from §6.1; the household's basket and the world's mix of skills by year; the residual gap measured sector by sector. **Recommendation:** batch R0 is a study and a batch of its own, before anything in research is built. Until then, nothing can be balanced.
3. **Which new mechanics** (§4). **Recommendation:** build six: horses as recipes that eat feed (farms and cartage), steam recipes for factories, household light and heat as goods, causes of death in `Health`, Money's gold years, and a Hydro Station (Q4). Leave out coal gas, natural ice, mail order, wartime mobilisation, rationing and tariffs; home butter and bread go into the household's 1900 basket.

**The rest:**

4. **Hydro power.** Canada's power in 1900 was hydro, and over 90% of capacity from 1920 to 1950 [P1.12]. **Recommendation:** a Hydro Station template in the electricity run, needing a river and a drop on Earth's map. It is a new mechanic, and today's game lacks it too.
5. **Doctors without beds.** In 1900 doctors mostly worked from their own offices and on house calls (est.); 1.02 per 1,000 people, as in 1951 [SC-B82]. **Recommendation:** the Community Health Centre's 1900 form is the dispensary and doctors' office (doctors, almost no nurses), available from founding.
6. **The drug minimum time before 1962.** The first study's 8 years is today's clinical trials; insulin went from the laboratory to patients in about a year. **Recommendation:** 2 years (est.) for drugs dated before 1962, when the US first required proof that a drug works (Kefauver–Harris, unverified date). The model above used 8 years throughout.
7. **Faculty research time before research universities.** The 40% is today's collective agreements; in 1900 professors mostly taught (unsourced). **Recommendation:** keep 40%. In §10 cutting it to 10% changed the early city's finds little (24.3 → 22.5), because paid posts are most of its researchers; halving the posts did matter (→ 14.7).
8. **The work before 1950.** **Recommendation:** run the measured growth backward (a building is 30 researcher-years in 1900); §10 shows it changes little.
9. **Self-service and the supermarket have no posts figure.** **Recommendation:** keep them as recipes that drop the counter store's delivery and credit costs (5% of the bill), and look for a posts figure when the retail batch is written.
10. **Streptomycin and M1** (§8.6). **Recommendation:** PM6 as M1's prerequisite with its own effect on TB deaths; M1 unchanged.
11. **Synthetic ammonia needs `FERTILISER`**, as the semi-dwarf wheat did (the first study's Q16). **Recommendation:** the same good; PE12 is the works that makes it.
12. **Buildings that did not exist in 1900 but need no discovery:** Middle School, Community Health Centre, Elevated Highway, Community College, Strategic Reserve, Large Surface Mine, Tree Plantation. **Recommendation:** available from founding, in their 1900 forms where they differ.
13. **The Panel Mill and Wood Pellet Mill have no entry.** **Recommendation:** add two entries to the 1951–1995 list when the wood batch is written, with sourced dates.
14. **The world's episodes** (§6.2). **Recommendation:** prices, wheat, interest and arrivals from §6.1; the 1918 influenza as an epidemic in `Health`; no mobilisation, price ceiling or rationing for now.
15. **Oil fields found after 1900** (§2.4): Spindletop 1901 to Az Zubayr 1949. **Recommendation:** the same question as the first study's Q13, now with more fields: for the Earth batches, whether a field is hidden until history finds it.
16. **A start year the player picks** (1900 or 1950). **Recommendation:** later; it comes from the same rule.
17. **Data to ship** (§6.4). **Recommendation:** ship Statistics Canada, Maddison, EIA and FHWA with credit; replace NBER prices with USGS DS140; key the pre-1926 GNP from Urquhart's published tables, or ask.

---

## 12. For the implementer

**Order, after every planned batch** (this replaces the first study's §9 order; the batch names are the first study's, not source keys):
1. **Batch R0, the 1900 economy and the world:** the epoch to 1900; founding wages by band (§5.2); the world's annual series (§6.1) as data; the household basket and the world's skill mix by year; Money's dated states (§7.2); the Policy levers as dated data, closed until their date (§7).
2. **Batch R0b, causes of death in `Health`:** typhoid read from water treatment, babies' diarrhoea, childhood outbreaks, TB among the long-term sick, deaths at home births, pneumonia, and the 1918 influenza.
3. **Batch R0c, the 1900 mechanics:** horse recipes on farms and road freight; household light and heat; steam recipes for factories; the Hydro Station with the electricity run.
4. **Batch R1, recipes and equipment as data** (the first study), now with 1900's forms: every building's recipe before each P-entry, and the known-in-1900 equipment (§3) unfitted at founding.
5. **Batch R2, the catalogue:** 89 entries (§8, the first study's §4, and L5), in `research.json`; history's dates; licence fees.
6. **Batches R3–R5** as the first study.

**Harnesses, added to the first study's:**
- a city with no research has every 1900–1950 entry on its date, and with all of them and the known equipment fitted, each building runs its 1950 figures;
- every Policy lever is closed before its date, opens at zero on it, and opens up to four years earlier only through a law find;
- on gold (1900–Aug 1914 and Jul 1926–1929/31), the D$ stays at par unless the reserves run out; off gold, it floats;
- a 1900 city's household spends on lamp oil and coal, and on power once its homes are electrified;
- the early, brisk and slow cities of §10 reproduce their finds within the Monte Carlo error.

**Save:** the levers' opening months, and whether the city is on gold. Old saves need not load until the game is published (Jerus, 2026-10-10).

---


### 12.1 Current-code handoff (2026-10-13)

This is an implementation pass over the current repository, not the older 0.7.108 tree cited in the opening note. The source and generated maps were inspected for `CityCalendar`, `PayTier`, `LabourMarket`, `PopulationManager`, `WorldEconomy`, `Consumption`, `CityBasket`, `Migration`, `Founding`, and the month order. No game code or tests were run or changed for this pass.

#### The important seams in today's code

| R0 requirement | Current source / fact | Consequence for the batch |
|---|---|---|
| Calendar starts in 1900 | `CityCalendar.EPOCH_YEAR` is 2000. Month 1 is January; the counter itself is used by saves, transactions, loans and histories. | Changing the epoch changes every existing save's displayed date. Treat this as a save-meaning change: the shipping batch must bump `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` together so an old city is refused, not silently re-dated. The shipper owns the version bump. |
| 1900 founding wages | `PayTier` owns six present-day Nova Scotia monthly wages; `PopulationManager.setWagesPerType()` copies them, and `LabourMarket.DEFAULT_MINIMUM_WAGE` is anchored to `PayTier.UNSKILLED`. The live wage is then priced by the labour market. | Do not simply change `PayTier` constants: that would rewrite today's ladder and all default cities. Give a newly founded 1900 city a historical starting ladder through an explicit history/founding seam, then let the existing labour market move it. The study's ~D$0.8k/month is a real-wage anchor, not a direct conversion of the census's nominal 1901 dollars. |
| Skill mix by year | `Migration` already chooses who arrives by skill band and wage/opportunity, but the current model is not a year-indexed Canadian immigration and education series. | Keep two inputs separate: how many people are arriving from outside, and the skill composition of those arrivals. Use annual immigration per Canadian capita as the external-volume signal, then normalize the educational shares independently. Do not add raw national arrivals as literal people to a city. |
| World prices through history | `WorldEconomy` currently makes a deterministic stochastic inflation path around a chosen mean; it does not contain the CPI/wholesale history in §6.1. Goods' base prices live elsewhere. | Add one historical price-level source with 1900 normalized to 1.0, and define exactly how the current stochastic component continues around it. Do not multiply historical CPI on top of an already compounded historical CPI path. Keep CPI (household cost of living) distinct from wholesale prices (business inputs); use the wheat series only as a relative price path unless a defensible conversion to the game's price units is documented. |
| Household basket by year | `consumption.json` is read by `Consumption`, but its own current contract says it is a **shadow basket**: actual households do not consume it yet. `CityBasket` reports what they would buy. | A year-dependent edit to `consumption.json` alone will not change actual shopping. Identify the live demand path before wiring a historical basket. Keep the 1900 light/heat goods for R0c, which introduces those mechanics, rather than referring to goods that do not exist yet. |
| World interest and city debt | R0 is queued after CD1–CD6, which are intended to replace the current city-debt pricing. The 1900 study's world bond yield is an outside-world historical observation, not automatically the right rate for every city's own debt. | Read the landed debt code when R0 begins. Feed the historical world rate to the outside-world/foreign-investor side it actually represents; do not overwrite a city's own curve if the new debt system derives that from its monetary regime, inflation expectations and credit risk. |
| Historical policy dates | The current policy and money systems are active model state, while §7 gives the proposed Canadian opening dates. | Gate the existing levers by dates in the model, not by greying out buttons only. Before a lever exists it must not affect monthly flows; on its date it becomes available at zero with its dated terms. Keep the Law-school advance as the sole exception. |

#### Recommended data contract

Use one small, immutable, keyed historical data class (for example `HistoricalEconomy`) rather than putting a second set of unrelated arrays into `Game`, `Migration` and `WorldEconomy`. This is a proposed name, not an existing class. It should answer questions for a game month, using `CityCalendar.yearOf(month)` and `monthOfYear(month)`:

- **Annual source rows:** year; CPI index; wholesale index; real GNP index; GDP per head in constant international dollars; wheat price; immigration count; unemployment where observed; external nominal/benchmark rate where sourced; skill shares where there is evidence.
- **Units:** indices are dimensionless and normalized to 1900 = 1.0 at the model boundary; rates are decimal annual rates; migration volume is a rate or per-capita measure, never a national headcount applied directly to a city; all money entering the game is converted to the game's founding-money units exactly once.
- **Between annual observations:** interpolate index *levels* between annual knots, then derive monthly growth from the level ratio. Interpolate rates linearly. For an unavailable series, return an explicit documented fallback, not zero or a silently repeated unrelated series.
- **Before/after the data:** January 1900 uses the 1900 row. For years after 1950, join smoothly to the existing model at the 1950 endpoint and then let the post-1950 system continue; avoid a jump in the price level, wages or interest at the join. If the project later permits a player-chosen start year, the epoch and historical offset must become per-city saved state rather than a mutable global constant.
- **Determinism:** the historical series itself has no random draws. Existing stochastic shocks can remain seeded, but their baseline must be explicit so a history path is not counted twice.

The §6.1 table already contains the principal annual anchors for 1900–1950: real GNP, GDP per head, CPI, wholesale prices, unemployment (from 1921), wheat and immigration. The source keys are listed in §13. Preserve the asterisks on the pre-1914 spliced CPI values in comments or data metadata; they are estimates, not observations. Do not fabricate annual rates or skill shares to fill a blank: source those series separately or document a simpler proxy.

#### Wage ladder: make the normalization explicit

The source evidence in §5.2 is annual nominal wage data from the 1901 census; the game pays monthly in thousands of founding D$. These are not directly comparable units. The study's estimate, D$0.8k per month for the unskilled floor, comes from taking today's D$3.46k floor to roughly 23% in real terms. It is an anchor to calibrate against, not an exchange-rate conversion.

The 1901 evidence gives useful relative observations against a male factory worker: farm hand 0.51, male labourer/domestic service 0.68, trade/transport 1.25, teachers 1.21, electricians 1.29, engineers 3.24. It does **not** identify reliable 1900 wages for every modern tier (notably doctors/lawyers, senior finance and some university jobs). R0 should therefore:

1. Record the mapping from each `JobType`/`PayTier` to a historical comparator in one table, with a source or an explicit estimate flag.
2. Normalize that ladder to the chosen unskilled floor once. Do not apply both a historical multiplier and `LabourMarket`'s cost-of-living multiplier to the same wage.
3. Keep the labour-market scarcity premium and the player's minimum-wage dial operational. Test the starting values before scarcity or local inflation moves them, then test that a month of simulation moves them through the normal labour-market path.
4. Report the unsupported tiers rather than imply that the census supplies a number it does not.

#### A small, high-value harness set for R0

- **Calendar:** month 1 = January 1900; months 12 and 13 = December 1900 and January 1901; month 121 = January 1910. Gregorian leap-year checks must still make 1900 a common year and 1904 a leap year.
- **Historical table:** each annual anchor returns its exact row; monthly lookup is deterministic and monotonic where the source index is monotonic; the 1900 index is exactly 1.0; spliced/estimated observations remain marked.
- **Join:** the 1950 endpoint and first post-history month have no unexplained jump in the price level, wage baseline, or outside rate. A historical level and the current stochastic inflation mechanism must not compound the same inflation twice.
- **Wages:** a fresh 1900 city starts at the declared historical floor and relative ladder; an ordinary modern city still starts on today's `PayTier` ladder; scarcity, cost-of-living and the player's dial still operate after founding.
- **Migration:** external volume and skill mix change at the intended historical dates, the shares sum to one, no arrival count becomes negative, and a small city receives a scaled flow rather than Canada's national total.
- **Policy:** every dated lever is closed before its date, opens at zero on its date, and the four-year early-opening rule is available only when a Law discovery actually occurs.
- **Repeatability and saves:** two runs of the same seed produce the same historical path; saving and loading preserve the same date and historical state. If the epoch change would misread old saves, assert that the loader refuses them after the format floor is raised.
- **Accounting:** run the existing money-conservation harnesses against the historical price and migration paths. A historical data change is not permission to relax cent-exact conservation.

#### Scope boundary

R0 should deliver the date/epoch, the dated macro series and the initial wage/skill/basket inputs, plus their harnesses. R0b owns the new causes of death; R0c owns horses, steam recipes, lamp oil/heat and hydro. R1 owns research recipes/equipment. The historical data can describe those later mechanics, but R0 must not quietly introduce their goods, buildings or health causes ahead of their queued batches.

**No code was changed or executed for this handoff.** The next implementation session should re-open the current source after CD1–CD6 have landed, because this handoff is based on the repository as inspected on 2026-10-13.

## 13. Sources

*Keys `[P1.n]` and `[P2.n]` are source n of the energy-and-transport and materials passes; the named keys are the people-and-services and economy passes' own. `[R2]`, `[BJVW]` and `[SC-E]` are the first study's (`research-design.md` §11); `[R2]` and `[R4]` are its research files `R2-1950-baseline.md` and `R4-medical-business-law.md`, in `research-sources.zip`. The four research files, with every source, the arithmetic behind each (est.) and the gaps, went with this study as `research-1900-sources.zip`: `P1-energy-oil-transport.md`, `P2-materials-farms-food.md`, `P3-people-and-services.md`, `P4-economy-world-policy.md`. They are not in the project, which is near its knowledge limit.*


**Energy, oil and transport** (pass P1)
- [P1.1] Ayres, R.U. et al. (2003), "Exergy, power and work in the US economy, 1900–1998", *Energy* 28(3):219–273; reposted at https://energyskeptic.com/2019/steam-engines-exergy-power-work-in-us-1900-1998/
- [P1.3] *Power Engineering* (1996), "Steam turbines power an industry", https://www.power-eng.com/coal/steam-turbines-power-an-industry/
- [P1.4] Wikipedia, "Fisk Generating Station", https://en.wikipedia.org/wiki/Fisk_Generating_Station
- [P1.5] ASME Landmark #51, Port Washington Power Plant, https://www.asme.org/wwwasmeorg/media/ResourceFiles/AboutASME/Who%20We%20Are/Engineering%20History/Landmarks/51-Port-Washington-Power-Plant.pdf
- [P1.6] EIA, Annual Energy Review Table A6 (heat rates), via [R2], https://www.eia.gov/totalenergy/data/browser/?tbl=TA6
- [P1.7] Wikipedia, "Pulverized coal-fired boiler", https://en.wikipedia.org/wiki/Pulverized_coal-fired_boiler
- [P1.8] Gould, J.M. (1946), *Output and Productivity in the Electric and Gas Utilities*, NBER, ch. on electricity, https://www.nber.org/system/files/chapters/c4272/c4272.pdf
- [P1.9] Gould (1946), ch. on manufactured gas, https://www.nber.org/system/files/chapters/c4273/c4273.pdf
- [P1.10] Smil, V., "Fundamental energy transitions can take a century", *IEEE Spectrum*, https://spectrum.ieee.org/fundamental-energy-transitions-can-take-a-century
- [P1.12] *The Canadian Encyclopedia*, "Hydroelectricity", https://thecanadianencyclopedia.ca/en/article/hydroelectricity
- [P1.13] Canada Year Book 1925, p. 389 (central electric stations), https://www66.statcan.gc.ca/eng/1925/192504270389_p.%20389.pdf
- [P1.14] Hannah, L. (2009), "The Global Supply of Lighting in 1900", Business History Conference, https://thebhc.org/node/939
- [P1.15] BLS Bulletin 53 (1904), cost of living of 2,567 families, 1901, https://fraser.stlouisfed.org/files/docs/publications/bls/bls_v09_0053_1904.pdf
- [P1.17] Wikipedia, "Ice trade", https://en.wikipedia.org/wiki/Ice_trade
- [P1.18] Olmstead, A. & Rhode, P. (2001), "Reshaping the Landscape", *J. Econ. Hist.* 61(3), https://faculty.econ.ucdavis.edu/faculty/alolmstead/Recent_Publications/Reshaping_the_Landscape.pdf
- [P1.19] Morris, E. (2007), "From Horse Power to Horsepower", *ACCESS* 30, https://www.accessmagazine.org/wp-content/uploads/sites/7/2016/07/Access-30-02-Horse-Power.pdf
- [P1.20] Olson, S. (2017), "The Urban Horse and the Shaping of Montreal, 1840–1914", in *Animal Metropolis*, https://ucp.manifoldapp.org/read/animal-metropolis/section/72132365-5401-4b5f-9b5d-567263cc126f
- [P1.21] USDA Office of Road Inquiry, Bulletin 15 (1895), https://ageconsearch.umn.edu/record/342987/files/ORI-Bulletin15.pdf
- [P1.22] NBER Macrohistory, revenue per freight ton-mile, all US railroads (FRED A0303BUSA259NNBR), https://fred.stlouisfed.org/data/A0303BUSA259NNBR
- [P1.25] FHWA, Highway Statistics Summary to 1995, Table MV-200, https://www.fhwa.dot.gov/ohim/Summary95/mv200.pdf
- [P1.26] Canada Year Book 1934–35, p. 752 (motor vehicles 1907–1933), https://www66.statcan.gc.ca/fra/1934-35/193408020752_p.%20752.pdf
- [P1.27] Canada Year Book 1926, p. 608 (electric railways 1901–1925), https://www66.statcan.gc.ca/eng/1926/192606500608_p.%20608.pdf
- [P1.28] Schrag, Z., "Urban Mass Transit in the United States", EH.net, https://eh.net/encyclopedia/urban-mass-transit-in-the-united-states/
- [P1.29] Wikipedia, "Toronto Railway Company", https://en.wikipedia.org/wiki/Toronto_Railway_Company
- [P1.30] Transit Toronto, "A Brief History of the TTC Bus Fleet Before 1959", https://transittoronto.ca/bus/8512.shtml
- [P1.31] *Urban Transport Magazine*, "130 years of motor bus service", https://www.urban-transport-magazine.com/en/130-years-of-motor-bus-service/
- [P1.33] Wikipedia, "Daimler Motor Lastwagen", https://en.wikipedia.org/wiki/Daimler_Motor_Lastwagen
- [P1.34] FHWA (1994), highway history text, https://www.fhwa.dot.gov/ohim/1994/text/history.htm
- [P1.35] Historical Society of Ottawa, "Asphalt Paving Comes to Ottawa", https://www.historicalsocietyottawa.ca/publications/ottawa-stories/significant-technological-changes-in-the-city/asphalt-paving-comes-to-ottawa
- [P1.40] Population Reference Bureau, summary of Cutler & Miller, https://www.prb.org/resource/clean-waters-historic-effect-on-u-s-mortality-rates-provides-hope-for-developing-countries/
- [P1.41] Cutler, D. & Miller, G. (2004), NBER WP 10511, https://www.nber.org/papers/w10511
- [P1.42] Anderson, Charles & Rees (2019), Cato Research Brief 150 (NBER WP 25027), https://www.cato.org/publications/research-briefs-economic-policy/public-health-efforts-decline-urban-mortality
- [P1.43] Wikipedia, "Water chlorination", https://en.wikipedia.org/wiki/Water_chlorination
- [P1.44] Wikipedia, "George W. Fuller", https://en.wikipedia.org/wiki/George_W._Fuller
- [P1.46] Lost Rivers, "Toronto's Water Supply", https://lostrivers.ca/points/watersupply.htm
- [P1.47] City of Toronto, "An Infectious Idea: Clean Water and Sewage Treatment", https://www.toronto.ca/explore-enjoy/history-art-culture/online-exhibits/web-exhibits/web-exhibits-local-government/an-infectious-idea-125-years-of-public-health-in-toronto/an-infectious-idea-clean-water-and-sewage-treatment/
- [P1.48] NBER chapter on petroleum refining output, 1899–1937 (Fabricant, *The Output of Manufacturing Industries*), https://www.nber.org/system/files/chapters/c6421/c6421.pdf
- [P1.49] *Invention & Technology*, "The Wizard of Octane", https://www.inventionandtech.com/content/wizard-octane-0
- [P1.50] Science History Institute, *Distillations*, "Cracking Down on Crude Oil", https://www.sciencehistory.org/distillations/cracking-down-on-crude-oil
- [P1.51] *Hydrocarbon Processing* (2017), "FCC process history driven by war, competition", https://www.hydrocarbonprocessing.com/conference-news/2017/07/irpc-americas-17-fcc-process-history-driven-by-war-competition
- [P1.52] Penn State FSC 432, "The Thermal Refinery (1910–1940)", https://courses.ems.psu.edu/fsc432/node/757
- [P1.57] Wikipedia, "Alkylation unit", https://en.wikipedia.org/wiki/Alkylation_unit
- [P1.58] Penn State FSC 432, alkylation, https://courses.ems.psu.edu/fsc432/node/724
- [P1.59] Wikipedia, "Catalytic reforming", https://en.wikipedia.org/wiki/Catalytic_reforming
- [P1.61] EIA, US field production of crude oil, annual, https://www.eia.gov/dnav/pet/hist/LeafHandler.ashx?n=PET&s=MCRFPUS1&f=A
- [P1.62] EIA, US crude oil first purchase price, annual, https://www.eia.gov/dnav/pet/hist/LeafHandler.ashx?n=PET&s=F000000__3&f=A
- [P1.63] Wikipedia, "Spindletop", https://en.wikipedia.org/wiki/Spindletop
- [P1.66] Wikipedia, "List of oil fields", https://en.wikipedia.org/wiki/List_of_oil_fields
- [P1.73] Wikipedia, "History of the petroleum industry in Canada", https://en.wikipedia.org/wiki/History_of_the_petroleum_industry_in_Canada
- [P1.75] Wikipedia, "Norman Wells", https://en.wikipedia.org/wiki/Norman_Wells
- [P1.78] Fairbank Oil, "The Early Refining of Black Gold in Lambton County", https://fairbankoil.com/the-early-refining-of-black-gold-in-lambton-county/
- [P1.79] *The Canadian Encyclopedia*, "Vancouver Feature: Canada's First Gas Station", https://thecanadianencyclopedia.ca/en/article/vancouver-feature-canada-s-first-gas-station-opens-for-business
- [P1.80] Wikipedia, "Filling station", https://en.wikipedia.org/wiki/Filling_station
- [P1.81] Wikipedia, "Ford Model T", https://en.wikipedia.org/wiki/Ford_Model_T
- [P1.82] Wikipedia, "Assembly line", https://en.wikipedia.org/wiki/Assembly_line
- [P1.85] Wikipedia, "Dieselisation", https://en.wikipedia.org/wiki/Dieselisation
- [P1.86] Toronto Railway Historical Association, "Canadian National's First Mainline Diesel Locomotives", https://www.trha.ca/canadian-nationals-first-mainline-diesel-locomotives/
- [P1.87] Wikipedia, "Hulett", https://en.wikipedia.org/wiki/Hulett

**Materials, farms and food** (pass P2)
- [P2.1] Fabricant & Shiskin, *The Output of Manufacturing Industries, 1899–1937*, NBER 1940, ch. 8 Iron and steel. https://www.nber.org/system/files/chapters/c6424/c6424.pdf
- [P2.2] "The Development of the Dominion Iron and Steel Company" (Univ. of Pittsburgh research archive). https://sites.pitt.edu/~zander/Research%20archive/Johnson%20Company/MoxhamatDISCO.html
- [P2.3] BLS Bulletin 474, *Productivity of Labor in Merchant Blast Furnaces*, Dec 1928. https://fraser.stlouisfed.org/title/productivity-labor-merchant-blast-furnaces-4056/fulltext
- [P2.5] 911Metallurgist, "Beehive Oven" (2017). https://www.911metallurgist.com/blog/beehive-oven/
- [P2.7] Barger & Schurr, *The Mining Industries, 1899–1939*, NBER 1944, ch. 6 Mechanization. https://www.nber.org/system/files/chapters/c6321/c6321.pdf
- [P2.8] Same, ch. 8 Bituminous coal (Table 15). https://www.nber.org/system/files/chapters/c6324/c6324.pdf
- [P2.9] Same, ch. 11 Iron mining (Tables 20–21). https://www.nber.org/system/files/chapters/c6327/c6327.pdf
- [P2.10] Same, ch. 12 Copper (Tables 22–24). https://www.nber.org/system/files/chapters/c6328/c6328.pdf
- [P2.12] Coal Age, "100 Years with Coal Age", p. 2 (1920s). https://www.coalage.com/features/100-years-with-coal-age/2/
- [P2.13] e-WV, "Joy Loading Machine". https://www.wvencyclopedia.org/entries/1021
- [P2.14] Wikipedia, Froth flotation. https://en.wikipedia.org/wiki/Froth_flotation
- [P2.15] CIM / OneMine, "The Application of Froth Flotation Technology to Copper in Canada in the Early 20th Century" (2019). https://onemine.org/documents/the-application-of-froth-flotation-technology-to-copper-in-canada-in-the-early-20th-century
- [P2.16] Wikipedia, Bingham Canyon Mine. https://en.wikipedia.org/wiki/Bingham_Canyon_Mine
- [P2.17] Wikipedia, Mesabi Range. https://en.wikipedia.org/wiki/Mesabi_Range
- [P2.20] OneMine (AIME), "The First Half-Century of Electric Furnace Steel Making". https://onemine.org/documents/the-first-half-century-electric-furnace-steel-making
- [P2.21] Wikipedia, Paul Héroult; Electric arc furnace. https://en.wikipedia.org/wiki/Paul_H%C3%A9roult ; https://en.wikipedia.org/wiki/Electric_arc_furnace
- [P2.22] Wikipedia, Strip mill. https://en.wikipedia.org/wiki/Strip_mill
- [P2.23] B. J. Widick, *Steel Workers Organizer* editorial, July 1939. https://www.marxists.org/history/etol/writers/widick/1939/07/unions2.htm
- [P2.24] Wikipedia, High-speed steel. https://en.wikipedia.org/wiki/High-speed_steel
- [P2.25] *Cutting Tool Engineering*, "Celebrating a century of cemented carbide"; Mitsubishi Materials, "History of cutting tools". https://www.ctemag.com/node/47628 ; https://mmc-carbide.com/permanent/courses/91/history-of-cutting-tools.html
- [P2.26] Warren D. Devine Jr., "From Shafts to Wires", *Journal of Economic History* 43 (1983). https://gwern.net/doc/economics/automation/1983-devine.pdf
- [P2.27] R. H. Thurston, *Steam Engines: Simple and Compound*, 4th ed., 1890. https://ia800403.us.archive.org/15/items/stationarysteame00thur/stationarysteame00thur.pdf
- [P2.29] cementkilns.co.uk, "Early opinions on rotary kilns". https://cementkilns.co.uk/cemkilndoc051.html
- [P2.30] Wikipedia, Edison Portland Cement Company. https://en.wikipedia.org/wiki/Edison_Portland_Cement_Company
- [P2.33] Heritage Newfoundland & Labrador, "Mechanization of the Logging Industry Since Confederation". https://www.heritage.nf.ca/articles/economy/mechanization.php
- [P2.34] The BC Review #858, "Chainsaws: A History"; Wikipedia, Chainsaw. https://thebcreview.ca/2020/07/05/858-allen-lee-chainsaws-a-history/ ; https://en.wikipedia.org/wiki/Chainsaw
- [P2.35] Canada Year Book 1926, pulp and paper, p. 282. https://www66.statcan.gc.ca/eng/1926/192603220282_p.%20282.pdf
- [P2.37] Wikipedia, Kraft process. https://en.wikipedia.org/wiki/Kraft_process
- [P2.38] Canada Year Book 1907, census of industries for 1900: food https://www65.statcan.ca/acyb02/1907/acyb02_1907013701b-eng.htm ; all groups https://www65.statcan.gc.ca/acyb02/1907/acyb02_1907013701a-eng.htm ; iron and steel https://www65.statcan.gc.ca/acyb02/1907/acyb02_1907013701d-eng.htm
- [P2.40] Canada Year Book 1926, Statistical summary 1871–1901. https://www66.statcan.gc.ca/eng/1926/192600320000_Statistical%20summary%20of%20the%20Dominion%20of%20Canada.pdf
- [P2.41] Canada Year Book 1916–17, Statistical Summary of the Progress of Canada (1911, 1916). https://www66.statcan.gc.ca/eng/1916-17/191700160000_Statistical%20Summary%20of%20the%20Progress%20of%20Canada.pdf
- [P2.42] Statistics Canada, "150 years of Canadian Agriculture". https://www150.statcan.gc.ca/pub/11-627-m/11-627-m2017018-eng.htm
- [P2.43] Statistics Canada, *The Daily*, 5 May 2017, infographic (share of employed in agriculture). https://www150.statcan.gc.ca/n1/daily-quotidien/170505/cg-a003-eng.htm
- [P2.44] Statistics Canada, "Are horses going the way of the bison?". https://www.statcan.gc.ca/o1/en/plus/9262-are-horses-going-way-bison
- [P2.45] Statistics Canada, "A-tread by a century: 100 years of tractor data" (2023). https://www.statcan.gc.ca/o1/en/plus/3681-tread-century-100-years-tractor-data
- [P2.47] Olmstead & Rhode, "Reshaping the Landscape: The Impact and Diffusion of the Tractor in American Agriculture, 1910–1960", *JEH* 61 (2001). https://faculty.econ.ucdavis.edu/faculty/alolmstead/Recent_Publications/Reshaping_the_Landscape.pdf
- [P2.48] USDA "Growing a Nation" farm machinery timeline (secondary copy). https://www.tapatalk.com/groups/tgta/historical-timeline-farm-machinery-technology-t396.html
- [P2.50] Richard Sutch, "The Impact of the 1936 Corn Belt Drought on American Farmers' Adoption of Hybrid Corn", NBER 2011. https://www.nber.org/system/files/chapters/c11987/c11987.pdf
- [P2.51] Csorba & Butler, *Dairy Cows: Housing and Methods of Milking*, USDA ERS-15 (c. 1961). https://ageconsearch.umn.edu/record/319936/files/ERS-15.pdf
- [P2.52] Ingenium, Milking machine (De Laval, Peterborough). https://collections.ingenium.ca/item/2012.1187.001/
- [P2.53] Wikipedia, Combine harvester. https://en.wikipedia.org/wiki/Combine_harvester
- [P2.54] BASF, "Ammonia synthesis" (Science around us). https://basf.com/global/documents/en/news-and-media/science-around-us/fertilizer-out-of-thin-air/BASF_Science_around_us_Ammonia_synthesis.pdf
- [P2.55] AOCS, "Wilhelm Normann (1870–1939)"; Wikipedia, Shortening. https://www.aocs.org/resource/wilhelm-normann-1870-1939/ ; https://en.wikipedia.org/wiki/Shortening
- [P2.56] *Machine Design*, "ASME's 20th-century milestones in manufacturing" (Owens AR). https://www.machinedesign.com/mechanical/asme-s-20th-century-milestones-manufacturing
- [P2.57] Wikipedia, Refrigerator car. https://en.wikipedia.org/wiki/Refrigerator_car

**People, services, the economy and policy** (passes P3 and P4)
- [A1], [A67], [A248], [A350]: Statistics Canada, *Historical Statistics of Canada* (2nd ed., 11-516-X), series A1, A67–69, A248–253, A350. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectiona/4147436-eng.htm
- [ACR] Anderson, Charles and Rees, "Public Health Efforts and the Decline in Urban Mortality", IZA DP 11773 (AEJ Applied 2022). https://docs.iza.org/dp11773.pdf; abstract: https://www.aeaweb.org/articles?id=10.1257/app.20190034
- [AWCBC]: Association of Workers' Compensation Boards of Canada, history. https://awcbc.org/about-us/history
- [BL97] Bourbeau, Légaré and Émond, *New Birth Cohort Life Tables for Canada and Quebec, 1801–1991*, Statistics Canada 91F0015MPE, 1997, Table 6.1 (1901), via the Human Life-Table Database. https://www.lifetable.de/File/GetDocument/data\CAN\CAN000019011901AU1.pdf
- [BOC-BR]: Bank of Canada, Bank Rate monthly series from 1935. https://www.bankofcanada.ca/wp-content/uploads/2010/09/selected_historical_v122530.pdf
- [BOC-W]: Wikipedia, "Bank of Canada". https://en.wikipedia.org/wiki/Bank_of_Canada
- [BURNS]: Burns, R.E. (1917), *The Income War Tax Act 1917: A Digest*, and House of Commons debate extracts. https://wartimecanada.ca/sites/default/files/documents/IncomeWarTax.1917.pdf
- [BW-W]: Wikipedia, "Bretton Woods system". https://en.wikipedia.org/wiki/Bretton_Woods_system
- [CDIC-W]: Wikipedia, "Canada Deposit Insurance Corporation". https://en.wikipedia.org/wiki/Canada_Deposit_Insurance_Corporation
- [CEF-W]: Wikipedia, "Canadian Expeditionary Force" (619,636 enlisted, 60,661 died, citing Nicholson's official history). https://en.wikipedia.org/wiki/Canadian_Expeditionary_Force
- [CM]: McInnis, R.M. (2001), *Historical Canadian Macroeconomic Dataset 1871–1994*, University of Saskatchewan Data Library. https://datalib.usask.ca/dli/business/histCanMacroData/ (guide: https://datalib.usask.ca/dli/business/histCanMacroData/CANMACRO_guide.pdf)
- [COLE] Wikipedia, "Leonard Colebrook". https://en.wikipedia.org/wiki/Leonard_Colebrook
- [CON-D] Connaught Fund history, "A Relentless Pace – Defeating Diphtheria". https://connaught.research.utoronto.ca/node/1743
- [CON-H] Connaught Fund history, article 6 (penicillin, serum). https://connaught.research.utoronto.ca/about/history/article6
- [CON-R] Connaught Fund, "Reduction in Diphtheria" (*Canadian Public Health Journal* 1931). https://connaught.research.utoronto.ca/reduction-diphtheria-canadian-public-health-journal
- [CPI]: Statistics Canada, table 18-10-0005-01, Consumer Price Index, annual (1914=6.0, 1950=12.5, 2025=164.2; 2002=100). https://www150.statcan.gc.ca/t1/tbl1/en/tv.action?pid=1810000501
- [CPL-PW] Chicago Public Library, "Technology That Changed Chicago: Piggly Wiggly". https://www.chipublib.org/technology-that-changed-chicago-piggly-wiggly
- [CPP-W]: Wikipedia, "Canada Pension Plan". https://en.wikipedia.org/wiki/Canada_Pension_Plan
- [CYB07a]: Canada Year Book 1907, "Earnings at regular work, by class of occupation, 1901". https://www65.statcan.gc.ca/acyb02/1907/acyb02_1907015901-eng.htm
- [CYB07b]: Canada Year Book 1907, "Employees and salaries, by principal occupation in the professional class, 1901". https://www65.statcan.gc.ca/acyb02/1907/acyb02_1907015903-eng.htm
- [CYB18]: Annuaire du Canada 1918, p. 485, "Budget hebdomadaire d'une famille… 60 villes canadiennes, 1900, 1905, 1910–1917". https://www66.statcan.gc.ca/fra/1918/191805150485_p.%20485.pdf
- [CYB25]: Canada Year Book 1925, pp. 760–763, "War Taxation in Canada". https://www66.statcan.gc.ca/eng/1925/192507980760_p.%20760.pdf (and …192507990761_p.%20761.pdf, …192508000762_p.%20762.pdf, …192508010763_p.%20763.pdf)
- [CYB26] *Canada Year Book* 1926, pp. 913, 915, 916, 917 (hospitals in NS, Quebec and Ontario, 1925; Ontario grant rules). https://www66.statcan.gc.ca/eng/1926/192609550913_p.%20913.pdf, https://www66.statcan.gc.ca/eng/1926/192609570915_p.%20915.pdf, https://www66.statcan.gc.ca/eng/1926/192609580916_p.%20916.pdf, https://www66.statcan.gc.ca/eng/1926/192609590917_p.%20917.pdf
- [CYB27S]: Canada Year Book 1927–28, school attendance 1901–1921. https://www65.statcan.gc.ca/acyb02/1927/acyb02_19270143042a-eng.htm ; p. 923, enrolment. https://www66.statcan.gc.ca/eng/1927-28/192709650923_p.%20923.pdf
- [D1], [D8], [D86], [D107], [D123], [D124]: HSC series D1–7, D8–85, D86–106, D107–122, D123, D124–133. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectiond/4057750-eng.htm
- [DIPH] Wikipedia, "Diphtheria vaccine". https://en.wikipedia.org/wiki/Diphtheria_vaccine
- [E41], [E128], [E198]: HSC series E41–48, E128–135, E198–208. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectione/4147438-eng.htm
- [EATON] Wikipedia, "Eaton's". https://en.wikipedia.org/wiki/Eaton%27s
- [FLU-W]: Wikipedia, "Spanish flu". https://en.wikipedia.org/wiki/Spanish_flu
- [FMC-5.1], [FMC-5.4], [FMC-8.1], [FMC-8.3], [FMC-3.1], [FMC-3.2]: Caplow, Hicks and Wattenberg, *The First Measured Century* (PBS). https://www.pbs.org/fmc/book/5living1.htm, https://www.pbs.org/fmc/book/5living4.htm, https://www.pbs.org/fmc/book/8health1.htm, https://www.pbs.org/fmc/book/8health3.htm, https://www.pbs.org/fmc/book/3education1.htm, https://www.pbs.org/fmc/book/3education2.htm
- [G473]: HSC series G473–487. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectiong/4147439-eng.htm
- [GOR53] T. Gordon, "Mortality in the United States, 1900–1950", *Public Health Reports* 68(4), 1953. https://stacks.cdc.gov/view/cdc/71905/cdc_71905_DS1.pdf
- [H1], [H113]: HSC series H1–18, H113–123, H428–435. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionh/4057752-eng.htm
- [HIDS-W]: Wikipedia, "Hospital Insurance and Diagnostic Services Act". https://en.wikipedia.org/wiki/Hospital_Insurance_and_Diagnostic_Services_Act
- [ILO63]: *International Labour Review* 87(5), 1963, pp. 456–471, on Canada's unemployment insurance. https://webapps.ilo.org/public/libdoc/ilo/P/09602/09602(1963-87-5)456-471.pdf
- [INS-HD] Wikipedia, "History of diabetes". https://en.wikipedia.org/wiki/History_of_diabetes
- [INS-HK] Hektoen International, "The impact of insulin on children with diabetes at Toronto Sick Kids in the 1920s". https://hekint.org/2018/05/15/part-i-the-impact-of-insulin-on-children-with-diabetes-at-toronto-sick-kids-in-the-1920s/
- [JLS] Jayachandran, Lleras-Muney and Smith, "Modern Medicine and the Twentieth Century Decline in Mortality: Evidence on the Impact of Sulfa Drugs". https://escholarship.org/uc/item/68x5c398
- [K1], [K33]: HSC series K1–7, K8–18, K33–43. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionk/4057753-eng.htm
- [LAC-TB] Library and Archives Canada, TB history: statistics. https://epe.lac-bac.gc.ca/100/205/301/ic/cdc/tuberculosis/tbhistory/impact/stats.html
- [LOB] Ontario Heritage Trust, "Theodore Pringle Loblaw" (plaque background paper). https://www.heritagetrust.on.ca/provincial-plaque-program/provincial-plaque-background-papers/theodore-pringle-loblaw
- [LOP]: Library of Parliament, *Canada's Public Pension System* (MR-58). https://publications.gc.ca/Collection-R/LoPBdP/MR/mr58-e.htm
- [M78], [M228]: HSC series M78–88, M228–238. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionm/4057754-eng.htm
- [MAD]: Maddison Project Database 2023 (Bolt and van Zanden), via Our World in Data. https://ourworldindata.org/grapher/gdp-per-capita-maddison-project-database ; https://www.rug.nl/ggdc/historicaldevelopment/maddison/releases/maddison-project-database-2023
- [MMWR99] CDC, "Control of Infectious Diseases, 1900–1999", *MMWR* 48(29). https://www.cdc.gov/mmwr/preview/mmwrhtml/mm4829a1.htm
- [MOH-MILK] Museum of Health Care, "Pasteurization". https://museumofhealthcare.ca/explore/exhibits/breath/pasteurization.html
- [NMC-BB] J. F. Johnson, *The Canadian Banking System* / "Branch Banking in Canada", National Monetary Commission, 1910 (FRASER full text). https://fraser.stlouisfed.org/title/branch-banking-canada-802/fulltext
- [OREO]: Oreopoulos, P. (2005), *Canadian Compulsory School Laws and their Impact on Educational Attainment*, Statistics Canada 11F0019MIE2005251 (dates as listed in Wikipedia, "Education in Canada"). http://publications.gc.ca/Collection/Statcan/11F0019MIE/11F0019MIE2005251.pdf ; https://en.wikipedia.org/wiki/Education_in_Canada
- [PEN] Wikipedia, "History of penicillin". https://en.wikipedia.org/wiki/History_of_penicillin
- [POWELL]: Powell, J. (2005), *A History of the Canadian Dollar*, Bank of Canada. https://www.bankofcanada.ca/wp-content/uploads/2010/07/dollar_book.pdf
- [PW] Wikipedia, "Piggly Wiggly". https://en.wikipedia.org/wiki/Piggly_Wiggly
- [Q97]: HSC series Q97–101. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionq/4057756-eng.htm
- [SC-B1], [SC-B15], [SC-B35], [SC-B51], [SC-B59], [SC-B65], [SC-B82], [SC-B141], [SC-B285], [SC-B304], [SC-B432]: *Historical Statistics of Canada*, Section B (series as numbered), 1983. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionb/4147437-eng.htm
- [SC-UI]: Statistics Canada, *Perspectives on Labour and Income* 1998 (75-001), "Unemployment insurance: a history" (read through a mirror). https://www150.statcan.gc.ca/n1/pub/75-001-x/1998002/3828-eng.pdf
- [SC-W67], [SC-W150], [SC-W439], [SC-W504]: Section W. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionw/4147445-eng.htm
- [SC-Z63], [SC-Z66], [SC-Z198], [SC-Z209], [SC-Z223]: Section Z (Z173-174 also read). https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionz/4147446-eng.htm
- [STREP] Wikipedia, "Streptomycin". https://en.wikipedia.org/wiki/Streptomycin
- [SULF] Wikipedia, "Sulfonamide (medicine)". https://en.wikipedia.org/wiki/Sulfonamide_(medicine)
- [T147]: HSC series T147–194. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectiont/4147444-eng.htm
- [TCE-MW]: The Canadian Encyclopedia, "Minimum Wage". https://thecanadianencyclopedia.ca/en/article/minimum-wage
- [TFR-W]: Wikipedia, "List of countries by past fertility rate" (UN WPP / OWID estimates). https://en.wikipedia.org/wiki/List_of_countries_by_past_fertility_rate
- [THOM] Thomasson and Treber, "From Home to Hospital", NBER 2004. https://conference.nber.org/confer/2004/si2004/dae/thomasson.pdf
- [TOR-MILK] City of Toronto, "Made in Toronto: Milk". https://www.toronto.ca/explore-enjoy/history-art-culture/online-exhibits/web-exhibits-culture-people/made-in-toronto-milk/
- [TOR-W] Lost Rivers, "Toronto's Water Supply". https://lostrivers.ca/points/watersupply.htm
- [TPS03] B. Gladstone, "Praise & Admiration for Toronto Police (1903)", quoting H. J. Ford, *Pittsburgh Gazette*, July 1903 (secondary). https://www.billgladstone.ca/?p=10505
- [TYPH] Wikipedia, "Typhoid fever". https://en.wikipedia.org/wiki/Typhoid_fever
- [VCU] "Death Rates by Cause of Death, 1900–2001" (*Statistical Abstract of the US* 2003). https://commed.vcu.edu/IntroPH/Introduction/deathrates_us.html
- [WHR] Waterloo House of Refuge, "Old Age & the Poorhouse". https://waterloohouseofrefuge.ca/a-peoples-history-poverty-in-ontario/old-age/
- [WP-PH] Wikipedia, "History of public health in Canada". https://en.wikipedia.org/wiki/History_of_public_health_in_Canada
- [WPTB-W]: Wikipedia, "Wartime Prices and Trade Board". https://en.wikipedia.org/wiki/Wartime_Prices_and_Trade_Board
- [WQ] *Wilson Quarterly*, "The Birth of the Supermarket". https://www.wilsonquarterly.com/quarterly/_/birth-supermarket
- [ZON-W]: Wikipedia, "Zoning". https://en.wikipedia.org/wiki/Zoning

**Licensing of shipped data:** see §6.4. Wikipedia is cited for dates and plain facts only (CC BY-SA); nothing here needs a licence fee.
