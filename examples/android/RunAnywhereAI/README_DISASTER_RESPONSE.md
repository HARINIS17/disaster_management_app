# 🚨 Offline Disaster Response App

## Overview

A comprehensive **100% offline** disaster response application powered by on-device AI using the *
*RunAnywhere SDK**. This app enables emergency responders and civilians to coordinate, communicate,
and make critical decisions during disasters **without internet connectivity**.

---

## ✨ Features

### 🗣 **1. Language Translation**

**Status**: ✅ Fully Implemented

Break down language barriers in disaster zones by translating between 15+ languages **completely
offline**.

**Capabilities:**

- Real-time text translation
- 15 supported languages (English, Spanish, French, German, Italian, Portuguese, Hindi, Bengali,
  Arabic, Chinese, Japanese, Korean, Russian, Turkish, Vietnamese)
- Translation history
- Voice input support (placeholder for Whisper integration)
- 100% on-device processing

**Use Cases:**

- Communicate with non-English speaking victims
- Translate emergency messages
- Coordinate with international response teams

**Example:**

```
Input:  "Where does it hurt?"
From:   English 🇬🇧
To:     Spanish 🇪🇸
Output: "¿Dónde te duele?"
```

---

### 🧾 **2. Situation Summarizer**

**Status**: ✅ Fully Implemented

Convert messy field notes into structured, professional mission reports using AI.

**Report Types:**

1. **SITREP** (Situation Report) - Overall status
2. **Casualty Report** - Injuries and fatalities
3. **Damage Assessment** - Infrastructure damage
4. **Resource Request** - Needed supplies
5. **Incident Report** - Event documentation

**Features:**

- AI-powered report generation
- Real-time streaming
- Professional disaster management terminology
- Copy and share capabilities

**Example:**

```
Field Notes:
"20 injured, 2 buildings collapsed, power out, 500 people affected"

Generated SITREP:
═══════════════════════════════════════
SITUATION REPORT (SITREP)
Date: 2025-01-07 14:30:00
═══════════════════════════════════════

SITUATION:
- 2 buildings collapsed
- Power outage affecting entire area
- 500 people impacted

CASUALTIES:
- 20 injured (severity classification pending)
- Medical triage in progress

ACTIONS TAKEN:
- Emergency response activated
- Medical teams deployed

NEXT STEPS:
- Establish emergency shelter
- Restore power infrastructure
- Complete casualty assessment
```

---

### 📦 **3. Resource Allocator**

**Status**: ✅ Fully Implemented

Predict critical resource needs using AI-powered analysis.

**Inputs:**

- Number of affected people
- Estimated duration (days)
- Location/area type

**Outputs:**

- 6-8 critical resource predictions
- Quantity estimates
- Priority levels (CRITICAL, HIGH, MEDIUM)
- Rationale for each resource

**Resources Predicted:**

- 💧 Water
- 🍲 Food
- 💊 Medical Supplies
- ⛺ Shelter
- 🛏️ Blankets
- 🧼 Hygiene Kits
- 🔦 Tools
- 📡 Communications

**Example:**

```
Input:
- 500 people affected
- 7 days duration
- Rural area, 3 villages

Output:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
💧 Water
Quantity: 7,000 liters
Priority: CRITICAL
Rationale: 2L per person per day for 7 days
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🍲 Food
Quantity: 10,500 meals
Priority: CRITICAL
Rationale: 3 meals per person per day
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

### 📷 **4. Damage Analyzer** (Coming Soon)

**Status**: 🚧 Planned

Analyze building and infrastructure damage from images using on-device vision AI.

**Planned Features:**

- Image capture and analysis
- Damage classification (Minor/Moderate/Severe/Destroyed)
- Structural integrity assessment
- Safety recommendations
- Offline operation

---

### 🗺 **5. Navigation Helper** (Coming Soon)

**Status**: 🚧 Planned

Find safe evacuation routes and navigate disaster zones without internet.

**Planned Features:**

- Route safety assessment
- Obstacle identification
- Alternative path suggestions
- Offline map integration
- Hazard warnings

---

### 🆘 **6. Emergency Assistant** (Coming Soon)

**Status**: 🚧 Planned

Provide first-aid instructions and survival tips offline.

**Planned Features:**

- First-aid step-by-step guides
- CPR instructions
- Burn treatment
- Fracture management
- Emergency contact database
- Survival tips

---

## 🚀 Quick Start

### Prerequisites

1. **Android Device** - Android 7.0+ (API 24+)
2. **RunAnywhere SDK** - Already integrated
3. **AI Model** - Download from Models screen

### Setup Steps

#### 1. Load an AI Model

```
1. Open the app
2. Navigate to "Models" tab
3. Download "Qwen 2.5 0.5B" (recommended) or "SmolLM2 360M" (fastest)
4. Click "Load Model"
5. Wait for loading to complete
```

#### 2. Access Disaster Features

```
1. Click "Disaster" tab in bottom navigation
2. See the Disaster Dashboard with 6 feature cards
3. Click any feature to start using it
```

---

## 📱 User Interface

### Dashboard

Beautiful gradient cards with priority indicators:

- **CRITICAL** features: Translation, Emergency Assistant
- **HIGH** features: Summarizer, Resource Allocator, Navigation
- **MEDIUM** features: Damage Analyzer

### Navigation

- Bottom tab bar: Chat, Models, **Disaster**
- Feature cards with emoji icons
- Offline status indicator (green badge)

---

## 🎯 Real-World Usage

### Scenario 1: Earthquake in Remote Village

**Timeline:**

```
T+0:00  Earthquake hits, communications down
T+0:05  📷 Use Damage Analyzer to assess buildings
T+0:10  🗣 Translate instructions to local language
T+0:15  🧾 Create SITREP from field observations
T+0:20  📦 Predict resource needs (500 people, 7 days)
T+0:30  🗺 Find safe evacuation routes
T+0:45  🆘 Emergency Assistant for first aid
```

**Actions:**

1. **Assess** - Photo damage, classify severity
2. **Communicate** - Translate messages
3. **Report** - Generate official reports
4. **Allocate** - Predict resource needs
5. **Navigate** - Find safe routes
6. **Treat** - Emergency medical guidance

---

## 🔐 Privacy & Security

### 100% On-Device Processing

✅ **No Internet Required**

- All AI runs on your device
- No data sent to cloud
- Works in airplane mode

✅ **Privacy-First**

- No user data collection
- No tracking
- No analytics sent to servers

✅ **Secure**

- Data stays on device
- No external API calls
- Offline operation guaranteed

---

## ⚡ Performance

### Model Recommendations

| Model | Size | Speed | Quality | Use Case |
|-------|------|-------|---------|----------|
| SmolLM2 360M | 119 MB | ⚡⚡⚡ | ⭐⭐ | Quick testing |
| Qwen 2.5 0.5B | 374 MB | ⚡⚡ | ⭐⭐⭐ | **Recommended** |
| Llama 3.2 1B | 815 MB | ⚡ | ⭐⭐⭐⭐ | Best quality |

### Response Times

- **Translation**: < 3 seconds
- **Summarizer**: 5-10 seconds
- **Resource Prediction**: 5-10 seconds

### Battery Consumption

- Moderate during AI processing
- Minimal in standby
- Optimized for emergency scenarios

---

## 🛠️ Technical Architecture

### Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **AI SDK**: RunAnywhere (llama.cpp backend)
- **Architecture**: MVVM
- **Async**: Coroutines + Flow

### Project Structure

```
presentation/
  disaster/
    ├── DisasterDashboardScreen.kt       # Main hub
    ├── translation/
    │   ├── TranslationScreen.kt         # UI
    │   └── TranslationViewModel.kt      # Logic
    ├── summarizer/
    │   ├── SummarizerScreen.kt          # UI
    │   └── SummarizerViewModel.kt       # Logic
    └── resources/
        ├── ResourceScreen.kt             # UI
        └── ResourceViewModel.kt          # Logic
```

---

## 📚 API Usage

### Translation Example

```kotlin
val result = RunAnywhere.generate(
    """
    Translate from English to Spanish:
    "Where does it hurt?"
    
    Translation:
    """.trimIndent()
)
```

### Report Generation Example

```kotlin
RunAnywhere.generateStream(
    """
    Create a SITUATION REPORT (SITREP):
    
    Field Notes: 20 injured, 2 buildings collapsed
    
    Report:
    """.trimIndent()
).collect { token ->
    // Stream tokens in real-time
}
```

### Resource Prediction Example

```kotlin
val prompt = buildResourcePrompt(
    people = "500",
    duration = "7",
    location = "Rural area"
)
val response = RunAnywhere.generate(prompt)
```

---

## 🐛 Troubleshooting

### Issue: "No model loaded"

**Solution**: Go to Models tab → Download a model → Load it

### Issue: Slow generation

**Solution**: Use smaller model (SmolLM2 360M) or wait for processing

### Issue: App crashes

**Solution**: Enable `largeHeap` in AndroidManifest.xml:

```xml
<application
    android:largeHeap="true"
    ...>
```

### Issue: Out of memory

**Solution**:

1. Close other apps
2. Use smaller model
3. Restart app

---

## 🔮 Roadmap

### Phase 1 (Current) ✅

- [x] Dashboard
- [x] Translation
- [x] Summarizer
- [x] Resource Allocator

### Phase 2 (In Progress) 🚧

- [ ] Damage Analyzer
- [ ] Navigation Helper
- [ ] Emergency Assistant

### Phase 3 (Future) 📅

- [ ] Voice integration (Whisper STT)
- [ ] Offline maps
- [ ] Image analysis (TensorFlow Lite)
- [ ] Multi-modal AI
- [ ] Team coordination features

---

## 🤝 Contributing

We welcome contributions! Areas needing help:

1. **Damage Analyzer** - Vision AI integration
2. **Navigation Helper** - Offline maps
3. **Emergency Assistant** - Medical knowledge base
4. **Voice Integration** - Whisper STT
5. **UI/UX** - Improvements and accessibility

---

## 📄 License

Apache License 2.0 - See [LICENSE](../../../LICENSE)

---

## 📞 Support

- **GitHub Issues**: Report bugs
- **Discord**: [Join community](https://discord.gg/pxRkYmWh)
- **Email**: founders@runanywhere.ai

---

## 🙏 Acknowledgments

Built with ❤️ for disaster response teams worldwide.

**Technologies:**

- RunAnywhere SDK
- llama.cpp
- Kotlin Multiplatform
- Jetpack Compose

---

**100% Offline • Privacy-First • AI-Powered**
**Ready for the worst. Built for the best.**

