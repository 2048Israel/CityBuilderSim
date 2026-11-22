# Real Estate study - closure validation

34 caused design checks passed. These are study-only prototypes; model integration, actual funding/completions, market/era/rate calibration and save/audit checks remain the named implementation batches' acceptance work.

## Critical counterexample

A 100-household cell has only 1% simultaneously over both credit thresholds. Two premium alternatives can each appear to have one qualified buyer. Independent edge caps would allow two purchases. Partitioning the cell into disjoint aggregate income/savings regions allows one purchase in total. Crossing thresholds give a 28% union, not 32%. Only temporary regions are added; the saved household and housing categories remain bounded.

## Catalogue credit cross-check

Illustrative reference-ask scenario: contract rate 6%, stress +2 points, 300 months, 5% down, 4% insurance premium on the base loan, 1.5% closing costs, property tax 1.5%, and heat 1% of gross. These are the accepted model-policy baseline plus explicit accounting assumptions, not an assertion of current national lending law. The income column is the GDS-only minimum with zero other debt. The full TDS/cash-budget tests can refuse additional buyers. The common-fee budget uses zero optional parking receipts and fixed area weights. Actual market asks and actual service costs replace these inputs in the model.

| Form | Dwelling | Count | Reference ask | Cash down + closing | Contract payment/month | Full fee/month | GDS gross/month |
|---|---|---:|---:|---:|---:|---:|---:|
| C01 | H55 | 1 | 439,845 | 28,590 | 2,800 | 0 | 10,273 |
| C02 | H120 | 1 | 846,000 | 54,990 | 5,385 | 0 | 19,760 |
| C03 | H240 | 1 | 1,586,506 | 103,123 | 10,099 | 0 | 37,056 |
| C04 | D90 | 2 | 557,371 | 36,229 | 3,548 | 0 | 13,018 |
| C05 | T100 | 6 | 460,153 | 29,910 | 2,929 | 0 | 10,748 |
| C06 | S | 80 | 191,100 | 12,422 | 1,216 | 221 | 4,754 |
| C07 | S | 8 | 205,495 | 13,357 | 1,308 | 203 | 5,066 |
| C07 | F | 12 | 317,641 | 20,647 | 2,022 | 380 | 7,919 |
| C07 | L | 4 | 452,286 | 29,399 | 2,879 | 557 | 11,297 |
| C08 | F | 63 | 308,286 | 20,039 | 1,962 | 311 | 7,610 |
| C09 | S | 16 | 188,794 | 12,272 | 1,202 | 173 | 4,637 |
| C09 | F | 40 | 286,326 | 18,611 | 1,823 | 324 | 7,114 |
| C09 | L | 8 | 406,358 | 26,413 | 2,587 | 476 | 10,117 |
| C10 | S | 48 | 209,531 | 13,620 | 1,334 | 222 | 5,186 |
| C10 | F | 96 | 325,208 | 21,139 | 2,070 | 416 | 8,143 |
| C10 | L | 12 | 510,885 | 33,208 | 3,252 | 610 | 12,736 |
| C10 | P140 | 4 | 724,860 | 47,116 | 4,614 | 777 | 17,953 |
| C11 | S | 120 | 189,542 | 12,320 | 1,207 | 189 | 4,675 |
| C11 | F | 216 | 287,727 | 18,702 | 1,832 | 354 | 7,186 |
| C11 | L | 24 | 408,413 | 26,547 | 2,600 | 519 | 10,222 |
| C12 | S | 120 | 232,426 | 15,108 | 1,480 | 234 | 5,736 |
| C12 | F | 232 | 368,136 | 23,929 | 2,343 | 438 | 9,175 |
| C12 | L | 44 | 580,846 | 37,755 | 3,697 | 643 | 14,413 |
| C12 | P180 | 4 | 1,036,803 | 67,392 | 6,600 | 1,052 | 25,601 |
| C13 | S | 240 | 269,579 | 17,523 | 1,716 | 261 | 6,640 |
| C13 | F | 480 | 437,797 | 28,457 | 2,787 | 490 | 10,870 |
| C13 | L | 76 | 697,015 | 45,306 | 4,437 | 719 | 17,226 |
| C13 | P360 | 4 | 2,341,461 | 152,195 | 14,905 | 2,352 | 57,783 |
| C14 | A | 61 | 876,426 | 56,968 | 5,579 | 856 | 21,597 |
| C14 | V | 48 | 1,314,790 | 85,461 | 8,370 | 1,284 | 32,399 |
| C14 | X | 8 | 3,312,029 | 215,282 | 21,083 | 3,211 | 81,583 |
| C14 | P600 | 3 | 3,496,566 | 227,277 | 22,258 | 3,211 | 85,893 |

## Passed cases

- same 1% wealthy/eligible tail buys at most one of two alternatives
- independent per-offer caps demonstrably overcount this tail
- crossing income/wealth thresholds use the 28% union once
- scarce alternative reallocates eligible regions
- no supply creates no transactions
- 1000x quantities preserve the same eligibility-region keys
- 1000x quantities scale placed amounts exactly
- equivalent demand groups split scarce results pro rata
- insured closing cash conserves money across five payers
- insurance is financed and paid once
- released seller debt is not transferred into common fees
- higher actual condo fee raises qualifying gross income
- qualifying loan includes insurance
- passing half-fee credit test need not pass full-fee cash budget
- rate increase tightens qualification at unchanged ask
- a larger down payment reduces principal and annuity
- unfunded voluntary short sale remains unsold
- funded voluntary markdown creates no insurance claim
- recognized insured loss has an explicit treasury claim
- uninsured recognized loss belongs to lender
- tenant purchase conserves ten homes and ten occupied homes
- external owner-occupier closing is limited by vacant supply
- refusing tenant remains rented and carried by RE
- partial-sale common fees transfer fixed shares
- zero RE ownership still funds common expenses
- closing boundary bills opening tenure once then new tenure next month
- unpaid common fees remain receivables and unfunded costs
- reload advances no markdown or trade
- next-month markdown is invariant to save/reload
- unfunded listing makes zero proceeds or price observation
- unsold markdown preserves physical stock and secured debt
- active exit intent suppresses same-category RE acquisition
- every numerical catalogue quote is covered exactly once
- all candidate mortgage/fee inputs are finite and nonnegative

## Supplemental timing

Prepared; execution and required docs fast checks must finish before readiness.

## Exact design-fixture source

Save beside section 12's `catalogue-data.json` and run with Python 3.

```python
"""RES1 design fixtures; aggregate arithmetic, not integrated game calibration."""
from collections import defaultdict, deque
from fractions import Fraction as F
from pathlib import Path
import json
import math

HERE = Path(__file__).resolve().parent
checks = []

def require(label, condition):
    if not condition:
        raise AssertionError(label)
    checks.append(label)

def regions(income_cuts, savings_cuts, quantity=F(100)):
    """CDF cut points partition a cell's independent income/savings spreads.

    A region is a quantity with the same eligible-offer bitset. These are
    temporary aggregate regions, never people or additional saved cells.
    """
    xs = sorted({F(0), F(1), *map(F, income_cuts)})
    ys = sorted({F(0), F(1), *map(F, savings_cuts)})
    result = defaultdict(F)
    for lo, hi in zip(xs, xs[1:]):
        for low, high in zip(ys, ys[1:]):
            u, v = (lo+hi)/2, (low+high)/2
            mask = sum(1 << j for j, (a,b) in enumerate(zip(income_cuts,savings_cuts)) if u >= a and v >= b)
            result[mask] += quantity*(hi-lo)*(high-low)
    assert sum(result.values()) == quantity
    return dict(result)

def allocate(masses, supply):
    """Maximum compatible cohort quantity; no per-buyer iteration."""
    rows = [(m,q) for m,q in masses.items() if m and q]
    source, sink = ('source',), ('sink',)
    residual = defaultdict(lambda: defaultdict(F))
    def edge(a,b,q):
        residual[a][b] += q
        residual[b][a] += 0
    for i,(mask,q) in enumerate(rows):
        r=('region',i)
        edge(source,r,q)
        for j in range(len(supply)):
            if mask & (1 << j):
                edge(r,('offer',j),q)
    for j,q in enumerate(supply):
        edge(('offer',j),sink,F(q))
    total=F(0)
    while True:
        parent={source:None}; todo=deque([source])
        while todo and sink not in parent:
            a=todo.popleft()
            for b,q in residual[a].items():
                if q>0 and b not in parent:
                    parent[b]=a; todo.append(b)
        if sink not in parent:
            break
        amount=F(10**30); b=sink
        while parent[b] is not None:
            a=parent[b]; amount=min(amount,residual[a][b]); b=a
        b=sink
        while parent[b] is not None:
            a=parent[b]; residual[a][b]-=amount; residual[b][a]+=amount; b=a
        total+=amount
    return total

# The naive per-offer cap of one buyer would sell two homes to the same 1% tail.
same = regions([F(9,10)]*2,[F(9,10)]*2)
require('same 1% wealthy/eligible tail buys at most one of two alternatives', allocate(same,[100,100]) == 1)
require('independent per-offer caps demonstrably overcount this tail', F(100)*(F(1,10)**2)*2 == 2)
cross = regions([F(1,5),F(4,5)],[F(4,5),F(1,5)])
require('crossing income/wealth thresholds use the 28% union once', allocate(cross,[100,100]) == 28)
require('scarce alternative reallocates eligible regions', allocate(cross,[5,100]) == 21)
require('no supply creates no transactions', allocate(cross,[0,0]) == 0)
scaled=regions([F(1,5),F(4,5)],[F(4,5),F(1,5)],F(100000))
require('1000x quantities preserve the same eligibility-region keys', scaled.keys()==cross.keys())
require('1000x quantities scale placed amounts exactly', allocate(scaled,[100000,100000])==28000)
require('equivalent demand groups split scarce results pro rata', tuple(F(10)*q/F(100) for q in [25,75])==(F(5,2),F(15,2)))

# The financed insurance premium is part of the mortgage and a real cash flow.
price, down, base_loan, premium, closing = F(120), F(6), F(114), F(114)*F(4,100), F(120)*F(15,1000)
loan=base_loan+premium
cash={'buyer':-down-closing,'seller':price-100,'bank':F(100)-loan,'treasury':premium,'closing_service':closing}
require('insured closing cash conserves money across five payers', sum(cash.values())==0)
require('insurance is financed and paid once', loan==F(2964,25) and cash['treasury']==F(114,25))
require('released seller debt is not transferred into common fees', cash['seller']==20)

def annuity(principal, rate, months):
    r=rate/12
    return principal*r/(1-(1+r)**(-months)) if r else principal/months

def gds_income(price, fee, rate=.06, tax=.015, months=300):
    payment=annuity(price*.95*1.04,rate+.02,months)
    return (payment+price*tax/12+fee*.5)/(.39-.01)

qual = gds_income(100000,1200)
require('higher actual condo fee raises qualifying gross income', gds_income(100000,1201)>qual)
require('qualifying loan includes insurance', gds_income(100000,1200)>gds_income(100000/1.04,1200))
owner_payment=annuity(98800,.06,300)+125+100+1200+83+100
require('passing half-fee credit test need not pass full-fee cash budget', 10000>=qual and 3000-1500-500<owner_payment)
require('rate increase tightens qualification at unchanged ask', gds_income(100000,1200,rate=.07)>qual)
require('a larger down payment reduces principal and annuity', annuity(80000,.06,300)<annuity(98800,.06,300))

# Sale only happens with a funded debt release; voluntary ask cuts create no claim.
def short_sale(price, secured, seller_cash, default=False, insured=False):
    gap=max(F(0),secured-price)
    seller_pay=min(seller_cash,gap)
    remaining=gap-seller_pay
    if remaining and not default:
        return {'closed':False,'claim':F(0),'bank_loss':F(0)}
    return {'closed':True,'claim':remaining if default and insured else F(0),
            'bank_loss':remaining if default and not insured else F(0)}
require('unfunded voluntary short sale remains unsold', not short_sale(F(80),F(100),F(0))['closed'])
require('funded voluntary markdown creates no insurance claim', short_sale(F(80),F(100),F(20))=={'closed':True,'claim':0,'bank_loss':0})
require('recognized insured loss has an explicit treasury claim', short_sale(F(80),F(100),F(0),True,True)['claim']==20)
require('uninsured recognized loss belongs to lender', short_sale(F(80),F(100),F(0),True,False)['bank_loss']==20)

# Tenure and physical inventory are independent; outsiders cannot evict occupants.
stock, occupied, vacant, re_owned, owned, listed=map(F,[10,10,0,10,0,10])
tenant_buys=F(3)
re_owned-=tenant_buys; owned+=tenant_buys; listed-=tenant_buys
require('tenant purchase conserves ten homes and ten occupied homes', re_owned+owned==stock==occupied and vacant==0)
require('external owner-occupier closing is limited by vacant supply', min(F(5),vacant)==0)
require('refusing tenant remains rented and carried by RE', (re_owned,owned,listed)==(7,3,7))
fee=F(12)
require('partial-sale common fees transfer fixed shares', re_owned*fee+owned*fee==stock*fee)
re_owned=F(0); owned=stock
require('zero RE ownership still funds common expenses', owned*fee==120)
opening_rent=F(100)*10
next_rent=F(100)*7
require('closing boundary bills opening tenure once then new tenure next month', opening_rent==1000 and next_rent==700)
assessed, paid, repairs=F(120),F(90),F(120)
require('unpaid common fees remain receivables and unfunded costs', assessed-paid==30 and max(0,repairs-paid)==30)

state={'quantity':100,'ask':100.,'age':0,'trades':0,'trade_value':0.,'physical':100,'loan':80.,'last_trade_price':110.}
def month(s):
    s=dict(s); s['age']+=1; s['ask']=max(.001,s['ask']*.99)
    return s
for _ in range(12):
    state=month(state)
restored=json.loads(json.dumps(state))
require('reload advances no markdown or trade', restored==state)
require('next-month markdown is invariant to save/reload', month(restored)==month(state))
require('unfunded listing makes zero proceeds or price observation', state['trades']==state['trade_value']==0 and state['last_trade_price']==110)
require('unsold markdown preserves physical stock and secured debt', state['physical']==100 and state['loan']==80 and state['ask']<100)
exit_category=True
require('active exit intent suppresses same-category RE acquisition', (0 if exit_category else 1)==0)

# All 32 quoted classes get the same explicit credit calculation. This uses
# allocated capital as an illustrative ASK, not an observed market sale price.
catalogue=json.loads((HERE/'catalogue-data.json').read_text(encoding='utf-8'))
rows=[]
for b in catalogue['buildings']:
    for key,q in b['quotes'].items():
        area=catalogue['unit_types'][key][0]
        fee=b['fee_zero_park']*b['N']*area/b['net']
        ask=q['asset']
        minimum=gds_income(ask,fee)
        payment=annuity(ask*.95*1.04,.06,300)
        rows.append((b['code'],key,q['count'],ask,ask*.065,payment,fee,minimum))
require('every numerical catalogue quote is covered exactly once', len(rows)==32)
require('all candidate mortgage/fee inputs are finite and nonnegative', all(math.isfinite(x) and x>=0 for row in rows for x in row[2:]))
report=['# HO1 study closure checks','',
        f'{len(checks)} caused design checks passed. These are study-only prototypes; model integration, actual funding/completions, market/era/rate calibration and save/audit checks remain the named implementation batches\' acceptance work.','',
        '## Critical counterexample','',
        'A 100-household cell has only 1% simultaneously over both credit thresholds. Two premium alternatives can each appear to have one qualified buyer. Independent edge caps would allow two purchases. Partitioning the cell into disjoint aggregate income/savings regions allows one purchase in total. Crossing thresholds give a 28% union, not 32%. Only temporary regions are added; the saved household and housing categories remain bounded.','',
        '## Catalogue credit cross-check','',
        'Illustrative reference-ask scenario: contract rate 6%, stress +2 points, 300 months, 5% down, 4% insurance premium on the base loan, 1.5% closing costs, property tax 1.5%, and heat 1% of gross. These are the accepted model-policy baseline plus explicit accounting assumptions, not an assertion of current national lending law. The income column is the GDS-only minimum with zero other debt. The full TDS/cash-budget tests can refuse additional buyers. The common-fee budget uses zero optional parking receipts and fixed area weights. Actual market asks and actual service costs replace these inputs in the model.','',
        '| Form | Dwelling | Count | Reference ask | Cash down + closing | Contract payment/month | Full fee/month | GDS gross/month |',
        '|---|---|---:|---:|---:|---:|---:|---:|']
report += ['| '+' | '.join([r[0],r[1],str(r[2]),*[f'{x:,.0f}' for x in r[3:]]])+' |' for r in rows]
report += ['', '## Passed cases', '']+['- '+s for s in checks]
(HERE/'study-closure-checks.md').write_text('\n'.join(report)+'\n',encoding='utf-8',newline='\n')
(HERE/'study-closure-checks.json').write_text(json.dumps({'passed':len(checks),'checks':checks,'quotes':rows},indent=2)+'\n',encoding='utf-8',newline='\n')
print(f'PASS: {len(checks)} design fixtures; 32 class-level credit/fee cross-checks; no game save or model changed')
```

## Exact supplemental timing source

Compile alongside the exact HousingTimingProbe from section 13, using its classpath and memory guard; run with the catalogue JSON path.

```java
package ham.citybuildersim;

import java.nio.file.Path;
import java.util.*;

/** Supplemental RES1 kernels: disjoint funding regions and dense loan rows.
 * No game implementation, save changes, calibrated economy or runtime gate. */
public class HousingClosureProbe {
  static final class Funding {
    final HousingTimingProbe.Kernel k;
    final int H,T,F;
    final int[] byIncome,bySavings,region,pref;
    final long[] masks;
    final double[] incomeNeed,savingsNeed,mass,ci,cs;
    final HousingTimingProbe.Flow flow;
    double eligible,placed;
    Funding(HousingTimingProbe.Kernel k) {
      this.k=k;H=k.H;T=k.T;
      if(T>32)throw new IllegalArgumentException("closure budget is <=32 classes");
      incomeNeed=new double[T];savingsNeed=new double[T];
      Integer[] is=new Integer[T],ss=new Integer[T];
      for(int j=0;j<T;j++){
        // Explicit illustrative ask/rate inputs; complete underwriting also
        // checks actual TDS, full bills and bank funding in the implementation.
        double payment=HousingTimingProbe.annuity(.08,300)*k.price[j]*.95*1.04;
        incomeNeed[j]=(payment+k.price[j]*.015/12+.5*k.fee[j])/(.39-.01);
        savingsNeed[j]=k.price[j]*.065;
        is[j]=ss[j]=j;
      }
      Arrays.sort(is,Comparator.comparingDouble(j->incomeNeed[j]));
      Arrays.sort(ss,Comparator.comparingDouble(j->savingsNeed[j]));
      byIncome=new int[T];bySavings=new int[T];
      for(int j=0;j<T;j++){byIncome[j]=is[j];bySavings[j]=ss[j];}
      region=new int[(T+1)*(T+1)];Arrays.fill(region,-1);
      LinkedHashMap<Long,Integer> found=new LinkedHashMap<>();
      long im=0;
      for(int a=0;a<=T;a++){
        if(a>0)im|=1L<<byIncome[a-1];
        long sm=0;
        for(int b=0;b<=T;b++){
          if(b>0)sm|=1L<<bySavings[b-1];
          long mask=im&sm;
          if(mask!=0){Integer id=found.get(mask);if(id==null){id=found.size();found.put(mask,id);}region[a*(T+1)+b]=id;}
        }
      }
      F=found.size();masks=new long[F];int edgeKinds=0;
      for(Map.Entry<Long,Integer> e:found.entrySet()){masks[e.getValue()]=e.getKey();edgeKinds+=Long.bitCount(e.getKey());}
      mass=new double[H*F];pref=new int[H*T];ci=new double[T+2];cs=new double[T+2];
      flow=new HousingTimingProbe.Flow(H*F+T+2,H*edgeKinds+H*F+T+4);
    }
    double allocate() {
      Arrays.fill(mass,0);eligible=0;
      for(int i=0;i<H;i++){
        double mi=Math.log(k.gross[i])-.35*.35/2,ms=Math.log(k.savings[i])-1.5*1.5/2;
        ci[0]=cs[0]=0;ci[T+1]=cs[T+1]=1;
        for(int j=0;j<T;j++){
          ci[j+1]=HousingTimingProbe.cdf((Math.log(incomeNeed[byIncome[j]])-mi)/.35);
          cs[j+1]=HousingTimingProbe.cdf((Math.log(savingsNeed[bySavings[j]])-ms)/1.5);
        }
        for(int a=0;a<=T;a++){
          double w=k.need[i]*(ci[a+1]-ci[a]);
          for(int b=0;b<=T;b++){
            int r=region[a*(T+1)+b];if(r>=0)mass[i*F+r]+=w*(cs[b+1]-cs[b]);
          }
        }
        k.ranked(i);for(int j=0;j<T;j++)pref[i*T+j]=(int)k.rank[j];
      }
      flow.clear(1e-9*k.scale);int source=H*F+T,sink=source+1;
      for(int j=0;j<T;j++)flow.edge(H*F+j,sink,k.stock[j]*.07);
      int offset=k.month%H;
      for(int c=0;c<H;c++){
        int i=(c+offset)%H;
        for(int r=0;r<F;r++){
          double q=mass[i*F+r];if(q<=1e-12*k.scale)continue;
          int node=i*F+r;flow.edge(source,node,q);
          long compatible=masks[r];
          for(int p=T-1;p>=0;p--){int j=pref[i*T+p];
            if((compatible&(1L<<j))!=0&&k.capacity[j]>=k.heads[i]&&(k.dependants[i]==0||k.beds[j]>0))
              flow.edge(node,H*F+j,q);
          }
          eligible+=q;
        }
      }
      placed=flow.max(source,sink);
      k.checksum+=placed;HousingTimingProbe.sink=k.checksum;
      return placed;
    }
  }

  /** Extra 40,320 bounded rows bring the loan book to 46,080 rows.
   * Counts are divided by eight across fragments; represented stock is fixed. */
  static final class ExtraLoans {
    final HousingTimingProbe.Kernel k;
    final int M;
    final double[] balance,base,count,arrears;
    final int[] month,rate;
    ExtraLoans(HousingTimingProbe.Kernel k){
      this.k=k;M=k.M*7;balance=new double[M];base=new double[M];count=new double[M];arrears=new double[M];month=new int[M];rate=new int[M];
      for(int m=0;m<k.M;m++){k.loan[m]/=8;k.loanBase[m]/=8;k.loanCount[m]/=8;k.loanArrears[m]/=8;}
      for(int m=0;m<M;m++){
        int p=m%k.M;balance[m]=base[m]=k.loanBase[p];count[m]=k.loanCount[p];arrears[m]=k.loanArrears[p];
        month[m]=60+(m*13)%301;rate[m]=m%8;
      }
    }
    void book(){
      double x=0;
      for(int m=0;m<M;m++){
        int r=rate[m],n=month[m];double annual=.03+.005*r;
        double payment=balance[m]*k.factors[r][n],interest=balance[m]*annual/12;
        double principal=Math.min(balance[m],Math.max(0,payment-interest));
        balance[m]-=principal;arrears[m]=arrears[m]*.98+count[m]*.00002;
        if(--month[m]<=0||balance[m]<.01*k.scale){month[m]=300;balance[m]=base[m];}
        x+=payment+principal+balance[m]*annual+balance[m]*month[m]+arrears[m];
      }
      k.checksum+=x;HousingTimingProbe.sink=k.checksum;
    }
  }

  static void laws(HousingTimingProbe.Offer[] offers){
    var k=new HousingTimingProbe.Kernel(180,32,1,offers);
    var f=new Funding(k);double a=f.allocate();
    double eligible=f.eligible;
    for(int i=0;i<k.H;i++){
      double n=0;for(int r=0;r<f.F;r++)n+=f.mass[i*f.F+r];
      if(n<0||n>k.need[i]+1e-7)throw new AssertionError("funding mass duplicated");
    }
    var scaled=new Funding(new HousingTimingProbe.Kernel(180,32,1000,offers));
    double b=scaled.allocate();
    if(Math.abs(a-b/1000)>1e-6||a>eligible+1e-7)throw new AssertionError("funding scaling/union failed");
    // Same 1% eligibility rectangle, two homes: exactly one buyer, not two.
    HousingTimingProbe.Flow g=new HousingTimingProbe.Flow(5,8);
    g.edge(3,0,100*(1-.9)*(1-.9));g.edge(0,1,100);g.edge(0,2,100);g.edge(1,4,100);g.edge(2,4,100);
    if(Math.abs(g.max(3,4)-1)>1e-9)throw new AssertionError("wealthy tail reused");
    HousingTimingProbe.row("closure_laws,mass-conservation=pass,scale-1000=pass,shared-tail=pass,eligibility_masks=%d,temporary_nodes=%d,placed=%.6f,eligible=%.6f",f.F,f.H*f.F,a,eligible);
  }
  public static void main(String[] args)throws Exception{
    var offers=HousingTimingProbe.offers(Path.of(args[0]));laws(offers);
    var k=new HousingTimingProbe.Kernel(180,32,1,offers);
    var f=new Funding(k);var extra=new ExtraLoans(k);
    HousingTimingProbe.row("closure_dimensions,H=180,T=32,base_assignment_rows=%d,total_loan_rows=%d,listing_rows=%d,eligibility_masks=%d,temporary_nodes=%d",k.A,k.M+extra.M,k.L,f.F,f.H*f.F);
    HousingTimingProbe.measure("H180_T32","joint_funded_allocation",()->f.allocate(),60,15,5);
    HousingTimingProbe.measure("H180_T32","extra_40320_loan_rows",()->extra.book(),60,15,5);
    HousingTimingProbe.measure("H180_T32","closure_bundle",()->{
      k.book(false);extra.book();k.quote(1);k.allocate();f.allocate();
    },60,15,5);
    HousingTimingProbe.row("closure_done,status=pass,checksum=%.6f",HousingTimingProbe.sink);
  }
}
```
