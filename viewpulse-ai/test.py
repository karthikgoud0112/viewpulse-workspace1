import requests

API_URL = "http://localhost:5000/analyze"

test_cases = [
    "I love this product! It's amazing!",
    "This is terrible. I'm very disappointed.",
    "I'm scared something bad will happen.",
    "Wow! I didn't expect that!",
    "The service was okay, nothing special.""I love this product! It's amazing!",
"This made me so happy!",
"I love this!"


]

print("\n" + "="*60)
print("Testing ViewPulse AI Service")
print("="*60 + "\n")

for text in test_cases:
    print(f"Text: {text}")
    
    response = requests.post(API_URL, json={"text": text})
    
    if response.status_code == 200:
        result = response.json()
        if result['success']:
            print(f"✅ Emotion: {result['emotion']} (Confidence: {result['confidence']:.2%})")
        else:
            print(f"❌ Error: {result['message']}")
    else:
        print(f"❌ HTTP Error: {response.status_code}")
    
    print("-" * 60)
