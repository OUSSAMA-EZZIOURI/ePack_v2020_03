/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.config;

import __main__.GlobalMethods;
import __main__.MainFrame;
import entity.ConfigCompany;
import entity.ConfigFamily;
import entity.ConfigProject;
import entity.ConfigSegment;
import entity.ConfigTransporter;
import entity.ConfigWarehouse;
import entity.ConfigWorkplace;
import helper.HQLHelper;
import helper.Helper;
import hibernate.DAO;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.exception.ConstraintViolationException;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 *
 * @author Oussama
 */
public class CONFIG_UI0004_CONFIG_PROJECT_MASTER_DATA_JPANEL extends javax.swing.JPanel {

    private ConfigCompany auxCompany;
    private ConfigProject auxProject;
    private ConfigSegment auxSegment;
    private ConfigFamily auxFamily;
    private ConfigWorkplace auxWorkplace;
    private ConfigTransporter auxTransporter;
    private ConfigWarehouse auxWarehouse;

    /**
     * Creates new form CONFIG_UI0004_CONFIG_PROJECT
     */
    public CONFIG_UI0004_CONFIG_PROJECT_MASTER_DATA_JPANEL() {
        initComponents();
        initGui();
    }

    public CONFIG_UI0004_CONFIG_PROJECT_MASTER_DATA_JPANEL(MainFrame aThis) {
        initComponents();
        initGui();
    }

    public void initGui() {
        disableActionButtons();
        addActionListenersToIdFields();
        addTabbedChangeListeners();
    }

    /**
     * *
     * Disable duplicate and delete buttons in each tabbedPane
     */
    private void disableActionButtons() {
        companyPane_btn_duplicate.setEnabled(false);
        companyPane_btn_delete.setEnabled(false);

        projectPane_btn_duplicate.setEnabled(false);
        projectPane_btn_delete.setEnabled(false);

        familyPane_btn_duplicate.setEnabled(false);
        familyPane_btn_delete.setEnabled(false);

        segmentPane_btn_duplicate.setEnabled(false);
        segmentPane_btn_delete.setEnabled(false);

        workplacePane_btn_duplicate.setEnabled(false);
        workplacePane_btn_delete.setEnabled(false);

        warehousePane_btn_duplicate.setEnabled(false);
        warehousePane_btn_delete.setEnabled(false);

        transporterPane_btn_duplicate.setEnabled(false);
        transporterPane_btn_delete.setEnabled(false);
    }

    /**
     * Add action listeners to ID fields
     */
    private void addActionListenersToIdFields() {
        //Company Tab
        companyPane_field_id.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                companyPane_btn_duplicate.setEnabled(true);
                companyPane_btn_delete.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                companyPane_btn_duplicate.setEnabled(false);
                companyPane_btn_delete.setEnabled(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        //Project Tab
        projectPane_field_id.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                projectPane_btn_duplicate.setEnabled(true);
                projectPane_btn_delete.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                projectPane_btn_duplicate.setEnabled(false);
                projectPane_btn_delete.setEnabled(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        //Family Tab
        familyPane_field_id.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                familyPane_btn_duplicate.setEnabled(true);
                familyPane_btn_delete.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                familyPane_btn_duplicate.setEnabled(false);
                familyPane_btn_delete.setEnabled(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        //Segment Tab
        segmentPane_field_id.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                segmentPane_btn_duplicate.setEnabled(true);
                segmentPane_btn_delete.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                segmentPane_btn_duplicate.setEnabled(false);
                segmentPane_btn_delete.setEnabled(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        //Workplace Tab
        workplacePane_field_id.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                workplacePane_btn_duplicate.setEnabled(true);
                workplacePane_btn_delete.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                workplacePane_btn_duplicate.setEnabled(false);
                workplacePane_btn_delete.setEnabled(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        //Magasin Tab
        warehousePane_field_id.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                warehousePane_btn_duplicate.setEnabled(true);
                warehousePane_btn_delete.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warehousePane_btn_duplicate.setEnabled(false);
                warehousePane_btn_delete.setEnabled(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        //Transporter
        transporterPane_field_id.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                transporterPane_btn_duplicate.setEnabled(true);
                transporterPane_btn_delete.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                transporterPane_btn_duplicate.setEnabled(false);
                transporterPane_btn_delete.setEnabled(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    /**
     *
     */
    private void addTabbedChangeListeners() {
        main_tab.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                System.out.println("Tab selected " + main_tab.getSelectedIndex());
                switch (main_tab.getSelectedIndex()) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2://Family Pane
                        familyPane_field_project = ConfigProject.initProjectsJBox(this, familyPane_field_project, "", false);
                        break;
                    case 3://Segment Pane
                        segmentPane_field_project = ConfigProject.initProjectsJBox(this, segmentPane_field_project,"", false);
                        break;
                    case 4://Workplace Pane
                        workplacePane_field_project = ConfigProject.initProjectsJBox(this, workplacePane_field_project,"", false);
                        ConfigSegment.setSegmentByProject(this, workplacePane_field_segment, workplacePane_field_project.getSelectedItem().toString(), false);
                        break;
                    case 5:
                        warehousePane_field_project = ConfigProject.initProjectsJBox(this, warehousePane_field_project,"", false);
                        break;
                    case 6:                                                
                        break;
                    case 7:                        
                        dispatchScanPane_field_project = ConfigProject.initProjectsJBox(this, dispatchScanPane_field_project, "",false);
                        break;
                    default:
                        break;
                }

            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        main_tab = new javax.swing.JTabbedPane();
        company_pane = new javax.swing.JPanel();
        company_fields = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        companyPane_field_id = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        companyPane_field_country = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        companyPane_field_name = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        companyPane_field_description = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        companyPane_field_address1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        companyPane_field_address2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        companyPane_field_city = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        companyPane_field_website = new javax.swing.JTextField();
        companyPane_field_zip = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        company_table = new javax.swing.JScrollPane();
        company_jtable = new javax.swing.JTable();
        actions_pane = new javax.swing.JPanel();
        companyPane_btn_duplicate = new javax.swing.JButton();
        companyPane_btn_save = new javax.swing.JButton();
        companyPane_btn_clear = new javax.swing.JButton();
        companyPane_btn_refresh = new javax.swing.JButton();
        companyPane_btn_delete = new javax.swing.JButton();
        project_pane = new javax.swing.JPanel();
        project_fields = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        projectPane_field_id = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        projectPane_field_project = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        projectPane_field_desc = new javax.swing.JTextArea();
        project_table = new javax.swing.JScrollPane();
        project_jtable = new javax.swing.JTable();
        actions_pane_7 = new javax.swing.JPanel();
        projectPane_btn_delete = new javax.swing.JButton();
        projectPane_btn_duplicate = new javax.swing.JButton();
        projectPane_btn_save = new javax.swing.JButton();
        projectPane_btn_clear = new javax.swing.JButton();
        projectPane_btn_refresh = new javax.swing.JButton();
        family_pane = new javax.swing.JPanel();
        family_fields = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        familyPane_field_id = new javax.swing.JTextField();
        familyPane_field_project = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        familyPane_field_name = new javax.swing.JTextField();
        actions_pane_2 = new javax.swing.JPanel();
        familyPane_btn_delete = new javax.swing.JButton();
        familyPane_btn_duplicate = new javax.swing.JButton();
        familyPane_btn_save = new javax.swing.JButton();
        familyPane_btn_clear = new javax.swing.JButton();
        familyPane_btn_refresh = new javax.swing.JButton();
        famille_table = new javax.swing.JScrollPane();
        family_jtable = new javax.swing.JTable();
        segment_pane = new javax.swing.JPanel();
        segment_fields = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        segmentPane_field_id = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        segmentPane_field_segment = new javax.swing.JTextField();
        segmentPane_field_project = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        actions_pane_3 = new javax.swing.JPanel();
        segmentPane_btn_delete = new javax.swing.JButton();
        segmentPane_btn_duplicate = new javax.swing.JButton();
        segmentPane_btn_save = new javax.swing.JButton();
        segmentPane_btn_clear = new javax.swing.JButton();
        segmentPane_btn_refresh = new javax.swing.JButton();
        segment_table = new javax.swing.JScrollPane();
        segment_jtable = new javax.swing.JTable();
        workplace_pane = new javax.swing.JPanel();
        workplace_fields = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        workplacePane_field_id = new javax.swing.JTextField();
        workplacePane_field_project = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        workplacePane_field_workplace = new javax.swing.JTextField();
        workplacePane_field_segment = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        workplace_table = new javax.swing.JScrollPane();
        workplace_jtable = new javax.swing.JTable();
        actions_pane_4 = new javax.swing.JPanel();
        workplacePane_btn_delete = new javax.swing.JButton();
        workplacePane_btn_duplicate = new javax.swing.JButton();
        workplacePane_btn_save = new javax.swing.JButton();
        workplacePane_btn_clear = new javax.swing.JButton();
        workplacePane_btn_refresh = new javax.swing.JButton();
        magasin_pane = new javax.swing.JPanel();
        warehouse_fields = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        warehousePane_field_id = new javax.swing.JTextField();
        warehousePane_field_project = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        warehousePane_field_name = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        warehousePane_field_WhType = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        warehousePane_field_desc = new javax.swing.JTextArea();
        actions_pane_5 = new javax.swing.JPanel();
        warehousePane_btn_delete = new javax.swing.JButton();
        warehousePane_btn_duplicate = new javax.swing.JButton();
        warehousePane_btn_save = new javax.swing.JButton();
        warehousePane_btn_clear = new javax.swing.JButton();
        warehousePane_btn_refresh = new javax.swing.JButton();
        magasin_table = new javax.swing.JScrollPane();
        warehouse_jtable = new javax.swing.JTable();
        transporter_pane = new javax.swing.JPanel();
        transporter_fields = new javax.swing.JPanel();
        transporterPane_label_id = new javax.swing.JLabel();
        transporterPane_field_id = new javax.swing.JTextField();
        transporterPane_field_name = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        actions_pane_6 = new javax.swing.JPanel();
        transporterPane_btn_delete = new javax.swing.JButton();
        transporterPane_btn_duplicate = new javax.swing.JButton();
        transporterPane_btn_save = new javax.swing.JButton();
        transporterPane_btn_clear = new javax.swing.JButton();
        transporterPane_btn_refresh = new javax.swing.JButton();
        transporter_table = new javax.swing.JScrollPane();
        transporter_jtable = new javax.swing.JTable();
        dispatch_scan_panel = new javax.swing.JPanel();
        project_fields1 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        dispatchScanPane_field_id = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        dispatchScanPane_field_odette_pn_begin = new javax.swing.JTextField();
        dispatchScanPane_field_project = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        dispatchScanPane_field_odette_pn_end = new javax.swing.JTextField();
        project_table1 = new javax.swing.JScrollPane();
        dispatchControl_jtable = new javax.swing.JTable();
        actions_pane_8 = new javax.swing.JPanel();
        dispatchControlPane_btn_delete = new javax.swing.JButton();
        dispatchControlPane_btn_duplicate = new javax.swing.JButton();
        dispatchControlPane_btn_save = new javax.swing.JButton();
        dispatchControlPane_btn_clear = new javax.swing.JButton();
        dispatchControlPane_btn_refresh = new javax.swing.JButton();
        msg_lbl = new javax.swing.JTextField();

        setBackground(new java.awt.Color(36, 65, 86));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Pramétrage de base");

        company_pane.setBackground(new java.awt.Color(36, 65, 86));

        company_fields.setBackground(new java.awt.Color(36, 65, 86));

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("ID");

        companyPane_field_id.setEditable(false);
        companyPane_field_id.setBackground(new java.awt.Color(255, 255, 255));
        companyPane_field_id.setForeground(new java.awt.Color(0, 0, 0));
        companyPane_field_id.setPreferredSize(new java.awt.Dimension(20, 24));

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Country");

        companyPane_field_country.setBackground(new java.awt.Color(204, 204, 255));
        companyPane_field_country.setPreferredSize(new java.awt.Dimension(20, 24));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Nom");

        companyPane_field_name.setBackground(new java.awt.Color(204, 204, 255));
        companyPane_field_name.setPreferredSize(new java.awt.Dimension(20, 24));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Description");

        companyPane_field_description.setBackground(new java.awt.Color(204, 204, 255));
        companyPane_field_description.setPreferredSize(new java.awt.Dimension(20, 24));

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Adresse 1");

        companyPane_field_address1.setBackground(new java.awt.Color(204, 204, 255));
        companyPane_field_address1.setPreferredSize(new java.awt.Dimension(20, 24));

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Adresse 2");

        companyPane_field_address2.setPreferredSize(new java.awt.Dimension(20, 24));

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Ville");

        companyPane_field_city.setBackground(new java.awt.Color(204, 204, 255));
        companyPane_field_city.setPreferredSize(new java.awt.Dimension(20, 24));

        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Website");

        companyPane_field_website.setPreferredSize(new java.awt.Dimension(20, 24));

        companyPane_field_zip.setBackground(new java.awt.Color(204, 204, 255));
        companyPane_field_zip.setPreferredSize(new java.awt.Dimension(20, 24));

        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Code postal");

        javax.swing.GroupLayout company_fieldsLayout = new javax.swing.GroupLayout(company_fields);
        company_fields.setLayout(company_fieldsLayout);
        company_fieldsLayout.setHorizontalGroup(
            company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(company_fieldsLayout.createSequentialGroup()
                .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(company_fieldsLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(company_fieldsLayout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(companyPane_field_city, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(company_fieldsLayout.createSequentialGroup()
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(companyPane_field_zip, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(companyPane_field_website, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(company_fieldsLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(company_fieldsLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(25, 25, 25)
                                .addComponent(companyPane_field_address1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(companyPane_field_address2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, company_fieldsLayout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(25, 25, 25)
                                .addComponent(companyPane_field_name, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(companyPane_field_description, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, company_fieldsLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(companyPane_field_id, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(companyPane_field_country, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(252, Short.MAX_VALUE))
        );
        company_fieldsLayout.setVerticalGroup(
            company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(company_fieldsLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(companyPane_field_id, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(companyPane_field_country, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(companyPane_field_name, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(companyPane_field_description, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(companyPane_field_address1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(companyPane_field_address2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(company_fieldsLayout.createSequentialGroup()
                        .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(companyPane_field_website, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9))
                    .addGroup(company_fieldsLayout.createSequentialGroup()
                        .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(companyPane_field_city, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(company_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(companyPane_field_zip, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        company_jtable.setModel(new javax.swing.table.DefaultTableModel(
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
        company_jtable.setColumnSelectionAllowed(true);
        company_jtable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        company_table.setViewportView(company_jtable);

        actions_pane.setBackground(new java.awt.Color(36, 65, 86));

        companyPane_btn_duplicate.setText("Dupliquer");
        companyPane_btn_duplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                companyPane_btn_duplicateActionPerformed(evt);
            }
        });

        companyPane_btn_save.setText("Enregistrer");
        companyPane_btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                companyPane_btn_saveActionPerformed(evt);
            }
        });

        companyPane_btn_clear.setText("Réinitialiser");
        companyPane_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                companyPane_btn_clearActionPerformed(evt);
            }
        });

        companyPane_btn_refresh.setText("Actualiser");
        companyPane_btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                companyPane_btn_refreshActionPerformed(evt);
            }
        });

        companyPane_btn_delete.setText("Supprimer");
        companyPane_btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                companyPane_btn_deleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout actions_paneLayout = new javax.swing.GroupLayout(actions_pane);
        actions_pane.setLayout(actions_paneLayout);
        actions_paneLayout.setHorizontalGroup(
            actions_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actions_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(companyPane_btn_refresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(companyPane_btn_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(companyPane_btn_duplicate)
                .addGap(18, 18, 18)
                .addComponent(companyPane_btn_clear)
                .addGap(125, 125, 125)
                .addComponent(companyPane_btn_delete)
                .addContainerGap(306, Short.MAX_VALUE))
        );
        actions_paneLayout.setVerticalGroup(
            actions_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actions_paneLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(actions_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(companyPane_btn_save)
                    .addComponent(companyPane_btn_duplicate)
                    .addComponent(companyPane_btn_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(companyPane_btn_refresh)
                    .addComponent(companyPane_btn_delete))
                .addGap(416, 416, 416))
        );

        javax.swing.GroupLayout company_paneLayout = new javax.swing.GroupLayout(company_pane);
        company_pane.setLayout(company_paneLayout);
        company_paneLayout.setHorizontalGroup(
            company_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(company_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(company_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(company_fields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(company_paneLayout.createSequentialGroup()
                        .addGroup(company_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(actions_pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(company_table))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        company_paneLayout.setVerticalGroup(
            company_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(company_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(company_fields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actions_pane, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(company_table, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        main_tab.addTab("Société", company_pane);

        project_pane.setBackground(new java.awt.Color(36, 65, 86));

        project_fields.setBackground(new java.awt.Color(36, 65, 86));

        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("ID");

        projectPane_field_id.setEditable(false);
        projectPane_field_id.setBackground(new java.awt.Color(255, 255, 255));
        projectPane_field_id.setForeground(new java.awt.Color(0, 0, 0));

        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Abréviation");

        projectPane_field_project.setBackground(new java.awt.Color(204, 204, 255));

        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Description");

        projectPane_field_desc.setBackground(new java.awt.Color(204, 204, 255));
        projectPane_field_desc.setColumns(20);
        projectPane_field_desc.setRows(2);
        projectPane_field_desc.setToolTipText("");
        projectPane_field_desc.setAutoscrolls(false);
        jScrollPane2.setViewportView(projectPane_field_desc);

        javax.swing.GroupLayout project_fieldsLayout = new javax.swing.GroupLayout(project_fields);
        project_fields.setLayout(project_fieldsLayout);
        project_fieldsLayout.setHorizontalGroup(
            project_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(project_fieldsLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(project_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(project_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(projectPane_field_id, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectPane_field_project, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(project_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(project_fieldsLayout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(229, 229, 229))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(132, 132, 132))
        );
        project_fieldsLayout.setVerticalGroup(
            project_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, project_fieldsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(project_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectPane_field_id, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(project_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectPane_field_project, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(146, 146, 146))
            .addGroup(project_fieldsLayout.createSequentialGroup()
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        project_jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        project_jtable.setColumnSelectionAllowed(true);
        project_table.setViewportView(project_jtable);

        actions_pane_7.setBackground(new java.awt.Color(36, 65, 86));

        projectPane_btn_delete.setText("Supprimer");
        projectPane_btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectPane_btn_deleteActionPerformed(evt);
            }
        });

        projectPane_btn_duplicate.setText("Dupliquer");
        projectPane_btn_duplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectPane_btn_duplicateActionPerformed(evt);
            }
        });

        projectPane_btn_save.setText("Enregistrer");
        projectPane_btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectPane_btn_saveActionPerformed(evt);
            }
        });

        projectPane_btn_clear.setText("Réinitialiser");
        projectPane_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectPane_btn_clearActionPerformed(evt);
            }
        });

        projectPane_btn_refresh.setText("Actualiser");
        projectPane_btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectPane_btn_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout actions_pane_7Layout = new javax.swing.GroupLayout(actions_pane_7);
        actions_pane_7.setLayout(actions_pane_7Layout);
        actions_pane_7Layout.setHorizontalGroup(
            actions_pane_7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actions_pane_7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectPane_btn_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projectPane_btn_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projectPane_btn_duplicate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projectPane_btn_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                .addComponent(projectPane_btn_delete)
                .addGap(48, 48, 48))
        );
        actions_pane_7Layout.setVerticalGroup(
            actions_pane_7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actions_pane_7Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(actions_pane_7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectPane_btn_save)
                    .addComponent(projectPane_btn_duplicate)
                    .addComponent(projectPane_btn_delete)
                    .addComponent(projectPane_btn_clear)
                    .addComponent(projectPane_btn_refresh))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout project_paneLayout = new javax.swing.GroupLayout(project_pane);
        project_pane.setLayout(project_paneLayout);
        project_paneLayout.setHorizontalGroup(
            project_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(project_paneLayout.createSequentialGroup()
                .addComponent(actions_pane_7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(project_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(project_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(project_fields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(project_paneLayout.createSequentialGroup()
                        .addComponent(project_table, javax.swing.GroupLayout.PREFERRED_SIZE, 626, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        project_paneLayout.setVerticalGroup(
            project_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(project_paneLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(project_fields, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actions_pane_7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(project_table, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(113, Short.MAX_VALUE))
        );

        main_tab.addTab("Projets", project_pane);

        family_pane.setBackground(new java.awt.Color(36, 65, 86));

        family_fields.setBackground(new java.awt.Color(36, 65, 86));

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("ID");

        familyPane_field_id.setEditable(false);
        familyPane_field_id.setBackground(new java.awt.Color(255, 255, 255));
        familyPane_field_id.setForeground(new java.awt.Color(0, 0, 0));

        familyPane_field_project.setBackground(new java.awt.Color(204, 204, 255));
        familyPane_field_project.setForeground(new java.awt.Color(0, 0, 0));

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Projet");

        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Famille");

        familyPane_field_name.setBackground(new java.awt.Color(204, 204, 255));

        javax.swing.GroupLayout family_fieldsLayout = new javax.swing.GroupLayout(family_fields);
        family_fields.setLayout(family_fieldsLayout);
        family_fieldsLayout.setHorizontalGroup(
            family_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(family_fieldsLayout.createSequentialGroup()
                .addGroup(family_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(family_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(familyPane_field_project, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(familyPane_field_id)
                    .addComponent(familyPane_field_name, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        family_fieldsLayout.setVerticalGroup(
            family_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, family_fieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(family_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(familyPane_field_id)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(family_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(familyPane_field_project, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(family_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(familyPane_field_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        actions_pane_2.setBackground(new java.awt.Color(36, 65, 86));

        familyPane_btn_delete.setText("Supprimer");
        familyPane_btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                familyPane_btn_deleteActionPerformed(evt);
            }
        });

        familyPane_btn_duplicate.setText("Dupliquer");
        familyPane_btn_duplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                familyPane_btn_duplicateActionPerformed(evt);
            }
        });

        familyPane_btn_save.setText("Enregistrer");
        familyPane_btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                familyPane_btn_saveActionPerformed(evt);
            }
        });

        familyPane_btn_clear.setText("Réinitialiser");
        familyPane_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                familyPane_btn_clearActionPerformed(evt);
            }
        });

        familyPane_btn_refresh.setText("Actualiser");
        familyPane_btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                familyPane_btn_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout actions_pane_2Layout = new javax.swing.GroupLayout(actions_pane_2);
        actions_pane_2.setLayout(actions_pane_2Layout);
        actions_pane_2Layout.setHorizontalGroup(
            actions_pane_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actions_pane_2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(familyPane_btn_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(familyPane_btn_save)
                .addGap(18, 18, 18)
                .addComponent(familyPane_btn_duplicate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(familyPane_btn_clear)
                .addGap(51, 51, 51)
                .addComponent(familyPane_btn_delete)
                .addContainerGap(282, Short.MAX_VALUE))
        );
        actions_pane_2Layout.setVerticalGroup(
            actions_pane_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actions_pane_2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(actions_pane_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(familyPane_btn_save)
                    .addComponent(familyPane_btn_duplicate)
                    .addComponent(familyPane_btn_delete)
                    .addComponent(familyPane_btn_clear)
                    .addComponent(familyPane_btn_refresh))
                .addGap(10, 10, 10))
        );

        family_jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        family_jtable.setColumnSelectionAllowed(true);
        famille_table.setViewportView(family_jtable);

        javax.swing.GroupLayout family_paneLayout = new javax.swing.GroupLayout(family_pane);
        family_pane.setLayout(family_paneLayout);
        family_paneLayout.setHorizontalGroup(
            family_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, family_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(family_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(family_fields, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, family_paneLayout.createSequentialGroup()
                        .addGroup(family_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(actions_pane_2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(famille_table, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 665, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 183, Short.MAX_VALUE)))
                .addContainerGap())
        );
        family_paneLayout.setVerticalGroup(
            family_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(family_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(family_fields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(actions_pane_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(famille_table, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                .addContainerGap())
        );

        main_tab.addTab("Famille", family_pane);

        segment_pane.setBackground(new java.awt.Color(36, 65, 86));

        segment_fields.setBackground(new java.awt.Color(36, 65, 86));

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("ID");

        segmentPane_field_id.setEditable(false);
        segmentPane_field_id.setBackground(new java.awt.Color(255, 255, 255));
        segmentPane_field_id.setForeground(new java.awt.Color(0, 0, 0));

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Ségment");

        segmentPane_field_segment.setBackground(new java.awt.Color(204, 204, 255));

        segmentPane_field_project.setBackground(new java.awt.Color(204, 204, 255));
        segmentPane_field_project.setForeground(new java.awt.Color(0, 0, 0));

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Projet");

        javax.swing.GroupLayout segment_fieldsLayout = new javax.swing.GroupLayout(segment_fields);
        segment_fields.setLayout(segment_fieldsLayout);
        segment_fieldsLayout.setHorizontalGroup(
            segment_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(segment_fieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(segment_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(segment_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(segmentPane_field_id, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(segmentPane_field_segment)
                    .addComponent(segmentPane_field_project, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        segment_fieldsLayout.setVerticalGroup(
            segment_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(segment_fieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(segment_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(segmentPane_field_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(segment_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(segmentPane_field_segment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(segment_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(segmentPane_field_project, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        actions_pane_3.setBackground(new java.awt.Color(36, 65, 86));

        segmentPane_btn_delete.setText("Supprimer");
        segmentPane_btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segmentPane_btn_deleteActionPerformed(evt);
            }
        });

        segmentPane_btn_duplicate.setText("Dupliquer");
        segmentPane_btn_duplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segmentPane_btn_duplicateActionPerformed(evt);
            }
        });

        segmentPane_btn_save.setText("Enregistrer");
        segmentPane_btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segmentPane_btn_saveActionPerformed(evt);
            }
        });

        segmentPane_btn_clear.setText("Réinitialiser");
        segmentPane_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segmentPane_btn_clearActionPerformed(evt);
            }
        });

        segmentPane_btn_refresh.setText("Actualiser");
        segmentPane_btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segmentPane_btn_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout actions_pane_3Layout = new javax.swing.GroupLayout(actions_pane_3);
        actions_pane_3.setLayout(actions_pane_3Layout);
        actions_pane_3Layout.setHorizontalGroup(
            actions_pane_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actions_pane_3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(segmentPane_btn_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(segmentPane_btn_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(segmentPane_btn_duplicate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(segmentPane_btn_clear)
                .addGap(58, 58, 58)
                .addComponent(segmentPane_btn_delete)
                .addContainerGap(470, Short.MAX_VALUE))
        );
        actions_pane_3Layout.setVerticalGroup(
            actions_pane_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actions_pane_3Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(actions_pane_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(segmentPane_btn_save)
                    .addComponent(segmentPane_btn_duplicate)
                    .addComponent(segmentPane_btn_delete)
                    .addComponent(segmentPane_btn_clear)
                    .addComponent(segmentPane_btn_refresh))
                .addGap(13, 13, 13))
        );

        segment_jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        segment_jtable.setColumnSelectionAllowed(true);
        segment_table.setViewportView(segment_jtable);

        javax.swing.GroupLayout segment_paneLayout = new javax.swing.GroupLayout(segment_pane);
        segment_pane.setLayout(segment_paneLayout);
        segment_paneLayout.setHorizontalGroup(
            segment_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, segment_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(segment_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(segment_fields, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, segment_paneLayout.createSequentialGroup()
                        .addGroup(segment_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(actions_pane_3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(segment_table, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        segment_paneLayout.setVerticalGroup(
            segment_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(segment_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(segment_fields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actions_pane_3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(segment_table, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        main_tab.addTab("Ségments", segment_pane);

        workplace_pane.setBackground(new java.awt.Color(36, 65, 86));

        workplace_fields.setBackground(new java.awt.Color(36, 65, 86));

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("ID");

        workplacePane_field_id.setEditable(false);
        workplacePane_field_id.setBackground(new java.awt.Color(255, 255, 255));
        workplacePane_field_id.setForeground(new java.awt.Color(0, 0, 0));
        workplacePane_field_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workplacePane_field_idActionPerformed(evt);
            }
        });

        workplacePane_field_project.setBackground(new java.awt.Color(204, 204, 255));
        workplacePane_field_project.setForeground(new java.awt.Color(0, 0, 0));
        workplacePane_field_project.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workplacePane_field_projectActionPerformed(evt);
            }
        });

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Projet");

        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Workplace");

        workplacePane_field_workplace.setBackground(new java.awt.Color(204, 204, 255));

        workplacePane_field_segment.setBackground(new java.awt.Color(204, 204, 255));
        workplacePane_field_segment.setForeground(new java.awt.Color(0, 0, 0));

        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("Ségment");

        javax.swing.GroupLayout workplace_fieldsLayout = new javax.swing.GroupLayout(workplace_fields);
        workplace_fields.setLayout(workplace_fieldsLayout);
        workplace_fieldsLayout.setHorizontalGroup(
            workplace_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workplace_fieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(workplace_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(workplace_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(workplacePane_field_project, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(workplacePane_field_id, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(workplacePane_field_segment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(workplacePane_field_workplace))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        workplace_fieldsLayout.setVerticalGroup(
            workplace_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, workplace_fieldsLayout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(workplace_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(workplacePane_field_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(workplace_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(workplacePane_field_project, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(workplace_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(workplacePane_field_segment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(workplace_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(workplacePane_field_workplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        workplace_jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        workplace_jtable.setColumnSelectionAllowed(true);
        workplace_table.setViewportView(workplace_jtable);

        actions_pane_4.setBackground(new java.awt.Color(36, 65, 86));

        workplacePane_btn_delete.setText("Supprimer");
        workplacePane_btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workplacePane_btn_deleteActionPerformed(evt);
            }
        });

        workplacePane_btn_duplicate.setText("Dupliquer");
        workplacePane_btn_duplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workplacePane_btn_duplicateActionPerformed(evt);
            }
        });

        workplacePane_btn_save.setText("Enregistrer");
        workplacePane_btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workplacePane_btn_saveActionPerformed(evt);
            }
        });

        workplacePane_btn_clear.setText("Réinitialiser");
        workplacePane_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workplacePane_btn_clearActionPerformed(evt);
            }
        });

        workplacePane_btn_refresh.setText("Actualiser");
        workplacePane_btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workplacePane_btn_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout actions_pane_4Layout = new javax.swing.GroupLayout(actions_pane_4);
        actions_pane_4.setLayout(actions_pane_4Layout);
        actions_pane_4Layout.setHorizontalGroup(
            actions_pane_4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actions_pane_4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(workplacePane_btn_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(workplacePane_btn_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(workplacePane_btn_duplicate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(workplacePane_btn_clear)
                .addGap(104, 104, 104)
                .addComponent(workplacePane_btn_delete)
                .addContainerGap(418, Short.MAX_VALUE))
        );
        actions_pane_4Layout.setVerticalGroup(
            actions_pane_4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actions_pane_4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(actions_pane_4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(workplacePane_btn_save)
                    .addComponent(workplacePane_btn_duplicate)
                    .addComponent(workplacePane_btn_delete)
                    .addComponent(workplacePane_btn_clear)
                    .addComponent(workplacePane_btn_refresh))
                .addGap(370, 370, 370))
        );

        javax.swing.GroupLayout workplace_paneLayout = new javax.swing.GroupLayout(workplace_pane);
        workplace_pane.setLayout(workplace_paneLayout);
        workplace_paneLayout.setHorizontalGroup(
            workplace_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workplace_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(workplace_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(workplace_fields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(actions_pane_4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(workplace_paneLayout.createSequentialGroup()
                        .addComponent(workplace_table, javax.swing.GroupLayout.PREFERRED_SIZE, 605, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        workplace_paneLayout.setVerticalGroup(
            workplace_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workplace_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(workplace_fields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actions_pane_4, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(workplace_table)
                .addContainerGap())
        );

        main_tab.addTab("Wokrplaces", workplace_pane);

        magasin_pane.setBackground(new java.awt.Color(36, 65, 86));
        magasin_pane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                magasin_paneFocusGained(evt);
            }
        });

        warehouse_fields.setBackground(new java.awt.Color(36, 65, 86));

        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("ID");

        warehousePane_field_id.setEditable(false);
        warehousePane_field_id.setBackground(new java.awt.Color(255, 255, 255));
        warehousePane_field_id.setForeground(new java.awt.Color(0, 0, 0));

        warehousePane_field_project.setBackground(new java.awt.Color(204, 204, 255));
        warehousePane_field_project.setForeground(new java.awt.Color(0, 0, 0));

        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Projet");

        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Magasin");

        warehousePane_field_name.setBackground(new java.awt.Color(204, 204, 255));

        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Description");

        warehousePane_field_WhType.setBackground(new java.awt.Color(204, 204, 255));
        warehousePane_field_WhType.setForeground(new java.awt.Color(0, 0, 0));
        warehousePane_field_WhType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FINISHED_GOODS", "PACKAGING", "INVENTORY", "SCRAP", "TRANSIT", "WIRES", "RAW_MATERIAL" }));

        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Type");

        warehousePane_field_desc.setBackground(new java.awt.Color(204, 204, 255));
        warehousePane_field_desc.setColumns(20);
        warehousePane_field_desc.setRows(2);
        warehousePane_field_desc.setToolTipText("");
        warehousePane_field_desc.setAutoscrolls(false);
        jScrollPane1.setViewportView(warehousePane_field_desc);

        javax.swing.GroupLayout warehouse_fieldsLayout = new javax.swing.GroupLayout(warehouse_fields);
        warehouse_fields.setLayout(warehouse_fieldsLayout);
        warehouse_fieldsLayout.setHorizontalGroup(
            warehouse_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(warehouse_fieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(warehouse_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(warehouse_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(warehousePane_field_project, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(warehousePane_field_id)
                    .addComponent(warehousePane_field_name, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(96, 96, 96)
                .addGroup(warehouse_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(warehouse_fieldsLayout.createSequentialGroup()
                        .addGroup(warehouse_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(warehousePane_field_WhType, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(141, Short.MAX_VALUE))
        );
        warehouse_fieldsLayout.setVerticalGroup(
            warehouse_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, warehouse_fieldsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(warehouse_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(warehouse_fieldsLayout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(warehouse_fieldsLayout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(warehouse_fieldsLayout.createSequentialGroup()
                        .addComponent(warehousePane_field_id, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(warehousePane_field_project, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(warehousePane_field_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(warehouse_fieldsLayout.createSequentialGroup()
                        .addGroup(warehouse_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(warehousePane_field_WhType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31))
        );

        actions_pane_5.setBackground(new java.awt.Color(36, 65, 86));

        warehousePane_btn_delete.setText("Supprimer");
        warehousePane_btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warehousePane_btn_deleteActionPerformed(evt);
            }
        });

        warehousePane_btn_duplicate.setText("Dupliquer");
        warehousePane_btn_duplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warehousePane_btn_duplicateActionPerformed(evt);
            }
        });

        warehousePane_btn_save.setText("Enregistrer");
        warehousePane_btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warehousePane_btn_saveActionPerformed(evt);
            }
        });

        warehousePane_btn_clear.setText("Réinitialiser");
        warehousePane_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warehousePane_btn_clearActionPerformed(evt);
            }
        });

        warehousePane_btn_refresh.setText("Actualiser");
        warehousePane_btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warehousePane_btn_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout actions_pane_5Layout = new javax.swing.GroupLayout(actions_pane_5);
        actions_pane_5.setLayout(actions_pane_5Layout);
        actions_pane_5Layout.setHorizontalGroup(
            actions_pane_5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actions_pane_5Layout.createSequentialGroup()
                .addComponent(warehousePane_btn_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warehousePane_btn_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warehousePane_btn_duplicate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warehousePane_btn_clear)
                .addGap(130, 130, 130)
                .addComponent(warehousePane_btn_delete)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        actions_pane_5Layout.setVerticalGroup(
            actions_pane_5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actions_pane_5Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(actions_pane_5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(warehousePane_btn_save)
                    .addComponent(warehousePane_btn_duplicate)
                    .addComponent(warehousePane_btn_delete)
                    .addComponent(warehousePane_btn_clear)
                    .addComponent(warehousePane_btn_refresh))
                .addContainerGap())
        );

        warehouse_jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        warehouse_jtable.setCellSelectionEnabled(true);
        warehouse_jtable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        magasin_table.setViewportView(warehouse_jtable);

        javax.swing.GroupLayout magasin_paneLayout = new javax.swing.GroupLayout(magasin_pane);
        magasin_pane.setLayout(magasin_paneLayout);
        magasin_paneLayout.setHorizontalGroup(
            magasin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(magasin_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(magasin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(magasin_paneLayout.createSequentialGroup()
                        .addComponent(magasin_table, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(warehouse_fields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(actions_pane_5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        magasin_paneLayout.setVerticalGroup(
            magasin_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(magasin_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(warehouse_fields, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(actions_pane_5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(magasin_table, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE))
        );

        main_tab.addTab("Magasins", magasin_pane);

        transporter_pane.setBackground(new java.awt.Color(36, 65, 86));

        transporter_fields.setBackground(new java.awt.Color(36, 65, 86));

        transporterPane_label_id.setForeground(new java.awt.Color(255, 255, 255));
        transporterPane_label_id.setText("ID");

        transporterPane_field_id.setEditable(false);
        transporterPane_field_id.setBackground(new java.awt.Color(255, 255, 255));
        transporterPane_field_id.setForeground(new java.awt.Color(0, 0, 0));

        transporterPane_field_name.setBackground(new java.awt.Color(204, 204, 255));

        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Nom");

        javax.swing.GroupLayout transporter_fieldsLayout = new javax.swing.GroupLayout(transporter_fields);
        transporter_fields.setLayout(transporter_fieldsLayout);
        transporter_fieldsLayout.setHorizontalGroup(
            transporter_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transporter_fieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(transporter_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(transporter_fieldsLayout.createSequentialGroup()
                        .addComponent(transporterPane_label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(transporterPane_field_id, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(transporter_fieldsLayout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(transporterPane_field_name, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        transporter_fieldsLayout.setVerticalGroup(
            transporter_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transporter_fieldsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(transporter_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(transporterPane_label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(transporterPane_field_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(transporter_fieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(transporterPane_field_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        actions_pane_6.setBackground(new java.awt.Color(36, 65, 86));

        transporterPane_btn_delete.setText("Supprimer");
        transporterPane_btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transporterPane_btn_deleteActionPerformed(evt);
            }
        });

        transporterPane_btn_duplicate.setText("Dupliquer");
        transporterPane_btn_duplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transporterPane_btn_duplicateActionPerformed(evt);
            }
        });

        transporterPane_btn_save.setText("Enregistrer");
        transporterPane_btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transporterPane_btn_saveActionPerformed(evt);
            }
        });

        transporterPane_btn_clear.setText("Réinitialiser");
        transporterPane_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transporterPane_btn_clearActionPerformed(evt);
            }
        });

        transporterPane_btn_refresh.setText("Actualiser");
        transporterPane_btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transporterPane_btn_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout actions_pane_6Layout = new javax.swing.GroupLayout(actions_pane_6);
        actions_pane_6.setLayout(actions_pane_6Layout);
        actions_pane_6Layout.setHorizontalGroup(
            actions_pane_6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actions_pane_6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(transporterPane_btn_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(transporterPane_btn_save, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(transporterPane_btn_duplicate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(transporterPane_btn_clear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                .addComponent(transporterPane_btn_delete)
                .addContainerGap())
        );
        actions_pane_6Layout.setVerticalGroup(
            actions_pane_6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actions_pane_6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(actions_pane_6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(transporterPane_btn_save)
                    .addComponent(transporterPane_btn_duplicate)
                    .addComponent(transporterPane_btn_delete)
                    .addComponent(transporterPane_btn_clear)
                    .addComponent(transporterPane_btn_refresh))
                .addGap(44, 44, 44))
        );

        transporter_table.setBackground(new java.awt.Color(36, 65, 86));

        transporter_jtable.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        transporter_jtable.setColumnSelectionAllowed(true);
        transporter_jtable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        transporter_jtable.setUpdateSelectionOnSort(false);
        transporter_table.setViewportView(transporter_jtable);

        javax.swing.GroupLayout transporter_paneLayout = new javax.swing.GroupLayout(transporter_pane);
        transporter_pane.setLayout(transporter_paneLayout);
        transporter_paneLayout.setHorizontalGroup(
            transporter_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transporter_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(transporter_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(transporter_fields, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, transporter_paneLayout.createSequentialGroup()
                        .addGroup(transporter_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(transporter_table, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actions_pane_6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 376, Short.MAX_VALUE)))
                .addContainerGap())
        );
        transporter_paneLayout.setVerticalGroup(
            transporter_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transporter_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(transporter_fields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actions_pane_6, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(transporter_table, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                .addContainerGap())
        );

        main_tab.addTab("Transporteurs", transporter_pane);

        dispatch_scan_panel.setBackground(new java.awt.Color(36, 65, 86));

        project_fields1.setBackground(new java.awt.Color(36, 65, 86));

        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("ID");

        dispatchScanPane_field_id.setEditable(false);
        dispatchScanPane_field_id.setBackground(new java.awt.Color(255, 255, 255));
        dispatchScanPane_field_id.setForeground(new java.awt.Color(0, 0, 0));

        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setText("Comparer le code article client à partir du caractère numéro...");

        dispatchScanPane_field_odette_pn_begin.setBackground(new java.awt.Color(204, 204, 255));

        dispatchScanPane_field_project.setBackground(new java.awt.Color(204, 204, 255));
        dispatchScanPane_field_project.setForeground(new java.awt.Color(0, 0, 0));

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Projet");

        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("jusqu'à");

        dispatchScanPane_field_odette_pn_end.setBackground(new java.awt.Color(204, 204, 255));

        javax.swing.GroupLayout project_fields1Layout = new javax.swing.GroupLayout(project_fields1);
        project_fields1.setLayout(project_fields1Layout);
        project_fields1Layout.setHorizontalGroup(
            project_fields1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(project_fields1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(project_fields1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(project_fields1Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dispatchScanPane_field_odette_pn_begin, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dispatchScanPane_field_odette_pn_end, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(project_fields1Layout.createSequentialGroup()
                        .addGroup(project_fields1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(project_fields1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dispatchScanPane_field_id, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(project_fields1Layout.createSequentialGroup()
                                .addComponent(dispatchScanPane_field_project, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(6, 6, 6)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        project_fields1Layout.setVerticalGroup(
            project_fields1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, project_fields1Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(project_fields1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dispatchScanPane_field_id, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(project_fields1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dispatchScanPane_field_project, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(project_fields1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dispatchScanPane_field_odette_pn_begin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dispatchScanPane_field_odette_pn_end, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43))
        );

        dispatchControl_jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        dispatchControl_jtable.setColumnSelectionAllowed(true);
        project_table1.setViewportView(dispatchControl_jtable);

        actions_pane_8.setBackground(new java.awt.Color(36, 65, 86));

        dispatchControlPane_btn_delete.setText("Supprimer");
        dispatchControlPane_btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispatchControlPane_btn_deleteActionPerformed(evt);
            }
        });

        dispatchControlPane_btn_duplicate.setText("Dupliquer");
        dispatchControlPane_btn_duplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispatchControlPane_btn_duplicateActionPerformed(evt);
            }
        });

        dispatchControlPane_btn_save.setText("Enregistrer");
        dispatchControlPane_btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispatchControlPane_btn_saveActionPerformed(evt);
            }
        });

        dispatchControlPane_btn_clear.setText("Réinitialiser");
        dispatchControlPane_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispatchControlPane_btn_clearActionPerformed(evt);
            }
        });

        dispatchControlPane_btn_refresh.setText("Actualiser");
        dispatchControlPane_btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispatchControlPane_btn_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout actions_pane_8Layout = new javax.swing.GroupLayout(actions_pane_8);
        actions_pane_8.setLayout(actions_pane_8Layout);
        actions_pane_8Layout.setHorizontalGroup(
            actions_pane_8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actions_pane_8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dispatchControlPane_btn_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dispatchControlPane_btn_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dispatchControlPane_btn_duplicate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dispatchControlPane_btn_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                .addComponent(dispatchControlPane_btn_delete)
                .addGap(48, 48, 48))
        );
        actions_pane_8Layout.setVerticalGroup(
            actions_pane_8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actions_pane_8Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(actions_pane_8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dispatchControlPane_btn_save)
                    .addComponent(dispatchControlPane_btn_duplicate)
                    .addComponent(dispatchControlPane_btn_delete)
                    .addComponent(dispatchControlPane_btn_clear)
                    .addComponent(dispatchControlPane_btn_refresh))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout dispatch_scan_panelLayout = new javax.swing.GroupLayout(dispatch_scan_panel);
        dispatch_scan_panel.setLayout(dispatch_scan_panelLayout);
        dispatch_scan_panelLayout.setHorizontalGroup(
            dispatch_scan_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dispatch_scan_panelLayout.createSequentialGroup()
                .addComponent(actions_pane_8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 356, Short.MAX_VALUE))
            .addGroup(dispatch_scan_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dispatch_scan_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(project_fields1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(dispatch_scan_panelLayout.createSequentialGroup()
                        .addComponent(project_table1, javax.swing.GroupLayout.PREFERRED_SIZE, 622, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        dispatch_scan_panelLayout.setVerticalGroup(
            dispatch_scan_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dispatch_scan_panelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(project_fields1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actions_pane_8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(project_table1, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        main_tab.addTab("Control Odette", dispatch_scan_panel);

        msg_lbl.setEditable(false);
        msg_lbl.setBackground(new java.awt.Color(255, 255, 255));
        msg_lbl.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        msg_lbl.setForeground(new java.awt.Color(0, 0, 204));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(main_tab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(176, 176, 176)
                    .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 733, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(128, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(main_tab))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(699, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void companyPane_btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_companyPane_btn_saveActionPerformed

        //######################################################################
        //                    Creating new Element
        //######################################################################
        List<JTextComponent> form = new ArrayList<>();
        form.add(companyPane_field_name);
        form.add(companyPane_field_address1);
        form.add(companyPane_field_city);
        form.add(companyPane_field_zip);
        form.add(companyPane_field_description);
        form.add(companyPane_field_country);
        if (!GlobalMethods.checkEmptyFields(form)) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0048, "Erreur de sauvegarde!");
            msg_lbl.setText(UILog.severe(ErrorMsg.APP_ERR0048[0]));
        } else {

            if (companyPane_field_id.getText().isEmpty()) { //new element

                ConfigCompany newObj = new ConfigCompany(
                        companyPane_field_name.getText(),
                        companyPane_field_description.getText(),
                        companyPane_field_address1.getText(),
                        companyPane_field_address2.getText(),
                        companyPane_field_country.getText(),
                        companyPane_field_city.getText(),
                        companyPane_field_zip.getText(),
                        companyPane_field_website.getText()
                );
                try {
                    int newId = newObj.create(newObj);
                    String msg = "Nouveau élément #" + newId + " enregistré !";
                    msg_lbl.setText(msg);
                    GlobalMethods.clearPaneFieldsValues(company_fields);
                    refreshCompanyTable();
                } catch (ConstraintViolationException e) {
                    BatchUpdateException batchUpdateException = (BatchUpdateException) e.getCause();
                    Exception psqlException = batchUpdateException.getNextException();
                    String msg = psqlException.getLocalizedMessage();
                    msg_lbl.setText("Valeur unique dupliqué !");
                    UILog.severeDialog(this, msg, "");
                }
            } else {//Editing existing element                
                auxCompany.setName(companyPane_field_name.getText());
                auxCompany.setDesc(companyPane_field_description.getText());
                auxCompany.setAddress1(companyPane_field_address1.getText());
                auxCompany.setAddress2(companyPane_field_address2.getText());
                auxCompany.setCountry(companyPane_field_country.getText());
                auxCompany.setCity(companyPane_field_city.getText());
                auxCompany.setZip(companyPane_field_zip.getText());
                auxCompany.setWebsite(companyPane_field_website.getText());
                auxCompany.update(auxCompany);
                String[] msg = {"Changements enregistrés"};
                msg_lbl.setText(msg[0]);
                GlobalMethods.clearPaneFieldsValues(company_fields);
                refreshCompanyTable();
            }
        }
    }//GEN-LAST:event_companyPane_btn_saveActionPerformed


    private void transporterPane_btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transporterPane_btn_saveActionPerformed

        //######################################################################
        //                    Creating new Element
        //######################################################################
        if (transporterPane_field_id.getText().isEmpty()) { //new element
            if (transporterPane_field_name.getText().isEmpty()) {
                UILog.severeDialog(this, ErrorMsg.APP_ERR0048, "Erreur de sauvegarde!");
                msg_lbl.setText(UILog.severe(ErrorMsg.APP_ERR0048[0]));
            } else {
                ConfigTransporter newObj = new ConfigTransporter(transporterPane_field_name.getText());
                try {
                    int newId = newObj.create(newObj);
                    String msg = "Nouveau élément " + newId + " enregistré !";
                    msg_lbl.setText(msg);
                    GlobalMethods.clearPaneFieldsValues(transporter_fields);
                    refreshTransportersTable();
                } catch (ConstraintViolationException e) {
                    BatchUpdateException batchUpdateException = (BatchUpdateException) e.getCause();
                    Exception psqlException = batchUpdateException.getNextException();
                    String msg = psqlException.getLocalizedMessage();
                    msg_lbl.setText("Valeur unique dupliqué !");
                    UILog.severeDialog(this, msg, "");
                }
            }
        } //######################################################################
        //
        //                    UPDATE an existing item
        //
        //######################################################################        
        else {//Editing existing element
            auxTransporter.setName(transporterPane_field_name.getText());
            auxTransporter.update(auxTransporter);
            String[] msg = {"Changements enregistrés"};
            msg_lbl.setText(msg[0]);
            GlobalMethods.clearPaneFieldsValues(transporter_fields);
            refreshTransportersTable();
        }

    }//GEN-LAST:event_transporterPane_btn_saveActionPerformed

    /*
    ############################################################################    
    *************************** Clear Field Action  ****************************
    ############################################################################    
     */

    private void companyPane_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_companyPane_btn_clearActionPerformed
        GlobalMethods.clearPaneFieldsValues(company_fields);
    }//GEN-LAST:event_companyPane_btn_clearActionPerformed

    private void transporterPane_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transporterPane_btn_clearActionPerformed
        GlobalMethods.clearPaneFieldsValues(transporter_fields);
    }//GEN-LAST:event_transporterPane_btn_clearActionPerformed

    private void projectPane_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectPane_btn_clearActionPerformed
        projectPane_field_desc.setText("");
        GlobalMethods.clearPaneFieldsValues(project_fields);
    }//GEN-LAST:event_projectPane_btn_clearActionPerformed

    private void familyPane_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_familyPane_btn_clearActionPerformed
        GlobalMethods.clearPaneFieldsValues(family_fields);
    }//GEN-LAST:event_familyPane_btn_clearActionPerformed

    private void segmentPane_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentPane_btn_clearActionPerformed
        GlobalMethods.clearPaneFieldsValues(segment_fields);
    }//GEN-LAST:event_segmentPane_btn_clearActionPerformed

    private void workplacePane_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplacePane_btn_clearActionPerformed
        GlobalMethods.clearPaneFieldsValues(workplace_fields);
    }//GEN-LAST:event_workplacePane_btn_clearActionPerformed

    private void warehousePane_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warehousePane_btn_clearActionPerformed
        warehousePane_field_desc.setText("");
        GlobalMethods.clearPaneFieldsValues(warehouse_fields);
    }//GEN-LAST:event_warehousePane_btn_clearActionPerformed

    /*
    ############################################################################    
    *************************** Family  METHODS ****************************
    ############################################################################    
     */

    private void projectPane_btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectPane_btn_deleteActionPerformed
        try {
            deleteAction(this.auxProject, this.auxProject.getId(), project_fields, msg_lbl);
            refreshProjectTable();
        } catch (Exception e) {
            UILog.errorDialog("Veuillez sélectionner un élément à supprimer !");
        }
    }//GEN-LAST:event_projectPane_btn_deleteActionPerformed

    private void familyPane_btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_familyPane_btn_deleteActionPerformed
        try {
            deleteAction(this.auxFamily, this.auxFamily.getId(), family_fields, msg_lbl);
            refreshFamilyTable();
        } catch (Exception e) {
            UILog.errorDialog("Veuillez sélectionner un élément à supprimer !");
        }
    }//GEN-LAST:event_familyPane_btn_deleteActionPerformed

    /*
    ############################################################################    
    *************************** TRANSPORTER METHODS ****************************
    ############################################################################    
     */

    private void transporterPane_btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transporterPane_btn_deleteActionPerformed
        try {
            deleteAction(this.auxTransporter, this.auxTransporter.getId(), transporter_fields, msg_lbl);
            refreshTransportersTable();
        } catch (Exception e) {
            UILog.errorDialog("Veuillez sélectionner un élément à supprimer !");
        }
    }//GEN-LAST:event_transporterPane_btn_deleteActionPerformed

    private void initTransporterTableDoubleClick() {
        this.transporter_jtable.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_TRANSPORTER_BY_ID);
                    query.setParameter("id", Integer.valueOf(transporter_jtable.getValueAt(transporter_jtable.getSelectedRow(), 0).toString()));
                    Helper.sess.getTransaction().commit();
                    auxTransporter = (ConfigTransporter) query.list().get(0);

                    transporterPane_field_id.setText(auxTransporter.getId().toString());
                    transporterPane_field_name.setText(auxTransporter.getName());

                    //transporterPane_btn_delete.setEnabled(true);
                    //transporterPane_btn_duplicate.setEnabled(true);
                }
            }
        }
        );
    }

    private void transporterPane_btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transporterPane_btn_refreshActionPerformed
        refreshTransportersTable();
        initTransporterTableDoubleClick();
        GlobalMethods.disableEditingTable(transporter_jtable);
    }//GEN-LAST:event_transporterPane_btn_refreshActionPerformed

    private void transporterPane_btn_duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transporterPane_btn_duplicateActionPerformed
        transporterPane_field_id.setText("");
        msg_lbl.setText("Elément dupliqué !");
    }//GEN-LAST:event_transporterPane_btn_duplicateActionPerformed

    /*
    ############################################################################    
    *************************** Warehouse METHODS ****************************
    ############################################################################    
     */

    private void warehousePane_btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warehousePane_btn_refreshActionPerformed
        refreshWarehouseTable();
        initWarehouseTableDoubleClick();
        GlobalMethods.disableEditingTable(warehouse_jtable);
    }//GEN-LAST:event_warehousePane_btn_refreshActionPerformed

    /*
    ############################################################################    
    *************************** Workplace  METHODS ****************************
    ############################################################################    
     */

    private void workplacePane_btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplacePane_btn_refreshActionPerformed
        refreshWorkplaceTable();
        initWorkplaceTableDoubleClick();
        GlobalMethods.disableEditingTable(workplace_jtable);
    }//GEN-LAST:event_workplacePane_btn_refreshActionPerformed

    private void magasin_paneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_magasin_paneFocusGained

    }//GEN-LAST:event_magasin_paneFocusGained

    private void warehousePane_btn_duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warehousePane_btn_duplicateActionPerformed
        warehousePane_field_id.setText("");
        msg_lbl.setText("Elément dupliqué !");
    }//GEN-LAST:event_warehousePane_btn_duplicateActionPerformed

    private void warehousePane_btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warehousePane_btn_deleteActionPerformed
        try {
            deleteAction(this.auxWarehouse, this.auxWarehouse.getId(), warehouse_fields, msg_lbl);
            refreshWarehouseTable();
        } catch (Exception e) {
            UILog.errorDialog("Veuillez sélectionner un élément à supprimer !");
        }
    }//GEN-LAST:event_warehousePane_btn_deleteActionPerformed

    private void warehousePane_btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warehousePane_btn_saveActionPerformed
        //######################################################################
        //                    Creating new Element
        //######################################################################
        if (warehousePane_field_id.getText().isEmpty()) { //new element
            List<JTextComponent> form = new ArrayList<>();
            form.add(warehousePane_field_name);
            form.add(warehousePane_field_desc);

            if (!GlobalMethods.checkEmptyFields(form)) {
                UILog.severeDialog(this, ErrorMsg.APP_ERR0048, "Erreur de sauvegarde!");
                msg_lbl.setText(UILog.severe(ErrorMsg.APP_ERR0048[0]));
                warehousePane_field_name.requestFocus();
            } else {
                ConfigWarehouse newObj = new ConfigWarehouse(
                        warehousePane_field_name.getText(),
                        warehousePane_field_desc.getText(),
                        warehousePane_field_project.getSelectedItem().toString(),
                        warehousePane_field_WhType.getSelectedItem().toString()
                );
                try {
                    int newId = newObj.create(newObj);
                    String msg = "Nouveau élément " + newId + " enregistré !";
                    msg_lbl.setText(msg);
                    GlobalMethods.clearPaneFieldsValues(warehouse_fields);
                    refreshWarehouseTable();
                } catch (ConstraintViolationException e) {
                    BatchUpdateException batchUpdateException = (BatchUpdateException) e.getCause();
                    Exception psqlException = batchUpdateException.getNextException();
                    String msg = psqlException.getLocalizedMessage();
                    msg_lbl.setText("Valeur unique dupliqué !");
                    UILog.severeDialog(this, msg, "");
                }
            }
        } else { // is an update
            auxWarehouse.setWarehouse(warehousePane_field_name.getText());
            auxWarehouse.setDescription(warehousePane_field_desc.getText());
            auxWarehouse.setProject(warehousePane_field_project.getSelectedItem().toString());
            auxWarehouse.setWhType(warehousePane_field_WhType.getSelectedItem().toString());
            auxWarehouse.update(auxWarehouse);
            String[] msg = {"Changements enregistrés"};
            msg_lbl.setText(msg[0]);
            GlobalMethods.clearPaneFieldsValues(warehouse_fields);
            refreshWarehouseTable();
        }
    }//GEN-LAST:event_warehousePane_btn_saveActionPerformed

    private void workplacePane_btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplacePane_btn_saveActionPerformed
        List<JTextComponent> form = new ArrayList<>();
        form.add(workplacePane_field_workplace);
        if (!GlobalMethods.checkEmptyFields(form)) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0048, "Erreur de sauvegarde!");
            msg_lbl.setText(UILog.severe(ErrorMsg.APP_ERR0048[0]));
        } else {
            //######################################################################
            //                    Creating new Element
            //######################################################################
            if (workplacePane_field_id.getText().isEmpty()) { //new element

                ConfigWorkplace newObj = new ConfigWorkplace(
                        workplacePane_field_project.getSelectedItem().toString(),
                        workplacePane_field_segment.getSelectedItem().toString(),
                        workplacePane_field_workplace.getText()
                );
                try {
                    int newId = newObj.create(newObj);
                    String msg = "Nouveau élément " + newId + " enregistré !";
                    msg_lbl.setText(msg);
                    GlobalMethods.clearPaneFieldsValues(workplace_fields);
                    refreshWorkplaceTable();
                } catch (ConstraintViolationException e) {
                    BatchUpdateException batchUpdateException = (BatchUpdateException) e.getCause();
                    Exception psqlException = batchUpdateException.getNextException();
                    String msg = psqlException.getLocalizedMessage();
                    msg_lbl.setText("Valeur unique dupliqué !");
                    UILog.severeDialog(this, msg, "");
                }
            } else { // is an update
                auxWorkplace.setProject(workplacePane_field_project.getSelectedItem().toString());
                auxWorkplace.setSegment(workplacePane_field_segment.getSelectedItem().toString());
                auxWorkplace.setWorkplace(workplacePane_field_workplace.getText());
                auxWorkplace.update(auxWorkplace);
                String[] msg = {"Changements enregistrés"};
                msg_lbl.setText(msg[0]);
                GlobalMethods.clearPaneFieldsValues(workplace_fields);
                refreshWorkplaceTable();
                GlobalMethods.disableEditingTable(workplace_jtable);
            }
        }
    }//GEN-LAST:event_workplacePane_btn_saveActionPerformed

    private void segmentPane_btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentPane_btn_saveActionPerformed

        List<JTextComponent> form = new ArrayList<>();
        form.add(segmentPane_field_segment);
        if (!GlobalMethods.checkEmptyFields(form)) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0048, "Erreur de sauvegarde!");
            msg_lbl.setText(UILog.severe(ErrorMsg.APP_ERR0048[0]));
        } else {
            //######################################################################
            //                    Creating new Element
            //######################################################################
            if (segmentPane_field_id.getText().isEmpty()) { //new element

                ConfigSegment newObj = new ConfigSegment(segmentPane_field_segment.getText(),
                        segmentPane_field_project.getSelectedItem().toString());
                try {
                    int newId = newObj.create(newObj);
                    String msg = "Nouveau élément " + newId + " enregistré !";
                    msg_lbl.setText(msg);
                    GlobalMethods.clearPaneFieldsValues(segment_fields);
                    refreshSegmentTable();
                } catch (ConstraintViolationException e) {
                    BatchUpdateException batchUpdateException = (BatchUpdateException) e.getCause();
                    Exception psqlException = batchUpdateException.getNextException();
                    String msg = psqlException.getLocalizedMessage();
                    msg_lbl.setText("Valeur unique dupliqué !");
                    UILog.severeDialog(this, msg, "");
                }

            } else { // is an update
                auxSegment.setSegment(segmentPane_field_segment.getText());
                auxSegment.setProject(segmentPane_field_project.getSelectedItem().toString());
                auxSegment.update(auxSegment);
                String[] msg = {"Changements enregistrés"};
                msg_lbl.setText(msg[0]);
                GlobalMethods.clearPaneFieldsValues(segment_fields);
                refreshSegmentTable();
                GlobalMethods.disableEditingTable(segment_jtable);
            }
        }
    }//GEN-LAST:event_segmentPane_btn_saveActionPerformed

    private void familyPane_btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_familyPane_btn_saveActionPerformed

        List<JTextComponent> form = new ArrayList<>();
        form.add(familyPane_field_name);
        if (!GlobalMethods.checkEmptyFields(form)) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0048, "Erreur de sauvegarde!");
            msg_lbl.setText(UILog.severe(ErrorMsg.APP_ERR0048[0]));
        } else {

            //######################################################################
            //                    Creating new Element
            //######################################################################
            if (familyPane_field_id.getText().isEmpty()) { //new element

                ConfigFamily newObj = new ConfigFamily(
                        familyPane_field_name.getText(),
                        familyPane_field_project.getSelectedItem().toString());
                try {
                    int newId = newObj.create(newObj);
                    String msg = "Nouveau élément " + newId + " enregistré !";
                    msg_lbl.setText(msg);
                    GlobalMethods.clearPaneFieldsValues(family_fields);
                    refreshFamilyTable();
                } catch (ConstraintViolationException e) {
                    BatchUpdateException batchUpdateException = (BatchUpdateException) e.getCause();
                    Exception psqlException = batchUpdateException.getNextException();
                    String msg = psqlException.getLocalizedMessage();
                    msg_lbl.setText("Valeur unique dupliqué !");
                    UILog.severeDialog(this, msg, "");
                }

            } else { // is an update
                auxFamily.setFamily(familyPane_field_name.getText());
                auxFamily.setProject(familyPane_field_project.getSelectedItem().toString());
                auxFamily.update(auxFamily);
                String[] msg = {"Changements enregistrés"};
                msg_lbl.setText(msg[0]);
                GlobalMethods.clearPaneFieldsValues(family_fields);
                refreshFamilyTable();
            }
        }
    }//GEN-LAST:event_familyPane_btn_saveActionPerformed

    private void projectPane_btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectPane_btn_saveActionPerformed
        List<JTextComponent> form = new ArrayList<>();
        form.add(projectPane_field_project);
        form.add(projectPane_field_desc);
        if (!GlobalMethods.checkEmptyFields(form)) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0048, "Erreur de sauvegarde!");
            msg_lbl.setText(UILog.severe(ErrorMsg.APP_ERR0048[0]));
        } else {
            //######################################################################
            //                    Creating new Element
            //######################################################################
            if (projectPane_field_id.getText().isEmpty()) { //new element
                ConfigProject newObj = new ConfigProject(
                        projectPane_field_project.getText(),
                        projectPane_field_desc.getText());
                try {
                    int newId = newObj.create(newObj);
                    String msg = "Nouveau élément " + newId + " enregistré !";
                    msg_lbl.setText(msg);
                    GlobalMethods.clearPaneFieldsValues(project_fields);
                    refreshProjectTable();
                } catch (ConstraintViolationException e) {
                    BatchUpdateException batchUpdateException = (BatchUpdateException) e.getCause();
                    Exception psqlException = batchUpdateException.getNextException();
                    String msg = psqlException.getLocalizedMessage();
                    msg_lbl.setText("Valeur unique dupliqué !");
                    UILog.severeDialog(this, msg, "");
                }
            } else { // is an update
                auxProject.setProject(projectPane_field_project.getText());
                auxProject.setDescription(projectPane_field_desc.getText());
                auxProject.update(auxProject);
                String[] msg = {"Changements enregistrés"};
                msg_lbl.setText(msg[0]);
                GlobalMethods.clearPaneFieldsValues(project_fields);
                refreshProjectTable();
            }
        }
    }//GEN-LAST:event_projectPane_btn_saveActionPerformed

    private void workplacePane_btn_duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplacePane_btn_duplicateActionPerformed
        workplacePane_field_id.setText("");
        msg_lbl.setText("Elément dupliqué !");
    }//GEN-LAST:event_workplacePane_btn_duplicateActionPerformed

    private void workplacePane_btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplacePane_btn_deleteActionPerformed
        try {
            deleteAction(this.auxWorkplace, this.auxWorkplace.getId(), workplace_fields, msg_lbl);
            refreshWorkplaceTable();
        } catch (Exception e) {
            UILog.errorDialog("Veuillez sélectionner un élément à supprimer !");
        }
    }//GEN-LAST:event_workplacePane_btn_deleteActionPerformed

    private void segmentPane_btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentPane_btn_deleteActionPerformed
        try {
            deleteAction(this.auxSegment, this.auxSegment.getId(), segment_fields, msg_lbl);
            refreshSegmentTable();
        } catch (Exception e) {
            UILog.errorDialog("Veuillez sélectionner un élément à supprimer !");
        }
    }//GEN-LAST:event_segmentPane_btn_deleteActionPerformed

    private void companyPane_btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_companyPane_btn_deleteActionPerformed
        try {
            deleteAction(this.auxCompany, this.auxCompany.getId(), company_fields, msg_lbl);
            refreshCompanyTable();
        } catch (Exception e) {
            UILog.errorDialog("Veuillez sélectionner un élément à supprimer !");
        }
    }//GEN-LAST:event_companyPane_btn_deleteActionPerformed

    private void companyPane_btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_companyPane_btn_refreshActionPerformed
        refreshCompanyTable();
        initCompanyTableDoubleClick();
        GlobalMethods.disableEditingTable(company_jtable);
    }//GEN-LAST:event_companyPane_btn_refreshActionPerformed

    private void projectPane_btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectPane_btn_refreshActionPerformed
        refreshProjectTable();
        initProjectTableDoubleClick();
        GlobalMethods.disableEditingTable(project_jtable);
    }//GEN-LAST:event_projectPane_btn_refreshActionPerformed

    private void familyPane_btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_familyPane_btn_refreshActionPerformed
        refreshFamilyTable();
        initFamilyTableDoubleClick();
        GlobalMethods.disableEditingTable(family_jtable);
    }//GEN-LAST:event_familyPane_btn_refreshActionPerformed

    private void segmentPane_btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentPane_btn_refreshActionPerformed
        refreshSegmentTable();
        initSegmentTableDoubleClick();
        GlobalMethods.disableEditingTable(segment_jtable);
    }//GEN-LAST:event_segmentPane_btn_refreshActionPerformed

    private void companyPane_btn_duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_companyPane_btn_duplicateActionPerformed
        companyPane_field_id.setText("");
        msg_lbl.setText("Elément dupliqué !");
    }//GEN-LAST:event_companyPane_btn_duplicateActionPerformed

    private void projectPane_btn_duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectPane_btn_duplicateActionPerformed
        projectPane_field_id.setText("");
        msg_lbl.setText("Elément dupliqué !");
    }//GEN-LAST:event_projectPane_btn_duplicateActionPerformed

    private void familyPane_btn_duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_familyPane_btn_duplicateActionPerformed
        familyPane_field_id.setText("");
        msg_lbl.setText("Elément dupliqué !");
    }//GEN-LAST:event_familyPane_btn_duplicateActionPerformed

    private void segmentPane_btn_duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentPane_btn_duplicateActionPerformed
        segmentPane_field_id.setText("");
        msg_lbl.setText("Elément dupliqué !");
    }//GEN-LAST:event_segmentPane_btn_duplicateActionPerformed

    private void workplacePane_field_projectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplacePane_field_projectActionPerformed
        ConfigSegment.setSegmentByProject(this, workplacePane_field_segment, workplacePane_field_project.getSelectedItem().toString(), false);
    }//GEN-LAST:event_workplacePane_field_projectActionPerformed

    private void workplacePane_field_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplacePane_field_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_workplacePane_field_idActionPerformed

    private void dispatchControlPane_btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatchControlPane_btn_deleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatchControlPane_btn_deleteActionPerformed

    private void dispatchControlPane_btn_duplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatchControlPane_btn_duplicateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatchControlPane_btn_duplicateActionPerformed

    private void dispatchControlPane_btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatchControlPane_btn_saveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatchControlPane_btn_saveActionPerformed

    private void dispatchControlPane_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatchControlPane_btn_clearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatchControlPane_btn_clearActionPerformed

    private void dispatchControlPane_btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatchControlPane_btn_refreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatchControlPane_btn_refreshActionPerformed

    /*
    ############################################################################    
    *************************** Commun  METHODS ****************************
    ############################################################################    
     */
    private void deleteAction(DAO object, int id, JPanel pane, JTextField feedBackField) {
        int confirmed = JOptionPane.showConfirmDialog(this,
                String.format("Confirmez-vous la suppression de cet élément [%s] ?",
                        id),
                "Confirmation !",
                JOptionPane.WARNING_MESSAGE);

        if (confirmed == 0) {
            object.delete(object);
            GlobalMethods.clearPaneFieldsValues(pane);
            feedBackField.setText("Element supprimé !");
        }
    }

    private void refreshCompanyTable() {
        Vector companyDataVector = new Vector();

        String query_str = " SELECT "
                + " u.id AS id, "
                + " u.company_name AS company_name, "
                + " u.country AS country, "
                + " u.city AS city "
                + " FROM Config_Company u WHERE 1=1 ";
        query_str += " ORDER BY id ASC ";
        Helper.startSession();
        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        List resultList = query.list();

        Helper.sess.getTransaction().commit();
        GlobalMethods.reset_jtable_data(resultList, companyDataVector, Arrays.asList(
                "ID",
                "NAME",
                "COUNTRY",
                "CITY"
        ),
                company_jtable);
        GlobalMethods.disableEditingTable(company_jtable);
    }

    private void refreshProjectTable() {
        Vector projectDataVector = new Vector();

        String query_str = " SELECT "
                + " u.id AS id, "
                + " u.project AS project, "
                + " u.description AS description "
                + " FROM Config_Project u WHERE 1=1 ";
        query_str += " ORDER BY id ASC ";
        Helper.startSession();
        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        List resultList = query.list();

        Helper.sess.getTransaction().commit();
        GlobalMethods.reset_jtable_data(resultList, projectDataVector, Arrays.asList(
                "ID",
                "PROJECT",
                "DESCRIPTION"
        ),
                project_jtable);
        GlobalMethods.disableEditingTable(project_jtable);
    }

    private void refreshFamilyTable() {
        Vector familyDataVector = new Vector();

        String query_str = " SELECT "
                + " u.id AS id, "
                + " u.harness_type AS harness_type, "
                + " u.project AS project "
                + " FROM Config_Family u WHERE 1=1 ";
        query_str += " ORDER BY id ASC ";
        Helper.startSession();
        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        List resultList = query.list();

        Helper.sess.getTransaction().commit();
        GlobalMethods.reset_jtable_data(resultList, familyDataVector, Arrays.asList(
                "ID",
                "PROJECT",
                "FAMILY"
        ),
                family_jtable);
        
        GlobalMethods.disableEditingTable(family_jtable);
    }

    private void refreshSegmentTable() {
        Vector segmentDataVector = new Vector();

        String query_str = " SELECT "
                + " u.id AS id, "
                + " u.project AS project, "
                + " u.segment AS segment "
                + " FROM Config_Segment u WHERE 1=1 ";
        query_str += " ORDER BY id ASC ";
        Helper.startSession();
        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        List resultList = query.list();

        Helper.sess.getTransaction().commit();
        GlobalMethods.reset_jtable_data(resultList, segmentDataVector, Arrays.asList(
                "ID",
                "PROJECT",
                "SEGMENT"
        ),
                segment_jtable);
    }

    private void refreshWorkplaceTable() {
        Vector wokrplaceDataVector = new Vector();

        String query_str = " SELECT "
                + " u.id AS id, "
                + " u.segment AS segment, "
                + " u.workplace AS workplace, "
                + " u.project AS project "
                + " FROM Config_Workplace u WHERE 1=1 ";
        query_str += " ORDER BY id ASC ";
        Helper.startSession();
        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        List resultList = query.list();

        Helper.sess.getTransaction().commit();
        GlobalMethods.reset_jtable_data(resultList, wokrplaceDataVector, Arrays.asList(
                "ID",
                "SEGMENT",
                "WORKPLACE",
                "PROJECT"
        ),
                workplace_jtable);
    }

    private void refreshWarehouseTable() {
        Vector warehousesDataVector = new Vector();

        String query_str = " SELECT "
                + " u.id AS id, "
                + " u.project AS project, "
                + " u.wh_type AS type, "
                + " u.warehouse AS warehouse, "
                + " u.description AS description "
                + " FROM Config_Warehouse u WHERE 1=1 ";
        query_str += " ORDER BY project ASC ";
        Helper.startSession();
        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        List resultList = query.list();

        Helper.sess.getTransaction().commit();
        GlobalMethods.reset_jtable_data(resultList, warehousesDataVector, Arrays.asList(
                "ID",
                "PROJET",
                "TYPE MAGASIN",
                "NOM",
                "DESIGNATION"
        ),
                warehouse_jtable);
        GlobalMethods.disableEditingTable(warehouse_jtable);
    }

    private void refreshTransportersTable() {
        Vector transportersDataVector = new Vector();

        String query_str = " SELECT "
                + " u.id AS id, "
                + " u.name AS name "
                + " FROM Config_Transporter u WHERE 1=1 ";
        query_str += " ORDER BY id ASC ";
        Helper.startSession();
        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        List resultList = query.list();

        Helper.sess.getTransaction().commit();
        GlobalMethods.reset_jtable_data(resultList, transportersDataVector, Arrays.asList(
                "ID",
                "NAME"), transporter_jtable);
        GlobalMethods.disableEditingTable(transporter_jtable);
    }

    private void initWarehouseTableDoubleClick() {
        this.warehouse_jtable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_WAREHOUSE_BY_ID);
                    query.setParameter("id", Integer.valueOf(warehouse_jtable.getValueAt(warehouse_jtable.getSelectedRow(), 0).toString()));
                    Helper.sess.getTransaction().commit();
                    auxWarehouse = (ConfigWarehouse) query.list().get(0);

                    warehousePane_field_id.setText(auxWarehouse.getId() + "");
                    warehousePane_field_name.setText(auxWarehouse.getWarehouse());
                    warehousePane_field_desc.setText(auxWarehouse.getDescription());
                    warehousePane_field_WhType.setSelectedItem(auxWarehouse.getWarehouseType());
                    warehousePane_field_project.setSelectedItem(auxWarehouse.getProject());
                }
            }

        });
    }

    private void initWorkplaceTableDoubleClick() {
        this.workplace_jtable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_WORKPLACE_BY_ID);
                    query.setParameter("id", Integer.valueOf(workplace_jtable.getValueAt(workplace_jtable.getSelectedRow(), 0).toString()));
                    Helper.sess.getTransaction().commit();
                    auxWorkplace = (ConfigWorkplace) query.list().get(0);

                    workplacePane_field_id.setText(auxWorkplace.getId() + "");
                    workplacePane_field_workplace.setText(auxWorkplace.getWorkplace());
                    workplacePane_field_project.setSelectedItem(auxWorkplace.getProject());

                    ConfigSegment.setSegmentByProject(this, workplacePane_field_segment, auxWorkplace.getProject(), false);
                    workplacePane_field_segment.setSelectedItem(auxWorkplace.getSegment());
                }
            }
        });
    }

    private void initCompanyTableDoubleClick() {
        this.company_jtable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_COMPANY_BY_ID);
                    query.setParameter("id", Integer.valueOf(company_jtable.getValueAt(company_jtable.getSelectedRow(), 0).toString()));
                    Helper.sess.getTransaction().commit();
                    auxCompany = (ConfigCompany) query.list().get(0);

                    companyPane_field_id.setText(auxCompany.getId() + "");
                    companyPane_field_name.setText(auxCompany.getName());
                    companyPane_field_address1.setText(auxCompany.getAddress1());
                    companyPane_field_address2.setText(auxCompany.getAddress2());
                    companyPane_field_city.setText(auxCompany.getCity());
                    companyPane_field_country.setText(auxCompany.getCountry());
                    companyPane_field_description.setText(auxCompany.getDesc());
                    companyPane_field_website.setText(auxCompany.getWebsite());
                    companyPane_field_zip.setText(auxCompany.getZip());
                }
            }
        });
    }

    private void initProjectTableDoubleClick() {
        this.project_jtable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_PROJECT_BY_ID);
                    query.setParameter("id", Integer.valueOf(project_jtable.getValueAt(project_jtable.getSelectedRow(), 0).toString()));
                    Helper.sess.getTransaction().commit();
                    auxProject = (ConfigProject) query.list().get(0);

                    projectPane_field_id.setText(auxProject.getId() + "");
                    projectPane_field_project.setText(auxProject.getProject());
                    projectPane_field_desc.setText(auxProject.getDescription());
                }
            }
        });
    }

    private void initFamilyTableDoubleClick() {
        this.family_jtable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_FAMILY_BY_ID);
                    query.setParameter("id", Integer.valueOf(family_jtable.getValueAt(family_jtable.getSelectedRow(), 0).toString()));
                    Helper.sess.getTransaction().commit();
                    auxFamily = (ConfigFamily) query.list().get(0);

                    familyPane_field_id.setText(auxFamily.getId() + "");
                    familyPane_field_name.setText(auxFamily.getFamily());
                    familyPane_field_project.setSelectedItem(auxFamily.getProject());
                }
            }
        });
    }

    private void initSegmentTableDoubleClick() {
        this.segment_jtable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_SEGMENT_BY_ID);
                    query.setParameter("id", Integer.valueOf(segment_jtable.getValueAt(segment_jtable.getSelectedRow(), 0).toString()));
                    Helper.sess.getTransaction().commit();
                    auxSegment = (ConfigSegment) query.list().get(0);

                    segmentPane_field_id.setText(auxSegment.getId() + "");
                    segmentPane_field_segment.setText(auxSegment.getSegment());
                    segmentPane_field_project.setSelectedItem(auxSegment.getProject());
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel actions_pane;
    private javax.swing.JPanel actions_pane_2;
    private javax.swing.JPanel actions_pane_3;
    private javax.swing.JPanel actions_pane_4;
    private javax.swing.JPanel actions_pane_5;
    private javax.swing.JPanel actions_pane_6;
    private javax.swing.JPanel actions_pane_7;
    private javax.swing.JPanel actions_pane_8;
    private javax.swing.JButton companyPane_btn_clear;
    private javax.swing.JButton companyPane_btn_delete;
    private javax.swing.JButton companyPane_btn_duplicate;
    private javax.swing.JButton companyPane_btn_refresh;
    private javax.swing.JButton companyPane_btn_save;
    private javax.swing.JTextField companyPane_field_address1;
    private javax.swing.JTextField companyPane_field_address2;
    private javax.swing.JTextField companyPane_field_city;
    private javax.swing.JTextField companyPane_field_country;
    private javax.swing.JTextField companyPane_field_description;
    private javax.swing.JTextField companyPane_field_id;
    private javax.swing.JTextField companyPane_field_name;
    private javax.swing.JTextField companyPane_field_website;
    private javax.swing.JTextField companyPane_field_zip;
    private javax.swing.JPanel company_fields;
    private javax.swing.JTable company_jtable;
    private javax.swing.JPanel company_pane;
    private javax.swing.JScrollPane company_table;
    private javax.swing.JButton dispatchControlPane_btn_clear;
    private javax.swing.JButton dispatchControlPane_btn_delete;
    private javax.swing.JButton dispatchControlPane_btn_duplicate;
    private javax.swing.JButton dispatchControlPane_btn_refresh;
    private javax.swing.JButton dispatchControlPane_btn_save;
    private javax.swing.JTable dispatchControl_jtable;
    private javax.swing.JTextField dispatchScanPane_field_id;
    private javax.swing.JTextField dispatchScanPane_field_odette_pn_begin;
    private javax.swing.JTextField dispatchScanPane_field_odette_pn_end;
    private javax.swing.JComboBox<String> dispatchScanPane_field_project;
    private javax.swing.JPanel dispatch_scan_panel;
    private javax.swing.JScrollPane famille_table;
    private javax.swing.JButton familyPane_btn_clear;
    private javax.swing.JButton familyPane_btn_delete;
    private javax.swing.JButton familyPane_btn_duplicate;
    private javax.swing.JButton familyPane_btn_refresh;
    private javax.swing.JButton familyPane_btn_save;
    private javax.swing.JTextField familyPane_field_id;
    private javax.swing.JTextField familyPane_field_name;
    private javax.swing.JComboBox<String> familyPane_field_project;
    private javax.swing.JPanel family_fields;
    private javax.swing.JTable family_jtable;
    private javax.swing.JPanel family_pane;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel magasin_pane;
    private javax.swing.JScrollPane magasin_table;
    private javax.swing.JTabbedPane main_tab;
    private javax.swing.JTextField msg_lbl;
    private javax.swing.JButton projectPane_btn_clear;
    private javax.swing.JButton projectPane_btn_delete;
    private javax.swing.JButton projectPane_btn_duplicate;
    private javax.swing.JButton projectPane_btn_refresh;
    private javax.swing.JButton projectPane_btn_save;
    private javax.swing.JTextArea projectPane_field_desc;
    private javax.swing.JTextField projectPane_field_id;
    private javax.swing.JTextField projectPane_field_project;
    private javax.swing.JPanel project_fields;
    private javax.swing.JPanel project_fields1;
    private javax.swing.JTable project_jtable;
    private javax.swing.JPanel project_pane;
    private javax.swing.JScrollPane project_table;
    private javax.swing.JScrollPane project_table1;
    private javax.swing.JButton segmentPane_btn_clear;
    private javax.swing.JButton segmentPane_btn_delete;
    private javax.swing.JButton segmentPane_btn_duplicate;
    private javax.swing.JButton segmentPane_btn_refresh;
    private javax.swing.JButton segmentPane_btn_save;
    private javax.swing.JTextField segmentPane_field_id;
    private javax.swing.JComboBox<String> segmentPane_field_project;
    private javax.swing.JTextField segmentPane_field_segment;
    private javax.swing.JPanel segment_fields;
    private javax.swing.JTable segment_jtable;
    private javax.swing.JPanel segment_pane;
    private javax.swing.JScrollPane segment_table;
    private javax.swing.JButton transporterPane_btn_clear;
    private javax.swing.JButton transporterPane_btn_delete;
    private javax.swing.JButton transporterPane_btn_duplicate;
    private javax.swing.JButton transporterPane_btn_refresh;
    private javax.swing.JButton transporterPane_btn_save;
    private javax.swing.JTextField transporterPane_field_id;
    private javax.swing.JTextField transporterPane_field_name;
    private javax.swing.JLabel transporterPane_label_id;
    private javax.swing.JPanel transporter_fields;
    private javax.swing.JTable transporter_jtable;
    private javax.swing.JPanel transporter_pane;
    private javax.swing.JScrollPane transporter_table;
    private javax.swing.JButton warehousePane_btn_clear;
    private javax.swing.JButton warehousePane_btn_delete;
    private javax.swing.JButton warehousePane_btn_duplicate;
    private javax.swing.JButton warehousePane_btn_refresh;
    private javax.swing.JButton warehousePane_btn_save;
    private javax.swing.JComboBox<String> warehousePane_field_WhType;
    private javax.swing.JTextArea warehousePane_field_desc;
    private javax.swing.JTextField warehousePane_field_id;
    private javax.swing.JTextField warehousePane_field_name;
    private javax.swing.JComboBox<String> warehousePane_field_project;
    private javax.swing.JPanel warehouse_fields;
    private javax.swing.JTable warehouse_jtable;
    private javax.swing.JButton workplacePane_btn_clear;
    private javax.swing.JButton workplacePane_btn_delete;
    private javax.swing.JButton workplacePane_btn_duplicate;
    private javax.swing.JButton workplacePane_btn_refresh;
    private javax.swing.JButton workplacePane_btn_save;
    private javax.swing.JTextField workplacePane_field_id;
    private javax.swing.JComboBox<String> workplacePane_field_project;
    private javax.swing.JComboBox<String> workplacePane_field_segment;
    private javax.swing.JTextField workplacePane_field_workplace;
    private javax.swing.JPanel workplace_fields;
    private javax.swing.JTable workplace_jtable;
    private javax.swing.JPanel workplace_pane;
    private javax.swing.JScrollPane workplace_table;
    // End of variables declaration//GEN-END:variables

}
