/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import hibernate.DAO;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name = "load_plan_dispatch_label")
public class LoadPlanDispatchLabel extends DAO implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_plan_dispatch_label_id_seq")
    @SequenceGenerator(name = "load_plan_dispatch_label_id_seq", sequenceName = "load_plan_dispatch_label_id_seq", allocationSize = 1)
    private Integer id;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "create_id")
    private int createId;

    @Column(name = "fdp")
    private String fdp;      
    
    @Column(name = "part_number")
    private String partNumber;   

    @Column(name = "qty")
    private Double qty;

    @Column(name = "load_plan_id", insertable = true, updatable = true)
    private int loadPlanId;

    /**
     * Dispatch label serial number
     */
    @Column(name = "dispatch_label_no", nullable = true)
    private String dispatchLabelNo;
    
    
    @ManyToOne(optional = true, cascade = CascadeType.REFRESH)
    private LoadPlan loadPlan;

    public LoadPlanDispatchLabel() {
    }

    public LoadPlanDispatchLabel(String fdp, String partNumber, Double qty, int loadPlanId, String dispatchLabelNo) {
        this.fdp = fdp;
        this.partNumber = partNumber;
        this.qty = qty;
        this.loadPlanId = loadPlanId;
        this.dispatchLabelNo = dispatchLabelNo;
    }

    

    @Override
    public String toString() {
        return "LoadPlanDispatchLabel{" + "id=" + id + ",\n createTime=" + createTime + ",\n createId=" + createId + ",\n fdp=" + fdp + ",\n qty=" + qty + ",\n loadPlanId=" + loadPlanId + ",\n dispatchLabelNo=" + dispatchLabelNo + ",\n loadPlan=" + loadPlan + '}';
    }
       

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getCreateId() {
        return createId;
    }

    public void setCreateId(int createId) {
        this.createId = createId;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    
    

    public String getFdp() {
        return fdp;
    }

    public void setFdp(String fdp) {
        this.fdp = fdp;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public int getLoadPlanId() {
        return loadPlanId;
    }

    public void setLoadPlanId(int loadPlanId) {
        this.loadPlanId = loadPlanId;
    }

    public String getDispatchLabelNo() {
        return dispatchLabelNo;
    }

    public void setDispatchLabelNo(String dispatchLabelNo) {
        this.dispatchLabelNo = dispatchLabelNo;
    }

    public LoadPlan getLoadPlan() {
        return loadPlan;
    }

    public void setLoadPlan(LoadPlan loadPlan) {
        this.loadPlan = loadPlan;
    }

    
    

}
