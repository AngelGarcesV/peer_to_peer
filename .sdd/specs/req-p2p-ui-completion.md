# Specifications: req-p2p-ui-completion

## Intent
Completar la UI y lógica faltante para cumplir 3 requerimientos P2P del sistema Cliente-Servidor.

## REQ-1: Detección de servidores (UI)

### Escenarios

**Escenario 1.1: Listar servidores disponibles (Happy Path)**
- **Given** que el usuario abrió la aplicación y hay al menos 2 servidores conectados a la red P2P.
- **When** el usuario hace clic en el botón "Servidores" en el sidebar principal.
- **Then** la vista `servidores.fxml` se carga en el panel principal.
- **And** se muestra una tabla con las columnas: ID, Host, Puerto y Estado.
- **And** la tabla se puebla con los datos obtenidos de `ServerService.listarServidores()`.

**Escenario 1.2: Red sin otros servidores conectados**
- **Given** que el servidor actual es el único en la red.
- **When** el usuario navega a la vista de "Servidores".
- **Then** la tabla se muestra vacía o con un único registro (el servidor local).
- **And** la UI no se bloquea ni lanza excepciones.

### Criterios de Aceptación
- Existe un botón "Servidores" en `main.fxml`.
- `ServidoresController.java` y `servidores.fxml` están implementados y funcionales.
- La tabla muestra correctamente la lista de nodos.

---

## REQ-2: Archivos unicast (P2P target)

### Escenarios

**Escenario 2.1: Enviar archivo a un destinatario específico (Happy Path)**
- **Given** que el usuario está en la vista de carga de archivos (`UploadController`).
- **And** seleccionó un archivo válido.
- **When** el usuario ingresa un `clientIdDestino` válido en el campo "Destinatario".
- **And** hace clic en "Subir/Enviar".
- **Then** `FileService.createUploadTask` es llamado con el ID del destinatario.
- **And** el archivo se divide en chunks y se envía con el target ID propagado en los eventos `INICIAR_STREAM`, chunks intermedios, y `FINALIZAR_STREAM`.
- **And** solo el cliente/servidor destino ensambla y guarda el archivo.

**Escenario 2.2: Enviar archivo en formato broadcast (Fallback)**
- **Given** que el usuario no ingresa ningún "Destinatario" (campo vacío).
- **When** envía el archivo.
- **Then** el sistema asume un envío broadcast (o destino por defecto) como en el comportamiento anterior.

**Escenario 2.3: Destinatario inexistente o desconectado (Error)**
- **Given** que el usuario ingresa un `clientIdDestino` que no está en la red.
- **When** intenta enviar el archivo.
- **Then** el envío falla durante la inicialización o el destino no acusa recibo.
- **And** la UI muestra un mensaje de error indicando que el destinatario es inalcanzable (opcional según el soporte de ack del protocolo).

### Criterios de Aceptación
- Nuevo input text/combo en `UploadController` para "Destinatario".
- `EnviarArchivoHandler` lee y respeta `clientIdDestino` en vez de ignorarlo.
- El ID de destino se mantiene en el DTO de red durante toda la transferencia.

---

## REQ-3: Logs de otros servidores

### Escenarios

**Escenario 3.1: Consultar logs de servidor remoto (Happy Path)**
- **Given** que el usuario navega a la vista de logs (`LogsController`).
- **When** el usuario selecciona un servidor remoto en el ComboBox "Seleccionar Servidor".
- **Then** la UI hace un request asíncrono utilizando `LogService.getRemoteLogs(servidorId)`.
- **And** los logs devueltos se renderizan en el panel de logs.

**Escenario 3.2: Consultar logs locales**
- **Given** que el usuario selecciona "Local" o el ID del servidor actual en el ComboBox.
- **When** se solicita la recarga de logs.
- **Then** se obtienen y muestran los logs locales (comportamiento legacy preservado).

**Escenario 3.3: Servidor remoto inalcanzable para logs (Error)**
- **Given** que el usuario selecciona un servidor remoto que se acaba de desconectar.
- **When** el sistema intenta obtener los logs remotos.
- **Then** `LogService` retorna un timeout o error.
- **And** la UI muestra un mensaje "No se pudieron obtener los logs del servidor seleccionado".

### Criterios de Aceptación
- ComboBox implementado en `LogsController`.
- Invocación correcta a `LogService.getRemoteLogs` según el ítem seleccionado.
- Manejo adecuado de la espera de respuesta (preferiblemente sin freezar el UI thread).