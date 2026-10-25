#!/usr/bin/env python3
"""A DeepSeek coding agent a Claude session hands a well-scoped slice of a batch to.

WHY: Claude's tokens are the project's cost (CLAUDE.md, "Every line you read is
paid for"). DeepSeek V4 Pro is about a ninth of Claude's price per token and
can write a model class and its harness to the repository's conventions, so a
batch's Claude session writes the brief and reviews the diff, and DeepSeek does
the reading and writing in between. The 2026-10-17 test (docs/deepseek.md) is
where this script came from.

The top agent gets read tools, write tools (only under the --write prefixes),
a short whitelist of checks, and ask_deepseek, which starts a read-only helper
agent (up to --depth levels). It never runs git or any other shell. Its
conversation, transcript and stats go to a run folder under target/deepseek/
(untracked), so a follow-up round continues the same conversation:

    python3 tools/deepseek/agent.py --brief target/deepseek/brief.md
    python3 tools/deepseek/agent.py --followup target/deepseek/review.md   # the newest run
    python3 tools/deepseek/agent.py --followup review.md --run target/deepseek/run-...

It prints only the agent's final report, the stats and the balance spent, so
the calling session's context stays small. The key: in the cloud the network
secret on api.deepseek.com injects it; elsewhere set DEEPSEEK_API_KEY.
"""
import argparse, json, os, re, shlex, shutil, ssl, subprocess, time, urllib.error, urllib.request, uuid

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.realpath(os.path.join(HERE, "..", ".."))
BASE = "https://api.deepseek.com"
CAP = 8000  # bytes a tool result keeps
GSON = os.path.expanduser("~/.m2/repository/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar")
# Generated files no agent writes: the maps and indexes (tools.Maps) and the pulled manual.
GENERATED = ("docs/map/", "docs/dials.md", "docs/harnesses.md", "docs/month-order.md",
             "docs/manual.md", "docs/manual.html")
LOCK = os.environ.get("CBS_JVM_LOCK", "/home/claude/jvm.lock")

cfg = {}  # filled by main(): model, write prefixes, turns, depth, run folder
stats = {"requests": 0, "prompt_tokens": 0, "cache_hit_tokens": 0, "completion_tokens": 0,
         "reasoning_tokens": 0, "deepest_depth": 1, "asks": 0, "top_turns": 0,
         "tool_calls": {}, "writes": []}


def path_in_run(name):
    return os.path.join(cfg["run"], name)


def log(rec):
    rec["t"] = time.strftime("%H:%M:%S")
    with open(path_in_run("transcript.jsonl"), "a", encoding="utf-8") as f:
        f.write(json.dumps(rec) + "\n")


def cap(s):
    b = s.encode("utf-8", "replace")
    if len(b) <= CAP:
        return s
    return b[:CAP].decode("utf-8", "ignore") + f"\n... [truncated: {len(b)} bytes, showing {CAP}]"


def clean(out):
    return "\n".join(l for l in out.splitlines() if not l.startswith("Picked up JAVA_TOOL_OPTIONS"))


def safe(path, write=False):
    if not isinstance(path, str) or not path:
        raise ValueError("path required")
    p = os.path.realpath(os.path.join(REPO, path))
    if p != REPO and not p.startswith(REPO + os.sep):
        raise ValueError(f"path outside the repository: {path}")
    rel = os.path.relpath(p, REPO).replace(os.sep, "/")
    if rel == ".git" or rel.startswith(".git/"):
        raise ValueError("the .git directory is off limits")
    if write:
        if not any(rel.startswith(w) or rel == w for w in cfg["write"]):
            raise ValueError(f"writes are allowed only under {', '.join(cfg['write']) or 'nothing (read-only run)'}: {path}")
        if any(rel.startswith(g) or rel == g for g in GENERATED):
            raise ValueError(f"{rel} is generated (tools.Maps or the manual's pull): never written by hand")
    return p, rel


# ---------------------------------------------------------------- tools

def t_read_file(path, start=1, end=None):
    p, rel = safe(path)
    with open(p, encoding="utf-8", errors="replace") as f:
        lines = f.readlines()
    start = max(1, int(start or 1))
    end = int(end) if end else start + 199
    end = min(end, start + 399, len(lines))  # never a large file whole
    body = "".join(f"{i:6d}\t{lines[i-1]}" for i in range(start, end + 1))
    return cap(f"{rel}: lines {start}-{end} of {len(lines)}\n{body}")


def t_grep(pattern, path=".", max_results=60):
    p, rel = safe(path)
    if shutil.which("rg"):
        cmd = ["rg", "-n", "--no-heading", "-m", "50", "-e", pattern, p]
    else:  # the PC may have no ripgrep; git grep searches the tracked files
        cmd = ["git", "grep", "-n", "-I", "-E", "-e", pattern, "--", rel]
    try:
        out = subprocess.run(cmd, cwd=REPO, capture_output=True, text=True, timeout=60).stdout
    except subprocess.TimeoutExpired:
        return "grep timed out"
    lines = [l.replace(REPO + os.sep, "").replace(REPO + "/", "") for l in out.splitlines()]
    n = len(lines)
    lines = [l[:300] for l in lines[: int(max_results or 60)]]
    return cap(f"{n} matching lines" + (f" (showing {len(lines)})" if n > len(lines) else "") + "\n" + "\n".join(lines))


def t_list_dir(path="."):
    p, rel = safe(path)
    if not os.path.isdir(p):
        return f"not a directory: {rel}"
    rows = []
    for name in sorted(os.listdir(p)):
        q = os.path.join(p, name)
        rows.append(name + "/" if os.path.isdir(q) else f"{name}  ({os.path.getsize(q)} bytes)")
    return cap(f"{rel}/: {len(rows)} entries\n" + "\n".join(rows))


def t_write_file(path, content):
    p, rel = safe(path, write=True)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, "w", encoding="utf-8", newline="\n") as f:
        f.write(content)
    stats["writes"].append(rel)
    return f"wrote {rel}: {content.count(chr(10)) + 1} lines"


def t_replace_in_file(path, old, new):
    p, rel = safe(path, write=True)
    with open(p, encoding="utf-8") as f:
        text = f.read()
    n = text.count(old)
    if n != 1:
        return f"refused: old text occurs {n} times in {rel} (must be exactly once)"
    with open(p, "w", encoding="utf-8", newline="\n") as f:
        f.write(text.replace(old, new, 1))
    stats["writes"].append(rel)
    return f"replaced one occurrence in {rel}"


def run(cmd, timeout=900, jvm=False):
    """Runs a whitelisted command; a JVM waits for the machine's one-JVM lock, as batch-check.sh does."""
    if jvm and os.name != "nt" and not os.environ.get("CBS_LOCKED") and shutil.which("flock") \
            and os.path.isdir(os.path.dirname(LOCK)):
        cmd = ["flock", LOCK] + cmd
    try:
        r = subprocess.run(cmd, cwd=REPO, capture_output=True, text=True, timeout=timeout)
        return r.returncode, clean(r.stdout + r.stderr)
    except subprocess.TimeoutExpired:
        return -1, f"timed out after {timeout}s"


def mvn():
    return shutil.which("mvn") or shutil.which("mvn.cmd") or "mvn"


def others():
    """One look at the machine, as the check scripts take it: (other batches' JVMs, the game's processes), 0 0 if unreadable."""
    game_re = re.compile(r'ham\.citybuildersim\.CityBuilderSim\b|CityBuilderSim-[^/"]*\.jar')
    try:
        if os.name == "nt":
            r = subprocess.run(["powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", "tools/jvms.ps1"],
                               cwd=REPO, capture_output=True, text=True, timeout=60)
            batch, game = r.stdout.split()[:2]
            return int(batch), int(game)
        lines = subprocess.run(["ps", "-eo", "args"], capture_output=True, text=True, timeout=30).stdout.splitlines()
    except (OSError, ValueError, subprocess.SubprocessError):
        return 0, 0
    game = sum(1 for l in lines if game_re.search(l))
    batch = sum(1 for l in lines if re.search(r"ham\.citybuildersim\.", l)
                and not re.search(r"ham\.citybuildersim\.CityBuilderSim\b", l))
    return batch, game


def t_run_check(name, args=""):
    """Whitelisted commands only."""
    if name == "compile":
        code, out = run([mvn(), "-q", "compile"], jvm=True)
        return cap(f"mvn -q compile: exit {code}\n" + (out[-6000:] if out else "(no output: compiled)"))
    if name == "fast":
        if os.name == "nt":
            return "refused: on Windows use 'compile' and 'harness'; the calling session runs Batch check.bat"
        code, out = run(["./batch-check.sh", "--fast"])  # takes the lock itself
        return cap(f"./batch-check.sh --fast: exit {code}\n{out[-6000:]}")
    if name == "harness":
        h = (args or "").strip()
        if not re.fullmatch(r"[A-Z][A-Za-z0-9]*Check", h):
            return "refused: args must be one harness class name ending in Check, e.g. MapCheck"
        code, out = run([mvn(), "-q", "compile"], jvm=True)
        if code != 0:
            return cap(f"compile failed (exit {code}) before the harness:\n{out[-6000:]}")
        if not os.path.exists(os.path.join(REPO, "target", "classes", "ham", "citybuildersim", h + ".class")):
            return f"no such harness class: ham.citybuildersim.{h}"
        batch, game = others()  # one look just before the JVM, as the check scripts take it (TM2)
        who = " and ".join(([f"{batch} other batch JVM(s)"] if batch else []) + (["the game"] if game else []))
        soft = ["-Dcbs.timing=soft"] if who else []  # else it times hard, as a bare java run does
        code, out = run(["java"] + soft + ["-cp", os.pathsep.join(["target/classes", GSON]), f"ham.citybuildersim.{h}"],
                        timeout=900, jvm=True)
        fails = [l for l in out.splitlines() if "FAIL" in l]
        head = f"timing soft - {who} running: a timing miss says SOFT and is not counted\n" if who else ""
        head += f"{h}: exit {code}, {len(fails)} lines with FAIL\n"
        if fails:
            head += "FAIL lines:\n" + "\n".join(fails[:40]) + "\n--- tail of output ---\n"
        return cap(head + out[-5000:])
    if name == "where":
        try:
            a = shlex.split(args or "")
        except ValueError as e:
            return f"refused: {e}"
        if not a or len(a) > 3 or not all(re.fullmatch(r"[A-Za-z0-9_.*\- ]+", x) for x in a):
            return "refused: Where takes a member name (Class.method), a prefix with *, or a SECTION TITLE, and optionally -print"
        code, out = run(["java", "-cp", "target/classes", "ham.citybuildersim.tools.Where"] + a, timeout=120, jvm=True)
        return cap(out)
    return "refused: name must be one of compile, fast, harness, where"


def tool_schemas(depth, write):
    s = [
        {"name": "read_file", "description": "Read lines of a repository file (paths relative to the repo root). At most 400 lines a call; output numbered.",
         "parameters": {"type": "object", "properties": {"path": {"type": "string"}, "start": {"type": "integer"}, "end": {"type": "integer"}}, "required": ["path"]}},
        {"name": "grep", "description": "Search a regex in a file or directory of the repository; returns path:line:text.",
         "parameters": {"type": "object", "properties": {"pattern": {"type": "string"}, "path": {"type": "string"}, "max_results": {"type": "integer"}}, "required": ["pattern"]}},
        {"name": "list_dir", "description": "List a repository directory.",
         "parameters": {"type": "object", "properties": {"path": {"type": "string"}}, "required": ["path"]}},
    ]
    if write:
        where = ", ".join(cfg["write"])
        s += [
            {"name": "write_file", "description": f"Create or overwrite a file under {where} with the whole content (LF line endings).",
             "parameters": {"type": "object", "properties": {"path": {"type": "string"}, "content": {"type": "string"}}, "required": ["path", "content"]}},
            {"name": "replace_in_file", "description": f"Replace text that occurs exactly once in a file under {where}.",
             "parameters": {"type": "object", "properties": {"path": {"type": "string"}, "old": {"type": "string"}, "new": {"type": "string"}}, "required": ["path", "old", "new"]}},
        ]
    if write:
        s.append({"name": "run_check", "description": "Run a whitelisted check. name: 'compile' (mvn -q compile), 'fast' (./batch-check.sh --fast: compile, maps, Stale), 'harness' (args = one harness class, e.g. MapCheck; compiles first), 'where' (args = arguments to tools.Where, e.g. 'SeaRoutes.run -print' or 'THE OFFING').",
                  "parameters": {"type": "object", "properties": {"name": {"type": "string", "enum": ["compile", "fast", "harness", "where"]}, "args": {"type": "string"}}, "required": ["name"]}})
    else:
        s.append({"name": "run_check", "description": "Run tools.Where (read-only): name must be 'where'; args e.g. 'SeaRoutes.run -print', 'Grid*', or a SECTION TITLE.",
                  "parameters": {"type": "object", "properties": {"name": {"type": "string", "enum": ["where"]}, "args": {"type": "string"}}, "required": ["name", "args"]}})
    if depth < cfg["depth"]:
        s.append({"name": "ask_deepseek", "description": f"Start a helper DeepSeek agent (depth {depth + 1} of at most {cfg['depth']}) with read-only tools to research a question in this repository; returns its answer. Give it the question and any context it needs (it does not see this conversation).",
                  "parameters": {"type": "object", "properties": {"question": {"type": "string"}, "context": {"type": "string"}}, "required": ["question"]}})
    return [{"type": "function", "function": f} for f in s]


NESTED_SYSTEM = """You are a research helper inside the CityBuilderSim repository (Java 21, package ham.citybuildersim under src/main/java). You are agent depth {depth} of at most {max}. You have read-only tools: read_file, grep, list_dir, and run_check name='where' (tools.Where: 'Class.method -print' prints one method numbered). Never read a file over 1,500 lines whole: use grep, Where, or docs/map/<File>.md (one file's methods with their lines). {delegate}
Answer the question you are given concisely (at most 300 words), with file:line references, and stop. Do not speculate past what you read."""


# ---------------------------------------------------------------- the API

def ssl_context():
    ca = os.environ.get("REQUESTS_CA_BUNDLE") or os.environ.get("SSL_CERT_FILE")
    return ssl.create_default_context(cafile=ca) if ca and os.path.exists(ca) else ssl.create_default_context()


def http(method, url, body=None, timeout=900):
    """The cloud's proxy injects the key on api.deepseek.com; a dummy bearer is replaced there."""
    key = os.environ.get("DEEPSEEK_API_KEY") or "dummy"
    data = json.dumps(body).encode() if body is not None else None
    req = urllib.request.Request(url, data=data, method=method,
                                 headers={"Authorization": f"Bearer {key}", "Content-Type": "application/json"})
    with urllib.request.urlopen(req, timeout=timeout, context=ssl_context()) as r:
        return json.loads(r.read().decode())


def balance():
    try:
        b = http("GET", BASE + "/user/balance", timeout=30)
        return float(b["balance_infos"][0]["total_balance"])
    except Exception:
        return None


def call_api(messages, tools, depth, agent, turn):
    body = {"model": cfg["model"], "messages": messages, "tools": tools, "max_tokens": cfg["max_tokens"]}
    for attempt in range(6):
        try:
            return http("POST", BASE + "/chat/completions", body)
        except urllib.error.HTTPError as e:
            text = e.read().decode(errors="replace")[:2000]
            log({"kind": "http_error", "depth": depth, "agent": agent, "turn": turn, "status": e.code, "body": text})
            if e.code in (429, 500, 502, 503, 504):
                time.sleep(2 ** attempt * 2)
                continue
            raise RuntimeError(f"HTTP {e.code}: {text[:500]}")
        except (urllib.error.URLError, TimeoutError, OSError) as e:
            log({"kind": "net_error", "depth": depth, "agent": agent, "turn": turn, "error": str(e)})
            time.sleep(2 ** attempt * 2)
    raise RuntimeError("API failed after retries")


def dispatch(name, args, depth, write):
    try:
        if name == "read_file":
            return t_read_file(args.get("path"), args.get("start", 1), args.get("end"))
        if name == "grep":
            return t_grep(args.get("pattern", ""), args.get("path", "."), args.get("max_results", 60))
        if name == "list_dir":
            return t_list_dir(args.get("path", "."))
        if name == "run_check":
            if not write and args.get("name") != "where":
                return "refused: a read-only agent may only run 'where'"
            return t_run_check(args.get("name"), args.get("args", ""))
        if write and name == "write_file":
            return t_write_file(args.get("path"), args.get("content", ""))
        if write and name == "replace_in_file":
            return t_replace_in_file(args.get("path"), args.get("old", ""), args.get("new", ""))
        if name == "ask_deepseek" and depth < cfg["depth"]:
            stats["asks"] += 1
            q, ctx = args.get("question", ""), args.get("context", "")
            return cap(agent_loop(None, f"Question: {q}\n\nContext from the agent asking: {ctx}" if ctx else f"Question: {q}",
                                  depth + 1, write=False, max_turns=cfg["nested_turns"]))
        return f"unknown or forbidden tool: {name}"
    except Exception as e:  # a tool error goes back to the model
        return f"error: {type(e).__name__}: {e}"


def agent_loop(messages, user_text, depth, write, max_turns):
    agent = f"d{depth}-{uuid.uuid4().hex[:6]}"
    stats["deepest_depth"] = max(stats["deepest_depth"], depth)
    if messages is None:
        delegate = ("If part of the question is a separable lookup, you may delegate it with ask_deepseek (the helper does not see this conversation)."
                    if depth < cfg["depth"] else "You cannot delegate further.")
        messages = [{"role": "system", "content": NESTED_SYSTEM.format(depth=depth, max=cfg["depth"], delegate=delegate)}]
    messages.append({"role": "user", "content": user_text})
    log({"kind": "start", "depth": depth, "agent": agent, "user": user_text[:3000]})
    tools = tool_schemas(depth, write)
    final = ""
    for turn in range(1, max_turns + 1):
        if depth == 1:
            stats["top_turns"] += 1
        resp = call_api(messages, tools, depth, agent, turn)
        stats["requests"] += 1
        u = resp.get("usage", {}) or {}
        stats["prompt_tokens"] += u.get("prompt_tokens", 0)
        stats["completion_tokens"] += u.get("completion_tokens", 0)
        stats["cache_hit_tokens"] += u.get("prompt_cache_hit_tokens", 0)
        stats["reasoning_tokens"] += (u.get("completion_tokens_details") or {}).get("reasoning_tokens", 0)
        msg = resp["choices"][0]["message"]
        finish = resp["choices"][0].get("finish_reason")
        calls = msg.get("tool_calls") or []
        log({"kind": "response", "depth": depth, "agent": agent, "turn": turn, "finish": finish, "usage": u,
             "content": (msg.get("content") or "")[:4000], "reasoning": (msg.get("reasoning_content") or "")[:4000],
             "tool_calls": [{"name": c["function"]["name"], "args": c["function"]["arguments"][:3000]} for c in calls]})
        keep = {"role": "assistant", "content": msg.get("content") or ""}
        if msg.get("reasoning_content"):
            keep["reasoning_content"] = msg["reasoning_content"]
        if calls:
            keep["tool_calls"] = calls
        messages.append(keep)
        if not calls:
            final = msg.get("content") or ""
            if finish == "length" and not final:
                messages.append({"role": "user", "content": "You ran out of output room. Continue, more briefly."})
                continue
            break
        for c in calls:
            name = c["function"]["name"]
            stats["tool_calls"][name] = stats["tool_calls"].get(name, 0) + 1
            try:
                args = json.loads(c["function"]["arguments"] or "{}")
                result = dispatch(name, args, depth, write)
            except json.JSONDecodeError as e:
                result = f"error: arguments were not valid JSON: {e}"
            log({"kind": "tool_result", "depth": depth, "agent": agent, "turn": turn, "tool": name, "result": result[:2000]})
            messages.append({"role": "tool", "tool_call_id": c["id"], "content": result})
        if depth == 1:
            save_state(messages)
    else:
        final = final or f"[stopped: {max_turns} turns used]"
    log({"kind": "end", "depth": depth, "agent": agent, "final": final[:4000]})
    if depth == 1:
        save_state(messages)
    return final


def save_state(messages):
    with open(path_in_run("state.json"), "w", encoding="utf-8") as f:
        json.dump(messages, f)


def newest_run():
    root = os.path.join(REPO, "target", "deepseek")
    runs = sorted(d for d in os.listdir(root) if d.startswith("run-")) if os.path.isdir(root) else []
    if not runs:
        raise SystemExit("no earlier run under target/deepseek/: start one with --brief")
    return os.path.join(root, runs[-1])


def main():
    ap = argparse.ArgumentParser(description="Hand a well-scoped slice to a DeepSeek coding agent (docs/deepseek.md).")
    ap.add_argument("--brief", help="the brief, a Markdown file: starts a new run")
    ap.add_argument("--followup", help="a review or an error to answer, a file or text: continues a run")
    ap.add_argument("--run", help="the run folder to continue (default: the newest under target/deepseek/)")
    ap.add_argument("--model", default="deepseek-v4-pro", help="deepseek-v4-pro (default) or deepseek-flash")
    ap.add_argument("--write", default="src/",
                    help="comma-separated path prefixes the agent may write (default src/); '' for a read-only run")
    ap.add_argument("--turns", type=int, default=40, help="the top agent's turns per round (default 40)")
    ap.add_argument("--nested-turns", type=int, default=15, help="a helper's turns (default 15)")
    ap.add_argument("--depth", type=int, default=3, help="how deep helpers may nest, the top agent being 1 (default 3)")
    ap.add_argument("--max-tokens", type=int, default=16000, help="output room per reply; V4 Pro's reasoning counts against it")
    a = ap.parse_args()
    if bool(a.brief) == bool(a.followup):
        ap.error("give --brief (a new run) or --followup (continue one)")

    cfg.update(model=a.model, write=[w.strip() for w in a.write.split(",") if w.strip()], turns=a.turns,
               nested_turns=a.nested_turns, depth=max(1, a.depth), max_tokens=a.max_tokens)
    if a.brief:
        cfg["run"] = a.run or os.path.join(REPO, "target", "deepseek", "run-" + time.strftime("%Y%m%d-%H%M%S"))
        os.makedirs(cfg["run"], exist_ok=True)
        with open(os.path.join(HERE, "system.md"), encoding="utf-8") as f:
            system = f.read()
        system = system.replace("{WRITE}", ", ".join(cfg["write"]) or "nothing: this run is read-only")
        system = system.replace("{DEPTH}", str(cfg["depth"]))
        with open(a.brief, encoding="utf-8") as f:
            text = f.read()
        messages = [{"role": "system", "content": system}]
        mode = "brief"
    else:
        cfg["run"] = a.run or newest_run()
        with open(path_in_run("state.json"), encoding="utf-8") as f:
            messages = json.load(f)
        text = open(a.followup, encoding="utf-8").read() if os.path.isfile(a.followup) else a.followup
        mode = "followup"

    before = balance()
    final = agent_loop(messages, text, 1, write=bool(cfg["write"]), max_turns=cfg["turns"])
    after = balance()
    stats["balance_before"], stats["balance_after"] = before, after
    stats["spent_usd"] = round(before - after, 2) if before is not None and after is not None else None

    rounds_path = path_in_run("rounds.json")
    rounds = json.load(open(rounds_path, encoding="utf-8")) if os.path.exists(rounds_path) else []
    rounds.append({"mode": mode, "model": cfg["model"], **stats})
    with open(rounds_path, "w", encoding="utf-8") as f:
        json.dump(rounds, f, indent=1)
    print("=== FINAL ===")
    print(final[:3000])
    print("=== STATS ===")
    print(json.dumps({"run": os.path.relpath(cfg["run"], REPO), "model": cfg["model"], "requests": stats["requests"],
                      "top_turns": stats["top_turns"], "prompt_tokens": stats["prompt_tokens"],
                      "cache_hit_tokens": stats["cache_hit_tokens"], "completion_tokens": stats["completion_tokens"],
                      "deepest_depth": stats["deepest_depth"], "asks": stats["asks"],
                      "writes": sorted(set(stats["writes"])), "spent_usd": stats["spent_usd"],
                      "balance_after": after}))


if __name__ == "__main__":
    main()
