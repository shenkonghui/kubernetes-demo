package com.harmonycloud.caas.observabilityhook.service;


import com.harmonycloud.caas.observabilityhook.model.admission.AdmissionRequest;
import com.harmonycloud.caas.observabilityhook.model.admission.AdmissionReview;

/**
 * @author dengyulong
 * @date 2020/03/26
 */
public interface NodePoolService {

    /**
     * 修改发布在某个资源池上的pod的标签和污点
     *
     * @param request 请求
     * @return
     */
    AdmissionReview mutateNodePool(AdmissionRequest request);

}
