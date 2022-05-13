package cz.cvut.fit.smejkdo1.bak.gui;

import cz.cvut.fit.smejkdo1.bak.acpf.agent.Agent;
import cz.cvut.fit.smejkdo1.bak.acpf.agent.Team;
import cz.cvut.fit.smejkdo1.bak.acpf.game.GameTurn;
import cz.cvut.fit.smejkdo1.bak.acpf.node.NodeState;
import cz.cvut.fit.smejkdo1.bak.acpf.node.Pos;
import cz.cvut.fit.smejkdo1.bak.acpf.util.NumberPadding;
import cz.cvut.fit.smejkdo1.bak.acpf.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapPanel extends JPanel {
    private final GameTurn turn;
    public boolean animating = false;
    int cellSize = 20;
    private Set<Pos> walls;

    public MapPanel(GameTurn turn) {
        this.turn = turn;
        this.walls = new HashSet<>();
        findWalls();

    }

    private void findWalls() {
        turn.getGameMap().getMap().values().forEach(node -> {
            if (node.getType().equals(NodeState.WALL))
                walls.add(node.getPos());
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(20, 20);

        paintMap(g2d);
        paintUnits(g2d);
        if (turn.isGameFinished())
            paintEndGame(g2d);
    }

    private void paintEndGame(Graphics2D g) {
        g.setFont(g.getFont().deriveFont(this.getHeight() / 5f));
        g.drawString(turn.getGameWinData().getWinner() + " wins!",
                this.getWidth() / 5f,
                this.getHeight() / 5f);
    }

    private void paintUnits(Graphics2D g) {
        Pair<List<Agent>, List<Agent>> pair = turn.getAgents();
        g.setFont(g.getFont().deriveFont(cellSize / 2f));

        pair.getKey().forEach(agent -> paintAgent(g, agent));
        pair.getValue().forEach(agent -> paintAgent(g, agent));
    }

    private void paintAgent(Graphics2D g, Agent agent) {
        if (agent.getTeam().equals(Team.RED))
            g.setColor(Color.RED);
        else
            g.setColor(Color.BLUE);
        //draw agent
        g.fillOval((agent.getPos().y * cellSize) + cellSize / 18,
                (agent.getPos().x * cellSize) + cellSize / 18,
                cellSize * 8 / 9, cellSize * 8 / 9);

        //draw his target
        g.setFont(g.getFont().deriveFont((cellSize / 5f)));
        g.drawString(String.valueOf(
                NumberPadding.intPadding(agent.getId(), 2)),
                agent.getTarget().y * cellSize + 1,
                agent.getTarget().x * cellSize + (cellSize / 5f) + 1);
        g.setFont(g.getFont().deriveFont((cellSize / 2f)));

        g.drawRect((agent.getTarget().y * cellSize) + cellSize / 18,
                (agent.getTarget().x * cellSize) + cellSize / 18,
                cellSize * 8 / 9, cellSize * 8 / 9);

        //draw his direction
        if (agent.getDirection() != null) {
            g.setColor(Color.LIGHT_GRAY);
            switch (agent.getDirection()) {
                case UP:
                    g.fillRect((agent.getPos().y * cellSize) + cellSize * 5 / 12,
                            (agent.getPos().x * cellSize) + 1,
                            cellSize / 6, cellSize / 10);
                    break;
                case DOWN:
                    g.fillRect((agent.getPos().y * cellSize) + cellSize * 5 / 12,
                            (agent.getPos().x * cellSize) + cellSize * 9 / 10,
                            cellSize / 6, cellSize / 10);
                    break;
                case LEFT:
                    g.fillRect((agent.getPos().y * cellSize) + 1,
                            (agent.getPos().x * cellSize) + cellSize * 5 / 12,
                            cellSize / 10, cellSize / 6);
                    break;

                case RIGHT:
                    g.fillRect((agent.getPos().y * cellSize) + cellSize * 9 / 10,
                            (agent.getPos().x * cellSize) + cellSize * 5 / 12,
                            cellSize / 10, cellSize / 6);
                    break;

                default:
                    g.fillRect((agent.getPos().y * cellSize) + 1,
                            (agent.getPos().x * cellSize) + 1,
                            cellSize / 6, cellSize / 6);
                    break;
            }
        }
        //draw his number
        g.setColor(Color.LIGHT_GRAY);
        g.drawString(String.valueOf(NumberPadding.intPadding(agent.getId(), 2)), agent.getPos().y * cellSize + (cellSize / 4),
                agent.getPos().x * cellSize + (cellSize * 2 / 3));

    }

    private void paintMap(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);

        for (int i = 0; i <= turn.getGameMap().getMapSize().x; i++) {
            g.drawLine(0, i * cellSize,
                    turn.getGameMap().getMapSize().y * cellSize,
                    i * cellSize);
        }

        for (int i = 0; i <= turn.getGameMap().getMapSize().y; i++) {
            g.drawLine(i * cellSize, 0, i * cellSize,
                    turn.getGameMap().getMapSize().x * cellSize);
        }

        walls.forEach(pos -> {
            g.fillRect(pos.y * cellSize, pos.x * cellSize, cellSize, cellSize);
        });
    }

    void reset() {
        animating = false;
        turn.reset();
        //GameMap::reinitialize called inside GameTurn::reset to reset map
        this.paintImmediately(this.getBounds());
    }

    public void animate() {
        if (animating)
            return;
        animating = true;
        new Thread(() -> {
            while (animating && !turn.isGameFinished()) {
                turn.makeTurn();
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            paintImmediately(getBounds());
                        }
                    });
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void step() {
        if (animating)
            return;
        turn.makeTurn();
        this.paintImmediately(this.getBounds());
    }
}
