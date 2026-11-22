package ham.citybuildersim;

import ham.citybuildersim.sectors.RealEstate;
import ham.citybuildersim.sectors.Retail;
import java.util.*;

/**
 * One monthly residential quote and clearing, with saved class prices and actual leases.
 * WHY (RES3): pooled rents attributed a cheap flat's demand to a vacant premium
 * home. Quotes are asks; only occupied assignments create rental receipts.
 */
public final class HousingMarket {
    /** Liquid savings can fund one year of recurring housing; no unfunded credit is assumed. */
    public static final int SAVINGS_MONTHS = 12;
    public static final class ClassState {
        public int recipe;
        public String dwelling;
        public double ask, capital, carry, demand, homes, occupied, target;
        public double lastTrade, tradeQuantity, tradeValue;
        public int stale;
        HousingInventory.Key key() { return new HousingInventory.Key(recipe,dwelling); }
        ClassState copy() { ClassState c=new ClassState();c.recipe=recipe;c.dwelling=dwelling;c.ask=ask;c.capital=capital;
            c.carry=carry;c.demand=demand;c.homes=homes;c.occupied=occupied;c.target=target;
            c.lastTrade=lastTrade;c.tradeQuantity=tradeQuantity;c.tradeValue=tradeValue;c.stale=stale;return c; }
    }
    /** A bounded save snapshot; a load restores it without quoting, clearing or repricing. */
    public static final class State {
        public int month=Integer.MIN_VALUE;
        public List<ClassState> classes=new ArrayList<>();
        public List<HousingChoice.Lease> leases=new ArrayList<>();
        public List<HousingChoice.Demand> demand=new ArrayList<>();
        public double kept,suitable,crowded,unaffordable,noSuitable,unplaced;
    }
    private final Map<HousingInventory.Key,ClassState> classes=new LinkedHashMap<>();
    private List<HousingChoice.Lease> leases=List.of();
    private int month=Integer.MIN_VALUE;
    private HousingChoice.Result result=new HousingChoice.Result(List.of(),0,0,0,0,0,0);
    private List<HousingChoice.Demand> demand=List.of();
    private List<HousingChoice.Offer> offers=List.of();
    public boolean active() { return month!=Integer.MIN_VALUE; }
    public int clearedMonth() { return month; }
    public List<HousingChoice.Lease> leases() { return leases; }
    public List<ClassState> classes() { return classes.values().stream().map(ClassState::copy).toList(); }
    public HousingChoice.Result result() { return result; }
    public double rentFor(String cell,double count) { return count>0?leases.stream().filter(l->l.cell().equals(cell)).mapToDouble(HousingChoice.Lease::bill).sum()/count:0; }
    public double receipts() { return leases.stream().mapToDouble(HousingChoice.Lease::bill).sum(); }
    public double occupied() { return leases.stream().mapToDouble(HousingChoice.Lease::count).sum(); }
    public double price(HousingInventory.Key key) { ClassState c=classes.get(key);return c==null?0:c.ask; }
    public double meanPerCapacity(BuildingsTemplate t) {
        HousingRecipe r=t.getHousingRecipe(); if(r==null||t.getCapacity()<=0)return 0;
        double total=0;for(var d:r.mix())total+=d.count()*price(new HousingInventory.Key(t.getId(),d.id()));
        return total/t.getCapacity();
    }
    /** Reads the existing builder's taxed work/material/land quote; no second construction timer. */
    private void quote(Game game) {
        var bm=game.getBuildingManager();var em=game.getEconomyManager();var re=game.getSectors().realEstate();
        double totalBasis=0;
        Map<Integer,Double> capital=new HashMap<>(),carry=new HashMap<>();
        for(BuildingsTemplate t:bm.getTemplatesBySector(re.key())){
            double land=t.getLandSqFt()*em.groundPriceFor(t);
            double full=game.getBusinessInvestment().getCostOf(t,1);
            capital.put(t.getId(),Math.max(0,full));
            // Whole-project utilities and payroll belong here once, alongside the repair/property carry.
            double wage=0;double[] wages=game.getPopulationManager().getWagesPerType();
            for(JobType job:JobType.values())wage+=t.getJobs(job)*wages[job.ordinal()];
            var u=game.getServicesManager().getUtilitiesHandler();
            carry.put(t.getId(),em.housingCarry(t));
            HousingRecipe r=t.getHousingRecipe();if(r==null)continue;
            HousingForms.Form f=HousingForms.of(t.getId());
            for(var d:r.mix()){
                var key=new HousingInventory.Key(t.getId(),d.id());
                ClassState c=classes.computeIfAbsent(key,k->{ClassState n=new ClassState();n.recipe=k.recipe();n.dwelling=k.dwelling();return n;});
                c.capital=f==null?full*d.costWeight()/r.costWeight():f.homeCapital(d.id(),Math.max(0,full-land),land);
                if(!(c.ask>0))c.ask=c.capital*RealEstate.LANDLORD_YIELD/12;
            }
        }
        var stock=bm.getHousingInventory().stock();
        for(var s:stock){ClassState c=classes.get(s.key());if(c==null)continue;
            c.homes=bm.getHousingInventory().owned(s.key(),HousingInventory.Owner.REAL_ESTATE);
            totalBasis+=(c.homes+s.pipeline())*c.capital;}
        for(var s:stock){ClassState c=classes.get(s.key());if(c==null)continue;
            double share=s.dwelling().costWeight()/s.recipe().costWeight();
            // Debt is currently a sector obligation. Fixed project capital weights partition it once,
            // including sites; a class never raises its target to make up another class's deficit.
            double debt=totalBasis>0?re.carriedInterest()*c.capital/totalBasis:0;
            c.carry=carry.getOrDefault(s.key().recipe(),0.0)*share+debt;
        }
    }
    public HousingChoice.Result clear(Game game,List<HousingChoice.Demand> cells){
        if(month==game.getMonth())return result;
        quote(game);
        var inventory=game.getBuildingManager().getHousingInventory();List<HousingChoice.Offer> supply=new ArrayList<>();
        for(var s:inventory.stock()){
            ClassState c=classes.get(s.key());if(c==null)continue;var d=s.dwelling();var f=HousingForms.of(s.key().recipe());
            supply.add(new HousingChoice.Offer(s.key(),c.homes,d.capacity(),d.bedrooms(),d.area(),d.quality(),
                    f==null?0:f.garden(),f!=null&&f.stepFree(),c.ask,0));
        }
        demand=List.copyOf(cells);offers=List.copyOf(supply);
        result=HousingChoice.clear(demand,offers,leases,game.getMonth());leases=result.leases();month=game.getMonth();
        for(ClassState c:classes.values()){c.occupied=0;c.demand=0;}
        for(var l:leases){ClassState c=classes.get(l.home());c.occupied+=l.count();c.demand+=l.count();}
        // One-count residual demand goes to its best compatible affordable class, never to every alternative.
        for(var d:demand){double used=leases.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::count).sum();
            double left=Math.max(0,d.count()-used);HousingChoice.Offer best=null;
            for(var o:offers)if(HousingChoice.suitable(d,o,false)&&HousingChoice.affordable(d,o)
                    &&(best==null||HousingChoice.score(d,o)>HousingChoice.score(d,best)))best=o;
            if(best!=null)classes.get(best.key()).demand+=left;
        }
        for(var c:classes.values())inventory.occupy(c.key(),HousingInventory.Owner.REAL_ESTATE,c.occupied);
        return result;
    }
    /** Forecast only: the same eligible demand, one count per household, across the actual mix. */
    public double candidateIncome(BuildingsTemplate t,double extraHeads){
        HousingRecipe recipe=t.getHousingRecipe();if(recipe==null)return 0;
        List<HousingChoice.Offer> supply=new ArrayList<>();
        for(var d:recipe.mix()){
            var key=new HousingInventory.Key(t.getId(),d.id());var f=HousingForms.of(t.getId());
            supply.add(new HousingChoice.Offer(key,d.count(),d.capacity(),d.bedrooms(),d.area(),d.quality(),
                    f==null?0:f.garden(),f!=null&&f.stepFree(),price(key),0));
        }
        // Candidate homes cannot collect from households already assigned to standing or funded pipeline supply.
        List<HousingChoice.Demand> movers=new ArrayList<>();
        double heads=demand.stream().mapToDouble(d->d.count()*d.size()).sum();
        for(var d:demand){double used=leases.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::count).sum();
            double n=Math.max(0,d.count()-used)+(heads>0?Math.max(0,extraHeads)*d.count()/heads:0);
            movers.add(new HousingChoice.Demand(d.key(),n,d.size(),d.children(),d.retired(),d.tier(),d.young(),d.budget()));}
        return HousingChoice.clear(movers,supply,List.of(),month).leases().stream().mapToDouble(HousingChoice.Lease::bill).sum();
    }
    public void sold(){
        for(ClassState c:classes.values()){
            c.tradeQuantity=leases.stream().filter(l->l.home().equals(c.key())).mapToDouble(HousingChoice.Lease::count).sum();
            c.tradeValue=leases.stream().filter(l->l.home().equals(c.key())).mapToDouble(HousingChoice.Lease::bill).sum();
            if(c.tradeQuantity>0){c.lastTrade=c.tradeValue/c.tradeQuantity;c.stale=0;}else c.stale++;
        }
    }
    /** Existing lease-length log adjustment and expected-inflation drift, class by class. */
    public void reprice(double expectedMonthly){
        for(ClassState c:classes.values()){
            double pressure=c.homes>0?c.demand/c.homes:c.demand>0?2:0;
            double anchor=c.capital*RealEstate.LANDLORD_YIELD/12;
            c.target=anchor*Math.pow(pressure,RealEstate.SCARCITY_ELASTICITY);
            if(pressure>=RealEstate.GLUT_PRESSURE&&c.occupied>0)c.target=Math.max(c.target,c.carry*c.homes/c.occupied);
            c.ask=c.target>0?Retail.stickyPrice(c.ask,c.target,expectedMonthly,1.0/RealEstate.LEASE_MONTHS)
                    :Math.max(0,c.ask*(1-1.0/RealEstate.LEASE_MONTHS));
        }
    }
    public State forSave(){State s=new State();s.month=month;s.classes=classes();s.leases=new ArrayList<>(leases);s.demand=new ArrayList<>(demand);
        s.kept=result.kept();s.suitable=result.suitable();s.crowded=result.crowded();s.unaffordable=result.unaffordable();s.noSuitable=result.noSuitable();s.unplaced=result.unplaced();return s;}
    public void restore(State s){reset();if(s==null)return;month=s.month;for(ClassState c:s.classes)classes.put(c.key(),c.copy());leases=List.copyOf(s.leases);demand=List.copyOf(s.demand);
        result=new HousingChoice.Result(leases,s.kept,s.suitable,s.crowded,s.unaffordable,s.noSuitable,s.unplaced);}
    public void reset(){classes.clear();leases=List.of();demand=List.of();offers=List.of();month=Integer.MIN_VALUE;result=new HousingChoice.Result(List.of(),0,0,0,0,0,0);}
    public void redenominate(double scale){for(ClassState c:classes.values()){c.ask*=scale;c.capital*=scale;c.carry*=scale;c.target*=scale;c.lastTrade*=scale;c.tradeValue*=scale;}
        leases=leases.stream().map(l->new HousingChoice.Lease(l.cell(),l.home(),l.count(),l.bill()*scale)).toList();
        result=new HousingChoice.Result(leases,result.kept(),result.suitable(),result.crowded(),result.unaffordable(),result.noSuitable(),result.unplaced());}
}
