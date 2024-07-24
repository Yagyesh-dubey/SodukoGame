import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class SudokuPanel extends JPanel {
    private static final int SIZE = 9;
    private SudokuCell[][] cells;
    private int[][] board = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    private Timer timer;
    private long startTime;
    private JLabel timerLabel;
    private boolean gameStarted = false;

    public SudokuPanel() {
        setLayout(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
        cells = new SudokuCell[SIZE][SIZE];
        initializeBoard(gridPanel);

        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton resetButton = new JButton("Reset");
        JButton checkButton = new JButton("Check");

        startButton.addActionListener(e -> startGame());
        resetButton.addActionListener(e -> resetGame());
        checkButton.addActionListener(e -> checkSolution());

        controlPanel.add(startButton);
        controlPanel.add(resetButton);
        controlPanel.add(checkButton);

        timerLabel = new JLabel("Time: 00:00");
        controlPanel.add(timerLabel);

        add(gridPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void initializeBoard(JPanel gridPanel) {
        gridPanel.removeAll();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new SudokuCell(i, j, board[i][j] != 0);
                if (board[i][j] != 0) {
                    cells[i][j].setText(String.valueOf(board[i][j]));
                    cells[i][j].setEditable(false);
                } else {
                    cells[i][j].setText("");
                }
                gridPanel.add(cells[i][j]);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void startGame() {
        if (gameStarted) return;
        gameStarted = true;
        startTime = System.currentTimeMillis();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                int minutes = (int) (elapsed / 60000);
                int seconds = (int) (elapsed % 60000) / 1000;
                SwingUtilities.invokeLater(() -> timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds)));
            }
        }, 0, 1000);
    }

    private void resetGame() {
        gameStarted = false;
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timerLabel.setText("Time: 00:00");
        initializeBoard((JPanel) getComponent(0)); // Reinitialize the board
    }

    private void checkSolution() {
        boolean correct = true;
        boolean allFilled = true;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int value = getCellValue(i, j);
                if (value != 0) {
                    if (!isValid(value, i, j) && !cells[i][j].isPreFilled()) {
                        cells[i][j].setBackground(Color.RED);
                        correct = false;
                    } else {
                        cells[i][j].setBackground(Color.WHITE);
                    }
                } else {
                    allFilled = false;
                }
            }
        }

        if (allFilled && correct) {
            JOptionPane.showMessageDialog(this, "Congratulations! 🎉 You solved the Sudoku correctly.");
        } else if (!allFilled) {
            JOptionPane.showMessageDialog(this, "Nice! Keep going and fill in the remaining cells.");
        } else {
            // Highlight only user-filled incorrect cells
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (!cells[i][j].isPreFilled() && !isValid(getCellValue(i, j), i, j)) {
                        cells[i][j].setBackground(Color.RED);
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Try again! Incorrect cells are highlighted.");
        }
    }

    private int getCellValue(int row, int col) {
        String text = cells[row][col].getText();
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    private boolean isValid(int value, int row, int col) {
        // Check the row
        for (int c = 0; c < SIZE; c++) {
            if (c != col && getCellValue(row, c) == value) {
                return false;
            }
        }
        // Check the column
        for (int r = 0; r < SIZE; r++) {
            if (r != row && getCellValue(r, col) == value) {
                return false;
            }
        }
        // Check the 3x3 grid
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if ((r != row || c != col) && getCellValue(r, c) == value) {
                    return false;
                }
            }
        }
        return true;
    }

    private class SudokuCell extends JTextField {
        private int row;
        private int col;
        private boolean preFilled; // Flag to mark pre-filled cells

        public SudokuCell(int row, int col, boolean preFilled) {
            this.row = row;
            this.col = col;
            this.preFilled = preFilled;
            setHorizontalAlignment(JTextField.CENTER);
            setFont(new Font("Arial", Font.BOLD, 20));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setPreferredSize(new Dimension(50, 50));
            setDocument(new LimitedDocument());

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int keyCode = e.getKeyCode();
                    if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_9) {
                        setText(String.valueOf(keyCode - KeyEvent.VK_0));
                    } else {
                        switch (keyCode) {
                            case KeyEvent.VK_UP:
                                if (row > 0) {
                                    cells[row - 1][col].requestFocus();
                                }
                                break;
                            case KeyEvent.VK_DOWN:
                                if (row < SIZE - 1) {
                                    cells[row + 1][col].requestFocus();
                                }
                                break;
                            case KeyEvent.VK_LEFT:
                                if (col > 0) {
                                    cells[row][col - 1].requestFocus();
                                }
                                break;
                            case KeyEvent.VK_RIGHT:
                                if (col < SIZE - 1) {
                                    cells[row][col + 1].requestFocus();
                                }
                                break;
                        }
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    requestFocus();
                }
            });
        }

        public boolean isPreFilled() {
            return preFilled;
        }
    }

    private class LimitedDocument extends PlainDocument {
        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str != null && str.length() > 0) {
                char c = str.charAt(0);
                if (c >= '1' && c <= '9') {
                    if (getLength() == 0) { // Allow only one digit
                        super.insertString(offset, str, attr);
                    }
                }
            }
        }
    }
}
