package gui.cra;

import __main__.GlobalMethods;
import entity.ProductionPlan;
import gui.warehouse_dispatch.WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN_JPANEL;
import helper.HQLHelper;
import helper.Helper;
import helper.JDialogExcelFileChooser;
import helper.UIHelper;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Query;
import ui.UILog;

/**
 *
 * @author Oussama
 */
public class CRA_UI0001_PRODUCTION_PLAN extends javax.swing.JPanel {

    private JTabbedPane parent;
    Vector<String> planning_table_header = new Vector<String>();

    List<String> header_columns = Arrays.asList(
            "Customer PN",
            "Internal PN",
            "Quantité",
            "Dernière modif.",
            "Modifié par."
    );
    Vector planning_table_data = new Vector();

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
        GlobalMethods.refreshJTable(planning_jtable, planning_table_data, header_columns);
        UIHelper.disableEditingJtable(planning_jtable);
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
        btn_refresh = new javax.swing.JButton();
        btn_csv_example = new javax.swing.JButton();
        btn_import_csv = new javax.swing.JButton();
        btn_delete_planning = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();

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

        btn_refresh.setText("Actualiser");
        btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refreshActionPerformed(evt);
            }
        });

        btn_csv_example.setText("Exemple fichier .csv ...");
        btn_csv_example.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_csv_exampleActionPerformed(evt);
            }
        });

        btn_import_csv.setText("Charger le planning .csv ...");
        btn_import_csv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_import_csvActionPerformed(evt);
            }
        });

        btn_delete_planning.setForeground(javax.swing.UIManager.getDefaults().getColor("nb.output.warning.foreground"));
        btn_delete_planning.setText("Effacer la table");
        btn_delete_planning.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_delete_planningActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Planning d'assemblage");

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_delete_planning, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btn_csv_example)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn_import_csv)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_refresh))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(120, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(180, 180, 180))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_csv_example)
                    .addComponent(btn_import_csv)
                    .addComponent(btn_refresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_delete_planning, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            JOptionPane.showMessageDialog(null, "Table supprimée !\n");
            planning_table_data = new Vector();
            refreshPlanningTable();
            return true;
        }

        return false;
    }

    private void btn_import_csvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_import_csvActionPerformed

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
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
        //GlobalMethods.refreshJTable(planning_jtable, new Vector(), header_columns);
        refreshPlanningTable();
    }//GEN-LAST:event_btn_delete_planningActionPerformed

    private void btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refreshActionPerformed
        refreshPlanningTable();
    }//GEN-LAST:event_btn_refreshActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        final Timer t = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                if (jProgressBar1.getValue() == 100) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        t.start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private Vector getPlanningLines() {
        Helper.startSession();

        //SQLQuery query = Helper.sess.createSQLQuery(HQLHelper.GET_ALL_PRODUCTION_PLAN_LINES);
        Query query = Helper.sess.createQuery(HQLHelper.GET_ALL_PRODUCTION_PLAN_LINES);
//        query
//                .addScalar("harness_part", StandardBasicTypes.STRING)
//                .addScalar("internal_part", StandardBasicTypes.STRING)
//                .addScalar("planned_qty", StandardBasicTypes.DOUBLE)
//                .addScalar("create_user", StandardBasicTypes.STRING);
        List<Object[]> result = query.list();
        Helper.sess.getTransaction().commit();

        //Populate the jTable with lines
        for (Object[] obj : result) {
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add((String) obj[0]);
            oneRow.add((String) obj[1]);
            oneRow.add(String.format("%1$,.2f", obj[2]));
            oneRow.add((Timestamp) obj[3]);
            oneRow.add((String) obj[4]);

            planning_table_data.add(oneRow);
        }
        return planning_table_data;
    }

    private void refreshPlanningTable() {
        GlobalMethods.refreshJTable(planning_jtable, getPlanningLines(), header_columns);
    }

    private void createNewPlanningLine(CSVRecord record) {
        ProductionPlan pp = new ProductionPlan(record.get("harness_part"), record.get("internal_part"), Double.valueOf(record.get("planned_qty")));
        pp.create(pp);
    }
    
        
        

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_csv_example;
    private javax.swing.JButton btn_delete_planning;
    private javax.swing.JButton btn_import_csv;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable planning_jtable;
    // End of variables declaration//GEN-END:variables

}