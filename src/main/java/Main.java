import com.thinkive.base.util.Console;
import com.thinkive.market.DataManager;
import com.thinkive.timerengine.TaskManager;

import org.apache.log4j.Logger;


public class Main {
    private final Logger logger = Logger.getLogger(this.getClass());


    public static void main(String[] args) {

        DataManager.update();

        TaskManager.start();

        Console.println("定时任务引擎服务已经正常启动......");

    }

}

 