#!/usr/bin/env bash
set -euo pipefail
project_dir="$(cd "$(dirname "$0")/.." && pwd)"
mkdir -p "$project_dir/logs"
log_file="$project_dir/logs/Kim_Eduard_system_report.log"

{
    printf 'Student full name: Kim Eduard\nGroup: IT2-2313\nStudent ID: 37324\n'
    printf 'Date and time: '; date --iso-8601=seconds
    printf 'Username: '; whoami
    printf 'Hostname: '; hostname
    printf 'Operating system: '; . /etc/os-release; printf '%s\n' "$PRETTY_NAME"
    printf 'Kernel: '; uname -sr
    printf '\nCPU information\n'; lscpu | sed -n '1,18p'
    printf '\nMemory information\n'; free -h
    printf '\nDisk usage\n'; df -h
    printf '\nAvailable disk space\n'; df -h --output=source,avail,target
} | tee "$log_file"
