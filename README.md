# MyAppDemo

字节工程训练营客户端方向作业

## 核心功能
点击start开始

LoginPageDemo： 实现注册及登录功能，通过Room存储用户信息，登录后进入个人页面，可以修改昵称

FeedPageDemo： 展示已注册的用户信息，支持下拉刷新，滚动到底时加载更多，长按删卡功能

## 版本更新
- v1.0.0   (2025/11/30 )
  * 更新了feed流部分的整体架构，保证单一职责:
  
    adapter负责管理UI，拆分出DataManager负责管理数据，拆分出ViewHolder，支持后续插件式添加卡片样式（还没做注册方法）
  * 添加了下拉加载时的loading动画

## 运行环境
- Android Studio Otter | 2025.2.1
- minSdk 24，targetSdk 36
- Java 21