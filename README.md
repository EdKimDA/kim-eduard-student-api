# Kim Eduard Student API

Student: **Kim Eduard**, group **IT2-2313**, Student ID **37324**.

Spring Boot CRUD application with PostgreSQL, Gradle, Docker Compose and Jenkins CI.
Documentation status: base.

## Requirements and versions

- Ubuntu or another Linux build agent, JDK 21, Git, Docker with Compose, Python 3 and curl.
- Spring Boot 3.5.16; Gradle Wrapper 8.14.3 (no global Gradle installation required).
- PostgreSQL 17 in Docker. Automated tests use an isolated H2 database; live CRUD and persistence checks use PostgreSQL.
- Application URL: `http://127.0.0.1:8081`; application port inside Docker: `8080`.
- Jenkins can remain on host port `8080`.

## Build and test

```bash
chmod +x gradlew
./gradlew clean assemble
./gradlew test
```

The runnable artifact is `build/libs/app.jar`. The application version is set by `appVersion` in `gradle.properties`.

## Linux administration

```bash
mkdir -p scripts logs backups
touch logs/Kim_Eduard_system_report.log
chmod 744 scripts/Kim_Eduard_system_report.sh
./scripts/Kim_Eduard_system_report.sh
ls -l scripts/Kim_Eduard_system_report.sh
cp README.md backups/Kim_Eduard_README_backup.md
cmp README.md backups/Kim_Eduard_README_backup.md
ps aux | grep '[j]ava'
tail -n 5 logs/Kim_Eduard_system_report.log
grep 'Kim' logs/Kim_Eduard_system_report.log > Kim_Eduard_log_analysis.txt
```

`744` grants owner read/write/execute, group and others read only. `>` replaces a file; `>>` appends.

## Local environment

Copy `.env.example` to `.env` and replace `POSTGRES_PASSWORD` with a locally generated value. Keep `.env` untracked and readable only by its owner (`chmod 600 .env`).

```bash
docker compose config --quiet
docker compose up -d --build
docker compose ps
curl http://127.0.0.1:8081/
curl http://127.0.0.1:8081/actuator/health
```

The application connects to `db:5432`, the Compose service name. `localhost` inside the application container means the application container itself. PostgreSQL readiness is checked with `pg_isready`; the app waits for `service_healthy`.

Named volume: `kim-eduard-student-api-postgres-data`. Dedicated network: `kim-eduard-student-api-network`.

## CRUD endpoints

| Method | Path | Result |
|---|---|---|
| POST | `/api/students` | Create, HTTP 201 and Location |
| GET | `/api/students` | List, HTTP 200 |
| GET | `/api/students/{id}` | Read, HTTP 200 or 404 |
| PUT | `/api/students/{id}` | Update, HTTP 200 or 404 |
| DELETE | `/api/students/{id}` | Delete, HTTP 204 or 404 |

Request JSON:

```json
{"name":"Kim Eduard","email":"eduard.kim.37324@example.test","groupName":"IT2-2313","studentId":"37324"}
```

The database-generated `id` identifies a record. `studentId` is the educational identifier and must be unique. Invalid input returns 400; duplicate student IDs return 409.

Run the repeatable live API demonstration:

```bash
python3 scripts/api_smoke_test.py
```

This creates a personal test record with a unique demo suffix and removes that same record after verification. HTTP responses are saved to `evidence/api-smoke.json`.

## Containers and data persistence

```bash
docker compose logs --tail 30 app
docker inspect --format '{{.State.Status}} {{.Config.Image}}' kim-eduard-student-api-app
docker compose exec app java -version
docker compose stop app
docker compose start app
docker compose down
docker compose up -d
```

Create a student before `down` and retrieve the same database-generated ID after `up`. The named volume preserves records across container recreation. `down -v` removes the volume and its data; it is not part of the persistence demonstration.

Inspect an image without exposing database credentials:

```bash
docker image inspect kim-eduard-student-api:v1.0.0-kim-eduard-37324 --format 'ID={{.Id}} Size={{.Size}} Created={{.Created}}'
```

For a new application version, update `appVersion` and the Compose image tag together, commit the change, then run `docker compose up -d --build`.

## Jenkins

Job: `Kim_Eduard_Student_API`. Definition: Pipeline script from SCM; Git repository: `https://github.com/EdKimDA/kim-eduard-student-api.git`; branch: `*/main`; script path: `Jenkinsfile`.

The Linux agent needs JDK 21 and Docker access. Gradle Wrapper downloads the pinned Gradle distribution. No database password is required for the CI tests because they use an isolated test database. Store any private repository or registry credentials in Jenkins Credentials and reference their credential IDs; do not commit tokens or passwords.

Stages: Checkout, Build, Test, Docker Build, Verify. Test failures stop the pipeline before image creation. Verify inspects the resulting image; it does not deploy the application.

`pollSCM('H/2 * * * *')` checks for source changes approximately every two minutes. After the first manual successful build, push a new commit and verify the next build is marked as started by an SCM change.

For a controlled failure, use `feature/kim-eduard-ci-demo`, temporarily configure the Job to read that branch, add a failing test, commit/push and run. Fix the test and rerun. Restore `*/main` afterwards. Keep the intentional error out of main.

CI integrates changes with automated build/test checks. Continuous Delivery keeps a releasable result ready for an approved release; Continuous Deployment automatically releases passing changes. Deployment is not required for this midterm.

## Git evidence

Personal branches: `feature/kim-eduard-crud`, `feature/kim-eduard-documentation`, and the isolated CI demonstration branch. The README conflict is intentionally created on the `Documentation status` line and manually resolved by keeping both meaningful descriptions.

```bash
git remote -v
git log --oneline --graph --all --decorate
git branch -a
git tag
git check-ignore .env logs/Kim_Eduard_system_report.log
```

Release tag: `v1.0-kim-eduard`. Credentials, `.env`, local Gradle caches, generated logs and database data are not committed.
