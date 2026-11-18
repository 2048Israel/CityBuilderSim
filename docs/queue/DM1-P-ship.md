<!-- readiness: provisional -->
## changelog
### DM1-P — batch-check.sh names the Earth and world files — 2026-11-17, ready, see docs/queue/DM1-P-notes.md
Batch checks fill missing Earth and world properties from the checkout's data, falling back to the main checkout.
Batch checks, nightly and the gate share one helper. Unrelated Java flags and explicit data paths are preserved;
Windows paths use C:/ form, and one line labels each property named, already set, or not found.
