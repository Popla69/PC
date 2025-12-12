# 🔑 API Key Setup

## Required API Key

This app requires an OpenRouter API key to function properly.

### 🚀 Quick Setup:

1. **Get your API key** from [OpenRouter.ai](https://openrouter.ai/)
2. **Replace placeholder** in `app/build.gradle`:
   ```gradle
   buildConfigField "String", "AGENT_API_KEY", "\"YOUR_API_KEY_HERE\""
   ```
   Change `YOUR_API_KEY_HERE` to your actual API key.

3. **Rebuild the app** using:
   ```batch
   ULTIMATE_ONE_CLICK_SETUP.bat
   ```

### 🔒 Security Note:

- **Never commit your real API key** to version control
- The placeholder `YOUR_API_KEY_HERE` is safe for public repositories
- Your actual API key should only be in your local copy

### 💡 Alternative Setup:

You can also set the API key via environment variables or local.properties file for better security.

---

**The app will not work without a valid API key!**