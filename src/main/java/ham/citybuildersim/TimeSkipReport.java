package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What happened while you were not watching.
 *
 * Fast-forwarding a hundred months is the normal way to play this game, and
 * until now it told you nothing: the screen said "100 of 100 months simulated"
 * and left you to work out what had changed by comparing numbers you had not
 * written down. Worse, anything that reports itself for a while and then expires
 * - the demolition log keeps entries for twenty-four months - could happen and
 * disappear entirely inside a single skip.
 *
 * So this takes a snapshot before the jump, samples every month during it, and
 * diffs against a snapshot after.
 *
 * TWO KINDS OF THING
 *
 *   DELTAS come from the two snapshots: population, cash, output, debt, land,
 *   what got built and what got demolished. Cheap and exact.
 *
 *   EPISODES come from the monthly samples: how many months the city ran short
 *   of power or water, how long it sat with no land, how many months its
 *   households went short - and since 0.7.1 how many months the treasury
 *   lived on the central bank's advances and how many of those at their
 *   ceiling, which is what replaced the emergency debt this line once said it
 *   counted (it never did; the note is gone since 0.7.0), and since 0.7.15
 *   what those advances came to over the skip. These cannot be
 *   reconstructed from the endpoints, because a
 *   city that starves for fifty months and recovers looks identical at both
 *   ends to one that never had a problem.
 *
 * AND THE SALES NOBODY PRESSED (CD2, slice 6; D28 to D30). The rollover and
 * automatic building sell the city's paper month after month inside a skip, each
 * at its own cap, and a short one pays the maturity from the cash and the
 * central bank's advances or keeps an ask open - none of which either end of
 * the skip shows. sampleSales() takes each month's sales (AutoSale), and the
 * report lists every one, its stop, its tail and where the rest of a short
 * one came from (getAutoSaleLines()), with a line or two of headlines.
 *
 * Nothing here computes anything the simulation does not already know. It reads
 * finished figures and remembers them, so no screen built on it can move a
 * single number in the game.
 */
public class TimeSkipReport {

    /** A city, at one instant. Everything a diff could want. */
    private static class Snapshot {

        int month;
        double cash;
        long population;
        long housing;
        long jobs;
        double monthlyGdp;
        double annualGdp;
        double cityDebt;
        double businessDebt;
        double landOwnedBlocks;
        double landUtilisation;
        double householdSavingRate;
        double householdRentBurden;
        double cumulativeWriteOffs;

        final Map<String, Integer> buildings = new LinkedHashMap<>();
    }

    private Snapshot before;
    private Snapshot after;

    private boolean complete;

    /** How many months were asked for, and how many actually ran. */
    private int requested;
    private int completed;

    /* ----------------------------- episodes ----------------------------- */
    private int monthsShortOfPower;
    private int monthsShortOfWater;

    /**
     * Months the road network could not carry the traffic on it.
     *
     * Counted for the same reason as the two above, and more urgently: power
     * and water fail loudly, all at once, and a player notices. Congestion
     * arrives one building at a time and shows up only as growth that quietly
     * stopped paying - which is exactly the thing a forty-month skip hides.
     */
    private int monthsCongested;
    private int monthsOutOfLand;
    private int monthsHouseholdsShort;
    private int monthsNothingBuilt;

    /** Months the treasury owed the central bank advances at the month's end, and of those, months it owed them at the ceiling (0.7.1). */
    private int monthsOnAdvances;
    private int monthsAtCeiling;

    /**
     * What the central bank advanced the treasury over the skip, and what the
     * treasury owed it when the last month closed (0.7.15): a skip runs
     * through an empty treasury on the advances since Jerus's "Skip runs
     * too", so the report says what they came to, not only for how long.
     */
    private double advancedDuringSkip;
    private double advancesOwedAtEnd;

    private double worstEnergyRatio = 1;
    private double worstRoadRatio = 1;
    private long peakPopulation;

    public void beginSkip(int requested) {
        this.requested = requested;
        this.completed = 0;
        this.complete = false;

        monthsShortOfPower = 0;
        monthsShortOfWater = 0;
        monthsCongested = 0;
        monthsOutOfLand = 0;
        monthsHouseholdsShort = 0;
        monthsNothingBuilt = 0;
        monthsOnAdvances = 0;
        monthsAtCeiling = 0;
        advancedDuringSkip = 0;
        advancesOwedAtEnd = 0;
        autoSales.clear();
        worstEnergyRatio = 1;
        worstRoadRatio = 1;
        peakPopulation = 0;
        // ...and the health counters (FW6, 2026-10-25): a long-lived city's report added every earlier skip's outbreaks,
        // sick months and worst ratios into this one's, which a reloaded city (a fresh report) never showed.
        outbreaks = 0;
        monthsInOutbreak = 0;
        wasOutbreak = false;
        monthsSick = 0;
        worstWorkRatio = 1;
        peakUnburied = 0;

        before = null;
        after = null;
    }

    /* ------------------------------ capture ------------------------------ */

    /**
     * Records the state at one instant.
     *
     * Every argument is a value some manager already holds. Taking them as
     * arguments rather than reaching into Game keeps this class testable
     * without a running simulation, which is the whole reason it can be
     * verified at all.
     */
    public void snapshot(boolean atStart, int month, double cash,
                         long population, long housing, long jobs,
                         double monthlyGdp, double annualGdp,
                         double cityDebt, double businessDebt,
                         double landOwnedBlocks, double landUtilisation,
                         double householdSavingRate, double householdRentBurden,
                         double cumulativeWriteOffs,
                         Map<String, Integer> buildings) {

        Snapshot s = new Snapshot();
        s.month = month;
        s.cash = cash;
        s.population = population;
        s.housing = housing;
        s.jobs = jobs;
        s.monthlyGdp = monthlyGdp;
        s.annualGdp = annualGdp;
        s.cityDebt = cityDebt;
        s.businessDebt = businessDebt;
        s.landOwnedBlocks = landOwnedBlocks;
        s.landUtilisation = landUtilisation;
        s.householdSavingRate = householdSavingRate;
        s.householdRentBurden = householdRentBurden;
        s.cumulativeWriteOffs = cumulativeWriteOffs;

        if (buildings != null) {
            s.buildings.putAll(buildings);
        }

        if (atStart) {
            before = s;
            peakPopulation = population;
        } else {
            after = s;
            complete = true;
        }
    }

    /**
     * One month of the skip, as it goes past.
     *
     * The endpoints cannot show any of this. A city that ran out of power for
     * forty months and then built a second station looks, at both ends, exactly
     * like one that never had a problem - and that is precisely the run the
     * player most needs told about.
     */
    public void sampleMonth(double energyRatio, double waterRatio,
                            double landAvailableSqFt, boolean householdsShort,
                            boolean anythingUnderConstruction, long population) {
        sampleMonth(energyRatio, waterRatio, 1, landAvailableSqFt, householdsShort,
                anythingUnderConstruction, population);
    }

    /** @param roadRatio what congestion let the city actually get done that month. */
    public void sampleMonth(double energyRatio, double waterRatio, double roadRatio,
                            double landAvailableSqFt, boolean householdsShort,
                            boolean anythingUnderConstruction, long population) {
        sampleMonth(energyRatio, waterRatio, roadRatio, landAvailableSqFt,
                householdsShort, anythingUnderConstruction, population, 1, false, 0);
    }

    /**
     * The same, plus the month's health.
     *
     * AN OUTBREAK IS THE THING A SKIP HIDES BEST. It lasts three or four months
     * and then decays to nothing, so a city that lost a quarter of its output to
     * an epidemic in the middle of a twenty-month skip looks, at both ends,
     * exactly like a city that was well the whole time - which is the same
     * argument the power and water samples were added for, and a sharper case of
     * it, because at least a brownout tends to persist.
     *
     * @param workRatio what sickness left of the month's work
     * @param outbreak  whether an epidemic was running
     * @param unburied  the dead the city had nowhere to put
     */
    public void sampleMonth(double energyRatio, double waterRatio, double roadRatio,
                            double landAvailableSqFt, boolean householdsShort,
                            boolean anythingUnderConstruction, long population,
                            double workRatio, boolean outbreak, double unburied) {

        completed++;

        if (outbreak) {
            monthsInOutbreak++;
            if (!wasOutbreak) outbreaks++;
        }
        wasOutbreak = outbreak;

        worstWorkRatio = Math.min(worstWorkRatio, workRatio);
        if (workRatio < .99) monthsSick++;
        peakUnburied = Math.max(peakUnburied, unburied);

        if (energyRatio < .99) monthsShortOfPower++;
        if (waterRatio < .99)  monthsShortOfWater++;
        if (roadRatio < .99)   monthsCongested++;

        worstRoadRatio = Math.min(worstRoadRatio, roadRatio);
        if (landAvailableSqFt <= 0) monthsOutOfLand++;
        if (householdsShort) monthsHouseholdsShort++;
        if (!anythingUnderConstruction) monthsNothingBuilt++;

        worstEnergyRatio = Math.min(worstEnergyRatio, energyRatio);
        peakPopulation = Math.max(peakPopulation, population);
    }

    /**
     * The treasury's month with its central bank (0.7.1): whether it ended the
     * month owing advances, and whether they stood at the ceiling. Sampled
     * beside sampleMonth(), and its own call so the older one's callers read
     * as they did.
     */
    public void sampleTreasury(boolean onAdvances, boolean atCeiling) {
        if (onAdvances) monthsOnAdvances++;
        if (onAdvances && atCeiling) monthsAtCeiling++;
    }

    /**
     * ...and since 0.7.15 what the central bank advanced the treasury this
     * month (CentralBank.getAdvancedToTreasury(), at the month's settle) and
     * what the treasury owes it at the month's end.
     */
    public void sampleTreasury(boolean onAdvances, boolean atCeiling, double advanced, double owed) {
        sampleTreasury(onAdvances, atCeiling);
        if (advanced > 0) advancedDuringSkip += advanced;
        advancesOwedAtEnd = Math.max(0, owed);
    }

    /* ----------------------- the automatic sales (CD2, slice 6) ----------------------- */

    /**
     * One sale the player did not press (D28 to D30): a rollover issue or an
     * automatic-building bond, sold at auction at the setting's own cap.
     *
     * @param by           "The rollover" or "Automatic building"
     * @param month        the month it ran in
     * @param paper        what was sold, in words: "10-year bond", "6-month bill"
     * @param asked        the face offered
     * @param sold         the face sold: less than asked when the sale was short
     * @param raised       the cash it brought
     * @param stop         the clearing yield
     * @param tail         the stop over fair
     * @param cover        what was bid at the cap, over the issue
     * @param shortSale    the bids did not reach the issue at the setting's cap
     * @param ceiling      that cap, as a yield
     * @param fromCash     the rollover's maturity the sale did not raise, paid from the treasury's cash
     * @param fromAdvances ...and from the central bank's advances
     * @param heldOpen     what automatic building's ask still has to raise after the sale, kept open (D30)
     */
    public record AutoSale(String by, int month, String paper, double asked, double sold, double raised,
                           double stop, double tail, double cover, boolean shortSale, double ceiling,
                           double fromCash, double fromAdvances, double heldOpen) {

        /** The sale as a line of the report: when, who, what, the stop and the tail, and where the rest of a short one came from. */
        public String line() {
            StringBuilder s = new StringBuilder();
            s.append(CityCalendar.formatShort(month)).append(": ").append(by).append(" sold a ").append(paper)
                    .append(" at ").append(String.format(java.util.Locale.ROOT, "%.2f%%", stop * 100))
                    .append(", a tail of ").append(String.format(java.util.Locale.ROOT, "%.0f", tail * 10_000))
                    .append(" basis points, ").append(DecisionLog.money(sold)).append(" of ").append(DecisionLog.money(asked))
                    .append(" of face for ").append(DecisionLog.money(raised));
            if (shortSale) {
                s.append(String.format(java.util.Locale.ROOT, ", short at its cap of %.2f%%", ceiling * 100));
                if (fromAdvances > 0) {
                    s.append("; ").append(DecisionLog.money(fromAdvances)).append(" of the maturity paid by the central bank's advances");
                    if (fromCash > 0) s.append(" and ").append(DecisionLog.money(fromCash)).append(" from the treasury's cash");
                } else if (fromCash > 0) {
                    s.append("; ").append(DecisionLog.money(fromCash)).append(" of the maturity paid from the treasury's cash");
                }
                if (heldOpen > 0) s.append("; ").append(DecisionLog.money(heldOpen)).append(" of the ask left open");
            }
            return s.append('.').toString();
        }
    }

    private final List<AutoSale> autoSales = new ArrayList<>();

    /** The most automatic sales a report keeps: a skip of centuries is not a ledger of them. */
    private static final int AUTO_SALES_MOST = 5000;

    /**
     * ...the month's automatic sales (Game.getAutoSalesThisMonth()), sampled
     * beside sampleMonth(): the rollover and automatic building sell month
     * after month inside a skip with nobody at the button, and none of it can
     * be read off the two ends.
     */
    public void sampleSales(List<AutoSale> sales) {
        if (sales == null) return;
        for (AutoSale s : sales) if (autoSales.size() < AUTO_SALES_MOST) autoSales.add(s);
    }

    /** Every automatic sale over the skip, in the order made. */
    public List<AutoSale> getAutoSales() { return java.util.Collections.unmodifiableList(autoSales); }

    /** ...as lines, one a sale: the stop, the tail, anything short and where the rest came from. */
    public List<String> getAutoSaleLines() {
        List<String> lines = new ArrayList<>();
        for (AutoSale s : autoSales) lines.add(s.line());
        return lines;
    }

    /**
     * What one seller sold over the skip, for the headlines: with worry, only
     * the line for the sales that came up short and where the rest came from
     * (the headlines' worrying lines, before "nothing went wrong" is decided);
     * without, the line saying how many it made and at what stops, which is
     * news but no worry. Nothing if it sold nothing.
     */
    private List<String> autoSaleHeadlines(String by, boolean worry) {
        List<String> lines = new ArrayList<>();
        int n = 0, shorts = 0;
        double lo = Double.POSITIVE_INFINITY, hi = 0, widest = 0, sold = 0, fromCash = 0, fromAdvances = 0, held = 0;
        for (AutoSale s : autoSales) {
            if (!s.by().equals(by)) continue;
            n++;
            lo = Math.min(lo, s.stop());
            hi = Math.max(hi, s.stop());
            widest = Math.max(widest, s.tail());
            sold += s.sold();
            if (s.shortSale()) {
                shorts++;
                fromCash += s.fromCash();
                fromAdvances += s.fromAdvances();
                held += s.heldOpen();
            }
        }
        if (n == 0) return lines;
        if (!worry) {
            lines.add(String.format(java.util.Locale.ROOT,
                    "%s sold %d time%s on its own (%s of face); the stops ran from %.2f%% to %.2f%%, the widest tail %.0f basis points.",
                    by, n, n == 1 ? "" : "s", DecisionLog.money(sold), lo * 100, hi * 100, widest * 10_000));
        }
        if (worry && shorts > 0) {
            StringBuilder s = new StringBuilder(String.format(java.util.Locale.ROOT,
                    "%s came up short at its cap %d time%s", by, shorts, shorts == 1 ? "" : "s"));
            if (fromAdvances > 0 || fromCash > 0) {
                s.append(": ").append(DecisionLog.money(fromCash)).append(" of maturities paid from the treasury's cash and ")
                        .append(DecisionLog.money(fromAdvances)).append(" by the central bank's advances");
            }
            if (held > 0) s.append(held > 0 && (fromAdvances > 0 || fromCash > 0) ? ", " : ": ")
                    .append(DecisionLog.money(held)).append(" of asks left open");
            lines.add(s.append('.').toString());
        }
        return lines;
    }

    public int getMonthsOnAdvances() { return monthsOnAdvances; }
    public int getMonthsAtCeiling()  { return monthsAtCeiling; }
    /** What the central bank advanced the treasury over the skip - printed for it. */
    public double getAdvancedDuringSkip() { return advancedDuringSkip; }
    /** ...and what the treasury owed it in advances when the skip's last month closed. */
    public double getAdvancesOwedAtEnd()  { return advancesOwedAtEnd; }

    /* ------------------------------- deltas ------------------------------- */

    /* -------------------------- health, sampled -------------------------- */

    private int outbreaks;
    private int monthsInOutbreak;
    private boolean wasOutbreak;
    private int monthsSick;
    private double worstWorkRatio = 1;
    private double peakUnburied;

    /** How many separate epidemics started during the skip. */
    public int getOutbreaks()          { return outbreaks; }
    public int getMonthsInOutbreak()   { return monthsInOutbreak; }
    public int getMonthsSick()         { return monthsSick; }
    /** The worst single month, as a share of work done. 1 means nobody was ill. */
    public double getWorstWorkRatio()  { return worstWorkRatio; }
    public double getPeakUnburied()    { return peakUnburied; }

    public boolean isComplete()   { return complete && before != null && after != null; }
    public int getRequested()     { return requested; }
    public int getCompleted()     { return completed; }
    public boolean stoppedEarly() { return completed < requested; }

    public int getStartMonth() { return (before != null) ? before.month : 0; }
    public int getEndMonth()   { return (after != null) ? after.month : 0; }

    public double getCashChange()        { return delta(a -> a.cash); }
    public double getPopulationChange()  { return delta(a -> (double) a.population); }
    public double getHousingChange()     { return delta(a -> (double) a.housing); }
    public double getJobsChange()        { return delta(a -> (double) a.jobs); }
    public double getMonthlyGdpChange()  { return delta(a -> a.monthlyGdp); }
    public double getAnnualGdpChange()   { return delta(a -> a.annualGdp); }
    public double getCityDebtChange()    { return delta(a -> a.cityDebt); }
    public double getBusinessDebtChange(){ return delta(a -> a.businessDebt); }
    public double getLandBlocksBought()  { return delta(a -> a.landOwnedBlocks); }

    public double getStartCash()        { return (before != null) ? before.cash : 0; }
    public double getEndCash()          { return (after != null) ? after.cash : 0; }
    public long getStartPopulation()     { return (before != null) ? before.population : 0; }
    public long getEndPopulation()       { return (after != null) ? after.population : 0; }
    public double getEndLandUtilisation(){ return (after != null) ? after.landUtilisation : 0; }
    public double getEndSavingRate()    { return (after != null) ? after.householdSavingRate : 0; }
    public double getEndRentBurden()    { return (after != null) ? after.householdRentBurden : 0; }
    public double getStartSavingRate()  { return (before != null) ? before.householdSavingRate : 0; }

    /** Debt the lenders wrote off during the skip. Not a figure either endpoint shows alone. */
    public double getWriteOffsDuringSkip() {
        return delta(a -> a.cumulativeWriteOffs);
    }

    /**
     * WHETHER THE SKIP'S DEFAULTS ARE NEWS (0.7.8). A sector defaults a
     * slice at a time now, so a little is written off most months wherever
     * a business owes much, and the summary names the write-offs only when
     * they came to more than the businesses' debt loses at Bank
     * .BASE_LOSS_RATE a year - what prime is priced for - over the months
     * skipped. The skip's own line, over its whole length; the inbox draws
     * a sharper one a month at a time (BusinessDebtManager.defaultsAreNews()).
     */
    public boolean defaultsWereNews() {
        double owed = Math.max(before == null ? 0 : before.businessDebt,
                after == null ? 0 : after.businessDebt);
        return getWriteOffsDuringSkip() > Bank.BASE_LOSS_RATE * completed / 12.0 * owed;
    }

    /** Cash per month, which is the number that says whether this is sustainable. */
    public double getCashPerMonth() {
        return (completed > 0) ? getCashChange() / completed : 0;
    }

    public double getPopulationPerMonth() {
        return (completed > 0) ? getPopulationChange() / completed : 0;
    }

    /** Population growth over the whole skip, annualised. */
    public double getPopulationGrowthRate() {
        if (before == null || after == null || before.population <= 0 || completed <= 0) {
            return 0;
        }
        double total = after.population / (double) before.population;
        return Math.pow(total, 12.0 / completed) - 1;
    }

    private interface Field { double of(Snapshot s); }

    private double delta(Field f) {
        if (before == null || after == null) {
            return 0;
        }
        return f.of(after) - f.of(before);
    }

    /* ------------------------------ buildings ------------------------------ */

    /** A building type whose count moved, and by how much. Gains and losses both. */
    public static class BuildingChange {
        public final String name;
        public final int change;

        BuildingChange(String name, int change) {
            this.name = name;
            this.change = change;
        }

        public boolean isGain() { return change > 0; }
    }

    /**
     * Everything whose count moved, biggest change first.
     *
     * Net, deliberately. A sector that put up four depots and scrapped three in
     * the same span really did end up with one more, and reporting "+4 / -3"
     * from endpoint data alone would be a fiction - the snapshots cannot see the
     * order things happened in. The demolition log carries the losses with
     * their dates for anyone who needs the detail.
     */
    public List<BuildingChange> getBuildingChanges() {

        List<BuildingChange> changes = new ArrayList<>();
        if (before == null || after == null) {
            return changes;
        }

        Map<String, Integer> seen = new LinkedHashMap<>();
        seen.putAll(before.buildings);
        seen.putAll(after.buildings);

        for (String name : seen.keySet()) {
            int start = before.buildings.getOrDefault(name, 0);
            int end = after.buildings.getOrDefault(name, 0);

            if (end != start) {
                changes.add(new BuildingChange(name, end - start));
            }
        }

        changes.sort((x, y) -> Integer.compare(Math.abs(y.change), Math.abs(x.change)));
        return changes;
    }

    public int getBuildingsGained() {
        int total = 0;
        for (BuildingChange c : getBuildingChanges()) {
            if (c.change > 0) total += c.change;
        }
        return total;
    }

    public int getBuildingsLost() {
        int total = 0;
        for (BuildingChange c : getBuildingChanges()) {
            if (c.change < 0) total -= c.change;
        }
        return total;
    }

    /* ------------------------------ episodes ------------------------------ */

    public int getMonthsShortOfPower()     { return monthsShortOfPower; }
    public int getMonthsShortOfWater()     { return monthsShortOfWater; }
    public int getMonthsCongested()        { return monthsCongested; }
    public double getWorstRoadRatio()      { return worstRoadRatio; }
    public int getMonthsOutOfLand()        { return monthsOutOfLand; }
    public int getMonthsHouseholdsShort()  { return monthsHouseholdsShort; }
    public int getMonthsNothingBuilt()     { return monthsNothingBuilt; }
    public double getWorstEnergyRatio()    { return worstEnergyRatio; }
    public long getPeakPopulation()         { return peakPopulation; }

    /** True when the city ended smaller than its high-water mark. */
    public boolean shrankFromPeak() {
        return after != null && peakPopulation > after.population;
    }

    /** Share of the skip spent with nothing on any building site. */
    public double getIdleShare() {
        return (completed > 0) ? monthsNothingBuilt / (double) completed : 0;
    }

    /**
     * The things worth putting in front of the player, in plain sentences.
     *
     * Ordered by how much they should worry someone: things that stop the city
     * growing first, then things that cost money, then things that are merely
     * notable. An empty list means the skip was uneventful, which is itself
     * worth saying rather than showing a blank panel.
     */
    public List<String> getHeadlines() {

        List<String> lines = new ArrayList<>();
        if (!isComplete()) {
            return lines;
        }

        // A skip runs through an empty treasury since 0.7.15, so the one thing
        // that stops it short is a month that threw - the window's SOMETHING
        // WENT WRONG says what (Game.takeSkipFailure()).
        if (stoppedEarly()) {
            lines.add("Stopped after " + completed + " of " + requested + " months.");
        }

        if (monthsOnAdvances > 0) {
            lines.add(String.format(
                    "The treasury lived on the central bank's advances for %d month%s%s.",
                    monthsOnAdvances, monthsOnAdvances == 1 ? "" : "s",
                    monthsAtCeiling > 0 ? String.format(", %d of them at the ceiling", monthsAtCeiling) : ""));
        }

        // The sales nobody pressed (D28 to D30): the rollover's and automatic building's.
        // ...those that came up short are a worry, said here; how many were made at what stops, below.
        lines.addAll(autoSaleHeadlines("The rollover", true));
        lines.addAll(autoSaleHeadlines("Automatic building", true));

        if (monthsOutOfLand > 0) {
            lines.add("No land to build on for " + monthsOutOfLand
                    + (monthsOutOfLand == 1 ? " month." : " months."));
        }

        if (monthsShortOfPower > 0) {
            lines.add(String.format(
                    "Short of power for %d month%s, down to %.0f%% at worst.",
                    monthsShortOfPower, monthsShortOfPower == 1 ? "" : "s",
                    worstEnergyRatio * 100));
        }

        if (monthsShortOfWater > 0) {
            lines.add("Short of water for " + monthsShortOfWater
                    + (monthsShortOfWater == 1 ? " month." : " months."));
        }

        if (monthsCongested > 0) {
            lines.add(String.format(
                    "Traffic was over capacity for %d month%s, down to %.0f%% throughput.",
                    monthsCongested, monthsCongested == 1 ? "" : "s",
                    worstRoadRatio * 100));
        }

        if (getBuildingsLost() > 0) {
            lines.add(getBuildingsLost() + " building"
                    + (getBuildingsLost() == 1 ? " was" : "s were")
                    + " demolished by their owners.");
        }

        if (defaultsWereNews()) {
            lines.add("Lenders wrote off debt that could not be repaid.");
        }

        if (monthsHouseholdsShort > 0) {
            lines.add("Households spent more than they earned in "
                    + monthsHouseholdsShort
                    + (monthsHouseholdsShort == 1 ? " month." : " months."));
        }

        if (shrankFromPeak()) {
            lines.add(String.format("Population peaked at %,d and ended at %,d.",
                    peakPopulation, after.population));
        }

        if (getIdleShare() > .5 && completed > 6) {
            lines.add(String.format("Nothing was being built for %.0f%% of the time.",
                    getIdleShare() * 100));
        }

        if (lines.isEmpty()) {
            lines.add("Nothing went wrong. The city grew without interruption.");
        }

        // The sales nobody pressed, each seller's count and stops (D28 to D30): news, not a worry.
        lines.addAll(autoSaleHeadlines("The rollover", false));
        lines.addAll(autoSaleHeadlines("Automatic building", false));

        return lines;
    }
}
