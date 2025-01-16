
## 项目介绍 🔝️

**😀 作为用户，您可以根据自己的需求浏览和选择合适的接口，并通过签到/邀请/购买积分来获取接口调用权限。您可以在线进行接口调试，快速验证接口的功能和效果。** 

**💻 作为开发者，我们提供了[客户端SDK](https://github.com/731016/api-platform-sdk)，通过[开发者凭证](todo)即可将 API 接口轻松集成到您的项目中，实现更高效的开发和调用。** 

**😽 您可以将自己的接口接入到 API 接口服务平台上，并发布给其他的用户使用。您可以管理自己的各个接口，以便更好地分析和优化接口性能。** 

**👏 我们还提供了详细的[开发者文档](todo)和技术支持，帮助您快速地接入和发布接口。**

**✅ 无论您是用户还是开发者，API 接口服务平台都致力于提供安全、稳定、高效的接口调用服务，帮助您实现更快速、便捷的开发和调用体验。**

## 网站导航 🧭

- **🔗 [API 接口服务平台](xxx)**
- **☘️ [API-frontend 前端](xxx)**
- **🐈 [API-platform 后端 ](xxx)**
- **🌈 [API-sdk 开发工具包](https://github.com/731016/api-platform-sdk)** 
-  **📖 [API-doc 开发者文档 ](xxx)**


## 目录结构 📑


| 目录                                                     | 描述               |
|--------------------------------------------------------| ------------------ |
| **🐈 [api-backend](./api-backend)**         | API-后端服务模块 |
| **🚌 [api-common](./api-common)**             | API-公共模块 |
| **🚀 [api-gateway](./api-gateway)**         | API-网关服务模块 |
| **🔗 [api-interface](./api-interface)**          | API-接口服务模块 |

## 项目流程 🗺️

![QiAPI 接口开放平台](https://img.qimuu.icu/typory/QiAPI%2520%25E6%258E%25A5%25E5%258F%25A3%25E5%25BC%2580%25E6%2594%25BE%25E5%25B9%25B3%25E5%258F%25B0.png)

## 

## ✨

## 欢迎使用API接口平台



## 快速启动 🚀

### 前端

环境要求：Node.js >= 16

安装依赖：

```bash
yarn or npm install
```

启动：

```bash
yarn dev or npm run dev
```

部署：

```bash
yarn build or npm run build
```

### 后端

执行 sql 目录下 api_platform.sql



## 项目介绍

提供API接口供开发者调用的平台，基于springBoot + react的全栈微服务项目

管理员可接入并发布接口、统计分析接口调用情况

用户可注册并开通接口调用权限、浏览、在线调试，还可使用SDK在代码中调用接口。



## 功能展示 

![image-20250116184120466](https://note-1259190304.cos.ap-chengdu.myqcloud.com/noteimage-20250116184120466.png)



![image-20250116184052027](https://note-1259190304.cos.ap-chengdu.myqcloud.com/noteimage-20250116184052027.png)

![image-20250116184159460](https://note-1259190304.cos.ap-chengdu.myqcloud.com/noteimage-20250116184159460.png)

## 技术选型

### 后端

+ springBoot
+ mysql
+ mybatis-Plus
+ API签名认证（http）
+ spring boot starter（SDK开发）
+ Dubbo分布式（RPC,nacos）
+ spring cloud gateway微服务网关
+ swagger + knife4j接口文档
+ Hutool、Apahce common utils、Gson等工具库