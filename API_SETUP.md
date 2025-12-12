# 🔑 API Key Setup

## Required API Key

This app requires an OpenRouter API key to function properly.

### 🚀 Quick Setup:

1. **Get your API key** from [OpenRouter.ai](https://openrouter.ai/)

2. **Create API key file:**
   ```batch
   copy api_key.properties.template api_key.properties
   ```

3. **Edit `api_key.properties`** and replace `YOUR_API_KEY_HERE` with your actual API key:
   ```properties
   AGENT_API_BASE=https://openrouter.ai/api/v1/
   AGENT_API_KEY=sk-or-v1-your-actual-key-here
   USE_BRAVE_CHROMIUM=false
   ```

4. **Build the app:**
   ```batch
   ULTIMATE_ONE_CLICK_SETUP.bat
   ```

### 🔒 Security Features:

- ✅ **API keys stored in separate file** (`api_key.properties`)
- ✅ **Automatically ignored by Git** (never committed)
- ✅ **Template provided** for easy setup (`api_key.properties.template`)
- ✅ **Fallback to placeholder** if file missing

### 📁 File Structure:

```
your-project/
├── api_key.properties.template  ← Template (safe to commit)
├── api_key.properties           ← Your keys (NEVER committed)
└── app/build.gradle             ← Reads from api_key.properties
```

### 🛡️ Security Benefits:

- **No API keys in source code**
- **No accidental commits of secrets**
- **Easy to share project safely**
- **Professional security practices**

---

**The app will automatically use your API key from the secure file!**