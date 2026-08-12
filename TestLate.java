import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TestLate {
    public static void main(String[] args) throws Exception {
        String checkInTimeStr = "08:30";
        Integer tolerance = 30;
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date scheduleTime = sdf.parse(checkInTimeStr);
        
        Calendar calNow = Calendar.getInstance();
        calNow.set(Calendar.HOUR_OF_DAY, 9);
        calNow.set(Calendar.MINUTE, 5);
        String nowStr = sdf.format(calNow.getTime());
        Date currentTime = sdf.parse(nowStr);
        
        Calendar calThreshold = Calendar.getInstance();
        calThreshold.setTime(scheduleTime);
        calThreshold.add(Calendar.MINUTE, tolerance);
        
        System.out.println("Schedule Time: " + scheduleTime);
        System.out.println("Threshold Time: " + calThreshold.getTime());
        System.out.println("Current Time: " + currentTime);
        System.out.println("Is Late: " + currentTime.after(calThreshold.getTime()));
    }
}
