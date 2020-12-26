package gui.cra;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import __main__.PropertiesLoader;
import entity.ConfigProject;
import entity.ConfigWarehouse;
import entity.WireStock;
import helper.HQLHelper;
import helper.Helper;
import helper.UIHelper;
import helper.XLSXExportHelper;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;

/**
 *
 * @author Oussama
 */
public class CRA_UI0005_WIRE_STOCK extends javax.swing.JPanel {

    private JTabbedPane parent;
    Vector<String> result_table_header = new Vector<String>(Arrays.asList(
            "Repère",
            "Magasin",
            "Location",
            "Num.Carte",
            "Stock",
            "Type",
            "FIFO Date."
    ));

    Vector result_table_data = new Vector();
    List<WireStock> excelLines;
    WireStock aux;
    boolean err = false;
    int lineId = 0;

    public static void main(String[] args) {

        String feedback = PropertiesLoader.loadConfigProperties();
        //LOGGER.log(Level.INFO, feedback);
        GlobalMethods.createDefaultDirectories();
        Helper.startSession();

        CRA_UI0005_WIRE_STOCK c = new CRA_UI0005_WIRE_STOCK();
        JFrame f = new JFrame();
        f.setSize(1200, 700);
        f.add(c);
        f.setVisible(true);

    }
    
    

    /**
     * Creates new form CRA_UI0001_PRODUCTION_PLAN
     */
    public CRA_UI0005_WIRE_STOCK() {
        initComponents();
    }

    public CRA_UI0005_WIRE_STOCK(JTabbedPane rootTabbedPane) {
        this.parent = rootTabbedPane;
        initComponents();
        initGui();
    }

    private void initProjectCombo() {        
        combo_project_filter = ConfigProject.initProjectsJBox(this, combo_project_filter, "", true);
    }
    
    private void initWarehouseCombo() {        
        combo_warehouse_filter = ConfigWarehouse.initWarehouseJBox(
                this, 
                combo_warehouse_filter, 
                "ALL", 
                //WarehouseType.WIRES.name(), 
                String.valueOf(GlobalVars.WarehouseType.WIRES),
                "",
                false);
    }

    private void initResultJTable() {
        result_jtable.setModel(new DefaultTableModel(new Vector(), result_table_header));
        UIHelper.disableEditingJtable(result_jtable);
        this.initContainerTableDoubleClick();
    }

    private void initGui() {
        initResultJTable();
        initProjectCombo();
        initWarehouseCombo();
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
        result_jtable = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        craUI0003_form_panel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        combo_project_filter = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txt_wireNo_filter = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt_location_filter = new javax.swing.JTextField();
        btn_refresh = new javax.swing.JButton();
        btn_export_excel = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txt_cardNumber_filter = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        combo_warehouse_filter = new javax.swing.JComboBox<>();
        checkbox_fifo_date = new javax.swing.JCheckBox();
        msg_lbl = new javax.swing.JLabel();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("jMenu3");

        setBackground(new java.awt.Color(36, 65, 86));
        setToolTipText("Planning d'assemblage");
        setName("Planning d'assemblage"); // NOI18N

        result_jtable.setAutoCreateRowSorter(true);
        result_jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        result_jtable.setCellSelectionEnabled(true);
        jScrollPane1.setViewportView(result_jtable);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Stock repères");

        craUI0003_form_panel.setBackground(new java.awt.Color(36, 65, 86));
        craUI0003_form_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Project");
        jLabel8.setPreferredSize(new java.awt.Dimension(130, 24));

        combo_project_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_project_filterActionPerformed(evt);
            }
        });

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Location");

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Repère");

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

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Num. Carte");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Magasin");
        jLabel2.setPreferredSize(new java.awt.Dimension(130, 24));

        checkbox_fifo_date.setText("FIFO Date");

        javax.swing.GroupLayout craUI0003_form_panelLayout = new javax.swing.GroupLayout(craUI0003_form_panel);
        craUI0003_form_panel.setLayout(craUI0003_form_panelLayout);
        craUI0003_form_panelLayout.setHorizontalGroup(
            craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(craUI0003_form_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(craUI0003_form_panelLayout.createSequentialGroup()
                        .addComponent(btn_refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_export_excel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, craUI0003_form_panelLayout.createSequentialGroup()
                        .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(craUI0003_form_panelLayout.createSequentialGroup()
                                .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(craUI0003_form_panelLayout.createSequentialGroup()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(29, 29, 29)
                                        .addComponent(combo_project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(craUI0003_form_panelLayout.createSequentialGroup()
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txt_wireNo_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(12, 12, 12)
                                .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)))
                            .addGroup(craUI0003_form_panelLayout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_cardNumber_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(checkbox_fifo_date, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                            .addComponent(txt_location_filter, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(combo_warehouse_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, 143, Short.MAX_VALUE))
                        .addGap(429, 429, 429))))
        );
        craUI0003_form_panelLayout.setVerticalGroup(
            craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(craUI0003_form_panelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(txt_wireNo_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9))
                    .addGroup(craUI0003_form_panelLayout.createSequentialGroup()
                        .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(combo_project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(combo_warehouse_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_location_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txt_cardNumber_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkbox_fifo_date))
                .addGap(21, 21, 21)
                .addGroup(craUI0003_form_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_refresh)
                    .addComponent(btn_export_excel))
                .addContainerGap())
        );

        msg_lbl.setBackground(new java.awt.Color(255, 255, 255));
        msg_lbl.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        msg_lbl.setForeground(new java.awt.Color(255, 255, 255));
        msg_lbl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(craUI0003_form_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(msg_lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(craUI0003_form_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
  

    private void initContainerTableDoubleClick() {
        this.result_jtable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    
                }
            }
        }
        );
    }
    private void btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refreshActionPerformed
        refreshResultTable();
    }//GEN-LAST:event_btn_refreshActionPerformed

    private void btn_export_excelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_export_excelActionPerformed
        XLSXExportHelper h = new XLSXExportHelper();
        h.exportToXSSFWorkbook(this, "STOCK_LOCATIONS", this.excelLines);
    }//GEN-LAST:event_btn_export_excelActionPerformed

    private void combo_project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_project_filterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_project_filterActionPerformed

    private Vector getResultLines() {
        result_table_data = new Vector();
        Helper.startSession();
        List<String> projects = new ArrayList<>();
        List<String> warehouses = new ArrayList<>();
        Query query = Helper.sess.createQuery(HQLHelper.GET_WIRE_STOCK_WHERE);
        if(combo_project_filter.getSelectedItem().toString().equals("ALL")){
            for (int i =0; i<combo_project_filter.getItemCount();i++) {
                projects.add(combo_project_filter.getItemAt(i));                
            }
        }else{
            projects.add(combo_project_filter.getSelectedItem().toString());
        }
        //combo_warehouse_filter
        if(combo_warehouse_filter.getSelectedItem().toString().equals("ALL")){
            for (int i =0; i<combo_warehouse_filter.getItemCount();i++) {
                warehouses.add(combo_warehouse_filter.getItemAt(i));                
            }
        }else{
            warehouses.add(combo_warehouse_filter.getSelectedItem().toString());
        }
        query.setParameter("location", "%" + txt_location_filter.getText() + "%");
        query.setParameter("cardNumber", "%" + txt_cardNumber_filter.getText() + "%");
        query.setParameter("wireNo", "%" + txt_wireNo_filter.getText() + "%");
        query.setParameterList("warehouses", warehouses);
        query.setParameterList("projects", projects);
        List<WireStock> result = query.list();
        Helper.sess.getTransaction().commit();

        this.excelLines = new ArrayList<WireStock>(result.size());

        //Populate the jTable with lines
        for (WireStock obj : result) {
            //Add this object to excelLines list
            this.excelLines.add((WireStock) obj);
            
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(obj.getWireNo());
            oneRow.add(obj.getWarehouse());
            oneRow.add(obj.getLocation());
            oneRow.add(obj.getQty());
            oneRow.add(obj.getWireType());
            oneRow.add(obj.getFifoTime());

            result_table_data.add(oneRow);
        }
        return result_table_data;
    }

    private void refreshResultTable() {
        result_jtable.setModel(new DefaultTableModel(new Vector(), result_table_header));
        result_jtable.setModel(new DefaultTableModel(getResultLines(), result_table_header));
    }

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_export_excel;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JCheckBox checkbox_fifo_date;
    private javax.swing.JComboBox<String> combo_project_filter;
    private javax.swing.JComboBox<String> combo_warehouse_filter;
    private javax.swing.JPanel craUI0003_form_panel;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel msg_lbl;
    private javax.swing.JTable result_jtable;
    private javax.swing.JTextField txt_cardNumber_filter;
    private javax.swing.JTextField txt_location_filter;
    private javax.swing.JTextField txt_wireNo_filter;
    // End of variables declaration//GEN-END:variables

}