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
    public static void main(String[] args){
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
        System.out.println(fails==0?"HousingChoiceCheck: ALL PASSED":fails+" FAILED");if(fails>0)System.exit(1);
    }
}
