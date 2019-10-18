package xuexishe;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class documentTest {
    @BeforeAll
    public static void setUp(){
        RestAssured.baseURI ="http://172.18.82.35/";
    }

    @Test
    @Tag("demo")
    @DisplayName("获取所有的原文原著并依次查看")
    void TestYWYX(){
        System.out.println("step1：获取所有的文章ID");
        List<Integer> ids=getAllDocument("ywyz");
        System.out.println("step2：依次查看每个文章");
        searchDocument(ids);
    }

    @Step("第一步：获取所有的文章ID")
    private List<Integer> getAllDocument(String type){
        JSONObject body=new JSONObject();
        body.put("pageNum",1);
        body.put("pageSize",500);
        body.put("docChannel",type);
        body.put("classification","");

        String content=given().contentType("application/json").body(body).when().post("qa/rest/qa/pageResource").
                then().extract().response().body().asString();
        //System.out.println(content);
        List<Integer> idList = (List<Integer>) JSONPath.read(content, "$.data.source[*].docId");
        return idList;
    }

    @Step("第二步：依次查看每个文章")
    private void searchDocument(List<Integer> idList){
        for(int id:idList){
            //System.out.println(id);
            given().contentType("application/json").param("id",id).when().get("qa/rest/qa/resourceDocument").
                    then().body("data.docId",equalTo(id))
                    .body("message",equalTo("success"));
        }
    }

    @Test
    @Tag("demo")
    @DisplayName("获取所有的论述摘编并依次查看")
    void TestLSZB(){
        System.out.println("step1：获取所有的论述摘编ID");
        List<Integer> ids=getAllDocument("lszb");
        System.out.println("step2：依次查看论述摘编");
        searchDocument(ids);
    }

    @Test
    @Tag("demo")
    @DisplayName("获取所有的活动并依次查看")
    void TestHD(){
        System.out.println("step1：获取所有的活动ID");
        List<Integer> ids=getAllDocument("hd");
        System.out.println("step2：依次查看每个活动");
        searchDocument(ids);
    }


    @Step("第一步：获取所有的金句ID")
    private List<Integer> getAllJinJu(){
        JSONObject body=new JSONObject();
        body.put("pageNum",1);
        body.put("pageSize",500);

        String content=given().log().all().contentType("application/json").body(body).when().post("qa/rest/qa/pageXiSentence").
                then().extract().response().body().asString();
        //System.out.println(content);
        List<Integer> idList = (List<Integer>) JSONPath.read(content, "$.data.source[*].id");
        return idList;
    }

    @Step("第二步：查看每篇金句")
    private void searchJinJu(List<Integer> idList){
        for(int id:idList){
            given().contentType("application/json").param("id",id).when().get("qa/rest/qa/goldSentenceDetail").
                    then().body("data.id",equalTo(id))
                    .body("message",equalTo("success"));
        }
    }

    @Test
    @Tag("demo")
    @DisplayName("获取所有的金句并依次查看")
    void TestJinJu(){
        System.out.println("step1：获取所有的金句ID");
        List<Integer> ids=getAllJinJu();
        System.out.println("step2：依次查看金句");
        searchJinJu(ids);
    }

}
