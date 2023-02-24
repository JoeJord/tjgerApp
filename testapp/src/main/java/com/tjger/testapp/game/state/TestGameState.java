package com.tjger.testapp.game.state;

import com.tjger.game.GamePlayer;
import com.tjger.game.GameState;
import com.tjger.game.MoveInformation;
import com.tjger.game.completed.GameEngine;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TestGameState implements GameState {
    @Override
    public void resetGame(GameEngine engine) {

    }

    @Override
    public void resetRound(GameEngine engine) {

    }

    @Override
    public void resetTurn(GameEngine engine) {

    }

    @Override
    public void stopGame() {

    }

    @Override
    public void changeState(GamePlayer player, MoveInformation move, GameEngine engine) {

    }

    @Override
    public void undoMove(GamePlayer player, MoveInformation move) {

    }

    @Override
    public Object clone() {
        return null;
    }

    @Override
    public int save(Document doc, Element root) {
        return 0;
    }

    @Override
    public int load(Node node) {
        return 0;
    }

    @Override
    public String toNetworkString() {
        return null;
    }

    @Override
    public boolean fromNetworkString(String data) {
        return false;
    }

    @Override
    public String toNetworkStringMove(MoveInformation move) {
        return null;
    }

    @Override
    public MoveInformation fromNetworkStringMove(String data) {
        return null;
    }
}
