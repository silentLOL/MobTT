# SKIE bridge: what Swift sees

Facts about the `Shared` framework that the generated Swift does not confess. Verified against SKIE 0.10.15; the generated Swift lives in `shared-umbrella/build/skie/`.

## Errors

- A Kotlin exception thrown from a `suspend` function reaches Swift as an `NSError`. `catch is DataUnavailable` compiles and never matches. `scripts/check-conventions.sh` rejects it.
- An error inside a `Flow` ends the app (`fatalError` in SKIE's iterator). Swift cannot catch it.
- So a call that can fail with `DataUnavailable` returns `DataResult` (`Success(value)` or `Unavailable(error)`), built in `shared-umbrella/.../DataResult.kt` and exposed through `KoinHelper`. Swift matches it with `onEnum(of:)`. Flows are wrapped with `asDataResult()`, single calls with `dataResult { }`.
- Add a `KoinHelper` wrapper for every new use case that can throw `DataUnavailable`; the use case itself stays unchanged for Android.

## Types

- A Kotlin `sealed interface` becomes a non-generic Swift protocol, even when generic in Kotlin (`DataResult`, not `DataResult<T>`).
- A generic payload is erased to `AnyObject?`: cast it (`success.value as? Club`).
- A `Flow` becomes an `AsyncSequence`; use `for await`, and wrap it first if it can fail.

## Testing

Swift ViewModels take their loaders as initializer parameters, so tests pass a `DataResult` built in Swift (`DataResultUnavailable(error: DataUnavailable(message:cause:))`). See `iosApp/iosAppTests/`.
