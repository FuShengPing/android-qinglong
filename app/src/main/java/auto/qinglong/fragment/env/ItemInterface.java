package auto.qinglong.fragment.env;

import auto.qinglong.api.object.Environment;

interface ItemInterface {
    void onEdit(Environment environment, int position);

    void onActions(Environment environment, int position);
}
