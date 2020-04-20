package __main__;

import helper.UIHelper;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Oussama
 */
public class SplashScreen extends javax.swing.JFrame implements PropertyChangeListener {

    private Task task;
    private static AuthFrame authFrame;

    public static void main(String[] args) {
        //Load the look and feel
        bootLookAndFeel();

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                SplashScreen p = new SplashScreen();

            }
        });
    }

    private void initLabels() {
        app_name.setText(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION);
        rights.setText(GlobalVars.ALL_RIGHTS_RESERVED);
        this.setTitle(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION);
        realised.setText(GlobalVars.APP_AUTHOR);
    }

    private void initTextArea() {
//        PrintStream printStream = new PrintStream(new JTextAreaOutputStream(taskOutput));        
//        System.setOut(printStream);
//        System.setErr(printStream);

        //Intialize jtextArea properties
        taskOutput.setEditable(false);
        taskOutput.setLineWrap(true);
        taskOutput.setWrapStyleWord(true);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        DefaultCaret caret = (DefaultCaret) taskOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        taskOutput.setCaret(caret);
    }

    private void initProgressBar() {
        progressBar.setValue(0);
        // Call setStringPainted now so that the progress bar height
        // stays the same whether or not the string is shown.
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
        // Instances of javax.swing.SwingWorker are not reusuable, so
        // we create new instances as needed.
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * Boot the Nimbus look and feel
     */
    private static void bootLookAndFeel() {
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
     * Creates new form ProgressBar
     */
    public SplashScreen() {
        initComponents();

        initLabels();

        initTextArea();

        initProgressBar();

        UIHelper.centerJFrame(this);

        this.setVisible(true);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        taskOutput = new javax.swing.JTextArea();
        progressBar = new javax.swing.JProgressBar();
        realised = new javax.swing.JLabel();
        rights = new javax.swing.JLabel();
        app_name = new javax.swing.JLabel();
        car_logo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ePack System");
        setBackground(new java.awt.Color(0, 0, 51));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(0, 0, 51));

        scrollPane.setBackground(new java.awt.Color(0, 0, 51));

        taskOutput.setEditable(false);
        taskOutput.setBackground(new java.awt.Color(0, 0, 51));
        taskOutput.setColumns(100);
        taskOutput.setForeground(new java.awt.Color(255, 255, 255));
        taskOutput.setRows(1);
        taskOutput.setTabSize(0);
        taskOutput.setAutoscrolls(false);
        taskOutput.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        taskOutput.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        taskOutput.setFocusable(false);
        taskOutput.setOpaque(false);
        taskOutput.setPreferredSize(new java.awt.Dimension(1102, 90000));
        taskOutput.setRequestFocusEnabled(false);
        scrollPane.setViewportView(taskOutput);

        realised.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        realised.setForeground(new java.awt.Color(255, 255, 255));
        realised.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        realised.setText("{AUTHOR}");

        rights.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rights.setForeground(new java.awt.Color(255, 255, 255));
        rights.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rights.setText("{RIGHTS}");

        app_name.setFont(new java.awt.Font("Dialog", 2, 24)); // NOI18N
        app_name.setForeground(new java.awt.Color(255, 255, 255));
        app_name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        app_name.setText("{APP NAME}");

        car_logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/__main__/images/splash-bg.png"))); // NOI18N
        car_logo.setPreferredSize(new java.awt.Dimension(32, 32));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(car_logo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(app_name, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(realised, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
            .addComponent(rights, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(app_name)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(car_logo, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(realised)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rights)
                .addGap(30, 30, 30)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel app_name;
    private javax.swing.JLabel car_logo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel realised;
    private javax.swing.JLabel rights;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextArea taskOutput;
    // End of variables declaration//GEN-END:variables

    class Task extends SwingWorker<Void, Void> {

        /*
     * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            // Initialize progress property.
            setProgress(0);
            // Sleep for at least one second to simulate "startup".
            try {

                System.out.println("Intialize application frame...");
                authFrame = new AuthFrame(this);
                //Thread.sleep(1000);
                authFrame.setVisible(true);
                setProgress(99);
                Thread.sleep(500);
                setProgress(100);

                if (getProgress() == 100) {
                    //Thread.sleep(200);
                    //Reset output stream;
                    GlobalMethods.resetOutputStram();
                    dispose();
                }

            } catch (InterruptedException ignore) {

            }

            return null;
        }

        /*
     * Executed in event dispatch thread
         */
        public void done() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setIndeterminate(false);
            progressBar.setValue(progress);
            taskOutput.append(String.format("Completed %d%% of task.\n", progress));

        }
    }

//    public void dispose() {
//        try {
//            
//            super.dispose();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}