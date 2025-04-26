import javax.swing.*;
import java.awt.*;

public class TicTac {
    private JFrame frame;
    private JPanel panel;
    private JButton[][] buttons;
    private JLabel status;
    private boolean vsComputer;
    private boolean playerXTurn;
    private char[][] board;
    private int xWins, oWins, draws;

    public TicTac() {
        xWins = oWins = draws = 0;
        initStartMenu();
    }

    private void initStartMenu() {
        frame = new JFrame("Tic Tac Toe");
        JPanel menu = new JPanel();
        menu.setLayout(new GridLayout(3, 1));

        JButton vsHuman = new JButton("Player vs Player");
        JButton vsComp = new JButton("Player vs Computer");

        vsHuman.addActionListener(e -> {
            vsComputer = false;
            initGame();
        });
        vsComp.addActionListener(e -> {
            vsComputer = true;
            initGame();
        });

        menu.add(vsHuman);
        menu.add(vsComp);

        frame.add(menu);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initGame() {
        frame.getContentPane().removeAll();
        panel = new JPanel(new GridLayout(3, 3));
        buttons = new JButton[3][3];
        board = new char[3][3];
        playerXTurn = true;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                final int r = i, c = j;
                buttons[i][j].addActionListener(e -> handleMove(r, c));
                panel.add(buttons[i][j]);
                board[i][j] = ' ';
            }
        }

        JPanel bottomPanel = new JPanel(new BorderLayout());

        status = new JLabel("X's Turn");
        JButton restartGame = new JButton("Restart Game");
        JButton restartMatch = new JButton("Restart Match");

        restartGame.addActionListener(e -> resetGame(false));
        restartMatch.addActionListener(e -> resetGame(true));

        bottomPanel.add(status, BorderLayout.CENTER);
        bottomPanel.add(restartGame, BorderLayout.WEST);
        bottomPanel.add(restartMatch, BorderLayout.EAST);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.revalidate();
        frame.repaint();
    }

    private void handleMove(int r, int c) {
        if (board[r][c] != ' ')
            return;

        board[r][c] = playerXTurn ? 'X' : 'O';
        buttons[r][c].setText(Character.toString(board[r][c]));
        buttons[r][c].setForeground(playerXTurn ? Color.RED : Color.BLUE);

        if (checkWin(playerXTurn ? 'X' : 'O')) {
            if (playerXTurn) xWins++;
            else oWins++;
            status.setText((playerXTurn ? "X" : "O") + " wins!");
            highlightWin(playerXTurn ? 'X' : 'O');
            JOptionPane.showMessageDialog(frame, "Congratulations! " + (playerXTurn ? "X" : "O") + " wins!");
            return;
        } else if (isBoardFull()) {
            draws++;
            status.setText("It's a draw!");
            JOptionPane.showMessageDialog(frame, "It's a draw!");
            return;
        }

        playerXTurn = !playerXTurn;
        status.setText((playerXTurn ? "X" : "O") + "'s Turn");

        if (vsComputer && !playerXTurn) {
            makeComputerMove();
        }
    }

    private void makeComputerMove() {
        int[] move = findBestMove();
        handleMove(move[0], move[1]);
    }

    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] move = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = 'O';
                    int score = minimax(false);
                    board[i][j] = ' ';
                    if (score > bestScore) {
                        bestScore = score;
                        move = new int[]{i, j};
                    }
                }
            }
        }

        return move;
    }

    private int minimax(boolean isMaximizing) {
        if (checkWin('O')) return 1;
        if (checkWin('X')) return -1;
        if (isBoardFull()) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'O';
                        bestScore = Math.max(bestScore, minimax(false));
                        board[i][j] = ' ';
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'X';
                        bestScore = Math.min(bestScore, minimax(true));
                        board[i][j] = ' ';
                    }
                }
            }
            return bestScore;
        }
    }

    private void resetGame(boolean resetScores) {
        if (resetScores) {
            xWins = oWins = draws = 0;
        }
        initGame();
    }

    private boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player)
                return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player)
                return true;
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player)
            return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player)
            return true;
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == ' ')
                    return false;
        return true;
    }

    private void highlightWin(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                flashButtons(new int[][]{{i, 0}, {i, 1}, {i, 2}});
                return;
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                flashButtons(new int[][]{{0, i}, {1, i}, {2, i}});
                return;
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            flashButtons(new int[][]{{0, 0}, {1, 1}, {2, 2}});
        } else if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            flashButtons(new int[][]{{0, 2}, {1, 1}, {2, 0}});
        }
    }

    private void flashButtons(int[][] cells) {
        new Thread(() -> {
            try {
                for (int k = 0; k < 6; k++) {
                    for (int[] cell : cells) {
                        buttons[cell[0]][cell[1]].setBackground(k % 2 == 0 ? Color.YELLOW : Color.WHITE);
                    }
                    Thread.sleep(300);
                }
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTac::new);
    }
}
