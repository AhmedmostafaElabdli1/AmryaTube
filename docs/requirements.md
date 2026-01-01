# Requirements

## Functional Requirements

### Must Have
- [ ] User registration and login
- [ ] JWT-based authentication
- [ ] Basic user profile management
- [ ] Video upload (up to 2GB)
- [ ] Video metadata storage (title, description, tags)
- [ ] Basic video listing

### Should Have 
- [ ] Video transcoding (multiple qualities)
- [ ] HLS streaming
- [ ] Video playback with quality selector
- [ ] User channels
- [ ] Comments system
- [ ] Like/Dislike functionality

### Nice to Have 
- [ ] Video transcription
- [ ] AI-powered recommendations
- [ ] Search with AI matching
- [ ] Playlists
- [ ] Video analytics

## Non-Functional Requirements
- Videos must start playing within 3 seconds
- Support concurrent uploads (at least 10)
- 99.5% uptime target
- Secure password storage (BCrypt)
- API rate limiting

## Technical Constraints
- MySQL for structured data
- Local filesystem for video storage
- JWT tokens expire in 24 hours
- Maximum video size: 2GB
