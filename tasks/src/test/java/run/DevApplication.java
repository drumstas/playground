package run;

import com.example.tasks.TasksApplication;
import org.springframework.boot.SpringApplication;

public class DevApplication {

    public static void main(String[] args) {
        var devApp = new SpringApplication(TasksApplication.class);
        devApp.setAdditionalProfiles("dev");
        devApp.run();
    }

}
