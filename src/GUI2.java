import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class GUI2 extends JFrame {
    public JPanel panel2;
    private JButton selectTargetButton;
    private JTextField targetDirField;
    private JButton exportButton;
    private JList<String> typeList;
    private JList<String> yearList;
    private File targetDir;

    GUI2() {
        targetDirField.setEditable(false);
        typeList.setListData(findTypes(GUI.foundedFiles));
        yearList.setListData(findYears(GUI.foundedFiles));
        typeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        yearList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        exportButton.setEnabled(false);
        repaint();
        revalidate();
        pack();

        selectTargetButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
            fc.setCurrentDirectory(new File(System.getProperty("user.home")));
            int returnVal = fc.showOpenDialog(panel2);
            targetDir = fc.getSelectedFile();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File dirNotNull = new File(targetDir.getAbsolutePath());
                if (dirNotNull.exists()) {
                    targetDirField.setText(targetDir.getAbsolutePath());
                    exportButton.setEnabled(true);
                } else {
                    targetDirField.setText("KATALOG NIE ISTNIEJE!");
                    exportButton.setEnabled(false);
                }
                revalidate();
                repaint();
                pack();
            } else {
                System.out.println("Open command cancelled by user.");
            }
        });

        exportButton.addActionListener(e -> {
            System.out.println(typeList.getSelectedValuesList());
            System.out.println(yearList.getSelectedValuesList());
            List<File> filteredFiles = GUI.foundedFiles.stream().filter(file -> containsAny(file.getName().split("_F_")[0], typeList.getSelectedValuesList()) && containsAny(file.getName().split("_F_")[1].split("_S")[0], yearList.getSelectedValuesList())).collect(Collectors.toList());
            copyFiles(filteredFiles, targetDir.getAbsolutePath());
            JOptionPane.showMessageDialog(null, "Pomyślnie wyeksportowano pliki w liczbie: " + filteredFiles.size(), "Sukces", JOptionPane.INFORMATION_MESSAGE);
        });

    }

    private Vector<String> findTypes(List<File> files) {
        Vector<String> result = new Vector<>();
        files.forEach(file -> {
            String name = file.getName().split("_")[0];
            if (!result.contains(name)) {
                result.add(name);
            }

        });
        Collections.sort(result);
        return result;
    }

    private Vector<String> findYears(List<File> files) {
        Vector<String> result = new Vector<>();
        files.forEach(file -> {
            String[] tmp = file.getName().split("_S")[0].split("_");
            String name = tmp[tmp.length-1];
            if (!result.contains(name)) {
                result.add(name);
            }

        });
        Comparator<String> reverseComparator = Comparator.reverseOrder();
        result.sort(reverseComparator);
        return result;
    }

    private void copyFiles(List<File> files, String targetDir) {
        files.forEach(file -> {
            try {
                String outPath = targetDir + "\\" + file.getName() + "." + GUI.getExtension(file);
                Files.copy(file.toPath(), Paths.get(outPath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean containsAny(String inputString, List<String> elements) {
        for (String element : elements) {
            if (inputString.contains(element)) {
                return true;
            }
        }
        return false;
    }
}
