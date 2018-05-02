#!/bin/sh
TargetPath=../target
BuildScriptPath=../BuildScript
BuildReleasePath=../BuildRelease
artifact=*.war
RootPath=../
ScriptsPath=../scripts

rm -rf $BuildReleasePath
if [ ! -d "$BuildReleasePath" ]; then
mkdir $BuildReleasePath
fi

cd ../
ll
mvn clean package
cd BuildScript

if (ls $TargetPath/$artifact) > /dev/null 2>&1 ;
then
echo "exist $artifact,copy to the BuildReleasePath"
cp $TargetPath/$artifact $BuildReleasePath
else
echo "$artifact is not found,Build Failure!"
exit 1
fi


if [ -f "$BuildScriptPath/pom.xml" ]; then 
echo "exist pom.xml,copy to the BuildReleasePath"
cp $BuildScriptPath/pom.xml $BuildReleasePath
else
echo "pom.xml not found" 
exit 1
fi

if [ -f "$RootPath/appspec.yml" ]; then
echo "exist appspec.yml,copy to the BuildReleasePath"
cp $RootPath/appspec.yml $BuildReleasePath
else
echo "appspec.yml not found"
exit 1
fi

if [ -d "$ScriptsPath" ]; then
echo "exist ScriptsPath,copy to the BuildReleasePath"
cp $ScriptsPath -r $BuildReleasePath
else
echo "ScriptsPath not found"
exit 1
fi

if [ -f "$BuildScriptPath/assembly.xml" ]; then 
echo "exist assembly.xml,copy to the BuildReleasePath"
cp $BuildScriptPath/assembly.xml $BuildReleasePath
else
echo "assembly.xml not found" 
exit 1
fi