package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Mining;

import java.util.Locale;

/**
 * Money is conserved: every dollar that leaves a pool arrives in another, or
 * crosses the city's boundary in a way the audit can name.
 *
 * The 29th harness, and the one the other twenty-eight were missing. Every
 * money bug this codebase has had was a flow with one side - a charge with no
 * payee, a tax with no payer - and each was found by hand, months after it
 * started. MoneyAudit strikes the identity every month; this plays a city with
 * all six sectors trading, borrowing, building and being taxed, and demands
 * the residual stay at rounding.
 *
 * Two cities, because a fixture that only ever grows can hide a leak that
 * only opens under stress: one is left to prosper, the other is bankrupted
 * and restructured, taxed hard, and made to import everything.
 */
public class MoneyCheck {

    static int fails = 0;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-70s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    static BuildingsTemplate t(Game g, String name) {
        for (BuildingsTemplate b : g.getBuildingManager().getTemplates()) {
            if (b.getName().equals(name)) return b;
        }
        throw new IllegalArgumentException("no template " + name);
    }

    /** The worst post-audit drift any month of the last play() saw. See Game.getPostAuditDrift(). */
    static double worstDrift = 0;
    static String worstDriftPool = "";

    /** Runs `months` and returns the worst relative residual seen, printing the worst month. */
    static MoneyAudit.Result play(String label, Game g, int months, boolean verbose) {
        MoneyAudit.Result worst = MoneyAudit.Result.NONE;
        double worstRel = -1;
        worstDrift = 0;
        worstDriftPool = "";
        for (int i = 0; i < months; i++) {
            final Game gg = g;
            quietly(() -> gg.simulateMonths(1));
            MoneyAudit.Result r = g.getLastMoneyAudit();
            if (verbose) System.out.println("   " + r);
            if (r.relative() > worstRel) { worstRel = r.relative(); worst = r; }
            if (Math.abs(g.getPostAuditDrift()) > Math.abs(worstDrift)) {
                worstDrift = g.getPostAuditDrift();
                worstDriftPool = g.getPostAuditDriftPool();
            }
        }
        System.out.printf("   %s: worst month %s%n", label, worst);
        if (worst.relative() >= 1e-4) System.out.print(worst.detail);
        return worst;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.CANADA);
        boolean verbose = args.length > 0 && args[0].equals("-v");

        /* ==================== 1. a city that prospers ==================== */
        System.out.println("--- every sector trading, nothing leaks ---");

        Game g = new Game(GameFiles.scratch("moneycheck"));
        g.run();
        // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
        // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
        // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
        g.setCashForTest(Founding.WEALTHY_CASH);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
        g.buildStack(t(g, "House"), 400, false);
        g.buildStack(t(g, "Low-Rise Apartments"), 4, false);
        g.buildStack(t(g, "Small Grocery Store"), 4, false);
        g.buildStack(t(g, "Industrial Bakery"), 3, false);
        g.buildStack(t(g, "Construction Depot"), 3, false);
        g.buildStack(t(g, "Coal Power Plant"), 1, false);
        g.buildStack(t(g, "Water Treatment Plant"), 1, false);
        g.buildStack(t(g, "Paved Road"), 10, false);
        g.buildStack(t(g, "Walk-in Clinic"), 2, false);
        g.buildStack(t(g, "Elementary School"), 1, false);

        MoneyAudit.Result worst = play("founding, 120 months", g, 120, verbose);
        assertTrue("a founding city conserves money to within 0.01% of what moved",
                worst.relative() < 1e-4);

        // Heavy industry and mining need a deposit, which the player buys -
        // in dollars since 0.7.6, converting cash at the day's rate, so the
        // treasury is handed exactly that much local money first.
        LandMarket market = g.getLandManager().getMarket();
        for (LandParcel parcel : market.getListing()) {
            if (parcel.getIronTonnes() > 0) {
                g.getGovernmentInvestor().spend(-parcel.localPrice(g.getForeignAccounts().getRate()));
                g.buyLandParcel(parcel.getId());
                break;
            }
        }
        g.getGovernmentInvestor().spend(-50_000_000);
        g.buildStack(g.getBuildingManager().getTemplate(Mining.IRON_MINE), 2, false);
        g.buildStack(g.getBuildingManager().getTemplate(HeavyIndustry.ARC_FURNACE), 1, false);
        g.buildStack(t(g, "Memorial Cemetery"), 1, false);
        g.buildStack(t(g, "Community College"), 1, false);
        /* =================================================================
           AND A BUS, WHICH IS THE WHOLE REASON THIS LINE EXISTS (2026-09-16).

           Transit had been in the game for as long as the modes had, and no
           harness in this suite had ever put a passenger on one - the playtest
           advisor answered every jam with tarmac, and every fixture that
           needed a road bought a road. So the fare, which the city collects
           and NOBODY WAS EVER DEBITED FOR, sat as money from nowhere through
           every run of the very harness that exists to catch money from
           nowhere. It was found the first time the advisor bought a Bus
           Network, and the residual was the fare to six decimals.

           This class's own header says it: "every money bug this codebase has
           had was a flow with one side". The twenty-ninth harness could not
           see this one because its city had no buses, which is the more
           general lesson - a mechanic no fixture exercises has no test,
           whatever the assertion count says.
           ================================================================= */
        g.buildStack(t(g, "Bus Network"), 3, false);

        worst = play("with mines, mills, a cemetery, a college and a bus, 240 months", g, 240, verbose);
        assertTrue("an industrial city conserves money to within 0.01% of what moved",
                worst.relative() < 1e-4);
        assertTrue("...and it really did carry passengers, so the fare was really charged",
                g.getInfrastructureManager().getTransitRiders() > 0
                        && g.getEconomyManager().getTransitFares() > 0);
        /*
         * ...AND SINCE 0.7.49 THE TREASURY PAYS THE BUSES' BILL, and the
         * drivers buy their fuel abroad: the crews' wages were paid by
         * nobody, and a car cost nothing to run. The month's audit names
         * both legs and still closes - the drivers' fuel PetrolFunded and
         * PetrolImports since 0.7.76, what they burn being petrol.
         *
         * ...AND SINCE 0.7.83 (batch O6) THEY BUY IT AT THE PUMP: the grocers'
         * forecourts import it on their own books and sell it to the drivers,
         * so the legs are Retail's - its sales to the households and its
         * imports - and the households' own PetrolFunded and PetrolImports
         * read nothing. Until then the audit had to name those two.
         */
        MoneyAudit.Result busMonth = g.getLastMoneyAudit();
        assertTrue("the bus town's audit closes with the bill paid and the fuel abroad, bought at the pump",
                busMonth.relative() < 1e-4 && g.getEconomyManager().getTransitBill() > 0 && g.getHouseholdFuel() > 0
                        && g.getSectors().retail().statement().bought.getOrDefault(Good.PETROL, new Sector.Split()).abroad > 0
                        && busMonth.detail.contains("- transit Bill")
                        && busMonth.detail.contains("+ Retail SalesToHouseholds")
                        && busMonth.detail.contains("- Retail Imports")
                        && !busMonth.detail.contains("+ households PetrolFunded")
                        && !busMonth.detail.contains("- households PetrolImports"));

        /* ==================================================================
           AND LAND PAID FOR OUT OF THE VAULT (0.7.6).

           The deposit above was bought the default way - cash converted at
           the day's rate - and the 240 months after it closed. The other way
           moves no local money at all: the vault's dollars go to the seller.
           THE FIXTURE CAUSES IT: the treasury buys the dollars into the vault
           first, so the vault can pay whatever the parcel costs, and the
           parcel is bought with the toggle on.
           ================================================================== */
        System.out.println("\n--- land bought out of the vault: nothing moves the audit cannot see ---");
        LandParcel plot = market.bestValue();
        double plotUsd = plot.getPriceUsd();
        g.getGovernmentInvestor().spend(-plot.localPrice(g.getForeignAccounts().getRate()) * 2);
        g.buyForeignCurrency(plot.localPrice(g.getForeignAccounts().getRate()) * 1.5);
        double cashBefore = g.getCash();
        double vaultBefore = g.getForeignAccounts().getReservesUsd();
        g.setLandPaidFromVault(true);
        boolean bought = g.buyLandParcel(plot.getId());
        g.setLandPaidFromVault(false);
        assertTrue("fixture: the vault paid for a parcel and the treasury's cash did not move",
                bought && g.getCash() == cashBefore
                        && Math.abs(vaultBefore - g.getForeignAccounts().getReservesUsd() - plotUsd) < 1e-6);
        worst = play("after land out of the vault, 24 months", g, 24, verbose);
        assertTrue("a city that paid for land out of the vault conserves money",
                worst.relative() < 1e-4);
        assertTrue("...and nothing moved a pool after any month's audit",
                Math.abs(worstDrift) < .01);

        /* ==================== 2. a city under stress ==================== */
        System.out.println("\n--- broke, banned, taxed and importing: still nothing leaks ---");

        Game s = new Game(GameFiles.scratch("moneycheck"));
        s.run();
        // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
        // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
        // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
        s.setCashForTest(Founding.WEALTHY_CASH);
        s.getLandManager().setOwnedSqFt(s.getLandManager().getOwnedSqFt() + 100_000_000L);
        s.buildStack(t(s, "House"), 600, false);
        s.buildStack(t(s, "Convenience Store"), 6, false);
        s.buildStack(t(s, "Construction Depot"), 2, false);
        s.buildStack(t(s, "Coal Power Plant"), 1, false);
        // No food industry at all: the shops import every unit they sell.
        // Every tax dial up, so the sectors are taxed on everything they do.
        TaxPolicy policy = s.getEconomyManager().getTaxPolicy();
        policy.setIncomeTaxRate(.35);
        for (String sector : Sectors.KEYS) {
            policy.setProfitOffset(sector, .25);
            policy.setSalesOffset(sector, .10);
        }
        // Take the treasury to the edge so the central bank's advances are
        // drawn and repaid inside the window (emergency notes, before 0.7.0).
        s.setCashForTest(20_000);

        worst = play("a stressed city, 180 months", s, 180, verbose);
        assertTrue("a stressed city conserves money to within 0.01% of what moved",
                worst.relative() < 1e-4);

        /*
         * Bankrupt retail by hand and let the restructure and its ban run.
         *
         * SINCE 0.7.8 THE WHOLE-SECTOR RESTRUCTURE IS THE BACKSTOP'S ALONE, a
         * sector with nothing left, so the fixture takes retail there: a
         * loan, and unpaid bills past everything else it owns, which the
         * restructure forgives - money in from outside the city, the case
         * the audit has to see. It used to hand retail the loan's cash and
         * set its assets to -1, which never stuck (the month's check reads
         * the assets off the balance sheet again before it judges); what
         * restructured it was its debt passing 1.5 times its assets later,
         * the second way in, which is a slice a month now - BankCheck (14)
         * audits those.
         */
        BusinessDebtManager credit = s.getEconomyManager().getBusinessDebtManager();
        credit.issueLoan(Sectors.RETAIL, 5_000_000, s.getMonth());
        double retailOwnsBesidesItsTill = credit.getAssets(Sectors.RETAIL) - credit.getCash(Sectors.RETAIL);
        s.getEconomyManager().setSectorCash(Sectors.RETAIL, -Math.max(0, retailOwnsBesidesItsTill) - 100_000);
        int wholeBefore = credit.getRestructureCount(Sectors.RETAIL);

        worst = play("through a restructure, 60 months", s, 60, verbose);
        assertTrue("fixture: retail went under whole, and was restructured",
                credit.getRestructureCount(Sectors.RETAIL) > wholeBefore);
        assertTrue("a restructure moves no cash the audit cannot see",
                worst.relative() < 1e-4);

        /* ==================================================================
           AND NOTHING MOVES AFTER THE AUDIT HAS STRUCK.

           The residual every check above asserts on reconciles WITHIN a month.
           It is blind, by construction, to money that moves after the strike:
           such money is missing from the flows and already in the pools by the
           next month's opening read, the two errors cancel exactly, and the
           residual stays at $0.00. Hot money lived in that gap for the whole
           life of the mechanic and 4,002 audited months never said a word.

           THE FIXTURE HAS TO CAUSE THE FLOW. A city whose rates sit under the
           world's attracts no hot money at all - the playtest's does, and its
           stock is zero for 4,002 months - so asserting this on an ordinary
           city proves nothing whatever. The policy rate goes to the maximum
           here, which puts the city well over the world's 2% and makes the
           carry money actually turn up. Without that line this check passes
           against the very bug it exists for; it was tried.
           ================================================================== */
        System.out.println("\n--- a city paying over the world: the money that arrives is audited ---");
        Game hot = new Game(GameFiles.scratch("moneycheck"));
        quietly(hot::run);
        // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
        // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
        // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
        hot.setCashForTest(Founding.WEALTHY_CASH);
        hot.getLandManager().setOwnedSqFt(hot.getLandManager().getOwnedSqFt() + 400_000_000L);
        hot.buildStack(t(hot, "House"), 400, false);
        hot.buildStack(t(hot, "Small Grocery Store"), 4, false);
        hot.buildStack(t(hot, "Industrial Bakery"), 3, false);
        hot.buildStack(t(hot, "Coal Power Plant"), 1, false);
        hot.buildStack(t(hot, "Water Treatment Plant"), 1, false);
        hot.buildStack(t(hot, "Paved Road"), 10, false);
        hot.getDebtManager().setPolicyRate(DebtManager.MAX_POLICY_RATE);
        worst = play("paying the maximum for fifteen years", hot, 180, verbose);
        CapitalFlows flows = hot.getCapitalFlows();
        System.out.printf("   hot money: $%,.0fk here, $%,.0fk arrived and $%,.0fk left over the run,"
                + " on a %.2f-point spread%n",
                flows.getStock() * 1000, flows.getLifetimeArrived() * 1000,
                flows.getLifetimeDeparted() * 1000, flows.getSpread() * 100);
        assertTrue("fixture: the rate actually brought money in",
                flows.getLifetimeArrived() > 0);
        assertTrue("...and the audit saw it cross the border",
                worst.financialIn > 0 || worst.financialOut > 0
                        || hot.getLastMoneyAudit().financialIn > 0);
        assertTrue("a city conserves money with hot money flowing", worst.relative() < 1e-4);
        System.out.printf("   worst drift after a strike: $%,.2f%s%n", worstDrift,
                worstDriftPool.isEmpty() ? "" : " (" + worstDriftPool + ")");
        assertTrue("...and nothing moved after the audit struck", Math.abs(worstDrift) < .01);

        /* ==================================================================
           4. AND A WORKS THAT MAKES ITS OWN POWER AND BUYS ITS CONSUMABLES
              ABROAD (0.7.112, batch I2; docs/studies/spec-materials.md 2.5, 2.8).

           Two flows the materials chains bring, neither of which any building
           has yet - so THE FIXTURE CAUSES BOTH: the bakeries are handed power
           of their own (the Industrial Bakery three times its draw, so it
           sells the grid twice that; the Bakery half, so it buys the other
           half) and consumables, before the city founds. The power is the
           treasury's pool paying the bakers', so the audit notes it, each
           side off its own book, and the identity sees a mismatch as a
           residual; the consumables are an import, inside "- Industry
           Imports", and cross the border in TRADE.
           ================================================================== */
        System.out.println("\n--- a works making its own power and buying consumables abroad: still nothing leaks ---");
        Game works = new Game(GameFiles.scratch("moneycheck"));
        quietly(works::run);
        BuildingsTemplate big = t(works, "Industrial Bakery"), small = t(works, "Bakery");
        big.setGen(3.0 * big.getElectricityConsumption());
        small.setGen(.5 * small.getElectricityConsumption());
        big.setConsumables(.001 * big.getCashCost());
        works.setCashForTest(Founding.WEALTHY_CASH);
        works.getLandManager().setOwnedSqFt(works.getLandManager().getOwnedSqFt() + 400_000_000L);
        // ...standing from the first month (no builders in this town to put them up).
        quietly(() -> {
            works.buildStack(t(works, "House"), 400, true);
            works.buildStack(t(works, "Small Grocery Store"), 4, true);
            works.buildStack(big, 3, true);
            works.buildStack(small, 2, true);
            works.buildStack(t(works, "Coal Power Plant"), 1, true);
            works.buildStack(t(works, "Water Treatment Plant"), 1, true);
            works.buildStack(t(works, "Paved Road"), 10, true);
        });
        worst = play("a city whose bakeries make power and buy consumables, 36 months", works, 36, verbose);
        Sector industry = works.getSectors().byKey(Sectors.INDUSTRY);
        MoneyAudit.Result last = works.getLastMoneyAudit();
        double sold = industry.statement().powerSold;
        Double consumables = industry.statement().otherInputs.get(Sector.CONSUMABLES);
        System.out.printf("   the bakers sold the grid $%,.2fk of power and bought $%,.2fk of consumables abroad last month%n",
                sold, consumables == null ? 0 : consumables);
        assertTrue("fixture: the bakers sold the grid power and the utility paid them the same, each off its own book",
                sold > 0 && works.getServicesManager().getUtilitiesHandler().getPowerBought() == works.getEconomyManager().getSectorPowerSold()
                        && works.getEconomyManager().getSectorPowerSold() == sold);
        assertTrue("...and bought consumables abroad, inside their imports", consumables != null && consumables > 0
                && industry.statement().imports >= consumables);
        assertTrue("a city whose works make power and buy consumables conserves money to within 0.01% of what moved",
                worst.relative() < 1e-4);
        assertTrue("...and its audit names both: the power as a pair noted beside the utility's bills, the consumables in Industry's"
                + " imports, in TRADE", last.detail.contains("chk + sectors PowerSold") && last.detail.contains("chk - utilities PowerBought")
                && last.detail.contains("- Industry Imports") && consumables != null && last.tradeOut >= consumables);

        /* ==================================================================
           OLD STEEL (0.7.123, batch I4; OldMetal): a Scrap Yard buys the
           city's old steel at 70% of scrap's export price from whoever threw
           it out - the households for their cars, paid into their savings as
           the yard buys and out of the yard's cash at its strike, which
           leaves the pools ("- sectors OldMetalToHouseholds"); and a sector
           for its offcuts or its demolitions, a pool paying a pool in the
           strike's window, noted beside it. THE FIXTURE CAUSES IT: the town's
           own cars and offcuts are too few to run a yard, so its pool is
           handed old steel from the households and the fabricators each
           month, more than the yard takes, and the rest goes to landfill.
           ================================================================== */
        System.out.println("\n--- a Scrap Yard buying the city's old steel from the households and a sector: still nothing leaks ---");
        Game yards = new Game(GameFiles.scratch("moneycheck-oldsteel"));
        quietly(yards::run);
        // WN3: Heavy Industry (Iron & Steel) is held out of the planner's retiring as well as its building (Game: a held sector is skipped by both), so
        // the yard the fixture stands is still standing when the audit is read. On the world's band scrap's export price is 0.13 a
        // tonne (0.30 on the literals), the yard buys old steel at 70% of it and sells the scrap at it, and "a Scrap Yard would lose
        // $100k a month": the sector retires it in month 9 after eight months at a loss, the pool goes to landfill from month 10 and
        // the last month's audit has no old steel to name. The fixture is about the money audit's naming of what the yard buys, not
        // whether a yard pays at that price, and an assertion whose subject a planner decides is an assertion about the planner.
        yards.getBusinessInvestment().holdSector(Sectors.HEAVY_INDUSTRY);
        yards.setCashForTest(Founding.WEALTHY_CASH);
        yards.getLandManager().setOwnedSqFt(yards.getLandManager().getOwnedSqFt() + 400_000_000L);
        quietly(() -> {
            yards.buildStack(t(yards, "House"), 400, true);
            yards.buildStack(t(yards, "Small Grocery Store"), 4, true);
            yards.buildStack(t(yards, "Coal Power Plant"), 1, true);
            yards.buildStack(t(yards, "Water Treatment Plant"), 1, true);
            yards.buildStack(t(yards, "Paved Road"), 10, true);
            yards.buildStack(yards.getBuildingManager().getTemplate(ham.citybuildersim.sectors.HeavyIndustry.SCRAP_YARD), 1, true);
        });
        MoneyAudit.Result worstYard = MoneyAudit.Result.NONE;
        double householdsDeclared = 0, sectorsSold = 0, yardBought = 0;
        for (int m = 0; m < 24; m++) {
            yards.getOldMetal().carsScrapped(6_000 / OldMetal.STEEL_A_CAR / OldMetal.RECOVERY);
            yards.getOldMetal().fabricated(Sectors.MANUFACTURING, 6_000 / OldMetal.OFFCUTS_A_TONNE_FABRICATED / OldMetal.RECOVERY);
            quietly(() -> yards.simulateMonths(1));
            MoneyAudit.Result r = yards.getLastMoneyAudit();
            if (r.relative() > worstYard.relative()) worstYard = r;
            householdsDeclared += MoneyAudit.oldMetalBoughtOfHouseholds(yards.getSectors());
            for (Sector x : yards.getSectors().all()) {
                sectorsSold += x.statement().otherParts.getOrDefault(OldMetal.SOLD, 0.0);
                yardBought += x.statement().otherInputs.getOrDefault(OldMetal.LINE, 0.0);
            }
        }
        System.out.printf("   over 24 months the yard bought $%,.1fk of old steel: $%,.1fk of the households', $%,.1fk of the sectors'%n",
                yardBought, householdsDeclared, sectorsSold);
        assertTrue("fixture: the yard bought old steel from the households and from a sector",
                householdsDeclared > 0 && sectorsSold > 0);
        assertTrue("a city whose Scrap Yard buys its old steel conserves money to within 0.01% of what moved, every month",
                worstYard.relative() < 1e-4);
        assertTrue("...what the yard bought is the households' part and the sectors' together, to the cent: the households' declared as it"
                        + " leaves the pools, the sectors' a pool paying a pool",
                Math.abs(yardBought - householdsDeclared - sectorsSold) < .01);
        MoneyAudit.Result lastYard = yards.getLastMoneyAudit();
        assertTrue("...and its audit names them: \"- sectors OldMetalToHouseholds\" declared, and the three noted beside it",
                lastYard.detail.contains("- sectors OldMetalToHouseholds") && lastYard.detail.contains("chk + households OldMetalSold")
                        && lastYard.detail.contains("chk + sectors OldMetalSold") && lastYard.detail.contains("chk - sectors OldMetalBought"));

        /* ==================================================================
           OLD COPPER (0.7.133, batch I8; OldMetal.copper(), the spec's 2.9):
           a Scrap Copper Refinery buys the city's old copper the same way, at
           70% of COPPER_SCRAP's export price - the households' from their
           cars into their savings, out of the pools as the old steel's is,
           and a sector's for its demolitions, a pool paying a pool - and bids
           on the scrap market for the rest. THE FIXTURE CAUSES IT: the town's
           own cars hold too little copper to feed a refinery, so its pool is
           handed old copper from the households and a sector each month,
           more than the refinery takes.
           ================================================================== */
        System.out.println("\n--- a Scrap Copper Refinery buying the city's old copper from the households and a sector: still nothing leaks ---");
        Game refiners = new Game(GameFiles.scratch("moneycheck-oldcopper"));
        quietly(refiners::run);
        refiners.setCashForTest(Founding.WEALTHY_CASH);
        refiners.getLandManager().setOwnedSqFt(refiners.getLandManager().getOwnedSqFt() + 400_000_000L);
        quietly(() -> {
            refiners.buildStack(t(refiners, "House"), 400, true);
            refiners.buildStack(t(refiners, "Small Grocery Store"), 4, true);
            refiners.buildStack(t(refiners, "Coal Power Plant"), 1, true);
            refiners.buildStack(t(refiners, "Water Treatment Plant"), 1, true);
            refiners.buildStack(t(refiners, "Paved Road"), 10, true);
            refiners.buildStack(refiners.getBuildingManager().getTemplate(ham.citybuildersim.sectors.CopperRefining.SCRAP_REFINERY), 1, true);
        });
        MoneyAudit.Result worstCopper = MoneyAudit.Result.NONE;
        double copperHouseholds = 0, copperSectors = 0, copperBought = 0;
        for (int m = 0; m < 24; m++) {
            refiners.getOldMetal().carsScrapped(3_000 / OldMetal.COPPER_A_CAR / OldMetal.RECOVERY);
            refiners.getOldMetal().copper().arise(OldMetal.Source.DEMOLITIONS, Sectors.MANUFACTURING, 3_000 / OldMetal.RECOVERY);
            quietly(() -> refiners.simulateMonths(1));
            MoneyAudit.Result r = refiners.getLastMoneyAudit();
            if (r.relative() > worstCopper.relative()) worstCopper = r;
            copperHouseholds += MoneyAudit.oldMetalBoughtOfHouseholds(refiners.getSectors());
            for (Sector x : refiners.getSectors().all()) {
                copperSectors += x.statement().otherParts.getOrDefault(OldMetal.SOLD, 0.0);
                copperBought += x.statement().otherInputs.getOrDefault(OldMetal.LINE, 0.0);
            }
        }
        System.out.printf("   over 24 months the refinery bought $%,.1fk of old copper: $%,.1fk of the households', $%,.1fk of the sectors'%n",
                copperBought, copperHouseholds, copperSectors);
        assertTrue("fixture: the refinery bought old copper from the households and from a sector",
                copperHouseholds > 0 && copperSectors > 0 && refiners.getOldMetal().copper().lastBought() > 0);
        assertTrue("a city whose Scrap Copper Refinery buys its old copper conserves money to within 0.01% of what moved, every month",
                worstCopper.relative() < 1e-4);
        assertTrue("...what it bought is the households' part and the sectors' together, to the cent: the households' declared as it"
                        + " leaves the pools (\"- sectors OldMetalToHouseholds\"), the sectors' a pool paying a pool",
                Math.abs(copperBought - copperHouseholds - copperSectors) < .01
                        && refiners.getLastMoneyAudit().detail.contains("- sectors OldMetalToHouseholds"));

        /* ==================================================================
           THE CITY'S PAPER ON ITS BOOKS (CD3, spec-city-debt.md section 5;
           TreasuryCheck 9 asserts the books themselves).

           Since CD3 the city carries each piece of its paper at amortised
           cost: its interest is the effective interest, the coupon in cash
           and the discount wearing off, and a buyback gains the carrying
           value less the price. The discount wearing off is an ACCOUNTING
           figure - no pool pays it and no pool receives it - so the audit,
           which counts cash, must not see it, and neither must the cash
           coupons fall short of it. The proof is two facts together:

             - every month's audit closes, with paper sold under and over par,
               reopened, and bought back: no double count and no leak, the
               amortisation having moved no pool the audit could miss;
             - and the books' interest less the books' amortisation is the
               coupons the month paid in cash, holder by holder: the
               households', the businesses', the world's, the central bank's
               and the bank's share - the one the cash pays the month after
               and the economy accrues (EconomyManager.getInterestAccrued()).
               The cash part of the books is the cash, to the cent.

           THE FIXTURE CAUSES EACH CASE. A bill and an amortizing bond are
           sold under par; a ten-year line is opened with the dial high and
           reopened with it low, so the coupon the line prints is over the
           day's yield and the reopening sells over par; and a month on the
           amortizing bond is bought back, at a gain or a loss against its
           carrying value.
           ================================================================== */
        System.out.println("\n--- the city's paper on its books: the amortisation is no money, and the audit closes ---");
        Game paperCity = new Game(GameFiles.scratch("moneycheck-paper"));
        quietly(paperCity::run);
        paperCity.setCashForTest(Founding.WEALTHY_CASH);
        paperCity.getLandManager().setOwnedSqFt(paperCity.getLandManager().getOwnedSqFt() + 400_000_000L);
        quietly(() -> {
            paperCity.buildStack(t(paperCity, "House"), 400, true);
            paperCity.buildStack(t(paperCity, "Small Grocery Store"), 4, true);
            paperCity.buildStack(t(paperCity, "Coal Power Plant"), 1, true);
            paperCity.buildStack(t(paperCity, "Water Treatment Plant"), 1, true);
            paperCity.buildStack(t(paperCity, "Paved Road"), 10, true);
        });
        DebtManager ledger = paperCity.getDebtManager();
        NationalAccounts paperBooks = paperCity.getEconomyManager().getNationalAccounts();
        // Paper sold under par: a bill, an amortizing bond, and a ten-year line at a high dial.
        ledger.setPolicyRate(.06);
        quietly(() -> {
            paperCity.handleTBillLogic(20_000, 6, 1000);
            paperCity.handleMediumBondLogic(30_000, 10, 1000);
            paperCity.handleLongBondLogic(50_000, 10, 1000);
        });
        Debt line = ledger.benchmarkLine(10);
        Debt serial = null;
        for (Debt d : ledger.getDebt()) if (d instanceof MediumTermBond) serial = d;
        boolean allUnderPar = ledger.getDebt().size() == 3 && line != null && serial != null;
        for (Debt d : ledger.getDebt()) allUnderPar &= d.getCarryingValue() < d.getOustandingPrincipal();
        assertTrue("fixture: a bill, an amortizing bond and a ten-year line, each sold under par", allUnderPar);
        final Debt serialBond = serial;

        MoneyAudit.Result worstPaper = MoneyAudit.Result.NONE;
        double worstPaperDrift = 0, worstCouponGap = 0, worstCashGap = 0;
        double sumAmortised = 0, boughtBackFor = 0, gainQuoted = 0, gainBooked = 0;
        boolean reopenedOverPar = false, sawPremiumMonth = false, sawDiscountMonth = false, boughtBack = false;
        int amortisingMonths = 0;
        for (int month = 0; month < 36; month++) {
            if (month == 3) {
                // The dial down: the line's coupon is over the day's yield, and a reopening sells over par.
                ledger.setPolicyRate(.01);
                double carryBefore = line.carryingInCurrency(), faceBefore = line.faceInCurrency();
                int pieces = ledger.getDebt().size();
                quietly(() -> paperCity.handleLongBondLogic(20_000, 10, 1000));
                reopenedOverPar = ledger.getDebt().size() == pieces && ledger.benchmarkLine(10) == line
                        && line.carryingInCurrency() - carryBefore > line.faceInCurrency() - faceBefore + 1;
            }
            if (month == 8) quietly(() -> paperCity.handleTBillLogic(10_000, 6, 1000));
            if (month == 14 && ledger.getDebt().contains(serialBond)) {
                gainQuoted = paperCity.repurchaseGain(serialBond);
                boughtBackFor = paperCity.repurchaseDebt(serialBond);
                boughtBack = boughtBackFor > 0;
            }
            final Game stepped = paperCity;
            quietly(() -> stepped.simulateMonths(1));
            MoneyAudit.Result r = paperCity.getLastMoneyAudit();
            if (r.relative() > worstPaper.relative()) worstPaper = r;
            worstPaperDrift = Math.max(worstPaperDrift, Math.abs(paperCity.getPostAuditDrift()));
            // The books' interest less the books' amortisation, against the coupons the month paid in cash, holder by holder.
            double booked = ledger.getInterestBooked() - ledger.getAmortisedBooked();
            double paidInCash = paperCity.getCouponsToHouseholds() + paperCity.getCouponsToFirms() + paperCity.getCouponsToWorld()
                    + paperCity.getCentralBank().getPaperCoupons() + paperCity.getEconomyManager().getInterestAccrued()
                    + paperCity.getForeignInterestPaidThisMonth();
            worstCouponGap = Math.max(worstCouponGap, Math.abs(booked - paidInCash));
            worstCashGap = Math.max(worstCashGap, Math.abs(paperBooks.getInterestPaidInCash() - paidInCash));
            sumAmortised += ledger.getAmortisedBooked();
            if (Math.abs(ledger.getAmortisedBooked()) > 0) amortisingMonths++;
            for (Debt d : ledger.getDebt()) {
                if (d.getLastAmortised() < 0) sawPremiumMonth = true;
                if (d.getLastAmortised() > 0) sawDiscountMonth = true;
            }
            if (month == 14 && boughtBack) gainBooked = paperBooks.getRetiredGain();
        }
        System.out.printf("   36 months: worst audit %s; %,.2fk of discount amortised; the buyback paid %,.2fk for a quoted gain of %,.2fk%n",
                worstPaper, sumAmortised, boughtBackFor, gainQuoted);
        assertTrue("fixture: the ten-year line was reopened over par, as one piece, and raised more than the face it added", reopenedOverPar);
        assertTrue("fixture: the books amortised something in most months, a discount wearing off in some pieces and a premium below nothing in another",
                amortisingMonths >= 30 && sawDiscountMonth && sawPremiumMonth);
        assertTrue("fixture: the amortizing bond was bought back between two presses, at a gain or loss against its carrying value",
                boughtBack && Math.abs(gainQuoted) > 1);
        assertTrue("the buyback's gain reached the month's books as the gain on debt retired, the carrying value less the price: a loss here, the line having been sold dear",
                Math.abs(gainBooked - gainQuoted) < 1e-6 && gainQuoted < 0);
        assertTrue("a city whose paper is sold under and over par, reopened and bought back conserves money to within 0.01% of what moved, every month",
                worstPaper.relative() < 1e-4);
        assertTrue("...and nothing moved a pool after any month's audit struck", worstPaperDrift < .01);
        assertTrue("the books' interest less the amortisation is the coupons the month paid in cash, holder by holder, every month: the amortisation moved no pool",
                worstCouponGap < MoneyAudit.tolerance(1e-6, paperCity.getCash()));
        assertTrue("...and it is the cash coupons the budget calls its cash interest", worstCashGap < MoneyAudit.tolerance(1e-6, paperCity.getCash()));

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
