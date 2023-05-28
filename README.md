# 青龙面板APP

## 1.软件介绍

本APP基于青龙面板***2.10.13***接口开发，支持面板大部分原生功能，同时提供拓展模块，帮助用户快捷管理。

## 2.使用环境

1. 安卓***8.0***及以上手机；
2. 面板版本***2.10.x***；

## 3.功能介绍

### 3.1.基础功能

提供定时任务、环境变量、配置文件、脚本管理、依赖管理、任务日志和系统设置功能。

1. 定时任务：支持增改删查、批量操作、日志查看、任务去重、跳转脚本、本地备份和本地导入；
2. 环境变量：支持增改删查、批量操作、变量排序、变量去重、快捷导入、远程导入、本地导入和本地备份；
3. 配置文件：支持查看和修改配置；
4. 脚本管理：支持查看、编辑和删除脚本；
5. 依赖管理：支持新建、删除、批量操作和日志查看；
6. 任务日志：支持查看日志列表；
7. 系统设置：支持常规设置和日志查看；

#### 操作提示
| 模块     | 单击标题 | 长按标题 | 长按内容 |
| -------- | -------- | -------- | -------- |
| 定时任务 | 查看日志 | 跳转脚本 | 编辑     |
| 环境变量 |          | 拖动排序 | 编辑     |
| 脚本管理 | 查看内容 | 操作栏   | 操作栏   |
| 依赖管理 | 查看日志 |          |          |
| 任务日志 | 查看内容 |          |          |

> (1) 变量快捷导入将从输入文本中提取'***export xx="xxxx"***'格式内容作为一个变量,支持同时提取多个变量；  
> (2) 变量去重将删除相同名称和值的变量；
> (3) 变量远程导入需要输入远程接口地址，具体查看[开发者文档](https://gitee.com/wsfsp4/QingLong/blob/master/developer.md)； 
> (4) 任务去重将删除相同命令的任务；  
> (5) 本地备份路径为外部存储**Android/data/auto.qinglong/files**文件夹下；  
> (6) 本地导入在外部存储**Android/data/auto.qinglong/files**文件夹下查找对应模块的文件，可自行参考备份文件的内容格式创建新文件，以json作为文件后缀即可；


### 3.2.拓展模块

#### 3.2.1 Web助手

提供网页ck提取和导入变量功能。

##### 3.2.1.1 ck提取

ck提取有常规提取和规则提取两种模式。

1. 常规提取：提取全部字段；
2. 规则提取：根据预先设置的规则，可提取特定的字段并排序、拼接；

##### 3.2.1.2 导入变量

支持一键提取ck并导入青龙面板环境变量，需要配置规则。

##### 3.2.1.3 规则配置

支持手动添加和远程导入，远程导入规则需要输入远程接口地址，具体查看[开发者文档](https://gitee.com/wsfsp4/QingLong/blob/master/developer.md)。各字段说明如下：

1. 规则名称：规则的名称，供用户识别用；
2. 环境变量：同面板的环境变量，由字母、数字和下划线组成；
3. 网址：原始加载的网址；
5. 主键：ck中的某个键，其值将作为面板环境变量的备注，匹配规则成功时若环境变量该值相同则进行更新操作，否则进行新建变量操作；
6. 连接符：拼接多个键值的符号，支持';'、'#'、'&'、'@'和'%'符号；
7. 目标键：要从ck中提取的键值,支持以下4种格式,假如网页ck为"a=1;b=2;c=3;"，连接符为";"：
   | 格式          | 说明                                               | 值             |
   | ------------- | -------------------------------------------------- | -------------- |
   | *             | 提取cookies全部键值                                | a=1;b=2;c=3;   |
   | *;a>>aa;b>>bb | 提取cookies全部键值，并将a键重命名为aa,b重命名为bb | aa=1;bb=2;c=3; |
   | a=;b>>bb=     | 提取cookies中a键值和b键值，并将b键重命名为bb       | a=1;bb=2;      |
   | a;b           | 提取cookies中a值和b值                              | 1;2;           |

> (1)匹配时，遍历启用的所有规则，匹配成功则停止；  
> (2)如果规则中提取具体字段，只要一个字段不存在将匹配失败；  
> (3)规则中的网址只和原始加载的网址相比较，尽管加载后用户点击页面跳转到其他页面；

## 4.界面预览

<img src="https://gitee.com/wsfsp4/QingLong/raw/master/static/imgs/preview_1.jpg" alt="模块导航" width="30%">  
<img src="https://gitee.com/wsfsp4/QingLong/raw/master/static/imgs/preview_2.jpg" alt="代码编辑" width="30%">  
<img src="https://gitee.com/wsfsp4/QingLong/raw/master/static/imgs/preview_3.jpg" alt="系统设置" width="30%">  

## 5.下载地址

[最新版本](https://gitee.com/wsfsp4/QingLong/releases/tag/V1.6)

[历史版本](https://gitee.com/wsfsp4/QingLong/releases)

## 6.开发者文档

为了增强应用拓展性，APP为用户提供自定义接口功能。你可以根据这份[开发者文档](https://gitee.com/wsfsp4/QingLong/blob/master/developer.md)
来开发接口，然后发布给其他用户使用。

## 7.意见反馈

APP还在开发，后续尝试加入更多拓展模块,提供更加强大的功能，有问题和功能需求可以提issue，有空就解决。

如果你有Android开发基础，并且有对该项目有自己的创意，欢迎加入！

## 8.支持项目

开发不易，如果APP对你有所帮助，可以考虑支持下项目开发，非常感谢!  
<img src="https://gitee.com/wsfsp4/QingLong/raw/master/static/imgs/donate_wx.png" alt="微信打赏" width="25%">
<img src="https://gitee.com/wsfsp4/QingLong/raw/master/static/imgs/donate_zfb.jpg" alt="支付宝打赏" width="25%">  

## 9.友情链接
1.[QingLong](https://github.com/whyour/qinglong)  
2.[ChatGPT-Web](https://newai-1308943175.cos-website.ap-shenzhen-fsi.myqcloud.com/)  
