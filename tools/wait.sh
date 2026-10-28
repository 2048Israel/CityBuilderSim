#!/bin/sh
# ---------------------------------------------------------------------------
#  Waiting without ending a turn: until a batch's row leaves the queue on
#  origin/main, until a batch branch on origin has its ship file, or until a
#  line turns up in a file. Prints exactly one line on every ending, so the
#  session that waits is woken once and reads one line.
#  Read-only apart from fetching origin's main and batch branches. POSIX sh.
#
#      sh tools/wait.sh --row ID... [--for MIN]       until none of the ids has a
#                                  row in the Queued table of origin/main's
#                                  QUEUE.md (origin's main fetched every 2 minutes);
#                                  or, for a branch built on one of them (RL1), until
#                                  that one is ready again with commits HEAD lacks
#      sh tools/wait.sh --ship [--skip ID...] [--for MIN]   until an unmerged batch/ branch on
#                                  origin has its docs/queue/<id>-ship.md ("ready to ship"),
#                                  less the ids skipped and those built on an unmerged
#                                  batch (tools/carries.sh: waiting, not ready); one that
#                                  has it already ends it at once (fetched every 2
#                                  minutes, as --row)
#      sh tools/wait.sh --line FILE ERE [--for MIN]   until a line of FILE matches the
#                                  extended regex ERE (looked for every 10 seconds;
#                                  a carriage return at a line's end is ignored)
#
#  Without --for it waits for ever: right for a background command
#  (run_in_background), whose exit wakes the session. A foreground caller
#  passes --for 9 in a Bash call whose timeout is raised to 600000 ms (the
#  tool's most: its default, 2 minutes, moves the call to the background with
#  no line). --for 0 looks once.
#  An id that is not queued, or a line already in the file, ends it at once.
#  --row never starts a fetch with less than 75 seconds left before its --for
#  deadline - it gives up there, with the last state it saw - and cuts each
#  fetch off at 45 seconds (where `timeout` exists), so a --for 9 call has said
#  its line by about 8.5 minutes. A --for of 1 or 0 is one look.
#  The pauses (120 and 10 seconds) can be shortened for a test: CBS_WAIT_ROW_SECS,
#  CBS_WAIT_LINE_SECS; so can the 75 and 45: CBS_WAIT_MARGIN, CBS_WAIT_FETCH_SECS.
#
#  The ending, the one line and the exit status:
#      wait      done - ID is out of the Queued table of origin/main (3a9f01c2, 14:02)   0
#      wait      done - ID ready again with commits HEAD lacks: git merge origin/batch/<id> (14:02)   0
#      wait      done - ready to ship: ID ID (./ship.sh batch/<id>; origin/main 3a9f01c2, 14:02)   0
#      wait      gave up after 7 min - still queued: ID (origin/main 3a9f01c2)            3
#      wait      gave up after 7 min - no batch branch ready to ship (origin/main 3a9f01c2)   3
#      wait      origin unreachable 6 fetches running: <the line git said>                4
#                (or "wait      origin/main has no QUEUE.md", also 4)
#      usage: sh tools/wait.sh ...                                                       2
#  After --row says done, the waiting session merges origin/main at once
#  (`git merge origin/main`; a batch cut from its predecessor's tip has commits,
#  so not --ff-only), or the other line's branch, `git merge origin/batch/ID`: its
#  worktree was cut when the session started, hours before, and work built on that
#  base conflicts at the ship in the very files the lane shares. A done line that
#  finds HEAD without origin/main says so after its time: "; HEAD lacks origin/main:
#  merge it first". Then it waits again for the same ids.
#  --ship is the orchestrator's (GP1): one background wait that ends when a branch is ready,
#  so a gate starts within minutes of the ship file and not at the next hourly check-in
#  (docs/batch.md, The day). A branch counts while its tip is not in origin/main's history
#  and carries docs/queue/<its id>-ship.md; a merged one, a deleted one and a push that
#  is not a ship file do not. A branch the orchestrator holds with its ship file on (a gate
#  stopped for Jerus's word) is named in --skip, or the wait ends at once every time.
#
#  WHY (BP1, 2026-10-24): a session that must wait for another batch's row to go
#  ended its turn and was woken by a poll, or wrote an until-loop by hand with no
#  deadline, which loops for ever, without a word, while a fetch keeps failing.
#  This has a deadline when asked for one, tells a failing fetch from a quiet
#  queue (6 failures in a row end it), and says how it ended in one line.
#  2026-10-25, the review: the last fetch of a --for 9 call started at the
#  deadline, and a stalled one (GIT_HTTP_LOW_SPEED_* does not cover the connect
#  phase: curl waits 300 s) outlived the Bash tool's 10 minutes with no line;
#  now no fetch starts in the last 75 s and each is cut off at 45. Nothing said
#  to take origin/main after the wait either, so a successor built on its
#  predecessor's old base: the header says it now, and the done line checks.
#  GP1 (2026-10-27): a branch that was ready waited for the orchestrator's next
#  hourly check-in, a median of about 60 minutes from the ship file to the merge;
#  --ship makes the ship file itself the thing waited for. tools/wait-selftest.sh.
#  RL1 (2026-10-27): a lane's next batch starts at its predecessor's ship file, built
#  on its tip, so --row also ends when that tip moves on to a ready state HEAD lacks
#  (the predecessor sent back and mended); --ship leaves a branch built on an unmerged
#  one out. Both fetch main and the batch branches, which --row needs now.
# ---------------------------------------------------------------------------

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
