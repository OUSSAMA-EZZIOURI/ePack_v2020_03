package entity;
// Generated 6 f�vr. 2016 21:43:55 by Hibernate Tools 3.6.0

import helper.ComboItem;
import helper.Helper;
import java.util.List;
import helper.HQLHelper;
import hibernate.DAO;
import java.awt.Component;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.swing.JComboBox;
import org.hibernate.Query;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 * 
 */
@Entity
@Table(name = "packaging_master")
public class PackagingMaster extends DAO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "packaging_master_id_seq")
    @SequenceGenerator(name = "packaging_master_id_seq", sequenceName = "packaging_master_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "pack_master", unique = true)
    private String packMaster;

    @Column(name = "pack_intern_pn", nullable = true)
    private String packInternPN;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "pack_height", nullable = true)
    private float packHeight;

    @Column(name = "pack_width", nullable = true)
    private float packWidth;

    @Column(name = "pack_length", nullable = true)
    private float packLength;

    @Column(name = "pack_weight", nullable = true)
    private float packWeight;

    @Column(name = "weight_uom", nullable = true)
    private String weightUOM;

    @Column(name = "dimension_uom", nullable = true)
    private String dimensionUOM;
    
    @Column(name = "volume", nullable = true)
    private double volume;

    public PackagingMaster() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPackMaster() {
        return packMaster;
    }

    public void setPackMaster(String packMaster) {
        this.packMaster = packMaster;
    }

    public String getPackInternPN() {
        return packInternPN;
    }

    public void setPackInternPN(String packInternPN) {
        this.packInternPN = packInternPN;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPackHeight() {
        return packHeight;
    }

    public void setPackHeight(float packHeight) {
        this.packHeight = packHeight;
    }

    public float getPackWidth() {
        return packWidth;
    }

    public void setPackWidth(float packWidth) {
        this.packWidth = packWidth;
    }

    public float getPackLength() {
        return packLength;
    }

    public void setPackLength(float packLength) {
        this.packLength = packLength;
    }

    public float getPackWeight() {
        return packWeight;
    }

    public void setPackWeight(float packWeight) {
        this.packWeight = packWeight;
    }

    public String getWeightUOM() {
        return weightUOM;
    }

    public void setWeightUOM(String weightUOM) {
        this.weightUOM = weightUOM;
    }

    public String getDimensionUOM() {
        return dimensionUOM;
    }

    public void setDimensionUOM(String dimensionUOM) {
        this.dimensionUOM = dimensionUOM;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
    
    //######################################################################
    public List select() {
        Helper.startSession();

        Query query = Helper.sess.createQuery(HQLHelper.GET_ALL_PACK_MASTER);

        Helper.sess.getTransaction().commit();
        return query.list();
    }

    public List selectPackMaster(String packMaster) {
        Helper.startSession();

        Query query = Helper.sess.createQuery(HQLHelper.GET_PACK_MASTER);
        query.setParameter("packMaster", packMaster);

        Helper.sess.getTransaction().commit();
        return query.list();
    }
    
    /**
     *
     * @param parentUI
     * @param box
     * @param displayAll display the "ALL" filter in shown values in the first 
     * position of the list
     * @return 
     */
    public static JComboBox initPackMasterJBox(Object parentUI, JComboBox box, boolean displayAll) {
        List result = new PackagingMaster().select();
        if (result.isEmpty()) {
            UILog.severeDialog((Component) parentUI, ErrorMsg.APP_ERR0046);
            UILog.severe(ErrorMsg.APP_ERR0046[1]);
        } else { //Map project data in the list
            box.removeAllItems();
            if (displayAll) {
                //box.addItem(new ComboItem("ALL", "ALL"));
                box.addItem("ALL");
            }
            for (Object o : result) {
                PackagingMaster p = (PackagingMaster) o;
                //box.addItem(new ComboItem(p.getPackMaster(), p.getPackMaster()));
                box.addItem(p.getPackMaster());
            }
        }
        return box;             

    }

    

}
