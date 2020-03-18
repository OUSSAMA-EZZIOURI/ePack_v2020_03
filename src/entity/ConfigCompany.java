package entity;
// Generated 6 f�vr. 2016 21:43:55 by Hibernate Tools 3.6.0

import helper.Helper;
import org.hibernate.Query;
import helper.HQLHelper;
import helper.HibernateUtil;
import hibernate.DAO;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 */
@Entity
@Table(name = "config_company")
public class ConfigCompany extends DAO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "config_company_id_seq")
    @SequenceGenerator(name = "config_company_id_seq", sequenceName = "config_company_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "company_name", unique = true)
    private String name;

    @Column(name = "description")
    private String desc;

    @Column(name = "address_1")
    private String address1;

    @Column(name = "address_2")
    private String address2;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "zip")
    private String zip;

    @Column(name = "website")
    private String website;

    public ConfigCompany() {
    }

    public ConfigCompany(String name, String desc, String address1, String address2, String country, String city, String zip, String website) {
        this.name = name;
        this.desc = desc;
        this.address1 = address1;
        this.address2 = address2;
        this.country = country;
        this.city = city;
        this.zip = zip;
        this.website = website;
    }
    
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
    
    /**
     * 
     * @param id company id
     * @return 
     */
    public ConfigCompany getCompany(int id) {
        
        Helper.openSession();
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_COMPANY_BY_ID);
        query.setParameter("id", id);
        Helper.sess.getTransaction().commit();
        return (ConfigCompany) query.list().get(0);
    }

    @Override
    public String toString() {
        return "ConfigCompany{" + "id=" + id + ", name=" + name + ", desc=" + desc + ", address1=" + address1 + ", address2=" + address2 + ", country=" + country + ", city=" + city + ", zip=" + zip + ", website=" + website + '}';
    }
    
    

}
