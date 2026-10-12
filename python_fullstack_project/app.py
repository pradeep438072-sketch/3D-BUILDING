import os
import sys
import json
import logging

# Ensure base directory is in sys.path before any local imports
BASE_DIR = os.path.abspath(os.path.dirname(__file__))
if BASE_DIR not in sys.path:
    sys.path.insert(0, BASE_DIR)

from flask import Flask, render_template, request, redirect, url_for, flash, jsonify
from werkzeug.security import generate_password_hash, check_password_hash
from flask_login import LoginManager, login_user, logout_user, login_required, current_user
from models import db, User, Building

# Setup Logging
logging.basicConfig(level=logging.INFO, stream=sys.stderr)
logger = logging.getLogger(__name__)

# Base directory path resolution for robust template & static discovery
TEMPLATES_DIR = os.path.join(BASE_DIR, 'templates')
STATIC_DIR = os.path.join(BASE_DIR, 'static')


app = Flask(
    __name__,
    template_folder=TEMPLATES_DIR,
    static_folder=STATIC_DIR
)

# Configuration
app.config['SECRET_KEY'] = os.environ.get('SECRET_KEY', 'architect-dev-secret-key-2026')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# Database Path Resolution:
# Vercel serverless containers are strictly read-only except for /tmp.
# If DATABASE_URL is provided, use it (PostgreSQL / Neon / Supabase).
# Otherwise, on Vercel use /tmp/building_system.db, and locally use ./building_system.db.
database_url = os.environ.get('DATABASE_URL')
if database_url:
    if database_url.startswith('postgres://'):
        database_url = database_url.replace('postgres://', 'postgresql://', 1)
    app.config['SQLALCHEMY_DATABASE_URI'] = database_url
    logger.info("Using external DATABASE_URL")
elif os.environ.get('VERCEL') or os.environ.get('AWS_LAMBDA_FUNCTION_NAME'):
    db_path = os.path.join('/tmp', 'building_system.db')
    app.config['SQLALCHEMY_DATABASE_URI'] = f'sqlite:///{db_path}'
    logger.info(f"Vercel serverless environment detected. SQLite path: {db_path}")
else:
    db_path = os.path.join(BASE_DIR, 'building_system.db')
    app.config['SQLALCHEMY_DATABASE_URI'] = f'sqlite:///{db_path}'
    logger.info(f"Local environment detected. SQLite path: {db_path}")

db.init_app(app)

login_manager = LoginManager()
login_manager.login_view = 'login'
login_manager.login_message_category = 'warning'
login_manager.init_app(app)

@login_manager.user_loader
def load_user(user_id):
    try:
        return User.query.get(int(user_id))
    except Exception as e:
        logger.error(f"Error loading user {user_id}: {e}")
        return None

def get_default_sample_plan():
    return {
        "name": "Villa Horizon",
        "type": "Residential",
        "plotWidth": 18.0,
        "plotLength": 16.0,
        "floorHeight": 3.2,
        "roofStyle": "Flat Terrace",
        "roomColors": {
            "Bedroom": "#7dd3fc",
            "Kitchen": "#fdba74",
            "Bathroom": "#86efac",
            "Living Room": "#fde047",
            "Dining Room": "#f472b6",
            "Balcony": "#cbd5e1",
            "Corridor": "#e2e8f0"
        },
        "floors": [
            {
                "name": "Ground Floor",
                "rooms": [
                    {"name": "Living Room", "type": "Living Room", "x": -4.0, "z": 3.0, "width": 8.0, "length": 7.0, "color": "#fde047"},
                    {"name": "Kitchen", "type": "Kitchen", "x": 4.5, "z": 4.5, "width": 6.0, "length": 5.0, "color": "#fdba74"},
                    {"name": "Dining Room", "type": "Dining Room", "x": 4.5, "z": -1.0, "width": 6.0, "length": 5.0, "color": "#f472b6"},
                    {"name": "Guest Bathroom", "type": "Bathroom", "x": -4.0, "z": -4.0, "width": 4.5, "length": 4.0, "color": "#86efac"}
                ]
            },
            {
                "name": "First Floor",
                "rooms": [
                    {"name": "Master Bedroom", "type": "Bedroom", "x": -4.0, "z": 3.0, "width": 8.0, "length": 7.0, "color": "#7dd3fc"},
                    {"name": "Guest Bedroom", "type": "Bedroom", "x": 4.5, "z": -1.0, "width": 6.0, "length": 6.0, "color": "#7dd3fc"},
                    {"name": "Master Bath", "type": "Bathroom", "x": 4.5, "z": 4.5, "width": 5.0, "length": 4.0, "color": "#86efac"},
                    {"name": "Balcony", "type": "Balcony", "x": -4.0, "z": -4.0, "width": 7.0, "length": 4.0, "color": "#cbd5e1"}
                ]
            }
        ]
    }

# Safe, Resilient Database Initialization
_db_initialized = False

def ensure_db_initialized():
    global _db_initialized
    if not _db_initialized:
        try:
            db.create_all()
            demo_user = User.query.filter_by(username='demo').first()
            if not demo_user:
                demo_user = User(
                    username='demo',
                    email='demo@architect.com',
                    password_hash=generate_password_hash('demo123')
                )
                db.session.add(demo_user)
                db.session.commit()

                sample = Building(
                    user_id=demo_user.id,
                    name="Villa Horizon",
                    building_type="Residential",
                    plot_width=18.0,
                    plot_length=16.0,
                    total_area=288.0,
                    floor_count=2,
                    floor_height=3.2,
                    roof_style="Flat Terrace",
                    building_style="Modern Minimalist",
                    data_json=json.dumps(get_default_sample_plan())
                )
                db.session.add(sample)
                db.session.commit()
            _db_initialized = True
            logger.info("Database initialized successfully.")
        except Exception as e:
            logger.error(f"Database initialization warning: {e}", exc_info=True)

@app.before_request
def before_request():
    ensure_db_initialized()

# Healthcheck Endpoint for Vercel Monitoring
@app.route('/api/health')
def health_check():
    ensure_db_initialized()
    return jsonify({
        "status": "healthy",
        "environment": "vercel" if os.environ.get('VERCEL') else "standard",
        "database_uri_prefix": app.config['SQLALCHEMY_DATABASE_URI'].split('///')[0] if '///' in app.config['SQLALCHEMY_DATABASE_URI'] else "external",
        "db_initialized": _db_initialized
    })

# Routes
@app.route('/')
def index():
    return render_template('index.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form.get('username', '').strip()
        password = request.form.get('password', '')
        user = User.query.filter_by(username=username).first()
        if user and check_password_hash(user.password_hash, password):
            login_user(user)
            flash('Successfully logged in!', 'success')
            return redirect(url_for('dashboard'))
        flash('Invalid username or password', 'danger')
    return render_template('login.html')

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        username = request.form.get('username', '').strip()
        email = request.form.get('email', '').strip()
        password = request.form.get('password', '')

        if not username or not email or not password:
            flash('All fields are required', 'warning')
            return redirect(url_for('register'))

        if User.query.filter((User.username == username) | (User.email == email)).first():
            flash('Username or Email already exists', 'warning')
            return redirect(url_for('register'))

        new_user = User(
            username=username,
            email=email,
            password_hash=generate_password_hash(password)
        )
        db.session.add(new_user)
        db.session.commit()
        login_user(new_user)
        flash('Registration successful! Welcome!', 'success')
        return redirect(url_for('dashboard'))
    return render_template('register.html')

@app.route('/logout')
@login_required
def logout():
    logout_user()
    flash('Logged out successfully', 'info')
    return redirect(url_for('index'))

@app.route('/dashboard')
@login_required
def dashboard():
    buildings = Building.query.filter_by(user_id=current_user.id).order_by(Building.updated_at.desc()).all()
    return render_template('dashboard.html', buildings=buildings)

@app.route('/plan/new', methods=['GET', 'POST'])
@login_required
def new_plan():
    if request.method == 'POST':
        name = request.form.get('name', 'Custom Plan').strip() or 'Custom Plan'
        b_type = request.form.get('type', 'Residential')
        try:
            width = float(request.form.get('plotWidth', 18))
            length = float(request.form.get('plotLength', 16))
            floors = int(request.form.get('floorCount', 2))
        except ValueError:
            width, length, floors = 18.0, 16.0, 2

        roof = request.form.get('roofStyle', 'Flat Terrace')
        style = request.form.get('buildingStyle', 'Modern Minimalist')

        sample_plan = get_default_sample_plan()
        sample_plan['name'] = name
        sample_plan['type'] = b_type
        sample_plan['plotWidth'] = width
        sample_plan['plotLength'] = length
        sample_plan['roofStyle'] = roof

        b = Building(
            user_id=current_user.id,
            name=name,
            building_type=b_type,
            plot_width=width,
            plot_length=length,
            total_area=width * length,
            floor_count=floors,
            roof_style=roof,
            building_style=style,
            data_json=json.dumps(sample_plan)
        )
        db.session.add(b)
        db.session.commit()
        flash('Building plan generated successfully!', 'success')
        return redirect(url_for('visualization', building_id=b.id))
    return render_template('plan_input.html')

@app.route('/visualize/<int:building_id>')
@login_required
def visualization(building_id):
    building = Building.query.get_or_404(building_id)
    return render_template('visualization.html', building=building)

# REST APIs for Three.js and AJAX
@app.route('/api/building/<int:building_id>', methods=['GET'])
def get_building_api(building_id):
    building = Building.query.get_or_404(building_id)
    return jsonify(building.to_dict())

@app.route('/api/building/<int:building_id>/update', methods=['POST'])
@login_required
def update_building_api(building_id):
    building = Building.query.get_or_404(building_id)
    if building.user_id != current_user.id:
        return jsonify({'error': 'Unauthorized'}), 403

    payload = request.get_json(silent=True) or {}
    if 'name' in payload:
        building.name = payload['name']
    if 'data' in payload:
        building.data_json = json.dumps(payload['data'])
    db.session.commit()
    return jsonify({'status': 'success', 'message': 'Building saved'})

@app.route('/report/<int:building_id>')
@login_required
def report_page(building_id):
    building = Building.query.get_or_404(building_id)
    return render_template('report.html', building=building)

# Error Handlers to prevent opaque 500 error pages
@app.errorhandler(404)
def not_found_error(error):
    return render_template('404.html'), 404

@app.errorhandler(500)
def internal_server_error(error):
    logger.error(f"Internal 500 Error: {error}", exc_info=True)
    return render_template('500.html', error=str(error)), 500

# Vercel entry point variable
# WSGI servers look for `app`
if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
