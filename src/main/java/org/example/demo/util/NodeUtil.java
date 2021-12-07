package org.example.demo.util;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;


/**
 * @author nmy
 * @version 1.0
 * @since 2021/12/7
 */
public class NodeUtil {

   private static final String INCLUDE_TAG="include";


    /**
     * <select id="ddd" op="ooo"></>
     * 获得某个节点的 参数
     *
     * @param node 1
     * @return id="ddd" op="ooo"
     */
    private static String getNodeParameter(Node node) {
        StringBuilder parameter = new StringBuilder();
        if (Objects.nonNull(node) && (node.getNodeType() == Node.ELEMENT_NODE)) {
            if (node.hasAttributes()) {
                NamedNodeMap attributes = node.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Node attribute = attributes.item(j);
                    if (Objects.nonNull(attribute)) {
                        String attributeNodeValue = attribute.getNodeValue();
                        String attributeNodeName = attribute.getNodeName();
                        if (StringUtils.isNoneBlank(attributeNodeName, attributeNodeValue)) {
                            parameter.append(" ").append(attributeNodeName).append("=\"").append(attributeNodeValue).append("\"");
                        }
                    }
                }
            }
        }
        return parameter.toString();
    }


    public static String addSqlTagToNode(Node node, Map<String, String> sqlNodes) {
        if (sqlNodes == null || sqlNodes.size() == 0) {
            return nodeToString(node);
        }
        StringBuilder xmlStr = new StringBuilder();
        //判断是否为节点
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            if (INCLUDE_TAG.equals(node.getNodeName())) {
                //获得引用的id
                String refid = getStringAttribute(node, "refid");
                if (StringUtils.isNotBlank(refid)) {
                    String includeSql = sqlNodes.get(refid);
                    if (StringUtils.isNotBlank(includeSql)) {
                        xmlStr.append(includeSql);
                    }
                }
            } else {
                xmlStr.append("<").append(node.getNodeName()).append(getNodeParameter(node)).append(">");
            }
        }
        NodeList childNodes = node.getChildNodes();
        //判断如果有子节点
        if (childNodes != null) {
            //遍历子节点 从
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                //判断是否为 文本节点
                if ((item.getNodeType() == Node.ELEMENT_NODE)) {
                    xmlStr.append(addSqlTagToNode(item, sqlNodes));
                } else {
                    xmlStr.append(item.getTextContent());
                }
            }
        }
        if (node.getNodeType() == Node.ELEMENT_NODE && !INCLUDE_TAG.equals(node.getNodeName())) {
            xmlStr.append("</").append(node.getNodeName()).append(">");
        }
        return xmlStr.toString();
    }

    /**
     * 获得 标签上对应的参数
     *
     */
    public static String getStringAttribute(Node node, String name) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node item = attributes.getNamedItem(name);
            if (item != null) {
                return item.getNodeValue();
            }
        }
        return null;

    }

    /**
     * 获得node 全部标签数据
     *
     * @param node
     * @return
     */
    public static String nodeToString(Node node) {
        return nodeToString(node, true);
    }

    /**
     * 获得某个节点 原始(解析前)的xml字符串
     *
     * @param node
     * @param hasRootTag true: 全部标签 ,false: 不包含顶级标签
     * @return
     */
    public static String nodeToString(Node node, boolean hasRootTag) {
        if (node == null) {
            return StringUtils.EMPTY;
        }
        StringBuilder xmlStr = new StringBuilder();

        //判断是否为节点
        if ((node.getNodeType() ==Node.ELEMENT_NODE) && hasRootTag) {
            xmlStr.append("<").append(node.getNodeName()).append(getNodeParameter(node)).append(">");
        }
        NodeList childNodes = node.getChildNodes();
        //判断如果有子节点
        if (childNodes != null) {
            //遍历子节点 从
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                //判断是否为 文本节点
                if ((item.getNodeType() == Node.ELEMENT_NODE)) {
                    xmlStr.append(nodeToString(item));
                } else {
                    xmlStr.append(item.getTextContent());
                }
            }
        }
        if (node.getNodeType() == Node.ELEMENT_NODE && hasRootTag) {
            xmlStr.append("</").append(node.getNodeName()).append(">");
        }
        return xmlStr.toString();
    }

    /**
     * 移除多余的空格
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

}
