package mypackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ImageIcon;

/**
 * @author Himanshu
 * 
 * Conventions Used in Source code
   ---------------------------------
    1. All JLabel components start with jlb*
    2. All JPanel components start with jpl*
    3. All JMenu components start with jmenu*
    4. All JMenuItem components start with jmenuItem*
    5. All JDialog components start with jdlg*
    6. All JButton components start with jbn*
 */
public class Calculator extends JFrame implements ActionListener {
    final int MAX_INPUT_LENGTH = 12;
    final int INPUT_MODE = 0;
    final int RESULT_MODE = 1;
    final int ERROR_MODE = 2;
   
    private int displayMode;
    private boolean clearOnNextDigit;
    private double lastNumber;
    private String lastOperator;

    private JMenu jmenuFile, jmenuHelp;
    private JMenuItem jmenuitemExit, jmenuitemAbout;

    private JLabel jlbOutput;
    private JLabel jlbIcon;
    private JButton[] jbnButtons;
    private ImageIcon imgIcon;
    private JPanel jplIcon;
    private JPanel jplMaster;
    private JPanel jplBackSpace;
    private JPanel jplControl;
    private JMenuBar mb;
    private JPanel jplButtons;
    
    private Font f12;
    private Font f121;
	
    public Calculator() {
        initializeFonts();
        
        setBackground(Color.gray);
        
        initializeMenu();
        initializeComponents();
    }

    public void actionPerformed(ActionEvent e) {
        double result = 0;

        if (e.getSource() == jmenuitemAbout) {
            JDialog dlgAbout = new CustomAboutDialog(this, "About Java Swing Calculator", true);
            dlgAbout.setVisible(true);
        } else if(e.getSource() == jmenuitemExit) {
            System.exit(0);
        }	

        // Search for the button pressed until end of array or key found
        for (int i = 0; i < jbnButtons.length; i++) {
            if(e.getSource() == jbnButtons[i]) {
                switch(i) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9: 
                        addDigitToDisplay(i);
                        break;

                    case 10: 
                        processSignChange();
                        break;

                    case 11: 
                        addDecimalPoint();
                        break;

                    case 12: 
                        processEquals();
                        break;

                    case 13: 
                        processOperator("/");
                        break;

                    case 14: 
                        processOperator("*");
                        break;

                    case 15: 
                        processOperator("-");
                        break;

                    case 16: 
                        processOperator("+");
                             break;

                    case 17: 
                        if (displayMode != ERROR_MODE) {
                            try {
                                if (getDisplayString().indexOf("-") == 0) {
                                    displayError("Invalid input for function!");
                                }
                                    
                                result = Math.sqrt(getNumberInDisplay());
                                displayResult(result);
                            } catch(Exception ex) {
                                displayError("Invalid input for function!");
                                displayMode = ERROR_MODE;
                            }
                        }
                        break;

                    case 18: 
                        if (displayMode != ERROR_MODE) {
                            try {
                                if (getNumberInDisplay() == 0) {
                                    displayError("Cannot divide by zero!");
                                }

                                result = 1 / getNumberInDisplay();
                                displayResult(result);
                            } catch(Exception ex) {
                                displayError("Cannot divide by zero!");
                                displayMode = ERROR_MODE;
                            }
                        }
                        break;

                    case 19: 
                        if (displayMode != ERROR_MODE) {
                            try {
                                result = getNumberInDisplay() / 100;
                                displayResult(result);
                            } catch(Exception ex) {
                                displayError("Invalid input for function!");
                                displayMode = ERROR_MODE;
                            }
                        }
                        break;

                    case 20: 
                        if (displayMode != ERROR_MODE) {
                            setDisplayString(getDisplayString().substring(0,
                            getDisplayString().length() - 1));

                            if (getDisplayString().length() < 1) {
                                setDisplayString("0");
                            }
                        }
                        break;

                    case 21:
                        // CE
                        clearExisting();
                        break;

                    case 22:	
                        // C
                        clearAll();
                        break;
                }
            }
        }
    }

    private ImageIcon resizeImage(ImageIcon imgIcon) {
        Image img = imgIcon.getImage();
        Image newimg = img.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH);
        imgIcon= new ImageIcon(newimg);
        return imgIcon;
    }

    void setDisplayString(String s) {
        jlbOutput.setText(s);
    }

    private String getDisplayString() {
        return jlbOutput.getText();
    }

    private void addDigitToDisplay(int digit) {
        if (clearOnNextDigit) {
            setDisplayString("");
        }

        String inputString = getDisplayString();

        if (inputString.indexOf("0") == 0) {
            inputString = inputString.substring(1);
        }

        if ((!inputString.equals("0") || digit > 0)  && inputString.length() < MAX_INPUT_LENGTH) {
            setDisplayString(inputString + digit);
        }

        displayMode = INPUT_MODE;
        clearOnNextDigit = false;
    }

    private void addDecimalPoint(){
        displayMode = INPUT_MODE;

        if (clearOnNextDigit) {
            setDisplayString("");
        }

        String inputString = getDisplayString();

        // If the input string already contains a decimal point, don't do anything to it.
        if (!inputString.contains(".")) {
            setDisplayString(new String(inputString + "."));
        }
    }

    private void processSignChange(){
        if (displayMode == INPUT_MODE) {
            String input = getDisplayString();

            if (input.length() > 0 && !input.equals("0")) {
                if (input.indexOf("-") == 0) {
                    setDisplayString(input.substring(1));
                } else {
                    setDisplayString("-" + input);
                }
            }
        } else if (displayMode == RESULT_MODE) {
            double numberInDisplay = getNumberInDisplay();

            if (numberInDisplay != 0) {
                displayResult(-numberInDisplay);
            }
        }
    }

    private void clearAll() {
        setDisplayString("0");
        lastOperator = "0";
        lastNumber = 0;
        displayMode = INPUT_MODE;
        clearOnNextDigit = true;
    }

    void clearExisting() {
        setDisplayString("0");
        clearOnNextDigit = true;
        displayMode = INPUT_MODE;
    }

    private double getNumberInDisplay()	{
        String input = jlbOutput.getText();
        return Double.parseDouble(input);
    }

    private void processOperator(String op) {
        if (displayMode != ERROR_MODE) {
            double numberInDisplay = getNumberInDisplay();

            if (!lastOperator.equals("0")) {
                try {
                    double result = processLastOperator();
                    displayResult(result);
                    lastNumber = result;
                } catch (DivideByZeroException e) {}
            } else {
                lastNumber = numberInDisplay;
            }

            clearOnNextDigit = true;
            lastOperator = op;
        }
    }

    private void processEquals() {
        double result = 0;

        if (displayMode != ERROR_MODE) {
            try {
                result = processLastOperator();
                displayResult(result);
            } catch (DivideByZeroException e) {
                displayError("Cannot divide by zero!");
            }

            lastOperator = "0";
        }
    }

    private double processLastOperator() throws DivideByZeroException {
        double result = 0;
        double numberInDisplay = getNumberInDisplay();

        if (lastOperator.equals("/")) {
            if (numberInDisplay == 0) {
                throw (new DivideByZeroException());
            }

            result = lastNumber / numberInDisplay;
        }

        if (lastOperator.equals("*")) {
            result = lastNumber * numberInDisplay;
        }

        if (lastOperator.equals("-")) {
            result = lastNumber - numberInDisplay;
        }

        if (lastOperator.equals("+")) {
            result = lastNumber + numberInDisplay;
        }

        return result;
    }

    private void displayResult(double result){
        setDisplayString(Double.toString(result));
        lastNumber = result;
        displayMode = RESULT_MODE;
        clearOnNextDigit = true;
    }

    private void displayError(String errorMessage){
        setDisplayString(errorMessage);
        lastNumber = 0;
        displayMode = ERROR_MODE;
        clearOnNextDigit = true;
    }

    public static void main(String args[]) {
        Calculator calci = new Calculator();
        
        calci.setTitle("Java Swing Calculator");
        calci.setSize(241, 217);
        calci.pack();
        calci.setLocation(400, 250);
        calci.setVisible(true);
        calci.setResizable(false);
    }
    
    private void initializeFonts() {
        f12 = new Font("Times New Roman", 0, 12);
        f121 = new Font("Times New Roman", 1, 12);
    }

    private void initializeMenu() {
        jmenuFile = new JMenu("File");
        jmenuFile.setFont(f121);
        jmenuFile.setMnemonic(KeyEvent.VK_F);

        jmenuitemExit = new JMenuItem("Exit");
        jmenuitemExit.setFont(f12);
        jmenuitemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        jmenuFile.add(jmenuitemExit);

        jmenuHelp = new JMenu("Help");
        jmenuHelp.setFont(f121);
        jmenuHelp.setMnemonic(KeyEvent.VK_H);

        jmenuitemAbout = new JMenuItem("About");
        jmenuitemAbout.setFont(f12);
        jmenuitemAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        jmenuHelp.add(jmenuitemAbout);

        mb = new JMenuBar();
        mb.add(jmenuFile);
        mb.add(jmenuHelp);
        setJMenuBar(mb);
    }
    
    private void initializeComponents() {
        jplMaster = new JPanel();
        
        initializeImageIcon();
        initializeButtons();
    }
    
    private void initializeImageIcon() {
        jplIcon = new JPanel();
        
        imgIcon = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\icon.png");
        jlbIcon = new JLabel(resizeImage(imgIcon));
        jlbIcon.setPreferredSize(new Dimension(50, 50));
        jlbIcon.setBackground(Color.RED);
    }

    private void initializeButtons() {
        jlbOutput = new JLabel("0");
        jlbOutput.setPreferredSize(new Dimension(200, 45));
        jlbOutput.setFont(new Font("Serif", Font.BOLD, 25));

        jlbOutput.setBackground(Color.WHITE);
        jlbOutput.setOpaque(true);

        jplIcon.add(jlbIcon, BorderLayout.WEST);
        jplIcon.add(jlbOutput, BorderLayout.EAST);
        getContentPane().add(jplIcon, BorderLayout.NORTH);
        jbnButtons = new JButton[23];

        jplButtons = new JPanel();		

        for (int i = 0; i <= 9; i++) {
            jbnButtons[i] = new JButton(String.valueOf(i));
            jbnButtons[i].setPreferredSize(new Dimension(50, 50));
        }

        jbnButtons[10] = new JButton("+/-");
        jbnButtons[11] = new JButton(".");
        jbnButtons[12] = new JButton("=");
        jbnButtons[13] = new JButton("/");
        jbnButtons[14] = new JButton("*");
        jbnButtons[15] = new JButton("-");
        jbnButtons[16] = new JButton("+");
        jbnButtons[17] = new JButton("sqrt");
        jbnButtons[18] = new JButton("1/x");
        jbnButtons[19] = new JButton("%");

        jplBackSpace = new JPanel();

        jbnButtons[20] = new JButton("Backspace");
        jplBackSpace.add(jbnButtons[20]);

        jplControl = new JPanel();

        jbnButtons[21] = new JButton("CE");
        jbnButtons[22] = new JButton("C");

        jplControl.add(jbnButtons[21]);
        jplControl.add(jbnButtons[22]);

        for (int i = 0; i < jbnButtons.length; i++) {
            jbnButtons[i].setFont(f12);
            jbnButtons[i].setForeground(Color.WHITE);
            jbnButtons[i].setFocusPainted(false);
            jbnButtons[i].addActionListener(this);

            if (i < 20) {
                jbnButtons[i].setBackground(Color.BLACK);
            } else {
                jbnButtons[i].setBackground(new Color(154,205,50));
            }
        }

        jplButtons.setLayout(new GridLayout(4, 5, 2, 2));

        //Add buttons to keypad panel starting at top left
        // First row
        for(int i = 7; i <= 9; i++) {
            jplButtons.add(jbnButtons[i]);
        }

        // add button / and sqrt
        jplButtons.add(jbnButtons[13]);
        jplButtons.add(jbnButtons[17]);

        // Second row
        for(int i = 4; i <= 6; i++) {
            jplButtons.add(jbnButtons[i]);
        }

        // add button * and x^2
        jplButtons.add(jbnButtons[14]);
        jplButtons.add(jbnButtons[18]);

        // Third row
        for(int i = 1; i <= 3; i++) {
            jplButtons.add(jbnButtons[i]);
        }

        //adds button - and %
        jplButtons.add(jbnButtons[15]);
        jplButtons.add(jbnButtons[19]);

        //Fourth Row
        // add +/-, 0, ., +, and =
        jplButtons.add(jbnButtons[10]);
        jplButtons.add(jbnButtons[0]);
        jplButtons.add(jbnButtons[11]);
        jplButtons.add(jbnButtons[16]);
        jplButtons.add(jbnButtons[12]);

        jplMaster.setLayout(new BorderLayout());
        jplMaster.add(jplBackSpace, BorderLayout.WEST);
        jplMaster.add(jplControl, BorderLayout.EAST);
        jplMaster.add(jplButtons, BorderLayout.SOUTH);

        getContentPane().add(jplMaster, BorderLayout.SOUTH);
        requestFocus();

        jmenuitemAbout.addActionListener(this);
        jmenuitemExit.addActionListener(this);

        clearAll();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class DivideByZeroException extends Exception {
    public DivideByZeroException() {
        super();
    }

    public DivideByZeroException(String s) {
        super(s);
    }
}

class CustomAboutDialog extends JDialog implements ActionListener {
    JButton jbnOk;

    CustomAboutDialog(JFrame parent, String title, boolean modal){
        super(parent, title, modal);

        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));

        StringBuffer text = new StringBuffer();
        text.append("Calculator Information\n\n");
        text.append("Developer:	HDMR Pvt. Ltd.\n");
        text.append("Version: 1.0");

        JTextArea jtAreaAbout = new JTextArea(5, 21);
        jtAreaAbout.setText(text.toString());
        jtAreaAbout.setFont(new Font("Times New Roman", 1, 13));
        jtAreaAbout.setEditable(false);

        p1.add(jtAreaAbout);
        p1.setBackground(Color.red);
        getContentPane().add(p1, BorderLayout.CENTER);

        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jbnOk = new JButton("OK");
        jbnOk.addActionListener(this);

        p2.add(jbnOk);
        getContentPane().add(p2, BorderLayout.SOUTH);

        setLocation(408, 270);
        setResizable(false);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbnOk) {
            this.dispose();
        }
    } 
}