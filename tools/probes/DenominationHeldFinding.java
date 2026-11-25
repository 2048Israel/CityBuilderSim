package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Bounded observations of the original reform scans and TP1 wait, with their laws intact. */
public final class DenominationHeldFinding {
    private static final Gson JSON = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    private static PrintWriter raw;
    private static Game plain, reformed;
    private static String phase;
    private static int previousEmptyHolding, scanSnapshots, waitSnapshots;
    private record Stock(double raw, String unitKind) { }

    public static void main(String[] args) throws Exception {
        if(args.length!=2 || !(args[0].equals("cells")||args[0].equals("tp1")))
            throw new IllegalArgumentException("cells|tp1 OUTPUT_DIRECTORY");
        if(Boolean.getBoolean("denom.trace")) throw new IllegalArgumentException("denom.trace must remain absent/false");
        Path output=Path.of(args[1]); Files.createDirectories(output);
        DenominationCheck.out=System.out;
        DenominationCheck.quiet=new PrintStream(new OutputStream(){public void write(int b) { }});
        DenominationCheck.fails=0;
        try(PrintWriter writer=new PrintWriter(Files.newBufferedWriter(output.resolve(args[0]+"-raw.jsonl")))) {
            raw=writer;
            if(args[0].equals("cells")) cells(); else tp1();
            Map<String,Object> done=new LinkedHashMap<>();done.put("type","complete");done.put("mode",args[0]);
            done.put("scanSnapshots",scanSnapshots);done.put("waitSnapshots",waitSnapshots);
            done.put("nativeAssertionFailures",DenominationCheck.fails);emit(done);
        } finally {
            DenominationCheck.emptyScanObserver=null; DenominationCheck.tp1WaitObserver=null;
        }
        System.out.println("DENOMINATION_HELD_COMPLETE mode="+args[0]+" diagnostic_only=true native_assertion_failures="+DenominationCheck.fails);
        if(DenominationCheck.fails!=0)System.exit(1);
    }

    private static void cells() {
        // The same two native city() inputs and closed-trade prelude as main section 2.
        plain=DenominationCheck.city("denom-plain");
        reformed=DenominationCheck.city("denom-lopped");
        DenominationCheck.assertTrue("fixture: the two cities really are identical to start with",DenominationCheck.same(plain,reformed));
        var used=DenominationCheck.usedTradeFixture();
        DenominationCheck.assertTrue("fixture: a seller and buyer actually traded a financed used car",used.traded()>0&&used.spend()>0&&used.credit()>0);
        plain.getMotoring().restoreUsedTrade(used);reformed.getMotoring().restoreUsedTrade(used);
        phase="before-reform";snapshot(plain);snapshot(reformed);
        Map<String,Map<String,Stock>> before=stockBook(reformed);
        double openingUnit=reformed.getDenomination().getUnit();
        double factor=100;
        DenominationCheck.assertTrue("the reform goes through",DenominationCheck.force(reformed,factor));
        phase="immediately-after-reform-before-scans";snapshot(plain);snapshot(reformed);
        DenominationCheck.relative("finding: denomination unit compounds by original factor",reformed.getDenomination().getUnit(),openingUnit*factor,1e-9);
        compareStocks(before,stockBook(reformed),factor);

        DenominationCheck.emptyHolding=0;DenominationCheck.looks=0;DenominationCheck.looksWithEmpty=0;DenominationCheck.cellsFolded=0;
        previousEmptyHolding=0;
        DenominationCheck.emptyScanObserver=g->{
            int fromCatalog=snapshot(g);
            int nativeIncrement=DenominationCheck.emptyHolding-previousEmptyHolding;
            DenominationCheck.assertTrue("finding: named stock inventory equals the unchanged native scan",fromCatalog==nativeIncrement);
            previousEmptyHolding=DenominationCheck.emptyHolding;scanSnapshots++;
        };
        phase="original-initial-scans";
        DenominationCheck.scanEmpty(plain);DenominationCheck.scanEmpty(reformed);
        DenominationCheck.emptyHeldAfterReform=DenominationCheck.emptyHolding;
        phase="original-follow-month";
        DenominationCheck.stepBoth(plain,reformed,1);
        DenominationCheck.sameCity(plain,reformed,factor,1e-9);
        DenominationCheck.stepBoth(plain,reformed,11);
        DenominationCheck.sameCity(plain,reformed,factor,1e-9);
        DenominationCheck.stepBoth(plain,reformed,88);
        DenominationCheck.sameCity(plain,reformed,factor,1e-9);
        // The original three scan laws and their predicates are preserved verbatim.
        DenominationCheck.assertTrue("right after the reform, in either city",DenominationCheck.emptyHeldAfterReform==0);
        DenominationCheck.assertTrue("fixture: the census left cells under half a household",DenominationCheck.looksWithEmpty>0);
        DenominationCheck.assertTrue("...and after no month did one hold anything",DenominationCheck.emptyHolding==0);
        DenominationCheck.assertTrue("finding: all 202 original native looks were retained",DenominationCheck.looks==202&&scanSnapshots==202);
    }

    private static Map<String,Map<String,Stock>> stockBook(Game game) {
        Map<String,Map<String,Stock>> result=new LinkedHashMap<>();
        Set<Integer> bondIds=new TreeSet<>();
        for(Household c:game.getHouseholdBalance().cells())bondIds.addAll(c.bondFace.keySet());
        for(Household c:game.getHouseholdBalance().cells()) {
            if(result.put(c.key(),stocks(c,bondIds))!=null)throw new IllegalStateException("duplicate actual cell key "+c.key());
        }
        return result;
    }

    /** Same inventory as HouseholdBalance.stocks(int[]), read directly without its bondStockIds cache assignment. */
    private static Map<String,Stock> stocks(Household c,Set<Integer> bondIds) {
        Map<String,Stock> s=new LinkedHashMap<>();
        local(s,"savings",c.savings);local(s,"debt",c.debt);
        for(int k=0;k<Equity.COMPANIES.length;k++)s.put("shares/"+Equity.COMPANIES[k],new Stock(c.shares[k],"count"));
        s.put("abroad",new Stock(c.abroad,"USD"));local(s,"studentDebt",c.studentDebt);
        s.put("cars",new Stock(c.cars,"count"));local(s,"paper",c.paper);
        for(int id:bondIds)local(s,"bondFace/"+id,c.bondFace.getOrDefault(id,0.0));
        for(int k=0;k<Equity.COMPANIES.length;k++)local(s,"basis/"+Equity.COMPANIES[k],c.basis[k]);
        for(int k=0;k<Equity.COMPANIES.length;k++) {
            s.put("taxShares/"+Equity.COMPANIES[k],new Stock(c.taxShares[k],"count"));
            local(s,"taxClaim/"+Equity.COMPANIES[k],c.taxClaim[k]);
        }
        local(s,"lossCarried",c.lossCarried);s.put("revolving",new Stock(c.revolving,"share"));
        local(s,"lateDebt",c.lateDebt);s.put("lateShare",new Stock(c.lateShare,"share"));
        s.put("shutOut",new Stock(c.shutOut,"share"));return s;
    }
    private static void local(Map<String,Stock> s,String name,double value){s.put(name,new Stock(value,"local-thousands"));}

    /** Every stock compared with the original relative guard, including zero and unchanged real/USD stocks. */
    private static void compareStocks(Map<String,Map<String,Stock>> before,Map<String,Map<String,Stock>> after,double factor) {
        Set<String> keys=new TreeSet<>(before.keySet());keys.addAll(after.keySet());
        for(String key:keys) {
            DenominationCheck.assertTrue("finding: reform keeps actual cell key "+key,before.containsKey(key)&&after.containsKey(key));
            Map<String,Stock> b=before.getOrDefault(key,Map.of()),a=after.getOrDefault(key,Map.of());
            Set<String> names=new TreeSet<>(b.keySet());names.addAll(a.keySet());
            for(String name:names) {
                Stock old=b.get(name),now=a.get(name);
                String kind=old!=null?old.unitKind():now.unitKind();
                double expected=old==null?0:old.raw()/(kind.equals("local-thousands")?factor:1);
                double actual=now==null?0:now.raw();
                DenominationCheck.relative("finding: original scale guard "+key+"/"+name,actual,expected,1e-9);
                Map<String,Object> row=new LinkedHashMap<>();row.put("type","scale-comparison");row.put("key",key);row.put("stock",name);
                row.put("unitKind",kind);row.put("beforeRaw",old==null?0:old.raw());row.put("afterRaw",actual);
                row.put("expectedRaw",expected);row.put("beforeHex",Double.toHexString(old==null?0:old.raw()));
                row.put("afterHex",Double.toHexString(actual));row.put("factor",factor);row.put("relativeBand",1e-9);emit(row);
            }
        }
    }

    /** Return the same scan's under-.5 holders counted from its complete named inventory. */
    private static int snapshot(Game g) {
        var hb=g.getHouseholdBalance();double[] saveBefore=hb.toCellSaveArray(),indexBefore=g.getPriceIndex().toSaveArray();
        int zero=0,fraction=0,large=0,invalid=0,legacyHolders=0;
        double zeroHouseholds=0,fractionHouseholds=0,largeHouseholds=0;
        Map<String,Object> heading=new LinkedHashMap<>();heading.put("type","snapshot");heading.put("phase",phase);
        heading.put("city",g==plain?"plain":g==reformed?"reformed":"tp1");heading.put("month",g.getMonth());
        heading.put("unit",g.getDenomination().getUnit());heading.put("nativeLooks",DenominationCheck.looks);
        heading.put("nativeEmptyHolding",DenominationCheck.emptyHolding);heading.put("legacyEmptyThreshold",HouseholdBalance.EMPTY_CELL);
        heading.put("nativeCellKeys",hb.cellKeys());heading.put("nativeCellSave",saveBefore);heading.put("nativeRowSave",hb.toSaveArray());
        heading.put("nativePriceIndex",indexBefore);heading.put("nativeFlows",hb.flowsToSave());emit(heading);
        Map<String,Map<String,Stock>> book=stockBook(g);
        for(Household c:hb.cells()) {
            Map<String,Stock> inventory=book.get(c.key());boolean any=inventory.values().stream().anyMatch(v->v.raw()!=0);
            String group=c.households()==0?"exact-zero":c.households()>0&&c.households()<HouseholdBalance.EMPTY_CELL?"positive-under-half":c.households()>=HouseholdBalance.EMPTY_CELL?"at-least-half":"invalid-count";
            if(any) {
                switch(group){case "exact-zero"->{zero++;zeroHouseholds+=c.households();}case "positive-under-half"->{fraction++;fractionHouseholds+=c.households();}case "at-least-half"->{large++;largeHouseholds+=c.households();}default->invalid++;}
                if(c.isEmpty())legacyHolders++;
            }
            Map<String,Object> row=new LinkedHashMap<>();row.put("type","cell");row.put("phase",phase);
            row.put("city",g==plain?"plain":g==reformed?"reformed":"tp1");row.put("month",g.getMonth());
            row.put("key",c.key());row.put("row",c.row());row.put("households",c.households());row.put("householdsHex",Double.toHexString(c.households()));
            row.put("group",group);row.put("legacyIsEmpty",c.isEmpty());row.put("holdsAnyNamedStock",any);
            row.put("unit",g.getDenomination().getUnit());row.put("bondsAggregate",c.bonds);row.put("bondsAggregateHex",Double.toHexString(c.bonds));
            Map<String,Object> held=new LinkedHashMap<>();
            for(var e:inventory.entrySet()) {
                Stock value=e.getValue();Map<String,Object> v=new LinkedHashMap<>();v.put("rawPerHousehold",value.raw());v.put("rawHex",Double.toHexString(value.raw()));
                v.put("weightedRaw",value.raw()*c.households());v.put("unitKind",value.unitKind());
                v.put("foundingUnitPerHousehold",value.raw()*(value.unitKind().equals("local-thousands")?g.getDenomination().getUnit():1));held.put(e.getKey(),v);
            }
            row.put("stocks",held);emit(row);
        }
        Map<String,Object> summary=new LinkedHashMap<>();summary.put("type","ownership-summary");summary.put("phase",phase);
        summary.put("city",g==plain?"plain":g==reformed?"reformed":"tp1");summary.put("month",g.getMonth());
        summary.put("exactZeroOwners",zero);summary.put("positiveUnderHalfOwners",fraction);summary.put("atLeastHalfOwners",large);summary.put("invalidCountOwners",invalid);
        summary.put("exactZeroOwnerHouseholds",zeroHouseholds);summary.put("positiveUnderHalfOwnerHouseholds",fractionHouseholds);summary.put("atLeastHalfOwnerHouseholds",largeHouseholds);
        summary.put("nativeScanPredicateHolders",legacyHolders);emit(summary);raw.flush();
        DenominationCheck.assertTrue("finding: exact-zero household counts retain no raw owned position",zero==0);
        DenominationCheck.assertTrue("finding: observation keeps exact native household and CPI arrays",Arrays.equals(saveBefore,hb.toCellSaveArray())&&Arrays.equals(indexBefore,g.getPriceIndex().toSaveArray()));
        return legacyHolders;
    }

    private static void tp1() throws java.io.IOException {
        Path root=TempDirs.create("denom-held-tp1");
        DenominationCheck.tp1WaitObserver=(a,b)->{
            phase="original-tp1-price-wait";captureWait(a,"plain");captureWait(b,"reformed");waitSnapshots++;
        };
        // Exact existing world-city/July reform/year-turn/12-wait code and all original assertions.
        DenominationCheck.reformedInMidYear(root);
    }

    private static void captureWait(Game g,String side) {
        double[] indexBefore=g.getPriceIndex().toSaveArray(),cellBefore=g.getHouseholdBalance().toCellSaveArray();
        var table=DenominationCheck.tradeOf(g).snapshot();
        double founding=TradeLanes.minLane(1,1);
        Map<String,Object> row=new LinkedHashMap<>();row.put("type","tp1-wait");row.put("side",side);row.put("waitObservation",waitSnapshots);
        row.put("month",g.getMonth());row.put("tradeMonth",table.month());row.put("index",g.getPriceIndex().getIndex());row.put("unit",g.getDenomination().getUnit());
        row.put("actualMinLane",table.minLane());row.put("foundingFloor",founding);row.put("originalWaitThreshold",founding*(1+1e-9));
        row.put("nativeIndexState",indexBefore);row.put("linkedMonth",g.getPriceIndex().getLinkedMonth());
        row.put("knownRedStableLabel","the price index has moved");
        row.put("actualAssertionLabel","the price index has moved ("+String.format("%.4f",g.getPriceIndex().getIndex())+"), and the floor with it: above the founding's");
        emit(row);raw.flush();
        DenominationCheck.assertTrue("finding: TP1 read keeps exact native household and CPI arrays",Arrays.equals(indexBefore,g.getPriceIndex().toSaveArray())&&Arrays.equals(cellBefore,g.getHouseholdBalance().toCellSaveArray()));
    }
    private static void emit(Map<String,Object> value){raw.println(JSON.toJson(value));}
}
