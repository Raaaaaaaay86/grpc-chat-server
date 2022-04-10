
Please run underneath command in the "manifest/" path before deploy envoy.
```
 kubectl create configmap envoy-config --from-file=config/envoy/envoy.yaml
```