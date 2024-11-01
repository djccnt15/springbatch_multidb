@echo off

:: Set the path to your Java installation (replace this with your actual path)
set JAVA_PATH=C:\programming\java\jdk-17.0.0.1\bin\java.exe
set XMS=128M
set XMX=256M

:: Get the current date and format it
for /f "tokens=2 delims==" %%i in ('wmic os get localdatetime /value') do set DATETIME=%%i
set BASE_DATE=%DATETIME:~0,4%-%DATETIME:~4,2%-%DATETIME:~6,2%

:: Set the classpath (replace this with the path to your jar and dependencies)
set CLASSPATH=C:\projects\springbatch_multidb\build\libs\multidb.jar
set CONFIG_PATH=C:\projects\springbatch_multidb\src\main\resources\application.yaml

:: Run the Spring Batch application
%JAVA_PATH% -jar -Dfile.encoding=UTF-8 "%CLASSPATH%" baseDate=%BASE_DATE% -Xms%XMS% -Xmx%XMX% --spring.config.location=%CONFIG_PATH%