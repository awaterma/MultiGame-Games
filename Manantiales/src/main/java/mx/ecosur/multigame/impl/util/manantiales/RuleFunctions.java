/*
 * Copyright (C) 2010 ECOSUR, Andrew Waterman
 *
 * Licensed under the Academic Free License v. 3.2.
 * http://www.opensource.org/licenses/afl-3.0.php
 */

package mx.ecosur.multigame.impl.util.manantiales;

import mx.ecosur.multigame.impl.Color;

import mx.ecosur.multigame.impl.model.GridCell;
import mx.ecosur.multigame.impl.model.GameGrid;
import mx.ecosur.multigame.impl.model.GridMove;
import mx.ecosur.multigame.impl.model.GridPlayer;

import mx.ecosur.multigame.impl.entity.manantiales.ManantialesFicha;
import mx.ecosur.multigame.impl.entity.manantiales.ManantialesGame;
import mx.ecosur.multigame.impl.entity.manantiales.ManantialesMove;
import mx.ecosur.multigame.impl.entity.manantiales.ManantialesPlayer;

import mx.ecosur.multigame.impl.enums.manantiales.BorderType;
import mx.ecosur.multigame.impl.enums.manantiales.ConditionType;
import mx.ecosur.multigame.impl.enums.manantiales.Mode;
import mx.ecosur.multigame.impl.enums.manantiales.TokenType;

import java.util.HashSet;
import java.util.List;

/**
 * @author awaterma@ecosur.mx
 */
public class RuleFunctions {

    public static  boolean isValidReplacement (GameGrid grid, ManantialesMove move) {
        boolean ret = false;
        if (move.getDestinationCell() != null) {
            ManantialesFicha destination = (ManantialesFicha) move.getDestinationCell();
            ManantialesFicha current = (ManantialesFicha) grid.getLocation (destination);
            TokenType replacement = destination.getType();
            if (current != null) {
               switch (current.getType()) {
                   case MANAGED_FOREST:
                       ret = !(replacement.equals(TokenType.INTENSIVE_PASTURE) || replacement.equals(TokenType.SILVOPASTORAL));
                       break;
                   case MODERATE_PASTURE:
                       ret = (replacement.equals(TokenType.INTENSIVE_PASTURE) || replacement.equals(TokenType.MANAGED_FOREST))
                               && !(replacement.equals(TokenType.SILVOPASTORAL));
                       break;
                   case VIVERO:
                       ret = replacement.equals(TokenType.SILVOPASTORAL);
                       break;
                   case SILVOPASTORAL:
                       ret = !(replacement.equals(TokenType.INTENSIVE_PASTURE));
                       break;
                   default:
                       ret = false;
                       break;
               }
            } else {
               ret = true;
            }
        }

        return ret;
    }

    public static int score (ManantialesPlayer player, ManantialesGame game) {
        // reset player count to 0
        player.reset();

        for (GridCell cell : game.getGrid().getCells()) {
            if (!cell.getColor().equals(player.getColor()))
                continue;            
            ManantialesFicha ficha = (ManantialesFicha) cell;
            switch (ficha.getType()) {
                case MANAGED_FOREST:
                    player.setForested(player.getForested() +  1);
                    break;
                case MODERATE_PASTURE:
                    player.setModerate(player.getModerate() + 1);
                    break;
                case INTENSIVE_PASTURE:
                    player.setIntensive(player.getIntensive() + 1);
                    break;
                case VIVERO:
                    player.setVivero(player.getVivero() + 1);
                    break;
                case SILVOPASTORAL:
                    player.setSilvo(player.getSilvo() + 1);
                    break;
                default:
                    break;
            }
        }

        int forested = player.getForested() * 1;
        int moderate = player.getModerate() * 2;
        int intensive = player.getIntensive() * 3;
        int silvo = player.getSilvo() * 4;

        return forested + moderate + intensive + silvo;
    }

    public static  boolean isWinner (Mode mode, ManantialesPlayer gamePlayer) {
        boolean ret = false;
        ManantialesPlayer player = (ManantialesPlayer) gamePlayer;
        if (mode != Mode.BASIC_PUZZLE && mode != Mode.SILVO_PUZZLE)
            ret =  player.getScore() >= mode.getWinningScore();
        return ret;
    }

    public static boolean isSolved (Mode mode, ManantialesGame game) {
        boolean ret = true;
        for (GridPlayer gridPlayer : game.getPlayers()) {
            ManantialesPlayer player = (ManantialesPlayer) gridPlayer;
            ret = ret && ( player.getScore() >= mode.getWinningScore() );
        }
        
        ret = ret && (game.getCheckConditions().size() == 0);

        return ret;
    }

    public static  boolean isBorder (GridCell cell) {
        return (cell.getColumn () == 4 || cell.getRow () == 4);
    }

    public static  GridPlayer incrementTurn (ManantialesGame game, GridMove move) {
        GridPlayer player = move.getPlayer();
        player.setTurn(false);
        List<GridPlayer> players = game.getPlayers();
        int playerNumber = players.indexOf(player);
        GridPlayer nextPlayer = null;
        if (playerNumber == players.size() - 1) {
            nextPlayer = players.get(0);
        } else {
            nextPlayer = players.get(playerNumber + 1);
        }
        nextPlayer.setTurn (true);
        return nextPlayer;
    }

    public static  boolean isPrecedingPlayer (ManantialesGame game, GridPlayer first, GridPlayer second) {
        boolean ret = false;
        if (!first.equals(second)) {
            List<GridPlayer> players = game.getPlayers();
            int playerNumber = players.indexOf(first);
            GridPlayer nextPlayer = null;
            if (playerNumber == players.size() - 1) {
                nextPlayer = players.get(0);
            } else {
                nextPlayer = players.get(playerNumber + 1);
            }
            ret = nextPlayer.equals(second);
        }
        return ret;
    }


    public static  GameGrid clearPlayer (ManantialesGame game, ManantialesPlayer player) {
        GameGrid grid = game.getGrid();

        for (GridCell cell : game.getGrid().getCells()) {
            if (cell.getColor().equals(player.getColor()))
                grid.removeCell(cell);
        }

        return grid;
    }

    public static  GameGrid clearBorder (ManantialesGame game, ConditionType violation) {
        GameGrid ret = game.getGrid();
        HashSet<ManantialesFicha> deletions = new HashSet<ManantialesFicha>();

            /* Set up deletable types */
        HashSet<TokenType> deletables = new HashSet<TokenType>();
        deletables.add (TokenType.MODERATE_PASTURE);
        deletables.add(TokenType.INTENSIVE_PASTURE);

        switch (violation) {
            case NORTHERN_BORDER_DEFORESTED:
                for (GridCell cell : ret.getCells()) {
                    ManantialesFicha ficha = (ManantialesFicha) cell;
                    if (deletables.contains(ficha.getType()) && ficha.getBorder().equals(BorderType.NORTH))
                        deletions.add(ficha);
                }
                break;
            case WESTERN_BORDER_DEFORESTED:
                for (GridCell cell : ret.getCells()) {
                    ManantialesFicha ficha = (ManantialesFicha) cell;
                    if (deletables.contains(ficha.getType()) && ficha.getBorder().equals(BorderType.WEST))
                        deletions.add(ficha);
                }
                break;
            case SOUTHERN_BORDER_DEFORESTED:
                for (GridCell cell : ret.getCells()) {
                    ManantialesFicha ficha = (ManantialesFicha) cell;
                    if (deletables.contains(ficha.getType()) && ficha.getBorder().equals(BorderType.SOUTH))
                        deletions.add(ficha);
                }
                break;
            case EASTERN_BORDER_DEFORESTED:
                for (GridCell cell : ret.getCells()) {
                    ManantialesFicha ficha = (ManantialesFicha) cell;
                    if (deletables.contains(ficha.getType()) && ficha.getBorder().equals(BorderType.EAST))
                        deletions.add(ficha);
                }
                break;
            default:
                break;
        }

        for (ManantialesFicha ficha : deletions)
            ret.removeCell(ficha);

        return ret;
    }

    public static  GameGrid clearTerritory (ManantialesGame game, Color color) {
        GameGrid ret = game.getGrid();
        HashSet<ManantialesFicha> deletions = new HashSet<ManantialesFicha>();
        for (GridCell cell : ret.getCells()) {
            if (cell.getColor().equals(color)) {
                ManantialesFicha ficha = (ManantialesFicha) cell;
                if (ficha.getType().equals(TokenType.MANAGED_FOREST))
                    continue;
                deletions.add(ficha);
            }
        }

        for (ManantialesFicha ficha : deletions) {
            ret.removeCell (ficha);
        }

        return ret;
    }

    public static  Mode incrementMode (Mode mode) {
        if (mode == null) {
            mode = Mode.CLASSIC;
        } else {
            switch (mode) {
                case CLASSIC:
                    mode = Mode.BASIC_PUZZLE;
                    break;
                case BASIC_PUZZLE:
                    mode = Mode.SILVOPASTORAL;
                    break;
                case SILVOPASTORAL:
                    mode = Mode.SILVO_PUZZLE;
                    break;
                default:
                    break;
            }
        }
        return mode;
    }

}
