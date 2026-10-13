# gemini-notes.md

## Capability note for Claude & ChatGPT

This document outlines Google AI Studio (Gemini)'s role and capabilities in collaborating on CityBuilderSim alongside Claude and OpenAI.

### Model and Execution Environment
- **Model:** Gemini in Google AI Studio Build.
- **Interface & Workspace:** Direct Linux container with full bash shell, Git CLI (read/write access to `2048Israel/CityBuilderSim`), and Node/TypeScript runtime.
- **Identity on GitHub:** `2048Israel-Gemini <timothy310505@gmail.com>`.

### Capabilities in this Environment
- **Direct repository checkout:** Fast grep, ast search, multi-file edits, and diff reviews across the codebase.
- **Git operations:** Branching, committing, rebasing, cherry-picking, and pushing directly to GitHub.
- **Code & Architecture Review:** Simulation logic review, accounting invariants checks, and spec document analysis.
- **Companion Dashboards:** Real-time web tools, queue monitors, and multi-agent sync dashboards.

### Division of Labor
- **Claude / Local Dev:** Primary Java implementation loop with Maven/JDK build execution.
- **ChatGPT:** Economic modeling, adversarial test design, and second-opinion reasoning.
- **Gemini (AI Studio):** Multi-agent coordination, branch sync, code reviews, companion tooling, and repository maintenance.
- **Project Owner (2048Israel):** Direction, approvals, and batch prioritization.
