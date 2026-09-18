/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ham.citybuildersim;

/**
 *
 * @author Jerus
 */
public enum BuildingType {
    RESIDENTIAL,
    COMMERCIAL,
    INDUSTRIAL,
    CONSTRUCTION,

    /**
     * Processors that buy their input abroad and sell their output abroad.
     *
     * Separate from INDUSTRIAL because that category feeds the food market and
     * prices off its own cost base; a second industry sharing that cost pool
     * would change what a loaf of bread appears to cost. See
     * sectors.HeavyIndustry.
     */
    HEAVY_INDUSTRY,

    ELECTRICITY,
    WATER,

    /**
     * The road network. Public works, not a business.
     *
     * APPENDED, NOT INSERTED - and it has to stay that way. Several saved arrays
     * are indexed by BuildingType.ordinal() (the per-sector property tax and
     * interest charges), so slipping a new constant in above an existing one
     * would silently hand every old save's industrial tax bill to whoever now
     * holds that ordinal. New categories go on the end, for the same reason
     * building ids in buildings.json are permanent.
     */
    INFRASTRUCTURE,

    /**
     * Iron mines. Its own category because the ore has a price.
     *
     * The cheap version made a mine a HEAVY_INDUSTRY building so the mills just
     * had a lower input cost - invisible, and no decision in it. Separate books
     * mean the ore clears on a market between two sectors that need each other,
     * which is the first thing in this economy that rewards building two
     * different things near each other. See sectors.Mining.
     *
     * On the END, like INFRASTRUCTURE and for the same reason: the saved
     * per-sector tax and interest arrays are indexed by ordinal().
     */
    MINING,

    /**
     * Childcare, hospitals, senior care and the two ways a city buries people.
     *
     * The city builds these and they never pay for themselves - patients are
     * charged something, but not enough, which is the point. Per Jerus: "the
     * first option, but patients still pay, just not much, aka its a net deficit
     * business."
     *
     * Its own category rather than a line under Industrial for the same reason
     * INFRASTRUCTURE got one: this is the second thing in the game the city
     * builds for itself, and a player looking for a hospital should not have to
     * find it filed under factories.
     *
     * ON THE END, like INFRASTRUCTURE and MINING before it. The per-sector
     * property tax and interest arrays are indexed by ordinal(), so inserting a
     * constant above an existing one would hand every old save's industrial tax
     * bill to whoever now holds that ordinal. There is room after this for
     * EDUCATION and SAFETY, which is why the menu button says Services.
     */
    HEALTHCARE,

    /**
     * Schools, colleges, universities and the four graduate schools.
     *
     * The other half of the labour market. Until this existed, every skilled
     * worker in the game had arrived from somewhere else - the city could
     * attract a doctor and could never make one - and the eleven job types were
     * a demand curve with no domestic supply behind them at all.
     *
     * Like healthcare, the city builds these and they never pay for themselves.
     * Unlike healthcare, what they produce is not a service consumed this month
     * but a person who will be working in twenty years, which is the longest
     * feedback loop in the game and the reason this category is worth having.
     *
     * ON THE END, for the third time and the same reason: ordinal() is a saved
     * key. The slot after this one is SAFETY.
     */
    EDUCATION,

    /**
     * Police stations, police headquarters, jails and penitentiaries.
     *
     * Jerus, 2026-09-11: "crime is a function of unemployment, and tight or
     * under households, we need police, and also prison". The police deter and
     * catch; the prisons hold whoever is caught. Neither removes a reason for
     * crime - "if there is a reason for crime there is no way to actually
     * remove it without changing the underlying reason" - which is why this
     * category is the fourth thing the city builds for itself and not the
     * answer to the other three.
     *
     * City-built and city-paid, like healthcare and education. See Crime and
     * SafetyType.
     *
     * ON THE END, for the fourth time: ordinal() is a saved key. The slot
     * after this one is BUSINESS_SERVICES.
     */
    SAFETY,

    /**
     * Contact centres, shared-services centres, engineering offices.
     *
     * The first category in the game whose buildings are PRIVATE and whose
     * customer is FOREIGN. Everything else a business builds here sells to the
     * city - shops to households, homes to tenants, materials to the builders -
     * or sells a physical good dug out of the city's own ground. These sell a
     * month of somebody's work to somebody who is not here, which is the only
     * kind of job creation that does not need the city to be bigger first.
     *
     * Jerus, 2026-09-12, on why the city plateaus: every job-creating sector is
     * either a domestic service whose demand IS the population - so it scales
     * with people and cannot lead them - or the one ore-to-steel chain, which
     * geology gates. Once both are saturated nobody here can pay for what
     * another worker would produce. There are exactly three ways out: sell
     * something to foreigners, replace an import, or have the government employ
     * people. This is the first.
     *
     * Private, investor-built and taxed like any other business - it is NOT in
     * the city-pays group with healthcare, education and safety.
     *
     * ON THE END, for the fifth time: ordinal() is a saved key.
     */
    BUSINESS_SERVICES,

    /**
     * Fields, orchards and glass. The tenth sector's, and the first category in
     * this game whose cost is almost entirely the GROUND it stands on.
     *
     * Its own row rather than filed under Industrial, which is where a
     * fabrication shop went this morning, because the thing a player has to
     * understand about a farm is the one thing it does not share with a
     * factory: a factory competes with housing for a LOT, and a farm competes
     * with the whole neighbourhood. A Mixed Farm is twenty-four city blocks,
     * against a Steel Foundry's nine tenths of one. Putting that on the same
     * shelf would hide the only decision in it.
     *
     * Private, investor-built and taxed like any other business - but the rate
     * it is taxed at is the player's, and it is the dial that decides whether
     * the fields survive the city reaching them. See TaxPolicy.FARMLAND_RELIEF.
     *
     * ON THE END, for the sixth time: ordinal() is a saved key, and slipping a
     * constant in above an existing one would hand every old save's industrial
     * tax bill to whoever now holds that ordinal.
     */
    AGRICULTURE,

    /**
     * Freight rail. Lines, yards and the terminals at the end of them.
     *
     * ITS OWN CATEGORY BECAUSE IT IS PRIVATE AND THE ROADS ARE NOT. Every
     * other thing that carries something in this game is INFRASTRUCTURE - the
     * city builds it, the city pays for it, and the city decides. Jerus drew
     * the line somewhere else for rail: "you build roads obviously but rail is
     * its own sector... it wants and will do everything possible to stay
     * profitable and maximize profits." So a rail line is financed by the
     * investor against a business case, like a steel mill, and if the freight
     * does not pay it does not get built.
     *
     * AND THE CITY IS NOT STUCK WITHOUT ONE. "no at first, rail doesnt even
     * build, everything is exported by truck, which is obviously more
     * expensive and road demanding." A founding city trades on lorries at the
     * price the world has always charged; rail is what makes that cheaper. See
     * sectors.Rail and GoodsMarket.setFreightFactor.
     *
     * ON THE END, for the seventh time, and the reason has not changed since
     * INFRASTRUCTURE: the saved per-sector property tax and interest arrays
     * are indexed by ordinal(), so a constant slipped in above an existing one
     * hands every old save's industrial tax bill to whoever now holds that
     * ordinal.
     */
    RAIL,

    /**
     * The automobile industry. Assembly plants, commercial vehicle plants and
     * the works that builds locomotives.
     *
     * ITS OWN CATEGORY BECAUSE IT IS THE TOP OF THE CHAIN, and a player needs
     * to see that it is there before they can plan for it. Every other private
     * category is one link - a mine digs, a mill smelts, a shop fabricates -
     * and this is the only one that takes TWO manufactured inputs and turns
     * them into something a person drives away in. Filing it under Industrial
     * beside the foundries would hide the one fact about it that matters:
     * you cannot have it until you have the three links underneath it.
     *
     * ON THE END, for the eighth time, and the reason has not changed: the
     * saved per-sector property tax and interest arrays are indexed by
     * ordinal(), so a constant slipped in above an existing one hands every
     * old save's bill to whoever now holds that ordinal.
     */
    AUTOMOTIVE,

    /**
     * ...and for the ninth time, on the end. See above.
     *
     * The shops that sell a city its watches. They make nothing: what they
     * have is COVERAGE - customers they can serve in a month - and that is the
     * scarce thing, because the world has no shortage of watches and this city
     * has a shortage of counters to sell them over.
     */
    LUXURY,

    /**
     * ...and for the tenth time, on the end. See above.
     *
     * The kitchens. Coverage here is MEALS a month rather than customers a
     * month, because a person eats ninety of them and buys one basket - and
     * the distinction matters the moment anything divides one by the other.
     */
    HOSPITALITY

}
