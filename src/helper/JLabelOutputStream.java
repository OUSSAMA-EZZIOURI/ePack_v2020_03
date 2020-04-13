package helper;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JLabel;
 
 
/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 * @author www.codejava.net
 *
 */
public class JLabelOutputStream extends OutputStream {
    private JLabel jLabel;
     
    public JLabelOutputStream(JLabel jLabel) {
        this.jLabel = jLabel;
    }
     
    @Override
    public void write(int b) throws IOException {
        // redirects data to the label
        jLabel.setText(String.valueOf((char)b));        
    }
}