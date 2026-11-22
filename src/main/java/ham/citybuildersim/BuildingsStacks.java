package ham.citybuildersim;

/**
 * One building type in the city: how many stand, how many are on site, and
 * the progress, material and contract its sites carry.
 *
 * @author Jerus
 */
public class BuildingsStacks {

    private BuildingsTemplate template;
    private long quantity;
    private long underConstruction;

    /** Finished in the most recent advanceConstruction() call. Not saved: monthly. */
    private long lastFinished;

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

    public void startConstruction(long n) {
        startConstruction(n, null);
    }

    /**
     * ...and the order's fitout with it (R1e; Fitout): an order a city founded the 1900 way chose goes on site as a lot of its own,
     * behind the lots already there, and keeps its set and its points until it finishes. A fitout no one chose (the whole template,
     * a city founded the default way), or none, adds no lot: those buildings are loose (see THE ORDERS' EQUIPMENT ON SITE).
     */
    public void startConstruction(long n, Fitout fit) {
        underConstruction += n;
        materialsOwed += template.getConstructionMaterials() * (double) n;
        if (n > 0 && fit != null && fit.isChosen() && fit.template() == template) addLot(n, fit);
    }

    /**
     * A foothill site's preparation, owed as work (ET2; SitePreparation): the stack owes `points` more before its
     * first building finishes, which is progress below nothing - advanceConstruction() reads it as
     * owedPoints = on site x points - progress, and the crews' output pays it down before any building gains a
     * point. Every reader of the progress takes it as the work done, so a negative figure is work still owed
     * (BuildingManager's owed sums; resumeShell() keeps it through a shell). Nothing for no points.
     */
    void addPreparation(double points) {
        if (points > 0) constructionProgress -= points;
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
        // ...and its lots go with it (R1e): the caller reads them first (getLots()), for the shell to keep.
        lots.clear();
        return out;
    }

    /**
     * ...and a shell put back on site (0.7.22): its buildings, the work in
     * them and the material they still owe, beside whatever is on site
     * already. The new order's contract is booked apart (bookContract()).
     */
    void resumeShell(long buildings, double progress, double owedUnits) {
        resumeShell(buildings, progress, owedUnits, null);
    }

    /** ...with the lots it was stopped with (R1e): back on site behind what is there, each with its set; a shell with none comes back loose. */
    void resumeShell(long buildings, double progress, double owedUnits, java.util.List<Lot> stopped) {
        underConstruction += Math.max(0, buildings);
        // ...a shell cancelled before its site's preparation was done holds it owed, progress below nothing (addPreparation()): it goes back as owed.
        constructionProgress += progress;
        materialsOwed += Math.max(0, owedUnits);
        if (stopped != null) for (Lot lot : stopped) if (lot != null && lot.buildings > 0) addLot(lot.buildings, lot.fitOf(template));
        trimLots();
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

    /*
     * ===== THE UNFITTED (R1, 2026-11-05; Equipment, EquipmentForms, docs/queue/R1-notes.md call 4) =====
     *
     * A stack is a number of buildings of one template with no building of its own, so the state of a piece of equipment on it is a count: how
     * many of its buildings are still WITHOUT the piece, by the piece's id, never more than stand. Empty in every played city (a building that
     * finishes comes fitted: today's way to run), and EquipmentForms sets each 1900 form's share from the counts. A building scrapped takes an
     * unfitted one first, the older plant, so a count never outlives the buildings it counts. Saved, by template id, only where a stack has any
     * (BuildingManager.getUnfittedById(); DataSave.unfittedById). A sorted map, so that anything that walks it walks it in one order.
     */
    private final java.util.TreeMap<String, Long> unfitted = new java.util.TreeMap<>();

    /** How many of this stack's buildings are still without the piece of equipment of this id: 0 for a piece none lacks. */
    public long getUnfitted(String id) {
        Long n = id == null ? null : unfitted.get(id);
        return n == null ? 0 : n;
    }

    /** Sets how many of this stack's buildings are without a piece: clamped to 0..quantity, and 0 takes the piece off the count. */
    public void setUnfitted(String id, long n) {
        if (id == null) return;
        long c = Math.max(0, Math.min(n, quantity));
        if (c == 0) unfitted.remove(id);
        else unfitted.put(id, c);
    }

    /** Every piece some of this stack's buildings lack, with how many, in the pieces' id order; unmodifiable, and empty for a stack that is fully fitted. For the save. */
    public java.util.Map<String, Long> getUnfittedView() {
        return java.util.Collections.unmodifiableMap(unfitted);
    }

    /** Whether any of this stack's buildings lacks any piece. */
    public boolean hasUnfitted() {
        return !unfitted.isEmpty();
    }

    private java.util.function.Consumer<BuildingsStacks> onScrap = s -> { };

    /** The city's fitting queue hears every removal, retirement and a harness's immediate removal included, after the counts are lowered (R1d). */
    void setOnScrap(java.util.function.Consumer<BuildingsStacks> listener) { onScrap = listener; }

    /** After the quantity fell from `before`: the buildings scrapped were the unfitted ones first (the older plants), and no count stays above what stands. */
    private void lowerUnfitted(long before) {
        long removed = before - quantity;
        java.util.Iterator<java.util.Map.Entry<String, Long>> it = unfitted.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry<String, Long> e = it.next();
            long n = Math.min(Math.max(0, e.getValue() - Math.max(0, removed)), quantity);
            if (n <= 0) it.remove();
            else e.setValue(n);
        }
        // ...and then the fittings on site, which may now have fewer unfitted buildings than jobs (Fittings.scrapped()).
        onScrap.accept(this);
    }

    /*
     * ===== THE ORDERS' EQUIPMENT ON SITE (R1e, 2026-11-19; Fitout, docs/queue/R1e.md) =====
     *
     * The buildings on site are one pool of progress, but not of what they come with: each order a city founded the 1900 way
     * placed is a LOT, its buildings and the pieces its quote chose them without, behind the lots before it. A lot's building
     * owes its fitout's points (Fitout.points(): the template's less the work of the pieces left off), and the site finishes
     * off the front, first placed first finished, so each order delivers the set it was priced on and the finish records it
     * (getLastFinishedWithout(); Game.fitWhatFinished()). A building on site with no lot is LOOSE: placed by no order that chose
     * (every building of a city founded the default way, a site a harness put up, a save from before R1e); the loose are the
     * front of the site, owe the template's points and come as the finish decides, as before. Saved by template id, only where
     * a stack has any (BuildingManager.getLotsById(); DataSave.lotsById); a shell keeps the lots it was stopped with.
     */

    /** One order's buildings on site and the pieces they come without (R1e): plain fields, so the save and a shell carry it. */
    public static final class Lot {
        /** How many of the order's buildings are still on site. */
        public long buildings;
        /** The pieces they come without, by id; empty for every piece. */
        public java.util.List<String> without;
        /** The fitout these read as on this stack's template; not saved (Gson skips a transient field), read again from the ids. */
        private transient Fitout fit;

        public Lot() { }

        Lot(long buildings, Fitout fit) {
            this.buildings = buildings;
            this.without = new java.util.ArrayList<>(fit.without());
            this.fit = fit;
        }

        /** Its fitout on `t`: the ids it was saved with, those the template no longer carries dropped (Fitout.without()). */
        public Fitout fitOf(BuildingsTemplate t) {
            if (fit == null || fit.template() != t) fit = Fitout.without(t, without);
            return fit;
        }

        /** A copy that shares nothing with this one: a shell's, the save's. */
        public Lot copy() {
            Lot c = new Lot();
            c.buildings = buildings;
            c.without = without == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(without);
            c.fit = fit;
            return c;
        }
    }

    private final java.util.ArrayList<Lot> lots = new java.util.ArrayList<>();

    /** Finished in the most recent advanceConstruction() call with no lot: loose, theirs to be decided at the finish. Not saved: monthly. */
    private long lastFinishedLoose;
    /** ...and of those that finished in lots, how many came without each piece, by id. Not saved: monthly. */
    private final java.util.TreeMap<String, Long> lastFinishedWithout = new java.util.TreeMap<>();

    /** A lot of n behind the others; one with the same set as the last joins it, which finishes the same buildings in the same order. */
    private void addLot(long n, Fitout fit) {
        Lot last = lots.isEmpty() ? null : lots.get(lots.size() - 1);
        if (last != null && last.fitOf(template).without().equals(fit.without())) last.buildings += n;
        else lots.add(new Lot(n, fit));
    }

    /** No lot holds more buildings than are on site: the newest give way first (a site cut by hand, a save's count). */
    private void trimLots() {
        long over = lotted() - Math.max(0, underConstruction);
        for (int i = lots.size() - 1; i >= 0 && over > 0; i--) {
            Lot lot = lots.get(i);
            long cut = Math.min(over, lot.buildings);
            lot.buildings -= cut;
            over -= cut;
            if (lot.buildings <= 0) lots.remove(i);
        }
    }

    private long lotted() {
        long n = 0;
        for (Lot lot : lots) n += lot.buildings;
        return n;
    }

    /** The buildings on site with no lot, the front of the site: every building on site where there is no lot. */
    public long getLooseOnSite() {
        return Math.max(0, underConstruction - lotted());
    }

    /** The lots on site, front first: copies, for the save and a shell. Empty with none. */
    public java.util.List<Lot> getLots() {
        java.util.List<Lot> out = new java.util.ArrayList<>(lots.size());
        for (Lot lot : lots) out.add(lot.copy());
        return out;
    }

    /** Puts a save's lots back, front first, after the site's count (BuildingManager.restoreLots()): none, or empty, restores none. */
    public void restoreLots(java.util.List<Lot> saved) {
        lots.clear();
        if (saved != null) for (Lot lot : saved) if (lot != null && lot.buildings > 0) addLot(lot.buildings, lot.fitOf(template));
        trimLots();
    }

    /**
     * The points every building on site takes, before any progress: the loose at the template's points, each lot at its
     * fitout's. With no lot, the site's buildings times the template's points, to the bit.
     */
    public double pointsOnSite() {
        double points = getLooseOnSite() * (double) template.getConstructionPoints();
        for (Lot lot : lots) points += lot.buildings * lot.fitOf(template).points();
        return points;
    }

    /** ...and the first k of them, front first: what the site owes before its k-th building finishes, progress aside. */
    public double pointsOfFirst(long k) {
        long left = Math.max(0, Math.min(k, underConstruction));
        long loose = Math.min(left, getLooseOnSite());
        double points = loose * (double) template.getConstructionPoints();
        left -= loose;
        for (Lot lot : lots) {
            if (left <= 0) break;
            long n = Math.min(left, lot.buildings);
            points += n * lot.fitOf(template).points();
            left -= n;
        }
        return points;
    }

    /** Points still owed on site: every building's points less the progress made (a foothill's preparation owed reads as more). */
    public double owedPoints() {
        return pointsOnSite() - constructionProgress;
    }

    /** The points the building at the front of the site takes: a loose one's the template's, else the first lot's. */
    private double nextPoints() {
        if (getLooseOnSite() > 0 || lots.isEmpty()) return template.getConstructionPoints();
        return lots.get(0).fitOf(template).points();
    }

    /** Takes k finished buildings off the front, the loose first, and counts what they come without. While they still count on site. */
    private void finishFront(long k) {
        long left = Math.max(0, k);
        long loose = Math.min(left, getLooseOnSite());
        lastFinishedLoose += loose;
        left -= loose;
        while (left > 0 && !lots.isEmpty()) {
            Lot lot = lots.get(0);
            long n = Math.min(left, lot.buildings);
            for (String id : lot.fitOf(template).without()) lastFinishedWithout.merge(id, n, Long::sum);
            lot.buildings -= n;
            left -= n;
            if (lot.buildings <= 0) lots.remove(0);
        }
    }

    /** Of the buildings the most recent advanceConstruction() call finished, those with no lot: their pieces are decided at the finish. */
    public long getLastFinishedLoose() {
        return lastFinishedLoose;
    }

    /** ...and of those it finished in lots, how many came without each piece, by id: unmodifiable, empty when every one came with all. */
    public java.util.Map<String, Long> getLastFinishedWithout() {
        return java.util.Collections.unmodifiableMap(lastFinishedWithout);
    }

    // for immediate add
    private java.util.function.LongPredicate mayRemove = n -> true;
    private java.util.function.Consumer<BuildingsStacks> physicalChanged = s -> { };

    /** Inventory hooks protect conveyed titles; stacks remain physical authority. */
    void bindHousing(java.util.function.LongPredicate guard,
                     java.util.function.Consumer<BuildingsStacks> changed) {
        mayRemove = guard; physicalChanged = changed;
    }

    public void addQuantity(long n) {
        if (n < 0 && !mayRemove.test(-n)) throw new IllegalStateException("Housing titles prevent removal");
        long before = quantity;
        quantity += n;
        if (quantity < before) lowerUnfitted(before);
        physicalChanged.accept(this);
    }

    public void advanceConstruction(double constructionOutput) {
        materialsDue = 0;
        revenueDue = 0;
        lastFinished = 0;
        lastApplied = 0;
        dues.clear();
        lastFinishedLoose = 0;
        lastFinishedWithout.clear();

        if (underConstruction == 0) {
            lots.clear();
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

        long finished = 0;
        // ...each building its own points: a lot's are its fitout's (R1e), a loose building's the template's.
        double owedPoints = pointsOnSite() - constructionProgress;

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
            finishFront(underConstruction);
            underConstruction = 0;
            remainingOutput = 0;
        }
        // ...off the front, first placed first finished: the loose, then the lots in the order they came (R1e).
        while (underConstruction > 0 && remainingOutput >= nextPoints()) {
            remainingOutput -= nextPoints();
            finishFront(1);
            finished++;
            underConstruction--;
        }

        long startedConstruction = underConstruction + finished;
        quantity += finished;
        if (finished > 0) physicalChanged.accept(this);
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
                    (pointsOnSite() - remainingOutput)
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
    public long getLastFinished() {
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
        double cap = Math.max(0, pointsOnSite());
        double over = constructionProgress - cap;
        if (!(over > 0)) return 0;
        constructionProgress = cap;
        return over;
    }

    //getters
    public long getTotalJobs(JobType type) {
        return (long) template.getJobs(type) * quantity;
    }

    public long getQuantity() {
        return quantity;
    }

    /** Scraps finished buildings. Floors at zero rather than going negative. The unfitted ones go first (see the unfitted counts). */
    public void removeQuantity(long amount) {
        if (amount < 0) throw new IllegalArgumentException("Negative removal");
        if (!mayRemove.test(Math.min(quantity, amount))) throw new IllegalStateException("Housing titles prevent removal");
        long before = quantity;
        quantity = Math.max(quantity - amount, 0);
        if (quantity < before) lowerUnfitted(before);
        physicalChanged.accept(this);
    }

    public long getUnderConstruction() {
        return underConstruction;
    }

    public String getName() {
        return template.name;
    }

    public double getConstructionProgress() {
        return constructionProgress;
    }

    public void increaseQuantity(long amount) {
        long before = quantity;
        this.quantity += amount;
        if (quantity < before) lowerUnfitted(before);
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

    public void setUnderConstruction(long quantity) {
        this.underConstruction = quantity;
        trimLots();
    }

    public void setMaterialsOwed(double units) {
        this.materialsOwed = Math.max(0, units);
    }

    /** Material that reached the sites without a purchase - the city's yard, on the order day. */
    public void deliverMaterials(double units) {
        this.materialsOwed = Math.max(0, materialsOwed - Math.max(0, units));
    }

}
