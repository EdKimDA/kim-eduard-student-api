#!/usr/bin/env bash
set -euo pipefail
project_dir="$(cd "$(dirname "$0")/.." && pwd)"
cd "$project_dir"
mkdir -p logs backups evidence
touch logs/Kim_Eduard_system_report.log
if [[ ! -f .env ]]; then
    python3 - <<'PY'
from pathlib import Path
import secrets
text = Path('.env.example').read_text()
Path('.env').write_text(text.replace('CHANGE_ME_BEFORE_START', secrets.token_urlsafe(24)))
PY
fi
chmod 600 .env
chmod 755 gradlew
chmod 744 scripts/*.sh scripts/*.py
cp README.md backups/Kim_Eduard_README_backup.md
printf 'Local setup ready for Kim Eduard. Database password was not printed.\n'
