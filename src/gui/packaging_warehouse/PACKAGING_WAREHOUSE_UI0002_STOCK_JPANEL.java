/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging_warehouse;

import __main__.GlobalVars;
import entity.ConfigWarehouse;
import helper.ComboItem;
import helper.Helper;
import helper.JDialogExcelFileChooser;
import helper.NumberRenderer;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 *
 * @author Administrator
 */
public class PACKAGING_WAREHOUSE_UI0002_STOCK_JPANEL extends javax.swing.JPanel {
    
    JTabbedPane parent;
    Vector packaging_stock_result_table_data = new Vector();
    Vector<String> packaging_stock_result_table_header = new Vector<String>();
    private List<Object[]> resultList;
    List<String> table_header = Arrays.asList(
            "WAREHOUSE",
            "PART",
            "QTY"
    );

    /**
     * Creates new form UI0011_ProdStatistics_
     */
    public PACKAGING_WAREHOUSE_UI0002_STOCK_JPANEL(JTabbedPane parent) {
        this.parent = parent;
        initComponents();
        initGui();
    }

    

    private void initGui() {
        //Center the this dialog in the screen
        //Helper.centerJDialog(this);
        //Helper.centerJFrame(this);

        //Desable table edition
        disableEditingTables();

        //Load table header
        load_table_header();

        initPackagingWarehouse();

        this.setVisible(true);

    }

    private void load_table_header() {
        this.reset_table_content();

        for (Iterator<String> it = table_header.iterator(); it.hasNext();) {
            packaging_stock_result_table_header.add(it.next());
        }
    }

    public void reset_table_content() {
        packaging_stock_result_table_data = new Vector();
        packaging_stock_table.setModel(new DefaultTableModel(packaging_stock_result_table_data, packaging_stock_result_table_header));
    }

    public void disableEditingTables() {
        for (int c = 0; c < packaging_stock_table.getColumnCount(); c++) {
            // remove editor   
            Class<?> col_class = packaging_stock_table.getColumnClass(c);
            packaging_stock_table.setDefaultEditor(col_class, null);
        }
        packaging_stock_table.setAutoCreateRowSorter(true);
        
    }

    private void initPackagingWarehouse() {
        List result = new ConfigWarehouse().selectWarehousesByType("PACKAGING");
        if (result.isEmpty()) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0042);
            UILog.severe(ErrorMsg.APP_ERR0042[1]);
        } else { //Map project data in the list
            packaging_wh_box.removeAllItems();
            //packaging_wh_box.addItem(new ComboItem("", ""));
            packaging_wh_box.addItem("");
            for (Object o : result) {
                ConfigWarehouse cp = (ConfigWarehouse) o;
                //packaging_wh_box.addItem(new ComboItem(cp.getWarehouse(), cp.getWarehouse()));
                packaging_wh_box.addItem(cp.getWarehouse());
            }
        }
    }

    @SuppressWarnings("empty-statement")
    public void reload_result_table_data(List<Object[]> resultList) {

        for (Object[] obj : resultList) {

            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(String.valueOf(obj[0])); // WAREHOUSE
            oneRow.add(String.valueOf(obj[1])); // PART
            oneRow.add(new DecimalFormat("00.00").format(Double.parseDouble(obj[2].toString()))); // STOCK
            packaging_stock_result_table_data.add(oneRow);
        }
        packaging_stock_table.setModel(new DefaultTableModel(packaging_stock_result_table_data, packaging_stock_result_table_header));
        packaging_stock_table.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.BOLD, Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONTSIZE"))));
        packaging_stock_table.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));
        
        TableColumnModel m = packaging_stock_table.getColumnModel();
        m.getColumn(2).setCellRenderer(NumberRenderer.getCurrencyRenderer());
    }

    public void refresh() {
        //Clear all tables
        this.reset_table_content();

        try {

            //#################  #################### 
            Helper.startSession();
            String query_str = "SELECT "
                    + "warehouse AS warehouse, "
                    + "pack_item AS pack_item, "
                    + "SUM(qty) AS qty "
                    + "FROM "
                    + "("
                    + "SELECT "
                    + "p.warehouse AS warehouse, "
                    + "p.pack_item AS pack_item, "
                    + "p.quantity AS qty "
                    + "FROM "
                    + "packaging_stock_movement p WHERE 1=1 ";
            if (!packaging_wh_box.getSelectedItem().toString().equals("")) {
                query_str += " AND warehouse IN ('" + packaging_wh_box.getSelectedItem().toString() + "') ";
            }
            if (!packaging_item.getText().equals("")) {
                query_str += " AND UPPER(pack_item) LIKE '%" + packaging_item.getText().toUpperCase() + "%' ";
            }
            query_str += ") AS Q1 ";
            query_str += "GROUP BY warehouse, pack_item ";
            query_str += "ORDER BY warehouse, pack_item; ";
            
            SQLQuery query = Helper.sess.createSQLQuery(query_str);

            query.addScalar("warehouse", StandardBasicTypes.STRING)
                    .addScalar("pack_item", StandardBasicTypes.STRING)
                    .addScalar("qty", StandardBasicTypes.DOUBLE);
            resultList = query.list();

            Helper.sess.getTransaction().commit();

            this.reload_result_table_data(resultList);

            this.disableEditingTables();

        } catch (HibernateException e) {
            if (Helper.sess.getTransaction() != null) {
                Helper.sess.getTransaction().rollback();
            }
            e.printStackTrace();
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

        north_panel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        packaging_stock_table = new javax.swing.JTable();
        refresh_btn1 = new javax.swing.JButton();
        export_btn = new javax.swing.JButton();
        packaging_wh_box = new javax.swing.JComboBox();
        fname_lbl14 = new javax.swing.JLabel();
        packaging_item = new javax.swing.JTextField();
        fname_lbl15 = new javax.swing.JLabel();

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        north_panel.setBackground(new java.awt.Color(36, 65, 86));
        north_panel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                north_panelKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Stock Packaging");

        packaging_stock_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        packaging_stock_table.setColumnSelectionAllowed(true);
        packaging_stock_table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        packaging_stock_table.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(packaging_stock_table);

        refresh_btn1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        refresh_btn1.setText("Actualiser");
        refresh_btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_btn1ActionPerformed(evt);
            }
        });

        export_btn.setText("Exporter en Excel...");
        export_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                export_btnActionPerformed(evt);
            }
        });

        packaging_wh_box.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        packaging_wh_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packaging_wh_boxActionPerformed(evt);
            }
        });

        fname_lbl14.setForeground(new java.awt.Color(255, 255, 255));
        fname_lbl14.setText("Warehouse");

        packaging_item.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                packaging_itemKeyPressed(evt);
            }
        });

        fname_lbl15.setForeground(new java.awt.Color(255, 255, 255));
        fname_lbl15.setText("Packaging Item");

        javax.swing.GroupLayout north_panelLayout = new javax.swing.GroupLayout(north_panel);
        north_panel.setLayout(north_panelLayout);
        north_panelLayout.setHorizontalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 578, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fname_lbl14)
                            .addComponent(fname_lbl15))
                        .addGap(18, 18, 18)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(packaging_item)
                            .addComponent(packaging_wh_box, 0, 203, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(refresh_btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(export_btn))))
                .addGap(237, 428, Short.MAX_VALUE))
        );
        north_panelLayout.setVerticalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(packaging_wh_box, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(export_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(fname_lbl14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(packaging_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fname_lbl15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(refresh_btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 764, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(north_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(north_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
           // this.dispose();
        }
    }//GEN-LAST:event_formKeyPressed

    private void north_panelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_north_panelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            //this.dispose();
        }
    }//GEN-LAST:event_north_panelKeyPressed

    private void refresh_btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_btn1ActionPerformed
        refresh();
    }//GEN-LAST:event_refresh_btn1ActionPerformed

    private void export_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export_btnActionPerformed

        //Create the excel workbook
        Workbook wb = new XSSFWorkbook(); // new HSSFWorkbook();
        Sheet sheet = wb.createSheet("PACKAGING STOCK");
        CreationHelper createHelper = wb.getCreationHelper();
        DataFormat df = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        cs.setDataFormat(df.getFormat("0.00"));
        //######################################################################
        //##################### SHEET 1 : PILES DETAILS ########################
        //Initialiser les entête du fichier
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow((short) 0);

        row.createCell(0).setCellValue("WAREHOUSE");
        row.createCell(1).setCellValue("PART");
        row.createCell(2).setCellValue("QTY");

        short sheetPointer = 1;

        for (Object[] obj : this.resultList) {
            row = sheet.createRow(sheetPointer);
            row.createCell(0).setCellValue(String.valueOf(obj[0]));
            row.createCell(1).setCellValue(String.valueOf(obj[1]));
            Cell cell = row.createCell(2);            
            cell.setCellStyle(cs);
            cell.setCellValue((double) obj[2]);            
            sheetPointer++;
        }
        //Past the workbook to the file chooser
        new JDialogExcelFileChooser(null, true, wb).setVisible(true);
    }//GEN-LAST:event_export_btnActionPerformed

    private void packaging_wh_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packaging_wh_boxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_packaging_wh_boxActionPerformed

    private void packaging_itemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_packaging_itemKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
           refresh();
        }
    }//GEN-LAST:event_packaging_itemKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton export_btn;
    private javax.swing.JLabel fname_lbl14;
    private javax.swing.JLabel fname_lbl15;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel north_panel;
    private javax.swing.JTextField packaging_item;
    private javax.swing.JTable packaging_stock_table;
    private javax.swing.JComboBox packaging_wh_box;
    private javax.swing.JButton refresh_btn1;
    // End of variables declaration//GEN-END:variables
}
