package com.tjger.lib;

import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.GamePlayer;
import com.tjger.game.GameRules;
import com.tjger.game.GameState;
import com.tjger.game.MoveInformation;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Provides some algorithms that are used to implement artificial intelligence.
 * 
 * @see AiMoveEvaluator
 * @see AiMoveInformation
 * @author hagru, Joe
 */
public final class AiAlgorithms {

    // as game rules and game engine exist already when methods are called
    final private static GameRules rules = GameManager.getInstance().getGameRules();
    final private static GameEngine engine = GameManager.getInstance().getGameEngine();

    private static int currentDepth = 0;
    private static int currentStep = 0;
    private static int countEvaluations = 0;

    /**
     * @return The current depth of the algorithm (for generate moves).
     */
    public static int getCurrentDepth() {
        return AiAlgorithms.currentDepth;
    }

    /**
     * @return The current step of the algorithm (for generate moves).
     */
    public static int getCurrentStep() {
        return AiAlgorithms.currentStep;
    }

    /**
     * Returns the number of evaluated moves.
     * 
     * @return The number of evaluated moves.
     */
    public static int getCountEvaluations() {
        return AiAlgorithms.countEvaluations;
    }

    /**
     * Returns the next player defined by the game rules depending by the current player, the last move and 
     * the current game state.
     * 
     * @param player the current player in the AI game tree
     * @param move the last move performed in the AI game tree
     * @param state the current game state in the AI game tree
     * @return the next player
     */
    private static GamePlayer getNextPlayer(GamePlayer player, MoveInformation move, GameState state) {
        if (MoveUtil.isMoveComplete(move)) {
            return rules.getNextPlayer(player, state);
        } else {
            return player;
        }
    }

    /**
     * Gets the best move with the Minimax algorithm.
     * 
     * @param depth The search depth.
     * @param evaluator The class for evaluating the moves.
     * @return The best move.
     * @see #getMiniMaxMove(int, AiMoveEvaluator, boolean, AiAnalyzer)
     */
    public static AiMoveInformation getMiniMaxMove(int depth, AiMoveEvaluator evaluator) {
        return getMiniMaxMove(depth, evaluator, true);
    }

    /**
     * Gets the best move with the Minimax algorithm.
     * 
     * @param depth The search depth.
     * @param evaluator The class for evaluating the moves.
     * @param usePruning Should always be {@code true} because the algorithm is much faster with pruning.
     * @return The best move.
     * @see #getMiniMaxMove(int, AiMoveEvaluator, boolean, AiAnalyzer)
     */
    public static AiMoveInformation getMiniMaxMove(int depth, AiMoveEvaluator evaluator, boolean usePruning) {
        return getMiniMaxMove(depth, evaluator, usePruning, null);
    }

    /**
     * Gets the best move with the Minimax algorithm (see <a
     * href="https://en.wikipedia.org/wiki/Minimax">Wikipedia</a>).
     * 
     * @param depth The search depth.
     * @param evaluator The class for evaluating the moves.
     * @param usePruning Should always be {@code true} because the algorithm is much faster with pruning. If
     *            {@code false} the Alpha-Beta-Pruning optimization (see <a
     *            href="https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning">Wikipedia</a>) is not used
     *            and so every possible game state in search depth is evaluated. <b>{@code false} should only
     *            be used for testing reasons!</b>
     * @param analyzer The class to analyze the algorithm. If {@code null} no analysis information will be
     *            generated.<b>Only needed for testing reasons!</b>
     * @return The best move.
     */
    public static AiMoveInformation getMiniMaxMove(int depth, AiMoveEvaluator evaluator, boolean usePruning, AiAnalyzer analyzer) {
        currentDepth = depth;
        currentStep = 0;
        countEvaluations = 0;
        GameState state = (GameState) engine.getGameState().clone();
        GamePlayer player = engine.getCurrentPlayer();
        Element analysisRoot = createAnalysisRoot(analyzer, player);
        Date startTimestamp = null;
        Date stopTimestamp = null;
        long duration = 0;
        AiMoveInformation bestMove;
        if (usePruning) {
            AiMoveInformation alpha = new StartingAlphaMove();
            AiMoveInformation beta = new StartingBetaMove();
            if (analyzer != null) {
	            startTimestamp = new Date();
	            HGBaseTools.testPerformance();
            }
            bestMove = getMiniMaxMoveAlphaBetaPruning(depth, currentStep, null, alpha, beta, player, null,
                                                      state, evaluator, true, analyzer, analysisRoot);
            if (analyzer != null) {
	            duration = HGBaseTools.testPerformance();
	            stopTimestamp = new Date();
            }
            if ((bestMove instanceof StartingAlphaMove) || (bestMove instanceof StartingBetaMove)) {
                bestMove = null;
            }
        } else {
        	if (analyzer != null) {
	            startTimestamp = new Date();
	            HGBaseTools.testPerformance();
        	}
            bestMove = getMiniMaxMove(depth, currentStep, null, player, state, evaluator, true, analyzer, analysisRoot);
            if (analyzer != null) {
	            duration = HGBaseTools.testPerformance();
	            stopTimestamp = new Date();
            }
        }
        setAnalysisSummary(analyzer, analysisRoot, getCountEvaluations(), duration, startTimestamp, stopTimestamp, bestMove);
        return bestMove;
    }

    /**
     * Gets the best move with the Minimax algorithm.
     * 
     * @param depth The search depth.
     * @param step The current step.
     * @param evaluateMove The move to evaluate.
     * @param player The player who is in turn.
     * @param state The current game state.
     * @param evaluator The class for evaluating the moves.
     * @param isMax {@code true} if the move with the highest value or {@code false} if the move with the
     *            lowest value should be returned.
     * @param analyzer The class to analyze the algorithm. If {@code null} no analysis information will be
     *            generated.
     * @param analysisParentNode The parent node for the analysis.
     * @return The best move.
     */
    private static AiMoveInformation getMiniMaxMove(int depth, int step, AiMoveInformation evaluateMove,
                                                    GamePlayer player, GameState state, AiMoveEvaluator evaluator,
                                                    boolean isMax, AiAnalyzer analyzer, Element analysisParentNode) {
        if (!engine.isActiveGame() || !engine.isActiveRound()) {
            return null;
        }
        boolean gameFinished = rules.isGameFinished(state);
        if (depth == 0 || gameFinished) {
            if (evaluateMove == null) {
                return null;
            } else {
                long value = evaluator.evaluateState(step, player, evaluateMove, state, rules);
                if (!player.equals(engine.getCurrentPlayer())) {
                    value = value * -1;
                }                
                evaluateMove.setEvaluationValue(value);
                countEvaluations++;
                appendEvaluationAnalysis(analyzer, analysisParentNode, step, player, evaluateMove, state, value);
                return evaluateMove;
            }
        } else {
            currentDepth = depth;
            currentStep = step + 1;
            Element stepNode = appendStepAnalysis(analyzer, analysisParentNode, currentDepth, currentStep, isMax, player, state, null, null);
            int numPossible = 0;
            ArrayList<AiMoveInformation> bestMoves = new ArrayList<>();
            AiMoveInformation bestValue = null;
            AiMoveInformation[] possibleMoves = evaluator.generateMoves(player, state, rules);
            for (AiMoveInformation nextMove : possibleMoves) {
                if (rules.isValidMove(nextMove, state)) {
                    Element moveNode = appendValidMoveAnalysis(analyzer, stepNode, nextMove, player, state);
                    numPossible++;
                    state.changeState(player, nextMove, engine);
                    setMoveAnalysisChangedState(analyzer, moveNode, state);
                    GamePlayer nextPlayer = getNextPlayer(player, nextMove, state);
                    //FIXME boolean helpIsMax = (nextPlayer == null) ? !isMax : nextPlayer.equals(engine.getCurrentPlayer());
                    boolean helpIsMax = (nextPlayer == null || depth == 1) ? isMax : nextPlayer.equals(engine.getCurrentPlayer());
                    if (nextPlayer == null || depth == 1) {
                    	// if the evaluation is done, do not take the next player FIXME
                    	nextPlayer = player;
                    }
                    AiMoveInformation move = getMiniMaxMove(depth - 1, step + 1, nextMove, nextPlayer, state, evaluator, helpIsMax, analyzer, moveNode);
                    state.undoMove(player, nextMove);
                    if (move != null) {
                        long evaluationValue = move.getEvaluationValue();
                        nextMove.setEvaluationValue(evaluationValue);
                        setMoveAnalysisEvaluationValue(analyzer, moveNode, evaluationValue);
                        long bestEvaluationValue = getEvaluationValue(bestValue);
                        if (bestValue == null) {
                            bestValue = nextMove;
                            bestMoves.add(nextMove);
                        } else if (evaluationValue == bestEvaluationValue) {
                            bestMoves.add(nextMove);
                        } else if ((isMax) && (evaluationValue > bestEvaluationValue)) {
                            bestValue = nextMove;
                            bestMoves.clear();
                            bestMoves.add(nextMove);
                        } else if ((!isMax) && (evaluationValue < bestEvaluationValue)) {
                            bestValue = nextMove;
                            bestMoves.clear();
                            bestMoves.add(nextMove);
                        }
                    }
                } else {
                    appendInvalidMoveAnalysis(analyzer, stepNode, nextMove);
                }
            }
            if (numPossible == 0) {
                // ignore isMax if depth = 0
                return getMiniMaxMove(0, step, evaluateMove, player, state, evaluator, false, analyzer, stepNode);
            }
            setStepAnalysisEvaluationValue(analyzer, stepNode, getEvaluationValue(bestValue));
            AiMoveInformation bestMove = (bestMoves.isEmpty()) ? null : bestMoves.get(DiceUtil.throwDice(0, bestMoves.size() - 1));
            markStepAnalysisBestMoves(analyzer, stepNode, bestMoves, bestMove);
            return bestMove;
        }
    }

    /**
     * Gets the best move with the Minimax algorithm optimized with the Alpha-Beta-Pruning method.
     * 
     * @param depth The search depth.
     * @param step The current step.
     * @param evaluateMove The move to evaluate.
     * @param alpha The lower pruning bound.
     * @param beta The upper pruning bound.
     * @param player The who is in turn.
     * @param prevPlayer the previous player who did the move to evaluate
     * @param state The current game state.
     * @param evaluator The class for evaluating the moves.
     * @param isMax {@code true} if the move with the highest value or {@code false} if the move with the
     *            lowest value should be returned.
     * @param analyzer The class to analyze the algorithm. If {@code null} no analysis information will be
     *            generated.
     * @param analysisParentNode The parent node for the analysis.
     * @return The best move.
     */
    private static AiMoveInformation getMiniMaxMoveAlphaBetaPruning(int depth, int step, AiMoveInformation evaluateMove, AiMoveInformation alpha, AiMoveInformation beta,
                                                                    GamePlayer player, GamePlayer prevPlayer, GameState state, AiMoveEvaluator evaluator,
                                                                    boolean isMax, AiAnalyzer analyzer, Element analysisParentNode) {
        if (!engine.isActiveGame() || !engine.isActiveRound()) {
            return null;
        }
        if (depth == 0 || rules.isGameFinished(state)) {
            if (evaluateMove == null || prevPlayer == null) {
                return null;
            } else {
                long value = evaluator.evaluateState(step, player, evaluateMove, state, rules);
                if (!player.equals(engine.getCurrentPlayer())) {
                    value = value * -1;
                }                
                evaluateMove.setEvaluationValue(value);
                countEvaluations++;
                appendEvaluationAnalysis(analyzer, analysisParentNode, step, player, evaluateMove, state, value);
                return evaluateMove;
            }
        } else {
            currentDepth = depth;
            currentStep = step + 1;
            Element stepNode = appendStepAnalysis(analyzer, analysisParentNode, currentDepth, currentStep,
                                                  isMax, player, state, alpha.getEvaluationValue(), beta.getEvaluationValue());
            int numPossible = 0;
            ArrayList<AiMoveInformation> bestMoves = new ArrayList<>();
            AiMoveInformation bestValue = null;
            if (isMax) {
                bestValue = alpha;
            } else {
                bestValue = beta;
            }
            AiMoveInformation[] possibleMoves = evaluator.generateMoves(player, state, rules);
            for (AiMoveInformation nextMove : possibleMoves) {
                if (rules.isValidMove(nextMove, state)) {
                    Element moveNode = appendValidMoveAnalysis(analyzer, stepNode, nextMove, player, state);
                    numPossible++;
                    state.changeState(player, nextMove, engine);
                    setMoveAnalysisChangedState(analyzer, moveNode, state);
                    AiMoveInformation helpAlpha = (isMax) ? bestValue : alpha;
                    AiMoveInformation helpBeta = (isMax) ? beta : bestValue;
                    GamePlayer nextPlayer = getNextPlayer(player, nextMove, state);
                    GamePlayer currentPlayer = player;
                    //FIXME boolean helpIsMax = (nextPlayer == null) ? !isMax : nextPlayer.equals(engine.getCurrentPlayer());
                    boolean helpIsMax = (nextPlayer == null || depth == 1) ? isMax : nextPlayer.equals(engine.getCurrentPlayer());
                    if (nextPlayer == null || depth == 1) {
                    	// if the evaluation is done, do not take the next player FIXME
                    	nextPlayer = player;
                    	currentPlayer = prevPlayer;
                    }                    
                    AiMoveInformation move = getMiniMaxMoveAlphaBetaPruning(depth - 1, step + 1, nextMove,
                                                                            helpAlpha, helpBeta, nextPlayer, currentPlayer,
                                                                            state, evaluator, helpIsMax, analyzer, moveNode);
                    state.undoMove(player, nextMove);
                    if (move != null) {
                        long evaluationValue = move.getEvaluationValue();
                        nextMove.setEvaluationValue(evaluationValue);
                        setMoveAnalysisEvaluationValue(analyzer, moveNode, evaluationValue);
                        long bestEvaluationValue = getEvaluationValue(bestValue);
                        if (isMax) {
                            if (evaluationValue > bestEvaluationValue) {
                                if (evaluationValue > beta.getEvaluationValue()) {
                                    setStepAnalysisEvaluationValue(analyzer, stepNode, evaluationValue);
                                    setMoveAnalysisPruningInformation(analyzer, moveNode, null, beta.getEvaluationValue());
                                    return nextMove;
                                } else {
                                    bestValue = nextMove;
                                    bestMoves.clear();
                                    bestMoves.add(nextMove);
                                }
                            } else if (evaluationValue == bestEvaluationValue) {
                                bestMoves.add(nextMove);
                            }
                        } else {
                            if (evaluationValue < bestEvaluationValue) {
                                if (evaluationValue < alpha.getEvaluationValue()) {
                                    setStepAnalysisEvaluationValue(analyzer, stepNode, evaluationValue);
                                    setMoveAnalysisPruningInformation(analyzer, moveNode, alpha.getEvaluationValue(), null);
                                    return nextMove;
                                } else {
                                    bestValue = nextMove;
                                    bestMoves.clear();
                                    bestMoves.add(nextMove);
                                }
                            } else if (evaluationValue == bestEvaluationValue) {
                                bestMoves.add(nextMove);
                            }
                        }
                    }
                } else {
                    appendInvalidMoveAnalysis(analyzer, stepNode, nextMove);
                }
            }
            if (numPossible == 0) {
                // ignore isMax if depth = 0
            	//FIXME
            	/*FIXME
                return getMiniMaxMoveAlphaBetaPruning(0, step, evaluateMove, alpha, beta, null, player,
                                                      state, evaluator, false, analyzer, stepNode);
                                                      */
                return getMiniMaxMoveAlphaBetaPruning(0, step, evaluateMove, alpha, beta, player, prevPlayer,
                        							  state, evaluator, false, analyzer, stepNode);
            }
            AiMoveInformation bestMove = (bestMoves.isEmpty()) ? ((isMax)
                                                               ? new StartingAlphaMove() : new StartingBetaMove())
                                                               : bestMoves.get(DiceUtil.throwDice(0, bestMoves.size() - 1));
            setStepAnalysisEvaluationValue(analyzer, stepNode, getEvaluationValue(bestMove));
            markStepAnalysisBestMoves(analyzer, stepNode, bestMoves, bestMove);
            return bestMove;
        }
    }

    /**
     * Returns the evaluation value of the specified move.<br>
     * If the specified move is {@code null}, {@code Long.MIN_VALUE} will be returned.
     * 
     * @param move The move.
     * @return The evaluation value of the specified move.
     */
    private static long getEvaluationValue(AiMoveInformation move) {
        return (move == null) ? Long.MIN_VALUE : move.getEvaluationValue();
    }

    /**
     * Creates the root node for the analysis.
     * 
     * @param analyzer The analyzer.
     * @param player The player for which the analysis is made.
     * @return The root node for the analysis.
     */
    private static Element createAnalysisRoot(AiAnalyzer analyzer, GamePlayer player) {
        if (analyzer != null) {
            return analyzer.createRootNode(player);
        }
        return null;
    }

    /**
     * Appends a node for a step to the given parent node.
     * 
     * @param analyzer The analyzer.
     * @param parentNode The parent node.
     * @param depth The current depth.
     * @param step The current step.
     * @param isMax Flag if the step is a maximum step or not.
     * @param player The player of the current step.
     * @param state The current step.
     * @param alpha The lower pruning bound.
     * @param beta The upper pruning bound.
     * @return The created step node.
     */
    private static Element appendStepAnalysis(AiAnalyzer analyzer, Element parentNode, int depth, int step,
                                              boolean isMax, GamePlayer player, GameState state, Long alpha, Long beta) {
        if (analyzer != null) {
            return analyzer.appendStep(parentNode, depth, step, isMax, player, state, alpha, beta);
        }
        return null;
    }

    /**
     * Marks the best moves of a step.
     * 
     * @param analyzer The analyzer.
     * @param stepNode The step node.
     * @param bestMoves The list of the best moves.
     * @param chosenMove The chosen best move.
     */
    private static void markStepAnalysisBestMoves(AiAnalyzer analyzer, Node stepNode, ArrayList<AiMoveInformation> bestMoves,
                                                  AiMoveInformation chosenMove) {
        if (analyzer != null) {
            analyzer.markStepBestMoves(stepNode, bestMoves, chosenMove);
        }
    }

    /**
     * Sets the evaluation value for the specified step node.
     * 
     * @param analyzer The analyzer
     * @param stepNode The step node.
     * @param evaluationValue The evaluation value to set.
     */
    private static void setStepAnalysisEvaluationValue(AiAnalyzer analyzer, Element stepNode, long evaluationValue) {
        if (analyzer != null) {
            analyzer.setStepEvaluationValueAttribute(stepNode, evaluationValue);
        }
    }

    /**
     * Appends a node for an evaluation to the given parent node.
     * 
     * @param analyzer The analyzer.
     * @param parentNode The parent node
     * @param step The current step.
     * @param player The player of the evaluation.
     * @param move The performed move.
     * @param state The evaluated state.
     * @param evaluationValue The value of the evaluation.
     */
    private static void appendEvaluationAnalysis(AiAnalyzer analyzer, Element parentNode, int step,
                                                 GamePlayer player, AiMoveInformation move, GameState state, long evaluationValue) {
        if (analyzer != null) {
            analyzer.appendEvaluation(parentNode, step, player, move, state, evaluationValue);
        }
    }

    /**
     * Appends a node for a valid move to the given parent node.
     * 
     * @param analyzer The analyzer.
     * @param parentNode The parent node.
     * @param move The performed move.
     * @param player The player who performed the move.
     * @param stateBefore The state before the move was performed.
     * @return The created move node.
     */
    private static Element appendValidMoveAnalysis(AiAnalyzer analyzer, Element parentNode,
                                                   AiMoveInformation move, GamePlayer player, GameState stateBefore) {
        if (analyzer != null) {
            return analyzer.appendValidMove(parentNode, move, player, stateBefore);
        }
        return null;
    }

    /**
     * Sets the game state after it was changed by the performed move.
     * 
     * @param analyzer The analyzer.
     * @param moveNode The node of the performed move.
     * @param stateAfter The game state after it was changed by the performed move.
     */
    private static void setMoveAnalysisChangedState(AiAnalyzer analyzer, Element moveNode, GameState stateAfter) {
        if (analyzer != null) {
            analyzer.setMoveChangedStateAttribute(moveNode, stateAfter);
        }
    }

    /**
     * Sets the evaluation value for the performed move.
     * 
     * @param analyzer The analyzer.
     * @param moveNode The node of the performed move.
     * @param evaluationValue The evaluation value of the performed move.
     */
    private static void setMoveAnalysisEvaluationValue(AiAnalyzer analyzer, Element moveNode, long evaluationValue) {
        if (analyzer != null) {
            analyzer.setMoveEvaluationValueAttribute(moveNode, evaluationValue);
        }
    }

    /**
     * Sets the pruning information for the performed move.
     * 
     * @param analyzer The analyzer.
     * @param moveNode The node of the performed move.
     * @param alpha The lower pruning bound.
     * @param beta The upper pruning bound.
     */
    private static void setMoveAnalysisPruningInformation(AiAnalyzer analyzer, Element moveNode, Long alpha, Long beta) {
        if (analyzer != null) {
            analyzer.setMovePruningInformation(moveNode, alpha, beta);
        }
    }

    /**
     * Appends a node for an invalid move to the given parent node.
     * 
     * @param analyzer The analyzer.
     * @param parentNode The parent node.
     * @param move The invalid move.
     */
    private static void appendInvalidMoveAnalysis(AiAnalyzer analyzer, Element parentNode, AiMoveInformation move) {
        if (analyzer != null) {
            analyzer.appendInvalidMove(parentNode, move);
        }
    }

    /**
     * Sets the summary values of the analysis.
     * 
     * @param analyzer The analyzer.
     * @param rootNode The root node where to set the values.
     * @param countEvaluations The number of evaluations.
     * @param duration The duration of the analysis.
     * @param startTimestamp The start time stamp.
     * @param stopTimestamp The stop time stamp.
     * @param bestMove The best possible move.
     */
    private static void setAnalysisSummary(AiAnalyzer analyzer, Element rootNode, int countEvaluations,
                                           long duration, Date startTimestamp, Date stopTimestamp,
                                           AiMoveInformation bestMove) {
        if (analyzer != null) {
            analyzer.setAnalysisSummaryAttributes(rootNode, countEvaluations, duration, startTimestamp, stopTimestamp, bestMove);
        }
    }

    /**
     * Class to initialize the alpha move.
     * 
     * @author hagru
     */
    private static class StartingAlphaMove extends AiMoveInformation {

        public StartingAlphaMove() {
            super();
            setEvaluationValue(Long.MIN_VALUE);
        }

        @Override
        public Object clone() {
            return new StartingAlphaMove();
        }
    }

    /**
     * Class to initialize the beta move.
     * 
     * @author hagru
     */
    private static class StartingBetaMove extends AiMoveInformation {

        public StartingBetaMove() {
            super();
            setEvaluationValue(Long.MAX_VALUE);
        }

        @Override
        public Object clone() {
            return new StartingBetaMove();
        }
    }
}