package ham.citybuildersim;

/**
 *
 * @author Jerus
 */
public class BuildingsStacks {

    private BuildingsTemplate template;
    private int quantity;
    private int underConstruction;
    private double constructionProgress;
    private int constructionMaterialCost;

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
        constructionMaterialCost = template.getConstructionMaterials() * n;
    }

    // for immediate add
    public void addQuantity(int n) {
        quantity += n;
    }

    public void advanceConstruction(double constructionOutput) {
        constructionMaterialCost = 0;

        if (underConstruction == 0) {
            return;
        }

        int finished = 0;
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

    public int getConstructionMaterialCost() {
        return constructionMaterialCost;
    }

    //setters
    public void setConstructionProgress(double progress) {
        this.constructionProgress = progress;
    }

    public void setUnderConstruction(int quantity) {
        this.underConstruction = quantity;
    }

}
