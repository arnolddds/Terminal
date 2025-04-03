# Terminal App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.10-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.0-brightgreen)](https://developer.android.com/jetpack/compose)

Приложение для отображения биржевых графиков с поддержкой различных временных интервалов и обработкой ограничений API.

<p align="center">
  <img src="screenshots/main_screen.png" width="30%">
  <img src="screenshots/error_state.png" width="30%">
</p>

## Особенности

- 📊 Отображение свечных графиков акций
- ⏳ Поддержка различных временных интервалов
- 🌐 Обработка сетевых ошибок и ограничений API
- 🔄 Автоматическое обновление данных
- 🎨 Полностью реализовано на Jetpack Compose
- 🏗 Чистая архитектура (Clean Architecture) с UseCase и Repository

## Технологии

- **Язык**: Kotlin
- **UI**: Jetpack Compose
- **Архитектура**: MVVM
- **DI**: Hilt
- **Сеть**: Retrofit
- **Состояние**: Flow, StateFlow
