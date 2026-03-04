# Contributing

## Code Style

This project uses KtLint and Detekt for code style enforcement.

```bash
# Run KtLint
./gradlew ktlintCheck

# Auto-fix KtLint issues
./gradlew ktlintFormat

# Run Detekt
./gradlew detekt
```

## Branch Naming

- `feature/short-description` — new features
- `fix/short-description` — bug fixes
- `refactor/short-description` — refactoring
- `docs/short-description` — documentation

## Commit Conventions

Follow Conventional Commits:
- `feat:` — new feature
- `fix:` — bug fix
- `refactor:` — code refactor
- `test:` — adding tests
- `docs:` — documentation
- `ci:` — CI/CD changes

## Pull Request Process

1. Ensure all tests pass: `./gradlew test`
2. Ensure lint passes: `./gradlew lintDebug`
3. Ensure detekt passes: `./gradlew detekt`
4. Update CHANGELOG.md
5. Request review from at least one maintainer

## Test Requirements

- Minimum 80% unit test coverage for new code
- All ViewModels must have unit tests
- All use cases must have unit tests
- Repository implementations must have integration tests

## PR Review Checklist

- [ ] No hardcoded strings (all in strings.xml)
- [ ] No API keys or secrets in code
- [ ] No VIN or PII in logs
- [ ] All Composables have semantics contentDescription
- [ ] Minimum 48×48dp touch targets
- [ ] StateFlow pattern for ViewModel state
- [ ] Offline-first for repository implementations
- [ ] DataResult<T> wrapping for all async operations
