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
    public record Demand(String key, double count, int size, int children, boolean retired,
                         int tier, boolean young, double budget) {
        public Demand { quantity(count); quantity(budget); if (size<1 || key==null) throw new IllegalArgumentException(); }
        double targetArea() { return (30+20*size)*(1+.15*tier)*(young?.9:1); }
    }
    public record Offer(HousingInventory.Key key, double count, int capacity, int bedrooms,
                        double area, double quality, double garden, boolean stepFree,
                        double rent, double recurring) {
        public Offer { quantity(count); quantity(rent); quantity(recurring); }
    }
    public record Lease(String cell, HousingInventory.Key home, double count, double bill) { }
    public record Result(List<Lease> leases, double kept, double suitable, double crowded,
                         double unaffordable, double noSuitable, double unplaced) { }
    private static void quantity(double v) { if (!Double.isFinite(v)||v<0) throw new IllegalArgumentException("Invalid housing quantity"); }
    public static boolean suitable(Demand d, Offer o, boolean emergency) {
        return !(d.children()>0 && o.capacity()<=FamilyModel.STUDIO_MAX_SIZE)
                && (emergency || d.size()<=o.capacity());
    }
    public static boolean affordable(Demand d, Offer o) { return o.rent()+o.recurring()<=d.budget(); }
    public static double score(Demand d, Offer o) {
        return -Math.abs(Math.log(o.area()/d.targetArea()))
                +(QUALITY_WEIGHT+.20*d.tier())*Math.log(o.quality())
                +(d.children()>0 ? GARDEN_WEIGHT*Math.min(1,o.garden()/100) : 0)
                +(d.retired()&&o.stepFree()?ACCESS_WEIGHT:0)
                -BEDROOM_PENALTY*Math.max(0,(d.size()+1)/2.0-o.bedrooms())
                -BUDGET_WEIGHT*(d.budget()>0?(o.rent()+o.recurring())/d.budget():0);
    }
    /** Equivalent nodes share their allocation pro rata; input order confers no priority. */
    public static Result clear(List<Demand> demands, List<Offer> offers, List<Lease> incumbents, int month) {
        List<Demand> ds=demands.stream().sorted(Comparator.comparing(Demand::key)).toList();
        List<Offer> os=offers.stream().sorted(Comparator.comparing(o->o.key().toString())).toList();
        int h=ds.size(),t=os.size(); double[][] x=new double[h][t];
        Map<String,Integer> di=new HashMap<>(); Map<HousingInventory.Key,Integer> oi=new HashMap<>();
        double[] need=new double[h],free=new double[t];
        for(int i=0;i<h;i++){ di.put(ds.get(i).key(),i);need[i]=ds.get(i).count(); }
        for(int j=0;j<t;j++){ oi.put(os.get(j).key(),j);free[j]=os.get(j).count(); }
        // Retained demand shrinks with its cell, then each depleted class is rationed pro rata.
        for(Lease l:incumbents){ Integer i=di.get(l.cell()),j=oi.get(l.home());
            if(i!=null&&j!=null&&suitable(ds.get(i),os.get(j),false)&&affordable(ds.get(i),os.get(j))) x[i][j]+=l.count(); }
        for(int i=0;i<h;i++){double sum=Arrays.stream(x[i]).sum();if(sum>need[i])for(int j=0;j<t;j++)x[i][j]*=need[i]/sum;}
        double kept=0;
        for(int j=0;j<t;j++){double sum=0;for(int i=0;i<h;i++)sum+=x[i][j];double ratio=sum>free[j]?free[j]/sum:1;
            for(int i=0;i<h;i++){x[i][j]*=ratio;need[i]=Math.max(0,need[i]-x[i][j]);free[j]-=x[i][j];kept+=x[i][j];}}
        double suitable=allocate(ds,os,need,free,x,month,false),crowded=allocate(ds,os,need,free,x,month,true);
        double poor=0,noSuitable=0,left=0; List<Lease> leases=new ArrayList<>();
        for(int i=0;i<h;i++){
            left+=need[i]; boolean compatible=false,canPay=false;
            for(int j=0;j<t;j++){
                if(suitable(ds.get(i),os.get(j),false)){compatible=true;canPay|=affordable(ds.get(i),os.get(j));}
                if(x[i][j]>0)leases.add(new Lease(ds.get(i).key(),os.get(j).key(),x[i][j],x[i][j]*os.get(j).rent()));
            }
            if(compatible&&!canPay)poor+=need[i];else noSuitable+=need[i];
        }
        return new Result(List.copyOf(leases),kept,suitable,crowded,poor,noSuitable,left);
    }
    private record DemandSignature(int size,int children,boolean retired,int tier,boolean young,double budget) { }
    private record OfferSignature(int capacity,int bedrooms,double area,double quality,double garden,boolean stepFree,double rent,double recurring) { }
    private static double allocate(List<Demand> ds,List<Offer> os,double[] need,double[] free,double[][] x,int month,boolean emergency){
        Map<DemandSignature,List<Integer>> dg=new LinkedHashMap<>(); Map<OfferSignature,List<Integer>> og=new LinkedHashMap<>();
        for(int i=0;i<ds.size();i++)if(need[i]>0){Demand d=ds.get(i);dg.computeIfAbsent(new DemandSignature(d.size(),d.children(),d.retired(),d.tier(),d.young(),d.budget()),k->new ArrayList<>()).add(i);}
        for(int j=0;j<os.size();j++)if(free[j]>0){Offer o=os.get(j);og.computeIfAbsent(new OfferSignature(o.capacity(),o.bedrooms(),o.area(),o.quality(),o.garden(),o.stepFree(),o.rent(),o.recurring()),k->new ArrayList<>()).add(j);}
        List<List<Integer>> hs=new ArrayList<>(dg.values()),ts=new ArrayList<>(og.values());
        // Stable keys, rotated monthly, decide only incomparable residual ties.
        hs.sort(Comparator.comparingLong(g->Integer.toUnsignedLong(Objects.hash(ds.get(g.getFirst()).key(),month))));
        int hn=hs.size(),tn=ts.size(),sink=hn+tn+1; List<List<Edge>> graph=new ArrayList<>();for(int i=0;i<=sink;i++)graph.add(new ArrayList<>());
        double[] nh=new double[hn],nt=new double[tn]; Edge[][] links=new Edge[hn][tn];
        for(int a=0;a<hn;a++){for(int i:hs.get(a))nh[a]+=need[i];edge(graph,0,1+a,nh[a]);}
        for(int b=0;b<tn;b++){for(int j:ts.get(b))nt[b]+=free[j];edge(graph,1+hn+b,sink,nt[b]);}
        for(int a=0;a<hn;a++){
            Demand d=ds.get(hs.get(a).getFirst()); List<Integer> rank=new ArrayList<>();for(int b=0;b<tn;b++)rank.add(b);
            rank.sort(Comparator.<Integer>comparingDouble(b->score(d,os.get(ts.get(b).getFirst()))).reversed()
                    .thenComparingLong(b->Integer.toUnsignedLong(Objects.hash(os.get(ts.get(b).getFirst()).key(),month))));
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
        for(int i=0;i<need.length;i++)need[i]=Math.max(0,need[i]-takeH[i]);
        for(int j=0;j<free.length;j++)free[j]=Math.max(0,free[j]-takeT[j]);
        return placed;
    }
    private static final class Edge { final int to; double left; Edge reverse;Edge(int to,double left){this.to=to;this.left=left;} }
    private static Edge edge(List<List<Edge>> g,int u,int v,double cap){Edge a=new Edge(v,cap),b=new Edge(u,0);a.reverse=b;b.reverse=a;g.get(u).add(a);g.get(v).add(b);return a;}
}
