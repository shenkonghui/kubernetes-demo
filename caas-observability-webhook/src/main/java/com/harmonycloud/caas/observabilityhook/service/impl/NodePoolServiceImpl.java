package com.harmonycloud.caas.observabilityhook.service.impl;

import static com.harmonycloud.caas.observabilityhook.common.constant.CommonConstant.NODE_POOL_LABELS;
import static com.harmonycloud.caas.observabilityhook.common.constant.CommonConstant.NODE_POOL_TAINTS;
import static com.harmonycloud.caas.observabilityhook.common.constant.DictionaryConstant.KIND;
import static com.harmonycloud.caas.observabilityhook.common.constant.DictionaryConstant.METADATA;
import static com.harmonycloud.caas.observabilityhook.common.constant.DictionaryConstant.NODE_SELECTOR;
import static com.harmonycloud.caas.observabilityhook.common.constant.DictionaryConstant.OWNER_REFERENCES;
import static com.harmonycloud.caas.observabilityhook.common.constant.DictionaryConstant.REPLACE;
import static com.harmonycloud.caas.observabilityhook.common.constant.DictionaryConstant.SPEC;
import static com.harmonycloud.caas.observabilityhook.common.constant.DictionaryConstant.TEMPLATE;
import static com.harmonycloud.caas.observabilityhook.common.constant.DictionaryConstant.TOLERATIONS;
import static com.harmonycloud.caas.observabilityhook.common.constant.K8sConstant.EQUAL;
import static com.harmonycloud.caas.observabilityhook.common.constant.K8sConstant.EXISTS;
import static com.harmonycloud.caas.observabilityhook.common.constant.K8sConstant.KUBE_SYSTEM;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.harmonycloud.caas.observabilityhook.common.enums.ErrorCodeMessage;
import com.harmonycloud.caas.observabilityhook.common.enums.ResourceKindEnum;
import com.harmonycloud.caas.observabilityhook.common.utils.K8sClient;
import com.harmonycloud.caas.observabilityhook.model.JSONPatch;
import com.harmonycloud.caas.observabilityhook.model.admission.AdmissionRequest;
import com.harmonycloud.caas.observabilityhook.model.admission.AdmissionReview;
import com.harmonycloud.caas.observabilityhook.service.NodePoolService;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.api.model.Taint;
import io.fabric8.kubernetes.api.model.Toleration;
import io.fabric8.kubernetes.api.model.admission.AdmissionResponse;


/**
 * @author dengyulong
 * @date 2020/03/26
 */
@Service
public class NodePoolServiceImpl implements NodePoolService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${k8s.node-pool.resource:Deployment,StatefulSet,DaemonSet,Pod}")
    private String resources;

    @Override
    public AdmissionReview mutateNodePool(AdmissionRequest request) {
        logger.info("admission namespace:{}, name:{}", request.getNamespace(), request.getName());
        AdmissionResponse response = new AdmissionResponse();
        response.setUid(request.getUid());

        logger.info("admission namespace:{}, name:{}", request.getNamespace(), request.getName());

        response.setUid(request.getUid());

        List<JSONPatch> patchList = new ArrayList<>(2);
        JSONPatch labelPatch =
                new JSONPatch().setOp("add").setPath("/metadata/annotations/test").setValue("hello");
        patchList.add(labelPatch);


        response.setPatchType("JSONPatch");
        // base64编码
        response.setPatch(Base64.getEncoder().encodeToString(JSON.toJSONString(patchList).getBytes()));

        return dealAdmissionReviewResponse(response, ErrorCodeMessage.OK);
    }

    /**
     * 处理标签和污点，并返回是否需要更新
     * 
     * @param request     webhook请求
     * @param annotations 分区annotations
     * @param patchList   要修改的属性
     * @return 是否需要修改
     */
    private boolean dealLabelsAndTaints(AdmissionRequest request, AdmissionResponse response,
                                        Map<String, String> annotations, List<JSONPatch> patchList) {
        // 校验kind
        String kind = request.getKind().getKind();
        if (!resources.contains(kind)) {
            logger.info("不支持类型{}", kind);
            return false;
        }
        ResourceKindEnum kindEnum = ResourceKindEnum.byKind(kind);
        if (kindEnum == null) {
            logger.info("不支持类型{}", kind);
            // 类型不符，不让创建
            response.setAllowed(false);
            return false;
        }

        // 由于fabric8里的Pod类里的有些字段是特殊封装过的(比如说maxSurge类型是IntOrString，而传入的是字符串)，
        // 直接转会报错，不确定性比较大，所以使用了json去取值
        JSONObject object = JSON.parseObject(request.getObject());
        JSONObject spec;
        if (ResourceKindEnum.isPod(kind)) {
            spec = object.getJSONObject(SPEC);
        } else if (ResourceKindEnum.isWorkLoad(kind)) {
            spec = object.getJSONObject(SPEC).getJSONObject(TEMPLATE).getJSONObject(SPEC);
        } else {
            logger.info("不支持类型{}", kind);
            // 类型不符，不让创建
            response.setAllowed(false);
            return false;
        }

        // 取出分区annotations里的主机资源池标签和污点
        String labelsStr = annotations.get(NODE_POOL_LABELS);
        String taintsStr = annotations.get(NODE_POOL_TAINTS);
        logger.info("name:{}, kind:{}, namespace:{}, labelsStr:{}, taintsStr:{}",request.getName(), kind, request.getNamespace(), labelsStr, taintsStr);
        if (StringUtils.isBlank(labelsStr) || StringUtils.isBlank(taintsStr)) {
            // 分区不符合配置，不让创建
            response.setAllowed(false);
            return false;
        }

        // 如果是kube-system分区下的 ds或ds起的pod 都不加上标签
        boolean isKubeSystemDs = false;
        if (KUBE_SYSTEM.equals(request.getNamespace())) {
            if (ResourceKindEnum.DAEMONSET.getKind().equals(kind)) {
                isKubeSystemDs = true;
            } else if (ResourceKindEnum.isPod(kind)) {
                JSONObject metadata = object.getJSONObject(METADATA);
                // 如果owner的类型为ds，不加标签
                if (metadata.containsKey(OWNER_REFERENCES) && ResourceKindEnum.DAEMONSET.getKind()
                    .equals(metadata.getJSONArray(OWNER_REFERENCES).getJSONObject(0).get(KIND))) {
                    isKubeSystemDs = true;
                }
            }
        }
        logger.info("isKubeSystemDs:{}", isKubeSystemDs);
        boolean changed = false;
        // 处理labels，原本的节点亲和 + 主机资源池的label
        Map<String, Object> labelMap = JSONObject.parseObject(labelsStr);
        if (!CollectionUtils.isEmpty(labelMap) && !isKubeSystemDs) {
            changed = true;
            Map<String, Object> nodeSelector = spec.getJSONObject(NODE_SELECTOR);
            if (CollectionUtils.isEmpty(nodeSelector)) {
                nodeSelector = Maps.newHashMap();
            }
            nodeSelector.putAll(labelMap);
            Map<String, String> labels = nodeSelector.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
            JSONPatch labelPatch =
                new JSONPatch().setOp(REPLACE).setPath(kindEnum.getNodeSelectorPath()).setValue(labels);
            patchList.add(labelPatch);
        }

        // 处理taints，原本的污点容忍 + 主机资源池的污点
        List<Taint> taintList = JSON.parseArray(taintsStr, Taint.class);
        if (!CollectionUtils.isEmpty(taintList)) {
            changed = true;
            Set<Toleration> tolerationSet;
            JSONArray tolerationArray = spec.getJSONArray(TOLERATIONS);
            if (!CollectionUtils.isEmpty(tolerationArray)) {
                tolerationSet = new HashSet<>(tolerationArray.toJavaList(Toleration.class));
            } else {
                tolerationSet = new HashSet<>(taintList.size());
            }
            for (Taint taint : taintList) {
                Toleration toleration = new Toleration();
                toleration.setKey(taint.getKey());
                toleration.setValue(taint.getValue());
                toleration.setEffect(taint.getEffect());
                toleration.setOperator(StringUtils.isBlank(taint.getValue()) ? EXISTS : EQUAL);
                tolerationSet.add(toleration);
            }
            JSONPatch tolerationPatch = new JSONPatch().setOp(REPLACE).setPath(kindEnum.getTolerationsPath())
                .setValue(new ArrayList<>(tolerationSet));
            logger.info("name:{}, 添加污点:{}",request.getName(),JSONObject.toJSONString(tolerationSet));

            patchList.add(tolerationPatch);
        }

        return changed;
    }

    /**
     * 处理返回值
     */
    private AdmissionReview dealAdmissionReviewResponse(AdmissionResponse response, ErrorCodeMessage error) {
        AdmissionReview admissionReview = new AdmissionReview();
        Status status = new Status();
        status.setMessage(error.getMsg());
        response.setStatus(status);
        if (response.getAllowed() == null) {
            response.setAllowed(true);
        }
        admissionReview.setResponse(response);
        return admissionReview;
    }

}
