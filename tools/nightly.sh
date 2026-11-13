#!/bin/sh
# ---------------------------------------------------------------------------
#  Nightly on origin/main's tip: 4,000 months, the nightly tier, hard timing.
#  One verdict in $HOME/.cbs-nightly.txt, detail in .cbs-nightly.log; figures
#  and commit per machine/night in $HOME/.cbs-timing (GR1). No commits or saves.
#  WHY (HA1, GR1): heavy checks belong nightly; gate timing named no faulty batch.
#  Exit: 0 OK, 1 RED, 2 not a checkout or an option, 3 a step before the run failed.
#  WHY (GT1, 2026-11-01, Jerus: "do we need 4k months?"): the gate plays 2,400.
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
. "$ORIG/tools/nightly-timing.sh"
nightly_lock "$HOME/.cbs-nightly.lock" || { echo 'nightly already running or lock unavailable (.cbs-nightly.lock)'; exit 3; }
CLONE=$(mktemp -d "$BASE/cbs-nightly-XXXXXXXX") || { nightly_unlock; exit 3; }
STEP=$CLONE.step
NUM=$(printf '%s' "$MONTHS" | sed -e :a -e 's/\(.*[0-9]\)\([0-9]\{3\}\)/\1,\2/;ta')
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=';' ;; *) SEP=':' ;; esac
VER=; SHA=
trap 'cd "$ORIG" 2>/dev/null; rm -rf "$CLONE" "$STEP"; nightly_unlock' EXIT
trap 'exit 3' INT TERM HUP

clean() { sed 's/^ *//; s/  */ /g'; }
verdict() {   # $1 the verdict after "N months: ": printed, and written over the last
    printf '%s %s %s %s months: %s%s%s\n' "$(date '+%Y-%m-%d %H:%M')" "${VER:-?}" "${SHA:-?}" "$NUM" "$1" "${TIER:+; $TIER}" "${TIMING:+; $TIMING}" > "$TXT"
    cat "$TXT"
}
failed() {   # $1 the step, its output in $STEP: the first error line (else the last) in the verdict
    L=$(tr -d '\r' < "$STEP" | clean | grep .)
    E=$(printf '%s\n' "$L" | grep -m 1 -E 'ERROR|error|fatal'); [ -n "$E" ] || E=$(printf '%s\n' "$L" | tail -n 1)
    cat "$STEP" >> "$LOG"; verdict "FAILED to $1: $(printf '%s' "${E:-no output}" | cut -c1-200)"; exit 3
}

GIT_TERMINAL_PROMPT=0 git fetch -q origin > "$STEP" 2>&1 || failed fetch
FULL=$(git rev-parse --verify -q origin/main 2>> "$STEP") || failed fetch
SHA=$(git rev-parse --short "$FULL")
git clone -q --no-checkout "$COMMON" "$CLONE" > "$STEP" 2>&1 && git -C "$CLONE" checkout -q --detach "$FULL" >> "$STEP" 2>&1 || failed clone
VER=$(sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p' "$CLONE/src/main/java/ham/citybuildersim/GameVersion.java" | head -n 1)
cd "$CLONE" || failed clone
mvn -q -B compile > "$STEP" 2>&1 || failed compile
mvn -q -B dependency:build-classpath -Dmdep.outputFile=target/classpath.txt > "$STEP" 2>&1 || failed classpath

nightly_data
T0=$(date +%s)
java -Xmx2048m -Xms64m -Dcbs.timing=soft -Dstdout.encoding=UTF-8 -Dplaytest.months=$MONTHS \
    -cp "target/classes$SEP$(cat target/classpath.txt)" ham.citybuildersim.LongPlaytest > "$STEP" 2>&1
RC=$?; SECS=$(( $(date +%s) - T0 ))
tr -d '\r' < "$STEP" >> "$LOG"
PLAY="OK in $SECS s"
if [ "$RC" != 0 ]; then
    F=$(awk '/^--- findings ---/ { f = 1; next } f && NF { print; exit }' "$LOG" | clean)
    case "$F" in ""|Nothing.*) F=$(grep . "$LOG" | tail -n 1 | clean) ;; esac
    PLAY="RED, exit $RC in $SECS s: $(printf '%s' "$F" | cut -c1-200)"
fi
TIMING='timing FAILED: incomplete run'
nightly_tier; NRC=$?
nightly_observations >> "$LOG" 2>&1; ORC=$?
nightly_timing >> "$LOG" 2>&1; TRC=$?
[ -z "$NIGHT" ] || cp "$LOG" "$NIGHT/nightly.log"
verdict "$PLAY"
[ "$RC" = 0 ] && [ "$NRC" = 0 ] && [ "$ORC" = 0 ] && [ "$TRC" = 0 ]
