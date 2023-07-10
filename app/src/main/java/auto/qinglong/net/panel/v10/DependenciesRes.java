package auto.qinglong.net.panel.v10;

import java.util.List;

import auto.qinglong.net.panel.BaseRes;

/**
 * @author wsfsp4
 * @version 2023.07.09
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
        private String _id;
        private String name;
        private long created;//13位时间戳
        private int status;

        public String getId() {
            return _id;
        }

        public void setId(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getCreated() {
            return created;
        }

        public void setCreated(long created) {
            this.created = created;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
