#!/bin/sh
# WHY (EI1): info failures must pass while money audit and findings still stop shipping.
cd "$(dirname "$0")/../.." || exit 1
if [ -z "${PYTHON:-}" ]; then
    if python3 -c 'import sys' >/dev/null 2>&1; then PYTHON=python3; else PYTHON=python; fi
fi
exec "$PYTHON" -I tools/ensemble/score-selftest.py
