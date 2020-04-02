package entity;
// Generated 6 f�vr. 2016 21:43:55 by Hibernate Tools 3.6.0

import helper.ComboItem;
import helper.Helper;
import java.util.List;
import org.hibernate.Query;
import helper.HQLHelper;
import hibernate.DAO;
import java.awt.Component;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.swing.JComboBox;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 *
 */
@Entity
@Table(name = "config_project")
public class ConfigProject extends DAO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "config_project_id_seq")
    @SequenceGenerator(name = "config_project_id_seq", sequenceName = "config_project_id_seq", allocationSize = 1)
    private Integer id;
    ;
    
    @Column(name = "project")
    private String project;

    @Column(name = "description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConfigProject() {
    }

    public ConfigProject(String project) {
        this.project = project;
    }

    public ConfigProject(String project, String description) {
        this.project = project;
        this.description = description;
    }
    
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    //######################################################################
    public List select() {
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_ALL_PROJECT);        
        UILog.info(query.getQueryString());
        Helper.sess.getTransaction().commit();
        return query.list();
    }
    
    public List selectDistinct() {
        Helper.startSession();        
        Query query = Helper.sess.createQuery(HQLHelper.GET_DISTINCT_PROJECT);
        UILog.info(query.getQueryString());
        Helper.sess.getTransaction().commit();
        return query.list();
    }

    /**
     *
     * @param parentUI
     * @param box
     * @param displayAll display the "ALL" filter in shown values in the first
     * position of the list
     */
    public static JComboBox initProjectsJBox(Object parentUI, JComboBox box, boolean displayAll) {

        List result = new ConfigProject().selectDistinct();
        if (result.isEmpty()) {
            UILog.severeDialog((Component) parentUI, ErrorMsg.APP_ERR0035);
            UILog.severe(ErrorMsg.APP_ERR0035[1]);
        } else { //Map project data in the list
            box.removeAllItems();
            if (displayAll) {
                box.addItem("ALL");
            }
            
            for (Object o : result) {
                box.addItem(o);                
            }           
        }
        return box;
    }
    
    /**
     *
     * @param parentUI
     * @param box
     * @param defaultValue A string value to be displayed in the first place (eg : 'ALL', or '')
     * position of the list
     */
    public static JComboBox initProjectsJBox(Object parentUI, JComboBox box, String defaultValue) {

        List result = new ConfigProject().selectDistinct();
        if (result.isEmpty()) {
            UILog.severeDialog((Component) parentUI, ErrorMsg.APP_ERR0035);
            UILog.severe(ErrorMsg.APP_ERR0035[1]);
        } else { //Map project data in the list
            box.removeAllItems();
            box.addItem(defaultValue);
            for (Object o : result) {
                box.addItem(o);                
            }           
        }
        return box;
    }

}
