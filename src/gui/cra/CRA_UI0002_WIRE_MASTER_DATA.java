package gui.cra;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import __main__.PropertiesLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.ConfigProject;
import entity.WireConfig;
import helper.HQLHelper;
import helper.Helper;
import helper.UIHelper;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.hibernate.Query;
import ui.UILog;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author Oussama
 */
public class CRA_UI0002_WIRE_MASTER_DATA extends javax.swing.JPanel {

    private JTabbedPane parent;
    Vector config_table_data = new Vector();
    WireConfig aux;
    boolean err = false;
    Vector<String> config_table_header = new Vector<String>(Arrays.asList(
            "ID",
            "Projet",
            "Wire num",
            "Article faisceau",
            "Module Intern.",
            "Stock",
            "Num Carte",
            "Qté Kanban",
            "Qté Bundle",
            "Num. Operation",
            "Mag. Source",
            "Mag. Location",
            "Mag. Dest",
            "Créé le.",
            "Créé par.",
            "Dernière modif.",
            "Modifié par."
    ));

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
     * @param object A non object to edit
     * @param debug True debug messages will be printed
     */
    private Object mapValuesFromJPanelToObj(JPanel form_panel, Object object, boolean debug) {
        try {
            if (debug) {
                System.out.println("Given object is type of " + object.getClass().getCanonicalName());
            }

            //Loop on all JTextFiels in form_panel
            for (Component c : form_panel.getComponents()) {
                if (c.isEnabled()) {
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
                        String theType = PropertyUtils.getPropertyType(object, c.getAccessibleContext().getAccessibleName()).getCanonicalName();
                        System.out.println("theType " + theType);
                        //Prepare the Class with the fully qualified name
                        Class theClass = Class.forName(theType);
                        System.out.println("theClass is type " + theClass.getCanonicalName());
                        //Convert to the correct class
                        Object value = convert(theClass, fieldValue);
                        // Setting the properties on the myBean
                        PropertyUtils.setSimpleProperty(object, c.getAccessibleContext().getAccessibleName(), value);
                    }
                }
            }
            return object;
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
     * "java.lang.Thread")
     * @param debug True debug messages will be printed
     */
    private Object mapValuesFromJPanelToObj(JPanel form_panel, String className, boolean debug) {

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

    static Object convert(Class<?> target, String s) {
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

    public static void main(String[] args) {

        String feedback = PropertiesLoader.loadConfigProperties();
        //LOGGER.log(Level.INFO, feedback);
        GlobalMethods.createDefaultDirectories();
        Helper.startSession();

        CRA_UI0002_WIRE_MASTER_DATA c = new CRA_UI0002_WIRE_MASTER_DATA();
        JFrame f = new JFrame();
        f.setSize(1200, 700);
        f.add(c);
        f.setVisible(true);
    }

    /**
     * Creates new form CRA_UI0002_WIRE_MASTER_DATA2
     */
    public CRA_UI0002_WIRE_MASTER_DATA() {
        initComponents();
        initGui();
    }

    public CRA_UI0002_WIRE_MASTER_DATA(JTabbedPane jTabbedPane) {
        this.parent = jTabbedPane;
        initComponents();
        initGui();
    }

    private void initGui() {
        config_jtable.setModel(new DefaultTableModel(new Vector(), config_table_header));
        UIHelper.disableEditingJtable(config_jtable);
        this.initContainerTableDoubleClick();

        combo_project.removeAllItems();
        if (combo_project.getItemCount() == 0) {
            ConfigProject.initProjectsJBox(this, combo_project, false);
        }

        ConfigProject.initProjectsJBox(this, combo_project_filter, true);
    }

    private void initContainerTableDoubleClick() {
        this.config_jtable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    //Cleat the message field
                    msg_lbl.setText("");

                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_WIRE_CONFIG_BY_ID);
                    query.setParameter("id", config_jtable.getValueAt(config_jtable.getSelectedRow(), 0));

                    Helper.sess.getTransaction().commit();
                    aux = (WireConfig) query.list().get(0);

                    //#######################
                    mapValuesInPanelFields(form_panel, aux, true);

                    //########################
                    btn_delete.setEnabled(true);
                    btn_duplicate.setEnabled(true);

                    //Hide the jtable and display the form
                    toggleFormVisibility();

                }
            }

        }
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txt_harness_part_filter = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt_internal_part_filter = new javax.swing.JTextField();
        btn_export_excel = new javax.swing.JButton();
        btn_refresh = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txt_card_number_filter = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        combo_project_filter = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        config_jtable = new javax.swing.JTable();
        root_panel = new javax.swing.JPanel();
        msg_lbl = new javax.swing.JLabel();
        form_panel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txt_id = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        combo_project = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txt_harnessPart = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txt_internalPart = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txt_sourceWarehouse = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_destWarehouse = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txt_sourceLocation = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_stock = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txt_cardNumber = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_kanbanQty = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txt_bundleQty = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txt_createTime = new javax.swing.JTextField();
        txt_createUser = new javax.swing.JTextField();
        txt_writeTime = new javax.swing.JTextField();
        txt_writeUser = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        txt_wireNo = new javax.swing.JTextField();
        txt_wireType = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txt_spoolPn = new javax.swing.JTextField();
        txt_color = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txt_intTerm1 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txt_intSeal1 = new javax.swing.JTextField();
        txt_intSeal2 = new javax.swing.JTextField();
        txt_intTerm2 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txt_stripLength1 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txt_stripLength2 = new javax.swing.JTextField();
        txt_length = new javax.swing.JTextField();
        txt_operationNo = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txt_multicoreName = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        txt_description = new javax.swing.JTextField();
        btn_hide_creation_form = new javax.swing.JToggleButton();
        btn_new = new javax.swing.JButton();
        btn_save = new javax.swing.JButton();
        btn_delete = new javax.swing.JButton();
        btn_duplicate = new javax.swing.JButton();

        setBackground(new java.awt.Color(36, 65, 86));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Master data fils");

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setText("Article faisceau");

        jLabel7.setText("Code interne");

        btn_export_excel.setText("Exporter en Excel");
        btn_export_excel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_export_excelActionPerformed(evt);
            }
        });

        btn_refresh.setText("Actualiser");
        btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refreshActionPerformed(evt);
            }
        });

        jLabel8.setText("Num Carte");

        jLabel9.setText("Project");

        combo_project_filter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", " ", " ", " " }));
        combo_project_filter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                combo_project_filterFocusGained(evt);
            }
        });

        config_jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        config_jtable.setCellSelectionEnabled(true);
        config_jtable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(config_jtable);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1225, Short.MAX_VALUE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(btn_refresh)
                                .add(18, 18, 18)
                                .add(btn_export_excel))
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel6)
                                    .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_internal_part_filter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_harness_part_filter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(35, 35, 35)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(txt_card_number_filter)
                                    .add(combo_project_filter, 0, 132, Short.MAX_VALUE))))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(txt_harness_part_filter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8)
                    .add(txt_card_number_filter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(txt_internal_part_filter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9)
                    .add(combo_project_filter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btn_export_excel)
                    .add(btn_refresh))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1)
                .addContainerGap())
        );

        txt_card_number_filter.getAccessibleContext().setAccessibleName("card_number");

        root_panel.setBackground(new java.awt.Color(36, 65, 86));
        root_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        msg_lbl.setForeground(new java.awt.Color(255, 255, 255));
        msg_lbl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        form_panel.setBackground(new java.awt.Color(36, 65, 86));
        form_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("ID");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel5.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_id.setEditable(false);
        txt_id.setText("#");
        txt_id.setName(""); // NOI18N
        txt_id.setNextFocusableComponent(combo_project);
        txt_id.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Projet");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel1.setPreferredSize(new java.awt.Dimension(130, 24));

        combo_project.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", " ", " " }));
        combo_project.setNextFocusableComponent(txt_wireNo);
        combo_project.setPreferredSize(new java.awt.Dimension(130, 24));
        combo_project.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                combo_projectFocusGained(evt);
            }
        });
        combo_project.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_projectActionPerformed(evt);
            }
        });

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Article faisceau");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel3.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_harnessPart.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Module Interne");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel10.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_internalPart.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Mag. Source");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel16.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_sourceWarehouse.setToolTipText("Magasin source");
        txt_sourceWarehouse.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Mag. Destination");
        jLabel17.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel17.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_destWarehouse.setToolTipText("Magasin destination");
        txt_destWarehouse.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Source Location");
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel22.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_sourceLocation.setToolTipText("Magasin source");
        txt_sourceLocation.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Stock");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel13.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_stock.setPreferredSize(new java.awt.Dimension(130, 24));
        txt_stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_stockActionPerformed(evt);
            }
        });

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Num. Carte");
        jLabel14.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_cardNumber.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Kanban qté");
        jLabel4.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_kanbanQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txt_kanbanQty.setNextFocusableComponent(txt_harnessPart);
        txt_kanbanQty.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Bundle qté");
        jLabel15.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_bundleQty.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Créé le.");
        jLabel18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel18.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Créé par.");
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel19.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Modifié le.");
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel20.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Modifié par.");
        jLabel21.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel21.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_createTime.setEditable(false);
        txt_createTime.setToolTipText("");
        txt_createTime.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_createUser.setEditable(false);
        txt_createUser.setToolTipText("");
        txt_createUser.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_writeTime.setEditable(false);
        txt_writeTime.setToolTipText("");
        txt_writeTime.setPreferredSize(new java.awt.Dimension(130, 24));

        txt_writeUser.setEditable(false);
        txt_writeUser.setToolTipText("");
        txt_writeUser.setPreferredSize(new java.awt.Dimension(130, 24));

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Fil num.");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txt_wireNo.setNextFocusableComponent(txt_kanbanQty);
        txt_wireNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_wireNoActionPerformed(evt);
            }
        });

        txt_wireType.setToolTipText("Type of wire");

        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Fil type");
        jLabel23.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("Code Bobine");
        jLabel26.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txt_color.setToolTipText("Magasin destination");

        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Color");
        jLabel24.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("Term 1");
        jLabel28.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("Joint 1");
        jLabel29.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txt_intTerm2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_intTerm2ActionPerformed(evt);
            }
        });

        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("Term 2");
        jLabel30.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText("Joint 2");
        jLabel31.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText("Strip long. 1");
        jLabel32.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txt_stripLength1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_stripLength1ActionPerformed(evt);
            }
        });

        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Strip long.  2");
        jLabel33.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txt_operationNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_operationNoActionPerformed(evt);
            }
        });

        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Nom Multicore");
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txt_multicoreName.setToolTipText("Multicore");

        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText("Longueur");
        jLabel34.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("Operation Num.");
        jLabel35.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel36.setText("Description");
        jLabel36.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        org.jdesktop.layout.GroupLayout form_panelLayout = new org.jdesktop.layout.GroupLayout(form_panel);
        form_panel.setLayout(form_panelLayout);
        form_panelLayout.setHorizontalGroup(
            form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSeparator1)
            .add(form_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(form_panelLayout.createSequentialGroup()
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(txt_sourceLocation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_harnessPart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_internalPart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, combo_project, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(txt_id, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(txt_sourceWarehouse, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(txt_destWarehouse, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txt_stock, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_cardNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_kanbanQty, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_bundleQty, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(18, 18, 18)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txt_createTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_createUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_writeTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_writeUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(form_panelLayout.createSequentialGroup()
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(6, 6, 6)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_color, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_operationNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_length, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_wireType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_wireNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(form_panelLayout.createSequentialGroup()
                                .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(txt_intTerm1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(txt_intSeal1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(txt_stripLength1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(txt_spoolPn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_multicoreName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_intTerm2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_intSeal2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txt_stripLength2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(txt_description))
                        .add(37, 37, 37))))
        );
        form_panelLayout.setVerticalGroup(
            form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(form_panelLayout.createSequentialGroup()
                .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(form_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_id, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(txt_createTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(txt_createUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(txt_writeTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_writeUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(form_panelLayout.createSequentialGroup()
                        .add(36, 36, 36)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(combo_project, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_stock, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(form_panelLayout.createSequentialGroup()
                                .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(txt_harnessPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(txt_internalPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(txt_sourceWarehouse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(form_panelLayout.createSequentialGroup()
                                    .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(txt_cardNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(txt_kanbanQty, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .add(form_panelLayout.createSequentialGroup()
                                    .add(62, 62, 62)
                                    .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jLabel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(txt_bundleQty, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                        .add(7, 7, 7)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_sourceLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_destWarehouse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(form_panelLayout.createSequentialGroup()
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_wireNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_wireType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(6, 6, 6)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_length, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_operationNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_color, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(form_panelLayout.createSequentialGroup()
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_spoolPn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_multicoreName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_intTerm1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_intTerm2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_intSeal1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_intSeal2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_stripLength1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txt_stripLength2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(form_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txt_description, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        txt_id.getAccessibleContext().setAccessibleName("id");
        txt_id.getAccessibleContext().setAccessibleDescription("");
        txt_id.getAccessibleContext().setAccessibleParent(root_panel);
        combo_project.getAccessibleContext().setAccessibleName("project");
        combo_project.getAccessibleContext().setAccessibleParent(root_panel);
        txt_harnessPart.getAccessibleContext().setAccessibleName("harnessPart");
        txt_harnessPart.getAccessibleContext().setAccessibleParent(root_panel);
        txt_internalPart.getAccessibleContext().setAccessibleName("internalPart");
        txt_internalPart.getAccessibleContext().setAccessibleParent(root_panel);
        txt_sourceWarehouse.getAccessibleContext().setAccessibleName("sourceWarehouse");
        txt_sourceWarehouse.getAccessibleContext().setAccessibleParent(root_panel);
        txt_destWarehouse.getAccessibleContext().setAccessibleName("destWarehouse");
        txt_destWarehouse.getAccessibleContext().setAccessibleParent(root_panel);
        txt_sourceLocation.getAccessibleContext().setAccessibleName("sourceLocation");
        txt_sourceLocation.getAccessibleContext().setAccessibleParent(root_panel);
        txt_stock.getAccessibleContext().setAccessibleName("stock");
        txt_stock.getAccessibleContext().setAccessibleParent(root_panel);
        txt_cardNumber.getAccessibleContext().setAccessibleName("cardNumber");
        txt_cardNumber.getAccessibleContext().setAccessibleParent(root_panel);
        txt_kanbanQty.getAccessibleContext().setAccessibleName("kanbanQty");
        txt_kanbanQty.getAccessibleContext().setAccessibleParent(root_panel);
        txt_bundleQty.getAccessibleContext().setAccessibleName("bundleQty");
        txt_bundleQty.getAccessibleContext().setAccessibleParent(root_panel);
        txt_createTime.getAccessibleContext().setAccessibleName("createTime");
        txt_createTime.getAccessibleContext().setAccessibleParent(root_panel);
        txt_createUser.getAccessibleContext().setAccessibleName("createUser");
        txt_createUser.getAccessibleContext().setAccessibleParent(root_panel);
        txt_writeTime.getAccessibleContext().setAccessibleName("writeTime");
        txt_writeTime.getAccessibleContext().setAccessibleParent(root_panel);
        txt_writeUser.getAccessibleContext().setAccessibleName("writeUser");
        txt_writeUser.getAccessibleContext().setAccessibleParent(root_panel);
        txt_wireNo.getAccessibleContext().setAccessibleName("wireNo");
        txt_wireNo.getAccessibleContext().setAccessibleDescription("");
        txt_wireNo.getAccessibleContext().setAccessibleParent(root_panel);
        txt_wireType.getAccessibleContext().setAccessibleName("wireType");
        txt_wireType.getAccessibleContext().setAccessibleDescription("Type of wire");
        txt_wireType.getAccessibleContext().setAccessibleParent(root_panel);
        txt_spoolPn.getAccessibleContext().setAccessibleName("spoolPn");
        txt_spoolPn.getAccessibleContext().setAccessibleDescription("Code Bobine");
        txt_spoolPn.getAccessibleContext().setAccessibleParent(root_panel);
        txt_color.getAccessibleContext().setAccessibleName("color");
        txt_color.getAccessibleContext().setAccessibleDescription("Color");
        txt_color.getAccessibleContext().setAccessibleParent(root_panel);
        txt_intTerm1.getAccessibleContext().setAccessibleName("intTerm1");
        txt_intTerm1.getAccessibleContext().setAccessibleDescription("Terminal Côté 1");
        txt_intTerm1.getAccessibleContext().setAccessibleParent(root_panel);
        txt_intSeal1.getAccessibleContext().setAccessibleName("intSeal1");
        txt_intSeal1.getAccessibleContext().setAccessibleDescription("Joint Côté 1");
        txt_intSeal1.getAccessibleContext().setAccessibleParent(root_panel);
        txt_intSeal2.getAccessibleContext().setAccessibleName("intSeal2");
        txt_intSeal2.getAccessibleContext().setAccessibleDescription("Joint côté 2");
        txt_intSeal2.getAccessibleContext().setAccessibleParent(root_panel);
        txt_intTerm2.getAccessibleContext().setAccessibleName("intTerm2");
        txt_intTerm2.getAccessibleContext().setAccessibleDescription("Terminal côté 2");
        txt_intTerm2.getAccessibleContext().setAccessibleParent(root_panel);
        txt_stripLength1.getAccessibleContext().setAccessibleName("stripLength1");
        txt_stripLength1.getAccessibleContext().setAccessibleDescription("Longeur Strip côté 1");
        txt_stripLength1.getAccessibleContext().setAccessibleParent(root_panel);
        txt_stripLength2.getAccessibleContext().setAccessibleName("stripLength2");
        txt_stripLength2.getAccessibleContext().setAccessibleDescription("Longeur Strip côté 2");
        txt_stripLength2.getAccessibleContext().setAccessibleParent(root_panel);
        txt_length.getAccessibleContext().setAccessibleName("length");
        txt_length.getAccessibleContext().setAccessibleParent(root_panel);
        txt_operationNo.getAccessibleContext().setAccessibleName("operationNo");
        txt_operationNo.getAccessibleContext().setAccessibleParent(root_panel);
        txt_multicoreName.getAccessibleContext().setAccessibleName("multicoreName");
        txt_multicoreName.getAccessibleContext().setAccessibleParent(root_panel);
        txt_description.getAccessibleContext().setAccessibleName("description");
        txt_description.getAccessibleContext().setAccessibleDescription("Description du fil");
        txt_description.getAccessibleContext().setAccessibleParent(root_panel);

        org.jdesktop.layout.GroupLayout root_panelLayout = new org.jdesktop.layout.GroupLayout(root_panel);
        root_panel.setLayout(root_panelLayout);
        root_panelLayout.setHorizontalGroup(
            root_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, root_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(root_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(msg_lbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 863, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, form_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(199, 199, 199))
        );
        root_panelLayout.setVerticalGroup(
            root_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(root_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(msg_lbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(form_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(13, 13, 13))
        );

        btn_hide_creation_form.setText("Masquer formulaire");
        btn_hide_creation_form.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hide_creation_formActionPerformed(evt);
            }
        });

        btn_new.setText("Nouveau");
        btn_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_newActionPerformed(evt);
            }
        });

        btn_save.setText("Enregistrer");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        btn_delete.setText("Supprimer");
        btn_delete.setEnabled(false);
        btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteActionPerformed(evt);
            }
        });

        btn_duplicate.setText("Dupliquer");
        btn_duplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_duplicateActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 396, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(btn_hide_creation_form)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btn_new, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(btn_duplicate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(48, 48, 48)
                        .add(btn_save)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btn_delete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(root_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 524, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel11)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btn_hide_creation_form)
                    .add(btn_new, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(btn_save)
                    .add(btn_delete)
                    .add(btn_duplicate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(root_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(56, 56, 56))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_export_excelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_export_excelActionPerformed
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
        fileChooser.setSelectedFile(new File("PLANNING.csv"));
        Helper.centerJFileChooser(fileChooser);
        int j = fileChooser.showSaveDialog(this);
        if (j != 0) {
            return;
        }
        String str = fileChooser.getSelectedFile().getAbsolutePath();

        try (FileWriter fw = new FileWriter(str)) {
            try (CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT
                    .withHeader("harness_part", "internal_part", "planned_qty"))) {
                for (int i = 0; i < this.config_jtable.getRowCount(); i++) {
                    printer.printRecord(
                            config_jtable.getValueAt(i, 1).toString(), //harness_part
                            config_jtable.getValueAt(i, 2).toString(), //internal_part
                            Integer.valueOf(config_jtable.getValueAt(i, 3).toString())//planned_qty
                    );
                }
            }
            UILog.infoDialog("Fichier enregistré dans " + fileChooser.getSelectedFile());
        } catch (IOException ex) {
            Logger.getLogger(CRA_UI0001_PRODUCTION_PLAN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_export_excelActionPerformed

    private void btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refreshActionPerformed
        refreshTable();
    }//GEN-LAST:event_btn_refreshActionPerformed

    private void combo_project_filterFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_combo_project_filterFocusGained

    }//GEN-LAST:event_combo_project_filterFocusGained

    private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteActionPerformed
        int confirmed = JOptionPane.showConfirmDialog(this,
                String.format("Confirmez-vous la suppression de cet élément [%s] ?",
                        this.aux.getId()),
                "Suppression",
                JOptionPane.WARNING_MESSAGE);

        if (confirmed == 0) {
            aux.delete(aux);
            clearFields();
            msg_lbl.setText("Elément supprimé !");
            refreshTable();
        }
    }//GEN-LAST:event_btn_deleteActionPerformed

    private Vector getConfigLines() {
        config_table_data = new Vector();
        Helper.startSession();

        Query query = Helper.sess.createQuery(HQLHelper.GET_WIRE_CONFIG_CPN_AND_LPN_AND_CARDNUM_AND_PROJECT);
        query.setParameter("harnessPart", "%" + txt_harness_part_filter.getText() + "%");
        query.setParameter("internalPart", "%" + txt_internal_part_filter.getText() + "%");

        List<Object> projects = new ArrayList<Object>();
        if (combo_project_filter.getSelectedItem().toString().equals("ALL")) {

            for (int i = 0; i < combo_project_filter.getItemCount(); i++) {
                projects.add(combo_project_filter.getItemAt(i).toString());
            }
        } else {
            projects.add(combo_project_filter.getSelectedItem().toString());
        }
        query.setParameterList("project", projects);
        System.out.println("projects " + projects);
        System.out.println("query " + query.toString());

        List<WireConfig> result = query.list();
        Helper.sess.getTransaction().commit();
        //Populate the jTable with lines
        for (WireConfig c : result) {
            Vector<Object> row = new Vector<Object>();
            row.add(c.getId());
            row.add(c.getProject());
            row.add(c.getWireNo());
            row.add(c.getHarnessPart());
            row.add(c.getInternalPart());
            row.add(c.getStock());
            row.add(c.getCardNumber());
            row.add(c.getKanbanQty());
            row.add(c.getBundleQty());
            row.add(c.getOperationNo());
            row.add(c.getSourceWarehouse());
            row.add(c.getSourceLocation());
            row.add(c.getDestWarehouse());
            row.add(c.getCreateTime());
            row.add(c.getCreateUser());
            row.add(c.getWriteTime());
            row.add(c.getWriteUser());

            config_table_data.add(row);
        }
        return config_table_data;
    }

    private void btn_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_newActionPerformed

        UIHelper.clearJTextFields(root_panel.getComponents());
        txt_id.setText("#");
    }//GEN-LAST:event_btn_newActionPerformed

    private void btn_hide_creation_formActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hide_creation_formActionPerformed
        this.toggleFormVisibility();
    }//GEN-LAST:event_btn_hide_creation_formActionPerformed
    
    
    public void toggleFormVisibility() {
        if (btn_hide_creation_form.isSelected()) {
            root_panel.setVisible(false);
            btn_hide_creation_form.setText("Afficher formulaire");
        } else {
            root_panel.setVisible(true);
            btn_hide_creation_form.setText("Masquer formulaire");
        }
    }
    
    private void combo_projectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_projectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_projectActionPerformed

    private void combo_projectFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_combo_projectFocusGained

    }//GEN-LAST:event_combo_projectFocusGained

    private void txt_stockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_stockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_stockActionPerformed

    private void btn_duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_duplicateActionPerformed
        txt_id.setText("#");
        txt_createTime.setText("");
        txt_createUser.setText("");
        txt_writeTime.setText("");
        txt_writeUser.setText("");
        this.aux = null;
        msg_lbl.setText("Element dupliqué !");
    }//GEN-LAST:event_btn_duplicateActionPerformed

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        // it's a new item
        if (txt_id.getText().equals("#")) {
            WireConfig c = (WireConfig) this.mapValuesFromJPanelToObj(root_panel, "entity.WireConfig", true);
            c.setCreateTime(new Date());
            c.setCreateId(GlobalVars.CONNECTED_USER.getId());
            c.setCreateUser(GlobalVars.CONNECTED_USER.getFNameLName());
            c.setWriteTime(new Date());
            c.setWriteId(GlobalVars.CONNECTED_USER.getId());
            c.setWriteUser(GlobalVars.CONNECTED_USER.getFNameLName());
            System.out.println("c " + c.toString());
            c.create(c);
            msg_lbl.setText("Nouveau élement enregistré !");
            clearFields();
        } //Editing existing item from the list
        else {
            WireConfig c = (WireConfig) this.mapValuesFromJPanelToObj(root_panel, aux, true);
            System.out.println("c " + c.toString());
            c.setWriteTime(new Date());
            c.setWriteId(GlobalVars.CONNECTED_USER.getId());
            c.setWriteUser(GlobalVars.CONNECTED_USER.getFNameLName());

            //c.update(c);
            msg_lbl.setText("Modification effectuée !");
            clearFields();
        }
    }//GEN-LAST:event_btn_saveActionPerformed

    private void txt_wireNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_wireNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_wireNoActionPerformed

    private void txt_operationNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_operationNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_operationNoActionPerformed

    private void txt_intTerm2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_intTerm2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_intTerm2ActionPerformed

    private void txt_stripLength1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_stripLength1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_stripLength1ActionPerformed

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
    private void mapValuesInPanelFields(JPanel form_panel, Object obj, boolean debug) {

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
                if (debug) System.out.println("--------------------------\n");
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
            UILog.errorDialog(ex.getStackTrace()[0].toString());
        }catch (NullPointerException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
            UILog.errorDialog(ex.getStackTrace()[0].toString());
        }

    }

    private void clearFields() {
        UIHelper.clearJTextFields(root_panel.getComponents());
        txt_id.setText("#");
        btn_delete.setEnabled(false);
        btn_duplicate.setEnabled(false);
    }

    

    private void refreshTable() {
        config_jtable.setModel(new DefaultTableModel(new Vector(), config_table_header));
        config_jtable.setModel(new DefaultTableModel(getConfigLines(), config_table_header));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_delete;
    private javax.swing.JButton btn_duplicate;
    private javax.swing.JButton btn_export_excel;
    private javax.swing.JToggleButton btn_hide_creation_form;
    private javax.swing.JButton btn_new;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JButton btn_save;
    private javax.swing.JComboBox<String> combo_project;
    private javax.swing.JComboBox<String> combo_project_filter;
    private javax.swing.JTable config_jtable;
    private javax.swing.JPanel form_panel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel msg_lbl;
    private javax.swing.JPanel root_panel;
    private javax.swing.JTextField txt_bundleQty;
    private javax.swing.JTextField txt_cardNumber;
    private javax.swing.JTextField txt_card_number_filter;
    private javax.swing.JTextField txt_color;
    private javax.swing.JTextField txt_createTime;
    private javax.swing.JTextField txt_createUser;
    private javax.swing.JTextField txt_description;
    private javax.swing.JTextField txt_destWarehouse;
    private javax.swing.JTextField txt_harnessPart;
    private javax.swing.JTextField txt_harness_part_filter;
    private javax.swing.JTextField txt_id;
    private javax.swing.JTextField txt_intSeal1;
    private javax.swing.JTextField txt_intSeal2;
    private javax.swing.JTextField txt_intTerm1;
    private javax.swing.JTextField txt_intTerm2;
    private javax.swing.JTextField txt_internalPart;
    private javax.swing.JTextField txt_internal_part_filter;
    private javax.swing.JFormattedTextField txt_kanbanQty;
    private javax.swing.JTextField txt_length;
    private javax.swing.JTextField txt_multicoreName;
    private javax.swing.JTextField txt_operationNo;
    private javax.swing.JTextField txt_sourceLocation;
    private javax.swing.JTextField txt_sourceWarehouse;
    private javax.swing.JTextField txt_spoolPn;
    private javax.swing.JTextField txt_stock;
    private javax.swing.JTextField txt_stripLength1;
    private javax.swing.JTextField txt_stripLength2;
    private javax.swing.JTextField txt_wireNo;
    private javax.swing.JTextField txt_wireType;
    private javax.swing.JTextField txt_writeTime;
    private javax.swing.JTextField txt_writeUser;
    // End of variables declaration//GEN-END:variables

    

}
