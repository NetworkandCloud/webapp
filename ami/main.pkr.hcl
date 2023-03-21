variable "aws-region" {
  type    = string
  default = "us-east-1"
}

variable "aws_profile" {
  type    = string
  default = "dev"
}
variable "source_ami" {
  type    = string
  default = "ami-0dfcb1ef8550277af"
}

variable "ssh_username" {
  type    = string
  default = "ec2-user"
}

variable "subnet_id" {
  type    = string
  default = "subnet-0ce098f8f96869352"
}
variable "aws-access-key-id" {
  type    = string
  default = env("aws-access-key-id")
}

variable "aws-secret-access-key" {
  type    = string
  default = env("aws-secret-access-key")
}
variable "ami_user" {
  type    = list(string)
  default = ["776479296963","972493460677"]
}

source "amazon-ebs" "my-ami" {
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = " AMI for CSYE 6225"
  instance_type   = "t2.micro"
  region          = "${var.aws-region}"
  profile         = "${var.aws_profile}"
  ssh_username    = "${var.ssh_username}"
  subnet_id       = "${var.subnet_id}"
  source_ami      = "${var.source_ami}"
  access_key      = "${var.aws-access-key-id}"
  secret_key      = "${var.aws-secret-access-key}"
  ami_users       = "${var.ami_user}"
  ami_regions = [
    var.aws-region
  ]
  aws_polling {
    delay_seconds = 120
    max_attempts  = 50
  }

  ami_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 8
    volume_type           = "gp2"
  }
}

build {
  name = "build-packer"
  sources = [
    "source.amazon-ebs.my-ami"
  ]


  provisioner "file"{
    source = "webapp-0.0.1-SNAPSHOT.jar"
    destination = "webapp-0.0.1-SNAPSHOT.jar"
  }
  provisioner "file" {
    source = "cloudwatch-config.json"
    destination = "/tmp/cloudwatch-config.json"
  }
  provisioner "shell" {
    script = "script.sh"
  }
  provisioner "file" {
    source = "webservice.service"
    destination = "/tmp/"
  }
  /*provisioner "shell" {
    script = "script.sh"
  }

  provisioner "file" {
    source      = "webapp-0.0.1-SNAPSHOT.jar"
    destination = "webapp-0.0.1-SNAPSHOT.jar"
  }


  provisioner "file" {
    source      = "webservice.service"
    destination = "/tmp/"
  }*/

  /*  provisioner "shell" {
      inline = [
        "sudo chmod 770 /home/ec2-user/webapp-0.0.1-SNAPSHOT.jar",
        "sudo cp /tmp/webservice.service /etc/systemd/system",
        "sudo chmod 770 /etc/systemd/system/webservice.service",
        "sudo systemctl start webservice.service",
        "sudo systemctl enable webservice.service",
        "sudo systemctl restart webservice.service",
        "sudo systemctl status webservice.service",
        "echo '****** Copied webservice! *******'"
      ]
    }*/

}
