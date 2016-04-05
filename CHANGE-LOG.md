Change Log

1.0 Features
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
* 1. 单线程：基于当前线程高效率运作。
* 2. 轻量级：微小的内存开销与Jar包体积，仅约 86K 。
* 3. 全支持：GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS, PATCH.
* 4. 全自动：一行代码将请求Java Model 转化为 Http Parameter，结果Json String 转化为 Java Model 。
* 5. 易拓展：自定义 DataParser，将网络数据流自由转化为你想要的任意数据类型。
* 6. 基于接口：架构灵活，轻松替换网络连接方式的核心实现方式，以及 Json 序列化库。
* 7. 文件上传：支持单个、多个大文件上传。
* 8. 文件下载：支持文件、Bimtap下载及其进度通知。
* 9. 网络禁用：快速禁用一种、多种网络环境，比如禁用 2G，3G 。
* 10. 数据统计：链接、读取时长统计，以及流量统计。
* 11. 异常体系：统一的异常处理体系，简明清晰地抛出可再细分的三大类异常：客户端、网络、服务器异常。
* 12. GZIP压缩：Request, Response 自动 GZIP 压缩节省流量。
* 13. 自动重试：结合探测异常类型和当前网络状况，智能执行重试策略。
* 14. 自动重定向：基于 30X 状态的重试，且可设置最大次数防止过度跳转。
* 15. 自带简单异步执行器，方便开发者实现异步请求方案。


2.0 Features
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
* 1. 可配置：更多更灵活的配置选择项，多达 23+ 项。
* 2. 多态化：更加直观的API，输入和输出更加明确。
* 3. 强并发：智能高效的并发调度，有效控制核心并发与队列控制策略。
* 4. 注解化：信息配置约定更多样，如果你喜欢，可以注解 API、Method、ID、TAG、CacheMode 等参数。
* 5. 多层缓存：内存命中更高效！支持多样的缓存模式，支持设置缓存有效期。
* 6. 完善回调：自由设置回调当前或UI线程，自由开启上传、下载进度通知。
* 7. 完善构建：提供 jar 包支持，后边支持 gradle 和 maven 。

2.2.0 
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
1. 修复某些情况下参数无法拼接到URI的bug；
2. http参数类可以注解指定Key，避免成员变量出现java关键词，同时增加动态URL构建；
3. Request接受直接注解参数、内部构建参数(此特性已删除)。

2.3.0
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
1. 注解参数动态化；
2. 优化HttpRichParamModel的使用。

3.0.0
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
1. 添加HttpUrlConnection支持，并设置为默认HTTP客户端引擎。
2. 将Apache HTTP Client移到独立项目。
3. 优化HttpConfig的体验。