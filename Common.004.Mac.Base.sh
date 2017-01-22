#!/bin/sh

cd ./bin

jar cvfm hy.common.base.jar MANIFEST.MF LICENSE org

cp hy.common.base.jar ..
rm hy.common.base.jar
cd ..

