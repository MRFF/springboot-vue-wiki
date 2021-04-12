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

#### 如何继承热部署？

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

#### 如何使用mybatis-generator插件自动生成代码？

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

前端要增加一个页面，要做以下几步：

1. 创建页面，即在views文件夹下创建vue文件。
2. 编写路由，即在router文件夹内的文件中添加路由映射。
3. 添加跳转，即在页面中添加<router-link>标签，跳转到新增页。

添加表格

使用PageHelper实现后端分页

1. 添加依赖，使用pagehelper插件（pagehelper-spring-boot-starter）。
2. 分页功能需要查询两次，首先要查询数据总数，然后查询当前页数据。
3. 每设置一次分页，只对接下来的第一个select查询语句生效。
4. 分页请求需要提供页码和每页条数，而响应需要提供当前页列表内容和总行数。因此，再为分页请求添加分页请求类，为分页响应增加分页响应类。

