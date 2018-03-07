

cd .\bin


rd /s/q .\org\hy\common\junit


jar cvfm hy.common.base.jar MANIFEST.MF META-INF org

copy hy.common.base.jar ..
del /q hy.common.base.jar
cd ..





cd .\src
jar cvfm hy.common.base-sources.jar MANIFEST.MF META-INF org 
copy hy.common.base-sources.jar ..
del /q hy.common.base-sources.jar
cd ..
