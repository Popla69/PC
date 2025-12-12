# AI-First Mobile Browser

An Android mobile browser application modeled after Perplexity Comet, where the AI conversation interface is the primary user experience.

## Overview

This is an AI-first browser that launches directly into an AI conversation screen. Users can:
- Ask questions and get streaming AI responses with citations
- Click citations to browse sources in full-screen
- Enter URLs directly to browse any website
- Seamlessly switch between AI conversation and web browsing

## Architecture

### Dual-Mode System
- **Conversation Mode** (default): AI chat interface with streaming responses
- **Browser Mode**: Full-screen web browsing with Brave Chromium integration

### Key Components
- `ModeManager`: Handles transitions between conversation and browser modes
- `ConversationManager`: Manages chat history and message state
- `URLDetector`: Distinguishes between queries and URLs
- `QueryProcessor`: Processes AI queries with streaming support
- `BrowserViewAdapter`: Abstraction layer for WebView/Brave Chromium
- `ConversationAdapter`: RecyclerView adapter for chat messages

## Project Structure

```
app/src/main/java/com/popla/perplexitydemo/
├── MainActivityNew.kt              # Main activity with dual-mode UI
├── adapter/
│   └── ConversationAdapter.kt      # RecyclerView adapter for messages
├── manager/
│   ├── ModeManager.kt              # Mode transition management
│   └── ConversationManager.kt      # Conversation history management
├── models/
│   ├── Message.kt                  # Message data models
│   ├── AppMode.kt                  # App mode enum
│   ├── ConversationState.kt        # Conversation state
│   └── ConversationSession.kt      # Session data
├── util/
│   └── URLDetector.kt              # URL detection and validation
├── browser/
│   ├── BrowserViewAdapter.kt       # Browser abstraction interface
│   └── StandardWebViewAdapter.kt   # WebView implementation
├── network/
│   └── ApiClient.kt                # API client configuration
├── processor/
│   └── QueryProcessor.kt           # Query processing logic
├── stream/
│   └── StreamHandler.kt            # Streaming response handler
└── citation/
    └── CitationParser.kt           # Citation parsing

app/src/main/res/layout/
├── activity_main.xml               # Main layout with mode containers
├── layout_conversation_mode.xml    # Conversation interface layout
├── layout_browser_mode.xml         # Browser interface layout
├── item_user_message.xml           # User message bubble
├── item_ai_message.xml             # AI message bubble
└── item_error_message.xml          # Error message display
```

## Features

✅ AI-first home screen with conversation interface
✅ Real-time streaming AI responses
✅ Citation parsing and clickable links
✅ URL detection in user input
✅ Seamless mode transitions
✅ Full-screen web browsing
✅ Conversation history persistence
✅ Dark theme UI
✅ Property-based testing
✅ Brave Chromium integration ready

## Configuration

API configuration is in `app/build.gradle`:
```gradle
buildConfigField "String", "AGENT_API_BASE", "\"https://openrouter.ai/api/v1/\""
buildConfigField "String", "AGENT_API_KEY", "\"your-api-key-here\""
```

## Building

```bash
./gradlew assembleDebug
```

## Testing

Property-based tests using Kotest:
```bash
./gradlew test
```

## Brave Integration

The app uses `BrowserViewAdapter` interface for easy integration with Brave's Chromium engine:
1. Implement `BraveChromiumAdapter` extending `BrowserViewAdapter`
2. Update `MainActivityNew` to use `BraveChromiumAdapter` instead of `StandardWebViewAdapter`
3. Set `USE_BRAVE_CHROMIUM` build flag to true

## Design Principles

- AI conversation is the primary interface, not a sidebar
- Seamless transitions between AI and browsing
- State preservation across mode changes
- Clean, minimal dark theme
- Property-based testing for correctness

## Spec Documentation

Full specification available in `.kiro/specs/agentic-mobile-browser/`:
- `requirements.md` - 12 requirements with 60+ acceptance criteria
- `design.md` - Complete architecture with 37 correctness properties
- `tasks.md` - 28 implementation tasks (all completed)
