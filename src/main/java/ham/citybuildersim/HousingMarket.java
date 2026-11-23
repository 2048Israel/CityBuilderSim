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
    /** A monthly transfer between existing cells, not a permanent preference or financial axis. */
    public record Sharing(String source,String target,double groups,double sourceCount,double targetCount,
                          double sourceBudget,double targetBudget) { }
    public static final class ClassState {
        public int recipe,capacity;
        public String dwelling;
        public double ask, capital, carry, demand, homes, occupied, target;
        public double lastTrade, tradeQuantity, tradeValue;
        public int stale;
        public boolean available;
        HousingInventory.Key key() { return new HousingInventory.Key(recipe,dwelling); }
        ClassState copy() { ClassState c=new ClassState();c.recipe=recipe;c.capacity=capacity;c.dwelling=dwelling;c.ask=ask;c.capital=capital;
            c.carry=carry;c.demand=demand;c.homes=homes;c.occupied=occupied;c.target=target;
            c.lastTrade=lastTrade;c.tradeQuantity=tradeQuantity;c.tradeValue=tradeValue;c.stale=stale;c.available=available;return c; }
    }
    /** A bounded save snapshot; a load restores it without quoting, clearing or repricing. */
    public static final class State {
        public int month=Integer.MIN_VALUE;
        public List<ClassState> classes=new ArrayList<>();
        public List<HousingChoice.Lease> leases=new ArrayList<>();
        public List<HousingChoice.Demand> demand=new ArrayList<>();
        public List<HousingChoice.Offer> pipeline=new ArrayList<>();
        public List<Sharing> sharing=new ArrayList<>();
        public double kept,suitable,crowded,unaffordable,noSuitable,unplaced;
        public double hosted;
    }
    private final Map<HousingInventory.Key,ClassState> classes=new LinkedHashMap<>();
    private List<HousingChoice.Lease> leases=List.of();
    private int month=Integer.MIN_VALUE;
    private HousingChoice.Result result=new HousingChoice.Result(List.of(),0,0,0,0,0,0);
    private List<HousingChoice.Demand> demand=List.of();
    private List<HousingChoice.Offer> offers=List.of();
    private List<HousingChoice.Offer> pipeline=List.of();
    private List<Sharing> sharing=List.of();
    private double hosted;
    /** Derived pre-migration workspace, discarded by clearing and rebuilt after load. */
    private boolean arrivalProjection, arrivalsNeedFamily;
    private double formingFamilies, formingAdults;
    /** Emergency guests whose host already funds the door; no extra rent or credit is invented. */
    public double hosted(){return hosted;}
    public double sharingGroups(){return sharing.stream().mapToDouble(Sharing::groups).sum();}
    public List<HousingChoice.Demand> demands(){return demand;}
    public boolean active() { return month!=Integer.MIN_VALUE; }
    public int clearedMonth() { return month; }
    public List<HousingChoice.Lease> leases() { return leases; }
    public List<ClassState> classes() { return classes.values().stream().map(ClassState::copy).toList(); }
    public HousingChoice.Result result() { return result; }
    public double rentFor(String cell,double count) { return count>0?leases.stream().filter(l->l.cell().equals(cell)||l.cell().startsWith(cell+"#")).mapToDouble(HousingChoice.Lease::bill).sum()/count:0; }
    public double receipts() { return leases.stream().mapToDouble(HousingChoice.Lease::bill).sum(); }
    public double occupied() { return leases.stream().mapToDouble(HousingChoice.Lease::count).sum(); }
    /** Reserve this press's domestic formation before admitting people who start work next press. */
    public void prepareMigration(Game game){
        arrivalProjection=true;formingFamilies=formingAdults=0;
        arrivalsNeedFamily=game.getCohorts().get(AgeBand.BABY)+game.getCohorts().get(AgeBand.CHILD)+game.getCohorts().get(AgeBand.TEEN)>0;
        if(!active())return;
        var current=game.getFamilies();var preview=new FamilyModel();
        preview.restore(current.toSaveArray());preview.rememberHouseholds(current.remembersHouseholds());
        var people=game.getPopulationManager();double[] jobs=new double[PayTier.values().length];
        var posts=people.getJobs();var fill=people.getJobFillRate();
        for(JobType job:JobType.values())jobs[PayTier.of(job).ordinal()]+=posts[job.ordinal()]*fill[job.ordinal()];
        preview.rebuild(game.getCohorts(),jobs,game.getCrime().prisoners(),people.getUnemployed()+people.getStudyingTotal(),current.getOwnerAdults());
        preview.setSeekers(game.getUnemployment().getHoused(),people.getStudyingTotal());
        double beforeFamily=0,beforeAdult=0,afterFamily=0,afterAdult=0;
        for(FamilyStructure shape:FamilyStructure.values()){
            double old=0;for(PayTier tier:PayTier.values())old+=current.getFormed(shape,tier);
            double n=preview.totalOf(shape);
            if(!shape.isRetired()&&shape.dependants()>0){beforeFamily+=old;afterFamily+=n;}else{beforeAdult+=old;afterAdult+=n;}
        }
        double previousSeekers=current.getSeekers(FamilyModel.Seeker.UNEMPLOYED)+current.getSeekers(FamilyModel.Seeker.STUDENT);
        if(current.dependantsPerOutsideHousehold()>0)beforeFamily+=previousSeekers;else beforeAdult+=previousSeekers;
        double seekers=preview.getSeekers(FamilyModel.Seeker.UNEMPLOYED)+preview.getSeekers(FamilyModel.Seeker.STUDENT);
        if(preview.dependantsPerOutsideHousehold()>0)afterFamily+=seekers;else afterAdult+=seekers;
        formingFamilies=Math.max(0,afterFamily-beforeFamily);formingAdults=Math.max(0,afterAdult-beforeAdult);
    }
    /** Read-only migration bound: actual remaining door/guest capacity, with the raw incoming household mix. */
    public FamilyModel.Room roomLeft(Game game,FamilyModel.Room legacy){
        if(!active())return null;
        var inventory=game.getBuildingManager().getHousingInventory();double family=0,compact=0,homes=0,children=0,adults=0;
        Map<String,Boolean> familyCells=new HashMap<>();for(var d:demand)familyCells.put(d.key(),d.children()>0);
        for(var stock:inventory.stock()){
            var c=classes.get(stock.key());if(c==null||!c.available)continue;
            double owned=inventory.owned(c.key(),HousingInventory.Owner.REAL_ESTATE);homes+=owned;
            double assigned=leases.stream().filter(l->l.home().equals(c.key())).mapToDouble(HousingChoice.Lease::households).sum();
            // A demolition/title transfer can precede the next monthly clearing.
            // Reserve other rooms for its displaced tenants before admitting arrivals.
            double lost=Math.max(0,assigned-2*owned);
            double parents=leases.stream().filter(l->l.home().equals(c.key())&&familyCells.getOrDefault(l.cell(),false))
                    .mapToDouble(HousingChoice.Lease::households).sum();
            double lostFamilies=Math.min(lost,Math.max(0,parents-2*owned));
            children+=lostFamilies;adults+=lost-lostFamilies;
            double slots=Math.max(0,2*owned-assigned);
            if(c.capacity>FamilyModel.STUDIO_MAX_SIZE)family+=slots;else compact+=slots;
        }
        if(result.unplaced()>0)for(var d:demand){double taken=leases.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
            double missing=Math.max(0,d.count()-taken);if(d.children()>0)children+=missing;else adults+=missing;}
        family=Math.max(0,family-children);double inCompact=Math.min(compact,adults);compact-=inCompact;family=Math.max(0,family-(adults-inCompact));
        if(arrivalProjection){
            family=Math.max(0,family-formingFamilies);inCompact=Math.min(compact,formingAdults);compact-=inCompact;family=Math.max(0,family-(formingAdults-inCompact));
        }
        double incoming=0,heads=0,parents=0;
        for(Household c:game.getHouseholdBalance().cells())if(c instanceof WorkingHousehold w){
            double n=game.getFamilies().getFormed(w.shape(),w.tier(),w.stage());incoming+=n;heads+=n*c.size();
            if(c.shape().dependants()>0)parents+=n;
        }
        double seeking=demand.stream().mapToDouble(HousingChoice.Demand::count).sum();
        // The approach ramp measures physical crowding; affordability already has its own migration pull.
        // Counting only paid leases here would freeze an empty founding city's workers out of its vacant homes.
        return new FamilyModel.Room(family,compact,0,seeking,Math.min(homes,legacy.withADoor),arrivalProjection?(arrivalsNeedFamily?1:0):incoming>0?parents/incoming:legacy.withDependants,
                arrivalProjection?1:incoming>0?heads/incoming:legacy.peoplePerHousehold);
    }
    public double price(HousingInventory.Key key) { ClassState c=classes.get(key);return c==null?0:c.ask; }
    /** A posted class ask can change without inventing a lease or a trade. */
    public void setAsk(HousingInventory.Key key,double ask){nonnegative(ask);ClassState c=classes.get(key);
        if(c==null)throw new IllegalArgumentException("Unknown housing quote");c.ask=ask;}
    public double meanPerCapacity(BuildingsTemplate t) {
        HousingRecipe r=t.getHousingRecipe(); if(r==null||t.getCapacity()<=0)return 0;
        double total=0;for(var d:r.mix())total+=d.count()*price(new HousingInventory.Key(t.getId(),d.id()));
        return total/t.getCapacity();
    }
    /** Descriptive pooled compatibility getter; it is never an independently traded quote. */
    public double paidPerCapacity(boolean family){double money=0,weight=0;
        for(var l:leases){ClassState c=classes.get(l.home());if((c.capacity>FamilyModel.STUDIO_MAX_SIZE)==family){money+=l.bill();weight+=l.count()*c.capacity;}}
        if(weight>0)return money/weight;
        for(ClassState c:classes.values())if(c.available&&(c.capacity>FamilyModel.STUDIO_MAX_SIZE)==family){money+=c.ask;weight+=c.capacity;}
        return weight>0?money/weight:0;
    }
    /** Reads the existing builder's taxed work/material/land quote; no second construction timer. */
    private void quote(Game game) {
        var bm=game.getBuildingManager();var em=game.getEconomyManager();var re=game.getSectors().realEstate();
        double totalBasis=0;
        Map<Integer,Double> carry=new HashMap<>();
        for(BuildingsTemplate t:bm.getTemplates()){
            if(!re.key().equals(t.getSector()))continue;
            double structure=game.quoteBuild(t,1).total;
            double full=game.doorPriceOf(t,1);
            double land=Math.max(0,full-structure);
            // Whole-project utilities and payroll belong here once, alongside the repair/property carry.
            carry.put(t.getId(),em.housingCarry(t));
            HousingRecipe r=t.getHousingRecipe();if(r==null)continue;
            HousingForms.Form f=HousingForms.of(t.getId());
            for(var d:r.mix()){
                var key=new HousingInventory.Key(t.getId(),d.id());
                ClassState c=classes.get(key);boolean initial=c==null;
                if(initial){c=new ClassState();c.recipe=key.recipe();c.dwelling=key.dwelling();classes.put(key,c);}
                c.capacity=d.capacity();
                c.capital=f==null?full*d.costWeight()/r.costWeight():f.homeCapital(d.id(),Math.max(0,full-land),land);
                boolean known=bm.isTemplateKnown(t);
                if(initial||(!c.available&&known))c.ask=c.capital*RealEstate.LANDLORD_YIELD/12;
                c.available=known;
            }
        }
        var stock=bm.getHousingInventory().stock();
        for(ClassState c:classes.values()){c.homes=0;c.carry=0;}
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
        for(var t:game.getBuildingManager().getTemplatesBySector(Sectors.REAL_ESTATE)){
            if(game.foundedUnfitted()&&!t.knownAt(game.getMonth(),game.getResearch()))continue;
            var recipe=t.getHousingRecipe();if(recipe==null)continue;
            for(var d:recipe.mix()){
            var key=new HousingInventory.Key(t.getId(),d.id());ClassState c=classes.get(key);if(c==null)continue;var f=HousingForms.of(t.getId());
            supply.add(new HousingChoice.Offer(key,c.homes,d.capacity(),d.bedrooms(),d.area(),d.quality(),
                    f==null?0:f.garden(),f!=null&&f.stepFree(),c.ask,0));
            }
        }
        demand=List.copyOf(cells);offers=List.copyOf(supply);
        hosted=0;arrivalProjection=false;formingFamilies=formingAdults=0;
        List<HousingChoice.Offer> pending=new ArrayList<>();
        for(var s:inventory.stock())if(s.pipeline()>0){var d=s.dwelling();var f=HousingForms.of(s.key().recipe());
            pending.add(new HousingChoice.Offer(s.key(),s.pipeline(),d.capacity(),d.bedrooms(),d.area(),d.quality(),
                    f==null?0:f.garden(),f!=null&&f.stepFree(),price(s.key()),0));}
        pipeline=List.copyOf(pending);
        List<HousingChoice.Lease> primary=new ArrayList<>();
        for(var l:leases){var c=classes.get(l.home());double n=c!=null&&c.lastTrade>0?Math.min(l.households(),l.bill()/c.lastTrade):l.count();
            if(n>0)primary.add(new HousingChoice.Lease(l.cell(),l.home(),n,l.bill()));}
        result=HousingChoice.clear(demand,offers,primary,game.getMonth());
        emergencySharing(game);
        emergencyDoubling(game);
        leases=result.leases();month=game.getMonth();
        for(ClassState c:classes.values()){c.occupied=0;c.demand=0;}
        for(var l:leases){ClassState c=classes.get(l.home());c.occupied+=l.count();c.demand+=l.count();}
        // One-count residual demand goes to its best compatible affordable class, never to every alternative.
        if(result.unplaced()>0)for(var d:demand){double used=leases.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
            double left=Math.max(0,d.count()-used),best=Double.NEGATIVE_INFINITY;
            if(left<=FamilyModel.packingRounding(d.count()))continue;
            List<HousingChoice.Offer> tied=new ArrayList<>();
            HousingChoice.Demand willing=d;
            for(var shift:sharing)if(shift.target().equals(d.key()))willing=with(d,d.count(),shift.targetBudget());
            final var seeker=willing;
            for(var o:offers)if(HousingChoice.suitable(seeker,o,false)&&HousingChoice.affordable(seeker,o)){
                double score=HousingChoice.score(seeker,o);int order=Double.compare(score,best);
                if(order>0){best=score;tied.clear();}if(order>=0)tied.add(o);
            }
            // Equal preferences have no catalogue-order entitlement. Split the one-count pressure
            // by comparable supply, with one quote unit for a class not built yet.
            double weight=tied.stream().mapToDouble(o->Math.max(1,o.count())).sum();
            for(var o:tied)classes.get(o.key()).demand+=left*Math.max(1,o.count())/weight;
        }
        Map<HousingInventory.Key,Double> actual=new LinkedHashMap<>();
        for(var s:inventory.stock())actual.put(s.key(),classes.get(s.key()).occupied);
        inventory.matchClasses(actual);
        return result;
    }
    private record LeaseKey(String cell,HousingInventory.Key home) { }
    /** Guests and hosts retain separate household counts while sharing one actual door and rent bill. */
    private void emergencyDoubling(Game game){
        Map<String,Integer> kinds=new HashMap<>();
        for(Household c:game.getHouseholdBalance().cells())kinds.put(c.key(),c instanceof StudentHousehold?1:c instanceof UnemployedHousehold?2:0);
        java.util.function.ToIntFunction<String> kind=key->kinds.getOrDefault(key.split("#",2)[0],0);
        List<HousingChoice.Lease> all=new ArrayList<>(result.leases());double extra=0;
        List<HousingChoice.Demand> without=new ArrayList<>();
        for(var d:demand){double used=all.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
            double n=Math.max(0,d.count()-used);if(n>FamilyModel.packingRounding(d.count()))without.add(with(d,n,d.budget()));}
        List<HousingChoice.Offer> hostRooms=new ArrayList<>();
        for(var o:offers){double n=all.stream().filter(l->l.home().equals(o.key())).mapToDouble(l->Math.max(0,2*l.count()-l.households())).sum();
            hostRooms.add(new HousingChoice.Offer(o.key(),n,o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),0,0));}
        var shelter=HousingChoice.clear(without,hostRooms,List.of(),game.getMonth());
        for(var o:offers){double n=shelter.leases().stream().filter(l->l.home().equals(o.key())).mapToDouble(HousingChoice.Lease::count).sum();
            double hosts=all.stream().filter(l->l.home().equals(o.key())).mapToDouble(l->Math.max(0,2*l.count()-l.households())).sum();if(!(n>0))continue;
            for(int i=0;i<all.size();i++){var l=all.get(i);if(l.home().equals(o.key()))
                all.set(i,new HousingChoice.Lease(l.cell(),l.home(),l.count()-n*Math.max(0,2*l.count()-l.households())/(2*hosts),l.bill(),l.households()));}
            for(var l:shelter.leases())if(l.home().equals(o.key()))all.add(new HousingChoice.Lease(l.cell(),l.home(),l.count()/2,0,l.count()));
            hosted+=n;extra+=n;
        }
        // Seekers retain their own-kind rule; rotate access to shared vacancies in the emergency phase.
        for(int turn=0;turn<3;turn++){
            int group=Math.floorMod(game.getMonth()+turn,3);
            List<HousingChoice.Demand> missing=new ArrayList<>();
            for(var d:demand)if(kind.applyAsInt(d.key())==group){
                double used=all.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
                double n=Math.max(0,d.count()-used);if(n>FamilyModel.packingRounding(d.count()))missing.add(with(d,n,d.budget()));
            }
            if(missing.isEmpty())continue;
            List<HousingChoice.Offer> rooms=new ArrayList<>();
            for(var o:offers){
                double occupied=all.stream().filter(l->l.home().equals(o.key())).mapToDouble(HousingChoice.Lease::count).sum();
                rooms.add(new HousingChoice.Offer(o.key(),2*Math.max(0,o.count()-occupied),o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent()/2,o.recurring()/2));
            }
            var guests=HousingChoice.clear(missing,rooms,List.of(),game.getMonth());
            for(var o:offers){
                double added=guests.leases().stream().filter(l->l.home().equals(o.key())).mapToDouble(HousingChoice.Lease::count).sum();
                if(!(added>0))continue;
                for(var l:guests.leases())if(l.home().equals(o.key()))
                    all.add(new HousingChoice.Lease(l.cell(),l.home(),l.count()/2,l.bill(),l.count()));
                extra+=added;
            }
        }
        Map<LeaseKey,HousingChoice.Lease> merged=new LinkedHashMap<>();
        for(var l:all){var key=new LeaseKey(l.cell(),l.home());var previous=merged.get(key);
            merged.put(key,previous==null?l:new HousingChoice.Lease(l.cell(),l.home(),previous.count()+l.count(),previous.bill()+l.bill(),previous.households()+l.households()));}
        all=bounded(new ArrayList<>(merged.values()));
        Map<String,Double> oldBudgets=new HashMap<>();for(var shift:sharing)oldBudgets.put(shift.target(),shift.targetBudget());
        result=HousingChoice.summarize(demand,offers,all,result.kept(),result.suitable(),result.crowded()+extra,oldBudgets);
    }
    private record Pool(Household source,HousingChoice.Demand original,HousingChoice.Demand target) { }
    private static HousingChoice.Demand with(HousingChoice.Demand d,double count,double budget){
        return new HousingChoice.Demand(d.key(),count,d.size(),d.children(),d.retired(),d.tier(),d.young(),budget);
    }
    /** Normal placements and their valid incumbents are settled before this emergency phase. */
    private void emergencySharing(Game game){
        Map<String,HousingChoice.Demand> cells=new LinkedHashMap<>();for(var d:demand)cells.put(d.key(),d);
        Map<String,Pool> pools=new LinkedHashMap<>();List<HousingChoice.Demand> proposed=new ArrayList<>();
        for(Household c:game.getHouseholdBalance().cells()){
            var d=cells.get(c.key());if(d==null)continue;
            boolean working=c instanceof WorkingHousehold w&&w.shape()==FamilyStructure.SINGLE_ADULT;
            if(!working&&!(c instanceof StudentHousehold)&&!(c instanceof UnemployedHousehold))continue;
            double used=result.leases().stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::count).sum();
            double groups=Math.min(Math.max(0,d.count()-used),d.count()*FamilyModel.MAX_SHARING)/5;
            if(!(groups>0))continue;String target;int size,children;boolean young=d.young();
            if(c instanceof WorkingHousehold w){target=game.getHouseholdBalance().cell(FamilyStructure.SHARED_ADULTS,w.tier(),w.stage()).key();size=5;children=0;}
            else {target=c.key()+"#sharing";size=5*d.size();children=5*d.children();}
            var next=new HousingChoice.Demand(target,0,size,children,false,d.tier(),young,d.budget()*5);
            String key=c.key()+"#pool";pools.put(key,new Pool(c,d,next));
            proposed.add(new HousingChoice.Demand(key,groups,size,children,false,d.tier(),young,d.budget()*5));
        }
        Map<HousingInventory.Key,Double> used=new HashMap<>();for(var l:result.leases())used.merge(l.home(),l.count(),Double::sum);
        List<HousingChoice.Offer> free=new ArrayList<>();
        for(var o:offers)free.add(new HousingChoice.Offer(o.key(),Math.max(0,o.count()-used.getOrDefault(o.key(),0.0)),o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent(),o.recurring()));
        var placed=HousingChoice.clear(proposed,free,List.of(),game.getMonth());
        List<HousingChoice.Lease> all=new ArrayList<>(result.leases());all.addAll(placed.leases());
        List<HousingChoice.Demand> missing=new ArrayList<>();
        for(var d:proposed){double n=d.count()-placed.leases().stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
            if(n>FamilyModel.packingRounding(d.count()))missing.add(with(d,n,d.budget()));}
        List<HousingChoice.Offer> hosts=new ArrayList<>();
        for(var o:offers){double n=all.stream().filter(l->l.home().equals(o.key())).mapToDouble(l->Math.max(0,2*l.count()-l.households())).sum();
            hosts.add(new HousingChoice.Offer(o.key(),n,o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),0,0));}
        // Child families cannot use compact host rooms. Keep their actual remaining
        // compatible slots before adult groups' softer preference for family space.
        Set<String> pooled=new HashSet<>();for(var p:pools.values())pooled.add(p.original().key());
        double waitingChildren=0;
        for(var d:demand)if(d.children()>0&&!pooled.contains(d.key())){
            double taken=all.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
            waitingChildren+=Math.max(0,d.count()-taken);
        }
        double familyHosts=hosts.stream().filter(o->o.capacity()>FamilyModel.STUDIO_MAX_SIZE).mapToDouble(HousingChoice.Offer::count).sum();
        double reserved=Math.min(familyHosts,waitingChildren);
        if(reserved>0)for(int i=0;i<hosts.size();i++){var o=hosts.get(i);if(o.capacity()>FamilyModel.STUDIO_MAX_SIZE)
            hosts.set(i,new HousingChoice.Offer(o.key(),o.count()*(1-reserved/familyHosts),o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),0,0));}
        var sheltered=HousingChoice.clear(missing,hosts,List.of(),game.getMonth());
        for(var o:hosts){double n=sheltered.leases().stream().filter(l->l.home().equals(o.key())).mapToDouble(HousingChoice.Lease::households).sum();if(!(n>0))continue;
            double actualHosts=all.stream().filter(l->l.home().equals(o.key())).mapToDouble(l->Math.max(0,2*l.count()-l.households())).sum();
            for(int i=0;i<all.size();i++){var l=all.get(i);if(l.home().equals(o.key()))
                all.set(i,new HousingChoice.Lease(l.cell(),l.home(),l.count()-n*Math.max(0,2*l.count()-l.households())/(2*actualHosts),l.bill(),l.households()));}
            for(var l:sheltered.leases())if(l.home().equals(o.key()))all.add(new HousingChoice.Lease(l.cell(),l.home(),l.count()/2,0,l.count()));
            hosted+=n;
        }
        all=bounded(all);
        List<Sharing> transfers=new ArrayList<>();Map<String,Double> oldBudgets=new HashMap<>();
        double normal=result.suitable(),crowded=result.crowded();
        for(var entry:pools.entrySet()){
            String key=entry.getKey();Pool pool=entry.getValue();
            double q=all.stream().filter(l->l.cell().equals(key)).mapToDouble(HousingChoice.Lease::households).sum();if(!(q>0))continue;
            double actual=game.getFamilies().shareForTypedHousing(pool.source(),q);
            if(!(actual>0))continue;
            var from=cells.get(pool.original().key());var before=cells.getOrDefault(pool.target().key(),pool.target());
            transfers.add(new Sharing(from.key(),before.key(),actual,from.count(),before.count(),from.budget(),before.budget()));
            double count=before.count()+actual;
            cells.put(from.key(),with(from,Math.max(0,from.count()-5*actual),from.budget()));
            cells.put(before.key(),with(before,count,(before.budget()*before.count()+5*from.budget()*actual)/count));
            oldBudgets.put(before.key(),before.budget());
            for(int i=0;i<all.size();i++){var l=all.get(i);if(!l.cell().equals(key))continue;
                double n=l.households()*actual/q;var o=offers.stream().filter(a->a.key().equals(l.home())).findFirst().orElseThrow();
                if(o.capacity()>=pool.target().size()&&l.households()==l.count())normal+=n;else crowded+=n;
                all.set(i,new HousingChoice.Lease(before.key(),l.home(),l.count()*actual/q,l.bill()*actual/q,n));
            }
        }
        // No proposed-but-unplaced group becomes a household or receives a rental bill.
        all.removeIf(l->pools.containsKey(l.cell()));
        Map<LeaseKey,HousingChoice.Lease> coalesced=new LinkedHashMap<>();
        for(var l:all){var key=new LeaseKey(l.cell(),l.home());var before=coalesced.get(key);
            coalesced.put(key,before==null?l:new HousingChoice.Lease(l.cell(),l.home(),before.count()+l.count(),before.bill()+l.bill(),before.households()+l.households()));}
        all=bounded(new ArrayList<>(coalesced.values()));demand=List.copyOf(cells.values());sharing=List.copyOf(transfers);
        result=HousingChoice.summarize(demand,offers,all,result.kept(),normal,crowded,oldBudgets);
    }
    /** Keeps exact physical upper bounds while tolerating only the existing packing-rounding scale. */
    private List<HousingChoice.Lease> bounded(List<HousingChoice.Lease> input){
        Map<HousingInventory.Key,Double> limits=new HashMap<>(),totals=new HashMap<>(),used=new HashMap<>();
        for(var o:offers)limits.put(o.key(),o.count());for(var l:input)totals.merge(l.home(),l.count(),Double::sum);
        for(var e:totals.entrySet())if(e.getValue()-limits.getOrDefault(e.getKey(),0.0)>FamilyModel.packingRounding(e.getValue()))
            throw new IllegalStateException("Housing emergency allocation exceeds physical homes");
        List<HousingChoice.Lease> out=new ArrayList<>();
        for(var l:input){double before=used.getOrDefault(l.home(),0.0),limit=limits.getOrDefault(l.home(),0.0),n=l.count();
            if(before+n>limit)n=Math.max(0,Math.nextDown(limit-before));if(!(n>0))continue;
            double households=l.households()==l.count()?n:Math.max(n,Math.min(2*n,l.households()*(n/l.count())));
            used.put(l.home(),before+n);out.add(new HousingChoice.Lease(l.cell(),l.home(),n,l.bill()*n/l.count(),households));}
        return out;
    }
    /** Forecast only: one disjoint source per current household and per incoming worker cohort. */
    public double candidateIncome(Game game,BuildingsTemplate t,long quantity){return forecast(game).income(t,quantity);}
    /** One planner read reserves standing homes and funded sites once for every alternative. */
    public Forecast forecast(Game game){
        if(game==null)return new Forecast(null,List.of());
        List<HousingChoice.Demand> planning=new ArrayList<>(planningDemand());
        var bm=game.getBuildingManager();var tax=game.getEconomyManager().getTaxPolicy();
        double[] wages=game.getPopulationManager().getWagesPerType();
        Map<String,Double> formed=new LinkedHashMap<>();double heads=0,workers=0;
        for(Household c:game.getHouseholdBalance().cells())if(c instanceof WorkingHousehold w){
            double n=game.getFamilies().getFormed(w.shape(),w.tier(),w.stage());
            formed.put(c.key(),n);heads+=n*c.size();workers+=n*c.earningWeight();
        }
        if(!(workers>0))for(var d:planning){
            Household c=game.getHouseholdBalance().cells().stream().filter(h->h.key().equals(d.key())).findFirst().orElse(null);
            if(c instanceof WorkingHousehold){formed.put(c.key(),d.count());heads+=d.count()*c.size();workers+=d.count()*c.earningWeight();}
        }
        double pipelineHeads=bm.getHousingInventory().stock().stream().mapToDouble(r->r.pipeline()*r.dwelling().capacity()).sum();
        double wanted=game.getSectors().realEstate().latentHeadShortfall()+game.getHouseholdCapacity()+pipelineHeads;
        double newHeads=Math.max(0,wanted-game.getCohorts().total());
        double jobs=game.getPopulationManager().getTotalJobs()+bm.getPostsWithheld()+bm.getJobsUnderConstruction();
        double scale=heads>0&&workers>0?Math.min(newHeads/heads,Math.max(0,jobs-workers)/workers):0;
        for(Household c:game.getHouseholdBalance().cells())if(c instanceof WorkingHousehold w){
            double n=formed.getOrDefault(c.key(),0.0)*scale;if(!(n>0))continue;
            double net=0;int kinds=0;
            for(JobType job:JobType.values())if(PayTier.of(job)==c.tier()){
                net+=wages[job.ordinal()]*Math.max(0,1-tax.payslipShare(WageBand.of(job)));kinds++;
            }
            double budget=game.getHouseholdBalance().incomingHousingBudget(c,kinds>0?net/kinds:0,game.getHouseholds(),game.getSectors().retail().getStoreSellPrice());
            planning.add(new HousingChoice.Demand(c.key()+"#incoming",n,c.size(),c.shape().dependants(),false,c.tier().ordinal(),w.stage()==AdultStage.YOUNG,budget));
        }
        List<HousingChoice.Offer> committed=new ArrayList<>();
        var inventory=bm.getHousingInventory();
        for(var r:inventory.stock()){
            var template=bm.getTemplate(r.key().recipe());if(!bm.isTemplateKnown(template))continue;
            var d=r.dwelling();var f=HousingForms.of(r.key().recipe());
            committed.add(new HousingChoice.Offer(r.key(),inventory.owned(r.key(),HousingInventory.Owner.REAL_ESTATE)+r.pipeline(),d.capacity(),d.bedrooms(),d.area(),d.quality(),f==null?0:f.garden(),f!=null&&f.stepFree(),price(r.key()),0));
        }
        var reserved=HousingChoice.clear(planning,committed,leases,month,false);
        List<HousingChoice.Demand> remaining=new ArrayList<>();
        for(var d:planning){double taken=reserved.leases().stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::count).sum();
            double n=Math.max(0,d.count()-taken);if(n<=FamilyModel.packingRounding(d.count()))continue;
            remaining.add(new HousingChoice.Demand(d.key(),n,d.size(),d.children(),d.retired(),d.tier(),d.young(),d.budget()));
        }
        return new Forecast(game,remaining);
    }
    public final class Forecast {
        private final Game city;
        private final List<HousingChoice.Demand> remaining;
        private final Map<HousingInventory.Key,Double> asks;
        private Forecast(Game city,List<HousingChoice.Demand> remaining){this.city=city;this.remaining=List.copyOf(remaining);
            Map<HousingInventory.Key,Double> prices=new HashMap<>();for(var c:classes.values())prices.put(c.key(),c.ask);asks=Map.copyOf(prices);}
        public double income(BuildingsTemplate t,long quantity){
            if(city==null||t==null||quantity<=0||t.getHousingRecipe()==null||!city.getBuildingManager().isTemplateKnown(t))return 0;
            List<HousingChoice.Offer> proposed=new ArrayList<>();var recipe=t.getHousingRecipe();var f=HousingForms.of(t.getId());
            for(var d:recipe.mix()){var key=new HousingInventory.Key(t.getId(),d.id());proposed.add(new HousingChoice.Offer(key,(double)d.count()*quantity,d.capacity(),d.bedrooms(),d.area(),d.quality(),f==null?0:f.garden(),f!=null&&f.stepFree(),asks.getOrDefault(key,0.0),0));}
            return HousingChoice.clear(remaining,proposed,List.of(),month,false).leases().stream().mapToDouble(HousingChoice.Lease::bill).sum();
        }
    }
    /** The current cell counts, before this month's emergency sharing transfers. */
    private List<HousingChoice.Demand> planningDemand(){
        Map<String,HousingChoice.Demand> before=new LinkedHashMap<>();for(var d:demand)before.put(d.key(),d);
        for(var transfer:sharing){before.put(transfer.source(),with(before.get(transfer.source()),transfer.sourceCount(),transfer.sourceBudget()));
            before.put(transfer.target(),with(before.get(transfer.target()),transfer.targetCount(),transfer.targetBudget()));}
        return List.copyOf(before.values());
    }
    public void sold(){
        for(ClassState c:classes.values()){
            if(!c.available)continue;
            c.tradeQuantity=leases.stream().filter(l->l.home().equals(c.key())).mapToDouble(HousingChoice.Lease::count).sum();
            c.tradeValue=leases.stream().filter(l->l.home().equals(c.key())).mapToDouble(HousingChoice.Lease::bill).sum();
            if(c.tradeQuantity>0){c.lastTrade=c.tradeValue/c.tradeQuantity;c.stale=0;}else c.stale++;
        }
    }
    /** Existing lease-length log adjustment and expected-inflation drift, class by class. */
    public void reprice(double expectedMonthly){
        for(ClassState c:classes.values()){
            if(!c.available)continue;
            // An unbuilt class keeps its opening supply quote until it has a market.
            double pressure=c.homes>0?c.demand/c.homes:1;
            double anchor=c.capital*RealEstate.LANDLORD_YIELD/12;
            c.target=anchor*Math.pow(pressure,RealEstate.SCARCITY_ELASTICITY);
            double balanced=c.homes*RealEstate.GLUT_PRESSURE;
            // Allocation can round down by an ULP at a full door. Classify
            // economic vacancy with the same quantity bound as the match.
            if(c.demand>=balanced-FamilyModel.packingRounding(balanced)&&c.occupied>0)c.target=Math.max(c.target,c.carry*c.homes/c.occupied);
            c.ask=c.target>0?Retail.stickyPrice(c.ask,c.target,expectedMonthly,1.0/RealEstate.LEASE_MONTHS)
                    :Math.max(0,c.ask*(1-1.0/RealEstate.LEASE_MONTHS));
        }
    }
    public State forSave(){State s=new State();s.month=month;s.classes=classes();s.leases=new ArrayList<>(leases);s.demand=new ArrayList<>(demand);s.pipeline=new ArrayList<>(pipeline);s.sharing=new ArrayList<>(sharing);
        s.hosted=hosted;
        s.kept=result.kept();s.suitable=result.suitable();s.crowded=result.crowded();s.unaffordable=result.unaffordable();s.noSuitable=result.noSuitable();s.unplaced=result.unplaced();return s;}
    private static void nonnegative(double n){if(!Double.isFinite(n)||n<0)throw new IllegalArgumentException("Invalid saved housing quantity");}
    private static void validate(State s){
        if(s.classes==null||s.leases==null||s.demand==null||s.pipeline==null||s.sharing==null||s.classes.size()>HousingRecipe.MAX_CLASSES)
            throw new IllegalArgumentException("Invalid saved housing dimensions");
        Map<HousingInventory.Key,ClassState> rows=new HashMap<>();
        for(ClassState c:s.classes){
            if(c.dwelling==null||c.dwelling.isBlank()||c.capacity<1||rows.put(c.key(),c)!=null)throw new IllegalArgumentException("Invalid saved housing class");
            for(double n:new double[]{c.ask,c.capital,c.carry,c.demand,c.homes,c.occupied,c.target,c.lastTrade,c.tradeQuantity,c.tradeValue})nonnegative(n);
            if(c.occupied>c.homes||c.demand<c.occupied||c.stale<0)throw new IllegalArgumentException("Invalid saved housing occupancy");
        }
        Map<HousingInventory.Key,Double> occupied=new HashMap<>();
        for(var l:s.leases){if(!rows.containsKey(l.home()))throw new IllegalArgumentException("Unknown saved lease class");
            nonnegative(l.count());nonnegative(l.bill());occupied.merge(l.home(),l.count(),Double::sum);}
        for(ClassState c:s.classes)if(Math.abs(c.occupied-occupied.getOrDefault(c.key(),0.0))>FamilyModel.packingRounding(c.homes))
            throw new IllegalArgumentException("Saved housing leases do not reconcile to occupancy");
        for(double n:new double[]{s.kept,s.suitable,s.crowded,s.unaffordable,s.noSuitable,s.unplaced})nonnegative(n);
        nonnegative(s.hosted);if(s.hosted>s.crowded+FamilyModel.packingRounding(s.crowded))throw new IllegalArgumentException("Invalid saved emergency hosting");
        Map<String,HousingChoice.Demand> profiles=new HashMap<>();for(var d:s.demand)if(profiles.put(d.key(),d)!=null)throw new IllegalArgumentException("Duplicate saved housing cell");
        Map<String,Double> placed=new HashMap<>();Set<LeaseKey> leaseKeys=new HashSet<>();
        for(var l:s.leases){
            if(!profiles.containsKey(l.cell())||!leaseKeys.add(new LeaseKey(l.cell(),l.home())))throw new IllegalArgumentException("Invalid saved housing assignment");
            placed.merge(l.cell(),l.households(),Double::sum);
        }
        for(var d:s.demand)if(placed.getOrDefault(d.key(),0.0)>d.count()+FamilyModel.packingRounding(d.count()))
            throw new IllegalArgumentException("Saved housing assignments exceed their source cell");
        Set<String> shifted=new HashSet<>(),targets=new HashSet<>();
        for(var t:s.sharing){
            for(double n:new double[]{t.groups(),t.sourceCount(),t.targetCount(),t.sourceBudget(),t.targetBudget()})nonnegative(n);
            var from=profiles.get(t.source());var to=profiles.get(t.target());
            if(from==null||to==null||!shifted.add(t.source())||!targets.add(t.target())||t.source().equals(t.target())
                    ||Math.abs(from.count()+5*t.groups()-t.sourceCount())>FamilyModel.packingRounding(t.sourceCount())
                    ||Math.abs(to.count()-t.groups()-t.targetCount())>FamilyModel.packingRounding(to.count()))
                throw new IllegalArgumentException("Saved housing sharing counts do not reconcile");
            double resources=t.targetBudget()*t.targetCount()+5*t.sourceBudget()*t.groups();
            if(Math.abs(to.budget()*to.count()-resources)>MoneyAudit.tolerance(resources))throw new IllegalArgumentException("Saved housing sharing resources do not reconcile");
        }
        double assigned=s.leases.stream().mapToDouble(HousingChoice.Lease::households).sum();
        double requested=s.demand.stream().mapToDouble(HousingChoice.Demand::count).sum();
        double rounding=FamilyModel.packingRounding(Math.max(assigned,requested));
        if(Math.abs(assigned-(s.kept+s.suitable+s.crowded))>rounding
                ||Math.abs(s.unplaced-(s.unaffordable+s.noSuitable))>rounding
                ||Math.abs(requested-assigned-s.unplaced)>rounding)
            throw new IllegalArgumentException("Saved housing allocation flows do not reconcile");
    }
    public void restore(State s){if(s!=null)validate(s);reset();if(s==null)return;month=s.month;for(ClassState c:s.classes)classes.put(c.key(),c.copy());leases=List.copyOf(s.leases);demand=List.copyOf(s.demand);pipeline=List.copyOf(s.pipeline);sharing=List.copyOf(s.sharing);
        hosted=s.hosted;
        result=new HousingChoice.Result(leases,s.kept,s.suitable,s.crowded,s.unaffordable,s.noSuitable,s.unplaced);}
    /** The native load additionally checks the complete physical catalogue, including unavailable quotes. */
    public void restore(State s,BuildingManager buildings){
        if(s!=null)validate(s);
        if(s!=null&&s.month!=Integer.MIN_VALUE&&s.classes.size()!=HousingRecipe.MAX_CLASSES)
            throw new IllegalArgumentException("Incomplete saved housing catalogue");
        if(s!=null)for(ClassState c:s.classes){
            BuildingsTemplate t=buildings.getTemplate(c.recipe);
            if(t==null||t.getHousingRecipe()==null||t.getHousingRecipe().mix().stream().noneMatch(d->d.id().equals(c.dwelling)&&d.capacity()==c.capacity))
                throw new IllegalArgumentException("Unknown saved housing class "+c.key());
        }
        restore(s);
    }
    public void reset(){classes.clear();leases=List.of();demand=List.of();offers=List.of();pipeline=List.of();sharing=List.of();hosted=0;arrivalProjection=false;formingFamilies=formingAdults=0;month=Integer.MIN_VALUE;result=new HousingChoice.Result(List.of(),0,0,0,0,0,0);}
    public void redenominate(double scale){for(ClassState c:classes.values()){c.ask*=scale;c.capital*=scale;c.carry*=scale;c.target*=scale;c.lastTrade*=scale;c.tradeValue*=scale;}
        leases=leases.stream().map(l->new HousingChoice.Lease(l.cell(),l.home(),l.count(),l.bill()*scale,l.households())).toList();
        sharing=sharing.stream().map(t->new Sharing(t.source(),t.target(),t.groups(),t.sourceCount(),t.targetCount(),t.sourceBudget()*scale,t.targetBudget()*scale)).toList();
        demand=demand.stream().map(d->new HousingChoice.Demand(d.key(),d.count(),d.size(),d.children(),d.retired(),d.tier(),d.young(),d.budget()*scale)).toList();
        pipeline=pipeline.stream().map(o->new HousingChoice.Offer(o.key(),o.count(),o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent()*scale,o.recurring()*scale)).toList();
        result=new HousingChoice.Result(leases,result.kept(),result.suitable(),result.crowded(),result.unaffordable(),result.noSuitable(),result.unplaced());}
}
