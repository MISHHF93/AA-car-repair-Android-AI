package com.aa.carrepair.domain.model

enum class UserPersona {
    DIY_OWNER,
    PROFESSIONAL_TECHNICIAN,
    FLEET_MANAGER;

    val displayName: String get() = when (this) {
        DIY_OWNER -> "DIY Vehicle Owner"
        PROFESSIONAL_TECHNICIAN -> "Professional Technician"
        FLEET_MANAGER -> "Fleet / Service Manager"
    }
}
