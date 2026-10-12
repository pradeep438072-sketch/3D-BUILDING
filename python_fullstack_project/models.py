from flask_sqlalchemy import SQLAlchemy
from flask_login import UserMixin
from datetime import datetime
import json

db = SQLAlchemy()

class User(UserMixin, db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password_hash = db.Column(db.String(128), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    buildings = db.relationship('Building', backref='owner', lazy=True, cascade="all, delete-orphan")

class Building(db.Model):
    __tablename__ = 'buildings'
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    name = db.Column(db.String(120), nullable=False)
    building_type = db.Column(db.String(50), default="Residential") # Residential, Commercial, Apartment
    plot_width = db.Column(db.Float, default=18.0)
    plot_length = db.Column(db.Float, default=16.0)
    total_area = db.Column(db.Float, default=288.0)
    floor_count = db.Column(db.Integer, default=2)
    floor_height = db.Column(db.Float, default=3.2)
    roof_style = db.Column(db.String(50), default="Flat Terrace")
    building_style = db.Column(db.String(50), default="Modern Minimalist")
    data_json = db.Column(db.Text, nullable=False) # Full JSON layout of floors & rooms & colors
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    def to_dict(self):
        try:
            details = json.loads(self.data_json)
        except Exception:
            details = {}
        return {
            'id': self.id,
            'name': self.name,
            'building_type': self.building_type,
            'plot_width': self.plot_width,
            'plot_length': self.plot_length,
            'total_area': self.total_area,
            'floor_count': self.floor_count,
            'floor_height': self.floor_height,
            'roof_style': self.roof_style,
            'building_style': self.building_style,
            'details': details,
            'updated_at': self.updated_at.strftime('%Y-%m-%d %H:%M:%S')
        }
