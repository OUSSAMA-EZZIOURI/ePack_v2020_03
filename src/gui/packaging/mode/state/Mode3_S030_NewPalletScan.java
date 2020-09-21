/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging.mode.state;

import __main__.GlobalVars;
import com.itextpdf.text.DocumentException;
import gui.packaging.Mode3_Context;
import helper.Helper;
import helper.PrinterHelper;
import entity.BaseContainer;
import entity.BaseContainerTmp;
import entity.BaseHarnessAdditionalBarecode;
import entity.BaseHarnessAdditionalBarecodeTmp;
import entity.BaseHarness;
import entity.HisBaseHarness;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import gui.packaging.PackagingVars;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 *
 * @author ezou1001
 */
public class Mode3_S030_NewPalletScan implements Mode3_State {

    private final ImageIcon imgIcon = new ImageIcon(GlobalVars.APP_PROP.getProperty("IMG_PATH") + "S041_NewPaletScan.jpg");

    private String palletNumber = "";

    /**
     *
     * @param palletNumber : The id of the new pallet created in
     * PACKAGING_UI9000_ChoosePackType_Mode2
     */
    public Mode3_S030_NewPalletScan(String palletNumber) {

        PackagingVars.Packaging_Gui_Mode3.setIconLabel(this.imgIcon);
        this.palletNumber = palletNumber;
        //Reload Data Table to display new pallet
        PackagingVars.Packaging_Gui_Mode3.reloadDataTable();

        PackagingVars.Packaging_Gui_Mode3.setFeedbackTextarea("Scanner la fiche d'ouverture palette \nN° "
                + palletNumber);
    }

    /**
     *
     * @param context
     */
    @Override
    public void doAction(Mode3_Context context) {
        JTextField scan_txtbox = PackagingVars.Packaging_Gui_Mode3.getScanTxt();
        String barcode = scan_txtbox.getText().trim();

        if (barcode.equals(this.palletNumber)) {
            try {
                //Vide le scan box
                clearScanBox(scan_txtbox);

                //##############################################################
                //Inserer les données de la nouvelle palette dans les 2 tables BaseContainer et BaseHarness
                BaseContainer bc = new BaseContainer().setDefautlVals();
                bc.setPalletNumber(context.getBaseContainerTmp().getPalletNumber());
                bc.setHarnessPart((context.getBaseContainerTmp().getHarnessPart().startsWith(GlobalVars.HARN_PART_PREFIX)) ? context.getBaseContainerTmp().getHarnessPart().substring(1) : context.getBaseContainerTmp().getHarnessPart());
                bc.setHarnessIndex(context.getBaseContainerTmp().getHarnessIndex());
                bc.setSupplierPartNumber(context.getBaseContainerTmp().getSupplierPartNumber());
                bc.setQtyExpected(context.getBaseContainerTmp().getQtyExpected());
                bc.setPackType(context.getBaseContainerTmp().getPackType());
                bc.setQtyRead(1);
                bc.setHarnessType(context.getBaseContainerTmp().getHarnessType());
                bc.setStdTime(context.getBaseContainerTmp().getStdTime());
                bc.setCraStdTime(context.getBaseContainerTmp().getCraStdTime());
                bc.setContainerState(GlobalVars.PALLET_OPEN);
                bc.setContainerStateCode(GlobalVars.PALLET_OPEN_CODE);
                bc.setPackWorkstation(GlobalVars.APP_HOSTNAME);
                bc.setSegment(context.getBaseContainerTmp().getSegment());
                bc.setWorkplace(context.getBaseContainerTmp().getWorkplace());
                bc.setUcsId(context.getBaseContainerTmp().getUcsId());
                bc.setUcsLifes(context.getBaseContainerTmp().getUcsLifes());
                bc.setComment(context.getBaseContainerTmp().getComment());
                bc.setOrder_no(context.getBaseContainerTmp().getOrder_no());
                bc.setSpecial_order(context.getBaseContainerTmp().getSpecial_order());
                bc.setPrice((context.getBaseContainerTmp().getPrice() != null) ? context.getBaseContainerTmp().getPrice() : 0.00);
                bc.setVolume(context.getBaseContainerTmp().getVolume());
                bc.setGrossWeight(context.getBaseContainerTmp().getGrossWeight());
                bc.setNetWeight(context.getBaseContainerTmp().getNetWeight());
                bc.setFGwarehouse(context.getBaseContainerTmp().getWarehouse());
                bc.setDestination(context.getBaseContainerTmp().getDestination());
                bc.setProject(context.getBaseContainerTmp().getProject());
                bc.setEngChange(context.getBaseContainerTmp().getEngChange());
                bc.setEngChangeDate(context.getBaseContainerTmp().getEngChangeDate());
                bc.setArticleDesc(context.getBaseContainerTmp().getArticleDesc());
                bc.setLabelPerPiece(context.getBaseContainerTmp().isLabelPerPiece());
                bc.setPriority(context.getBaseContainerTmp().getPriority());
                bc.setOpenningSheetCopies(context.getBaseContainerTmp().getOpenSheetCopies());
                bc.setClosingSheetCopies(context.getBaseContainerTmp().getCloseSheetCopies());
                bc.setClosingSheetFormat(context.getBaseContainerTmp().getClosingSheetFormat());
                bc.setPrint_destination(context.getBaseContainerTmp().getPrint_destination());
                //UILog.infoDialog("Closing format for mode 2: "+context.getBaseContainerTmp().getClosingSheetFormat());
                bc.create(bc);
                UILog.info(String.format("BaseContainer created %s ", bc.toString()));
                //##############################################################

                //##############################################################
                //Inserer les données du BaseHarness
                BaseHarness bh = new BaseHarness().setDefautlVals();
                System.out.println("The first piece in the pallet " + context.getBaseContainerTmp().getHarnessPart());
                bh.setHarnessPart(context.getBaseContainerTmp().getHarnessPart());
                bh.setCounter(context.getBaseContainerTmp().getHernessCounter());
                bh.setPalletNumber(context.getBaseContainerTmp().getPalletNumber());
                bh.setHarnessType(context.getBaseContainerTmp().getHarnessType());
                bh.setStdTime(context.getBaseContainerTmp().getStdTime());
                bh.setCraStdTime(context.getBaseContainerTmp().getCraStdTime());
                bh.setPackWorkstation(GlobalVars.APP_HOSTNAME);
                bh.setAssyWorkstation(context.getBaseContainerTmp().getAssyWorkstation());
                bh.setSegment(context.getBaseContainerTmp().getSegment());
                bh.setWorkplace(context.getBaseContainerTmp().getWorkplace());
                bh.setProject(context.getBaseContainerTmp().getProject());
                
                bh.setContainer(bc);

                //Insert the harness into the container
                bc.getHarnessList().add(bh);
                //##############################################################

                //##############################################################
                //Save harness History Line
                HisBaseHarness hbh = new HisBaseHarness().parseHarnessData(bh, "New harness added to pallet.");
                hbh.create(hbh);
                //##############################################################

                //############### SET & SAVE ENGINE LABEL DATA #################
                //if (PackagingVars.context.getUser().getHarnessType().equals(Helper.ENGINE)) {
                if (PackagingVars.mode3_context.getBaseHarnessAdditionalBarecodeTmp().getLabelCode().length != 0) {
                    for (String labelCode : PackagingVars.mode3_context.getBaseHarnessAdditionalBarecodeTmp().getLabelCode()) {
                        BaseHarnessAdditionalBarecode bel = new BaseHarnessAdditionalBarecode();
                        bel.setDefautlVals();
                        bel.setLabelCode(labelCode);
                        bel.setHarness(bh);
                        bel.create(bel);
                    }
                }
                //##############################################################
                //Print Harness Label if this option is set to true
                //######################### Print Harness Label ################
                if (bc.isLabelPerPiece()) {
                    PrinterHelper.PrintPieceLabel(bc, bh);
                }
                //##############################################################

                //Close connection
                //Clear session vals in mode3_context
                clearContextSessionVals();

                //Clear requested pallet txt box
                clearRequestedPallet_txt();

                //############## Check if pallet should be closed ##############
                //############## UCS Contains just 1 harness ###################
                if (bc.getQtyExpected() == 1) {
                    for (int copies = 0; copies < bc.getClosingSheetCopies(); copies++) {
                        System.out.println("Printing closing sheet copie " + copies);
                        try {
                            PrinterHelper.saveAndPrintClosingSheet(PackagingVars.mode3_context, bc, false, bc.getClosingSheetFormat());
                        } catch (DocumentException | IOException ex) {
                            UILog.severe(ex.toString());
                        } catch (Exception ex) {
                            Logger.getLogger(Mode3_S030_NewPalletScan.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    Helper.startSession();
                    bc.setContainerState(GlobalVars.PALLET_WAITING);
                    bc.setContainerStateCode(GlobalVars.PALLET_WAITING_CODE);
                    bc.update(bc);

                    context.getBaseContainerTmp().setPalletNumber(bc.getPalletNumber());
                    PackagingVars.Packaging_Gui_Mode3.setFeedbackTextarea("N° "
                            + GlobalVars.CLOSING_PALLET_PREFIX + this.palletNumber);

                    context.setState(new Mode3_S040_ClosingPallet());
                } else {
                    // Change go back to state HarnessPartScan
                    context.setState(new Mode3_S020_HarnessPartScan());
                }
            } catch (Exception ex) {
                Logger.getLogger(Mode3_S030_NewPalletScan.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            UILog.severe(ErrorMsg.APP_ERR0018[0], barcode);
            UILog.severeDialog(null, ErrorMsg.APP_ERR0018, barcode);
            clearScanBox(scan_txtbox);
            context.setState(this);
        }
    }

    /**
     *
     */
    @Override
    public void clearContextSessionVals() {
        //Pas besoin de réinitialiser le uid
        PackagingVars.mode3_context.setBaseContainerTmp(new BaseContainerTmp());
        PackagingVars.mode3_context.setBaseHarnessAdditionalBarecodeTmp(new BaseHarnessAdditionalBarecodeTmp());
    }

    @Override
    public String toString() {
        return "State S041 : S041_NewPaletScan";
    }

    /**
     *
     * @return
     */
    @Override
    public ImageIcon getImg() {
        return this.imgIcon;
    }

    /**
     *
     * @param scan_txtbox
     */
    @Override
    public void clearScanBox(JTextField scan_txtbox) {
        //Vider le champs de text scan
        scan_txtbox.setText("");
        scan_txtbox.requestFocusInWindow();
        PackagingVars.Packaging_Gui_Mode3.setScanTxt(scan_txtbox);
    }

    /**
     *
     */
    public void clearRequestedPallet_txt() {
        PackagingVars.Packaging_Gui_Mode3.setFeedbackTextarea("");

    }

}
