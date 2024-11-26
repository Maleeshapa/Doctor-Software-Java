package View;

import DB.SQLiteDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import View.excelMenu;
import javax.swing.JFrame;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Third extends javax.swing.JFrame {

    private TableRowSorter<DefaultTableModel> sorter;
    private DefaultTableModel model;

    public Third() {
        initComponents();
        SQLiteDatabase.connect();
        setupTableAndSearch();
        loadHistoryData();

    }

    private void setupTableAndSearch() {
        model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"id", "Date and Time", "Patient Name", "Age", "Phone", "Address", "illness", "Medicine", "Note", "Fee"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Make ID column non-editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Integer.class; // Age column
                }
                if (columnIndex == 9) {
                    return Double.class; // Fee column
                }
                return String.class;
            }
        };

        jTable1.setModel(model);
        sorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(sorter);

        // Add table cell edit listener
        jTable1.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int modelRow = e.getFirstRow();
                int column = e.getColumn();

                if (modelRow >= 0 && column >= 0) {
                    // Convert model row to view row
                    int viewRow = jTable1.convertRowIndexToModel(modelRow);
                    updateDatabase(viewRow);
                }
            }
        });

        // Add document listener to search field
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });
    }

    private void updateDatabase(int row) {
    try {
        // Ensure we're connected
        if (!SQLiteDatabase.isConnected()) {
            SQLiteDatabase.connect();
        }

        String query = "UPDATE history SET dateTime=?, name=?, age=?, phone=?, address=?, "
                + "illness=?, medicine=?, note=?, fee=? WHERE id=?";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (PreparedStatement pstmt = SQLiteDatabase.connection.prepareStatement(query)) {
            int id = (Integer) model.getValueAt(row, 0);
            String formattedDateTime = (String) model.getValueAt(row, 1);
            LocalDateTime dateTime = LocalDateTime.parse(formattedDateTime, formatter);

            pstmt.setString(1, dateTime.toString()); // Store in standard format
            pstmt.setString(2, (String) model.getValueAt(row, 2));
            pstmt.setInt(3, (Integer) model.getValueAt(row, 3));
            pstmt.setString(4, (String) model.getValueAt(row, 4));
            pstmt.setString(5, (String) model.getValueAt(row, 5));
            pstmt.setString(6, (String) model.getValueAt(row, 6));
            pstmt.setString(7, (String) model.getValueAt(row, 7));
            pstmt.setString(8, (String) model.getValueAt(row, 8));
            pstmt.setDouble(9, (Double) model.getValueAt(row, 9));
            pstmt.setInt(10, id);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                        "Record updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Error updating record: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        loadHistoryData(); // Reload the data to refresh the table
    }
}


    private void filterTable() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null); // Show all rows when search field is empty
        } else {
            // Create a case-insensitive regex pattern that matches any column
            Pattern pattern = Pattern.compile(Pattern.quote(searchText), Pattern.CASE_INSENSITIVE);
            RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (entry.getValue(i) != null) {
                            String value = entry.getValue(i).toString();
                            if (pattern.matcher(value).find()) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            };
            sorter.setRowFilter(rf);
        }
    }

    private void loadHistoryData() {
    try {
        // Ensure we're connected
        if (!SQLiteDatabase.isConnected()) {
            SQLiteDatabase.connect();
        }

        model.setRowCount(0); // Clear existing rows

        String query = "SELECT * FROM history";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (PreparedStatement pstmt = SQLiteDatabase.connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LocalDateTime dateTime = LocalDateTime.parse(rs.getString("dateTime"));
                Object[] row = {
                    rs.getInt("id"),
                    dateTime.format(formatter),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("illness"),
                    rs.getString("medicine"),
                    rs.getString("note"),
                    rs.getDouble("fee")
                };
                model.addRow(row);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Error loading data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}


    @Override
    public void dispose() {
        SQLiteDatabase.disconnect(); // Properly disconnect when closing the window
        super.dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        searchField = new javax.swing.JTextField();
        searchbtn = new javax.swing.JButton();
        excelBtn = new javax.swing.JButton();
        deletebtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        medibtn = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        Incomebtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("History");

        jTable1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "Date and Time", "Patient Name", "Age", "Phone", "Address", "illness", "Medicine", "Note", "Fee"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        searchbtn.setText("Search");

        excelBtn.setBackground(new java.awt.Color(102, 255, 102));
        excelBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        excelBtn.setText("Generate Excel");
        excelBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                excelBtnMouseClicked(evt);
            }
        });
        excelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excelBtnActionPerformed(evt);
            }
        });

        deletebtn.setBackground(new java.awt.Color(255, 51, 51));
        deletebtn.setText("Delete");
        deletebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletebtnActionPerformed(evt);
            }
        });

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(deletebtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(excelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1133, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchbtn)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(excelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deletebtn))
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed

    }//GEN-LAST:event_searchFieldActionPerformed

    private void excelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excelBtnActionPerformed
        try {
            excelMenu excelMenu = new excelMenu();
            excelMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            excelMenu.setLocationRelativeTo(null);
            excelMenu.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace(); // This will print any exception to the console
            JOptionPane.showMessageDialog(this, "Error opening Excel Menu: " + e.getMessage());
        }
    }//GEN-LAST:event_excelBtnActionPerformed

    private void excelBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_excelBtnMouseClicked
        try {
            excelMenu excelMenu = new excelMenu();
            excelMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            excelMenu.setLocationRelativeTo(null);
            excelMenu.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace(); // This will print any exception to the console
            JOptionPane.showMessageDialog(this, "Error opening Excel Menu: " + e.getMessage());
        }
    }//GEN-LAST:event_excelBtnMouseClicked

    private void deleteSelectedRecord() {
    int selectedRowIndex = jTable1.getSelectedRow();
    if (selectedRowIndex == -1) {
        JOptionPane.showMessageDialog(this, "Please select a record to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Convert view index to model index in case table is sorted or filtered
    int modelRow = jTable1.convertRowIndexToModel(selectedRowIndex);
    
    // Get the ID from the first column
    Integer id = (Integer) model.getValueAt(modelRow, 0);

    // Confirm deletion
    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this record?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            // Ensure we're connected
            if (!SQLiteDatabase.isConnected()) {
                SQLiteDatabase.connect();
            }

            String query = "DELETE FROM history WHERE id = ?";
            try (PreparedStatement pstmt = SQLiteDatabase.connection.prepareStatement(query)) {
                pstmt.setInt(1, id);
                int result = pstmt.executeUpdate();

                if (result > 0) {
                    // Remove the row from the table model
                    model.removeRow(modelRow);
                    JOptionPane.showMessageDialog(this,
                            "Record deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete record.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting record: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    private void deletebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletebtnActionPerformed
        deleteSelectedRecord();
    }//GEN-LAST:event_deletebtnActionPerformed

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
                new Third().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Incomebtn;
    private javax.swing.JButton deletebtn;
    private javax.swing.JButton excelBtn;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton medibtn;
    private javax.swing.JTextField searchField;
    private javax.swing.JButton searchbtn;
    // End of variables declaration//GEN-END:variables
}
