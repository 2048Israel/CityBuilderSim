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
 *   of power, how long it sat with no land, how often it had to issue emergency
 *   debt. These cannot be reconstructed from the endpoints, because a city that
 *   starves for fifty months and recovers looks identical at both ends to one
 *   that never had a problem.
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
        int population;
        int housing;
        int jobs;
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

    private double worstEnergyRatio = 1;
    private double worstRoadRatio = 1;
    private int peakPopulation;

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
        worstEnergyRatio = 1;
        worstRoadRatio = 1;
        peakPopulation = 0;

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
                         int population, int housing, int jobs,
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
                            boolean anythingUnderConstruction, int population) {
        sampleMonth(energyRatio, waterRatio, 1, landAvailableSqFt, householdsShort,
                anythingUnderConstruction, population);
    }

    /** @param roadRatio what congestion let the city actually get done that month. */
    public void sampleMonth(double energyRatio, double waterRatio, double roadRatio,
                            double landAvailableSqFt, boolean householdsShort,
                            boolean anythingUnderConstruction, int population) {
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
                            boolean anythingUnderConstruction, int population,
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
    public int getStartPopulation()     { return (before != null) ? before.population : 0; }
    public int getEndPopulation()       { return (after != null) ? after.population : 0; }
    public double getEndLandUtilisation(){ return (after != null) ? after.landUtilisation : 0; }
    public double getEndSavingRate()    { return (after != null) ? after.householdSavingRate : 0; }
    public double getEndRentBurden()    { return (after != null) ? after.householdRentBurden : 0; }
    public double getStartSavingRate()  { return (before != null) ? before.householdSavingRate : 0; }

    /** Debt the lenders wrote off during the skip. Not a figure either endpoint shows alone. */
    public double getWriteOffsDuringSkip() {
        return delta(a -> a.cumulativeWriteOffs);
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
    public int getPeakPopulation()         { return peakPopulation; }

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

        if (stoppedEarly()) {
            lines.add("Stopped after " + completed + " of " + requested
                    + " months - the treasury ran empty.");
        }

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

        if (getWriteOffsDuringSkip() > 0) {
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

        return lines;
    }
}
