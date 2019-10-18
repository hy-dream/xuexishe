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


public class subjectTest {

    @BeforeAll
    public static void setUp(){
        RestAssured.baseURI ="http://172.18.82.35/";
    }

    @Test
    @DisplayName("测试获取所有的专题并依次查看图谱")
    @Tag("demo")
    @Order(2)
    void TestGetAllSubjectAndCheck(){
        System.out.println("step1：获取所有的专题ID");
        List<Integer> idList=getAllEntityWords();
        System.out.println("step2：依次查看每个专题");
        searchEntityWord(idList);
    }


    @Step("第一步：获取所有的专题ID")
    private List<Integer> getAllEntityWords(){
        JSONObject body=new JSONObject();
        body.put("pageNum",1);
        body.put("pageSize",60);

        String content=given().contentType("application/json").body(body).when().post("qa/rest/specialsubject/pageSpecialSubject").
                then().extract().response().body().asString();
        //System.out.println(content);
        List<Integer> idList = (List<Integer>) JSONPath.read(content, "$.data.source[*].id");
        return idList;
    }

    @Step("第二步：依次查看每个专题")
    private void searchEntityWord(List<Integer> idList){
        for(int id:idList){
            given().contentType("application/json").param("id",id).when().get("qa/rest/qa/getGraphBySpecialSubject").
                    then().body("links",not(hasSize(0)))
                    .body("nodes",not(hasSize(0)));
        }
    }

}
