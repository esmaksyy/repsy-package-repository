📦 Package Repository Backend
A modular and extensible RESTful API, built with Spring Boot and PostgreSQL, enabling secure storage and versioning of digital software packages.
It supports file uploads (with meta-information), retrieval, and integrates easily into modern CI/CD and development workflows.

🚀 Features
Upload package files with custom meta-data (JSON)
Download package files or meta information by name/version/filename
Consistent validation and error handling
File type and extension checks for security
Pluggable storage layer for local or cloud backends
Built with best practices, ready for production & extension

⚙️ Getting Started
Prerequisites
Java 17 or newer
Maven 3.6+
PostgreSQL (installed or via Docker)
(Optional) Docker & docker-compose for quick provision
Clone & Build
SH

git clone https://github.com/YOUR_USERNAME/YOUR_REPO.git
cd YOUR_REPO
mvn clean package
Configuration
Copy or edit the config:

src/main/resources/application.properties
Run Locally
SH

java -jar target/app-main-*.jar
Server will run on http://localhost:8080 by default.

🐳 Run with Docker (Optional)
If you want to run everything with Docker and PostgreSQL quickly:

SH

docker-compose up --build
(Assume you have a docker-compose.yml in the project)

🧩 API Endpoints
1. Upload a Package
HTTP

POST /{packageName}/{version}
Content-Type: multipart/form-data
Fields
package.rep (file, required): The package file, must end with .rep
meta.json (file, required): Meta-information file, must be a valid JSON
Example cURL
SH

curl -X POST http://localhost:8080/mypackage/1.0.0 \
     -F "package.rep=@/path/to/package.rep" \
     -F "meta.json=@/path/to/meta.json"
meta.json Example
JSON

{
  "name": "mypackage",
  "version": "1.0.0",
  "author": "Jane Developer"
}
The API validates that name and version match the path variables.
2. Download a Package or Its Metadata
HTTP

GET /{packageName}/{version}/{filename}
Examples
SH

curl http://localhost:8080/mypackage/1.0.0/package.rep --output mypackage.rep
curl http://localhost:8080/mypackage/1.0.0/meta.json --output mypackage-meta.json
❌ Error Handling
If files are missing:
400 Bad Request – "Both package.rep and meta.json files are required."
If file extension does not match:
400 Bad Request – "package.rep file must have .rep extension"
If validation fails:
400 Bad Request – Details in response
If package not found (GET):
404 Not Found
All errors are returned with a clear, human-readable message.

🧑‍💻 Development & Contribution
Fork & clone the repo
Setup your database or use Docker
Use logical Git commits (see commit plan below)
Run tests:
SH

mvn test
Pull requests are welcome!
Suggested Commit Plan
Project setup & configuration
Entity & Repository
Controllers & Endpoints
Storage Service
Error Handling & Validation
Docker + CI/CD files
README and documentation
