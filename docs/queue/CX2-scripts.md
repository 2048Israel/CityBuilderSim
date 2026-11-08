# CX2 - the measuring scripts

The scripts behind `docs/queue/CX2-notes.md`, kept as text so CX2-M can rerun
them, as CX1's are in `docs/queue/CX1-scripts.md`. They read the transcripts
under `~/.claude/projects/*CityBuilderSim*` only (JSONL; a subagent's own file
is in `<session>/subagents/`), write nothing into the repository and need only
Python 3. Extract them into a scratch folder:

    awk '/^### cx2_/{f=$2; next} /^```python/{on=1; next} /^```$/{on=0} on{print > f}' docs/queue/CX2-scripts.md

then, in that folder: `python cx2_measure.py 7 cx2_opus.json` (days, output:
every Opus transcript begun in the window, its kind, steps, context per step,
compactions, the start by part, the climb by kind, the tools called);
`python cx2_report.py cx2_opus.json [head|start|climb|tools]` (the tables);
`python cx2_more.py [ever|reads|caps]` (every tool the folder ever called and
when; the Bash file reads by target; context by step and the share past a
step); `python cx2_steps.py` (what each step was for and what it re-sent);
`python cx2_model.py` (the week replayed with a lighter start and compaction
at a context cap); `python cx2_calib.py` (characters to tokens: a step's growth
regressed on what came between). Kinds are told apart by the session's title
(a batch id, `Study`, `orchestrator`) and a subagent's description (`review`);
`CX2_PERSONAL` (comma-separated words) names the titles of Jerus's own chats to leave out; CX2 used two, kept out of the repository. For
CX2-M, run it with the days since CX2 reached main and read the batch rows.

### cx2_measure.py

```python
# CX2 measurement: the Opus sessions and Opus subagents of the last N days, from the CityBuilderSim transcripts. Read-only.
# Per transcript: its kind, steps, context per step, tokens, compactions, the start by part, the climb by kind, the tools called.
import json, os, glob, sys, re, datetime, collections
DAYS = float(sys.argv[1]) if len(sys.argv) > 1 else 7
OUT = sys.argv[2] if len(sys.argv) > 2 else "cx2_opus.json"
ROOT = os.path.expanduser("~/.claude/projects")
now = datetime.datetime.now(datetime.timezone.utc)
since = now - datetime.timedelta(days=DAYS)
BATCH_ID = re.compile(r"^[A-Z]{1,3}\d+[a-z]?\b")
PERSONAL = tuple(w for w in os.environ.get("CX2_PERSONAL", "").split(",") if w)  # words of the titles of Jerus's own chats, left out


def ts(s):
    return datetime.datetime.fromisoformat(s.replace("Z", "+00:00"))


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
                elif b.get("type") == "tool_reference":
                    n += 0  # counted through the tools' shape instead
                else:
                    n += len(json.dumps(b))
            else:
                n += len(str(b))
        return n
    return len(json.dumps(c)) if c is not None else 0


def kind_of_session(title, first):
    t = title or ""
    f = (first or "")[:400]
    if any(p in t for p in PERSONAL):
        return "personal"
    if t.startswith("CX2"):
        return "this batch"
    if "orchestrator" in t.lower() or re.search(r"orchestrator for today|you are the orchestrator", f, re.I):
        return "orchestrator"
    if t.startswith("Study") or "Find the credit" in t:
        return "study"
    if BATCH_ID.match(t):
        return "batch"
    return "chat with Jerus"


def kind_of_sub(desc, agent_type):
    d = desc or ""
    if agent_type == "Explore":
        return "sub: Explore"
    if agent_type == "assistant-review":
        return "sub: assistant-review"
    if re.search(r"review", d, re.I):
        return "sub: gate review"
    if BATCH_ID.match(d) or d.startswith("Batch "):
        return "sub: batch as agent"
    return "sub: other"


def classify(name, inp):
    if name == "Read":
        return "Read part" if (inp.get("offset") or inp.get("limit")) else "Read whole"
    if name in ("Grep", "Glob"):
        return "Grep/Glob tool"
    if name in ("Edit", "Write", "NotebookEdit"):
        return "Edit/Write"
    if name in ("Bash", "PowerShell"):
        c = inp.get("command", "")
        piped = bool(re.search(r"\|\s*(grep|tail|head|sed|awk)\b", c))
        if re.search(r"\bship\.sh\b", c):
            return "ship.sh"
        if re.search(r"batch-check\.sh|AllChecks|\w+Check\b|LongPlaytest", c):
            return "batch-check/harness" + (" (filtered)" if piped else " (raw)")
        if "wait.sh" in c:
            return "wait.sh"
        if "day.sh" in c:
            return "day.sh"
        if re.search(r"\bgit (diff|show)\b", c):
            return "git diff/show" + (" --stat" if re.search(r"--stat|--numstat|--name-only|--name-status", c) else "")
        if re.search(r"\bgit\b", c):
            return "git other"
        if "tools.Where" in c:
            return "Where"
        if re.search(r"\b(sed -n|cat|head|tail|Get-Content)\b", c):
            return "cat/sed/head (file read)"
        if re.search(r"\b(grep|rg|find|ls|wc)\b", c):
            return "grep/find/ls"
        return "Bash other"
    if name in ("Agent", "Task"):
        if inp.get("subagent_type") == "slice" or (inp.get("model") or "").startswith("sonnet"):
            return "slice report"
        return "subagent result"
    if name == "ToolSearch":
        return "ToolSearch"
    if name in ("SendMessage", "ReadNotifications", "ListAgents") or name.startswith("mcp__ccd_session"):
        return "sessions/messages tools"
    if name.startswith("mcp__ccd_turn"):
        return "plan/wrap-up"
    if name.startswith("mcp__"):
        return "MCP: " + name.split("__")[1]
    return "other: " + name


def user_text_kind(s):
    s = s.lstrip()
    if s.startswith("<task-notification>"):
        return "in: task notifications"
    if "<side-sessions-event>" in s[:300]:
        return "in: side-session events"
    if "Linked sessions now" in s[:300]:
        return "in: linked-session notes"
    if s.startswith("<system-reminder>"):
        return "in: system reminders"
    return "in: user/session messages"


rows = []
for d in glob.glob(os.path.join(ROOT, "*CityBuilderSim*")):
    for path in glob.glob(os.path.join(d, "**", "*.jsonl"), recursive=True):
        if os.path.getmtime(path) < since.timestamp():
            continue
        sub = os.sep + "subagents" + os.sep in path
        meta = {}
        if sub and os.path.exists(path[:-6] + ".meta.json"):
            try:
                meta = json.load(open(path[:-6] + ".meta.json", encoding="utf8"))
            except ValueError:
                pass
        recs = []
        for line in open(path, encoding="utf8", errors="replace"):
            try:
                recs.append(json.loads(line))
            except ValueError:
                pass
        steps = collections.OrderedDict()
        step_ts = {}
        model = None
        for r in recs:
            if r.get("type") == "assistant":
                m = r.get("message") or {}
                if m.get("usage") and m.get("model") and not str(m["model"]).startswith("<"):
                    k = m.get("id") or r.get("uuid")
                    steps[k] = m["usage"]
                    model = m["model"]
                    step_ts.setdefault(k, r.get("timestamp"))
        if not steps or "opus" not in (model or ""):
            continue
        t0 = next((r["timestamp"] for r in recs if r.get("timestamp")), None)
        if t0 is None or ts(t0) < since:
            continue
        ids = list(steps.keys())
        idx = {k: i for i, k in enumerate(ids)}
        n = len(ids)
        ctx = [ctxof(u) for u in steps.values()]
        outs = [(u.get("output_tokens") or 0) for u in steps.values()]
        title = next((r.get("customTitle") for r in reversed(recs) if r.get("type") == "custom-title"), None)
        first = None
        for r in recs:
            if r.get("type") == "queue-operation" and r.get("content"):
                first = r["content"]
                break
            if r.get("type") == "user":
                c = r["message"].get("content")
                first = c if isinstance(c, str) else " ".join(b.get("text", "") for b in c if isinstance(b, dict) and b.get("type") == "text")
                break
        kind = kind_of_sub(meta.get("description"), meta.get("agentType")) if sub else kind_of_session(title, first)
        # ---- the start: everything before the first assistant record
        start = collections.Counter()
        tools_by = {}
        shapes = [r for r in recs if r.get("type") == "api-request-shape"]
        if shapes:
            sh = shapes[0]["shape"]
            start["system prompt"] = len(json.dumps(sh.get("system")))
            for x in sh.get("tools", []):
                nm = x.get("name", "?")
                ch = len(json.dumps(x))
                tools_by[nm] = ch
                start[("tools: MCP " + nm.split("__")[1]) if nm.startswith("mcp__") else "tools: built-in"] += ch
        for r in recs:
            t = r.get("type")
            if t == "assistant":
                break
            if t == "attachment":
                a = r["attachment"]
                ty = a.get("type")
                if ty == "instructions":
                    for fi in a.get("files", []):
                        start["CLAUDE.md" if fi.get("type") == "Project" else "memory index"] += len(fi.get("content", ""))
                elif ty == "prompt_snapshot":
                    pass
                elif ty in ("skill_listing", "deferred_tools_delta", "mcp_instructions_delta", "agent_listing_delta", "session_context"):
                    start[ty] += len(json.dumps({k: v for k, v in a.items() if k != "type"}))
                else:
                    start["other attachments"] += len(json.dumps(a))
            if t == "user":
                start["the prompt"] += textlen(r["message"].get("content"))
        # ---- the climb
        call = {}
        called = collections.Counter()
        for r in recs:
            if r.get("type") != "assistant":
                continue
            m = r.get("message") or {}
            k = m.get("id") or r.get("uuid")
            if k not in idx:
                continue
            for b in m.get("content") or []:
                if isinstance(b, dict) and b.get("type") == "tool_use":
                    nm = b.get("name", "")
                    called[nm] += 1
                    call[b["id"]] = classify(nm, b.get("input") or {})
        add = collections.Counter()
        wt = collections.Counter()
        cnt = collections.Counter()

        def put(kd, ch, si):
            add[kd] += ch
            wt[kd] += ch * max(0, n - 1 - si)
            cnt[kd] += 1

        cur = -1
        compactions = 0
        begun = False
        for r in recs:
            t = r.get("type")
            if t == "assistant":
                m = r.get("message") or {}
                k = m.get("id") or r.get("uuid")
                if k in idx:
                    cur = idx[k]
                    begun = True
            if t == "system" and r.get("subtype") == "compact_boundary":
                compactions += 1
            if not begun:
                continue
            if t == "user":
                c = r["message"].get("content")
                if isinstance(c, list):
                    for b in c:
                        if isinstance(b, dict) and b.get("type") == "tool_result":
                            put(call.get(b.get("tool_use_id"), "unknown"), textlen(b.get("content")), cur)
                        elif isinstance(b, dict) and b.get("type") == "text":
                            put(user_text_kind(b.get("text", "")), len(b.get("text", "")), cur)
                elif isinstance(c, str):
                    put(user_text_kind(c), len(c), cur)
            if t == "attachment":
                a = r["attachment"]
                ty = a.get("type")
                if ty == "queued_command":
                    put(user_text_kind(str(a.get("prompt", ""))), len(str(a.get("prompt", ""))), cur)
                elif ty in ("deferred_tools_delta", "mcp_instructions_delta", "skill_listing", "agent_listing_delta"):
                    put("in: listings re-announced", len(json.dumps(a)), cur)
                elif ty in ("deferred_tools_record", "prompt_snapshot"):
                    pass
                else:
                    put("in: attachments (" + ty + ")", len(json.dumps(a)), cur)
        # tool definitions loaded later (each shape after the first)
        prev = sum(len(json.dumps(x)) for x in shapes[0]["shape"].get("tools", [])) if shapes else 0
        st_list = [(ts(step_ts[k]) if step_ts.get(k) else None) for k in ids]
        for s in shapes[1:]:
            tc = sum(len(json.dumps(x)) for x in s["shape"].get("tools", []))
            dlt = tc - prev
            prev = tc
            when = ts(s["timestamp"])
            si = next((i for i, x in enumerate(st_list) if x and x >= when), n - 1)
            if dlt > 0:
                put("tool schemas loaded (ToolSearch)", dlt, si)
        own_w = sum(o * max(0, n - 1 - i) for i, o in enumerate(outs))
        rows.append(dict(path=os.path.relpath(path, ROOT), folder=os.path.basename(d), sub=sub, kind=kind,
                         title=title or meta.get("description"), agentType=meta.get("agentType"), ts=t0, steps=n,
                         ctx=ctx, outs=outs, compactions=compactions, start=dict(start), tools_by=tools_by,
                         add=dict(add), wt=dict(wt), cnt=dict(cnt), own_w=own_w, called=dict(called),
                         cr=sum((u.get("cache_read_input_tokens") or 0) for u in steps.values()),
                         cw=sum((u.get("cache_creation_input_tokens") or 0) for u in steps.values()),
                         inp=sum((u.get("input_tokens") or 0) for u in steps.values())))
json.dump(rows, open(OUT, "w"))
print(len(rows), "opus transcripts", collections.Counter(r["kind"] for r in rows).most_common())
```

### cx2_report.py

```python
# CX2: tables from cx2_opus.json (written by cx2_measure.py). Read-only.
import json, sys, statistics as st, collections
rows = json.load(open(sys.argv[1] if len(sys.argv) > 1 else "cx2_opus.json"))
what = sys.argv[2] if len(sys.argv) > 2 else "all"
K = collections.defaultdict(list)
for r in rows:
    if r["kind"] in ("personal", "this batch"):
        continue
    K[r["kind"]].append(r)
    K["ALL OPUS"].append(r)
TOK = 3.5  # chars per token for results and inputs (calib.py: 3.53 over 12,643 Opus steps; output coefficient 1.11)


def med(xs):
    return st.median(xs) if xs else 0


if what in ("all", "head"):
    print("kind | n | steps med (sum) | start med | mean ctx/step med | step-weighted mean | peak med (max) | tokens sum M | med M | compactions | cache read %")
    for k, rs in sorted(K.items(), key=lambda kv: -sum(sum(r["ctx"]) for r in kv[1])):
        steps = [r["steps"] for r in rs]
        allctx = [c for r in rs for c in r["ctx"]]
        tok = [sum(r["ctx"]) + sum(r["outs"]) for r in rs]
        cr = sum(r["cr"] for r in rs) / max(1, sum(sum(r["ctx"]) for r in rs))
        print(f"{k} | {len(rs)} | {med(steps):.0f} ({sum(steps)}) | {med([r['ctx'][0] for r in rs])/1e3:.1f}k | {med([sum(r['ctx'])/r['steps'] for r in rs])/1e3:.0f}k | {sum(allctx)/len(allctx)/1e3:.0f}k | {med([max(r['ctx']) for r in rs])/1e3:.0f}k ({max(max(r['ctx']) for r in rs)/1e3:.0f}k) | {sum(tok)/1e6:.0f} | {med(tok)/1e6:.1f} | {sum(r['compactions'] for r in rs)} | {cr*100:.0f}%")

if what in ("all", "start"):
    print("\nTHE START: median chars by part, and the calibration (first-step context / start chars)")
    for k in ("batch", "orchestrator", "sub: gate review", "study", "chat with Jerus", "sub: Explore", "sub: batch as agent"):
        rs = K.get(k, [])
        if not rs:
            continue
        ratio = med([sum(r["start"].values()) / r["ctx"][0] for r in rs if r["start"]])
        parts = collections.defaultdict(list)
        for r in rs:
            for p in set(p for x in rs for p in x["start"]):
                parts[p].append(r["start"].get(p, 0))
        print(f"-- {k}: n {len(rs)}, start med {med([r['ctx'][0] for r in rs])/1e3:.1f}k tokens, {med([sum(r['start'].values()) for r in rs])/1e3:.0f}k chars, chars/token {ratio:.2f}")
        for p, xs in sorted(parts.items(), key=lambda kv: -med(kv[1])):
            m = med(xs)
            if m >= 300:
                print(f"   {p:34} {m/1e3:6.1f}k chars {m/ratio/1e3:5.1f}k tok")

if what in ("all", "climb"):
    print("\nTHE CLIMB: share of each kind's re-sent tokens (start x steps, own output, results by kind)")
    for k in ("batch", "orchestrator", "sub: gate review", "study", "chat with Jerus", "ALL OPUS"):
        rs = K.get(k, [])
        if not rs:
            continue
        total = sum(sum(r["ctx"]) for r in rs)
        startw = sum(r["ctx"][0] * r["steps"] for r in rs)
        ownw = sum(r["own_w"] for r in rs)
        w = collections.Counter(); a = collections.Counter(); c = collections.Counter()
        for r in rs:
            for kd, v in r["wt"].items():
                w[kd] += v / TOK; a[kd] += r["add"][kd] / TOK; c[kd] += r["cnt"][kd]
        print(f"-- {k}: re-sent {total/1e6:.0f}M; start {startw/total*100:.0f}%, own output {ownw/total*100:.0f}%, results+inputs {sum(w.values())/total*100:.0f}%")
        for kd, v in w.most_common(16):
            print(f"   {kd:38} {c[kd]:6} calls {a[kd]/max(1,c[kd]):7.0f} tok mean {v/total*100:5.1f}%")

if what in ("all", "tools"):
    print("\nTOOLS CALLED: by kind, calls (transcripts calling it)")
    for k in ("batch", "orchestrator", "sub: gate review", "study", "chat with Jerus", "sub: Explore", "sub: batch as agent", "sub: other"):
        rs = K.get(k, [])
        cc = collections.Counter(); tr = collections.Counter()
        for r in rs:
            for nm, v in r["called"].items():
                key = nm if not nm.startswith("mcp__") else "mcp__" + nm.split("__")[1] + ("__" + nm.split("__")[2] if nm.split("__")[1].startswith("ccd") else "")
                cc[key] += v; tr[key] += 1
        print(f"-- {k}: " + ", ".join(f"{nm} {v} ({tr[nm]})" for nm, v in cc.most_common()))
```

### cx2_more.py

```python
# CX2 pass 2. (a) every tool any transcript in the CityBuilderSim folders ever called, with its last date and the kinds
# of session; (b) the Bash file reads of the Opus kinds by target; (c) context by step and the share past a step cap.
import json, os, glob, re, sys, collections, statistics as st
ROOT = os.path.expanduser("~/.claude/projects")
what = sys.argv[1] if len(sys.argv) > 1 else "all"
if what in ("all", "ever"):
    last = {}; n = collections.Counter(); first_seen = None
    for d in glob.glob(os.path.join(ROOT, "*CityBuilderSim*")):
        for path in glob.glob(os.path.join(d, "**", "*.jsonl"), recursive=True):
            for line in open(path, encoding="utf8", errors="replace"):
                if '"tool_use"' not in line or '"assistant"' not in line:
                    continue
                try:
                    o = json.loads(line)
                except ValueError:
                    continue
                t = (o.get("timestamp") or "")[:10]
                if first_seen is None or t < first_seen:
                    first_seen = t
                for b in (o.get("message") or {}).get("content") or []:
                    if isinstance(b, dict) and b.get("type") == "tool_use":
                        nm = b.get("name", "")
                        n[nm] += 1
                        if t > last.get(nm, ""):
                            last[nm] = t
    print("transcripts from", first_seen)
    print(", ".join(f"{k} {v} ({last[k][5:]})" for k, v in sorted(n.items())))
if what in ("all", "reads"):
    rows = json.load(open("cx2_opus.json"))
    for kind in ("batch", "sub: gate review", "study", "orchestrator"):
        tgt = collections.Counter(); ch = collections.Counter()
        for r in rows:
            if r["kind"] != kind:
                continue
            recs = [json.loads(l) for l in open(os.path.join(ROOT, r["path"]), encoding="utf8", errors="replace") if l.strip()]
            cmd = {}
            for o in recs:
                if o.get("type") == "assistant":
                    for b in (o.get("message") or {}).get("content") or []:
                        if isinstance(b, dict) and b.get("type") == "tool_use" and b.get("name") in ("Bash", "PowerShell", "Read"):
                            inp = b.get("input") or {}
                            cmd[b["id"]] = inp.get("command") or ("Read " + inp.get("file_path", ""))
                elif o.get("type") == "user" and isinstance(o["message"].get("content"), list):
                    for b in o["message"]["content"]:
                        if isinstance(b, dict) and b.get("type") == "tool_result" and b.get("tool_use_id") in cmd:
                            c = cmd[b["tool_use_id"]]
                            if not (c.startswith("Read ") or (re.search(r"\b(sed -n|cat|head|tail|Get-Content)\b", c) and not re.search(r"batch-check|AllChecks|Check\b|ship\.sh|wait\.sh|day\.sh|\bgit (diff|show)|tools\.Where", c))):
                                continue
                            cc = b.get("content")
                            L = len(cc) if isinstance(cc, str) else sum(len(x.get("text", "")) for x in (cc or []) if isinstance(x, dict))
                            if re.search(r"target/checks|\.log\b|run\.txt|/tmp/|scratch|Temp", c): k = "logs, check output files"
                            elif re.search(r"docs/queue/", c): k = "docs/queue (briefs, notes, ship files)"
                            elif re.search(r"docs/(batch|orchestrator|deepseek)\.md|CLAUDE\.md|QUEUE\.md|memory", c): k = "process docs, QUEUE.md, memory"
                            elif re.search(r"docs/map/", c): k = "docs/map"
                            elif re.search(r"docs/(harnesses|dials|month-order)\.md", c): k = "the big indexes"
                            elif re.search(r"docs/studies|docs/notes|claude/", c): k = "studies, notes, the project"
                            elif re.search(r"Check\.java|Playtest\.java", c): k = "harness source"
                            elif re.search(r"\.java", c): k = "model/ui source"
                            elif re.search(r"\.json|\.sh|\.bat|\.py", c): k = "data and scripts"
                            else: k = "other: " + c[:50].replace("\n", " ")
                            tgt[k] += 1; ch[k] += L
        tot = sum(ch.values())
        print(f"-- {kind}: {sum(tgt.values())} reads, {tot/3.5/1e6:.1f}M tokens added")
        for k, v in ch.most_common(9):
            print(f"   {k:42} {tgt[k]:5} reads {v/3.5/max(1,tgt[k]):6.0f} tok mean {v/tot*100:5.1f}%")
if what in ("all", "caps"):
    rows = json.load(open("cx2_opus.json"))
    for kind in ("batch", "orchestrator", "study", "sub: gate review"):
        rs = [r for r in rows if r["kind"] == kind]
        tot = sum(sum(r["ctx"]) for r in rs)
        by = collections.defaultdict(list)
        for r in rs:
            for i, c in enumerate(r["ctx"]):
                by[i + 1].append(c)
        print(f"-- {kind}: context by step " + ", ".join(f"{s}: {st.median(by[s])/1e3:.0f}k" for s in (1, 10, 25, 50, 100, 150, 200, 300, 400, 600) if len(by[s]) >= 2))
        print("   share of re-sent tokens past step M (sessions past it): " + ", ".join(
            f"{M}: {sum(sum(r['ctx'][M:]) for r in rs)/tot*100:.0f}% ({sum(1 for r in rs if r['steps'] > M)})" for M in (100, 150, 200, 250, 300)))
```

### cx2_steps.py

```python
# CX2 pass 3: what each step was for, and what it cost (a step re-sends the whole context, so its cost is its context).
import json, os, re, collections, sys
ROOT = os.path.expanduser("~/.claude/projects")
rows = json.load(open("cx2_opus.json"))


def step_class(calls):
    if not calls:
        return "reply, no tool (end of turn or text)"
    names = [n for n, _ in calls]
    cmds = " ".join(c for _, c in calls)
    if all(n.startswith("mcp__ccd_turn") or n == "mcp__ccd_session__mark_chapter" for n in names):
        return "plan/wrap-up alone"
    if any(n in ("Agent", "Task") for n in names):
        return "start a slice/subagent"
    if all(n in ("TaskOutput", "Monitor", "ReadNotifications") or re.search(r"wait\.sh|\bsleep\b", c) for n, c in calls):
        return "wait"
    if any(n in ("Edit", "Write", "NotebookEdit") for n in names):
        return "edit/write"
    if re.search(r"batch-check\.sh|AllChecks|Check\b|LongPlaytest|ship\.sh", cmds):
        return "run or read checks"
    if re.search(r"\bgit (commit|push|merge|fetch|checkout|rm|add|tag|worktree|branch)\b", cmds):
        return "git write/fetch"
    if re.search(r"\bgit (diff|show|log)\b", cmds):
        return "read diff/log"
    if any(n in ("Read", "Grep", "Glob") for n in names) or re.search(r"\b(sed -n|cat|head|tail|grep|rg|find|ls|wc|Where)\b", cmds):
        return "read/search"
    if any(n == "SendMessage" or n.startswith("mcp__ccd_session") for n in names):
        return "sessions/messages"
    if any(n == "ToolSearch" for n in names):
        return "load tools"
    return "other"


for kind in ("batch", "orchestrator", "study", "sub: gate review"):
    cost = collections.Counter(); cnt = collections.Counter(); ncalls = collections.Counter()
    tot = 0
    for r in rows:
        if r["kind"] != kind:
            continue
        recs = [json.loads(l) for l in open(os.path.join(ROOT, r["path"]), encoding="utf8", errors="replace") if l.strip()]
        calls = collections.OrderedDict(); ctx = {}
        for o in recs:
            if o.get("type") != "assistant":
                continue
            m = o.get("message") or {}
            if not (m.get("usage") and m.get("model") and not str(m["model"]).startswith("<")):
                continue
            k = m.get("id") or o.get("uuid")
            calls.setdefault(k, [])
            u = m["usage"]
            ctx[k] = (u.get("input_tokens") or 0) + (u.get("cache_creation_input_tokens") or 0) + (u.get("cache_read_input_tokens") or 0)
            for b in m.get("content") or []:
                if isinstance(b, dict) and b.get("type") == "tool_use":
                    inp = b.get("input") or {}
                    calls[k].append((b.get("name", ""), inp.get("command", "") if isinstance(inp.get("command"), str) else ""))
        for k, cl in calls.items():
            c = step_class(cl)
            cost[c] += ctx[k]; cnt[c] += 1; tot += ctx[k]; ncalls[min(len(cl), 3)] += 1
    steps = sum(cnt.values())
    print(f"-- {kind}: {steps} steps, {tot/1e6:.0f}M re-sent; calls per step 0: {ncalls[0]/steps*100:.0f}%, 1: {ncalls[1]/steps*100:.0f}%, 2: {ncalls[2]/steps*100:.0f}%, 3+: {ncalls[3]/steps*100:.0f}%")
    for c, v in cost.most_common():
        print(f"   {c:38} {cnt[c]:5} steps {cnt[c]/steps*100:4.0f}%  {v/tot*100:4.0f}% of re-sent  {v/cnt[c]/1e3:4.0f}k a step")
```

### cx2_model.py

```python
# CX2: the week replayed with a lighter start (D tokens off every step) and a hand-over at a context cap C
# (the successor starts at the old start less D, plus O of orientation, and pays R extra steps there). A model.
import json, sys, statistics as st
rows = json.load(open("cx2_opus.json"))
O, R = 30000, 10


def replay(ctx, D, C):
    out = []; base = 0; cur0 = ctx[0] - D
    for c in ctx:
        v = c - D - base
        if C and v > C:
            base = c - (cur0 + O)            # the successor's context = its start + O + the climb since
            out += [cur0 + O] * R
            v = c - D - base
        out.append(v)
    return out


for kind in ("batch", "orchestrator", "study", "sub: gate review", "ALL"):
    rs = [r for r in rows if (kind == "ALL" and r["kind"] not in ("personal", "this batch")) or r["kind"] == kind]
    base = sum(sum(r["ctx"]) for r in rs)
    line = f"-- {kind}: week {base/1e6:.0f}M, median mean/step {st.median([sum(r['ctx'])/len(r['ctx']) for r in rs])/1e3:.0f}k"
    print(line)
    for D, C in ((7500, 0), (7500, 400000), (7500, 300000), (7500, 250000), (7500, 200000)):
        caps = C if kind != "sub: gate review" else 0
        reps = [replay(r["ctx"], D, caps) for r in rs]
        tot = sum(sum(x) for x in reps)
        hand = sum(len(x) - len(r["ctx"]) for x, r in zip(reps, rs)) // R
        print(f"   start -{D/1e3:.1f}k, hand over at {caps/1e3:.0f}k: {tot/1e6:.0f}M ({(tot/base-1)*100:+.0f}%), median mean/step {st.median([sum(x)/len(x) for x in reps])/1e3:.0f}k, hand-overs {hand}")
```

### cx2_calib.py

```python
# regress per-step context growth on (chars of results/inputs added after step i) and (output tokens of step i)
import json, os, glob, collections
ROOT = os.path.expanduser("~/.claude/projects")
rows = json.load(open("cx2_opus.json"))
X = []
for r in rows:
    if r["kind"] not in ("batch", "orchestrator", "sub: gate review", "study"): continue
    path = os.path.join(ROOT, r["path"])
    recs = [json.loads(l) for l in open(path, encoding="utf8", errors="replace") if l.strip()]
    ids = []; seen = set(); added = collections.Counter(); cur = -1
    for o in recs:
        t = o.get("type")
        if t == "assistant":
            m = o.get("message") or {}
            if m.get("usage") and m.get("model") and not str(m["model"]).startswith("<"):
                k = m.get("id") or o.get("uuid")
                if k not in seen: seen.add(k); ids.append(k); cur = len(ids) - 1
        elif t == "user" and cur >= 0:
            c = o["message"].get("content")
            if isinstance(c, str): added[cur] += len(c)
            elif isinstance(c, list):
                for b in c:
                    if isinstance(b, dict):
                        if b.get("type") == "tool_result":
                            cc = b.get("content")
                            added[cur] += len(cc) if isinstance(cc, str) else sum(len(x.get("text", "")) if isinstance(x, dict) and x.get("type") == "text" else (0 if isinstance(x, dict) and x.get("type") == "tool_reference" else len(json.dumps(x))) for x in (cc or []))
                        elif b.get("type") == "text": added[cur] += len(b.get("text", ""))
        elif t == "attachment" and cur >= 0 and o["attachment"].get("type") == "queued_command":
            added[cur] += len(str(o["attachment"].get("prompt", "")))
    ctx, outs = r["ctx"], r["outs"]
    for i in range(min(len(ids), len(ctx)) - 1):
        g = ctx[i + 1] - ctx[i]
        if 0 <= g < 60000 and added[i] < 150000: X.append((added[i], outs[i], g))
n = len(X); sx = sum(a for a, _, _ in X); so = sum(b for _, b, _ in X); sg = sum(g for *_, g in X)
# least squares, no intercept, two regressors
a11 = sum(a * a for a, _, _ in X); a12 = sum(a * b for a, b, _ in X); a22 = sum(b * b for _, b, _ in X)
b1 = sum(a * g for a, _, g in X); b2 = sum(b * g for _, b, g in X)
det = a11 * a22 - a12 * a12
ca = (b1 * a22 - b2 * a12) / det; cb = (a11 * b2 - a12 * b1) / det
print(f"steps {n}: tokens per result char {ca:.3f} (chars/token {1/ca:.2f}), output coefficient {cb:.2f}")
print(f"growth total {sg/1e6:.1f}M, results {sx*ca/1e6:.1f}M, output {so*cb/1e6:.1f}M (raw output {so/1e6:.1f}M)")
```

