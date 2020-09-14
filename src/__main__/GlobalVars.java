/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package __main__;

import entity.ConfigCompany;
import entity.ManufactureUsers;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.swing.JComboBox;

/**
 *
 * @author Oussama EZZIOURI
 */
public class GlobalVars {

    /**
     *
     */
    public static String APP_NAME = "ePack System";

    /**
     *
     */
    public static String APP_VERSION = "v2.3";

    /**
     *
     */
    public static String APP_AUTHOR = "Réalisé par EZZIOURI Oussama";

    /**
     *
     */
    public static String ALL_RIGHTS_RESERVED = "Tous droits réservés © 2020";
    /**
     *
     */
    public static String APP_HOSTNAME;

    /**
     * GlobalVars propeties of the application
     */
    public final static Properties APP_PROP = new Properties();

    public static ConfigCompany COMPANY_INFO = new ConfigCompany();

    public static ManufactureUsers CONNECTED_USER = new ManufactureUsers();

    //........................ COLORS .........................
    /**
     * Default light green for required form field
     */
    public static Color BG_DEFAULT_GREEN = new Color(153, 255, 153);

    /**
     * Default light yellow for required form field in case of error
     */
    public static Color BG_DEFAULT_YELLOW = new Color(255, 255, 0);

    /**
     * Default Blue for Jpanel
     */
    public static Color BG_DEFAULT_BLUE = new Color(36, 65, 86);
    //........................ PREFIXS .........................
    /**
     *
     */
    public static String HARN_PART_PREFIX = GlobalVars.APP_PROP.getProperty("HARN_PART_PREFIX");

    /**
     *
     */
    public static String DISPATCH_SERIAL_NO_PREFIX = GlobalVars.APP_PROP.getProperty("DISPATCH_SERIAL_NO_PREFIX");

    /**
     *
     */
    public static String DISPATCH_PN_PREFIX = GlobalVars.APP_PROP.getProperty("DISPATCH_PN_PREFIX");

    /**
     *
     */
    public static String DISPATCH_QUANTITY_PREFIX = GlobalVars.APP_PROP.getProperty("DISPATCH_QUANTITY_PREFIX");

    /**
     *
     */
    public static String SUPPLIER_PART_PREFIX = GlobalVars.APP_PROP.getProperty("SUPPLIER_PART_PREFIX");

    /**
     *
     */
    public static String HARN_COUNTER_PREFIX = GlobalVars.APP_PROP.getProperty("HARN_COUNTER_PREFIX");

    /**
     *
     */
    public static String QUANTITY_PREFIX = GlobalVars.APP_PROP.getProperty("QUANTITY_PREFIX");

    /**
     *
     */
    public static String CLOSING_PALLET_PREFIX = GlobalVars.APP_PROP.getProperty("CLOSING_PALLET_PREFIX");

    /**
     *
     */
    public static String WAREHOUSE_PREFIX = GlobalVars.APP_PROP.getProperty("WAREHOUSE_PREFIX");

    /**
     *
     */
    public static String OPEN_PALLET_KEYWORD = GlobalVars.APP_PROP.getProperty("OPEN_PALLET_KEYWORD");
    public static String WEIGHT_PREFIX = GlobalVars.APP_PROP.getProperty("WEIGHT_PREFIX");
    public static String FIFO_DATE_PREFIX = GlobalVars.APP_PROP.getProperty("FIFO_DATE_PREFIX");

    //Prevent from openning many windows, maximum 1 window allowed
    public static int OPENED_SCAN_WINDOW = 0;

    /**
     * To do : This method must be migrated to a database table in order to
     * facilitate the update. and some fields must be allocated by project
     * QUANTITY_PREFIX HARN_PART_PREFIX DISPATCH_QUANTITY_PREFIX
     * DISPATCH_PN_PREFIX DISPATCH_SERIAL_NO_PREFIX WAREHOUSE_PREFIX
     */
    public static void mapProperties() {

        COMPANY_INFO = new ConfigCompany().getCompany(Integer.valueOf(GlobalVars.APP_PROP.getProperty("COMPANY_ID")));

        OPEN_PALLET_KEYWORD = GlobalVars.APP_PROP.getProperty("OPEN_PALLET_KEYWORD");

        HARN_PART_PREFIX = GlobalVars.APP_PROP.getProperty("HARN_PART_PREFIX");

        SUPPLIER_PART_PREFIX = GlobalVars.APP_PROP.getProperty("SUPPLIER_PART_PREFIX");

        HARN_COUNTER_PREFIX = GlobalVars.APP_PROP.getProperty("HARN_COUNTER_PREFIX");

        QUANTITY_PREFIX = GlobalVars.APP_PROP.getProperty("QUANTITY_PREFIX");

        CLOSING_PALLET_PREFIX = GlobalVars.APP_PROP.getProperty("CLOSING_PALLET_PREFIX");

        DISPATCH_SERIAL_NO_PREFIX = GlobalVars.APP_PROP.getProperty("DISPATCH_SERIAL_NO_PREFIX");

        DISPATCH_PN_PREFIX = GlobalVars.APP_PROP.getProperty("DISPATCH_PN_PREFIX");

        DISPATCH_QUANTITY_PREFIX = GlobalVars.APP_PROP.getProperty("DISPATCH_QUANTITY_PREFIX");

        WEIGHT_PREFIX = GlobalVars.APP_PROP.getProperty("WEIGHT_PREFIX");

        FIFO_DATE_PREFIX = GlobalVars.APP_PROP.getProperty("FIFO_DATE_PREFIX");

        WAREHOUSE_PREFIX = GlobalVars.APP_PROP.getProperty("WAREHOUSE_PREFIX");

    }

    /**
     *
     */
    //public static String UCS_SPLITER = "|";
    //##########################################################################
    //##########################################################################
    //##########################################################################
    //.............................. PATTERNS LIST .............................
    //Example ; P2220512705.02.2016;11:44:00
    /**
     *
     */
    public static String[] DATAMATRIX_PATTERN_LIST;

    /**
     *
     */
    public static String[] PARTNUMBER_PATTERN_LIST;

    /**
     *
     */
    public static String[][] PLASTICBAG_BARCODE_PATTERN_LIST;

    //##########################################################################
    //##########################################################################
    //##########################################################################    
    //......................PALLET PRINTING STATES .............................
    /**
     *
     */
    public static final String PALLET_PRINT_NEW = "NEW";

    /**
     *
     */
    public static final String PALLET_PRINT_INPROCESS = "IN_PROCESS";

    /**
     *
     */
    public static final String PALLET_PRINT_PRINTED = "PRINTED";

    /**
     *
     */
    public static final String PALLET_PRINT_ERROR = "ERROR";

    /**
     *
     */
    public static final String PALLET_PRINT_REPRINT = "REPRINT";

    //##########################################################################
    //##########################################################################
    //##########################################################################    
    //........................... PALLET STATES ................................
    /**
     *
     */
    public static final String PALLET_OPEN = "OPEN";

    /**
     *
     */
    public static final String PALLET_OPEN_CODE = "1000";

    /**
     *
     */
    public static final String PALLET_WAITING = "WAITING";

    /**
     *
     */
    public static final String PALLET_WAITING_CODE = "1500";

    /**
     *
     */
    public static final String PALLET_BLOCKED = "BLOCKED";

    /**
     *
     */
    public static final String PALLET_BLOCKED_CODE = "9999";

    /**
     *
     */
    public static final String PALLET_CLOSED = "CLOSED";

    /**
     *
     */
    public static final String PALLET_CLOSED_CODE = "1900";

    /**
     *
     */
    public static final String PALLET_STORED = "STORED";

    /**
     *
     */
    public static final String PALLET_STORED_CODE = "2000";

    /**
     *
     */
    public static final String PALLET_RESERVED = "RESERVED";

    /**
     *
     */
    public static final String PALLET_RESERVED_CODE = "2500";
    /**
     *
     */
    public static final String PALLET_DISPATCHED = "DISPATCHED";

    /**
     *
     */
    public static final String PALLET_DISPATCHED_CODE = "3000";

    /**
     *
     */
    public static final String PALLET_DELETED = "DELETED";

    /**
     *
     */
    public static final String PALLET_DELETED_CODE = "-1000";

    /**
     *
     */
    public static final String[][] PALLET_STATES = {
        {"ALL", "selected"},
        {PALLET_OPEN, ""},
        {PALLET_WAITING, ""},
        {PALLET_CLOSED, ""},
        {PALLET_STORED, ""},
        {PALLET_RESERVED, ""},
        {PALLET_DISPATCHED, ""},
        {PALLET_BLOCKED, ""},
        {PALLET_DELETED, ""}
    };

    
    
    

    /**
     *
     * @return the state's code
     */
    public static String getStateCode(String state) {
        switch (state) {
            case PALLET_CLOSED:
                return PALLET_CLOSED_CODE;
            case PALLET_STORED:
                return PALLET_STORED_CODE;

        }
        return null;
    }
    //##########################################################################
    //##########################################################################
    //##########################################################################    
    //........................... CONFIG STATES ................................
    /**
     *
     */
    public static List<String> CONFIG_MENUS = Arrays.asList(
            "---",
            "Unités de conditionnement standard (UCS)",
            "Masque code à barre",
            "Utilisateurs",
            //"Planner",
            "Configuration packaging"
    );

    //##########################################################################
    //##########################################################################
    //##########################################################################    
    //........................... PROFIL LEVELS ................................
    /**
     *
     */
    public enum ACCESS_LEVEL {

        PROFIL_OPERATOR(1000), PROFIL_WAREHOUSE_AGENT(2000), PROFIL_ADMIN(9000), PROFIL_READER(0000);

        private ACCESS_LEVEL(int level) {
            this.level = level;
        }

        private Integer level;

        public Integer getLevel() {
            return this.level;
        }

        @Override
        public String toString() {
            return this.level + "";
        }
    }
    /**
     *
     */
    public static final Integer PROFIL_READER = 0000;
    public static final Integer PROFIL_OPERATOR = 1000;
    public static final Integer PROFIL_WAREHOUSE_AGENT = 2000;
    public static final Integer PROFIL_ADMIN = 9000;
    public static final Integer[] PROFILS = new Integer[]{PROFIL_READER, PROFIL_OPERATOR, PROFIL_WAREHOUSE_AGENT, PROFIL_ADMIN};

    /**
     * true: Only one pack type can be opened in the actual workstation (KLTV,
     * HV, RV)
     *
     */
    public static final boolean UNIQUE_PALLET_PER_PACK_TYPE = false;

    //PALLET STATE COLLUMN INDEX IN UI0000_MAIN CONTAINER TABLE
    /**
     *
     */
    public static int PALLET_STATE_COL_INDEX = 8;

    /**
     *
     *
     */
    public static String SCHEDULE_STATE_NEW = "NEW";

    /**
     *
     *
     */
    public static String SCHEDULE_STATE_NEW_CODE = "1000";

    //HARNESS PART COUNTER LENGTH
    /**
     *
     */
    public static Integer HARN_PART_LEN = 9;
    
    public enum WarehouseType {

        FINISHED_GOODS,
        PACKAGING,
        INVENTORY,
        SCRAP,
        TRANSIT,
        WIRES,
        RAW_MATERIAL;

    }
}
