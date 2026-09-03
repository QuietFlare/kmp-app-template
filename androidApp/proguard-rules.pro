# R8 rules for the release build.
#
# kotlinx.serialization is the part that matters: generated `$$serializer` classes and
# `Companion` objects are reached by reflection, so R8 may strip or rename them, and decode
# then returns null (the app looks like a fresh install). The library ships consumer rules;
# these cover the app's own @Serializable model explicitly. After any change, round-trip a
# real store file through a release build.

-keepattributes *Annotation*, InnerClasses

# Keep every generated serializer for the shared core's persisted model.
-if @kotlinx.serialization.Serializable class app.starter.core.**
-keepclassmembers class app.starter.core.** {
    *** Companion;
}
-keepclasseswithmembers class app.starter.core.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class app.starter.core.**$$serializer { *; }

# Compose and AndroidX ship their own consumer rules; nothing to add.
