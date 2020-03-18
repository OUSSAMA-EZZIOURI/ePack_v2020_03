/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging.mode2.gui;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import __main__.MainFrame;
import gui.packaging.reports.PACKAGING_UI0013_PalletWaiting;
import java.util.List;
import javax.swing.JFrame;
import helper.Helper;
import helper.HQLHelper;
import entity.BaseContainer;
import entity.BaseContainerTmp;
import entity.BaseHarnessAdditionalBarecodeTmp;
import entity.HisLogin;
import gui.packaging.PackagingVars;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import gui.packaging.mode2.state.Mode2_S010_UserCodeScan;
import gui.packaging.mode2.state.Mode2_S040_ClosingPallet;
import gui.packaging.mode2.state.Mode2_State;
import gui.packaging.mode3.state.Mode3_S040_ClosingPallet;
import gui.packaging.reports.PACKAGING_UI0010_PalletDetails_JFRAME;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JTextArea;
import ui.UILog;

/**
 *
 * @author user
 */
public final class PACKAGING_UI0001_Main_Mode2 extends javax.swing.JFrame implements KeyListener, WindowListener {

    String str = null;
    public Mode2_State state = PackagingVars.mode2_context.getState();
    Vector<String> container_table_header = new Vector<String>();
    List<String> table_header = Arrays.asList(
            "Pallet Number",
            "Harness Part",
            "Index",
            "Pack Type",
            "Quantity Expected",
            "Quantity Read",
            "Create Time",
            "Harness Type",
            "State"
    );
    Vector container_table_data = new Vector();

    /* "Pallet number" Column index in "container_table" */
    private static int PALLET_NUMBER_COLINDEX = 0;
    MainFrame parent;

    //########################################################################
    public PACKAGING_UI0001_Main_Mode2() {
    }

    /**
     * Creates new form UI0000_Login
     *
     * @param context
     */
    public PACKAGING_UI0001_Main_Mode2(Object[] context, MainFrame parent) {
        this.parent = parent;
        initComponents();
        //Initialiser les valeurs globales de test (Pattern Liste,...)

        Helper.startSession();

        //loadBarcodeConfig();
        initGui();
    }

    public void initGui() {
        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
        // Set jTable Row Style
        //Centrer le jframe dans le screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        this.setIconLabel(state.getImg());

        this.connectedUserName_label.setHorizontalAlignment(JLabel.CENTER);
        //-----------------------

        //Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = dim.width;
        int ySize = dim.height;
        this.setSize(xSize, ySize);

        panel_top.setLayout(new BorderLayout());
        panel_top.setBackground(Color.DARK_GRAY);
        //panel_top.setPreferredSize(new Dimension(xSize, (int) (Math.round(ySize * 0.90))));
        this.add(panel_top, BorderLayout.NORTH);

        panel_bottom.setLayout(new BorderLayout());
        //panel_bottom.setPreferredSize(new Dimension((int) (Math.round(xSize * 0.80)), (int) (Math.round(ySize * 0.20))));
        this.add(panel_bottom, BorderLayout.SOUTH);
        //container_table.setPreferredSize(new Dimension((int) (Math.round(xSize * 0.60)), (int) (Math.round(ySize * 0.80))));
        //------------------------
        //Initialize table header                        
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        img_lbl.setHorizontalAlignment(JLabel.CENTER);
        img_lbl.setVerticalAlignment(JLabel.CENTER);

        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        //Initialize double clique on table row
        this.initContainerTableDoubleClick();

        Helper.loadProjectsInJbox(harnessTypeBox);
        Helper.loadProjectsInJbox(harnessTypeFilterBox);

        //this.day_date_picker.setDate(new Date());
        //Focus on scann textbox        
        scan_txtbox.requestFocus();

        //Show the jframe
        this.setVisible(true);

        //Maximaze the jframe
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        //Load table header
        //load_table_header();
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);
    }

    //########################################################################
    //######################## MAIN TABLE METHODS ############################
    //########################################################################
    public void load_table_header() {
        //this.reset_table_content();
        container_table_header = new Vector<String>();
        for (Iterator<String> it = table_header.iterator(); it.hasNext();) {
            container_table_header.add(it.next());
        }

        container_table.setModel(new DefaultTableModel(container_table_data, container_table_header));
    }

    public void reset_table_content() {
        container_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(container_table_data, container_table_header);
        container_table.setModel(dataModel);
    }

    private void initContainerTableDoubleClick() {

        this.container_table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String palletNum = String.valueOf(container_table.getValueAt(container_table.getSelectedRow(), PALLET_NUMBER_COLINDEX));
                    try {
                        if (PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN || PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT) {
                            //new PACKAGING_UI0010_PalletDetails(null, rootPaneCheckingEnabled, String.valueOf(container_table.getValueAt(container_table.getSelectedRow(), PALLET_NUMBER_COLINDEX)), "", 1, true, true, true).setVisible(true);
                            new PACKAGING_UI0010_PalletDetails_JFRAME(null, palletNum, "", 1, true, true, true).setVisible(true);
                        } else {
                            new PACKAGING_UI0010_PalletDetails_JFRAME(null, palletNum, "", 1, false, false, false).setVisible(true);
                            //new PACKAGING_UI0010_PalletDetails(null, rootPaneCheckingEnabled, String.valueOf(container_table.getValueAt(container_table.getSelectedRow(), PALLET_NUMBER_COLINDEX)), "", 1, false, false, false).setVisible(true);
                        }
                    } catch (NullPointerException ex) {
                        new PACKAGING_UI0010_PalletDetails_JFRAME(null, palletNum, "", 1, false, false, false).setVisible(true);
                        //new PACKAGING_UI0010_PalletDetails(null, rootPaneCheckingEnabled, String.valueOf(container_table.getValueAt(container_table.getSelectedRow(), PALLET_NUMBER_COLINDEX)), "", 1, false, false, false).setVisible(true);
                    }
                }
            }
        }
        );
    }

    public void reload_container_table_data(List resultList) {
        this.reset_table_content();

        this.load_table_header();

        for (Object o : resultList) {
            BaseContainer bc = (BaseContainer) o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(bc.getPalletNumber());
            oneRow.add(bc.getHarnessPart());
            oneRow.add(bc.getHarnessIndex());
            oneRow.add(bc.getPackType());
            oneRow.add(bc.getQtyExpected());
            oneRow.add(bc.getQtyRead());
            oneRow.add(bc.getCreateTimeString("dd/MM/yy HH:mm"));
            oneRow.add(bc.getHarnessType());
            oneRow.add(bc.getContainerState());
            container_table_data.add(oneRow);
            System.out.println("Container to add " + bc.toString());
        }
        container_table.setModel(new DefaultTableModel(container_table_data, container_table_header));

        //Initialize default style for table container
        setContainerTableRowsStyle();
    }

    public void setContainerTableRowsStyle() {
        //Initialize default style for table container

        //#######################
        container_table.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.BOLD, Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONTSIZE"))));
        container_table.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));
        container_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                String status = (String) table.getModel().getValueAt(row, GlobalVars.PALLET_STATE_COL_INDEX);
                //############### OPEN
                if (GlobalVars.PALLET_OPEN.equals(status)) {
                    setBackground(Color.YELLOW);
                    setForeground(Color.BLACK);
                } //############### CLOSED
                else if (GlobalVars.PALLET_CLOSED.equals(status)) {
                    setBackground(Color.LIGHT_GRAY);
                    setForeground(Color.BLACK);
                } //############### BLOCKED
                else if (GlobalVars.PALLET_BLOCKED.equals(status)) {
                    setBackground(Color.RED);
                    setForeground(Color.BLACK);
                } //############### WAITING
                else if (GlobalVars.PALLET_WAITING.equals(status)) {
                    setBackground(Color.CYAN);
                    setForeground(Color.BLACK);
                } //############### STORED
                else if (GlobalVars.PALLET_STORED.equals(status)) {
                    setBackground(Color.ORANGE);
                    setForeground(Color.BLACK);
                }//############### RESERVED
                else if (GlobalVars.PALLET_RESERVED.equals(status)) {
                    setBackground(Color.PINK);
                    setForeground(Color.BLACK);
                } //############### DISPATCHED
                else if (GlobalVars.PALLET_DISPATCHED.equals(status)) {
                    setBackground(Color.BLUE);
                    setForeground(Color.BLACK);
                }

                setHorizontalAlignment(JLabel.CENTER);

                return this;
            }
        });
        //#######################
        this.disableEditingTable();

    }

    public void disableEditingTable() {
        for (int c = 0; c < container_table.getColumnCount(); c++) {
            Class<?> col_class = container_table.getColumnClass(c);
            container_table.setDefaultEditor(col_class, null);        // remove editor            
        }
    }

    public void enableOperatorMenus() {
        this.menu02_process.setVisible(true);
        this.menu020_waiting.setVisible(true);
    }

    public void disableOperatorMenus() {
        this.menu02_process.setVisible(false);
        this.menu020_waiting.setVisible(false);
    }

    public void enableAdminMenus() {
        this.enableOperatorMenus();

    }

    public void disableAdminMenus() {
        this.disableOperatorMenus();
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
        panel_top = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        container_table = new javax.swing.JTable();
        requestedPallet_label = new javax.swing.JLabel();
        connectedUserName_label = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        refresh = new javax.swing.JButton();
        harnessTypeFilterBox = new javax.swing.JComboBox();
        scan_txtbox = new javax.swing.JTextField();
        harnessTypeBox = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        feedbackTextarea = new javax.swing.JTextArea();
        img_lbl = new javax.swing.JLabel();
        requestedPallet_label1 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        panel_bottom = new javax.swing.JPanel();
        menu_bar = new javax.swing.JMenuBar();
        menu02_process = new javax.swing.JMenu();
        menu020_waiting = new javax.swing.JMenuItem();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Packaging Module");
        setBackground(new java.awt.Color(51, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panel_top.setBackground(new java.awt.Color(255, 255, 255));
        panel_top.setForeground(new java.awt.Color(255, 255, 255));
        panel_top.setAutoscrolls(true);
        panel_top.setMinimumSize(new java.awt.Dimension(800, 400));
        panel_top.setPreferredSize(new java.awt.Dimension(1364, 780));
        panel_top.setRequestFocusEnabled(false);

        container_table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        container_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(container_table);

        requestedPallet_label.setBackground(new java.awt.Color(255, 0, 0));
        requestedPallet_label.setFont(new java.awt.Font("Courier New", 1, 36)); // NOI18N
        requestedPallet_label.setForeground(new java.awt.Color(255, 255, 255));
        requestedPallet_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        connectedUserName_label.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        connectedUserName_label.setForeground(new java.awt.Color(255, 255, 255));
        connectedUserName_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        connectedUserName_label.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/refresh.png"))); // NOI18N
        refresh.setText("Actualiser");
        refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });

        harnessTypeFilterBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                harnessTypeFilterBoxItemStateChanged(evt);
            }
        });
        harnessTypeFilterBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                harnessTypeFilterBoxActionPerformed(evt);
            }
        });

        scan_txtbox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        scan_txtbox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        scan_txtbox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scan_txtbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                scan_txtboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                scan_txtboxFocusLost(evt);
            }
        });
        scan_txtbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                scan_txtboxKeyPressed(evt);
            }
        });

        harnessTypeBox.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        harnessTypeBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                harnessTypeBoxItemStateChanged(evt);
            }
        });
        harnessTypeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                harnessTypeBoxActionPerformed(evt);
            }
        });

        feedbackTextarea.setEditable(false);
        feedbackTextarea.setBackground(new java.awt.Color(255, 255, 204));
        feedbackTextarea.setColumns(20);
        feedbackTextarea.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        feedbackTextarea.setLineWrap(true);
        feedbackTextarea.setRows(5);
        feedbackTextarea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane2.setViewportView(feedbackTextarea);

        img_lbl.setBackground(new java.awt.Color(204, 204, 255));
        img_lbl.setForeground(new java.awt.Color(255, 255, 255));
        img_lbl.setText(" ");
        img_lbl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        requestedPallet_label1.setBackground(new java.awt.Color(255, 0, 0));
        requestedPallet_label1.setFont(new java.awt.Font("Courier New", 1, 36)); // NOI18N
        requestedPallet_label1.setForeground(new java.awt.Color(255, 255, 255));
        requestedPallet_label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        requestedPallet_label1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 204, 153));
        jLabel1.setText("Scan mode : 2");

        javax.swing.GroupLayout panel_topLayout = new javax.swing.GroupLayout(panel_top);
        panel_top.setLayout(panel_topLayout);
        panel_topLayout.setHorizontalGroup(
            panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_topLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1304, 1304, 1304)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1350, Short.MAX_VALUE))
            .addGroup(panel_topLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1240, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel_topLayout.createSequentialGroup()
                            .addComponent(harnessTypeFilterBox, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(harnessTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(scan_txtbox)))
                    .addGroup(panel_topLayout.createSequentialGroup()
                        .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel_topLayout.createSequentialGroup()
                                .addComponent(img_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(requestedPallet_label1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panel_topLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(connectedUserName_label, javax.swing.GroupLayout.PREFERRED_SIZE, 945, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(113, 113, 113)
                        .addComponent(requestedPallet_label, javax.swing.GroupLayout.PREFERRED_SIZE, 1345, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        panel_topLayout.setVerticalGroup(
            panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_topLayout.createSequentialGroup()
                .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_topLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(connectedUserName_label, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_topLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_topLayout.createSequentialGroup()
                        .addComponent(requestedPallet_label, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(panel_topLayout.createSequentialGroup()
                        .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(img_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(requestedPallet_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(refresh, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(harnessTypeFilterBox, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scan_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(harnessTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panel_bottom.setBackground(new java.awt.Color(51, 51, 51));
        panel_bottom.setAutoscrolls(true);

        javax.swing.GroupLayout panel_bottomLayout = new javax.swing.GroupLayout(panel_bottom);
        panel_bottom.setLayout(panel_bottomLayout);
        panel_bottomLayout.setHorizontalGroup(
            panel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1362, Short.MAX_VALUE)
        );
        panel_bottomLayout.setVerticalGroup(
            panel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        menu02_process.setText("Process");
        menu02_process.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu02_processActionPerformed(evt);
            }
        });

        menu020_waiting.setBackground(java.awt.Color.cyan);
        menu020_waiting.setText("Continue \"Waiting\"");
        menu020_waiting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu020_waitingActionPerformed(evt);
            }
        });
        menu02_process.add(menu020_waiting);

        menu_bar.add(menu02_process);

        setJMenuBar(menu_bar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_top, javax.swing.GroupLayout.DEFAULT_SIZE, 1362, Short.MAX_VALUE)
            .addComponent(panel_bottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_top, javax.swing.GroupLayout.PREFERRED_SIZE, 931, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_bottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //########################################################################
    //########################## USER LABEL METHODS ##########################
    //########################################################################
    public void setUserLabelText(String newText) {
        connectedUserName_label.setText(newText);
    }

    //########################################################################
    //####################### PRODUCT_FAMILY BOX FILTER METHODS #####################
    //########################################################################
    public void HarnessTypeFilterSetSelectedItem(String value) {
        harnessTypeFilterBox.setSelectedItem(value);
    }

    //########################################################################
    //########################## PRODUCT_FAMILY BOX METHODS #########################
    //########################################################################
    public void setHarnessTypeBoxState(boolean state) {
        harnessTypeBox.setEnabled(state);
    }

    public JComboBox getHarnessTypeBox() {
        return harnessTypeBox;
    }

    public void setHarnessTypeFilterBoxState(boolean state) {
        this.harnessTypeFilterBox.setEnabled(state);
    }

    public void setHarnessTypeBox(JComboBox projectBox) {
        this.harnessTypeBox = projectBox;
    }

    public void HarnessTypeBoxSelectIndex(int index) {
        this.harnessTypeBox.setSelectedIndex(index);
    }

    public void clearContextSessionVals() {
        //Pas besoin de réinitialiser le uid
        PackagingVars.mode2_context.setBaseContainerTmp(new BaseContainerTmp());
        PackagingVars.mode2_context.setBaseHarnessAdditionalBarecodeTmp(new BaseHarnessAdditionalBarecodeTmp());
        PackagingVars.context.setUser(null);
    }

    //########################################################################
    //########################## SCAN BOX METHODS ############################
    //########################################################################
    public void clearScanBox() {
        //Vider le champs de text scan
        scan_txtbox.setText("");
        scan_txtbox.requestFocusInWindow();
    }

    //########################################################################
    //################ Reset GUI Component to Mode2_State S01 ######################
    //########################################################################
    public void logout() {
        try {
            //Save authentication line in HisLogin table
            if (PackagingVars.context.getUser().getId() != null) {
                HisLogin his_login = new HisLogin(
                        PackagingVars.context.getUser().getId(), PackagingVars.context.getUser().getId(),
                        String.format(Helper.INFO0012_LOGOUT_SUCCESS,
                                PackagingVars.context.getUser().getFirstName()
                                + " " + PackagingVars.context.getUser().getLastName()
                                + " / " + PackagingVars.context.getUser().getLogin(),
                                GlobalVars.APP_HOSTNAME, GlobalMethods.getStrTimeStamp()));
                his_login.setCreateId(PackagingVars.context.getUser().getId());
                his_login.setWriteId(PackagingVars.context.getUser().getId());

                str = String.format(Helper.INFO0012_LOGOUT_SUCCESS,
                        PackagingVars.context.getUser().getFirstName() + " " + PackagingVars.context.getUser().getLastName()
                        + " / " + PackagingVars.context.getUser().getLogin(), GlobalVars.APP_HOSTNAME,
                        GlobalMethods.getStrTimeStamp() + " Project : "
                        + PackagingVars.context.getUser().getHarnessType());
                his_login.setMessage(str);

                str = "";
                his_login.create(his_login);
            }
            //Reset the state
            state = new Mode2_S010_UserCodeScan();

            this.clearContextSessionVals();

            //Reset Image
            PackagingVars.Packaging_Gui_Mode2.img_lbl.setIcon(state.getImg());
            //Clear Scan Box
            PackagingVars.Packaging_Gui_Mode2.requestedPallet_label.setText("");
            //Enable Project Box
            PackagingVars.Packaging_Gui_Mode2.HarnessTypeBoxSelectIndex(0);
            PackagingVars.Packaging_Gui_Mode2.setHarnessTypeBoxState(true);
            PackagingVars.Packaging_Gui_Mode2.setHarnessTypeFilterBoxState(true);
            disableAdminMenus();
            disableOperatorMenus();

            connectedUserName_label.setText("");
        } catch (NullPointerException ex) {
            dispose();
            dispose();
        }
    }

    public JTextArea getFeedbackTextarea() {
        return feedbackTextarea;
    }

    public void setFeedbackTextarea(JTextArea assistanceTextarea) {
        this.feedbackTextarea = assistanceTextarea;
    }

    public void setFeedbackTextarea(String text) {
        this.feedbackTextarea.setText(text);
    }

    public void reloadDataTable() {

        Helper.startSession();

        List<Object> states = new ArrayList<Object>();
        List<Object> projects = new ArrayList<Object>();

        Query query = Helper.sess.createQuery(HQLHelper.GET_CONTAINER_BY_STATES_AND_PROJECTS);
        query.setFirstResult(0);
        query.setMaxResults(100);

        states.add(GlobalVars.PALLET_OPEN);
        states.add(GlobalVars.PALLET_WAITING);

        projects.add(String.valueOf(harnessTypeFilterBox.getSelectedItem()));

        query.setParameterList("states", states)
                .setParameterList("projects", projects);

        Helper.sess.getTransaction().commit();
        if (query.list().isEmpty()) {
            this.reset_table_content();
            this.load_table_header();
        } else {
            this.reload_container_table_data(query.list());
        }
    }
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        /*
         if (PackagingVars.context.getUser() != null) {
         this.logout();
         }*/

        GlobalVars.OPENED_SCAN_WINDOW = 0;
        System.out.println("MainFrame.OPENED_SCAN_WINDOW " + GlobalVars.OPENED_SCAN_WINDOW);
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
        reloadDataTable();
    }//GEN-LAST:event_refreshActionPerformed

    private void scan_txtboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scan_txtboxKeyPressed
        // User has pressed Carriage return button
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            state.doAction(PackagingVars.mode2_context);
            state = PackagingVars.mode2_context.getState();
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            int confirmed = JOptionPane.showConfirmDialog(null,
                    "Voulez-vous fermer la session ?", "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirmed == 0) {
                logout();
            }

        }
    }//GEN-LAST:event_scan_txtboxKeyPressed

    private void harnessTypeFilterBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_harnessTypeFilterBoxItemStateChanged
        //
    }//GEN-LAST:event_harnessTypeFilterBoxItemStateChanged

    private void harnessTypeFilterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_harnessTypeFilterBoxActionPerformed

        for (int i = 0; i < harnessTypeBox.getItemCount(); i++) {
            if (String.valueOf(harnessTypeFilterBox.getSelectedItem()).equals(String.valueOf(harnessTypeBox.getItemAt(i)))) {
                this.harnessTypeBox.setSelectedIndex(i);
                break;
            }
        }
        reloadDataTable();
    }//GEN-LAST:event_harnessTypeFilterBoxActionPerformed

    private void harnessTypeBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_harnessTypeBoxItemStateChanged

    }//GEN-LAST:event_harnessTypeBoxItemStateChanged

    private void harnessTypeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_harnessTypeBoxActionPerformed

        for (int i = 0; i < harnessTypeFilterBox.getItemCount(); i++) {
            if (String.valueOf(harnessTypeBox.getSelectedItem()).equals(String.valueOf(harnessTypeFilterBox.getItemAt(i)))) {
                this.harnessTypeFilterBox.setSelectedIndex(i);
                break;
            }
        }
        reloadDataTable();
    }//GEN-LAST:event_harnessTypeBoxActionPerformed

    private void menu02_processActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu02_processActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_menu02_processActionPerformed

    private void menu020_waitingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu020_waitingActionPerformed
        //new PACKAGING_UI0013_PalletWaiting(this, true);
        UILog.infoDialog("Fonction invalid!");
    }//GEN-LAST:event_menu020_waitingActionPerformed

    private void scan_txtboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scan_txtboxFocusGained
        scan_txtbox.setBackground(Color.GREEN);
    }//GEN-LAST:event_scan_txtboxFocusGained

    private void scan_txtboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scan_txtboxFocusLost
        scan_txtbox.setBackground(Color.WHITE);
    }//GEN-LAST:event_scan_txtboxFocusLost

    public void setIconLabel(ImageIcon icon) {
        this.img_lbl.setIcon(icon);
    }

    public JTextField getScanTxt() {
        return this.scan_txtbox;
    }

    public void setScanTxt(JTextField setScanTxt) {
        this.scan_txtbox = setScanTxt;
    }

    public JLabel getRequestedPallet_label() {
        return requestedPallet_label;
    }

    public void setRequestedPallet_label(JLabel requestedPallet_label) {
        this.requestedPallet_label = requestedPallet_label;
    }

    public void setRequestedPallet_txt(String text) {
        this.requestedPallet_label.setText(text);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel connectedUserName_label;
    private javax.swing.JTable container_table;
    private javax.swing.JTextArea feedbackTextarea;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JComboBox harnessTypeBox;
    private javax.swing.JComboBox harnessTypeFilterBox;
    private javax.swing.JLabel img_lbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem menu020_waiting;
    private javax.swing.JMenu menu02_process;
    private javax.swing.JMenuBar menu_bar;
    private javax.swing.JPanel panel_bottom;
    private javax.swing.JPanel panel_top;
    private javax.swing.JButton refresh;
    private javax.swing.JLabel requestedPallet_label;
    private javax.swing.JLabel requestedPallet_label1;
    private javax.swing.JTextField scan_txtbox;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("e.getKeyCode()" + e.getKeyCode());
        System.out.println("keyTyped.VK_HOME" + KeyEvent.VK_HOME);
        if (e.getKeyCode() == KeyEvent.VK_HOME) {
            scan_txtbox.requestFocus();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("e.getKeyCode()" + e.getKeyCode());
        System.out.println("keyPressed.VK_HOME" + KeyEvent.VK_HOME);
        if (e.getKeyCode() == KeyEvent.VK_HOME) {
            scan_txtbox.requestFocus();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("e.getKeyCode()" + e.getKeyCode());
        System.out.println("keyReleased.VK_HOME" + KeyEvent.VK_HOME);
        if (e.getKeyCode() == KeyEvent.VK_HOME) {
            scan_txtbox.requestFocus();
        }

    }

    @Override
    public void windowOpened(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowClosing(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowClosed(WindowEvent e) {
        GlobalVars.OPENED_SCAN_WINDOW = 0;
        System.out.println("OPENED_SCAN_WINDOW " + GlobalVars.OPENED_SCAN_WINDOW);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowActivated(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
