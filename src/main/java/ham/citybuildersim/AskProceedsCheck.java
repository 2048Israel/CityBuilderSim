package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The bond proceeds an open ask has raised pay its works, as a fenced fund pays them: the treasury holds them outside the operating
 * reserve, the order is placed on them, and where the month's bills drew the cash under them the order is paid from them all the
 * same, the treasury overdrawn by what the bills drew as far as the central bank advances (AutoBuilder.spendableFor(), funded(),
 * covered(), stillToRaise(); Game.allowProceeds()).
 * WHY (OA1, 2026-11-25). An open ask held an essential works for years: PW3's ensemble had a Water Treatment Plant's ask raise its
 * whole cost, the cash 53.4M against the order's 49.8M and the reserve 6.9M, and the old rule took the reserve out of the proceeds
 * first, so the order waited 133 months with water served at 26% - and, its sales past its cost, nothing was offered again, so
 * neither the order, a sale nor the debt limit's test ever ran. OA1's first mend sold again what the bills drew, bounded only by
 * the debt limit (REV-OA1: a deficit wider than the market sold paper every month for an order never covered); 2b and 2c play
 * that case for months. The real law: bond proceeds are restricted to the works they were raised for, outside the operating
 * reserve (GASB Statement 54; GFOA's fund-balance practice); and a tax-exempt issue keeps its temporary period only if its issuer
 * expects to bind 5% of the proceeds within six months and spend 85% within three years (26 CFR 1.148-2(e)(2)) - the six months
 * this harness and AutoBuilder.PROCEEDS_WAIT_MONTHS read.
 * What this has to prove:
 * 1. The finding, caused: a town short of water, an ask whose sales raised the order's whole cost, the cash its proceeds and half
 *    the reserve; one pass orders the works from them, closes the ask, sells no bond.
 * 2. The bills drew the proceeds within what the central bank advances, a sale able to sell: one pass orders the works from them,
 *    sells no paper for it, and the treasury goes under by what the bills drew; the money conserved.
 * 2b. Drawn, and the market short, played for months: the ask closes within six months (in the first), raising nothing past its
 *    cost, every month's money audit closed.
 * 2c. Drawn past what the central bank advances, played for months with the bills drawing the cash back each month: the order is
 *    not placed, nothing is sold twice (a repricing at most), the builder says why, and the ask is withdrawn at the first pass it has been open
 *    PROCEEDS_WAIT_MONTHS.
 * 3. The debt limit closes an ask with money still to raise: the limit just under what a sale of the rest would take the debt to;
 *    the ask is withdrawn, nothing sold, what it raised stays in the treasury.
 * 4. Within months, in the played month: case 1's city played with the month, the works on order in the first month and audited.
 * 5. Without the lack nothing changes: no ask, spendable() is the cash over the reserve (an overdraft in full); with proceeds and
 *    reserve both whole, held() is the cash over the reserve beside the other asks' money; water served in full opens no ask.
 */
public final class AskProceedsCheck {
    static final PrintStream out = System.out;
    static int fails;

    static void check(String words, boolean condition) {
        out.println(words + (condition ? " OK" : " FAIL"));
        if (!condition) fails++;
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-6 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static final BuildAdvice.Measure WATER = BuildAdvice.Measure.of(BuildAdvice.Kind.WATER);
    static final BuildAdvice.Measure POWER = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
    static final BuildAdvice.Measure ROADS = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);

    /** The months the works may take to be ordered at the latest: the six months in which a tax-exempt issuer must expect to bind 5% of the proceeds (26 CFR 1.148-2(e)(2)). */
    static final int BINDING_COMMITMENT_MONTHS = 6;

    /** How many times what the plants supply the buildings are made to draw, so the town is short of water. */
    static final double WATER_DRAW_OVER_SUPPLY = 3;

    /** The room the debt limit is given over the town's ratio, in years of GDP: a sale of an order's gap fits within it, a second plant's whole bond does not. */
    static final double LIMIT_ROOM = 1;

    /** How many Paved Roads the town stands, so its streets are ahead of their load and the only works the builder wants first is water. */
    static final int PAVED_ROADS = 24;

    /** The face of paper the bank has room for where the market is made ample, and where it is made short. */
    static final double AMPLE_ROOM = 1e9, SHORT_ROOM = 100;

    static final String BY = "Automatic building";

    /** The share of the reserve the cash holds beside the proceeds in the finding: the proceeds whole, the reserve half there, so the cash is over the cost and the cash over the reserve is under it. */
    static final double RESERVE_BESIDE = .5;

    /** The share of the proceeds the treasury still holds after the month's bills drew the rest, where the bills have drawn them. */
    static final double PROCEEDS_LEFT = .5;

    /** The shares of their costs that section 5's two asks have raised, each short of its cost; and how many reserves of cash stand free over the reserve and the money the asks hold. */
    static final double WATER_RAISED = .6, POWER_RAISED = .3, FREE_RESERVES = 1;

    /** A month, played with the narration off, and its money audit (AuctionCheck.play() puts back AuctionCheck.out, which only its own main sets: null here). */
    static MoneyAudit.Result play(Game g) {
        EssentialWorksCheck.quietly(g::toggleNextMonth);
        return g.getLastMoneyAudit();
    }

    /** A town short of water with its works' card: `cost` is the order's, one building and its ground, as the pass prices it. */
    record Finding(Game g, AutoBuilder auto, BuildAdvice.Suggestion card, double cost) { }

    static BuildAdvice.Suggestion card(Game g) {
        List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
        return BuildAdvice.suggestFor(g, AutoBuilder.rowFor(all, WATER), WATER, g.getCash(),
                g.getLandManager().getAvailableSqFt(), g.getAutoBuilder().getSlack());
    }

    static double orderCost(Game g, BuildAdvice.Suggestion card) {
        return AutoBuilder.total(g, card, 1) + AutoBuilder.groundFor(g, card.template(), 1).cash();
    }

    static double[] water(Game g) {
        return BuildAdvice.supplyDemand(g, WATER, BuildAdvice.onSite(g, WATER));
    }

    /** WorksFinanceCheck.town(): its founding and terms, with PAVED_ROADS roads in its works. */
    static Game town(Path root, String name) {
        String[][] works = WorksFinanceCheck.TOWN_WORKS.clone();
        for (int i = 0; i < works.length; i++)
            if (works[i][0].equals("Paved Road")) works[i] = new String[] { "Paved Road", Integer.toString(PAVED_ROADS) };
        Game g = WorksFinanceCheck.founded(root, name, works);
        EssentialWorksCheck.quietly(() -> g.handleLongBondForCash(2 * AutoBuilder.annualGdp(g),
                Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
        g.getAutoBuilder().setDebtLimit(0, null);
        g.getAutoBuilder().setCashAnyway(true, null);
        return g;
    }

    /** The town, its buildings made to draw more water than the plants supply, the builder on and its limit open. */
    static Finding finding(Path root, String name) {
        Game g = town(root, name);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        u.setBuildingWaterDraw(WATER_DRAW_OVER_SUPPLY * water(g)[0]);
        u.updateWaterRatio();
        AutoBuilder auto = g.getAutoBuilder();
        auto.setOn(true, null);
        auto.setDebtLimit(AutoBuilder.ratio(g) + LIMIT_ROOM, null);
        BuildAdvice.Suggestion card = card(g);
        return new Finding(g, auto, card, card == null ? Double.NaN : orderCost(g, card));
    }

    static AutoBuilder.OpenAsk ask(Game g, BuildAdvice.Measure m, BuildAdvice.Suggestion card, double cost, double raised) {
        AutoBuilder.OpenAsk a = new AutoBuilder.OpenAsk();
        a.measure = m.label();
        a.building = card.template().getName();
        a.templateId = card.template().getId();
        a.paving = card.paving();
        a.count = 1;
        a.cost = cost;
        a.raised = raised;
        a.toRaise = Math.max(0, cost - raised);
        a.month = g.getMonth();
        return a;
    }

    /** The ask for the water works open with `raised` of its cost raised, and the treasury at `cash`. */
    static void open(Finding f, double raised, double cash) {
        AutoBuilder.State state = f.auto().toState();
        state.on = true;
        state.asks = new ArrayList<>(List.of(ask(f.g(), WATER, f.card(), f.cost(), raised)));
        f.auto().restore(state);
        f.g().setCashForTest(cash);
    }

    static AutoBuilder.Entry order(AutoBuilder auto, int from) {
        for (int i = from; i < auto.log().size(); i++) if (auto.log().get(i).measure().equals(WATER.label())) return auto.log().get(i);
        return null;
    }

    static double paid(AutoBuilder.Entry e) {
        return e == null ? Double.NaN : e.cost() + e.landCost();
    }

    static double soldFace(Game g) {
        double face = 0;
        for (TimeSkipReport.AutoSale s : g.getAutoSalesThisMonth()) if (s.by().equals(BY)) face += s.sold();
        return face;
    }

    /** What the pass ordered, a line each, and what it said of water: the figures a reader sees beside the checks. */
    static void steps(AutoBuilder auto) {
        for (AutoBuilder.Step st : auto.steps())
            if (st.ordered() > 0) out.printf("      pass: %s ordered %d %s, cost %.3f, borrowed %.3f%n", st.measure().label(), st.ordered(),
                    st.building(), st.cost(), st.borrowed());
        for (String l : auto.held()) if (l.startsWith(WATER.label() + ":")) out.println("      held: " + l);
    }

    static void figures(Game g, AutoBuilder.OpenAsk a, AutoBuilder auto) {
        out.printf("      cost %.3f, raised %.3f, cash %.3f, reserve %.3f, held %.3f%n", a.cost(), a.raised(), g.getCash(),
                AutoBuilder.reserve(g), auto.held(g, a));
    }

    /** The conditions every water section stands on, said first. */
    static void stands(Finding f, AutoBuilder.OpenAsk a) {
        double[] sd = water(f.g());
        check("fixture: the town is short of water (supply " + String.format("%.0f", sd[0]) + " under demand "
                + String.format("%.0f", sd[1]) + "), the advice gives a card for it, and its ask is open",
                sd[0] < sd[1] && f.card() != null && a != null && a.building().equals(f.card().template().getName())
                        && a.measure().equals(WATER.label()));
        check("fixture: the streets are ahead of their load, so the builder has no street order to place before the water works",
                BuildAdvice.maintained(f.g(), ROADS, java.util.Map.of(), BuildAdvice.opening(f.g(), 0, f.auto().getSlack())));
    }

    /** The debt limit open, said first where a section needs the sale: the town is within it with room to spare. */
    static void limitOpen(Finding f) {
        check("fixture: the debt limit is open - the town's debt is within it, with room (limit " + f.auto().getDebtLimit()
                + " of a year's GDP, ratio " + String.format("%.3f", AutoBuilder.ratio(f.g())) + ")",
                f.auto().within(f.g()) && f.auto().getDebtLimit() >= AutoBuilder.ratio(f.g()) + LIMIT_ROOM - AutoBuilder.DEBT_STEP);
    }

    static void theFinding(Path root) {
        out.println("--- 1. the finding, caused: the proceeds the treasury holds pay the works, the reserve beside them short ---");
        Finding f = finding(root, "ask-finding");
        Game g = f.g();
        AutoBuilder auto = f.auto();
        double reserve = AutoBuilder.reserve(g);
        open(f, f.cost(), f.cost() + reserve * RESERVE_BESIDE);
        AutoBuilder.OpenAsk ask = auto.askFor(WATER);
        stands(f, ask);
        limitOpen(f);
        figures(g, ask, auto);
        check("fixture: the ask has raised the order's whole cost, the treasury holds it - and the reserve is not beside it: the cash"
                        + " over the reserve is short of the cost, the old rule's hold",
                ask.raised() >= ask.cost() && g.getCash() >= ask.cost() && g.getCash() - AutoBuilder.reserve(g) < ask.cost()
                        && near(g.getCash(), ask.cost() + reserve * RESERVE_BESIDE));
        check("fixture: the card the advice gives now is still the ask's building, and the treasury holds what covers it (held at least the cost)",
                card(g) != null && card(g).template().getId() == ask.templateId && auto.held(g, ask) >= ask.cost() - 1e-6 * ask.cost());
        int bonds = auto.getBonds(), logged = auto.log().size();
        double principal = g.getDebtManager().getAllPrincipal(), borrowed = auto.getBorrowed(), cash = g.getCash();
        EssentialWorksCheck.quietly(() -> auto.pass(g));
        steps(auto);
        AutoBuilder.Entry e = order(auto, logged);
        AutoBuilder.Step step = EssentialWorksCheck.step(g, WATER);
        check("the water works is ordered on the proceeds", e != null && step != null && step.ordered() > 0);
        check("...and its ask is closed", auto.askFor(WATER) == null && auto.asks().isEmpty());
        // The pass may go on to order more of the works on paper of its own (the merged 0.9.206 town's ratio leaves a second plant's bond
        // within the limit): the ask's order is read from its own entry, and the money over the whole pass.
        double paidAll = 0;
        for (int i = logged; i < auto.log().size(); i++) paidAll += paid(auto.log().get(i));
        double newPaper = auto.getBorrowed() - borrowed;
        check("...and its order sold no paper of its own: its entry's borrowing is what the ask had raised, and no more",
                e != null && near(e.borrowed(), ask.raised()) && e.cost() + e.landCost() <= ask.raised() + 1e-6 * ask.raised());
        out.printf("      the ask's order cost %.3f; the pass's orders %.3f, its new paper %.3f (%d bond(s)); the cash fell %.3f%n", paid(e),
                paidAll, newPaper, auto.getBonds() - bonds, cash - g.getCash());
        check("...and no dollar was made or lost: the cash fell by exactly what the pass's orders cost, ground with them, less what"
                        + " its own new paper raised, and the principal rose by that paper's face alone - the proceeds paid the ask's works",
                e != null && near(cash - g.getCash(), paidAll - newPaper) && (auto.getBonds() > bonds || near(cash - g.getCash(), paid(e)))
                        && near(g.getDebtManager().getAllPrincipal() - principal, soldFace(g)));
    }

    /** What the central bank advances the treasury past its cash today: Game.discretionaryRoom() over the cash in hand. */
    static double advance(Game g) {
        return g.discretionaryRoom() - Math.max(0, g.getCash());
    }

    /**
     * Case 1's city with the bills drawn: the ask's whole cost raised, and the treasury holding less. `pastAdvance` false, the
     * bills drew PROCEEDS_LEFT of what the central bank advances (the treasury can pay the order, overdrawn); true, they drew past
     * it (the cash PROCEEDS_LEFT of the cost less the advance: the treasury cannot).
     */
    static Finding drawn(Path root, String name, Auction.Cap cap, double room, boolean pastAdvance) {
        Finding f = finding(root, name);
        f.auto().setCap(cap, null);
        AuctionCheck.roomTo(f.g(), room);
        open(f, f.cost(), f.cost());
        double advance = advance(f.g());
        f.g().setCashForTest(pastAdvance ? (f.cost() - advance) * PROCEEDS_LEFT : f.cost() - advance * PROCEEDS_LEFT);
        return f;
    }

    /** The money over one pass from its start: the orders it placed (ground with them), the paper it sold, what the ask's own order took. */
    record Pass(AutoBuilder.Entry e, double paidAll, double newPaper, double otherPaper) { }

    static Pass pass(Game g, AutoBuilder auto, int logged, double borrowed) {
        AutoBuilder.Entry e = order(auto, logged);
        double paidAll = 0, otherPaper = 0;
        for (int i = logged; i < auto.log().size(); i++) {
            AutoBuilder.Entry x = auto.log().get(i);
            paidAll += paid(x);
            if (x != e) otherPaper += x.borrowed();
        }
        return new Pass(e, paidAll, auto.getBorrowed() - borrowed, otherPaper);
    }

    static void billsDrew(Path root) {
        out.println("--- 2. the bills drew the proceeds: the works is paid from them, the treasury overdrawn by what the bills drew, no paper sold for it ---");
        Finding f = drawn(root, "ask-drawn", Auction.Cap.FILL, AMPLE_ROOM, false);
        Game g = f.g();
        AutoBuilder auto = f.auto();
        AutoBuilder.OpenAsk ask = auto.askFor(WATER);
        stands(f, ask);
        limitOpen(f);
        figures(g, ask, auto);
        double drew = ask.cost() - g.getCash();
        DebtQuote quote = AutoBuilder.bondQuote(g, drew, auto.getCap());
        out.printf("      the bills drew %.3f; the central bank advances %.3f past the cash%n", drew, advance(g));
        check("fixture: the ask raised the whole cost, the bills drew part of it (the cash under the cost) within what the central bank"
                        + " advances, and the cap is Fill with room - a bond for what they drew would sell in full, within the limit",
                ask.raised() >= ask.cost() && g.getCash() < ask.cost() && g.getCash() > 0 && ask.cost() <= g.discretionaryRoom()
                        && auto.getCap().fill() && quote != null && !quote.isShort() && quote.cashReceived() >= drew
                        && AutoBuilder.ratioAfter(g, quote) <= auto.getDebtLimit());
        int logged = auto.log().size();
        double principal = g.getDebtManager().getAllPrincipal(), borrowed = auto.getBorrowed(), cash = g.getCash();
        EssentialWorksCheck.quietly(() -> auto.pass(g));
        steps(auto);
        Pass p = pass(g, auto, logged, borrowed);
        AutoBuilder.Entry e = p.e();
        out.printf("      the ask's order cost %.3f; the pass's orders %.3f, its new paper %.3f (its other orders' %.3f); cash %.3f -> %.3f%n",
                paid(e), p.paidAll(), p.newPaper(), p.otherPaper(), cash, g.getCash());
        check("the water works is ordered that pass, and its ask is closed", e != null && EssentialWorksCheck.step(g, WATER).ordered() > 0
                && auto.askFor(WATER) == null && auto.asks().isEmpty());
        check("...from its proceeds, no paper sold for it: its entry's borrowing is what the ask had raised, and every dollar of the"
                        + " pass's new paper is its other orders'",
                e != null && near(e.borrowed(), ask.raised()) && near(p.newPaper(), p.otherPaper()));
        check("...paid past the cash: the order cost more than the treasury held, and the log says the treasury is overdrawn for the"
                        + " deficit a fenced fund would have left",
                e != null && paid(e) > cash && e.why().contains("the deficit a fenced fund would have left"));
        check("conservation: the cash fell by what the pass's orders cost less its new paper, the principal rose by that paper's face",
                e != null && near(cash - g.getCash(), p.paidAll() - p.newPaper())
                        && near(g.getDebtManager().getAllPrincipal() - principal, soldFace(g)));
    }

    static void marketShort(Path root) {
        out.println("--- 2b. drawn, and the market short, played for months: the order is placed from the proceeds, and the ask raises nothing past its cost ---");
        Finding f = drawn(root, "ask-short", Auction.Cap.tail(0), SHORT_ROOM, false);
        Game g = f.g();
        AutoBuilder auto = f.auto();
        AutoBuilder.OpenAsk ask = auto.askFor(WATER);
        stands(f, ask);
        limitOpen(f);
        AuctionCheck.useUpTheRoom(g);
        double drew = ask.cost() - g.getCash();
        DebtQuote quote = AutoBuilder.bondQuote(g, drew, auto.getCap());
        figures(g, ask, auto);
        out.printf("      a bond for what the bills drew, %.3f, sells %.3f at the cap%n", drew, quote == null ? 0 : quote.cashReceived());
        check("fixture: the bills drew part of the proceeds within what the central bank advances, the bank's room is used up and the"
                        + " cap is fair, so a bond for what they drew sells short (the review's case: the draw past what sells)",
                g.getCash() < ask.cost() && ask.cost() <= g.discretionaryRoom() && quote != null && quote.isShort() && quote.cashReceived() < drew);
        double[] atPass = { Double.NaN, Double.NaN };
        g.autoBuildProbeForTest = after -> {
            if (after || !Double.isNaN(atPass[0])) return;
            atPass[0] = g.getCash();
            atPass[1] = g.discretionaryRoom();
        };
        int start = g.getMonth(), logged = auto.log().size(), ordered = -1;
        double cost = ask.cost(), raised = ask.raised();
        boolean audits = true;
        for (int played = 1; played <= BINDING_COMMITMENT_MONTHS && ordered < 0; played++) {
            MoneyAudit.Result audit = play(g);
            audits &= Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved());
            if (order(auto, logged) != null) ordered = played;
        }
        g.autoBuildProbeForTest = null;
        AutoBuilder.Entry e = order(auto, logged);
        out.printf("      at the first pass: cash %.3f, the treasury's cash and advance %.3f, cost %.3f; ordered in played month %d, its entry's"
                + " borrowing %.3f%n", atPass[0], atPass[1], cost, ordered, e == null ? Double.NaN : e.borrowed());
        check("fixture: the first pass found the treasury drawn under the cost and able to pay it with the central bank's advance",
                atPass[0] < cost && cost <= atPass[1]);
        AutoBuilder.OpenAsk next = auto.askFor(WATER);
        if (next != null) out.printf("      the pass went on to open an ask for the next plant, raised %.3f of %.3f%n", next.raised(), next.cost());
        check("the ask closes within " + BINDING_COMMITMENT_MONTHS + " months - in the first: its works on order, the ask no longer open",
                ordered == 1 && auto.asks().stream().noneMatch(x -> x == ask) && g.getMonth() - start <= BINDING_COMMITMENT_MONTHS);
        check("...and it raised nothing past its cost: its entry's borrowing is what it had raised when the months began",
                e != null && near(e.borrowed(), raised) && raised <= cost + 1e-6 * cost);
        check("...and every played month's money audit closed within its tolerance", audits);
    }

    static void pastAdvance(Path root) {
        out.println("--- 2c. drawn past what the central bank advances: the ask waits, selling nothing twice, and is withdrawn once open "
                + AutoBuilder.PROCEEDS_WAIT_MONTHS + " months ---");
        Finding f = drawn(root, "ask-past", Auction.Cap.FILL, AMPLE_ROOM, true);
        Game g = f.g();
        AutoBuilder auto = f.auto();
        AutoBuilder.OpenAsk ask = auto.askFor(WATER);
        stands(f, ask);
        limitOpen(f);
        figures(g, ask, auto);
        double cash = g.getCash(), draw = g.getServicesManager().getUtilitiesHandler().getBuildingWaterDraw();
        out.printf("      the treasury's cash and advance %.3f against the cost %.3f%n", g.discretionaryRoom(), ask.cost());
        check("fixture: the ask raised the whole cost, the bills drew past what the central bank advances (the cash and the advance"
                        + " short of the cost), and the cap is Fill with room - a sale would sell, so not selling is the rule's",
                ask.raised() >= ask.cost() && g.getCash() >= 0 && g.discretionaryRoom() < ask.cost() && auto.getCap().fill());
        // Each month the bills draw the cash back down and the town stays short of water: both set again before the month, its pass the first thing in it.
        int opened = ask.monthOpened(), logged = auto.log().size(), withdrawnAt = -1;
        int[] passMonth = { -1 };
        g.autoBuildProbeForTest = after -> { if (!after) passMonth[0] = g.getMonth(); };
        boolean audits = true, bounded = true, waits = true;
        double sold = 0;
        for (int played = 1; played <= AutoBuilder.PROCEEDS_WAIT_MONTHS + 2 && withdrawnAt < 0; played++) {
            AutoBuilder.OpenAsk before = auto.askFor(WATER);
            double raisedBefore = before == null ? 0 : before.raised();
            g.setCashForTest(cash);
            g.getServicesManager().getUtilitiesHandler().setBuildingWaterDraw(draw);
            g.getServicesManager().getUtilitiesHandler().updateWaterRatio();
            MoneyAudit.Result audit = play(g);
            audits &= Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved());
            sold += soldFace(g);
            AutoBuilder.OpenAsk open = auto.askFor(WATER);
            if (open == null) withdrawnAt = passMonth[0];
            else {
                out.printf("      the pass of month %d: raised %.3f of the cost as repriced %.3f, held %.3f%n", passMonth[0], open.raised(), open.cost(), auto.held(g, open));
                bounded &= open.raised() - raisedBefore <= Math.max(0, open.cost() - raisedBefore) + g.fundingLot("Term") + 1e-9;
                waits &= auto.held().stream().anyMatch(l -> l.contains("have drawn the rest") && l.contains("nothing more is borrowed for it"));
            }
        }
        g.autoBuildProbeForTest = null;
        for (int i = logged; i < auto.log().size(); i++) { AutoBuilder.Entry x = auto.log().get(i); out.printf("      logged: month %d %s %d %s, cost %.3f, borrowed %.3f%n", x.month(), x.measure(), x.count(), x.building(), x.cost(), x.borrowed()); }
        out.printf("      opened in month %d, withdrawn at the pass of month %d; the builder sold %.3f of face in the months, a funding lot %.3f%n", opened,
                withdrawnAt, sold, g.fundingLot("Term"));
        check("while it waits its order is not placed - the treasury cannot pay it - and nothing is sold twice: each month's sale for"
                        + " it is at most what its cost, as repriced, asked past what it had raised, and a funding lot",
                order(auto, logged) == null && bounded);
        check("...and the builder says the bills drew the rest, and that nothing more is borrowed for it", waits);
        check("it is withdrawn at the first pass at which it has been open " + AutoBuilder.PROCEEDS_WAIT_MONTHS + " months, and the"
                        + " inbox's line says the treasury cannot pay the order and that what it raised stays",
                withdrawnAt - opened == AutoBuilder.PROCEEDS_WAIT_MONTHS
                        && auto.asked().stream().anyMatch(l -> l.contains("cannot pay the order") && l.contains("stays in the treasury")));
        check("...and every played month's money audit closed within its tolerance", audits);
    }

    /** The highest debt limit, on its slider's steps, that a sale of `quote` takes the debt past: it is at or over the ratio now, or the fixture says no step lies between. */
    static double limitUnder(Game g, DebtQuote quote) {
        double step = AutoBuilder.DEBT_STEP;
        return Math.floor((AutoBuilder.ratioAfter(g, quote) - 1e-9) / step) * step;
    }

    static void limitReleases(Path root) {
        out.println("--- 3. the debt limit closes an ask with money still to raise: the sale would cross it, so the ask is withdrawn, not held ---");
        Finding f = finding(root, "ask-limit");
        Game g = f.g();
        AutoBuilder auto = f.auto();
        auto.setCap(Auction.Cap.FILL, null);
        AuctionCheck.roomTo(g, AMPLE_ROOM);
        open(f, f.cost() * PROCEEDS_LEFT, f.cost() * PROCEEDS_LEFT);
        AutoBuilder.OpenAsk ask = auto.askFor(WATER);
        double gap = ask.cost() - ask.raised();
        DebtQuote quote = AutoBuilder.bondQuote(g, gap, auto.getCap());
        auto.setDebtLimit(limitUnder(g, quote), null);
        quote = AutoBuilder.bondQuote(g, gap, auto.getCap());
        double after = AutoBuilder.ratioAfter(g, quote);
        out.printf("      ratio %.4f, a sale of what is still to raise %.3f takes it to %.4f; limit %.2f%n", AutoBuilder.ratio(g), gap, after,
                auto.getDebtLimit());
        stands(f, ask);
        figures(g, ask, auto);
        check("fixture: the ask has raised part of its cost and the treasury holds it, and a sale of the rest would take the debt past"
                        + " the limit (ratio " + String.format("%.4f", AutoBuilder.ratio(g)) + " <= limit " + auto.getDebtLimit() + " < "
                        + String.format("%.4f", after) + ")",
                quote != null && ask.raised() < ask.cost() && near(auto.held(g, ask), ask.raised()) && auto.within(g)
                        && AutoBuilder.ratio(g) <= auto.getDebtLimit() && after > auto.getDebtLimit() && auto.getDebtLimit() > 0);
        int bonds = auto.getBonds(), logged = auto.log().size();
        double principal = g.getDebtManager().getAllPrincipal(), cash = g.getCash(), raised = ask.raised();
        EssentialWorksCheck.quietly(() -> auto.pass(g));
        steps(auto);
        check("the ask is withdrawn, and the inbox's line says the debt limit closed on it",
                auto.askFor(WATER) == null && auto.asked().stream().anyMatch(l -> l.contains("the debt limit closed on it") && l.contains("withdrew")));
        check("...nothing was sold: the builder's bonds and the city's principal stand, and no sale is in the month's report",
                auto.getBonds() == bonds && g.getDebtManager().getAllPrincipal() == principal && AuctionCheck.salesBy(g, BY) == 0);
        check("...no order was placed, the cash is as it was, and what the ask raised ("
                        + String.format("%.3f", raised) + ") stays in the treasury and is said to",
                order(auto, logged) == null && near(g.getCash(), cash) && g.getCash() > 0
                        && auto.asked().stream().anyMatch(l -> l.contains("stays in the treasury")));
    }

    static void withinMonths(Path root) {
        out.println("--- 4. within months, in the played month: case 1's city, played, has the works on order and every month's money audit closes ---");
        Finding f = finding(root, "ask-played");
        Game g = f.g();
        AutoBuilder auto = f.auto();
        double reserve = AutoBuilder.reserve(g);
        open(f, f.cost(), f.cost() + reserve * RESERVE_BESIDE);
        AutoBuilder.OpenAsk ask = auto.askFor(WATER);
        // The month's own pass runs before anything moves the cash (Game.nextMonth()'s first step), and the probe reads what it found.
        double[] atPass = { Double.NaN, Double.NaN, Double.NaN };
        g.autoBuildProbeForTest = after -> {
            if (after || !Double.isNaN(atPass[0])) return;
            atPass[0] = g.getCash();
            atPass[1] = AutoBuilder.reserve(g);
            atPass[2] = ask.raised();
        };
        int start = g.getMonth(), logged = auto.log().size(), ordered = -1;
        boolean audits = true;
        for (int played = 1; played <= BINDING_COMMITMENT_MONTHS && ordered < 0; played++) {
            MoneyAudit.Result audit = play(g);
            audits &= Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved());
            if (order(auto, logged) != null) ordered = played;
        }
        g.autoBuildProbeForTest = null;
        out.printf("      at the first pass: cash %.3f, reserve %.3f, raised %.3f of cost %.3f; ordered in played month %d%n", atPass[0],
                atPass[1], atPass[2], f.cost(), ordered);
        check("fixture: the treasury as the first pass found it is case 1's - the cash set before the month, the proceeds whole and the"
                        + " cash over the reserve short of the cost",
                near(atPass[0], f.cost() + reserve * RESERVE_BESIDE) && atPass[0] >= f.cost() && atPass[0] - atPass[1] < f.cost() && atPass[2] >= f.cost());
        check("the water works is on order by the end of the first month", ordered == 1);
        check("...and at the latest within " + BINDING_COMMITMENT_MONTHS + " months (the six-month binding-commitment test)",
                ordered > 0 && ordered <= BINDING_COMMITMENT_MONTHS && g.getMonth() - start <= BINDING_COMMITMENT_MONTHS);
        check("...and every played month's money audit closed within its tolerance", audits);
    }

    static void withoutTheLack(Path root) {
        out.println("--- 5. without the lack, nothing changes ---");
        Game g = town(root, "ask-none");
        AutoBuilder auto = g.getAutoBuilder();
        double reserve = AutoBuilder.reserve(g);
        g.setCashForTest((FREE_RESERVES + 1) * reserve);
        boolean over = AutoBuilder.spendable(g) == Math.max(0, g.getCash() - reserve);
        g.setCashForTest(reserve * RESERVE_BESIDE);
        boolean under = AutoBuilder.spendable(g) == 0;
        g.setCashForTest(-reserve);
        check("(a) with no ask open, spendable() is the cash over the reserve, nothing under it, and an overdraft in full",
                auto.asks().isEmpty() && reserve > 0 && over && under && AutoBuilder.spendable(g) == g.getCash());

        Finding f = finding(root, "ask-two");
        Game h = f.g();
        UtilitiesHandler u = h.getServicesManager().getUtilitiesHandler();
        u.setWattsConsumption(u.getProduction() * 4);
        BuildAdvice.Suggestion plant = BuildAdvice.suggestFor(h, null, POWER, h.getCash(), h.getLandManager().getAvailableSqFt());
        if (plant == null || f.card() == null) {
            check("fixture: the advice gives a water card and a power card for two asks", false);
            return;
        }
        double powerCost = orderCost(h, plant);
        AutoBuilder.State state = f.auto().toState();
        state.on = true;
        state.asks = new ArrayList<>(List.of(ask(h, WATER, f.card(), f.cost(), WATER_RAISED * f.cost()), ask(h, POWER, plant, powerCost, POWER_RAISED * powerCost)));
        f.auto().restore(state);
        AutoBuilder.OpenAsk a = f.auto().askFor(WATER), b = f.auto().askFor(POWER);
        double reserveH = AutoBuilder.reserve(h);
        h.setCashForTest(a.raised() + b.raised() + (FREE_RESERVES + 1) * reserveH);
        out.printf("      water ask raised %.3f of %.3f, power ask %.3f of %.3f, cash %.3f, reserve %.3f%n", a.raised(), a.cost(), b.raised(),
                b.cost(), h.getCash(), reserveH);
        check("fixture: two asks open, each short of its cost, the cash whole over both and the reserve",
                a != null && b != null && a.raised() < a.cost() && b.raised() < b.cost() && h.getCash() >= a.raised() + b.raised() + reserveH);
        check("(b) each ask holds the cash over the reserve less the other's raised money, as before",
                near(f.auto().held(h, a), h.getCash() - reserveH - b.raised()) && near(f.auto().held(h, b), h.getCash() - reserveH - a.raised()));
        check("...and the builder's own orders have the cash over the reserve less both asks' raised money",
                near(AutoBuilder.spendable(h), h.getCash() - reserveH - a.raised() - b.raised()));

        Game s = town(root, "ask-served");
        AutoBuilder served = s.getAutoBuilder();
        served.setOn(true, null);
        served.setDebtLimit(AutoBuilder.ratio(s) + LIMIT_ROOM, null);
        served.setCap(Auction.Cap.tail(0), null);
        AuctionCheck.roomTo(s, SHORT_ROOM);
        s.setCashForTest(AutoBuilder.reserve(s));
        double[] sd = water(s);
        double principal = s.getDebtManager().getAllPrincipal();
        EssentialWorksCheck.quietly(() -> served.pass(s));
        steps(served);
        AutoBuilder.Step lacking = EssentialWorksCheck.step(s, WATER);
        check("(c) water served in full (supply " + String.format("%.0f", sd[0]) + " at demand " + String.format("%.0f", sd[1])
                        + ") and the cash at the reserve: a pass opens no ask for it, orders and holds nothing for it",
                sd[0] >= sd[1] && served.askFor(WATER) == null && lacking != null && lacking.outcome() == AutoBuilder.Outcome.AHEAD
                        && served.log().stream().noneMatch(e -> e.measure().equals(WATER.label())));
        long paidFor = served.steps().stream().filter(x -> x.ordered() > 0 && x.borrowed() > 0).count();
        check("...and no ask is open or raised anything: the paper sold (" + AuctionCheck.salesBy(s, BY) + ") is for the " + paidFor
                        + " order placed on it, none for an ask, and the principal rose by its face",
                served.asks().isEmpty() && served.askedRaised() == 0 && AuctionCheck.salesBy(s, BY) == paidFor
                        && near(s.getDebtManager().getAllPrincipal() - principal, soldFace(s)));
    }

    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(java.util.Locale.CANADA);
        Path root = TempDirs.create("askproceedscheck");
        theFinding(root);
        billsDrew(root);
        marketShort(root);
        pastAdvance(root);
        limitReleases(root);
        withinMonths(root);
        withoutTheLack(root);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
