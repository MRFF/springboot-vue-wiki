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