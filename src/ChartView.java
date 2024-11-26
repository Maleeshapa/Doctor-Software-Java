/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;

/**
 *
 * @author Pc
 */
public class ChartView extends JFrame {

    /**
     * Creates new form ChartView
     */
    public ChartView() {
        // Set up JFrame properties
        setTitle("Monthly Income Line Chart");
        setSize(300, 200); // Smaller size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a dataset for the chart
        DefaultCategoryDataset dataset = createDataset();

        // Create a chart using the dataset
        JFreeChart chart = createChart(dataset);

        // Create a panel to hold the chart
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(300, 200)); // Smaller size

        // Add the chart panel to the JFrame
        setLayout(new java.awt.BorderLayout());
        add(chartPanel, java.awt.BorderLayout.CENTER);

        // Make the JFrame visible
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    // Method to create a dataset for the chart
   private DefaultCategoryDataset createDataset() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1000, "Income", "Jan");
        dataset.addValue(1500, "Income", "Feb");
        dataset.addValue(2000, "Income", "Mar");
        dataset.addValue(2500, "Income", "Apr");
        dataset.addValue(2200, "Income", "May");
        dataset.addValue(1800, "Income", "Jun");
        dataset.addValue(2100, "Income", "Jul");
        dataset.addValue(2300, "Income", "Aug");
        dataset.addValue(2600, "Income", "Sep");
        dataset.addValue(2800, "Income", "Oct");
        dataset.addValue(3000, "Income", "Nov");
        dataset.addValue(3200, "Income", "Dec");
        return dataset;
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        return ChartFactory.createBarChart(
               "Monthly Income",   // Chart title
                "Month",            // X-axis label
                "Income (Rs)",      // Y-axis label
                dataset,            // Dataset
                org.jfree.chart.plot.PlotOrientation.VERTICAL, // Line chart orientation
                true,               // Include legend
                true,               // Tooltips enabled
                false        // URLs disabled
        );
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChartView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChartView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChartView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChartView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
         new ChartView();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
