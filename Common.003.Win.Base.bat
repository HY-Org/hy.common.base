

cd .\target\classes


rd /s/q .\org\hy\common\junit


jar cvfm hy.common.base.jar META-INF/MANIFEST.MF META-INF org

copy hy.common.base.jar ..\..
del /q hy.common.base.jar
cd ..\..





cd .\src\main\java
xcopy /S ..\resources\* .
jar cvfm hy.common.base-sources.jar META-INF\MANIFEST.MF META-INF org 
copy hy.common.base-sources.jar ..\..\..
del /Q hy.common.base-sources.jar
rd /s/q META-INF
cd ..\..\..
