## 2

#### SpringBoot项目中，如何写接口?

一般来说接口都放在controller文件夹下，之后新建xxxController类，在类中写方法。、那Spring怎么知道这个类是接口类，用户访问什么地址时才会返回呢？首先，要使用@RestConroller将类注册为RESTful接口（即形如/user/1的接口），同时在方法上使用@RequestMapping配置映射地址（允许所有请求方法），之后启动应用，即可访问/hello。那应用又是怎么找到我们注册的接口呢？答案藏在应用上方的@SpringBootApplication注释中，该注释内有@ComponentScan，默认配置下会扫描应用所在文件夹下的所有已注册接口，可使用该注解可自行配置需要扫描的包。

```java
@RestController // @Conroller + @ResponseBody
public class TestController {
    /*
    @RequestMapper(path = "/hello", method = RequestMethod.GET)
    @GetMapping("/hello")
     */
    @RequestMapping("/hello")
    public String hello(){
        return "Hello, Spring!";
    }
}
```

#### 接口写完后，如何快速测试？

写好接口后，如果要测试，要么要切换到浏览器窗口，妖魔二使用Postman进行POST请求，都比较麻烦。可以使用httpclient进行接口测试，定义xxx.http文件，可以在idea中直接运行，查看运行结果。

```http
GET http://localhost:8080/hello

> {%
 client.test("test-hello", function() {
   client.log("test hello");
   client.log("response.body");
   client.assert(response.status === 200, "status code is not 200");
   client.assert(response.body === "Hello, Spring!", "result is incorrect");
 })
 %}

###
```

#### SpringBoot项目可以如何配置？

SpringBoot会自动读取放在resources目录及其子目录下的application配置，配置文件形式可以是.properties，也可以是.yaml。熟悉哪种用哪种，也可在网上对二者互相转换。而要在代码中使用配置中的属性，可使用@Value("${prop : defaultVal}"注解使用。

#### 如何集成热部署？

开发过程，每有一点更改想要测试，都需要重新启动应用。而要做到边开发边测试，可以继承热部署。开启热部署分三步走：

1. 需要在项目中引入依赖spring-boot-devtools。此时不必指明版本号，因为对于spring的内置依赖项目，其版本号在父配置中已经隐式指明了，不必单独写出。
2. 进入Idea的设置，搜索Compile，开启项目自动构建（Build Project Automatically）。此步骤是为了确保应用启动前自动编译。
3. 双击shift（或者点击Help-Action），找到compiler.automake.allow.when.app.runnin，开启配置。此步骤是为了确保应用启动后自动编译。

如此，以后每次代码改动，只要保存，就会随后触发自动编译（比起手动重启要快很多），立即看到结果。



## 3

#### 如何在SpringBoot中整合并使用Mybatis？

要使用Mybatis建立持久层，需要以下几步：

1. 在依赖中引入mysql和mybatis-spring-boot-starter。

   ```xml
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
   </dependency>
   
   <dependency>
       <groupId>org.mybatis.spring.boot</groupId>
       <artifactId>mybatis-spring-boot-starter</artifactId>
       <version>2.1.3</version>
   </dependency>
   ```

2. 在配置中设置spring.datasource的url、username、password以及driver

   ```properties
   # 配置数据源
   spring.datasource.url=jdbc:mysql://localhost:3306/wiki?characterEncoding=UTF8&autoReconnect=true&serverTimezone=Asia/Shanghai
   spring.datasource.username=wiki
   spring.datasource.password=wiki
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   ```

3. 建立实体类与操作实体的接口mapper，在mapper中定义查询方法，并在resources中新建同名xml配置，编写slq语句。

4. 在应用中引入mapper类，在Application类上使用@MapperScan注解（让应用知道有mapper都在哪儿）；在配置中设置mapper配置的路径（让应用知道mapper配置都在哪儿），使用mybatis.mapper-locations属性。

   ```properties
   # 设置mapper.xml的位置
   mybatis.mapper-locations=classpath:/mapper/**/*.xml
   ```

5.  编写相应的service和controller。service中定义mapper属性，并使用@Autowired自动装配；对service使用@Service注册后，在controller中定义service属性，并使用@Autowired自动装配。使用@Autowired等于是让spring托管对象，我们不必去new对象。@Service的意义类似，也是让spring为我们在容器中创建该对象，好在其它地方使用。
6. 

#### 如何使用mybatis-generator插件自动生成持久层代码？

1. 在依赖插件中引入mybatis-generator-maven-plugin，并编写好插件配置文件。要提供数据库连接字段，要对哪个表生成，代码生成后各自存放的位置等。

   ```xml
   <!--  Mybatis代码自动生成插件    -->
   <plugin>
       <groupId>org.mybatis.generator</groupId>
       <artifactId>mybatis-generator-maven-plugin</artifactId>
       <version>1.4.0</version>
       <configuration>
           <configurationFile>src/main/resources/generator/generator-config.xml</configurationFile>
           <verbose>true</verbose>
       </configuration>
       <dependencies>
           <dependency>
               <groupId>mysql</groupId>
               <artifactId>mysql-connector-java</artifactId>
               <version>8.0.23</version>
           </dependency>
       </dependencies>
   </plugin>
   ```

2. 增加maven启动选项，配置maven启动命令，并以该命令执行，等待生成完成。

   ```cmd
   mybatis-generator:generate -e
   ```



#### 如何编写模糊查询的接口？

1. 使用参数查询，需要使用mybatis生成的xxxExample实例，调用其createCriteria方法获得Criteria实例，再调用Criteria实例的andNameLike方法，参数就是要模糊查询的字符串。之后，再将xxxExample当作参数，传入mapper的方法中即可。

2. 后端返回给前端的信息，应当采用标准化格式，比如有响应成功与否的信息，有给前端的提示信息，还要有真正的响应的内容。统一的响应格式无疑有益于前后端的协同开发。

3. 无论请求响应，很多时候都无法与数据库实体类的各个属性一一对应。有时响应要少一点，比如前端传来账号密码，登录成功后，后端就不该把密码再给返回；有时查询请求有多个参数，用单个字符串参数不方面，可也没必要用到实体类的所有属性，常用的也就是id和name；有时多个对象可能有关联，可能就要给某个对象添几个属性。凡此种种，都需要将请求和响应重新包装，要保证Controller层中不要与domain/pojo层发生直接交互。

   ```java
   // EBookController.java
   @GetMapping("/get")
   public CommonResp get(EbookReq req){
       CommonResp<List<EbookResp>> resp = new CommonResp<>();
       List<EbookResp> ebookRespList = ebookService.get(req);
       resp.setContent(ebookRespList);
       return resp;
   }
   
   // 参数名一致时，会自动找到类中的属性映射
   public List<EbookResp> get(EbookReq req){
       EbookExample example = new EbookExample();
       EbookExample.Criteria criteria = example.createCriteria();
       criteria.andNameLike("%" + req.getName() + "%");
       List<Ebook> ebooks = ebookMapper.selectByExample(example);
   
       List<EbookResp> respList = new ArrayList<>();
       for (Ebook ebook : ebooks) {
           EbookResp ebookResp = new EbookResp();
           BeanUtils.copyProperties(ebook, ebookResp);
           respList.add(ebookResp);
       }
       return respList;
   }
   ```

4. 注意上面代码中将Ebook转换为EbookResp再放入EbookResp列表的逻辑可以抽取出来，做成通用的工具类中的方法，从而减少代码冗余。



## 4

#### 如何搭建Vue开发环境？

遇到的麻烦

1. vue cli创建项目时，中途报错，提示unexpected token，后升级node，使用最新版vue cli解决。
2. 安装ant design vue组件时，没有切入项目文件夹，导致ant组件无法使用，后进入项目目录安装解决。
3. 查看ant design vue的文档时，vue3对应的版本应该是2.x.x，我选择了1.x.x，有些标签在vue3中已经废弃了，导致出现报错。

```cmd
# 确保已安装vue，且已升级到10以上
npm get registry
npm config set registry http://registry.npm.taobao.org
npm install -g @vue/cli@4.5.9
vue --version
vue create web
cd web
npm run serve

# @next表示安装最新的未发布正式版本
npm install ant-design-vue@next --save 
```

vue cli初始执行main.ts，将内容页App.vue渲染到id=app所在的页面（index.html）。

UI界面可以怎么写？

1. 要么使用原生html5，完全自己写页面
2. 要么基于第三方css库，如bootstrap
3. 要么使用基于Vue的UI组件，如ElementUI、Ant Design Vue 

#### 如何完成布局并自定义组件？

环境搭建完成后，首先要确定的是网页的部剧。

区分页面中不变的部分和变化的部分，其中header和footer每个页面都是如此，而sider和content却要变化。不变的部分就写在App.vue中，而变化的部分放在<router-view/>中。

随着项目扩展，公共部分的逻辑可能也很复杂，因此可以考虑将公共部分做成自定义组件。步骤如下：

1. 提取公共部分的代码，在components目录下新建组件，一般命名格式为the-xxx.vue（驼峰TheXxx.vue亦可）。the表示该组件独一无二，且能避免与html自有标签重名。
2. 在该组件文件中底部的<script>标签内，要使用vue提供的defineComponent将当前组件导出。
3. 在使用到组件的页面的<script>标签内import组件，之后步骤同2。



## 5

#### 前后端如何互通？

```cmd
npm install axios --save
# 导入ant-design-vue的图标
npm install @ant-design/icons-vue --save
```

前端使用http库axios向后端发送请求。凡是前后端分离的项目，首先要解决的问题就是跨域问题。跨域问题之所以产生，是因为浏览器有所谓同源策略，即从源头A获取得的文档与脚本无法在浏览器内与源头B的资源交互，该策略是为了避免恶意攻击。而所谓源头，由协议、域名、端口共同定义。因此，即使是本地开发前后端，因为端口不同，前端发给后端的请求也会被认为是跨域请求。Spring可以配置跨域注册，从而规避该问题。

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)
                // 允许前端携带凭证
                .allowCredentials(true)
                // 1小时内不需要再预检（发送OPTIONS请求）
                .maxAge(3600);
    }
}
```

#### Vue3中前端如何使用响应式数据？

Vue2使用data定义与页面交互的变量，methods定义用到的方法，mounted、created等生命周期声明各自要执行的函数。Vue3则将这些统归于setup之内，并使用ref或reactive来定义响应式数据。

```typescript
// vue3新增onMounted来取代vue2中的mounted，用ref来定义响应式数据
import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';

  export default defineComponent({
    name: 'Home',
    components: { 
    },

    // vue3新增，初始化方法
    setup: function () {
      const ebooks = ref();

      onMounted(() => {
        axios.get('http://localhost:8082/ebook/get?name=spring')
                .then((response) => {
                  const data = response.data;
                  ebooks.value = data.content;
                });
      });
		
      return {
        ebooks,
      };
    }
  });
```

前端使用a-list组件展示电子书，并引入grid属性分栏。而要使用ant-design-vue的图标，需要额外导入，并在main.ts中全局导入。后端则使用动态SQL，保证前端不给查询参数时，能返回表中全部数据。

#### 如何完成vue cli多环境配置？

现在前端访问后端接口时，由于前后端都在本地开发，主机名均为localhost。但是，之后上线部署，前后端可能并不在一起，到时在一一替换，岂不麻烦。因此，vue cli引入环境配置，支持为不同的环境配置不同的参数，供前端访问、编译、运行使用。具体做法如下：

1. 在前端项目根目录下新建.env.dev和.env.prod文件，为开发环境与生产环境配置不同的参数。

   ```
   NODE_ENV=development
   VUE_APP_SERVER=http://localhost:8082
   ```

2. 在package.json中，为生产和开发配置不同的编译命令。

   ```json
   "scripts": {
       "serve-dev": "vue-cli-service serve --mode dev --port 8080",
       "serve-prod": "vue-cli-service serve --mode prod --port 8080",
       "build-dev": "vue-cli-service build --mode dev",
       "build-prod": "vue-cli-service build --mode prod",
   },
   ```

3. 为axios库设置默认的baseURL，这样可以直接调用接口，domain根据使用环境不同，动态变化，而不必与domain拼接。

   ```typescript
   // main.ts
   import axios from 'axios';
   axios.defaults.baseURL = process.env.VUE_APP_SERVER;
   ```

#### 前端如何拦截请求？

使用axios拦截器统一打印日志

```typescript
axios.interceptors.request.use(function(config){
    console.log('Parameters: ', config);
    return config;
}, error => {
    return Promise.reject(error);
});
axios.interceptors.response.use(function(response){
    console.log('Return: ', response);
    return response;
}, error => {
    return Promise.reject(error);
});
```



#### 如何在后端拦截请求，以统计接口访问耗时？

在Spring中要拦截请求，有三种方法：

1. 借助Servlet原生支持的Filter过滤器。
2. 借助Sping的AOP机制。
3. 借助SpringBoot的拦截器。



## 6

本章主要完成了电子书管理页面的前后端功能开发，包括分页、新增、查询（按名字）、删除、编辑、校验等功能。

#### 前端增加一个页面，要做以下几步：

1. 创建页面，即在views文件夹下创建vue文件。
2. 编写路由，即在router文件夹内的文件中添加路由映射。
3. 添加跳转，即在页面中添加<router-link>标签，跳转到新增页。

#### 如何使用PageHelper实现后端分页？

1. 添加依赖，使用pagehelper插件（pagehelper-spring-boot-starter）。
2. 分页功能需要查询两次，首先要查询数据总数，然后查询当前页数据。
3. 每设置一次分页，只对接下来的第一个select查询语句生效。
4. 分页请求需要提供页码和每页条数，而响应需要提供当前页列表内容和总行数。因此，再为分页请求添加分页请求类，为分页响应增加分页响应类。

#### 后端开发增删改查的过程中有哪些需要注意的点？

后端增加编辑功能时：

1. 为查询和保存提供不同的请求类响应类，之后开发起来更为灵活。
2. 如果POST请求提交的是json，那么需要在对应方法的参数前添加@RequestBody，否则会接收不到json。

生成id时用到了雪花算法：

生成id有多种算法，最简单的是自增，还有uuid，还有就是雪花算法。而雪花算法是根据时间戳、数据中心、机器标识、序列号（当前时间内的第几条记录）共同生成唯一的id，它可以保证多数据中心多机器的环境下并发请求都能生成唯一的id。

实现删除功能时

对于前端提交的删除请求，id放在url中，形如/ebook/delete/123456，后端在获取时，要在参数中加@PathVariable注解。

#### 如何在后端集成Validation做参数校验？

1. 引入依赖，spring-boot-starter-validation。注意引入pom依赖后，即便是热部署，也要重启一下，否则即便代码在IDE不报错，应用还是无法使用。

2. 在需要校验的请求类属性，添加注解并设置提示信息，如@NotNull， @Max。此处就显示出将不同的请求类分开的好处了，在添加注解时，只会对被注解过的请求类起作用。

   ```java
   public class PageReq {
       @NotNull(message = "【页码】不能为空")
       private int page;
   
       @Max(value = 1000, message = "【每页条数】不能超过1000")
       @NotNull(message = "【每页条数】不能为空")
       private int size;
       ...
   ```

3. 在用到请求类的Controller参数前，添加注解@Valid。

   ```java
   @PostMapping("/save")
   public CommonResp save(@Valid @RequestBody EbookSaveReq req){
       CommonResp resp = new CommonResp<>();
       ebookService.save(req);
       return resp;
   }
   ```

4. 处理校验时产生的BindException异常，使异常不至于又影响前端，并让返回给前端的值保持格式统一

   ```java
   // controller/ControllerExceptionHandler.java
   @ControllerAdvice
   public class ControllerExceptionHandler {
   
       private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);
       /**
        * 校验异常统一处理
        * @param e
        * @return
        */
       @ExceptionHandler(value = BindException.class)
       @ResponseBody
       public CommonResp validExceptionHandler(BindException e) {
           CommonResp commonResp = new CommonResp();
           LOG.warn("参数校验失败：{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
           commonResp.setSuccess(false);
           commonResp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
           return commonResp;
       }
   }
   ```

#### 遇到了哪些Bug？

1. 电子书id是long型，有17位，传给前段时，由于js精度有限，超过16位的部分都变为0，于是删除时，id与数据库中的id不匹配。在响应类的id属性上，加上@JsonSerialize注解，转为字符串即可。

   ```java
   public class EbookQueryResp {
       @JsonSerialize(using = ToStringSerializer.class)
       private Long id;
       ...
   ```

2. 编辑电子书时，由于是响应式数据，列表中的值会和输入值同步，即便退出编辑框也无法还愿，解决办法是给编辑框中的列赋值时，复制一份放进去，而不是用原值。



## 7

创建基于无限树结构的分类表，并生成持久层代码。

分类管理功能的前后端代码完全可以从电子书管理那里复制过来，再替换修改即可。这样真的可以省下不少时间，诚如老师提到的：“体力活也是技巧的。”后端代码复制要从抽象的高层到底层，替换好后，再看缺少什么，再去补充，一点点让controller里的错误消失不见。

去除分页管理中的分页，并改为树形表格展示 

分类编辑功能优化：改父分类为下拉框选择

电子书管理页面的分类改为级联选择

1. 编辑对话框中的分类一和分类二input要改用<a-cascade>

   - 联级选择的value形如[category_1, category_2]，因此要再定义categoryIds来代替原来的category1Id和category2Id。
   - level1用来存放所有分类信息，获取分类信息的请求可以在onMounted中发出。

   ```html
   <a-cascader
       v-model:value="categoryIds"
       :field-names="{ label:'name', value:'id', children: 'children' }"
       :options="level1"
   />
   ```

2. 管理页面的表格改为显示分类一/分类二，而不是两个分类各占一列

首页显示分类菜单

首页点击分类菜单后，显示该分类下的电子书

遇到的问题：

1. 分类表中的初始化数据，id都为三位，如100，可是在表格中的代表当前行数据的record，其id却似乎被截断了，变为了1，2，很奇怪，找了半天，不知道是哪里发生了转换。后来才发现，是自己的电子书表中，初始化category1_id和category2_id时，是随便填的2和3。还是数据的问题。
2. 



## 8

创建文档表，并生成持久层代码

完成前后端文档表的增删改查功能

编辑时父文档选择框改为树形选择组件

1. 目前无论编辑还是新增文档，其父文档只能选择一级分类。原因是文档页面的代码是从分类管理页面复制过来的，使用的<a-select>组件取值就是doc.parent，而文档却是支持无限层级的。

   父文档选择可改为树形选择组件。

2. 编辑状态下，选择父节点时，可以选择当前节点的子节点，彼此为父，在递归生成树时，两个节点都不会被读取出来。

   要解决这个问题，就要在每次加载编辑框时，将当前节点及其子节点的disabled属性设置true，保证其不可选，为此要引入递归方法。但要注意的一点是，我们此时就不能再使用原来的树level1，因为编辑任意一个文档时，要禁用的节点都是不一样的，如果还使用level1，每次点击编辑都禁用当前节点及子节点，那所有文档的diabled属性都会被设置为true。为解决这个问题，就需要引入新的变量，每次点击编辑按钮，要设置diabled属性时，都复制一份level1的数据，再去修改备份中的数据，这样就能保证，每次在父分类的选择中都只禁用当前文档及其子文档。

3. 选择父节点时，没有“无”的选项，即无法作为顶级节点。

   每次新增与编辑时，都在父节点的数组的首位置插入一个id为0，name为无的选项。 

完善新增文档的功能

1. 新增文档时，由于没有ebook_id，导致校验不通过，无法新增。编辑时，由于数据是从数据库拿过来的，所以没有这个问题。

   电子书管理页面点击文档管理时，要将电子书id作为参数，传递给文档编辑页面，新增时就可以拿到这个ebook_id。

完善删除文档的功能，支持文档下的所有子文档同时删除，并在删除给予二次提示

集成富文本插件wangeditor

文档内容表的设计与代码生成

- 分表是什么？为什么要分表？

文档管理页面布局修改，引入栅格