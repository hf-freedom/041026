# 理发店SaaS管理系统 API分析报告

## 1. 项目主要功能总结

本项目是一个基于Spring Boot的理发店SaaS管理系统，主要为理发店商家提供一站式的店铺管理功能。系统包含五大核心功能模块：

| 功能模块 | 主要功能 |
|---------|---------|
| **商家管理** | 商家注册申请、审核、登录、状态管理（上线/下线） |
| **会员管理** | 会员录入、查询、充值、消费、等级管理、交易记录 |
| **理发师管理** | 理发师增删改查、状态管理、级别设置、提成管理 |
| **发型管理** | 发型类型管理、发型选项管理、分类管理 |
| **消费管理** | 消费记录、理发师匹配、消费查询统计 |

---

## 2. 功能流程与接口调用

### 2.1 商家管理流程

#### 2.1.1 商家注册审核流程
```
商家提交注册申请 → 系统验证用户名唯一性 → 密码加密存储 → 状态设为待审核 → 管理员审核通过/拒绝 → 审核通过后可上线运营
```
调用接口：
- `POST /api/merchants/register` - 商家注册
- `POST /api/merchants/login` - 商家登录
- `GET /api/merchants/pending` - 获取待审核列表
- `POST /api/merchants/{id}/approve` - 审核通过
- `POST /api/merchants/{id}/reject` - 审核拒绝

#### 2.1.2 商家状态管理流程
```
审核通过 → 商家上线（可正常使用系统功能） → 商家下线（暂停使用）
```
调用接口：
- `POST /api/merchants/{id}/online` - 商家上线
- `POST /api/merchants/{id}/offline` - 商家下线
- `GET /api/merchants` - 获取所有商家列表
- `GET /api/merchants/{id}` - 获取商家详情

### 2.2 会员管理流程

#### 2.2.1 会员创建与充值流程
```
录入会员信息 → 验证手机号唯一性 → 创建会员 → 会员充值 → 生成交易记录
```
调用接口：
- `POST /api/members` - 创建会员
- `POST /api/members/recharge` - 会员充值
- `GET /api/members/transactions` - 获取交易记录

#### 2.2.2 会员等级自动升级流程
```
累计消费金额达到阈值 → 系统自动升级会员等级（青铜→白银→黄金→铂金→钻石）
```
调用接口：
- `GET /api/members` - 获取会员列表
- `GET /api/members/{id}` - 获取会员详情
- `PUT /api/members/{id}/level` - 手动更新会员等级

### 2.3 理发师管理流程
```
创建理发师 → 设置级别和提成比例 → 启用/禁用理发师 → 查看理发师业绩
```
调用接口：
- `POST /api/barbers` - 创建理发师
- `GET /api/barbers` - 获取理发师列表
- `GET /api/barbers/active` - 获取活跃理发师
- `PUT /api/barbers/{id}` - 更新理发师
- `PUT /api/barbers/{id}/status` - 更新理发师状态
- `DELETE /api/barbers/{id}` - 删除理发师

### 2.4 发型管理流程
```
创建发型类型 → 创建发型选项 → 设置分类 → 管理启用/禁用
```
调用接口：
- `POST /api/hairstyles/types` - 创建发型类型
- `GET /api/hairstyles/types` - 获取发型类型列表
- `POST /api/hairstyles/options` - 创建发型选项
- `GET /api/hairstyles/options` - 获取发型选项列表

### 2.5 消费管理流程

#### 2.5.1 消费结算流程
```
选择会员 → 选择理发师 → 选择发型 → 计算折扣金额 → 扣除余额/其他支付 → 计算理发师提成 → 生成消费记录和交易记录 → 自动升级会员等级
```
调用接口：
- `POST /api/consumptions` - 创建消费记录
- `GET /api/consumptions` - 获取消费记录列表

#### 2.5.2 理发师匹配流程
```
根据发型选择 → 按理发师级别匹配（总监级评分最高） → 匹配度从高到低排序 → 推荐最合适理发师
```
调用接口：
- `POST /api/consumptions/match-barber` - 匹配理发师
- `GET /api/consumptions/barber/{barberId}` - 获取理发师消费记录

---

## 3. 接口详细说明

### 3.1 商家管理接口

#### 3.1.1 商家注册申请

**接口地址**：`POST /api/merchants/register`

**功能描述**：商家提交注册申请，等待管理员审核

**内部实现细节**：
1. **调用方法**：
   - `merchantRepository.existsByUsername()` - 检查用户名是否已存在
   - `passwordEncoder.encode()` - BCrypt密码加密
   - `merchantRepository.save()` - 保存商家信息

2. **数据状态变化**：
   - 新建Merchant实体记录
   - status字段设为 `PENDING`（待审核）
   - balance初始化为0
   - createdAt自动记录当前时间
   - approvedAt为null

3. **异常处理**：
   - 用户名已存在时抛出 RuntimeException
   - 事务注解保证数据一致性

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 | 校验规则 |
|-------|------|------|------|---------|
| username | String | 是 | 用户名 | 长度3-50，非空 |
| password | String | 是 | 密码 | 长度6-100，非空 |
| shopName | String | 是 | 店铺名称 | 非空 |
| address | String | 否 | 店铺地址 | - |
| phone | String | 是 | 联系电话 | 非空 |
| contactPerson | String | 否 | 联系人 | - |
| businessLicense | String | 否 | 营业执照 | - |

**返回字段**：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | Long | 商家ID |
| username | String | 用户名 |
| shopName | String | 店铺名称 |
| address | String | 店铺地址 |
| phone | String | 联系电话 |
| contactPerson | String | 联系人 |
| businessLicense | String | 营业执照 |
| status | Enum | 状态：PENDING-待审核 |
| balance | BigDecimal | 余额 |
| createdAt | LocalDateTime | 创建时间 |
| approvedAt | LocalDateTime | 审核时间 |

---

#### 3.1.2 商家登录

**接口地址**：`POST /api/merchants/login`

**功能描述**：商家登录获取Token

**内部实现细节**：
1. **调用方法**：
   - `merchantRepository.findByUsername()` - 根据用户名查询商家
   - `passwordEncoder.matches()` - 验证密码匹配
   - `jwtTokenProvider.generateToken()` - 生成JWT Token

2. **状态校验流程**：
   - 验证用户名和密码正确性
   - 校验账号状态：
     - PENDING → 提示"账号待审核"
     - REJECTED → 提示"账号审核未通过"
     - OFFLINE → 提示"店铺已下线"
   - 只有 ONLINE/APPROVED 状态允许登录

3. **返回数据**：
   - JWT Token（包含merchantId和username）
   - 商家完整信息

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**返回字段**：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| token | String | JWT Token |
| merchant | Object | 商家信息 |

---

#### 3.1.3 审核通过

**接口地址**：`POST /api/merchants/{id}/approve`

**功能描述**：管理员审核通过商家申请

**内部实现细节**：
1. **调用方法**：
   - `merchantRepository.findById()` - 查询商家
   - `merchantRepository.save()` - 更新商家状态

2. **数据状态变化**：
   - 校验商家当前状态必须是 PENDING
   - status字段从 `PENDING` → `APPROVED`
   - approvedAt字段设置为当前时间
   - 商家获得上线资格

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 是 | 商家ID（路径参数） |

**返回字段**：审核后的商家信息

---

#### 3.1.4 审核拒绝

**接口地址**：`POST /api/merchants/{id}/reject`

**功能描述**：管理员拒绝商家申请

**内部实现细节**：
1. **调用方法**：
   - `merchantRepository.findById()` - 查询商家
   - `merchantRepository.save()` - 更新商家状态

2. **数据状态变化**：
   - 校验商家当前状态必须是 PENDING
   - status字段从 `PENDING` → `REJECTED`
   - 商家永久无法登录，需重新注册

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 是 | 商家ID（路径参数） |

**返回字段**：审核后的商家信息

---

#### 3.1.5 商家上线

**接口地址**：`POST /api/merchants/{id}/online`

**功能描述**：将商家状态设为上线，可正常使用系统

**内部实现细节**：
1. **调用方法**：
   - `merchantRepository.findById()` - 查询商家
   - `merchantRepository.save()` - 更新商家状态

2. **数据状态变化**：
   - 校验商家当前状态必须是 APPROVED 或 OFFLINE
   - status字段变更为 `ONLINE`
   - 商家可正常使用所有业务功能

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 是 | 商家ID（路径参数） |

**返回字段**：更新后的商家信息

---

#### 3.1.6 商家下线

**接口地址**：`POST /api/merchants/{id}/offline`

**功能描述**：将商家状态设为下线，暂停使用

**内部实现细节**：
1. **调用方法**：
   - `merchantRepository.findById()` - 查询商家
   - `merchantRepository.save()` - 更新商家状态

2. **数据状态变化**：
   - 校验商家当前状态必须是 ONLINE 或 APPROVED
   - status字段变更为 `OFFLINE`
   - 商家账号被冻结，无法登录使用

---

### 3.2 会员管理接口

#### 3.2.1 创建会员

**接口地址**：`POST /api/members`

**功能描述**：录入新会员

**内部实现细节**：
1. **调用方法**：
   - `securityUtil.getCurrentMerchantId()` - 从Token获取当前登录商家ID
   - `memberRepository.existsByMerchantIdAndPhone()` - 检查同商家下手机号是否重复
   - `memberRepository.save()` - 保存会员信息

2. **数据初始化**：
   - level默认为 `BRONZE`（青铜）
   - balance默认为 `BigDecimal.ZERO`
   - totalConsumption默认为 `BigDecimal.ZERO`
   - 关联当前登录的merchantId

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| name | String | 是 | 会员姓名 |
| phone | String | 是 | 手机号 |

**返回字段**：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | Long | 会员ID |
| merchantId | Long | 商家ID |
| name | String | 会员姓名 |
| phone | String | 手机号 |
| level | Enum | 会员等级 |
| discount | Double | 折扣率 |
| balance | BigDecimal | 余额 |
| totalConsumption | BigDecimal | 累计消费 |
| createdAt | LocalDateTime | 创建时间 |

---

#### 3.2.2 会员充值

**接口地址**：`POST /api/members/recharge`

**功能描述**：为会员账户充值

**内部实现细节**：
1. **调用方法**：
   - `securityUtil.getCurrentMerchantId()` - 获取当前商家ID
   - `memberRepository.findById()` - 查询会员
   - `memberRepository.save()` - 更新会员余额
   - `transactionRepository.save()` - 创建充值交易记录

2. **数据状态变化**：
   - 校验会员归属权（必须属于当前商家）
   - member.balance = balance + amount
   - 新增Transaction记录：
     - type = `RECHARGE`
     - balanceAfter记录充值后余额
     - 关联memberId和merchantId

3. **事务保证**：使用`@Transactional`保证会员余额更新和交易记录创建的原子性

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| memberId | Long | 是 | 会员ID |
| amount | BigDecimal | 是 | 充值金额，必须大于0 |
| remark | String | 否 | 备注 |

**返回字段**：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | Long | 交易记录ID |
| merchantId | Long | 商家ID |
| memberId | Long | 会员ID |
| type | Enum | 交易类型：RECHARGE-充值 |
| amount | BigDecimal | 充值金额 |
| balanceAfter | BigDecimal | 充值后余额 |
| remark | String | 备注 |
| createdAt | LocalDateTime | 交易时间 |

---

### 3.3 理发师管理接口

#### 3.3.1 创建理发师

**接口地址**：`POST /api/barbers`

**功能描述**：添加新理发师

**内部实现细节**：
1. **调用方法**：
   - `securityUtil.getCurrentMerchantId()` - 获取当前商家ID
   - `barberRepository.save()` - 保存理发师信息

2. **默认值设置**：
   - active默认为 `true`（启用状态）
   - level未设置时默认为 `JUNIOR`（初级）
   - commissionRate未设置时默认为 `0.3`（30%提成）

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| name | String | 是 | 理发师姓名 |
| phone | String | 否 | 联系电话 |
| level | Enum | 否 | 理发师级别：JUNIOR/INTERMEDIATE/SENIOR/MASTER/CHIEF |
| commissionRate | BigDecimal | 否 | 提成比例：0-1之间 |

**返回字段**：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | Long | 理发师ID |
| merchantId | Long | 商家ID |
| name | String | 理发师姓名 |
| phone | String | 联系电话 |
| level | Enum | 理发师级别 |
| commissionRate | BigDecimal | 提成比例 |
| active | Boolean | 是否启用 |
| createdAt | LocalDateTime | 创建时间 |

---

#### 3.3.2 提成计算

**内部实现方法**：`BarberService.calculateCommission()`
- 提成金额 = 实际消费金额 × 提成比例
- 使用HALF_UP四舍五入保留2位小数
- 在消费结算时自动调用

---

### 3.4 发型管理接口

#### 3.4.1 创建发型类型

**接口地址**：`POST /api/hairstyles/types`

**功能描述**：添加新的发型类型

**内部实现细节**：
1. **调用方法**：
   - `securityUtil.getCurrentMerchantId()` - 获取当前商家ID
   - `hairstyleTypeRepository.save()` - 保存发型类型

2. **数据初始化**：
   - active默认为 `true`
   - 关联当前商家ID

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| name | String | 是 | 发型名称 |
| description | String | 否 | 描述 |
| imageUrl | String | 否 | 图片URL |

**返回字段**：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | Long | 发型类型ID |
| merchantId | Long | 商家ID |
| name | String | 发型名称 |
| description | String | 描述 |
| imageUrl | String | 图片URL |
| active | Boolean | 是否启用 |
| createdAt | LocalDateTime | 创建时间 |

---

#### 3.4.2 切换发型类型状态

**接口地址**：`PUT /api/hairstyles/types/{id}/toggle`

**功能描述**：启用或禁用发型类型

**内部实现细节**：
- 状态取反：`type.setActive(!type.getActive())`
- 禁用的发型类型在选择时不会显示

---

### 3.5 消费管理接口

#### 3.5.1 创建消费记录

**接口地址**：`POST /api/consumptions`

**功能描述**：记录会员消费，自动计算折扣和提成

**内部实现细节**：

1. **前置校验（按顺序）**：
   - 校验会员是否存在且归属当前商家
   - 校验理发师是否存在、归属当前商家、且状态为active
   - 若支付方式为BALANCE，校验会员余额 ≥ 实际消费金额

2. **金额计算**：
   - 调用 `memberService.calculateDiscountedAmount()`:
     - 根据会员等级获取对应折扣率（青铜0.95、白银0.9、黄金0.85、铂金0.8、钻石0.75）
     - 折扣金额 = 原价 × 折扣率，四舍五入2位小数
   - 调用 `barberService.calculateCommission()`:
     - 理发师提成 = 实际消费金额 × 提成比例

3. **会员账户更新**：
   - 余额支付时：member.balance = balance - actualAmount
   - member.totalConsumption = totalConsumption + actualAmount
   - 调用 `updateMemberLevel()` 自动升级：
     - ≥10000元 → DIAMOND（钻石）
     - ≥5000元 → PLATINUM（铂金）
     - ≥2000元 → GOLD（黄金）
     - ≥500元 → SILVER（白银）
     - <500元 → BRONZE（青铜）

4. **数据持久化（原子事务）**：
   - 更新Member表（balance、totalConsumption、level）
   - 新增ConsumptionRecord消费记录
     - 保存所有金额信息
     - 发型选项名称拼接成逗号分隔字符串存储
   - 新增Transaction交易记录
     - type = `CONSUME`
     - 关联消费记录ID到remark

5. **返回数据组装**：
   - 回填会员姓名、理发师姓名
   - 回填发型类型名称

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| memberId | Long | 是 | 会员ID |
| barberId | Long | 是 | 理发师ID |
| hairstyleTypeId | Long | 否 | 发型类型ID |
| originalAmount | BigDecimal | 是 | 原价，必须大于0 |
| paymentMethod | Enum | 是 | 支付方式：BALANCE-余额支付，CASH-现金支付 |
| hairstyleOptionIds | List&lt;Long&gt; | 否 | 发型选项ID列表 |
| remark | String | 否 | 备注 |

**返回字段**：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | Long | 消费记录ID |
| merchantId | Long | 商家ID |
| memberId | Long | 会员ID |
| memberName | String | 会员姓名 |
| barberId | Long | 理发师ID |
| barberName | String | 理发师姓名 |
| hairstyleTypeId | Long | 发型类型ID |
| hairstyleTypeName | String | 发型类型名称 |
| originalAmount | BigDecimal | 原价 |
| discountAmount | BigDecimal | 折扣后金额 |
| actualAmount | BigDecimal | 实际支付金额 |
| barberCommission | BigDecimal | 理发师提成 |
| paymentMethod | Enum | 支付方式 |
| hairstyleOptions | String | 发型选项（逗号分隔） |
| remark | String | 备注 |
| createdAt | LocalDateTime | 消费时间 |

---

#### 3.5.2 匹配理发师

**接口地址**：`POST /api/consumptions/match-barber`

**功能描述**：根据发型选择匹配最适合的理发师，按级别评分排序

**内部实现细节**：
1. **调用方法**：
   - `barberRepository.findByMerchantIdAndActiveTrue()` - 获取当前商家所有活跃理发师

2. **匹配算法**：
   - 按理发师级别设定基础分值：
     - CHIEF（总监）→ 5分
     - MASTER（技师）→ 4分
     - SENIOR（高级）→ 3分
     - INTERMEDIATE（中级）→ 2分
     - JUNIOR（初级）→ 1分
   - 按匹配分数从高到低排序返回
   - 级别越高，推荐优先级越高

**入参字段**：

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| hairstyleTypeId | Long | 否 | 发型类型ID |
| hairstyleOptionIds | List&lt;Long&gt; | 否 | 发型选项ID列表 |

**返回字段**：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | Long | 理发师ID |
| name | String | 理发师姓名 |
| phone | String | 联系电话 |
| level | Enum | 理发师级别 |
| commissionRate | BigDecimal | 提成比例 |
| matchScore | Integer | 匹配分数（越高越推荐） |

---

## 附录：枚举值说明

### 商家状态枚举
- `PENDING` - 待审核
- `APPROVED` - 审核通过
- `REJECTED` - 审核拒绝
- `ONLINE` - 上线
- `OFFLINE` - 下线

### 会员等级枚举
- `BRONZE` - 青铜（0.95折，累计消费<500元）
- `SILVER` - 白银（0.9折，累计消费≥500元）
- `GOLD` - 黄金（0.85折，累计消费≥2000元）
- `PLATINUM` - 铂金（0.8折，累计消费≥5000元）
- `DIAMOND` - 钻石（0.75折，累计消费≥10000元）

### 理发师级别枚举
- `JUNIOR` - 初级（1分）
- `INTERMEDIATE` - 中级（2分）
- `SENIOR` - 高级（3分）
- `MASTER` - 技师（4分）
- `CHIEF` - 总监（5分）

### 交易类型枚举
- `RECHARGE` - 充值
- `CONSUME` - 消费

### 支付方式枚举
- `BALANCE` - 余额支付
- `CASH` - 现金支付

---

## 数据流转总结

| 业务场景 | 涉及数据表 | 主要字段变化 |
|---------|-----------|-------------|
| 会员充值 | Member、Transaction | Member.balance增加<br>Transaction新增RECHARGE记录 |
| 会员消费 | Member、ConsumptionRecord、Transaction | Member.balance减少（余额支付时）<br>Member.totalConsumption增加<br>Member.level可能升级<br>新增消费记录和消费交易记录 |
| 理发师提成计算 | ConsumptionRecord | barberCommission字段存储计算结果 |
