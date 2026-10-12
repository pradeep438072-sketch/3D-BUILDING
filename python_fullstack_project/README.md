# 3D Building Visualization System Using Python Full Stack Development

## Overview
A full-stack web application designed for architectural building visualization. Users can submit customized building requirements, floor plans, and room dimensions, dynamically generating procedural 3D building models rendered in real-time with Three.js.

---

## Technology Stack
- **Frontend**: HTML5, CSS3, JavaScript, Bootstrap 5, Three.js (WebGL OrbitControls)
- **Backend**: Python 3.10+, Flask Web Framework, REST API endpoints
- **Database**: SQLite with SQLAlchemy ORM, Flask-Login user authentication
- **Deployment**: Vercel Serverless with `@vercel/python` or local Gunicorn/Flask

---

## Root Cause Analysis: 500 FUNCTION_INVOCATION_FAILED on Vercel

### 1. Read-Only Serverless Filesystem (Primary Cause)
* **Cause**: On AWS Lambda / Vercel Serverless, the application runs inside a read-only environment (`/var/task`). SQLite attempts to create or write `building_system.db` in the current working directory, triggering `sqlite3.OperationalError: unable to open database file` or `sqlite3.ReadOnlyError: attempt to write a readonly database` at module import time (`db.create_all()`). This crashes the serverless runtime before any request can be handled.
* **Fix**: In `app.py`, the database path automatically resolves to `/tmp/building_system.db` when running on Vercel (`os.environ.get('VERCEL')`), which is the only writable scratchpad directory in serverless environments. If an external PostgreSQL or Neon/Supabase database is provided via `DATABASE_URL`, it is automatically preferred.

### 2. Missing Serverless Routing Configuration
* **Cause**: Without `vercel.json` and a serverless entry point (`api/index.py`), Vercel cannot determine how to route HTTP requests to the WSGI Flask `app` object.
* **Fix**: Added `vercel.json` with `@vercel/python` routing and standard `api/index.py` entrypoint.

### 3. Password Hash Truncation Risk
* **Cause**: Werkzeug 3 `scrypt` hashing produces string lengths over 160 characters. A `String(128)` column can cause SQL overflow errors.
* **Fix**: Updated `models.py` column definition to `String(256)`.

### 4. Unused Native Dependencies in `requirements.txt`
* **Cause**: Packages requiring C-extensions (such as `reportlab`) can cause compile-time failures or import errors in Amazon Linux serverless containers.
* **Fix**: Cleaned `requirements.txt` to include only necessary pure Python packages.

---

## Step-by-Step Vercel Deployment Guide

### Option A: If Deploying from the `python_fullstack_project` Folder
1. In your Vercel Dashboard project settings:
   - Go to **Settings** > **General**.
   - Under **Root Directory**, enter `python_fullstack_project` (or leave as `./` if your repo root already contains `app.py`).
2. Set Environment Variables (Optional):
   - `SECRET_KEY`: Any secure random string (e.g. `your-production-secret-key-2026`).
   - `DATABASE_URL`: Optional (e.g., your Neon/PostgreSQL connection string). If omitted, the app uses `/tmp/building_system.db`.
3. Push changes to GitHub:
   ```bash
   git add .
   git commit -m "Fix Vercel serverless SQLite path and deployment entrypoints"
   git push origin main
   ```
4. Vercel will automatically redeploy. Verify status at `/api/health`.

### Option B: Deploying Using Vercel CLI
```bash
npm i -g vercel
vercel --prod
```

---

## Local VS Code Execution

### 1. Setup Virtual Environment
```bash
python3 -m venv venv
source venv/bin/activate  # Linux/macOS
# or: .\venv\Scripts\activate  # Windows
```

### 2. Install Dependencies
```bash
pip install -r requirements.txt
```

### 3. Run Locally
```bash
python app.py
```
Open [http://localhost:5000](http://localhost:5000) in your browser.

Demo credentials:
- **Username**: `demo`
- **Password**: `demo123`
