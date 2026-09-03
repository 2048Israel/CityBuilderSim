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
    private boolean ifUnderConstruction;
    private int constructionMaterialCost;

    public BuildingsStacks(BuildingsTemplate template, int initialQuantity) {
        this.template = template;
        this.quantity = 0;
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
            double monthsLeft = Math.ceil((underConstruction * template.getConstructionPoints() - remainingOutput) / constructionOutput);
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
        if (underConstruction > 0) {
            return ifUnderConstruction = true;
        }
        return ifUnderConstruction = false;
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
