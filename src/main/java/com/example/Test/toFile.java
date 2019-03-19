package com.example.Test;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

public class toFile {

    public static File analyzeFile(MultipartFile file, HttpServletRequest request) throws IOException {
        File tempFile=null;
        if (!file.isEmpty()) {
            String filePath = request.getSession().getServletContext().getRealPath("/") + "upload/";
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            String path = filePath + file.getOriginalFilename();
            //save to the /upload path

                tempFile = new File(path);
             FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);

        }
        return tempFile;
    }
}
