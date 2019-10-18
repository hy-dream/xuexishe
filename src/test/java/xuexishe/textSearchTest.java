package xuexishe;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;


public class textSearchTest {

    @BeforeAll
    public static void setUp(){
        RestAssured.baseURI ="http://172.18.82.35/";
    }

    @ParameterizedTest
    @ValueSource(strings = { "天气", "演员的自我修养","上海世贸组织", "习近平出席美国总统奥巴马在白宫举行的欢迎仪式并致辞","给北京体育大学2016级研究生冠军班全体学生的回信" })
    @DisplayName("测试搜索非实体词")
    @Tag("demo")
    @Order(2)
    void TestNotEntityWord(String candidate){
        given().contentType("application/json").param("text",candidate).when().get("algorithmic/textSearch").
                then().body("message",equalTo("success"))
                .body("data.entityWord",hasSize(0))
                .body("data.nonEntityWord",not(hasSize(0)));
    }

    @ParameterizedTest
    @MethodSource("entityWordProvider")
    @DisplayName("获取所有的实体词并搜索")
    @Tag("demo")
    void TestGetAllEntityWordAndSearch(String word){
        //System.out.println(word);
        given().log().all().contentType("application/json").param("text", word).when().get("algorithmic/textSearch").
                then().log().all().body("message", equalTo("success"))
                .body("data.entityWord[0].title", containsString(word))
                .body("data.entityWord[0].url", startsWith("http://172.18.82.35/wd/me/zsy?name="));
        //http://172.18.82.35/wd/me/zsy?name= http://59.108.36.236/wd/me/zsy?name=
    }


     static List<String> getAllEntityWords(){
        JSONObject body=new JSONObject();
        body.put("pageNo",1);
        body.put("pageSize",600);
        body.put("queryStr","");
        body.put("superClassId","思想概念");

        String content=given().contentType("application/json").body(body).when().post("/refining/rest/ont/pageIndividualByClass").
                then().extract().response().body().asString();
        //System.out.println(content);
        // body("statusCode",equalTo(200)).log().all().body("data.totalHits",equalTo(518));
        List<String> nameList = (List<String>) JSONPath.read(content, "$.data.source[*].individualName");
        return nameList;
    }


    @ParameterizedTest
    @ValueSource(strings = {"习近平和王岐山一起参加了哪些活动","王岐山参加过哪些活动","习近平在什么会议上首次提到了中国梦",
            "如何认识党的十八大以来党和国家事业发生的历史性变革？","怎样理解我们的工作还存在许多不足，也面临不少困难和挑战？","为什么第二轮土地承包到期后再延长30年？"
    ,"为什么要坚持房子是用来住的不是用来炒的定位？","如何构建国土空间开发保护制度？","为何加强纪律建设要坚持惩前毖后治病救人，运用监督执纪“四种形态”，抓早抓小防微杜渐？"})
    @DisplayName("测试问答对")
    @Tag("demo")
    void TestQA(String qa){
        given().contentType("application/json").param("text",qa).when().get("algorithmic/textSearch").
                then().body("message",equalTo("success"))
                .body("data.answer",is(notNullValue()));
    }

    static Stream<String> entityWordProvider() {
        List<String> result=getAllEntityWords();
        if(result==null){
            result=getAllEntityWords();
        }
        return Stream.of(result.toString().replace("[","").replace("]","").replace("\"","").split(","));
    }

}
