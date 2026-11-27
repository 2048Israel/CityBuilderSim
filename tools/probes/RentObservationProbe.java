package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Observes the default player's billed household flows and linked CPI without changing the city. */
public final class RentObservationProbe {
    private static final Gson JSON = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    private static PrintWriter months, cells, ordinary;
    private static List<HousingChoice.Lease> billedLeases = List.of();
    private static List<Map<String,Object>> openingCells = List.of(), openingRows = List.of();
    private static int openingMonth, openingClear, captured;
    private static long openingPopulation;
    private static boolean paired;
    private static final int ORDINARY_MONTHS = 600, EXPECTED_INITIAL_CLOCK = 1;
    private static Game openingGame;
    private static int initialClock = -1, lastSettledClock = -1, ordinarySteps, ordinaryRows;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("one external output directory required");
        // These properties precede the FIRST use/initialization of LongPlaytest.
        // Optional policy, founding, fee, builder and rate switches must be absent.
        for (String key : System.getProperties().stringPropertyNames()) {
            if (key.startsWith("playtest.") || key.startsWith("ensemble."))
                throw new IllegalArgumentException("unexpected inherited experiment property: " + key);
        }
        System.setProperty("playtest.world", "random");
        System.setProperty("playtest.pathSeed", "48213");
        System.setProperty("playtest.seed", "0");
        // TARGET_MONTHS is a terminal game clock, not a transition count (Game starts at 1).
        System.setProperty("playtest.months", Integer.toString(EXPECTED_INITIAL_CLOCK + ORDINARY_MONTHS));
        Path output = Path.of(args[0]);
        Files.createDirectories(output);
        months = new PrintWriter(Files.newBufferedWriter(output.resolve("monthly.jsonl")));
        cells = new PrintWriter(Files.newBufferedWriter(output.resolve("cells.jsonl")));
        ordinary = new PrintWriter(Files.newBufferedWriter(output.resolve("ordinary-audits.jsonl")));
        LongPlaytest.passiveObserver = RentObservationProbe::observe;
        // Reuse the ENTIRE committed DEFAULT script, including its exact founding,
        // ordinary audited months, annual player and normal checkpoint/reload work.
        LongPlaytest.main(new String[0]);
        throw new IllegalStateException("DEFAULT driver returned before observation completed");
    }

    private static void observe(Game game, Boolean before) {
        if (before) {
            if (paired) throw new IllegalStateException("second opening callback before settlement");
            if (initialClock < 0) {
                initialClock = game.getMonth();
                if (initialClock != EXPECTED_INITIAL_CLOCK)
                    throw new IllegalStateException("unexpected DEFAULT initial clock " + initialClock);
                lastSettledClock = initialClock;
            }
            if (game.getMonth() != lastSettledClock || ordinarySteps >= ORDINARY_MONTHS)
                throw new IllegalStateException("ordinary clock/callback gap at " + game.getMonth());
            openingGame = game;
            openingMonth = game.getMonth();
            openingPopulation = game.getPopulationManager().getPopulation();
            var housing = game.getSectors().realEstate().housingMarket();
            openingClear = housing.clearedMonth();
            // Immutable held contracts that the upcoming early household strike bills.
            // End-of-month leases would already describe the NEXT strike.
            billedLeases = List.copyOf(housing.leases());
            openingCells = rawCells(game);
            openingRows = rawRows(game);
            paired = true;
            return;
        }
        int month = game.getMonth();
        if (!paired || game != openingGame || month != openingMonth + 1)
            throw new IllegalStateException("unpaired ordinary month " + openingMonth + " -> " + month);
        paired = false;
        openingGame = null;
        int completed = ordinarySteps + 1;
        if (month != initialClock + completed)
            throw new IllegalStateException("ordinary step/clock mismatch " + completed + " at " + month);
        double[] indexBefore = game.getPriceIndex().toSaveArray();
        double[] cellBefore = game.getHouseholdBalance().toSaveArray();
        Map<String,Object> audit = new LinkedHashMap<>();
        audit.put("month",month); audit.put("initialClock",initialClock); audit.put("ordinaryStep",completed);
        audit.put("phase","after unchanged LongPlaytest.audit/countWorks, before traceMonth");
        audit.put("ordinaryFindingKinds",LongPlaytest.findings.size());
        audit.put("ordinaryFindingCount",LongPlaytest.findings.values().stream().mapToInt(f->f.count).sum());
        audit.put("findings",JSON.toJsonTree(LongPlaytest.findings));
        audit.put("moneyAudit",JSON.toJsonTree(game.getLastMoneyAudit()));
        audit.put("householdsAudit",JSON.toJsonTree(game.getLastHouseholdsAudit()));
        audit.put("moneyFixtureFails",LongPlaytest.MoneyFixtures.fails);
        audit.put("finalProbesExecuted",false); ordinary.println(JSON.toJson(audit));
        if (month >= 240 && month <= 600) capture(game);
        if (!Arrays.equals(indexBefore, game.getPriceIndex().toSaveArray())
                || !Arrays.equals(cellBefore, game.getHouseholdBalance().toSaveArray()))
            throw new IllegalStateException("observer changed saved index/household values");
        ordinarySteps = completed;
        ordinaryRows++;
        lastSettledClock = month;
        months.flush(); cells.flush(); ordinary.flush();
        if (ordinarySteps == ORDINARY_MONTHS) {
            if (ordinaryRows != ORDINARY_MONTHS || month != initialClock + ORDINARY_MONTHS)
                throw new IllegalStateException("ordinary callback/row/clock count differs");
            if (captured != 361) throw new IllegalStateException("window has " + captured + " observations");
            boolean ordinaryClear=LongPlaytest.findings.isEmpty()&&LongPlaytest.MoneyFixtures.fails==0;
            boolean outputClear=!months.checkError()&&!cells.checkError()&&!ordinary.checkError();
            months.close(); cells.close(); ordinary.close();
            LongPlaytest.out.println("RENT_OBSERVATION_COMPLETE initial_clock="+initialClock+" final_clock="+month
                    +" ordinary_steps="+ordinarySteps+" ordinary_rows="+ordinaryRows+" observed_clock=240-600 rows="+captured
                    +" diagnostic_only=true ordinary_clear="+ordinaryClear
                    +" output_clear="+outputClear+" finding_kinds="+LongPlaytest.findings.size()+" money_fixture_fails="+LongPlaytest.MoneyFixtures.fails);
            // LongPlaytest.main catches Throwable and then starts unrelated final
            // harness probes. Stop this sole diagnostic JVM after its last ordinary
            // month; the JVM's existing TempDirs shutdown cleanup remains active.
            System.exit(ordinaryClear&&outputClear?0:1);
        }
    }

    private static void capture(Game g) {
        var a = g.getHouseholds();
        var housing = g.getSectors().realEstate().housingMarket();
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("month", g.getMonth()); m.put("openingMonth", openingMonth);
        m.put("phase", "LongPlaytest.run after audit/countWorks, before traceMonth; leases frozen before preceding simulateMonths(1)");
        m.put("openingClearMonth", openingClear); m.put("currentClearMonth", housing.clearedMonth());
        m.put("moneyUnit", g.getDenomination().getUnit());
        m.put("population", g.getPopulationManager().getPopulation()); m.put("openingPopulation", openingPopulation);
        m.put("doors", g.getBuildingManager().getTotalHomes());
        m.put("houseCapacity", g.getBuildingManager().getTotalHouseCapacity());
        m.put("households", g.getFamilies().totalHouseholds()); m.put("unplaced", g.getFamilies().getStillUnplaced());
        m.put("pipeline", g.getBuildingManager().getHousingInventory().pipelineHomes());
        m.put("rent", a.getRent()); m.put("householdSpending", a.getSpending());
        m.put("consumption", g.getEconomyManager().getNationalAccounts().getConsumption());
        m.put("consumptionHousing", g.getEconomyManager().getNationalAccounts().getConsumptionHousing());
        m.put("consumptionGoods", g.getEconomyManager().getNationalAccounts().getConsumptionGoods());
        m.put("indexSpends", g.indexSpends()); m.put("indexFeeSpends", g.indexFeeSpends());
        m.put("indexPrices", g.indexPrices()); m.put("indexFees", g.indexFees());
        m.put("indexRaw", indexSnapshot(g));
        m.put("cpi", g.getPriceIndex().getIndex()); m.put("linkedMonth", g.getPriceIndex().getLinkedMonth());
        double[] weights = new double[PriceIndex.COMPONENTS];
        for (int k=0;k<weights.length;k++) weights[k]=g.getPriceIndex().getWeight(k);
        m.put("weights", weights); m.put("componentNames", PriceIndex.COMPONENT_NAMES);
        m.put("billedLeases", billedLeases);
        m.put("openingCells",openingCells); m.put("openingRows",openingRows);
        m.put("settledHeldCellsAtClose",rawCells(g));
        m.put("billedLeaseTotal", billedLeases.stream().mapToDouble(HousingChoice.Lease::bill).sum());
        m.put("billedRentIncome", g.getSectors().realEstate().getRentIncomeBilled());
        m.put("moneyAudit", JSON.toJsonTree(g.getLastMoneyAudit()));
        double[] incomeWeight = new double[a.getRowCount()];
        for (Household c:g.getHouseholdBalance().cells())
            if (c.households()>0) incomeWeight[c.row()]+=c.households()*c.earningWeight();
        List<Map<String,Object>> rows = new ArrayList<>();
        for (int r=0;r<a.getRowCount();r++) {
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("row",r); row.put("label",a.getRowLabel(r)); row.put("households",a.getRowHouseholds(r));
            row.put("wages",a.getRowWages(r)); row.put("pensions",a.getRowPensions(r)); row.put("benefits",a.getRowBenefits(r));
            row.put("tax",a.getRowTax(r)); row.put("contributions",a.getRowContributions(r));
            row.put("eiPremiums",a.getRowEiPremiums(r)); row.put("healthPremiums",a.getRowHealthPremiums(r));
            row.put("rent",a.getRowRent(r)); row.put("disposable",a.getRowDisposable(r)); rows.add(row);
        }
        m.put("rows",rows);
        Set<String> positiveKeys = new HashSet<>();
        for(Household c:g.getHouseholdBalance().cells())if(c.households()>0)positiveKeys.add(c.key());
        m.put("unmatchedBilledLeases",billedLeases.stream().filter(l->positiveKeys.stream()
                .noneMatch(key->l.cell().equals(key)||l.cell().startsWith(key+"#"))).toList());
        m.put("billedLeaseMinusAccountRent",billedLeases.stream().mapToDouble(HousingChoice.Lease::bill).sum()-a.getRent());
        for (Household c:g.getHouseholdBalance().cells()) {
            if (!(c.households()>0)) continue; // Never isEmpty()/0.5: EVERY positive count.
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("month",g.getMonth()); row.put("key",c.key()); row.put("row",c.row());
            row.put("households",c.households()); row.put("people",c.people()); row.put("earningWeight",c.earningWeight());
            row.put("disposable",c.disposable()); row.put("investmentIncomeNet",c.investmentIncome);
            row.put("depositIncome",c.depositIncome); row.put("assistance",c.assistance());
            row.put("unfunded",c.unfunded()); row.put("accountFee",c.accountFee());
            double gross=a.getRowWages(c.row())+a.getRowPensions(c.row())+a.getRowBenefits(c.row());
            double grossProxy=incomeWeight[c.row()]>0?gross*c.earningWeight()/incomeWeight[c.row()]:0;
            row.put("scheduledGrossProxyPerHousehold",grossProxy);
            row.put("cashGrossProxyPerHousehold",grossProxy+c.investmentIncome+c.depositIncome+c.assistance());
            row.put("billedLeases",billedLeases.stream().filter(l->l.cell().equals(c.key())||l.cell().startsWith(c.key()+"#")).toList());
            cells.println(JSON.toJson(row));
        }
        months.println(JSON.toJson(m)); captured++;
    }

    private static double[] indexSnapshot(Game game) { return game.getPriceIndex().toSaveArray(); }

    private static List<Map<String,Object>> rawCells(Game game) {
        List<Map<String,Object>> result=new ArrayList<>();
        // Keep even exact-zero cells here: extinct billed keys cannot disappear silently.
        for(Household c:game.getHouseholdBalance().cells()) {
            Map<String,Object> v=new LinkedHashMap<>();
            v.put("key",c.key());v.put("row",c.row());v.put("households",c.households());
            v.put("earningWeight",c.earningWeight());v.put("peopleAtThisRead",c.people());
            v.put("disposable",c.disposable());v.put("investmentIncomeNet",c.investmentIncome);
            v.put("depositIncome",c.depositIncome);v.put("assistance",c.assistance());
            result.add(v);
        }
        return result;
    }

    private static List<Map<String,Object>> rawRows(Game game) {
        var a=game.getHouseholds();List<Map<String,Object>> result=new ArrayList<>();
        for(int r=0;r<a.getRowCount();r++) {
            Map<String,Object> v=new LinkedHashMap<>();v.put("row",r);v.put("label",a.getRowLabel(r));
            v.put("households",a.getRowHouseholds(r));v.put("wages",a.getRowWages(r));
            v.put("pensions",a.getRowPensions(r));v.put("benefits",a.getRowBenefits(r));
            v.put("tax",a.getRowTax(r));v.put("contributions",a.getRowContributions(r));
            v.put("eiPremiums",a.getRowEiPremiums(r));v.put("healthPremiums",a.getRowHealthPremiums(r));
            v.put("rent",a.getRowRent(r));v.put("disposable",a.getRowDisposable(r));result.add(v);
        }
        return result;
    }
}
