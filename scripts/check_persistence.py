#!/usr/bin/env python3
"""Verify PostgreSQL data survives Compose container recreation; keep the personal row."""
import json
import subprocess
import time
from pathlib import Path

from api_smoke_test import records, request

project = Path(__file__).resolve().parents[1]
students = request('GET', '/api/students', 200)
student = next((item for item in students if item['studentId'] == '37324'), None)
if student is None:
    student = request('POST', '/api/students', 201, {
        'name': 'Kim Eduard', 'email': 'eduard.kim.37324@example.test',
        'groupName': 'IT2-2313', 'studentId': '37324'})
student_id = student['id']
print('Personal record before recreation:', student_id, student['name'], flush=True)
subprocess.run(['docker', 'compose', 'down'], cwd=project, check=True)
subprocess.run(['docker', 'compose', 'up', '-d', '--wait'], cwd=project, check=True)
for attempt in range(90):
    try:
        restored = request('GET', f'/api/students/{student_id}', 200)
        break
    except (OSError, AssertionError):
        if attempt == 89:
            raise
        time.sleep(1)
assert restored == student, (student, restored)
evidence = project / 'evidence'
evidence.mkdir(exist_ok=True)
(evidence / 'postgres-persistence.json').write_text(json.dumps({
    'passed': True, 'database': 'PostgreSQL', 'recordId': student_id,
    'before': student, 'after': restored, 'httpResults': records}, indent=2) + '\n')
print('PERSISTENCE PASSED: same personal record survived down/up without deleting the volume.')
