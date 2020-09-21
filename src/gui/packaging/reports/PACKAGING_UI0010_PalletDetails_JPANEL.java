/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging.reports;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import com.itextpdf.text.DocumentException;
import helper.Helper;
import helper.HQLHelper;
import entity.BaseContainer;
import entity.BaseHarness;
import entity.HisOpenPalPrint;
import entity.LoadPlan;
import entity.LoadPlanLine;
import __main__.MainFrame;
import gui.packaging.PackagingVars;
//import gui.packaging.mode1.state.Mode1_S050_ClosingPallet;
import gui.packaging.mode.state.Mode3_S040_ClosingPallet;
import helper.PrinterHelper;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
//import gui.packaging.mode2.state.Mode2_S040_ClosingPallet;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import ui.UILog;

/**
 *
 * @author user
 */
public final class PACKAGING_UI0010_PalletDetails_JPANEL extends javax.swing.JPanel {

    JTabbedPane parent;
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector<String> searchResult_table_header = new Vector<>();
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector searchResult_table_data = new Vector();
    private BaseContainer bc = null;
    private boolean dropAccess = false;
    private boolean printOpenSheet = false;
    private boolean printCloseSheet = false;
    private String palletNumber = "";
    private String dispatchLabelNo = "";

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     */
    public PACKAGING_UI0010_PalletDetails_JPANEL(JTabbedPane parent) {

        initComponents();
        initGui(parent);
        history_btn.setEnabled(false);
        open_sheet_button.setEnabled(false);
        close_sheet_button.setEnabled(false);
        dropButton.setEnabled(false);
    }

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     * @param palletNumber : Requested container number to be displayed
     * @param dispatchLabel
     * @param searchMode
     */
    public PACKAGING_UI0010_PalletDetails_JPANEL(JTabbedPane parent, String palletNumber, String dispatchLabel, int searchMode) {
        this.palletNumber = palletNumber;
        this.dispatchLabelNo = dispatchLabel;
        initComponents();
        this.searchForPallet(palletNumber, dispatchLabel, searchMode);
        this.palletNum_txtbox.setText(palletNumber);
        initGui(parent);

    }

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     * @param drop : Show drop button in the form
     * @param printOpenSheet
     * @param printCloseSheet
     * @param emptyAccess
     */
    public PACKAGING_UI0010_PalletDetails_JPANEL(JTabbedPane parent, boolean drop, boolean printOpenSheet,
            boolean printCloseSheet, boolean emptyAccess, boolean canChangeStatus) {
        //super(parent, modal);
        initComponents();

        if (drop == false || this.bc == null) {
            dropButton.setEnabled(false);
        }
        if (printOpenSheet == false || this.bc == null) {
            open_sheet_button.setEnabled(false);
        }
        if (printCloseSheet == false || this.bc == null) {
            close_sheet_button.setEnabled(false);
        }
        if (canChangeStatus == false || this.bc == null) {
            set_state_btn.setEnabled(false);
        }

        initGui(parent);
    }

    /**
     *
     * @param parent
     * @param modal
     * @param palletNumber
     * @param dispatchLabelNo
     * @param searchMode
     * @param drop
     * @param printOpenSheet
     * @param printCloseSheet
     */
    public PACKAGING_UI0010_PalletDetails_JPANEL(JTabbedPane parent,
            String palletNumber, String dispatchLabelNo, int searchMode,
            boolean drop, boolean printOpenSheet, boolean printCloseSheet) {
        this.palletNumber = palletNumber;
        this.dispatchLabelNo = dispatchLabelNo;
        initComponents();

        this.setDropAccess(drop);
        this.setPrintOpenSheet(printOpenSheet);
        this.setPrintCloseSheet(printCloseSheet);

        this.searchForPallet(palletNumber, dispatchLabelNo, searchMode);
        initGui(parent);
        this.palletNum_txtbox.setText(palletNumber);

        System.out.println("state_txtbox.getText() " + state_txtbox.getText());

        //Activer ou désactiver le bouton print closing palette si Open
        if (state_txtbox.getText().equals(GlobalVars.PALLET_OPEN) && PackagingVars.context.getUser() != null) {
            close_sheet_button.setEnabled(false);
        } else {
            close_sheet_button.setEnabled(true);
        }

    }

    private void initGui(JTabbedPane parent) {
        //Center the this dialog in the screen
        this.parent = parent;

        //Desable table edition
        disableEditingTable();

        //Load table header
        load_container_table_header();

        //this.setTitle(this.getTitle() + " " + this.palletNumber);
        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Détails " + this.palletNumber));

        if (GlobalVars.CONNECTED_USER.getAccessLevel() == GlobalVars.PROFIL_OPERATOR) {
            set_state_btn.setVisible(false);
            dropButton.setVisible(false);
        }
        try {

            if (this.bc.getContainerState().equals(GlobalVars.PALLET_DISPATCHED)) {
                set_state_btn.setEnabled(false);
            } else {
                set_state_btn.setEnabled(true);
            }
        } catch (Exception e) {
            set_state_btn.setEnabled(false);
        }
    }

    public void disableEditingTable() {
        for (int c = 0; c < searchResult_table.getColumnCount(); c++) {
            Class<?> col_class = searchResult_table.getColumnClass(c);
            searchResult_table.setDefaultEditor(col_class, null);        // remove editor            
        }
    }

    public BaseContainer getBaseContainer() {
        return bc;
    }

    public void setBaseContainer(BaseContainer bc) {
        this.bc = bc;
    }

    public boolean isDropAccess() {
        return dropAccess;
    }

    /**
     *
     * @param dropAccess
     */
    public void setDropAccess(boolean dropAccess) {
        // if the pallet is not dispatched, then it can be deleted, otherwise 
        // no delete is possible.
        System.out.println("setDropAccess " + state_txtbox.getText());
        System.out.println("PALLET_DISPATCHED " + GlobalVars.PALLET_DISPATCHED);
        if (!state_txtbox.getText().equals(GlobalVars.PALLET_DISPATCHED)) {
            this.dropAccess = dropAccess;
            dropButton.setEnabled(dropAccess);
        } else {
            dropButton.setEnabled(false);
        }
    }

    public boolean isPrintOpenSheet() {
        return printOpenSheet;
    }

    public void setPrintOpenSheet(boolean printOpenSheet) {
        this.printOpenSheet = printOpenSheet;
        open_sheet_button.setEnabled(printOpenSheet);
    }

    public boolean isPrintCloseSheet() {
        return printCloseSheet;
    }

    public void setPrintCloseSheet(boolean printCloseSheet) {
        this.printCloseSheet = printCloseSheet;
        close_sheet_button.setEnabled(printCloseSheet);
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
        palletNum_txtbox = new javax.swing.JTextField();
        search_btn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        msg_lbl = new javax.swing.JLabel();
        palletNumber_txtbox = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        clear_btn = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        generales = new javax.swing.JPanel();
        jSeparator8 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        palletId_txtbox = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        harnessPart_txtbox = new javax.swing.JTextField();
        index_txtbox = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        supplierPartNumber_txtbox = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        project_txtbox = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        state_txtbox = new javax.swing.JTextField();
        special_order_txtbox = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        packType_txtbox = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        qtyRead_txtbox = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        qtyExptected_txtbox = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        startTime_txtbox = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        completeTime_txtbox = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        workingTime_txtbox = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        comment_txt = new javax.swing.JTextArea();
        login_lbl3 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        create_user_txtbox = new javax.swing.JTextField();
        user_txtbox = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        workstation_txtbox = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        stdTime_txtbox = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        total_stdTime_txtbox = new javax.swing.JTextField();
        dispatch = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        planId_txtbox = new javax.swing.JTextField();
        planCreateTime_txtbox = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        planDispatchTime_txtbox = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        dispatchLabelNo_txtbox = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        planDestination_txtbox = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        planCreateUser_txtbox = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        planStatus_txtbox = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        pile_txtbox = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        position_txtbox = new javax.swing.JTextField();
        lineCreateTime_txtbox = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        lineCreateUser_txtbox = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        truckNo_txtbox = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        destination_txtbox = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        donnes_technique = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        net_weight_txt = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        gross_weight_txt = new javax.swing.JTextField();
        volume_txt = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        eng_change_date_txt = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        eng_change_txt = new javax.swing.JTextArea();
        jLabel37 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        article_desc_txt = new javax.swing.JTextArea();
        jLabel38 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel43 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        pieces_liste = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        table_scroll = new javax.swing.JScrollPane();
        searchResult_table = new javax.swing.JTable();
        dispatch_label_no_txtbox = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        history_btn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        open_sheet_button = new javax.swing.JButton();
        close_sheet_button = new javax.swing.JButton();
        dropButton = new javax.swing.JButton();
        set_state_btn = new javax.swing.JButton();

        setBackground(new java.awt.Color(36, 65, 86));
        setForeground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(36, 65, 86));

        palletNum_txtbox.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        palletNum_txtbox.setForeground(new java.awt.Color(0, 0, 153));
        palletNum_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                palletNum_txtboxActionPerformed(evt);
            }
        });
        palletNum_txtbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                palletNum_txtboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                palletNum_txtboxKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                palletNum_txtboxKeyTyped(evt);
            }
        });

        search_btn.setBackground(new java.awt.Color(0, 102, 102));
        search_btn.setForeground(new java.awt.Color(255, 255, 255));
        search_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/edit-find.png"))); // NOI18N
        search_btn.setText("Recherche");
        search_btn.setToolTipText("Search");
        search_btn.setBorderPainted(false);
        search_btn.setMaximumSize(new java.awt.Dimension(24, 24));
        search_btn.setMinimumSize(new java.awt.Dimension(24, 24));
        search_btn.setOpaque(false);
        search_btn.setPreferredSize(new java.awt.Dimension(24, 24));
        search_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_btnActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("N° Palette");

        msg_lbl.setForeground(new java.awt.Color(255, 255, 51));

        palletNumber_txtbox.setEditable(false);
        palletNumber_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        palletNumber_txtbox.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        palletNumber_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                palletNumber_txtboxActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Détails palette");

        clear_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/edit-clear.png"))); // NOI18N
        clear_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_btnActionPerformed(evt);
            }
        });

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        generales.setBackground(new java.awt.Color(36, 65, 86));

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jPanel4.setBackground(new java.awt.Color(36, 65, 86));

        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setText("Informations générales");

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("ID");

        palletId_txtbox.setEditable(false);
        palletId_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        palletId_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                palletId_txtboxActionPerformed(evt);
            }
        });

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Article");

        harnessPart_txtbox.setEditable(false);
        harnessPart_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        harnessPart_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                harnessPart_txtboxActionPerformed(evt);
            }
        });

        index_txtbox.setEditable(false);
        index_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        index_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                index_txtboxActionPerformed(evt);
            }
        });

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Indice");

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("LPN");

        supplierPartNumber_txtbox.setEditable(false);
        supplierPartNumber_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        supplierPartNumber_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supplierPartNumber_txtboxActionPerformed(evt);
            }
        });

        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setText("Projet");

        project_txtbox.setEditable(false);
        project_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        project_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                project_txtboxActionPerformed(evt);
            }
        });

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Statut");

        state_txtbox.setEditable(false);
        state_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        state_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                state_txtboxActionPerformed(evt);
            }
        });

        special_order_txtbox.setEditable(false);
        special_order_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        special_order_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                special_order_txtboxActionPerformed(evt);
            }
        });

        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("Spéciale");

        packType_txtbox.setEditable(false);
        packType_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        packType_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packType_txtboxActionPerformed(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Type Pack");

        qtyRead_txtbox.setEditable(false);
        qtyRead_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        qtyRead_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qtyRead_txtboxActionPerformed(evt);
            }
        });

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Quantité scannée");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Quantité standard");

        qtyExptected_txtbox.setEditable(false);
        qtyExptected_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        qtyExptected_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qtyExptected_txtboxActionPerformed(evt);
            }
        });

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Ouverte le...");

        startTime_txtbox.setEditable(false);
        startTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        startTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startTime_txtboxActionPerformed(evt);
            }
        });

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Fermée le...");

        completeTime_txtbox.setEditable(false);
        completeTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        completeTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completeTime_txtboxActionPerformed(evt);
            }
        });

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Temps travail (min)");

        workingTime_txtbox.setEditable(false);
        workingTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        workingTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workingTime_txtboxActionPerformed(evt);
            }
        });

        comment_txt.setEditable(false);
        comment_txt.setColumns(10);
        comment_txt.setRows(3);
        comment_txt.setToolTipText("");
        jScrollPane1.setViewportView(comment_txt);

        login_lbl3.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl3.setText("Commentaire");

        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Utilisateur");

        create_user_txtbox.setEditable(false);
        create_user_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        create_user_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                create_user_txtboxActionPerformed(evt);
            }
        });

        user_txtbox.setEditable(false);
        user_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        user_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_txtboxActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Créé par");

        workstation_txtbox.setEditable(false);
        workstation_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        workstation_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workstation_txtboxActionPerformed(evt);
            }
        });

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Workstation");

        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Standard Time");

        stdTime_txtbox.setEditable(false);
        stdTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        stdTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stdTime_txtboxActionPerformed(evt);
            }
        });

        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("Total Standard Time");

        total_stdTime_txtbox.setEditable(false);
        total_stdTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        total_stdTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                total_stdTime_txtboxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel41)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(palletId_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(harnessPart_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(index_txtbox)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(qtyExptected_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(qtyRead_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(packType_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel30)
                                        .addGap(18, 18, 18)
                                        .addComponent(stdTime_txtbox))
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(startTime_txtbox, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(workstation_txtbox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(completeTime_txtbox)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel2)
                                        .addComponent(user_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(create_user_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel27)
                                    .addComponent(workingTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(total_stdTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel10)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(supplierPartNumber_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(project_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel39)))
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(login_lbl3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(special_order_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel29))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel14)
                                        .addComponent(state_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap(160, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel41)
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(supplierPartNumber_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(project_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(jLabel10)
                                .addComponent(jLabel39)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(index_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(harnessPart_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(palletId_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(qtyRead_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(qtyExptected_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addComponent(jLabel6)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(startTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addComponent(jLabel7)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(completeTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(workingTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel16))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(user_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(workstation_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(create_user_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(login_lbl3)
                                .addGap(1, 1, 1)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel29)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(packType_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(special_order_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(state_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(stdTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(total_stdTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel31)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout generalesLayout = new javax.swing.GroupLayout(generales);
        generales.setLayout(generalesLayout);
        generalesLayout.setHorizontalGroup(
            generalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalesLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
        );
        generalesLayout.setVerticalGroup(
            generalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, generalesLayout.createSequentialGroup()
                .addGroup(generalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(generalesLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 167, Short.MAX_VALUE))
                    .addComponent(jSeparator8))
                .addGap(47, 47, 47))
        );

        jTabbedPane1.addTab("Générales", generales);

        jPanel5.setBackground(new java.awt.Color(36, 65, 86));

        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("ID plan de chargement");

        planId_txtbox.setEditable(false);
        planId_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planId_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planId_txtboxActionPerformed(evt);
            }
        });

        planCreateTime_txtbox.setEditable(false);
        planCreateTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planCreateTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planCreateTime_txtboxActionPerformed(evt);
            }
        });

        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Date création du plan");

        planDispatchTime_txtbox.setEditable(false);
        planDispatchTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planDispatchTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planDispatchTime_txtboxActionPerformed(evt);
            }
        });

        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText(" Date Dispatch");

        dispatchLabelNo_txtbox.setEditable(false);
        dispatchLabelNo_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        dispatchLabelNo_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispatchLabelNo_txtboxActionPerformed(evt);
            }
        });

        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setText("Etiquette Dispatch");

        planDestination_txtbox.setEditable(false);
        planDestination_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planDestination_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planDestination_txtboxActionPerformed(evt);
            }
        });

        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Destination");

        planCreateUser_txtbox.setEditable(false);
        planCreateUser_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planCreateUser_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planCreateUser_txtboxActionPerformed(evt);
            }
        });

        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Utilisateur");

        planStatus_txtbox.setEditable(false);
        planStatus_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planStatus_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planStatus_txtboxActionPerformed(evt);
            }
        });

        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Statut");

        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Position");

        pile_txtbox.setEditable(false);
        pile_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        pile_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pile_txtboxActionPerformed(evt);
            }
        });

        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("ID Ligne");

        position_txtbox.setEditable(false);
        position_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        position_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                position_txtboxActionPerformed(evt);
            }
        });

        lineCreateTime_txtbox.setEditable(false);
        lineCreateTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        lineCreateTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineCreateTime_txtboxActionPerformed(evt);
            }
        });

        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Date création de ligne");

        lineCreateUser_txtbox.setEditable(false);
        lineCreateUser_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        lineCreateUser_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineCreateUser_txtboxActionPerformed(evt);
            }
        });

        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Utilisateur");

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 255, 255));
        jLabel42.setText("Détails chargement");

        truckNo_txtbox.setEditable(false);
        truckNo_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        truckNo_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                truckNo_txtboxActionPerformed(evt);
            }
        });

        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setText("Truck No.");

        destination_txtbox.setEditable(false);
        destination_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        destination_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                destination_txtboxActionPerformed(evt);
            }
        });

        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setText("Destination");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(planStatus_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(planCreateUser_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(planDestination_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel44)
                                    .addComponent(truckNo_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jLabel42)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(planId_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(planCreateTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(planDispatchTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32)
                            .addComponent(dispatchLabelNo_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel40)
                            .addComponent(destination_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(pile_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28)
                            .addComponent(position_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(lineCreateTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lineCreateUser_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(98, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42)
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(planId_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(planCreateTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel32))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(planDispatchTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dispatchLabelNo_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel40)
                                .addGap(30, 30, 30))
                            .addComponent(destination_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel23))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(planCreateUser_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(planStatus_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(planDestination_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(truckNo_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26))
                        .addGap(2, 2, 2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addComponent(jLabel19))
                        .addComponent(jLabel28)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(pile_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(position_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lineCreateTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lineCreateUser_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dispatchLayout = new javax.swing.GroupLayout(dispatch);
        dispatch.setLayout(dispatchLayout);
        dispatchLayout.setHorizontalGroup(
            dispatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dispatchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dispatchLayout.setVerticalGroup(
            dispatchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dispatchLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(279, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Détails chargement", dispatch);

        donnes_technique.setBackground(new java.awt.Color(36, 65, 86));

        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("Poids net");

        net_weight_txt.setEditable(false);
        net_weight_txt.setBackground(new java.awt.Color(255, 255, 255));
        net_weight_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                net_weight_txtActionPerformed(evt);
            }
        });

        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("Poids brut");

        gross_weight_txt.setEditable(false);
        gross_weight_txt.setBackground(new java.awt.Color(255, 255, 255));
        gross_weight_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gross_weight_txtActionPerformed(evt);
            }
        });

        volume_txt.setEditable(false);
        volume_txt.setBackground(new java.awt.Color(255, 255, 255));
        volume_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volume_txtActionPerformed(evt);
            }
        });

        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("Volume");

        eng_change_date_txt.setEditable(false);
        eng_change_date_txt.setBackground(new java.awt.Color(255, 255, 255));
        eng_change_date_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eng_change_date_txtActionPerformed(evt);
            }
        });

        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setText("Date changement Engineering");

        eng_change_txt.setEditable(false);
        eng_change_txt.setColumns(20);
        eng_change_txt.setRows(5);
        jScrollPane2.setViewportView(eng_change_txt);

        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("Commentaire changement téchnique");

        article_desc_txt.setEditable(false);
        article_desc_txt.setColumns(20);
        article_desc_txt.setRows(5);
        jScrollPane3.setViewportView(article_desc_txt);

        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setText("Description de l'article");

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 255, 255));
        jLabel43.setText("Données techniques");

        javax.swing.GroupLayout donnes_techniqueLayout = new javax.swing.GroupLayout(donnes_technique);
        donnes_technique.setLayout(donnes_techniqueLayout);
        donnes_techniqueLayout.setHorizontalGroup(
            donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(donnes_techniqueLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(donnes_techniqueLayout.createSequentialGroup()
                        .addGroup(donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(net_weight_txt, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(eng_change_date_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36)))
                    .addGroup(donnes_techniqueLayout.createSequentialGroup()
                        .addGroup(donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34)
                            .addComponent(gross_weight_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(volume_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35))
                        .addGap(18, 18, 18)
                        .addGroup(donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel43)
                    .addComponent(jSeparator1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(240, Short.MAX_VALUE))
        );
        donnes_techniqueLayout.setVerticalGroup(
            donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(donnes_techniqueLayout.createSequentialGroup()
                .addGroup(donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(donnes_techniqueLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel43)
                        .addGap(18, 18, 18)
                        .addGroup(donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(donnes_techniqueLayout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(net_weight_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(donnes_techniqueLayout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(eng_change_date_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(jLabel37)
                            .addComponent(jLabel38))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(donnes_techniqueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, donnes_techniqueLayout.createSequentialGroup()
                                .addComponent(gross_weight_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(volume_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE))
                    .addComponent(jSeparator7))
                .addContainerGap(267, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Avancés", donnes_technique);

        pieces_liste.setBackground(new java.awt.Color(36, 65, 86));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Liste des pièces");

        jButton1.setText("Charger");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        searchResult_table.setAutoCreateRowSorter(true);
        searchResult_table.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        searchResult_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Create Time", "Create User", "Harness Part ", "Counter", "Pallet Number"
            }
        ));
        table_scroll.setViewportView(searchResult_table);

        javax.swing.GroupLayout pieces_listeLayout = new javax.swing.GroupLayout(pieces_liste);
        pieces_liste.setLayout(pieces_listeLayout);
        pieces_listeLayout.setHorizontalGroup(
            pieces_listeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pieces_listeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pieces_listeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pieces_listeLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1))
                    .addComponent(table_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 894, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(146, Short.MAX_VALUE))
        );
        pieces_listeLayout.setVerticalGroup(
            pieces_listeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pieces_listeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pieces_listeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(table_scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Liste des pièces", pieces_liste);

        dispatch_label_no_txtbox.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        dispatch_label_no_txtbox.setForeground(new java.awt.Color(0, 0, 153));
        dispatch_label_no_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispatch_label_no_txtboxActionPerformed(evt);
            }
        });
        dispatch_label_no_txtbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dispatch_label_no_txtboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dispatch_label_no_txtboxKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dispatch_label_no_txtboxKeyTyped(evt);
            }
        });

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("N° Etiquette Dispatch");

        history_btn.setBackground(new java.awt.Color(0, 102, 102));
        history_btn.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        history_btn.setForeground(new java.awt.Color(255, 255, 255));
        history_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/time-icon.png"))); // NOI18N
        history_btn.setText("Historique");
        history_btn.setToolTipText("Search");
        history_btn.setBorderPainted(false);
        history_btn.setMaximumSize(new java.awt.Dimension(24, 24));
        history_btn.setMinimumSize(new java.awt.Dimension(24, 24));
        history_btn.setOpaque(false);
        history_btn.setPreferredSize(new java.awt.Dimension(24, 24));
        history_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                history_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(palletNum_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addGap(9, 9, 9)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(dispatch_label_no_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(clear_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)
                                        .addComponent(search_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(history_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel5)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(palletNumber_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jTabbedPane1)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel15)
                        .addComponent(palletNumber_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(clear_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(history_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dispatch_label_no_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(palletNum_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE))
        );

        open_sheet_button.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        open_sheet_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/document-print.png"))); // NOI18N
        open_sheet_button.setText("Fiche d'ouverture palette");
        open_sheet_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open_sheet_buttonActionPerformed(evt);
            }
        });

        close_sheet_button.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        close_sheet_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/document-print.png"))); // NOI18N
        close_sheet_button.setText("Fiche fermeture palette");
        close_sheet_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_sheet_buttonActionPerformed(evt);
            }
        });

        dropButton.setBackground(new java.awt.Color(255, 153, 153));
        dropButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        dropButton.setForeground(new java.awt.Color(0, 0, 0));
        dropButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/edit-delete.png"))); // NOI18N
        dropButton.setText("Supprimer...");
        dropButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropButtonActionPerformed(evt);
            }
        });

        set_state_btn.setBackground(new java.awt.Color(255, 204, 0));
        set_state_btn.setForeground(new java.awt.Color(0, 0, 0));
        set_state_btn.setText("Changer l'état...");
        set_state_btn.setToolTipText("Search");
        set_state_btn.setBorderPainted(false);
        set_state_btn.setMaximumSize(new java.awt.Dimension(24, 24));
        set_state_btn.setMinimumSize(new java.awt.Dimension(24, 24));
        set_state_btn.setOpaque(false);
        set_state_btn.setPreferredSize(new java.awt.Dimension(24, 24));
        set_state_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                set_state_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(open_sheet_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(close_sheet_button, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(set_state_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dropButton, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(open_sheet_button)
                    .addComponent(close_sheet_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dropButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(set_state_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void clearSearchBox() {
        //Vider le champs de text scan
        palletNum_txtbox.setText("");
        palletNum_txtbox.requestFocusInWindow();
    }

    public void load_container_table_header() {
        this.reset_container_table_content();
        searchResult_table_header.add("ID");
        searchResult_table_header.add("Create Time");
        searchResult_table_header.add("Create User");
        searchResult_table_header.add("Harness Part");
        searchResult_table_header.add("Counter");
        searchResult_table_header.add("Pallet Number");

        searchResult_table.setModel(new DefaultTableModel(searchResult_table_data, searchResult_table_header));
    }

    @SuppressWarnings("UseOfObsoleteCollectionType")
    public void reset_container_table_content() {
        searchResult_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(searchResult_table_data, searchResult_table_header);
        searchResult_table.setModel(dataModel);
    }

    public void reload_container_table_data(List resultList) {
        this.reset_container_table_content();

        Set<BaseHarness> harnessList = new HashSet<>(0);
        for (Object o : resultList) {
            BaseHarness base_harness = (BaseHarness) o;
            harnessList.add(base_harness);
            @SuppressWarnings("UseOfObsoleteCollectionType")
            Vector<Object> oneRow = new Vector<>();

            oneRow.add(base_harness.getId());
            oneRow.add(base_harness.getCreateTimeString("dd/MM/yy HH:mm"));
            oneRow.add(base_harness.getUser() + " / " + base_harness.getCreateUser());
            oneRow.add(base_harness.getHarnessPart());
            oneRow.add(base_harness.getCounter());
            oneRow.add(base_harness.getPalletNumber());

            searchResult_table_data.add(oneRow);
        }

        //Charger la liste des fx (Lazy mode) !
        this.bc.setHarnessList(harnessList);

        searchResult_table.setModel(new DefaultTableModel(searchResult_table_data, searchResult_table_header));
        searchResult_table.setAutoCreateRowSorter(true);
    }

    private void reload_load_plan_data(List result) {
        this.clearLoadPlanFields();

        System.out.println("reload_load_plan_data result" + result.size());

        LoadPlanLine l = (LoadPlanLine) result.get(0);
        pile_txtbox.setText("" + l.getPileNum());
        position_txtbox.setText("" + l.getId());
        lineCreateUser_txtbox.setText(l.getUser());
        lineCreateTime_txtbox.setText("" + l.getCreateTime());
        dispatchLabelNo_txtbox.setText(l.getDispatchLabelNo());
        truckNo_txtbox.setText(l.getTruckNo());
        //Get Laod Plan Data
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_BY_ID);
        query.setParameter("id", l.getLoadPlanId());
        Helper.sess.getTransaction().commit();
        List planList = query.list();
        LoadPlan plan = (LoadPlan) planList.get(0);
        planId_txtbox.setText("" + plan.getId());
        planCreateTime_txtbox.setText("" + plan.getCreateTime());
        planCreateUser_txtbox.setText(plan.getUser());
        planDispatchTime_txtbox.setText("" + plan.getDeliveryTime());
        planDestination_txtbox.setText("" + l.getDestinationWh());
        planStatus_txtbox.setText(plan.getPlanState());

    }

    private void clearLoadPlanFields() {
        planId_txtbox.setText("");
        pile_txtbox.setText("");
        planCreateTime_txtbox.setText("");
        position_txtbox.setText("");
        planDispatchTime_txtbox.setText("");
        planDestination_txtbox.setText("");
        planStatus_txtbox.setText("");
        planCreateUser_txtbox.setText("");
        lineCreateUser_txtbox.setText("");
        lineCreateTime_txtbox.setText("");
    }

    /**
     *
     * @param palletNumber
     * @param mode 1 = search by production pallet num, 2 by fors serial number,
     * 3 both
     */
    private void searchForPallet(String palletNumber, String dispatchLabelNo, int mode) {
        msg_lbl.setText("");

        this.clearContainerFieldsValues();
        this.clearLoadPlanFields();
        this.reset_container_table_content();

        if (!palletNumber.trim().equals("") && palletNumber.startsWith(GlobalVars.CLOSING_PALLET_PREFIX)) {
            palletNumber = palletNumber.substring(2);
        }

        System.out.println("palletNumber" + palletNumber);
        //################# Container Data ####################
        //Start transaction                
        Query query = null;
        Helper.startSession();
        switch (mode) {
            case 1:
                //Search by production serial
                System.out.println("search mode 1 " + palletNumber);
                query = Helper.sess.createQuery(HQLHelper.GET_CONTAINER_BY_NUMBER);
                query.setParameter("palletNumber", palletNumber.trim());
                Helper.sess.getTransaction().commit();
                break;
            case 2:
                //Search by fors serial
                System.out.println("search mode 2 " + dispatchLabelNo);
                query = Helper.sess.createQuery(HQLHelper.GET_CONTAINER_BY_FORS_SERIAL);
                query.setParameter("dispatchLabelNo", "%" + dispatchLabelNo.trim() + "%");
                Helper.sess.getTransaction().commit();
                break;
            case 3:
                System.out.println("search mode 3 " + palletNumber + " " + dispatchLabelNo);
                query = Helper.sess.createQuery(HQLHelper.GET_CONTAINER_BY_NUMBER_AND_FORS_SERIAL);
                query.setParameter("palletNumber", palletNumber.trim());
                query.setParameter("dispatchLabelNo", "%" + dispatchLabelNo.trim() + "%");
                Helper.sess.getTransaction().commit();
                break;
            default:
                break;
        }
        @SuppressWarnings("null")
        List result = query.list();
        if (result.isEmpty()) {
            msg_lbl.setText("Num. palette introuvable !");
            this.reset_container_table_content();
            //Show / Hide tools buttons
        } else {
            msg_lbl.setText("");
            System.out.println("Result found " + result.size());
            this.setBaseContainer((BaseContainer) result.get(0));
            this.setContainerFieldsValues(this.bc);

            //reload_harness_list(this.bc.getPalletNumber());
            //################# LoadPlan line data ####################
            Helper.startSession();
            query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PAL_NUM);
            query.setParameter("palletNumber", this.bc.getPalletNumber());
            Helper.sess.getTransaction().commit();
            result = query.list();
            //Reload LoadPlan line details
            if (!result.isEmpty()) {
                this.reload_load_plan_data(result);
            }

            //Activate histoy button
            history_btn.setEnabled(true);
            open_sheet_button.setEnabled(true);

            switch (this.bc.getContainerStateCode()) {
                case GlobalVars.PALLET_OPEN_CODE:
                    close_sheet_button.setEnabled(false);
                    dropButton.setEnabled(true);
                    break;
                case GlobalVars.PALLET_WAITING_CODE:
                    dropButton.setEnabled(true);
                    break;
                case GlobalVars.PALLET_CLOSED_CODE:
                    dropButton.setEnabled(true);
                    break;
                case GlobalVars.PALLET_STORED_CODE:
                    dropButton.setEnabled(true);
                    break;
                case GlobalVars.PALLET_RESERVED_CODE:
                    dropButton.setEnabled(false);
                    break;
                case GlobalVars.PALLET_BLOCKED_CODE:
                    close_sheet_button.setEnabled(false);
                    dropButton.setEnabled(true);
                    break;
                case GlobalVars.PALLET_DISPATCHED_CODE:
                    dropButton.setEnabled(false);
                    close_sheet_button.setEnabled(true);
                    break;
                default:
                    //close_sheet_button.setEnabled(true);
                    break;
            }
        }

    }

    private void setContainerFieldsValues(BaseContainer bc) {
        palletNumber_txtbox.setText(String.valueOf(bc.getPalletNumber()));
        palletId_txtbox.setText(String.valueOf(bc.getId()));
        user_txtbox.setText(bc.getUser());
        create_user_txtbox.setText(bc.getCreateUser());
        harnessPart_txtbox.setText(bc.getHarnessPart());
        index_txtbox.setText(bc.getHarnessIndex());
        supplierPartNumber_txtbox.setText(bc.getSupplierPartNumber());
        workstation_txtbox.setText(bc.getPackWorkstation());
        qtyExptected_txtbox.setText(String.valueOf(bc.getQtyExpected()));
        qtyRead_txtbox.setText(String.valueOf(bc.getQtyRead()));
        packType_txtbox.setText(bc.getPackType());
        stdTime_txtbox.setText(bc.getStdTime() + "");
        //total_stdTime_txtbox.setText("" + (bc.getStdTime() * bc.getQtyRead()));
        total_stdTime_txtbox.setText(String.valueOf(String.format("%1$,.2f", (bc.getStdTime() * bc.getQtyRead()))));
        if (bc.getSpecial_order() != null && bc.getSpecial_order() == 1) {
            special_order_txtbox.setText("SPECIAL");
        } else {
            special_order_txtbox.setText("");
        }
        state_txtbox.setText(bc.getContainerState());
        startTime_txtbox.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(bc.getStartTime()));
        if (bc.getClosedTime() != null) {
            completeTime_txtbox.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(bc.getClosedTime()));
        }
        workingTime_txtbox.setText(String.valueOf(bc.getWorkTime()));
        comment_txt.setText(bc.getComment());
        project_txtbox.setText(bc.getProject());
        destination_txtbox.setText(bc.getDestination());
        net_weight_txt.setText(bc.getNetWeight() + "");
        gross_weight_txt.setText(bc.getGrossWeight() + "");
        article_desc_txt.setText(bc.getArticleDesc());
        eng_change_date_txt.setText(bc.getEngChangeDate() + "");
        eng_change_txt.setText(bc.getEngChange());
        volume_txt.setText(bc.getVolume() + "");

    }

    public void clearContainerFieldsValues() {
        palletNumber_txtbox.setText("");
        palletId_txtbox.setText("");
        user_txtbox.setText("");
        create_user_txtbox.setText("");
        harnessPart_txtbox.setText("");
        index_txtbox.setText("");
        supplierPartNumber_txtbox.setText("");
        workstation_txtbox.setText("");
        qtyExptected_txtbox.setText("");
        qtyRead_txtbox.setText("");
        packType_txtbox.setText("");
        state_txtbox.setText("");
        startTime_txtbox.setText("");
        completeTime_txtbox.setText("");
        workingTime_txtbox.setText("");
        stdTime_txtbox.setText("");
        total_stdTime_txtbox.setText("");
        volume_txt.setText("");
        gross_weight_txt.setText("");
        net_weight_txt.setText("");
        article_desc_txt.setText("");
        eng_change_date_txt.setText("");
        eng_change_txt.setText("");
        destination_txtbox.setText("");
        project_txtbox.setText("");
    }

    public void setOkText(String newTxt) {
        msg_lbl.setText(newTxt);
    }

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dropButton.setEnabled(false);
            this.open_sheet_button.setEnabled(false);
            this.close_sheet_button.setEnabled(false);

            this.setVisible(false);
        }
    }//GEN-LAST:event_formKeyPressed

    private void pile_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pile_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pile_txtboxActionPerformed

    private void lineCreateTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineCreateTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lineCreateTime_txtboxActionPerformed

    private void lineCreateUser_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineCreateUser_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lineCreateUser_txtboxActionPerformed

    private void planCreateUser_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planCreateUser_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planCreateUser_txtboxActionPerformed

    private void planStatus_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planStatus_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planStatus_txtboxActionPerformed

    private void planDestination_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planDestination_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planDestination_txtboxActionPerformed

    private void planDispatchTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planDispatchTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planDispatchTime_txtboxActionPerformed

    private void planCreateTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planCreateTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planCreateTime_txtboxActionPerformed

    private void planId_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planId_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planId_txtboxActionPerformed

    private void create_user_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_create_user_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_create_user_txtboxActionPerformed

    private void user_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_txtboxActionPerformed

    private void workingTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workingTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_workingTime_txtboxActionPerformed

    private void completeTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completeTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_completeTime_txtboxActionPerformed

    private void startTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startTime_txtboxActionPerformed

    private void state_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_state_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_state_txtboxActionPerformed

    private void packType_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packType_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_packType_txtboxActionPerformed

    private void qtyRead_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qtyRead_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_qtyRead_txtboxActionPerformed

    private void qtyExptected_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qtyExptected_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_qtyExptected_txtboxActionPerformed

    private void workstation_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workstation_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_workstation_txtboxActionPerformed

    private void supplierPartNumber_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supplierPartNumber_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_supplierPartNumber_txtboxActionPerformed

    private void index_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_index_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_index_txtboxActionPerformed

    private void harnessPart_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_harnessPart_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_harnessPart_txtboxActionPerformed

    private void palletId_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_palletId_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_palletId_txtboxActionPerformed

    private void clear_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_btnActionPerformed

        palletNum_txtbox.setText("");
        history_btn.setEnabled(false);
        open_sheet_button.setEnabled(false);

    }//GEN-LAST:event_clear_btnActionPerformed

    private void history_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_history_btnActionPerformed

        GlobalMethods.addNewTabToParent("Historique", parent, new PACKAGING_UI0014_PalletHistory_JPANEL(parent, palletNumber_txtbox.getText()), evt);
//                "Historique", parent, 
//                new PACKAGING_UI0010_PalletDetails_JPANEL(parent, String.valueOf(searchResult_table.getValueAt(searchResult_table.getSelectedRow(), PALLET_NUMBER_COLINDEX)), "", 1, true, true, true), evt);
    }//GEN-LAST:event_history_btnActionPerformed

    private void palletNumber_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_palletNumber_txtboxActionPerformed

    }//GEN-LAST:event_palletNumber_txtboxActionPerformed

    private void search_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_btnActionPerformed
        String dispatch_no = "";
        if (!dispatch_label_no_txtbox.getText().isEmpty()) {
            dispatch_no = (dispatch_label_no_txtbox.getText().startsWith(GlobalVars.APP_PROP.getProperty("DISPATCH_SERIAL_NO_PREFIX"))) ? dispatch_label_no_txtbox.getText().substring(1) : dispatch_label_no_txtbox.getText();
        }
        if (dispatch_label_no_txtbox.getText().isEmpty() && !palletNum_txtbox.getText().isEmpty()) {
            this.searchForPallet(palletNum_txtbox.getText(), "", 1);
        } else if (palletNum_txtbox.getText().isEmpty() && !dispatch_no.isEmpty()) {
            this.searchForPallet("", dispatch_no, 2);
        } else {
            this.searchForPallet(palletNum_txtbox.getText(), dispatch_no, 3);
        }

        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Détails " + palletNum_txtbox.getText()));
    }//GEN-LAST:event_search_btnActionPerformed

    private void palletNum_txtboxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_palletNum_txtboxKeyTyped
        if (!palletNumber_txtbox.getText().isEmpty()) {
            history_btn.setEnabled(true);
            open_sheet_button.setEnabled(true);
        } else {
            history_btn.setEnabled(false);
            open_sheet_button.setEnabled(false);
            msg_lbl.setText("");
        }
    }//GEN-LAST:event_palletNum_txtboxKeyTyped

    private void palletNum_txtboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_palletNum_txtboxKeyReleased
        if (!palletNumber_txtbox.getText().isEmpty()) {
            history_btn.setEnabled(true);
        } else {
            history_btn.setEnabled(false);
            msg_lbl.setText("");
        }
    }//GEN-LAST:event_palletNum_txtboxKeyReleased

    private void palletNum_txtboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_palletNum_txtboxKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (palletNum_txtbox.getText().startsWith(GlobalVars.CLOSING_PALLET_PREFIX)) {
                    this.searchForPallet(palletNum_txtbox.getText().substring(GlobalVars.CLOSING_PALLET_PREFIX.length()), "", 1);
                } else {
                    this.searchForPallet(palletNum_txtbox.getText(), "", 1);
                }
                break;
            case KeyEvent.VK_ESCAPE:
                this.setVisible(false);
                break;
            case KeyEvent.VK_CLEAR:
                this.palletNum_txtbox.setText("");
                this.reset_container_table_content();
                break;
            default:
                if (!palletNumber_txtbox.getText().isEmpty()) {
                    history_btn.setEnabled(true);
                    msg_lbl.setText("");
                } else {
                    history_btn.setEnabled(false);
                }
                break;
        }
    }//GEN-LAST:event_palletNum_txtboxKeyPressed

    private void palletNum_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_palletNum_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_palletNum_txtboxActionPerformed

    private void position_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_position_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_position_txtboxActionPerformed

    private void special_order_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_special_order_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_special_order_txtboxActionPerformed

    private void stdTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stdTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stdTime_txtboxActionPerformed

    private void total_stdTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_total_stdTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_total_stdTime_txtboxActionPerformed

    private void dispatchLabelNo_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatchLabelNo_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatchLabelNo_txtboxActionPerformed

    private void dispatch_label_no_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatch_label_no_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatch_label_no_txtboxActionPerformed

    private void dispatch_label_no_txtboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dispatch_label_no_txtboxKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatch_label_no_txtboxKeyPressed

    private void dispatch_label_no_txtboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dispatch_label_no_txtboxKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatch_label_no_txtboxKeyReleased

    private void dispatch_label_no_txtboxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dispatch_label_no_txtboxKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatch_label_no_txtboxKeyTyped

    private void net_weight_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_net_weight_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_net_weight_txtActionPerformed

    private void gross_weight_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gross_weight_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gross_weight_txtActionPerformed

    private void volume_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volume_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_volume_txtActionPerformed

    private void eng_change_date_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eng_change_date_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eng_change_date_txtActionPerformed

    private void project_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_project_txtboxActionPerformed

    private void destination_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_destination_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_destination_txtboxActionPerformed

    private void open_sheet_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open_sheet_buttonActionPerformed
        msg_lbl.setText("");
        if (!palletNum_txtbox.getText().isEmpty()) {
            Query query = Helper.sess.createQuery(HQLHelper.GET_OPEN_SHEET);
            query.setParameter("id", Integer.valueOf(palletNumber_txtbox.getText()));
            Helper.sess.beginTransaction();
            Helper.sess.getTransaction().commit();
            List result = query.list();
            if (result.size() > 0) {
                HisOpenPalPrint pallet = (HisOpenPalPrint) result.get(0);
                pallet.setWriteTime(new Date());
                pallet.setWriteId(PackagingVars.context.getUser().getId());

                PrinterHelper.saveAndReprintOpenSheet(pallet);
            } else {
                msg_lbl.setText("Num. Palette introuvable dans les palettes ouvertes !");
            }
        } else {
            msg_lbl.setText("Num. Palette introuvable dans les palettes ouvertes !");
        }
    }//GEN-LAST:event_open_sheet_buttonActionPerformed

    private void close_sheet_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_sheet_buttonActionPerformed
        msg_lbl.setText("");
        if (!palletNum_txtbox.getText().isEmpty()) {
            try {
                WIZARD_PACKAGING_MODE_CHOICE wiz = new WIZARD_PACKAGING_MODE_CHOICE(null, true);
                int format = wiz.showDialog();
                if (format != -1) {
                    System.out.println("Printing closing sheet " + this.bc.getPalletNumber());
                    PrinterHelper.saveAndPrintClosingSheet(PackagingVars.mode3_context, this.bc, false, format);
                }else{
                    System.out.println("Print aborted");
                }
            } catch (IOException | DocumentException ex) {
                UILog.severe(ex.toString());
            } catch (Exception ex) {
                Logger.getLogger(PACKAGING_UI0010_PalletDetails_JFRAME.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*
            Query query = Helper.sess.createQuery(HQLHelper.GET_CONTAINER_BY_NUMBER);
            query.setParameter("palletNumber", palletNumber_txtbox.getText());
            Helper.sess.beginTransaction();
            Helper.sess.getTransaction().commit();
            List result = query.list();
            if (result.size() > 0) {
                BaseContainer b = (BaseContainer) result.get(0);
                b.setFifoTime(new Date());
                b.setWriteId(PackagingVars.context.getUser().getId());
                try {
                    WIZARD_PACKAGING_MODE_CHOICE wiz = new WIZARD_PACKAGING_MODE_CHOICE(null, true);
                    int mode = wiz.showDialog(); 
                    System.out.println("Printing closing sheet "+b.getPalletNumber());
                    PrinterHelper.saveAndPrintClosingSheetMode2(PackagingVars.mode3_context, b, true);
                    //TO Do : Conditions on mode PACKAGING_SCAN_MODE will be removed
                    //After removing the mode2 package from source code in future releases
                    /*
                    if (mode != -1) {
//                        if(GlobalVars.APP_PROP.getProperty("PACKAGING_SCAN_MODE").equals("1")){
                            if(mode == 1)
                            PrinterHelper.saveAndPrintClosingSheetMode1(PackagingVars.mode2_context, b, true);
                            else
                            PrinterHelper.saveAndPrintClosingSheetMode2(PackagingVars.mode2_context, b, true);    
                        }
                        else{
                            if(mode == 1)
                            PrinterHelper.saveAndPrintClosingSheetMode1(PackagingVars.mode3_context, b, true);
                            else
                            PrinterHelper.saveAndPrintClosingSheetMode2(PackagingVars.mode3_context, b, true);    
                        }
                    }   
                    
                } catch (IOException | DocumentException ex) {
                    UILog.severe(ex.toString());
                } catch (Exception ex) {
                    Logger.getLogger(PACKAGING_UI0010_PalletDetails_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                msg_lbl.setText("Num. Palette introuvable dans les palettes fermées !");
            } */
        } else {
            msg_lbl.setText("Num. Palette introuvable dans les palettes fermées !");
        }
    }//GEN-LAST:event_close_sheet_buttonActionPerformed

    private void dropButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropButtonActionPerformed
        if (palletId_txtbox.getText().equals("")) {
            msg_lbl.setText("Numéro palette introuvable !");
        } else if (!planId_txtbox.getText().equals("")) {
            UILog.warnDialog("La palette déjà reservée et ne peut être supprimer !\nLibérer la palette avant de la supprimer : \n "
                    + "- Plan de chargement " + planId_txtbox.getText() + "\n"
                    + "- Destination " + destination_txtbox.getText() + "\n"
                    + "- Num Pile " + pile_txtbox.getText() + "\n"
                    + "- Position " + position_txtbox.getText() + "\n"
            );
        } else {
            PACKAGING_UI9001_DropContainerConfirmation dropConfirm = new PACKAGING_UI9001_DropContainerConfirmation(this, true, this.bc, Integer.valueOf(GlobalVars.APP_PROP.getProperty("PACKAGING_SCAN_MODE")));
            dropConfirm.setVisible(true);
        }
    }//GEN-LAST:event_dropButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        try {
            reload_harness_list(this.bc.getPalletNumber());

        } catch (Exception e) {
            UILog.severeDialog(this, "Numéro palette invalide !", "Erreur !");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void set_state_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_set_state_btnActionPerformed
        PACKAGING_UI0010A_Changing_Status changeStatusUI = new PACKAGING_UI0010A_Changing_Status(this, true, this.bc, Integer.valueOf(GlobalVars.APP_PROP.getProperty("PACKAGING_SCAN_MODE")));
        changeStatusUI.setVisible(true);
    }//GEN-LAST:event_set_state_btnActionPerformed

    private void truckNo_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_truckNo_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_truckNo_txtboxActionPerformed

    public JTextField getPlanId_txtbox() {
        return planId_txtbox;
    }

    public void setPlanId_txtbox(JTextField planId_txtbox) {
        this.planId_txtbox = planId_txtbox;
    }

    public JTextField getPile_txtbox() {
        return pile_txtbox;
    }

    public void setPile_txtbox(JTextField pile_txtbox) {
        this.pile_txtbox = pile_txtbox;
    }

    public JTextField getPlanDestination_txtbox() {
        return planDestination_txtbox;
    }

    public void setPlanDestination_txtbox(JTextField planDestination_txtbox) {
        this.planDestination_txtbox = planDestination_txtbox;
    }

    public JTextField getPosition_txtbox() {
        return position_txtbox;
    }

    public void setPosition_txtbox(JTextField position_txtbox) {
        this.position_txtbox = position_txtbox;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea article_desc_txt;
    private javax.swing.JButton clear_btn;
    private javax.swing.JButton close_sheet_button;
    private javax.swing.JTextArea comment_txt;
    private javax.swing.JTextField completeTime_txtbox;
    private javax.swing.JTextField create_user_txtbox;
    private javax.swing.JTextField destination_txtbox;
    private javax.swing.JPanel dispatch;
    private javax.swing.JTextField dispatchLabelNo_txtbox;
    private javax.swing.JTextField dispatch_label_no_txtbox;
    private javax.swing.JPanel donnes_technique;
    private javax.swing.JButton dropButton;
    private javax.swing.JTextField eng_change_date_txt;
    private javax.swing.JTextArea eng_change_txt;
    private javax.swing.JPanel generales;
    private javax.swing.JTextField gross_weight_txt;
    private javax.swing.JTextField harnessPart_txtbox;
    private javax.swing.JButton history_btn;
    private javax.swing.JTextField index_txtbox;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField lineCreateTime_txtbox;
    private javax.swing.JTextField lineCreateUser_txtbox;
    private javax.swing.JLabel login_lbl3;
    private javax.swing.JLabel msg_lbl;
    private javax.swing.JTextField net_weight_txt;
    private javax.swing.JButton open_sheet_button;
    private javax.swing.JTextField packType_txtbox;
    private javax.swing.JTextField palletId_txtbox;
    private javax.swing.JTextField palletNum_txtbox;
    private javax.swing.JTextField palletNumber_txtbox;
    private javax.swing.JPanel pieces_liste;
    private javax.swing.JTextField pile_txtbox;
    private javax.swing.JTextField planCreateTime_txtbox;
    private javax.swing.JTextField planCreateUser_txtbox;
    private javax.swing.JTextField planDestination_txtbox;
    private javax.swing.JTextField planDispatchTime_txtbox;
    private javax.swing.JTextField planId_txtbox;
    private javax.swing.JTextField planStatus_txtbox;
    private javax.swing.JTextField position_txtbox;
    private javax.swing.JTextField project_txtbox;
    private javax.swing.JTextField qtyExptected_txtbox;
    private javax.swing.JTextField qtyRead_txtbox;
    private javax.swing.JTable searchResult_table;
    private javax.swing.JButton search_btn;
    private javax.swing.JButton set_state_btn;
    private javax.swing.JTextField special_order_txtbox;
    private javax.swing.JTextField startTime_txtbox;
    private javax.swing.JTextField state_txtbox;
    private javax.swing.JTextField stdTime_txtbox;
    private javax.swing.JTextField supplierPartNumber_txtbox;
    private javax.swing.JScrollPane table_scroll;
    private javax.swing.JTextField total_stdTime_txtbox;
    private javax.swing.JTextField truckNo_txtbox;
    private javax.swing.JTextField user_txtbox;
    private javax.swing.JTextField volume_txt;
    private javax.swing.JTextField workingTime_txtbox;
    private javax.swing.JTextField workstation_txtbox;
    // End of variables declaration//GEN-END:variables

    private void reload_harness_list(String palletNumber) {
        //################# Harness Data ####################
        try {
            Helper.startSession();
            Query query = null;
            List result;
            query = Helper.sess.createQuery(HQLHelper.GET_HP_BY_PALLET_NUMBER);
            query.setParameter("palletNumber", palletNumber);
            Helper.sess.getTransaction().commit();
            result = query.list();

            //reload table data                
            this.reload_container_table_data(result);
        } catch (Exception e) {
            msg_lbl.setText("Numéro palette invalide ou introuvable !");
        }
    }

}
