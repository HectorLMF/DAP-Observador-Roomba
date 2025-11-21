#!/usr/bin/env bash
set -euo pipefail

SRC_DIR=src
OUT_DIR=out

echo "Compilando fuentes..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Crear lista de fuentes
find "$SRC_DIR" -name '*.java' > sources.txt

javac -d "$OUT_DIR" @sources.txt

echo "Ejecutando gui.MainApp..."
java -cp "$OUT_DIR" gui.MainApp
