package org.example.demo.util;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;

import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import com.sun.org.apache.xerces.internal.dom.DeferredTextImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.SqlSourceBuilder;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        log.info("原始sqlXml:{} , params:{}", xmlSQL, JSON.toJSONString(parameterObject));
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
                    } else if (parameterObject == null) {
                        value = null;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    executeSql = executeSql.replaceFirst("[?]", value instanceof String ? "'" + value + "'" : String.valueOf(value));
                }
            }
        }
        //格式化 sql 移除多余空格
        log.info("removeExtraWhitespace -> executeSql: {}", SqlSourceBuilder.removeExtraWhitespaces(executeSql));
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
        String sql = "<mapper>\n" +
                "\n" +
                " <select id=\"catEyeAbnormalOrderManageExportAdbSqlNewOrderFor363000\">\n" +
                "        select trade_id 交易单号,\n" +
                "        scp_order_code 履约单号,\n" +
                "        fwd_mail_no_list 正向运单号,\n" +
                "        fwd_lbx_order_code 发货LBX单号,\n" +
                "        rev_lbx_order_code 退货LBX单号,\n" +
                "        rev_mail_no 逆向运单号,\n" +
                "        excp_order_code 异常编号,\n" +
                "        concat( select type_name from ascp_abnormal_type where type_code = abnormal_type_level1,\n" +
                "        select type_name from ascp_abnormal_type where type_code=abnormal_type_level2,\n" +
                "        \"\\/ \",\n" +
                "        select type_name from ascp_abnormal_type where type_code=abnormal_type_level3) 异常类型,\n" +
                "        error_desc 异常描述,\n" +
                "        excp_status 异常状态,\n" +
                "        gmt_create 异常发生时间,\n" +
                "        trade_pay_time 交易支付时间,\n" +
                "        scp_biz_order_status 订单状态,\n" +
                "        receiver_address 收货地址,\n" +
                "        call_cco 消费者是否进线,\n" +
                "        active_service CCO是否主动服务,\n" +
                "        event_list 关联事件,\n" +
                "        buyer_id 买家ID,\n" +
                "        store_code 发货仓,\n" +
                "        operate_status 人工处理状态,\n" +
                "        attributes 处理备注,\n" +
                "        time_sensitive 消费者时效敏感,\n" +
                "        complain_sensitive 消费者是否投诉敏感,\n" +
                "        province_name 省,\n" +
                "        city_name 市,\n" +
                "        model_type 新老类型,\n" +
                "        area_name 区,\n" +
                "        attributes 扩展信息\n" +
                "        from dwd_ascp_uop_excp_order\n" +
                "        where 1=1\n" +
                "        <if test=\"orderCode!=null and orderCode!=''\">\n" +
                "            and (fwd_lbx_order_code = #{orderCode} or trade_id = #{orderCode} or rev_lbx_order_code = #{orderCode} or\n" +
                "            scp_order_code = #{orderCode})\n" +
                "        </if>\n" +
                "        <if test=\"excpStatuss!=null\">\n" +
                "            and excp_status in (${excpStatuss})\n" +
                "        </if>\n" +
                "        <if test=\"abnormalOrderType!=null\">\n" +
                "            and excp_type in (${abnormalOrderType})\n" +
                "        </if>\n" +
                "        <if test=\"abnormalTimeFrom!=null\">\n" +
                "            and gmt_create >= #{abnormalTimeFrom}\n" +
                "        </if>\n" +
                "        <if test=\"abnormalTimeTo!=null\">\n" +
                "            and gmt_create &lt;= #{abnormalTimeTo}\n" +
                "        </if>\n" +
                "        <if test=\"payTimeFrom!=null\">\n" +
                "            and trade_pay_time >= #{payTimeFrom}\n" +
                "        </if>\n" +
                "        <if test=\"payTimeTo!=null\">\n" +
                "            and trade_pay_time &lt;= #{payTimeTo}\n" +
                "        </if>\n" +
                "        <if test=\"orderStatus!=null\">\n" +
                "            and excp_status = #{orderStatus}\n" +
                "        </if>\n" +
                "        <if test=\"store_code!=null\">\n" +
                "            and store_code = #{storeCode}\n" +
                "        </if>\n" +
                "        <if test=\"spCode!=null\">\n" +
                "            and sp_code = #{spCode}\n" +
                "        </if>\n" +
                "        <if test=\"tmsCode!=null\">\n" +
                "            and tms_code = #{tmsCode}\n" +
                "        </if>\n" +
                "        <if test=\"incomeLine!=null\">\n" +
                "            and call_cco =#{incomeLine}\n" +
                "        </if>\n" +
                "        <if test=\"shopId!=null\">\n" +
                "            and seller_id in (${shopId})\n" +
                "        </if>\n" +
                "        <if test=\"abnormalTypeLevel1!=null\">\n" +
                "            and abnormal_type_level1 in (${abnormalTypeLevel1})\n" +
                "        </if>\n" +
                "        <if test=\"abnormalTypeLevel2!=null\">\n" +
                "            abnormal_type_level2 in (${abnormalTypeLevel2})\n" +
                "        </if>\n" +
                "        <if test=\"abnormalTypeLevel3!=null\">\n" +
                "            abnormal_type_level3 in (${abnormalTypeLevel3})\n" +
                "        </if>\n" +
                "        <if test=\"ownerId!=null\">\n" +
                "            and owner_id = #{ownerId}\n" +
                "        </if>\n" +
                "        <if test=\"scpBizOrderStatus!=null\">\n" +
                "            and scp_biz_order_status = #{scpBizOrderStatus}\n" +
                "        </if>\n" +
                "        <if test=\"activeService!=null\">\n" +
                "            and active_service = #{activeService}\n" +
                "        </if>\n" +
                "        <if test=\"associatedEvent!=null\">\n" +
                "            and event_list like concat('%',#{associatedEvent},'%')\n" +
                "        </if>\n" +
                "        <if test=\"excpOrderCode!=null\">\n" +
                "            and excp_order_code = #{excpOrderCode}\n" +
                "        </if>\n" +
                "        <if test=\"scpBizGroup!=null\">\n" +
                "            and scp_biz_group = #{scpBizGroup}\n" +
                "        </if>\n" +
                "        <if test=\"city!=null\">\n" +
                "            AND (city_code = #{city} or province_code = #{city})\n" +
                "        </if>\n" +
                "    </select>\n" +
                "</mapper>";

        Map<String, Map<String, String>> map = new HashMap<>();
        Document document = documentBuilder.parse(new InputSource(new StringReader(sql)));

        NodeList selectList = document.getElementsByTagName("select");
        map.put("select", getIdAndSql(selectList));
        //System.out.println(map);


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
            map.put("select", getIdAndSql(selectList));
            map.put("update", getIdAndSql(updateList));
            map.put("delete", getIdAndSql(deleteList));
            map.put("insert", getIdAndSql(insertList));
            return map;
        } catch (Exception e) {
            log.error("XML解析异常,请检查XML格式是否正确,errMsg:{}", e.getMessage());
        }
        return null;
    }

    private static Map<String, String> getIdAndSql(NodeList nodeList) {
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
                            // 把整个xml 加入
                            child.put(node.getNodeValue(),  getNodeAll(item));
                        }
                    }
                }
            }
            return child;
        }
        return Collections.EMPTY_MAP;
    }

    private static String getNodeAll(Node node) {
        StringBuilder stringBuilder = new StringBuilder();
        String nodeName = node.getNodeName();
        if (DeferredElementImpl.class.isInstance(node)) {
            String idNodeStr = "";
            if (node.hasAttributes()) {
                Node idNode = node.getAttributes().getNamedItem("id");
                if (Objects.nonNull(idNode)) {
                    String nodeValue = idNode.getNodeValue();
                    if (StringUtils.isNotBlank(nodeName)) {
                        idNodeStr = " id=\""+nodeValue+"\"";
                    }

                }
            }
            stringBuilder.append("<" + nodeName + idNodeStr + ">");
        }
        NodeList childNodes = node.getChildNodes();
        if (childNodes.getLength() == 0) {
            return stringBuilder.toString();
        }
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (DeferredTextImpl.class.isInstance(item)) {
                String textContent = item.getTextContent();
                stringBuilder.append(textContent);
            } else {
                stringBuilder.append(getNodeAll(item));
            }

        }
        stringBuilder.append("</" + nodeName + ">");
        return stringBuilder.toString();
    }
}


