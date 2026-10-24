package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

/**
 * The city's borrowing: every bond, note and dollar bond the treasury owes,
 * the market that prices the next one, and the policy rate every price of
 * money in the city is built on - the player's dial, or the rule's with the
 * autopilot on (0.7.0).
 *
 * @author Jerus
 */
public class DebtManager {

    /** Where the player's change to the policy rate, the rule, the inflation target or how strictly the rule holds it is written (DecisionLog, 0.7.23); null for one no city holds. Never saved: the city wires it. */
    private transient DecisionLog decisions;

    /** Wires this to its city's decision log (Game.buildWorld()). */
    public void recordTo(DecisionLog log) { decisions = log; }

    private void decided(String kind, String label) {
        if (decisions != null) decisions.record(kind, label);
    }

    /* =======================================================================
       THE POLICY RATE
       =======================================================================

       This was a constant, and it was the most load-bearing constant in the
       game without anybody having decided anything about it. Every price of
       money in the city is built on it: floorRate() IS it (never less than the
       bank's own cost of funds, on the share of the city's paper the central
       bank does not hold - 0.7.15), ceilingRate() is it plus both spreads, and
       the city's own borrowing rate sits between them. Since 0.7.0 it is the
       rate the CENTRAL BANK pays on the commercial bank's reserves, so
       Bank.fundToCover() is handed it directly: the bank's spare cash earns
       it, the window charges it plus CentralBank.WINDOW_PENALTY, and the
       treasury's advances are charged it. Since phase 4 the carry trade reads
       the gap between the bank's lending rate and the world's.

       So the central bank did not need new plumbing here. It needed to own
       this number, and since 0.7.0 it does (CentralBank; autopilot below).

       WHAT MOVING IT DOES, in the order the ECB lists the channels:
         - the bank's deposit and lending rates, immediately
         - what the city itself pays to borrow
         - how much the bank will lend at all, through the capital and funding
           limits, which is the channel that actually bites
         - the carry spread, so foreign money arrives or leaves
         - and, since this phase, the exchange rate directly

       Bounded at both ends because a policy rate is a decision, not a wish: no
       central bank sets a negative nominal rate by typing one. The top was 25%
       until 0.7.2, "past about a fifth the instrument stops transmitting and
       starts destroying" - which in this model was true: the currency's
       support from a high rate saturated 13 points over the world and the
       carry appetite at 6, so a higher dial bought nothing but dearer credit.
       0.7.2 gave the rate its channel (ForeignAccounts, THE REAL RATE, NOT THE
       NOMINAL; CapitalFlows.MAX_SPREAD) and the dial lost its stop with it.
       Jerus: "i think we need to uncap the rate... but if we do... what
       happens to everyone?" - the answer is the-central-bank.md's batch D
       measurement.
    */
    /** The floor of the dial: no central bank sets a negative nominal rate by typing one. */
    public static final double MIN_POLICY_RATE = .0;
    /** The top of the dial, 100% a year since 0.7.2 (25% before): a bound that never binds in play, kept so a typo cannot set 2,500%. */
    public static final double MAX_POLICY_RATE = 1.00;

    /** What the city's central bank charges. The player's dial - or the rule's, with autopilot on. */
    private double baseRate = .03;

    public double getPolicyRate() { return baseRate; }

    public void setPolicyRate(double rate) {
        baseRate = Math.max(MIN_POLICY_RATE, Math.min(MAX_POLICY_RATE, rate));
    }

    /* -----------------------------------------------------------------------
       THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1).

       Until 0.7.0 the city's own paper was quoted CITY_DISCOUNT - two points -
       UNDER the policy rate, "the one number in this file that does not
       describe anything real": the Policy screen read 3.00% and the Finances
       screen 1.00% on the same morning, and the city borrowed cheaper than
       its own central bank, which no borrower anywhere does. Jerus found it
       on the screens, 2026-09-12, and decided: "leave as is for now, much
       later when we will redesign it realistically aka central bank stepping
       in to keep it at that rate and longer durations deviating."

       The central bank steps in now. The commercial bank's reserves earn the
       policy rate, so nobody lends the city less than that, and a note the
       city sells settles at the policy rate plus the credit spread its own
       debt measures produce - the T-bill rate, by arbitrage. The discount is
       deleted and the floor is the dial. The rest of Jerus's sentence,
       "longer durations deviating", is THE CURVE below (0.7.1): a term premium
       by maturity on top of this floor, and the central bank's holdings
       bending the long end down.
       ----------------------------------------------------------------------- */

    /**
     * THE AUTOPILOT (0.7.0): whether the rule holds the dial rather than the
     * player. Jerus's toggle, decided with the floor: when on, the top of
     * every month sets the dial to advisedPolicyRate() off the price index
     * before anything is priced (Game.nextMonth()), and the player's hand on
     * the dial - takeTheDial() - turns it off. Saved beside the rate
     * (DataSave.policyAutopilot); an old save reads off, which is the hand
     * that was on the dial when it was saved.
     */
    private boolean autopilot;

    public boolean isAutopilot()              { return autopilot; }
    public void setAutopilot(boolean on) {
        if (on != autopilot) {
            decided(DecisionLog.CENTRAL_BANK, on ? "Central bank rate handed to the rule"
                                                 : "Central bank rate taken back by hand");
        }
        this.autopilot = on;
    }

    /* -----------------------------------------------------------------------
       WHAT THE AUTOPILOT AIMS AT, AND THE DIAL UNDER A PEG (XR2, 2026-10-22)

       Jerus's own question, 2026-10-21: "the policy rate you could choose to
       either follow the taylor rule, or try to control the currency or a mix of
       both? or real life doesnt work like that?" It does, all three: the Fed
       and the ECB aim at inflation and let the currency float; many small open
       economies put the exchange rate into the rule, an open-economy Taylor
       rule; Singapore sets its currency's band and lets its rate follow (the
       study's R36, R39). So the autopilot has an AIM: Inflation (the rule above,
       the default), the Currency (the dial that holds the band or the peg), or a
       Mix of the two. And under a PEG the dial is TIED to the US's rate, as
       Hong Kong's follows the US's by the board's own working (star 5): only a
       SPREAD over it is the city's to set, up against money leaving, down
       against money arriving - or the currency aim sets it.

       WORLD_BASE_RATE STANDS FOR THE US'S RATE until XR4 gives the US one.
       The gap each of these reads is ForeignAccounts.currencyGap(): the D$'s
       shadow past its target (the peg, the band's centre), a share, positive
       weaker; Lean and Float have no target, so the aim has nothing to read
       there (a call for Jerus whether a float should aim at the real rate).
       ----------------------------------------------------------------------- */

    /** What the autopilot sets the dial to hold (XR2; the study's star 13). */
    public enum Aim {
        INFLATION("Inflation"), CURRENCY("The currency"), MIX("A mix");
        public final String words;
        Aim(String words) { this.words = words; }
    }

    /** A quarter point of rate for each 1% the D$ sits past its target: the open-economy Taylor rule's weight on the exchange rate (the study's star 13, R39). */
    public static final double CURRENCY_WEIGHT = .25;

    private Aim aim = Aim.INFLATION;

    /** What the autopilot aims at: Inflation unless the player says otherwise. */
    public Aim getAim() { return aim; }

    public void setAim(Aim a) {
        Aim next = a == null ? Aim.INFLATION : a;
        if (next != aim) decided(DecisionLog.CENTRAL_BANK, "Central bank aims at " + next.words);
        this.aim = next;
    }

    /** Under a peg, what the dial stands over the US's rate (a fraction a year, negative under it): the player's to set, to defend the peg. Kept when the peg is left. */
    private double pegSpread;

    public double getPegSpread() { return pegSpread; }

    /** Sets the spread, inside what keeps the tied rate on the dial (MIN_POLICY_RATE to MAX_POLICY_RATE over WORLD_BASE_RATE). */
    public void setPegSpread(double spread) {
        double next = Double.isFinite(spread)
                ? Math.max(MIN_POLICY_RATE - WORLD_BASE_RATE, Math.min(MAX_POLICY_RATE - WORLD_BASE_RATE, spread))
                : 0;
        if (DecisionLog.moved(pegSpread, next)) {
            decided(DecisionLog.CENTRAL_BANK, "Spread over the US rate to " + DecisionLog.pct2(next));
        }
        this.pegSpread = next;
    }

    /** The dial that holds the currency alone: the US's rate plus CURRENCY_WEIGHT for each unit of gap (the D$ past its target, a share, positive weaker), inside the dial's bounds. Pure. */
    public double currencyRate(double gap) {
        return Math.max(MIN_POLICY_RATE, Math.min(MAX_POLICY_RATE, WORLD_BASE_RATE + CURRENCY_WEIGHT * gap));
    }

    /** The inflation rule's dial plus CURRENCY_WEIGHT for each unit of gap: the mix of the two aims, inside the dial's bounds. Pure. */
    public double mixRate(double inflation, double gap) {
        return Math.max(MIN_POLICY_RATE, Math.min(MAX_POLICY_RATE,
                advisedPolicyRate(inflation) + CURRENCY_WEIGHT * gap));
    }

    /** The dial under a peg (star 5): the US's rate plus the spread - the player's, or with the autopilot aiming at the currency (or a mix) the one the gap asks - inside the dial's bounds. Pure. */
    public double tiedRate(double gap) {
        double spread = autopilot && aim != Aim.INFLATION ? CURRENCY_WEIGHT * gap : pegSpread;
        return Math.max(MIN_POLICY_RATE, Math.min(MAX_POLICY_RATE, WORLD_BASE_RATE + spread));
    }

    /**
     * The player moves the dial by hand, which takes it back from the rule.
     * What the screens call; setPolicyRate() is what the rule and a fixture
     * call, and leaves the toggle alone.
     */
    public void takeTheDial(double rate) {
        boolean wasRule = autopilot;
        double was = baseRate;
        autopilot = false;
        setPolicyRate(rate);
        if (wasRule || DecisionLog.moved(was, baseRate)) {
            decided(DecisionLog.CENTRAL_BANK, "Central bank rate to " + DecisionLog.pct2(baseRate));
        }
    }

    /** Where the rate sits when nobody is leaning on it either way, at the default 2% target: a one-point real rate (ruleRate() adds the target's distance from the default since 0.7.42 - its aim's, at a strict setting, since 0.7.52). */
    public static final double NEUTRAL_RATE = .03;

    /** Where the inflation target opens, and what a save from before the dial reads: two per cent, the constant it was until 0.7.4. */
    public static final double DEFAULT_INFLATION_TARGET = .02;

    /** The top of the target's dial: 20% a year, Jerus's number (0.7.15: "inflation target can be higher tgan 10%, up yo 20%"); it was 10% from 0.7.4. */
    public static final double MAX_INFLATION_TARGET = .20;

    /** The bottom of the target's dial: stable prices to the letter - no central bank aims at falling ones. */
    public static final double MIN_INFLATION_TARGET = 0;

    /*
     * THE TARGET IS A DIAL (0.7.4). What the city is trying to hold inflation
     * at, which the rule below aims at (or under, at a strict setting since
     * 0.7.52 - HOW STRICT) and the strip's colour reads. It was
     * the constant INFLATION_TARGET, 2%. Jerus: "i want to have the dial not
     * target 0 inflation, set it so that you can choose what is your
     * inflation target." The player's, in half points from 0 to
     * MAX_INFLATION_TARGET on the monetary page - 10% until 0.7.15, 20% since,
     * Jerus's - applied at once like the autopilot beside it; saved
     * under its own key (DataSave.inflationTarget), and an older save reads
     * DEFAULT_INFLATION_TARGET - the constant it was.
     */
    private double inflationTarget = DEFAULT_INFLATION_TARGET;

    /** What the city holds inflation to, a fraction a year: the rule aims at it, or under it at a strict setting (Strictness.aim(), 0.7.52). */
    public double getInflationTarget() { return inflationTarget; }

    /** Sets the target, held between MIN_INFLATION_TARGET and MAX_INFLATION_TARGET; not a number leaves it where it was. */
    public void setInflationTarget(double target) {
        if (Double.isNaN(target)) return;
        double was = inflationTarget;
        inflationTarget = Math.max(MIN_INFLATION_TARGET, Math.min(MAX_INFLATION_TARGET, target));
        if (DecisionLog.moved(was, inflationTarget)) {
            decided(DecisionLog.CENTRAL_BANK, "Inflation target to " + DecisionLog.pct(inflationTarget));
        }
    }

    /** A target as the screens write it: "2%", or "2.5%" on a half point. */
    public static String targetWords(double target) {
        double pct = target * 100;
        return Math.abs(pct - Math.rint(pct)) < 1e-9
                ? String.format("%.0f%%", pct) : String.format("%.1f%%", pct);
    }

    /**
     * How hard the advised rate reacts to inflation missing its target, at
     * Standard - since 0.7.52 the dial beside the target sets another,
     * never as little as one (HOW STRICT, below).
     *
     * The Taylor principle: a coefficient greater than one, so a point of extra
     * inflation is met with MORE than a point of extra rate and the REAL rate
     * rises. Below one, raising rates in response to inflation still leaves
     * money cheaper than it was and feeds the thing it was meant to stop.
     */
    public static final double TAYLOR_WEIGHT = 1.5;

    /* -----------------------------------------------------------------------
       HOW STRICT (0.7.52)

       Jerus, 2026-10-06, rather than chasing the inflation ensemble's two
       failing criteria: "just a very simple fix, aka beside the target
       inflation, how strict, very strict then it trys to have it below the
       target, very loose and the target is a suggestion". A dial of five
       steps beside the target, and two numbers of the rule at each:

       - the SLACK, where the rule starts to act, against the target. Under
         nothing it is a strict bank's AIM under the target - the target
         treated as a ceiling, and the rule struck exactly as it would be at
         a target of the aim. Over nothing it is a loose bank's BAND over
         the target (either side of it until 0.7.81 - LOOSE MINDS LOW
         INFLATION, below): inside it the rule holds the neutral rate, and
         past it it answers only the excess, so the rate never jumps at the
         band's edge;
       - the WEIGHT it answers the gap with: TAYLOR_WEIGHT at Standard, more
         at Strict, less at Loose - never as little as one, so past its band
         a point of inflation still makes money dearer and nothing spirals.

       STANDARD IS THE RULE AS IT WAS, TO THE BIT: no slack and TAYLOR_WEIGHT,
       and ruleRate() reduces to the 0.7.42 line operation for operation.
       Trust is still judged against the target, not the aim (Expectations),
       and the lean that forgives a miss is measured against what holding the
       target takes - the Standard rule's advice, holdingRate() - so a loose
       bank that does less than that loses trust as far as it falls short,
       through the rule that was already there; a strict one can lean no more
       than all the way. Saved by name (DataSave.policyStrictness); an older
       save reads STANDARD. CentralBankCheck 21 holds it.

       LOOSE MINDS LOW INFLATION (0.7.81, batch N6). Jerus, 2026-10-08, told
       that very loose held its rate while inflation ran under the target
       and Standard eased: "loose yes, its just doesnt mind high inflation
       as much, but low inflation definitely". So a loose step's band is
       over the target only. From the target up to the band's top it holds
       the neutral rate, and past it it answers the excess at its weight, as
       since 0.7.52; under the target it cuts as Standard does, to the bit -
       no band, and TAYLOR_WEIGHT on the whole gap (ruleRate()). As Standard
       and no harder (star N6-1): his words make a loose bank differ from
       Standard only in how much it minds inflation over the target, and
       nothing in the record says how much harder than Standard a bank that
       minds low inflation would cut. So a loose bank's money is never
       dearer than Standard's at any inflation, and under the target it
       leans exactly as far as holding the target takes (holdingRate()),
       so it loses no trust there. The strict steps are as they were.
       ----------------------------------------------------------------------- */

    /** How far under the target the very strict bank aims, a fraction a year: Expectations.TOLERANCE, the furthest under it inflation can sit and still count as on target - so hitting the aim costs no trust. Never under MIN_INFLATION_TARGET (Strictness.aim()). */
    public static final double STRICTEST_AIM = Expectations.TOLERANCE;

    /** The band over the target the very loose bank lets be, a fraction a year (either side of it until 0.7.81; under it, it cuts as Standard does): twice Expectations.TOLERANCE, so its top is a miss trust counts and the bank does not answer. */
    public static final double LOOSEST_BAND = 2 * Expectations.TOLERANCE;

    /** The very strict bank's weight on the gap from its aim: TAYLOR_WEIGHT's margin over one, doubled. */
    public static final double STRICTEST_WEIGHT = 2.0;

    /** The very loose bank's weight on the gap past its band: TAYLOR_WEIGHT's margin over one, halved - still over one (the Taylor principle). */
    public static final double LOOSEST_WEIGHT = 1.25;

    /** The five steps of the dial, loosest first; Strict and Loose sit halfway between Standard and their ends, in both numbers. */
    public enum Strictness {
        VERY_LOOSE("Very loose", LOOSEST_BAND, LOOSEST_WEIGHT),
        LOOSE("Loose", LOOSEST_BAND / 2, (TAYLOR_WEIGHT + LOOSEST_WEIGHT) / 2),
        STANDARD("Standard", 0, TAYLOR_WEIGHT),
        STRICT("Strict", -STRICTEST_AIM / 2, (TAYLOR_WEIGHT + STRICTEST_WEIGHT) / 2),
        VERY_STRICT("Very strict", -STRICTEST_AIM, STRICTEST_WEIGHT);

        /** Its name on the dial. */
        public final String words;
        /** Where the rule starts to act, against the target, a fraction a year: under nothing a strict bank's aim under it, over nothing a loose bank's band over it (either side until 0.7.81). */
        public final double slack;
        /** The rule's weight on the gap it answers. */
        public final double weight;

        Strictness(String words, double slack, double weight) {
            this.words = words;
            this.slack = slack;
            this.weight = weight;
        }

        /** What it aims inflation at under a target: the target, or under it by the slack - never under MIN_INFLATION_TARGET, because no central bank aims at falling prices. */
        public double aim(double target) {
            return slack < 0 ? Math.max(Math.min(target, MIN_INFLATION_TARGET), target + slack) : target;
        }

        /** How far over its aim it lets inflation be before it acts: nothing, or the slack. Under its aim a loose step cuts as Standard does since 0.7.81 (ruleRate()). */
        public double band() { return Math.max(slack, 0); }

        /** The step at a place on the dial, 0 the loosest, held inside the ends. */
        public static Strictness at(int step) {
            Strictness[] all = values();
            return all[Math.max(0, Math.min(all.length - 1, step))];
        }

        /** By its saved name: STANDARD for null or a name this build does not know. */
        public static Strictness named(String name) {
            for (Strictness s : values()) if (s.name().equals(name)) return s;
            return STANDARD;
        }
    }

    /** How strict the rule is: STANDARD until the player moves it. */
    private Strictness strictness = Strictness.STANDARD;

    public Strictness getStrictness() { return strictness; }

    /** Sets how strict the rule is, applied at once like the target; null leaves it where it was. */
    public void setStrictness(Strictness s) {
        if (s == null) return;
        if (s != strictness) decided(DecisionLog.CENTRAL_BANK, "Central bank strictness to " + s.words);
        strictness = s;
    }

    /** What a bank this strict aims at under a target, in words: "aims at 2.0%", "aims under 2.0%, at 1.0%", "acts only past 3.0% (or under 2.0%)" - under the target itself since 0.7.81, as Standard does; its band's foot, "(or under 1.0%)", until then. */
    public static String aimWords(double target, Strictness s) {
        if (s.band() > 0) {
            return "acts only past " + pct1(target + s.band()) + " (or under " + pct1(target) + ")";
        }
        if (s.aim(target) < target) return "aims under " + pct1(target) + ", at " + pct1(s.aim(target));
        return "aims at " + pct1(target);
    }

    /** ...at the city's target and strictness. */
    public String aimWords() { return aimWords(inflationTarget, strictness); }

    /** A rate to one place, with a true minus: "2.0%", "−1.0%". */
    private static String pct1(double rate) {
        double pct = rate * 100;
        return (pct < -.05 ? "\u2212" : "") + String.format("%.1f%%", Math.abs(pct) < .05 ? 0 : Math.abs(pct));
    }

    /**
     * What a rule would set, given this month's inflation, held inside the
     * dial's bounds - ruleRate() is the rule before them.
     *
     * INFLATION ONLY, WITH NO OUTPUT GAP, and that is a judgement about this
     * model rather than about monetary policy. A Taylor rule normally carries a
     * second term in the output gap; this city runs 34% unemployment and has
     * for its whole history, so any gap measure built on it would read as
     * permanent enormous slack and advise the floor for ever. The game has a
     * trustworthy price index and does not have a trustworthy output gap, so
     * the rule uses the one it has.
     *
     * Advisory unless the rule has the dial. It is shown beside the dial with
     * its reasoning, and moves nothing on its own - except with the autopilot
     * on (THE AUTOPILOT, above), when the top of every month sets the dial to
     * it.
     */
    public double advisedPolicyRate(double inflation) {
        return advisedPolicyRate(inflation, strictness);
    }

    /** ...at a strictness of the caller's, at the city's target (0.7.52): the Policy tab's preview of the dial (PolicyPreview.ruleAt()) and holdingRate(). */
    public double advisedPolicyRate(double inflation, Strictness s) {
        return Math.max(MIN_POLICY_RATE, Math.min(MAX_POLICY_RATE, ruleRate(inflation, inflationTarget, s)));
    }

    /**
     * What the rule itself says, before the dial's bounds (2026-09-21).
     *
     * advisedPolicyRate() is clamped to the dial, which is right for anything
     * that SETS the dial and wrong for a sentence about it: at 45% inflation
     * the monetary page said "the rule says 25.00%" when the rule says 67.5%
     * and it is the dial that stops at 25%. A player reading that could not
     * tell which of the two was the limit. The screens print both when they
     * differ. The cap was Jerus's until the money supply gave the rate a
     * channel to work through, and lifted to 100% with it in 0.7.2 - so, at
     * the default 2% target and Standard, the two now differ at the floor,
     * when inflation runs under zero, and at the top only past 66.7%
     * inflation. Both thresholds move with the target since 0.7.4, point for
     * point, and with the strictness since 0.7.52.
     */
    public double ruleRate(double inflation) {
        return ruleRate(inflation, inflationTarget);
    }

    /** ...and what the Standard rule would set at it, held inside the dial's bounds: what holding the target takes, whatever the strictness - the advice Expectations measures the lean against (0.7.52, HOW STRICT). At Standard it is advisedPolicyRate(). */
    public double holdingRate(double inflation) {
        return advisedPolicyRate(inflation, Strictness.STANDARD);
    }

    /**
     * ...and what it would say at a target of the caller's rather than the
     * dial's (0.7.4): the monetary page's sentence, "at 0% it would set
     * 10.5%", struck from the rule rather than restated by the screen. Moves
     * nothing.
     *
     * THE INTERCEPT MOVES WITH THE TARGET (0.7.42, the anchor). On target the
     * rule sets the neutral REAL rate plus the target: NEUTRAL_RATE is the
     * neutral nominal rate at DEFAULT_INFLATION_TARGET, a one-point real rate,
     * so a 5% target is met on target at 6% and a 0% target at 1%. Until
     * 0.7.42 the rule read NEUTRAL_RATE + TAYLOR_WEIGHT x (inflation - target)
     * at every target, which at a 20% target sat at the 0% floor until
     * inflation passed 18% - a target the rule could not aim at
     * (inflation-research.md, point 4). At the default 2% target the two are
     * the same to the bit: the target's own term is zero.
     *
     * At the city's strictness since 0.7.52 (HOW STRICT, above).
     */
    public double ruleRate(double inflation, double target) {
        return ruleRate(inflation, target, strictness);
    }

    /**
     * ...and at a strictness of the caller's (0.7.52): struck at the aim,
     * as the rule is struck at a target, on the gap past the band, at the
     * strictness's weight. At Strictness.STANDARD - no slack, TAYLOR_WEIGHT -
     * it is the 0.7.42 line to the bit: the aim is the target plus nothing,
     * the gap is the gap less nothing, and the sum is taken in the same
     * order. A loose step under its aim is Standard's rule, to the bit
     * (0.7.81, LOOSE MINDS LOW INFLATION): its band is over the target only.
     * A gap that is not a number stays not a number. Pure.
     */
    public static double ruleRate(double inflation, double target, Strictness s) {
        double aim = s.aim(target);
        double gap = inflation - aim;
        double band = s.band();
        if (band > 0 && gap < 0) return ruleRate(inflation, target, Strictness.STANDARD);
        double answered = Math.abs(gap) <= band ? 0 : gap - Math.copySign(band, gap);
        return NEUTRAL_RATE + (aim - DEFAULT_INFLATION_TARGET) + s.weight * answered;
    }

    /** The neutral nominal rate at the city's target: the neutral real rate plus the target - what the Standard rule sets on target, at any strictness (0.7.45, for the screens; Expectations' lean is measured from it). Pure. */
    public double neutralRate() { return ruleRate(inflationTarget, inflationTarget, Strictness.STANDARD); }

    /**
     * ...and why, in words, for the screen. Past either stop it names both
     * figures, the rule's and the dial's, because they are then not the same
     * number and only one of them is the limit. Off Standard (0.7.52) it
     * says first what the strictness aims at (strictReason()).
     */
    public String adviceReason(double inflation) {
        double advised = advisedPolicyRate(inflation);
        String target = targetWords(inflationTarget);
        if (strictness != Strictness.STANDARD) return strictReason(inflation, advised, target);
        if (Math.abs(inflation - inflationTarget) < .002) {
            return String.format("Inflation is %.1f%%, on its %s target. Hold at %.2f%%.",
                    inflation * 100, target, advised * 100);
        }
        double rule = ruleRate(inflation);
        String says = rule > MAX_POLICY_RATE
                ? String.format("the rule would set %.1f%%; the dial stops at %.0f%%, a guard "
                        + "against a typo rather than a policy", rule * 100, MAX_POLICY_RATE * 100)
                : rule < MIN_POLICY_RATE
                ? String.format("the rule would set %.1f%%; the dial stops at %.0f%%, because no "
                        + "central bank sets a negative rate by typing one",
                        rule * 100, MIN_POLICY_RATE * 100)
                : String.format("the rule says %.2f%%", advised * 100);
        return String.format(
                "Inflation is %.1f%% against a %s target, so %s "
                + "- %s a point of inflation by %.1f points of rate, which is what it "
                + "takes to make money genuinely dearer rather than only nominally.",
                inflation * 100, target, says,
                inflation > inflationTarget ? "meeting" : "giving back",
                TAYLOR_WEIGHT);
    }

    /** adviceReason() off Standard (0.7.52): what this strictness aims at, and what the rule says against it - a loose bank's band over its aim only (0.7.81), Standard's rule under it. */
    private String strictReason(double inflation, double advised, String target) {
        double aim = strictness.aim(inflationTarget), band = strictness.band();
        String bank = String.format("Inflation is %.1f%% against a %s target, and a %s bank %s", inflation * 100, target,
                strictness.words.toLowerCase(Locale.ROOT), aimWords());
        boolean asStandard = band > 0 && inflation < aim;
        if (band > 0 ? !asStandard && inflation <= aim + band : Math.abs(inflation - aim) <= .002) {
            return String.format("%s: %s, so it holds %.2f%%.", bank,
                    band > 0 ? "this is inside the band it lets be" : "this is on its aim", advised * 100);
        }
        double rule = ruleRate(inflation);
        String says = rule > MAX_POLICY_RATE
                ? String.format("the rule would set %.1f%%; the dial stops at %.0f%%", rule * 100, MAX_POLICY_RATE * 100)
                : rule < MIN_POLICY_RATE
                ? String.format("the rule would set %.1f%%; the dial stops at %.0f%%", rule * 100, MIN_POLICY_RATE * 100)
                : String.format("the rule says %.2f%%", advised * 100);
        return String.format("%s, so %s - %s a point of inflation %s by %s points of rate%s.", bank, says,
                inflation > aim ? "meeting" : "giving back",
                asStandard ? "under the target" : band > 0 ? "past the band" : "off its aim",
                java.math.BigDecimal.valueOf(asStandard ? TAYLOR_WEIGHT : strictness.weight).stripTrailingZeros().toPlainString(),
                asStandard ? ", as Standard does" : "");
    }
    private double currentRate = baseRate;
    private double GDP;

    /**
     * The month's tax take. One of the two measures the rate is priced on.
     *
     * GDP alone is the wrong denominator for a city: the lender is not repaid
     * out of the economy's output, it is repaid out of what the government can
     * actually collect from it. Real municipal credit is rated on debt against
     * revenue for exactly that reason. Using both, evenly, means a city that
     * grows its economy AND one that raises its rate both become better credits,
     * which is the right pair of levers to reward.
     */
    private double monthlyTaxRevenue;

    /**
     * How far the city is overdrawn. Counted as borrowing, because it is.
     *
     * Without this the city could sit $1.1M in the red with no bonds outstanding
     * and be quoted 1%, because getAllPrincipal() was the only input. That
     * happened in the hand-played run the day this was written: the 30-year
     * balloon matured, principal went to zero, cash went to -$1,143,330, and the
     * market immediately offered the floor rate. An overdraft is the most
     * desperate borrowing there is and it now prices like it.
     */
    private double overdraft;

    /**
     * The most either measure alone can add to the rate.
     *
     * Two measures, five points each, so the spread runs 0 to 10 points over the
     * floor and the rate from the dial to ten points above it - 3% to 13% at
     * the default dial, where it ran 1% to 11% while CITY_DISCOUNT stood (until
     * 0.7.0). Neither measure can price the city on
     * its own: a city with no economy but plenty of revenue, or the reverse, is
     * capped at half the punishment.
     *
     * GENTLER AGAIN. This was ten points each, and Jerus asked for "a lot more
     * gentler" after playing a city by hand: a young city with a small tax base
     * was quoted 18% for one ordinary apartment block, because a sensible build
     * is simply large next to the revenue of a town of two thousand people. The
     * curve was punishing the size of the city, not the recklessness of the
     * borrowing.
     */
    private static final double MAX_SPREAD_PER_MEASURE = 0.05;


    /**
     * Debt, as a multiple of a year of the thing, at which a measure maxes out.
     *
     * GENTLER ON PURPOSE. The first version blended the two measures into one
     * index and multiplied by a flat spread, with no per-measure ceiling, which
     * put the rate against its cap at about 20x annual revenue. Jerus played a
     * run under it: "the financing actually should be gentler... debt was really
     * bad." He is right, and 20x was never defensible - real cities carry
     * multiples of revenue for decades without being priced as distressed.
     *
     * A hundred and fifty years of revenue is where a measure reaches its five
     * points, and it ramps linearly to get there.
     *
     * That is deliberately far out. Fifty was the first attempt and it read as
     * harsh in play, because the ratio a growing city runs at is dominated by
     * how small its tax base is rather than by how rash it is being - a city
     * borrowing to build the thing that will produce its revenue is at its worst
     * ratio precisely when the borrowing is most sensible.
     *
     * MEASURED on a city of 1,256 with $3.6M of annual tax revenue, with the
     * loan itself priced in - before 0.7.0, on a floor two points under the
     * dial, so every figure below is two points higher today:
     *
     *      1x revenue  1.05%      30x   2.44%      200x   8.96%
     *      2x revenue  1.10%      50x   3.41%      500x   the 11% ceiling
     *      5x revenue  1.24%     100x   5.81%
     *     10x revenue  1.48%
     *
     * For comparison, the ask that made Jerus raise this - $900k on a town of
     * 2,000, about 42x its annual revenue - was quoted 18.23% under the old
     * curve and prices at about 3% under this one.
     */
    private static final double FULL_STRESS_MULTIPLE = 150;

    /** The cheapest money the market will ever offer, whatever the books say (public since CD1: YieldCurve clamps every forward at it). */
    public static final double MIN_RATE = 0.005;

    /** How many times to walk the face-value/rate fixed point. See quoteRate(). */
    private static final int QUOTE_ITERATIONS = 6;

    private List<Debt> debts;

    public DebtManager() {
        debts = new ArrayList<>();

    }

    public Debt addShortTermTBill(double faceValue, int months, int monthStarted) {
        return addShortTermTBill(faceValue, months, monthStarted, false);
    }

    public Debt addShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign) {
        return book(new ShortTermTBill(faceValue, months, monthStarted, foreign));
    }

    public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate) {
        return addMediumTermBond(faceValue, months, monthStarted, rate, false);
    }

    public Debt addMediumTermBond(double faceValue, int months, int monthStarted,
                                  double rate, boolean foreign) {
        return book(new MediumTermBond(faceValue, months, monthStarted, rate, foreign));
    }

    public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate) {
        return addLongTermBond(faceValue, months, monthStarted, rate, false);
    }

    public Debt addLongTermBond(double faceValue, int months, int monthStarted,
                                double rate, boolean foreign) {
        return book(new LongTermBond(faceValue, months, monthStarted, rate, foreign));
    }

    /** A bond sold callable (CD4): the right to call it whole at 100 from the game month firstCallMonth on, its coupon the caller's, option included. */
    public Debt addCallableBond(double faceValue, int months, int monthStarted, double rate, int firstCallMonth) {
        return book(LongTermBond.callable(faceValue, months, monthStarted, rate, firstCallMonth));
    }

    /**
     * A floating-rate note (CD5, spec-city-debt.md section 2.4): a coupon at the policy
     * rate plus {@code margin}, the face at the end. {@code index} is the policy rate now,
     * which the first coupon follows until the first reset (processAllDebts()).
     */
    public Debt addFloatingRateNote(double faceValue, int months, int monthStarted, double margin, double index) {
        return book(new FloatingRateNote(faceValue, months, monthStarted, margin, index));
    }

    /**
     * An inflation-linked bond (CD5, section 2.5): a real coupon on a principal that follows
     * the price index, never under the face at the end. {@code baseIndex} is the price index
     * now (priceIndexLevel()), which the ratio is the index over.
     */
    public Debt addLinkedBond(double faceValue, int months, int monthStarted, double realCoupon, double baseIndex) {
        return book(new InflationLinkedBond(faceValue, months, monthStarted, realCoupon, baseIndex));
    }

    /**
     * The one place paper joins the list, so the one place it can be told the
     * rate.
     *
     * Three add methods each doing their own `debts.add()` is three chances to
     * forget, and a foreign bond that never learned the exchange rate values
     * itself at 1.00 for ever - it would sit in the city's debt looking cheap
     * and never respond to the currency at all. Silent, and the kind of thing
     * that only shows up when somebody wonders why devaluation did nothing.
     */
    private Debt book(Debt paper) {
        paper.setExchangeRate(exchangeRate);
        debts.add(paper);
        return paper;
    }

    /* =======================================================================
       THE RATE THE FOREIGN PAPER IS VALUED AT
       ======================================================================= */

    private double exchangeRate = 1.0;

    /**
     * Pushed down to every instrument, from the month tick AND from the load
     * path.
     *
     * Both, because they are two different ways of arriving at the same state
     * and only one of them was ever going to be remembered. A city loaded with
     * USD debt and a currency at 1.40 would otherwise value that debt at 1.00
     * until the first month ticked, which is long enough for the debt screen,
     * the credit rating and the player's decision to all be wrong.
     */
    public void setExchangeRate(double rate) {
        if (rate <= 0) return;
        this.exchangeRate = rate;
        for (Debt d : debts) d.setExchangeRate(rate);
    }

    public double getExchangeRate() { return exchangeRate; }

    /** What the city owes abroad, in local money at today's rate. */
    public double getForeignPrincipal() {
        double total = 0;
        for (Debt d : debts) if (d.isForeign()) total += d.getOustandingPrincipal();
        return total;
    }

    /** ...and in the dollars it is actually owed in, which do not move. */
    public double getForeignPrincipalUsd() {
        double total = 0;
        for (Debt d : debts) if (d.isForeign()) total += d.principalInCurrency();
        return total;
    }

    /** What it owes at home. */
    public double getDomesticPrincipal() {
        double total = 0;
        for (Debt d : debts) if (!d.isForeign()) total += d.getOustandingPrincipal();
        return total;
    }

    /** Next month's USD coupon bill, in dollars. */
    public double getForeignCouponUsd() {
        double total = 0;
        for (Debt d : debts) if (d.isForeign()) total += d.couponInCurrency();
        return total;
    }

    public boolean hasForeignDebt() {
        for (Debt d : debts) if (d.isForeign()) return true;
        return false;
    }

    /* =======================================================================
       WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING
       =======================================================================

       CHEAPER UP FRONT, AND THAT IS THE TRAP.

       The world's base rate sits below the city's own floor, so the first USD
       bond a city issues really is cheaper than the domestic one beside it -
       which is precisely why governments take them, and precisely why the bill
       arrives later in a currency they do not print. The screen quotes both
       together so the choice is informed rather than hidden; it does not make
       the choice safe.

       PRICED ON EXPORTS, not on GDP or tax revenue. A foreign lender is not
       repaid out of the local economy and cannot be repaid in local money: it
       is repaid in dollars, and the only dollars the city has ever earned came
       from selling something abroad. Debt-to-exports is the measure sovereign
       analysts actually use for exactly this reason.

       AND THE MEASURE IS TAKEN IN LOCAL MONEY, which is what closes the loop.
       getForeignPrincipal() rises when the currency falls, so a devaluation
       worsens the ratio without a cent being borrowed, which raises the
       premium, which makes the next bond dearer. Devalue, the burden rises,
       solvency looks worse, the currency falls further. Mexico 1994, Asia 1997,
       Argentina 2001, Turkey repeatedly. It is not modelled as a special case;
       it falls out of measuring the right two things against each other.
       ======================================================================= */

    /** The world's price of money. Deliberately below the city's own floor. */
    public static final double WORLD_BASE_RATE = .02;

    /** What the world adds on top of that, at the city's very worst. */
    public static final double MAX_COUNTRY_PREMIUM = .16;

    /** USD debt at this many years of exports, and the solvency term maxes out. */
    public static final double FULL_STRESS_EXPORT_YEARS = 8;

    /**
     * A year's USD bill at this share of a year's exports, and the service term
     * maxes out. A quarter of export earnings going to foreign creditors is
     * roughly where real sovereigns start being priced as distressed.
     */
    public static final double FULL_STRESS_SERVICE_SHARE = .25;

    /** How the two halves of country risk are weighted. Can it pay, and can it pay NOW. */
    public static final double SOLVENCY_WEIGHT = .60, SERVICE_WEIGHT = .40;

    /** Above this many years of exports the window shuts outright. */
    public static final double WINDOW_SHUT_EXPORT_YEARS = 14;

    /** ...and above this share of exports going out in service, likewise. */
    public static final double WINDOW_SHUT_SERVICE_SHARE = .45;

    /** What a default abroad adds to the premium the day it happens. */
    public static final double DEFAULT_SCAR = .10;

    /** ...and how much of the scar is left after each month. Half-life ~5 years. */
    public static final double SCAR_DECAY = .9885;

    private double monthlyExports;
    private double importCover = Double.MAX_VALUE;
    private double defaultScar;
    private int monthsSinceForeignDefault = -1;

    /**
     * The two figures the world prices the city on, handed down each month.
     *
     * @param monthlyExportsLocal a trailing month of exports, in local money
     * @param cover               months of import cover
     */
    public void setTrade(double monthlyExportsLocal, double cover) {
        this.monthlyExports = Math.max(0, monthlyExportsLocal);
        this.importCover = cover;
    }

    public double getMonthlyExports()    { return monthlyExports; }
    public double getMonthlyTaxRevenue() { return monthlyTaxRevenue; }

    /**
     * Tears every piece of foreign paper off the books.
     *
     * @return what was written off, in local money at today's rate
     */
    public double repudiateForeignDebt() {
        double written = 0;
        Iterator<Debt> it = debts.iterator();
        while (it.hasNext()) {
            Debt d = it.next();
            if (d.isForeign()) {
                written += d.getOustandingPrincipal();
                it.remove();
            }
        }
        return written;
    }
    public double getImportCover()    { return importCover; }

    /** Can it pay at all: USD debt against a year of exports. */
    public double solvencyStress() {
        return solvencyStressAt(getForeignPrincipal());
    }

    /**
     * ...priced with a proposed bond already on the books.
     *
     * A quote that ignores the loan being quoted is the same bug the domestic
     * curve had and quoteRate() exists to avoid: the city is told a rate that
     * stops being true the instant it accepts.
     *
     * @param owed foreign principal in LOCAL money, the new paper included
     */
    public double solvencyStressAt(double owed) {
        if (owed <= 0) return 0;
        double annual = monthlyExports * 12;
        if (annual <= 0) return 1;                       // owes dollars, earns none
        return Math.min(1, (owed / annual) / FULL_STRESS_EXPORT_YEARS);
    }

    /**
     * Can it pay NOW: next year's USD bill against next year's export earnings.
     *
     * THE DEBT SERVICE RATIO, which is the other half of every sovereign credit
     * assessment and the one that actually times a crisis. A country can carry
     * a large stock of foreign debt indefinitely if the schedule is long; it
     * fails when too much of it comes due at once against what it earns.
     *
     * THIS REPLACED A RESERVE-COVER TERM, and the reason is worth recording.
     * Cover was the obvious measure and it was wrong twice over: importCover()
     * returned 0 for any city whose cumulative foreign position was negative
     * (the vault and the cumulative balance were one number then), which was
     * most of them and said nothing about creditworthiness, and it
     * meant a city with NO foreign debt at all was quoted a 5.6% risk premium
     * for the privilege of not owing anybody anything. Measured, on a fixture
     * that had never borrowed a dollar.
     *
     * Reserves still matter - they are what defends the currency, which is
     * phase 2's job and shown on the trade screen. They are not what a lender
     * is looking at.
     */
    public double serviceStress() {
        return serviceStressAt(nextYearService());
    }

    public double serviceStressAt(double service) {
        if (service <= 0) return 0;
        double annual = monthlyExports * 12;
        if (annual <= 0) return 1;
        return Math.min(1, (service / annual) / FULL_STRESS_SERVICE_SHARE);
    }

    /** Everything foreign paper demands over the next twelve months, in local money. */
    public double nextYearService() {
        double total = 0;
        for (Debt d : debts) {
            if (!d.isForeign()) continue;
            double[] flows = d.remainingCashFlows();
            for (int i = 0; i < Math.min(12, flows.length); i++) total += flows[i];
        }
        return total;
    }

    /** What the world adds to its base rate for lending to THIS city. */
    public double countryPremium() {
        return countryPremiumAt(getForeignPrincipal());
    }

    public double countryPremiumAt(double owed) {
        double scale = getForeignPrincipal() > 0 ? owed / getForeignPrincipal() : 1;
        double risk = SOLVENCY_WEIGHT * solvencyStressAt(owed)
                + SERVICE_WEIGHT * serviceStressAt(nextYearService() * Math.max(1, scale));
        return Math.min(MAX_COUNTRY_PREMIUM, MAX_COUNTRY_PREMIUM * risk + defaultScar);
    }

    /** The all-in annual rate on a new USD bond: the short end of the world's curve (foreignCurveRate()). */
    public double foreignRate() {
        return WORLD_BASE_RATE + countryPremium();
    }

    /**
     * THE WORLD'S CURVE (0.7.2): what the world charges this city for dollar
     * paper of this many months - the foreign rate, plus the SAME term
     * premium table the city's own curve carries (termPremium()). One curve
     * shape for both currencies: a lender tying money up for thirty years
     * wants paying for the thirty years whoever's money it is, and the
     * world's term premium is not a separate dial. What the city's central
     * bank holds does not compress it - it holds none of the world's paper.
     *
     * WHY (0.7.1's found-on-the-way): a dollar bond was VALUED at the city's
     * short rate (marketValue() read currentRate) and a dollar term loan
     * PRICED flat at the world's rate at every maturity. So the player's dial
     * moved what a dollar bond was worth, which nothing in the world would
     * do, and a dollar buyback priced off the city's curve rather than the
     * one it was sold on. Issue and buyback both read this now, which is what
     * keeps a dollar round trip neutral by construction, as the domestic one
     * is (RestructureCheck).
     */
    public double foreignCurveRate(int months) {
        return foreignRate() + termPremium(months);
    }

    /**
     * ...quoted with the proposed bond priced in.
     *
     * @param extraUsd face of the bond being contemplated, in dollars
     */
    public double quoteForeignRate(double extraUsd) {
        return WORLD_BASE_RATE
                + countryPremiumAt(getForeignPrincipal() + Math.max(0, extraUsd) * exchangeRate);
    }

    /** ...and at a maturity, on the world's curve (0.7.2): the quote plus the term premium for those months. */
    public double quoteForeignRate(double extraUsd, int months) {
        return quoteForeignRate(extraUsd) + termPremium(months);
    }

    /**
     * ...and priced with the proposed paper ON THE BOOKS, its own next year
     * of service included (0.7.2): the rate the world's curve will read for
     * this paper, at this maturity, the moment it is booked.
     *
     * WHY. quoteForeignRate(extraUsd) prices the proposed principal in, and
     * scales the service the city ALREADY owes by it - so a city with no
     * dollar paper was quoted a service term of nothing, and the paper, once
     * booked, was valued with its own coupons in that term. The buyback read
     * a dearer rate than the issue had. At 0.7.1, with a dollar bond valued at
     * the city's short rate, a dollar term round trip netted the city 10-22%
     * of the money raised; on the world's curve alone it still netted
     * 0.4-1%. Priced this way the issue and the buyback read the same premium
     * - this is countryPremium() as it will stand, computed before the paper
     * is booked - so the round trip is neutral by construction, the way the
     * domestic one is. Game.quoteForeign() walks the fixed point (the coupon
     * is struck on the rate, and the service on the coupon).
     *
     * @param proposed the paper as it would be booked, in dollars, not yet on the books
     */
    public double quoteForeignRate(Debt proposed, int months) {
        if (proposed == null) return foreignCurveRate(months);
        proposed.setExchangeRate(exchangeRate);
        double owed = getForeignPrincipal() + proposed.getOustandingPrincipal();
        double service = nextYearService();
        double[] flows = proposed.remainingCashFlows();
        for (int i = 0; i < Math.min(12, flows.length); i++) service += flows[i];
        double risk = SOLVENCY_WEIGHT * solvencyStressAt(owed) + SERVICE_WEIGHT * serviceStressAt(service);
        return WORLD_BASE_RATE + Math.min(MAX_COUNTRY_PREMIUM, MAX_COUNTRY_PREMIUM * risk + defaultScar)
                + termPremium(months);
    }

    /**
     * Whether anybody abroad is still willing to lend.
     *
     * A SUDDEN STOP, on conditions the player can read off the screen before it
     * happens rather than discover. Existing paper still has to be repaid on
     * schedule - that is what makes it dangerous - but no new dollars arrive.
     */
    public boolean foreignWindowOpen() {
        return foreignWindowReason() == null;
    }

    /** Why it is shut, in words, or null if it is open. */
    public String foreignWindowReason() {
        if (monthsSinceForeignDefault >= 0 && monthsSinceForeignDefault < 60) {
            return String.format("the city defaulted abroad %d months ago",
                    monthsSinceForeignDefault);
        }
        double annual = monthlyExports * 12;
        double owed = getForeignPrincipal();
        if (owed > 0 && annual <= 0) {
            return "the city owes dollars and sells nothing abroad";
        }
        if (annual > 0 && owed / annual > WINDOW_SHUT_EXPORT_YEARS) {
            return String.format("USD debt is %.0f years of exports",
                    owed / annual);
        }
        double service = nextYearService();
        if (annual > 0 && service / annual > WINDOW_SHUT_SERVICE_SHARE) {
            return String.format("%.0f%% of exports already goes to foreign creditors",
                    service / annual * 100);
        }
        return null;
    }

    /** Called the month the city fails to pay abroad. */
    public void markForeignDefault() {
        defaultScar = Math.min(MAX_COUNTRY_PREMIUM, defaultScar + DEFAULT_SCAR);
        monthsSinceForeignDefault = 0;
    }

    /** The scar fades, slowly, and the window reopens before the price does. */
    public void ageForeignStanding() {
        if (monthsSinceForeignDefault >= 0) monthsSinceForeignDefault++;
        defaultScar *= SCAR_DECAY;
        if (defaultScar < 1e-6) defaultScar = 0;
    }

    public double getDefaultScar()            { return defaultScar; }
    public int getMonthsSinceForeignDefault() { return monthsSinceForeignDefault; }

    /** Carried, because a scar that heals on reload is not a scar. */
    public double[] foreignStandingToSave() {
        return new double[] { defaultScar, monthsSinceForeignDefault };
    }

    public void restoreForeignStanding(double[] saved) {
        if (saved == null || saved.length < 2) return;
        defaultScar = saved[0];
        monthsSinceForeignDefault = (int) Math.round(saved[1]);
    }

    public void printDebtInfo(int currentMonth) {
        double totalPrincipal = 0;
        double totalMonthlyInterest = 0;

        System.out.println("\n================ DEBT PORTFOLIO ================");

        // 1. Sort by Maturity
        List<Debt> sortedDebts = new ArrayList<>(debts);
        sortedDebts.sort(Comparator.comparingInt(Debt::getMaturityMonth));

        // 2. Header with Column Spacing
        System.out.printf("%-10s | %-15s | %-12s | %-15s%n", "Maturity", "Type", "Principal", "Mo. Interest");
        System.out.println("------------------------------------------------------------");

        for (Debt debt : sortedDebts) {
            double principal = debt.getOustandingPrincipal();
            double interest = debt.getMonthlyInterestExpense(); // Assuming this getter exists

            totalPrincipal += principal;
            totalMonthlyInterest += interest;

            // Highlight if due this month
            String status = (debt.getMaturityMonth() == currentMonth) ? " [DUE NOW]" : "";

            System.out.printf("Month %-4d | %-15s | %-12s | %-15s%s%n",
                    debt.getMaturityMonth(),
                    debt.getType(),
                    formatter.format(principal),
                    formatter.format(interest),
                    status);
        }

        // NOTE: this used to assign the manager's allPrincipal field here, which
        // made the entire debt->interest feedback loop a side effect of printing.
        // getAllPrincipal() computes it on demand now; this printer is pure.
        System.out.println("------------------------------------------------------------");
        System.out.println("TOTAL OUTSTANDING PRINCIPAL: " + formatter.format(totalPrincipal));
        System.out.println("TOTAL MONTHLY INTEREST COST: " + formatter.format(totalMonthlyInterest));
        System.out.println("============================================================\n");
    }

    /**
     * THE CITY'S RATE: the SHORT END of the curve - the note's rate, no term
     * premium - which is what every screen means by "the city's rate" and
     * what the advisor reads. The households' lender priced off it until
     * 0.7.7 (Bank.householdRate() since). A term bond is quoted at
     * curveRate() for its maturity (0.7.1); this one number stays the T-bill
     * rate.
     */
    public double getRate() {
        return currentRate;
    }

    /*
     * THE BANK'S STRAIN PREMIUM USED TO BE HERE (until 0.7.7): bankPremium,
     * set once a month from Bank.ratePremium() and added to every price of
     * the city's paper outside the credit clamp - up to eighteen points, the
     * whole of it with no branch - with getRateBeforeStrain() taking it back
     * off for the hot money. The premium is gone (Bank, WHAT A LOAN COSTS),
     * so the city's paper is its own market price: the dial, its credit
     * spreads and the term premium, and nothing about how lent out the bank is.
     */

    /**
     * What the bank pays for the money it lends the city.
     *
     * Set from Bank.marginalCostOfFunds(): what the bank's money costs it at
     * all, which the city's rate may not go under - see floorRate(). (It was
     * set beside the strain premium until 0.7.7, and for the opposite job.)
     */
    private double costOfFunds;
    public void setCostOfFunds(double rate) { this.costOfFunds = Math.max(0, rate); }
    public double getCostOfFunds()          { return costOfFunds; }
    /**
     * Total outstanding principal across every live debt.
     *
     * NOTE: this used to return a cached field assigned in exactly one place -
     * inside printDebtInfo(). printStartOfMonth() only calls that printer
     * `if(reports)`, so the debt->interest feedback loop silently stopped working
     * whenever reports were off (which simulateMonths does by design). Observed:
     * a $700,000 bond sat outstanding for 100 months with the rate pinned at the
     * 1% floor, then jumped straight to the 20% ceiling the first time a report
     * printed. It went stale the other way too - a matured, removed bond kept
     * being priced in until the next printed report.
     *
     * The new JavaFX debt screen sums the list itself and never touched that
     * cache, so the side effect was disappearing entirely as the console reports
     * get retired. Computed on demand now; the list is small.
     */
    public double getAllPrincipal(){
        double total = 0;
        for (Debt debt : debts) {
            total += debt.getOustandingPrincipal();
        }
        return total;
    }

    //setters
    public void setGDP(double GDP) {
        this.GDP = GDP;
    }

    /** The month's tax take - the other half of what the market prices against. */
    public void setTaxRevenue(double monthlyTaxRevenue) {
        this.monthlyTaxRevenue = monthlyTaxRevenue;
    }

    /**
     * How far the city is overdrawn, pushed in from Game each month.
     *
     * Takes the CASH balance and keeps the negative part; a positive balance is
     * not credit and does not improve the rate.
     */
    public void setCashPosition(double cash) {
        this.overdraft = Math.max(0, -cash);
    }

    public double getOverdraft()  { return overdraft; }

    /**
     * What the treasury owes its central bank in advances (0.7.0), pushed in
     * with the overdraft. Priced like it, because it is the same borrowing a
     * month later: the overdraft at a month's end is advanced at the top of
     * the next, and before 0.7.0 it became an emergency note, which was
     * principal and priced. The market looks at everything the city owes.
     */
    private double advances;

    public void setAdvances(double owed) { this.advances = Math.max(0, owed); }
    public double getAdvances()          { return advances; }

    /**
     * THE CITY'S AUCTIONS, COUNTED (CD2, D33): every domestic sale that booked,
     * its tail and its cover, and the last one's figures. Game.bookProceeds()
     * adds to it; the harness and the Borrow screen read it. Saved beside the
     * market's strike (DataSave.getAuctionTally()). See AuctionTally.
     */
    private final AuctionTally auctionTally = new AuctionTally();

    public AuctionTally getAuctionTally()      { return auctionTally; }

    /** Sales the treasury has booked at auction. */
    public int auctionSales()                  { return auctionTally.auctionSales(); }
    /** ...of them, the ones that cleared more than a point over fair (Auction.MAX_TAIL). */
    public int auctionTailsOverPoint()         { return auctionTally.auctionTailsOverPoint(); }
    /** ...and the ones whose bids did not reach the issue at the city's cap. */
    public int auctionShortSales()             { return auctionTally.auctionShortSales(); }
    /** The largest tail any of them cleared at, in yield. */
    public double largestTail()                { return auctionTally.largestTail(); }
    /** The sale last booked, or null before the first. */
    public AuctionTally.Last lastAuction()     { return auctionTally.lastAuction(); }

    /**
     * THE MARKET'S LAST STRIKE, for the save (0.7.14): the four inputs it was
     * last handed - a month's output, a month's tax, the overdraft, the
     * advances - and the rate it struck on them. The month hands them in
     * before its last price and the treasury's cash moves after it, so a
     * reloaded city that re-struck them from the cash the save holds priced
     * its own paper on a different overdraft than the live city it came
     * from: a basis point apart on a city in deficit, and every curve rate,
     * quote and mark read off it with it. Carried whole, it is the live one.
     *
     * Seven slots once the curve's expectations are struck (CD1): the
     * inflation the rule read and the inflation the city expected, which the
     * curve's path is built from and which cannot be re-derived from the
     * state a month ended in. A manager nobody has struck saves five, so it
     * never writes a number that is not one.
     */
    public double[] marketToSave() {
        if (!isStruck()) return new double[] { GDP, monthlyTaxRevenue, overdraft, advances, currentRate };
        return new double[] { GDP, monthlyTaxRevenue, overdraft, advances, currentRate, struckInflation, struckExpected };
    }

    /** ...and back, over the load path's own strike; an older save has none and keeps that strike. The two expectations come back only from a save that carries them (seven slots or more): an older one loads unstruck, and its curve is the flat one until the next month strikes it. */
    public void restoreMarket(double[] saved) {
        if (saved == null || saved.length < 5) return;
        GDP = saved[0];
        monthlyTaxRevenue = saved[1];
        overdraft = saved[2];
        advances = saved[3];
        currentRate = saved[4];
        if (saved.length >= 7) strikeExpectations(saved[5], saved[6]);
    }

    public List<Debt> getDebt() {
        return debts;
    }

    public void processAllDebts(Game game) {

        Iterator<Debt> iterator = debts.iterator();
        accretedForBank = 0;
        interestBooked = amortisedBooked = 0;
        upliftBooked = upliftPaidBooked = 0;
        java.util.Arrays.fill(interestByKind, 0);

        while (iterator.hasNext()) {
            Debt debt = iterator.next();

            /*
             * A FLOATER READS THE DIAL AND A LINKER THE PRICE INDEX (CD5),
             * before the month's books are struck: the floater's effective
             * rate resets with its coupon, the linker's carrying value
             * catches up with its index. Nothing else is touched.
             */
            if (debt instanceof FloatingRateNote note) note.reset(getPolicyRate());
            else if (debt instanceof InflationLinkedBond linked) linked.index(priceIndexLevel());

            /*
             * THE CITY'S BOOKS FOR THE MONTH (CD3), before the payments: the
             * piece's interest expense at its effective rate, the coupon
             * part of the payment it is about to make, and its carrying value
             * stepped. Summed in local money at the rate the paper is valued
             * at, which is the rate the coupon is paid at, so the month's
             * expense less its amortisation is the coupons the month paid.
             * See Debt, THE CITY'S BOOKS.
             */
            debt.amortise();
            interestBooked += debt.getLastInterestExpense();
            interestByKind[bookKind(debt.getType())] += debt.getLastInterestExpense();
            if (debt instanceof InflationLinkedBond linked) {
                // The uplift paid in the last month is the uplift line's, cash, and not the discount wearing
                // off: the interest line's not-cash part is the discount alone (CD5).
                amortisedBooked += debt.getLastAmortised() + linked.getLastUpliftPaid();
                upliftBooked += linked.getLastUplift();
                upliftPaidBooked += linked.getLastUpliftPaid();
            } else {
                amortisedBooked += debt.getLastAmortised();
            }

            /*
             * THE DISCOUNT ACCRETES (0.7.1), a month of it, before the month's
             * payments move the holders' shares: the bank's part is its
             * interest at the settle, the households' is theirs at maturity
             * (their paper is carried at market), and the central bank's was
             * taken when it bought, at face. See Debt.accrete().
             */
            if (!debt.isForeign()) {
                double out = debt.getOustandingPrincipal();
                double bankShare = out > 0 ? debt.bankPrincipal() / out : 0;
                double step = debt.accrete();
                if (step > 0) accretedForBank += step * bankShare;
            }

            debt.processMonth(game);

            if (debt.isMatured()) {
                iterator.remove();
            }

        }
        updateInterest();
    }

    /* -----------------------------------------------------------------------
       THE CITY'S BOOKS, IN TOTAL (CD3, spec-city-debt.md section 5)

       Struck in processAllDebts() from each piece's amortise(), in local
       money at the rate each paper is valued at, and read until the next
       month's strike. The totals are not saved: the month's government block
       carries the amortisation (NationalAccounts.getInterestNotCash()) and
       each piece carries its own last expense, so a loaded city has both;
       these totals read nothing until the first month ticks. THE INTEREST BY
       KIND (CD6) IS SAVED, though: the Government tab's grid reads it after
       the month, and it is a flow the state the month ended in cannot give
       back - a bill that matured in the month is gone from the list, and a
       dollar piece's expense, struck at the rate of that month, is not what
       its last expense reads at the new rate. A save without it reads zeros.
       ----------------------------------------------------------------------- */

    /** This month's interest expense on the city's paper: every piece's carrying value at its effective rate, a month. The coupons and the discount wearing off together. */
    private double interestBooked;

    /** ...and the part of it that is not coupon: the discount amortised, below nothing for a premium. Non-cash. */
    private double amortisedBooked;

    /**
     * The month's inflation uplift on the linked bonds (CD5): the indexation each booked as its
     * carrying value caught up with the price index (InflationLinkedBond.index()), summed. An
     * expense beside the interest, and not cash - below nothing in a month the index fell.
     */
    private double upliftBooked;

    /** ...and the uplift the month PAID in cash: a linker's indexed principal over its face, in its last month. The cash is in the coupons paid; here it is the uplift line's, taken off the not-cash row. */
    private double upliftPaidBooked;

    public double getInterestBooked()  { return interestBooked; }
    public double getAmortisedBooked() { return amortisedBooked; }

    /**
     * THE INTEREST BOOKED, BY KIND (CD6): the month's interest expense on each kind of paper, in
     * BOOK_KINDS' order, struck in processAllDebts() beside interestBooked from the same figures, so
     * the five add up to getInterestBooked() - the budget's line (NationalAccounts.getInterestExpense())
     * - including the interest of a piece that fell due in the month and is no longer on the list, and
     * a dollar piece's, booked at the month's rate and not read again at a new one. Saved.
     */
    private final double[] interestByKind = new double[5];

    /** The kinds of paper the interest is booked by, as Debt.getType() calls them: the notes, the serial bonds, the bonds, the floaters, the linkers. */
    public static final String[] BOOK_KINDS = {"NOTE", "SERIAL", "TERM", "FLOAT", "LINKED"};

    /** A type's place in BOOK_KINDS; a type that is none of them is a term loan's, as ladderKind() reads it. */
    static int bookKind(String type) {
        for (int i = 0; i < BOOK_KINDS.length; i++) if (BOOK_KINDS[i].equals(type)) return i;
        return 2;
    }

    /** The month's interest expense on one kind of paper (a BOOK_KINDS entry), in local money: the Government tab's row of the budget's interest line. A type that is none of them reads as a term loan's. */
    public double getInterestBookedOf(String type) { return interestByKind[bookKind(type)]; }

    /** The interest by kind as a save carries it: a copy, in BOOK_KINDS' order. */
    double[] interestByKindToSave() { return interestByKind.clone(); }

    /** ...and put back by a load; null or the wrong length (an older save) reads as zeros. */
    void restoreInterestByKind(double[] saved) {
        java.util.Arrays.fill(interestByKind, 0);
        if (saved != null && saved.length == interestByKind.length) System.arraycopy(saved, 0, interestByKind, 0, saved.length);
    }

    /** The month's inflation uplift on linked bonds, booked (CD5): an expense, not cash. Nothing for a city that holds none. */
    public double getUpliftBooked()    { return upliftBooked; }

    /** ...and the part of it the month paid in cash (a linker's last month): in the cash coupons the holders were paid, and the uplift line's, not the interest line's. */
    public double getUpliftPaid()      { return upliftPaidBooked; }

    /**
     * The coupons the month's books paid: its interest expense less what was amortised. With a
     * linked bond in its last month the cash coupons the holders were paid are this plus
     * getUpliftPaid() (CD5): the uplift paid goes to the holders through the coupon path, and
     * the books count it on the uplift's line.
     */
    public double getCouponBooked()    { return interestBooked - amortisedBooked; }

    /** What the city's paper is carried at, every piece together, in local money: what the balance sheet shows (face less the unamortised discount, plus the premium). */
    public double getCarryingValue() {
        double total = 0;
        for (Debt d : debts) total += d.getCarryingValue();
        return total;
    }

    /** What the city's paper is worth on the market today, every piece together: the note a government discloses and does not book (getTotalMarketValue()). */
    public double getFairValue() { return getTotalMarketValue(); }

    /**
     * Face value of the discount notes outstanding.
     *
     * The screens need it because a note is the one instrument with no
     * coupon: its interest is its discount, which since CD3 wears off on the
     * budget month by month as effective interest (Debt.amortise()) though no
     * cash leaves until it falls due - a sentence the screen says beside the
     * number. See ShortTermTBill.
     */
    public double getNotePrincipal() {
        double total = 0;
        for (Debt d : debts) {
            if (d instanceof ShortTermTBill) total += d.getOustandingPrincipal();
        }
        return total;
    }

    /* -----------------------------------------------------------------------
       WHAT THE FINANCES TAB DRAWS THE DEBT FROM (0.7.32)

       The sums the tab used to make for itself - the coupon, each kind's
       principal, the ladder's years, the next twelve months, the rate a
       piece is valued at - here, so the screen prints the model's figures
       and a harness can hold them (ForeignDebtCheck, section 9). Pure reads.
       ----------------------------------------------------------------------- */

    /**
     * What the paper pays in coupon next month, in cash, every piece together,
     * in local money: the pieces' own getMonthlyCoupon(), summed - the
     * Finances tab's COUPON. A forward cash measure, which is not the budget's
     * interest line (that is the carrying value at the effective rate, struck
     * last month: getInterestBooked()). A note pays none (its cost was its
     * discount), so a city owing only notes reads nothing here;
     * getNotePrincipal() is what pays at maturity instead. A linked bond's
     * inflation uplift, which it pays with its last coupon, is not in it: that
     * is getUpliftDueNextMonth().
     */
    public double getMonthlyCoupon() {
        double total = 0;
        for (Debt d : debts) total += d.getMonthlyCoupon();
        return total;
    }

    /** The inflation uplift the linked bonds pay next month, with their last coupon, every piece together, in local money (CD6): cash beside getMonthlyCoupon(), and the uplift line's in the budget. */
    public double getUpliftDueNextMonth() {
        double total = 0;
        for (Debt d : debts) total += d.getUpliftDue();
        return total;
    }

    /** Principal outstanding of one kind of paper, by Debt.getType() - "NOTE", "SERIAL" or "TERM" - at home and abroad, in local money. */
    public double getPrincipalOf(String type) {
        double total = 0;
        for (Debt d : debts) if (d.getType().equals(type)) total += d.getOustandingPrincipal();
        return total;
    }

    /**
     * Principal outstanding of one kind as the ladder draws it (CD5; LADDER_KINDS' index, 0 the notes, 1
     * the serial bonds, 2 the bonds): a floating-rate note's face and an inflation-linked bond's are the
     * bonds', as ladderKind() sorts them, which getPrincipalOf("TERM") leaves out. At home and abroad, in
     * local money. The Finances hub's bar of the principal by kind reads it.
     */
    public double getPrincipalOfLadderKind(int kind) {
        double total = 0;
        for (Debt d : debts) if (ladderKind(d.getType()) == kind) total += d.getOustandingPrincipal();
        return total;
    }

    /**
     * Everything the city's paper asks of it over the next `months` months,
     * coupons and principal together, in local money - the first `months` of
     * every piece's remainingCashFlows(), the ladder's own read: the Debt
     * service page's twelve months (the Finances spec's D6).
     */
    public double dueWithin(int months) {
        double total = 0;
        for (Debt d : debts) {
            double[] flows = d.remainingCashFlows();
            for (int i = 0; i < Math.min(months, flows.length); i++) total += flows[i];
        }
        return total;
    }

    /** ...of the dollar paper alone, in the dollars it is owed in (0.7.40): the first `months` of each foreign piece's own schedule - Finances' ask abroad. */
    public double dueAbroadWithinUsd(int months) {
        double total = 0;
        for (Debt d : debts) {
            if (!d.isForeign()) continue;
            double[] flows = d.scheduleOwed();
            for (int i = 0; i < Math.min(months, flows.length); i++) total += flows[i];
        }
        return total;
    }

    /**
     * The one flat rate at which a piece's payments price to marketValue(): its
     * yield to maturity on the price a buyback pays (CD1; until then the curve
     * read at the months it has left, which was the rate marketValue() had
     * discounted it at). A screen that prices the piece flat at this rate
     * reads the price a buyback pays; the curve discounts each payment at its
     * own month's factor, so no single month's curve rate is this. The short
     * rate for no paper.
     */
    public double valuationRate(Debt paper) {
        if (paper == null) return getRate();
        return yieldOf(paper);
    }

    /** What buying a piece back today would save against its face: its principal less marketValue() - below nothing when it costs a premium to retire (the Finances tab's book, which subtracted them itself). */
    public double underFace(Debt paper) {
        return paper == null ? 0 : paper.getOustandingPrincipal() - marketValue(paper);
    }

    /** How many calendar years the ladder draws before it totals the rest as "later". */
    public static final int LADDER_YEARS = 12;

    /** The ladder's instruments, in its order: what Debt.getType() calls each, short to long. */
    public static final String[] LADDER_KINDS = {"NOTE", "SERIAL", "TERM"};

    /** A kind's place in LADDER_KINDS: a note 0, a serial 1, anything else - a term loan - 2. */
    public static int ladderKind(String type) {
        return "NOTE".equals(type) ? 0 : "SERIAL".equals(type) ? 1 : 2;
    }

    /**
     * One rung of the ladder: a calendar year of payments, from its first
     * month on the ladder to its last - a whole year, but the first, which
     * starts next month, and the one the last payment falls in, which ends
     * with it; both -1 for a year after that; and "later" from the January
     * after the twelfth to the last payment: what falls due in it by kind,
     * coupons and principal together, and how much of each kind is owed in
     * dollars; and a proposed issue's payments in it, when the ladder was
     * asked with one.
     */
    public record Rung(int fromYear, int toYear, int fromMonth, int toMonth, double[] byKind,
                       double[] abroadByKind, double proposed) {
        /** What the city's paper asks in it. */
        public double owed() { return byKind[0] + byKind[1] + byKind[2]; }
        /** ...of which in dollars. */
        public double abroad() { return abroadByKind[0] + abroadByKind[1] + abroadByKind[2]; }
        /** ...and with the proposed issue's. */
        public double total() { return owed() + proposed; }
    }

    /**
     * The ladder: LADDER_YEARS calendar years from next month's, and what
     * falls after them as one "later" rung (null when nothing does); which
     * of the years is the heaviest (-1 with nothing owed in them) - never
     * "later", which is many years summed; and the totals.
     */
    public record Ladder(List<Rung> years, Rung later, int heaviest, double owed, double proposed) {
        /** What is owed and what the proposed issue would ask, together. */
        public double total() { return owed + proposed; }
    }

    /** The ladder as the city owes it, struck in `month`. */
    public Ladder ladder(int month) { return ladder(month, null); }

    /**
     * WHEN IT FALLS DUE, BY THE CALENDAR YEAR IT IS PAID IN (0.7.32, the
     * Finances spec's D4). Every piece's remainingCashFlows() - payment i in
     * month + 1 + i, so the maturity lands in the year NEXT DUE dates it -
     * binned by CityCalendar.yearOf() of its month. The tab binned twelve
     * months at a time from next month and labelled the bin with this
     * month's year, so a payment in January 2210 stood under "2209" (its
     * B2); and it called the thirteenth bin "year 13 from now" and weighed
     * it against single years, raising a red alert at the many years it
     * summed (B1).
     *
     * @param extra a proposed issue's payments in local money, index i paid
     *              in month + 1 + i as the pieces' are, or null
     */
    public Ladder ladder(int month, double[] extra) {
        int first = CityCalendar.yearOf(month + 1);
        int lastShown = first + LADDER_YEARS - 1;
        int lastMonth = month;
        for (Debt d : debts) lastMonth = Math.max(lastMonth, month + d.remainingCashFlows().length);
        if (extra != null) lastMonth = Math.max(lastMonth, month + extra.length);
        double[][] kind = new double[LADDER_YEARS + 1][3], abroad = new double[LADDER_YEARS + 1][3];
        double[] proposed = new double[LADDER_YEARS + 1];
        int[] from = new int[LADDER_YEARS + 1], to = new int[LADDER_YEARS + 1];
        java.util.Arrays.fill(from, Integer.MAX_VALUE);
        java.util.Arrays.fill(to, Integer.MIN_VALUE);
        for (int m = month + 1; m <= lastMonth; m++) {
            int slot = Math.min(LADDER_YEARS, CityCalendar.yearOf(m) - first);
            from[slot] = Math.min(from[slot], m);
            to[slot] = Math.max(to[slot], m);
        }
        double owed = 0, ask = 0;
        for (Debt d : debts) {
            int k = ladderKind(d.getType());
            double[] flows = d.remainingCashFlows();
            for (int i = 0; i < flows.length; i++) {
                int slot = Math.min(LADDER_YEARS, CityCalendar.yearOf(month + 1 + i) - first);
                kind[slot][k] += flows[i];
                if (d.isForeign()) abroad[slot][k] += flows[i];
                owed += flows[i];
            }
        }
        if (extra != null) {
            for (int i = 0; i < extra.length; i++) {
                proposed[Math.min(LADDER_YEARS, CityCalendar.yearOf(month + 1 + i) - first)] += extra[i];
                ask += extra[i];
            }
        }
        List<Rung> years = new ArrayList<>();
        int heaviest = -1;
        double most = 0;
        for (int y = 0; y < LADDER_YEARS; y++) {
            int fromMonth = from[y] == Integer.MAX_VALUE ? -1 : from[y];
            int toMonth = to[y] == Integer.MIN_VALUE ? -1 : to[y];
            Rung r = new Rung(first + y, first + y, fromMonth, toMonth, kind[y], abroad[y], proposed[y]);
            years.add(r);
            if (r.owed() > most) { most = r.owed(); heaviest = y; }
        }
        Rung later = null;
        if (to[LADDER_YEARS] != Integer.MIN_VALUE) {
            later = new Rung(lastShown + 1, CityCalendar.yearOf(to[LADDER_YEARS]), from[LADDER_YEARS],
                    to[LADDER_YEARS], kind[LADDER_YEARS], abroad[LADDER_YEARS], proposed[LADDER_YEARS]);
        }
        return new Ladder(years, later, heaviest, owed, ask);
    }

    /**
     * Everything the city owes, including what it is overdrawn and what it
     * owes the central bank in advances.
     *
     * NOT the same as getAllPrincipal(), which is bonds and bills only. This is
     * what the market is actually looking at when it decides what to charge.
     */
    public double getPricedDebt() {
        double owed = getAllPrincipal() + Math.max(0, overdraft) + advances;
        // What the market prices the city on is all it owes (CD5): the indexation a linked bond has
        // accrued past its face, when there is some. A city with no linker has none, and owes what it did.
        double indexation = getIndexationOwed();
        return indexation > 0 ? owed + indexation : owed;
    }

    /**
     * What the linked bonds owe past their face today (CD5): each one's face times how far its
     * ratio is over 1 - nothing for a ratio at or under 1, because the deflation floor means the
     * face is owed whatever the index did. In local money; the principal on the books stays the
     * face (InflationLinkedBond).
     */
    public double getIndexationOwed() {
        double total = 0;
        for (Debt d : debts) {
            if (d instanceof InflationLinkedBond linked) total += linked.getFaceValue() * Math.max(0, linked.getRatio() - 1);
        }
        return total;
    }

    /**
     * Takes one bond off the books. The caller has already paid for it.
     *
     * Deliberately dumb: no cash, no pricing, no policy. Game.repurchaseDebt()
     * owns the decision and the money, and this owns the list - which keeps the
     * one irreversible step (a debt disappearing) in the class that is allowed
     * to change the list, and keeps DebtManager from needing to know what the
     * city can afford.
     *
     * @return true if that bond was on the books and is not any more
     */
    public boolean retire(Debt debt) {
        return debts.remove(debt);
    }

    /** What every outstanding bond would cost to buy back today: marketValue() of each, every payment at its own month's factor on the curve. */
    public double getTotalMarketValue() {
        double total = 0;
        for (Debt debt : debts) {
            total += marketValue(debt);
        }
        return total;
    }

    /**
     * What one measure adds to the rate: a linear ramp, then flat.
     *
     * PIECEWISE, AND THAT IS THE POINT. Below FULL_STRESS_MULTIPLE years of
     * whatever is being measured against, each extra dollar of debt costs the
     * same small amount of rate. Above it, the measure has said everything it
     * has to say and stops. A single unbounded term is what let one bad
     * denominator - a city whose GDP had not caught up yet, say - drag the whole
     * quote to the ceiling on its own.
     *
     * @param annualCapacity a year of GDP, or a year of tax revenue
     */
    private double spreadFor(double debt, double annualCapacity) {

        if (debt <= 0) return 0;

        // Nothing to measure against is the worst case, not a free pass: a
        // borrower whose capacity to repay cannot be established pays the full
        // ten points for that measure.
        if (annualCapacity <= 0) return MAX_SPREAD_PER_MEASURE;

        double yearsOfIt = debt / annualCapacity;
        return MAX_SPREAD_PER_MEASURE * Math.min(1.0, yearsOfIt / FULL_STRESS_MULTIPLE);
    }

    /**
     * The curve itself: a floor, plus up to ten points from each measure.
     *
     * Either one alone is a bad measure. GDP flatters a city that cannot tax
     * what it produces, and revenue alone would let a city with a tiny economy
     * borrow freely by taxing it to death. Between them they say "how much of
     * this can you carry", and because each is capped separately, being poor on
     * one measure and sound on the other is priced as exactly that - half of the
     * worst case, not the whole of it.
     *
     * The market always lends. There is a price at which it will do anything.
     *
     * @return the city's own credit spread on the floor. Until 0.7.7 it also
     *         carried the bank's strain premium, and that had to be added HERE
     *         rather than at the one call site that sets the standing rate: it
     *         was, for about ten minutes, and quoteRate() - which prices a NEW
     *         loan by iterating priceAt() against the face it would create -
     *         then had the premium in the repurchase price and not in the
     *         issue price. Free money, $567,131 over eight round trips, caught
     *         by RestructureCheck's "can the city print money with this?"
     *         section. Whatever a price of the city's paper is made of goes in
     *         here, for that reason.
     */
    private double priceAt(double debt) {
        return YieldCurve.shortRate(floorRate(), spreadFor(debt, GDP * 12),
                spreadFor(debt, monthlyTaxRevenue * 12), ceilingRate());
    }

    /* =======================================================================
       THE CURVE (0.7.1)
       =======================================================================

       Jerus: "i think that the short term rates, aka the one you choose,
       those should be basically the tbill rate, the others change just as in
       real life."

       Until 0.7.1 a thirty-year bond and a six-month note were priced at the
       same rate - one number for every maturity, which no market has ever
       done. A lender who ties money up for thirty years wants paying for the
       thirty years: inflation might return, rates might rise, the city might
       change. So:

           rate(months) = policy + termPremium(months) - compression(months)
                          + credit spread (+ the bank's premium, until 0.7.7)

       (the first term, the dial, is the dial EXPECTED over those months since
       CD1; at no months, or with the dial where the rule will have it, the
       two are one)

       THE SHORT END IS THE DIAL. A bill (1-12 months since CD2; a note of 3-12
       before) carries no premium: it
       is the T-bill rate, Jerus's "the one you choose", and getRate() - the
       standing "city's rate" the screens read - stays
       exactly that. The premium is a table of five points, at 10, 20, 30, 40
       and 50 years (the five term maturities until CD2's six), linear between them, linear from nothing at a year to the
       ten-year point (so a serial bond's final maturity prices on the same
       curve), and flat past fifty years.

       THE CREDIT SPREAD IS THE SAME AT EVERY MATURITY, so a city that
       over-borrows pays more at every point of it. (Until CD1 so was the
       dial: a hike moved the whole curve up in parallel. It is the dial the
       market EXPECTS now - see CD1, below.) THE COMPRESSION is what the central bank's holdings
       buy (CentralBank, THE HOLDINGS DIAL): the premium times the share of
       the city's term paper it holds, over CentralBank.FULL_COMPRESSION_SHARE
       - half, the most it could hold until 0.7.15. It never touches the short
       end, which has no premium to compress, and at half the paper and past
       it the premium is gone.

       CD1 (docs/studies/spec-city-debt.md, section 3): THE CURVE IS BUILT
       FROM THE DIAL THE MARKET EXPECTS, not the dial as it stands. The dial is
       expected to drift back to the rule over YieldCurve.DRIFT_MONTHS, the
       rule run on the inflation the city expects to settle at
       (Expectations); each month's forward is that expected dial plus the
       credit spreads, the zero rate compounds the forwards, and the term
       premium less the compression goes on the zero rate (YieldCurve, curve()).
       A city whose dial is held where the rule would not put it prices its
       long money where the rule will, and a hiking city's curve inverts. The
       short end is the dial still. Every payment is discounted at its own
       month's factor (marketValue()), and a piece's yield is a result
       (yieldOf()). The month strikes the expectations once, at its top
       (strikeExpectations()).

       EVERY PRICE OF CITY PAPER GOES THROUGH curveRate() or quoteRate() at a
       maturity: the three quotes hand their duration in, a buyback and the
       market value of a bond price at the months it has LEFT, the central
       bank buys at it, the households sell back at it. Issue and buyback off
       one function is what keeps RestructureCheck's round trip neutral by
       construction.
       ======================================================================= */

    /** The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. */
    public static final double TERM_PREMIUM_10Y = .0050;

    /** ...on twenty-year money. */
    public static final double TERM_PREMIUM_20Y = .0090;

    /** ...on thirty-year money. */
    public static final double TERM_PREMIUM_30Y = .0115;

    /** ...on forty-year money. */
    public static final double TERM_PREMIUM_40Y = .0135;

    /** ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. */
    public static final double TERM_PREMIUM_50Y = .0150;

    /** The table, at 10, 20, 30, 40 and 50 years: the curve's points (40 is no longer a term a bond is sold at, CD2, but it still shapes the line between 30 and 50). */
    private static final double[] TERM_PREMIUM = {
            TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TERM_PREMIUM_50Y };

    /**
     * What a lender adds for tying money up this many months, before the
     * central bank compresses any of it.
     *
     * Nothing for a year or less - the note, the T-bill. Linear from nothing
     * at twelve months to TERM_PREMIUM_10Y at ten years, linear between the
     * table's points, and TERM_PREMIUM_50Y from fifty years on.
     */
    public static double termPremium(int months) {
        if (months <= 12) return 0;
        if (months <= 120) return TERM_PREMIUM_10Y * (months - 12) / (120.0 - 12);
        if (months >= 600) return TERM_PREMIUM_50Y;
        int below = months / 120 - 1;                   // 0 at 10y .. 3 at 40y
        double at = (months - (below + 1) * 120) / 120.0;
        return TERM_PREMIUM[below] + (TERM_PREMIUM[below + 1] - TERM_PREMIUM[below]) * at;
    }

    /**
     * What the central bank's holdings take off the premium at this many
     * months: the premium, times the share of the city's term paper it
     * actually holds (not its target) over CentralBank.FULL_COMPRESSION_SHARE,
     * times CentralBank.QE_COMPRESSION. Nothing at the short end, which has no
     * premium; the whole premium at half the term paper and past it.
     *
     * OVER FULL_COMPRESSION_SHARE, NOT THE DIAL'S TOP, SINCE 0.7.15. The two
     * were one constant, MAX_QE_SHARE, at a half, until Jerus let the dial
     * reach the whole of the paper; the compression keeps the half it always
     * had, so every holding up to half prices exactly as it did, and holding
     * more takes nothing more off the long end - the premium is gone already.
     */
    public double compression(int months) {
        double premium = termPremium(months);
        if (premium <= 0) return 0;
        double held = centralBankShareOfTerm();
        if (held <= 0) return 0;
        return premium * Math.min(1, held / CentralBank.FULL_COMPRESSION_SHARE) * CentralBank.QE_COMPRESSION;
    }

    /**
     * ...at a share of the term paper of the caller's (0.7.36): what holding
     * `share` would take off the premium at this many months, compression()'s
     * rule with the share given - the Policy tab's holdings dial, whose
     * thirty-year rate "once the book has moved" is the premium less this at
     * the dial's target. Pure.
     */
    public double compressionAt(double share, int months) {
        double premium = termPremium(months);
        if (premium <= 0 || share <= 0) return 0;
        return premium * Math.min(1, share / CentralBank.FULL_COMPRESSION_SHARE) * CentralBank.QE_COMPRESSION;
    }

    /**
     * The curve at a maturity with the central bank holding `share` of the
     * term paper (0.7.36): curveRate()'s credit judgement and premium, less
     * compressionAt() that share - the thirty-year rate "once the book has
     * moved" to a holdings dial's target, on the Policy tab. Pure.
     */
    public double curveRateAtShare(int months, double share) {
        return curve().baseZero(months) + termShapeAt(share, months);
    }

    /** The premium less the compression at a central-bank share of the term paper: the curve's shape over the short end. Nothing up to a year. */
    private double termShapeAt(double share, int months) {
        if (months <= 12) return 0;
        return termPremium(months) - compressionAt(share, months);
    }

    /* ----- the expectations the curve is built from (CD1) ----- */

    /** The inflation the rule read when the month's expectations were struck (strikeExpectations()); NaN until something strikes them. */
    private double struckInflation = Double.NaN;

    /** ...and the inflation the city expected then (Expectations.getExpectedInflation()); NaN until struck. */
    private double struckExpected = Double.NaN;

    /**
     * Strikes the two figures the curve's expected dial is built from: the
     * inflation the rule reads (this month's) and the inflation the city
     * expects (where it settles). The month does it once, at its top, straight
     * after the autopilot has set the dial (Game.nextMonth()); the pair is
     * held for the month, whatever happens to the prices inside it, and saved
     * (marketToSave()), because the expectation the month began with cannot be
     * re-derived from the state it ended in. A manager nobody has struck - a
     * fixture's bare one - prices a flat path at the floor: the parallel curve
     * of the pricing before CD1. A figure that is not a number leaves it
     * unstruck.
     */
    public void strikeExpectations(double inflationNow, double expectedInflation) {
        struckInflation = inflationNow;
        struckExpected = expectedInflation;
    }

    /** True once the curve's expectations have been struck and are numbers. */
    public boolean isStruck() {
        return !Double.isNaN(struckInflation) && !Double.isNaN(struckExpected);
    }

    /** The inflation the rule read when the month struck the curve's expectations, a fraction a year; NaN unstruck. */
    public double getStruckInflation() { return struckInflation; }

    /** ...and the inflation the city expected then; NaN unstruck. */
    public double getStruckExpected() { return struckExpected; }

    /**
     * WHAT THE RULE WOULD SET THE DIAL TO AT TODAY'S INFLATION (CD6): the rule
     * the market expects the dial to close on (YieldCurve.expectedPath()'s
     * R(0)), advisedPolicyRate() at the inflation the month struck; the dial
     * itself when nothing is struck, as the path is then flat at it. It is not
     * the path's first month - that is the dial, and the gap between the two
     * closes from there (expectedPath()) - except with the dial on the rule,
     * when it is both.
     */
    public double ruleNow() {
        return isStruck() ? advisedPolicyRate(struckInflation) : getPolicyRate();
    }

    /**
     * ...and where the market expects the dial to settle (CD6): the rule at
     * the inflation the city expects it to settle at, which the path
     * approaches with the gap closed (expectedPath()'s limit); the dial when
     * nothing is struck.
     */
    public double settledDial() {
        return isStruck() ? advisedPolicyRate(struckExpected) : getPolicyRate();
    }

    /**
     * The expected dial's path and the curve built on it are CACHED, because
     * marketValue() runs for every piece several times a month and quoteRate()
     * walks a fixed point that builds a curve in each pass. Each cache is one
     * immutable object behind one reference, so a screen reading while the
     * month runs sees a path and a curve that belong together; a key that moves
     * rebuilds. Keys are compared by their bits, so a NaN key (unstruck)
     * matches itself.
     */
    private static final class PathEntry {
        final double floor, inflation, expected, target;
        final Strictness strictness;
        final double[] path;
        PathEntry(double floor, double inflation, double expected, double target, Strictness strictness, double[] path) {
            this.floor = floor; this.inflation = inflation; this.expected = expected;
            this.target = target; this.strictness = strictness; this.path = path;
        }
    }

    private static final class CurveEntry {
        final double[] path;
        final double gdpSpread, revenueSpread, ceiling, share;
        final YieldCurve curve;
        CurveEntry(double[] path, double gdpSpread, double revenueSpread, double ceiling, double share, YieldCurve curve) {
            this.path = path; this.gdpSpread = gdpSpread; this.revenueSpread = revenueSpread;
            this.ceiling = ceiling; this.share = share; this.curve = curve;
        }
    }

    private transient volatile PathEntry pathCache;
    private transient volatile CurveEntry curveCache;

    private static boolean same(double a, double b) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    /** The dial the market expects from this floor: the rule's path from the struck expectations, or the floor held flat when nothing is struck. */
    private double[] pathFor(double floor) {
        double inflation = struckInflation, expected = struckExpected;
        boolean struck = !Double.isNaN(inflation) && !Double.isNaN(expected);
        if (!struck) inflation = expected = Double.NaN;
        double target = inflationTarget;
        Strictness strict = strictness;
        PathEntry e = pathCache;
        if (e != null && same(e.floor, floor) && same(e.inflation, inflation) && same(e.expected, expected)
                && same(e.target, target) && e.strictness == strict) {
            return e.path;
        }
        final double now = inflation, settle = expected;
        double[] path = struck
                ? YieldCurve.expectedPath(floor, x -> advisedPolicyRate(x), now, settle)
                : YieldCurve.flatPath(floor);
        pathCache = new PathEntry(floor, inflation, expected, target, strict, path);
        return path;
    }

    /**
     * The curve this debt would be priced on: the expected dial plus the
     * credit judgement at this debt (spreadFor() on a year of output and a
     * year of tax, clamped at the ceiling), the shape the term premium and the
     * central bank's holdings give the long end, built once and kept while its
     * inputs stand (CD1; YieldCurve says how).
     */
    private YieldCurve curveAt(double debt) {
        double floor = floorRate();
        double[] path = pathFor(floor);
        double gdpSpread = spreadFor(debt, GDP * 12);
        double revenueSpread = spreadFor(debt, monthlyTaxRevenue * 12);
        double ceiling = ceilingRate();
        final double share = centralBankShareOfTerm();
        CurveEntry c = curveCache;
        if (c != null && c.path == path && same(c.gdpSpread, gdpSpread) && same(c.revenueSpread, revenueSpread)
                && same(c.ceiling, ceiling) && same(c.share, share)) {
            return c.curve;
        }
        YieldCurve made = new YieldCurve(path, gdpSpread, revenueSpread, ceiling, m -> termShapeAt(share, m));
        curveCache = new CurveEntry(path, gdpSpread, revenueSpread, ceiling, share, made);
        return made;
    }

    /** THE CURVE (CD1): the city's own, on what it owes now - a discount factor for every month to fifty years. See YieldCurve. */
    public YieldCurve curve() {
        return curveAt(getPricedDebt());
    }

    /**
     * THE CURVE: what the city's paper of this many months is worth to a
     * lender today - the zero rate at that maturity, on what the city owes
     * now: the compounded forward rates of the dial the market expects, plus
     * the shape. At no months it is getRate(), to the bit.
     */
    public double curveRate(int months) {
        return curve().zero(months);
    }

    /**
     * What this paper would fetch today: the present value of what it still
     * owes, EACH PAYMENT AT ITS OWN MONTH'S FACTOR (CD1) - the city's curve()
     * for its own paper, the world's (worldCurveAt()) for a dollar bond since
     * 0.7.2, which was valued at the city's short rate until then, so the dial
     * moved it. Until CD1 the whole schedule was discounted at ONE rate, the
     * curve read at the months the piece had left, so the yield it showed was
     * the rate it had been handed; now the yield is a result (yieldOf()).
     * Nothing left to pay (no months) costs what is owed, as Debt does.
     */
    public double marketValue(Debt paper) {
        if (paper == null) return 0;
        if (paper.getRemainingMonths() <= 0) return paper.getOustandingPrincipal();
        // Its own schedule - except a floater's and a linker's, which the market prices on the dial and
        // the index it expects (CD5, pricingFlows()).
        double[] flows = pricingFlows(paper);
        if (paper.isForeign()) return worldCurveAt(foreignRate()).pv(flows);
        YieldCurve curve = curve();
        if (paper instanceof LongTermBond bond && bond.isCallable()) return callableValue(bond, curve, flows);
        return curve.pv(flows);
    }

    /**
     * A callable bond's value (CD4): what the tree prices its remaining
     * payments at on today's curve, with the city's right to call it at its
     * principal from the first call month on (LongTermBond.monthsToFirstCall()),
     * at the volatility of the city's own rate (rateVolatility()). Never more
     * than a non-call bond with the same payments is worth, and never more than
     * the principal once it can be called.
     */
    private double callableValue(LongTermBond bond, YieldCurve curve, double[] flows) {
        CallTree tree = treeOn(curve, rateVolatility(), flows.length);
        return tree.price(flows, bond.monthsToFirstCall(), bond.getOustandingPrincipal());
    }

    private static final class TreeEntry {
        final YieldCurve curve;
        final double sigma;
        final CallTree tree;
        TreeEntry(YieldCurve curve, double sigma, CallTree tree) { this.curve = curve; this.sigma = sigma; this.tree = tree; }
    }

    private transient volatile TreeEntry treeCache;

    /**
     * The tree fitted to this curve at this volatility, long enough for a
     * schedule of this many months: built once and kept while the curve (one
     * object while its inputs stand, curveAt()) and the volatility stand, and
     * refitted longer when a longer schedule is asked for. A callable piece is
     * valued several times a month, and a fit is the dear part.
     */
    private CallTree treeOn(YieldCurve curve, double sigma, int months) {
        TreeEntry e = treeCache;
        if (e != null && e.curve == curve && same(e.sigma, sigma) && e.tree.steps() >= months) return e.tree;
        CallTree made = CallTree.fit(curve, sigma, months);
        treeCache = new TreeEntry(curve, sigma, made);
        return made;
    }

    /**
     * The volatility of the city's short rate, a year, that a callable piece
     * is valued at: Game's measure of its own history (Game.rateVolatility(),
     * handed in with setRateVolatility()), the floor (CallTree.RATE_VOL_FLOOR)
     * for a manager nobody has handed one.
     */
    public double rateVolatility() {
        java.util.function.DoubleSupplier source = rateVolatility;
        return source == null ? CallTree.RATE_VOL_FLOOR : source.getAsDouble();
    }

    /** Hands the manager the city's volatility, so it can value a callable piece without knowing the city's history. Not saved: Game hands it in when it builds the manager. */
    public void setRateVolatility(java.util.function.DoubleSupplier source) {
        this.rateVolatility = source;
        this.treeCache = null;
    }

    private transient java.util.function.DoubleSupplier rateVolatility;

    /* ----- the price index, and the paths a floater and a linker are priced on (CD5) ----- */

    /**
     * The city's price index now (PriceIndex.getIndex()), as a linked bond is indexed on it: what
     * Game hands in with setPriceIndexSource(), read afresh each time so a reloaded index is
     * the one read. NaN for a manager nobody has handed one - and then nothing is indexed.
     */
    public double priceIndexLevel() {
        java.util.function.DoubleSupplier source = priceIndexSource;
        return source == null ? Double.NaN : source.getAsDouble();
    }

    /** Hands the manager the price index, so it can index a linked bond without knowing the city's prices. Not saved: Game hands it in when it builds the manager; a fixture hands its own, to cause a deflation. */
    public void setPriceIndexSource(java.util.function.DoubleSupplier source) {
        this.priceIndexSource = source;
    }

    private transient java.util.function.DoubleSupplier priceIndexSource;

    private transient volatile PathEntry policyPathCache;

    /**
     * THE DIAL THE MARKET EXPECTS FROM THE POLICY RATE, t = 0 the dial now (CD5): what a
     * floating-rate note's coupons are priced on - the same expected path the curve is built on
     * (YieldCurve.expectedPath()), started from the dial and not from the floor. The rule's path
     * from the struck expectations; the dial held flat when nothing is struck. Cached as
     * pathFor() caches, and not to be changed.
     */
    public double[] expectedPolicyPath() {
        double dial = getPolicyRate();
        double inflation = struckInflation, expected = struckExpected;
        boolean struck = !Double.isNaN(inflation) && !Double.isNaN(expected);
        if (!struck) inflation = expected = Double.NaN;
        double target = inflationTarget;
        Strictness strict = strictness;
        PathEntry e = policyPathCache;
        if (e != null && same(e.floor, dial) && same(e.inflation, inflation) && same(e.expected, expected)
                && same(e.target, target) && e.strictness == strict) {
            return e.path;
        }
        final double now = inflation, settle = expected;
        double[] path = struck
                ? YieldCurve.expectedPath(dial, x -> advisedPolicyRate(x), now, settle)
                : YieldCurve.flatPath(dial);
        policyPathCache = new PathEntry(dial, inflation, expected, target, strict, path);
        return path;
    }

    private static final class GrowthEntry {
        final double inflation, expected, target;
        final double[] growth;
        GrowthEntry(double inflation, double expected, double target, double[] growth) {
            this.inflation = inflation; this.expected = expected; this.target = target; this.growth = growth;
        }
    }

    private transient volatile GrowthEntry growthCache;

    /**
     * THE PRICE INDEX THE MARKET EXPECTS, month by month (CD5): entry j is the growth of the index
     * in the month j months ahead, (1 + pi(j))^(1/12), with pi(j) = expected + (now - expected) x
     * e^(-j / Expectations.ADAPT_MONTHS) from the struck pair - the same path YieldCurve
     * .expectedPath() expects inflation along - and the inflation target held flat when nothing is
     * struck. What a linked bond's ratio is projected on (InflationLinkedBond.projectedFlows()).
     * Cached; not to be changed.
     */
    public double[] expectedInflationGrowth() {
        double inflation = struckInflation, expected = struckExpected;
        boolean struck = !Double.isNaN(inflation) && !Double.isNaN(expected);
        if (!struck) inflation = expected = Double.NaN;
        double target = inflationTarget;
        GrowthEntry e = growthCache;
        if (e != null && same(e.inflation, inflation) && same(e.expected, expected) && same(e.target, target)) {
            return e.growth;
        }
        double[] growth = new double[YieldCurve.HORIZON];
        for (int j = 0; j < growth.length; j++) {
            double pi = struck
                    ? expected + (inflation - expected) * Math.exp(-(double) j / Expectations.ADAPT_MONTHS)
                    : target;
            growth[j] = Math.pow(1 + pi, 1.0 / 12);
        }
        growthCache = new GrowthEntry(inflation, expected, target, growth);
        return growth;
    }

    /**
     * The payments the market prices a piece on (CD5): a floating-rate note's coupons at the dial
     * it expects (expectedPolicyPath()), a linked bond's on the index it expects
     * (expectedInflationGrowth(), the next payment on the index now over the base); every other
     * piece's own remainingCashFlows(), the same array as before.
     */
    public double[] pricingFlows(Debt paper) {
        if (paper instanceof FloatingRateNote note) return note.projectedFlows(expectedPolicyPath());
        if (paper instanceof InflationLinkedBond linked) {
            return linked.projectedFlows(linked.ratioAt(priceIndexLevel()), expectedInflationGrowth());
        }
        return paper.remainingCashFlows();
    }

    /**
     * The real yield of a linked bond at its market value (CD5): the known schedule - the real
     * coupon on the principal as the index stands now, held flat, and the indexed principal at
     * the end - solved for the yield that makes it worth what the market pays. The schedule and
     * the price both scale with the ratio, so the figure is the same on the indexed principal as
     * on the face: a yield with the inflation taken out. For the screen; yieldOf() is the
     * nominal one.
     */
    public double realYieldOf(InflationLinkedBond paper) {
        if (paper == null || paper.getRemainingMonths() <= 0) return 0;
        return Debt.effectiveRateOf(paper.projectedFlows(paper.ratioAt(priceIndexLevel()), null), marketValue(paper));
    }

    /* ----- the call (CD4, spec-city-debt.md section 6.4) ----- */

    /**
     * The present value a call would save, per the study: the line's remaining
     * payments at this curve, as a bond that cannot be called, less what the
     * call pays (its principal) and less what selling the paper again costs
     * (the auction's fee, the caller's). Below nothing when the line is worth
     * less than it costs to call.
     */
    public static double callSavingOn(YieldCurve curve, Debt paper, double fee) {
        if (paper == null) return 0;
        return curve.pv(paper.remainingCashFlows()) - paper.getOustandingPrincipal() * CallTree.CALL_PRICE / 100 - fee;
    }

    /** callSavingOn() the city's curve. */
    public double callSaving(Debt paper, double fee) {
        return callSavingOn(curve(), paper, fee);
    }

    /** True when a saving passes CallTree.WORTH_CALLING of the principal called (D24). */
    public static boolean worthCalling(double saving, double principal) {
        return saving > CallTree.WORTH_CALLING * principal;
    }

    private static final class WorldEntry {
        final double rate;
        final YieldCurve curve;
        WorldEntry(double rate, YieldCurve curve) { this.rate = rate; this.curve = curve; }
    }

    private transient volatile WorldEntry worldCache;

    /**
     * THE WORLD'S CURVE as a YieldCurve (CD1): the world's short rate held
     * flat, and the term premium table (termPremium()) on top - the one
     * definition of what a dollar payment this many months away is
     * discounted by, for the issue (Game.quoteForeign(), which walks the
     * short rate to a fixed point) and for the buyback (marketValue(), at
     * foreignRate()) alike. The world's dial does not move, so the path is
     * flat; nobody abroad holds the city's paper, so nothing is compressed;
     * the credit judgement is already in the short rate (countryPremium()),
     * so no spread is added and no ceiling applies. Built once per rate and
     * kept, one immutable object behind one reference, as curveAt()'s is.
     */
    public YieldCurve worldCurveAt(double shortRate) {
        WorldEntry e = worldCache;
        if (e != null && same(e.rate, shortRate)) return e.curve;
        YieldCurve made = new YieldCurve(YieldCurve.flatPath(shortRate), 0, 0, Double.MAX_VALUE, m -> termPremium(m));
        worldCache = new WorldEntry(shortRate, made);
        return made;
    }

    /** What a dollar payment this many months away is discounted by: (1 + foreignCurveRate(months)/12)^(-months), on worldCurveAt(foreignRate()). */
    public double foreignFactor(int months) {
        return worldCurveAt(foreignRate()).factor(months);
    }

    /** The yield to maturity of a piece on the price a buyback pays: a RESULT of marketValue(), not the rate it was priced at (CD1). */
    public double yieldOf(Debt paper) {
        // A floater's and a linker's yield is the IRR of the payments the market expects at the price it
        // pays (CD5, pricingFlows()): the nominal yield the households weigh against deposits. Every
        // other piece keeps its yield to maturity on its own schedule.
        if (paper instanceof FloatingRateNote || paper instanceof InflationLinkedBond) {
            return Debt.effectiveRateOf(pricingFlows(paper), marketValue(paper));
        }
        return paper.getYieldToMaturity(marketValue(paper));
    }

    /** Where a piece trades against par, as a percentage of what is still owed on it: marketValue() over its outstanding principal x 100; 0 when nothing is owed. */
    public double priceOfPar(Debt paper) {
        double owed = paper == null ? 0 : paper.getOustandingPrincipal();
        return owed > 0 ? marketValue(paper) / owed * 100 : 0;
    }

    /** A point on the curve (CD6): the rise in every rate that valueOfAPoint() prices a piece at, 1% a year. */
    public static final double A_POINT = 0.01;

    /**
     * WHAT A POINT MOVES (CD6): what the piece's worth falls by if every rate on its curve (the world's
     * for dollar paper, as marketValue() uses) rises a point, A_POINT - its pricing flows (pricingFlows())
     * worth at the curve less the same flows at the curve and a point, YieldCurve.pvAtSpread(). A pure read.
     *
     * A callable line's is on its schedule as a bond that cannot be called: the right to call is not
     * repriced. A floater's coupons would rise with the dial, so every coupon after the one fixed for
     * the month ahead is raised the point too, and the sensitivity left is to its next reset only - about
     * its worth a point and a month; a linker's coupons follow the index, which a rate does not move, so
     * it is priced on the nominal curve like a bond. Nothing left to pay is not at risk: 0.
     */
    public double valueOfAPoint(Debt paper) {
        if (paper == null || paper.getRemainingMonths() <= 0) return 0;
        double[] flows = pricingFlows(paper);
        YieldCurve curve = paper.isForeign() ? worldCurveAt(foreignRate()) : curve();
        double[] shifted = flows;
        if (paper instanceof FloatingRateNote note) {
            // The dial a point up lifts the coupon of every month after the next, which was fixed at the last reset.
            shifted = flows.clone();
            for (int i = 1; i < shifted.length; i++) shifted[i] += note.getFaceValue() * A_POINT / 12;
        }
        return curve.pv(flows) - curve.pvAtSpread(shifted, A_POINT);
    }

    /**
     * ...and the piece's duration, in years (CD6): valueOfAPoint() over its worth and the point,
     * V / (marketValue x A_POINT) - the finite difference, the one definition. A floater's is its
     * months to its next reset, a bond's under its life.
     */
    public double durationOf(Debt paper) {
        double worth = marketValue(paper);
        return worth > 0 ? valueOfAPoint(paper) / (worth * A_POINT) : 0;
    }

    /* ----- what the month's close keeps for the debt screens (CD6, spec-city-debt.md section 7) -----

       Two histories the state a month ended in cannot give back: each piece's price against its carrying
       value since its sale (Debt.recordPath()), and the curve as it stood in the months behind. Both are
       RECORDED at the close of the month (Game.recordMonth()) by reads alone - marketValue(),
       priceOfPar(), curveRate() and curve() are pure, and nothing here calls what strikes the market
       (Game.fairValueOf(), quoteBuyback(), priceTheDebtMarket(), updateInterest()) - so the month's
       money, rate and debt are what they were without them. The pieces carry their own path through
       Gson; the curve book is saved beside the interest by kind (curveBookToSave()). */

    /** The maturities, in months, the curve is drawn and kept at: three months to fifty years. */
    public static final int[] CURVE_TENORS = {3, 6, 12, 24, 60, 120, 240, 360, 480, 600};

    /** The months of the curve kept: today, a year ago and two years ago. */
    public static final int CURVE_KEPT = 25;

    /** A curve-book row: the month, the dial, the city's zero rate at each of CURVE_TENORS, the world's at each. */
    private static final int CURVE_ROW = 2 + 2 * CURVE_TENORS.length;

    private final java.util.ArrayList<double[]> curveBook = new java.util.ArrayList<>();

    /** True: the month's close records the book. A harness turns it off for a twin city, to hold that recording moves nothing the month computes (DeskCheck). */
    private boolean recording = true;

    void setRecordingForTest(boolean on) { this.recording = on; }

    /**
     * Records the month's close: every piece's price against par and its carrying value per 100 of what is
     * owed (Debt.recordPath()), then the curve (the zero rates at CURVE_TENORS, the dial and the world's
     * rates) in the curve book, the newest CURVE_KEPT months kept. Pure reads, writing only these records.
     */
    public void recordTheBook(int month) {
        if (!recording) return;
        for (Debt d : debts) {
            double owed = d.getOustandingPrincipal();
            if (!(owed > 0)) continue;
            d.recordPath(month, priceOfPar(d), d.getCarryingValue() / owed * 100);
        }
        recordCurve(month);
    }

    private void recordCurve(int month) {
        double[] row = new double[CURVE_ROW];
        row[0] = month;
        row[1] = getPolicyRate();
        YieldCurve curve = curve();
        // foreignCurveRate(m) is the world's short rate plus the term premium: the short rate (the country premium,
        // which reads the whole dollar book) struck once for the ten tenors, the same figure to the bit.
        double world = foreignRate();
        for (int i = 0; i < CURVE_TENORS.length; i++) {
            row[2 + i] = curve.zero(CURVE_TENORS[i]);
            row[2 + CURVE_TENORS.length + i] = world + termPremium(CURVE_TENORS[i]);
        }
        // A month already recorded is recorded again (the close of a month is once, a harness may run it twice).
        if (!curveBook.isEmpty() && curveBook.get(curveBook.size() - 1)[0] == month) curveBook.remove(curveBook.size() - 1);
        curveBook.add(row);
        while (curveBook.size() > CURVE_KEPT) curveBook.remove(0);
    }

    /** The row recorded this many months before the newest, or null when none was: the book is read by the month, not by place. */
    private double[] curveRowAgo(int months) {
        if (months < 0 || curveBook.isEmpty()) return null;
        double want = curveBook.get(curveBook.size() - 1)[0] - months;
        for (double[] row : curveBook) if (row[0] == want) return row;
        return null;
    }

    /** The city's zero rates at CURVE_TENORS as the curve stood this many months before the newest month recorded (0 the newest); null when that month was not kept - none recorded, or more than CURVE_KEPT - 1 months back. */
    public double[] curveMonthsAgo(int months) {
        double[] row = curveRowAgo(months);
        return row == null ? null : java.util.Arrays.copyOfRange(row, 2, 2 + CURVE_TENORS.length);
    }

    /** ...and the world's, at the same tenors (foreignCurveRate()); null likewise. */
    public double[] worldCurveMonthsAgo(int months) {
        double[] row = curveRowAgo(months);
        return row == null ? null : java.util.Arrays.copyOfRange(row, 2 + CURVE_TENORS.length, CURVE_ROW);
    }

    /** ...and the dial that month; NaN when that month was not kept. */
    public double policyRateMonthsAgo(int months) {
        double[] row = curveRowAgo(months);
        return row == null ? Double.NaN : row[1];
    }

    /** How many months the curve book holds. */
    public int curveBookSize() { return curveBook.size(); }

    /** The curve book as a save carries it: a copy, one row a month, oldest first - [month, dial, the city's rates, the world's]. */
    double[][] curveBookToSave() {
        double[][] rows = new double[curveBook.size()][];
        for (int i = 0; i < rows.length; i++) rows[i] = curveBook.get(i).clone();
        return rows;
    }

    /** ...and put back by a load; null (an older save) or a row of the wrong length (a changed tenor list) is not kept, and the book is built afresh from the next close. */
    void restoreCurveBook(double[][] saved) {
        curveBook.clear();
        if (saved == null) return;
        for (double[] row : saved) {
            if (row == null || row.length != CURVE_ROW) {
                curveBook.clear();
                return;
            }
            curveBook.add(row.clone());
        }
        while (curveBook.size() > CURVE_KEPT) curveBook.remove(0);
    }

    /** True for the city's own term paper - serial and term, not the notes: what the central bank's dial holds. */
    public static boolean isTermPaper(Debt paper) {
        return paper != null && !paper.isForeign() && !(paper instanceof ShortTermTBill);
    }

    /** The city's own term paper outstanding, at face: the base of the holdings dial. */
    public double termPrincipal() {
        double total = 0;
        for (Debt d : debts) if (isTermPaper(d)) total += d.getOustandingPrincipal();
        return total;
    }

    /** The share of it the central bank holds - the compression's measure. Zero with none outstanding. */
    public double centralBankShareOfTerm() {
        double term = 0, held = 0;
        for (Debt d : debts) {
            if (!isTermPaper(d)) continue;
            term += d.getOustandingPrincipal();
            held += d.centralBankPrincipal();
        }
        return term > 0 ? held / term : 0;
    }

    /**
     * The central bank's share of ALL the city's own paper outstanding, at
     * face, every maturity - the notes it never buys in the denominator - the
     * floor's measure (0.7.15, floorRate()). Not the term share, because the
     * floor prices every maturity: a note is the bank's paper and priced at
     * its floor, so a city that funds itself on notes has its floor set by
     * the bank however much of its term paper the central bank holds. The
     * dollar paper is not in it; it is priced on the world's curve. Zero with
     * none outstanding.
     */
    public double centralBankShareOfPaper() {
        double paper = 0, held = 0;
        for (Debt d : debts) {
            if (d.isForeign()) continue;
            paper += d.getOustandingPrincipal();
            held += d.centralBankPrincipal();
        }
        return paper > 0 ? held / paper : 0;
    }

    /* ---------------------- who holds it (0.7.1) ---------------------- */

    /** What the city's households hold of its own paper, at face. The sum of every cell's paper; HoldersCheck asserts it. */
    public double householdPrincipal() {
        double total = 0;
        for (Debt d : debts) total += d.householdPrincipal();
        return total;
    }

    /** ...the central bank, at face. CentralBank.getPaperHeld() is the same figure from the other side. */
    public double centralBankPrincipal() {
        double total = 0;
        for (Debt d : debts) total += d.centralBankPrincipal();
        return total;
    }

    /** ...the businesses, at face (CD2, slice 4). OutwardInvestment.totalPaper() is the same figure from the other side; HoldersCheck asserts it. */
    public double firmPrincipal() {
        double total = 0;
        for (Debt d : debts) total += d.firmPrincipal();
        return total;
    }

    /** ...the world, at face (CD2, slice 5). CapitalFlows.getPaper() is the same figure from the other side; HoldersCheck asserts it. */
    public double worldPrincipal() {
        double total = 0;
        for (Debt d : debts) total += d.worldPrincipal();
        return total;
    }

    /** ...and the commercial bank: the domestic principal less the other four. What its book carries. */
    public double bankPrincipal() {
        double total = 0;
        for (Debt d : debts) total += d.bankPrincipal();
        return total;
    }

    /**
     * ...of which the paper the bank has PAID for: what its book carries. Paper
     * sold between two presses is on nobody's book until the settle - the
     * treasury has the cash, and what the buyers owe is carried against the
     * bank's pool in MoneyAudit - so a city reloaded between the issue and
     * the settle carries the same book as the one that was saved.
     */
    public double bankBook() {
        double total = 0;
        for (Debt d : debts) total += d.bankPrincipalPaid();
        return total;
    }

    /** Each holder's book at the curve: 0 the households, 1 the central bank, 2 the bank, 3 the businesses, 4 the world. */
    public double[] bookValues() {
        double[] out = new double[5];
        for (Debt d : debts) {
            if (d.isForeign()) continue;
            double face = d.getOustandingPrincipal();
            if (face <= 0) continue;
            double perFace = marketValue(d) / face;
            out[0] += d.householdPrincipal() * perFace;
            out[1] += d.centralBankPrincipal() * perFace;
            out[2] += d.bankPrincipal() * perFace;
            out[3] += d.firmPrincipal() * perFace;
            out[4] += d.worldPrincipal() * perFace;
        }
        return out;
    }

    /**
     * The households' book at market over its face: ONE RATIO A MONTH, which
     * is what their paper counts for in their net worth and what the desk
     * pays them for it. At face - 1 - when they hold none.
     */
    public double householdBookRatio() {
        double face = householdPrincipal();
        return face > 0 ? bookValues()[0] / face : 1;
    }

    /**
     * The businesses' book at market over its face (CD2, slice 4): the
     * households' twin, ONE RATIO A MONTH - what a dollar of face of their
     * paper is worth, what the desk pays them for it. At face - 1 - when they
     * hold none.
     */
    public double firmBookRatio() {
        double face = firmPrincipal();
        return face > 0 ? bookValues()[3] / face : 1;
    }

    /** What the businesses' paper yields at today's curve: the households' twin, weighted by what the businesses hold of each piece; the short rate when they hold none. */
    public double firmBookYield() {
        double face = 0, weighted = 0;
        for (Debt d : debts) {
            double held = d.firmPrincipal();
            if (held <= 0) continue;
            face += held;
            weighted += held * yieldOf(d);
        }
        return face > 0 ? weighted / face : getRate();
    }

    /**
     * The world's book at market over its face (CD2, slice 5): the households'
     * twin, one ratio a month - what a dollar of face of the paper it holds is
     * worth at the curve, which is what the auction counts of it beside its hot
     * money (Game.auctionBidders()). At face - 1 - when it holds none.
     */
    public double worldBookRatio() {
        double face = worldPrincipal();
        return face > 0 ? bookValues()[4] / face : 1;
    }

    /** What the households' paper yields at today's curve (each piece's yieldOf(), its yield to maturity on the price a buyback pays), weighted by what they hold of each piece; the short rate when they hold none. */
    public double householdBookYield() {
        double face = 0, weighted = 0;
        for (Debt d : debts) {
            double held = d.householdPrincipal();
            if (held <= 0) continue;
            face += held;
            weighted += held * yieldOf(d);
        }
        return face > 0 ? weighted / face : getRate();
    }

    /**
     * The discount the bank has not yet earned on what it holds: every piece's
     * unaccreted remainder, in the share of its principal the bank holds. The
     * bank carries it against its book (Bank.setUnearnedDiscount()), so its
     * equity does not jump at the settle and the discount reaches its income
     * only as it accretes.
     */
    public double bankUnearnedDiscount() {
        double total = 0;
        for (Debt d : debts) {
            if (d.isForeign()) continue;
            total += d.unaccretedOnPaid();   // only the part that has settled: see bankBook()
        }
        return total;
    }

    /** This month's accretion on the bank's share, struck in processAllDebts() before the month's payments; handed to the bank at the settle. */
    private double accretedForBank;

    public double getAccretedForBank() { return accretedForBank; }

    /* ===================================================================
       THE RATE, TAKEN APART - for the Finances screen and nothing else.

       priceAt() is three terms added together at the short end - the floor
       and the two spreads; four until 0.7.7, the bank's strain premium the
       fourth - and until these existed the game
       could tell a player their rate was 1.23% and not one thing about WHY, or
       which of their decisions would move it. A quoted price with no visible
       components is a price a player can only respond to by borrowing less.

       Pure reads of the same functions priceAt() uses. Nothing here is a second
       definition: change the pricing and these change with it.
       =================================================================== */

    /**
     * What the city would be quoted if the policy rate were this instead.
     *
     * The short end of the curve (priceAt(), YieldCurve.shortRate())
     * with the base rate swapped and nothing else touched, so the
     * Policies screen can show a player what moving the dial does to their own
     * borrowing before they move it. It lives here rather than in the screen
     * for the obvious reason: the day the pricing changes, this changes with
     * it, and a copy in the UI would not.
     *
     * PURE. Reads the same spreads priceAt() reads and sets nothing.
     */
    public double rateAtPolicy(double policy) {
        double floor   = floorAt(policy);
        double ceiling = policy + 2 * MAX_SPREAD_PER_MEASURE;
        return YieldCurve.shortRate(floor, gdpSpread(), revenueSpread(), ceiling);
    }

    /**
     * The floor everybody pays: the policy rate, or the bank's cost of funds
     * if that is higher - on the share of the city's paper the bank and the
     * households hold, since 0.7.15; the central bank's share is priced at
     * the policy rate (floorRate()).
     */
    public double baseComponent() { return floorRate(); }

    /** What the debt costs against the size of the economy. */
    public double gdpSpread() { return spreadFor(getPricedDebt(), GDP * 12); }

    /** ...and against what the city can actually collect. */
    public double revenueSpread() { return spreadFor(getPricedDebt(), monthlyTaxRevenue * 12); }

    /** How much of the worst case each measure has used up, 0 to 1. */
    public double gdpStress() {
        return MAX_SPREAD_PER_MEASURE > 0 ? gdpSpread() / MAX_SPREAD_PER_MEASURE : 0;
    }

    public double revenueStress() {
        return MAX_SPREAD_PER_MEASURE > 0 ? revenueSpread() / MAX_SPREAD_PER_MEASURE : 0;
    }

    /** What the city's own rate is over the world's (0.7.36): the short end against WORLD_BASE_RATE - the difference hot money follows. */
    public double overTheWorld() { return currentRate - WORLD_BASE_RATE; }

    /** ...at a policy rate of the caller's: rateAtPolicy() against the world's. Pure. */
    public double overTheWorldAt(double policy) { return rateAtPolicy(policy) - WORLD_BASE_RATE; }

    /** The most either measure can add on its own. */
    public static double maxSpreadPerMeasure() { return MAX_SPREAD_PER_MEASURE; }

    /** Years of GDP, or of revenue, at which a measure has said all it can. */
    public static double fullStressMultiple() { return FULL_STRESS_MULTIPLE; }

    /** A year of output, as the market is pricing it. */
    public double annualCapacityGdp() { return GDP * 12; }

    /** ...and a year of tax, likewise. */
    public double annualCapacityRevenue() { return monthlyTaxRevenue * 12; }

    /** True when the quoted rate is pinned at the top of the curve. */
    public boolean atCeiling() {
        return getRate() >= ceilingRate() - 1e-9;
    }

    /*
     * THE CURVE TAKEN APART (CD1). A rate at a maturity is three parts, which
     * add to curveRate(m):
     *
     *     curveRate(m) = expectedDialOver(m) + creditSpread()
     *                    + (m <= 12 ? 0 : termPremium(m) - compression(m))
     *
     * (to rounding; the screen's sum is within 1e-12 of the curve's own).
     */

    /** The dial the market expects, averaged over this many months, and nothing else in the rate: the compounded forwards less the credit spread. curveRate(m) == expectedDialOver(m) + creditSpread() + (m <= 12 ? 0 : termPremium(m) - compression(m)), to rounding (CD1). */
    public double expectedDialOver(int months) {
        return curve().expectedDialOver(months);
    }

    /** What the credit judgement adds to the forward, as the curve carries it: the two spreads, or the room under the ceiling if less - the same in every month, the middle term of curveRate(m) == expectedDialOver(m) + creditSpread() + (m <= 12 ? 0 : termPremium(m) - compression(m)) (CD1). */
    public double creditSpread() {
        return curve().effectiveSpread();
    }

    /** The dial the market expects this many months ahead (0 this month: the floor). */
    public double expectedDialIn(int monthsAhead) {
        return curve().path(monthsAhead);
    }

    /**
     * What a spotless city pays: the policy rate - since 0.7.0, see THE FLOOR
     * IS REAL NOW - but never less than the money costs the bank that lends it.
     *
     * THE DISCOUNT SURVIVED THIS, AND STOPPED BEING A SUBSIDY THE LENDER PAID
     * (2026-09-13), until 0.7.0 deleted it outright; the note is kept as the
     * history. CITY_DISCOUNT was two points UNDER the policy rate, and the
     * bank then funded itself at two points OVER it. The city therefore
     * borrowed at a guaranteed four-point loss to its own bank, by
     * construction, on every dollar the bank's deposits did not cover - and
     * the bank had no say in how much of it to hold, because
     * Game.refreshBank() set cityBook to the whole of the principal.
     *
     * Measured on Jerus's own save, 2026-09-13: $18.2bn of thirty-year paper
     * issued in month 144 at 0.625% a year, 91.6% of a book yielding 1.307%
     * against wholesale funding at 5.593%. The bank had failed eighteen times
     * by month 354 and was three months from the nineteenth. See
     * claude/the-bond-that-broke-the-bank.md.
     *
     * NOTE WHAT THIS IS NOT. It is not a re-tightening of the stress curve.
     * FULL_STRESS_MULTIPLE stays at 150 years and MAX_SPREAD_PER_MEASURE at
     * five points, exactly as gentle as Jerus asked for them - a city can
     * still borrow a large multiple of its economy without being priced as
     * distressed. What it can no longer do is borrow below cost.
     *
     * SPLIT BY WHO HOLDS THE PAPER (0.7.15). The bank's cost is a floor
     * because the bank is who lends: it will not hold the city's paper for
     * less than its money costs it. The central bank's money costs it
     * nothing but the policy rate, so the share of the paper it holds is
     * priced there, and only the rest at the bank's floor:
     *
     *     floor = max(MIN_RATE, policy + (1 - s) x max(0, costOfFunds + MIN_MARGIN - policy))
     *
     * s being the central bank's share of the city's own paper outstanding,
     * every maturity (centralBankShareOfPaper()). At s = 0 it is the floor
     * above to the bit; at s = 1 the policy rate, whatever the bank's state.
     * Jerus, lifting the holdings dial to 100%: "Its for later, for when
     * banks and investors dont want government bonds, its also f bank is in
     * dire state, to not harm it further, also make it so that if bank is in
     * a pickle, and central bank is at 100% then your debt isnt priced at tge
     * bank, ira proportional". A bank in trouble - at the window, its cost of
     * funds over the policy rate - makes the city's borrowing dearer only on
     * the share it and the households still hold.
     */
    public double floorRate() {
        return floorAt(baseRate);
    }

    /**
     * ...at a policy rate of the caller's: the one definition floorRate() and
     * rateAtPolicy() both read, so the Policy tab's "what moving the dial
     * does" splits the floor as the market does. At s = 0 it is exactly
     * max(MIN_RATE, max(policy, costOfFunds + Bank.MIN_MARGIN)), the floor
     * until 0.7.15.
     */
    private double floorAt(double policy) {
        double floor = Math.max(policy, costOfFunds + Bank.MIN_MARGIN);
        double s = Math.min(1, centralBankShareOfPaper());
        if (s > 0) floor = policy + (1 - s) * (floor - policy);
        return Math.max(MIN_RATE, floor);
    }

    /**
     * The bank's floor, unsplit: the policy rate, or the bank's cost of funds
     * plus Bank.MIN_MARGIN if that is higher - what the share of the city's
     * paper the central bank does not hold is priced at, and the whole floor
     * until 0.7.15. For the Finances tab's rate page; floorRate() is what
     * prices.
     */
    public double bankFloorRate() {
        return Math.max(MIN_RATE, Math.max(baseRate, costOfFunds + Bank.MIN_MARGIN));
    }

    /** What a hopeless one pays - both measures maxed out. */
    public double ceilingRate() {
        return baseRate + 2 * MAX_SPREAD_PER_MEASURE;
    }

    /**
     * Re-prices the standing rate off what the city owes right now.
     *
     * NOTE: the old version did `if (GDP == 0) GDP = allPrincipal;`, permanently
     * overwriting the GDP field with a debt figure. That corruption then fed
     * every later rate calculation. Uses a local fallback instead.
     */
    public void updateInterest() {
        /*
         * The city's own paper is priced on its debt against its tax base on
         * the floor, and that is the whole of it. Until 0.7.7 the bank's
         * strain premium rode on top - eighteen points at the worst, which a
         * founding city with no branch paid until it built one - and the
         * premium is gone (Bank, WHAT A LOAN COSTS).
         */
        currentRate = priceAt(getPricedDebt());
    }

    /**
     * What a NEW loan of this size would cost - priced with itself included.
     *
     * WHY THIS IS NOT JUST priceAt(debt + amount)
     *
     * The face value of a loan depends on the rate (a T-Bill discounts by it, a
     * bond takes a premium off it), and the rate now depends on the face value.
     * That is a fixed point, not a formula. It needs no calculus though: the
     * spread is small and the curve is clamped at both ends, so iterating the
     * two definitions against each other contracts onto the answer in three or
     * four passes. Six, for margin.
     *
     * The caller supplies faceOf(), because each instrument grosses a request up
     * differently - and getting that wrong is the whole point of the exercise.
     * Pricing off the balance sheet BEFORE the loan is what let a debt-free city
     * borrow ten million at one percent, which is not a thing that happens.
     *
     * @param requested what the city wants to receive
     * @param faceOf    given a rate, what the city would end up owing
     */
    public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf) {
        return quoteRate(requested, faceOf, 0);
    }

    /**
     * ...at a maturity (0.7.1): the same fixed point, read as the curve's zero
     * rate for that many months (none: the short rate). The three instruments
     * hand their duration in, so the issue and the buyback price off the same
     * curve - see THE CURVE. Since CD1 this is quoteCurve()'s fixed point with
     * one number read off it; Game's quotes that price a schedule payment by
     * payment ask for the curve itself.
     */
    public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf, int months) {
        return quoteCurve(requested, curve -> faceOf.applyAsDouble(curve.zero(months))).zero(months);
    }

    /** Straight-line version for instruments whose face value IS the request. */
    public double quoteRate(double requested) {
        return quoteRate(requested, 0);
    }

    /** ...at a maturity. */
    public double quoteRate(double requested, int months) {
        return quoteCurve(requested).zero(months);
    }

    /**
     * THE CURVE A NEW LOAN IS PRICED ON, with itself included (CD1): the
     * fixed point quoteRate() walks, returned as the whole curve instead of
     * one rate read off it. A schedule that is priced payment by payment (a
     * serial's slices, a term loan's coupons) needs every month's factor of
     * the curve the loan will stand on once it is booked, not the one zero
     * rate of its last month - so the face the instrument grosses up to is
     * asked of the curve (faceOf), the curve is rebuilt on the debt that face
     * makes, and so on, QUOTE_ITERATIONS times. Month 0 of the curve it
     * returns is the short rate quoteRate(requested, faceOf) would have given,
     * and zero(m) the rate for m months, to the bit.
     *
     * @param requested what the city wants to receive
     * @param faceOf    given the curve the loan would stand on, what the city would end up owing
     */
    public YieldCurve quoteCurve(double requested, java.util.function.ToDoubleFunction<YieldCurve> faceOf) {
        double existing = debtAfterProceedsOf(requested);
        YieldCurve curve = curveAt(existing + Math.max(0, requested));

        for (int i = 0; i < QUOTE_ITERATIONS; i++) {
            double face = faceOf.applyAsDouble(curve);
            if (!Double.isFinite(face) || face < 0) break;
            curve = curveAt(existing + face);
        }
        return curve;
    }

    /** ...for an instrument whose face value IS the request: one pass, the loan priced in. */
    public YieldCurve quoteCurve(double faceIsRequest) {
        return curveAt(debtAfterProceedsOf(faceIsRequest) + Math.max(0, faceIsRequest));
    }

    /**
     * The debt the loan lands ON TOP OF - which is not simply what is owed now.
     *
     * The cash a loan hands over pays the overdraft down, so an overdrawn city
     * borrowing its way out is not left owing both. Counting both would price
     * the hole twice and quote a rate for a balance sheet that will not exist a
     * moment after the money arrives.
     *
     * The emergency T-Bill was exactly this case until 0.7.0: it was issued
     * precisely to cover the gap, so what it should be priced against is the
     * bonds plus the bill itself, not the bonds plus the bill plus the gap it
     * is closing. The central bank's advances are the same case now: cash
     * above zero repays them first thing, so a note's proceeds clear them
     * after the overdraft.
     */
    private double debtAfterProceedsOf(double received) {
        double owedShort = Math.max(0, overdraft) + advances;
        double clears = Math.min(owedShort, Math.max(0, received));
        return getAllPrincipal() + owedShort - clears;
    }

    /* -----------------------------------------------------------------------
       WHAT THE AUCTION STARTS FROM, AND WHERE A SALE GOES (CD2)
       ----------------------------------------------------------------------- */

    /**
     * The yield an auction of this paper starts from - fair (spec-city-debt.md
     * section 4): what the curve says the paper is worth with `extraFace` of
     * new debt on the books (quoteCurve(): the issue is priced into the credit
     * spread, as every quote has been since 0.7.0). The par yield for a bond
     * (the coupon that prices a bullet of these months at 100), the one coupon
     * that prices the amortizing schedule at 100, and for a bill the zero rate
     * at its months as a monthly-compounded yield - the rate that discounts
     * its face to the curve's price. Nobody bids under it (Auction).
     *
     * @param months a bill's or a bond's months; an amortizing bond's years times twelve
     */
    public double fairYield(Auction.Kind kind, int months, double extraFace) {
        return fairYieldOn(quoteCurve(Math.max(0, extraFace)), kind, months);
    }

    /** ...read off a curve already in hand: the one the sale is priced on (Game.runAuction() asks quoteCurve() once and reads both from it). */
    public static double fairYieldOn(YieldCurve curve, Auction.Kind kind, int months) {
        return switch (kind) {
            case BILL -> curve.zero(months);
            // A floater and a linker are asked for the nominal yield the bidders weigh (CD5): a par bullet of that
            // term. The margin and the real coupon that price them at 100 are the coupons (fairMargin(), fairRealCoupon()).
            case BOND, FLOATING, LINKED -> curve.parYield(months);
            case AMORTIZING -> amortizingParYield(curve, months / 12);
        };
    }

    /* -----------------------------------------------------------------------
       FAIR AT THE SALE, FOR A FLOATER AND A LINKER (CD5, spec-city-debt.md
       sections 2.4 and 2.5)

       The study's frn_margin() and ilb_real_coupon() (debt-model.py) on the
       game's timing. Static and pure, so the harness can price them itself.
       ----------------------------------------------------------------------- */

    /**
     * The margin over the policy rate that prices a floating-rate note of these months at 100 on
     * this curve: each coupon at the dial the path expects when it is paid
     * (FloatingRateNote.flowsFor()), the face at the end, found by bisection on [-50%, +50%] a
     * year - the present value rises with the margin. The curve's term premium, the credit spread
     * its life carries and the gap between the expected dial and the floor are in it: 18 basis points
     * at 2 years in a calm city.
     *
     * @param policyPath the dial expected t months ahead, t = 0 now (expectedPolicyPath())
     */
    public static double fairMargin(YieldCurve curve, double[] policyPath, int months) {
        double lo = -0.5, hi = 0.5;
        for (int i = 0; i < FAIR_COUPON_STEPS; i++) {
            double mid = (lo + hi) / 2;
            if (curve.pv(FloatingRateNote.flowsFor(100, mid, policyPath, months)) < 100) lo = mid; else hi = mid;
        }
        return (lo + hi) / 2;
    }

    /** The halvings of the bisection above: far past what a double keeps of a bracket of one unit of yield. */
    private static final int FAIR_COUPON_STEPS = 100;

    /**
     * The real coupon that prices a NEW inflation-linked bond of these months at 100 on this curve:
     * the first payment on a ratio of 1, each later month's grown by the growth the market expects
     * (InflationLinkedBond.ratiosFor()), the last month adding the indexed principal - never under
     * the face. The present value is linear in the coupon, so it is closed: with A the
     * coupon's annuity (a twelfth of the sum of each ratio times its month's factor) and P the
     * principal's present value (the floor in it), the coupon is (100 - P) / A.
     *
     * @param growth the index's growth month by month, entry j the month j months ahead (expectedInflationGrowth())
     */
    public static double fairRealCoupon(YieldCurve curve, double[] growth, int months) {
        if (months <= 0) return 0;
        double[] ratios = InflationLinkedBond.ratiosFor(1.0, growth, months);
        double annuity = 0;
        for (int i = 0; i < ratios.length; i++) annuity += 100 * ratios[i] / 12 * curve.factor(i + 1);
        double principal = 100 * Math.max(1, ratios[ratios.length - 1]) * curve.factor(months);
        return annuity > 0 ? (100 - principal) / annuity : 0;
    }

    /** fairMargin() on the curve a sale of `extraFace` is priced on and the dial the market expects now: the Borrow page's term column. */
    public double fairMargin(int months, double extraFace) {
        return fairMargin(quoteCurve(Math.max(0, extraFace)), expectedPolicyPath(), months);
    }

    /** fairRealCoupon() on the curve a sale of `extraFace` is priced on and the index the market expects now. */
    public double fairRealCoupon(int months, double extraFace) {
        return fairRealCoupon(quoteCurve(Math.max(0, extraFace)), expectedInflationGrowth(), months);
    }

    /**
     * The inflation a linked line prices in (CD5): the nominal par yield of this term less the
     * real coupon that prices a linked bond at 100 - the breakeven the market's two lines
     * disagree by. With a flat curve and a steady inflation it is that inflation to a first
     * approximation.
     */
    public double breakevenInflation(int months, double extraFace) {
        YieldCurve curve = quoteCurve(Math.max(0, extraFace));
        return curve.parYield(months) - fairRealCoupon(curve, expectedInflationGrowth(), months);
    }

    /**
     * The nominal coupon a new bullet bond of these months sells at par for (CD6): the par yield
     * of the curve a sale of `extraFace` is priced on, YieldCurve.parYield() - the figure
     * breakevenInflation() takes fairRealCoupon() from, so that
     * parYield(m, f) == fairRealCoupon(m, f) + breakevenInflation(m, f). For Your rate's
     * sentence on the ten-year linked line against a nominal one.
     */
    public double parYield(int months, double extraFace) {
        return quoteCurve(Math.max(0, extraFace)).parYield(months);
    }

    /**
     * The one coupon that prices an amortizing bond of these years at 100 on a
     * curve: every payment of its schedule at its own month's factor. The
     * schedule is linear in the coupon (a month's interest is the balance times
     * coupon / 12, and the principal slices do not depend on it), so two
     * evaluations give the line: P at a coupon of nothing is the principal
     * alone, Q at a coupon of 1.0 adds a year's interest on every balance, and
     * the coupon that prices it at 100 is (100 - P) / (Q - P). CD1 solved this
     * in Game for the serial bond; the auction's fair yield for the amortizing
     * bond is the same figure, and it lives here now.
     */
    public static double amortizingParYield(YieldCurve curve, int years) {
        double p = curve.pv(Auction.amortizingPer100(0, years));
        double q = curve.pv(Auction.amortizingPer100(1.0, years));
        if (!(q - p > 1e-12)) return curve.zero(years * 12);
        return (100 - p) / (q - p);
    }

    /**
     * The bond line a sale at this term goes into (spec-city-debt.md section
     * 2.2, D6, D14): the city's own LongTermBond - not dollar paper, and not a
     * callable one (CD4: a sale that is not callable never reopens a line that
     * is) - whose original term is these years and which still has at least nine tenths of
     * it left (Auction.BENCHMARK_LIFE: 22 months of a 2-year, 54 of a 5-year,
     * 108, 216, 324 and 540 of the rest); the one with the most months left if
     * two qualify. Null when there is none, and the sale opens a new line.
     */
    public Debt benchmarkLine(int years) {
        return benchmarkLine(years, false);
    }

    /**
     * ...of a callable sale (CD4): a callable sale reopens only a callable line
     * of the same term whose first call sits as far from its start as the
     * sale's would (CallTree.firstCallYears()), and a sale that is not callable
     * never reopens a callable line - the paper is not the same paper.
     */
    public Debt benchmarkLine(int years, boolean callable) {
        int term = years * 12;
        int need = (int) Math.ceil(Auction.BENCHMARK_LIFE * term - 1e-9);
        Debt best = null;
        for (Debt d : debts) {
            if (d.isForeign() || !(d instanceof LongTermBond bond) || d.getDuration() != term) continue;
            if (bond.isCallable() != callable) continue;
            if (callable && bond.callTermMonths() != CallTree.firstCallYears(years) * 12) continue;
            if (d.getRemainingMonths() < need) continue;
            if (best == null || d.getRemainingMonths() > best.getRemainingMonths()) best = d;
        }
        return best;
    }

    public void clearDebts() {
        debts.clear();
    }

    public void setDebt(List<Debt> debts) {
        this.debts = debts;
        setExchangeRate(exchangeRate);   // the load path's half of the rule above
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

    /** The city's debt book, in the new unit. Rates and premiums do not move. */
    public void redenominate(double scale) {
        overdraft *= scale;
        GDP *= scale;
        monthlyTaxRevenue *= scale;
        monthlyExports *= scale;
        exchangeRate *= scale;
        for (Debt paper : getDebt()) {
            if (paper != null) paper.redenominate(scale);
        }
    }

}
