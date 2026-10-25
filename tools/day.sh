#!/bin/sh
# ---------------------------------------------------------------------------
#  The orchestrator's morning in one call: where main stands, what is queued,
#  and every batch branch on origin with what it waits for. Read-only apart
#  from the fetch it starts with. POSIX sh and awk; no JVM.
#
#      sh tools/day.sh            the whole picture, about 15 lines
#      sh tools/day.sh --due      only the branches that need the orchestrator
#                                 (ready to ship, or stuck): nothing printed and
#                                 exit 1 when none does - the hourly check-in's
#                                 probe, "nothing printed: end the turn"
#
#  The lines, in this order:
#      main      0.8.41 = the last version tag; merged 3 h ago: Merge batch/ET1 ...
#      reds      MapCheck 2, EarthCheck 1 - gate of 2026-10-24, docs/notes/gate.txt
#      queue     14 queued, next: HY1 ET2 ET3 ET4
#      pending   18: ET1-C PT1-M4-C ...
#      ready     ID - ./ship.sh batch/ID  (its docs/queue/ID-ship.md is on the
#                branch), and "BUT conflicts with main in ..." when git merge-tree
#                predicts any, generated docs aside (ship.sh takes the branch's
#                side of those); "conflicts not predicted" where git is too old
#      ready     ID - old-style (its row out on the branch, no ship file) - ./ship.sh batch/ID
#                a branch finished the old way (docs/batch.md, the change-over):
#                main still queues ID, the branch's own QUEUE.md had the row
#                where it was cut and no longer has it, which is what ship.sh
#                asks of a branch with no ship file; the same conflict note
#      stuck    ID - queued, no commit for 30 h
#      working   ID - queued, last commit 3 h ago
#      off-queue unmerged and not queued, one line for all: ID (age), ...
#      tidy      N merged batch branches still on origin
#  A fetch that fails is said in one line at the top ("origin as last fetched").
#  main's reds are the harness names of docs/notes/gate.txt's non-# lines, each
#  with its count of failing lines (a bare "exit N" line counts one). A VERSION
#  that is not the last tag is said on the main line: ship.sh stops on it.
#
#  Exit status: 0 the picture was printed (--due: something is due); 1 --due and
#  nothing is due; 2 not a git checkout, or no origin/main here.
#
#  WHY (BP1, 2026-10-24): a fresh orchestrator began each day with about ten
#  calls - QUEUE.md, the changelog, the branch lists, the tags - and the hourly
#  check-in needed a probe that costs one short call when nothing is due. The
#  first draft counted the merged branches inside a pipe's subshell, where the
#  count was lost; the loop here runs in the shell itself.
#  2026-10-25, the review: a branch finished the old way (ET2 was one) has no
#  ship file, so the first draft called it "working" for 24 h and then "stuck",
#  and --due stayed silent on it: the old-style ready test below is that fix.
#  It goes, with ship.sh's old path, when no branch from before BP1 is left.
#  The gate's date was the first date in any '#' line, which is often a date in
#  the queue row's text; it is now the '# date:' line ship.sh writes for it.
# ---------------------------------------------------------------------------

usage() { echo "usage: sh tools/day.sh [--due]" >&2; exit 2; }
DUE=0
case "$1" in
    "") ;;
    --due) DUE=1 ;;
    *) usage ;;
esac
[ $# -le 1 ] || usage
git rev-parse --git-dir >/dev/null 2>&1 || { echo "day       not in a git checkout" >&2; exit 2; }
cd "$(git rev-parse --show-toplevel)" || exit 2

# --- origin ----------------------------------------------------------------
FETCHMSG=
if [ -z "$CBS_DAY_NOFETCH" ]; then
    FERR=$(GIT_TERMINAL_PROMPT=0 git fetch -q --prune --tags origin 2>&1) ||
        FETCHMSG="day       fetch failed ($(printf '%s\n' "$FERR" | head -n 1 | cut -c1-80)): origin as last fetched"
fi
M=origin/main
git rev-parse --verify -q "$M" >/dev/null || { echo "day       no $M here" >&2; exit 2; }
NOW=$(date +%s)
NL='
'
GEN='^docs/(map/|dials\.md|month-order\.md|harnesses\.md)'
ago() {   # seconds since the epoch time $1, said in minutes, hours or days
    s=$((NOW - $1)); [ "$s" -ge 0 ] || s=0
    if [ "$s" -lt 3600 ]; then echo "$((s / 60)) min"
    elif [ "$s" -lt 172800 ]; then echo "$((s / 3600)) h"
    else echo "$((s / 86400)) days"; fi
}
rows() {   # the ids in the first column of the table under "## $1" in QUEUE.md, from stdin
    awk -F'|' -v sec="$1" '/^## / { ins = (index($0, "## " sec) == 1); next }
        ins && /^\| [A-Za-z0-9]/ { gsub(/^ +| +$/, "", $2); if ($2 != "batch" && $2 != "item") print $2 }'
}
Q=$(git show "$M:QUEUE.md" 2>/dev/null)
QUEUED=$(printf '%s\n' "$Q" | rows Queued)

# --- the branches ----------------------------------------------------------
# Built in variables, not in a pipe: a loop in a pipe runs in a subshell and
# its counts are lost.
CAN_MT=0
git merge-tree --write-tree --no-messages "$M" "$M" >/dev/null 2>&1 && CAN_MT=1
mtnote() {   # what git predicts of merging branch ref $1 into main, as the tail of a ready line
    if [ "$CAN_MT" != 1 ]; then printf ' - conflicts not predicted'; return; fi
    R=$(git merge-tree --write-tree --name-only --no-messages "$M" "$1" 2>/dev/null); rc=$?
    if [ "$rc" -eq 1 ]; then
        C=$(printf '%s\n' "$R" | sed 1d | grep -v '^$' | grep -v -E "$GEN" | head -n 4 | tr '\n' ' ')
        [ -z "$C" ] || printf ' - BUT conflicts with main in %s' "${C% }"
    elif [ "$rc" -ne 0 ]; then printf ' - conflicts not predicted'
    fi
}
queues() {   # does the QUEUE.md of commit-ish $1 have a row for $id under Queued?
    git show "$1:QUEUE.md" 2>/dev/null | rows Queued | grep -qxF "$id"
}
READY=; STUCK=; WORK=; OFFQ=; MERGED=0
for b in $(git for-each-ref --format='%(refname)' 'refs/remotes/origin/batch/*'); do
    id=${b#refs/remotes/origin/batch/}
    if git merge-base --is-ancestor "$b" "$M"; then MERGED=$((MERGED + 1)); continue; fi
    t=$(git log -1 --format=%ct "$b")
    age=$(ago "$t")
    if git cat-file -e "$b:docs/queue/$id-ship.md" 2>/dev/null; then
        READY="${READY}ready     $id - ./ship.sh batch/$id (last commit $age ago)$(mtnote "$b")$NL"
    elif ! printf '%s\n' "$QUEUED" | grep -qxF "$id"; then
        OFFQ="${OFFQ:+$OFFQ, }$id ($age)"
    elif base=$(git merge-base "$M" "$b" 2>/dev/null) && [ -n "$base" ] && queues "$base" &&
         git cat-file -e "$b:QUEUE.md" 2>/dev/null && ! queues "$b"; then
        # old-style, finished: main queues it (the test above), the row was on the branch where it
        # was cut (a branch cut before the row was queued is not this one), and is out of it now
        READY="${READY}ready     $id - old-style (its row out on the branch, no ship file) - ./ship.sh batch/$id (last commit $age ago)$(mtnote "$b")$NL"
    elif [ $((NOW - t)) -ge 86400 ]; then
        STUCK="${STUCK}stuck     $id - queued, no commit for $age$NL"
    else
        WORK="${WORK}working   $id - queued, last commit $age ago$NL"
    fi
done

# --- --due: only what needs the orchestrator --------------------------------
if [ "$DUE" = 1 ]; then
    [ -z "$FETCHMSG" ] || echo "$FETCHMSG"
    OUT="$READY$STUCK"
    [ -n "$OUT" ] || exit 1
    printf '%s' "$OUT"
    exit 0
fi

# --- main, the record, the queue -------------------------------------------
[ -z "$FETCHMSG" ] || echo "$FETCHMSG"
V=$(git show "$M:src/main/java/ham/citybuildersim/GameVersion.java" | sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p' | head -n 1)
LAST=$(git tag -l | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$' | sort -t. -k1,1n -k2,2n -k3,3n | tail -n 1)
if [ "$V" = "$LAST" ]; then VS="$V = the last version tag"; else VS="$V BUT the last version tag is ${LAST:-none} (tag or mend main)"; fi
echo "main      $VS; merged $(ago "$(git log -1 --format=%ct "$M")") ago: $(git log -1 --format=%s "$M" | cut -c1-70)"
if G=$(git show "$M:docs/notes/gate.txt" 2>/dev/null); then
    # ship.sh's '# date:' line: the '# gate of' line above it carries the queue row's text, dates and all
    GDATE=$(printf '%s\n' "$G" | sed -n 's/^# date: \([0-9]\{4\}-[0-9]\{2\}-[0-9]\{2\}\).*/\1/p' | head -n 1)
    REDS=$(printf '%s\n' "$G" | awk '
        /^#/ || /^[ \t]*$/ { next }
        { i = index($0, " | "); if (i == 0) next
          h = substr($0, 1, i - 1); rest = substr($0, i + 3)
          if (!(h in seen)) { seen[h] = 1; order[++n] = h }
          if (rest !~ /^exit [0-9]+$/) cnt[h]++ }
        END { for (k = 1; k <= n; k++) { c = cnt[order[k]]; if (c == 0) c = 1
                                          printf "%s%s %d", (k > 1 ? ", " : ""), order[k], c } }')
    echo "reds      ${REDS:-none} - gate of ${GDATE:-an unknown day}, docs/notes/gate.txt"
else
    echo "reds      no gate record yet (docs/notes/gate.txt is not on $M)"
fi
echo "queue     $(printf '%s\n' "$QUEUED" | grep -c .) queued, next: $(printf '%s\n' "$QUEUED" | head -n 4 | tr '\n' ' ' | sed 's/ $//')"
PEND=$(printf '%s\n' "$Q" | rows Pending)
echo "pending   $(printf '%s\n' "$PEND" | grep -c .): $(printf '%s\n' "$PEND" | tr '\n' ' ' | sed 's/ $//')"

# --- the branches, then the tidy count ---------------------------------------
printf '%s' "$READY$STUCK$WORK"
[ -z "$OFFQ" ] || echo "off-queue unmerged and not queued: $OFFQ"
[ "$MERGED" = 0 ] || echo "tidy      $MERGED merged batch branches still on origin"
exit 0
