package com.demo;

import com.alibaba.fastjson.JSONObject;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.util.Config;

import java.io.IOException;

public class Crd {
    public static void main(String[] args) throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

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