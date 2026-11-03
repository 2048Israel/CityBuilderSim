package ham.citybuildersim;

public class Ld2Wood {
    public static void main(String[] a) {
        long seed = Long.parseLong(a[0]);
        int from = Integer.parseInt(a[1]), to = Integer.parseInt(a[2]);
        String key = a.length > 3 ? a[3] : "Wood Products";
        Game g = new Game(GameFiles.scratch("ld2-wood-" + seed));
        g.newGame(Founding.defaults().withPath(Nations.Path.RANDOM, seed));
        g.setRescueMode(TreasuryFund.RescueMode.BUTTON);
        BusinessDebtManager d = g.getEconomyManager().getBusinessDebtManager();
        for (int m = 0; m < to; m++) {
            g.toggleNextMonth();
            Sector s = g.getSectors().byKey(key);
            Bank b = g.getBank();
            if (g.getMonth() < from || s == null) continue;
            Sector.Statement st = s.statement();
            BalanceSheet bs = s.getBalanceSheet();
            System.out.printf("m%d rev %.1f (loc %.1f exp %.1f) inputs %.1f pay %.1f int %.1f net %.1f | cash %.1f assets %.1f princ %.1f | wo %.1f allow %.1f stage %d | bankEq %.0f%n",
                    g.getMonth(), st.revenue, st.localSales, st.exports, st.inputs, st.payroll, st.interest, st.netIncome,
                    bs.getCash(), bs.getTotalAssets(), d.getPrincipal(key), b.getWrittenOff(key), b.getSectorAllowance(key), b.getStage(key), b.equity());
            double[][] k = d.debtByKind(key, g.getMonth());
            System.out.printf("   owed mortgage %.1f interim %.1f loan %.1f | interimLent %.1f matured %.1f interimWO %.1f restructured %b defaulted %.1f | bldg %.1f land %.1f inv %.1f bonds %.1f | refusal %s%n",
                    k[0][0], k[0][1], k[0][2], d.getInterimLentThisMonth(key), d.getInterimMaturedThisMonth(key), d.getInterimWrittenOffThisMonth(key),
                    d.wasRestructuredThisMonth(key), d.getDefaultedThisMonth(key), bs.getBuildings(), bs.getLand(), bs.getInventory(), bs.getBondsPayable(),
                    d.getInterimRefusal(key));
            try {
                java.lang.reflect.Field f = BuildingManager.class.getDeclaredField("stacks");
                f.setAccessible(true);
                StringBuilder sb = new StringBuilder("   stacks:");
                for (Object o : (java.util.List<?>) f.get(g.getBuildingManager())) {
                    BuildingsStacks st2 = (BuildingsStacks) o;
                    if (!st2.getBuilding().getSector().equals(key)) continue;
                    sb.append(String.format(" %s q%d uc%d;", st2.getBuilding().getName(), st2.getQuantity(), st2.getUnderConstruction()));
                }
                sb.append(String.format(" valueBySector %.1f", g.getBuildingManager().getBuildingsValueBySector(key)));
                System.out.println(sb);
            } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
            if (b.isInResolution()) break;
        }
    }
}
