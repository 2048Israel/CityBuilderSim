"""The wealth study's currency prototype (docs/studies/spec-wealth.md section 2.5): a SCRATCH copy of
ForeignAccounts.java whose balance term builds pressure while the overall balance lasts, in place of the capped
level of balanceTarget(). Never applied to the tree.

    python -I make-fx.py src/main/java/ham/citybuildersim/ForeignAccounts.java OUT/ForeignAccounts.java

-Dwealth.pull (default .025 a month per year-of-GDP of balance: 0.3% a year per point of GDP) and -Dwealth.half
(default 120 months) set it. Compile OUT against target/classes into a folder put first on the classpath and run
WealthRun as usual.
"""
import sys

src, dst = sys.argv[1], sys.argv[2]
L = open(src, encoding="utf-8").read().split("\n")
hits = [i for i, l in enumerate(L) if "balanceReached += MARKET_SPEED * (balanceTarget() - balanceReached);" in l]
assert len(hits) == 1, hits
L[hits[0]] = ('        { /* WEALTH STUDY PROTOTYPE: the balance builds pressure while it lasts. */\n'
              '          double pull = Double.parseDouble(System.getProperty("wealth.pull", "0.025"));\n'
              '          double half = Double.parseDouble(System.getProperty("wealth.half", "120"));\n'
              '          double raw = gdpTrailing > 0 ? -(currentTrailing + financialTrailing) / gdpTrailing : 0;\n'
              '          balanceReached = balanceReached * Math.pow(.5, 1 / half) + pull * raw;\n'
              '          balanceReached = Math.max(-4, Math.min(4, balanceReached)); }')
open(dst, "w", encoding="utf-8").write("\n".join(L))
