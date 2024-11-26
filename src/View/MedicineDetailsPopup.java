// MedicineDetailsPopup.java
package View;

import javax.swing.*;
import java.awt.*;

public class MedicineDetailsPopup extends JDialog {
    private JTextArea medicineDetails;
    
    public MedicineDetailsPopup(JFrame parent, String medicine) {
        super(parent, "Medicine Details", true);
        
        // Create and configure the text area
        medicineDetails = new JTextArea(medicine);
        medicineDetails.setEditable(false);
        medicineDetails.setWrapStyleWord(true);
        medicineDetails.setLineWrap(true);
        medicineDetails.setFont(new Font("Tahoma", Font.PLAIN, 14));
        medicineDetails.setMargin(new Insets(10, 10, 10, 10));
        
        // Add text area to a scroll pane
        JScrollPane scrollPane = new JScrollPane(medicineDetails);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        
        // Add to dialog
        getContentPane().add(scrollPane);
        
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        // Configure dialog
        pack();
        setLocationRelativeTo(parent);
    }
}