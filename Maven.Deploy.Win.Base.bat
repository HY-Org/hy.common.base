start mvn deploy:deploy-file -Dfile=hy.common.base.jar                                                   -DpomFile=./src/main/resources/META-INF/maven/cn.openapis/hy.common.base/pom.xml -DrepositoryId=thirdparty -Durl=http://HY-ZhengWei:8081/repository/thirdparty
start mvn deploy:deploy-file -Dfile=hy.common.base-sources.jar -Dclassifier=sources -DpomFile=./src/main/resources/META-INF/maven/cn.openapis/hy.common.base/pom.xml -DrepositoryId=thirdparty -Durl=http://HY-ZhengWei:8081/repository/thirdparty
