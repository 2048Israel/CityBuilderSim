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
        state.leases=List.of(new HousingChoice.Lease("family",new HousingInventory.Key(901,"family"),5,75,10,new HousingChoice.Funding(0,10,0)));
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
    static void rentPriceIndex(){
        out.println("--- comparable rents hold their basket when tenants change forms ---");
        Game g=new Game(GameFiles.scratch("res3-rent-index"));g.run();
        var re=g.getSectors().realEstate();var market=re.housingMarket();
        var state=priceInputs(0);state.pipeline=List.of();
        for(var c:state.classes){c.homes=10;c.occupied=c.demand=c.recipe==900?8:2;}
        state.leases=List.of(new HousingChoice.Lease("studio",new HousingInventory.Key(900,"studio"),8,160),
                new HousingChoice.Lease("family",new HousingInventory.Key(901,"family"),2,20));
        state.demand=List.of(new HousingChoice.Demand("studio",8,1,0,false,0,false,100),
                new HousingChoice.Demand("family",2,4,2,false,0,false,100));
        market.restore(state);re.setRentWeight(16,8);market.sold();
        near("the comparable rent index opens at the actual unit value",re.getAverageRentPaid(),7.5);
        state=market.forSave();
        for(var c:state.classes)c.occupied=c.demand=c.recipe==900?2:8;
        state.leases=List.of(new HousingChoice.Lease("studio",new HousingInventory.Key(900,"studio"),2,40),
                new HousingChoice.Lease("family",new HousingInventory.Key(901,"family"),8,80));
        state.demand=List.of(new HousingChoice.Demand("studio",2,1,0,false,0,false,100),
                new HousingChoice.Demand("family",8,4,2,false,0,false,100));
        market.restore(state);re.setRentWeight(4,32);market.sold();
        near("unchanged class bills read no inflation when the occupied mix changes",re.getAverageRentPaid(),7.5);
        near("the changed mix still pays its actual unadjusted bills",market.receipts(),120);
        state=market.forSave();state.leases=List.of(new HousingChoice.Lease("studio",new HousingInventory.Key(900,"studio"),2,44),
                new HousingChoice.Lease("family",new HousingInventory.Key(901,"family"),8,88));
        market.restore(state);market.sold();
        near("a ten-percent rise in each comparable bill reads ten-percent inflation",re.getAverageRentPaid(),8.25);
        near("the rent index does not replace the actual receipt",market.receipts(),132);
        String saved=JSON.toJson(market.forSave());
        for(int i=0;i<20;i++)re.getAverageRentPaid();
        check("reading comparable rent leaves every saved figure unchanged",saved.equals(JSON.toJson(market.forSave())));
        market.restore(JSON.fromJson(saved,HousingMarket.State.class));
        near("the comparable rent level survives its saved state exactly",re.getAverageRentPaid(),8.25);
        market.redenominate(.01);
        near("a currency redenomination scales the saved rent level once",re.getAverageRentPaid(),.0825);
        market.restore(JSON.fromJson(saved,HousingMarket.State.class));
        var bad=market.forSave();bad.rentPriceIndex=-1;boolean refused=false;
        try{market.restore(bad);}catch(IllegalArgumentException expected){refused=true;}
        check("an invalid rent level is refused before changing the live market",refused&&saved.equals(JSON.toJson(market.forSave())));
        state=market.forSave();for(var c:state.classes)c.occupied=c.demand=c.recipe==900?0:8;
        state.leases=List.of(new HousingChoice.Lease("family",new HousingInventory.Key(901,"family"),8,88));
        state.demand=List.of(new HousingChoice.Demand("family",8,4,2,false,0,false,100));state.suitable=8;
        market.restore(state);market.sold();
        near("a class with no new paid observation holds its previous price",re.getAverageRentPaid(),8.25);
        near("a vacant class contributes no receipt while its price is carried",market.receipts(),88);
    }
    static void plannerInputs(){
        out.println("--- actual operating quotes, feasible plots and the full typed backlog ---");
        Game g=new Game(GameFiles.scratch("res3-planner-inputs"));g.run();
        var bm=g.getBuildingManager();var re=g.getSectors().realEstate();var market=re.housingMarket();
        market.clear(g,List.of(new HousingChoice.Demand("planner-adults",80,2,0,true,0,false,1000)));
        for(var form:HousingForms.ALL){var t=bm.getTemplate(form.id());if(!bm.isTemplateKnown(t))continue;
            double rent=market.classes().stream().filter(c->c.recipe==t.getId()).mapToDouble(c->c.ask*t.getHousingRecipe().mix().stream().filter(d->d.id().equals(c.dwelling)).findFirst().orElseThrow().count()).sum();
            double expected=0,carried=0;
            for(var c:market.classes())if(c.recipe==t.getId()){
                var d=t.getHousingRecipe().mix().stream().filter(home->home.id().equals(c.dwelling)).findFirst().orElseThrow();
                double anchor=c.capital*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12;
                near("unbuilt "+form.code()+"/"+d.id()+" quotes gross yield or actual operating cost",c.ask,Math.max(anchor,c.carry));
                expected+=Math.max(anchor,c.carry)*d.count();carried+=c.carry*d.count();
            }
            near("unbuilt "+form.code()+" rent reconciles its comparable class quotes",rent,expected);
            near("unbuilt "+form.code()+" actual carry is allocated exactly once",carried,g.getEconomyManager().housingCarry(t));
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
    static void netOperatingChoice()throws Exception{
        out.println("--- actual project choice ranks net operating income before financing ---");
        Game g=new Game(GameFiles.scratch("res3-net-operating-choice"));g.run();
        var bm=g.getBuildingManager();var em=g.getEconomyManager();var re=g.getSectors().realEstate();var market=re.housingMarket();
        var compact=bm.getTemplate(HousingForms.ALL.stream().filter(f->f.code().equals("C01")).findFirst().orElseThrow().id());
        var house=bm.getTemplateByName("House");
        // The available area admits these two physical forms without a knowledge hook.
        // Free ground keeps the actual invoice and planner quote identical.
        g.getLandManager().setPricePerSqFt(0);em.setLandPricePerSqFt(0);
        em.getTaxPolicy().setPropertyTaxRate(0);em.setPricePerWaterUnit(0);em.setPricePerWatt(0);
        // Both the paid invoice and replacement carry buy the materials; no endowed yard stock funds either alternative.
        bm.setConstructionMaterials(0);near("the choice fixture starts with no endowed material stock",bm.getConstructionMaterials(),0);
        var plans=new BusinessInvestment(bm,em);plans.setLandAvailable(house.getLandSqFt(),0);
        double compactCost=plans.getCostOf(compact,1),houseCost=plans.getCostOf(house,1);
        double compactBase=em.housingCarry(compact),houseBase=em.housingCarry(house);
        check("both physical alternatives are actually known",bm.isTemplateKnown(compact)&&bm.isTemplateKnown(house));
        check("only the two house forms fit the available land",bm.getTemplatesBySector(re.key()).stream()
                .filter(t->t.getCapacity()>0&&plans.plotsAvailableFor(t)>0).count()==2);
        check("both alternatives have a real positive invoice",compactCost>0&&houseCost>0);
        near("compact planner cost is the actual door invoice",compactCost,g.doorPriceOf(compact,1));
        near("House planner cost is the actual door invoice",houseCost,g.doorPriceOf(house,1));
        // Open at an electricity bill equal to the compact form's gross capital
        // return, then lower that tariff (below). Existing asks retain their opening quote;
        // no rent, yield, template cost or preference is fitted to the ranking.
        double openingPower=compactCost*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12/compact.getElectricityConsumption();
        double households=compact.getDwellings()+house.getDwellings();
        em.setPricePerWatt(openingPower);market.reset();
        var demand=List.of(new HousingChoice.Demand("choice-retired-couples",households,2,0,true,0,false,houseCost));
        market.clear(g,demand);
        var compactKey=new HousingInventory.Key(compact.getId(),compact.getHousingRecipe().mix().getFirst().id());
        var houseKey=new HousingInventory.Key(house.getId(),house.getHousingRecipe().mix().getFirst().id());
        near("compact opening quote is its actual operating floor",market.price(compactKey),em.housingCarry(compact));
        // The ask's capital is the home's market value, the carry's own base (RES3c, 4f690edfc), not the door's invoice.
        near("House opening quote keeps the unchanged gross reference",market.price(houseKey),
                (em.housingStructureValue(house)+em.housingLandValue(house))*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12);
        // ...then move the tariff to where the gross and net winners part (RES3c). Since the ask's capital is the home's market
        // value (4f690edfc), the House's gross per project dollar fell, and at half the tariff the compact form won both ways.
        // The carry is linear in the tariff: read at two tariffs, it gives the tariff where the two forms' net per project
        // dollar are equal and where the first build hurdle would bind, and the fixture stands halfway between.
        var opening=market.forecast(g);
        double openIncomeC=opening.income(compact,1),openIncomeH=opening.income(house,1);
        em.setPricePerWatt(0);double compactAt0=em.housingCarry(compact),houseAt0=em.housingCarry(house);
        em.setPricePerWatt(openingPower);
        double compactPer=(em.housingCarry(compact)-compactAt0)/openingPower,housePer=(em.housingCarry(house)-houseAt0)/openingPower;
        double parity=((openIncomeH-houseAt0)/houseCost-(openIncomeC-compactAt0)/compactCost)/(housePer/houseCost-compactPer/compactCost);
        double margin=1+ham.citybuildersim.sectors.RealEstate.BUILD_MARGIN;
        double hurdle=Math.min((openIncomeC/margin-compactAt0)/compactPer,(openIncomeH/margin-houseAt0)/housePer);
        out.printf(Locale.ROOT,"   the tariff parts the winners from %.6g of the opening; the first hurdle binds at %.6g%n",
                parity/openingPower,hurdle/openingPower);
        check("fixture: a tariff parts the gross and net winners inside both build hurdles",parity>0&&parity<hurdle);
        em.setPricePerWatt((parity+hurdle)/2);
        var forecast=market.forecast(g);
        double compactIncome=forecast.income(compact,1),houseIncome=forecast.income(house,1);
        double compactCarry=em.housingCarry(compact),houseCarry=em.housingCarry(house);
        near("the actual match fills the compact alternative",compactIncome,market.price(compactKey)*compact.getDwellings());
        near("the actual match fills the House alternative",houseIncome,market.price(houseKey)*house.getDwellings());
        check("both alternatives clear the unchanged operating hurdle",compactIncome>em.housingBuildHurdle(compact)&&houseIncome>em.housingBuildHurdle(house));
        check("the compact form wins on gross receipts per actual project dollar",compactIncome/compactCost>houseIncome/houseCost);
        check("the gross winner has higher operating carry per project dollar",compactCarry/compactCost>houseCarry/houseCost);
        out.printf(Locale.ROOT,"   per project dollar: gross House %.6g compact %.6g; net House %.6g compact %.6g%n",houseIncome/houseCost,
                compactIncome/compactCost,(houseIncome-houseCarry)/houseCost,(compactIncome-compactCarry)/compactCost);
        check("House dominates on actual net operating income per project dollar",(houseIncome-houseCarry)/houseCost>(compactIncome-compactCarry)/compactCost);
        var choice=re.plan(plans,g);
        check("the actual planner chooses the net operating winner",choice.build&&choice.template==house);

        // A planner quote is an observation, including its full native saved state.
        check("choice fixture saves only in scratch slot10",g.saveGame(10,"RES3 net operating choice").ok);
        String saved=Files.readString(g.getGameFiles().saveFile(10));
        double[] pools=MoneyAudit.pools(g);String leases=JSON.toJson(market.forSave());
        String stock=JSON.toJson(bm.getHousingInventory().forSave());
        for(int i=0;i<3;i++){
            near("repeat compact quote keeps the actual invoice",plans.getCostOf(compact,1),g.doorPriceOf(compact,1));
            near("repeat forecast keeps actual expected rent",market.candidateIncome(g,house,1),houseIncome);
            var repeated=re.plan(plans,g);
            check("repeat plan preserves the chosen project and quantity",repeated.template==choice.template&&repeated.quantity==choice.quantity);
        }
        check("quotes and plans spend no cash or raise capital in any pool",Arrays.equals(pools,MoneyAudit.pools(g)));
        check("quotes and plans change no lease or saved market field",leases.equals(JSON.toJson(market.forSave())));
        check("quotes and plans change no owned or funded physical stock",stock.equals(JSON.toJson(bm.getHousingInventory().forSave())));
        check("repeated quote fixture saves only in scratch slot10",g.saveGame(10,"RES3 net operating choice").ok);
        String repeatedSave=Files.readString(g.getGameFiles().saveFile(10));
        // Each native write stamps wall-clock metadata. Preserve every other character, including numeric tokens.
        var stamp=java.util.regex.Pattern.compile("(?m)^  \"savedAt\": ([1-9][0-9]*),?$");
        var firstStamp=stamp.matcher(saved);var repeatedStamp=stamp.matcher(repeatedSave);
        boolean stamped=firstStamp.find()&&repeatedStamp.find();
        check("both native writes contain root savedAt write metadata",stamped);
        String sameStamp=stamped?repeatedSave.substring(0,repeatedStamp.start(1))+firstStamp.group(1)
                +repeatedSave.substring(repeatedStamp.end(1)):repeatedSave;
        check("quotes and plans preserve complete native saved text with only root savedAt normalized",saved.equals(sameStamp));

        em.setPricePerWatt(0);
        check("with no power bill the gross winner is also the net winner",(compactIncome-em.housingCarry(compact))/compactCost>(houseIncome-em.housingCarry(house))/houseCost);
        var same=re.plan(plans,g);
        check("the actual planner retains a gross winner that also wins on net",same.build&&same.template==compact);
        double breakEvenPower=Math.max((compactIncome-compactBase)/compact.getElectricityConsumption(),(houseIncome-houseBase)/house.getElectricityConsumption());
        em.setPricePerWatt(breakEvenPower);
        near("one actual alternative earns exactly zero net operating income",Math.max(compactIncome-em.housingCarry(compact),houseIncome-em.housingCarry(house)),0);
        check("the unchanged hurdle rejects zero net operating income",!re.plan(plans,g).build);
        em.setPricePerWatt(breakEvenPower+openingPower);
        check("actual utility bills cause negative net income for both",compactIncome<em.housingCarry(compact)&&houseIncome<em.housingCarry(house));
        check("the unchanged hurdle rejects negative net operating income",!re.plan(plans,g).build);
        em.setPricePerWatt(0);plans.setLandAvailable(compact.getLandSqFt()/2,0);
        check("the unchanged land gate rejects every otherwise profitable project",!re.plan(plans,g).build);
        plans.setLandAvailable(house.getLandSqFt(),0);market.reset();
        market.clear(g,List.of(new HousingChoice.Demand("choice-unfunded",households,2,0,true,0,false,0)));
        near("an unfunded actual match promises no compact rent",market.candidateIncome(g,compact,1),0);
        near("an unfunded actual match promises no House rent",market.candidateIncome(g,house,1),0);
        check("the unchanged funded-demand gate rejects zero receipts",!re.plan(plans,g).build);
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
    static void unbuiltGrossQuotes(){
        out.println("--- unbuilt gross rent covers actual costs without adding them to gross yield ---");
        Game g=new Game(GameFiles.scratch("res3-unbuilt-gross"));g.run();
        var bm=g.getBuildingManager();var em=g.getEconomyManager();var market=g.getSectors().realEstate().housingMarket();
        var t=bm.getTemplate(0);var recipe=t.getHousingRecipe();var dwelling=recipe.mix().getFirst();
        var key=new HousingInventory.Key(t.getId(),dwelling.id());
        for(boolean high:new boolean[]{false,true}){
            em.getTaxPolicy().setPropertyTaxRate(high?TaxPolicy.MAX_PROPERTY_TAX:0);
            market.reset();market.clear(g,List.of());
            var c=market.classes().stream().filter(row->row.key().equals(key)).findFirst().orElseThrow();
            double anchor=c.capital*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12;
            double cost=em.housingCarry(t)*dwelling.costWeight()/recipe.costWeight();
            check("fixture has an available unbuilt quote",c.available&&c.homes==0&&anchor>0&&cost>0);
            check("actual property-tax input causes costs "+(high?"above":"below")+" gross yield",high?cost>anchor:cost<anchor);
            near("unbuilt carry comes from the actual operating-cost getter",c.carry,cost);
            near("opening quote uses "+(high?"actual cost floor":"gross market yield"),c.ask,high?cost:anchor);
            market.reprice(0);
            c=market.classes().stream().filter(row->row.key().equals(key)).findFirst().orElseThrow();
            near("later unbuilt target uses "+(high?"actual cost floor":"gross market yield"),c.target,high?cost:anchor);
            near("an already correct unbuilt quote stays at its target",c.ask,c.target);
            near("unbuilt vacancy earns no rent",market.receipts(),0);
            market.sold();c=market.classes().stream().filter(row->row.key().equals(key)).findFirst().orElseThrow();
            near("unbuilt quote records no actual rental trade",c.tradeQuantity+c.tradeValue,0);
            String before=JSON.toJson(market.forSave());market.price(key);market.meanPerCapacity(t);
            check("reading the unbuilt quote changes no saved market field",before.equals(JSON.toJson(market.forSave())));
            if(high){
                double costOfHome=g.doorPriceOf(t,1);
                var refused=Mortgage.decide(1,n->g.doorPriceOf(t,n),Mortgage.ownFundsFor(costOfHome),
                        market.candidateIncome(g,t,1)-em.housingCarry(t),g.getBank().insuredMortgageRate(0));
                check("the existing lender still refuses an unviable vacant project",refused.quantity()==0
                        &&Mortgage.Decision.LENDERS_TEST.equals(refused.trimmedBy()));
            }
        }
    }
    static void fractionalSettlement(){
        out.println("--- actual fractional households receive income and pay their supplied bills ---");
        for(double count:new double[]{.25,HouseholdBalance.EMPTY_CELL,Math.nextDown(HouseholdBalance.EMPTY_CELL),0}){
            Game g=new Game(GameFiles.scratch("res3-fractional-settle-"+count));g.run();
            var bm=g.getBuildingManager();var t=bm.getTemplate(0);bm.addStack(t,4,true);
            var market=g.getSectors().realEstate().housingMarket();var b=new HouseholdBalance();
            var tier=PayTier.UNSKILLED;
            java.util.function.ToDoubleBiFunction<FamilyStructure,PayTier> census=(s,p)->p!=tier?0:
                    s==FamilyStructure.SINGLE_ADULT?count:s==FamilyStructure.COUPLE?1:0;
            b.censusOnly(census);var fraction=b.cell(FamilyStructure.SINGLE_ADULT,tier);var couple=b.cell(FamilyStructure.COUPLE,tier);
            market.clear(g,List.of(new HousingChoice.Demand(fraction.key(),count,1,0,true,0,false,1e9),
                    new HousingChoice.Demand(couple.key(),1,2,0,true,0,false,1e9)));
            double unit=Math.max(1,market.rentFor(couple.key(),1));double each=100*unit;
            b.setRentBills(c->market.rentFor(c.key(),c.households()));b.setHousedShares(c->c.households()>0?1:0);
            b.setAccountFee(unit);b.setStudentLoanRate(.06);b.setCardLenderOpenForHarness(false);
            for(var c:List.of(fraction,couple))if(c.households()>0){
                c.savings=20*unit;c.debt=3*unit;c.revolving=.5;c.studentDebt=2*unit;
                c.shares[0]=4;c.basis[0]=2*unit;c.taxShares[0]=1;c.taxClaim[0]=unit;
            }
            double[] income=new double[Household.ROWS],fees=new double[Household.ROWS],shopping=new double[Household.ROWS];
            income[tier.ordinal()]=(count*fraction.earningWeight()+couple.earningWeight())*each;
            fees[tier.ordinal()]=(count*fraction.headcount()+couple.headcount())*unit;
            b.planOnly(census,income,0,fees,unit,.05);
            double supplied=b.groceriesWanted(unit);b.allocateGroceries(supplied,unit,unit);shopping[tier.ordinal()]=supplied*unit;
            double cashBefore=b.totalSavings(),debtBefore=b.totalDebt(),studentBefore=b.totalStudentDebt();
            double claimBefore=b.sum(c->c.taxClaim[0]*c.households()),sharesBefore=b.sharesHeld(0);
            double fractionCash=fraction.savings(),fractionDebt=fraction.debt();
            b.advanceMonth(census,income,0,fees,shopping,unit,.05,1);
            near("actual fractional count is retained: "+count,fraction.households(),count);
            near("no allocated income is left unsettled: "+count,b.getUnsettledIncome(),0);
            near("no supplied fee, shopping or rent is left unsettled: "+count,b.getUnsettledFees()+b.getUnsettledShopping()+b.getUnsettledRent(),0);
            near("the funded fixture invents no cash for unpaid bills: "+count,b.getUnfunded(),0);
            double paidRent=b.sum(c->market.rentFor(c.key(),c.households())*c.households());
            near("actual household rent equals its landlord recipient: "+count,paidRent,market.receipts());
            double sellers=paidRent+fees[tier.ordinal()]+shopping[tier.ordinal()]+b.totalEmergencyBills();
            double bank=b.totalAccountFees()+b.totalInterest()+b.totalRepaid()-b.totalBorrowed();
            double treasury=b.totalStudentRepaid()+b.totalStudentInterest();
            near("sender and all actual recipients conserve cash: "+count,b.totalSavings()+sellers+bank+treasury-income[tier.ordinal()],cashBefore);
            near("card principal and fee claims conserve: "+count,b.totalDebt()+b.totalRepaid()+b.getWrittenOff(),debtBefore+b.totalBorrowed()+b.totalLoanFees());
            near("student loan claims conserve: "+count,b.totalStudentDebt()+b.totalStudentRepaid(),studentBefore+b.totalStudentBorrowed());
            near("secured tax claims are not cleared or transferred: "+count,b.sum(c->c.taxClaim[0]*c.households()),claimBefore);
            near("the existing share holdings are conserved: "+count,b.sharesHeld(0),sharesBefore);
            if(count>0){
                near("fractional recipient receives its own earning-weighted income: "+count,fraction.disposable(),each);
                near("fractional recipient pays its actual account bill: "+count,fraction.accountFee,unit);
                check("fractional recipient buys actual supplied groceries: "+count,fraction.groceriesGot>0&&shopping[tier.ordinal()]>0);
                check("fractional recipient services its own card and student loan: "+count,fraction.repaid()>0&&fraction.studentRepaid>0);
                near("fractional card claim follows only its own settled flows: "+count,fraction.debt()+fraction.repaid()+fraction.cardWrittenOff,fractionDebt+fraction.borrowed()+fraction.loanFee);
                near("fractional cash follows only its own bills: "+count,fraction.savings()-fractionCash,
                        fraction.disposable()-market.rentFor(fraction.key(),count)-unit-unit-fraction.groceriesGot*unit
                                -fraction.interest()-fraction.studentRepaid-fraction.studentInterest-fraction.repaid()-fraction.emergencyBill+fraction.borrowed());
            }else{
                near("exact zero receives no income, groceries, account bill or stock",fraction.disposable()+fraction.groceriesGot+fraction.accountFee+fraction.savings()+fraction.debt(),0);
            }
            var copy=new HouseholdBalance();check("the cell save restores its existing schema",copy.restoreCells(b.cellKeys(),b.toCellSaveArray(),Equity.COMPANIES));
            copy.setRentBills(c->market.rentFor(c.key(),c.households()));copy.setHousedShares(c->c.households()>0?1:0);
            copy.setAccountFee(unit);copy.setStudentLoanRate(.06);copy.setCardLenderOpenForHarness(false);
            double settledCash=b.totalSavings();b.planOnly(census,income,0,fees,unit,.05);copy.planOnly(census,income,0,fees,unit,.05);
            near("load/restrike spends no cash and preserves fractional ownership: "+count,copy.totalSavings(),settledCash);
            near("live restrike spends no cash: "+count,b.totalSavings(),settledCash);
            near("load preserves the actual card claims: "+count,copy.totalDebt(),b.totalDebt());
            near("load preserves the student claims: "+count,copy.totalStudentDebt(),b.totalStudentDebt());
            near("load preserves the secured tax claims: "+count,copy.sum(c->c.taxClaim[0]*c.households()),claimBefore);
            near("load reproduces the positive fractional spending plan: "+count,copy.getSpendingCapacity(),b.getSpendingCapacity());
            near("load retains the fractional recipient own cash: "+count,copy.cell(FamilyStructure.SINGLE_ADULT,tier).savings(),fraction.savings());
            double depositsBefore=b.totalSavings(),fractionBefore=fraction.savings(),coupleBefore=couple.savings();
            // The existing credit method receives the bank's booked debit; the
            // full native founding audit also checks its actual bank counterparty.
            double sourceDebit=unit;b.creditDepositInterest(sourceDebit);
            near("the booked deposit-interest debit reaches actual account holders: "+count,b.totalSavings()-depositsBefore,sourceDebit);
            near("credited deposit-income flows reconcile the actual source debit: "+count,b.sum(c->c.depositIncome*c.households()),sourceDebit);
            if(count>0){
                check("a positive fractional deposit receives its own interest: "+count,fraction.depositIncome>0);
                near("existing deposit balances receive the same actual return: "+count,
                        fraction.depositIncome/fractionBefore,couple.depositIncome/coupleBefore);
                near("the fractional owner retains its interest payment: "+count,fraction.savings()-fractionBefore,fraction.depositIncome);
            }else{
                near("exact zero receives no deposit interest",fraction.depositIncome+fraction.savings(),0);
            }
        }
    }
    static void fractionalClaims(){
        out.println("--- positive fractional owners receive existing paper and bond claims ---");
        for(double count:new double[]{.25,HouseholdBalance.EMPTY_CELL,Math.nextDown(HouseholdBalance.EMPTY_CELL),0}){
            var b=new HouseholdBalance();var tier=PayTier.UNSKILLED;
            java.util.function.ToDoubleBiFunction<FamilyStructure,PayTier> census=(s,p)->p!=tier?0:
                    s==FamilyStructure.SINGLE_ADULT?count:s==FamilyStructure.COUPLE?1:0;
            b.censusOnly(census);var fraction=b.cell(FamilyStructure.SINGLE_ADULT,tier);var couple=b.cell(FamilyStructure.COUPLE,tier);
            for(var c:List.of(fraction,couple))if(c.households()>0){
                c.savings=10;c.paper=12;c.putBondFace(7,6);c.recountBonds();c.shares[0]=2;c.basis[0]=3;
            }
            double basis=b.sum(c->c.basis[0]*c.households());
            double cash=b.totalSavings(),paper=b.totalPaper(),fractionCash=fraction.savings();
            double coupon=paper/12;b.creditPaperCoupon(coupon);
            near("paper coupon debit reaches all actual owners: "+count,b.totalSavings()-cash,coupon);
            near("paper coupon preserves its source face: "+count,b.totalPaper(),paper);
            if(count>0){
                check("the fractional paper owner receives its coupon: "+count,fraction.savings()>fractionCash);
                near("fractional and whole paper claims receive the same coupon per face: "+count,fraction.paperIncome/fraction.paper,couple.paperIncome/couple.paper);
            }
            cash=b.totalSavings();double fractionPaper=fraction.paper;double principal=paper/3;
            b.creditPaperPrincipal(principal);
            near("paper principal debit reaches every actual owner: "+count,b.totalSavings()-cash,principal);
            near("paper principal reduces exactly the redeemed face: "+count,paper-b.totalPaper(),principal);
            if(count>0)check("fractional principal is actually redeemed: "+count,fraction.paper<fractionPaper);
            cash=b.totalSavings();paper=b.totalPaper();fractionPaper=fraction.paper;
            double boughtFace=paper/2,buybackPrice=boughtFace*2;b.creditPaperBuyback(boughtFace,buybackPrice);
            near("paper buyback pays its actual source cash: "+count,b.totalSavings()-cash,buybackPrice);
            near("paper buyback reduces its actual source face: "+count,paper-b.totalPaper(),boughtFace);
            if(count>0)check("fractional buyback reduces its own paper: "+count,fraction.paper<fractionPaper);
            cash=b.totalSavings();fractionCash=fraction.savings();double dueFraction=count*2,dueCouple=2;
            double credited=b.creditBondCoupons(Map.of(fraction.key(),dueFraction,couple.key(),dueCouple));
            near("record-date bond coupons reach their actual owners: "+count,credited,dueFraction+dueCouple);
            near("bond coupon source debit equals aggregate account credit: "+count,b.totalSavings()-cash,credited);
            near("the fractional owner's own recorded coupon stays with it: "+count,(fraction.savings()-fractionCash)*count,dueFraction);
            double[] noIncome=new double[Household.ROWS];
            var copy=new HouseholdBalance();check("paper and bond owner cell save restores",copy.restoreCells(b.cellKeys(),b.toCellSaveArray(),Equity.COMPANIES));
            copy.restoreBondsByCell(b.bondsByCellToSave());
            cash=b.totalSavings();b.planOnly(census,noIncome,0,noIncome,0,0);copy.planOnly(census,noIncome,0,noIncome,0,0);
            near("claim reload/replan conserves every owner's cash: "+count,copy.totalSavings(),cash);
            near("claim reload retains the actual paper face: "+count,copy.totalPaper(),b.totalPaper());
            near("claim reload retains the actual bond face: "+count,copy.totalBonds(),b.totalBonds());
            near("claim payments preserve existing security basis: "+count,b.sum(c->c.basis[0]*c.households()),basis);
            near("claim reload preserves existing security basis: "+count,copy.sum(c->c.basis[0]*c.households()),basis);
            near("claim reload reproduces the same spending plan: "+count,copy.getSpendingCapacity(),b.getSpendingCapacity());
            cash=b.totalSavings();double face=b.bondFaceHeld(7);double paid=b.creditBondPrincipal(7);
            near("maturing bond principal pays all actual face: "+count,paid,face);
            near("maturing bond source debit reaches accounts: "+count,b.totalSavings()-cash,paid);
            near("maturity extinguishes exactly that bond claim: "+count,b.bondFaceHeld(7),0);
            if(count==0)near("exact zero has no paper, bond or payment",fraction.paper+fraction.bonds+fraction.savings(),0);
            cash=b.totalSavings();double orphaned=b.creditBondCoupons(Map.of("no-owner",1.0));
            near("a genuine orphan coupon retains the existing eligible-recipient fallback: "+count,orphaned,1);
            near("the genuine orphan fallback conserves its actual source debit: "+count,b.totalSavings()-cash,orphaned);
        }
        var nobody=new HouseholdBalance();
        near("no eligible recipient creates no orphan-coupon cash",nobody.creditBondCoupons(Map.of("no-owner",1.0)),0);
        near("no eligible recipient leaves aggregate cash unchanged",nobody.totalSavings(),0);
    }
    /** REV-RES3z: a cell under EMPTY_CELL keeps its shares, so its dividend and lending fee are paid to it at home, where
     *  Equity books as abroad what creditDividend() does not pay and Exchange pays the cells only what creditLendingFee() does. */
    static void fractionalDividends(){
        out.println("--- positive fractional owners receive their dividend and lending fee at home ---");
        for(double count:new double[]{.25,HouseholdBalance.EMPTY_CELL,Math.nextDown(HouseholdBalance.EMPTY_CELL),0}){
            var b=new HouseholdBalance();var tier=PayTier.UNSKILLED;double[] taxed={0};
            b.setInvestmentTaxes(new TaxPolicy(),c->10,(line,amount)->taxed[0]+=amount);
            java.util.function.ToDoubleBiFunction<FamilyStructure,PayTier> census=(s,p)->p!=tier?0:
                    s==FamilyStructure.SINGLE_ADULT?count:s==FamilyStructure.COUPLE?1:0;
            b.censusOnly(census);var fraction=b.cell(FamilyStructure.SINGLE_ADULT,tier);var couple=b.cell(FamilyStructure.COUPLE,tier);
            for(var c:List.of(fraction,couple))if(c.households()>0){c.savings=10;c.shares[0]=2;c.basis[0]=3;}
            double held=b.sharesHeld(0),perShare=.5;
            double cash=b.totalSavings(),fractionCash=fraction.savings();
            double home=b.creditDividend(0,perShare);
            near("the dividend paid at home is every actual owner's, none left for Equity to book abroad: "+count,home,held*perShare);
            near("the dividend conserves: the owners' cash and the tax on it are what was paid: "+count,b.totalSavings()-cash+taxed[0],home);
            if(count>0){
                check("the fractional owner receives its own dividend: "+count,fraction.savings()>fractionCash);
                near("fractional and whole owners receive the same dividend per share: "+count,
                        fraction.dividends()/fraction.shares[0],couple.dividends()/couple.shares[0]);
            }
            cash=b.totalSavings();fractionCash=fraction.savings();double taxBefore=taxed[0],fee=.25;
            double paid=b.creditLendingFee(0,fee);
            near("the lending fee reaches every actual lender at home, all of the cells' share: "+count,paid,held*fee);
            near("the lending fee conserves: the lenders' cash and any claim it settles are what was paid: "+count,
                    b.totalSavings()-cash+taxed[0]-taxBefore,paid);
            if(count>0)near("the fractional lender receives its own fee: "+count,fraction.savings()-fractionCash,fraction.shares[0]*fee);
            if(count==0)near("exact zero holds nothing and is paid nothing",fraction.shares[0]+fraction.savings(),0);
        }
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
            near("an unbuilt class keeps its gross quote or actual cost floor "+c.key(),c.ask,
                    Math.max(c.capital*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12,c.carry));
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
            double structure=g.getEconomyManager().housingStructureValue(house),full=structure+g.getEconomyManager().housingLandValue(house);
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
        double structure=g.getEconomyManager().housingStructureValue(t),full=structure+g.getEconomyManager().housingLandValue(t);
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
        double structure=g.getEconomyManager().housingStructureValue(t),full=structure+g.getEconomyManager().housingLandValue(t);
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
        double structure=g.getEconomyManager().housingStructureValue(t),full=structure+g.getEconomyManager().housingLandValue(t);
        double ask=HousingForms.of(0).homeCapital(t.getHousingRecipe().mix().getFirst().id(),structure,full-structure)*ham.citybuildersim.sectors.RealEstate.LANDLORD_YIELD/12;
        var market=g.getSectors().realEstate().housingMarket();
        // Four to a household: a host and one guest fill the door to its census line (HousingChoice.line), so the
        // host place is scarce and no lodging room is left beside them (RES3c, 2026-11-25).
        int size=HousingChoice.line(t.getHousingRecipe().mix().getFirst().bedrooms())/2;
        check("fixture: a host and one guest fill the door to its census line",2*size==HousingChoice.line(t.getHousingRecipe().mix().getFirst().bedrooms())
                &&size<=t.getHousingRecipe().mix().getFirst().capacity());
        var result=market.clear(g,List.of(new HousingChoice.Demand("host",1,size,0,false,0,false,2*ask),
                new HousingChoice.Demand("fundable",1,size,0,true,0,false,.75*ask),
                new HousingChoice.Demand("poor",1,size,0,true,0,false,0)));
        near("all three households have feasible emergency shelter",result.unplaced(),0);
        near("the poor household uses the already funded host",market.rentFor("poor",1),0);
        near("the other guest funds its actual vacant half door",market.rentFor("fundable",1),ask/2);
        near("the original host still funds its whole actual bill",market.rentFor("host",1),ask);
        near("one hosted guest is counted explicitly",market.hosted(),1);
        near("occupied shares earn only their actual rent",market.receipts(),1.5*ask);
        near("actual occupied door equivalents are bounded",market.occupied(),1.5);
        check("no temporary matching node reaches a saved lease",market.leases().stream().allMatch(l->l.home().recipe()>=0));
        market.reset();result=market.clear(g,List.of(new HousingChoice.Demand("host",1,size,0,false,0,false,2*ask),
                new HousingChoice.Demand("fundable",1,size,0,true,0,false,.25*ask),
                new HousingChoice.Demand("poor",1,size,0,true,0,false,0)));
        near("without a funded vacant share one household remains explicitly unplaced",result.unplaced(),1);
        near("unfunded vacancy creates no extra receipts",market.receipts(),ask);
        near("unfunded vacancy creates no extra occupied home",market.occupied(),1);
    }
    static void fundedHost(){
        Game g=new Game(GameFiles.scratch("res3-funded-host"));g.run();var bm=g.getBuildingManager();var t=bm.getTemplate(0);bm.addStack(t,1,true);
        double structure=g.getEconomyManager().housingStructureValue(t),full=structure+g.getEconomyManager().housingLandValue(t);
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
        // Lodgers (HousingMarket.lodging()) hold no share: the shares stay two to a door, the people within the census line.
        check("a sharing group consumes the host slot before further guests",result.leases().stream().mapToDouble(HousingMarket::shares).sum()
                <=2*market.occupied()+FamilyModel.packingRounding(2*market.occupied()));
        check("...and whoever boards beyond it keeps the door within its census line (HousingChoice.line)",withinLine(market,result));
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
        // Today's city, its compact forms and depot (R1g: probeCity() founds the 1900 city since 0.9.206; this fixture's subject is today's door census).
        Game g=LongPlaytest.probeTodayCity(GameFiles.scratch("res3-family-door-census"),null);
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
        var modes=g.getSectors().realEstate().housingMarket().leases();
        out.println("   native scarce-door saved contracts full="+modes.stream().mapToDouble(l->l.funding().full()).sum()+" half="+modes.stream().mapToDouble(l->l.funding().half()).sum()+" sponsored="+modes.stream().mapToDouble(l->l.funding().sponsored()).sum());
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
    /** A paid Bank mortgage, vacant-title closure, then the real capacity/share writer and native twins. */
    static void causedStrandedDebtContinuation() throws Exception {
        out.println("--- caused positive closed-home debt, native save and continuation ---");
        Game g=new Game(GameFiles.scratch("res3-caused-closed-debt"),Founding.defaults());g.run();
        holdHousingFixture(g);
        var bm=g.getBuildingManager();var re=g.getSectors().realEstate();
        var credit=g.getEconomyManager().getBusinessDebtManager();
        var house=bm.getTemplateByName("House");var compact=bm.getTemplateByName("Studio Apartments");
        long[] order={4};
        // Declared initial stock/equity/ground, before this fixture's audit opens.
        // Excess real doors CAUSE vacancy instead of finding it in a played town.
        g.setCashForTest(Founding.WEALTHY_CASH);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt()+20_000_000);
        check("closed-debt fixture builds its known standing family stock",g.buildStack(house,1000,true)==Game.BuildResult.SUCCESS);
        check("closed-debt fixture builds its known standing compact stock",g.buildStack(compact,20,true)==Game.BuildResult.SUCCESS);
        check("closed-debt fixture funds an actual Bank branch",g.buildStack(bm.getTemplateByName("Commercial Bank"),1,true)==Game.BuildResult.SUCCESS);
        check("closed-debt fixture funds actual crews",g.buildStack(bm.getTemplateByName("Construction Depot"),2,true)==Game.BuildResult.SUCCESS);
        double cost=g.quoteBuild(house,order[0]).total+g.getLandManager().priceFor(house.getLandSqFt()*order[0],house.getSector(),Game.groundUse(house));
        double minimum=Mortgage.ownFundsFor(cost);
        re.setCash(minimum+(cost-minimum)/3);
        // run() initializes templates/bank/yard, not a settled household census.
        // Seed only an absent opening population through the native migration
        // input; derive its age/stage/shape/tier census before the first audit.
        var openingBalance=g.getHouseholdBalance();var openingCells=openingBalance.cells();
        long startupPositive=openingCells.stream().filter(h->h.households()>0).count();
        out.printf(Locale.ROOT,"   startup: positive cells %d held HH %a cohorts %a family HH %a%n",startupPositive,openingCells.stream().mapToDouble(Household::households).sum(),g.getCohorts().total(),g.getFamilies().householdsSeekingDoors());
        // Reuse the pushed complete financial-position reader, without running
        // its finding: census setup must preserve every existing raw position.
        var positions=FinancialOwnerFinding.class.getDeclaredMethod("positions",Household.class);positions.setAccessible(true);
        List<Object> beforeCensus=new ArrayList<>();for(Household h:openingCells)beforeCensus.add(positions.invoke(null,h));
        if(startupPositive==0){
            if(!(g.getCohorts().total()>0))g.getCohorts().migrate(500);
            if(!(g.getFamilies().householdsSeekingDoors()>0)){
                var population=g.getPopulationManager();
                population.applyPopulation(Math.round(g.getCohorts().total()),g.getCohorts().get(AgeBand.ADULT));
                population.updateJobs(g.getJobs());
                double[] jobsByTier=new double[PayTier.values().length];
                long[] posts=population.getJobs();double[] fill=population.getJobFillRate();
                for(JobType type:JobType.values())jobsByTier[PayTier.of(type).ordinal()]+=posts[type.ordinal()]*fill[type.ordinal()];
                g.getFamilies().rebuild(g.getCohorts(),jobsByTier);
            }
            double[] familyCensus=g.getFamilies().toSaveArray(),cohortCensus=g.getCohorts().toSaveArray();
            // Existing non-accruing native load/replan path -> HB.planOnly();
            // no second settlement, by-hand HB counts, or chosen family shares.
            g.refreshHouseholdAccounts();
            check("opening native census preserves the age/stage pyramid and family shape/tier matrix",Arrays.equals(cohortCensus,g.getCohorts().toSaveArray())&&Arrays.equals(familyCensus,g.getFamilies().toSaveArray()));
        }
        List<Object> afterCensus=new ArrayList<>();for(Household h:openingCells)afterCensus.add(positions.invoke(null,h));
        check("opening native census preserves every existing financial position",beforeCensus.equals(afterCensus));
        long positiveCells=openingCells.stream().filter(h->h.households()>0).count();
        double openingHouseholds=openingCells.stream().filter(h->h.households()>0).mapToDouble(Household::households).sum();
        double[] openingCounts=openingCells.stream().mapToDouble(Household::households).toArray();
        boolean populated=positiveCells>0&&openingHouseholds>0&&g.getCohorts().total()>0;
        check("opening wealth has actual positive households and population",populated);
        if(!populated||!beforeCensus.equals(afterCensus))throw new IllegalStateException("invalid opening census; no wealth or closure attempted");
        // Opening savings and matching Bank cash are DECLARED fixture stocks,
        // before the first audit. No after-audit cash or eligibility rewrite.
        double openingSaving=g.doorPriceOf(house,1),addedDeposits=0;
        check("opening native House invoice is a positive finite wealth floor",Double.isFinite(openingSaving)&&openingSaving>0);
        if(!(Double.isFinite(openingSaving)&&openingSaving>0))throw new IllegalStateException("invalid opening wealth floor");
        double heldBefore=g.getHouseholdDeposits(),bankCashBefore=g.getBank().getCash();
        List<Object> otherPositions=new ArrayList<>();for(Household h:openingCells){Map<?,?> p=(Map<?,?>)positions.invoke(null,h);p.remove("savings");otherPositions.add(p);}
        int endowedCells=0;
        for(Household h:openingCells)if(h.households()>0){
            double before=h.savings(),add=Math.max(0,openingSaving-before);
            h.savings+=add;addedDeposits+=h.households()*add;endowedCells++;
            out.printf(Locale.ROOT,"   opening wealth %s: count %a before %a add %a after %a%n",h.key(),h.households(),before,add,h.savings());
        }
        g.getBank().setCash(bankCashBefore+addedDeposits);
        double endowedHouseholds=openingCells.stream().filter(h->h.households()>0).mapToDouble(Household::households).sum();
        boolean floorHolds=endowedCells==positiveCells&&Double.doubleToLongBits(endowedHouseholds)==Double.doubleToLongBits(openingHouseholds)
                &&Arrays.equals(openingCounts,openingCells.stream().mapToDouble(Household::households).toArray())
                &&openingCells.stream().filter(h->h.households()>0).allMatch(h->h.savings()>=openingSaving);
        check("every positive opening cell and household receives the declared wealth floor",floorHolds);
        List<Object> otherAfter=new ArrayList<>();for(Household h:openingCells){Map<?,?> p=(Map<?,?>)positions.invoke(null,h);p.remove("savings");otherAfter.add(p);}
        check("opening wealth preserves all non-savings financial positions",otherPositions.equals(otherAfter));
        near("opening weighted savings increase equals declared Bank cash backing",g.getHouseholdDeposits()-heldBefore,addedDeposits);
        check("opening Bank receives exactly the declared weighted cash backing",Double.doubleToLongBits(g.getBank().getCash())==Double.doubleToLongBits(bankCashBefore+addedDeposits));
        // Strike the Bank's liabilities/claims from those actual opening stocks;
        // refreshBank derives books and weights, and transfers no cash.
        var refresh=Game.class.getDeclaredMethod("refreshBank");refresh.setAccessible(true);refresh.invoke(g);
        check("opening native Bank deposit book equals actual weighted savings",Double.doubleToLongBits(g.getBank().getHouseholdDeposits())==Double.doubleToLongBits(g.getHouseholdDeposits()));
        check("opening native Bank book refresh transfers no cash",Double.doubleToLongBits(g.getBank().getCash())==Double.doubleToLongBits(bankCashBefore+addedDeposits));
        out.printf(Locale.ROOT,"   opening census/wealth: cells %d endowed %d HH %a floor %a weighted add %a held %a->%a Bank cash %a->%a book %a%n",positiveCells,endowedCells,openingHouseholds,openingSaving,addedDeposits,heldBefore,g.getHouseholdDeposits(),bankCashBefore,g.getBank().getCash(),g.getBank().getHouseholdDeposits());
        if(!floorHolds||!otherPositions.equals(otherAfter))throw new IllegalStateException("invalid opening wealth; no closure attempted");
        g.toggleNextMonth();audits(g);
        check("closed-debt fixture starts without stranded debt",re.strandedDebtShare()==0);
        long sitesBefore=bm.getStack(house).getUnderConstruction();
        check("closed-debt fixture has no earlier family sites",sitesBefore==0);
        // The existing HoldersCheck callback is inside the open audit, after
        // startAuditMonth() and before bank.lend/fees. A between-press mortgage
        // would have its pending lending/fee/premium counters cleared next press.
        boolean[] orderedInMonth={false};
        g.settleProbeForTest=after->{
            if(after||orderedInMonth[0])return;
            orderedInMonth[0]=true;
            double available=re.getCash(),one=g.doorPriceOf(house,1);
            // Size the actual purchase above ACTUAL cash, not yesterday's quote
            // and endowment. This is an order choice, never a rewritten till.
            order[0]=Math.max(order[0],(long)Math.ceil(2*Math.max(0,available)/one));
            double actualCost=g.quoteBuild(house,order[0]).total+g.getLandManager().priceFor(house.getLandSqFt()*order[0],house.getSector(),Game.groundUse(house));
            double principalBeforeFunding=credit.getPrincipal(Sectors.REAL_ESTATE);
            double lentBefore=credit.getLentThisMonth(),feesBefore=credit.getFeesThisMonth(),premiumsBefore=credit.getPremiumsThisMonth();
            int mortgagesBefore=credit.getMortgageCount(Sectors.REAL_ESTATE);
            out.println("   in-audit pre-order native claims "+JSON.toJson(credit.getLoans()));
            out.printf(Locale.ROOT,"   in-audit pre-order m%d: cash %a cost %a own-min %a shortfall %a principal %a lent/fee/premium %a/%a/%a qty %d%n",g.getMonth(),available,actualCost,Mortgage.ownFundsFor(actualCost),actualCost-available,principalBeforeFunding,lentBefore,feesBefore,premiumsBefore,order[0]);
            check("closed-debt actual quote requires a mortgage with sufficient native down payment",actualCost>available&&available>=Mortgage.ownFundsFor(actualCost));
            if(!(actualCost>available&&available>=Mortgage.ownFundsFor(actualCost)))throw new IllegalStateException("closed-debt fixture has no admissible actual shortfall");
            Investor payer;
            String[] refusal={null};
            try{
                var factory=Game.class.getDeclaredMethod("mortgageInvestor",String.class,String[].class);factory.setAccessible(true);
                payer=(Investor)factory.invoke(g,Sectors.REAL_ESTATE,(Object)refusal);
            }catch(ReflectiveOperationException e){throw new IllegalStateException(e);}
            boolean funded=g.buildFor(payer,house,order[0]);
            out.println("   in-audit post-order native claims "+JSON.toJson(credit.getLoans()));
            out.printf(Locale.ROOT,"   in-audit post-order: funded %s refusal %s cash %a principal %a mortgages %d->%d lent/fee/premium %a/%a/%a%n",funded,refusal[0],re.getCash(),credit.getPrincipal(Sectors.REAL_ESTATE),mortgagesBefore,credit.getMortgageCount(Sectors.REAL_ESTATE),credit.getLentThisMonth(),credit.getFeesThisMonth(),credit.getPremiumsThisMonth());
            check("closed-debt fixture funds the actual residential order",funded);
            check("closed-debt fixture created actual Bank mortgage principal",credit.getPrincipal(Sectors.REAL_ESTATE)>principalBeforeFunding);
            check("closed-debt order is on the actual paid work queue",bm.getStack(house).getUnderConstruction()-sitesBefore==order[0]);
            check("closed-debt order wrote one actual mortgage",credit.getMortgageCount(Sectors.REAL_ESTATE)==mortgagesBefore+1);
            if(!funded||!(credit.getPrincipal(Sectors.REAL_ESTATE)>principalBeforeFunding)||credit.getMortgageCount(Sectors.REAL_ESTATE)!=mortgagesBefore+1)throw new IllegalStateException("native mortgage/build refusal: "+refusal[0]);
            var mortgage=credit.getMortgages(Sectors.REAL_ESTATE).getLast();
            near("new actual mortgage principal is on the native lending flow",credit.getLentThisMonth()-lentBefore,mortgage.getOutstandingPrincipal());
            near("new actual mortgage fee is on the native fee flow",credit.getFeesThisMonth()-feesBefore,BusinessDebtManager.feeOn(mortgage.getOutstandingPrincipal()));
            near("new actual mortgage premium is on the native premium flow",credit.getPremiumsThisMonth()-premiumsBefore,mortgage.getPremium());
        };
        try{g.toggleNextMonth();}finally{g.settleProbeForTest=null;}
        audits(g);check("closed-debt paid order executed in exactly one native settlement window",orderedInMonth[0]);
        long ordered=order[0],closed=ordered+1;
        int reIndex=Arrays.asList(Sectors.KEYS).indexOf(Sectors.REAL_ESTATE);
        check("the real Bank holds the borrower's funded claim",g.getBank().getLoansToSector(reIndex)>0&&credit.getPrincipal(Sectors.REAL_ESTATE)>0);
        var family=g.getFamilies();var heldMarket=re.housingMarket().forSave();
        boolean eligible=re.mayRetire(house);long vacantLimit=re.limitSpareRetirement(house,closed,0);
        out.printf(Locale.ROOT,"   normal retirement m%d: unplaced %a doubled %a studio/family shortfall %a/%a market unplaced/unaffordable/noSuitable/crowded %a/%a/%a/%a mayRetire %s vacantLimit %d requested %d%n",g.getMonth(),family.getStillUnplaced(),family.getDoubledUpHouseholds(),re.doorShortfall(false),re.doorShortfall(true),heldMarket.unplaced,heldMarket.unaffordable,heldMarket.noSuitable,heldMarket.crowded,eligible,vacantLimit,closed);
        check("normal retirement eligibility permits this vacant form",eligible);
        check("every class's actual vacant RE titles permit the whole closure",vacantLimit>=closed);
        if(!eligible||vacantLimit<closed)throw new IllegalStateException("closed-debt normal retirement precondition failed; physical closure not attempted");
        double capacityBefore=re.getOwnedHousingCapacity(),debtBefore=credit.getPrincipal(Sectors.REAL_ESTATE);
        String claimsBefore=JSON.toJson(credit.getLoans());double[] poolsBefore=MoneyAudit.pools(g);
        // Same physical vacant-retirement/land-release path as the existing loaded-door case.
        // It books no invented sale, salvage receipt, or repayment.
        long removed=bm.retire(house,closed);
        check("the caused vacancy really closes all requested structures",removed==closed);
        g.getLandManager().release(removed*house.getLandSqFt(),house.getSector(),Game.groundUse(house));
        check("physical closure preserves every actual loan claim",claimsBefore.equals(JSON.toJson(credit.getLoans()))&&debtBefore==credit.getPrincipal(Sectors.REAL_ESTATE));
        check("physical closure creates or loses no money",Arrays.equals(poolsBefore,MoneyAudit.pools(g)));
        g.toggleNextMonth();audits(g);
        // At most ordered homes can complete; closing ordered+1 guarantees a real net capacity fall.
        check("the normal capacity writer observes a real owned-capacity reduction",re.getOwnedHousingCapacity()<capacityBefore);
        check("the actual capacity writer CAUSED a positive stranded share",re.strandedDebtShare()>0&&re.strandedDebtShare()<=1);
        check("positive closed-home share still carries actual Bank debt",credit.getPrincipal(Sectors.REAL_ESTATE)>0&&g.getBank().getLoansToSector(reIndex)>0);
        double share=re.strandedDebtShare(),capacity=re.getOwnedHousingCapacity(),debt=credit.getPrincipal(Sectors.REAL_ESTATE);
        double bankClaim=g.getBank().getLoansToSector(reIndex);
        String claims=JSON.toJson(credit.getLoans()),inventory=JSON.toJson(bm.getHousingInventory().forSave());
        out.printf(Locale.ROOT,"   caused closure m%d: %d structures; share %a; owned capacity %a -> %a; debt %a; Bank claim %a%n",g.getMonth(),removed,share,capacityBefore,capacity,debt,bankClaim);
        check("positive closed-home debt saves through the actual native writer",g.saveGame(10,"RES3 caused closed-home debt").ok);
        // Retain the exact native pair outside TempDirs' normal JVM-exit cleanup.
        Path retained=Path.of("target","checks","closed-debt-native");Files.createDirectories(retained);
        Files.copy(g.getGameFiles().saveFile(10),retained.resolve("save10.json"),StandardCopyOption.REPLACE_EXISTING);
        Files.copy(g.getGameFiles().historyFile(10),retained.resolve("history10.json"),StandardCopyOption.REPLACE_EXISTING);
        Game back=new Game(g.getGameFiles());back.loadGameSave(10);holdHousingFixture(back);
        var loaded=back.getSectors().realEstate();var backCredit=back.getEconomyManager().getBusinessDebtManager();
        check("native reload preserves the exact held positive stranded share",Double.doubleToLongBits(share)==Double.doubleToLongBits(loaded.strandedDebtShare()));
        check("native reload preserves the exact capacity writer's base",Double.doubleToLongBits(capacity)==Double.doubleToLongBits(loaded.getOwnedHousingCapacity()));
        check("native reload preserves the exact live borrower principal",Double.doubleToLongBits(debt)==Double.doubleToLongBits(backCredit.getPrincipal(Sectors.REAL_ESTATE)));
        check("native reload preserves every actual loan claim",claims.equals(JSON.toJson(backCredit.getLoans())));
        check("native reload preserves the actual Bank's held claim",Double.doubleToLongBits(bankClaim)==Double.doubleToLongBits(back.getBank().getLoansToSector(reIndex)));
        check("native reload preserves all physical owner/title cohorts",inventory.equals(JSON.toJson(back.getBuildingManager().getHousingInventory().forSave())));
        g.toggleNextMonth();back.toggleNextMonth();audits(g);audits(back);
        check("next native twins keep the exact positive debt share",Double.doubleToLongBits(re.strandedDebtShare())==Double.doubleToLongBits(loaded.strandedDebtShare()));
        check("next native twins keep the exact capacity base",Double.doubleToLongBits(re.getOwnedHousingCapacity())==Double.doubleToLongBits(loaded.getOwnedHousingCapacity()));
        check("next native twins keep all actual borrower claims",JSON.toJson(credit.getLoans()).equals(JSON.toJson(backCredit.getLoans())));
        check("next native twins keep the actual Bank claim",Double.doubleToLongBits(g.getBank().getLoansToSector(reIndex))==Double.doubleToLongBits(back.getBank().getLoansToSector(reIndex)));
        check("next native twins keep every physical owner/title cohort",JSON.toJson(bm.getHousingInventory().forSave()).equals(JSON.toJson(back.getBuildingManager().getHousingInventory().forSave())));
    }

    static void savingHousingBudget(){
        out.println("--- the real saving return reaches funded demand at the ask ---");
        Game g=new Game(GameFiles.scratch("res3-saving-budget"));g.run();
        double[] people=new double[AgeBand.values().length+3];people[AgeBand.ADULT.ordinal()]=100;
        g.getCohorts().restore(PopulationCohorts.saveBands(),people);g.getFamilies().rememberHouseholds(false);
        double[] jobs=new double[PayTier.values().length];jobs[PayTier.UNSKILLED.ordinal()]=100;g.getFamilies().rebuild(g.getCohorts(),jobs);
        var balances=g.getHouseholdBalance();balances.censusOnly(g.getFamilies());
        var cell=balances.cells().stream().filter(c->c instanceof WorkingHousehold w&&w.shape()==FamilyStructure.SINGLE_ADULT&&c.households()>0).findFirst().orElseThrow();
        cell.savings=0;balances.setSpendFactor(1);
        double ordinary=balances.strikeHousingBudgets(g.getFamilies(),g.getHouseholds(),0,c->4,c->10).get(cell.key()).budget();
        balances.setSpendFactor(HouseholdBalance.SPEND_FLOOR);
        double saving=balances.strikeHousingBudgets(g.getFamilies(),g.getHouseholds(),0,c->4,c->10).get(cell.key()).budget();
        near("saving scales only the bid above the committed rent",saving,4+HouseholdBalance.SPEND_FLOOR*(ordinary-4));
        check("a higher saving return reduces the funded bid",saving<ordinary);
        check("the current funded lease is protected",saving>=4);
        balances.setSpendFactor(HouseholdBalance.SPEND_CEILING);
        near("a spending stimulus cannot promise more cash than the cell can fund",balances.strikeHousingBudgets(g.getFamilies(),g.getHouseholds(),0,c->4,c->10).get(cell.key()).budget(),ordinary);
        double[] targets=new double[2];int i=0;double ask=(ordinary+saving)/2;
        for(double budget:new double[]{ordinary,saving}){
            var state=priceInputs(0);var home=state.classes.get(1);home.occupied=home.demand=0;
            state.leases=List.of();state.suitable=0;state.unplaced=state.noSuitable=20;
            state.demand=List.of(new HousingChoice.Demand("family",20,4,2,false,0,false,budget));
            HousingMarket market=new HousingMarket();market.restore(state);
            market.strikePriceDemand(List.of(new HousingChoice.Offer(home.key(),10,4,2,75,1,0,false,ask,0)));
            near("the ask admits only the funded bids at budget "+budget,market.classes().get(1).demand,i==0?20:0);
            market.reprice(0);targets[i++]=market.classes().get(1).target;
        }
        check("the reduced bid lowers the class price target through demand",targets[1]<targets[0]);
        near("striking bids moves no household savings",cell.savings(),0);
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
        // A hundred one-bedroom doors, each a couple and its guest couple: four, the door's census line, so the
        // residue has no room in the doubling or in lodging (RES3c, 2026-11-25).
        Game g=new Game(GameFiles.scratch("res3-aggregate-packing"));g.run();var bm=g.getBuildingManager();var h55=bm.getTemplate(HousingForms.ALL.getFirst().id());bm.addStack(h55,100,true);
        check("fixture: a couple and its guest couple fill the door to its census line",HousingChoice.line(h55.getHousingRecipe().mix().getFirst().bedrooms())==4);
        String host=g.getHouseholdBalance().cell(FamilyStructure.COUPLE,PayTier.UNSKILLED).key();
        String guest=g.getHouseholdBalance().cell(FamilyStructure.SENIOR_COUPLE,PayTier.UNSKILLED).key();
        String residue=g.getHouseholdBalance().cell(FamilyStructure.SENIOR_ALONE,PayTier.UNSKILLED).key();
        var market=g.getSectors().realEstate().housingMarket();
        for(double missing:new double[]{Math.ulp(100.0),1e-4}){
            market.reset();List<HousingChoice.Demand> cells=List.of(new HousingChoice.Demand(host,100,2,0,false,0,false,1000),
                    new HousingChoice.Demand(guest,100,2,0,true,0,false,0),new HousingChoice.Demand(residue,missing,1,0,true,0,false,0));
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
        // Two three-bedroom doors (census line 8): beside a couple a place for six, beside a couple and child one
        // for five. Both fit the five adults' group; only the first fits the child family of six. Two one-bedroom
        // doors (line 4) beside couples take the single adults left over from the group (MAX_SHARING), and no child.
        var bm=g.getBuildingManager();var h120=bm.getTemplate(0);var h55=bm.getTemplate(HousingForms.ALL.getFirst().id());
        bm.addStack(h120,2,true);bm.addStack(h55,2,true);
        check("fixture: the three-bedroom door's line is eight, the one-bedroom's four",HousingChoice.line(h120.getHousingRecipe().mix().getFirst().bedrooms())==8
                &&HousingChoice.line(h55.getHousingRecipe().mix().getFirst().bedrooms())==4);
        String host=g.getHouseholdBalance().cell(FamilyStructure.COUPLE_CHILD,PayTier.UNSKILLED).key();
        String couple=g.getHouseholdBalance().cell(FamilyStructure.COUPLE,PayTier.UNSKILLED).key();
        String guest=g.getHouseholdBalance().cell(FamilyStructure.LARGE_FAMILY,PayTier.SKILLED).key();
        String compact=g.getHouseholdBalance().cell(FamilyStructure.COUPLE,PayTier.SKILLED).key();
        List<HousingChoice.Demand> demands=List.of(new HousingChoice.Demand(host,1,3,1,false,0,false,1000),
                new HousingChoice.Demand(couple,1,2,0,false,0,false,1000),
                new HousingChoice.Demand(compact,2,2,0,false,2,false,1000),
                new HousingChoice.Demand(single.key(),5,1,0,false,single.tier().ordinal(),single.stage()==AdultStage.YOUNG,0),
                new HousingChoice.Demand(guest,1,6,4,false,2,false,0));
        double before=0;for(FamilyStructure shape:FamilyStructure.values())before+=g.getFamilies().totalOf(shape)*shape.size();
        var market=g.getSectors().realEstate().housingMarket();var result=market.clear(g,demands);
        double after=0;for(FamilyStructure shape:FamilyStructure.values())after+=g.getFamilies().totalOf(shape)*shape.size();
        near("host allocation preserves all actual family members",after,before);
        near("four funded hosts occupy exactly four real doors",market.occupied(),4);
        check("actual single adults really form emergency sharing groups",market.sharingGroups()>0);
        near("compatible family host space is not consumed by adult soft preferences",result.unplaced(),0);
        near("the child guest obtains its whole compatible host slot",result.leases().stream().filter(l->l.cell().equals(guest)).mapToDouble(HousingChoice.Lease::households).sum(),1);
        check("all emergency guests fit the actual two-household door limit",result.leases().stream().mapToDouble(HousingChoice.Lease::households).sum()<=8+FamilyModel.packingRounding(8));
        check("and every door within its census line",withinLine(market,result));
        near("emergency hosts collect only the four funded lease bills",market.receipts(),result.leases().stream().filter(l->l.cell().equals(host)||l.cell().equals(couple)||l.cell().equals(compact)).mapToDouble(HousingChoice.Lease::bill).sum());
    }
    /** Every class's occupied doors hold at most their census line of people (HousingChoice.line, RES3c). */
    static boolean withinLine(HousingMarket market,HousingChoice.Result result){
        Map<String,Integer> sizes=new HashMap<>();for(var d:market.demands())sizes.put(d.key(),d.size());
        Map<HousingInventory.Key,Double> people=new HashMap<>(),doors=new HashMap<>();
        for(var l:result.leases()){people.merge(l.home(),l.households()*sizes.getOrDefault(l.cell(),Integer.MAX_VALUE/4),Double::sum);doors.merge(l.home(),l.count(),Double::sum);}
        for(var c:market.classes()){var k=c.key();if(!people.containsKey(k))continue;
            var o=market.offers().stream().filter(x->x.key().equals(k)).findFirst().orElse(null);if(o==null)return false;
            if(people.get(k)>o.people()*doors.get(k)+FamilyModel.packingRounding(people.get(k)))return false;}
        return true;
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
        // A paid branch causes a real non-rent bill before the native continuation twin.
        check("native fixed-floor twin funds an actual bank branch",g.buildStack(bm.getTemplateByName("Commercial Bank"),1,false)==Game.BuildResult.SUCCESS);
        Map<String,Double> billedRent=new HashMap<>();
        for(int i=0;i<2;i++){
            billedRent.clear();
            for(Household c:g.getHouseholdBalance().cells())billedRent.put(c.key(),re.housingMarket().rentFor(c.key(),1));
            g.toggleNextMonth();audits(g);
        }
        check("the paid native-twin branch charges its model account fee",g.getBank().accountFee(1)>0);
        double fixedFloor=g.getHouseholdBalance().cells().stream()
                .filter(c->c instanceof WorkingHousehold&&!c.isEmpty()&&c.disposable()>0&&c.accountFee>0)
                .mapToDouble(c->c.disposable()-c.afterFixed()-billedRent.get(c.key())/c.households()).max().orElse(0);
        check("native twin has positive disposable income and an actual non-rent fixed-bill floor",fixedFloor>0);
        out.printf(Locale.ROOT,"   %s native fixed floor %.12g; account fee %.12g%n",label,fixedFloor,g.getBank().accountFee(1));
        String saved=JSON.toJson(re.housingMarket().forSave());
        g.saveGame(10);Game back=new Game(files);back.loadGame(10);
        check("native reload preserves asks, assignments, actual trades and staleness",saved.equals(JSON.toJson(back.getSectors().realEstate().housingMarket().forSave())));
        g.toggleNextMonth();back.toggleNextMonth();audits(g);audits(back);
        check("next native month preserves the class market and housing budgets after positive fixed bills",JSON.toJson(re.housingMarket().forSave()).equals(JSON.toJson(back.getSectors().realEstate().housingMarket().forSave())));
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
    static void retainedFundedGuest(){
        out.println("--- prior guests retain only currently funded host capacity ---");
        Game g=new Game(GameFiles.scratch("res3-retained-funded-guest"));g.run();
        var bm=g.getBuildingManager();var t=bm.getTemplate(0);bm.addStack(t,1,true);
        var market=g.getSectors().realEstate().housingMarket();
        var first=market.clear(g,List.of(new HousingChoice.Demand("old-host",1,2,0,false,0,false,1e6),
                new HousingChoice.Demand("old-guest",1,1,0,true,0,false,0)));
        near("fixture has a genuine already funded guest",first.unplaced(),0);
        market.sold();market.reprice(.01);var previous=market.forSave();previous.month=g.getMonth()-1;
        var key=previous.leases.getFirst().home();double current=market.price(key);
        market.restore(previous);
        var next=market.clear(g,List.of(new HousingChoice.Demand("old-host",1,2,0,false,0,false,1e6),
                new HousingChoice.Demand("old-guest",1,1,0,true,0,false,0),
                new HousingChoice.Demand("new-rich",1,1,0,true,5,false,1e6)));
        near("the existing guest keeps its zero-bill funded arrangement",next.leases().stream().filter(l->l.cell().equals("old-guest")).mapToDouble(HousingChoice.Lease::households).sum(),1);
        near("a rich new guest cannot displace that incumbent",next.leases().stream().filter(l->l.cell().equals("new-rich")).mapToDouble(HousingChoice.Lease::households).sum(),0);
        near("incumbency creates no additional door",market.occupied(),1);
        near("retained rent follows the current class quote once",market.receipts(),current);
        near("the retained guest owes no invented rent",market.rentFor("old-guest",1),0);
        check("all retained arrangements respect two households per door",next.leases().stream().allMatch(l->l.households()<=2*l.count()+FamilyModel.packingRounding(l.households())));
        market.restore(previous);
        next=market.clear(g,List.of(new HousingChoice.Demand("old-guest",1,1,0,true,0,false,0)));
        near("a prior zero charge cannot create an unfunded empty-door right",next.unplaced(),1);
        near("without a funded host no rent is invented",market.receipts(),0);
        near("without a funded host no door is occupied",market.occupied(),0);
    }
    static void stagedHousingStocks(){
        out.println("--- RES3 staged housing stocks follow the same funded census ---");
        var balance=new HouseholdBalance();var families=new FamilyModel();
        var source=balance.cell(FamilyStructure.SINGLE_ADULT,PayTier.UNSKILLED);
        var on=balance.unemployed(UnemployedHousehold.Status.ON_EI);
        var off=balance.unemployed(UnemployedHousehold.Status.OFF_EI);
        source.households=10;source.savings=120;source.debt=12;
        source.revolving=.8;source.lateDebt=3;source.lateShare=.2;source.shutOut=.1;
        source.shares[0]=3;source.basis[0]=2;source.taxShares[0]=1;source.taxClaim[0]=.2;
        source.studentDebt=4;source.paper=8;source.abroad=6;source.putBondFace(17,5);source.recountBonds();
        balance.setOutsideCensus(c->c==on?10:0);
        double[] fresh=new double[balance.cells().size()];int oi=balance.cells().indexOf(on),fi=balance.cells().indexOf(off);
        fresh[oi]=10;
        double[] saved=balance.toCellSaveArray();var bonds=balance.bondsByCellToSave();
        Household projected=balance.housingCensusCells(families,fresh)[oi];
        near("a newly unemployed household carries actual source cash",projected.savings,120);
        near("its existing card balance follows it",projected.debt,12);
        near("HC1 current borrower share follows",projected.revolving,.8);
        near("HC1 late balance follows",projected.lateDebt,3);
        near("HC1 late borrower share follows",projected.lateShare,.2);
        near("HC1 shut-out share follows",projected.shutOut,.1);
        near("security units follow the same stock route",projected.shares[0],3);
        near("security basis follows the same stock route",projected.basis[0],2);
        near("tagged security units follow",projected.taxShares[0],1);
        near("tagged security claim follows",projected.taxClaim[0],.2);
        near("student principal follows",projected.studentDebt,4);
        near("paper follows",projected.paper,8);near("foreign cash follows",projected.abroad,6);
        near("individual bond face follows",projected.bondFace(17),5);
        check("projection leaves every saved live field unchanged",Arrays.equals(saved,balance.toCellSaveArray()));
        check("projection leaves all live bond holdings unchanged",bonds.equals(balance.bondsByCellToSave()));
        var bid=balance.strikeHousingBudgets(families,new HouseholdAccounts(),0,c->0,c->0,.03,1).get(on.key());
        check("real inherited cash can fund a finite housing buffer",bid.budget()>0&&bid.budget()<=120/HousingMarket.SAVINGS_MONTHS);
        check("quoting also leaves every saved live field unchanged",Arrays.equals(saved,balance.toCellSaveArray()));
        fresh[fi]=10;Household[] tied=balance.housingCensusCells(families,fresh);
        near("equal new cells share cash pro rata",tied[oi].savings,tied[fi].savings);
        near("extra people bring no invented opening cash",tied[oi].savings*10+tied[fi].savings*10,source.savings*source.households);
        near("extra people bring no invented card balance",tied[oi].debt*10+tied[fi].debt*10,source.debt*source.households);
        var otherCity=new HouseholdBalance();
        check("a different city's complete stock state restores into the warmed ledger",balance.restoreCells(otherCity.cellKeys(),otherCity.toCellSaveArray(),Equity.COMPANIES));
        balance.restoreBondsByCell(otherCity.bondsByCellToSave());
        near("a warmed quote cannot retain the old city's cash",balance.strikeHousingBudgets(families,new HouseholdAccounts(),0,c->0,c->0,.03,1).get(on.key()).budget(),0);
        double[] restored=balance.toCellSaveArray();
        check("an invalid restore is refused",!balance.restoreCells(balance.cellKeys(),new double[1],Equity.COMPANIES));
        check("a refused restore preserves the live ledger",Arrays.equals(restored,balance.toCellSaveArray()));
        near("a refused restore cannot revive stale quoted resources",balance.strikeHousingBudgets(families,new HouseholdAccounts(),0,c->0,c->0,.03,1).get(on.key()).budget(),0);
        var empty=new HouseholdBalance();var hungry=empty.unemployed(UnemployedHousehold.Status.ON_EI);
        empty.setOutsideCensus(c->c==hungry?10:0);
        var zero=empty.strikeHousingBudgets(new FamilyModel(),new HouseholdAccounts(),0,c->0,c->0,.03,1).get(hungry.key());
        near("no source stocks or received income gives no funded bid",zero.budget(),0);
        var offer=new HousingChoice.Offer(new HousingInventory.Key(900,"no-funding"),10,2,1,30,1,0,false,1,0);
        var result=HousingChoice.clear(List.of(new HousingChoice.Demand(hungry.key(),zero.count(),1,0,false,0,false,zero.budget())),List.of(offer),List.of(),0);
        near("empty unfunded doors create no tenancy",result.leases().size(),0);
        near("the no-funding household stays explicitly unplaced",result.unplaced(),zero.count());
    }

    /** An actually paid non-wage row and its billed source population, before the next census. */
    static HouseholdAccounts paidHousingRow(int row,double people,double income,double fees){
        var accounts=new HouseholdAccounts();
        accounts.setOutsideMoney(0,row==Household.UNEMPLOYED_ROW?income:0,row==Household.STUDENT_ROW?income:0);
        accounts.update(0,0,0,0,0,0,fees,0,0,(long)people,0,0);
        double[] heads=new double[Household.ROWS];heads[row]=people;
        accounts.updateByTier(new double[PayTier.values().length],new double[PayTier.values().length],heads,heads);
        return accounts;
    }
    static void stagedHousingFlows(){
        out.println("--- housing quotes keep opening paid income and recurring fee source weights ---");
        for(boolean student:new boolean[]{false,true}){
            var balances=new HouseholdBalance();var families=new FamilyModel();
            Household cell=student?balances.cells().stream().filter(c->c instanceof StudentHousehold).findFirst().orElseThrow()
                    :balances.unemployed(UnemployedHousehold.Status.ON_EI);
            cell.studentLoansOpen=false;cell.households=10;
            double[] fresh={2};balances.setOutsideCensus(c->c==cell?fresh[0]:0);
            var accounts=paidHousingRow(cell.row(),10,200,0);
            near(cell.key()+" contraction keeps the paid source's per-recipient income",balances.strikeHousingBudgets(families,accounts,0,c->0,c->Double.NaN,.03,1).get(cell.key()).budget(),20);
            fresh[0]=20;
            var expanded=balances.strikeHousingBudgets(families,accounts,0,c->0,c->Double.NaN,.03,1).get(cell.key());
            near(cell.key()+" expansion shares the funded opening pool",expanded.budget(),10);
            check(cell.key()+" expansion never multiplies the paid opening receipt",expanded.budget()*expanded.count()<=accounts.getRowDisposable(cell.row()));
            cell.households=0;
            near(cell.key()+" no source recipients cannot claim a stale row receipt",balances.strikeHousingBudgets(families,accounts,0,c->0,c->Double.NaN,.03,1).get(cell.key()).budget(),0);
            near(cell.key()+" no opening funding invents no current receipt",balances.strikeHousingBudgets(families,paidHousingRow(cell.row(),0,0,0),0,c->0,c->Double.NaN,.03,1).get(cell.key()).budget(),0);
            cell.households=10;accounts=paidHousingRow(cell.row(),10,0,40);fresh[0]=2;
            near(cell.key()+" contraction does not concentrate a recurring per-head fee",balances.strikeHousingBudgets(families,accounts,0,c->0,c->100,.03,1).get(cell.key()).budget(),96);
            fresh[0]=20;
            near(cell.key()+" expansion cannot dilute a recurring per-head fee",balances.strikeHousingBudgets(families,accounts,0,c->0,c->100,.03,1).get(cell.key()).budget(),96);
        }
        var balances=new HouseholdBalance();var families=new FamilyModel();
        var cell=balances.unemployed(UnemployedHousehold.Status.ON_EI);cell.households=10;
        balances.setOutsideCensus(c->c==cell?2:0);
        var missingHeads=new HouseholdAccounts(){
            @Override public double getRowHealthcare(int row){return row==cell.row()?40:0;}
        };
        near("a billed fee without source heads still reserves the existing fresh split",balances.strikeHousingBudgets(families,missingHeads,0,c->0,c->100,.03,1).get(cell.key()).budget(),80);
        cell.disposable=10;cell.afterFixed=5;
        var accounts=paidHousingRow(cell.row(),10,0,0);
        near("detached stocks preserve the live fixed-bill floor and its live rent denominator",balances.strikeHousingBudgets(families,accounts,0,c->c.key().equals(cell.key())?20/c.households():0,c->10,.03,1).get(cell.key()).budget(),7);
        near("quoting changes no live disposable income",cell.disposable,10);
        near("quoting changes no live income after fixed bills",cell.afterFixed,5);
    }
    static void carriedStudentService(){
        out.println("--- carried student principal reserves current contractual service ---");
        Game g=new Game(GameFiles.scratch("res3-carried-student-service"));g.run();
        double[] people=new double[AgeBand.values().length+3];people[AgeBand.ADULT.ordinal()]=100;
        g.getCohorts().restore(PopulationCohorts.saveBands(),people);g.getFamilies().rememberHouseholds(false);
        double[] jobs=new double[PayTier.values().length];jobs[PayTier.UNSKILLED.ordinal()]=100;
        g.getFamilies().rebuild(g.getCohorts(),jobs);
        var balances=new HouseholdBalance();var source=balances.unemployed(UnemployedHousehold.Status.ON_EI);
        source.households=10;source.studentDebt=Household.STUDENT_LOAN_MONTHS;
        var worker=balances.cell(FamilyStructure.SINGLE_ADULT,PayTier.UNSKILLED);
        double rate=TaxPolicy.MAX_STUDENT_LOAN_RATE/2;balances.setStudentLoanRate(rate);
        double[] saved=balances.toCellSaveArray();var accounts=new HouseholdAccounts();
        var quoted=balances.strikeHousingBudgets(g.getFamilies(),accounts,0,c->0,c->100,.03,1);
        double[] fresh=balances.cells().stream().mapToDouble(c->quoted.get(c.key()).count()).toArray();
        Household carried=balances.housingCensusCells(g.getFamilies(),fresh)[balances.cells().indexOf(worker)];
        check("the new working cell really carries principal from the old unemployed source",worker.studentDebt()==0&&carried.studentDebt()>0&&quoted.get(worker.key()).count()>0);
        double principal=carried.studentDebt();
        double service=principal/Household.STUDENT_LOAN_MONTHS+principal*rate/12;
        near("a quote reserves the carried loan's contractual term and current-rate interest",quoted.get(worker.key()).budget(),100-service);
        carried.studentLoanRate=rate;carried.settle(100,0,0,0,0,0);
        near("the quote's reservation agrees with the actual model strike",quoted.get(worker.key()).budget(),carried.afterFixed());
        balances.setStudentLoanRate(TaxPolicy.MAX_STUDENT_LOAN_RATE);
        double dearer=balances.strikeHousingBudgets(g.getFamilies(),accounts,0,c->0,c->100,.03,1).get(worker.key()).budget();
        near("a current rate change is reserved without waiting for settled flow fields",quoted.get(worker.key()).budget()-dearer,principal*(TaxPolicy.MAX_STUDENT_LOAN_RATE-rate)/12);
        check("student service quotes and a detached comparison strike change no live saved field",Arrays.equals(saved,balances.toCellSaveArray()));
        source.studentDebt=0;
        near("a working cell carrying no student principal reserves no student service",balances.strikeHousingBudgets(g.getFamilies(),accounts,0,c->0,c->100,.03,1).get(worker.key()).budget(),100);
        source.studentDebt=Household.STUDENT_LOAN_MONTHS;balances.setOutsideCensus(c->c==source?10:0);
        near("unemployed principal remains in contractual forbearance",balances.strikeHousingBudgets(new FamilyModel(),accounts,0,c->0,c->100,.03,1).get(source.key()).budget(),100);
        var enrolled=new HouseholdBalance();var student=enrolled.students();student.households=10;
        student.studentDebt=Household.STUDENT_LOAN_MONTHS;student.studentLoansOpen=false;
        enrolled.setStudentLoanRate(TaxPolicy.MAX_STUDENT_LOAN_RATE);enrolled.setOutsideCensus(c->c==student?10:0);
        near("an enrolled student's existing principal is not serviced during study",enrolled.strikeHousingBudgets(new FamilyModel(),accounts,0,c->0,c->100,.03,1).get(student.key()).budget(),100);
    }

    static void rentCommitmentBudgets(){
        out.println("--- rent commitment uses surviving recipients; live fixed-bill floor stays separate ---");
        var balances=new HouseholdBalance();var families=new FamilyModel();
        var cell=balances.unemployed(UnemployedHousehold.Status.ON_EI);cell.households=10;
        double[] fresh={2};balances.setOutsideCensus(c->c==cell?fresh[0]:0);
        cell.disposable=10;cell.afterFixed=5;
        var accounts=paidHousingRow(cell.row(),10,0,0);
        java.util.function.ToDoubleFunction<Household> charged=c->c.key().equals(cell.key())&&c.households()>0?20/c.households():0;
        double[] before=balances.toCellSaveArray(),flows=balances.flowsToSave();
        near("the unchanged live non-rent fixed floor leaves seven resources",balances.strikeHousingBudgets(families,accounts,0,charged,c->10,.03,1).get(cell.key()).budget(),7);
        var home=new HousingChoice.Offer(new HousingInventory.Key(900,"rent-source"),10,2,0,40,1,0,false,2,0);
        var actual=HousingChoice.clear(List.of(new HousingChoice.Demand(cell.key(),2,1,0,false,0,false,7)),List.of(home),List.of(new HousingChoice.Lease(cell.key(),home.key(),10,20,10,new HousingChoice.Funding(10,0,0))),8,true);
        double actualHH=actual.leases().stream().mapToDouble(HousingChoice.Lease::households).sum();
        double actualBill=actual.leases().stream().mapToDouble(HousingChoice.Lease::bill).sum();
        near("actual retention scales ten old households to the two current recipients",actualHH,2);
        near("the surviving actual rent bill is four, not the stale twenty",actualBill,4);
        double surviving=actualBill/actualHH;
        near("actual current rent commitment is two per household",surviving,2);
        double contraction=balances.strikeHousingBudgets(families,accounts,0,charged,c->10,.03,.5).get(cell.key()).budget();
        near("contraction protects only the surviving actual commitment from saving",contraction,surviving+.5*(7-surviving));
        fresh[0]=20;
        near("expansion spreads the original bill over twenty recipients",balances.strikeHousingBudgets(families,accounts,0,charged,c->10,.03,.5).get(cell.key()).budget(),4);
        check("both quotes preserve all live saved stocks and flows",Arrays.equals(before,balances.toCellSaveArray())&&Arrays.equals(flows,balances.flowsToSave()));
        near("both quotes preserve live household recipients",cell.households(),10);
        near("both quotes preserve live disposable",cell.disposable(),10);
        near("both quotes preserve live fixed-bill balance",cell.afterFixed(),5);
        cell.households=0;cell.disposable=0;cell.afterFixed=0;fresh[0]=2;
        double noSource=balances.strikeHousingBudgets(families,paidHousingRow(cell.row(),0,0,0),0,charged,c->10,.03,.5).get(cell.key()).budget();
        near("no live source recipients inherit stale bill protection",noSource,5);
    }


    static void independentDoorPressure(){
        out.println("--- current paid occupancy and funded independent-door pressure both survive forecasting ---");
        for(double budget:new double[]{12,20})for(boolean hasRich:new boolean[]{false,true})for(int stock:new int[]{1,2,3}){
            out.println("--- independent-door case budget="+budget+", rich newcomer="+hasRich+", existing stock="+stock+" ---");
            Game g=new Game(GameFiles.scratch("res3-forecast-retained-"+(int)budget+(hasRich?"-rich":"-alone")+"-stock"+stock));g.run();
            var bm=g.getBuildingManager();var t=bm.getTemplate(0);bm.addStack(t,stock,true);
            var market=g.getSectors().realEstate().housingMarket();market.clear(g,List.of());
            var key=new HousingInventory.Key(t.getId(),t.getHousingRecipe().mix().getFirst().id());
            var a=new HousingChoice.Demand("old-a",1,1,0,false,0,false,budget);
            var b=new HousingChoice.Demand("old-b",1,1,0,false,0,false,budget);
            var rich=new HousingChoice.Demand("new-rich",1,1,0,false,5,false,100);
            var state=market.forSave();var home=state.classes.stream().filter(c->c.key().equals(key)).findFirst().orElseThrow();
            near("fixture one actual recipe door at budget "+budget,t.getDwellings(),1);
            near("fixture actual RE inventory stock at budget "+budget,bm.getHousingInventory().owned(key,HousingInventory.Owner.REAL_ESTATE),stock);
            near("fixture has no endogenous source households at budget "+budget,g.getCohorts().total(),0);
            home.ask=home.lastTrade=20;home.tradeQuantity=1;home.tradeValue=20;home.occupied=1;home.demand=2;
            state.month=g.getMonth()-1;state.demand=List.of(a,b);
            state.leases=List.of(new HousingChoice.Lease(a.key(),key,.5,10,1,new HousingChoice.Funding(0,1,0)),new HousingChoice.Lease(b.key(),key,.5,10,1,new HousingChoice.Funding(0,1,0)));
            state.kept=1;state.crowded=1;state.suitable=state.unaffordable=state.noSuitable=state.unplaced=0;
            market.restore(state);
            var actual=market.clear(g,hasRich?List.of(a,b,rich):List.of(a,b));
            double oldHH=actual.leases().stream().filter(l->l.cell().startsWith("old-")).mapToDouble(HousingChoice.Lease::households).sum();
            double richHH=actual.leases().stream().filter(l->l.cell().equals(rich.key())).mapToDouble(HousingChoice.Lease::households).sum();
            near("actual paid halves retain both old households at budget "+budget,oldHH,2);
            double richPlaced=hasRich&&stock>1?1:0;
            double completed=budget>=20?Math.min(1,Math.max(0,stock-1-richPlaced)):0;
            near("actual newcomer uses only a genuinely vacant door at budget "+budget,richHH,richPlaced);
            near("actual occupied footprint uses current paid doors at budget "+budget,market.occupied(),1+richPlaced+completed);
            near("actual newcomer shortage matches spare stock at budget "+budget,actual.unplaced(),(hasRich?1:0)-richPlaced);
            near("actual total household source closes at budget "+budget,oldHH+richHH+actual.unplaced(),hasRich?3:2);
            near("actual rent follows occupied asks at budget "+budget,market.receipts(),20*(1+richPlaced+completed));
            near("only affordable paid crowding is completed after movers",actual.crowded(),1-completed);
            near("paid incumbent bills follow the completed physical footprint",actual.leases().stream().filter(l->l.cell().startsWith("old-")).mapToDouble(HousingChoice.Lease::bill).sum(),20*(1+completed));
            near("fixture retains ask twenty at budget "+budget,market.price(key),20);
            String before=JSON.toJson(market.forSave());var forecast=market.forecast(g);
            int expected=Math.max(0,(budget>=20?2+(hasRich?1:0):1+(hasRich?1:0))-stock);
            double income=forecast.income(t,Math.max(1,expected));int needed=forecast.quantityNeeded(t);
            near("forecast preserves funded newcomer and independent-door backlog at budget "+budget,needed,expected);
            near("forecast new projects collect only supported full-door income at budget "+budget,income,20*expected);
            check("forecast moves no actual housing state at budget "+budget,before.equals(JSON.toJson(market.forSave())));
        }
    }

    static void committedDoorBoundaries(){
        out.println("--- unfinished committed homes and incompatible spare stock ---");
        for(boolean pipeline:new boolean[]{true,false}){
            Game g=new Game(GameFiles.scratch("res3-forecast-committed-"+pipeline),Founding.defaults().withUnfitted(false));g.run();
            var bm=g.getBuildingManager();var house=bm.getTemplate(0);var studio=bm.getTemplate(1);
            bm.addStack(house,1,true);
            if(pipeline){bm.addStack(house,1,false);bm.getStack(house).advanceConstruction(house.getConstructionPoints()*.5);}
            else bm.addStack(studio,1,true);
            var inventory=bm.getHousingInventory();var key=new HousingInventory.Key(house.getId(),house.getHousingRecipe().mix().getFirst().id());
            near("committed fixture has one finished family door",inventory.owned(key,HousingInventory.Owner.REAL_ESTATE),1);
            near("committed fixture excludes endogenous arrivals",g.getCohorts().total(),0);
            if(pipeline){
                near("supported stack input is still actually under construction",bm.getStack(house).getUnderConstruction(),1);
                near("unfinished registry contains the one committed door",inventory.pipelineHomes(),1);
            }else{
                check("spare studio is known and every dwelling excludes children",bm.isTemplateKnown(studio)&&studio.getHousingRecipe().mix().stream().allMatch(d->d.capacity()<=FamilyModel.STUDIO_MAX_SIZE));
                check("incompatible spare is actual finished RE stock",studio.getHousingRecipe().mix().stream().mapToDouble(d->inventory.owned(new HousingInventory.Key(studio.getId(),d.id()),HousingInventory.Owner.REAL_ESTATE)).sum()>0);
            }
            var market=g.getSectors().realEstate().housingMarket();market.clear(g,List.of());
            var a=new HousingChoice.Demand("old-family-a",1,4,2,false,0,false,20);
            var b=new HousingChoice.Demand("old-family-b",1,4,2,false,0,false,20);
            var state=market.forSave();var home=state.classes.stream().filter(c->c.key().equals(key)).findFirst().orElseThrow();
            home.ask=home.lastTrade=20;home.tradeQuantity=1;home.tradeValue=20;home.occupied=1;home.demand=2;
            state.month=g.getMonth()-1;state.demand=List.of(a,b);state.leases=List.of(new HousingChoice.Lease(a.key(),key,.5,10,1,new HousingChoice.Funding(0,1,0)),new HousingChoice.Lease(b.key(),key,.5,10,1,new HousingChoice.Funding(0,1,0)));
            state.kept=1;state.crowded=1;state.suitable=state.unaffordable=state.noSuitable=state.unplaced=0;market.restore(state);
            var actual=market.clear(g,List.of(a,b));
            near("committed forecast keeps both paid actual families",actual.leases().stream().mapToDouble(HousingChoice.Lease::households).sum(),2);
            near("neither pending nor incompatible stock creates current occupied doors",market.occupied(),1);
            near("neither pending nor incompatible stock creates current rent",market.receipts(),20);
            near("both actual families remain sheltered",actual.unplaced(),0);
            String housingBefore=JSON.toJson(market.forSave()),inventoryBefore=JSON.toJson(inventory.forSave());
            var forecast=market.forecast(g);int expected=pipeline?0:1;
            near("only suitable affordable committed doors cover independent pressure, pipeline="+pipeline,forecast.quantityNeeded(house),expected);
            near("covered pipeline cannot promise a duplicate new funded order, pipeline="+pipeline,forecast.income(house,1),20*expected);
            if(!pipeline){near("child families promise no studio project income",forecast.income(studio,1),0);near("child families require no incompatible studio project",forecast.quantityNeeded(studio),0);}
            check("forecast leaves actual housing and construction inventory unchanged",housingBefore.equals(JSON.toJson(market.forSave()))&&inventoryBefore.equals(JSON.toJson(inventory.forSave())));
            if(pipeline)near("forecast never advances the committed work",bm.getStack(house).getUnderConstruction(),1);
        }
    }

    static HousingInventory.Key lateHome(){return new HousingInventory.Key(0,HousingForms.of(0).recipe().mix().getFirst().id());}
    static HousingChoice.Demand lateDemand(String key,double count,double budget){return new HousingChoice.Demand(key,count,1,0,false,0,false,budget);}
    static HousingMarket lateFixture(String label,int stock,List<HousingChoice.Demand> current,List<HousingChoice.Demand> previous,List<HousingChoice.Lease> prior){
        Game g=new Game(GameFiles.scratch("res3-late-"+label));g.run();var bm=g.getBuildingManager();bm.addStack(bm.getTemplate(0),stock,true);
        var market=g.getSectors().realEstate().housingMarket();market.clear(g,List.of());var key=lateHome();
        var state=market.forSave();var home=state.classes.stream().filter(c->c.key().equals(key)).findFirst().orElseThrow();
        double doors=prior.stream().mapToDouble(HousingChoice.Lease::count).sum(),hh=prior.stream().mapToDouble(HousingChoice.Lease::households).sum();
        home.ask=home.lastTrade=20;home.tradeQuantity=doors;home.tradeValue=prior.stream().mapToDouble(HousingChoice.Lease::bill).sum();home.occupied=doors;home.demand=Math.max(doors,previous.stream().mapToDouble(HousingChoice.Demand::count).sum());
        state.month=g.getMonth()-1;state.demand=previous;state.leases=prior;state.kept=doors;state.crowded=hh-doors;state.suitable=state.unaffordable=0;
        state.unplaced=state.noSuitable=Math.max(0,previous.stream().mapToDouble(HousingChoice.Demand::count).sum()-hh);
        state.hosted=prior.stream().filter(l->l.bill()==0).mapToDouble(HousingChoice.Lease::households).sum();market.restore(state);
        var actual=market.clear(g,current);var saved=market.forSave();
        near(label+" source closes",actual.leases().stream().mapToDouble(HousingChoice.Lease::households).sum()+actual.unplaced(),current.stream().mapToDouble(HousingChoice.Demand::count).sum());
        check(label+" current stock and lease household caps",market.occupied()<=stock&&actual.leases().stream().allMatch(l->l.count()<=l.households()&&l.households()<=2*l.count()+FamilyModel.packingRounding(l.households())));
        for(var c:market.classes())if(c.occupied>0)near(label+" class bill per physical door "+c.key(),actual.leases().stream().filter(l->l.home().equals(c.key())).mapToDouble(HousingChoice.Lease::bill).sum(),c.occupied*c.ask);
        for(var d:current){double assigned=actual.leases().stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum(),bill=actual.leases().stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::bill).sum();
            check(label+" cell source and budget "+d.key(),assigned<=d.count()+FamilyModel.packingRounding(d.count())&&bill<=d.budget()*assigned+MoneyAudit.tolerance(bill));
            check(label+" funded doors do not exceed household source "+d.key(),bill/20<=assigned+FamilyModel.packingRounding(assigned));}
        check(label+" hosted provenance remains within crowding",saved.hosted<=saved.crowded+FamilyModel.packingRounding(saved.crowded));
        out.println("late-footprint "+label+" occupied="+market.occupied()+" bills="+market.receipts()+" crowded="+saved.crowded+" hosted="+saved.hosted+" unplaced="+saved.unplaced+" leases="+market.leases());
        return market;
    }
    static void latePaidFootprints(){
        out.println("--- late paid footprint completion preserves movers, host funding and equivalent shares ---");
        var key=lateHome();var a=lateDemand("old-a",1,20);var b=lateDemand("old-b",1,20);
        List<HousingChoice.Demand> current=new ArrayList<>();List<HousingChoice.Lease> prior=new ArrayList<>();
        for(int i=0;i<4;i++){var d=lateDemand("equivalent-"+i,1,20);current.add(d);prior.add(new HousingChoice.Lease(d.key(),key,.5,10,1,new HousingChoice.Funding(0,1,0)));}
        var rationed=lateFixture("rationed",3,current,current,prior);
        for(var l:rationed.leases()){near("equivalent incumbent receives pro-rata physical completion",l.count(),.75);near("equivalent incumbent pays the identical physical delta",l.bill(),15);near("equivalent household source unchanged",l.households(),1);}
        near("rationed completion occupies all three current doors",rationed.occupied(),3);near("rationed completion leaves one crowded equivalent",rationed.forSave().crowded,1);
        Collections.reverse(current);Collections.reverse(prior);var reverse=lateFixture("rationed-reverse",3,current,current,prior);
        check("equivalent completion does not confer input-order priority",JSON.toJson(rationed.leases()).equals(JSON.toJson(reverse.leases())));
        var host=lateDemand("full-host",1,100);var guest=lateDemand("zero-guest",1,0);
        var hosted=lateFixture("fully-funded-host",2,List.of(host,guest),List.of(host,guest),List.of(new HousingChoice.Lease(host.key(),key,.5,20,1,new HousingChoice.Funding(1,0,0)),new HousingChoice.Lease(guest.key(),key,.5,0,1,new HousingChoice.Funding(0,0,1))));
        near("full-funded host keeps its one bill rather than paying twice",hosted.rentFor(host.key(),1),20);near("hosted zero-bill guest stays free",hosted.rentFor(guest.key(),1),0);
        near("full-funded host and guest retain one actual door",hosted.occupied(),1);near("full-funded hosted provenance unchanged",hosted.forSave().hosted,1);near("full-funded guest is not evicted",hosted.forSave().unplaced,0);
        var mixed=lateDemand("mixed-paid",2,20);
        for(int stock:new int[]{2,3}){
            var m=lateFixture("mixed-host-"+stock,stock,List.of(mixed,guest),List.of(mixed),List.of(new HousingChoice.Lease(mixed.key(),key,1.5,30,2,new HousingChoice.Funding(1,1,0))));
            near("mixed host completes only its funded deficit",m.leases().stream().filter(l->l.cell().equals(mixed.key())).mapToDouble(HousingChoice.Lease::count).sum(),1.5);
            near("mixed host bill funds exactly two households",m.rentFor(mixed.key(),1),40);near("mixed paid household count unchanged",m.leases().stream().filter(l->l.cell().equals(mixed.key())).mapToDouble(HousingChoice.Lease::households).sum(),2);
            near("mixed hosted guest physical share unchanged",m.leases().stream().filter(l->l.cell().equals(guest.key())).mapToDouble(HousingChoice.Lease::count).sum(),.5);near("mixed guest bill stays zero",m.rentFor(guest.key(),1),0);
            near("mixed hosted counter unchanged",m.forSave().hosted,1);near("mixed paid crowding becomes only the hosted guest",m.forSave().crowded,1);near("mixed footprint never expands past funding",m.occupied(),2);
        }
        var zeros=lateDemand("late-zero-guests",2,0);var demand=List.of(a,b,zeros);
        var retained=lateFixture("new-host-room-retained",2,demand,List.of(a,b),List.of(new HousingChoice.Lease(a.key(),key,.5,10,1,new HousingChoice.Funding(0,1,0)),new HousingChoice.Lease(b.key(),key,.5,10,1,new HousingChoice.Funding(0,1,0))));
        var normal=lateFixture("new-host-room-normal-control",2,demand,List.of(),List.of());
        double retainedHH=retained.leases().stream().mapToDouble(HousingChoice.Lease::households).sum(),normalHH=normal.leases().stream().mapToDouble(HousingChoice.Lease::households).sum();
        near("native normal control proves four households can be housed",normalHH,4);near("native normal control has no unplaced free guests",normal.forSave().unplaced,0);
        out.println("late-host-room witness retainedHH="+retainedHH+" normalHH="+normalHH+" retainedUnplaced="+retained.forSave().unplaced+" normalUnplaced="+normal.forSave().unplaced+" retainedHosted="+retained.forSave().hosted+" normalHosted="+normal.forSave().hosted);
        check("late completion does not strand guests feasible in the normal control",retainedHH>=normalHH-FamilyModel.packingRounding(normalHH));
        for(var payer:List.of(a,b))check("new host placement preserves exact payer households and bill",retained.leases().stream().filter(l->l.cell().equals(payer.key())).mapToDouble(HousingChoice.Lease::households).sum()==1&&retained.rentFor(payer.key(),1)==20);
        check("new zero-budget guests are assigned exactly once",retained.leases().stream().filter(l->l.cell().equals(zeros.key())).mapToDouble(HousingChoice.Lease::households).sum()==2&&retained.rentFor(zeros.key(),1)==0);
        near("newly funded host provenance counts the two new guests",retained.forSave().hosted,2);near("newly funded host crowding counts only the two guests",retained.forSave().crowded,2);
        var oldHost=lateDemand("old-full-host",1,100);var oldZero=lateDemand("old-zero-guest",1,0);var newZero=lateDemand("new-zero-guests",2,0);
        var previous=List.of(oldHost,oldZero,a,b);var all=List.of(oldHost,oldZero,a,b,newZero);
        var oldAndNew=lateFixture("old-and-new-zero-guests",3,all,previous,List.of(new HousingChoice.Lease(oldHost.key(),key,.5,20,1,new HousingChoice.Funding(1,0,0)),new HousingChoice.Lease(oldZero.key(),key,.5,0,1,new HousingChoice.Funding(0,0,1)),new HousingChoice.Lease(a.key(),key,.5,10,1,new HousingChoice.Funding(0,1,0)),new HousingChoice.Lease(b.key(),key,.5,10,1,new HousingChoice.Funding(0,1,0))));
        for(var payer:List.of(oldHost,a,b))check("old/new guest follow-up preserves exact payer households and bill "+payer.key(),oldAndNew.leases().stream().filter(l->l.cell().equals(payer.key())).mapToDouble(HousingChoice.Lease::households).sum()==1&&oldAndNew.rentFor(payer.key(),1)==20);
        check("already hosted zero guest is not assigned a second time",oldAndNew.leases().stream().filter(l->l.cell().equals(oldZero.key())).mapToDouble(HousingChoice.Lease::households).sum()==1&&oldAndNew.rentFor(oldZero.key(),1)==0);
        check("new zero guests receive exactly the remaining source quantity",oldAndNew.leases().stream().filter(l->l.cell().equals(newZero.key())).mapToDouble(HousingChoice.Lease::households).sum()==2&&oldAndNew.rentFor(newZero.key(),1)==0);
        near("old and new zero guests preserve all six household units",oldAndNew.leases().stream().mapToDouble(HousingChoice.Lease::households).sum(),6);
        near("old/new zero guests leave no feasible source unplaced",oldAndNew.forSave().unplaced,0);near("old/new zero guests use three funded physical doors",oldAndNew.occupied(),3);near("old/new zero guest total bills stay three asks",oldAndNew.receipts(),60);
        near("hosted provenance contains one old plus two new guests",oldAndNew.forSave().hosted,3);near("crowding provenance contains only those three guests",oldAndNew.forSave().crowded,3);

    }


    private record FundingFixture(Game game,HousingMarket market,BuildingsTemplate house,BuildingsTemplate studio,HousingInventory.Key home) { }
    static HousingChoice.Demand fundingDemand(String key,double count,boolean retired,double budget){
        return new HousingChoice.Demand(key,count,retired?1:2,0,retired,0,false,budget);
    }
    static FundingFixture fundingFixture(String label,List<HousingChoice.Demand> demand,boolean pair,boolean cheap){
        Game g=new Game(GameFiles.scratch("res3-funded-pressure-"+label),Founding.defaults().withUnfitted(false));g.run();
        var bm=g.getBuildingManager();var house=bm.getTemplate(0);var studio=bm.getTemplate(1);bm.addStack(house,1,true);
        var m=g.getSectors().realEstate().housingMarket();m.clear(g,List.of());
        var home=new HousingInventory.Key(house.getId(),house.getHousingRecipe().mix().getFirst().id());
        check(label+" fixture is one actual owned recipe door",house.getDwellings()==1&&bm.getHousingInventory().owned(home,HousingInventory.Owner.REAL_ESTATE)==1);
        check(label+" fixture excludes endogenous source households",g.getCohorts().total()==0);
        check(label+" fixture cheap alternative is currently known",bm.isTemplateKnown(studio));
        var state=m.forSave();var a=state.classes.stream().filter(c->c.key().equals(home)).findFirst().orElseThrow();
        a.ask=a.lastTrade=20;a.occupied=a.tradeQuantity=1;a.tradeValue=20;a.demand=2;
        if(cheap)for(var c:state.classes)if(c.recipe==studio.getId())c.ask=c.lastTrade=10;
        state.month=g.getMonth()-1;state.demand=List.copyOf(demand);state.sharing=List.of();
        if(pair&&demand.size()==1)state.leases=List.of(new HousingChoice.Lease(demand.getFirst().key(),home,1,20,2,new HousingChoice.Funding(0,2,0)));
        else if(pair)state.leases=List.of(new HousingChoice.Lease(demand.get(0).key(),home,.5,10,1,new HousingChoice.Funding(0,1,0)),new HousingChoice.Lease(demand.get(1).key(),home,.5,10,1,new HousingChoice.Funding(0,1,0)));
        else state.leases=List.of(new HousingChoice.Lease(demand.get(0).key(),home,.5,20,1,new HousingChoice.Funding(1,0,0)),new HousingChoice.Lease(demand.get(1).key(),home,.5,0,1,new HousingChoice.Funding(0,0,1)));
        state.kept=1;state.crowded=1;state.hosted=pair?0:1;state.suitable=state.unaffordable=state.noSuitable=state.unplaced=0;
        m.restore(state);var result=m.clear(g,demand);
        near(label+" fixture keeps ask twenty",m.price(home),20);
        near(label+" actual household source closes",result.leases().stream().mapToDouble(HousingChoice.Lease::households).sum()+result.unplaced(),2);
        near(label+" actual occupied door",m.occupied(),1);near(label+" actual total bill",m.receipts(),20);
        for(var d:demand){double hh=result.leases().stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
            check(label+" current source can fund its bill "+d.key(),m.rentFor(d.key(),1)<=d.budget()*hh+MoneyAudit.tolerance(d.budget()*hh));}
        return new FundingFixture(g,m,house,studio,home);
    }
    static void fundedPressureAndForecast(){
        out.println("--- financial claims and physical guest donations do not create a second funded bid ---");
        for(double guestBudget:new double[]{0,20}){
            String label="host-guest-"+(int)guestBudget;
            var f=fundingFixture(label,List.of(fundingDemand("host",1,false,20),fundingDemand("guest",1,true,guestBudget)),false,false);
            var m=f.market();String before=JSON.toJson(m.forSave()),inventory=JSON.toJson(f.game().getBuildingManager().getHousingInventory().forSave());
            near(label+" host keeps its one full bill",m.rentFor("host",1),20);near(label+" guest remains free",m.rentFor("guest",1),0);
            near(label+" funded class price pressure",m.classes().stream().filter(c->c.key().equals(f.home())).findFirst().orElseThrow().demand,guestBudget==0?1:2);
            near(label+" new full-door forecast",m.forecast(f.game()).income(f.house(),1),guestBudget);
            check(label+" forecast preserves actual leases and stock",before.equals(JSON.toJson(m.forSave()))&&inventory.equals(JSON.toJson(f.game().getBuildingManager().getHousingInventory().forSave())));
        }
        var paid=fundingFixture("paid-pair",List.of(fundingDemand("paid-a",1,false,20),fundingDemand("paid-b",1,false,20)),true,false);
        near("paid pair keeps its two funded normal bids",paid.market().classes().stream().filter(c->c.key().equals(paid.home())).findFirst().orElseThrow().demand,2);
        near("paid pair can fund another full door",paid.market().forecast(paid.game()).income(paid.house(),1),20);
        var cheap=fundingFixture("cheap-alternative",List.of(fundingDemand("old-pair",2,false,12)),true,true);var m=cheap.market();
        var bm=cheap.game().getBuildingManager();
        near("cheap alternative has no finished current stock",bm.getHousingInventory().stock().stream().filter(s->s.key().recipe()==cheap.studio().getId()).mapToDouble(s->bm.getHousingInventory().owned(s.key(),HousingInventory.Owner.REAL_ESTATE)).sum(),0);
        for(var dw:cheap.studio().getHousingRecipe().mix())near("cheap alternative is quoted at ten "+dw.id(),m.price(new HousingInventory.Key(cheap.studio().getId(),dw.id())),10);
        String before=JSON.toJson(m.forSave()),inventory=JSON.toJson(bm.getHousingInventory().forSave());
        double income=m.forecast(cheap.game()).income(cheap.studio(),1),oldBill=m.receipts(),remaining=2*12-oldBill;
        check("new alternative income fits resources remaining after retained claims",income<=remaining+MoneyAudit.tolerance(remaining));
        check("implied remaining incumbents can still pay the retained bill",oldBill<=12*(2-income/10)+MoneyAudit.tolerance(oldBill));
        check("alternative quote preserves actual leases and stock",before.equals(JSON.toJson(m.forSave()))&&inventory.equals(JSON.toJson(bm.getHousingInventory().forSave())));
    }

    static void coalescedSponsored(){
        out.println("--- actual sponsored groups survive coalescence with newly funded sharing groups ---");
        for(int stock:new int[]{2,3}){
            Game g=new Game(GameFiles.scratch("res3-coalesced-sponsored-"+stock),Founding.defaults().withUnfitted(false));g.run();
            double[] people=new double[AgeBand.values().length+3];people[AgeBand.ADULT.ordinal()]=100;
            g.getCohorts().restore(PopulationCohorts.saveBands(),people);g.getFamilies().rememberHouseholds(false);
            double[] jobs=new double[PayTier.values().length];jobs[PayTier.UNSKILLED.ordinal()]=100;g.getFamilies().rebuild(g.getCohorts(),jobs);
            var single=g.getHouseholdBalance().cells().stream().filter(c->c instanceof WorkingHousehold w&&w.shape()==FamilyStructure.SINGLE_ADULT&&g.getFamilies().get(w.shape(),w.tier(),w.stage())>=6).map(c->(WorkingHousehold)c).findFirst().orElseThrow();
            String shared=g.getHouseholdBalance().cell(FamilyStructure.SHARED_ADULTS,single.tier(),single.stage()).key();
            var bm=g.getBuildingManager();bm.addStack(bm.getTemplate(0),stock,true);bm.addStack(bm.getTemplate(156),1,true);
            if(stock==2)bm.addStack(bm.getTemplate(157),1,true);
            var market=g.getSectors().realEstate().housingMarket();market.clear(g,List.of());var state=market.forSave();var key=lateHome();
            for(var c:state.classes){c.ask=c.lastTrade=c.recipe==156?8:20;if(c.recipe==0){c.occupied=1;c.demand=2;c.tradeQuantity=1;c.tradeValue=20;}}
            var host=lateDemand("funded-donor",1,20);var old=new HousingChoice.Demand(shared,1,5,0,false,single.tier().ordinal(),single.stage()==AdultStage.YOUNG,0);
            var singles=new HousingChoice.Demand(single.key(),6,1,0,false,single.tier().ordinal(),single.stage()==AdultStage.YOUNG,8);
            state.month=g.getMonth()-1;state.demand=List.of(host,old);state.leases=List.of(new HousingChoice.Lease(host.key(),key,.5,20,1,new HousingChoice.Funding(1,0,0)),new HousingChoice.Lease(shared,key,.5,0,1,new HousingChoice.Funding(0,0,1)));
            state.kept=1;state.crowded=state.hosted=1;state.suitable=state.unaffordable=state.noSuitable=state.unplaced=0;market.restore(state);
            double[] families=g.getFamilies().toSaveArray();
            try{
                var r=market.clear(g,List.of(host,old,singles));
                near("coalesced "+stock+" native formation causes exactly one paid group",market.sharingGroups(),1);
                near("coalesced "+stock+" sponsored household remains hosted",market.hosted(),1);
                near("coalesced "+stock+" sponsored crowding is preserved",r.crowded(),1);
                near("coalesced "+stock+" original free guest has no new charge",market.rentFor(shared,1),20);
                near("coalesced "+stock+" formed and original groups both remain",r.leases().stream().filter(l->l.cell().equals(shared)).mapToDouble(HousingChoice.Lease::households).sum(),2);
                check("coalesced "+stock+" fixture really transfers family census",!Arrays.equals(families,g.getFamilies().toSaveArray()));
            }catch(IllegalStateException e){check("coalesced "+stock+" preserves sponsored provenance without throwing: "+e.getMessage(),false);}
        }
    }
    static void sharingForecastSources(){
        out.println("--- forecast current contracts and prospective worker cohorts close separate sources ---");
        Game g=new Game(GameFiles.scratch("res3-sharing-forecast-sources"),Founding.defaults().withUnfitted(false));g.run();
        double[] people=new double[AgeBand.values().length+3];people[AgeBand.ADULT.ordinal()]=100;
        g.getCohorts().restore(PopulationCohorts.saveBands(),people);g.getFamilies().rememberHouseholds(false);
        double[] jobs=new double[PayTier.values().length];jobs[PayTier.UNSKILLED.ordinal()]=100;g.getFamilies().rebuild(g.getCohorts(),jobs);
        var single=g.getHouseholdBalance().cells().stream().filter(c->c instanceof WorkingHousehold w&&w.shape()==FamilyStructure.SINGLE_ADULT&&g.getFamilies().get(w.shape(),w.tier(),w.stage())>=6).map(c->(WorkingHousehold)c).findFirst().orElseThrow();
        String shared=g.getHouseholdBalance().cell(FamilyStructure.SHARED_ADULTS,single.tier(),single.stage()).key();
        var bm=g.getBuildingManager();bm.addStack(bm.getTemplate(0),1,true);bm.addStack(bm.getTemplate(156),1,true);
        var m=g.getSectors().realEstate().housingMarket();m.clear(g,List.of());var opening=m.forSave();
        for(var c:opening.classes)c.ask=c.lastTrade=c.recipe==0?20:8;
        opening.month=g.getMonth()-1;m.restore(opening);
        for(int press=0;press<2;press++){
            if(press>0)g.getFamilies().rebuild(g.getCohorts(),jobs);
            double oldSingles=g.getFamilies().get(single.shape(),single.tier(),single.stage()),formed=g.getFamilies().getFormed(single.shape(),single.tier(),single.stage());
            var prior=m.forSave();prior.month=g.getMonth()-1;m.restore(prior);
            var r=m.clear(g,List.of(new HousingChoice.Demand(single.key(),6,1,0,false,single.tier().ordinal(),single.stage()==AdultStage.YOUNG,8)));
            near("press "+press+" actual five-to-one transfer occurs",m.sharingGroups(),1);
            near("press "+press+" family census loses only the five actual source singles",g.getFamilies().get(single.shape(),single.tier(),single.stage()),oldSingles-5);
            near("press "+press+" prospective mix remains the pre-valve formation record",g.getFamilies().getFormed(single.shape(),single.tier(),single.stage()),formed);
            near("press "+press+" current source singleton count",m.demands().stream().filter(d->d.key().equals(single.key())).mapToDouble(HousingChoice.Demand::count).sum(),1);
            near("press "+press+" current target group count",m.demands().stream().filter(d->d.key().equals(shared)).mapToDouble(HousingChoice.Demand::count).sum(),1);
            near("press "+press+" current demand conserves original six heads",m.demands().stream().mapToDouble(d->d.count()*d.size()).sum(),6);
            near("press "+press+" current budgets conserve original resources",m.demands().stream().mapToDouble(d->d.count()*d.budget()).sum(),48);
            near("press "+press+" actual contracts fund both current households",r.leases().stream().mapToDouble(HousingChoice.Lease::households).sum(),2);
            near("press "+press+" old committed contracts keep both bills",m.receipts(),28);
            String before=JSON.toJson(m.forSave());double[] familyBefore=g.getFamilies().toSaveArray();
            var forecast=m.forecast(g);var candidate=bm.getTemplate(157);
            near("press "+press+" restoring five old singles cannot spend the retained group bill again",forecast.income(candidate,100),0);
            near("press "+press+" current full-price contracts create no duplicate project request",forecast.quantityNeeded(candidate),0);
            check("press "+press+" forecast changes neither actual census nor contracts",Arrays.equals(familyBefore,g.getFamilies().toSaveArray())&&before.equals(JSON.toJson(m.forSave())));
        }
        double currentHeads=m.demands().stream().mapToDouble(d->d.count()*d.size()).sum();String before=JSON.toJson(m.forSave());
        var candidate=bm.getTemplate(157);for(var d:candidate.getHousingRecipe().mix())m.setAsk(new HousingInventory.Key(candidate.getId(),d.id()),0);
        near("no funded future job cohort requests another free candidate",m.forecast(g).quantityNeeded(candidate),0);
        bm.addStack(bm.getTemplateByName("Construction Depot"),100,false);
        check("prospective worker cause is actual jobs under construction",bm.getJobsUnderConstruction()>100);
        var projected=m.forecast(g);check("actual future jobs create a positive separately projected worker cohort",projected.quantityNeeded(candidate)>0);
        near("projected arriving workers do not inflate the current household source",m.demands().stream().mapToDouble(d->d.count()*d.size()).sum(),currentHeads);
        // Quote changes are intentional fixture inputs; forecasting itself remains read-only.
        before=JSON.toJson(m.forSave());var repeated=m.forecast(g);
        check("incoming projection is repeatable without consuming current contracts",projected.quantityNeeded(candidate)==repeated.quantityNeeded(candidate)&&before.equals(JSON.toJson(m.forSave())));
    }
    static void crossClassPaidExcess(){
        out.println("--- late normal vacancies receive only privately funded paid-half excess ---");
        for(double budget:new double[]{12,20})for(boolean rich:new boolean[]{false,true}){
            Game g=new Game(GameFiles.scratch("res3-cross-excess-"+(int)budget+rich),Founding.defaults().withUnfitted(false));g.run();
            var bm=g.getBuildingManager();bm.addStack(bm.getTemplate(0),1,true);bm.addStack(bm.getTemplate(157),1,true);
            var m=g.getSectors().realEstate().housingMarket();m.clear(g,List.of());var state=m.forSave();var a=lateHome();
            var b=new HousingInventory.Key(157,HousingForms.of(157).recipe().mix().getFirst().id());
            for(var c:state.classes){c.ask=c.lastTrade=20;if(c.recipe==0){c.occupied=1;c.demand=2;c.tradeQuantity=1;c.tradeValue=20;}}
            var pair=lateDemand("paid-pair",2,budget);var mover=lateDemand("rich-mover",1,100);
            state.month=g.getMonth()-1;state.demand=List.of(pair);state.leases=List.of(new HousingChoice.Lease(pair.key(),a,1,20,2,new HousingChoice.Funding(0,2,0)));
            state.kept=state.crowded=1;state.hosted=state.suitable=state.unaffordable=state.noSuitable=state.unplaced=0;m.restore(state);
            var r=m.clear(g,rich?List.of(pair,mover):List.of(pair));boolean moves=budget>=20&&!rich;
            var old=r.leases().stream().filter(l->l.cell().equals(pair.key())&&l.home().equals(a)).findFirst().orElseThrow();
            near("cross excess keeps the old funded footprint exact",old.count(),1);near("cross excess keeps the old bill exact",old.bill(),20);
            near("cross excess moves exactly the eligible source household",old.households(),moves?1:2);
            check("cross excess updates the remaining full/half modes",old.funding().equals(moves?new HousingChoice.Funding(1,0,0):new HousingChoice.Funding(0,2,0)));
            near("normal newcomer has priority over excess upgrades",r.leases().stream().filter(l->l.cell().equals(mover.key())&&l.home().equals(b)).mapToDouble(HousingChoice.Lease::households).sum(),rich?1:0);
            near("cross excess uses the actual alternative only when funded",r.leases().stream().filter(l->l.cell().equals(pair.key())&&l.home().equals(b)).mapToDouble(HousingChoice.Lease::households).sum(),moves?1:0);
            near("cross excess preserves source households",r.leases().stream().mapToDouble(HousingChoice.Lease::households).sum()+r.unplaced(),rich?3:2);
            check("cross excess cannot consume unavailable native stock",m.classes().stream().allMatch(c->c.occupied<=c.homes));
        }
    }
    static void savedContractModes()throws Exception{
        out.println("--- all three coalesced contract modes survive native fresh and running loads ---");
        Game g=new Game(GameFiles.scratch("res3-contract-modes-save"),Founding.defaults().withUnfitted(false));g.run();
        var bm=g.getBuildingManager();bm.addStack(bm.getTemplate(0),3,true);var m=g.getSectors().realEstate().housingMarket();m.clear(g,List.of());
        var state=m.forSave();var key=lateHome();var modes=new HousingChoice.Funding(1,1,1);
        for(var c:state.classes)if(c.key().equals(key)){c.ask=c.lastTrade=20;c.occupied=c.tradeQuantity=1.5;c.tradeValue=30;c.demand=3;}
        state.demand=List.of(lateDemand("all-modes",3,20));state.leases=List.of(new HousingChoice.Lease("all-modes",key,1.5,30,3,modes));
        state.month=g.getMonth();state.kept=1.5;state.crowded=1.5;state.hosted=1;state.suitable=state.unaffordable=state.noSuitable=state.unplaced=0;m.restore(state);
        Map<HousingInventory.Key,Double> occupied=new LinkedHashMap<>();for(var s:bm.getHousingInventory().stock())occupied.put(s.key(),s.key().equals(key)?1.5:0);bm.getHousingInventory().matchClasses(occupied);
        String before=JSON.toJson(m.forSave());var copy=new HousingMarket();copy.restore(JSON.fromJson(before,HousingMarket.State.class));
        check("nonzero full/half/sponsored contract round-trips exactly",before.equals(JSON.toJson(copy.forSave()))&&copy.leases().getFirst().funding().equals(modes));
        copy.redenominate(.001);check("denomination preserves every contract quantity",copy.leases().getFirst().funding().equals(modes));near("denomination scales only the actual bill",copy.receipts(),.03);
        var corrupt=JSON.fromJson(before,HousingMarket.State.class);corrupt.leases=List.of(new HousingChoice.Lease("all-modes",key,1.5,30,3,new HousingChoice.Funding(2,0,1)));
        boolean refused=false;try{new HousingMarket().restore(corrupt);}catch(IllegalArgumentException expected){refused=true;}
        check("load rejects funded components inconsistent with actual occupied stock",refused);
        corrupt=JSON.fromJson(before,HousingMarket.State.class);corrupt.hosted=0;refused=false;
        try{new HousingMarket().restore(corrupt);}catch(IllegalArgumentException expected){refused=true;}
        check("load rejects lost sponsored provenance",refused);
        check("all-mode fixture saves natively in scratch slot10",g.saveGame(10,"RES3 contract modes").ok);
        Game fresh=new Game(g.getGameFiles());fresh.loadGameSave(10);
        Game running=new Game(GameFiles.scratch("res3-contract-modes-running"));running.run();
        running.getBuildingManager().addStack(running.getBuildingManager().getTemplate(0),7,true);
        Files.createDirectories(running.getGameFiles().saveFile(10).getParent());
        Files.copy(g.getGameFiles().saveFile(10),running.getGameFiles().saveFile(10),StandardCopyOption.REPLACE_EXISTING);
        Files.copy(g.getGameFiles().historyFile(10),running.getGameFiles().historyFile(10),StandardCopyOption.REPLACE_EXISTING);running.loadGameSave(10);
        for(Game loaded:List.of(g,fresh,running)){
            var market=loaded.getSectors().realEstate().housingMarket();check("native load preserves coalesced contract bytes",before.equals(JSON.toJson(market.forSave())));
            var previous=market.forSave();previous.month=loaded.getMonth()-1;market.restore(previous);
            var result=market.clear(loaded,List.of(lateDemand("all-modes",3,16)));
            near("next clearing cannot retain an unaffordable full-price component",result.leases().stream().mapToDouble(l->l.funding().full()).sum(),0);
            near("without a funded host every actual assignment is now a paid half",result.leases().stream().mapToDouble(l->l.funding().half()).sum(),3);
            near("previous sponsorship has priority only in actually funded host room",market.hosted(),0);
            near("next clearing keeps the three actual households",result.leases().stream().mapToDouble(HousingChoice.Lease::households).sum(),3);
        }
    }
    public static void main(String[] args)throws Exception{
        out=System.out;fractionalSettlement();fractionalClaims();fractionalDividends();
        out=System.out;unbuiltGrossQuotes();
        out=System.out;coalescedSponsored();sharingForecastSources();crossClassPaidExcess();savedContractModes();
        out=System.out;fundedPressureAndForecast();rentCommitmentBudgets();independentDoorPressure();committedDoorBoundaries();latePaidFootprints();stagedHousingStocks();stagedHousingFlows();carriedStudentService();retainedFundedGuest();PrintStream silent=new PrintStream(OutputStream.nullOutputStream());
        StringBuilder report=new StringBuilder("# RES3 actual quote, budget and availability report\n\nGenerated by HousingMarketCheck through scratch cities, model inputs and the paid build path. Starting geometry, capital partitions and reference anchors are RES1 section 12; observed completions and receipts below are actual model outcomes.\n\nThe two catalogue cities begin with two billion present-day dollars of RE equity, four billion of government equity, two billion square feet of owned land, 100 construction depots, 20 coal plants, 20 water plants, 200 gravel roads and 100,000 migrants. These are supporting fixture inputs before the audit opens; every residential order uses the paid builder, materials, work queue, land, funding and settlement path. Ordinary city construction may also finish during the observation window. Historical founding gates use current research/known-template state; modern equipment is not inferred. Timings are individual integrated months on the executing host, including all other city systems, not isolated solver timings.\n");
        try{System.setOut(silent);classPrices();rentPriceIndex();mergedBudgetPressure();plannerInputs();netOperatingChoice();foundingBudgets();fractionalPressure();sharingOrder();doubledDoors();doubledDemand();studentFacility();fundedHost();scarceFreeHost();retiredRoom();loadedDoorCensus();causedStrandedDebtContinuation();familyHostReservation();aggregatePackingRemainder();savingHousingBudget();cardHousingBudget();smallProfile(report);city(report,false);city(report,true);}finally{System.setOut(out);silent.close();}
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
