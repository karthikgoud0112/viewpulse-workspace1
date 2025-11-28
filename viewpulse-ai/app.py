from flask import Flask, request, jsonify
from flask_cors import CORS
from transformers import DistilBertForSequenceClassification, DistilBertTokenizer
import torch
import logging

app = Flask(__name__)
CORS(app)

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Device configuration
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
logger.info(f"Using device: {device}")

# Load YOUR model
logger.info("Loading custom DistilBERT emotion model...")

try:
    # Load model from local folder
    model = DistilBertForSequenceClassification.from_pretrained("./my_model")
    model.to(device)
    model.eval()
    
    # Load tokenizer
    tokenizer = DistilBertTokenizer.from_pretrained('distilbert-base-uncased')
    
    logger.info("Custom model loaded successfully!")
    
except Exception as e:
    logger.error(f"Error loading model: {str(e)}")
    raise

# Your model's emotion classes (in order)
EMOTION_CLASSES = ['sadness', 'joy', 'love', 'anger', 'fear', 'surprise']

# Emotion mapping to ViewPulse categories
EMOTION_MAP = {
    'sadness': 'sadness',
    'joy': 'joy',
    'love': 'love',
    'anger': 'anger',
    'fear': 'fear',
    'surprise': 'surprise'
}

# Emojis for fun
EMOTION_EMOJI = {
    'sadness': '😢',
    'joy': '😊',
    'love': '❤️',
    'anger': '😡',
    'fear': '😨',
    'surprise': '😮'
}

def predict_emotion(text):
    """Predict emotion from text using your DistilBERT model"""
    try:
        # Tokenize input
        input_encoded = tokenizer(text, return_tensors='pt', truncation=True, max_length=512).to(device)
        
        # Get predictions
        with torch.no_grad():
            outputs = model(**input_encoded)
        
        # Get logits and probabilities
        logits = outputs.logits
        probabilities = torch.nn.functional.softmax(logits, dim=1)[0]
        
        # Get predicted class
        pred = torch.argmax(logits, dim=1).item()
        confidence = probabilities[pred].item()
        emotion = EMOTION_CLASSES[pred]
        
        # Get all scores
        all_scores = []
        for idx, score in enumerate(probabilities.tolist()):
            all_scores.append({
                'label': EMOTION_CLASSES[idx],
                'score': score
            })
        
        return emotion, confidence, all_scores
        
    except Exception as e:
        logger.error(f"Prediction error: {str(e)}")
        return "joy", 0.5, []

@app.route('/health', methods=['GET'])
def health():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'service': 'ViewPulse AI Service',
        'model': 'Custom DistilBERT emotion classification',
        'emotions': EMOTION_CLASSES,
        'device': str(device)
    })

@app.route('/analyze', methods=['POST'])
def analyze_emotion():
    """Analyze emotion from text"""
    try:
        data = request.get_json()
        text = data.get('text', '')
        
        if not text or len(text.strip()) == 0:
            return jsonify({
                'success': False,
                'message': 'Text is required'
            }), 400
        
        logger.info(f"Analyzing: {text[:50]}...")
        
        # Get emotion prediction
        emotion, confidence, all_scores = predict_emotion(text)
        
        # Map to ViewPulse emotion categories
        mapped_emotion = EMOTION_MAP.get(emotion.lower(), 'joy')
        emoji = EMOTION_EMOJI.get(emotion.lower(), '😊')
        
        logger.info(f"Detected: {emoji} {mapped_emotion} ({confidence:.2%})")
        
        return jsonify({
            'success': True,
            'emotion': mapped_emotion,
            'confidence': confidence,
            'raw_emotion': emotion,
            'emoji': emoji,
            'all_scores': all_scores
        })
        
    except Exception as e:
        logger.error(f"Error: {str(e)}")
        return jsonify({
            'success': False,
            'message': str(e),
            'emotion': 'joy',
            'confidence': 0.5
        }), 500

if __name__ == '__main__':
    print("\n" + "="*60)
    print("🎭 ViewPulse AI Service Starting...")
    print("📊 Custom DistilBERT Emotion Detection")
    print(f"🎯 Emotions: {', '.join(EMOTION_CLASSES)}")
    print(f"💻 Device: {device}")
    print("🌐 API: http://localhost:5000")
    print("="*60 + "\n")
    
    app.run(host='0.0.0.0', port=5000, debug=True)
