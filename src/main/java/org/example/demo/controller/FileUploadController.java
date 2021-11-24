package org.example.demo.controller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/24
 */
@RestController
@RequestMapping("upload")
public class FileUploadController {
    @PostMapping("fileUpload")
    public String fileUpload(MultipartFile file) throws Exception {
        CommonsMultipartFile commonsMultipartFile = ((CommonsMultipartFile) file);
        FileItem fileItem =  commonsMultipartFile.getFileItem();
        File storeLocation = ((DiskFileItem) fileItem).getStoreLocation();
        return FileUtils.readFileToString(storeLocation);
    }
}
