import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.LayoutManager;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
public class ProjectGUI extends JFrame {
    static List<Stmt> statements = new ArrayList<>();
    protected static String source;

    ProjectGUI() {      
    }

    static String path;    

    ProjectGUI(String path) {
      this.path = path;
    }
    static String getInputPath() {
      return path; 
    }

    static void setInputPath(String path2) {
      path = path2;
    }

    void fillList(List<Stmt> stmts) {
      this.statements = stmts;
    }
    static List<Stmt> getList() {
      return statements;
    }

   
     public static void createWindow() {
      
      try {
      File guiFile = new File("output.txt");      
      if (guiFile.createNewFile())
        System.out.println("File created");
      else
        System.out.println("File already exists");

      } catch (Exception e) {
        System.err.println(e);
      }

      JFrame frame = new JFrame("Java to C# Translator");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
      createGUI(frame);
      frame.setSize(1000, 850);   
      }
      


      private static void createGUI(final JFrame frame) { 
        JPanel mainPanel = new JPanel();
        LayoutManager layout = new FlowLayout();
        mainPanel.setLayout(layout);

        JLabel originalTitle = new JLabel("Java"); 
        JLabel translatedFile = new JLabel("C#");       

        JButton importButton = new JButton("Import Java File"); 
        JButton translateButton = new JButton("Translate");  
        JButton saveButton = new JButton("Save C# File"); 
        JButton clipButton = new JButton("Copy C# to Clipboard"); 

        
        JTextArea originalFile = new JTextArea(50, 40); 
        originalFile.append("Import the Java code and it will appear here");  
        //originalFile.setLineWrap(true);
        originalFile.setEditable(false);
        
        JScrollPane scrollOriginal = new JScrollPane(originalFile);
        scrollOriginal.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        scrollOriginal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
           
       
 

        
      
        
        FlowLayout text = new FlowLayout();
        mainPanel.setLayout(text);
        JPanel controls = new JPanel();
        JPanel labels = new JPanel();
        labels.setLayout (new FlowLayout(FlowLayout.LEFT));
        controls.setLayout (new FlowLayout(FlowLayout.LEFT));
        JTextArea convertedFile = new JTextArea(50,40);
        convertedFile.append("This is where the C# code will appear");
        convertedFile.setLineWrap(true);
        JScrollPane scrollConverted = new JScrollPane(convertedFile);
        scrollConverted.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        scrollConverted.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
        mainPanel.add(scrollConverted);

     
        JPanel p = new JPanel();
        p.setLayout( new BorderLayout()); 
        controls.add(Box.createRigidArea(new Dimension(150, 25)));        
        controls.add(importButton);
        controls.add(translateButton);
        controls.add(Box.createRigidArea(new Dimension(150, 25)));        
        controls.add(saveButton);
        controls.add(clipButton);        
        mainPanel.add(scrollOriginal);
        mainPanel.add(scrollConverted);              
        p.add(controls,BorderLayout.SOUTH);
        p.add(mainPanel,BorderLayout.CENTER);
        frame.add(p);
        frame.pack ();
        frame.setLocationRelativeTo(null);
        frame.setVisible ( true );
               
     
      
        final JLabel label = new JLabel();

        //Button to import Java file and load into text area
        importButton.addActionListener(new ActionListener() {      
          @Override
          public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();      
            BufferedReader reader = null;  
            int option = fileChooser.showOpenDialog(frame);
            
            if (option == JFileChooser.APPROVE_OPTION)
              { 
                File file = fileChooser.getSelectedFile();
                Path path = file.toPath();
                //String fileName = file.getName();        
                try {
                  byte[] bytes = Files.readAllBytes(path);
                  source = new String(bytes,Charset.defaultCharset());
                } catch (IOException msg) {
                  System.out.println("Error importing file");
                }               
               setInputPath(source);
                try {  //fill in text area with file's contents
                  FileReader fr = new FileReader(file);
                  reader = new BufferedReader(fr);
                  originalFile.setText("");
                  String line = reader.readLine();
                  while (line != null) {
                  originalFile.append(line + "\n");
                  line = reader.readLine();
                  } //end while
                } catch (IOException e2)
              {
                originalFile.setText("");
                originalFile.append("Error filling original code text area");
              }
            
              } //end if
          };  
        });   //end ActionListener
        translateButton.addActionListener(new ActionListener() {
          
          public void actionPerformed(ActionEvent e) {

            Main.run2();            
                       
             try{
              FileWriter fw = new FileWriter("output.txt");
              fw.write("using System;\n");
              fw.write("public class Program\n");
              fw.write("{\n");

              StringBuilder bodybuilder = new StringBuilder();
              for (Stmt statement: statements) {
                bodybuilder.append(new Translator().print(statement));  
                }             
              fw.write(bodybuilder.toString().replaceAll("(?m)^", "    "));
              fw.write("}");
              
              fw.close();
            } catch (IOException e2) {
              convertedFile.setText("");              
              convertedFile.append("Error writing to output.txt");
            } 
            
            try {               
              FileReader fr = new FileReader("output.txt");
              BufferedReader reader = new BufferedReader(fr);
              convertedFile.setText("");
              String line2 = reader.readLine();
              while (line2 != null) {
                convertedFile.append(line2 + "\n");
                line2 = reader.readLine();
              }
              
              reader.close();
            } catch (IOException E) {
              originalFile.setText("");
              originalFile.append("Error translating file");
            }            
          }

        });
        clipButton.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent event)  {
String str = convertedFile.getText();

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(str);
		clipboard.setContents(strSel, null);
}
        });
    saveButton.addActionListener(new ActionListener()  {

         public void actionPerformed(ActionEvent event)  {
JFileChooser fileChooser = new JFileChooser();
FileNameExtensionFilter filter = new FileNameExtensionFilter("C# FILES", "cs");

fileChooser.setDialogTitle("Specify a file to save");   
File fileToSave = null;
int userSelection = fileChooser.showSaveDialog(frame);
 
if (userSelection == JFileChooser.APPROVE_OPTION) {
    fileToSave = fileChooser.getSelectedFile();
    System.out.println("Save as file: " + fileToSave.getAbsolutePath() + ".cs");
}
         var source = new File("output.txt");
         String dest =  fileToSave.getAbsolutePath() + ".cs";

         try (var fis = new FileInputStream(source);
             var fos = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = fis.read(buffer)) > 0) {

                fos.write(buffer, 0, length);
            }
            
         }catch (FileNotFoundException ex) {
          System.out.println("");

         }catch (IOException e) {
          System.out.println("");
        }
       

      }
      
    });  

  } 
} //end createGUI
