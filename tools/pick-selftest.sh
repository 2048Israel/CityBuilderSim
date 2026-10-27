#!/bin/sh
# ---------------------------------------------------------------------------
#  tools/pick.sh run against a throwaway git repository: a few harness files, a
#  class they name and a harness table, changed in each way a batch changes
#  them. No JVM, about two seconds. For whoever changes pick.sh next. POSIX sh,
#  git and awk.
#
#      sh tools/pick-selftest.sh               test the pick.sh of the current checkout
#      sh tools/pick-selftest.sh <checkout>    test the pick.sh of another checkout (a worktree)
#
#  Exit status: the number of failed cases, so 0 is green.
#
#  WHY (TM3 item 8, 2026-10-27): ET6's new BridgeCheck compared times against its
#  budgets directly, in 3 places; TimingCheck's rule that no harness does so but
#  through Timing caught it only in the gate's full suite, because ET6's own
#  --pick ran BridgeCheck and nothing else. A *Check.java changed or added now
#  picks TimingCheck too, and this holds it, with the rules around it.
# ---------------------------------------------------------------------------

SRC=$(cd "${1:-.}" && pwd) || exit 2
[ -f "$SRC/tools/pick.sh" ] || { echo "pick-selftest: no tools/pick.sh in $SRC"; exit 2; }
TA=$(mktemp -d 2>/dev/null) || { TA="${TMPDIR:-/tmp}/cbs-pick-selftest.$$"; mkdir -p "$TA" || exit 2; }
trap 'rm -rf "$TA"' EXIT
FAILS=0; N=0
ok() { N=$((N + 1)); if "$@" >/dev/null 2>&1; then echo "PASS  $LABEL"; else echo "FAIL  $LABEL (picked: $PICKED; $PERR)"; FAILS=$((FAILS + 1)); fi; }
J=src/main/java/ham/citybuildersim

cd "$TA" || exit 2
git init -q . && git config user.name selftest && git config user.email selftest@example.invalid
mkdir -p tools docs "$J"
cp "$SRC/tools/pick.sh" tools/pick.sh
for h in AllChecks FooCheck BarCheck TimingCheck; do printf 'class %s { }\n' "$h" > "$J/$h.java"; done
printf 'class Widget { }\n' > "$J/Widget.java"
printf 'class Gadget { }\n' > "$J/Gadget.java"
printf '// FooCheck names Widget\nclass FooCheck { Widget w; }\n' > "$J/FooCheck.java"
printf 'class AllChecks { String[] H = { "FooCheck", "BarCheck", "TimingCheck", "NewCheck" }; }\n' > "$J/AllChecks.java"
cat > docs/harnesses.md <<'EOF'
# Harnesses

## Which harnesses read which class

| class | harnesses |
|---|---|
| [Widget](x) | FooCheck |
| [Gadget](x) | none |

## Next
EOF
git add -A && git commit -q -m base && git branch -q -M main
git update-ref refs/remotes/origin/main HEAD

pick() { PICKED=$(sh tools/pick.sh origin/main 2>"$TA/err"); PERR=$(head -2 "$TA/err" | tr '\n' '/'); }
reset() { git reset -q --hard origin/main && git clean -fdq; }

reset; echo "// changed" >> "$J/FooCheck.java"; pick
LABEL="a changed harness picks itself and TimingCheck"; ok test "$PICKED" = "FooCheck TimingCheck"

reset; printf 'class NewCheck { }\n' > "$J/NewCheck.java"; pick
LABEL="an added harness picks itself and TimingCheck"; ok test "$PICKED" = "NewCheck TimingCheck"

reset; echo "// changed" >> "$J/TimingCheck.java"; pick
LABEL="TimingCheck changed picks only itself"; ok test "$PICKED" = "TimingCheck"

reset; echo "// changed" >> "$J/FooCheck.java"; echo "// changed" >> "$J/BarCheck.java"; pick
LABEL="two changed harnesses pick both and TimingCheck once"; ok test "$PICKED" = "BarCheck FooCheck TimingCheck"

reset; git rm -q "$J/FooCheck.java"; printf 'class AllChecks { String[] H = { "BarCheck", "TimingCheck", "NewCheck" }; }\n' > "$J/AllChecks.java"; pick
LABEL="a deleted harness picks nothing, TimingCheck included"; ok test "$PICKED" = "NONE"

reset; echo "// changed" >> "$J/Widget.java"; pick
LABEL="a class a harness reads picks its readers, not TimingCheck"; ok test "$PICKED" = "FooCheck"

reset; echo "// changed" >> "$J/Gadget.java"; pick
LABEL="a class no harness reads picks nothing"; ok test "$PICKED" = "NONE"

reset; echo "# note" >> docs/harnesses.md; pick
LABEL="a document picks nothing"; ok test "$PICKED" = "NONE"

reset; echo "// changed" >> "$J/AllChecks.java"; pick
LABEL="AllChecks changed alone picks nothing (the guard reads it)"; ok test "$PICKED" = "NONE"

reset; echo "// changed" >> "$J/FooCheck.java"; git add -A; git commit -q -m "a harness"; PICKED=$(sh tools/pick.sh origin/main HEAD 2>"$TA/err"); PERR=$(head -2 "$TA/err" | tr '\n' '/')
LABEL="the same through a committed range (A B) picks itself and TimingCheck"; ok test "$PICKED" = "FooCheck TimingCheck"

echo "pick-selftest: $((N - FAILS)) of $N passed"
exit "$FAILS"
