@echo off
set SrcPath=".\.."
set TargetPath="target"
set BuildScriptPath="BuildScript"
set BuildReleasePath="BuildRelease"
set artifact="*.war"
 
if exist %SrcPath%  (cd %SrcPath%) else (echo “请检查目录是否存在” && goto error)
 
if exist %BuildReleasePath% (rd /S /Q %BuildReleasePath% )
mkdir %BuildReleasePath%
 
call mvn clean package

if %errorlevel%==0 goto success else goto error
 
:error
echo 编译失败!
exit 1

:success
echo 编译成功继续执行...
xcopy /S /Q %TargetPath%\%artifact% %BuildReleasePath%
 
if exist %BuildScriptPath%\pom.xml (copy %BuildScriptPath%\pom.xml %BuildReleasePath%)  else (echo “请检查pom.xml是否存在” && exit 1)
if exist %BuildScriptPath%\assembly.xml (copy %BuildScriptPath%\assembly.xml %BuildReleasePath%)  else (echo “请检查assembly.xml是否存在” && exit 1)
echo build.bat执行成功!