# Static-analysis triage record

This file is the working record for the analyzer-triage assignment.  It records
the evidence and rationale behind each choice so the pull request can be
reviewed without relying on an analyzer's message alone.

## Starting point

- Repository baseline date: 2026-11-01.
- SpotBugs baseline: 237 `<Match>` entries (untriaged).
- PMD baseline: 130 findings (untriaged).
- No baseline entry will be added.  A resolved entry will be removed only after
  its fix or documented suppression is in place.
- The assigned slice has not yet been identified; it is determined by the last
  digit of the student's TCU ID.
- The current PMD baseline contains 13 `ControlStatementBraces` entries,
  although the assignment handout says 30.  Treat the checked-in baseline and
  the generated report as the source for the final before/after count; keep
  this discrepancy visible in the PR if it remains.

## Project-wide decisions (Part 1)

| ID | Rule(s) | Scope | Choice | Evidence and checkable rationale | Configuration / baseline work |
| --- | --- | --- | --- | --- | --- |
| D1 | SpotBugs `EI_EXPOSE_REP`, `EI_EXPOSE_REP2` | 219 findings | Pending | Inspect representative JPA entities, DTO records, and Spring constructor injection separately before deciding whether the rule is appropriate for each. | Pending |
| D2 | PMD `UncommentedEmptyConstructor`, `UnnecessaryConstructor` | 40 findings | Pending | Determine whether each no-argument constructor is intentionally required by JPA/Hibernate or is redundant Java code. | Pending |
| D3 | PMD `ControlStatementBraces` | 30 findings | Pending | Inspect the affected one-line control statements and determine whether the project wants a uniform readability/safety rule. | Pending |
| D4 | PMD `GuardLogStatement` | 4 findings | Pending | Check whether debug/trace logging deliberately avoids an enabled-level guard, or whether the guard is redundant with the project's logging API. | Pending |

## Slice triage (Part 2)

Pending the assigned digit.  Each row added here will include source location,
rule, verdict (real / false positive / not worth it), the exact record of the
decision, and a one-line reason.

## Test evidence (Part 3)

Pending the slice.  For a finding judged real and testable, record the test
commit, the failing command before the fix, and the passing command after it.

## Verification log

- 2026-11-13: inspected repository guidance and analyzer configuration. No
  analyzer configuration or baseline was changed.
- 2026-11-13: counted the project-decision entries in the checked-in
  baselines: D1 219, D2 40, D3 13, D4 4.  The D3 difference from the handout
  was recorded above.
