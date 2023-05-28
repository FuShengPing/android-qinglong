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
    public static String TAG = "CronUnit";
    private static final String PATTERN = "yyyy/M/d HH:mm:ss";
    private static final CronParser cronParser6;
    private static final CronParser cronParser5;
    private static final DateTimeFormatter dateTimeFormatter;

    static {
        cronParser5 = getParser(5);
        cronParser6 = getParser(6);
        dateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN);
    }

    /**
     * 判断定时规则是否合法
     *
     * @param expression 定时表达式
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
            return false;
        }
    }

    /**
     * 获取定时表达式的下次执行时间
     *
     * @param expression 定时表达式
     * @return 格式化时间或null
     */
    public static String nextExecutionTime(String expression) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }

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
            return nextExecutionTime.map(zonedDateTime -> zonedDateTime.format(dateTimeFormatter)).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取定时表达式的下次执行时间
     *
     * @param expression 定时表达式
     * @param def        默认值，解析错误时返回该值
     * @return 格式化时间或默认值
     */
    public static String nextExecutionTime(String expression, String def) {
        if (expression == null || expression.isEmpty()) {
            return def;
        }

        ExecutionTime executionTime;
        String[] values = expression.trim().split(" ");

        try {
            if (values.length == 6) {
                executionTime = ExecutionTime.forCron(cronParser6.parse(expression));
            } else if (values.length == 5) {
                executionTime = ExecutionTime.forCron(cronParser5.parse(expression));
            } else {
                return def;
            }
            Optional<ZonedDateTime> nextExecutionTime = executionTime.nextExecution(ZonedDateTime.now());
            return nextExecutionTime.isPresent() ? nextExecutionTime.get().format(dateTimeFormatter) : def;
        } catch (Exception e) {
            return def;
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
