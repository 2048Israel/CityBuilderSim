<!-- gate-run: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-DM1-P-20261118-002805-6934 -->
<!-- readiness: provisional -->
## changelog
### DM1-P — batch-check.sh names the Earth and world files — 2026-11-18, ready, see docs/queue/DM1-P-notes.md
Batch checks fill missing Earth and world properties from the checkout's data, falling back to the main checkout.
Batch checks, nightly and the gate share one helper. Unrelated Java flags and explicit data paths are preserved;
Windows paths use C:/ form, and one line labels each property named, already set, or not found.

## known-reds
LandNationsCheck | A block's price could not be worked out: java.lang.IllegalStateException: fixture quote failure | QW1 | 2026-11-18 | Intentional HoverFixture.measure exception at L1547, surfaced by main AllChecks.FAILING only because the unrelated QW1 fixture crashes later
LandNationsCheck | RuntimeException hands its request back and shows the failure line                                                     OK | QW1 | 2026-11-18 | Passing section 14 assertion ending OK, falsely extracted by main AllChecks.FAILING when the unrelated QW1 fixture times out; exact OK text only
LandNationsCheck | RuntimeException waits before retrying the same month                                                                  OK | QW1 | 2026-11-18 | Passing section 14 assertion ending OK, falsely extracted by main AllChecks.FAILING when the unrelated QW1 fixture times out; exact OK text only
LandNationsCheck | RuntimeException releases its asked key so the same block is retried after RETRY_MS                                    OK | QW1 | 2026-11-18 | Passing section 14 assertion ending OK, falsely extracted by main AllChecks.FAILING when the unrelated QW1 fixture times out; exact OK text only
LandNationsCheck | RuntimeException retry recovers a visible price in the same month                                                      OK | QW1 | 2026-11-18 | Passing section 14 assertion ending OK, falsely extracted by main AllChecks.FAILING when the unrelated QW1 fixture times out; exact OK text only
LandNationsCheck | Error hands its request back and shows the failure line                                                                OK | QW1 | 2026-11-18 | Passing section 14 assertion ending OK, falsely extracted by main AllChecks.FAILING when the unrelated QW1 fixture times out; exact OK text only
LandNationsCheck | Error waits before retrying the same month                                                                             OK | QW1 | 2026-11-18 | Passing section 14 assertion ending OK, falsely extracted by main AllChecks.FAILING when the unrelated QW1 fixture times out; exact OK text only
LandNationsCheck | Error releases its asked key so the same block is retried after RETRY_MS                                               OK | QW1 | 2026-11-18 | Passing section 14 assertion ending OK, falsely extracted by main AllChecks.FAILING when the unrelated QW1 fixture times out; exact OK text only
LandNationsCheck | Error retry recovers a visible price in the same month                                                                 OK | QW1 | 2026-11-18 | Passing section 14 assertion ending OK, falsely extracted by main AllChecks.FAILING when the unrelated QW1 fixture times out; exact OK text only
LandNationsCheck | Exception in thread "main" java.lang.IllegalStateException: The real quote did not release its worker | QW1 | 2026-11-18 | Unchanged main hoverRealCancellation L1734: the QW1 10004.40ms deadline miss is followed by its finally awaitTermination(10s) timeout under the allowed ensemble load
