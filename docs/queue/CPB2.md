# CPB2 — close the other loss-making undemanded production paths

Queued by the orchestrator's call on REV-CPB1-1, 2026-11-17. These are
uncovered pre-existing paths, not regressions introduced by CPB1. Keep this
separate from CPB1's reviewed empty-yard cathode mend.

`Sector.getPlannedOutput` prices an undemanded stock buffer against
`flowCostPerUnit`, but `getExportBoundOutput` still prices its spare against
`getMarginalCostPerUnit`, the joint bill allocated by `costShareOf`. For a
multi-output sector where marginal cost <= net export price < recipe input
cost, the rejected buffer becomes export spare. `forecastOrder` sees a full
run again and buys the feed; the output ships below its input cost.

Separately, the net export price is NaN for goods the world will not buy,
including VANS, TRUCKS, ROLLING_STOCK and LUXURIES. The comparison in the
buffer gate then does not veto even arbitrarily expensive feed. Decide what
an undemanded buffer with no world outlet can sell for, and gate it on that
decision rather than letting NaN authorize the run.

Cause both paths in real-sector fixtures through Markets.clearMonth and the
strike/accounts path. Prove that imported feed is not bought merely to make
undemanded output that cannot cover its inputs. Keep real domestic/captive
demand, profitable production, co-product ratios, committed feed,
existing-stock sales and the national-account identities. Each new assertion
must fail with its mend removed and its premise retained. Re-find the
methods with Where; the review's line numbers belong to CPB1 5728b4d93.
