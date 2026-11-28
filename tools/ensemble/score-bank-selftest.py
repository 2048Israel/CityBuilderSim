"""CBK3 bank observations: recorded flows/counters, legacy gaps, and unchanged scoring."""
import csv
import importlib.util
import json
import math
from pathlib import Path
import unittest

TOOLS = Path(__file__).resolve().parent


def module(name, filename):
    spec = importlib.util.spec_from_file_location(name, TOOLS / filename)
    loaded = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(loaded)
    return loaded


score = module("ensemble_score", "score.py")


class BankObservations(unittest.TestCase):
    def frame(self):
        return score.pd.DataFrame({
            "month": [1, 2], "phase": ["auto", "auto"],
            "cb_remitted": [1, 1], "budget_revenue": [1, 100],
            "cb_remittance_share": [1, .01], "bank_failures": [1, 3],
            "bank_in_resolution": [False, False],
        })

    def test_ratio_uses_summed_flows_and_retains_window(self):
        observed = score.bank_observation(self.frame())
        self.assertEqual(observed["cb_remitted_sum"], 2)
        self.assertEqual(observed["budget_revenue_sum"], 101)
        self.assertAlmostEqual(observed["cb_remittance_share"], 2 / 101)
        self.assertNotAlmostEqual(observed["cb_remittance_share"], (1 + .01) / 2)
        self.assertEqual((observed["window_start"], observed["window_end"], observed["months_recorded"]), (1, 2, 2))
        json.dumps(observed, allow_nan=False)

    def test_rescued_failures_use_cumulative_counter(self):
        observed = score.bank_observation(self.frame())
        self.assertEqual(observed["bank_failures"], 3)
        clean = dict(observed, bank_failures=0)
        displayed = score.bank_observation_values({"0": observed, "1": clean})["bank_failures"]
        self.assertIn("3 total; 1/2 runs with failures", displayed)
        missing = dict(observed, bank_failures=None)
        self.assertIn("1/2 runs observed", score.bank_observation_values({"0": observed, "1": missing})["bank_failures"])
        self.assertIn("unavailable", score.bank_observation_values({"0": missing})["bank_failures"])

    def test_missing_legacy_columns_and_baseline_are_unavailable(self):
        observed = score.bank_observation(self.frame().drop(columns=["cb_remitted", "budget_revenue", "bank_failures"]))
        for key in ("cb_remitted_sum", "budget_revenue_sum", "cb_remittance_share", "bank_failures"):
            self.assertIsNone(observed[key])
        self.assertEqual(score.matching_bank_baseline({"0": observed}, {}), {})
        self.assertTrue(all("unavailable" in value for value in score.bank_observation_values({"0": observed}).values()))

    def test_invalid_data_stays_null(self):
        for column in ("cb_remitted", "budget_revenue", "bank_failures"):
            for invalid in (math.nan, math.inf, -math.inf, "unread"):
                with self.subTest(column=column, invalid=invalid):
                    frame = self.frame().astype({column: object})
                    frame.loc[1, column] = invalid
                    observed = score.bank_observation(frame)
                    key = "bank_failures" if column == "bank_failures" else "cb_remittance_share"
                    self.assertIsNone(observed[key])
                    json.dumps(observed, allow_nan=False)
        for revenue in ([0, 0], [-1, -2]):
            frame = self.frame()
            frame["budget_revenue"] = revenue
            self.assertIsNone(score.bank_observation(frame)["cb_remittance_share"])
        for failures in ([3, 1], [1, -1], [1, 1.5]):
            frame = self.frame()
            frame["bank_failures"] = failures
            self.assertIsNone(score.bank_observation(frame)["bank_failures"])

    def test_founding_row_is_not_a_played_flow(self):
        founding = self.frame().iloc[:1].copy()
        founding["month"], founding["phase"] = 0, "founding"
        founding["cb_remitted"], founding["bank_failures"] = math.nan, math.nan
        frame = score.pd.concat([founding, self.frame()], ignore_index=True)
        self.assertEqual(score.bank_observation(frame), score.bank_observation(self.frame()))

    def test_baseline_pairs_seed_and_complete_window(self):
        observed = score.bank_observation(self.frame())
        baseline = {"bank_observations": {"0": observed, "1": observed}}
        self.assertEqual(score.matching_bank_baseline({"0": observed}, baseline), {"0": observed})
        self.assertEqual(score.matching_bank_baseline({"2": observed}, baseline), {})
        for changed in (dict(observed, window_end=3), dict(observed, window_start=0),
                        dict(observed, months_recorded=1)):
            self.assertEqual(score.matching_bank_baseline({"0": changed}, baseline), {})

    def test_cli_persists_observations_without_changing_any_criterion(self):
        fixtures = module("ensemble_score_fixtures", "score-selftest.py")
        fixture = fixtures.Scoring("runTest")
        fixture.maxDiff = None
        fixture.setUp()
        self.addCleanup(fixture.doCleanups)
        before, old_summary = fixture.score()
        for path in fixture.run.glob("*.csv"):
            with path.open(encoding="utf-8", newline="") as stream:
                rows = list(csv.reader(stream))
            rows[0].extend(["cb_remitted", "budget_revenue", "cb_remittance_share", "bank_failures", "cb_remitted_cum"])
            for i, row in enumerate(rows[1:], 1):
                # The supplied monthly share is deliberately unrelated; raw flows define the ratio.
                row.extend([1, 100, .99, 3, i])
            with path.open("w", encoding="utf-8", newline="") as stream:
                csv.writer(stream, lineterminator="\n").writerows(rows)
        after, new_summary = fixture.score()
        self.assertEqual(before.returncode, 0)
        self.assertEqual(after.returncode, before.returncode)
        self.assertEqual(new_summary["criteria"], old_summary["criteria"])

        def without_bank_rows(output):
            return [line for line in output.splitlines() if not any(measure in line for measure in score.BANK_MEASURES.values())]

        self.assertEqual(without_bank_rows(after.stdout), without_bank_rows(before.stdout))
        for policy, end in fixtures.POLICIES.items():
            observed = new_summary["policies"][policy]["bank_observations"]["0"]
            self.assertEqual((observed["window_start"], observed["window_end"]), (0, end))
            self.assertEqual(observed["bank_failures"], 3)
            self.assertAlmostEqual(observed["cb_remittance_share"], .01)
            self.assertEqual(observed["cb_remitted_sum"], end + 1)
            self.assertEqual(observed["budget_revenue_sum"], 100 * (end + 1))
        self.assertIn("unavailable", after.stdout)
        saved = score.read_baseline(str(fixture.root / "summary.json"))
        self.assertEqual(saved["policies"], new_summary["policies"])
        observed_baseline = fixture.root / "observed-baseline.json"
        observed_baseline.write_text(json.dumps(saved), encoding="utf-8")
        repeated, _ = fixture.score(observed_baseline)
        self.assertEqual(repeated.returncode, 0)
        self.assertIn("matching windows 1/1", repeated.stdout)
        bank_rows = [line for line in repeated.stdout.splitlines() if score.BANK_MEASURES["cb_remittance_share"] in line]
        self.assertTrue(all(line.count("1.00% [1.00% to 1.00%]") == 2 for line in bank_rows))


if __name__ == "__main__":
    unittest.main()
