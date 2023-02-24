package com.tjger.testapp.game.rules;

import com.tjger.game.GamePlayer;
import com.tjger.game.GameState;
import com.tjger.game.MoveInformation;
import com.tjger.game.SimpleGameRules;

public class TestGameRules extends SimpleGameRules {
    @Override
    public boolean isRoundFinished(GameState gameState) {
        return false;
    }

    @Override
    public boolean isGameFinished(GameState gameState) {
        return false;
    }

    @Override
    public void doScoring(GamePlayer[] playerToScore, GameState gameState) {

    }

    @Override
    public boolean isValidMove(MoveInformation move, GameState gameState) {
        return false;
    }
}
