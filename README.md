# 📈 Terminal Charts Pro

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.10-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-1.5.0-brightgreen)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/API-24%2B-orange)](https://android-arsenal.com/api?level=24)

Приложение для анализа биржевых графиков с продвинутым управлением и обработкой ошибок.

## 🌟 Демонстрация

<div align="center">
  <img src="screenshots/main_screen.png" width="50%" alt="Основной экран">
  <img src="screenshots/main_zoomed_screen.png" width="50%" alt="Увеличенный график">
</div>

## 🚀 Ключевые особенности

### 📊 Визуализация данных
- Интерактивные свечные графики (OHLC)
- Поддержка 5+ таймфреймов
- Масштабирование жестами (pinch-to-zoom)
- Автомасштабирование по данным

### 🛡 Обработка ошибок
| Ошибка | Скриншот | Описание |
|--------|----------|----------|
| **Сеть** | <img src="screenshots/error_network_screenshot.png" width="150"> | Автоповтор при восстановлении связи |
| **Сервер** | <img src="screenshots/screen_server_error.png" width="150"> | Уведомление о проблемах API |

## ⚙️ Технический стек

### Ядро приложения
- **Kotlin** - Основной язык с корутинами и Flow
- **Jetpack Compose** - Современный UI-фреймворк
- **Material Design 3** - Система компонентов

### Архитектура
- **Clean Architecture** - Разделение на слои
- **MVVM** - Паттерн управления состоянием
- **Dagger Hilt** - Внедрение зависимостей

### Работа с данными
- **Retrofit + Gson** - Сетевые запросы
- **OkHttp** - HTTP-клиент с логированием
- **StateFlow** - Реактивное состояние UI

### Дополнительно
- **Custom Rate Limiter** - Ограничение 5 запросов/мин
- **Logging Interceptor** - Отладка API-запросов
