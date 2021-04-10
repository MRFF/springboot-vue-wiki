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





