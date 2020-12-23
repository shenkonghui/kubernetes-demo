package main

import (
	"os"
	"os/signal"
	"syscall"
	"time"

	"k8s.io/apimachinery/pkg/util/wait"
	"k8s.io/client-go/informers"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/cache"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/klog"
)

var onlyOneSignalHandler = make(chan struct{})
var shutdownSignals = []os.Signal{os.Interrupt, syscall.SIGTERM}

func SetupSignalHandler() (stopCh <-chan struct{}) {
	close(onlyOneSignalHandler) // panics when called twice

	stop := make(chan struct{})
	c := make(chan os.Signal, 2)
	signal.Notify(c, shutdownSignals...)
	go func() {
		<-c
		close(stop)
		<-c
		os.Exit(1) // second signal. Exit directly.
	}()

	return stop
}

func main() {
	stopCh := SetupSignalHandler()

	var kubeconfig = "/root/.kube/config"
	cfg, err := clientcmd.BuildConfigFromFlags("", kubeconfig)
	if err != nil {
		klog.Fatalf("Error building kubeconfig: %s", err.Error())
	}

	kubeClient, err := kubernetes.NewForConfig(cfg)
	if err != nil {
		klog.Fatalf("Error building kubernetes clientset: %s", err.Error())
	}

	helloInformerFactory := informers.NewSharedInformerFactory(kubeClient, time.Second*30)
	deployInfomer := helloInformerFactory.Apps().V1().Deployments()

	deployInfomerLister := deployInfomer.Lister()
	sharedIndexInformer := deployInfomer.Informer()

	sharedIndexInformer.AddEventHandler(
		cache.ResourceEventHandlerFuncs{
			AddFunc: controller.enqueueFoo,
		},
	)

	helloInformerFactory.Start(stopCh)
	// 执行自定义任务
	go wait.Until(worker, time.Second, stopCh)
	<-stopCh

}

func worker() {

}
