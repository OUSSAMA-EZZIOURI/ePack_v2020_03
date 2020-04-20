package helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JFrame;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import ui.UILog;

/**
 * @author hennebrueder This class garanties that only one single SessionFactory
 * is instanciated and that the configuration is done thread safe as singleton.
 * Actually it only wraps the Hibernate SessionFactory. You are free to use any
 * kind of JTA or Thread transactionFactories.
 */
public class HibernateUtil {

    private final static Properties p = new Properties();
    /**
     * The single instance of hibernate SessionFactory
     */
    private static org.hibernate.SessionFactory sessionFactory;

    /**
     * disable contructor to guaranty a single instance
     */
    private HibernateUtil() {
    }

    static {
        try {
            //Load properties from db.properties file
            InputStream input = null;
            try {
                String path = "."+File.separatorChar+"db.properties";
                input = new FileInputStream(path);
                
                // load a properties file
                p.load(input);
                // get the property value and print it out
                System.out.println("Load Database properties file :\n " + path);

                // Create the SessionFactory from standard (hibernate.cfg.xml) 
                // config file.      
                sessionFactory = new AnnotationConfiguration().setProperties(p).configure().buildSessionFactory();
            } catch (IOException ex) {
                UILog.exceptionDialog(new JFrame(), "Database properties error !",
                        "Le fichier 'config.properties' est introuvable dans le dossier"
                                + " de l'application.", 
                        ex);
                ex.printStackTrace();
                System.exit(0);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (HibernateException ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);

        }
    }

    public static SessionFactory getInstance() {
        return sessionFactory;
    }

    /**
     * Opens a session and will not bind it to a session context
     *
     * @return the session
     */
    public Session openSession() {
        return sessionFactory.openSession();
    }

    /**
     * Returns a session from the session context. If there is no session in the
     * context it opens a session, stores it in the context and returns it. This
     * factory is intended to be used with a hibernate.cfg.xml including the
     * following property <property
     * name="current_session_context_class">thread</property> This would return
     * the current open session or if this does not exist, will create a new
     * session
     *
     * @return the session
     */
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * closes the session factory
     */
    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        sessionFactory = null;

    }
}
