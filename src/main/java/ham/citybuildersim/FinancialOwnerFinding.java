package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Caused absent-owner and positive fractional-owner laws, registered through DenominationCheck.
 * WHY: the optional-action threshold classified real fractional owners as absent;
 * the native reform scan must retain their claims and forbid raw positions at zero count.
 */
final class FinancialOwnerFinding {
    private record Position(double value,boolean localMoney) { }
    private FinancialOwnerFinding() { }

    static void run() {
        DenominationCheck.out.println("--- absent financial owners and positive fractional claims, caused separately ---");
        HouseholdBalance b=new HouseholdBalance();
        Household absent=b.cell(FamilyStructure.SINGLE_ADULT,PayTier.SKILLED);
        Household below=b.cell(FamilyStructure.COUPLE,PayTier.SKILLED);
        Household above=b.cell(FamilyStructure.COUPLE_CHILD,PayTier.SKILLED);
        double small=HouseholdBalance.EMPTY_CELL/2;
        double large=HouseholdBalance.EMPTY_CELL+small;
        absent.households=0;below.households=small;above.households=large;
        seed(absent);seed(below);seed(above);
        DenominationCheck.assertTrue("caused: an exact-zero count has raw positions before the native census",absent.households()==0&&b.holdsAnything(absent));
        DenominationCheck.assertTrue("caused: positive owners straddle the unchanged optional-action threshold",small>0&&below.isEmpty()&&!above.isEmpty());
        Map<String,Position> belowBefore=positions(below),aboveBefore=positions(above),totalsBefore=totals(b);
        // Real census/followThePeople/fold, with no new income, death, arrival or stock transfer premise.
        b.censusOnly((shape,tier)->tier!=PayTier.SKILLED?0:shape==FamilyStructure.COUPLE?small:shape==FamilyStructure.COUPLE_CHILD?large:0);
        DenominationCheck.assertTrue("native census leaves no raw position without a household",!b.holdsAnything(absent)&&absent.bonds==0);
        DenominationCheck.relative("native census retains the positive fractional count",below.households(),small,1e-9);
        DenominationCheck.relative("native census retains the larger comparison count",above.households(),large,1e-9);
        compare("positive fractional owner retains each position",positions(below),belowBefore,1);
        compare("larger owner retains each position",positions(above),aboveBefore,1);
        compare("census conserves every household-weighted claim",totals(b),totalsBefore,1);

        // Actual paid-flow APIs, with a named external inflow, rather than ending-stock inference.
        double deposit=HouseholdBalance.EMPTY_CELL,coupon=HouseholdBalance.EMPTY_CELL;
        Map<String,Position> beforeCredit=totals(b);
        b.creditDepositInterest(deposit);
        DenominationCheck.assertTrue("actual deposit interest reaches the positive fractional owner",below.depositIncome>0);
        b.creditPaperCoupon(coupon);
        DenominationCheck.assertTrue("actual paper coupon reaches the positive fractional owner",below.paperIncome()>0);
        DenominationCheck.assertTrue("the absent owner receives neither paid flow",absent.depositIncome==0&&absent.paperIncome()==0);
        Map<String,Position> expected=new LinkedHashMap<>(beforeCredit);
        Position cash=expected.get("savings");expected.put("savings",new Position(cash.value()+deposit+coupon,true));
        compare("paid cash enters once and every other weighted claim stays",totals(b),expected,1);

        double factor=Denomination.FACTORS[1];
        Map<String,Position> beforeScale=positions(below),aboveBeforeScale=positions(above),totalBeforeScale=totals(b);
        b.redenominate(1/factor);
        compare("fractional owner's original unit-scale law",positions(below),beforeScale,1/factor);
        compare("larger owner's original unit-scale law",positions(above),aboveBeforeScale,1/factor);
        compare("all weighted claims keep the original unit-scale law",totals(b),totalBeforeScale,1/factor);
        DenominationCheck.assertTrue("reform retains both positive owners and no absent-owner position",b.holdsAnything(below)&&b.holdsAnything(above)&&!b.holdsAnything(absent)&&absent.bonds==0);
    }

    /** Cause every native Stock family, including each security's basis and tax claims. */
    private static void seed(Household c) {
        c.savings=2;c.debt=1;c.abroad=.5;c.studentDebt=.25;c.cars=.25;c.paper=.75;
        c.bondFace.put(1,.5);c.recountBonds();
        for(int k=0;k<Equity.COMPANIES.length;k++) {
            c.shares[k]=(k+1)/10.0;c.basis[k]=(k+1)/20.0;
            c.taxShares[k]=c.shares[k]/2;c.taxClaim[k]=(k+1)/40.0;
        }
        c.lossCarried=.125;c.revolving=.5;c.lateDebt=.1;c.lateShare=.25;c.shutOut=.125;
    }

    /** Match HouseholdBalance.stocks(int[]); also guard the derived bonds rollup. */
    private static Map<String,Position> positions(Household c) {
        Map<String,Position> p=new LinkedHashMap<>();
        money(p,"savings",c.savings);money(p,"debt",c.debt);
        for(int k=0;k<Equity.COMPANIES.length;k++)p.put("shares/"+Equity.COMPANIES[k],new Position(c.shares[k],false));
        p.put("abroad/USD",new Position(c.abroad,false));money(p,"studentDebt",c.studentDebt);
        p.put("cars",new Position(c.cars,false));money(p,"paper",c.paper);
        for(var bond:c.bondFace.entrySet())money(p,"bond/"+bond.getKey(),bond.getValue());
        money(p,"bonds-rollup",c.bonds);
        for(int k=0;k<Equity.COMPANIES.length;k++) {
            money(p,"basis/"+Equity.COMPANIES[k],c.basis[k]);
            p.put("taxShares/"+Equity.COMPANIES[k],new Position(c.taxShares[k],false));
            money(p,"taxClaim/"+Equity.COMPANIES[k],c.taxClaim[k]);
        }
        money(p,"lossCarried",c.lossCarried);p.put("revolving",new Position(c.revolving,false));
        money(p,"lateDebt",c.lateDebt);p.put("lateShare",new Position(c.lateShare,false));
        p.put("shutOut",new Position(c.shutOut,false));return p;
    }
    private static void money(Map<String,Position> p,String name,double value){p.put(name,new Position(value,true));}

    private static Map<String,Position> totals(HouseholdBalance b) {
        Map<String,Position> totals=new LinkedHashMap<>();
        for(Household c:b.cells())for(var e:positions(c).entrySet()) {
            Position value=e.getValue();Position old=totals.get(e.getKey());
            totals.put(e.getKey(),new Position((old==null?0:old.value())+value.value()*c.households(),value.localMoney()));
        }
        return totals;
    }
    private static void compare(String label,Map<String,Position> actual,Map<String,Position> original,double moneyScale) {
        var keys=new TreeSet<>(original.keySet());keys.addAll(actual.keySet());
        for(String key:keys) {
            Position expected=original.get(key),seen=actual.get(key);
            double value=expected==null?0:expected.value()*(expected.localMoney()?moneyScale:1);
            DenominationCheck.relative(label+": "+key,seen==null?0:seen.value(),value,1e-9);
        }
    }
}
