package entity;
// Generated 6 f�vr. 2016 21:43:55 by Hibernate Tools 3.6.0

import helper.Helper;
import java.util.List;
import org.hibernate.Query;
import helper.HQLHelper;
import hibernate.DAO;
import java.awt.Component;
import java.io.Serializable;
import java.util.Vector;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 * ConfigFamily generated by hbm2java
 */
@Entity
@Table(name = "config_transporter")
public class ConfigTransporter extends DAO implements Serializable {

    

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "config_transporter_id_seq")
    @SequenceGenerator(name = "config_transporter_id_seq", sequenceName = "config_transporter_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "name")
    private String name;

    public ConfigTransporter(String name) {
        this.name = name;
    }

    public ConfigTransporter() {
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

    //######################################################################
    public List select() {
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_ALL_TRANSPORTERS);
        Helper.sess.getTransaction().commit();
        return query.list();
    }

    public static JComboBox initFamilyByProject(Object parentUI, JComboBox box) {
        List result;
        box.removeAllItems();

            //box.addItem(new ComboItem("ALL", "ALL"));
            box.addItem("ALL");
            result = new ConfigTransporter().select();
            if (result.isEmpty()) {
                UILog.severeDialog((Component) parentUI, ErrorMsg.APP_ERR0048);
                UILog.severe(ErrorMsg.APP_ERR0049[1]);
            } else { //Map project data in the list
                for (Object o : result) {
                    ConfigTransporter item = (ConfigTransporter) o;
                    //box.addItem(new ComboItem(item.getName(), item.getName()));
                    box.addItem(item.getName());
                }
            }
        
        return box;

    }
    
    public JTable getData(JTable jtable, Vector dataVector){
        
        
        Vector<String> jtable_header = new Vector<String>();
        jtable_header.add("ID");
        jtable_header.add("NAME");
        jtable.setModel(new DefaultTableModel(dataVector, jtable_header));
        
        return jtable;
        
    }
    
    /**
     * 
     * @param parentUI
     * @param transporter_filter
     * @param displayAll
     * @return 
     */
    public static JComboBox initTransporterJBox(Object parentUI, JComboBox transporter_filter, boolean displayAll) {
        List result = new ConfigTransporter().select();
        if (result.isEmpty()) {
            UILog.severeDialog((Component) parentUI, ErrorMsg.APP_ERR0049);
            UILog.severe(ErrorMsg.APP_ERR0049[1]);
        } else { //Map project data in the list
            transporter_filter.removeAllItems();
            if (displayAll) {
                //transporter_filter.addItem(new ComboItem("ALL", "ALL"));
                transporter_filter.addItem("ALL");
            }
            for (Object o : result) {
                ConfigTransporter p = (ConfigTransporter) o;
                //transporter_filter.addItem(new ComboItem(p.getName(), p.getName()));
                transporter_filter.addItem(p.getName());
            }
        }
        return transporter_filter;
    }

}
