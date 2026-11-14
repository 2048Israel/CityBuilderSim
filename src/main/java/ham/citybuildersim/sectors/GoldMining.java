package ham.citybuildersim.sectors;

import ham.citybuildersim.*;

/**
 * Gold mines and their mills recover fine ounces at a geology-dependent grade.
 *
 * WHY (GD4, spec-gold-money 4.4). Bullion can be made at home. The same
 * ore throughput costs the same as its grade falls. Under Gold the mint
 * buys the output with new money and keeps the metal in the vault.
 */
public final class GoldMining extends ExtractionSector {

    /** Permanent catalogue id, after R4's research labs at 148 through 151. */
    public static final int MINE_AND_MILL = 152;

    /** Ore milled a month at nameplate, tonnes (GD4, estimate). */
    public static final double ORE_PER_MONTH = 30_000;

    /** The accepted mint offer waiting for the next strike, ounces and thousands of D$. */
    private double mintOunces, mintPayment;

    public GoldMining() {
        super(Sectors.GOLD_MINING, Sectors.GOLD_MINING, BuildingType.MINING, Resource.GOLD);
        makes(Good.GOLD);
        uses(Good.ACID);
        uses(Good.DIESEL);
        blurb("Mines gold-bearing rock and mills it into fine gold. Richer ground yields "
                + "more ounces from the same work. Under Gold the mint buys the metal.");
    }

    /** The deposits and the buyer of the mill's fine ounces. */
    @Override
    public java.util.List<Line> ownLines(Game game) {
        java.util.List<Line> lines = super.ownLines(game);
        if (game != null && game.getForeignAccounts().getRegime() == ForeignAccounts.Regime.GOLD) {
            lines.add(Line.head("Who buys it here"));
            lines.add(Line.of(Good.GOLD.label(), "The mint at its declared par"));
        } else {
            lines.addAll(BuildCard.chainLines(game, this));
        }
        return lines;
    }

    /** Fixed ore throughput, recovered at the fields' declining grades. Pure. */
    @Override
    protected double madeAtRate(Good g) {
        if (g != Good.GOLD || game == null) return super.madeAtRate(g);
        return game.getLandManager().goldOutput(getCapacity(g) * getOperatingRate());
    }

    /** Bullion is offered straight from the mill to its buyer. */
    @Override
    public double getPlannedOutput(Good g) {
        return g == Good.GOLD ? 0 : super.getPlannedOutput(g);
    }

    /** The mint takes the offer at M. Abroad it must cover the marginal cost. */
    @Override
    public double getExportBoundOutput(Good g) {
        if (g != Good.GOLD) return super.getExportBoundOutput(g);
        if (game != null && game.getForeignAccounts().getRegime() == ForeignAccounts.Regime.GOLD) return madeAtRate(g);
        return super.getExportBoundOutput(g);
    }

    /** Ore-based costs divided by the ounces that ore actually recovers. */
    @Override
    public double getCostPerUnit(Good g) {
        double actual = madeAtRate(g);
        return actual > 0 ? super.getCostPerUnit(g) * getCapacity(g) * getOperatingRate() / actual : Double.MAX_VALUE;
    }

    /** The grade also decides whether another tonne of ore is worth milling. */
    @Override
    public double getMarginalCostPerUnit(Good g) {
        double actual = madeAtRate(g);
        return actual > 0 ? super.getMarginalCostPerUnit(g) * getCapacity(g) * getOperatingRate() / actual : Double.MAX_VALUE;
    }

    /** The plan's revenue follows the next field's grade, with ore costs held. */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        double profit = super.estimatedMonthlyProfit(t, plans);
        if (t == null || game == null || markets == null) return profit;
        double reference = t.makes(Good.GOLD) * BusinessInvestment.operatingRateOf(getOperatingRate());
        double abroad = markets.get(Good.GOLD).netExportPrice();
        double price = game.getForeignAccounts().getRegime() == ForeignAccounts.Regime.GOLD
                ? game.getForeignAccounts().getMintPrice() / 1000 : abroad;
        return profit + game.getLandManager().goldOutput(reference) * price - reference * abroad;
    }

    /** A mint purchase is a domestic receipt funded by new base money. */
    @Override
    protected void sellDirectOutput(Good g, GoodsMarket market, double units) {
        ForeignAccounts fa = game == null ? null : game.getForeignAccounts();
        if (fa == null || fa.getRegime() != ForeignAccounts.Regime.GOLD) {
            super.sellDirectOutput(g, market, units);
            return;
        }
        double price = fa.getMintPrice() / 1000;
        mintOunces += units;
        mintPayment += units * price;
        Output o = output(g);
        o.exportBound -= units;
        o.produced += units;
        bookSale(market.record(key(), "Mint", units, price));
    }

    /** Settles the accepted offer in the same audit window as its sale reaches cash. */
    @Override
    protected void beforeBank() {
        super.beforeBank();
        if (game != null && mintPayment > 0) {
            game.getForeignAccounts().receiveMinedGold(mintOunces);
            game.getCentralBank().mintGold(mintPayment);
            mintOunces = mintPayment = 0;
        }
    }

    /** An accepted offer survives the gap between production and the next strike. */
    @Override
    protected void saveExtras(java.util.Map<String, Double> extras) {
        super.saveExtras(extras);
        extras.put("mintOunces", mintOunces);
        extras.put("mintPayment", mintPayment);
    }

    @Override
    protected void restoreExtras(java.util.Map<String, Double> extras) {
        super.restoreExtras(extras);
        mintOunces = extras.getOrDefault("mintOunces", 0.0);
        mintPayment = extras.getOrDefault("mintPayment", 0.0);
    }

    /** A change of currency unit changes the accepted payment and keeps its fine ounces. */
    @Override
    protected void redenominateExtras(double scale) {
        super.redenominateExtras(scale);
        mintPayment *= scale;
    }

    @Override
    protected void resetExtras() {
        super.resetExtras();
        mintOunces = mintPayment = 0;
    }

    /** Deplete precisely what was made, once, after the input-order machinery ran. */
    @Override
    public void produceStock(Good g, GoodsMarket market) {
        super.produceStock(g, market);
        if (g == Good.GOLD && game != null) {
            Output o = output(g);
            game.getLandManager().extract(Resource.GOLD, o.produced + o.exportBound);
        }
    }
}
