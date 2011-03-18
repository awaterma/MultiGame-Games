/*
* Copyright (C) 2010 ECOSUR, Andrew Waterman and Max Pimm
*
* Licensed under the Academic Free License v. 3.0.
* http://www.opensource.org/licenses/afl-3.0.php
*/

/**
 * @author awaterma@ecosur.mx
 */

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import mx.ecosur.multigame.enums.GameState;
import mx.ecosur.multigame.enums.MoveStatus;
import mx.ecosur.multigame.exception.InvalidMoveException;
import mx.ecosur.multigame.grid.Color;
import mx.ecosur.multigame.impl.entity.gente.GenteMove;
import mx.ecosur.multigame.grid.model.GridCell;
import mx.ecosur.multigame.grid.model.GameGrid;

import mx.ecosur.multigame.model.interfaces.Move;
import org.junit.Test;

import static junit.framework.Assert.*;


public class GenteAgentLogicTest extends GenteAgentTestBase {
        
    @Test
    /* Simple test to check the Available move logic in GenteStrategyAgent */
    public void testAvailableMoves () {
        TreeSet<GenteMove> unbound = alice.determineAvailableMoves(game);
        assertEquals (7, unbound.size());
    }

    @Test
    public void testRandomNextMove () throws InvalidMoveException {
        Set<Move> moves = alice.determineMoves(game);
        for (Move next : moves) {
            assertNotNull (next);
            game.move(next);
            /* Validate that the move was made */
            assertEquals (MoveStatus.EVALUATED, next.getStatus());
            assertEquals (next.getDestinationCell(), game.getGrid().getLocation(
                        (GridCell) next.getDestinationCell()));
            break;
        }
}

    public void testRandomMoveOnEmptyBoard () throws InvalidMoveException {
        /* Reset the Grid */
        game.setGrid(new GameGrid());
        game.setState(GameState.PLAY);

        /* Run the same code in test GridMove but on a simpler board */
        testRandomNextMove ();
    }

    @Test
    public void testBlockerNextMove () throws InvalidMoveException {
        alice.setTurn(false);
        bob.setTurn(true);
        Set<Move> moves = bob.determineMoves(game);
        for (Move next : moves) {
            assertNotNull (next);
            /* Ensure that the last move blocks a score by blue or green */
            GridCell destination = (GridCell) next.getDestinationCell();
            if (destination.getColumn() == 11) {
                    if (destination.getRow() != 10 && destination.getRow () != 7)
                            fail("Destination [" + destination + "] does not block any available " +
                            "enemy moves!");
            } else if (destination.getColumn() == 9) {
                    assertEquals (8, destination.getRow());
            } else if (destination.getColumn() == 13) {
                    assertEquals (7, destination.getRow());
            } else
                    fail("Destination [" + destination + "] does not block any available " +
                                    "enemy moves!");
            /* Validate that the next move was made, and is on the board */
            game.move(next);
            assertEquals (next.getDestinationCell(), game.getGrid().getLocation(
                            (GridCell) next.getDestinationCell()));
            break;
        }
    }

    @Test
    public void testSimpleNextMove () throws InvalidMoveException {
        charlie.setTurn(true);
        Set<Move> moves = charlie.determineMoves(game);
        for (Move next : moves) {
            assertNotNull (next);
            /* Validate that the next move is possible */
            game.move (next);
            assertTrue (next.getStatus() == MoveStatus.EVALUATED);
            break;
        }
    }

    public void testSimpleMoveOnStartingBoard () throws InvalidMoveException {
        /* Reset the Grid */
        GameGrid grid = game.getGrid();
        HashSet<GridCell> cells = new HashSet<GridCell> ();
        cells.add(new GridCell(10,10, Color.YELLOW));
        cells.add(new GridCell(9,9, Color.BLUE));
        grid.setCells(cells);
        game.setGrid(grid);
        game.setState(GameState.PLAY);

        charlie.setTurn(true);
        Set<Move> moves = charlie.determineMoves(game);
        for (Move next : moves) {
            assertNotNull (next);
            /* Validate that the next move is possible */
            game.move (next);
            assertEquals (MoveStatus.EVALUATED, next.getStatus());
            break;
        }
    }
}
