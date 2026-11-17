# Paolicias.Sweet

App móvil Android de **banquetería**: el cliente arma su evento con productos del catálogo, reserva fecha y horario, y bloquea la fecha pagando un **abono del 50%** que la administradora valida manualmente con el comprobante de transferencia.

MVP desarrollado para el ramo **DSY1105 – Desarrollo de Aplicaciones Móviles** (Duoc UC). Todos los datos de la app (usuarios, correos, reservas, comprobantes) son **ficticios**.

---

## Contenido

1. [Funcionalidades](#funcionalidades)
2. [Tecnologías](#tecnologías)
3. [Arquitectura](#arquitectura)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Cómo ejecutar](#cómo-ejecutar)
6. [Flujo del abono y estados](#flujo-del-abono-y-estados)
7. [Reglas de negocio supuestas](#reglas-de-negocio-supuestas)
8. [Validación de formularios](#validación-de-formularios)
9. [Persistencia local (Room)](#persistencia-local-room)
10. [Recursos nativos](#recursos-nativos)
11. [Animaciones](#animaciones)
12. [Limitaciones conocidas](#limitaciones-conocidas)
13. [Pendiente](#pendiente)

---

## Funcionalidades

### Cliente

- **Registro e inicio de sesión** con validación por campo (borde rojo, mensaje e ícono de error).
- **Catálogo** de productos cargado desde Room, con buscador y filtro por categoría.
- **Detalle de producto** con botón *Agregar a mi evento* / *Quitar de mi evento*.
- **Reserva del evento**: nombre, fecha (selector de calendario, solo desde hoy en adelante), horario (selector de hora), cantidad de invitados (1 a 100) y observaciones. Vibra al enviar.
- **Mi Agenda**: lista de sus reservas ordenadas por fecha real, con precio del evento, abono del 50% y estado del abono.
- **Adjuntar comprobante** de transferencia desde la galería (sin pedir permisos).
- **Perfil** con sus datos y cierre de sesión.

### Administradora

- **Panel de reservas** ordenadas cronológicamente, con filtros *Todas / Pendiente / En revisión / Pagado 50%*.
- **Revisión del comprobante** (miniatura de la imagen y abono a validar).
- **Validar abono** (el estado pasa a *Pagado 50%* y se guarda la fecha de pago) o **Rechazar** (vuelve a *Pendiente* y el cliente debe adjuntar otro comprobante).

La administradora es la cuenta con el correo `admin@paolicias.cl`.

---

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin 2.2.10 |
| Interfaz | Jetpack Compose + Material 3 (BOM 2026.02.01) |
| Navegación | Navigation Compose 2.9.8 |
| Arquitectura | MVVM + Repository |
| Persistencia | Room 2.8.4 con KSP 2.3.10 |
| Build | Android Gradle Plugin 9.4.1 |
| Android | minSdk 26, compileSdk y targetSdk 37 |

---

## Arquitectura

La app usa **MVVM** con una capa **Repository** entre el ViewModel y la base de datos:

```
Screen (Composable)  →  ViewModel  →  Repository  →  DAO  →  Room (SQLite)
   dibuja la UI         estado de      de dónde       consultas
                        la pantalla    salen los datos SQL
```

| Capa | Responsabilidad | Ejemplo |
|---|---|---|
| **Screen / Composable** | Dibuja la interfaz y avisa los eventos del usuario. No toca la base de datos. | `ReservaScreen`, `AgendaScreen` |
| **Model** | Datos que describen el estado de una pantalla (incluye un mensaje de error por campo). | `ReservaModel`, `RegisterModel` |
| **ViewModel** | Guarda el estado de la pantalla (`mutableStateOf`), valida y decide cuándo pedir datos. | `ReservaViewmodel`, `AdminViewModel` |
| **Repository** | Único que conoce el DAO. Los ViewModel no saben de dónde vienen los datos. | `ReservaRepository` |
| **DAO / Entity** | Consultas SQL y tablas de Room. | `ReservaDao`, `ReservaEntity` |

**Tres Repository, uno por tabla:**

| Repository | Tabla | Lo usan |
|---|---|---|
| `ReservaRepository` | `reservas` | Agenda, Panel admin, Reserva |
| `ProductoRepository` | `productos` | Catálogo, Detalle, Reserva |
| `UserRepository` | `users` | Login, Registro, Perfil |

Objetos en memoria (no son tablas):

- `Sesion`: correo del usuario con sesión iniciada y si es administradora. Se pierde al cerrar la app.
- `SeleccionEvento`: ids de los productos que el cliente va eligiendo para su evento.

---

## Estructura del proyecto

```
com.ban.paoliciassweet
├── MainActivity.kt
├── data
│   ├── local
│   │   ├── dao         ProductoDao, ReservaDao, UserDao
│   │   ├── database    AppDatabase
│   │   ├── entity      ProductoEntity, ReservaEntity, UserEntity
│   │   └── seed        ProductosIniciales (11 productos de ejemplo)
│   └── repository      ReservaRepository, ProductoRepository, UserRepository
├── model               un modelo por pantalla + Sesion + SeleccionEvento
└── ui
    ├── components      BarraInferior, CampoValidado, Header, Footer
    ├── navigation      AppNavigation (rutas y NavHost)
    ├── screen          una pantalla por archivo
    ├── theme           colores, tipografía y tema
    ├── util            Validaciones, LineaCotizacion, Formato
    └── viewmodel       un ViewModel por pantalla
```

### Pantallas y rutas

| Ruta | Pantalla | Quién |
|---|---|---|
| `INICIO` | Bienvenida | Todos |
| `Login` / `register` | Inicio de sesión / Registro | Todos |
| `Home` | Catálogo | Cliente |
| `producto/{id}` | Detalle de producto | Cliente |
| `Reserva` | Formulario de reserva | Cliente |
| `Agenda` | Mi Agenda | Cliente |
| `Perfil` | Perfil y cierre de sesión | Cliente |
| `Admin` | Panel de administradora | Administradora |

---

## Cómo ejecutar

1. Abrir el proyecto con **Android Studio** y esperar la sincronización de Gradle.
2. Ejecutar en un emulador o dispositivo con **Android 8.0 (API 26) o superior**.
3. La primera vez que se abre el catálogo, la tabla de productos está vacía y se cargan automáticamente los 11 productos de ejemplo.
4. Para probar como **cliente**: registrar un usuario con datos ficticios e iniciar sesión.
5. Para probar como **administradora**: registrar el correo `admin@paolicias.cl` e iniciar sesión con él (el sistema la reconoce por ese correo).

> **Si cambia una entidad de Room**, hay que subir la versión de `AppDatabase` o desinstalar la app del emulador. La base usa `fallbackToDestructiveMigration`, que **borra y recrea** todas las tablas al subir de versión (solo para desarrollo). Después hay que volver a registrar a la administradora.

---

## Flujo del abono y estados

El pago no usa pasarela: el cliente transfiere y adjunta el comprobante, y la administradora lo valida a mano. El estado del abono vive **solo** en `ReservaEntity.estadoAbono`.

```
                 el cliente adjunta
   Pendiente  ───────────────────────►  En revisión
       ▲                                     │
       │          la administradora          │
       └────────────  rechaza  ◄─────────────┤
                                             │ la administradora valida
                                             ▼
                                        Pagado 50%
```

| Estado | Qué significa | Qué puede hacer el cliente |
|---|---|---|
| `Pendiente` | Reserva creada, sin comprobante (o comprobante rechazado). | Adjuntar comprobante. |
| `En revisión` | Hay un comprobante esperando validación. | Cambiar el comprobante. |
| `Pagado 50%` | Abono validado; la fecha queda bloqueada. | El botón de comprobante queda deshabilitado. |

El caso del ramo también nombra el estado *Completado*; **todavía no está implementado** (ver [Pendiente](#pendiente)).

Al **rechazar**, se borra la ruta del comprobante y la fecha de pago, para que el cliente deba adjuntar uno nuevo.

---

## Reglas de negocio supuestas

Estas reglas no vienen del enunciado: son decisiones del equipo y están documentadas aquí.

### Cotización

El total de una reserva se calcula al enviarla y se guarda en `ReservaEntity.total`:

```
total = tarifa del servicio (según invitados) + Σ por cada producto: ceil(invitados / porciones) × precio
```

**Tarifa del servicio**, escalonada de 10 en 10 invitados (tope 100):

| Invitados | Tarifa | Invitados | Tarifa |
|---|---|---|---|
| 1 – 10 | $30.000 | 51 – 60 | $140.000 |
| 11 – 20 | $50.000 | 61 – 70 | $160.000 |
| 21 – 30 | $80.000 | 71 – 80 | $180.000 |
| 31 – 40 | $100.000 | 81 – 90 | $200.000 |
| 41 – 50 | $120.000 | 91 – 100 | $220.000 |

Cada producto trae `porciones` = a cuántas personas rinde **una unidad**. Se compran las unidades necesarias para cubrir a todos los invitados (se redondea hacia arriba).

**Ejemplo:** 25 invitados con una *Torta Frutos Rojos* (12 porciones, $45.000):

- Tarifa: 21–30 invitados → $80.000
- Unidades: ceil(25 / 12) = 3 → 3 × $45.000 = $135.000
- **Total: $215.000** → abono del 50%: **$107.500**

### Abono

- El abono es el **50% del total** y es obligatorio para bloquear la fecha.
- Se calcula como `total / 2` (división entera).

### Otras reglas

- **Correo:** al registrar y al iniciar sesión se normaliza con `trim().lowercase()`.
- **Administradora:** se identifica por el correo fijo `admin@paolicias.cl`.
- **Fechas:** se guardan como texto `dd/MM/yyyy`. El orden cronológico se logra reordenando el texto como `yyyyMMdd` con `substr` en la consulta SQL.
- **Selector de fecha:** solo permite desde hoy en adelante.
- **Estimación de personal e insumos** (panel admin): regla por definir y documentar aquí cuando se implemente.

---

## Validación de formularios

Las funciones viven en `ui/util/Validaciones.kt`: cada una devuelve el mensaje de error o `null` si el dato es válido. Los formularios usan el componente `CampoValidado` (campo con borde rojo, mensaje debajo e ícono de error).

| Formulario | Campo | Regla |
|---|---|---|
| Registro | Nombre | Mínimo 3 caracteres; solo letras y espacios |
| Registro | Correo | Formato válido (`Patterns.EMAIL_ADDRESS`); no puede estar ya registrado |
| Registro | Contraseña | Mínimo 6 caracteres |
| Registro | Repetir contraseña | Debe coincidir |
| Login | Correo | Formato válido |
| Login | Contraseña | No vacía |
| Reserva | Nombre del evento | Mínimo 3 caracteres (acepta números) |
| Reserva | Fecha | Obligatoria |
| Reserva | Horario | Obligatorio |
| Reserva | Invitados | Solo dígitos, entre 1 y 100 |

**Cómo funciona** (igual en las tres pantallas):

1. Cada campo tiene su propio mensaje de error en el modelo de la pantalla (`nombreEventoError`, `emailError`, etc.).
2. Al tocar el botón se validan **todos los campos a la vez**, para mostrar todos los errores juntos.
3. Al editar un campo, se borra **solo el error de ese campo**.

---

## Persistencia local (Room)

Base de datos `AppDatabase`, **versión 6**, con tres tablas:

| Tabla | Entidad | Contenido |
|---|---|---|
| `users` | `UserEntity` | id, name, email, password |
| `productos` | `ProductoEntity` | id, nombre, descripcion, categoria, precio, porciones, imagen |
| `reservas` | `ReservaEntity` | id, nombreEvento, fecha, horario, invitados, observaciones, correoCliente, estadoAbono, productos, total, comprobante, fechaPago |

- `ReservaEntity.productos` guarda los ids de los productos separados por coma.
- `ReservaEntity.comprobante` guarda la **ruta** de la imagen dentro de `filesDir`, no la imagen.
- `ProductoEntity.imagen` guarda el nombre del drawable; si no existe, se usa una imagen por defecto.
- El catálogo se lee como `Flow`, así que la lista se actualiza sola cuando cambia la tabla.

---

## Recursos nativos

| Recurso | Uso | Permiso |
|---|---|---|
| **Vibración** | Vibra al enviar la reserva (`Vibracion.kt`). | `VIBRATE`, declarado en el manifest (es un permiso normal: se concede al instalar). |
| **Galería** | El cliente elige la imagen del comprobante con el selector de fotos del sistema (`PickVisualMedia`). | Ninguno: el selector del sistema entrega solo la imagen elegida. |
| **Almacenamiento interno** | La imagen elegida se copia a `filesDir` y se guarda su ruta en la reserva. | Ninguno: es almacenamiento privado de la app. |

La miniatura del comprobante se carga con `inSampleSize = 4` para no gastar memoria y se muestra con recorte (`ContentScale.Crop`) y alto fijo.

---

## Animaciones

En las tarjetas de **Mi Agenda**:

- **Color del estado** (`animateColorAsState`): el texto *Estado del abono* cambia de color de forma gradual (600 ms) según el estado: burdeos (Pendiente), ámbar (En revisión) y verde (Pagado 50%).
- **Aparición del comprobante** (`AnimatedVisibility`): la imagen entra con fundido y expansión al adjuntarla.

Sirven como retroalimentación visual: el cliente ve que su acción tuvo efecto. El color anima porque la lista usa `key = { it.id }`, así cada tarjeta conserva su identidad al recargar los datos.

---

## Limitaciones conocidas

Es un MVP académico. Estas limitaciones están identificadas y no se corrigieron:

- **Contraseñas sin cifrar:** se guardan y comparan en texto plano. Aceptable solo porque los datos son ficticios.
- **Administradora por correo:** cualquiera que registre `admin@paolicias.cl` queda como administradora.
- **Sesión en memoria:** `Sesion` no persiste; al cerrar la app hay que volver a iniciar sesión.
- **Cotización sin desglose para el cliente:** el total se calcula al enviar y se ve en Mi Agenda, pero el formulario no muestra el desglose (tarifa + productos) antes de enviar.
- **Doble envío:** después de guardar una reserva el formulario no se limpia, y un segundo toque crea una reserva duplicada.
- **Archivos huérfanos:** al rechazar o cambiar un comprobante, la imagen anterior queda en `filesDir`.
- **Imágenes de productos:** solo algunos productos tienen su imagen en `drawable`; el resto usa la imagen por defecto.
- **Nombres de archivo inconsistentes:** por ejemplo, `UsuarioDao.kt` contiene `UserDao` y `AgendaViewModel.kt` contiene `AgendaViewmodel`.
- **Vibración siempre:** vibra al tocar *Enviar reserva* incluso si el formulario tiene errores.

---

## Pendiente

- [ ] Teléfono y empresa en el registro y el perfil (con su validación).
- [ ] Panel admin: detalle de la reserva y estimación de personal e insumos.
- [ ] Decidir si se agrega *Marcar completado* para el estado Completado.
- [ ] Evaluar cámara y notificaciones como recursos nativos adicionales.
- [ ] Imágenes que faltan en `drawable` y unificar nombres de archivos.
- [ ] Limpiar comprobantes huérfanos y corregir el doble envío.
- [ ] Evaluación final: API REST con **Retrofit + Spring Boot**, notificaciones y **APK firmado** en release.

---

## Equipo y seguimiento

- **Equipo:** _(completar nombres)_
- **Tablero Trello:** _(completar enlace)_
- **Convención de commits:** un commit pequeño por cambio, con prefijo `feat:`, `fix:` o `refactor:`.
