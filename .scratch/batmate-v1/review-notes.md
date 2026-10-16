# Review ledger: batmate-v1

- fixed: Composables missing `@BatScreenPreview` (`BatIconView`, `BatScreenScaffold`, `BatContentBackground`). Enforced by `scripts/check-conventions.sh`.
- fixed: Hardcoded border and padding dp in design-system components. Now `BatSizing`/`BatSpacing` tokens.
- fixed: `GetPlatformNameUseCase` was not `suspend`. CLAUDE.md §2.
- fixed: Duplicated container colours across badge, avatar and live pill. Shared `BatContainerColors`.
- fixed: Restating doc comments on `BatTopBar` and `BatNavigationItem`.
- fixed: `BatScreenScaffold(backgroundStyle = …)` cited in §9 but nonexistent. Doc corrected instead of adding the parameter.
- declined: Rename components to one `View`-suffix scheme. Design-system components are named by role; exception written in `CODING_STANDARDS.md` §5.
- declined: Pass label strings to `BatFormResultView` through a bundled type. The design system owns no string resources.
- declined: ADR for the SKIE and `shared-umbrella` setup. It follows CLAUDE.md.
- declined: Scope-creep notes about design-system, glossary and tickets in the `develop` range. They belong to earlier commits.
- deferred: SKIE Flow and sealed-class conversion is unexercised. Needs a Flow or sealed type, so tickets 06 or 07.
- deferred: `KoinHelper.start()` takes no `appDeclaration`. Nothing on iOS needs a hook yet.
- deferred: CI job running `./gradlew check`. No git remote.
