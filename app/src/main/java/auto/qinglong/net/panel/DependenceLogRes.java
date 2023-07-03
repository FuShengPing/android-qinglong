package auto.qinglong.net.panel;

import java.util.List;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class DependenceLogRes extends BaseRes {
    private DependenceLogObject data;

    public DependenceLogObject getData() {
        return data;
    }

    public void setData(DependenceLogObject data) {
        this.data = data;
    }

    public static class DependenceLogObject {
        private List<String> log;

        public List<String> getLog() {
            return log;
        }

        public void setLog(List<String> log) {
            this.log = log;
        }
    }
}
