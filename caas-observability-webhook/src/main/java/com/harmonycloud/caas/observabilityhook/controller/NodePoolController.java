package com.harmonycloud.caas.observabilityhook.controller;

import com.harmonycloud.caas.observabilityhook.common.enums.ErrorCodeMessage;
import com.harmonycloud.caas.observabilityhook.model.JSONPatch;
import com.harmonycloud.caas.observabilityhook.model.admission.AdmissionRequest;
import io.fabric8.kubernetes.api.model.admission.AdmissionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.harmonycloud.caas.observabilityhook.model.admission.AdmissionReview;
import com.harmonycloud.caas.observabilityhook.service.NodePoolService;

import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author dengyulong
 * @date 2020/03/26
 *
 * 主机资源池相关的webhook接口
 */
@Api
@RestController
//@RequestMapping("/nodes/pools")
public class NodePoolController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NodePoolService nodePoolService;

    /**
     * 给pod增加label和toleration
     */
    @PostMapping("/mutate")
    public AdmissionReview mutateNodePool(@RequestBody String review) {
        logger.info("api : /nodes/pools/mutate, params : {}", review);
        if (review == null) {
            return null;
        }
        AdmissionReview admissionReview = JSON.parseObject(review, AdmissionReview.class);



        return nodePoolService.mutateNodePool(admissionReview.getRequest());
    }


}
