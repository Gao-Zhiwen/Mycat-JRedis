## todo list

勾选的为进行中的项目，未勾选的为待定的项目

### 数据结构

- [x] sds（字符串）数据结构
- [x] 字典数据结构
- [x] RedisString字符串对象（编码：int、raw、embstr）
- [ ] 链表、跳跃表、集合、压缩列表


### 单机

- [x] 数据库（服务器及其管理的客户端状态）


- [ ] 过期


- [ ] RDB


- [ ] AOF
- [ ] 发布订阅
- [ ] 事务
- [ ] lua脚本
- [ ] 排序
- [ ] 二进制位数组
- [ ] 慢查询
- [ ] 监视器

### 多机

- [ ] 复制

- [ ] 集群

      ​

---



##1. 数据结构

| 文件                                       | 内容             |
| ---------------------------------------- | -------------- |
| **sds.h  sds.c**                         | 动态字符串实现        |
| adjust.h  adlist.c                       | 双端链表实现         |
| dict.h  dict.c                           | 字典实现           |
| redis.h中的zskiplist和zskiplistNode结构，以及t_zset.c中以zs1开头的函数 | 跳跃表实现          |
| hyperloglog.c中的hllhdr结构，以及所有以hll开头的函数    | HyperLogLog 实现 |



##2. 内存编码数据结构

| 文件                   | 内容                 |
| -------------------- | ------------------ |
| intset.h  intset.c   | 整数集合（intset）数据结构   |
| ziplist.h  ziplist.c | 压缩列表（zip list）数据结构 |



## 3. 具体数据类型

| 文件                       | 内容               |
| ------------------------ | ---------------- |
| object.c                 | redis的对象（类型）系统实现 |
| t_string.c               | 字符串键的实现          |
| t_list.c                 | 列表键的实现           |
| t_hash.c                 | 散列键的实现           |
| t_set.c                  | 集合键的实现           |
| t_zset.c中除zs1开头的函数之外的函数  | 有序集合键的实现         |
| hyperloglog.c中所有以pf开头的函数 | HyperLogLog键的实现  |



## 4. 数据库相关

| 文件                          | 内容              |
| --------------------------- | --------------- |
| redis.h文件中的redisDb结构，以及db.c | redis的数据库实现     |
| notify.c                    | redis的数据库通知功能实现 |
| rdb.h  rdb.c                | redis的rdb持久化实现  |
| aof.c                       | redis的aof持久化实现  |



独立的功能模块：

| 文件                                       | 内容                  |
| ---------------------------------------- | ------------------- |
| redis.h文件的pubsubPattern结构，以及pubsub.c     | 发布与订阅功能             |
| redis.h文件的multiState结构以及multiCmd结构，multi.c | 事务功能                |
| sort.c                                   | sort命令              |
| bitops.c                                 | getbit、setbit等二进制操作 |



## 5. 客户端与服务端

| 文件                                 | 内容                                       |
| ---------------------------------- | ---------------------------------------- |
| ae.c  ae_*.c                       | redis的事件处理器实现（基于reactor）                 |
| networking.c                       | redis的网络连接库，负责发送命令回复和接受命令请求，同时也负责创建、销毁客户端，以及通信协议分析 |
| redis.h  redis.c 中和单机redis服务器有关的部分 | 单机redis服务器的实现                            |



独立的功能模块：

| 文件          | 内容    |
| ----------- | ----- |
| scripting.c | lua脚本 |
| slowlog.c   | 慢查询   |
| monitor.c   | 监视器   |



## 6. 多机

| 文件            | 内容   |
| ------------- | ---- |
| replication.c | 复制   |
| sentinel.c    | 哨兵   |
| cluster.c     | 集群   |




## 参考资料

[redis](http://blog.huangz.me/diary/2014/how-to-read-redis-source-code.html)
