# ==============================================================================
# ATH Móvil SDK - ProGuard Rules
# ==============================================================================

# 1. Preservar la API pública del SDK
# Esto evita que las clases que los clientes usan desaparezcan o cambien de nombre.
-keep public class com.evertecinc.athmovil.sdk.checkout.** {
    public protected *;
}

# 2. Modelos de Datos (CRÍTICO para GSON)
# Mantener clases, campos y constructores para evitar que la serialización falle.
-keep class com.evertecinc.athmovil.sdk.checkout.objects.** { *; }
-keep class com.evertecinc.athmovil.sdk.models.** { *; }

-keepclassmembers class com.evertecinc.athmovil.sdk.checkout.objects.** {
    public <init>(...);
    @com.google.gson.annotations.SerializedName <fields>;
}

# 3. Reglas Generales de Reflexión y Metadata
# Necesario para GSON, Retrofit y la interoperabilidad con Java/Kotlin.
-keepattributes Signature, EnclosingMethod, InnerClasses, *Annotation*, Exceptions
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# 4. Soporte para GSON (Dependencia interna)
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-dontwarn com.google.gson.**

# 5. Soporte para Retrofit / OkHttp / Okio
# Evita advertencias de clases opcionales no presentes en tiempo de compilación.
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

-keep interface retrofit2.** { *; }
-keepclassmembers interface * {
    @retrofit2.http.* <methods>;
}

# 6. Silenciar advertencias específicas del SDK (opcional si hay dependencias opcionales)
-dontwarn com.evertecinc.athmovil.sdk.**