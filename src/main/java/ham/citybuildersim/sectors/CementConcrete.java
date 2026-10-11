package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.Good;
import ham.citybuildersim.Sectors;

/**
 * Kilns and cement plants - Cement & Concrete (0.7.131, batch I7;
 * runs/spec-materials.md 2.1, 2.12 and 5's I7 row; the study's 6): stone's
 * processing industry, on the end of Sectors.KEYS after Quarrying (Jerus's
 * Q21).
 *
 * WHY. Limestone is what the kilns burn: into quicklime, the Oxygen Steel
 * Shop's flux, which the city imported since the integrated route (0.7.123,
 * batch I4), and with aggregate and coal into cement. One sector owns every
 * building that turns stone into something else and builds whichever earns
 * most on its whole cost (the study's 1.1 and 1.2, Jerus's A) -
 * ProcessingSector's rule, so this class is a declaration: what it makes,
 * what it uses, and its rows.
 *
 * THE THREE BUILDINGS (the study's 6.4; ids 118 to 120):
 *   - the CEMENT PLANT, 25,000 t of cement a month from 30,000 t of
 *     limestone, 3,750 t of aggregate and 3,350 t of thermal coal;
 *   - the LARGE CEMENT PLANT, five times that, 125,000 t;
 *   - the LIME KILN, 18,000 t of quicklime from 32,400 t of limestone and
 *     3,600 t of thermal coal.
 * The Ready-Mix Plant and the Asphalt Plant, CONCRETE and ASPHALT, wait for
 * the construction upgrade (Jerus's Q18).
 *
 * WHAT IT PAYS (the study's 6.5): a cement plant 0.1-0.3% a month exporting
 * and 1.5-1.9% replacing imports - cement is "really regional", its band
 * wide - so the rule builds one only where the city buys cement, which
 * nothing here does until the construction upgrade; the lime kiln 1.6% on
 * limestone at its export price and 5.7% replacing imported lime. Its
 * limestone is weighed at the export price for as much as the quarries
 * carried into aggregate (ProcessingSector.values(); sectors.Quarrying, THE
 * CARRY), the import price past it; its coal is Coal Mining's where a city
 * digs it (batch I6), else imported.
 *
 * Who buys: Iron & Steel's oxygen shop the lime; the world the cement and the
 * rest of the lime, until the construction upgrade draws cement for the
 * builders (spec 2.14).
 */
public final class CementConcrete extends ProcessingSector {

    /** The Cement Plant's permanent id in buildings.json (0.7.131): 25,000 t of cement a month. */
    public static final int CEMENT_PLANT = 118;

    /** The Large Cement Plant's (0.7.131): 125,000 t of cement a month - Edison's long kilns, 1902. */
    public static final int LARGE_CEMENT_PLANT = 119;

    /** The Lime Kiln's (0.7.131): 18,000 t of quicklime a month. */
    public static final int LIME_KILN = 120;

    public CementConcrete() {
        super(Sectors.CEMENT_CONCRETE, "Cement & Concrete", BuildingType.HEAVY_INDUSTRY);
        makes(Good.CEMENT);
        makes(Good.LIME);
        uses(Good.LIMESTONE);
        uses(Good.AGGREGATE);
        uses(Good.THERMAL_COAL);
        blurb("Burns limestone in kilns: into quicklime for the steelworks, and with aggregate "
                + "and coal into cement. Builds whichever plant pays most on its whole cost - "
                + "a lime kiln where the city's limestone is cheap or it imports lime, a cement "
                + "plant where the city buys cement.");
    }
}
