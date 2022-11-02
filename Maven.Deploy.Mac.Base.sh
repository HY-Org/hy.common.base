#!/bin/sh

start mvn install:install-file -Dfile=hy.common.base.jar                              -DpomFile=./src/main/resources/META-INF/maven/cn.openapis/hy.common.base/pom.xml
start mvn install:install-file -Dfile=hy.common.base-sources.jar -Dclassifier=sources -DpomFile=./src/main/resources/META-INF/maven/cn.openapis/hy.common.base/pom.xml


mvn deploy:deploy-file         -Dfile=hy.common.base.jar                              -DpomFile=./src/main/resources/META-INF/maven/cn.openapis/hy.common.base/pom.xml -DrepositoryId=thirdparty -Durl=http://HY-ZhengWei:8081/repository/thirdparty
mvn deploy:deploy-file         -Dfile=hy.common.base-sources.jar -Dclassifier=sources -DpomFile=./src/main/resources/META-INF/maven/cn.openapis/hy.common.base/pom.xml -DrepositoryId=thirdparty -Durl=http://HY-ZhengWei:8081/repository/thirdparty
