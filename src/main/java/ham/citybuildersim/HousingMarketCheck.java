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
    /** Two billion present-day dollars of fixture equity, before the first audit opens. */
    static final double SECTOR_ENDOWMENT=2_000_000*PriceLevel.FACTOR;
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
        check("retired dependants are not misclassified as children",market.demands().stream().filter(HousingChoice.Demand::retired).allMatch(d->d.children()==0));
        for(var f:HousingForms.ALL){
            double privateBays=f.recipe().mix().stream().mapToDouble(d->d.count()*d.privateParking()).sum();
            double surface=f.basementLevels()>0?0:f.recipe().sharedParking();
            check("floor and surface parking fit the actual plot "+f.code(),f.footprint()+(privateBays+surface)*HousingForms.BAY_SQFT<=f.plot());
            check("private dwelling area fits the residential gross area "+f.code(),f.recipe().mix().stream().mapToDouble(d->d.count()*d.area()).sum()<=f.grossArea());
            if(f.basementLevels()>0)check("basement parking fits its own actual geometry "+f.code(),f.basementFootprint()<=f.plot()
                    &&f.basementLevels()*f.basementFootprint()>=f.recipe().sharedParking()*HousingForms.BAY_SQFT);
        }
        double receipt=0;
        for(var c:market.classes()){
            check("finite nonnegative class ask "+c.recipe+"/"+c.dwelling,Double.isFinite(c.ask)&&c.ask>=0);
            check("occupancy is bounded by actual RE titles "+c.recipe+"/"+c.dwelling,c.occupied<=c.homes+FamilyModel.packingRounding(c.homes));
            if(c.tradeQuantity==0)near("vacancy earns no rent "+c.recipe+"/"+c.dwelling,c.tradeValue,0);
            receipt+=market.leases().stream().filter(l->l.home().equals(new HousingInventory.Key(c.recipe,c.dwelling))).mapToDouble(HousingChoice.Lease::bill).sum();
        }
        near("rental proceeds are exactly actual occupied leases",market.receipts(),receipt);
        near("sector bills only actual occupied leases",g.getSectors().realEstate().getRentIncome(),receipt);
        near("the compatibility occupancy reader counts actual typed lets",g.getSectors().realEstate().getOccupiedHomes(),market.occupied());
        var state=JSON.toJson(market.forSave());
        // A second call in the same month is not a second quote/clearing or a trade.
        market.clear(g,List.of());check("one clearing per month",state.equals(JSON.toJson(market.forSave())));
    }
    static HousingMarket.State priceInputs(double studioCarry){
        HousingMarket.State s=new HousingMarket.State();s.month=1;
        HousingMarket.ClassState studio=new HousingMarket.ClassState(),family=new HousingMarket.ClassState();
        studio.recipe=900;studio.dwelling="studio";studio.capacity=2;studio.available=true;
        studio.ask=20;studio.capital=1200;studio.carry=studioCarry;studio.homes=10;studio.lastTrade=20;studio.stale=3;
        family.recipe=901;family.dwelling="family";family.capacity=4;family.available=true;
        family.ask=10;family.capital=1000;family.carry=5;family.homes=family.occupied=family.demand=10;
        s.classes=List.of(studio,family);s.leases=List.of(new HousingChoice.Lease("family",new HousingInventory.Key(901,"family"),10,100));s.suitable=10;
        s.demand=List.of(new HousingChoice.Demand("family",10,4,2,false,0,false,20));
        s.pipeline=List.of(new HousingChoice.Offer(new HousingInventory.Key(901,"family"),2,4,2,75,1,0,false,10,2));
        return s;
    }
    static void classPrices(){
        out.println("--- class-local debt carry, actual trades and zero-demand glut ---");
        HousingMarket normal=new HousingMarket(),debt=new HousingMarket();
        normal.restore(priceInputs(0));debt.restore(priceInputs(1000));normal.reprice(0);debt.reprice(0);
        var studio=debt.classes().get(0);var family=debt.classes().get(1);
        near("zero demand in standing stock has zero target despite debt",studio.target,0);
        near("zero-demand glut walks down at the existing lease speed",studio.ask,20*(1-1.0/ham.citybuildersim.sectors.RealEstate.LEASE_MONTHS));
        near("a glut class's debt does not change another class's ask",family.ask,normal.classes().get(1).ask);
        near("the tight class covers only its own carry",family.target,5);
        var roundedInputs=priceInputs(0);double almostTen=Math.nextDown(10.0);
        roundedInputs.classes.get(1).occupied=roundedInputs.classes.get(1).demand=almostTen;
        roundedInputs.leases=List.of(new HousingChoice.Lease("family",new HousingInventory.Key(901,"family"),almostTen,almostTen*10));
        roundedInputs.suitable=almostTen;
        HousingMarket rounded=new HousingMarket();rounded.restore(roundedInputs);rounded.reprice(0);
        near("one ULP of fully placed stock is not an economic glut",rounded.classes().get(1).target,family.target);
        var glutInputs=priceInputs(0);glutInputs.classes.get(1).occupied=glutInputs.classes.get(1).demand=9.9;
        glutInputs.leases=List.of(new HousingChoice.Lease("family",new HousingInventory.Key(901,"family"),9.9,99));
        glutInputs.suitable=9.9;
        glutInputs.demand=List.of(new HousingChoice.Demand("family",9.9,4,2,false,0,false,20));
        HousingMarket glut=new HousingMarket();glut.restore(glutInputs);glut.reprice(0);
        check("a genuine fractional vacancy still waives the carry lift",glut.classes().get(1).target<5);
        var blueprint=priceInputs(0);var opening=blueprint.classes.get(1);
        opening.homes=opening.occupied=opening.carry=0;opening.demand=1e-13;
        blueprint.leases=List.of();blueprint.suitable=0;blueprint.noSuitable=blueprint.unplaced=1e-13;
        blueprint.demand=List.of(new HousingChoice.Demand("family",1e-13,4,2,false,0,false,20));
        HousingMarket unbuilt=new HousingMarket();unbuilt.restore(blueprint);unbuilt.reprice(0);
        near("an unbuilt class has an opening quote, without a fictitious scarce market",unbuilt.classes().get(1).target,
                opening.capital*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12);
        near("repricing itself records no actual rent trade",family.tradeQuantity,0);
        near("repricing preserves last observed class trade",studio.lastTrade,20);
        near("actual occupied proceeds remain the accepted lease bill",debt.receipts(),100);
        debt.sold();studio=debt.classes().get(0);family=debt.classes().get(1);
        near("vacant class records no rent receipt",studio.tradeValue,0);
        near("vacant class's last trade is retained",studio.lastTrade,20);
        check("vacant class trade becomes stale",studio.stale==4);
        near("actual class quantity counts occupied homes only",family.tradeQuantity,10);
        near("actual class price is actual value over quantity",family.lastTrade,10);
        String before=JSON.toJson(debt.forSave());var corrupt=debt.forSave();corrupt.classes.get(0).ask=Double.NaN;
        boolean refused=false;try{debt.restore(corrupt);}catch(IllegalArgumentException e){refused=true;}
        check("corrupt class prices are refused atomically",refused&&before.equals(JSON.toJson(debt.forSave())));
        corrupt=debt.forSave();corrupt.classes.get(1).demand=0;
        refused=false;try{debt.restore(corrupt);}catch(IllegalArgumentException e){refused=true;}
        check("saved demand below actual occupied quantity is refused atomically",refused&&before.equals(JSON.toJson(debt.forSave())));
        debt.redenominate(.01);near("a monetary reform scales actual receipts",debt.receipts(),1);
        near("a monetary reform scales saved household budgets",debt.forSave().demand.getFirst().budget(),.2);
        near("a monetary reform scales committed pipeline asks",debt.forSave().pipeline.getFirst().rent(),.1);
        near("a monetary reform scales committed pipeline recurring costs",debt.forSave().pipeline.getFirst().recurring(),.02);
    }
    static void foundingBudgets(){
        out.println("--- a founding worker's current wage funds the first actual home ---");
        Game g=new Game(GameFiles.scratch("res3-founding-budgets"),LongPlaytest.played(null));g.run();
        LongPlaytest.villageBuild(g,"House",40);LongPlaytest.villageBuild(g,"Convenience Store",3);LongPlaytest.villageBuild(g,"Mixed Farm",2);
        for(int i=0;i<3;i++){
            g.simulateMonths(1);audits(g);
            check("founding month "+g.getMonth()+" does not use an empty prior wage row for new workers",g.getFamilies().getStillUnplaced()<=1);
            near("founding rent receipts are actual accepted bills",g.getSectors().realEstate().getRentIncome(),g.getSectors().realEstate().housingMarket().receipts());
        }
        check("the founding premise really admits people and lets occupied homes",g.getCohorts().total()>0&&g.getSectors().realEstate().housingMarket().occupied()>0);
    }
    static void fractionalPressure(){
        out.println("--- fully placed fractional demand creates no phantom blueprint scarcity ---");
        Game g=new Game(GameFiles.scratch("res3-fractional-pressure"));g.run();
        var bm=g.getBuildingManager();bm.addStack(bm.getTemplate(0),100,true);
        List<HousingChoice.Demand> cells=new ArrayList<>();
        for(int i=1;i<=17;i++)cells.add(new HousingChoice.Demand("fraction-"+i,i/10.0,1,0,false,0,false,100));
        var market=g.getSectors().realEstate().housingMarket();var result=market.clear(g,cells);
        near("fractional sources all find a real affordable door",result.unplaced(),0);
        check("no unbuilt class receives floating-point leftover demand",market.classes().stream().filter(c->c.homes==0).allMatch(c->c.demand==0));
        market.reprice(0);
        for(var c:market.classes())if(c.available&&c.homes==0)
            near("an unbuilt class keeps its opening comparator "+c.key(),c.ask,c.capital*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12);
    }
    static void sharingOrder(){
        out.println("--- affordable singles before emergency sharing; one quote and conserved existing cells ---");
        for(boolean ordinary:new boolean[]{true,false}){
            Game g=new Game(GameFiles.scratch(ordinary?"res3-normal-before-sharing":"res3-funded-sharing"));g.run();
            double[] people=new double[AgeBand.values().length+3];people[AgeBand.ADULT.ordinal()]=100;
            g.getCohorts().restore(Arrays.stream(AgeBand.values()).map(Enum::name).toArray(String[]::new),people);
            g.getFamilies().rememberHouseholds(false);
            double[] jobs=new double[PayTier.values().length];jobs[PayTier.UNSKILLED.ordinal()]=100;
            g.getFamilies().rebuild(g.getCohorts(),jobs);
            WorkingHousehold single=null;
            for(Household c:g.getHouseholdBalance().cells())if(c instanceof WorkingHousehold w&&w.shape()==FamilyStructure.SINGLE_ADULT
                    &&g.getFamilies().get(w.shape(),w.tier(),w.stage())>0){single=w;break;}
            check("fixture has an actual existing single-adult cell",single!=null);if(single==null)continue;
            var bm=g.getBuildingManager();var house=bm.getTemplate(0);bm.addStack(house,100,true);
            if(ordinary)bm.addStack(bm.getTemplate(1),2,true);
            double quantity=g.getFamilies().get(single.shape(),single.tier(),single.stage());
            double structure=g.quoteBuild(house,1).total,full=g.doorPriceOf(house,1);
            double ask=HousingForms.of(0).homeCapital(house.getHousingRecipe().mix().getFirst().id(),structure,full-structure)
                    *ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12;
            double budget=ordinary?ask*2:ask/4;
            var d=new HousingChoice.Demand(single.key(),quantity,1,0,false,single.tier().ordinal(),single.stage()==AdultStage.YOUNG,budget);
            double beforePeople=0;for(FamilyStructure shape:FamilyStructure.values())beforePeople+=g.getFamilies().totalOf(shape)*shape.size();
            var market=g.getSectors().realEstate().housingMarket();var result=market.clear(g,List.of(d));
            double afterPeople=0;for(FamilyStructure shape:FamilyStructure.values())afterPeople+=g.getFamilies().totalOf(shape)*shape.size();
            near("sharing preserves the actual family members",afterPeople,beforePeople);
            if(ordinary){near("all suitable affordable singles placed first",result.suitable(),quantity);near("affordable singles are not forced to share",market.sharingGroups(),0);}
            else {
                check("five budgets fund a real remaining home",market.sharingGroups()>0);
                near("only actually placed sharing groups change the census",g.getFamilies().get(single.shape(),single.tier(),single.stage()),quantity-5*market.sharingGroups());
                near("the actual sharing rent is one bill per occupied door",market.receipts(),market.occupied()*ask);
                check("emergency sharing retains explicit affordability or hosted-guest reasons",result.unaffordable()+market.hosted()>0);
                var copy=new HousingMarket();copy.restore(JSON.fromJson(JSON.toJson(market.forSave()),HousingMarket.State.class));
                check("saved sharing transfers and budgets round-trip exactly",JSON.toJson(copy.forSave()).equals(JSON.toJson(market.forSave())));
            }
        }
    }
    static void doubledDoors(){
        out.println("--- actual doubled doors and divided rent, including a vacant affordable pair ---");
        Game g=new Game(GameFiles.scratch("res3-doubled-doors"));g.run();
        double[] people=new double[AgeBand.values().length+3];people[AgeBand.ADULT.ordinal()]=100;
        g.getCohorts().restore(PopulationCohorts.saveBands(),people);g.getFamilies().rememberHouseholds(false);
        double[] jobs=new double[PayTier.values().length];jobs[PayTier.UNSKILLED.ordinal()]=100;g.getFamilies().rebuild(g.getCohorts(),jobs);
        var c=g.getHouseholdBalance().cells().stream().filter(h->h instanceof WorkingHousehold w&&w.shape()==FamilyStructure.COUPLE
                &&g.getFamilies().get(w.shape(),w.tier(),w.stage())>0).map(h->(WorkingHousehold)h).findFirst().orElseThrow();
        var bm=g.getBuildingManager();var t=bm.getTemplate(0);bm.addStack(t,100,true);
        double structure=g.quoteBuild(t,1).total,full=g.doorPriceOf(t,1);
        double ask=HousingForms.of(0).homeCapital(t.getHousingRecipe().mix().getFirst().id(),structure,full-structure)*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12;
        double count=g.getFamilies().get(c.shape(),c.tier(),c.stage());
        var d=new HousingChoice.Demand(c.key(),count,c.size(),c.shape().dependants(),false,c.tier().ordinal(),c.stage()==AdultStage.YOUNG,ask*.75);
        var market=g.getSectors().realEstate().housingMarket();var result=market.clear(g,List.of(d));
        near("full rent is unaffordable, two actual households fund one door",result.leases().stream().mapToDouble(HousingChoice.Lease::households).sum(),count);
        near("doubled households occupy half as many real doors",market.occupied(),count/2);
        near("each doubled door earns one rent, not two",market.receipts(),count*ask/2);
        near("actual doubled placements leave no fictional unplaced residual",result.unplaced(),0);
        g.getFamilies().acceptTypedHousing(market.demands(),result,g.getHouseholdBalance().cells(),bm.getHousingInventory());
        near("the existing crowding reader sees actual guest equivalents",g.getFamilies().getDoubledUpFamilies(),count/2);
        near("the existing unplaced reader sees the actual allocation",g.getFamilies().getStillUnplaced(),0);
        long spare=g.getSectors().realEstate().limitSpareRetirement(t,100,0);
        check("a multi-structure retirement cannot cut into typed occupied doors",spare<=100-market.occupied()&&spare>0);
        var copy=new HousingMarket();copy.restore(JSON.fromJson(JSON.toJson(market.forSave()),HousingMarket.State.class));
        check("doubled door and household quantities round-trip",JSON.toJson(copy.forSave()).equals(JSON.toJson(market.forSave())));
    }
    static void studentFacility(){
        HouseholdBalance balances=new HouseholdBalance();FamilyModel families=new FamilyModel();HouseholdAccounts accounts=new HouseholdAccounts();
        balances.setOutsideCensus(c->c instanceof StudentHousehold?1:0);
        var student=balances.cells().stream().filter(c->c instanceof StudentHousehold).findFirst().orElseThrow();
        student.studentLoansOpen=false;
        double shut=balances.strikeHousingBudgets(families,accounts,1,c->0).get(student.key()).budget();
        student.studentLoansOpen=true;
        double open=balances.strikeHousingBudgets(families,accounts,1,c->0).get(student.key()).budget();
        check("student housing counts only the existing open treasury facility",open>shut&&open-shut==student.planningRoom());
        balances.setMoneyUnitSource(()->100);
        near("the student's finite planning allowance follows a currency reform",balances.strikeHousingBudgets(families,accounts,1,c->0).get(student.key()).budget(),open/100);
        near("quoting a funded student budget itself borrows nothing",student.studentDebt(),0);
    }
    static void fundedHost(){
        Game g=new Game(GameFiles.scratch("res3-funded-host"));g.run();var bm=g.getBuildingManager();var t=bm.getTemplate(0);bm.addStack(t,1,true);
        double structure=g.quoteBuild(t,1).total,full=g.doorPriceOf(t,1);
        double ask=HousingForms.of(0).homeCapital(t.getHousingRecipe().mix().getFirst().id(),structure,full-structure)*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12;
        String host=g.getHouseholdBalance().cell(FamilyStructure.COUPLE,PayTier.UNSKILLED).key();
        String guest=g.getHouseholdBalance().cell(FamilyStructure.SENIOR_ALONE,PayTier.UNSKILLED).key();
        var market=g.getSectors().realEstate().housingMarket();var result=market.clear(g,List.of(
                new HousingChoice.Demand(host,1,2,0,false,0,false,2*ask),new HousingChoice.Demand(guest,1,1,0,true,0,false,0)));
        near("a zero-resource emergency guest has an actual funded host",result.unplaced(),0);
        near("a hosted guest creates no extra occupied door",market.occupied(),1);
        near("a hosted guest creates no extra landlord proceeds",market.receipts(),ask);
        near("the host retains its actual funded bill",market.rentFor(host,1),ask);
        near("the guest owes no invented rent or credit",market.rentFor(guest,1),0);
        near("emergency hosting has an explicit reason count",market.hosted(),1);
        String beforeRoom=JSON.toJson(market.forSave());
        near("a doubled home offers migration no fictional extra guest slot",market.roomLeft(g,new FamilyModel.Room(0,0,0,2,1,0,2)).households(),0);
        check("reading actual migration room does not quote or clear",beforeRoom.equals(JSON.toJson(market.forSave())));
        double[] people=new double[AgeBand.values().length+3];people[AgeBand.ADULT.ordinal()]=100;
        g.getCohorts().restore(PopulationCohorts.saveBands(),people);g.getFamilies().rememberHouseholds(false);
        double[] jobs=new double[PayTier.values().length];jobs[PayTier.UNSKILLED.ordinal()]=100;g.getFamilies().rebuild(g.getCohorts(),jobs);
        var single=g.getHouseholdBalance().cells().stream().filter(c->c instanceof WorkingHousehold w&&w.shape()==FamilyStructure.SINGLE_ADULT
                &&g.getFamilies().get(w.shape(),w.tier(),w.stage())>=5).map(c->(WorkingHousehold)c).findFirst().orElseThrow();
        market.reset();result=market.clear(g,List.of(new HousingChoice.Demand(host,1,2,0,false,0,false,2*ask),
                new HousingChoice.Demand(single.key(),5,1,0,false,0,single.stage()==AdultStage.YOUNG,0)));
        check("an emergency sharing group can join a funded host without a vacant door",market.sharingGroups()>0);
        near("hosting a sharing group still earns just the one funded rent",market.receipts(),ask);
        check("a sharing group consumes the host slot before further guests",result.leases().stream().mapToDouble(HousingChoice.Lease::households).sum()
                <=2*market.occupied()+FamilyModel.packingRounding(2*market.occupied()));
        beforeRoom=JSON.toJson(market.forSave());bm.addStack(t,1,true);
        near("migration room sees a real completion and first reserves current unplaced demand",
                market.roomLeft(g,new FamilyModel.Room(0,0,0,2,1,0,2)).households(),2-result.unplaced());
        check("a completion projection leaves the one monthly quote untouched",beforeRoom.equals(JSON.toJson(market.forSave())));
        people[AgeBand.ADULT.ordinal()]=200;g.getCohorts().restore(PopulationCohorts.saveBands(),people);
        double[] oldFamilies=g.getFamilies().toSaveArray();market.prepareMigration(g);
        near("current domestic household formation reserves the last rooms before arrivals",
                market.roomLeft(g,new FamilyModel.Room(0,0,0,2,1,0,2)).households(),0);
        check("the formation preview leaves actual families and the monthly market unchanged",
                Arrays.equals(oldFamilies,g.getFamilies().toSaveArray())&&beforeRoom.equals(JSON.toJson(market.forSave())));
        bm.addStack(t,100,true);
        var newcomers=market.roomLeft(g,new FamilyModel.Room(0,0,0,2,1,0,2));
        check("real additional doors still admit arrivals after the formation reserve",newcomers.households()>0);
        near("arrivals who start work next press cannot use the working family's density",newcomers.people(),newcomers.households());
    }
    static void writeReport(StringBuilder report,Game g,String label,Map<Integer,Long> starts,Map<Integer,Long> completions,int months,double ms){
        var re=g.getSectors().realEstate();var market=re.housingMarket();var bm=g.getBuildingManager();var em=g.getEconomyManager();
        report.append("\n## ").append(label).append("\n\nIntegrated month: ").append(String.format(Locale.ROOT,"%.3f",ms)).append(" ms. H/T/A/M/L = ")
                .append(market.forSave().demand.size()).append('/').append(market.classes().size()).append('/').append(market.leases().size())
                .append("/0/").append(bm.getHousingInventory().forSave().stream().filter(h->h.listedVacant+h.listedOccupied>0).count())
                .append(". M counts new typed loan cohorts (ownership follows in HO1); L counts live listing cohorts. Physical cohorts: ")
                .append(bm.getHousingInventory().stock().size()).append(".\n\n");
        report.append("| Form | ID | Since / known | Floors / gross m² / site ft² | Materials / points | Cash / project quote | Posts / full payroll / power / water | Wage scale | Funded starts | Completed / months |\n|---|---:|---|---|---|---|---|---|---:|---|\n");
        for(var f:HousingForms.ALL){var t=bm.getTemplate(f.id());long completed=completions.getOrDefault(f.id(),0L);
            double payroll=0;for(JobType job:JobType.values())payroll+=t.getJobs(job)*em.getWageRates()[job.ordinal()];
            report.append(String.format(Locale.ROOT,"| %s | %d | %d / %s | %d / %.1f / %.0f | %d / %d | %.6f / %.6f | %d / %.6f / %d / %.3f | %.6f | %d | %d / %d |%n",f.code(),f.id(),t.getSince(),!g.foundedUnfitted()||t.knownAt(g.getMonth(),g.getResearch()),f.floors(),f.grossArea(),f.plot(),t.getConstructionMaterials(),t.getConstructionPoints(),t.getCashCost(),g.doorPriceOf(t,1),f.staff(),payroll,t.getElectricityConsumption(),t.getWaterConsumption(),g.getRecipes().datedScale(t),starts.getOrDefault(f.id(),0L),completed,months));}
        double gross=2*em.getWageRates()[JobType.NO_DIPLOMA.ordinal()];
        double count=g.getHouseholds().getRowHouseholds(PayTier.UNSKILLED.ordinal());
        double rowNet=count>0?g.getHouseholds().getRowDisposable(PayTier.UNSKILLED.ordinal())/count:0;
        double net=gross*Math.max(0,1-em.getTaxPolicy().payslipShare(WageBand.of(JobType.NO_DIPLOMA)));
        report.append("\nCompletions are observed BuildLog events collected every month, including prior funded work that completes in the window. They are structure counts, not net changes in standing stock. Money is in current model thousands. Both burden columns use the same two-earner unskilled household: gross current wages and take-home after the actual payslip levies. Land is charged once by the existing builder quote. Optional parking has capital but no assumed lease receipts.\n")
                .append(String.format(Locale.ROOT,"Reference gross / take-home: %.6f / %.6f. Observed unskilled row mean take-home, including its actual mix of singles, families and sharing cells: %.6f.%n%n",gross,net,rowNet))
                .append("| Form / dwelling | Area / bedrooms / places / quality | Private / included bays | Ask | Gross burden | Take-home burden | RE doors / vacancy | Last trade qty / value / price / stale |\n|---|---|---|---:|---:|---:|---|---|\n");
        for(var f:HousingForms.ALL)for(var d:f.recipe().mix()){
            var c=market.classes().stream().filter(s->s.recipe==f.id()&&s.dwelling.equals(d.id())).findFirst().orElseThrow();
            report.append(String.format(Locale.ROOT,"| %s / %s | %.0f / %d / %d / %.2f | %.2f / %.2f | %.6f | %.4f | %s | %.3f / %.3f | %.3f / %.6f / %.6f / %d |%n",f.code(),d.id(),d.area(),d.bedrooms(),d.capacity(),d.quality(),d.privateParking(),d.includedParking(),c.ask,gross>0?c.ask/gross:0,net>0?String.format(Locale.ROOT,"%.4f",c.ask/net):"no take-home",c.homes,c.homes-c.occupied,c.tradeQuantity,c.tradeValue,c.lastTrade,c.stale));}
        report.append("\n| Form | Floorplate ft² | Shared bays | Basement levels / footprint ft² |\n|---|---:|---:|---|\n");
        for(var f:HousingForms.ALL)report.append(String.format(Locale.ROOT,"| %s | %.1f | %.0f | %d / %.0f |%n",f.code(),f.footprint(),f.recipe().sharedParking(),f.basementLevels(),f.basementFootprint()));
        report.append("\nReference home capital is section 12's present-day benchmark dollars, before the game's live price, tax and construction quote. The monthly comparator is capital × 0.058 / 12: the reference is in benchmark dollars, and actual allocated capital and its comparator are current model thousands.\n\n| Form / class | Reference home capital / monthly comparator | Actual allocated capital / monthly comparator |\n|---|---:|---:|\n");
        for(var f:HousingForms.ALL)for(var d:f.recipe().mix()){
            var c=market.classes().stream().filter(s->s.recipe==f.id()&&s.dwelling.equals(d.id())).findFirst().orElseThrow();
            report.append(String.format(Locale.ROOT,"| %s / %s | %.2f / %.2f | %.6f / %.6f |%n",f.code(),d.id(),d.costWeight(),d.costWeight()*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12,c.capital,c.capital*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12));
        }
        report.append(String.format(Locale.ROOT,"%nActual accepted occupied rent this month: %.6f; last settled class trade value total: %.6f; vacant RE doors: %.3f. Current staffed RE payroll: %.6f (the per-form table budgets every post at full staffing).%n",market.receipts(),market.classes().stream().mapToDouble(c->c.tradeValue).sum(),market.classes().stream().mapToDouble(c->c.homes-c.occupied).sum(),re.getPayroll()));
        var outcome=market.result();
        report.append(String.format(Locale.ROOT,"%nMonthly allocation: %.3f retained, %.3f normal movers, %.3f emergency placements (%.3f hosted at no additional rent), %.3f unplaced for affordability, %.3f for compatible supply. Occupied household units / door-equivalents: %.3f / %.3f.%n",outcome.kept(),outcome.suitable(),outcome.crowded(),market.hosted(),outcome.unaffordable(),outcome.noSuitable(),market.leases().stream().mapToDouble(HousingChoice.Lease::households).sum(),market.occupied()));
    }
    static void city(StringBuilder report,boolean historical)throws Exception{
        String label=historical?"Historical founding (1900)":"Present-day equipment founding";
        out.println("--- "+label+" ---");
        GameFiles files=GameFiles.scratch(historical?"res3-historical":"res3-present");
        Game g=new Game(files,Founding.defaults().withUnfitted(historical));g.run();
        var bm=g.getBuildingManager();var re=g.getSectors().realEstate();
        Map<Integer,Long> starts=new HashMap<>(),completed=new HashMap<>();
        // Explicit fixture endowment before the audit opens, followed by the actual paid build path.
        re.setCash(SECTOR_ENDOWMENT);
        g.getGovernmentInvestor().spend(-4_000_000*PriceLevel.FACTOR);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt()+2_000_000_000L);
        // Supporting infrastructure is a fixture input; all residential sites below use paid work.
        g.buildStack(bm.getTemplateByName("Construction Depot"),100,true);
        g.buildStack(bm.getTemplateByName("Coal Power Plant"),20,true);
        g.buildStack(bm.getTemplateByName("Water Treatment Plant"),20,true);
        g.buildStack(bm.getTemplateByName(ConstructionControl.PAVE_FROM),200,true);
        g.getCohorts().migrate(100_000);
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
        for(var f:HousingForms.ALL){var t=bm.getTemplate(f.id());var s=bm.getStack(t);
            long before=s==null?0:s.getUnderConstruction();LongPlaytest.build(g,t.getName(),1);
            s=bm.getStack(t);starts.put(f.id(),(s==null?0:s.getUnderConstruction())-before);
            check("catalogue order follows actual availability "+f.code(),starts.get(f.id())==(bm.isTemplateKnown(t)?1:0));
            if(historical&&f.since()>1900)check("modern form unavailable in 1900 "+f.code(),starts.get(f.id())==0&&!t.knownAt(g.getMonth(),g.getResearch()));
        }
        double ms=0;
        for(int i=0;i<24;i++){long at=System.nanoTime();g.toggleNextMonth();ms=(System.nanoTime()-at)/1e6;audits(g);
            for(var e:g.getBuildLog().all())if(e.month==g.getMonth())for(var f:HousingForms.ALL)if(e.building.equals(f.name()))
                completed.merge(f.id(),e.quantity,Long::sum);
        }
        classes(g);
        check("fixture caused funded residential starts",starts.values().stream().mapToLong(Long::longValue).sum()>0);
        check("fixture has actual household demand",g.getFamilies().householdsSeekingDoors()>0);
        check("fixture has actual occupied rental homes",re.housingMarket().occupied()>0);
        for(var f:HousingForms.ALL)if(starts.getOrDefault(f.id(),0L)>0)
            check("funded form completes through actual builder work "+f.code(),completed.getOrDefault(f.id(),0L)>0);
        writeReport(report,g,label,starts,completed,24,ms);
        String saved=JSON.toJson(re.housingMarket().forSave());
        g.saveGame(10);Game back=new Game(files);back.loadGame(10);
        check("native reload preserves asks, assignments, actual trades and staleness",saved.equals(JSON.toJson(back.getSectors().realEstate().housingMarket().forSave())));
        g.toggleNextMonth();back.toggleNextMonth();audits(g);audits(back);
        check("next native month preserves the class market",JSON.toJson(re.housingMarket().forSave()).equals(JSON.toJson(back.getSectors().realEstate().housingMarket().forSave())));
        int cells=g.getHouseholdBalance().cells().size();g.getCohorts().migrate(100_000);
        long at=System.nanoTime();g.toggleNextMonth();double largeMs=(System.nanoTime()-at)/1e6;audits(g);
        check("larger population adds no permanent household cells",g.getHouseholdBalance().cells().size()==cells);
        check("larger city's class count stays bounded",re.housingMarket().classes().size()==HousingRecipe.MAX_CLASSES);
        check("larger city's assignments stay bounded by H times T",re.housingMarket().leases().size()<=
                re.housingMarket().forSave().demand.size()*HousingRecipe.MAX_CLASSES);
        report.append(String.format(Locale.ROOT,"%nLarger scratch-city population: %.0f; integrated month %.3f ms; H/T/A/M/L = %d/%d/%d/0/%d. Permanent household cells: %d.%n",
                g.getCohorts().total(),largeMs,re.housingMarket().forSave().demand.size(),re.housingMarket().classes().size(),re.housingMarket().leases().size(),
                bm.getHousingInventory().forSave().stream().filter(h->h.listedVacant+h.listedOccupied>0).count(),cells));
    }
    public static void main(String[] args)throws Exception{
        out=System.out;PrintStream silent=new PrintStream(OutputStream.nullOutputStream());
        StringBuilder report=new StringBuilder("# RES3 actual quote, budget and availability report\n\nGenerated by HousingMarketCheck through scratch cities, model inputs and the paid build path. Starting geometry, capital partitions and reference anchors are RES1 section 12; observed completions and receipts below are actual model outcomes.\n\nThe two catalogue cities begin with two billion present-day dollars of RE equity, four billion of government equity, two billion square feet of owned land, 100 construction depots, 20 coal plants, 20 water plants, 200 gravel roads and 100,000 migrants. These are supporting fixture inputs before the audit opens; every residential order uses the paid builder, materials, work queue, land, funding and settlement path. Ordinary city construction may also finish during the observation window. Historical founding gates use current research/known-template state; modern equipment is not inferred. Timings are individual integrated months on this laptop, including all other city systems, not isolated solver timings.\n");
        try{System.setOut(silent);classPrices();foundingBudgets();fractionalPressure();sharingOrder();doubledDoors();studentFacility();fundedHost();smallProfile(report);city(report,false);city(report,true);}finally{System.setOut(out);silent.close();}
        Path target=Path.of("target/RES3-catalogue-report.md");Files.writeString(target,report);
        out.println("Report: "+target);out.println(fails==0?"HousingMarketCheck: ALL PASSED":fails+" FAILED");if(fails>0)System.exit(1);
    }
    static void smallProfile(StringBuilder report){
        out.println("--- small scratch-city integrated profile ---");
        Game g=LongPlaytest.probeCity(GameFiles.scratch("res3-small-profile"),null);
        long at=System.nanoTime();g.toggleNextMonth();double ms=(System.nanoTime()-at)/1e6;audits(g);
        var market=g.getSectors().realEstate().housingMarket();
        report.append(String.format(Locale.ROOT,"%nSmall scratch city: %.0f residents; integrated month %.3f ms; H/T/A/M/L = %d/%d/%d/0/%d.%n",
                g.getCohorts().total(),ms,market.forSave().demand.size(),market.classes().size(),market.leases().size(),
                g.getBuildingManager().getHousingInventory().forSave().stream().filter(h->h.listedVacant+h.listedOccupied>0).count()));
    }
}
