#!/bin/sh
# ---------------------------------------------------------------------------
#  Waiting without ending a turn: until a batch's row leaves the queue on
#  origin/main, or until a line turns up in a file. Prints exactly one line on
#  every ending, so the session that waits is woken once and reads one line.
#  Read-only apart from fetching origin's main. POSIX sh.
#
#      sh tools/wait.sh --row ID... [--for MIN]       until none of the ids has a
#                                  row in the Queued table of origin/main's
#                                  QUEUE.md (origin's main fetched every 2 minutes)
#      sh tools/wait.sh --line FILE ERE [--for MIN]   until a line of FILE matches the
#                                  extended regex ERE (looked for every 10 seconds;
#                                  a carriage return at a line's end is ignored)
#
#  Without --for it waits for ever: right for a background command
#  (run_in_background), whose exit wakes the session. A foreground caller
#  passes --for 9: the Bash tool stops a call at 10 minutes. --for 0 looks once.
#  An id that is not queued, or a line already in the file, ends it at once.
#  The pauses (120 and 10 seconds) can be shortened for a test: CBS_WAIT_ROW_SECS,
#  CBS_WAIT_LINE_SECS.
#
#  The ending, the one line and the exit status:
#      wait      done - ID is out of the Queued table of origin/main (3a9f01c2, 14:02)   0
#      wait      gave up after 9 min - still queued: ID (origin/main 3a9f01c2)            3
#      wait      origin unreachable 6 fetches running: <the line git said>                4
#                (or "wait      origin/main has no QUEUE.md", also 4)
#      usage: sh tools/wait.sh ...                                                       2
#  A session chains its wait and its work in one background command:
#      sh tools/wait.sh --row FW5 && ./batch-check.sh --pick > target/checks/run.txt 2>&1; tail -12 target/checks/run.txt
#
#  WHY (BP1, 2026-10-24): a session that must wait for another batch's row to go
#  ended its turn and was woken by a poll, or wrote an until-loop by hand with no
#  deadline, which loops for ever, without a word, while a fetch keeps failing.
#  This has a deadline when asked for one, tells a failing fetch from a quiet
#  queue (6 failures in a row end it), and says how it ended in one line.
# ---------------------------------------------------------------------------

usage() { echo "usage: sh tools/wait.sh --row ID... [--for MIN] | --line FILE ERE [--for MIN]" >&2; exit 2; }

MODE=; IDS=; FILE=; ERE=; MIN=
while [ $# -gt 0 ]; do
    case "$1" in
        --row)
            [ -z "$MODE" ] || usage
            MODE=row; shift
            while [ $# -gt 0 ]; do
                case "$1" in -*) break ;; *) IDS="$IDS $1"; shift ;; esac
            done
            [ -n "$IDS" ] || usage ;;
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
export GIT_TERMINAL_PROMPT=0 GIT_HTTP_LOW_SPEED_LIMIT=1000 GIT_HTTP_LOW_SPEED_TIME=60   # a stalled fetch ends, not hangs

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

# --- --row: the ids leave the Queued table of origin/main ---------------------
if [ "$MODE" = row ]; then
    git rev-parse --git-dir >/dev/null 2>&1 || { echo "wait: not in a git checkout" >&2; exit 2; }
    FAILS=0
    while :; do
        if ERR=$(git fetch -q origin main 2>&1); then
            FAILS=0
            Q=$(git show origin/main:QUEUE.md 2>/dev/null) || { echo "wait      origin/main has no QUEUE.md"; exit 4; }
            QUEUED=$(printf '%s\n' "$Q" | awk -F'|' '/^## / { ins = (index($0, "## Queued") == 1); next }
                ins && /^\| [A-Za-z0-9]/ { gsub(/^ +| +$/, "", $2); print $2 }')
            LEFT=
            for id in $IDS; do
                printf '%s\n' "$QUEUED" | grep -qxF -- "$id" && LEFT="$LEFT $id"
            done
            HEAD="origin/main $(git rev-parse --short origin/main)"
            if [ -z "$LEFT" ]; then
                echo "wait      done -$IDS out of the Queued table of origin/main ($(git rev-parse --short origin/main), $(date +%H:%M))"
                exit 0
            fi
        else
            FAILS=$((FAILS + 1))
            if [ "$FAILS" -ge 6 ]; then
                echo "wait      origin unreachable 6 fetches running: $(printf '%s\n' "$ERR" | head -n 1 | cut -c1-120)"
                exit 4
            fi
        fi
        if late; then
            if [ "$FAILS" -gt 0 ]; then
                gave_up "origin not reached: $(printf '%s\n' "$ERR" | head -n 1 | cut -c1-100)"
            else gave_up "still queued:$LEFT ($HEAD)"; fi
        fi
        nap "${CBS_WAIT_ROW_SECS:-120}"
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
