# Zoning: a design study, and what it does to forestry

*2026-10-08. A read-only design study. Samuel's answer to the build plan's question B (plantation land) was zoning: "in policy zoning will now exist, and basically the default is free, anything everywhere, but with zoning, you can allocate a % of total land or a specific km² for a specific industry, and even provide the land at a lower rate, or free". In the zone he wants the industry's tax, sales tax and property tax shown and editable, "so that some industries can build even tho land is expensive and property tax would kill them", and to "reserve specific space for some industries". Forest is "a resource on the map same as ores and oil, just a lot more common and together", with a global law "of not building on the forest, or always keep a certain % of forest available". He asked: would this fix forestry?*

*No game code was read or changed. The model facts come from the project's notes: `land.md`, `land-two-prices.md`, `property-tax-and-solvency.md`, `farms.md`, `policy-tab-and-vat.md`, `policy-at-a-glance.md`, `spec-land.md` and `materials-chains-research.md`. Anything that needs checking in code is marked "(to check)". The forest figures come from a small model of the game's regrowth rule, `zoning-forest-model.py`, beside this note. The mockups are the artifact "Zoning Study" (three boards). Figures are illustrative: the materials mockups' city, June 2183, 2.61M people.*

---

## 0. In one page

**Zoning is a fifth Policy area,** beside Taxes, Wages, Money and Promises. With no zones the city works as today: any business builds on any free ground (the default, as Samuel set it). A zone does three things for one industry:

1. **Holds ground** that only that industry may build on: a number of km², or a share of the city's dry ground that grows with the city.
2. **Prices it**: a share of the going business price, from 100% (held, not discounted) to 0% (free). Ground in a zone is assessed for property tax at that price.
3. **Shows its taxes**: the industry's profit, sales and property rates, editable in place. They are the same dials as Policy › Taxes, so moving one in the zone moves it everywhere.

**The forest law** is city-wide. It splits the city's forest ground three ways: **protected** (never cut, never built on), **working** (cut, never built on) and **open** (cut or built on). The default is all open. A building on open forest clears it.

**Would it fix forestry?** Partly, and the part it does not fix is the important finding.

| Problem | Does zoning fix it? |
|---|---|
| **B. Plantation land costs D$0.5–7.7B** at business prices | **Yes.** Forestry plants only on ground zoned for it, at the zone's price, down to free. No zone, no plantation. |
| **E. Crews strip the forest** (one crew clears 10 km² in 11 months) | **No, not by itself.** E is still needed: it stops crews being built for forest that will run out. Count only the forest Forestry may cut. |
| **How much forest stands in the long run** | **Only the law decides it.** Under the game's regrowth rule a stripped forest regrows fastest, so with E alone the forest still ends at 0% standing, in about 17 years instead of 5. With 40% protected it settles at exactly 40%. |
| Houses against forest | **Yes, and it was unanswered.** Today nothing says whether a house can stand on forest ground or what happens to the timber under it (to check). The law decides it. |

So: **zoning fixes the land, E fixes the crews, and the protected share is the forest the city keeps.** The player gets the forest he protects.

**A–E as decided** (the build plan's questions, recorded in `materials-chains-research.md` §10.3): A, C, D and E as recommended, E's horizon then set at 120 months; B replaced by this study. §7 has them.

**Samuel's answers, 2026-10-08 (§5):** all thirteen decided. Every recommendation stands except Z7: wood on forest ground a building takes is **sold by Forestry as a salvage cut**, not lost. E's horizon is **120 months** (Z13), the zone's tax rows are **the sector's own dials** (Z5), and the age rule for regrown wood is **not now** (Z12).

---

## 1. The model

### 1.1 With no zones
Nothing changes. Free ground is free for anyone, at the going business price, assessed at that price. Every trace should stay byte-identical with no zones and no forest law set, except where the clearing rule (§2.2) touches the forest stock.

### 1.2 A zone
A zone belongs to one sector (the 17 today, 24 after the materials run). It has:
- **a size**: km², or a share of the city's dry ground (★Z2). A share is re-read each month, so 2% stays 2% as the city buys land;
- **a price share**: 0–100% of the going business price.

**What it holds.** A new zone is empty of buildings. It takes free ground: bare ground first, then open forest (★Z9). The sector's buildings already standing elsewhere stay where they are and are not in it. Ground the sector builds on inside the zone stays in the zone. So a zone reads **used** (the sector's buildings in it) and **held** (free ground waiting for it).

**Who may build in it.** Only its sector. Every other business sees the free ground less what zones hold. The farms' rule that fields yield to housing ("nobody breaks ground on a field while somebody is sleeping outside") does not apply to ground zoned for Agriculture: the zone is the player's word (★Z11).

**Homes can be zoned too.** Real Estate is a sector, so ground can be held for homes, which is the usual reason real cities zone at all.

**When ground is short.** A zone holds only free ground. If the city has less free ground than the zone asks, it holds what there is, fills as land is bought, and says so: "holds 12.4 of 40 km² · buy land ›" (★Z10).

**Un-zoning.** Held ground goes back to everyone. Ground already sold in the zone stays with its owner, at what it paid.

### 1.3 The price
The zone's price is its share × the going business price (in the mockups, D$186 a m²). The city is paid that when the sector builds there. Ground already sold is not refunded or re-charged when the share changes.

**What it costs the city** is the price it does not get: shown as "price given up" on ground sold so far, and as what the held ground would fetch at the going price.

### 1.4 Property tax on zoned ground
Property tax is charged on land at today's sale price plus buildings at replacement cost (`property-tax-and-solvency.md`). **Zoned ground is assessed at the zone's price** (★Z4).

Why: ground that can only be sold to Forestry at nothing is worth nothing to a buyer. That is what zoning really does to land values, and the game already does it once: `FARMLAND_RELIEF` assesses farmland at use value, not development value (`farms.md`). It is a mechanism, not a modifier.

So a free zone removes the land half of the property tax, and a 40% zone cuts it to 40%. This answers "property tax would kill them" for the ground. The buildings stay taxed at their replacement cost, through the sector's property offset (§1.5).

In the mockups' city, Forestry's one plantation stands on 10 km² zoned free. At the going price that ground would be assessed at D$1.86B, and taxed D$2.33M a month: about what Forestry adds in a month (D$2.6M, materials mockup 4). Zoned free, it pays nothing on the ground.

### 1.5 The zone's taxes: one dial, two doors
The zone page shows the sector's three rates as the city rate, the sector's offset and what it resolves to:
- **profit**, city 15.00%, offset ±30 points;
- **sales** (the VAT), city 15.00%, offset ±30;
- **property**, city 1.50% a year, offset ±10 (`MAX_PROPERTY_TAX`).

**They are the sector's own offsets** (★Z5), the dials Policy › Taxes already has (`TaxPolicy.effective*()`). Moving one in the zone moves it for every building of that sector, zoned or not, and the zone page says so. Two sets of rates for one tax, one zoned and one not, would double the dials and the bugs (the Policy study's B4 was screen arithmetic of exactly that kind). The zone is a second door to the same dial.

### 1.6 The investors
- **Land gate:** a sector sees as free the ground its zones hold plus the ground free for anyone.
- **Land cost:** where it would build in its zone, the ground costs the zone's price, and its standing cost (`BusinessInvestment.standingCostOf()`) reads the zone's assessment.
- **A (decided):** every order is tested on its whole cost, whoever pays, and the ground in that cost is at the zone's price. **That is how a discount makes a plant pass:** the player prices the ground down until the investors' sums work, and sees what it costs the city.
- **Where it builds first:** in its zone, while the zone holds ground; then on ground free for anyone.

---

## 2. The forest law

### 2.1 Three kinds of forest ground
Forest is terrain on the world map (31% of land, 13,700 m³ a km², `spec-land.md`), and it comes with the land the city buys. The law splits the city's forest ground into:

| Kind | Cut? | Built on? |
|---|---|---|
| **Protected** | no | no |
| **Working** | yes | no |
| **Open** | yes | yes |

Two shares on one bar: kept from building (protected + working) and kept from cutting (protected). **Default: 0% and 0%, all open** (★Z6), Samuel's "free, anything everywhere".

The shares are of the city's forest ground, so they hold as the city buys more forest. Protected ground is taken from the most-standing forest first, so protecting 40% keeps 40% standing at once where the forest is still full.

### 2.2 Building on open forest clears it
A building that takes open forest ground takes **cut-over ground first** (it is already cleared), then standing forest (★Z7). The standing wood on it is cleared with it:
- it counts as extracted, so the world's totals still hold (W = unowned + remaining + extracted, `spec-land.md`);
- **Forestry sells it, as a salvage cut** (Z7, Samuel's answer): the month a building takes the ground, Forestry books the wood on it, half sawlogs and half pulpwood, at its usual cost a m³, and sells it like any cut. It needs no crew of its own and does not count toward E's crew test, which is about the forest that stays. Before the wood batch exists (no Forestry), the wood is lost. (est.) Whether a salvage cut counts toward "the cut exceeds regrowth" for planting is the implementer's to settle; recommended: no, since cleared ground does not grow back.

**To check first in code:** whether forest ground counts today as dry ground a building can take (`LandManager`, `CityLand`), and if so, whether anything clears the forest under it. The materials mockups showed a city with 412 ha free beside 1,850 km² of forest, which reads as if forest were not buildable; `spec-land.md` lists forest as one of an offer's five km² beside dry, which reads as if it were dry. The law makes the answer explicit either way.

### 2.3 Plantations are working forest
A Tree Plantation stands on ground zoned for Forestry. Its ground is working forest once planted: cut, never built on.

---

## 3. Forestry under zoning: would it fix it?

### 3.1 Plantations (B): yes
The decided rule (materials §5.7, Q15) stays: Forestry plants a Tree Plantation as a cost of cutting whenever its cut exceeds regrowth. What changes is the ground:
- **it plants only on ground zoned for Forestry**, at the zone's price;
- **with no Forestry zone it plants nothing.** No cut can carry D$0.5–7.7B of ground for a tree that is cut in 25 years, and the old rule would have had Forestry try.

So the plantation is the player's decision, made where it belongs: whether 10 km² of the city's ground is worth more as forest than sold. Zoned free, a plantation costs Forestry D$2M; at the going price its ground alone would cost D$1.86B in the mockups' city.

This replaces the recommended Crown licence. The licence was the city keeping the ground and lending it to Forestry; a free zone is that, chosen by the player and shown with its price.

### 3.2 Crews (E): still needed
A model of the game's rule (`zoning-forest-model.py`): the natural woods of the materials city (1,850 km², 25.3M m³ full, 21.9M standing), six Harvesting Operations of 12,500 m³ a month, investors adding at most one crew a month while the sector runs at 90% or more, and a crew shed after six months under 50%.

| Rule | Peak crews | Forest standing | Cut a month, long run | Crews, long run |
|---|---|---|---|---|
| **Q14 as decided** (no limit) | 63, month 57 | **0% by year 5** | 105.6k m³ | 16 |
| **E** (120 months, decided) | 14, month 8 | **0% by year 17** | 105.6k m³ | 14 |
| **E + law, 40% protected** | 7, month 1 | **40%** from year 22 | 63.4k m³ | 7 |
| **E + law, 60% protected** | 6, never added | **60%** from year 12 | 42.2k m³ | 6 |

At the 36 months first recommended, the same rows read: E alone 34 crews and 0% by year 6; 40% protected, 21 crews and 40% from year 5, 11 of the crews later shed.

What it says:
- **E stops the worst of it, not the end of it.** It cuts the peak from 63 crews to 14 and spreads the strip over 17 years. It stops crews being built for a small forest: a 10 km² forest (137,000 m³) gets no crew at all, since even a Logging Camp needs 240,000 m³ for 120 months of its cut. But on a big forest the forest still goes.
- **Count only the forest Forestry may cut** (★Z8): working and open forest, and its plantations. Protected forest is not wood Forestry can count on.

### 3.3 Why only the law keeps forest: the regrowth rule pays most at 0%
The game's rule regrows 1/240 of what was cut, a month. So regrowth is largest when everything has been cut:
- at today's 86% standing the natural woods regrow 14.4k m³ a month;
- at 0% they regrow **105.6k m³ a month**, every month, forever.

So for investors a stripped forest is the best forest, and nothing in the model stops them reaching it: E only slows the way there. **The protected share is what the city keeps.** In the table the long-run standing equals the protected share exactly, because everything cuttable is cut to the bone.

Jerus kept the 1/240 rule (Q13), and it is not changed here. One mechanism would change the picture, listed as an option (★Z12), not a recommendation:
- **Regrown wood cuttable only once it is old enough** (40 years here, est.), kept as a ring of monthly cohorts like the sickness ring.
- In the model it settles at about **67% standing** (nearly all of it young) and **35k m³ a month**. Stripping then has a price the investors' own sums see: after the old wood is gone, about **35 years with nothing to cut**, and every crew shed.
- That is real forestry, and it is brutal on a city that strips first. With the law it would rarely bite.

### 3.4 E's horizon
36 months is about a crew's payback (D$15M at 2.7% a month). On a big forest it lets far more crews be built than the forest can keep:
- E + 40% protected, at 36 months: 21 crews by month 15, then 11 of them shed;
- at 120 months: 7 crews, none shed, the same 40% and 63.4k m³ in the long run.

**Decided: 120 months** (Z13, Samuel), a crew's working life rather than its payback (est.).

### 3.5 The answer, in one line each
- **B:** fixed by the Forestry zone.
- **E:** keep it, on the forest Forestry may cut, at 120 months.
- **The forest's size:** the protected share, the player's lever.
- **Houses against forest:** the working and protected shares.

---

## 4. The screens

Mockups: the artifact "Zoning Study", three boards in the house style (the oil and materials mockups' frame, 1,389 × 868).

**Board 1: Policy › Zoning, the hub.**
- **Head:** the money-blue swatch, Policy's five area chips (Zoning lit), the breadcrumb.
- **Four figures:** ZONED "75.0 km²" (2.9% of dry ground · 4 zones); HELD FOR THEM "55.9 km²" (free ground no other business may take); PRICE GIVEN UP "D$1.93B" (on 10.6 km² sold in zones · tax relief D$2.41M a month); FOREST KEPT "none" (all open, the default; board 3 stages the law).
- **THE CITY'S GROUND:** one bar of the dry ground: built, each zone (used solid, held hatched), bare ground free for anyone, and the forest (by its three kinds once a law is set); under it a closer bar of the zones and the bare ground.
- **THE ZONES:** a row each: the sector's icon and name, size and share, used against held, its price, its three rates as chips (an offset marked), the month's tax relief, a door.
- **THE FOREST LAW,** the bar and what it means, with a door.
- A "Zone ground for an industry" button.

**Board 2: Policy › Zoning › Forestry.** Dial cards, as Policy's:
- **HOW MUCH GROUND,** staged 40 → 60 km²: plantations it can carry 4 → 6; bare ground free for anyone 25.3 → 5.3 km²; the zone's ground at the going price D$7.44B → D$11.16B; regrowth once grown +6.7k → +10.0k m³ a month (25 years).
- **ITS PRICE,** 0–100% of D$186 a m², at "free": a plantation's ground D$0 (D$1.86B at the going price); property tax on the zone's ground D$0 (D$13.95M a month at the going price, all 60 km²).
- **ITS TAXES:** profit, sales and property as city rate + offset → rate, each with its last month in money; property staged −0.50 points, D$137.5k → D$91.7k a month on its buildings; the line "The same dials as Policy › Taxes: a move here moves it for every Forestry building".
- **The tray:** "Apply 2" and "Leave it as it is".

**Board 3: Policy › Zoning › The forest law.**
- **The bar** with two thumbs, staged 0/0 → 40% protected, 30% working.
- **WHAT IT WOULD DO,** in the long run: forest standing 0% → 40%; the most the crews can cut 105.6k → 63.4k m³ a month; crews 14 → 7; forest a building may take 1,850 → 555 km²; when it gets there, 0% in year 17 → 40% in year 22.
- **WHERE THE FOREST SETTLES:** standing over 100 years under no law, this law, and 60% protected, from the model.
- **THE CREW RULE (E)** line, at 120 months: room for 1 more crew now; the forest carries 7.

**Elsewhere** (not drawn):
- **Land office:** THE CITY's line adds "75.0 km² zoned".
- **Build › LAND FREE:** "free for anyone 25.3 km² · zoned for Forestry 30.0 ›".
- **The investors' word:** "no land: 55.9 km² is zoned for others" when that is why.
- **NEEDS YOU's GROUND row:** names what zones hold when homes are short of ground.
- **The map:** tints a zone's districts later. Zones are quantities, not places (★Z1), so the map picks where to draw them; the map draws from the model and never the other way.

---

## 5. Decisions (Samuel, 2026-10-08)

All thirteen answered the same day. Each line gives the decision in bold. He took every recommendation but Z7, and answered Z5, Z12 and Z13 in his own words: "yes 120, oh ya obviously for shared dials, aka for that sector, sure".

1. **Z1. Zones are amounts, not places.** **Yes:** km² or a share, no map painting. The model has no positions; the map tints districts to match.
2. **Z2. Km² or a share of dry ground.** **Both, share the default:** a share grows with the city.
3. **Z3. The price.** **A share of the going business price, 0–100%,** charged when the sector builds; no refunds when it changes.
4. **Z4. Zoned ground assessed at the zone's price.** **Yes,** as farmland relief already does.
5. **Z5. The zone's tax dials.** **The sector's own offsets** (one dial, two doors): "obviously for shared dials, aka for that sector". Not a second set of rates for buildings on zoned ground.
6. **Z6. The forest law.** **Three kinds on one bar,** default all open.
7. **Z7. Building on open forest.** **Clears it,** cut-over ground first; the wood counts as extracted and **Forestry sells it as a salvage cut** (§2.2). Recommended was that it be lost; Samuel chose the salvage.
8. **Z8. E on the forest Forestry may cut.** **Yes:** working, open and its plantations.
9. **Z9. Where a zone's ground comes from.** **Bare ground first, then open forest.**
10. **Z10. A zone bigger than the free ground.** **Holds what is free, fills as land is bought, says so.**
11. **Z11. Zoned farms and the housing rule.** **Zoned fields do not yield to homes;** unzoned fields still do.
12. **Z12. An age rule for regrown wood.** **Not now** (his "sure", read as taking the recommendation). It stays on record as the one change that makes stripping cost the investors something.
13. **Z13. E's horizon.** **120 months**, not 36, so a big forest does not build 21 crews to keep 10 ("yes 120").

---

## 6. For the implementer

**Order.** Zoning and the forest law before the wood batch (I-wood): the wood batch's planting rule needs the Forestry zone, and E needs the law. Zoning can ship on its own first, with no forestry.

**Model (est. names):**
- `Zoning` (new): zones by sector (size as km² or share, price share), and the law (protected and kept-from-building shares). Read by `LandManager`, the investors and `TaxPolicy`'s assessment.
- `LandManager`: `freeFor(sector)`, `freeForAnyone()`, held and used by zone; the forest's three kinds; the clearing rule.
- Land price: `groundPriceFor(sector)` at the zone's share; `EconomyManager.getAssessedValue()` and `BusinessInvestment.standingCostOf()` read the zone's assessment.
- Forestry (wood batch): planting only on its zone; E on cuttable forest.
- `PolicyPreview`: zoning's before → after, as for every other dial.

**Save:** zones and the law are new state. With none set an older save loads as today.

**Harness, `ZoningCheck`:**
- with no zones and the law at 0/0, the month equals today's (traces byte-identical but for the clearing rule);
- a zone's ground is built on only by its sector; held ground leaves others' free ground;
- the city is paid the zone's price; zoned ground is assessed at it;
- a zone dial moves the sector's offset, the same as Policy › Taxes;
- protected forest is never cut or built on; building on open forest clears cut-over ground first, and its standing wood reaches Forestry as a salvage cut; the world totals hold;
- E adds a crew only while the forest Forestry may cut lasts 120 months at the cut with it;
- no Forestry zone, no plantation;
- a saved and reloaded city is identical.

**Screens:** PolicyScreen's fifth area (hub, a page a zone, the forest law), the dial cards and tray as built in 0.7.36; the Land office, Build and NEEDS YOU lines in §4.

---

## 7. A–E (the build plan's questions), as decided

Recorded with the materials study (`materials-chains-research.md` §10.3).

- **A. When a works may be built: test every order on its whole cost, whoever pays.** Today a works paid from the sector's own cash is not asked to earn anything, so loss-making plants get built. With zoning, the ground in that cost is at its zone's price.
- **B. Plantation land: replaced by zoning** (this study). No licence: Forestry plants only on ground zoned for it, at the zone's price; with no zone it plants none.
- **C. Car plant prices: car, van and train prices move with the steel in them,** as fabricated steel and machinery did (Q6b).
- **D. Iron ground price: keep US$0.40 a tonne in the ground.** The new ore price would have cut the founding field from US$180M to US$109M; the in-ground share is re-struck so a tonne stays US$0.40.
- **E. Forestry crews: add a crew only while the forest would last 120 months at that cutting rate,** counting only forest Forestry may cut (Z8). The plan said 36 months; Samuel set 120 (Z13).

---

## 8. Found on the way

1. **The regrowth rule rewards stripping** (§3.3). Regrowth is largest at 0% standing (105.6k m³ a month against 14.4k at 86% in the materials city), so investors left alone strip the forest and keep it stripped.
2. **Forest ground and building** (§2.2): whether forest counts as buildable dry ground today, and whether it is cleared, is not settled in the notes (to check).
3. **Old plantations would be taxed out:** ground Forestry bought when it was cheap is assessed at today's price. In a city of 2.61M, 120 km² of plantations (materials mockup 4) would be assessed at about D$22.3B, D$27.9M a month in tax against the D$2.6M Forestry adds. Z4 fixes it for zoned ground; plantations outside a zone stay exposed.
4. **The plantation's size.** At 1,000 ha a plantation needs 10 km², 11% of Samuel's old city's 89.6 km² of dry ground. A smaller woodlot (100 ha, 167 m³ a month once grown) would let a small city's zone fill in steps.
5. **The materials mockups' 412 ha** of free ground beside 1,850 km² of forest (mockups 4 and 6) assumed forest is not buildable; under the default law here it is.
