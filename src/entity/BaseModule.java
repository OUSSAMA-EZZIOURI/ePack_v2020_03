package entity;
// Generated 6 févr. 2016 21:43:55 by Hibernate Tools 3.6.0

import hibernate.DAO;
import java.io.Serializable;
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
 * BaseUser generated by hbm2java
 */
@Entity
@Table(name = "base_module")

public class BaseModule extends DAO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "base_module_id_seq")
    @SequenceGenerator(name = "base_module_id_seq", sequenceName = "base_module_id_seq", allocationSize = 1)
    private Integer id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "write_time")
    private Date writeTime;

    @Column(name = "create_id")
    private int createId;

    @Column(name = "write_id")
    private int writeId;
    
    @Column(name = "module_name")
    private String moduleName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "version")
    private String version;

    public BaseModule() {
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

    public Date getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(Date writeTime) {
        this.writeTime = writeTime;
    }

    public int getCreateId() {
        return createId;
    }

    public void setCreateId(int createId) {
        this.createId = createId;
    }

    public int getWriteId() {
        return writeId;
    }

    public void setWriteId(int writeId) {
        this.writeId = writeId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
