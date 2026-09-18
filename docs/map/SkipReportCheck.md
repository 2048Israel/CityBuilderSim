# SkipReportCheck.java - 229 lines · 4 methods · 0 constants · harnesses

`ham/citybuildersim/SkipReportCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> Verifies the fast-forward summary.
> 
> The reason this needs testing at all is that half of it cannot be derived
> from the endpoints. A city that ran out of power for forty months and then
> built a second station looks, at both ends, exactly like one that never had a
> problem - so the episode counters have to be sampled, and a sampling bug
> would silently report a smooth century on a city that spent it starving.

**Uses:** [TimeSkipReport](TimeSkipReport.md) (12)

## Sections

| line | section |
|---:|---|
| 42 | · 1. a hundred good months |
| 85 | · 2. what got built |
| 104 | · 3. a century that went badly |
| 146 | · 4. the headlines |
| 178 | · 5. stopping early |
| 195 | · 6. nothing to report |
| 215 | · 7. beginSkip clears the last one |

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 214 | **type** `public class SkipReportCheck` | Verifies the fast-forward summary. |
| 20 | 6 | `static void check(String label, double actual, double expected)` |  |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 7 | `static Map<String, Integer> buildings(Object...pairs)` |  |
| 40 | 189 | `public static void main(String[] args)` |  |

