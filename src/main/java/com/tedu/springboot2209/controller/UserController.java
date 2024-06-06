package com.tedu.springboot2209.controller;

import com.tedu.springboot2209.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 我们定义的用于处理某个请求的类(某个Controller类)都需要在类上添加注解@Controller
 * 这样SpringMVC框架才能识别这个类
 */
@Controller
public class UserController {
    //用来表示保存所有用户信息的目录:users
    private static File userDir;

    static {
        userDir = new File("./users");
        if(!userDir.exists()){
           userDir.mkdirs();
        }
    }

    @RequestMapping("/userList.html")
    public void userList(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理动态页面");
        /*
            1:将users目录中的所有obj文件获取到
            2:将这些文件逐一反序列化这样会得到一组User对象
            3:拼接一个HTML页面内容，并将这组用户信息拼接到表格里
            4:发送
         */
        //1
        File[] subs = userDir.listFiles(f->f.getName().endsWith(".obj"));
        //2
        List<User> userList = new ArrayList<>();
        for(File sub : subs){
            try (
                    FileInputStream fis = new FileInputStream(sub);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ){
               User user = (User)ois.readObject();
               userList.add(user);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        //3
        try {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("<meta charset=\"UTF-8\">");
            pw.println("<title>用户列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<center>");
            pw.println("<h1>用户列表</h1>");
            pw.println("<table border=\"1\">");
            pw.println("<tr>");
            pw.println("<td>用户名</td>");
            pw.println("<td>密码</td>");
            pw.println("<td>昵称</td>");
            pw.println("<td>年龄</td>");
            pw.println("</tr>");

            for(User user : userList) {
                pw.println("<tr>");
                pw.println("<td>" + user.getUsername() + "</td>");
                pw.println("<td>" + user.getPassword() + "</td>");
                pw.println("<td>" + user.getNickname() + "</td>");
                pw.println("<td>" + user.getAge() + "</td>");
                pw.println("</tr>");
            }

            pw.println("</table>");
            pw.println("</center>");
            pw.println("</body>");
            pw.println("</html>");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @RequestMapping("/loginUser")
    public void login(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理登录!");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username+","+password);
        if(username==null||username.isEmpty()||password==null||password.isEmpty()){
            try {
                response.sendRedirect("/login_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        //根据登录用户的用户名去users目录下提取该obj文件读取曾经的注册信息
        File file = new File(userDir,username+".obj");
        if(file.exists()){//如果该文件存在，说明此次登录的用户是一个注册用户
            //反序列化文件中曾经该用户的注册信息
            try (
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ){
                User user = (User)ois.readObject();
                //用注册时用户的密码和本次登录用户输入的密码比对
                if(user.getPassword().equals(password)){//一致则登录成功
                    //登录成功
                    response.sendRedirect("/login_success.html");
                    return;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        //登录失败
        try {
            response.sendRedirect("/login_fail.html");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @RequestMapping("/regUser")
    public void reg(HttpServletRequest request, HttpServletResponse response){
        /*
            请求对象HttpServletRequest:用来表示浏览器提交上来的所有内容
            响应对象HttpServletResponse:用来表示服务器要给浏览器回复的内容
         */

        /*
            1:通过request对象获取到注册页面上表单提交的注册信息
            2:将该注册用户信息保存
            3:通过设置response响应注册结果页面
         */
        //1
        /*
            HttpServletRequest提供了获取参数的方法:
            String getParameter(String name)
            参数就是传入的为要获取的表单提交上来的信息，对应的就是输入框的名字

            该方法会返回两种特殊值，一个是空字符串，一个是null
            例如:获取用户名这个输入框的内容时
            如果该输入框中用户没有输入任何内容，此时浏览器提交时格式如下
            /regUser?username=&password=123456&nickname=chuanqi&age=22
            此时获取username的值时返回的就是【空字符串】:
            String username = request.getParameter("username");//返回空字符串


            如果浏览器提交参数时不含有用户名(或者获取参数时指定的参数名不一致):
            /regUser?password=123456&nickname=chuanqi&age=22
            String username = request.getParameter("username");//没有这个参数
            此时获取username的值时返回的就是【null】

            /regUser?usernamename=fan&password=123456&nickname=chuanqi&age=22
            String username = request.getParameter("username");//没有这个参数(参数名不一致)
            此时获取username的值时返回的就是【null】
         */
        //对应的是reg.html页面上<input name="username">
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        //必要验证工作

        User user = new User(username,password,nickname,new Integer(ageStr));
        try (
                FileOutputStream fos = new FileOutputStream(username);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            oos.writeObject(user);
            //使用响应对象要求浏览器查看注册成功页面
            response.sendRedirect("/reg_success.html");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
