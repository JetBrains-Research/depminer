#!/usr/bin/env bash

if [ $# -ne "3" ]; then
  echo "usage: extract-dependencies <path to project directory> <path to desired source root> <path to output folder>"
  exit 1
fi

# https://stackoverflow.com/a/246128
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null && pwd)"
if uname -s | grep -iq cygwin; then
  DIR=$(cygpath -w "$DIR")
  PWD=$(cygpath -w "$PWD")
fi

"$DIR/gradlew" -p "$DIR" extractDependencies -Pdataset="$PWD/$1" -Psource="$PWD/$2" -Poutput="$PWD/$3"
