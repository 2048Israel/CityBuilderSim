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
    /** Five old shared households at twenty, joined by five groups of five singles, preserve their actual resources. */
    static HousingMarket.State mixedSharingPrices(double singleBudget){
        var state=priceInputs(0);var home=state.classes.get(1);
        home.capacity=6;home.ask=15;home.homes=10;home.occupied=5;home.demand=10;
        state.classes=List.of(home);state.suitable=0;state.crowded=10;state.pipeline=List.of();
        state.leases=List.of(new HousingChoice.Lease("family",new HousingInventory.Key(901,"family"),5,75,10));
        double pooled=(5*20+25*singleBudget)/10;
        state.demand=List.of(new HousingChoice.Demand("source",0,1,0,false,0,true,singleBudget),
                new HousingChoice.Demand("family",10,5,0,false,0,true,pooled));
        state.sharing=List.of(new HousingMarket.Sharing("source","family",5,25,5,singleBudget,20));
        return state;
    }
    static void mergedBudgetPressure(){
        out.println("--- rent pressure uses the current pooled budget after emergency sharing ---");
        var key=new HousingInventory.Key(901,"family");
        var supply=List.of(new HousingChoice.Offer(key,10,6,3,120,1,0,false,15,0));
        HousingMarket poor=new HousingMarket();poor.restore(mixedSharingPrices(1));
        near("the merged cell has exactly the original 125 of resources",poor.demands().stream().mapToDouble(d->d.count()*d.budget()).sum(),125);
        poor.strikePriceDemand(supply);
        near("the 12.5 budget cannot promise another whole door at 15",poor.classes().getFirst().demand,5);
        near("price pressure preserves the actual occupied rent receipt",poor.receipts(),75);
        HousingMarket funded=new HousingMarket();funded.restore(mixedSharingPrices(4));funded.strikePriceDemand(supply);
        near("the 20 budget still prices in every funded doubled-up household",funded.classes().getFirst().demand,10);
        near("striking price pressure does not spend or recreate the merged resources",funded.demands().stream().mapToDouble(d->d.count()*d.budget()).sum(),200);
    }
    static void plannerInputs(){
        out.println("--- actual operating quotes, feasible plots and the full typed backlog ---");
        Game g=new Game(GameFiles.scratch("res3-planner-inputs"));g.run();
        var bm=g.getBuildingManager();var re=g.getSectors().realEstate();var market=re.housingMarket();
        market.clear(g,List.of(new HousingChoice.Demand("planner-adults",80,2,0,true,0,false,1000)));
        for(var form:HousingForms.ALL){var t=bm.getTemplate(form.id());if(!bm.isTemplateKnown(t))continue;
            double rent=market.classes().stream().filter(c->c.recipe==t.getId()).mapToDouble(c->c.ask*t.getHousingRecipe().mix().stream().filter(d->d.id().equals(c.dwelling)).findFirst().orElseThrow().count()).sum();
            double capital=market.classes().stream().filter(c->c.recipe==t.getId()).mapToDouble(c->c.capital*t.getHousingRecipe().mix().stream().filter(d->d.id().equals(c.dwelling)).findFirst().orElseThrow().count()).sum()*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12;
            check("unbuilt "+form.code()+" quotes its real operation and capital return",rent-g.getEconomyManager().housingCarry(t)>=capital-MoneyAudit.tolerance(capital));
        }
        near("unbuilt supply quotes earn no actual rent",market.receipts(),0);
        var terrace=HousingForms.ALL.stream().filter(f->f.code().equals("C05")).findFirst().orElseThrow();
        var key=new HousingInventory.Key(terrace.id(),terrace.recipe().mix().getFirst().id());
        market.setAsk(key,market.price(key)*3);
        var plans=new BusinessInvestment(bm,g.getEconomyManager());plans.setLandAvailable(6000,0);
        var narrow=re.plan(plans,g);
        check("a profitable compact alternative uses the available plot",narrow.template!=null&&narrow.template.getLandSqFt()<=6000);
        plans.setLandAvailable(1_000_000_000,0);var backlog=re.plan(plans,g);
        check("the fixture selects the six-door terrace",backlog.template!=null&&backlog.template.getId()==terrace.id());
        check("the order covers the actual typed backlog rather than one project's income",backlog.template!=null&&backlog.quantity*backlog.template.getDwellings()>=80);
        int needed=market.forecast(g).quantityNeeded(bm.getTemplate(terrace.id()));
        check("the typed backlog needs the smallest whole recipe covering eighty households",needed==(int)Math.ceil(80.0/bm.getTemplate(terrace.id()).getDwellings()));
        check("the smaller head-based gap does not inflate the typed order",backlog.template!=null&&backlog.quantity==needed);
        market.reset();market.clear(g,List.of(new HousingChoice.Demand("planner-family",80,4,2,false,0,false,1000)));
        market.setAsk(key,market.price(key)*3);plans.setLandAvailable(6000,0);
        check("a child family is not offered an unsuitable compact fallback",re.plan(plans,g).template==null);
        market.reset();market.clear(g,List.of(new HousingChoice.Demand("planner-poor",80,2,0,true,0,false,0)));
        plans.setLandAvailable(1_000_000_000,0);
        check("an unfunded backlog does not promise a profitable project",re.plan(plans,g).template==null);
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
            near("an unbuilt class keeps its opening cost quote "+c.key(),c.ask,c.capital*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12+c.carry);
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
            double[] cardsBefore=null;
            if(!ordinary){
                g.getHouseholdBalance().censusOnly(g.getFamilies());single.debt=10;single.revolving=.1;single.lateDebt=2;single.lateShare=.02;single.shutOut=.05;
                cardsBefore=cardPositions(g.getHouseholdBalance());
            }
            var market=g.getSectors().realEstate().housingMarket();var result=market.clear(g,List.of(d));
            double afterPeople=0;for(FamilyStructure shape:FamilyStructure.values())afterPeople+=g.getFamilies().totalOf(shape)*shape.size();
            near("sharing preserves the actual family members",afterPeople,beforePeople);
            if(ordinary){near("all suitable affordable singles placed first",result.suitable(),quantity);near("affordable singles are not forced to share",market.sharingGroups(),0);}
            else {
                g.getHouseholdBalance().censusOnly(g.getFamilies());var cardsAfter=cardPositions(g.getHouseholdBalance());
                String[] positions={"card balance","holder position","late balance","late holder position","excluded position"};
                for(int i=0;i<positions.length;i++)near("a five-to-one housing move preserves HC1's "+positions[i],cardsAfter[i],cardsBefore[i]);
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
        near("a half-bill budget creates no unfunded full-rent pressure in that class",
                market.classes().stream().filter(x->x.recipe==t.getId()).mapToDouble(x->x.demand).sum(),market.occupied());
        near("actual doubled placements leave no fictional unplaced residual",result.unplaced(),0);
        g.getFamilies().acceptTypedHousing(market.demands(),result,g.getHouseholdBalance().cells(),bm.getHousingInventory());
        near("the existing crowding reader sees actual guest equivalents",g.getFamilies().getDoubledUpFamilies(),count/2);
        near("the existing unplaced reader sees the actual allocation",g.getFamilies().getStillUnplaced(),0);
        long spare=g.getSectors().realEstate().limitSpareRetirement(t,100,0);
        check("a multi-structure retirement cannot cut into typed occupied doors",spare<=100-market.occupied()&&spare>0);
        var copy=new HousingMarket();copy.restore(JSON.fromJson(JSON.toJson(market.forSave()),HousingMarket.State.class));
        check("doubled door and household quantities round-trip",JSON.toJson(copy.forSave()).equals(JSON.toJson(market.forSave())));
    }
    static void doubledDemand(){
        out.println("--- emergency shelter retains funded demand for a door of one's own ---");
        Game g=new Game(GameFiles.scratch("res3-doubled-demand"));g.run();
        double[] people=new double[AgeBand.values().length+3];people[AgeBand.ADULT.ordinal()]=100;
        g.getCohorts().restore(PopulationCohorts.saveBands(),people);g.getFamilies().rememberHouseholds(false);
        double[] jobs=new double[PayTier.values().length];jobs[PayTier.UNSKILLED.ordinal()]=100;g.getFamilies().rebuild(g.getCohorts(),jobs);
        var c=g.getHouseholdBalance().cells().stream().filter(h->h instanceof WorkingHousehold w&&w.shape()==FamilyStructure.COUPLE
                &&g.getFamilies().get(w.shape(),w.tier(),w.stage())>0).map(h->(WorkingHousehold)h).findFirst().orElseThrow();
        var bm=g.getBuildingManager();var t=bm.getTemplate(HousingForms.ALL.getFirst().id());bm.addStack(t,1,true);
        double structure=g.quoteBuild(t,1).total,full=g.doorPriceOf(t,1);
        double ask=HousingForms.of(t.getId()).homeCapital(t.getHousingRecipe().mix().getFirst().id(),structure,full-structure)
                *ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12;
        var d=new HousingChoice.Demand(c.key(),2,c.size(),c.shape().dependants(),false,c.tier().ordinal(),c.stage()==AdultStage.YOUNG,ask*2);
        var market=g.getSectors().realEstate().housingMarket();var result=market.clear(g,List.of(d));
        near("two funded households find emergency shelter in one actual home",result.unplaced(),0);
        near("emergency shelter consumes one physical door",market.occupied(),1);
        near("each sheltered household counts once in class demand for its own door",market.classes().stream().mapToDouble(x->x.demand).sum(),d.count());
        check("shelter does not erase scarcity in the occupied class",market.classes().stream().anyMatch(x->x.homes>0&&x.demand>x.homes));
        double receipts=market.receipts();
        market.reprice(0);
        near("additional demand creates no additional rent receipt",market.receipts(),receipts);
        near("the one actual door earns at most its one ask",receipts,ask);
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
    static void scarceFreeHost(){
        out.println("--- scarce free host space goes to households that cannot fund a vacant share ---");
        Game g=new Game(GameFiles.scratch("res3-scarce-free-host"));g.run();
        var bm=g.getBuildingManager();var t=bm.getTemplate(0);bm.addStack(t,2,true);
        double structure=g.quoteBuild(t,1).total,full=g.doorPriceOf(t,1);
        double ask=HousingForms.of(0).homeCapital(t.getHousingRecipe().mix().getFirst().id(),structure,full-structure)*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12;
        var market=g.getSectors().realEstate().housingMarket();
        var result=market.clear(g,List.of(new HousingChoice.Demand("host",1,2,0,false,0,false,2*ask),
                new HousingChoice.Demand("fundable",1,2,0,true,0,false,.75*ask),
                new HousingChoice.Demand("poor",1,2,0,true,0,false,0)));
        near("all three households have feasible emergency shelter",result.unplaced(),0);
        near("the poor household uses the already funded host",market.rentFor("poor",1),0);
        near("the other guest funds its actual vacant half door",market.rentFor("fundable",1),ask/2);
        near("the original host still funds its whole actual bill",market.rentFor("host",1),ask);
        near("one hosted guest is counted explicitly",market.hosted(),1);
        near("occupied shares earn only their actual rent",market.receipts(),1.5*ask);
        near("actual occupied door equivalents are bounded",market.occupied(),1.5);
        check("no temporary matching node reaches a saved lease",market.leases().stream().allMatch(l->l.home().recipe()>=0));
        market.reset();result=market.clear(g,List.of(new HousingChoice.Demand("host",1,2,0,false,0,false,2*ask),
                new HousingChoice.Demand("fundable",1,2,0,true,0,false,.25*ask),
                new HousingChoice.Demand("poor",1,2,0,true,0,false,0)));
        near("without a funded vacant share one household remains explicitly unplaced",result.unplaced(),1);
        near("unfunded vacancy creates no extra receipts",market.receipts(),ask);
        near("unfunded vacancy creates no extra occupied home",market.occupied(),1);
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
    static double[] cardPositions(HouseholdBalance balances){
        double[] totals=new double[5];for(Household c:balances.cells()){
            double n=c.households();totals[0]+=n*c.debt;totals[1]+=n*c.revolving;totals[2]+=n*c.lateDebt;totals[3]+=n*c.lateShare;totals[4]+=n*c.shutOut;
        }return totals;
    }
    static void retiredRoom(){
        Game g=new Game(GameFiles.scratch("res3-retired-room"));g.run();var bm=g.getBuildingManager();
        var house=bm.getTemplateByName("House");var large=bm.getTemplateByName("Large house");
        bm.addStack(house,2,true);bm.addStack(large,2,true);
        String family=g.getHouseholdBalance().cell(FamilyStructure.COUPLE_TWO_CHILDREN,PayTier.SKILLED).key();
        var market=g.getSectors().realEstate().housingMarket();
        var placed=market.clear(g,List.of(new HousingChoice.Demand(family,2,4,2,false,1,false,1e9)));
        near("retirement fixture starts with two actually placed families",placed.suitable(),2);
        var chosen=bm.getTemplate(placed.leases().getFirst().home().recipe());
        near("retirement removes the actual occupied form",bm.retire(chosen,2),2);
        String before=JSON.toJson(market.forSave());
        near("migration room reserves doors for families displaced by retirement",
                market.roomLeft(g,new FamilyModel.Room(0,0,0,2,2,.5,4)).households(),2);
        check("the retirement room read does not strike a second quote or clearing",before.equals(JSON.toJson(market.forSave())));
    }
    static void holdHousingFixture(Game g){
        for(String sector:Sectors.KEYS)g.getBusinessInvestment().holdSector(sector);
        g.getBusinessInvestment().holdBranches();g.getAutoBuilder().setOn(false,null);
    }
    static void loadedDoorCensus()throws Exception{
        Game g=LongPlaytest.probeCity(GameFiles.scratch("res3-family-door-census"),null);
        var bm=g.getBuildingManager();holdHousingFixture(g);
        for(var t:bm.getTemplatesBySector(Sectors.REAL_ESTATE))if(t.getHousingRecipe()!=null
                &&t.getHousingRecipe().mix().stream().anyMatch(d->d.capacity()>FamilyModel.STUDIO_MAX_SIZE)){
            long removed=bm.retire(t,bm.getQuantity(t.getId()));
            g.getLandManager().release(removed*t.getLandSqFt(),t.getSector(),Game.groundUse(t));
        }
        g.setCashForTest(2*SECTOR_ENDOWMENT);g.getSectors().realEstate().setCash(SECTOR_ENDOWMENT);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt()+20_000_000);
        // Real jobs and an active builder make the next native migration draw
        // positive once the funded replacement doors open.
        check("door-census fixture pays for active construction jobs",
                g.buildStack(bm.getTemplateByName("Construction Depot"),20,true)==Game.BuildResult.SUCCESS);
        check("door-census fixture builds actual compact homes",g.buildStack(bm.getTemplateByName("Studio Apartments"),5,true)==Game.BuildResult.SUCCESS);
        g.getCohorts().migrate(500);g.toggleNextMonth();
        FamilyModel withoutCensus=new FamilyModel();withoutCensus.restore(PopulationCohorts.saveBands(),FamilyModel.saveShapes(),g.getFamilies().toSaveArray());
        withoutCensus.adoptCarriedUnplaced();withoutCensus.adoptCarriedDoubling();
        check("door-census control preserves the exact family matrix",Arrays.equals(g.getFamilies().toSaveArray(),withoutCensus.toSaveArray()));
        long familyDoors=0;long[] doors=bm.getHousingInventory().homesBySize();
        for(int size=FamilyModel.STUDIO_MAX_SIZE+1;size<doors.length;size++)familyDoors+=doors[size];
        check("fixture has more family seekers than twice its family doors",g.getFamilies().familySeekers()>2*familyDoors);
        double floor=g.getFamilies().minimumHomesTolerable();
        check("family-door scarcity actually raises the migration floor",floor>withoutCensus.minimumHomesTolerable());
        check("door-census fixture funds actual replacement homes before the next press",
                g.buildStack(bm.getTemplateByName("House"),100,false)==Game.BuildResult.SUCCESS);
        check("scarce-door fixture saves natively",g.saveGame(10,"RES3 family-door census").ok);
        Game back=new Game(g.getGameFiles());back.loadGameSave(10);holdHousingFixture(back);
        Game running=new Game(GameFiles.scratch("res3-old-door-census"));running.run();
        running.getBuildingManager().addStack(running.getBuildingManager().getTemplateByName("House"),1000,true);
        holdHousingFixture(running);running.toggleNextMonth();
        check("running load starts with a different actual family-door census",running.getBuildingManager().getTotalHomes()>=1000);
        Files.createDirectories(running.getGameFiles().saveFile(10).getParent());
        Files.copy(g.getGameFiles().saveFile(10),running.getGameFiles().saveFile(10),StandardCopyOption.REPLACE_EXISTING);
        Files.copy(g.getGameFiles().historyFile(10),running.getGameFiles().historyFile(10),StandardCopyOption.REPLACE_EXISTING);
        running.loadGameSave(10);holdHousingFixture(running);
        check("fresh native load rebuilds the scarce family-door floor",back.getFamilies().minimumHomesTolerable()==floor);
        check("running native load replaces the previous city's door census",running.getFamilies().minimumHomesTolerable()==floor);
        double crowding=g.getMigration().crowdingFactor(bm.getTotalHomes(),g.getFamilies());
        check("fresh and running reloads preserve the crowding-floor reader",
                back.getMigration().crowdingFactor(bm.getTotalHomes(),back.getFamilies())==crowding
                &&running.getMigration().crowdingFactor(bm.getTotalHomes(),running.getFamilies())==crowding);
        out.printf("   scarce-door floor %.12g (without census %.12g), crowding %.12g%n",floor,withoutCensus.minimumHomesTolerable(),crowding);
        g.toggleNextMonth();back.toggleNextMonth();running.toggleNextMonth();
        out.printf("   replacement homes finished %d; migration target %.12g; population %d%n",bm.countByName("House"),g.getMigration().getLastTarget(),g.getPopulationManager().getPopulation());
        out.printf("   next native arrivals %.12g / %.12g / %.12g; crowding %.12g / %.12g / %.12g%n",g.getMigration().getLastArrivals(),back.getMigration().getLastArrivals(),running.getMigration().getLastArrivals(),
                g.getMigration().getLastCrowding(),back.getMigration().getLastCrowding(),running.getMigration().getLastCrowding());
        check("completed funded homes cause actual positive first-month arrivals",g.getMigration().getLastArrivals()>0);
        check("the migration crowding ramp is active before the physical-room cap",
                g.getMigration().getLastCrowding()>0&&g.getMigration().getLastCrowding()<1
                &&g.getMigration().getLastArrivals()<g.getMigration().getLastRoom());
        check("first native month after both loads preserves actual arrivals",g.getMigration().getLastArrivals()==back.getMigration().getLastArrivals()
                &&g.getMigration().getLastArrivals()==running.getMigration().getLastArrivals());
        check("first native month after both loads preserves the migration crowding factor",g.getMigration().getLastCrowding()==back.getMigration().getLastCrowding()
                &&g.getMigration().getLastCrowding()==running.getMigration().getLastCrowding());
        check("scarce-door native continuations keep the actual class market",JSON.toJson(g.getSectors().realEstate().housingMarket().forSave()).equals(JSON.toJson(back.getSectors().realEstate().housingMarket().forSave()))
                &&JSON.toJson(g.getSectors().realEstate().housingMarket().forSave()).equals(JSON.toJson(running.getSectors().realEstate().housingMarket().forSave())));
        audits(g);audits(back);audits(running);
    }
    static void cardHousingBudget(){
        out.println("--- the housing budget reserves HC1's fixed card minimum ---");
        Game g=new Game(GameFiles.scratch("res3-card-budget"));g.run();
        double[] people=new double[AgeBand.values().length+3];people[AgeBand.ADULT.ordinal()]=100;
        g.getCohorts().restore(PopulationCohorts.saveBands(),people);g.getFamilies().rememberHouseholds(false);
        double[] jobs=new double[PayTier.values().length];jobs[PayTier.UNSKILLED.ordinal()]=100;g.getFamilies().rebuild(g.getCohorts(),jobs);
        var balances=g.getHouseholdBalance();balances.censusOnly(g.getFamilies());
        var cell=balances.cells().stream().filter(c->c instanceof WorkingHousehold w&&w.shape()==FamilyStructure.SINGLE_ADULT&&c.households()>0).findFirst().orElseThrow();
        double before=balances.strikeHousingBudgets(g.getFamilies(),g.getHouseholds(),0,c->0,c->10).get(cell.key()).budget();
        cell.debt=10;cell.revolving=.1;cell.lateDebt=2;cell.lateShare=.02;cell.shutOut=.05;
        var opening=cardPositions(balances);
        double withCard=balances.strikeHousingBudgets(g.getFamilies(),g.getHouseholds(),0,c->0,c->10).get(cell.key()).budget();
        near("principal in the contractual card minimum is reserved before rent",before-withCard,Bank.CARD_MINIMUM*cell.debt);
        var settled=CardCheck.cell(10,0,10,.1);settled.settle(10,0,0,0,0,.2,null,1);
        double atCurrentRate=balances.strikeHousingBudgets(g.getFamilies(),g.getHouseholds(),0,c->0,c->10,.2).get(cell.key()).budget();
        near("the quoted minimum at the current price matches actual card settlement",before-atCurrentRate,settled.minimumDue());
        check("a housing quote does not overwrite the saved settled minimum or interest",cell.minimumDue()==0&&cell.interest()==0);
        var closing=cardPositions(balances);for(int i=0;i<opening.length;i++)near("quoting a home moves no card position "+i,closing[i],opening[i]);
    }
    static void aggregatePackingRemainder(){
        out.println("--- the final shortage uses the existing whole-match packing resolution ---");
        Game g=new Game(GameFiles.scratch("res3-aggregate-packing"));g.run();var bm=g.getBuildingManager();bm.addStack(bm.getTemplate(0),100,true);
        String host=g.getHouseholdBalance().cell(FamilyStructure.COUPLE,PayTier.UNSKILLED).key();
        String guest=g.getHouseholdBalance().cell(FamilyStructure.SENIOR_ALONE,PayTier.UNSKILLED).key();
        String residue=g.getHouseholdBalance().cell(FamilyStructure.SENIOR_COUPLE,PayTier.UNSKILLED).key();
        var market=g.getSectors().realEstate().housingMarket();
        for(double missing:new double[]{Math.ulp(100.0),1e-4}){
            market.reset();List<HousingChoice.Demand> cells=List.of(new HousingChoice.Demand(host,100,2,0,false,0,false,1000),
                    new HousingChoice.Demand(guest,100,1,0,true,0,false,0),new HousingChoice.Demand(residue,missing,2,0,true,0,false,0));
            var result=market.clear(g,cells);g.getFamilies().acceptTypedHousing(market.demands(),result,g.getHouseholdBalance().cells(),bm.getHousingInventory());
            if(missing<FamilyModel.packingRounding(200)){
                check("an ULP of the whole source is exactly zero final shortage",result.unplaced()==0);
                check("an ULP cannot activate the farm's housing-priority guard",g.getFamilies().getStillUnplaced()==0);
                check("a rounded complete match records no phantom affordability or suitability reason",result.unaffordable()==0&&result.noSuitable()==0);
            }else{
                near("a genuine fractional shortage is still explicit",result.unplaced(),missing);
                check("a genuine shortage still activates housing priority",g.getFamilies().getStillUnplaced()>0);
            }
            check("rounding the counter creates no door or extra guest slot",market.occupied()<=100&&market.leases().stream().mapToDouble(HousingChoice.Lease::households).sum()<=200);
        }
    }
    static void familyHostReservation(){
        out.println("--- emergency adult groups leave compatible host space for child families ---");
        Game g=new Game(GameFiles.scratch("res3-family-host-reservation"));g.run();
        double[] people=new double[AgeBand.values().length+3];people[AgeBand.ADULT.ordinal()]=1000;people[AgeBand.CHILD.ordinal()]=100;
        g.getCohorts().restore(PopulationCohorts.saveBands(),people);g.getFamilies().rememberHouseholds(false);
        double[] jobs=new double[PayTier.values().length];jobs[PayTier.UNSKILLED.ordinal()]=1000;g.getFamilies().rebuild(g.getCohorts(),jobs);
        var single=g.getHouseholdBalance().cells().stream().filter(c->c instanceof WorkingHousehold w&&w.shape()==FamilyStructure.SINGLE_ADULT
                &&g.getFamilies().get(w.shape(),w.tier(),w.stage())>=5).map(c->(WorkingHousehold)c).findFirst().orElseThrow();
        var bm=g.getBuildingManager();bm.addStack(bm.getTemplate(0),1,true);bm.addStack(bm.getTemplate(1),1,true);
        String host=g.getHouseholdBalance().cell(FamilyStructure.LARGE_FAMILY,PayTier.UNSKILLED).key();
        String guest=g.getHouseholdBalance().cell(FamilyStructure.LARGE_FAMILY,PayTier.SKILLED).key();
        String compact=g.getHouseholdBalance().cell(FamilyStructure.COUPLE,PayTier.SKILLED).key();
        List<HousingChoice.Demand> demands=List.of(new HousingChoice.Demand(host,1,6,4,false,0,false,1000),
                new HousingChoice.Demand(compact,2,2,0,false,2,false,1000),
                new HousingChoice.Demand(single.key(),5,1,0,false,single.tier().ordinal(),single.stage()==AdultStage.YOUNG,0),
                new HousingChoice.Demand(guest,1,6,4,false,2,false,0));
        double before=0;for(FamilyStructure shape:FamilyStructure.values())before+=g.getFamilies().totalOf(shape)*shape.size();
        var market=g.getSectors().realEstate().housingMarket();var result=market.clear(g,demands);
        double after=0;for(FamilyStructure shape:FamilyStructure.values())after+=g.getFamilies().totalOf(shape)*shape.size();
        near("host allocation preserves all actual family members",after,before);
        near("three funded hosts occupy exactly three real doors",market.occupied(),3);
        check("actual single adults really form emergency sharing groups",market.sharingGroups()>0);
        near("compatible family host space is not consumed by adult soft preferences",result.unplaced(),0);
        near("the child guest obtains its whole compatible host slot",result.leases().stream().filter(l->l.cell().equals(guest)).mapToDouble(HousingChoice.Lease::households).sum(),1);
        check("all emergency guests fit the actual two-household door limit",result.leases().stream().mapToDouble(HousingChoice.Lease::households).sum()<=6+FamilyModel.packingRounding(6));
        near("emergency hosts collect only the three funded lease bills",market.receipts(),result.leases().stream().filter(l->l.cell().equals(host)||l.cell().equals(compact)).mapToDouble(HousingChoice.Lease::bill).sum());
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
        try{System.setOut(silent);classPrices();mergedBudgetPressure();plannerInputs();foundingBudgets();fractionalPressure();sharingOrder();doubledDoors();doubledDemand();studentFacility();fundedHost();scarceFreeHost();retiredRoom();loadedDoorCensus();familyHostReservation();aggregatePackingRemainder();cardHousingBudget();smallProfile(report);city(report,false);city(report,true);}finally{System.setOut(out);silent.close();}
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
