package ham.citybuildersim;

public class Ld2Trace {
    public static void main(String[] a) {
        int months = Integer.parseInt(a[0]);
        for (int i = 1; i < a.length; i++) {
            long seed = Long.parseLong(a[i]);
            Game g = new Game(GameFiles.scratch("ld2-trace-" + seed));
            g.newGame(Founding.defaults().withPath(Nations.Path.RANDOM, seed));
            g.setRescueMode(TreasuryFund.RescueMode.BUTTON);
            double nii = 0, fees = 0, prov = 0, trade = 0, opex = 0, kept = 0, tax = 0, divs = 0, back = 0, issued = 0, inj = 0, other = 0;
            double intIn = 0;
            Bank b = g.getBank();
            double eq0 = Double.NaN;
            int failed = -1;
            StringBuilder yearly = new StringBuilder();
            for (int m = 0; m < months; m++) {
                g.toggleNextMonth();
                b = g.getBank();
                if (Double.isNaN(eq0)) eq0 = b.getOpeningEquity();
                Bank.EquityMovement e = b.equityMovement();
                double n = b.netInterestIncome(), f = b.feeIncome(), al = b.afterLosses(), at = b.afterTrading(), op = b.operatingExpenses();
                intIn += b.interestIncome();
                nii += n; fees += f; prov += n + f - al; trade += at - al; opex += op;
                kept += e.kept(); tax += b.profitBeforeTax() - e.kept();
                divs += e.dividends(); back += e.boughtBack(); issued += e.issued(); inj += e.fromShareholders() + e.fromCity();
                other += e.founding() + e.absorbed() + e.treasuryBuyback() - e.allowanceOpened() + e.preferredIn() - e.preferredOut()
                        - e.preferredDividends() - e.warrantsBoughtBack() + e.discountRestruck();
                if (g.getMonth() % 12 == 0) yearly.append(String.format(" m%d:%.0f", g.getMonth(), b.equity()));
                if (b.isInResolution()) { failed = g.getMonth(); break; }
            }
            System.out.printf("seed %d failed %d eq0 %.0f eq %.0f | intIn %.0f NII %.0f fees %.0f prov %.0f trade %.0f opex %.0f tax %.0f kept %.0f | divs %.0f back %.0f issued %.0f inj %.0f other %.0f | dep %.0f loans %.0f%n",
                    seed, failed, eq0, b.equity(), intIn, nii, fees, prov, trade, opex, tax, kept, divs, back, issued, inj, other,
                    b.getDeposits(), b.netLoans());
            System.out.println("   " + yearly);
        }
    }
}
