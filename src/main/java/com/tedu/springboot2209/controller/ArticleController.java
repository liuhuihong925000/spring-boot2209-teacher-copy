package com.tedu.springboot2209.controller;

import com.tedu.springboot2209.entity.Article;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Controller
public class ArticleController {
    //用来表示保存所有文章信息的目录:articles
    private static File articleDir;

    static {
        articleDir = new File("./articles");
        if(!articleDir.exists()){
            articleDir.mkdirs();
        }
    }
    @RequestMapping("/writeArticle")
    public void writeArticle(HttpServletRequest request, HttpServletResponse response){
        System.out.println("开始处理发表文章");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");
        System.out.println(title+","+author+","+content);
        if(title==null||title.isEmpty()||author==null||author.isEmpty()||
           content==null||content.isEmpty()){
            try {
                response.sendRedirect("/article_fail.html");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = new File(articleDir,title+".obj");
        Article article = new Article(title,author,content);

        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            oos.writeObject(article);
            response.sendRedirect("/article_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
