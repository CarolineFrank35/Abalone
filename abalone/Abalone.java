package abalone;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;

/**
 * Controller, Main class
 *
 */
public class Abalone {
    JFrame frame;
    AbaloneView view;
    private JMenuBar jMenuBar;
    
    private JMenu FileMenu;
    private JMenuItem New, Exit;
    
    private JMenu HelpMenu;
    private JMenuItem Help;

    private JMenu AboutMenu;
    private JMenuItem About;
    
    public Abalone() {
        initUI();
    }
    
    private void initUI() {
        frame = new JFrame("Abalone");
        frame.setSize(650,650);
        frame.setLayout(new BorderLayout());
        // Game View added here
        view = new AbaloneView();
        frame.add(view, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //************************** MENU ******************************************
        jMenuBar = new JMenuBar();
        
        //Menu
        FileMenu = new JMenu("File");
        //Menu Item
        New = new JMenuItem("New");
        Exit = new JMenuItem("Exit");
        //ActionListener
        //New.addActionListener(listener);
        //Exit.addActionListener(listener);
        //Add Buttons to Menu
        FileMenu.add(New);
        FileMenu.add(Exit);
        
        //Menu
        HelpMenu = new JMenu("Help");
        //Menu Item
        Help = new JMenuItem("Help");
        //ActionListener
        //Help.addActionListener(listener);
        //Add Buttons to Menu
        HelpMenu.add(Help);

        //Menu
        AboutMenu = new JMenu("About");
        //Menu Item
        About = new JMenuItem("About");
        //ActionListener
        //About.addActionListener(listener);
        //Add Buttons to Menu
        AboutMenu.add(About);
        
        jMenuBar.add(FileMenu);
        jMenuBar.add(HelpMenu);
        jMenuBar.add(AboutMenu);
        frame.setJMenuBar(jMenuBar);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Abalone();
            }
        });
    }

}
