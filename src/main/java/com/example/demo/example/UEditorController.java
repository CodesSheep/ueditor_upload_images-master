package com.example.demo.example;

import com.example.demo.ActionEnter;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by ldb on 2017/4/9.
 */
@Controller
public class UEditorController {


    @RequestMapping("/")
    private String showPage(){
        return "index";
    }

    @RequestMapping(value="/config")
    public void config(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        response.setContentType("application/json");
        String rootPath = request.getSession().getServletContext().getRealPath("/");
        try {
            String exec = new ActionEnter(request, rootPath).exec();
            System.out.println(exec);
            Map mapTypes = JSON.parseObject(exec);
            /*System.out.println("这个是用JSON类的parseObject来解析JSON字符串!!!");
            for (Object obj : mapTypes.keySet()){
                System.out.println("key为："+obj+"值为："+mapTypes.get(obj));
            }*/
            String fileurl= "http://localhost:8088"+ mapTypes.get("url");
            System.out.println(fileurl);
            PrintWriter writer = response.getWriter();
            writer.write(exec);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
