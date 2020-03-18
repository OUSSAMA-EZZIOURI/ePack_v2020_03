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
@Table(name = "wire_stock_loc")
public class WireStockLoc extends DAO implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wire_stock_loc_id_seq")
    @SequenceGenerator(name = "wire_stock_loc_id_seq", sequenceName = "wire_stock_loc_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "location")
    private String location;

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

    public WireStockLoc() {
    }

    public WireStockLoc(String location) {
        this.location = location;
        this.createId = PackagingVars.context.getUser().getId();
        this.writeId = PackagingVars.context.getUser().getId();
        this.writeUser = this.createUser = PackagingVars.context.getUser().getFirstName() + " " + PackagingVars.context.getUser().getLastName();
        this.createTime = this.writeTime = new Date();
    }

    //######################################################################
    /**
     * public List select() { Helper.startSession(); Query query =
     * Helper.sess.createQuery(HQLHelper.); UILog.info(query.getQueryString());
     * Helper.sess.getTransaction().commit(); return query.list(); } *
     */
    //####################### Getters & Setters ############################
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
        this.createUser = createUser;
    }

    public String getWriteUser() {
        return writeUser;
    }

    @Override
    public String toString() {
        return "WireStockLoc{" + "id=" + id + ",\n location=" + location + ",\n createId=" + createId + ",\n writeId=" + writeId + ",\n writeTime=" + writeTime + ",\n createTime=" + createTime + ",\n createUser=" + createUser + ",\n writeUser=" + writeUser + '}';
    }

}
