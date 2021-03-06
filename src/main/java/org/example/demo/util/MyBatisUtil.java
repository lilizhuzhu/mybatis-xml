package org.example.demo.util;


import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.apache.ibatis.session.Configuration;

import org.example.demo.common.IdLabelType;
import org.example.demo.common.MapperNameSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class MyBatisUtil {
    private static final Logger log = LoggerFactory.getLogger(MyBatisUtil.class);
    private static final Configuration configuration = new Configuration();
    private static final EntityResolver ENTITY_RESOLVER = new XMLMapperEntityResolver();
    private static DocumentBuilder documentBuilder;
    private static final String PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">";

    static {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        try {
            documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }


    /**
     * ?????????????????????????????????sql,????????????mybatis???????????????
     * ?????????????????????????????????mybatis ?????????
     * ???????????????????????????????????? {@link  org.apache.ibatis.scripting.defaults.DefaultParameterHandler#setParameters(java.sql.PreparedStatement)}
     *
     * @param xmlSQL          eg:  <select> mybatisXML sql ??????</select>
     * @param parameterObject ???????????????
     * @return ????????????sql ??????
     */
    public static String parseDynamicXMLFormXmlStr(String xmlSQL, Object parameterObject) {

        //log.info("??????sqlXml:{} , params:{}", xmlSQL, JSON.toJSONString(parameterObject));
        //?????????xml
        XPathParser xPathParser = new XPathParser(xmlSQL, false);
        XNode xNode = xPathParser.evalNode("select|insert|update|delete");

        // ????????????????????? ?????????????????? XMLScriptBuilder ??????,
        XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, xNode);
        //?????? ??????xml ????????????xml
        SqlSource sqlSource = xmlScriptBuilder.parseScriptNode();

        MappedStatement ms = new MappedStatement.Builder(configuration, xNode.getStringAttribute("id"), sqlSource, null).build();
        //?????????sql ??? ????????????
        BoundSql boundSql = ms.getBoundSql(parameterObject);

        String executeSql = getExecuteSql(boundSql);
        //????????? sql ??????????????????
        return removeExtraWhitespaces(executeSql);
    }

    /**
     * ?????????????????????
     *
     * @param original
     * @return
     */
    public static String removeExtraWhitespaces(String original) {
        if (StringUtils.isBlank(original)) {
            return StringUtils.EMPTY;
        }
        StringTokenizer tokenizer = new StringTokenizer(original);
        StringBuilder builder = new StringBuilder();
        boolean hasMoreTokens = tokenizer.hasMoreTokens();
        while (hasMoreTokens) {
            builder.append(tokenizer.nextToken());
            hasMoreTokens = tokenizer.hasMoreTokens();
            if (hasMoreTokens) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    /**
     * ?????????????????????sql ??? ????????? ??????
     *
     * @param boundSql
     * @return
     */
    public static String getExecuteSql(BoundSql boundSql) {
        if (boundSql == null) {
            log.error("sql ");
            return null;
        }
        //?????? ??????????????? sql
        String resultSql = boundSql.getSql();
        //???'  ???  '???"  ???  " ????????? ???
        String executeSql = resultSql.replaceAll("(\'\\s*\\?\\s*\')|(\"\\s*\\?\\s*\")", "?");
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (boundSql.getParameterObject() == null) {
                        value = null;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(boundSql.getParameterObject());
                        value = metaObject.getValue(propertyName);
                    }
                    executeSql = StringUtils.replaceOnce(executeSql, "?", value instanceof String ? "'" + value + "'" : String.valueOf(value));
                }
            }
        }
        return executeSql;
    }


    public static void main(String[] args) throws Exception {
        String fileUrl="/Users/nmy/Desktop/self_work/mybatis-xml-test/src/main/java/org/example/demo/testfile/sql.xml";
        String mapperXml = FileUtils.readFileToString(new File(fileUrl), StandardCharsets.UTF_8.name());

        XPathParser xPathParser = new XPathParser(PREFIX + mapperXml, true, configuration.getVariables(), ENTITY_RESOLVER);
        XNode mapperNode = xPathParser.evalNode("/mapper");
        String stringByNode = NodeUtil.nodeToString(mapperNode.getNode(),false);
        System.out.println(stringByNode);
    }

    public static MapperNameSpace getYSMapper(String allXml) {
        if (StringUtils.isBlank(allXml)) {
            return null;
        }
        XPathParser xPathParser = new XPathParser(PREFIX + allXml, true, configuration.getVariables(), ENTITY_RESOLVER);
        XNode mapperNode = xPathParser.evalNode("/mapper");
        String namespace = mapperNode.getStringAttribute("namespace");
        if (namespace == null || namespace.isEmpty()) {
            throw new BuilderException("Mapper's namespace cannot be empty");
        }

        List<XNode> curdNodeList = mapperNode.evalNodes("select|insert|update|delete");
        List<XNode> sqlNodes = mapperNode.evalNodes("sql");
        Map<String, String> sqlNodeMap = Maps.newHashMap();
        if (sqlNodes != null) {
            sqlNodes.stream().forEach(s -> {
                sqlNodeMap.put(s.getStringAttribute("id"), NodeUtil.nodeToString(s.getNode(),false));
            });
        }
        MapperNameSpace mapperNameSpace = new MapperNameSpace();
        mapperNameSpace.setNamespace(namespace);
        curdNodeList.stream().forEach(curd -> {
            String id = curd.getStringAttribute("id");
            String mapperStr = NodeUtil.addSqlTagToNode(curd.getNode(), sqlNodeMap);
            mapperNameSpace.putIdXml(IdLabelType.valueOf(curd.getName().toUpperCase(Locale.ROOT)), id, mapperStr);
        });
        mapperNameSpace.putIdXml(IdLabelType.SQL,sqlNodeMap);
        return mapperNameSpace;
    }


}


