# Google Play Deployment

This repository has a manual GitHub Actions workflow for production uploads:

`Actions -> Google Play Production -> Run workflow`

Required repository secrets:

| Secret | Value |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | Base64 content of the Android upload keystore (`Untitled.jks`) |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore password |
| `ANDROID_KEY_ALIAS` | Upload key alias, currently `key0` |
| `ANDROID_KEY_PASSWORD` | Key password |
| `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON` | Full JSON key for the Google Play service account |

Create the keystore base64 value locally:

```bash
base64 -i Untitled.jks | pbcopy
```

Do not commit keystores, AAB files, APK files, or service account JSON files.

The Google Play workflow uploads:

- package: `com.prismwin.apps`
- track: `production`
- status: `draft`

Google Play allows API-created releases for draft apps only when the release status is `draft`.
After the first release is manually reviewed/submitted in Play Console and the app is no longer draft, this workflow can be switched back to `completed`.

When running the workflow, set `version_code` to a value higher than every previous Google Play upload.
