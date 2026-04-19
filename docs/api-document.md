# 理发店SaaS管理系统 - API接口文档

## 一、项目概述

本项目是一个理发店SaaS管理系统，为理发店提供会员管理、消费记录、理发师管理、发型管理等一站式解决方案。系统采用Spring Boot框架开发，支持多商家入驻模式，包含完整的商家审核流程和JWT认证机制。

## 二、主要功能模块

本系统包含以下 **5个主要功能模块**：

1. **商家管理** - 商家注册、登录、审核、上下线管理
2. **理发师管理** - 理发师增删改查、级别设置、状态管理
3. **会员管理** - 会员录入、充值、等级管理、交易记录
4. **发型管理** - 发型类型和发型选项的CRUD管理
5. **消费管理** - 消费记录创建、理发师匹配、消费统计

---

## 三、功能流程与接口详情

### 3.1 商家管理模块

#### 3.1.1 功能流程

商家管理模块负责整个平台的商家入驻和管理，主要流程如下：

1. **商家注册流程**：
   - 商家提交注册申请（用户名、密码、店铺名称、联系电话等）
   - 系统创建商家记录，状态设为`PENDING`（待审核）
   - 等待管理员审核

2. **商家登录流程**：
   - 商家输入用户名和密码
   - 系统验证账号密码
   - 检查商家状态（需审核通过且在线才能登录）
   - 生成JWT Token返回

3. **商家审核流程**：
   - 管理员查看待审核商家列表
   - 审核通过：状态变为`APPROVED`，记录审核时间
   - 审核拒绝：状态变为`REJECTED`

4. **商家上下线流程**：
   - 上线：将状态设为`ONLINE`，商家可正常营业
   - 下线：将状态设为`OFFLINE`，商家暂停营业

#### 3.1.2 调用的接口

| 序号 | 接口路径 | 请求方法 | 接口功能 |
|------|----------|----------|----------|
| 1 | `/api/merchants/register` | POST | 商家注册申请 |
| 2 | `/api/merchants/login` | POST | 商家登录 |
| 3 | `/api/merchants/pending` | GET | 获取待审核商家列表 |
| 4 | `/api/merchants/{id}/approve` | POST | 审核通过 |
| 5 | `/api/merchants/{id}/reject` | POST | 审核拒绝 |
| 6 | `/api/merchants/{id}/online` | POST | 商家上线 |
| 7 | `/api/merchants/{id}/offline` | POST | 商家下线 |
| 8 | `/api/merchants/{id}` | GET | 获取商家详情 |
| 9 | `/api/merchants` | GET | 获取所有商家列表 |

---

### 3.2 理发师管理模块

#### 3.2.1 功能流程

理发师管理模块为商家提供员工管理功能：

1. **创建理发师**：
   - 商家添加新理发师（姓名、手机号、级别、提成比例）
   - 默认级别为`JUNIOR`，默认提成比例为30%

2. **理发师级别**：
   - JUNIOR（初级）
   - INTERMEDIATE（中级）
   - SENIOR（高级）
   - MASTER（资深）
   - CHIEF（首席）

3. **理发师状态管理**：
   - 启用/禁用理发师
   - 禁用后不可接待顾客

#### 3.2.2 调用的接口

| 序号 | 接口路径 | 请求方法 | 接口功能 |
|------|----------|----------|----------|
| 1 | `/api/barbers` | POST | 创建理发师 |
| 2 | `/api/barbers` | GET | 获取理发师列表 |
| 3 | `/api/barbers/active` | GET | 获取活跃理发师列表 |
| 4 | `/api/barbers/{id}` | GET | 获取理发师详情 |
| 5 | `/api/barbers/{id}` | PUT | 更新理发师信息 |
| 6 | `/api/barbers/{id}/status` | PUT | 更新理发师状态 |
| 7 | `/api/barbers/{id}` | DELETE | 删除理发师 |

---

### 3.3 会员管理模块

#### 3.3.1 功能流程

会员管理模块为商家提供会员管理和营销功能：

1. **会员创建**：
   - 录入新会员（姓名、手机号）
   - 同一商家下手机号唯一

2. **会员充值**：
   - 为会员账户充值
   - 记录充值交易流水

3. **会员等级**：
   - 系统根据累计消费金额自动升级等级
   - 等级规则：
     - NORMAL（普通）：消费 < 500元，无折扣
     - SILVER（白银）：消费 >= 500元，95折
     - GOLD（黄金）：消费 >= 2000元，9折
     - PLATINUM（铂金）：消费 >= 5000元，85折
     - DIAMOND（钻石）：消费 >= 10000元，8折

4. **交易记录**：
   - 查询会员的所有充值和消费记录

#### 3.3.2 调用的接口

| 序号 | 接口路径 | 请求方法 | 接口功能 |
|------|----------|----------|----------|
| 1 | `/api/members` | POST | 创建会员 |
| 2 | `/api/members` | GET | 获取会员列表 |
| 3 | `/api/members/{id}` | GET | 获取会员详情 |
| 4 | `/api/members/phone/{phone}` | GET | 根据手机号查询会员 |
| 5 | `/api/members/recharge` | POST | 会员充值 |
| 6 | `/api/members/transactions` | GET | 获取交易记录 |
| 7 | `/api/members/{id}/level` | PUT | 更新会员等级 |

---

### 3.4 发型管理模块

#### 3.4.1 功能流程

发型管理模块为商家提供发型项目维护功能：

1. **发型类型管理**：
   - 创建发型类型（名称、描述、图片）
   - 例如：剪发、烫发、染发、造型等
   - 支持启用/禁用状态

2. **发型选项管理**：
   - 创建发型选项（名称、分类、描述）
   - 例如：短发、长发、卷发、直发等
   - 按分类组织选项

#### 3.4.2 调用的接口

| 序号 | 接口路径 | 请求方法 | 接口功能 |
|------|----------|----------|----------|
| 1 | `/api/hairstyles/types` | POST | 创建发型类型 |
| 2 | `/api/hairstyles/types` | GET | 获取发型类型列表 |
| 3 | `/api/hairstyles/types/active` | GET | 获取活跃发型类型列表 |
| 4 | `/api/hairstyles/types/{id}` | GET | 获取发型类型详情 |
| 5 | `/api/hairstyles/types/{id}` | PUT | 更新发型类型 |
| 6 | `/api/hairstyles/types/{id}` | DELETE | 删除发型类型 |
| 7 | `/api/hairstyles/types/{id}/toggle` | PUT | 切换发型类型状态 |
| 8 | `/api/hairstyles/options` | POST | 创建发型选项 |
| 9 | `/api/hairstyles/options` | GET | 获取发型选项列表 |
| 10 | `/api/hairstyles/options/{id}` | GET | 获取发型选项详情 |
| 11 | `/api/hairstyles/options/{id}` | PUT | 更新发型选项 |
| 12 | `/api/hairstyles/options/{id}` | DELETE | 删除发型选项 |

---

### 3.5 消费管理模块

#### 3.5.1 功能流程

消费管理模块是系统的核心业务模块：

1. **创建消费记录**：
   - 选择消费会员
   - 选择服务理发师
   - 选择发型类型和选项
   - 输入原价
   - 选择支付方式（余额、现金、银行卡）
   - 系统计算折扣价（根据会员等级）
   - 如果使用余额支付，扣减会员余额
   - 计算理发师提成
   - 自动更新会员等级
   - 记录消费流水

2. **理发师匹配**：
   - 根据选择的发型类型和选项
   - 返回匹配度最高的理发师列表
   - 匹配规则：理发师级别越高，得分越高

#### 3.5.2 调用的接口

| 序号 | 接口路径 | 请求方法 | 接口功能 |
|------|----------|----------|----------|
| 1 | `/api/consumptions` | POST | 创建消费记录 |
| 2 | `/api/consumptions` | GET | 获取消费记录列表 |
| 3 | `/api/consumptions/barber/{barberId}` | GET | 获取理发师的消费记录 |
| 4 | `/api/consumptions/match-barber` | POST | 匹配理发师 |

---

## 四、接口详细描述

### 4.1 商家管理接口

#### 4.1.1 商家注册申请

- **接口路径**：`POST /api/merchants/register`
- **接口描述**：商家提交注册申请，等待管理员审核

**请求参数 (Request Body - MerchantRegisterRequest)**：

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| username | String | 是 | 用户名，长度3-50字符 |
| password | String | 是 | 密码，长度6-100字符 |
| shopName | String | 是 | 店铺名称 |
| address | String | 否 | 店铺地址 |
| phone | String | 是 | 联系电话 |
| contactPerson | String | 否 | 联系人 |
| businessLicense | String | 否 | 营业执照 |

**返回参数 (ApiResponse<MerchantResponse>)**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| code | int | 状态码（200表示成功） |
| message | String | 响应消息 |
| data | MerchantResponse | 商家响应对象 |

**MerchantResponse 包含字段**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 商家ID |
| username | String | 用户名 |
| shopName | String | 店铺名称 |
| address | String | 店铺地址 |
| phone | String | 联系电话 |
| contactPerson | String | 联系人 |
| businessLicense | String | 营业执照 |
| status | MerchantStatus | 商家状态（PENDING/APPROVED/REJECTED/OFFLINE/ONLINE） |
| balance | BigDecimal | 账户余额 |
| createdAt | LocalDateTime | 创建时间 |
| approvedAt | LocalDateTime | 审核通过时间 |

---

#### 4.1.2 商家登录

- **接口路径**：`POST /api/merchants/login`
- **接口描述**：商家登录获取JWT Token

**请求参数 (Request Body - MerchantLoginRequest)**：

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**返回参数 (ApiResponse<LoginResponse>)**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| code | int | 状态码 |
| message | String | 响应消息 |
| data | LoginResponse | 登录响应对象 |

**LoginResponse 包含字段**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| token | String | JWT Token |
| merchant | MerchantResponse | 商家信息 |

---

#### 4.1.3 获取待审核商家列表

- **接口路径**：`GET /api/merchants/pending`
- **接口描述**：管理员获取待审核的商家列表

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码，从0开始 |
| size | int | 否 | 10 | 每页数量 |

**返回参数 (ApiResponse<Page<MerchantResponse>>)**：返回分页的待审核商家列表

---

#### 4.1.4 审核通过

- **接口路径**：`POST /api/merchants/{id}/approve`
- **接口描述**：管理员审核通过商家申请

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 商家ID |

**返回参数**：返回更新后的商家信息

---

#### 4.1.5 审核拒绝

- **接口路径**：`POST /api/merchants/{id}/reject`
- **接口描述**：管理员拒绝商家申请

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 商家ID |

**返回参数**：返回更新后的商家信息

---

#### 4.1.6 商家上线

- **接口路径**：`POST /api/merchants/{id}/online`
- **接口描述**：将商家状态设为上线

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 商家ID |

**返回参数**：返回更新后的商家信息

---

#### 4.1.7 商家下线

- **接口路径**：`POST /api/merchants/{id}/offline`
- **接口描述**：将商家状态设为下线

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 商家ID |

**返回参数**：返回更新后的商家信息

---

#### 4.1.8 获取商家详情

- **接口路径**：`GET /api/merchants/{id}`
- **接口描述**：根据ID获取商家详情

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 商家ID |

**返回参数**：返回商家详细信息

---

#### 4.1.9 获取所有商家列表

- **接口路径**：`GET /api/merchants`
- **接口描述**：管理员获取所有商家列表

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**返回参数**：返回分页的所有商家列表

---

### 4.2 理发师管理接口

#### 4.2.1 创建理发师

- **接口路径**：`POST /api/barbers`
- **接口描述**：添加新理发师

**请求参数 (Request Body - BarberRequest)**：

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | String | 是 | 理发师姓名 |
| phone | String | 否 | 理发师手机号 |
| level | BarberLevel | 否 | 理发师级别（默认JUNIOR） |
| commissionRate | BigDecimal | 否 | 提成比例（默认0.3，即30%） |

**BarberLevel 枚举值**：JUNIOR, INTERMEDIATE, SENIOR, MASTER, CHIEF

**返回参数 (ApiResponse<BarberResponse>)**：

**BarberResponse 包含字段**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 理发师ID |
| merchantId | Long | 所属商家ID |
| name | String | 姓名 |
| phone | String | 手机号 |
| level | BarberLevel | 级别 |
| commissionRate | BigDecimal | 提成比例 |
| active | Boolean | 是否活跃 |
| createdAt | LocalDateTime | 创建时间 |

---

#### 4.2.2 获取理发师列表

- **接口路径**：`GET /api/barbers`
- **接口描述**：分页获取当前商家的理发师列表

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**返回参数**：返回分页的理发师列表

---

#### 4.2.3 获取活跃理发师列表

- **接口路径**：`GET /api/barbers/active`
- **接口描述**：获取当前商家所有活跃的理发师

**返回参数**：返回所有活跃理发师的列表（非分页）

---

#### 4.2.4 获取理发师详情

- **接口路径**：`GET /api/barbers/{id}`
- **接口描述**：根据ID获取理发师详情

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 理发师ID |

**返回参数**：返回理发师详细信息

---

#### 4.2.5 更新理发师

- **接口路径**：`PUT /api/barbers/{id}`
- **接口描述**：更新理发师信息

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 理发师ID |

**请求参数**：同创建理发师

**返回参数**：返回更新后的理发师信息

---

#### 4.2.6 更新理发师状态

- **接口路径**：`PUT /api/barbers/{id}/status`
- **接口描述**：启用或禁用理发师

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 理发师ID |

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| active | Boolean | 是 | true启用，false禁用 |

**返回参数**：返回更新后的理发师信息

---

#### 4.2.7 删除理发师

- **接口路径**：`DELETE /api/barbers/{id}`
- **接口描述**：删除理发师

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 理发师ID |

**返回参数**：返回成功消息

---

### 4.3 会员管理接口

#### 4.3.1 创建会员

- **接口路径**：`POST /api/members`
- **接口描述**：录入新会员

**请求参数 (Request Body - MemberRequest)**：

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | String | 是 | 会员姓名 |
| phone | String | 是 | 会员手机号 |

**返回参数 (ApiResponse<MemberResponse>)**：

**MemberResponse 包含字段**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 会员ID |
| merchantId | Long | 所属商家ID |
| name | String | 姓名 |
| phone | String | 手机号 |
| level | MemberLevel | 会员等级 |
| discount | Double | 折扣率 |
| balance | BigDecimal | 账户余额 |
| totalConsumption | BigDecimal | 累计消费金额 |
| createdAt | LocalDateTime | 创建时间 |

**MemberLevel 枚举值及折扣**：

| 等级 | 折扣 | 升级条件 |
|------|------|----------|
| NORMAL | 1.0 | 消费 < 500元 |
| SILVER | 0.95 | 消费 >= 500元 |
| GOLD | 0.9 | 消费 >= 2000元 |
| PLATINUM | 0.85 | 消费 >= 5000元 |
| DIAMOND | 0.8 | 消费 >= 10000元 |

---

#### 4.3.2 获取会员列表

- **接口路径**：`GET /api/members`
- **接口描述**：分页获取当前商家的会员列表

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**返回参数**：返回分页的会员列表

---

#### 4.3.3 获取会员详情

- **接口路径**：`GET /api/members/{id}`
- **接口描述**：根据ID获取会员详情

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 会员ID |

**返回参数**：返回会员详细信息

---

#### 4.3.4 根据手机号查询会员

- **接口路径**：`GET /api/members/phone/{phone}`
- **接口描述**：根据手机号查询会员信息

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| phone | String | 手机号 |

**返回参数**：返回会员信息

---

#### 4.3.5 会员充值

- **接口路径**：`POST /api/members/recharge`
- **接口描述**：为会员充值

**请求参数 (Request Body - RechargeRequest)**：

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| memberId | Long | 是 | 会员ID |
| amount | BigDecimal | 是 | 充值金额（必须大于0） |
| remark | String | 否 | 备注 |

**返回参数 (ApiResponse<TransactionResponse>)**：

**TransactionResponse 包含字段**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 交易ID |
| merchantId | Long | 商家ID |
| memberId | Long | 会员ID |
| type | TransactionType | 交易类型（RECHARGE/CONSUME/REFUND） |
| amount | BigDecimal | 交易金额 |
| balanceAfter | BigDecimal | 交易后余额 |
| remark | String | 备注 |
| createdAt | LocalDateTime | 创建时间 |

---

#### 4.3.6 获取交易记录

- **接口路径**：`GET /api/members/transactions`
- **接口描述**：获取会员交易记录

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| memberId | Long | 否 | - | 会员ID（不传则查询所有） |
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**返回参数**：返回分页的交易记录列表

---

#### 4.3.7 更新会员等级

- **接口路径**：`PUT /api/members/{id}/level`
- **接口描述**：手动更新会员等级

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 会员ID |

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| level | MemberLevel | 是 | 会员等级 |

**返回参数**：返回更新后的会员信息

---

### 4.4 发型管理接口

#### 4.4.1 创建发型类型

- **接口路径**：`POST /api/hairstyles/types`
- **接口描述**：添加新的发型类型

**请求参数 (Request Body - HairstyleTypeRequest)**：

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | String | 是 | 发型名称 |
| description | String | 否 | 发型描述 |
| imageUrl | String | 否 | 发型图片URL |

**返回参数 (ApiResponse<HairstyleTypeResponse>)**：

**HairstyleTypeResponse 包含字段**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 发型类型ID |
| merchantId | Long | 所属商家ID |
| name | String | 名称 |
| description | String | 描述 |
| imageUrl | String | 图片URL |
| active | Boolean | 是否启用 |
| createdAt | LocalDateTime | 创建时间 |

---

#### 4.4.2 获取发型类型列表

- **接口路径**：`GET /api/hairstyles/types`
- **接口描述**：分页获取发型类型列表

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**返回参数**：返回分页的发型类型列表

---

#### 4.4.3 获取活跃发型类型列表

- **接口路径**：`GET /api/hairstyles/types/active`
- **接口描述**：获取所有活跃的发型类型

**返回参数**：返回所有活跃的发型类型（非分页）

---

#### 4.4.4 获取发型类型详情

- **接口路径**：`GET /api/hairstyles/types/{id}`
- **接口描述**：根据ID获取发型类型详情

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 发型类型ID |

**返回参数**：返回发型类型详细信息

---

#### 4.4.5 更新发型类型

- **接口路径**：`PUT /api/hairstyles/types/{id}`
- **接口描述**：更新发型类型信息

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 发型类型ID |

**请求参数**：同创建发型类型

**返回参数**：返回更新后的发型类型信息

---

#### 4.4.6 删除发型类型

- **接口路径**：`DELETE /api/hairstyles/types/{id}`
- **接口描述**：删除发型类型

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 发型类型ID |

**返回参数**：返回成功消息

---

#### 4.4.7 切换发型类型状态

- **接口路径**：`PUT /api/hairstyles/types/{id}/toggle`
- **接口描述**：启用或禁用发型类型

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 发型类型ID |

**返回参数**：返回更新后的发型类型信息

---

#### 4.4.8 创建发型选项

- **接口路径**：`POST /api/hairstyles/options`
- **接口描述**：添加新的发型选项

**请求参数 (Request Body - HairstyleOptionRequest)**：

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | String | 是 | 选项名称 |
| category | String | 否 | 分类 |
| description | String | 否 | 描述 |

**返回参数 (ApiResponse<HairstyleOptionResponse>)**：

**HairstyleOptionResponse 包含字段**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 发型选项ID |
| merchantId | Long | 所属商家ID |
| name | String | 名称 |
| category | String | 分类 |
| description | String | 描述 |
| active | Boolean | 是否启用 |
| createdAt | LocalDateTime | 创建时间 |

---

#### 4.4.9 获取发型选项列表

- **接口路径**：`GET /api/hairstyles/options`
- **接口描述**：分页获取发型选项列表

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**返回参数**：返回分页的发型选项列表

---

#### 4.4.10 获取发型选项详情

- **接口路径**：`GET /api/hairstyles/options/{id}`
- **接口描述**：根据ID获取发型选项详情

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 发型选项ID |

**返回参数**：返回发型选项详细信息

---

#### 4.4.11 更新发型选项

- **接口路径**：`PUT /api/hairstyles/options/{id}`
- **接口描述**：更新发型选项信息

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 发型选项ID |

**请求参数**：同创建发型选项

**返回参数**：返回更新后的发型选项信息

---

#### 4.4.12 删除发型选项

- **接口路径**：`DELETE /api/hairstyles/options/{id}`
- **接口描述**：删除发型选项

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 发型选项ID |

**返回参数**：返回成功消息

---

### 4.5 消费管理接口

#### 4.5.1 创建消费记录

- **接口路径**：`POST /api/consumptions`
- **接口描述**：记录会员消费

**请求参数 (Request Body - ConsumeRequest)**：

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| memberId | Long | 是 | 会员ID |
| barberId | Long | 是 | 理发师ID |
| hairstyleTypeId | Long | 否 | 发型类型ID |
| originalAmount | BigDecimal | 是 | 原价（必须大于0） |
| paymentMethod | PaymentMethod | 是 | 支付方式 |
| hairstyleOptionIds | List<Long> | 否 | 发型选项ID列表 |
| remark | String | 否 | 备注 |

**PaymentMethod 枚举值**：BALANCE（余额）, CASH（现金）, CARD（银行卡）

**返回参数 (ApiResponse<ConsumptionRecordResponse>)**：

**ConsumptionRecordResponse 包含字段**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 消费记录ID |
| merchantId | Long | 商家ID |
| memberId | Long | 会员ID |
| memberName | String | 会员姓名 |
| barberId | Long | 理发师ID |
| barberName | String | 理发师姓名 |
| hairstyleTypeId | Long | 发型类型ID |
| hairstyleTypeName | String | 发型类型名称 |
| originalAmount | BigDecimal | 原价 |
| discountAmount | BigDecimal | 优惠金额 |
| actualAmount | BigDecimal | 实际金额 |
| barberCommission | BigDecimal | 理发师提成 |
| paymentMethod | PaymentMethod | 支付方式 |
| hairstyleOptions | String | 发型选项 |
| remark | String | 备注 |
| createdAt | LocalDateTime | 创建时间 |

**功能说明**：
1. 系统根据会员等级计算折扣价
2. 如果使用余额支付，自动扣减会员余额
3. 根据理发师级别和提成比例计算理发师提成
4. 自动更新会员累计消费金额和等级
5. 记录消费流水

---

#### 4.5.2 获取消费记录列表

- **接口路径**：`GET /api/consumptions`
- **接口描述**：分页获取消费记录

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| memberId | Long | 否 | - | 会员ID（不传则查询所有） |
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**返回参数**：返回分页的消费记录列表

---

#### 4.5.3 获取理发师的消费记录

- **接口路径**：`GET /api/consumptions/barber/{barberId}`
- **接口描述**：分页获取指定理发师的消费记录

**路径参数**：

| 参数名 | 类型 | 描述 |
|--------|------|------|
| barberId | Long | 理发师ID |

**请求参数 (Query Params)**：

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**返回参数**：返回分页的理发师消费记录列表

---

#### 4.5.4 匹配理发师

- **接口路径**：`POST /api/consumptions/match-barber`
- **接口描述**：根据发型选择匹配最适合的理发师

**请求参数 (Request Body - MatchBarberRequest)**：

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| hairstyleTypeId | Long | 否 | 发型类型ID |
| hairstyleOptionIds | List<Long> | 否 | 发型选项ID列表 |

**返回参数 (ApiResponse<List<MatchedBarberResponse>>)**：

**MatchedBarberResponse 包含字段**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | Long | 理发师ID |
| name | String | 姓名 |
| phone | String | 手机号 |
| level | BarberLevel | 级别 |
| commissionRate | BigDecimal | 提成比例 |
| matchScore | Integer | 匹配分数 |

**匹配规则**：
- 根据理发师级别计算匹配分数
- CHIEF（首席）：5分
- MASTER（资深）：4分
- SENIOR（高级）：3分
- INTERMEDIATE（中级）：2分
- JUNIOR（初级）：1分
- 返回按分数降序排列的理发师列表

---

## 五、通用响应格式

所有接口统一使用以下响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

**ApiResponse 包含字段**：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| code | int | 状态码（200成功，500服务器错误） |
| message | String | 响应消息 |
| data | Generic | 响应数据 |

---

## 六、认证机制

除公开接口外，所有接口需要在请求头中携带JWT Token：

```
Authorization: Bearer <token>
```

Token由`/api/merchants/login`接口登录后获取，有效期为24小时。

---

## 七、错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 500 | 服务器内部错误 |

业务错误会返回相应的错误消息，如：
- "用户名已存在"
- "用户名或密码错误"
- "账号待审核，请等待管理员审核"
- "账号审核未通过"
- "店铺已下线，请联系管理员"
- "会员不存在"
- "余额不足"
- "理发师不存在"
- "无权访问该资源"
