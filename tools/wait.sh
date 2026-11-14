#!/bin/sh
# --row ID... waits for queue removal (or a legacy predecessor mended); --ship for readiness.
# --line FILE ERE waits for output. --for MIN bounds any wait; 0 looks once.
# Ship records marked <!-- readiness: provisional --> do not count (GG1).
# Exit 0 done, 3 deadline, 4 six failed fetches/unreadable queue, 2 usage.
# CBS_WAIT_ROW_SECS/LINE_SECS shorten polling; MARGIN/FETCH_SECS bound fetches.
# WHY (GP1, RL1): hand loops hung on failed fetches; wait-selftest.sh covers the endings.

usage() { echo "usage: sh tools/wait.sh --row ID... [--for MIN] | --ship [--skip ID...] [--for MIN] | --line FILE ERE [--for MIN]" >&2; exit 2; }

MODE=; IDS=; SKIP=; FILE=; ERE=; MIN=
while [ $# -gt 0 ]; do
    case "$1" in
        --row)
            [ -z "$MODE" ] || usage
            MODE=row; shift
            while [ $# -gt 0 ]; do
                case "$1" in -*) break ;; *) IDS="$IDS $1"; shift ;; esac
            done
            [ -n "$IDS" ] || usage ;;
        --ship)
            [ -z "$MODE" ] || usage
            MODE=ship; shift ;;
        --skip)
            [ "$MODE" = ship ] || usage
            shift
            while [ $# -gt 0 ]; do
                case "$1" in -*) break ;; *) SKIP="$SKIP $1"; shift ;; esac
            done
            [ -n "$SKIP" ] || usage ;;
        --line)
            [ -z "$MODE" ] || usage
            [ $# -ge 3 ] && [ -n "$2" ] && [ -n "$3" ] || usage
            MODE=line; FILE=$2; ERE=$3; shift 3 ;;
        --for)
            [ $# -ge 2 ] || usage
            MIN=$2; shift 2
            case "$MIN" in ''|*[!0-9]*) usage ;; esac ;;
        *) usage ;;
    esac
done
[ -n "$MODE" ] || usage

START=$(date +%s)
DEADLINE=
[ -z "$MIN" ] || DEADLINE=$((START + MIN * 60))
export GIT_TERMINAL_PROMPT=0 GIT_HTTP_LOW_SPEED_LIMIT=1000 GIT_HTTP_LOW_SPEED_TIME=60   # a transfer that stalls ends (the connect phase is cut off by fetch_main)
MARGIN=${CBS_WAIT_MARGIN:-75}          # the last fetch starts this many seconds before the deadline
FETCH_SECS=${CBS_WAIT_FETCH_SECS:-45}  # and no fetch runs longer than this, where `timeout` exists
# GNU and busybox timeout take "timeout 5 true"; Windows' timeout.exe, which a PATH can put first, does not
HAVE_TIMEOUT=0; timeout 5 true >/dev/null 2>&1 && HAVE_TIMEOUT=1

gave_up() {   # $1 what was still missing
    echo "wait      gave up after $(( ($(date +%s) - START) / 60 )) min - $1"
    exit 3
}
nap() {   # sleep $1 seconds, never past the deadline
    n=$1
    if [ -n "$DEADLINE" ]; then
        r=$((DEADLINE - $(date +%s)))
        [ "$r" -ge "$n" ] || n=$r
    fi
    [ "$n" -le 0 ] || sleep "$n"
}
late() { [ -n "$DEADLINE" ] && [ "$(date +%s)" -ge "$DEADLINE" ]; }
fetch_main() {   # origin's main and batch branches, the deleted ones pruned; ends in FETCH_SECS, which no git setting does for a stalled TLS handshake
    set -- --prune origin +refs/heads/main:refs/remotes/origin/main '+refs/heads/batch/*:refs/remotes/origin/batch/*'
    if [ "$HAVE_TIMEOUT" = 1 ]; then timeout "$FETCH_SECS" git fetch -q "$@"
    else git fetch -q "$@"; fi
}
CARRIES="$(dirname "$0")/carries.sh"
# the unmerged batch/ branches with their ship file, each after a space. ships ready: less --skip and those built on an
# unmerged batch (waiting, not ready). ships mended (--row): those of the ids that HEAD has part of but not the tip of,
# ready again - a predecessor sent back and mended.
ships() {
    for b in $(git for-each-ref --format='%(refname)' 'refs/remotes/origin/batch/*'); do
        id=${b#refs/remotes/origin/batch/}
        git merge-base --is-ancestor "$b" origin/main && continue
        git cat-file -e "$b:docs/queue/$id-ship.md" 2>/dev/null || continue
        git show "$b:docs/queue/$id-ship.md" | grep -q -x '<!-- readiness: provisional -->' && continue
        if [ "$1" = ready ]; then
            case " $SKIP " in *" $id "*) continue ;; esac
            [ -z "$(sh "$CARRIES" "$b")" ] && printf ' %s' "$id"
        else
            case " $IDS " in *" $id "*) ;; *) continue ;; esac
            git merge-base --is-ancestor "$b" HEAD && continue
            mb=$(git merge-base HEAD "$b") && ! git merge-base --is-ancestor "$mb" origin/main && printf ' %s' "$id"
        fi
    done
}

# --- --row: the ids leave the Queued table of origin/main; --ship: a batch branch is ready ---
if [ "$MODE" = row ] || [ "$MODE" = ship ]; then
    git rev-parse --git-dir >/dev/null 2>&1 || { echo "wait: not in a git checkout" >&2; exit 2; }
    FAILS=0
    FETCH_BY=; [ -z "$DEADLINE" ] || FETCH_BY=$((DEADLINE - MARGIN))   # the latest a fetch may start
    while :; do
        ERR=$(fetch_main 2>&1); rc=$?
        if [ "$rc" -eq 0 ]; then
            FAILS=0
            HEAD="origin/main $(git rev-parse --short origin/main)"
            if [ "$MODE" = ship ]; then
                READY=$(ships ready)
                if [ -n "$READY" ]; then echo "wait      done - ready to ship:$READY (./ship.sh batch/<id>; $(git rev-parse --short origin/main), $(date +%H:%M))"; exit 0; fi
            else
                Q=$(git show origin/main:QUEUE.md 2>/dev/null) || { echo "wait      origin/main has no QUEUE.md"; exit 4; }
                QUEUED=$(printf '%s\n' "$Q" | awk -F'|' '/^## / { ins = (index($0, "## Queued") == 1); next }
                    ins && /^\| [A-Za-z0-9]/ { gsub(/^ +| +$/, "", $2); print $2 }')
                LEFT=
                for id in $IDS; do
                    printf '%s\n' "$QUEUED" | grep -qxF -- "$id" && LEFT="$LEFT $id"
                done
            fi
            if [ "$MODE" = row ] && [ -z "$LEFT" ]; then
                BEHIND=; git merge-base --is-ancestor origin/main HEAD 2>/dev/null || BEHIND="; HEAD lacks origin/main: merge it first"
                echo "wait      done -$IDS out of the Queued table of origin/main ($(git rev-parse --short origin/main), $(date +%H:%M))$BEHIND"
                exit 0
            fi
            if [ "$MODE" = row ] && MENDED=$(ships mended) && [ -n "$MENDED" ]; then
                echo "wait      done -$MENDED ready again with commits HEAD lacks: git merge origin/batch/<id> ($(date +%H:%M))"
                exit 0
            fi
        else
            [ "$rc" -ne 124 ] || ERR="fetch cut off after $FETCH_SECS s"
            FAILS=$((FAILS + 1))
            if [ "$FAILS" -ge 6 ]; then
                echo "wait      origin unreachable 6 fetches running: $(printf '%s\n' "$ERR" | head -n 1 | cut -c1-120)"
                exit 4
            fi
        fi
        n=${CBS_WAIT_ROW_SECS:-120}
        if [ -n "$FETCH_BY" ]; then   # sleep to the last moment a fetch may start, and give up there
            r=$((FETCH_BY - $(date +%s)))
            if [ "$r" -le 0 ]; then
                if [ "$FAILS" -gt 0 ]; then
                    gave_up "origin not reached: $(printf '%s\n' "$ERR" | head -n 1 | cut -c1-100)"
                elif [ "$MODE" = ship ]; then gave_up "no batch branch ready to ship ($HEAD)"
                else gave_up "still queued:$LEFT ($HEAD)"; fi
            fi
            [ "$r" -ge "$n" ] || n=$r
        fi
        sleep "$n"
    done
fi

# --- --line: a line of FILE matches ERE ---------------------------------------
printf 'x\n' | grep -Eq -- "$ERE" 2>/dev/null
[ $? -le 1 ] || { echo "wait: not a regular expression: $ERE" >&2; exit 2; }
while :; do
    if [ -r "$FILE" ] && tr -d '\r' < "$FILE" 2>/dev/null | grep -Eq -- "$ERE"; then
        L=$(tr -d '\r' < "$FILE" 2>/dev/null | grep -E -- "$ERE" | head -n 1 | cut -c1-100)
        echo "wait      done - $FILE has a line matching $ERE: $L"
        exit 0
    fi
    if late; then
        if [ -r "$FILE" ]; then gave_up "$FILE has no line matching $ERE"
        else gave_up "$FILE does not exist"; fi
    fi
    nap "${CBS_WAIT_LINE_SECS:-10}"
done
