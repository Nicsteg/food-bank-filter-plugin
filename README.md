# Food Bank Filter

A RuneLite plugin that helps players quickly find food items in the bank and sort them by value or healing.

## Overview
Food Bank Filter adds a side-panel workflow to RuneLite for bank food management.

## Features
- toggle a food-only bank filter
- compact matching food into visible bank slots
- sort food by highest GE price
- sort food by highest healing value

## Tech stack
- Java 11
- Gradle
- RuneLite Plugin API

## Build
```bash
./gradlew build
```

On Windows PowerShell:
```powershell
.\gradlew.bat build
```

## Run locally
You can run the development client by launching:
- `com.foodbankfilter.FoodBankFilterPluginTest`

or by using the Gradle/dev workflow configured for the project.

## Project files
- `src/main/java/` - plugin source code
- `src/main/resources/runelite-plugin.properties` - plugin metadata
- `PLUGIN_HUB_SUBMISSION.md` - plugin hub submission notes
- `plugin-hub-entry/food-bank-filter` - plugin hub entry metadata

## Plugin metadata
- Display name: `Food Bank Filter`
- Tags: `bank, food, filter`

## Use cases
- quickly isolate edible bank items
- compare high-heal foods
- compare food by Grand Exchange value
- make restocking easier for PvM / skilling sessions

## Support
Use GitHub Issues for:
- bug reports
- feature requests
