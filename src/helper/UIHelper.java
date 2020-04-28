/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gui.cra.CRA_UI0002_WIRE_MASTER_DATA;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.beanutils.PropertyUtils;
import ui.UILog;

/**
 *
 * @author user
 */
public class UIHelper {

        //------------------------ Center JDialog in screen ------------------------
    /**
     *
     * @param jdialog
     *
     * Center the jdialog in the center of the screen
     */
    public static void centerJDialog(JDialog jdialog) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - jdialog.getWidth()) / 2;
        int y = (screenSize.height - jdialog.getHeight()) / 2;
        jdialog.setLocation(x, y);
    }
    
    /**
     *
     * @param jfilechooser
     *
     * Center the jdialog in the center of the screen
     */
    public static void centerJFileChooser(JFileChooser jfilechooser) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - jfilechooser.getWidth()) / 2;
        int y = (screenSize.height - jfilechooser.getHeight()) / 2;
        jfilechooser.setLocation(x, y);
    }

    /**
     *
     * @param jframe
     *
     * Center the jframe in the center of the screen
     */
    public static void centerJFrame(JFrame jframe) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - jframe.getWidth()) / 2;
        int y = (screenSize.height - jframe.getHeight()) / 2;
        jframe.setLocation(x, y);
    }
    
    /**
     * Delete the text content of given components
     *
     * @param args
     */
    public static void clearJTextFields(Component[] args) {
        for (Component component : args) {

            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            }
        }
    }

    /**
     * Delete the text content of given components
     *
     * @param args
     */
    public static void clearTextFields(Component... args) {
        for (Component component : args) {
            switch (component.getClass().getName()) {
                case "JTextField":
                    ((JTextField) component).setText("");
                    break;
                case "JLabel":
                    ((JLabel) component).setText("");
                    break;
                case "JTextArea":
                    ((JTextArea) component).setText("");
                    break;
                case "JComboBox":
                    ((JComboBox) component).setSelectedIndex(0);
                    break;

            }
        }
    }

    /**
     * Set required field background color to
     *
     * @param args
     */
    public static void highlightRequiredFields(Component... args) {
        for (Component component : args) {
            switch (component.getClass().getName()) {
                case "JTextField":
                    ((JTextField) component).setBackground(new Color(204, 204, 255));
                    break;
                case "JLabel":
                    ((JLabel) component).setBackground(new Color(204, 204, 255));
                    break;
                case "JTextArea":
                    ((JTextArea) component).setBackground(new Color(204, 204, 255));
                    break;
                case "JComboBox":
                    ((JComboBox) component).setBackground(new Color(204, 204, 255));
                    break;

            }
        }
    }

    /**
     * Set the actual state of UI components
     *
     * @param newState : New state of the components
     * @param args : List of components, text, textarea, list, combobox
     */
    public static void setComponentsState(boolean newState, Component... args) {
        for (Component component : args) {
            component.setEnabled(newState);
        }
    }

    /**
     * Disable edition of jtable cells
     *
     * @param jtable
     */
    public static void disableEditingJtable(JTable jtable) {
        for (int c = 0; c < jtable.getColumnCount(); c++) {
            Class<?> col_class = jtable.getColumnClass(c);
            jtable.setDefaultEditor(col_class, null);        // remove editor            
        }
        JTableHelper.sizeColumnsToFit(jtable);
    }

    /**
     * @param classFields
     * @param table_data Table data
     * @param table_header Header list
     * @param jtable Target jTable to set
     * @param entity : The entity class name, used to cast table rows
     * @param resultList List of data, Hibernate query result
     */
    public void reload_table_data(String[] classFields, List resultList, Vector table_data, Vector<String> table_header, JTable jtable, String entity) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        for (Object obj : resultList) {
            Class<?> clazz = Class.forName(entity);
            Constructor<?> ctor = clazz.getConstructor(String.class);
            Object object = ctor.newInstance(new Object[]{});
            object = obj;
            Method fieldGetter = object.getClass().getMethod("getId");
            Vector<Object> oneRow = new Vector<Object>();
            String id = fieldGetter.invoke(object).toString();
            oneRow.add(id);
            table_data.add(oneRow);
        }

        jtable.setModel(new DefaultTableModel(table_data, table_header));
        jtable.setAutoCreateRowSorter(true);
    }

    public static JTable load_table_header(JTable jtable, List<String> table_header) {
        Vector<String> header_vector = new Vector<String>();
        for (Iterator<String> it = table_header.iterator(); it.hasNext();) {
            header_vector.add(it.next());
        }

        jtable.setModel(new DefaultTableModel(header_vector, 0));
        return jtable;
    }

    public static List<String> manageCheckedCheckboxes(final Container c) {
        Component[] comps = c.getComponents();
        List<String> checkedTexts = new ArrayList<String>();

        for (Component comp : comps) {

            if (comp instanceof JCheckBox) {
                JCheckBox box = (JCheckBox) comp;
                if (box.isSelected()) {

                    String text = box.getText();
                    checkedTexts.add(text);
                }
            }
        }
        return checkedTexts;
    }

    public static List<String> manageCheckedRadioButtons(final Container c) {
        Component[] comps = c.getComponents();
        List<String> checkedTexts = new ArrayList<String>();

        for (Component comp : comps) {

            if (comp instanceof JRadioButton) {
                JRadioButton radio = (JRadioButton) comp;
                if (radio.isSelected()) {

                    String text = radio.getText();
                    checkedTexts.add(text);
                }
            }
        }
        return checkedTexts;
    }

    /**
     * Check if a list contains a sting
     *
     * @param list
     * @param string
     * @return
     */
    public static boolean listContains(List<String> list, String string) {
        for (int i = 0; i < list.size(); i++) {
            if (list.contains(string)) {
                System.out.println(string + " exist !");
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param c
     * @param value
     */
    public static JComboBox selectValueInJComboBox(JComboBox c, String value) {
        for (int i = 0; i < c.getItemCount(); i++) {
            if (c.getItemAt(i).toString().equals(value)) {
                c.setSelectedIndex(i);
                break;
            }
        }
        return c;
    }
    
    /**
     * Loop on components of JPanel and fill them with object values
     *
     * IMPORTANT : For this method to work, All components property
     * 'AccessibleName' must have the same name as the attribute from the
     * entity.
     *
     * @see
     * //http://tutorials.jenkov.com/java-json/jackson-objectmapper.html#read-object-from-json-reader
     * @param form_panel JPanel which contains the components (JTextfields,
     * JTextArea, JCombobox, etc...)
     * @param obj The object to map in the fields
     * @param debug True debug messages will be printed
     */
    public static void mapValuesInPanelFields(JPanel form_panel, Object obj, boolean debug) {

        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            String auxJson = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            if (debug) {
                System.out.println(auxJson);
            }
            JsonNode jsonNode = jsonMapper.readValue(auxJson, JsonNode.class);

            //Loop on all JTextFiels in form_panel
            for (Component c : form_panel.getComponents()) {

                if (debug) {
                    System.out.println("--------------------------");
                    System.out.printf("Looking for %s in json tree \n", c.getAccessibleContext().getAccessibleName());
                }

                if (c instanceof JTextField) {
                    JsonNode node = jsonNode.get(c.getAccessibleContext().getAccessibleName());

                    if (debug) {
                        System.out.printf("value found = %s \n", node.asText());
                    }

                    ((JTextField) c).setText(node.asText());
                } else if (c instanceof JTextArea) {
                    JsonNode node = jsonNode.get(c.getAccessibleContext().getAccessibleName());

                    if (debug) {
                        System.out.printf("value found = %s \n", node.asText());
                    }

                    ((JTextArea) c).setText(node.asText());
                } else if (c instanceof JComboBox) {
                    JsonNode node = jsonNode.get(c.getAccessibleContext().getAccessibleName());

                    if (debug) {
                        System.out.printf("value found = %s \n", node.asText());
                    }
                    UIHelper.selectValueInJComboBox(((JComboBox) c), node.asText());
                } else {
                    if (debug) {
                        System.out.println("component type of %s not supported! \n" + c.getClass().getSimpleName());
                    }
                }
                if (debug) {
                    System.out.println("--------------------------\n");
                }
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
            //throw ex;
            //UILog.errorDialog(ex.getStackTrace()[0].toString());
        } catch (NullPointerException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
            //UILog.errorDialog(ex.getStackTrace()[0].toString());
        }

    }
    
     /**
     * Loop on components of JPanel and return an instanciated object with
     * values
     *
     * IMPORTANT : For this method to work, All components property
     * 'AccessibleName' must have the same name as the attribute from the
     * entity.
     *
     * @see
     * https://www.tutorialspoint.com/java_beanutils/standard_javabeans_basic_property_access.htm
     * @param form_panel JPanel which contains the components (JTextfields,
     * JTextArea, JCombobox, etc...)
     * @param className The fully classified name of the class (eg :
     * "java.lang.Thread" or "john.doe.User") 
     * @param debug True debug messages will be printed
     */
    public static Object mapValuesFromJPanelToObj(JPanel form_panel, String className, boolean debug) {

        try {
            if (debug) {
                System.out.println("Target simpleClassName " + className);
            }

            // Creating the bean and allows to access getter and setter properties
            Class bean = Class.forName(className);
            Object newObject = bean.newInstance();

            //Loop on all JTextFiels in form_panel
            for (Component c : form_panel.getComponents()) {
                String fieldValue = "";
                boolean itsAField = false;
                boolean isEditable = false;
                if (c instanceof JTextField || c instanceof JTextArea) {
                    fieldValue = ((JTextField) c).getText();
                    itsAField = true;
                    isEditable = ((JTextField) c).isEditable();
                } else if (c instanceof JComboBox) {
                    fieldValue = ((JComboBox) c).getSelectedItem().toString();
                    itsAField = true;
                    isEditable = ((JComboBox) c).isEnabled();
                } else {
                    if (debug) {
                        System.out.println("Not supported type " + c.getClass().getSimpleName());
                    }
                }

                if (itsAField && isEditable) {
                    //Get the correct class name from the bean property
                    String theType = PropertyUtils.getPropertyType(newObject, c.getAccessibleContext().getAccessibleName()).getCanonicalName();
                    System.out.println("theType " + theType);
                    //Prepare the Class with the fully qualified name
                    Class theClass = Class.forName(theType);
                    System.out.println("theClass is type " + theClass.getCanonicalName());
                    //Convert to the correct class
                    Object value = convert(theClass, fieldValue);
                    // Setting the properties on the myBean
                    PropertyUtils.setSimpleProperty(newObject, c.getAccessibleContext().getAccessibleName(), value);
                }
            }
            return newObject;
        } catch (InstantiationException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.lang.IllegalArgumentException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }
     /**
     * Loop on components of JPanel and return an instanciated object with
     * values
     *
     * IMPORTANT : For this method to work, All components property
     * 'AccessibleName' must have the same name as the attribute from the
     * entity.
     *
     * @see
     * https://www.tutorialspoint.com/java_beanutils/standard_javabeans_basic_property_access.htm
     * @param form_panel JPanel which contains the components (JTextfields,
     * JTextArea, JCombobox, etc...)
     * @param className The fully classified name of the class (eg :
     * "java.lang.Thread" or "john.doe.User") 
     * @param debug True debug messages will be printed
     */
    public static Object setValuesFromJPanelToObj(JPanel form_panel, Object obj, boolean debug) {
        String className = obj.getClass().getCanonicalName();
        System.out.println("Passed object "+obj.toString());
        try {
            if (debug) {
                System.out.println("Target simpleClassName " + className);
            }

            // Creating the bean and allows to access getter and setter properties
//            Class bean = Class.forName(className);
            //Object newObject = bean.newInstance();

            //Loop on all JTextFiels in form_panel
            for (Component c : form_panel.getComponents()) {
                String fieldValue = "";
                boolean itsAField = false;
                boolean isEditable = false;
                if (c instanceof JTextField || c instanceof JTextArea) {
                    fieldValue = ((JTextField) c).getText();
                    itsAField = true;
                    isEditable = ((JTextField) c).isEditable();
                } else if (c instanceof JComboBox) {
                    fieldValue = ((JComboBox) c).getSelectedItem().toString();
                    itsAField = true;
                    isEditable = ((JComboBox) c).isEnabled();
                } else {
                    if (debug) {
                        System.out.println("Not supported type " + c.getClass().getSimpleName());
                    }
                }

                if (itsAField && isEditable) {
                    //Get the correct class name from the bean property
                    String theType = PropertyUtils.getPropertyType(obj, c.getAccessibleContext().getAccessibleName()).getCanonicalName();
                    System.out.println("theType " + theType);
                    //Prepare the Class with the fully qualified name
                    Class theClass = Class.forName(theType);
                    System.out.println("theClass is type " + theClass.getCanonicalName());
                    //Convert to the correct class
                    Object value = convert(theClass, fieldValue);
                    // Setting the properties on the myBean
                    PropertyUtils.setSimpleProperty(obj, c.getAccessibleContext().getAccessibleName(), value);
                }else if(itsAField && (c.getAccessibleContext().getAccessibleName().equals("id")
                        || c.getAccessibleContext().getAccessibleName().equals("createId")
                        || c.getAccessibleContext().getAccessibleName().equals("writeId")
                        )){
                    //Get the correct class name from the bean property
                    String theType = PropertyUtils.getPropertyType(obj, c.getAccessibleContext().getAccessibleName()).getCanonicalName();
                    System.out.println("theType " + theType);
                    //Prepare the Class with the fully qualified name
                    Class theClass = Class.forName(theType);
                    System.out.println("theClass is type " + theClass.getCanonicalName());
                    //Convert to the correct class
                    Object value = convert(theClass, fieldValue);
                    // Setting the properties on the myBean
                    PropertyUtils.setSimpleProperty(obj, c.getAccessibleContext().getAccessibleName(), value);
                }
            }
            return obj;
        } 
//        catch (InstantiationException ex) {
//            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
//        } 
        catch (IllegalAccessException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.lang.IllegalArgumentException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }
    
    /**
     * Method used in mapValuesFromJPanelToObj
     * Convert string values to appropriate target type
     * @param target
     * @param s
     * @return 
     */
    private static Object convert(Class<?> target, String s) {
        if (target == Object.class || target == String.class || s == null) {
            return s;
        }
        if (target == Character.class || target == char.class) {
            return s.charAt(0);
        }
        if (target == Byte.class || target == byte.class) {
            return Byte.parseByte(s);
        }
        if (target == Short.class || target == short.class) {
            return Short.parseShort(s);
        }
        if (target == Integer.class || target == int.class) {
            return Integer.parseInt(s);
        }
        if (target == Long.class || target == long.class) {
            return Long.parseLong(s);
        }
        if (target == Float.class || target == float.class) {
            return Float.parseFloat(s);
        }
        if (target == Double.class || target == double.class) {
            return Double.parseDouble(s);
        }
        if (target == Boolean.class || target == boolean.class) {
            return Boolean.parseBoolean(s);
        }
        throw new IllegalArgumentException("Don't know how to convert to " + target);
    }
}
