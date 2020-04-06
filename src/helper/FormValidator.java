package helper;

import java.awt.Color;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * This class will help to validate the user's data inside a form before saving
 * operation.
 *
 * It uses Regex (Regular Expression) to validate the data on a field.
 *
 *
 * No.	Character Class Description 1	[abc] a, b, or c (simple class) 2	[^abc]
 * Any character except a, b, or c (negation) 3	[a-zA-Z] a through z or A
 * through Z, inclusive (range) 4	[a-d[m-p]] a through d, or m through p:
 * [a-dm-p] (union) 5	[a-z&&[def]] d, e, or f (intersection) 6	[a-z&&[^bc]] a
 * through z, except for b and c: [ad-z] (subtraction) 7	[a-z&&[^m-p]] a through
 * z, and not m through p: [a-lq-z](subtraction) 8 X? X occurs once or not at
 * all 9 X+ X occurs once or more times 10 X* X occurs zero or more times 11
 * X{n} X occurs n times only 12 X{n,} X occurs n or more times 13 X{y,z} X
 * occurs at least y times but less than z times 14 . Any character (may or may
 * not match terminator) 15 \d Any digits, short of [0-9] 16 \D Any non-digit,
 * short for [^0-9] 17 \s Any whitespace character, short for [\t\n\x0B\f\r] 18
 * \S Any non-whitespace character, short for [^\s] 19 \w Any word character,
 * short for [a-zA-Z_0-9] 20 \W Any non-word character, short for [^\w] 21 \b A
 * word boundary 22 \B A non word boundary
 *
 *
 *
 *
 * @author Oussama
 */
public class FormValidator {

    /**
     *
     * @param fieldsList
     * @param debug
     * @return
     */
    public boolean validateFields(List<FormField> fieldsList, boolean debug) {
        System.out.println("fieldsList "+fieldsList.size());
        System.out.println(fieldsList.toString());
        for (FormField field : fieldsList) {
            //Loop on each line
            
            if (debug) {
                System.out.println("\n- field " + field.getComponent().getName());
            }
            if (field.getComponent() instanceof JTextField) {
                if (debug) {
                    System.out.println(field.getComponent().getName() + " is a JTextField");
                }                        
                if (!Pattern.matches(field.getRegex(), ((JTextField) field.getComponent()).getText())) {
                    if (debug) {
                        System.out.println(((JTextField) field.getComponent()).getText()+" value doesn't matches the pattern "+field.getRegex());
                    }
                    field.getErrMsgContainer().setText(field.getErrorMsg());
                    field.getErrMsgContainer().setForeground(Color.red);
                    field.getComponent().setBackground(field.getErrBgColor());
                    field.getComponent().requestFocus();
                    ((JTextField) field.getComponent()).selectAll();
                    return false;
                } else {
                    if (debug) {
                        System.out.println(((JTextField) field.getComponent()).getText() + " value matches the pattern "+field.getRegex());
                    }
                    field.getErrMsgContainer().setForeground(Color.green);
                    field.getErrMsgContainer().setText("");
                    field.getComponent().setBackground(field.getDefaultBgColor());
                    continue;
                }
            } 

        }
        return true;
    }

}
