---
status: ready-for-human
blocked_by: []
---

# 04: Decide: do analytics events carry Player/Club ids

The consent text promises statistics never include "your Player or Club", but `favorite_toggled` and `detail_opened` send the id of the Player, Club or Team. Either drop the `id` parameter on both platforms (and `docs/ANALYTICS.md`) or reword the consent text, the Settings supporting text, the store descriptions and the policy drafts. Decide before the first release.

- [ ] Decision made and applied on Android (iOS follows when it ships)
