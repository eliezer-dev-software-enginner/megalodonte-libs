#!/bin/bash

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

PROJECTS=(
  "megalodonte-base"
  "megalodonte-reactivity"
  "megalodonte-theme"
  "megalodonte-router"
  "megalodonte-components"
)

echo "Instalando bibliotecas localmente..."

for project in "${PROJECTS[@]}"; do

  echo ""
  echo "=============================="
  echo "Projeto: $project"
  echo "=============================="

  cd "$ROOT_DIR/$project" || exit

  chmod +x gradlew

  ./gradlew build publishToMavenLocal

  if [ $? -ne 0 ]; then
    echo ""
    echo "ERRO ao buildar $project"
    exit 1
  fi

done

echo ""
echo "Tudo instalado no Maven local."
