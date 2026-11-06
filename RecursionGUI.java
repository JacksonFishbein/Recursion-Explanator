import javax.swing.*;
import java.awt.*;

public class RecursionGUI extends JFrame{
    //Main GUI window  dimensions
    private static final int WindowWidth = 1200;
    private static final int WindowHeight = 800;
    
    public RecursionGUI() {
        //Mian appllication window
        setTitle("Recursion Explanator");
        setSize(WindowWidth, WindowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Main Layout
        setLayout(new BorderLayout());

        //Application Title
        JLabel title = new JLabel(
            "Recusion Explanator", SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        add(title, BorderLayout.NORTH);

        //Start Instructions
        JLabel welcome = new JLabel(
            "Import or Create a Java program to begin.", SwingConstants.CENTER
        );

        welcome.setFont(new Font("Arial", Font.PLAIN, 18));
        add(welcome, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        RecursionGUI gui = new RecursionGUI();
        gui.setVisible(true);
    });
}
}
