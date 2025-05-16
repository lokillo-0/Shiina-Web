# **Shiina-Web**

[![](https://dcbadge.limes.pink/api/server/Dr79DU9kbD)](https://discord.gg/Dr79DU9kbD) ![Contributors](https://img.shields.io/github/contributors/osu-NoLimits/Shiina-Web?style=for-the-badge) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=for-the-badge)](http://makeapullrequest.com)

A Java-based web frontend for **bancho.py-ex** osu! private servers, with extensive features and plugin support.

## **Requirements**
- Java 21 (OpenJDK 21)
- Maven
- [bancho.py-ex](https://github.com/osu-NoLimits/bancho.py-ex) (standard bancho.py is not currently supported)

## **Installation**
1. `make install` - installs dependencies and compiles the project
2. Copy all files in `.config/` with `.example` extension and remove the `.example` suffix
   ```
   cp .config/.env.example .config/.env
   cp .config/customization.yml.example .config/customization.yml
   cp .config/logger.env.example .config/logger.env
   ```
3. Configure the files in `.config/` directory according to your needs
4. `make run` - starts the Shiina web frontend

## **Feature List**

### Core Features
- ✅ User authentication and authorization
- ✅ Homepage with server statistics
- ✅ User profiles with customizable userpages
- ✅ Profile picture changing
- ✅ Beatmap browsing and search
- ✅ Comprehensive leaderboard system
  - Global leaderboards
  - Country-specific leaderboards
  - Clan leaderboards with competitive statistics
- ✅ Score tracking and display
  - First place scores
  - Personal best scores
  - Most recent scores
  - Playcount graphs
- ✅ Admin panel with extensible functionality

### Customization & Extensibility
- ✅ Multiple theme support (Classic, Modern, Elegant) seen in [THEMING](https://github.com/osu-NoLimits/Shiina-Web/wiki/Theming)
- ✅ Java plugin system with event hooks
- ✅ Extensive API integration
- ✅ Donation system (Kofi)

### Technical Features
- ✅ API request caching for improved performance
- ✅ Configurable error and request logging
- ✅ Easy customization via `.config/customization.yml`
- ✅ Multiple webhook support

### In Progress
- 🧩 SEO optimization

## **Plugin System**

Shiina features a Java plugin system that allows extending functionality without modifying the core codebase.

Custom plugins can be added to the `/plugins` directory. To learn making plugins for shiina check out [PLUGINS](https://github.com/osu-NoLimits/Shiina-Web/wiki/Plugins)

## **Project Structure**

```
shiina/
├── .config/         # Configuration files
├── data/            # Application data storage
├── docs/            # Documentation
├── logs/            # Application logs
├── plugins/         # Java plugins and configurations
├── src/             # Source code
├── static/          # Static web assets (CSS, JS, images)
├── target/          # Compiled output
└── templates/       # HTML templates
```

## **License**

This project is licensed under the MIT LICENSE found in the [LICENSE](/LICENSE) file.

## **Contributing**

Contributions are welcome! Please feel free to submit a Pull Request.