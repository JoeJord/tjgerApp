package com.tjger.lib;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tjger.game.GamePlayer;
import com.tjger.game.GameState;

import android.annotation.SuppressLint;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Used to analyze the artificial intelligence algorithm.
 * 
 * @author Josef Jordan
 */
public class AiAnalyzer {
  /**
   * The XML document.
   */
  private Document doc;
  /**
   * Additional information about the evaluation.
   */
  private static String additionalEvaluationInformation = "";

  /**
   * Constructs a new instance.
   */
  public AiAnalyzer() {
    this.doc = HGBaseXMLTools.createDocument();
  }

  /**
   * Writes the XML document to the specified file.
   * 
   * @param file The file to be written.
   */
  public void writeXML(File file) {
    if (doc != null) {
      HGBaseXMLTools.writeXML(doc, file.getPath());
    }
  }

  /**
   * Returns the name for the root node.
   * 
   * @return The name for the root node.
   */
  protected String getRootNodeName() {
    return "analysis";
  }

  /**
   * Returns the name for the step node.
   * 
   * @return The name for the step node.
   */
  protected String getStepNodeName() {
    return "step";
  }
  
  /**
   * Returns the name for the evaluation node.
   * 
   * @return The name for the evaluation node.
   */
  protected String getEvaluationNodeName() {
    return "evaluation";
  }

  /**
   * Returns the name for the valid move node.
   * 
   * @return The name for the valid move node.
   */
  protected String getValidMoveNodeName() {
    return "vmove";
  }

  /**
   * Returns the name for the invalid move node.
   * 
   * @return The name for the invalid move node.
   */
  protected String getInvalidMoveNodeName() {
    return "imove";
  }

  /**
   * Returns the name for the depth attribute.
   * 
   * @return The name for the depth attribute.
   */
  protected String getDepthAttributeName() {
    return "depth";
  }

  /**
   * Returns the name for the step attribute.
   * 
   * @return The name for the step attribute.
   */
  protected String getStepAttributeName() {
    return "step";
  }

  /**
   * Returns the name for the maximum attribute.
   * 
   * @return The name for the maximum attribute.
   */
  protected String getMaximumAttributeName() {
    return "max";
  }

  /**
   * Returns the name for the player attribute.
   * 
   * @return The name for the player attribute.
   */
  protected String getPlayerAttributeName() {
    return "player";
  }

  /**
   * Returns the name for the move attribute.
   * 
   * @return The name for the move attribute.
   */
  protected String getMoveAttributeName() {
    return "move";
  }

  /**
   * Returns the name for the state attribute.
   * 
   * @return The name for the state attribute.
   */
  protected String getStateAttributeName() {
    return "state";
  }

  /**
   * Returns the name for the state before attribute.
   * 
   * @return The name for the state before attribute.
   */
  protected String getStateBeforeAttributeName() {
    return "stateBefore";
  }

  /**
   * Returns the name for the state after attribute.
   * 
   * @return The name for the state after attribute.
   */
  protected String getStateAfterAttributeName() {
    return "stateAfter";
  }

  /**
   * Returns the name for the evaluation value attribute.
   * 
   * @return The name for the evaluation value attribute.
   */
  protected String getEvaluationValueAttributeName() {
    return "value";
  }

  /**
   * Returns the name for the count evaluations attribute.
   * 
   * @return The name for the count evaluations attribute.
   */
  protected String getCountEvaluationsAttributeName() {
    return "countEvaluations";
  }

  /**
   * Returns the name for the duration attribute.
   * 
   * @return The name for the duration attribute.
   */
  protected String getDurationAttributeName() {
    return "duration";
  }

  /**
   * Returns the name for the start time stamp attribute.
   * 
   * @return The name for the start time stamp attribute.
   */
  protected String getStartTimestampAttributeName() {
    return "startTimestamp";
  }

  /**
   * Returns the name for the stop time stamp attribute.
   * 
   * @return The name for the stop time stamp attribute.
   */
  protected String getStopTimestampAttributeName() {
    return "stopTimestamp";
  }

  /**
   * Returns the name for the best move attribute.
   * 
   * @return The name for the best move attribute.
   */
  protected String getBestMoveAttributeName() {
    return "bestMove";
  }

  /**
   * Returns the name for the move short information attribute.
   * 
   * @return The name for the move short information attribute.
   */
  protected String getMoveShortInfoAttributeName() {
    return "moveShort";
  }

  /**
   * Returns the name for the move id attribute.
   * 
   * @return The name for the move id attribute.
   */
  public String getMoveIdAttributeName() {
    return "moveId";
  }

  /**
   * Returns the name for the flag attribute if the move is a best move or not.
   * 
   * @return The name for the flag attribute if the move is a best move or not.
   */
  protected String getBestMoveFlagAttributeName() {
    return "isBestMove";
  }

  /**
   * Returns the name for the flag attribute if the move is the chosen move or not.
   * 
   * @return The name for the flag attribute if the move is the chosen move or not.
   */
  protected String getChosenMoveFlagAttributeName() {
    return "isChosenMove";
  }

  /**
   * Returns the name for the additional information attribute.
   * 
   * @return The name for the additional information attribute.
   */
  protected String getAdditionalInformationAttributeName() {
    return "additionalInformation";
  }

  /**
   * Returns the name for the lower pruning bound attribute.
   * 
   * @return The name for the lower pruning bound attribute.
   */
  protected String getAlphaAttributeName() {
    return "alpha";
  }

  /**
   * Returns the name for the upper pruning bound attribute.
   * 
   * @return The name for the upper pruning bound attribute.
   */
  protected String getBetaAttributeName() {
    return "beta";
  }

  /**
   * Returns the name for the flag attribute if the step has no best moves.
   * 
   * @return The name for the flag attribute if the step has no best moves.
   */
  protected String getNoBestMovesAttributeName() {
    return "noBestMoves";
  }

  /**
   * Returns the information which represents the given player.
   * 
   * @param player The player.
   * @return The information which represents the given player.
   */
  protected String getPlayerInformation(GamePlayer player) {
    return player.toString();
  }

  /**
   * Returns the information which represents the given state.
   * 
   * @param state The state.
   * @return The information which represents the given state.
   */
  protected String getStateInformation(GameState state) {
    return state.toString();
  }

  /**
   * Returns the information which represents the given move.
   * 
   * @param move The move.
   * @return The information which represents the given move.
   */
  protected String getMoveInformation(AiMoveInformation move) {
    return (move != null) ? move.toString() : "";
  }

  /**
   * Returns the short information about the given move.
   * 
   * @param move The move.
   * @return The short information about the given move.
   */
  protected String getMoveShortInformation(AiMoveInformation move) {
    return getMoveInformation(move);
  }

  /**
   * Returns an unique identification of the given move.
   * 
   * @param move The move.
   * @return An unique identification of the given move.
   */
  public String getMoveIdInformation(AiMoveInformation move) {
    return String.valueOf(move.hashCode());
  }

  /**
   * Sets the additional information about the evaluation.
   * 
   * @param additionalInformation The additional information about the evaluation.
   */
  public static void setAdditionalEvaluationInformation(String additionalInformation) {
    additionalEvaluationInformation = additionalInformation;
  }

  /**
   * Returns the additional information about the evaluation.<br>
   * The information has to be set before with the
   * {@link AiAnalyzer#setAdditionalEvaluationInformation(String)} method.
   * 
   * @return The additional information about the evaluation.
   */
  protected String getAdditionalEvaluationInformation() {
    return additionalEvaluationInformation;
  }

  /**
   * Returns the mask to format a time stamp.
   * 
   * @return The mask to format a time stamp.
   */
  protected String getTimestampFormat() {
    return "dd.MM.yyyy HH:mm:ss.SSS";
  }

  /**
   * Formats the given time stamp.<br>
   * {@link AiAnalyzer#getTimestampFormat()} is used as format mask.
   * 
   * @param timestamp The time stamp to format.
   * @return The formatted time stamp.
   */
  @SuppressLint("SimpleDateFormat")
protected String formatTimestamp(Date timestamp) {
    return new SimpleDateFormat(getTimestampFormat()).format(timestamp);
  }

  /**
   * Appends the given child to the given parent node. If the parent node is {@code null}, the child is
   * appended to the document.
   * 
   * @param parentNode The parent node.
   * @param childNode The child node.
   */
  private void appendChild(Node parentNode, Node childNode) {
    if (parentNode != null) {
      parentNode.appendChild(childNode);
    } else {
      doc.appendChild(childNode);
    }
  }

  /**
   * Sets the attributes for the root node.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code player}: {@link AiAnalyzer#getPlayerAttributeName()}</li>
   * </ul>
   * For the following values the information is retrieved by the following methods:
   * <ul>
   * <li>{@code player}: {@link AiAnalyzer#getPlayerInformation(GamePlayer)}</li>
   * </ul>
   * 
   * @param rootNode The root node where to set the attributes.
   * @param player The player to set.
   */
  protected void setRootNodeAttributes(Element rootNode, GamePlayer player) {
    rootNode.setAttribute(getPlayerAttributeName(), getPlayerInformation(player));
  }

  /**
   * Creates the root node of the analysis.<br>
   * The name of the node is retrieved by {@link AiAnalyzer#getRootNodeName()}.<br>
   * The attributes are set with {@link AiAnalyzer#setRootNodeAttributes(Element)}.
   * 
   * @param player The player to set.
   * @return The root node of the analysis.
   */
  public Element createRootNode(GamePlayer player) {
    Element rootNode = doc.createElement(getRootNodeName());
    setRootNodeAttributes(rootNode, player);
    appendChild(null, rootNode);
    return rootNode;
  }

  /**
   * Sets the attributes of the given step node.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code depth}: {@link AiAnalyzer#getDepthAttributeName()}</li>
   * <li>{@code step}: {@link AiAnalyzer#getStepAttributeName()}</li>
   * <li>{@code isMax}: {@link AiAnalyzer#getMaximumAttributeName()}</li>
   * <li>{@code player}: {@link AiAnalyzer#getPlayerAttributeName()}</li>
   * <li>{@code state}: {@link AiAnalyzer#getStateAttributeName()}</li>
   * <li>{@code alpha}: {@link AiAnalyzer#getAlphaAttributeName()}</li>
   * <li>{@code beta}: {@link AiAnalyzer#getBetaAttributeName()}</li>
   * </ul>
   * For the following values the information is retrieved by the following methods:
   * <ul>
   * <li>{@code player}: {@link AiAnalyzer#getPlayerInformation(GamePlayer)}</li>
   * <li>{@code state}: {@link AiAnalyzer#getStateInformation(GameState)}</li>
   * </ul>
   * 
   * @param stepNode The step node where to set the attributes.
   * @param depth The depth to set.
   * @param step The step to set.
   * @param isMax The maximum flag to set.
   * @param player The player to set.
   * @param state The state to set.
   * @param alpha The lower pruning bound.
   * @param beta The upper pruning bound.
   */
  protected void setStepNodeAttributes(Element stepNode, int depth, int step, boolean isMax,
      GamePlayer player, GameState state, Long alpha, Long beta) {
    stepNode.setAttribute(getDepthAttributeName(), String.valueOf(depth));
    stepNode.setAttribute(getStepAttributeName(), String.valueOf(step));
    stepNode.setAttribute(getMaximumAttributeName(), String.valueOf(isMax));
    stepNode.setAttribute(getPlayerAttributeName(), getPlayerInformation(player));
    stepNode.setAttribute(getStateAttributeName(), getStateInformation(state));
    stepNode.setAttribute(getAlphaAttributeName(), (alpha == null) ? "" : alpha.toString());
    stepNode.setAttribute(getBetaAttributeName(), (beta == null) ? "" : beta.toString());
  }

  /**
   * Sets the evaluation value attribute of the given step node.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code evaluationValue}: {@link AiAnalyzer#getEvaluationValueAttributeName()}</li>
   * </ul>
   * 
   * @param stepNode The step node where to set the attribute.
   * @param evaluationValue The evaluation value to set.
   */
  public void setStepEvaluationValueAttribute(Element stepNode, long evaluationValue) {
    stepNode.setAttribute(getEvaluationValueAttributeName(), String.valueOf(evaluationValue));
  }
  
  /**
   * Sets the flag attribute if the step has no best moves.
   * 
   * @param stepNode The step node where to set the attribute.
   * @param hasNoBestMoves The flag if the step has no best moves.
   */
  protected void setStepNoBestMovesAttribute(Element stepNode, boolean hasNoBestMoves) {
    stepNode.setAttribute(getNoBestMovesAttributeName(), String.valueOf(hasNoBestMoves));
  }

  /**
   * Marks the best moves of a step.
   * 
   * @param stepNode The step node.
   * @param bestMoves The list of the best moves.
   * @param chosenMove The chosen best move.
   */
  public void markStepBestMoves(Node stepNode, ArrayList<AiMoveInformation> bestMoves, AiMoveInformation chosenMove) {
    NodeList moveNodes = stepNode.getChildNodes();
    String chosenMoveId = getMoveIdInformation(chosenMove);
    if (bestMoves.isEmpty()) {
      setStepNoBestMovesAttribute((Element) stepNode, true);
      return;
    }
    // Create a move id array out of the best moves array.
    ArrayList<String> bestMoveIds = new ArrayList<>();
    for (AiMoveInformation bestMove : bestMoves) {
      bestMoveIds.add(getMoveIdInformation(bestMove));
    }
    // Iterate through the move nodes and mark it if it is a best move or it is the chosen move.
    for (int nodeIndex = 0; nodeIndex < moveNodes.getLength(); nodeIndex++) {
      Element moveNode = (Element) moveNodes.item(nodeIndex);
      String moveId = HGBaseXMLTools.getAttributeValue(moveNode, getMoveIdAttributeName());
      if (moveId.equals(chosenMoveId)) {
        setMoveChosenMoveAttribute(moveNode, true);
      }
      if (bestMoveIds.contains(moveId)) {
        setMoveIsBestMoveAttribute(moveNode, true);
      }
    }
  }

  /**
   * Creates a step node and appends it to the given parent node.<br>
   * The name of the node is retrieved by {@link AiAnalyzer#getStepNodeName()}.<br>
   * The attributes are set with
   * {@link AiAnalyzer#setStepNodeAttributes(Element, int, int, boolean, GamePlayer, GameState, Long, Long)}.
   * 
   * @param parentNode The parent node.
   * @param depth The depth to set.
   * @param step The step to set.
   * @param isMax The maximum flag to set.
   * @param player The player to set.
   * @param state The state to set.
   * @param alpha The lower pruning bound.
   * @param beta The upper pruning bound.
   * @return The created step node.
   */
  public Element appendStep(Element parentNode, int depth, int step, boolean isMax, GamePlayer player,
      GameState state, Long alpha, Long beta) {
    Element stepNode = doc.createElement(getStepNodeName());
    setStepNodeAttributes(stepNode, depth, step, isMax, player, state, alpha, beta);
    appendChild(parentNode, stepNode);
    return stepNode;
  }

  /**
   * Sets the attributes of the given evaluation node.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code step}: {@link AiAnalyzer#getStepAttributeName()}</li>
   * <li>{@code player}: {@link AiAnalyzer#getPlayerAttributeName()}</li>
   * <li>{@code move}: {@link AiAnalyzer#getMoveAttributeName()}</li>
   * <li>{@code moveShortInfo}: {@link AiAnalyzer#getMoveShortInfoAttributeName()}</li>
   * <li>{@code moveId}: {@link AiAnalyzer#getMoveIdAttributeName()}</li>
   * <li>{@code state}: {@link AiAnalyzer#getStateAttributeName()}</li>
   * <li>{@code evaluationValue}: {@link AiAnalyzer#getEvaluationValueAttributeName()}</li>
   * <li>{@code additionalInformation}: {@link AiAnalyzer#getAdditionalInformationAttributeName()}</li>
   * </ul>
   * For the following values the information is retrieved by the following methods:
   * <ul>
   * <li>{@code player}: {@link AiAnalyzer#getPlayerInformation(GamePlayer)}</li>
   * <li>{@code move}: {@link AiAnalyzer#getMoveInformation(AiMoveInformation)}</li>
   * <li>{@code moveShortInfo}: {@link AiAnalyzer#getMoveShortInformation(AiMoveInformation)}</li>
   * <li>{@code moveId}: {@link AiAnalyzer#getMoveIdInformation(AiMoveInformation)}</li>
   * <li>{@code state}: {@link AiAnalyzer#getStateInformation(GameState)}</li>
   * <li>{@code additionalInformation}: {@link AiAnalyzer#getAdditionalEvaluationInformation()}</li>
   * </ul>
   * 
   * @param evaluationNode The evaluation node where to set the attributes.
   * @param step The step to set.
   * @param player The player to set.
   * @param move The move to set.
   * @param state The state to set.
   * @param evaluationValue The evaluation value to set.
   */
  protected void setEvaluationNodeAttributes(Element evaluationNode, int step, GamePlayer player,
      AiMoveInformation move, GameState state, long evaluationValue) {
    evaluationNode.setAttribute(getStepAttributeName(), String.valueOf(step));
    evaluationNode.setAttribute(getPlayerAttributeName(), getPlayerInformation(player));
    evaluationNode.setAttribute(getMoveAttributeName(), getMoveInformation(move));
    evaluationNode.setAttribute(getMoveShortInfoAttributeName(), getMoveShortInformation(move));
    evaluationNode.setAttribute(getMoveIdAttributeName(), getMoveIdInformation(move));
    evaluationNode.setAttribute(getStateAttributeName(), getStateInformation(state));
    evaluationNode.setAttribute(getEvaluationValueAttributeName(), String.valueOf(evaluationValue));
    evaluationNode
        .setAttribute(getAdditionalInformationAttributeName(), getAdditionalEvaluationInformation());
  }

  /**
   * Creates an evaluation node and appends it to the given parent node.<br>
   * The name of the node is retrieved by {@link AiAnalyzer#getEvaluationNodeName()}.<br>
   * The attributes are set with
   * {@link AiAnalyzer#setEvaluationNodeAttributes(Element, int, GamePlayer, AiMoveInformation, GameState, long)}
   * .
   * 
   * @param parentNode The parent node.
   * @param step The step to set.
   * @param player The player to set.
   * @param move The move to set.
   * @param state The state to set.
   * @param evaluationValue The evaluation value to set.
   */
  public void appendEvaluation(Element parentNode, int step, GamePlayer player, AiMoveInformation move,
      GameState state, long evaluationValue) {
    Element evaluationNode = doc.createElement(getEvaluationNodeName());
    setEvaluationNodeAttributes(evaluationNode, step, player, move, state, evaluationValue);
    appendChild(parentNode, evaluationNode);
  }

  /**
   * Sets the game state after it was changed by the performed move.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code stateAfter}: {@link AiAnalyzer#getStateAfterAttributeName()}</li>
   * </ul>
   * For the following values the information is retrieved by the following methods:
   * <ul>
   * <li>{@code stateAfter}: {@link AiAnalyzer#getStateInformation(GameState)}</li>
   * </ul>
   * 
   * @param moveNode The node of the performed move.
   * @param stateAfter The game state after it was changed by the performed move.
   */
  public void setMoveChangedStateAttribute(Element moveNode, GameState stateAfter) {
    moveNode.setAttribute(getStateAfterAttributeName(), getStateInformation(stateAfter));
  }

  /**
   * Sets the evaluation value for the performed move.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code evaluationValue}: {@link AiAnalyzer#getEvaluationValueAttributeName()}</li>
   * </ul>
   * 
   * @param moveNode The node of the performed move.
   * @param evaluationValue The evaluation value of the performed move.
   */
  public void setMoveEvaluationValueAttribute(Element moveNode, long evaluationValue) {
    moveNode.setAttribute(getEvaluationValueAttributeName(), String.valueOf(evaluationValue));
  }

  /**
   * Sets the pruning information for the performed move.<br>
   * The move will be also marked as chosen move (see
   * {@link AiAnalyzer#setMoveChosenMoveAttribute(Element, boolean)}).<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code alpha}: {@link AiAnalyzer#getAlphaAttributeName()}</li>
   * <li>{@code beta}: {@link AiAnalyzer#getBetaAttributeName()}</li>
   * </ul>
   * 
   * @param moveNode The node of the performed move.
   * @param alpha The lower pruning bound.
   * @param beta The upper pruning bound.
   */
  public void setMovePruningInformation(Element moveNode, Long alpha, Long beta) {
    moveNode.setAttribute(getAlphaAttributeName(), (alpha == null) ? "" : alpha.toString());
    moveNode.setAttribute(getBetaAttributeName(), (beta == null) ? "" : beta.toString());
    setMoveChosenMoveAttribute(moveNode, true);
  }

  /**
   * Sets the flag attribute, if the move is a best move or not.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code isBestMove}: {@link AiAnalyzer#getBestMoveFlagAttributeName()}</li>
   * </ul>
   * 
   * @param moveNode The node of the move.
   * @param isBestMove {@code true} if the move is a best move.
   */
  protected void setMoveIsBestMoveAttribute(Element moveNode, boolean isBestMove) {
    moveNode.setAttribute(getBestMoveFlagAttributeName(), String.valueOf(isBestMove));
  }

  /**
   * Sets the flag attribute, if the move is the chosen move or not.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code isChosenMove}: {@link AiAnalyzer#getChosenMoveFlagAttributeName()}</li>
   * </ul>
   * 
   * @param moveNode The node of the move.
   * @param isChosenMove {@code true} if the move is the chosen move.
   */
  protected void setMoveChosenMoveAttribute(Element moveNode, boolean isChosenMove) {
    moveNode.setAttribute(getChosenMoveFlagAttributeName(), String.valueOf(isChosenMove));
  }

  /**
   * Sets the attributes of the given valid move node.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code move}: {@link AiAnalyzer#getMoveAttributeName()}</li>
   * <li>{@code moveShortInfo}: {@link AiAnalyzer#getMoveShortInfoAttributeName()}</li>
   * <li>{@code moveId}: {@link AiAnalyzer#getMoveIdAttributeName()}</li>
   * <li>{@code player}: {@link AiAnalyzer#getPlayerAttributeName()}</li>
   * <li>{@code stateBefore}: {@link AiAnalyzer#getStateBeforeAttributeName()}</li>
   * </ul>
   * For the following values the information is retrieved by the following methods:
   * <ul>
   * <li>{@code move}: {@link AiAnalyzer#getMoveInformation(AiMoveInformation)}</li>
   * <li>{@code moveShortInfo}: {@link AiAnalyzer#getMoveShortInformation(AiMoveInformation)}</li>
   * <li>{@code moveId}: {@link AiAnalyzer#getMoveIdInformation(AiMoveInformation)}</li>
   * <li>{@code player}: {@link AiAnalyzer#getPlayerInformation(GamePlayer)}</li>
   * <li>{@code stateBefore}: {@link AiAnalyzer#getStateInformation(GameState)}</li>
   * </ul>
   * 
   * @param moveNode The valid move node where to set the attributes.
   * @param move The move to set.
   * @param player The player to set.
   * @param stateBefore The state before the move was performed to set.
   */
  protected void setValidMoveNodeAttributes(Element moveNode, AiMoveInformation move, GamePlayer player,
      GameState stateBefore) {
    moveNode.setAttribute(getMoveAttributeName(), getMoveInformation(move));
    moveNode.setAttribute(getMoveShortInfoAttributeName(), getMoveShortInformation(move));
    moveNode.setAttribute(getMoveIdAttributeName(), getMoveIdInformation(move));
    moveNode.setAttribute(getPlayerAttributeName(), getPlayerInformation(player));
    moveNode.setAttribute(getStateBeforeAttributeName(), getStateInformation(stateBefore));
  }

  /**
   * Creates a valid move node and appends it to the given parent node.<br>
   * The name of the node is retrieved by {@link AiAnalyzer#getValidMoveNodeName()}.<br>
   * The attributes are set with
   * {@link AiAnalyzer#setValidMoveNodeAttributes(Element, AiMoveInformation, GameState)}.
   * 
   * @param parentNode The parent node.
   * @param move The performed move.
   * @param player The player who performed the move.
   * @param stateBefore The state before the move was performed.
   * @return
   */
  public Element appendValidMove(Element parentNode, AiMoveInformation move, GamePlayer player,
      GameState stateBefore) {
    Element moveNode = doc.createElement(getValidMoveNodeName());
    setValidMoveNodeAttributes(moveNode, move, player, stateBefore);
    appendChild(parentNode, moveNode);
    return moveNode;
  }

  /**
   * Sets the attributes of the given invalid move node.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code move}: {@link AiAnalyzer#getMoveAttributeName()}</li>
   * <li>{@code moveShortInfo}: {@link AiAnalyzer#getMoveShortInfoAttributeName()}</li>
   * <li>{@code moveId}: {@link AiAnalyzer#getMoveIdAttributeName()}</li>
   * </ul>
   * For the following values the information is retrieved by the following methods:
   * <ul>
   * <li>{@code move}: {@link AiAnalyzer#getMoveInformation(AiMoveInformation)}</li>
   * <li>{@code moveShortInfo}: {@link AiAnalyzer#getMoveShortInformation(AiMoveInformation)}</li>
   * <li>{@code moveId}: {@link AiAnalyzer#getMoveIdInformation(AiMoveInformation)}</li>
   * </ul>
   * 
   * @param moveNode The invalid move node where to set the attributes.
   * @param move The move to set.
   */
  protected void setInvalidMoveNodeAttributes(Element moveNode, AiMoveInformation move) {
    moveNode.setAttribute(getMoveAttributeName(), getMoveInformation(move));
    moveNode.setAttribute(getMoveShortInfoAttributeName(), getMoveShortInformation(move));
    moveNode.setAttribute(getMoveIdAttributeName(), getMoveIdInformation(move));
  }

  /**
   * Creates an invalid move node and appends it to the given parent node.<br>
   * The name of the node is retrieved by {@link AiAnalyzer#getInvalidMoveNodeName()}.<br>
   * The attributes are set with {@link AiAnalyzer#setInvalidMoveNodeAttributes(Element, AiMoveInformation)}.
   * 
   * @param parentNode The parent node.
   * @param move The move to set.
   */
  public void appendInvalidMove(Element parentNode, AiMoveInformation move) {
    Element moveNode = doc.createElement(getInvalidMoveNodeName());
    setInvalidMoveNodeAttributes(moveNode, move);
    appendChild(parentNode, moveNode);
  }

  /**
   * Sets the attributes of the analysis summary to the given root node.<br>
   * The name for the attributes are retrieved by the following methods:
   * <ul>
   * <li>{@code countEvaluations}: {@link AiAnalyzer#getCountEvaluationsAttributeName()}</li>
   * <li>{@code duration}: {@link AiAnalyzer#getDurationAttributeName()}</li>
   * <li>{@code startTimestamp}: {@link AiAnalyzer#getStartTimestampAttributeName()}</li>
   * <li>{@code stopTimestamp}: {@link AiAnalyzer#getStopTimestampAttributeName()}</li>
   * <li>{@code bestMove}: {@link AiAnalyzer#getBestMoveAttributeName()}</li>
   * <li>{@code moveShortInfo}: {@link AiAnalyzer#getMoveShortInfoAttributeName()}</li>
   * <li>{@code moveId}: {@link AiAnalyzer#getMoveIdAttributeName()}</li>
   * </ul>
   * The time stamps will be formatted with {@link AiAnalyzer#formatTimestamp(Date)}.<br>
   * <br>
   * For the following values the information is retrieved by the following methods:
   * <ul>
   * <li>{@code bestMove}: {@link AiAnalyzer#getMoveInformation(AiMoveInformation)}</li>
   * <li>{@code moveShortInfo}: {@link AiAnalyzer#getMoveShortInformation(AiMoveInformation)}</li>
   * <li>{@code moveId}: {@link AiAnalyzer#getMoveIdInformation(AiMoveInformation)}</li>
   * </ul>
   * 
   * @param rootNode The root node of the analysis.
   * @param countEvaluations The count evaluations to set.
   * @param duration The duration to set.
   * @param startTimestamp The start time stamp to set.
   * @param stopTimestamp The stop time stamp to set.
   * @param bestMove The best move to set.
   */
  public void setAnalysisSummaryAttributes(Element rootNode, int countEvaluations, long duration,
      Date startTimestamp, Date stopTimestamp, AiMoveInformation bestMove) {
    rootNode.setAttribute(getCountEvaluationsAttributeName(), String.valueOf(countEvaluations));
    rootNode.setAttribute(getDurationAttributeName(), String.valueOf(duration));
    rootNode.setAttribute(getStartTimestampAttributeName(), formatTimestamp(startTimestamp));
    rootNode.setAttribute(getStopTimestampAttributeName(), formatTimestamp(stopTimestamp));
    rootNode.setAttribute(getBestMoveAttributeName(), getMoveInformation(bestMove));
    rootNode.setAttribute(getMoveShortInfoAttributeName(), getMoveShortInformation(bestMove));
    rootNode.setAttribute(getMoveIdAttributeName(), getMoveIdInformation(bestMove));
  }
}
