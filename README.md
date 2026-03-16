# Encrypted Room sample

Small Android sample app using:

- Jetpack Compose UI
- Room
- SQLCipher via `net.zetetic:android-database-sqlcipher:4.13.0`
- Gradle Kotlin DSL
- Version catalog (`gradle/libs.versions.toml`)

## Behavior

- On every app open, the app reads the full database and recomputes checksums for all rows.
- Each tap on **Add 10,000 synthetic rows** inserts exactly 10k new rows.
- The passphrase is currently stored as a demo `BuildConfig` constant. Replace it for any real use.
