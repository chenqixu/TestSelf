package com.newland.bi.bigdata.thread.interrupted;

/**
 * PlayerMatcher
 *
 * @author chenqixu
 */
public class PlayerMatcher {
    private PlayerSource players;

    public PlayerMatcher(PlayerSource players) {
        this.players = players;
    }

    public void matchPlayers() throws InterruptedException {
        Player playerOne = null, playerTwo;
        try {
            while (true) {
                playerOne = playerTwo = null;
                // Wait for two players to arrive and start a new game
                playerOne = players.waitForPlayer(); // could throw IE
                playerTwo = players.waitForPlayer(); // could throw IE
                startNewGame(playerOne, playerTwo);
            }
        } catch (InterruptedException e) {
            // If we got one player and were interrupted, put that player back
            if (playerOne != null)
                players.addFirst(playerOne);
            // Then propagate the exception
            throw e;
        }
    }

    void startNewGame(Player playerOne, Player playerTwo) {
    }

    class PlayerSource {
        Player waitForPlayer() throws InterruptedException {
            return null;
        }

        void addFirst(Player player) {
        }
    }

    class Player {
    }
}
