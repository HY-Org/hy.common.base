start mvn deploy:deploy-file -Dfile=hy.common.base.jar                              -DpomFile=./src/META-INF/maven/org/hy/common/base/pom.xml -DrepositoryId=thirdparty -Durl=http://HY-ZhengWei:1481/repository/thirdparty
start mvn deploy:deploy-file -Dfile=hy.common.base-sources.jar -Dclassifier=sources -DpomFile=./src/META-INF/maven/org/hy/common/base/pom.xml -DrepositoryId=thirdparty -Durl=http://HY-ZhengWei:1481/repository/thirdparty
