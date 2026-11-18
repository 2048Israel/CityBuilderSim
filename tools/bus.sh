#!/bin/sh
# sh tools/bus.sh ARGS: the workflow's one way to MemWatch's bus, ARGS passed to cbs-bus (MemWatch's SPEC.md 3, 4, 7).
# cbs-bus is $CBS_BUS, else MemWatch's own; where there is none (the gate box, a cloud session) this does nothing.
# Exit 0 always: state and mail are told, never depended on, so a failing call never fails a gate or a check.
# WHY (BW1, 2026-11-18): MemWatch guessed the workflow's state from logs, and a send-back waited on a person.
BUS=${CBS_BUS:-/c/Users/Jerus/Tools/MemWatch/cbs-bus.exe}
[ -f "$BUS" ] || exit 0
if command -v timeout >/dev/null 2>&1; then timeout 60 "$BUS" "$@"; else "$BUS" "$@"; fi
CODE=$?
[ "$CODE" = 0 ] || echo "bus       cbs-bus $1 failed (exit $CODE); nothing depends on it" >&2
exit 0
