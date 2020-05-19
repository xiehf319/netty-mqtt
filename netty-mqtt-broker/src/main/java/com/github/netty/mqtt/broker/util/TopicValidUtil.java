package com.github.netty.mqtt.broker.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/19 13:58
 */
@UtilityClass
@Slf4j
public class TopicValidUtil {

    private static final String SEPERATOR = "/";
    private static final String MULTI = "#";
    private static final String SINGLE = "+";

    /**
     * [MQTT-4.7.1-2] 多层通配符必须位于它自己的层级或者跟在主题层级分配符后面，必须是最后一个字符
     * [MQTT-4.7.1-3] 在主题过滤器的任意层级都可以使用单层通配符，包括第一个和最后一个层级。然而它必须占据过滤器的整个层级
     *
     * @param topicFilter
     * @return
     */
    public static boolean valid(String topicFilter) {
        if (!StringUtils.hasLength(topicFilter)) {
            log.info("[0]topicFilter: {} 不能为空", topicFilter);
            return false;
        }
        if (StringUtils.containsWhitespace(topicFilter)) {
            log.info("[0]topicFilter: {} 不允许包含空字符", topicFilter);
            return false;
        }
        if (topicFilter.contains(MULTI)) {
            if (topicFilter.contains(SINGLE)) {
                log.info("[1] topicFilter: {} 不能同时存在+和#", topicFilter);
                return false;
            }
            if (topicFilter.startsWith(MULTI)) {
                // #开始但是不止一个字符，不符合要求
                if (topicFilter.length() > 1) {
                    log.error("[2]#开头的不允许有其他的符号: {}", topicFilter);
                    return false;
                } else {
                    return true;
                }
            }
            if (topicFilter.endsWith(MULTI)) {
                if (!topicFilter.substring(0, topicFilter.length() - 1).endsWith(SEPERATOR)) {
                    // 只是#结尾不是/#结尾
                    log.error("[3]#结尾必须跟在/后面: {}", topicFilter);
                    return false;
                }
                if (topicFilter.substring(0, topicFilter.length() - 2).contains(MULTI)) {
                    // 超过1个#
                    log.error("[4]: {}", topicFilter);
                    return false;
                }
            }
        }
        String[] split = topicFilter.split(SEPERATOR);
        for (int i = 0, j = split.length - 1; j >= i; i++, j--) {
            String idx1 = split[i];
            String idx2 = split[j];
            if (idx1.contains(SINGLE) && idx1.length() > 1) {
                log.error("[5] topFilter不能连续2个加号: {}", topicFilter);
                return false;
            }
            if (idx2.contains(SINGLE) && idx2.length() > 1) {
                log.error("[5] topFilter + 只能独立存在与两个/之间: {}", topicFilter);
                return false;
            }
        }
        return true;
    }
}
