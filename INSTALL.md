# Building gen2sat from Source

This document records every step taken to compile gen2sat into a runnable JAR
on a Linux machine without root access.

---

## Prerequisites

| Java JDK (javac + java) 
| Apache Ant

## Step 0.1 (if you do not have java installed) — Install a local JDK (no root required)

```bash
mkdir -p ~/opt
cd ~/opt
curl -L "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.13%2B11/OpenJDK17U-jdk_x64_linux_hotspot_17.0.13_11.tar.gz" \
     -o jdk17.tar.gz
tar xzf jdk17.tar.gz
# JDK now lives at ~/opt/jdk-17.0.13+11/
~/opt/jdk-17.0.13+11/bin/javac -version   # should print: javac 17.0.13
```

## Step 0.2 (if you do not have and installed) — Install Apache Ant (no root required)

```bash
cd ~/opt
curl -L "https://archive.apache.org/dist/ant/binaries/apache-ant-1.10.14-bin.tar.gz" \
     -o ant.tar.gz
tar xzf ant.tar.gz
# Ant now lives at ~/opt/apache-ant-1.10.14/
~/opt/apache-ant-1.10.14/bin/ant -version
```

## Step 1 — Build the JAR

From the repository root:

```bash
ant -f build-portable.xml
```

## Step 2 — Run

```bash
java -jar gen2sat.jar <path-to-input-file>

# Example:
java -jar gen2sat.jar example.txt
```
