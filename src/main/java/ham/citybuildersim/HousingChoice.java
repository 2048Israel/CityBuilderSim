package ham.citybuildersim;

import java.util.*;

/**
 * A bounded household-cell to dwelling-class allocation, retaining valid leases first.
 * WHY (RES3): size-only packing hid premium vacancy and let an enum's first cell
 * win a rationed home. Residual paths maximize suitable placements before preferences.
 */
public final class HousingChoice {
    private HousingChoice() { }
    /** Quality's base utility weight; richer tiers value finish more. */
    public static final double QUALITY_WEIGHT = .35;
    /** A missing bedroom's soft layout penalty, from the numerical prototype. */
    public static final double BEDROOM_PENALTY = .10;
    /** A family's utility for actual outdoor space, saturating at 100 square metres. */
    public static final double GARDEN_WEIGHT = .35;
    /** Retired households' utility for an explicitly step-free dwelling. */
    public static final double ACCESS_WEIGHT = .60;
    /** Remaining-budget sensitivity, after all recurring necessities are reserved. */
    public static final double BUDGET_WEIGHT = .20;
    /** Base desired private floor area, in square metres. */
    public static final double BASE_AREA = 30;
    /** Desired private floor area added per household member. */
    public static final double AREA_PER_PERSON = 20;
    /** Each tier's increment in the desired-area vector. */
    public static final double AREA_PER_TIER = .15;
    /** Young adults' desired area relative to established households. */
    public static final double YOUNG_AREA = .9;
    /**
     * The census line: the census of England and Wales called a home overcrowded past two persons a room (1891, and so
     * to 1931), its rooms its bedrooms and a kitchen. No occupied door passes it: a household alone, two to a share, a
     * guest beside its host and a lodger (RES3, the orchestrator's call of 2026-11-25).
     */
    public static final double PERSONS_PER_ROOM = 2;
    /** The most people a door of so many bedrooms holds at the census line (H55 4, D90 6, H120 8). */
    public static int line(int bedrooms){return (int)Math.floor(PERSONS_PER_ROOM*(bedrooms+1));}
    /** Each tier's extra preference for finish quality. */
    public static final double QUALITY_PER_TIER = .20;
    /** Outdoor square metres at which the family garden term saturates. */
    public static final double GARDEN_AREA = 100;
    public record Demand(String key, double count, int size, int children, boolean retired,
                         int tier, boolean young, double budget) {
        public Demand { quantity(count); quantity(budget); if (size<1 || children<0 || tier<0 || key==null || key.isBlank()) throw new IllegalArgumentException(); }
        double targetArea() { return (BASE_AREA+AREA_PER_PERSON*size)*(1+AREA_PER_TIER*tier)*(young?YOUNG_AREA:1); }
    }
    /** people is the most one household placed here may bring: a whole door's census line, or the line less who shares it. */
    public record Offer(HousingInventory.Key key, double count, int capacity, int bedrooms,
                        double area, double quality, double garden, boolean stepFree,
                        double rent, double recurring, int people) {
        public Offer {
            quantity(count);quantity(rent);quantity(recurring);quantity(garden);
            if(key==null||capacity<1||bedrooms<0||people<1||!(area>0)||!Double.isFinite(area)||!(quality>0)||!Double.isFinite(quality))
                throw new IllegalArgumentException("Invalid housing offer");
        }
        /** A whole door, open to a household up to its census line. */
        public Offer(HousingInventory.Key key, double count, int capacity, int bedrooms, double area, double quality,
                     double garden, boolean stepFree, double rent, double recurring) {
            this(key,count,capacity,bedrooms,area,quality,garden,stepFree,rent,recurring,line(bedrooms));
        }
    }
    /** Contract quantities, independent of donated physical room and the cell's preference vector. */
    public record Funding(double full, double half, double sponsored) {
        public Funding { quantity(full);quantity(half);quantity(sponsored); }
        public double households(){return full+half+sponsored;}
        public double doors(){return full+half/2;}
        public Funding scale(double factor){return new Funding(full*factor,half*factor,sponsored*factor);}
        public Funding plus(Funding other){return new Funding(full+other.full,half+other.half,sponsored+other.sponsored);}
    }
    /** count is occupied door-equivalents; funding preserves the actual contracts through coalescence and saves. */
    public record Lease(String cell, HousingInventory.Key home, double count, double bill, double households, Funding funding) {
        public Lease(String cell,HousingInventory.Key home,double count,double bill){this(cell,home,count,bill,count,new Funding(count,0,0));}
        public Lease { quantity(count);quantity(bill);quantity(households);
            double rounding=FamilyModel.packingRounding(households);
            // Two to a share binds who funds the door. A guest rides on its hosts: the doubling's two to a share
            // (half a door each), a lodger with no door of its own up to the door's people (HousingMarket.lodging(), RES3c).
            if(cell==null||cell.isBlank()||home==null||funding==null||households<count||households-funding.sponsored()>2*count+rounding
                    ||Math.abs(funding.households()-households)>rounding
                    ||count>funding.doors()+funding.sponsored()/2+rounding)throw new IllegalArgumentException("Invalid lease"); }
        public Lease scale(double factor){return new Lease(cell,home,count*factor,bill*factor,households*factor,funding.scale(factor));}
        public Lease plus(Lease other){return new Lease(cell,home,count+other.count,bill+other.bill,households+other.households,funding.plus(other.funding));}
    }
    public record Result(List<Lease> leases, double kept, double suitable, double crowded,
                         double unaffordable, double noSuitable, double unplaced) { }
    private static void quantity(double v) { if (!Double.isFinite(v)||v<0) throw new IllegalArgumentException("Invalid housing quantity"); }
    /** Crowding may pass the door's built capacity, never the census line its people bound carries. */
    public static boolean suitable(Demand d, Offer o, boolean emergency) {
        return !(d.children()>0 && o.capacity()<=FamilyModel.STUDIO_MAX_SIZE) && d.size()<=o.people()
                && (emergency || d.size()<=o.capacity());
    }
    /** Two of this household to a share stay within the door's census line. */
    public static boolean halves(Demand d, Offer o) { return suitable(d,o,true) && 2*d.size()<=o.people(); }
    public static boolean affordable(Demand d, Offer o) { return o.rent()+o.recurring()<=d.budget(); }
    public static double score(Demand d, Offer o) {
        return -Math.abs(Math.log(o.area()/d.targetArea()))
                +(QUALITY_WEIGHT+QUALITY_PER_TIER*d.tier())*Math.log(o.quality())
                +(d.children()>0 ? GARDEN_WEIGHT*Math.min(1,o.garden()/GARDEN_AREA) : 0)
                +(d.retired()&&o.stepFree()?ACCESS_WEIGHT:0)
                -BEDROOM_PENALTY*Math.max(0,(d.size()+1)/2.0-o.bedrooms())
                -BUDGET_WEIGHT*(d.budget()>0?(o.rent()+o.recurring())/d.budget():0);
    }
    /** Equivalent nodes share their allocation pro rata; input order confers no priority. */
    public static Result clear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month) {
        return clear(demands,offers,incumbents,month,true);
    }
    /** Planning can reserve suitable homes without assuming emergency crowding in a new project. */
    public static Result clear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month,boolean emergency) {
        return clear(demands,offers,incumbents,month,emergency,Set.of());
    }
    /** sitting: the cells whose incumbents keep their doors whatever their budget, until they can be evicted (tenure). */
    public static Result clear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month,boolean emergency,Set<String> sitting) {
        if(!incumbents.isEmpty())return retainAndClear(demands,offers,incumbents,month,emergency,sitting);
        List<Demand> ds=demands.stream().sorted(Comparator.comparing(Demand::key)).toList();
        List<Offer> os=offers.stream().sorted(Comparator.comparing(o->o.key().toString())).toList();
        int h=ds.size(),t=os.size(); double[][] x=new double[h][t];
        Map<String,Integer> di=new HashMap<>(); Map<HousingInventory.Key,Integer> oi=new HashMap<>();
        double[] need=new double[h],free=new double[t];
        for(int i=0;i<h;i++){ di.put(ds.get(i).key(),i);need[i]=ds.get(i).count(); }
        for(int j=0;j<t;j++){ oi.put(os.get(j).key(),j);free[j]=os.get(j).count(); }
        // Retained demand shrinks with its cell, then each depleted class is rationed pro rata.
        for(Lease l:incumbents){ Integer i=di.get(l.cell()),j=oi.get(l.home());
            if(i!=null&&j!=null&&l.households()==l.count()&&suitable(ds.get(i),os.get(j),false)&&affordable(ds.get(i),os.get(j))) x[i][j]+=l.count(); }
        for(int i=0;i<h;i++){double sum=Arrays.stream(x[i]).sum();if(sum>need[i])for(int j=0;j<t;j++)x[i][j]*=need[i]/sum;}
        double kept=0;
        for(int j=0;j<t;j++){double sum=0;for(int i=0;i<h;i++)sum+=x[i][j];double ratio=sum>free[j]?free[j]/sum:1;
            for(int i=0;i<h;i++){x[i][j]*=ratio;need[i]=Math.max(0,need[i]-x[i][j]);free[j]-=x[i][j];kept+=x[i][j];}}
        for(int i=0;i<h;i++)need[i]=residual(need[i],ds.get(i).count());
        for(int j=0;j<t;j++)free[j]=residual(free[j],os.get(j).count());
        double[][] retained=new double[h][];for(int i=0;i<h;i++)retained[i]=x[i].clone();
        double suitable=allocate(ds,os,need,free,x,month,false);
        double crowded=emergency?completeEmergency(ds,os,need,free,x,retained,month):0;
        // Pro-rata products may sum one ULP over a source. Round down only within
        // the model's packing bound; a genuine excess remains an error.
        for(int j=0;j<t;j++){double sum=0;for(int i=0;i<h;i++)sum+=x[i][j];
            if(sum>os.get(j).count()){
                if(sum-os.get(j).count()>FamilyModel.packingRounding(sum))throw new IllegalStateException("Housing allocation exceeds stock");
                double ratio=Math.nextDown(os.get(j).count()/sum);for(int i=0;i<h;i++)x[i][j]*=ratio;
            }
            double used=0;
            for(int i=0;i<h;i++){
                if(used+x[i][j]>os.get(j).count())x[i][j]=Math.max(0,Math.nextDown(os.get(j).count()-used));
                used+=x[i][j];
            }}
        for(int i=0;i<h;i++){double sum=Arrays.stream(x[i]).sum();need[i]=Math.max(0,ds.get(i).count()-sum);
            if(need[i]<=FamilyModel.packingRounding(ds.get(i).count()))need[i]=0;}
        double poor=0,noSuitable=0,left=0; List<Lease> leases=new ArrayList<>();
        for(int i=0;i<h;i++){
            left+=need[i]; boolean compatible=false,canPay=false;
            for(int j=0;j<t;j++){
                if(free[j]>0&&suitable(ds.get(i),os.get(j),false)){compatible=true;canPay|=affordable(ds.get(i),os.get(j));}
                if(x[i][j]>0)leases.add(new Lease(ds.get(i).key(),os.get(j).key(),x[i][j],x[i][j]*os.get(j).rent()));
            }
            if(compatible&&!canPay)poor+=need[i];else noSuitable+=need[i];
        }
        // A tiny residual cell may itself have been made by the whole match's
        // subtraction. Use that source's existing packing resolution, once,
        // rather than treating the residue as a new exactly sized household.
        double source=ds.stream().mapToDouble(Demand::count).sum();
        if(left<=FamilyModel.packingRounding(source))left=poor=noSuitable=0;
        return new Result(List.copyOf(leases),kept,suitable,crowded,poor,noSuitable,left);
    }
    /**
     * Existing funded door shares precede new movers; their household quantity is not a whole-door quantity. A sitting
     * cell keeps its door and its bill on the budget it has (tenure: the landlord carries the arrears); only a cell
     * that can be evicted keeps no more than its budget funds.
     */
    private static Result retainAndClear(List<Demand> demands,List<Offer> offers,List<Lease> incumbents,int month,boolean emergency,Set<String> sitting){
        Map<String,Demand> cells=new HashMap<>();for(var d:demands)cells.put(d.key(),d);
        Map<HousingInventory.Key,Offer> homes=new HashMap<>();for(var o:offers)homes.put(o.key(),o);
        List<Lease> ordered=incumbents.stream().sorted(Comparator.comparing(Lease::cell).thenComparing(l->l.home().toString())).toList();
        List<Lease> valid=new ArrayList<>();Map<String,Double> wanted=new HashMap<>();
        for(var l:ordered){var d=cells.get(l.cell());var o=homes.get(l.home());if(d==null||o==null)continue;
            boolean sits=sitting.contains(l.cell());
            // A sitter keeps its door within the census line, as a family that grows in it stays (RES3c).
            double full=suitable(d,o,sits)&&(sits||affordable(d,o))?l.funding().full():0;
            double half=halves(d,o)&&(sits||(o.rent()+o.recurring())/2<=d.budget())?l.funding().half():0;
            Funding funding=new Funding(full,half,0);double n=funding.doors(),people=funding.households();
            if(!(n>0))continue;
            var paid=new Lease(l.cell(),l.home(),n,n*o.rent(),people,funding);
            valid.add(paid);wanted.merge(paid.cell(),paid.households(),Double::sum);
        }
        valid.sort(Comparator.comparing(Lease::cell).thenComparing(l->l.home().toString()));
        List<Lease> scaled=new ArrayList<>();Map<HousingInventory.Key,Double> occupied=new HashMap<>();
        for(var l:valid){double factor=Math.min(1,cells.get(l.cell()).count()/wanted.get(l.cell()));
            double n=l.count()*factor;if(!(n>0))continue;
            var q=l.scale(factor);
            scaled.add(q);occupied.merge(q.home(),q.count(),Double::sum);
        }
        List<Lease> kept=new ArrayList<>();Map<String,Double> placed=new HashMap<>();Map<HousingInventory.Key,Double> used=new HashMap<>();
        double doors=0,crowded=0;
        for(var l:scaled){double factor=Math.min(1,homes.get(l.home()).count()/occupied.get(l.home()));
            double n=l.count()*factor;if(!(n>0))continue;
            var q=l.scale(factor);
            kept.add(q);placed.merge(q.cell(),q.households(),Double::sum);used.merge(q.home(),q.count(),Double::sum);
            doors+=q.count();crowded+=q.households()-q.count();
        }
        List<Demand> rest=new ArrayList<>();for(var d:demands){double n=Math.max(0,d.count()-placed.getOrDefault(d.key(),0.0));
            if(n>FamilyModel.packingRounding(d.count()))rest.add(new Demand(d.key(),n,d.size(),d.children(),d.retired(),d.tier(),d.young(),d.budget()));}
        List<Offer> free=new ArrayList<>();for(var o:offers)free.add(new Offer(o.key(),Math.max(0,o.count()-used.getOrDefault(o.key(),0.0)),o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent(),o.recurring()));
        var next=clear(rest,free,List.of(),month,emergency);kept.addAll(next.leases());
        return new Result(List.copyOf(kept),doors,next.suitable(),crowded+next.crowded(),next.unaffordable(),next.noSuitable(),next.unplaced());
    }

    private record DemandSignature(int size,int children,boolean retired,int tier,boolean young,double budget) { }
    record Completion(List<Lease> leases,double moved) { }
    private record ContractKey(String cell,HousingInventory.Key home) { }
    static List<Lease> coalesce(List<Lease> leases){
        Map<ContractKey,Lease> merged=new LinkedHashMap<>();
        for(var l:leases)merged.merge(new ContractKey(l.cell(),l.home()),l,Lease::plus);
        return List.copyOf(merged.values());
    }
    /** After all movers, keep old claims exact and move only paid-half excess to unused normal doors. */
    static Completion completePaidExcess(List<Demand> demand,List<Offer> offers,List<Lease> old,int month){
        Map<String,Demand> cells=new TreeMap<>();for(var d:demand)cells.put(d.key(),d);
        Map<HousingInventory.Key,Offer> homes=new HashMap<>();for(var o:offers)homes.put(o.key(),o);
        Map<HousingInventory.Key,Double> used=new HashMap<>();Map<String,Double> costs=new HashMap<>(),gaps=new TreeMap<>();
        for(var l:old){used.merge(l.home(),l.count(),Double::sum);costs.merge(l.cell(),l.bill()+l.count()*homes.get(l.home()).recurring(),Double::sum);}
        double[] eligible=new double[old.size()];
        for(int i=0;i<old.size();i++){
            var l=old.get(i);var d=cells.get(l.cell());var o=homes.get(l.home());
            if(!(l.bill()>0)||!suitable(d,o,false)||!affordable(d,o))continue;
            double n=l.funding().half()/2;if(n<=FamilyModel.packingRounding(l.households()))continue;
            eligible[i]=n;gaps.merge(l.cell(),n,Double::sum);
        }
        List<Demand> extra=new ArrayList<>();
        for(var e:gaps.entrySet()){var d=cells.get(e.getKey());
            double resources=Math.max(0,d.budget()*d.count()-costs.getOrDefault(d.key(),0.0));
            extra.add(new Demand(d.key(),e.getValue(),d.size(),d.children(),d.retired(),d.tier(),d.young(),Math.min(d.budget(),resources/e.getValue())));
        }
        List<Offer> free=new ArrayList<>();for(var o:offers)free.add(new Offer(o.key(),Math.max(0,o.count()-used.getOrDefault(o.key(),0.0)),o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent(),o.recurring()));
        var placed=clear(extra,free,List.of(),month,false);
        // Only NEW additions round down against the running old+new book. Old claims
        // are never scaled to make an allocation fit; larger excess remains an error.
        List<Lease> added=new ArrayList<>();Map<HousingInventory.Key,Double> running=new HashMap<>(used);
        for(var l:placed.leases()){
            var o=homes.get(l.home());double before=running.getOrDefault(l.home(),0.0),n=l.count();
            if(before+n>o.count()){
                if(before+n-o.count()>FamilyModel.packingRounding(before+n))throw new IllegalStateException("New normal addition exceeds current stock");
                n=Math.min(n,Math.max(0,Math.nextDown(o.count()-before)));
                while(n>0&&before+n>o.count())n=Math.nextDown(n);
            }
            if(n>0){
                // Saving coalesces equal cell/class rows. Check that summation order too,
                // reducing this new row alone if its merge would cross the strict cap.
                for(;;){
                    List<Lease> book=new ArrayList<>(old);book.addAll(added);book.add(new Lease(l.cell(),l.home(),n,n*o.rent()));
                    double total=0;for(var q:coalesce(book))if(q.home().equals(l.home()))total+=q.count();
                    if(total<=o.count())break;
                    double excess=total-o.count();
                    if(excess>FamilyModel.packingRounding(total))throw new IllegalStateException("Coalesced normal addition exceeds current stock");
                    n=Math.max(0,Math.nextDown(n-excess));if(!(n>0))break;
                }
                if(n>0){added.add(new Lease(l.cell(),l.home(),n,n*o.rent()));running.put(l.home(),before+n);}
            }
        }
        Map<String,Double> moved=new HashMap<>();for(var l:added)moved.merge(l.cell(),l.households(),Double::sum);
        List<Lease> all=new ArrayList<>();
        for(int i=0;i<old.size();i++){
            var l=old.get(i);if(!(eligible[i]>0)){all.add(l);continue;}
            double n=Math.min(eligible[i],eligible[i]*Math.min(1,moved.getOrDefault(l.cell(),0.0)/gaps.get(l.cell())));
            if(!(n>0)){all.add(l);continue;}
            var f=l.funding();
            // Two paid halves become one remaining full payer and one normal mover.
            all.add(new Lease(l.cell(),l.home(),l.count(),l.bill(),l.households()-n,new Funding(f.full()+n,Math.max(0,f.half()-2*n),f.sponsored())));
        }
        all.addAll(added);return new Completion(coalesce(all),moved.values().stream().mapToDouble(Double::doubleValue).sum());
    }
    /** Books the outcome of internal emergency phases without clearing or pricing again. */
    public static Result summarize(List<Demand> demand,List<Offer> offers,List<Lease> leases,double kept,double normal,double crowded,
                                   Map<String,Double> remainingBudgets){
        Map<HousingInventory.Key,Double> used=new HashMap<>();for(var l:leases)used.merge(l.home(),l.count(),Double::sum);
        double poor=0,shortage=0,left=0;
        for(var d:demand){double taken=leases.stream().filter(l->l.cell().equals(d.key())).mapToDouble(Lease::households).sum();
            double missing=Math.max(0,d.count()-taken);if(missing<=FamilyModel.packingRounding(d.count()))continue;
            Demand budget=new Demand(d.key(),d.count(),d.size(),d.children(),d.retired(),d.tier(),d.young(),remainingBudgets.getOrDefault(d.key(),d.budget()));
            boolean compatible=false,affordable=false;
            for(var o:offers)if(o.count()>used.getOrDefault(o.key(),0.0)&&suitable(budget,o,false)){compatible=true;affordable|=affordable(budget,o);}
            left+=missing;if(compatible&&!affordable)poor+=missing;else shortage+=missing;
        }
        double source=demand.stream().mapToDouble(Demand::count).sum();
        if(left<=FamilyModel.packingRounding(source))left=poor=shortage=0;
        return new Result(List.copyOf(leases),kept,normal,crowded,poor,shortage,left);
    }
    private record OfferSignature(int capacity,int bedrooms,double area,double quality,double garden,boolean stepFree,double rent,double recurring,int people) { }
    /** A subtraction residue is measured against its original source, before it can create a graph node. */
    private static double residual(double left,double source){return left<=FamilyModel.packingRounding(source)?0:Math.max(0,left);}
    private static double allocate(List<Demand> ds,List<Offer> os,double[] need,double[] free,double[][] x,int month,boolean emergency){
        Map<DemandSignature,List<Integer>> dg=new LinkedHashMap<>(); Map<OfferSignature,List<Integer>> og=new LinkedHashMap<>();
        boolean freeOffers=true;for(int j=0;j<os.size();j++)if(free[j]>0&&os.get(j).rent()+os.get(j).recurring()>0)freeOffers=false;
        for(int i=0;i<ds.size();i++)if(need[i]>0){Demand d=ds.get(i);dg.computeIfAbsent(new DemandSignature(d.size(),d.children(),d.retired(),d.tier(),d.young(),freeOffers?0:d.budget()),k->new ArrayList<>()).add(i);}
        for(int j=0;j<os.size();j++)if(free[j]>0){Offer o=os.get(j);og.computeIfAbsent(new OfferSignature(o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent(),o.recurring(),o.people()),k->new ArrayList<>()).add(j);}
        List<List<Integer>> hs=new ArrayList<>(dg.values()),ts=new ArrayList<>(og.values());
        // Stable keys, rotated monthly, decide only incomparable residual ties.
        hs.sort(Comparator.comparingLong(g->rotatedKey(ds.get(g.getFirst()).key(),month)));
        int hn=hs.size(),tn=ts.size(),sink=hn+tn+1; List<List<Edge>> graph=new ArrayList<>();for(int i=0;i<=sink;i++)graph.add(new ArrayList<>());
        double[] nh=new double[hn],nt=new double[tn]; Edge[][] links=new Edge[hn][tn];
        for(int a=0;a<hn;a++){for(int i:hs.get(a))nh[a]+=need[i];edge(graph,0,1+a,nh[a]);}
        for(int b=0;b<tn;b++){for(int j:ts.get(b))nt[b]+=free[j];edge(graph,1+hn+b,sink,nt[b]);}
        for(int a=0;a<hn;a++){
            Demand d=ds.get(hs.get(a).getFirst()); List<Integer> rank=new ArrayList<>();for(int b=0;b<tn;b++)rank.add(b);
            rank.sort(Comparator.<Integer>comparingDouble(b->score(d,os.get(ts.get(b).getFirst()))).reversed()
                    .thenComparingLong(b->rotatedKey(os.get(ts.get(b).getFirst()).key().toString(),month)));
            for(int b:rank){Offer o=os.get(ts.get(b).getFirst());if(suitable(d,o,emergency)&&affordable(d,o))links[a][b]=edge(graph,1+a,1+hn+b,Math.min(nh[a],nt[b]));}
        }
        double placed=0;
        while(true){Edge[] path=new Edge[sink+1];int[] from=new int[sink+1];Arrays.fill(from,-1);from[0]=0;
            ArrayDeque<Integer> q=new ArrayDeque<>();q.add(0);
            while(!q.isEmpty()&&from[sink]<0){int u=q.remove();for(Edge e:graph.get(u))if(e.left>1e-12&&from[e.to]<0){from[e.to]=u;path[e.to]=e;q.add(e.to);}}
            if(from[sink]<0)break;double flow=Double.POSITIVE_INFINITY;
            for(int n=sink;n!=0;n=from[n])flow=Math.min(flow,path[n].left);
            for(int n=sink;n!=0;n=from[n]){path[n].left-=flow;path[n].reverse.left+=flow;}placed+=flow;
        }
        double[] takeH=new double[ds.size()],takeT=new double[os.size()];
        for(int a=0;a<hn;a++)for(int b=0;b<tn;b++)if(links[a][b]!=null){double amount=links[a][b].reverse.left;
            for(int i:hs.get(a))for(int j:ts.get(b)){double n=amount*(need[i]/nh[a])*(free[j]/nt[b]);x[i][j]+=n;takeH[i]+=n;takeT[j]+=n;}}
        for(int i=0;i<need.length;i++)need[i]=residual(need[i]-takeH[i],ds.get(i).count());
        for(int j=0;j<free.length;j++)free[j]=residual(free[j]-takeT[j],os.get(j).count());
        return placed;
    }
    /**
     * Complete the maximum normal matching through its residual paths. A path's
     * integer cost counts normal placements lost; only zero-cost additions are
     * allowed. Thus soft preferences may move, but neither incumbents nor the
     * maximum number of normally suitable households can be displaced by crowding.
     */
    private static double completeEmergency(List<Demand> ds,List<Offer> os,double[] need,double[] free,
                                            double[][] x,double[][] retained,int month){
        if(Arrays.stream(need).sum()<=1e-12||Arrays.stream(free).sum()<=1e-12)return 0;
        double[] originalH=new double[ds.size()],originalT=new double[os.size()];
        for(int i=0;i<ds.size();i++)originalH[i]=residual(ds.get(i).count()-Arrays.stream(retained[i]).sum(),ds.get(i).count());
        boolean freeOffers=true;
        for(int j=0;j<os.size();j++){
            double kept=0;for(int i=0;i<ds.size();i++)kept+=retained[i][j];
            originalT[j]=residual(os.get(j).count()-kept,os.get(j).count());
            if(originalT[j]>0&&os.get(j).rent()+os.get(j).recurring()>0)freeOffers=false;
        }
        Map<DemandSignature,List<Integer>> dg=new LinkedHashMap<>();Map<OfferSignature,List<Integer>> og=new LinkedHashMap<>();
        for(int i=0;i<ds.size();i++)if(originalH[i]>0){Demand d=ds.get(i);dg.computeIfAbsent(new DemandSignature(d.size(),d.children(),d.retired(),d.tier(),d.young(),freeOffers?0:d.budget()),k->new ArrayList<>()).add(i);}
        for(int j=0;j<os.size();j++)if(originalT[j]>0){Offer o=os.get(j);og.computeIfAbsent(new OfferSignature(o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent(),o.recurring(),o.people()),k->new ArrayList<>()).add(j);}
        List<List<Integer>> hs=new ArrayList<>(dg.values()),ts=new ArrayList<>(og.values());
        hs.sort(Comparator.comparingLong(g->rotatedKey(ds.get(g.getFirst()).key(),month)));
        int hn=hs.size(),tn=ts.size(),sink=hn+tn+1;List<List<Edge>> graph=new ArrayList<>();for(int n=0;n<=sink;n++)graph.add(new ArrayList<>());
        double[] nh=new double[hn],nt=new double[tn];Edge[][] links=new Edge[hn][tn];
        for(int a=0;a<hn;a++){double left=0;for(int i:hs.get(a)){nh[a]+=originalH[i];left+=need[i];}edge(graph,0,1+a,left);}
        for(int b=0;b<tn;b++){double left=0;for(int j:ts.get(b)){nt[b]+=originalT[j];left+=free[j];}edge(graph,1+hn+b,sink,left);}
        for(int a=0;a<hn;a++){
            Demand d=ds.get(hs.get(a).getFirst());List<Integer> rank=new ArrayList<>();for(int b=0;b<tn;b++)rank.add(b);
            rank.sort(Comparator.<Integer>comparingDouble(b->score(d,os.get(ts.get(b).getFirst()))).reversed()
                    .thenComparingLong(b->rotatedKey(os.get(ts.get(b).getFirst()).key().toString(),month)));
            for(int b:rank){Offer o=os.get(ts.get(b).getFirst());if(!suitable(d,o,true)||!affordable(d,o))continue;
                Edge e=costEdge(graph,1+a,1+hn+b,Math.min(nh[a],nt[b]),suitable(d,o,false)?-1:0);links[a][b]=e;
                double old=0;for(int i:hs.get(a))for(int j:ts.get(b))old+=Math.max(0,x[i][j]-retained[i][j]);
                e.left=Math.max(0,e.left-old);e.reverse.left=old;
            }
        }
        double added=0;
        while(true){
            int[] distance=new int[sink+1],from=new int[sink+1];Arrays.fill(distance,Integer.MAX_VALUE);Arrays.fill(from,-1);
            Edge[] path=new Edge[sink+1];boolean[] queued=new boolean[sink+1];ArrayDeque<Integer> q=new ArrayDeque<>();
            distance[0]=0;from[0]=0;q.add(0);queued[0]=true;
            while(!q.isEmpty()){
                int u=q.remove();queued[u]=false;
                for(Edge e:graph.get(u))if(e.left>1e-12&&e.to!=0&&distance[e.to]>distance[u]+e.cost){
                    distance[e.to]=distance[u]+e.cost;from[e.to]=u;path[e.to]=e;
                    if(!queued[e.to]){q.add(e.to);queued[e.to]=true;}
                }
            }
            if(from[sink]<0||distance[sink]>0)break;
            double flow=Double.POSITIVE_INFINITY;for(int n=sink;n!=0;n=from[n])flow=Math.min(flow,path[n].left);
            for(int n=sink;n!=0;n=from[n]){path[n].left-=flow;path[n].reverse.left+=flow;}added+=flow;
        }
        for(int i=0;i<ds.size();i++)System.arraycopy(retained[i],0,x[i],0,os.size());
        for(int a=0;a<hn;a++)for(int b=0;b<tn;b++)if(links[a][b]!=null){double amount=links[a][b].reverse.left;
            for(int i:hs.get(a))for(int j:ts.get(b))x[i][j]+=amount*(originalH[i]/nh[a])*(originalT[j]/nt[b]);}
        for(int i=0;i<ds.size();i++)need[i]=residual(ds.get(i).count()-Arrays.stream(x[i]).sum(),ds.get(i).count());
        for(int j=0;j<os.size();j++){double used=0;for(int i=0;i<ds.size();i++)used+=x[i][j];free[j]=residual(os.get(j).count()-used,os.get(j).count());}
        return added;
    }
    private static final class Edge { final int to,cost; double left; Edge reverse;Edge(int to,double left,int cost){this.to=to;this.left=left;this.cost=cost;} }
    private static long rotatedKey(String key,int month){
        long x=key.hashCode()*0x9e3779b97f4a7c15L ^ (long)month*0xbf58476d1ce4e5b9L;
        x=(x^(x>>>30))*0xbf58476d1ce4e5b9L;x=(x^(x>>>27))*0x94d049bb133111ebL;return x^(x>>>31);
    }
    private static Edge edge(List<List<Edge>> g,int u,int v,double cap){return costEdge(g,u,v,cap,0);}
    private static Edge costEdge(List<List<Edge>> g,int u,int v,double cap,int cost){Edge a=new Edge(v,cap,cost),b=new Edge(u,0,-cost);a.reverse=b;b.reverse=a;g.get(u).add(a);g.get(v).add(b);return a;}
}
