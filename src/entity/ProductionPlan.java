package entity;

import gui.packaging.PackagingVars;
import hibernate.DAO;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * ConfigUcs generated by hbm2java
 */
@Entity
@Table(name = "production_plan")
public class ProductionPlan extends DAO implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "production_plan_id_seq")
    @SequenceGenerator(name = "production_plan_id_seq", sequenceName = "production_plan_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "harness_part")
    private String harnessPart;

    @Column(name = "internal_part")
    private String internalPart;

    @Column(name = "create_id")
    private Integer createId;

    @Column(name = "write_id")
    private Integer writeId;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "write_time")
    private Date writeTime;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "write_user")
    private String writeUser;

    @Column(name = "planned_qty", nullable = false)
    private Integer plannedQty;

    public ProductionPlan() {
    }

    public ProductionPlan(String harnessPart, String internalPart, Integer plannedQty) {
        this.harnessPart = harnessPart.toUpperCase();
        this.plannedQty = plannedQty;
        this.internalPart = internalPart.toUpperCase();
        this.createId = PackagingVars.context.getUser().getId();
        this.writeId = PackagingVars.context.getUser().getId();
        this.writeUser = this.createUser = (PackagingVars.context.getUser().getFirstName() + " " + PackagingVars.context.getUser().getLastName()).toUpperCase();
        this.createTime = this.writeTime = new Date();
    }

    //####################### Getters & Setters ############################
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHarnessPart() {
        return harnessPart;
    }

    public void setHarnessPart(String harnessPart) {
        this.harnessPart = harnessPart.toUpperCase();
    }

    public String getInternalPart() {
        return internalPart;
    }

    public void setInternalPart(String internalPart) {
        this.internalPart = internalPart.toUpperCase();
    }

    public Integer getCreateId() {
        return createId;
    }

    public void setCreateId(Integer createId) {
        this.createId = createId;
    }

    public Integer getWriteId() {
        return writeId;
    }

    public void setWriteId(Integer writeId) {
        this.writeId = writeId;
    }

    public Date getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(Date writeTime) {
        this.writeTime = writeTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser.toUpperCase();
    }

    public String getWriteUser() {
        return writeUser;
    }

    public void setWriteUser(String writeUser) {
        this.writeUser = writeUser.toUpperCase();
    }

    public Integer getPlannedQty() {
        return plannedQty;
    }

    public void setPlannedQty(Integer plannedQty) {
        this.plannedQty = plannedQty;
    }

   
    
    public void setPlannedQty(int plannedQty) {
        this.plannedQty =  plannedQty;
    }

    @Override
    public String toString() {
        return "ProductionPlan{" + "id=" + id + "\n, harnessPart=" + harnessPart + "\n, internalPart=" + internalPart + "\n, createId=" + createId + "\n, writeId=" + writeId + "\n, writeTime=" + writeTime + "\n, createTime=" + createTime + "\n, createUser=" + createUser + "\n, writeUser=" + writeUser + "\n, plannedQty=" + plannedQty + '}';
    }
    
    
    
    
    
}
