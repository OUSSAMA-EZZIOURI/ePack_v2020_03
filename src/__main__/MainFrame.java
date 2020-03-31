/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package __main__;

import entity.ManufactureUsers;
import gui.config.CONFIG_UI0001_CONFIG_BARCODE_JPANEL;
import gui.config.CONFIG_UI0001_CONFIG_UCS_JPANEL;
import gui.config.CONFIG_UI0002_CONFIG_PACK_MASTERDATA_JPANEL;
import gui.config.CONFIG_UI0003_CONFIG_USERS_JPANEL;
import gui.config.CONFIG_UI0004_CONFIG_PROJECT_MASTER_DATA_JPANEL;
import gui.cra.CRA_UI0001_PRODUCTION_PLAN;
import gui.cra.CRA_UI0002_WIRE_MASTER_DATA;
import gui.packaging.PackagingVars;
import gui.packaging.mode3.gui.PACKAGING_UI0001_Main_Mode3;
import gui.packaging.mode3.state.Mode3_S010_UserCodeScan;
import gui.packaging.reports.PACKAGING_UI0010_PalletDetails_JPANEL;
import gui.packaging.reports.PACKAGING_UI0011_ProdStatistics_JPANEL;
import gui.packaging.reports.PACKAGING_UI0012_HarnessDetails_JPANEL;
import gui.packaging.reports.PACKAGING_UI0015_DroppedPallets_JPANEL;
import gui.packaging.reports.PACKAGING_UI0016_DroppedHarness_JPANEL;
import gui.packaging.reports.PACKAGING_UI0017_UCS_List_JPANEL;
import gui.packaging.reports.PACKAGING_UI0018_OpenPallets_JPANEL;
import gui.packaging.reports.PACKAGING_UI0019_EfficiencyCalculation_JPANEL;
import gui.packaging.reports.PACKAGING_UI0021_FINISHED_GOODS_STOCK_JPANEL;
import gui.packaging.reports.PACKAGING_UI0022_ClosedPallets_JPANEL;
import gui.packaging_warehouse.PACKAGING_WAREHOUSE_UI0001_TRANSACTIONS_JPANEL;
import gui.packaging_warehouse.PACKAGING_WAREHOUSE_UI0002_STOCK_JPANEL;
import gui.packaging_warehouse.PackagingHelper;
import gui.warehouse_dispatch.WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL;
import gui.warehouse_dispatch.process_reservation.ReservationState;
import gui.warehouse_dispatch.process_reservation.S001_ReservPalletNumberScan;
import gui.warehouse_dispatch.state.WarehouseHelper;
import gui.warehouse_fg_reception.WAREHOUSE_FG_UI0001_SCAN_JPANEL;
import gui.packaging.reports.PACKAGING_UI0020_PALLET_LIST_JPANEL;
import helper.CloseTabButtonComponent;
import helper.Helper;
import helper.InactivityListener;
import helper.UIHelper;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.bytecode.stackmap.TypeData;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import ui.UILog;
import ui.info.InfoMsg;

/**
 *
 * @author Oussama
 */
public class MainFrame extends javax.swing.JFrame {

    static InactivityListener LogoutListener, ExitListener;

    int InactivityLogoutTime = 30; //In minutes

    //private ManufactureUsers user;
    public MainFrame(Frame parent, boolean b, ManufactureUsers user) {
        initComponents();
        GlobalVars.CONNECTED_USER = user;
        initMenus();
        intiGui();
    }

    private void intiGui() {
        this.setTitle(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION + " " + GlobalVars.APP_AUTHOR);
        ImageIcon img = new ImageIcon(GlobalVars.APP_PROP.getProperty("IMG_PATH") + "/icon.png");
        this.setIconImage(img.getImage());
        UIHelper.centerJFrame(this);
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        //Intialize the innactivity auto-logout
        Action auto_logout = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //Disconnect the user and close mainFrame UI
                System.out.println("Call for auto-logout action ");

                try {
                    /* Create and display the form */

                    StartFrame ui = new StartFrame();
                    ui.setVisible(true);
                    UILog.createDailyLogFile(GlobalVars.APP_PROP.getProperty("LOG_PATH"));
                    PropertiesLoader.createDailyOutPrintDir(GlobalVars.APP_PROP.getProperty("PRINT_DIR"),
                            GlobalVars.APP_PROP.getProperty("PRINT_PALLET_DIR"),
                            GlobalVars.APP_PROP.getProperty("PRINT_CLOSING_PALLET_DIR"),
                            GlobalVars.APP_PROP.getProperty("PRINT_PICKING_SHEET_DIR"),
                            GlobalVars.APP_PROP.getProperty("PRINT_DISPATCH_SHEET_DIR"));

                    JFrame frame = (JFrame) e.getSource();
                    frame.dispose();
                    try {
                        PackagingVars.Packaging_Gui_Mode3.dispose();
                        GlobalVars.OPENED_SCAN_WINDOW = 0;
                    } catch (Exception e1) {

                    }
                } catch (Exception e2) {
                    UILog.exceptionDialog(null, e2);
                }

            }

        };
        InactivityLogoutTime = Integer.valueOf(GlobalVars.APP_PROP.getProperty("INACTIVITY_LOGOUT_TIME"));
        LogoutListener = new InactivityListener(this, auto_logout, InactivityLogoutTime);
        LogoutListener.start();

    }

    public JTabbedPane getRootTabbedPane() {
        return rootTabbedPane;
    }

    public void setRootTabbedPane(JTabbedPane rootTabbedPane) {
        this.rootTabbedPane = rootTabbedPane;
    }

    /**
     *
     */
    private void initMenus() {
        //Load menus depends on user profile
        //Simple user
        MENU_01_PRODUCTION.setVisible(true);
        //Masquer le module conditionnement au utilisateur 0000
        //packaging_submenu.setVisible(false);

        MENU_02_MODULE_FG.setVisible(false);
        MENU_03_MODULE_DISPATCH.setVisible(false);
        MENU_04_MODULE_STOCK_PACKAGING.setVisible(false);

        MENU_05_MODULE_CONFIG.setVisible(false);
        if (GlobalVars.CONNECTED_USER.getAccessLevel() == 0) {
            menu012_packaging.setVisible(false);
        }
        //Warehouse agent
        if (GlobalVars.CONNECTED_USER.getAccessLevel() == 2000) {
            MENU_02_MODULE_FG.setVisible(true);
            MENU_03_MODULE_DISPATCH.setVisible(true);
            MENU_04_MODULE_STOCK_PACKAGING.setVisible(true);
        }
        //Administrator
        if (GlobalVars.CONNECTED_USER.getAccessLevel() == 9000) {
            MENU_02_MODULE_FG.setVisible(true);
            MENU_03_MODULE_DISPATCH.setVisible(true);
            MENU_04_MODULE_STOCK_PACKAGING.setVisible(true);
            MENU_05_MODULE_CONFIG.setVisible(true);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem3 = new javax.swing.JMenuItem();
        rootTabbedPane = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();
        MENU_01_PRODUCTION = new javax.swing.JMenu();
        menu012_packaging = new javax.swing.JMenuItem();
        menu011_prod_statistics = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        menu013_pallet_list = new javax.swing.JMenuItem();
        menu012_harness_details = new javax.swing.JMenuItem();
        menu010_pallet_details = new javax.swing.JMenuItem();
        menu018_open_pallet_list = new javax.swing.JMenuItem();
        menu018_open_pallet_list1 = new javax.swing.JMenuItem();
        menu012_deleted_pallet = new javax.swing.JMenuItem();
        menu012_deleted_harness = new javax.swing.JMenuItem();
        menu017_ucs = new javax.swing.JMenuItem();
        MENU_02_MODULE_FG = new javax.swing.JMenu();
        MENU_00_01_FG_RECEPTION = new javax.swing.JMenuItem();
        MENU_00_02_PART_STOCK = new javax.swing.JMenuItem();
        menu013_pallet_list1 = new javax.swing.JMenuItem();
        MENU_03_MODULE_DISPATCH = new javax.swing.JMenu();
        menu_dispatch = new javax.swing.JMenuItem();
        MENU_04_MODULE_STOCK_PACKAGING = new javax.swing.JMenu();
        MENU_00_03_PACKAGING_STOCK = new javax.swing.JMenuItem();
        packaging_stock_menu = new javax.swing.JMenuItem();
        MENU_06_CRA = new javax.swing.JMenu();
        MENU_06_PRODUCTION_PLAN = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        MENU_05_MODULE_CONFIG = new javax.swing.JMenu();
        MENU_01_00_CONFIG_UCS = new javax.swing.JMenuItem();
        MENU_01_01_CONFIG_BARCODE = new javax.swing.JMenuItem();
        MENU_01_02_CONFIG_PACK_MASTERDATA = new javax.swing.JMenuItem();
        MENU_01_03_AVANCE = new javax.swing.JMenu();
        MENU_01_03_00_CONFIG_USERS = new javax.swing.JMenuItem();
        MENU_01_03_01_CONFIG_COMPANY = new javax.swing.JMenuItem();
        MENU_06_MODULE_HELP = new javax.swing.JMenu();
        logoutMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();

        jMenuItem3.setText("jMenuItem3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 102));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        rootTabbedPane.setBackground(new java.awt.Color(0, 0, 51));
        rootTabbedPane.setForeground(new java.awt.Color(255, 255, 255));
        rootTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        rootTabbedPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        rootTabbedPane.setOpaque(true);

        menuBar.setBackground(new java.awt.Color(0, 0, 51));
        menuBar.setForeground(new java.awt.Color(0, 0, 102));

        MENU_01_PRODUCTION.setText("Production");

        menu012_packaging.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        menu012_packaging.setText("Conditionnement");
        menu012_packaging.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu012_packagingActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(menu012_packaging);

        menu011_prod_statistics.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menu011_prod_statistics.setText("Statistiques production");
        menu011_prod_statistics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu011_prod_statisticsActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(menu011_prod_statistics);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Calcul Efficience");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(jMenuItem1);

        menu013_pallet_list.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        menu013_pallet_list.setText("Liste des palettes");
        menu013_pallet_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu013_pallet_listActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(menu013_pallet_list);

        menu012_harness_details.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        menu012_harness_details.setText("Détails faisceau");
        menu012_harness_details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu012_harness_detailsActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(menu012_harness_details);

        menu010_pallet_details.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        menu010_pallet_details.setText("Détails palette");
        menu010_pallet_details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu010_pallet_detailsActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(menu010_pallet_details);

        menu018_open_pallet_list.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menu018_open_pallet_list.setText("Palettes ouvertes");
        menu018_open_pallet_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu018_open_pallet_listActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(menu018_open_pallet_list);

        menu018_open_pallet_list1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        menu018_open_pallet_list1.setText("Palettes fermées");
        menu018_open_pallet_list1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu018_open_pallet_list1ActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(menu018_open_pallet_list1);

        menu012_deleted_pallet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menu012_deleted_pallet.setText("Palettes annulées");
        menu012_deleted_pallet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu012_deleted_palletActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(menu012_deleted_pallet);

        menu012_deleted_harness.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menu012_deleted_harness.setText("Pièces annulés");
        menu012_deleted_harness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu012_deleted_harnessActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(menu012_deleted_harness);

        menu017_ucs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        menu017_ucs.setText("Liste des UCS");
        menu017_ucs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu017_ucsActionPerformed(evt);
            }
        });
        MENU_01_PRODUCTION.add(menu017_ucs);

        menuBar.add(MENU_01_PRODUCTION);

        MENU_02_MODULE_FG.setText("Magasin Produit finis");

        MENU_00_01_FG_RECEPTION.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        MENU_00_01_FG_RECEPTION.setText("Réception produits finis");
        MENU_00_01_FG_RECEPTION.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_00_01_FG_RECEPTIONActionPerformed(evt);
            }
        });
        MENU_02_MODULE_FG.add(MENU_00_01_FG_RECEPTION);

        MENU_00_02_PART_STOCK.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        MENU_00_02_PART_STOCK.setText("Stock produits finis");
        MENU_00_02_PART_STOCK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_00_02_PART_STOCKActionPerformed(evt);
            }
        });
        MENU_02_MODULE_FG.add(MENU_00_02_PART_STOCK);

        menu013_pallet_list1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        menu013_pallet_list1.setText("Liste des palettes");
        menu013_pallet_list1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu013_pallet_list1ActionPerformed(evt);
            }
        });
        MENU_02_MODULE_FG.add(menu013_pallet_list1);

        menuBar.add(MENU_02_MODULE_FG);

        MENU_03_MODULE_DISPATCH.setText("Dispatch");
        MENU_03_MODULE_DISPATCH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MENU_03_MODULE_DISPATCHMouseClicked(evt);
            }
        });
        MENU_03_MODULE_DISPATCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_03_MODULE_DISPATCHActionPerformed(evt);
            }
        });

        menu_dispatch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        menu_dispatch.setText("Dispatch");
        menu_dispatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_dispatchActionPerformed(evt);
            }
        });
        MENU_03_MODULE_DISPATCH.add(menu_dispatch);

        menuBar.add(MENU_03_MODULE_DISPATCH);

        MENU_04_MODULE_STOCK_PACKAGING.setText("Magasin emballage");

        MENU_00_03_PACKAGING_STOCK.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        MENU_00_03_PACKAGING_STOCK.setText("Mouvements des emballages");
        MENU_00_03_PACKAGING_STOCK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_00_03_PACKAGING_STOCKActionPerformed(evt);
            }
        });
        MENU_04_MODULE_STOCK_PACKAGING.add(MENU_00_03_PACKAGING_STOCK);

        packaging_stock_menu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        packaging_stock_menu.setText("Stock d'emballage");
        packaging_stock_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                packaging_stock_menuMouseClicked(evt);
            }
        });
        packaging_stock_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packaging_stock_menuActionPerformed(evt);
            }
        });
        MENU_04_MODULE_STOCK_PACKAGING.add(packaging_stock_menu);

        menuBar.add(MENU_04_MODULE_STOCK_PACKAGING);

        MENU_06_CRA.setText("CRA");

        MENU_06_PRODUCTION_PLAN.setText("Planning de production");
        MENU_06_PRODUCTION_PLAN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_06_PRODUCTION_PLANActionPerformed(evt);
            }
        });
        MENU_06_CRA.add(MENU_06_PRODUCTION_PLAN);

        jMenuItem2.setText("Master data fil");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        MENU_06_CRA.add(jMenuItem2);

        menuBar.add(MENU_06_CRA);

        MENU_05_MODULE_CONFIG.setText("Configuration");

        MENU_01_00_CONFIG_UCS.setText("Configuration Standard Pack");
        MENU_01_00_CONFIG_UCS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_00_CONFIG_UCSActionPerformed(evt);
            }
        });
        MENU_05_MODULE_CONFIG.add(MENU_01_00_CONFIG_UCS);

        MENU_01_01_CONFIG_BARCODE.setText("Format Code à barre / QR");
        MENU_01_01_CONFIG_BARCODE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_01_CONFIG_BARCODEActionPerformed(evt);
            }
        });
        MENU_05_MODULE_CONFIG.add(MENU_01_01_CONFIG_BARCODE);

        MENU_01_02_CONFIG_PACK_MASTERDATA.setText("Packaging Master Data");
        MENU_01_02_CONFIG_PACK_MASTERDATA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_02_CONFIG_PACK_MASTERDATAActionPerformed(evt);
            }
        });
        MENU_05_MODULE_CONFIG.add(MENU_01_02_CONFIG_PACK_MASTERDATA);

        MENU_01_03_AVANCE.setText("Avancé");

        MENU_01_03_00_CONFIG_USERS.setText("Utilisateurs");
        MENU_01_03_00_CONFIG_USERS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_03_00_CONFIG_USERSActionPerformed(evt);
            }
        });
        MENU_01_03_AVANCE.add(MENU_01_03_00_CONFIG_USERS);

        MENU_01_03_01_CONFIG_COMPANY.setText("Paramétrage de base");
        MENU_01_03_01_CONFIG_COMPANY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_03_01_CONFIG_COMPANYActionPerformed(evt);
            }
        });
        MENU_01_03_AVANCE.add(MENU_01_03_01_CONFIG_COMPANY);

        MENU_05_MODULE_CONFIG.add(MENU_01_03_AVANCE);

        menuBar.add(MENU_05_MODULE_CONFIG);

        MENU_06_MODULE_HELP.setText("Session");

        logoutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        logoutMenuItem.setMnemonic('x');
        logoutMenuItem.setText("Se déconnecter");
        logoutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutMenuItemActionPerformed(evt);
            }
        });
        MENU_06_MODULE_HELP.add(logoutMenuItem);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Quitter");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        MENU_06_MODULE_HELP.add(exitMenuItem);

        menuBar.add(MENU_06_MODULE_HELP);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1020, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(rootTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1008, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(rootTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * To be used because the custom UILog object has not been initialized yet !
     */
    private static final Logger LOGGER = Logger.getLogger(TypeData.ClassName.class.getName());

    // static initializer
    {
        /* Create and display the form */
        String feedback = PropertiesLoader.loadConfigProperties();
        LOGGER.log(Level.INFO, feedback);
    }

    private void menu011_prod_statisticsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu011_prod_statisticsActionPerformed
        addNewTab(new PACKAGING_UI0011_ProdStatistics_JPANEL(rootTabbedPane), evt);
    }//GEN-LAST:event_menu011_prod_statisticsActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        addNewTab(new PACKAGING_UI0019_EfficiencyCalculation_JPANEL(this, false), evt);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void menu013_pallet_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu013_pallet_listActionPerformed
        addNewTab(new PACKAGING_UI0020_PALLET_LIST_JPANEL(rootTabbedPane, false), evt);
    }//GEN-LAST:event_menu013_pallet_listActionPerformed

    private void menu018_open_pallet_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu018_open_pallet_listActionPerformed
        addNewTab(new PACKAGING_UI0018_OpenPallets_JPANEL(rootTabbedPane), evt);
    }//GEN-LAST:event_menu018_open_pallet_listActionPerformed

    private void menu018_open_pallet_list1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu018_open_pallet_list1ActionPerformed
        addNewTab(new PACKAGING_UI0022_ClosedPallets_JPANEL(rootTabbedPane), evt);
    }//GEN-LAST:event_menu018_open_pallet_list1ActionPerformed

    private void menu010_pallet_detailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu010_pallet_detailsActionPerformed

        try {
            if (PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN || PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT) {
                addNewTab(new PACKAGING_UI0010_PalletDetails_JPANEL(rootTabbedPane), evt);

            } else {
                addNewTab(new PACKAGING_UI0010_PalletDetails_JPANEL(rootTabbedPane), evt);
            }
        } catch (NullPointerException ex) {
            addNewTab(new PACKAGING_UI0010_PalletDetails_JPANEL(rootTabbedPane), evt);
        }
    }//GEN-LAST:event_menu010_pallet_detailsActionPerformed

    private void menu012_deleted_palletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu012_deleted_palletActionPerformed
        addNewTab(new PACKAGING_UI0015_DroppedPallets_JPANEL(this, false), evt);
    }//GEN-LAST:event_menu012_deleted_palletActionPerformed

    private void menu012_harness_detailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu012_harness_detailsActionPerformed
        //PACKAGING_UI0012_HarnessDetails harnessDetails;
        if (PackagingVars.context.getUser() != null && PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
            addNewTab(new PACKAGING_UI0012_HarnessDetails_JPANEL(rootTabbedPane, true), evt);
        } else {
            addNewTab(new PACKAGING_UI0012_HarnessDetails_JPANEL(rootTabbedPane, false), evt);
        }
    }//GEN-LAST:event_menu012_harness_detailsActionPerformed

    private void menu012_deleted_harnessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu012_deleted_harnessActionPerformed
        addNewTab(new PACKAGING_UI0016_DroppedHarness_JPANEL(this, false), evt);
    }//GEN-LAST:event_menu012_deleted_harnessActionPerformed

    private void menu017_ucsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu017_ucsActionPerformed
        addNewTab(new PACKAGING_UI0017_UCS_List_JPANEL(this, false), evt);
    }//GEN-LAST:event_menu017_ucsActionPerformed

    private void MENU_00_02_PART_STOCKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_00_02_PART_STOCKActionPerformed
        addNewTab(new PACKAGING_UI0021_FINISHED_GOODS_STOCK_JPANEL(rootTabbedPane), evt);
    }//GEN-LAST:event_MENU_00_02_PART_STOCKActionPerformed

    private void MENU_00_01_FG_RECEPTIONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_00_01_FG_RECEPTIONActionPerformed
        //Create and display the dispatch interface
        addNewTab(new WAREHOUSE_FG_UI0001_SCAN_JPANEL(null, rootTabbedPane, GlobalVars.CONNECTED_USER), evt);
    }//GEN-LAST:event_MENU_00_01_FG_RECEPTIONActionPerformed

    private void MENU_01_00_CONFIG_UCSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_00_CONFIG_UCSActionPerformed
        addNewTab(new CONFIG_UI0001_CONFIG_UCS_JPANEL(rootTabbedPane), evt);
    }//GEN-LAST:event_MENU_01_00_CONFIG_UCSActionPerformed

    private void MENU_01_01_CONFIG_BARCODEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_01_CONFIG_BARCODEActionPerformed

        addNewTab(new CONFIG_UI0001_CONFIG_BARCODE_JPANEL(rootTabbedPane), evt);
    }//GEN-LAST:event_MENU_01_01_CONFIG_BARCODEActionPerformed

    private void MENU_01_03_00_CONFIG_USERSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_03_00_CONFIG_USERSActionPerformed
        addNewTab(new CONFIG_UI0003_CONFIG_USERS_JPANEL(rootTabbedPane), evt);

    }//GEN-LAST:event_MENU_01_03_00_CONFIG_USERSActionPerformed

    private void MENU_00_03_PACKAGING_STOCKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_00_03_PACKAGING_STOCKActionPerformed
        Helper.startSession();

        GlobalVars.CONNECTED_USER.setLoginTime(new Date());
        WarehouseHelper.warehouse_reserv_context.setUser(GlobalVars.CONNECTED_USER);
        WarehouseHelper.warehouse_reserv_context.getUser().update(WarehouseHelper.warehouse_reserv_context.getUser());

        PackagingHelper.user = GlobalVars.CONNECTED_USER;
        //Create and display the packaing main form
        GlobalMethods.addNewTabToParent("Mouvements des emballages", this.rootTabbedPane, new PACKAGING_WAREHOUSE_UI0001_TRANSACTIONS_JPANEL(this.rootTabbedPane), evt);
    }//GEN-LAST:event_MENU_00_03_PACKAGING_STOCKActionPerformed

    private void MENU_01_02_CONFIG_PACK_MASTERDATAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_02_CONFIG_PACK_MASTERDATAActionPerformed
        addNewTab(new CONFIG_UI0002_CONFIG_PACK_MASTERDATA_JPANEL(this, false), evt);
    }//GEN-LAST:event_MENU_01_02_CONFIG_PACK_MASTERDATAActionPerformed

    private void MENU_01_03_01_CONFIG_COMPANYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_03_01_CONFIG_COMPANYActionPerformed
        addNewTab(new CONFIG_UI0004_CONFIG_PROJECT_MASTER_DATA_JPANEL(this), evt);
    }//GEN-LAST:event_MENU_01_03_01_CONFIG_COMPANYActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        int confirmed = JOptionPane.showConfirmDialog(this, "Voulez-vous quitter le programme ?",
                "Quitter ?",
                JOptionPane.WARNING_MESSAGE);

        if (confirmed == 0) {
            System.exit(0);
        }

    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void packaging_stock_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_packaging_stock_menuMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_packaging_stock_menuMouseClicked

    private void packaging_stock_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packaging_stock_menuActionPerformed
        GlobalMethods.addNewTabToParent("Stock d'emballage", this.rootTabbedPane, new PACKAGING_WAREHOUSE_UI0002_STOCK_JPANEL(this.rootTabbedPane), evt);
    }//GEN-LAST:event_packaging_stock_menuActionPerformed
//
//   private void menu_scan_mode_1ActionPerformed(java.awt.event.ActionEvent evt) {                                                 
//        PackagingVars.mode2_context.setState(new Mode2_S010_UserCodeScan());
//        PackagingVars.Packaging_Gui_Mode2 = new PACKAGING_UI0001_Main_Mode2(null, this);
//        PackagingVars.Packaging_Gui_Mode2.reloadDataTable();
//        PackagingVars.Packaging_Gui_Mode2.disableAdminMenus();
//        
//    }   

    private void MENU_03_MODULE_DISPATCHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MENU_03_MODULE_DISPATCHMouseClicked

    }//GEN-LAST:event_MENU_03_MODULE_DISPATCHMouseClicked

    private void MENU_03_MODULE_DISPATCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_03_MODULE_DISPATCHActionPerformed

    }//GEN-LAST:event_MENU_03_MODULE_DISPATCHActionPerformed

    private void menu_dispatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_dispatchActionPerformed
        Helper.startSession();

        GlobalVars.CONNECTED_USER.setLoginTime(new Date());
        WarehouseHelper.warehouse_reserv_context.setUser(GlobalVars.CONNECTED_USER);
        WarehouseHelper.warehouse_reserv_context.getUser().update(WarehouseHelper.warehouse_reserv_context.getUser());
        //Go back to step S020
        ReservationState state = new S001_ReservPalletNumberScan();
        WarehouseHelper.warehouse_reserv_context.setState(state);
        String str = String.format(InfoMsg.APP_INFO0003[1],
                GlobalVars.CONNECTED_USER.getFirstName() + " " + GlobalVars.CONNECTED_USER.getLastName()
                + " / " + GlobalVars.CONNECTED_USER.getLogin(), GlobalVars.APP_HOSTNAME,
                GlobalMethods.getStrTimeStamp() + " Dispatch interface : ");

        UILog.info(str);
        //Create and display the dispatch interface
        //WarehouseHelper.Dispatch_Gui = new WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN(null, this);

        Object[] objects = {rootTabbedPane};
        WarehouseHelper.Dispatch_Gui_Jpanel = new WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL(objects, this);
        addNewTab(WarehouseHelper.Dispatch_Gui_Jpanel, evt);
        //Set connected user label text
        WarehouseHelper.Dispatch_Gui_Jpanel.setUserLabelText(GlobalVars.CONNECTED_USER.getFirstName() + " "
                + GlobalVars.CONNECTED_USER.getLastName() + " Connecté à la machine "
                + "[" + GlobalVars.APP_HOSTNAME + "]"
        );

        //Auth réussie, Passage à l'état S02 de lecture des fiches Galia               
        WarehouseHelper.warehouse_reserv_context.setState(new S001_ReservPalletNumberScan());
    }//GEN-LAST:event_menu_dispatchActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int confirmed = JOptionPane.showConfirmDialog(null,
                "Voulez-vous quittez le programme ?", "Quitter !",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == 0) {

            System.exit(-1);
        }
    }//GEN-LAST:event_formWindowClosing

    private void logoutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutMenuItemActionPerformed
        int confirmed = JOptionPane.showConfirmDialog(this, "Voulez-vous fermer la session ?",
                "Se déconnecter",
                JOptionPane.WARNING_MESSAGE);

        if (confirmed == 0) {
                
            if(PackagingVars.Packaging_Gui_Mode3 != null){
                PackagingVars.Packaging_Gui_Mode3.dispose();
                GlobalVars.OPENED_SCAN_WINDOW = 0;
            }
//            if (PackagingVars.Packaging_Gui_Mode2 != null || PackagingVars.Packaging_Gui_Mode3 != null) {
//                //Ferme l'interface contenu contenant s'elle ouverte.
//                if (GlobalVars.APP_PROP.getProperty("PACKAGING_SCAN_MODE").equals("1")) {
//                    PackagingVars.Packaging_Gui_Mode2.dispose();
//                    GlobalVars.OPENED_SCAN_WINDOW = 0;
//                } else if (GlobalVars.APP_PROP.getProperty("PACKAGING_SCAN_MODE").equals("2")) {
//                    PackagingVars.Packaging_Gui_Mode3.dispose();
//                    GlobalVars.OPENED_SCAN_WINDOW = 0;
//                }
//            }
                try {
                /* Create and display the form */
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        StartFrame ui = new StartFrame();

                        ui.setVisible(true);
                        UILog.createDailyLogFile(GlobalVars.APP_PROP.getProperty("LOG_PATH"));
                        PropertiesLoader.createDailyOutPrintDir(GlobalVars.APP_PROP.getProperty("PRINT_DIR"),
                                GlobalVars.APP_PROP.getProperty("PRINT_PALLET_DIR"),
                                GlobalVars.APP_PROP.getProperty("PRINT_CLOSING_PALLET_DIR"),
                                GlobalVars.APP_PROP.getProperty("PRINT_PICKING_SHEET_DIR"),
                                GlobalVars.APP_PROP.getProperty("PRINT_DISPATCH_SHEET_DIR"));
                    }
                });
                this.dispose();

            } catch (Exception e) {
                UILog.exceptionDialog(null, e);
            }
        }
    }//GEN-LAST:event_logoutMenuItemActionPerformed

    private void menu013_pallet_list1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu013_pallet_list1ActionPerformed
        addNewTab(new PACKAGING_UI0020_PALLET_LIST_JPANEL(rootTabbedPane, false), evt);
    }//GEN-LAST:event_menu013_pallet_list1ActionPerformed

    private void menu012_packagingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu012_packagingActionPerformed
        if (GlobalVars.OPENED_SCAN_WINDOW == 0) {
            PackagingVars.mode3_context.setState(new Mode3_S010_UserCodeScan());
            PackagingVars.Packaging_Gui_Mode3 = new PACKAGING_UI0001_Main_Mode3(null, this);
            PackagingVars.Packaging_Gui_Mode3.reloadDataTable();
            PackagingVars.Packaging_Gui_Mode3.disableAdminMenus();
            GlobalVars.OPENED_SCAN_WINDOW = 1;
            System.out.println("OPENED_SCAN_WINDOW " + GlobalVars.OPENED_SCAN_WINDOW);
        } else {
            //UILog.severeDialog(this, "Une fenêtre du module scanne déjà ouverte.", "Nombre de fenêtre maximal atteint !");
            PackagingVars.Packaging_Gui_Mode3.requestFocus();
        }
    }//GEN-LAST:event_menu012_packagingActionPerformed

    private void MENU_06_PRODUCTION_PLANActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_06_PRODUCTION_PLANActionPerformed
        addNewTab(new CRA_UI0001_PRODUCTION_PLAN(rootTabbedPane), evt);
    }//GEN-LAST:event_MENU_06_PRODUCTION_PLANActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        addNewTab(new CRA_UI0002_WIRE_MASTER_DATA(rootTabbedPane), evt);
    }//GEN-LAST:event_jMenuItem2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem MENU_00_01_FG_RECEPTION;
    private javax.swing.JMenuItem MENU_00_02_PART_STOCK;
    private javax.swing.JMenuItem MENU_00_03_PACKAGING_STOCK;
    private javax.swing.JMenuItem MENU_01_00_CONFIG_UCS;
    private javax.swing.JMenuItem MENU_01_01_CONFIG_BARCODE;
    private javax.swing.JMenuItem MENU_01_02_CONFIG_PACK_MASTERDATA;
    private javax.swing.JMenuItem MENU_01_03_00_CONFIG_USERS;
    private javax.swing.JMenuItem MENU_01_03_01_CONFIG_COMPANY;
    private javax.swing.JMenu MENU_01_03_AVANCE;
    private javax.swing.JMenu MENU_01_PRODUCTION;
    private javax.swing.JMenu MENU_02_MODULE_FG;
    private javax.swing.JMenu MENU_03_MODULE_DISPATCH;
    private javax.swing.JMenu MENU_04_MODULE_STOCK_PACKAGING;
    private javax.swing.JMenu MENU_05_MODULE_CONFIG;
    private javax.swing.JMenu MENU_06_CRA;
    private javax.swing.JMenu MENU_06_MODULE_HELP;
    private javax.swing.JMenuItem MENU_06_PRODUCTION_PLAN;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem logoutMenuItem;
    private javax.swing.JMenuItem menu010_pallet_details;
    private javax.swing.JMenuItem menu011_prod_statistics;
    private javax.swing.JMenuItem menu012_deleted_harness;
    private javax.swing.JMenuItem menu012_deleted_pallet;
    private javax.swing.JMenuItem menu012_harness_details;
    private javax.swing.JMenuItem menu012_packaging;
    private javax.swing.JMenuItem menu013_pallet_list;
    private javax.swing.JMenuItem menu013_pallet_list1;
    private javax.swing.JMenuItem menu017_ucs;
    private javax.swing.JMenuItem menu018_open_pallet_list;
    private javax.swing.JMenuItem menu018_open_pallet_list1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menu_dispatch;
    private javax.swing.JMenuItem packaging_stock_menu;
    private javax.swing.JTabbedPane rootTabbedPane;
    // End of variables declaration//GEN-END:variables

    /**
     * Add a new tab with JPanel in the main tab.
     *
     * @param panel
     * @param evt
     */
    public void addNewTab(JPanel panel, java.awt.event.ActionEvent evt) {
        rootTabbedPane.addTab(((JMenuItem) evt.getSource()).getText(), null, panel,
                ((JMenuItem) evt.getSource()).getText());

        rootTabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        rootTabbedPane.setTabComponentAt(rootTabbedPane.getTabCount() - 1,
                new CloseTabButtonComponent(rootTabbedPane));
        rootTabbedPane.setSelectedIndex(rootTabbedPane.getTabCount() - 1);
    }

}
