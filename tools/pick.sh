#!/bin/sh
# Picks harnesses against main, a named base or a committed A B range.
# stdout ALL, NONE or sorted names. Exit 1 means an unregistered harness.
# WHY (GR2): shared classes and borrowed harness fixtures escaped the builder.

LC_ALL=C; export LC_ALL

say() { printf '%-9s %s\n' "$1" "$2" >&2; }
files() { if [ "$1" = 1 ]; then echo "1 file"; else echo "$1 files"; fi; }
usage() { say usage "sh tools/pick.sh [A [B]]  - no args: the working tree against origin/main (or the unmerged batch it is built on); A: against A; A B: what B has that A has not"; exit 2; }

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
REF=""; P=""   # REF: the commit whose files are read, empty: the working tree; P: the unmerged batch the tree is built on (RL1)
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
        P=$(sh tools/carries.sh | tail -n 1); [ -z "$P" ] || BASE=origin/batch/$P
    fi
    MB=$(git merge-base HEAD "$BASE" 2>/dev/null)
    RANGE=$MB
fi
if [ -z "$MB" ]; then
    echo ALL
    say pick "ALL - no common ancestor with $BASE here (a shallow clone, or no such branch): the whole suite"
    exit 0
fi
SHORT=$(git rev-parse --short=8 "$MB"); [ -z "$P" ] || SHORT="$SHORT, $P's tip (not on main yet)"

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
    if (f == src "/AllChecks.java" || f == src "/Timing.java" ||
        f == src "/KnownReds.java" || f == "batch-check.sh") { print "ALL\t" f " runs the suite"; next }
    if (index(f, src "/") == 1 && f ~ /\.java$/) {
        rel = substr(f, length(src) + 2)
        if (rel !~ /\// && (cls ~ /Check$/ || cls == "LongPlaytest")) {
            if (s != "D") print "H\t" cls
            if (s != "D" && cls ~ /Check$/) print "H\tTimingCheck"   # TM3 item 8: a harness written or changed is read by the source rules of TimingCheck
            print "G\t" cls
            next
        }
        if (cls in idx) {
            if (idx[cls] == "") { print "Z\t" cls ".java"; next }
            n = split(idx[cls], a, ",")
            if (n > fan) { print "ALL\t" cls ".java (" n " readers)"; next }
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
ZERO=$(awk -F'\t' '$1 == "Z" { print $2 }' "$TMP/ev" | sort -u | paste -sd, - | sed 's/,/, /g')
[ -z "$ZERO" ] || say pick "read by no harness: $ZERO"
NOT=$(awk -F'\t' '$1 == "N"' "$TMP/ev" | wc -l | tr -d ' ')
[ "$NOT" = 0 ] || say pick "not a harness's: $(files "$NOT")"
review
exit 0
