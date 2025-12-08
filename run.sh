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
    # start responder (server) in background
    java -cp target/classes com.saif.assessment.AppTcpResponder "$PORT" > responder.log 2>&1 &
    RESP_PID=$!
    sleep 0.5

    # start initiator (client) in foreground
    java -cp target/classes com.saif.assessment.AppTcpInitiator "localhost" "$PORT"

    # give responder time to exit after TERMINATE; clean up if still alive
    sleep 0.5
    if ps -p "$RESP_PID" >/dev/null 2>&1; then
      echo "[run] stopping responder (pid=$RESP_PID)"
      kill "$RESP_PID" >/dev/null 2>&1 || true
      wait "$RESP_PID" 2>/dev/null || true
    fi
    echo "[run] done. See responder.log"
    ;;

  *)
    echo "Usage: ./run.sh [local|multi [port]]" >&2
    exit 1
    ;;
esac
