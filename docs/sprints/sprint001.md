# Sprint 1 - Project Foundation

**Duration**: ?!
**Goal**: Set up project structure, database, and basic user authentication

## User Stories

### US-001: User Registration
- **As a** new user
- **I want** to create an account
- **So that** I can upload and manage videos
- **Acceptance Criteria**:
  - [ ] User can register with email and password
  - [ ] Password is hashed before storage
  - [ ] Duplicate emails are rejected
- **Story Points**: 5

### US-002: User Login
- **As a** registered user
- **I want** to log in securely
- **So that** I can access my account
- **Acceptance Criteria**:
  - [ ] User receives JWT token on successful login
  - [ ] Token is valid for 24 hours
  - [ ] Invalid credentials return appropriate error
- **Story Points**: 5

## Tasks
- [ ] Set up Spring Boot project
- [ ] Configure MySQL database
- [ ] Create User entity and repository
- [ ] Implement JWT configuration
- [ ] Create authentication endpoints
- [ ] Write unit tests

## Definition of Done
- Code reviewed
- Unit tests passing (>80% coverage)
- API documented
- No critical bugs
