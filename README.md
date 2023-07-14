# 定时精灵

## 软件介绍

本软件基于青龙面板***2.10.13***接口开发，支持面板大部分原生功能，同时提供拓展模块，帮助用户快捷管理。

## 使用环境

* 安卓版本：8.0+
* 面板版本：参考下表，选择合适软件版本

| 软件版本 | API版本 | 面板版本 | 备注                                                       |
| -------- | ------- | -------- | ---------------------------------------------------------- |
| 1.x.x    | 2.10.13 | 2.10.x   | 仅支持2.10.x                                               |
| 2.0.x    | 2.10.13 | all      | 部分版本可能出现部分功能不可用                             |
| 2.1.0+   | 2.10.13 | all      | 未发布，停止维护2.10.x版本，部分版本可能出现部分功能不可用 |

## 功能介绍

### 基础功能

提供定时任务、环境变量、配置文件、脚本管理、依赖管理、任务日志和系统设置功能。

* 定时任务：支持增改删查、批量操作、查看日志、查看脚本、任务去重、本地备份和本地导入；
* 环境变量：支持增改删查、批量操作、变量排序、变量去重、快捷导入、本地备份和本地导入；
* 配置文件：支持查看和修改配置；
* 依赖管理：支持新建、删除、批量操作和查看日志；
* 脚本管理：支持查看、编辑和删除脚本；
* 任务日志：支持查看日志文件列表；
* 系统设置：支持常规设置和登录日志查看；

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
> (4) 任务去重将删除相同命令的任务；  
> (5) 本地备份路径为外部存储**Android/data/auto.qinglong/files**文件夹下；  
> (6) 本地导入在外部存储**Android/data/auto.qinglong/files**文件夹下查找对应模块的文件，可自行参考备份文件的内容格式创建新文件，以json作为文件后缀即可；


### 拓展模块
拓展模块后续将逐步开放。

#### Web助手

#### Docker助手

#### LanProxy


## 界面预览

<img src="https://gitee.com/wsfsp4/QingLong/raw/master/static/imgs/preview_1.jpg" alt="模块导航" width="30%">  
<img src="https://gitee.com/wsfsp4/QingLong/raw/master/static/imgs/preview_2.jpg" alt="代码编辑" width="30%">  
<img src="https://gitee.com/wsfsp4/QingLong/raw/master/static/imgs/preview_3.jpg" alt="系统设置" width="30%">  

## 下载地址

[最新版本](https://gitee.com/wsfsp4/QingLong/releases/tag/V1.6)

[历史版本](https://gitee.com/wsfsp4/QingLong/releases)


## 交流反馈

APP还在开发，后续尝试加入更多拓展模块,提供更加强大的功能，有问题和功能需求可以提issue，有空就解决。

如果你有Android开发基础，并且有对该项目有自己的创意，欢迎加入！

## 支持项目

开发不易，如果APP对你有所帮助，可以考虑支持下项目开发，非常感谢!  
<img src="https://gitee.com/wsfsp4/QingLong/raw/master/static/imgs/donate_wx.png" alt="微信打赏" width="25%">
<img src="https://gitee.com/wsfsp4/QingLong/raw/master/static/imgs/donate_zfb.jpg" alt="支付宝打赏" width="25%">  

## 友情链接
* [qinglong](https://github.com/whyour/qinglong)  
* [NewAI](https://newai-1308943175.cos-website.ap-shenzhen-fsi.myqcloud.com/)  
