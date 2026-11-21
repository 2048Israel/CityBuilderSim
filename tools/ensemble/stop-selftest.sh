#!/bin/bash
# The ensemble's stop on Windows (HN1-C): `sh tools/ensemble/stop-selftest.sh` after stop.ps1 edits. A run with
# nothing left says so (exit 0); a survivor, here one a parent outside the run's folder restarts, is named (exit 1).
cd "$(dirname "$0")/../.." || exit 1
command -v powershell.exe > /dev/null 2>&1 || { echo "stop-selftest: no PowerShell here, skipped"; exit 0; }
fails=0
check() { if [ "$2" = 0 ]; then echo "ok   $1"; else echo "FAIL $1"; fails=$((fails + 1)); fi; }
mkdir -p target/ensemble; RUN=$(mktemp -d "$(pwd)/target/ensemble/stop-selftest.XXXXXX") || exit 1   # a run's place
LABEL=$(basename "$RUN")
stop() {   # as ensemble.sh --stop calls it; and ENS_A, which main's caller set, so a stop.ps1 reading it is tested
    ENS_LABEL="$LABEL" ENS_A="$RUN" ENS_B="$(cd "$RUN" && pwd -W)" powershell.exe -NoProfile \
        -ExecutionPolicy Bypass -File "$(pwd -W)/tools/ensemble/stop.ps1" | tr -d '\r' > "$RUN.out"
    echo "${PIPESTATUS[0]}"
}
# Not a run's own folder stops nothing (REV-FB15), read on a copy whose Stop-Process only says what it would stop, and
# only where stop.ps1 holds its refusal: before FB15 an empty folder matched every process on the PC. Never main's.
DRY="target/$LABEL.dry.ps1"
sed 's/Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue/Write-Output "WOULD-STOP $($_.ProcessId)"/' \
    tools/ensemble/stop.ps1 > "$DRY"
refused() {   # label, folder: the copy stops nothing and exits 2
    code=$(ENS_LABEL="$1" ENS_A= ENS_B="$2" powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$(pwd -W)/$DRY" \
        | tr -d '\r' > "$RUN.out"; echo "${PIPESTATUS[0]}")
    ! grep -q WOULD-STOP "$RUN.out" && [ "$code" = 2 ]
}
if grep -q 'nothing stopped' tools/ensemble/stop.ps1 && ! grep -q Stop-Process "$DRY"; then
    W=$(pwd -W)
    refused "" ""; check "an empty label and run folder stop nothing, exit 2" $?
    refused . "$W/target/ensemble"; check "...nor '.', the folder of every run (--stop .)" $?
    refused "$LABEL" "$W/target"; check "...nor the label with an ancestor's folder, all of target/" $?
else
    check "stop.ps1 holds its refusal, and its copy no Stop-Process, for the cases of no run's folder" 1
fi
for l in . .. a/b; do   # refused by ensemble.sh itself; with no powershell.exe on its PATH it could stop nothing anyway
    PATH=/usr/bin bash tools/ensemble/ensemble.sh --stop "$l" > "$RUN.out" 2>&1; code=$?
    grep -q "not a run's label" "$RUN.out"; check "ensemble.sh --stop $l is refused, exit 2 (exit $code)" $(($? + (code != 2)))
done
: > "$RUN/one.log"; : > "$RUN/again.log"
tail -f "$RUN/one.log" & one=$!
sleep 1
kill -0 "$one" 2> /dev/null; check "fixture: a process of the run, alive before the stop" $?
code=$(stop)
kill -0 "$one" 2> /dev/null; check "a run's process is stopped" $((1 - $?))
grep -qF "nothing of run $LABEL is left" "$RUN.out"; check "...and the stop says nothing is left (exit $code)" $(($? + code))
RUNDIR=$RUN sh -c 'while [ ! -f "$RUNDIR.done" ]; do tail -f "$RUNDIR/again.log" & c=$!
    while kill -0 $c 2> /dev/null && [ ! -f "$RUNDIR.done" ]; do sleep 0.2; done; done' & parent=$!
sleep 1
code=$(stop)
grep -q "^left: .*again.log" "$RUN.out"; check "a process back after both passes is named as left, exit 1 (exit $code)" $(($? + (code != 1)))
touch "$RUN.done"; wait "$parent"; stop > /dev/null
rm -rf "$RUN" "$RUN.out" "$RUN.done" "$DRY"
[ "$fails" = 0 ] && echo "stop-selftest: all passed" || echo "stop-selftest: $fails failed"
exit "$fails"
