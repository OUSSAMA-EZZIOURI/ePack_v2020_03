/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.error;

/**
 *
 * @author Oussama EZZIOURI
 */
public class ErrorMsg {

    /**
     * Failed to connect to database !
     */
    public static String[] APP_ERR0001 = {"APP_ERR0001", "APP_ERR0001 : Failed to connect to the database !"};

    /**
     * Application encountered a problem !
     */
    public static String[] APP_ERR0002 = {"APP_ERR0002", "APP_ERR0002 : Application encountered a problem !"};

    /**
     * Application already running !
     */
    public static String[] APP_ERR0003 = {"APP_ERR0003", "APP_ERR0003 : Application already running !"};
    /**
     * Invalid matricule number or account locked !
     */
    public static String[] APP_ERR0004 = {"APP_ERR0004", "APP_ERR0004 : Invalid matricule number or account locked !"};
    /**
     * Part number [%s] not found.
     */
    public static String[] APP_ERR0005 = {"APP_ERR0005", "APP_ERR0005 : Part number [%s] not found."};

    /**
     * Invalid part number scanned [%s] !.
     */
    public static String[] APP_ERR0006 = {"APP_ERR0006", "APP_ERR0006 : Invalid part number scanned [%s] !."};
    /**
     * Part number [%s] not configured for the project [%s].
     */
    public static String[] APP_ERR0007 = {"APP_ERR0007", "APP_ERR0007 : Part number [%s] not configured for the project [%s]."};
    /**
     * Many users founds with the same login [%s]
     */
    public static String[] APP_ERR0008 = {"APP_ERR0008", "APP_ERR0008 : Many users founds with the same login [%s]."};

    /**
     * APP_ERR0010 : QR code [%s] already scanned in pallet N° [%s] ! Invalid QR
     * code format [%s]!
     */
    public static String[] APP_ERR0009 = {"APP_ERR0009", "APP_ERR0009 : Invalid QR code format [%s]!"};
    /**
     * QR code [%s] already scanned in pallet N° [%s] !
     */
    public static String[] APP_ERR0010 = {"APP_ERR0010", "APP_ERR0010 : QR code [%s] already scanned in pallet N° [%s] !"};
    /**
     * QR code [%s] doesn't match the part number [%s]
     */
    public static String[] APP_ERR0011 = {"APP_ERR0011", "APP_ERR0011 : QR code [%s] doesn't match the part number [%s] !"};
    /**
     * Invalid barecode scanned format [%s]
     */
    public static String[] APP_ERR0012 = {"APP_ERR0012", "APP_ERR0012 : Invalid barecode scanned format [%s] !"};

    /**
     * Barecode %s already scanned !
     */
    public static String[] APP_ERR0013 = {"APP_ERR0013", "APP_ERR0013 : Barecode %s already scanned !"};
    /**
     * Pallet opened in workstation %s and you use another workstation %s. You
     * should complete the scan in the same workstation %s!
     */
    public static String[] APP_ERR0014 = {"APP_ERR0014", "APP_ERR0014 : Pallet opened in workstation %s and you use another workstation %s. You should complete the scan in the same workstation %s!"};

    /**
     * Pallet number / code [%s] not correct to open the pallete!
     */
    public static String[] APP_ERR0015 = {"APP_ERR0015", "APP_ERR0015 : Pallet number / code [%s] not correct to open the pallete! \n-Scan the code %s to open a new palette.\n- Scanne an open pallet barecode to continue."};

    /**
     * Pallet/Box of type %s already open in this workstation, for part number
     * %s.
     */
    public static String[] APP_ERR0016 = {"APP_ERR0016", "APP_ERR0016 : Pallet/Box of type %s already open in this workstation %s, for part number %s. \n\n"
        + "Solution N° 1 : - Finish the packaging process of the pallet number %s before you can open a new one of type %s.\n"
        + "Solution N° 2 : - Open this pack type %s in another workstation."};

    /**
     * Pallet already open for part number %s..
     */
    public static String[] APP_ERR0017 = {"APP_ERR0017", "APP_ERR0017 : Pallet already open for part number %s."};

    /**
     * Invalid pallet number %s.
     */
    public static String[] APP_ERR0018 = {"APP_ERR0018", "APP_ERR0018 : Invalid pallet number %s."};

    /**
     * An error has occured during pallet creation for part number %s.\n
     * Creation returned code %s
     */
    public static String[] APP_ERR0019 = {"APP_ERR0019", "APP_ERR0019 : An error has occured during pallet creation for part number %s.\n Creation returned code %s"};
    /**
     * Invalid closing palette barcode [%s]!
     */
    public static String[] APP_ERR0020 = {"APP_ERR0020", "APP_ERR0020 : Invalid closing palette barcode [%s]!"};
    /**
     * No standard pack configuration found for part number [%s]!.
     */
    public static String[] APP_ERR0021 = {"APP_ERR0021", "APP_ERR0021 : No standard pack configuration found for part number [%s]!"};
    /**
     * Failed to found UCS matching criteria %s AND %s
     */
    public static String[] APP_ERR0022 = {"APP_ERR0022", "Failed to found standard pack config matching this criteria : \n- harnessPart [%s]\n- Internal part number [%s]\n- Index [%s}\n-packType [%s]\n-packSize [%s]   !"};
    /**
     * APP_ERR0023 DISPATCH : Load plan not empty.\nPlease delete all items
     * before deleting the plan.
     */
    public static String[] APP_ERR0023 = {"APP_ERR0023", "APP_ERR0023 DISPATCH : Load plan not empty.\nPlease delete all items before deleting the plan."};
    /**
     * APP_ERR0024 DISPATCH : Destination %s not empty.\nRemove associated items
     * before delete the destination.
     */
    public static String[] APP_ERR0024 = {"APP_ERR0024", "APP_ERR0024 DISPATCH : Destination %s not empty.\nRemove associated items before delete the destination."};
    /**
     * APP_ERR0025 DISPATCH : No destination found for the dispatch module.
     * Create destinations in the table 'load_plan_destination'.
     */
    public static String[] APP_ERR0025 = {"APP_ERR0025", "APP_ERR0025 DISPATCH : No destination found for the dispatch module. Create destinations in the table 'load_plan_destination'."};
    /**
     * APP_ERR0026 DISPATCH : No final destination selected.
     */
    public static String[] APP_ERR0026 = {"APP_ERR0026", "APP_ERR0026 DISPATCH : No final destination selected."};
    /**
     * APP_ERR0027 DISPATCH : No dispatch date selected.
     */
    public static String[] APP_ERR0027 = {"APP_ERR0027", "APP_ERR0027 DISPATCH : No dispatch date selected."};
    /**
     * APP_ERR0028 DISPATCH : Invalid date format.
     */
    public static String[] APP_ERR0028 = {"APP_ERR0028", "APP_ERR0028 DISPATCH : Invalid date format."};
    /**
     * APP_ERR0029 DISPATCH : No pallets/positions found for this destination
     * %s.
     */
    public static String[] APP_ERR0029 = {"APP_ERR0029", "APP_ERR0029 DISPATCH : No pallets/positions found for this destination %s."};
    /**
     * APP_ERR0030 DISPATCH : No items associated with this load plan %s.
     */
    public static String[] APP_ERR0030 = {"APP_ERR0030", "APP_ERR0030 DISPATCH : No items associated with this load plan %s."};

    /**
     * APP_ERR0031 DISPATCH : No loading plan selected.
     */
    public static String[] APP_ERR0031 = {"APP_ERR0031", "APP_ERR0031 DISPATCH : No loading plan selected."};
    /**
     * APP_ERR0032 DISPATCH : No loading plan selected.
     */
    public static String[] APP_ERR0032 = {"APP_ERR0032", "APP_ERR0032 DISPATCH : Truck number is missing."};
    /**
     * APP_ERR0033 CONFIG : No Segment found in config_segment table.
     */
    public static String[] APP_ERR0033 = {"APP_ERR0033", "APP_ERR0033 CONFIG : No Segment found in config_segment table."};
    /**
     * APP_ERR0034 CONFIG : No Workplace found in config_workplace table.
     */
    public static String[] APP_ERR0034 = {"APP_ERR0034", "APP_ERR0034 CONFIG : No Workplace found in config_workplace table."};
    /**
     * APP_ERR0035 CONFIG : No Project found in config_project table.
     */
    public static String[] APP_ERR0035 = {"APP_ERR0035", "APP_ERR0035 CONFIG : No Project found in config_project table."};
    /**
     * APP_ERR0036 CONFIG : No Warehouse found for the selected project
     */
    public static String[] APP_ERR0036 = {"APP_ERR0036", "APP_ERR0036 CONFIG : No Warehouse found for the selected project."};
    /**
     * APP_ERR0037 CONFIG : No Segment found for the selected project
     */
    public static String[] APP_ERR0037 = {"APP_ERR0037", "APP_ERR0037 CONFIG : No Segment found for the selected project."};
    /**
     * APP_ERR0038 CONFIG : No Workplace associated with selected Segment.
     */
    public static String[] APP_ERR0038 = {"APP_ERR0038", "APP_ERR0038 CONFIG : No Workplace associated with selected Segment."};    
    /**
     * APP_ERR0039 CONFIG : Invalid login/password.
     */
    public static String[] APP_ERR0039 = {"APP_ERR0039", "APP_ERR0039 CONFIG : Invalid login/password."};

    /**
     * APP_ERR0040 CONFIG : APP_ERR0040 CONFIG : You don't have enaugh access rights to this section.\nContact your admin for support.
     */
    public static String[] APP_ERR0040 = {"APP_ERR0040", "APP_ERR0040 CONFIG : You don't have enaugh access rights to this section.\nContact your admin for support."};
    /**
     * APP_ERR0041 CONFIG : Your account has been locked.\nContact your admin for support.
     */
    public static String[] APP_ERR0041 = {"APP_ERR0041", "APP_ERR0041 CONFIG : Your account has been locked.\nContact your admin for support."};
    /**
     * APP_ERR0042 CONFIG : No packaging warehouse found for the selected project.
     */
    public static String[] APP_ERR0042 = {"APP_ERR0042", "APP_ERR0042 CONFIG : No warehouse found for the selected project."};
    /**
     * APP_ERR0043 CONFIG : No destinations found for this projet. Please check load_plan_dest table.
     */
    public static String[] APP_ERR0043 = {"APP_ERR0043", "APP_ERR0043 CONFIG :  No destinations found for this projet. Please check load_plan_dest table."};
    
    /**
     * APP_ERR0044 CONFIG : No family found in config_family table.
     */
    public static String[] APP_ERR0044 = {"APP_ERR0044", "APP_ERR0044 CONFIG : No family found in config_family table."};
    
    /**
     * APP_ERR0045 DISPATCH : No transport company specified.
     */
    public static String[] APP_ERR0045 = {"APP_ERR0045", "APP_ERR0045 DISPATCH : No transport company specified."};
    
    /**
     * APP_ERR0046 CONFIG : No packaging master found in packaging_config table.
     */
    public static String[] APP_ERR0046 = {"APP_ERR0046", "APP_ERR0046 CONFIG : No packaging master found in packaging_config table."};
 
    /**
     * APP_ERR0047 CONFIG : No family found for selected project. Check the project master data.
     */
    public static String[] APP_ERR0047 = {"APP_ERR0047", "APP_ERR0047 CONFIG : No family found for selected project. Check the project master data."};
    
    /**
     * APP_ERR0048 CONFIG : Please fill all required fields.
     */
    public static String[] APP_ERR0048 = {"APP_ERR0048", "APP_ERR0048 CONFIG : Please fill all required fields."};
    
    /**
     * APP_ERR0049 CONFIG : No transporter found in the global configuration.
     */
    public static String[] APP_ERR0049 = {"APP_ERR0049", "APP_ERR0049 CONFIG : No transporter found in the global configuration."};
    
    /**
     * APP_ERR0050 CONFIG : Pallet blocked. Can't add new pieces.
     */
    public static String[] APP_ERR0050A = {"APP_ERR0050","APP_ERR0050 USER ERROR : Pallet "};
    public static String[] APP_ERR0050B = {"APP_ERR0050","blocked no piece can be scanned."};
    
    /**
     * APP_ERR0051 CONFIG : Pallet blocked. Can't be moved to
     */
    public static String[] APP_ERR0051A = {"APP_ERR0051","APP_ERR0051 USER ERROR : Pallet "};
    public static String[] APP_ERR0051B = {"APP_ERR0051","blocked can't be moved to"};
}
