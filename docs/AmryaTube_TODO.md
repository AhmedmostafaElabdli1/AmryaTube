# AmryaTube — TODO Checklist

Rule: tick a box ONLY after the commit is pushed to GitHub.
`[x]` = done and verified · `[ ]` = to do · `[~]` = moved to a later phase

---

## Phase 0 — Clean Foundation

### Backend
- [x] **Commit 1:** JwtFilter → SLF4J, no token values in logs + fix OAuth2FailureHandler println — *pushed 2026-07-12 (87c0e4a; token kept at DEBUG only, `resolveToken()` extracted)*
- [x] `GlobalRole role` field on User (`@Enumerated(STRING)`, `@Builder.Default`)
- [x] **Commit 2:** real role everywhere — register (`user.getRole()`), login (+ `findByEmail`), OAuth2SuccessHandler, CustomUserDetailsService — *pushed 2026-07-20 (47bef88; also fixed OAuth2SuccessHandler discarding the loaded user)*
- [x] **Commit 3:** `@Data` → `@Getter @Setter` on all 7 entities — *pushed 2026-07-12 (87c0e4a)*
- [ ] **Secrets → env vars** before `application.properties` is ever committed: `${DB_PASSWORD}`, `${GOOGLE_CLIENT_ID}`, `${GOOGLE_CLIENT_SECRET}`, `${JWT_SECRET}` + values in IntelliJ run config (Google secret currently sits in the staged local file — do NOT commit it as-is)
- [x] AuthService servlet-free; controller builds cookie + JSON (no "Token: ..." strings) — *pushed 2026-07-20 (47bef88). Design decision: service returns `ApiResponse<AuthResponse>` envelope (instead of the planned AuthResult record); controller builds cookie from body token. Convention: future services return ApiResponse the same way*
  - [x] Fix repo git identity — local git config now personal (verified on 47bef88)
- [~] `@Transactional` → moved to Phase 1 (decision 2026-07-20: current writes are single-save; adopt with ChannelService)
- [~] Register race 409 catch → moved to Phase 1 (same `DataIntegrityViolationException` handler as handle uniqueness)
- [x] Package-by-feature structure
- [x] Fold `Security/` into `auth/` — *pushed 2026-07-20 (47bef88): all 6 files in `auth/security/`, package lowercase everywhere*
- [ ] CORS for localhost:4200 (with credentials)
- [ ] `GET /api/users/me` endpoint

### Frontend
- [ ] Angular project setup (v17+ standalone; core/ features/ shared/)
- [ ] AuthService with signal state
- [ ] HTTP interceptor + 401 redirect
- [ ] authGuard + roleGuard
- [ ] Login page (reactive form + errors)
- [ ] Register page (confirm password + 409 handling)
- [ ] Google login button + redirect
- [ ] App shell (toolbar with auth state)

### Exit test
- [ ] Register + login from Angular works
- [ ] DB-flipped ADMIN → `ROLE_ADMIN` on jwt.io
- [ ] Zero token characters in logs

---

## Phase 1 — Channel Module

### Backend
- [x] Channel entity exists (review vs Epic 2 design)
- [ ] i18n messages (En + Ar): `MessageSource` + `messages_en/ar.properties` per module, resolved via `Accept-Language`; exception handlers + ApiResponse messages use message keys
- [ ] `@Transactional` on all write methods — ChannelService onward, retrofit AuthService/OAuth2SuccessHandler; **hard deadline: before Phase 2 upload state machine**
- [ ] `DataIntegrityViolationException` → 409 handler in GlobalExceptionHandler (covers register race + handle uniqueness + future unique constraints)
- [ ] ChannelService: create / update / delete / getByHandle
- [ ] Paginated list (`Page<ChannelSummary>`)
- [ ] DTOs + mapper (never expose entities)
- [ ] Handle validation (URL-safe, unique → 409)
- [ ] `assertOwner()` → 403 pattern
- [ ] Subscribe / unsubscribe endpoints (idempotent, unique pair)
- [ ] Subscriber count projection

### Frontend
- [ ] Angular ChannelService
- [ ] Channel list page (paginated)
- [ ] Channel detail `/c/:handle`
- [ ] Create/edit form (handle 409 feedback)
- [ ] Subscribe button component
- [ ] My subscriptions page
- [ ] Owner-only edit/delete UI

### Exit test
- [ ] Create → browse → visit handle → subscribe; non-owner edit = 403; double-subscribe impossible

---

## Phase 2 — Video + Storage (MinIO → R2)

### Backend — storage
- [ ] MinIO docker-compose + bucket + keys in env vars
- [ ] AWS SDK v2 + `S3Config` beans
- [ ] `StorageService` interface + `S3StorageService`
- [ ] Smoke test: presign → curl PUT → exists → download

### Backend — video
- [ ] Video entity complete (visibility, `storageKey`, `thumbnailKey`, sizeBytes, durationSeconds, viewCount, publishedAt)
- [x] `VideoStatus` enum
- [ ] `POST /videos/initiate` (validate → UPLOADING → presigned PUT)
- [ ] `POST /videos/{id}/complete` (verify object → READY)
- [ ] `GET /videos/{id}/watch` (visibility check → presigned GET)
- [ ] Video CRUD (edit / delete incl. storage object)
- [ ] View count rule
- [ ] Videos-per-channel + subscription feed
- [ ] Thumbnail upload + channel avatar/banner (parked from Phase 1)
- [ ] R2: bucket + token + `application-cloud.yml` + profile-switch test

### Frontend
- [ ] Upload page (picker / drag-drop / pre-checks)
- [ ] Upload flow: initiate → PUT with progress → complete
- [ ] Progress bar + retry/error states
- [ ] Processing / Ready status on my videos
- [ ] Watch page (`<video>` + metadata)
- [ ] Video grid component (reused: home / channel / feed)
- [ ] Edit video dialog + delete confirm
- [ ] Subscription feed page

### Exit test
- [ ] Upload from Angular → MinIO → plays with seeking
- [ ] Profile flip → identical against R2

---

## Phase 3 — Playlist + Channel Roles + Mail

### Backend
- [x] `PlaylistItem` with `position` + unique (playlist, video) + index
- [ ] PlaylistService reviewed (PR-style) and gaps fixed
- [ ] Reorder endpoint (positions rewritten in one transaction)
- [ ] Playlist public/private enforced on read
- [ ] `ChannelMember` entity (OWNER / MODERATOR, unique pair)
- [ ] Invite / remove member endpoints (owner only)
- [ ] `assertChannelRole()` on video/channel endpoints
- [ ] Mail module: events + `@Async` + welcome email
- [ ] Per-subscription notify flag

### Frontend
- [ ] Playlist pages (list / detail / create / rename / delete)
- [ ] Add-to-playlist button on watch page
- [ ] Drag-drop reorder (CDK)
- [ ] Continuous play
- [ ] Channel members tab (invite / remove)
- [ ] Role-based UI directive
- [ ] Notification toggle

### Exit test
- [ ] CV sentence "full CRUD + multi-layered RBAC" is 100% true
- [ ] Welcome email arrives without slowing register

---

## Phase 4 — Extraction: RabbitMQ + FFmpeg

### Monolith
- [ ] RabbitMQ compose + AMQP config (exchange, queues, retry, DLQ)
- [ ] `complete` → PROCESSING + publish `VideoUploadedEvent`
- [ ] Consume `VideoReadyEvent` → READY + duration + HLS key
- [ ] `watch` returns HLS URL (fallback: original)
- [ ] Mail consumes `VideoReadyEvent` (creator + subscribers)

### Worker (new app, no DB)
- [ ] `amryatube-transcoder` project
- [ ] Consume event → fetch original
- [ ] FFmpeg → HLS 480p + 720p + master playlist
- [ ] Upload segments via StorageService
- [ ] Publish `VideoReadyEvent` / `VideoFailedEvent` + reason
- [ ] Idempotency (skip if HLS exists)
- [ ] Poison messages → DLQ

### Frontend
- [ ] hls.js player (native fallback)
- [ ] Quality indicator
- [ ] Processing auto-refresh
- [ ] Failure state with reason

### Exit test
- [ ] Upload → queue → transcode → adaptive playback
- [ ] Worker killed mid-job → message survives → completes after restart

---

## Phase 5 — Hardening & Demo

- [ ] Unit tests: AuthService / ChannelService / Video state machine / playlist reorder
- [ ] `@WebMvcTest` auth + channel (401/403 asserted)
- [ ] Actuator + Micrometer trace across the queue
- [ ] Full docker-compose (MySQL + backend + worker + RabbitMQ + MinIO + Angular/nginx)
- [ ] Secrets hygiene pass (all env vars)
- [ ] Frontend: prod build behind nginx, skeletons, error toast, OG tags
- [ ] README: diagram + one-command run + screen recording
- [ ] CV: remove "(In Progress)"

### Exit test
- [ ] Stranger clones → `docker compose up` → watches a video

---

## Backlog (pull only after a phase's core is done)
- [ ] 59 Resume playback · [ ] 61 Like/dislike · [ ] 66 Resumable upload · [ ] 71 Creator dashboard · [ ] 76 Session invalidation · [ ] 84 Arabic UI + RTL
