package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ham.citybuildersim.sectors.Construction;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * Plays a city, saves it, loads the save into a second game, runs both a month
 * and compares what each would save: a reloaded city has to play its next
 * month as the city that kept running. Not part of the game.
 *
 * WHY THIS EXISTS. 0.7.46 (A1) cut the series that differ a month after a
 * reload from 62 of 226 to 12 of 229, and the residual was left (todo-archive,
 * star 2). Nothing compared a reloaded city with its running twin past the
 * moment of the load: LongPlaytest's roundTrip() compares at the load only,
 * and SaveFileCheck 14 plays fixture cities on, not a played city's whole
 * save. FW6 measured it with a scratch tool - the probe city saved at a month,
 * loaded into a second Game, both run a month, both saved, the two saves'
 * JSON compared leaf by leaf. Every history series matched for 24 months; the
 * save did not: each sector book's stockRevalued and stockCounted (the month
 * after a load counts no revaluation), Construction's drawnImported and
 * drawnLocal, and the lastInvestment notes (the first investment pass after a
 * load weighs a different candidate, because the markets' month tally is not
 * saved). This is that tool as a check, so the next change that lets a city
 * play differently after a load is a red line and not a month nobody compared.
 *
 * WHAT IT PROVES, on the probe city (Danzik on the flag's preset, played by
 * LongPlaytest.probePlay) saved at about months 30, 120, 360 and 1200, each time
 * loaded into a second Game, with both run a month and then another:
 *
 *   1. EVERY HISTORY SERIES of the reloaded city is the running city's, a
 *      month on and two months on.
 *   2. ...AND EVERY FIGURE IN ITS SAVE, to the cent, a month on and two on
 *      (the map sidecar's stamp among them since CC1-M).
 *   3. THE CAUSES, ONE BY ONE, so a red section 2 says why: (a) every
 *      sector's stock revaluation is counted in the reload's first month and
 *      equals the live city's; (b) Construction's materials drawn at home and
 *      imported; (c) every market's month tally right after the load, and the
 *      investment notes a month on; (d) each sector book's shares right after
 *      the load; (e) the price index's rent and food weights right after the
 *      load; (f) a time skip reports what the running city's skip reports;
 *      (g) the sudden stop's words and the hot money's peaks; (h) Quarrying's
 *      carry; (i) each market's draws since the month closed, and the seller
 *      rows they put back a month on; (j) a seller-priced market's trades of a
 *      month are off its list the month after.
 *   4. THE REAL EARTH: sections 1 and 2 at one checkpoint (about month 120)
 *      on LongPlaytest.probeCity(files, "rotterdam"), where earth-2.bin is
 *      named; a line saying it did not run, never a FAIL, where it is not.
 *   5. THE AUTOSAVE (FB3, REV-FB3-1): the live city played on to the month its
 *      own autosave falls due in, written by the game at that month's end, and
 *      loaded from the autosave slot: right after the load every market's draws
 *      are the live city's, and a month on every history figure and every
 *      figure in the save is. A save carries the draws since FW6-C, and the
 *      autosave was taken before the month closed them, so a load put the
 *      month's draws back and the next month filled them again.
 *
 * Every fixture is asserted: the history holds more than a hundred series and
 * the save more than ten thousand numbers (so the comparison is not empty),
 * and each cause is shown at work in the live city before its parity is.
 *
 * WHY FOUR CHECKPOINTS AND NOT THREE: a cause shows only in the states that
 * exercise it. The investment notes, for one, came out the same after a reload
 * at months 35 and 365 of this city and different at 123 and 1205 (Heavy
 * Industry weighing an Electric Arc Furnace on the live city's tally and a
 * Blast Furnace on an empty one), so a harness with the first two alone would
 * have passed a bug it was written to find. The months are the probe's own
 * (probePlay's skips are fixed by its stop count), so they do not move unless
 * LongPlaytest does; a fixture line says each cause was seen at work.
 *
 * FS17 (the flaky-suite sweep): section 3's causes are no longer only found. The caused point (the live city as it stands after
 * its last checkpoint) has, by hand, a market's tally set (GoodsMarket.restoreTally), the good the hand order draws re-priced
 * (setLocalPrice), a build order placed (Game.buildStack, so the crews draw materials in the month on), the sudden stop started
 * and the carry placed every time (not only when no checkpoint had one); and a "by hand" fixture line asserts that the caused
 * point's OWN city showed each of the causes it set, whatever the checkpoints showed. FS17, second slice: the caused point has too,
 * by hand, an outbreak in force on both twins before the skips (Health.restore, the severity between OUTBREAK_MIN_PEAK and
 * OUTBREAK_MAX_PEAK and so far over OUTBREAK_FLOOR that it outlives both skips: 3f, and a fixture line that the caused point's own
 * skips had an outbreak and months sick), one sector's shares for this month and last month put on the books (SectorBooks.restoreShares,
 * every slot of the record a figure of its own: 3d), and a trade placed on every seller-priced market's list after the first month on,
 * on both twins (GoodsMarket.record: 3j, so the month that must clear it has one to clear, building work's market included). The
 * investment notes (3c) are read on the caused point's own month on: the investors decide in every month (Game.runPrivateInvestment()),
 * with the tally set by hand on a market. The index's weights (3e) are no longer found: every point is asserted past
 * PriceIndex.SETTLING_MONTHS and its index based. What stays played, and is found in the checkpoints (the hot money's peaks, the
 * seller-priced markets' sales that the played months made): no model setter puts the peaks there between the presses.
 *
 * @author Jerus
 */
public class ReloadCheck {

    static int fails = 0;

    /** The months the live city is saved at; LongPlaytest.probePlay overshoots by a few, so a point reads g.getMonth(). */
    static final int[] CHECKPOINTS = { 30, 120, 360, 1200 };

    /** The month the Earth city is saved at (section 4). */
    static final int EARTH_CHECKPOINT = 120;

    /** Where section 4's city is founded: the real Earth round Rotterdam, as LongPlaytest --earth rotterdam plays it. */
    static final String EARTH_SPOT = "rotterdam";

    /** The words of the sudden stop the harness starts on the live city before its caused point (FB3, FW6-C): no checkpoint of the probe city is in a stop, and the words ride in a String no array carries. */
    static final String CAUSED_STOP = "the reload check's own sudden stop";

    /** The share of what the makers of one good hold that the harness's hand order between the presses takes (FB3, FW6-C): part of it, so the order is all filled at home and none imported. */
    static final double HAND_ORDER_SHARE = .5;

    /** What the carry's fixture counts (FB3, FW6-C). */
    static final String CARRY_SAW = "cities that carried limestone into aggregate";

    /** The tonnes of limestone the harness places as a carry on the caused point (FB3, FW6-C; every time since FS17, not only when no checkpoint's city had one). */
    static final double CAUSED_CARRY = 100;

    /** The month's tally the harness puts on one market of the caused point (FS17): the goods imported, exported, filled at home, offered, bid for and drawn since the strike, each above zero and none the same. */
    static final double[] CAUSED_TALLY = { 120, 80, 300, 700, 650, 40 };

    /** How far the harness moves the price of the good its hand order draws, between the month's close and the next (FS17): so the makers' stock of it is revalued by a figure that is not zero. */
    static final double CAUSED_PRICE_MOVE = 1.25;

    /** The Houses worked before the caused point (FS17, R1g): their materials are reported at the next month's strike. */
    static final int CAUSED_ORDER = 5;

    /** The name of the caused point's city, which the by-hand fixtures look it up by (FS17). */
    static final String CAUSED_CITY = "Danzik, caused";

    /** The seller-priced goods no sector sells through its market (FB3, FW6-C): building work is the crews' own points, so its list is empty at every checkpoint and no fixture can ask that it sold. */
    static final Set<Good> NEVER_FILLED = Set.of(Good.BUILDING_WORK);

    /** A time skip of section 3f: two months, so it is a skip (the game autosaves before one), and two are run on each city. */
    static final int SKIP_MONTHS = 2;

    /** The outbreak the harness puts in force on both twins of the caused point before their skips (FS17): the middle of the model's peaks, which Health's decay leaves over its floor through both skips, so every skipped month is one in outbreak. */
    static final double CAUSED_OUTBREAK = (Health.OUTBREAK_MIN_PEAK + Health.OUTBREAK_MAX_PEAK) / 2;

    /** The shares the harness puts on one sector's book of the caused point for this month (FS17): each slot of SectorBooks.SHARES_SLOTS a figure of its own (shares, outstanding, price, fair value, traded, dividend, dividend a year), so a load that crossed two of them shows. */
    static final double[] CAUSED_SHARES = { 1200, 480, 13.5, 15.25, 1, .75, 3.25 };

    /** ...and for last month, every slot different from this month's, and not traded (FS17). */
    static final double[] CAUSED_SHARES_BEFORE = { 1100, 450, 12.5, 14.25, 0, .5, 3 };

    /** The units and the price of the trade the harness places by hand on each seller-priced market's list after the first month on (FS17), so the second month has a trade of the first to clear. */
    static final double CAUSED_TRADE_UNITS = 1, CAUSED_TRADE_PRICE = 1;

    /** Two numbers are the same when they differ by no more than a cent... */
    static final double CENT = 0.005;

    /** ...and by no more than a billionth of the larger (or of 1, for the tiny). */
    static final double RELATIVE = 1e-9;

    /**
     * The keys no two saves of one city share, whatever it did: the slot's name and the time it was written. Skipped wherever
     * they stand in the tree. The map sidecar's stamp (mapStamp) was exempt too until batch CC1-M, because the first month after a
     * load re-laid the railway; it is compared since, like every figure: a map that moves after a load is a red line here (and in
     * GatewayCheck 21, which causes it on a coast).
     */
    static final Set<String> EXEMPT = Set.of("slotName", "savedAt");

    /** How many differing paths a failing line lists, each with both values; the rest is counted. */
    static final int SHOWN = 12;

    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    /* ----------------------------- the findings ----------------------------- */

    /**
     * What a comparison found: how many things differ, in how many kinds of place, and the first SHOWN of them, each
     * with both values. A thing's kind is what is left of its name once the one it belongs to is taken off: a path with
     * its indexes masked (.sectorBooks[*].stockCounted), or the words after the colon of "Retail: stockCounted".
     */
    static class Findings {
        int differing;
        final List<String> first = new ArrayList<>();
        final Map<String, Integer> kinds = new LinkedHashMap<>();

        void differ(String what, String live, String reloaded) {
            differing++;
            int colon = what.indexOf(": ");
            kinds.merge(colon >= 0 ? what.substring(colon + 2) : what.replaceAll("\\[\\d+\\]", "[*]"), 1, Integer::sum);
            if (first.size() < SHOWN) first.add(what + "  live " + live + "  reloaded " + reloaded);
        }

        /** The kinds, the first few with how many of each: what a failing line says before the list. */
        String kindsLine() {
            StringBuilder s = new StringBuilder(differing + " differ, in " + kinds.size() + (kinds.size() == 1 ? " kind: " : " kinds: "));
            int n = 0;
            for (Map.Entry<String, Integer> e : kinds.entrySet()) {
                if (n++ == 4) { s.append(" (+").append(kinds.size() - 4).append(" more)"); break; }
                s.append(n == 1 ? "" : ", ").append(e.getKey()).append(" x").append(e.getValue());
            }
            return s.toString();
        }
    }

    /** Two saves' files compared leaf by leaf: how many numbers and how many series (arrays of numbers) were compared. */
    static final class Compared extends Findings {
        int numbers, series;
    }

    /** One cause (section 3): what was compared, and what the live city showed the cause at work in (the fixture). */
    static final class Cause extends Findings {
        int compared;
        final Map<String, Integer> saw = new LinkedHashMap<>();

        void saw(String what, int n) { saw.merge(what, n, Integer::sum); }

        void compare(String what, double live, double reloaded) {
            compared++;
            if (!same(live, reloaded)) differ(what, num(live), num(reloaded));
        }

        void compare(String what, String live, String reloaded) {
            compared++;
            if (!java.util.Objects.equals(live, reloaded)) differ(what, String.valueOf(live), String.valueOf(reloaded));
        }
    }

    /** What one checkpoint found: the city saved at {@code month}, reloaded, and both played on. */
    static final class Point {
        final String city;
        final int month;
        /** Why the checkpoint could not be compared (a save or a load that failed), or null. */
        String problem;
        Compared history1, history2, save1, save2;
        final Cause revalued = new Cause(), drawn = new Cause(), tally = new Cause(), notes = new Cause(),
                shares = new Cause(), weights = new Cause(), skipped = new Cause(),
                stop = new Cause(), carry = new Cause(), draws = new Cause(), sellerPriced = new Cause();
        int skips, skipsWithOutbreak, skipsSick;
        /** The hand order the harness made on the live city before this point (FB3, FW6-C), or null: a point with one is the caused point. */
        HandOrder order;

        Point(String city, int month) { this.city = city; this.month = month; }
    }

    /** A hand order made between the presses (FB3, FW6-C): the good and the draws it logged, one a seller, with the units taken from its stock. */
    record HandOrder(Good good, List<Trade> rows) { }

    /* --------------------------------- the numbers --------------------------------- */

    static boolean same(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return Double.isNaN(a) && Double.isNaN(b);
        if (a == b) return true;
        double d = Math.abs(a - b);
        return d <= CENT && d <= RELATIVE * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static String num(double v) { return Double.isNaN(v) ? "NaN" : String.valueOf(v); }

    /* ------------------------------- the two files ------------------------------- */

    static JsonElement read(Path file) {
        try {
            return Files.isRegularFile(file) ? JsonParser.parseString(Files.readString(file)) : null;
        } catch (IOException e) {
            return null;
        }
    }

    /** A number, or the strings Gson writes for the ones JSON has no word for. */
    static boolean isNumeric(JsonElement e) {
        if (e == null || !e.isJsonPrimitive()) return false;
        if (e.getAsJsonPrimitive().isNumber()) return true;
        String s = e.getAsString();
        return e.getAsJsonPrimitive().isString() && (s.equals("NaN") || s.equals("Infinity") || s.equals("-Infinity"));
    }

    static double valueOf(JsonElement e) { return e.getAsJsonPrimitive().isNumber() ? e.getAsDouble() : Double.parseDouble(e.getAsString()); }

    static String show(JsonElement e) {
        if (e == null) return "(absent)";
        String s = e.toString();
        return s.length() > 60 ? s.substring(0, 60) + "..." : s;
    }

    /** The live city's file against the reloaded city's, key by key and leaf by leaf; null (a file not there) is itself a difference. */
    static Compared compare(Path live, Path reloaded) {
        Compared c = new Compared();
        JsonElement a = read(live), b = read(reloaded);
        if (a == null || b == null) c.differ(live.getFileName() + " / " + reloaded.getFileName(), a == null ? "(not there)" : "read", b == null ? "(not there)" : "read");
        else walk("", a, b, c);
        return c;
    }

    static void walk(String path, JsonElement a, JsonElement b, Compared c) {
        if (a == null || b == null) {
            if (a != b) c.differ(path, show(a), show(b));
        } else if (a.isJsonObject() && b.isJsonObject()) {
            JsonObject x = a.getAsJsonObject(), y = b.getAsJsonObject();
            Set<String> keys = new TreeSet<>(x.keySet());
            keys.addAll(y.keySet());
            for (String k : keys) {
                if (!EXEMPT.contains(k)) walk(path + "." + k, x.get(k), y.get(k), c);
            }
        } else if (a.isJsonArray() && b.isJsonArray()) {
            JsonArray x = a.getAsJsonArray(), y = b.getAsJsonArray();
            if (x.size() > 0 && isNumeric(x.get(0))) c.series++;
            if (x.size() != y.size()) c.differ(path + ".length", String.valueOf(x.size()), String.valueOf(y.size()));
            for (int i = 0; i < Math.min(x.size(), y.size()); i++) walk(path + "[" + i + "]", x.get(i), y.get(i), c);
        } else if (isNumeric(a) && isNumeric(b)) {
            c.numbers++;
            if (!same(valueOf(a), valueOf(b))) c.differ(path, a.getAsString(), b.getAsString());
        } else if (!a.equals(b)) {
            c.differ(path, show(a), show(b));
        }
    }

    /* ---------------------------- the caused point ---------------------------- */

    /**
     * FB3 (FW6-C): three restores that no checkpoint's save causes the case of, caused on the live city as it stands (between the
     * presses, a month just closed): a sudden stop begun (the words are a String no array carries); a hand order, half of what the
     * makers of the good they hold the most of have in stock, drawn as the player's orders draw (Game.drawMaterials(), drawBitumen(),
     * fit(): the builders the buyer, their purchase booked), the seller rows of which only the draws list can put back; and,
     * when no checkpoint's city carried any, a carry of limestone placed. Returns the hand order (null if no maker holds any
     * stock of a traded good the builders do not make).
     */
    static HandOrder causeTheConditions(Game g, boolean placeACarry) {
        causeBuildingDraw(g);
        g.getCapitalFlows().startStopForTest(CAUSED_STOP);
        if (placeACarry) g.getSectors().quarrying().setCarriedForTest(CAUSED_CARRY);
        Sector builders = g.getSectors().construction();
        Good most = null;
        double mostHeld = 0;
        for (Good good : Good.values()) {
            if (!good.traded()) continue;
            if (builders.isMaker(good)) continue;   // the builders buy it, so not from themselves
            double held = 0;
            for (Sector s : g.getSectors().all()) if (s.isMaker(good)) held += s.getStock(good);
            if (held > mostHeld) { most = good; mostHeld = held; }
        }
        /*
         * FS17: THE CAUSES THE CHECKPOINTS ONLY SHOWED, SET BY HAND. A market's month tally (all six figures above zero: the checkpoints'
         * markets showed imports, exports and units drawn only where the city's month happened to leave them), the price of the good the
         * order draws moved (the makers' stock of it is then revalued by a figure that is not zero), and a build order placed (the crews
         * draw materials for it in the month on).
         *
         * R1g (2026-11-21): THE ORDER IS PLACED AS THE SCREENS PLACE ONE (LongPlaytest.build(): the ground bought first when the city has too
         * little), ON BUILDERS WHO HOLD NOTHING TO BUILD IT WITH. Since R1g the probe city is founded the 1900 way: at its caused point on
         * 0.9.176 it had no ground for five Houses (buildStack() answered NO_LAND and no site stood), and where it has the ground its builders
         * hold the public works yard's months (7,445 units at month 1211 on 0.9.177; Game.drawMaterials() draws it first) and the salvage of
         * its scrapped plant (1,253.5 t, drawn next): the order took its materials from the yard as it was placed, its Houses opened within
         * the month, and the month on bought none (fixture 3b red). Today's city's builders, building every month, hold neither at the
         * caused point, so an order's materials fall due on the sites as they work. Taken here through the model's own
         * calls (BuildingManager.takeFromYard(), Construction.takeSalvage(), which books the salvage's cost as it would on a site), the
         * caused city's builders stand as those do, whichever way the city was founded. The order is worked one month before the save:
         * Construction.beforeBank() reports the month just closed, so an order placed immediately before saving has no draw on the next
         * strike even where that new month works its sites. causeBuildingDraw() runs first, before the other between-press conditions.
         */
        if (most != null) {
            GoodsMarket priced = g.getMarkets().get(most);
            priced.setLocalPrice(priced.getLocalPrice() * CAUSED_PRICE_MOVE);
        }
        for (GoodsMarket m : g.getMarkets().all()) {
            if (!m.good().traded() || m.good() == most) continue;
            m.restoreTally(CAUSED_TALLY[0], CAUSED_TALLY[1], CAUSED_TALLY[2], CAUSED_TALLY[3], CAUSED_TALLY[4], CAUSED_TALLY[5]);
            break;
        }
        // FS17: ...and one sector's shares, this month's and last month's, put on the books by the model's own restore (SectorBooks.restoreShares,
        // the load's path), the others as they stand: the shares of a played month are found in the checkpoints only where a company happened to be listed.
        Sector sharer = g.getSectors().all().get(0);
        Map<String, double[]> sharesNow = g.getSectorBooks().sharesToSave(true), sharesBefore = g.getSectorBooks().sharesToSave(false);
        sharesNow.put(sharer.key(), CAUSED_SHARES.clone());
        sharesBefore.put(sharer.key(), CAUSED_SHARES_BEFORE.clone());
        g.getSectorBooks().restoreShares(sharesNow, sharesBefore);
        if (most == null) return null;
        GoodsMarket market = g.getMarkets().get(most);
        int before = market.monthsDraws().size();
        g.getMarkets().draw(most, builders, null, mostHeld * HAND_ORDER_SHARE, g.getSectors());
        return new HandOrder(most, new ArrayList<>(market.monthsDraws().subList(before, market.monthsDraws().size())));
    }

    /** Work the order before the checkpoint: the next strike reports the materials drawn in this month, not the one it starts. */
    private static void causeBuildingDraw(Game g) {
        PrintStream real = System.out, report = LongPlaytest.out;
        System.setOut(QUIET);
        LongPlaytest.out = QUIET;
        try {
            g.getBuildingManager().takeFromYard(g.getBuildingManager().getConstructionMaterials());
            g.getSectors().construction().takeSalvage(g.getSectors().construction().getSalvage());
            // The played treasury may be insolvent; its advisor then refuses even to borrow. This fixture needs a placed order,
            // so give it the model's wealthy founding cash before the normal ground purchase and order, as the tally is caused above.
            g.setCashForTest(Math.max(g.getCash(), Founding.WEALTHY_CASH));
            if (!LongPlaytest.build(g, "House", CAUSED_ORDER)) throw new IllegalStateException("the caused House order was refused");
            g.simulateMonths(1);
        } finally { LongPlaytest.out = report; System.setOut(real); }
    }

    /* ------------------------------ one checkpoint ------------------------------ */

    /**
     * The city {@code g} saved where it stands and loaded into a second game, observed right after the load (the
     * markets' tally, the shares, the price index's weights), then both run a month (the sector books, Construction,
     * the investment notes), saved and compared, a month more and compared again, then two time skips of each.
     * The game's narration is silenced throughout; nothing here asserts - section by section the report does.
     */
    static Point reload(GameFiles files, Game g, String city) { return reload(files, g, city, null); }

    /** ...with the hand order the harness made on the live city before it, if any (FB3, FW6-C), so a month on can read the seller rows it put back. */
    static Point reload(GameFiles files, Game g, String city, HandOrder order) {
        Point p = new Point(city, g.getMonth());
        p.order = order;
        PrintStream real = System.out;
        System.setOut(QUIET);
        try {
            if (!g.saveGame(1, "reloadcheck m" + p.month).ok) { p.problem = "the save of the live city failed"; return p; }
            Game back = new Game(files);
            back.loadGameSave(1);
            if (back.getLoadFailure() != null) { p.problem = "the load failed: " + back.getLoadFailure(); return p; }

            observeTheLoad(p, g, back);
            g.simulateMonths(1);
            back.simulateMonths(1);
            observeAMonthOn(p, g, back);
            Map<Good, Set<Trade>> firstMonthsTrades = sellerPricedTrades(p, g);
            Compared[] monthOn = saveAndCompare(files, g, back, "a month on");
            p.history1 = monthOn[0];
            p.save1 = monthOn[1];
            placeATradeByHand(p, g, back, firstMonthsTrades);   // FS17
            g.simulateMonths(1);
            back.simulateMonths(1);
            tradesOfTheFirstMonthGone(p, g, firstMonthsTrades);
            Compared[] twoOn = saveAndCompare(files, g, back, "two months on");
            p.history2 = twoOn[0];
            p.save2 = twoOn[1];
            skips(p, g, back);
        } finally {
            System.setOut(real);
        }
        return p;
    }

    /** Both cities saved (the live one to slot 2, the reloaded to slot 3) and their files compared: {the history's, the save's}. */
    private static Compared[] saveAndCompare(GameFiles files, Game g, Game back, String when) {
        boolean ok = g.saveGame(2, "live " + when).ok & back.saveGame(3, "reloaded " + when).ok;
        Compared[] both = { compare(files.historyFile(2), files.historyFile(3)), compare(files.saveFile(2), files.saveFile(3)) };
        if (!ok) for (Compared c : both) c.differ("saving " + when, "a save failed", "a save failed");
        return both;
    }

    /**
     * Right after the load, before any month: (c) every market's tally of the month just closed - the goods imported,
     * exported, filled at home, offered and bid for, and the units drawn since (FB3, FW6-C); the trades list has no getter
     * that is a figure, so it is not compared - (d) each sector book's shares, this month's and last month's, (e) the
     * price index's two weights, and since FB3 (FW6-C) the restores no checkpoint's save used to cause the case of: (g) the
     * sudden stop's words and the hot money's peaks, (h) Quarrying's carry, which produceFlow() zeroes at the top of the next
     * month so only here can it be read, and (i) every market's draws since the month closed, row by row.
     */
    private static void observeTheLoad(Point p, Game g, Game back) {
        for (GoodsMarket live : g.getMarkets().all()) {
            GoodsMarket re = back.getMarkets().get(live.good());
            String good = String.valueOf(live.good());
            p.tally.saw("markets that imported", live.getImported() > 0 ? 1 : 0);
            p.tally.saw("markets that exported", live.getExported() > 0 ? 1 : 0);
            p.tally.compare(good + ": imported", live.getImported(), re == null ? Double.NaN : re.getImported());
            p.tally.compare(good + ": exported", live.getExported(), re == null ? Double.NaN : re.getExported());
            p.tally.compare(good + ": filled at home", live.getLocalFilled(), re == null ? Double.NaN : re.getLocalFilled());
            p.tally.compare(good + ": offered", live.getOffered(), re == null ? Double.NaN : re.getOffered());
            p.tally.compare(good + ": bid", live.getBid(), re == null ? Double.NaN : re.getBid());
            // FB3 (FW6-C): the units drawn on demand since the strike, which the next strike counts as demand; no harness compared them.
            p.tally.saw("markets with units drawn since the strike", live.getDrawn() > 0 ? 1 : 0);
            // FS17: ...and the one market that carries exactly the tally the harness set on the caused point, which no played month leaves: the cause by hand, told from the found ones.
            p.tally.saw("markets carrying the tally the harness set", live.getImported() == CAUSED_TALLY[0] && live.getExported() == CAUSED_TALLY[1]
                    && live.getLocalFilled() == CAUSED_TALLY[2] && live.getOffered() == CAUSED_TALLY[3] && live.getBid() == CAUSED_TALLY[4]
                    && live.getDrawn() == CAUSED_TALLY[5] ? 1 : 0);
            p.tally.compare(good + ": drawn since the strike", live.getDrawn(), re == null ? Double.NaN : re.getDrawn());
            compareDraws(p.draws, good, live.monthsDraws(), re == null ? List.of() : re.monthsDraws());
        }
        CapitalFlows hot = g.getCapitalFlows(), hotBack = back.getCapitalFlows();
        p.stop.saw("cities in a sudden stop", hot.isStopped() ? 1 : 0);
        p.stop.saw("...of them with the stop's words", hot.isStopped() && !hot.getStopReason().isBlank() ? 1 : 0);
        p.stop.saw("cities with a hot money peak", hot.getPeakStock() > 0 ? 1 : 0);
        p.stop.saw("...of them with the stock below its peak", hot.getPeakStock() > hot.getStock() ? 1 : 0);
        p.stop.saw("cities with a widest spread", hot.getPeakSpread() > 0 ? 1 : 0);
        p.stop.compare("the sudden stop: its words", hot.getStopReason(), hotBack.getStopReason());
        p.stop.compare("the sudden stop: isStopped", String.valueOf(hot.isStopped()), String.valueOf(hotBack.isStopped()));
        p.stop.compare("the sudden stop: months left", hot.stopMonthsLeft(), hotBack.stopMonthsLeft());
        p.stop.compare("the hot money: the most that was ever here", hot.getPeakStock(), hotBack.getPeakStock());
        p.stop.compare("the hot money: the widest the gap ever got", hot.getPeakSpread(), hotBack.getPeakSpread());
        p.carry.saw(CARRY_SAW, g.getSectors().quarrying().carried() > 0 ? 1 : 0);
        p.carry.compare("Quarrying: the limestone carried", g.getSectors().quarrying().carried(), back.getSectors().quarrying().carried());
        for (Sector s : g.getSectors().all()) {
            String key = s.key();
            p.shares.saw("sectors with shares this month", g.getSectorBooks().shares(key) != null ? 1 : 0);
            p.shares.saw("sectors with shares last month", g.getSectorBooks().sharesBefore(key) != null ? 1 : 0);
            // FS17: ...and the sector that carries the record the harness put on the caused point's books, this month and last, which no played month holds.
            p.shares.saw("sectors carrying the shares the harness set, this month", shareRecord(CAUSED_SHARES).equals(g.getSectorBooks().shares(key)) ? 1 : 0);
            p.shares.saw("sectors carrying the shares the harness set, last month", shareRecord(CAUSED_SHARES_BEFORE).equals(g.getSectorBooks().sharesBefore(key)) ? 1 : 0);
            compareShares(p.shares, key + " this month", g.getSectorBooks().shares(key), back.getSectorBooks().shares(key));
            compareShares(p.shares, key + " last month", g.getSectorBooks().sharesBefore(key), back.getSectorBooks().sharesBefore(key));
        }
        p.weights.saw("weights above zero", g.getPriceIndex().getRentWeight() > 0 && g.getPriceIndex().getFoodWeight() > 0 ? 1 : 0);
        // FS17: ...and whether the index is based, which section 3 asserts of every point against PriceIndex.SETTLING_MONTHS (the weights of a based basket are the spending's, not the .5 and .5 a new one starts on).
        p.weights.saw("indexes based", g.getPriceIndex().isBased() ? 1 : 0);
        p.weights.compare("the price index: rent weight", g.getPriceIndex().getRentWeight(), back.getPriceIndex().getRentWeight());
        p.weights.compare("the price index: food weight", g.getPriceIndex().getFoodWeight(), back.getPriceIndex().getFoodWeight());
    }

    /** (i) One market's draws since the month closed, the live city's against the reloaded city's: how many, and each one's seller, buyer, units and price. */
    private static void compareDraws(Cause c, String good, List<Trade> live, List<Trade> re) {
        c.saw("markets with draws since the month closed", live.isEmpty() ? 0 : 1);
        c.compare(good + ": draws since the month closed", live.size(), re.size());
        for (int i = 0; i < Math.min(live.size(), re.size()); i++) {
            Trade a = live.get(i), b = re.get(i);
            String row = good + ": draw " + i;
            c.compare(row + ", seller", a.seller(), b.seller());
            c.compare(row + ", buyer", a.buyer(), b.buyer());
            c.compare(row + ", units", a.units(), b.units());
            c.compare(row + ", price", a.price(), b.price());
        }
    }

    /** FS17: a record of the books' shares as SectorBooks.sharesToSave() lays it out in its slots. */
    private static SectorBooks.Shares shareRecord(double[] a) {
        return new SectorBooks.Shares(a[0], a[1], a[2], a[3], a[4] != 0, a[5], a[6]);
    }

    private static void compareShares(Cause c, String what, SectorBooks.Shares live, SectorBooks.Shares re) {
        if (live == null || re == null) {
            c.compared++;
            if (live != re) c.differ(what + ": shares", live == null ? "not counted" : live.toString(), re == null ? "not counted" : re.toString());
            return;
        }
        c.compare(what + ": shares", live.shares(), re.shares());
        c.compare(what + ": outstanding", live.outstanding(), re.outstanding());
        c.compare(what + ": price", live.price(), re.price());
        c.compare(what + ": fair value", live.fair(), re.fair());
        c.compare(what + ": dividend", live.dividend(), re.dividend());
        c.compare(what + ": dividend a year", live.dividendYear(), re.dividendYear());
        c.compare(what + ": traded", String.valueOf(live.traded()), String.valueOf(re.traded()));
    }

    /**
     * A month on: (a) each sector book's stock revaluation and whether it was counted, (b) Construction's materials
     * drawn at home and imported in the month last struck, and (c) the investment notes the investors left.
     */
    private static void observeAMonthOn(Point p, Game g, Game back) {
        for (Sector s : g.getSectors().all()) {
            SectorBooks.SectorMonth live = g.getSectorBooks().get(s.key()), re = back.getSectorBooks().get(s.key());
            if (live == null || re == null) {
                p.revalued.compared++;
                if (live != re) p.revalued.differ(s.key() + ": its month", live == null ? "no month" : "a month", re == null ? "no month" : "a month");
                continue;
            }
            p.revalued.saw("sectors that counted a revaluation", live.stockCounted() ? 1 : 0);
            p.revalued.saw("...of them one not zero", live.stockCounted() && live.stockRevalued() != 0 ? 1 : 0);
            p.revalued.compare(s.key() + ": stockCounted", String.valueOf(live.stockCounted()), String.valueOf(re.stockCounted()));
            p.revalued.compare(s.key() + ": stockRevalued", SectorStatements.stockRevalued(live), SectorStatements.stockRevalued(re));
        }
        Construction live = g.getSectors().construction(), re = back.getSectors().construction();
        p.drawn.saw("crews that drew materials", live.getDrawnLocal() + live.getDrawnImported() > 0 ? 1 : 0);
        p.drawn.compare("Construction: isDrawnKnown", String.valueOf(live.isDrawnKnown()), String.valueOf(re.isDrawnKnown()));
        p.drawn.compare("Construction: drawn at home", live.getDrawnLocal(), re.getDrawnLocal());
        p.drawn.compare("Construction: drawn imported", live.getDrawnImported(), re.getDrawnImported());

        // (i) The hand order between the presses, a month on (FB3, FW6-C): the month's strike clears every row and puts the draws back on their sellers',
        // so a seller's units sold at home hold the order on the live city, and on the reloaded one only if the draws crossed the save.
        if (p.order != null) {
            for (Trade t : p.order.rows()) {
                if (t.isImport()) continue;
                double liveSold = soldAtHome(g.getSectors().byKey(t.seller()), t.good()), reSold = soldAtHome(back.getSectors().byKey(t.seller()), t.good());
                p.draws.saw("hand-ordered seller rows that hold the order a month on", liveSold >= t.units() ? 1 : 0);
                p.draws.compare(t.seller() + " " + t.good() + ": units sold at home a month on", liveSold, reSold);
            }
        }

        Map<String, String> liveNotes = g.getLastInvestments(), reNotes = back.getLastInvestments();
        p.notes.saw("investment notes the live city left", liveNotes.size());
        Set<String> keys = new TreeSet<>(liveNotes.keySet());
        keys.addAll(reNotes.keySet());
        for (String k : keys) p.notes.compare(k + ": lastInvestment", liveNotes.get(k), reNotes.get(k));
    }

    private static double soldAtHome(Sector s, Good good) {
        Sector.Output row = s == null ? null : s.outputRow(good);
        return row == null ? 0 : row.soldLocal;
    }

    /**
     * (j) Every seller-priced market's trades as the live city has them a month on (FB3, FW6-C), kept by object: Trade is a
     * record, so a set that asked equals() would call this month's sale the last one's. The markets that sold at all are counted.
     */
    private static Map<Good, Set<Trade>> sellerPricedTrades(Point p, Game g) {
        Map<Good, Set<Trade>> month = new LinkedHashMap<>();
        for (GoodsMarket m : g.getMarkets().all()) {
            if (m.good().traded()) continue;
            Set<Trade> these = Collections.newSetFromMap(new IdentityHashMap<>());
            these.addAll(m.getTrades());
            month.put(m.good(), these);
            if (!NEVER_FILLED.contains(m.good())) p.sellerPriced.saw(m.good() + " markets that sold in the first month on", these.isEmpty() ? 0 : 1);
        }
        return month;
    }

    /**
     * FS17: on the caused point, after the first month on's saves are compared and before the second month, a trade placed by hand on every seller-priced
     * market's list, on both twins (GoodsMarket.record, the model's own fill), and kept with the first month's trades: so the month that must clear them
     * has one on each, building work's market (which no sector fills) too. The played month's trades are counted as they were, above.
     */
    private static void placeATradeByHand(Point p, Game g, Game back, Map<Good, Set<Trade>> firstMonth) {
        if (!CAUSED_CITY.equals(p.city)) return;
        String seller = g.getSectors().construction().key();
        for (Map.Entry<Good, Set<Trade>> e : firstMonth.entrySet()) {
            GoodsMarket m = g.getMarkets().get(e.getKey());
            Trade mine = m.record(seller, Trade.HOUSEHOLDS, CAUSED_TRADE_UNITS, CAUSED_TRADE_PRICE);
            back.getMarkets().get(e.getKey()).record(seller, Trade.HOUSEHOLDS, CAUSED_TRADE_UNITS, CAUSED_TRADE_PRICE);
            if (mine != null) e.getValue().add(mine);
            p.sellerPriced.saw(handTradeSaw(e.getKey()), mine != null && m.getTrades().contains(mine) ? 1 : 0);
        }
    }

    /** FS17: what a seller-priced market's fixture counts of the trade the harness placed on its list. */
    static String handTradeSaw(Good good) { return good + " markets with the trade the harness placed on the list"; }

    /** ...and a month later none of those trades is on its market's list: a seller-priced good's month begins as a traded good's does (Markets.startSellerPricedMonth()). */
    private static void tradesOfTheFirstMonthGone(Point p, Game g, Map<Good, Set<Trade>> firstMonth) {
        for (Map.Entry<Good, Set<Trade>> e : firstMonth.entrySet()) {
            int lingering = 0;
            for (Trade t : g.getMarkets().get(e.getKey()).getTrades()) if (e.getValue().contains(t)) lingering++;
            p.sellerPriced.compared++;
            if (lingering > 0) p.sellerPriced.differ(e.getKey() + ": the first month's trades still on the list a month later", lingering + " of " + e.getValue().size(), "none");
        }
    }

    /**
     * (f) Two skips run on each city from the same month, and the second report's health figures compared. Whether a skip
     * had an outbreak is read off the reloaded city's reports, not the live city's (which carry every skip it has made):
     * its first report is its first skip's alone, and its second skip had one when the count rose, or fell and is not
     * zero (a report that starts afresh). Equal counts are read as none.
     */
    private static void skips(Point p, Game g, Game back) {
        // FS17: the caused point's twins have an outbreak in force before their skips (an outbreak in a skip is a one in OUTBREAK_CHANCE (1/60) chance a month, so the played checkpoints rarely showed one).
        if (CAUSED_CITY.equals(p.city)) {
            startOutbreak(g);
            startOutbreak(back);
        }
        g.simulateMonths(SKIP_MONTHS);
        back.simulateMonths(SKIP_MONTHS);
        int first = outbreakMonths(back.getSkipReport()), firstSick = back.getSkipReport().getMonthsSick();
        g.simulateMonths(SKIP_MONTHS);
        back.simulateMonths(SKIP_MONTHS);
        int second = outbreakMonths(back.getSkipReport()), secondSick = back.getSkipReport().getMonthsSick();
        TimeSkipReport live = g.getSkipReport(), re = back.getSkipReport();
        p.skips = 2;
        p.skipsWithOutbreak = (first > 0 ? 1 : 0) + ((second < first ? second > 0 : second > first) ? 1 : 0);
        // ...and the skips with months sick, read the same way (FB3, FW6-C): the fixture asked only for skips, so a city never sick in one passed.
        p.skipsSick = (firstSick > 0 ? 1 : 0) + ((secondSick < firstSick ? secondSick > 0 : secondSick > firstSick) ? 1 : 0);
        p.skipped.compare("the report: outbreaks", live.getOutbreaks(), re.getOutbreaks());
        p.skipped.compare("the report: months in outbreak", live.getMonthsInOutbreak(), re.getMonthsInOutbreak());
        p.skipped.compare("the report: months sick", live.getMonthsSick(), re.getMonthsSick());
        p.skipped.compare("the report: worst work ratio", live.getWorstWorkRatio(), re.getWorstWorkRatio());
        p.skipped.compare("the report: peak unburied", live.getPeakUnburied(), re.getPeakUnburied());
    }

    private static int outbreakMonths(TimeSkipReport report) { return report.getOutbreaks() + report.getMonthsInOutbreak(); }

    /**
     * FS17: an outbreak put in force by Health's own restore, in the order getState() lays it out: the severity (CAUSED_OUTBREAK), the month
     * it began (this one) and a sick rate with the outbreak on top of what the city had (to the model's MAX_SICK_RATE), the rest of the
     * city's own state kept. Called on both twins, each with its own state, so the skips compare like with like.
     */
    private static void startOutbreak(Game game) {
        double[] state = game.getHealth().getState();
        state[0] = CAUSED_OUTBREAK;
        state[1] = game.getMonth();
        state[2] = Math.min(Health.MAX_SICK_RATE, state[2] + CAUSED_OUTBREAK);
        game.getHealth().restore(state);
    }

    /* ----------------------------------- the report ----------------------------------- */

    /** One labelled assertion, printed either way; a failing one lists, under its line, what differs. */
    static void check(String label, boolean ok, String detail, List<String> more) {
        if (!ok) fails++;
        System.out.printf("%-112s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
        if (!ok && more != null) for (String line : more) System.out.println("      " + line);
    }

    static void check(String label, boolean ok, String detail) { check(label, ok, detail, null); }

    /** An assertion that nothing differs: the first difference on its line, the others (to SHOWN) under it, the rest counted. */
    static void nothingDiffers(String label, Findings f, String what) {
        boolean ok = f.differing == 0;
        List<String> more = new ArrayList<>(f.first);
        if (f.differing > f.first.size()) more.add("... and " + (f.differing - f.first.size()) + " more");
        check(label, ok, ok ? what : f.kindsLine(), more);
    }

    static String seen(Map<String, Integer> saw) {
        StringBuilder s = new StringBuilder();
        saw.forEach((k, v) -> s.append(s.length() == 0 ? "" : ", ").append(v).append(" ").append(k));
        return s.toString();
    }

    static Map<String, Integer> sawTogether(List<Point> points, Function<Point, Cause> cause) {
        Map<String, Integer> all = new LinkedHashMap<>();
        for (Point p : points) if (p.problem == null) cause.apply(p).saw.forEach((k, v) -> all.merge(k, v, Integer::sum));
        return all;
    }

    static boolean allAbove0(Map<String, Integer> saw) {
        return !saw.isEmpty() && saw.values().stream().allMatch(v -> v > 0);
    }

    static String when(Point p, int monthsOn) { return p.city + " month " + p.month + ", reloaded and " + (monthsOn == 1 ? "a month on" : "two months on"); }

    /** Sections 1 and 4's history lines: the fixture line first, then each checkpoint a month on and two months on. */
    static void history(List<Point> points) {
        int fewest = Integer.MAX_VALUE;
        for (Point p : points) if (p.problem == null) fewest = Math.min(fewest, Math.min(p.history1.series, p.history2.series));
        check("fixture: the history holds more than a hundred series with the month's entry, at every checkpoint",
                fewest != Integer.MAX_VALUE && fewest > 100, "fewest " + fewest);
        for (Point p : points) {
            if (p.problem != null) {
                check(p.city + " month " + p.month + ": the city was saved and loaded", false, p.problem);
                continue;
            }
            for (int on = 1; on <= 2; on++) {
                Compared c = on == 1 ? p.history1 : p.history2;
                nothingDiffers(when(p, on) + ": every history figure is the live city's", c, c.numbers + " numbers in " + c.series + " series, the same");
            }
        }
    }

    /** Sections 2 and 4's whole-save lines. */
    static void whole(List<Point> points) {
        int fewest = Integer.MAX_VALUE;
        for (Point p : points) if (p.problem == null) fewest = Math.min(fewest, Math.min(p.save1.numbers, p.save2.numbers));
        check("fixture: the save holds over 10,000 numbers, at every checkpoint", fewest != Integer.MAX_VALUE && fewest > 10_000, "fewest " + fewest);
        for (Point p : points) {
            if (p.problem != null) continue;
            for (int on = 1; on <= 2; on++) {
                Compared c = on == 1 ? p.save1 : p.save2;
                nothingDiffers(when(p, on) + ": every figure in the save is the live city's, to the cent", c, c.numbers + " numbers the same");
            }
        }
    }

    static void section3(List<Point> points) {
        System.out.println("--- 3. the causes, one by one ---");
        Point caused = causedPoint(points);
        cause(points, p -> p.revalued, "3a", "a month on", "every sector's stock revaluation is counted in the reload's first month and equals the live city's");
        onCausedPoint(caused, "3a", p -> p.revalued, "sectors that counted a revaluation", "...of them one not zero");
        cause(points, p -> p.drawn, "3b", "a month on", "Construction's materials drawn at home and imported equal the live city's");
        onCausedPoint(caused, "3b", p -> p.drawn, "crews that drew materials");
        cause(points, p -> p.tally, "3c, the tally", "right after the load",
                "every market's month tally (imported, exported, filled at home, offered, bid, drawn since the strike) is the live city's");
        onCausedPoint(caused, "3c, the tally", p -> p.tally, "markets that imported", "markets that exported", "markets with units drawn since the strike",
                "markets carrying the tally the harness set");
        cause(points, p -> p.notes, "3c, the notes", "a month on", "each sector's lastInvestment note is the live city's");
        // FS17: the investors decide in every month (Game.runPrivateInvestment(), once a month, each business noting its decision), so the caused point's own month on leaves notes, with the tally set by hand on a market.
        onCausedPoint(caused, "3c, the notes", p -> p.notes, "investment notes the live city left");
        cause(points, p -> p.shares, "3d", "right after the load", "each sector book's shares month, this and last, is the live city's");
        // FS17: the caused point's books carry the shares the harness put there (SectorBooks.restoreShares), this month's and last month's.
        onCausedPoint(caused, "3d", p -> p.shares, "sectors with shares this month", "sectors with shares last month",
                "sectors carrying the shares the harness set, this month", "sectors carrying the shares the harness set, last month");
        cause(points, p -> p.weights, "3e", "right after the load", "the price index's rent and food weights are the live city's");
        // FS17: the weights are structural once the basket is based, so the premise is asserted against the model's constant and not found: every point stands past PriceIndex.SETTLING_MONTHS, and its index is based.
        int youngest = Integer.MAX_VALUE, based = 0, counted = 0;
        for (Point p : points) {
            if (p.problem != null) continue;
            youngest = Math.min(youngest, p.month);
            counted++;
            based += p.weights.saw.getOrDefault("indexes based", 0);
        }
        check("fixture 3e, by the constant: every point stands past PriceIndex.SETTLING_MONTHS, and its price index is based, so the weights are the spending's and not the .5 and .5 a new basket starts on",
                counted > 0 && youngest > PriceIndex.SETTLING_MONTHS && based == counted,
                "the youngest point is at month " + youngest + " (SETTLING_MONTHS " + PriceIndex.SETTLING_MONTHS + "), " + based + " of " + counted + " indexes based");
        int skips = 0, withOutbreak = 0, sick = 0;
        for (Point p : points) if (p.problem == null) { skips += p.skips; withOutbreak += p.skipsWithOutbreak; sick += p.skipsSick; }
        check("fixture 3f: time skips were run on both cities from the same month, one with an outbreak and one with months sick",
                skips > 0 && withOutbreak > 0 && sick > 0,
                skips + " skips, " + withOutbreak + " with an outbreak, " + sick + " with months sick (read off the reloaded city's reports)");
        // FS17: ...and the caused point's own skips had both, by the outbreak put in force on its twins (Health.restore): the severity within the model's peaks and so far over the floor that it outlives both skips.
        boolean outlives = CAUSED_OUTBREAK >= Health.OUTBREAK_MIN_PEAK && CAUSED_OUTBREAK <= Health.OUTBREAK_MAX_PEAK
                && CAUSED_OUTBREAK * Math.pow(Health.OUTBREAK_DECAY, 2 * SKIP_MONTHS) > Health.OUTBREAK_FLOOR;
        check("fixture 3f, by hand: the caused point's own skips had an outbreak and months sick, from the outbreak put in force on both twins",
                caused != null && outlives && caused.skips > 0 && caused.skipsWithOutbreak > 0 && caused.skipsSick > 0,
                caused == null ? "(no caused point)" : caused.skips + " skips, " + caused.skipsWithOutbreak + " with an outbreak, " + caused.skipsSick + " with months sick; the outbreak "
                        + CAUSED_OUTBREAK + " (peaks " + Health.OUTBREAK_MIN_PEAK + " to " + Health.OUTBREAK_MAX_PEAK + ", floor " + Health.OUTBREAK_FLOOR + ")"
                        + (outlives ? " outlives both skips" : " does not outlive both skips"));
        for (Point p : points) {
            if (p.problem != null) continue;
            String over = p.skipsWithOutbreak == 0 ? "no outbreak in either of the " + p.skips + " skips, so only the sick months and the ratios were tried"
                    : p.skipsWithOutbreak + " of " + p.skips + " skips with an outbreak";
            nothingDiffers("3f. " + p.city + " month " + p.month + ", the second time skip after the load reports what the running city's does (" + over
                    + "): outbreaks, months in outbreak, months sick, worst work ratio, peak unburied", p.skipped, p.skipped.compared + " figures the same");
        }
        // FB3 (FW6-C): the restores no checkpoint's save caused the case of, each one's fixture passing on the caused point (the detail says what the checkpoints alone showed).
        causedCause(points, p -> p.stop, "3g", "right after the load",
                "the sudden stop's words, whether it runs and the months left, and the hot money's peaks, are the live city's");
        onCausedPoint(caused, "3g", p -> p.stop, "cities in a sudden stop", "...of them with the stop's words");
        causedCause(points, p -> p.carry, "3h", "right after the load", "Quarrying's limestone carried into aggregate is the live city's");
        onCausedPoint(caused, "3h", p -> p.carry, CARRY_SAW);
        causedCause(points, p -> p.draws, "3i", "right after the load and a month on",
                "every market's draws since the month closed (seller, buyer, units, price) and the seller rows a hand order lifts are the live city's");
        onCausedPoint(caused, "3i", p -> p.draws, "markets with draws since the month closed", "hand-ordered seller rows that hold the order a month on");
        cause(points, p -> p.sellerPriced, "3j", "two months on",
                "a seller-priced market's trades of the first month on are off its list a month later, as a traded good's are");
        // FS17: the caused point's twins each had a trade placed by hand on every seller-priced market's list (building work's too), which the month on cleared.
        List<String> handTrades = new ArrayList<>();
        for (Good good : Good.values()) if (!good.traded()) handTrades.add(handTradeSaw(good));
        onCausedPoint(caused, "3j", p -> p.sellerPriced, handTrades.toArray(new String[0]));
    }

    /** The caused point (FS17): the city after its last checkpoint, with the causes set by hand, or null. */
    static Point causedPoint(List<Point> points) {
        for (Point p : points) if (p.problem == null && CAUSED_CITY.equals(p.city)) return p;
        return null;
    }

    /**
     * FS17: the fixture of a cause the harness sets by hand: the caused point's OWN city showed each of the named figures above zero,
     * whatever the checkpoints showed (a figure the checkpoints alone could supply would be found in the played city, not caused).
     */
    static void onCausedPoint(Point caused, String id, Function<Point, Cause> which, String... keys) {
        boolean ok = caused != null;
        StringBuilder detail = new StringBuilder();
        for (String key : keys) {
            int n = ok ? which.apply(caused).saw.getOrDefault(key, 0) : 0;
            ok &= n > 0;
            detail.append(detail.length() == 0 ? "" : ", ").append(n).append(' ').append(key);
        }
        check("fixture " + id + ", by hand: the caused point's own city exercised it", ok, detail + (caused == null ? " (no caused point)" : ""));
    }

    /** A cause whose fixture the caused point supplies (FB3, FW6-C): the line says what the checkpoints alone showed. */
    static void causedCause(List<Point> points, Function<Point, Cause> which, String id, String when, String label) {
        Map<String, Integer> saw = sawTogether(points, which), natural = new LinkedHashMap<>();
        for (Point p : points) if (p.problem == null && p.order == null) which.apply(p).saw.forEach((k, v) -> natural.merge(k, v, Integer::sum));
        check("fixture " + id + ": the live city exercised it", allAbove0(saw),
                seen(saw) + "; the checkpoints alone showed " + seen(natural) + ", the rest is the caused point's (a stop started, a hand order made between the presses"
                        + ", and a carry placed if no checkpoint had one)");
        for (Point p : points) {
            if (p.problem != null) continue;
            Cause c = which.apply(p);
            nothingDiffers(id + ". " + p.city + " month " + p.month + ", " + when + ": " + label, c, c.compared + " compared, the same");
        }
    }

    /** One cause: the fixture line (the live city showed it at work, in every way named), then its parity at each checkpoint. */
    static void cause(List<Point> points, Function<Point, Cause> which, String id, String when, String label) {
        Map<String, Integer> saw = sawTogether(points, which);
        check("fixture " + id + ": the live city exercised it", allAbove0(saw), seen(saw));
        for (Point p : points) {
            if (p.problem != null) continue;
            Cause c = which.apply(p);
            nothingDiffers(id + ". " + p.city + " month " + p.month + ", " + when + ": " + label, c, c.compared + " compared, the same");
        }
    }

    /* ------------------------------ the autosave (FB3, REV-FB3-1) ------------------------------ */

    /** What section 5 found: the city the month-end autosave holds, loaded from that slot, against the city that wrote it. */
    static final class Autosaved {
        /** Why it could not be compared (a load that failed), or null. */
        String problem;
        /** The month the live city stood at once its autosave was written, and the slot's header: its name and month. */
        int month, savedMonth;
        String name;
        /** Whether the month's end wrote it: the counter started again. */
        boolean fired;
        /** The draws the autosaved month closed at its end (Markets.drawsClosedLastMonth()): what its autosave would have carried before FB3 closed them first. */
        int drawsClosed;
        /** Every market's draws since the month closed, right after the load (as cause 3i compares them). */
        final Cause draws = new Cause();
        Compared history, save;
    }

    /**
     * The live city played on, a month at a time, to the month its autosave falls due in, under a save clock past the
     * floor at every look (SaveSlotCheck's), so the game writes the autosave where it writes it: at that month's end,
     * slot GameFiles.AUTOSAVE_SLOT of the harness's scratch folder. Loaded from that slot into a second game, the draws
     * compared right after the load, both run a month, and their saves compared.
     */
    static Autosaved autosaveAndReload(GameFiles files, Game g) {
        Autosaved a = new Autosaved();
        PrintStream real = System.out;
        System.setOut(QUIET);
        try {
            long[] now = { System.nanoTime() };
            g.useSaveClockForChecks(() -> now[0] += (long) (SaveWriter.FLOOR_SECONDS * 1e9) + 1);
            // Single months, so nothing but the interval autosaves: on to the month before it falls due, then that month.
            while (g.getMonthsUntilAutosave() > 1) g.simulateMonths(1);
            g.simulateMonths(1);
            g.awaitSaves();
            a.month = g.getMonth();
            a.fired = g.getMonthsUntilAutosave() == Game.AUTOSAVE_MONTHS;
            SaveHeader h = files.readHeader(GameFiles.AUTOSAVE_SLOT);
            a.savedMonth = h == null ? -1 : h.getMonth();
            a.name = h == null || !h.hasName() ? null : h.getSlotName();
            a.drawsClosed = g.getMarkets().drawsClosedLastMonth();
            Game back = new Game(files);
            back.loadGameSave(GameFiles.AUTOSAVE_SLOT);
            if (back.getLoadFailure() != null) { a.problem = "the load of the autosave failed: " + back.getLoadFailure(); return a; }
            for (GoodsMarket live : g.getMarkets().all()) {
                GoodsMarket re = back.getMarkets().get(live.good());
                compareDraws(a.draws, String.valueOf(live.good()), live.monthsDraws(), re == null ? List.of() : re.monthsDraws());
            }
            g.simulateMonths(1);
            back.simulateMonths(1);
            Compared[] on = saveAndCompare(files, g, back, "a month on from the autosave");
            a.history = on[0];
            a.save = on[1];
        } finally {
            System.setOut(real);
        }
        return a;
    }

    /** Section 5's lines: the two fixtures (the game wrote the autosave at its month's end, in a month that drew), then the draws, the history and the save. */
    static void section5(Autosaved a) {
        System.out.println("--- 5. a city loaded from its month-end autosave plays its next month as the one that kept running ---");
        if (a.problem != null) {
            check("the autosave was loaded", false, a.problem);
            return;
        }
        check("fixture: the month the autosave fell due in wrote slot " + GameFiles.AUTOSAVE_SLOT + " at its end, as the game takes it",
                a.fired && a.savedMonth == a.month && ("Autosave - month " + a.month).equals(a.name),
                "the slot reads \"" + a.name + "\" at month " + a.savedMonth + ", the city at " + a.month
                        + (a.fired ? ", the counter started again" : ", the counter did not start again"));
        check("fixture: ...a month that drew from the markets, so it had draws to close at its end", a.drawsClosed > 0,
                a.drawsClosed + " draws closed");
        nothingDiffers("5. month " + a.month + ", right after the load from the autosave: every market's draws since the month closed are the live city's",
                a.draws, a.draws.compared + " compared, the same");
        nothingDiffers("5. month " + a.month + ", a month on from the autosave: every history figure is the live city's",
                a.history, a.history.numbers + " numbers in " + a.history.series + " series, the same");
        nothingDiffers("5. ...and every figure in its save, to the cent", a.save, a.save.numbers + " numbers the same");
    }

    /* ------------------------------------ the Earth ------------------------------------ */

    /** Whether section 4 can run: earth-2.bin named (-Dcbs.earth, or at data/earth-2.bin) and the game reading it; says so in one line where not. */
    static boolean earthHere() {
        Path p = EarthData.locate();
        String named = System.getProperty(EarthData.PROPERTY);
        if (named != null && !named.isBlank() && (p == null || !Files.isRegularFile(p))) {
            check("the file -D" + EarthData.PROPERTY + " names is there", false, named);
            return false;
        }
        if (p == null) {
            System.out.println("\nearth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": section 4, a city on the real Earth, was not run");
            return false;
        }
        EarthSite.Data game = EarthSite.gameData();
        if (!game.here() || !game.reader().identity().equals(EarthData.EARTH_2)) {
            System.out.println("\nthe game's Earth is not earth-2 (" + game.words() + "): section 4 was not run");
            return false;
        }
        return true;
    }

    /* -------------------------------------- the run -------------------------------------- */

    public static void main(String[] args) {
        long started = System.nanoTime();
        List<Point> points = new ArrayList<>();
        Game g = LongPlaytest.probeCity(GameFiles.scratch("reloadcheck"), null);
        GameFiles files = g.getGameFiles();
        for (int target : CHECKPOINTS) {
            long t0 = System.nanoTime();
            LongPlaytest.probePlay(g, target);
            points.add(reload(files, g, "Danzik"));
            System.out.printf("  played, saved, reloaded and compared at month %d (%.0f s)%n", points.get(points.size() - 1).month, (System.nanoTime() - t0) / 1e9);
        }
        // FB3 (FW6-C): the live city as it stands, with the three conditions no checkpoint's save had caused: a sudden stop, a hand order between the
        // presses and, only if no checkpoint carried limestone, a carry; reloaded through the same path, and counted with the rest.
        // FS17: and a carry every time, a market's tally, a price moved and a build order (causeTheConditions); one sector's shares on the books (causeTheConditions);
        // an outbreak on both twins before their skips (skips()); and a trade on each seller-priced market's list after the first month on (placeATradeByHand()).
        long c0 = System.nanoTime();
        HandOrder order = causeTheConditions(g, true);
        points.add(reload(files, g, CAUSED_CITY, order));
        System.out.printf("  caused (a sudden stop, a hand order, a carry, a tally, a price moved, a build order, shares, an outbreak, a trade on each seller-priced list), saved, reloaded and compared at month %d (%.0f s)%n", points.get(points.size() - 1).month, (System.nanoTime() - c0) / 1e9);
        // FB3 (REV-FB3-1): the live city on to its own autosave, written by the game at the month's end, and loaded from that slot.
        long a0 = System.nanoTime();
        Autosaved autosaved = autosaveAndReload(files, g);
        System.out.printf("  autosaved at month %d, loaded from the autosave and compared (%.0f s)%n", autosaved.month, (System.nanoTime() - a0) / 1e9);
        Point earth = null;
        if (earthHere()) {
            GameFiles earthFiles = GameFiles.scratch("reloadcheck-earth");
            Game e = LongPlaytest.probeCity(earthFiles, EARTH_SPOT);
            LongPlaytest.probePlay(e, EARTH_CHECKPOINT);
            earth = reload(earthFiles, e, "Rotterdam");
            System.out.println("  the Earth city played, saved, reloaded and compared at month " + earth.month);
        }

        System.out.println("--- 1. a played city, reloaded, plays its next month as the one that kept running: every history series ---");
        history(points);
        System.out.println("--- 2. ...and every figure in its save, to the cent ---");
        whole(points);
        section3(points);
        if (earth != null) {
            System.out.println("--- 4. on the real Earth ---");
            history(List.of(earth));
            whole(List.of(earth));
        }
        section5(autosaved);

        System.out.printf("%n%.0f s%n", (System.nanoTime() - started) / 1e9);
        System.out.println(fails == 0 ? "All checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
