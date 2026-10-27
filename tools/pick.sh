#!/bin/sh
# ---------------------------------------------------------------------------
#  The harnesses a change needs (BP1, 2026-10-25): no JVM, about a second.
#
#      sh tools/pick.sh            the working tree (staged, unstaged, untracked) against origin/main
#      sh tools/pick.sh A          the working tree against A
#      sh tools/pick.sh A B        what B has that A has not (a merge: sh tools/pick.sh M^1 M);
#                                  B's files are read, not the working tree's
#
#  stdout is one line: the harness names, space-separated and sorted, or ALL
#  (the whole suite), or NONE (no harness reads what changed). stderr says why,
#  in batch-check's column style, and ends in the review line:
#
#      pick      14 of 138 harnesses for 6 files changed since 031b49a9
#      pick      left to the gate: Game.java (120 readers); LongPlaytest in its place
#      pick      not a harness's: 9 files
#      review    needed - money: Bond.java; saves: DataSave.java, +SAVE_FORMAT; threading: +Platform.runLater in ui/X.java
#
#  Exit status 0 ok, 1 a guard (a *Check.java that AllChecks.HARNESSES does not
#  register: no suite would ever run it), 2 usage or not in a git tree.
#
#  The rules, the first that matches a changed file wins:
#    pom.xml                          -> ALL
#    AllChecks.java                   -> nothing (the guard reads it)
#    a *Check.java or LongPlaytest    -> itself (a deleted one: nothing); and a
#      *Check.java changed or added -> TimingCheck as well, which holds that no
#      harness compares a figure against a *_MS or *_NS dial, or compares two
#      measured times, but through Timing (ET6's BridgeCheck did, in 3 places;
#      its own --pick never ran TimingCheck and only the gate's full suite
#      caught it - TM3, 2026-10-27)
#    a .java class in docs/harnesses.md's "Which harnesses read which class"
#      table, as the fork point had it -> its readers; but a class more than
#      CBS_PICK_FANOUT (default 50) harnesses read - Game, GameFiles,
#      BuildingsTemplate, Founding, TempDirs, Sectors, Sector - is left to the
#      gate's full suite and LongPlaytest is picked in its place
#    a .java class not in the table   -> the harnesses that name it (grep -w),
#      when it existed at the fork point; one new since then -> nothing, since
#      only a harness that is itself changed can name it
#    src/main/resources/X             -> the harnesses whose text holds X's file
#      name, the readers (by the same fan-out rule) of the classes whose text
#      holds it, and LongPlaytest when X is a .json
#    docs/ensemble/*, tools/ensemble/* -> EnsembleCheck
#    anything else                    -> nothing ("not a harness's", counted)
#  Only a name that is a harness file at the diff's end is ever picked.
#
#  The review line is a floor (BP1 item 5): money (Bank*, CentralBank*, Bond*,
#  OrderBook*, Rollover*, Treasury*, Fund*, Debt*, Expectations*), saves
#  (DataSave*, HistorySave*, GameFiles*, an added line naming SAVE_FORMAT or
#  OLDEST_LOADABLE_FORMAT), threading (an added line with synchronized,
#  volatile, new Thread, Executor, runLater, Atomic*, Concurrent* or
#  CompletableFuture; ui/UserInterface.java changed, the UI-wide). Harness
#  files and comment lines are not read for it. The orchestrator may add a
#  stranger's review, never skip one this names.
#
#  WHY (BP1, 2026-10-25): a batch ran the whole suite three times (the
#  baseline, its final run, the gate's rerun, 15-22 minutes each) and the
#  orchestrator read every diff for a reason to review it. docs/harnesses.md
#  already says which harnesses mention which class, so the diff names the
#  harnesses and the review the same way. A cap that fell back to the whole
#  suite when a pick grew was left out: the pick is a floor, the gate's full
#  suite is the net, and a class most harnesses read is said to be left to it.
# ---------------------------------------------------------------------------

LC_ALL=C; export LC_ALL

say() { printf '%-9s %s\n' "$1" "$2" >&2; }
files() { if [ "$1" = 1 ]; then echo "1 file"; else echo "$1 files"; fi; }
usage() { say usage "sh tools/pick.sh [A [B]]  - no args: the working tree against origin/main; A: against A; A B: what B has that A has not"; exit 2; }

case $# in 0|1|2) ;; *) usage ;; esac
case "$1" in -*) usage ;; esac
TOP=$(git rev-parse --show-toplevel 2>/dev/null) || { say pick "not inside a git work tree"; exit 2; }
cd "$TOP" || exit 2

SRC=src/main/java/ham/citybuildersim
FANOUT=${CBS_PICK_FANOUT:-50}
case "$FANOUT" in ''|*[!0-9]*) FANOUT=50 ;; esac

TMP=$(mktemp -d 2>/dev/null) || { TMP="${TMPDIR:-/tmp}/pick.$$"; mkdir -p "$TMP" || exit 2; }
trap 'rm -rf "$TMP"' EXIT
trap 'exit 2' HUP INT TERM

rev() { git rev-parse -q --verify "$1^{commit}" 2>/dev/null; }

# --- what is compared with what -----------------------------------------------
REF=""      # the commit whose files are read; empty: the working tree
if [ $# = 2 ]; then
    A=$1; B=$2
    rev "$A" >/dev/null && rev "$B" >/dev/null || { say pick "no such commit: $A or $B"; exit 2; }
    REF=$B; RANGE="$A...$B"; BASE=$A
    MB=$(git merge-base "$A" "$B" 2>/dev/null)
else
    if [ $# = 1 ]; then
        BASE=$1
        rev "$BASE" >/dev/null || { say pick "no such commit: $BASE"; exit 2; }
    else
        BASE=origin/main
        rev "$BASE" >/dev/null || BASE=main
    fi
    MB=$(git merge-base HEAD "$BASE" 2>/dev/null)
    RANGE=$MB
fi
if [ -z "$MB" ]; then
    echo ALL
    say pick "ALL - no common ancestor with $BASE here (a shallow clone, or no such branch): the whole suite"
    exit 0
fi
SHORT=$(git rev-parse --short=8 "$MB")

show()  { if [ -n "$REF" ]; then git show "$REF:$1"; else cat -- "$1"; fi; }
# files_with FLAG NAMEFILE PATHSPEC... : the paths whose text holds a name of NAMEFILE (FLAG -w for a whole word, else -F)
files_with() {
    flag=$1; pf=$2; shift 2
    [ -s "$pf" ] || return 0
    if [ -n "$REF" ]; then
        git grep -l -F $flag -f "$pf" "$REF" -- "$@" 2>/dev/null | awk -v p="$REF:" 'index($0, p) == 1 { print substr($0, length(p) + 1) }'
    else
        git grep -l --untracked -F $flag -f "$pf" -- "$@" 2>/dev/null
    fi
}
# harness_files_with FLAG NAMEFILE : the harness files (the *Check.java and LongPlaytest, not subdirectories) holding a name
harness_files_with() { files_with "$1" "$2" ":(glob)$SRC/*Check.java" "$SRC/LongPlaytest.java"; }

# --- the changed files: "status<TAB>path" ----------------------------------------
CH="$TMP/changes"
git -c core.quotepath=off diff --name-status --no-renames "$RANGE" > "$CH" 2>/dev/null
: > "$TMP/untracked"
if [ -z "$REF" ]; then
    git -c core.quotepath=off ls-files -o --exclude-standard > "$TMP/untracked" 2>/dev/null
    awk '{ printf "A\t%s\n", $0 }' "$TMP/untracked" >> "$CH"
fi

# --- the harness files, and the guard --------------------------------------------
if [ -n "$REF" ]; then git ls-tree --name-only "$REF" -- "$SRC/" 2>/dev/null; else ls -1 "$SRC" 2>/dev/null; fi | sed 's#.*/##' > "$TMP/srcfiles"
grep -E '(Check|^LongPlaytest)\.java$' "$TMP/srcfiles" | sed 's/\.java$//' | sort -u > "$TMP/harn"
HN=$(wc -l < "$TMP/harn" | tr -d ' ')
if show "$SRC/AllChecks.java" > "$TMP/allchecks" 2>/dev/null; then
    grep -o '"[A-Za-z0-9_]*"' "$TMP/allchecks" | tr -d '"' | grep -E 'Check$|^LongPlaytest$' | sort -u > "$TMP/reg"
    awk 'FILENAME == ARGV[1] { r[$0] = 1; next } !($0 in r)' "$TMP/reg" "$TMP/harn" > "$TMP/unreg"
    if [ -s "$TMP/unreg" ]; then
        say pick "GUARD - $(sed 's/$/.java/' "$TMP/unreg" | paste -sd, - | sed 's/,/, /g') not in AllChecks.HARNESSES: no suite would ever run it"
        exit 1
    fi
fi

# --- the index at the fork point: "class<TAB>reader,reader" -------------------------
git show "$MB:docs/harnesses.md" 2>/dev/null | awk '
    /^## Which harnesses read which class/ { on = 1; next }
    on && /^## / { on = 0 }
    !on || $0 !~ /^\| \[/ { next }
    { split($0, c, "|"); n = c[2]; sub(/^ *\[/, "", n); sub(/\].*/, "", n)
      r = c[3]; gsub(/[ *]/, "", r); if (r == "none") r = ""
      printf "%s\t%s\n", n, r }' > "$TMP/index"

# --- a resource is read by name: the classes whose text holds it join the changes -----
: > "$TMP/derived"
awk -F'\t' '$2 ~ /^src\/main\/resources\// { n = $2; sub(/^.*\//, "", n); print n }' "$CH" | sort -u > "$TMP/resnames"
files_with "" "$TMP/resnames" src/main/java | grep '\.java$' | grep -v -E '(Check|AllChecks|LongPlaytest)\.java$' \
    | awk '{ printf "X\t%s\n", $0 }' > "$TMP/derived"

# --- the rules ------------------------------------------------------------------
awk -F'\t' -v src="$SRC" -v fan="$FANOUT" '
FILENAME == ARGV[1] { idx[$1] = $2; nidx++; next }
{
    s = $1; f = $2
    cls = f; sub(/^.*\//, "", cls); sub(/\.[^.]*$/, "", cls)
    if (s != "X") nfiles++
    if (f == "pom.xml") { print "ALL\tpom.xml changed"; next }
    if (f == src "/AllChecks.java") next
    if (index(f, src "/") == 1 && f ~ /\.java$/) {
        rel = substr(f, length(src) + 2)
        if (rel !~ /\// && (cls ~ /Check$/ || cls == "LongPlaytest")) {
            if (s != "D") print "H\t" cls
            if (s != "D" && cls ~ /Check$/) print "H\tTimingCheck"   # TM3 item 8: a harness written or changed is read by the source rules of TimingCheck
            next
        }
        if (cls in idx) {
            if (idx[cls] == "") { print "Z\t" cls ".java"; next }
            n = split(idx[cls], a, ",")
            if (n > fan) { print "GATE\t" cls ".java\t" n; print "H\tLongPlaytest"; next }
            for (i = 1; i <= n; i++) print "H\t" a[i]
            next
        }
        if (s != "A") print "G\t" cls
        next
    }
    if (index(f, "src/main/resources/") == 1) {
        b = f; sub(/^.*\//, "", b); print "R\t" b
        if (b ~ /\.json$/) print "H\tLongPlaytest"
        next
    }
    if (f ~ /^(docs|tools)\/ensemble\//) { print "H\tEnsembleCheck"; next }
    print "N\t" f
}
END { print "COUNT\t" nfiles + 0 "\t" nidx + 0 }' "$TMP/index" "$CH" "$TMP/derived" > "$TMP/ev"

K=$(awk -F'\t' '$1 == "COUNT" { print $2 }' "$TMP/ev")
NIDX=$(awk -F'\t' '$1 == "COUNT" { print $3 }' "$TMP/ev")
WHY=$(awk -F'\t' '$1 == "ALL" { print $2; exit }' "$TMP/ev")
[ -n "$WHY" ] || [ "$NIDX" != 0 ] || WHY="no harness table in docs/harnesses.md at $SHORT"

# --- the review line, whatever the pick ---------------------------------------------
review() {
    {
        awk -F'\t' -v src="$SRC" 'index($2, src "/") == 1 && $2 ~ /\.java$/ { print "NAME\t" substr($2, length(src) + 2) }' "$CH"
        git diff -U0 --no-renames --src-prefix=a/ --dst-prefix=b/ "$RANGE" -- '*.java' 2>/dev/null | awk '
            /^\+\+\+ / { f = ($0 == "+++ /dev/null") ? "" : substr($0, 7); next }
            /^\+/      { if (f != "") print "ADD\t" f "\t" substr($0, 2) }'
        while IFS= read -r p; do
            case "$p" in *.java) awk -v f="$p" '{ print "ADD\t" f "\t" $0 }' "$p" ;; esac
        done < "$TMP/untracked"
    } | awk -F'\t' -v src="$SRC" '
    BEGIN { THR = "[A-Za-z_.]*(synchronized|volatile|new Thread|Executor|runLater|Atomic[A-Z]|Concurrent[A-Z]|CompletableFuture)[A-Za-z_]*" }
    function note(kind, item) {
        if (((kind SUBSEP item) in seen)) return
        seen[kind SUBSEP item] = 1; cnt[kind]++; list[kind, cnt[kind]] = item
    }
    function join(kind, max,   i, s) {
        s = ""
        for (i = 1; i <= cnt[kind] && i <= max; i++) s = s (i > 1 ? ", " : "") list[kind, i]
        if (cnt[kind] > max) s = s ", +" (cnt[kind] - max) " more"
        return s
    }
    function harness(rel,   c) { c = rel; sub(/^.*\//, "", c); sub(/\.java$/, "", c); return (c ~ /Check$/ || c == "LongPlaytest" || c == "AllChecks") ? "" : c }
    $1 == "NAME" {
        c = harness($2); if (c == "") next
        if (c ~ /^(Bank|CentralBank|Bond|OrderBook|Rollover|Treasury|Fund|Debt|Expectations)/) note("money", $2)
        if (c ~ /^(DataSave|HistorySave|GameFiles)/) note("saves", $2)
        if ($2 == "ui/UserInterface.java") note("ui", $2 " (UI-wide)")
        next
    }
    $1 == "ADD" {
        if (index($2, src "/") != 1) next
        rel = substr($2, length(src) + 2)
        if (harness(rel) == "") next
        t = $3; for (i = 4; i <= NF; i++) t = t "\t" $i
        u = t; sub(/^[ \t]+/, "", u)
        if (u ~ /^(\/\/|\/\*|\*)/) next
        if (t ~ /OLDEST_LOADABLE_FORMAT/) note("savetok", "+OLDEST_LOADABLE_FORMAT")
        if (t ~ /SAVE_FORMAT/) note("savetok", "+SAVE_FORMAT")
        if (match(t, THR)) note("thrtok", "+" substr(t, RSTART, RLENGTH) " in " rel)
        next
    }
    END {
        out = ""
        if (cnt["money"]) out = "money: " join("money", 6)
        if (cnt["saves"] || cnt["savetok"]) {
            s = ""
            if (cnt["saves"]) s = join("saves", 6)
            if (cnt["savetok"]) s = s (s != "" ? ", " : "") join("savetok", 3)
            out = out (out != "" ? "; " : "") "saves: " s
        }
        if (cnt["thrtok"] || cnt["ui"]) {
            s = ""
            if (cnt["thrtok"]) s = join("thrtok", 4)
            if (cnt["ui"]) s = s (s != "" ? ", " : "") join("ui", 1)
            out = out (out != "" ? "; " : "") "threading: " s
        }
        if (out == "") print "review    not needed - no money, saves or threading in the diff"
        else print "review    needed - " out
    }' >&2
}

# --- the pick ------------------------------------------------------------------------
if [ -n "$WHY" ]; then
    echo ALL
    say pick "ALL $HN harnesses - $WHY ($(files "$K") changed since $SHORT)"
    review
    exit 0
fi

awk -F'\t' '$1 == "G" { print $2 }' "$TMP/ev" | sort -u > "$TMP/gnames"
awk -F'\t' '$1 == "R" { print $2 }' "$TMP/ev" | sort -u > "$TMP/rnames"
{
    awk -F'\t' '$1 == "H" { print $2 }' "$TMP/ev"
    harness_files_with -w "$TMP/gnames" | sed 's#.*/##; s#\.java$##'
    harness_files_with "" "$TMP/rnames" | sed 's#.*/##; s#\.java$##'
} | sort -u | awk 'FILENAME == ARGV[1] { h[$0] = 1; next } ($0 in h)' "$TMP/harn" - > "$TMP/pick"
N=$(wc -l < "$TMP/pick" | tr -d ' ')

if [ "$N" = 0 ]; then
    echo NONE
    say pick "NONE of $HN harnesses read what changed ($(files "$K") changed since $SHORT)"
else
    paste -sd' ' "$TMP/pick"
    say pick "$N of $HN harnesses for $(files "$K") changed since $SHORT"
fi
GATE=$(awk -F'\t' '$1 == "GATE" { print $2 " (" $3 " readers)" }' "$TMP/ev" | sort -u | paste -sd, - | sed 's/,/, /g')
if [ -n "$GATE" ]; then
    case "$GATE" in *,*) PLACE=their ;; *) PLACE=its ;; esac
    say pick "left to the gate: $GATE; LongPlaytest in $PLACE place"
fi
ZERO=$(awk -F'\t' '$1 == "Z" { print $2 }' "$TMP/ev" | sort -u | paste -sd, - | sed 's/,/, /g')
[ -z "$ZERO" ] || say pick "read by no harness: $ZERO"
NOT=$(awk -F'\t' '$1 == "N"' "$TMP/ev" | wc -l | tr -d ' ')
[ "$NOT" = 0 ] || say pick "not a harness's: $(files "$NOT")"
review
exit 0
