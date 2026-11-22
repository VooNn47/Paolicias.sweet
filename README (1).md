# Paolicias.Sweet

App móvil Android de **banquetería**: el cliente arma su evento con productos del catálogo, reserva fecha y horario, y bloquea la fecha pagando un **abono del 50%** que la administradora valida con el comprobante de transferencia.

Proyecto del ramo **DSY1105 – Desarrollo de Aplicaciones Móviles** (Duoc UC), Experiencia 3. Todos los datos (usuarios, correos, reservas, comprobantes) son **ficticios**.

- Backend (microservicios Spring Boot): repositorio `Paolicias.sweet-backend`.
- Diagramas de arquitectura: carpeta [`documents/`](documents/arquitectura.md) (rama `documents`).

---

## Contenido

1. [Funcionalidades](#funcionalidades)
2. [Tecnologías](#tecnologías)
3. [Arquitectura](#arquitectura)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Cómo ejecutar](#cómo-ejecutar)
6. [APK firmado](#apk-firmado)
7. [Flujo del abono y estados](#flujo-del-abono-y-estados)
8. [Reglas de negocio](#reglas-de-negocio)
9. [Validación de formularios](#validación-de-formularios)
10. [Persistencia local (SQLite / Room)](#persistencia-local-sqlite--room)
11. [Recursos nativos](#recursos-nativos)
12. [APIs externas](#apis-externas)
13. [Animaciones](#animaciones)
14. [Pruebas](#pruebas)
15. [Limitaciones conocidas](#limitaciones-conocidas)
16. [Cumplimiento de la rúbrica](#cumplimiento-de-la-rúbrica-experiencia-3)

---

## Funcionalidades

### Cliente

- **Registro e inicio de sesión** en SQLite, con validación por campo (borde rojo, mensaje e ícono de error).
- **Catálogo** desde `catalogo-service`, con buscador y filtro por categoría. Sin conexión muestra la copia guardada en Room.
- **Detalle de producto** con *Agregar a mi evento* / *Quitar de mi evento*.
- **Reserva del evento**: nombre, fecha (solo desde hoy), horario, invitados (1 a 100) y observaciones. Muestra la **cotización** (tarifa + productos + total + abono) calculada por `cotizacion-service` antes de enviar. Vibra cuando el servidor confirma.
- **Mi Agenda**: sus reservas desde `reservas-service`, ordenadas por fecha, con precio, abono y estado. El precio también se muestra en dólares (API externa ExchangeRate-API).
- **Aviso de feriado**: al elegir la fecha, la app avisa si es feriado en Chile (API externa Nager.Date).
- **Comprobante** de transferencia con la **cámara** o desde la **galería**; se sube al backend.
- **Eliminar reserva** desde Mi Agenda, con confirmación (`DELETE /api/reservas/{id}`; el backend borra también el comprobante).
- **Perfil** con sus datos y cierre de sesión.

### Administradora

- **Panel de reservas** de todos los clientes, ordenadas por fecha, con filtros *Todas / Pendiente / En revisión / Pagado 50% / Completado*.
- **Revisión del comprobante** (imagen descargada del backend) y abono a validar.
- **Calcular insumos** de cada reserva antes de aceptarla: muestra cuánto se necesita de cada insumo, cuánto hay y qué falta.
- **Validar abono**, **Rechazar** o **Marcar como completada**. Al validar se descuentan los insumos del inventario; si falta algo, no deja validar y avisa qué falta.
- **Inventario de insumos**: pantalla con la lista y el stock de cada insumo, con opción de ajustarlo (por ejemplo, después de comprar).
- **Feriados marcados**: las reservas que caen en feriado aparecen con un aviso, para planificar el personal.
- **Notificación** cuando hay comprobantes esperando validación.
- **Contactar al cliente** por correo.

La administradora es la cuenta registrada con el correo `admin@paolicias.cl` (el rol queda guardado en SQLite).

---

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin 2.2.10 |
| Interfaz | Jetpack Compose + Material 3 (BOM 2026.02.01) |
| Navegación | Navigation Compose 2.9.8 |
| Arquitectura | MVVM + Repository |
| Persistencia local | Room 2.8.4 (SQLite) con KSP |
| Red | Retrofit 2.11 + Gson + OkHttp |
| Backend | Spring Boot 4 + JPA + PostgreSQL, en Docker sobre AWS EC2 |
| Build | Android Gradle Plugin 9.4.1 |
| Android | minSdk 26, compileSdk y targetSdk 37 |

---

## Arquitectura

```
Screen (Composable) → ViewModel → Repository → Room (SQLite)       login, registro, copia del catálogo
                                             → Retrofit → Backend   catálogo, cotización, reservas e insumos
```

| Capa | Responsabilidad | Ejemplo |
|---|---|---|
| **Screen** | Dibuja la interfaz y avisa los eventos. No toca datos. | `ReservaScreen`, `AgendaScreen` |
| **ViewModel** | Estado de la pantalla, validación, carga y errores. | `ReservaViewModel`, `AdminViewModel` |
| **Repository** | Único que sabe de dónde vienen los datos (Room o Retrofit). | `ReservaRepository` |
| **DAO / Api** | Consultas SQL (Room) y endpoints HTTP (Retrofit). | `UserDao`, `ReservaApi` |

| Repository | Fuente | Lo usan |
|---|---|---|
| `UserRepository` | SQLite (`users`) | Login, Registro, Perfil |
| `ProductoRepository` | `catalogo-service` + copia en SQLite (`productos`) | Catálogo, Detalle, Reserva |
| `ReservaRepository` | `reservas-service` | Reserva, Mi Agenda, Panel admin |
| `CotizacionRepository` | `cotizacion-service` | Reserva (desglose del precio) |
| `InsumoRepository` | `insumos-service` | Insumos (inventario de la admin) |

Objetos en memoria: `Sesion` (correo, nombre y rol del usuario) y `SeleccionEvento` (productos elegidos).

Diagramas completos en [`documents/arquitectura.md`](documents/arquitectura.md).

---

## Estructura del proyecto

```
com.ban.paoliciassweet
├── MainActivity.kt
├── data
│   ├── api            ProductoApi, ReservaApi, CotizacionApi, InsumoApi y sus Retrofit
│   ├── local
│   │   ├── dao        UserDao, ProductoDao
│   │   ├── database   AppDatabase
│   │   ├── entity     UserEntity (+ Roles), ProductoEntity
│   │   └── seed       ProductosIniciales (catálogo de respaldo sin conexión)
│   └── repository     UserRepository, ProductoRepository, ReservaRepository, CotizacionRepository, InsumoRepository
├── model              un modelo por pantalla, ProductoRemoto, ReservaRemota, Sesion, SeleccionEvento
├── util               Seguridad (PBKDF2), Imagenes, Notificaciones, Errores
└── ui
    ├── components     BarraInferior, CampoValidado, Header, Footer
    ├── navigation     AppNavigation (rutas por rol)
    ├── screen         una pantalla por archivo
    ├── theme          colores, tipografía y tema
    ├── util           Validaciones, Formato
    └── viewmodel      un ViewModel por pantalla
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
| `Admin/insumos` | Inventario de insumos | Administradora |

Las pantallas del cliente redirigen al login si no hay sesión, y al panel si entra la administradora.
Al cerrar sesión se borra el historial de navegación (el botón *Atrás* no vuelve a la sesión cerrada).

---

## Cómo ejecutar

1. Levantar el backend (ver su README): `docker compose up -d --build`.
2. Abrir este proyecto en **Android Studio** y esperar la sincronización de Gradle.
3. Ejecutar en un **emulador** (Android 8.0 / API 26 o superior). En *debug* la app usa `http://10.0.2.2:8081/` (catálogo), `http://10.0.2.2:8082/` (reservas), `http://10.0.2.2:8083/` (cotización) y `http://10.0.2.2:8084/` (insumos), que en el emulador apuntan al computador.
4. Registrar un **cliente** con datos ficticios, o la **administradora** con `admin@paolicias.cl`.

**Cambiar la dirección del backend** sin tocar el código, en `gradle.properties`:

```properties
API_CATALOGO_DEBUG=http://IP_DE_LA_EC2:8081/
API_RESERVAS_DEBUG=http://IP_DE_LA_EC2:8082/
API_COTIZACION_DEBUG=http://IP_DE_LA_EC2:8083/
API_INSUMOS_DEBUG=http://IP_DE_LA_EC2:8084/
API_URL_RELEASE=https://tu-dominio.duckdns.org/
```

> **Ojo con el `gradle.properties` del repositorio:** hoy trae las cuatro líneas `API_*_DEBUG` apuntando a la IP de la EC2.
> Para probar contra el backend local en el emulador, bórralas o coméntalas (así vuelve el valor por defecto `10.0.2.2`) y sincroniza Gradle.
> Si la EC2 está apagada o su IP cambió, la app *debug* no conecta con esas líneas.

**Release:** usa **una sola dirección HTTPS** para los cuatro servicios (`API_URL_RELEASE`, por defecto `https://paolicias.duckdns.org/`).
El `network_security_config.xml` de *release* bloquea HTTP (solo deja pasar `10.0.2.2` y `localhost`); el de *debug* (`src/debug/res/xml`) permite HTTP para pruebas.
Por eso el APK firmado solo carga datos del backend si este corre con el perfil `https` (Caddy) y el dominio apunta a la IP vigente de la EC2 (ver el README del backend).

> **Si cambia una entidad de Room**, hay que subir la versión de `AppDatabase` (hoy **7**). Se usa `fallbackToDestructiveMigration`, que borra y recrea las tablas: hay que volver a registrar los usuarios.

---

## APK firmado

El APK entregable está en [`app/release/app-release.apk`](app/release/app-release.apk) (`versionCode 1`, `versionName 1.0`, `applicationId com.ban.paoliciassweet`).

**Cómo se genera** (dos formas equivalentes):

- **Android Studio:** *Build → Generate Signed App Bundle / APK… → APK → Choose existing… (o Create new…) → variante `release` → Create*.
  Guardar el `.jks` **fuera de la carpeta del proyecto** (y fuera de OneDrive: Gradle falla al leer archivos que están solo en la nube).
- **Consola:** con `keystore.properties` creado, `./gradlew assembleRelease` → `app/build/outputs/apk/release/`.

Para la consola:

1. Crear la llave (una sola vez; **no subirla a GitHub**):
   ```
   keytool -genkeypair -v -keystore paolicias-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias paolicias
   ```
   (`keytool` viene con Android Studio, en `jbr/bin`).
2. Copiar `keystore.properties.example` como `keystore.properties` y completar las claves (`storeFile`, `storePassword`, `keyAlias`, `keyPassword`).
3. Generar con `./gradlew assembleRelease`.

`keystore.properties` y `*.jks` están en `.gitignore`. **Respaldar el `.jks` y sus claves**: sin ellos no se puede firmar una actualización con la misma identidad.

**Verificar la firma** (`apksigner` está en `build-tools` del SDK de Android):

```
apksigner verify --verbose --print-certs app-release.apk
```

Debe decir `Verifies` y `Verified using v2 scheme ... true`. Que v1, v3 y v4 salgan en `false` es normal con `minSdk 26`.

**Notas del build release**

- La optimización (R8) del *release* está **desactivada** (`optimization { enable = false }` en `app/build.gradle.kts`): con ella activada, la APK se cerraba al abrir. Por eso la APK pesa unos 19 MB.
- Para una nueva entrega con cambios, subir `versionCode` y volver a firmar con **la misma llave**; así se instala encima sin borrar las cuentas guardadas en el teléfono.
- Al pasar de una versión *debug* a una *release* (o al revés) hay que **desinstalar** la anterior: la firma es distinta y Android no deja actualizar.

---

## Flujo del abono y estados

El pago no usa pasarela: el cliente transfiere, adjunta el comprobante y la administradora lo valida. El estado vive en `reservas-service`.

```
Pendiente --(cliente adjunta)--> En revisión --(admin valida)--> Pagado 50% --(admin)--> Completado
    ^                                 |
    +--------(admin rechaza)----------+
```

| Estado | Qué significa | Qué puede hacer el cliente |
|---|---|---|
| `Pendiente` | Reserva creada, sin comprobante (o rechazado). | Adjuntar comprobante. |
| `En revisión` | Comprobante esperando validación. | Cambiar el comprobante. |
| `Pagado 50%` | Abono validado; la fecha queda bloqueada. | Nada (ya no se cambia el comprobante). |
| `Completado` | El evento se realizó. | Nada. |

Al **rechazar**, el backend borra el comprobante para que el cliente adjunte uno nuevo.

---

## Reglas de negocio

### Cotización (la calcula `cotizacion-service` con su tabla `tarifas` y los precios de `catalogo-service`)

```
total = tarifa del servicio (según invitados) + Σ por cada producto: ceil(invitados / porciones) × precio
```

| Invitados | Tarifa | Invitados | Tarifa |
|---|---|---|---|
| 1 – 10 | $30.000 | 51 – 60 | $140.000 |
| 11 – 20 | $50.000 | 61 – 70 | $160.000 |
| 21 – 30 | $80.000 | 71 – 80 | $180.000 |
| 31 – 40 | $100.000 | 81 – 90 | $200.000 |
| 41 – 50 | $120.000 | 91 – 100 | $220.000 |

**Ejemplo:** 25 invitados con una *Torta Frutos Rojos* (12 porciones, $45.000): tarifa $80.000 + 3 × $45.000 = **$215.000**, abono **$107.500**.

### Otras reglas

- **Abono:** 50% del total, obligatorio para bloquear la fecha.
- **Correo:** se normaliza con `trim().lowercase()` en la app y en el backend.
- **Administradora:** la cuenta registrada con `admin@paolicias.cl` recibe el rol `ADMIN` en SQLite.
- **Doble envío:** el botón se bloquea mientras se envía, el formulario se limpia al terminar y el backend rechaza duplicados (409).
- **Fechas:** viajan como `dd/MM/yyyy`; el backend las guarda como fechas reales y ordena por fecha y horario.

---

## Validación de formularios

`ui/util/Validaciones.kt` (cada función devuelve el mensaje de error o `null`). El backend repite las reglas de la reserva.

| Formulario | Campo | Regla |
|---|---|---|
| Registro | Nombre | Mínimo 3 caracteres; solo letras y espacios |
| Registro | Correo | Formato válido; no puede estar ya registrado |
| Registro | Contraseña | Mínimo 6 caracteres |
| Registro | Repetir contraseña | Debe coincidir |
| Login | Correo / Contraseña | Formato válido / no vacía |
| Reserva | Nombre del evento | Mínimo 3 caracteres |
| Reserva | Fecha / Horario | Obligatorios (fecha desde hoy) |
| Reserva | Invitados | Solo dígitos, entre 1 y 100 |

Se validan todos los campos a la vez y, al editar un campo, se borra solo su error. Los errores del servidor (sin conexión, datos rechazados) se muestran en rojo bajo el formulario.

---

## Persistencia local (SQLite / Room)

Base `AppDatabase`, **versión 7**:

| Tabla | Entidad | Uso |
|---|---|---|
| `users` | `UserEntity` | Login y registro de los dos roles: nombre, correo (único), contraseña cifrada, rol |
| `productos` | `ProductoEntity` | Copia del catálogo del backend (mismos ids), para usar sin conexión |
| `reservas` | `ReservaEntity` | Tabla **antigua, sin uso**: las reservas viven en `reservas-service`. Sigue registrada en `AppDatabase` |

- Las contraseñas se guardan con **PBKDF2-SHA256 + sal** (`util/Seguridad.kt`), nunca en texto plano.
- El catálogo se observa como `Flow`: cuando llega la respuesta del backend, la lista se actualiza sola.

---

## Recursos nativos

| Rol | Recurso | Uso | Permiso |
|---|---|---|---|
| Cliente | **Cámara** | Fotografiar el comprobante (`TakePicture` + `FileProvider`). | Ninguno: usa la app de cámara del sistema. |
| Cliente | **Galería** | Elegir el comprobante (`PickVisualMedia`). | Ninguno. |
| Cliente | **Vibración** | Al confirmar la reserva (solo si el servidor la guardó). | `VIBRATE` (normal). |
| Admin | **Notificaciones** | Aviso de comprobantes por revisar. | `POST_NOTIFICATIONS`, pedido en tiempo de ejecución (Android 13+). Si se niega, el panel funciona igual y se avisa con un mensaje. |
| Admin | **Vibración** | Al validar o rechazar un abono. | `VIBRATE`. |
| Admin | **Correo** | Contactar al cliente (`ACTION_SENDTO`). | Ninguno. |

La foto se reduce a máximo 1600 px y JPEG 80% antes de subirla (~300 KB). La cámara es opcional en el manifest, así que la app se instala también en equipos sin cámara.

---

## APIs externas

Además de los microservicios propios, la app consume **dos APIs públicas de terceros** con Retrofit (`data/api/ApisExternas.kt`). Las dos son gratuitas y no piden registro ni clave.

| API | Endpoint | Dónde se usa | Para qué |
|---|---|---|---|
| **Nager.Date** (feriados) | `GET https://date.nager.at/api/v3/PublicHolidays/{año}/CL` | Reservar evento, Panel admin | Avisa si la fecha del evento es feriado en Chile, porque ese día cuesta más conseguir personal |
| **ExchangeRate-API** (tipo de cambio) | `GET https://open.er-api.com/v6/latest/CLP` | Cotización y Mi Agenda | Muestra el total y el abono también en dólares, con el valor del día |

- `ExternasRepository` guarda las respuestas en memoria: los feriados de cada año y la tasa del dólar se piden una sola vez.
- **Si la API externa no responde, la app sigue funcionando**: solo no muestra el aviso ni el monto en dólares.
- La lógica pura (convertir la fecha, buscar el feriado, convertir pesos a dólares) está en `util/Externas.kt` y tiene pruebas unitarias (`ExternasTest`).

---

## Animaciones

En **Mi Agenda**: el color del estado cambia gradualmente (`animateColorAsState`, 600 ms) y el comprobante aparece con fundido y expansión (`AnimatedVisibility`).

---

## Pruebas

- App: `./gradlew testDebugUnitTest`, 13 pruebas: `SeguridadTest` (cifrado de contraseñas) y `ExternasTest` (feriados y dólar).
- Backend: JUnit 5 + Mockito en cada microservicio (`./mvnw test`), 49 pruebas en total (catálogo 8, cotización 8, insumos 11, reservas 22).

---

## Limitaciones conocidas

- **Sesión en memoria:** al cerrar la app hay que volver a iniciar sesión.
- **Login local:** como pide la rúbrica, las cuentas viven en el SQLite de cada teléfono; el backend no autentica las peticiones.
- **Administradora por correo:** quien registre `admin@paolicias.cl` en su teléfono queda como administradora en ese teléfono.
- **Archivos antiguos:** `ProductoRemotoScreen.kt`, `ProductoViewModel.kt` (paquete `com.grupo9`), `LineaCotizacion.kt`, `ReservaDao`/`ReservaEntity` y `SaldoDao`/`SaldoEntity` ya no se usan y se pueden borrar (`ReservaEntity` sigue registrada en `AppDatabase`; `SaldoDao`/`SaldoEntity` no).
- **Sin backend:** el catálogo se ve desde la copia guardada en Room, pero reservar, Mi Agenda, el panel de la administradora y los insumos necesitan conexión.
- **APK release con HTTPS:** si la EC2 está apagada, o el dominio no apunta a su IP actual, el APK firmado no llega al backend. La IP pública cambia al reiniciar la instancia si no tiene Elastic IP.

---

## Cumplimiento de la rúbrica (Experiencia 3)

| Requisito | Dónde se cumple |
|---|---|
| Dos roles con navegación distinta | Cliente y administradora; rutas por rol en `AppNavigation` |
| Autenticación con SQLite en ambos roles | `UserRepository` + tabla `users` (Room), contraseñas con PBKDF2 |
| Microservicios con Spring Boot | 4 servicios en el repositorio del backend: catálogo, cotización, reservas e insumos |
| Pruebas unitarias | Backend: 49 con JUnit 5 y Mockito. App: 13 (`SeguridadTest`, `ExternasTest`) |
| Base de datos relacional | PostgreSQL (`paolicias_db`) con 6 tablas |
| Conexión con los microservicios | Retrofit en `data/api`, un repositorio por servicio |
| Recursos nativos según el rol | Cámara, galería, vibración, notificaciones y correo (sección *Recursos nativos*) |
| API externa | Nager.Date (feriados) y ExchangeRate-API (dólar) |
| Formularios validados | `ui/util/Validaciones.kt` y `CampoValidado` |
| Inicio y cierre de sesión | `Sesion`, `LoginViewModel`, botón de cierre en Perfil y en el panel de admin |
| Backend en la nube | AWS EC2 con Docker Compose y HTTPS con Caddy |
| APK firmado | `app/release/app-release.apk` (sección *APK firmado*) |
| Diagramas de arquitectura | [`documents/arquitectura.md`](documents/arquitectura.md) (rama `documents`) |
| Planificación y trabajo en equipo | Tablero Trello y commits por integrante (sección siguiente) |

---

## Equipo y seguimiento

| Integrante | Usuario GitHub |
|---|---|
| Ian Muñoz | [VooNn47](https://github.com/VooNn47) |
| Alexander Heredia | [alheredia-cloud](https://github.com/alheredia-cloud) |
| Bastian Canales | [BastyYisus](https://github.com/BastyYisus) |

- **Tablero Trello:** https://trello.com/b/VhJgbjqS/banqueteriatrello
- **Repositorio del backend:** https://github.com/VooNn47/Paolicias.sweet-backend
- **Convención de commits:** un commit pequeño por cambio, con prefijo `feat:`, `fix:` o `refactor:`.
