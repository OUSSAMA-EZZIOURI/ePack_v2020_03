package gui.cra;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import entity.ProductionPlan;
import gui.warehouse_dispatch.WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL;
import helper.HQLHelper;
import helper.Helper;
import helper.JDialogExcelFileChooser;
import helper.UIHelper;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Query;
import ui.UILog;

/**
 *
 * @author Oussama
 */
public class CRA_UI0001_PRODUCTION_PLAN extends javax.swing.JPanel {

    private JTabbedPane parent;
    Vector<String> planning_table_header = new Vector<String>(Arrays.asList(
            "ID",
            "Article",
            "Module Intern.",
            "Quantité plannif.",
            "Dernière modif.",
            "Modifié par."
    ));

    Vector planning_table_data = new Vector();
    ProductionPlan aux;
    boolean err = false;

    /**
     * Creates new form CRA_UI0001_PRODUCTION_PLAN
     */
    public CRA_UI0001_PRODUCTION_PLAN() {
        initComponents();
    }

    public CRA_UI0001_PRODUCTION_PLAN(JTabbedPane rootTabbedPane) {
        this.parent = rootTabbedPane;
        initComponents();
        initGui();
    }

    private void initGui() {
        planning_jtable.setModel(new DefaultTableModel(new Vector(), planning_table_header));
        UIHelper.disableEditingJtable(planning_jtable);
        this.initContainerTableDoubleClick();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        planning_jtable = new javax.swing.JTable();
        btn_delete_planning = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btn_import_csv = new javax.swing.JButton();
        btn_csv_example = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_harness_part = new javax.swing.JTextField();
        txt_internal_part = new javax.swing.JTextField();
        txt_qty_planned = new javax.swing.JTextField();
        btn_delete = new javax.swing.JButton();
        msg_lbl = new javax.swing.JLabel();
        txt_id = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btn_save = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txt_harness_part_filter = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt_internal_part_filter = new javax.swing.JTextField();
        btn_export_excel = new javax.swing.JButton();
        btn_refresh = new javax.swing.JButton();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setBackground(new java.awt.Color(36, 65, 86));
        setToolTipText("Planning d'assemblage");
        setName("Planning d'assemblage"); // NOI18N

        planning_jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(planning_jtable);

        btn_delete_planning.setBackground(new java.awt.Color(255, 0, 51));
        btn_delete_planning.setForeground(new java.awt.Color(255, 255, 255));
        btn_delete_planning.setText("Supprimer le planning");
        btn_delete_planning.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_delete_planningActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Planning d'assemblage");

        jPanel1.setBackground(new java.awt.Color(36, 65, 86));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btn_import_csv.setText("Importer le planning .csv ...");
        btn_import_csv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_import_csvActionPerformed(evt);
            }
        });

        btn_csv_example.setText("Exemple fichier .csv ...");
        btn_csv_example.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_csv_exampleActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Article faisceau");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Code interne");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Quantité planifiée");

        txt_qty_planned.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_qty_plannedFocusLost(evt);
            }
        });

        btn_delete.setText("Supprimer");
        btn_delete.setEnabled(false);
        btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteActionPerformed(evt);
            }
        });

        msg_lbl.setForeground(new java.awt.Color(255, 255, 255));

        txt_id.setEditable(false);
        txt_id.setText("#");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("ID");

        btn_save.setText("Enregistrer");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        jButton1.setText("Initialiser");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_harness_part, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                            .addComponent(txt_internal_part)
                            .addComponent(txt_qty_planned))
                        .addGap(103, 103, 103)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btn_save)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_delete))
                            .addComponent(btn_csv_example, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_import_csv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
            .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 958, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txt_harness_part, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_csv_example))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txt_internal_part, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_import_csv))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_qty_planned, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_save)
                    .addComponent(btn_delete)
                    .addComponent(jButton1))
                .addGap(20, 20, 20)
                .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Article faisceau");

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Code interne");

        btn_export_excel.setText("Exporter en Excel");
        btn_export_excel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_export_excelActionPerformed(evt);
            }
        });

        btn_refresh.setText("Actualiser");
        btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txt_harness_part_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txt_internal_part_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addComponent(btn_refresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_export_excel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txt_harness_part_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txt_internal_part_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_export_excel)
                    .addComponent(btn_refresh))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_delete_planning, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_delete_planning, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_csv_exampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_csv_exampleActionPerformed

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
                File source = new File(".\\src\\production_plan_csv_example.csv");
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
    }//GEN-LAST:event_btn_csv_exampleActionPerformed

    private boolean deletePlanning() {
        int confirmed = JOptionPane.showConfirmDialog(
                this.parent.getParent(),
                "Le planning actuel va être écraser ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirmed == 0) {
            Helper.startSession();
            Query query = Helper.sess.createQuery(HQLHelper.DEL_PRODUCTION_PLAN);
            int result = query.executeUpdate();
            JOptionPane.showMessageDialog(null, "Planning supprimé !\n");
            planning_table_data = new Vector();
            refreshPlanningTable();
            return true;
        }

        return false;
    }

    private void initContainerTableDoubleClick() {
        this.planning_jtable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    //Cleat the message field
                    msg_lbl.setText("");

                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_PLANNING_LINE_BY_ID);
                    query.setParameter("id", planning_jtable.getValueAt(planning_jtable.getSelectedRow(), 0));

                    Helper.sess.getTransaction().commit();
                    aux = (ProductionPlan) query.list().get(0);
                    txt_id.setText(aux.getId() + "");
                    txt_harness_part.setText(aux.getHarnessPart());
                    txt_internal_part.setText(aux.getInternalPart());

                    txt_qty_planned.setText(aux.getPlannedQty().toString());
                    btn_delete.setEnabled(true);
                }
            }
        }
        );
    }
    private void btn_import_csvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_import_csvActionPerformed

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop/"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV", "csv");
            fileChooser.setFileFilter(filter);
            int status = fileChooser.showOpenDialog(null);

            if (status == JFileChooser.APPROVE_OPTION && deletePlanning()) {

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
                        String harness_part = record.get("harness_part");
                        String internal_part = record.get("internal_part");
                        String planed_qty = record.get("planned_qty");
                        try {
                            createNewPlanningLine(record);
                            System.out.println(harness_part + "\t" + internal_part + "\t" + planed_qty);
                        } catch (Exception e) {
                            System.out.println("Erreur dans la ligne " + i);
                        }
                        i++;
                    }
                    UILog.info("Importation terminée !");

                    // In the end of the import, refresh the list
                    refreshPlanningTable();
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
    }//GEN-LAST:event_btn_import_csvActionPerformed

    private void btn_delete_planningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_delete_planningActionPerformed

        deletePlanning();
        //GlobalMethods.refreshJTable(planning_jtable, new Vector(), header_titles_list);
        refreshPlanningTable();
    }//GEN-LAST:event_btn_delete_planningActionPerformed

    private void btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refreshActionPerformed
        refreshPlanningTable();
    }//GEN-LAST:event_btn_refreshActionPerformed

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        int lineId = 0;
        //If it's a modification, we look for an existing line with a different id != 0
        //and not this one that we want to edit.
        if (!txt_id.getText().equals("#")) {
            lineId = Integer.valueOf(txt_id.getText());
        }
        //Is all required field are set
        if (txt_harness_part.getText().equals("")
                || txt_internal_part.getText().equals("")
                || txt_qty_planned.getText().equals("")) {
            err = true;
            String[] msg = {"Merci de saisir toutes les valeurs"};
            msg_lbl.setText(msg[0]);
        } // Is planned qty null
        else if (!GlobalMethods.isNumeric(txt_qty_planned.getText()) || Integer.valueOf(txt_qty_planned.getText()) == 0) {
            String[] msg = {"Valeur numérique incorrecte! Saisir un entier supérieur à 0."};
            UILog.errorDialog(msg[0]);
            txt_qty_planned.requestFocus();
            txt_qty_planned.selectAll();
        } // Is the combinaison of the harness part and internal harness part
        else if (is_CPN_OR_LPN_Planned(txt_harness_part.getText(), txt_internal_part.getText(), lineId)) {
            String[] msg = {"Article ou/et Module interne déjà planifiés!"};
            UILog.errorDialog(msg[0]);
            txt_harness_part.requestFocus();
            txt_harness_part.selectAll();
            txt_internal_part.requestFocus();
            txt_internal_part.selectAll();

        } //Is the internal part exist in production plan
        else if (isInternalPartPlanned(txt_internal_part.getText(), lineId)) {
            String[] msg = {"Le module " + txt_internal_part.getText() + " déjà planifié!"};
            UILog.errorDialog(msg[0]);
            txt_internal_part.requestFocus();
            txt_internal_part.selectAll();
        } else {
            if (txt_id.getText().equals("#")) {//New item
                //@to do ajouter les controls du formulaire
                try {
                    ProductionPlan p = new ProductionPlan(
                            txt_harness_part.getText(), txt_internal_part.getText(), Integer.valueOf(txt_qty_planned.getText()));
                    p.create(p);
                    clearFields();
                    String[] msg = {"Nouveau élement créé"};
                    msg_lbl.setText(msg[0]);
                    refreshPlanningTable();
                } catch (Exception e) {
                    String[] msg = {"Valeur numérique incorrecte! Saisir un entier supérieur à 0."};
                    msg_lbl.setText(msg[0]);
                    txt_qty_planned.requestFocus();
                    txt_qty_planned.selectAll();
                }

            } else { // it's a modification
                if (isInternalPartPlanned(txt_internal_part.getText(), Integer.valueOf(txt_id.getText()))) {
                    String[] msg = {"Harness part et Internal part existe déjà dans la base!"};
                    UILog.errorDialog(msg[0]);
                    txt_internal_part.requestFocus();
                    txt_internal_part.selectAll();
                }
                try {
                    aux.setHarnessPart(txt_harness_part.getText());
                    aux.setInternalPart(txt_internal_part.getText());

                    aux.setPlannedQty(Integer.valueOf(txt_qty_planned.getText()));

                    aux.setWriteId(GlobalVars.CONNECTED_USER.getId());
                    aux.setWriteUser(GlobalVars.CONNECTED_USER.getFirstName() + " " + GlobalVars.CONNECTED_USER.getLastName());
                    aux.setWriteTime(new Date());
                    aux.update(aux);
                    clearFields();
                    String[] msg = {"Changements enregistrés"};
                    msg_lbl.setText(msg[0]);
                    refreshPlanningTable();
                } catch (Exception e) {
                    String[] msg = {"Valeur numérique incorrecte! Saisir un entier supérieur à 0."};
                    msg_lbl.setText(msg[0]);
                    txt_qty_planned.requestFocus();
                    txt_qty_planned.selectAll();
                }
            }

        }
    }//GEN-LAST:event_btn_saveActionPerformed

    private void txt_qty_plannedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_qty_plannedFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_qty_plannedFocusLost

    private void btn_export_excelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_export_excelActionPerformed
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
        fileChooser.setSelectedFile(new File("PLANNING.csv"));
        Helper.centerJFileChooser(fileChooser);
        int j = fileChooser.showSaveDialog(this);
        if (j != 0) {
            return;
        }
        String str = fileChooser.getSelectedFile().getAbsolutePath();

        try (FileWriter fw = new FileWriter(str)) {
            try (CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT
                    .withHeader("harness_part", "internal_part", "planned_qty"))) {
                for (int i = 0; i < this.planning_jtable.getRowCount(); i++) {
                    printer.printRecord(
                            planning_jtable.getValueAt(i, 1).toString(), //harness_part
                            planning_jtable.getValueAt(i, 2).toString(), //internal_part
                            Integer.valueOf(planning_jtable.getValueAt(i, 3).toString())//planned_qty
                    );
                }
            }
            UILog.infoDialog("Fichier enregistré dans " + fileChooser.getSelectedFile());
        } catch (IOException ex) {
            Logger.getLogger(CRA_UI0001_PRODUCTION_PLAN.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_btn_export_excelActionPerformed

    private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteActionPerformed

        int confirmed = JOptionPane.showConfirmDialog(this,
                String.format("Confirmez-vous la suppression de cet élément [%s] ?",
                        this.aux.getId()),
                "Suppression",
                JOptionPane.WARNING_MESSAGE);

        if (confirmed == 0) {
            aux.delete(aux);
            clearFields();
            msg_lbl.setText("Elément supprimé !");
            refreshPlanningTable();
        }
    }//GEN-LAST:event_btn_deleteActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        clearFields();
    }//GEN-LAST:event_jButton1ActionPerformed

    private Vector getPlanningLines() {
        planning_table_data = new Vector();
        Helper.startSession();

        Query query = Helper.sess.createQuery(HQLHelper.GET_PLANNING_LINE_LIKE_CPN_AND_LPN);
        query.setParameter("harnessPart", "%" + txt_harness_part_filter.getText() + "%");
        query.setParameter("internalPart", "%" + txt_internal_part_filter.getText() + "%");
        List<ProductionPlan> result = query.list();
        Helper.sess.getTransaction().commit();

        //Populate the jTable with lines
        for (ProductionPlan obj : result) {
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(obj.getId());
            oneRow.add(obj.getHarnessPart());
            oneRow.add(obj.getInternalPart());
            oneRow.add(obj.getPlannedQty());
            oneRow.add(new SimpleDateFormat("YYYY-MM-DD HH:mm:ss").format(obj.getWriteTime()));
            oneRow.add(obj.getWriteUser());

            planning_table_data.add(oneRow);
        }
        return planning_table_data;
    }

    private boolean is_CPN_OR_LPN_Planned(String harnessPart, String internalPart, int id) {
        Query query = Helper.sess.createQuery(HQLHelper.GET_PLANNING_LINE_BY_CPN_OR_LPN_WITH_DIFF_ID);
        query.setParameter("harnessPart", harnessPart);
        query.setParameter("internalPart", internalPart);
        query.setParameter("id", id);
        List<ProductionPlan> result = query.list();
        if (result.size() > 0) {
            return true;
        }
        return false;
    }

    private boolean isInternalPartPlanned(String internalPart, int id) {
        Query query = Helper.sess.createQuery(HQLHelper.GET_PLANNING_LINE_LPN_WITH_DIFFERENT_ID);
        query.setParameter("internalPart", internalPart);
        query.setParameter("id", id);
        List<ProductionPlan> result = query.list();
        if (result.size() > 0) {
            return true;
        }
        return false;
    }

    private void refreshPlanningTable() {
        planning_jtable.setModel(new DefaultTableModel(new Vector(), planning_table_header));
        planning_jtable.setModel(new DefaultTableModel(getPlanningLines(), planning_table_header));
    }

    private void createNewPlanningLine(CSVRecord record) {
        ProductionPlan pp = new ProductionPlan(record.get("harness_part"), record.get("internal_part"), Integer.valueOf(record.get("planned_qty")));
        pp.create(pp);
    }

    private void clearFields() {
        txt_harness_part.setText("");
        txt_internal_part.setText("");
        txt_qty_planned.setText("");
        txt_id.setText("#");
        btn_delete.setEnabled(false);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_csv_example;
    private javax.swing.JButton btn_delete;
    private javax.swing.JButton btn_delete_planning;
    private javax.swing.JButton btn_export_excel;
    private javax.swing.JButton btn_import_csv;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JButton btn_save;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel msg_lbl;
    private javax.swing.JTable planning_jtable;
    private javax.swing.JTextField txt_harness_part;
    private javax.swing.JTextField txt_harness_part_filter;
    private javax.swing.JTextField txt_id;
    private javax.swing.JTextField txt_internal_part;
    private javax.swing.JTextField txt_internal_part_filter;
    private javax.swing.JTextField txt_qty_planned;
    // End of variables declaration//GEN-END:variables

}
