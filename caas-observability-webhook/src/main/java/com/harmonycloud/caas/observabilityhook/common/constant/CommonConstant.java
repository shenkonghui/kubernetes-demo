package com.harmonycloud.caas.observabilityhook.common.constant;

/**
 * @author dengyulong
 * @date 2020/03/26
 * 通用常量类
 */
public class CommonConstant {

    /**
     * 分区的annotation存放主机资源池的标签和污点
     */
    public static String NODE_POOL_LABELS = "node-pool-labels";
    public static String NODE_POOL_TAINTS = "node-pool-taints";

    /**
     * 环境变量
     */
    public static String ENV_KUBERNETES_SERVICE_HOST = "KUBERNETES_SERVICE_HOST";
    public static String ENV_KUBERNETES_SERVICE_PORT = "KUBERNETES_SERVICE_PORT";

}
