package entity;
// Generated 6 f�vr. 2016 21:43:55 by Hibernate Tools 3.6.0

import helper.Helper;
import java.util.List;
import helper.HQLHelper;
import hibernate.DAO;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.Query;

/**
 * 
 */
@Entity
@Table(name = "packaging_items")
public class PackagingItems extends DAO implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "packaging_items_id_seq")
    @SequenceGenerator(name = "packaging_items_id_seq", sequenceName = "packaging_items_id_seq", allocationSize = 1)
    private Integer id;
    
    @Column(name="pack_item", unique = true)
    private String packItem;        
   
    @Column(name="item_intern_pn", nullable = true)
    private String itemInternPN;
    
    @Column(name="description", nullable = true)
    private String description;    
    
    @Column(name="item_height", nullable = true)
    private float itemHeight;    
    
    @Column(name="item_width", nullable = true)
    private float itemWidth;    
    
    @Column(name="item_length", nullable = true)
    private float itemLength;    
    
    @Column(name="item_weight", nullable = true)
    private float itemWeight; 
    
    @Column(name="weight_uom", nullable = true)
    private String weightUOM; 
    
    @Column(name="dimension_uom", nullable = true)
    private String dimensionUOM; 
    
    @Column(name="alert_qty")
    private float alertQty = 0;
    
    //@Column(name="qty")
    //private float qty = 0;           
    
    public PackagingItems() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPackItem() {
        return packItem;
    }

    public void setPackItem(String packItem) {
        this.packItem = packItem;
    }

    public String getItemInternPN() {
        return itemInternPN;
    }

    public void setItemInternPN(String itemInternPN) {
        this.itemInternPN = itemInternPN;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(float itemHeight) {
        this.itemHeight = itemHeight;
    }

    public float getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(float itemWidth) {
        this.itemWidth = itemWidth;
    }

    public float getItemLength() {
        return itemLength;
    }

    public void setItemLength(float itemLength) {
        this.itemLength = itemLength;
    }

    public float getItemWeight() {
        return itemWeight;
    }

    public void setItemWeight(float itemWeight) {
        this.itemWeight = itemWeight;
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

    public float getAlertQty() {
        return alertQty;
    }

    public void setAlertQty(float alertQty) {
        this.alertQty = alertQty;
    }
    
    /*
    public float getQty() {
        return qty;
    }

    public void setQty(float initialQty) {
        this.qty = initialQty;
    }*/        
            
    //######################################################################
    public List selectAllPackItems() {
        Helper.startSession();
        
        Query query = Helper.sess.createQuery(HQLHelper.GET_ALL_PACK_ITEMS);
        
        Helper.sess.getTransaction().commit();
        return query.list();
    }
    
    public List selectPackItem(String packItem) {
        Helper.startSession();
        
        Query query = Helper.sess.createQuery(HQLHelper.GET_PACK_ITEM_BY_NAME);
        query.setParameter("packItem", packItem);
        
        Helper.sess.getTransaction().commit();
        return query.list();
    }
        

}