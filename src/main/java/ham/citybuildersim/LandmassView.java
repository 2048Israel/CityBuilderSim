package ham.citybuildersim;

/**
 * What the screens and the investors' words say of the city's landmasses: pure functions of the model's figures, so that
 * the words are written in one place and a harness can rebuild them from getters (ET5, 2026-10-25).
 *
 * WHY THIS EXISTS. A screen must not recompute a model figure, and a refusal the investors write ("no land - needs ...")
 * and the Build card's gate must say the same thing about ground across the water in the same words. LandClassView is
 * the shape: a pure class beside the model that the interface calls. Slice A of ET5 puts here the two things the model's
 * own words and the pickers need - the investors' clause and the ISLAND tag's share; the screens' heads, rows, chips and
 * link sentences join them with the interface.
 *
 * @author Jerus
 */
public final class LandmassView {

    private LandmassView() { }

    /** The share of an offer's dry ground that must lie on landmasses the city has not linked for the land office to tag the offer ISLAND (before BEST VALUE and the rest): half. */
    public static final double ISLAND_TAG_SHARE = 0.5;

    /** Whether the land office tags an offer ISLAND: it has dry ground on a landmass the city has not linked, and at least ISLAND_TAG_SHARE of its dry ground is such. False for every offer of home's ground and on every generated world. */
    public static boolean islandTagged(LandParcel p) {
        return p.getUnlinkedDryKm2() > 0 && p.getUnlinkedDryKm2() >= ISLAND_TAG_SHARE * p.getDryKm2();
    }

    /**
     * The clause the investors' land refusals end with when the city owns ground it cannot build on: "12.0 km² is free on
     * Island 2, which no port links". `freeSqFt` is LandManager.getUnlinkedFreeSqFt(), `names` LandManager.getUnlinkedNames().
     * BusinessInvestment.landReason() and Game's "Could not build" words join it to theirs with "; ".
     */
    public static String unlinkedWords(double freeSqFt, String names) {
        return LandManager.areaWords(freeSqFt) + " is free on " + names + ", which no port links";
    }
}
