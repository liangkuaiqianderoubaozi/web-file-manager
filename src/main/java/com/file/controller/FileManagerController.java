package com.file.controller;

import com.file.iInterceptor.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@Controller
public class FileManagerController {

    @Autowired
    private User user;

    @RequestMapping("/login")
    public ModelAndView login(String username, String password, HttpServletRequest request) {
        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
            request.getSession().setAttribute("certification", true);
            return new ModelAndView("forward:/");
        } else {
            return new ModelAndView("login.html");
        }
    }


    @RequestMapping("/")
    public ModelAndView index() throws IOException {

        ModelAndView modelAndView = new ModelAndView("filesIndex");
        modelAndView.addObject("listRoots", getListFile("/Users/mogu/Desktop/"));
        return modelAndView;
    }

    @RequestMapping("/open")
    public ModelAndView open(String path) {
        File file = new File(path);
        if (file.isFile()) {
            ModelAndView model = new ModelAndView("forward:/download");
            model.addObject("path", file.getPath());
            return model;
        }

        ModelAndView modelAndView = new ModelAndView("open");
        modelAndView.addObject("files", getListFile(path));

        return modelAndView;
    }

    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(String path) throws IOException {
        // 2.读取文件
        File file = new File(path);
        byte[] body = null;
        InputStream is = new FileInputStream(file);
        body = new byte[is.available()];
        is.read(body);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" +  new String(file.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, statusCode);
        return entity;
    }


    /**
     * 获取子文件路径转义:"\"
     *
     * @param path
     * @return
     */
    private List<String> getListFile(String path) {
        File[] listRoots;

        if (path == null) {
            listRoots = File.listRoots();
        } else {
            File file = new File(path);
            listRoots = file.listFiles();
        }

        List<String> paths = new ArrayList<>();
        if (listRoots != null) {
            for (File listRoot : listRoots) {
                paths.add(listRoot.getPath().replace('\\', '/'));
            }
        }


        return paths;
    }


}



