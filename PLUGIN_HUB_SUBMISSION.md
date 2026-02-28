# Plugin Hub submission package (Food Bank Filter)

Use this as your exact submission checklist.

## 1) Push plugin source to GitHub

Repo should contain at minimum:
- `build.gradle`
- `settings.gradle`
- `src/main/java/...`
- `src/main/resources/runelite-plugin.properties`
- `README.md`
- `LICENSE`

> If you don't have a license yet, add MIT (or your preferred OSS license).

## 2) Confirm plugin metadata

`src/main/resources/runelite-plugin.properties` should include:

- `displayName`
- `author`
- `description`
- `tags`
- `plugins` (fully-qualified plugin class)

Current file is already present in this project.

## 3) Create Plugin Hub entry file (in plugin-hub repo PR)

In the **RuneLite plugin-hub** repository, create a new file at:

`plugins/food-bank-filter`

with content:

```properties
repository=https://github.com/<YOUR_GITHUB_USER>/<YOUR_REPO>
commit=<FULL_40_CHAR_COMMIT_SHA>
```

Example:

```properties
repository=https://github.com/nicks/food-bank-filter-plugin
commit=0123456789abcdef0123456789abcdef01234567
```

## 4) Open PR to plugin-hub

PR title suggestion:

`Add Food Bank Filter plugin`

PR body should include:
- what plugin does
- screenshots/GIF
- support link (GitHub Issues)

## 5) Support link

Use your repo issues page as support page:

`https://github.com/<YOUR_GITHUB_USER>/<YOUR_REPO>/issues`

This project already includes:
- `.github/SUPPORT.md`
- issue templates for bugs/features

## 6) Review readiness checklist

- [ ] No automation/botting behavior
- [ ] No account-risking behavior
- [ ] Plugin does not crash with bank closed/opened repeatedly
- [ ] Filter toggle reliably restores original bank layout
- [ ] README has usage + support info
- [ ] Repository is public
