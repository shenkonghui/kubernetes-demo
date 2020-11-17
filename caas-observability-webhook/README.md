# caas-admission-webhook

Admission Webhook

> 本项目用来给所有的k8s的admission webhook提供服务 

[官方链接](https://kubernetes.io/docs/reference/access-authn-authz/extensible-admission-controllers/):
https://kubernetes.io/docs/reference/access-authn-authz/extensible-admission-controllers/

### 项目结构

- src
  - main
    - java 源目录 
      - common 公共包
        - base 基本类
        - constant 常量类
        - enums 枚举类
        - exception 异常类
        - utils 工具类
      - config 配置类
      - controller 接口层
      - model 实体类
      - service 业务层
        - impl 业务实现类
    - resources 资源目录
      - application.yml 配置文件
  - test 测试
    - java 源目录
    - resource 资源目录

## 快速开始
