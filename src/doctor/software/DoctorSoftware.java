
package doctor.software;
import View.First; 
import DB.SQLiteDatabase;

public class DoctorSoftware {

   
    public static void main(String[] args) {
        new First().setVisible(true); 
         SQLiteDatabase.connect();      
      
        System.out.println("Running the frontend logic...");
    }
    
}
