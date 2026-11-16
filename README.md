# SkillSwap — Peer-to-Peer Student Skill Exchange (Java Swing + H2)

A desktop application built with **Pure Java (JDK 8+)**, **Java Swing/AWT**, **H2 Embedded Database (JDBC)**, and **BCrypt** password hashing following the **MVC architecture**.

## Features
- **Authentication**: Student registration & login with BCrypt password hashing (`LoginFrame`, `RegisterFrame`).
- **Dashboard Hub**: Real-time statistics cards, campus announcements, and sidebar navigation (`DashboardFrame`).
- **Session Management**: Create, edit, delete, and manage bookings for *Offering Help* and *Requesting Help* sessions (`CreateSessionDialog`, `MySessionsFrame`, `SessionBookingsDialog`).
- **Smart Peer Matching**: Calculates compatibility scores (0–100%) based on complementary skills and learning modes (`FindMatchesFrame`).
- **Skill Exchange Requests**: Send, accept, or reject direct peer skill swap proposals (`SendRequestDialog`, `MyRequestsFrame`).
- **Ratings & Leaderboard**: 1–5 star session reviews and Top 10 peer mentor rankings (`MyExchangesFrame`, `RateSessionDialog`, `LeaderboardFrame`).
- **Profile Management**: Update skills, learning preferences, and bio (`EditProfileDialog`).

## Setup & Running Locally

1. **Add Dependencies** to the `lib/` folder:
   - `h2-1.4.200.jar`
   - `bcrypt-0.10.2.jar` (or `jbcrypt-0.4.jar`)

2. **Compile & Run**:
   - **Windows**: Double-click or run `build.bat`
   - **macOS / Linux**: Run `chmod +x build.sh && ./build.sh`

## Demo Credentials (Auto-seeded on First Launch)
- **Email**: `alice@uni.edu` | **Password**: `pass123`
- **Email**: `bob@uni.edu` | **Password**: `pass123`
