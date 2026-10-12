# 3D Building Visualization System Using Python Full Stack Development

## Overview
A comprehensive full-stack web application designed for architectural building visualization. Users can submit customized building requirements, floor plans, and room dimensions, dynamically generating procedural 3D building models rendered in real-time with Three.js.

---

## Technology Stack
- **Frontend**: HTML5, CSS3, JavaScript, Bootstrap 5, Three.js (WebGL OrbitControls)
- **Backend**: Python 3.10+, Flask Web Framework, REST API endpoints
- **Database**: SQLite with SQLAlchemy ORM, Flask-Login user authentication

---

## Features
1. **Interactive 3D Model Generation**: Dynamic generation of multiple floors, individual rooms, slabs, walls, doors, windows, staircases, and roofs.
2. **Room Color Customization**: Live color picker for Bedrooms, Kitchens, Bathrooms, Living Rooms, Dining Rooms, and Balconies.
3. **Camera & Floor Inspection**: 360-degree rotation, zoom, pan, floor-by-floor isolation, cutaway interior views, and camera presets (Iso, Front, Top).
4. **Save, Load & Edit**: Persistent SQLite database storage for registered users.
5. **Architectural Reports & 3D Export**: Floor-by-floor room schedule, budget estimation, and `.OBJ` 3D model export compatible with Blender and AutoCAD.

---

## VS Code Setup & Local Execution Guide

### Prerequisites
- Python 3.9 or higher installed
- Visual Studio Code

### Step 1: Open the Project in VS Code
Open the `python_fullstack_project` directory in VS Code:
```bash
cd python_fullstack_project
```

### Step 2: Create and Activate a Python Virtual Environment
- **On Linux / macOS**:
  ```bash
  python3 -m venv venv
  source venv/bin/activate
  ```
- **On Windows**:
  ```powershell
  python -m venv venv
  .\venv\Scripts\activate
  ```

### Step 3: Install Required Dependencies
```bash
pip install -r requirements.txt
```

### Step 4: Run the Application
```bash
python app.py
```

### Step 5: Open in Web Browser
Navigate to:
```
http://localhost:5000
```

### Default Demo Credentials
- **Username**: `demo`
- **Password**: `demo123`
*(Or register a new account from the registration page)*
