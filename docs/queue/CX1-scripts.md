# CX1 - the measuring scripts

The scripts behind `docs/queue/CX1-notes.md`, kept here as text so CX1-M (the
next ten slices measured against these figures) can rerun them: they read the
session transcripts under `~/.claude/projects/*CityBuilderSim*` (JSONL; each
Sonnet subagent's own file is in `<session>/subagents/`), write nothing into the
repository and need only Python 3 (on the PC, `python`). Not in `tools/`: CX1
changes no `.java` or script, so the gate runs it `--fast` only. Extract them
into a scratch folder:

    awk '/^### cx1_/{f=$2; next} /^```python/{on=1; next} /^```$/{on=0} on{print > f}' docs/queue/CX1-scripts.md

then `python cx1_measure.py 7 slices.json` (days, output), `python cx1_report.py
slices.json` (the start by part, the climb by kind of result; the re-sent weight
of a result is its tokens times the steps after it) and `python cx1_climb2.py`
(the Bash reads split, result sizes, calls per step, cost past a step cap, mean
context by step). Tokens are characters / 2.5 (the regression of context growth
on result characters over 8,758 steps gave 2.47). To measure the slices that use
`slice`, add `and meta.get("agentType") == "slice"` where the files are read.

### cx1_measure.py

```python
# CX1 measurement: Sonnet subagents of the last N days, from the CityBuilderSim transcripts. Read-only.
# Pass 1 per slice: steps, context per step, tokens, the start by part, the climb by kind of tool result.
import json, os, glob, sys, re, datetime, statistics as st, collections

DAYS = float(sys.argv[1]) if len(sys.argv) > 1 else 7
OUT = sys.argv[2] if len(sys.argv) > 2 else os.path.join(os.path.dirname(os.path.abspath(__file__)), "cx1_slices.json")
ROOT = os.path.expanduser("~/.claude/projects")
since = datetime.datetime.now(datetime.timezone.utc) - datetime.timedelta(days=DAYS)


def ctxof(u):
    return (u.get("input_tokens") or 0) + (u.get("cache_creation_input_tokens") or 0) + (u.get("cache_read_input_tokens") or 0)


def textlen(c):
    if isinstance(c, str):
        return len(c)
    if isinstance(c, list):
        n = 0
        for b in c:
            if isinstance(b, dict):
                if b.get("type") == "text":
                    n += len(b.get("text", ""))
                elif b.get("type") == "image":
                    n += 3000
                else:
                    n += len(json.dumps(b))
            else:
                n += len(str(b))
        return n
    return len(json.dumps(c))


def classify(name, inp):
    """kind of a tool call, from its name and input"""
    if name == "Read":
        return "Read whole file" if not (inp.get("offset") or inp.get("limit")) else "Read part"
    if name in ("Grep", "Glob"):
        return "Grep/Glob"
    if name in ("Edit", "Write", "NotebookEdit"):
        return "Edit/Write"
    if name == "Bash" or name == "PowerShell":
        c = inp.get("command", "")
        if re.search(r"\bWhere\b.*-print|\bWhere\b ", c) and "ham.citybuildersim.tools.Where" in c:
            return "Bash: Where"
        if re.search(r"AllChecks|\w+Check\b|\w+Playtest|tools\.\w*Harness|\bbatch-check\.sh\b.*[A-Z]\w+Check", c):
            return "Bash: harness run"
        if re.search(r"batch-check\.sh|mvn|javac|compile", c):
            return "Bash: compile/check"
        if re.search(r"\bgit (diff|log|show|status|stash)", c):
            return "Bash: git"
        if re.search(r"\b(cat|head|tail|sed|less|type|Get-Content)\b", c):
            return "Bash: cat/head/tail/sed"
        if re.search(r"\b(grep|rg|find|ls|wc|awk)\b", c):
            return "Bash: grep/find/ls"
        return "Bash: other"
    if name in ("Agent", "Task"):
        return "Agent"
    if name == "ToolSearch":
        return "ToolSearch"
    return "other tool: " + name


rows = []
files = 0
for d in glob.glob(os.path.join(ROOT, "*CityBuilderSim*")):
    for path in glob.glob(os.path.join(d, "**", "subagents", "*.jsonl"), recursive=True):
        if os.path.getmtime(path) < since.timestamp():
            continue
        files += 1
        meta = {}
        mp = path[:-6] + ".meta.json"
        if os.path.exists(mp):
            try:
                meta = json.load(open(mp, encoding="utf8"))
            except ValueError:
                pass
        recs = []
        with open(path, encoding="utf8", errors="replace") as f:
            for line in f:
                try:
                    recs.append(json.loads(line))
                except ValueError:
                    pass
        # steps = unique assistant message ids, in order, with last-seen usage
        steps = collections.OrderedDict()
        model = None
        for r in recs:
            if r.get("type") == "assistant":
                m = r.get("message") or {}
                if m.get("usage") and m.get("model") and not str(m.get("model")).startswith("<"):
                    steps[m.get("id") or r.get("uuid")] = (m["usage"], r)
                    model = m["model"]
        if not steps or "sonnet" not in (model or ""):
            continue
        ts0 = None
        for r in recs:
            if r.get("timestamp"):
                ts0 = datetime.datetime.fromisoformat(r["timestamp"].replace("Z", "+00:00"))
                break
        if ts0 is None or ts0 < since:
            continue
        ctx = [ctxof(u) for u, _ in steps.values()]
        outs = [(u.get("output_tokens") or 0) for u, _ in steps.values()]
        cw = [(u.get("cache_creation_input_tokens") or 0) for u, _ in steps.values()]
        cr = [(u.get("cache_read_input_tokens") or 0) for u, _ in steps.values()]
        inp_ = [(u.get("input_tokens") or 0) for u, _ in steps.values()]
        # the start by part (chars)
        start = {}
        for r in recs:
            t = r.get("type")
            if t == "api-request-shape" and "shape" not in start and "sys_chars" not in start:
                sh = r["shape"]
                start["sys_chars"] = len(json.dumps(sh.get("system")))
                tl = {x.get("name", "?"): len(json.dumps(x)) for x in sh.get("tools", []) if isinstance(x, dict)}
                start["tools_chars"] = sum(tl.values())
                start["tools_n"] = len(tl)
                start["slice_tools_chars"] = sum(v for k, v in tl.items() if k in ("Bash", "Read", "Edit", "Write", "Grep", "Glob"))
                start["tool_chars_by_name"] = tl
            if t == "attachment":
                a = r["attachment"]
                ty = a.get("type")
                if ty == "skill_listing":
                    start["skills_chars"] = len(a.get("content", ""))
                elif ty == "deferred_tools_delta":
                    start["deferred_chars"] = len(json.dumps(a.get("addedLines", "")))
                elif ty == "instructions":
                    for fi in a.get("files", []):
                        k = "claude_md_chars" if fi.get("type") == "Project" else "memory_chars"
                        start[k] = start.get(k, 0) + len(fi.get("content", ""))
                elif ty == "session_context":
                    start["session_ctx_chars"] = len(json.dumps(a.get("context", "")))
                elif ty in ("environment", "model", "date", "remote_session_change", "prompt_snapshot", "credential_org"):
                    start[ty + "_chars"] = len(json.dumps({k: v for k, v in a.items() if k != "type" and k != "systemPrompt"}))
        first_user = next((r for r in recs if r.get("type") == "user"), None)
        start["brief_chars"] = textlen(first_user["message"]["content"]) if first_user else 0
        # the climb: tool results by kind. map tool_use id -> (kind, step index it was called in)
        call_kind = {}
        ids = list(steps.keys())
        idx = {mid: i for i, mid in enumerate(ids)}
        for r in recs:
            if r.get("type") == "assistant":
                m = r.get("message") or {}
                mid = m.get("id") or r.get("uuid")
                if mid not in idx:
                    continue
                c = m.get("content")
                if isinstance(c, list):
                    for b in c:
                        if isinstance(b, dict) and b.get("type") == "tool_use":
                            call_kind[b["id"]] = (classify(b.get("name", ""), b.get("input") or {}), idx[mid], b.get("name"))
        n = len(ids)
        climb_chars = collections.defaultdict(float)     # chars added, by kind
        climb_weight = collections.defaultdict(float)    # chars x steps re-sent after, by kind
        climb_n = collections.Counter()
        for r in recs:
            if r.get("type") == "user" and isinstance(r.get("message", {}).get("content"), list):
                for b in r["message"]["content"]:
                    if isinstance(b, dict) and b.get("type") == "tool_result":
                        kind, si, nm = call_kind.get(b.get("tool_use_id"), ("unknown", 0, "?"))
                        ch = textlen(b.get("content"))
                        remaining = max(0, n - 1 - si)
                        climb_chars[kind] += ch
                        climb_weight[kind] += ch * remaining
                        climb_n[kind] += 1
        rows.append(dict(
            path=os.path.relpath(path, ROOT), agentType=meta.get("agentType"), desc=meta.get("description"),
            ts=ts0.isoformat(), steps=n, first_ctx=ctx[0], peak_ctx=max(ctx), mean_ctx=sum(ctx) / n,
            total_tokens=sum(ctx) + sum(outs), ctx_sum=sum(ctx), out_sum=sum(outs),
            cache_read_sum=sum(cr), cache_write_sum=sum(cw), input_sum=sum(inp_), out_steps=outs, ctx_steps=ctx,
            first_cw=cw[0], first_cr=cr[0], start=start,
            climb_chars=dict(climb_chars), climb_weight=dict(climb_weight), climb_n=dict(climb_n)))
json.dump(rows, open(OUT, "w"))
print(f"files {files}, sonnet subagents {len(rows)}, written {OUT}")
```

### cx1_report.py

```python
# CX1: tables from cx1_slices.json (written by cx1_measure.py)
import json, sys, os, statistics as st, collections

P = sys.argv[1] if len(sys.argv) > 1 else os.path.join(os.path.dirname(os.path.abspath(__file__)), "cx1_slices.json")
rows = json.load(open(P))
med = st.median


def q(xs, p):
    xs = sorted(xs)
    return xs[min(len(xs) - 1, int(round(p * (len(xs) - 1))))]


n = len(rows)
print(f"slices {n}; agentTypes {collections.Counter(r['agentType'] for r in rows)}")
for k in ("steps", "first_ctx", "peak_ctx", "mean_ctx", "total_tokens"):
    xs = [r[k] for r in rows]
    print(f"{k:13} median {med(xs):>12,.0f}  p25 {q(xs,.25):>12,.0f}  p75 {q(xs,.75):>12,.0f}  max {max(xs):>12,.0f}  mean {sum(xs)/n:>12,.0f}")
tot = sum(r["total_tokens"] for r in rows)
print(f"total tokens {tot/1e9:.2f}B; ctx_sum {sum(r['ctx_sum'] for r in rows)/1e9:.2f}B; out_sum {sum(r['out_sum'] for r in rows)/1e6:.1f}M;"
      f" cache_read {sum(r['cache_read_sum'] for r in rows)/1e9:.2f}B, cache_write {sum(r['cache_write_sum'] for r in rows)/1e9:.2f}B, fresh input {sum(r['input_sum'] for r in rows)/1e6:.1f}M")
steps_all = sum(r["steps"] for r in rows)
print(f"mean ctx per step, all steps: {sum(r['ctx_sum'] for r in rows)/steps_all:,.0f} over {steps_all} steps")

# --- chars -> tokens ratio: growth of context = results + own output
g_tok = 0
g_chars = 0
g_out = 0
for r in rows:
    g_tok += r["ctx_steps"][-1] - r["ctx_steps"][0]
    g_out += sum(r["out_steps"][:-1])
    g_chars += sum(r["climb_chars"].values())
ratio = g_chars / max(1, (g_tok - g_out))
print(f"climb: ctx grew {g_tok/1e6:.1f}M tokens over all slices, own output {g_out/1e6:.1f}M, tool-result chars {g_chars/1e6:.1f}M -> {ratio:.2f} chars/token")

# --- the start by part (chars; tokens at the calibrated ratio)
print("\nTHE START (median chars -> tokens at %.2f chars/token; first step median %s)" % (ratio, f"{med([r['first_ctx'] for r in rows]):,.0f}"))
parts = ["sys_chars", "tools_chars", "slice_tools_chars", "deferred_chars", "skills_chars", "claude_md_chars", "memory_chars", "session_ctx_chars",
         "environment_chars", "model_chars", "date_chars", "remote_session_change_chars", "prompt_snapshot_chars", "brief_chars"]
acc = 0
for p in parts:
    xs = [r["start"].get(p, 0) for r in rows if r["start"]]
    if not xs:
        continue
    m = med(xs)
    print(f"  {p:30} median {m:>9,.0f} chars  ~{m/ratio:>7,.0f} tokens   (min {min(xs):,.0f} max {max(xs):,.0f})")
fc = [r["first_ctx"] for r in rows]
print("  first-step: fresh cache-write median %s, cache-read median %s" % (f"{med([r['first_cw'] for r in rows]):,.0f}", f"{med([r['first_cr'] for r in rows]):,.0f}"))

# --- the climb by kind: tokens added and re-send weight
kinds = collections.Counter()
W = collections.Counter()
C = collections.Counter()
N = collections.Counter()
for r in rows:
    for k, v in r["climb_chars"].items():
        C[k] += v
    for k, v in r["climb_weight"].items():
        W[k] += v
    for k, v in r["climb_n"].items():
        N[k] += v
own_w = 0
start_w = 0
for r in rows:
    s = r["steps"]
    outs = r["out_steps"]
    # own output of step i sits in the context of steps i+1..s-1
    own_w += sum(o * (s - 1 - i) for i, o in enumerate(outs))
    start_w += r["ctx_steps"][0] * s
totW = sum(r["ctx_sum"] for r in rows)
print("\nTHE CLIMB, by kind of tool result (weight = tokens x steps it is re-sent after the call)")
print(f"  {'kind':28} {'calls':>7} {'mean tok':>9} {'tokens added':>13} {'weight':>10} {'% of all re-sent':>16}")
for k, v in sorted(W.items(), key=lambda kv: -kv[1]):
    print(f"  {k:28} {N[k]:>7} {C[k]/ratio/max(1,N[k]):>9,.0f} {C[k]/ratio/1e6:>11.1f}M {v/ratio/1e9:>8.2f}B {100*v/ratio/totW:>15.1f}%")
print(f"  {'own output (thinking+calls)':28} {'':>7} {'':>9} {'':>13} {own_w/1e9:>8.2f}B {100*own_w/totW:>15.1f}%")
print(f"  {'the start (first step x steps)':28} {'':>7} {'':>9} {'':>13} {start_w/1e9:>8.2f}B {100*start_w/totW:>15.1f}%")
acc = sum(W.values()) / ratio + own_w + start_w
print(f"  total re-sent (ctx_sum) {totW/1e9:.2f}B; parts sum {acc/1e9:.2f}B")
```

### cx1_climb2.py

```python
# CX1 pass 2: the climb with the Bash reads split, call size classes, calls per step, cost beyond a step cap. Read-only.
import json, os, glob, re, sys, datetime, collections, statistics as st

RATIO = 2.5
ROOT = os.path.expanduser("~/.claude/projects")
since = datetime.datetime.now(datetime.timezone.utc) - datetime.timedelta(days=7)


def ctxof(u):
    return (u.get("input_tokens") or 0) + (u.get("cache_creation_input_tokens") or 0) + (u.get("cache_read_input_tokens") or 0)


def textlen(c):
    if isinstance(c, str):
        return len(c)
    if isinstance(c, list):
        return sum(len(b.get("text", "")) if isinstance(b, dict) and b.get("type") == "text" else (3000 if isinstance(b, dict) and b.get("type") == "image" else len(json.dumps(b))) for b in c)
    return len(json.dumps(c))


SRC = re.compile(r"\.(java|json|md|sh|txt|bat|xml|properties)\b")


def fine(name, inp):
    if name == "Read":
        return "Read whole" if not (inp.get("offset") or inp.get("limit")) else "Read part"
    if name in ("Grep", "Glob"):
        return "Grep/Glob tool"
    if name in ("Edit", "Write"):
        return "Edit/Write"
    if name not in ("Bash", "PowerShell"):
        return "other tool"
    c = inp.get("command", "")
    c1 = re.sub(r"^(\s*cd\s+[^;&|]+(&&|;)\s*)+", "", c)
    if "tools.Where" in c1:
        return "Where -print/lookup"
    if re.search(r"batch-check\.sh|AllChecks|\bjava\b.*\b\w+(Check|Playtest|Harness)\b|wait\.sh", c1):
        if re.search(r">\s*\S+(\.txt|\.log)", c1) and not re.search(r"\|\s*(tail|head|grep)", c1):
            return "harness/check -> file"
        if re.search(r"\|\s*(tail|head|grep|sed)", c1):
            return "harness/check | tail/grep"
        return "harness/check raw output"
    if re.search(r"^\s*(mvn|javac)\b", c1):
        return "compile raw"
    if re.search(r"^\s*git\b", c1):
        return "git"
    if re.search(r"^\s*sed\s+-n", c1):
        return "sed -n range"
    if re.search(r"^\s*(cat|type|Get-Content)\b", c1):
        return "cat file"
    if re.search(r"^\s*(head|tail)\b", c1):
        return "head/tail file"
    if re.search(r"^\s*(grep|rg|egrep)\b", c1) or re.search(r"\|\s*(grep|rg)\b", c1):
        return "grep in Bash"
    if re.search(r"^\s*(ls|find|wc|stat|du|tree)\b", c1):
        return "ls/find/wc"
    if re.search(r"^\s*(python|py|node|awk|powershell|pwsh)\b", c1):
        return "script"
    return "Bash other"


calls = []          # (kind, tokens, remaining steps, slice id)
nsteps = 0
tools_per_step = []
slices = []
for d in glob.glob(os.path.join(ROOT, "*CityBuilderSim*")):
    for path in glob.glob(os.path.join(d, "**", "subagents", "*.jsonl"), recursive=True):
        if os.path.getmtime(path) < since.timestamp():
            continue
        recs = []
        for line in open(path, encoding="utf8", errors="replace"):
            try:
                recs.append(json.loads(line))
            except ValueError:
                pass
        steps = collections.OrderedDict()
        ntool = collections.Counter()
        kinds = {}
        model = None
        for r in recs:
            if r.get("type") == "assistant":
                m = r.get("message") or {}
                if m.get("usage") and m.get("model") and not str(m["model"]).startswith("<"):
                    mid = m.get("id") or r.get("uuid")
                    steps[mid] = m["usage"]
                    model = m["model"]
                    if isinstance(m.get("content"), list):
                        for b in m["content"]:
                            if isinstance(b, dict) and b.get("type") == "tool_use":
                                ntool[mid] += 1
                                kinds[b["id"]] = (fine(b.get("name", ""), b.get("input") or {}), mid)
        if not steps or "sonnet" not in (model or ""):
            continue
        first_ts = next((r["timestamp"] for r in recs if r.get("timestamp")), None)
        if not first_ts or datetime.datetime.fromisoformat(first_ts.replace("Z", "+00:00")) < since:
            continue
        ids = list(steps)
        pos = {m: i for i, m in enumerate(ids)}
        n = len(ids)
        ctx = [ctxof(steps[m]) for m in ids]
        slices.append(dict(n=n, ctx=ctx, id=os.path.basename(path)))
        nsteps += n
        tools_per_step += [ntool[m] for m in ids]
        for r in recs:
            if r.get("type") == "user" and isinstance(r.get("message", {}).get("content"), list):
                for b in r["message"]["content"]:
                    if isinstance(b, dict) and b.get("type") == "tool_result":
                        k, mid = kinds.get(b.get("tool_use_id"), ("unknown", None))
                        if mid is None:
                            continue
                        calls.append((k, textlen(b.get("content")) / RATIO, max(0, n - 1 - pos[mid]), os.path.basename(path)))

totW = sum(sum(s["ctx"]) for s in slices)
print(f"slices {len(slices)}, steps {nsteps}, re-sent {totW/1e9:.2f}B")

# by fine kind
agg = collections.defaultdict(lambda: [0, 0.0, 0.0])
for k, t, rem, _ in calls:
    a = agg[k]
    a[0] += 1
    a[1] += t
    a[2] += t * rem
print("\nCLIMB BY KIND (fine)")
print(f"{'kind':30} {'calls':>7} {'mean tok':>9} {'added':>8} {'weight':>8} {'% re-sent':>9}")
for k, (c, t, w) in sorted(agg.items(), key=lambda kv: -kv[1][2]):
    if w / totW < 0.002:
        continue
    print(f"{k:30} {c:>7} {t/c:>9,.0f} {t/1e6:>7.1f}M {w/1e9:>7.2f}B {100*w/totW:>8.1f}%")

# by size class
bins = [(0, 500), (500, 1500), (1500, 4000), (4000, 10000), (10000, 1e9)]
print("\nCLIMB BY RESULT SIZE (tokens)")
tw = sum(t * rem for _, t, rem, _ in calls)
for lo, hi in bins:
    sel = [(k, t, rem) for k, t, rem, _ in calls if lo <= t < hi]
    w = sum(t * rem for _, t, rem in sel)
    print(f"  {lo:>6}-{hi if hi<1e9 else 'up':>6}: {len(sel):>6} calls, {sum(t for _,t,_ in sel)/1e6:6.1f}M added, weight {w/1e9:5.2f}B = {100*w/totW:4.1f}% of re-sent")
    if lo >= 4000:
        c = collections.Counter()
        for k, t, rem in sel:
            c[k] += t * rem
        print("      mostly:", ", ".join(f"{k} {100*v/totW:.1f}%" for k, v in c.most_common(4)))

# parallel calls
print(f"\ntool calls per step: mean {sum(tools_per_step)/len(tools_per_step):.2f}; steps with 0 calls {sum(1 for x in tools_per_step if x==0)}, 1 call {sum(1 for x in tools_per_step if x==1)}, 2+ calls {sum(1 for x in tools_per_step if x>=2)}")

# cost beyond a step cap
print("\nCOST OF STEPS PAST A CAP (share of all re-sent tokens in steps with index >= M; slices that run past M)")
for M in (60, 80, 100, 120, 150, 200, 300):
    past = sum(sum(s["ctx"][M:]) for s in slices)
    k = sum(1 for s in slices if s["n"] > M)
    print(f"  M={M:>3}: {k:>3} slices run past it, steps past M hold {100*past/totW:4.1f}% of re-sent tokens")
# mean context by step index
print("\nMEAN CONTEXT AT STEP k (tokens)")
for k in (0, 10, 20, 40, 60, 80, 100, 150, 200):
    xs = [s["ctx"][k] for s in slices if s["n"] > k]
    print(f"  step {k:>3}: {st.mean(xs):>9,.0f}  ({len(xs)} slices)")
# repeated: per slice share of calls to Bash reads
sizes = [s["n"] for s in slices]
print("\nsteps histogram:", {f"<={b}": sum(1 for x in sizes if x <= b) for b in (30, 60, 100, 150, 250, 1000)})
```
