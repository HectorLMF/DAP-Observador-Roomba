#!/usr/bin/env bash
set -euo pipefail

SRC_DIR=src
OUT_DIR=out

echo "Compilando fuentes..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"
find "$SRC_DIR" -name '*.java' > sources.txt
javac -d "$OUT_DIR" @sources.txt

# Arrancar con JDWP para depuración remota en el puerto 5005
JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

echo "Ejecutando gui.MainApp en modo debug (puerto 5005)..."
java $JAVA_OPTS -cp "$OUT_DIR" gui.MainApp

