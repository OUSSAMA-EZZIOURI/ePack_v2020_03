/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.warehouse_dispatch;

import entity.ConfigBarcode;
import entity.LoadPlan;
import gui.warehouse_dispatch.process_control_labels.ControlState;
import gui.warehouse_dispatch.process_control_labels.S001_PalletNumberScan;
import gui.warehouse_dispatch.state.WarehouseHelper;
import helper.HQLHelper;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTextField;
import org.hibernate.Query;
import helper.Helper;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import ui.UILog;

/**
 *
 * @author Oussama EZZIOURI
 */
public class WAREHOUSE_DISPATCH_UI0009_LABELS_CONTROL extends javax.swing.JFrame {

    private String frameTitle = "Contrôle des étiquettes export";
    public ControlState state = null;//WarehouseHelper.warehouse_control_context.getState();

    /**
     * Creates new form WAREHOUSE_DISPATCH_UI0009_LABELS_CONTROL
     */
    public WAREHOUSE_DISPATCH_UI0009_LABELS_CONTROL(LoadPlan p) {
        initComponents();
        this.setTitle(frameTitle);
        scan_textbox.requestFocus();
        //Maximaze the jframe
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        loadDispatchPNPatterns();
        loadDispatchSerialNoPatterns();
        loadDispatchQTYPatterns();

        initGui();
    }

    /**
     *
     */
    private void loadDispatchPNPatterns() {
        System.out.println("Loading DISPATCH_PN pattern list ... ");
        Query query = Helper.sess.createQuery(HQLHelper.GET_PATTERN_BY_KEYWORD);
        query.setParameter("keyWord", "DISPATCH_PN");
        UILog.info(query.getQueryString());
        Helper.sess.beginTransaction();
        Helper.sess.getTransaction().commit();
        List resultList = query.list();
        WarehouseHelper.DISPATCH_PN_LIST = new String[query.list().size()];

        System.out.println("DISPATCH_PN_LIST pattern list...");
        int i = 0;
        for (Iterator it = resultList.iterator(); it.hasNext();) {
            ConfigBarcode object = (ConfigBarcode) it.next();
            WarehouseHelper.DISPATCH_PN_LIST[i] = object.getBarcodePattern();
            System.out.println(WarehouseHelper.DISPATCH_PN_LIST[i]);
            i++;
        }

        System.out.println(WarehouseHelper.DISPATCH_PN_LIST.length + " DISPATCH_PN_LIST pattern successfuly loaded 100% ! ");

    }

    /**
     *
     */
    private void loadDispatchSerialNoPatterns() {
        System.out.println("Loading DISPATCH_SERIAL_NO pattern list ... ");
        Query query = Helper.sess.createQuery(HQLHelper.GET_PATTERN_BY_KEYWORD);
        query.setParameter("keyWord", "DISPATCH_SERIAL_NO");
        UILog.info(query.getQueryString());
        Helper.sess.beginTransaction();
        Helper.sess.getTransaction().commit();
        List resultList = query.list();
        WarehouseHelper.DISPATCH_SERIAL_NO_LIST = new String[query.list().size()];

        System.out.println("DISPATCH_SERIAL_NO pattern list...");
        int i = 0;
        for (Iterator it = resultList.iterator(); it.hasNext();) {
            ConfigBarcode object = (ConfigBarcode) it.next();
            WarehouseHelper.DISPATCH_SERIAL_NO_LIST[i] = object.getBarcodePattern();
            System.out.println(WarehouseHelper.DISPATCH_SERIAL_NO_LIST[i]);
            i++;
        }

        System.out.println(WarehouseHelper.DISPATCH_SERIAL_NO_LIST.length + " DISPATCH_SERIAL_NO_LIST pattern successfuly loaded 100% ! ");

    }

    /**
     *
     */
    private void loadDispatchQTYPatterns() {
        System.out.println("Loading DISPATCH_SERIAL_NO pattern list ... ");
        Query query = Helper.sess.createQuery(HQLHelper.GET_PATTERN_BY_KEYWORD);
        query.setParameter("keyWord", "DISPATCH_QTY");
        UILog.info(query.getQueryString());
        Helper.sess.beginTransaction();
        Helper.sess.getTransaction().commit();
        List resultList = query.list();
        WarehouseHelper.DISPATCH_QTY_LIST = new String[query.list().size()];

        System.out.println("DISPATCH_QTY pattern list...");
        int i = 0;
        for (Iterator it = resultList.iterator(); it.hasNext();) {
            ConfigBarcode object = (ConfigBarcode) it.next();
            WarehouseHelper.DISPATCH_QTY_LIST[i] = object.getBarcodePattern();
            System.out.println(WarehouseHelper.DISPATCH_QTY_LIST[i]);
            i++;
        }

        System.out.println(WarehouseHelper.DISPATCH_QTY_LIST.length + " DISPATCH_QTY_LIST pattern successfuly loaded 100% ! ");

    }

    @Override
    public void setVisible(boolean b) {
        System.out.println("Current State = " + state.toString());
        super.setVisible(b);
    }

    public void setState(ControlState state) {
        this.state = state;
    }

    public JTextField getScanTxt() {
        return this.scan_textbox;
    }

    public void setScanTxt(JTextField setScanTxt) {
        this.scan_textbox = setScanTxt;
    }

    /**
     *
     * @param msg : String to be displayed
     * @param type : 2 for info, 1 for OK , -1 for error, 0 to clean the label
     */
    public void setMessageLabel(String msg, int type) {

        switch (type) {
            case -1:
                message_label.setBackground(Color.white);
                message_label.setForeground(Color.red);
                message_label.setText(msg);
                break;
            case 1:
                message_label.setBackground(Color.green);
                message_label.setForeground(Color.black);
                message_label.setText(msg);
                break;
            case 2:
                message_label.setBackground(Color.white);
                message_label.setForeground(Color.blue);
                message_label.setText(msg);
                break;
            case 0:
                message_label.setBackground(Color.WHITE);
                message_label.setText("");
                break;
        }

    }

    public void setProgressValue(int n) {
        this.progressBar.setValue(n);
    }

    public void setTxt_dispatchPN(String txt_dispatchPN) {
        this.txt_dispatchPN.setText(txt_dispatchPN);
    }

    public void setTxt_dispatchSerialNo(String txt_dispatchSerialNo) {
        this.txt_dispatchSerialNo.setText(txt_dispatchSerialNo);
    }

    public void setTxt_productPN(String txt_productPN) {
        this.txt_productPN.setText(txt_productPN);
    }

    public void setTxt_productSerialNo(String txt_productSerialNo) {
        this.txt_productSerialNo.setText(txt_productSerialNo);
    }

    public String getTxt_productQty() {
        return txt_productQty.getText();
    }

    public void setTxt_productQty(String txt_productQty) {
        this.txt_productQty.setText(txt_productQty);
    }

    public String getTxt_dispatchSerialQty() {
        return txt_dispatchQty.getText();
    }

    public void setTxt_dispatchQty(String txt_dispatchQty) {
        this.txt_dispatchQty.setText(txt_dispatchQty);
    }

    public void clearTextFields() {
        this.txt_dispatchPN.setText("");
        this.txt_dispatchSerialNo.setText("");
        this.txt_productSerialNo.setText("");
        this.txt_productPN.setText("");
        this.txt_productQty.setText("");
        this.txt_dispatchQty.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scan_textbox = new javax.swing.JTextField();
        progressBar = new javax.swing.JProgressBar();
        message_label = new javax.swing.JTextField();
        button_initialize = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_productSerialNo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_productPN = new javax.swing.JTextField();
        txt_productQty = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txt_dispatchPN = new javax.swing.JTextField();
        txt_dispatchSerialNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_dispatchQty = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
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
        project_text = new javax.swing.JTextField();
        login_lbl4 = new javax.swing.JLabel();
        magasinFG_text = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        transporter_text = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        truck_no_text = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        scan_textbox.setBackground(new java.awt.Color(204, 255, 255));
        scan_textbox.setFont(new java.awt.Font("Calibri", 1, 36)); // NOI18N
        scan_textbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scan_textboxActionPerformed(evt);
            }
        });
        scan_textbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                scan_textboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                scan_textboxFocusLost(evt);
            }
        });
        scan_textbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                scan_textboxKeyPressed(evt);
            }
        });

        progressBar.setBackground(new java.awt.Color(51, 255, 51));

        message_label.setEditable(false);
        message_label.setFont(new java.awt.Font("Calibri", 1, 36)); // NOI18N

        button_initialize.setText("Initialiser");
        button_initialize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_initializeActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Production sérial number :");

        txt_productSerialNo.setEditable(false);
        txt_productSerialNo.setBackground(new java.awt.Color(255, 255, 255));
        txt_productSerialNo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txt_productSerialNo.setForeground(new java.awt.Color(0, 0, 153));
        txt_productSerialNo.setToolTipText("");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Production part number :");

        txt_productPN.setEditable(false);
        txt_productPN.setBackground(new java.awt.Color(255, 255, 255));
        txt_productPN.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txt_productPN.setForeground(new java.awt.Color(0, 0, 153));
        txt_productPN.setToolTipText("");

        txt_productQty.setEditable(false);
        txt_productQty.setBackground(new java.awt.Color(255, 255, 255));
        txt_productQty.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txt_productQty.setForeground(new java.awt.Color(0, 0, 153));
        txt_productQty.setToolTipText("");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Quantité production  :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(26, 26, 26)
                        .addComponent(txt_productSerialNo, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(26, 26, 26)
                        .addComponent(txt_productPN, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(26, 26, 26)
                        .addComponent(txt_productQty, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txt_productSerialNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txt_productQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txt_productPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Dispatch part number :");

        txt_dispatchPN.setEditable(false);
        txt_dispatchPN.setBackground(new java.awt.Color(255, 255, 255));
        txt_dispatchPN.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txt_dispatchPN.setForeground(new java.awt.Color(0, 0, 153));
        txt_dispatchPN.setToolTipText("");

        txt_dispatchSerialNo.setEditable(false);
        txt_dispatchSerialNo.setBackground(new java.awt.Color(255, 255, 255));
        txt_dispatchSerialNo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txt_dispatchSerialNo.setForeground(new java.awt.Color(0, 0, 153));
        txt_dispatchSerialNo.setToolTipText("");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Dispatch sérial number :");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Quantité dispatch :");

        txt_dispatchQty.setEditable(false);
        txt_dispatchQty.setBackground(new java.awt.Color(255, 255, 255));
        txt_dispatchQty.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txt_dispatchQty.setForeground(new java.awt.Color(0, 0, 153));
        txt_dispatchQty.setToolTipText("");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_dispatchPN, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_dispatchQty, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(20, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_dispatchSerialNo, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txt_dispatchPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txt_dispatchQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txt_dispatchSerialNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setLayout(new java.awt.GridLayout(2, 2));

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

        jPanel3.add(panel1);

        panel2.setLayout(new java.awt.GridLayout(5, 2, 0, 12));

        login_lbl3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        login_lbl3.setText("Projet");
        panel2.add(login_lbl3);

        project_text.setEnabled(false);
        panel2.add(project_text);

        login_lbl4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        login_lbl4.setText("Magasin F.G");
        panel2.add(login_lbl4);

        magasinFG_text.setEnabled(false);
        panel2.add(magasinFG_text);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Transporteur");
        panel2.add(jLabel8);

        transporter_text.setEnabled(false);
        panel2.add(transporter_text);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Matricule Remorque");
        panel2.add(jLabel9);

        truck_no_text.setEnabled(false);
        panel2.add(truck_no_text);

        jPanel3.add(panel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 1192, Short.MAX_VALUE)
            .addComponent(scan_textbox)
            .addComponent(message_label)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button_initialize)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 704, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(message_label, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scan_textbox, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(button_initialize)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void scan_textboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scan_textboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_scan_textboxActionPerformed

    private void scan_textboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scan_textboxKeyPressed
        // User has pressed Carriage return button
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            state.doAction(WarehouseHelper.warehouse_control_context);
            state = WarehouseHelper.warehouse_control_context.getState();
        }
    }//GEN-LAST:event_scan_textboxKeyPressed

    private void button_initializeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_initializeActionPerformed
        S001_PalletNumberScan state = new S001_PalletNumberScan();
        WarehouseHelper.warehouse_control_context.setState(state);
        scan_textbox.setText("");
        scan_textbox.requestFocus();
        clearTextFields();
    }//GEN-LAST:event_button_initializeActionPerformed

    private void scan_textboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scan_textboxFocusGained
        scan_textbox.setBackground(Color.GREEN);
    }//GEN-LAST:event_scan_textboxFocusGained

    private void scan_textboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scan_textboxFocusLost
        scan_textbox.setBackground(Color.WHITE);
    }//GEN-LAST:event_scan_textboxFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_initialize;
    private javax.swing.JLabel create_time_label;
    private javax.swing.JLabel create_user_label;
    private javax.swing.JLabel deliv_date_label;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel login_lbl3;
    private javax.swing.JLabel login_lbl4;
    private javax.swing.JTextField magasinFG_text;
    private javax.swing.JTextField message_label;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JLabel plan_num_label;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField project_text;
    private javax.swing.JTextField scan_textbox;
    private javax.swing.JLabel state_label;
    private javax.swing.JLabel time_label1;
    private javax.swing.JLabel time_label2;
    private javax.swing.JLabel time_label3;
    private javax.swing.JLabel time_label4;
    private javax.swing.JLabel time_label6;
    private javax.swing.JTextField transporter_text;
    private javax.swing.JTextField truck_no_text;
    private javax.swing.JTextField txt_dispatchPN;
    private javax.swing.JTextField txt_dispatchQty;
    private javax.swing.JTextField txt_dispatchSerialNo;
    private javax.swing.JTextField txt_productPN;
    private javax.swing.JTextField txt_productQty;
    private javax.swing.JTextField txt_productSerialNo;
    // End of variables declaration//GEN-END:variables

    private void initGui() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        plan_num_label.setText(WarehouseHelper.temp_load_plan.getId().toString());
        create_user_label.setText(WarehouseHelper.temp_load_plan.getUser());
        create_time_label.setText(sdf.format(WarehouseHelper.temp_load_plan.getCreateTime()));
        deliv_date_label.setText(sdf.format(WarehouseHelper.temp_load_plan.getDeliveryTime()));
        truck_no_text.setText(WarehouseHelper.temp_load_plan.getTruckNo());
        //transporter_text.setText(WarehouseHelper.temp_load_plan.getTransporter());
        state_label.setText(WarehouseHelper.temp_load_plan.getPlanState());

    }
}
