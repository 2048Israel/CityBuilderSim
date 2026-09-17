package ham.citybuildersim;

/**
 * A thing that can be made, bought, held, imported and exported.
 *
 * THE GOODS ECONOMY (2026-09-11). Until the sector template, the city had
 * four bespoke markets - food between the mills and the shops, ore between
 * the mines and the mills, a free yard of building material, and rent - and
 * every one of them was written by hand inside two handlers that knew each
 * other by name. A thousand sectors cannot trade that way. So a good is one
 * row here, a market is one object per row (see GoodsMarket), and a sector
 * says which rows it makes and which it uses (see Sector). Nothing else has
 * to know that bread and ore are different.
 *
 * PRICES ARE IN THE WORLD'S MONEY, per unit, in THOUSANDS like every money
 * field in the game (see BuildingsTemplate's header). A unit of food is what
 * one person eats in a month, so .20 is $200 of food; ore is .14 a tonne,
 * $140; steel .847, $847; a unit of building material is 18, $18,000, and a
 * House is ten of them. The city faces these multiplied by the exchange rate
 * (city money per dollar, times the world's own price level - see
 * Game.startOfMonthUpdate), which is why they are stored unconverted: a
 * devaluation lifts every ceiling and every floor at once, and that is what
 * a devaluation does.
 *
 * TWO WORLD PRICES, NOT ONE. What the world charges the city for a unit
 * (the import price, the CEILING on the local price - nobody pays more at
 * home than it costs to bring one in) and what the world pays the city for
 * one (the export price, the FLOOR - nobody sells at home for less than the
 * ship pays). The gap between them is freight, middlemen and the buyer's
 * margin, and it is the whole of what makes a local market worth having:
 * inside the band a local buyer and a local seller both do better than
 * trading with the world. Food's .12 against .20 is the 0.6 wedge
 * IndustrialHandler measured; ore's .14 against .41 is the mine's export
 * price against the mill's scrap, which IronMarket carried as its two ends.
 * A good with no import price (NaN) cannot be imported and has no ceiling
 * from the world; a good with no export price cannot be exported and its
 * makers idle or stock what they cannot sell.
 *
 * ...AND SINCE 2026-09-16 THE GAP IS ARITHMETIC RATHER THAN A SENTENCE.
 *
 * The paragraph above has always said what the wedge IS - "freight, middlemen
 * and the buyer's margin" - and for a year it said it in words while the code
 * carried two constants nobody could move. Jerus: "i really like the idea of
 * decomposing the existing import/export bands into world price +
 * transportation cost... then later, transportation efficiency can make that
 * freight component smaller or larger."
 *
 * So each good now carries a THIRD number, the cost of moving one unit, and
 * the two ends split into four:
 *
 *      worldBuyPrice   the world's own ask, before anything is moved
 *    + baseFreight     getting it here
 *    = worldImportPrice
 *
 *      worldSellPrice  the world's own bid
 *    - baseFreight     getting it there
 *    = worldExportPrice
 *
 * THE DELIVERED PRICES ARE THE STORED ONES AND THE WORLD BAND IS DERIVED,
 * which is the opposite of how it reads and is the only way round that is
 * exact. Storing the world band and adding freight back gives 0.847 as
 * 0.8470000000000002 for steel and 0.28 as 0.27999999999999997 for crops -
 * one ulp, which in this codebase is never nothing: DenominationCheck spent
 * a day on a 1e-16 tie that tripled a company's share price. Deriving the
 * other way cannot drift, because nothing on the hot path is recomputed at
 * all.
 *
 * WHAT IT IS FOR. Today `baseFreight` is read by nothing and every price in
 * the game is the literal it always was. It exists so that freight can later
 * become a PRICE - what the haulage sector charges - rather than a constant,
 * at which point a city with good logistics faces a narrow band and one
 * without faces today's wide one. The world's own margin stays where it is;
 * that half is not the player's to move. See
 * claude/transport-and-the-freight-band.md.
 *
 * THE SPLIT IS THREE QUARTERS FREIGHT, ONE QUARTER THE WORLD'S MARGIN, on
 * every good that has both ends, and it is a game decision rather than a
 * measured one: it sets how much of the wedge good logistics can ever win
 * back. At three quarters a 23% band bottoms out near 6%; at a half it would
 * stop at 11% and the mechanic would barely be worth the screens.
 *
 * Two goods are wider than the rest and it is the right two. IRON at 49% of
 * its world price and MATERIALS at 43%, against 21-25% for the whole food
 * shelf and steel - ore and aggregate are the cheapest things here per tonne,
 * and freight really is half of what delivered ore costs. Nobody designed the
 * table that way; it came out that way, which is the best evidence there is
 * that a freight reading of the wedge is the right one.
 *
 * WHAT CARRIES NO FREIGHT. The three seat-months, because a month of
 * somebody's work is delivered down a wire and there is nothing to ship. The
 * seller-priced three, because the world never sees them. Both read zero and
 * both are correct rather than unfinished.
 *
 * THREE FLAGS. Importable and exportable are the two prices. Stockable says
 * whether a maker can hold it from one month to the next: food keeps, in a
 * warehouse the plant owns; ore does not - a mine ships what it lifts; steel
 * does not. A service - a haircut, a night in a hotel - is none of the
 * three, and that is how the thousand sectors that are not factories fit the
 * same template.
 *
 * WHO SETS THE PRICE. Most goods clear in the band, on scarcity, in
 * GoodsMarket - that is Pricing.BAND. Two kinds do not: what a sector sells
 * straight to households or the city at a price it strikes itself - the
 * shops' cost-plus shelf, the landlords' lagged rent, the builders' order
 * price - is Pricing.SELLER, and the market for such a good only records
 * what was sold. See Sector for the hooks.
 *
 * NAMES ARE SAVED. A sector's stock and the month's trades are keyed by the
 * enum name, so a constant here can be added but not renamed.
 */
public enum Good {

    /**
     * What the fields grow, by the tonne, before anybody has done anything to
     * it - grain, roots, vegetables, fruit, the milk and the feed behind the
     * meat. THE TENTH SECTOR'S OUTPUT and the mills' first real input
     * (2026-09-13, Jerus): until this good existed the Textile Mill and the
     * Food Processing Plant made food OUT OF NOTHING, which is why the pair
     * of them ran a 72-77% operating margin and the Food Processing Plant
     * returned 6.2% of its build cost a month - twice the next best building
     * in the game, on a raw material that cost nothing because there was none.
     *
     * NINE AND A HALF UNITS OF FOOD TO THE TONNE, which is a person fed for
     * nine and a half months - about 1.26 tonnes of farm output a person a
     * year. That is gross farm-gate MASS rather than what anybody eats: a
     * couple of hundred kilos of milk, a hundred of potatoes, eighty of
     * vegetables, seventy of fruit, seventy of meat carrying feed behind it, a
     * hundred of cereals. Counted that way 1.26 tonnes is squarely inside the
     * developed-diet range of 0.9 to 1.5.
     *
     * IT WAS 6.5 FOR ONE AFTERNOON, and the afternoon is worth writing down
     * because it is the shape of this whole design. At 6.5 the crop bill came
     * to 43.6% of what a mill sold at the price food actually clears at over a
     * long run - and a mill's payroll is 36.7% at that same price, because
     * wages are domestic and rise while the food price is the world's and
     * falls. Eighty percent of revenue gone before the lights: the mills went
     * bankrupt fourteen times in 333 years, stopped buying, and THE FARMS DIED
     * WITH THEM - seven restructures and nothing standing at the end, because
     * the only customer had gone.
     *
     * THE FIX COULD NOT BE THE PRICE, and that is the interesting part. The
     * crop price is the farms' revenue and the mills' cost at the same time, so
     * cutting it to save the mills kills the farms as surely as leaving it
     * killed them. What was wrong was the QUANTITY: 1.85 tonnes of farm output
     * a person a year is the top of the real range and 1.26 is the middle of
     * it, and moving it leaves the farms' economics untouched - a tonne still
     * fetches what a tonne fetches - while taking the mills' crop bill to 29.7%
     * of revenue, which is what a real processor pays for its raw material.
     *
     * .44 IN AND .28 OUT, the low end of a real mixed basket - wheat runs
     * about $250 a tonne, corn $200, potatoes $300, apples $800, milk $600 -
     * so a delivered mixed basket at $440 and a distant farm's gate at $280.
     * The 1.57x wedge is food's own 1.67 and steel's 1.52. THE LOW END IS
     * DELIBERATE and it is the breathing room Jerus asked for: at the ceiling,
     * which is what a city with no fields of its own pays, the two mills land
     * at 29% and 35% of revenue in operating income, which is the band every
     * other plant in this game occupies. Priced any higher they would be
     * underwater the day a city was founded, because a city is founded with
     * mills and without farms.
     *
     * STOCKABLE, unlike every other flow good here, because a harvest keeps -
     * that is what a silo is for, and it is the whole reason a farm can sell
     * into a market that wants the same amount every month.
     */
    CROPS("Crops", "tonne", .44, .28, .06, true, Pricing.BAND, false),

    /* =====================================================================
       FOOD IS GONE, AND THIS IS THE NOTE THAT REPLACES IT.

       FOOD("Food", "unit", .20, .12, ...) - one unit was one person fed for one
       month, everything they ate, and for the life of this project every
       question about what a city ate had that one answer. The mills made it out
       of crops and the shops sold it on.

       It was retired on 2026-09-15, behind the thirteen below. Two things it
       had been standing in for are worth naming, because both are now real:
       the shops' INPUT, which is thirteen invoices; and the ovens' OUTPUT,
       which is BREAD. The retail sale is still one good - GROCERIES, one unit a
       head a month - because the household ledger, hunger, subsistence and the
       price index are all written in it.

       A SAVE WRITTEN BEFORE THAT DAY STILL HAS "FOOD" IN ITS STOCK AND PANTRY.
       Sector.restore() maps goods by name and drops a name this build does not
       know, which is the right answer and the same one the age bands take: that
       food cannot go anywhere honest, because nothing eats it any more. See the
       note there.
       ===================================================================== */

    /* =====================================================================
       THE THIRTEEN, AND WHY THEY ARE PRICED IN KILOGRAMS

       The shops' basket, itemised. Until 2026-09-15 a city bought one good
       called FOOD, one unit of it a person a month, and every question about
       what people ate had the same answer. These are that unit taken apart.

       KILOGRAMS, NOT TONNES, and deliberately out of step with CROPS and IRON
       above. consumption.json is written in kilograms because that is the unit
       a household's month is legible in - three kilos of grain, four and a
       half of meat - and the ONE thing that must never happen to these numbers
       is a conversion. This project has found twenty bugs of the units family
       and every one of them lived at a boundary where the same quantity had
       two names. So the file's number and the enum's number are the same
       number, and ConsumptionCheck asserts to the cent that they still are.

       THE PRICES ARE THE FILE'S PRICES. consumption.json owns what a good
       costs the world; these constants exist because the market machinery is
       built on the enum and cannot read a file at class-init time. That is a
       duplication, so it is guarded rather than trusted - see section 9 of
       ConsumptionCheck, which fails the day the two disagree.

       ALL THIRTEEN ARE IMPORTED TODAY. No industry makes any of them; a city
       buys its dinner from the world. That is Jerus's call and it is what
       makes this batch a decomposition rather than a supply chain: thirteen
       import lines where there was one, and nothing new to produce.
       ===================================================================== */

    /** The cheapest calorie there is, and what subsistence is measured in. */
    GRAINS("Grains", "kg", .00080, .00050, .000113, true, Pricing.BAND, false),

    /** A staple with the milling and baking already done. */
    BREAD("Bread", "kg", .00250, .00160, .000338, true, Pricing.BAND, false),

    /** Milk, cheese and eggs - the protein a poor city can still afford. */
    DAIRY_EGGS("Dairy and eggs", "kg", .00300, .00190, .000412, true, Pricing.BAND, false),

    /** Cheap by the kilo, dear by the calorie, which is why the poor eat few. */
    VEGETABLES("Vegetables", "kg", .00180, .00110, .000262, true, Pricing.BAND, false),

    /** The most income-elastic produce in the file: the first thing a raise buys. */
    FRUIT("Fruit", "kg", .00250, .00150, .000375, true, Pricing.BAND, false),

    /** Bennett's law in one line - the share of this rises with every wage. */
    MEAT("Meat", "kg", .00700, .00440, .000975, true, Pricing.BAND, false),

    /** Dearer than meat and healthier than it; the last thing a city learns to buy. */
    FISH("Fish", "kg", .00800, .00500, .001125, true, Pricing.BAND, false),

    /** Cooking fats. Eight thousand calories a kilo, and almost no quality. */
    FATS("Cooking fats", "kg", .00300, .00190, .000412, true, Pricing.BAND, false),

    /** Bought for convenience, not for nutrition - see Consumption's time axis. */
    PROCESSED_MEAT("Processed meats", "kg", .00900, .00560, .001275, true, Pricing.BAND, false),

    /** What a household with two earners and three children eats on a Tuesday. */
    READY_MEALS("Ready meals", "kg", .00800, .00500, .001125, true, Pricing.BAND, false),

    /** Bakery goods, as distinct from bread: a treat, priced like one. */
    BAKERY("Bakery goods", "kg", .00600, .00370, .000862, true, Pricing.BAND, false),

    /** The dearest calorie in the file, and the one a rich city buys most of. */
    SNACKS("Snacks", "kg", .01000, .00620, .001425, true, Pricing.BAND, false),

    /** Mostly water, sold by the kilo, and a tenth of what the city spends. */
    DRINKS("Drinks", "kg", .00150, .00090, .000225, true, Pricing.BAND, false),

    /**
     * Iron ore, and the scrap that stands in for it. Made by the mines, used
     * by the mills. The import price is imported scrap, the export price is
     * what a mine gets shipping ore out - IronMarket's two ends, unchanged.
     */
    IRON("Iron ore", "tonne", .41, .14, .101, false, Pricing.BAND, false),

    /**
     * Smelted by the mills. .847 is scrap plus half the real conversion
     * margin - a small distant producer is a price taker at both ends, see
     * IronMarket's note on why the numbers are these numbers.
     *
     * IT HAS A CEILING NOW (2026-09-13), AND IT HAS ONE BECAUSE SOMETHING
     * FINALLY BUYS IT. For two builds the comment here read "not importable:
     * there is no local buyer to import for", and that was true and it made
     * steel the one good in the game with no ceiling from the world -
     * GoodsMarket priced it between the floor and twice the floor, a guess
     * its own header admitted to and said mattered for no good in the game.
     * The ninth sector buys steel, so the guess started mattering.
     *
     * 1.284 is the US hot-rolled band, SteelBenchmarker 26 Aug 2026 - the
     * same quote MiningCheck's note already cites for the real conversion
     * margin. It is the price the city pays to bring a tonne in, so it is
     * also the most a fabricator here will pay a mill here, and the gap to
     * the mill's own .847 export floor is the whole of what a mill and a
     * fabricator in the same city are worth to each other.
     *
     * NOTHING CHANGES FOR A CITY WITH NO FABRICATOR. With no local buyer
     * demand is zero, the strike puts the price at the floor, and the mills
     * ship every tonne abroad at .847 exactly as before. The ceiling is a
     * ceiling; it binds only when somebody bids.
     */
    STEEL("Steel", "tonne", 1.284, .847, .1639, false, Pricing.BAND, false),

    /**
     * One unit of building material - a House is ten of them. Made by the
     * materials plant, used by the builders on every order and every repair,
     * imported at BuildingManager.MATERIALS_WORLD_PRICE when the city has
     * none. Exportable, at a WIDER wedge than food's: aggregate and cement
     * are heavy and cheap, and shipping them out is most of their price.
     *
     * The wedge was food's - sixty percent of the import price - for one
     * afternoon, and at that price a plant's payroll came to $7,500 a unit
     * against $10,800 abroad, so a plant with nobody to build for was a
     * profitable export mill: the first fixture city that built one built
     * six, shipped $1.1B of aggregate abroad in five years and doubled its
     * population on the jobs. Forty percent puts the world's price under
     * the plant's payroll: a plant still ships its spare nameplate rather
     * than idle (the staff are paid either way), but it cannot LIVE on it,
     * and a city that stops building sells its plants back within the
     * year. The sector is the builders' supplier, not an exporter.
     */
    MATERIALS("Building materials", "unit", 18, 7.2, 4.05, true, Pricing.BAND, false),

    /**
     * What the shops sell: food, on a shelf, to a household. One unit of
     * food becomes one unit of this. Priced by the shops, cost-plus with a
     * scarcity mark-up (see sectors.Retail), never traded with the world.
     */
    GROCERIES("Groceries", "unit", Double.NaN, Double.NaN, 0, false, Pricing.SELLER, false),

    /**
     * A home for a month. Made by the landlords out of their doors, priced
     * by the lagged rent walk (see sectors.RealEstate), and EXEMPT from sales
     * tax - long-term residential rent is an exempt supply under real HST,
     * and taxing it would put fifteen percent on every tenant in the city.
     */
    HOUSING("Housing", "home", Double.NaN, Double.NaN, 0, false, Pricing.SELLER, true),

    /**
     * A point of construction work. Made by the builders and sold to whoever
     * orders a building, at the order price; recognised as the points are
     * delivered. See sectors.Construction.
     */
    BUILDING_WORK("Building work", "point", Double.NaN, Double.NaN, 0, false, Pricing.SELLER, false),

    /* =======================================================================
       THE THREE THE WORLD PAYS FOR (2026-09-12)

       A seat-month: one month of one person's delivered work. That is not a
       game abstraction, it is how this industry is actually priced and sold -
       a contract is so many seats for so many months.

       All three are export-only, and that is the whole design. An export-only
       BAND good clears at its FLOOR, because there is no local demand to lift
       it off the bottom (STEEL already works this way). So the revenue per
       seat is fixed by the world, and whether the sector makes money depends
       entirely on its wage bill. The brake is the labour market: hire, and the
       band's wage walks up on clamp(tightness^0.5, .70, 4.0); unit cost rises;
       the expansion test fails; twenty-four months of losses sheds plant. No
       new rule, and no ceiling on the world's appetite - Jerus's call.

       And because the price arrives THROUGH THE EXCHANGE RATE, a weak currency
       wins the city contracts and a strong one kills the sector. That is
       offshoring, and it is the model working rather than a special case.

       The prices are the world's money in thousands per seat-month, so at
       founding (rate 1) they are also the local figures:

         SUPPORT_WORK      5.5   Canada contact-centre loaded rate $32/hr over
                                 173 billable hours a month = $66,400/yr. Two
                                 sources put payroll at 67-70% of revenue;
                                 Teleperformance's own 2024 accounts say 67%.
         BACK_OFFICE_WORK  8.6   Onshore back-office billing, anchored on
                                 Genpact (64.5%) and Concentrix (64.1%) cost of
                                 revenue. The listed BPOs' revenue per head is
                                 NOT usable here - it is dragged down by
                                 Philippine and Indian headcount.
         ENGINEERING_WORK 14.6   Stantec $183k and WSP $167k of net revenue per
                                 employee, CAD 2024, from their annual reports.

       Not importable: the city cannot buy somebody else's back office, and
       there is no local buyer to import for. Not stockable: a seat-month
       cannot be warehoused - the month is delivered or it is gone, which is
       also what makes this sector fail fast when the price turns. Not tax
       exempt: exports are zero-rated with input credits still claimable,
       exactly as steel already is.

       See claude/business-services.md for the sourcing and the arithmetic.
       ======================================================================= */

    SUPPORT_WORK("Support work", "seat-month", Double.NaN, 5.5, 0, false, Pricing.BAND, false),

    BACK_OFFICE_WORK("Back-office work", "seat-month", Double.NaN, 8.6, 0, false, Pricing.BAND, false),

    ENGINEERING_WORK("Engineering work", "seat-month", Double.NaN, 14.6, 0, false, Pricing.BAND, false),

    /* =======================================================================
       THE TWO THE CITY MAKES OUT OF ITS OWN STEEL (2026-09-13)

       The ninth sector's outputs, and the first goods in the game that are
       made from another sector's traded good rather than out of the ground.
       IRON to STEEL to these: three links, and the second one now has a
       customer that is not a ship.

       WHY A SECOND TRADABLE CHAIN AT ALL. Business Services answered the
       plateau with pure labour - no ore, no ground, a seat and a wage. It
       works, and it is bounded by exactly one thing: the unskilled wage. A
       city that succeeds at it bids that wage up until the centres close,
       which is the boom-and-bleed the sector was calibrated on and is
       supposed to do. What the city then has no answer for is the NEXT rung:
       work that pays more than a contact centre seat and can still be sold
       to somebody who is not here. That is manufacturing, and the two goods
       below are deliberately different from each other in the one property
       that decides which cities can do them.

       FABRICATED_STEEL is bounded by the STEEL PRICE. Steel is 61% of what
       a fabrication shop sells and the wage bill is 20%, so what decides
       whether a shop pays is whether there is a mill in the city: on
       imported steel at the 1.284 ceiling the margin is thin, on local steel
       in the middle of the band it is an ordinary business, and on steel at
       its export floor it is a good one. Which is the mine-and-mill story
       one link further up the chain, and the first time the game rewards
       putting THREE things near each other.

       MACHINERY is bounded by the WAGE and the CURRENCY, like a contact
       centre - the wage bill is about half of revenue and steel is a fifth -
       but at a wage a contact centre could never pay: diploma machinists and
       college technicians rather than agents. So it is the rung ABOVE
       business services rather than a competitor to it, and a city that has
       bid its unskilled wage out of call-centre range can still build one.

       THE PRICES, in the world's money per tonne, in thousands.

         FABRICATED_STEEL  2.21
              Shop-fabricated structural steel runs $1.10-1.40 a pound with
              material in it - $2,430 to $3,090 a tonne - which is about
              twice the hot-rolled band it is cut from, and that doubling IS
              the trade. $2,600 as the world's own price, less 15% for
              distance and freight. NOT the 50% price-taker haircut steel
              takes: a fabricator quotes a job for a named client, it does
              not sell into a spot market, and a bid business does not
              discount like a commodity one.

         MACHINERY  9.0
              Heavy equipment and industrial machinery, at about $10,600 a
              tonne before the same 15% distance discount. Every number in
              the plant below follows from it: at 180 tonnes a month a
              machine works bills $1.62M, buys $335k of steel, and pays $796k
              in wages to a hundred and seventy-three people.

       FABRICATED STEEL IS NOT IMPORTABLE, for the reason steel used to not
       be: nothing in the city buys a beam. It is made HERE out of steel and it
       leaves, so it clears at its floor and the world's price is simply what a
       tonne fetches - which is what makes the wage bill and the steel bill the
       whole of the question.

       MACHINERY GOT ITS CEILING ON 2026-09-16, exactly as the sentence that
       used to sit here said it would: "The day something here buys one, it
       gets a ceiling that day and for the reason steel just got one." The
       thing that buys one is an assembly plant - see the vehicles below.

       14.3 A TONNE, and the freight did not have to move for it: 1.98 was
       already three quarters of the half-wedge of a 14.3/9.0 band, which is
       the shape every other good in this table has. A 37% band, wider than
       steel's 34%, because a machine tool is a specialised thing to buy off a
       ship and a cheap thing to make next door to a foundry.

       AND FABRICATED STEEL DELIBERATELY DID NOT GET ONE, which is the whole
       design of the chain above it. A car is three and a half tonnes of
       fabricated steel and four hundred kilos of machinery, so the BULKY
       input is the one a city must be able to make itself and the specialised
       one can come off a ship - which is how industrialisation has actually
       worked everywhere it has happened. A city with fabricators can be in the
       car business; a city without them cannot, at any price.

       (What made this urgent rather than tidy: the four-thousand-month
       playtest builds 179 Fabrication Works and ZERO Machine Works, because
       Manufacturing's planner scores its two products against each other every
       month and fabrication wins every month. Gating an entire industry on a
       building the model never puts up is not a design decision, it is a dead
       branch. The monoculture underneath that - an unbounded export market at
       a fixed floor means the best export is built for ever and nothing else
       ever is - is real, is older than this batch, and is not fixed here.)

       NEITHER IS STOCKABLE, and that is not a simplification. Fabricated
       steel is cut for one client's drawings and a machine is built to one
       client's order; neither is a thing you warehouse against next month's
       price. So both take the flow path - made, sold at home if anybody
       here is buying, shipped if not - which is the path steel and ore
       already take and the one with no hog cycle in it.

       See claude/manufacturing.md.
       ======================================================================= */

    FABRICATED_STEEL("Fabricated steel", "tonne", Double.NaN, 2.21, .486, false, Pricing.BAND, false),

    MACHINERY("Machinery", "tonne", 14.3, 9.0, 1.98, false, Pricing.BAND, false),

    /* =======================================================================
       THE VEHICLES (2026-09-16)

       Jerus: "we are going to also add the autombile industry, where customers
       can buy cars and business can buy vans/trucks, and rail sector can buy
       trains."

       THE FIRST DOMESTIC CUSTOMER MACHINERY HAS EVER HAD. Read the header
       above this one: fabricated steel and machinery are made here out of
       steel and both leave, because nothing in the city buys a beam or a
       machine. An assembly plant buys both, which makes this the FOURTH LINK
       of a chain that until today stopped at three - ore, steel, fabrication,
       and now something a person drives away in.

       AND THE CHAIN CANNOT BE SHORTCUT, which is the whole point of putting
       the recipe on those two goods rather than on steel. Neither is
       importable: a city with no fabrication shop and no machine works cannot
       buy the parts from anywhere, so it cannot make cars, however much money
       it has. It can still BUY cars - CARS is importable like anything else -
       it simply cannot be in the business. That is the strongest version of
       the thing this game keeps rewarding: putting several things near each
       other.

       (The header above predicted that the day something here bought a machine
       it would need an import ceiling. It already has one: GoodsMarket.ceiling()
       falls back to twice the floor for a good the world will not sell, so a
       local buyer bidding into a shortage pays double rather than NaN. A real
       world ask for those two is a separate decision about how much of the
       chain a city may skip, and it is not this batch's.)

       THE PRICES, in the world's money, per vehicle, in thousands. Each band
       is the usual shape - the world's own margin plus freight at three
       quarters of the half-wedge - and each is NARROWER than the food shelf's
       21-25%, which is what value density does: moving a $44,000 car costs
       about what moving two tonnes of steel costs, and the car is worth
       thirty-four times as much.

         CARS  44 / 36, freight 3.0        18.2% band
              A car is about 1.5 tonnes of finished vehicle and takes roughly
              3.5 tonnes of fabricated steel and four hundred kilos of
              machinery to build, so the parts are about $11.3k against a
              $40k mid-band price - a bit over a quarter, which is where the
              Meat Works sits and is what a manufactured consumer good should
              look like. The rest is the wage bill, and that is the point of
              an automobile industry: it is the largest payroll a city of this
              size can build.

         VANS  72 / -, freight 4.5
              A working vehicle: heavier, dearer, and bought by a business
              rather than a household - and, like a locomotive, NOT something
              the world buys. Jerus asked for cars to be a real export
              industry; a van is a capital good a business orders for its own
              yard, and the two are different things.

              Measured with it exportable, and the measurement is why: the
              4,000-month playtest built TWO HUNDRED AND FORTY Commercial
              Vehicle Plants and shipped thirty-six thousand vans a month. That
              is the same failure 179 Fabrication Works is - an unbounded export
              market at a fixed floor means the single best export is built for
              ever and nothing else ever is - and the narrow fix is to stop
              pretending the world wants a town's vans. The general fix is a
              bounded world demand, which is a real piece of work and older
              than this batch.

         ROLLING_STOCK  2,400 / -, freight 150
              A locomotive and the wagons behind it. The most expensive single
              thing the world sells this city, and a railway needs one for
              every few thousand tonnes a month it means to move.

              THE ONE THE WORLD WILL NOT BUY, and that asymmetry is the whole
              design of it. Jerus asked for cars to be "a real export industry"
              and they are; a locomotive is not a consumer good with a world
              market, it is a capital good a railway orders for its own track.
              Left exportable it was measured doing exactly what an unbounded
              export market lets anything do: a fixture town of forty thousand
              people built NINETEEN Locomotive Works - 9,880 posts against the
              city's 25,102 jobs - and shipped seventy-six wagon sets a month
              to nobody in particular.

              So the world sells them, which is what lets a city lay track
              before it has an industry, and does not buy them. A Locomotive
              Works is therefore worth building only when the city's own
              railway wants trains - which is the next batch, and until then it
              is a building nobody has a reason to put up. That is the correct
              state for a plant with no customer, and it is the same state
              MACHINERY was in until this morning.

              The floor for a good the world will not buy is zero
              (GoodsMarket.floor()), so with no local buyer the price is zero
              and the planner values a works at nothing. Nothing else was
              needed to stop it.

       ALL THREE ARE STOCKABLE, unlike the two they are made from, and for the
       opposite reason: a beam is cut to one client's drawings and a car is
       not. A finished vehicle sits on a lot until somebody buys it, which is
       what a stockable good is for.
       ======================================================================= */

    CARS("Cars", "car", 44.0, 36.0, 3.0, true, Pricing.BAND, false),

    VANS("Vans and trucks", "van", 72.0, Double.NaN, 4.5, true, Pricing.BAND, false),

    ROLLING_STOCK("Rolling stock", "wagon set", 2400.0, Double.NaN, 150.0, true, Pricing.BAND, false);

    /** How a good's price is struck. */
    public enum Pricing {
        /** Clears in GoodsMarket between the export floor and the import ceiling. */
        BAND,
        /** The selling sector strikes it; the market only records the sale. */
        SELLER
    }

    private final String label;
    private final String unit;
    private final double worldImportPrice;
    private final double worldExportPrice;
    private final double baseFreight;
    private final boolean stockable;
    private final Pricing pricing;
    private final boolean taxExempt;

    Good(String label, String unit, double worldImportPrice, double worldExportPrice,
         double baseFreight, boolean stockable, Pricing pricing, boolean taxExempt) {
        this.label = label;
        this.unit = unit;
        this.worldImportPrice = worldImportPrice;
        this.worldExportPrice = worldExportPrice;
        this.baseFreight = baseFreight;
        this.stockable = stockable;
        this.pricing = pricing;
        this.taxExempt = taxExempt;
    }

    public String label() { return label; }
    public String unit()  { return unit; }

    /**
     * What the world charges for one DELIVERED HERE, in ITS money. NaN when it
     * will not sell the city any.
     *
     * The stored literal, untouched since the day it was written, and the
     * reason the decomposition below is exact. Every one of the fifty-eight
     * call sites in this codebase reads this and none of them had to change.
     */
    public double worldImportPrice() { return worldImportPrice; }

    /** What the world pays for one DELIVERED THERE, in ITS money. NaN when it will not buy any. */
    public double worldExportPrice() { return worldExportPrice; }

    /**
     * What it costs to move one unit between the city and the world, in the
     * world's money - three quarters of the wedge on every good that has both
     * ends, and zero for the things nobody ships.
     *
     * A CONSTANT TODAY AND NOT FOR EVER. This is the half of the band that
     * belongs to whoever carries the goods, and when that becomes a business
     * with a price to set, this is the number it sets. See the header.
     */
    public double baseFreight() { return baseFreight; }

    /**
     * The world's own ask, before anything is moved - the import price less
     * the freight in it. NaN when the world will not sell.
     *
     * The floor under the import ceiling: no amount of logistics gets a city
     * a unit cheaper than the world is willing to part with one for.
     */
    public double worldBuyPrice() { return worldImportPrice - baseFreight; }

    /**
     * The world's own bid, before anything is moved - the export price with
     * the freight added back. NaN when the world will not buy.
     *
     * The ceiling over the export floor, and with worldBuyPrice() the pair
     * that says how narrow this band could ever get. The gap between the two
     * is the world's margin and the player never touches it, which is why
     * freight can fall a long way and the market still has something to do.
     */
    public double worldSellPrice() { return worldExportPrice + baseFreight; }

    public boolean importable() { return !Double.isNaN(worldImportPrice) && worldImportPrice > 0; }
    public boolean exportable() { return !Double.isNaN(worldExportPrice) && worldExportPrice > 0; }
    public boolean stockable()  { return stockable; }
    public Pricing pricing()    { return pricing; }

    /** True for a supply the sales tax never touches. Housing, today. */
    public boolean taxExempt()  { return taxExempt; }

    /**
     * How many months of the city's take a maker averages before it plans
     * a plant against it. A year for a good people eat every month; three
     * for a good that is DRAWN ON ORDER - a foundry takes four hundred
     * units of material in the month it is ordered and none for the years
     * after, and a year's average of that read as seven hundred a month in
     * the first long run, built three plants, and sold them back within the
     * year. See GoodsMarket.getDemandTrend().
     */
    public int planningMonths() { return this == MATERIALS ? 36 : 12; }

    /** Clears in the band on scarcity, as opposed to being priced by its seller. */
    public boolean traded()     { return pricing == Pricing.BAND; }

    /* =======================================================================
       WHAT IT TAKES TO CARRY ONE (2026-09-16)

       Two facts a transport model needs and a price table never had: how much
       one unit WEIGHS, and WHAT KIND of traffic moving it is. See Traffic.
       ======================================================================= */

    /**
     * One unit, in tonnes.
     *
     * DERIVED FROM THE UNIT RATHER THAN STORED, because it already is: this
     * file has been writing "kg" and "tonne" in the second column since the
     * day it was written, and a good whose unit is a kilogram weighs a
     * kilogram. Adding twenty-five more numbers to say so again would be
     * twenty-five more chances to disagree with the column beside them.
     *
     * MATERIALS is the one that needs telling. Its unit is neither: "a House
     * is ten of them", and a house's structure is on the order of fifty
     * tonnes, so a unit is about five. Aggregate, cement and timber are the
     * heaviest thing this city moves per dollar, which is the same fact that
     * makes its freight wedge the second widest in the table.
     *
     * The non-physical goods weigh nothing and that is not a gap: a home, a
     * point of building work and a seat-month of somebody's attention are not
     * put on a lorry.
     */
    public double tonnesPerUnit() {
        if (this == MATERIALS) return 5;
        switch (unit) {
            case "kg":    return .001;
            case "tonne": return 1;
            /*
             * THE VEHICLES WEIGH WHAT THEY WEIGH. A unit here is one machine,
             * not a tonne, and the road and the railway both count tonnes - so
             * a car that reported nothing would cross the city boundary
             * weightless and cost nobody a freight bill. A wagon set is a
             * locomotive and what it pulls.
             */
            case "car":       return 1.5;
            case "van":       return 3;
            case "wagon set": return 300;
            default:      return 0;
        }
    }

    /**
     * Which stream of traffic a tonne of this joins, or null for the things
     * that never take up road at all.
     *
     * THE LINE IS DRAWN BY WHAT CAN CARRY IT, not by weight - see Traffic.
     * Ore, steel, crops, grain and aggregate move in hopper and flatbed loads
     * and a railway is the obvious answer to them. The shelf does not: a
     * shop's bread and milk is a van at a back door however many rails a city
     * lays, which is the whole of "some stuff wont even take the rail".
     *
     * DRINKS SITS ON THE LINE and is called GOODS. Twelve kilos a head a
     * month makes it far the heaviest thing on the shelf and it is mostly
     * water, which argues for BULK - but it is palletised, it is delivered to
     * shops, and it goes in the same van as everything else in the basket.
     * Worth revisiting if the freight screens ever make it look wrong.
     */
    public Traffic traffic() {
        switch (this) {
            case CROPS: case GRAINS: case IRON: case STEEL:
            case MATERIALS: case FABRICATED_STEEL: case MACHINERY:
            // A locomotive on a low-loader is not a crate of bread.
            case ROLLING_STOCK:
                return Traffic.BULK;
            case GROCERIES: case HOUSING: case BUILDING_WORK:
            case SUPPORT_WORK: case BACK_OFFICE_WORK: case ENGINEERING_WORK:
                return null;
            default:
                return Traffic.GOODS;
        }
    }

    /** The good with this saved name, or null - a save from a build without it loses that line, not the load. */
    public static Good byName(String name) {
        if (name == null) return null;
        for (Good g : values()) if (g.name().equals(name)) return g;
        return null;
    }
}
