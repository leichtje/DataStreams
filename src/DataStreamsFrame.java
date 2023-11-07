import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
public class DataStreamsFrame extends JFrame {
    JPanel mainPnl,displayPnl,buttonPnl,searchPnl;
    JTextArea leftArea,rightArea;
    JScrollPane leftPane,rightPane;

    JButton loadBtn,filterBtn,quitBtn;
    JLabel searchLbl;
    JTextField searchTxt;

    private File selectedFile;
    private Path filePath;

    public Set set = new HashSet();

   DataStreamsFrame()
    {
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());

        createDisplayPnl();
        createButtonPnl();
        createSearchPanel();

        mainPnl.add(searchPnl, BorderLayout.NORTH);
        mainPnl.add(displayPnl, BorderLayout.CENTER);
        mainPnl.add(buttonPnl, BorderLayout.SOUTH);

        add(mainPnl);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setSize(5*(screenWidth / 6), 5*(screenHeight / 6));
        setLocationRelativeTo(null);
        //setResizable(false);
        setTitle("Data Stream Search");

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void createSearchPanel()
    {
        searchPnl = new JPanel();
        searchPnl.setLayout(new GridLayout(1,2));

        searchTxt = new JTextField();

        searchLbl = new JLabel("Enter Your Search String: ");
        searchLbl.setFont(new Font("Monospaced", Font.PLAIN, 24));
        searchLbl.setHorizontalAlignment(JLabel.CENTER);

        searchPnl.add(searchLbl);
        searchPnl.add(searchTxt);
    }

    public void createDisplayPnl()
    {
        displayPnl = new JPanel();
        displayPnl.setLayout(new GridLayout(1,2));
        displayPnl.setBorder(new TitledBorder(new EtchedBorder(), ""));

        leftArea = new JTextArea();
        rightArea = new JTextArea();

        leftArea.setEditable(false);
        rightArea.setEditable(false);

        leftArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        rightArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));

        leftPane = new JScrollPane(leftArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPane = new JScrollPane(rightArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        leftArea.setBorder(new TitledBorder("Original File"));
        rightArea.setBorder(new TitledBorder("Filtered File"));

        displayPnl.add(leftPane);
        displayPnl.add(rightPane);
    }

    public void createButtonPnl()
    {
        buttonPnl = new JPanel();
        buttonPnl.setLayout(new GridLayout(1,3));
        buttonPnl.setBorder(new TitledBorder(new EtchedBorder(), ""));

        loadBtn = new JButton("Load");
        filterBtn = new JButton("Filter");
        quitBtn = new JButton("Quit");

        filterBtn.setEnabled(false);
        filterBtn.setBackground(new Color(255, 255, 235));

        loadBtn.setFont(new Font("Monospaced", Font.BOLD, 20));
        filterBtn.setFont(new Font("Monospaced", Font.BOLD, 20));
        quitBtn.setFont(new Font("Monospaced", Font.BOLD, 20));

        loadBtn.addActionListener((ActionEvent e) ->load());
        filterBtn.addActionListener((ActionEvent e) -> filter());
        quitBtn.addActionListener((ActionEvent e) -> {
            int res = JOptionPane.showOptionDialog(null, "Are you sure you want to quit?", "Message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,  new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);
            if(res == JOptionPane.YES_OPTION){
                System.exit(0);
            }
            else if(res == JOptionPane.NO_OPTION){
                JOptionPane.showMessageDialog(null, "Canceled quit request", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
            else if(res == JOptionPane.CLOSED_OPTION){
                JOptionPane.showMessageDialog(null, "Canceled quit request", "Message", JOptionPane.INFORMATION_MESSAGE);
            }});

        buttonPnl.add(loadBtn);
        buttonPnl.add(filterBtn);
        buttonPnl.add(quitBtn);
    }

    public void load()
    {
        JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = chooser.getSelectedFile();
            filePath = selectedFile.toPath();
        }
        filterBtn.setEnabled(true);
        filterBtn.setBackground(null);
        JOptionPane.showMessageDialog(mainPnl, "File Loaded", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void filter() {
        leftArea.setText("");
        rightArea.setText("");
        String wordFilter = searchTxt.getText();
        String rec;
        try (Stream<String> lines = Files.lines(Paths.get(selectedFile.getPath())))
        {
            Set<String> set = lines.filter(w -> w.contains(wordFilter)).collect(Collectors.toSet());
            set.forEach(w -> rightArea.append(w + "\n"));
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found!!!");
            e.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        try
        {
            InputStream in =
                    new BufferedInputStream(Files.newInputStream(filePath, CREATE));
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in));
            int line = 0;
            while(reader.ready())
            {
                rec = reader.readLine();
                leftArea.append(rec + "\n");
                line++;
            }
            reader.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println("File not found!!!");
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
