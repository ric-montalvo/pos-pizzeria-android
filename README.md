# Sistema de Punto de Venta (POS) - Android Native

![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Room SQLite](https://img.shields.io/badge/Room_Database-3DDC84?style=for-the-badge&logo=android&logoColor=white)

Una aplicación móvil nativa para Android diseñada para agilizar el proceso de cobro, gestión de turnos y cortes de caja en un negocio de comida rápida. 

## 📖 El Problema: De la libreta al software
Este proyecto nació de la observación directa en un negocio local real. Originalmente, el registro de ventas, el cálculo del cambio, el desglose de productos y el corte de caja (cuadrar el dinero físico con las ventas) se realizaba de forma 100% manual usando **papel, lápiz y una calculadora básica**. 

Esto generaba varios problemas:
* Pérdida de tiempo durante horas pico.
* Errores humanos al sumar los tickets o calcular el cambio.
* Dificultad para saber exactamente cuántos productos de cada tipo se vendieron en el día.
* Descuadres en el fondo de caja al final del turno.

## 💡 La Solución
Desarrollé este **Producto Mínimo Viable (MVP)** que evolucionó rápidamente a una herramienta de uso diario. La app digitaliza todo el flujo de trabajo de la caja registradora, operando completamente *offline* y adaptándose a la velocidad que requiere un negocio de comida.

### ✨ Características Principales
* **Calculadora de Ventas Ágil:** Interfaz de "baja fricción" con botones de acceso rápido para los productos más comunes, calculando totales de forma instantánea.
* **Precios Dinámicos (SharedPreferences):** Menú de ajustes que permite actualizar los precios de los productos en tiempo real, guardando la configuración en el dispositivo sin necesidad de migraciones de bases de datos.
* **Gestor de Turnos:** Permite registrar un fondo de caja inicial al abrir la app.
* **Control de Salidas:** Registro de gastos operativos (gas, insumos) tomados directamente del cajón, restándolos automáticamente del flujo esperado.
* **Corte de Caja Detallado:** Generación de un reporte completo al finalizar el turno que incluye el efectivo esperado, desglose de artículos más vendidos (agrupados) y detalle de folios.
* **Compartir Reportes:** Integración con `Intent.ACTION_SEND` para enviar el corte de caja generado directamente por WhatsApp a los administradores.
* **Prevención de Suspensión (Wake Lock):** Mantiene la pantalla encendida automáticamente mientras la app está en primer plano para evitar bloqueos durante la operación.

## 🛠️ Stack Tecnológico
* **Lenguaje:** Kotlin
* **UI:** Jetpack Compose (Material Design 3 con paleta personalizada Dark Mode)
* **Arquitectura:** MVVM (Model-View-ViewModel)
* **Persistencia de Datos:** 
  * **Room (SQLite):** Para el historial inmutable de tickets, ventas y turnos.
  * **SharedPreferences:** Para la gestión ligera de configuraciones y precios dinámicos.

## ⚠️ Nota de Uso
**Declaración de uso personal:** Este software fue diseñado y programado con especificaciones hechas a la medida para resolver las necesidades operativas de un negocio local específico. Aunque el código es de código abierto para propósitos de demostración y portafolio, está fuertemente acoplado a la lógica de negocio de dicho establecimiento.

---
*Desarrollado por **Ricardo Montalvo** (RP Studios).*
