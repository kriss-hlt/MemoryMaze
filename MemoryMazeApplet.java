import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MemoryMazeApplet extends Applet implements KeyListener {
    private int[][] maze;
    private int size = 10;
    private int cellSize = 40;
    private int playerX, playerY;
    private int exitX, exitY;
    private String mensaje = "Usa las flechas para moverte. Presiona R para nuevo nivel.";
    private boolean gano = false;
    private int nivel = 1;

    private int movimientos = 0;
    private String posicionTexto = "Posición: (0,0)";

    @Override
    public void init() {
        setSize(size * cellSize, size * cellSize + 80);
        generarLaberintoGanable();
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Dibujar laberinto
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                g.setColor(maze[i][j] == 1 ? Color.DARK_GRAY : Color.WHITE);
                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }

        // Jugador
        g.setColor(Color.BLUE);
        g.fillOval(playerX * cellSize + 5, playerY * cellSize + 5, cellSize - 10, cellSize - 10);

        // Meta
        g.setColor(Color.GREEN);
        g.fillRect(exitX * cellSize + 10, exitY * cellSize + 10, cellSize - 20, cellSize - 20);

        // Texto
        g.setColor(Color.BLACK);
        g.drawString("Nivel: " + nivel, 10, size * cellSize + 20);
        g.drawString(mensaje, 10, size * cellSize + 35);

        g.drawString("Movimientos: " + movimientos, 10, size * cellSize + 50);
        g.drawString(posicionTexto, 10, size * cellSize + 65);

        if (gano) {
            g.setColor(Color.MAGENTA);
            g.drawString("¡Ganaste! Presiona R para el siguiente nivel.", 10, size * cellSize + 80);
        }
    }

    private void generarLaberintoGanable() {
        maze = new int[size][size];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                maze[i][j] = 1;

        playerX = 0;
        playerY = 0;
        exitX = size - 1;
        exitY = size - 1;

        generarMazeDFS();

        // Asegurar inicio y meta
        maze[playerY][playerX] = 0;
        maze[exitY][exitX] = 0;

        // 🔥 CORRECCIÓN: asegurar que la meta esté conectada
        if (!(maze[exitY - 1][exitX] == 0 || maze[exitY][exitX - 1] == 0)) {
            if (Math.random() < 0.5) maze[exitY - 1][exitX] = 0;
            else maze[exitY][exitX - 1] = 0;
        }

        movimientos = 0;
        posicionTexto = "Posición: (0,0)";
        gano = false;
        repaint();
    }

    private void generarMazeDFS() {
        boolean[][] visited = new boolean[size][size];
        dfs(0, 0, visited);
    }

    private void dfs(int x, int y, boolean[][] visited) {
        visited[y][x] = true;
        maze[y][x] = 0;

        int[][] dirs = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int i = 0; i < 4; i++) {
            int r = (int)(Math.random() * 4);
            int[] t = dirs[i];
            dirs[i] = dirs[r];
            dirs[r] = t;
        }

        for (int[] d : dirs) {
            int nx = x + d[0] * 2;
            int ny = y + d[1] * 2;

            if (nx >= 0 && nx < size && ny >= 0 && ny < size && !visited[ny][nx]) {
                maze[y + d[1]][x + d[0]] = 0;
                dfs(nx, ny, visited);
            }
        }
    }

    private void siguienteNivel() {
        nivel++;
        size += 2;
        cellSize = Math.max(20, cellSize - 2);
        setSize(size * cellSize, size * cellSize + 80);
        generarLaberintoGanable();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int nx = playerX;
        int ny = playerY;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: ny--; break;
            case KeyEvent.VK_DOWN: ny++; break;
            case KeyEvent.VK_LEFT: nx--; break;
            case KeyEvent.VK_RIGHT: nx++; break;
            case KeyEvent.VK_R:
                if (gano) siguienteNivel();
                else generarLaberintoGanable();
                return;
        }

        if (nx >= 0 && nx < size && ny >= 0 && ny < size && maze[ny][nx] == 0) {
            playerX = nx;
            playerY = ny;

            movimientos++;
            posicionTexto = "Posición: (" + playerX + "," + playerY + ")";
        }

        if (playerX == exitX && playerY == exitY) gano = true;

        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
