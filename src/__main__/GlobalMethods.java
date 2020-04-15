/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package __main__;

import entity.ConfigBarcode;
import entity.ConfigWarehouse;
import gui.packaging.PackagingVars;
import helper.CloseTabButtonComponent;
import helper.HQLHelper;
import helper.Helper;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import org.hibernate.Query;
import ui.UILog;
import ui.config.ConfigMsg;
import ui.error.ErrorMsg;

/**
 *
 * @author Oussama EZZIOURI
 */
public class GlobalMethods {

    /**
     * Disconnect the current session and display the main frame
     */
    public static void logout() {
        try {
            PackagingVars.Packaging_Gui_Mode3.dispose();
            GlobalVars.OPENED_SCAN_WINDOW = 0;
        } catch (Exception e) {
            //do nothing
        }
        try {
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    AuthFrame ui = new AuthFrame();

                    ui.setVisible(true);
                    UILog.createDailyLogFile(GlobalVars.APP_PROP.getProperty("LOG_PATH"));
                    PropertiesLoader.createDailyOutPrintDir(GlobalVars.APP_PROP.getProperty("PRINT_DIR"),
                            GlobalVars.APP_PROP.getProperty("PRINT_PALLET_DIR"),
                            GlobalVars.APP_PROP.getProperty("PRINT_CLOSING_PALLET_DIR"),
                            GlobalVars.APP_PROP.getProperty("PRINT_PICKING_SHEET_DIR"),
                            GlobalVars.APP_PROP.getProperty("PRINT_DISPATCH_SHEET_DIR"));
                }
            });
            //currentFrame.dispose();

        } catch (Exception e) {
            UILog.exceptionDialog(null, e);
        }
    }

    /**
     * @param dataVector
     * @param jtable
     * @param headerList
     * @param dataResultList
     */
    public static void reset_jtable_data(List<Object[]> dataResultList, Vector dataVector, List<String> headerList, JTable jtable) {

        Vector<String> headerVector = new Vector<String>();

        //reset Table content
        jtable.setModel(new DefaultTableModel(new Vector(), new Vector()));

        for (Iterator<String> it = headerList.iterator(); it.hasNext();) {
            headerVector.add(it.next());
        }
        for (Object[] line : dataResultList) {
            @SuppressWarnings("UseOfObsoleteCollectionType")
            Vector<Object> oneRow = new Vector<Object>();
            for (Object cell : line) {
                oneRow.add(String.valueOf(cell));
            }
            dataVector.add(oneRow);
        }

        for (int c = 0; c < jtable.getColumnCount(); c++) {
            Class<?> col_class = jtable.getColumnClass(c);
            jtable.setDefaultEditor(col_class, null);        // remove editor            
        }

        jtable.setModel(new DefaultTableModel(dataVector, headerVector));
        jtable.setAutoCreateRowSorter(true);
    }

    /**
     *
     * @return
     */
    public static String convertToStandardDate(Date d) {
        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date (month/day/year)
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(d);
    }

    /**
     *
     * @return
     */
    public static String convertDateToStringFormat(Date d, String format) {
        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date (month/day/year)

        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.format(d);
    }

    /**
     *
     * @return
     */
    public static String getStrTimeStamp() {
        // Create an instance of SimpleDateFormat used for formatting 
        // the string representation of date (month/day/year)
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
        // Using DateFormat format method we can create a string 
        // representation of a date with the defined format.
        String reportDate = df.format(today);

        return reportDate;
    }

    /**
     *
     * @param format "yyyy/MM/dd" or "yy.MM.dd"
     * @return
     */
    public static String getStrDateStamp(String format) {
        // Create an instance of SimpleDateFormat used for formatting 
        // the string representation of date (year/month/day)
        DateFormat df = new SimpleDateFormat(format);

        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
        // Using DateFormat format method we can create a string 
        // representation of a date with the defined format.
        String reportDate = df.format(today);

        return reportDate;
    }

    /**
     *
     * @param format : Patter of datetime example : yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getStrTimeStamp(String format) {
        // Create an instance of SimpleDateFormat used for formatting 
        // the string representation of date (month/day/year)
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        DateFormat df = new SimpleDateFormat(format);

        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
        // Using DateFormat format method we can create a string 
        // representation of a date with the defined format.
        String reportDate = df.format(today);

        return reportDate;
    }

    /**
     *
     * @param format
     * @return
     */
    public static Date getTimeStamp(String format) {
        // Create an instance of SimpleDateFormat used for formatting 
        // the string representation of date (month/day/year)
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        DateFormat df = new SimpleDateFormat(format);

        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(reportDate);
        } catch (ParseException ex) {
            Logger.getLogger(GlobalMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }

    /**
     *
     * @param end
     * @param start
     * @return
     */
    public static long DiffInHours(Date end, Date start) {
        int duration = (int) (end.getTime() - start.getTime());
        long diffInHr = TimeUnit.MILLISECONDS.toHours(duration);
        return diffInHr;
    }

    /**
     *
     * @param end
     * @param start
     * @return
     */
    public static long DiffInMinutes(Date end, Date start) {
        int duration = (int) (end.getTime() - start.getTime());
        long diffInMin = TimeUnit.MILLISECONDS.toMinutes(duration);
        return diffInMin;
    }

    /**
     * Add new tab to the parent JTabbedPane
     *
     * @param title The title of the new tab
     * @param parent The parent JTabbedPane
     * @param newTab The new tab object
     * @param evt MouseEvent that trigger this method
     */
    public static void addNewTabToParent(String title, JTabbedPane parent, JPanel newTab, MouseEvent evt) {
        parent.addTab(title, null, newTab,
                title);

        parent.setMnemonicAt(0, KeyEvent.VK_1);

        parent.setTabComponentAt(parent.getTabCount() - 1,
                new CloseTabButtonComponent(parent));
        parent.setSelectedIndex(parent.getTabCount() - 1);
    }

    /**
     * Add new tab to the parent JTabbedPane
     *
     * @param title The title of the new tab
     * @param parent The parent JTabbedPane
     * @param newTab The new tab object
     * @param evt ActionEvent that trigger this method
     */
    public static void addNewTabToParent(String title, JTabbedPane parent, JPanel newTab, AWTEvent evt) {
        parent.addTab(title, null, newTab,
                title);

        parent.setMnemonicAt(0, KeyEvent.VK_1);

        parent.setTabComponentAt(parent.getTabCount() - 1,
                new CloseTabButtonComponent(parent));
        parent.setSelectedIndex(parent.getTabCount() - 1);
    }

    /**
     * Add new tab to the parent JTabbedPane
     *
     * @param title The title of the new tab
     * @param parent The parent JTabbedPane
     * @param newTab The new tab object
     * @param evt ActionEvent that trigger this method
     */
    public static void addNewTabToParent(String title, JTabbedPane parent, JPanel newTab, ActionEvent evt) {
        parent.addTab(title, null, newTab,
                title);

        parent.setMnemonicAt(0, KeyEvent.VK_1);

        parent.setTabComponentAt(parent.getTabCount() - 1,
                new CloseTabButtonComponent(parent));
        parent.setSelectedIndex(parent.getTabCount() - 1);
    }
    
    
    public static void resetOutputStram() {
        PrintStream standardOut = System.out;
        PrintStream standardErr = System.err;
        System.setErr(standardErr);
        System.setOut(standardOut);
    }

    /**
     *
     * @param harnessType : Harness Type to filter on (Volvo, Ducati)
     */
    public static void loadDotMatrixCodePatterns(String harnessType) {
        //System.out.println("Loading DATAMATRIX_PATTERN_LIST pattern list ... ");
        UILog.info(ConfigMsg.APP_CONFIG0002[0]);
        Helper.sess.beginTransaction();
        Query query = Helper.sess.createQuery(HQLHelper.GET_PATTERN_BY_KEYWORD_AND_HARNESSTYPE);
        query.setParameter("keyWord", "DOTMATRIX");
        query.setParameter("harnessType", harnessType);
        //PLASTIC_BAG_BARCODE

        UILog.info(query.getQueryString());
        Helper.sess.beginTransaction();
        Helper.sess.getTransaction().commit();
        List resultList = query.list();
        GlobalVars.DATAMATRIX_PATTERN_LIST = new String[query.list().size()];

        int i = 0;
        String patterns = "";
        for (Iterator it = resultList.iterator(); it.hasNext();) {
            ConfigBarcode object = (ConfigBarcode) it.next();
            GlobalVars.DATAMATRIX_PATTERN_LIST[i] = object.getBarcodePattern();
            patterns += GlobalVars.DATAMATRIX_PATTERN_LIST[i] + "\n";

            i++;
        }
        UILog.info(patterns);

        UILog.info(ConfigMsg.APP_CONFIG0003[0], GlobalVars.DATAMATRIX_PATTERN_LIST.length + "", harnessType);
    }

    /**
     *
     * @param harnessType : Harness Type to filter on (Volvo, Ducati)
     */
    public static void loadPartNumberCodePatterns(String harnessType) {
        System.out.println("Loading PARTNUMBER_PATTERN_LIST pattern list ... ");
        Query query = Helper.sess.createQuery(HQLHelper.GET_PATTERN_BY_KEYWORD_AND_HARNESSTYPE);
        query.setParameter("keyWord", "PARTNUMBER");
        query.setParameter("harnessType", harnessType);
        //PLASTIC_BAG_BARCODE
        UILog.info(query.getQueryString());
        Helper.sess.beginTransaction();
        Helper.sess.getTransaction().commit();
        List resultList = query.list();
        GlobalVars.PARTNUMBER_PATTERN_LIST = new String[query.list().size()];

        int i = 0;
        String patterns = "";
        for (Iterator it = resultList.iterator(); it.hasNext();) {
            ConfigBarcode object = (ConfigBarcode) it.next();
            GlobalVars.PARTNUMBER_PATTERN_LIST[i] = object.getBarcodePattern();
            patterns += GlobalVars.PARTNUMBER_PATTERN_LIST[i] + "\n";

            i++;
        }

        UILog.info(patterns);

        UILog.info(ConfigMsg.APP_CONFIG0004[0], "" + GlobalVars.PARTNUMBER_PATTERN_LIST.length, harnessType);
    }

    /**
     *
     * @param parentUI
     * @param project
     * @param box
     */
    public static JComboBox setWarehouseComboboxByProject(Object parentUI, String project, JComboBox box) {
        if (!project.toUpperCase().equals("ALL")) {
            List result = new ConfigWarehouse().selectByProjectAndType(project, "FINISH_GOODS");
            if (result.isEmpty()) {
                UILog.severeDialog(null, ErrorMsg.APP_ERR0036);
                UILog.severe(ErrorMsg.APP_ERR0036[1]);
            } else { //Map project data in the list
                box.removeAllItems();
                for (Object o : result) {
                    ConfigWarehouse cp = (ConfigWarehouse) o;
                    box.addItem(cp.getWarehouse());
                }

            }
        }
        return box;
    }

    /**
     *
     * @param project
     * @return
     */
    public static String getPackagingWh(String project) {
        ConfigWarehouse cw = new ConfigWarehouse();
        List result = cw.selectByProjectAndType(project, "PACKAGING");

        cw = (ConfigWarehouse) result.get(0);
        return (String) cw.getWarehouse();
    }

    /**
     *
     */
    public static void disableEditingTable(JTable jtable) {
        for (int c = 0; c < jtable.getColumnCount(); c++) {
            Class<?> col_class = jtable.getColumnClass(c);
            jtable.setDefaultEditor(col_class, null);        // remove editor            
        }
    }

    public static boolean checkEmptyFields(List<JTextComponent> form) {
        for (JTextComponent jTextComponent : form) {
            if (jTextComponent.getText().isEmpty()) {
                jTextComponent.requestFocus();
                return false;
            }
        }
        return true;
    }

    public static void clearPaneFieldsValues(JPanel panel) {
        for (Component c : panel.getComponents()) {
            System.out.println("Component " + c.getClass().getCanonicalName() + " " + c.getName());
            if (c instanceof JTextField) {
                ((JTextField) c).setText("");
            } else if (c instanceof JTextArea) {
                ((JTextArea) c).setText("");
            } else if (c instanceof JComboBox) {
                ((JComboBox) c).setSelectedIndex(0);
            }
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void createDefaultDirectories() {
        UILog.createDailyLogFile(GlobalVars.APP_PROP.getProperty("LOG_PATH"));
        PropertiesLoader.createDailyOutPrintDir(GlobalVars.APP_PROP.getProperty("PRINT_DIR"),
                GlobalVars.APP_PROP.getProperty("PRINT_PALLET_DIR"),
                GlobalVars.APP_PROP.getProperty("PRINT_CLOSING_PALLET_DIR"),
                GlobalVars.APP_PROP.getProperty("PRINT_PICKING_SHEET_DIR"),
                GlobalVars.APP_PROP.getProperty("PRINT_DISPATCH_SHEET_DIR"));
    }

}
