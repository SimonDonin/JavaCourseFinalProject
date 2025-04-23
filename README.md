Ты абсолютно прав, что в GitHub используется язык разметки Markdown для оформления README файлов. Давай я переработаю текст, чтобы он соответствовал стандартам GitHub и выглядел красиво:
# **2D Retro Game**

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

---

## **Problem Definition**

### **What problem does it solve?**
People often seek an engaging activity to enjoy with friends or family. This project provides a simple yet dynamic **2D retro-style game** that keeps players entertained for hours.

> *Basic game rules are available via the following [link](#).*

---

## **Key Features**
### **Gameplay Enhancements**
- 🎇 **Modifiers** grant players abilities such as:
  - Additional bombs for simultaneous planting.
  - Longer explosion rays.
  - Increased movement speed.
  - Remote bomb detonation.
  - Reverse-control challenges for added complexity.

### **Dynamic Game Mechanics**
- **Chain Bomb Explosions**: Exploding bombs detonate other bombs within range.
- **Leaderboards**: Players accumulate stats and wins.
- **Audio Effects**: Action-packed sounds and challenging background music.

### **Player Sets**
- Stores player pairs and restores them upon app re-launch.

---

## **Goals**

### **Ultimate Goal**
To develop an exciting **2D retro-style game** designed for two players to play together on one keyboard.

### **What it should achieve?**
To engage two players in **at least one hour of enjoyable gameplay**, creating unforgettable moments.

---

## **Project Plan**
The full project plan is outlined in a Google Sheet, accessible via [this link](#).

---

## **Technical Implementation**

The project follows a **layered architecture**:

### **Packages**
- `model`
- `view`
- `controller`
- `service`

---

### **Classes & Interfaces**

#### **Model**
- **`Board`**: Represents the playboard grid with specific tiles or sprites at each cell.
- **`Bomb`**: Can be planted and exploded, destroying brick walls and killing players.
- **`Player`**: Represents human-controlled characters trying to win the game.
- **`Coordinates`**: Specifies a cell's position on the board.
- **`Game`**: Contains everything necessary to run the game.
- **`Modifier`**: Enhances players' abilities with special powers.

#### **View**
- **`MainMenu`**: Contains the game menu for choosing players, levels, and starting a new game.
- **`GameView`**: The main game window where gameplay occurs.
- **`GameOverWindow`**: Displays results at the end of the game.
- **`TileType`**: Enum holding possible types of tiles.
- **`ImageTileCutter`**: Cuts tiles from images.

#### **Controller**
- **`GameController`**: Manages application logic, scheduling, and events.
- **`ControllerInterface`**: Prevents irrelevant calls to `GameView` methods.

#### **Service**
- **`DataHandler`**: Handles serialization and deserialization for saving/loading player data.
- **`FileResourcesImporter`**: Loads board structures from external files based on level specifications.

---

## **How to Run**

### **Prerequisites**
- **Java JDK** (17 or higher)
- **Apache Maven**

### **Commands**
1. Clone the repository:
   ```bash
   git clone https://github.com/your_username/repository_name.git
