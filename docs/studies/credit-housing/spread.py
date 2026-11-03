"""The spread inside a cell (spec-credit-housing.md section 2.1).

Every household in a cell is the same today, so a cell is either wholly in
debt or not at all. The design keeps the cell's averages and assumes a shape
around them: a share of the cell holds the thing (a card balance, a mortgage),
and among those who hold it the amount is lognormal with a fixed sigma, its
mean the cell's average. Everything the month needs from that shape has a
closed form, so the month never draws a household. This file checks each
closed form against a Monte Carlo draw and times the city median.

    python spread.py > out/spread.txt
"""
import math, random, time

SQ2 = math.sqrt(2.0)
def Phi(x):            # the standard normal CDF
    return 0.5 * math.erfc(-x / SQ2)

def mu_of(mean, sigma):      # the log-mean that gives this arithmetic mean
    return math.log(mean) - 0.5 * sigma * sigma

# ---- the closed forms the month uses -------------------------------------
def share_below(x, mean, sigma):
    """Share of a lognormal(mean, sigma) at or below x."""
    if x <= 0: return 0.0
    return Phi((math.log(x) - mu_of(mean, sigma)) / sigma)

def expected_shortfall(gap, mean, sigma):
    """E[max(0, gap - S)] for S lognormal(mean, sigma): what the households
    whose buffer is under the gap must borrow, per household of the cell
    (the put's formula)."""
    if gap <= 0: return 0.0
    d1 = (math.log(mean / gap) + 0.5 * sigma * sigma) / sigma
    d2 = d1 - sigma
    return gap * Phi(-d2) - mean * Phi(-d1)

def partial_mean_above(x, mean, sigma):
    """E[S ; S > x]: the part of the mean held by those above x."""
    if x <= 0: return mean
    d1 = (mu_of(mean, sigma) + sigma * sigma - math.log(x)) / sigma
    return mean * Phi(d1)

# ---- Monte Carlo checks ---------------------------------------------------
def draws(mean, sigma, n, rng):
    mu = mu_of(mean, sigma)
    return [math.exp(rng.gauss(mu, sigma)) for _ in range(n)]

def check(rng):
    print("--- 1. the closed forms against 200,000 draws ---")
    worst = 0
    for mean, sigma, x in [(1.0, 0.8, 0.5), (1.0, 1.28, 2.0), (3.0, 1.5, 1.0),
                           (0.6, 1.28, 0.3), (40.0, 1.5, 6.0), (5.0, 0.68, 9.0)]:
        s = draws(mean, sigma, 200_000, rng)
        mc_below = sum(1 for v in s if v <= x) / len(s)
        mc_short = sum(max(0.0, x - v) for v in s) / len(s)
        mc_above = sum(v for v in s if v > x) / len(s)
        cf = (share_below(x, mean, sigma), expected_shortfall(x, mean, sigma),
              partial_mean_above(x, mean, sigma))
        err = max(abs(cf[0] - mc_below), abs(cf[1] - mc_short) / max(x, 1e-9),
                  abs(cf[2] - mc_above) / mean)
        worst = max(worst, err)
        print(f"  mean {mean:5.1f} sigma {sigma:4.2f} x {x:4.1f}: share below {cf[0]:.4f} (MC {mc_below:.4f}),"
              f" shortfall {cf[1]:.4f} (MC {mc_short:.4f}), mean above {cf[2]:.4f} (MC {mc_above:.4f})")
    print(f"  worst error {worst:.4f} (share, or a share of x or of the mean)")

# ---- the city median from the cells ---------------------------------------
def city_cdf(x, cells):
    """Share of the city's households at or below x, the cells' mixture:
    each cell w (households), p (share holding), mean, sigma."""
    tot = 0.0; acc = 0.0
    for w, p, mean, sigma in cells:
        tot += w
        acc += w * ((1 - p) + (p * share_below(x, mean, sigma) if p > 0 else 0))
    return acc / tot

def city_quantile(q, cells, holders_only=False):
    """The q-quantile of the mixture by bisection in log space. holders_only
    gives the median among households that hold any (the figure statistics
    offices print as 'median debt of those with debt')."""
    if holders_only:
        cells = [(w * p, 1.0, m, s) for w, p, m, s in cells if p > 0 and w > 0]
    lo, hi = 1e-9, max(m for _, _, m, _ in cells) * 1e4
    if city_cdf(lo, cells) >= q: return 0.0
    for _ in range(60):
        mid = math.sqrt(lo * hi)
        if city_cdf(mid, cells) < q: lo = mid
        else: hi = mid
    return math.sqrt(lo * hi)

def check_median(rng, ncells):
    cells = []
    for i in range(ncells):
        w = rng.uniform(1_000, 100_000)
        p = rng.uniform(0.05, 0.7)
        mean = math.exp(rng.uniform(math.log(0.2), math.log(8)))
        cells.append((w, p, mean, 1.28))
    t0 = time.perf_counter()
    med = city_quantile(0.5, cells)
    medh = city_quantile(0.5, cells, holders_only=True)
    t = time.perf_counter() - t0
    # Monte Carlo of the same mixture, households drawn in proportion to w
    tot = sum(c[0] for c in cells); sample = []
    for w, p, mean, sigma in cells:
        k = int(round(200_000 * w / tot))
        mu = mu_of(mean, sigma)
        for _ in range(k):
            sample.append(math.exp(rng.gauss(mu, sigma)) if rng.random() < p else 0.0)
    sample.sort(); held = [v for v in sample if v > 0]
    mc = sample[len(sample) // 2]; mch = held[len(held) // 2]
    print(f"  {ncells} cells: median {med:.4f} (MC {mc:.4f}), among holders {medh:.4f} (MC {mch:.4f}),"
          f" both in {t*1000:.1f} ms of Python")

if __name__ == "__main__":
    rng = random.Random(20261102)
    check(rng)
    print("--- 2. the city's median from the cells' mixture, against a draw of 200,000 households ---")
    for n in (78, 390):
        check_median(rng, n)
    print("--- 3. why the spread: one cell, three shapes ---")
    # a cell owing on average 2 months of income, against a 6-month ceiling
    for label, p, sigma in [("today: every household the same", 1.0, 1e-6),
                            ("all hold, lognormal 1.28", 1.0, 1.28),
                            ("45% hold, lognormal 1.28", 0.45, 1.28)]:
        mean_holder = 2.0 / p
        over3 = p * (1 - share_below(3.0, mean_holder, sigma))
        over6 = p * (1 - share_below(6.0, mean_holder, sigma))
        print(f"  {label:34s}: owe over 3 months {over3:6.1%}, over 6 months {over6:6.1%}")
