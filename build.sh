#!/bin/bash

# 1. Nettoyage des anciens dossiers de build
rm -rf bin
mkdir bin
rm -f mon-framework.jar

# 2. Chemin vers l'API Servlet de Tomcat (À adapter selon ton installation)
# Par exemple: "/opt/tomcat/lib/servlet-api.jar" ou "/usr/local/Cellar/tomcat/..."
TOMCAT_SERVLET_API="/Users/rotsy/Documents/TOMCAT/lib/servlet-api.jar" 

echo "Compilation des sources du Framework..."
find src -name "*.java" > sources.txt
javac -d bin -cp "$TOMCAT_SERVLET_API" @sources.txt
rm sources.txt

if [ $? -eq 0 ]; then
    echo "[FRAMEWORK] Compilation réussie. Création du fichier .jar..."
    jar cf framework-sprit0.jar -C bin .
    echo "[FRAMEWORK] framework-sprit0.jar généré avec succès."
else
    echo "[FRAMEWORK] Erreur lors de la compilation."
    exit 1
fi