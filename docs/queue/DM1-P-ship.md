<!-- readiness: provisional -->
## changelog
### DM1-P — batch-check.sh names the Earth and world files — 2026-11-17, ready, see docs/queue/DM1-P-notes.md
Batch checks now name earth-2.bin and world-1.bin when Java options are empty, preferring each checkout's data
and falling back to the main checkout. Batch checks, nightly and the gate share the helper; existing Java options
are preserved, Windows paths use C:/ form, and one line reports the files named or why none were named.
