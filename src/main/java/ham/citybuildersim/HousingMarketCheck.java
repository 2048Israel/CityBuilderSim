package ham.citybuildersim;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.google.gson.Gson;

/**
 * Integrated quotes, funded sites, rental receipts, budgets and reload evidence.
 * WHY (RES3): a synthetic catalogue quote does not prove an actual funded project
 * or a month whose occupied rents and household cash reconcile.
 */
public final class HousingMarketCheck {
    static PrintStream out;
    static int fails;
    static final Gson JSON=new Gson();
    static void check(String label,boolean ok){out.println((ok?"  ok   ":"  FAIL ")+label);if(!ok)fails++;}
    static void near(String label,double a,double b){check(label+" ("+a+", expected "+b+")",Math.abs(a-b)<=Math.max(1e-8,Math.ulp(b)*64));}
    static void audits(Game g){
        var a=g.getLastMoneyAudit();var h=g.getLastHouseholdsAudit();
        check("month "+g.getMonth()+" pools conserve money",Math.abs(a.residual)<=MoneyAudit.tolerance(Math.abs(a.before)+Math.abs(a.after)+a.inflows+a.outflows));
        check("month "+g.getMonth()+" households conserve money",Math.abs(h.residual)<=MoneyAudit.tolerance(h.moved()+Math.abs(h.before)+Math.abs(h.after)));
    }
    static void classes(Game g){
        var market=g.getSectors().realEstate().housingMarket();
        check("exactly fourteen form recipes",HousingForms.ALL.size()==14);
        check("exactly thirty-two comparable class quotes",market.classes().size()==HousingRecipe.MAX_CLASSES);
        double receipt=0;
        for(var c:market.classes()){
            check("finite nonnegative class ask "+c.recipe+"/"+c.dwelling,Double.isFinite(c.ask)&&c.ask>=0);
            check("occupancy is bounded by actual RE titles "+c.recipe+"/"+c.dwelling,c.occupied<=c.homes+FamilyModel.packingRounding(c.homes));
            if(c.tradeQuantity==0)near("vacancy earns no rent "+c.recipe+"/"+c.dwelling,c.tradeValue,0);
            receipt+=market.leases().stream().filter(l->l.home().equals(new HousingInventory.Key(c.recipe,c.dwelling))).mapToDouble(HousingChoice.Lease::bill).sum();
        }
        near("rental proceeds are exactly actual occupied leases",market.receipts(),receipt);
        near("sector bills only actual occupied leases",g.getSectors().realEstate().getRentIncome(),receipt);
        var state=JSON.toJson(market.forSave());
        // A second call in the same month is not a second quote/clearing or a trade.
        market.clear(g,List.of());check("one clearing per month",state.equals(JSON.toJson(market.forSave())));
    }
    static void writeReport(StringBuilder report,Game g,String label,Map<Integer,Long> starts,Map<Integer,Long> opening,int months,double ms){
        var market=g.getSectors().realEstate().housingMarket();var bm=g.getBuildingManager();var em=g.getEconomyManager();
        report.append("\n## ").append(label).append("\n\nIntegrated month: ").append(String.format(Locale.ROOT,"%.3f",ms)).append(" ms. H/T/A/M/L = ")
                .append(market.forSave().demand.size()).append('/').append(market.classes().size()).append('/').append(market.leases().size())
                .append("/0/").append(bm.getHousingInventory().forSave().stream().filter(h->h.listedVacant+h.listedOccupied>0).count())
                .append(". M counts new typed loan cohorts (ownership follows in HO1); L counts live listing cohorts. Physical cohorts: ")
                .append(bm.getHousingInventory().stock().size()).append(".\n\n");
        report.append("| Form | ID | Since / known | Floors / gross m² / site ft² | Materials / points | Cash / project quote | Payroll posts / power / water | Wage scale | Funded starts | Completed / months |\n|---|---:|---|---|---|---|---|---|---:|---|\n");
        for(var f:HousingForms.ALL){var t=bm.getTemplate(f.id());var stack=bm.getStack(t);long completed=stack==null?0:stack.getQuantity()-opening.getOrDefault(f.id(),0L);
            report.append(String.format(Locale.ROOT,"| %s | %d | %d / %s | %d / %.1f / %.0f | %d / %d | %.6f / %.6f | %d / %d / %.3f | %.6f | %d | %d / %d |%n",f.code(),f.id(),t.getSince(),!g.foundedUnfitted()||t.knownAt(g.getMonth(),g.getResearch()),f.floors(),f.grossArea(),f.plot(),t.getConstructionMaterials(),t.getConstructionPoints(),t.getCashCost(),g.getBusinessInvestment().getCostOf(t,1),f.staff(),t.getElectricityConsumption(),t.getWaterConsumption(),g.getRecipes().datedScale(t),starts.getOrDefault(f.id(),0L),completed,months));}
        double gross=2*em.getWageRates()[JobType.NO_DIPLOMA.ordinal()];
        double count=g.getHouseholds().getRowHouseholds(PayTier.UNSKILLED.ordinal());
        double net=count>0?g.getHouseholds().getRowDisposable(PayTier.UNSKILLED.ordinal())/count:0;
        report.append("\nMoney is in current model thousands. Gross comparator is two current unskilled wages; take-home is the actual unskilled household row mean. Land is charged once by the existing builder quote. Optional parking has capital but no assumed lease receipts.\n\n")
                .append("| Form / dwelling | Area / bedrooms / places / quality | Private / included bays | Ask | Gross burden | Take-home burden | RE doors / vacancy | Last trade qty / value / price / stale |\n|---|---|---|---:|---:|---:|---|---|\n");
        for(var f:HousingForms.ALL)for(var d:f.recipe().mix()){
            var c=market.classes().stream().filter(s->s.recipe==f.id()&&s.dwelling.equals(d.id())).findFirst().orElseThrow();
            report.append(String.format(Locale.ROOT,"| %s / %s | %.0f / %d / %d / %.2f | %.2f / %.2f | %.6f | %.4f | %s | %.3f / %.3f | %.3f / %.6f / %.6f / %d |%n",f.code(),d.id(),d.area(),d.bedrooms(),d.capacity(),d.quality(),d.privateParking(),d.includedParking(),c.ask,gross>0?c.ask/gross:0,net>0?String.format(Locale.ROOT,"%.4f",c.ask/net):"no take-home",c.homes,c.homes-c.occupied,c.tradeQuantity,c.tradeValue,c.lastTrade,c.stale));}
    }
    static void city(StringBuilder report,boolean historical)throws Exception{
        String label=historical?"Historical founding (1900)":"Present-day equipment founding";
        out.println("--- "+label+" ---");
        GameFiles files=GameFiles.scratch(historical?"res3-historical":"res3-present");
        Game g=new Game(files,Founding.defaults().withUnfitted(historical));g.run();
        var bm=g.getBuildingManager();var re=g.getSectors().realEstate();
        Map<Integer,Long> starts=new HashMap<>(),opening=new HashMap<>();
        // Explicit fixture endowment before the audit opens, followed by the actual paid build path.
        re.setCash(2_000_000_000*PriceLevel.FACTOR);
        LongPlaytest.build(g,"House",40);
        LongPlaytest.build(g,"Convenience Store",3);
        LongPlaytest.build(g,"Mixed Farm",2);
        for(int i=0;i<3;i++){g.toggleNextMonth();audits(g);}
        LongPlaytest.build(g,"House",20);
        for(int i=0;i<4;i++){g.toggleNextMonth();audits(g);}
        LongPlaytest.build(g,"Convenience Store",2);
        LongPlaytest.build(g,"Construction Depot",1);
        for(int i=0;i<11;i++){g.toggleNextMonth();audits(g);}
        check("staged fixture completed real housing before catalogue-wide orders",bm.getTotalHomes()>0);
        for(var f:HousingForms.ALL){var t=bm.getTemplate(f.id());var s=bm.getStack(t);opening.put(f.id(),s==null?0:s.getQuantity());
            long before=s==null?0:s.getUnderConstruction();LongPlaytest.build(g,t.getName(),1);
            s=bm.getStack(t);starts.put(f.id(),(s==null?0:s.getUnderConstruction())-before);
            if(historical&&f.since()>1900)check("modern form unavailable in 1900 "+f.code(),starts.get(f.id())==0&&!t.knownAt(g.getMonth(),g.getResearch()));
        }
        double ms=0;
        for(int i=0;i<24;i++){long at=System.nanoTime();g.toggleNextMonth();ms=(System.nanoTime()-at)/1e6;audits(g);}
        classes(g);
        check("fixture caused funded residential starts",starts.values().stream().mapToLong(Long::longValue).sum()>0);
        check("fixture has actual household demand",g.getFamilies().householdsSeekingDoors()>0);
        check("fixture has actual occupied rental homes",re.housingMarket().occupied()>0);
        writeReport(report,g,label,starts,opening,24,ms);
        String saved=JSON.toJson(re.housingMarket().forSave());
        g.saveGame(10);Game back=new Game(files);back.loadGame(10);
        check("native reload preserves asks, assignments, actual trades and staleness",saved.equals(JSON.toJson(back.getSectors().realEstate().housingMarket().forSave())));
        g.toggleNextMonth();back.toggleNextMonth();audits(g);audits(back);
        check("next native month preserves the class market",JSON.toJson(re.housingMarket().forSave()).equals(JSON.toJson(back.getSectors().realEstate().housingMarket().forSave())));
    }
    public static void main(String[] args)throws Exception{
        out=System.out;PrintStream silent=new PrintStream(OutputStream.nullOutputStream());
        StringBuilder report=new StringBuilder("# RES3 actual quote, budget and availability report\n\nGenerated by HousingMarketCheck through scratch cities, model inputs and the paid build path. Starting geometry, capital partitions and reference anchors are RES1 section 12; observed completions and receipts below are actual model outcomes.\n");
        try{System.setOut(silent);city(report,false);city(report,true);}finally{System.setOut(out);silent.close();}
        Path target=Path.of("target/RES3-catalogue-report.md");Files.writeString(target,report);
        out.println("Report: "+target);out.println(fails==0?"HousingMarketCheck: ALL PASSED":fails+" FAILED");if(fails>0)System.exit(1);
    }
}
