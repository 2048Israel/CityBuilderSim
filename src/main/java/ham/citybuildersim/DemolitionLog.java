package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * What the city has lost, and when.
 *
 * Businesses can scrap capacity they cannot afford to hold, and until now that
 * happened in complete silence: the building count on the overview simply went
 * down between one month and the next, with nothing anywhere saying which
 * building went or why. A city that shrinks without telling you is a city you
 * cannot govern.
 *
 * Entries hang around for KEEP_MONTHS rather than one turn, because the whole
 * point is that the player may have been fast-forwarding and needs to see what
 * happened while they were not watching. Each one carries the month it happened
 * so the panel can say how long ago it was.
 */
public class DemolitionLog {

    /** How long a demolition stays on the panel. Two years of turns. */
    public static final int KEEP_MONTHS = 24;

    /** Hard cap, so a city demolishing constantly cannot grow this without limit. */
    private static final int MAX_ENTRIES = 40;

    public static class Entry {

        public final String building;
        public final int quantity;
        public final String sector;
        public final int month;

        /** What the city paid for the plot, or 0 if it was abandoned. */
        public final double proceeds;

        Entry(String building, int quantity, String sector, int month, double proceeds) {
            this.building = building;
            this.quantity = quantity;
            this.sector = sector;
            this.month = month;
            this.proceeds = proceeds;
        }

        public boolean wasPaidFor() {
            return proceeds > 0;
        }

        /** How long ago, in months. Never negative, however the clock moved. */
        public int monthsAgo(int currentMonth) {
            return Math.max(currentMonth - month, 0);
        }

        /** "this month", "last month", "3 months ago". */
        public String when(int currentMonth) {
            int ago = monthsAgo(currentMonth);
            if (ago <= 0) return "this month";
            if (ago == 1) return "last month";
            return ago + " months ago";
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    public void record(String building, int quantity, String sector,
                       int month, double proceeds) {

        if (building == null || quantity <= 0) {
            return;
        }

        entries.add(new Entry(building, quantity, sector, month, proceeds));

        while (entries.size() > MAX_ENTRIES) {
            entries.remove(0);
        }
    }

    /**
     * What to show right now: everything within KEEP_MONTHS, newest first.
     *
     * Filtered on read rather than pruned on write, so the list does not depend
     * on anything having called a tidy-up method at the right moment - a whole
     * class of bug this codebase has hit before with monthly clears.
     */
    public List<Entry> recent(int currentMonth) {

        List<Entry> visible = new ArrayList<>();

        for (Entry entry : entries) {
            if (entry.monthsAgo(currentMonth) <= KEEP_MONTHS) {
                visible.add(entry);
            }
        }

        Collections.reverse(visible);
        return visible;
    }

    /** Every entry ever kept, newest first. For a fuller history screen later. */
    public List<Entry> all() {
        List<Entry> copy = new ArrayList<>(entries);
        Collections.reverse(copy);
        return copy;
    }

    /**
     * Puts a saved log back, in the order it was written.
     *
     * The log is a record of things the city LOST, which is exactly the kind of
     * thing a player wants to still be there after a reload - and it was being
     * emptied by every load, silently, because it was never saved at all.
     * Entries are re-recorded rather than assigned so the cap and the ordering
     * stay the business of one method.
     */
    public void restore(List<Entry> saved) {
        clear();
        if (saved == null) return;
        for (int i = saved.size() - 1; i >= 0; i--) {
            Entry e = saved.get(i);
            if (e != null) {
                record(e.building, e.quantity, e.sector, e.month, e.proceeds);
            }
        }
    }

    public int size() {
        return entries.size();
    }

    public void clear() {
        entries.clear();
    }
}
