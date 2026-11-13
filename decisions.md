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

Slice 5 — Evaluations and rubrics.  `Rating`'s `UncommentedEmptyConstructor`
is handled by D2 and is intentionally not duplicated here.

| File and location | Rule | Verdict | Record | Reason |
| --- | --- | --- | --- | --- |
| `evaluation/Rating`, constructors | `CT_CONSTRUCTOR_THROW` (2) | Not worth it | Class-level `@SuppressFBWarnings` | Both constructors call the score validator; rejection is required to preserve the `0..criterion.maxScore` entity invariant before persistence. |
| `evaluation/EvaluationService`, role check | `LiteralsFirstInComparisons` | Real | Literal-first comparison; baseline entry removed | A nonstandard `GrantedAuthority` may return null; putting the constant on the receiver avoids a 500 without changing ordinary role matching. |
| `rubric/CriterionSpecs`, class | `InstantiableUtilityClass` | Real | Private constructor; baseline entry removed | The type contains only static specification factories and should not be instantiated. |
| `rubric/CriterionSpecs`, search terms | `UseLocaleWithCaseConversions` (2) | Real | `Locale.ROOT`; baseline entry removed | Search terms are compared to database `LOWER(...)` values and must not change with the server's default locale. |
| `rubric/RubricSpecs`, class | `InstantiableUtilityClass` | Real | Private constructor; baseline entry removed | The type contains only static specification factories and should not be instantiated. |
| `rubric/RubricSpecs`, search term | `UseLocaleWithCaseConversions` | Real | `Locale.ROOT`; baseline entry removed | Same locale-independent database-search invariant as `CriterionSpecs`. |

## Test evidence (Part 3)

`CriterionSpecsTest#containsCriterionUsesLocaleIndependentLowercaseSearchTerm`
sets the default locale to Turkish and verifies that `TITLE` yields `%title%`,
not `%tıtle%`.  Commit `87668ad` added this test before the production fix. Its
first run failed before the fix with the expected `%tıtle%` actual argument.

## Verification log

- 2026-11-13: inspected repository guidance and analyzer configuration. No
  analyzer configuration or baseline was changed.
- 2026-11-13: counted the project-decision entries in the checked-in
  baselines: D1 219, D2 40, D3 13, D4 4.  The D3 difference from the handout
  was recorded above.
- 2026-11-13: the locale regression test failed before the production fix and
  passed after it (`mvn test -Dtest=CriterionSpecsTest`, using the workspace
  Maven cache because the wrapper's PowerShell launcher is broken here).
