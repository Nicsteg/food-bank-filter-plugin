# Food Bank Filter (RuneLite plugin)

This plugin adds a **Food** toggle in the bank title bar. When enabled, it hides non-food bank item widgets and shows only items with an `Eat` inventory action.

## Project path

- `C:\Users\nicks\food-bank-filter-plugin`

## Files

- `build.gradle`
- `settings.gradle`
- `src/main/java/com/foodbankfilter/FoodBankFilterPlugin.java`
- `src/main/java/com/foodbankfilter/FoodBankFilterConfig.java`
- `src/main/resources/runelite-plugin.properties`

## Notes

- Food detection uses item composition inventory actions and matches `Eat`.
- This is a UI filter (does not move bank items).

## Build

```bash
./gradlew build
```

## Run locally for testing (recommended)

This project includes a launcher class that starts RuneLite with this plugin loaded:

```bash
./gradlew run
```

On Windows PowerShell:

```powershell
.\gradlew run
```
