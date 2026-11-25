package ham.citybuildersim;

import java.util.*;

/**
 * Caused cardinality, budget, preference, incumbent and fair-tie findings for typed housing.
 * WHY (RES3): neither a pooled price nor a catalogue table proves who obtains an actual home.
 */
public final class HousingChoiceCheck {
    static int fails;
    static void check(String label,boolean yes){System.out.println((yes?"  ok   ":"  FAIL ")+label);if(!yes)fails++;}
    static void near(String label,double a,double b){check(label+" ("+a+", expected "+b+")",Math.abs(a-b)<=Math.max(1e-8,Math.ulp(b)*32));}
    static HousingChoice.Demand d(String key,double n,int size,int children,int tier,double budget){return new HousingChoice.Demand(key,n,size,children,false,tier,false,budget);}
    static HousingChoice.Offer o(String key,double n,int size,double area,double quality,double garden,boolean accessible,double rent){
        return new HousingChoice.Offer(new HousingInventory.Key(900,key),n,size,size<=2?0:size/2,area,quality,garden,accessible,rent,0);}
    static double count(HousingChoice.Result r,String cell,String home){return r.leases().stream().filter(l->(cell==null||l.cell().equals(cell))&&(home==null||l.home().dwelling().equals(home))).mapToDouble(HousingChoice.Lease::count).sum();}
    static HousingChoice.Result clear(List<HousingChoice.Demand> d,List<HousingChoice.Offer> o){return HousingChoice.clear(d,o,List.of(),7);}
    static void retainedHalfLeases(){
        System.out.println("--- funded half-paying incumbents precede a rich new mover ---");
        var home=o("paired",1,2,40,1,0,false,20);
        var a=d("old-a",1,1,0,0,12);var b=d("old-b",1,1,0,0,12);var rich=d("new-rich",1,1,0,5,100);
        var old=List.of(new HousingChoice.Lease(a.key(),home.key(),.5,5,1,new HousingChoice.Funding(0,1,0)),new HousingChoice.Lease(b.key(),home.key(),.5,5,1,new HousingChoice.Funding(0,1,0)));
        var r=HousingChoice.clear(List.of(rich,b,a),List.of(home),old,8);
        near("both old households keep their actual half shares",r.leases().stream().filter(l->!l.cell().equals(rich.key())).mapToDouble(HousingChoice.Lease::households).sum(),2);
        near("a rich new mover cannot displace those funded incumbents",count(r,rich.key(),null),0);
        near("one real door is occupied once",count(r,null,null),1);
        near("only the new mover remains unplaced",r.unplaced(),1);
        near("retained and unplaced households close the source",r.leases().stream().mapToDouble(HousingChoice.Lease::households).sum()+r.unplaced(),a.count()+b.count()+rich.count());
        near("the two half charges follow the current class quote",r.leases().stream().mapToDouble(HousingChoice.Lease::bill).sum(),20);
        check("every retained half share remains within its funded budget",r.leases().stream().allMatch(l->l.bill()<=12*l.households()));
        var reversed=HousingChoice.clear(List.of(a,b,rich),List.of(home),List.of(old.get(1),old.get(0)),8);
        check("incumbent order confers no priority",r.leases().equals(reversed.leases()));
    }

    static void mixedContracts(){
        System.out.println("--- coalescing cannot subsidize an unaffordable full-price incumbent ---");
        var home=o("mixed",2,6,120,1,0,false,20);
        var d=d("mixed",2,1,0,0,16);
        var merged=HousingChoice.clear(List.of(d),List.of(home),List.of(new HousingChoice.Lease(d.key(),home.key(),1.5,30,2,new HousingChoice.Funding(1,1,0))),8,false);
        var split=HousingChoice.clear(List.of(d),List.of(home),List.of(new HousingChoice.Lease(d.key(),home.key(),1,20,1,new HousingChoice.Funding(1,0,0)),new HousingChoice.Lease(d.key(),home.key(),.5,10,1,new HousingChoice.Funding(0,1,0))),8,false);
        near("merged B16 retains only the individually affordable half household",merged.leases().stream().mapToDouble(HousingChoice.Lease::households).sum(),1);
        near("split B16 retains only the individually affordable half household",split.leases().stream().mapToDouble(HousingChoice.Lease::households).sum(),1);
        near("merged and split contracts retain equivalent physical space",count(merged,null,null),count(split,null,null));
        var full=HousingChoice.clear(List.of(d("full",1,1,0,0,20)),List.of(home),List.of(new HousingChoice.Lease("full",home.key(),1,20,1,new HousingChoice.Funding(1,0,0))),8,false);
        near("pure full B20 remains stable",full.kept(),1);
        var half=HousingChoice.clear(List.of(d("half",1,1,0,0,16)),List.of(home),List.of(new HousingChoice.Lease("half",home.key(),.5,10,1,new HousingChoice.Funding(0,1,0))),8,false);
        near("pure half B16 remains stable",half.kept(),.5);
    }
    static List<HousingChoice.Lease> canonical(List<HousingChoice.Lease> leases){return leases.stream().sorted(Comparator.comparing(HousingChoice.Lease::cell).thenComparing(l->l.home().toString())).toList();}
    static void paidExcessContracts(){
        System.out.println("--- paid excess proof boundaries preserve held claims, modes and actual stock ---");
        var a=o("A",1,6,120,1,0,false,20);var b=o("B",1,6,120,1,0,false,20);
        var pair=new HousingChoice.Lease("old",a.key(),1,20,2,new HousingChoice.Funding(0,2,0));
        var old=List.of(pair);var ds=List.of(d("old",2,2,0,0,20));
        var p=HousingChoice.completePaidExcess(ds,List.of(a,b),old,7);
        near("whole affordable paid excess moves to vacant B",p.moved(),1);
        check("old A footprint and bill remain exact",p.leases().getFirst().count()==1&&p.leases().getFirst().bill()==20);
        check("old remaining payer becomes full truthfully",p.leases().getFirst().funding().equals(new HousingChoice.Funding(1,0,0)));
        check("new B takes exactly one normal full payer",p.leases().getLast().funding().equals(new HousingChoice.Funding(1,0,0))&&p.leases().getLast().households()==1);
        // RES3c, 2026-11-25: every half paired where the payers left equal the doors only in exact arithmetic (the 1900
        // town's month 26, CpiReferenceFinding's: a D90 lease of 0.0015320668 doors). The households less the pairs came
        // out a rounding under the doors, the Invalid lease stopped the month and the home fuel was never struck.
        double edgeFull=0.001322724656172436,edgeHalf=4.186842980481003E-4,edgeCount=0.0015320668051964862,edgeHouseholds=0.0017414089542205362;
        double edgePairs=edgeHalf/2;
        check("fixture: the households less the paired halves come out under the doors in floating point",edgeHouseholds-edgePairs<edgeCount);
        var edgeHome=o("A-edge",edgeCount,6,120,1,0,false,20);   // the lease fills its own door, so a mover goes to B
        var edge=new HousingChoice.Lease("edge",edgeHome.key(),edgeCount,edgeCount*20,edgeHouseholds,new HousingChoice.Funding(edgeFull,edgeHalf,0));
        HousingChoice.Completion ep=null;
        try{ep=HousingChoice.completePaidExcess(List.of(d("edge",edgeHouseholds,2,0,0,30)),List.of(edgeHome,b),List.of(edge),7);}catch(IllegalArgumentException e){System.out.println("   "+e);}
        check("every half paired at the doors' edge completes, with no Invalid lease",ep!=null);
        if(ep!=null){
            var stays=ep.leases().stream().filter(l->l.home().equals(edgeHome.key())).findFirst().orElseThrow();
            check("...every pair's mover moves",Math.abs(ep.moved()-edgePairs)<=1e-12*edgePairs);
            check("...the old door keeps its doors and its bill exactly",stays.count()==edgeCount&&stays.bill()==edge.bill());
            check("...its households all full payers now, and no fewer than its doors",stays.funding().half()==0&&stays.households()>=stays.count());
        }
        p=HousingChoice.completePaidExcess(List.of(d("old",2,2,0,0,12)),List.of(a,b),old,7);
        check("B12 cannot leave an unaffordable remaining full payer",p.moved()==0&&p.leases().equals(old));
        var hosted=List.of(new HousingChoice.Lease("host",a.key(),.5,20,1,new HousingChoice.Funding(1,0,0)),new HousingChoice.Lease("guest",a.key(),.5,0,1,new HousingChoice.Funding(0,0,1)));
        p=HousingChoice.completePaidExcess(List.of(d("host",1,2,0,0,20),d("guest",1,2,0,0,0)),List.of(a,b),hosted,7);
        check("full funded donor and sponsored guest remain exact",p.moved()==0&&p.leases().equals(hosted));
        var a2=o("A",2,6,120,1,0,false,20);
        var fullMixed=List.of(new HousingChoice.Lease("host",a2.key(),1.5,40,2,new HousingChoice.Funding(2,0,0)),hosted.getLast());
        p=HousingChoice.completePaidExcess(List.of(d("host",2,2,0,0,20),d("guest",1,2,0,0,0)),List.of(a2,b),fullMixed,7);
        check("mixed physical full donors contain no paid excess",p.moved()==0&&p.leases().equals(fullMixed));
        var a15=o("A",1.5,6,120,1,0,false,20);
        var halfMixed=List.of(new HousingChoice.Lease("host",a15.key(),1,30,2,new HousingChoice.Funding(1,1,0)),hosted.getLast());
        p=HousingChoice.completePaidExcess(List.of(d("host",2,2,0,0,20),d("guest",1,2,0,0,0)),List.of(a15,b),halfMixed,7);
        near("mixed contract moves only its half-price deficit",p.moved(),.5);
        check("mixed contract keeps sponsored claim exact",p.leases().get(1).equals(halfMixed.get(1)));
        var richOld=List.of(pair,new HousingChoice.Lease("rich",b.key(),1,20));
        p=HousingChoice.completePaidExcess(List.of(ds.getFirst(),d("rich",1,2,0,5,100)),List.of(a,b),richOld,7);
        check("all normal movers reserve B before paid upgrades",p.moved()==0&&p.leases().equals(richOld));
        var equivalent=List.of(new HousingChoice.Lease("left",a2.key(),1,20,2,new HousingChoice.Funding(0,2,0)),new HousingChoice.Lease("right",a2.key(),1,20,2,new HousingChoice.Funding(0,2,0)));
        var sources=List.of(d("left",2,2,0,0,20),d("right",2,2,0,0,20));
        p=HousingChoice.completePaidExcess(sources,List.of(a2,b),equivalent,7);
        near("equivalent deficit receives half the one normal vacancy",p.leases().getFirst().households(),1.5);
        var reverse=HousingChoice.completePaidExcess(List.of(sources.getLast(),sources.getFirst()),List.of(b,a2),List.of(equivalent.getLast(),equivalent.getFirst()),7);
        check("paid excess ties are independent of input order",canonical(p.leases()).equals(canonical(reverse.leases())));
        p=HousingChoice.completePaidExcess(List.of(d("old",2,4,2,0,20)),List.of(a,o("studio",1,2,40,1,0,false,20)),old,7);
        check("children cannot upgrade into a compact vacancy",p.moved()==0&&p.leases().equals(old));
        p=HousingChoice.completePaidExcess(ds,List.of(a,o("premium",1,6,120,1,0,false,100)),old,7);
        check("unaffordable premium spare creates no placement",p.moved()==0&&p.leases().equals(old));
        var recurring=new HousingChoice.Offer(b.key(),1,6,3,120,1,0,false,20,1);
        p=HousingChoice.completePaidExcess(ds,List.of(a,recurring),old,7);
        check("new recurring costs count against the original source",p.moved()==0&&p.leases().equals(old));
    }
    static void fractionalExcessBook(){
        System.out.println("--- fractional new books keep old claims exact under running and coalesced stock caps ---");
        boolean stock=true,sources=true,claims=true,resources=true;int frames=0;
        for(double scale:new double[]{1e-6,1,1e6})for(int frame=0;frame<12;frame++){
            List<HousingChoice.Demand> ds=new ArrayList<>();List<HousingChoice.Lease> old=new ArrayList<>();
            double aStock=0,bStock=0;double[] a=new double[32],b=new double[32];
            for(int i=0;i<a.length;i++){a[i]=.1*(i+1)*scale;b[i]=.03*(i+2)*scale;aStock+=a[i];bStock+=b[i];}
            var home=o("fraction-A",aStock,6,120,1,0,false,20);var spare=o("fraction-B",bStock+.07*(frame+1)*scale,6,120,1,0,false,20);
            for(int i=0;i<a.length;i++){String key=String.format(Locale.ROOT,"fraction-%02d",i);
                ds.add(d(key,2*a[i]+b[i],2,0,0,30));
                old.add(new HousingChoice.Lease(key,home.key(),a[i],20*a[i],2*a[i],new HousingChoice.Funding(0,2*a[i],0)));
                old.add(new HousingChoice.Lease(key,spare.key(),b[i],20*b[i]));
            }
            var p=HousingChoice.completePaidExcess(ds,List.of(home,spare),old,7);frames++;
            for(var o:List.of(home,spare))stock&=p.leases().stream().filter(l->l.home().equals(o.key())).mapToDouble(HousingChoice.Lease::count).reduce(0,(x,y)->x+y)<=o.count();
            for(int i=0;i<ds.size();i++){var d=ds.get(i);double hh=0,cost=0;
                for(var l:p.leases())if(l.cell().equals(d.key())){hh+=l.households();cost+=l.bill();
                    if(l.home().equals(home.key()))claims&=l.count()==a[i]&&l.bill()==20*a[i];}
                sources&=Math.abs(hh-d.count())<=FamilyModel.packingRounding(d.count());
                resources&=cost<=d.count()*d.budget()+FamilyModel.packingRounding(cost);
            }
        }
        check(frames+" fractional books respect strict final stock including equal-key coalescence",stock);
        check("fractional completion preserves every old A footprint and bill bit for bit",claims);
        check("fractional completion conserves each actual household source",sources);
        check("fractional completion reserves each source's old recurring resources",resources);
    }
    /** Tenure (RES3c, 2026-11-25): the doors follow the people across cells, and a sitter keeps its door within the census line. */
    static void tenureFollowsThePeople(){
        System.out.println("--- tenure follows the people: a household that changes cell takes its door, a sitter keeps it within the census line ---");
        var home=o("four",10,4,90,1,0,false,10);
        check("fixture: a door built for four, two bedrooms, holds six on the census line",home.capacity()==4&&home.people()==HousingChoice.line(2)&&home.people()==6);
        String small="COUPLE_CHILD:UNSKILLED",grown="LARGE_FAMILY:UNSKILLED";
        var lease=new HousingChoice.Lease(small,home.key(),8,80);
        var route=new HouseholdBalance.HousingRoute(Map.of(small,10.0),Map.of(small,Map.of(grown,2.5)));
        var carried=HousingMarket.carry(List.of(lease),route);
        near("a quarter of the cell's households changed cell and take a quarter of its doors",carried.stream().filter(l->l.cell().equals(grown)).mapToDouble(HousingChoice.Lease::count).sum(),2);
        near("...the households that stayed keep the rest",carried.stream().filter(l->l.cell().equals(small)).mapToDouble(HousingChoice.Lease::count).sum(),6);
        near("...and the bills go with the doors, none made or lost",carried.stream().mapToDouble(HousingChoice.Lease::bill).sum(),80);
        check("a route with no one changing cell carries nothing",HousingMarket.carry(List.of(lease),HouseholdBalance.HousingRoute.NONE).equals(List.of(lease)));
        var family=new HousingChoice.Demand(grown,2,6,4,false,0,false,0);
        var held=carried.stream().filter(l->l.cell().equals(grown)).toList();
        var sat=HousingChoice.clear(List.of(family),List.of(home),held,8,true,Set.of(grown));
        near("a sitting family of six keeps its door past the four it was built for, within the census line, on no budget",sat.kept(),2);
        near("...its bill still due at the class's quote: the landlord carries the arrears",sat.leases().stream().filter(l->l.cell().equals(grown)).mapToDouble(HousingChoice.Lease::bill).sum(),20);
        near("a household that can be evicted and cannot pay keeps no door",HousingChoice.clear(List.of(family),List.of(home),held,8,true,Set.of()).kept(),0);
        var seven=new HousingChoice.Demand(grown,2,7,5,false,0,false,0);
        near("...nor does a sitter past the census line",HousingChoice.clear(List.of(seven),List.of(home),held,8,true,Set.of(grown)).kept(),0);
    }

    public static void main(String[] args){
        tenureFollowsThePeople();
        mixedContracts();
        paidExcessContracts();
        fractionalExcessBook();
        retainedHalfLeases();
        System.out.println("--- suitable placements before preferences; studios cannot take children ---");
        var family=d("family",1,4,2,0,100);var single=d("single",1,1,0,0,100);
        var studios=o("studio",100,2,40,1,0,false,10);var home=o("family",1,4,80,1,0,false,10);
        var r=clear(List.of(single,family),List.of(home,studios));
        near("all compatible households placed",r.unplaced(),0);near("child family receives family door",count(r,"family","family"),1);
        r=clear(List.of(d("family",10,4,2,0,100)),List.of(home,studios));
        near("compact surplus does not erase family shortage",r.unplaced(),9);near("children never assigned a studio",count(r,"family","studio"),0);

        System.out.println("--- full recurring budgets and premium vacancy ---");
        var premium=o("premium",10,6,240,1.4,150,false,90);
        var ordinary=o("ordinary",1,4,75,1,0,false,10);
        var poor=d("poor",1,4,2,0,50);var rich=d("rich",1,4,2,5,150);
        r=clear(List.of(poor,rich),List.of(premium,ordinary));
        near("ordinary home reserved for affordable household",count(r,"poor","ordinary"),1);
        near("wealthy family chooses space, finish and garden",count(r,"rich","premium"),1);
        near("unoccupied premium homes generate no bill",r.leases().stream().mapToDouble(HousingChoice.Lease::bill).sum(),100);
        r=clear(List.of(poor),List.of(premium));near("unaffordable vacancy has a reason",r.unaffordable(),1);
        var fee=new HousingChoice.Offer(premium.key(),1,6,4,240,1.4,150,false,40,20);
        r=clear(List.of(poor),List.of(fee));near("all recurring obligations count before a preference",r.unaffordable(),1);

        System.out.println("--- retired accessibility and retained incumbents ---");
        var retired=new HousingChoice.Demand("retired",1,1,0,true,3,false,100);
        var ground=o("ground",1,2,55,1,0,true,20);var upstairs=o("upstairs",1,2,75,1,0,false,20);
        r=clear(List.of(retired),List.of(ground,upstairs));near("known accessibility wins for retired household",count(r,"retired","ground"),1);
        r=HousingChoice.clear(List.of(retired),List.of(ground,upstairs),List.of(new HousingChoice.Lease("retired",upstairs.key(),1,20)),8);
        near("valid affordable incumbent remains",r.kept(),1);near("soft preference does not force a move",count(r,"retired","upstairs"),1);
        var dear=new HousingChoice.Offer(upstairs.key(),1,2,1,75,1,0,false,101,0);
        r=HousingChoice.clear(List.of(retired),List.of(ground,dear),r.leases(),9);
        near("unaffordable incumbent can move",count(r,"retired","ground"),1);

        System.out.println("--- rationed fractional demand and genuinely equivalent ties ---");
        var a=d("a",2,1,0,0,100);var b=d("b",6,1,0,0,100);
        r=clear(List.of(a,b),List.of(o("x",2,2,40,1,0,false,10),o("y",2,2,40,1,0,false,10)));
        near("equivalent demand split pro rata",count(r,"a",null),1);near("larger equivalent demand gets its share",count(r,"b",null),3);
        near("equivalent offers split pro rata",count(r,"a","x"),.5);
        var reversed=clear(List.of(b,a),List.of(o("y",2,2,40,1,0,false,10),o("x",2,2,40,1,0,false,10)));
        check("reversing input order changes no lease",r.leases().equals(reversed.leases()));
        r=clear(List.of(d("poor",2,1,0,0,0),d("funded",6,1,0,0,100)),List.of(o("free-host",4,2,40,1,0,false,0)));
        near("budgets give no priority on a genuinely free offer",count(r,"poor",null),1);
        near("otherwise equivalent free guests split pro rata",count(r,"funded",null),3);
        r=clear(List.of(d("fraction",.75,1,0,0,100)),List.of(o("x",.25,2,40,1,0,false,10)));
        near("partial placement is conserved",count(r,null,null),.25);near("remaining demand counted once",r.unplaced(),.5);
        var huge=clear(List.of(d("a",2e9,1,0,0,100),d("b",6e9,1,0,0,100)),List.of(o("x",2e9,2,40,1,0,false,10),o("y",2e9,2,40,1,0,false,10)));
        check("count scaling creates no additional assignment rows",huge.leases().size()==4);
        near("scaled equivalent allocation is still pro rata",count(huge,"a",null),1e9);
        List<HousingChoice.Demand> fractional=new ArrayList<>();
        for(int i=0;i<64;i++)fractional.add(d("fraction-"+i,.1*(i+1),1,0,0,100));
        r=clear(fractional,List.of(o("four",4,2,40,1,0,false,10)));
        check("decimal pro-rata quantities never exceed four physical homes",count(r,null,null)<=4);
        double total=fractional.stream().mapToDouble(HousingChoice.Demand::count).sum();
        check("decimal allocations and residual demand conserve the source",Math.abs(count(r,null,null)+r.unplaced()-total)<=FamilyModel.packingRounding(total));
        boolean first=false,second=false;
        for(int month=0;month<24;month++){
            var rotated=HousingChoice.clear(List.of(d("first",1,1,0,0,100),d("second",1,1,0,1,100)),List.of(o("one",1,2,40,1,0,false,10)),List.of(),month);
            first|=count(rotated,"first",null)>0;second|=count(rotated,"second",null)>0;
        }
        check("incomparable ties rotate across months without enum entitlement",first&&second);

        System.out.println("--- emergency crowding is explicit, suitable placements happen first ---");
        r=clear(List.of(d("large",1,6,3,0,100)),List.of(home));near("smaller family home is emergency crowding",r.crowded(),1);near("crowding still occupies one real door",count(r,null,null),1);
        r=clear(List.of(d("large",1,6,3,0,100)),List.of(studios));near("emergency never bypasses adults-only rule",count(r,null,null),0);
        var adult=d("adult",1,2,0,0,100);var large=d("large",1,6,4,0,100);
        var compact=o("compact",1,2,32,1,0,false,10);var smallerFamily=o("smaller-family",1,4,70,1,0,false,10);
        check("fixture adult softly prefers the family space",HousingChoice.score(adult,smallerFamily)>HousingChoice.score(adult,compact));
        r=clear(List.of(adult,large),List.of(smallerFamily,compact));
        near("residual emergency reassignment preserves one normal placement",r.suitable(),1);
        near("residual emergency reassignment places the large family",r.crowded(),1);
        near("soft adult preference cannot strand a compatible family",r.unplaced(),0);
        near("adult moves to the compact normal door",count(r,"adult","compact"),1);
        near("large family uses the undersized family door explicitly",count(r,"large","smaller-family"),1);
        var opposite=clear(List.of(large,adult),List.of(compact,smallerFamily));
        check("emergency reassignment is independent of input order",r.leases().equals(opposite.leases()));
        r=HousingChoice.clear(List.of(adult,large),List.of(smallerFamily,compact),
                List.of(new HousingChoice.Lease("adult",smallerFamily.key(),1,10)),7);
        near("emergency does not evict a valid incumbent",r.kept(),1);
        near("incumbent's family door stays occupied by that incumbent",count(r,"adult","smaller-family"),1);
        near("blocked family is counted when an incumbent cannot move",r.unplaced(),1);
        var staying=d("staying",100,1,0,0,100);var moving=d("moving",1,1,0,0,100);
        var occupied=o("occupied",100,2,40,1,0,false,10);var vacant=o("vacant",1,2,40,1,0,false,10);
        r=HousingChoice.clear(List.of(staying,moving),List.of(occupied,vacant),
                List.of(new HousingChoice.Lease("staying",occupied.key(),Math.nextDown(100.0),Math.nextDown(100.0)*10)),7);
        check("an incumbent's one-ULP remainder creates no extra normal mover",r.suitable()==1);
        check("rounding residual eligibility never invents an incumbent or a door",count(r,"staying",null)<=100&&count(r,null,null)<=101);
        System.out.println(fails==0?"HousingChoiceCheck: ALL PASSED":fails+" FAILED");if(fails>0)System.exit(1);
    }
}
