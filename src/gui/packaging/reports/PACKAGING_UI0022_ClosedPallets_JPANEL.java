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
import gui.packaging.PackagingVars;
import helper.Helper;
import helper.JTableHelper;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 *
 * @author Administrator
 */
public class PACKAGING_UI0022_ClosedPallets_JPANEL extends javax.swing.JPanel {

    static JTabbedPane parent;

    public JTabbedPane getParent() {
        return parent;
    }

    public void setParent(JTabbedPane parent) {
        this.parent = parent;
    }

    /* "Pallet number" Column index in "container_table" */
    private static int PALLET_NUMBER_COLINDEX = 1;
    Vector<String> closed_result_table_header = new Vector<String>();
    Vector closed_result_table_data = new Vector();
    List<String> table_header = Arrays.asList(
            "Segment",
            "Workplace",
            "Pack Number",
            "CPN",
            "Pack Type",
            "Pack Size",
            "Qty Read",
            "Index",
            "User",
            "Create time",
            "Closed time"
    );

    List<Object> segments = new ArrayList<Object>();
    List<Object> workplaces = new ArrayList<Object>();
    List<Object> projects = new ArrayList<Object>();

    /**
     * Creates new form UI0011_ProdStatistics_
     */
    public PACKAGING_UI0022_ClosedPallets_JPANEL(JTabbedPane parent) {
        //super(parent, modal);
        initComponents();
        this.initGui(parent);
        this.refresh();
//        Helper.centerJDialog(this);
    }

    private void initGui(JTabbedPane parent) {
        this.parent = parent;

        //Desable table edition
        disableEditingTables();

        //Load table header
        load_table_header();

        //Init projects filter
        project_filter = ConfigProject.initProjectsJBox(this, project_filter, "", true);
        this.workplace_filter.setEnabled(false);

        //Initialize double clique on table row
        this.initContainerTableDoubleClick();

    }

    private void initContainerTableDoubleClick() {
        this.closed_result_table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN || PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT) {
                        new PACKAGING_UI0010_PalletDetails_JPANEL(parent, String.valueOf(closed_result_table.getValueAt(closed_result_table.getSelectedRow(), PALLET_NUMBER_COLINDEX)), "", 1, true, true, true).setVisible(true);
                    } else {
                        new PACKAGING_UI0010_PalletDetails_JPANEL(parent, String.valueOf(closed_result_table.getValueAt(closed_result_table.getSelectedRow(), PALLET_NUMBER_COLINDEX)), "", 1, false, false, false).setVisible(true);
                    }
                }
            }
        }
        );
    }

    private void load_table_header() {
        this.reset_table_content();

        for (Iterator<String> it = table_header.iterator(); it.hasNext();) {
            closed_result_table_header.add(it.next());
        }

        closed_result_table.setModel(new DefaultTableModel(closed_result_table_data, closed_result_table_header));
    }

    public void reset_table_content() {
        closed_result_table_data = new Vector();
        DefaultTableModel openDataModel = new DefaultTableModel(closed_result_table_data, closed_result_table_header);
        closed_result_table.setModel(openDataModel);
    }

    public void disableEditingTables() {
        for (int c = 0; c < closed_result_table.getColumnCount(); c++) {
            // remove editor   
            Class<?> col_class2 = closed_result_table.getColumnClass(c);
            closed_result_table.setDefaultEditor(col_class2, null);
        }
    }

    @SuppressWarnings("empty-statement")
    public void reload_closed_result_table_data(List<Object[]> resultList) {

        for (Object[] obj : resultList) {

            Vector<Object> oneRow = new Vector<Object>();

            oneRow.add(String.valueOf(obj[0])); // Segment
            oneRow.add(String.valueOf(obj[1])); // Workplace
            oneRow.add(String.valueOf(obj[2])); // Pallet Number
            oneRow.add(String.valueOf(obj[3])); // Harness Part                        
            oneRow.add(String.valueOf(obj[4])); // Pack Type
            oneRow.add(String.valueOf(obj[5])); // Qty Expected
            oneRow.add(String.valueOf(obj[6])); // Qty Read
            oneRow.add(String.valueOf(obj[7])); // Index
            oneRow.add(String.valueOf(obj[8])); // USER            
            oneRow.add(String.valueOf(obj[9])); // Create Time
            oneRow.add(String.valueOf(obj[10])); // ClosedTime
            closed_result_table_data.add(oneRow);
        }
        closed_result_table.setModel(new DefaultTableModel(closed_result_table_data, closed_result_table_header));
        closed_result_table.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.BOLD, Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONTSIZE"))));
        closed_result_table.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));
    }

    private void refresh() {

        //Clear all tables
        this.reset_table_content();

        segments.clear();
        workplaces.clear();
        projects.clear();

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

        try {

            //################# Dropped Harness Data #################### 
            Helper.startSession();
            String query_str = " SELECT "
                    + " bc.segment AS segment, "
                    + " bc.workplace AS workplace, "
                    + " bc.pallet_number AS pack_number, "
                    + " bc.harness_part AS harness_part, "
                    + " bc.pack_type AS pack_type, "
                    + " bc.qty_expected AS pack_size, "
                    + " bc.qty_read AS qty_read, "
                    + " bc.harness_index AS index, "
                    + " bc.m_user AS user, "
                    + " bc.create_time as create_time, "
                    + " bc.closed_time as closed_time, "
                    + " DATE_PART('hours', closed_time - create_time ) AS working_hours "
                    + " FROM base_container bc "
                    + " WHERE bc.container_state = '%s' ";

            if (!projects.isEmpty()) {
                query_str += " AND bc.project IN (:projects) ";
                System.out.println("projects " + projects.size());
            }
            if (!segments.isEmpty()) {
                query_str += " AND bc.segment IN (:segments) ";
            }
            if (!workplaces.isEmpty()) {
                query_str += " AND bc.workplace IN (:workplaces) ";
            }

            //Add harness part filter                
            if (!filter_harness_part_txt.getText().isEmpty()) {
                query_str += " AND harness_part LIKE :hp";
            }

            query_str += " ORDER BY project, segment, workplace, harness_part ASC ";

            query_str = String.format(query_str, GlobalVars.PALLET_CLOSED);
            SQLQuery query = Helper.sess.createSQLQuery(query_str);

            query.addScalar("segment", StandardBasicTypes.STRING)
                    .addScalar("workplace", StandardBasicTypes.STRING)
                    .addScalar("pack_number", StandardBasicTypes.STRING)
                    .addScalar("harness_part", StandardBasicTypes.STRING)
                    .addScalar("pack_type", StandardBasicTypes.STRING)
                    .addScalar("pack_size", StandardBasicTypes.INTEGER)
                    .addScalar("qty_read", StandardBasicTypes.INTEGER)
                    .addScalar("index", StandardBasicTypes.STRING)
                    .addScalar("user", StandardBasicTypes.STRING)
                    .addScalar("create_time", StandardBasicTypes.TIMESTAMP)
                    .addScalar("closed_time", StandardBasicTypes.TIMESTAMP)
                    .addScalar("working_hours", StandardBasicTypes.DOUBLE);
            //.setParameterList("projects", projects);

            if (!projects.isEmpty()) {
                query.setParameterList("projects", projects);
            }
            if (!segments.isEmpty()) {
                query.setParameterList("segments", segments);
            }
            if (!workplaces.isEmpty()) {
                query.setParameterList("workplaces", workplaces);
            }

            if (!filter_harness_part_txt.getText().isEmpty()) {
                query.setParameter("hp", "%" + filter_harness_part_txt.getText() + "%");
            }

            List<Object[]> closedResultList = query.list();

            Helper.sess.getTransaction().commit();

            this.reload_closed_result_table_data(closedResultList);

            this.disableEditingTables();
            JTableHelper.sizeColumnsToFit(closed_result_table);

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
        refresh_btn = new javax.swing.JButton();
        result_table_scroll = new javax.swing.JScrollPane();
        closed_result_table = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel23 = new javax.swing.JLabel();
        project_filter = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        segment_filter = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        workplace_filter = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        filter_harness_part_txt = new javax.swing.JTextField();

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

        refresh_btn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        refresh_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/refresh.png"))); // NOI18N
        refresh_btn.setText("Actualiser");
        refresh_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_btnActionPerformed(evt);
            }
        });

        closed_result_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        closed_result_table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        result_table_scroll.setViewportView(closed_result_table);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Liste des palettes fermées");

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

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Segment");

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

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Référence Client");

        filter_harness_part_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter_harness_part_txtActionPerformed(evt);
            }
        });
        filter_harness_part_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filter_harness_part_txtKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout north_panelLayout = new javax.swing.GroupLayout(north_panel);
        north_panel.setLayout(north_panelLayout);
        north_panelLayout.setHorizontalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(result_table_scroll)
            .addComponent(jSeparator1)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6))
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20)
                                    .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(filter_harness_part_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(340, 340, Short.MAX_VALUE))
        );
        north_panelLayout.setVerticalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(20, 20, 20)
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filter_harness_part_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(18, 18, 18)
                .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(result_table_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 695, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(north_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(north_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void project_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_project_filterItemStateChanged

    }//GEN-LAST:event_project_filterItemStateChanged

    private void project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_filterActionPerformed
        String project = String.valueOf(project_filter.getSelectedItem()).trim();
        ConfigSegment.setSegmentByProject(this, segment_filter, project, true);
    }//GEN-LAST:event_project_filterActionPerformed

    private void segment_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_segment_filterItemStateChanged

    }//GEN-LAST:event_segment_filterItemStateChanged

    private void segment_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segment_filterActionPerformed
        String segment = String.valueOf(segment_filter.getSelectedItem()).trim();
        this.workplace_filter.removeAllItems();
        this.workplace_filter.addItem("ALL");
        if ("ALL".equals(segment) || segment.equals("null")) {
            this.workplace_filter.setSelectedIndex(0);
            this.workplace_filter.setEnabled(false);
        } else {
            workplace_filter = ConfigWorkplace.initWorkplaceJBox(this, workplace_filter, segment, true);
            this.workplace_filter.setEnabled(true);
        }
    }//GEN-LAST:event_segment_filterActionPerformed

    private void workplace_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_workplace_filterItemStateChanged

    }//GEN-LAST:event_workplace_filterItemStateChanged

    private void workplace_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplace_filterActionPerformed
        //        refresh();
    }//GEN-LAST:event_workplace_filterActionPerformed

    private void filter_harness_part_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter_harness_part_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filter_harness_part_txtActionPerformed

    private void filter_harness_part_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filter_harness_part_txtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_filter_harness_part_txtKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable closed_result_table;
    private javax.swing.JTextField filter_harness_part_txt;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel north_panel;
    private javax.swing.JComboBox project_filter;
    private javax.swing.JButton refresh_btn;
    private javax.swing.JScrollPane result_table_scroll;
    private javax.swing.JComboBox segment_filter;
    private javax.swing.JComboBox workplace_filter;
    // End of variables declaration//GEN-END:variables
}
