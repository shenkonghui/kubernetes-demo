package com.demo;

import com.alibaba.fastjson.JSONObject;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;

import java.io.IOException;
/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException, ApiException {
        System.out.println( "Hello World!");

        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();


        // 查询所有pod
       V1PodList list =
               api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
       for (V1Pod item : list.getItems()) {
           System.out.println(item.getMetadata().getName());
       }

         String isioCR = "{\"apiVersion\":\"install.istio.io/v1alpha1\",\"kind\":\"IstioOperator\",\"metadata\":{\"annotations\":{},\"name\":\"istio-operator\",\"namespace\":\"istio-system\"},\"spec\":{\"addonComponents\":{\"grafana\":{\"enabled\":false},\"kiali\":{\"enabled\":false},\"prometheus\":{\"enabled\":false},\"tracing\":{\"enabled\":false}},\"profile\":\"demo\"}}";
         CustomObjectsApi apiInstance = new CustomObjectsApi();
         String group = "install.istio.io";
         String version = "v1alpha1";
         String plural = "istiooperators";
         String pretty = "ture";

        Object body = JSONObject.parse(isioCR);
        try {
            Object result = apiInstance.createNamespacedCustomObject(group, version, "istio-system", plural, body, pretty, null, "");
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CustomObjectsApi#createNamespacedCustomObject");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }

}
