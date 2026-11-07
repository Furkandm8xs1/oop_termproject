# AI Usage Log — FoodFlow Iteration 1 (Team 11)

This log records how AI tools were used, what we asked, and what we accepted, changed or rejected. All AI output was reviewed by the team before being committed; the team remains responsible for every artifact.

## Summary

| Tool | Used for | Not used for |
|------|----------|--------------|
| Gemini | Git/GitHub branch workflow help; structuring markdown documents; generating PlantUML syntax (use case, SSD, domain model) from the existing Java code; drafting the glossary | Design decisions in diagrams |
| GitHub Copilot | Editing the CI workflow (`.github/workflows/ci.yml`) | Application code, documents |
| Claude | Discussing the project scope; proposing the repository/file hierarchy | Final scope and diagram decisions |

Main area of use: **documentation** (glossary, use cases, vision, report) and diagram syntax.

## Entries

### 1. Git/GitHub branch workflow (Gemini)
- **Asked:** How to organise branches, commits and pull requests for a team project.
- **Accepted:** The branch-per-task workflow and commit-message convention.
- **Changed:** Simplified it to our team size and our CI setup.
- **Rejected:** Nothing significant.

### 2. Documentation structure (Gemini)
- **Asked:** A structure for the vision, use cases and glossary matching the project handbook.
- **Accepted:** The section layout and the numbered-term format of the glossary (term, aliases, BR reference, implementing class).
- **Changed:** We wrote the term definitions and BR references ourselves against the brief and the code, and corrected generic wording.
- **Rejected:** Generic, non-FoodFlow terms that did not map to a BR or a class.

### 3. PlantUML diagrams (Gemini)
- **Asked:** PlantUML syntax for the use-case diagram, the place-order SSD and the domain model, based on the existing Java code.
- **Accepted:** The syntax and layout (`use-cases.puml`, `ssd-place-order.puml`, `domain-model.puml`).
- **Changed / decided by us:** The content of the diagrams (concepts, multiplicities, actors, SSD message flow) was decided by the team, not the AI. We fixed names and associations so they match the glossary.
- **Rejected:** Diagram elements that did not match the domain model or the glossary.

### 4. Project scope discussion (Claude)
- **Asked:** Help discussing the scope of Iteration 1 and how to organise the project.
- **Accepted:** The professional file/folder hierarchy it suggested (separate `docs/`, `docs/diagrams/`, `docs/iterations/`, layered `domain` / `application` / `infrastructure` / `ui` packages). We switched to it because it was cleaner than our earlier layout.
- **Changed:** Scope boundaries and diagram content stayed our own decisions.
- **Rejected:** Suggestions that did not fit the brief.

### 5. CI workflow (GitHub Copilot)
- **Asked:** Help editing `.github/workflows/ci.yml` so the build and tests run on every push.
- **Accepted:** The workflow structure suggested by Copilot (checkout, Java setup, Maven build and test steps).
- **Changed:** We adjusted it to our project (Java version, Maven commands) and checked that the run passes on GitHub.
- **Rejected:** Nothing significant.

## Reflection

- AI helped most with formatting, syntax and structure, which saved time.
- AI was least reliable for domain specifics: it produced generic wording that had to be matched to our BRs and classes.
- Lesson for Iteration 2: record each AI interaction at the time it happens, with the prompt, the output and our decision.
