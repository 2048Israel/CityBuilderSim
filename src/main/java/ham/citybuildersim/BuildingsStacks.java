package ham.citybuildersim;

/**
 * One building type in the city: how many stand, how many are on site, and
 * the progress, material and contract its sites carry.
 *
 * @author Jerus
 */
public class BuildingsStacks {

    private BuildingsTemplate template;
    private int quantity;
    private int underConstruction;

    /** Finished in the most recent advanceConstruction() call. Not saved: monthly. */
    private int lastFinished;

    /** Points the most recent advanceConstruction() call put into buildings: never more than the stack owed (0.7.17). Not saved: monthly. */
    private double lastApplied;
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

    /*
     * WHO PAID FOR THE CONTRACT, AND FOR THE MATERIAL IN IT (0.7.19). Jerus
     * chose "Builders' prices keep up": "...materials are paid at the price
     * when they're used, and sales tax is in the builder's quote." An order
     * still pays its whole quote up front - the material in it priced at the
     * day's rates, the ALLOWANCE - and each month the crews draw, whoever
     * placed the order pays the difference between what the material drawn
     * cost that month and its share of the allowance, or is refunded it: an
     * escalation clause (Game, MATERIAL AT THE PRICE WHEN IT IS USED). And a
     * business that claims the sales tax back claims it on what it paid, as
     * the work is billed (EconomyManager.settleSalesTax()). So a stack keeps,
     * per payer, the part of its contract that payer placed: what is left of
     * the price, the material units still to be drawn for it, and the
     * allowance still in it for them. Saved (Game's load path; an older save's
     * contracts are read as its template owner's - see Game, OLD CONTRACTS).
     * And the share of the tax on it the payer gets back (revised 0.7.19): a
     * business's credit or a landlord's rebate on a new rental home
     * (EconomyManager.taxRecoveredShare()).
     */
    public static final class Contract {
        /** "City", or a sector's key: whoever placed the order and pays its escalation. */
        public final String payer;
        /** Whether the payer gets any of the sales tax on it back - a business building for its own taxable trade, or a landlord's rebate. */
        public boolean creditable;
        /** ...and what share of it: one for a credit or a purpose-built rental's rebate, less for a smaller rebate. */
        double recovered;
        double value, units, allowance;
        Contract(String payer, boolean creditable) { this(payer, creditable ? 1 : 0); }
        Contract(String payer, double recovered) {
            this.payer = payer;
            this.recovered = Math.max(0, Math.min(1, recovered));
            this.creditable = this.recovered > 0;
        }
        public double getValue()     { return value; }
        public double getUnits()     { return units; }
        public double getAllowance() { return allowance; }
        public double getRecovered() { return recovered; }
    }

    /** ...and one payer's share of this month's work: the contract it earned, the units drawn for it and the allowance they took. Set by advanceConstruction(), read once. */
    public static final class Due {
        public final String payer;
        public final boolean creditable;
        /** The share of the tax on it the payer gets back. See Contract. */
        public final double recovered;
        public final double revenue, units, allowance;
        Due(Contract c, double revenue, double units, double allowance) {
            this.payer = c.payer; this.creditable = c.creditable; this.recovered = c.recovered;
            this.revenue = revenue; this.units = units; this.allowance = allowance;
        }
    }

    private final java.util.LinkedHashMap<String, Contract> contracts = new java.util.LinkedHashMap<>();
    private final java.util.List<Due> dues = new java.util.ArrayList<>();

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

    /**
     * ...and who placed it (0.7.19): the price, the material units beyond the
     * yard the sites will draw for it, and the allowance priced into it for
     * them. See Contract.
     */
    public void bookContract(String payer, double recovered, double amount, double units, double allowance) {
        bookContract(amount);
        Contract c = contracts.computeIfAbsent(payer, k -> new Contract(k, recovered));
        // A second order by the same payer on these sites: the share it gets
        // back is the two orders' own, by what each is worth.
        double r = Math.max(0, Math.min(1, recovered)), a = Math.max(0, amount);
        if (c.recovered != r && c.value + a > 0) {
            c.recovered = (c.recovered * c.value + r * a) / (c.value + a);
            c.creditable = c.recovered > 0;
        }
        c.value += Math.max(0, amount);
        c.units += Math.max(0, units);
        c.allowance += Math.max(0, allowance);
    }

    /** Restores one payer's contract from a save, or sets an older save's up (Game, OLD CONTRACTS). The stack's own totals are restored apart. */
    public void restoreContract(String payer, double recovered, double value, double units, double allowance) {
        Contract c = contracts.computeIfAbsent(payer, k -> new Contract(k, recovered));
        c.recovered = Math.max(0, Math.min(1, recovered));
        c.creditable = c.recovered > 0;
        c.value = Math.max(0, value);
        c.units = Math.max(0, units);
        c.allowance = Math.max(0, allowance);
    }

    /** The payers' contracts on this stack, in the order they were placed. */
    public java.util.Collection<Contract> getContracts() { return contracts.values(); }

    /**
     * Whether what is on site is the city's alone (0.7.22): buildings on
     * site, and every order on them the city's - the sites the player may
     * reorder, rush and stop (ConstructionControl). A stack shared with an
     * investor's order is not: its progress is one pool and cannot be
     * divided by who paid for which building.
     */
    public boolean isCitysOwn() {
        if (underConstruction <= 0 || contracts.isEmpty()) return false;
        for (Contract c : contracts.values()) if (!"City".equals(c.payer)) return false;
        return true;
    }

    /**
     * Takes everything on site off the stack, for the shell a cancelled
     * order leaves (0.7.22; ConstructionControl, C. CANCEL): its buildings,
     * their progress and the material they still owe come off with it, and
     * so does the city's contract - what is left of it is the refund, and
     * the stack's own book comes down by it.
     *
     * @return {buildings, progress, materials still owed, the city's contract left, its allowance left}
     */
    double[] stopForShell() {
        Contract city = contracts.get("City");
        double value = city == null ? 0 : city.value;
        double allowance = city == null ? 0 : city.allowance;
        double[] out = { underConstruction, constructionProgress, materialsOwed, value, allowance };
        contracts.remove("City");
        contractValue = Math.max(0, contractValue - value);
        underConstruction = 0;
        constructionProgress = 0;
        materialsOwed = 0;
        return out;
    }

    /**
     * ...and a shell put back on site (0.7.22): its buildings, the work in
     * them and the material they still owe, beside whatever is on site
     * already. The new order's contract is booked apart (bookContract()).
     */
    void resumeShell(int buildings, double progress, double owedUnits) {
        underConstruction += Math.max(0, buildings);
        constructionProgress += Math.max(0, progress);
        materialsOwed += Math.max(0, owedUnits);
    }

    /** This month's work, payer by payer - per the last advanceConstruction() call. */
    public java.util.List<Due> getDues() { return dues; }

    /** Each payer's share of the month's work: the same fraction of its contract, its units and its allowance as the stack's work was of what it owed; drawn=false when the work drew no material (nothing on site). */
    private void takeDues(double fraction, boolean drawn) {
        dues.clear();
        java.util.Iterator<Contract> it = contracts.values().iterator();
        while (it.hasNext()) {
            Contract c = it.next();
            double f = Math.max(0, Math.min(1, fraction));
            double v = c.value * f, u = c.units * f, a = c.allowance * f;
            if (f >= 1) { v = c.value; u = c.units; a = c.allowance; }
            c.value -= v; c.units -= u; c.allowance -= a;
            if (v > 0 || u > 0 || a > 0) dues.add(new Due(c, v, drawn ? u : 0, a));
            if (c.value <= 1e-12 && c.units <= 1e-12 && c.allowance <= 1e-12) it.remove();
        }
    }

    // for immediate add
    public void addQuantity(int n) {
        quantity += n;
    }

    public void advanceConstruction(double constructionOutput) {
        materialsDue = 0;
        revenueDue = 0;
        lastFinished = 0;
        lastApplied = 0;
        dues.clear();

        if (underConstruction == 0) {
            // Nothing on site: a residue here is rounding, not an order. The
            // money is not dropped - the builders were paid it - it is earned.
            // ...and the material nobody will draw is refunded to whoever paid
            // its allowance, by the escalation (0.7.19): drawn nothing.
            materialsOwed = 0;
            revenueDue = contractValue;
            contractValue = 0;
            takeDues(1, false);
            // ...and no points are kept for an order nobody has placed
            // (0.7.17): see BuildingManager, EVERY BUILDING GETS THE CREW IT
            // CAN USE.
            constructionProgress = 0;
            return;
        }

        int finished = 0;
        double owedPoints = underConstruction * (double) template.getConstructionPoints() - constructionProgress;

        /*
         * NEVER MORE THAN IT OWES (0.7.17). What this stack is handed past
         * the points its buildings still need is idle output, not progress:
         * it used to be kept, and a one-depot order handed a quarter of the
         * city's sites carried ten thousand points it could never use. The
         * site that is handed all it owes finishes all of it, exactly - not
         * by adding the points back up, which can land a hair short in
         * floating point and hold the last building a month.
         */
        double applied = Math.max(0, Math.min(constructionOutput, Math.max(0, owedPoints)));
        lastApplied = applied;
        double remainingOutput = applied + constructionProgress;
        constructionProgress = 0;

        if (applied >= owedPoints) {
            finished = underConstruction;
            underConstruction = 0;
            remainingOutput = 0;
        }
        while ((remainingOutput >= template.getConstructionPoints() && (underConstruction > 0))) {
            finished++;
            underConstruction--;
            remainingOutput -= template.getConstructionPoints();
        }

        int startedConstruction = underConstruction + finished;
        quantity += finished;
        constructionProgress = underConstruction > 0 ? remainingOutput : 0;
        lastFinished = finished;

        // The material and the money, in proportion to the work - and all of
        // what is left when the site empties, so nothing is owed on a
        // finished building.
        if (underConstruction == 0) {
            materialsDue = materialsOwed;
            revenueDue = contractValue;
            takeDues(1, true);
        } else if (owedPoints > 0) {
            double done = applied;
            materialsDue = Math.min(materialsOwed, materialsOwed * done / owedPoints);
            revenueDue = Math.min(contractValue, contractValue * done / owedPoints);
            takeDues(done / owedPoints, true);
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
            if (applied <= 0) {
                System.out.println(" Stalled - no construction capacity.");
                return;
            }

            double monthsLeft = Math.ceil(
                    (underConstruction * template.getConstructionPoints() - remainingOutput)
                            / applied);
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

    /** Points the most recent advanceConstruction() call put into buildings. */
    public double getLastApplied() {
        return lastApplied;
    }

    /**
     * Drops progress past what the stack owes - all of it with nothing on
     * site - and returns how many points that was. The load path's, for a
     * save from before a stack could not carry them (0.7.17): see
     * BuildingManager.clearBankedProgress(), which says why no money moves.
     */
    public double clearBankedProgress() {
        double cap = Math.max(0, underConstruction * (double) template.getConstructionPoints());
        double over = constructionProgress - cap;
        if (!(over > 0)) return 0;
        constructionProgress = cap;
        return over;
    }

    //getters
    public long getTotalJobs(JobType type) {
        return (long) template.getJobs(type) * quantity;
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
        for (Contract c : contracts.values()) { c.value *= scale; c.allowance *= scale; }
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
