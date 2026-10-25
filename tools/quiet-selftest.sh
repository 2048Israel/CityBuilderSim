#!/bin/sh
# ---------------------------------------------------------------------------
#  The ship gate's wait for a quiet minute, tried against real JVMs that come
#  and go: a runner that starts short JVMs one after another with a gap
#  between them, a fake game, and the waiters themselves. For whoever changes
#  the wait next (tools/jvms.ps1 -Quiet, quiet_minute in batch-check.sh, the
#  --timing of Batch check.bat): run it before you hand the change back.
#  POSIX sh and a java on the PATH; on Windows (Git Bash) PowerShell and cmd too.
#
#      sh tools/quiet-selftest.sh
#
#  The stand-ins are Nap.java, a program that sleeps and nothing more, run as
#  a single-file source program (java Nap.java <ms> ham.citybuildersim.QuietSelftest <mark>).
#  Its command line names a ham.citybuildersim class, so tools/jvms.ps1 and
#  ps count it as another batch's JVM; named ham.citybuildersim.CityBuilderSim
#  it counts as the game. Every look is narrowed to the command lines that carry
#  this run's mark (CBS_JVMS_MARK), so another lane's real JVMs on the machine,
#  or Jerus's game, cannot disturb it, and the dials are cut to a second a look
#  (CBS_QUIET_LOOKS, CBS_QUIET_SECS, CBS_QUIET_CAP_SECS). The waiters, one at a
#  time so that one runner JVM lives at a time:
#    sh   CBS_TIMING_WAIT_ONLY=1 ./batch-check.sh --timing   (every platform)
#    ps1  tools/jvms.ps1 -Quiet                              (Windows)
#    bat  CBS_TIMING_WAIT_ONLY=1 Batch check.bat --no-pause --timing  (Windows,
#         through a wrapper .cmd in the scratch area that calls the .bat by its full path)
#  The cases, each a line, PASS or FAIL:
#    1. the one look (Windows): one nap is "1 0", a fake game "0 1", nothing "0 0";
#    2. alone: each waiter exits 0 after the quiet looks, taking at least
#       (looks - 1) x seconds, says "N of N quiet looks" and no waiting line;
#    3. beside a runner whose gaps are longer than a look: it exits 0, not before
#       the runner's last JVM has ended, and says "the count starts over" at
#       least once - a look fell in a gap and was not taken for the end, which
#       the one-look rule of TM1 would have done;
#    4. the cap: beside a runner that outlasts it, exit 3 and "without the quiet minute";
#    5. the game: beside a fake game it does not wait, and says the game runs;
#       with CBS_QUIET_GAME=1 it waits until the game ends;
#    6. the .bat's refusal (Windows): "Batch check.bat --no-pause --pick" exits
#       2, says unknown option and does not say All clear (BP1).
#  Ends with "selftest: N of M passed"; the exit status is the number of fails,
#  2 if it could not start (no java). About three minutes. It writes only its
#  scratch area, ${TEMP:-/tmp}/cbs-quiet-selftest-<pid>, removed on exit, and
#  never a game save.
#
#  WHY (TM2, 2026-10-25): TM1's wait ended at the first look that found no JVM,
#  and a runner that starts its JVMs one after another (AllChecks, the
#  ensemble's runner) leaves a gap of a second or two between them; the gate
#  of 2026-10-18 ran its hard timing beside FR2's ensemble that way. The fix is
#  four empty looks in a row, and a wait is only believed when real JVMs have
#  come and gone under it, so this builds the gap on purpose.
# ---------------------------------------------------------------------------

R=$(cd "$(dirname "$0")/.." && pwd) || exit 2
cd "$R" || exit 2
command -v java >/dev/null 2>&1 || { echo "selftest: no java on the PATH: it runs Nap.java as a stand-in for a batch's JVM"; exit 2; }
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) WIN=1 ;; *) WIN=0 ;; esac
if [ "$WIN" = 1 ]; then
    command -v powershell.exe >/dev/null 2>&1 || { echo "selftest: no powershell.exe on the PATH"; exit 2; }
fi

# the scratch area; on Windows TEMP is a DOS path, which cd takes and pwd prints in the POSIX form
BASE=$(cd "${TEMP:-/tmp}" && pwd) || exit 2
MARK="cbs-quiet-selftest-$$"
D="$BASE/$MARK"
mkdir -p "$D" || exit 2
RPID=""
cleanup() {
    touch "$D/stop" 2>/dev/null
    [ -z "$RPID" ] || wait "$RPID" 2>/dev/null
    rm -rf "$D"
}
trap cleanup EXIT
trap 'exit 130' INT TERM
export CBS_JVMS_MARK="$MARK"

cat > "$D/Nap.java" <<'EOF'
public class Nap { public static void main(String[] a) throws Exception { Thread.sleep(Long.parseLong(a[0])); } }
EOF

WAITERS="sh"
if [ "$WIN" = 1 ]; then
    WAITERS="sh ps1 bat"
    # a wrapper .cmd that calls the .bat by its full Windows path, so cmd's quoting of the name with a space is not ours
    WRAPW=$(cygpath -w "$D/wrap.cmd")
    printf '@echo off\r\ncall "%s" --no-pause %%*\r\nexit /b %%errorlevel%%\r\n' "$(cygpath -w "$R/Batch check.bat")" > "$D/wrap.cmd"
fi

N=0; BAD=0
result() {   # result 0|1 label [why]
    N=$((N + 1))
    if [ "$1" = 0 ]; then echo "PASS  $2"; else BAD=$((BAD + 1)); echo "FAIL  $2${3:+ - $3}"; fi
}
now() { date +%s; }

# a JVM that does nothing for $1 ms; its command line names the class $2 (a batch's by default) and the mark
nap() { java "$D/Nap.java" "$1" "${2:-ham.citybuildersim.QuietSelftest}" "$MARK" >/dev/null 2>&1; }

# $1 naps of $2 ms one after another with $3 s between them, as a runner of short JVMs does; the end is stamped
# in $D/rend. A stop file ends it after the nap that is running.
runner() {
    rm -f "$D/rend" "$D/stop"
    (
        i=0
        while [ "$i" -lt "$1" ] && [ ! -f "$D/stop" ]; do
            [ "$i" = 0 ] || sleep "$3"
            nap "$2"
            i=$((i + 1))
        done
        now > "$D/rend"
    ) &
    RPID=$!
}
# a fake game for $1 ms; its end is stamped in $D/gend
gamenap() {
    rm -f "$D/gend"
    ( nap "$1" ham.citybuildersim.CityBuilderSim; now > "$D/gend" ) &
    RPID=$!
}
# the background job is over (the stop file ends a long runner first)
reap() { touch "$D/stop"; wait "$RPID" 2>/dev/null; RPID=""; rm -f "$D/stop"; }

# the wait as waiter $1 (sh, ps1, bat) runs it: its output, CRs stripped, in $2; its exit status is returned
waiter() {
    case "$1" in
        sh)  CBS_TIMING_WAIT_ONLY=1 sh ./batch-check.sh --timing > "$2.raw" 2>&1 ;;
        ps1) powershell.exe -NoProfile -ExecutionPolicy Bypass -File tools/jvms.ps1 -Quiet > "$2.raw" 2>&1 ;;
        bat) CBS_TIMING_WAIT_ONLY=1 cmd.exe //c "$WRAPW" --timing > "$2.raw" 2>&1 ;;
    esac
    WRC=$?
    tr -d '\r' < "$2.raw" > "$2"
    return "$WRC"
}
has() { grep -q -F -- "$2" "$1"; }   # has FILE TEXT
count() { grep -c -F -- "$2" "$1"; }

T0=$(now)
echo "--- quiet-selftest: the wait for a quiet minute, against real JVMs (mark $MARK) ---"

# ---- 1. the one look (Windows) ----
if [ "$WIN" = 1 ]; then
    look() { powershell.exe -NoProfile -ExecutionPolicy Bypass -File tools/jvms.ps1 | tr -d '\r'; }
    NONE=$(look)
    runner 1 5000 0; sleep 2; ONE=$(look); reap
    gamenap 5000; sleep 2; GAME=$(look); reap
    if [ "$NONE" = "0 0" ] && [ "$ONE" = "1 0" ] && [ "$GAME" = "0 1" ]; then
        result 0 "one look: nothing is '0 0', a nap '1 0', a fake game '0 1' (each once, the javapath shim's parent not counted again)"
    else
        result 1 "one look: nothing, a nap, a fake game" "saw '$NONE', '$ONE', '$GAME'; wanted '0 0', '1 0', '0 1'"
    fi
fi

# ---- 2. alone ----
export CBS_QUIET_LOOKS=3 CBS_QUIET_SECS=1 CBS_QUIET_CAP_SECS=120
for W in $WAITERS; do
    O="$D/alone-$W"
    t0=$(now); waiter "$W" "$O"; rc=$?; took=$(( $(now) - t0 ))
    if [ "$rc" = 0 ] && [ "$took" -ge 2 ] && has "$O" "3 of 3 quiet looks" && [ "$(count "$O" "waiting for a quiet minute")" = 0 ]; then
        result 0 "alone, $W: exit 0 after 3 quiet looks, $took s, no waiting line"
    else
        result 1 "alone, $W: exit 0 after 3 quiet looks, no waiting line" "exit $rc, $took s, output: $(tr '\n' '|' < "$O")"
    fi
done

# ---- 3. beside a runner whose gaps are longer than a look ----
export CBS_QUIET_LOOKS=5 CBS_QUIET_SECS=1 CBS_QUIET_CAP_SECS=120
for W in $WAITERS; do
    O="$D/runner-$W"
    runner 3 2000 3; sleep 1
    waiter "$W" "$O"; rc=$?; wend=$(now)
    wait "$RPID" 2>/dev/null; RPID=""; rend=$(cat "$D/rend" 2>/dev/null)
    starts=$(count "$O" "the count starts over")
    if [ "$rc" = 0 ] && [ -n "$rend" ] && [ "$wend" -ge "$rend" ] && [ "$starts" -ge 1 ] && ! has "$O" "without the quiet minute"; then
        result 0 "beside a runner, $W: waited for the runner's last JVM ($((wend - rend)) s after it), the count started over $starts time(s)"
    else
        result 1 "beside a runner, $W: exit 0 after the runner ended, the count started over" "exit $rc, ended at ${wend}, runner at ${rend:-never}, starts $starts, output: $(tr '\n' '|' < "$O")"
    fi
done

# ---- 4. the cap ----
export CBS_QUIET_LOOKS=5 CBS_QUIET_SECS=1 CBS_QUIET_CAP_SECS=6
for W in $WAITERS; do
    O="$D/cap-$W"
    runner 40 2000 1; sleep 1
    t0=$(now); waiter "$W" "$O"; rc=$?; took=$(( $(now) - t0 ))
    reap
    if [ "$rc" = 3 ] && has "$O" "without the quiet minute" && has "$O" "after 6 s" && [ "$took" -ge 6 ]; then
        result 0 "the cap, $W: beside a runner that outlasts it, exit 3 after $took s, 'without the quiet minute'"
    else
        result 1 "the cap, $W: exit 3 and 'without the quiet minute'" "exit $rc, $took s, output: $(tr '\n' '|' < "$O")"
    fi
done

# ---- 5. the game: not waited for, but said; waited for with CBS_QUIET_GAME=1 ----
export CBS_QUIET_LOOKS=3 CBS_QUIET_SECS=1 CBS_QUIET_CAP_SECS=120
for W in $WAITERS; do
    O="$D/game-$W"
    gamenap 10000; sleep 2
    waiter "$W" "$O"; rc=$?; wend=$(now)
    wait "$RPID" 2>/dev/null; RPID=""; gend=$(cat "$D/gend" 2>/dev/null)
    if [ "$rc" = 0 ] && [ -n "$gend" ] && [ "$wend" -lt "$gend" ] && has "$O" "the game is running: a timing red may be its load" \
            && ! has "$O" "waiting for a quiet minute"; then
        result 0 "the game, $W: not waited for (the wait ended $((gend - wend)) s before it), and said"
    else
        result 1 "the game, $W: not waited for, and said" "exit $rc, ended at ${wend}, game at ${gend:-never}, output: $(tr '\n' '|' < "$O")"
    fi
    O="$D/gamewait-$W"
    gamenap 6000; sleep 2
    export CBS_QUIET_GAME=1
    waiter "$W" "$O"; rc=$?; wend=$(now)
    unset CBS_QUIET_GAME
    wait "$RPID" 2>/dev/null; RPID=""; gend=$(cat "$D/gend" 2>/dev/null)
    if [ "$rc" = 0 ] && [ -n "$gend" ] && [ "$wend" -ge "$gend" ] && has "$O" "the game running"; then
        result 0 "the game with CBS_QUIET_GAME=1, $W: waited until the fake game ended ($((wend - gend)) s after it)"
    else
        result 1 "the game with CBS_QUIET_GAME=1, $W: exit 0 only after the fake game ended" "exit $rc, ended at ${wend}, game at ${gend:-never}, output: $(tr '\n' '|' < "$O")"
    fi
done

# ---- 6. the .bat's refusal of an unknown option (Windows) ----
if [ "$WIN" = 1 ]; then
    O="$D/refuse"
    cmd.exe //c "$WRAPW" --pick > "$O.raw" 2>&1; rc=$?
    tr -d '\r' < "$O.raw" > "$O"
    if [ "$rc" = 2 ] && has "$O" "unknown option --pick" && ! has "$O" "All clear"; then
        result 0 "Batch check.bat --no-pause --pick: exit 2, unknown option, no 'All clear'"
    else
        result 1 "Batch check.bat --no-pause --pick: exit 2, unknown option, no 'All clear'" "exit $rc, output: $(tr '\n' '|' < "$O")"
    fi
fi

echo "(took $(( $(now) - T0 )) s)"
echo "selftest: $((N - BAD)) of $N passed"
exit "$BAD"
