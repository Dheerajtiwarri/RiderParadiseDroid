# RiderParadise Backend — Design & Specification

**Stack:** Next.js (App Router, Route Handlers) + Firebase Admin SDK
**Firebase project:** `riderparadise-3aae5`
**Status:** Design spec. Backend not yet built. Android app currently calls Firestore directly (client SDK).

---

## 1. Goal & Principles

1. **Backend owns Firestore.** All create/read/update/delete on Firestore goes through the Next.js API. Clients hold **zero** Firestore client-SDK calls — except one carve-out (below).
2. **Single exception — live location.** During an **active (LIVE) ride**, group members read each other's live location **directly from Firestore** via a real-time listener. This is the only direct Firestore access left in any client.
3. **One backend, four clients.** Web app, Android, iOS, Admin panel all talk to the same HTTP API.
4. **Service-account writes.** Backend uses Firebase **Admin SDK** (service account) -> bypasses security rules -> rules can be locked to deny all client writes.

```
Web app / Android / iOS / Admin panel
        |  HTTPS/JSON (REST)
        v
  Next.js Backend API (Route Handlers)  <- Firebase Admin SDK (service account)
        v
     Firestore
        |  (read-only listener, LIVE ride only)
        v
  Clients: live location of group members during ride
```

---

## 2. Current State (what backend replaces)

App today uses Firestore **client SDK** directly from Android repositories. Auth = custom notification-OTP (no Firebase Auth); session = local user id in DataStore. Security rules wide open (`allow read, write: if true`) because `request.auth` is null.

**Every direct Firestore call to be removed from clients** (replaced by API call):

| Domain | Operation | Current location (Android) |
|--------|-----------|----------------------------|
| Auth | create/verify user doc on OTP | `OtpRepositoryImpl.kt:75-87` |
| User | observe current user | `UserRepositoryImpl.kt:25-29` |
| User | save/update profile | `UserRepositoryImpl.kt:35-76` |
| Group | list all groups (+ membership) | `GroupRepositoryImpl.kt:29-45` |
| Group | list joined groups | `GroupRepositoryImpl.kt:47-48` |
| Group | seed/refresh groups | `GroupRepositoryImpl.kt:50-57` |
| Group | join group | `GroupRepositoryImpl.kt:59-75` |
| Group | request to join (private) | `GroupRepositoryImpl.kt:62-75` |
| Ride | list rides | `RideRepositoryImpl.kt:26-27` |
| Ride | get single ride | `RideRepositoryImpl.kt:29-30` |
| Ride | create ride | `RideRepositoryImpl.kt:72-100` |
| Ride | RSVP | `RideRepositoryImpl.kt:37-53` |
| Ride | start ride | `RideRepositoryImpl.kt:55-63` |
| Ride | complete ride | `RideRepositoryImpl.kt:65-70` |
| Ride | roster (currently simulated) | `RideRepositoryImpl.kt:102-108` |
| Ride | recap (currently simulated) | `RideRepositoryImpl.kt:110-122` |
| Chat | observe messages | `RideRepositoryImpl.kt:124-128` |
| Chat | send message | `RideRepositoryImpl.kt:130-145` |
| Chat | seed chat on ride start | `RideRepositoryImpl.kt:147-176` |
| Feedback | submit | `FeedbackRepositoryImpl.kt:17-34` |

**Kept as direct Firestore (the one exception):**
- Live location reads of group members during a LIVE ride -> `rides/{rideId}/locations/{userId}` (new collection, see section 5).

---

## 3. Firestore Data Model (authoritative)

Backend is the only writer to all of these (except `locations` writes, see section 5).

### `users/{uid}`
| Field | Type | Notes |
|-------|------|-------|
| `phone` | string | |
| `displayName` | string | |
| `bikeType` | string | ADVENTURE / SPORTS / MTB / ROAD / CRUISER |
| `avatarUrl` | string? | |
| `colorHex` | string | identity color e.g. `#3B9DFF` |
| `shareLocation` | bool | gates live location publishing |
| `allowContacts` | bool | |
| `createdAt` | number (ms) | |

Subcollection `users/{uid}/memberships/{groupId}`: `status` (NONE/REQUESTED/JOINED), `role` (`member`), `joinedAt` (number).

### `groups/{groupId}`
`name`, `initials`, `category` (ADVENTURE/SPORTS/MTB/ROAD/GRAVEL/EBIKE/CRUISER), `privacy` (PUBLIC/PRIVATE), `riderCount` (number), `distanceKm` (number?), `trending` (bool), `gradientStartHex`, `gradientEndHex`.

Subcollection `groups/{groupId}/members/{userId}`: `status`, `role`, `joinedAt`.

### `rides/{rideId}`
`groupId`, `groupName`, `title`, `whenLabel`, `difficulty`, `distanceKm` (number), `climbM` (number), `durationLabel`, `leaderName`, `startLocation`, `status` (PLANNED/LOBBY/LIVE/COMPLETED), `goingCount`, `maybeCount`, `myRsvp` (NONE/GOING/MAYBE/CANT).

> Note: `myRsvp` is per-user state. Backend should move this to `rides/{rideId}/rsvps/{userId}` and compute per-caller, not store one global value. (Migration item.)

Subcollection `rides/{rideId}/messages/{messageId}`: `senderName`, `senderInitials`, `colorHex`, `text`, `mine` (bool — compute per-caller, do not persist), `kind` (NORMAL/MACRO/SYSTEM), `createdAt`.

### `rides/{rideId}/locations/{userId}` — NEW (live location)
| Field | Type | Notes |
|-------|------|-------|
| `lat` | number | |
| `lng` | number | |
| `speedKmh` | number | |
| `bearingDeg` | number | |
| `updatedAt` | number (ms) | |

Written by client (rate-limited) only while ride LIVE and user `shareLocation=true`. Read by group members via direct Firestore listener. (See section 5.)

### `feedback/{docId}`
`userId` (or `anon`), `type` (SUGGESTION/PROBLEM), `area`, `body`, `appVersion`, `createdAt`.

---

## 4. Authentication & Sessions

Current app: custom notification-OTP, deterministic `userId = "u_" + sha256(phone,"rp_phone").take(20)`, salted SHA-256 OTP storage, 5-min expiry, 5 attempts. No Firebase Auth.

**Backend design — move OTP server-side and issue real tokens:**

1. `POST /api/auth/request-otp` `{ phone }` -> backend generates 6-digit code, stores salted hash + salt + expiry in `otps/{phone}` (server-only collection), triggers delivery.
   - Delivery: keep notification-OTP for dev; production should use SMS (Firebase Auth phone, or provider). Notification path stays as fallback.
2. `POST /api/auth/verify-otp` `{ phone, code }` -> backend validates, derives `userId`, **mints a Firebase custom token / session JWT**, ensures `users/{uid}` doc exists.
   - Returns `{ token, user }`. Token = signed JWT (HMAC or Firebase custom token) with `uid`, expiry, role claim.
3. Clients send `Authorization: Bearer <token>` on every request. Backend middleware verifies, attaches `uid` + role.
4. **Roles** via claim: `user`, `admin`. Admin panel requires `admin`.

This lets us issue a **Firebase ID token** so the one client carve-out (live location listener) can authenticate to Firestore and rules can be tightened (see section 6).

---

## 5. Live Location — the one direct-Firestore feature

**Why direct:** real-time, high-frequency (~every 5s), many-to-many fan-out among group members. Routing through HTTP backend adds latency + cost. Firestore's snapshot listener is the right tool.

**Rules:**
- Only available when ride `status == LIVE`.
- Only members of the ride's group (`status == JOINED`) can read.
- Each user writes **only their own** `locations/{userId}` doc, and only if `shareLocation == true`.
- On COMPLETE/COMPLETED, backend deletes the `locations` subcollection (or stops accepting writes; clients stop listening).

**Client flow:**
1. Client starts ride via `POST /api/rides/{id}/start` (backend sets status LIVE, returns ok).
2. Client opens **direct Firestore listener** on `rides/{rideId}/locations` (authenticated with Firebase ID token).
3. Client writes own location doc directly to Firestore on interval (authenticated).
4. Client stops listener + write loop when ride leaves LIVE.

Everything else about the ride (start, complete, roster metadata, RSVP, chat) goes through the backend API.

---

## 6. Firestore Security Rules (target)

Lock writes; allow only authenticated, scoped live-location access. Backend (Admin SDK) bypasses rules entirely.

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Default: clients cannot read or write. Backend Admin SDK bypasses these.
    match /{document=**} {
      allow read, write: if false;
    }

    // Live location — the only client-accessible path.
    match /rides/{rideId}/locations/{userId} {
      allow read: if isJoinedGroupMember(rideId);
      allow write: if request.auth != null
                   && request.auth.uid == userId
                   && rideIsLive(rideId)
                   && userSharesLocation();
    }

    function rideIsLive(rideId) {
      return get(/databases/$(database)/documents/rides/$(rideId)).data.status == 'LIVE';
    }
    function userSharesLocation() {
      return get(/databases/$(database)/documents/users/$(request.auth.uid)).data.shareLocation == true;
    }
    function isJoinedGroupMember(rideId) {
      let ride = get(/databases/$(database)/documents/rides/$(rideId)).data;
      return request.auth != null
        && get(/databases/$(database)/documents/groups/$(ride.groupId)/members/$(request.auth.uid)).data.status == 'JOINED';
    }
  }
}
```

> Requires real auth tokens (Firebase ID token from custom token in section 4). Until tokens ship, keep current open rules but only in dev.

`firestore.indexes.json` currently empty. Add composite indexes as backend queries appear (e.g. `rides` filtered by `groupId` + ordered by `createdAt`; messages by `createdAt`).

---

## 7. API Surface (REST, JSON)

Base: `/api`. All routes require `Authorization: Bearer <token>` unless noted. Standard envelope:
`{ "data": <payload>, "error": null }` or `{ "data": null, "error": { "code", "message" } }`.

### Auth
| Method | Path | Body | Returns | Notes |
|--------|------|------|---------|-------|
| POST | `/api/auth/request-otp` | `{ phone }` | `{ ok }` | public |
| POST | `/api/auth/verify-otp` | `{ phone, code }` | `{ token, user }` | public; mints token |
| POST | `/api/auth/refresh` | `{ token }` | `{ token }` | rotate session |
| POST | `/api/auth/logout` | — | `{ ok }` | invalidate (optional) |

### User
| Method | Path | Returns / Body |
|--------|------|----------------|
| GET | `/api/me` | current `user` |
| PATCH | `/api/me` | body `{ displayName?, bikeType?, shareLocation?, allowContacts?, avatarUrl? }` |

### Groups
| Method | Path | Notes |
|--------|------|-------|
| GET | `/api/groups` | all groups + caller's membership status |
| GET | `/api/groups/joined` | groups caller has JOINED |
| GET | `/api/groups/{id}` | single group + members |
| POST | `/api/groups/{id}/join` | join (public) — sets both membership docs |
| POST | `/api/groups/{id}/request` | request to join (private) |
| POST | `/api/groups/{id}/leave` | leave group |

### Rides
| Method | Path | Notes |
|--------|------|-------|
| GET | `/api/rides` | rides (optional `?groupId=` / `?status=`) |
| GET | `/api/rides/{id}` | single ride |
| POST | `/api/rides` | create ride `{ groupId, title, difficulty, ... }` |
| POST | `/api/rides/{id}/rsvp` | `{ response: GOING/MAYBE/CANT }` -> per-user rsvp + counts |
| POST | `/api/rides/{id}/start` | leader only -> status LIVE, seed chat, open locations |
| POST | `/api/rides/{id}/complete` | status COMPLETED, delete locations, write recap |
| GET | `/api/rides/{id}/roster` | participants (real, from rsvps/members) |
| GET | `/api/rides/{id}/recap` | aggregated stats (computed/persisted, not simulated) |

### Chat
| Method | Path | Notes |
|--------|------|-------|
| GET | `/api/rides/{id}/messages` | `?after=<cursor>` poll, or use SSE below |
| POST | `/api/rides/{id}/messages` | `{ text, kind }` |
| GET | `/api/rides/{id}/messages/stream` | **SSE** real-time push (backend tails Firestore) |

> Chat real-time without exposing Firestore: backend opens an Admin-SDK listener and relays via **Server-Sent Events** (or WebSocket). Keeps the "clients don't touch Firestore" rule. Polling `?after=` is the simple fallback.

### Feedback
| Method | Path |
|--------|------|
| POST | `/api/feedback` `{ type, area, body, appVersion }` |

### Admin (role: `admin`)
| Method | Path | Notes |
|--------|------|-------|
| GET | `/api/admin/users` | list/search users |
| PATCH/DELETE | `/api/admin/users/{id}` | manage user |
| CRUD | `/api/admin/groups`, `/api/admin/rides` | full management |
| GET | `/api/admin/feedback` | review feedback |
| POST | `/api/admin/seed` | run group/ride seeding (replaces client SeedManager) |
| GET | `/api/admin/metrics` | dashboard stats |

---

## 8. Real-time strategy (no client Firestore, except location)

| Data | Real-time need | Mechanism |
|------|----------------|-----------|
| Live location | High freq, P2P fan-out | **Direct Firestore listener** (the exception) |
| Chat | Medium | Backend SSE relay (Admin listener -> SSE) or poll `?after=` |
| Ride status / roster | Low | Poll on screen focus, or SSE |
| Groups / rides lists | Low | Fetch on load + pull-to-refresh |

---

## 9. Next.js Project Layout

```
backend/
  app/
    api/
      auth/{request-otp,verify-otp,refresh,logout}/route.ts
      me/route.ts
      groups/{route.ts, joined/route.ts, [id]/{route.ts, join, request, leave}/route.ts}
      rides/{route.ts, [id]/{route.ts, rsvp, start, complete, roster, recap, messages}/route.ts}
      feedback/route.ts
      admin/.../route.ts
  lib/
    firebaseAdmin.ts     # Admin SDK init (service account from env)
    auth.ts              # token mint/verify, role guard middleware
    otp.ts               # generate/verify, salted SHA-256 (port from Hashing.kt)
    repos/               # userRepo, groupRepo, rideRepo, chatRepo, feedbackRepo
    validation/          # zod schemas per endpoint
  middleware.ts          # Bearer auth + CORS for web/mobile/admin
  .env.local             # FIREBASE_* service account, JWT_SECRET
```

**Key libs:** `firebase-admin`, `zod` (validation), `jose` or Firebase custom-token for JWTs.

**Env:** service account JSON (or `GOOGLE_APPLICATION_CREDENTIALS`), `JWT_SECRET`, `OTP_PEPPER`, allowed CORS origins.

---

## 10. Migration Plan

1. **Stand up backend** with Admin SDK + auth endpoints; port OTP logic from `Hashing.kt`/`OtpRepositoryImpl.kt`.
2. **Build read endpoints** (`/me`, `/groups`, `/rides`, messages); point one client screen at them to validate.
3. **Build write endpoints** (join, rsvp, create ride, start/complete, chat, feedback).
4. **Move seeding** to `/api/admin/seed`; remove client `SeedManager` (`RidersParadiseApp.kt`, `data/seed/SeedManager.kt`).
5. **Swap Android repositories** to HTTP (Retrofit/Ktor) — delete direct Firestore calls listed in section 2; keep only live-location listener.
6. **Add live location**: create `locations` subcollection, client write loop + listener, backend start/complete lifecycle.
7. **Tighten rules** (section 6) once Firebase ID tokens issued; flip default to `if false`.
8. **Build web + iOS + admin** against the same API.

---

## 11. Open Decisions

- **OTP delivery in prod:** notification-OTP (current) vs real SMS / Firebase phone auth.
- **Chat transport:** SSE relay vs polling vs allow chat as a second direct-Firestore exception.
- **Token type:** Firebase custom token (needed for live-location auth to Firestore) vs plain backend JWT (then live-location needs custom token anyway -> prefer Firebase custom token).
- **`myRsvp` / `mine`:** stop storing global per-doc; compute per-caller (move RSVP to subcollection).
- **Hosting:** Vercel vs Firebase App Hosting vs Cloud Run.
