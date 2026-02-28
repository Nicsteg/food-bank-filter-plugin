@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  food-bank-filter-plugin startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and FOOD_BANK_FILTER_PLUGIN_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-ea"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\food-bank-filter-plugin-1.0.0.jar;%APP_HOME%\lib\client-1.12.18.jar;%APP_HOME%\lib\runelite-api-1.12.18.jar;%APP_HOME%\lib\http-api-1.2.21.jar;%APP_HOME%\lib\discord-1.4.jar;%APP_HOME%\lib\rlawt-1.7.jar;%APP_HOME%\lib\jshell-1.12.18.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\logback-classic-1.2.9.jar;%APP_HOME%\lib\jopt-simple-5.0.1.jar;%APP_HOME%\lib\guava-23.2-jre.jar;%APP_HOME%\lib\guice-4.1.0-no_aop.jar;%APP_HOME%\lib\gson-2.8.5.jar;%APP_HOME%\lib\flatlaf-extras-3.2.5-rl4.jar;%APP_HOME%\lib\flatlaf-3.2.5-rl4.jar;%APP_HOME%\lib\commons-text-1.2.jar;%APP_HOME%\lib\jna-platform-5.9.0.jar;%APP_HOME%\lib\jna-5.9.0.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\protobuf-javalite-3.21.12.jar;%APP_HOME%\lib\lwjgl-opengl-3.3.2.jar;%APP_HOME%\lib\lwjgl-opengl-3.3.2-natives-linux.jar;%APP_HOME%\lib\lwjgl-opengl-3.3.2-natives-linux-arm64.jar;%APP_HOME%\lib\lwjgl-opengl-3.3.2-natives-macos.jar;%APP_HOME%\lib\lwjgl-opengl-3.3.2-natives-macos-arm64.jar;%APP_HOME%\lib\lwjgl-opengl-3.3.2-natives-windows-x86.jar;%APP_HOME%\lib\lwjgl-opengl-3.3.2-natives-windows.jar;%APP_HOME%\lib\lwjgl-opengl-3.3.2-natives-windows-arm64.jar;%APP_HOME%\lib\lwjgl-opencl-3.3.2.jar;%APP_HOME%\lib\lwjgl-3.3.2.jar;%APP_HOME%\lib\lwjgl-3.3.2-natives-linux.jar;%APP_HOME%\lib\lwjgl-3.3.2-natives-linux-arm64.jar;%APP_HOME%\lib\lwjgl-3.3.2-natives-macos.jar;%APP_HOME%\lib\lwjgl-3.3.2-natives-macos-arm64.jar;%APP_HOME%\lib\lwjgl-3.3.2-natives-windows-x86.jar;%APP_HOME%\lib\lwjgl-3.3.2-natives-windows.jar;%APP_HOME%\lib\lwjgl-3.3.2-natives-windows-arm64.jar;%APP_HOME%\lib\injected-client-1.12.18.jar;%APP_HOME%\lib\okhttp-3.14.9.jar;%APP_HOME%\lib\logback-core-1.2.9.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\commons-lang3-3.7.jar;%APP_HOME%\lib\autocomplete-3.1.1.jar;%APP_HOME%\lib\rsyntaxtextarea-3.1.2.jar;%APP_HOME%\lib\jsvg-1.2.0.jar;%APP_HOME%\lib\rlicn-1.1-windows.jar;%APP_HOME%\lib\json-20090211.jar;%APP_HOME%\lib\okio-1.17.2.jar


@rem Execute food-bank-filter-plugin
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %FOOD_BANK_FILTER_PLUGIN_OPTS%  -classpath "%CLASSPATH%" com.foodbankfilter.FoodBankFilterPluginTest %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable FOOD_BANK_FILTER_PLUGIN_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%FOOD_BANK_FILTER_PLUGIN_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
