# The queue

The batches waiting to be done, in order, and nothing else: one line each and
a link to its brief. Jerus, 2026-10-11: "a proper md file with just the
batches queued, just indexed and as soon as a batch is done its gone from
that md fully ... just minimal info, just with links or something, not actual
stuff".

- **The top row is next.** A new batch goes on the end unless Jerus places it.
- **Done: delete the row**, in the merge that ships it. What shipped is the
  changelog's to say; this file keeps no history.
- **Not done, or done in part: delete the row and put what was NOT done under
  Pending**, with why - it failed, it waits on Jerus's decision, a caveat, a
  bug.
- **A Pending row leaves when Jerus reviews it**: back into the queue, or
  dropped. Nobody else moves one.
- **No detail here.** The brief holds it - a study in `docs/studies/`, a note
  in `docs/queue/`, or a file in the project, named.

## Queued

_The batches already queued in the project's todo go above PT1: the
orchestrator's to add, one row each. This line goes when they are in._

| batch | what | brief |
|---|---|---|
| PT1 | Eleven small fixes to screens, words and the log, from a played city | [PT1](docs/queue/PT1.md#pt1) |

## Pending

Not done, and waiting on Jerus.

| item | what is left | why it waits | brief |
|---|---|---|---|
| PT1-M1 | One Boutique opening moved the policy rate to 23% | his decision on the model; a harness first | [M1](docs/queue/PT1.md#m1) |
| PT1-M2 | The ground runs out and the city stops, quietly | his choice of three | [M2](docs/queue/PT1.md#m2) |
| PT1-M3 | The advice and automatic building disagree on the same page | his word on the wording | [M3](docs/queue/PT1.md#m3) |
| PT1-M4 | Construction earns with nobody working | is it meant? | [M4](docs/queue/PT1.md#m4) |
