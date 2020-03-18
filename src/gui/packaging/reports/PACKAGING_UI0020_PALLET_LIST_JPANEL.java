/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging.reports;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import helper.Helper;
import entity.BaseContainer;
import entity.ConfigProject;
import entity.ConfigSegment;
import entity.ConfigWorkplace;
import gui.packaging.PackagingVars;
import gui.warehouse_fg_reception.WAREHOUSE_FG_UI0001_SCAN_JPANEL;
import helper.JDialogExcelFileChooser;
import helper.JTableHelper;
import helper.UIHelper;
import java.awt.Component;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import ui.UILog;
import ui.error.ErrorMsg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author user
 */
public final class PACKAGING_UI0020_PALLET_LIST_JPANEL extends javax.swing.JPanel {

    JTabbedPane parent;
    Vector<String> searchResult_table_header = new Vector<String>();
    List<String> table_header = Arrays.asList(
            "Segment", //0
            "Workplace", //1
            "Pack Number", //2            
            "CPN", //3
            "Fifo Time", //4
            "SPN", //5
            "Pack Type", //6
            "Pack Size", //7
            "Qty Read", //8
            "Std Time", //9
            "Total Std Time", //10
            "Actual State", //11
            "Actual State code", //12
            "Dispatch Time", //13            
            "Dispatch Label No", //14
            "Truck No" //15
    );

    SimpleDateFormat timeDf = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateDf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateTimeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String startTimeStr = "";
    String endTimeStr = "";
    String startDateStr = null;
    String endDateStr = null;

    List<Object> segments = new ArrayList<Object>();
    List<Object> workplaces = new ArrayList<Object>();
    List<Object> projects = new ArrayList<Object>();

    List<Object[]> resultList;

    Vector searchResult_table_data = new Vector();
    private BaseContainer bc = new BaseContainer();
    /* "Pallet number" Column index in "container_table" */
    private static int PALLET_NUMBER_COLINDEX = 2;

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     */
    public PACKAGING_UI0020_PALLET_LIST_JPANEL(JTabbedPane parent, boolean modal) {
        this.parent = parent;
        initComponents();
        initGui();
    }

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     * @param PalletNumber : Requested container number to be displayed
     */
    public PACKAGING_UI0020_PALLET_LIST_JPANEL(JTabbedPane parent, boolean modal, String PalletNumber) {
        this.parent = parent;
        initComponents();
        initGui();

    }

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     * @param PalletNumber : Requested container number to be displayed
     * @param drop : Show drop button in the form
     * @param printOpenSheet : Show print open sheet button in the form
     * @param printCloseSheet : Show print close sheet button in the form
     *
     */
    public PACKAGING_UI0020_PALLET_LIST_JPANEL(JTabbedPane parent, boolean modal,
            String PalletNumber, boolean drop, boolean printOpenSheet,
            boolean printCloseSheet) {
        this.parent = parent;
        initComponents();
        initGui();

    }

    private void initContainerTableDoubleClick() {
        this.searchResult_table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    try {
                        if (PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN || PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT) {
                            GlobalMethods.addNewTabToParent("Détails palette", parent, new PACKAGING_UI0010_PalletDetails_JPANEL(parent, String.valueOf(searchResult_table.getValueAt(searchResult_table.getSelectedRow(), PALLET_NUMBER_COLINDEX)), "", 1, true, true, true), evt);
                        } else {
                            GlobalMethods.addNewTabToParent("Détails palette", parent, new PACKAGING_UI0010_PalletDetails_JPANEL(parent, String.valueOf(searchResult_table.getValueAt(searchResult_table.getSelectedRow(), PALLET_NUMBER_COLINDEX)), "", 1, false, false, false), evt);
                        }
                    } catch (NullPointerException ex) {
                        GlobalMethods.addNewTabToParent("Détails palette", parent, new PACKAGING_UI0010_PalletDetails_JPANEL(parent, String.valueOf(searchResult_table.getValueAt(searchResult_table.getSelectedRow(), PALLET_NUMBER_COLINDEX)), "", 1, false, false, false), evt);
                    }
                }
            }
        }
        );
    }

    private void initStateFilters() {
        
        for (int i = 0; i < GlobalVars.PALLET_STATES.length; i++) {
            status_filter.addItem(GlobalVars.PALLET_STATES[i][0]);
        }
    }

    private void initGui() {
        //Center the this dialog in the screen
        JTableHelper.sizeColumnsToFit(searchResult_table);

        //Load table header
        reset_table_content();
        load_table_header(table_header);

        //Init time spinner
        initTimeSpinners();

        project_filter = ConfigProject.initProjectsJBox(this, project_filter, true);

        initStateFilters();

        this.workplace_filter.setEnabled(false);

        //Support double click on rows in container jtable to display history
        this.initContainerTableDoubleClick();

    }

    private void load_table_header(List<String> table_header) {
        this.reset_table_content();

        for (Iterator<String> it = table_header.iterator(); it.hasNext();) {
            searchResult_table_header.add(it.next());
        }

        searchResult_table.setModel(new DefaultTableModel(searchResult_table_data, searchResult_table_header));
    }

    private void reset_table_content() {

        searchResult_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(searchResult_table_data, searchResult_table_header);
        searchResult_table.setModel(dataModel);
    }

    public BaseContainer getBaseContainer() {
        return bc;
    }

    public void setBaseContainer(BaseContainer bc) {
        this.bc = bc;
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
        jScrollPane1 = new javax.swing.JScrollPane();
        searchResult_table = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        startDatePicker = new org.jdesktop.swingx.JXDatePicker();
        startTimeSpinner = new javax.swing.JSpinner();
        segment_filter = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        workplace_filter = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        endDatePicker = new org.jdesktop.swingx.JXDatePicker();
        endTimeSpinner = new javax.swing.JSpinner();
        filter_harness_part_txt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        refresh_btn = new javax.swing.JButton();
        lafm_btn = new javax.swing.JButton();
        export_btn = new javax.swing.JButton();
        project_filter = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        filter_spn_txt = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        filter_packtype_txt = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        status_filter = new javax.swing.JComboBox();
        jLabel24 = new javax.swing.JLabel();
        total_declared_lbl = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        nbreLigne = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        total_hours_lbl = new javax.swing.JTextField();

        setBackground(new java.awt.Color(36, 65, 86));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(36, 65, 86));

        searchResult_table.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        searchResult_table.setModel(new javax.swing.table.DefaultTableModel(
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
        searchResult_table.setCellSelectionEnabled(true);
        jScrollPane1.setViewportView(searchResult_table);

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("INFO : Double clique sur une ligne pour consulter l'historique.");

        jPanel2.setBackground(new java.awt.Color(36, 65, 86));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("De");

        startDatePicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startDatePickerActionPerformed(evt);
            }
        });
        startDatePicker.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                startDatePickerKeyPressed(evt);
            }
        });

        startTimeSpinner.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        startTimeSpinner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                startTimeSpinnerKeyPressed(evt);
            }
        });

        segment_filter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL" }));
        segment_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                segment_filterItemStateChanged(evt);
            }
        });
        segment_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segment_filterActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Segment");

        workplace_filter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL" }));
        workplace_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                workplace_filterItemStateChanged(evt);
            }
        });
        workplace_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workplace_filterActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Workplace");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("A");

        endDatePicker.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                endDatePickerKeyPressed(evt);
            }
        });

        endTimeSpinner.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        endTimeSpinner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                endTimeSpinnerKeyPressed(evt);
            }
        });

        filter_harness_part_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter_harness_part_txtActionPerformed(evt);
            }
        });
        filter_harness_part_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filter_harness_part_txtKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Référence Client");

        refresh_btn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        refresh_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/refresh.png"))); // NOI18N
        refresh_btn.setText("Actualiser");
        refresh_btn.setToolTipText("Filter");
        refresh_btn.setBorderPainted(false);
        refresh_btn.setMaximumSize(new java.awt.Dimension(24, 24));
        refresh_btn.setMinimumSize(new java.awt.Dimension(24, 24));
        refresh_btn.setOpaque(false);
        refresh_btn.setPreferredSize(new java.awt.Dimension(24, 24));
        refresh_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_btnActionPerformed(evt);
            }
        });

        lafm_btn.setText("Exporter Données LAFM...");
        lafm_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lafm_btnActionPerformed(evt);
            }
        });

        export_btn.setText("Exporter en Excel...");
        export_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                export_btnActionPerformed(evt);
            }
        });

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

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Projet");

        filter_spn_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter_spn_txtActionPerformed(evt);
            }
        });
        filter_spn_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filter_spn_txtKeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Référence Interne");

        filter_packtype_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter_packtype_txtActionPerformed(evt);
            }
        });
        filter_packtype_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filter_packtype_txtKeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Pack type");

        status_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                status_filterItemStateChanged(evt);
            }
        });
        status_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                status_filterActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Statut");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(endDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(endTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(startTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(status_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21)
                                    .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20)
                                    .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(export_btn)
                                .addGap(18, 18, 18)
                                .addComponent(lafm_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filter_harness_part_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filter_spn_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filter_packtype_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(102, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(status_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel6)
                            .addComponent(jLabel22)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filter_harness_part_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filter_spn_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filter_packtype_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel8)))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lafm_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(export_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        total_declared_lbl.setEditable(false);
        total_declared_lbl.setBackground(new java.awt.Color(153, 255, 255));
        total_declared_lbl.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        total_declared_lbl.setText("0");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Σ Quantités");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Σ Packages");

        nbreLigne.setEditable(false);
        nbreLigne.setBackground(new java.awt.Color(153, 255, 255));
        nbreLigne.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        nbreLigne.setText("0");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Σ Heures");

        total_hours_lbl.setEditable(false);
        total_hours_lbl.setBackground(new java.awt.Color(153, 255, 255));
        total_hours_lbl.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        total_hours_lbl.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(jLabel18)
                .addGap(10, 10, 10)
                .addComponent(total_declared_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jLabel19)
                .addGap(10, 10, 10)
                .addComponent(nbreLigne, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jLabel23)
                .addGap(10, 10, 10)
                .addComponent(total_hours_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel23)
                        .addComponent(total_hours_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel18)
                        .addComponent(total_declared_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19)
                        .addComponent(nbreLigne, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    public void clearSearchBox() {
        //Vider le champs de text scan

    }

    public void reset_container_table_content() {
        searchResult_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(searchResult_table_data, searchResult_table_header);
        searchResult_table.setModel(dataModel);
    }

    public void reload_container_table_data(List<Object[]> resultList) {
        this.reset_container_table_content();
        double total_qty = 0.00;
        double total_hours = 0.00;
        final DecimalFormat decimalFormat = new DecimalFormat("00.00");
        for (Object[] obj : resultList) {
            @SuppressWarnings("UseOfObsoleteCollectionType")
            Vector<Object> oneRow = new Vector<Object>();

            oneRow.add(String.valueOf(obj[0])); // "Segment",
            oneRow.add(String.valueOf(obj[1])); // "Workplace",
            oneRow.add(String.valueOf(obj[2])); // "Pack Number",
            oneRow.add(String.valueOf(obj[3])); // "Harness Part",
            oneRow.add(String.valueOf(obj[4])); // "Update Time",
            oneRow.add(String.valueOf(obj[5])); // "Supplier Part Number",
            oneRow.add(String.valueOf(obj[6])); // "Pack Type",
            oneRow.add(decimalFormat.format(Double.parseDouble(obj[7].toString()))); // "Pack Size",
            oneRow.add(decimalFormat.format(Double.parseDouble(obj[8].toString()))); // "Qty Read",
            oneRow.add(decimalFormat.format(Double.parseDouble(obj[9].toString()))); // "Std Time",            
            oneRow.add(decimalFormat.format(Double.parseDouble(obj[10].toString()))); // "Total Std Time",
            oneRow.add(String.valueOf(obj[11])); // "State",
            oneRow.add(String.valueOf(obj[12])); // "State code"
            oneRow.add(String.valueOf(obj[13])); // Dispatch Label No
            oneRow.add(String.valueOf(obj[14])); // Truck No
            oneRow.add(String.valueOf(obj[15])); // Dispatch Date

            total_qty = total_qty + Double.valueOf(obj[8].toString());
            total_hours = total_hours + Double.valueOf(obj[10].toString());

            searchResult_table_data.add(oneRow);
        }
        total_declared_lbl.setText(decimalFormat.format(total_qty));
        total_hours_lbl.setText(decimalFormat.format(total_hours));
        searchResult_table.setModel(new DefaultTableModel(searchResult_table_data, searchResult_table_header));
        searchResult_table.setAutoCreateRowSorter(true);

        UIHelper.disableEditingJtable(searchResult_table);
        JTableHelper.sizeColumnsToFit(searchResult_table);
    }

    private void initTimeSpinners() {

        String startTime = GlobalVars.APP_PROP.getProperty("START_TIME");
        String endTime = GlobalVars.APP_PROP.getProperty("END_TIME");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        //################# Start Time Spinner ####################
        startTimeSpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
        try {
            startTimeSpinner.setValue(timeFormat.parse(startTime));
        } catch (ParseException ex) {
            Logger.getLogger(PACKAGING_UI0020_PALLET_LIST_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
        }

        //################# End Time Spinner ######################
        endTimeSpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        try {
            endTimeSpinner.setValue(timeFormat.parse(endTime));
        } catch (ParseException ex) {
            Logger.getLogger(PACKAGING_UI0020_PALLET_LIST_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
        }

        startDatePicker.setDate(new Date());
        endDatePicker.setDate(new Date());

    }

    private boolean checkValidFields() {
        if (startTimeSpinner.getValue() != ""
                && endTimeSpinner.getValue() != "") {
            return true;
        } else {
            return false;
        }
    }

    private void refresh() {

        if (checkValidFields()) {
            segments.clear();
            workplaces.clear();
            projects.clear();

            startTimeStr = timeDf.format(startTimeSpinner.getValue());
            endTimeStr = timeDf.format(endTimeSpinner.getValue());
            try {
                startDateStr = dateDf.format(startDatePicker.getDate()) + " " + startTimeStr;
                endDateStr = dateDf.format(endDatePicker.getDate()) + " " + endTimeStr;
            } catch (NullPointerException e) {
                startDateStr = null;
                endDateStr = null;
            }

            //Populate the project Array with data
            if (String.valueOf(project_filter.getSelectedItem()).equals("ALL") || String.valueOf(project_filter.getSelectedItem()).equals("null")) {
                List result = new ConfigProject().select();
                if (result.isEmpty()) {
                    UILog.severeDialog(this, ErrorMsg.APP_ERR0035);
                    UILog.severe(ErrorMsg.APP_ERR0035[1]);
                } else { //Map project data in the list
                    for (Object o : result) {
                        ConfigProject cp = (ConfigProject) o;
                        projects.add(cp.getProject());
                    }
                }
            } else {
                projects.add(String.valueOf(project_filter.getSelectedItem()).trim());
            }

            //Populate the segments Array with data
            if (String.valueOf(segment_filter.getSelectedItem()).equals("ALL") || String.valueOf(segment_filter.getSelectedItem()).equals("null")) {
                List result = new ConfigSegment().select();
                if (result.isEmpty()) {
                    JOptionPane.showMessageDialog(null, Helper.ERR0026_NO_SEGMENT_FOUND, "Configuration error !", ERROR_MESSAGE);
                    System.err.println(Helper.ERR0026_NO_SEGMENT_FOUND);
                } else { //Map project data in the list
                    for (Object o : result) {
                        ConfigSegment cs = (ConfigSegment) o;
                        segments.add(String.valueOf(cs.getSegment()));
                    }
                }
            } else {
                segments.add(String.valueOf(segment_filter.getSelectedItem()));
                //Populate the workplaces Array with data
                if (String.valueOf(workplace_filter.getSelectedItem()).equals("ALL")) {
                    List result = new ConfigWorkplace().selectBySegment(String.valueOf(segment_filter.getSelectedItem()));
                    if (result.isEmpty()) {
                        JOptionPane.showMessageDialog(null, Helper.ERR0027_NO_WORKPLACE_FOUND, "Configuration error !", ERROR_MESSAGE);
                        System.err.println(Helper.ERR0027_NO_WORKPLACE_FOUND);
                    } else { //Map project data in the list
                        for (Object o : result) {
                            ConfigWorkplace cw = (ConfigWorkplace) o;
                            workplaces.add(String.valueOf(cw.getWorkplace()));
                        }
                    }
                } else {
                    workplaces.add(String.valueOf(workplace_filter.getSelectedItem()));
                }
            }

            String state;
            String fifo_time = " bc.write_time AS fifo_time,";
            this.searchResult_table_header.set(4, "FIFO TIME"); // Modify the header of fifo time acording to choosen status

            // List<String> selected = UIHelper.manageCheckedRadioButtons(jpanel_state);
            //  for (int i = 0; i < selected.size(); i++) {
            if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_OPEN)) {
                state = GlobalVars.PALLET_OPEN;
                fifo_time = " bc.create_time AS fifo_time, ";
                this.searchResult_table_header.set(4, "OPEN TIME"); // Modify the header of fifo time acording to choosen status
            }
            if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_WAITING)) {
                state = GlobalVars.PALLET_WAITING;
                fifo_time = " bc.complete_time AS fifo_time, ";
                this.searchResult_table_header.set(4, "COMPLETE TIME"); // Modify the header of fifo time acording to choosen status
            }
            if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_CLOSED)) {
                state = GlobalVars.PALLET_CLOSED;
                fifo_time = " bc.closed_time AS fifo_time, ";
                this.searchResult_table_header.set(4, "CLOSED TIME"); // Modify the header of fifo time acording to choosen status
            }
            if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_STORED)) {
                state = GlobalVars.PALLET_STORED;
                fifo_time = " bc.stored_time AS fifo_time, ";
                this.searchResult_table_header.set(4, "STORED TIME"); // Modify the header of fifo time acording to choosen status
            }
            if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_RESERVED)) {
                state = GlobalVars.PALLET_RESERVED;
                fifo_time = " bc.reserved_time AS fifo_time, ";
                this.searchResult_table_header.set(4, "RESERVED TIME"); // Modify the header of fifo time acording to choosen status
            }
            if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_DISPATCHED)) {
                state = GlobalVars.PALLET_DISPATCHED;
                fifo_time = " bc.dispatch_time AS fifo_time, ";
                this.searchResult_table_header.set(4, "DISPATCH TIME"); // Modify the header of fifo time acording to choosen status
            }
            if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_BLOCKED)) {
                state = GlobalVars.PALLET_BLOCKED;
                fifo_time = " bc.write_time AS fifo_time, ";
                this.searchResult_table_header.set(4, "BLOCK. TIME"); // Modify the header of fifo time acording to choosen status
            }
            if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_DELETED)) {
                state = GlobalVars.PALLET_DELETED;
                fifo_time = " bc.dispatch_time AS fifo_time, ";
                this.searchResult_table_header.set(4, "OPEN TIME"); // Modify the header of fifo time acording to choosen status
            }

            // }
            //################# Query ####################            
            Helper.startSession();
            try {
                String query_str = " SELECT bc.segment AS segment, "
                        + " bc.workplace AS workplace, "
                        + " bc.pallet_number AS pack_number, "
                        + " bc.harness_part AS harness_part, "
                        + fifo_time
                        + " bc.supplier_part_number AS supplier_part_number, "
                        + " bc.pack_type AS pack_type, "
                        + " bc.qty_expected AS pack_size, "
                        + " bc.qty_read AS qty_read, "
                        + " bc.std_time AS std_time, "
                        + " bc.std_time*bc.qty_read AS total_std_time, "
                        + " bc.container_state AS state, "
                        + " bc.container_state_code AS state_code, "
                        + " bc.dispatch_time AS dispatch_time, "
                        + " bc.dispatch_label_no AS dispatch_label_no, "
                        + " bc.truck_no AS truck_no "
                        + " FROM base_container bc "
                        + " WHERE 1=1 ";

                if (!projects.isEmpty()) {
                    query_str += " AND bc.project IN (:projects) ";
                    System.out.println("projects " + projects.size());
                }
                if (!segments.isEmpty()) {
                    query_str += " AND bc.segment IN (:segments) ";
                }
                if (!workplaces.isEmpty()) {
                    query_str += " AND bc.workplace IN (:workplaces) ";
                }

                //Add harness part filter                
                if (!filter_harness_part_txt.getText().isEmpty()) {
                    query_str += " AND harness_part LIKE :hp";
                }

                //Add supplier part filter                
                if (!filter_spn_txt.getText().isEmpty()) {
                    query_str += " AND supplier_part_number LIKE :spn";
                }

                //Add pack type filter                
                if (!filter_packtype_txt.getText().isEmpty()) {
                    query_str += " AND pack_type LIKE :packType";
                }

                if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_OPEN)) {
                    //Open : open pallets w/o considering time.
                    if (startDatePicker.getDate() == null && endDatePicker.getDate() == null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_OPEN + "' ORDER BY bc.create_time DESC";
                    } //Open : open pallets between 2 dates
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() != null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_OPEN + "' AND bc.create_time BETWEEN '%s' AND '%s' ORDER BY bc.create_time DESC";
                        query_str = String.format(query_str, startDateStr, endDateStr);
                    } //Open : open pallets starting from start date
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() == null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_OPEN + "' AND bc.create_time > '%s' ORDER BY bc.create_time DESC ";
                        query_str = String.format(query_str, startDateStr);
                    } //Open : open pallets until end date
                    else if (startDatePicker.getDate() == null && endDatePicker.getDate() != null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_OPEN + "' AND bc.create_time < '%s' ORDER BY bc.create_time DESC";
                        query_str = String.format(query_str, endDateStr);
                    }
                } else if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_WAITING)) {
                    //Waiting : waiting pallets w/o considering the time
                    if (startDatePicker.getDate() == null && endDatePicker.getDate() == null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_WAITING + "' ORDER BY bc.write_time DESC";
                    } //Waiting : waiting pallets between start date and end date
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() != null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_WAITING + "' AND bc.write_time BETWEEN '%s' AND '%s' ORDER BY bc.write_time DESC";
                        query_str = String.format(query_str, startDateStr, endDateStr);
                    } //Waiting : waiting pallets starting from start date.
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() == null) {
                        query_str += " AND bc.write_time >= '%s' ORDER BY bc.write_time DESC ";
                        query_str = String.format(query_str, startDateStr);
                    }
                } else if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_CLOSED)) {
                    //Closed : closed pallets wihout considering the time.
                    if (startDatePicker.getDate() == null && endDatePicker.getDate() == null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_CLOSED + "' ORDER BY bc.closed_time DESC";
                    } //Closed : closed pallets between start date and end date.
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() != null) {
                        query_str += " AND bc.closed_time BETWEEN '%s' AND '%s' ORDER BY bc.closed_time DESC";
                        query_str = String.format(query_str, startDateStr, endDateStr);
                    } //Closed : open pallets starting from start date
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() == null) {
                        query_str += " AND bc.closed_time >= '%s' ORDER BY bc.closed_time DESC ";
                        query_str = String.format(query_str, startDateStr);
                    } //Closed : open pallets until end date
                    else if (startDatePicker.getDate() == null && endDatePicker.getDate() != null) {
                        query_str += " AND bc.closed_time <= '%s' ORDER BY bc.closed_time DESC";
                        query_str = String.format(query_str, endDateStr);
                    }
                } else if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_STORED)) {
                    //Stored : stored pallets wihout considering the time.
                    if (startDatePicker.getDate() == null && endDatePicker.getDate() == null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_STORED + "' ORDER BY bc.stored_time DESC";
                    } //Stored : stored pallets between start date and end date.
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() != null) {
                        query_str += " AND bc.stored_time BETWEEN '%s' AND '%s' ORDER BY bc.stored_time DESC";
                        query_str = String.format(query_str, startDateStr, endDateStr);
                    } //Stored : stored pallets starting from start date.
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() == null) {
                        query_str += " AND bc.stored_time >= '%s' ORDER BY bc.stored_time DESC ";
                        query_str = String.format(query_str, startDateStr);
                    } //Stored : stored pallets until end date.
                    else if (startDatePicker.getDate() == null && endDatePicker.getDate() != null) {
                        query_str += " AND bc.stored_time <= '%s' ORDER BY bc.stored_time DESC";
                        query_str = String.format(query_str, endDateStr);
                    }
                } else if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_RESERVED)) {
                    //Reserved : reserved pallets wihout considering the time.
                    if (startDatePicker.getDate() == null && endDatePicker.getDate() == null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_RESERVED + "' ORDER BY bc.reserved_time DESC";
                    } //Reserved : reserved pallets at the given time.
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() != null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_RESERVED + "' AND bc.reserved_time BETWEEN '%s' AND '%s' ORDER BY bc.reserved_time DESC";
                        query_str = String.format(query_str, startDateStr, endDateStr);
                    } //Reserved : reserved pallets starting from start date
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() == null) {
                        query_str += " AND bc.reserved_time >= '%s' ORDER BY bc.reserved_time DESC ";
                        query_str = String.format(query_str, startDateStr);
                    } //Reserved : reserved pallets until end date
                    else if (startDatePicker.getDate() == null && endDatePicker.getDate() != null) {
                        query_str += " AND bc.reserved_time <= '%s' ORDER BY bc.reserved_time DESC";
                        query_str = String.format(query_str, endDateStr);
                    }
                } else if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_DISPATCHED)) {
                    //Dispatched : dispatched pallets wihout considering the time.
                    if (startDatePicker.getDate() == null && endDatePicker.getDate() == null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_DISPATCHED + "' AND bc.dispatch_time BETWEEN '%s' AND '%s' ORDER BY bc.dispatch_time DESC";
                        query_str = String.format(query_str, startDateStr, endDateStr);
                    } //Dispatched : dispatched pallets between 2 dates.
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() != null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_DISPATCHED + "' AND bc.dispatch_time BETWEEN '%s' AND '%s' ORDER BY bc.dispatch_time DESC";
                        query_str = String.format(query_str, startDateStr, endDateStr);
                    } //Dispatched : dispatched pallets starting from stard date
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() == null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_DISPATCHED + "' AND bc.dispatch_time >= '%s' ORDER BY bc.dispatch_time DESC ";
                        query_str = String.format(query_str, startDateStr);
                    } //Dispatched : dispatched pallets until end date
                    else if (startDatePicker.getDate() == null && endDatePicker.getDate() != null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_DISPATCHED + "' AND bc.dispatch_time <= '%s' ORDER BY bc.dispatch_time DESC";
                        query_str = String.format(query_str, endDateStr);
                    }
                } else if (status_filter.getSelectedItem().toString().equals(GlobalVars.PALLET_BLOCKED)) {
                    if (startDatePicker.getDate() != null && endDatePicker.getDate() != null) {
                        query_str += " AND container_state ='" + GlobalVars.PALLET_BLOCKED + "' AND bc.write_time BETWEEN '%s' AND '%s' ORDER BY bc.write_time DESC";
                        query_str = String.format(query_str, startDateStr, endDateStr);
                    } else if (startDatePicker.getDate() != null && endDatePicker.getDate() == null) {
                        query_str += " AND bc.write_time > '%s' ORDER BY bc.write_time DESC ";
                        query_str = String.format(query_str, startDateStr);
                    } else if (startDatePicker.getDate() == null && endDatePicker.getDate() != null) {
                        query_str += " AND bc.shipped_time < '%s' ORDER BY bc.write_time DESC";
                        query_str = String.format(query_str, endDateStr);
                    }
                } else {
                    //All pallets status between 2 dates (taking time of update as a criteria)
                    if (startDatePicker.getDate() != null && endDatePicker.getDate() != null) {
                        query_str += " AND bc.write_time BETWEEN '%s' AND '%s'";
                        query_str = String.format(query_str, startDateStr, endDateStr);
                    } //All pallets status starting from start date
                    else if (startDatePicker.getDate() != null && endDatePicker.getDate() == null) {
                        query_str += " AND bc.write_time > '%s'";
                        query_str = String.format(query_str, startDateStr);
                    } //All pallets status until end date
                    else if (startDatePicker.getDate() == null && endDatePicker.getDate() != null) {
                        query_str += " AND bc.write_time < '%s' ";
                        query_str = String.format(query_str, endDateStr);
                    }

                }

                SQLQuery query = Helper.sess.createSQLQuery(query_str);

                query.addScalar("segment", StandardBasicTypes.STRING)
                        .addScalar("workplace", StandardBasicTypes.STRING)
                        .addScalar("pack_number", StandardBasicTypes.STRING)
                        .addScalar("harness_part", StandardBasicTypes.STRING)
                        .addScalar("fifo_time", StandardBasicTypes.TIMESTAMP)
                        .addScalar("supplier_part_number", StandardBasicTypes.STRING)
                        .addScalar("pack_type", StandardBasicTypes.STRING)
                        .addScalar("pack_size", StandardBasicTypes.STRING)
                        .addScalar("qty_read", StandardBasicTypes.STRING)
                        .addScalar("std_time", StandardBasicTypes.DOUBLE)
                        .addScalar("total_std_time", StandardBasicTypes.DOUBLE)
                        .addScalar("state", StandardBasicTypes.STRING)
                        .addScalar("state_code", StandardBasicTypes.STRING)
                        .addScalar("dispatch_time", StandardBasicTypes.TIMESTAMP)
                        .addScalar("dispatch_label_no", StandardBasicTypes.STRING)
                        .addScalar("truck_no", StandardBasicTypes.STRING);

                if (!projects.isEmpty()) {
                    query.setParameterList("projects", projects);
                }
                if (!segments.isEmpty()) {
                    query.setParameterList("segments", segments);
                }
                if (!workplaces.isEmpty()) {
                    query.setParameterList("workplaces", workplaces);
                }

                if (!filter_harness_part_txt.getText().isEmpty()) {
                    query.setParameter("hp", "%" + filter_harness_part_txt.getText() + "%");
                }

                //Add supplier part filter                
                if (!filter_spn_txt.getText().isEmpty()) {
                    query.setParameter("spn", "%" + filter_spn_txt.getText() + "%");
                }

                //Add pack type filter                
                if (!filter_packtype_txt.getText().isEmpty()) {
                    query.setParameter("packType", "%" + filter_packtype_txt.getText() + "%");
                }

                this.resultList = resultList = query.list();
                nbreLigne.setText(resultList.size() + "");

                //this.load_table_header(table_header);
                this.reload_container_table_data(resultList);
                Helper.sess.getTransaction().commit();

                searchResult_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table,
                            Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                        String status = (String) table.getModel().getValueAt(row, 11);
                        //############### CLOSED
                        if (GlobalVars.PALLET_CLOSED.equals(status)) {
                            Color ORANGE = new Color(255, 234, 158);
                            setBackground(ORANGE);
                            setForeground(java.awt.Color.BLACK);
                        } else if (GlobalVars.PALLET_BLOCKED.equals(status)) {
                            Color RED = new Color(255, 194, 194);
                            setBackground(RED);
                            setForeground(java.awt.Color.BLACK);
                        } //############### WAITING
                        else if (GlobalVars.PALLET_WAITING.equals(status)) {
                            setBackground(java.awt.Color.CYAN);
                            setForeground(java.awt.Color.BLACK);
                        } //############### STORED
                        else if (GlobalVars.PALLET_STORED.equals(status)) {
                            Color GREEN = new Color(158, 255, 160);
                            setBackground(GREEN);
                            setForeground(java.awt.Color.BLACK);
                        }//############### RESERVED
                        else if (GlobalVars.PALLET_RESERVED.equals(status)) {
                            Color MAGENTA = new Color(240, 209, 255);
                            setBackground(MAGENTA);
                            setForeground(java.awt.Color.BLACK);
                        } else if (GlobalVars.PALLET_DISPATCHED.equals(status)) {
                            setBackground(java.awt.Color.LIGHT_GRAY);
                            setForeground(java.awt.Color.BLACK);
                        }

                        setHorizontalAlignment(JLabel.CENTER);

                        return this;
                    }
                });

            } catch (HibernateException e) {
                if (Helper.sess.getTransaction() != null) {
                    Helper.sess.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Empty field", "Empty field Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.setVisible(false);
        }
    }//GEN-LAST:event_formKeyPressed

    private void refresh_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_btnActionPerformed
        refresh();

    }//GEN-LAST:event_refresh_btnActionPerformed


    private void filter_harness_part_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filter_harness_part_txtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_filter_harness_part_txtKeyPressed

    private void export_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export_btnActionPerformed
        startTimeStr = timeDf.format(startTimeSpinner.getValue());
        endTimeStr = timeDf.format(endTimeSpinner.getValue());
        startDateStr = dateDf.format(startDatePicker.getDate()) + " " + startTimeStr;
        endDateStr = dateDf.format(endDatePicker.getDate()) + " " + endTimeStr;
        int total_produced = 0;
        //Create the excel workbook
        Workbook wb = new XSSFWorkbook(); // new HSSFWorkbook();
        Sheet sheet = wb.createSheet("PALLET_LIST");
        CreationHelper createHelper = wb.getCreationHelper();

        //######################################################################
        //##################### SHEET 1 : PILES DETAILS ########################
        //Initialiser les entête du fichier
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow((short) 0);

        row.createCell(0).setCellValue("SEGMENT");
        row.createCell(1).setCellValue("WORKPLACE");
        row.createCell(2).setCellValue("PAL NUM");
        row.createCell(3).setCellValue("PART NUMBER");
        row.createCell(4).setCellValue("FIFO TIME");
        row.createCell(5).setCellValue("LEONI PART NUM");
        row.createCell(6).setCellValue("PACK TYPE");
        row.createCell(7).setCellValue("PACK SIZE");
        row.createCell(8).setCellValue("QTY READ");
        row.createCell(9).setCellValue("STD TIME");
        row.createCell(10).setCellValue("TOTAL STD TIME");
        row.createCell(11).setCellValue("STATE");
        row.createCell(12).setCellValue("STATE CODE");
        row.createCell(13).setCellValue("DISPATCH TIME");
        row.createCell(14).setCellValue("DISPATCH LABEL NO");
        row.createCell(15).setCellValue("TRUCK NO");

        short sheetPointer = 1;
        CellStyle numericFormat = wb.createCellStyle();
        numericFormat.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        for (Object[] obj : this.resultList) {
            row = sheet.createRow(sheetPointer);
            row.createCell(0).setCellValue(String.valueOf(obj[0])); //SEGMENT
            row.createCell(1).setCellValue(String.valueOf(obj[1])); //WORKPLACE
            row.createCell(2).setCellValue(Integer.valueOf(obj[2].toString())); //PAL NUM
            row.createCell(3).setCellValue(String.valueOf(obj[3]));//PART NUMBER
            row.createCell(4).setCellValue(String.valueOf(obj[4]));//FIFO TIME
            row.createCell(5).setCellValue(String.valueOf(obj[5]));//LEONI PART NUM
            row.createCell(6).setCellValue(String.valueOf(obj[6]));//PACK TYPE
            row.createCell(7).setCellStyle(numericFormat);
            row.getCell(7).setCellValue((double) Double.parseDouble(obj[7].toString()));//PACK SIZE
            row.createCell(8).setCellStyle(numericFormat);
            row.getCell(8).setCellValue((double) Double.parseDouble(obj[8].toString()));//QTY READ
            row.createCell(9).setCellStyle(numericFormat);
            row.getCell(9).setCellValue((double) Double.parseDouble(obj[9].toString()));//STD TIME
            row.createCell(10).setCellStyle(numericFormat);
            row.getCell(10).setCellValue((double) Double.parseDouble(obj[10].toString()));//TOTAL STD TIME
            row.createCell(11).setCellValue(String.valueOf(obj[11]));//STATE
            row.createCell(12).setCellValue(Integer.valueOf(String.valueOf(obj[12])));//STATE CODE
            row.createCell(13).setCellValue(String.valueOf(String.valueOf(obj[13])));//DISPATCH TIME
            row.createCell(14).setCellValue(String.valueOf(String.valueOf(obj[14])));//DISPATCH LABEL NO
            row.createCell(15).setCellValue(String.valueOf(String.valueOf(obj[15])));//TRUCK NO

            total_produced = total_produced + Integer.valueOf(String.valueOf(obj[8]));

            sheetPointer++;
        }

        //Total produced line
        row = sheet.createRow(sheetPointer++);
        row.createCell(0).setCellValue("TOTAL PRODUCED QTY :");
        row.createCell(1).setCellValue(total_produced);

        //Past the workbook to the file chooser
        new JDialogExcelFileChooser(null, true, wb).setVisible(true);
    }//GEN-LAST:event_export_btnActionPerformed

    private void segment_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_segment_filterItemStateChanged

    }//GEN-LAST:event_segment_filterItemStateChanged

    private void segment_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segment_filterActionPerformed
        String segment = String.valueOf(segment_filter.getSelectedItem()).trim();
        this.workplace_filter.removeAllItems();
        this.workplace_filter.addItem("ALL");
        if ("ALL".equals(segment) || segment.equals("null")) {
            this.workplace_filter.setSelectedIndex(0);
            this.workplace_filter.setEnabled(false);
        } else {
            workplace_filter = ConfigWorkplace.initWorkplaceJBox(this, workplace_filter, segment, true);
            this.workplace_filter.setEnabled(true);
        }


    }//GEN-LAST:event_segment_filterActionPerformed

    private void workplace_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_workplace_filterItemStateChanged

    }//GEN-LAST:event_workplace_filterItemStateChanged

    private void workplace_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplace_filterActionPerformed
//        refresh();
    }//GEN-LAST:event_workplace_filterActionPerformed

    private void lafm_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lafm_btnActionPerformed
        if (checkValidFields()) {

            startTimeStr = timeDf.format(startTimeSpinner.getValue());
            endTimeStr = timeDf.format(endTimeSpinner.getValue());
            startDateStr = dateDf.format(startDatePicker.getDate()) + " " + startTimeStr;
            endDateStr = dateDf.format(endDatePicker.getDate()) + " " + endTimeStr;
            segments.clear();
            workplaces.clear();
            projects.clear();

            //Populate the segments Array with data
            if (String.valueOf(project_filter.getSelectedItem()).equals("ALL") || String.valueOf(project_filter.getSelectedItem()).equals("null")) {
                List result = new ConfigProject().select();
                if (result.isEmpty()) {
                    UILog.severeDialog(this, ErrorMsg.APP_ERR0035);
                    UILog.severe(ErrorMsg.APP_ERR0035[1]);
                } else { //Map project data in the list
                    for (Object o : result) {
                        ConfigProject cp = (ConfigProject) o;
                        projects.add(cp.getProject());
                    }
                }
            } else {
                projects.add(String.valueOf(project_filter.getSelectedItem()).trim());
            }

            //Populate the segments Array with data
            if (String.valueOf(segment_filter.getSelectedItem()).equals("ALL") || String.valueOf(segment_filter.getSelectedItem()).equals("null")) {
                List result = new ConfigSegment().select();
                if (result.isEmpty()) {
                    JOptionPane.showMessageDialog(null, Helper.ERR0026_NO_SEGMENT_FOUND, "Configuration error !", ERROR_MESSAGE);
                    System.err.println(Helper.ERR0026_NO_SEGMENT_FOUND);
                } else { //Map project data in the list
                    for (Object o : result) {
                        ConfigSegment cs = (ConfigSegment) o;
                        segments.add(String.valueOf(cs.getSegment()));
                    }
                }
            } else {
                segments.add(String.valueOf(segment_filter.getSelectedItem()));
                //Populate the workplaces Array with data
                if (String.valueOf(workplace_filter.getSelectedItem()).equals("ALL")) {
                    List result = new ConfigWorkplace().selectBySegment(String.valueOf(segment_filter.getSelectedItem()));
                    if (result.isEmpty()) {
                        JOptionPane.showMessageDialog(null, Helper.ERR0027_NO_WORKPLACE_FOUND, "Configuration error !", ERROR_MESSAGE);
                        System.err.println(Helper.ERR0027_NO_WORKPLACE_FOUND);
                    } else { //Map project data in the list
                        for (Object o : result) {
                            ConfigWorkplace cw = (ConfigWorkplace) o;
                            workplaces.add(String.valueOf(cw.getWorkplace()));
                        }
                    }
                } else {
                    workplaces.add(String.valueOf(workplace_filter.getSelectedItem()));
                }
            }

            //################# Harness Data ####################            
            Helper.startSession();
            try {
                String q = " SELECT "
                        + " bc.supplier_part_number AS supplier_part_number, "
                        + " SUM(bc.qty_expected) AS qty_expected, "
                        + " bc.warehouse AS warehouse "
                        + " FROM base_container bc "
                        + " WHERE 1=1 ";
                //bc.container_state = '" + GlobalVars.PALLET_STORED + "' ";
                if (!projects.isEmpty()) {
                    q += " AND bc.project IN (:projects) ";
                    System.out.println("projects " + projects.size());
                }
                if (!segments.isEmpty()) {
                    q += " AND bc.segment IN (:segments) ";
                }
                if (!workplaces.isEmpty()) {
                    q += " AND bc.workplace IN (:workplaces) ";
                }
                //Add harness part filter                
                if (!filter_harness_part_txt.getText().isEmpty()) {
                    q += " AND harness_part LIKE :hp";
                }

                System.out.println("Query" + q);

                try {
                    Date startDate = dateTimeDf.parse(startDateStr);
                    Date endDate = dateTimeDf.parse(endDateStr);
                    System.out.println(startDate.before(endDate));
                } catch (Exception ex) {
                    Logger.getLogger(WAREHOUSE_FG_UI0001_SCAN_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (startDatePicker.getDate() != null && endDatePicker.getDate() != null) {
                    q += " AND bc.stored_time BETWEEN '%s' AND '%s'";
                    q += " GROUP BY supplier_part_number, qty_expected, warehouse";
                    q = String.format(q, startDateStr, endDateStr);
                } else if (startDatePicker.getDate() != null && endDatePicker.getDate() == null) {
                    q += " AND bc.stored_time > '%s'";
                    q += " GROUP BY supplier_part_number, qty_expected, warehouse";
                    q = String.format(q, startDateStr);
                }
                if (startDatePicker.getDate() == null && endDatePicker.getDate() != null) {
                    q += " AND bc.stored_time < '%s'";
                    q += " GROUP BY supplier_part_number, qty_expected, warehouse";
                    q = String.format(q, endDateStr);
                }

                SQLQuery query = Helper.sess.createSQLQuery(q);

                query.addScalar("supplier_part_number", StandardBasicTypes.STRING)
                        .addScalar("qty_expected", StandardBasicTypes.DOUBLE)
                        .addScalar("warehouse", StandardBasicTypes.STRING);

                if (!projects.isEmpty()) {
                    query.setParameterList("projects", projects);
                }
                if (!segments.isEmpty()) {
                    query.setParameterList("segments", segments);
                }
                if (!workplaces.isEmpty()) {
                    query.setParameterList("workplaces", workplaces);
                }

                if (!filter_harness_part_txt.getText().isEmpty()) {
                    query.setParameter("hp", "%" + filter_harness_part_txt.getText() + "%");
                }

                List<Object[]> lafmResult = query.list();
                Workbook wb = new XSSFWorkbook();
                CreationHelper createHelper = wb.getCreationHelper();
                Sheet sheet = wb.createSheet("LAFM");

                //Create the excel workbook
                Row row = sheet.createRow((short) 0);

                short sheetPointer = 0;

                row = sheet.createRow(sheetPointer++);
                row.createCell(0).setCellValue("Du : " + startDateStr);
                row.createCell(1).setCellValue("Au : " + endDateStr);

                for (Object[] obj : lafmResult) {
                    row = sheet.createRow(sheetPointer);
                    row.createCell(0).setCellValue(GlobalVars.HARN_PART_PREFIX + String.valueOf(obj[0])); //LPN                    
                    DecimalFormat df = new DecimalFormat("00.00");
                    row.createCell(1).setCellValue(GlobalVars.QUANTITY_PREFIX + df.format(Double.parseDouble(obj[1].toString())));//TOTAL QTY 
                    row.createCell(2).setCellValue(String.valueOf("LO" + obj[2].toString()));//WAREHOUSE
                    sheetPointer++;
                }

                Helper.sess.getTransaction().commit();
                //Past the workbook to the file chooser
                new JDialogExcelFileChooser(null, true, wb).setVisible(true);
            } catch (HibernateException e) {
                if (Helper.sess.getTransaction() != null) {
                    Helper.sess.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Empty field", "Empty field Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_lafm_btnActionPerformed

    private void startDatePickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDatePickerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startDatePickerActionPerformed

    private void filter_harness_part_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter_harness_part_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filter_harness_part_txtActionPerformed

    private void project_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_project_filterItemStateChanged

    }//GEN-LAST:event_project_filterItemStateChanged

    private void project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_filterActionPerformed
        String project = String.valueOf(project_filter.getSelectedItem()).trim();
        ConfigSegment.setSegmentByProject(this, segment_filter, project, true);
    }//GEN-LAST:event_project_filterActionPerformed

    private void filter_spn_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter_spn_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filter_spn_txtActionPerformed

    private void filter_spn_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filter_spn_txtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_filter_spn_txtKeyPressed

    private void filter_packtype_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter_packtype_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filter_packtype_txtActionPerformed

    private void filter_packtype_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filter_packtype_txtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_filter_packtype_txtKeyPressed

    private void startDatePickerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_startDatePickerKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_startDatePickerKeyPressed

    private void startTimeSpinnerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_startTimeSpinnerKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_startTimeSpinnerKeyPressed

    private void endDatePickerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_endDatePickerKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_endDatePickerKeyPressed

    private void endTimeSpinnerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_endTimeSpinnerKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_endTimeSpinnerKeyPressed

    private void status_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_status_filterItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_status_filterItemStateChanged

    private void status_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_status_filterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_status_filterActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    org.jdesktop.swingx.JXDatePicker endDatePicker;
    javax.swing.JSpinner endTimeSpinner;
    javax.swing.JButton export_btn;
    javax.swing.JTextField filter_harness_part_txt;
    javax.swing.JTextField filter_packtype_txt;
    javax.swing.JTextField filter_spn_txt;
    javax.swing.JLabel jLabel18;
    javax.swing.JLabel jLabel19;
    javax.swing.JLabel jLabel2;
    javax.swing.JLabel jLabel20;
    javax.swing.JLabel jLabel21;
    javax.swing.JLabel jLabel22;
    javax.swing.JLabel jLabel23;
    javax.swing.JLabel jLabel24;
    javax.swing.JLabel jLabel3;
    javax.swing.JLabel jLabel5;
    javax.swing.JLabel jLabel6;
    javax.swing.JLabel jLabel7;
    javax.swing.JLabel jLabel8;
    javax.swing.JPanel jPanel1;
    javax.swing.JPanel jPanel2;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JButton lafm_btn;
    javax.swing.JTextField nbreLigne;
    javax.swing.JComboBox project_filter;
    javax.swing.JButton refresh_btn;
    javax.swing.JTable searchResult_table;
    javax.swing.JComboBox segment_filter;
    org.jdesktop.swingx.JXDatePicker startDatePicker;
    javax.swing.JSpinner startTimeSpinner;
    javax.swing.JComboBox status_filter;
    javax.swing.JTextField total_declared_lbl;
    javax.swing.JTextField total_hours_lbl;
    javax.swing.JComboBox workplace_filter;
    // End of variables declaration//GEN-END:variables

    void clearSearchBox(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
