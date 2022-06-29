package com.tjger.game.internal;

import at.hagru.hgbase.lib.IntCollection;

/**
 * This class saves won and played games (of a player).
 */
public class WonPlayedGames extends IntCollection {

    public WonPlayedGames(int won, int played) {
        super(new int[] { won, played} );
    }

    public int getWonGames() {
        return get(0);
    }

    public int getPlayedGames() {
        return get(1);
    }
}