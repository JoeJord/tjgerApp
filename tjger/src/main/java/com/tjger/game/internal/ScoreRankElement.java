package com.tjger.game.internal;

import at.hagru.hgbase.lib.IntCollection;

/**
 * This class saves the current scores and the current round rank in one element.
 */
public class ScoreRankElement extends IntCollection {

    public ScoreRankElement(int score, int rank) {
        super(new int[] { score, rank} );
    }

    public int getScore() {
        return get(0);
    }

    public int getRank() {
        return get(1);
    }
}