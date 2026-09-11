package ham.citybuildersim;

/**
 *
 * @author Jerus
 */
public class BuildingsStacks {

    private BuildingsTemplate template;
    private int quantity;
    private int underConstruction;

    /** Finished in the most recent advanceConstruction() call. Not saved: monthly. */
    private int lastFinished;
    private double constructionProgress;

    /**
     * Units of material the sites still have to take. Saved.
     *
     * DRAWN AS THE WORK IS DONE, since 2026-09-11. An order used to take its
     * whole material the day it was placed - the yard's for free, the
     * plant's at the market price, the world's for the rest - which was the
     * right shape while the yard was the only local source. With a materials
     * plant selling into a market it was the wrong one: the builders' demand
     * arrived in lumps the size of an order, a founding city's houses drew
     * three thousand units in one month and nothing for a year, and a plant
     * sized to the average idled nine months in ten with a full shed while
     * the next order imported what it could not hold. The first plant ever
     * built sold nothing for six months, was scrapped for it, and took the
     * bank down with its loan. Builders buy material as they build; the
     * order is invoiced up front, as it always was, and the crews draw on
     * the yard, the plant and the world month by month in proportion to the
     * work they deliver. See BuildingManager.takeMaterialsDue().
     */
    private double materialsOwed;

    /** What this month's work drew on. Set by advanceConstruction(), read once. Not saved. */
    private double materialsDue;

    /**
     * What the sites are still owed FOR, in money: the builders' contract
     * for the work on site, recognised as the work is done. Saved.
     *
     * PER STACK, since 2026-09-11. The builders kept one order book for the
     * whole city and earned it by the point - so a road, which is a little
     * work and a lot of material, had its price earned on the houses' points
     * ahead of it, and when the roads came up last the crews bought two
     * hundred units a month of material against four hundred dollars of
     * revenue. A contract is earned by its own work; see materialsOwed for
     * the material's half of the same rule.
     */
    private double contractValue;

    /** ...and what this month's work earned of it. Set by advanceConstruction(), read once. Not saved. */
    private double revenueDue;

    /**
     * @param initialQuantity buildings that already exist, finished and standing
     *
     * The parameter used to be accepted and then thrown away - the body assigned
     * quantity = 0 regardless. Every caller passes 0, so honouring it changes
     * nothing today; the point is that the next one to pass 5 will get 5 rather
     * than silently losing five buildings.
     */
    public BuildingsStacks(BuildingsTemplate template, int initialQuantity) {
        this.template = template;
        this.quantity = Math.max(0, initialQuantity);
        this.underConstruction = 0;
    }

    public void startConstruction(int n) {
        underConstruction += n;
        materialsOwed += template.getConstructionMaterials() * (double) n;
    }

    /** The builders' price for an order just placed on this stack. See contractValue. */
    public void bookContract(double amount) {
        contractValue += Math.max(0, amount);
    }

    // for immediate add
    public void addQuantity(int n) {
        quantity += n;
    }

    public void advanceConstruction(double constructionOutput) {
        materialsDue = 0;
        revenueDue = 0;
        lastFinished = 0;

        if (underConstruction == 0) {
            // Nothing on site: a residue here is rounding, not an order. The
            // money is not dropped - the builders were paid it - it is earned.
            materialsOwed = 0;
            revenueDue = contractValue;
            contractValue = 0;
            return;
        }

        int finished = 0;
        double owedPoints = underConstruction * (double) template.getConstructionPoints() - constructionProgress;
        double remainingOutput = constructionOutput + constructionProgress;
        constructionProgress = 0;

        while ((remainingOutput >= template.getConstructionPoints() && (underConstruction > 0))) {
            finished++;
            underConstruction--;
            remainingOutput -= template.getConstructionPoints();
        }

        int startedConstruction = underConstruction + finished;
        quantity += finished;
        constructionProgress = remainingOutput;
        lastFinished = finished;

        // The material and the money, in proportion to the work - and all of
        // what is left when the site empties, so nothing is owed on a
        // finished building.
        if (underConstruction == 0) {
            materialsDue = materialsOwed;
            revenueDue = contractValue;
        } else if (owedPoints > 0) {
            double done = Math.min(Math.max(0, constructionOutput), owedPoints);
            materialsDue = Math.min(materialsOwed, materialsOwed * done / owedPoints);
            revenueDue = Math.min(contractValue, contractValue * done / owedPoints);
        }
        materialsOwed = Math.max(0, materialsOwed - materialsDue);
        contractValue = Math.max(0, contractValue - revenueDue);

        // Print how many finished
        System.out.print(finished + "/" + startedConstruction + " " + template.name + "(s) finished construction.");

        // If there are still buildings under construction, calculate months to finish
        if (underConstruction > 0) {

            /*
             * constructionOutput can genuinely be zero, and dividing by it used
             * to print "2147483647 month(s)" - (int) of positive infinity.
             *
             * It got easier to reach with every pass. The site's share is the
             * city's construction capacity scaled by the sector's labour fill
             * rate and now by road throughput as well, and either of those can
             * round the whole thing to nothing: a city whose builders have no
             * staff, or one so congested that nothing reaches the site, really
             * is making no progress. "Stalled" is the honest word for that, and
             * it tells the player something a nonsense number does not.
             */
            if (constructionOutput <= 0) {
                System.out.println(" Stalled - no construction capacity.");
                return;
            }

            double monthsLeft = Math.ceil(
                    (underConstruction * template.getConstructionPoints() - remainingOutput)
                            / constructionOutput);
            System.out.println(" " + (int) monthsLeft + " month(s).");
        }
    }

    /**
     * How many finished in the most recent advanceConstruction() call.
     *
     * Reset at the top of that method, so it means "this month" for as long as
     * the month lasts and reads zero for a stack that was asked and had nothing
     * to finish. Deliberately not accumulated: the build log wants the month's
     * completions, and a running total here would need clearing by someone,
     * which is the shape of bug this codebase keeps finding in monthly state.
     */
    public int getLastFinished() {
        return lastFinished;
    }

    //getters
    public int getTotalJobs(JobType type) {
        return template.getJobs(type) * quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    /** Scraps finished buildings. Floors at zero rather than going negative. */
    public void removeQuantity(int amount) {
        quantity = Math.max(quantity - amount, 0);
    }

    public int getUnderConstruction() {
        return underConstruction;
    }

    public String getName() {
        return template.name;
    }

    public double getConstructionProgress() {
        return constructionProgress;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public BuildingsTemplate getBuilding() {
        return this.template;
    }

    public boolean getIfUnderConstruction() {
        // Was a getter that ASSIGNED a field on every read, which made an
        // innocuous-looking call a mutation. The field it maintained was never
        // read anywhere else, so it is gone; this answers the question directly.
        return underConstruction > 0;
    }

    /** Units the sites still have to draw. */
    public double getMaterialsOwed() {
        return materialsOwed;
    }

    /** Units this month's work drew on, per the last advanceConstruction() call. */
    public double getMaterialsDue() {
        return materialsDue;
    }

    /** The builders' contract still on site. */
    public double getContractValue() {
        return contractValue;
    }

    /** What this month's work earned of it, per the last advanceConstruction() call. */
    public double getRevenueDue() {
        return revenueDue;
    }

    public void setContractValue(double amount) {
        this.contractValue = Math.max(0, amount);
    }

    /** A reform: the contract is money. The material and the points are not. */
    public void redenominate(double scale) {
        contractValue *= scale;
    }

    //setters
    public void setConstructionProgress(double progress) {
        this.constructionProgress = progress;
    }

    public void setUnderConstruction(int quantity) {
        this.underConstruction = quantity;
    }

    public void setMaterialsOwed(double units) {
        this.materialsOwed = Math.max(0, units);
    }

    /** Material that reached the sites without a purchase - the city's yard, on the order day. */
    public void deliverMaterials(double units) {
        this.materialsOwed = Math.max(0, materialsOwed - Math.max(0, units));
    }

}
