#!/bin/sh

mvn deploy:deploy-file -Dfile=hy.common.base.jar                              -DpomFile=./src/META-INF/maven/org/hy/common/base/pom.xml -DrepositoryId=thirdparty -Durl=http://218.21.3.19:1481/nexus/content/repositories/thirdparty
mvn deploy:deploy-file -Dfile=hy.common.base-sources.jar -Dclassifier=sources -DpomFile=./src/META-INF/maven/org/hy/common/base/pom.xml -DrepositoryId=thirdparty -Durl=http://218.21.3.19:1481/nexus/content/repositories/thirdparty
