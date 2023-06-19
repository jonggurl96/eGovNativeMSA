from flask import Flask, jsonify

app = Flask(__name__)

@app.route("/")
def home():
    return "Hello, World!"

@app.route("/health.json")
def health():
    return jsonify({"status": "UP"}), 200

app.run(debug=True)