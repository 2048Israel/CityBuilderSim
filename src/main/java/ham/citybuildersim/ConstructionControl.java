package ham.citybuildersim;

/**
 * The player's hand on the construction queue (0.7.22): the order the
 * city's own sites are served in, the sites it has put on overtime, the
 * orders it has stopped and the shells they left, the buildings it is
 * pulling down, what it paid to buy a business's buildings to do so, and
 * since 0.7.70 the gravel roads it is paving (F).
 *
 * WHY. Until 0.7.22 an order, once placed, was out of the player's hands:
 * the builders' crews were shared by the rule (BuildingManager, EVERY
 * BUILDING GETS THE CREW IT CAN USE), nothing could be stopped, and nothing
 * the city owned could be taken down. Jerus, on the play-through of 0.7.19,
 * asked for "not only repirotize and cancel but also destroy buildings, like
 * you yourself destroy buildings", and chose the mechanics on 2026-09-30:
 * priority "Both" (a free reorder of the city's own sites, and overtime
 * paid for), cancel "Keep the half-built shell", demolish "City's, plus
 * buy-outs". Each rule below names its source.
 *
 * WHAT THIS CLASS HOLDS: the state, saved whole under one key
 * (DataSave.constructionControl; a format-27 save has none of it and loads
 * with none), and the arithmetic every rule is made of, as statics a
 * harness can hold the game to. BuildingManager applies the crew rules in
 * the month's advance; Game, THE PLAYER'S HAND ON THE QUEUE, moves the
 * money. None of the rules runs unless the player has used it: engaged() is
 * false in a city nobody has touched, and the advance then takes the path
 * it always took, to the bit. (Each stack's run - the page's "done of
 * total" and "paid" - is kept in every city, on every order, and saved with
 * the rest; it is bookkeeping and moves nothing. A paving, F, rides on the
 * Paved Road stack's own advance in every city, engaged or not.)
 *
 * A SITE is what the model keeps on site: one stack per building type
 * ("B" + its template id) - however many orders are on it - and, since
 * this class, each demolition the city has ordered ("D" + its number). The
 * CITY'S sites are the stacks whose every order on site is the city's, and
 * the demolitions. A stack the city shares with an investor's order is not
 * the city's to reorder, rush or stop: its progress is one pool, and the
 * model cannot divide it by who paid for which building.
 */
public final class ConstructionControl {

    /* =====================================================================
       A. PRIORITY: THE CITY'S OWN SITES, IN THE ORDER THE PLAYER SETS

       The month's site output is shared by the crew each building can use,
       water-filled by Bromilow weight (BuildingManager.siteShares()). That
       stays the rule for everyone, and the city's sites together keep
       exactly the share it gives them. When the player has set an order,
       that share - the city's - is split among the city's own sites top
       down: the first takes what it still owes, at most, then the next, and
       so on (BuildingManager.applyPriority()). No other owner's site gains or
       loses a point by it. With no order set the split is the rule's own,
       untouched.

       FREE. An owner deciding which of its own contracts its crews serve
       first is not a change to the contract; nobody is paid for it.

       AN ORDER LASTS AS LONG AS ITS SITES (after the docs pass, which found
       one that never turned itself off). Both, at each month's end
       (afterMonth()): a city site placed while an order is set JOINS IT AT
       THE BOTTOM - between the presses it is timed and served there already
       (effectiveOrder()), and the month's end writes it into the order; and
       once NONE OF THE ORDER'S SITES IS LEFT ON SITE - finished, stopped or
       shared with an investor's order - THE ORDER IS CLEARED, and the city
       sites ordered after it go back to the crews' rule, as with "Back to
       the crews' rule" (clearOrder()).
       ===================================================================== */

    /* =====================================================================
       B. RUSH: PAID OVERTIME ON ONE OF THE CITY'S SITES

       SOURCE: The Business Roundtable, "Scheduled Overtime Effect on
       Construction Projects", Report C-2 (November 1980), Figure 4: a
       50-hour week (five tens) against a 40-hour week, productivity by weeks
       on the schedule - 0.926 for weeks 0-2, 0.90 for 2-4, 0.87 for 4-6, 0.80
       for 6-8, 0.752 for 8-10 and 0.750 beyond. "After working overtime for
       six to eight weeks, labor cost is inflated by 50 percent with the
       productive returns no greater than would be accomplished on a 40-hour
       week"; the report recommends overtime for about two months at most.

       THE RULE. A rushed site's crews work OVERTIME_HOURS instead of
       STANDARD_HOURS. Its month's work is its share x (50 / 40) x the
       month's productivity factor (overtimeOutput()), the factor for the
       n-th consecutive month on overtime being the table averaged over that
       month's weeks, WEEKS_A_MONTH of them, week by week
       (overtimeProductivity()): about 1.14 times a normal month's work in
       the first month, 1.02 in the second, and 0.94 - LESS than a normal
       month - from the third on. The extra points are the site's alone; the
       same crews work longer, so none are taken from another site. A month
       whose share already finishes the site is not worked on overtime.

       THE PREMIUM. Hours over 40 are paid at time and a half (Canada Labour
       Code, R.S.C. 1985, c. L-2, s. 174), so the crews' wage bill on
       overtime is OVERTIME_WAGE_BILL = (40 + 10 x 1.5) / 40 = 1.375 times
       the normal bill. The city pays the 0.375 (premiumShare()) of the wage
       bill of the crews on that site's share - the share's points at the
       builders' own wage bill a point (BuildingManager.BuildersWages
       .perPointToday(), a depot's posts at today's wages) - each month, on
       top of the contract, with the builders' sales tax in it as every
       price of theirs has (Game, THE BUILDERS' PRICE). The builders pay it
       out as wages (sectors.Construction.setOvertimeWages()), so it reaches
       the households. It is paid from the treasury like any construction
       bill and is owed to the builders as arrears past the ceiling
       (TreasuryLine.BUILDING_OVERTIME), as the material escalation is.

       The count of months on overtime resets after a month off overtime. A
       rush is the city's own sites' only; it can be stopped at any time and
       ends when the site completes.
       ===================================================================== */

    /** The normal working week the report measures against, in hours. */
    public static final double STANDARD_HOURS = 40;

    /** The week on overtime: the Business Roundtable's five tens (Report C-2, Figure 4). */
    public static final double OVERTIME_HOURS = 50;

    /** What an hour over the standard week is paid at: time and a half (Canada Labour Code, section 174). */
    public static final double OVERTIME_RATE = 1.5;

    /** Weeks in a month for averaging the report's table: 52 / 12, the 4.33 the brief reads it at. */
    public static final double WEEKS_A_MONTH = 52.0 / 12.0;

    /** Where each step of the report's 50-hour curve ends, in weeks on the schedule (Report C-2, Figure 4); the last step runs on. */
    static final double[] OVERTIME_WEEKS_ENDING = { 2, 4, 6, 8, 10 };

    /** Productivity on a 50-hour week against a 40-hour one, for each step above and beyond the last (Report C-2, Figure 4). */
    static final double[] OVERTIME_PRODUCTIVITY = { 0.926, 0.90, 0.87, 0.80, 0.752, 0.750 };

    /** The crews' wage bill on overtime over their normal bill: (40 + 10 x 1.5) / 40 = 1.375. */
    public static final double OVERTIME_WAGE_BILL =
            (STANDARD_HOURS + (OVERTIME_HOURS - STANDARD_HOURS) * OVERTIME_RATE) / STANDARD_HOURS;

    /**
     * The report's productivity factor for the n-th consecutive month on
     * overtime: its table averaged over that month's weeks, each week
     * weighted by the part of it the month covers.
     */
    public static double overtimeProductivity(int month) {
        if (month < 1) return 1;
        double from = (month - 1) * WEEKS_A_MONTH, to = month * WEEKS_A_MONTH;
        double sum = 0, start = 0;
        for (int i = 0; i < OVERTIME_PRODUCTIVITY.length; i++) {
            double end = i < OVERTIME_WEEKS_ENDING.length ? OVERTIME_WEEKS_ENDING[i] : Double.POSITIVE_INFINITY;
            double lo = Math.max(from, start), hi = Math.min(to, end);
            if (hi > lo) sum += (hi - lo) * OVERTIME_PRODUCTIVITY[i];
            start = end;
        }
        return sum / WEEKS_A_MONTH;
    }

    /** A rushed site's month of work over a normal month's, in its n-th consecutive month on overtime: (50 / 40) x the month's factor. */
    public static double overtimeOutput(int month) {
        return OVERTIME_HOURS / STANDARD_HOURS * overtimeProductivity(month);
    }

    /** What the city pays on a rushed site's crews, over their normal wage bill: 0.375. */
    public static double premiumShare() {
        return OVERTIME_WAGE_BILL - 1;
    }

    /* =====================================================================
       C. CANCEL: TERMINATION FOR CONVENIENCE, THE SHELL KEPT

       SOURCE: FAR 52.249-2, Termination for Convenience of the Government
       (Fixed-Price), paragraphs (b) and (g): the contractor stops work and
       is paid "the costs incurred in the performance of the work
       terminated", with a fair profit on it, plus "the reasonable costs of
       settlement"; the materials acquired pass to the owner.

       THE RULE. The city's own orders only. A cancel takes effect at the end
       of the month: the month's work is done and billed - the crews worked
       it; that is the wind-down - and nothing after. The refund is what the
       city prepaid and the builders have not earned on that site: the work
       not done, the material allowance not drawn, and the sales tax on
       both, which the builders have not yet remitted - the city's contract
       left on the stack. It comes back out of the builders' unearned revenue
       on the site (sectors.Construction.cancelOrder()), so their books still
       balance. The material already drawn stays in the shell.

       FAR's settlement expenses - the accounting, legal and clerical costs
       of winding the contract up - have no line in the model, and none is
       invented: the refund is the contract left, whole.

       THE SHELL is a stopped site: its progress and its drawn material stay,
       it holds its land, and it has no crew, no upkeep and no output.
       RESTART puts it back on site for its remaining work at today's quote -
       the labour at today's builders' wages, the material still to draw at
       today's price, and the tax, by the same rules as any order
       (Game.quoteRestart()). DEMOLISH clears it (D), at the demolition cost
       of the work it holds.
       ===================================================================== */

    /* =====================================================================
       D. DEMOLISH: THE CITY'S OWN BUILDINGS

       SOURCES. The cost is about a twentieth of what the building cost to
       build: SIGTARP's special report of 26 April 2017 on the Hardest Hit
       Fund's blight elimination gives Detroit's average cost of demolition
       per house as $11,515 (Q3 2014) to $17,622 (Q2 2016), $13,896 at Q4
       2016, and $14,855 in July 2015; NAHB's "Cost of Constructing a Home"
       (2015) puts the average construction cost of a new single-family home
       at $289,415. $14,855 / $289,415 = 5.1%.

       THE RULE. Demolishing a building is a site of DEMOLITION_SHARE of its
       template's construction points, ordered like any other and served by
       the same crew rule. The city pays its quote by the same price rules as
       any order (Game.quoteDemolition()): the work at the builders' rate for
       that building's work today, no material, and the tax.

       THE BUILDING CLOSES AS THE NEXT MONTH STARTS - the first month the
       order is in force for, since an order placed between two presses is
       the coming month's - before anything in it reads the city
       (Game.closeDemolished(), right after the calendar turns): its staff
       are let go, its residents - a home's - are moved out by the model's
       own rule for a home that is gone (the families are arranged into the
       homes that stand, and whoever does not fit is unhoused, not deleted:
       Game, DEMOGRAPHICS), and its service capacity goes. Not the instant
       the order is placed: a city reloaded between the presses recounts its
       posts and its wage bill from what stands, and the live one carries
       last month's, so a building gone in the gap would make the two play
       different months. When the
       demolition completes, its construction material is salvaged by the
       0.7.8 rule (Game, THE PLANT'S MATERIAL, TO THE BUILDERS: the builders
       buy it at the materials market's price, as much as their cash covers),
       with the proceeds to the treasury, and its ground returns to the city
       as free land.
       ===================================================================== */

    /**
     * The share of a building's construction points its demolition is:
     * Detroit's average demolition of July 2015, $14,855 (SIGTARP, 26 April
     * 2017), over the average new single-family home of 2015, $289,415
     * (NAHB) - 5.1%, taken at 5%.
     */
    public static final double DEMOLITION_SHARE = 0.05;

    /* =====================================================================
       E. BUY-OUTS: A BUSINESS'S OR A LANDLORD'S BUILDING, BY COMPULSORY
       PURCHASE

       SOURCE: the Expropriations Act, R.S.O. 1990, c. E.26 (Ontario).
       s. 13(2): compensation is based on "(a) the market value of the land;
       (b) the damages attributable to disturbance; (c) damages for injurious
       affection; and (d) any special difficulties in relocation". s. 14(1):
       market value is "the amount that the land might be expected to realize
       if sold in the open market by a willing seller to a willing buyer".
       s. 19(1): compensation for "business loss resulting from the
       relocation of the business made necessary by the expropriation". The
       5% allowance of s. 18(1)(a)(i) is for land "used by the owner for
       residential purposes"; no owner in the model lives in what it owns,
       so it does not apply. Injurious affection - harm to land the owner
       keeps - has no figure in the model and is not paid.

       THE RULE (Game.quoteBuyOut()). The city pays the owner:
         - MARKET VALUE: the building at the model's own value of it for its
           owner - its cash cost and its material at today's price, the
           figure its owner's balance sheet carries and the property tax is
           assessed on (BuildingManager.getBookValueBySector(),
           EconomyManager.getAssessedValue()) - plus its ground at the land
           market's price (LandManager.priceFor(), what the city pays a
           business for a plot it gives back);
         - BUSINESS LOSS: the building's share of its owner's operating
           profit - its share of the owner's buildings at that same value,
           of last month's operating income - for the months a replacement
           would take at today's queue (Game.quoteMonths(), waitFor()); and
           nothing if that share of profit is negative.
       Then the city owns the building and demolishes it by D.

       MORTGAGES AND DEBTS STAY WITH THE OWNING SECTOR, which receives the
       cash. In the model a loan, a bond and an insured mortgage are the
       sector's (BusinessDebt is keyed by sector, not by building), so there
       is nothing to discharge: the sector holds the cash and still owes
       what it owed.
       ===================================================================== */

    /* =====================================================================
       F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70)

       Jerus: "make it an option to upgrade from gravel to paved, but not from
       paved to highway, and that the build menu allows and recommends this
       if better, total cost is higher than just building paved."

       THE ORDER IS A PAVED ROAD'S, ON THE GRAVEL ROAD'S OWN GROUND. Paving n
       of the city's standing Gravel Roads puts n Paved Roads on the Paved
       Road site, as an order of the city's (Game.paveRoads()): the same
       crews, the same material drawn as they build, the same contract and
       escalation, the same wait - but they take no ground of their own,
       because each stands where its gravel road stands. Only a Gravel Road
       can be paved, and only to a Paved Road: a Paved Road is not raised to
       an Elevated Highway (Jerus), and nothing else has a cheaper form.

       THE PRICE (Game.quotePave()), star N1-1: a Paved Road's quote, less the
       gravel road's material, which goes into the new road's bed - the
       order draws the paved road's material less the gravel road's (450 - 193
       units), so the salvage is that material at the day's price, the 0.7.8
       rule's value of a building's material (Game, THE PLANT'S MATERIAL, TO
       THE BUILDERS) - plus the works premium: taking up the old surface,
       priced as a demolition of it is (DEMOLITION_SHARE of the gravel road's
       work at the builders' rate, D above). So a gravel road and its paving
       cost more than a paved road built outright, by the gravel road's own
       work (all of its price but its material) and the take-up: the sum
       Jerus asked for, from the model's own two rules and no new figure.

       THE ROAD STAYS OPEN, star N1-2: the gravel road carries its traffic
       until its paving finishes, and is then retired as the paved road
       opens - in the same month's advance, so the network goes from 900 to
       1,200 trips in one step. The model has no half-open road, and a
       closed one would make paving a loss of capacity for months in exactly
       the congested city that wants it; real practice keeps a rural road
       open by working it a half at a time.

       THE TIME, star N1-3: a Paved Road's points, 4,000 a road, queued on its
       site like any order. The take-up's 70 points (DEMOLITION_SHARE of the
       gravel's 1,400) are paid for but not added: they are 1.75% of the
       paved road's work, whose earthworks the gravel bed already has done.

       THE GROUND, star N1-4: freed. A paved road needs 250,000 sq ft where a
       gravel road took 450,000, and the 200,000 between come back to the
       city's free ground when the paving finishes, as a demolition's ground
       does - which is what makes paving the answer when land is dear.

       WHICH GRAVEL ROAD, AND WHEN. The units on a site finish in the order
       they were placed (BuildingsStacks.advanceConstruction() finishes them
       off the front of one pool), so each paving is kept as a batch with the
       Paved Roads on site ahead of it: of the f that finish in a month, those
       past a batch's place are its own (pavedOf()), and that many gravel
       roads retire. A Paved Road site with paving on it is not stopped
       (Game.cancelSite()): a road dug a half at a time is not left as a
       shell, and the paving cannot be told apart from the new roads in the
       site's one pool of work.
       ===================================================================== */

    /** The road that can be paved (0.7.70): a Gravel Road, and nothing else. */
    public static final String PAVE_FROM = "Gravel Road";

    /** ...and what it is paved to: a Paved Road. A Paved Road is not raised to an Elevated Highway (Jerus). */
    public static final String PAVE_TO = "Paved Road";

    /** Whether a building is one the city can pave: a Gravel Road. */
    public static boolean paves(BuildingsTemplate t) {
        return t != null && PAVE_FROM.equals(t.getName());
    }

    /* ---------------------------------------------------------------------
       THE STATE, as the save carries it
       --------------------------------------------------------------------- */

    /** A site put on overtime, and how many months in a row it has been worked on it. */
    public static final class Rush {
        public String key;
        /** Whether the player has it on; off until the next month passes, so a stop and a restart between two presses keep the count. */
        public boolean on;
        /** Consecutive months worked on overtime so far. */
        public int months;
        public Rush() { }
        Rush(String key) { this.key = key; this.on = true; }
    }

    /** A stopped site: what an order cancelled left standing. One per building type. */
    public static final class Shell {
        public int templateId;
        public String building;
        public int buildings;
        /** The points of work it holds. */
        public double progress;
        /** Units of material it has still to draw - what a restart quotes for. */
        public double materialsOwed;
        /** What came back to the city when it stopped. */
        public double refunded;
        /** The month it stopped. */
        public int month;
        public Shell() { }
    }

    /** A demolition on site: the city's own building, a shell, or a building it bought. */
    public static final class Demolition {
        public int id;
        public int templateId;
        public String building;
        public int buildings;
        /** The points of work it is, all told: DEMOLITION_SHARE of the work it holds. */
        public double points;
        public double progress;
        /** Units of construction material the buildings hold, salvaged when it completes. */
        public double salvageUnits;
        /** The ground it holds until it completes, in square feet. */
        public double landSqFt;
        /** What the city paid the builders for it. */
        public double paid;
        /** ...and what of that they have still to earn. */
        public double value;
        /** "City" for the city's own, a sector's key for a building it bought, "Shell" for a stopped site. */
        public String from;
        /** The month it was ordered. */
        public int month;
        /** True from the order until the month starts and the buildings close (Game.closeDemolished()): they still stand, and hold their own ground. A shell's demolition is never closing. */
        public boolean closing;
        /** What the city paid a bought building's owner for its ground, for the demolition log; nothing for the city's own. */
        public double groundPaid;
        public Demolition() { }

        public String key() { return "D" + id; }
        /** Points still to do. */
        public double owed() { return Math.max(0, points - progress); }
    }

    /**
     * A compulsory purchase, as the city made it, kept and saved as its
     * record. Nothing on screen reads it yet - the Demolish tab has no
     * history; the demolition log's line says whom the buildings were bought
     * from (Game.closeDemolished()) - and the harness and the save do.
     */
    public static final class Expropriation {
        public int month;
        public int templateId;
        public String building;
        public int buildings;
        public String sector;
        public double buildingValue;
        public double ground;
        public double businessLoss;
        /** The months a replacement would have taken, which the business loss was paid for. */
        public double months;
        public Expropriation() { }
        public double total() { return buildingValue + ground + businessLoss; }
    }

    /** One stack's run since it last stood empty: how many of it have opened, and what its orders cost. For the panel's "done of total" and "paid". */
    public static final class Run {
        public int templateId;
        public int built;
        public double billed;
        public Run() { }
    }

    /**
     * F. A paving on the Paved Road site (0.7.70): how many gravel roads it
     * paves, and how many Paved Roads on the site are ahead of it - placed
     * before it and not yet open. The gravel roads stand, and carry their
     * traffic, until their own Paved Roads open.
     */
    public static final class Paving {
        public int roads;
        /** Paved Roads on the site ahead of this paving, which open first. */
        public int ahead;
        /** The month it was ordered. */
        public int month;
        public Paving() { }
    }

    /** Everything above, under one key in the save. */
    public static final class State {
        public boolean prioritySet;
        public java.util.List<String> order = new java.util.ArrayList<>();
        public java.util.List<Rush> rushes = new java.util.ArrayList<>();
        public java.util.List<String> cancelling = new java.util.ArrayList<>();
        public java.util.List<Shell> shells = new java.util.ArrayList<>();
        public java.util.List<Demolition> demolitions = new java.util.ArrayList<>();
        public int nextDemolition = 1;
        public java.util.List<Expropriation> expropriations = new java.util.ArrayList<>();
        public java.util.List<Run> runs = new java.util.ArrayList<>();
        /** F. The pavings on the Paved Road site (0.7.70); none in an older save. */
        public java.util.List<Paving> paving = new java.util.ArrayList<>();
    }

    private State state = new State();

    /** The state as the save writes it. */
    public State toState() { return state; }

    /** ...and back, from a save; null - a format-27 save, or none - is a city nobody has touched. */
    public void restore(State saved) {
        state = saved == null ? new State() : saved;
        if (state.order == null) state.order = new java.util.ArrayList<>();
        if (state.rushes == null) state.rushes = new java.util.ArrayList<>();
        if (state.cancelling == null) state.cancelling = new java.util.ArrayList<>();
        if (state.shells == null) state.shells = new java.util.ArrayList<>();
        if (state.demolitions == null) state.demolitions = new java.util.ArrayList<>();
        if (state.expropriations == null) state.expropriations = new java.util.ArrayList<>();
        if (state.runs == null) state.runs = new java.util.ArrayList<>();
        if (state.paving == null) state.paving = new java.util.ArrayList<>();
        if (state.nextDemolition < 1) state.nextDemolition = 1;
    }

    /**
     * Whether the month's advance has anything of this class to apply: an
     * order set, a rush, a cancel waiting for the month's end, or a
     * demolition on site. False in every city nobody has touched, and the
     * advance then takes the path it always took.
     */
    public boolean engaged() {
        return state.prioritySet || !state.rushes.isEmpty() || !state.cancelling.isEmpty()
                || !state.demolitions.isEmpty();
    }

    /* ----- the key of a site ----- */

    /** A stack's key: "B" and its template's id. */
    public static String keyOf(BuildingsTemplate t) { return "B" + t.getId(); }

    /** The template id a stack's key names, or -1 for a key that is not a stack's. */
    public static int templateIdOf(String key) {
        if (key == null || key.length() < 2 || key.charAt(0) != 'B') return -1;
        try { return Integer.parseInt(key.substring(1)); } catch (NumberFormatException e) { return -1; }
    }

    /* ----- A. the order ----- */

    public boolean isPrioritySet() { return state.prioritySet; }

    /** The order as the player left it, keys of sites that may since have gone included. */
    public java.util.List<String> savedOrder() { return java.util.Collections.unmodifiableList(state.order); }

    /**
     * The order the city's sites are served in: the player's, with any of
     * them it does not name - ordered since - after it in the order given,
     * and none that has gone. With no order set, `present` as given.
     */
    public java.util.List<String> effectiveOrder(java.util.List<String> present) {
        if (!state.prioritySet) return new java.util.ArrayList<>(present);
        java.util.List<String> out = new java.util.ArrayList<>();
        for (String k : state.order) if (present.contains(k) && !out.contains(k)) out.add(k);
        for (String k : present) if (!out.contains(k)) out.add(k);
        return out;
    }

    /** Sets the order: the player's hand, from the panel's arrows. */
    public void setOrder(java.util.List<String> keys) {
        state.prioritySet = true;
        state.order = new java.util.ArrayList<>(keys);
    }

    /** Back to the crews' rule, with no order of the city's own. */
    public void clearOrder() {
        state.prioritySet = false;
        state.order = new java.util.ArrayList<>();
    }

    /* ----- B. the rushes ----- */

    public Rush rushOf(String key) {
        for (Rush r : state.rushes) if (r.key.equals(key)) return r;
        return null;
    }

    /** Whether the player has this site on overtime. */
    public boolean isRushed(String key) {
        Rush r = rushOf(key);
        return r != null && r.on;
    }

    /** Months in a row this site has been worked on overtime so far. */
    public int monthsOnOvertime(String key) {
        Rush r = rushOf(key);
        return r == null ? 0 : r.months;
    }

    /** On or off, by the player's hand. Off keeps the count until a month passes. */
    public void setRush(String key, boolean on) {
        Rush r = rushOf(key);
        if (on) {
            if (r == null) state.rushes.add(new Rush(key));
            else r.on = true;
        } else if (r != null) {
            r.on = false;
        }
    }

    /** Every rush on record, on or waiting out its month off. */
    public java.util.List<Rush> rushes() { return java.util.Collections.unmodifiableList(state.rushes); }

    /**
     * The month's rush bookkeeping, after the advance: a site worked on
     * overtime counts a month more; one that was not - stopped, finished,
     * or given no crews - starts again from nothing, and a stopped one is
     * forgotten.
     */
    void afterMonth(java.util.Set<String> workedOnOvertime, java.util.Set<String> present,
                    java.util.List<String> cityPresent) {
        java.util.Iterator<Rush> it = state.rushes.iterator();
        while (it.hasNext()) {
            Rush r = it.next();
            if (!present.contains(r.key) || !r.on) { it.remove(); continue; }
            if (workedOnOvertime.contains(r.key)) r.months++;
            else r.months = 0;
        }
        if (state.prioritySet) {
            // The order keeps the city's sites still on site, in its order;
            // one placed while it was set joins it at the bottom; and an
            // order with none of its sites left is cleared. See A. PRIORITY,
            // AN ORDER LASTS AS LONG AS ITS SITES.
            state.order.retainAll(cityPresent);
            for (String k : cityPresent) if (!state.order.contains(k)) state.order.add(k);
            if (state.order.isEmpty()) clearOrder();
        }
        state.cancelling.retainAll(present);
    }

    /* ----- C. the cancels and the shells ----- */

    public boolean isCancelling(String key) { return state.cancelling.contains(key); }

    public void setCancelling(String key, boolean cancel) {
        if (cancel) { if (!state.cancelling.contains(key)) state.cancelling.add(key); }
        else state.cancelling.remove(key);
    }

    java.util.List<String> cancelling() { return state.cancelling; }

    /** Every cancel settled at the month's end, stopped or moot. */
    void clearCancelling() { state.cancelling.clear(); }

    public java.util.List<Shell> shells() { return java.util.Collections.unmodifiableList(state.shells); }

    public Shell shellOf(int templateId) {
        for (Shell s : state.shells) if (s.templateId == templateId) return s;
        return null;
    }

    /** A stopped order's buildings, onto the shell of that building - a second stop joins the first. */
    Shell addShell(BuildingsTemplate t, int buildings, double progress, double materialsOwed, int month) {
        Shell s = shellOf(t.getId());
        if (s == null) {
            s = new Shell();
            s.templateId = t.getId();
            s.building = t.getName();
            s.month = month;
            state.shells.add(s);
        }
        s.buildings += buildings;
        s.progress += progress;
        s.materialsOwed += materialsOwed;
        return s;
    }

    void removeShell(Shell s) { state.shells.remove(s); }

    /* ----- D. the demolitions ----- */

    public java.util.List<Demolition> demolitions() { return java.util.Collections.unmodifiableList(state.demolitions); }

    public Demolition demolitionOf(String key) {
        for (Demolition d : state.demolitions) if (d.key().equals(key)) return d;
        return null;
    }

    Demolition addDemolition(BuildingsTemplate t, int buildings, double points, double salvageUnits,
                             double landSqFt, double paid, String from, int month, boolean closing) {
        Demolition d = new Demolition();
        d.id = state.nextDemolition++;
        d.templateId = t.getId();
        d.building = t.getName();
        d.buildings = buildings;
        d.points = Math.max(0, points);
        d.salvageUnits = Math.max(0, salvageUnits);
        d.landSqFt = Math.max(0, landSqFt);
        d.paid = Math.max(0, paid);
        d.value = d.paid;
        d.from = from;
        d.month = month;
        d.closing = closing;
        state.demolitions.add(d);
        return d;
    }

    /** Buildings of this kind ordered demolished that still stand until the month starts. */
    public int closingOf(int templateId) {
        int n = 0;
        for (Demolition d : state.demolitions) if (d.closing && d.templateId == templateId) n += d.buildings;
        return n;
    }

    void removeDemolition(Demolition d) { state.demolitions.remove(d); }

    /* ----- E. the buy-outs ----- */

    public java.util.List<Expropriation> expropriations() { return java.util.Collections.unmodifiableList(state.expropriations); }

    void recordExpropriation(Expropriation e) { state.expropriations.add(e); }

    /* ----- F. the pavings ----- */

    public java.util.List<Paving> pavings() { return java.util.Collections.unmodifiableList(state.paving); }

    /** Gravel roads being paved: on the Paved Road site, standing until their paving opens. */
    public int paving() {
        int n = 0;
        for (Paving p : state.paving) n += p.roads;
        return n;
    }

    /** A paving of n roads, behind the `ahead` Paved Roads already on the site. */
    Paving addPaving(int roads, int ahead, int month) {
        Paving p = new Paving();
        p.roads = Math.max(0, roads);
        p.ahead = Math.max(0, ahead);
        p.month = month;
        state.paving.add(p);
        return p;
    }

    /**
     * Of `finished` Paved Roads opening this month off the front of the site,
     * the ones that are pavings', each paving moved up the site by them: the
     * gravel roads to retire. A paving all of whose roads have opened is gone.
     */
    int pavedOf(int finished) {
        if (finished <= 0 || state.paving.isEmpty()) return 0;
        int paved = 0;
        java.util.Iterator<Paving> it = state.paving.iterator();
        while (it.hasNext()) {
            Paving p = it.next();
            int own = Math.max(0, Math.min(p.roads, finished - p.ahead));
            p.roads -= own;
            p.ahead = Math.max(0, p.ahead - finished);
            paved += own;
            if (p.roads <= 0) it.remove();
        }
        return paved;
    }

    /* ----- each stack's run ----- */

    /** A stack's run, null if none is on record. */
    public Run runOf(int templateId) {
        for (Run r : state.runs) if (r.templateId == templateId) return r;
        return null;
    }

    Run runFor(int templateId) {
        Run r = runOf(templateId);
        if (r == null) {
            r = new Run();
            r.templateId = templateId;
            state.runs.add(r);
        }
        return r;
    }

    void endRun(int templateId) {
        state.runs.removeIf(r -> r.templateId == templateId);
    }

    /* ----- a reform ----- */

    /** A currency reform: the money is money; the points, the units and the ground are not. */
    public void redenominate(double scale) {
        for (Shell s : state.shells) s.refunded *= scale;
        for (Demolition d : state.demolitions) { d.paid *= scale; d.value *= scale; d.groundPaid *= scale; }
        for (Expropriation e : state.expropriations) {
            e.buildingValue *= scale; e.ground *= scale; e.businessLoss *= scale;
        }
        for (Run r : state.runs) r.billed *= scale;
    }

    /* ---------------------------------------------------------------------
       THE MONTH'S EVENTS, for Game to settle. A month's flows, read once.
       --------------------------------------------------------------------- */

    /** A rushed site worked on overtime this month: its share, its work, the builders' wage bill a point it was struck at, and the premium on its crews, before the tax. */
    public record Overtime(String key, String building, int month, double share, double work, double perPoint,
                           double premium) { }

    /** An order stopped at the month's end: what comes back to the city, the material allowance in it, the points of work it was for, and the shell it left. */
    public record Refund(int templateId, String building, int buildings, double value, double allowance,
                         double points, Shell shell) { }

    /**
     * A demolition finished this month - and, once Game has sold it, its
     * own sale: the units the builders bought and what they paid, which the
     * inbox reads off the site rather than by its name (after the docs pass:
     * two of one building and count finishing in one month are two sales).
     */
    public static final class Completed {
        private final Demolition site;
        private double unitsSold, paid;
        public Completed(Demolition site) { this.site = site; }
        public Demolition site() { return site; }
        /** Units of its material the builders bought. */
        public double unitsSold() { return unitsSold; }
        /** ...and what they paid for them. */
        public double paid() { return paid; }
        void sold(double units, double money) { unitsSold = units; paid = money; }
    }

    /** F. Gravel roads whose paving opened this month (0.7.70): retired as their Paved Roads opened, the ground between the two to free. */
    public record Paved(int roads, double landFreedSqFt) { }

    /** The month's events, as the advance left them. */
    public static final class Events {
        public final java.util.List<Overtime> overtime = new java.util.ArrayList<>();
        public final java.util.List<Refund> refunds = new java.util.ArrayList<>();
        public final java.util.List<Completed> completed = new java.util.ArrayList<>();
        public final java.util.List<Paved> paved = new java.util.ArrayList<>();
        public double overtimePoints;
        public boolean isEmpty() { return overtime.isEmpty() && refunds.isEmpty() && completed.isEmpty() && paved.isEmpty(); }
    }
}
