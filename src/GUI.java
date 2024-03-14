import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GUI extends JFrame {
    public static List<File> foundedFiles = new ArrayList<>();
    private JPanel mainPanel;
    private JTextField startDirField;
    private JButton selectStartButton;
    private JButton nextButton;
    private File startDir;

    GUI() {
        setLocationRelativeTo(null);
        setTitle("FakturoSzukator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(800, 600);
        repaint();
        revalidate();
        pack();
        setVisible(true);
        startDirField.setEditable(false);
        nextButton.setEnabled(false);

        selectStartButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
            fc.setCurrentDirectory(new File(System.getProperty("user.home")));
            int returnVal = fc.showOpenDialog(mainPanel);
            startDir = fc.getSelectedFile();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File dirNotNull = new File(startDir.getAbsolutePath());
                if (dirNotNull.exists()) {
                    startDirField.setText(startDir.getAbsolutePath());
                    nextButton.setEnabled(true);
                } else {
                    startDirField.setText("KATALOG NIE ISTNIEJE!");
                    nextButton.setEnabled(false);
                }
                revalidate();
                repaint();
                pack();
            } else {
                System.out.println("Open command cancelled by user.");
            }
        });

        nextButton.addActionListener(e -> {
            File dirNotNull = new File(startDir.getAbsolutePath());
            if (dirNotNull.exists()) {
                System.out.println(dirNotNull.getAbsolutePath());
                System.out.println(dirNotNull.getPath());
                lookupThisDir(dirNotNull.getPath(), 1);
                setContentPane(new GUI2().panel2);
                revalidate();
                repaint();
                pack();
            }
        });
    }

    public static String getExtension(File file) {
        return file.getAbsolutePath().split("\\.")[file.getAbsolutePath().split("\\.").length - 1];
    }

    void lookupThisDir(String dir, int level) {
        File lookupDir = new File(dir);

        //Przeglądanie plików w TYM katalogu
        Stream.of(Objects.requireNonNull(new File(dir).listFiles())).filter(file -> !file.isDirectory() && getExtension(file).equalsIgnoreCase("pdf") && file.getName().matches("(^.*_F_.*_SZ_.*)|(^.*_F_.*_S_.*)")).forEach(file -> foundedFiles.add(file));

        //Wchodzenie głębiej w katalogi
        List<String> list = Stream.of(Objects.requireNonNull(lookupDir.listFiles())).filter(File::isDirectory).map(File::getName).collect(Collectors.toList());
        list.forEach(el -> {
            for (int i = 0; i < level; i++) {
                System.out.print("-");
            }
            System.out.println(el);

            String path = dir + "\\" + el;
            lookupThisDir(path, level + 1);
        });
    }

}

