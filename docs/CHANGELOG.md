# Changelog

## 0.8.0

- Rebuilt the home screen around the actual PSP collection.
- Removed the permanent hero area from populated libraries and hid empty-state search/filter controls.
- Added a fixed, always-visible PSP import action.
- Replaced unbounded ScrollView row inflation with a recycled native ListView adapter.
- Added consistent premium dark/light color tokens and refined typography, spacing and card hierarchy.
- Improved Arabic RTL layout and accessibility descriptions for icon-only actions.
- Added pure-Java library filtering and URI-grant policy with regression tests.
- Added duplicate URI checks before persisted permission acquisition.
- Released a newly acquired persisted grant after a failed database insert when no library row references it.
- Added a non-destructive SQLite v3 index for the active library query.
- Kept unsupported platforms hidden and clarified that PSP playback uses the official PPSSPP app.
- Gated APK packaging and emulator smoke tests so they run only at the final build step.
