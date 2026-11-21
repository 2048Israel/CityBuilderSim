#!/bin/sh
# Fresh-clone gate with retained logs. WHY (GF1, LM1): temp launchers hid ship's exit and lost stopped runs.
# tools/gate.sh ID [ship.sh options], or tools/gate.sh --stop RUN_DIRECTORY
# CBS_GATE_DIRECTORY names a new retained run directory; --reuse RUN is passed to the publisher.
# --branch-gate tests the branch publisher with --dry-run only. --red-lines FILE adds one --red-line per nonempty line. CBS_GATE_MAIN names the main checkout.
set -u
HERE=$(cd "$(dirname "$0")" && pwd)
WINDOWS=0; case "$(uname -s)" in MINGW*|MSYS*) WINDOWS=1 ;; esac
# GB1: a gate box (CBS_GATE_HOST, else ~/.cbs-gate-box) runs the whole call, through tools/gate-box.sh; CBS_GATE_HOST= runs it here.
[ -n "${CBS_GATE_ON_BOX:-}" ] || [ -z "${CBS_GATE_HOST-$(cat "$HOME/.cbs-gate-box" 2>/dev/null)}" ] || exec sh "$HERE/gate-box.sh" "$@"
tree_stop() {
    kill -STOP "$1" 2>/dev/null || return 0
    for child in $(pgrep -P "$1"); do tree_stop "$child"; done
    kill -KILL "$1" 2>/dev/null || :
}
if [ "${1:-}" = --stop ]; then
    [ "$#" = 2 ] && [ -f "$2/active" ] || { echo 'gate: name an active run directory'; exit 2; }
    RUN=$(cd "$2" && pwd)
    echo --stop > "$RUN/cancelled"
    if [ "$WINDOWS" = 1 ]; then
        powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$(cygpath -w "$HERE/gate-process.ps1")" -Mode Stop -Run "$(cygpath -w "$RUN")" || exit 1
    else
        tree_stop "$(cat "$RUN/process")"
    fi
    sh "$HERE/gate-report.sh" "$RUN"
    exit 0
fi
[ "$#" -gt 0 ] || { echo 'usage: tools/gate.sh ID [ship.sh options] | --stop RUN_DIRECTORY'; exit 2; }
ID=$1; shift
case "$ID" in ''|*[!A-Za-z0-9_-]*) echo 'gate: invalid batch id'; exit 2 ;; esac
SOURCE=$(cd "$HERE/.." && git rev-parse --path-format=absolute --git-common-dir) || exit 1
MAIN=${CBS_GATE_MAIN:-$(dirname "$SOURCE")}
URL=$(git -C "$MAIN" remote get-url origin) || exit 1
if [ -n "${CBS_GATE_DIRECTORY:-}" ]; then
    RUN=$CBS_GATE_DIRECTORY
    mkdir "$RUN" || exit 1
else
    RUN=$(mktemp -d "${TMP:-${TMPDIR:-/tmp}}/cbs-gate-$ID-XXXXXXXX") || exit 1
fi
RUN=$(cd "$RUN" && pwd)
unset CBS_GATE_DIRECTORY
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
REDFILE=""; BRANCH_GATE=0; DRY_REQUESTED=0; REUSED=""; N=$#
while [ "$N" -gt 0 ]; do
    ARG=$1; shift; N=$((N - 1))
    case "$ARG" in
        --)
            set -- "$@" --
            while [ "$N" -gt 0 ]; do ARG=$1; shift; N=$((N - 1)); set -- "$@" "$ARG"; done ;;
        --branch-gate) BRANCH_GATE=1 ;;
        --dry-run) DRY_REQUESTED=1; set -- "$@" "$ARG" ;;
        --red-lines)
            [ "$N" -gt 0 ] && [ -f "$1" ] || { echo 'gate: --red-lines needs a file'; exit 2; }
            REDFILE=$1; shift; N=$((N - 1)) ;;
        --reuse|--red-ok|--red-line|--version|-m|-F)
            [ "$N" -gt 0 ] || { echo "gate: $ARG needs a value"; exit 2; }
            [ "$ARG" != --reuse ] || REUSED=$1
            set -- "$@" "$ARG" "$1"; shift; N=$((N - 1)) ;;
        *) set -- "$@" "$ARG" ;;
    esac
done
[ "$BRANCH_GATE" = 0 ] || [ "$DRY_REQUESTED" = 1 ] || { echo 'gate: --branch-gate requires --dry-run'; exit 2; }
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
echo cbs-gate-v1 > "$RUN/launcher"
git -C "$CLONE" remote get-url origin > "$RUN/origin"
git -C "$CLONE" rev-parse "origin/batch/$ID" > "$RUN/branch.commit" || exit 1
git -C "$CLONE" rev-parse origin/main > "$RUN/main.commit"
echo "refs/heads/batch/$ID" > "$RUN/branch"
git -C "$CLONE" rev-parse HEAD > "$RUN/head.before"
CBS_GATE_SCRIPT=./ship.sh
if [ "$BRANCH_GATE" = 1 ]; then
    git -C "$CLONE" show "origin/batch/$ID:ship.sh" > "$RUN/publisher.sh" || exit 1
    CBS_GATE_SCRIPT="$RUN/publisher.sh"
fi
export CBS_GATE_SCRIPT
# A stable wrapper gives the guardian one process identity even when an older publisher execs its copy.
cat > "$RUN/ship.sh" <<'WRAPPER'
#!/bin/sh
case "$(uname -s)" in MINGW*|MSYS*) ps -p $$ | awk 'NR == 2 {print $4}' > "$CBS_GATE_RUN/ship.native" ;; esac
for attempt in $(seq 1 100); do
    if [ -f "$CBS_GATE_RUN/guardian.ready" ] && [ -f "$CBS_GATE_RUN/guardian" ] && kill -0 "$(cat "$CBS_GATE_RUN/guardian")" 2>/dev/null; then break; fi
    sleep 0.1
done
[ -f "$CBS_GATE_RUN/guardian.ready" ] || { echo 'gate: guardian did not start'; exit 1; }
sh "$CBS_GATE_SCRIPT" "$@"
RESULT=$?
exit "$RESULT"
WRAPPER
# BW1: where the gate is, in MemWatch's state\gate.txt and the batch's own file; ship.sh posts the steps between.
bus() { sh "$HERE/bus.sh" "$@" > /dev/null 2>> "$LOG"; }
version() { sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p' "$CLONE/src/main/java/ham/citybuildersim/GameVersion.java" | head -1; }
CBS_GATE_NUMBER=1   # this batch's kept runs here, this one counted
for d in "$(dirname "$RUN")/cbs-gate-$ID-"????????; do [ ! -d "$d" ] || [ "$d" = "$RUN" ] || CBS_GATE_NUMBER=$((CBS_GATE_NUMBER + 1)); done
DRY=no; [ "$DRY_REQUESTED" = 0 ] || DRY=yes
WHERE=$LOG; [ "$WINDOWS" = 0 ] || WHERE=$(cygpath -m "$LOG")
CBS_ME=${CBS_ME:-gate}; export CBS_ME CBS_GATE_NUMBER
bus gate "$ID" merge --run "$CBS_GATE_NUMBER" commit="$(cat "$RUN/branch.commit")" base="$(cat "$RUN/main.commit")" version="$(version)" \
    machine="$(uname -n)" log="$WHERE" dry_run=$DRY pid="$(head -1 "$RUN/owners" 2>/dev/null || echo $$)" \
    started="$(date +%Y-%m-%dT%H:%M:%S%:z)" outcome= published= reused= skipped= timed_out= known_reds=
[ "$DRY" = yes ] || bus state "$ID" gating
. "$HERE/check-data.sh"; cbs_check_data "$MAIN"
export CBS_GATE_RUN=$RUN
export CBS_SHIP_HOME=$CLONE
touch "$RUN/active"
( cd "$CLONE" && exec sh "$RUN/ship.sh" "batch/$ID" "$@" ) >> "$LOG" 2>&1 &
SHIP=$!
if [ "$WINDOWS" = 1 ]; then
    powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$(cygpath -w "$HERE/gate-process.ps1")" -Mode Watch -Run "$(cygpath -w "$RUN")" > "$RUN/watch.log" 2>&1 &
else
    echo "$SHIP" > "$RUN/process"
    ( touch "$RUN/guardian.ready"
      while kill -0 "$SHIP" 2>/dev/null; do
        if ! kill -0 $$ 2>/dev/null || [ -f "$RUN/cancelled" ]; then
            [ -f "$RUN/cancelled" ] || echo caller > "$RUN/cancelled"
            tree_stop "$SHIP"; sh "$HERE/gate-report.sh" "$RUN"; break
        fi
        sleep 1
      done ) > "$RUN/watch.log" 2>&1 &
fi
GUARD=$!
echo "$GUARD" > "$RUN/guardian"
trap 'echo signal > "$RUN/cancelled"' INT TERM HUP
wait "$SHIP"; CODE=$?
if [ -f "$RUN/cancelled" ]; then
    CODE=130
    wait "$GUARD" || :
else
    rm -f "$RUN/active"
    wait "$GUARD" || :
fi
rm -f "$RUN/active"
[ ! -f "$RUN/cancelled" ] || sh "$HERE/gate-report.sh" "$RUN" >> "$LOG"
[ ! -f "$RUN/watch.log" ] || cat "$RUN/watch.log" >> "$LOG"
git -C "$CLONE" rev-parse HEAD > "$RUN/head.after"
git -C "$CLONE" ls-remote origin refs/heads/main "refs/heads/batch/$ID" 'refs/tags/*' > "$RUN/refs.after"
echo "$CODE" > "$RUN/exit"
# BW1: the outcome, to the gate's file, the batch's own and, on a publication or a stop, one message to the batch.
CHECKS=$CLONE/target/checks; TAG=$(cat "$RUN/tag" 2>/dev/null); KNOWN=$(grep -c . "$RUN/stages/used-parts" 2>/dev/null) || :
# BW2: published only when origin's main after the run holds the batch's tip, which the main it began on did not; a dry run
# never. Main moves under any run when another gate pushes (HS2's dry run beside FB13's ship was told "HS2 shipped",
# 2026-11-20; LM1-D's beside a queue commit, 2026-11-18), and a refused push leaves the clone on that stranger's main.
ref() { sed -n "s|[[:space:]]$1\$||p" "$RUN/refs.after" | head -1; }
holds() { [ -n "$1" ] || return 1; git -C "$CLONE" cat-file -e "$1^{commit}" 2> /dev/null || git -C "$CLONE" fetch -q origin refs/heads/main 2> /dev/null
    git -C "$CLONE" merge-base --is-ancestor "$2" "$1" 2> /dev/null; }   # commit $1 has $2 in its history
AFTER=$(ref refs/heads/main); TIP=$(cat "$RUN/branch.commit"); PUBLISHED=no; STEP=stopped; OURS=1
[ "$DRY" = yes ] || [ -z "$AFTER" ] || ! holds "$AFTER" "$TIP" || holds "$(cat "$RUN/main.commit")" "$TIP" || PUBLISHED=yes
[ "$PUBLISHED" = yes ] && [ -n "$TAG" ] && holds "$(ref "refs/tags/$TAG")" "$TIP" || TAG=   # a tag counts only as pushed with it
! grep -q 'kept run refused' "$LOG" || REUSED=""
REASON=$(sed -n 's/.*ship      STOPPED - //p; s/^gate: \(stopped by .*\)/\1/p' "$LOG" | tail -1)
case "$CODE:$REASON" in
    0:*) STEP=done; OUTCOME=passed; [ "$PUBLISHED" = no ] || OUTCOME=${TAG:+tagged}; OUTCOME=${OUTCOME:-pushed} ;;
    130:*|*'stopped by '*) OUTCOME=cancelled; OURS=0 ;;
    *'NEW red'*) OUTCOME='new red' ;;
    *'conflicts with main'*|*'merge of'*) OUTCOME=conflict ;;
    *'did not finish'*) OUTCOME=hung ;;
    *'main moved on origin'*|*'push of main'*) OUTCOME='push refused'; OURS=0 ;;
    *) OUTCOME=failed; grep -q 'ship      check .* started' "$LOG" || OUTCOME=records ;;
esac
names() { grep "$1" "$2" 2>/dev/null | sed "$3" | sort -u | tr '\n' ',' | sed 's/,$//'; }   # the harness of each line of $2 that holds $1
V=$(version); set --; [ "$PUBLISHED" = no ] || set -- version="$V"
bus gate "$ID" "$STEP" "$@" outcome="$OUTCOME" published=$PUBLISHED reused="$REUSED" known_reds="${KNOWN:-0}" \
    skipped="$(names 'skipped (needs' "$CHECKS/suite.txt" 's/ .*//')" timed_out="$(names 'FAIL: timed out after' "$CHECKS/reds.txt" 's/ |.*//')"
if [ "$PUBLISHED" = yes ]; then
    AS="untagged on $V"; [ -z "$TAG" ] || AS="as $TAG"
    bus state "$ID" shipped version="$V"; bus send "$ID" --kind info --body "$ID shipped $AS"
elif [ "$STEP" = stopped ]; then
    WHAT=gate; MARKER=", restore the readiness marker"; [ "$DRY" = no ] || { WHAT="dry run"; MARKER=""; }
    {   echo "$ID's $WHAT stopped, $OUTCOME: ${REASON:-the log names no reason}"
        if [ "$OURS" = 1 ]; then echo "Mend or name each line below (docs/contract.md 5.3)$MARKER and mail the orchestrator \"$ID mends at <commit>\"."
        else echo "Nothing of the batch's to mend."; fi
        echo "log: $WHERE"; echo "$(cat "$RUN/unnamed.txt" 2>/dev/null | grep -c .) unnamed line(s):"; tr -d '\r' < "$RUN/unnamed.txt" 2>/dev/null; } > "$RUN/mail.txt"
    bus send "$ID" --kind instruction --urgent - < "$RUN/mail.txt"
    if [ "$DRY" = yes ]; then :; elif [ "$OURS" = 1 ]; then bus state "$ID" sent-back --note "$OUTCOME: $REASON"; else bus state "$ID" ready --note "gate $OUTCOME"; fi
fi
printf '[%s] gate      exit %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$CODE" >> "$LOG"
tail -16 "$LOG"
echo "gate: exit $CODE; log $LOG; unnamed lines $RUN/unnamed.txt"
# Keep stopped clones too, for inspecting HEAD and the unfinished merge. Logs always survive.
if [ "$CODE" = 0 ]; then
    [ -f "$RUN/checks.complete" ] || [ ! -d "$CLONE/target/checks" ] || { mkdir -p "$RUN/checks"; cp -R "$CLONE/target/checks/." "$RUN/checks/"; }
    if [ -f "$RUN/checks/times.tsv" ]; then
        cp "$RUN/checks/times.tsv" "$HOME/.cbs-gate-times.tsv.$$" && mv "$HOME/.cbs-gate-times.tsv.$$" "$HOME/.cbs-gate-times.tsv"
    fi
    rm -rf "$CLONE"
fi
exit "$CODE"
