@echo off
rem author: yuehan1@lenovo.com
rem date: 2016-06-08
cd %~dp0
cd ..

rem set title
set /p dhversion= < version/version
title Datahub Client %dhversion%

set javahome=%JAVA_HOME%
if "%javahome%"=="" (
	echo error: JAVA_HOME not exist  
	pause
)
echo JAVA_HOME=%javahome%

rem read java version
if not exist "%javahome%/bin/java.exe" (
    echo error: %javahome%/bin/java.exe not exist  
    pause
)
if not exist logs (
   md logs
)

"%javahome%/bin/java" -version 2>version/java.version
set /p jversion= < version/java.version
echo JAVA_VERSION=%jversion%

set v1=1.8
set v2=%jversion:~14,3%

if not %v1% equ %v2% (
	echo error: Datahub Client rely on JRE version least is 1.8
	pause
)

echo Welcome to Datahub Client
echo %DATE:~0,10% %TIME:~0,8% Datahub Client is running...
"%javahome%/bin/java" -cp lib/*;./ com.lenovo.datahub.client.ClientMain