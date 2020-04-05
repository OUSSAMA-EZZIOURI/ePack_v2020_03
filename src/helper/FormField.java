package helper;

import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author Oussama
 */
public class FormField {

    /**
     * // Component can be JTextField, JTextarea, JCombobox...
     */
    private JComponent component;

    /**
     *
     */
    private String type;
    /**
     * Regular expression pattern
     */
    private String errorMsg;
    /**
     * Error message
     */
    private String regex;
    /**
     * Container of error messages
     */
    private JLabel errMsgContainer;
    /**
     * Color to be set in component backgrounds case of error
     */
    private Color errBgColor;

    /**
     * Default Color to be set in component
     */
    private Color defaultBgColor;

    public FormField() {
    }

    public FormField(JComponent component, String type, String regex, String errMsg, JLabel errMsgContainer, Color errBgColor, Color defaultBgColor) {
        this.component = component;
        this.type = type;
        this.regex = regex;
        this.errorMsg = errMsg;
        this.errMsgContainer = errMsgContainer;
        this.errBgColor = errBgColor;
        this.defaultBgColor = defaultBgColor;

    }

    public JComponent getComponent() {
        return component;
    }

    public void setComponent(JComponent component) {
        this.component = component;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public JLabel getErrMsgContainer() {
        return errMsgContainer;
    }

    public void setErrMsgContainer(JLabel errMsgContainer) {
        this.errMsgContainer = errMsgContainer;
    }

    public Color getErrBgColor() {
        return errBgColor;
    }

    public void setErrBgColor(Color errBgColor) {
        this.errBgColor = errBgColor;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Color getDefaultBgColor() {
        return defaultBgColor;
    }

    public void setDefaultBgColor(Color defaultBgColor) {
        this.defaultBgColor = defaultBgColor;
    }

    @Override
    public String toString() {
        return "FormField{\n\n" + "component=" + component + "\n type=" + type + "\n errorMsg=" + errorMsg + "\n regex=" + regex + "\n errMsgContainer=" + errMsgContainer + "\n errBgColor=" + errBgColor + "\n defaultBgColor=" + defaultBgColor + '}';
    }

}
