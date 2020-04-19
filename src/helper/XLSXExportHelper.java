package helper;

import gui.cra.CRA_UI0002_WIRE_MASTER_DATA;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui.UILog;

/**
 *
 * @author Oussama
 */


public class XLSXExportHelper {
    
    JDialog dialog;
    /**
     * 
     * @param defaultName
     * @param objectsList 
     */
    public void exportToXSSFWorkbook(Component parent, String defaultName, List<?> objectsList){
        try {
            
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
            fileChooser.setSelectedFile(new File(defaultName));
            UIHelper.centerJFileChooser(fileChooser);
            int j = fileChooser.showSaveDialog(parent);
            if (j != 0) {
                return;
            }
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            XSSFWorkbook workbook = new XSSFWorkbook();
            
            //inProcessDialog(true);
            
            XSSFSheet sheet = workbook.createSheet(defaultName);

            int rowCount = 0;
            int columnCount = 0;
            
            //Fill the head of the table
            Row row = sheet.createRow(rowCount++);
            for (Field title : objectsList.get(0).getClass().getDeclaredFields()){
                Cell cell = row.createCell(columnCount++);
                cell.setCellValue((String) title.getName().toUpperCase());
            }
            //Fill the body of the table
            for (Object c : objectsList) {
                columnCount = 0;
                row = sheet.createRow(rowCount++);

                for (Field field : c.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(c);
                    Cell cell = row.createCell(columnCount++);
                    if (value instanceof String) {
                        cell.setCellValue((String) value);
                    } else if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof Double) {
                        cell.setCellValue((Double) value);
                    } else if (value instanceof Date) {
                        cell.setCellValue((String) new SimpleDateFormat("YYYY/MM/dd HH:mm:ss").format(value));
                    }
                }

            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath + ".xlsx")) {
                workbook.write(outputStream);
                //inProcessDialog(false);
                UILog.infoDialog("Export terminé !");
            } catch (FileNotFoundException ex) {
                UILog.errorDialog(ex.getMessage());
            }

        } catch (IOException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CRA_UI0002_WIRE_MASTER_DATA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Show or hide an in process dialog during excel exportation
     * @param visible 
     */
    private void inProcessDialog(boolean visible) {
        if(visible){
            this.dialog = new JDialog(new JFrame(), "Export en cours...", true);
            this.dialog.setSize(100, 30);
            this.dialog.setLocationRelativeTo(null);
            this.dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            JPanel panel = new JPanel();
            JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL);            
            panel.add(progress);
            this.dialog.add(progress);            
            this.dialog.setVisible(true);
        }else{
            this.dialog.dispose();
        }
    }
}
