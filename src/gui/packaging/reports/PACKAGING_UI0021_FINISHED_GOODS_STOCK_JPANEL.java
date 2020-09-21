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
import java.awt.ComponentOrientation;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JTabbedPane;
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
public class PACKAGING_UI0021_FINISHED_GOODS_STOCK_JPANEL extends javax.swing.JPanel {

    JTabbedPane parent;
    Vector<String> declared_result_table_header = new Vector<String>();
    Vector declared_result_table_data = new Vector();

    private List<Object[]> storedResultList;

    List<Object> segments = new ArrayList<>();
    List<Object> workplaces = new ArrayList<>();
    List<Object> projects = new ArrayList<>();

    SimpleDateFormat timeDf = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateDf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateTimeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String startTimeStr = "";
    String endTimeStr = "";
    String startDateStr = null;
    String endDateStr = null;
    String harness_part = "";
    String supplier_part_number = "";
    ButtonGroup radioGroup = new ButtonGroup();

    /**
     * Creates new form UI0011_ProdStatistics_
     */
    public PACKAGING_UI0021_FINISHED_GOODS_STOCK_JPANEL(JTabbedPane parent) {
        this.parent = parent;
        initComponents();
        
        project_filter = ConfigProject.initProjectsJBox(this, project_filter, "ALL", true);
        this.workplace_filter.setEnabled(false);
        this.reset_tables_content();
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
        declared_result_table_header.add("Ségment");
        declared_result_table_header.add("Workplace");
        declared_result_table_header.add("Article");
        declared_result_table_header.add("Code Interne");
        declared_result_table_header.add("Disponible");
        declared_result_table_header.add("Reservé");
        declared_result_table_header.add("Total");
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

        for (Object[] obj : resultList) {
            Vector<Object> oneRow = new Vector<Object>();
            //segment, workplace, harness_part, sum(qty_read) AS stored_qty
            oneRow.add(String.valueOf(obj[0])); //segment
            oneRow.add(String.valueOf(obj[1])); //workplace
            if (String.valueOf(obj[2]).startsWith("P")) {
                oneRow.add(String.valueOf(obj[2]).substring(1)); //harness_part
            } else {
                oneRow.add(String.valueOf(obj[2])); //harness_part
            }
            oneRow.add(String.valueOf(obj[3])); //Code Article Interne
            oneRow.add(("nu".equals(String.valueOf(String.format("%1$,.2f", obj[4])))) ? "0,00" : String.valueOf(String.format("%1$,.2f", obj[4]))); //RESERVED;
            oneRow.add(("nu".equals(String.valueOf(String.format("%1$,.2f", obj[5])))) ? "0,00" : String.valueOf(String.format("%1$,.2f", obj[5]))); //RESERVED;
            oneRow.add(("nu".equals(String.valueOf(String.format("%1$,.2f", obj[6])))) ? "0,00" : String.valueOf(String.format("%1$,.2f", obj[6]))); //TOTAL;
            declared_result_table_data.add(oneRow);
        }
        declared_result_table.setModel(new DefaultTableModel(declared_result_table_data, declared_result_table_header));
        //declared_result_table.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.BOLD, Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONTSIZE"))));
        declared_result_table.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));

    }

    private void refresh() {
        segments.clear();
        workplaces.clear();
        projects.clear();
        int special = 0;
        harness_part = "%" + cpn_txt.getText().trim() + "%";
        supplier_part_number = "%" + internal_pn_txt.getText().trim() + "%";
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
        if (String.valueOf(segment_filter.getSelectedItem()).equals("ALL")) {
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
            if (String.valueOf(workplace_filter.getSelectedItem()).equals("ALL") && !String.valueOf(segment_filter.getSelectedItem()).equals("null")) {
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

        switch (special_combobox.getSelectedItem().toString()) {
            case "Special":
                special = 1;
                break;
            case "Normal":
                special = 0;
                break;
        }

        try {
            //Clear all tables
            this.reset_tables_content();
            String query_str = "";
            //################# Declared Harness Data ####################        
            Helper.startSession();
            SQLQuery query;
            //if (radio_filled_ucs.isSelected()) { // UCS Complet
            //Request 1
            query_str = "SELECT segment, workplace, harness_part, supplier_part_number, "
                    + "SUM(CASE WHEN container_state IN ('CLOSED', 'STORED') THEN qty_read END) AS AVAILABLE, "
                    + "SUM(CASE WHEN container_state IN ('RESERVED') THEN qty_read END) AS RESERVED, "
                    + "SUM(CASE WHEN container_state IN ('CLOSED', 'STORED','RESERVED') THEN qty_read END) AS TOTAL "
                    + "FROM base_container "
                    + "WHERE container_state IN ('CLOSED', 'STORED', 'RESERVED') ";

            if (!segments.isEmpty()) {
                query_str += " AND segment IN (:segments) ";
            }
            if (!workplaces.isEmpty()) {
                query_str += " AND workplace IN (:workplaces) ";
            }
            if (!projects.isEmpty()) {
                query_str += " AND project IN (:projects) ";
            }

            if (!special_combobox.getSelectedItem().toString().toUpperCase().equals("ALL")) {
                query_str += " AND special_order = " + special + " ";
            }
            query_str += "AND harness_part like '%s' AND supplier_part_number like '%s' "
                    + "GROUP BY workplace, segment, harness_part, supplier_part_number "
                    + "ORDER BY workplace, segment, harness_part;";

            query_str = String.format(query_str, harness_part, supplier_part_number);

            //System.out.println("Stock available query_str "+query_str);
            //Select only harness parts with UCS completed.                                
            query = Helper.sess.createSQLQuery(query_str);

            query.addScalar("segment", StandardBasicTypes.STRING)
                    .addScalar("workplace", StandardBasicTypes.STRING)
                    .addScalar("harness_part", StandardBasicTypes.STRING)
                    .addScalar("supplier_part_number", StandardBasicTypes.STRING)
                    .addScalar("AVAILABLE", StandardBasicTypes.DOUBLE)
                    .addScalar("RESERVED", StandardBasicTypes.DOUBLE)
                    .addScalar("TOTAL", StandardBasicTypes.DOUBLE);

            if (!projects.isEmpty()) {
                query.setParameterList("projects", projects);
            }
            if (!segments.isEmpty()) {
                query.setParameterList("segments", segments);
            }
            if (!workplaces.isEmpty()) {
                query.setParameterList("workplaces", workplaces);
            }
            this.storedResultList = query.list();

            Helper.sess.getTransaction().commit();

            this.reload_declared_result_table_data(storedResultList);

            this.disableEditingTables();

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
        result_table_scroll = new javax.swing.JScrollPane();
        declared_result_table = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        project_filter = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        segment_filter = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        workplace_filter = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        cpn_txt = new javax.swing.JTextField();
        refresh_btn = new javax.swing.JButton();
        export_btn = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        internal_pn_txt = new javax.swing.JTextField();
        special_combobox = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();

        setForeground(new java.awt.Color(0, 153, 153));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        north_panel.setBackground(new java.awt.Color(36, 65, 86));
        north_panel.setForeground(new java.awt.Color(0, 153, 153));
        north_panel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                north_panelKeyPressed(evt);
            }
        });

        declared_result_table.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
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
        declared_result_table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        result_table_scroll.setViewportView(declared_result_table);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Stock produits finis");

        jPanel1.setBackground(new java.awt.Color(36, 65, 86));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Projet");

        project_filter.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        project_filter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL" }));
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

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Ségment");

        segment_filter.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
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

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Workplace");

        workplace_filter.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
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

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Article");

        cpn_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpn_txtActionPerformed(evt);
            }
        });
        cpn_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cpn_txtKeyPressed(evt);
            }
        });

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

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Code Article Interne");

        internal_pn_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                internal_pn_txtActionPerformed(evt);
            }
        });
        internal_pn_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                internal_pn_txtKeyPressed(evt);
            }
        });

        special_combobox.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        special_combobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Normal", "Special" }));

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Type");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel21)
                    .addComponent(jLabel25)
                    .addComponent(project_filter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(special_combobox, 0, 187, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cpn_txt)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(workplace_filter, 0, 223, Short.MAX_VALUE)
                    .addComponent(jLabel22)
                    .addComponent(internal_pn_txt)
                    .addComponent(jLabel24))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refresh_btn)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(266, 266, 266)
                        .addComponent(export_btn)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(31, 31, 31))
                    .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(export_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addGap(3, 3, 3)
                        .addComponent(special_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel24))
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cpn_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(internal_pn_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(4, 4, 4))
        );

        javax.swing.GroupLayout north_panelLayout = new javax.swing.GroupLayout(north_panel);
        north_panel.setLayout(north_panelLayout);
        north_panelLayout.setHorizontalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(result_table_scroll)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        north_panelLayout.setVerticalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(result_table_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(north_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(north_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        //Create the excel workbook
        Workbook wb = new XSSFWorkbook(); // new HSSFWorkbook();
        Sheet sheet = wb.createSheet("Finished goods");
        CreationHelper createHelper = wb.getCreationHelper();
        double total_available = 0;

        //######################################################################
        //##################### SHEET 1 : PILES DETAILS ########################
        //Initialiser les entête du fichier
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow((short) 0);

        row.createCell(0).setCellValue("SEGMENT");
        row.createCell(1).setCellValue("WORKPLACE");
        row.createCell(2).setCellValue("PART NUMBER");
        row.createCell(3).setCellValue("INTERNAL PN");
        row.createCell(4).setCellValue("AVAILABLE QTY");
        row.createCell(5).setCellValue("RESERVED QTY");
        row.createCell(6).setCellValue("TOTAL QTY");

        short sheetPointer = 1;

        for (Object[] obj : this.storedResultList) {

            row = sheet.createRow(sheetPointer);
            row.createCell(0).setCellValue(String.valueOf(obj[0])); //SEGMENT
            row.createCell(1).setCellValue(String.valueOf(obj[1])); //WORKPLACE
            if (String.valueOf(obj[2].toString()).startsWith("P")) {
                row.createCell(2).setCellValue(String.valueOf(obj[2]).substring(1));//PART NUMBER
            } else {
                row.createCell(2).setCellValue(String.valueOf(obj[2].toString()));//PART NUMBER
            }
            row.createCell(3).setCellValue(String.valueOf(obj[3])); //INTERNAL PART NUMBER
            try {
                row.createCell(4).setCellValue(Double.valueOf(obj[4].toString()));//AVAILABLE QUANTITY
                total_available += Double.valueOf(obj[4].toString());
            } catch (NullPointerException e) {
                row.createCell(4).setCellValue(0.00f);//RESERVED QTY                
            }

            try {
                row.createCell(5).setCellValue(Double.valueOf(obj[5].toString()));//RESERVED QTY                
            } catch (NullPointerException e) {
                row.createCell(5).setCellValue(0.00f);//RESERVED QTY                
            }

            row.createCell(6).setCellValue(Double.valueOf(obj[6].toString()));//TOTAL

            sheetPointer++;
        }

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
        System.out.println("Segment "+segment);
        System.out.println(segment.getClass());
        System.out.println("ALL == "+segment+"ALL".equals(segment));
        if ("ALL".equals(segment)|| segment == null || "null".equals(segment) || segment.isEmpty()) {
            System.out.println("IF condition validate "+segment);
            this.workplace_filter.removeAllItems();
            this.workplace_filter.addItem("ALL");
            this.workplace_filter.setSelectedIndex(0);
            this.workplace_filter.setEnabled(false);
        } else {
            System.out.println("ELSE condition validate "+segment);
            this.workplace_filter.removeAllItems();
            this.workplace_filter.setEnabled(true);
            this.workplace_filter = ConfigWorkplace.initWorkplaceJBox(this, workplace_filter, segment, true);
        }


    }//GEN-LAST:event_segment_filterActionPerformed

    private void cpn_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpn_txtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_cpn_txtKeyPressed

    private void project_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_project_filterItemStateChanged
    }//GEN-LAST:event_project_filterItemStateChanged

    private void project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_filterActionPerformed
            String project = String.valueOf(project_filter.getSelectedItem()).trim();
            
            if ("ALL".equals(project) || "null".equals(project) || project.isEmpty()) {
                this.segment_filter.removeAllItems();
                this.segment_filter.addItem("ALL");
                this.segment_filter.setSelectedIndex(0);
                this.segment_filter.setEnabled(false);
                
                this.workplace_filter.removeAllItems();
                this.workplace_filter.addItem("ALL");
                this.workplace_filter.setSelectedIndex(0);
                this.workplace_filter.setEnabled(false);
            } else {
                
                JComboBox box = ConfigSegment.setSegmentByProject(this, segment_filter, project, false);
                if (box.getItemCount() > 0) {                    
                    this.workplace_filter = ConfigWorkplace.initWorkplaceJBox(this, workplace_filter, String.valueOf(segment_filter.getSelectedItem()), true);
                }
                this.segment_filter.addItem("ALL");
                this.segment_filter.setSelectedItem("ALL");
                this.segment_filter.setEnabled(true);

            }
    }//GEN-LAST:event_project_filterActionPerformed

    private void cpn_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpn_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cpn_txtActionPerformed

    private void internal_pn_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_internal_pn_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_internal_pn_txtActionPerformed

    private void internal_pn_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_internal_pn_txtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_internal_pn_txtKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField cpn_txt;
    private javax.swing.JTable declared_result_table;
    private javax.swing.JButton export_btn;
    private javax.swing.JTextField internal_pn_txt;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel north_panel;
    private javax.swing.JComboBox project_filter;
    private javax.swing.JButton refresh_btn;
    private javax.swing.JScrollPane result_table_scroll;
    private javax.swing.JComboBox segment_filter;
    private javax.swing.JComboBox special_combobox;
    private javax.swing.JComboBox workplace_filter;
    // End of variables declaration//GEN-END:variables
}
