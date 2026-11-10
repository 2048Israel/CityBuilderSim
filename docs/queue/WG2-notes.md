# WG2 - senior care's excess, and the out-of-work verdict ({next}, 2026-11-10)

The brief is WG2's row in `QUEUE.md` and [the wages study](../studies/spec-wages.md) §3.4 and §6, Jerus's call C2(a)
(2026-11-02: "Cut the excess, honest label (Recommended)"), with the staffing-gate study's C4(a) (settled by the
orchestrator 2026-11-09): the brake and the pull rule apply to `Migration.drawFor()` too. Lane B, after GW1 (0.9.89).
A model batch: Opus, with Sonnet slices for the code.

## 1. The rule

- **The pulls lift the homes' draw only** (part 1): `target = JOB_WEIGHT x jobs x residents a job x min(1, rent x
  crime) + HOME_WEIGHT x homes x senior x rent x crime` (`Migration.jobsPull()`, `homesPull()`). Senior care's
  x1.30 lifted the whole target before, the jobs' draw with it: 1.46 workers a post in a city with full care, the
  21% of his cities (spec-wages §2.4).
- **Arrivals slow past `Migration.HEALTHY_OUT_OF_WORK` (.06) and stop at `ARRIVALS_STOP_OUT_OF_WORK` (.12)**, a
  straight line between (`arrivalBrake()`, part 2), read off the month's own unemployment as migration is struck.
- **The jobless past the healthy rate leave** (part 3): `(unemployed - .06 x labour force) x SURPLUS_DEPARTURE_RATE`
  a month, less those the pinned bands sent. Two choices the prototype left open, taken here: the excess is counted
  on the **labour force** (what `getUnemploymentRate()` divides by; the prototype multiplied by the workforce, which
  counts the students and prisoners and overstates it), and the leavers are spread on each band's **surplus over its
  own posts** (`surplusInBand()`: who is out of work at their level), not on the city's band share.
- **The gate's draw reads both** (GW0-C C4a): `drawFor()`'s jobs' pull is `jobsPull()`, and the people it draws are
  braked after the room cap, as the month's arrivals are. No new saved figure: the pulls are saved
  (`LAST_SCALARS`), and the brake reads the city's unemployment, which a load restores.
- Part 4 (vacancies drawing people) is left out, as measured (spec-wages table 2). `TARGET_LABOUR_SLACK` stays .125.

## 2. The words

- The verdict: green 3-7%, amber to 12%, red past it (`PeopleScreen.OUT_OF_WORK_HIGH` .07, `_FAR` =
  `Migration.ARRIVALS_STOP_OUT_OF_WORK`), the header's tip with what migration does at 6% and 12% and the cities
  beside it (spec-wages S4, the mockup's wording). SummaryScreen's unemployed line read a bare `.15`; it reads the
  dial now.
- The People screen's draw card drew `(jobs + homes) x the pulls`; it draws each half with its own pulls.
- Senior care's words ("draws 30% more people to the city") say the homes' draw.

## 3. The young city, measured first

(to come)
