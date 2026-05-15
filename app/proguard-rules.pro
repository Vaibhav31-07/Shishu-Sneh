# ProGuard rules for Gemini AI SDK
-keep class com.google.ai.client.generativeai.** { *; }
-keep interface com.google.ai.client.generativeai.** { *; }

# Keep serialization classes if used
-keep class kotlinx.serialization.** { *; }
-keepattributes *Annotation*, EnclosingMethod, Signature
