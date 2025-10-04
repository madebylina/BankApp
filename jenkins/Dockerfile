FROM jenkins/jenkins:lts-jdk21

USER root

# Установка зависимостей для сборок
RUN apt-get update && apt-get install -y \
    git \
    maven \
    curl \
    unzip \
    ca-certificates \
    gnupg \
    lsb-release \
    docker.io \
    conntrack \
    socat \
    && rm -rf /var/lib/apt/lists/*

# Установка Helm
RUN curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# Установка kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" \
    && install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# Установка Minikube
RUN curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 \
    && install minikube-linux-amd64 /usr/local/bin/minikube \
    && rm minikube-linux-amd64

# Установка Docker CLI
RUN apt-get update && apt-get install -y docker.io

# Установка плагинов из файла
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

# Инициализация groovy-скриптов
COPY init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/

# Настройка пути (на всякий случай)
USER jenkins
ENV PATH="/usr/local/bin:$PATH"