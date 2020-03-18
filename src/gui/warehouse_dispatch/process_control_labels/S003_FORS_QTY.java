/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.warehouse_dispatch.process_control_labels;

import __main__.GlobalVars;
import entity.BaseContainer;
import helper.HQLHelper;
import helper.Helper;
import java.util.List;
import javax.swing.JTextField;

import org.hibernate.Query;
import gui.warehouse_dispatch.state.WarehouseHelper;
import helper.SoundHelper;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */
public class S003_FORS_QTY implements ControlState {

    private BaseContainer bc;
    private Double qtyProduction = 0.00;
    private Double qtyDispatch = 0.00;

    /**
     *
     * @param bc Container/Pallet object to be checked
     */
    public S003_FORS_QTY(BaseContainer bc) {
        this.bc = bc;
        this.qtyProduction = bc.getQtyExpectedDouble();
        WarehouseHelper.Label_Control_Gui.setMessageLabel("Scanner la quantité de la fiche expédition (eg: " + GlobalVars.DISPATCH_QUANTITY_PREFIX + "50.00).", 2);
        WarehouseHelper.Label_Control_Gui.setProgressValue(75);
    }

    @Override
    public void doAction(WarehouseControlContext context) {
        JTextField scan_txtbox = WarehouseHelper.Label_Control_Gui.getScanTxt();
        try {

            //If it's starts with prefix we will remove it
            this.qtyDispatch = (scan_txtbox.getText().trim().startsWith(GlobalVars.DISPATCH_QUANTITY_PREFIX)) ? Double.valueOf(scan_txtbox.getText().trim().substring(1)) : Double.valueOf(scan_txtbox.getText().trim());
            
            System.out.println("this.qtyDispatch "+this.qtyDispatch+" "+this.qtyDispatch.getClass());
            System.out.println("this.qtyProduction "+this.qtyProduction+" "+this.qtyProduction.getClass());
            System.out.println("test de quantités  : dispatch " + this.qtyDispatch + " = prod "
                    + this.qtyProduction + " "
                    + (Double.valueOf(this.qtyDispatch).compareTo(Double.valueOf(this.qtyProduction))));
            if (!WarehouseHelper.checkForsQtyFormat(new DecimalFormat("00.00").format(this.qtyDispatch))) {
                SoundHelper.PlayErrorSound(null);
                String msg = "Format de quantité incorrecte. Vérifier le code scanné ou la configuration des formats.";
                WarehouseHelper.Label_Control_Gui.setMessageLabel(msg, -1);
                JOptionPane.showOptionDialog(null, msg, "Erreur de format inconnue.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
                clearScanBox(scan_txtbox);

            } // Check the qty in FORS label vs qty in Production label
            else if (Double.valueOf(this.qtyDispatch).compareTo(Double.valueOf(this.qtyProduction)) != 0) {

                SoundHelper.PlayErrorSound(null);

                String msg = "Quantité fiche production et fiche dispatch incompatibles: " + this.qtyProduction + " # " + this.qtyDispatch;
                WarehouseHelper.Label_Control_Gui.setMessageLabel(msg, -1);
                JOptionPane.showOptionDialog(null, msg, "Erreur de quantité incompatibles.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
                clearScanBox(scan_txtbox);
            } else { // Everything is fine OK
                //Passer au statut S004_DISPATCH_LABEL_NO
                SoundHelper.PlayOkSound(null);
                clearScanBox(scan_txtbox);

                WarehouseHelper.Label_Control_Gui.setTxt_dispatchQty(new DecimalFormat("00.00").format(this.qtyDispatch));

                S004_DISPATCH_LABEL_NO state = new S004_DISPATCH_LABEL_NO(this.bc);
                context.setState(state);
            }
        } catch (Exception e) {
            SoundHelper.PlayErrorSound(null);
            String msg = "Format de quantité incorrecte. Vérifier le code scanné ou la configuration des formats.";
            WarehouseHelper.Label_Control_Gui.setMessageLabel(msg, -1);
            JOptionPane.showOptionDialog(null, msg, "Erreur de format inconnue.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
            clearScanBox(scan_txtbox);
        }

    }

    @Override
    public void clearScanBox(JTextField scan_txtbox) {
        //Vider le champs de text scan
        scan_txtbox.setText("");
        scan_txtbox.requestFocusInWindow();
        WarehouseHelper.Label_Control_Gui.setScanTxt(scan_txtbox);
    }

    @Override
    public void clearContextSessionVals() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Check if this pallet Number is already read.
     *
     * @param palletNumber
     * @return
     */
    public boolean checkIfLineExist(String palletNumber) {
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PAL_NUM);
        query.setParameter("palletNumber", palletNumber);

        Helper.sess.getTransaction().commit();
        List result = query.list();

        if (!result.isEmpty()) {
            return true;
        }
        return false;
    }
}
