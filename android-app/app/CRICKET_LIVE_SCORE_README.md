# Cricket Live Score Module

A comprehensive cricket live score tracking system with real-time updates, admin controls, and user-friendly interface.

## Features

### 🏏 Core Cricket Features
- **Live Score Tracking**: Real-time score updates with WebSocket integration
- **Match Management**: Create, start, and manage cricket matches
- **Admin Controls**: Full admin panel for score updates and match management
- **Multiple Match Types**: Support for T20, ODI, and Test matches
- **Detailed Statistics**: Runs, wickets, overs, run rate, and more

### 📱 User Interface
- **Modern UI**: Beautiful Material Design 3 interface
- **Real-time Updates**: Live score updates without page refresh
- **Admin Panel**: Comprehensive admin controls for match management
- **Responsive Design**: Works seamlessly on all screen sizes
- **Intuitive Navigation**: Easy-to-use navigation between screens

### 🔧 Technical Features
- **WebSocket Integration**: Real-time bidirectional communication
- **MVVM Architecture**: Clean separation of concerns
- **Kotlin Serialization**: Type-safe data serialization
- **State Management**: Reactive state management with StateFlow
- **Error Handling**: Comprehensive error handling and user feedback

## Architecture

### Backend (Ktor Server)
```
cricket_backend_enhanced.kt
├── Cricket Match Models
│   ├── CricketMatch
│   ├── CricketInnings
│   ├── Batsman
│   ├── Bowler
│   ├── Over
│   ├── Ball
│   └── Extras
├── WebSocket Endpoints
│   └── /ws/{matchId}
├── REST API Endpoints
│   ├── POST /matches (Create match)
│   ├── GET /matches (List matches)
│   ├── GET /matches/{id} (Get match)
│   ├── PUT /matches/{id}/status (Update status)
│   └── PUT /matches/{id}/admin (Set admin)
└── Match Store
    ├── createMatch()
    ├── updateScore()
    ├── updateInnings()
    └── broadcast()
```

### Frontend (Android Compose)
```
src/main/java/com/project/odoo_235/
├── data/
│   ├── models/
│   │   └── dataclass.kt (Enhanced with cricket models)
│   ├── api/
│   │   └── CricketApiClient.kt
│   └── repository/
│       └── CricketWebSocketManager.kt
├── presentation/
│   ├── viewmodels/
│   │   └── CricketMatchViewModels.kt
│   └── screens/user/screen/livematches/
│       ├── CricketMatchScreens.kt
│       └── CricketAdminPanel.kt
└── navigation/
    └── Screen.kt (Enhanced with cricket routes)
```

## Data Models

### CricketMatch
```kotlin
data class CricketMatch(
    val id: String,
    val teamA: String,
    val teamB: String,
    val matchType: String, // "T20", "ODI", "Test"
    val totalOvers: Int,
    val status: MatchStatus,
    val adminId: String?,
    val innings: List<CricketInnings>,
    val currentInnings: Int,
    val currentOver: Int,
    val currentBall: Int,
    val currentBatsman: String?,
    val currentBowler: String?,
    val target: Int?,
    val requiredRuns: Int?,
    val requiredBalls: Int?,
    val runRate: Double,
    val requiredRunRate: Double?
)
```

### CricketInnings
```kotlin
data class CricketInnings(
    val inningsNumber: Int,
    val battingTeam: String,
    val bowlingTeam: String,
    val totalRuns: Int,
    val totalWickets: Int,
    val totalOvers: Double,
    val runRate: Double,
    val batsmen: List<Batsman>,
    val bowlers: List<Bowler>,
    val overs: List<Over>,
    val extras: Extras,
    val isCompleted: Boolean
)
```

## API Endpoints

### REST Endpoints
- `POST /matches` - Create a new cricket match
- `GET /matches` - Get all cricket matches
- `GET /matches/{id}` - Get specific match details
- `PUT /matches/{id}/status` - Update match status
- `PUT /matches/{id}/admin` - Set match admin

### WebSocket Endpoints
- `ws://localhost:8080/ws/{matchId}` - Real-time match updates

## Usage

### 1. Starting the Backend Server
```bash
# Run the enhanced cricket backend
kotlin cricket_backend_enhanced.kt
```

### 2. Creating a Match
```kotlin
val apiClient = CricketApiClient()
val match = apiClient.createCricketMatch(
    teamA = "India",
    teamB = "Australia", 
    matchType = "T20",
    overs = 20
)
```

### 3. Connecting to Live Updates
```kotlin
val webSocketManager = CricketWebSocketManager(matchId)
webSocketManager.connect()

// Listen for updates
webSocketManager.messages.collect { message ->
    when (message) {
        is CricketServerUpdate -> {
            // Handle live score update
            updateUI(message.match)
        }
    }
}
```

### 4. Admin Score Updates
```kotlin
val scoreUpdate = CricketScorePayload(
    runs = 150,
    wickets = 3,
    overs = 15.2,
    runRate = 9.8
)
webSocketManager.sendCricketScoreUpdate(scoreUpdate)
```

## Screens

### 1. Cricket Match List
- View all available cricket matches
- Create new matches
- Filter by match type and status
- Real-time status updates

### 2. Match Detail Screen
- Live score display
- Current innings information
- Admin controls (if admin)
- Real-time updates

### 3. Admin Panel
- Match status management
- Score updates
- Batsman management
- Bowler management
- Ball-by-ball controls

## Navigation

### Routes
- `/cricket_matches` - Match list screen
- `/cricket_match/{matchId}` - Match detail screen
- `/cricket_admin/{matchId}` - Admin panel

### Navigation from Home
Click on the "Cricket" sports card in the home screen to navigate to cricket matches.

## WebSocket Messages

### Client Messages
```kotlin
sealed class ClientMessage {
    data class CricketScoreUpdate(val update: CricketScorePayload) : ClientMessage()
    data class UpdateInnings(val innings: CricketInnings) : ClientMessage()
    data class UpdateBowler(val bowler: Bowler) : ClientMessage()
    data class UpdateBatsman(val batsman: Batsman) : ClientMessage()
    object RequestSnapshot : ClientMessage()
    object Heartbeat : ClientMessage()
}
```

### Server Messages
```kotlin
sealed class ServerMessage {
    data class CricketServerSnapshot(val match: CricketMatch) : ServerMessage()
    data class CricketServerUpdate(val match: CricketMatch) : ServerMessage()
    data class ServerError(val code: String, val message: String) : ServerMessage()
}
```

## Admin Features

### Match Management
- Start/stop matches
- Update match status
- Set match admin
- Manage match settings

### Score Management
- Update runs and wickets
- Manage overs
- Update run rate
- Handle extras

### Player Management
- Add/remove batsmen
- Add/remove bowlers
- Update player statistics
- Manage strike rotation

### Ball-by-Ball Controls
- Quick run buttons (0, 1, 2, 3, 4, 6)
- Special ball handling (wide, no ball, wicket)
- Over completion
- Innings management

## Error Handling

### Connection Errors
- Automatic reconnection attempts
- Connection status indicators
- User-friendly error messages

### Data Validation
- Input validation for score updates
- Match status validation
- Player data validation

## Performance Optimizations

### WebSocket Management
- Efficient message serialization
- Connection pooling
- Automatic cleanup on disconnect

### UI Optimizations
- Lazy loading for match lists
- Efficient state updates
- Minimal recomposition

## Testing

### Unit Tests
- ViewModel testing
- API client testing
- WebSocket manager testing

### Integration Tests
- End-to-end match creation
- Real-time update testing
- Admin functionality testing

## Deployment

### Backend Deployment
1. Deploy the Ktor server to your preferred hosting platform
2. Update the WebSocket URL in the Android app
3. Configure CORS settings if needed

### Android App
1. Update the base URL in `CricketApiClient.kt`
2. Update the WebSocket host in `CricketWebSocketManager.kt`
3. Build and deploy the APK

## Configuration

### Backend Configuration
```kotlin
// Update these values in cricket_backend_enhanced.kt
embeddedServer(Netty, port = 8080, host = "0.0.0.0")
```

### Frontend Configuration
```kotlin
// Update these values in CricketApiClient.kt
private val baseUrl = "http://192.168.43.29:8080"

// Update these values in CricketWebSocketManager.kt
host = "192.168.43.29"
port = 8080
```

## Future Enhancements

### Planned Features
- [ ] Match statistics and analytics
- [ ] Player profiles and history
- [ ] Tournament management
- [ ] Push notifications
- [ ] Offline support
- [ ] Match recording and playback
- [ ] Social features (comments, reactions)
- [ ] Multi-language support

### Technical Improvements
- [ ] Database persistence
- [ ] Authentication and authorization
- [ ] Rate limiting
- [ ] Caching strategies
- [ ] Performance monitoring
- [ ] Automated testing

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

---

**Note**: This cricket live score module is fully integrated with the existing Odoo 235 app and provides a complete solution for cricket match management and live score tracking.
