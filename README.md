# 📱 NutriApp – App de Accesibilidad en Kotlin

Aplicación móvil desarrollada en **Kotlin + Jetpack Compose**, orientada a **personas con discapacidad visual**.  
Incluye accesibilidad con **Text-to-Speech (TTS)**, **alto contraste**, ajuste de **tamaño de texto** y una interfaz simple.

---

## ✨ Funcionalidades principales
- **Login / Registro / Recuperación de clave** con validaciones.
- **Gestión de usuarios** (máximo 5, almacenados en un repositorio en memoria).
- **Gestión de recetas**: crear, buscar y filtrar por nombre, categoría y calorías (máximo 20 recetas).
- **Preferencias de accesibilidad**: TTS, contraste alto, tamaño de texto, tema claro/oscuro/sistema.
- **Interfaz con Material Design 3** (chips, tablas, dropdowns, snackbar, etc.).

---

## 📂 Estructura del proyecto
app/
├── data/ # Repositorios y data classes (UserRepo, RecipeRepo)
├── screens/ # Pantallas principales (Login, Registro, Home, Preferencias, etc.)
├── nav/ # Navegación entre pantallas
├── ui/theme/ # Definición de tema, colores y tipografía
└── MainActivity.kt # Punto de entrada

---

## 🖼️ Capturas (ejemplo)
- Pantalla de **Login**
- Pantalla de **Registro**
- Pantalla de **Preferencias de accesibilidad**
- Pantalla de **Buscar Recetas**
- Pantalla de **Crear Receta**

---

## 🔧 Tecnologías usadas
- **Kotlin**
- **Jetpack Compose**
- **Material Design 3**

---


## 📌 Notas
- El sistema usa **repositorios en memoria** (`UserRepo`, `RecipeRepo`) en lugar de base de datos, tal como lo solicita la pauta.
- Los formularios validan datos (campos obligatorios, duplicados, cupos máximos).
- Se priorizó accesibilidad y usabilidad en el diseño.