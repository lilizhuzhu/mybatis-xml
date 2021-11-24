package org.example.demo.controller;

import com.google.common.base.Charsets;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.example.demo.common.DbCodeEnum;
import org.example.demo.common.SqlQueryRequest;
import org.example.demo.config.nacos.ALLSQL;
import org.example.demo.mapper.a.CommonAMapper;
import org.example.demo.mapper.b.CommonBMapper;
import org.example.demo.util.MyBatisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/12
 */
@RestController
public class TestController {



    @GetMapping("findAll")
    public Object findAll() {
        return ALLSQL.finaAll();
    }


    @GetMapping("/find/{key}")
    public Object find(@PathVariable String key) {
        return ALLSQL.findByKey(key);
    }
    @GetMapping("/find/{key}/{curd}")
    public Object find(@PathVariable String key, @PathVariable String curd) {
        return ALLSQL.findByKey(key,curd);
    }
    @GetMapping("/find/{key}/{curd}/{id}")
    public String find(@PathVariable String key, @PathVariable String curd, @PathVariable String id) {
        return ALLSQL.findByKey(key, curd, id);
    }

    @PostMapping("fileUpload")
    public String fileUpload(MultipartFile file) throws Exception {

        CommonsMultipartFile commonsMultipartFile = ((CommonsMultipartFile) file);
        FileItem fileItem =  commonsMultipartFile.getFileItem();
        File storeLocation = ((DiskFileItem) fileItem).getStoreLocation();
        return FileUtils.readFileToString(storeLocation);
    }

    @PostMapping("/run/{dbCode}/{key}/{curd}/{id}")
    public Object run(@PathVariable String dbCode, @PathVariable String key, @PathVariable String curd, @PathVariable String id, @RequestBody Map<String, Object> map) {
        String sql = ALLSQL.findByKey(key, curd, id);
        if (StringUtils.isNotBlank(sql)) {
            SqlQueryRequest sqlQueryRequest = new SqlQueryRequest();
            sqlQueryRequest.setSql(MyBatisUtil.parseDynamicXMLFormXmlStr(sql, map));

            DbCodeEnum dbCodeEnum = DbCodeEnum.getEnumByName(dbCode);
            if (dbCodeEnum==null){
                return "dbCode错误";
            }
            return dbCodeEnum.getCommonMapper().sqlQueryByCondition(sqlQueryRequest);
        }
        return sql;
    }
}
