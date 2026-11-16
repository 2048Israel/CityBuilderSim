package ham.citybuildersim;

import java.util.Arrays;
import java.util.List;

import static ham.citybuildersim.Good.*;

/**
 * The world market's chain: for each good made of other goods, which goods and in what shares of its price, and the order the January solves
 * the goods in (inputs first), so that a made good's price in a nation is what its inputs cost that nation plus its own part (CU2;
 * docs/studies/spec-world-prices.md 3).
 *
 * WHY THIS EXISTS. CU1 found rod priced apart from the cathode it is rolled from: WorldMarket solved each good alone, on its own price path, so by
 * 2100 on the Historical path a cathode stood at 0.38 of the literals' level and the rod beside it at 2.4 times its inputs' cost, and a city
 * rolling imported cathode grew rich on a pricing artifact (spec-copper-margin.md). Here a made good follows what it is made of, as the works
 * that makes it does: the table is the works' recipe in buildings.json at the literals' mid-band prices, and a check holds each row to it.
 *
 * Built once for a Base, from the goods the file models: a row whose made good is not modelled is skipped, an input that is not modelled is
 * dropped (its share leaves the sum, so the good's own part grows). Pure data and a sort; nothing is saved.
 */
final class WorldChain {

    /** One made good: the works whose recipe gives its shares (its template's name in buildings.json; null for a good the price file prices wholly off another with no works between them), its inputs and their shares of its price at the literals' mid-band prices. */
    record Row(Good made, String works, Good[] inputs, double[] shares) { }

    /** One input of a row and its share of the made good's price. */
    private record In(Good good, double share) { }

    private static In in(Good good, double share) { return new In(good, share); }

    private static Row row(Good made, String works, In... ins) {
        Good[] goods = new Good[ins.length];
        double[] shares = new double[ins.length];
        for (int k = 0; k < ins.length; k++) {
            goods[k] = ins[k].good();
            shares[k] = ins[k].share();
        }
        return new Row(made, works, goods, shares);
    }

    /**
     * The forty rows: each good a works makes, on that works' inputs at the shares buildings.json gives at the literals' mid-band prices (the rest of its
     * price is its conversion, on the manufactures index), and each good the price file prices off another with no works between them wholly on it
     * (spec-world-prices.md 3.3); a dial, and WorldMarketCheck holds every row's works to its share.
     */
    static final List<Row> RECIPE = List.of(
            row(COPPER_CONC, null, in(COPPER, 1)),
            row(ANODE, null, in(COPPER, 1)),
            row(COPPER_SCRAP, null, in(COPPER, 1)),
            row(COPPER_ROD, "Rod Mill", in(COPPER, .98)),
            row(WIRE_CABLE, "Wire & Cable Works", in(COPPER_ROD, .70)),
            row(PELLETS, "Pelletising Plant", in(IRON, .70), in(THERMAL_COAL, .01)),
            row(COKING_COAL, null, in(THERMAL_COAL, 1)),
            row(MET_COKE, "Coke Ovens", in(COKING_COAL, .74)),
            row(PIG_IRON, "Blast Furnace", in(IRON, .36), in(MET_COKE, .33)),
            row(SCRAP, null, in(PIG_IRON, 1)),
            row(STEEL, "Oxygen Steel Shop", in(PIG_IRON, .66), in(SCRAP, .16)),
            row(FABRICATED_STEEL, "Fabrication Works", in(STEEL, .38)),
            row(MACHINERY, "Machine Works", in(STEEL, .08)),
            row(CARS, "Assembly Plant", in(FABRICATED_STEEL, .15), in(MACHINERY, .11)),
            row(VANS, "Commercial Vehicle Plant", in(FABRICATED_STEEL, .11), in(MACHINERY, .09)),
            row(ROLLING_STOCK, "Locomotive Works", in(MACHINERY, .09), in(FABRICATED_STEEL, .08)),
            row(TRUCKS, "Heavy Truck Plant", in(MACHINERY, .10), in(FABRICATED_STEEL, .07)),
            row(CEMENT, "Cement Plant", in(LIMESTONE, .34), in(THERMAL_COAL, .12), in(AGGREGATE, .04)),
            row(LIME, "Lime Kiln", in(LIMESTONE, .21), in(THERMAL_COAL, .07)),
            row(BITUMEN, "Crude Unit", in(CRUDE, .85)),
            row(COKE, "Crude Unit", in(CRUDE, .85)),
            row(DIESEL, "Crude Unit", in(CRUDE, .85)),
            row(FUEL_OIL, "Crude Unit", in(CRUDE, .85)),
            row(JET, "Crude Unit", in(CRUDE, .85)),
            row(LPG, "Crude Unit", in(CRUDE, .85)),
            row(LUBRICANTS, "Crude Unit", in(CRUDE, .85)),
            row(NAPHTHA, "Crude Unit", in(CRUDE, .85)),
            row(PETROL, "Crude Unit", in(CRUDE, .85)),
            row(PULPWOOD, null, in(SAWLOGS, 1)),
            row(RESIDUES, null, in(SAWLOGS, 1)),
            row(LUMBER, "Sawmill", in(SAWLOGS, .52)),
            row(PANELS, "Panel Mill", in(PULPWOOD, .31)),
            row(PULP, "Pulp Mill", in(PULPWOOD, .21), in(RESIDUES, .08)),
            row(PAPER, "Paper Mill", in(PULP, .74), in(FUEL_OIL, .07)),
            row(WOOD_PELLETS, "Wood Pellet Mill", in(RESIDUES, .32)),
            row(PROCESSED_MEAT, "Meat Works", in(MEAT, .40)),
            row(READY_MEALS, "Meat Works", in(MEAT, .40)),
            row(BREAD, "Bakery", in(CROPS, .24)),
            row(BAKERY, "Bakery", in(CROPS, .24)),
            row(SNACKS, "Snack & Oils Plant", in(CROPS, .19)));

    /** Per modelled good (the Base's traded order): its inputs' indices; empty for a root, a good that follows nothing. */
    final int[][] parent;
    /** Per modelled good: its inputs' shares of its price, in parent's order. */
    final double[][] share;
    /** Per modelled good: its own part of its price, 1 - sum(shares) for a made good (0 if below 1e-9), 1 for a root. */
    final double[] own;
    /** Every modelled good, roots first (in index order), then each made good once its inputs are placed. */
    final int[] order;

    /** The chain on the goods a file models: traded in the Base's order, indexOf each Good's number among them (-1 for a good not modelled). */
    WorldChain(WorldMarket.Traded[] traded, int[] indexOf) {
        int count = traded.length;
        parent = new int[count][0];
        share = new double[count][0];
        own = new double[count];
        Arrays.fill(own, 1);
        for (Row row : RECIPE) {
            int gi = indexOf[row.made().ordinal()];
            if (gi < 0) continue;
            int[] pp = new int[row.inputs().length];
            double[] ss = new double[pp.length];
            int m = 0;
            double sum = 0;
            for (int k = 0; k < pp.length; k++) {
                int pi = indexOf[row.inputs()[k].ordinal()];
                if (pi < 0) continue;
                pp[m] = pi;
                ss[m] = row.shares()[k];
                sum += ss[m++];
            }
            parent[gi] = Arrays.copyOf(pp, m);
            share[gi] = Arrays.copyOf(ss, m);
            own[gi] = m == 0 ? 1 : Math.max(0, 1 - sum);
            if (own[gi] < 1e-9) own[gi] = 0;
        }
        order = new int[count];
        boolean[] placed = new boolean[count];
        int at = 0;
        for (int gi = 0; gi < count; gi++) {
            if (parent[gi].length == 0) {
                order[at++] = gi;
                placed[gi] = true;
            }
        }
        while (at < count) {
            int before = at;
            for (int gi = 0; gi < count; gi++) {
                if (placed[gi]) continue;
                boolean ready = true;
                for (int p : parent[gi]) ready &= placed[p];
                if (ready) {
                    order[at++] = gi;
                    placed[gi] = true;
                }
            }
            if (at == before) throw new IllegalStateException("world chain: the recipe goes round");
        }
    }

    /** Whether a modelled good is made of others (it has inputs); a root has none. */
    boolean made(int gi) { return parent[gi].length > 0; }
}
