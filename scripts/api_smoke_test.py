#!/usr/bin/env python3
"""Demonstrate CRUD against the running PostgreSQL-backed API."""
import json
import os
import time
from http.client import HTTPConnection
from pathlib import Path
from urllib.parse import urlsplit

BASE = os.environ.get("BASE_URL", "http://127.0.0.1:8081").rstrip("/")
parts = urlsplit(BASE)
records = []


def request(method, path, expected, payload=None):
    connection = HTTPConnection(parts.hostname, parts.port or 80, timeout=20)
    body = None if payload is None else json.dumps(payload)
    try:
        connection.request(method, path, body, {"Content-Type": "application/json"})
        response = connection.getresponse()
        raw = response.read().decode()
        data = json.loads(raw) if raw else None
        records.append({"method": method, "path": path, "status": response.status, "body": data})
        print(f"{method} {path} -> HTTP {response.status}")
        assert response.status == expected, (expected, response.status, data)
        return data
    finally:
        connection.close()


def main():
    suffix = str(time.time_ns())
    payload = {"name": "Kim Eduard", "email": "eduard.kim.37324@example.test",
               "groupName": "IT2-2313", "studentId": "37324-demo-" + suffix}
    info = request("GET", "/", 200)
    assert info["studentName"] == "Kim Eduard"
    student = request("POST", "/api/students", 201, payload)
    student_id = student["id"]
    request("GET", "/api/students", 200)
    request("GET", f"/api/students/{student_id}", 200)
    payload["name"] = "Kim Eduard Updated"
    updated = request("PUT", f"/api/students/{student_id}", 200, payload)
    assert updated["name"] == "Kim Eduard Updated"
    request("GET", "/api/students/999999999", 404)
    request("DELETE", f"/api/students/{student_id}", 204)
    request("GET", f"/api/students/{student_id}", 404)
    evidence = Path(__file__).resolve().parents[1] / "evidence"
    evidence.mkdir(exist_ok=True)
    (evidence / "api-smoke.json").write_text(json.dumps(records, indent=2) + "\n")
    print("CRUD PASSED: Kim Eduard | IT2-2313 | 37324")


if __name__ == "__main__":
    main()
