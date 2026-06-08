# Commit Message Convention

All commit messages in this project must strictly adhere to the following conventions.

## AI / Assistant Persona & Instructions
When generating or writing a commit message:
- You are an experienced software engineer and efficient technical communicator.
- Summarize the given diffs into a concise commit message.
- Focus on specific changes.

## Core Rules
1. **Prepend exactly ONE appropriate Gitmoji at the very beginning of the commit message.**
2. Write a concise and clear subject line that summarizes the changes (Diffs).
3. Separate the subject line from the body with a blank line.
4. Use a bulleted list (`-`) in the body to describe the specific changes in detail.
5. Focus on specific changes.
6. Do NOT output names, e-mail addresses, or any other personally identifiable information if they are not explicitly in the diffs.
7. Do NOT output bug IDs or any other unique identifiers if they are not explicitly in the diffs.

---

## Gitmoji Reference Guide

| Emoji | Description |
| :---: | :--- |
| 🎨 | Improve structure / format of the code |
| ⚡️ | Improve performance |
| 🔥 | Remove code or files |
| 🐛 | Fix a bug |
| 🚑 | Critical hotfix |
| ✨ | Introduce new features |
| 📝 | Add or update documentation |
| 🚀 | Deploy stuff |
| 💄 | Add or update the UI and style files |
| 🎉 | Begin a project |
| ✅ | Add, update, or pass tests |
| 🔒 | Fix security issues |
| 🔐 | Add or update secrets |
| 🔖 | Release / Version tags |
| 🚨 | Fix compiler / linter warnings |
| 🚧 | Work in progress |
| 💚 | Fix CI Build |
| 📌 | Pin dependencies to specific versions |
| 👷 | Add or update CI build system |
| 📈 | Add or update analytics or track code |
| ♻️ | Refactor code |
| ➕ | Add a dependency |
| ➖ | Remove a dependency |
| 🔧 | Add or update configuration files |
| 🔨 | Add or update development scripts |
| 🌐 | Internationalization and localization |
| ✏️ | Fix typos |
| 💩 | Write bad code that needs to be improved |
| ⏪ | Revert changes |
| 🔀 | Merge branches |
| 📦 | Add or update compiled files or packages |
| 👽 | Update code due to external API changes |
| 🚚 | Move or rename resources (e.g.: files, paths, routes) |
| 📄 | Add or update license |
| 💥 | Introduce breaking changes |
| 💡 | Add or update comments in source code |
| 🍻 | Write code drunkenly |
| 🗃️ | Perform database related changes |
| 🔊 | Add or update logs |
| 🔇 | Reduce or remove logs |
| 👥 | Add or update contributor(s) |
| 🚸 | Improve user experience / accessibility |
| 🏗️ | Make architectural changes |
| 📱 | Work on responsive design |
| 🤡 | Mock things |
| 🥚 | Add or update an easter egg |
| 🙈 | Add or update a .gitignore file |
| 📸 | Add or update snapshots |
| ⚗️ | Perform experiments |
| 🔍 | Improve SEO |
| 🏷️ | Add or update types |
| 🌱 | Add or update seed files |
| 🥅 | Add, update, or remove feature flags |
| ⚡️ | Catch errors |
| 🎨 | Add or update animations and transitions |
| 🗑️ | Deprecate code that needs to be cleaned up |
| 🛂 | Work on code related to authorization, roles and permissions |
| 🩹 | Simple fix for a non-critical issue |
| 🧐 | Data exploration / inspection |
| ⚰️ | Remove dead code |
| 🧪 | Add a failing test |
| 👔 | Add or update business logic |
| 🩺 | Add or update healthcheck |
| 🧱 | Infrastructure related changes |
| 🧑‍💻 | Improve developer experience |
| 🪙 | Add sponsorships or money related infrastructure |
| 🧵 | Add or update code related to multithreading or concurrency |
| ✅ | Add or update code related to validation |

---

## Example Commit Messages

### Example 1:
```
🛂 Disable subscription for guest users

- Update `SubscriptionRoute` and `SubscriptionScreen` to accept user data and identify guest sessions.
- Disable the `SubscribeButton` when the app is in guest mode to prevent unauthorized subscription attempts.
- Update `SubscriptionNavigation` to pass the current `UserData` to the subscription flow.
```

### Example 2:
```
✨ Guest mode UI and empty state improvements

- Add guest-specific localized strings for empty trip states and sign-in prompts.
- Update `NoTripCard` to display a simplified message when in guest mode.
- Add a sign-in call-to-action and button to the `TripsScreen` for guest users.
- Refine layout padding and spacing in the trip list empty state.
```
