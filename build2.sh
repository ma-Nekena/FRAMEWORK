#!/bin/bash

# 1. Nettoyage
rm -rf bin
mkdir -p bin
rm -f framework-nekena.jar

# 2. Chemins des bibliothèques
TOMCAT_SERVLET_API="lib/servlet-api.jar"
GSON_JAR="lib/gson-2.10.1.jar"
POSTGRESQL_JAR="lib/postgresql.jar"

# 3. Compilation
echo "Compilation des sources du Framework..."

find src -name "*.java" > sources.txt

javac --release 21 -parameters -d bin \
-cp "$TOMCAT_SERVLET_API:$GSON_JAR:$POSTGRESQL_JAR" \
@sources.txt

if [ $? -eq 0 ]; then

    echo "[FRAMEWORK] Compilation réussie."

    # 4. Intégration du pilote PostgreSQL
    mkdir -p bin/postgresql

    cd bin || exit 1

    jar xf "../$POSTGRESQL_JAR"

    rm -f META-INF/MANIFEST.MF

    cd ..

    # 5. Génération du JAR
    jar cf framework-nekena.jar -C bin .

    echo "[FRAMEWORK] framework-nekena.jar généré avec succès."

else
    echo "[FRAMEWORK] Erreur lors de la compilation."
    exit 1
fi

rm -f sources.txt
