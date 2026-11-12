#!/bin/sh
# Fresh-clone gate with retained logs. WHY (GF1, LM1): temp launchers hid ship's exit and lost stopped runs.
# tools/gate.sh ID [ship.sh options], or tools/gate.sh --stop RUN_DIRECTORY
# --red-lines FILE adds one --red-line per nonempty line. CBS_GATE_MAIN names the main checkout.
set -u
HERE=$(cd "$(dirname "$0")" && pwd)
WINDOWS=0; case "$(uname -s)" in MINGW*|MSYS*) WINDOWS=1 ;; esac
tree_stop() {
    kill -STOP "$1" 2>/dev/null || return 0
    for child in $(pgrep -P "$1"); do tree_stop "$child"; done
    kill -KILL "$1" 2>/dev/null || :
}
if [ "${1:-}" = --stop ]; then
    [ "$#" = 2 ] && [ -f "$2/active" ] || { echo 'gate: name an active run directory'; exit 2; }
    RUN=$(cd "$2" && pwd)
    touch "$RUN/cancelled"
    if [ "$WINDOWS" = 1 ]; then
        powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$(cygpath -w "$HERE/gate-process.ps1")" -Mode Stop -Run "$(cygpath -w "$RUN")" || exit 1
    else
        tree_stop "$(cat "$RUN/process")"
    fi
    echo "gate: stopped $RUN; main and the tags are where they were (see retained log)"
    exit 0
fi
[ "$#" -gt 0 ] || { echo 'usage: tools/gate.sh ID [ship.sh options] | --stop RUN_DIRECTORY'; exit 2; }
ID=$1; shift
case "$ID" in ''|*[!A-Za-z0-9_-]*) echo 'gate: invalid batch id'; exit 2 ;; esac
SOURCE=$(cd "$HERE/.." && git rev-parse --path-format=absolute --git-common-dir) || exit 1
MAIN=${CBS_GATE_MAIN:-$(dirname "$SOURCE")}
URL=$(git -C "$MAIN" remote get-url origin) || exit 1
RUN=$(mktemp -d "${TMP:-${TMPDIR:-/tmp}}/cbs-gate-$ID-XXXXXXXX") || exit 1
RUN=$(cd "$RUN" && pwd)
CLONE="$RUN/clone"; LOG="$RUN/ship.log"
echo "gate: run $RUN"
echo "gate: stop with sh tools/gate.sh --stop '$RUN'"
# MSYS tracks parents across exec, whereas Win32's parent PID can name an already-exited exec helper.
if [ "$WINDOWS" = 1 ]; then
    ANCESTOR=$$
    while [ "$ANCESTOR" -gt 1 ]; do
        ROW=$(ps -p "$ANCESTOR" | awk 'NR == 2 {print $2 " " $4}')
        [ -n "$ROW" ] || break
        ANCESTOR=${ROW% *}; echo "${ROW##* }" >> "$RUN/owners"
    done
fi
# Options are kept as arguments throughout, including spaces in approval text and filenames.
REDFILE=""; N=$#
while [ "$N" -gt 0 ]; do
    ARG=$1; shift; N=$((N - 1))
    if [ "$ARG" = --red-lines ]; then
        [ "$N" -gt 0 ] && [ -f "$1" ] || { echo 'gate: --red-lines needs a file'; exit 2; }
        REDFILE=$1; shift; N=$((N - 1))
    else set -- "$@" "$ARG"; fi
done
if [ -n "$REDFILE" ]; then
    while IFS= read -r line || [ -n "$line" ]; do
        [ -z "$line" ] || set -- --red-line "$line" "$@"
    done < "$REDFILE"
fi
git clone -q --no-hardlinks "$MAIN" "$CLONE" > "$LOG" 2>&1 &&
git -C "$CLONE" remote set-url origin "$URL" &&
git -C "$CLONE" fetch -q origin &&
git -C "$CLONE" checkout -q -B main origin/main || { tail -16 "$LOG"; exit 1; }
git -C "$CLONE" ls-remote origin refs/heads/main "refs/heads/batch/$ID" 'refs/tags/*' > "$RUN/refs.before" || exit 1
# A batch changing the publisher must exercise that publisher, before its merge rewrites ship.sh.
git -C "$CLONE" show "origin/batch/$ID:ship.sh" > "$RUN/ship.sh" || exit 1
MAINPATH=$MAIN
[ "$WINDOWS" = 0 ] || MAINPATH=$(cygpath -m "$MAIN")
export JDK_JAVA_OPTIONS="${JDK_JAVA_OPTIONS:-} -Dcbs.earth=$MAINPATH/data/earth-2.bin -Dcbs.world=$MAINPATH/data/world-1.bin"
export CBS_GATE_RUN=$RUN
export CBS_SHIP_HOME=$CLONE
touch "$RUN/active"
( cd "$CLONE" && exec sh "$RUN/ship.sh" "batch/$ID" "$@" ) >> "$LOG" 2>&1 &
SHIP=$!
if [ "$WINDOWS" = 1 ]; then
    powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$(cygpath -w "$HERE/gate-process.ps1")" -Mode Watch -Run "$(cygpath -w "$RUN")" > "$RUN/watch.log" 2>&1 &
else
    echo "$SHIP" > "$RUN/process"
    ( while kill -0 "$SHIP" 2>/dev/null; do
        if ! kill -0 $$ 2>/dev/null || [ -f "$RUN/cancelled" ]; then tree_stop "$SHIP"; break; fi
        sleep 1
      done ) > "$RUN/watch.log" 2>&1 &
fi
GUARD=$!
trap 'touch "$RUN/cancelled"' INT TERM HUP
wait "$SHIP"; CODE=$?
if [ -f "$RUN/cancelled" ]; then
    CODE=130
    wait "$GUARD" || :
else
    rm -f "$RUN/active"
    wait "$GUARD" || :
fi
rm -f "$RUN/active"
[ ! -f "$RUN/watch.log" ] || cat "$RUN/watch.log" >> "$LOG"
git -C "$CLONE" rev-parse HEAD > "$RUN/head.after"
git -C "$CLONE" ls-remote origin refs/heads/main "refs/heads/batch/$ID" 'refs/tags/*' > "$RUN/refs.after"
echo "$CODE" > "$RUN/exit"
printf '[%s] gate      exit %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$CODE" >> "$LOG"
tail -16 "$LOG"
echo "gate: exit $CODE; log $LOG; unnamed lines $RUN/unnamed.txt"
# Keep stopped clones too, for inspecting HEAD and the unfinished merge. Logs always survive.
if [ "$CODE" = 0 ]; then
    [ ! -d "$CLONE/target/checks" ] || cp -R "$CLONE/target/checks" "$RUN/checks"
    git -C "$CLONE" worktree remove --force target/main-timing >/dev/null 2>&1 || :
    rm -rf "$CLONE"
fi
exit "$CODE"
