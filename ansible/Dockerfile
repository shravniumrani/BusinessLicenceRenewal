FROM tomcat:10.1.60-jdk21-temurin
USER root
RUN apt-get update && apt-get install -y --no-install-recommends ansible-core python3 python3-apt curl util-linux && rm -rf /var/lib/apt/lists/*
ENV ANSIBLE_NOCOLOR=1
WORKDIR /ansible
CMD ["sleep", "infinity"]
