package com.harmonycloud.caas.observabilityhook.model.admission;

import javax.validation.Valid;

import io.fabric8.kubernetes.api.model.GroupVersionKind;
import io.fabric8.kubernetes.api.model.GroupVersionResource;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.authentication.UserInfo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dengyulong
 * @date 2020/03/27
 */
@Accessors(chain = true)
@Data
public class AdmissionRequest implements KubernetesResource {

    private Boolean dryRun;
    @Valid
    private GroupVersionKind kind;
    private String name;
    private String namespace;
    private String object;
    private String oldObject;
    private String operation;
    @Valid
    private GroupVersionResource resource;
    private String subResource;
    private String uid;
    @Valid
    private UserInfo userInfo;

}
