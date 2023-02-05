# 开发者文档

## 1.版本

文档版本：***1.0***  
应用版本：***1.5+***

## 2.接口列表

### 变量远程导入

url：自定义  
method：***get***  
body:

```json
[
  {
    "name": "test",
    "value": "test",
    "remarks": "test"
  }
]
```

| 字段      | 类型     | 属性  | 说明   |
|---------|--------|-----|------|
| name    | string | 必填  | 变量名  |
| value   | string | 必填  | 变量值  |
| remarks | string | 选填  | 变量备注 |

测试地址：<https://gitee.com/wsfsp4/QingLong/raw/master/static/examples/envs.json>

### Web助手-规则远程导入

url：自定义  
method：***get***  
body:

```json
[
  {
    "name": "baidu",
    "url": "www.baidu.com",
    "envName": "baiduck",
    "target": "*",
    "main": "BAIDUID",
    "joinChar": ";"
  }
]
```

| 字段       | 类型     | 属性  | 说明                         |
|----------|--------|-----|----------------------------|
| name     | string | 必填  | 规则名,供用户识别用                 |
| url      | string | 必填  | 目标网页                       |
| envName  | string | 必填  | 变量名,同面板的环境变量，由字母、数字和下划线组成  |
| target   | string | 必填  | 提取值,即要从ck中提取的键值,具体支持格式参考主页 |
| main     | string | 必填  | ck主键,其值将作为面板环境变量的备注        |
| joinChar | string | 必填  | ck键拼接符                     |

>1. 匹配时，遍历启用的所有规则，匹配成功则停止；
>2. 如果规则中提取具体字段，只要一个字段不存在将匹配失败；
>3. 规则中的网址只和原始加载的网址相比较，尽管加载后用户点击页面跳转到其他页面；

测试地址：<https://gitee.com/wsfsp4/QingLong/raw/master/static/examples/rules.json>

后续会加入更多自定义接口，敬请期待！
