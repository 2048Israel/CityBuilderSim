# DeepSeek - a coder for a Claude builder's slices

When Jerus has the budget, a Claude builder may hand a slice to DeepSeek V4 Pro
through `tools/deepseek/agent.py` instead of a Sonnet `slice` agent: the same
brief, the same review, the same rules (`docs/contract.md`, section 4).
DeepSeek has no branch and no brief of its own, and nothing it writes is
committed until the builder has reviewed it. It is billed by the token on top
of his subscription, so it is not the default (Jerus, 2026-10-20: "for a long
while there will be no deepseek so its sonnet"); Sonnet is its fallback
whenever `DEEPSEEK_API_KEY` is unset or the API fails.

## How

1. **The brief** goes in `target/deepseek/brief-<slice>.md`: the batch and the
   slice; what the game wants, with the spec's lines and the nearest mechanic
   to mirror; each file to write, its shape, signatures and dials, and what is
   not this slice; the harness, modelled on a small one, each section's
   fixture and its line in `AllChecks.HARNESSES`; done means a clean compile
   and the harness exiting 0 (a bare `java ... <Name>` takes
   `-Dcbs.timing=soft` while another JVM runs, TM2).
2. **Run it in the background** and wait for the notification (10-30 minutes):
   `python3 tools/deepseek/agent.py --brief target/deepseek/brief-<slice>.md`
   (on the PC `py` or `python`). It prints only DeepSeek's report, one stats
   line and the dollars spent; its transcript is
   `target/deepseek/run-*/transcript.jsonl`, for grep.
3. **Review** `git diff --stat`, then the diff, as the contract says.
4. **One follow-up**, the points numbered:
   `python3 tools/deepseek/agent.py --followup target/deepseek/review-<slice>.md`
   continues the newest run (`--run <folder>` another). Make the last small
   fixes yourself.
5. **Commit** with a body naming the files DeepSeek drafted ("drafted by
   DeepSeek V4 Pro through tools/deepseek, reviewed by Claude").

`agent.py --help` lists the options (`--model deepseek-flash` for lookups;
`--write` the prefixes it may write, the generated docs refused whatever is
given).

## Access, money and limits

- **The key** is Jerus's DeepSeek account ($20 topped up, 2026-10-17). In a
  cloud session the environment's network secret on `api.deepseek.com`
  injects it; on the PC `DEEPSEEK_API_KEY` is a user environment variable;
  never commit it. The balance: `curl -sS https://api.deepseek.com/user/balance`
  (the script prints `spent_usd` and `balance_after`); below $2, tell Jerus.
- **What leaves the machine:** the brief and every file DeepSeek reads go to
  DeepSeek's servers (Jerus asked for it, 2026-10-17). Never put a save, a
  key or anything personal in a brief.
- **The models** (`curl -sS https://api.deepseek.com/models`):
  `deepseek-v4-pro`, which reasons before it answers, the reasoning counting
  against `--max-tokens`; `deepseek-flash` (V4.1 Flash), which the old name
  `deepseek-chat` now gives.
- **In the cloud** an agent cannot start another agent (depth 1), so a batch
  run as an agent runs DeepSeek through Bash. The rest is in
  `docs/notes/batch-history.md`, "The DeepSeek test and its limits".
