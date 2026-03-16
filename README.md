# Encrypted Room sample

- Uses Room with SQLCipher
- Inserts synthetic rows in batches of 10k
- Executes queries through Room

## Behavior

- On every app open, the app reads the full database and recomputes checksums for all rows. (If that takes to long, delete the app data or uninstall)
- Each tap on **Add 10,000 synthetic rows** inserts exactly 10k new rows.

## How to trigger/solve the intermittend `(26) file is not a database`-Bug:

- Set `val queryExecutor = Executors.newFixedThreadPool(2)` on line 54 in `data/AppDatabase.kt`.
- Build and launch the app.
- Tap multiple times on **Add 10,000 synthetic rows**. The error should show after ~4 taps (eg. ~40k rows added).
- After the bug surfaced, set line 54 in `data/AppDatabase.kt` to `val queryExecutor = Executors.newFixedThreadPool(1)` or `val queryExecutor = Executors.newSingleThreadExecutor()`. Rebuild and launch the app.
- The bug should now be gone.


## Notes

- The passphrase is currently stored as a demo `BuildConfig` constant. 
