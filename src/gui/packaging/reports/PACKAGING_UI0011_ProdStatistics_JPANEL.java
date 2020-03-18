/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging.reports;

import __main__.GlobalVars;
import entity.ConfigProject;
import entity.ConfigSegment;
import entity.ConfigWorkplace;
import helper.Helper;
import helper.JDialogExcelFileChooser;
import helper.UIHelper;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 *
 * @author Administrator
 */
public class PACKAGING_UI0011_ProdStatistics_JPANEL extends javax.swing.JPanel {

    JTabbedPane parent;

    Vector<String> declared_result_table_header = new Vector<String>();
    Vector declared_result_table_data = new Vector();

    private List<Object[]> declaredResultList;

    List<Object> projects = new ArrayList<Object>();
    List<Object> segments = new ArrayList<Object>();
    List<Object> workplaces = new ArrayList<Object>();

    SimpleDateFormat timeDf = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateDf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateTimeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String startTimeStr = "";
    String endTimeStr = "";
    String startDateStr = null;
    String endDateStr = null;
    String harness_part = "";
    String spn_part = "";

    ButtonGroup radioGroup = new ButtonGroup();

    /**
     * Creates new form UI0011_ProdStatistics_
     */
    public PACKAGING_UI0011_ProdStatistics_JPANEL(JTabbedPane parent) {
        initComponents();
        initTimeSpinners();
        project_filter = ConfigProject.initProjectsJBox(this, project_filter, true);
//        combox = ConfigProject.initProjectsJBox(this, combox, true);

        this.workplace_filter.setEnabled(false);
        radioGroup.add(radio_scanned_harness);
        radioGroup.add(radio_closed_pack);
        radioGroup.add(radio_stored_pack);
        this.reset_tables_content();
    }

    private void initTimeSpinners() {

        String startTime = GlobalVars.APP_PROP.getProperty("START_TIME");
        String endTime = GlobalVars.APP_PROP.getProperty("END_TIME");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        //################# Start Time Spinner ####################
        startTimeSpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
        try {
            startTimeSpinner.setValue(timeFormat.parse(startTime));
        } catch (ParseException ex) {
            Logger.getLogger(PACKAGING_UI0011_ProdStatistics_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
        }

        //################# End Time Spinner ######################
        endTimeSpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        try {
            endTimeSpinner.setValue(timeFormat.parse(endTime));
        } catch (ParseException ex) {
            Logger.getLogger(PACKAGING_UI0011_ProdStatistics_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
        }

        startDatePicker.setDate(new Date());
        endDatePicker.setDate(new Date());

    }

    public void reset_tables_content() {
        //############ Reset declared table result
        this.load_declared_result_table_header();
        declared_result_table_data = new Vector();
        DefaultTableModel declaredDataModel = new DefaultTableModel(declared_result_table_data, declared_result_table_header);
        declared_result_table.setModel(declaredDataModel);

    }

    /**
     *
     */
    public void load_declared_result_table_header() {
        declared_result_table_header.clear();
        declared_result_table_header.add("Part number");
        if (spn_part_checkbox.isSelected()) {
            declared_result_table_header.add("Intern PN");
        }
        declared_result_table_header.add("Produced qty");
        declared_result_table_header.add("Segment");
        declared_result_table_header.add("Workplace");
        declared_result_table_header.add("Std Time (Hours)");
        declared_result_table_header.add("Produced hours");
        declared_result_table.setModel(new DefaultTableModel(declared_result_table_data, declared_result_table_header));
        declared_result_table.setAutoCreateRowSorter(true);
    }

    public void disableEditingTables() {
        for (int c = 0; c < declared_result_table.getColumnCount(); c++) {
            // remove editor   
            Class<?> col_class1 = declared_result_table.getColumnClass(c);
            declared_result_table.setDefaultEditor(col_class1, null);
        }
    }

    @SuppressWarnings("empty-statement")
    public void reload_declared_result_table_data(List<Object[]> resultList) {

        Double total_produced = 0.00;
        Double produced_hours = 0.00;

        for (Object[] obj : resultList) {
            Vector<Object> oneRow = new Vector<Object>();
            if (String.valueOf(obj[0]).startsWith("P")) {
                oneRow.add(String.valueOf(obj[0]).substring(1)); //harness_part
            } else {
                oneRow.add(String.valueOf(obj[0])); //harness_part
            }
            if (spn_part_checkbox.isSelected()) {
                oneRow.add(String.valueOf(obj[6])); //supplier part number
            }
            oneRow.add(new DecimalFormat("0.00").format(Double.parseDouble(obj[1].toString()))); // produced_qty
            oneRow.add(String.valueOf(obj[2])); //segment
            oneRow.add(String.valueOf(obj[3])); //workplace
            oneRow.add(new DecimalFormat("00.00").format(Double.parseDouble(obj[4].toString()))); // std_time            
            oneRow.add(new DecimalFormat("00.00").format(Double.parseDouble(obj[5].toString()))); // produced hours

            total_produced = total_produced + Double.valueOf(obj[1].toString());
            produced_hours = produced_hours + Double.valueOf(obj[5].toString());
            declared_result_table_data.add(oneRow);
        }
        declared_result_table.setModel(new DefaultTableModel(declared_result_table_data, declared_result_table_header));
        declared_result_table.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.BOLD, Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONTSIZE"))));
        declared_result_table.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));

        //Set declared qty labels values
        this.total_declared_lbl.setText(new DecimalFormat("0.00").format(total_produced));

        //Set declared hours labels values
        this.total_produced_hours_lbl.setText(new DecimalFormat("00.00").format(produced_hours));

    }

    private boolean checkValidFields() {
        if (startTimeSpinner.getValue() != ""
                && endTimeSpinner.getValue() != ""
                && startDatePicker.getDate() != null
                && endDatePicker.getDate() != null) {
            return true;
        } else {
            return false;
        }
    }

    private void refresh() {
        //System.out.println("Execute refresh()");
        if (checkValidFields()) {
            segments.clear();
            workplaces.clear();
            projects.clear();
            startTimeStr = timeDf.format(startTimeSpinner.getValue());
            endTimeStr = timeDf.format(endTimeSpinner.getValue());
            startDateStr = dateDf.format(startDatePicker.getDate()) + " " + startTimeStr;
            endDateStr = dateDf.format(endDatePicker.getDate()) + " " + endTimeStr;
            harness_part = "%" + harness_part_txt.getText() + "%";
            spn_part = "%" + spn_part_txt.getText() + "%";
            try {
                Date startDate = dateTimeDf.parse(startDateStr);
                Date endDate = dateTimeDf.parse(endDateStr);
                System.out.println(startDate.before(endDate));
            } catch (Exception ex) {
                Logger.getLogger(PACKAGING_UI0011_ProdStatistics_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Populate the segments Array with data
            if (String.valueOf(project_filter.getSelectedItem()).equals("ALL") || String.valueOf(project_filter.getSelectedItem()).equals("null")) {
                List result = new ConfigProject().select();
                if (result.isEmpty()) {
                    UILog.severeDialog(this, ErrorMsg.APP_ERR0035);
                    UILog.severe(ErrorMsg.APP_ERR0035[1]);
                } else { //Map project data in the list
                    for (Object o : result) {
                        ConfigProject cp = (ConfigProject) o;
                        projects.add(cp.getProject());
                    }
                }
            } else {
                projects.add(String.valueOf(project_filter.getSelectedItem()));
            }
            if (String.valueOf(segment_filter.getSelectedItem()).equals("ALL") || String.valueOf(segment_filter.getSelectedItem()).equals("null")) {
                List result = new ConfigSegment().select();
                if (result.isEmpty()) {
                    JOptionPane.showMessageDialog(null, Helper.ERR0026_NO_SEGMENT_FOUND, "Configuration error !", ERROR_MESSAGE);
                    System.err.println(Helper.ERR0026_NO_SEGMENT_FOUND);
                } else { //Map project data in the list
                    for (Object o : result) {
                        ConfigSegment cs = (ConfigSegment) o;
                        segments.add(String.valueOf(cs.getSegment()));
                    }
                }
            } else {
                segments.add(String.valueOf(segment_filter.getSelectedItem()));
                //Populate the workplaces Array with data
                if (String.valueOf(workplace_filter.getSelectedItem()).equals("ALL")) {
                    List result = new ConfigWorkplace().selectBySegment(String.valueOf(segment_filter.getSelectedItem()));
                    if (result.isEmpty()) {
                        JOptionPane.showMessageDialog(null, Helper.ERR0027_NO_WORKPLACE_FOUND, "Configuration error !", ERROR_MESSAGE);
                        System.err.println(Helper.ERR0027_NO_WORKPLACE_FOUND);
                    } else { //Map project data in the list
                        for (Object o : result) {
                            ConfigWorkplace cw = (ConfigWorkplace) o;
                            workplaces.add(String.valueOf(cw.getWorkplace()));
                        }
                    }
                } else {
                    workplaces.add(String.valueOf(workplace_filter.getSelectedItem()));
                }
            }

            try {
                //Clear all tables
                this.reset_tables_content();
                String query_str = "";
                Helper.startSession();
                SQLQuery query;

                //###############################################                
                if (!radio_scanned_harness.isSelected()) {
                    //Pallets with SOTRED state                    
                    query_str = "(SELECT bc.harness_part AS harness_part "
                            + " ,SUM(bc.qty_read) AS produced_qty "
                            + " ,bc.segment AS segment "
                            + " ,bc.workplace AS workplace "
                            + " ,bc.std_time AS std_time "
                            + " ,bc.std_time*SUM(bc.qty_read) AS produced_hours ";
                    if (spn_part_checkbox.isSelected()) {
                        query_str += " ,bc.supplier_part_number AS supplier_part_number ";
                    }
                    query_str += " FROM base_container bc WHERE ";

                    if (radio_closed_pack.isSelected()) {
                        query_str += " (bc.closed_time BETWEEN '%s' AND '%s')";
                    } else if (radio_stored_pack.isSelected()) {
                        query_str += " (bc.stored_time BETWEEN '%s' AND '%s')";
                    }

                    query_str += " AND bc.harness_part like '%s' AND bc.supplier_part_number like '%s' ";

                    if (!segments.isEmpty()) {
                        query_str += " AND bc.segment IN (:segments) ";
                    }
                    if (!workplaces.isEmpty()) {
                        query_str += " AND bc.workplace IN (:workplaces) ";
                    }
                    if (!projects.isEmpty()) {
                        query_str += " AND bc.project IN (:projects) ";
                    }

                    query_str = String.format(query_str, startDateStr, endDateStr, harness_part, spn_part);

                    query_str += " GROUP BY bc.harness_part, bc.segment, bc.workplace, bc.std_time ";

                    if (spn_part_checkbox.isSelected()) {
                        query_str += " ,bc.supplier_part_number ";
                    }

                    query_str += " ORDER BY bc.segment ASC, bc.workplace ASC, bc.harness_part ASC)";
                    System.out.println("Query ");
                    System.out.println(query_str);
                    //Select only harness parts with UCS completed.                                
                    query = Helper.sess.createSQLQuery(query_str);

                    query.addScalar("harness_part", StandardBasicTypes.STRING)
                            .addScalar("produced_qty", StandardBasicTypes.DOUBLE)
                            .addScalar("segment", StandardBasicTypes.STRING)
                            .addScalar("workplace", StandardBasicTypes.STRING)
                            .addScalar("std_time", StandardBasicTypes.DOUBLE)
                            .addScalar("produced_hours", StandardBasicTypes.DOUBLE);

                    if (spn_part_checkbox.isSelected()) {
                        query.addScalar("supplier_part_number", StandardBasicTypes.STRING);
                    }
                    if (!projects.isEmpty()) {
                        query.setParameterList("projects", projects);
                    }
                    if (!segments.isEmpty()) {
                        query.setParameterList("segments", segments);
                    }
                    if (!workplaces.isEmpty()) {
                        query.setParameterList("workplaces", workplaces);
                    }

                } else { // Fx Scannés
                    //Request 2
                    query_str = " SELECT  "
                            + " bh.harness_part AS harness_part,"
                            + " COUNT(bh.harness_part) AS produced_qty,"
                            + " bh.segment AS segment,"
                            + " bh.workplace AS workplace,"
                            + " bh.std_time AS std_time,"
                            + " SUM(bh.std_time) AS produced_hours ";
                    if (spn_part_checkbox.isSelected()) {
                        query_str += " ,bc.supplier_part_number AS supplier_part_number ";
                    }
                    query_str += " FROM base_harness bh, base_container bc "
                            + " WHERE bc.id = bh.container_id "
                            + " AND bh.create_time BETWEEN '%s' AND '%s'"
                            + " AND bc.harness_part like '%s' "
                            + " AND bc.supplier_part_number like '%s' ";

                    if (!segments.isEmpty()) {
                        query_str += " AND bc.segment IN (:segments) ";
                    }
                    if (!workplaces.isEmpty()) {
                        query_str += " AND bc.workplace IN (:workplaces) ";
                    }
                    if (!projects.isEmpty()) {
                        query_str += " AND bc.project IN (:projects) ";
                    }

                    query_str = String.format(query_str, startDateStr, endDateStr, harness_part, spn_part);
                    query_str += " GROUP BY bh.segment, bh.workplace, bh.harness_part, bh.std_time  ";
                    if (spn_part_checkbox.isSelected()) {
                        query_str += " ,bc.supplier_part_number ";
                    }
                            
                    query_str += " ORDER BY bh.segment ASC, bh.workplace ASC, bh.harness_part ASC";

                    //Select only harness parts with UCS completed.                                
                    query = Helper.sess.createSQLQuery(query_str);

                    query.addScalar("harness_part", StandardBasicTypes.STRING)
                            .addScalar("produced_qty", StandardBasicTypes.DOUBLE)
                            .addScalar("segment", StandardBasicTypes.STRING)
                            .addScalar("workplace", StandardBasicTypes.STRING)
                            .addScalar("std_time", StandardBasicTypes.DOUBLE)
                            .addScalar("produced_hours", StandardBasicTypes.DOUBLE);
                    if (spn_part_checkbox.isSelected()) {
                        query.addScalar("supplier_part_number", StandardBasicTypes.STRING);
                    }
                    if (!projects.isEmpty()) {
                        query.setParameterList("projects", projects);
                    }
                    if (!segments.isEmpty()) {
                        query.setParameterList("segments", segments);
                    }
                    if (!workplaces.isEmpty()) {
                        query.setParameterList("workplaces", workplaces);
                    }

                }

                this.declaredResultList = query.list();

                Helper.sess.getTransaction().commit();

                this.reload_declared_result_table_data(declaredResultList);

                this.disableEditingTables();

            } catch (HibernateException e) {
                if (Helper.sess.getTransaction() != null) {
                    Helper.sess.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Empty field", "Empty field Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        north_panel = new javax.swing.JPanel();
        result_table_scroll = new javax.swing.JScrollPane();
        declared_result_table = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        radio_scanned_harness = new javax.swing.JRadioButton();
        radio_closed_pack = new javax.swing.JRadioButton();
        radio_stored_pack = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        harness_part_txt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        startDatePicker = new org.jdesktop.swingx.JXDatePicker();
        startTimeSpinner = new javax.swing.JSpinner();
        endTimeSpinner = new javax.swing.JSpinner();
        endDatePicker = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        spn_part_txt = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        spn_part_checkbox = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        total_declared_lbl = new javax.swing.JTextField();
        total_produced_hours_lbl = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        total_dropped_lbl = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        total_dropped_hours_lbl = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        project_filter = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        segment_filter = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        workplace_filter = new javax.swing.JComboBox();
        refresh_btn = new javax.swing.JButton();
        export_btn = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(36, 65, 86));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        north_panel.setBackground(new java.awt.Color(36, 65, 86));
        north_panel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                north_panelKeyPressed(evt);
            }
        });

        declared_result_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        declared_result_table.setColumnSelectionAllowed(true);
        declared_result_table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        result_table_scroll.setViewportView(declared_result_table);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Déclaration fin de ligne");

        jPanel2.setBackground(new java.awt.Color(36, 65, 86));

        jPanel3.setBackground(new java.awt.Color(36, 65, 86));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        radio_scanned_harness.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        radio_scanned_harness.setForeground(new java.awt.Color(255, 255, 255));
        radio_scanned_harness.setSelected(true);
        radio_scanned_harness.setText("Pièces scannées");
        radio_scanned_harness.setToolTipText("<html>Calcul le total des faisceaux scannés au niveau fin de ligne.</html>");
        radio_scanned_harness.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_scanned_harnessItemStateChanged(evt);
            }
        });

        radio_closed_pack.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        radio_closed_pack.setForeground(new java.awt.Color(255, 255, 255));
        radio_closed_pack.setText("Palette fermée (CLOSED)");
        radio_closed_pack.setToolTipText("Calcul la quantité des palettes avec UCS Complet.");
        radio_closed_pack.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_closed_packItemStateChanged(evt);
            }
        });
        radio_closed_pack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_closed_packActionPerformed(evt);
            }
        });

        radio_stored_pack.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        radio_stored_pack.setForeground(new java.awt.Color(255, 255, 255));
        radio_stored_pack.setText("Palette stockée (STORED)");
        radio_stored_pack.setToolTipText("Calcul la quantité des palettes avec UCS Complet.");
        radio_stored_pack.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_stored_packItemStateChanged(evt);
            }
        });
        radio_stored_pack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_stored_packActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(radio_closed_pack, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                        .addComponent(radio_scanned_harness, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(radio_stored_pack, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(radio_scanned_harness)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(radio_closed_pack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(radio_stored_pack)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(36, 65, 86));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        harness_part_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                harness_part_txtActionPerformed(evt);
            }
        });
        harness_part_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                harness_part_txtKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Article");

        jLabel1.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Du");

        startTimeSpinner.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        endTimeSpinner.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Au");

        spn_part_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spn_part_txtActionPerformed(evt);
            }
        });
        spn_part_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                spn_part_txtKeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Intern PN");

        spn_part_checkbox.setBackground(new java.awt.Color(36, 65, 86));
        spn_part_checkbox.setForeground(new java.awt.Color(255, 255, 255));
        spn_part_checkbox.setText("Intern PN");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(spn_part_checkbox, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(spn_part_txt)
                            .addComponent(harness_part_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(endDatePicker, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(endTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(startTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(harness_part_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spn_part_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(spn_part_checkbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(startTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(endDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(36, 65, 86));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel18.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Σ Quantités produites");

        total_declared_lbl.setEditable(false);
        total_declared_lbl.setBackground(new java.awt.Color(153, 255, 255));
        total_declared_lbl.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        total_declared_lbl.setText("0");

        total_produced_hours_lbl.setEditable(false);
        total_produced_hours_lbl.setBackground(new java.awt.Color(153, 255, 255));
        total_produced_hours_lbl.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        total_produced_hours_lbl.setText("0");

        jLabel15.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Σ Heures produites");

        total_dropped_lbl.setEditable(false);
        total_dropped_lbl.setBackground(new java.awt.Color(255, 255, 102));
        total_dropped_lbl.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        total_dropped_lbl.setText("0");

        jLabel21.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Σ Quantités annulées");

        total_dropped_hours_lbl.setEditable(false);
        total_dropped_hours_lbl.setBackground(new java.awt.Color(255, 255, 102));
        total_dropped_hours_lbl.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        total_dropped_hours_lbl.setText("0");

        jLabel16.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Σ Heures annulées");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(total_dropped_lbl, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(total_produced_hours_lbl, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(total_declared_lbl, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(total_dropped_hours_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(total_declared_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(total_produced_hours_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(total_dropped_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(total_dropped_hours_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(36, 65, 86));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel23.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Projet");

        project_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                project_filterItemStateChanged(evt);
            }
        });
        project_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                project_filterActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Segment");

        segment_filter.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        segment_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                segment_filterItemStateChanged(evt);
            }
        });
        segment_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segment_filterActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Calibri", 1, 13)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Workplace");

        workplace_filter.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        workplace_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                workplace_filterItemStateChanged(evt);
            }
        });
        workplace_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workplace_filterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(270, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(1, 1, 1)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(127, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        refresh_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        refresh_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/refresh.png"))); // NOI18N
        refresh_btn.setText("Actualiser");
        refresh_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_btnActionPerformed(evt);
            }
        });

        export_btn.setText("Exporter en Excel...");
        export_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                export_btnActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Quantités déclarées");

        javax.swing.GroupLayout north_panelLayout = new javax.swing.GroupLayout(north_panel);
        north_panel.setLayout(north_panelLayout);
        north_panelLayout.setHorizontalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(34, 34, 34)
                        .addComponent(export_btn)
                        .addGap(13, 13, 13)
                        .addComponent(refresh_btn)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(result_table_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 898, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(78, Short.MAX_VALUE))))
        );
        north_panelLayout.setVerticalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(export_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(result_table_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(212, 212, 212))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(north_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 177, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(north_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 669, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refresh_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_btnActionPerformed

        refresh();

    }//GEN-LAST:event_refresh_btnActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.setVisible(false);
        }
    }//GEN-LAST:event_formKeyPressed

    private void north_panelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_north_panelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.setVisible(false);
        }
    }//GEN-LAST:event_north_panelKeyPressed

    private void export_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export_btnActionPerformed

        startTimeStr = timeDf.format(startTimeSpinner.getValue());
        endTimeStr = timeDf.format(endTimeSpinner.getValue());
        startDateStr = dateDf.format(startDatePicker.getDate()) + " " + startTimeStr;
        endDateStr = dateDf.format(endDatePicker.getDate()) + " " + endTimeStr;

        //Create the excel workbook
        Workbook wb = new XSSFWorkbook(); // new HSSFWorkbook();
        Sheet sheet = null;
        if (radio_scanned_harness.isSelected()) {
            sheet = wb.createSheet("SCANNED");
        } else if (radio_closed_pack.isSelected()) {
            sheet = wb.createSheet("CLOSED");
        } else {
            sheet = wb.createSheet("STORED");
        }
        CreationHelper createHelper = wb.getCreationHelper();
        //Export excel cell with numeric format
        CellStyle doubleFormat = wb.createCellStyle();
        CellStyle intFormat = wb.createCellStyle();
        //numericFormat.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        doubleFormat.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        intFormat.setDataFormat(wb.createDataFormat().getFormat("00"));

        double total_produced = 0.00;
        double total_produced_hours = 0.00;

        //######################################################################
        //##################### SHEET 1 : PILES DETAILS ########################
        //Initialiser les entête du fichier
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow((short) 0);

        row.createCell(0).setCellValue("PART NUMBER");
        if (spn_part_checkbox.isSelected()) {
            row.createCell(1).setCellValue("SUPPLIER PART");
        }
        row.createCell(2).setCellValue("PRODUCED QTY");
        row.createCell(3).setCellValue("SEGMENT");
        row.createCell(4).setCellValue("WORKPLACE");
        row.createCell(5).setCellValue("STD TIME");
        row.createCell(6).setCellValue("PRODUCED HOURS");

        short sheetPointer = 1;

        for (Object[] obj : this.declaredResultList) {
            row = sheet.createRow(sheetPointer);
            if (String.valueOf(obj[0].toString()).startsWith("P")) {
                row.createCell(0).setCellValue(String.valueOf(obj[0]).substring(1));//PN
            } else {
                row.createCell(0).setCellValue(String.valueOf(obj[0])); //PN
            }
            if (spn_part_checkbox.isSelected()) {
                row.createCell(1).setCellValue(String.valueOf(obj[6]));
            } else {
                row.createCell(1).setCellValue("");
            }
            (row.createCell(2)).setCellStyle(intFormat);
            row.getCell(2).setCellValue((double) obj[1]); //QTY
            row.createCell(3).setCellValue(String.valueOf(obj[2]));//SEGMENT
            row.createCell(4).setCellValue(obj[3].toString());//WORKPLACE
            (row.createCell(5)).setCellStyle(doubleFormat);
            row.getCell(5).setCellValue((double) obj[4]);//STD TIME
            (row.createCell(6)).setCellStyle(doubleFormat);
            row.getCell(6).setCellValue((double) obj[5]);//PRODUCED HOURS

            total_produced = total_produced + Double.valueOf(obj[1].toString());
            total_produced_hours = total_produced_hours + Double.valueOf(String.valueOf(obj[5]));

            sheetPointer++;
        }

        //Total produced line
        row = sheet.createRow(sheetPointer++);
        row.createCell(0).setCellValue("TOTAL PRODUCED QTY :");
        row.createCell(1).setCellValue(total_produced);

        //Total produced hours
        row = sheet.createRow(sheetPointer++);
        row.createCell(0).setCellValue("TOTAL PRODUCED HOURS :");
        row.createCell(1).setCellValue(Double.valueOf(total_produced_hours));

        //Start date
        row = sheet.createRow(sheetPointer++);
        row.createCell(0).setCellValue("FROM : ");
        row.createCell(1).setCellValue(String.valueOf(startDateStr));

        //End date
        row = sheet.createRow(sheetPointer++);
        row.createCell(0).setCellValue("TO : ");
        row.createCell(1).setCellValue(String.valueOf(endDateStr));

        //Past the workbook to the file chooser
        new JDialogExcelFileChooser(null, true, wb).setVisible(true);
    }//GEN-LAST:event_export_btnActionPerformed

    private void workplace_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_workplace_filterItemStateChanged
    }//GEN-LAST:event_workplace_filterItemStateChanged

    private void workplace_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplace_filterActionPerformed
    }//GEN-LAST:event_workplace_filterActionPerformed

    private void segment_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_segment_filterItemStateChanged

    }//GEN-LAST:event_segment_filterItemStateChanged

    private void segment_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segment_filterActionPerformed
        String segment = String.valueOf(segment_filter.getSelectedItem()).trim();
        //this.workplace_filter.addItem(new ComboItem("ALL", "ALL"));
        this.workplace_filter.addItem("ALL");
        if ("ALL".equals(segment) || segment.equals("null")) {
            this.workplace_filter.setSelectedIndex(0);
            this.workplace_filter.setEnabled(false);

        } else {
            workplace_filter = ConfigWorkplace.initWorkplaceJBox(this, workplace_filter, segment, true);
            this.workplace_filter.setEnabled(true);
        }
    }//GEN-LAST:event_segment_filterActionPerformed

    private void radio_scanned_harnessItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_scanned_harnessItemStateChanged
//        refresh();
    }//GEN-LAST:event_radio_scanned_harnessItemStateChanged

    private void radio_closed_packItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_closed_packItemStateChanged
//        refresh();
    }//GEN-LAST:event_radio_closed_packItemStateChanged

    private void harness_part_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_harness_part_txtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_harness_part_txtKeyPressed

    private void project_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_project_filterItemStateChanged

    }//GEN-LAST:event_project_filterItemStateChanged


    private void project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_filterActionPerformed
        String project = String.valueOf(project_filter.getSelectedItem()).trim();
        ConfigSegment.setSegmentByProject(this, segment_filter, project, true);
    }//GEN-LAST:event_project_filterActionPerformed

    private void radio_closed_packActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_closed_packActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radio_closed_packActionPerformed

    private void radio_stored_packItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_stored_packItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_radio_stored_packItemStateChanged

    private void radio_stored_packActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_stored_packActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radio_stored_packActionPerformed

    private void harness_part_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_harness_part_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_harness_part_txtActionPerformed

    private void spn_part_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spn_part_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_spn_part_txtActionPerformed

    private void spn_part_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spn_part_txtKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_spn_part_txtKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable declared_result_table;
    private org.jdesktop.swingx.JXDatePicker endDatePicker;
    private javax.swing.JSpinner endTimeSpinner;
    private javax.swing.JButton export_btn;
    private javax.swing.JTextField harness_part_txt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel north_panel;
    private javax.swing.JComboBox project_filter;
    private javax.swing.JRadioButton radio_closed_pack;
    private javax.swing.JRadioButton radio_scanned_harness;
    private javax.swing.JRadioButton radio_stored_pack;
    private javax.swing.JButton refresh_btn;
    private javax.swing.JScrollPane result_table_scroll;
    private javax.swing.JComboBox segment_filter;
    private javax.swing.JCheckBox spn_part_checkbox;
    private javax.swing.JTextField spn_part_txt;
    private org.jdesktop.swingx.JXDatePicker startDatePicker;
    private javax.swing.JSpinner startTimeSpinner;
    private javax.swing.JTextField total_declared_lbl;
    private javax.swing.JTextField total_dropped_hours_lbl;
    private javax.swing.JTextField total_dropped_lbl;
    private javax.swing.JTextField total_produced_hours_lbl;
    private javax.swing.JComboBox workplace_filter;
    // End of variables declaration//GEN-END:variables
}
