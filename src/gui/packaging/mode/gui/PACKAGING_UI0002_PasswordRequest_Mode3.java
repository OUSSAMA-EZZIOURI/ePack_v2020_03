/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging.mode.gui;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import entity.ManufactureUsers;
import gui.packaging.PackagingVars;
import gui.packaging.mode.state.Mode3_S010_UserCodeScan;
import gui.packaging.mode.state.Mode3_S020_HarnessPartScan;
import helper.HQLHelper;
import helper.Helper;
import helper.UIHelper;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import ui.UILog;
import ui.error.ErrorMsg;
import ui.info.InfoMsg;

/**
 *
 * @author user
 */
public class PACKAGING_UI0002_PasswordRequest_Mode3 extends javax.swing.JDialog {

    private final ManufactureUsers user;

    /**
     * Creates new form UI0010_PalletDetails
     */
    public PACKAGING_UI0002_PasswordRequest_Mode3(java.awt.Frame parent, boolean modal, ManufactureUsers user) {
        super(parent, modal);
        this.user = user;
        initComponents();
        UIHelper.centerJDialog(this);
        this.setResizable(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        ok_btn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        error_lbl = new javax.swing.JLabel();
        admin_password_txtbox = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Password");
        setType(java.awt.Window.Type.UTILITY);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        ok_btn.setText("OK");
        ok_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ok_btnActionPerformed(evt);
            }
        });

        jLabel1.setText("Mot de passe");

        error_lbl.setForeground(new java.awt.Color(255, 0, 0));

        admin_password_txtbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                admin_password_txtboxKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(admin_password_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ok_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addGap(60, 60, 60)
                .addComponent(error_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(38, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ok_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(admin_password_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(error_lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void clearPasswordBox() {
        //Vider le champs de text scan
        admin_password_txtbox.setText("");
    }

    private boolean checkLoginAndPass() {
        Helper.sess.beginTransaction();
        System.out.println("Login to check " + this.user.getLogin());
        System.out.println("Password " + String.valueOf(admin_password_txtbox.getPassword()));
        Query query = Helper.sess.createQuery(HQLHelper.CHECK_LOGIN_PASS);
        query.setParameter("login", this.user.getLogin());
        query.setParameter("password", String.valueOf(admin_password_txtbox.getPassword()));
        //query.setParameter("active", PackagingVars.context.getUser().getActive()); //active user only
        //query.setParameter("harnessType", PackagingVars.context.getUser().getFamily());
        Helper.sess.getTransaction().commit();
        List result = query.list();
        System.out.println("Resultat du check " + result.size());
        if (result.isEmpty()) {
            return false;
        }
        return true;
    }

    private void ok_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ok_btnActionPerformed

        if (checkLoginAndPass()) {
            connect_to_mode3();
        } else {
            UILog.severeDialog(null, ErrorMsg.APP_ERR0004);
            UILog.severe(ErrorMsg.APP_ERR0004[0]);
            //JOptionPane.showMessageDialog(null, Helper.ERR0001_LOGIN_FAILED, "Login Error", JOptionPane.ERROR_MESSAGE);
            admin_password_txtbox.setText("");
        }
    }//GEN-LAST:event_ok_btnActionPerformed

    

    private void connect_to_mode3() {

        PackagingVars.Packaging_Gui_Mode3.enableAdminMenus();
        String harnessType = String.valueOf(PackagingVars.Packaging_Gui_Mode3.getHarnessTypeBox().getSelectedItem());
        Helper.startSession();

        this.user.setLoginTime(new Date());
        PackagingVars.context.setUser(this.user);
        PackagingVars.context.getUser().update(PackagingVars.context.getUser());
        PackagingVars.mode3_context.getBaseContainerTmp().setHarnessType(harnessType);
        PackagingVars.PRODUCT_FAMILY = PackagingVars.PRODUCT_FAMILY.getFamilyByName(harnessType);
        System.out.println("PackagingVars.PROJECT "+PackagingVars.PRODUCT_FAMILY.toString());
        System.out.println("PackagingVars.context.getUser() "+PackagingVars.context.getUser().getProject());
        try {
            GlobalVars.APP_HOSTNAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Mode3_S010_UserCodeScan.class.getName()).log(Level.SEVERE, null, ex);
        }
        String str = String.format(InfoMsg.APP_INFO0003[1],
                this.user.getFirstName() + " " + this.user.getLastName()
                + " / " + this.user.getLogin(), GlobalVars.APP_HOSTNAME,
                GlobalMethods.getStrTimeStamp() + " Project : "
                + harnessType);

        UILog.info(str);

 
        //Set connected user label text
        PackagingVars.Packaging_Gui_Mode3.setUserLabelText(PackagingVars.context.getUser().getFirstName() + " "
                + PackagingVars.context.getUser().getLastName() + " "
                + "[" + PackagingVars.Packaging_Gui_Mode3.getHarnessTypeBox().getSelectedItem().toString() + "]"
        );
        //Disable filter
        PackagingVars.Packaging_Gui_Mode3.setHarnessTypeFilterBoxState(false);
        System.out.println("Connected to company info : "+GlobalVars.COMPANY_INFO.toString());
        //Load DOTMATRIX
        GlobalMethods.loadDotMatrixCodePatterns(PackagingVars.Packaging_Gui_Mode3.getHarnessTypeBox().getSelectedItem().toString());

        //Load PART NUMBER patterns
        GlobalMethods.loadPartNumberCodePatterns(PackagingVars.Packaging_Gui_Mode3.getHarnessTypeBox().getSelectedItem().toString());

        //Auth réussie, Passage à l'état S02 de lecture Harness part                
        PackagingVars.mode3_context.setState(new Mode3_S020_HarnessPartScan());

        this.dispose();
    }

    private void admin_password_txtboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_admin_password_txtboxKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (checkLoginAndPass()) {
                connect_to_mode3();
            } else {
                //JOptionPane.showMessageDialog(null, Helper.ERR0001_LOGIN_FAILED, "Login Error", JOptionPane.ERROR_MESSAGE);
                UILog.severeDialog(null, ErrorMsg.APP_ERR0004);
                UILog.severe(ErrorMsg.APP_ERR0004[0]);
                admin_password_txtbox.setText("");
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            PackagingVars.Packaging_Gui_Mode3.logout();
            PackagingVars.Packaging_Gui_Mode3.setHarnessTypeBoxState(true);
            this.dispose();
        }
    }//GEN-LAST:event_admin_password_txtboxKeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (PackagingVars.context.getUser() == null) {
            PackagingVars.mode3_context.setState(new Mode3_S010_UserCodeScan());
        }

    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField admin_password_txtbox;
    private javax.swing.JLabel error_lbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton ok_btn;
    // End of variables declaration//GEN-END:variables
}