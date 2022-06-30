package main

import (
	"context"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/runtime/schema"
	"log"

	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/clientcmd"
)


func main() {
	config, err := clientcmd.BuildConfigFromFlags("", "/root/.kube/config")
	if err != nil {
		log.Fatalln(err)
	}

	groupversion := schema.GroupVersion{
		Group:   "k8s.io",
		Version: "v1",
	}
	config.GroupVersion = &groupversion
	config.APIPath = "/apis"
	config.ContentType = runtime.ContentTypeJSON
	//config.NegotiatedSerializer = serializer.DirectCodecFactory{CodecFactory: api.Codecs}
	restclient, err := rest.RESTClientFor(config)
	if err != nil {
		log.Fatalln(err)
	}

	e := example{}
	err = restclient.Get().
		Resource("examples").
		Namespace("default").
		Name("example1").
		Do(context.TODO()).Into(&e)
}

type example struct{}

