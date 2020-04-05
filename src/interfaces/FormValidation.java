package interfaces;

import java.awt.Component;
import javax.swing.ButtonGroup;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Oussama
 */


public interface FormValidation {
    
    /**
     * 
     * @return Return true if all fields are ok, false otherwise
     */
    public boolean validateFields();
    
    /**
     * 
     * Check if a JTextField not empty. The body if this method must call 
     * showErrorDialog() and/or showErrorMessage() methods like example bellow :
     * 
     *  if ( f.getText().equals("") )
          return showErrorDialog( f, errormsg );
        else
          return true; // validation successful
     * 
     * @param field The name if JTextField
     * @param errorMsg The error message to be displayed
     */
    public void validateJTextField(JTextField field, String errorMsg);
    
    /**
     * 
     * Check if a JTextArea not empty. The body if this method must call 
     * showErrorDialog() and/or showErrorMessage() methods like example bellow :
     * 
     *  if ( f.getText().equals("") )
          return showErrorDialog( f, errormsg );
        else
          return true; // validation successful
     * 
     * @param field The name if JTextArea
     * @param errorMsg The error message to be displayed
     */
    public void validateJTextArea(JTextArea field, String errorMsg);
    
    /**
     * Check if the JTextField is not empty and contains a numeric value
     . The body if this method must call 
     * showErrorDialog() and/or showErrorMessage() methods like example bellow :
     * 
     *  if ( f.getText().equals("") )
          return showErrorDialog( f, errormsg );
        else
          return true; // validation successful
     * 
     * @param field The name if JTextArea
     * @param errorMsg The error message to be displayed
     */
    public void validateNumericJTextField(JTextField field, String errorMsg);
    
    /*
     * Check if the JTextField is not empty and contains a numeric value
     . The body if this method must call 
     * showErrorDialog() and/or showErrorMessage() methods like example bellow :
     * 
     *  if ( f.getText().equals("") )
          return showErrorDialog( f, errormsg );
        else
          return true; // validation successful
     * 
     * @param field The name if JTextArea
     * @param errorMsg The error message to be displayed
     */

    public void validateIntegerJTextField(JTextField field, String errorMsg);
    /**
     *
     * @param field
     * @param errorMsg
     */
    public void validateDoubleJTextField(JTextField field, String errorMsg);
    
    public void validateRadioButtonValues(ButtonGroup radioGroup, String errorMsg);
    
    public void validateDateJTextField(JTextField field, String errorMsg);
    
    /**
     * It display an error dialog for the user.
     * You can use UILog.showError helper 
     * @param errorMsg
     */
    public void showErrorDialog(String errorMsg);
    
    /**
     * Display the error message in a component and set the background of the
     * field with red
     * @param errorMsg
     * @param errorMsgContainer A container for the error message. It can be
     * a JLabel, JTextArea or a JTextField
     */
    public void showErrorMessage(String errorMsg, Component errorMsgContainer);
    
}
