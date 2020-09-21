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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
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
public class PACKAGING_UI0017_UCS_List_JPANEL extends javax.swing.JPanel {

    Vector ucs_result_table_data = new Vector();
    Vector<String> ucs_result_table_header = new Vector<String>();
    private List<Object[]> resultList;
    List<String> table_header = Arrays.asList(
            "SEGMENT",
            "WORKPLACE",
            "ARTICLE",
            "INDEX",
            "INT. PART NUMBER",
            "STD TIME",
            "PACK TYPE",
            "PACK SIZE",
            "ORDER NO.",
            "LIFES",
            "SPECIAL ORDER",
            "ACTIVE"
    );

    /**
     * Creates new form UI0011_ProdStatistics_
     */
    public PACKAGING_UI0017_UCS_List_JPANEL(java.awt.Frame parent, boolean modal) {
        initComponents();
        initGui();
    }

    private void initGui() {
        //Load table header
        load_table_header();

        //Init projects filter
        project_filter = ConfigProject.initProjectsJBox(this, project_filter, "ALL", true);
        this.workplace_filter.setEnabled(false);
        //Focuse on text filter field
        harness_part_filter.requestFocus();
    }

    private void load_table_header() {
        this.reset_table_content();

        for (Iterator<String> it = table_header.iterator(); it.hasNext();) {
            ucs_result_table_header.add(it.next());
        }
    }

    public void reset_table_content() {
        ucs_result_table_data = new Vector();
        ucs_result_table.setModel(new DefaultTableModel(ucs_result_table_data, ucs_result_table_header));
    }

    public void disableEditingTables() {
        for (int c = 0; c < ucs_result_table.getColumnCount(); c++) {
            // remove editor   
            Class<?> col_class = ucs_result_table.getColumnClass(c);
            ucs_result_table.setDefaultEditor(col_class, null);
        }
        ucs_result_table.setAutoCreateRowSorter(true);
    }

    @SuppressWarnings("empty-statement")
    public void reload_result_table_data(List<Object[]> resultList) {

        for (Object[] obj : resultList) {

            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(String.valueOf(obj[0])); // SEGMENT
            oneRow.add(String.valueOf(obj[1])); // WORKPLACE
            oneRow.add(String.valueOf(obj[2])); // "Harness Part"
            oneRow.add(String.valueOf(obj[3])); // "Harness Index"
            oneRow.add(String.valueOf(obj[4])); // "SPN"                     
            oneRow.add(String.valueOf(obj[5])); // "Std Time"
            oneRow.add(String.valueOf(obj[6])); // "Pack Type"
            oneRow.add(String.valueOf(obj[7])); // "Pack Size"
            oneRow.add(String.valueOf(obj[8])); // "ORDER NO"     
            oneRow.add(String.valueOf(obj[9])); // "LIFES"     
            oneRow.add(String.valueOf(obj[10])); // "SPECIAL ORDER"     
            oneRow.add(String.valueOf(obj[11])); // "ACTIVE"     

            ucs_result_table_data.add(oneRow);
        }
        ucs_result_table.setModel(new DefaultTableModel(ucs_result_table_data, ucs_result_table_header));
        ucs_result_table.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.BOLD, 12));
        ucs_result_table.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));
        //setContainerTableRowsStyle();
    }

    public void setContainerTableRowsStyle() {

        ucs_result_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, true, hasFocus, row, col);

                Integer status = Integer.valueOf(table.getModel().getValueAt(row, 10).toString());
                if (status == 1) {
                    setBackground(Color.YELLOW);
                    setForeground(Color.BLACK);

                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }

                ucs_result_table.setRowSelectionAllowed(true);
                ucs_result_table.setColumnSelectionAllowed(true);

                setHorizontalAlignment(JLabel.CENTER);

                return this;
            }
        });

    }

    private void refresh() {
        int special = 0;
        int active = 0;
        //Clear all tables
        this.reset_table_content();

        List<Object> segments = new ArrayList<Object>();
        List<Object> workplaces = new ArrayList<Object>();
        List<Object> projects = new ArrayList<Object>();
        segments.clear();
        workplaces.clear();
        projects.clear();
        
        String supplier_part_number = "%" + internal_pn_txt.getText().trim() + "%";

        //Populate the project Array with data
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
            projects.add(String.valueOf(project_filter.getSelectedItem()).trim());
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

        switch (special_combobox.getSelectedItem().toString()) {
            case "Special":
                special = 1;
                break;
            case "Normal":
                special = 0;
                break;
        }

        if (active_checkbox.isSelected() == true) {
            active = 1;
        } else {
            active = 0;
        }

        try {

            //################# UCS List #################### 
            Helper.startSession();

            String query_str = " SELECT "
                    + " bc.segment AS segment, "
                    + " bc.workplace AS workplace, "
                    + " bc.harness_part AS harness_part, "
                    + " bc.harness_index AS harness_index, "
                    + " bc.supplier_part_number AS spn,"
                    + " bc.std_time AS std_time, "
                    + " bc.pack_type AS pack_type, "
                    + " bc.pack_size AS pack_size, "
                    + " bc.order_no AS order_no, "
                    + " bc.lifes AS lifes, "
                    + " bc.special_order AS special_order, "
                    + " bc.active AS active "
                    + " FROM config_ucs bc "
                    + " WHERE 1=1 ";

            if (!projects.isEmpty()) {
                query_str += " AND bc.project IN (:projects) ";
            }
            if (!segments.isEmpty()) {
                query_str += " AND bc.segment IN (:segments) ";
            }
            if (!workplaces.isEmpty()) {
                query_str += " AND bc.workplace IN (:workplaces) ";
            }

            if (!harness_part_filter.getText().trim().isEmpty()) {
                query_str += " AND bc.harness_part like '%" + harness_part_filter.getText().trim() + "%'";
            }

            if (!special_combobox.getSelectedItem().toString().toUpperCase().equals("ALL")) {
                query_str += " AND bc.special_order = " + special + " ";
            }

            if (!projects.isEmpty()) {
                query_str += " AND bc.project IN (:projects) ";
            }

            query_str += " AND bc.active = " + active + " ";
            query_str += " AND bc.supplier_part_number like " + supplier_part_number + " ";

            query_str += " ORDER BY project ASC, segment ASC, workplace ASC, pack_type ASC, pack_size ASC";
            System.out.println(projects.toString());
            System.out.println(segments.toString());
            System.out.println(workplaces.toString());
            System.out.println(query_str);

            SQLQuery query = Helper.sess.createSQLQuery(query_str);

            query.addScalar("segment", StandardBasicTypes.STRING)
                    .addScalar("workplace", StandardBasicTypes.STRING)
                    .addScalar("harness_part", StandardBasicTypes.STRING)
                    .addScalar("harness_index", StandardBasicTypes.STRING)
                    .addScalar("supplier_part_number", StandardBasicTypes.STRING)
                    .addScalar("std_time", StandardBasicTypes.STRING)
                    .addScalar("pack_type", StandardBasicTypes.STRING)
                    .addScalar("pack_size", StandardBasicTypes.STRING)
                    .addScalar("spn", StandardBasicTypes.STRING)
                    .addScalar("order_no", StandardBasicTypes.STRING)
                    .addScalar("lifes", StandardBasicTypes.INTEGER)
                    .addScalar("special_order", StandardBasicTypes.INTEGER)
                    .addScalar("active", StandardBasicTypes.INTEGER);

            if (!projects.isEmpty()) {
                query.setParameterList("projects", projects);
            }
            if (!segments.isEmpty()) {
                query.setParameterList("segments", segments);
            }
            if (!workplaces.isEmpty()) {
                query.setParameterList("workplaces", workplaces);
            }

            resultList = query.list();

            Helper.sess.getTransaction().commit();

            this.reload_result_table_data(resultList);

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
        clear_btn = new javax.swing.JButton();
        segment_filter = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        ucs_result_table = new javax.swing.JTable();
        harness_part_filter = new javax.swing.JTextField();
        refresh_btn = new javax.swing.JButton();
        export_btn = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        workplace_filter = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        special_combobox = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        active_checkbox = new javax.swing.JCheckBox();
        jLabel24 = new javax.swing.JLabel();
        project_filter = new javax.swing.JComboBox();
        internal_pn_txt = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();

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

        clear_btn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        clear_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/edit-clear.png"))); // NOI18N
        clear_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_btnActionPerformed(evt);
            }
        });

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

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Unités de conditionnement standard");

        ucs_result_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        ucs_result_table.setColumnSelectionAllowed(true);
        ucs_result_table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ucs_result_table.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ucs_result_tableKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(ucs_result_table);

        harness_part_filter.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        harness_part_filter.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                harness_part_filterComponentShown(evt);
            }
        });
        harness_part_filter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                harness_part_filterKeyPressed(evt);
            }
        });

        refresh_btn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        refresh_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/filter-icon.png"))); // NOI18N
        refresh_btn.setText("Filtrer");
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

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Segment");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Workplace");

        workplace_filter.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        workplace_filter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL" }));
        workplace_filter.setToolTipText("");
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

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Article");

        special_combobox.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        special_combobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "Normal", "Special" }));
        special_combobox.setToolTipText("");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Type");

        active_checkbox.setForeground(new java.awt.Color(255, 255, 255));
        active_checkbox.setSelected(true);
        active_checkbox.setText("Active");
        active_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                active_checkboxActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Projet");

        project_filter.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        project_filter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL" }));
        project_filter.setMaximumSize(null);
        project_filter.setMinimumSize(new java.awt.Dimension(37, 26));
        project_filter.setPreferredSize(new java.awt.Dimension(37, 26));
        project_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                project_filterActionPerformed(evt);
            }
        });

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

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Code Article Interne");

        javax.swing.GroupLayout north_panelLayout = new javax.swing.GroupLayout(north_panel);
        north_panel.setLayout(north_panelLayout);
        north_panelLayout.setHorizontalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(clear_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(392, 392, 392)
                                .addComponent(export_btn))
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(north_panelLayout.createSequentialGroup()
                                        .addComponent(jLabel23)
                                        .addGap(196, 196, 196))
                                    .addGroup(north_panelLayout.createSequentialGroup()
                                        .addComponent(special_combobox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(16, 16, 16)))
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(harness_part_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22))
                                .addGap(18, 18, 18)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25)
                                    .addGroup(north_panelLayout.createSequentialGroup()
                                        .addComponent(internal_pn_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(active_checkbox, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel6)
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24)
                                    .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel21))))
                        .addContainerGap(530, Short.MAX_VALUE))))
        );
        north_panelLayout.setVerticalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel24)
                    .addComponent(jLabel21))
                .addGap(2, 2, 2)
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(special_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(harness_part_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(internal_pn_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(clear_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(active_checkbox)
                        .addGap(18, 19, Short.MAX_VALUE)
                        .addComponent(export_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 765, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 9, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        internal_pn_txt.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(north_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(north_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void clear_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_btnActionPerformed
        harness_part_filter.setText("");
        segment_filter.setSelectedIndex(0);
        refresh();
    }//GEN-LAST:event_clear_btnActionPerformed

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

    private void segment_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segment_filterActionPerformed
        
    }//GEN-LAST:event_segment_filterActionPerformed

    private void segment_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_segment_filterItemStateChanged

    }//GEN-LAST:event_segment_filterItemStateChanged


    private void refresh_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_btnActionPerformed
        refresh();
    }//GEN-LAST:event_refresh_btnActionPerformed

    private void harness_part_filterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_harness_part_filterKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_harness_part_filterKeyPressed

    private void export_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export_btnActionPerformed

        //Create the excel workbook
        Workbook wb = new XSSFWorkbook(); // new HSSFWorkbook();
        Sheet sheet = wb.createSheet("UCS List");
        CreationHelper createHelper = wb.getCreationHelper();

        //######################################################################
        //##################### SHEET 1 : PILES DETAILS ########################
        //Initialiser les entête du fichier
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow((short) 0);

        row.createCell(0).setCellValue("SEGMENT");
        row.createCell(1).setCellValue("WORKPLACE");
        row.createCell(2).setCellValue("PART NUMBER");
        row.createCell(3).setCellValue("INDEX");
        row.createCell(4).setCellValue("STD TIME");
        row.createCell(5).setCellValue("PACK TYPE");
        row.createCell(6).setCellValue("PACK SIZE");
        row.createCell(7).setCellValue("SPN");
        row.createCell(8).setCellValue("ORDER NO");
        row.createCell(9).setCellValue("TOTAL PLANNED PACK");
        row.createCell(10).setCellValue("SPECIAL ORDER");
        row.createCell(11).setCellValue("ACTIVE");

        short sheetPointer = 1;

        for (Object[] obj : this.resultList) {
            row = sheet.createRow(sheetPointer);
            row.createCell(0).setCellValue(String.valueOf(obj[0]));
            row.createCell(1).setCellValue(String.valueOf(obj[1]));
            row.createCell(2).setCellValue("" + String.valueOf(obj[2].toString()));
            row.createCell(3).setCellValue(String.valueOf(obj[3]));
            row.createCell(4).setCellValue(Double.valueOf(obj[4].toString()));
            row.createCell(5).setCellValue(String.valueOf(obj[5]));
            row.createCell(6).setCellValue(Integer.valueOf(obj[6].toString()));
            row.createCell(7).setCellValue(String.valueOf(obj[7]));
            try {
                row.createCell(8).setCellValue(Integer.valueOf(obj[8].toString()));
            } catch (Exception e) {
                row.createCell(8).setCellValue(0 + "");
            }
            row.createCell(9).setCellValue(Integer.valueOf(obj[9].toString()));
            row.createCell(10).setCellValue(Integer.valueOf(obj[10].toString()));
            row.createCell(11).setCellValue(Integer.valueOf(obj[11].toString()));
            sheetPointer++;
        }
        //Past the workbook to the file chooser
        new JDialogExcelFileChooser(null, true, wb).setVisible(true);
    }//GEN-LAST:event_export_btnActionPerformed

    private void ucs_result_tableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ucs_result_tableKeyPressed

    }//GEN-LAST:event_ucs_result_tableKeyPressed

    private void harness_part_filterComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_harness_part_filterComponentShown

    }//GEN-LAST:event_harness_part_filterComponentShown

    private void workplace_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_workplace_filterItemStateChanged

    }//GEN-LAST:event_workplace_filterItemStateChanged

    private void workplace_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplace_filterActionPerformed

    }//GEN-LAST:event_workplace_filterActionPerformed

    private void active_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_active_checkboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_active_checkboxActionPerformed


    private void project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_filterActionPerformed
       
    }//GEN-LAST:event_project_filterActionPerformed

    private void internal_pn_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_internal_pn_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_internal_pn_txtActionPerformed

    private void internal_pn_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_internal_pn_txtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_internal_pn_txtKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox active_checkbox;
    private javax.swing.JButton clear_btn;
    private javax.swing.JButton export_btn;
    private javax.swing.JTextField harness_part_filter;
    private javax.swing.JTextField internal_pn_txt;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel north_panel;
    private javax.swing.JComboBox project_filter;
    private javax.swing.JButton refresh_btn;
    private javax.swing.JComboBox segment_filter;
    private javax.swing.JComboBox special_combobox;
    private javax.swing.JTable ucs_result_table;
    private javax.swing.JComboBox workplace_filter;
    // End of variables declaration//GEN-END:variables
}
