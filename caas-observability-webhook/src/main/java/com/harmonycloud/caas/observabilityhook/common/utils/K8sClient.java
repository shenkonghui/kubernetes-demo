package com.harmonycloud.caas.observabilityhook.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dengyulong
 * @date 2020/03/26
 */
@Slf4j
@Component
public class K8sClient {

    private static String url;

    private static String token;

    @Value("${k8s.token.path:/run/secrets/kubernetes.io/serviceaccount/token}")
    private void setToken(String tokenPath) {
        if (StringUtils.isBlank(token)) {
            K8sClient.token = FileUtil.readContent(tokenPath);
        }
    }

    public static KubernetesClient getClient() {
        return InstanceHolder.KUBERNETES_CLIENT;
    }

    /**
     * 内部类来保证单例的线程安全
     */
    private static class InstanceHolder {
        private static final KubernetesClient KUBERNETES_CLIENT = new DefaultKubernetesClient(
            new ConfigBuilder().withMasterUrl(url).withTrustCerts(true).withOauthToken(token).build());
    }

    public void setUrl(String url) {
        K8sClient.url = url;
    }

}
