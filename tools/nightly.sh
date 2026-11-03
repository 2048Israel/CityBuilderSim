#!/bin/sh
# ---------------------------------------------------------------------------
#  The nightly long playtest: LongPlaytest for the full 4,000 months on
#  origin/main's tip, one verdict line. The gate plays 2,400 months; this is the
#  rest, once a night, so a fault past month 2,400 is met the next morning with
#  only that day's ships as suspects. Writes no commit and no branch. POSIX sh.
#
#      sh tools/nightly.sh     fetch origin, clone its main (from this checkout's own
#                              repository) to $TEMP/cbs-nightly, build it, play the
#                              months (about 5 minutes), say the verdict, delete the
#                              clone. The orchestrator runs it in the background when
#                              tools/day.sh --due says "nightly due"
#
#  The verdict, one line, is printed and written over the last to
#  $HOME/.cbs-nightly.txt (the home folder: ship.sh's temp clone on the PC reads
#  it too); the run's whole output is $HOME/.cbs-nightly.log:
#      2026-11-03 01:12 0.9.3 abc1234 4,000 months: OK in 152 s
#      2026-11-03 01:12 0.9.3 abc1234 4,000 months: RED, exit 1 in 150 s: <first finding>
#      2026-11-03 01:12 0.9.3 abc1234 4,000 months: FAILED to compile: <first error line>
#  A RED's text is the first line under "--- findings ---", else (a crash has none)
#  the last line of the output. tools/day.sh prints the line, ship.sh copies it into
#  docs/notes/gate.txt. As the gate does, the run names the main working tree's
#  data/earth-2.bin and data/world-1.bin (-Dcbs.earth, -Dcbs.world) unless
#  JDK_JAVA_OPTIONS is set: a run without them plays a different world.
#
#  Exit status: 0 OK; 1 RED; 2 not a git checkout, or an unknown option; 3 a step
#  before the run failed (fetch, clone, compile, classpath).
#
#  WHY (GT1, 2026-11-01, Jerus: "faster gates"): the gate's long playtest dropped
#  from 4,000 months to 2,400; the other 1,600 are played here, once a night, on
#  main's tip, not at every batch's gate.
# ---------------------------------------------------------------------------

MONTHS=4000
usage() { echo "usage: sh tools/nightly.sh   (no options)" >&2; }
case "$1" in "") ;; -h|--help) usage; exit 0 ;; *) usage; exit 2 ;; esac
[ $# -le 1 ] || { usage; exit 2; }
git rev-parse --git-dir >/dev/null 2>&1 || { echo "nightly   not in a git checkout" >&2; exit 2; }
ORIG=$(git rev-parse --show-toplevel); cd "$ORIG" || exit 2
COMMON=$(cd "$(git rev-parse --git-common-dir)" && pwd); TXT=$HOME/.cbs-nightly.txt; LOG=$HOME/.cbs-nightly.log
MAIN=$(git worktree list --porcelain | sed -n '1s/^worktree //p')
BASE=${TEMP:-${TMPDIR:-/tmp}}; command -v cygpath >/dev/null 2>&1 && BASE=$(cygpath -u "$BASE")
CLONE=$BASE/cbs-nightly; STEP=$BASE/cbs-nightly.step
NUM=$(printf '%s' "$MONTHS" | sed -e :a -e 's/\(.*[0-9]\)\([0-9]\{3\}\)/\1,\2/;ta')
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=';' ;; *) SEP=':' ;; esac
VER=; SHA=
rm -rf "$CLONE" "$STEP"
trap 'cd "$ORIG" 2>/dev/null; rm -rf "$CLONE" "$STEP"' EXIT   # from outside the clone (TD1: leftover temp folders held 37 GB)
trap 'exit 3' INT TERM HUP

clean() { sed 's/^ *//; s/  */ /g'; }
verdict() {   # $1 the verdict after "N months: ": printed, and written over the last
    printf '%s %s %s %s months: %s\n' "$(date '+%Y-%m-%d %H:%M')" "${VER:-?}" "${SHA:-?}" "$NUM" "$1" > "$TXT"
    cat "$TXT"
}
failed() {   # $1 the step, its output in $STEP: the first error line (else the last) in the verdict
    L=$(tr -d '\r' < "$STEP" | clean | grep .)
    E=$(printf '%s\n' "$L" | grep -m 1 -E 'ERROR|error|fatal'); [ -n "$E" ] || E=$(printf '%s\n' "$L" | tail -n 1)
    cat "$STEP" > "$LOG"; verdict "FAILED to $1: $(printf '%s' "${E:-no output}" | cut -c1-200)"; exit 3
}

GIT_TERMINAL_PROMPT=0 git fetch -q origin > "$STEP" 2>&1 || failed fetch
FULL=$(git rev-parse --verify -q origin/main 2>> "$STEP") || failed fetch
SHA=$(git rev-parse --short "$FULL")
git clone -q --no-checkout "$COMMON" "$CLONE" > "$STEP" 2>&1 && git -C "$CLONE" checkout -q --detach "$FULL" >> "$STEP" 2>&1 || failed clone
VER=$(sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p' "$CLONE/src/main/java/ham/citybuildersim/GameVersion.java" | head -n 1)
cd "$CLONE" || failed clone
mvn -q -B compile > "$STEP" 2>&1 || failed compile
mvn -q -B dependency:build-classpath -Dmdep.outputFile=target/classpath.txt > "$STEP" 2>&1 || failed classpath

set --
if [ -z "$JDK_JAVA_OPTIONS" ]; then
    for f in earth-2:cbs.earth world-1:cbs.world; do
        [ ! -f "$MAIN/data/${f%%:*}.bin" ] || set -- "$@" "-D${f#*:}=$MAIN/data/${f%%:*}.bin"
    done
fi
T0=$(date +%s)
java -Xmx2048m -Xms64m -Dcbs.timing=soft -Dstdout.encoding=UTF-8 -Dplaytest.months=$MONTHS "$@" \
    -cp "target/classes$SEP$(cat target/classpath.txt)" ham.citybuildersim.LongPlaytest > "$STEP" 2>&1
RC=$?; SECS=$(( $(date +%s) - T0 ))
tr -d '\r' < "$STEP" > "$LOG"
[ "$RC" != 0 ] || { verdict "OK in $SECS s"; exit 0; }
F=$(awk '/^--- findings ---/ { f = 1; next } f && NF { print; exit }' "$LOG" | clean)
case "$F" in ""|Nothing.*) F=$(grep . "$LOG" | tail -n 1 | clean) ;; esac
verdict "RED, exit $RC in $SECS s: $(printf '%s' "$F" | cut -c1-200)"
exit 1
