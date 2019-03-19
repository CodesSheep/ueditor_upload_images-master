package com.example.demo;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.PipeOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;


import com.example.Test.Test;
import com.example.Test.toFile;
import com.example.demo.hunter.TestService;
import org.apache.commons.io.FileUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
     * 将Word文档转换成html字符串的工具类
     *
     * @author MZULE
     *
     */
@RestController
@CrossOrigin
@RequestMapping("/test")
    public class OpenOfficeUtil {
    @Autowired
    private TestService testService;
        @RequestMapping(value = "file/uploadImage",method = RequestMethod.POST)
        public  String main(@RequestParam("upload") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setCharacterEncoding("UTF-8");


             File newfile = toFile.analyzeFile(file,request);
            String a=toHtmlString(newfile,"D:/");
            OpenOfficeUtil.convert(newfile,"D:/");
            System.out.println(a);
            Test test=new Test();
            test.setContent(a);
            System.out.println(test.getContent());
            testService.insert(test);

          return   "上传成功";

        }




   /* public static String getOfficeHome() {
        String osName = System.getProperty("os.name");
        System.out.println("操作系统名称:" + osName);

        if (Pattern.matches("Linux.*", osName)) {
            return "/opt/openoffice.org3";
        } else if (Pattern.matches("Windows.*", osName)) {
            return "C:\\Program Files (x86)\\OpenOffice 4"
                    + "";
        } else if (Pattern.matches("Mac.*", osName)) {
            return "/Applications/OpenOffice.org.app/Contents/";
        }
        return null;
    }*/
    /**
     * 连接OpenOffice 并且启动OpenOffice
     * @return
     */
    /*public static OfficeManager getOfficeManager() {
        DefaultOfficeManagerConfiguration config = new DefaultOfficeManagerConfiguration();
        // 设置OpenOffice.org 3的安装目录
        config.setOfficeHome(getOfficeHome());
        config.setPortNumbers(8100); //设置转换端口，默认为8100
        config.setTaskExecutionTimeout(1000 * 60 * 5L);//设置任务执行超时为5分钟
        config.setTaskQueueTimeout(1000 * 60 * 60 * 24L);//设置任务队列超时为24小时
        // 启动OpenOffice的服务
        OfficeManager officeManager = config.buildOfficeManager();
        officeManager.start();
        return officeManager;
    }*/
        /**
         * 将word文档转换成html文档
         *
         * @param docFile
         *                需要转换的word文档
         * @param filepath
         *                转换之后html的存放路径
         * @return 转换之后的html文件
         */
        public static  File convert(File docFile, String filepath) {
            // 创建保存html的文件
            File htmlFile = new File(filepath + "/" + new Date().getTime()
                    + ".html");
           /* // 获取OpenOffice的安装路径并启动
            OfficeManager officeManager = null;
            officeManager = getOfficeManager();
            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);*/
            // 创建Openoffice连接
            OpenOfficeConnection con = new SocketOpenOfficeConnection(8100);
            OpenOfficeConnection cons =new PipeOpenOfficeConnection();
            try {
                // 连接

                con.connect();
            } catch (ConnectException e) {
                System.out.println("获取OpenOffice连接失败...");
                e.printStackTrace();
            }
            // 创建转换器
            DocumentConverter converter = new OpenOfficeDocumentConverter(con);
            // 转换文档问html
            converter.convert(docFile, htmlFile);
            // 关闭openoffice连接
            /*officeManager.stop();*/
            con.disconnect();
            return htmlFile;
        }

        /**
         * 将word转换成html文件，并且获取html文件代码。
         *
         * @param docFile
         *                需要转换的文档
         * @param filepath
         *                文档中图片的保存位置
         * @return 转换成功的html代码
         */
        public  String toHtmlString(File docFile, String filepath) {

            // 转换word文档
            File htmlFile = convert(docFile, filepath);
            // 获取html文件流
            StringBuffer htmlSb = new StringBuffer();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(htmlFile)));
                while (br.ready()) {
                    htmlSb.append(br.readLine());
                }
                br.close();
                // 删除临时文件
                htmlFile.delete();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // HTML文件字符串
            String htmlStr = htmlSb.toString();
            // 返回经过清洁的html文本

            return clearFormat(htmlStr, filepath);
        }

        /**
         * 清除一些不需要的html标记
         *
         * @param htmlStr
         *                带有复杂html标记的html语句
         * @return 去除了不需要html标记的语句
         */
        protected  String clearFormat(String htmlStr, String docImgPath) {
            // 获取body内容的正则
            String bodyReg = "<BODY .*</BODY>";
            Pattern bodyPattern = Pattern.compile(bodyReg);
            Matcher bodyMatcher = bodyPattern.matcher(htmlStr);
            if (bodyMatcher.find()) {
                // 获取BODY内容，并转化BODY标签为DIV
                htmlStr = bodyMatcher.group().replaceFirst("<BODY", "<DIV")
                        .replaceAll("</BODY>", "</DIV>");
            }
            // 调整图片地址
            htmlStr = htmlStr.replaceAll("<IMG SRC=\"", "<IMG SRC=\"" + docImgPath
                    + "/");
            // 把<P></P>转换成</div></div>保留样式
            // content = content.replaceAll("(<P)([^>]*>.*?)(<\\/P>)",
            // "<div$2</div>");
            // 把<P></P>转换成</div></div>并删除样式
            htmlStr = htmlStr.replaceAll("(<P)([^>]*)(>.*?)(<\\/P>)", "<p$3</p>");
            // 删除不需要的标签
            htmlStr = htmlStr
                    .replaceAll(
                            "<[/]?(font|FONT|span|SPAN|xml|XML|del|DEL|ins|INS|meta|META|[ovwxpOVWXP]:\\w+)[^>]*?>",
                            "");
            // 删除不需要的属性
            htmlStr = htmlStr
                    .replaceAll(
                            "<([^>]*)(?:lang|LANG|class|CLASS|style|STYLE|size|SIZE|face|FACE|[ovwxpOVWXP]:\\w+)=(?:'[^']*'|\"\"[^\"\"]*\"\"|[^>]+)([^>]*)>",
                            "<$1$2>");

            return htmlStr;
        }

    }



