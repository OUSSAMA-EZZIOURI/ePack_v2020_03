/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import __main__.GlobalVars;
import entity.ConfigFamily;
import entity.ConfigShift;
import entity.ConfigUcs;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import org.hibernate.Session;

/**
 *
 * @author user
 */
public class Helper {

    private static Helper instance = null;

    @SuppressWarnings("empty-statement")
    protected Helper() {
        // Exists only to defeat instantiation.        
    }

    public static Helper getInstance() {
        if (instance == null) {
            instance = new Helper();
        }
        return instance;
    }

    public static Session openSession() {

        try {
            System.out.println("Helper.openSession return an opened session");
            return HibernateUtil.getInstance().getCurrentSession();
        } catch (Exception e) {
            System.out.println("Helper.openSession starts a new sessions");
            return HibernateUtil.getInstance().openSession();
        }
    }

    //State Design pattern
    /**
     *
     */
    public static Session sess = openSession();

    /**
     *
     */
    //public static Main Module_Choice_Gui = null;
    private static MessageDigest digester;

    /**
     * @deprecated
     */
    public static String HOSTNAME;

    /**
     * Actual hostname
     */
    private static String TMP_MSG = "";

    //-------------------------- Properties       -----------------------------
    /**
     *
     */
    //public final static Properties PROP = new Properties();
    //-------------------------- LOG and Messages -----------------------------
    /**
     *
     */
    public static Logger log = null;

    /**
     *
     */
    public static FileHandler logFileHandler;

    
    public static String ERR0027_NO_WORKPLACE_FOUND = "No Workplace found in database. Please check Config_Workplace table.";
    /**
     *
     */
    public static String ERR0026_NO_SEGMENT_FOUND = "No Segment found in database. Please check Config_Segment table.";
    /**
     *
     */
    public static String ERR0024_PALLET_ALREADY_OPEN = "Palette déjà ouverte pour la référence %s !";

    /**
     *
     */
    public static String ERR0023_PALLET_NOT_FOUND = "Aucune palette trouvée avec ce numéro %s !";

    /**
     *
     */
    public static String ERR0022_PACK_TYPE_NOT_FOUND_IN_UCS = "No pack type found in UCS for project [%s], harness part [%s], supplier part [%s], index [%s] and pack size [%s].";

    /**
     *
     */
    public static String ERR0021_PACK_SIZE_NOT_FOUND_IN_UCS = "No pack size found in UCS for project [%s], harness part [%s], supplier part [%s], index [%s] and pack type [%s].";

    /**
     *
     */
    public static String ERR0020_INDEX_NOT_FOUND_IN_UCS = "No index found in UCS for project [%s], harness part [%s] and supplier part [%s].";

    /**
     *
     */
    public static String ERR0019_SUPPLIER_NOT_FOUND_IN_UCS_HARNESS_TYPE = "No supplier part number found in UCS for project [%s] and harness part [%s].";

    /**
     *
     */
    public static String ERR0018_HP_NOT_FOUND_IN_UCS_HARNESS_TYPE = "No harness part found in UCS for project [%s].";

    /**
     *
     */
    public static String ERR0017_NO_SHIFT_FOUND = "No Shift found in database. Please check Config_Shift table.";

    /**
     *
     */
    public static String ERR0016_HP_NOT_FOUND_IN_UCS_HARNESS_TYPE = "Harness part [%s] NOT found in UCS [%s].";

    /**
     *
     */
    public static String ERR0015_ENGINE_LABEL_FORMAT = "Invalid Engine Label scanned format [%s]";

    /**
     *
     */
    public static String ERR0014_NO_PROJECT_FOUND = "No Project found in database. Please check Config_Project table.";

    /**
     *
     */
    public static String ERR0013_UNKNOWN_PRINT_MODE = "Unknown PRINT_MODE %s property. Please use DesktopPrinter or JobPrinter values.";

    /**
     *
     */
    public static String ERR0012_APPLICATION_ENCOUNTRED_PROBLEM = "Application encountered some problem !";

    /**
     *
     */
    public static String ERR0012_APPLICATION_ALREADY_RUNNING = "Application already running !";

    /**
     *
     */
    public static String ERR0011_INVALID_CLOSE_PALLET_BARCODE = "Invalid CLOSE PALLET barcode [%s]!";

    /**
     *
     */
    public static String ERR0010_CONTAINER_NOT_FOUND = "Container [%s] not found.";

    /**
     *
     */
    public static String ERR0009_COUNTER_NOT_MATCH_HP = "Counter [%s] doesn't match harness part [%s]";

    /**
     *
     */
    public static String ERR0008_NO_PALLET_SELECTED = "Aucune palette n'est sélectionnée\nMerci de sélectionner d'abord une palette.";
    /**
     *
     */
    public static String ERR0008_INCORRECT_PALLET_NUMBER = "Incorrect pallet number [%s]";

    /**
     *
     */
    public static String ERR0007_PRINTING_FAILED = "Failed to print [%s] file!";

    /**
     *
     */
    public static String ERR0006_INVALID_OPEN_PALLET_BARCODE = "Invalid OPEN PALLET barcode [%s]! \n. Verify the QR code/Barcode scanned.";

    /**
     *
     */
    public static String ERR0005_HP_COUNTER_FORMAT = "Invalid Counter scanned format [%s]!";

    /**
     *
     */
    public static String ERR0004_HP_NOT_FOUND = "Harness part [%s] NOT found.";

    /**
     *
     */
    public static String ERR0003_HP_FORMAT = "Invalid Harness Part scanned [%s] !";

    /**
     *
     */
    //public static String ERR0002_LOGIN_PASS_FAILED = "Login or password failed !";

    /**
     *
     */
    public static String ERR0001_LOGIN_FAILED = "Invalid login/password combination or account locked !";

    /**
     *
     */
    public static String ERR0000_DB_CONNECT_FAILED = "com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure";

    
    /**
     *
     */
    public static String INFO0012_LOGOUT_SUCCESS = "User %s disconnected from host %s at %s.";

    public static String BOOK_WAREHOUSE_IN_PACK_FG = "BOOK PACKAGING RECIEVED FINISH GOODS";

    public static String BOOK_WAREHOUSE_OUT_PACK_FG = "BOOK PACKAGING SHIPPED FINISH GOODS";

    //-------------------------- NEW PALLET MASK
    /**
     *
     */
    public static String PALLET_NUMBER_PATTERN = "^\\d{9}";

    /**
     *
     */
    public static String CLOSING_PALLET_PATTERN = "^[C]{1}[P]{1}\\d{9}";

    public static void startSession() {
        Helper.sess.flush();
        try {
            //Helper.openSession();
            Helper.sess.beginTransaction();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, Helper.ERR0000_DB_CONNECT_FAILED, "Database error !", ERROR_MESSAGE);
            System.err.println("Initial SessionFactory creation failed." + e.getMessage());
        }
    }

    //----------------------------- MD5 BLOC ---------------------------------
    static {
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            HOSTNAME = InetAddress.getLocalHost().getHostName().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param str
     * @return
     */
    public static String crypt(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        }

        digester.update(str.getBytes());
        byte[] hash = digester.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & hash[i]));
            }
        }
        return hexString.toString();
    }
    //-------------------------- END MD5 BLOC ---------------------------------


    public static void loadProjectsInJbox(JComboBox jbox) {
        List result = new ConfigFamily().select();
        if (result.isEmpty()) {
            //JOptionPane.showMessageDialog(null, Helper.ERR0014_NO_PROJECT_FOUND, "Projects Configuration error !", ERROR_MESSAGE);
            System.err.println(Helper.ERR0014_NO_PROJECT_FOUND);
        } else { //Map project data in the list
            for (Object o : result) {
                ConfigFamily cp = (ConfigFamily) o;
                //jbox.addItem(new ComboItem(cp.getFamily(), cp.getFamily()));
                jbox.addItem(cp.getFamily());
            }
        }
    }

    public static void loadShiftsInJbox(JComboBox jbox) {
        List result = new ConfigShift().select();
        if (result.isEmpty()) {
            //JOptionPane.showMessageDialog(null, Helper.ERR0017_NO_SHIFT_FOUND, "Shift Configuration error !", ERROR_MESSAGE);
            System.err.println(Helper.ERR0017_NO_SHIFT_FOUND);
        } else { //Map project data in the list
            for (Object o : result) {
                ConfigShift cs = (ConfigShift) o;
                //jbox.addItem(new ComboItem(cs.getDescription(), cs.getName()));
                jbox.addItem(cs.getDescription());
            }
        }
    }

    public static void loadHarnessInJbox(JComboBox jbox, String harnessType) {
        List result = new ConfigUcs().getHarnessList(harnessType);

        if (result.isEmpty()) {
            //JOptionPane.showMessageDialog(null, String.format(Helper.ERR0018_HP_NOT_FOUND_IN_UCS_HARNESS_TYPE, harnessType), "UCS Configuration error !", ERROR_MESSAGE);
            System.err.println(String.format(Helper.ERR0018_HP_NOT_FOUND_IN_UCS_HARNESS_TYPE, harnessType));
        } else { //Map project data in the list
            for (Object o : result) {
                ConfigUcs cu = (ConfigUcs) o;
                //jbox.addItem(new ComboItem(cu.getHarnessPart(),String.valueOf(cu.getHarnessPart())));
                jbox.addItem(String.valueOf(cu.getHarnessPart()));
            }
        }
    }

    public static void loadSupplierPartInJbox(JComboBox jbox, String harnessType, String harnessPart) {
        List result = new ConfigUcs().getSupplierPartList(harnessType, harnessPart);
        if (result.isEmpty()) {
            //JOptionPane.showMessageDialog(null, String.format(Helper.ERR0019_SUPPLIER_NOT_FOUND_IN_UCS_HARNESS_TYPE, harnessType, harnessPart), "UCS Configuration error !", ERROR_MESSAGE);
            System.err.println(String.format(Helper.ERR0019_SUPPLIER_NOT_FOUND_IN_UCS_HARNESS_TYPE, harnessType, harnessPart));
        } else { //Map project data in the list
            for (Object o : result) {
                ConfigUcs cu = (ConfigUcs) o;
                //jbox.addItem(new ComboItem(cu.getSupplierPartNumber(),String.valueOf(cu.getSupplierPartNumber())));
                jbox.addItem(String.valueOf(cu.getSupplierPartNumber()));
            }
        }
    }

    public static void loadIndexInJbox(JComboBox jbox, String harnessType, String harnessPart, String supplierPartNumber) {
        List result = new ConfigUcs().getIndexList(harnessType, harnessPart, supplierPartNumber);
        if (result.isEmpty()) {
            //JOptionPane.showMessageDialog(null, String.format(Helper.ERR0020_INDEX_NOT_FOUND_IN_UCS, harnessType, harnessPart, supplierPart), "UCS Configuration error !", ERROR_MESSAGE);
            System.err.println(String.format(Helper.ERR0020_INDEX_NOT_FOUND_IN_UCS, harnessType, harnessPart, supplierPartNumber));
        } else { //Map project data in the list
            for (Object o : result) {
                ConfigUcs cu = (ConfigUcs) o;
                //jbox.addItem(new ComboItem(cu.getHarnessIndex(),cu.getHarnessIndex()));
                jbox.addItem(cu.getHarnessIndex());
            }
        }
    }

    public static void loadPackSizeInJbox(JComboBox jbox,
            String harnessType,
            String harnessPart,
            String supplierPartNumber,
            String harnessIndex,
            String packType) {
        List result = new ConfigUcs().getPackSizeList(harnessType, harnessPart, supplierPartNumber, harnessIndex, packType);
        if (result.isEmpty()) {
            System.err.println(String.format(Helper.ERR0021_PACK_SIZE_NOT_FOUND_IN_UCS, harnessType, harnessPart, supplierPartNumber, harnessIndex, packType));
        } else { //Map project data in the list
            for (Object o : result) {
                ConfigUcs cu = (ConfigUcs) o;
                jbox.addItem(String.valueOf(cu.getPackSize()));
            }
        }
    }

    public static void loadPackTypeInJbox(JComboBox jbox,
            String harnessType,
            String harnessPart,
            String supplierPartNumber,
            String harnessIndex) {
        List result = new ConfigUcs().getPackTypeList(harnessType, harnessPart, supplierPartNumber, harnessIndex);
        if (result.isEmpty()) {
            //JOptionPane.showMessageDialog(null, String.format(Helper.ERR0022_PACK_TYPE_NOT_FOUND_IN_UCS, harnessType, harnessPart, supplierPart, harnessIndex, packType), "UCS Configuration error !", ERROR_MESSAGE);
            System.err.println(String.format(Helper.ERR0022_PACK_TYPE_NOT_FOUND_IN_UCS, harnessType, harnessPart, supplierPartNumber, harnessIndex));
        } else { //Map project data in the list
            for (Object o : result) {
                ConfigUcs cu = (ConfigUcs) o;
                //jbox.addItem(new ComboItem(String.valueOf(cu.getPackType()),String.valueOf(cu.getPackType())));
                jbox.addItem(String.valueOf(cu.getPackType()));
            }
        }
    }

    public static void loadContainerStateInJbox(JComboBox jbox) {
        jbox.addItem(GlobalVars.PALLET_STORED);
        jbox.addItem(GlobalVars.PALLET_OPEN);
        jbox.addItem(GlobalVars.PALLET_WAITING);
        jbox.addItem(GlobalVars.PALLET_DISPATCHED);
        jbox.addItem(GlobalVars.PALLET_CLOSED);
    }

    public static void loadCustomersInJbox(JComboBox jbox) {
        List<String[]> result = new ConfigFamily().selectHarnessType();
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(null, Helper.ERR0014_NO_PROJECT_FOUND, "Projects Configuration error !", ERROR_MESSAGE);
            System.err.println(Helper.ERR0014_NO_PROJECT_FOUND);
        } else {
            System.out.println(result.toString());
            for (int i = 0; i < result.size(); i++) {
                jbox.addItem(result.get(i)[0]);
            }
        }
    }

}
