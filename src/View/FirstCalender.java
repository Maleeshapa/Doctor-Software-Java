package View;

import DB.SQLiteDatabase;
import com.toedter.calendar.JCalendar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableCellRenderer;

public class FirstCalender extends javax.swing.JFrame {

    private Map<String, Double> dailyIncomes = new HashMap<>();
    private JTable calendarTable;
    private JLabel monthLabel;
    private Calendar currentCalendar = Calendar.getInstance();

    public FirstCalender() {
        // Ensure database connection is established first
        SQLiteDatabase.connect();
        
        initComponents();
        setupCustomCalendar();
        loadIncomeData();
        updateCalendarDisplay();
    }

    private void setupCustomCalendar() {
        jPanel2.remove(jCalendar1);

        JPanel navigationPanel = new JPanel(new FlowLayout());
        JButton prevMonth = new JButton("<");
        JButton nextMonth = new JButton(">");
        monthLabel = new JLabel("", SwingConstants.CENTER);

        navigationPanel.add(prevMonth);
        navigationPanel.add(monthLabel);
        navigationPanel.add(nextMonth);

        calendarTable = new JTable(7, 7);
        calendarTable.setRowHeight(80); // Increase row height for better visibility
    calendarTable.setDefaultRenderer(Object.class, new CalendarCellRenderer());

        jPanel2.setLayout(new BorderLayout());
        jPanel2.add(navigationPanel, BorderLayout.NORTH);
        jPanel2.add(new JScrollPane(calendarTable), BorderLayout.CENTER);

        prevMonth.addActionListener(e -> {
            currentCalendar.add(Calendar.MONTH, -1);
            loadIncomeData();
            updateCalendarDisplay();
        });

        nextMonth.addActionListener(e -> {
            currentCalendar.add(Calendar.MONTH, 1);
            loadIncomeData();
            updateCalendarDisplay();
        });
    }

    private void loadIncomeData() {
    dailyIncomes.clear();
    Calendar cal = (Calendar) currentCalendar.clone();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    
    // Modified SQL query to properly group by date regardless of time
    String sql = "SELECT date(dateTime) as date, SUM(fee) as total_fee FROM history "
            + "WHERE strftime('%Y-%m', dateTime) = ? "
            + "GROUP BY date(dateTime)";

    String yearMonth = String.format("%d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1);
    
    System.out.println("Loading data for: " + yearMonth);

    try (PreparedStatement pstmt = SQLiteDatabase.connection.prepareStatement(sql)) {
        pstmt.setString(1, yearMonth);

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String date = rs.getString("date"); // Now getting just the date part
                double totalFee = rs.getDouble("total_fee");
                dailyIncomes.put(date, totalFee);
                System.out.println("Loaded income for " + date + ": Rs. " + totalFee);
            }
        }
        
        System.out.println("Total dates with income loaded: " + dailyIncomes.size());
        
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Error loading income data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

private void updateCalendarDisplay() {
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
    monthLabel.setText(monthFormat.format(currentCalendar.getTime()));

    // Set up header row
    String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (int i = 0; i < 7; i++) {
        calendarTable.setValueAt(weekdays[i], 0, i);
    }

    // Clear existing cells
    for (int i = 1; i < 7; i++) {
        for (int j = 0; j < 7; j++) {
            calendarTable.setValueAt("", i, j);
        }
    }

    Calendar cal = (Calendar) currentCalendar.clone();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
    int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Update cells with date and income
    for (int i = 0; i < daysInMonth; i++) {
        int row = (firstDayOfWeek + i) / 7 + 1;
        int col = (firstDayOfWeek + i) % 7;

        cal.set(Calendar.DAY_OF_MONTH, i + 1);
        String date = sdf.format(cal.getTime());
        Double income = dailyIncomes.get(date);

        String cellText = (i + 1) + (income != null ? String.format("\nRs. %.2f", income) : "");
        
        if (income != null) {
            System.out.println("Setting income for " + date + ": " + income); // Debug print
        }

        calendarTable.setValueAt(cellText, row, col);
    }

    calendarTable.repaint();
}

    private class CalendarCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        // Use JTextArea for multi-line display
        JTextArea textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setMargin(new Insets(5, 5, 5, 5));
        textArea.setOpaque(true);

        if (value != null) {
            textArea.setText(value.toString());
        }

        if (row == 0) {
            // Header row styling
            textArea.setBackground(new Color(153, 51, 255));
            textArea.setForeground(Color.WHITE);
            textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
        } else {
            // Data cell styling
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);

            if (value != null && value.toString().contains("Rs.")) {
                textArea.setBackground(new Color(240, 240, 255));
            }
        }

        // Add border to cell
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        return textArea;
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        medibtn = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        Incomebtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jCalendar1 = new com.toedter.calendar.JCalendar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
        Incomebtn.setActionCommand("Income");
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
                .addGap(54, 54, 54)
                .addComponent(Incomebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(medibtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jCalendar1, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                .addGap(26, 26, 26))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FirstCalender().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Incomebtn;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton medibtn;
    // End of variables declaration//GEN-END:variables
}
