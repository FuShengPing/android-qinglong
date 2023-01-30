package auto.qinglong.utils;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CronUnit {
    static String TAG = "CronUnit";
    private static final CronParser cronParser6;
    private static final CronParser cronParser5;
    private static final DateTimeFormatter dateTimeFormatter;

    static {
        cronParser5 = getParser(5);
        cronParser6 = getParser(6);
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");
    }

    /**
     * 判断定时规则是否合法
     *
     * @param expression 定时规则
     * @return true/false
     */
    public static boolean isValid(String expression) {
        try {
            if (TextUnit.isEmpty(expression)) {
                return false;
            }
            String[] values = expression.split(" ");
            if (values.length == 6) {
                cronParser6.parse(expression);
            } else if (values.length == 5) {
                cronParser5.parse(expression);
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            LogUnit.log(TAG, e.getMessage());
            return false;
        }
    }

    public static String nextExecutionTime(String expression) {
        ExecutionTime executionTime;
        String[] values = expression.trim().split(" ");
        try {
            if (values.length == 6) {
                executionTime = ExecutionTime.forCron(cronParser6.parse(expression));
            } else if (values.length == 5) {
                executionTime = ExecutionTime.forCron(cronParser5.parse(expression));
            } else {
                return null;
            }
            Optional<ZonedDateTime> nextExecutionTime = executionTime.nextExecution(ZonedDateTime.now());
            return nextExecutionTime.get().format(dateTimeFormatter);
        } catch (Exception e) {
            LogUnit.log(TAG, e.getMessage());
            return null;
        }
    }

    private static CronParser getParser(int len) {
        CronDefinition cronDefinition;
        if (len == 6) {
            cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING);
        } else {
            cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.CRON4J);
        }
        return new CronParser(cronDefinition);

    }
}
