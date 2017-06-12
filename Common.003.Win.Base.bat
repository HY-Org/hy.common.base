

cd .\bin

rd /s/q .\org\hy\common\junit


jar cvfm hy.common.base.jar MANIFEST.MF LICENSE org

copy hy.common.base.jar ..
del /q hy.common.base.jar
cd ..

