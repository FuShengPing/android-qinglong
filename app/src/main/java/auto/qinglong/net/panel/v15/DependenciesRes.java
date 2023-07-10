package auto.qinglong.net.panel.v15;

import java.util.List;

import auto.qinglong.net.panel.BaseRes;

/**
 * @author wsfsp4
 * @version 2023.07.08
 */
public class DependenciesRes extends BaseRes {
    private List<DependenceObject> data;

    public List<DependenceObject> getData() {
        return data;
    }

    public void setData(List<DependenceObject> data) {
        this.data = data;
    }

    public static class DependenceObject {
        private int id;
        private String name;
        private String createAt;
        private int status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
