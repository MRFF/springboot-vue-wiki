## 2

### 开发第一个HelloWorld接口

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