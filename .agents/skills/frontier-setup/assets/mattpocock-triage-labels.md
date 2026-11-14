# Triage Labels

The skills speak of five triage roles. A role is written as a string in the Ticket's `status` front matter key, from the Tracker contract's [Status vocabulary](../../.agents/skills/frontier-setup/references/tracker-contract.md#status-vocabulary):

| Role in mattpocock/skills | `status` written |
|---|---|
| `needs-triage` | `needs-triage` |
| `needs-info` | `needs-human` |
| `ready-for-agent` | `ready-for-agent` |
| `ready-for-human` | `ready-for-human` |
| `wontfix` | `wont-do` |

When a skill mentions a role, write the string from this table.
