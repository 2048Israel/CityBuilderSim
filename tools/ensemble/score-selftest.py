"""EI1 regression fixtures exercise the scorer CLI and resumed runner without playing cities."""
import csv
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

    def test_runner_retains_baseline_and_propagates_verdict(self):
        tools = self.root / "tools" / "ensemble"
        tools.mkdir(parents=True)
        for name in ("ensemble.sh", "score.py"):
            shutil.copyfile(TOOLS / name, tools / name)
        (self.run / "classes").mkdir()
        (self.run / "logs").mkdir()
        (self.run / "times").mkdir()
        (self.run / "deps.txt").write_text("unused\n", encoding="utf-8")
        for policy, end in POLICIES.items():
            (self.run / "times" / f"{policy}-0.txt").write_text(f"{policy} 0 {end} 1\n", encoding="utf-8")
        stub = self.root / "bin"
        stub.mkdir()
        javac = stub / "javac"
        javac.write_text("#!/bin/sh\nexit 0\n", encoding="utf-8")
        javac.chmod(0o755)
        env = dict(os.environ, PATH=str(stub) + os.pathsep + os.environ["PATH"], SEEDS="0", RESUME="1",
                   POLICIES=" ".join(POLICIES), JOBS="1",
                   BASELINE=self.base.as_posix(), ENSEMBLE_OUT=self.run.parent.as_posix(), PYTHON=sys.executable)
        for expected in (0, 4):
            if expected == 4:
                path = self.run / "HIGH-0.csv"
                path.write_text(path.read_text(encoding="utf-8").replace(",1,0,\n", ",0,0,\n", 1), encoding="utf-8")
            result = subprocess.run(["bash", (tools / "ensemble.sh").as_posix(), "fixture"], env=env,
                                    capture_output=True, encoding="utf-8")
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
        env.pop("JDK_JAVA_OPTIONS", None)
        env.pop("JAVA_OPTS", None)
        env["BASELINE"] = self.base.as_posix()
        for inherited, files in (("", True), ("-Dcbs.world=caller-world.bin", True), ("", False)):
            shutil.rmtree(self.run / "classes")
            env["JDK_JAVA_OPTIONS"] = inherited
            if not files:
                shutil.rmtree(data)
            result = subprocess.run(["bash", (tools / "ensemble.sh").as_posix(), "fixture"], env=env,
                                    capture_output=True, encoding="utf-8")
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

        baselines = self.root / "docs" / "ensemble"
        baselines.mkdir(parents=True)
        old = self.base.read_text(encoding="utf-8")
        (baselines / "0.8.0.md").write_text(old, encoding="utf-8")
        (baselines / "EN2.md").write_text("# Ensemble baseline: 0.9.0\n" + old, encoding="utf-8")
        env.pop("BASELINE")
        for expected in ("EN2.md", "0.10.0.md"):
            if expected != "EN2.md":
                (baselines / expected).write_text(old, encoding="utf-8")
            result = subprocess.run(["bash", (tools / "ensemble.sh").as_posix(), "fixture"], env=env,
                                    capture_output=True, encoding="utf-8")
            self.assertEqual(result.returncode, 4, result.stdout + result.stderr)
            self.assertIn("Scored against `docs/ensemble/" + expected + "`", (self.run / "baseline.md").read_text(encoding="utf-8"))

        # A real --one invocation preserves each discovered path as one Java argument.
        java = stub / "java"
        args = self.root / "java-args.txt"
        java.write_text('#!/bin/sh\nprintf "%s\\n" "$@" > "$ENS_TEST_ARGS"\n'
                        'echo "DONE AUTO2 seed 0 months 1 of 1 in 1s"\n', encoding="utf-8")
        java.chmod(0o755)
        env.update(ENS_TEST_ARGS=args.as_posix(), ENS_EARTH=(self.root / "earth file.bin").as_posix(),
                   ENS_WORLD=(self.root / "world file.bin").as_posix(), ENS_RUNW=self.run.as_posix(),
                   ENS_SEP=os.pathsep, ENS_DEPS="unused", ENS_JAVA_OPTS="", ENS_LOCK="")
        result = subprocess.run(["bash", (tools / "ensemble.sh").as_posix(), "--one",
                                 self.run.as_posix(), "AUTO2", "0"], env=env, capture_output=True, encoding="utf-8")
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        passed = args.read_text(encoding="utf-8").splitlines()
        self.assertIn("-Dcbs.earth=" + env["ENS_EARTH"], passed)
        self.assertIn("-Dcbs.world=" + env["ENS_WORLD"], passed)


if __name__ == "__main__":
    unittest.main()
