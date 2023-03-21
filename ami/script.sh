#!/bin/bash
sudo yum update
sudo yum upgrade
echo Start Java Installation
sudo yum install java-17-amazon-corretto -y
echo "export JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto.x86_64" >>~/.bashrc
echo "export PATH=$PATH:$JAVA_HOME/bin" >>~/.bashrc
echo Java Location
java --version
echo completed Java Installation
sudo yum install -y tomcat - y
sudo systemctl start tomcat
sudo systemctl enable tomcat
sudo curl -o amazon-cloudwatch-agent.rpm https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
sudo rpm -U amazon-cloudwatch-agent.rpm