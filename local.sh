version=`date +%s`
imagename="ghcr.io/opendonationassistant/oda-payment-processing:$version"

export JAVA_HOME=/usr/lib/jvm/graalvm-jdk-21.0.2+13.1
mvn clean package -Dpackaging=native-image
podman build . -t $imagename
podman save $imagename | sudo k3s ctr image import -
kubectl get deploy payment-processing -o json | jq ".spec.template.spec.containers[0].image = \"$imagename\"" | kubectl apply -f -
