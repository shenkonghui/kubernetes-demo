/*
Copyright (c) 2019 StackRox Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package main

import (
	"fmt"
	"log"
	"net/http"
	"os"
	"path/filepath"

	"k8s.io/api/admission/v1beta1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"admission/pkg/admission"
)

const (
	tlsDir      = `/run/secrets/tls`
	tlsCertFile = `server.crt`
	tlsKeyFile  = `server.key`
)

var (
	podResource = metav1.GroupVersionResource{Version: "v1", Resource: "pods"}
)

// applySecurityDefaults implements the logic of our example admission controller webhook. For every pod that is created
// (outside of Kubernetes namespaces), it first checks if `runAsNonRoot` is set. If it is not, it is set to a default
// value of `false`. Furthermore, if `runAsUser` is not set (and `runAsNonRoot` was not initially set), it defaults
// `runAsUser` to a value of 1234.
//
// To demonstrate how requests can be rejected, this webhook further validates that the `runAsNonRoot` setting does
// not conflict with the `runAsUser` setting - i.e., if the former is set to `true`, the latter must not be `0`.
// Note that we combine both the setting of defaults and the check for potential conflicts in one webhook; ideally,
// the latter would be performed in a validating webhook admission controller.
func applySecurityDefaults(req *v1beta1.AdmissionRequest) ([]admission.PatchOperation, error) {
	// This handler should only get called on Pod objects as per the MutatingWebhookConfiguration in the YAML file.
	// However, if (for whatever reason) this gets invoked on an object of a different kind, issue a log message but
	// let the object request pass through otherwise.
	if req.Resource != podResource {
		log.Printf("expect resource to be %s", podResource)
		return nil, nil
	}

	// Parse the Pod object.
	raw := req.Object.Raw
	pod := corev1.Pod{}
	if _, _, err := admission.UiversalDeserializer.Decode(raw, nil, &pod); err != nil {
		return nil, fmt.Errorf("could not deserialize pod object: %v", err)
	}

	var patches []admission.PatchOperation
	for i,c := range pod.Spec.Containers{
		if c.Resources.Requests != nil {
			if c.Resources.Requests.Cpu().Value() != 0{
				patches = append(patches, admission.PatchOperation{
					Op:   "replace",
					Path: fmt.Sprintf("/spec/containers/%d/resources/requests/cpu",i),
					Value: "1m",
				})
			}
			if c.Resources.Requests.Memory().Value() != 0{
				patches = append(patches, admission.PatchOperation{
					Op:   "replace",
					Path: fmt.Sprintf("/spec/containers/%d/resources/requests/memory",i),
					Value: "50Mi",
				})
			}
		}
	}


	return patches, nil
}

func main() {
	path := os.Getenv("tlsPath")
	if path == ""{
		path = tlsDir
	}
	certPath := filepath.Join(path, tlsCertFile)
	keyPath := filepath.Join(path, tlsKeyFile)

	mux := http.NewServeMux()
	mux.Handle("/mutate", admission.AdmitFuncHandler(applySecurityDefaults))
	server := &http.Server{
		Addr:    ":8443",
		Handler: mux,
	}
	log.Fatal(server.ListenAndServeTLS(certPath, keyPath))
}
