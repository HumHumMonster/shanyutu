import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class test {

    @Test
    public void test() throws ParseException {
        String tim = "* * * * * ?" ;
        CronExpression cronExpression = new CronExpression(tim);//导包import org.quartz.CronExpression;
        Date date = cronExpression.getTimeAfter(new Date());
        //将date转换为指定日期格式字符串
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dataFormat.format(date);
        System.out.println(dateString);
    }
}
