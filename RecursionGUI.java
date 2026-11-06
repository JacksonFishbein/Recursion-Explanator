import javax.swing.*;
import java.awt.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RecursionGUI extends JFrame{
    //Main GUI window  dimensions
    private static final int WindowWidth = 1200;
    private static final int WindowHeight = 800;

    //GUI components
    private JTextArea codeEditor;
    private JLabel fileStatus;

    private File currentFile; //tracks the open Java File
    
    public RecursionGUI() {
        //Mian appllication window
        setTitle("Recursion Explanator");
        setSize(WindowWidth, WindowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Main Layout
        setLayout(new BorderLayout(10, 10));

        //Application Title
        JLabel title = new JLabel(
            "Recusion Explanator", SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        //Top section
        JPanel topPanel = new JPanel (new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(createToolbar(), BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        //Code editor
        codeEditor = new JTextArea();
        codeEditor.setFont(new Font("Consolas", Font.PLAIN, 15));
        codeEditor.setTabSize(4);
        codeEditor.setLineWrap(false);
        codeEditor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane editorScrollPane = new JScrollPane(codeEditor);

        editorScrollPane.setBorder(BorderFactory.createTitledBorder("Java Code Editor"));

        add(editorScrollPane, BorderLayout.CENTER);

        //File Status Bar
        fileStatus = new JLabel("No file currently loaded");
        fileStatus.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        add(fileStatus, BorderLayout.SOUTH);
    }

    //Toolbar for File Controls
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        //Java File access buttons
        JButton openButton = new JButton("Open Java File");
        JButton saveButton = new JButton("Save File");
        JButton clearButton = new JButton("Clear Editor");

        openButton.addActionListener(e -> openJavaFile());
        saveButton.addActionListener(e -> saveJavaFile());
        clearButton.addActionListener(e -> clearEditor());

        toolbar.add(openButton);
        toolbar.add(saveButton);
        toolbar.add(clearButton);

        return toolbar;
    }

    //Opens the File Chooser and loads the Java Source File
    private void openJavaFile() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileFilter(
            new FileNameExtensionFilter(
                "Java Source Files (*.java)",
                "java"
            )
        );

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                String sourceCode = Files.readString(
                    selectedFile.toPath()
                );

                codeEditor.setText(sourceCode);
                codeEditor.setCaretPosition(0);

                currentFile = selectedFile;

                fileStatus.setText(
                    "Loaded: " + currentFile.getName()
                );

                setTitle( "Recursive Explanator -" + currentFile.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this, "Could not open selected file:\n" + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    //Saves the contents of the editor to the current File
    private void saveJavaFile(){
        if (currentFile == null) {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setFileFilter(
                new FileNameExtensionFilter(
                    "Java Source Files (*.java)",
                    "java"
                )
            );

            int result = fileChooser.showSaveDialog(this);

            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            currentFile = fileChooser.getSelectedFile();

            if (!currentFile.getName().toLowerCase().endsWith(".java")) {
                currentFile = new File(
                    currentFile.getAbsolutePath() + ".java"
                );
            }
        }
        
        try {
            Files.writeString(
                currentFile.toPath(), codeEditor.getText()
            );

            fileStatus.setText(
                "Saved: " + currentFile.getName()
            );

            setTitle(
                "Recursive Explanator - " + currentFile.getName()
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                this, "could not save the file:\n" + ex.getMessage(),
                "Save Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    //Clears the Editor and resets the File
    private void clearEditor(){
        //Ask Users to confirm before clearing Editor
        int confimration = JOptionPane.showConfirmDialog(
            this, "Are you sure you want to clear the editor?\n" +
                    "Any unsaved changes will be lost.",
                    "Confirm Clear:",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        //Only clears editor if User selects yes
        if (confimration == JOptionPane.YES_OPTION) {
            codeEditor.setText("");
            currentFile = null;

            fileStatus.setText("No file currently loaded");
            setTitle("Recursive Explanator");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RecursionGUI gui = new RecursionGUI();
            gui.setVisible(true);
        });
    }
}
