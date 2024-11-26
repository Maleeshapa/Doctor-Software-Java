
package View;
import com.toedter.calendar.JDateChooser;
import javax.swing.JFrame;
import DB.SQLiteDatabase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;  // This import is required for using File class




public class excelMenu extends javax.swing.JFrame {
private JDateChooser dateChooser;
    public excelMenu() {
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);         
        
    }
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        startDate = new com.toedter.calendar.JDateChooser();
        endDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        downloadBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI Semilight", 1, 14)); // NOI18N
        jLabel1.setText("Select Date Range");

        jLabel2.setText("Start Date");

        jLabel3.setText("End Date");

        downloadBtn.setBackground(new java.awt.Color(204, 255, 204));
        downloadBtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        downloadBtn.setText("Download");
        downloadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
            .addGroup(layout.createSequentialGroup()
                .addGap(159, 159, 159)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(downloadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(149, 149, 149))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(startDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(46, 46, 46)
                .addComponent(downloadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void downloadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadBtnActionPerformed
        try {
        // Get dates from JDateChoosers
        Date startDateTime = startDate.getDate();
        Date endDateTime = endDate.getDate();

        if (startDateTime == null || endDateTime == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please select both start and end dates",
                    "Date Selection Required",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Format dates for SQL query
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = sdf.format(startDateTime);
        String endDateStr = sdf.format(endDateTime);

        // Create Excel Workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Patient History");

        // Create header row with bold style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Date and Time", "Patient Name", "Age",
                "Phone", "Address", "Illness", "Medicine", "Note", "Fee"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // Query database for records
        String sql = "SELECT * FROM history WHERE date(dateTime) BETWEEN ? AND ?";

        SQLiteDatabase.connect();
        PreparedStatement pstmt = SQLiteDatabase.connection.prepareStatement(sql);
        pstmt.setString(1, startDateStr);
        pstmt.setString(2, endDateStr);

        ResultSet rs = pstmt.executeQuery();

        int rowNum = 1;
        while (rs.next()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rs.getInt("id"));
            row.createCell(1).setCellValue(rs.getString("dateTime"));
            row.createCell(2).setCellValue(rs.getString("name"));
            row.createCell(3).setCellValue(rs.getString("age"));
            row.createCell(4).setCellValue(rs.getString("phone"));
            row.createCell(5).setCellValue(rs.getString("address"));
            row.createCell(6).setCellValue(rs.getString("illness"));
            row.createCell(7).setCellValue(rs.getString("medicine"));
            row.createCell(8).setCellValue(rs.getString("note"));
            row.createCell(9).setCellValue(rs.getInt("fee"));
        }

        // Auto-size all columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Define the target folder and ensure it exists
        String targetFolderPath = System.getProperty("user.home") + "\\Downloads\\Doctor Excels";
        File targetFolder = new File(targetFolderPath);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs(); // Create folder if it does not exist
        }

        // Generate the file name based on the current date
        String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String filePath = targetFolderPath + "\\history_" + timestamp + ".xlsx";

        // Write the workbook to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Excel file generated successfully at:\n" + filePath,
                    "Success",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }

        workbook.close();
        rs.close();
        pstmt.close();
        SQLiteDatabase.disconnect();

    } catch (Exception e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this,
                "Error generating Excel file: " + e.getMessage(),
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_downloadBtnActionPerformed

   
    public static void main(String args[]) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new excelMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton downloadBtn;
    private com.toedter.calendar.JDateChooser endDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private com.toedter.calendar.JDateChooser startDate;
    // End of variables declaration//GEN-END:variables
}
