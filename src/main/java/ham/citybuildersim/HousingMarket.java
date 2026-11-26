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
        public double rentPriceIndex;
        /** The households housed past the census line, by cell (overcrowding()); an older save has none. */
        public Map<String,Double> overcrowded=new LinkedHashMap<>();
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
    /** Chained comparable paid rent, opened at the first actual rent per capacity; saved through DataSave. */
    private double rentPriceIndex;
    /** Derived pre-migration workspace, discarded by clearing and rebuilt after load. */
    private boolean arrivalProjection, arrivalsNeedFamily;
    private double formingFamilies, formingAdults;
    /** The households housed past the census line this clearing, by cell (overcrowding()), saved with the state. */
    private Map<String,Double> overcrowded=Map.of();
    /** Emergency guests whose host already funds the door; no extra rent or credit is invented. */
    public double hosted(){return hosted;}
    /** Households living past the census line, overcrowded as the censuses counted them, never without a door (overcrowding()). */
    public double overcrowded(){double n=0;for(double v:overcrowded.values())n+=v;return n;}
    /** Their people, at their cells' sizes. */
    public double overcrowdedPeople(){var sizes=sizes();double n=0;for(var e:overcrowded.entrySet())n+=e.getValue()*sizes.getOrDefault(e.getKey(),0);return n;}
    public double sharingGroups(){return sharing.stream().mapToDouble(Sharing::groups).sum();}
    public List<HousingChoice.Demand> demands(){return demand;}
    /** The last clearing's doors as offered (not saved: a load has none until its next clearing). */
    public List<HousingChoice.Offer> offers(){return offers;}
    public boolean active() { return month!=Integer.MIN_VALUE; }
    public int clearedMonth() { return month; }
    public List<HousingChoice.Lease> leases() { return leases; }
    public List<ClassState> classes() { return classes.values().stream().map(ClassState::copy).toList(); }
    public HousingChoice.Result result() { return result; }
    public double rentFor(String cell,double count) { return count>0?leases.stream().filter(l->l.cell().equals(cell)||l.cell().startsWith(cell+"#")).mapToDouble(HousingChoice.Lease::bill).sum()/count:0; }
    public double receipts() { return leases.stream().mapToDouble(HousingChoice.Lease::bill).sum(); }
    /**
     * THE ASKED BURDEN (RES3c, 2026-11-25): what a mover weighs, every household's cheapest suitable class's ask, the
     * rent its next vacant door asks, over the city's take-home pay; NaN off the typed market. A class asks one rent,
     * let or vacant, so a class whose doors are all let still names the price of the next one built.
     * WHY: the paid burden, receipts over take-home, counts what doubled-up households pay. On the 1900 city 41% of
     * households paid nothing as guests and lodgers, the paid burden fell to 0.296 (main 0.717), and Migration read
     * a cheap city where the House asked its anchor. A mover weighs the asking rent (the orchestrator's call). The
     * doors are read from the saved classes, not the clearing's offers, which a load does not keep.
     * A household that fits two to a share in a class's door (HousingChoice.halves(), within the census line) weighs the
     * cheaper of the door's ask and a paid half share's, half the ask: a room in a shared house is an asked rent too, and
     * only a guest pays nothing (the orchestrator's call, 2026-11-25).
     */
    public double askedBurden(Game game,double disposable){return asked(game,disposable,true);}
    /** The same with every household asked a whole door's rent: what a household alone weighs, before a half share is open to it. */
    public double aloneBurden(Game game,double disposable){return asked(game,disposable,false);}
    private double asked(Game game,double disposable,boolean shares){
        if(!active()||!(disposable>0))return Double.NaN;
        double asked=0;var doors=supply(game);
        for(var d:demand){double best=Double.POSITIVE_INFINITY;
            for(var o:doors)if(o.count()>0&&HousingChoice.suitable(d,o,false))best=Math.min(best,shares&&HousingChoice.halves(d,o)?o.rent()/2:o.rent());
            if(Double.isFinite(best))asked+=d.count()*best;}
        return asked/disposable;
    }
    public double rentPriceIndex() { return rentPriceIndex; }
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
            // Who holds a share, two to a door: a lodger holds none (lodging()), so it is neither a slot taken nor displaced.
            double assigned=leases.stream().filter(l->l.home().equals(c.key())).mapToDouble(HousingMarket::shares).sum();
            // A demolition/title transfer can precede the next monthly clearing.
            // Reserve other rooms for its displaced tenants before admitting arrivals.
            double lost=Math.max(0,assigned-2*owned);
            double parents=leases.stream().filter(l->l.home().equals(c.key())&&familyCells.getOrDefault(l.cell(),false))
                    .mapToDouble(HousingMarket::shares).sum();
            double lostFamilies=Math.min(lost,Math.max(0,parents-2*owned));
            children+=lostFamilies;adults+=lost-lostFamilies;
            double slots=Math.max(0,2*owned-assigned);
            if(c.capacity>FamilyModel.STUDIO_MAX_SIZE)family+=slots;else compact+=slots;
        }
        // Who lives past the census line (overcrowding()) still seeks a door of its own: its room is kept as the doorless's.
        if(result.unplaced()>0||!overcrowded.isEmpty())for(var d:demand){double taken=leases.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
            double missing=Math.max(0,d.count()-taken+overcrowded.getOrDefault(d.key(),0.0));if(d.children()>0)children+=missing;else adults+=missing;}
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
    /** Strikes each class's capital at the home's market value and its carry; no second construction timer. */
    private void quote(Game game) {
        var bm=game.getBuildingManager();var em=game.getEconomyManager();var re=game.getSectors().realEstate();
        double totalBasis=0;
        Map<Integer,Double> carry=new HashMap<>();
        Set<HousingInventory.Key> opening=new HashSet<>();
        for(BuildingsTemplate t:bm.getTemplates()){
            if(!re.key().equals(t.getSector()))continue;
            // The capital a landlord's gross yield is struck on is the home's market value, the base its repairs and property
            // tax are on (EconomyManager.housingStructureValue(), RES3c): not the door, which charges only the materials beyond
            // the builders' yard, so a dated 1900 block asked a yield on under two thirds of the value its carry was struck on.
            double structure=em.housingStructureValue(t);
            double land=em.housingLandValue(t);
            double full=structure+land;
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
                if(initial||(!c.available&&known))opening.add(key);
                c.available=known;
            }
        }
        var stock=bm.getHousingInventory().stock();
        Map<HousingInventory.Key,Double> onSite=new HashMap<>();
        for(ClassState c:classes.values()){c.homes=0;c.carry=0;}
        for(var s:stock){ClassState c=classes.get(s.key());if(c==null)continue;
            c.homes=bm.getHousingInventory().owned(s.key(),HousingInventory.Owner.REAL_ESTATE);
            onSite.put(s.key(),s.pipeline());
            totalBasis+=(c.homes+s.pipeline())*c.capital;}
        // An unbuilt form is absent from physical stock, but still has a real
        // operating quote. Compare the gross market anchor with actual cost recovery.
        for(BuildingsTemplate t:bm.getTemplates()){
            HousingRecipe recipe=t.getHousingRecipe();if(!re.key().equals(t.getSector())||recipe==null)continue;
            for(var d:recipe.mix()){
                var key=new HousingInventory.Key(t.getId(),d.id());ClassState c=classes.get(key);
                double share=d.costWeight()/recipe.costWeight();
                // Actual titles/sites partition debt once. An unbuilt blueprint
                // cannot inherit another class's loans or promise their rent.
                double debt=totalBasis>0&&c.homes+onSite.getOrDefault(key,0.0)>0?re.carriedInterest()*c.capital/totalBasis:0;
                c.carry=carry.getOrDefault(t.getId(),0.0)*share+debt;
            }
        }
        for(ClassState c:classes.values())if(opening.contains(c.key())){
            double anchor=c.capital*RealEstate.LANDLORD_YIELD/12;
            c.ask=c.homes==0?Math.max(anchor,c.carry):anchor;
        }
    }
    public HousingChoice.Result clear(Game game,List<HousingChoice.Demand> cells){
        return clear(game,cells,HouseholdBalance.HousingRoute.NONE);
    }
    /** The month's clearing, the households that changed cell taking their doors along the route (carry()). */
    public HousingChoice.Result clear(Game game,List<HousingChoice.Demand> cells,HouseholdBalance.HousingRoute route){
        if(month==game.getMonth())return result;
        leases=carry(leases,route);
        quote(game);
        var inventory=game.getBuildingManager().getHousingInventory();
        demand=List.copyOf(cells);offers=supply(game);
        hosted=0;arrivalProjection=false;formingFamilies=formingAdults=0;
        List<HousingChoice.Offer> pending=new ArrayList<>();
        for(var s:inventory.stock())if(s.pipeline()>0){var d=s.dwelling();var f=HousingForms.of(s.key().recipe());
            pending.add(new HousingChoice.Offer(s.key(),s.pipeline(),d.capacity(),d.bedrooms(),d.area(),d.quality(),
                    f==null?0:f.garden(),f!=null&&f.stepFree(),price(s.key()),0));}
        pipeline=List.copyOf(pending);
        result=HousingChoice.clear(demand,offers,leases,game.getMonth(),true,sitting(game));
        retainHosted();
        emergencySharing(game);
        emergencyDoubling(game);
        if(completeNormalFootprints())emergencyDoubling(game);
        if(completePaidExcess(game.getMonth()))emergencyDoubling(game);
        lodging();
        var housed=result.leases();
        overcrowding(game.getMonth());
        leases=result.leases();month=game.getMonth();
        strikePriceDemand(offers,housed);
        Map<HousingInventory.Key,Double> actual=new LinkedHashMap<>();
        for(var s:inventory.stock())actual.put(s.key(),classes.get(s.key()).occupied);
        inventory.matchClasses(actual);
        return result;
    }
    /** Current physical occupancy and funded whole-door pressure, from the clearing's actual budget profiles. */
    void strikePriceDemand(List<HousingChoice.Offer> pricedOffers){strikePriceDemand(pricedOffers,leases);}
    /** housed: the census line's placements. Who lives past it (overcrowding()) still presses for a door, as it did with none. */
    private void strikePriceDemand(List<HousingChoice.Offer> pricedOffers,List<HousingChoice.Lease> housed){
        for(ClassState c:classes.values()){c.occupied=0;c.demand=0;}
        for(var l:leases){ClassState c=classes.get(l.home());c.occupied+=l.count();c.demand+=l.count();}
        // Emergency shelter does not supply a door of one's own. Occupied physical shares
        // plus the remaining funded demand count each household once, including guests.
        // Send that residual to its best compatible affordable class, never every alternative.
        for(var d:demand){
            // The transfer has pooled both counts and resources into this cell. Its old target budget cannot fund
            // every newly added household; price pressure uses the same current budget the clearing can actually pay.
            final var seeker=d;
            double used=0;
            for(var l:housed)if(l.cell().equals(d.key())){
                used+=l.households();
                var home=pricedOffers.stream().filter(o->o.key().equals(l.home())).findFirst().orElseThrow();
                // Funded doors include a host's donated room. Physical shares would
                // count that donation again even when its guest cannot pay.
                if(HousingChoice.suitable(seeker,home,false)&&HousingChoice.affordable(seeker,home)){
                    double funded=home.rent()>0?l.bill()/home.rent():l.households();
                    classes.get(l.home()).demand+=Math.max(0,l.households()-funded);
                }
            }
            double left=Math.max(0,d.count()-used),best=Double.NEGATIVE_INFINITY;
            if(left<=FamilyModel.packingRounding(d.count()))continue;
            List<HousingChoice.Offer> tied=new ArrayList<>();
            for(var o:pricedOffers)if(HousingChoice.suitable(seeker,o,false)&&HousingChoice.affordable(seeker,o)){
                double score=HousingChoice.score(seeker,o);int order=Double.compare(score,best);
                if(order>0){best=score;tied.clear();}if(order>=0)tied.add(o);
            }
            // Equal preferences have no catalogue-order entitlement. Split the one-count pressure
            // by comparable supply, with one quote unit for a class not built yet.
            double weight=tied.stream().mapToDouble(o->Math.max(1,o.count())).sum();
            for(var o:tied)classes.get(o.key()).demand+=left*Math.max(1,o.count())/weight;
        }
    }
    private record LeaseKey(String cell,HousingInventory.Key home) { }
    /**
     * TENURE FOLLOWS THE PEOPLE (RES3c, 2026-11-25). A lease is a cell's, and a household that changes cell (a child
     * born, a job lost, a pension drawn) would leave its door behind for a stranger and seek a new one with what it
     * has now: a working family of six at a zero budget had no door although it never moved out. So each household
     * that changes cell takes its share of its old cell's leases, as it takes its share of the cell's savings
     * (HouseholdBalance.housingRoute(), the money's own route). Whether it keeps the door is the clearing's: a sitter
     * keeps it within the census line on its arrears, an evictable cell within its budget (HousingChoice).
     */
    static List<HousingChoice.Lease> carry(List<HousingChoice.Lease> leases,HouseholdBalance.HousingRoute route){
        if(route.moved().isEmpty())return leases;
        List<HousingChoice.Lease> out=new ArrayList<>();
        for(var l:leases){var to=route.moved().get(l.cell());double b=route.before().getOrDefault(l.cell(),0.0);
            if(to==null||!(b>0)){out.add(l);continue;}
            double sent=0;
            for(var e:to.entrySet()){double f=Math.min(1-sent,e.getValue()/b);if(!(f>0))continue;sent+=f;
                var q=l.scale(f);out.add(new HousingChoice.Lease(e.getKey(),q.home(),q.count(),q.bill(),q.households(),q.funding()));}
            if(sent<1)out.add(l.scale(1-sent));
        }
        return HousingChoice.coalesce(out);
    }
    /** A lease's households that hold a share of its doors, two to a door; the rest are its lodgers. */
    public static double shares(HousingChoice.Lease l){return Math.min(l.households(),2*l.count());}
    /** Every known class's standing doors at its ask, as the clearing offers them. */
    private List<HousingChoice.Offer> supply(Game game){
        List<HousingChoice.Offer> supply=new ArrayList<>();
        for(var t:game.getBuildingManager().getTemplatesBySector(Sectors.REAL_ESTATE)){
            if(game.foundedUnfitted()&&!t.knownAt(game.getMonth(),game.getResearch()))continue;
            var recipe=t.getHousingRecipe();if(recipe==null)continue;
            for(var d:recipe.mix()){
            var key=new HousingInventory.Key(t.getId(),d.id());ClassState c=classes.get(key);if(c==null)continue;var f=HousingForms.of(t.getId());
            supply.add(new HousingChoice.Offer(key,c.homes,d.capacity(),d.bedrooms(),d.area(),d.quality(),
                    f==null?0:f.garden(),f!=null&&f.stepFree(),c.ask,0));
            }
        }
        return List.copyOf(supply);
    }
    /**
     * TENURE (RES3c, 2026-11-25). Main's rule is Jerus's (2026-09-11): an incumbent keeps its door until it is past
     * EI with its savings gone. A cell that cannot yet be evicted sits on, and its unpaid rent is booked as main books
     * it, the landlord's arrears (the bill stays due; HouseholdBalance counts what it could not fund); only a cell that
     * can be evicted keeps no more than its budget funds, its savings in the budget.
     * WHY: kept on the budget alone, a family on EI lost its door the month its budget fell, and the 1900 city's first
     * crunch month put 13 households with no home where main had none (CreditCrunchCheck 3). An eviction takes months
     * of arrears, and the landlord carries them.
     */
    static Set<String> sitting(Game game){
        Set<String> sitting=new HashSet<>();if(game==null||game.getHouseholdBalance()==null)return sitting;
        for(Household c:game.getHouseholdBalance().cells())if(!c.canBeEvicted()){sitting.add(c.key());sitting.add(c.key()+"#sharing");}
        return sitting;
    }

    /* ----- host places within the census line (RES3c, 2026-11-25) ----- */
    /** The places beside lone host households of one size on one class of home. */
    private record HostKey(HousingInventory.Key home,int size) { }
    /**
     * A lone household on its door leaves the one place beside it, two to a door; a guest takes it only if the pair
     * stays within the door's census line (HousingChoice.PERSONS_PER_ROOM), so the places are kept by the host's size.
     * WHY: kept by class alone, a family of six doubled into a two-bedroom door beside another four, ten in three rooms.
     */
    private static Map<HostKey,Double> hostPlaces(List<HousingChoice.Lease> all,Map<String,Integer> sizes){
        Map<HostKey,Double> places=new TreeMap<>(Comparator.comparing((HostKey k)->k.home().toString()).thenComparingInt(HostKey::size));
        for(var l:all){Integer size=sizes.get(l.cell());double n=Math.max(0,2*l.count()-l.households());
            if(size!=null&&n>0)places.merge(new HostKey(l.home(),size),n,Double::sum);}
        return places;
    }
    /** Hosts give up the places their guests took: half a door a place, from the leases that gave it pro rata. */
    private static void giveUp(List<HousingChoice.Lease> all,Map<String,Integer> sizes,Map<HostKey,Double> places,Map<HostKey,Double> taken){
        for(int i=0;i<all.size();i++){var l=all.get(i);Integer size=sizes.get(l.cell());if(size==null)continue;
            var key=new HostKey(l.home(),size);double n=taken.getOrDefault(key,0.0);if(!(n>0))continue;
            all.set(i,new HousingChoice.Lease(l.cell(),l.home(),l.count()-n*Math.max(0,2*l.count()-l.households())/(2*places.get(key)),l.bill(),l.households(),l.funding()));}
    }
    /** A host place as an offer: the home as built, free, open to one the census line still fits beside its host. */
    private static HousingChoice.Offer hostRoom(HousingInventory.Key key,HousingChoice.Offer o,int host,double n){
        return new HousingChoice.Offer(key,n,o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),0,0,o.people()-host);
    }
    private Map<String,Integer> sizes(){Map<String,Integer> sizes=new HashMap<>();for(var d:demand)sizes.put(d.key(),d.size());return sizes;}
    private Map<HousingInventory.Key,HousingChoice.Offer> homes(){Map<HousingInventory.Key,HousingChoice.Offer> homes=new HashMap<>();for(var o:offers)homes.put(o.key(),o);return homes;}

    /** Prior guests keep priority only in the real room funded by this clearing's paying hosts, beside a host the line fits. */
    private void retainHosted(){
        Map<String,HousingChoice.Demand> cells=new HashMap<>();for(var d:demand)cells.put(d.key(),d);
        var homes=homes();var sizes=sizes();
        List<HousingChoice.Lease> all=new ArrayList<>(result.leases());
        Map<String,Double> prior=new HashMap<>(),left=new HashMap<>();
        for(var d:demand){double placed=all.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();left.put(d.key(),Math.max(0,d.count()-placed));}
        Map<LeaseKey,Double> claims=new LinkedHashMap<>();
        for(var l:leases){var c=classes.get(l.home());var d=cells.get(l.cell());var o=homes.get(l.home());
            if(c==null||d==null||o==null||!HousingChoice.suitable(d,o,true))continue;
            // Only the doubling's guests hold half a door to keep. A lodger has none: it lodges again after the doubling
            // (lodging()), and given this room first it would take the share a guest who can pay needs.
            double guests=Math.min(l.funding().sponsored(),2*Math.max(0,l.count()-l.funding().doors()));
            if(!(guests>0))continue;claims.merge(new LeaseKey(l.cell(),l.home()),guests,Double::sum);prior.merge(l.cell(),guests,Double::sum);
        }
        var places=hostPlaces(all,sizes);
        // A claim asks of the host sizes it fits beside, pro rata to their places; each host size rations its own places.
        Map<LeaseKey,Map<HostKey,Double>> asks=new LinkedHashMap<>();Map<HostKey,Double> asked=new HashMap<>(),taken=new HashMap<>();
        for(var e:claims.entrySet()){var k=e.getKey();double n=e.getValue()*Math.min(1,left.getOrDefault(k.cell(),0.0)/prior.get(k.cell()));
            int guest=cells.get(k.cell()).size(),line=homes.get(k.home()).people();
            Map<HostKey,Double> fits=new LinkedHashMap<>();double room=0;
            for(var p:places.entrySet())if(p.getKey().home().equals(k.home())&&guest+p.getKey().size()<=line){fits.put(p.getKey(),p.getValue());room+=p.getValue();}
            if(!(n>0)||!(room>0))continue;
            Map<HostKey,Double> ask=new LinkedHashMap<>();
            for(var f:fits.entrySet()){double a=n*f.getValue()/room;ask.put(f.getKey(),a);asked.merge(f.getKey(),a,Double::sum);}
            asks.put(k,ask);
        }
        List<HousingChoice.Lease> guests=new ArrayList<>();double extra=0;
        for(var e:asks.entrySet()){double n=0;
            for(var a:e.getValue().entrySet()){double got=a.getValue()*Math.min(1,places.get(a.getKey())/asked.get(a.getKey()));n+=got;taken.merge(a.getKey(),got,Double::sum);}
            if(!(n>0))continue;
            guests.add(new HousingChoice.Lease(e.getKey().cell(),e.getKey().home(),n/2,0,n,new HousingChoice.Funding(0,0,n)));extra+=n;}
        giveUp(all,sizes,places,taken);
        all.addAll(guests);hosted+=extra;
        result=HousingChoice.summarize(demand,offers,bounded(all),result.kept(),result.suitable(),result.crowded()+extra,Map.of());
    }

    /**
     * LODGING (RES3c, 2026-11-25). Who funds no share of any standing home boards with the housed, beyond two to a
     * share and up to the census line (HousingChoice.PERSONS_PER_ROOM), the hosts carrying its share: no bill, no new
     * money, no payer added, and the doors occupied stay the doors occupied. The market keeps no single door's count,
     * so a class's people are taken as spread evenly over its occupied doors: a lodger boards only where they keep room
     * for it, and the class's people never pass the line.
     *
     * WHY: the doubling places a guest two to a share and no further, so a household past its EI with its savings
     * gone was left with no home at all: on the 1900 city one EI cohort's expiry left 252 households with no door in
     * a month (m253) beside family doors with people's room to spare. 1900's unemployed doubled up and took lodging:
     * a fifth to a third of urban households took boarders (Modell and Hareven 1973, "Urbanization and the Malleable
     * Household"), and relief was negligible, so nobody new pays for it. Who can fund a share and finds none waits
     * for the building, as before (CreditCrunchCheck 3).
     */
    private void lodging(){
        Map<String,HousingChoice.Demand> cells=new HashMap<>();for(var d:demand)cells.put(d.key(),d);
        var homes=homes();
        Map<LeaseKey,HousingChoice.Lease> all=new LinkedHashMap<>();
        Map<String,Double> placed=new HashMap<>();Map<HousingInventory.Key,Double> room=new HashMap<>(),doors=new HashMap<>();
        for(var l:result.leases()){
            all.merge(new LeaseKey(l.cell(),l.home()),l,HousingChoice.Lease::plus);placed.merge(l.cell(),l.households(),Double::sum);
            var d=cells.get(l.cell());var o=homes.get(l.home());
            if(d!=null&&o!=null){room.merge(l.home(),o.people()*l.count()-d.size()*l.households(),Double::sum);doors.merge(l.home(),l.count(),Double::sum);}
        }
        double extra=0;
        for(var d:demand){
            double need=d.count()-placed.getOrDefault(d.key(),0.0);
            if(need<=FamilyModel.packingRounding(d.count())||paysAShare(d))continue;
            var boards=offers.stream().filter(o->HousingChoice.suitable(d,o,true)&&room.getOrDefault(o.key(),0.0)>0)
                    .sorted(Comparator.comparingDouble(o->-HousingChoice.score(d,o))).toList();
            for(var o:boards){
                double r=room.get(o.key());if(d.size()>r/doors.get(o.key()))continue;
                double n=Math.min(need,r/d.size());if(!(n>0))continue;
                all.merge(new LeaseKey(d.key(),o.key()),new HousingChoice.Lease(d.key(),o.key(),0,0,n,new HousingChoice.Funding(0,0,n)),HousingChoice.Lease::plus);
                room.merge(o.key(),-n*d.size(),Double::sum);need-=n;extra+=n;
                if(need<=FamilyModel.packingRounding(d.count()))break;
            }
        }
        if(!(extra>0))return;
        hosted+=extra;
        Map<String,Double> oldBudgets=new HashMap<>();for(var shift:sharing)oldBudgets.put(shift.target(),shift.targetBudget());
        result=HousingChoice.summarize(demand,offers,new ArrayList<>(all.values()),result.kept(),result.suitable(),result.crowded()+extra,oldBudgets);
    }
    /** Whether a cell's budget funds half a share it fits of a home that stands: who does is not a lodger. */
    private boolean paysAShare(HousingChoice.Demand d){
        for(var o:offers)if(o.count()>0&&HousingChoice.halves(d,o)&&(o.rent()+o.recurring())/2<=d.budget())return true;
        return false;
    }

    /**
     * OVERCROWDED (RES3c, 2026-11-25). A household the census line leaves with no door lives past the line, never
     * doorless: the censuses of 1891 and 1901 counted 7.04 million families in 6.26 million houses in England and Wales,
     * and called more than two persons a room overcrowded, not homeless (the orchestrator's call A). It takes the nearest
     * door or share past the line, by the placement's own score (HousingChoice.clear() on offers with the line lifted,
     * every other test kept): a door still standing at its ask, then half of one at half of it, for one whose budget
     * funds it; one whose budget funds neither, or finds neither standing, lodges with the housed as lodging() does, no
     * bill and no new money, a household beside each occupied door a round. It is the last pass: every placement the
     * line has room for is settled before it, who can fund a share it fits still waits for the building (lodging()),
     * and retainAndClear() keeps a door within the line only, so it runs again at each clearing. Who it houses is
     * counted overcrowded (overcrowded(), saved with the state), still seeks a door of its own (roomLeft()) and still
     * presses the rent as when it had none (strikePriceDemand()).
     *
     * WHY: with every door full and the ground gone, the line left HousingDoorCheck's town (AUTO2 seed 12) 47.986
     * households with no door at m888, every one a family of six with no door the line fits beside another, and with
     * the room mend 114.574 at m897: the line had become a law of homelessness the censuses never counted.
     */
    private void overcrowding(int now){
        overcrowded=Map.of();
        List<HousingChoice.Lease> all=new ArrayList<>(result.leases());
        Map<String,Double> placed=new HashMap<>();for(var l:all)placed.merge(l.cell(),l.households(),Double::sum);
        List<HousingChoice.Demand> left=new ArrayList<>();
        for(var d:demand){double n=d.count()-placed.getOrDefault(d.key(),0.0);
            if(n>FamilyModel.packingRounding(d.count())&&!paysAShare(d))left.add(with(d,n,d.budget()));}
        // A residue within the whole match's packing resolution is nobody (summarize()): it is housed past no line.
        double source=demand.stream().mapToDouble(HousingChoice.Demand::count).sum();
        if(left.isEmpty()||left.stream().mapToDouble(HousingChoice.Demand::count).sum()<=FamilyModel.packingRounding(source))return;
        Map<HousingInventory.Key,Double> occupied=new HashMap<>();for(var l:all)occupied.merge(l.home(),l.count(),Double::sum);
        Map<String,Double> crowded=new LinkedHashMap<>();double extra=0,lodged=0;
        // A door still standing at its ask, then half of one at half of it.
        for(int share=1;share<=2&&!left.isEmpty();share++){
            List<HousingChoice.Offer> vacant=new ArrayList<>();
            for(var o:offers){double n=Math.max(0,o.count()-occupied.getOrDefault(o.key(),0.0));if(n>0)vacant.add(pastTheLine(o,share*n,share));}
            if(vacant.isEmpty())break;
            var taken=HousingChoice.clear(left,vacant,List.of(),now).leases();
            for(var l:taken){double n=l.count(),doors=n/share;
                all.add(share==1?l:new HousingChoice.Lease(l.cell(),l.home(),doors,l.bill(),n,new HousingChoice.Funding(0,n,0)));
                occupied.merge(l.home(),doors,Double::sum);crowded.merge(l.cell(),n,Double::sum);extra+=n;}
            left=remaining(left,taken);
        }
        // Who pays for neither lodges: a household beside each occupied door a round, nearest first.
        while(!left.isEmpty()){
            List<HousingChoice.Offer> doors=new ArrayList<>();
            for(var o:offers){double n=occupied.getOrDefault(o.key(),0.0);if(n>0)doors.add(pastTheLine(o,n,0));}
            var taken=doors.isEmpty()?List.<HousingChoice.Lease>of():HousingChoice.clear(left,doors,List.of(),now).leases();
            double round=0,before=left.stream().mapToDouble(HousingChoice.Demand::count).sum();
            for(var l:taken){double n=l.count();
                all.add(new HousingChoice.Lease(l.cell(),l.home(),0,0,n,new HousingChoice.Funding(0,0,n)));crowded.merge(l.cell(),n,Double::sum);round+=n;}
            if(!(round>FamilyModel.packingRounding(before)))break;
            extra+=round;lodged+=round;left=remaining(left,taken);
        }
        if(!(extra>0))return;
        Map<LeaseKey,HousingChoice.Lease> merged=new LinkedHashMap<>();
        for(var l:all)merged.merge(new LeaseKey(l.cell(),l.home()),l,HousingChoice.Lease::plus);
        // Each home's doors summed in summarize()'s order and held to the homes standing, as bounded() holds the other passes: a vacancy
        // read as stock less a floating sum, taken whole, can pass the stock by an ulp (D90 1,988.0000000000002 of 1,988, RES3c 2026-11-25,
        // which the inventory's match refused mid-month). The lodgers and the households stay; only the door-equivalents and their bill trim.
        Map<HousingInventory.Key,Double> limits=new HashMap<>(),used=new HashMap<>();for(var o:offers)limits.put(o.key(),o.count());
        List<HousingChoice.Lease> held=new ArrayList<>();
        for(var l:merged.values()){double before=used.getOrDefault(l.home(),0.0),limit=limits.getOrDefault(l.home(),0.0),n=l.count();
            if(before+n>limit){double fit=Math.max(0,limit-before);while(fit>0&&before+fit>limit)fit=Math.nextDown(fit);
                if(n-fit>FamilyModel.packingRounding(limit))throw new IllegalStateException("Housing overcrowding exceeds physical homes");
                double f=fit/n;var fu=l.funding();
                l=new HousingChoice.Lease(l.cell(),l.home(),fit,l.bill()*f,l.households(),new HousingChoice.Funding(fu.full()*f,fu.half()*f,fu.sponsored()));}
            used.put(l.home(),before+l.count());held.add(l);}
        hosted+=lodged;overcrowded=Collections.unmodifiableMap(crowded);
        Map<String,Double> oldBudgets=new HashMap<>();for(var shift:sharing)oldBudgets.put(shift.target(),shift.targetBudget());
        result=HousingChoice.summarize(demand,offers,held,result.kept(),result.suitable(),result.crowded()+extra,oldBudgets);
    }
    /** A class's doors offered past the census line, at its ask over share (0: a lodger's, no bill), every other test kept. */
    private static HousingChoice.Offer pastTheLine(HousingChoice.Offer o,double n,int share){
        return new HousingChoice.Offer(o.key(),n,o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),
                share>0?o.rent()/share:0,share>0?o.recurring()/share:0,Integer.MAX_VALUE);
    }
    /** What a pass left of its households. */
    private static List<HousingChoice.Demand> remaining(List<HousingChoice.Demand> left,List<HousingChoice.Lease> taken){
        Map<String,Double> got=new HashMap<>();for(var l:taken)got.merge(l.cell(),l.households(),Double::sum);
        List<HousingChoice.Demand> rest=new ArrayList<>();
        for(var d:left){double n=d.count()-got.getOrDefault(d.key(),0.0);if(n>FamilyModel.packingRounding(d.count()))rest.add(with(d,n,d.budget()));}
        return rest;
    }

    /** A doubling node: a home's places beside one host size, or (host null) its vacant doors' paid halves. */
    private record EmergencyRoom(HousingChoice.Offer home,HostKey host) { }
    /** Guests and hosts retain separate household counts while sharing one actual door and rent bill. */
    private void emergencyDoubling(Game game){
        List<HousingChoice.Lease> all=new ArrayList<>(result.leases());double extra=0;
        List<HousingChoice.Demand> without=new ArrayList<>();
        for(var d:demand){double used=all.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
            double n=Math.max(0,d.count()-used);if(n>FamilyModel.packingRounding(d.count()))without.add(with(d,n,d.budget()));}
        // Match free host places and paid vacant shares together. Otherwise a guest
        // who can fund a vacant share can strand a poor guest in the free-host pass.
        // Same-kind five-adult groups were formed in emergencySharing; these guests
        // keep their existing household cells. Negative keys are local flow nodes,
        // translated back to their actual comparable classes before any lease is kept.
        List<HousingChoice.Offer> rooms=new ArrayList<>();
        Map<HousingInventory.Key,EmergencyRoom> nodes=new HashMap<>();
        // Host places are kept by the host's size and a vacant door's halves at half its line, so no pair passes it.
        var sizes=sizes();var homes=homes();var places=hostPlaces(all,sizes);
        for(var p:places.entrySet()){var o=homes.get(p.getKey().home());if(o==null||o.people()-p.getKey().size()<1)continue;
            var key=new HousingInventory.Key(-1-rooms.size(),o.key().dwelling());
            rooms.add(hostRoom(key,o,p.getKey().size(),p.getValue()));nodes.put(key,new EmergencyRoom(o,p.getKey()));}
        for(var o:offers){
            double occupied=all.stream().filter(l->l.home().equals(o.key())).mapToDouble(HousingChoice.Lease::count).sum();
            double n=2*Math.max(0,o.count()-occupied);if(!(n>0))continue;
            var key=new HousingInventory.Key(-1-rooms.size(),o.key().dwelling());
            rooms.add(new HousingChoice.Offer(key,n,o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent()/2,o.recurring()/2,o.people()/2));
            nodes.put(key,new EmergencyRoom(o,null));
        }
        var guests=HousingChoice.clear(without,rooms,List.of(),game.getMonth());
        Map<HostKey,Double> takenHosts=new HashMap<>();
        for(var l:guests.leases()){var node=nodes.get(l.home());if(node.host()!=null)takenHosts.merge(node.host(),l.count(),Double::sum);}
        giveUp(all,sizes,places,takenHosts);
        for(var l:guests.leases()){
            var node=nodes.get(l.home());double n=l.count();boolean host=node.host()!=null;
            all.add(new HousingChoice.Lease(l.cell(),node.home().key(),n/2,l.bill(),n,new HousingChoice.Funding(0,host?0:n,host?n:0)));extra+=n;
        }
        hosted+=takenHosts.values().stream().mapToDouble(Double::doubleValue).sum();
        Map<LeaseKey,HousingChoice.Lease> merged=new LinkedHashMap<>();
        for(var l:all){var key=new LeaseKey(l.cell(),l.home());var previous=merged.get(key);
            merged.put(key,previous==null?l:previous.plus(l));}
        all=bounded(new ArrayList<>(merged.values()));
        Map<String,Double> oldBudgets=new HashMap<>();for(var shift:sharing)oldBudgets.put(shift.target(),shift.targetBudget());
        result=HousingChoice.summarize(demand,offers,all,result.kept(),result.suitable(),result.crowded()+extra,oldBudgets);
    }
    /** Same-class normal space follows all movers; only actual paid-half contracts can expand. */
    private boolean completeNormalFootprints(){
        List<HousingChoice.Lease> before=result.leases(),all=new ArrayList<>(before);
        Map<String,HousingChoice.Demand> cells=new HashMap<>();for(var d:demand)cells.put(d.key(),d);
        Map<HousingInventory.Key,HousingChoice.Offer> homes=new HashMap<>();for(var o:offers)homes.put(o.key(),o);
        Map<HousingInventory.Key,Double> used=new HashMap<>(),wanted=new HashMap<>();
        double[] need=new double[before.size()];
        for(int i=0;i<before.size();i++){
            var l=before.get(i);used.merge(l.home(),l.count(),Double::sum);
            var d=cells.get(l.cell());var o=homes.get(l.home());
            if(d==null||o==null||!(l.bill()>0)||!(o.rent()>0)||!HousingChoice.suitable(d,o,false)||!HousingChoice.affordable(d,o))continue;
            double n=l.funding().half()/2;
            if(n<=FamilyModel.packingRounding(l.households()))continue;
            need[i]=n;wanted.merge(l.home(),n,Double::sum);
        }
        double expanded=0;
        for(int i=0;i<before.size();i++)if(need[i]>0){
            var l=before.get(i);var o=homes.get(l.home());
            double free=Math.max(0,o.count()-used.get(l.home()));
            double n=Math.min(l.households(),l.count()+need[i]*Math.min(1,free/wanted.get(l.home())));
            while(n>l.count()&&(n-l.count()>need[i]||l.bill()+(n-l.count())*o.rent()>l.households()*o.rent()))n=Math.nextDown(n);
            if(!(n>l.count()))continue;
            for(;;){
                double bill=l.bill()+(n-l.count())*o.rent();
                double converted=2*(n-l.count());var f=l.funding();
                all.set(i,new HousingChoice.Lease(l.cell(),l.home(),n,bill,l.households(),new HousingChoice.Funding(f.full()+converted,Math.max(0,f.half()-converted),f.sponsored())));
                double total=0;for(var q:all)if(q.home().equals(l.home()))total+=q.count();
                if(total<=o.count())break;
                double excess=total-o.count();
                if(excess>FamilyModel.packingRounding(total))throw new IllegalStateException("Normal completion exceeds current homes");
                n=Math.max(l.count(),Math.nextDown(n-excess));
            }
            expanded+=n-l.count();
        }
        if(!(expanded>0))return false;
        if(expanded-result.crowded()>FamilyModel.packingRounding(expanded))throw new IllegalStateException("Normal completion exceeds paid crowding");
        double crowded=Math.max(0,result.crowded()-expanded);
        if(hosted-crowded>FamilyModel.packingRounding(hosted))throw new IllegalStateException("Normal completion erases hosted provenance");
        Map<String,Double> oldBudgets=new HashMap<>();for(var shift:sharing)oldBudgets.put(shift.target(),shift.targetBudget());
        result=HousingChoice.summarize(demand,offers,all,result.kept(),result.suitable()+expanded,crowded,oldBudgets);
        return true;
    }


    private record Pool(Household source,HousingChoice.Demand original,HousingChoice.Demand target) { }
    /** New normal contracts follow every mover and same-class completion, without releasing old claims. */
    private boolean completePaidExcess(int currentMonth){
        var completed=HousingChoice.completePaidExcess(demand,offers,result.leases(),currentMonth);
        if(!(completed.moved()>0))return false;
        double crowded=result.crowded()-completed.moved();
        if(crowded<0||hosted-crowded>FamilyModel.packingRounding(hosted))throw new IllegalStateException("Paid excess erases emergency provenance");
        Map<String,Double> oldBudgets=new HashMap<>();for(var shift:sharing)oldBudgets.put(shift.target(),shift.targetBudget());
        result=HousingChoice.summarize(demand,offers,completed.leases(),result.kept(),result.suitable()+completed.moved(),crowded,oldBudgets);
        return true;
    }
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
            double used=result.leases().stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
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
        // Host places by the host's size: a group shelters only beside a host the census line still fits.
        var sizes=sizes();for(var d:proposed)sizes.put(d.key(),d.size());var homes=homes();var places=hostPlaces(all,sizes);
        List<HousingChoice.Offer> hosts=new ArrayList<>();Map<HousingInventory.Key,HostKey> nodes=new HashMap<>();
        for(var p:places.entrySet()){var o=homes.get(p.getKey().home());if(o==null||o.people()-p.getKey().size()<1)continue;
            var key=new HousingInventory.Key(-1-hosts.size(),o.key().dwelling());hosts.add(hostRoom(key,o,p.getKey().size(),p.getValue()));nodes.put(key,p.getKey());}
        // Child families cannot use compact host rooms. Keep their actual remaining
        // compatible slots before adult groups' softer preference for family space.
        Set<String> pooled=new HashSet<>();for(var p:pools.values())pooled.add(p.original().key());
        double waitingChildren=0;int smallest=Integer.MAX_VALUE;
        for(var d:demand)if(d.children()>0&&!pooled.contains(d.key())){
            double taken=all.stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::households).sum();
            double n=Math.max(0,d.count()-taken);waitingChildren+=n;if(n>0)smallest=Math.min(smallest,d.size());
        }
        // Only a family place a waiting child family fits within the census line is kept for it.
        final int fits=smallest;
        java.util.function.Predicate<HousingChoice.Offer> forFamilies=o->o.capacity()>FamilyModel.STUDIO_MAX_SIZE&&o.people()>=fits;
        double familyHosts=hosts.stream().filter(forFamilies).mapToDouble(HousingChoice.Offer::count).sum();
        double reserved=Math.min(familyHosts,waitingChildren);
        if(reserved>0)for(int i=0;i<hosts.size();i++){var o=hosts.get(i);if(forFamilies.test(o))
            hosts.set(i,new HousingChoice.Offer(o.key(),o.count()*(1-reserved/familyHosts),o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),0,0,o.people()));}
        var sheltered=HousingChoice.clear(missing,hosts,List.of(),game.getMonth());
        Map<HostKey,Double> taken=new HashMap<>();
        for(var l:sheltered.leases())taken.merge(nodes.get(l.home()),l.households(),Double::sum);
        giveUp(all,sizes,places,taken);
        for(var l:sheltered.leases()){all.add(new HousingChoice.Lease(l.cell(),nodes.get(l.home()).home(),l.count()/2,0,l.count(),new HousingChoice.Funding(0,0,l.count())));hosted+=l.households();}
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
                all.set(i,new HousingChoice.Lease(before.key(),l.home(),l.count()*actual/q,l.bill()*actual/q,n,l.funding().scale(actual/q)));
            }
        }
        // No proposed-but-unplaced group becomes a household or receives a rental bill.
        all.removeIf(l->pools.containsKey(l.cell()));
        Map<LeaseKey,HousingChoice.Lease> coalesced=new LinkedHashMap<>();
        for(var l:all){var key=new LeaseKey(l.cell(),l.home());var before=coalesced.get(key);
            coalesced.put(key,before==null?l:before.plus(l));}
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
            used.put(l.home(),before+n);out.add(new HousingChoice.Lease(l.cell(),l.home(),n,l.bill()*n/l.count(),households,l.funding().scale(n/l.count())));}
        return out;
    }
    /** Forecast only: one disjoint source per current household and per incoming worker cohort. */
    public double candidateIncome(Game game,BuildingsTemplate t,long quantity){return forecast(game).income(t,quantity);}
    /** One planner read reserves standing homes and funded sites once for every alternative. */
    public Forecast forecast(Game game){
        if(game==null)return new Forecast(null,List.of());
        // These are the closing household quantities and budgets attached to the
        // held contracts. Undoing five-to-one formation here drops its funded bill.
        // The separate arriving-worker cohort below uses the pre-valve formed mix.
        List<HousingChoice.Demand> planning=new ArrayList<>(demand);
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
        // The tenants a landlord and its lender count on are the ones the town draws: never more than Migration's own
        // target over the people here, which a dear or unsafe town cuts. Posts alone projected the 1900 stalled twin
        // 479 heads to come at m40 (552 posts, 203 of them withheld, for 231 workers), its target 577 for 533 at m60,
        // and the slice lender passed 67 units from m9 to m53 on rent rolls those never-coming tenants filled
        // (RES3c, 2026-11-25).
        var drawn=game.getMigration();
        if(drawn.hasDrawn())newHeads=Math.min(newHeads,Math.max(0,drawn.getLastTarget()-game.getCohorts().total()));
        double jobs=game.getPopulationManager().getTotalJobs()+bm.getPostsWithheld()+bm.getJobsUnderConstruction();
        double scale=heads>0&&workers>0?Math.min(newHeads/heads,Math.max(0,jobs-workers)/workers):0;
        for(Household c:game.getHouseholdBalance().cells())if(c instanceof WorkingHousehold w){
            double n=formed.getOrDefault(c.key(),0.0)*scale;if(!(n>0))continue;
            double net=0;int kinds=0;
            for(JobType job:JobType.values())if(PayTier.of(job)==c.tier()){
                net+=wages[job.ordinal()]*Math.max(0,1-tax.payslipShare(WageBand.of(job)));kinds++;
            }
            double budget=game.getHouseholdBalance().incomingHousingBudget(c,kinds>0?net/kinds:0,game.getHouseholds(),game.getSectors().retail().getStoreSellPrice(),game.spendFactor());
            planning.add(new HousingChoice.Demand(c.key()+"#incoming",n,c.size(),c.shape().dependants(),false,c.tier().ordinal(),w.stage()==AdultStage.YOUNG,budget));
        }
        List<HousingChoice.Offer> committed=new ArrayList<>();
        var inventory=bm.getHousingInventory();
        for(var r:inventory.stock()){
            var template=bm.getTemplate(r.key().recipe());if(!bm.isTemplateKnown(template))continue;
            var d=r.dwelling();var f=HousingForms.of(r.key().recipe());
            committed.add(new HousingChoice.Offer(r.key(),inventory.owned(r.key(),HousingInventory.Owner.REAL_ESTATE)+r.pipeline(),d.capacity(),d.bedrooms(),d.area(),d.quality(),f==null?0:f.garden(),f!=null&&f.stepFree(),price(r.key()),0));
        }
        var reserved=HousingChoice.clear(planning,committed,leases,month,false,sitting(game));
        List<HousingChoice.Demand> remaining=new ArrayList<>();
        Map<HousingInventory.Key,HousingChoice.Offer> homes=new HashMap<>();for(var o:committed)homes.put(o.key(),o);
        // Actual normal movers and privately funded paid-half deficits are disjoint.
        // Held bills stay reserved; an extra claim cannot leave an old tenant unable to pay.
        for(var d:planning){double taken=0,cost=0,extra=0;
            for(var l:reserved.leases())if(l.cell().equals(d.key())){
                var o=homes.get(l.home());taken+=l.households();cost+=l.bill()+l.count()*o.recurring();
                if(HousingChoice.suitable(d,o,false)&&HousingChoice.affordable(d,o))extra+=l.funding().half()/2;
            }
            double n=Math.max(0,d.count()-taken)+extra;if(n<=FamilyModel.packingRounding(d.count()))continue;
            double budget=Math.min(d.budget(),Math.max(0,d.budget()*d.count()-cost)/n);
            remaining.add(with(d,n,budget));
        }
        Map<HousingInventory.Key,Double> used=new HashMap<>();
        for(var l:reserved.leases())used.merge(l.home(),l.count(),Double::sum);
        List<HousingChoice.Offer> spare=new ArrayList<>();
        for(var o:committed)spare.add(new HousingChoice.Offer(o.key(),Math.max(0,o.count()-used.getOrDefault(o.key(),0.0)),
                o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent(),o.recurring()));
        var covered=HousingChoice.clear(remaining,spare,List.of(),month,false);
        List<HousingChoice.Demand> unmet=new ArrayList<>();
        for(var d:remaining){double placed=covered.leases().stream().filter(l->l.cell().equals(d.key())).mapToDouble(HousingChoice.Lease::count).sum();
            double n=Math.max(0,d.count()-placed);if(n<=FamilyModel.packingRounding(d.count()))continue;
            unmet.add(new HousingChoice.Demand(d.key(),n,d.size(),d.children(),d.retired(),d.tier(),d.young(),d.budget()));
        }
        return new Forecast(game,unmet);
    }
    public final class Forecast {
        private final Game city;
        private final List<HousingChoice.Demand> remaining;
        private final Map<HousingInventory.Key,Double> asks;
        private Forecast(Game city,List<HousingChoice.Demand> remaining){this.city=city;this.remaining=List.copyOf(remaining);
            Map<HousingInventory.Key,Double> prices=new HashMap<>();for(var c:classes.values())prices.put(c.key(),c.ask);asks=Map.copyOf(prices);}
        public double income(BuildingsTemplate t,long quantity){
            return match(t,quantity).leases().stream().mapToDouble(HousingChoice.Lease::bill).sum();
        }
        /** Enough actual recipe units for the remaining compatible, affordable demand; no fictitious head-capacity demand. */
        public int quantityNeeded(BuildingsTemplate t){
            long limit=(long)Math.min(Integer.MAX_VALUE,Math.ceil(remaining.stream().mapToDouble(HousingChoice.Demand::count).sum()));
            var placed=match(t,limit);int wanted=0;
            if(t==null||t.getHousingRecipe()==null)return 0;
            for(var d:t.getHousingRecipe().mix()){
                double units=placed.leases().stream().filter(l->l.home().dwelling().equals(d.id())).mapToDouble(HousingChoice.Lease::count).sum();
                wanted=Math.max(wanted,(int)Math.ceil(Math.max(0,units-FamilyModel.packingRounding(units))/d.count()));
            }
            return wanted;
        }
        private HousingChoice.Result match(BuildingsTemplate t,long quantity){
            if(city==null||t==null||quantity<=0||t.getHousingRecipe()==null||!city.getBuildingManager().isTemplateKnown(t))
                return new HousingChoice.Result(List.of(),0,0,0,0,0,0);
            List<HousingChoice.Offer> proposed=new ArrayList<>();var recipe=t.getHousingRecipe();var f=HousingForms.of(t.getId());
            for(var d:recipe.mix()){var key=new HousingInventory.Key(t.getId(),d.id());proposed.add(new HousingChoice.Offer(key,(double)d.count()*quantity,d.capacity(),d.bedrooms(),d.area(),d.quality(),f==null?0:f.garden(),f!=null&&f.stepFree(),asks.getOrDefault(key,0.0),0));}
            return HousingChoice.clear(remaining,proposed,List.of(),month,false);
        }
    }
    public void sold(){
        double oldCost=0,currentCost=0,paid=0,capacity=0;
        for(ClassState c:classes.values()){
            double quantity=leases.stream().filter(l->l.home().equals(c.key())).mapToDouble(HousingChoice.Lease::count).sum();
            double value=leases.stream().filter(l->l.home().equals(c.key())).mapToDouble(HousingChoice.Lease::bill).sum();
            paid+=value;capacity+=quantity*c.capacity;
            if(c.tradeQuantity>0&&c.lastTrade>0){
                // No paid observation carries its previous price; a newly occupied class joins next month's basket.
                double price=quantity>0&&value>0?value/quantity:c.lastTrade;
                oldCost+=c.tradeQuantity*c.lastTrade;currentCost+=c.tradeQuantity*price;
            }
        }
        if(rentPriceIndex<=0){if(capacity>0&&paid>0)rentPriceIndex=paid/capacity;}
        else if(oldCost>0)rentPriceIndex*=currentCost/oldCost;
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
            c.target=c.homes>0?anchor*Math.pow(pressure,RealEstate.SCARCITY_ELASTICITY):Math.max(anchor,c.carry);
            double balanced=c.homes*RealEstate.GLUT_PRESSURE;
            // Allocation can round down by an ULP at a full door. Classify
            // economic vacancy with the same quantity bound as the match.
            if(c.demand>=balanced-FamilyModel.packingRounding(balanced)&&c.occupied>0)c.target=Math.max(c.target,c.carry*c.homes/c.occupied);
            c.ask=c.target>0?Retail.stickyPrice(c.ask,c.target,expectedMonthly,1.0/RealEstate.LEASE_MONTHS)
                    :Math.max(0,c.ask*(1-1.0/RealEstate.LEASE_MONTHS));
        }
    }
    public State forSave(){State s=new State();s.month=month;s.classes=classes();s.leases=new ArrayList<>(leases);s.demand=new ArrayList<>(demand);s.pipeline=new ArrayList<>(pipeline);s.sharing=new ArrayList<>(sharing);
        s.hosted=hosted;s.rentPriceIndex=rentPriceIndex;
        s.kept=result.kept();s.suitable=result.suitable();s.crowded=result.crowded();s.unaffordable=result.unaffordable();s.noSuitable=result.noSuitable();s.unplaced=result.unplaced();s.overcrowded=new LinkedHashMap<>(overcrowded);return s;}
    private static void nonnegative(double n){if(!Double.isFinite(n)||n<0)throw new IllegalArgumentException("Invalid saved housing quantity");}
    private static void validate(State s){
        nonnegative(s.rentPriceIndex);
        if(s.classes==null||s.leases==null||s.demand==null||s.pipeline==null||s.sharing==null||s.classes.size()>HousingRecipe.MAX_CLASSES)
            throw new IllegalArgumentException("Invalid saved housing dimensions");
        Map<HousingInventory.Key,ClassState> rows=new HashMap<>();
        for(ClassState c:s.classes){
            if(c.dwelling==null||c.dwelling.isBlank()||c.capacity<1||rows.put(c.key(),c)!=null)throw new IllegalArgumentException("Invalid saved housing class");
            for(double n:new double[]{c.ask,c.capital,c.carry,c.demand,c.homes,c.occupied,c.target,c.lastTrade,c.tradeQuantity,c.tradeValue})nonnegative(n);
            if(c.occupied>c.homes||c.demand<c.occupied||c.stale<0)throw new IllegalArgumentException("Invalid saved housing occupancy");
        }
        Map<HousingInventory.Key,Double> occupied=new HashMap<>(),funded=new HashMap<>(),bills=new HashMap<>();double sponsored=0;
        for(var l:s.leases){if(!rows.containsKey(l.home()))throw new IllegalArgumentException("Unknown saved lease class");
            nonnegative(l.count());nonnegative(l.bill());occupied.merge(l.home(),l.count(),Double::sum);
            funded.merge(l.home(),l.funding().doors(),Double::sum);bills.merge(l.home(),l.bill(),Double::sum);sponsored+=l.funding().sponsored();}
        for(ClassState c:s.classes){double rounding=FamilyModel.packingRounding(c.homes);
            if(Math.abs(c.occupied-occupied.getOrDefault(c.key(),0.0))>rounding||Math.abs(c.occupied-funded.getOrDefault(c.key(),0.0))>rounding)
                throw new IllegalArgumentException("Saved housing contracts do not reconcile to occupancy");
        }
        for(var l:s.leases){double doors=funded.get(l.home());double price=doors>0?bills.get(l.home())/doors:0;
            if(Math.abs(l.bill()-l.funding().doors()*price)>MoneyAudit.tolerance(bills.get(l.home())))
                throw new IllegalArgumentException("Saved housing bills do not reconcile to contracts");}
        for(double n:new double[]{s.kept,s.suitable,s.crowded,s.unaffordable,s.noSuitable,s.unplaced})nonnegative(n);
        nonnegative(s.hosted);if(s.hosted>s.crowded+FamilyModel.packingRounding(s.crowded))throw new IllegalArgumentException("Invalid saved emergency hosting");
        if(Math.abs(sponsored-s.hosted)>FamilyModel.packingRounding(sponsored))throw new IllegalArgumentException("Saved sponsored contracts do not reconcile to hosting");
        Map<String,HousingChoice.Demand> profiles=new HashMap<>();for(var d:s.demand)if(profiles.put(d.key(),d)!=null)throw new IllegalArgumentException("Duplicate saved housing cell");
        Map<String,Double> placed=new HashMap<>();Set<LeaseKey> leaseKeys=new HashSet<>();
        for(var l:s.leases){
            if(!profiles.containsKey(l.cell())||!leaseKeys.add(new LeaseKey(l.cell(),l.home())))throw new IllegalArgumentException("Invalid saved housing assignment");
            placed.merge(l.cell(),l.households(),Double::sum);
        }
        for(var d:s.demand)if(placed.getOrDefault(d.key(),0.0)>d.count()+FamilyModel.packingRounding(d.count()))
            throw new IllegalArgumentException("Saved housing assignments exceed their source cell");
        if(s.overcrowded!=null)for(var e:s.overcrowded.entrySet()){var n=e.getValue();
            if(n==null||!profiles.containsKey(e.getKey()))throw new IllegalArgumentException("Invalid saved overcrowding");nonnegative(n);
            if(n>placed.getOrDefault(e.getKey(),0.0)+FamilyModel.packingRounding(n))throw new IllegalArgumentException("Saved overcrowding exceeds its cell's placements");}
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
        hosted=s.hosted;rentPriceIndex=s.rentPriceIndex;overcrowded=s.overcrowded==null?Map.of():Collections.unmodifiableMap(new LinkedHashMap<>(s.overcrowded));
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
    public void reset(){classes.clear();leases=List.of();demand=List.of();offers=List.of();pipeline=List.of();sharing=List.of();hosted=0;overcrowded=Map.of();rentPriceIndex=0;arrivalProjection=false;formingFamilies=formingAdults=0;month=Integer.MIN_VALUE;result=new HousingChoice.Result(List.of(),0,0,0,0,0,0);}
    public void redenominate(double scale){for(ClassState c:classes.values()){c.ask*=scale;c.capital*=scale;c.carry*=scale;c.target*=scale;c.lastTrade*=scale;c.tradeValue*=scale;}
        rentPriceIndex*=scale;
        leases=leases.stream().map(l->new HousingChoice.Lease(l.cell(),l.home(),l.count(),l.bill()*scale,l.households(),l.funding())).toList();
        sharing=sharing.stream().map(t->new Sharing(t.source(),t.target(),t.groups(),t.sourceCount(),t.targetCount(),t.sourceBudget()*scale,t.targetBudget()*scale)).toList();
        demand=demand.stream().map(d->new HousingChoice.Demand(d.key(),d.count(),d.size(),d.children(),d.retired(),d.tier(),d.young(),d.budget()*scale)).toList();
        pipeline=pipeline.stream().map(o->new HousingChoice.Offer(o.key(),o.count(),o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent()*scale,o.recurring()*scale)).toList();
        result=new HousingChoice.Result(leases,result.kept(),result.suitable(),result.crowded(),result.unaffordable(),result.noSuitable(),result.unplaced());}
}
