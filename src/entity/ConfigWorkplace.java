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
@Table(name = "config_workplace")
public class ConfigWorkplace extends DAO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "config_workplace_id_seq")
    @SequenceGenerator(name = "config_workplace_id_seq", sequenceName = "config_workplace_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "workplace")
    private String workplace;

    @Column(name = "segment")
    private String segment;

    @Column(name = "project")
    private String project;

    public ConfigWorkplace() {
    }

    public ConfigWorkplace(String configWorkplace) {
        this.workplace = configWorkplace;
    }

    public ConfigWorkplace(String project, String segment, String workplace) {
        this.workplace = workplace;
        this.segment = segment;
        this.project = project;
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

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    //######################################################################
    public List select() {
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_ALL_WORKPLACES);
        UILog.info(query.getQueryString());
        Helper.sess.getTransaction().commit();
        return query.list();
    }

    public List selectBySegment(String segment) {
        Helper.startSession();
        Query query;
        if (!"".equals(segment) || segment.length() != 0) {
            query = Helper.sess.createQuery(HQLHelper.GET_WORKPLACES_BY_SEGMENT);
            query.setParameter("segment", segment);
        } else {
            query = Helper.sess.createQuery(HQLHelper.GET_ALL_WORKPLACES);
        }

        Helper.sess.getTransaction().commit();
        return query.list();
    }

    /**
     *
     * @param parentUI
     * @param jbox
     * @param segment Segment name to be filter on
     * @param displayAll display the "ALL" filter in shown values in the first
     * position of the list
     * @return
     */
    public static JComboBox initWorkplaceJBox(Object parentUI, JComboBox jbox, String segment, boolean displayAll) {
        List result;
        jbox.removeAllItems();
        if (displayAll) {
            jbox.addItem("ALL");
        }
        if (segment != null && !segment.isEmpty() && !"null".equals(segment)) {
            result = new ConfigWorkplace().selectBySegment(segment);
            if (result.isEmpty()) {
                UILog.severeDialog((Component) parentUI, ErrorMsg.APP_ERR0038);
                UILog.severe(ErrorMsg.APP_ERR0038[1]);
            } else { //Map project data in the list
                for (Object o : result) {
                    ConfigWorkplace cp = (ConfigWorkplace) o;
                    //jbox.addItem(new ComboItem(cp.getWorkplace(), cp.getWorkplace()));
                    jbox.addItem(cp.getWorkplace());
                }
            }
        } else {
            result = new ConfigWorkplace().select();
            if (result.isEmpty()) {
                UILog.severeDialog((Component) parentUI, ErrorMsg.APP_ERR0034);
                UILog.severe(ErrorMsg.APP_ERR0034[1]);
            } else { //Map project data in the list
                for (Object o : result) {
                    ConfigWorkplace p = (ConfigWorkplace) o;
                    //jbox.addItem(new ComboItem(p.getWorkplace(), p.getWorkplace()));
                    jbox.addItem(p.getWorkplace());
                }
            }
        }
        return jbox;
    }

}
