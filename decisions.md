# Static-analysis triage record

This file is the working record for the analyzer-triage assignment.  It records
the evidence and rationale behind each choice so the pull request can be
reviewed without relying on an analyzer's message alone.

## Starting point

- Repository baseline date: 2026-11-01.
- SpotBugs baseline: 237 `<Match>` entries (untriaged).
- PMD baseline: 101 class/rule entries in the checked-in file (the handout
  describes 130 PMD report findings; one baseline entry can cover multiple
  violations in a class).
- No baseline entry will be added.  A resolved entry will be removed only after
  its fix or documented suppression is in place.
- Assigned slice: **5 — Evaluations and rubrics**.
- The current PMD baseline contains 13 `ControlStatementBraces` entries,
  although the assignment handout says 30.  Treat the checked-in baseline and
  the generated report as the source for the final before/after count; keep
  this discrepancy visible in the PR if it remains.

## Project-wide decisions (Part 1)

| ID | Rule(s) | Scope | Choice | Evidence and checkable rationale | Configuration / baseline work |
| --- | --- | --- | --- | --- | --- |
| D1 | SpotBugs `EI_EXPOSE_REP`, `EI_EXPOSE_REP2` | 219 findings | Narrow | The rule is disabled only for JPA entities, `dto` package transport objects, Spring components/services/controllers, and five named report/template/security boundary models. Each intentionally shares a persistence graph, JSON payload, injected singleton, or security identity. All other classes remain covered by the rule. | `backend/config/spotbugs-exclude.xml`; all 219 matching baseline entries removed |
| D2 | PMD `UncommentedEmptyConstructor`, `UnnecessaryConstructor` | 40 findings | Turn off | JPA entities require accessible no-argument constructors, while the remaining findings are intentional framework, serialization, or report-model boilerplate. Repeating that purpose in constructor comments adds no behavioral information, so these rules do not fit this project. | `backend/config/pmd-ruleset.xml`; matching baseline tokens removed |
| D3 | PMD `ControlStatementBraces` | 30 findings in handout; 13 class/rule entries locally | Enforce | An early-return guard is still control flow that can later grow a second statement. Braces make its boundary explicit and prevent a future statement from accidentally escaping the condition. | Added braces at every affected site; matching baseline tokens removed |
| D4 | PMD `GuardLogStatement` | 4 findings | Turn off | SLF4J parameterized calls already avoid message formatting when the level is disabled. The affected calls only pass existing values, so an explicit level guard is duplicate work. | `backend/config/pmd-ruleset.xml`; matching baseline tokens removed |

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
- 2026-11-13: applied the approved project decisions. SpotBugs baseline
  matches changed from 237 to 16 (the 219 D1 matches and the two Slice 5
  constructor findings were removed). PMD baseline rule tokens changed from
  101 to 39 after the approved D2-D4 removals and Slice 5 work.
- 2026-11-13: `pmd:check` completed with no `ControlStatementBraces` findings.
  The utility specification classes are final, so their private constructors
  do not introduce `ClassWithOnlyPrivateConstructorsShouldBeFinal` findings.
- 2026-11-13: corrected the four remaining nullness paths in
  `InstructorService`, `StudentService`, and `UserUtils` by using literal-first
  role comparisons. This preserves role matching while safely handling a
  `GrantedAuthority` whose authority is null.
- 2026-11-13: the locale regression test failed before the production fix and
  passed after it (`mvn test -Dtest=CriterionSpecsTest`, using the workspace
  Maven cache because the wrapper's PowerShell launcher is broken here).
- 2026-11-13: resolved Maven dependencies with `dependency:go-offline` and
  generated the PMD report. SpotBugs confirmed the two `Rating` constructor
  findings are gone; the full analyzer-only gate was re-run after the final
  fixes.
- 2026-11-13: final focused verification passed:
  `CriterionSpecsTest` ran with one test, zero failures, and zero errors on
  the committed branch.
