package com.demo;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;

@Slf4j
public class Apply {

    public static  List<String> clusterResource = Arrays.asList(
                "clusterrole",
                "clusterrolebinding",
                "namespace",
                "customresourcedefinition");

    public static void main(String[] args) throws IOException, ApiException {
        String filePath = "xxx.yaml";
        File file = new File(filePath);
        List<Object> list = null;

        try {
            list = Yaml.loadAll(file);
        } catch (IOException e) {
            System.out.println("Failed to parse yaml file");
            e.printStackTrace();
        }

        //先创建namespace，否则有些资源没有namespace创建不出来
        for (int i = 0; i < list.size(); i++) {
            KubernetesObject obj = (KubernetesObject)list.get(i);
            if (obj.getKind().toLowerCase().equals("namespace")){
                try {
                    log.info("{} {} wait to create",obj.getKind(),obj.getMetadata().getName());
                    createObject(obj);
                    log.info("{} {} create success",obj.getKind(),obj.getMetadata().getName());
                } catch (ApiException e) {
                    if (e.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                        log.info("{} {} is not found",obj.getKind(),obj.getMetadata().getName());

                    }else if (e.getCode() != HttpURLConnection.HTTP_CONFLICT) {
                        log.info("{} {} is conflict",obj.getKind(),obj.getMetadata().getName());
                    }
                    log.info(String.valueOf(e.getCode()));
                    log.info(e.getResponseBody());
                    e.printStackTrace();
                }
            }
        }

        // 然后创建其他资源
        for (int i = 0; i < list.size(); i++) {
            KubernetesObject obj = (KubernetesObject)list.get(i);
            if (obj.getKind().toLowerCase().equals("namespace")){
                continue;
            }
            // 创建资源、没有找到创建object资源对象的接口，自定义restful接口实现
            try {
                log.info("{} {} wait to create",obj.getKind(),obj.getMetadata().getName());
                createObject(obj);
                log.info("{} {} create success",obj.getKind(),obj.getMetadata().getName());
            } catch (ApiException e) {
                if (e.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    log.info("{} {} is not found",obj.getKind(),obj.getMetadata().getName());

                }else if (e.getCode() != HttpURLConnection.HTTP_CONFLICT) {
                    log.info("{} {} is conflict",obj.getKind(),obj.getMetadata().getName());
                }
                log.info(String.valueOf(e.getCode()));
                log.info(e.getResponseBody());
                e.printStackTrace();
            }
        }
    }

    public static void createObject(KubernetesObject obj) throws ApiException {
        String result = doCoreObject(obj,"POST");
        System.out.println(result);
    }

    public void deleteObject(KubernetesObject obj) throws ApiException {
        String result = doCoreObject(obj,"DELETE");
        System.out.println(result);
    }

    public static String doCoreObject(KubernetesObject obj, String method) throws ApiException {
        V1ObjectMeta metadata = obj.getMetadata();
        String api = obj.getApiVersion();
        String apiPrefix = api.equals("v1")? "api":"apis";
        String kind = obj.getKind().toLowerCase();
        String namespace = metadata.getNamespace() == null? "default": metadata.getNamespace();
        String name = metadata.getName();

        String[] localVarAuthNames = new String[] {"BearerToken"};
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();
        final String[] localVarAccepts = {
                "application/json", "application/yaml", "application/vnd.kubernetes.protobuf"
        };
        final String localVarAccept = getK8Client().selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }
        final String[] localVarContentTypes = {};
        final String localVarContentType =
                getK8Client().selectHeaderContentType(localVarContentTypes);


        localVarHeaderParams.put("Content-Type", localVarContentType);
        String localVarPathTemp = "/%s/%s/namespaces/%s/%ss";
        String localVarPathClusterTemp = "/%s/%s/%ss";
        String localVarPath  = "";
        // 集群级别和namespace级别的资源接口不同, 其实最好通过接口拿，我这里比较懒，直接声明了
        if (clusterResource.contains(kind)){
            localVarPath  = String.format(localVarPathClusterTemp,apiPrefix,api,kind);
        }else{
            localVarPath  = String.format(localVarPathTemp,apiPrefix,api,namespace,kind);
        }
        List<Pair> localVarQueryParams = new ArrayList();
        List<Pair> localVarCollectionQueryParams = new ArrayList();

        Call call = null;
        if ("DELETE".equals(method)){
            localVarPath = localVarPath + "/" + name;
            localVarQueryParams.addAll(getK8Client().parameterToPair("gracePeriodSeconds", 0));
            System.out.println(localVarPath);
            call = getK8Client().buildCall(localVarPath,method,localVarQueryParams,null,null,
                    localVarHeaderParams,localVarCookieParams,localVarFormParams,localVarAuthNames,null);
        }else{
            call = getK8Client().buildCall(localVarPath,method,localVarQueryParams,null,obj,
                    localVarHeaderParams,localVarCookieParams,localVarFormParams,localVarAuthNames,null);
        }
        System.out.println(localVarPath);
        String result = null;
        try {
            result = call.execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    static ApiClient client = null;
    static String kubeConfigPath = System.getenv("HOME") + "/.kube/config";
    public static ApiClient getK8Client(){
        if (client == null){
            try {
                client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Configuration.setDefaultApiClient(client);
        }
        return client;
    }
}