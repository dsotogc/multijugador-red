# multijugador-red

Proyecto del módulo **Programación de Servicios y Procesos (2º DAW)** centrado en la implementación de una arquitectura **cliente–servidor utilizando UDP**.  

El sistema implementa un combate por turnos 2 contra 2 que sirve como contexto para trabajar comunicación entre procesos, sincronización de estado y diseño de protocolos de red.

---

## Table of Contents

- [About](#about)
- [Gameplay Overview](#gameplay-overview)
- [Turn System](#turn-system)
- [Character Classes](#character-classes)
- [Requirements](#requirements)
- [Execution](#execution)
- [Network Architecture](#network-architecture)
  - [Communication Model](#communication-model)
  - [Game Flow Phases](#game-flow-phases)
  - [Message Protocol](#message-protocol)
  - [Synchronized Game State](#synchronized-game-state)
  - [UDP Design Considerations](#udp-design-considerations)
- [Project Structure](#project-structure)
- [Author](#author)

---

## About

**multijugador-red** is a multiplayer client–server application developed in Java.  
The primary objective of the project is to design and document a **UDP-based communication system** that coordinates four concurrent clients through a turn-based game session.

The game logic is intentionally simple; the core focus is on:

- UDP communication  
- multi-client coordination  
- state synchronization  
- turn management  
- protocol design  

---

## Gameplay Overview

- 4 human players  
- 1 character per player  
- Automatic team assignment by connection order:
  - Players 0 and 1 → Team 1  
  - Players 2 and 3 → Team 2  

After the four players connect, a class selection phase begins. No class repetition restrictions apply.

### Victory Condition

A team wins when **both players on the opposing team are dead**.

---

## Turn System

The turn order is fixed and alternates between teams:

```
0 → 2 → 1 → 3 → repeat
```

- Only **one action per turn**
- No time limit
- Dead players are skipped
- Revived players re-enter the rotation in their natural position

---

## Character Classes

### Luchador
1. Basic attack  
2. Defend (increase defense)  
3. Boosted attack (reduces own defense)

### Mago
1. Basic attack  
2. Boosted attack (loses half of current HP)  
3. Area attack (damages enemies)

### Curandero
1. Basic attack  
2. Heal (self or ally)  
3. Sacrifice / revive (dies and revives an ally)

---

## Requirements

- Java 21  
- Maven  

Maven Wrapper is included.

---

## Execution

Five terminals are required: **1 server + 4 clients**

### Compile

```bash
mvn clean compile
```

### Run Server

```bash
mvn exec:java -Dexec.mainClass="com.combate.server.ServidorUDP"
```

Server runs on:

```
localhost:9000
```

### Run Clients (4 terminals)

```bash
mvn exec:java -Dexec.mainClass="com.combate.client.ClienteUDP"
```

---

# Network Architecture

## Communication Model

- Client–Server architecture  
- Transport protocol: **UDP**  
- Messages transmitted as **serialized Java objects**  
- The **server is authoritative**:
  - Maintains the real game state  
  - Validates actions  
  - Controls turn order  
  - Determines victory  

---

## Game Flow Phases

### 1. Connection Phase
Client → `CONEXION`  
Server → `CONFIRMACION_CONEXION (playerIndex|team)`  

While waiting:
- `ESPERA`

When full:
- `TODOS_CONECTADOS`

### 2. Class Selection
Client → `SELECCION_CLASE`

### 3. Game Start
Server → `INICIO_PARTIDA`

### 4. Turn Loop
Each turn:
1. Server broadcasts `ESTADO_PARTIDA`
2. Server sends `TURNO` to active player
3. Client sends `ACCION`
4. Server validates and executes
5. If invalid → `ERROR`

### 5. Chat
Client → `MENSAJE_CHAT`  
Server → `CHAT_GLOBAL`

### 6. End Game
Server → `FIN_PARTIDA`

---

## Message Protocol

Messages use:

```java
class Mensaje implements Serializable {
    TipoMensaje tipo;
    String datos;
}
```

| Type | Direction | Data Format |
|------|-----------|-------------|
| CONEXION | Client → Server | — |
| CONFIRMACION_CONEXION | Server → Client | `numJugador|equipo` |
| ESPERA | Server → Clients | text |
| TODOS_CONECTADOS | Server → Clients | text |
| SELECCION_CLASE | Client → Server | class name |
| INICIO_PARTIDA | Server → Clients | text |
| ESTADO_PARTIDA | Server → Clients | `vida0|vida1|vida2|vida3|turno|estado` |
| TURNO | Server → Active Client | text |
| ACCION | Client → Server | `accion|obj1|obj2` |
| MENSAJE_CHAT | Client → Server | `num|clase|mensaje` |
| CHAT_GLOBAL | Server → Clients | same chat format |
| ERROR | Server → Client | text |
| FIN_PARTIDA | Server → Clients | `Gana equipo X` |

---

## Synchronized Game State

`ESTADO_PARTIDA` format:

```
vida0|vida1|vida2|vida3|turno|estado
```

| Field | Meaning |
|------|--------|
| vidaN | Current HP of player N |
| turno | Index of active player |
| estado | waiting / playing / finished |

---

## UDP Design Considerations

This project uses a simplified UDP model for educational purposes:

- No acknowledgements  
- No retransmission  
- No duplicate control  
- No sequence numbers  
- No reconnection logic  

Designed for localhost or controlled network environments.

---

## Project Structure

```
src/main/java/com/combate/
│
├── server/
│   └── ServidorUDP.java
│
├── client/
│   └── ClienteUDP.java
│
├── net/
│   ├── Mensaje.java
│   └── TipoMensaje.java
│
└── model/
    └── Partida.java
```

---

## Author

**David Soto García**

- GitHub: [@dsotogc](https://github.com/dsotogc)
- Project Link: [https://github.com/dsotogc/game-of-life](https://https://github.com/dsotogc/multijugador-red)