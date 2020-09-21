/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.config;

import __main__.GlobalVars;
import entity.ConfigFamily;
import entity.ConfigProject;
import entity.ConfigSegment;
import entity.ConfigUcs;
import entity.ConfigWarehouse;
import entity.ConfigWorkplace;
import entity.LoadPlanDestination;
import entity.PackagingMaster;
import gui.packaging.PackagingVars;
import helper.HQLHelper;
import helper.Helper;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import ui.UILog;

/**
 *
 * @author Administrator
 */
public class CONFIG_UI0001_CONFIG_PN_JPANEL extends javax.swing.JPanel {

    JTabbedPane parent;
    
    

    List<String> table_header = Arrays.asList(
            "#",
            "CPN",
            "LPN",
            "Order No",
            "Harness Type",
            "Harness Index",
            "Pack type",
            "Pack size",
            "Additional barecode",
            "Std Time",
            "Destination",
            "Segment",
            "Workplace",
            "Active",
            "Lifes",
            "Special Order",
            "Price"
    );
    
    Vector<String> ucs_table_header = new Vector<String>();
    Vector ucs_table_data = new Vector();
    public List<Object[]> resultList;
    ConfigUcs aux;

    public CONFIG_UI0001_CONFIG_PN_JPANEL(JTabbedPane parent) {
        initComponents();
        initGui(parent);
        
    }

    private void initGui(JTabbedPane parent) {

        //Desable table edition
        disableEditingTable();
        //Load table header
        load_table_header();

        //Load pack master data
        pack_type_filter = PackagingMaster.initPackMasterJBox(this, pack_type_filter, false);

        project_filter = ConfigProject.initProjectsJBox(this, project_filter, "", false);
        
        this.initLineTableDoubleClick();
        
        tabbedPane_ucs_config.setSelectedIndex(0);
        cpn_txtbox_search.requestFocus();
    }

    
    private void load_table_header() {
        this.reset_table_content();

        for (Iterator<String> it = table_header.iterator(); it.hasNext();) {
            ucs_table_header.add(it.next());
        }

        ucs_table.setModel(new DefaultTableModel(ucs_table_data, ucs_table_header));
    }

    private void initLineTableDoubleClick() {
        this.ucs_table.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_UCS_BY_ID);
                    query.setParameter("id", Integer.valueOf(ucs_table.getValueAt(ucs_table.getSelectedRow(), 0).toString()));
                    Helper.sess.getTransaction().commit();
                    aux = (ConfigUcs) query.list().get(0);
                    
                    id_lbl.setText(aux.getId().toString());
                    create_time_txt.setText(sdf.format(aux.getCreateTime()));
                    write_time_txt.setText(sdf.format(aux.getWriteTime()));
                    cpn_txtbox.setText(aux.getHarnessPart());
                    lpn_txtbox.setText(aux.getSupplierPartNumber());
                    index_txtbox.setText(aux.getHarnessIndex());
                    engChange_textArea.setText(aux.getEngChange());
                    engChangeDatePicker.setDate(aux.getEngChangeDate());
                    articleDesc_textArea.setText(aux.getArticleDesc());
                    netWeight_txtbox.setText(aux.getNetWeight() + "");
                    grossWeight_txtbox.setText(aux.getGrossWeight() + "");
                    volume_txtbox.setText(aux.getVolume() + "");
                    pack_size_txtbox.setText(aux.getPackSize() + "");
                    assy_txtbox.setText(aux.getAssyWorkstationName());
                    barcodes_nbre_txtbox.setText(aux.getAdditionalBarcode() + "");
                    nbreOfBoxes_txtbox.setText(aux.getLifes() + "");
                    order_no_txt.setText(aux.getOrderNo());
                    comment_txt.setText(aux.getComment());
                    priority.setText(aux.getPriority() + "");
                    open_sheet_copies.setText(aux.getOpenningSheetCopies() + "");
                    closing_sheet_copies.setText(aux.getOpenningSheetCopies() + "");
                    print_destination_check.setSelected(aux.getPrint_destination());
                    //destination_txtbox.setText(aux.getDestination());

                    try {
                        stdTime_txtbox.setText(aux.getStdTime() + "");
                    } catch (Exception ex) {
                        stdTime_txtbox.setText("0.00");
                    }
                    try {
                        price_txtbox.setText(aux.getPrice() + "");
                    } catch (Exception ex) {
                        price_txtbox.setText("0.00");
                    }
                    for (int i = 0; i < pack_type_filter.getItemCount(); i++) {
                        if (pack_type_filter.getItemAt(i).toString().equals(aux.getPackType())) {
                            pack_type_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < project_filter.getItemCount(); i++) {
                        if (project_filter.getItemAt(i).toString().equals(aux.getProject())) {
                            project_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < fg_warehouse_filter.getItemCount(); i++) {
                        if (fg_warehouse_filter.getItemAt(i).toString().equals(aux.getWarehouse())) {
                            fg_warehouse_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < segment_filter.getItemCount(); i++) {
                        if (segment_filter.getItemAt(i).toString().equals(aux.getSegment())) {
                            segment_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < workplace_filter.getItemCount(); i++) {
                        if (workplace_filter.getItemAt(i).toString().equals(aux.getWorkplace())) {
                            workplace_filter.setSelectedIndex(i);
                            break;
                        }
                    }

                    for (int i = 0; i < destination_filter.getItemCount(); i++) {
////                        System.out.println("TEST DESTINATION " + i + " " + aux.getDestination() + " vs " + destination_filter.getItemAt(i).toString());
                        if (destination_filter.getItemAt(i).toString().equals(aux.getDestination())) {
                            destination_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < family_filter.getItemCount(); i++) {
                        if (family_filter.getItemAt(i).toString().equals(aux.getHarnessType())) {
                            family_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < packaging_wh_filter.getItemCount(); i++) {
                        if (packaging_wh_filter.getItemAt(i).toString().equals(aux.getPackaging_warehouse())) {
                            packaging_wh_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < closing_sheet_format.getItemCount(); i++) {
                        if (Integer.valueOf(closing_sheet_format.getItemAt(i)) == aux.getClosingSheetFormat()) {
                            closing_sheet_format.setSelectedIndex(i);
                            break;
                        }
                    }

                    if (String.valueOf(aux.getActive()).equals("1")) {
                        active_combobox.setSelectedIndex(0);
                    } else {
                        active_combobox.setSelectedIndex(1);
                    }

                    if (aux.getSpecialOrder() == 1) {
                        special_order_check.setSelected(true);
                    } else {
                        special_order_check.setSelected(false);
                    }

                    try {
                        if (aux.isLabelPerPiece()) {
                            label_per_piece_checkbox.setSelected(true);
                        } else {
                            label_per_piece_checkbox.setSelected(false);
                        }
                    } catch (NullPointerException ex) {
                        label_per_piece_checkbox.setSelected(false);
                    }

                    delete_btn.setEnabled(true);
                    duplicate_btn.setEnabled(true);
                    tabbedPane_ucs_config.setSelectedIndex(1);
                }
            }
        }
        );
    }

    private void reset_table_content() {

        ucs_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(ucs_table_data, ucs_table_header);
        ucs_table.setModel(dataModel);
    }

    public void disableEditingTable() {
        for (int c = 0; c < ucs_table.getColumnCount(); c++) {
            Class<?> col_class = ucs_table.getColumnClass(c);
            ucs_table.setDefaultEditor(col_class, null);        // remove editor            
        }
    }

    /**
     * @param ucs_table_data
     * @param ucs_table_header
     * @param ucs_table
     * @todo : reload_table_data a mettre dans une classe interface
     * @param resultList
     */
    public void reload_table_data(List<Object[]> resultList, Vector ucs_table_data, Vector<String> ucs_table_header, JTable ucs_table) {
        this.reset_table_content();
        for (Object[] line : resultList) {
            @SuppressWarnings("UseOfObsoleteCollectionType")
            Vector<Object> oneRow = new Vector<Object>();
            for (Object cell : line) {
                oneRow.add(String.valueOf(cell));
            }
            ucs_table_data.add(oneRow);
        }

        ucs_table.setModel(new DefaultTableModel(ucs_table_data, ucs_table_header));
        ucs_table.setAutoCreateRowSorter(true);
    }

    private void clearFields() {
        id_lbl.setText("");
        create_time_txt.setText("");
        write_time_txt.setText("");
        cpn_txtbox.setText("");
        lpn_txtbox.setText("");
        index_txtbox.setText("");
        active_combobox.setSelectedIndex(0);
        assy_txtbox.setText("");
        nbreOfBoxes_txtbox.setText("-1");
        barcodes_nbre_txtbox.setText("0");
        pack_size_txtbox.setText("");
        stdTime_txtbox.setText("0,00");
        cra_stdTime_txtbox.setText("0,00");
        price_txtbox.setText("0,00");
        order_no_txt.setText("");
        delete_btn.setEnabled(false);
        duplicate_btn.setEnabled(false);
        msg_lbl.setText("");
        comment_txt.setText("");
        destination_filter.setSelectedIndex(0);
        special_order_check.setSelected(false);
        articleDesc_textArea.setText("");
        engChange_textArea.setText("");
        engChangeDatePicker.setDate(null);
        netWeight_txtbox.setText("0,00");
        grossWeight_txtbox.setText("0,00");
        volume_txtbox.setText("0,00");
        label_per_piece_checkbox.setSelected(false);
        print_destination_check.setSelected(false);
        priority.setText("99");
        open_sheet_copies.setText("1");
        closing_sheet_copies.setText("1");
        aux = null;
    }

    private void clearSearchFields() {
        project_txtbox_search.setText("");
        segment_txtbox_search.setText("");
        cpn_txtbox_search.setText("");
        pack_type_txtbox_search.setText("");
        supplier_pn_txtbox_search.setText("");
    }

    private void refresh() {
        Helper.startSession();
        String query_str = " SELECT "
                + " u.id AS id, "
                + " u.harness_part AS cpn, "
                + " u.supplier_part_number AS lpn, "
                + " u.order_no AS order_no, "
                + " u.harness_type AS harness_type, "
                + " u.harness_index AS harness_index, "
                + " u.pack_type AS pack_type, "
                + " u.pack_size AS pack_size, "
                + " u.additional_barcode AS barecodes, "
                + " u.std_time AS stdTime, "
                + " u.destination AS destination, "
                + " u.segment AS segment, "
                + " u.workplace AS workplace, "
                + " u.active AS active, "
                + " u.lifes AS lifes, "
                + " u.special_order AS special_order,"
                + " u.price "
                + " FROM Config_Ucs u WHERE 1=1 ";
        
        if (!project_txtbox_search.getText().trim().equals("")) {
            query_str += " AND project LIKE '%" + project_txtbox_search.getText().trim() + "%'";
        }
        if (!cpn_txtbox_search.getText().trim().equals("")) {
            query_str += " AND harness_part LIKE '%" + cpn_txtbox_search.getText().trim() + "%'";
        }
        if (!pack_type_txtbox_search.getText().trim().equals("")) {
            query_str += " AND pack_type LIKE '%" + pack_type_txtbox_search.getText().trim() + "%'";
        }
        if (!project_txtbox_search.getText().trim().equals("")) {
            query_str += " AND project LIKE '%" + project_txtbox_search.getText().trim() + "%'";
        }
        if (!segment_txtbox_search.getText().trim().equals("")) {
            query_str += " AND segment LIKE '%" + segment_txtbox_search.getText().trim() + "%'";
        }
        if (!supplier_pn_txtbox_search.getText().trim().equals("")) {
            query_str += " AND supplier_part_number LIKE '%" + supplier_pn_txtbox_search.getText().trim() + "%'";
        }

        query_str += " ORDER BY id DESC ";
        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        resultList = query.list();

        Helper.sess.getTransaction().commit();

        this.reload_table_data(resultList, ucs_table_data, ucs_table_header, ucs_table);

        this.disableEditingTable();
    }

    /**
     * This method is called from within the constrauxctor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        save_btn = new javax.swing.JButton();
        cancel_btn = new javax.swing.JButton();
        delete_btn = new javax.swing.JButton();
        duplicate_btn = new javax.swing.JButton();
        msg_lbl = new javax.swing.JLabel();
        tabbedPane_ucs_config = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        llogin_lbl_search2 = new javax.swing.JLabel();
        project_txtbox_search = new javax.swing.JTextField();
        llogin_lbl_search = new javax.swing.JLabel();
        segment_txtbox_search = new javax.swing.JTextField();
        fname_lbl_search = new javax.swing.JLabel();
        cpn_txtbox_search = new javax.swing.JTextField();
        lname_lbl_search = new javax.swing.JLabel();
        pack_type_txtbox_search = new javax.swing.JTextField();
        llogin_lbl_search1 = new javax.swing.JLabel();
        supplier_pn_txtbox_search = new javax.swing.JTextField();
        filter_btn = new javax.swing.JButton();
        clear_search_btn = new javax.swing.JButton();
        user_table_scroll = new javax.swing.JScrollPane();
        ucs_table = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        project_filter = new javax.swing.JComboBox();
        login_lbl3 = new javax.swing.JLabel();
        login_lbl5 = new javax.swing.JLabel();
        fg_warehouse_filter = new javax.swing.JComboBox();
        login_lbl = new javax.swing.JLabel();
        segment_filter = new javax.swing.JComboBox();
        pwd_lbl = new javax.swing.JLabel();
        workplace_filter = new javax.swing.JComboBox();
        pwd_lbl7 = new javax.swing.JLabel();
        family_filter = new javax.swing.JComboBox();
        cpn_txtbox = new javax.swing.JTextField();
        fname_lbl = new javax.swing.JLabel();
        lname_lbl = new javax.swing.JLabel();
        lpn_txtbox = new javax.swing.JTextField();
        pwd_lbl1 = new javax.swing.JLabel();
        index_txtbox = new javax.swing.JTextField();
        active_combobox = new javax.swing.JComboBox();
        pwd_lbl2 = new javax.swing.JLabel();
        login_lbl1 = new javax.swing.JLabel();
        assy_txtbox = new javax.swing.JTextField();
        order_no_txt = new javax.swing.JTextField();
        login_lbl4 = new javax.swing.JLabel();
        pwd_lbl6 = new javax.swing.JLabel();
        stdTime_txtbox = new javax.swing.JTextField();
        lname_lbl1 = new javax.swing.JLabel();
        create_time_txt = new javax.swing.JTextField();
        lname_lbl2 = new javax.swing.JLabel();
        write_time_txt = new javax.swing.JTextField();
        packaging_wh_filter = new javax.swing.JComboBox();
        fname_lbl14 = new javax.swing.JLabel();
        priority = new javax.swing.JTextField();
        login_lbl7 = new javax.swing.JLabel();
        cra_stdTime_txtbox = new javax.swing.JTextField();
        pwd_lbl15 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        pwd_lbl3 = new javax.swing.JLabel();
        pack_type_filter = new javax.swing.JComboBox();
        login_lbl2 = new javax.swing.JLabel();
        barcodes_nbre_txtbox = new javax.swing.JTextField();
        label_per_piece_checkbox = new javax.swing.JCheckBox();
        pwd_lbl4 = new javax.swing.JLabel();
        pack_size_txtbox = new javax.swing.JTextField();
        pwd_lbl5 = new javax.swing.JLabel();
        nbreOfBoxes_txtbox = new javax.swing.JTextField();
        special_order_check = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        comment_txt = new javax.swing.JTextArea();
        open_sheet_copies = new javax.swing.JTextField();
        closing_sheet_copies = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        closing_sheet_format = new javax.swing.JComboBox<>();
        login_lbl8 = new javax.swing.JLabel();
        print_destination_check = new javax.swing.JCheckBox();
        login_lbl6 = new javax.swing.JLabel();
        destination_filter = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        pwd_lbl9 = new javax.swing.JLabel();
        netWeight_txtbox = new javax.swing.JTextField();
        pwd_lbl10 = new javax.swing.JLabel();
        grossWeight_txtbox = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 100), new java.awt.Dimension(0, 100), new java.awt.Dimension(32767, 100));
        pwd_lbl12 = new javax.swing.JLabel();
        volume_txtbox = new javax.swing.JTextField();
        pwd_lbl8 = new javax.swing.JLabel();
        price_txtbox = new javax.swing.JTextField();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 100), new java.awt.Dimension(0, 100), new java.awt.Dimension(32767, 100));
        pwd_lbl13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        engChange_textArea = new javax.swing.JTextArea();
        pwd_lbl14 = new javax.swing.JLabel();
        engChangeDatePicker = new org.jdesktop.swingx.JXDatePicker();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 100), new java.awt.Dimension(0, 100), new java.awt.Dimension(32767, 100));
        pwd_lbl11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        articleDesc_textArea = new javax.swing.JTextArea();
        fname_lbl1 = new javax.swing.JLabel();
        id_lbl = new javax.swing.JLabel();

        setAutoscrolls(true);
        setName("Configuration packaging par référence"); // NOI18N

        jPanel1.setBackground(new java.awt.Color(36, 65, 86));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Standard Pack Master Data", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setToolTipText("Standard Pack Master Data");
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setName("Standard Pack Master Data"); // NOI18N

        save_btn.setText("Enregistrer");
        save_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_btnActionPerformed(evt);
            }
        });

        cancel_btn.setText("Réinitialiser");
        cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_btnActionPerformed(evt);
            }
        });

        delete_btn.setText("Supprimer");
        delete_btn.setEnabled(false);
        delete_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_btnActionPerformed(evt);
            }
        });

        duplicate_btn.setText("Dupliquer");
        duplicate_btn.setEnabled(false);
        duplicate_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duplicate_btnActionPerformed(evt);
            }
        });

        msg_lbl.setBackground(new java.awt.Color(255, 255, 255));
        msg_lbl.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        msg_lbl.setForeground(new java.awt.Color(0, 255, 51));

        jPanel6.setBackground(new java.awt.Color(36, 65, 86));

        jPanel5.setBackground(new java.awt.Color(36, 65, 86));

        llogin_lbl_search2.setForeground(new java.awt.Color(255, 255, 255));
        llogin_lbl_search2.setText("Projet");

        project_txtbox_search.setName("fname_txtbox"); // NOI18N
        project_txtbox_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                project_txtbox_searchKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                project_txtbox_searchKeyTyped(evt);
            }
        });

        llogin_lbl_search.setForeground(new java.awt.Color(255, 255, 255));
        llogin_lbl_search.setText("Segment");

        segment_txtbox_search.setName("fname_txtbox"); // NOI18N
        segment_txtbox_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                segment_txtbox_searchKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                segment_txtbox_searchKeyTyped(evt);
            }
        });

        fname_lbl_search.setForeground(new java.awt.Color(255, 255, 255));
        fname_lbl_search.setText("CPN");

        cpn_txtbox_search.setName("fname_txtbox"); // NOI18N
        cpn_txtbox_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cpn_txtbox_searchKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cpn_txtbox_searchKeyTyped(evt);
            }
        });

        lname_lbl_search.setForeground(new java.awt.Color(255, 255, 255));
        lname_lbl_search.setText("Pack Type");

        pack_type_txtbox_search.setName("fname_txtbox"); // NOI18N
        pack_type_txtbox_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pack_type_txtbox_searchActionPerformed(evt);
            }
        });
        pack_type_txtbox_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pack_type_txtbox_searchKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pack_type_txtbox_searchKeyTyped(evt);
            }
        });

        llogin_lbl_search1.setForeground(new java.awt.Color(255, 255, 255));
        llogin_lbl_search1.setText("LPN");

        supplier_pn_txtbox_search.setName("fname_txtbox"); // NOI18N
        supplier_pn_txtbox_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supplier_pn_txtbox_searchActionPerformed(evt);
            }
        });
        supplier_pn_txtbox_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                supplier_pn_txtbox_searchKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                supplier_pn_txtbox_searchKeyTyped(evt);
            }
        });

        filter_btn.setText("Actualiser");
        filter_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter_btnActionPerformed(evt);
            }
        });

        clear_search_btn.setText("Effacer");
        clear_search_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_search_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lname_lbl_search)
                    .addComponent(fname_lbl_search))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cpn_txtbox_search)
                    .addComponent(pack_type_txtbox_search, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
                .addGap(45, 45, 45)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(llogin_lbl_search1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(llogin_lbl_search2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(supplier_pn_txtbox_search, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                    .addComponent(project_txtbox_search))
                .addGap(43, 43, 43)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(llogin_lbl_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(segment_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(filter_btn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clear_search_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(179, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(project_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(llogin_lbl_search2)
                    .addComponent(cpn_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fname_lbl_search)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(segment_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(llogin_lbl_search)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(supplier_pn_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(llogin_lbl_search1)
                    .addComponent(pack_type_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lname_lbl_search)
                    .addComponent(clear_search_btn)
                    .addComponent(filter_btn)))
        );

        ucs_table.setModel(new javax.swing.table.DefaultTableModel(
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
        ucs_table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        user_table_scroll.setViewportView(ucs_table);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(user_table_scroll)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(user_table_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane_ucs_config.addTab("Liste", jPanel6);

        jPanel2.setBackground(new java.awt.Color(36, 65, 86));
        jPanel2.setMaximumSize(null);

        project_filter.setBackground(new java.awt.Color(204, 204, 255));
        project_filter.setMaximumSize(null);
        project_filter.setMinimumSize(new java.awt.Dimension(37, 26));
        project_filter.setPreferredSize(new java.awt.Dimension(37, 26));
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

        login_lbl3.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl3.setText("Projet *");

        login_lbl5.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl5.setText("Magasin F.G");

        fg_warehouse_filter.setBackground(new java.awt.Color(204, 204, 255));
        fg_warehouse_filter.setMaximumSize(null);
        fg_warehouse_filter.setMinimumSize(new java.awt.Dimension(37, 26));
        fg_warehouse_filter.setPreferredSize(new java.awt.Dimension(37, 26));
        fg_warehouse_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fg_warehouse_filterItemStateChanged(evt);
            }
        });
        fg_warehouse_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fg_warehouse_filterActionPerformed(evt);
            }
        });

        login_lbl.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl.setText("Segment *");

        segment_filter.setBackground(new java.awt.Color(204, 204, 255));
        segment_filter.setMaximumSize(null);
        segment_filter.setMinimumSize(new java.awt.Dimension(37, 26));
        segment_filter.setPreferredSize(new java.awt.Dimension(37, 26));
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

        pwd_lbl.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl.setText("Workplace *");

        workplace_filter.setBackground(new java.awt.Color(204, 204, 255));
        workplace_filter.setMaximumSize(null);
        workplace_filter.setMinimumSize(new java.awt.Dimension(37, 26));
        workplace_filter.setPreferredSize(new java.awt.Dimension(37, 26));
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

        pwd_lbl7.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl7.setText("Famille Article *");

        family_filter.setBackground(new java.awt.Color(204, 204, 255));
        family_filter.setMaximumSize(null);
        family_filter.setMinimumSize(new java.awt.Dimension(37, 26));
        family_filter.setPreferredSize(new java.awt.Dimension(37, 26));

        cpn_txtbox.setBackground(new java.awt.Color(204, 204, 255));
        cpn_txtbox.setMaximumSize(null);
        cpn_txtbox.setName("cpn_txtbox"); // NOI18N

        fname_lbl.setForeground(new java.awt.Color(255, 255, 255));
        fname_lbl.setText("CPN *");

        lname_lbl.setForeground(new java.awt.Color(255, 255, 255));
        lname_lbl.setText("LPN *");

        lpn_txtbox.setBackground(new java.awt.Color(204, 204, 255));
        lpn_txtbox.setMaximumSize(null);
        lpn_txtbox.setName("fname_txtbox"); // NOI18N

        pwd_lbl1.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl1.setText("Indice *");

        index_txtbox.setBackground(new java.awt.Color(204, 204, 255));
        index_txtbox.setMaximumSize(null);
        index_txtbox.setName("fname_txtbox"); // NOI18N

        active_combobox.setBackground(new java.awt.Color(204, 204, 255));
        active_combobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "0" }));
        active_combobox.setMaximumSize(null);
        active_combobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                active_comboboxActionPerformed(evt);
            }
        });

        pwd_lbl2.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl2.setText("Active *");

        login_lbl1.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl1.setText("Station Assemblage");

        assy_txtbox.setMaximumSize(null);
        assy_txtbox.setName("fname_txtbox"); // NOI18N

        order_no_txt.setBackground(new java.awt.Color(204, 204, 255));
        order_no_txt.setMaximumSize(null);
        order_no_txt.setName("fname_txtbox"); // NOI18N

        login_lbl4.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl4.setText("Order No *");

        pwd_lbl6.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl6.setText("Assemblage *");

        stdTime_txtbox.setBackground(new java.awt.Color(204, 204, 255));
        stdTime_txtbox.setText("0.00");
        stdTime_txtbox.setMaximumSize(null);
        stdTime_txtbox.setName("fname_txtbox"); // NOI18N
        stdTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stdTime_txtboxActionPerformed(evt);
            }
        });

        lname_lbl1.setForeground(new java.awt.Color(255, 255, 255));
        lname_lbl1.setText("Date création");

        create_time_txt.setEditable(false);
        create_time_txt.setMaximumSize(null);
        create_time_txt.setName("fname_txtbox"); // NOI18N

        lname_lbl2.setForeground(new java.awt.Color(255, 255, 255));
        lname_lbl2.setText("Modifier le");

        write_time_txt.setEditable(false);
        write_time_txt.setMaximumSize(null);
        write_time_txt.setName("fname_txtbox"); // NOI18N

        packaging_wh_filter.setBackground(new java.awt.Color(204, 204, 255));
        packaging_wh_filter.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        packaging_wh_filter.setMaximumSize(null);
        packaging_wh_filter.setMinimumSize(new java.awt.Dimension(37, 26));
        packaging_wh_filter.setPreferredSize(new java.awt.Dimension(37, 26));
        packaging_wh_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packaging_wh_filterActionPerformed(evt);
            }
        });

        fname_lbl14.setForeground(new java.awt.Color(255, 255, 255));
        fname_lbl14.setText("Magasin Emballage");

        login_lbl7.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl7.setText("Priorité");

        cra_stdTime_txtbox.setBackground(new java.awt.Color(204, 204, 255));
        cra_stdTime_txtbox.setText("0.00");
        cra_stdTime_txtbox.setMaximumSize(null);
        cra_stdTime_txtbox.setName("fname_txtbox"); // NOI18N
        cra_stdTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cra_stdTime_txtboxActionPerformed(evt);
            }
        });

        pwd_lbl15.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl15.setText("Coupe + préparation *");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Temps de gamme");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(login_lbl3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(login_lbl5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fname_lbl14, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(login_lbl1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(36, 36, 36)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(packaging_wh_filter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(create_time_txt, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fg_warehouse_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(project_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, 176, Short.MAX_VALUE)
                    .addComponent(assy_txtbox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pwd_lbl7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pwd_lbl2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pwd_lbl, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(login_lbl, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(write_time_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(segment_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, 171, Short.MAX_VALUE)
                                .addComponent(workplace_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(family_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addComponent(login_lbl7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(active_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 36, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fname_lbl, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lname_lbl, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pwd_lbl1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(login_lbl4, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(priority)
                    .addComponent(cpn_txtbox, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(lpn_txtbox, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(order_no_txt, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(index_txtbox, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                .addGap(446, 446, 446))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pwd_lbl15)
                            .addComponent(pwd_lbl6))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cra_stdTime_txtbox, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                            .addComponent(stdTime_txtbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(351, 351, 351)
                        .addComponent(lname_lbl2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lname_lbl1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(login_lbl3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fg_warehouse_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(login_lbl5)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(login_lbl)
                            .addComponent(fname_lbl)
                            .addComponent(cpn_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pwd_lbl)
                            .addComponent(lname_lbl)
                            .addComponent(lpn_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(packaging_wh_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pwd_lbl7)
                    .addComponent(family_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pwd_lbl1)
                    .addComponent(index_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fname_lbl14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(pwd_lbl2, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(order_no_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(login_lbl4)
                            .addComponent(active_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(assy_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(login_lbl1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(create_time_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lname_lbl1)
                        .addComponent(lname_lbl2))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(write_time_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(priority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(login_lbl7)))
                .addGap(35, 35, 35)
                .addComponent(jLabel4)
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stdTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pwd_lbl6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cra_stdTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pwd_lbl15))
                .addContainerGap())
        );

        tabbedPane_ucs_config.addTab("infos Générales", jPanel2);

        jPanel4.setBackground(new java.awt.Color(36, 65, 86));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        pwd_lbl3.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl3.setText("Pack Type *");

        pack_type_filter.setBackground(new java.awt.Color(204, 204, 255));

        login_lbl2.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl2.setText("Barcodes Nbre *");

        barcodes_nbre_txtbox.setBackground(new java.awt.Color(204, 204, 255));
        barcodes_nbre_txtbox.setText("0");
        barcodes_nbre_txtbox.setName("fname_txtbox"); // NOI18N

        label_per_piece_checkbox.setForeground(new java.awt.Color(255, 255, 255));
        label_per_piece_checkbox.setText("Imprimer une étiquette pour chaque pièce scannée");
        label_per_piece_checkbox.setToolTipText("Print an A5 label for each scanned piece.\nThis label is different from open and closing sheet labels, If set to true, it will be printed once the user scan the QR code of a harness.");
        label_per_piece_checkbox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                label_per_piece_checkboxStateChanged(evt);
            }
        });

        pwd_lbl4.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl4.setText("Std Pack Qty  *");

        pack_size_txtbox.setBackground(new java.awt.Color(204, 204, 255));
        pack_size_txtbox.setText("1");
        pack_size_txtbox.setName("fname_txtbox"); // NOI18N

        pwd_lbl5.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl5.setText("Nbre Packs *");

        nbreOfBoxes_txtbox.setBackground(new java.awt.Color(204, 204, 255));
        nbreOfBoxes_txtbox.setText("-1");
        nbreOfBoxes_txtbox.setName("fname_txtbox"); // NOI18N

        special_order_check.setForeground(new java.awt.Color(255, 255, 255));
        special_order_check.setText("Cmd. Spéciale ?");
        special_order_check.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                special_order_checkStateChanged(evt);
            }
        });

        comment_txt.setColumns(10);
        comment_txt.setRows(2);
        comment_txt.setToolTipText("Commentaire pour la commande spéciale...");
        comment_txt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                comment_txtFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(comment_txt);

        open_sheet_copies.setBackground(new java.awt.Color(204, 204, 255));

        closing_sheet_copies.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Copies de fiches ouvertures palette");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Copies de fiche fermetures palettes");

        closing_sheet_format.setBackground(new java.awt.Color(204, 204, 255));
        closing_sheet_format.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2" }));

        login_lbl8.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl8.setText("Format étiquette ferméture *");

        print_destination_check.setForeground(new java.awt.Color(255, 255, 255));
        print_destination_check.setText("Imprimer la destination dans la fiche de ferméture");
        print_destination_check.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                print_destination_checkStateChanged(evt);
            }
        });

        login_lbl6.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl6.setText("Destination finale");

        destination_filter.setBackground(new java.awt.Color(204, 204, 255));
        destination_filter.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        destination_filter.setMaximumSize(null);
        destination_filter.setMinimumSize(new java.awt.Dimension(37, 26));
        destination_filter.setPreferredSize(new java.awt.Dimension(37, 26));

        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_per_piece_checkbox)
                            .addComponent(print_destination_check)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(login_lbl6)
                                    .addGap(18, 18, 18)
                                    .addComponent(destination_filter, 0, 164, Short.MAX_VALUE))
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(pwd_lbl3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pwd_lbl4)
                                        .addComponent(pwd_lbl5))
                                    .addGap(36, 36, 36)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(pack_type_filter, 0, 155, Short.MAX_VALUE)
                                        .addComponent(pack_size_txtbox)
                                        .addComponent(nbreOfBoxes_txtbox)))))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(login_lbl8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(open_sheet_copies)
                                            .addComponent(closing_sheet_copies)))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(closing_sheet_format, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(login_lbl2)
                                .addGap(18, 18, 18)
                                .addComponent(barcodes_nbre_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(466, 466, 466))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(special_order_check)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pwd_lbl3)
                            .addComponent(pack_type_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pwd_lbl4)
                            .addComponent(pack_size_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pwd_lbl5)
                            .addComponent(nbreOfBoxes_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(login_lbl6)
                            .addComponent(destination_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addComponent(print_destination_check)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_per_piece_checkbox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(special_order_check))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(open_sheet_copies, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(closing_sheet_copies, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(barcodes_nbre_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(login_lbl2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(closing_sheet_format, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(login_lbl8))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(170, Short.MAX_VALUE))
        );

        tabbedPane_ucs_config.addTab("Conditionnement", jPanel4);
        jPanel4.getAccessibleContext().setAccessibleParent(tabbedPane_ucs_config);

        jPanel3.setBackground(new java.awt.Color(36, 65, 86));

        pwd_lbl9.setBackground(new java.awt.Color(36, 65, 86));
        pwd_lbl9.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl9.setText("Net Weight (kg)");

        netWeight_txtbox.setText("0.00");
        netWeight_txtbox.setName("fname_txtbox"); // NOI18N
        netWeight_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netWeight_txtboxActionPerformed(evt);
            }
        });

        pwd_lbl10.setBackground(new java.awt.Color(36, 65, 86));
        pwd_lbl10.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl10.setText("Gross Weight (kg)");

        grossWeight_txtbox.setText("0.00");
        grossWeight_txtbox.setName("fname_txtbox"); // NOI18N

        filler1.setBackground(new java.awt.Color(255, 255, 255));
        filler1.setOpaque(true);

        pwd_lbl12.setBackground(new java.awt.Color(36, 65, 86));
        pwd_lbl12.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl12.setText("Volume (m3)");

        volume_txtbox.setText("0.00");
        volume_txtbox.setName("fname_txtbox"); // NOI18N

        pwd_lbl8.setBackground(new java.awt.Color(36, 65, 86));
        pwd_lbl8.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl8.setText("Price *");

        price_txtbox.setText("0.00");
        price_txtbox.setName("fname_txtbox"); // NOI18N

        filler2.setBackground(new java.awt.Color(255, 255, 255));
        filler2.setOpaque(true);

        pwd_lbl13.setBackground(new java.awt.Color(36, 65, 86));
        pwd_lbl13.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl13.setText("Engineering Change");

        engChange_textArea.setColumns(20);
        engChange_textArea.setRows(5);
        engChange_textArea.setText("-");
        jScrollPane3.setViewportView(engChange_textArea);

        pwd_lbl14.setBackground(new java.awt.Color(36, 65, 86));
        pwd_lbl14.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl14.setText("Change Date");

        filler3.setBackground(new java.awt.Color(255, 255, 255));
        filler3.setOpaque(true);

        pwd_lbl11.setBackground(new java.awt.Color(36, 65, 86));
        pwd_lbl11.setForeground(new java.awt.Color(255, 255, 255));
        pwd_lbl11.setText("Article Description");

        articleDesc_textArea.setColumns(20);
        articleDesc_textArea.setRows(5);
        articleDesc_textArea.setText("-");
        jScrollPane2.setViewportView(articleDesc_textArea);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pwd_lbl10)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(pwd_lbl9))
                            .addComponent(pwd_lbl12, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pwd_lbl8, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(pwd_lbl11)
                        .addGap(18, 18, 18)))
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(netWeight_txtbox)
                    .addComponent(grossWeight_txtbox)
                    .addComponent(volume_txtbox)
                    .addComponent(price_txtbox)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(pwd_lbl14)
                        .addGap(56, 56, 56))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(pwd_lbl13)
                        .addGap(18, 18, 18)))
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(engChangeDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(83, 83, 83))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(filler2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pwd_lbl9)
                            .addComponent(netWeight_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pwd_lbl10)
                            .addComponent(grossWeight_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pwd_lbl12)
                            .addComponent(volume_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(pwd_lbl8))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(price_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pwd_lbl11)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 297, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pwd_lbl14)
                    .addComponent(engChangeDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pwd_lbl13)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(filler3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabbedPane_ucs_config.addTab("Paramétrage Engineering", jPanel3);

        fname_lbl1.setForeground(new java.awt.Color(255, 255, 255));
        fname_lbl1.setText("ID");

        id_lbl.setBackground(new java.awt.Color(153, 204, 255));
        id_lbl.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        id_lbl.setForeground(new java.awt.Color(255, 255, 255));
        id_lbl.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        id_lbl.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(save_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(duplicate_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancel_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)
                        .addComponent(delete_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(fname_lbl1)
                        .addGap(18, 18, 18)
                        .addComponent(id_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 940, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(tabbedPane_ucs_config, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(fname_lbl1)
                    .addComponent(id_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancel_btn)
                            .addComponent(delete_btn))
                        .addComponent(duplicate_btn)
                        .addComponent(save_btn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabbedPane_ucs_config, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void netWeight_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netWeight_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_netWeight_txtboxActionPerformed

    private void label_per_piece_checkboxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_label_per_piece_checkboxStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_label_per_piece_checkboxStateChanged

    private void special_order_checkStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_special_order_checkStateChanged

    }//GEN-LAST:event_special_order_checkStateChanged

    private void supplier_pn_txtbox_searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_supplier_pn_txtbox_searchKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_supplier_pn_txtbox_searchKeyTyped

    private void supplier_pn_txtbox_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_supplier_pn_txtbox_searchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_supplier_pn_txtbox_searchKeyPressed

    private void clear_search_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_search_btnActionPerformed
        clearSearchFields();
    }//GEN-LAST:event_clear_search_btnActionPerformed

    private void filter_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter_btnActionPerformed
        refresh();
    }//GEN-LAST:event_filter_btnActionPerformed

    private void segment_txtbox_searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_segment_txtbox_searchKeyTyped

    }//GEN-LAST:event_segment_txtbox_searchKeyTyped

    private void segment_txtbox_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_segment_txtbox_searchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_segment_txtbox_searchKeyPressed

    private void pack_type_txtbox_searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pack_type_txtbox_searchKeyTyped

    }//GEN-LAST:event_pack_type_txtbox_searchKeyTyped

    private void pack_type_txtbox_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pack_type_txtbox_searchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_pack_type_txtbox_searchKeyPressed

    private void cpn_txtbox_searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpn_txtbox_searchKeyTyped

    }//GEN-LAST:event_cpn_txtbox_searchKeyTyped

    private void cpn_txtbox_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpn_txtbox_searchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_cpn_txtbox_searchKeyPressed

    private void duplicate_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicate_btnActionPerformed
        id_lbl.setText("");
        aux.setHarnessPart(cpn_txtbox.getText());
        aux.setSupplierPartNumber(lpn_txtbox.getText());
        aux.setHarnessIndex(index_txtbox.getText());
        aux.setStdTime(Double.valueOf(stdTime_txtbox.getText()));
        aux.setPrice(Double.valueOf(price_txtbox.getText()));
        aux.setPackSize(Integer.valueOf(pack_size_txtbox.getText()));
        aux.setComment(comment_txt.getText());
        aux.setOrderNo(order_no_txt.getText());
        assy_txtbox.setText(aux.getAssyWorkstationName());
        barcodes_nbre_txtbox.setText(aux.getAdditionalBarcode() + "");
        nbreOfBoxes_txtbox.setText(aux.getLifes() + "");
        aux.setProject(project_filter.getSelectedItem().toString());
        aux.setWarehouse(fg_warehouse_filter.getSelectedItem().toString());
        aux.setPackaging_warehouse(packaging_wh_filter.getSelectedItem().toString());
        aux.setArticleDesc(articleDesc_textArea.getText());
        aux.setEngChange(engChange_textArea.getText());
        aux.setEngChangeDate(engChangeDatePicker.getDate());
        aux.setNetWeight(Double.valueOf(netWeight_txtbox.getText().trim()));
        aux.setGrossWeight(Double.valueOf(grossWeight_txtbox.getText().trim()));
        aux.setVolume(Double.valueOf(volume_txtbox.getText().trim()));
        aux.setPriority(Integer.valueOf(priority.getText()));
        aux.setOpenningSheetCopies(Integer.valueOf(open_sheet_copies.getText()));
        aux.setClosingSheetCopies(Integer.valueOf(closing_sheet_copies.getText()));
        if (aux.getSpecialOrder() == 1) {
            special_order_check.setSelected(true);
        } else {
            special_order_check.setSelected(false);
        }
        if (aux.getPrint_destination()) {
            print_destination_check.setSelected(true);
        } else {
            print_destination_check.setSelected(false);
        }

        try {
            if (aux.isLabelPerPiece()) {
                label_per_piece_checkbox.setSelected(true);
            } else {
                label_per_piece_checkbox.setSelected(false);
            }
        } catch (NullPointerException ex) {
            label_per_piece_checkbox.setSelected(false);
        }

        if (String.valueOf(aux.getActive()).equals("1")) {
            active_combobox.setSelectedIndex(0);
        } else {
            active_combobox.setSelectedIndex(1);
        }

        //----------------------------------------------------------------------
        // Combobox
        for (int i = 0; i < pack_type_filter.getItemCount(); i++) {
            if (pack_type_filter.getItemAt(i).toString().equals(aux.getPackType())) {
                pack_type_filter.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < segment_filter.getItemCount(); i++) {
            if (segment_filter.getItemAt(i).toString().equals(aux.getSegment())) {
                segment_filter.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < workplace_filter.getItemCount(); i++) {
            if (workplace_filter.getItemAt(i).toString().equals(aux.getWorkplace())) {
                workplace_filter.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < family_filter.getItemCount(); i++) {
            if (family_filter.getItemAt(i).toString().equals(aux.getHarnessType())) {
                family_filter.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < packaging_wh_filter.getItemCount(); i++) {
            if (packaging_wh_filter.getItemAt(i).toString().equals(aux.getPackaging_warehouse())) {
                packaging_wh_filter.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < destination_filter.getItemCount(); i++) {
            if (destination_filter.getItemAt(i).toString().equals(aux.getDestination())) {
                destination_filter.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < closing_sheet_format.getItemCount(); i++) {
////            System.out.println("Integer.valueOf(closing_sheet_format.getItemAt(i)) " + Integer.valueOf(closing_sheet_format.getItemAt(i)));
////            System.out.println("aux.getClosingSheetFormat() " + aux.getClosingSheetFormat());
            if (Integer.valueOf(closing_sheet_format.getItemAt(i)) == aux.getClosingSheetFormat()) {
                closing_sheet_format.setSelectedIndex(i);
                break;
            }
        }

        this.aux = null;
        tabbedPane_ucs_config.setSelectedIndex(1);
        msg_lbl.setText("Element dupliqué !");
    }//GEN-LAST:event_duplicate_btnActionPerformed

    private void delete_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_btnActionPerformed
        int confirmed = JOptionPane.showConfirmDialog(this,
                String.format("Confirmez-vous la suppression de cet élément [%s] ?",
                        this.aux.getId()),
                "Suppression UCS",
                JOptionPane.WARNING_MESSAGE);

        if (confirmed == 0) {
            aux.delete(aux);
            clearFields();
            msg_lbl.setText("Elément supprimé !");
            refresh();
        }
    }//GEN-LAST:event_delete_btnActionPerformed

    private void cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_btnActionPerformed
        clearFields();
    }//GEN-LAST:event_cancel_btnActionPerformed

    private void save_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_btnActionPerformed

        //######################################################################
        //
        //                           SAVE a new item
        //
        //######################################################################
        if (id_lbl.getText().isEmpty()) { // ID Label is empty, then is a new Object
            ConfigUcs cu = new ConfigUcs();
            boolean err = false;

            if (cpn_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid CPN.", "CPN Error");
                err = true;
                cpn_txtbox.requestFocus();
                cpn_txtbox.setBackground(Color.red);
            } else {
                cu.setHarnessPart(cpn_txtbox.getText().trim());
                cpn_txtbox.setBackground(Color.white);
            }
            if (lpn_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid LPN.", "LPN Error");
                err = true;
                lpn_txtbox.requestFocus();
                lpn_txtbox.setBackground(Color.red);
            } else {
                cu.setSupplierPartNumber(lpn_txtbox.getText().trim());
                lpn_txtbox.setBackground(Color.white);
            }

            if (index_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid index.", "Index Error");
                err = true;
                index_txtbox.requestFocus();
                index_txtbox.setBackground(Color.red);
            } else {
                cu.setHarnessIndex(index_txtbox.getText().trim());
                index_txtbox.setBackground(Color.white);
            }

            if (pack_type_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid pack type.", "Pack type Error");
                err = true;
                pack_type_filter.requestFocus();
                pack_type_filter.setBackground(Color.red);
            } else {
                cu.setPackType(pack_type_filter.getSelectedItem().toString());
                pack_type_filter.setBackground(Color.white);
            }
            if (pack_size_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid pack size.", "Pack size Error");
                err = true;
                pack_size_txtbox.requestFocus();
                pack_size_txtbox.setBackground(Color.red);
            } else {
                try {
                    cu.setPackSize(Integer.valueOf(pack_size_txtbox.getText().trim()));
                    pack_size_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for pack size.", "Number format error.");
                    err = true;
                    pack_size_txtbox.requestFocus();
                    pack_size_txtbox.setBackground(Color.red);
                }
            }
            if (segment_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid segment.", "Segment Error");
                err = true;
                segment_filter.requestFocus();
                segment_filter.setBackground(Color.red);
            } else {
                cu.setSegment(segment_filter.getSelectedItem().toString());
                segment_filter.setBackground(Color.white);
            }
            if (workplace_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid workplace.", "Workplace Error");
                err = true;
                workplace_filter.requestFocus();
                workplace_filter.setBackground(Color.red);
            } else {
                cu.setWorkplace(workplace_filter.getSelectedItem().toString());
                workplace_filter.setBackground(Color.white);
            }
            if (family_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid harness type.", "Harness type Error");
                err = true;
                family_filter.requestFocus();
                family_filter.setBackground(Color.red);
            } else {
                cu.setHarnessType(family_filter.getSelectedItem().toString());
                family_filter.setBackground(Color.white);
            }
            if (nbreOfBoxes_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Number of pack cannot be null.", "Number of packs Error");
                err = true;
                nbreOfBoxes_txtbox.requestFocus();
                nbreOfBoxes_txtbox.setBackground(Color.red);
            } else {
                try {
                    cu.setLifes(Integer.valueOf(nbreOfBoxes_txtbox.getText().trim()));
                    nbreOfBoxes_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for number of packs.", "Number format error.");
                    err = true;
                    nbreOfBoxes_txtbox.requestFocus();
                    nbreOfBoxes_txtbox.setBackground(Color.red);
                }
            }
            if (stdTime_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Assembly standard time cannot be null.", "Standard time Error");
                err = true;
                stdTime_txtbox.requestFocus();
                stdTime_txtbox.setBackground(Color.red);
            } 
            else if (cra_stdTime_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Cutting & Lead Preparation standard time cannot be null.", "Standard time Error");
                err = true;
                cra_stdTime_txtbox.requestFocus();
                cra_stdTime_txtbox.setBackground(Color.red);
            } 
            else {
                //Assembly standard time
                try {
                    cu.setStdTime(Double.valueOf(stdTime_txtbox.getText().trim()));                    
                    stdTime_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for standard time.", "Number format error.");
                    err = true;
                    stdTime_txtbox.requestFocus();
                    stdTime_txtbox.setBackground(Color.red);
                }
                // Cutting & LeadPrep standard time
                try {
                    cu.setCraStdTime(Double.valueOf(cra_stdTime_txtbox.getText().trim()));                    
                    cra_stdTime_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for standard time.", "Number format error.");
                    err = true;
                    cra_stdTime_txtbox.requestFocus();
                    cra_stdTime_txtbox.setBackground(Color.red);
                }
            }
            if (price_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Price cannot be null.", "Price Error");
                err = true;
                price_txtbox.requestFocus();
                price_txtbox.setBackground(Color.red);
            } else {
                try {
                    cu.setPrice(Double.valueOf(price_txtbox.getText().trim()));
                    price_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for price.", "Number format error.");
                    err = true;
                    price_txtbox.requestFocus();
                    price_txtbox.setBackground(Color.red);
                }
            }
            if (netWeight_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Net weight cannot be null.", "Net weight Error");
                err = true;
                netWeight_txtbox.requestFocus();
                netWeight_txtbox.setBackground(Color.red);
            } else {
                try {
                    cu.setNetWeight(Double.valueOf(netWeight_txtbox.getText().trim()));
                    netWeight_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for net weight.", "Number format error.");
                    err = true;
                    netWeight_txtbox.requestFocus();
                    netWeight_txtbox.setBackground(Color.red);
                }
            }
            if (barcodes_nbre_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Additional barcodes number cannot be null.", "Barecode number Error");
                err = true;
                barcodes_nbre_txtbox.requestFocus();
                barcodes_nbre_txtbox.setBackground(Color.red);
            } else {
                try {
                    cu.setAdditionalBarcode(Integer.valueOf(barcodes_nbre_txtbox.getText().trim()));
                    barcodes_nbre_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for additional barecodes number.", "Number format error.");
                    err = true;
                    barcodes_nbre_txtbox.requestFocus();
                    barcodes_nbre_txtbox.setBackground(Color.red);
                }
            }

            try {
                if (engChangeDatePicker.getDate().toString().isEmpty()) {
                    UILog.severeDialog(this, "Empty date error for engineering change date.", "Date format error.");
                    err = true;
                    engChangeDatePicker.requestFocus();
                    engChangeDatePicker.setBackground(Color.red);
                } else {
                    cu.setEngChangeDate(engChangeDatePicker.getDate());
                    engChangeDatePicker.setBackground(Color.white);
                }
            } catch (NullPointerException | NumberFormatException ex) {
                UILog.severeDialog(this, "Date format error for engineering change date.", "Date format error.");
                err = true;
                engChangeDatePicker.requestFocus();
                engChangeDatePicker.setBackground(Color.red);
            }
            cu.setCreateId(PackagingVars.context.getUser().getId());
            cu.setWriteId(PackagingVars.context.getUser().getId());
            cu.setCreateTime(new Date());
            cu.setWriteTime(new Date());
            cu.setActive(Integer.valueOf(active_combobox.getSelectedItem().toString()));
            cu.setProject(project_filter.getSelectedItem().toString());
            cu.setWarehouse(fg_warehouse_filter.getSelectedItem().toString());
            cu.setPackaging_warehouse(packaging_wh_filter.getSelectedItem().toString());

            cu.setDestination(destination_filter.getSelectedItem().toString());
            cu.setArticleDesc((articleDesc_textArea.getText().length() > 25) ? articleDesc_textArea.getText().substring(0, 25) : articleDesc_textArea.getText());
            cu.setEngChange((engChange_textArea.getText().length() > 25) ? engChange_textArea.getText().substring(0, 25) : engChange_textArea.getText());
            cu.setGrossWeight(Double.valueOf(grossWeight_txtbox.getText().trim()));
            cu.setVolume(Double.valueOf(volume_txtbox.getText().trim()));
            cu.setPriority(Integer.valueOf(priority.getText()));
            cu.setOpenningSheetCopies(Integer.valueOf(open_sheet_copies.getText()));
            cu.setClosingSheetCopies(Integer.valueOf(closing_sheet_copies.getText()));
            if (assy_txtbox.getText().isEmpty()) {
                cu.setAssyWorkstationName("-");
            } else {
                cu.setAssyWorkstationName(assy_txtbox.getText().trim());
            }
            cu.setComment(comment_txt.getText());
            if (order_no_txt.getText().isEmpty()) {
                cu.setOrderNo("-");
            } else {
                cu.setOrderNo(order_no_txt.getText());
            }
            if (special_order_check.isSelected()) {
                cu.setSpecialOrder(1);
            } else {
                cu.setSpecialOrder(0);
            }
            if (print_destination_check.isSelected()) {
                cu.setPrint_destination(true);
            } else {
                cu.setPrint_destination(false);
            }
            if (label_per_piece_checkbox.isSelected()) {
                cu.setLabelPerPiece(true);
            } else {
                cu.setLabelPerPiece(false);
            }
////            System.out.println("FORM validation : " + err);
////            System.out.println("New object " + mu.toString());
            if (!err) {
                int newId = cu.create(cu);
                String[] msg = {"Nouveau élément " + newId + " enregistré !"};
                clearFields();
                msg_lbl.setText(msg[0]);
                //UILog.infoDialog(this, msg);
                refresh();
            }
        } //######################################################################
        //
        //                    UPDATE an existing item
        //
        //######################################################################
        else { // ID Label is filed, then is an update

            boolean err = false;

            if (cpn_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid CPN.", "CPN Error");
                err = true;
                cpn_txtbox.requestFocus();
                cpn_txtbox.setBackground(Color.red);

            } else {
                aux.setHarnessPart(cpn_txtbox.getText().trim());
                cpn_txtbox.setBackground(Color.white);
            }
            if (lpn_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid LPN.", "LPN Error");
                err = true;
                lpn_txtbox.requestFocus();
                lpn_txtbox.setBackground(Color.red);
            } else {
                aux.setSupplierPartNumber(lpn_txtbox.getText().trim());
                lpn_txtbox.setBackground(Color.white);
            }

            if (index_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid index.", "Index Error");
                err = true;
                index_txtbox.requestFocus();
                index_txtbox.setBackground(Color.red);
            } else {
                aux.setHarnessIndex(index_txtbox.getText().trim());
                index_txtbox.setBackground(Color.white);
            }

            if (pack_type_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid pack type.", "Pack type Error");
                err = true;
                pack_type_filter.requestFocus();
                pack_type_filter.setBackground(Color.red);
            } else {
                aux.setPackType(pack_type_filter.getSelectedItem().toString());
                pack_type_filter.setBackground(Color.white);
            }
            if (pack_size_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid pack size.", "Pack size Error");
                err = true;
                pack_size_txtbox.requestFocus();
                pack_size_txtbox.setBackground(Color.red);
            } else {
                if (Integer.valueOf(pack_size_txtbox.getText()) <= 0) {
                    UILog.severeDialog(this, "Negative number not allowed for pack size.", "Number format error.");
                    err = true;
                    pack_size_txtbox.requestFocus();
                    pack_size_txtbox.setBackground(Color.red);
                } else {
                    try {
                        aux.setPackSize(Integer.valueOf(pack_size_txtbox.getText().trim()));
                        pack_size_txtbox.setBackground(Color.white);
                    } catch (java.lang.NumberFormatException ex) {
                        UILog.severeDialog(this, "Number format error for pack size.", "Number format error.");
                        err = true;
                        pack_size_txtbox.requestFocus();
                        pack_size_txtbox.setBackground(Color.red);
                    }
                }
            }
            if (segment_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid segment.", "Segment Error");
                err = true;
                segment_filter.requestFocus();
                segment_filter.setBackground(Color.red);
            } else {
                aux.setSegment(segment_filter.getSelectedItem().toString());
                segment_filter.setBackground(Color.white);
            }
            if (workplace_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid workplace.", "Workplace Error");
                err = true;
                workplace_filter.requestFocus();
                workplace_filter.setBackground(Color.red);
            } else {
                aux.setWorkplace(workplace_filter.getSelectedItem().toString());
                workplace_filter.setBackground(Color.white);
            }
            if (family_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid harness type.", "Harness type Error");
                err = true;
                family_filter.requestFocus();
                family_filter.setBackground(Color.red);
            } else {
                aux.setHarnessType(family_filter.getSelectedItem().toString());
                family_filter.setBackground(Color.white);
            }
            if (nbreOfBoxes_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Number of pack cannot be null.", "Number of packs Error");
                err = true;
                nbreOfBoxes_txtbox.requestFocus();
                nbreOfBoxes_txtbox.setBackground(Color.red);

            } else {
                try {
                    aux.setLifes(Integer.valueOf(nbreOfBoxes_txtbox.getText().trim()));
                    nbreOfBoxes_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for number of packs.", "Number format error.");
                    err = true;
                    nbreOfBoxes_txtbox.requestFocus();
                    nbreOfBoxes_txtbox.setBackground(Color.red);
                }
            }
            
            if (stdTime_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Assembly standard time cannot be null.", "Standard time Error");
                err = true;
                stdTime_txtbox.requestFocus();
                stdTime_txtbox.setBackground(Color.red);
            } 
            else if (cra_stdTime_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Cutting & Lead Preparation standard time cannot be null.", "Standard time Error");
                err = true;
                cra_stdTime_txtbox.requestFocus();
                cra_stdTime_txtbox.setBackground(Color.red);
            } 
            else {
                //Assembly standard time
                try {
                    aux.setStdTime(Double.valueOf(stdTime_txtbox.getText().trim()));                    
                    stdTime_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for standard time.", "Number format error.");
                    err = true;
                    stdTime_txtbox.requestFocus();
                    stdTime_txtbox.setBackground(Color.red);
                }
                // Cutting & LeadPrep standard time
                try {
                    aux.setCraStdTime(Double.valueOf(cra_stdTime_txtbox.getText().trim()));                    
                    cra_stdTime_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for standard time.", "Number format error.");
                    err = true;
                    cra_stdTime_txtbox.requestFocus();
                    cra_stdTime_txtbox.setBackground(Color.red);
                }
            }
            
            if (price_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Price cannot be null.", "Price Error");
                err = true;
                price_txtbox.requestFocus();
                price_txtbox.setBackground(Color.red);
            } else {
                try {
                    aux.setPrice(Double.valueOf(price_txtbox.getText().trim()));
                    price_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for price.", "Number format error.");
                    err = true;
                    price_txtbox.requestFocus();
                    price_txtbox.setBackground(Color.red);
                }
            }
            if (netWeight_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Net weight cannot be null.", "Net weight Error");
                err = true;
                netWeight_txtbox.requestFocus();
                netWeight_txtbox.setBackground(Color.red);
            } else {
                try {
                    aux.setNetWeight(Double.valueOf(netWeight_txtbox.getText().trim()));
                    netWeight_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for net weight.", "Number format error.");
                    err = true;
                    netWeight_txtbox.requestFocus();
                    netWeight_txtbox.setBackground(Color.red);
                }
            }
            if (barcodes_nbre_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Additional barcodes number cannot be null.", "Barecode number Error");
                err = true;
                barcodes_nbre_txtbox.requestFocus();
                barcodes_nbre_txtbox.setBackground(Color.red);
            } else {
                try {
                    aux.setAdditionalBarcode(Integer.valueOf(barcodes_nbre_txtbox.getText().trim()));
                    barcodes_nbre_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for additional barecodes number.", "Number format error.");
                    err = true;
                    barcodes_nbre_txtbox.requestFocus();
                    barcodes_nbre_txtbox.setBackground(Color.red);
                }
            }

            try {
                if (engChangeDatePicker.getDate().toString().isEmpty()) {
                    UILog.severeDialog(this, "Empty date error for engineering change date.", "Date format error.");
                    err = true;
                    engChangeDatePicker.requestFocus();
                    engChangeDatePicker.setBackground(Color.red);
                } else {
                    aux.setEngChangeDate(engChangeDatePicker.getDate());
                    engChangeDatePicker.setBackground(Color.white);
                }
            } catch (NullPointerException | NumberFormatException ex) {
                UILog.severeDialog(this, "Date format error for engineering change date.", "Date format error.");
                err = true;
                engChangeDatePicker.requestFocus();
                engChangeDatePicker.setBackground(Color.red);
            }

            // Priorité
            if (priority.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Priority cannot be null.", "Barecode number Error");
                err = true;
                priority.requestFocus();
                priority.setBackground(Color.red);
            } else {
                if (Integer.valueOf(priority.getText()) < 0) {
                    UILog.severeDialog(this, "Negative number not allowed for priority.", "Number format error.");
                    err = true;
                    priority.requestFocus();
                    priority.setBackground(Color.red);
                } else {
                    try {
                        aux.setPriority(Integer.valueOf(priority.getText()));
                        priority.setBackground(Color.white);
                    } catch (java.lang.NumberFormatException ex) {
                        UILog.severeDialog(this, "Number format error for priority.", "Number format error.");
                        err = true;
                        priority.requestFocus();
                        priority.setBackground(Color.red);
                    }
                }
            }

            // Open sheet copies
            if (open_sheet_copies.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Open sheet copies field cannot be null.", "Barecode number Error");
                err = true;
                open_sheet_copies.requestFocus();
                open_sheet_copies.setBackground(Color.red);
            } else {
                if (Integer.valueOf(open_sheet_copies.getText()) <= 0) {
                    UILog.severeDialog(this, "Negative number not allowed for Open sheet copies field.", "Number format error.");
                    err = true;
                    open_sheet_copies.requestFocus();
                    open_sheet_copies.setBackground(Color.red);
                } else {

                    try {
                        aux.setOpenningSheetCopies(Integer.valueOf(open_sheet_copies.getText()));
                        open_sheet_copies.setBackground(Color.white);
                    } catch (java.lang.NumberFormatException ex) {
                        UILog.severeDialog(this, "Number format error for Open sheet copies field.", "Number format error.");
                        err = true;
                        open_sheet_copies.requestFocus();
                        open_sheet_copies.setBackground(Color.red);
                    }
                }
            }

            // Closing sheet copies
            if (closing_sheet_copies.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Closing sheet copies field cannot be null.", "Barecode number Error");
                err = true;
                closing_sheet_copies.requestFocus();
                closing_sheet_copies.setBackground(Color.red);
            } else {
                if (Integer.valueOf(closing_sheet_copies.getText()) <= 0) {
                    UILog.severeDialog(this, "Negative number not allowed for closing sheet copies field.", "Number format error.");
                    err = true;
                    closing_sheet_copies.requestFocus();
                    closing_sheet_copies.setBackground(Color.red);
                } else {
                    try {
                        aux.setClosingSheetCopies(Integer.valueOf(closing_sheet_copies.getText()));
                        closing_sheet_copies.setBackground(Color.white);
                    } catch (java.lang.NumberFormatException ex) {
                        UILog.severeDialog(this, "Number format error for closing sheet copies field.", "Number format error.");
                        err = true;
                        closing_sheet_copies.requestFocus();
                        closing_sheet_copies.setBackground(Color.red);
                    }
                }
            }

            if (assy_txtbox.getText().isEmpty()) {
                aux.setAssyWorkstationName("-");
            } else {
                aux.setAssyWorkstationName(assy_txtbox.getText().trim());
            }
            aux.setComment(comment_txt.getText());
            if (order_no_txt.getText().isEmpty()) {
                aux.setOrderNo("-");
            } else {
                aux.setOrderNo(order_no_txt.getText());
            }
            if (special_order_check.isSelected()) {
                aux.setSpecialOrder(1);
            } else {
                aux.setSpecialOrder(0);
            }
            if (label_per_piece_checkbox.isSelected()) {
                aux.setLabelPerPiece(true);
            } else {
                aux.setLabelPerPiece(false);
            }
            
            if (print_destination_check.isSelected()) {
                aux.setPrint_destination(true);
            } else {
                aux.setPrint_destination(false);
            }
            aux.setWriteId(PackagingVars.context.getUser().getId());
            aux.setWriteTime(new Date());
            aux.setActive(Integer.valueOf(active_combobox.getSelectedItem().toString()));
            aux.setProject(project_filter.getSelectedItem().toString());
            aux.setWarehouse(fg_warehouse_filter.getSelectedItem().toString());
            aux.setPackaging_warehouse(packaging_wh_filter.getSelectedItem().toString());
            aux.setDestination(destination_filter.getSelectedItem().toString());
            aux.setClosingSheetFormat(Integer.valueOf(closing_sheet_format.getSelectedItem().toString()));
            aux.setArticleDesc(articleDesc_textArea.getText());
            aux.setEngChange(engChange_textArea.getText());
            aux.setGrossWeight(Double.valueOf(grossWeight_txtbox.getText().trim()));
            aux.setVolume(Double.valueOf(volume_txtbox.getText().trim()));

//////            System.out.println("FORM validation : " + err);
//////            System.out.println("Object updated" + aux.toString());
            if (!err) {
                aux.update(aux);
                clearFields();
                String[] msg = {"Changements enregistrés"};
                msg_lbl.setText(msg[0]);
                //UILog.infoDialog(this, msg);
                refresh();
            }

        }
    }//GEN-LAST:event_save_btnActionPerformed

    private void stdTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stdTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stdTime_txtboxActionPerformed

    private void active_comboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_active_comboboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_active_comboboxActionPerformed

    private void workplace_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplace_filterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_workplace_filterActionPerformed

    private void workplace_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_workplace_filterItemStateChanged

    }//GEN-LAST:event_workplace_filterItemStateChanged

    private void segment_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segment_filterActionPerformed
//////        System.out.println("Selected Segment " + String.valueOf(segment_filter.getSelectedItem()));
        if ("ALL".equals(String.valueOf(segment_filter.getSelectedItem()).trim())) {
            this.workplace_filter.setSelectedIndex(0);
            this.workplace_filter.setEnabled(false);
        } else {
            this.workplace_filter.removeAllItems();
            this.workplace_filter.setEnabled(true);
            this.workplace_filter = ConfigWorkplace.initWorkplaceJBox(this, workplace_filter, String.valueOf(segment_filter.getSelectedItem()), false);
        }
    }//GEN-LAST:event_segment_filterActionPerformed

    private void segment_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_segment_filterItemStateChanged

    }//GEN-LAST:event_segment_filterItemStateChanged

    private void fg_warehouse_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fg_warehouse_filterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fg_warehouse_filterActionPerformed

    private void fg_warehouse_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fg_warehouse_filterItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_fg_warehouse_filterItemStateChanged

    private void project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_filterActionPerformed
        if ("ALL".equals(String.valueOf(segment_filter.getSelectedItem()).trim())) {
            this.segment_filter.setSelectedIndex(0);
            this.segment_filter.setEnabled(false);
        } else {
            fg_warehouse_filter = ConfigWarehouse.initWarehouseJBox(this, fg_warehouse_filter, String.valueOf(project_filter.getSelectedItem()), String.valueOf(GlobalVars.WarehouseType.FINISHED_GOODS),"", false);
            packaging_wh_filter = ConfigWarehouse.initWarehouseJBox(this, packaging_wh_filter, String.valueOf(project_filter.getSelectedItem()), String.valueOf(GlobalVars.WarehouseType.PACKAGING),"", false);
            LoadPlanDestination.setDestinationByProject(this, destination_filter, String.valueOf(project_filter.getSelectedItem()) );
            JComboBox box = ConfigSegment.setSegmentByProject(this, segment_filter, String.valueOf(project_filter.getSelectedItem()), false);
            if (box.getItemCount()>0) {
                workplace_filter = ConfigWorkplace.initWorkplaceJBox(this, workplace_filter, String.valueOf(segment_filter.getSelectedItem()), false);  
                family_filter = ConfigFamily.initFamilyByProject(this, family_filter, String.valueOf(project_filter.getSelectedItem()));
            }
        }
    }//GEN-LAST:event_project_filterActionPerformed


    private void project_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_project_filterItemStateChanged

    }//GEN-LAST:event_project_filterItemStateChanged

    private void comment_txtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comment_txtFocusGained
//        comment_txt.setText("");
    }//GEN-LAST:event_comment_txtFocusGained

    private void packaging_wh_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packaging_wh_filterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_packaging_wh_filterActionPerformed

    private void supplier_pn_txtbox_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supplier_pn_txtbox_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_supplier_pn_txtbox_searchActionPerformed

    private void project_txtbox_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_project_txtbox_searchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_project_txtbox_searchKeyPressed

    private void project_txtbox_searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_project_txtbox_searchKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_project_txtbox_searchKeyTyped

    private void pack_type_txtbox_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pack_type_txtbox_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pack_type_txtbox_searchActionPerformed

    private void print_destination_checkStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_print_destination_checkStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_print_destination_checkStateChanged

    private void cra_stdTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cra_stdTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cra_stdTime_txtboxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox active_combobox;
    private javax.swing.JTextArea articleDesc_textArea;
    private javax.swing.JTextField assy_txtbox;
    private javax.swing.JTextField barcodes_nbre_txtbox;
    private javax.swing.JButton cancel_btn;
    private javax.swing.JButton clear_search_btn;
    private javax.swing.JTextField closing_sheet_copies;
    private javax.swing.JComboBox<String> closing_sheet_format;
    private javax.swing.JTextArea comment_txt;
    private javax.swing.JTextField cpn_txtbox;
    private javax.swing.JTextField cpn_txtbox_search;
    private javax.swing.JTextField cra_stdTime_txtbox;
    private javax.swing.JTextField create_time_txt;
    private javax.swing.JButton delete_btn;
    private javax.swing.JComboBox destination_filter;
    private javax.swing.JButton duplicate_btn;
    private org.jdesktop.swingx.JXDatePicker engChangeDatePicker;
    private javax.swing.JTextArea engChange_textArea;
    private javax.swing.JComboBox family_filter;
    private javax.swing.JComboBox fg_warehouse_filter;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton filter_btn;
    private javax.swing.JLabel fname_lbl;
    private javax.swing.JLabel fname_lbl1;
    private javax.swing.JLabel fname_lbl14;
    private javax.swing.JLabel fname_lbl_search;
    private javax.swing.JTextField grossWeight_txtbox;
    private javax.swing.JLabel id_lbl;
    private javax.swing.JTextField index_txtbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JCheckBox label_per_piece_checkbox;
    private javax.swing.JLabel llogin_lbl_search;
    private javax.swing.JLabel llogin_lbl_search1;
    private javax.swing.JLabel llogin_lbl_search2;
    private javax.swing.JLabel lname_lbl;
    private javax.swing.JLabel lname_lbl1;
    private javax.swing.JLabel lname_lbl2;
    private javax.swing.JLabel lname_lbl_search;
    private javax.swing.JLabel login_lbl;
    private javax.swing.JLabel login_lbl1;
    private javax.swing.JLabel login_lbl2;
    private javax.swing.JLabel login_lbl3;
    private javax.swing.JLabel login_lbl4;
    private javax.swing.JLabel login_lbl5;
    private javax.swing.JLabel login_lbl6;
    private javax.swing.JLabel login_lbl7;
    private javax.swing.JLabel login_lbl8;
    private javax.swing.JTextField lpn_txtbox;
    private javax.swing.JLabel msg_lbl;
    private javax.swing.JTextField nbreOfBoxes_txtbox;
    private javax.swing.JTextField netWeight_txtbox;
    private javax.swing.JTextField open_sheet_copies;
    private javax.swing.JTextField order_no_txt;
    private javax.swing.JTextField pack_size_txtbox;
    private javax.swing.JComboBox pack_type_filter;
    private javax.swing.JTextField pack_type_txtbox_search;
    private javax.swing.JComboBox packaging_wh_filter;
    private javax.swing.JTextField price_txtbox;
    private javax.swing.JCheckBox print_destination_check;
    private javax.swing.JTextField priority;
    private javax.swing.JComboBox project_filter;
    private javax.swing.JTextField project_txtbox_search;
    private javax.swing.JLabel pwd_lbl;
    private javax.swing.JLabel pwd_lbl1;
    private javax.swing.JLabel pwd_lbl10;
    private javax.swing.JLabel pwd_lbl11;
    private javax.swing.JLabel pwd_lbl12;
    private javax.swing.JLabel pwd_lbl13;
    private javax.swing.JLabel pwd_lbl14;
    private javax.swing.JLabel pwd_lbl15;
    private javax.swing.JLabel pwd_lbl2;
    private javax.swing.JLabel pwd_lbl3;
    private javax.swing.JLabel pwd_lbl4;
    private javax.swing.JLabel pwd_lbl5;
    private javax.swing.JLabel pwd_lbl6;
    private javax.swing.JLabel pwd_lbl7;
    private javax.swing.JLabel pwd_lbl8;
    private javax.swing.JLabel pwd_lbl9;
    private javax.swing.JButton save_btn;
    private javax.swing.JComboBox segment_filter;
    private javax.swing.JTextField segment_txtbox_search;
    private javax.swing.JCheckBox special_order_check;
    private javax.swing.JTextField stdTime_txtbox;
    private javax.swing.JTextField supplier_pn_txtbox_search;
    private javax.swing.JTabbedPane tabbedPane_ucs_config;
    private javax.swing.JTable ucs_table;
    private javax.swing.JScrollPane user_table_scroll;
    private javax.swing.JTextField volume_txtbox;
    private javax.swing.JComboBox workplace_filter;
    private javax.swing.JTextField write_time_txt;
    // End of variables declaration//GEN-END:variables

}
