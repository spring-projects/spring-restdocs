#!/bin/bash
set -e

case "$1" in
	java8)
		 echo "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jdk_x64_linux_hotspot_8u292b10.tar.gz"
	;;
  *)
		echo $"Unknown java version"
		exit 1
esac
