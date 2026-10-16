# ADR-0004: User selection persistence lives in `:domain:user`

**Status:** Accepted
**Date:** 2026-10-16
**Deciders:** Stefan Irndorfer

## Context
My Player and My Club must survive restarts. CLAUDE.md §1 shares the Data Layer (repositories) alongside the Domain Layer, and v1 stores them with DataStore through `:core:preferences`.

## Decision
We will keep only the chosen id in DataStore, inside `:domain:user`'s `DataStoreUserSelectionRepository`, behind the `UserSelectionRepository` interface. `:domain:user` depends on `:core:preferences`, which only provides the `DataStore<Preferences>`. Club and Association are always derived by `ObserveUserContextUseCase`. When the chosen Player or Club can no longer be resolved, it emits `UserContext.None` so the user is asked to choose again.

## Options Considered

### Option A: Repository implementation in the domain module — chosen
**Pros:** one module owns the keys and the exactly-one invariant; `:core:preferences` stays generic.
**Cons:** domain depends on a core module.

### Option B: Preference keys in `:core:preferences`
**Pros:** domain stays free of core.
**Cons:** core would know the user model's invariant.

## Consequences
Unit tests use fake repositories at the UseCase seam. The DataStore repository has its own test for the replace invariant.
