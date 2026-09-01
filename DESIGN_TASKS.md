# Guitar Hero — Retoques de Diseño (Feria Universitaria)

Referencia de tareas de diseño visual priorizadas para la presentación.
No tocar funcionalidad hasta completar estas mejoras.

---

## Prioridad 1 — Menú principal (`GameMenu.java`)

**Impacto:** Primera impresión ante los visitantes de la feria.

### 1.1 Título "Guitar Hero"
- Dibujar el título en `paintComponent` de `GameMenu`
- Fuente grande, negrita (ej. 72px), color blanco con sombra oscura
- Posición: centrado en la parte superior del panel

### 1.2 Fondo con degradado
- Reemplazar `setBackground(new Color(43, 45, 48))` por un `GradientPaint`
- Dirección: vertical, de arriba hacia abajo
- Colores sugeridos: negro → gris oscuro → morado/rojo oscuro
- Implementar en `paintComponent` con `Graphics2D`

### 1.3 Resaltado de ítem seleccionado en el menú
- El ítem activo (por teclado/controller) no tiene retroalimentación visual clara
- Pintar un fondo coloreado o borde lateral al ítem con `pressedIndex` activo
- Archivo: `Menu3dItem.java` o `Menu3D.paintComponent`

---

## Prioridad 2 — HUD en pantalla de juego (`Player.java`)

**Impacto:** Legibilidad para espectadores durante la feria.

### 2.1 Barra de vida visual
- Reemplazar la `JLabel` "Life: 50" por una barra gráfica
- Rectángulo que se llena/vacía según `player.life` (0–100)
- Colores: verde → amarillo → rojo según nivel
- Pintar en `Tab.paintComponent` o crear un componente `LifeBar`

### 2.2 HUD semi-transparente
- Cambiar `label.setOpaque(true)` a `false` en `labelDesign()`
- Pintar el fondo del label manualmente con `new Color(0, 0, 0, 160)` (negro con alpha)
- Efecto: las etiquetas se integran con el stage en vez de bloquear la imagen

### 2.3 Score más prominente
- Aumentar tamaño de fuente del score a ~24px
- Considerar colocarlo más centrado o en la parte superior de la pantalla

---

## Prioridad 3 — Highway / Pista de notas (`Tab.java` → `drawLines`)

**Impacto:** Visual central del juego, lo que más tiempo se ve.

### 3.1 Degradado vertical en la pista
- Reemplazar `g.fillRect(...)` gris plano por `GradientPaint`
- Arriba: completamente transparente; Abajo: gris oscuro semitransparente
- Efecto de perspectiva / profundidad

### 3.2 Líneas de carril coloreadas
- Reemplazar `g.setColor(Color.BLACK)` por los colores de cada carril
  - Verde, Rojo, Amarillo, Azul, Naranja (en orden)
- Usar transparencia (~180 alpha) para no saturar

### 3.3 Zona de hit visual
- Dibujar una línea horizontal brillante en `ypos` (donde se deben presionar las notas)
- Color: blanco con alpha, grosor 3–4px
- Opcional: pequeño resplandor con un segundo `drawLine` más ancho y más transparente

---

## Prioridad 4 — Notas que caen (`GameNote` / `Tab.paintNotes`)

**Impacto:** Las notas se ven durante toda la partida.

### 4.1 Degradado interno en las notas
- Reemplazar `g.fillOval(...)` por `GradientPaint` en `Tab.paintNotes`
- De: color sólido del carril → a: versión más clara/blanca en la parte superior
- Efecto visual 3D / brilloso

### 4.2 Glow/resplandor exterior
- Antes del óvalo principal, dibujar uno más grande con el mismo color a ~60 alpha
- Tamaño: +8px en cada dimensión, centrado

---

## Prioridad 5 — Feedback al presionar (`PlayerNote.java` / `Tab.java`)

**Impacto:** Sensación de respuesta del juego — muy notorio al jugar.

### 5.1 Flash al presionar botón
- Cuando `isReleased() == true`, cambiar el fondo del `PlayerNote` a su color brillante
- Cuando `isReleased() == false`, restaurar a `new Color(54, 58, 59)`
- Implementar en el setter `setReleased()` de `PlayerNote`

### 5.2 Texto flotante "PERFECT!" / "MISS!"
- Al acertar una nota: mostrar "PERFECT!" en verde, centrado en la pista
- Al fallar: mostrar "MISS!" en rojo
- El texto desaparece tras ~500ms (usar un `Timer` de Swing)
- Implementar en `Tab.paintComponent` con una variable `feedbackText` + `feedbackTimer`

---

## Archivos involucrados

| Archivo | Tareas |
|---------|--------|
| `Components/Menu/GameMenu.java` | 1.1, 1.2 |
| `Components/Menu/Menu3dItem.java` | 1.3 |
| `Player/Player.java` | 2.1, 2.2, 2.3 |
| `Player/Tab.java` | 2.1, 3.1, 3.2, 3.3, 4.1, 4.2, 5.2 |
| `Player/PlayerNote.java` | 5.1 |

---

## Notas generales

- Todas las tareas usan solo Swing/AWT — sin dependencias externas nuevas.
- Usar `Graphics2D` con `RenderingHints.VALUE_ANTIALIAS_ON` en todo dibujo custom.
- No modificar lógica de juego (puntuación, colisiones, audio) en estas tareas.
- Probar cada cambio corriendo el juego antes de pasar al siguiente.