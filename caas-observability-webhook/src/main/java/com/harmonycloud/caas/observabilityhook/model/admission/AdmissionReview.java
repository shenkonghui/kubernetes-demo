package com.harmonycloud.caas.observabilityhook.model.admission;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.admission.AdmissionResponse;
import lombok.Data;

/**
 * @author dengyulong
 * @date 2020/03/27
 */
@Data
public class AdmissionReview implements KubernetesResource {

    private String apiVersion = "admission.k8s.io/v1beta1";

    private String kind = "AdmissionReview";

    private AdmissionRequest request;

    private AdmissionResponse response;

}

