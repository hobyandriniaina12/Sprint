@echo off
setlocal enabledelayedexpansion

:: Déclaration des variables
set "jar_name=framework"
set "work_dir=D:\Etude\L2\S4\Mr_Naina\SprintTest\framework"
set "servlet_path=C:\Program Files\Apache Software Foundation\Tomcat 10.1\lib\servlet-api.jar"

set "temp=%work_dir%\temp"
set "src=%work_dir%\src"

@REM CHEMIN AMETRAHANA ANLE LIB
set "lib=D:\Etude\L2\S4\Mr_Naina\SprintTest\test\lib"

:: Effacer le dossier [temp]
if exist "%lib%" (
    rd /s /q "%lib%"
)

:: Effacer le dossier [temp]
if exist "%temp%" (
    rd /s /q "%temp%"
)

:: Créer la structure de dossier
mkdir "%temp%\classes"
mkdir "%lib%"
:: Compilation des fichiers .java dans src avec les options suivantes
:: Note: Assurez-vous que le chemin vers le compilateur Java (javac) est correctement configuré dans votre variable d'environnement PATH.
:: Créer une liste de tous les fichiers .java dans le répertoire src et ses sous-répertoires
dir /s /B "%src%\*.java" > sources.txt
:: Exécuter la commande javac
javac -d "%temp%\classes" -cp "%servlet_path%" @sources.txt
:: Supprimer les fichiers sources.txt et libs.txt après la compilation
del sources.txt

cd "%temp%\classes"
jar cvf "%lib%\%jar_name%.jar" *

cd "%work_dir%"
if exist "%temp%" (
    rd /s /q "%temp%"
)
echo Cretion Jar Terminer.
pause
