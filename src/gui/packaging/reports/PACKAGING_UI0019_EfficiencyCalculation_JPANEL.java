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
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
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
public class PACKAGING_UI0019_EfficiencyCalculation_JPANEL extends javax.swing.JPanel {

    Vector<String> declared_result_table_header = new Vector<String>();
    Vector declared_result_table_data = new Vector();

    Vector<String> dropped_result_table_header = new Vector<String>();
    Vector dropped_result_table_data = new Vector();

    private List<Object[]> declaredResultList;

    List<Object> segments = new ArrayList<Object>();
    List<Object> workplaces = new ArrayList<Object>();
    List<Object> projects = new ArrayList<Object>();

    SimpleDateFormat timeDf = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateDf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateTimeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String startTimeStr = "";
    String endTimeStr = "";
    String startDateStr = null;
    String endDateStr = null;

    int operators = 0;
    Double produced_hours = 0.00;
    Double worked_hours = 7.50;
    Double posted_hours = 0.00;
    Double efficiency = 0.00;

    ButtonGroup radioGroup = new ButtonGroup();

    DecimalFormat decimForm = new DecimalFormat("0.00");
    NumberFormat nf = NumberFormat.getInstance();

    /**
     * Creates new form UI0011_ProdStatistics_
     */
    public PACKAGING_UI0019_EfficiencyCalculation_JPANEL(java.awt.Frame parent, boolean modal) {
        //super(parent, modal);
        initComponents();
        initTimeSpinners();
        project_filter = ConfigProject.initProjectsJBox(this, project_filter, "", true);
        this.workplace_filter.setEnabled(false);
        radioGroup.add(radio_scanned_harness);
        radioGroup.add(radio_closed_pack);
        radioGroup.add(radio_stored_pack);
        operators_txt.setValue(1);
        this.reset_tables_content();
        //this.refresh();
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
            Logger.getLogger(PACKAGING_UI0019_EfficiencyCalculation_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
        }

        //################# End Time Spinner ######################
        endTimeSpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        try {
            endTimeSpinner.setValue(timeFormat.parse(endTime));
        } catch (ParseException ex) {
            Logger.getLogger(PACKAGING_UI0019_EfficiencyCalculation_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
        }

        datePicker.setDate(new Date());

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
        declared_result_table_header.add("Segment");
        declared_result_table_header.add("Workplace");
        declared_result_table_header.add("Part number");
        declared_result_table_header.add("Std Time (Hours)");
        declared_result_table_header.add("Produced qty");
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

        double total_produced_qty = 0;
        //Set declared hours labels values
        produced_hours = 0.00;
        this.produced_hours_txt.setText("" + produced_hours);

        for (Object[] obj : resultList) {
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(String.valueOf(obj[0])); //segment
            oneRow.add(String.valueOf(obj[1])); //workplace            
            if (String.valueOf(obj[2]).startsWith(GlobalVars.HARN_PART_PREFIX)) {
                oneRow.add(String.valueOf(obj[2]).substring(1)); //harness_part
            } else {
                oneRow.add(String.valueOf(obj[2])); //harness_part
            }
            //oneRow.add(String.valueOf(String.format("%1$,.2f", obj[3]))); //std_time            
            //oneRow.add(String.valueOf(String.format("%1$,.2f", obj[4]))); //produced_qty;

            oneRow.add(decimForm.format(Double.parseDouble(obj[3].toString()))); //std_time    
            oneRow.add(decimForm.format(Double.parseDouble(obj[4].toString()))); //produced_qty;

            oneRow.add(decimForm.format(Double.valueOf(String.valueOf(obj[5])))); //produced_hours
            total_produced_qty = total_produced_qty + Double.valueOf(String.valueOf(obj[4]));
            produced_hours = produced_hours + Double.valueOf(obj[5].toString());
            declared_result_table_data.add(oneRow);
        }
        declared_result_table.setModel(new DefaultTableModel(declared_result_table_data, declared_result_table_header));
        declared_result_table.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.BOLD, Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONTSIZE"))));
        declared_result_table.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));

        //Set declared qty labels values
        this.produced_qty_txt.setText(String.valueOf(total_produced_qty));

        //Set declared hours labels values
        this.produced_hours_txt.setText("" + produced_hours);

    }

    private void refresh() {
        String query_str = "";
        segments.clear();
        workplaces.clear();
        projects.clear();

        startTimeStr = timeDf.format(startTimeSpinner.getValue());
        endTimeStr = timeDf.format(endTimeSpinner.getValue());
        startDateStr = dateDf.format(datePicker.getDate()) + " " + startTimeStr;
        endDateStr = dateDf.format(datePicker.getDate()) + " " + endTimeStr;
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
        //Populate the segments Array with data
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

            //################# Declared Harness Data ####################        
            Helper.startSession();

            if (!radio_scanned_harness.isSelected()) { // UCS Complet
                //Request 1
                query_str = "(SELECT bc.segment AS segment,"
                        + " bc.workplace AS workplace,"
                        + " bc.harness_part AS harness_part,"
                        + " bc.std_time AS std_time,"
                        + " SUM(bc.qty_read) AS produced_qty,"
                        + " bc.std_time*SUM(bc.qty_read) AS produced_hours"
                        + " FROM base_container bc "
                        + " WHERE ";
                if (radio_closed_pack.isSelected()) {
                    query_str += " (bc.closed_time BETWEEN '%s' AND '%s')";
                } else if (radio_stored_pack.isSelected()) {
                    query_str += " (bc.stored_time BETWEEN '%s' AND '%s')";
                }
                if (!segments.isEmpty()) {
                    query_str += " AND bc.segment IN (:segments) ";
                }
                if (!workplaces.isEmpty()) {
                    query_str += " AND bc.workplace IN (:workplaces) ";
                }
                if (!projects.isEmpty()) {
                    query_str += " AND bc.project IN (:projects) ";
                }

                query_str = String.format(query_str, startDateStr, endDateStr);
                query_str += "GROUP BY bc.harness_part, bc.segment, bc.workplace, bc.std_time "
                        + "ORDER BY bc.harness_part ASC, bc.segment ASC, bc.workplace ASC)";

            } else {
                //Request 2
                query_str = " SELECT  bh.segment AS segment,"
                        + " bh.workplace AS workplace,"
                        + " bh.harness_part AS harness_part,"
                        + " bh.std_time AS std_time,"
                        + " COUNT(bh.harness_part) AS produced_qty,"
                        + " SUM(bh.std_time) AS produced_hours "
                        + " FROM base_harness bh, base_container bc "
                        + " WHERE bc.id = bh.container_id "
                        + " AND bh.create_time BETWEEN '%s' AND '%s'";

                if (!segments.isEmpty()) {
                    query_str += " AND bc.segment IN (:segments) ";
                }
                if (!workplaces.isEmpty()) {
                    query_str += " AND bc.workplace IN (:workplaces) ";
                }
                if (!projects.isEmpty()) {
                    query_str += " AND bc.project IN (:projects) ";
                }

                query_str = String.format(query_str, startDateStr, endDateStr);
                query_str += " GROUP BY bh.segment, bh.workplace, bh.harness_part, bh.std_time  "
                        + " ORDER BY bh.segment ASC,bh.workplace ASC, bh.harness_part ASC";
            }

            query_str = String.format(query_str, startDateStr, endDateStr);

            SQLQuery query = Helper.sess.createSQLQuery(query_str);

            query.addScalar("segment", StandardBasicTypes.STRING)
                    .addScalar("workplace", StandardBasicTypes.STRING)
                    .addScalar("harness_part", StandardBasicTypes.STRING)
                    .addScalar("std_time", StandardBasicTypes.DOUBLE)
                    .addScalar("produced_qty", StandardBasicTypes.DOUBLE)
                    .addScalar("produced_hours", StandardBasicTypes.DOUBLE);

            if (!projects.isEmpty()) {
                query.setParameterList("projects", projects);
            }
            if (!segments.isEmpty()) {
                query.setParameterList("segments", segments);
            }
            if (!workplaces.isEmpty()) {
                query.setParameterList("workplaces", workplaces);
            }

            this.declaredResultList = query.list();

            Helper.sess.getTransaction().commit();

            this.reload_declared_result_table_data(declaredResultList);

            this.disableEditingTables();

            //Calculate efficiency                
            operators = Integer.valueOf(operators_txt.getValue().toString());
            worked_hours = Double.valueOf(worked_hours_txt.getText().toString().replace(",", "."));
            //worked_hours = Double.valueOf(String.valueOf(String.format("%1$,.2f", worked_hours_txt.getText())));
            posted_hours = operators * worked_hours;
            //Efficiency                    
            efficiency = (produced_hours / posted_hours) * 100;

            this.posted_hours_txt.setText(decimForm.format(posted_hours));
            this.posted_hours_txt2.setText(decimForm.format(posted_hours));
            this.efficiency_txt.setText(decimForm.format(efficiency));
            this.produced_hours_txt.setText(decimForm.format(produced_hours));

        } catch (HibernateException e) {
            if (Helper.sess.getTransaction() != null) {
                Helper.sess.getTransaction().rollback();
            }
            e.printStackTrace();
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
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        jLabel1 = new javax.swing.JLabel();
        startTimeSpinner = new javax.swing.JSpinner();
        endTimeSpinner = new javax.swing.JSpinner();
        result_table_scroll = new javax.swing.JScrollPane();
        declared_result_table = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        worked_hours_txt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        operators_txt = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        posted_hours_txt2 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        radio_scanned_harness = new javax.swing.JRadioButton();
        radio_stored_pack = new javax.swing.JRadioButton();
        radio_closed_pack = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        project_filter = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        segment_filter = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        workplace_filter = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        efficiency_txt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        posted_hours_txt = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        produced_qty_txt = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        produced_hours_txt = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        refresh_btn = new javax.swing.JButton();
        export_btn = new javax.swing.JButton();

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Du");

        startTimeSpinner.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        startTimeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                startTimeSpinnerStateChanged(evt);
            }
        });
        startTimeSpinner.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                startTimeSpinnerFocusLost(evt);
            }
        });

        endTimeSpinner.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        endTimeSpinner.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                endTimeSpinnerFocusLost(evt);
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
        declared_result_table.setCellSelectionEnabled(true);
        declared_result_table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        declared_result_table.setUpdateSelectionOnSort(false);
        result_table_scroll.setViewportView(declared_result_table);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Calcul de l'Efficience");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Effectif");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Heures travailées");

        worked_hours_txt.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        worked_hours_txt.setText("7.5");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Au");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Jour");

        operators_txt.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        operators_txt.setVerifyInputWhenFocusTarget(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("x");

        posted_hours_txt2.setEditable(false);
        posted_hours_txt2.setBackground(new java.awt.Color(153, 255, 255));
        posted_hours_txt2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        posted_hours_txt2.setText("0.00");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Heures postées");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("=");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("=");

        jPanel1.setBackground(new java.awt.Color(36, 65, 86));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        radio_scanned_harness.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        radio_scanned_harness.setForeground(new java.awt.Color(255, 255, 255));
        radio_scanned_harness.setSelected(true);
        radio_scanned_harness.setText("Total par pièces scannées");
        radio_scanned_harness.setToolTipText("<p>Le total des pièces scannées au niveau poste packaging. Prend en considération les 3 états de la palette :</p><br/>\n<p>- Ouverte (OPEN) \t: Palette ouverte au niveau poste packaging, UCS incomplet.</p><br/>\n<p>- Fermé    (CLOSED)\t: Palette fermée mais pas encore entrée au magasin produit fini., UCS complet.</p><br/>\n<p>- Stockée (STORED)\t: Palette stockée au niveau magasin produit fini, UCS complet.</p><br/>");
        radio_scanned_harness.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_scanned_harnessItemStateChanged(evt);
            }
        });

        radio_stored_pack.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        radio_stored_pack.setForeground(new java.awt.Color(255, 255, 255));
        radio_stored_pack.setText("Palettes stockées (STORED)");
        radio_stored_pack.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_stored_packItemStateChanged(evt);
            }
        });

        radio_closed_pack.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        radio_closed_pack.setForeground(new java.awt.Color(255, 255, 255));
        radio_closed_pack.setText("Palettes fermées (CLOSED)");
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(radio_closed_pack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(radio_scanned_harness, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(radio_stored_pack, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radio_scanned_harness)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_closed_pack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_stored_pack)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(36, 65, 86));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
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

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Segment");

        segment_filter.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        segment_filter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL" }));
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

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Workplace");

        workplace_filter.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        workplace_filter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL" }));
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

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 51));
        jLabel17.setText("Efficience %");

        efficiency_txt.setEditable(false);
        efficiency_txt.setBackground(new java.awt.Color(102, 255, 102));
        efficiency_txt.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        efficiency_txt.setText("0.00");
        efficiency_txt.setMinimumSize(new java.awt.Dimension(15, 29));
        efficiency_txt.setPreferredSize(new java.awt.Dimension(15, 29));
        efficiency_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                efficiency_txtActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("=");

        posted_hours_txt.setEditable(false);
        posted_hours_txt.setBackground(new java.awt.Color(153, 255, 255));
        posted_hours_txt.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        posted_hours_txt.setText("0.00");
        posted_hours_txt.setMinimumSize(new java.awt.Dimension(15, 29));
        posted_hours_txt.setPreferredSize(new java.awt.Dimension(15, 29));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Heures postées");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("/");

        produced_qty_txt.setEditable(false);
        produced_qty_txt.setBackground(new java.awt.Color(153, 255, 255));
        produced_qty_txt.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        produced_qty_txt.setText("0.00");
        produced_qty_txt.setMinimumSize(new java.awt.Dimension(15, 29));
        produced_qty_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                produced_qty_txtActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Σ Pièces");

        produced_hours_txt.setEditable(false);
        produced_hours_txt.setBackground(new java.awt.Color(153, 255, 255));
        produced_hours_txt.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        produced_hours_txt.setText("0.00");
        produced_hours_txt.setMinimumSize(new java.awt.Dimension(15, 29));
        produced_hours_txt.setPreferredSize(new java.awt.Dimension(15, 29));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Σ Heures produites");

        refresh_btn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        refresh_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/refresh.png"))); // NOI18N
        refresh_btn.setText("Actualiser");
        refresh_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_btnActionPerformed(evt);
            }
        });

        export_btn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        export_btn.setText("Exporter en Excel...");
        export_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                export_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(produced_qty_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(produced_hours_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(posted_hours_txt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(66, 66, 66)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(efficiency_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(export_btn)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(29, 29, 29))
                    .addComponent(project_filter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(28, 28, 28))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(produced_qty_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel22)
                            .addGap(26, 26, 26))))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(produced_hours_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(posted_hours_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(efficiency_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(export_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout north_panelLayout = new javax.swing.GroupLayout(north_panel);
        north_panel.setLayout(north_panelLayout);
        north_panelLayout.setHorizontalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(result_table_scroll, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(281, 281, 281))
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(north_panelLayout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(193, 193, 193)
                                        .addComponent(jLabel1)
                                        .addGap(73, 73, 73)
                                        .addComponent(jLabel3))
                                    .addGroup(north_panelLayout.createSequentialGroup()
                                        .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(startTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(endTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addGroup(north_panelLayout.createSequentialGroup()
                                        .addComponent(worked_hours_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel8)))
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(north_panelLayout.createSequentialGroup()
                                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(north_panelLayout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(jLabel13))
                                            .addGroup(north_panelLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(operators_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel12)))
                                        .addGap(12, 12, 12)
                                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(posted_hours_txt2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel21)))
                                    .addGroup(north_panelLayout.createSequentialGroup()
                                        .addGap(197, 197, 197)
                                        .addComponent(jLabel10)))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        north_panelLayout.setVerticalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addGap(69, 69, 69)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel14))
                                .addGap(7, 7, 7))
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(operators_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(posted_hours_txt2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(worked_hours_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(endTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(startTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(north_panelLayout.createSequentialGroup()
                            .addGap(70, 70, 70)
                            .addComponent(jLabel1))
                        .addGroup(javax.swing.GroupLayout.Alignment.CENTER, north_panelLayout.createSequentialGroup()
                            .addGap(115, 115, 115)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(north_panelLayout.createSequentialGroup()
                            .addGap(92, 92, 92)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(result_table_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(388, 388, 388))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(north_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(north_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 793, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.setVisible(false);
        }
    }//GEN-LAST:event_formKeyPressed

    private void north_panelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_north_panelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            //this.dispose();
            this.setVisible(false);
        }
    }//GEN-LAST:event_north_panelKeyPressed

    private void segment_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segment_filterActionPerformed
        String segment = String.valueOf(segment_filter.getSelectedItem()).trim();
        this.workplace_filter.removeAllItems();
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

    private void segment_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_segment_filterItemStateChanged

    }//GEN-LAST:event_segment_filterItemStateChanged

    private void workplace_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplace_filterActionPerformed

    }//GEN-LAST:event_workplace_filterActionPerformed

    private void workplace_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_workplace_filterItemStateChanged
    }//GEN-LAST:event_workplace_filterItemStateChanged

    private void radio_scanned_harnessItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_scanned_harnessItemStateChanged
    }//GEN-LAST:event_radio_scanned_harnessItemStateChanged

    private void radio_stored_packItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_stored_packItemStateChanged
    }//GEN-LAST:event_radio_stored_packItemStateChanged

    private void export_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export_btnActionPerformed
        try {
            startTimeStr = timeDf.format(startTimeSpinner.getValue());
            endTimeStr = timeDf.format(endTimeSpinner.getValue());
            startDateStr = dateDf.format(datePicker.getDate()) + " " + startTimeStr;
            endDateStr = dateDf.format(datePicker.getDate()) + " " + endTimeStr;

            //Create the excel workbook
            Workbook wb = new XSSFWorkbook(); // new HSSFWorkbook();
            Sheet sheet = null;
            if (radio_scanned_harness.isSelected()) {
                sheet = wb.createSheet("SCANNED EFFICIENCY");
            } else if (radio_closed_pack.isSelected()) {
                sheet = wb.createSheet("CLOSED EFFICIENCY");
            } else {
                sheet = wb.createSheet("STORED EFFICIENCY");
            }

            CreationHelper createHelper = wb.getCreationHelper();
            double total_qty = 0;

            //######################################################################
            //##################### SHEET 1 : PILES DETAILS ########################
            //Initialiser les entête du fichier
            // Create a row and put some cells in it. Rows are 0 based.
            Row row = sheet.createRow((short) 0);

            row.createCell(0).setCellValue("SEGMENT");
            row.createCell(1).setCellValue("WORKPLACE");
            row.createCell(2).setCellValue("PART NUMBER");
            row.createCell(3).setCellValue("STD TIME");
            row.createCell(4).setCellValue("PRODUCED QTY");
            row.createCell(5).setCellValue("PRODUCED HOURS");

            short sheetPointer = 1;

            for (Object[] obj : this.declaredResultList) {
                row = sheet.createRow(sheetPointer);
                row.createCell(0).setCellValue(String.valueOf(obj[0])); //SEGMENT
                row.createCell(1).setCellValue(String.valueOf(obj[1])); //WORKPLACE
                if (String.valueOf(obj[2]).startsWith(GlobalVars.HARN_PART_PREFIX)) {
                    row.createCell(2).setCellValue(String.valueOf(obj[2]).substring(1));//PART NUMBER
                } else {
                    row.createCell(2).setCellValue(String.valueOf(obj[2]));//PART NUMBER
                }
                row.createCell(3).setCellValue(Double.valueOf(obj[3].toString()));//STD TIME
                row.createCell(4).setCellValue(Double.valueOf(obj[4].toString()));//PRODUCED QTY
                row.createCell(5).setCellValue(Double.valueOf(obj[5].toString()));//PRODUCED HOURS

                total_qty = total_qty + Double.valueOf(String.valueOf(obj[4]));

                sheetPointer++;
            }

            //Segment
            row = sheet.createRow(sheetPointer++);
            row.createCell(0).setCellValue("SEGMENT : ");
            row.createCell(1).setCellValue(String.valueOf(segment_filter.getSelectedItem()));

            //Workplace
            row = sheet.createRow(sheetPointer++);
            row.createCell(0).setCellValue("WORKPLACE : ");
            row.createCell(1).setCellValue(String.valueOf(workplace_filter.getSelectedItem()));

            //Start date
            row = sheet.createRow(sheetPointer++);
            row.createCell(0).setCellValue("FROM : ");
            row.createCell(1).setCellValue(String.valueOf(startDateStr));

            //End date
            row = sheet.createRow(sheetPointer++);
            row.createCell(0).setCellValue("TO : ");
            row.createCell(1).setCellValue(String.valueOf(endDateStr));

            //Worked Hours
            row = sheet.createRow(sheetPointer++);
            row.createCell(0).setCellValue("WORKED HOURS : ");
            row.createCell(1).setCellValue(nf.parse(worked_hours_txt.getText()).doubleValue());

            //Nbre Operators
            row = sheet.createRow(sheetPointer++);
            row.createCell(0).setCellValue("OPERATORS : ");
            row.createCell(1).setCellValue(String.valueOf(operators_txt.getValue()));

            //Posted Hours
            row = sheet.createRow(sheetPointer++);
            row.createCell(0).setCellValue("POSTED HOURS : ");
            row.createCell(1).setCellValue(nf.parse(posted_hours_txt.getText()).doubleValue());

            //Total produced qty
            row = sheet.createRow(sheetPointer++);
            row.createCell(0).setCellValue("TOTAL PRODUCED QTY :");
            row.createCell(1).setCellValue(total_qty);

            //Total produced hours
            row = sheet.createRow(sheetPointer++);
            row.createCell(0).setCellValue("TOTAL PRODUCED HOURS :");
            row.createCell(1).setCellValue(nf.parse(produced_hours_txt.getText()).doubleValue());

            //Total produced hours
            row = sheet.createRow(sheetPointer++);
            row.createCell(0).setCellValue("EFFICIENCY :");
            row.createCell(1).setCellValue(nf.parse(efficiency_txt.getText()).doubleValue());

            //Past the workbook to the file chooser
            new JDialogExcelFileChooser((Frame) super.getParent(), true, wb).setVisible(true);
        } catch (ParseException ex) {
            Logger.getLogger(PACKAGING_UI0019_EfficiencyCalculation_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_export_btnActionPerformed

    private void refresh_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_btnActionPerformed
        refresh();
    }//GEN-LAST:event_refresh_btnActionPerformed

    private void efficiency_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_efficiency_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_efficiency_txtActionPerformed

    private void produced_qty_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_produced_qty_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_produced_qty_txtActionPerformed

    private void startTimeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_startTimeSpinnerStateChanged

    }//GEN-LAST:event_startTimeSpinnerStateChanged

    private void startTimeSpinnerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_startTimeSpinnerFocusLost
        //updateHours();
    }//GEN-LAST:event_startTimeSpinnerFocusLost

    private void endTimeSpinnerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_endTimeSpinnerFocusLost
        //updateHours();
    }//GEN-LAST:event_endTimeSpinnerFocusLost

    private void project_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_project_filterItemStateChanged

    }//GEN-LAST:event_project_filterItemStateChanged


    private void project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_filterActionPerformed
        String project = String.valueOf(project_filter.getSelectedItem()).trim();
        ConfigSegment.setSegmentByProject(this, segment_filter, project, true);
    }//GEN-LAST:event_project_filterActionPerformed

    private void radio_closed_packItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_closed_packItemStateChanged
        //        refresh();
    }//GEN-LAST:event_radio_closed_packItemStateChanged

    private void radio_closed_packActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_closed_packActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radio_closed_packActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private javax.swing.JTable declared_result_table;
    private javax.swing.JTextField efficiency_txt;
    private javax.swing.JSpinner endTimeSpinner;
    private javax.swing.JButton export_btn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel north_panel;
    private javax.swing.JSpinner operators_txt;
    private javax.swing.JTextField posted_hours_txt;
    private javax.swing.JTextField posted_hours_txt2;
    private javax.swing.JTextField produced_hours_txt;
    private javax.swing.JTextField produced_qty_txt;
    private javax.swing.JComboBox project_filter;
    private javax.swing.JRadioButton radio_closed_pack;
    private javax.swing.JRadioButton radio_scanned_harness;
    private javax.swing.JRadioButton radio_stored_pack;
    private javax.swing.JButton refresh_btn;
    private javax.swing.JScrollPane result_table_scroll;
    private javax.swing.JComboBox segment_filter;
    private javax.swing.JSpinner startTimeSpinner;
    private javax.swing.JTextField worked_hours_txt;
    private javax.swing.JComboBox workplace_filter;
    // End of variables declaration//GEN-END:variables
}
