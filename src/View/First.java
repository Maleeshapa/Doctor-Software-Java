package View;

import DB.SQLiteDatabase;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Year;

public class First extends javax.swing.JFrame {

   
     public First() {
    initComponents();
    DefaultCategoryDataset dataset = createDataset();

    JFreeChart chart = createChart(dataset);

    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(300, 200)); // Smaller size

    jPanel2.setLayout(new java.awt.BorderLayout());
    jPanel2.add(chartPanel, java.awt.BorderLayout.CENTER);
    jPanel2.revalidate(); 
    refreshChart();
    calculateTodayIncome();
    calculateYesterdayIncome(); // Add this call
}

      
      private void calculateYesterdayIncome() {
    SQLiteDatabase.connect(); // Ensure database connection is established
    
    try {
        // Get yesterday's date
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // SQL query to sum yesterday's income
        String query = "SELECT COALESCE(SUM(fee), 0) as total_income FROM history " +
                     "WHERE date(dateTime) = date(?)";
        
        PreparedStatement pstmt = SQLiteDatabase.connection.prepareStatement(query);
        pstmt.setString(1, yesterdayStr);
        
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            double yesterdayIncome = rs.getDouble("total_income");
            // Format the number with thousands separator
            String formattedIncome = String.format("%,d", (int)yesterdayIncome);
            yesterdayIncomeText.setText("Rs. " + formattedIncome);
        } else {
            yesterdayIncomeText.setText("Rs. 0");
        }
        
        rs.close();
        pstmt.close();
        
    } catch (SQLException e) {
        System.err.println("Error calculating yesterday's income: " + e.getMessage());
        yesterdayIncomeText.setText("Rs. 0");
    }
}

      
      private void calculateTodayIncome() {
        SQLiteDatabase.connect(); // Ensure database connection is established
        
        try {
            // Get today's date
            LocalDate today = LocalDate.now();
            String todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // SQL query to sum today's income
            String query = "SELECT COALESCE(SUM(fee), 0) as total_income FROM history " +
                         "WHERE date(dateTime) = date(?)";
            
            PreparedStatement pstmt = SQLiteDatabase.connection.prepareStatement(query);
            pstmt.setString(1, todayStr);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double totalIncome = rs.getDouble("total_income");
                // Format the number with thousands separator
                String formattedIncome = String.format("%,d", (int)totalIncome);
                totalIncomeLabel.setText("Rs. " + formattedIncome);
            } else {
                totalIncomeLabel.setText("Rs. 0");
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("Error calculating today's income: " + e.getMessage());
            totalIncomeLabel.setText("Rs. 0");
        }
    } 
      
    public void refreshIncomeLabels() {
    calculateTodayIncome();
    calculateYesterdayIncome();
}

             

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SQLiteDatabase.connect();

        try {
            // Get current year
            int currentYear = Year.now().getValue();
            
            // SQL query to get monthly totals - using regular string concatenation
            String query = "SELECT strftime('%m', dateTime, '+5.5 hours') AS month, "
                    + "COALESCE(SUM(fee), 0) AS totalIncome "
                    + "FROM history "
                    + "WHERE strftime('%Y', dateTime, '+5.5 hours') = ? "
                    + "GROUP BY month "
                    + "ORDER BY month";
            
            PreparedStatement pstmt = SQLiteDatabase.connection.prepareStatement(query);
            pstmt.setString(1, String.valueOf(currentYear));
            
            ResultSet rs = pstmt.executeQuery();
            
            // Initialize all months with zero
            double[] monthlyIncome = new double[12];
            
            // Fill in the actual values from database
            while (rs.next()) {
                int monthIndex = Integer.parseInt(rs.getString("month")) - 1;
                monthlyIncome[monthIndex] = rs.getDouble("totalIncome");
            }
            
            // Add values to dataset with month names
            String[] monthNames = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            };
            
            for (int i = 0; i < 12; i++) {
                dataset.addValue(monthlyIncome[i], "Income", monthNames[i]);
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("Error creating dataset: " + e.getMessage());
            // In case of error, add zero values
            String[] monthNames = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            };
            for (String month : monthNames) {
                dataset.addValue(0, "Income", month);
            }
        }
        
        return dataset;
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Monthly Income " + Year.now().getValue(),  // Updated title with current year
                "Month",
                "Income (Rs)",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        
        // Customize the chart appearance
        chart.setBackgroundPaint(java.awt.Color.WHITE);
        
        org.jfree.chart.plot.CategoryPlot plot = (org.jfree.chart.plot.CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setRangeGridlinePaint(java.awt.Color.GRAY);
        plot.setDomainGridlinePaint(java.awt.Color.GRAY);
        
        // Customize the line
        org.jfree.chart.renderer.category.LineAndShapeRenderer renderer = 
            (org.jfree.chart.renderer.category.LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new java.awt.Color(59, 125, 221)); // Same blue color as React version
        renderer.setSeriesStroke(0, new java.awt.BasicStroke(2.0f));
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
        renderer.setDefaultShapesVisible(true);
        
        return chart;
    }

    // Add a method to refresh the chart
    public void refreshChart() {
        DefaultCategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(300, 200));
        
        jPanel2.removeAll();
        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(chartPanel, java.awt.BorderLayout.CENTER);
        jPanel2.revalidate();
        jPanel2.repaint();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelOne = new javax.swing.JLabel();
        totalIncomeLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        yesterdayIncomeLabel = new javax.swing.JLabel();
        yesterdayIncomeText = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        medibtn = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        Incomebtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        labelOne.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        labelOne.setText("Today Income");

        totalIncomeLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        totalIncomeLabel.setText("Rs. 0");

        jPanel2.setBackground(new java.awt.Color(255, 255, 204));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 710, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 358, Short.MAX_VALUE)
        );

        yesterdayIncomeLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        yesterdayIncomeLabel.setText("Yesterday Income");

        yesterdayIncomeText.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        yesterdayIncomeText.setText("Rs. 0");

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        medibtn.setBackground(new java.awt.Color(153, 51, 255));
        medibtn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        medibtn.setForeground(new java.awt.Color(255, 255, 255));
        medibtn.setText("Medicine ");
        medibtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                medibtnActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(153, 51, 255));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Add Data");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(153, 51, 255));
        jButton3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Dashboard");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(153, 51, 255));
        jButton4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("History");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        Incomebtn.setBackground(new java.awt.Color(153, 51, 255));
        Incomebtn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Incomebtn.setForeground(new java.awt.Color(255, 255, 255));
        Incomebtn.setText("Income");
        Incomebtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                IncomebtnMouseClicked(evt);
            }
        });
        Incomebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IncomebtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(medibtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Incomebtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(Incomebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(medibtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelOne)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(totalIncomeLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(yesterdayIncomeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(yesterdayIncomeText)
                                .addGap(87, 87, 87)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelOne)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(yesterdayIncomeText)
                            .addComponent(totalIncomeLabel)))
                    .addComponent(yesterdayIncomeLabel))
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void medibtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_medibtnActionPerformed
        SQLiteDatabase.connect();
        new Four().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_medibtnActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        SQLiteDatabase.connect();
        new Second().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked

    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        SQLiteDatabase.connect();
        new First().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Third third = new Third();
        third.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void IncomebtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_IncomebtnMouseClicked

    }//GEN-LAST:event_IncomebtnMouseClicked

    private void IncomebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IncomebtnActionPerformed
        FirstCalender FirstCalender = new FirstCalender();
        FirstCalender.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_IncomebtnActionPerformed

    public static void main(String args[]) {
       
       java.awt.EventQueue.invokeLater(() -> new First().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Incomebtn;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel labelOne;
    private javax.swing.JButton medibtn;
    private javax.swing.JLabel totalIncomeLabel;
    private javax.swing.JLabel yesterdayIncomeLabel;
    private javax.swing.JLabel yesterdayIncomeText;
    // End of variables declaration//GEN-END:variables
}
