import javax.swing.*;
import java.awt.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.awt.Color;

public class RecursionGUI extends JFrame{
    //Main GUI window  dimensions
    private static final int WindowWidth = 1200;
    private static final int WindowHeight = 800;

    //GUI components
    private JTextArea codeEditor;
    private JLabel fileStatus;
    private JTextArea consoleOutput;

    //Split Panes
    private JSplitPane workspaceSplitPane;
    private JSplitPane mainSplitPane;

    //Dark Mode Toggle
    private boolean darkMode = false;

    private File currentFile; //tracks the open Java File
    
    public RecursionGUI() {
        //Mian appllication window
        setTitle("Recursion Explanator");
        setSize(WindowWidth, WindowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Main Layout
        setLayout(new BorderLayout(5, 5));

        //Application Title
        JLabel title = new JLabel(
            "Recursion Explanator", SwingConstants.CENTER
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

        //Visualization Workspace placeholder
        JPanel vizPanel = new JPanel(new BorderLayout());

        JLabel vizTitle = new JLabel(
            "Visualization Workspace", SwingConstants.CENTER
        );
        vizTitle.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        JLabel vizPlaceholder = new JLabel(
            "Stack, Tree, and Array visualizations will appear here",
            SwingConstants.CENTER
        );

        vizPanel.add(vizTitle, BorderLayout.NORTH);
        vizPanel.add(vizPlaceholder, BorderLayout.CENTER);

        vizPanel.setBorder(
            BorderFactory.createTitledBorder("Visualizations")
        );

        //Horizontal Split
        workspaceSplitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            editorScrollPane,
            vizPanel
        );

        workspaceSplitPane.setResizeWeight(0.5);
        workspaceSplitPane.setContinuousLayout(true);
        workspaceSplitPane.setOneTouchExpandable(true);
        workspaceSplitPane.setDividerLocation(0.5);

        //Console output placeholder
        consoleOutput = new JTextArea();
        consoleOutput.setFont(
            new Font("Consolas", Font.PLAIN, 13)
        );
        consoleOutput.setEditable(false);
        consoleOutput.setText("Program output will appear here");

        JScrollPane consoleScrollPane = new JScrollPane(consoleOutput);

        consoleScrollPane.setBorder(
            BorderFactory.createTitledBorder("Console Output")
        );

        //Verticle split
        mainSplitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            workspaceSplitPane,
            consoleScrollPane
        );

        mainSplitPane.setResizeWeight(0.8);
        mainSplitPane.setContinuousLayout(true);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setDividerLocation(0.8);

        add(mainSplitPane, BorderLayout.CENTER);

        //File Status Bar
        fileStatus = new JLabel("No file currently loaded");
        fileStatus.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        add(fileStatus, BorderLayout.SOUTH);
    }

    private void runProgram() {
        String sourceCode = codeEditor.getText();

        if (sourceCode.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter or open Java code first.",
                    "No Code",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        consoleOutput.setText("Starting Program...\n");

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

            @Override
            protected Void doInBackground() throws Exception {

                publish("=== Analyzing Recursive Methods ===");

                RecursionExplanator.analyzeCode(
                        sourceCode,
                        line -> publish(line)
                );

                publish("");
                publish("=== Running Your Program ===");

                RunInputProgram.run(
                        sourceCode,
                        line -> publish(line)
                );

                publish("");
                publish("=== Program Finished ===");

                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String line : chunks) {
                    consoleOutput.append(line + "\n");
                }
            }
            //Trying to find where it is having error
            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    consoleOutput.append(
                            "\nERROR: " + e.getCause() + "\n"
                    );
                }
            }
        };
        worker.execute();
    }
    //Toolbar for File Controls
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // Run and recursion control buttons
        JButton runButton = new JButton("Run Program");
        JButton nextStepButton = new JButton("Next Step");
        JButton continueButton = new JButton("Continue");
        JButton themeButton = new JButton("Dark Mode");

        runButton.addActionListener(e -> runProgram());
        nextStepButton.addActionListener(e -> RunInputProgram.sendNextStep());
        continueButton.addActionListener(e -> RunInputProgram.continueAutomatically());

        // Toggle between light and dark mode
        themeButton.addActionListener(e -> {
            darkMode = !darkMode;
            themeButton.setText(darkMode ? "Light Mode" : "Dark Mode");
            applyTheme();
        });

        toolbar.add(runButton);
        toolbar.add(nextStepButton);
        toolbar.add(continueButton);
        toolbar.add(themeButton);

        // Java File access buttons
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

    //Windows-Style Java file chooser
    private JFileChooser createJavaFileChooser(){
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileFilter(
            new FileNameExtensionFilter(
                "Java Source Files (*.java)",
                "java"
            )
        );

        return fileChooser;
    }

    //Opens the file chooser and loads the selected Java file
    private void openJavaFile(){
        JFileChooser fileChooser = createJavaFileChooser();

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

                setTitle(
                    "Crecursion Explanator - " + currentFile.getName()
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this, "Could not open selected file: \n" + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    //Saves the contents of the editor to the current File
    private void saveJavaFile() {
        if (currentFile == null) {
            JFileChooser fileChooser = createJavaFileChooser();

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
                currentFile.toPath(),
                codeEditor.getText()
            );

            fileStatus.setText(
                "Saved: " + currentFile.getName()
            );

            setTitle(
                "Recursion Explanator - " + currentFile.getName()
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Could not save the file: \n" + ex.getMessage(),
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
            
            //Use system's native Swing appearance
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.out.println("Could not load system look and feel");
            }

            SwingUtilities.invokeLater(() -> {
                RecursionGUI gui = new RecursionGUI();

                gui.setVisible(true);

                //Set divider positions after window is displayed
                SwingUtilities.invokeLater(() -> {
                    gui.workspaceSplitPane.setDividerLocation(0.5);
                    gui.mainSplitPane.setDividerLocation(0.8);
                });
            });
        });
    }
    //Logic for dark mode switch

    private void applyTheme() {
        Color background;
        Color foreground;
        Color editorBackground;
        Color consoleBackground;

        if (darkMode) {
            background = new Color(45, 45, 48);
            foreground = new Color(230, 230, 230);
            editorBackground = new Color(30, 30, 30);
            consoleBackground = new Color(25, 25, 25);
        } else {
            background = new Color(238, 238, 238);
            foreground = Color.BLACK;
            editorBackground = Color.WHITE;
            consoleBackground = Color.WHITE;
        }

        // Apply general theme first
        updateComponentTheme(getContentPane(), background, foreground);

        // Apply specific editor and console colors afterward
        codeEditor.setBackground(editorBackground);
        codeEditor.setForeground(foreground);
        codeEditor.setCaretColor(foreground);

        consoleOutput.setBackground(consoleBackground);
        consoleOutput.setForeground(foreground);
        consoleOutput.setCaretColor(foreground);

        if (fileStatus != null) {
            fileStatus.setForeground(foreground);
        }

        revalidate();
        repaint();
    }

    private void updateComponentTheme(java.awt.Container container, Color background, Color foreground) {
        for (java.awt.Component component : container.getComponents()) {
            component.setBackground(background);
            component.setForeground(foreground);

            if (component instanceof JButton) {
                ((JButton) component).setForeground(foreground);
                ((JButton) component).setOpaque(true);
            }

            if (component instanceof java.awt.Container) {
                updateComponentTheme((java.awt.Container) component, background, foreground);
            }
        }
    }
}
