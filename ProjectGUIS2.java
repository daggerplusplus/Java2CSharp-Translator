import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.LayoutManager;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.LayoutManager;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;


public class ProjectGUIS2 extends JFrame {
    static List<StmtS2> statements = new ArrayList<>();
    protected static String source;

    ProjectGUIS2() {      
    }

    static String path;    

    ProjectGUIS2(String path) {
      this.path = path;
    }
    static String getInputPath() {
      return path; 
    }

    static void setInputPath(String path2) {
      path = path2;
    }

    void fillList(List<StmtS2> stmts) {
      this.statements = stmts;
    }
    static List<StmtS2> getList() {
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
      frame.setSize(1400, 900); 
  
      }
      


      private static void createGUI(final JFrame frame) { 
        JPanel mainPanel = new JPanel();
        LayoutManager layout = new FlowLayout();
        mainPanel.setLayout(layout);

        JLabel originalTitle = new JLabel("Java"); 
        JLabel translatedFile = new JLabel("C#");       

        JButton importButton = new JButton("Import Java File"); 
        JButton saveButton = new JButton("Save C# File"); 
        JButton translateButton = new JButton("Translate");       
        
        JTextArea originalFile = new JTextArea(50, 40); 
        originalFile.setLineWrap(true);
        JScrollPane scrollOriginal = new JScrollPane(originalFile);
        scrollOriginal.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        scrollOriginal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
        
        mainPanel.add(scrollOriginal);
       
 

        originalFile.setEditable(false);
        frame.add (mainPanel);
        frame.pack ();
        frame.setLocationRelativeTo(null);
        frame.setVisible ( true );
        

        JTextArea convertedFile = new JTextArea(50,40);
        convertedFile.setLineWrap(true);
        JScrollPane scrollConverted = new JScrollPane(convertedFile);
        scrollConverted.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        scrollConverted.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );

        mainPanel.add(scrollConverted);

        saveButton.setLayout(new FlowLayout(FlowLayout.LEFT));
        translateButton.setLayout(new FlowLayout(FlowLayout.CENTER));
        importButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
    
        frame.getContentPane().add(saveButton,BorderLayout.SOUTH);
        frame.getContentPane().add(translateButton,BorderLayout.SOUTH);
        
        mainPanel.add(originalTitle); 
        mainPanel.add(scrollOriginal);
        mainPanel.add(scrollConverted);
        mainPanel.add(translatedFile); 
        mainPanel.add(importButton); 
        mainPanel.add(saveButton);
        mainPanel.add(translateButton);      

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
                  System.out.println("Errrrrrr");
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
                originalFile.append("Error reading file");
              }
            
              } //end if
          };  
        });   //end ActionListener
        translateButton.addActionListener(new ActionListener() {
          
          public void actionPerformed(ActionEvent e) {

            Main.run2(); 
            
            String line = "";            
             try{
              FileWriter fw = new FileWriter("output.txt");
              fw.write("using System;\n");
              fw.write("public class Program\n");
              fw.write("{\n");

              StringBuilder bodybuilder = new StringBuilder();
              for (StmtS2 statement: statements) {
                bodybuilder.append(new Translator().print(statement));  
                }
              // for (StmtS2 statement: statements) {
              //   fw.write(new Translator().print(statement));  
              //   }
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
              //convertedFile.append();  //append output
              reader.close();
            } catch (IOException E) {
              originalFile.setText("");
              originalFile.append("Error translating file");
            }            
          }

        });
        
    saveButton.addActionListener(new ActionListener()  {

         public void actionPerformed(ActionEvent event)  {

         var source = new File("output.txt");
         var dest = new File("Program.cs");

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
