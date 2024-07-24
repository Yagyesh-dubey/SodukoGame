import javax.swing.*;
import java.awt.*;

public class SudokuFrame extends JFrame {
    private SudokuPanel sudokuPanel;

    public SudokuFrame() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        sudokuPanel = new SudokuPanel();
        add(sudokuPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
