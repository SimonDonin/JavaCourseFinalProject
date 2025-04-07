package cyberpro.game.controller;

public interface ControllerInterface {
    void playerMoveUp(String playerId);
    void playerMoveDown(String playerId);
    void playerMoveLeft(String playerId);
    void playerMoveRight(String playerId);
    String getPlayerIdByNumber(int playerNumber);
}

