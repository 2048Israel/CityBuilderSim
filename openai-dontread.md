# openai-dontread.md

## Capability note for Claude

This document describes what ChatGPT can contribute to CityBuilderSim and where the connected GitHub environment has limits.

### Current model and environment

- **Model in this ChatGPT session:** GPT-6.
- **Interface:** ChatGPT, using the GitHub connector for repository access and edits.
- **Repository:** `2048Israel/CityBuilderSim`; the connected GitHub account has been able to read and write repository files.
- **Execution environment:** GitHub API/connector operations, not a local checkout or provisioned development machine. I do not have an ordinary repository terminal, Java/Maven runtime, or guaranteed CI runner exposed in this session.
- **Testing implication:** I can inspect source and reason about tests, and I can read CI results if accessible, but I must not say I ran `mvn`, Java tests, harnesses, or long simulations unless an actual available tool ran them and returned the result.
- **Plan/access caveat:** This describes the capabilities exposed in this session, not a promise about every ChatGPT plan or future connector configuration. Access and write ability depend on the active connection and permissions.

### Useful tasks

- Read repository files and docs through the connected GitHub tools, search symbols, and explain code based on inspected sources.
- Reason about Java logic, simulation economics, money flows, accounting invariants, edge cases, and likely bugs.
- Draft scoped code changes, tests, documentation, design notes, and implementation plans.
- Review Claude's proposed plan or patch as an independent second opinion.
- Design regression tests and adversarial scenarios involving inflation, money creation, treasury debt, leverage, defaults, and conservation of money.
- Create or update repository files when the GitHub connector permits the requested action.

### Limits

- GitHub access is connector-based. I do not automatically have a local clone, repository terminal, or unrestricted filesystem.
- I must not claim a build, Java test, harness, batch check, or long simulation passed unless I actually ran it in an available execution environment and observed the result.
- Treat this as the ordinary GitHub connector environment, not a provisioned cloud IDE or guaranteed CI runner. Connector permissions and platform limits apply.
- Design studies and generated code maps may be stale. Inspect current source before making claims about current behavior.
- I cannot see Claude's private conversation, local files, terminal history, or uncommitted changes unless they are shared here.
- Repository instructions remain authoritative. Protect user saves and accounting invariants. Never weaken a test's premise merely to make a change pass.

### Best-fit tasks

1. Source-grounded explanations of existing systems.
2. Patch review for correctness, edge cases, money conservation, and project conventions.
3. Small, well-scoped code or documentation changes, with exact file and commit reporting.
4. Test design and review of actual test output supplied by Claude or a local run.
5. Comparison of current code with a design study, separating implemented, missing, uncertain, and unverified items.
6. Independent review of economic-model choices and assumptions.

### Better handled in a local or CI environment

- Running the full build, automated suite, batch checks, or long playtests when execution is unavailable here.
- Debugging behavior dependent on GUI state, runtime logs, save files, or uncommitted local code I cannot see.
- Large refactors that require repeated build-and-test cycles.
- Work requiring secrets, local credentials, or tools not exposed in this chat.

### Suggested division of labor

- **Claude / local environment:** primary implementation loop when it has the checkout and can run builds and harnesses.
- **ChatGPT here:** repository research, second-opinion review, test design, economic reasoning, documentation, and connector-supported small edits.
- **Project owner:** approves scope and consequential gameplay or accounting decisions.

This is a practical split, not a claim that one assistant is inherently better. Always distinguish what was read, reasoned about, edited, and actually executed or tested.