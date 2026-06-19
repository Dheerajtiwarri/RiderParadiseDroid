# RidersParadise — SDLC & Implementation Plan

## 1. Context

`app/docs/RidersParadise.dc.html` is a 12-screen high-fidelity design for **RidersParadise**, a
group-ride companion for cyclists/motorcyclists: plan a route, roll out, and see every rider live on
one map. The Android project is a fresh Jetpack Compose scaffold (`com.droid.riderparadise`, minSdk 24,
compileSdk 36, Kotlin 2.0.21, Compose BOM). Firebase is already provisioned and wired to the package:
`google-services.json` → project `riderparadise-3aae5`.

**Goal:** turn the design into a working Android app with **Firestore** (remote DB), **Room** (local/
offline cache), **MVI** architecture, **Jetpack Compose** UI, and **OTP delivered via a local
notification** (no real SMS — "as of now"). This document defines the SDLC plan; code follows after sign-off.

**Decisions locked (from user):**
- OTP = locally-generated 6-digit code posted as a **system notification**, verified in-app. No Firebase Phone Auth / SMS.
- Map SDK choice (Google Maps vs styled placeholder) is **deferred** — decided after this plan. See §11.
- Build order: **plan/document first**, then implement in phases.

---

## 2. Objectives & Scope

### In scope
- Full app skeleton: DI, navigation, theming from the design, MVI plumbing, Firestore + Room repos.
- All 12 screens, grouped into 4 delivery phases (§9). Onboarding + auth fully functional first.
- OTP-via-notification auth, profile setup, discover/join groups, plan ride + RSVP, pre-ride lobby,
  live ride screen, in-ride chat, post-ride recap, feedback.

### Out of scope (POC)
- Real SMS / production auth, real GPS multi-device sync of other riders (simulated for live map/lobby).
- Payments, push to other devices (FCM), social graph beyond group membership.
- Backend Cloud Functions; all logic is client-side against Firestore.

---

## 3. Requirements

### Functional
| # | Requirement |
|---|---|
| F1 | User signs in with phone number; app generates OTP, posts it as a notification, user enters 6 digits to verify. |
| F2 | First-time user sets up profile: display name, bike type, avatar, location/contacts consent. |
| F3 | User discovers groups (search, category chips, popular), joins public / requests private groups. |
| F4 | "Groups" tab lists joined groups; "Riders" and "Suggestions" tabs (Phase 3+). |
| F5 | Group leader plans a ride (title, when, difficulty, route, stats) and publishes to the group. |
| F6 | Members RSVP Going / Maybe / Can't; pre-ride lobby shows countdown + check-in status. |
| F7 | Leader starts the ride → live ride screen (map + rider pucks) + in-ride chat with quick macros. |
| F8 | After finish, post-ride recap with combined stats; feedback screen to suggest/report. |
| F9 | App works offline-first: Room is the UI source of truth, Firestore syncs in the background. |

### Non-functional
- **Architecture:** MVI (unidirectional: Intent → ViewModel reduce → State → UI; one-shot Effects).
- **Offline-first:** Room cache mirrors Firestore; reads observe Room `Flow`, writes go remote then cache.
- **Min SDK 24**, target 36. Notification runtime permission handled on Android 13+ (POST_NOTIFICATIONS).
- Theme matches design tokens (green brand, light + dark "night ride" mode).
- Testable: ViewModel reducers unit-tested; repos behind interfaces.

---

## 4. Tech Stack & Dependencies

Additions to `gradle/libs.versions.toml` + `app/build.gradle.kts` + root `build.gradle.kts`:

- **Firebase:** `google-services` Gradle plugin + classpath; `firebase-bom`, `firebase-firestore`,
  `firebase-storage` (avatars), `firebase-analytics` (optional).
- **Room:** `room-runtime`, `room-ktx`, `room-compiler` (via **KSP** plugin `com.google.devtools.ksp`).
- **DI:** **Hilt** — `hilt-android`, `hilt-compiler`, `hilt-navigation-compose` (+ Hilt Gradle plugin).
- **Navigation:** `androidx.navigation:navigation-compose` (type-safe routes).
- **Async:** kotlinx-coroutines (core + play-services for Firestore `await()`).
- **Images:** `coil-compose` for avatars / image-slot.
- **Local prefs:** `androidx.datastore:datastore-preferences` (session + onboarding flags).
- **Lifecycle:** `lifecycle-viewmodel-compose`, `lifecycle-runtime-compose` (collectAsStateWithLifecycle).
- **Permissions:** Activity Result API (or `accompanist-permissions`) for notifications/location/contacts.

> Note: `google-services.json` has an empty `oauth_client`; fine for Firestore + the notification-OTP
> approach (no Google Sign-In needed). Firestore security rules + a Firestore DB must exist in the
> console (verify during Phase 0).

---

## 5. Architecture — MVI

Unidirectional data flow, one `ViewModel` per screen/feature:

```
        Intent (user action / sealed class)
              │
              ▼
   ┌──────────────────────┐      observes Flow
   │     ViewModel         │◀──────────────┐
   │  reduce(intent,state) │               │
   │  → new State          │        ┌──────────────┐
   │  → emit Effect (1shot)│        │  Repository   │ (interface)
   └──────────────────────┘        │  Room + FS    │
        │            │              └──────────────┘
        ▼            ▼                 │        │
  StateFlow<State>  Channel<Effect>    ▼        ▼
        │            │            Room DAO   Firestore
        ▼            ▼          (source of   (remote sync)
   Compose UI   side-effects     truth)
 (collectAsState) (nav, toast,
                   notification)
```

**Core contracts** (`core/mvi`):
```kotlin
interface UiState
interface UiIntent
interface UiEffect
abstract class MviViewModel<S: UiState, I: UiIntent, E: UiEffect>(initial: S) {
    val state: StateFlow<S>            // single source of UI truth
    val effects: Flow<E>              // one-shot: navigation, notifications, snackbars
    fun onIntent(intent: I)
    protected fun setState(reducer: S.() -> S)
    protected suspend fun emitEffect(effect: E)
}
```
Each feature gets `XxxState`, `XxxIntent`, `XxxEffect`, `XxxViewModel`, `XxxScreen` (stateless
composable taking `state` + `onIntent`) and a stateful wrapper that wires the ViewModel.

### Layers / package structure (single module `:app`)
```
com.droid.riderparadise
├── RidersParadiseApp.kt           (@HiltAndroidApp)
├── MainActivity.kt                (NavHost host)
├── core/
│   ├── mvi/                       MviViewModel, UiState/Intent/Effect
│   ├── notification/              OtpNotifier, NotificationChannels
│   ├── result/                    Resource<T> / AppError
│   └── di/                        AppModule, FirebaseModule, DatabaseModule
├── data/
│   ├── local/                     AppDatabase, DAOs, Room entities, Converters
│   ├── remote/                    Firestore data sources, DTOs, mappers
│   ├── repository/                *RepositoryImpl (Room+FS), OtpRepository
│   └── datastore/                 SessionStore (current user, onboarding flag)
├── domain/
│   ├── model/                     Rider, Group, Ride, RSVP, ChatMessage, Recap, Feedback
│   └── repository/                repository interfaces
├── navigation/                    Routes, RidersNavHost
└── feature/
    ├── auth/        (OTP sign-in)
    ├── onboarding/  (profile, join group)
    ├── home/        (discover/popular)
    ├── groups/      riders/ suggestions/
    ├── ride/        (plan, lobby, live, chat, recap)
    └── feedback/
```

---

## 6. Data Model

### Firestore collections (remote)
- `users/{userId}` — phone, displayName, bikeType, avatarUrl, colorHex, perms{location,contacts}, createdAt.
- `groups/{groupId}` — name, initials, category, privacy(public/private), riderCount, gradientColors, trending, location.
- `groups/{groupId}/members/{userId}` — role(member|leader), status(joined|requested), joinedAt.
- `rides/{rideId}` — groupId, title, scheduledAt, difficulty, distanceKm, climbM, durationMin, routePoints[], startLocation, leaderId, status(planned|lobby|live|completed).
- `rides/{rideId}/rsvps/{userId}` — response(going|maybe|cant).
- `rides/{rideId}/checkins/{userId}` — status(here|onway|not), distanceKm.
- `rides/{rideId}/locations/{userId}` — lat, lng, heading, updatedAt, fresh (live pucks; simulated in POC).
- `rides/{rideId}/messages/{msgId}` — userId, text, kind(normal|broadcast|macro), sentAt.
- `rides/{rideId}/recap` — distanceKm, movingTimeMin, avgSpeed, maxSpeed, climbM, kcal, tracks[].
- `feedback/{id}` — userId, type(suggest|report), area, body, appVersion, screenshotUrl, createdAt.

### Room entities (local cache — mirror the above)
`UserEntity`, `GroupEntity`, `MembershipEntity`, `RideEntity`, `RsvpEntity`, `CheckInEntity`,
`ChatMessageEntity`, `RecapEntity`, plus **`OtpEntity`** (local-only, never synced):
```
OtpEntity(phone PK, codeHash, salt, expiresAt, attempts, createdAt)
```
DAOs expose `Flow<...>` for reactive UI. A lightweight mapper layer converts DTO ↔ domain ↔ entity.

### Repository sync strategy (offline-first)
- **Read:** UI collects Room `Flow`; repo opens a Firestore snapshot listener that upserts into Room.
- **Write:** repo writes to Firestore (`await()`), then upserts Room on success; surfaces `Resource.Error` otherwise.

---

## 7. OTP-via-Notification Design

```
[Phone screen] enter phone → Intent.SubmitPhone
   → OtpRepository.requestOtp(phone):
        code = random 6 digits (SecureRandom)
        store OtpEntity(phone, hash(code+salt), expiresAt=+5min, attempts=0) in Room
        emit Effect.PostOtpNotification(code)         ← ViewModel
   → OtpNotifier.post(code): system notification "RidersParadise code: 481•••"
[OTP screen] enter 6 digits → Intent.VerifyOtp(code)
   → OtpRepository.verify(phone, code): compare hash, check expiry/attempts
        success → ensure users/{uid} in Firestore (create if new), save session in DataStore
                → Effect.NavigateTo(onboarding if new else home)
        fail → setState(error), increment attempts
   Resend timer (0:24) re-issues a new code + notification.
```
- Channel `otp` created at app start; **POST_NOTIFICATIONS** requested on Android 13+ before first send.
- POC identity: `userId` = stable hash of phone (no real auth backend). Documented as POC-only.
- Codes hashed at rest in Room (no plaintext OTP stored). 5-min expiry, max 5 attempts.

---

## 8. Theming

Extract design tokens into `ui/theme`:
- Brand green `#16893B` / deep `#0B5824` / accent mint `#36E0A1`; light bg `#EEF5EE`/`#DBE9D6`;
  ink `#14241A`; muted `#6B8473`. Dark "night ride" surface `#0C1411`/`#152A1D` for the live map screen.
- Plus Jakarta Sans (body/display) + JetBrains Mono (mono labels) via downloadable fonts or bundled.
- Rounded shapes (14–48dp radii), soft shadows, pill chips, gradient hero bands — codified as reusable
  composables: `RpButton`, `RpChip`, `RpCard`, `PhoneScaffold`, `GradientHeader`, `OtpInput`, `StepProgress`.

---

## 9. Phased Delivery (SDLC iterations)

**Phase 0 — Foundation** (no UI features)
- Add deps (Firebase, Room+KSP, Hilt, Nav, DataStore, Coil). Apply google-services plugin.
- `RidersParadiseApp`, Hilt modules, `AppDatabase`, MVI core, theme tokens, NavHost, shared composables.
- Verify Firestore reachable + notification channel posts a test code.

**Phase 1 — Onboarding & Auth** (screens 1–3) ← first working slice
- OTP sign-in (notification), profile setup, join-first-group. Full Firestore+Room+MVI round-trip.

**Phase 2 — Discover & Groups** (screens A, B, C, D)
- Home/discover, joined groups, riders-from-contacts, suggestions. Search + join/request flows.

**Phase 3 — Plan & Lobby** (screens 3, 4)
- Plan-a-ride + RSVP, pre-ride lobby (countdown, check-ins). Map = per §11 decision.

**Phase 4 — Live Ride & After** (screens 5, 6, 7, F, ★)
- Live ride map (simulated pucks), in-ride chat + macros, post-ride recap, feedback, rider color identity.

Each phase ends with: build green, ViewModel unit tests pass, manual smoke on emulator.

---

## 10. Testing Strategy
- **Unit:** ViewModel reducers (Intent→State), OTP hash/expiry/attempt logic, mappers. JUnit + Turbine for Flows.
- **Repository:** fake DAO + fake Firestore data source; verify offline-first read/write paths.
- **UI:** Compose UI tests for OTP entry, profile validation, join button states (key screens only for POC).
- **Manual:** emulator smoke per phase; Firestore console inspection of written docs.

## 11. Open Decision — Map SDK (deferred)
| Option | Pros | Cons |
|---|---|---|
| **Styled placeholder** (Canvas/Compose) | Self-contained, no key/billing, matches design's stylized maps | Not a real map |
| **Google Maps Compose** | Real map + camera | Needs Maps SDK enabled + API key + billing; more setup |

Recommendation: **placeholder for Phases 3–4**, swap to Maps Compose later behind a `MapRenderer`
interface so the screen code doesn't change. Confirm before Phase 3.

## 12. Risks
- Firestore DB / security rules may not be initialized in console → Phase 0 verification gate.
- POC identity (phone-hash, no real auth) is insecure by design — must be flagged, not shipped.
- Live multi-rider tracking is **simulated** (no second device); documented as POC behavior.
- minSdk 24 + POST_NOTIFICATIONS (API 33+) branching for OTP delivery.

## 13. Verification (end-to-end)
1. `./gradlew :app:assembleDebug` builds clean after each phase.
2. `./gradlew :app:testDebugUnitTest` green (reducer + OTP tests).
3. Emulator: enter phone → OTP notification appears → enter code → profile → join group →
   confirm `users/*` and `groups/*/members/*` docs in Firestore console → kill network, relaunch,
   data still renders from Room (offline-first proof).

---

### First implementation step after approval
Phase 0: wire dependencies + Hilt + Room + theme + NavHost, then Phase 1 OTP-notification auth.
This SDLC doc will also be copied to `app/docs/SDLC.md` for the repo record.
