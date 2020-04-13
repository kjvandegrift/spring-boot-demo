# Kubernetes Pipeline Demo

## Software Involved in Demo
### Software to be installed
```
docker 19.03.7
kubectl 1.17
minikube 1.8.1
allure 2.10.0
helm 3.1.2
ChartMuseum 0.12.0
```
### Software used in allure build
````
spring-boot-gradle-plugin:2.2.5.RELEASE
allure-gradle:2.8.1
junit5
jib-gradle-plugin 2.1.0 # Used for jib portion of demo and not needed for allure.
````
### Software used in cucumber build
````
spring-boot-gradle-plugin:2.2.5.RELEASE
cucumber-java 4.3.0
cucumber-junit 4.3.0
cucumber-spring 4.3.0
````
##  Kubernetes Prep Work  
``` bash
# Initialize minikube
$ minikube stop; minikube delete
$ minikube start --driver=docker --insecure-registry "10.0.0.0/24"
$ minikube addons enable registry
# Use minikube port forwarding to allow docker to use minikube docker registry
# Open a new terminal window and run the following command
$ kubectl port-forward --namespace kube-system $(kubectl get po -n kube-system | grep -v proxy | grep registry | cut -f1 -d" ") 5000:5000
# List minikube registry
$ curl -s http://localhost:5000/v2/_catalog | jq
# Watch the docker images with the following command 
$ watch -d "docker ps; docker image ls"
# Watch the K8s cluster by opening a new terminal window and run the following command
$ watch -d "kubectl get all"
# Run the Helm Chart registry
$ chartmuseum --debug --port=9090 --storage="local" --storage-local-rootdir="./chartstorage"
```
## Build Application and add to Kubernetes
### Create docker file and docker image and push image to docker registry manually
``` bash
# Build the spring-boot-cucumber application
$ gradle clean build
# Create spring-boot-cucumber docker file 
$ cat >> Dockerfile << EOF
FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY build/dependency/BOOT-INF/lib /app/lib
COPY build/dependency/META-INF /app/META-INF
COPY build/dependency/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.bits.Application"]
EOF
# Create spring-boot-cucumber docker image with docker file 
$ docker build -t localhost:5000/spring-boot-cucumber:latest .
# Push spring-boot-cucumber docker image to minikube docker registry
$ docker push localhost:5000/spring-boot-cucumber
# Verify minikube registry contains spring-boot-cucumber
$ curl -s http://localhost:5000/v2/_catalog | jq
```
### Create docker file and docker image and push image to docker registry with jib
``` bash
# Build the spring-boot-allure application
$ gradle clean build jib
# Verify minikube registry contains spring-boot-allure
$ curl -s http://localhost:5000/v2/_catalog | jq
```
### Add spring-boot-cucumber to kubernetes
``` bash
# Create kubernetes folder to hold the manifest fils
$ mkdir k8s
# Create spring-boot-cucumber kubernetes deployment file
$ kubectl create deployment spring-boot-cucumber --image=localhost:5000/spring-boot-cucumber --dry-run -o yaml > k8s/deployment.yaml
# Remove null entries of creationTimestamp, strategy, resources, status
# Create deployment to deploy spring-boot-cucumber container to kubernetes
$ kubectl apply -f deployment.yaml
# Create spring-boot-cucumber kubernetes service file
$ kubectl expose deployment spring-boot-cucumber --name=spring-boot-cucumber --type=LoadBalancer --port=8082 --target-port=55002 --dry-run -o yaml > k8s/service.yaml
# Create service to expose spring-boot-cucumber deployment to be accessed external to kubernetes cluster
$ kubectl apply -f service.yaml
```
### Create spring-boot-demo Helm chart
``` bash
# Create a Helm chart for the application
$ helm create spring-boot-cucumber
# Rename the generated folder to helm-chart
$ mv spring-boot-cucumber helm-chart
# Remove the generated templates
$ rm helm-chart/templates/*.yaml
# Create a deployemt.yaml file in the helm-chart/templates folder
$ cat >> deployment.yaml << EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "spring-boot-cucumber.fullname" . }}
  labels:
    {{- include "spring-boot-cucumber.labels" . | nindent 4 }}
spec:
  replicas: {{ .Values.replicas }}
  selector:
    matchLabels:
      {{- include "spring-boot-cucumber.selectorLabels" . | nindent 6 }}
  template:
    metadata:
      labels:
        {{- include "spring-boot-cucumber.selectorLabels" . | nindent 8 }}
    spec:
      containers:
        - name: {{ .Chart.Name }}
          image: "{{ .Values.image.repository }}{{ if .Values.image.tag }}:{{ .Values.image.tag }}{{ end }}"
          imagePullPolicy: {{ .Values.image.pullPolicy }}
EOF
# Create a service file in the helm-chart/templates folder
$ cat >> serivce.yaml << EOF
apiVersion: v1
kind: Service
metadata:
  name: {{ include "spring-boot-cucumber.fullname" . }}
  labels:
    {{- include "spring-boot-cucumber.labels" . | nindent 4 }}
spec:
  type: {{ .Values.service.type }}
  ports:
    - port: {{ .Values.service.port }}
      targetPort: {{ .Values.service.targetPort }}
      protocol: {{ .Values.service.protocol }}
  selector:
    {{- include "spring-boot-cucumber.selectorLabels" . | nindent 4 }}
EOF
# Create a values file in the helm-chart folder
$ cat >> values.yaml << EOF
replicas: 1
image:
  repository: localhost:5000/spring-boot-cucumber
  pullPolicy: IfNotPresent
  tag: latest
fullnameOverride: "spring-boot-cucumber"
service:
  type: LoadBalancer
  port: 8082
  targetPort: 55002
EOF
# install the Helm chart into your Kubernetes cluster from the root of your project
$ helm install spring-boot-cucumber helm-chart
```
###  Add Helm Charts to Local Helm Repository
``` bash
# Create Local Respository
$ chartmuseum --debug --port=9090 --storage="local" --storage-local-rootdir="./chartstorage"
# Add local repository to helm repos
$ helm repo add chartmuseum http://localhost:9090
# Create spring-boot-cucumber helm package
$ cd spring-boot-cucumber/helm-chart
$ helm package .
# Upload the package to the local repository
$ curl --data-binary "@spring-boot-cucumber-0.1.0.tgz" http://localhost:9090/api/charts
# Refresh the helm repository
$ helm repo update
# Install helm package into kubernetes 
$ helm install spring-boot-cucumber chartmuseum/spring-boot-cucumber
# Create spring-boot-allure helm package
$ cd spring-boot-allure/helm-chart
$ helm package . 
# Upload the package to the local repository
$ curl --data-binary "@spring-boot-allure-0.1.0.tgz" http://localhost:9090/api/charts
# Refresh the helm repository
$ helm repo update
# Install helm package into kubernetes 
$ helm install spring-boot-allure chartmuseum/spring-boot-allure
```
## Create Umbrella Chart
``` bash
# Create helm chart with child charts
$ helm create spring-boot-chart
# Remve templates
$ rm -rf spring-boot-chart/templates/*
# Create configmap.ymal template
$ cd spring-boot-chart/templates
$ cat >> configmap.yaml << EOF
apiVersion: v1
kind: ConfigMap
metadata:
  name:  {{ .Release.Name }}--configmap
data:
  myvalue: "Spring Boot Demo"
EOF
# Create NOTES.txt
$ cat >> NOTES.txt << EOF
Thank you for installing {{ .Chart.Name }}.
Your release is named {{ .Release.Name }}.
To learn more about the release, try:
  $ helm status {{ .Release.Name }}
  $ helm get all {{ .Release.Name }}
EOF
# Change back to chart root
$ cd spring-boot-chart
# Create values file
$ cat >> values.yaml << EOF
replicaCount: 1
image:
  pullPolicy: IfNotPresent
  tag: latest
service:
  type: LoadBalancer
EOF
# Create Chart.yaml containing subchart information
$ cat >> Chart.yaml << EOF
appVersion: 1.16.0
dependencies:
  - name: spring-boot-cucumber
    repository: '@chartmuseum'
    version: 0.1.0
  - name: spring-boot-allure
    repository: '@chartmuseum'
    version: 0.1.0
EOF
# Change to chart parent directory
$ cd spring-boot
# Load the subchart dependencies for the umbrella chart
$ helm dep update ./spring-boot-chart/
# Create spring-boot helm package
$ cd spring-boot-chart
$ helm package .
# Upload the package to the local repository
$ curl --data-binary "@spring-boot-chart-0.1.0.tgz" http://localhost:9090/api/charts
# Refresh the helm repository
$ helm repo update
# Install helm package into kubernetes 
$ helm install spring-boot-chart chartmuseum/spring-boot-chart
```
## Jenkins Pipeline
``` bash
# Jenkins pulls/clones spring-boot-alure/
# Jenkins runs allure tests

# Jenkins pulls/clones spring-boot-cucumber/
# Show cucumber test reports

# Jenkins runs JMeter tests
# Show JMeter test reports
```
