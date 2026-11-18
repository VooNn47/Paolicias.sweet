# Paolicias.Sweet – Diagramas de arquitectura

> Esta carpeta va en la rama **`documents`** del repositorio Android (entregable 3 de la rúbrica).
> Los diagramas están en Mermaid: GitHub los dibuja solo al abrir este archivo.

## 1. Arquitectura de microservicios

```mermaid
flowchart LR
    subgraph Android["App Android (Jetpack Compose)"]
        UI["Compose UI<br/>pantallas"] --> VM["ViewModel<br/>estado y validación"]
        VM --> REPO["Repository"]
        REPO --> RET["Retrofit<br/>HTTPS + JSON"]
        REPO --> ROOM[("SQLite / Room<br/>login, registro,<br/>copia del catálogo")]
    end

    subgraph Nube["AWS EC2 – Docker (un contenedor por microservicio)"]
        CADDY["Caddy<br/>HTTPS :443"]
        CAT["catalogo-service<br/>Spring Boot + JPA<br/>:8081"]
        RES["reservas-service<br/>Spring Boot + JPA<br/>:8082"]
        PG[("PostgreSQL<br/>paolicias_db<br/>productos · reservas")]
        VOL[["Volumen<br/>comprobantes"]]
    end

    RET -- "/api/productos" --> CADDY
    RET -- "/api/reservas · /api/cotizacion" --> CADDY
    CADDY --> CAT
    CADDY --> RES
    RES -- "precios (HTTP interno)" --> CAT
    CAT --> PG
    RES --> PG
    RES --> VOL
```

| Microservicio | Tabla | Endpoints principales |
|---|---|---|
| catalogo-service | `productos` | `GET /api/productos`, `GET /api/productos/{id}`, `POST /api/productos` |
| reservas-service | `reservas` | `POST /api/cotizacion`, `POST/GET /api/reservas`, `POST/GET /api/reservas/{id}/comprobante`, `PATCH /api/reservas/{id}/validar · rechazar · completar` |

## 2. Arquitectura del proyecto Android (MVVM + Repository)

```mermaid
flowchart TB
    subgraph UI["ui/screen (Composables)"]
        S1[LoginScreen / RegisterScreen]
        S2[HomeScreen / ProductoDetalleScreen]
        S3[ReservaScreen]
        S4[AgendaScreen]
        S5[PerfilScreen]
        S6[PanelAdminScreen]
    end

    subgraph VMs["ui/viewmodel"]
        V1[LoginViewModel / RegisterViewModel / PerfilViewModel]
        V2[HomeViewModel / ProductoDetalleViewModel]
        V3[ReservaViewModel]
        V4[AgendaViewModel]
        V6[AdminViewModel]
    end

    subgraph Datos["data/repository"]
        R1[UserRepository]
        R2[ProductoRepository]
        R3[ReservaRepository]
    end

    S1 --> V1 --> R1 --> DAO1[("UserDao → SQLite users")]
    S5 --> V1
    S2 --> V2 --> R2
    R2 --> DAO2[("ProductoDao → SQLite productos")]
    R2 --> API1["ProductoApi (Retrofit) → catalogo-service"]
    S3 --> V3 --> R3
    V3 --> R2
    S4 --> V4 --> R3
    S6 --> V6 --> R3
    R3 --> API2["ReservaApi (Retrofit) → reservas-service"]
```

## 3. Navegación por rol

```mermaid
flowchart LR
    INICIO --> LOGIN
    LOGIN --> REGISTER --> LOGIN
    LOGIN -- "rol CLIENTE (SQLite)" --> HOME
    LOGIN -- "rol ADMIN (SQLite)" --> ADMIN["Panel Admin"]
    subgraph Cliente
        HOME["Catálogo"] --> PRODUCTO["Detalle"]
        HOME <--> RESERVA["Reservar"]
        HOME <--> AGENDA["Mi Agenda"]
        HOME <--> PERFIL["Perfil"]
    end
    PERFIL -- "cerrar sesión" --> LOGIN
    ADMIN -- "cerrar sesión" --> LOGIN
```

## 4. Recursos nativos por rol

| Rol | Recurso | Dónde | Permiso |
|---|---|---|---|
| Cliente | Cámara (foto del comprobante) | Mi Agenda | Ninguno: usa la app de cámara del sistema y un `FileProvider` privado |
| Cliente | Galería (selector de fotos) | Mi Agenda | Ninguno: `PickVisualMedia` |
| Cliente | Vibración al confirmar la reserva | Reservar | `VIBRATE` (normal) |
| Admin | Notificación de comprobantes por revisar | Panel Admin | `POST_NOTIFICATIONS`, pedido en tiempo de ejecución (Android 13+) |
| Admin | Vibración al validar / rechazar | Panel Admin | `VIBRATE` (normal) |
| Admin | Correo al cliente (intent) | Panel Admin | Ninguno |

## 5. Flujo del abono

```mermaid
stateDiagram-v2
    [*] --> Pendiente: cliente envía la reserva
    Pendiente --> EnRevision: cliente adjunta comprobante
    EnRevision --> EnRevision: cliente cambia comprobante
    EnRevision --> Pagado50: admin valida
    EnRevision --> Pendiente: admin rechaza
    Pagado50 --> Completado: admin marca completada
    Completado --> [*]
```
