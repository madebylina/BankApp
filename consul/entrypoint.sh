#!/bin/sh
set -e

# Запускаем Consul в фоне
consul agent -dev -client=0.0.0.0 &

# PID процесса Consул
CONSUL_PID=$!

echo 'Waiting for Consul to be ready...'

# Ждем, пока Consul не станет готов (проверяем endpoint)
until curl -s http://localhost:8500/v1/agent/self | grep -q '"Config"' ; do
  echo 'Consul is not ready yet, retrying...'
  sleep 1
done

echo 'Consul is ready. Importing configuration from YAML...'

# Читаем содержимое YAML-файла
yaml_content=$(cat /config/config.yaml)

# Помещаем YAML в Consul KV
consul kv put 'config/application/data' "$yaml_content"

echo 'Import finished.'

# Ждем завершения процесса Consul, чтобы контейнер не завершился
wait $CONSUL_PID