#!/bin/bash
# The ensemble's stop on Windows (HN1-C): `sh tools/ensemble/stop-selftest.sh` after stop.ps1 edits. A run with
# nothing left says so (exit 0); a survivor, here one a parent outside the run's folder restarts, is named (exit 1).
cd "$(dirname "$0")/../.." || exit 1
command -v powershell.exe > /dev/null 2>&1 || { echo "stop-selftest: no PowerShell here, skipped"; exit 0; }
fails=0
check() { if [ "$2" = 0 ]; then echo "ok   $1"; else echo "FAIL $1"; fails=$((fails + 1)); fi; }
mkdir -p target; RUN=$(mktemp -d "$(pwd)/target/stop-selftest.XXXXXX") || exit 1   # where a run is, target/; /tmp shows as /tmp
stop() {
    ENS_LABEL=stop-selftest ENS_B="$(cd "$RUN" && pwd -W)" powershell.exe -NoProfile \
        -ExecutionPolicy Bypass -File "$(pwd -W)/tools/ensemble/stop.ps1" | tr -d '\r' > "$RUN.out"
    echo "${PIPESTATUS[0]}"
}
: > "$RUN/one.log"; : > "$RUN/again.log"
tail -f "$RUN/one.log" & one=$!
sleep 1
kill -0 "$one" 2> /dev/null; check "fixture: a process of the run, alive before the stop" $?
code=$(stop)
kill -0 "$one" 2> /dev/null; check "a run's process is stopped" $((1 - $?))
grep -q "^nothing of run stop-selftest is left" "$RUN.out"; check "...and the stop says nothing is left (exit $code)" $(($? + code))
RUNDIR=$RUN sh -c 'while [ ! -f "$RUNDIR.done" ]; do tail -f "$RUNDIR/again.log" & c=$!
    while kill -0 $c 2> /dev/null && [ ! -f "$RUNDIR.done" ]; do sleep 0.2; done; done' & parent=$!
sleep 1
code=$(stop)
grep -q "^left: .*again.log" "$RUN.out"; check "a process back after both passes is named as left, exit 1 (exit $code)" $(($? + (code != 1)))
touch "$RUN.done"; wait "$parent"; stop > /dev/null
rm -rf "$RUN" "$RUN.out" "$RUN.done"
[ "$fails" = 0 ] && echo "stop-selftest: all passed" || echo "stop-selftest: $fails failed"
exit "$fails"
