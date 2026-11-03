# Gold and the city's money: a study, 2026-11-02

A read-only study of 0.9.2 (main at 5602c6d0), from Jerus's two asks of 2026-11-02 (both quoted in §1). It covers gold, and choosing the city's money at the start. The prototypes are in `gold-money/`:
- `GoldRuleProbe.java` (its output in `probe.txt`) plays the regime harness's own town, pegged at founding, nine ways through the public dials. It changes no game code and writes only scratch folders.
- `SignProbe.java` (`signs.txt`) asks the Java runtime for each nation's sign and its place.
- `StartRates.java` (`startrates.txt`) reads the money and rate a city founded at seven spots gets from today's world file.
- `pars.py` (`pars.txt`) derives the 1900 gold pars from the coinage laws.

His saves were read, never written: slot 3 (Rotterdam) and the autosave (Stockholm), through `tools.SaveDump` and their history files.

Every figure is one of four kinds: cited [key] with its licence in §8; derived, with the sum shown; measured, by a file here; or ★est., a named estimate with its reason.

## 1. Summary for Jerus

His words: "i think we need to add gold, idk why but i feel like that is something that would massively help fx control", and "on the starting menu, allow the player to choose currecny type and even starting level ... defualting to the nation ... with the yen sign, and also if you choose your own currency allow to for example name it whatever but also choose whether its $ or something else".

1. **Gold alone gives no control.** What decides whether a peg holds is how fast the vault drains, and a chance of doubt that reads only the gap between the peg and where the float would be. It never reads the vault. Measured on the regime harness's town pegged at founding:
   - With the founding vault (US$647k), the peg breaks in month 49 when the vault runs short.
   - With a vault 40 times deeper, it breaks in month 56 by doubt. At month 48 that vault still held 712 months of imports.
2. **Your own cities show two different troubles.**
   - **Rotterdam (slot 3)** shows the rate running away while the vault is full. Your peg of 1942 held 35 years with the D$ 30-35% stronger than the float would be, and broke with no decision of yours. Then in 2293 one purchase of D$137.6B of dollars (about 4.5 years of the city's output) moved the D$ 11,600-fold in a month, because the rule for an intervention has no ceiling. Seven bank rescues followed.
   - **A young town** shows reserves running out, as item 1 measures.
   - That intervention fix is its own small batch, **XI1**, and goes first.
3. **What gold adds**, in three batches:
   - Gold as a ninth reserve in the vault, beside the eight moneys (**GD2**).
   - A fifth regime, **Gold** (**GD3**). The D$ is fixed to an ounce, and the vault must hold gold to cover the money. The rate rises by itself as the gold leaves, and doubt fades as the cover grows. Leaving gold is your decision, and the US$ leaving it in 1933 and 1971 asks you what to do.
   - Gold mines placed by geology, whose gold the city's mint buys (**GD4**). It is the one way a city makes its own reserves.
4. **Gold's price:**
   - US$20.6718 an ounce from the Gold Standard Act of 1900 (derived: 480 grains ÷ 23.22).
   - US$35 from Proclamation 2072, 31 January 1934, to 15 August 1971.
   - Then the US Geological Survey's annual price (public domain) to 2025, then a rule. This is batch **GD1**.
   - Not the World Bank's monthly gold. It is CC BY 4.0 on its face, but until May 2025 it is the London fix, the LBMA's, which the licence study's rule leaves with its owner. Only its swing ships, as a dial: 4.75% a month (16.5% a year, measured 1973-2024).
5. **The gold standard's automatic tightening, measured, does not correct a young town's deficit.**
   - Over months 25-48 the town's current account was -US$753k in every one of the nine runs, whatever the rate did.
   - A rule raising the rate as the vault falls only draws capital in. At its strongest (up to 25 points from two years of imports) it holds the peg to month 71, not 49. That costs an average rate of 9-12% and 13% fewer people at month 120.
   - So the Gold regime offers the rule as an aim, not as a cure.
6. **The money at the start (FM1 to FM3)** defaults to the nation's. In Stockholm that is "the Stockholm krona", written "2.6M kr", at 3.731 kronor a US$ in 1900. In Tokyo it is "the Tokyo yen", "¥2.6M", at 2.006.
   - The signs and their places come from the Java runtime's own locale data (the Unicode CLDR's), so no table ships for today's moneys.
   - A money of your own takes any name, code and sign (up to 3 characters), placed before or after the number. Your Rotterdam's "fRotterdam Guilder" is the case it is for.
7. **The starting level** is one currency reform on day one. The city's unit is set to 1 over the level at founding, and every price is that many times bigger. A twin harness holds the city to the same city at 1:1.
8. **The cost per month is negligible:** one lookup for gold's price, a ninth vault entry, and a few multiplications for the regime. The mine is one more sector.
9. **Eight batches in two lanes** (§6): XI1, then GD1-GD4; and FM1-FM3. FM2 and FM3 come after EP1b, so a Berlin city of 1900 gets the mark, not the euro.
10. **Four calls** (§5): which trouble to fix first, how much gold, whose yen, and what "starting level" means.

## 2. What exists today, checked

| the brief said | on 0.9.2 |
|---|---|
| no gold anywhere | **True.** The only hits are an author's name and SplitMix's "golden gamma". `Currency.java:95` names ISO's XAU in a comment. |
| the vault holds only US$ (`Game.FOUNDING_RESERVE_USD`) | **No longer, since WN4 (0.8.81).** `ForeignAccounts` keeps a balance in each of `WorldMoneys.EIGHT` (USD, EUR, JPY, GBP, CNY, CAD, AUD, CHF), filled by a reserve mix (`Mix.ALL_USD` by default, `FA:1577`). The founding vault is `25_000 × PriceLevel.FACTOR` = US$647.1k (`Game:1163`). |
| the world holds one other money (`Currency.java`) | **Stale.** `WorldMoneys` steps every money of the 196 nations each month. `Currency`'s javadoc ("the world holds exactly one other money") predates WN4, and FM1 rewrites it. |
| "<City> dollar" or a typed name and code; the sign always "$" | **True.** The sign is saved (`DataSave` `currencySymbol`) but no screen reads it: the "$" is a literal in `ui/Money` (`tightMoney`, `cash`, `signed`, `unitPrice`). There are about 900 money-format calls in 23 `ui/` files, about 83 lines with a literal "$" in `ui/` and 140 in the model, and 40 tests of `"$0".equals(shown)`. |
| currency reforms (`Denomination`) | **They divide only.** `canLop` takes a factor of 2 or more, the chips are 10, 100 and 1,000, the player presses it, and it unlocks at a price index of 10. The unit is per city, and the load path takes any unit above 0 and re-seeds the money constants (`Game:19614`). |
| which regimes | **Four**, `ForeignAccounts.Regime` (`FA:1191`): Float, Lean against falls (the default), Band (edges held by the vault), and Peg to the US$ (the vault meets the month's whole balance). A peg breaks two ways. By the vault when it would fall under one month of imports (`VAULT_FLOOR` 1). Or by doubt, at a chance a month of 0.001 + 0.03 × (gap - 0.10)/0.10 on the weak side (`BREAK_*`, `FA:1227-1239`), and 0.001 on the strong. The dial under a peg is the US's rate plus a spread (`DM.tiedRate`). XR5 (queued) makes a peg pay the pressure rather than the balance. |
| starts 1 January 1900; rates from about 1950 | **The calendar starts in 1900** (`CityCalendar.EPOCH_YEAR`). **The world file's rates start in January 2000** (`WorldData.FILE_FIRST_YEAR`), and the stand-in century holds January 2000's rate through 1900-1999. Measured (`startrates.txt`, on `data/world-1.bin`): at month 0 Tokyo has the yen at 105.10, Stockholm the krona at 8.482, and Berlin the **euro** at 0.9867. EP1b (queued) rebuilds the file from 1900 with the gold pars of EP0 (`1900-start/moneys-eight-regimes.csv`). |
| deposits by geology class | **True.** `Resource` has 7 kinds (IRON to FOREST), and their order is load-bearing (`CityLand` row widths). There are 7 `Geology.Kind` classes from the USGS's World Energy provinces, with a weight a class for each kind (`W_COPPER` = {1, 1, 4, 1, 0, .3, 1}: ocean, shield, fold belt, rift, shelf, basin, other). |
| (not in the brief) | `research-1900.md` §7.2 proposed "Money's gold years", the D$ held at par by dated states. GD3 replaces it with a regime the player chooses. |

## 3. A peg in the game, measured

### 3.1 His cities

**Rotterdam** (slot 3, 0.8.95, Historical, month 7,871 = 2555, 2.9M people; the history file's 6,166 months):
- The D$ was under 0.8 of its parity in 40% of the months, and over 1.25 in 1.3%. The vault grew without a break, to US$6.5e15, or 6,692 months of imports at month 7,800. It is a lasting surplus.
- The decision log:
  - month 323: bought reserves, D$24.8M.
  - month 514 (1942): aimed at the currency, pegged, and bought US$ for D$28.3M.
  - month 624: devalued the peg to 0.7063.
  - month 4,705: reformed, 1,000 old for one new.
  - month 4,716: "Bought US dollars for $137.6B".
  - months 4,720-4,727: seven automatic bank rescues.
  - month 4,782: borrowed US$100Q abroad.
- The peg held from month 528 to about 950. The float would have stood 30-35% stronger (the shadow at 0.49 against 0.706). The vault bought dollars, to 29 months of imports, and the peg broke with no decision logged: the strong side's base chance of 0.1% a month, about 34% over 420 months (derived: 1 - 0.999^420).
- At month 4,716 the city's output was D$2.54B a month, so D$137.6B was 4.5 years of it (derived: 137.6 ÷ (2.54 × 12)). `interventionFelt` adds `INTERVENTION_IMPACT` (2.0) × 4.5 = 9.0 in logs (×8,100). The D$ went from 0.0005 to 5.82 per US$ in month 4,717 (×11,600), with a surplus, no foreign debt and the vault at 390 months of imports. Prices rose ×3.8 in a year (the index from 4,246 to 16,107) and the dial went to 100%.
- **The rule is the IMF's 1.7-2.0% per 1% of GDP (R33 in spec-exchange-rates), carried far past the sizes it was measured on.** XI1 bounds it (§4.5).

**Stockholm** (the autosave, 0.9.1, month 61, 1905): the D$ is at 0.868 per US$, 13% stronger than at founding. Its money is "Stockholm dollar", S$.

### 3.2 The probe

`GoldRuleProbe` takes `CurrencyRegimeCheck.town()` (its section 11's town: the default founding, 500 houses, the four depots), pegs it at founding at 1.00, and plays 240 months. In the rule runs the autopilot is off, and each month's spread is set to K × clamp((T - m)/(T - 1), 0, 1), where m is the vault in months of imports. This is the classical bank raising its rate as the gold leaves.

| run | the peg | vault, months of imports at 12 / 24 / 48 / 120 | mean dial pegged | price index 120 | people 120 | months 25-48: current / financial account |
|---|---|---|---|---|---|---|
| today's peg | breaks month 49, vault short | 64 / 55 / 2.6 / 5.2 | 2.00% | 3.45 | 1,960 | -US$753k / -620k |
| vault ×40 (US$25.9M, the Wealthy preset's) | breaks month 56, **by doubt** | 1,253 / 1,007 / 712 / 1,430 | 2.00% | 3.46 | 1,965 | -753k / -620k |
| K 3 points, T 6 months | month 49 | 64 / 55 / 2.7 / 4.8 | 2.06% | 3.28 | 1,969 | -753k / -615k |
| K 10, T 6 | month 49 | 64 / 55 / 3.0 / 5.9 | 2.19% | 2.82 | 1,955 | -753k / -604k |
| K 10, T 24 | month 51 | 64 / 55 / 5.7 / 4.2 | 3.08% | 2.57 | 1,914 | -754k / -511k |
| K 25, T 24 | month 71 | 64 / 55 / 9.4 / 8.5 | 9.35% | 1.90 | 1,697 | -753k / -378k |
| K 10, T 60 | month 55 | 64 / 55 / 12.4 / 5.4 | 5.04% | 2.65 | 1,900 | -756k / -274k |
| K 25, T 60 | month 71 | 64 / 56 / 24.4 / 8.3 | 11.51% | 1.88 | 1,694 | -763k / +155k |

(Measured, `probe.txt`. CurrencyRegimeCheck 11 on the same tree prints the same break: month 49, the vault short, the D$ +15.1% in the month, 3.13 by month 240.)

### 3.3 What follows

- **The young town's deficit is its building.** Its current account does not answer the rate at all over two years (-US$753k to -763k in all nine). So "a trade deficit drains gold and tightens money by itself" tightens prices (3.45 down to 1.88 at month 120), not the deficit. Only capital answers. At 11.5% the financial account turns from -US$620k to +US$155k, which is the 1900 settler economies' way (Canada and Australia on gold, borrowing abroad).
- **A deep vault does not make a peg credible here,** because doubt never reads it. A real bank with every unit of its money covered cannot be pushed off. `WorldMoneys` already says so for the world's boards: `CURRENCY_BOARDS` never break, as "a board breaks only if its vault falls under its money". The city has no such rule. GD3 gives it one, through the cover (§4.3).
- **The price-specie flow's money half is already there under a peg.** The D$ a deficit pays abroad leave the city's pools when the imports are paid, and the vault that buys them back retires them: the vault and the central bank's equity fall, and M0 is unmoved (`CB:611-647`). On the strong side the bank pays for dollars in new money (`CB.issue`, `FA:1246`'s default payer). So the Gold regime needs no new money machinery, only a cover to read and a price to hold.
- **Neither of his troubles is cured by gold as a metal.** The rate running away is XI1's fix, plus the backing that GD3 lets doubt read. Reserves running out is cured by a deeper founding vault, or by making reserves: GD4's mines, and the mint buying their gold.

## 4. The design, on the code

### 4.1 Gold's price (GD1: `GoldPrice`, pure, never saved)

`GoldPrice.usdPerOunce(path, seed, month)` gives US$ for a fine troy ounce:

| months | price | from |
|---|---|---|
| Jan 1900 to Mar 1933 | 20.6718 | the Gold Standard Act, 14 Mar 1900: the dollar is 25.8 grains nine-tenths fine, 23.22 fine; 480 ÷ 23.22 [GSA1900]. The Coinage Act of 1837 had set the same weight, so January and February 1900 are the same par. |
| Apr 1933 to Jan 1934 | 20.6718 × (35/20.6718)^(k/10), k = 1..10 | ★est.: the dollar floated from April 1933, and from October the RFC bought gold at prices raised by steps. A geometric path from the old par to the new stands in. Its 1933 average is US$25.53 (derived: 3 months at 20.6718 and nine on the path, over 12), against USGS's recorded US$25.56 [USGS-DS140] |
| Feb 1934 to Jul 1971 | 35.00 | Proclamation 2072, 31 Jan 1934, under the Gold Reserve Act [GRA1934]; USGS records $34.95 for 1934 and $35.00 for 1935-70 |
| Aug 1971 to Dec 2025 | the year's average, months log-linear between July of each year (★est.; Aug 1971 from $35 at July) | USGS Data Series 140's unit value, 1971-2022 [USGS-DS140], then Mineral Commodity Summaries 2026's 2023 $1,945, 2024 $2,388 and 2025e $3,300 [USGS-MCS2026]: public domain, 55 numbers in a resource table `gold-usd-annual.csv` |
| from Jan 2026, and on Random from Aug 1971 | the rule below | |

- **The rule.** ln of gold's real price (over the US price level of the path, `Nations`' US line) is an AR(1) toward the mean real price of the data's last 20 years.
  - The innovation's σ is **4.749% a month**, measured from the World Bank's monthly gold, 1973-2024, 624 changes [WB-PINK]. It ships as a dial, not as the series.
  - The drift is zero (★est.). The measured real drift depends on its start: +3.30% a year from 1973 but +0.77% from 1980, since 1980-2000 fell from $675 to $283.
  - The half-life is ★est. 10 years: gold's real price has wandered far for decades, and a pull is needed only so a 2400 city has a finite price.
  - Every draw is `new Random(Nations.scramble(seed, GOLD, t, ...))`, as `WorldMoneys`' are, so one seed is one path and a reload continues it.
- **The swing is not in the official years.** The Pink Sheet starts in January 1960 at $35, its free-market price drifting to about $43 by August 1971. The game keeps the official $35 to August 1971, the price a central bank dealt at.
- **On Random,** the official path to 1971 is the same as Historical's. The US's gold laws are the anchor's, not a nation's draw.
- **Gold is far cheaper in the game's world than today's.** The game's month 0 prices today's real economy at 1900's level (`PriceLevel.FACTOR` = 1/38.6). Gold at the statute's $20.67 is 1900's real price, worth US$798 of 2025 (derived: 20.6718 × 38.6). 2025's $3,300 is 4.14 times that (derived), or $85.4 at 1900's level. This is history, not a fault: gold was cheap in 1900 and its price was a law. It matters for the mines (§4.4), not the vault.

### 4.2 Gold in the vault (GD2)

- **A ninth reserve.** `ForeignAccounts.MONEYS` goes from 8 to 9, with XAU at the end, ISO 4217's own code for a troy ounce of gold. `WorldMoneys.EIGHT` stays eight, so the debt moneys, `PolicyRates`, `YearBook` and the world's moneys are untouched (agent map: about 20 places size by `MONEYS`).
  - `usdPer[8]` is the month's `GoldPrice`, handed where the cross rates are (`Game.handTheMoneysToTheVault`, month-order 4).
  - It earns nothing, because `PolicyRates` gives NaN outside 0-7 and NaN earns zero (`FA.earnVaultInterest`).
  - It is revalued as the others are (`setMoneyRates`' `crossUsd`).
- **The mix.** Gold enters only under `Mix.CHOSEN` and the Gold regime. The COFER mixes stay moneys, as COFER counts no gold.
  - Trade › The reserves gets a gold chip: buy and sell gold at the month's price with the treasury's money, as dollars are bought today.
  - The vault table gets a gold row in ounces and US$.
- **Which money pays a deficit.** `vaultRemove` takes the largest holding first. Under the Gold regime it takes gold first (the standard ships gold); otherwise as today.
- **The save.** `foreignAccounts` gains slots 147 onward (balance, usdPer, chosen share), read only when the array is that long. No format bump: an older save loads with no gold.
- **What it buys the player.** Before 1971, gold is the US$ at a fixed price with no interest: a cost, plus a hedge against 1934's +69% (derived in `pars.txt`) and against 1971. After 1971 it is a haven with its own swing.

### 4.3 The gold standard (GD3: `Regime.GOLD`, at the end of the enum, whose ordinal is saved)

- **The mint price** `M`, in D$ an ounce, is saved. Entering Gold sets `M` = the day's rate × the month's `GoldPrice`, so the D$ keeps its value. The player may type a par instead (Britain's 1925 return "at the old par"). In 1900 at 1.00 D$ per US$, `M` is D$20.6718: the D$ is the US$'s 1.504631 g.
- **The rate** is `M / GoldPrice`. It is held inside the gold points, ±★est. 0.5% (the cost of shipping gold between New York and London, about half a percent [OFFICER]), as a Band whose centre moves with gold:
  - Until 1933 that is 1.00, today's Peg at par.
  - In February 1934 it becomes 0.5906, the D$ 69% dearer in dollars, if the city keeps its gold.
  - After 1971 it swings with gold.
  - The edges are paid as a Band's are (`FA:1043-1057`): the weak edge sells the vault's gold first, and the strong edge buys gold, paid in new money as the strong edge is today (`CB.issue`). This is the free coinage of a mint.
- **The cover.** Cover = the vault's gold at `M` ÷ `CB.m0()`. The legal floor is ★ `COVER_RATIO` 0.40, the Federal Reserve Act's 40% against notes [FRA1913]. Free gold is gold × M - 0.40 × M0.
- **The rules of the game.** The tied dial (`DM.tiedRate`) gains a spread that rises as the cover falls, from twice the floor to the floor: spread = `GOLD_DEFENCE` × clamp((0.80 - cover)/(0.80 - 0.40), 0, 1).
  - `GOLD_DEFENCE` is ★est. 0.10, inside the probe's range (its K 10 and K 25 runs).
  - It is the autopilot's fourth aim, **Defend the gold**, the default under Gold. The player may aim at inflation instead and watch the gold go.
- **The break.**
  - By the cover: in a month whose drain would take the cover under the floor, convertibility is suspended. The regime falls to Float at the shadow plus a quarter of the gap (`OVERSHOOT`), with credibility's dent, as a peg's break by the vault does. It replaces the one-month-of-imports floor under Gold.
  - **By doubt:** the peg's chance is multiplied by clamp((1 - cover)/(1 - 0.40), 0, 1), so it is today's chance at the legal cover and none at a full cover. This is the boards' rule (`CURRENCY_BOARDS`) given to the city.
  - Measured: the vault-×40 town broke by doubt in month 56, its vault at 712 months of imports in month 48. Under this rule it would break by doubt only as far as its cover is under one, which the batch measures.
- **Leaving and returning.**
  - Choosing any other regime leaves gold. The gold stays in the vault, valued at `GoldPrice`.
  - Choosing Gold again asks the par.
  - When the US$ leaves gold (April 1933), is revalued (February 1934) or closes its window (August 1971), a NEEDS YOU notice offers three choices: stay on gold at `M` (the D$ dearer), move `M` with the dollar (devalue), or leave gold.
- **Per month:** a division, a clamp and a comparison, in `repriceCurrency`'s own step (month-order 137), and the dial's spread at month-order 51-52. Nothing new is stored but `M` and the regime.
- **The trace:** byte-identical. Lean stays the default (call 2); a city enters Gold only by choice, or by the founding screen's "start on gold" box beside the money.

### 4.4 Gold mines (GD4: SAVE_FORMAT and its floor raised)

- **The kind.** `Resource.GOLD`, the eighth, after FOREST. It widens `CityLand`'s rows, so an old save would load wrongly: the format and `OLDEST_LOADABLE_FORMAT` rise together (CLAUDE.md).
  - Its fields are placed by geology class. `W_GOLD` = {0, 3, 3, 0.5, 0, 0.5, 1} ★est., for ocean, shield, fold belt, rift, shelf, basin and other.
  - The shield and the fold belt get 3. Orogenic gold (USGS model 36a) sits in "greenstone belts; oceanic metasediments" of the old shields and in accreted fold belts. Porphyry and epithermal gold (20c, 25c, 25e) sits in the volcanic arcs [USGS-B1693].
  - The basin gets 0.5 for the placers (39a, Cenozoic gravels below their sources) and the Witwatersrand's Archean conglomerates (29a). The shelf and the ocean get nothing.
  - `Z_GOLD` follows from the weights, as `EarthTerrainCheck` 13 computes Z.
- **How much gold.** The game's copper is 378.6 t of metal a km² at weight 1 (derived: 0.0005 fields × 2.524 sites × 300,000 t). The world mined 3,280 t of gold in 2024 [USGS-MCS2026] against about 23 Mt of copper (★est., Mineral Commodity Summaries 2025's copper, to confirm in GD4): 1.43 × 10⁻⁴.
  - So gold's amount a km² is ★est. copper's × 1.43 × 10⁻⁴ = 54 kg (derived), in more and smaller fields: ★est. a field per 500 km² and 25 t a site.
  - The reserves' ratio would give half that (66,000 t of gold [USGS-MCS2026] against copper's reserves). The flow is the fairer measure, as reserves follow the price.
  - GD4's research step confirms both.
- **The building.** Gold Mine & Mill, id 148, the next free. It sells doré on site, on the precedent of copper's oxide leach plant, which makes finished metal at the mine with a consumable (`uses(Good.ACID)`; for gold, cyanide as `CHEMICALS`, ★est.).
  - The good is `Good.GOLD`: troy ounce, stockable, its world band from `GoldPrice` each month.
  - Freight and the wedge are ★est. 0.25% each side, as bullion's value per tonne makes freight nothing.
  - The sector is `Gold Mining`, the 25th of `Sectors.KEYS`, at the end; `sectors/GoldMining` on `ExtractionSector`, about 15 code lines over `Mining`'s shape.
- **The grade decides whether a mine pays in 1900.**
  - Today's open pits work 1-4 g/t and underground mines 8-10 [CALVO2016]. Australia's producers reported costs of about A$2,500 an ounce in 2025 (company reports, a fact; no open series), about US$1,650. At 1900's level that is $42.7 an ounce (derived: × 0.025883), so today's mine loses money at the statute's $20.67.
  - At ★est. 10 g/t in an unworked field, it pays. The cost a tonne of ore is held, so the cost an ounce falls ×6.7 from 1.5 g/t, to $6.41 (derived). The Witwatersrand's recovered grade was 13.2 g/t and fell to 4.6 by 2006 (a fact).
  - A field's grade is drawn from its geology, richer in the shield, and falls as it is worked, as `vintages` age oil.
  - The 1920s squeeze (costs up, gold fixed) and 1934's boom (gold +69%) then come out of the rule, not a script.
- **Who buys.** Abroad, at `GoldPrice` × rate. Under Gold the mint takes all that is offered at `M`, which is the same price at the par. That gold goes to the vault, and new money pays for it, which is reserves made at home.
- **Per month:** one more sector's month, about `Mining`'s.
- **The trace moves.** Generated worlds get gold fields at W = 1, so the ensemble runs.

### 4.5 The intervention bounded (XI1, a fix)

- `ForeignAccounts.repriceCurrency` step 5 adds `INTERVENTION_IMPACT × dealt / annual GDP` to `interventionFelt`, linear without end.
- Make it linear up to ★est. `INTERVENTION_LINEAR` = 10% of a year's GDP, and logarithmic past it: impact = I × s₀ × (1 + ln(s/s₀)).
  - Every purchase and every Lean defence under 10% of a year's GDP moves the rate exactly as today, so the trace is byte-identical where no one buys that much.
  - Rotterdam's 4.5 years would move the rate 2.0 × 0.10 × (1 + ln 45) = 0.96 in logs (×2.6), not ×8,100 (derived).
- Trade › The reserves warns past 10%: "this purchase is N years of the city's output".
- Its harness repeats Rotterdam's case on a scratch town.

### 4.6 The sign and its place (FM1)

- **`Currency` gains `signAfter`** (saved as `currencySignAfter`; a missing key reads false), and the sign becomes the player's.
  - `Currency.write(String number)` gives "$2.6M", "¥2.6M", "2.6M kr" or "CHF 2.6M". A sign of letters takes a space; a symbol does not, before or after (★est., CLDR's patterns' habit, in `signs.txt`).
  - The number keeps the game's own "2.6M"; only the sign and its place are the nation's. CLDR's patterns use no-break spaces (U+00A0, U+202F), and `write()` uses the game's own.
- **One writer.** `ui/Money`'s ten forms and the model's `Formats` call `write()`. The roughly 220 lines with a literal "$" route through them, and the 40 `"$0".equals(shown)` tests go through `Money.isZero(shown)`.
  - The prefix-only code becomes sign-aware: `Money.marked`, `Game:11609`'s `face.substring(1)`, and the amount-parsing regex at `FinancesScreen:5089`.
  - It is mechanical work for Sonnet, slice by screen.
- **The qualified sign**, where a foreign figure is in sight, is the city's initial and the sign (A$, T¥). It is used only when the sign is also one of the vault's moneys' marks (`FA.MONEY_MARK`: US$, €, ¥, £, CN¥, C$, A$, CHF) or is the same as a partner's on the screen. Otherwise the sign alone already says whose money it is: the Stockholm krona's "kr" needs no letter. The rule of 0.7.10 holds for "$": "$" alone, and "S$" beside US$.
- **The founding screen** gains a sign field (1-3 characters, ★est.; a row of common signs to click: $ € £ ¥ ₹ ₩ ₽ ₺ ₪ ƒ kr zł Kč Fr R$) and a before/after switch, under "name it yourself".
- **Fonts.** The interface's font must draw the sign. The batch checks every sign the founding screen offers against the font (`Font`/`Text` bounds), and offers only those.
- **The trace:** byte-identical, as "$" before is the default.

### 4.7 The money from the nation (FM2, after EP1b)

- The founding spot's nation (`WorldData.ownerAt`, as `WorldMarket.setSite` finds the host) gives its money in the start year (`Nations.year(nation, 1900).money()`).
  - The money is **the city's own, styled after the nation's**: "the Tokyo yen, ¥, TOK", "the Stockholm krona, kr, STO". The noun and sign are the nation's, and the code comes from the city as today, never the nation's, which the vault and the world already hold. This is call 3.
  - Founding saves the code it was styled after, for the founding screen's line and Trade's ("styled after the Japanese yen").
- **The sign and place** of an ISO money come from `java.util.Currency` and `NumberFormat` at the nation's main locale, the Unicode CLDR's data inside the Java 21 runtime [CLDR].
  - A full-width form is folded (NFKC), so "￥" becomes "¥". A sign in a script the font may not draw (Egypt's "ج.م.‏") falls back to the English one ("EGP").
  - Measured, `signs.txt`: of 522 country locales, 303 put the sign before and 219 after. Of 36 main ones, the sign follows the number in Berlin, Paris, Rome, Madrid, Stockholm, Copenhagen, Warsaw, Prague, Budapest, Moscow, Kyiv and Hanoi. It precedes in Tokyo, London, New York, Amsterdam, Dublin, Oslo and Zurich.
  - A nation's main locale (★est. in the probe) is CLDR's territory data's most-written official language in the batch.
- **The pre-ISO moneys** EP1b brings (the gold mark, the franc, the krone of 1892, the ruble of 1897) take a hand table of facts: noun, sign, place. About 20 rows.
- **The default follows the spot** until the player picks a money or types one: "if you haven't named your city yet". Typing a city name changes the money's prefix ("Edo yen"), not its noun. A generated world keeps "<City> dollar".
- **Why after EP1b:** before it, the world file's month 0 is January 2000's. A Berlin city of 1900 would get the euro at 0.9867, and Tokyo the yen at 105.1. After it they get the mark at 4.198 and the yen at 2.006 (`pars.txt`).

### 4.8 The level at the start (FM3, after DZ1 and FM2)

- **The level L** is how many of the city's money one US$ buys on founding day:
  - the nation's money's rate at month 0 (`WorldMoneys.rateAtStart(code)`; the default when the money is the nation's);
  - 1.00 for a "<City> dollar" (as today, and as in 1900);
  - today's (December 2025's: the yen 155.86, the krona 9.306);
  - or typed, from 0.01 to 100,000 (★est. bounds: a city at 10⁵ units a dollar is Indonesia's rupiah ×6, and `Denomination.MAX_UNIT` is 10¹²).
- **How: one reform on day one, in the multiplying direction.**
  - Founding sets the denomination unit to 1/L, re-seeds the money constants (`Game.seedMoneyConstants(unit)`, the load path's own), and multiplies the founding cash, the rate and the parity base by L (`parityBase = OPENING_RATE / unit` already reads the unit, `FA:199`).
  - Later reforms work as today from that unit; `lop` multiplies it.
  - Nothing real moves. The harness is `DenominationCheck`'s twin: a city founded at L = 150 equals the plain city ×150 in every money series, to 1e-9.
  - DZ1 (queued) must land first: it is the absolute tolerance that does not scale with the unit, and L = 150 is that bug ×150.
- **The vault** is US$ and does not scale. World goods are US$ times the rate and scale on their own.

### 4.9 Compute

Every addition is O(1) a month:
- GD1: a table read or one AR(1) step.
- GD2: one more pass in loops of eight.
- GD3: about six arithmetic operations.
- FM1 to FM3: none a month. `write()` is a string join, which today's format already is.
- GD4: one sector's month, and at world-build one more kind in the fields pass, measured in the batch against `EarthTerrain.cellsPass`.

## 5. The calls

1. **Which trouble should gold fix first?**
   - Measured: a young town's peg dies in month 49 of a thin vault. A deep one dies in month 56 of doubt, with 712 months of imports in its vault eight months before. Your Rotterdam's trouble was a rate that ran away with a full vault (×11,600 in one month after one purchase).
   - Options: the rate running away (XI1, then GD3's cover-read doubt); the reserves (GD3's dial and GD4's mines); both.
   - **Recommended: the rate first** (XI1 is small and from your own city), and GD1-GD4 in order after it.
2. **How much gold?**
   - Options: all four batches (price, vault, regime, mines); the first three, mines later; only gold in the vault.
   - Lean stays the default regime either way; Gold is a choice, at founding or on Policy.
   - **Recommended: all four**, mines last (it is the largest: a format bump, a sector, a deposit kind).
3. **Whose yen?**
   - Options: the city's own money styled after the nation's (its own rate and policy, written ¥, code TOK); or the nation's money itself (the city on Japan's yen at 1:1, its interest the Bank of Japan's, no regime of its own).
   - **Recommended: its own, styled after the nation's.** The game's whole currency screen is the city's own policy.
4. **"Starting level" is how many of your money one US$ buys at the start?**
   - Options: yes (the nation's 1900 rate by default, today's, or typed); or something else (the price level, the treasury's size).
   - **Recommended: yes, with the start year's rate the default.**

Adopted as recommended unless he says otherwise (none blocks a batch):
- the gold points ±0.5%;
- the 40% cover;
- Defend the gold as the Gold default aim;
- the NEEDS YOU notices of 1933, 1934 and 1971;
- gold only in the CHOSEN mix;
- the qualified sign rule;
- the typed level's bounds;
- `W_GOLD`.

## 6. The batches

Names checked unused against `QUEUE.md`, `docs/`, `src/` and `tools/` (GD, FM and XI have no prior batch).

| batch | size | what | touches | saved / checks | the trace |
|---|---|---|---|---|---|
| **XI1** | small (Sonnet) | an intervention's impact linear to 10% of a year's GDP, logarithmic past it; the warning | `ForeignAccounts` step 5, `TradeScreen` reserves | none / InterventionCheck (new; Rotterdam's case); ExchangeRateCheck, CurrencyCheck | byte-identical (no default purchase reaches 10%) |
| **GD1** | small | `GoldPrice`: statute, USGS's annual table, the AR(1) past it; the credit line | new class, `gold-usd-annual.csv`, the credits (LC1) | none / GoldPriceCheck: the pars to the digit, the table's months, the rule's σ and pull, a reload continues the path | byte-identical (nothing reads it) |
| **GD2** | medium | XAU the vault's ninth: price, no interest, revalued; the gold chip; the vault's row | `FA` (MONEYS 9, slots 147+), `Game.handTheMoneysToTheVault`, `TradeScreen`, `VaultTable`, `VaultView` | `foreignAccounts` 147+ / VaultMoneysCheck (gold), SaveFileCheck, ReadPathCheck | byte-identical (ALL_USD default) |
| **GD3** | medium-large | `Regime.GOLD`: M, the gold points, gold paid first, the cover, Defend the gold, the cover's break and doubt, leaving and returning, the US$'s three dates, Policy's card | `FA` (regime, reprice), `DM` (aim, tiedRate), `CB` (m0 read), `Expectations`, `PolicyScreen`, notices | `foreignAccounts` (M, cover state) / GoldStandardCheck (new): a town on gold holds through 1933 and rises 69.3% in Feb 1934; a drain to the floor suspends; full cover never breaks by doubt; Defend the gold raises the dial as cover falls; save mid-gold | byte-identical (Lean default) |
| **GD4** | large | `Resource.GOLD`, `W_GOLD`/`Z_GOLD`, `Good.GOLD`, id 148, `Gold Mining` (25th key), grades, the mint's purchase | `Resource`, `Geology`, `World`, `Good`, `buildings.json`, `Sectors`, `sectors/GoldMining`, `BuildingManager` mirror, the map's switches | SAVE_FORMAT and floor / GoldMiningCheck (CopperMiningCheck's seven sections), EarthTerrainCheck 13, BuildingDataCheck | moves: ensemble |
| **FM1** | medium (Sonnet, by screen) | `Currency` sign and place, `write()`, every "$" through it, `isZero`, the founding screen's sign field, the font check | `Currency`, `ui/Money`, `Formats`, about 260 lines (the 223 literals and the 40 tests) in about 30 files, `FoundingScreen`, `DataSave` | `currencySignAfter` / CurrencyCheck (sign), NewGameCheck, a words probe over every screen with "kr" after | byte-identical |
| **FM2** | medium, after EP1b | the money from the spot's nation: noun, sign and place from CLDR via the JDK; the pre-ISO table; the default follows the spot | `FoundingScreen`, `Founding` (styledAfter), `Currency`, `WorldData.ownerAt` | `currencyStyledAfter` / NewGameCheck (Tokyo gives ¥ before, Stockholm kr after, a generated world "<City> dollar") | byte-identical (the default playtest's world is generated) |
| **FM3** | medium, after DZ1 and FM2 | the level: the nation's, today's or typed; the unit at 1/L on day one | `Founding` (level), `Game` founding, `Denomination` (a unit below 1 at founding), `FoundingScreen` | `foundingLevel` / LevelCheck (new): L = 150 and L = 2.006 twins equal the plain ×L to 1e-9; a reform later from 1/L | byte-identical at L = 1 |

**Order.**
- XI1 first.
- The gold lane is GD1, GD2, GD3, GD4.
- The money lane is FM1, then FM2 after EP1b, then FM3 after DZ1.
- GD3 waits for XR5, which changes what a peg and a band pay.
- The lanes touch only at the founding screen: GD3's "start on gold" box goes beside the money line, after FM1 if FM1 is in.
- The traces move only at GD4.

## 7. Where this touches the other studies

- **The wealth study** (where the wealth piles up): Rotterdam's D$ sat stronger than parity in 40% of its months, its vault rising without a break. Under Gold a surplus brings gold in and new money with it (the strong edge's issue), so prices rise and the surplus corrects, unless the bank holds the money back. That is the 1920s' sterilisation, which no regime here does. If that study finds the float fails to correct a surplus, Gold is one regime that would, and the measuring is theirs. A note is in the shared folder.
- **The wages study:** under Gold a deficit city adjusts by falling prices and wages. How far the game's wages fall decides what the gold standard costs in output (the probe's town lost 13% of its people at K 25). Theirs.
- **The households and the credit and housing studies:** gold as a household's store of wealth (coins, and the 1933 surrender) is theirs if they want it. This study keeps gold to the vault and the mines.
- **EP1b:** its world moneys on the gold pars must use the same statute figures as `GoldPrice` (they do: EP0's CSV and `pars.txt` agree to the fifth figure). FM2 waits for it.
- **XR5** (queued) changes what a peg pays. GD3's edges are a Band's, which XR5 also touches: GD3 after XR5.

## 8. Sources

Facts in law are nobody's (`licences.md`); a source the game ships must allow commercial use.

- [GSA1900] Gold Standard Act, 14 March 1900, 31 Stat. 45: the dollar of 25.8 grains of gold nine-tenths fine. US federal law, public domain.
- [GRA1934] Gold Reserve Act, 30 January 1934, 48 Stat. 337, and Presidential Proclamation 2072, 31 January 1934, 48 Stat. 1730: the dollar of 15 5/21 grains nine-tenths fine, $35 an ounce. Public domain.
- [PL92-268] Par Value Modification Act, 31 March 1972, Pub. L. 92-268, 86 Stat. 116: $38 an ounce. [PL93-110] 21 September 1973, Pub. L. 93-110, 87 Stat. 352: $42.2222. Public domain.
- [FRA1913] Federal Reserve Act, 23 December 1913, §16: a reserve in gold of not less than 40% against Federal Reserve notes, 35% in gold or lawful money against deposits (read on FRASER). Public domain. The Bank Charter Act 1844 (a £14M fiduciary issue, every note above it in coin or bullion) and the Reichsbank's law of 1875, §17 (a third in money or gold) are the other two shapes.
- [USGS-DS140] US Geological Survey, Data Series 140, gold, historical statistics 1900-2022 (ds140-gold-2022.xlsx), "Sources/Usage: Public Domain": unit value from the average world market price for 1900-67 and Engelhard's quotation from 1968. Its 1900-14 values ($18.94) are 11/12 of the par, an artifact, so the statute is used to 1933. https://www.usgs.gov/media/files/gold-historical-statistics-data-series-140
- [USGS-MCS2026] US Geological Survey, Mineral Commodity Summaries 2026, gold: 2023-2025e prices, 2024 world mine production 3,280 t, reserves 66,000 t. Public domain. https://pubs.usgs.gov/periodicals/mcs2026/mcs2026-gold.pdf
- [WB-PINK] The World Bank, Commodity Price Data (the Pink Sheet), monthly, gold, 1960M01 ($35) to 2026M09 ($4,319): the catalogue says CC BY 4.0 (0038238; `licences.md` row 57). But its gold note reads "London afternoon fixing" to May 2025 and "spot average of daily rates" after, so the series is the LBMA's and then a data vendor's. Under `licences.md`'s rule for a republished series, it does not ship. Only the σ measured from it ships, as a dial. **For the licence study:** the Pink Sheet's other series may carry originators too, which is worth a look at the world file's 16.
- [USGS-B1693] Cox and Singer, eds., Mineral Deposit Models, USGS Bulletin 1693 (1986): models 36a (low-sulfide gold-quartz veins, greenstone belts), 20c (porphyry Cu-Au, island arcs), 25c and 25e (epithermal), 39a (placer gold), 29a (quartz-pebble conglomerate gold). Public domain. https://pubs.usgs.gov/bul/b1693/
- [CALVO2016] Calvo, Mudd et al., "Decreasing Ore Grades in Global Metallic Mining", Resources 5(4):36 (2016): CC BY 4.0. Gold mines' grades by method and nation.
- [CLDR] Unicode CLDR, as shipped inside the Java 21 runtime (JEP 252), read through `java.util.Currency` and `NumberFormat`: the Unicode License v3, which allows commercial use. Nothing is copied into the game's files.
- [OFFICER] L. H. Officer, Between the Dollar-Sterling Gold Points (Cambridge, 1996): the gold points about ±0.5%. Cited for a fact; nothing shipped.
- The coinage laws behind `pars.txt`: Japan's Coinage Act of 1897 (Law 16), the Scandinavian Monetary Union's convention of 1873, the German Coinage Act of 1873, the Latin Monetary Union's convention of 1865, Russia's reform of 1897, Austria-Hungary's of 1892, the Dutch law of 1875, and India's fixing of 1899. Facts in law. Every rate agrees with the US Mint's "Values of foreign coins" of 1898 and 1904 (public domain), and EP0's `moneys-eight-regimes.csv` cites the same pars.
- Not on gold on 1 January 1900: on silver, China's tael, Mexico (to 1905, then 0.75 g), Persia, Bolivia, Colombia and Peru; on inconvertible paper, Spain, Italy, Portugal, Brazil, Chile and Greece. Argentina's paper peso was convertible at 0.44 gold from 1899 (2.3557 per US$). EP1b owns these as the world's moneys; FM2 reads them.
