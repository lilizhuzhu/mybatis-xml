package org.example.demo.util;


import cn.hutool.extra.spring.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SqlRunner;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.UUID;


public class MyBatisUtil {
    private static final Logger log = LoggerFactory.getLogger(MyBatisUtil.class);
    private static final Configuration configuration = new Configuration();
    private static DocumentBuilder documentBuilder;

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
     * 该方法主要用来解析动态sql,可以使用mybatis的所有标签
     * 解析和赋值的方式都是由mybatis 完成的
     * 赋值绑定几乎完全使用该类 {@link  org.apache.ibatis.scripting.defaults.DefaultParameterHandler#setParameters(java.sql.PreparedStatement)}
     *
     * @param xmlSQL          eg:  <select> mybatisXML sql 语句</select>
     * @param parameterObject 对应的参数
     * @return 解析后的sql 语句
     */
    public static String parseDynamicXMLFormXmlStr(String xmlSQL, Object parameterObject) {

        //log.info("原始sqlXml:{} , params:{}", xmlSQL, JSON.toJSONString(parameterObject));
        //解析成xml
        Document doc = parseXMLDocument(xmlSQL);
        if (doc == null) {
            return null;
        }
        //走mybatis 流程 parse成Xnode
        XNode xNode = new XNode(new XPathParser(doc, false), doc.getFirstChild(), null);
        // 之前的所有步骤 都是为了构建 XMLScriptBuilder 对象,
        XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, xNode);

        //解析 静态xml 和动态的xml
        SqlSource sqlSource = xmlScriptBuilder.parseScriptNode();
        MappedStatement ms = new MappedStatement.Builder(configuration, UUID.randomUUID().toString(), sqlSource, null).build();
        //将原始sql 与 参数绑定
        BoundSql boundSql = ms.getBoundSql(parameterObject);

        String executeSql = getExecuteSql(boundSql);
        //格式化 sql 移除多余空格
        return removeExtraWhitespaces(executeSql);
    }
    public static String removeExtraWhitespaces(String original) {
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
     * 获得最后执行的sql 将 ？变为 参数
     *
     * @param boundSql
     * @return
     */
    public static String getExecuteSql(BoundSql boundSql) {
        if (boundSql == null) {
            log.error("sql ");
            return null;
        }
        //获得 预编译后的 sql
        String resultSql = boundSql.getSql();
        //将'  ？  '和"  ？  " 替换为 ？
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


    private static Document parseXMLDocument(String xmlString) {
        if (StringUtils.isBlank(xmlString)) {
            log.error("动态解析的xmlString 不能为空!!");
            return null;
        }
        try {
            return documentBuilder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            log.error("XML解析异常,请检查XML格式是否正确,errMsg:{}", e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String sql = "<mapper><select uop=\"李四\" id=\"selectAll\">\n" +
                "select * from people \n" +
                "<where>\n" +
                "<if test=\"name!=null and name!=''\">\n" +
                "  and name=#{name}\n" +
                "</if>\n" +
                "<if test=\"age!=null\">\n" +
                "  and age=#{age}\n" +
                "</if>\n" +
                "\n" +
                "</where>\n" +
                "</select></mapper>";
        Document document = documentBuilder.parse(new InputSource(new StringReader(sql)));
        NodeList selectList = document.getElementsByTagName("select");
        Map<String, String> idAndSql = getIdAndXmlSql(selectList);
        System.out.println(idAndSql);


    }

    /**
     * <select, <id,sql>>
     *
     * @param allXml
     * @return
     */
    public static Map<String, Map<String, String>> selectParseXML(String allXml) {
        if (StringUtils.isBlank(allXml)) {
            log.error("动态解析的xmlString 不能为空!!");
            return null;
        }
        try {
            Map<String, Map<String, String>> map = new HashMap<>();
            Document document = documentBuilder.parse(new InputSource(new StringReader(allXml)));
            NodeList selectList = document.getElementsByTagName("select");
            NodeList updateList = document.getElementsByTagName("update");
            NodeList deleteList = document.getElementsByTagName("delete");
            NodeList insertList = document.getElementsByTagName("insert");
            map.put("select", getIdAndXmlSql(selectList));
            map.put("update", getIdAndXmlSql(updateList));
            map.put("delete", getIdAndXmlSql(deleteList));
            map.put("insert", getIdAndXmlSql(insertList));
            return map;
        } catch (Exception e) {
            log.error("XML解析异常,请检查XML格式是否正确,errMsg:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 获得xml 中 父节点参数id 对应的原始Xml字符串
     *
     * @param nodeList
     * @return
     */
    private static Map<String, String> getIdAndXmlSql(NodeList nodeList) {
        int length = nodeList.getLength();
        if (length > 0) {
            Map<String, String> child = new HashMap<>(length);
            for (int i = 0; i < length; i++) {
                Node item = nodeList.item(i);
                NamedNodeMap attributes = item.getAttributes();
                if (attributes != null) {
                    Node node = attributes.getNamedItem("id");
                    if (node != null) {
                        if (StringUtils.isNoneEmpty(node.getNodeValue(), item.getTextContent())) {
                            // id , 把整个xml字符串 加入
                            child.put(node.getNodeValue(), getNodeAll(item));
                        }
                    }
                }
            }
            return child;
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * <select id="ddd" op="ooo"></>
     * 获得某个节点的 参数
     *
     * @param node
     * @return id="ddd" op="ooo"
     */
    private static String getNodeParameter(Node node) {
        StringBuilder parameter = new StringBuilder();
        if (Objects.nonNull(node) && node.hasAttributes()) {
            if (node.hasAttributes()) {
                NamedNodeMap attributes = node.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Node attribute = attributes.item(j);
                    if (Objects.nonNull(attribute)) {
                        String attributeNodeValue = attribute.getNodeValue();
                        String attributeNodeName = attribute.getNodeName();
                        if (StringUtils.isNoneBlank(attributeNodeName, attributeNodeValue)) {
                            parameter.append(" " + attributeNodeName + "=\"" + attributeNodeValue + "\"");
                        }
                    }
                }
            }
        }
        return parameter.toString();
    }

    /**
     * 获得某个节点 原始(解析前)的xml字符串
     *
     * @param node
     * @return
     */
    private static String getNodeAll(Node node) {
        StringBuilder xmlStr = new StringBuilder();

        //判断是否为节点
        if (node.hasAttributes()) {
            xmlStr.append("<" + node.getNodeName() + getNodeParameter(node) + ">");
        }
        NodeList childNodes = node.getChildNodes();
        //判断如果有子节点
        if (childNodes != null) {
            //遍历子节点 从
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                //判断是否为 文本节点
                if (item.hasAttributes()) {
                    xmlStr.append(getNodeAll(item));
                } else {
                    xmlStr.append(item.getTextContent());
                }
            }
        }
        if (node.hasAttributes()) {
            xmlStr.append("</" + node.getNodeName() + ">");
        }
        return removeExtraWhitespaces(xmlStr.toString());
    }

    public static SqlRunner getSqlRunner(String dataSourceName) {
        Map<String, DataSource> beansOfType = SpringUtil.getBeansOfType(DataSource.class);
        if (beansOfType != null && beansOfType.size() > 0) {
            DataSource dataSource = beansOfType.get(dataSourceName);
            if (dataSource != null) {
                try {
                    return new SqlRunner(dataSource.getConnection());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}


