package org.example.demo.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/12/7
 */
public enum IdLabelType {
    SELECT,INSERT,UPDATE,DELETE,SQL,BIND;
    public static IdLabelType getByName(String name){
        return IdLabelType.valueOf(StringUtils.upperCase(name));
    }

}
