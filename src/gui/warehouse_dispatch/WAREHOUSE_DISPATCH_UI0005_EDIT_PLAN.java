/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.warehouse_dispatch;

import entity.ConfigProject;
import entity.ConfigWarehouse;
import entity.LoadPlan;
import entity.LoadPlanDestinationRel;
import gui.warehouse_dispatch.state.WarehouseHelper;
import helper.HQLHelper;
import helper.Helper;
import helper.UIHelper;
import java.awt.HeadlessException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 *
 * @author user
 */
public class WAREHOUSE_DISPATCH_UI0005_EDIT_PLAN extends javax.swing.JDialog {

    private LoadPlan lp;
    private final WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL parent;
    public String[] planDests;
    public LoadPlanDestinationRel[] loadPlanDestRel;

    /**
     * Creates new form WAREHOUSE_OUT_UI0004_NEW_PLAN
     *
     * @param parent
     * @param modal
     * @param lp
     */
    public WAREHOUSE_DISPATCH_UI0005_EDIT_PLAN(WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL parent, LoadPlan lp) {
//        super(parent, modal);
        this.parent = parent;
        this.lp = lp;

        initComponents();
        initGui();       

    }

    private void initGui() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            plan_num_label.setText(this.lp.getId().toString());
            create_user_label.setText(this.lp.getUser());
            create_time_label.setText(sdf.format(this.lp.getCreateTime()));
            deliv_date_label.setText(sdf.format(this.lp.getDeliveryTime()));
            truck_no_text.setText(this.lp.getTruckNo());
//            transporter_text.setText(this.lp.getTransportCompany());
            state_label.setText(this.lp.getPlanState());
            newDeliveryDatePicker.setDate(this.lp.getDeliveryTime());

            project_filter = ConfigProject.initProjectsJBox(this, project_filter, false);

            //Set the project value
            for (int i = 0; i < project_filter.getItemCount(); i++) {
                if (project_filter.getItemAt(i).toString().equals(this.lp.getProject())) {
                    project_filter.setSelectedIndex(i);
                    break;
                }
            }
            //Set the fg warehouse value
            for (int i = 0; i < warehouse_filter.getItemCount(); i++) {
                if (warehouse_filter.getItemAt(i).toString().equals(this.lp.getFgWarehouse())) {
                    warehouse_filter.setSelectedIndex(i);
                    break;
                }
            }

            //Load plan destination
            this.initDestinationsJtable();

            UIHelper.centerJDialog((JDialog) this);
            this.setResizable(false);
        } catch (Exception e) {
            UILog.severe(ErrorMsg.APP_ERR0031[0]);
            UILog.severeDialog(null, ErrorMsg.APP_ERR0031);
            throw e;
        }
    }

    public boolean ifDestIsSelected(String dest, String[] destsList) {
        for (String item : destsList) {
            System.out.println("item" + item);
            if (item != null && item.equals(dest)) {
                return true;
            }
        }
        return false;
    }

    public void disableEditingTable() {
        for (int c = 1; c < destinations_table.getColumnCount(); c++) {
            Class<?> col_class = destinations_table.getColumnClass(c);
            if (col_class != Boolean.class) {
                destinations_table.setDefaultEditor(col_class, null);        // remove editor            
            }
        }
    }

    private String[] loadPlanDestinationsList() {
        // Load destinations of this plan
        Helper.startSession();

        Query query = Helper.sess.createQuery(HQLHelper.LOAD_PLAN_DEST_BY_PLAN_ID);
        query.setParameter("loadPlanId", this.lp.getId());
        Helper.sess.getTransaction().commit();
        List result = query.list();
        if (result.isEmpty()) {

            UILog.severe(ErrorMsg.APP_ERR0025[0]);
            UILog.severeDialog(null, ErrorMsg.APP_ERR0025);

            return null;
        } else {
            this.planDests = new String[result.size()];
            this.loadPlanDestRel = new LoadPlanDestinationRel[result.size()];
            int i = 0;
            //Map project data in the list
            for (Object o : result) {
                LoadPlanDestinationRel obj = (LoadPlanDestinationRel) o;
                this.loadPlanDestRel[i] = obj;
                this.planDests[i] = obj.getDestination();
                i++;
            }

            return this.planDests;
        }
    }

    /**
     * Load destinations in jTable
     */
    public void initDestinationsJtable() {
        //First we load the plan destinations list
        this.planDests = this.loadPlanDestinationsList();
        if (this.planDests != null) {
            //Second we load all destinations, if the destination
            //is in the plan, we mark it with true, else we mark it with false
            Helper.startSession();

            SQLQuery dest_query = Helper.sess.createSQLQuery(String.format(HQLHelper.LOAD_PLAN_DEST_BY_PLAN_ID_SQL, this.lp.getId()));

            //SQL Query to get the destinations of the current plan
            dest_query.addScalar("destination", StandardBasicTypes.STRING);

            List<Object[]> result = dest_query.list();
            Helper.sess.getTransaction().commit();
            System.out.println("result Yahya Ibn Ibrahim Al Judali  " + result.toString());

            //List result = query.list();
            if (result.isEmpty()) {
                UILog.severe(ErrorMsg.APP_ERR0025[0]);
                UILog.severeDialog(null, ErrorMsg.APP_ERR0025);
                this.dispose();
            } else {

                //Create the destinations table component
                Vector dest_vector = new Vector();
                Vector<String> dest_header = new Vector<String>();
                for (int i = 0; i < result.size(); i++) {

                    Vector<Object> oneRow = new Vector<Object>();
                    oneRow.add(result.get(i));

                    dest_vector.add(oneRow);

                }
                dest_header.add("Destination");
                destinations_table.setModel(new DefaultTableModel(dest_vector, dest_header));

                this.setVisible(true);
            }
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

        ok_btn = new javax.swing.JButton();
        cancel_btn = new javax.swing.JButton();
        main_tabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        panel1 = new javax.swing.JPanel();
        time_label2 = new javax.swing.JLabel();
        plan_num_label = new javax.swing.JLabel();
        time_label3 = new javax.swing.JLabel();
        create_user_label = new javax.swing.JLabel();
        time_label1 = new javax.swing.JLabel();
        create_time_label = new javax.swing.JLabel();
        time_label4 = new javax.swing.JLabel();
        deliv_date_label = new javax.swing.JLabel();
        time_label6 = new javax.swing.JLabel();
        state_label = new javax.swing.JLabel();
        panel2 = new javax.swing.JPanel();
        login_lbl3 = new javax.swing.JLabel();
        project_filter = new javax.swing.JComboBox();
        login_lbl4 = new javax.swing.JLabel();
        warehouse_filter = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        newDeliveryDatePicker = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        transporter_filter = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        truck_no_text = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        destinations_table = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modifier plan de chargment");

        ok_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        ok_btn.setText("Sauvegarder");
        ok_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ok_btnActionPerformed(evt);
            }
        });

        cancel_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cancel_btn.setText("Annuler");
        cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_btnActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridLayout(2, 2));

        panel1.setLayout(new java.awt.GridLayout(5, 2, 1, 0));

        time_label2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label2.setText("Plan de chargement N°");
        panel1.add(time_label2);

        plan_num_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        plan_num_label.setText("-----");
        panel1.add(plan_num_label);

        time_label3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label3.setText("Créé par");
        panel1.add(time_label3);

        create_user_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        create_user_label.setText("-----");
        panel1.add(create_user_label);

        time_label1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label1.setText("Date création");
        panel1.add(time_label1);

        create_time_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        create_time_label.setText("--/--/---- --:--");
        panel1.add(create_time_label);

        time_label4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label4.setText("Date Expédition");
        panel1.add(time_label4);

        deliv_date_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        deliv_date_label.setText("--/--/----");
        panel1.add(deliv_date_label);

        time_label6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label6.setText("Statut");
        panel1.add(time_label6);

        state_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        state_label.setText("-----");
        panel1.add(state_label);

        jPanel1.add(panel1);

        panel2.setLayout(new java.awt.GridLayout(5, 2, 0, 12));

        login_lbl3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        login_lbl3.setText("Projet");
        panel2.add(login_lbl3);

        project_filter.setEnabled(false);
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
        panel2.add(project_filter);

        login_lbl4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        login_lbl4.setText("Magasin F.G");
        panel2.add(login_lbl4);

        warehouse_filter.setToolTipText("Magasin Produit Fini");
        warehouse_filter.setEnabled(false);
        warehouse_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                warehouse_filterItemStateChanged(evt);
            }
        });
        warehouse_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warehouse_filterActionPerformed(evt);
            }
        });
        panel2.add(warehouse_filter);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Date d'expédition");
        panel2.add(jLabel1);

        newDeliveryDatePicker.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        panel2.add(newDeliveryDatePicker);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Transporteur");
        panel2.add(jLabel2);

        panel2.add(transporter_filter);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Matricule Remorque");
        panel2.add(jLabel3);
        panel2.add(truck_no_text);

        jPanel1.add(panel2);

        destinations_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null}
            },
            new String [] {
                "Destination"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        destinations_table.setEnabled(false);
        jScrollPane2.setViewportView(destinations_table);

        jPanel1.add(jScrollPane2);

        main_tabbedPane.addTab("Informations générales", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ok_btn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancel_btn))
                    .addComponent(main_tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(main_tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ok_btn)
                    .addComponent(cancel_btn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ok_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ok_btnActionPerformed
        //Get selected destinations 
        String[] selectedDestinations = new String[destinations_table.getRowCount()];
        boolean flag = false;
        try {
            if (newDeliveryDatePicker.getDate() == null) {
                UILog.severe(ErrorMsg.APP_ERR0027[0]);
                UILog.severeDialog(null, ErrorMsg.APP_ERR0027);
                newDeliveryDatePicker.requestFocus();
            } else {
                this.lp.setDeliveryTime(newDeliveryDatePicker.getDate());
//                if (transporter_text.getText().isEmpty()) {
//                    UILog.severe(ErrorMsg.APP_ERR0045[0]);
//                    UILog.severeDialog(null, ErrorMsg.APP_ERR0045);
//                    transporter_text.requestFocus();
//                } else {

                    this.lp.setTruckNo((truck_no_text.getText().isEmpty()) ? "" : truck_no_text.getText());
                    this.lp.setFgWarehouse(warehouse_filter.getSelectedItem() + "");
                    this.lp.setTransportCompany(transporter_filter.getSelectedItem() + "");
                    String packaging_wh = new ConfigWarehouse().getPackagingWh(project_filter.getSelectedItem().toString());
                    lp.setPackagingWarehouse(packaging_wh);
                    this.lp.update(this.lp);
                    //Load data into labels
                    WarehouseHelper.Dispatch_Gui_Jpanel.loadPlanDataToLabels(lp, this.planDests[0]);

                    //Load plans JTable
                    WarehouseHelper.Dispatch_Gui_Jpanel.loadPlanDataInGui(lp, this.planDests[0]);

                    WarehouseHelper.Dispatch_Gui_Jpanel.reloadPlansData();

                    this.dispose();
                //}
            }

        } catch (HibernateException | HeadlessException e) {
            UILog.severe(ErrorMsg.APP_ERR0028[0]);
            UILog.severeDialog(null, ErrorMsg.APP_ERR0028);
            throw e;
        }
//        }
    }//GEN-LAST:event_ok_btnActionPerformed

    private void cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_btnActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancel_btnActionPerformed

    private void project_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_project_filterItemStateChanged
        warehouse_filter = ConfigWarehouse.initWarehouseJBox(this, warehouse_filter, String.valueOf(project_filter.getSelectedItem()), 1, false);
    }//GEN-LAST:event_project_filterItemStateChanged

    private void project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_filterActionPerformed

    }//GEN-LAST:event_project_filterActionPerformed

    private void warehouse_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_warehouse_filterItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_warehouse_filterItemStateChanged

    private void warehouse_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warehouse_filterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_warehouse_filterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel_btn;
    private javax.swing.JLabel create_time_label;
    private javax.swing.JLabel create_user_label;
    private javax.swing.JLabel deliv_date_label;
    private javax.swing.JTable destinations_table;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel login_lbl3;
    private javax.swing.JLabel login_lbl4;
    private javax.swing.JTabbedPane main_tabbedPane;
    private org.jdesktop.swingx.JXDatePicker newDeliveryDatePicker;
    private javax.swing.JButton ok_btn;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JLabel plan_num_label;
    private javax.swing.JComboBox project_filter;
    private javax.swing.JLabel state_label;
    private javax.swing.JLabel time_label1;
    private javax.swing.JLabel time_label2;
    private javax.swing.JLabel time_label3;
    private javax.swing.JLabel time_label4;
    private javax.swing.JLabel time_label6;
    private javax.swing.JComboBox<String> transporter_filter;
    private javax.swing.JTextField truck_no_text;
    private javax.swing.JComboBox warehouse_filter;
    // End of variables declaration//GEN-END:variables
}
