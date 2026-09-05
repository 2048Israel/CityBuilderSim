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
     * HeavyIndustryHandler.
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
     * different things near each other. See MiningHandler.
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
    HEALTHCARE

}
