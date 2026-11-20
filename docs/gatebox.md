# The gate box

`gatebox` is the quiet Ryzen box beside the laptop. The gate runs on it (GB1), and so
do the nightly (GB2) and, behind both, builders' ensembles (GB3). This note says what the box is and what its first night
measured, and what that means for how many jobs to run on it at once. Measured
2026-11-19/20. Jerus asked for it on record so a session can look it up instead of
asking again (2026-11-20: "if other sessions ask its there").

## The machine

- **CPU:** Ryzen 7 9850X3D, 8 cores and 16 threads; cpuN and cpuN+8 are one core's
  two threads. It boosts to 5.65 GHz.
- **Memory and disk:** 32 GB DDR5-6000 (EXPO) and a 1 TB WD_Black SN7100 NVMe.
- **System:** Ubuntu Server 26.04.1, on Wi-Fi at `192.168.0.16`, also
  `gatebox.local`. The laptop's key logs in as `jerus`; sudo is Jerus's alone.
- **Tuning:** `cpu-perf.service` sets the governor and EPP to `performance` with
  boost on at each boot. `cpu-perf run N cmd` pins a command to the N fastest cores
  (0, 2, 5, 3, 1, 4, 7, 6).
- **The gate's own folders:** `~/cbs/main`, `~/cbs/runs`, `~/cbs/tools`, and
  Temurin in `~/cbs/jdk`. See `docs/queue/GB1-notes.md`.

## The soak: 7 hours, every test passed

| test (stress-ng `--verify`) | length | result |
|---|---|---|
| CPU and memory mixed | 2.5 h | 0 failures |
| memory, every pattern | 2.5 h | 0 failures |
| all 16 threads, heaviest load | 2 h | 0 failures |

- **Errors:** no machine-check, memory or NVMe errors, and no reboot.
- **Wi-Fi:** no drop all night, at -60 dBm and about 600 Mbit/s.
- **Heat:** the hottest was 83.8 °C, in brief spikes (about 65 °C typical). Full
  load held 75 °C steady for 2 h, and the 95 °C guard never came near.
- **Speed:** the same after the heat soak as before it.

## One job alone

A single-threaded Java bench (the box's `~/overnight`, serial GC, 256 MB heap)
gives these medians:

- **Cores:** cores 0 and 2 take 28.3 ms at 5.63 GHz. The slowest, 6 and 7, take
  29.0 ms at 5.52 GHz. That is about 3% from best to worst, so any core will do.
- **Repeat runs:** they vary by about 1%.
- **Neighbours:** what other load costs the bench on cpu0:

| running beside it | time | cost |
|---|---|---|
| nothing | 28.3 ms | |
| the other 7 cores busy | 29.3-30.7 ms | +5% |
| its own core's other thread (cpu8) busy | 36.6-37.4 ms | +30% |
| all 15 other threads busy | 38.4-38.9 ms | +37% |

## Many jobs at once: each one slower, all of them sooner

N copies of the bench started together, three rounds each:

| at once | each job | the whole lot | per job |
|---|---|---|---|
| 1 | 28.4 ms | 5.6 s | 5.6 s |
| 8, one per core | 29.9 ms (+5%) | 6.0 s | 0.75 s |
| 16, every thread | 40.5 ms (+43%) | 8.3 s | 0.52 s |

At 16, each job runs about 40% slower, yet the 16 finish in 8.3 s, where two rounds
of 8 take 12.0 s. That is about 30% sooner. The reason is that a core's two threads
share it: each runs at about 70% of the core's speed, but together they do about 1.4
cores' work (2 x 28.4 / 40.5). The cycles one thread leaves idle while it waits on
memory go to the other. So:

- **A job someone waits on**, such as a gate or a timing run: at most 8 at once, one
  per core, and never two on one core's threads. A run judged on time wants the box
  otherwise quiet, which is why the nightly holds the box whole.
- **A pile of independent jobs where only the last one's end matters**, such as an
  ensemble's runs or a suite's harnesses: fill all 16 threads, and the lot ends about
  30% sooner. Anywhere from 9 to 16 jobs, 16 at once still beats two rounds.
- **Pinning** hardly matters when many run, because Linux spreads them as well as
  `taskset` does. 8 unpinned took 6.0-6.1 s against 6.0 s pinned; 16 unpinned took
  8.2-8.5 s against 8.2-8.7 s pinned. For one job timed alone, pin it with
  `cpu-perf run 2`.

The bench is small, single-threaded and fits in cache. Harnesses with bigger heaps
and threads of their own share memory bandwidth and the 96 MB L3, so they may gain
less from the second thread. Real work measured so far:

- **The full suite:** 9 m 44 s on the box (GB1). The suite's CPU guard holds new
  harness starts while CPU is above 80%. On the laptop it took about 55 min (BO1-S's
  laptop gate, 54 m 28 s). BO1-S's gate on the box, with its 9 nightly picks, took
  12 m 25 s.
- **An ensemble's 80 runs**, 8 at once: 17.5 min against 116 min on the laptop
  (GB3, measured on its branch). At 16 at once, 16 ZERO_SLACK seeds took 310 s a run
  against 198 s at 8: 28% more runs an hour, but each run 57% longer, and a gate that
  comes waits for the runs started, so GB3 plays 8.
- **Not yet measured:** whether the suite does better at 16 than at 8 on the box.
  Today the 80% guard decides it.

## Where the figures are

They are on the box in `~/overnight`, not in the repository: `run.log`, `temps.log`,
`bench.log`, `para.sh` and `para.log` (the many-at-once rounds), each phase's log,
`kernel-errors.log` and `wifi-events.log`.
