#!/bin/sh

cd ./bin

rm -R ./org/hy/common/junit


jar cvfm hy.common.base.jar MANIFEST.MF META-INF org

cp hy.common.base.jar ..
rm hy.common.base.jar
cd ..

