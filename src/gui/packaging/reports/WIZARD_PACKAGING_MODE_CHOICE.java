/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging.reports;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import helper.UIHelper;
import javax.swing.ImageIcon;

/**
 *
 * @author Oussama
 */
public class WIZARD_PACKAGING_MODE_CHOICE extends javax.swing.JDialog {

    public int mode = 2;
    private ImageIcon imgIcon;
    /**
     * Creates new form WIZARD_PACKAGING_MODE_CHOICE
     */
    public WIZARD_PACKAGING_MODE_CHOICE(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        imgIcon = new ImageIcon(GlobalVars.APP_PROP.getProperty("IMG_PATH") + "closing_sheet_mode_1.jpg");
        this.img_lbl.setIcon(imgIcon);
        UIHelper.centerJDialog(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        print_format_combobox = new javax.swing.JComboBox<>();
        print_btn = new javax.swing.JButton();
        cancel_btn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        img_lbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Impression étiquette ferméture");
        setMaximumSize(new java.awt.Dimension(300, 250));
        setPreferredSize(new java.awt.Dimension(500, 400));
        setResizable(false);
        setSize(new java.awt.Dimension(500, 400));

        print_format_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Format simple", "Format avancé" }));
        print_format_combobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                print_format_comboboxActionPerformed(evt);
            }
        });

        print_btn.setText("Imprimer");
        print_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                print_btnActionPerformed(evt);
            }
        });

        cancel_btn.setText("Annuler");
        cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_btnActionPerformed(evt);
            }
        });

        jLabel1.setText("Imprimer l'étiquette en format :");

        img_lbl.setBackground(new java.awt.Color(255, 255, 255));
        img_lbl.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(img_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(print_format_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(print_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancel_btn)
                .addGap(182, 182, 182))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(223, 223, 223))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(print_format_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(print_btn)
                    .addComponent(cancel_btn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(img_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void print_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_print_btnActionPerformed
        if (print_format_combobox.getSelectedIndex() == 0) {
            this.mode = 1;
        } else if (print_format_combobox.getSelectedIndex() == 1) {
            this.mode = 2;
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_print_btnActionPerformed

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private void cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_btnActionPerformed
        this.mode = -1;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_cancel_btnActionPerformed

    private void print_format_comboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_print_format_comboboxActionPerformed
        switch(print_format_combobox.getSelectedIndex()){
            case 0:
                this.imgIcon = new ImageIcon(GlobalVars.APP_PROP.getProperty("IMG_PATH") + "closing_sheet_mode_1.jpg");
                this.img_lbl.setIcon(imgIcon);
                break;
            case 1:
                imgIcon = new ImageIcon(GlobalVars.APP_PROP.getProperty("IMG_PATH") + "closing_sheet_mode_2.jpg");
                this.img_lbl.setIcon(imgIcon);
                break;
        }
    }//GEN-LAST:event_print_format_comboboxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel_btn;
    private javax.swing.JLabel img_lbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton print_btn;
    private javax.swing.JComboBox<String> print_format_combobox;
    // End of variables declaration//GEN-END:variables

    public int showDialog() {
        setVisible(true);
        return this.mode;
    }
}
