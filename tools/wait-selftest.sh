#!/bin/sh
# ---------------------------------------------------------------------------
#  tools/wait.sh --ship tried against a throwaway local origin, with no JVM:
#  a ship file pushed to a batch/ branch ends the wait, and nothing else does.
#  For whoever changes tools/wait.sh next. POSIX sh and git, about 20 seconds.
#
#      sh tools/wait-selftest.sh               test the tools/wait.sh of the current checkout
#
#  CBS_WAITTEST_DIR names the area it builds (default ${TEMP:-/tmp}/cbs-wait-selftest;
#  it refuses one that exists and is not its own), CBS_WAITTEST_KEEP=1 leaves it
#  behind. It builds a bare origin.git, work (where the branches are written and
#  pushed) and orch (the orchestrator's clone, where the wait runs, polling every
#  second). It never pushes anywhere but origin.git. Exit status: the number of
#  failed cases, so 0 is green.
#
#  WHY (GP1, 2026-10-27): the wait that starts a gate when a ship file lands is
#  the one thing between a ready branch and a gate that waited for the hourly
#  check-in; a wait that ends on the wrong push wakes the orchestrator for
#  nothing, and one that does not end on the right one is the hour again.
# ---------------------------------------------------------------------------

SRC=$(cd "$(dirname "$0")/.." && pwd) || exit 2
[ -f "$SRC/tools/wait.sh" ] || { echo "selftest: no tools/wait.sh in $SRC"; exit 2; }
TA=${CBS_WAITTEST_DIR:-${TEMP:-/tmp}/cbs-wait-selftest}
mkdir -p "$(dirname "$TA")" && TA=$(cd "$(dirname "$TA")" && pwd)/$(basename "$TA")
if [ -e "$TA" ] && [ ! -d "$TA/origin.git" ] && [ -n "$(ls -A "$TA" 2>/dev/null)" ]; then
    echo "selftest: $TA exists and is not a selftest area: name another with CBS_WAITTEST_DIR"; exit 2
fi
FAILS=0; N=0
ok() { N=$((N + 1)); if "$@" >/dev/null 2>&1; then echo "PASS  $LABEL"; else echo "FAIL  $LABEL"; FAILS=$((FAILS + 1)); fi; }
ship_wait() { ( cd "$TA/orch" && CBS_WAIT_ROW_SECS=1 sh "$SRC/tools/wait.sh" "$@" ) > "$TA/out.txt" 2>&1; RC=$?; }   # status in RC, the line in out.txt
none_ready() { [ "$RC" = 3 ] && grep -q -F "no batch branch ready to ship" "$TA/out.txt"; }
ready_is() { [ "$RC" = 0 ] && grep -q -F "wait      done - ready to ship: $1 (./ship.sh batch/<id>;" "$TA/out.txt"; }
W() { git -C "$TA/work" "$@"; }
branch() {   # branch NAME FILE...: a commit adding the files, on a branch cut from main, pushed
    b=$1; shift; W checkout -q -B "$b" origin/main
    for f in "$@"; do mkdir -p "$TA/work/$(dirname "$f")"; echo "$f" > "$TA/work/$f"; W add "$f"; done
    W commit -q -m "selftest: $*" && W push -q -f origin "HEAD:refs/heads/$b"
}
main_moves() { W checkout -q -B main origin/main; echo more >> "$TA/work/README"; W commit -q -am "main moves" && W push -q origin main; }

rm -rf "$TA"; mkdir -p "$TA"; cd "$TA" || exit 1
git init -q --bare origin.git && git -C origin.git symbolic-ref HEAD refs/heads/main
git init -q work && W symbolic-ref HEAD refs/heads/main && W remote add origin "$TA/origin.git"
W config user.name selftest; W config user.email selftest@example.invalid; W config core.autocrlf false
echo main > work/README && printf '## Queued\n\n| batch | what | brief |\n|---|---|---|\n| P1 | a predecessor | x |\n| S1 | its successor | x |\n| P2 | another | x |\n\n## Pending\n' > work/QUEUE.md
W add README QUEUE.md && W commit -q -m "main" && W push -q origin main
git clone -q origin.git orch 2>/dev/null; git -C orch config user.name selftest; git -C orch config user.email selftest@example.invalid

echo "--- nothing but a ship file ends it ---"
LABEL="no batch branch on origin: one look, gave up, exit 3"; ship_wait --ship --for 0; ok none_ready
branch batch/W1 docs/queue/W1-notes.md
LABEL="a batch branch pushed with no ship file does not end it"; ship_wait --ship --for 0; ok none_ready
branch batch/W3 docs/queue/OTHER-ship.md
LABEL="a ship file of another id on a batch branch does not end it"; ship_wait --ship --for 0; ok none_ready
branch chatgpt/W2 docs/queue/W2-ship.md
LABEL="a ship file on a branch that is not batch/ does not end it"; ship_wait --ship --for 0; ok none_ready
main_moves
LABEL="a push to main does not end it"; ship_wait --ship --for 0; ok none_ready

echo "--- a ship file pushed ends it ---"
( cd "$TA/orch" && CBS_WAIT_ROW_SECS=1 sh "$SRC/tools/wait.sh" --ship --for 3 ) > "$TA/bg.txt" 2>&1 &
BG=$!; sleep 2
main_moves; branch batch/W6 docs/queue/W6-notes.md; sleep 4
waiting() { [ ! -s "$TA/bg.txt" ] && kill -0 "$BG" 2>/dev/null; }
LABEL="still waiting in the background, though main and a batch branch were pushed meanwhile"; ok waiting
W checkout -q batch/W1; echo ship > "$TA/work/docs/queue/W1-ship.md"; W add docs/queue/W1-ship.md; W commit -q -m "W1: the ship file, last"; W push -q origin batch/W1
n=0; while [ ! -s "$TA/bg.txt" ] && [ "$n" -lt 30 ]; do sleep 1; n=$((n + 1)); done
wait $BG; BGRC=$?
ended() { [ "$BGRC" = 0 ] && [ "$n" -lt 15 ] && grep -q -F "wait      done - ready to ship: W1 (./ship.sh batch/<id>;" "$TA/bg.txt"; }
LABEL="the ship file pushed ends it within seconds: exit 0, the id and the command"; ok ended
LABEL="a branch that is ready already ends it at once"; ship_wait --ship --for 0; ok ready_is W1
LABEL="--skip names a branch the gate is holding: skipped, it is not ready"; ship_wait --ship --skip W1 --for 0; ok none_ready
branch batch/W5 docs/queue/W5-ship.md
LABEL="two ready: one line names both"; ship_wait --ship --for 0; ok ready_is "W1 W5"
LABEL="...and --skip takes one out"; ship_wait --ship --skip W1 --for 0; ok ready_is W5

echo "--- and no more once it is shipped or gone ---"
W checkout -q -B main origin/main; W merge -q --no-ff -m "Merge batch/W1" origin/batch/W1; W push -q origin main
LABEL="a branch merged into main is not ready"; ship_wait --ship --skip W5 --for 0; ok none_ready
W push -q origin :refs/heads/batch/W5
LABEL="a branch deleted on origin is not ready"; ship_wait --ship --for 0; ok none_ready
LABEL="--skip without --ship is a usage error: exit 2"; ship_wait --skip W1; ok test "$RC" = 2

echo "--- a successor cut from its predecessor's tip (RL1) ---"
O() { git -C "$TA/orch" "$@"; }
row_wait() { ( cd "$TA/orch" && CBS_WAIT_ROW_SECS=1 sh "$SRC/tools/wait.sh" --row "$@" --for 0 ) > "$TA/out.txt" 2>&1; RC=$?; }
still() { [ "$RC" = 3 ] && grep -q -F "still queued: $1" "$TA/out.txt"; }
branch batch/P1 docs/queue/P1-ship.md
O fetch -q origin; O checkout -q -B batch/S1 origin/batch/P1; mkdir -p "$TA/orch/docs/queue"; echo s > "$TA/orch/s.txt"; O add s.txt; O commit -q -m "S1: its own work"
LABEL="--row P1 from S1, built on P1's ready tip: P1 ready is not P1 landed, still waiting"; row_wait P1; ok still P1
echo ship > "$TA/orch/docs/queue/S1-ship.md"; O add docs/queue/S1-ship.md; O commit -q -m "S1: the ship file"; O push -q origin batch/S1
LABEL="S1's ship file, pushed while P1 is not on main, makes S1 waiting: only P1 is ready"; ship_wait --ship --for 0; ok ready_is P1
W checkout -q batch/P1; W rm -q docs/queue/P1-ship.md; W commit -q -m "P1: sent back, ship file off"; W push -q origin batch/P1
LABEL="P1 sent back (ship file off): --row P1 does not end"; row_wait P1; ok still P1
echo fix > "$TA/work/fix.txt"; echo ship > "$TA/work/docs/queue/P1-ship.md"; W add -A; W commit -q -m "P1: mended, ship file last"; W push -q origin batch/P1
LABEL="P1 mended, ready again with commits S1 lacks: --row P1 ends (exit 0, the id, the merge)"; row_wait P1
ok sh -c "[ $RC = 0 ] && grep -q -F 'wait      done - P1 ready again with commits HEAD lacks: git merge origin/batch/<id>' $TA/out.txt"
O merge -q --no-edit origin/batch/P1
LABEL="...and once S1 has merged the fix it waits again"; row_wait P1; ok still P1
W checkout -q -B main origin/main; W merge -q --no-ff --no-commit origin/batch/P1 >/dev/null; sed -i '/^| P1 |/d' "$TA/work/QUEUE.md"; W rm -q -f docs/queue/P1-ship.md; W add -A; W commit -q -m "Merge batch/P1"; W push -q origin main
LABEL="P1 landed: S1 is ready"; ship_wait --ship --for 0; ok ready_is S1
LABEL="P1 landed: --row P1 ends, and says S1's HEAD lacks origin/main"; row_wait P1
ok sh -c "[ $RC = 0 ] && grep -q -F 'done - P1 out of the Queued table' $TA/out.txt && grep -q -F 'HEAD lacks origin/main: merge it first' $TA/out.txt"
branch batch/P2 docs/queue/P2-ship.md; O fetch -q origin; O checkout -q -B batch/N origin/main
LABEL="--row P2 from a branch not built on P2: P2 ready does not end it"; row_wait P2; ok still P2

echo "selftest: $((N - FAILS)) of $N passed"
[ -n "$CBS_WAITTEST_KEEP" ] || { cd / && rm -rf "$TA"; }
exit "$FAILS"
