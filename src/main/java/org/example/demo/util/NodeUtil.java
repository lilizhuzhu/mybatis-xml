package org.example.demo.util;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import static org.w3c.dom.Node.ELEMENT_NODE;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/12/7
 */
public class NodeUtil {

    public static void main(String[] args) {

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
                            child.put(node.getNodeValue(), removeExtraWhitespaces(getStringByNode(item)));
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
        if (Objects.nonNull(node) && (node.getNodeType() == ELEMENT_NODE)) {
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
    public static String getStringByNode(Node node) {
        if (node == null) {
            return StringUtils.EMPTY;
        }
        StringBuilder xmlStr = new StringBuilder();

        //判断是否为节点
        if ((node.getNodeType() == ELEMENT_NODE)) {
            xmlStr.append("<" + node.getNodeName() + getNodeParameter(node) + ">");
        }
        NodeList childNodes = node.getChildNodes();
        //判断如果有子节点
        if (childNodes != null) {
            //遍历子节点 从
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                //判断是否为 文本节点
                if ((item.getNodeType() == ELEMENT_NODE)) {
                    xmlStr.append(getStringByNode(item));
                } else {
                    xmlStr.append(item.getTextContent());
                }
            }
        }
        if (node.getNodeType() == ELEMENT_NODE) {
            xmlStr.append("</" + node.getNodeName() + ">");
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
