package __main__;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import entity.ManufactureUsers;
import gui.packaging.PackagingVars;
import gui.packaging.mode.state.Mode3_S010_UserCodeScan;
import helper.Helper;
import java.net.ServerSocket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import helper.HQLHelper;
import helper.InactivityListener;
import helper.JTextAreaOutputStream;
import helper.UIHelper;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.bytecode.stackmap.TypeData.ClassName;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.hibernate.Query;
import ui.UILog;
import ui.error.ErrorMsg;
import ui.info.InfoMsg;

/**
 *
 * @author Administrator
 */
public class AuthFrame extends javax.swing.JFrame {

    /**
     * To be used because the custom UILog object has not been initialized yet !
     */
    private Logger LOGGER;

    //private static final int PORT = 12345;        // random large port number
    private ServerSocket s;

    private ManufactureUsers user;

    static InactivityListener inactivityExitListener;

    int InactivityExitTime = 30; //In minutes

    /**
     * Boot the Nimbus look and feel
     */
    private void loadLookAndFeel() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                System.out.println("" + info.getName());
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AuthFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AuthFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AuthFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AuthFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    private void loadProperties() {
        String feedback = PropertiesLoader.loadConfigProperties();
        GlobalMethods.createDefaultDirectories();
        LOGGER = Logger.getLogger(ClassName.class.getName());
        LOGGER.log(Level.INFO, feedback);
    }

    private void loadAppIcon() {
        ImageIcon img = new ImageIcon(GlobalVars.APP_PROP.getProperty("IMG_PATH") + "/icon.png");
        this.setIconImage(img.getImage());
    }

    private void loadLabelsContent() {
        versionLabel.setText(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION);
        authorLabel.setText(GlobalVars.APP_AUTHOR);
        this.setTitle(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION);
    }

    private void setInactivityListener() {
        InactivityExitTime = Integer.valueOf(GlobalVars.APP_PROP.getProperty("INACTIVITY_EXIT_TIME"));
        //Set innactivity exit action
        //Intialize the innactivity auto-logout
        Action logout = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //Disconnect the user and close mainFrame UI
                System.out.println("Call for exit action ");
                //System.exit(0);
                JFrame frame = (JFrame) e.getSource();
                frame.dispose();
            }

        };
        inactivityExitListener = new InactivityListener(this, logout, InactivityExitTime);
        inactivityExitListener.start();
    }

    private void setJFrameOptions() {
        UIHelper.centerJFrame(this);
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Creates new form UI0000_ProjectChoice
     */
    public AuthFrame() {

        try {
            initComponents();

            loadProperties();

            loadLookAndFeel();

            loadAppIcon();

            loadLabelsContent();

            setInactivityListener();

            setJFrameOptions();

            login_textfield.requestFocus(true);

        } catch (Exception e) {
            UILog.exceptionDialog(this, "", "", e);
            System.exit(-1);
        }
    }

    /**
     * Creates new form UI0000_ProjectChoice
     */
    public AuthFrame(SplashScreen.Task task) throws InterruptedException {
        try {
            initComponents();

            Random random = new Random();
            int progress = task.getProgress();
            int oldProgress = progress;

            System.out.println("Starting...");
            oldProgress = progress;
            progress = oldProgress + random.nextInt(10);
            task.firePropertyChange("progress", oldProgress, Math.min(progress, 100));
            Thread.sleep(random.nextInt(2000));

            loadLookAndFeel();
            System.out.println("Load application theme...");
            oldProgress = progress;
            progress = oldProgress + random.nextInt(30);
            task.firePropertyChange("progress", oldProgress, Math.min(progress, 100));
            Thread.sleep(random.nextInt(2000));

            loadProperties();
            System.out.println("Load properties...");
            oldProgress = progress;
            progress = oldProgress + random.nextInt(50);
            task.firePropertyChange("progress", oldProgress, Math.min(progress, 100));
            Thread.sleep(random.nextInt(2000));

            //UIHelper.centerJFrame(this);
            System.out.println("Load window content...");
            oldProgress = progress;
            progress = oldProgress + random.nextInt(60);
            task.firePropertyChange("progress", oldProgress, Math.min(progress, 100));
            Thread.sleep(random.nextInt(2000));
            loadAppIcon();
            loadLabelsContent();

            System.out.println("Set innactivity listener...");
            oldProgress = progress;
            progress = oldProgress + random.nextInt(70);
            task.firePropertyChange("progress", oldProgress, Math.min(progress, 100));
            Thread.sleep(random.nextInt(2000));
            setInactivityListener();

            System.out.println("Finalizing...");
            oldProgress = progress;
            progress = oldProgress + random.nextInt(90);
            task.firePropertyChange("progress", oldProgress, Math.min(progress, 100));
            Thread.sleep(random.nextInt(2000));
            setJFrameOptions();
            System.out.println("Application intialized.");

        } catch (Exception e) {
            UILog.exceptionDialog(this, "", "", e
            );
            System.exit(-1);
        }
    }

    /**
     * Instanciate the main window and close the actual one
     */
    private void success() {
        MainFrame mainFrame = new MainFrame(this, true, this.user);
        mainFrame.setVisible(true);
        inactivityExitListener.stop();
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        versionLabel = new javax.swing.JLabel();
        authorLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        login_btn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        pwd_textfield = new javax.swing.JPasswordField();
        login_textfield = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btn_exit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));
        setForeground(new java.awt.Color(255, 255, 255));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        versionLabel.setForeground(new java.awt.Color(255, 255, 255));
        versionLabel.setText(".");

        authorLabel.setForeground(new java.awt.Color(255, 255, 255));
        authorLabel.setText(".");

        login_btn.setText("S'authentifier");
        login_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                login_btnActionPerformed(evt);
            }
        });
        login_btn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                login_btnKeyPressed(evt);
            }
        });

        jLabel1.setText("Login");

        jLabel2.setText("Mot de passe ");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setText("Authentification");

        pwd_textfield.setText("9");
        pwd_textfield.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pwd_textfieldMouseClicked(evt);
            }
        });
        pwd_textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pwd_textfieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pwd_textfieldKeyTyped(evt);
            }
        });

        login_textfield.setText("9");
        login_textfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                login_textfieldFocusGained(evt);
            }
        });
        login_textfield.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                login_textfieldMouseClicked(evt);
            }
        });
        login_textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                login_textfieldKeyTyped(evt);
            }
        });

        jLabel4.setText("Profil consultation (Login : 1 / Mot de passe : 1)");

        btn_exit.setText("Quitter");
        btn_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_exitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(login_btn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btn_exit))
                            .addComponent(login_textfield, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pwd_textfield, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(18, 18, 18)))
                .addContainerGap())
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(login_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(pwd_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_exit)
                    .addComponent(login_btn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGap(23, 23, 23))
        );

        jDesktopPane1.setLayer(versionLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(authorLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jPanel6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addComponent(versionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 352, Short.MAX_VALUE)
                        .addComponent(authorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(107, 107, 107))))
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 389, Short.MAX_VALUE)
                .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(authorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane1)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane1)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        int confirmed = JOptionPane.showConfirmDialog(null,
                "Voulez-vous quitter le programme ?", "Quitter ?",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == 0) {
            dispose();
        } else {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);//no
        }
    }//GEN-LAST:event_formWindowClosing

    private void login_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_login_btnActionPerformed
        //UILog.infoDialog("Event "+evt.toString());
        System.out.println("Event " + evt.toString());
        if (checkLoginAndPass(login_textfield.getText(), new String(pwd_textfield.getPassword()))) {
            //System.out.println("check auth "+checkLoginAndPass(login_textfield.getText(), new String(pwd_textfield.getPassword())));
            //this.dispose();
            //UILog.infoDialog("check auth "+checkLoginAndPass(login_textfield.getText(), new String(pwd_textfield.getPassword())));
            success();

        }
    }//GEN-LAST:event_login_btnActionPerformed

    private void pwd_textfieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pwd_textfieldKeyTyped
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (checkLoginAndPass(login_textfield.getText(), new String(pwd_textfield.getPassword()))) {
                success();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dispose();
        }
    }//GEN-LAST:event_pwd_textfieldKeyTyped

    private void pwd_textfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pwd_textfieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (checkLoginAndPass(login_textfield.getText(), new String(pwd_textfield.getPassword()))) {
                success();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dispose();
        }
    }//GEN-LAST:event_pwd_textfieldKeyPressed

    private void login_btnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_login_btnKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (checkLoginAndPass(login_textfield.getText(), new String(pwd_textfield.getPassword()))) {
                success();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dispose();
        }
    }//GEN-LAST:event_login_btnKeyPressed

    private void login_textfieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_login_textfieldMouseClicked
        login_textfield.selectAll();
    }//GEN-LAST:event_login_textfieldMouseClicked

    private void pwd_textfieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pwd_textfieldMouseClicked
        pwd_textfield.selectAll();
    }//GEN-LAST:event_pwd_textfieldMouseClicked

    private void login_textfieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_login_textfieldFocusGained
        pwd_textfield.selectAll();
    }//GEN-LAST:event_login_textfieldFocusGained

    private void login_textfieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_login_textfieldKeyTyped
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            pwd_textfield.requestFocus();
            pwd_textfield.selectAll();
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dispose();
        }
    }//GEN-LAST:event_login_textfieldKeyTyped

    private void btn_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_exitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btn_exitActionPerformed

    private boolean checkLoginAndPass(String login, String pwd) {
        Helper.startSession();

        Query query = Helper.sess.createQuery(HQLHelper.CHECK_LOGIN_PASS);
        query.setParameter("password", login);
        query.setParameter("login", pwd);

        Helper.sess.getTransaction().commit();
        List result = query.list();
        if (!result.isEmpty()) {
            Helper.startSession();
            this.user = (ManufactureUsers) result.get(0);

            user.setLoginTime(new Date());
            PackagingVars.context.setUser(user);
            PackagingVars.context.getUser().update(PackagingVars.context.getUser());
            GlobalVars.CONNECTED_USER = PackagingVars.context.getUser();
            try {
                GlobalVars.APP_HOSTNAME = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                Logger.getLogger(Mode3_S010_UserCodeScan.class.getName()).log(Level.SEVERE, null, ex);
            }
            String str = String.format(InfoMsg.APP_INFO0003[1],
                    user.getFirstName() + " " + user.getLastName()
                    + " / " + user.getLogin(), GlobalVars.APP_HOSTNAME,
                    GlobalMethods.getStrTimeStamp() + " Module : Configuration : ");

            UILog.info(str);

            return true;

        }
        UILog.severeDialog(this, ErrorMsg.APP_ERR0039);
        UILog.severe(ErrorMsg.APP_ERR0039[1]);
        login_textfield.requestFocus();
        login_textfield.selectAll();
        return false;

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel authorLabel;
    private javax.swing.JButton btn_exit;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JButton login_btn;
    private javax.swing.JTextField login_textfield;
    private javax.swing.JPasswordField pwd_textfield;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables

}
