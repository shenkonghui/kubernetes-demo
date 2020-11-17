package com.harmonycloud.caas.observabilityhook.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author dengyulong
 * @date 2020/03/27
 */
public enum ResourceKindEnum {

    /**
     * k8s资源类型
     */
    DEPLOYMENT("Deployment", "/spec/template/spec/nodeSelector", "/spec/template/spec/tolerations"),
    STATEFULSET("StatefulSet", "/spec/template/spec/nodeSelector", "/spec/template/spec/tolerations"),
    DAEMONSET("DaemonSet", "/spec/template/spec/nodeSelector", "/spec/template/spec/tolerations"),
    POD("Pod", "/spec/nodeSelector", "/spec/tolerations"),
    ;

    private String kind;
    private String nodeSelectorPath;
    private String tolerationsPath;

    public static ResourceKindEnum byKind(String kind) {
        for (ResourceKindEnum typeEnum : ResourceKindEnum.values()) {
            if (kind.equals(typeEnum.getKind())) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 是否工作负载
     */
    public static boolean isWorkLoad(String kind) {
        return StringUtils.isNotBlank(kind)
            && (DEPLOYMENT.kind.equals(kind) || STATEFULSET.kind.equals(kind) || DAEMONSET.kind.equals(kind));
    }

    /**
     * 是否pod
     */
    public static boolean isPod(String kind) {
        return StringUtils.isNotBlank(kind) && POD.kind.equals(kind);
    }

    ResourceKindEnum(String kind, String nodeSelectorPath, String tolerationsPath) {
        this.kind = kind;
        this.nodeSelectorPath = nodeSelectorPath;
        this.tolerationsPath = tolerationsPath;
    }

    public String getKind() {
        return kind;
    }

    public String getNodeSelectorPath() {
        return nodeSelectorPath;
    }

    public String getTolerationsPath() {
        return tolerationsPath;
    }
}
