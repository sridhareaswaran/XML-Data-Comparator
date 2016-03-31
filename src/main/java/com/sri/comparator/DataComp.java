package com.sri.comparator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/*
@author - Sridhar Easwaran
email - sridhar.1788@gmail.com
*/

public class DataComp extends JPanel implements ActionListener {
    static private final String newline = "\n";
    JButton openButton, saveButton;
    JTextArea log;
    JFileChooser fc;
    String Summary;
    String jcb="                        ___\n" +
            "                     /======/\n" +
            "            ____    //      \\___       ,/\n" +
            "             | \\\\  //           :,   ./\n" +
            "     |_______|__|_//            ;:; /\n" +
            "    _L_____________\\o           ;;;/\n" +
            "____(CCCCCCCCCCCCCC)____________-/_____________________";


    static String err_Summary="some error occured";
    Boolean processing=false;

    public DataComp() {
        super(new BorderLayout());

        //Create the log first, because the action listeners
        //need to refer to it.
        log = new JTextArea(40,50);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        //Create a file chooser
        fc = new JFileChooser();
       // checkExcelData cd = new checkExcelData();

        //fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        //Create the open button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        openButton = new JButton("Select Master File...");
        openButton.addActionListener(this);

        //Create the save button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
       // saveButton = new JButton("Save a File...");
       // saveButton.addActionListener(this);

        //For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); //use FlowLayout
        buttonPanel.add(openButton);
        //buttonPanel.add(saveButton);

        //Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }

    public void updatetxt()
    {
        log.setCaretPosition(log.getDocument().getLength());
        log.update(log.getGraphics());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(DataComp.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                File path = fc.getCurrentDirectory();
                log.append(newline+ "-> Processing : " + file.getName() + newline);
                updatetxt();
                String masterdir=path.getAbsolutePath();
                log.append(newline + "-> Verifying presence of 'Controller' & 'Receiver' folder in " + masterdir );
                updatetxt();
                File cdir = new File(masterdir+"/controller");
                File rdir = new File(masterdir+"/receiver");
                if (cdir.exists() && cdir.isDirectory() && rdir.exists() && rdir.isDirectory())
                {
                    //code to check the contents
                    processing=true;
                    String file_name=file.getAbsolutePath();
                    log.append(newline + "-> Everything looks good.. Sit back & relax, while we analyze ur XMLs ..." + newline + newline);
                    updatetxt();
                    for(int j=200;j<0;j--)
                    {
                        log.append(".");
                        updatetxt();
                    }

                    //call the compare function
                    checkExcelData cd=new checkExcelData(file_name,masterdir);
                    Summary=cd.getSummary();
                    log.append(newline + Summary + newline);
                    updatetxt();

                }
                else
                {
                    log.append(newline + "** Either of the 'Controller' or 'Receiver' folder is missing **" + newline);
                    updatetxt();
                }


            }
            else
            {
                log.append(newline + "** Open command cancelled by user **" + newline);
                updatetxt();
            }

                updatetxt();
        }

        /*else if (e.getSource() == saveButton) {
            int returnVal = fc.showSaveDialog(DataComp.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would save the file.
                log.append("Saving: " + file.getName() + "." + newline);
            } else {
                log.append("Save command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());
        }*/
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = DataComp.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Middleware Data Comparator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*java.net.URL imgpath = frame.getClass().getResource("/res/compare.png");
        ImageIcon img = new ImageIcon(imgpath);
        frame.setIconImage(img.getImage());*/
        //Add content to the window.
        frame.add(new DataComp());


        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }




}