/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.warehouse_dispatch;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import entity.BaseContainer;
import entity.LoadPlan;
import entity.LoadPlanDestinationRel;
import entity.LoadPlanDispatchLabel;
import entity.LoadPlanLine;
import entity.ManufactureUsers;
import entity.PackagingStockMovement;
import gui.packaging.PackagingVars;
import gui.packaging.reports.PACKAGING_UI0010_PalletDetails_JPANEL;
import gui.packaging.reports.PACKAGING_UI0021_FINISHED_GOODS_STOCK_JPANEL;
import gui.warehouse_dispatch.process_control_labels.ControlState;
import gui.warehouse_dispatch.process_control_labels.S001_PalletNumberScan;
import gui.warehouse_dispatch.process_reservation.ReservationState;
import helper.JDialogExcelFileChooser;
import gui.warehouse_dispatch.state.WarehouseHelper;
import helper.HQLHelper;
import helper.Helper;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import static java.awt.Event.DELETE;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.hibernate.Query;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import ui.UILog;
import ui.error.ErrorMsg;
import gui.warehouse_dispatch.process_reservation.S001_ReservPalletNumberScan;
import gui.packaging.reports.PACKAGING_UI0020_PALLET_LIST_JPANEL;
import helper.JTableHelper;
import java.awt.GridLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author user
 */
public final class WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL extends javax.swing.JPanel implements ActionListener,
        PropertyChangeListener {

    JTabbedPane parent;
    ReservationState state = WarehouseHelper.warehouse_reserv_context.getState();
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector<String> load_plan_lines_table_header = new Vector<String>();
    Vector<String> load_plan_table_header = new Vector<String>();
    Vector load_plan_lines_table_data = new Vector();
    Vector load_plan_table_data = new Vector();
    Vector total_per_dest_table_data = new Vector();
    Vector odette_list_data = new Vector();
    Vector odette_list_header = new Vector<String>(Arrays.asList("Destination", "Article", "Quantite", "NumSerie"));
    Vector total_per_dest_table_data_header = new Vector<String>();

    //Used in tab 3
    Vector total_packages_data = new Vector();
    Vector<String> total_packages_header = new Vector<String>() {
    };

    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector load_plan_lines_data = new Vector();
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector load_plan_data = new Vector();
    String initTextValue = "...................";
    int DESTINATION_COMLUMN = 7;
    int LINE_ID_COMLUMN = 8;
    int PALLET_NUM_COLUMN = 1;
    static int destIndex = 0;
    static String selectedDestination = "";
    JRadioButton[] radioButtonList;
    private ReleasingJob task;

    private ManufactureUsers u = new ManufactureUsers();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getSelectedDestination() {
        return selectedDestination;
    }

    /**
     * Insert new record into load_plan_dispatch_label table
     *
     * @param record
     */
    private void insertDispatchLabelLine(String planId, CSVRecord record) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.                
        LoadPlanDispatchLabel item = new LoadPlanDispatchLabel(record.get("Destination"), record.get("Article"), Double.valueOf(record.get("Quantite")), Integer.valueOf(planId), record.get("NumSerie"));
        item.create(item);
        //System.out.println("New record inserted");
    }

    public void reset_odette_table_content() {
        odette_list_data = new Vector();
        jtable_odette_labels.setModel(new DefaultTableModel(odette_list_data, odette_list_header));
    }

    private void refreshOdetteTable() {
        reset_odette_table_content();

        Helper.startSession();

        String query_str = String.format(
                HQLHelper.GET_LOAD_PLAN_DISPATCH_LABELS_NOT_YET_CHECKED,
                Integer.valueOf(plan_num_label.getText()),
                Integer.valueOf(plan_num_label.getText()));

        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        query
                .addScalar("DESTINATION", StandardBasicTypes.STRING)
                .addScalar("ARTICLE", StandardBasicTypes.STRING)
                .addScalar("QUANTITE", StandardBasicTypes.DOUBLE)
                .addScalar("NUM_SERIE", StandardBasicTypes.STRING);
        List<Object[]> result = query.list();
        Helper.sess.getTransaction().commit();
        
        //Get the total of packages
        tab4_txt_totalPackages.setText(result.size()+"");
        
        //Populate the jTable with lines
        for (Object[] obj : result) {
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add((String) obj[0]);
            oneRow.add((String) obj[1]);
            oneRow.add(String.format("%1$,.2f", obj[2]));
            oneRow.add((String) obj[3]);

            odette_list_data.add(oneRow);
        }
        

        jtable_odette_labels.setModel(new DefaultTableModel(odette_list_data, odette_list_header));
    }

    private boolean deleteOdetteTable() {
        int confirmed = JOptionPane.showConfirmDialog(
                this.parent.getParent(),
                "La liste des odettes actuelles sera écrasée " + plan_num_label.getText() + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        
        
        if (confirmed == 0) {
            Helper.startSession();
            Query query = Helper.sess.createQuery(HQLHelper.DEL_LOAD_PLAN_DISPATCH_LABELS_BY_PLAN_ID);
            query.setParameter("loadPlanId", Integer.valueOf(plan_num_label.getText()));
            int result = query.executeUpdate();
            //Helper.sess.getTransaction().commit();
            JOptionPane.showMessageDialog(null, "La liste a été écrasée !\n");

            refreshOdetteTable();
            return true;
        }

        return false;
    }

    class ReleasingJob extends SwingWorker<Void, Void> {

        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            Random random = new Random();

            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            //close_plan_btn.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            JOptionPane.showMessageDialog(null, "Plan released !\n");

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Creates new form NewJFrame
     */
    public WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL() {
        initComponents();
    }

    public WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL(Object[] context, JFrame parent) {
        this.parent = (JTabbedPane) context[0];
        initComponents();
        //Initialiser les valeurs globales de test (Pattern Liste,...)
        Helper.startSession();

        initGui();

    }

    /**
     * Charge les destinations finales du plan sous forme de radioButton
     */
    private boolean loadDestinationsRadioGroup(int loadPlanId) {
        System.out.println("Start loadDestinationsRadioGroup ");
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.LOAD_PLAN_DEST_BY_PLAN_ID);
        query.setParameter("loadPlanId", loadPlanId);
        Helper.sess.getTransaction().commit();
        List result = query.list();
        if (result.isEmpty()) {
            UILog.info(ErrorMsg.APP_ERR0025[0]);
            UILog.infoDialog(null, ErrorMsg.APP_ERR0025);
            return false;
        } else {
            //Remouve all items from jpanel_destinations
            destIndex = 0;
            jpanel_destinations.removeAll();
            jpanel_destinations.setLayout(new GridLayout(0, 6));
            ButtonGroup group = new ButtonGroup();
            radioButtonList = new JRadioButton[result.size()];
            System.out.println("radioButtonList " + radioButtonList.length);
            System.out.println(radioButtonList.toString());

            //Map destinations data in the list
            for (Object o : result) {
                System.out.println("Rendering radioButton " + destIndex);
                LoadPlanDestinationRel lp = (LoadPlanDestinationRel) o;
                radioButtonList[destIndex] = new JRadioButton(lp.getDestination(), false);

                if (destIndex == 0) {
                    radioButtonList[destIndex].setSelected(true);
                    selectedDestination = radioButtonList[destIndex].getText();

                }

                radioButtonList[destIndex].addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent evt) {
                        JRadioButton button = (JRadioButton) evt.getSource();
                        String command = button.getActionCommand();
                        if (null != button && button.isSelected()) {
                            // do something with the button
                            button.getText();
                            selectedDestination = button.getText();
                            destination_label_help.setText(button.getText());
                        }
                        filterPlanLines(false);
                    }
                });

                destination_label_help.setText(this.selectedDestination);

                System.out.println("Add " + radioButtonList[destIndex] + " to group.");
                group.add(radioButtonList[destIndex]);
                jpanel_destinations.add(radioButtonList[destIndex]);
                jpanel_destinations.revalidate();
                jpanel_destinations.repaint();
                destIndex++;
            }
            System.out.println("End loadDestinationsRadioGroup");
            return true;
        }
    }

    /**
     * 
     */
    private void initPlanTableDoubleClick() {

        this.load_plan_lines_table.addMouseListener(
                new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    GlobalMethods.addNewTabToParent("Détails palette ", parent,
                            new PACKAGING_UI0010_PalletDetails_JPANEL(parent,
                                    String.valueOf(
                                            load_plan_lines_table.getValueAt(
                                                    load_plan_lines_table.getSelectedRow(),
                                                    PALLET_NUM_COLUMN)), "", 1, true, true, true
                            ), evt);
                }
            }
        }
        );

        this.load_plan_table.addMouseListener(
                new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadPlanDataInGui();
                }
            }
            
            
            /**
             * Load plan data into the UI fields
             */
            public void loadPlanDataInGui() {
                String id = String.valueOf(load_plan_table.getValueAt(load_plan_table.getSelectedRow(), 1));
                Helper.startSession();
                Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_BY_ID);
                query.setParameter("id", Integer.valueOf(id));

                Helper.sess.getTransaction().commit();
                List result = query.list();
                //WarehouseHelper.warehouse_reserv_context.setPlan();
                WarehouseHelper.temp_load_plan = (LoadPlan) result.get(0); //selectedPlan;

                //Load destinations of the plan
                if (loadDestinationsRadioGroup(Integer.valueOf(id))) {
                    loadPlanDataToLabels(WarehouseHelper.temp_load_plan, radioButtonList[0].getText());
                    reloadPlanLinesData(Integer.valueOf(id), radioButtonList[0].getText());

                    plan_id_filter.setText(id);
                    //Disable delete button if the plan is CLOSED
                    if (WarehouseHelper.LOAD_PLAN_STATE_CLOSED.equals(WarehouseHelper.temp_load_plan.getPlanState())) {
                        
                        close_plan_btn.setEnabled(false);
                        export_to_excel_btn.setEnabled(true);
                        edit_plan_btn.setEnabled(false);
                        labels_control_btn.setEnabled(false);
                        set_packaging_pile_btn.setEnabled(true);
                        piles_box.setEnabled(true);
                        scan_txt.setEnabled(false);
                        txt_filter_part.setEnabled(true);
                        radio_btn_20.setEnabled(false);
                        radio_btn_40.setEnabled(false);
                        //Disable csv buttons
                        tab5_refresh.setEnabled(false);
                        tab5_import_dispatch_labels.setEnabled(false);
                        tab5_example.setEnabled(false);
                        tab5_reset_labels_table.setEnabled(false);
                    } else { // The plan still Open
                        export_to_excel_btn.setEnabled(true);
                        edit_plan_btn.setEnabled(true);
                        labels_control_btn.setEnabled(true);
                        set_packaging_pile_btn.setEnabled(true);
                        piles_box.setEnabled(true);
                        scan_txt.setEnabled(true);
                        radio_btn_20.setEnabled(true);
                        radio_btn_40.setEnabled(true);
                        txt_filter_part.setEnabled(true);
                        //Enable csv buttons
                        tab5_refresh.setEnabled(true);
                        tab5_import_dispatch_labels.setEnabled(true);
                        tab5_example.setEnabled(true);
                        tab5_reset_labels_table.setEnabled(true);

                        if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT
                                || WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
                            close_plan_btn.setEnabled(true);
                        } else {
                            close_plan_btn.setEnabled(false);
                        }
                        if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
                            delete_plan_btn.setEnabled(true);
                        } else {
                            delete_plan_btn.setEnabled(false);
                        }

                    }
                }

                filterPlanLines(false);
                current_plan_jpanel.setSelectedIndex(1);
            }
        }
        );
    }

    public JTable getLoadPlan_lines_table() {
        return load_plan_lines_table;
    }

    public void setDispatch_lines_table(JTable load_plan_lines_table) {
        this.load_plan_lines_table = load_plan_lines_table;
    }

    /**
     * Calculate how many packaging item is consumed by this plan for each
     * destination. It calculate the full explosion of pallets/box types to
     * single item + the external packaging items.
     *
     * @param planId
     */
    public void reloadTruckTotals(int planId) {

        Helper.startSession();
        String query_str = String.format(
                HQLHelper.GET_TOTAL_TRUCK_VALUES,
                planId);
        SQLQuery query = Helper.sess.createSQLQuery(query_str);

        query.addScalar("total_net_weight", StandardBasicTypes.DOUBLE);
        query.addScalar("total_gross_weight", StandardBasicTypes.DOUBLE);
        query.addScalar("total_volume", StandardBasicTypes.DOUBLE);
        query.addScalar("total_value", StandardBasicTypes.DOUBLE);
        query.addScalar("total_std_time", StandardBasicTypes.DOUBLE);

        List<Object[]> result = query.list();
        Helper.sess.getTransaction().commit();
        //Reset table content
        //Populate jtable rows
        for (Object[] o : result) {

            txt_total_net_weight.setText(String.format("%1$,.2f", o[0]));
            txt_gross_weight.setText(String.format("%1$,.2f", o[1]));
            txt_total_volume.setText(String.format("%1$,.2f", o[2]));
            txt_total_value.setText(String.format("%1$,.2f", o[3]));
            txt_total_hours.setText(String.format("%1$,.2f", o[4]));
        }
    }

    /**
     * Calculate the total per part number and per destination.
     *
     * @param planId
     */
    public void reloadTotalPerPNTab2(int planId) {
        /*
         Helper.startSession();
         String query_str = String.format(
         HQLHelper.GET_LOAD_PLAN_EXT_PACKAGING_AND_CONTAINER,
         planId, planId);
         SQLQuery query = Helper.sess.createSQLQuery(query_str);

         query.addScalar("destination", StandardBasicTypes.STRING);
         query.addScalar("pack_item", StandardBasicTypes.STRING);
         query.addScalar("quantity", StandardBasicTypes.DOUBLE);

         List<Object[]> result = query.list();
         Helper.sess.getTransaction().commit();
         //Reset table content
         Vector total_packages_data = new Vector();
         Vector<String> total_packages_header = new Vector<String>() {
         };
         total_packages_header.add("Destination");
         total_packages_header.add("Pack Item");
         total_packages_header.add("Quantity");
         DefaultTableModel dataModel = new DefaultTableModel(total_packages_data, total_packages_header);
         jtable_total_packages.setModel(dataModel);

         //Populate jtable rows
         for (Object[] o : result) {
         Vector<Object> oneRow = new Vector<Object>();
         oneRow.add(o[0]);
         oneRow.add(o[1]);
         System.out.println("o[2].toString() "+o[2].toString());
         oneRow.add(String.valueOf(String.format("%1$,2f", Double.valueOf(o[2].toString()))));
         total_packages_data.add(oneRow);
         }
         jtable_total_packages.setModel(new DefaultTableModel(total_packages_data, total_packages_header));
         setTotalPackagingTableRowsStyle();
         */
    }

    /**
     * Calculate how many packaging item is consumed by this plan for each
     * destination. It calculate the full explosion of pallets/box types to
     * single item + the external packaging items.
     *
     * @param planId
     */
    public void reloadPackagingContainerTab3(int planId) {
        System.out.println("reloadPackagingContainerTab3 ");

        Helper.startSession();
        String query_str = String.format(
                HQLHelper.GET_LOAD_PLAN_EXT_PACKAGING_AND_CONTAINER,
                planId, planId);
        SQLQuery query = Helper.sess.createSQLQuery(query_str);

        query.addScalar("destination", StandardBasicTypes.STRING);
        query.addScalar("pack_item", StandardBasicTypes.STRING);
        query.addScalar("quantity", StandardBasicTypes.DOUBLE);

        List<Object[]> result = query.list();
        Helper.sess.getTransaction().commit();

        System.out.println("result lines " + result.size());

        //Reset table content
        total_packages_data = new Vector();
        total_packages_header = new Vector<String>() {
        };
        total_packages_header.add("Destination");
        total_packages_header.add("Pack Item");
        total_packages_header.add("Quantity");

        jtable_total_packages.setModel(new DefaultTableModel(total_packages_data, total_packages_header));

        //Populate jtable rows
        for (Object[] o : result) {
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add((String) o[0]);
            oneRow.add((String) o[1]);
            oneRow.add(String.valueOf(String.format("%1$,.2f", Double.valueOf(o[2].toString()))));
            System.out.println("one row " + oneRow.toString());
            total_packages_data.add(oneRow);
        }

        System.out.println("reloadPackagingContainerTab3 query Total packaging \n\n" + query_str);
        jtable_total_packages.setModel(new DefaultTableModel(total_packages_data, total_packages_header));
        setTotalPackagingTableRowsStyle();

    }

    /**
     * Load data in the UI from the given object
     *
     * @param p
     */
    public void loadPlanDataToLabels(LoadPlan p, String defaultDest) {
        System.out.println("HAHAHA " + defaultDest);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        plan_id_filter.setText("" + p.getId());
        this.plan_num_label.setText("" + p.getId());
        this.create_user_label.setText(p.getUser());
        this.destination_label_help.setText("");
        this.destination_label_help.setText(defaultDest);
        this.create_time_label.setText(sdf1.format(p.getCreateTime()));
        if (p.getDeliveryTime() != null) {
            this.dispatch_date_label.setText(sdf2.format(p.getDeliveryTime()));
        }
        if (p.getEndTime() != null) {
            this.release_date_label.setText(sdf1.format(p.getEndTime()));
        }
        this.project_label.setText(p.getProject());
        this.state_label.setText(p.getPlanState());

        this.truck_no_txt1.setText(p.getTruckNo());
        this.transporteur_txt.setText(p.getTransportCompany());
        this.fg_warehouse_label.setText(p.getFgWarehouse());
        //Select the last pile of the plan
        Helper.startSession();
        String query_str = String.format(HQLHelper.GET_PILES_OF_PLAN, Integer.valueOf(p.getId()));
        SQLQuery query = Helper.sess.createSQLQuery(query_str);

        query.addScalar("pile_num", StandardBasicTypes.INTEGER);
        List<Object[]> resultList = query.list();
        Helper.sess.getTransaction().commit();
        Integer[] arg = (Integer[]) resultList.toArray(new Integer[resultList.size()]);
        if (!resultList.isEmpty()) {
            for (int i = 1; i < arg.length; i++) {
                if (Integer.valueOf(piles_box.getItemAt(i).toString().trim()) == arg[i]) {
                    piles_box.setSelectedIndex(i);
                    pile_label_help.setText(arg[i].toString());
                }
            }
        } else {
            piles_box.setSelectedIndex(1);
            pile_label_help.setText(piles_box.getSelectedItem().toString());
        }

    }

    public void cleanDataLabels() {
        this.truck_no_txt1.setText("#");
        this.transporteur_txt.setText("#");
        this.plan_num_label.setText("#");
        this.create_user_label.setText("-----");
        this.create_time_label.setText("--/--/---- --:--");
        this.dispatch_date_label.setText("--/--/----");
        this.release_date_label.setText("--/--/---- --:--");
        this.project_label.setText("-----");
        this.state_label.setText("-----");
        this.destination_label_help.setText("#");
        this.pile_label_help.setText("0");
        this.fg_warehouse_label.setText(".");
    }

    public void setDestinationHelpLabel(String dest) {
        destination_label_help.setText(dest);
    }

    public void setTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        create_time_label.setText(sdf.format(date));
    }

    public void setPlanNumLabel(String text) {
        plan_num_label.setText(text);
    }

    public String getSelectedPileNum() {
        return String.valueOf(piles_box.getSelectedItem());
    }

    public String getPlanNum() {
        return plan_num_label.getText();
    }

    public String getPlanDispatchTime() {
        return dispatch_date_label.getText();
    }

    private void initGui() {

        this.scan_txt.setEnabled(false);
        this.txt_filter_part.setEnabled(false);

//        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        //Group radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(radio_btn_20);
        group.add(radio_btn_40);

        //Center the this dialog in the screen
//        Helper.centerJFrame(this);
        //Desable load plans lines table edition
        disableLoadPlanLinesEditingTable();

        //Desable load plans table edition
        disableLoadPlanEditingTable();

        //Init JTable Key Listener
        initJTableKeyListener();

        //Load table header
        load_line_table_header();

        //Load table header
        load_plan_table_header();

        //Set the rendering of plans table in Tab1
        // setLoadPlanTableRowsStyle();
        //Initialize double clique on table row
        initPlanTableDoubleClick();

        ///Charger les plan de la base
        //reloadPlansData();
        //
        //this.loadDestinations(0);
        //Disable destinations Jbox
        //destinations_box.setEnabled(false);
        piles_box.setEnabled(false);

        export_to_excel_btn.setEnabled(false);
        delete_plan_btn.setEnabled(false);
        close_plan_btn.setEnabled(false);
        edit_plan_btn.setEnabled(false);
        labels_control_btn.setEnabled(false);
        set_packaging_pile_btn.setEnabled(false);

        //Clean values form fields
        cleanDataLabels();

        //Show the jframe
        this.setVisible(true);

    }

    private void loadPiles() {
        piles_box.removeAllItems();
        piles_box.addItem("*");
        int len = 32;
        if (radio_btn_40.isSelected()) {
            len = 64;
        }
        for (int i = 1; i <= len; i++) {
            piles_box.addItem(i);
        }
    }

    public void initJTableKeyListener() {
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = this.load_plan_lines_table.getInputMap(condition);
        ActionMap actionMap = this.load_plan_lines_table.getActionMap();

        // DELETE is a String constant that for me was defined as "Delete"
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE);

        actionMap.put(DELETE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!state_label.getText().equals(WarehouseHelper.LOAD_PLAN_STATE_CLOSED)) {
                    int confirmed = JOptionPane.showConfirmDialog(null,
                            "Voulez-vous supprimer la ligne ?", "Suppression !",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmed == 0) {

                        Integer id = (Integer) load_plan_lines_table.getValueAt(load_plan_lines_table.getSelectedRow(), LINE_ID_COMLUMN);
                        //Delete line from the database
                        Helper.startSession();
                        Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_ID);
                        query.setParameter("id", id);

                        Helper.sess.getTransaction().commit();
                        List result = query.list();
                        LoadPlanLine line = (LoadPlanLine) result.get(0);

                        line.delete(line);
                        filterPlanLines(false);
                    }
                }
            }

        });

    }

    /**
     * Desactive l'édition du jTable load plan lines
     */
    public void disableLoadPlanLinesEditingTable() {
        for (int c = 0; c < load_plan_lines_table.getColumnCount(); c++) {

            Class<?> col_class = load_plan_lines_table.getColumnClass(c);
            load_plan_lines_table.setDefaultEditor(col_class, null);        // remove editor                       
        }
        JTableHelper.sizeColumnsToFit(load_plan_lines_table);
    }

    /**
     * Desactive l'édition du jTable load plan
     */
    public void disableLoadPlanEditingTable() {
        for (int c = 0; c < load_plan_table.getColumnCount(); c++) {
            Class<?> col_class = load_plan_table.getColumnClass(c);
            load_plan_table.setDefaultEditor(col_class, null);        // remove editor                 
        }
        load_plan_table.setAutoCreateRowSorter(true);
        JTableHelper.sizeColumnsToFit(load_plan_table);
    }

    /**
     * Charge les entête du jTable load plan lines
     */
    public void load_line_table_header() {
        this.reset_load_plan_lines_table_content();

        load_plan_lines_table_header.add("POSITION NUM");
        load_plan_lines_table_header.add("PALLET NUM");
        load_plan_lines_table_header.add("CUSTOMER PN");
        load_plan_lines_table_header.add("INTERNAL PN");
        load_plan_lines_table_header.add("PACK TYPE");
        load_plan_lines_table_header.add("PACK SIZE");
        load_plan_lines_table_header.add("DISPATCH LABEL NO");
        load_plan_lines_table_header.add("DESTINATION");
        load_plan_lines_table_header.add("N° LINE");
        load_plan_lines_table_header.add("SEGMENT");
        load_plan_lines_table_header.add("FIFO TIME");

        load_plan_lines_table.setModel(new DefaultTableModel(load_plan_lines_data, load_plan_lines_table_header));
    }

    /**
     * Charge les entête du jTable load plan
     */
    public void load_plan_table_header() {
        this.reset_load_plan_table_content();
        load_plan_table_header.add("TRUCK NO");
        load_plan_table_header.add("N° PLAN");
        load_plan_table_header.add("CREATE DATE");
        load_plan_table_header.add("DELIV DATE");
        load_plan_table_header.add("USER");
        load_plan_table_header.add("PROJECT");
        load_plan_table_header.add("STATE");

        load_plan_table.setModel(new DefaultTableModel(load_plan_data, load_plan_table_header));
    }

    /**
     *
     * @param planId
     * @param destinationWh
     * @param harnessPart
     * @param pileNum
     * @param lineState 0 All lines, 1 Controlled lines, 2 Not controlled lines
     * @param palletNumber
     */
    //completer la méthode pour filtrer sur les lignes
    public void filterPlanLines(int planId, String destinationWh, String harnessPart, int pileNum, int lineState, String palletNumber, String dispatchNumber) {

        Helper.startSession();
        String GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE = "FROM LoadPlanLine lpl WHERE "
                + "lpl.loadPlanId = :loadPlanId "
                + "AND lpl.destinationWh LIKE :destinationWh "
                + "AND lpl.harnessPart LIKE :harnessPart ";
        if (pileNum != 0) {
            GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "AND lpl.pileNum = :pileNum ";
        }
        if (lineState == 1) {
            GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "AND lpl.dispatchLabelNo != '' ";
        }
        if (lineState == 2) {
            GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "AND lpl.dispatchLabelNo = '' ";
        }

        if (palletNumber.length() != 0) {
            GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "AND lpl.palletNumber LIKE :palletNumber ";
        }
        if (dispatchNumber.length() != 0) {
            GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "AND lpl.dispatchLabelNo LIKE :dispatchLabelNo ";
        }
        GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "ORDER BY "
                + "destinationWh ASC, "
                + "pileNum DESC,"
                + "id ASC";

        Query query = Helper.sess.createQuery(GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE).setCacheable(false);
        query.setParameter("loadPlanId", planId);
        query.setParameter("destinationWh", "%" + destinationWh + "%");
        query.setParameter("harnessPart", "%" + harnessPart + "%");
        if (palletNumber.length() != 0) {
            query.setParameter("palletNumber", "%" + palletNumber + "%");
        }
        if (dispatchNumber.length() != 0) {
            query.setParameter("dispatchLabelNo", "%" + dispatchNumber + "%");
        }

        if (pileNum != 0) {
            query.setParameter("pileNum", pileNum);
        }

        Helper.sess.getTransaction().commit();
        List result = query.list();
        txt_nbreLigne.setText(result.size() + "");
        this.reload_load_plan_lines_table_data(result);

        this.reloadPackagingContainerTab3(planId);

        this.reloadTruckTotals(planId);

        Helper.sess.clear();
    }

    /**
     * Charge les données du jTable load plan lines
     *
     * @param planId
     * @param destinationWh
     */
    public void reloadPlanLinesData(int planId, String destinationWh) {
        System.out.println("reloadPlanLinesData destination WH" + destinationWh);
        Helper.startSession();
        Query query = null;
        if (destinationWh != null && !destinationWh.isEmpty()) {
            query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_WH);
            query.setParameter("loadPlanId", planId);
            query.setParameter("destinationWh", destinationWh);
        } else {
            query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PLAN_ID);
            query.setParameter("loadPlanId", planId);
        }

        Helper.sess.getTransaction().commit();
        List result = query.list();
        this.reload_load_plan_lines_table_data(result);
    }

    /**
     * Charge les données du jTable load plan lines
     */
    public void reloadPlansData() {
        Helper.startSession();
        Query query;
        if (!lp_filter_val.getText().isEmpty()) {
            query = Helper.sess.createQuery(HQLHelper.GET_LOAD_ALL_PLANS_BY_FILTER);
            int id = 0;
            try {
                id = Integer.valueOf(lp_filter_val.getText());
            } catch (Exception ex) {
                id = 0;
            }
            query.setInteger("id", id);
            query.setString("truckNo", "%" + lp_filter_val.getText() + "%");
            query.setString("user", "%" + lp_filter_val.getText() + "%");
            query.setString("state", plan_state_filter.getSelectedItem().toString().trim());
        } else {
            query = Helper.sess.createQuery(HQLHelper.GET_LOAD_ALL_PLANS);
            query.setString("state", plan_state_filter.getSelectedItem().toString().trim());
        }
        Helper.sess.getTransaction().commit();
        List result = query.list();
        this.reload_load_plan_table_data(result);

    }

    /**
     *
     * @param table : JTable element en entrée
     * @return Les donées du JTable sous forme de table 2 dimensions
     */
    public Object[][] getTableData(JTable table) {
        TableModel dtm = table.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        Object[][] tableData = new Object[nRow][nCol];
        for (int i = 0; i < nRow; i++) {
            for (int j = 0; j < nCol; j++) {
                tableData[i][j] = dtm.getValueAt(i, j);
            }
        }
        return tableData;
    }

    public void reset_load_plan_table_content() {
        load_plan_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(load_plan_table_data, load_plan_table_header);
        load_plan_table.setModel(dataModel);
    }

    public void reset_load_plan_lines_table_content() {
        load_plan_lines_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(load_plan_lines_table_data, load_plan_lines_table_header);
        load_plan_lines_table.setModel(dataModel);
    }

    /**
     * Mapping des donées dans le JTable load plan lines
     *
     * @param resultList
     */
    public void reload_load_plan_lines_table_data(List resultList) {
        this.reset_load_plan_lines_table_content();

        txt_totalQty.setText("0.0");
        for (Object o : resultList) {
            LoadPlanLine lpl = (LoadPlanLine) o;
            Vector<Object> oneRow = new Vector<Object>();

            oneRow.add(String.format("%02d", lpl.getPileNum()));
            oneRow.add(lpl.getPalletNumber());
            oneRow.add(lpl.getHarnessPart());
            oneRow.add(lpl.getSupplierPart());
            oneRow.add(lpl.getPackType());
            oneRow.add(lpl.getQty());
            oneRow.add(lpl.getDispatchLabelNo());
            oneRow.add(lpl.getDestinationWh());
            oneRow.add(lpl.getId());
            oneRow.add(lpl.getHarnessType());
            oneRow.add(lpl.getCreateTime());

            //Set total qty label
            txt_totalQty.setText("" + (Float.valueOf(txt_totalQty.getText()) + lpl.getQty()));

            load_plan_lines_table_data.add(oneRow);

        }
        load_plan_lines_table.setModel(new DefaultTableModel(load_plan_lines_table_data, load_plan_lines_table_header));

        //Initialize default style for table container
        setLoadPlanLinesTableRowsStyle();
    }

    /**
     * Mapping des donées dans le JTable load plan lines
     *
     * @param resultList
     */
    public void reload_load_plan_table_data(List resultList) {
        this.reset_load_plan_table_content();

        for (Object o : resultList) {
            LoadPlan lp = (LoadPlan) o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(lp.getTruckNo());
            oneRow.add(lp.getId());
            oneRow.add(GlobalMethods.convertToStandardDate(lp.getCreateTime()));
            oneRow.add(GlobalMethods.convertToStandardDate(lp.getDeliveryTime()));
            oneRow.add(lp.getUser());
            oneRow.add(lp.getProject());
            oneRow.add(lp.getPlanState());

            load_plan_table_data.add(oneRow);

        }
        load_plan_table.setModel(new DefaultTableModel(load_plan_table_data, load_plan_table_header));

        //Initialize default style for table container
        setLoadPlanTableRowsStyle();
    }

    public void tableAddRow(Component oneRow) {
        load_plan_lines_table.add(oneRow);
    }

    /**
     * Réinitialise le style de la table load plan lines
     */
    public void setLoadPlanLinesTableRowsStyle() {
        //Initialize default style for table container

        //#######################
        load_plan_lines_table.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.PLAIN, 12));
        load_plan_lines_table.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));
        load_plan_lines_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                String dispatchLabelNo = (String) table.getModel().getValueAt(row, 6);
                try {
                    //############### DISPATCH LABEL CONTROLLED ?
                    if (isSelected) {
                        setBackground(new Color(51, 204, 255));
                        setForeground(Color.BLACK);
                    } else if (!"".equals(dispatchLabelNo) && dispatchLabelNo.length() != 0) {
                        setBackground(new Color(146, 255, 167)); //BACKGROUND WITH GREEN
                        setForeground(Color.BLACK);
                    } else {
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    }
                } catch (NullPointerException e) {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }

                setHorizontalAlignment(JLabel.LEFT);
                return this;
            }
        });
        //#######################
        this.disableLoadPlanLinesEditingTable();
    }

    /**
     * Réinitialise le style de la table load plan lines
     */
    public void setTotalPackagingTableRowsStyle() {
        //Initialize default style for table container

        //#######################
        jtable_total_packages.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.PLAIN, 12));
        jtable_total_packages.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));
        jtable_total_packages.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (isSelected) {
                    setBackground(new Color(51, 204, 255));
                    setForeground(Color.BLACK);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                setHorizontalAlignment(JLabel.LEFT);
                return this;
            }
        });
        //#######################
        JTableHelper.sizeColumnsToFit(jtable_total_packages);
    }

    /**
     * Réinitialise le style de la table load plan
     */
    public void setLoadPlanTableRowsStyle() {
        //Initialize default style for table container

        //#######################        
        load_plan_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (isSelected) {
                    setBackground(new Color(51, 204, 255));
                    setForeground(Color.BLACK);
                }

                setBackground(Color.WHITE);
                setForeground(Color.BLACK);

                setHorizontalAlignment(JLabel.LEFT);

                return this;
            }
        });
        //#######################
        this.disableLoadPlanEditingTable();
    }

    /**
     *
     * @param msg : String to be displayed
     * @param type : 1 for OK , -1 for error, 0 to clean the label
     */
    public void setMessageLabel(String msg, int type) {

        switch (type) {
            case -1:
                message_label.setBackground(Color.red);
                message_label.setForeground(Color.white);
                message_label.setText(msg);
                break;
            case 1:
                message_label.setBackground(Color.green);
                message_label.setForeground(Color.black);
                message_label.setText(msg);
                break;
            case 0:
                message_label.setBackground(Color.WHITE);
                message_label.setText("");
                break;
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

        connectedUserName_label = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        scan_txt = new javax.swing.JTextField();
        radio_btn_20 = new javax.swing.JRadioButton();
        radio_btn_40 = new javax.swing.JRadioButton();
        time_label5 = new javax.swing.JLabel();
        pile_label_help = new javax.swing.JLabel();
        destination_label_help = new javax.swing.JLabel();
        message_label = new javax.swing.JTextField();
        jSplitPane2 = new javax.swing.JSplitPane();
        details_jpanel = new javax.swing.JPanel();
        create_time_label = new javax.swing.JLabel();
        time_label1 = new javax.swing.JLabel();
        time_label2 = new javax.swing.JLabel();
        plan_num_label = new javax.swing.JLabel();
        create_user_label = new javax.swing.JLabel();
        time_label3 = new javax.swing.JLabel();
        time_label4 = new javax.swing.JLabel();
        dispatch_date_label = new javax.swing.JLabel();
        state_label = new javax.swing.JLabel();
        time_label6 = new javax.swing.JLabel();
        time_label7 = new javax.swing.JLabel();
        release_date_label = new javax.swing.JLabel();
        time_label9 = new javax.swing.JLabel();
        project_label = new javax.swing.JLabel();
        time_label10 = new javax.swing.JLabel();
        fg_warehouse_label = new javax.swing.JLabel();
        current_plan_jpanel = new javax.swing.JTabbedPane();
        tab1_all_plans_jpanel = new javax.swing.JPanel();
        all_plans_scroll_panel = new javax.swing.JScrollPane();
        load_plan_table = new javax.swing.JTable();
        new_plan_btn = new javax.swing.JButton();
        refresh_btn = new javax.swing.JButton();
        lp_filter_val = new javax.swing.JTextField();
        plan_id_filter = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        plan_state_filter = new javax.swing.JComboBox<>();
        tab2_plan_details = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_filter_part = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_filter_pal_number = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_filter_dispatchl_number = new javax.swing.JTextField();
        piles_box = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        controlled_combobox = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jpanel_destinations = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        set_packaging_pile_btn = new javax.swing.JButton();
        btn_filter_ok = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_totalQty = new javax.swing.JLabel();
        txt_nbreLigne = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        load_plan_lines_table = new javax.swing.JTable();
        tab3_total_pn = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        total_per_pn_table = new javax.swing.JTable();
        tab2_destination = new javax.swing.JTextField();
        tab2_cpn = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        tab2_packtype = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        tab2_refresh = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        tab2_txt_totalQty = new javax.swing.JLabel();
        tab2_txt_nbreLigne = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        controlled_combobox_tab_2 = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        group_by_position = new javax.swing.JCheckBox();
        tab4_labels_to_be_controled = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jtable_odette_labels = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        tab5_reset_labels_table = new javax.swing.JButton();
        tab5_refresh = new javax.swing.JButton();
        tab5_import_dispatch_labels = new javax.swing.JButton();
        tab5_example = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        tab4_txt_totalPackages = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tab5_packaging = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txt_total_hours = new javax.swing.JTextField();
        txt_total_value = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txt_total_volume = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txt_gross_weight = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txt_total_net_weight = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        tab3_refresh = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtable_total_packages = new javax.swing.JTable();
        time_label8 = new javax.swing.JLabel();
        time_label11 = new javax.swing.JLabel();
        truck_no_txt1 = new javax.swing.JTextField();
        transporteur_txt = new javax.swing.JTextField();
        controls_btn_panel = new javax.swing.JPanel();
        add_new_plan_btn = new javax.swing.JButton();
        edit_plan_btn = new javax.swing.JButton();
        export_to_excel_btn = new javax.swing.JButton();
        fg_stock_btn = new javax.swing.JButton();
        pallet_list_btn = new javax.swing.JButton();
        details_pallet_btn = new javax.swing.JButton();
        labels_control_btn = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(60, 0), new java.awt.Dimension(60, 0), new java.awt.Dimension(30, 32767));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(60, 0), new java.awt.Dimension(60, 0), new java.awt.Dimension(113, 32767));
        delete_plan_btn = new javax.swing.JButton();
        close_plan_btn = new javax.swing.JButton();

        setBackground(new java.awt.Color(36, 65, 86));
        setFocusTraversalPolicyProvider(true);
        setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        setName("dispatch_module"); // NOI18N
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        connectedUserName_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        connectedUserName_label.setForeground(new java.awt.Color(0, 255, 102));
        connectedUserName_label.setText(" ");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Session : ");

        scan_txt.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        scan_txt.setForeground(new java.awt.Color(0, 0, 153));
        scan_txt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                scan_txtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                scan_txtFocusLost(evt);
            }
        });
        scan_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scan_txtActionPerformed(evt);
            }
        });
        scan_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                scan_txtKeyPressed(evt);
            }
        });

        radio_btn_20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radio_btn_20.setForeground(new java.awt.Color(255, 255, 255));
        radio_btn_20.setSelected(true);
        radio_btn_20.setText("Remorque 20\" (32 Positions)");
        radio_btn_20.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_btn_20ItemStateChanged(evt);
            }
        });

        radio_btn_40.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radio_btn_40.setForeground(new java.awt.Color(255, 255, 255));
        radio_btn_40.setText("Remorque 40\" (64 Position)");
        radio_btn_40.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_btn_40ItemStateChanged(evt);
            }
        });

        time_label5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label5.setForeground(new java.awt.Color(255, 255, 255));
        time_label5.setText("Destination");

        pile_label_help.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        pile_label_help.setForeground(new java.awt.Color(0, 255, 102));
        pile_label_help.setText("0");

        destination_label_help.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        destination_label_help.setForeground(new java.awt.Color(0, 255, 102));
        destination_label_help.setText("#");

        message_label.setEditable(false);
        message_label.setBackground(new java.awt.Color(255, 255, 255));
        message_label.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        message_label.setForeground(new java.awt.Color(255, 51, 51));
        message_label.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                message_labelActionPerformed(evt);
            }
        });

        details_jpanel.setBackground(new java.awt.Color(36, 65, 86));

        create_time_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        create_time_label.setForeground(new java.awt.Color(255, 255, 255));
        create_time_label.setText("--/--/---- --:--");

        time_label1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label1.setForeground(new java.awt.Color(255, 255, 255));
        time_label1.setText("Créé le");

        time_label2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label2.setForeground(new java.awt.Color(255, 255, 255));
        time_label2.setText("Plan No :");

        plan_num_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        plan_num_label.setForeground(new java.awt.Color(255, 255, 255));
        plan_num_label.setText("#");

        create_user_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        create_user_label.setForeground(new java.awt.Color(255, 255, 255));
        create_user_label.setText("-----");

        time_label3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label3.setForeground(new java.awt.Color(255, 255, 255));
        time_label3.setText("Crée par :");

        time_label4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label4.setForeground(new java.awt.Color(255, 255, 255));
        time_label4.setText("Dispatch pour :");

        dispatch_date_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dispatch_date_label.setForeground(new java.awt.Color(255, 255, 255));
        dispatch_date_label.setText("--/--/----");

        state_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        state_label.setForeground(new java.awt.Color(255, 255, 255));
        state_label.setText("-----");

        time_label6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label6.setForeground(new java.awt.Color(255, 255, 255));
        time_label6.setText("Statut :");

        time_label7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label7.setForeground(new java.awt.Color(255, 255, 255));
        time_label7.setText("Date fermeture :");

        release_date_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        release_date_label.setForeground(new java.awt.Color(255, 255, 255));
        release_date_label.setText("--/--/----");

        time_label9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label9.setForeground(new java.awt.Color(255, 255, 255));
        time_label9.setText("Projet :");

        project_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        project_label.setForeground(new java.awt.Color(255, 255, 255));
        project_label.setText("-----");

        time_label10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label10.setForeground(new java.awt.Color(255, 255, 255));
        time_label10.setText("Magasin F.G :");

        fg_warehouse_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        fg_warehouse_label.setForeground(new java.awt.Color(255, 255, 255));
        fg_warehouse_label.setText(".");

        current_plan_jpanel.setPreferredSize(new java.awt.Dimension(800, 1543));
        current_plan_jpanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                current_plan_jpanelMouseClicked(evt);
            }
        });

        tab1_all_plans_jpanel.setBackground(new java.awt.Color(36, 65, 86));
        tab1_all_plans_jpanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tab1_all_plans_jpanel.setPreferredSize(new java.awt.Dimension(300, 1543));

        load_plan_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Truck No", "N° Plan", "Create date", "Dispatch date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        load_plan_table.setCellSelectionEnabled(true);
        load_plan_table.setMinimumSize(new java.awt.Dimension(10, 10));
        load_plan_table.setName(""); // NOI18N
        all_plans_scroll_panel.setViewportView(load_plan_table);

        new_plan_btn.setBackground(new java.awt.Color(153, 204, 255));
        new_plan_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        new_plan_btn.setText("Nouveau...");
        new_plan_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_plan_btnActionPerformed(evt);
            }
        });

        refresh_btn.setBackground(new java.awt.Color(153, 204, 255));
        refresh_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        refresh_btn.setText("Actualiser");
        refresh_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_btnActionPerformed(evt);
            }
        });

        lp_filter_val.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lp_filter_valActionPerformed(evt);
            }
        });
        lp_filter_val.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lp_filter_valKeyTyped(evt);
            }
        });

        plan_id_filter.setBackground(new java.awt.Color(153, 255, 255));
        plan_id_filter.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        plan_id_filter.setMinimumSize(new java.awt.Dimension(14, 24));
        plan_id_filter.setPreferredSize(new java.awt.Dimension(14, 24));
        plan_id_filter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                plan_id_filterKeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Plan ID");

        plan_state_filter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "OPEN", "CLOSED" }));
        plan_state_filter.setToolTipText("");

        javax.swing.GroupLayout tab1_all_plans_jpanelLayout = new javax.swing.GroupLayout(tab1_all_plans_jpanel);
        tab1_all_plans_jpanel.setLayout(tab1_all_plans_jpanelLayout);
        tab1_all_plans_jpanelLayout.setHorizontalGroup(
            tab1_all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab1_all_plans_jpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab1_all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(all_plans_scroll_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tab1_all_plans_jpanelLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(plan_id_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lp_filter_val, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(plan_state_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(new_plan_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(667, Short.MAX_VALUE))
        );
        tab1_all_plans_jpanelLayout.setVerticalGroup(
            tab1_all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab1_all_plans_jpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab1_all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(tab1_all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(new_plan_btn)
                        .addComponent(refresh_btn)
                        .addComponent(jLabel5)
                        .addComponent(plan_state_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tab1_all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(plan_id_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(lp_filter_val, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(45, 45, 45)
                .addComponent(all_plans_scroll_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 725, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(999, Short.MAX_VALUE))
        );

        current_plan_jpanel.addTab("Tous plans", tab1_all_plans_jpanel);

        tab2_plan_details.setBackground(new java.awt.Color(36, 65, 86));

        jPanel4.setBackground(new java.awt.Color(36, 65, 86));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Article / référence");

        txt_filter_part.setBackground(new java.awt.Color(204, 255, 204));
        txt_filter_part.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txt_filter_part.setToolTipText("Part Number");
        txt_filter_part.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_filter_partActionPerformed(evt);
            }
        });
        txt_filter_part.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_filter_partKeyTyped(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Num Série Production");

        txt_filter_pal_number.setBackground(new java.awt.Color(204, 255, 204));
        txt_filter_pal_number.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txt_filter_pal_number.setToolTipText("Part Number");
        txt_filter_pal_number.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_filter_pal_numberKeyTyped(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Num série Odette");

        txt_filter_dispatchl_number.setBackground(new java.awt.Color(204, 255, 204));
        txt_filter_dispatchl_number.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txt_filter_dispatchl_number.setToolTipText("Part Number");
        txt_filter_dispatchl_number.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_filter_dispatchl_numberKeyTyped(evt);
            }
        });

        piles_box.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        piles_box.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "*", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32" }));
        piles_box.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                piles_boxItemStateChanged(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Position");

        controlled_combobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "Controlled", "Not controlled" }));
        controlled_combobox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                controlled_comboboxItemStateChanged(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Labels Control");

        jpanel_destinations.setBackground(new java.awt.Color(204, 255, 204));

        javax.swing.GroupLayout jpanel_destinationsLayout = new javax.swing.GroupLayout(jpanel_destinations);
        jpanel_destinations.setLayout(jpanel_destinationsLayout);
        jpanel_destinationsLayout.setHorizontalGroup(
            jpanel_destinationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 207, Short.MAX_VALUE)
        );
        jpanel_destinationsLayout.setVerticalGroup(
            jpanel_destinationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Destination");

        set_packaging_pile_btn.setBackground(new java.awt.Color(153, 204, 255));
        set_packaging_pile_btn.setText("Packaging supplémentaire");
        set_packaging_pile_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                set_packaging_pile_btnActionPerformed(evt);
            }
        });

        btn_filter_ok.setBackground(new java.awt.Color(153, 204, 255));
        btn_filter_ok.setText("Actualiser");
        btn_filter_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_filter_okActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Total Packs");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Total Qty");

        txt_totalQty.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txt_totalQty.setForeground(new java.awt.Color(255, 255, 255));
        txt_totalQty.setText("0");

        txt_nbreLigne.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txt_nbreLigne.setForeground(new java.awt.Color(255, 255, 255));
        txt_nbreLigne.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txt_filter_part, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(txt_filter_pal_number, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(txt_filter_dispatchl_number, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(piles_box, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(controlled_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jpanel_destinations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btn_filter_ok)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(set_packaging_pile_btn)
                        .addGap(319, 319, 319)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_nbreLigne, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(106, 106, 106)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txt_totalQty, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel1))
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel9)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(controlled_combobox)
                    .addComponent(jpanel_destinations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(piles_box)
                    .addComponent(txt_filter_dispatchl_number)
                    .addComponent(txt_filter_pal_number)
                    .addComponent(txt_filter_part))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(set_packaging_pile_btn)
                            .addComponent(btn_filter_ok))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_nbreLigne)
                            .addComponent(jLabel6)
                            .addComponent(txt_totalQty)
                            .addComponent(jLabel7)))))
        );

        load_plan_lines_table.setAutoCreateRowSorter(true);
        load_plan_lines_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PILE NUM", "PALLET NUM", "CPN", "INTERNAL PN", "PACK TYPE", "PACK SIZE", "DESTINATION", "LINE ID", "FAMILY", "FIFO"
            }
        ));
        jScrollPane3.setViewportView(load_plan_lines_table);

        javax.swing.GroupLayout tab2_plan_detailsLayout = new javax.swing.GroupLayout(tab2_plan_details);
        tab2_plan_details.setLayout(tab2_plan_detailsLayout);
        tab2_plan_detailsLayout.setHorizontalGroup(
            tab2_plan_detailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab2_plan_detailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab2_plan_detailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1204, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tab2_plan_detailsLayout.setVerticalGroup(
            tab2_plan_detailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab2_plan_detailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1094, Short.MAX_VALUE))
        );

        current_plan_jpanel.addTab("Détails plan", tab2_plan_details);

        tab3_total_pn.setBackground(new java.awt.Color(36, 65, 86));
        tab3_total_pn.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tab3_total_pnFocusGained(evt);
            }
        });
        tab3_total_pn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab3_total_pnMouseClicked(evt);
            }
        });
        tab3_total_pn.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tab3_total_pnPropertyChange(evt);
            }
        });
        tab3_total_pn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tab3_total_pnKeyTyped(evt);
            }
        });

        total_per_pn_table.setAutoCreateRowSorter(true);
        total_per_pn_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CPN", "LPN", "PACK TYPE", "TOTAL QTY", "UCS", "TOTAL PACK", "DESTINATION"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        total_per_pn_table.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(total_per_pn_table);

        tab2_destination.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        tab2_destination.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tab2_destinationKeyTyped(evt);
            }
        });

        tab2_cpn.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        tab2_cpn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tab2_cpnKeyTyped(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Destination");

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("CPN");

        tab2_packtype.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        tab2_packtype.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tab2_packtypeKeyTyped(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Pack Type");

        tab2_refresh.setText("Actualiser");
        tab2_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tab2_refreshActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Total Qty");

        tab2_txt_totalQty.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tab2_txt_totalQty.setForeground(new java.awt.Color(255, 255, 255));
        tab2_txt_totalQty.setText("0");

        tab2_txt_nbreLigne.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tab2_txt_nbreLigne.setForeground(new java.awt.Color(255, 255, 255));
        tab2_txt_nbreLigne.setText("0");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Total Packages ");

        controlled_combobox_tab_2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "Controlled", "Not controlled" }));
        controlled_combobox_tab_2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                controlled_combobox_tab_2ItemStateChanged(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Labels Control");

        group_by_position.setForeground(new java.awt.Color(255, 255, 255));
        group_by_position.setText("Group by position");
        group_by_position.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                group_by_positionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tab3_total_pnLayout = new javax.swing.GroupLayout(tab3_total_pn);
        tab3_total_pn.setLayout(tab3_total_pnLayout);
        tab3_total_pnLayout.setHorizontalGroup(
            tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab3_total_pnLayout.createSequentialGroup()
                .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab3_total_pnLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(tab3_total_pnLayout.createSequentialGroup()
                                .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addGroup(tab3_total_pnLayout.createSequentialGroup()
                                        .addComponent(controlled_combobox_tab_2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(group_by_position)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(tab2_txt_totalQty, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tab2_txt_nbreLigne, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tab3_total_pnLayout.createSequentialGroup()
                                .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17)
                                    .addComponent(tab2_destination, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18)
                                    .addComponent(tab2_cpn, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19)
                                    .addGroup(tab3_total_pnLayout.createSequentialGroup()
                                        .addComponent(tab2_packtype, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(tab2_refresh)))
                                .addGap(56, 56, 56))))
                    .addGroup(tab3_total_pnLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 762, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(624, Short.MAX_VALUE))
        );
        tab3_total_pnLayout.setVerticalGroup(
            tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab3_total_pnLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(tab2_refresh)
                        .addGroup(tab3_total_pnLayout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addComponent(tab2_packtype)
                            .addGap(2, 2, 2)))
                    .addGroup(tab3_total_pnLayout.createSequentialGroup()
                        .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18)
                                .addComponent(jLabel19)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tab2_cpn, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                            .addComponent(tab2_destination))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(controlled_combobox_tab_2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(group_by_position))
                    .addGroup(tab3_total_pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel20)
                        .addComponent(tab2_txt_totalQty)
                        .addComponent(jLabel21)
                        .addComponent(tab2_txt_nbreLigne)))
                .addGap(9, 9, 9)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1174, Short.MAX_VALUE))
        );

        current_plan_jpanel.addTab("Total par PN", tab3_total_pn);

        tab4_labels_to_be_controled.setBackground(new java.awt.Color(36, 65, 86));
        tab4_labels_to_be_controled.setAutoscrolls(true);
        tab4_labels_to_be_controled.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tab4_labels_to_be_controledComponentShown(evt);
            }
        });

        jtable_odette_labels.setAutoCreateRowSorter(true);
        jtable_odette_labels.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Destination", "Article", "Quantite", "NumSsrie"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtable_odette_labels.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane5.setViewportView(jtable_odette_labels);

        jPanel1.setBackground(new java.awt.Color(36, 65, 86));

        tab5_reset_labels_table.setText("Réinitialiser la liste");
        tab5_reset_labels_table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tab5_reset_labels_tableActionPerformed(evt);
            }
        });

        tab5_refresh.setText("Actualiser");
        tab5_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tab5_refreshActionPerformed(evt);
            }
        });

        tab5_import_dispatch_labels.setText("Importer les Odettes dispatch (.csv)");
        tab5_import_dispatch_labels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tab5_import_dispatch_labelsActionPerformed(evt);
            }
        });

        tab5_example.setText("Exemple fichier .CSV");
        tab5_example.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tab5_exampleActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Total Packages ");

        tab4_txt_totalPackages.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tab4_txt_totalPackages.setForeground(new java.awt.Color(255, 255, 255));
        tab4_txt_totalPackages.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tab5_refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tab5_import_dispatch_labels)
                        .addGap(73, 73, 73)
                        .addComponent(tab5_example)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tab5_reset_labels_table, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tab4_txt_totalPackages, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tab5_reset_labels_table, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tab5_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tab5_import_dispatch_labels, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tab5_example, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel24)
                    .addComponent(tab4_txt_totalPackages))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jLabel23.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Packages à charger");

        javax.swing.GroupLayout tab4_labels_to_be_controledLayout = new javax.swing.GroupLayout(tab4_labels_to_be_controled);
        tab4_labels_to_be_controled.setLayout(tab4_labels_to_be_controledLayout);
        tab4_labels_to_be_controledLayout.setHorizontalGroup(
            tab4_labels_to_be_controledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab4_labels_to_be_controledLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab4_labels_to_be_controledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab4_labels_to_be_controledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 841, Short.MAX_VALUE))
                    .addComponent(jLabel23))
                .addContainerGap(545, Short.MAX_VALUE))
        );
        tab4_labels_to_be_controledLayout.setVerticalGroup(
            tab4_labels_to_be_controledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab4_labels_to_be_controledLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1155, Short.MAX_VALUE))
        );

        current_plan_jpanel.addTab("Packages à charger", tab4_labels_to_be_controled);

        tab5_packaging.setBackground(new java.awt.Color(36, 65, 86));
        tab5_packaging.setAutoscrolls(true);
        tab5_packaging.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tab5_packagingComponentShown(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Total hours");

        txt_total_hours.setEditable(false);

        txt_total_value.setEditable(false);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Total value");

        txt_total_volume.setEditable(false);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Total volume");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Total gross weight");

        txt_gross_weight.setEditable(false);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Total net weight");

        txt_total_net_weight.setEditable(false);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Total packaging ");

        tab3_refresh.setText("Actualiser");
        tab3_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tab3_refreshActionPerformed(evt);
            }
        });

        jtable_total_packages.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane4.setViewportView(jtable_total_packages);

        javax.swing.GroupLayout tab5_packagingLayout = new javax.swing.GroupLayout(tab5_packaging);
        tab5_packaging.setLayout(tab5_packagingLayout);
        tab5_packagingLayout.setHorizontalGroup(
            tab5_packagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab5_packagingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab5_packagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(tab5_packagingLayout.createSequentialGroup()
                        .addGroup(tab5_packagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(tab5_packagingLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(59, 59, 59)
                                .addComponent(txt_total_hours, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tab5_packagingLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txt_total_volume, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(tab5_packagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel11))
                        .addGap(18, 18, 18)
                        .addGroup(tab5_packagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tab5_packagingLayout.createSequentialGroup()
                                .addComponent(txt_total_value, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel14))
                            .addComponent(txt_gross_weight, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tab5_packagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tab3_refresh)
                            .addComponent(txt_total_net_weight, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel16)
                    .addComponent(jScrollPane4))
                .addContainerGap(644, Short.MAX_VALUE))
        );
        tab5_packagingLayout.setVerticalGroup(
            tab5_packagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab5_packagingLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(tab5_packagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_total_net_weight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(txt_total_value, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txt_total_hours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tab5_packagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab5_packagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(txt_gross_weight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(txt_total_volume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12))
                    .addComponent(tab3_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1146, Short.MAX_VALUE))
        );

        current_plan_jpanel.addTab("Packaging", tab5_packaging);

        time_label8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label8.setForeground(new java.awt.Color(255, 255, 255));
        time_label8.setText("Remorque Num");

        time_label11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        time_label11.setForeground(new java.awt.Color(255, 255, 255));
        time_label11.setText("Transporteur");

        truck_no_txt1.setEditable(false);
        truck_no_txt1.setBackground(new java.awt.Color(153, 255, 255));
        truck_no_txt1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        truck_no_txt1.setText("#");
        truck_no_txt1.setToolTipText("");
        truck_no_txt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                truck_no_txt1ActionPerformed(evt);
            }
        });

        transporteur_txt.setEditable(false);
        transporteur_txt.setBackground(new java.awt.Color(153, 255, 255));
        transporteur_txt.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        transporteur_txt.setText("#");
        transporteur_txt.setToolTipText("");
        transporteur_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transporteur_txtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout details_jpanelLayout = new javax.swing.GroupLayout(details_jpanel);
        details_jpanel.setLayout(details_jpanelLayout);
        details_jpanelLayout.setHorizontalGroup(
            details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(details_jpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(details_jpanelLayout.createSequentialGroup()
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(details_jpanelLayout.createSequentialGroup()
                                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(time_label4)
                                    .addComponent(time_label3)
                                    .addComponent(time_label6)
                                    .addComponent(time_label7)
                                    .addComponent(time_label2)
                                    .addComponent(time_label1))
                                .addGap(18, 18, 18)
                                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dispatch_date_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(create_user_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(release_date_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(details_jpanelLayout.createSequentialGroup()
                                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(state_label, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(create_time_label, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(plan_num_label))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(details_jpanelLayout.createSequentialGroup()
                                .addComponent(time_label9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(project_label, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(details_jpanelLayout.createSequentialGroup()
                                .addComponent(time_label10)
                                .addGap(18, 18, 18)
                                .addComponent(fg_warehouse_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(41, 41, 41))
                    .addGroup(details_jpanelLayout.createSequentialGroup()
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(time_label8)
                            .addComponent(time_label11))
                        .addGap(18, 18, 18)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(truck_no_txt1, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                            .addComponent(transporteur_txt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(current_plan_jpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1394, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(265, 265, 265))
        );
        details_jpanelLayout.setVerticalGroup(
            details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(details_jpanelLayout.createSequentialGroup()
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(details_jpanelLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(time_label8)
                            .addComponent(truck_no_txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(time_label11)
                            .addComponent(transporteur_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(time_label2)
                            .addComponent(plan_num_label))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(time_label1)
                            .addComponent(create_time_label))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(time_label3)
                            .addComponent(create_user_label, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(time_label4)
                            .addComponent(dispatch_date_label))
                        .addGap(18, 18, 18)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(project_label)
                            .addComponent(time_label9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(state_label)
                            .addComponent(time_label6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(time_label7)
                            .addComponent(release_date_label))
                        .addGap(18, 18, 18)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(time_label10)
                            .addComponent(fg_warehouse_label)))
                    .addGroup(details_jpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(current_plan_jpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1834, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(details_jpanel);

        controls_btn_panel.setBackground(new java.awt.Color(36, 65, 86));

        add_new_plan_btn.setBackground(new java.awt.Color(102, 255, 255));
        add_new_plan_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        add_new_plan_btn.setForeground(new java.awt.Color(0, 0, 0));
        add_new_plan_btn.setText("Nouveau plan...");
        add_new_plan_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_new_plan_btnActionPerformed(evt);
            }
        });

        edit_plan_btn.setBackground(new java.awt.Color(102, 255, 255));
        edit_plan_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        edit_plan_btn.setForeground(new java.awt.Color(0, 0, 0));
        edit_plan_btn.setText("Editer...");
        edit_plan_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_plan_btnActionPerformed(evt);
            }
        });

        export_to_excel_btn.setBackground(new java.awt.Color(102, 255, 255));
        export_to_excel_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        export_to_excel_btn.setForeground(new java.awt.Color(0, 0, 0));
        export_to_excel_btn.setText("Exporter en excel...");
        export_to_excel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                export_to_excel_btnActionPerformed(evt);
            }
        });

        fg_stock_btn.setBackground(new java.awt.Color(102, 255, 255));
        fg_stock_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        fg_stock_btn.setForeground(new java.awt.Color(0, 0, 0));
        fg_stock_btn.setText("Stock F.G");
        fg_stock_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fg_stock_btnActionPerformed(evt);
            }
        });

        pallet_list_btn.setBackground(new java.awt.Color(102, 255, 255));
        pallet_list_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        pallet_list_btn.setForeground(new java.awt.Color(0, 0, 0));
        pallet_list_btn.setText("Liste des palettes");
        pallet_list_btn.setFocusable(false);
        pallet_list_btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pallet_list_btn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pallet_list_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pallet_list_btnActionPerformed(evt);
            }
        });

        details_pallet_btn.setBackground(new java.awt.Color(102, 255, 255));
        details_pallet_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        details_pallet_btn.setForeground(new java.awt.Color(0, 0, 0));
        details_pallet_btn.setText("Détails palette");
        details_pallet_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                details_pallet_btnActionPerformed(evt);
            }
        });

        labels_control_btn.setBackground(new java.awt.Color(102, 255, 255));
        labels_control_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labels_control_btn.setForeground(new java.awt.Color(0, 0, 0));
        labels_control_btn.setText("Contrôle des étiquettes");
        labels_control_btn.setFocusable(false);
        labels_control_btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labels_control_btn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        labels_control_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labels_control_btnActionPerformed(evt);
            }
        });

        delete_plan_btn.setBackground(java.awt.Color.red);
        delete_plan_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        delete_plan_btn.setForeground(new java.awt.Color(255, 255, 255));
        delete_plan_btn.setText("Supprimer");
        delete_plan_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_plan_btnActionPerformed(evt);
            }
        });

        close_plan_btn.setBackground(java.awt.Color.orange);
        close_plan_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        close_plan_btn.setForeground(new java.awt.Color(0, 0, 0));
        close_plan_btn.setText("Fermer le plan");
        close_plan_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_plan_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controls_btn_panelLayout = new javax.swing.GroupLayout(controls_btn_panel);
        controls_btn_panel.setLayout(controls_btn_panelLayout);
        controls_btn_panelLayout.setHorizontalGroup(
            controls_btn_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controls_btn_panelLayout.createSequentialGroup()
                .addComponent(add_new_plan_btn)
                .addGap(0, 0, 0)
                .addComponent(edit_plan_btn)
                .addGap(0, 0, 0)
                .addComponent(export_to_excel_btn)
                .addGap(0, 0, 0)
                .addComponent(fg_stock_btn)
                .addGap(0, 0, 0)
                .addComponent(pallet_list_btn)
                .addGap(0, 0, 0)
                .addComponent(details_pallet_btn)
                .addGap(0, 0, 0)
                .addComponent(labels_control_btn)
                .addGap(48, 48, 48)
                .addComponent(close_plan_btn)
                .addGap(106, 106, 106)
                .addComponent(delete_plan_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        controls_btn_panelLayout.setVerticalGroup(
            controls_btn_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controls_btn_panelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(controls_btn_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(delete_plan_btn)
                    .addComponent(close_plan_btn)
                    .addComponent(labels_control_btn)
                    .addComponent(details_pallet_btn)
                    .addComponent(pallet_list_btn)
                    .addComponent(fg_stock_btn)
                    .addComponent(export_to_excel_btn)
                    .addComponent(edit_plan_btn)
                    .addComponent(add_new_plan_btn)
                    .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(message_label)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(442, 442, 442)
                                .addComponent(time_label5)
                                .addGap(58, 58, 58)
                                .addComponent(destination_label_help, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(scan_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(radio_btn_20)
                                        .addGap(11, 11, 11)
                                        .addComponent(radio_btn_40)))
                                .addGap(86, 86, 86)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(connectedUserName_label, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(pile_label_help)
                        .addGap(672, 672, 672))
                    .addComponent(controls_btn_panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSplitPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1546, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(controls_btn_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(message_label, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(destination_label_help)
                            .addComponent(time_label5, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pile_label_help))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(connectedUserName_label, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(scan_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(radio_btn_20)
                            .addComponent(radio_btn_40))))
                .addGap(21, 21, 21)
                .addComponent(jSplitPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1673, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleDescription("Dispatch Module");
    }// </editor-fold>//GEN-END:initComponents

    private void scan_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scan_txtKeyPressed
        // User has pressed Carriage return button
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            state.doAction(WarehouseHelper.warehouse_reserv_context);
            state = WarehouseHelper.warehouse_reserv_context.getState();
            
        }
//else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
//            int confirmed = JOptionPane.showConfirmDialog(null,
//                    "Voulez-vous fermer la session ?", "Confirmation",
//                    JOptionPane.YES_NO_OPTION);
//            if (confirmed == 0) {
//                //logout();
//                state = new S001_ReservPalletNumberScan();
//
//                this.setVisible(false);
//
//            }
//        }
    }//GEN-LAST:event_scan_txtKeyPressed

    //########################################################################
    //################ Reset GUI Component to ReservationState S01 ######################
    //########################################################################
    public void logout() {

        if (WarehouseHelper.warehouse_reserv_context.getUser().getId() != null) {
            //Save authentication line in HisLogin table
            /*
             HisLogin his_login = new HisLogin(
             WarehouseHelper.warehouse_reserv_context.getUser().getId(),
             WarehouseHelper.warehouse_reserv_context.getUser().getId(),
             String.format(Helper.INFO0012_LOGOUT_SUCCESS,
             WarehouseHelper.warehouse_reserv_context.getUser().getFirstName()
             + " " + WarehouseHelper.warehouse_reserv_context.getUser().getLastName()
             + " / " + WarehouseHelper.warehouse_reserv_context.getUser().getLogin(),
             GlobalVars.APP_HOSTNAME, GlobalMethods.getStrTimeStamp()));
             his_login.setCreateId(WarehouseHelper.warehouse_reserv_context.getUser().getId());
             his_login.setWriteId(WarehouseHelper.warehouse_reserv_context.getUser().getId());
            
            
             String str = String.format(Helper.INFO0012_LOGOUT_SUCCESS,
             WarehouseHelper.warehouse_reserv_context.getUser().getFirstName() + " " + WarehouseHelper.warehouse_reserv_context.getUser().getLastName()
             + " / " + PackagingVars.context.getUser().getLogin(), GlobalVars.APP_HOSTNAME,
             GlobalMethods.getStrTimeStamp());
             his_login.setMessage(str);

             str = "";
             his_login.create(his_login);
             */
            //Reset the state
            state = new S001_ReservPalletNumberScan();

            this.clearContextSessionVals();

            connectedUserName_label.setText("");

            this.setVisible(false);
        }

    }

    //########################################################################
    //########################## USER LABEL METHODS ##########################
    //########################################################################
    public void setUserLabelText(String newText) {
        connectedUserName_label.setText(newText);
    }

    public JTextField getScanTxt() {
        return this.scan_txt;
    }

    public void setScanTxt(JTextField setScanTxt) {
        this.scan_txt = setScanTxt;
    }

    public void clearContextSessionVals() {
        //Pas besoin de réinitialiser le uid        
        PackagingVars.context.setUser(new ManufactureUsers());
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        int confirmed = JOptionPane.showConfirmDialog(null,
                "On quittant le programme vous perdez toutes vos données actuelles. Voulez-vous quitter ?", "Exit Program Message Box",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == 0) {
            //clearGui();
            //logout();
            state = new S001_ReservPalletNumberScan();

            this.setVisible(false);

        } else {
            //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);//no
        }
    }//GEN-LAST:event_formWindowClosing

    private void radio_btn_40ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_btn_40ItemStateChanged
        loadPiles();
    }//GEN-LAST:event_radio_btn_40ItemStateChanged

    private void radio_btn_20ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_btn_20ItemStateChanged
        loadPiles();
    }//GEN-LAST:event_radio_btn_20ItemStateChanged

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped

    }//GEN-LAST:event_formKeyTyped


    private void scan_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scan_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_scan_txtActionPerformed

    public boolean loadPlanDataInGui(int id) {
        try {

            Helper.startSession();
            Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_BY_ID);
            query.setParameter("id", id);

            Helper.sess.getTransaction().commit();
            List result = query.list();
            LoadPlan plan = (LoadPlan) result.get(0);
            WarehouseHelper.temp_load_plan = plan;

            //Load destinations of the plan
            //if (loadDestinations(Integer.valueOf(id))) {
            if (loadDestinationsRadioGroup(Integer.valueOf(id))) {
                System.out.println("selectedDestination " + selectedDestination);
                loadPlanDataToLabels(plan, selectedDestination);
                reloadPlanLinesData(Integer.valueOf(id), selectedDestination);

                //Disable delete button if the plan is CLOSED
                if (WarehouseHelper.LOAD_PLAN_STATE_CLOSED.equals(plan.getPlanState())) {
                    delete_plan_btn.setEnabled(false);
                    close_plan_btn.setEnabled(false);
                    export_to_excel_btn.setEnabled(true);
                    edit_plan_btn.setEnabled(false);
                    set_packaging_pile_btn.setEnabled(true);
                    labels_control_btn.setEnabled(false);
                    piles_box.setEnabled(true);
                    scan_txt.setEnabled(false);
                    txt_filter_part.setEnabled(true);
                    radio_btn_20.setEnabled(false);
                    radio_btn_40.setEnabled(false);

                } else { // The plan still Open
                    /*
                     if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT) {
                     delete_plan_btn.setEnabled(false);
                     close_plan_btn.setEnabled(false);
                     }
                     if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
                     delete_plan_btn.setEnabled(true);
                     close_plan_btn.setEnabled(true);
                     }
                     */
                    if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT
                            || WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
                        close_plan_btn.setEnabled(true);
                    } else {
                        close_plan_btn.setEnabled(false);
                    }
                    if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
                        delete_plan_btn.setEnabled(true);
                    } else {
                        delete_plan_btn.setEnabled(false);
                    }
                    export_to_excel_btn.setEnabled(true);
                    edit_plan_btn.setEnabled(true);
                    labels_control_btn.setEnabled(true);
                    set_packaging_pile_btn.setEnabled(true);
                    piles_box.setEnabled(true);
                    scan_txt.setEnabled(true);
                    txt_filter_part.setEnabled(true);
                    radio_btn_20.setEnabled(true);
                    radio_btn_40.setEnabled(true);
                }
            }

            filterPlanLines(false);
            filterPlanLines(false);
            return true;
        } catch (Exception e) {
            return false;

        }
    }

    private void message_labelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_message_labelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_message_labelActionPerformed

    private void truck_no_txt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_truck_no_txt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_truck_no_txt1ActionPerformed

    /**
     *
     * @param destination
     * @param pn
     * @param pack_type
     * @param lineState
     */
    public void total_per_part_and_destination(String destination, String pn, String pack_type, int lineState) {

        tab2_txt_nbreLigne.setText("0");
        tab2_txt_totalQty.setText("0");
        total_per_dest_table_data = new Vector();
        total_per_dest_table_data_header = new Vector();
        //Init table header
        if (group_by_position.isSelected()) {
            total_per_dest_table_data_header.add("PILE");
        }
        total_per_dest_table_data_header.add("CPN");
        total_per_dest_table_data_header.add("SPN");
        total_per_dest_table_data_header.add("PACK TYPE");
        total_per_dest_table_data_header.add("TOTAL QTY");
        total_per_dest_table_data_header.add("UCS");
        total_per_dest_table_data_header.add("TOTAL PACKS");
        total_per_dest_table_data_header.add("DESTINATION");

        DefaultTableModel dataModel = new DefaultTableModel(total_per_dest_table_data, total_per_dest_table_data_header);
        total_per_pn_table.setModel(dataModel);

        Helper.startSession();

        String query_str = "SELECT \n";

        if (group_by_position.isSelected()) {
            query_str += "line.pile_num AS pile_num,\n";
        }
        query_str += "line.harness_part AS harness_part,\n"
                + "line.supplier_part AS supplier_part,\n"
                + "line.pack_type AS pack_type,\n"
                + "SUM(line.qty) AS total_qty,\n"
                + "line.qty AS qty,\n"
                + "COUNT(*) AS total_pack,\n"
                + "line.destination_wh AS destination_wh \n"
                + "FROM load_plan_line line\n"
                + "WHERE load_plan_id = '" + plan_num_label.getText() + "' \n";
        if (destination != null && !destination.isEmpty()) {
            query_str += " AND destination_wh LIKE '%" + destination.toUpperCase() + "%'";
        }
        if (pn != null && !pn.isEmpty()) {
            query_str += " AND harness_part LIKE '%" + pn + "%'";
        }
        if (pack_type != null && !pack_type.isEmpty()) {
            query_str += " AND pack_type LIKE '%" + pack_type.toUpperCase() + "%'";
        }

        if (lineState == 1) {
            query_str += " AND line.dispatch_label_no != '' ";
        }
        if (lineState == 2) {
            query_str += " AND line.dispatch_label_no = '' ";
        }

        if (group_by_position.isSelected()) {
            query_str += " GROUP BY pile_num, destination_wh, harness_part, pack_type, qty, supplier_part\n"
                    + "ORDER BY destination_wh ASC,pile_num ASC, pack_type DESC;";
        } else {
            query_str += " GROUP BY destination_wh, harness_part, pack_type, qty, supplier_part\n"
                    + "ORDER BY destination_wh ASC, pack_type DESC;";
        }
        SQLQuery query = Helper.sess.createSQLQuery(query_str);

        //System.out.println("Query " + query_str);
        if (group_by_position.isSelected()) {
            query.addScalar("pile_num", StandardBasicTypes.INTEGER);
        }
        query
                .addScalar("harness_part", StandardBasicTypes.STRING)
                .addScalar("supplier_part", StandardBasicTypes.STRING)
                .addScalar("pack_type", StandardBasicTypes.STRING)
                .addScalar("total_qty", StandardBasicTypes.DOUBLE)
                .addScalar("qty", StandardBasicTypes.DOUBLE)
                .addScalar("total_pack", StandardBasicTypes.INTEGER)
                .addScalar("destination_wh", StandardBasicTypes.STRING);

        List<Object[]> result = query.list();
        Helper.sess.getTransaction().commit();
        float total_packs = 0;
        float total_qty = 0;

        for (Object[] obj : result) {
            Vector<Object> oneRow = new Vector<Object>();
            if (group_by_position.isSelected()) {
                oneRow.add((Integer) obj[0]);
                oneRow.add((String) obj[1]);
                oneRow.add((String) obj[2]);
                oneRow.add((String) obj[3]);
                oneRow.add(String.format("%1$,.2f", obj[4]));
                oneRow.add(String.format("%1$,.2f", obj[5]));
                oneRow.add(String.format("%d", obj[6]));
                oneRow.add((String) obj[7]);

                total_qty += Float.valueOf(obj[4].toString());
                total_packs += Float.valueOf(String.format("%d", obj[6]));
            } else {
                oneRow.add((String) obj[0]);
                oneRow.add((String) obj[1]);
                oneRow.add((String) obj[2]);
                oneRow.add(String.format("%1$,.2f", obj[3]));
                oneRow.add(String.format("%1$,.2f", obj[4]));
                oneRow.add(String.format("%d", obj[5]));
                oneRow.add((String) obj[6]);

                total_qty += Float.valueOf(obj[3].toString());
                total_packs += Float.valueOf(String.format("%d", obj[5]));
            }
            //System.out.println("one row " + oneRow.toString());
            total_per_dest_table_data.add(oneRow);

        }
        tab2_txt_nbreLigne.setText(total_packs + "");
        tab2_txt_totalQty.setText(total_qty + "");
        total_per_pn_table.setModel(new DefaultTableModel(total_per_dest_table_data, total_per_dest_table_data_header));

    }

    private void scan_txtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scan_txtFocusGained
        scan_txt.setBackground(Color.GREEN);
    }//GEN-LAST:event_scan_txtFocusGained

    private void scan_txtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scan_txtFocusLost
        scan_txt.setBackground(Color.WHITE);
    }//GEN-LAST:event_scan_txtFocusLost

    private void add_new_plan_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_new_plan_btnActionPerformed
        new WAREHOUSE_DISPATCH_UI0004_NEW_PLAN(null, true).setVisible(true);
    }//GEN-LAST:event_add_new_plan_btnActionPerformed

    private void edit_plan_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_plan_btnActionPerformed
        new WAREHOUSE_DISPATCH_UI0005_EDIT_PLAN(null, WarehouseHelper.temp_load_plan);
    }//GEN-LAST:event_edit_plan_btnActionPerformed

    private void export_to_excel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export_to_excel_btnActionPerformed
        exportPlanDetails();
    }//GEN-LAST:event_export_to_excel_btnActionPerformed

    private void close_plan_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_plan_btnActionPerformed
        System.out.println("WarehouseHelper.temp_load_plan.getTruckNo() " + WarehouseHelper.temp_load_plan.getTruckNo());

        if (WarehouseHelper.temp_load_plan.getTruckNo() == null || WarehouseHelper.temp_load_plan.getTruckNo().length() == 0) {
            UILog.severe(ErrorMsg.APP_ERR0032[0], plan_num_label.getText());
            UILog.severeDialog(null, ErrorMsg.APP_ERR0032, plan_num_label.getText());
        } else {

            int confirmed = JOptionPane.showConfirmDialog(null,
                    "Confirmez-vous la fin du chargement N° " + plan_num_label.getText() + " avec date dispatch " + WarehouseHelper.temp_load_plan.getDeliveryTime() + " ?", "Fin du chargement",
                    JOptionPane.YES_NO_OPTION);
            if (confirmed == 0) {
                Helper.startSession();
                Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PLAN_ID);
                query.setParameter("loadPlanId", Integer.valueOf(plan_num_label.getText()));
                Helper.sess.getTransaction().commit();
                List result = query.list();
                if (!result.isEmpty()) {
                    //Initialize progress property.

//                    close_plan_menu.setEnabled(false);
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    for (Object obj : result) {
                        LoadPlanLine line = (LoadPlanLine) obj;

                        BaseContainer bc = new BaseContainer().getBaseContainer(line.getPalletNumber());
                        bc.setContainerState(GlobalVars.PALLET_DISPATCHED);
                        bc.setContainerStateCode(GlobalVars.PALLET_DISPATCHED_CODE);
                        //bc.setDispatchTime(WarehouseHelper.temp_load_plan.getDeliveryTime());
                        //bc.setFifoTime(WarehouseHelper.temp_load_plan.getDeliveryTime());
                        bc.setDispatchTime(new Date());
                        bc.setFifoTime(new Date());
                        bc.setDestination(line.getDestinationWh());
                        bc.update(bc);

                        line.setTruckNo(WarehouseHelper.temp_load_plan.getTruckNo());

                        //Book packaging items
                        PackagingStockMovement pm = new PackagingStockMovement();
                        pm.bookMasterPack(bc.getCreateUser(),
                                bc.getPackType(), 1, "IN",
                                bc.getFGwarehouse(),
                                bc.getDestination(),
                                "Dispatch of finished goods to final destination " + bc.getDestination() + ".", bc.getPalletNumber());
                    }

                    Helper.startSession();
                    WarehouseHelper.temp_load_plan.setPlanState(WarehouseHelper.LOAD_PLAN_STATE_CLOSED);
                    WarehouseHelper.temp_load_plan.setEndTime(new Date());
                    WarehouseHelper.temp_load_plan.update(WarehouseHelper.temp_load_plan);

                    clearGui();

                    //Refresh Data
                    reloadPlansData();

                    //Go back to step S020
                    state = new S001_ReservPalletNumberScan();
                    WarehouseHelper.warehouse_reserv_context.setState(state);

                    Toolkit.getDefaultToolkit().beep();
                    setCursor(null);
                    JOptionPane.showMessageDialog(null, "Plan de chargement clôturé avec succès !\n");
                    //UILog.infoDialog(null, new String["Plan released !\n");                
                } else {
                    UILog.severe(ErrorMsg.APP_ERR0030[0], plan_num_label.getText());
                    UILog.severeDialog(null, ErrorMsg.APP_ERR0030, plan_num_label.getText());

                }

            }

        }
    }//GEN-LAST:event_close_plan_btnActionPerformed

    private void details_pallet_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_details_pallet_btnActionPerformed

        //addNewTab("Détails palette", new PACKAGING_UI0010_PalletDetails_JPANEL(parent, true, true, true, true), evt);
        GlobalMethods.addNewTabToParent("Détails palette", parent, new PACKAGING_UI0010_PalletDetails_JPANEL(parent, true, true, true, true, true), evt);
    }//GEN-LAST:event_details_pallet_btnActionPerformed

    private void fg_stock_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fg_stock_btnActionPerformed
        //addNewTab("Stock F.G", new PACKAGING_UI0021_FINISHED_GOODS_STOCK_JPANEL(parent), evt);
        GlobalMethods.addNewTabToParent("Stock F.G", parent, new PACKAGING_UI0021_FINISHED_GOODS_STOCK_JPANEL(parent), evt);
    }//GEN-LAST:event_fg_stock_btnActionPerformed

    private void delete_plan_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_plan_btnActionPerformed
        int confirmed = JOptionPane.showConfirmDialog(null,
                "Voulez-vous supprimer le plan de chargement "+plan_num_label.getText()+" ?", "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == 0) {
            Helper.startSession();
            Integer id = Integer.valueOf(plan_num_label.getText());
            Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PLAN_ID)
                    .setParameter("loadPlanId", id);
            Helper.sess.getTransaction().commit();
            List result = query.list();
            int lines = result.size();
            System.out.println("Le plan " + id + " contient " + lines + " Lines");

            if (lines == 0) {
                Helper.startSession();
                query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_BY_ID);
                query.setParameter("id", id);
                Helper.sess.getTransaction().commit();
                result = query.list();
                LoadPlan plan = (LoadPlan) result.get(0);

                //############ REMOVE THE PLAN LINES ###############
                query = Helper.sess.createQuery(HQLHelper.DEL_LOAD_PLAN_LINE_BY_PLAN_ID);
                query.setParameter("load_plan_id", plan.getId());
                query.executeUpdate();
                
                
                //############ REMOVE THE PLAN'S ODETTES ###############
                query = Helper.sess.createQuery(HQLHelper.DEL_DISPATCH_LABELS_BY_PLAN_ID);
                query.setParameter("load_plan_id", plan.getId());
                query.executeUpdate();

                plan.delete(plan);

                //Reload Load Plan list
                this.reloadPlansData();

                //Reset Load Plan Lines table
                this.reset_load_plan_lines_table_content();

                clearGui();
                //Go back to step S020
                state = new S001_ReservPalletNumberScan();
                WarehouseHelper.warehouse_reserv_context.setState(state);
            } else {
                UILog.severeDialog(null, ErrorMsg.APP_ERR0023);
                UILog.severe("Total lines = " + lines);
            }
        }
    }//GEN-LAST:event_delete_plan_btnActionPerformed

    private void labels_control_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labels_control_btnActionPerformed
        WarehouseHelper.Label_Control_Gui = new WAREHOUSE_DISPATCH_UI0009_LABELS_CONTROL(WarehouseHelper.temp_load_plan);
        ControlState _state_ = new S001_PalletNumberScan();
        WarehouseHelper.warehouse_control_context.setState(_state_);
        WarehouseHelper.Label_Control_Gui.setState(_state_);
        WarehouseHelper.warehouse_control_context.setUser(WarehouseHelper.warehouse_reserv_context.getUser());
        WarehouseHelper.Label_Control_Gui.setVisible(true);
        System.out.println("Intializing WarehouseHelper.Label_Control_Gui " + WarehouseHelper.Label_Control_Gui.toString());

    }//GEN-LAST:event_labels_control_btnActionPerformed

    private void pallet_list_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pallet_list_btnActionPerformed

        GlobalMethods.addNewTabToParent("Liste des palettes", this.parent, new PACKAGING_UI0020_PALLET_LIST_JPANEL(null, false), evt);
    }//GEN-LAST:event_pallet_list_btnActionPerformed

    private void transporteur_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transporteur_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_transporteur_txtActionPerformed

    private void current_plan_jpanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_current_plan_jpanelMouseClicked

    }//GEN-LAST:event_current_plan_jpanelMouseClicked

    private void tab5_packagingComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tab5_packagingComponentShown
        this.reloadPackagingContainerTab3(Integer.valueOf(plan_num_label.getText()));
    }//GEN-LAST:event_tab5_packagingComponentShown

    private void tab3_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tab3_refreshActionPerformed
        this.reloadPackagingContainerTab3(Integer.valueOf(plan_num_label.getText()));
    }//GEN-LAST:event_tab3_refreshActionPerformed

    private void tab4_labels_to_be_controledComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tab4_labels_to_be_controledComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_tab4_labels_to_be_controledComponentShown

    private void tab5_exampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tab5_exampleActionPerformed
        //Files.copy(".\\..\\..\\..\\lib\\odette_csv_example.csv", dst, StandardCopyOption.REPLACE_EXISTING);

        JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        chooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        chooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        Helper.centerJFileChooser(chooser);
        int status = chooser.showSaveDialog(null);

        if (status == JFileChooser.APPROVE_OPTION) {
            //FileOutputStream target = null;
            try {

                File selectedFile = chooser.getSelectedFile();
                //target.close();
                File source = new File(".\\src\\odette_csv_example.csv");
                System.out.println(" source " + source.getAbsolutePath() + ".csv");

                File dest = chooser.getSelectedFile();

                InputStream is = null;
                OutputStream os = null;
                try {
                    is = new FileInputStream(source);
                    os = new FileOutputStream(chooser.getSelectedFile() + ".csv");
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                } finally {
                    is.close();
                    os.close();
                }

                JOptionPane.showMessageDialog(null,
                    "Fichier enregistré à l'emplacement \n " + selectedFile.getAbsolutePath() + ".csv", "File saved !",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (FileNotFoundException ex) {
                //
                JOptionPane.showMessageDialog(null, "Le processus ne peut pas accéder au fichier car ce fichier est utilisé ou un fihier du même nom est ouvert.\n Fermer le fichier puis réessayer.", "Erreur de sauvegarde !", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(JDialogExcelFileChooser.class.getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                Logger.getLogger(JDialogExcelFileChooser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_tab5_exampleActionPerformed

    private void tab5_import_dispatch_labelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tab5_import_dispatch_labelsActionPerformed

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV", "csv");
            fileChooser.setFileFilter(filter);
            int status = fileChooser.showOpenDialog(null);

            if (status == JFileChooser.APPROVE_OPTION && deleteOdetteTable()) {

                File selectedFile = fileChooser.getSelectedFile();
                //Past the workbook to the file chooser
                String SAMPLE_CSV_FILE_PATH = selectedFile.getAbsolutePath();
                Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
                    .withDelimiter(';')
                );

                try {
                    int i = 1;
                    for (CSVRecord record : csvParser) {

                        //@Todo : Control sur la destination du plan
                        String destination = record.get("Destination");
                        String article = record.get("Article");
                        String quantite = record.get("Quantite");
                        String serialNo = record.get("NumSerie");

                        if (!destinationInList(destination)) {
                            UILog.errorDialog("Destination " + destination + " ne correspond pas à celle-ci du plan.\nErreur dans la ligne " + i);
                            break;
                        } else {
                            insertDispatchLabelLine(plan_num_label.getText(), record);
                            System.out.println(destination + "\t" + article + "\t" + quantite + "\t" + serialNo);
                            i++;
                        }
                    }

                    // In the end of the import, refresh the list
                    refreshOdetteTable();
                } catch (Exception ex) {
                    Logger.getLogger(WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
                    UILog.severeDialog(this, ex.getMessage(), "Exception");
                }
            } else if (status == JFileChooser.CANCEL_OPTION) {
                System.out.println("Canceled");
            }
        } catch (IOException ex) {
            Logger.getLogger(WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL.class.getName()).log(Level.SEVERE, null, ex);
            UILog.severeDialog(this, ex.getMessage(), "IOException");
        }
    }//GEN-LAST:event_tab5_import_dispatch_labelsActionPerformed

    private void tab5_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tab5_refreshActionPerformed
        refreshOdetteTable();
    }//GEN-LAST:event_tab5_refreshActionPerformed

    private void tab5_reset_labels_tableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tab5_reset_labels_tableActionPerformed

        deleteOdetteTable();

    }//GEN-LAST:event_tab5_reset_labels_tableActionPerformed

    private void tab3_total_pnKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tab3_total_pnKeyTyped
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim(), controlled_combobox_tab_2.getSelectedIndex());
    }//GEN-LAST:event_tab3_total_pnKeyTyped

    private void tab3_total_pnPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tab3_total_pnPropertyChange

    }//GEN-LAST:event_tab3_total_pnPropertyChange

    private void tab3_total_pnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab3_total_pnMouseClicked
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim(), controlled_combobox_tab_2.getSelectedIndex());
    }//GEN-LAST:event_tab3_total_pnMouseClicked

    private void tab3_total_pnFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tab3_total_pnFocusGained
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim(), controlled_combobox_tab_2.getSelectedIndex());
    }//GEN-LAST:event_tab3_total_pnFocusGained

    private void group_by_positionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_group_by_positionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_group_by_positionActionPerformed

    private void controlled_combobox_tab_2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_controlled_combobox_tab_2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_controlled_combobox_tab_2ItemStateChanged

    private void tab2_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tab2_refreshActionPerformed

        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim(), controlled_combobox_tab_2.getSelectedIndex());
    }//GEN-LAST:event_tab2_refreshActionPerformed

    private void tab2_packtypeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tab2_packtypeKeyTyped
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim(), controlled_combobox_tab_2.getSelectedIndex());
    }//GEN-LAST:event_tab2_packtypeKeyTyped

    private void tab2_cpnKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tab2_cpnKeyTyped
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim(), controlled_combobox_tab_2.getSelectedIndex());
    }//GEN-LAST:event_tab2_cpnKeyTyped

    private void tab2_destinationKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tab2_destinationKeyTyped
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim(), controlled_combobox_tab_2.getSelectedIndex());
    }//GEN-LAST:event_tab2_destinationKeyTyped

    private void btn_filter_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_filter_okActionPerformed
        load_plan_lines_table.setAutoCreateRowSorter(true);
        load_plan_lines_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "PILE NUM", "PALLET NUM", "CPN", "INTERNAL PN", "PACK TYPE", "PACK SIZE", "DESTINATION", "LINE ID", "FAMILY", "FIFO"
            }
        ));
        this.reloadPlansData();
        //this.loadPlanDataInGui();
        filterPlanLines(false);
    }//GEN-LAST:event_btn_filter_okActionPerformed

    private void set_packaging_pile_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_set_packaging_pile_btnActionPerformed
        new WAREHOUSE_DISPATCH_UI0008_SET_PACKAGING_OF_PILE(null, WarehouseHelper.temp_load_plan, selectedDestination);
    }//GEN-LAST:event_set_packaging_pile_btnActionPerformed

    private void controlled_comboboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_controlled_comboboxItemStateChanged
        filterPlanLines(false);
    }//GEN-LAST:event_controlled_comboboxItemStateChanged

    private void piles_boxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_piles_boxItemStateChanged
        //Set the values of destination and pile labels help
        try {
            destination_label_help.setText(selectedDestination);
            pile_label_help.setText(piles_box.getSelectedItem().toString());
            filterPlanLines(false);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_piles_boxItemStateChanged

    private void txt_filter_dispatchl_numberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_filter_dispatchl_numberKeyTyped
        filterPlanLines(false);
    }//GEN-LAST:event_txt_filter_dispatchl_numberKeyTyped

    private void txt_filter_pal_numberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_filter_pal_numberKeyTyped
        filterPlanLines(false);
    }//GEN-LAST:event_txt_filter_pal_numberKeyTyped

    private void txt_filter_partKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_filter_partKeyTyped
        filterPlanLines(false);
    }//GEN-LAST:event_txt_filter_partKeyTyped

    private void txt_filter_partActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_filter_partActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_filter_partActionPerformed

    private void plan_id_filterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_plan_id_filterKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                if (loadPlanDataInGui(Integer.valueOf(plan_id_filter.getText()))) {
                    current_plan_jpanel.setSelectedIndex(1);
                } else {
                    JOptionPane.showOptionDialog(null, "Plan " + plan_id_filter.getText() + " introuvable !", "Plan introuvable  !", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showOptionDialog(null, "Plan " + plan_id_filter.getText() + " introuvable !", "Plan introuvable  !", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
            }
        }
    }//GEN-LAST:event_plan_id_filterKeyPressed

    private void lp_filter_valKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lp_filter_valKeyTyped
        reloadPlansData();
    }//GEN-LAST:event_lp_filter_valKeyTyped

    private void lp_filter_valActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lp_filter_valActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lp_filter_valActionPerformed

    private void refresh_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_btnActionPerformed
        reloadPlansData();
    }//GEN-LAST:event_refresh_btnActionPerformed

    private void new_plan_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_plan_btnActionPerformed
        new WAREHOUSE_DISPATCH_UI0004_NEW_PLAN(null, true).setVisible(true);
    }//GEN-LAST:event_new_plan_btnActionPerformed

    private boolean destinationInList(String destination) {
        for (JRadioButton jRadioButton : radioButtonList) {
            if (destination.equals(jRadioButton.getText())) {
                return true;
            }
        }
        return false;
    }

    private void clearGui() {

        this.cleanDataLabels();

        //Clear mode2_context temp vars
        WarehouseHelper.warehouse_reserv_context.clearAllVars();

        //Clear lines from Jtable
        reset_load_plan_lines_table_content();

        //Disable delete button
        delete_plan_btn.setEnabled(false);

        //Disable End load button
        close_plan_btn.setEnabled(false);

        //Disable Export Excel button
        export_to_excel_btn.setEnabled(false);

        //Disable Edit plan button
        edit_plan_btn.setEnabled(false);

        labels_control_btn.setEnabled(false);

        piles_box.setEnabled(false);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add_new_plan_btn;
    private javax.swing.JScrollPane all_plans_scroll_panel;
    private javax.swing.JButton btn_filter_ok;
    private javax.swing.JButton close_plan_btn;
    private javax.swing.JLabel connectedUserName_label;
    private javax.swing.JComboBox controlled_combobox;
    private javax.swing.JComboBox controlled_combobox_tab_2;
    private javax.swing.JPanel controls_btn_panel;
    private javax.swing.JLabel create_time_label;
    private javax.swing.JLabel create_user_label;
    private javax.swing.JTabbedPane current_plan_jpanel;
    private javax.swing.JButton delete_plan_btn;
    private javax.swing.JLabel destination_label_help;
    private javax.swing.JPanel details_jpanel;
    private javax.swing.JButton details_pallet_btn;
    private javax.swing.JLabel dispatch_date_label;
    private javax.swing.JButton edit_plan_btn;
    private javax.swing.JButton export_to_excel_btn;
    private javax.swing.JButton fg_stock_btn;
    private javax.swing.JLabel fg_warehouse_label;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JCheckBox group_by_position;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel jpanel_destinations;
    private javax.swing.JTable jtable_odette_labels;
    private javax.swing.JTable jtable_total_packages;
    private javax.swing.JButton labels_control_btn;
    private javax.swing.JTable load_plan_lines_table;
    private javax.swing.JTable load_plan_table;
    private javax.swing.JTextField lp_filter_val;
    private javax.swing.JTextField message_label;
    private javax.swing.JButton new_plan_btn;
    private javax.swing.JButton pallet_list_btn;
    private javax.swing.JLabel pile_label_help;
    private javax.swing.JComboBox piles_box;
    private javax.swing.JFormattedTextField plan_id_filter;
    private javax.swing.JLabel plan_num_label;
    private javax.swing.JComboBox<String> plan_state_filter;
    private javax.swing.JLabel project_label;
    private javax.swing.JRadioButton radio_btn_20;
    private javax.swing.JRadioButton radio_btn_40;
    private javax.swing.JButton refresh_btn;
    private javax.swing.JLabel release_date_label;
    private javax.swing.JTextField scan_txt;
    private javax.swing.JButton set_packaging_pile_btn;
    private javax.swing.JLabel state_label;
    private javax.swing.JPanel tab1_all_plans_jpanel;
    private javax.swing.JTextField tab2_cpn;
    private javax.swing.JTextField tab2_destination;
    private javax.swing.JTextField tab2_packtype;
    private javax.swing.JPanel tab2_plan_details;
    private javax.swing.JButton tab2_refresh;
    private javax.swing.JLabel tab2_txt_nbreLigne;
    private javax.swing.JLabel tab2_txt_totalQty;
    private javax.swing.JButton tab3_refresh;
    private javax.swing.JPanel tab3_total_pn;
    private javax.swing.JPanel tab4_labels_to_be_controled;
    private javax.swing.JLabel tab4_txt_totalPackages;
    private javax.swing.JButton tab5_example;
    private javax.swing.JButton tab5_import_dispatch_labels;
    private javax.swing.JPanel tab5_packaging;
    private javax.swing.JButton tab5_refresh;
    private javax.swing.JButton tab5_reset_labels_table;
    private javax.swing.JLabel time_label1;
    private javax.swing.JLabel time_label10;
    private javax.swing.JLabel time_label11;
    private javax.swing.JLabel time_label2;
    private javax.swing.JLabel time_label3;
    private javax.swing.JLabel time_label4;
    private javax.swing.JLabel time_label5;
    private javax.swing.JLabel time_label6;
    private javax.swing.JLabel time_label7;
    private javax.swing.JLabel time_label8;
    private javax.swing.JLabel time_label9;
    private javax.swing.JTable total_per_pn_table;
    private javax.swing.JTextField transporteur_txt;
    private javax.swing.JTextField truck_no_txt1;
    private javax.swing.JTextField txt_filter_dispatchl_number;
    private javax.swing.JTextField txt_filter_pal_number;
    private javax.swing.JTextField txt_filter_part;
    private javax.swing.JTextField txt_gross_weight;
    private javax.swing.JLabel txt_nbreLigne;
    private javax.swing.JLabel txt_totalQty;
    private javax.swing.JTextField txt_total_hours;
    private javax.swing.JTextField txt_total_net_weight;
    private javax.swing.JTextField txt_total_value;
    private javax.swing.JTextField txt_total_volume;
    // End of variables declaration//GEN-END:variables

    public void loadPlanDataInGui(LoadPlan plan, String finalDest) {
        String id = plan_num_label.getText();
//        Helper.startSession();
//        Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_BY_ID);
//        query.setParameter("id", Integer.valueOf(id));
//
//        Helper.sess.getTransaction().commit();
//        List result = query.list();
//        LoadPlan plan = (LoadPlan) result.get(0);
        WarehouseHelper.temp_load_plan = plan;
        loadDestinationsRadioGroup(plan.getId());

        loadPlanDataToLabels(plan, "");
        reloadPlanLinesData(Integer.valueOf(id), finalDest);
        //loadDestinations(Integer.valueOf(id));
        //Disable delete button if the plan is CLOSED
        if (WarehouseHelper.LOAD_PLAN_STATE_CLOSED.equals(plan.getPlanState())) {
            delete_plan_btn.setEnabled(false);
            close_plan_btn.setEnabled(false);
            export_to_excel_btn.setEnabled(true);
            edit_plan_btn.setEnabled(false);
            labels_control_btn.setEnabled(false);
            piles_box.setEnabled(false);
            set_packaging_pile_btn.setEnabled(false);
        } else {
            if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT
                    || WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
                close_plan_btn.setEnabled(true);
            } else {
                close_plan_btn.setEnabled(false);
            }
            if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
                delete_plan_btn.setEnabled(true);
            } else {
                delete_plan_btn.setEnabled(false);
            }

            labels_control_btn.setEnabled(true);
            export_to_excel_btn.setEnabled(true);
            edit_plan_btn.setEnabled(true);
            piles_box.setEnabled(true);
            set_packaging_pile_btn.setEnabled(true);
            scan_txt.setEnabled(true);
            radio_btn_20.setEnabled(true);
            radio_btn_40.setEnabled(true);
        }
    }

    /**
     * Filter the plan lines according to the give values
     *
     * @param pass
     */
    public void filterPlanLines(boolean pass) {

        if (!"*".equals(piles_box.getSelectedItem().toString())) {
            try {
                int pile = Integer.parseInt(piles_box.getSelectedItem().toString());
                filterPlanLines(
                        Integer.valueOf(plan_num_label.getText()),
                        selectedDestination,
                        txt_filter_part.getText().trim(),
                        pile,
                        controlled_combobox.getSelectedIndex(),
                        txt_filter_pal_number.getText().trim(),
                        txt_filter_dispatchl_number.getText().trim());

            } catch (NumberFormatException e) {
                filterPlanLines(
                        Integer.valueOf(plan_num_label.getText()),
                        selectedDestination,
                        txt_filter_part.getText().trim(),
                        0,
                        controlled_combobox.getSelectedIndex(),
                        txt_filter_pal_number.getText().trim(),
                        txt_filter_dispatchl_number.getText().trim());
            }
        } else {
            if (!plan_num_label.getText().equals("#")) {
                filterPlanLines(
                        Integer.valueOf(plan_num_label.getText()),
                        selectedDestination,
                        txt_filter_part.getText().trim(), 0, controlled_combobox.getSelectedIndex(),
                        txt_filter_pal_number.getText().trim(),
                        txt_filter_dispatchl_number.getText().trim());
            }
        }
    }

    private void exportPlanDetails() {
        if (plan_num_label.getText().equals("#")) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0031);
            return;
        }

        //Create the excel workbook
        Workbook wb = new XSSFWorkbook(); // new HSSFWorkbook();
        Sheet sheet1 = wb.createSheet("PILES_DETAILS");
        Sheet sheet2 = wb.createSheet("PILES_GROUPED");
        Sheet sheet4 = wb.createSheet("TOTAL_PACKAGING");
        Sheet sheet5 = wb.createSheet("TOTAL_STOCK_PER_DESTINATION");
        CreationHelper createHelper = wb.getCreationHelper();

        //######################################################################
        //##################### SHEET 1 : PILES DETAILS ########################
        //Initialiser les entête du fichier
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet1.createRow((short) 0);
        // Create a cell and put a value in it.    
        //N° LINE
        row.createCell(0).setCellValue("PILE NUM");
        row.createCell(1).setCellValue("PALLET NUM");
        row.createCell(2).setCellValue("CUSTOMER PN");
        row.createCell(3).setCellValue("INTERNAL PN");
        row.createCell(4).setCellValue("PACK TYPE");
        row.createCell(5).setCellValue("QTY");
        row.createCell(6).setCellValue("ORDER NUM");
        row.createCell(7).setCellValue("TYPE");
        row.createCell(8).setCellValue("DISPATCH LABEL NO");
        row.createCell(9).setCellValue("DESTINATION");
        row.createCell(10).setCellValue("N° LINE");
        //Load lines of the actual loading plan
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PLAN_ID_ASC);
        query.setParameter("loadPlanId", Integer.valueOf(plan_num_label.getText()));

        Helper.sess.getTransaction().commit();
        List result = query.list();

        short sheetPointer = 1;

        for (Object o : result) {
            LoadPlanLine lpl = (LoadPlanLine) o;
            row = sheet1.createRow(sheetPointer);
            row.createCell(0).setCellValue(lpl.getPileNum());
            row.createCell(1).setCellValue(Integer.valueOf(lpl.getPalletNumber()));
            try {
                row.createCell(2).setCellValue(Integer.valueOf(lpl.getHarnessPart()));
            } catch (NumberFormatException e) {
                row.createCell(2).setCellValue(lpl.getHarnessPart());
            }
            row.createCell(3).setCellValue(lpl.getSupplierPart());
            row.createCell(4).setCellValue(lpl.getPackType());
            row.createCell(5).setCellValue(lpl.getQty());
            row.createCell(6).setCellValue(lpl.getOrderNum());
            row.createCell(7).setCellValue(lpl.getHarnessType());
            row.createCell(8).setCellValue((lpl.getDispatchLabelNo().startsWith(GlobalVars.DISPATCH_SERIAL_NO_PREFIX)) ? lpl.getDispatchLabelNo().substring(0) : lpl.getDispatchLabelNo());
            row.createCell(9).setCellValue(lpl.getDestinationWh());
            row.createCell(10).setCellValue(lpl.getId());
            sheetPointer++;
        }

        //######################################################################
        //##################### SHEET 2 : PILES SUMMARY ########################
        short sheet2Pointer = 0;

        Row row2 = sheet2.createRow(sheet2Pointer);
        sheet2Pointer++;
        // Create a cell and put a value in it.    

        row2.createCell(0).setCellValue("FAMILY");
        row2.createCell(1).setCellValue("PILE NUM");
        row2.createCell(2).setCellValue("CUSTOMER PN");
        row2.createCell(3).setCellValue("INDEX");
        row2.createCell(4).setCellValue("LEONI PN");
        row2.createCell(5).setCellValue("TOTAL QTY");
        row2.createCell(6).setCellValue("UCS QTY");
        row2.createCell(7).setCellValue("PACK TYPE");
        row2.createCell(8).setCellValue("NBRE PACK");
        row2.createCell(9).setCellValue("ORDER NUM");
        row2.createCell(10).setCellValue("DESTINATION");

        Helper.startSession();

        SQLQuery query2 = Helper.sess.createSQLQuery(String.format(HQLHelper.GET_LOAD_PLAN_LINE_GROUPED_BY_PILES, plan_num_label.getText()));

        query2.addScalar("harness_type", StandardBasicTypes.STRING) //0
                .addScalar("pile_num", StandardBasicTypes.INTEGER) //1
                .addScalar("harness_part", StandardBasicTypes.STRING)//2
                .addScalar("harness_index", StandardBasicTypes.STRING)//3
                .addScalar("supplier_part", StandardBasicTypes.STRING)//4
                .addScalar("total_qty", StandardBasicTypes.INTEGER)//5
                .addScalar("qty", StandardBasicTypes.INTEGER)//6
                .addScalar("pack_type", StandardBasicTypes.STRING)//7
                .addScalar("nbre_pack", StandardBasicTypes.INTEGER)//8
                .addScalar("order_num", StandardBasicTypes.STRING)//9
                .addScalar("destination_wh", StandardBasicTypes.STRING);//10

        List<Object[]> result2 = query2.list();
        Helper.sess.getTransaction().commit();
        for (Object[] obj : result2) {
            row2 = sheet2.createRow(sheet2Pointer);

            row2.createCell(0).setCellValue((String) obj[0]);
            row2.createCell(1).setCellValue((Integer) obj[1]);
            row2.createCell(2).setCellValue((String) obj[2]);
            row2.createCell(3).setCellValue((String) obj[3]);
            row2.createCell(4).setCellValue((String) obj[4]);
            row2.createCell(5).setCellValue((Integer) obj[5]);
            row2.createCell(6).setCellValue((Integer) obj[6]);
            row2.createCell(7).setCellValue((String) obj[7]);
            row2.createCell(8).setCellValue((Integer) obj[8]);
            try {
                row2.createCell(9).setCellValue(Integer.valueOf(obj[9].toString()));
            } catch (Exception e) {
                row2.createCell(9).setCellValue("");
            }
            row2.createCell(10).setCellValue((String) obj[10]);
            sheet2Pointer++;
        }

        //######################################################################
        //##################### SHEET 4 : PACKAGIN MOUVEMENTS ########################
        short sheet4Pointer = 0;

        Row row4 = sheet4.createRow(sheet4Pointer);
        sheet4Pointer++;
        // Create a cell and put a value in it.    

        row4.createCell(0).setCellValue("DESTINATION");
        row4.createCell(1).setCellValue("PACK INTEM");
        row4.createCell(2).setCellValue("QTY");

        Helper.startSession();
        String query_str = String.format(
                HQLHelper.GET_LOAD_PLAN_EXT_PACKAGING_AND_CONTAINER,
                Integer.valueOf(this.plan_num_label.getText()), Integer.valueOf(this.plan_num_label.getText()));
        SQLQuery query4 = Helper.sess.createSQLQuery(query_str);

        query4.addScalar("destination", StandardBasicTypes.STRING);
        query4.addScalar("pack_item", StandardBasicTypes.STRING);
        query4.addScalar("quantity", StandardBasicTypes.DOUBLE);

        List<Object[]> result4 = query4.list();
        Helper.sess.getTransaction().commit();

        for (Object[] obj : result4) {
            row4 = sheet4.createRow(sheet4Pointer);
            row4.createCell(0).setCellValue((String) obj[0]);
            row4.createCell(1).setCellValue((String) obj[1]);
            row4.createCell(2).setCellValue((Double) obj[2]);
            sheet4Pointer++;
        }
        /*
         ####################SHEET 5 : LABEST MASK ########################*/
        short sheet5Pointer = 0;

        Row row5 = sheet5.createRow(sheet5Pointer);
        sheet5Pointer++;
        // Create a cell and put a value in it.    

        row5.createCell(0).setCellValue("FAMILY");
        row5.createCell(1).setCellValue("CPN");
        row5.createCell(2).setCellValue("INDEX");
        row5.createCell(3).setCellValue("LPN");
        row5.createCell(4).setCellValue("TOTAL QTY");
        row5.createCell(5).setCellValue("ORDER NUM");
        row5.createCell(6).setCellValue("DESTINATION");

        Helper.startSession();

        SQLQuery query5 = Helper.sess.createSQLQuery(String.format(HQLHelper.GET_LOAD_PLAN_STOCK_PER_FDP, plan_num_label.getText()));
        query5.addScalar("harness_type", StandardBasicTypes.STRING)
                .addScalar("harness_part", StandardBasicTypes.STRING)
                .addScalar("harness_index", StandardBasicTypes.STRING)
                .addScalar("supplier_part", StandardBasicTypes.STRING)
                .addScalar("total_qty", StandardBasicTypes.INTEGER)
                .addScalar("order_num", StandardBasicTypes.STRING)
                .addScalar("destination_wh", StandardBasicTypes.STRING);

        List<Object[]> result5 = query5.list();
        Helper.sess.getTransaction().commit();
        for (Object[] obj : result5) {
            row5 = sheet5.createRow(sheet5Pointer);
            row5.createCell(0).setCellValue((String) obj[0]);
            row5.createCell(1).setCellValue((String) obj[1]);
            row5.createCell(2).setCellValue((String) obj[2]);
            row5.createCell(3).setCellValue((String) obj[3]);
            row5.createCell(4).setCellValue((Integer) obj[4]);
            try {
                row5.createCell(5).setCellValue((Integer) obj[5]);
            } catch (Exception e) {
                row5.createCell(5).setCellValue("");
            }
            row5.createCell(6).setCellValue((String) obj[6]);
            sheet5Pointer++;
        }

        //Past the workbook to the file chooser
        new JDialogExcelFileChooser(null, true, wb).setVisible(true);
    }
}
