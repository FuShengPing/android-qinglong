package auto.panel.net.panel.v10;

import java.util.List;

import auto.panel.net.panel.BaseRes;

/**
 * @author wsfsp4
 * @version 2023.07.12
 */
public class EnvironmentsRes extends BaseRes {
    private List<EnvironmentObject> data;

    public List<EnvironmentObject> getData() {
        return data;
    }

    public void setData(List<EnvironmentObject> data) {
        this.data = data;
    }

    public static class EnvironmentObject {
        private String _id;
        private int status;
        private String name;
        private float position;
        private long created;
        private String remarks;
        private String timestamp;
        private String value;

        public String getId() {
            return _id;
        }

        public void setId(String _id) {
            this._id = _id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getPosition() {
            return position;
        }

        public void setPosition(float position) {
            this.position = position;
        }

        public long getCreated() {
            return created;
        }

        public void setCreated(long created) {
            this.created = created;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
