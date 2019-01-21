package com.file.controller;

import com.file.interceptor.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Administrator
 */
@Controller
public class FileManagerController {

    @Autowired
    private User user;

    @Value("${base-path}")
    private String basePath;


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
    public String index(Model model) {
        model.addAttribute("listRoots", getListFile(""));
        return "filesIndex";
    }

    @RequestMapping("/open")
    public ModelAndView open(String path) {
        File file = new File(basePath + path);
        if (file.isFile()) {
            ModelAndView model = new ModelAndView("forward:/download");
            model.addObject("path", file.getPath());
            return model;
        }

        ModelAndView modelAndView = new ModelAndView("filesIndex");
        modelAndView.addObject("listRoots", getListFile(path));

        modelAndView.addObject("paths", splitePath(path));

        return modelAndView;
    }

    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(String path) throws IOException {
        path = basePath + path;
        // 2.读取文件
        File file = new File(path);
        byte[] body = null;
        InputStream is = new FileInputStream(file);
        body = new byte[is.available()];
        is.read(body);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" + new String(file.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        HttpStatus statusCode = HttpStatus.OK;
        return new ResponseEntity<byte[]>(body, headers, statusCode);
    }


    /**
     * 获取子文件路径转义:"\"
     *
     * @param path
     * @return
     */
    private List<Map<String, Object>> getListFile(String path) {
        path = basePath + path;
        List<Map<String, Object>> result = new ArrayList<>();
        File[] listRoots;

        if (StringUtils.isEmpty(path)) {
            listRoots = File.listRoots();
        } else {
            File file = new File(path);
            listRoots = file.listFiles();
        }

        if (listRoots != null) {
            for (File listRoot : listRoots) {
                Map<String, Object> map = new HashMap<>();
                String newPath = listRoot.getPath().replace('\\', '/');
                BasicFileAttributes basicFileAttributes = null;
                try {
                    basicFileAttributes = Files.readAttributes(listRoot.toPath(), BasicFileAttributes.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                map.put("name", listRoot.getName());
                map.put("path", newPath.startsWith(basePath) ? newPath.substring(basePath.length()) : newPath);
                map.put("size", format(listRoot.length(), 2));
                if (basicFileAttributes != null) {
                    map.put("lastModifiedTime", LocalDateTime.ofInstant(basicFileAttributes.lastModifiedTime().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                map.put("isDir", listRoot.isDirectory());
                result.add(map);
            }
        }


        return result;
    }

    private static String format(double bytes, int digits) {
        String[] dictionary = {"Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int index;
        for (index = 0; index < dictionary.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        return String.format("%." + digits + "f", bytes) + " " + dictionary[index];
    }

    private Map<String, Object> splitePath(String path) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (path.contains(File.separator)) {
            String[] split = path.split(File.separator);
            for (int i = 0; i < split.length; i++) {
                StringBuilder paths = new StringBuilder();
                for (int i1 = 0; i1 < split.length; i1++) {
                    if (i1 <= i) {
                        paths.append(split[i1]).append(File.separator);
                    }
                }
                map.put(split[i], paths.toString());
            }
        } else {
            map.put(path, path);
        }
        return map;
    }

}



