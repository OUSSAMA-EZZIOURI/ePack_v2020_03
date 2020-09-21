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
@Table(name = "config_segment")
public class ConfigSegment extends DAO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "config_segment_id_seq")
    @SequenceGenerator(name = "config_segment_id_seq", sequenceName = "config_segment_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "segment")
    private String segment;

    @Column(name = "project")
    private String project;

    public ConfigSegment() {
    }

    public ConfigSegment(String segment, String project) {
        this.segment = segment;
        this.project = project;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
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
        Query query = Helper.sess.createQuery(HQLHelper.GET_ALL_SEGMENTS);
        Helper.sess.getTransaction().commit();
        return query.list();
    }

    public List selectSegmentByProject(String project) {
        
        Helper.startSession();

        Query query = Helper.sess.createQuery(HQLHelper.GET_SEGMENTS_BY_PROJECT);
        query.setParameter("project", project);

        Helper.sess.getTransaction().commit();
        return query.list();
    }
    /**
     * Load segments list in jbox according to selected project.
     * @param parentUI
     * @param segment_jbox
     * @param project
     * @param displayAll
     * @return 
     */
    public static JComboBox setSegmentByProject(Object parentUI, JComboBox segment_jbox, String project, boolean displayAll) {
        segment_jbox.removeAllItems();
        if (displayAll) {
            segment_jbox.addItem("ALL");
        }
        if ("ALL".equals(project)) {
            segment_jbox.setSelectedIndex(0);
            segment_jbox.setEnabled(false);
            return segment_jbox;
        } else {
            List result = new ConfigSegment().selectSegmentByProject(project);
            if (result.isEmpty()) {
                UILog.severeDialog((Component) parentUI, ErrorMsg.APP_ERR0037);
                UILog.severe(ErrorMsg.APP_ERR0037[1]);
                return segment_jbox;
            } else { //Map project data in the list
                for (Object o : result) {
                    ConfigSegment cp = (ConfigSegment) o;
                    //segment_jbox.addItem(cp.getSegment());
                    segment_jbox.addItem(cp.getSegment());
                }
                segment_jbox.setSelectedIndex(0);
                segment_jbox.setEnabled(true);
                return segment_jbox;
            }
        }
    }

}
