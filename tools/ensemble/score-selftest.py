"""EI1 regression fixtures exercise the scorer CLI and resumed runner without playing cities."""
import csv
import importlib.util
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest

TOOLS = Path(__file__).resolve().parent
POLICIES = {"AUTO2": 2400, "ZERO_SLACK": 2040, "ZERO_HOT": 402, "SHOCK": 840, "HIGH": 720}
# GB3: the gate box has no pandas, so the cases that score run where the scoring does, the laptop; the rest run on both.
_PYLIB = os.environ.get("ENSEMBLE_PYLIB") or os.path.expanduser("~/.cache/cbs-ensemble/py")
if os.path.isdir(_PYLIB):
    sys.path.insert(0, _PYLIB)
needs_pandas = unittest.skipUnless(importlib.util.find_spec("pandas"), "no pandas here: the scoring runs on the laptop (GB3)")


class Scoring(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix="cbs-ei1-")
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name) / "checkout with spaces"
        self.root.mkdir()
        self.run = self.root / "runs" / "fixture"
        self.run.mkdir(parents=True)
        for policy, end in POLICIES.items():
            with (self.run / f"{policy}-0.csv").open("w", encoding="utf-8", newline="") as stream:
                writer = csv.writer(stream, lineterminator="\n")
                writer.writerow(["month", "phase", "index", "infl_yoy", "pop", "fx", "policy_rate",
                                 "unemployment", "rent", "shelf", "world_level", "deposit_rate",
                                 "audit_ok", "findings_new", "finding"])
                for month in range(end + 1):
                    switch = 42 if policy == "ZERO_HOT" else 1800 if policy == "ZERO_SLACK" else 600
                    phase = "pre"
                    if policy == "ZERO_HOT" and month > 282:
                        phase = "stop"
                    elif month > switch:
                        phase = {"ZERO_HOT": "zero", "ZERO_SLACK": "zero", "SHOCK": "shock",
                                 "HIGH": "high", "AUTO2": "auto"}[policy]
                    writer.writerow([month, phase, 1.02 ** (month / 12), .02, 1000 + month,
                                     1, .02, .05, 1, 1, 1, .01, 1, 0, ""])
        self.base = self.root / "baseline.md"
        summary = {"takeoffs": 16, "policies": {p: {"final_pop_median": 1000000,
                   "runs_with_findings": 0, "audit_months_failed": 0} for p in POLICIES}}
        self.base.write_text("| policy | id | measure | median [min to max] | threshold | verdict |\n"
                             "| AUTO2 | T2 | population | 1,000,000 | baseline | PASS |\n"
                             "```json\n" + json.dumps(summary) + "\n```\n", encoding="utf-8")

    def score(self, baseline=None):
        result = subprocess.run([sys.executable, "-I", str(TOOLS / "score.py"), str(self.run),
                                 "--baseline", str(baseline or self.base), "--summary",
                                 str(self.root / "summary.json")], capture_output=True, encoding="utf-8")
        self.assertEqual(result.stderr, "")
        return result, json.loads((self.root / "summary.json").read_text(encoding="utf-8"))

    def runner(self, java=False):
        """The runner and scorer in the fixture checkout, a javac (and java) stub first on the PATH, and its env."""
        tools = self.root / "tools" / "ensemble"
        tools.mkdir(parents=True)
        for name in ("ensemble.sh", "score.py"):
            shutil.copyfile(TOOLS / name, tools / name)
        for folder in ("classes", "logs", "times"):
            (self.run / folder).mkdir()
        (self.run / "deps.txt").write_text("unused\n", encoding="utf-8")
        for policy, end in POLICIES.items():
            (self.run / "times" / f"{policy}-0.txt").write_text(f"{policy} 0 {end} 1\n", encoding="utf-8")
        stub = self.root / "bin"
        stub.mkdir()
        stubs = {"javac": "#!/bin/sh\nexit 0\n"}
        if java:   # records its arguments and writes no CSV, so a run it plays is missing
            stubs["java"] = ('#!/bin/sh\nprintf "%s\\n" "$@" > "$ENS_TEST_ARGS"\n'
                             'echo "DONE AUTO2 seed 0 months 1 of 1 in 1s"\n')
        for name, text in stubs.items():
            (stub / name).write_text(text, encoding="utf-8")
            (stub / name).chmod(0o755)
        env = dict(os.environ, PATH=str(stub) + os.pathsep + os.environ["PATH"], SEEDS="0", RESUME="1",
                   POLICIES=" ".join(POLICIES), JOBS="1", BASELINE=self.base.as_posix(),
                   ENSEMBLE_OUT=self.run.parent.as_posix(), PYTHON=sys.executable, CBS_GATE_HOST="",
                   ENS_TEST_ARGS=(self.root / "java-args.txt").as_posix())
        for name in ("CBS_GATE_ON_BOX", "ENS_PART", "ENS_YIELD", "JDK_JAVA_OPTIONS", "JAVA_OPTS", "ENSEMBLE_DEPS"):
            env.pop(name, None)
        return tools, env

    def ensemble(self, tools, env, *args):
        return subprocess.run(["bash", (tools / "ensemble.sh").as_posix(), *args], env=env,
                              capture_output=True, encoding="utf-8")

    @needs_pandas
    def test_info_passes_and_old_and_new_baselines_compare(self):
        result, summary = self.score()
        self.assertEqual(result.returncode, 0)
        self.assertIn("batch verdict PASS (hard criteria passed 10/10)", result.stdout)
        self.assertIn("changed verdict AUTO2 T2 (info) PASS -> FAIL", result.stdout)
        self.assertIn("| 1,000,000 | PASS |", result.stdout)
        self.assertEqual(summary["criteria"]["AUTO2/T2"]["verdict"], "FAIL")
        for key, entry in summary["criteria"].items():
            self.assertEqual(entry["class"], "hard" if key.split("/")[1] in ("M1", "F1") else "info")
        result, _ = self.score(self.root / "summary.json")
        self.assertEqual(result.returncode, 0)
        self.assertIn("changed verdict AUTO2 T2 (info) FAIL -> PASS", result.stdout)
        legacy = self.root / "legacy.json"
        summary.pop("criteria")
        legacy.write_text(json.dumps(summary), encoding="utf-8")
        result, _ = self.score(legacy)
        self.assertEqual(result.returncode, 0)
        self.assertIn("unavailable", result.stdout)

    @needs_pandas
    def test_hard_failures_stop_every_policy(self):
        for policy in POLICIES:
            path = self.run / f"{policy}-0.csv"
            original = path.read_text(encoding="utf-8")
            for label, tail in [("failed audit", ",0,0,"), ("unread audit", ",,0,"),
                                ("finding", ",1,1,fixture")]:
                with self.subTest(policy=policy, condition=label):
                    path.write_text(original.replace(",1,0,\n", tail + "\n", 1), encoding="utf-8")
                    result, _ = self.score()
                    self.assertEqual(result.returncode, 4)
                    self.assertIn("batch verdict FAIL (hard criteria passed 9/10)", result.stdout)
            path.write_text(original, encoding="utf-8")

    @needs_pandas
    def test_runner_retains_baseline_and_propagates_verdict(self):
        tools, env = self.runner()
        for expected in (0, 4):
            if expected == 4:
                path = self.run / "HIGH-0.csv"
                path.write_text(path.read_text(encoding="utf-8").replace(",1,0,\n", ",0,0,\n", 1), encoding="utf-8")
            result = self.ensemble(tools, env, "fixture")
            self.assertEqual(result.returncode, expected, result.stdout + result.stderr)
            self.assertTrue((self.run / "baseline.md").exists())
            self.assertIn("| class |", (self.run / "baseline.md").read_text(encoding="utf-8"))
            env["BASELINE"] = (self.run / "baseline.md").as_posix()

        # EN2 exercises actual discovery in a checkout with spaces, plus inherited and absent data.
        subprocess.run(["git", "init", "-q", str(self.root)], check=True, capture_output=True)
        data = self.root / "data"
        data.mkdir()
        for name in ("earth-2.bin", "world-1.bin"):
            (data / name).touch()
        (self.root / "target" / "classes" / "ham" / "citybuildersim").mkdir(parents=True)
        deps = self.root / "fixture-deps.txt"
        deps.write_text("unused\n", encoding="utf-8")
        env["ENSEMBLE_DEPS"] = deps.as_posix()
        env["BASELINE"] = self.base.as_posix()
        for inherited, files in (("", True), ("-Dcbs.world=caller-world.bin", True), ("", False)):
            shutil.rmtree(self.run / "classes")
            env["JDK_JAVA_OPTIONS"] = inherited
            if not files:
                shutil.rmtree(data)
            result = self.ensemble(tools, env, "fixture")
            self.assertEqual(result.returncode, 4, result.stdout + result.stderr)
            page = (self.run / "baseline.md").read_text(encoding="utf-8")
            if inherited:
                self.assertIn(inherited, page)
                self.assertNotIn("earth-2.bin", page)
            elif files:
                self.assertIn((data / "earth-2.bin").as_posix(), page)
                self.assertIn((data / "world-1.bin").as_posix(), page)
            else:
                self.assertIn("Data files named for this snapshot — `none`", page)

        baselines = self.root / "docs" / "ensemble"   # the newest by version, not by name: 0.10.0 after 0.8.0
        baselines.mkdir(parents=True)
        for name in ("0.8.0.md", "0.10.0.md"):
            (baselines / name).write_text(self.base.read_text(encoding="utf-8"), encoding="utf-8")
        env.pop("BASELINE")
        result = self.ensemble(tools, env, "fixture")
        self.assertEqual(result.returncode, 4, result.stdout + result.stderr)
        self.assertIn("Scored against `docs/ensemble/0.10.0.md`", (self.run / "baseline.md").read_text(encoding="utf-8"))

        # GB3: the box's runs, copied home without classes or driver, are scored here and say where they ran.
        shutil.rmtree(self.run / "classes")
        (self.run / "snapshot.txt").write_text("version: 0.9.0\ncommit: abc1234\ndate: 2026-11-20T00:00Z\nlabel: fixture\n"
                                               "data: none\nhost: gatebox\njava: openjdk version \"21.0.13\"\n",
                                               encoding="utf-8")
        result = self.ensemble(tools, dict(env, ENS_PART="score"), "fixture")
        self.assertEqual(result.returncode, 4, result.stdout + result.stderr)
        self.assertIn("run on gatebox, scored here", result.stdout)
        self.assertFalse((self.run / "classes").exists())
        self.assertIn('Run on gatebox, openjdk version "21.0.13".', (self.run / "baseline.md").read_text(encoding="utf-8"))

    def test_one_run_passes_each_path_whole_and_waits_out_a_yield(self):
        tools, env = self.runner(java=True)
        args = Path(env["ENS_TEST_ARGS"])
        env.update(ENS_EARTH=(self.root / "earth file.bin").as_posix(), ENS_WORLD=(self.root / "world file.bin").as_posix(),
                   ENS_RUNW=self.run.as_posix(), ENS_SEP=os.pathsep, ENS_DEPS="unused", ENS_JAVA_OPTS="", ENS_LOCK="")
        result = self.ensemble(tools, env, "--one", self.run.as_posix(), "AUTO2", "0")
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        passed = args.read_text(encoding="utf-8").splitlines()
        self.assertIn("-Dcbs.earth=" + env["ENS_EARTH"], passed)
        self.assertIn("-Dcbs.world=" + env["ENS_WORLD"], passed)
        args.unlink()
        (self.root / "yield").touch()   # GB3: a gate came to the box, so a run not yet started does not start
        result = self.ensemble(tools, dict(env, ENS_YIELD=(self.root / "yield").as_posix()),
                               "--one", self.run.as_posix(), "AUTO2", "0")
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertFalse(args.exists())

    def test_the_box_runs_alone_and_says_it_yielded(self):
        tools, env = self.runner(java=True)
        env.update(ENS_PART="runs", PYTHON="false")   # GB3: the box's half needs no pandas and scores nothing
        result = self.ensemble(tools, env, "fixture")
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn("runs: 5 of 5 done, none missing", result.stdout)
        self.assertFalse((self.run / "score.md").exists())
        self.assertFalse((self.run / "baseline.md").exists())
        (self.run / "HIGH-0.csv").unlink()
        (self.run / "snapshot.txt").write_text("data: none\n", encoding="utf-8")   # a resume keeps its data settings
        yielded = dict(env, ENS_YIELD=(self.root / "yield").as_posix())
        result = self.ensemble(tools, yielded, "fixture")
        self.assertEqual(result.returncode, 3, result.stdout + result.stderr)   # played, and no CSV: missing
        (self.root / "yield").touch()
        result = self.ensemble(tools, yielded, "fixture")
        self.assertEqual(result.returncode, 5, result.stdout + result.stderr)   # not played: the box's next pass plays it

    def test_a_named_box_takes_the_runs_and_never_passes_them_on(self):
        tools, env = self.runner()
        record = self.root / "gate-box-args.txt"
        (self.root / "tools" / "gate-box.sh").write_text('printf "%s\\n" "$@" > "$ENS_TEST_ARGS"\nexit 7\n',
                                                         encoding="utf-8")
        env.update(CBS_GATE_HOST="fakebox", PYTHON="true", ENS_TEST_ARGS=record.as_posix())
        result = self.ensemble(tools, env, "fixture")
        self.assertEqual(result.returncode, 7, result.stdout + result.stderr)
        self.assertEqual(record.read_text(encoding="utf-8").splitlines(), ["--ensemble", "fixture"])
        self.assertTrue((self.run / "logs").is_dir())   # the run's folder is here first, for --stop to find
        record.unlink()
        for part in ({"CBS_GATE_ON_BOX": "1"}, {"ENS_PART": "runs"}, {"ENS_PART": "score"}, {"CBS_GATE_HOST": ""}):
            with self.subTest(**part):
                self.ensemble(tools, dict(env, **part), "fixture")
                self.assertFalse(record.exists())


if __name__ == "__main__":
    unittest.main()
