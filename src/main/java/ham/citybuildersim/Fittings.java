package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The fittings on site: pieces of equipment ordered for a standing building and not yet worked, first ordered first worked.
 *
 * WHY (R1b, 2026-11-05; docs/studies/research-design.md 2.2 step 5; Jerus: "equipment requires investment"). Before R1b a piece could be judged
 * (EquipmentFit) and never bought: the count of a stack's buildings still without it (BuildingsStacks.getUnfitted()) only ever went up or stayed.
 * A fitting is the builders' work on a building that is standing, so it is worked by their crews like the repairs are, off the top of the
 * month's output (Game.getBuildingOutput(): the repairs first, then the fittings, then the sites), and the building counts as fitted only when
 * the work is done: this is the list of what is waiting on those crews. One job is one building and one piece, with the points of work still
 * owed on it and who ordered it (a sector's key, or "City" for the player's own Fit), so that a sector fits one at a time and a save, which
 * writes the jobs, resumes them.
 *
 * Money is not kept here. What a fitting costs is paid at the order (Game.fit()) and the builders are paid then; this holds the work only.
 * A city with nothing unfitted never adds a job, so every method below does nothing in it.
 */
public final class Fittings {

    /**
     * One fitting on site, and the shape a save writes it in (DataSave.fittings; public fields for Gson, as BuildingManager.ContractRecord is).
     * pointsLeft is the only field that moves.
     */
    public static final class Job {
        /** The building template's id (BuildingsTemplate.getId()). */
        public int templateId;
        /** The piece's id on that template (Equipment.getId()). */
        public String pieceId;
        /** Who ordered it: a sector's key, or "City" (Game.getGovernmentInvestor()'s name). */
        public String payer;
        /** The BUILDING_WORK points still owed on it. */
        public double pointsLeft;

        public Job() { }

        Job(int templateId, String pieceId, String payer, double pointsLeft) {
            this.templateId = templateId;
            this.pieceId = pieceId;
            this.payer = payer;
            this.pointsLeft = pointsLeft;
        }

        Job copy() { return new Job(templateId, pieceId, payer, pointsLeft); }

        @Override
        public String toString() { return payer + ": " + pieceId + " on " + templateId + ", " + pointsLeft + " points left"; }
    }

    /** First ordered, first worked. */
    private final List<Job> jobs = new ArrayList<>();

    /** Puts one fitting on site: one building of the template, the piece, its payer and the points of work it owes (not below 0, and 0 for anything that is not a number). */
    public void add(int templateId, String pieceId, String payer, double points) {
        jobs.add(new Job(templateId, pieceId, payer, Double.isFinite(points) ? Math.max(0, points) : 0));
    }

    /** How many fittings of this piece are on site for this template (the buildings being fitted, not yet fitted). */
    public int onSite(int templateId, String pieceId) {
        int n = 0;
        for (Job j : jobs) if (j.templateId == templateId && j.pieceId.equals(pieceId)) n++;
        return n;
    }

    /** Whether this payer has a fitting on site: a sector fits one at a time. */
    public boolean hasOnSite(String payer) {
        for (Job j : jobs) if (j.payer.equals(payer)) return true;
        return false;
    }

    /** Whether nothing is on site. */
    public boolean isEmpty() { return jobs.isEmpty(); }

    /** The points of work still owed on every fitting on site. */
    public double pointsOwed() {
        double owed = 0;
        for (Job j : jobs) owed += j.pointsLeft;
        return owed;
    }

    /**
     * Works `points` off the jobs in the order placed: the first job's remainder first, then the next, and so on until the points are gone. A
     * job whose points reach nothing is finished and taken off the list; a job of no points finishes at the first call, whatever `points` is.
     *
     * @return the jobs that finished, in the order they were placed, no longer on site
     */
    public List<Job> work(double points) {
        List<Job> finished = new ArrayList<>();
        double left = Double.isFinite(points) ? Math.max(0, points) : 0;
        Iterator<Job> it = jobs.iterator();
        while (it.hasNext()) {
            Job j = it.next();
            double take = Math.min(left, j.pointsLeft);
            j.pointsLeft -= take;
            left -= take;
            if (j.pointsLeft > 0) break;
            it.remove();
            finished.add(j);
        }
        return finished;
    }

    /** Forgets every job: Game.buildWorld() calls it, as it clears the forms, so a city founded or loaded into a Game that played another starts with none on site. */
    public void clear() { jobs.clear(); }

    /** The jobs on site, in the order placed, as copies: for the save and for the harnesses. */
    public List<Job> view() {
        List<Job> out = new ArrayList<>(jobs.size());
        for (Job j : jobs) out.add(j.copy());
        return out;
    }

    /** Puts back the jobs a save carries (null or empty: none), replacing any on site; a job with no piece, no payer or points that are not a number is skipped. */
    public void restore(List<Job> saved) {
        jobs.clear();
        if (saved == null) return;
        for (Job j : saved) {
            if (j == null || j.pieceId == null || j.payer == null) continue;
            add(j.templateId, j.pieceId, j.payer, j.pointsLeft);
        }
    }
}
