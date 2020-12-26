package gui.cra;

import __main__.GlobalMethods;
import __main__.PropertiesLoader;
import entity.ConfigProject;
import helper.Helper;
import helper.UIHelper;
import helper.XLSXExportHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;

/**
 *
 * @author Oussama
 */
public class CRA_UI0004_WIRE_DEMAND extends javax.swing.JPanel {

    private JTabbedPane parent;
    Vector<String> table_header = new Vector<String>(Arrays.asList(
            "Projet",
            "Num. Carte",
            "Repère Num.",
            "Besoin repère",
            "Stock repère",
            "Besoin bundles",
            "Besoin repère Arroundi"
    ));

    private Vector table_data = new Vector();
    private Vector<Vector<Object>> excelLines;
    
    private boolean err = false;

    public static void main(String[] args) {

        String feedback = PropertiesLoader.loadConfigProperties();
        //LOGGER.log(Level.INFO, feedback);
        GlobalMethods.createDefaultDirectories();
        Helper.startSession();

        CRA_UI0004_WIRE_DEMAND c = new CRA_UI0004_WIRE_DEMAND();
        JFrame f = new JFrame();
        f.setSize(1200, 700);
        f.add(c);
        f.setVisible(true);

    }

    /**
     * Creates new form CRA_UI0001_PRODUCTION_PLAN
     */
    public CRA_UI0004_WIRE_DEMAND() {
        initComponents();
        initGui();
    }

    public CRA_UI0004_WIRE_DEMAND(JTabbedPane rootTabbedPane) {
        this.parent = rootTabbedPane;
        initComponents();
        initGui();
    }

    
    private void initProjectCombo() {
        combo_project_filter = ConfigProject.initProjectsJBox(this, combo_project_filter, "", true);
    }
    
    private void initGui() {
        jtable_result.setModel(new DefaultTableModel(new Vector(), table_header));
        UIHelper.disableEditingJtable(jtable_result);
        initProjectCombo();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtable_result = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        craUI0001_form_panel = new javax.swing.JPanel();
        msg_lbl = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        combo_project_filter = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        txt_wireNo_filter = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_card_number_filter = new javax.swing.JTextField();
        btn_refresh = new javax.swing.JButton();
        btn_export_excel = new javax.swing.JButton();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("jMenu3");

        setBackground(new java.awt.Color(36, 65, 86));
        setToolTipText("Planning d'assemblage");
        setName("Planning d'assemblage"); // NOI18N

        jtable_result.setAutoCreateRowSorter(true);
        jtable_result.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jtable_result.setCellSelectionEnabled(true);
        jScrollPane1.setViewportView(jtable_result);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Besoin par repère");

        craUI0001_form_panel.setBackground(new java.awt.Color(36, 65, 86));
        craUI0001_form_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        msg_lbl.setBackground(new java.awt.Color(255, 255, 255));
        msg_lbl.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        msg_lbl.setForeground(new java.awt.Color(255, 255, 255));
        msg_lbl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Project");

        combo_project_filter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", " ", " ", " " }));
        combo_project_filter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                combo_project_filterFocusGained(evt);
            }
        });
        combo_project_filter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                combo_project_filterKeyTyped(evt);
            }
        });

        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("Repère num.");

        txt_wireNo_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_wireNo_filterActionPerformed(evt);
            }
        });
        txt_wireNo_filter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_wireNo_filterKeyTyped(evt);
            }
        });

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Num Carte");

        txt_card_number_filter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_card_number_filterKeyTyped(evt);
            }
        });

        btn_refresh.setText("Actualiser");
        btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refreshActionPerformed(evt);
            }
        });

        btn_export_excel.setText("Exporter en Excel");
        btn_export_excel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_export_excelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout craUI0001_form_panelLayout = new javax.swing.GroupLayout(craUI0001_form_panel);
        craUI0001_form_panel.setLayout(craUI0001_form_panelLayout);
        craUI0001_form_panelLayout.setHorizontalGroup(
            craUI0001_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(craUI0001_form_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(craUI0001_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(msg_lbl, javax.swing.GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
                    .addGroup(craUI0001_form_panelLayout.createSequentialGroup()
                        .addGroup(craUI0001_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combo_project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(craUI0001_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_card_number_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addGroup(craUI0001_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(craUI0001_form_panelLayout.createSequentialGroup()
                                .addComponent(txt_wireNo_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(137, 137, 137)
                                .addComponent(btn_refresh)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_export_excel)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        craUI0001_form_panelLayout.setVerticalGroup(
            craUI0001_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(craUI0001_form_panelLayout.createSequentialGroup()
                .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(craUI0001_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(craUI0001_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(combo_project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_card_number_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_wireNo_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(craUI0001_form_panelLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(craUI0001_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_export_excel)
                    .addComponent(btn_refresh))
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1)
                    .addComponent(craUI0001_form_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(craUI0001_form_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refreshActionPerformed
        refreshjTable();
    }//GEN-LAST:event_btn_refreshActionPerformed


    private void btn_export_excelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_export_excelActionPerformed
        XLSXExportHelper h = new XLSXExportHelper();
        h.exportVectorToXSSFWorkbook(this, "WIRE_DEMAND",this.table_header, this.excelLines);
    }//GEN-LAST:event_btn_export_excelActionPerformed

    private void combo_project_filterFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_combo_project_filterFocusGained

    }//GEN-LAST:event_combo_project_filterFocusGained

    private void combo_project_filterKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_combo_project_filterKeyTyped
    }//GEN-LAST:event_combo_project_filterKeyTyped

    private void txt_wireNo_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_wireNo_filterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_wireNo_filterActionPerformed

    private void txt_wireNo_filterKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_wireNo_filterKeyTyped
    }//GEN-LAST:event_txt_wireNo_filterKeyTyped

    private void txt_card_number_filterKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_card_number_filterKeyTyped
    }//GEN-LAST:event_txt_card_number_filterKeyTyped

    private String str_query(){
        return  "SELECT \n"
            + "	project,		\n"
            + "	card_number,	\n"
            + "	wire_no,\n"
            + "	cast(SUM(planned_qty) AS int) AS wire_demand,	\n"
            + "	cast(wire_stock AS int) AS wire_stock,		\n"
            + "	CASE\n"
            + "		WHEN MOD(CAST(SUM(planned_qty) as int), CAST(bundle_qty as int)) > 0 THEN \n"
            + "		round(SUM(bundle_demand)+0.5)\n"
            + "		ELSE\n"
            + "		SUM(bundle_demand)\n"
            + "	END AS bundle_demand,\n"
            + "	CASE\n"
            + "		WHEN MOD(CAST(SUM(planned_qty) as int), CAST(bundle_qty as int)) > 0 THEN\n"
            + "		bundle_qty * round(SUM(bundle_demand)+0.5) \n"
            + "		ELSE\n"
            + "		bundle_qty * SUM(bundle_demand)\n"
            + "	END AS wire_demand_rounded	\n"
            + "	FROM	\n"
            + "	(\n"
            + "		SELECT \n"
            + "			project,		\n"
            + "			stock AS wire_stock,		\n"
            + "			card_number,	\n"
            + "			wire_no,\n"
            + "			SUM(planned_qty) AS planned_qty,\n"
            + "			CASE\n"
            + "				WHEN (planned_qty - stock) < 0 THEN 0\n"
            + "				ELSE (planned_qty - stock)\n"
            + "			END AS wire_demand,\n"
            + "			bundle_qty,\n"
            + "			CASE\n"
            + "				WHEN (planned_qty - stock) / bundle_qty  < 0 THEN 0\n"
            + "				ELSE (planned_qty - stock) / bundle_qty \n"
            + "			END AS bundle_demand	\n"
            + "			FROM (\n"
            + "					SELECT \n"
            + "						wc.project,		\n"
            + "						cs.workplace,\n"
            + "						pp.harness_part, \n"
            + "						pp.internal_part, 					\n"
            + "						wc.card_number,	\n"
            + "						wc.wire_no,\n"
            + "						wc.stock,			\n"
            + "						pp.planned_qty,\n"
            + "						CAST(wc.bundle_qty As real) as bundle_qty\n"
            + "						FROM public.production_plan pp	\n"
            + "						INNER JOIN public.config_ucs cs ON pp.internal_part = cs.supplier_part_number\n"
            + "						INNER JOIN public.wire_config wc ON pp.internal_part = wc.internal_part\n"
            + "					GROUP BY wc.project, wc.wire_no, wc.card_number, wc.stock, pp.planned_qty, cs.workplace,pp.harness_part, \n"
            + "						pp.internal_part, wc.bundle_qty\n"
            + "					ORDER BY wc.wire_no\n"
            + "					) AS T\n"
            + "		GROUP BY project, wire_no, planned_qty, stock, card_number, bundle_qty	\n"
            + "		) AS WIRE_DEMAND WHERE 1=1 \n"
            + " %s \n" // Project filter
            + " %s \n" // card_number filter
            + " %s \n" //wire_no filter
            + " GROUP BY project,		\n"
            + "	wire_stock,		\n"
            + "	card_number,	\n"
            + "	wire_no,\n"
            + "	bundle_qty\n"
            + " ORDER BY wire_no\n"
            + ";";
    }
    
    private Vector executeQuery() {
        
        String projects_filter = "";
        String card_number = "";
        String wire_no = "";
        
        
        if(combo_project_filter.getSelectedItem().toString().equals("ALL")){
            projects_filter = " AND project IN ('1'";
            for (int i =0; i<combo_project_filter.getItemCount();i++) {                
                projects_filter += ",'"+combo_project_filter.getItemAt(i)+"'";
            }
            projects_filter += ")";
        }else{            
            projects_filter = " AND project = '"+combo_project_filter.getSelectedItem().toString()+"'";
        }
        
        if(!txt_card_number_filter.getText().isEmpty()){
            card_number = " AND CAST(card_number As text) LIKE '%"+txt_card_number_filter.getText()+"%'";
        }
        
        if(!txt_wireNo_filter.getText().isEmpty()){
            wire_no = " AND wire_no LIKE '%"+txt_wireNo_filter.getText()+"%'";
        }
        String query_str = String.format(str_query(), 
                projects_filter,                
                card_number,
                wire_no);
        
//        System.out.println(query_str);
//        System.out.println("projects_filter "+projects_filter);
//        System.out.println("card_number "+card_number);
//        System.out.println("wire_no "+wire_no);
        
        Helper.startSession();
        SQLQuery query;
        query = Helper.sess.createSQLQuery(query_str);
        query.addScalar("project", StandardBasicTypes.STRING)
                .addScalar("card_number", StandardBasicTypes.INTEGER)
                .addScalar("wire_no", StandardBasicTypes.STRING)
                .addScalar("wire_demand", StandardBasicTypes.INTEGER)
                .addScalar("wire_stock", StandardBasicTypes.INTEGER)
                .addScalar("bundle_demand", StandardBasicTypes.INTEGER)
                .addScalar("wire_demand_rounded", StandardBasicTypes.INTEGER);
        
        List<Object[]> result = query.list();
        Helper.sess.getTransaction().commit();
        
        if (result.isEmpty()) {
            this.table_data = new Vector();
            return table_data;
        } else {
            this.table_data = new Vector(result.size());
            this.excelLines = new Vector<Vector<Object>>(result.size());
            for (Object[] obj : result) {
                Vector<Object> oneRow = new Vector<Object>();
                oneRow.add(obj[0]);//project
                oneRow.add(Integer.valueOf(obj[1].toString()));//card_number
                oneRow.add(obj[2]);//wire_no
                oneRow.add(Integer.valueOf(obj[3].toString()));//planned_qty
                oneRow.add(Integer.valueOf(obj[4].toString()));//wire_stock
                oneRow.add(Integer.valueOf(obj[5].toString()));//bundle_demand
                oneRow.add(Integer.valueOf(obj[6].toString()));//wire_demand_rounded
                table_data.add(oneRow);
                excelLines.add(oneRow);
            }

            return table_data;
        }
    }

    private void refreshjTable() {
        jtable_result.setModel(new DefaultTableModel(new Vector(), table_header));
        jtable_result.setModel(new DefaultTableModel(executeQuery(), table_header));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_export_excel;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JComboBox<String> combo_project_filter;
    private javax.swing.JPanel craUI0001_form_panel;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtable_result;
    private javax.swing.JLabel msg_lbl;
    private javax.swing.JTextField txt_card_number_filter;
    private javax.swing.JTextField txt_wireNo_filter;
    // End of variables declaration//GEN-END:variables

}