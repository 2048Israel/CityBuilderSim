"""The manufacturing margin study's second prototype (MA1, docs/studies/manufacturing-margin.md, section 6).

    python -I docs/studies/manufacturing-margin/hold.py OUTDIR

Writes into OUTDIR a copy of WorldMarket.java whose January holds each manufactured good's band at the
city's site to its chain: after a made good is priced off its inputs (Base.price), its makers' prices are
moved as one, by the root's own anchor (Base.anchor, with a throwaway supply scale), until the reference
city's band with nothing traded, over the band the literals give it, equals its inputs' shares times their
own bands there over theirs (each input finished first, in the chain's order, so a held input is read as
held) plus its own part times the manufactures index's level over the literals' level. Only the six goods
Manufacturing and Automotive make are held (MA1_HELD); every other made good is priced as on main. (The
first version chained each input's target, not its band; steel, which is not held, stood far below its
chain's target from m1200 on, and fabricated steel was held to that.) run.sh's PATCH=OUTDIR compiles it over
the snapshot (with proto.py's two files beside it for the two ways together). Run from the checkout's root.
The hold slips once the world's solve tips into a second branch (the study's section 6).
"""
import sys

OLD = """                } else {
                    boolean ownPart = chain.own[gi] > 0;
                    if (warm >= 0 && ownPart) base.moveOntoLevel(gi, slice, lambda, gi, target);
                    passes[gi] = ownPart ? solve(t, g, null, true, target, SOLVE_TOL, slice, lambda, gi, work) : 0;
                    base.price(gi, slice, phat, seenCache, nations, year, false);
                }"""
NEW = """                } else {
                    boolean ownPart = chain.own[gi] > 0;
                    if (warm >= 0 && ownPart) base.moveOntoLevel(gi, slice, lambda, gi, target);
                    passes[gi] = ownPart ? solve(t, g, null, true, target, SOLVE_TOL, slice, lambda, gi, work) : 0;
                    base.price(gi, slice, phat, seenCache, nations, year, false);
                    // MA1's prototype: a held good's band at the city's site is its inputs' bands there by their shares plus its own part on the level
                    if (MA1_HELD.contains(t.good)) {
                        double r = 0;
                        double[] q = new double[n];
                        for (int k = 0; k < chain.parent[gi].length; k++) {
                            int pg = chain.parent[gi][k];
                            double[] ps = new double[n];
                            System.arraycopy(phat, pg * n, ps, 0, n);
                            r += chain.share[gi][k] * Math.exp(base.off(base.traded[pg], ps, 0, q, 1));
                        }
                        r += chain.own[gi] * target / base.refLevel[base.ownLevelGood(gi)];
                        if (Double.isFinite(r) && r > 0) base.anchor(gi, slice, new double[1], 0, r * base.refLevel[gi], 0);
                    }
                }"""
OLD_SOLVE = """    public void solveYear(int year) {"""
NEW_SOLVE = """    /** MA1's prototype: the goods whose band at the city's site is held to their chain. */
    private static final java.util.Set<Good> MA1_HELD = java.util.EnumSet.of(Good.FABRICATED_STEEL, Good.MACHINERY, Good.CARS,
            Good.VANS, Good.ROLLING_STOCK, Good.TRUCKS);

    public void solveYear(int year) {"""


def main(out):
    src = open("src/main/java/ham/citybuildersim/WorldMarket.java", encoding="utf-8").read()
    for old, new in ((OLD, NEW), (OLD_SOLVE, NEW_SOLVE)):
        if src.count(old) != 1:
            sys.exit("WorldMarket.solveYear moved: the prototype's patch no longer applies (" + old.strip()[:60] + ")")
        src = src.replace(old, new)
    with open(out + "/WorldMarket.java", "w", encoding="utf-8", newline="\n") as fh:
        fh.write(src)


if __name__ == "__main__":
    main(sys.argv[1])
