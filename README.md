# Prisma Win (Android, Kotlin)

A mini-game for training sports skills:
- Reaction (tap after the signal)
- Accuracy (hit targets in 30 seconds)
- Memory (repeat the sequence)

## Stack
- Kotlin
- Jetpack Compose
- Navigation Compose
- Room
- DataStore
- MVVM (ViewModel + StateFlow)

## Implemented
- 3 game modes
- Saves results to a local database
- Stats screen (best and recent results)
- 7-day progress charts (reaction/accuracy/memory)
- Achievements
- Daily challenges with current-day progress
- Push reminders for daily challenges (via WorkManager, 20:00)
- Weekly progress ranking (compared with last week)
- First-launch onboarding
- Settings (sound/vibration/reminders/onboarding reset)

## Build
```bash
./gradlew test
./gradlew assembleDebug
```

APK after build:
`app/build/outputs/apk/debug/app-debug.apk`
