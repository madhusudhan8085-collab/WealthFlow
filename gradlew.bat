@rem WealthFlow Gradle Wrapper
@echo off
set "DIR=%~dp0"
if "%JAVA_HOME%"=="" set "JAVA_HOME=C:\Program Files\Java\jdk-25"
if "%ANDROID_HOME%"=="" set "ANDROID_HOME=C:\Users\MADHUSUDAN\AppData\Local\Android\Sdk"
if "%GRADLE_USER_HOME%"=="" set "GRADLE_USER_HOME=%DIR%.gradle_home"
"C:\Users\MADHUSUDAN\.gradle\wrapper\dists\gradle-9.3.1-bin\23ovyewtku6u96viwx3xl3oks\gradle-9.3.1\bin\gradle.bat" %*
