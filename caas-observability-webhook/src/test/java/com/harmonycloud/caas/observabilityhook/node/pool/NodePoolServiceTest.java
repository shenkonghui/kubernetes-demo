package com.harmonycloud.caas.observabilityhook.node.pool;

import com.harmonycloud.caas.observabilityhook.BaseTest;
import com.harmonycloud.caas.observabilityhook.model.admission.AdmissionReview;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.harmonycloud.caas.observabilityhook.service.NodePoolService;


/**
 * @author dengyulong
 * @date 2020/03/27
 */
public class NodePoolServiceTest extends BaseTest {

    @Autowired
    private NodePoolService nodePoolService;

    private static AdmissionReview podAdmissionReview;
    private static AdmissionReview deployAdmissionReview;

    @Test
    public void testMutate() {
        // 测试pod
        AdmissionReview admissionReview = nodePoolService.mutateNodePool(NodePoolServiceTest.podAdmissionReview.getRequest());
        Assert.notNull(admissionReview.getResponse(), "admission response 不应该为空");

        // 测试deploy
        admissionReview = nodePoolService.mutateNodePool(NodePoolServiceTest.deployAdmissionReview.getRequest());
        Assert.notNull(admissionReview.getResponse(), "admission response 不应该为空");
    }
    

    @BeforeAll
    private static void initAdmissionReview() {
        // pod
        String review = "{\n" +
                "    \"apiVersion\":\"admission.k8s.io/v1beta1\",\n" +
                "    \"kind\":\"AdmissionReview\",\n" +
                "    \"request\":{\n" +
                "        \"kind\":{\n" +
                "            \"group\":\"\",\n" +
                "            \"kind\":\"Pod\",\n" +
                "            \"version\":\"v1\"\n" +
                "        },\n" +
                "        \"namespace\":\"caas-system\",\n" +
                "        \"object\":{\n" +
                "            \"kind\":\"Pod\",\n" +
                "            \"apiVersion\":\"v1\",\n" +
                "            \"metadata\":{\n" +
                "                \"name\":\"test-pod\",\n" +
                "                \"namespace\":\"caas-system\",\n" +
                "                \"labels\":{\n" +
                "                    \"app\":\"test-pod\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"spec\":{\n" +
                "                \"volumes\":[\n" +
                "                    {\n" +
                "                        \"name\":\"default-token-8gtcj\",\n" +
                "                        \"secret\":{\n" +
                "                            \"secretName\":\"default-token-8gtcj\"\n" +
                "                        }\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"containers\":[\n" +
                "                    {\n" +
                "                        \"name\":\"test-container\",\n" +
                "                        \"image\":\"busybox\",\n" +
                "                        \"command\":[\n" +
                "                            \"sh\",\n" +
                "                            \"-c\",\n" +
                "                            \"echo Hello Harmonycloud! && sleep 3600\"\n" +
                "                        ],\n" +
                "                        \"resources\":{\n" +
                "\n" +
                "                        },\n" +
                "                        \"volumeMounts\":[\n" +
                "                            {\n" +
                "                                \"name\":\"default-token-8gtcj\",\n" +
                "                                \"readOnly\":true,\n" +
                "                                \"mountPath\":\"/var/run/secrets/kubernetes.io/serviceaccount\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"terminationMessagePath\":\"/dev/termination-log\",\n" +
                "                        \"terminationMessagePolicy\":\"File\",\n" +
                "                        \"imagePullPolicy\":\"Always\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"restartPolicy\":\"Always\",\n" +
                "                \"terminationGracePeriodSeconds\":30,\n" +
                "                \"dnsPolicy\":\"ClusterFirst\",\n" +
                "                \"serviceAccountName\":\"default\",\n" +
                "                \"serviceAccount\":\"default\",\n" +
                "                \"securityContext\":{\n" +
                "\n" +
                "                },\n" +
                "                \"schedulerName\":\"default-scheduler\",\n" +
                "                \"tolerations\":[\n" +
                "                    {\n" +
                "                        \"key\":\"node.kubernetes.io/not-ready\",\n" +
                "                        \"operator\":\"Exists\",\n" +
                "                        \"effect\":\"NoExecute\",\n" +
                "                        \"tolerationSeconds\":300\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"key\":\"node.kubernetes.io/unreachable\",\n" +
                "                        \"operator\":\"Exists\",\n" +
                "                        \"effect\":\"NoExecute\",\n" +
                "                        \"tolerationSeconds\":300\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"priority\":0,\n" +
                "                \"enableServiceLinks\":true\n" +
                "            },\n" +
                "            \"status\":{\n" +
                "\n" +
                "            }\n" +
                "        },\n" +
                "        \"operation\":\"CREATE\",\n" +
                "        \"resource\":{\n" +
                "            \"group\":\"\",\n" +
                "            \"resource\":\"pods\",\n" +
                "            \"version\":\"v1\"\n" +
                "        },\n" +
                "        \"uid\":\"a9a5c2ec-d9a1-41cd-b142-0c0cfa24a627\"\n" +
                "    }\n" +
                "}";

        podAdmissionReview = JSON.parseObject(review, AdmissionReview.class);

        // deploy
        review = "{\n" +
                "    \"kind\": \"AdmissionReview\",\n" +
                "    \"apiVersion\": \"admission.k8s.io/v1beta1\",\n" +
                "    \"request\": {\n" +
                "        \"uid\": \"f5bbd99a-9a38-4004-9b5e-29689a0fb2aa\",\n" +
                "        \"kind\": {\n" +
                "            \"group\": \"apps\",\n" +
                "            \"version\": \"v1\",\n" +
                "            \"kind\": \"Deployment\"\n" +
                "        },\n" +
                "        \"resource\": {\n" +
                "            \"group\": \"apps\",\n" +
                "            \"version\": \"v1\",\n" +
                "            \"resource\": \"deployments\"\n" +
                "        },\n" +
                "        \"requestKind\": {\n" +
                "            \"group\": \"apps\",\n" +
                "            \"version\": \"v1\",\n" +
                "            \"kind\": \"Deployment\"\n" +
                "        },\n" +
                "        \"requestResource\": {\n" +
                "            \"group\": \"apps\",\n" +
                "            \"version\": \"v1\",\n" +
                "            \"resource\": \"deployments\"\n" +
                "        },\n" +
                "        \"name\": \"nginx-deployment\",\n" +
                "        \"namespace\": \"caas-system\",\n" +
                "        \"operation\": \"CREATE\",\n" +
                "        \"userInfo\": {\n" +
                "            \"username\": \"kubernetes-admin\",\n" +
                "            \"groups\": [\n" +
                "                \"system:masters\",\n" +
                "                \"system:authenticated\"\n" +
                "            ]\n" +
                "        },\n" +
                "        \"object\": {\n" +
                "            \"kind\": \"Deployment\",\n" +
                "            \"apiVersion\": \"apps/v1\",\n" +
                "            \"metadata\": {\n" +
                "                \"name\": \"nginx-deployment\",\n" +
                "                \"namespace\": \"caas-system\",\n" +
                "                \"creationTimestamp\": null,\n" +
                "                \"labels\": {\n" +
                "                    \"app\": \"nginx\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"spec\": {\n" +
                "                \"replicas\": 3,\n" +
                "                \"selector\": {\n" +
                "                    \"matchLabels\": {\n" +
                "                        \"app\": \"nginx\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"template\": {\n" +
                "                    \"metadata\": {\n" +
                "                        \"creationTimestamp\": null,\n" +
                "                        \"labels\": {\n" +
                "                            \"app\": \"nginx\"\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"spec\": {\n" +
                "                        \"containers\": [\n" +
                "                            {\n" +
                "                                \"name\": \"nginx\",\n" +
                "                                \"image\": \"nginx:1.14.7\",\n" +
                "                                \"ports\": [\n" +
                "                                    {\n" +
                "                                        \"containerPort\": 80,\n" +
                "                                        \"protocol\": \"TCP\"\n" +
                "                                    }\n" +
                "                                ],\n" +
                "                                \"resources\": {},\n" +
                "                                \"terminationMessagePath\": \"/dev/termination-log\",\n" +
                "                                \"terminationMessagePolicy\": \"File\",\n" +
                "                                \"imagePullPolicy\": \"IfNotPresent\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"restartPolicy\": \"Always\",\n" +
                "                        \"terminationGracePeriodSeconds\": 30,\n" +
                "                        \"dnsPolicy\": \"ClusterFirst\",\n" +
                "                        \"securityContext\": {},\n" +
                "                        \"schedulerName\": \"default-scheduler\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"strategy\": {\n" +
                "                    \"type\": \"RollingUpdate\",\n" +
                "                    \"rollingUpdate\": {\n" +
                "                        \"maxUnavailable\": \"25%\",\n" +
                "                        \"maxSurge\": \"25%\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"revisionHistoryLimit\": 10,\n" +
                "                \"progressDeadlineSeconds\": 600\n" +
                "            },\n" +
                "            \"status\": {}\n" +
                "        },\n" +
                "        \"oldObject\": null,\n" +
                "        \"dryRun\": false,\n" +
                "        \"options\": {\n" +
                "            \"kind\": \"CreateOptions\",\n" +
                "            \"apiVersion\": \"meta.k8s.io/v1\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        deployAdmissionReview = JSON.parseObject(review, AdmissionReview.class);
    }

}
