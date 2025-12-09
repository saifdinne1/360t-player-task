#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-local}"   # local | multi
PORT="${2:-5001}"    # TCP port for multi-process mode

# Build (skip tests for speed; remove -DskipTests to run them)
mvn -q -DskipTests package

case "$MODE" in
  local)
    echo "[run] single-JVM mode"
    exec java -cp target/classes com.saif.assessment.AppLocal
    ;;

  multi)
    echo "[run] two-process mode on port $PORT"
    stdbuf -oL -eL java -cp target/classes com.saif.assessment.AppTcpResponder "$PORT" >> responder.log 2>&1 &

    RESP_PID=$!
    sleep 0.5
    java -cp target/classes com.saif.assessment.AppTcpInitiator "localhost" "$PORT"
    wait "$RESP_PID" || true
    echo "[run] done. See responder.log"
    ;;

  *)
    echo "Usage: ./run.sh [local|multi [port]]" >&2
    exit 1
    ;;
esac
