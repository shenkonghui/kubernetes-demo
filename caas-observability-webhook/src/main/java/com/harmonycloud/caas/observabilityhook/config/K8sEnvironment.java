package com.harmonycloud.caas.observabilityhook.config;

import com.harmonycloud.caas.observabilityhook.common.utils.K8sClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.stereotype.Component;

import static com.harmonycloud.caas.observabilityhook.common.constant.CommonConstant.ENV_KUBERNETES_SERVICE_HOST;
import static com.harmonycloud.caas.observabilityhook.common.constant.CommonConstant.ENV_KUBERNETES_SERVICE_PORT;

/**
 * @author dengyulong
 * @date 2020/06/05
 * 读取环境变量
 */
@Component
public class K8sEnvironment implements EnvironmentAware {

    @Autowired
    private K8sClient k8sClient;

    private String k8sUrl;

    @Override
    public void setEnvironment(org.springframework.core.env.Environment environment) {
        String k8sServiceHost = environment.getProperty(ENV_KUBERNETES_SERVICE_HOST);
        String k8sServicePort = environment.getProperty(ENV_KUBERNETES_SERVICE_PORT);

        if (StringUtils.isBlank(k8sServiceHost)) {
            // svc的名称
            k8sServiceHost = "kubernetes";
        }
        if (StringUtils.isBlank(k8sServicePort)) {
            // 默认端口
            k8sServicePort = "443";
        }
        k8sUrl = "https://" + k8sServiceHost + ":" + k8sServicePort;
        k8sClient.setUrl(k8sUrl);
    }


    public String getK8sUrl() {
        return k8sUrl;
    }

}
