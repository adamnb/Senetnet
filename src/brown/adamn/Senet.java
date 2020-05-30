package brown.adamn;

import java.util.Random;

public class Senet {
    private int[] board;   // Stores position of player pieces
    private int turn  = 0; // Current player's turn. 1: player 1 (server), 2: player 2 (client), 0: no player
    private int roll  = 1; // What the current roll amount is for the current turn
    private int moves = 0; // How many moves have been executed
    private char[] playerSymbol = new char[2]; // On-board representation of character pieces
    private  int[] score        = new int[2];

    /**
     * @param player1 The symbol representing player 1
     * @param player2 The symbol representing player 2
     * @param initiative Which player (1, 2, or 0) goes first
     * @param rows The number of rows in the generated board
     */
    public Senet (char player1, char player2, int initiative, int rows) {
        if (player1 == player2) {
            player1 = 'A';
            player2 = 'B';
        }

        if (rows < 1)
            rows = 3;
        if (rows > 10)
            rows = 10;
        board = new int[10*rows];

        playerSymbol[0] = player1;
        playerSymbol[1] = player2;

        // Keep first player within range
        if (initiative > 2)
            turn = 2;
        else if (initiative < 0)
            turn = 1;
        else
            turn = initiative;

        // Populate board
        for (int i = 0; i < 10; i++) {
            if (i%2 == 0) {
                board[i] = oppTurn();
            } else {
                board[i] = turn;
            }
        }
    }
    public Senet (char player1, char player2, int initiative){ this (player1, player2, initiative, 3); }
    public Senet (char player1, char player2) { this ('A', 'B', 1, 3); }
    public Senet (int rows) { this('A', 'B', 1, rows); }
    public Senet () { this ('A', 'B', 1, 3);}

    /**
     * @param print If set to true, prints the board directly into the console
     * @return The board represented in text
     */
    // TODO: Use StringBuilder for concatenation
    public String drawBoard(boolean print) {
        String ret = "";

        for (int j = 0; j < board.length; j+=10) {
            // Display board pieces
            if (j >= 10)
                ret+="\n";

            for (int i = j; i < j+10; i++) {
                char resolvedSym = board[i] == 0 ? ' ' : playerSymbol[ board[i]-1]; // Shows a space if there is no gamepiece
                ret += " " + resolvedSym + " ";
            }
            ret += "\n";
            // Write numbers below
            for (int i = 0; i < 10; i++) {
                // Handle formatting for single & double digit numbers
                String digit = "" + (j + i + 1);
                if (j + i + 1 < 10)
                    digit = (j + i + 1) + " ";
                ret += " " + digit;
            }
            ret += "\n------------------------------"; // Row separator
        }

        if (print)
            System.out.println(ret);

        return ret;
    }
    public String drawBoard () { return drawBoard(true); }

    public int move (int pos) {
        // Determining move distance and handling extra turns
        boolean extraTurn = false;
        int spaces = roll;
        if (roll == 0) { // No white sides up
            spaces = 6;
            extraTurn = true;
        } else if (roll == 4 || roll == 1) {
            extraTurn = true;
        }

        final int destPos = pos+spaces;
        final int dest = board[destPos]; // The boardpiece where the selected piece is going

        // Invalid Moves
        if (board[pos] == 0)
            return 1; // Illegal move: There is no piece at this position
        if (board[pos] != turn)
            return 2; // Illegal move: Attempt to move opponent piece
        if (dest == turn)
            return 3; // Illegal move: Attempt to attack friendly piece
        if (dest == oppTurn() && isGuarded(destPos))
            return 4; // Illegal move: This piece is guarded


        if (pos+spaces >= board.length) {
            moves++;
            board[pos] = 0;
            score[turn-1]++;
            nextTurn();
            return -oppTurn(); // Player has moved a piece off the board. Congrats!
        }

        if (dest == oppTurn()) { // Swap places with opponent
            board[pos+spaces] = turn;
            board[pos] = oppTurn();
        }

        if (dest == 0) { // Move piece to empty square
            board[pos+spaces] = turn;
            board[pos] = 0;
        }

        if (!extraTurn) {
            nextTurn();
        }

        moves++;
        return 0;

    }

    /**
     * Simulates dice-stick throwing and updates the roll attribute accordingly
     * @return The result of throwing the sticks
     */
    public int toss () {
        int result = 0;

        Random rng = new Random();
        for (int i = 0; i < 4; i++) { // Throw four sticks (flip four coins)
            double r = rng.nextDouble();
            if (r < 0.5)
                result++;
        }

        roll = result;
        return result;
    }

    /**
     * @return The current opponents turn number. 1 for player 1. 2 for player 2.
     */
    int oppTurn () {
        if (turn == 0)
            return 0;
        if (turn == 1)
            return 2;
        else
            return 1;
    }

    boolean isGuarded (int pos) {
        // Out of bounds protection
        if (pos < 0)
            pos = 0;
        if (pos >= board.length)
            pos = board.length-1;

        int team = board[pos];
        if (pos > 0 && board[pos-1] == team)
            return true;
        if (pos < board.length-1 && board[pos+1] == team)
            return true;

        return false;
    }

    /**
     * This method switches the turn number to the opposite player
     * @return The turn number after being switched
     */
    // Progress to next turn
    public int nextTurn() {
        turn = oppTurn();
        return turn;
    }

    // Set-Get methods
    public int getTurn() { return turn; }

    public int[] getBoard() { return board; }

    public char[] getPlayerSymbol() { return playerSymbol; }

    public int getMoves () { return moves; }

    public int[] getScore () { return score; }

}